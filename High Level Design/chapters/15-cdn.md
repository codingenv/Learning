# Chapter 15: Content Delivery Networks (CDN)

## Introduction

Imagine a user in Tokyo trying to load images from a server hosted in Virginia, USA. The data
must travel roughly 11,000 km across undersea cables, multiple network hops, and back. That
round trip can easily add 200-300ms of latency — per request. Multiply that by dozens of
assets on a page, and you have a sluggish, frustrating user experience.

**Content Delivery Networks (CDNs)** solve this by bringing content closer to users. Instead
of every request traveling to a single origin server, a CDN distributes copies of your content
across a global network of servers, so users fetch data from a nearby location.

> **Key Insight:** A CDN is essentially a geographically distributed caching layer that sits
> between your users and your origin server.

---

## 15.1 What Is a CDN?

A CDN is a network of servers distributed across multiple geographic locations (called
**Points of Presence** or **PoPs**) that cache and serve content to users based on their
geographic proximity.

### Core Goals of a CDN

| Goal                    | Description                                                        |
|-------------------------|--------------------------------------------------------------------|
| **Reduce latency**      | Serve content from the nearest edge server                         |
| **Reduce origin load**  | Offload traffic from the origin server by caching at the edge      |
| **Improve availability**| If one PoP fails, traffic is rerouted to the next nearest PoP      |
| **Handle traffic spikes**| CDN's massive infrastructure absorbs sudden surges                |
| **Improve security**    | DDoS protection, WAF, bot mitigation at the edge                   |

### CDN Architecture — ASCII Diagram

```
                          +---------------------+
                          |   Origin Server     |
                          |  (Your App Server)  |
                          +----------+----------+
                                     |
                          +----------+----------+
                          |   CDN Origin Shield  |
                          |   (Optional Layer)   |
                          +----------+----------+
                                     |
              +----------------------+----------------------+
              |                      |                      |
     +--------+--------+   +--------+--------+   +---------+-------+
     |  PoP: New York  |   |  PoP: London    |   |  PoP: Tokyo     |
     |  Edge Server(s) |   |  Edge Server(s) |   |  Edge Server(s) |
     +--------+--------+   +--------+--------+   +---------+-------+
              |                      |                      |
       +------+------+       +------+------+        +------+------+
       | Users in    |       | Users in    |        | Users in    |
       | Americas    |       | Europe      |        | Asia-Pacific|
       +-------------+       +-------------+        +-------------+
```

### How a CDN Request Works (Step by Step)

1. **User requests a resource** (e.g., `https://cdn.example.com/images/logo.png`)
2. **DNS resolution** routes the request to the nearest CDN PoP (using Anycast or geo-DNS)
3. **Edge server checks its cache:**
   - **Cache HIT** -> Return the cached content immediately (fast!)
   - **Cache MISS** -> Edge server fetches from the origin (or origin shield)
4. **Edge server caches the response** for future requests based on TTL
5. **Response delivered to user** from the edge server

```
  User Request Flow (Cache HIT):
  
  User --> DNS Lookup --> Nearest PoP --> [Cache HIT] --> Response (< 50ms)

  User Request Flow (Cache MISS):
  
  User --> DNS Lookup --> Nearest PoP --> [Cache MISS] --> Origin Server
                                     <-- Cache + Respond <-- Origin Response
```

---

## 15.2 Points of Presence (PoPs) and Edge Servers

### What is a PoP?

A **Point of Presence (PoP)** is a physical data center location within the CDN network. Each
PoP contains multiple **edge servers** — the machines that actually cache and serve content.

Major CDN providers operate hundreds of PoPs:

| Provider         | Approximate PoPs | Countries |
|------------------|-------------------|-----------|
| Cloudflare       | 310+              | 120+      |
| Akamai           | 4,100+            | 130+      |
| AWS CloudFront   | 450+              | 90+       |
| Fastly           | 80+               | 50+       |

### Origin Shield

An **origin shield** is an intermediate caching layer between edge servers and the origin:

```
  Without Origin Shield:             With Origin Shield:
  
  Edge A ---\                        Edge A ---\
  Edge B ----+---> Origin            Edge B ----+---> Shield ---> Origin
  Edge C ---/                        Edge C ---/
  
  (3 cache misses = 3 origin hits)   (3 cache misses = 1 origin hit)
```

The shield consolidates requests, dramatically reducing load on the origin server.

---

## 15.3 Push CDN vs Pull CDN

There are two fundamental models for how content gets onto the CDN.

### Pull CDN (Lazy Loading)

The CDN **pulls** content from the origin on demand when a user requests it.

**How it works:**
1. First user requests a resource -> cache MISS
2. CDN fetches from origin, caches it, serves it
3. Subsequent users get cache HITs until TTL expires

**Best for:**
- High-traffic sites with frequently changing content
- When you don't know in advance what content will be popular
- Most web applications and APIs

### Push CDN (Eager Loading)

You **push** content to the CDN proactively before any user requests it.

**How it works:**
1. You upload/publish content directly to CDN storage
2. CDN distributes it across PoPs
3. Users always get cache HITs (no cold-start penalty)

**Best for:**
- Static assets that rarely change (videos, large files)
- Content you know will be needed (software updates, media releases)
- Scenarios where cold-start latency is unacceptable

### Comparison Table

| Aspect                | Pull CDN                          | Push CDN                          |
|-----------------------|-----------------------------------|-----------------------------------|
| **Content upload**    | Automatic on first request        | Manual or via CI/CD pipeline      |
| **First request**     | Slow (cache miss, origin fetch)   | Fast (already cached)             |
| **Storage management**| CDN manages eviction              | You manage what's stored          |
| **Freshness**         | Controlled via TTL/headers        | You control by re-pushing         |
| **Origin load**       | Spikes on cache misses            | Minimal (content pre-distributed) |
| **Cost model**        | Pay for bandwidth + requests      | Pay for storage + bandwidth       |
| **Complexity**        | Simple — just point DNS           | More complex — deployment step    |
| **Best for**          | Dynamic sites, APIs               | Large static files, video, builds |

> **Real-World:** Most modern CDNs (Cloudflare, CloudFront) are **pull-based** by default.
> Push-based workflows are common for video platforms (Netflix pre-positions content on
> their Open Connect CDN appliances at ISPs).

---

## 15.4 CDN Caching Deep Dive

### Cache Keys

A **cache key** uniquely identifies a cached resource. By default, most CDNs use the full
URL as the cache key:

```
Cache Key = scheme + host + path + query string

Example: https://cdn.example.com/api/products?page=2&sort=price
```

**Customizing cache keys** is critical for performance:

| Strategy                    | Example                                  | Use Case                    |
|-----------------------------|------------------------------------------|-----------------------------|
| Ignore query strings        | `/style.css?v=1` and `/style.css?v=2`    | When query params are       |
|                             | both map to `/style.css`                 | for cache busting only      |
| Include specific headers    | Vary by `Accept-Language`                | Serve localized content     |
| Include cookies             | Vary by session/user type                | Personalized content        |
| Device type                 | Vary by `User-Agent` (mobile/desktop)    | Responsive content          |

### Cache-Control Headers

The `Cache-Control` HTTP header is the primary mechanism for controlling CDN caching behavior:

```
Cache-Control: public, max-age=31536000, immutable

  public        -> CDN (and browsers) may cache this
  max-age       -> Cache for 31,536,000 seconds (1 year)
  immutable     -> Content will never change (don't revalidate)
```

**Common Cache-Control Directives:**

| Directive          | Meaning                                                        |
|--------------------|----------------------------------------------------------------|
| `public`           | Any cache (CDN, browser, proxy) may store this                 |
| `private`          | Only the end-user's browser may cache (NOT the CDN)            |
| `no-cache`         | Cache may store, but MUST revalidate with origin before serving|
| `no-store`         | Do NOT cache at all (sensitive data)                           |
| `max-age=N`        | Cache is fresh for N seconds                                   |
| `s-maxage=N`       | Like max-age, but specifically for shared caches (CDN)         |
| `stale-while-revalidate=N` | Serve stale content while fetching fresh in background |
| `immutable`        | Content will never change; skip revalidation                   |

### TTL (Time to Live)

**TTL** determines how long content stays cached before the CDN checks the origin for updates.

**Recommended TTL by content type:**

| Content Type          | Recommended TTL      | Rationale                           |
|-----------------------|----------------------|-------------------------------------|
| Versioned static files| 1 year (immutable)   | Filename changes on update          |
| Images/fonts          | 1 month - 1 year     | Rarely change                       |
| CSS/JS (unversioned)  | 1 hour - 1 day       | May change with deployments         |
| HTML pages            | 5 min - 1 hour       | Changes frequently                  |
| API responses         | 0 - 5 minutes        | Data changes often                  |
| User-specific data    | No cache (private)   | Personalized, sensitive             |

---

## 15.5 Cache Invalidation Strategies

> "There are only two hard things in Computer Science: cache invalidation and naming things."
> — Phil Karlton

When content changes at the origin, you need the CDN to serve the updated version. There
are several strategies:

### 1. Purge (Hard Invalidation)

Immediately removes the cached object from all edge servers.

```
# Cloudflare API purge example
POST https://api.cloudflare.com/client/v4/zones/{zone_id}/purge_cache
{
  "files": ["https://example.com/images/logo.png"]
}
```

**Pros:** Immediate, precise
**Cons:** Slow to propagate globally (seconds to minutes), can spike origin load

### 2. Soft Purge (Stale-While-Revalidate)

Marks content as stale but continues serving it while fetching fresh content in the background.

```
  Timeline:
  
  [Content cached] --> [Soft purge] --> [Serve stale + fetch new] --> [Fresh content cached]
                                         (no user-visible delay)
```

**Pros:** No latency spike for users, smooth transition
**Cons:** Users may briefly see stale content

### 3. Versioned URLs (Cache Busting)

Append a version hash to the filename so updated content has a new URL:

```
  Old: /static/app.js        -> cached for 1 year
  New: /static/app.a3b8c9.js -> completely new cache entry

  Or with query strings:
  Old: /static/app.js?v=1.0
  New: /static/app.js?v=1.1
```

**Pros:** Instant, no purge needed, zero stale content risk
**Cons:** Requires build tooling, HTML must reference new URLs

> **Best Practice:** Use versioned URLs for static assets (CSS, JS, images) and short TTLs
> with soft purge for dynamic/HTML content.

---

## 15.6 CDN for Static vs Dynamic Content

### Static Content (Traditional CDN Use Case)

- Images, CSS, JavaScript, fonts, videos, PDFs
- Identical for every user
- Long TTLs, high cache hit ratios (95%+)

### Dynamic Content (Modern CDN Capabilities)

Modern CDNs can also accelerate dynamic content:

| Technique                    | How It Works                                          |
|------------------------------|-------------------------------------------------------|
| **Route optimization**       | CDN uses optimized private backbone instead of public  |
|                              | internet between edge and origin                       |
| **Connection pooling**       | Persistent connections between edge and origin reduce  |
|                              | TCP/TLS handshake overhead                             |
| **Edge compute**             | Run logic at the edge (Cloudflare Workers, Lambda@Edge)|
|                              | to generate dynamic responses without hitting origin   |
| **Dynamic content caching**  | Cache API responses with short TTLs (5-60 seconds)     |
| **ESI (Edge Side Includes)** | Assemble pages at the edge from cached + dynamic parts |

```
  Edge Compute Example:
  
  User --> Edge PoP --> [Cloudflare Worker runs]
                        - Checks auth token
                        - Reads from edge KV store
                        - Returns personalized response
                        (Origin never contacted!)
```

---

## 15.7 How CDN Routing Works Internally

Understanding how a CDN routes a user to the **nearest and fastest** edge server is fundamental
to understanding CDN performance.

### Anycast Routing — The Magic Behind CDN Speed

**Anycast** is a network addressing method where the **same IP address** is advertised from
multiple locations around the world. The network (BGP routing) automatically sends packets
to the **nearest** location.

```
  Traditional Unicast:                  Anycast:
  
  One IP = One server                   One IP = Many servers worldwide
  
  1.2.3.4 -> Virginia server            1.2.3.4 -> Advertised from:
                                                    - New York PoP
  User in Tokyo must                                - London PoP
  travel to Virginia                                - Tokyo PoP
  (200ms+ latency)                                  - Sydney PoP
  
                                         User in Tokyo -> routed to
                                         Tokyo PoP automatically (5ms!)
  
  How Anycast works:
  
  User in Tokyo                       Internet Backbone (BGP)
  +--------+                          +--------------------+
  | Device |--- "Connect to 1.2.3.4" -->| BGP sees multiple |
  +--------+                          | routes to 1.2.3.4:|
                                      |                    |
                                      | Via Tokyo PoP: 2ms |  <- SHORTEST
                                      | Via Sydney PoP:30ms|
                                      | Via Virginia: 150ms|
                                      +--------+-----------+
                                               |
                                      Routes to Tokyo PoP (nearest)
                                               |
                                      +--------v-----------+
                                      | Tokyo PoP          |
                                      | (Edge Server)      |
                                      | IP: 1.2.3.4        |
                                      +--------------------+
  
  Key Properties:
  - Routing decision is made by the NETWORK, not the CDN
  - Based on BGP (Border Gateway Protocol) shortest-path routing
  - Automatic failover: if Tokyo PoP goes down, BGP re-routes to Sydney
  - No DNS dependency (unlike GeoDNS) — routing happens at IP layer
  
  Used by: Cloudflare, Google, most modern CDNs
```

### GeoDNS Routing — DNS-Based Geographic Routing

**GeoDNS** returns different IP addresses based on the geographic location of the DNS resolver.

```
  How GeoDNS works:
  
  User in London:
  Browser: "What is the IP for cdn.example.com?"
  DNS Resolver -> Authoritative DNS (GeoDNS)
  GeoDNS: "Resolver IP is in London -> return London PoP IP"
  Response: cdn.example.com -> 10.2.0.1 (London edge server)
  
  User in Tokyo:
  Browser: "What is the IP for cdn.example.com?"
  DNS Resolver -> Authoritative DNS (GeoDNS)
  GeoDNS: "Resolver IP is in Tokyo -> return Tokyo PoP IP"
  Response: cdn.example.com -> 10.3.0.1 (Tokyo edge server)
  
  +--------+    DNS Query    +----------+    "You're in EU"    +--------+
  | User   +--------------->| GeoDNS   +--------------------->| London |
  | London |                | Server   |  Return: 10.2.0.1    | PoP    |
  +--------+                +----------+                       +--------+
  
  +--------+    DNS Query    +----------+    "You're in Asia"  +--------+
  | User   +--------------->| GeoDNS   +--------------------->| Tokyo  |
  | Tokyo  |                | Server   |  Return: 10.3.0.1    | PoP    |
  +--------+                +----------+                       +--------+
  
  Anycast vs GeoDNS:
  +------------------+--------------------------------+----------------------------+
  | Aspect           | Anycast                        | GeoDNS                     |
  +------------------+--------------------------------+----------------------------+
  | Routing layer    | Network (BGP/IP layer)         | DNS layer                  |
  | Routing decision | Network routers                | DNS server                 |
  | Failover speed   | Seconds (BGP reconvergence)    | Minutes (DNS TTL expiry)   |
  | Granularity      | Network proximity (hops)       | Geographic location (IP DB)|
  | Accuracy         | Very high (actual network path)| Good (GeoIP database)      |
  | DDoS resilience  | Excellent (traffic distributed)| Lower (single DNS IP)      |
  +------------------+--------------------------------+----------------------------+
  
  Modern CDNs use BOTH: Anycast for the edge servers + GeoDNS for 
  origin-shield or multi-region failover.
```

### TCP and Connection Optimizations

CDNs optimize the networking stack between user, edge, and origin:

```
  Problem: TCP + TLS handshake to a distant origin = 3+ round trips = 300ms+
  
  Without CDN:
  User (Tokyo) <---------- 150ms RTT ----------> Origin (Virginia)
  
  TCP handshake:      150ms (SYN -> SYN-ACK -> ACK)
  TLS handshake:      300ms (2 round trips for TLS 1.2)
  HTTP request:       150ms
  Total first byte:   600ms minimum!
  
  With CDN (edge in Tokyo):
  User (Tokyo) <--- 5ms RTT ---> Edge (Tokyo) <-- optimized --> Origin
  
  TCP handshake:      5ms  (to nearby edge)
  TLS handshake:      10ms (to nearby edge, TLS 1.3 = 1 RT)
  HTTP request:       5ms  (to edge, cache HIT)
  Total first byte:   20ms!  (30x faster)
  
  Even on cache MISS:
  User <-> Edge:      15ms (TCP+TLS+request)
  Edge <-> Origin:    Persistent connection (already established)
                      Optimized backbone (CDN's private network)
                      ~80ms (vs 150ms over public internet)
  Total first byte:   95ms (6x faster than no CDN)
  
  CDN TCP Optimizations:
  +------------------------------------------------------------------+
  | Optimization              | How It Helps                         |
  +------------------------------------------------------------------+
  | TCP connection pooling    | Edge maintains persistent connections |
  |                           | to origin (no per-request handshake) |
  +------------------------------------------------------------------+
  | TCP window tuning         | Larger initial window sizes for edge  |
  |                           | <-> origin (higher throughput)       |
  +------------------------------------------------------------------+
  | Congestion control        | BBR or CUBIC tuned for CDN backbone  |
  +------------------------------------------------------------------+
  | TLS session resumption    | Reuse TLS sessions (0-RTT in TLS 1.3)|
  +------------------------------------------------------------------+
  | Private backbone          | CDN's own fiber network between PoPs |
  |                           | (lower latency, less packet loss)    |
  +------------------------------------------------------------------+
  | Request collapsing        | Multiple edge misses for same object  |
  |                           | = single origin fetch (dedup at edge)|
  +------------------------------------------------------------------+
```

---

## 15.8 HTTP/2, HTTP/3, and QUIC at the CDN Edge

Modern CDNs are at the forefront of protocol adoption, often supporting the latest HTTP
versions before most origin servers do.

### HTTP/1.1 vs HTTP/2 vs HTTP/3

```
  HTTP/1.1 (1997):
  +------+     Connection 1: GET /style.css  -> Response
  |Client|     Connection 2: GET /app.js     -> Response
  |      |     Connection 3: GET /image1.png -> Response
  +------+     Connection 4: GET /image2.png -> Response
               Connection 5: GET /image3.png -> Response
               Connection 6: GET /font.woff  -> Response
  
  Problem: 6 separate TCP connections per domain!
  Each needs its own TCP + TLS handshake.
  Browsers limit to 6 connections per domain.
  
  ---
  
  HTTP/2 (2015):
  +------+     Single TCP Connection:
  |Client|-----> Stream 1: GET /style.css  -> Response
  |      |-----> Stream 2: GET /app.js     -> Response
  |      |-----> Stream 3: GET /image1.png -> Response (all multiplexed!)
  +------+-----> Stream 4: GET /image2.png -> Response
         |-----> Stream 5: GET /image3.png -> Response
         +-----> Stream 6: GET /font.woff  -> Response
  
  Benefits: 1 TCP connection, multiplexed streams, header compression (HPACK),
  server push, stream prioritization.
  
  Problem: TCP head-of-line blocking — if one packet is lost, ALL streams
  wait until it's retransmitted (TCP guarantees in-order delivery).
  
  ---
  
  HTTP/3 + QUIC (2022):
  +------+     Single QUIC Connection (over UDP):
  |Client|=====> Stream 1: GET /style.css  -> Response
  |      |=====> Stream 2: GET /app.js     -> Response
  |      |=====> Stream 3: GET /image1.png -> Response
  +------+=====> Stream 4: GET /image2.png -> Response
  
  QUIC solves TCP's problems:
  - No head-of-line blocking (each stream independent)
  - 0-RTT connection establishment (vs 2-3 RTT for TCP+TLS)
  - Built-in encryption (TLS 1.3 integrated into protocol)
  - Connection migration (survives network changes — WiFi to cellular)
  
  +------------------+----------+----------+-----------+
  | Feature          | HTTP/1.1 | HTTP/2   | HTTP/3    |
  +------------------+----------+----------+-----------+
  | Transport        | TCP      | TCP      | QUIC(UDP) |
  | Multiplexing     | No       | Yes      | Yes       |
  | Head-of-line blk | Per conn | Per conn | Per stream|
  | Header compress  | No       | HPACK    | QPACK     |
  | 0-RTT connect    | No       | No       | Yes       |
  | Connection migr  | No       | No       | Yes       |
  | Encryption       | Optional | Optional | Always    |
  +------------------+----------+----------+-----------+
```

### Why CDNs Are Critical for HTTP/3 Adoption

```
  Challenge: HTTP/3 requires QUIC (UDP-based). Many firewalls and 
  middleboxes block or throttle UDP traffic.
  
  Solution: CDN terminates HTTP/3 at the edge, speaks HTTP/1.1 or 
  HTTP/2 to the origin.
  
  User <---HTTP/3 (QUIC/UDP)---> CDN Edge <---HTTP/2 (TCP)---> Origin
  
  The user gets:
  - 0-RTT connection setup (instant page load on repeat visits)
  - No head-of-line blocking (faster parallel downloads)
  - Connection migration (stream continues when switching WiFi/cellular)
  
  The origin server doesn't need to change anything!
  
  CDN HTTP/3 support:
  - Cloudflare: Enabled by default (free)
  - Fastly: Supported
  - AWS CloudFront: Supported
  - Akamai: Supported
  - Google Cloud CDN: Supported
```

---

## 15.9 Edge Computing — Running Code at the CDN

Edge computing is the most transformative CDN advancement. Instead of just caching static
files, CDN edge servers can **execute application logic** — running code milliseconds from
the user, without ever touching the origin server.

### How Edge Computing Works

```
  Traditional CDN (cache only):
  
  User -> Edge PoP -> [Cache HIT?]
                        YES -> return cached file
                        NO  -> fetch from origin (slow)
  
  Edge Computing CDN:
  
  User -> Edge PoP -> [Run code at edge]
                        - Read/write edge KV store
                        - Transform requests/responses
                        - Generate dynamic HTML
                        - A/B test routing
                        - Auth token validation
                        - API response assembly
                        -> Return response (origin never touched!)
  
  Execution environment:
  +----------------------------------------------------------------+
  | Platform              | Runtime          | Cold Start | Limits  |
  +----------------------------------------------------------------+
  | Cloudflare Workers    | V8 Isolates      | 0ms (!)    | 10ms CPU|
  |                       | (JavaScript/Wasm) |            | per req |
  +----------------------------------------------------------------+
  | AWS Lambda@Edge       | Node.js, Python  | 50-200ms   | 5s exec |
  | (CloudFront)          |                  |            | 1MB body|
  +----------------------------------------------------------------+
  | AWS CloudFront Func   | JavaScript       | <1ms       | 1ms CPU |
  +----------------------------------------------------------------+
  | Fastly Compute@Edge   | Wasm (Rust, Go,  | 0ms (!)    | Generous|
  |                       | JS, AssemblyScript)|           |         |
  +----------------------------------------------------------------+
  | Vercel Edge Functions | V8 Isolates      | 0ms        | 25ms CPU|
  +----------------------------------------------------------------+
  | Deno Deploy           | V8 Isolates      | 0ms        | 50ms CPU|
  +----------------------------------------------------------------+
```

### Edge Computing Code Examples

```javascript
  // Cloudflare Worker: A/B Testing at the edge
  // Runs at 310+ locations worldwide, 0ms cold start
  
  addEventListener('fetch', event => {
    event.respondWith(handleRequest(event.request))
  })
  
  async function handleRequest(request) {
    // Determine user's test bucket (consistent hashing on cookie)
    const cookie = request.headers.get('Cookie') || ''
    const userId = getCookie(cookie, 'user_id') || crypto.randomUUID()
    const bucket = hashToPercent(userId) // 0-100
    
    // Route to different origins based on bucket
    let origin
    if (bucket < 50) {
      origin = 'https://v1.example.com'  // Control (50%)
    } else {
      origin = 'https://v2.example.com'  // Experiment (50%)
    }
    
    const response = await fetch(origin + new URL(request.url).pathname)
    
    // Add tracking header
    const newResponse = new Response(response.body, response)
    newResponse.headers.set('X-AB-Bucket', bucket < 50 ? 'control' : 'experiment')
    newResponse.headers.set('Set-Cookie', `user_id=${userId}; Path=/; Max-Age=86400`)
    
    return newResponse
  }
```

```javascript
  // Cloudflare Worker: Geo-personalization at the edge
  
  async function handleRequest(request) {
    const country = request.cf.country     // "JP", "US", "DE", etc.
    const city = request.cf.city           // "Tokyo", "New York", etc.
    const continent = request.cf.continent // "AS", "NA", "EU", etc.
    
    // Serve country-specific content without hitting origin
    const content = await EDGE_KV.get(`homepage:${country}`)
    if (content) {
      return new Response(content, {
        headers: { 'Content-Type': 'text/html', 'Cache-Control': 'public, max-age=300' }
      })
    }
    
    // Fallback to origin for uncached countries
    return fetch(request)
  }
```

### Edge Key-Value Stores

```
  Edge computing becomes truly powerful with edge-native storage:
  
  +-------------------------------------------------------------------+
  | Store               | Provider     | Read Latency  | Consistency  |
  +-------------------------------------------------------------------+
  | Workers KV          | Cloudflare   | <10ms global  | Eventually   |
  |                     |              |               | consistent   |
  +-------------------------------------------------------------------+
  | Durable Objects     | Cloudflare   | <10ms (same   | Strong       |
  |                     |              | location)     | (per object) |
  +-------------------------------------------------------------------+
  | DynamoDB@Edge       | AWS (via     | ~20ms         | Eventually   |
  | (Global Tables)     | Lambda@Edge) |               | consistent   |
  +-------------------------------------------------------------------+
  | Fastly KV Store     | Fastly       | <10ms         | Eventually   |
  +-------------------------------------------------------------------+
  
  Use cases for edge KV:
  - Feature flags (read flag at edge, no origin call)
  - Geo-specific configuration (pricing, currency, language)
  - URL redirects (millions of redirects served at edge)
  - Rate limiting counters
  - Auth session validation
  - Short URL resolution
```

---

## 15.10 CDN for Video Streaming

Video streaming is the **largest CDN use case by bandwidth** — Netflix alone accounts for
~15% of global internet traffic, nearly all served through CDNs.

### How Video Streaming Over CDN Works

```
  Video Pipeline:
  
  Raw Video -> Transcoding -> Packaging -> CDN Distribution -> Playback
  (4K master)   (multiple     (HLS/DASH    (edge servers    (adaptive
                 bitrates)     segments)     worldwide)       bitrate)
  
  Step 1: TRANSCODING (Server-side)
  +----------------------------------------------------------+
  | Original: 4K video, 20 Mbps, H.264                       |
  |                                                           |
  | Transcode into multiple quality levels (renditions):      |
  | +--------------------------------------------------+      |
  | | 1080p @ 5.0 Mbps (H.264)                        |      |
  | | 720p  @ 2.5 Mbps (H.264)                        |      |
  | | 480p  @ 1.0 Mbps (H.264)                        |      |
  | | 360p  @ 0.5 Mbps (H.264)                        |      |
  | | 240p  @ 0.3 Mbps (H.264)                        |      |
  | +--------------------------------------------------+      |
  +----------------------------------------------------------+
  
  Step 2: SEGMENTING (Split into small chunks)
  +----------------------------------------------------------+
  | Each rendition is split into 2-10 second segments:       |
  |                                                           |
  | 1080p: seg001.ts, seg002.ts, seg003.ts, ...              |
  | 720p:  seg001.ts, seg002.ts, seg003.ts, ...              |
  | 480p:  seg001.ts, seg002.ts, seg003.ts, ...              |
  |                                                           |
  | A MANIFEST file lists all available renditions + segments:|
  | master.m3u8 (HLS) or manifest.mpd (DASH)                |
  +----------------------------------------------------------+
  
  Step 3: CDN DISTRIBUTION
  +----------------------------------------------------------+
  | Segments are distributed to CDN edge servers:             |
  |                                                           |
  | Origin (S3) -> Origin Shield -> Edge PoPs worldwide      |
  |                                                           |
  | Edge server caches segments on first request (pull CDN)   |
  | OR segments pre-positioned on edges (push CDN, Netflix)   |
  +----------------------------------------------------------+
  
  Step 4: ADAPTIVE BITRATE PLAYBACK (Client-side)
  +----------------------------------------------------------+
  | Player downloads manifest -> starts with medium quality   |
  |                                                           |
  | Monitors bandwidth continuously:                          |
  | - Bandwidth high (WiFi) -> switch UP to 1080p             |
  | - Bandwidth drops (cellular) -> switch DOWN to 480p       |
  | - Buffer running low -> drop to 240p (prevent stall)      |
  |                                                           |
  | Each segment can be a DIFFERENT quality level:            |
  | seg001(720p) -> seg002(1080p) -> seg003(480p) -> ...     |
  | (seamless quality switching!)                              |
  +----------------------------------------------------------+
```

### HLS vs DASH

```
  +-------------------+--------------------------------+------------------------------+
  | Feature           | HLS (Apple)                    | DASH (MPEG)                  |
  +-------------------+--------------------------------+------------------------------+
  | Full name         | HTTP Live Streaming            | Dynamic Adaptive Streaming   |
  |                   |                                | over HTTP                    |
  +-------------------+--------------------------------+------------------------------+
  | Manifest format   | .m3u8 (playlist)               | .mpd (XML)                   |
  +-------------------+--------------------------------+------------------------------+
  | Segment format    | .ts (MPEG-TS) or .fmp4         | .m4s (fragmented MP4)        |
  +-------------------+--------------------------------+------------------------------+
  | Codec support     | H.264, H.265/HEVC              | Codec-agnostic (any codec)   |
  +-------------------+--------------------------------+------------------------------+
  | DRM support       | FairPlay (Apple)               | Widevine (Google), PlayReady |
  +-------------------+--------------------------------+------------------------------+
  | Browser support   | Safari native, others via JS   | Most browsers via JS (MSE)   |
  +-------------------+--------------------------------+------------------------------+
  | Latency           | 6-30 seconds (standard)        | 2-6 seconds (low latency)    |
  |                   | 2-5 sec (LL-HLS)               |                              |
  +-------------------+--------------------------------+------------------------------+
  | Used by           | Apple, Twitch, many OTT        | YouTube, Netflix, Hulu       |
  +-------------------+--------------------------------+------------------------------+
  
  In practice: Most platforms support BOTH HLS and DASH for maximum device coverage.
```

### Netflix Open Connect — Building Your Own CDN

```
  Netflix Open Connect Architecture:
  
  Netflix doesn't use traditional CDNs for video. They built their OWN CDN
  by placing custom servers INSIDE ISPs (Internet Service Providers).
  
  Traditional CDN:                    Netflix Open Connect:
  
  User -> ISP -> Internet -> CDN     User -> ISP -> [Netflix box INSIDE ISP]
                   PoP                              No internet traversal!
  
  How it works:
  1. Netflix identifies popular content per region
  2. During off-peak hours (2-6 AM), Netflix pushes content to 
     Open Connect Appliances (OCAs) deployed at ISPs
  3. When a user presses play, Netflix's control plane (in AWS)
     determines the best OCA to serve from
  4. Video streams from the OCA inside the user's OWN ISP
  
  +--------+     Control Plane (AWS)
  | Netflix|     - User auth, billing, recommendations
  | App    |     - Determines which OCA has the content
  +---+----+     - Returns streaming URL
      |
      v
  +--------+     +-------------------------------------------+
  | User's |     | ISP Network                               |
  | Device |     |                                           |
  |        |---->| +-------------------+                     |
  +--------+     | | Netflix OCA       |  <- Content served  |
                 | | (Open Connect     |     from INSIDE ISP |
                 | |  Appliance)       |     Zero internet   |
                 | | 100+ TB storage   |     hops!           |
                 | +-------------------+                     |
                 +-------------------------------------------+
  
  Scale:
  - 17,000+ servers in 6,000+ ISP locations in 175+ countries
  - Serves ~15% of all global internet traffic
  - Each OCA: custom FreeBSD, 100TB+ SSD storage, 100 Gbps network
  - Cost savings: Netflix avoids paying commercial CDN rates
  - ISP benefit: reduces their transit costs (traffic stays local)
```

---

## 15.11 CDN Security — Deep Dive

CDNs are a critical security layer. By sitting in front of your origin server, they can
absorb attacks, filter malicious traffic, and hide your infrastructure.

### DDoS Protection

```
  DDoS Attack WITHOUT CDN:
  
  Attacker botnet (100,000 bots)
       |
       v
  +--------+     100 Gbps flood     +----------+
  | Bots   |========================>| Origin   |  <- CRUSHED!
  | (100K) |                         | Server   |     (can handle
  +--------+                         | (1 Gbps) |      1 Gbps max)
                                     +----------+
  
  DDoS Attack WITH CDN:
  
  Attacker botnet (100,000 bots)
       |
       v
  +--------+     100 Gbps flood     +------------------+     Normal traffic
  | Bots   |========================>| CDN Edge Network |====================>
  | (100K) |                         | (150+ Tbps       |     +----------+
  +--------+                         |  total capacity) |     | Origin   |
                                     |                  |     | Server   |
                                     | DDoS mitigation: |     | (safe!)  |
                                     | - Rate limiting  |     +----------+
                                     | - IP reputation  |
                                     | - Challenge pages|
                                     | - Traffic scrubbing
                                     +------------------+
  
  CDN DDoS mitigation layers:
  
  Layer 3/4 DDoS (volumetric attacks — SYN floods, UDP floods):
  +----------------------------------------------------------------+
  | 1. Anycast distributes attack across ALL PoPs worldwide        |
  |    100 Gbps attack / 310 PoPs = ~300 Mbps per PoP (trivial)   |
  | 2. Edge routers drop malformed packets (hardware-level)        |
  | 3. Rate limiting per source IP                                 |
  | 4. BGP flowspec rules for known attack signatures              |
  +----------------------------------------------------------------+
  
  Layer 7 DDoS (application-layer — HTTP floods):
  +----------------------------------------------------------------+
  | 1. JavaScript challenges (bots can't execute JS)               |
  | 2. CAPTCHA for suspicious traffic patterns                     |
  | 3. Behavioral analysis (request rate, pattern anomalies)       |
  | 4. IP reputation database (known bad IPs)                      |
  | 5. WAF rules (block known attack patterns)                     |
  +----------------------------------------------------------------+
  
  CDN DDoS capacity:
  +-------------------+-------------------+
  | Provider          | Network Capacity  |
  +-------------------+-------------------+
  | Cloudflare        | 280+ Tbps         |
  | Akamai            | 250+ Tbps         |
  | AWS Shield Adv.   | Multi-Tbps        |
  | Google Cloud Armor| Multi-Tbps        |
  +-------------------+-------------------+
```

### Web Application Firewall (WAF) at the Edge

```
  WAF inspects HTTP requests and blocks malicious ones before they reach your origin.
  
  User Request -> CDN Edge -> [WAF Inspection] -> Origin (only clean traffic)
  
  WAF rules protect against:
  +-------------------------------------------------------------------+
  | Attack                    | WAF Rule Example                      |
  +-------------------------------------------------------------------+
  | SQL Injection             | Block: ' OR 1=1 --, UNION SELECT      |
  |                           | Pattern: /(\%27)|(\')|(\-\-)/i        |
  +-------------------------------------------------------------------+
  | Cross-Site Scripting (XSS)| Block: <script>, javascript:, onerror  |
  |                           | Pattern: /<script[^>]*>|on\w+\s*=/i   |
  +-------------------------------------------------------------------+
  | Path Traversal            | Block: ../../, /etc/passwd             |
  |                           | Pattern: /\.\.\/|\.\.\\/ /            |
  +-------------------------------------------------------------------+
  | Remote Code Execution     | Block: eval(, system(, exec(           |
  +-------------------------------------------------------------------+
  | Credential Stuffing       | Rate limit login endpoints             |
  |                           | Block known leaked credential patterns |
  +-------------------------------------------------------------------+
  | Bad Bots                  | Block known bot User-Agents            |
  |                           | JavaScript challenge for suspicious UA  |
  +-------------------------------------------------------------------+
  
  WAF rule types:
  - OWASP Core Rule Set (CRS) — open-source, covers top 10 vulnerabilities
  - Custom rules — your own patterns for your specific application
  - Managed rules — vendor-curated rules updated automatically
  - Rate-based rules — block IPs exceeding N requests per minute
```

### Origin Cloaking — Hiding Your Real Server

```
  Without origin cloaking:
  - Attacker discovers your origin IP (DNS history, SSL cert scan, etc.)
  - Attacker bypasses CDN and attacks origin directly
  
  With origin cloaking:
  +----------------------------------------------------------------+
  | 1. Only CDN's IP addresses are in DNS (origin IP never exposed)|
  | 2. Origin server firewall: ONLY allow traffic from CDN IPs     |
  |    iptables -A INPUT -s <CDN_IP_RANGES> -p tcp --dport 443 -j ACCEPT
  |    iptables -A INPUT -p tcp --dport 443 -j DROP                |
  | 3. Use Cloudflare Tunnel or AWS PrivateLink (no public origin) |
  | 4. Rotate origin IP if it was ever exposed                     |
  | 5. Use pull-based CDN (origin never serves users directly)     |
  +----------------------------------------------------------------+
  
  DNS records:
  WRONG: cdn.example.com -> CDN,  example.com -> 203.0.113.50 (origin exposed!)
  RIGHT: cdn.example.com -> CDN,  example.com -> CDN (both proxied through CDN)
         origin.example.com -> does not exist in public DNS
```

---

## 15.12 CDN Performance Optimization

### Image Optimization at the Edge

```
  Modern CDNs can transform images on-the-fly at the edge:
  
  Original: product-photo.png (2.5 MB, 4000x3000px, PNG)
  
  Request from mobile device:
  GET /images/product-photo.png?w=400&format=webp&quality=80
  
  CDN Edge transforms:
  - Resize: 4000x3000 -> 400x300 (matched to device width)
  - Format: PNG -> WebP (75% smaller)
  - Quality: 80% (acceptable for mobile)
  - Result: 35 KB (98.6% reduction from 2.5 MB!)
  
  CDN caches the transformed version for future mobile requests.
  
  Image optimization features by CDN:
  +-------------------+-----------------------------------------------+
  | Feature           | Description                                   |
  +-------------------+-----------------------------------------------+
  | Auto-format       | Serve WebP to Chrome, AVIF to supported       |
  |                   | browsers, JPEG as fallback                    |
  +-------------------+-----------------------------------------------+
  | Responsive resize | Resize based on client hints (viewport width) |
  +-------------------+-----------------------------------------------+
  | Quality reduction | Lossy compression to target file size         |
  +-------------------+-----------------------------------------------+
  | Lazy loading      | Serve placeholder, load full image on scroll  |
  +-------------------+-----------------------------------------------+
  | Blur-up           | Serve tiny blurred placeholder first, then    |
  |                   | full image (progressive loading UX)           |
  +-------------------+-----------------------------------------------+
  
  Providers:
  - Cloudflare Image Resizing / Polish
  - Imgix (dedicated image CDN)
  - Cloudinary (image + video CDN)
  - AWS CloudFront + Lambda@Edge for transforms
```

### Compression at the Edge

```
  CDNs compress responses automatically:
  
  Uncompressed:  style.css (150 KB)
  gzip:          style.css (35 KB)   — 77% reduction
  Brotli (br):   style.css (28 KB)   — 81% reduction
  Zstandard:     style.css (30 KB)   — 80% reduction
  
  How it works:
  Client: Accept-Encoding: gzip, deflate, br
  CDN:    Content-Encoding: br  (serves Brotli if client supports it)
  
  Compression comparison:
  +-------------------+-----------+-----------+-----------+
  | Algorithm         | Ratio     | Speed     | Support   |
  +-------------------+-----------+-----------+-----------+
  | gzip              | Good      | Fast      | Universal |
  | Brotli (br)       | Best      | Slower    | 97%+      |
  | Zstandard (zstd)  | Very good | Fastest   | Growing   |
  +-------------------+-----------+-----------+-----------+
  
  Best practice:
  - Enable Brotli for text assets (HTML, CSS, JS, JSON, SVG)
  - Pre-compress static assets at build time (Brotli level 11)
  - Use gzip as fallback for older clients
  - Don't compress already-compressed formats (JPEG, PNG, MP4, ZIP)
```

### Resource Hints and Preloading

```
  HTML hints that help browsers + CDNs deliver content faster:
  
  <head>
    <!-- DNS prefetch: resolve CDN domain early -->
    <link rel="dns-prefetch" href="https://cdn.example.com">
    
    <!-- Preconnect: establish TCP+TLS early -->
    <link rel="preconnect" href="https://cdn.example.com" crossorigin>
    
    <!-- Preload: fetch critical resources immediately -->
    <link rel="preload" href="/fonts/inter.woff2" as="font" crossorigin>
    <link rel="preload" href="/css/critical.css" as="style">
    
    <!-- Prefetch: fetch resources for NEXT page (low priority) -->
    <link rel="prefetch" href="/next-page.html">
  </head>
  
  HTTP 103 Early Hints (CDN feature):
  
  Before the origin has finished generating the response, the CDN
  can send preliminary hints to the browser:
  
  Step 1: Browser requests /page
  Step 2: CDN immediately sends 103 Early Hints:
          Link: </style.css>; rel=preload; as=style
          Link: </app.js>; rel=preload; as=script
  Step 3: Browser starts downloading style.css and app.js
  Step 4: Origin finishes, CDN sends 200 OK with the HTML
  Step 5: By the time browser parses HTML, CSS and JS are already downloaded!
  
  Result: Page loads 200-500ms faster (critical resources fetched early).
  Supported by: Cloudflare (Early Hints), Fastly, most modern CDNs.
```

---

## 15.13 CDN for API Acceleration

CDNs aren't just for static files. They accelerate APIs through caching, connection
optimization, and edge logic.

### API Response Caching

```
  Scenario: Product catalog API, 10,000 requests/second
  
  GET /api/products?category=electronics&page=1
  
  Without CDN:
  All 10,000 req/s hit your API servers -> database queries -> slow, expensive
  
  With CDN caching (TTL = 60 seconds):
  First request: cache MISS -> origin -> response cached at edge
  Next 599,999 requests in 60 seconds: cache HIT at edge (sub-5ms!)
  
  Cache-Control: public, s-maxage=60, stale-while-revalidate=300
  
  s-maxage=60:                Fresh for 60 seconds at CDN
  stale-while-revalidate=300: Serve stale for 300 seconds while refreshing
  
  Result: Origin handles ~1 request per minute per category/page.
  CDN handles the other 599,999. Origin load reduced by 99.9998%.
  
  What to cache:
  +--------------------------------------+----------+-----------+
  | API Endpoint                         | Cacheable| TTL       |
  +--------------------------------------+----------+-----------+
  | GET /api/products (catalog)          | YES      | 60-300s   |
  | GET /api/products/:id (detail)       | YES      | 60-300s   |
  | GET /api/config (app config)         | YES      | 300-3600s |
  | GET /api/user/me (personal data)     | NO       | private   |
  | POST /api/orders (write operation)   | NO       | no-store  |
  | GET /api/search?q=... (search)       | YES      | 30-60s    |
  | GET /api/feed (personalized)         | PARTIAL  | Vary by   |
  |                                      |          | user type |
  +--------------------------------------+----------+-----------+
```

### GraphQL API Caching at the Edge

```
  Challenge: GraphQL uses POST requests (CDNs don't cache POST by default)
  and every query can be different.
  
  Solutions:
  
  1. Persisted Queries (GET-based):
     Instead of: POST /graphql  body: {query: "{ products { name price } }"}
     Use:        GET /graphql?id=abc123  (pre-registered query hash)
     -> CDN can cache GET requests normally!
  
  2. Edge query normalization:
     Edge worker normalizes the GraphQL query (sort fields, remove whitespace)
     -> Same logical query always produces the same cache key
  
  3. Fragment caching:
     Cache individual resolver results at the edge:
     products:list -> cached 300s
     user:me -> not cached (private)
     config:settings -> cached 3600s
     
     Edge assembles response from cached fragments + fresh origin data.
```

---

## 15.14 CDN Failure Modes and Resilience

### What Happens When the CDN Fails?

```
  Failure Scenario 1: CDN PoP goes down
  +--------------------------------------------------------------+
  | Impact: Users routed to that PoP experience errors            |
  | Recovery: Anycast re-routes traffic to next nearest PoP       |
  | Time: Seconds (BGP reconvergence)                             |
  | User impact: Brief timeout, then normal service               |
  +--------------------------------------------------------------+
  
  Failure Scenario 2: CDN-wide outage (rare but happens)
  +--------------------------------------------------------------+
  | Real examples:                                                |
  | - Cloudflare outage (June 2022): Backbone routing issue       |
  | - Fastly outage (June 2021): Config bug, 1 hour global outage|
  | - AWS CloudFront (Nov 2020): Elevated error rates             |
  |                                                               |
  | Impact: ALL sites using that CDN are affected                 |
  | Recovery: Depends on the root cause (minutes to hours)        |
  +--------------------------------------------------------------+
  
  Failure Scenario 3: Origin is unreachable
  +--------------------------------------------------------------+
  | CDN can't fetch content on cache misses                       |
  | Mitigation: Serve stale content (stale-if-error)              |
  |   Cache-Control: public, max-age=300, stale-if-error=86400   |
  |   -> Serve stale content for up to 24 hours if origin is down|
  +--------------------------------------------------------------+
```

### Building CDN Resilience

```
  Strategy 1: MULTI-CDN FAILOVER
  
  Primary CDN (Cloudflare) -> healthy -> serve traffic
  Fallback CDN (CloudFront) -> standby
  
  Health check detects Cloudflare failure:
  DNS/Traffic Manager switches to CloudFront within seconds.
  
  Strategy 2: ORIGIN FALLBACK
  
  If CDN fails entirely, DNS can be updated to point directly
  to the origin (or a backup origin behind a different CDN).
  
  Automation:
  CDN health check fails -> 
    automated script updates DNS ->
    traffic goes directly to origin (or backup CDN)
  
  Strategy 3: STALE CONTENT POLICY
  
  Configure CDN to serve stale content when origin is unreachable:
  
  Cloudflare: "Always Online" feature (serves cached version from 
              Cloudflare's archive even when origin is completely down)
  
  CloudFront: Custom error pages + stale-if-error header
  
  Strategy 4: STATIC SITE FALLBACK
  
  If your dynamic origin fails, serve a static "maintenance mode" page
  from the CDN's cache or edge KV store:
  
  Edge Worker:
  if (origin_health == 'down') {
    return caches.match('/maintenance.html')
  }
```

---

## 15.15 Real-World CDN Architectures

### Cloudflare's Architecture

```
  Cloudflare's "Every server runs every service" philosophy:
  
  Traditional CDN:                    Cloudflare:
  Dedicated cache servers             Every server in every PoP
  Dedicated WAF servers               runs ALL services:
  Dedicated DNS servers               - Cache
  Dedicated DDoS servers              - WAF
                                      - DNS
                                      - DDoS mitigation
                                      - Workers (edge compute)
                                      - DNS resolver (1.1.1.1)
  
  Architecture:
  +----------------------------------------------------------------+
  | Every Cloudflare PoP (310+ locations):                          |
  |                                                                 |
  | +-----------------------------------------------------------+  |
  | | Every server:                                              |  |
  | | - Anycast IP (all servers share same IPs)                  |  |
  | | - Linux kernel + custom network stack                      |  |
  | | - Cache (tiered: memory -> SSD -> peer PoPs)               |  |
  | | - WAF (Lua rules engine)                                   |  |
  | | - Workers runtime (V8 isolates, thousands per server)      |  |
  | | - DNS resolver                                             |  |
  | | - DDoS mitigation (XDP/eBPF in kernel)                     |  |
  | +-----------------------------------------------------------+  |
  |                                                                 |
  | No single point of failure within a PoP.                        |
  | Any server can handle any request for any customer.             |
  +----------------------------------------------------------------+
  
  Tiered caching:
  Request -> Edge PoP cache (SSD) 
          -> Regional tier (larger PoPs, more storage)
          -> Origin Shield (one PoP designated per origin)
          -> Origin server
  
  Each tier reduces origin load further.
```

### How Akamai Works

```
  Akamai is the oldest and largest CDN (founded 1998).
  
  Scale: 4,100+ PoPs, 365,000+ servers, 130+ countries
  Serves 15-30% of all web traffic globally.
  
  Architecture:
  +----------------------------------------------------------------+
  | Edge Platform (hundreds of thousands of servers):               |
  |                                                                 |
  | Mapping System (DNS-based routing):                             |
  | - Uses real-time internet health data to route requests         |
  | - Considers: server load, network congestion, user proximity    |
  | - Updates every few seconds (not just static GeoDNS)            |
  |                                                                 |
  | Edge Servers:                                                   |
  | - 2-tier caching: "edge" (user-facing) + "parent" (regional)   |
  | - Custom HTTP stack optimized for performance                   |
  | - Runs customer-specific EdgeWorkers (JavaScript)               |
  |                                                                 |
  | SureRoute:                                                      |
  | - Continuously probes multiple paths between edge and origin    |
  | - Routes each request via the fastest path at that moment       |
  | - Can be 2-5x faster than default internet routing              |
  +----------------------------------------------------------------+
  
  Akamai's unique value:
  - Deeply embedded in ISPs (many servers deployed inside ISP networks)
  - Real-time internet mapping (monitors global internet health)
  - Enterprise-grade SLAs (used by banks, governments, large media)
```

---

### Edge Termination

Most CDNs terminate TLS at the edge, meaning:

```
  User <--HTTPS--> Edge Server <--HTTPS or HTTP--> Origin Server
       (TLS terminated here)
```

**Connection modes:**

| Mode                | User-to-Edge | Edge-to-Origin | Security Level |
|---------------------|--------------|----------------|----------------|
| **Flexible SSL**    | HTTPS        | HTTP           | Low (not E2E)  |
| **Full SSL**        | HTTPS        | HTTPS (any cert)| Medium         |
| **Full Strict SSL** | HTTPS        | HTTPS (valid cert)| High (recommended)|
| **End-to-End mTLS**| HTTPS        | mTLS           | Highest        |

### Benefits of Edge TLS Termination

1. **Faster TLS handshakes** — User negotiates TLS with nearby edge, not distant origin
2. **Certificate management** — CDN manages certs (many offer free certs)
3. **HTTP/2 and HTTP/3** — Edge supports latest protocols even if origin doesn't

---

## 15.16 Multi-CDN Strategy

Large organizations often use **multiple CDN providers** simultaneously.

### Why Multi-CDN?

```
  +--------+     +---------------+
  | Users  +---->| DNS / Traffic |
  +--------+     | Manager       |
                  +-------+-------+
                          |
             +------------+------------+
             |            |            |
        +----+----+  +----+----+  +----+----+
        |Cloudflare|  | Akamai  |  |CloudFront|
        +---------+  +---------+  +----------+
             |            |            |
             +------------+------------+
                          |
                  +-------+-------+
                  | Origin Server |
                  +---------------+
```

### Benefits and Trade-offs

| Benefit                       | Trade-off                                    |
|-------------------------------|----------------------------------------------|
| **Redundancy** — if one CDN   | Increased operational complexity              |
| has an outage, traffic shifts |                                              |
| **Performance** — route users | Cache hit ratios drop (traffic split across   |
| to fastest CDN per region     | multiple caches)                              |
| **Negotiating leverage** —    | Multiple contracts and billing to manage      |
| competitive pricing           |                                              |
| **Compliance** — different    | Inconsistent feature sets across CDNs         |
| CDNs for different regions    |                                              |

**Real-World:** Apple, Microsoft, and Netflix use multi-CDN strategies. Netflix built their
own CDN (Open Connect) and supplements with commercial CDNs.

---

## 15.17 CDN Providers Comparison

| Feature              | Cloudflare        | Akamai             | AWS CloudFront    | Fastly            |
|----------------------|-------------------|--------------------|-------------------|-------------------|
| **PoPs**             | 310+              | 4,100+             | 450+              | 80+               |
| **Edge Compute**     | Workers (V8)      | EdgeWorkers        | Lambda@Edge       | Compute@Edge(Wasm)|
| **Free Tier**        | Yes (generous)    | No                 | 1TB/month free    | Limited            |
| **DDoS Protection**  | Included          | Included           | AWS Shield        | Included          |
| **Instant Purge**    | ~30 seconds       | ~5 seconds         | Minutes           | ~150ms            |
| **Real-time Logs**   | Yes               | Yes                | Yes (S3/Kinesis)  | Yes (streaming)   |
| **Pricing Model**    | Flat rate plans   | Contract-based     | Pay-per-use       | Pay-per-use       |
| **Best For**         | General web,      | Enterprise,        | AWS-native apps   | API-heavy,        |
|                      | developers        | large media        |                   | real-time purge   |

---

## 15.18 CDN in System Design

### When to Add a CDN

Add a CDN to your architecture when:

- [x] Your users are geographically distributed
- [x] You serve static assets (images, CSS, JS, videos)
- [x] You need to handle traffic spikes or viral content
- [x] You want to reduce origin server load and costs
- [x] You need DDoS protection
- [x] Your application requires low-latency content delivery

### System Design Example: E-Commerce Platform

```
  +-------------+     +-----+     +-----------+     +-------------+
  |   Browser   +---->| CDN +---->| API       +---->| Application |
  |             |     |     |     | Gateway   |     | Servers     |
  +-------------+     +--+--+     +-----+-----+     +------+------+
                         |              |                    |
                   +-----+-----+       |             +------+------+
                   | Static    |       |             | Database    |
                   | Assets    |       |             | Cluster     |
                   | (S3/Blob) |       |             +-------------+
                   +-----------+       |
                                +------+------+
                                | Rate Limiter|
                                +-------------+
  
  What the CDN serves:
  - Product images, thumbnails
  - CSS, JavaScript bundles
  - Fonts and icons
  - Marketing pages (cached HTML)
  
  What goes to origin:
  - Shopping cart operations
  - User authentication
  - Order placement
  - Real-time inventory checks
```

---

## 15.19 Cost Considerations

CDN costs are typically based on:

| Cost Factor          | Description                                          | Optimization Tip              |
|----------------------|------------------------------------------------------|-------------------------------|
| **Bandwidth**        | GB transferred from edge to users                    | Compress assets (gzip/brotli) |
| **Requests**         | Number of HTTP requests handled                      | Bundle files, use sprites     |
| **Origin pulls**     | Requests from edge to your origin                    | Use origin shield, long TTLs  |
| **Edge compute**     | CPU time for edge functions                          | Cache compute results         |
| **SSL certificates** | Often free, but custom/wildcard may cost extra       | Use CDN-provided certs        |
| **Purge requests**   | Some CDNs charge for cache invalidation              | Use versioned URLs instead    |

### Cost Optimization Strategies

1. **Maximize cache hit ratio** — Longer TTLs, proper cache keys, origin shield
2. **Compress everything** — Enable Brotli/gzip at the edge (30-70% size reduction)
3. **Use appropriate image formats** — WebP/AVIF instead of PNG/JPEG
4. **Commit to contracts** — Reserved capacity is cheaper than on-demand
5. **Monitor and audit** — Track cache hit ratios; below 90% means you're overpaying

---

## 15.20 Common CDN Anti-Patterns

| Anti-Pattern                      | Problem                                   | Solution                          |
|-----------------------------------|-------------------------------------------|-----------------------------------|
| Caching user-specific content     | Users see each other's data               | Use `Cache-Control: private`      |
| No cache-busting strategy         | Users stuck with stale assets             | Use versioned URLs                |
| Caching error responses           | 500 errors cached and served to everyone  | Set `no-cache` on error responses |
| Same TTL for everything           | Over-caching dynamic, under-caching static| Tune TTL per content type         |
| No origin shield                  | Cache misses overload origin              | Enable origin shield              |
| Ignoring `Vary` header            | Wrong content served (e.g., wrong lang)   | Configure Vary properly           |

---

## Key Takeaways

```
+-----------------------------------------------------------------------+
|               Content Delivery Networks — Key Takeaways                |
+-----------------------------------------------------------------------+
|                                                                        |
|  1. A CDN is a geographically distributed caching layer that brings   |
|     content closer to users. Reduces latency by 10-30x and offloads   |
|     90%+ of traffic from the origin server.                           |
|                                                                        |
|  2. CDN routing uses Anycast (BGP shortest path, automatic failover   |
|     in seconds) and/or GeoDNS (DNS-based geo-routing). Modern CDNs    |
|     use both together.                                                 |
|                                                                        |
|  3. Pull CDNs fetch on demand (most web apps). Push CDNs pre-position |
|     content (video platforms, large static files, Netflix Open Connect)|
|                                                                        |
|  4. Cache-Control headers are your primary tool. Master: public,       |
|     s-maxage, no-cache, no-store, stale-while-revalidate, immutable.  |
|     Use versioned URLs for static assets (best invalidation strategy). |
|                                                                        |
|  5. HTTP/3 + QUIC is a game-changer: 0-RTT connections, no head-of-  |
|     line blocking, connection migration (WiFi<->cellular). CDNs are    |
|     the adoption path — edge speaks HTTP/3, origin stays HTTP/2.      |
|                                                                        |
|  6. Edge computing (Cloudflare Workers, Lambda@Edge) transforms CDNs  |
|     from dumb caches into programmable platforms. Run A/B tests, auth, |
|     geo-personalization, and API logic at the edge with 0ms cold start.|
|                                                                        |
|  7. Video streaming is the largest CDN workload. Adaptive bitrate      |
|     (HLS/DASH) segments video into chunks served from edge PoPs.       |
|     Netflix built its own CDN (Open Connect) inside ISPs.             |
|                                                                        |
|  8. CDN security: DDoS absorption (280+ Tbps capacity), WAF at edge   |
|     (SQL injection, XSS, bot detection), origin cloaking (hide origin |
|     IP, firewall to CDN IPs only).                                    |
|                                                                        |
|  9. CDN performance optimization: image transforms at edge (resize,    |
|     WebP/AVIF conversion), Brotli compression (81% reduction), HTTP    |
|     103 Early Hints (pre-load critical resources), resource hints.    |
|                                                                        |
| 10. API acceleration: cache GET responses with short TTLs (s-maxage), |
|     use stale-while-revalidate for smooth refreshes. Origin load can  |
|     be reduced by 99.99% for read-heavy API endpoints.                |
|                                                                        |
| 11. CDN resilience: multi-CDN failover, stale-if-error headers (serve |
|     stale for 24h if origin is down), static fallback pages. Plan for |
|     CDN-wide outages — they happen (Fastly 2021, Cloudflare 2022).   |
|                                                                        |
| 12. In system design interviews: mention CDN as the FIRST optimization|
|     for any user-facing system. Discuss: what to cache (static vs      |
|     dynamic), TTL strategy, invalidation approach, edge compute for   |
|     personalization, and DDoS protection.                             |
|                                                                        |
+-----------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 16 - Rate Limiting and Throttling](./16-rate-limiting.md)*
