# Chapter 6: Load Balancers

## Introduction — Why Load Balancing Matters

Imagine your web application starts getting 10,000 requests per second. A single server — no matter how powerful — has finite CPU, memory, and network capacity. At some point, it will buckle under the load, response times will spike, and users will get errors.

**Load balancing** is the practice of distributing incoming network traffic across multiple backend servers to ensure:

- **No single server is overwhelmed** — traffic is spread evenly
- **High availability** — if one server dies, others continue serving
- **Horizontal scalability** — add more servers to handle more traffic
- **Improved latency** — route users to the fastest available server

A **load balancer** sits between clients and your backend servers, acting as a **traffic cop** that decides which server should handle each incoming request.

```
                         +------------------+
                         |   Load Balancer  |
    Clients              |                  |              Backend Servers
  +--------+             |  Distributes     |             +----------+
  |User 1  +------------>|  traffic across  +------------>| Server 1 |
  +--------+             |  healthy servers |             +----------+
  +--------+             |                  |             +----------+
  |User 2  +------------>|                  +------------>| Server 2 |
  +--------+             |                  |             +----------+
  +--------+             |                  |             +----------+
  |User 3  +------------>|                  +------------>| Server 3 |
  +--------+             +------------------+             +----------+
```

---

## Layer 4 vs Layer 7 Load Balancing

Load balancers operate at different layers of the **OSI model**. The two most important are **Layer 4 (Transport)** and **Layer 7 (Application)**.

### Layer 4 Load Balancing (Transport Layer)

Layer 4 LBs make routing decisions based on **IP addresses and TCP/UDP ports** — they don't inspect the content of the traffic. They operate at the TCP/UDP level.

**How it works:**
1. Client initiates a TCP connection to the LB's virtual IP (VIP)
2. LB selects a backend server based on its algorithm
3. LB either **NATs** the connection or acts as a **TCP proxy**
4. All packets in that TCP connection go to the same backend

```
  Client                    L4 Load Balancer              Backend
  +------+    TCP SYN      +-------------+    TCP SYN    +--------+
  |      +---------------->| Sees:       +-------------->|        |
  |      |    to VIP:80    | Src IP/Port |    to S1:80   |Server 1|
  |      |                 | Dst IP/Port |               |        |
  |      |<----------------+ (no HTTP    |<--------------+        |
  +------+    TCP SYN-ACK  | inspection) |   TCP SYN-ACK +--------+
                           +-------------+
```

### Layer 7 Load Balancing (Application Layer)

Layer 7 LBs inspect the **content of the HTTP request** — headers, URL paths, cookies, query parameters — and make intelligent routing decisions.

**How it works:**
1. Client establishes a TCP connection to the LB
2. LB terminates the TCP connection and reads the HTTP request
3. Based on the request content, LB routes to the appropriate backend
4. LB maintains a **separate** TCP connection to the backend

```
  Client                     L7 Load Balancer                Backend
  +------+   HTTP Request   +----------------+   HTTP Req   +--------+
  |      +----------------->| Inspects:      +------------->|        |
  |      | GET /api/users   | - URL path     | /api/users   |API Svc |
  |      | Host: example.com| - Headers      |              |        |
  |      |                  | - Cookies      |              +--------+
  |      |                  | - HTTP method  |
  |      |   HTTP Request   |                |   HTTP Req   +--------+
  |      +----------------->| Routes based   +------------->|        |
  |      | GET /images/logo | on content     | /images/logo |CDN/    |
  |      |                  |                |              |Static  |
  +------+                  +----------------+              +--------+
```

### Comparison: Layer 4 vs Layer 7

| Feature | Layer 4 (Transport) | Layer 7 (Application) |
|---|---|---|
| **Operates on** | IP address, TCP/UDP port | HTTP headers, URL, cookies, body |
| **Speed** | Very fast (minimal processing) | Slower (must parse HTTP) |
| **SSL Termination** | Limited (pass-through or terminate) | Full SSL termination and re-encryption |
| **Content-based routing** | No | Yes (route /api to API servers, /static to CDN) |
| **WebSocket support** | Yes (treats as TCP) | Yes (with HTTP upgrade awareness) |
| **Connection handling** | Single connection (NAT/forwarding) | Two connections (client-LB, LB-server) |
| **Health checks** | TCP connect, ping | HTTP status code, response body check |
| **Cost/Complexity** | Lower | Higher |
| **Use cases** | Database LB, TCP services, high-throughput | Web apps, microservices, API gateways |
| **AWS equivalent** | NLB (Network Load Balancer) | ALB (Application Load Balancer) |

### When to Use Which?

- **Use Layer 4** when you need raw performance, handle non-HTTP protocols, or don't need content-based routing
- **Use Layer 7** when you need URL-based routing, host-based routing, header inspection, or API gateway features

---

## How Load Balancing Works Internally — Deep Dive

Understanding the internals of load balancing — how connections are established, how packets
flow, and how the LB actually forwards traffic — is essential for explaining load balancing
in any forum or interview with confidence.

### The Life of a Request Through a Load Balancer

```
  Step-by-step: What happens when a user visits https://example.com?
  
  1. DNS RESOLUTION
     Browser: "What is the IP of example.com?"
     DNS: "It's 203.0.113.10" (this is the LB's Virtual IP, NOT a backend server)
  
  2. TCP HANDSHAKE WITH THE LOAD BALANCER
     Client -> LB: SYN (to 203.0.113.10:443)
     LB -> Client: SYN-ACK
     Client -> LB: ACK
     (TCP connection established between client and LB)
  
  3. TLS HANDSHAKE (if HTTPS)
     Client <-> LB: TLS negotiation, certificate exchange, key agreement
     (LB terminates TLS — backend servers may use plain HTTP)
  
  4. HTTP REQUEST
     Client -> LB: GET /api/users HTTP/1.1
                    Host: example.com
                    Cookie: session_id=abc123
  
  5. LOAD BALANCER DECISION
     LB reads the request and decides which backend to use:
     - Checks algorithm (Round Robin, Least Connections, etc.)
     - Checks health status of backends
     - Checks sticky session rules (if configured)
     - Selects: Server 2 (10.0.0.2:8080)
  
  6. FORWARDING TO BACKEND
     LB -> Server 2: GET /api/users HTTP/1.1
                      Host: example.com
                      X-Forwarded-For: 192.168.1.100   (original client IP)
                      X-Forwarded-Proto: https          (original protocol)
                      X-Real-IP: 192.168.1.100
  
  7. BACKEND RESPONSE
     Server 2 -> LB: HTTP/1.1 200 OK
                      Content-Type: application/json
                      {"users": [...]}
  
  8. RESPONSE TO CLIENT
     LB -> Client: HTTP/1.1 200 OK (re-encrypted if TLS)
  
  Total added latency by LB: ~0.5-2ms (L7) or ~0.1-0.5ms (L4)
```

### Connection Modes — How the LB Forwards Traffic

There are four fundamental ways a load balancer can forward traffic to backend servers. Each
has dramatically different performance characteristics.

#### Mode 1: Reverse Proxy (Full Proxy / L7 Default)

The LB maintains **two separate TCP connections** — one with the client and one with the backend.
Every byte flows through the LB.

```
  Client                    Load Balancer                Backend Server
  (1.2.3.4)                (203.0.113.10)               (10.0.0.2)
  
  +--------+   TCP Conn 1  +------------+   TCP Conn 2  +--------+
  |        |==============>|            |==============>|        |
  |        |   Client<->LB |            |   LB<->Server |        |
  |        |               |   BOTH     |               |        |
  |        |<==============| CONNECTIONS|<==============|        |
  |        |   Response    |  THROUGH   |   Response    |        |
  +--------+   via LB      |     LB     |   via LB      +--------+
  
  Request flow:  Client -> LB -> Backend
  Response flow: Backend -> LB -> Client
  
  Pros:
  + LB can inspect, modify, cache, compress HTTP content
  + LB can retry failed requests on a different backend
  + Backend servers never see client IPs (security)
  + Supports connection multiplexing (fewer backend connections)
  
  Cons:
  - LB is a bottleneck (ALL traffic flows through it, both directions)
  - Higher latency (extra hop + TCP overhead)
  - LB needs more CPU and memory
  
  Used by: Nginx, HAProxy (HTTP mode), AWS ALB, all L7 load balancers
```

#### Mode 2: NAT (Network Address Translation / L4 Default)

The LB rewrites packet headers (source/destination IP) but does **not** terminate the TCP
connection. Both request AND response flow through the LB.

```
  Client (1.2.3.4)           LB (203.0.113.10)          Backend (10.0.0.2)
  
  Request:
  [Src: 1.2.3.4             [Src: 203.0.113.10          
   Dst: 203.0.113.10]  -->   Dst: 10.0.0.2]        -->  Received!
   (client sends to LB)      (LB rewrites dst IP)
  
  Response:
                         <-- [Src: 203.0.113.10     <--  [Src: 10.0.0.2
                              Dst: 1.2.3.4]              Dst: 203.0.113.10]
                              (LB rewrites src IP)       (backend replies to LB)
  
  The client thinks it's talking to 203.0.113.10 the whole time.
  The backend thinks all traffic comes from the LB's IP.
  
  Pros:
  + Faster than full proxy (no TCP termination)
  + Simpler than proxy mode
  
  Cons:
  - Both request AND response go through LB (bandwidth bottleneck)
  - Cannot inspect HTTP content (L4 only)
  - Backend sees LB's IP, not client's IP (need X-Forwarded-For at L7)
  
  Used by: AWS NLB, LVS (Linux Virtual Server), F5 BIG-IP (default mode)
```

#### Mode 3: Direct Server Return (DSR)

The LB only handles the **incoming request**. The backend server sends the **response directly
to the client**, bypassing the LB entirely. This is a massive performance optimization.

```
  Client (1.2.3.4)           LB (203.0.113.10)          Backend (10.0.0.2)
  
  Request (small, e.g., 200 bytes):
  [Src: 1.2.3.4                                          
   Dst: 203.0.113.10]  -->  LB forwards to backend  -->  Received!
  
  Response (large, e.g., 5 MB video):
  <---------------------------------------------------------+
  [Src: 203.0.113.10  (backend spoofs the LB's IP)          |
   Dst: 1.2.3.4]      Response goes DIRECTLY to client      |
                       BYPASSING the LB entirely!            |
  
  Why this matters:
  - Typical web traffic: requests are SMALL (100-500 bytes)
  - Responses are LARGE (10KB - 5MB: HTML, images, video)
  - With DSR, the LB only handles the small requests
  - The heavy response traffic goes directly from backend to client
  - LB bandwidth requirement reduced by 90-95%!
  
  Pros:
  + LB only handles inbound traffic (massive bandwidth savings)
  + Much higher throughput than proxy or NAT modes
  + Ideal for streaming, large downloads, video
  
  Cons:
  - Complex configuration (backends must be configured to respond to LB's VIP)
  - Cannot modify response (LB never sees it)
  - Health checking is harder (LB doesn't see response errors)
  - Does NOT work with L7 features (no HTTP inspection)
  
  Used by: Large-scale CDNs, video streaming platforms, high-throughput services
           LVS (IPVS) in DSR mode, F5 BIG-IP in DSR mode
```

#### Mode 4: IP Tunneling (IP-in-IP Encapsulation)

Similar to DSR, but the LB encapsulates the original packet inside a new IP packet and sends
it to the backend. The backend decapsulates and responds directly to the client.

```
  Client (1.2.3.4)           LB (203.0.113.10)          Backend (10.0.0.2)
  
  Request:
  [Src: 1.2.3.4              LB encapsulates:
   Dst: 203.0.113.10]  -->   [Outer: Src: LB, Dst: 10.0.0.2
                               Inner: original packet]    -->  Backend decapsulates
  
  Response (direct to client, like DSR):
  <-----------------------------------------------------------+
  
  Pros:
  + Backends can be in different subnets/networks (unlike DSR)
  + Same bandwidth benefits as DSR
  
  Cons:
  + Higher overhead (encapsulation adds bytes to each packet)
  + MTU issues (encapsulated packets may exceed MTU)
  + Backends must support IP tunneling
```

#### Connection Modes Comparison

```
  +-------------------+----------+----------+----------+----------+
  | Feature           | Reverse  | NAT      | DSR      | Tunnel   |
  |                   | Proxy    |          |          |          |
  +-------------------+----------+----------+----------+----------+
  | Request path      | C->LB->S | C->LB->S| C->LB->S| C->LB->S|
  | Response path     | S->LB->C | S->LB->C| S->C    | S->C     |
  | LB sees response? | Yes      | Yes      | No      | No       |
  | HTTP inspection?  | Yes      | No       | No      | No       |
  | Modify content?   | Yes      | No       | No      | No       |
  | LB bandwidth      | High     | High     | LOW     | LOW      |
  | Latency added     | ~1-2ms   | ~0.5ms   | ~0.2ms  | ~0.3ms   |
  | Retry on failure? | Yes      | No       | No      | No       |
  | Cross-subnet?     | Yes      | Yes      | No      | Yes      |
  | Complexity        | Medium   | Low      | High    | High     |
  +-------------------+----------+----------+----------+----------+
  
  Choose:
  - Reverse Proxy: When you need L7 features (most web applications)
  - NAT: When you need L4 simplicity (databases, TCP services)
  - DSR: When bandwidth/throughput is critical (streaming, CDN)
  - Tunnel: When you need DSR across different networks
```

### Connection Multiplexing — A Hidden Superpower of L7 Load Balancers

One of the most powerful optimizations that L7 load balancers perform is **connection
multiplexing** (also called connection pooling or connection coalescing).

```
  WITHOUT multiplexing:
  
  1000 clients each open a TCP connection to the LB.
  LB opens 1000 TCP connections to the backend.
  Backend must handle 1000 concurrent connections.
  
  Client 1 ----TCP----> LB ----TCP----> Backend  (connection 1)
  Client 2 ----TCP----> LB ----TCP----> Backend  (connection 2)
  Client 3 ----TCP----> LB ----TCP----> Backend  (connection 3)
  ...
  Client 1000 --TCP---> LB ----TCP----> Backend  (connection 1000)
  
  Backend: "I'm managing 1000 TCP connections!" (memory, file descriptors, overhead)
  
  ---
  
  WITH multiplexing (L7 LB):
  
  1000 clients each open a TCP connection to the LB.
  LB opens ONLY 10 persistent connections (keep-alive) to the backend.
  LB multiplexes all 1000 client requests over these 10 connections.
  
  Client 1    ----TCP---+
  Client 2    ----TCP---+
  Client 3    ----TCP---+--> LB --10 TCP conns--> Backend
  ...                   |
  Client 1000 ----TCP---+
  
  Backend: "I'm managing only 10 connections!" (massive resource savings)
  
  This is especially powerful for:
  - HTTP/2 (multiplexes multiple requests over single TCP connection)
  - Databases behind connection-limited backends
  - Microservices with connection pool limits
```

### How a Load Balancer Maintains State — The Connection Table

Even "stateless" L4 load balancers must track connections internally:

```
  Load Balancer Connection Table:
  +-------------------+------------------+------------------+----------+
  | Client            | Backend          | Protocol         | State    |
  +-------------------+------------------+------------------+----------+
  | 1.2.3.4:54321     | 10.0.0.2:8080   | TCP              | ESTAB    |
  | 5.6.7.8:49876     | 10.0.0.3:8080   | TCP              | ESTAB    |
  | 9.10.11.12:61234  | 10.0.0.2:8080   | TCP              | TIME_WAIT|
  | 13.14.15.16:55555 | 10.0.0.4:8080   | TCP              | SYN_RECV |
  +-------------------+------------------+------------------+----------+
  
  This table ensures that:
  - All packets of a TCP connection go to the SAME backend
  - The LB can track connection lifecycle (SYN -> ESTABLISHED -> FIN)
  - Connection timeouts are enforced
  - Connection counts per backend are known (for Least Connections algo)
  
  Table size: A busy LB may track MILLIONS of concurrent connections.
  Memory: ~100-200 bytes per connection entry.
  1 million connections = ~200 MB of memory for the connection table alone.
```

---

## Stateless vs Stateful Requests — How Load Balancers Handle Each

This is one of the most important distinctions in load balancing and system design. The
statefulness of your application **fundamentally changes** how you configure load balancing.

### What is a Stateless Request?

A **stateless request** contains ALL the information the server needs to process it. The server
does not need to remember anything from previous requests. Every request is **self-contained
and independent.**

```
  Stateless Request Examples:
  
  1. REST API with JWT token:
     GET /api/users/42
     Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiam9obiJ9.abc
     
     -> ANY server can verify the JWT and serve the request
     -> No server-side state needed (token carries the identity)
  
  2. Static content:
     GET /images/logo.png
     
     -> ANY server with the file can respond
  
  3. Idempotent API call:
     GET /api/products?category=electronics&page=2
     
     -> Same request gives same result regardless of which server handles it
```

### What is a Stateful Request?

A **stateful request** depends on **server-side session state** from a previous request. If the
request lands on a different server, that server won't have the context and the request fails
or produces wrong results.

```
  Stateful Request Examples:
  
  1. Server-side session (shopping cart stored in server memory):
     POST /cart/add
     Cookie: JSESSIONID=abc123
     {"product_id": 42}
     
     -> ONLY the server that created session abc123 has the cart
     -> If this request goes to a different server, the cart is empty!
  
  2. Multi-step wizard (server tracks current step):
     POST /checkout/step-3
     Cookie: session_id=xyz789
     
     -> Server must know that steps 1 and 2 were already completed
     -> Different server = no knowledge of prior steps
  
  3. WebSocket connection (long-lived stateful connection):
     ws://example.com/chat
     
     -> The connection is tied to a specific server
     -> Messages must route to the same server for the duration
  
  4. File upload in progress (chunked upload):
     PUT /upload/chunk/3
     X-Upload-ID: upload_12345
     
     -> Server is assembling chunks in its local storage
     -> Different server = chunks are on different machines
```

### How a Load Balancer Handles Stateless Requests

Stateless requests are **the ideal case** for load balancing. Any algorithm works, any server
can handle any request, and the system scales linearly.

```
  Stateless Architecture:
  
  +--------+     +------------------+     +---------+
  | Client |---->| Load Balancer    |---->| Server 1|  (any server can handle
  +--------+     |                  |     +---------+   any request)
  +--------+     | Algorithm:       |     +---------+
  | Client |---->| Round Robin /    |---->| Server 2|
  +--------+     | Least Connections|     +---------+
  +--------+     | (anything works!)|     +---------+
  | Client |---->|                  |---->| Server 3|
  +--------+     +------------------+     +---------+
  
  Request 1 from User A -> Server 1 (processes, responds)
  Request 2 from User A -> Server 3 (processes, responds — no problem!)
  Request 3 from User A -> Server 2 (processes, responds — still no problem!)
  
  Key properties:
  + ANY server can handle ANY request
  + Server failure: LB simply removes it; no user impact
  + Scaling: add servers, LB distributes traffic automatically
  + No session affinity needed
  + No data loss when a server crashes
  + Perfect horizontal scalability
  
  How state is carried in stateless architectures:
  +--------------------------------------------------------------+
  | Mechanism         | How It Works                              |
  +--------------------------------------------------------------+
  | JWT Token         | Client sends signed token with every      |
  |                   | request. Server validates token, extracts |
  |                   | user identity. No server-side session.    |
  +--------------------------------------------------------------+
  | API Key/Token     | Client includes API key in header.        |
  |                   | Server authenticates via key lookup.      |
  +--------------------------------------------------------------+
  | Request Body      | All needed data is in the request itself. |
  |                   | e.g., "Update user 42's name to Bob"      |
  +--------------------------------------------------------------+
  | URL Parameters    | State encoded in the URL.                 |
  |                   | e.g., /orders?user_id=42&status=pending   |
  +--------------------------------------------------------------+
```

### How a Load Balancer Handles Stateful Requests

Stateful requests require **special handling** to ensure requests from the same user always
reach the same server. There are three approaches, ranging from worst to best.

#### Approach 1: Sticky Sessions (Session Affinity)

The load balancer routes all requests from the same user to the same backend server.

```
  How Sticky Sessions Work:
  
  First request from User A:
  +--------+                +------------------+               +---------+
  | User A |---Request 1--->| Load Balancer    |---selected--->| Server 2|
  +--------+                |                  |               +---------+
                            | 1. No sticky     |
                            |    cookie found  |
                            | 2. Use algorithm |
                            |    -> picks S2   |
                            | 3. Set cookie:   |
                            |    SERVERID=S2   |
                            +------------------+
  
  Response includes: Set-Cookie: SERVERID=S2
  
  Subsequent requests from User A:
  +--------+                +------------------+               +---------+
  | User A |---Request 2--->| Load Balancer    |---routed----->| Server 2|
  |        | Cookie:        |                  |   to S2       |         |
  |        | SERVERID=S2    | 1. Reads cookie  |               +---------+
  +--------+                | 2. Routes to S2  |
                            +------------------+
  
  Types of Sticky Session identification:
  
  +-------------------------+-----------------------------------------------+
  | Method                  | How It Works                                  |
  +-------------------------+-----------------------------------------------+
  | Cookie-based            | LB sets a cookie (SERVERID, ROUTEID)          |
  | (most common)           | Browser sends it back on every request        |
  +-------------------------+-----------------------------------------------+
  | Source IP               | hash(client_ip) -> always same server         |
  |                         | Problem: users behind NAT/proxy share an IP   |
  +-------------------------+-----------------------------------------------+
  | URL rewriting           | Append session ID to URL: /page?srv=s2        |
  |                         | Problem: ugly URLs, caching issues            |
  +-------------------------+-----------------------------------------------+
  | SSL Session ID          | TLS session identifier used for routing       |
  |                         | Problem: TLS session may rotate frequently    |
  +-------------------------+-----------------------------------------------+
```

**Problems with Sticky Sessions:**

```
  Problem 1: UNEVEN LOAD DISTRIBUTION
  
  Server 1: 5 users (light usage)
  Server 2: 5 users (one is a power user doing 100x more requests)
  Server 3: 5 users (light usage)
  
  -> Server 2 is overwhelmed while 1 and 3 are idle
  -> LB can't redistribute because sessions are "stuck"
  
  Problem 2: SERVER FAILURE = DATA LOSS
  
  Server 2 crashes!
  +--------+                +------------------+               +---------+
  | User A |---Request----->| Load Balancer    |---S2 is dead! | Server 2|
  |        | Cookie:        |                  |   Now what?   | CRASHED |
  |        | SERVERID=S2    | Options:         |               +---------+
  +--------+                | a) Error (bad UX)|
                            | b) Route to S3   |               +---------+
                            |    (session lost!)| ------------->| Server 3|
                            +------------------+    empty      +---------+
                                                    session!
  
  User A's shopping cart, form progress, login state — ALL GONE.
  
  Problem 3: CANNOT AUTO-SCALE
  
  Traffic spike: Need to add Server 4 and Server 5.
  - Existing sticky sessions stay on Servers 1-3.
  - Only NEW users go to Servers 4-5.
  - Old servers remain overloaded; new servers underutilized.
  
  Traffic drop: Want to remove Server 3.
  - Can't! Server 3 has active sessions.
  - Must wait for ALL sessions to expire (could be hours/days).
  - Or drain connections (complex, slow).
```

#### Approach 2: Session Replication

All servers share session data by replicating it across the cluster.

```
  Session Replication:
  
  +--------+                +------------------+
  | User A |---Request----->| Load Balancer    |---any server--->
  +--------+                | (no sticky       |
                            |  session needed!)|
                            +------------------+
  
  +---------+     +---------+     +---------+
  | Server 1|<--->| Server 2|<--->| Server 3|
  | Session:|     | Session:|     | Session:|
  | A: cart |     | A: cart |     | A: cart |
  | B: prefs|     | B: prefs|     | B: prefs|
  +---------+     +---------+     +---------+
       ^               ^               ^
       |               |               |
       +---replication-+--replication---+
  
  Every session is copied to ALL servers.
  Any server can handle any request.
  
  Pros:
  + No sticky sessions needed
  + Server failure: no session loss (other servers have copies)
  
  Cons:
  - Massive memory waste (every session on every server)
  - O(n^2) replication traffic (each update goes to n-1 servers)
  - Doesn't scale: 100 servers with 1 million sessions each = disaster
  - Replication lag: stale session data on some servers
  
  Verdict: Acceptable for small clusters (2-4 servers), terrible at scale.
```

#### Approach 3: External Session Store (The Correct Solution)

Move session state OUT of the application servers and into a **shared, centralized session
store.** This makes the application servers **truly stateless** while preserving session data.

```
  External Session Store Architecture:
  
  +--------+                +------------------+
  | User A |---Request----->| Load Balancer    |
  | Cookie:|                | (no sticky       |
  | sid=X  |                |  session needed!)|
  +--------+                +---+---------+----+
                                |         |
                          +-----v--+ +----v---+
                          |Server 1| |Server 2|    Servers are STATELESS
                          |  (no   | |  (no   |    No session data locally
                          | session| | session|
                          |  data) | |  data) |
                          +---+----+ +----+---+
                              |           |
                              v           v
                         +-------------------+
                         | Session Store     |     Shared, fast, replicated
                         | (Redis / Memcached)|
                         |                   |
                         | session:X ->      |
                         |   {user: "alice", |
                         |    cart: [...],   |
                         |    step: 3}       |
                         +-------------------+
  
  Request flow:
  1. User sends request with session cookie (sid=X)
  2. LB routes to ANY available server (Round Robin, Least Connections, etc.)
  3. Server receives request, reads session cookie
  4. Server queries Redis: GET session:X
  5. Redis returns session data: {user: "alice", cart: [...]}
  6. Server processes request using session data
  7. Server updates session in Redis: SET session:X {updated data}
  8. Server responds to client
  
  Why this is the best approach:
  +----------------------------------------------------------------+
  | Benefit                  | Explanation                          |
  +----------------------------------------------------------------+
  | No sticky sessions       | LB uses any algorithm freely        |
  | No session replication   | One copy in Redis (with replicas)   |
  | Server failure safe      | Sessions survive server crashes     |
  | Auto-scaling friendly    | Add/remove servers without impact   |
  | Fast session access      | Redis: sub-millisecond reads        |
  | TTL-based expiry         | Redis TTL auto-expires old sessions |
  | Cross-service sharing    | Multiple services can share sessions|
  +----------------------------------------------------------------+
  
  Redis configuration for sessions:
  - SET session:abc123 '{...}' EX 3600   (1 hour TTL)
  - Use Redis Cluster for HA (3+ nodes)
  - Memory: ~1KB per session * 1M sessions = ~1 GB
```

#### Approach 4: Stateless Authentication (JWT — No Server-Side Session At All)

The most scalable approach: **eliminate server-side sessions entirely.** Use JSON Web Tokens
(JWT) where the token itself carries all the authentication and authorization information.

```
  JWT-Based Stateless Authentication:
  
  LOGIN:
  +--------+   POST /login          +---------+
  | Client |---{user, password}---->| Server  |
  +--------+                        |         |
       ^                            | 1. Validates credentials
       |                            | 2. Creates JWT:
       |    JWT Token               |    Header: {"alg":"RS256"}
       +----------------------------+    Payload: {
                                    |      "sub": "user123",
                                    |      "name": "Alice",
                                    |      "role": "admin",
                                    |      "exp": 1735689600
                                    |    }
                                    |    Signature: RS256(header.payload, private_key)
                                    +---------+
  
  SUBSEQUENT REQUESTS:
  +--------+                +------------------+               +---------+
  | Client |---GET /api/--->| Load Balancer    |---any server->| Server  |
  | Header:|   users        | (any algorithm)  |               |         |
  | Auth:  |                +------------------+               | 1. Read |
  | Bearer |                                                   |    JWT  |
  | eyJ... |                                                   | 2. Verify|
  +--------+                                                   |    signature
                                                               | 3. Extract
                                                               |    user info
                                                               | 4. Process
                                                               |    request
                                                               | NO REDIS
                                                               | NO SESSION
                                                               | LOOKUP!
                                                               +---------+
  
  Why JWT is the most scalable:
  - Zero server-side state
  - Zero session store queries
  - ANY server can validate the token (just needs the public key)
  - Works across microservices (shared public key)
  - Perfect for mobile apps, SPAs, API consumers
  
  Trade-offs:
  - Cannot revoke individual tokens (use short TTLs + refresh tokens)
  - Token size: ~500 bytes vs ~32 byte session ID (more bandwidth)
  - Sensitive data in token is base64-encoded (not encrypted) — don't put secrets in JWT
```

### Stateless vs Stateful — Complete Comparison for Load Balancing

```
  +---------------------------+----------------------------------+----------------------------------+
  | Aspect                    | Stateful (Server Sessions)       | Stateless (External/JWT)         |
  +---------------------------+----------------------------------+----------------------------------+
  | LB Algorithm              | Must use sticky sessions or      | Any algorithm works (Round Robin,|
  |                           | session-aware routing             | Least Connections, etc.)         |
  +---------------------------+----------------------------------+----------------------------------+
  | Server Failure            | Session data LOST (unless        | No impact — any server can       |
  |                           | replicated or externalized)       | handle the request               |
  +---------------------------+----------------------------------+----------------------------------+
  | Horizontal Scaling        | Difficult (sessions are sticky)  | Trivial (add servers freely)     |
  +---------------------------+----------------------------------+----------------------------------+
  | Auto-Scaling              | Complex (draining required)      | Simple (add/remove at will)      |
  +---------------------------+----------------------------------+----------------------------------+
  | Load Distribution         | Often uneven (sticky imbalance)  | Even (LB chooses freely)         |
  +---------------------------+----------------------------------+----------------------------------+
  | Server Resource Usage     | Higher (session data in memory)  | Lower (no session memory)        |
  +---------------------------+----------------------------------+----------------------------------+
  | Infrastructure Needed     | Just app servers                 | External store (Redis) or JWT    |
  +---------------------------+----------------------------------+----------------------------------+
  | Deployment Complexity     | Lower (simpler initial setup)    | Slightly higher (Redis or JWT)   |
  +---------------------------+----------------------------------+----------------------------------+
  | Latency per Request       | Lower (session data is local)    | Slightly higher (Redis lookup)   |
  |                           |                                  | or same (JWT — no lookup)        |
  +---------------------------+----------------------------------+----------------------------------+
  | Production Recommendation | AVOID for new applications       | STRONGLY PREFERRED               |
  +---------------------------+----------------------------------+----------------------------------+
```

### The Evolution: Stateful to Stateless

```
  The industry's journey:
  
  2000s: Server-Side Sessions (Stateful)
  +----------------------------------------------------------+
  | - Session stored in server memory (HttpSession in Java)   |
  | - Sticky sessions via cookies                             |
  | - Problems: scaling, failover, deployment                 |
  +----------------------------------------------------------+
       |
       v
  2008-2012: Session Replication
  +----------------------------------------------------------+
  | - Tomcat session replication, JBoss clustering            |
  | - All servers replicate all sessions                      |
  | - Problems: memory waste, replication lag, O(n^2) traffic |
  +----------------------------------------------------------+
       |
       v
  2012-2016: External Session Store
  +----------------------------------------------------------+
  | - Sessions moved to Redis / Memcached                     |
  | - Servers become stateless (wrt sessions)                 |
  | - LB can use any algorithm                                |
  | - Standard approach for most web applications today       |
  +----------------------------------------------------------+
       |
       v
  2016-Present: Token-Based Authentication (JWT)
  +----------------------------------------------------------+
  | - No server-side session at all                           |
  | - Token carries identity + permissions                    |
  | - Zero session store dependency                           |
  | - Ideal for microservices, SPAs, mobile apps              |
  | - Combined with refresh tokens for security               |
  +----------------------------------------------------------+
  
  Modern best practice:
  - Use JWT for authentication (stateless)
  - Use Redis for application state that CAN'T be in the token 
    (shopping cart, preferences, temp data)
  - NEVER use server-side sessions for new applications
```

### Real-World Scenario: E-Commerce Checkout

```
  "How would you load-balance a checkout flow where the user adds items
   to cart, enters shipping info, then pays?"
  
  BAD (stateful, sticky sessions):
  +---------+                                +---------+
  | Browser |---Add to Cart (step 1)-------->| Server 2|  <- session stored here
  |         |---Enter Shipping (step 2)----->| Server 2|  <- sticky to Server 2
  |         |---Pay (step 3)---------------->| Server 2|  <- must be Server 2!
  +---------+                                +---------+
  
  If Server 2 crashes between step 2 and 3: cart and shipping data LOST.
  User must start over. Revenue lost.
  
  GOOD (stateless, external store):
  +---------+                  +------+     +---------+
  | Browser |---Add to Cart--->| LB   |---->| Server 1| --writes--> Redis
  |         |---Enter Ship.--->| (any)|---->| Server 3| --reads/writes--> Redis
  |         |---Pay----------->| algo)|---->| Server 5| --reads--> Redis
  +---------+                  +------+     +---------+
  
  Redis: {
    "cart:user42": {
      "items": [{"id": 1, "qty": 2}, {"id": 5, "qty": 1}],
      "shipping": {"addr": "123 Main St", "method": "express"},
      "step": 3
    }
  }
  
  Server 1 crashes? No problem — Server 3 reads cart from Redis.
  Server 3 crashes? No problem — Server 5 reads cart from Redis.
  Need to scale up? Add Server 6, 7, 8 — they all read from Redis.
```

---

## Types of Load Balancers — Complete Classification

Load balancers can be classified along multiple dimensions. Understanding the full taxonomy
helps you pick the right solution for your specific needs.

### Classification by Deployment

```
  +-----------------------------------------------------------------------+
  |                     LOAD BALANCER TAXONOMY                            |
  +-----------------------------------------------------------------------+
  |                                                                       |
  |  By Deployment:                                                       |
  |  +------------------+  +------------------+  +---------------------+  |
  |  | Hardware LB      |  | Software LB      |  | Cloud-Managed LB    |  |
  |  | (Physical        |  | (Runs on          |  | (Fully managed      |  |
  |  |  appliance)      |  |  commodity HW)    |  |  by cloud provider) |  |
  |  |                  |  |                   |  |                     |  |
  |  | F5 BIG-IP        |  | HAProxy           |  | AWS ALB/NLB         |  |
  |  | Citrix ADC       |  | Nginx             |  | GCP Cloud LB        |  |
  |  | A10 Networks     |  | Envoy             |  | Azure LB            |  |
  |  | Radware          |  | Traefik           |  | Cloudflare LB       |  |
  |  +------------------+  +------------------+  +---------------------+  |
  |                                                                       |
  |  By OSI Layer:                                                        |
  |  +------------------+  +------------------+  +---------------------+  |
  |  | Layer 4 (L4)     |  | Layer 7 (L7)     |  | DNS-Based           |  |
  |  | Transport        |  | Application      |  | (Layer "0")         |  |
  |  |                  |  |                   |  |                     |  |
  |  | TCP/UDP routing  |  | HTTP routing     |  | DNS returns          |  |
  |  | IP + Port only   |  | URL, headers,    |  | different IPs       |  |
  |  |                  |  | cookies, body    |  | per query           |  |
  |  | AWS NLB          |  | AWS ALB           |  | Route 53            |  |
  |  | HAProxy TCP mode |  | Nginx, Envoy     |  | Cloudflare DNS      |  |
  |  +------------------+  +------------------+  +---------------------+  |
  |                                                                       |
  |  By Location:                                                         |
  |  +------------------+  +------------------+  +---------------------+  |
  |  | Server-Side LB   |  | Client-Side LB   |  | Sidecar Proxy       |  |
  |  | (centralized)    |  | (in the client)  |  | (per-pod/container) |  |
  |  |                  |  |                   |  |                     |  |
  |  | Traditional LB   |  | Netflix Ribbon   |  | Envoy (Istio mesh)  |  |
  |  | sits between     |  | gRPC client LB   |  | Linkerd proxy       |  |
  |  | client & server  |  | Eureka + client  |  | One proxy per pod   |  |
  |  +------------------+  +------------------+  +---------------------+  |
  +-----------------------------------------------------------------------+
```

### 1. Hardware Load Balancers

Dedicated physical appliances with custom ASICs (Application-Specific Integrated Circuits)
designed specifically for packet processing.

```
  +-----------------------------------------------------+
  | Hardware Load Balancer (e.g., F5 BIG-IP i10800)     |
  |-----------------------------------------------------|
  | Custom ASIC chips for SSL offloading                |
  | Dedicated hardware for packet processing            |
  | Up to 160 Gbps throughput                           |
  | Up to 10 million concurrent connections             |
  | Built-in DDoS protection                            |
  | Price: $50,000 - $500,000+                          |
  +-----------------------------------------------------+
  
  When to use:
  - Legacy enterprise data centers
  - Extremely high throughput requirements (100+ Gbps)
  - Regulatory requirements mandating physical appliances
  - When SSL offloading performance is critical
  
  When NOT to use:
  - Cloud-native architectures (use cloud LBs instead)
  - Cost-sensitive environments
  - Teams that need agility and rapid configuration changes
  - Auto-scaling environments
```

### 2. Software Load Balancers

Software that runs on commodity hardware (VMs, containers, bare metal). Far more flexible
and cost-effective than hardware LBs.

```
  +-------------------+--------------------------------------------+
  | Software LB       | Key Characteristics                        |
  +-------------------+--------------------------------------------+
  | HAProxy           | - Gold standard for high-performance LB    |
  |                   | - L4 and L7 support                        |
  |                   | - Single-threaded event-driven architecture |
  |                   | - Handles 1M+ concurrent connections       |
  |                   | - Used by: GitHub, Reddit, Stack Overflow  |
  |                   | - Config: haproxy.cfg (declarative)        |
  +-------------------+--------------------------------------------+
  | Nginx             | - Web server + reverse proxy + LB          |
  |                   | - Excellent for static content + LB combo  |
  |                   | - Master-worker process model              |
  |                   | - NGINX Plus (commercial) adds HA, API     |
  |                   | - Used by: Netflix, Dropbox, WordPress.com |
  +-------------------+--------------------------------------------+
  | Envoy             | - Modern L7 proxy built for microservices   |
  |                   | - xDS API for dynamic configuration        |
  |                   | - Built-in observability (metrics, tracing)|
  |                   | - Sidecar proxy for service meshes (Istio) |
  |                   | - Used by: Uber, Lyft, Airbnb, Salesforce  |
  +-------------------+--------------------------------------------+
  | Traefik           | - Cloud-native LB with auto-discovery      |
  |                   | - Natively integrates with Docker, K8s     |
  |                   | - Auto-generates SSL certs (Let's Encrypt) |
  |                   | - Dashboard for configuration              |
  +-------------------+--------------------------------------------+
  | Caddy             | - Auto HTTPS by default                    |
  |                   | - Simple configuration (Caddyfile)         |
  |                   | - Good for small-medium deployments        |
  +-------------------+--------------------------------------------+
```

### 3. Cloud-Managed Load Balancers

Fully managed by cloud providers. No servers to manage, automatic scaling, pay-per-use.

```
  AWS:
  +-------------------+------------------+------------------+
  | ALB               | NLB              | GWLB             |
  | (Application)     | (Network)        | (Gateway)        |
  |-------------------|------------------|------------------|
  | Layer 7           | Layer 4          | Layer 3          |
  | HTTP/HTTPS/gRPC   | TCP/UDP/TLS      | IP packets       |
  | Path/host/header  | Port-based       | For appliances   |
  | routing           | routing          | (firewalls, IDS) |
  | ~100K req/sec     | Millions req/sec | Inline traffic   |
  | $0.0225/hr +      | $0.0225/hr +     | inspection       |
  | $0.008/LCU-hr     | $0.006/NLCU-hr   |                  |
  +-------------------+------------------+------------------+
  
  GCP:
  +-------------------+------------------+------------------+
  | HTTP(S) LB        | TCP/UDP LB       | Internal LB      |
  | (Global L7)       | (Regional L4)    | (Internal only)  |
  |-------------------|------------------|------------------|
  | Single anycast IP | Regional IP      | No public IP     |
  | Built-in CDN      | TCP/UDP passthru | VPC-internal     |
  | SSL termination   | Raw performance  | Microservices    |
  | URL map routing   |                  |                  |
  +-------------------+------------------+------------------+
  
  Azure:
  +-------------------+------------------+------------------+
  | App Gateway       | Azure LB         | Front Door       |
  | (L7)              | (L4)             | (Global L7)      |
  |-------------------|------------------|------------------|
  | WAF integration   | 5-tuple hash     | CDN + LB + WAF   |
  | URL routing       | Health probes    | Global anycast   |
  | SSL termination   | Internal/public  | Edge routing     |
  +-------------------+------------------+------------------+
```

### 4. DNS-Based Load Balancing

DNS servers return different IP addresses for the same domain name, distributing traffic
before it even reaches a load balancer.

```
  How DNS LB works:
  
  Query: "What is the IP of api.example.com?"
  
  Response (Round Robin DNS):
  - First query:  api.example.com -> 10.0.0.1 (Server A)
  - Second query: api.example.com -> 10.0.0.2 (Server B)
  - Third query:  api.example.com -> 10.0.0.3 (Server C)
  - Fourth query: api.example.com -> 10.0.0.1 (Server A — cycles)
  
  Geo-DNS:
  - Query from India:  api.example.com -> 10.1.0.1 (Mumbai server)
  - Query from UK:     api.example.com -> 10.2.0.1 (London server)
  - Query from US:     api.example.com -> 10.3.0.1 (Virginia server)
  
  Pros:
  + No dedicated LB infrastructure needed
  + Global distribution with zero added latency
  + Scales to billions of queries (DNS is massively distributed)
  
  Cons:
  - DNS TTL caching: clients may use stale IP for minutes/hours
  - No health checks (DNS doesn't know if a server is dead)
  - Coarse-grained: can't route by URL, header, or content
  - Client-side caching makes load distribution unpredictable
  
  In practice: DNS LB is used as the FIRST layer, with dedicated LBs behind it.
  
  Internet -> DNS (geo-routing) -> L4 LB (high throughput) -> L7 LB (smart routing)
```

### 5. Client-Side Load Balancing

The load balancing logic runs **inside the client** (or calling service), not in a centralized
LB. The client discovers available servers and chooses one directly.

```
  Traditional (Server-Side) LB:        Client-Side LB:
  
  +--------+     +----+    +----+      +--------+    +----+
  | Client |---->| LB |--->| S1 |      | Client |--->| S1 |
  +--------+     |    |--->| S2 |      | (has   |--->| S2 |
                 |    |--->| S3 |      |  built |--->| S3 |
                 +----+    +----+      |  in LB)|    +----+
                                       +--------+
                                            |
                                            v
                                       +----------+
                                       | Service  |
                                       | Registry |
                                       | (Consul, |
                                       | Eureka,  |
                                       | etcd)    |
                                       +----------+
  
  How it works:
  1. Service registers itself in a service registry (e.g., Consul)
  2. Client queries the registry: "Where are the instances of UserService?"
  3. Registry returns: [10.0.0.1:8080, 10.0.0.2:8080, 10.0.0.3:8080]
  4. Client's built-in LB picks one (Round Robin, random, least conns)
  5. Client sends request DIRECTLY to the chosen server
  
  Pros:
  + No central LB bottleneck
  + Lower latency (no extra hop through LB)
  + Client can use smarter algorithms (weighted by response time)
  
  Cons:
  - LB logic in every client (coupled, harder to update)
  - Language-specific (need libraries for Java, Go, Python, etc.)
  - Client must handle health checking and failover
  
  Used by: gRPC (built-in client LB), Netflix Ribbon (now retired),
           Spring Cloud LoadBalancer, Kubernetes DNS
```

### 6. Sidecar Proxy (Service Mesh)

A **sidecar proxy** is deployed alongside each application instance (in the same pod/VM).
It handles all network traffic transparently, including load balancing.

```
  Service Mesh Architecture:
  
  Pod A                            Pod B                        Pod C
  +---------------------------+    +---------------------------+ +-----------+
  | +-------+ +-------------+ |    | +-------+ +-------------+ | |           |
  | | App A | | Envoy Proxy | |<-->| | App B | | Envoy Proxy | | | App C ... |
  | |       | | (sidecar)   | |    | |       | | (sidecar)   | | |           |
  | +-------+ +-------------+ |    | +-------+ +-------------+ | +-----------+
  +---------------------------+    +---------------------------+
        |                                |
        v                                v
  +---------------------------------------------------------+
  | Control Plane (Istio, Linkerd)                          |
  | - Pushes configuration to all sidecar proxies           |
  | - Defines routing rules, retries, circuit breakers      |
  | - Collects metrics from all proxies                     |
  +---------------------------------------------------------+
  
  How it load-balances:
  1. App A wants to call App B
  2. App A sends request to localhost (its sidecar intercepts it)
  3. Sidecar looks up App B instances from the control plane
  4. Sidecar applies LB algorithm (Round Robin, Least Requests, etc.)
  5. Sidecar sends request to the chosen App B sidecar
  6. App B sidecar forwards to its local App B instance
  
  Pros:
  + Application is completely unaware of LB (transparent)
  + Uniform LB, retries, circuit breaking across all services
  + Language-agnostic (sidecar handles it for Java, Go, Python, etc.)
  + Centralized configuration via control plane
  + Rich observability (every request is metered)
  
  Cons:
  - Added latency (every request goes through 2 proxies: source + dest)
  - Increased resource usage (one proxy per pod)
  - Complex to operate (control plane, proxy configuration)
  - Debugging is harder (more moving parts)
  
  Used by: Istio (Envoy), Linkerd, Consul Connect
```

### 7. API Gateway as Load Balancer

An API Gateway sits at the edge of a microservices architecture and acts as a single entry
point. It combines load balancing with API management features.

```
  +--------+         +-------------------+        +--------------+
  | Client |-------->| API Gateway       |------->| User Service |
  +--------+         |                   |        | (3 instances)|
                     | - Load Balancing  |        +--------------+
                     | - Rate Limiting   |------->| Order Service|
                     | - Authentication  |        | (5 instances)|
                     | - Request Routing |        +--------------+
                     | - Response Caching|------->| Payment Svc  |
                     | - SSL Termination |        | (2 instances)|
                     | - API Versioning  |        +--------------+
                     | - Logging/Metrics |
                     +-------------------+
  
  Popular API Gateways:
  +-------------------+--------------------------------------------+
  | Gateway           | Key Features                               |
  +-------------------+--------------------------------------------+
  | Kong              | Open-source, plugin architecture, Lua/Go   |
  | AWS API Gateway   | Fully managed, Lambda integration          |
  | Apigee (Google)   | Enterprise API management, analytics       |
  | Zuul (Netflix)    | Java-based, dynamic routing, filters       |
  | Spring Cloud GW   | Java/Spring ecosystem, reactive            |
  | APISIX            | Apache project, high performance, Lua      |
  +-------------------+--------------------------------------------+
  
  API Gateway vs Load Balancer:
  +-------------------+--------------------+-----------------------+
  | Feature           | Load Balancer      | API Gateway           |
  +-------------------+--------------------+-----------------------+
  | Traffic routing   | IP/Port or URL     | URL + headers + body  |
  | Authentication    | No                 | Yes (JWT, OAuth, API  |
  |                   |                    |  key validation)      |
  | Rate limiting     | Basic              | Advanced (per user,   |
  |                   |                    |  per API, per plan)   |
  | Request transform | No                 | Yes (rewrite headers, |
  |                   |                    |  transform body)      |
  | API versioning    | No                 | Yes (/v1, /v2 routing)|
  | Analytics         | Basic metrics      | Detailed API analytics|
  | Caching           | No (typically)     | Response caching      |
  +-------------------+--------------------+-----------------------+
```

---

## Load Balancing Algorithms

The algorithm determines **which server receives the next request**. The right choice depends on your workload characteristics.

### 1. Round Robin

Requests are distributed to servers in sequential, circular order.

```
Request 1 -> Server A
Request 2 -> Server B
Request 3 -> Server C
Request 4 -> Server A  (cycle repeats)
Request 5 -> Server B
```

**Pros:** Simple, even distribution when servers are identical
**Cons:** Ignores server load and capacity; a slow request on Server A doesn't prevent more requests from being sent to it

### 2. Weighted Round Robin

Like Round Robin, but servers with higher weights receive proportionally more traffic.

```
Server A (weight: 5) -> receives 5 out of every 8 requests
Server B (weight: 2) -> receives 2 out of every 8 requests
Server C (weight: 1) -> receives 1 out of every 8 requests
```

**Use case:** When servers have different hardware specs (e.g., one has 16 CPUs, another has 8).

### 3. Least Connections

Routes traffic to the server with the **fewest active connections**. Ideal when request processing times vary significantly.

```
Server A: 12 active connections
Server B:  3 active connections  <-- next request goes here
Server C:  7 active connections
```

**Pros:** Adapts to server load dynamically
**Cons:** Requires tracking connection counts; new servers get flooded initially

### 4. Weighted Least Connections

Combines least connections with weights. Picks the server with the lowest `(connections / weight)` ratio.

```
Server A: 12 connections, weight 4 -> ratio = 3.0
Server B:  3 connections, weight 2 -> ratio = 1.5  <-- next request
Server C:  7 connections, weight 1 -> ratio = 7.0
```

### 5. IP Hash

Uses a hash of the client's IP address to determine which server receives the request. The same IP always goes to the same server.

```
hash(client_ip) % num_servers = server_index

hash("192.168.1.1")   % 3 = 0 -> Server A
hash("192.168.1.50")  % 3 = 2 -> Server C
hash("10.0.0.1")      % 3 = 1 -> Server B
```

**Pros:** Provides natural session affinity without cookies
**Cons:** Uneven distribution if IP distribution is skewed; all users behind the same NAT go to the same server

### 6. Consistent Hashing

An advanced hashing technique that **minimizes remapping** when servers are added or removed. Essential for distributed caching and stateful services.

```
         Hash Ring (0 to 2^32)
              0
             /|\
            / | \
           /  |  \
          /   |   \
     S-A *    |    * S-B
        /     |     \
       /      |      \
      /       |       \
  S-C *-------+--------* S-D

  Adding Server E only remaps keys between
  S-D and S-E, not the entire ring.
```

**Pros:** Minimal disruption when scaling; widely used in distributed systems
**Cons:** Requires virtual nodes for even distribution; more complex to implement

### 7. Least Response Time

Routes to the server with the **lowest average response time** AND fewest active connections. This is the most adaptive algorithm.

### Algorithm Comparison Table

| Algorithm | Best For | Server Awareness | Session Affinity | Complexity |
|---|---|---|---|---|
| Round Robin | Homogeneous servers, stateless apps | None | No | Low |
| Weighted Round Robin | Heterogeneous servers | Capacity-aware | No | Low |
| Least Connections | Variable request durations | Load-aware | No | Medium |
| IP Hash | Session affinity needed | None | Yes (by IP) | Low |
| Consistent Hashing | Distributed caches, stateful services | None | Yes (by key) | Medium |
| Least Response Time | Latency-sensitive applications | Performance-aware | No | High |

---

## Hardware vs Software Load Balancers

| Aspect | Hardware LB (F5 BIG-IP) | Software LB (HAProxy, Nginx, Envoy) |
|---|---|---|
| **Cost** | $10,000–$100,000+ | Free (open-source) to modest licensing |
| **Performance** | Millions of connections, dedicated ASICs | Hundreds of thousands of connections per instance |
| **Flexibility** | Limited customization | Highly configurable, scriptable |
| **Deployment** | Physical appliance in data center | VM, container, cloud instance |
| **Scaling** | Buy bigger hardware (vertical) | Add more instances (horizontal) |
| **Cloud-native** | No | Yes — fits containers, Kubernetes |
| **Updates** | Firmware updates, vendor dependency | Rapid iteration, community-driven |

### Popular Software Load Balancers

- **HAProxy:** High-performance TCP/HTTP LB; used by GitHub, Stack Overflow, Reddit
- **Nginx:** Web server + reverse proxy + LB; used by Netflix, Dropbox, WordPress.com
- **Envoy:** Modern L7 proxy designed for microservices; sidecar proxy in service meshes (Istio)
- **Traefik:** Cloud-native LB with auto-discovery for Docker/Kubernetes

---

## Health Checks

A load balancer must know which servers are **healthy** and can accept traffic. Unhealthy servers should be removed from the pool.

### Active Health Checks

The LB periodically sends probe requests to each backend server.

```
  Load Balancer                    Backend Servers
  +------------+    HTTP GET       +----------+
  |            +----- /health ---->| Server 1 | -> 200 OK (healthy)
  |            |                   +----------+
  |            |    HTTP GET       +----------+
  |            +----- /health ---->| Server 2 | -> 503 (unhealthy)
  |            |                   +----------+
  |            |    HTTP GET       +----------+
  |            +----- /health ---->| Server 3 | -> 200 OK (healthy)
  +------------+                   +----------+
  
  Server 2 removed from pool after N consecutive failures
```

**Configuration parameters:**
- **Interval:** How often to check (e.g., every 10 seconds)
- **Timeout:** How long to wait for a response (e.g., 5 seconds)
- **Unhealthy threshold:** How many consecutive failures before marking unhealthy (e.g., 3)
- **Healthy threshold:** How many consecutive successes to mark healthy again (e.g., 2)

### Passive Health Checks

The LB monitors **actual traffic** responses. If a server returns too many errors (5xx), it's marked unhealthy.

```
Client -> LB -> Server 2 -> 500 Internal Server Error
Client -> LB -> Server 2 -> 502 Bad Gateway
Client -> LB -> Server 2 -> 503 Service Unavailable
                             (3 consecutive errors)
LB marks Server 2 as unhealthy
```

**Pros:** No extra probe traffic; detects real failures
**Cons:** Requires real traffic to detect issues; some users experience errors before the server is removed

### Best Practice: Use Both

Most production systems use **active + passive** health checks. Active checks provide proactive detection; passive checks catch issues that probes miss.

---

## Session Persistence (Sticky Sessions)

Some applications store user session state on the server (e.g., shopping cart, login state). If a user's requests go to different servers, they'll lose their session.

**Sticky sessions** ensure all requests from the same user go to the same backend server.

### Implementation Methods

| Method | How It Works | Pros | Cons |
|---|---|---|---|
| **Cookie-based** | LB inserts a cookie (e.g., `SERVERID=S1`) | Works across NATs, reliable | Requires cookie support |
| **IP-based** | Hash of client IP | No cookie needed | Users behind NAT share a server |
| **URL/parameter** | Session ID in URL query parameter | Works without cookies | Ugly URLs, security risk |

### Trade-offs of Sticky Sessions

```
+-----------------------------+--------------------------------+
|         Advantages          |         Disadvantages          |
+-----------------------------+--------------------------------+
| Session state preserved     | Uneven load distribution       |
| Simpler server-side code    | Harder to scale horizontally   |
| No shared session store     | Server failure = session loss  |
| Lower latency (no lookups)  | Complicates auto-scaling       |
+-----------------------------+--------------------------------+
```

> **Modern best practice:** Avoid sticky sessions. Instead, externalize session state to a shared store like **Redis** or **Memcached**, making your servers truly stateless.

---

## Global Server Load Balancing (GSLB)

GSLB distributes traffic across **multiple data centers or regions**, typically using DNS-based routing combined with health checks.

```
                    +-------------------+
                    |       GSLB        |
                    | (DNS + Health     |
                    |  Checks)          |
                    +---+-------+---+---+
                        |       |   |
           +------------+   +---+   +------------+
           |                |                     |
   +-------v-------+ +-----v---------+ +---------v-----+
   |  US-East DC   | |  EU-West DC   | |  AP-South DC  |
   |               | |               | |               |
   |  +----+       | |  +----+       | |  +----+       |
   |  | LB |       | |  | LB |       | |  | LB |       |
   |  +-+--+       | |  +-+--+       | |  +-+--+       |
   |    |          | |    |          | |    |          |
   | +--v--+ +---+ | | +--v--+ +---+ | | +--v--+ +---+ |
   | |Srv1 | |S2 | | | |Srv1 | |S2 | | | |Srv1 | |S2 | |
   | +-----+ +---+ | | +-----+ +---+ | | +-----+ +---+ |
   +----------------+ +---------------+ +---------------+
```

**GSLB routing strategies:**
- **Geographic proximity** — Route to the nearest data center
- **Latency-based** — Route to the fastest data center (measured RTT)
- **Failover** — Route to a secondary DC when primary is down
- **Load-based** — Route based on current DC utilization

---

## High Availability of Load Balancers

The load balancer itself is a **single point of failure (SPOF)**. If the LB goes down, all traffic stops. We must make the LB itself highly available.

### Active-Passive (Failover)

```
                     Virtual IP (VIP): 10.0.0.1
                            |
               +------------+------------+
               |                         |
        +------v------+          +-------v-----+
        |   Active    |  heart  |   Passive    |
        |   LB (A)    |<------->|   LB (B)     |
        | Handles all |  beat   | Standby,     |
        | traffic     |         | monitors A   |
        +------+------+         +------+-------+
               |                       |
         (if A fails, B takes over VIP and all traffic)
```

**How it works:**
1. Both LBs share a **Virtual IP (VIP)** using protocols like **VRRP** or **keepalived**
2. Active LB handles all traffic; Passive monitors via heartbeat
3. If Active fails, Passive takes over the VIP within seconds
4. **Downside:** Passive LB is idle — wasted resources

### Active-Active

```
              DNS Round-Robin or Anycast
              +------+------+
              |             |
       +------v------+ +---v---------+
       |   LB (A)    | |   LB (B)    |
       | Handles 50% | | Handles 50% |
       | of traffic  | | of traffic  |
       +------+------+ +------+------+
              |                |
              v                v
         (shared backend server pool)
```

**How it works:**
1. Both LBs are active and handle traffic simultaneously
2. DNS or upstream routing distributes traffic across both LBs
3. If one LB fails, the other absorbs all traffic
4. **Better resource utilization** than Active-Passive

---

## SSL/TLS Termination

**SSL/TLS termination** at the load balancer means the LB handles the encryption/decryption, and backend traffic flows in plaintext (or re-encrypted).

```
  Client                    Load Balancer              Backend Server
  +------+    HTTPS         +------------+    HTTP     +--------+
  |      +--- (encrypted)-->| Decrypts   +-(plaintext)->|        |
  |      |    TLS 1.3       | SSL/TLS    |   port 80   | App    |
  |      |<-- (encrypted)---| Re-encrypts|<-(plaintext)-| Server |
  +------+    HTTPS         +------------+    HTTP     +--------+
```

### Benefits of SSL Termination at the LB

1. **Offloads CPU-intensive encryption** from application servers
2. **Centralized certificate management** — one place to update certs
3. **Enables Layer 7 inspection** — LB can read HTTP headers for routing
4. **Simplified backend** — servers don't need SSL configuration

### When to Use End-to-End Encryption

If your internal network is not trusted (e.g., multi-tenant cloud, compliance requirements like PCI-DSS), use **SSL re-encryption**: the LB decrypts, inspects, re-encrypts, and sends to the backend over TLS.

---

## Multi-Tier Load Balancing Architecture

Large-scale systems often use **multiple layers** of load balancing:

```
                          Internet
                             |
                    +--------v--------+
                    |    DNS (GSLB)   |   Layer: DNS-based geographic routing
                    +--------+--------+
                             |
                    +--------v--------+
                    |   L4 Load       |   Layer: TCP/Network load balancing
                    |   Balancer      |   (e.g., AWS NLB, hardware LB)
                    |   (Network)     |   High throughput, low latency
                    +---+----+----+---+
                        |    |    |
              +---------+    |    +---------+
              |              |              |
       +------v------+ +----v------+ +-----v-----+
       | L7 LB       | | L7 LB    | | L7 LB     |   Layer: Application
       | (Nginx/     | | (Nginx/  | | (Nginx/   |   load balancing
       |  Envoy)     | |  Envoy)  | |  Envoy)   |   Content-based routing
       +--+------+---+ +--+----+--+ +--+-----+--+
          |      |        |    |       |      |
       +--v--+ +-v--+  +-v--+ +-v-+ +-v--+ +-v--+
       |API  | |Web | |API | |Web| |API | |Web |   Application Servers
       |Svc  | |Svc | |Svc | |Svc| |Svc | |Svc |
       +-----+ +----+ +----+ +---+ +----+ +----+
```

### Why Multiple Layers?

| Layer | Purpose | Technology |
|---|---|---|
| **DNS / GSLB** | Route to correct region/data center | Route 53, Cloudflare DNS |
| **L4 LB** | High-throughput TCP distribution | AWS NLB, F5, LVS |
| **L7 LB** | Content-based routing, SSL termination | Nginx, Envoy, HAProxy, ALB |
| **Service Mesh** | Service-to-service load balancing | Envoy (sidecar), Linkerd |

---

## Real-World Cloud Load Balancers

### AWS Load Balancers

| Feature | ALB (Application) | NLB (Network) | CLB (Classic) |
|---|---|---|---|
| **OSI Layer** | Layer 7 | Layer 4 | Layer 4/7 |
| **Protocols** | HTTP, HTTPS, gRPC | TCP, UDP, TLS | TCP, HTTP |
| **Routing** | Path, host, header, query string | Port-based | Basic |
| **Performance** | Good | Ultra-high (millions RPS) | Moderate |
| **WebSockets** | Yes | Yes (TCP) | Limited |
| **Static IP** | No (use Global Accelerator) | Yes (Elastic IP per AZ) | No |
| **Use case** | Web apps, microservices, APIs | Gaming, IoT, real-time | Legacy (deprecated) |

### GCP Load Balancing

- **HTTP(S) Load Balancer:** Global L7 LB with built-in CDN, DDoS protection
- **TCP/UDP Load Balancer:** Regional or global L4 LB
- **Internal Load Balancer:** For internal microservice traffic
- GCP's global LB uses **anycast** — a single IP address routes globally to the nearest backend

---

## Connection Draining and Graceful Shutdown

When you need to remove a server from the pool (for deployment, maintenance, or scaling down),
you can't just yank it out — active requests would fail. **Connection draining** ensures
in-flight requests complete before the server is removed.

### How Connection Draining Works

```
  Timeline of Graceful Server Removal:
  
  T=0s: Admin/auto-scaler decides to remove Server 2
  
  T=0s: LB marks Server 2 as "draining"
        +------------------+
        | Load Balancer    |     +---------+
        |                  |     | Server 1|  <- receives NEW requests
        | Server 2 =      |     +---------+
        | DRAINING         |     +---------+
        | (no new requests |     | Server 2|  <- only finishes EXISTING requests
        |  sent here)      |     | DRAINING|     (5 active connections remaining)
        +------------------+     +---------+
                                 +---------+
                                 | Server 3|  <- receives NEW requests
                                 +---------+
  
  T=5s:  Server 2 finishes 3 of 5 requests. 2 remaining.
  T=10s: Server 2 finishes remaining 2 requests. 0 active.
  
  T=10s: LB removes Server 2 from pool completely.
         Server 2 can now be safely shut down/updated.
  
  T=30s: (Timeout) If drain timeout expires and connections remain,
         LB forcibly closes them. Set drain timeout based on your
         longest expected request duration (typically 30-300 seconds).
```

### Connection Draining Configuration

```
  AWS ALB:
    Deregistration delay: 300 seconds (default)
    -> When a target is deregistered, ALB waits up to 300s for in-flight
       requests to complete before forcibly closing connections.
  
  Nginx:
    # Graceful shutdown of upstream server
    upstream backend {
        server 10.0.0.1:8080;
        server 10.0.0.2:8080 down;    # Marked as down, no new connections
    }
    # Existing connections finish naturally
  
  HAProxy:
    # Set drain mode for a server
    set server backend/server2 state drain
    # Server stops receiving new connections
    # Existing connections complete or timeout
  
  Kubernetes:
    spec:
      terminationGracePeriodSeconds: 60   # Pod gets 60s to finish requests
    # 1. Pod receives SIGTERM
    # 2. Pod is removed from Service endpoints (no new traffic)
    # 3. Pod finishes active requests
    # 4. After 60s, SIGKILL if still running
```

### Why Connection Draining Matters

```
  WITHOUT draining (hard removal):
  
  User A: "POST /checkout/pay" (in progress on Server 2)
  Admin: *removes Server 2*
  User A: CONNECTION RESET! Payment may have been partially processed!
           -> Double charge? Lost order? Angry customer!
  
  WITH draining (graceful removal):
  
  User A: "POST /checkout/pay" (in progress on Server 2)
  Admin: *initiates drain on Server 2*
  New requests: go to Server 1 and Server 3
  User A: Response 200 OK — payment completed successfully!
  Server 2: 0 active connections -> safely removed
```

---

## Auto-Scaling Integration with Load Balancers

In cloud environments, load balancers work hand-in-hand with auto-scaling groups to
dynamically adjust capacity based on demand.

### How Auto-Scaling + LB Works

```
  Normal traffic (3 servers):
  
  +--------+     +--------+     +---------+ +---------+ +---------+
  |        |     | Load   |---->| Server 1| | Server 2| | Server 3|
  | Users  |---->| Balancer|---->|         | |         | |         |
  | (1000  |     |        |---->|         | |         | |         |
  |  req/s)|     +--------+     +---------+ +---------+ +---------+
  
  Traffic spike detected! (5000 req/s)
  
  Auto-Scaling Group:
  1. CloudWatch alarm: CPU > 70% for 2 minutes -> SCALE OUT
  2. ASG launches Server 4 and Server 5
  3. New servers pass health checks (LB probes /health -> 200 OK)
  4. LB automatically adds Server 4 and Server 5 to the pool
  5. Traffic is distributed across 5 servers
  
  +--------+     +--------+     +---------+ +---------+ +---------+
  |        |     | Load   |---->| Server 1| | Server 2| | Server 3|
  | Users  |---->| Balancer|---->|         | |         | |         |
  | (5000  |     |        |---->|         | |         | |         |
  |  req/s)|     +--------+     +---------+ +---------+ +---------+
                     |          +---------+ +---------+
                     +--------->| Server 4| | Server 5|  <- NEW
                     +--------->| (added) | | (added) |
                                +---------+ +---------+
  
  Traffic drops back to normal (1000 req/s):
  
  1. CloudWatch alarm: CPU < 30% for 10 minutes -> SCALE IN
  2. ASG selects Server 4 and Server 5 for removal
  3. LB initiates connection draining on Server 4 and Server 5
  4. After drain timeout: instances terminated
  5. Back to 3 servers
```

### Scaling Policies

```
  +-------------------+--------------------------------------------------+
  | Policy Type       | How It Works                                     |
  +-------------------+--------------------------------------------------+
  | Target Tracking   | "Keep average CPU at 60%"                        |
  |                   | ASG adds/removes instances to maintain target    |
  |                   | Most common and easiest to configure             |
  +-------------------+--------------------------------------------------+
  | Step Scaling      | "If CPU > 70% add 2 instances"                   |
  |                   | "If CPU > 90% add 5 instances"                   |
  |                   | Different actions at different thresholds        |
  +-------------------+--------------------------------------------------+
  | Scheduled Scaling | "Add 10 instances every Monday at 9 AM"          |
  |                   | For predictable traffic patterns                 |
  +-------------------+--------------------------------------------------+
  | Predictive        | ML-based: analyzes historical patterns and       |
  |                   | pre-scales BEFORE the traffic arrives            |
  +-------------------+--------------------------------------------------+
  
  Key metrics to scale on:
  - CPU utilization (most common)
  - Request count per target (ALB metric)
  - Memory utilization
  - Custom metrics (queue depth, active users, etc.)
  
  Important settings:
  - Cooldown period: 300s (don't scale again for 5 min after last scaling)
  - Warm-up time: 120s (new instance needs time to start serving)
  - Min/Max instances: min=2 (always HA), max=20 (cost control)
```

### The Warm-Up Problem

```
  When a new server is added to the LB pool, it may not be ready for full traffic:
  
  1. JVM warm-up: JIT compilation hasn't optimized hot paths yet
  2. Cache cold start: local caches are empty (every request = cache miss)
  3. Connection pool: database connections not yet established
  
  Solution: SLOW START (Gradual Ramp-Up)
  
  +------------------------------------------------------------------+
  | Time after registration | Traffic share                          |
  +------------------------------------------------------------------+
  | 0-30 seconds            | 10% of normal share                   |
  | 30-60 seconds           | 25% of normal share                   |
  | 60-120 seconds          | 50% of normal share                   |
  | 120+ seconds            | 100% of normal share (fully warmed up)|
  +------------------------------------------------------------------+
  
  AWS ALB: "Slow start duration" setting (30-900 seconds)
  HAProxy: "slowstart" parameter on server line
  Nginx: Not natively supported (use weight adjustment)
  
  WITHOUT slow start:
    New server added -> immediately gets 33% of traffic -> overwhelmed!
    -> High latency, errors, potential crash
    -> Other servers still overloaded because new server is failing
  
  WITH slow start:
    New server added -> gets 10% -> gradually increases
    -> JVM warms up, caches fill, connections establish
    -> Smoothly absorbs its share of traffic
```

---

## Load Balancing Anti-Patterns and Troubleshooting

### Common Anti-Patterns

```
  Anti-Pattern 1: THE THUNDERING HERD
  
  All backends go down simultaneously. LB health checks detect failure.
  When backends come back up, ALL queued requests hit them at once.
  
  +--------+     +----+     X Server 1 (down)
  | 10,000 |---->| LB |     X Server 2 (down)
  | queued |     | ?? |     X Server 3 (down)
  | req    |     +----+
  +--------+
  
  Servers come back:
  +--------+     +----+     +---------+
  | 10,000 |====>| LB |====>| Server 1| <- 3,333 requests simultaneously!
  | req    |     |    |====>| Server 2| <- 3,333 requests simultaneously!
  | FLOOD! |     |    |====>| Server 3| <- 3,333 requests simultaneously!
  +--------+     +----+     +---------+
                             ALL CRASH AGAIN!
  
  Solution:
  - Slow start for recently recovered servers
  - Rate limiting at the LB
  - Circuit breaker pattern (fail fast, don't queue)
  - Exponential backoff with jitter on the client side
  
  ---
  
  Anti-Pattern 2: SINGLE LB WITHOUT HA
  
  +--------+     +----+     +---------+
  | Users  |---->| LB |---->| Servers |
  +--------+     +----+     +---------+
                   ^
                   |
              Single Point
              of Failure!
  
  LB crashes -> ENTIRE system is down, even though all servers are healthy.
  
  Solution: Always deploy LBs in HA pairs (Active-Passive or Active-Active).
  
  ---
  
  Anti-Pattern 3: HEALTH CHECK THAT DOESN'T CHECK REAL HEALTH
  
  BAD health check:
    GET /health -> always returns 200 OK
    (Even when the database is down, the app can't serve real requests,
     but the health check says "everything is fine!")
  
  GOOD health check:
    GET /health -> checks:
    - Database connection: OK
    - Redis connection: OK
    - Disk space: OK (>10% free)
    - Memory: OK (<90% used)
    - Returns 200 only if ALL checks pass
    - Returns 503 with details if any check fails
  
  ---
  
  Anti-Pattern 4: OVER-RELIANCE ON STICKY SESSIONS
  
  Using sticky sessions as a crutch instead of fixing the architecture.
  
  Symptoms:
  - Can't auto-scale effectively
  - Deployments are risky (must drain sessions)
  - Uneven load distribution
  - Server failure = user impact
  
  Fix: Externalize state to Redis. Use JWT for auth. Make servers stateless.
  
  ---
  
  Anti-Pattern 5: IGNORING THE LB'S LIMITS
  
  Common limits people forget:
  +--------------------------------------------+-------------------+
  | Limit                                      | Typical Default   |
  +--------------------------------------------+-------------------+
  | Max concurrent connections                 | 1M (haproxy)      |
  | Max new connections per second             | 50K-100K          |
  | Max request size (body)                    | 1MB (ALB), 10MB   |
  | Max header size                            | 8KB-16KB          |
  | Idle connection timeout                    | 60s (ALB)         |
  | Max backends per LB                        | 1000 (ALB)        |
  | SSL handshakes per second                  | 10K-50K           |
  +--------------------------------------------+-------------------+
  
  Hitting these limits: requests are dropped silently (HTTP 503 or TCP RST).
  Always monitor LB metrics: active connections, new connections/sec,
  spillover count, target response time, error rate.
```

### Troubleshooting Guide

```
  +-----------------------------+-------------------------------------------+
  | Symptom                     | Likely Cause & Fix                        |
  +-----------------------------+-------------------------------------------+
  | 502 Bad Gateway             | Backend server crashed or timed out.      |
  |                             | Check backend health, increase timeout.   |
  +-----------------------------+-------------------------------------------+
  | 503 Service Unavailable     | No healthy backends available.            |
  |                             | Check health check config, backend health.|
  +-----------------------------+-------------------------------------------+
  | 504 Gateway Timeout         | Backend too slow to respond within LB     |
  |                             | timeout. Optimize query, increase timeout.|
  +-----------------------------+-------------------------------------------+
  | Uneven load distribution    | Sticky sessions, bad algorithm, or        |
  |                             | one backend is slower (attracts fewer     |
  |                             | connections with Least Connections).      |
  +-----------------------------+-------------------------------------------+
  | Intermittent connection     | LB connection limit reached, or backend   |
  | resets                      | connection pool exhausted. Scale LB or    |
  |                             | backends, enable connection multiplexing. |
  +-----------------------------+-------------------------------------------+
  | Slow response times after   | New server has cold caches/JVM. Enable    |
  | scaling out                 | slow start mode on the LB.               |
  +-----------------------------+-------------------------------------------+
  | Client IP not available     | X-Forwarded-For header not configured.    |
  | on backend                  | Enable proxy protocol or XFF on LB.      |
  +-----------------------------+-------------------------------------------+
  | WebSocket connections       | LB idle timeout too short. Increase idle  |
  | dropping                    | timeout or enable WebSocket-aware mode.   |
  +-----------------------------+-------------------------------------------+
```

---

## Common Interview Questions

1. **"How would you design a load balancer?"** — Discuss L4 vs L7, connection modes (proxy, NAT, DSR), health checks, algorithms, HA (Active-Active), connection table, and how to handle millions of concurrent connections.

2. **"How do you handle session state with load balancing?"** — Explain the four approaches: sticky sessions (bad), session replication (doesn't scale), external session store in Redis (good), JWT stateless tokens (best). Always recommend making servers stateless.

3. **"What happens if the load balancer itself fails?"** — Active-passive with VRRP/keepalived, active-active with DNS or anycast, VIP failover within seconds, cloud-managed LBs handle this automatically.

4. **"How would you load balance across multiple regions?"** — GSLB with GeoDNS for geographic routing, latency-based routing, failover policies, multi-tier architecture: DNS -> L4 -> L7.

5. **"Explain the difference between stateless and stateful load balancing."** — Stateless requests can go to any server (ideal); stateful requests need sticky sessions or external state. Describe the evolution from server sessions to Redis to JWT.

6. **"What is Direct Server Return and when would you use it?"** — DSR bypasses the LB for responses, reducing LB bandwidth by 90%+. Used for streaming, large downloads, CDNs. Trade-off: cannot inspect or modify responses.

7. **"How does a load balancer work with auto-scaling?"** — Auto-scaler adds instances, they pass health checks, LB adds them to pool with slow start. For scale-in: LB drains connections, then instances are terminated.

8. **"What load balancing algorithm would you choose for a microservices architecture?"** — Least Connections or Least Response Time for heterogeneous service instances. Consistent Hashing if services maintain local caches. Client-side LB or service mesh for service-to-service communication.

---

## Key Takeaways

```
+---------------------------------------------------------------------+
|                Load Balancers — Key Takeaways                        |
+---------------------------------------------------------------------+
|                                                                      |
|  1. Layer 4 LBs are faster but dumber; Layer 7 LBs are smarter      |
|     but add more latency. Choose based on whether you need           |
|     content-based routing.                                           |
|                                                                      |
|  2. Connection modes matter: Reverse Proxy for L7 features,          |
|     NAT for L4 simplicity, DSR for high-throughput scenarios         |
|     (streaming, CDN). DSR reduces LB bandwidth by 90%+.             |
|                                                                      |
|  3. Stateless servers are the goal. Externalize session state        |
|     to Redis or use JWT tokens. This unlocks any LB algorithm,      |
|     effortless auto-scaling, and zero-downtime deployments.          |
|                                                                      |
|  4. Least Connections is the best default algorithm for web          |
|     apps. Use Consistent Hashing for caching layers. Use            |
|     Weighted algorithms when servers have different capacities.      |
|                                                                      |
|  5. Health checks (active + passive) are essential. A good           |
|     /health endpoint checks downstream dependencies (DB, Redis)     |
|     not just "is the process alive."                                 |
|                                                                      |
|  6. The LB itself must be HA. Use Active-Passive (VRRP) or          |
|     Active-Active (DNS/anycast). In the cloud, use managed LBs.     |
|                                                                      |
|  7. Connection draining prevents request failures during deploys.    |
|     Slow start prevents overwhelming newly added servers.            |
|                                                                      |
|  8. Large-scale systems use multi-tier LB: DNS (GSLB) -> L4 LB     |
|     -> L7 LB -> service mesh (sidecar). Each layer serves a         |
|     different purpose.                                               |
|                                                                      |
|  9. In microservices, consider client-side LB (gRPC) or service     |
|     mesh (Istio/Envoy sidecar) for service-to-service traffic.      |
|     Centralized LBs are still needed for external traffic.           |
|                                                                      |
| 10. Monitor your LB: active connections, new connections/sec,        |
|     error rates (5xx), target response times, spillover counts.      |
|     A misconfigured or overloaded LB is the fastest way to          |
|     take down your entire system.                                    |
|                                                                      |
+---------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 7 — Caching](./07-caching.md)*
