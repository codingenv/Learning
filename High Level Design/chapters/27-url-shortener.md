# Chapter 27: Design a URL Shortener (TinyURL / Bitly)

## 1. Problem Statement

Design a URL shortening service like TinyURL or Bitly that takes a long URL and returns a
short, unique alias. When users visit the short URL, they are redirected to the original
long URL. The system must handle massive scale, provide low-latency redirects, and
optionally support analytics, custom aliases, and link expiration.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Shorten URL | Given a long URL, generate a unique short URL |
| FR2 | Redirect | When a user visits the short URL, redirect to the original long URL |
| FR3 | Custom Alias | Users can optionally choose a custom short code (e.g., `sho.rt/my-brand`) |
| FR4 | Link Expiry | Users can set an expiration time for shortened URLs |
| FR5 | Analytics | Track click count, referrer, geo, device (optional, async) |
| FR6 | Delete URL | Users can delete their shortened URLs |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | High Availability | 99.99% uptime for redirect service |
| NFR2 | Low Latency | Redirect in < 10ms (excluding network) |
| NFR3 | Not Guessable | Short codes should not be sequential or easily predictable |
| NFR4 | Durability | Once created, URL mappings must not be lost |
| NFR5 | Scalability | Handle 100M new URLs/day, 10B redirects/day |
| NFR6 | Read-Heavy | Read-to-write ratio ~100:1 |

---

## 4. Capacity Estimation

### 4.1 Traffic Estimation

```
Write (URL creation):
  100M new URLs/day
  = 100,000,000 / 86,400 sec
  ~ 1,160 writes/sec
  Peak: ~3,500 writes/sec (3x average)

Read (Redirects):
  Read:Write = 100:1
  = 100 * 1,160
  ~ 116,000 reads/sec
  Peak: ~350,000 reads/sec
```

### 4.2 Storage Estimation

```
Assume each URL mapping record:
  - short_code:    7 bytes
  - long_url:      average 200 bytes
  - created_at:    8 bytes
  - expires_at:    8 bytes
  - user_id:       8 bytes
  - metadata:      ~70 bytes
  Total:           ~300 bytes per record

Daily:   100M * 300 bytes = 30 GB/day
Yearly:  30 GB * 365 = ~11 TB/year
5 years: ~55 TB (manageable with sharding)
```

### 4.3 Bandwidth Estimation

```
Incoming (writes):  1,160 req/s * 300 bytes ~ 350 KB/s
Outgoing (reads):   116,000 req/s * 300 bytes ~ 35 MB/s
```

### 4.4 Cache Estimation

```
Following the 80/20 rule (80% traffic from 20% URLs):
  Daily unique URLs accessed: ~1B redirect / day (assume)
  20% hot URLs: 200M URLs
  Cache size: 200M * 300 bytes ~ 60 GB
  Fits in a few Redis instances
```

---

## 5. API Design

### 5.1 Shorten URL

```
POST /api/v1/shorten
Headers:
  Authorization: Bearer <api_key>
  Content-Type: application/json

Request Body:
{
  "long_url": "https://www.example.com/very/long/path?with=params&and=more",
  "custom_alias": "my-brand",       // optional
  "expires_at": "2025-12-31T23:59:59Z"  // optional
}

Response: 201 Created
{
  "short_url": "https://sho.rt/aB3xK9z",
  "short_code": "aB3xK9z",
  "long_url": "https://www.example.com/very/long/path?with=params&and=more",
  "created_at": "2024-01-15T10:30:00Z",
  "expires_at": "2025-12-31T23:59:59Z"
}
```

### 5.2 Redirect

```
GET /{shortCode}
  e.g., GET /aB3xK9z

Response: 301 Moved Permanently (or 302 Found)
Headers:
  Location: https://www.example.com/very/long/path?with=params&and=more
```

### 5.3 Delete URL

```
DELETE /api/v1/urls/{shortCode}
Headers:
  Authorization: Bearer <api_key>

Response: 204 No Content
```

### 5.4 Get Analytics

```
GET /api/v1/urls/{shortCode}/analytics
Headers:
  Authorization: Bearer <api_key>

Response: 200 OK
{
  "short_code": "aB3xK9z",
  "total_clicks": 15234,
  "clicks_by_day": [...],
  "top_referrers": [...],
  "top_countries": [...]
}
```

---

## 6. Short URL Generation Strategies

The core challenge: how do we generate a unique, short, non-guessable code?

### 6.1 How Many Characters Do We Need?

```
Characters in Base62: [a-z, A-Z, 0-9] = 62 characters

Length 6: 62^6 = 56.8 billion combinations
Length 7: 62^7 = 3.5 trillion combinations

At 100M URLs/day for 10 years = 365 billion URLs
Length 7 gives us 3.5T >> 365B (sufficient with 10x headroom)

Decision: Use 7-character Base62 codes
```

### 6.2 Approach 1: Hash + Collision Handling

```
Algorithm:
  1. Compute MD5 or SHA-256 of the long URL
  2. Take the first 43 bits (enough for 7 Base62 chars)
  3. Encode as Base62 -> 7-character code
  4. Check DB for collision
  5. If collision, append a counter and re-hash, or take next 43 bits

Pros:
  - Same URL always produces the same hash (dedup built-in)
  - Stateless — no coordination needed

Cons:
  - Collision handling adds complexity and DB lookups
  - Not truly "non-guessable" since same input = same output
  - Need to handle race conditions on collision check
```

### 6.3 Approach 2: Pre-Generated Key Service (KGS)

```
Algorithm:
  1. A Key Generation Service pre-generates millions of unique 7-char codes
  2. Stores them in a "keys" database table (unused_keys, used_keys)
  3. When a shorten request arrives, KGS hands out an unused key
  4. Mark the key as used (move from unused to used pool)

Key DB:
  +------------------+     +------------------+
  |   unused_keys    |     |    used_keys     |
  |------------------|     |------------------|
  | key: aB3xK9z     | --> | key: aB3xK9z     |
  | key: Zm8wP2q     |     | key: Qw5tY1n     |
  | key: ...          |     | key: ...          |
  +------------------+     +------------------+

Pros:
  - No collision handling needed
  - Very fast (just a lookup and move)
  - Keys are random and non-guessable

Cons:
  - KGS is a single point of failure (mitigate with replicas)
  - Need to pre-generate and store keys
  - Two servers could get the same key (use locking or batch allocation)

Batch Allocation:
  - Each app server requests a batch of 1,000 keys at once
  - Serves from local memory until batch is exhausted
  - Reduces contention on KGS dramatically
```

### 6.4 Approach 3: Counter-Based (Distributed ID)

```
Algorithm:
  1. Use a distributed counter (e.g., ZooKeeper, Twitter Snowflake)
  2. Each server gets a unique range: Server1 [1-1M], Server2 [1M-2M], etc.
  3. Convert counter value to Base62

Example:
  Counter = 2009215674938 -> Base62 -> "aB3xK9z"

Pros:
  - Guaranteed unique (no collisions)
  - Simple and fast

Cons:
  - Sequential by default (guessable) — mitigate with shuffling/XOR
  - Requires range coordination
```

### 6.5 Recommended Approach: KGS with Batch Allocation

The **Key Generation Service** approach is the cleanest for production:

```
  +----------+     Batch of 1000 keys      +---------+
  | App Srv 1| <-------------------------> |         |
  +----------+                              |   KGS   |
  +----------+     Batch of 1000 keys      |  (Key   |
  | App Srv 2| <-------------------------> | Genera- |
  +----------+                              |  tion   |
  +----------+     Batch of 1000 keys      | Service)|
  | App Srv N| <-------------------------> |         |
  +----------+                              +---------+
                                                 |
                                            +---------+
                                            | Key DB  |
                                            +---------+
```

---

## 7. Database Schema

### 7.1 URL Mapping Table

```sql
CREATE TABLE url_mappings (
    short_code   VARCHAR(7)    PRIMARY KEY,
    long_url     TEXT          NOT NULL,
    user_id      BIGINT,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    expires_at   TIMESTAMP,
    click_count  BIGINT        DEFAULT 0,
    is_active    BOOLEAN       DEFAULT TRUE
);

-- Index for looking up by long_url (dedup check)
CREATE INDEX idx_long_url ON url_mappings(long_url);

-- Index for cleanup of expired URLs
CREATE INDEX idx_expires_at ON url_mappings(expires_at)
    WHERE expires_at IS NOT NULL AND is_active = TRUE;
```

### 7.2 User Table

```sql
CREATE TABLE users (
    user_id       BIGINT       PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    api_key       VARCHAR(64)  UNIQUE NOT NULL,
    tier          ENUM('free', 'pro', 'enterprise') DEFAULT 'free',
    rate_limit    INT          DEFAULT 100,  -- requests per minute
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

### 7.3 Analytics Table (Append-Only)

```sql
CREATE TABLE click_events (
    event_id      BIGINT       PRIMARY KEY AUTO_INCREMENT,
    short_code    VARCHAR(7)   NOT NULL,
    clicked_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    ip_address    VARCHAR(45),
    user_agent    TEXT,
    referrer      TEXT,
    country       VARCHAR(2),
    device_type   VARCHAR(20)
);

-- Partition by time for efficient querying and cleanup
-- PARTITION BY RANGE (clicked_at) ...
```

---

## 8. Database Choice

### Why NoSQL (Key-Value) for URL Mappings?

| Factor | SQL | NoSQL (DynamoDB/Cassandra) |
|--------|-----|---------------------------|
| Access pattern | Simple key lookup | Simple key lookup |
| Schema | Fixed | Flexible |
| Scale | Vertical + sharding | Horizontal natively |
| Latency | Good | Excellent for key-value |
| Joins needed | No | No |
| Transactions | Not needed | Not needed |

**Decision**: Use **DynamoDB** or **Cassandra** for the URL mapping table (simple
key-value lookups at massive scale). Use a relational DB for user management.

```
DynamoDB Table Design:
  Table Name: url_mappings
  Partition Key: short_code (String)

  Attributes:
    short_code  (S)  - "aB3xK9z"
    long_url    (S)  - "https://example.com/..."
    user_id     (N)  - 12345
    created_at  (N)  - 1705312200 (epoch)
    expires_at  (N)  - 1735689599 (epoch)
    is_active   (BOOL) - true

  GSI: user_id-index (for listing a user's URLs)
    Partition Key: user_id
    Sort Key: created_at
```

---

## 9. Caching Strategy

### 9.1 Cache Architecture

Since the system is read-heavy (100:1), caching is critical.

```
Client --> Load Balancer --> App Server --> Cache (Redis) --> Database
                                              |
                                         Cache Hit? 
                                        /          \
                                      Yes           No
                                       |             |
                                  Return URL    Query DB
                                               Store in Cache
                                               Return URL
```

### 9.2 Cache Policy

```
Cache Type:        Redis Cluster
Eviction Policy:   LRU (Least Recently Used)
Cache Size:        ~60 GB (fits 200M hot URLs)
TTL:               24 hours (refresh on access)
Write Policy:      Write-through (write to DB + cache simultaneously)

Key Format:        "url:{short_code}" -> "{long_url}"
Example:           "url:aB3xK9z" -> "https://example.com/long/path"
```

### 9.3 Cache Warming

```
On service startup:
  - Load the top 1000 most-accessed URLs into cache
  - Pre-warm from analytics data
```

---

## 10. Read Path (Redirect Flow)

### 10.1 Step-by-Step Flow

```
1. User clicks: https://sho.rt/aB3xK9z
2. DNS resolves sho.rt to load balancer IP
3. Load balancer routes to an app server
4. App server extracts short_code = "aB3xK9z"
5. Check Redis cache for key "url:aB3xK9z"
6. CACHE HIT:  Return long_url immediately
   CACHE MISS: Query DynamoDB for short_code
               If found, store in Redis, return long_url
               If not found, return 404
7. Check if URL is expired (expires_at < now)
   If expired, return 410 Gone
8. Return HTTP 301/302 with Location header
9. Async: Log click event to Kafka for analytics
```

### 10.2 Read Path Diagram

```
                          +------------------+
    User clicks           |                  |
    short URL             |  Load Balancer   |
   ------>                |  (nginx/ALB)     |
                          +--------+---------+
                                   |
                          +--------v---------+
                          |                  |
                          |   App Server     |
                          |                  |
                          +---+---------+----+
                              |         |
                    +---------v--+  +---v-----------+
                    |            |  |                |
                    |   Redis    |  |   DynamoDB     |
                    |   Cache    |  |   (fallback)   |
                    |            |  |                |
                    +-----+------+  +-------+--------+
                          |                 |
                          v                 v
                    +-----------+    +-----------+
                    | 301/302   |    | 301/302   |
                    | Redirect  |    | Redirect  |
                    +-----------+    +-----------+
                          |
                          |  (async)
                    +-----v------+
                    |   Kafka    |
                    | (analytics)|
                    +-----+------+
                          |
                    +-----v------+
                    | Analytics  |
                    | Service    |
                    +------------+
```

---

## 11. 301 vs 302 Redirect Trade-offs

| Aspect | 301 (Permanent) | 302 (Temporary) |
|--------|----------------|-----------------|
| Meaning | "Moved Permanently" | "Found (Temporary)" |
| Browser caching | Browser caches the redirect | Browser does NOT cache |
| Subsequent visits | Browser goes directly to long URL | Browser hits our server every time |
| Server load | Lower (browser bypasses us) | Higher (every click hits us) |
| Analytics accuracy | POOR (we miss cached redirects) | EXCELLENT (we see every click) |
| SEO | Link juice passes to destination | Link juice stays with short URL |

### Decision Matrix

```
If analytics matter (Bitly use case):       Use 302
If pure redirection (no tracking needed):   Use 301
If hybrid:                                  Use 302 + client-side tracking pixel

Recommendation: Use 302 for most cases (analytics are valuable)
```

---

## 12. Analytics (Click Tracking)

### 12.1 Async Analytics Pipeline

Analytics must NOT add latency to the redirect. Use an async pipeline:

```
App Server                   Kafka                Analytics Consumer       OLAP DB
    |                          |                        |                     |
    |--- click event --------->|                        |                     |
    |   {short_code,           |--- consume batch ----->|                     |
    |    timestamp,            |                        |--- aggregate &  --->|
    |    ip, ua, referrer}     |                        |    store            |
    |                          |                        |                     |
    |<-- 302 redirect --------|                        |                     |
    |   (no wait for           |                        |                     |
    |    analytics)            |                        |                     |
```

### 12.2 Analytics Data Flow

```
1. App server publishes click event to Kafka topic "click-events"
2. Stream processor (Flink/Spark Streaming) consumes events
3. Enrichment: GeoIP lookup, device parsing from user-agent
4. Aggregate: per-minute, per-hour, per-day roll-ups
5. Store in OLAP database (ClickHouse, Druid, or TimescaleDB)
6. Serve analytics API from OLAP DB
```

---

## 13. Rate Limiting

### 13.1 Rate Limiting Strategy

```
Rate Limit by:
  - API key (for authenticated users): based on tier
  - IP address (for unauthenticated): 10 creates/hour
  
Tiers:
  Free:        100 URLs/day,  1,000 redirects/min
  Pro:         10,000 URLs/day, 100,000 redirects/min
  Enterprise:  Unlimited

Implementation:
  - Token bucket algorithm in Redis
  - Key: "rate:{api_key}:{window}" -> count
  - Sliding window counter
  
Redis Commands:
  INCR rate:{api_key}:{minute_timestamp}
  EXPIRE rate:{api_key}:{minute_timestamp} 60
  
  If count > limit: return 429 Too Many Requests
```

---

## 14. Scaling Strategy

### 14.1 Database Sharding

```
Sharding Strategy: Hash-based on short_code

  short_code -> hash(short_code) % N -> shard_id

  Example with 16 shards:
    hash("aB3xK9z") % 16 = 7 -> Shard 7
    hash("Zm8wP2q") % 16 = 3 -> Shard 3

  Consistent hashing preferred for easier rebalancing:

  +-------+     +-------+     +-------+     +-------+
  |Shard 0|     |Shard 1|     |Shard 2|     |Shard 3|
  | a-d   |     | e-h   |     | i-l   |     | m-p   |
  +-------+     +-------+     +-------+     +-------+
```

### 14.2 Full Scaling Architecture

```
                         +-----+
                         | DNS |
                         +--+--+
                            |
                    +-------v-------+
                    | Global Load   |
                    | Balancer      |
                    | (Anycast/CDN) |
                    +-------+-------+
                            |
              +-------------+-------------+
              |                           |
      +-------v-------+          +-------v-------+
      | Region: US    |          | Region: EU    |
      +-------+-------+          +-------+-------+
              |                           |
      +-------v-------+          +-------v-------+
      | App Servers   |          | App Servers   |
      | (Auto-scale)  |          | (Auto-scale)  |
      +---+-------+---+          +---+-------+---+
          |       |                  |       |
    +-----v-+  +--v------+    +-----v-+  +--v------+
    | Redis  |  |DynamoDB |    | Redis  |  |DynamoDB |
    |Cluster |  | (Global |    |Cluster |  | (Global |
    |        |  |  Table) |    |        |  |  Table) |
    +--------+  +---------+    +--------+  +---------+
```

---

## 15. Expired URL Cleanup Service

### 15.1 Cleanup Strategy

```
Approach: Lazy deletion + Periodic cleanup

Lazy Deletion:
  - On every read, check expires_at
  - If expired, return 410 Gone and mark as inactive
  - Pros: No extra service needed
  - Cons: Expired URLs still occupy storage

Periodic Cleanup (Cron Job):
  - Run daily during off-peak hours
  - Scan for expired URLs (expires_at < NOW() AND is_active = TRUE)
  - Batch delete or mark inactive
  - Return short_codes to KGS unused pool for recycling

  +----------+    scan expired    +----------+    recycle keys    +-----+
  |  Cron    | -----------------> | Database | -----------------> | KGS |
  |  Job     |    delete/mark     |          |    return codes    |     |
  +----------+                    +----------+                    +-----+
```

---

## 16. Full System Architecture

```
+------------------------------------------------------------------------+
|                        CLIENTS (Browser / Mobile / API)                 |
+-----------------------------------+------------------------------------+
                                    |
                            +-------v--------+
                            |  CDN / Edge    |
                            |  (CloudFront)  |
                            +-------+--------+
                                    |
                            +-------v--------+
                            | Load Balancer  |
                            | (ALB / nginx)  |
                            +-------+--------+
                                    |
                 +------------------+------------------+
                 |                  |                  |
          +------v------+   +------v------+   +------v------+
          | App Server  |   | App Server  |   | App Server  |
          | (Stateless) |   | (Stateless) |   | (Stateless) |
          +--+--+---+---+   +------+------+   +------+------+
             |  |   |              |                  |
             |  |   +---------+---+--+----------------+
             |  |             |      |
       +-----v--v--+   +-----v------v---+   +-----------------+
       |   Redis   |   |   DynamoDB     |   |  Key Generation |
       |   Cache   |   |   (URL Store)  |   |  Service (KGS)  |
       |  Cluster  |   |   (Sharded)    |   |                 |
       +-----------+   +----------------+   +--------+--------+
                                                     |
             +---------------------+           +-----v--------+
             |  Kafka / SQS       |           |  Key Database |
             |  (Click Events)    |           |  (unused/used)|
             +--------+-----------+           +--------------+
                      |
             +--------v-----------+
             | Analytics Pipeline |
             | (Flink / Spark)    |
             +--------+-----------+
                      |
             +--------v-----------+
             | ClickHouse / OLAP  |
             | (Analytics Store)  |
             +--------------------+
                      |
             +--------v-----------+
             | Analytics API      |
             | Dashboard          |
             +--------------------+

  +--------------------+
  | Cleanup Cron Job   |----> Scans expired URLs, recycles keys
  +--------------------+

  +--------------------+
  | Rate Limiter       |----> Redis-based token bucket
  | (Middleware)       |
  +--------------------+
```

---

## 17. Write Path (URL Creation Flow)

```
1. Client sends POST /api/v1/shorten with long_url
2. Rate limiter checks API key quota -> reject if exceeded
3. Validate long_url format (valid URL, not blacklisted)
4. Check if custom_alias requested:
   a. YES: Check if alias is available in DB
           If taken, return 409 Conflict
           If available, use it as short_code
   b. NO:  Get next key from local batch (from KGS)
           If local batch empty, request new batch from KGS
5. Create URL mapping record in DynamoDB
6. Write to Redis cache (write-through)
7. Return short URL to client

  Client       Rate Limiter    App Server       KGS        DynamoDB     Redis
    |               |              |              |            |           |
    |--POST-------->|              |              |            |           |
    |               |--check------>|              |            |           |
    |               |              |--get key---->|            |           |
    |               |              |<--key--------|            |           |
    |               |              |              |            |           |
    |               |              |--write---------------------->|       |
    |               |              |--write------------------------------>|
    |               |              |              |            |           |
    |<-201 Created-----------------|              |            |           |
```

---

## 18. Security Considerations

```
1. URL Validation:
   - Reject malicious URLs (phishing, malware)
   - Integrate with Google Safe Browsing API
   - Block known bad domains

2. Abuse Prevention:
   - Rate limiting per IP and API key
   - CAPTCHA for anonymous users creating many URLs
   - Monitor for spam patterns

3. Short Code Security:
   - Use random generation (KGS), not sequential
   - No enumeration attacks possible
   - Don't expose internal IDs

4. HTTPS:
   - All API calls over HTTPS
   - Redirect URLs via HTTPS
   
5. Input Sanitization:
   - Prevent XSS in custom aliases
   - Validate URL format strictly
```

---

## 19. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Short code generation | Hash-based | KGS (pre-generated) | **KGS** | No collisions, simpler |
| Database | SQL (PostgreSQL) | NoSQL (DynamoDB) | **DynamoDB** | Key-value pattern, auto-scaling |
| Redirect type | 301 Permanent | 302 Temporary | **302** | Analytics accuracy |
| Cache eviction | LRU | LFU | **LRU** | Simpler, effective for hot URLs |
| Analytics processing | Sync (in redirect path) | Async (Kafka pipeline) | **Async** | Don't add redirect latency |
| Code length | 6 chars | 7 chars | **7 chars** | 10x headroom for 10 years |
| Sharding strategy | Range-based | Hash-based | **Hash-based** | Even distribution |

---

## 20. Failure Scenarios and Mitigations

```
Scenario 1: KGS goes down
  Mitigation: Each app server has a local batch of ~10K keys
              Can serve for hours without KGS
              KGS runs as replicated service (primary + standby)

Scenario 2: Cache (Redis) goes down
  Mitigation: Fall back to DynamoDB directly
              DynamoDB can handle the load (provisioned capacity)
              Redis cluster with replicas for HA

Scenario 3: Database goes down
  Mitigation: DynamoDB multi-AZ replication
              Cache serves reads during DB issues
              Writes queue in Kafka, replay when DB recovers

Scenario 4: Hot URL (viral link)
  Mitigation: CDN caching at edge for very hot URLs
              Redis handles 100K+ reads/sec per node
              Multiple cache replicas
```

---

## 21. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. URL shorteners are READ-HEAVY (100:1) -- cache aggressively   |
|                                                                    |
| 2. Key Generation Service (KGS) eliminates collision handling     |
|    and is the cleanest approach for unique code generation        |
|                                                                    |
| 3. 7-character Base62 codes give 3.5T combinations (10yr+)       |
|                                                                    |
| 4. Use 302 redirects if you want analytics (301 gets cached      |
|    by the browser and you lose visibility)                        |
|                                                                    |
| 5. Analytics MUST be async -- never block the redirect path       |
|                                                                    |
| 6. NoSQL (DynamoDB) is ideal for this simple key-value pattern    |
|                                                                    |
| 7. Cache the hot 20% of URLs in Redis (~60GB fits easily)        |
|                                                                    |
| 8. Shard by hash of short_code for even distribution              |
|                                                                    |
| 9. Security: validate URLs, rate-limit, use random codes          |
|                                                                    |
| 10. Design for failure: local key batches, cache fallback,        |
|     multi-AZ databases                                            |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 28 - Design a Social Media Feed (Twitter / X)](./28-social-feed.md)*
