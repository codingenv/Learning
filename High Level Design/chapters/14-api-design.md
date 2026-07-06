# Chapter 14: API Design

## Introduction

APIs (Application Programming Interfaces) are the **contracts** between system components. In
a high-level design context, API design determines how clients communicate with your backend,
how microservices talk to each other, and how third-party developers integrate with your platform.

A well-designed API is intuitive, consistent, performant, and evolvable. A poorly designed API
creates confusion, tight coupling, performance bottlenecks, and breaking changes that ripple
across dependent systems.

This chapter covers the four major API paradigms (REST, GraphQL, gRPC, WebSockets), the
API Gateway pattern, and cross-cutting concerns like idempotency, rate limiting, and versioning.

---

## 14.1 Why API Design Matters in HLD

In a system design interview or architecture review, the API is often the **first thing you define**.
It establishes:

- **What operations the system supports** (the "what")
- **How clients interact with the system** (the "how")
- **The data contract** between frontend and backend
- **Service boundaries** in a microservices architecture
- **Performance characteristics** (payload size, number of round trips, streaming capability)

```
                  +---------------------+
                  |   Mobile App        |
                  +---------------------+
                           |
                       REST API
                           |
                  +---------------------+
                  |   API Gateway       | <-- Auth, Rate Limiting, Routing
                  +---------------------+
                    /       |         \
                gRPC      gRPC      gRPC
                 /          |          \
         +--------+  +---------+  +---------+
         | User   |  | Order   |  | Payment |
         | Service|  | Service |  | Service |
         +--------+  +---------+  +---------+
```

---

## 14.2 REST API Design

REST (Representational State Transfer) is the most widely used API paradigm for client-facing APIs.
It uses HTTP methods to operate on **resources** identified by URLs.

### 14.2.1 Resource Naming Conventions

| Principle                        | Good Example                    | Bad Example                      |
|----------------------------------|---------------------------------|----------------------------------|
| Use nouns, not verbs             | `/users/123`                    | `/getUser?id=123`                |
| Use plural nouns                 | `/users`, `/orders`             | `/user`, `/order`                |
| Use kebab-case for multi-word    | `/order-items`                  | `/orderItems`, `/order_items`    |
| Nest for relationships           | `/users/123/orders`             | `/getUserOrders?userId=123`      |
| Max 2-3 levels of nesting        | `/users/123/orders`             | `/users/123/orders/456/items/789`|
| Use query params for filtering   | `/users?role=admin`             | `/admin-users`                   |

### 14.2.2 HTTP Methods

```
  Resource: /users/{id}

  GET    /users          List all users         (safe, idempotent)
  GET    /users/123      Get user 123           (safe, idempotent)
  POST   /users          Create a new user      (NOT idempotent)
  PUT    /users/123      Replace user 123       (idempotent)
  PATCH  /users/123      Partial update user 123 (idempotent*)
  DELETE /users/123      Delete user 123        (idempotent)

  * PATCH is idempotent if you apply the same patch repeatedly
    (e.g., {name: "Alice"} is idempotent; {age: age+1} is NOT)
```

**PUT vs. PATCH:**
```json
// PUT: Full replacement (must send ALL fields)
PUT /users/123
{
  "name": "Alice",
  "email": "alice@mail.com",
  "age": 30,
  "role": "admin"
}

// PATCH: Partial update (send only changed fields)
PATCH /users/123
{
  "email": "newalice@mail.com"
}
```

### 14.2.3 HTTP Status Codes

```
  2xx Success:
  +---------+-----------------------+----------------------------------------+
  | Code    | Name                  | When to Use                            |
  +---------+-----------------------+----------------------------------------+
  | 200     | OK                    | Successful GET, PUT, PATCH, DELETE     |
  | 201     | Created               | Successful POST (resource created)     |
  | 202     | Accepted              | Request accepted, processing async     |
  | 204     | No Content            | Successful DELETE (no response body)   |
  +---------+-----------------------+----------------------------------------+

  3xx Redirection:
  +---------+-----------------------+----------------------------------------+
  | 301     | Moved Permanently     | Resource URL has permanently changed   |
  | 304     | Not Modified          | Cached version is still valid (ETag)   |
  +---------+-----------------------+----------------------------------------+

  4xx Client Errors:
  +---------+-----------------------+----------------------------------------+
  | 400     | Bad Request           | Malformed request, validation error    |
  | 401     | Unauthorized          | Missing or invalid authentication      |
  | 403     | Forbidden             | Authenticated but lacks permission     |
  | 404     | Not Found             | Resource doesn't exist                 |
  | 409     | Conflict              | Resource state conflict (duplicate)    |
  | 422     | Unprocessable Entity  | Valid syntax but semantic errors        |
  | 429     | Too Many Requests     | Rate limit exceeded                    |
  +---------+-----------------------+----------------------------------------+

  5xx Server Errors:
  +---------+-----------------------+----------------------------------------+
  | 500     | Internal Server Error | Unexpected server failure              |
  | 502     | Bad Gateway           | Upstream service returned error        |
  | 503     | Service Unavailable   | Server overloaded or in maintenance    |
  | 504     | Gateway Timeout       | Upstream service timed out             |
  +---------+-----------------------+----------------------------------------+
```

### 14.2.4 HATEOAS (Hypermedia as the Engine of Application State)

HATEOAS means the API response includes **links** to related actions, so clients can
discover available operations dynamically.

```json
GET /users/123

{
  "id": 123,
  "name": "Alice",
  "email": "alice@mail.com",
  "_links": {
    "self":    { "href": "/users/123" },
    "orders":  { "href": "/users/123/orders" },
    "edit":    { "href": "/users/123", "method": "PATCH" },
    "delete":  { "href": "/users/123", "method": "DELETE" },
    "friends": { "href": "/users/123/friends" }
  }
}
```

**In practice:** HATEOAS is rarely fully implemented. Most REST APIs are "RESTish" — they use
HTTP methods and resource URLs but don't include hypermedia links.

### 14.2.5 Pagination

Three main approaches for paginating large collections:

#### Offset-Based Pagination

```
GET /users?offset=20&limit=10

Returns users 21-30

Response:
{
  "data": [...],
  "pagination": {
    "offset": 20,
    "limit": 10,
    "total": 1000
  }
}
```

**Problem:** Slow for large offsets (`OFFSET 1000000` scans 1M rows). Also, inserting or
deleting items shifts offsets, causing missed or duplicate items during pagination.

#### Cursor-Based Pagination

```
GET /users?cursor=eyJpZCI6MjB9&limit=10

"cursor" encodes the last seen position (e.g., base64 of {"id": 20})

Response:
{
  "data": [...],
  "pagination": {
    "next_cursor": "eyJpZCI6MzB9",
    "has_more": true
  }
}
```

**Advantage:** Constant-time regardless of page depth. No skipped/duplicate items.
**Used by:** Twitter API, Slack API, Stripe API.

#### Keyset Pagination

```
GET /users?last_id=20&limit=10

SQL: SELECT * FROM users WHERE id > 20 ORDER BY id LIMIT 10

Similar to cursor-based but the cursor is a transparent value (the actual sort key).
```

| Approach     | Performance at Depth | Consistency | Jumpable | Complexity |
|-------------|---------------------|-------------|----------|------------|
| Offset       | Degrades            | Weak        | Yes      | Simple     |
| Cursor       | Constant            | Strong      | No       | Medium     |
| Keyset       | Constant            | Strong      | No       | Medium     |

### 14.2.6 Filtering, Sorting, and Field Selection

```
# Filtering
GET /users?role=admin&status=active&created_after=2024-01-01

# Sorting
GET /users?sort=name:asc,created_at:desc

# Field selection (sparse fieldsets)
GET /users?fields=id,name,email          (reduce payload size)

# Combining all
GET /users?role=admin&sort=name:asc&fields=id,name&limit=20
```

### 14.2.7 API Versioning

| Strategy           | Example                                | Pros                    | Cons                          |
|-------------------|----------------------------------------|-------------------------|-------------------------------|
| URI path          | `/v1/users`, `/v2/users`               | Explicit, easy to cache | URL pollution, hard to evolve |
| Query parameter   | `/users?version=2`                     | Simple to add           | Easy to forget, caching issues|
| Header            | `Accept: application/vnd.api.v2+json`  | Clean URLs              | Hidden, harder to test        |
| Content negotiation| `Accept: application/vnd.api+json;v=2`| Standards-compliant     | Complex for clients           |

**Recommendation:** URI-based versioning (`/v1/`, `/v2/`) is the most common and pragmatic
choice. Use it unless you have a strong reason not to.

---

## 14.3 GraphQL

GraphQL is a **query language for APIs** that lets clients request exactly the data they need —
no more, no less.

### Schema Definition

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
  friends: [User!]!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
  comments: [Comment!]!
  createdAt: DateTime!
}

type Query {
  user(id: ID!): User
  users(limit: Int, offset: Int): [User!]!
  post(id: ID!): Post
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: UpdateUserInput!): User!
  deleteUser(id: ID!): Boolean!
}

type Subscription {
  postCreated(userId: ID!): Post!
  commentAdded(postId: ID!): Comment!
}
```

### Query Example

```graphql
# Client requests exactly what it needs
query {
  user(id: "123") {
    name
    email
    posts(limit: 5) {
      title
      comments {
        text
        author {
          name
        }
      }
    }
  }
}
```

```json
// Response: exactly the shape requested, nothing more
{
  "data": {
    "user": {
      "name": "Alice",
      "email": "alice@mail.com",
      "posts": [
        {
          "title": "GraphQL is great",
          "comments": [
            {
              "text": "Agreed!",
              "author": { "name": "Bob" }
            }
          ]
        }
      ]
    }
  }
}
```

### When GraphQL > REST

| Scenario                              | Why GraphQL Wins                                    |
|---------------------------------------|-----------------------------------------------------|
| Multiple client types (web, mobile)   | Each client fetches exactly what it needs           |
| Deeply nested/related data            | One query replaces multiple REST round-trips         |
| Rapidly evolving frontend             | Frontend changes don't require backend API changes   |
| Over-fetching is a problem            | Client specifies exact fields                        |

### When REST > GraphQL

| Scenario                              | Why REST Wins                                        |
|---------------------------------------|------------------------------------------------------|
| Simple CRUD operations                | REST is simpler and well-understood                  |
| Caching is critical                   | HTTP caching works naturally with REST (GET URLs)    |
| File uploads                          | REST handles multipart uploads natively              |
| Public APIs for third parties         | REST is more widely understood by external developers|
| Simple, predictable access patterns   | No need for query flexibility                        |

### The N+1 Query Problem

```
GraphQL query:
  query { users { name posts { title } } }

Naive resolver execution:
  1. Fetch all users (1 query)
  2. For EACH user, fetch their posts (N queries)
  Total: N+1 database queries for N users!

With DataLoader (batching):
  1. Fetch all users (1 query)
  2. Collect all user IDs
  3. Batch-fetch all posts for those IDs in ONE query
  Total: 2 database queries regardless of N users!
```

**DataLoader pattern:**
```javascript
const postLoader = new DataLoader(async (userIds) => {
  // Single batch query instead of N individual queries
  const posts = await db.query(
    'SELECT * FROM posts WHERE user_id IN ($1)', [userIds]
  );
  // Group posts by user_id and return in same order as input
  return userIds.map(id => posts.filter(p => p.userId === id));
});

// In resolver:
const resolvers = {
  User: {
    posts: (user) => postLoader.load(user.id)  // automatically batched!
  }
};
```

---

## 14.4 gRPC

gRPC is a high-performance RPC (Remote Procedure Call) framework that uses **Protocol Buffers**
(protobuf) for serialization and **HTTP/2** for transport.

### Protocol Buffers

```protobuf
// user.proto
syntax = "proto3";

package userservice;

service UserService {
  rpc GetUser(GetUserRequest) returns (User);                          // Unary
  rpc ListUsers(ListUsersRequest) returns (stream User);               // Server streaming
  rpc UploadUsers(stream User) returns (UploadResponse);               // Client streaming
  rpc Chat(stream ChatMessage) returns (stream ChatMessage);           // Bidirectional
}

message User {
  int64 id = 1;
  string name = 2;
  string email = 3;
  repeated string roles = 4;
}

message GetUserRequest {
  int64 id = 1;
}

message ListUsersRequest {
  int32 limit = 1;
  string cursor = 2;
}

message UploadResponse {
  int32 count = 1;
  bool success = 2;
}
```

### Four Communication Patterns

```
1. UNARY (request-response):
   Client ---[GetUserRequest]---> Server
   Client <---[User]------------- Server

2. SERVER STREAMING:
   Client ---[ListUsersRequest]---------> Server
   Client <---[User]-------------------- Server
   Client <---[User]-------------------- Server
   Client <---[User]-------------------- Server
   Client <---[END]-------------------- Server

3. CLIENT STREAMING:
   Client ---[User]-------------------> Server
   Client ---[User]-------------------> Server
   Client ---[User]-------------------> Server
   Client ---[END]--------------------> Server
   Client <---[UploadResponse]---------- Server

4. BIDIRECTIONAL STREAMING:
   Client ---[ChatMessage]---> Server
   Client <---[ChatMessage]--- Server
   Client ---[ChatMessage]---> Server
   Client ---[ChatMessage]---> Server
   Client <---[ChatMessage]--- Server
   (messages flow in both directions simultaneously)
```

### When to Use gRPC

| Use Case                              | Why gRPC                                             |
|---------------------------------------|------------------------------------------------------|
| Internal microservice communication   | 5-10x faster than JSON/REST due to binary encoding   |
| Low-latency, high-throughput services | HTTP/2 multiplexing, header compression              |
| Streaming data (live feeds, logs)     | Native streaming support                             |
| Polyglot environments                 | Code generation for 10+ languages from one .proto    |
| Strongly typed contracts              | Schema is the source of truth; breaking changes caught|

**Why NOT gRPC for external APIs:**
- Not browser-friendly (requires gRPC-Web proxy)
- Not human-readable (binary protocol)
- Harder to debug with curl
- Less tooling/ecosystem than REST

---

## 14.5 WebSockets

WebSockets provide **full-duplex communication** over a single, persistent TCP connection. Unlike
HTTP (request-response), either side can send messages at any time.

### Connection Lifecycle

```
  Client                                          Server
    |                                                |
    |--- HTTP GET /chat (Upgrade: websocket) ------->|
    |<-- HTTP 101 Switching Protocols ---------------|
    |                                                |
    |=============== WebSocket Connection ============|
    |                                                |
    |--- "Hello, World!" --------------------------->|
    |<-- "Hi there!" -------------------------------|
    |<-- "New notification: ..." -------------------|
    |--- "Thanks!" --------------------------------->|
    |                                                |
    |--- Close frame ------------------------------->|
    |<-- Close frame --------------------------------|
    |                                                |
```

### Use Cases

| Use Case            | Why WebSockets                                           |
|---------------------|----------------------------------------------------------|
| Real-time chat      | Messages need to be pushed instantly to all participants  |
| Live dashboards     | Server pushes data updates without client polling        |
| Collaborative editing| Multiple users see each other's changes in real-time    |
| Gaming              | Low-latency bidirectional communication                  |
| Live sports scores  | Server pushes score updates as they happen               |
| Stock tickers       | Real-time price updates                                  |

### Connection Management at Scale

Managing millions of WebSocket connections is a significant engineering challenge:

```
  Challenge: 1 million concurrent WebSocket connections

  +------------------+     +------------------+
  |  Load Balancer   |     |  Load Balancer   |
  | (Layer 4 / TCP)  |     | (sticky sessions)|
  +------------------+     +------------------+
        |         |               |         |
  +----------+ +----------+ +----------+ +----------+
  | WS Server| | WS Server| | WS Server| | WS Server|
  | 250K     | | 250K     | | 250K     | | 250K     |
  | conns    | | conns    | | conns    | | conns    |
  +----------+ +----------+ +----------+ +----------+
        \           |           |           /
         +----------+-----------+----------+
                    |
            +---------------+
            |  Message Bus  |  (Redis Pub/Sub, Kafka, NATS)
            | (fan-out to   |
            |  all servers) |
            +---------------+

  When User A on Server 1 sends a message to User B on Server 3:
  1. Server 1 publishes to Message Bus: {to: B, msg: "Hello"}
  2. All servers receive the message from the bus
  3. Server 3 has User B's connection, delivers the message
```

**Key considerations:**
- **Sticky sessions:** WebSocket connections are stateful; the load balancer must route
  reconnections to the same server (or use a message bus for fan-out).
- **Heartbeats:** Send periodic pings to detect dead connections.
- **Reconnection logic:** Clients must handle disconnections gracefully with exponential backoff.
- **Memory:** Each connection uses memory (~10-50KB). 1M connections = 10-50GB RAM.

---

## 14.6 Comparison: REST vs. GraphQL vs. gRPC vs. WebSocket

| Feature            | REST               | GraphQL            | gRPC                | WebSocket           |
|--------------------|--------------------|--------------------|---------------------|---------------------|
| Protocol           | HTTP/1.1 or 2      | HTTP/1.1 or 2      | HTTP/2              | TCP (upgraded HTTP) |
| Data format        | JSON (usually)     | JSON               | Protobuf (binary)   | Any (text/binary)   |
| Communication      | Request-response   | Request-response   | Req-resp + streaming| Full-duplex         |
| Typing             | Weak (OpenAPI opt.)| Strong (schema)    | Strong (protobuf)   | None (app-defined)  |
| Caching            | HTTP caching       | Difficult          | Not built-in        | Not applicable      |
| Browser support    | Native             | Native             | Via gRPC-Web proxy  | Native              |
| Best for           | Public APIs, CRUD  | Flexible queries   | Internal services   | Real-time, push     |
| Latency            | Medium             | Medium             | Low                 | Lowest (persistent) |
| Overhead per call  | Higher (headers)   | Medium             | Low (binary)        | Lowest (no headers) |
| Learning curve     | Low                | Medium             | Medium-High         | Low-Medium          |

### Decision Matrix

```
  Need real-time push updates?
    |
    +-- Yes --> WebSocket (or SSE for one-way)
    |
    +-- No
          |
          Is this internal service-to-service?
            |
            +-- Yes --> gRPC (performance + type safety)
            |
            +-- No (client-facing)
                  |
                  Do clients need flexible queries?
                    |
                    +-- Yes --> GraphQL
                    |
                    +-- No  --> REST (simplest, most widely understood)
```

---

## 14.7 API Gateway Pattern

An API Gateway sits between clients and backend services, handling cross-cutting concerns.

```
                     Clients
                (Mobile, Web, IoT)
                       |
                       v
            +---------------------+
            |    API GATEWAY      |
            |---------------------|
            | - Authentication    |
            | - Rate Limiting     |
            | - Request Routing   |
            | - Load Balancing    |
            | - SSL Termination   |
            | - Request/Response  |
            |   Transformation    |
            | - Caching           |
            | - Logging/Metrics   |
            | - Circuit Breaking  |
            +---------------------+
              /       |         \
             v        v          v
        +--------+ +--------+ +--------+
        |  User  | | Order  | |Payment |
        |  Svc   | |  Svc   | |  Svc   |
        +--------+ +--------+ +--------+
```

### Key Gateway Functions

| Function                | What It Does                                          |
|-------------------------|-------------------------------------------------------|
| **Authentication**      | Verify JWT tokens, API keys, OAuth tokens             |
| **Rate Limiting**       | Enforce request quotas per client/endpoint             |
| **Request Routing**     | Route `/users/*` to User Service, `/orders/*` to Orders|
| **Protocol Translation**| Accept REST from clients, forward as gRPC to services |
| **Response Aggregation**| Combine responses from multiple services into one      |
| **Caching**             | Cache frequent responses at the gateway layer          |
| **Circuit Breaking**    | Stop forwarding to unhealthy services                  |

**Popular API Gateways:** Kong, AWS API Gateway, Nginx, Envoy, Traefik, Zuul.

### Backend for Frontend (BFF) Pattern

Different clients may need different API shapes:

```
  Mobile App  ------>  Mobile BFF  -----+
                       (slim payloads)   |
                                         v
  Web App  -------->   Web BFF  -------> Backend Services
                       (rich payloads)   ^
                                         |
  IoT Device ------->  IoT BFF  --------+
                       (minimal data)
```

Each BFF is a specialized gateway that tailors the API for its client type.

---

## 14.8 Idempotency in API Design

An operation is **idempotent** if performing it multiple times produces the same result as
performing it once. This is critical for reliability — if a network timeout occurs, the client
can safely retry without causing duplicate side effects.

### Idempotency by HTTP Method

| Method | Idempotent? | Example                                              |
|--------|-------------|------------------------------------------------------|
| GET    | Yes         | Reading a resource never changes state               |
| PUT    | Yes         | Replacing with same data yields same result          |
| DELETE | Yes         | Deleting an already-deleted resource is a no-op      |
| PATCH  | Usually     | `{name: "Alice"}` is idempotent; `{views: views+1}` isn't|
| POST   | **No**      | Each POST may create a new resource                  |

### Idempotency Keys for POST Requests

```
Client generates a unique idempotency key for each logical operation:

POST /payments
Headers:
  Idempotency-Key: a1b2c3d4-e5f6-7890-abcd-ef1234567890

Body:
  { "amount": 100, "to": "merchant_123" }

Server behavior:
  1. Check: Have I seen this idempotency key before?
     - Yes: Return the stored response (don't process again)
     - No:  Process the payment, store the response keyed by idempotency key

  First call:   Process payment, return 201 Created, store result
  Retry call:   Return stored 201 Created (payment NOT processed again)
```

**Implementation:**
```
+------------------+        +-------------------+
|  Client          |        |    Server          |
|  Idempotency-Key | -----> | 1. Check key in   |
|  = "abc123"      |        |    Redis/DB        |
+------------------+        | 2. If found:       |
                            |    return cached   |
                            |    response        |
                            | 3. If not found:   |
                            |    process request |
                            |    store response  |
                            |    with key "abc123"|
                            +-------------------+
```

**Used by:** Stripe (all POST endpoints), PayPal, Square, AWS.

---

## 14.9 API Rate Limiting and Throttling

Rate limiting protects your API from abuse, prevents resource exhaustion, and ensures fair usage.

### Common Algorithms

| Algorithm            | How It Works                                    | Pros / Cons                       |
|---------------------|-------------------------------------------------|-----------------------------------|
| **Token Bucket**     | Tokens added at fixed rate; each request costs  | Allows short bursts; smooth       |
|                     | one token; rejected if bucket empty              | average rate                      |
| **Leaky Bucket**     | Requests enter a queue; processed at fixed rate | Strict rate enforcement; can      |
|                     | Excess requests are dropped                      | queue requests if desired         |
| **Fixed Window**     | Count requests in fixed time windows (e.g.,     | Simple; boundary problem (2x      |
|                     | 100 req/minute); reset at window boundary        | burst at window edges)            |
| **Sliding Window**   | Weighted average of current and previous window | Smooths the boundary problem;     |
|                     | counts                                           | slightly more complex             |

### Rate Limit Response Headers

```
HTTP/1.1 200 OK
X-RateLimit-Limit: 100          # Max requests per window
X-RateLimit-Remaining: 42       # Requests left in current window
X-RateLimit-Reset: 1640000000   # Unix timestamp when window resets

---

HTTP/1.1 429 Too Many Requests
Retry-After: 30                 # Seconds until client should retry
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1640000000

{
  "error": "Rate limit exceeded",
  "message": "You have exceeded the rate limit of 100 requests per minute.",
  "retry_after": 30
}
```

### Rate Limiting Tiers

```
  Free Tier:      100 requests/hour
  Basic Tier:     1,000 requests/hour
  Pro Tier:       10,000 requests/hour
  Enterprise:     100,000 requests/hour + dedicated support

  Rate limits can be applied per:
  - API key / client ID
  - IP address
  - User account
  - Endpoint (stricter limits on expensive operations)
```

---

## 14.10 Real-World Best Practices

### Error Response Format

Use a consistent error response format across all endpoints:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The request body contains invalid fields.",
    "details": [
      {
        "field": "email",
        "message": "Must be a valid email address",
        "value": "not-an-email"
      },
      {
        "field": "age",
        "message": "Must be a positive integer",
        "value": -5
      }
    ],
    "request_id": "req_abc123",
    "documentation_url": "https://api.example.com/docs/errors#VALIDATION_ERROR"
  }
}
```

### API Design Checklist

```
  [ ] Resources use plural nouns (/users, /orders)
  [ ] HTTP methods match semantics (GET=read, POST=create, etc.)
  [ ] Consistent error response format with error codes
  [ ] Pagination for all list endpoints
  [ ] Versioning strategy defined (URI, header, or query param)
  [ ] Authentication documented (API key, OAuth, JWT)
  [ ] Rate limits defined and communicated via headers
  [ ] Idempotency keys for non-idempotent operations (POST)
  [ ] Request/response examples in documentation
  [ ] CORS headers configured for browser clients
  [ ] Compression enabled (gzip, brotli)
  [ ] Request size limits enforced
  [ ] Sensitive data excluded from URLs (use headers/body)
  [ ] Timestamps in ISO 8601 format (2024-01-15T10:30:00Z)
  [ ] All IDs are strings (not integers) for forward compatibility
```

### Security Best Practices

| Practice                     | Implementation                                      |
|------------------------------|-----------------------------------------------------|
| Use HTTPS everywhere         | TLS 1.2+ for all API traffic                        |
| Authenticate every request   | JWT, OAuth 2.0, or API keys                         |
| Authorize at the resource    | Check permissions per resource, not just per endpoint|
| Validate all input           | Reject unexpected fields, enforce types and ranges   |
| Don't expose internal IDs    | Use UUIDs or opaque IDs, not auto-increment integers |
| Log all API access           | Include request ID, client ID, timestamp, status     |
| Mask sensitive data in logs  | Never log passwords, tokens, or full credit card #s  |

---

## 14.11 Key Takeaways

1. **REST** is the default choice for client-facing APIs. It's simple, well-understood, and
   leverages HTTP semantics (methods, status codes, caching). Use it unless you have a
   specific reason not to.

2. **GraphQL** shines when clients need flexibility — different screens need different data
   shapes, or you're building for multiple client types (web, mobile, IoT). Watch out for
   the N+1 problem and use DataLoader.

3. **gRPC** is ideal for **internal microservice communication** where performance matters.
   Protobuf is 5-10x more compact than JSON, and HTTP/2 enables multiplexing and streaming.

4. **WebSockets** are the right choice for **real-time, bidirectional communication** — chat,
   live updates, collaborative editing. But they're harder to scale (stateful connections,
   message fan-out).

5. **API Gateways** centralize cross-cutting concerns (auth, rate limiting, routing) so
   individual services don't have to. The BFF (Backend for Frontend) pattern tailors the
   API per client type.

6. **Idempotency** is essential for reliability. GET, PUT, and DELETE are naturally idempotent.
   For POST, use **idempotency keys** so clients can safely retry failed requests.

7. **Rate limiting** protects your system from abuse and ensures fair usage. Use token bucket
   or sliding window algorithms. Always communicate limits via response headers.

8. **Versioning** is inevitable. URI-based versioning (`/v1/`, `/v2/`) is the most pragmatic
   approach. Plan for backward compatibility from day one.

9. **Pagination** is mandatory for all list endpoints. Prefer **cursor-based pagination** for
   large datasets — it's consistent and performs well at any depth.

10. **Consistency matters more than cleverness.** A predictable, well-documented API that follows
    conventions is far more valuable than a "creative" API that surprises developers.

---

*Previous chapter: [Chapter 13 — Consistency Models and Consensus](./13-consistency.md)*
