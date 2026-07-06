# Chapter 4: Back-of-the-Envelope Estimation

---

## 4.1 Why Estimation Matters in System Design

Back-of-the-envelope estimation is the art of quickly calculating approximate system
requirements using simplified assumptions and basic math. It is one of the most critical
skills in High Level Design because it **grounds** your architectural decisions in reality.

```
+-------------------------------------------------------------------+
|                    WHY ESTIMATE?                                  |
+-------------------------------------------------------------------+
|                                                                   |
|  Without estimation:           With estimation:                   |
|                                                                   |
|  "We need a database"          "We need a database that can       |
|                                 handle 5,000 writes/sec and       |
|                                 store 50 TB over 3 years"         |
|                                                                   |
|  "We should add caching"       "We need ~40 GB of cache memory   |
|                                 to cover the 20% hot data that    |
|                                 serves 80% of reads"              |
|                                                                   |
|  "We need multiple servers"    "We need ~15 application servers   |
|                                 to handle 10,000 peak QPS at      |
|                                 700 QPS per server"               |
|                                                                   |
+-------------------------------------------------------------------+
```

### What Estimation Provides

| Benefit                        | Description                                            |
|--------------------------------|--------------------------------------------------------|
| **Feasibility check**          | Can the proposed design actually handle the load?      |
| **Technology selection**       | Which database, cache, or queue can meet the numbers?  |
| **Cost estimation**            | How many servers, how much storage = infrastructure $  |
| **Bottleneck identification**  | Where will the system hit limits first?                |
| **Communication tool**         | Numbers make architectural proposals concrete          |

> **Important:** Estimation is not about precision. It is about being **within an order of
> magnitude** (factor of 10). If the real answer is 500 QPS, an estimate of 300-1000 QPS
> is excellent. An estimate of 50 or 5,000 indicates a flawed assumption.

---

## 4.2 Powers of 2 Reference Table

When estimating, you will constantly deal with data sizes. Knowing the powers of 2 by heart
eliminates the need for a calculator.

### Essential Powers of 2

| Power  | Exact Value          | Approximate Value | Common Name         |
|--------|----------------------|-------------------|---------------------|
| 2^10   | 1,024                | ~1 Thousand       | 1 KB (Kilobyte)     |
| 2^20   | 1,048,576            | ~1 Million        | 1 MB (Megabyte)     |
| 2^30   | 1,073,741,824        | ~1 Billion        | 1 GB (Gigabyte)     |
| 2^40   | 1,099,511,627,776    | ~1 Trillion       | 1 TB (Terabyte)     |
| 2^50   | ~1.13 x 10^15        | ~1 Quadrillion    | 1 PB (Petabyte)     |

### Quick Conversion Table

| Unit      | Bytes               | Bits                 | Practical Example                 |
|-----------|---------------------|----------------------|-----------------------------------|
| 1 Byte    | 1                   | 8                    | One ASCII character               |
| 1 KB      | 1,024               | 8,192                | A short email                     |
| 1 MB      | ~1 Million           | ~8 Million           | A high-res photo                  |
| 1 GB      | ~1 Billion           | ~8 Billion           | A movie (SD quality)              |
| 1 TB      | ~1 Trillion          | ~8 Trillion          | ~250,000 photos or 500 hours HD   |
| 1 PB      | ~1 Quadrillion       | ~8 Quadrillion       | ~500 billion pages of text        |

### Useful Approximations for Estimation

```
  +---------------------------------------------------+
  |  HANDY NUMBERS TO MEMORIZE                        |
  +---------------------------------------------------+
  |                                                   |
  |  Seconds in a day:    ~86,400   (use 10^5 or     |
  |                                  ~100,000)        |
  |  Seconds in a month:  ~2.5 Million               |
  |  Seconds in a year:   ~31.5 Million (~3 x 10^7)  |
  |                                                   |
  |  1 Million requests/day = ~12 QPS                 |
  |  1 Billion requests/day = ~12,000 QPS             |
  |                                                   |
  |  Average tweet size:   ~300 bytes                 |
  |  Average web page:     ~2 MB                      |
  |  Average photo:        ~200 KB (compressed)       |
  |  Average video (1 min): ~10 MB (compressed)       |
  |  Average user metadata: ~1 KB                     |
  |                                                   |
  +---------------------------------------------------+
```

---

## 4.3 Latency Numbers Every Programmer Should Know

These numbers, originally compiled by Jeff Dean and updated over the years, help you
understand the cost of different operations and make informed design decisions.

### The Latency Hierarchy

```
  Operation                                Latency        Relative Scale
  =========                                =======        ==============
  
  L1 cache reference .................... 0.5 ns          |
  Branch mispredict ..................... 5 ns            |
  L2 cache reference ................... 7 ns            |  NANOSECONDS
  Mutex lock/unlock .................... 25 ns           |  (CPU-bound)
  Main memory (RAM) reference .......... 100 ns          |
                                                          
  Compress 1 KB with Zippy ............. 3,000 ns        |
  Send 1 KB over 1 Gbps network ....... 10,000 ns       |  MICROSECONDS
  Read 4 KB randomly from SSD ......... 150,000 ns       |  (I/O-bound)
  Read 1 MB sequentially from RAM ..... 250,000 ns       |
                                                          
  Round trip within same datacenter .... 500,000 ns       |
  Read 1 MB sequentially from SSD ..... 1,000,000 ns     |  MILLISECONDS
  HDD seek ............................ 10,000,000 ns    |  (Disk/Network)
  Read 1 MB sequentially from HDD ..... 20,000,000 ns    |
  Send packet CA -> Netherlands -> CA .. 150,000,000 ns   |
```

### Visual Scale (Orders of Magnitude)

```
  1 ns       1 us       1 ms       1 s        1 min
  |          |          |          |          |
  v          v          v          v          v
  L1   RAM   SSD read   HDD seek   Cross-     DNS
  cache ref  (4KB)      (1MB seq)  continent  timeout
                                   round trip (worst)
  
  |<-- CPU -->|<-- I/O -->|<-- Disk/Network ----------->|
```

### What These Numbers Mean for Design

| Design Decision                                  | Informed by Latency Numbers              |
|--------------------------------------------------|------------------------------------------|
| Use in-memory cache (Redis) instead of DB query  | RAM: 100 ns vs SSD: 150,000 ns          |
| Use CDN for static content                       | Same DC: 0.5 ms vs cross-continent: 150 ms|
| Prefer SSD over HDD for databases                | SSD read: 150 us vs HDD seek: 10 ms     |
| Batch small writes instead of many single writes | Each I/O has fixed overhead              |
| Co-locate services in the same datacenter        | Same DC: 0.5 ms vs cross-DC: 150 ms     |
| Compress data before sending over network        | Compression: 3 us + less network time    |

> **Key Insight:** There is roughly a **10x gap** between each level of the storage
> hierarchy: L1 -> L2 -> RAM -> SSD -> HDD -> Network. This is why caching works ---
> moving data one level closer to the CPU provides approximately 10x improvement.

---

## 4.4 Traffic Estimation: DAU to QPS

### The Standard Flow

```
  Total Registered Users
          |
          | x active_ratio (typically 10-50%)
          v
  Daily Active Users (DAU)
          |
          | x actions_per_user_per_day
          v
  Total Daily Requests
          |
          | / 86,400 (seconds in a day)
          v
  Average QPS
          |
          | x peak_multiplier (typically 2-5x)
          v
  Peak QPS
          |
          | x safety_margin (typically 1.5-2x)
          v
  Design QPS (what you architect for)
```

### Worked Example: Social Media Platform

```
  Given:
    Total users:          500 Million
    Active ratio:         20%
    Actions per user/day: 30 (10 reads, 15 scrolls, 3 likes, 2 posts)
  
  Step 1: DAU
    DAU = 500M x 0.20 = 100M
  
  Step 2: Total daily requests
    Daily requests = 100M x 30 = 3 Billion requests/day
  
  Step 3: Average QPS
    Avg QPS = 3,000,000,000 / 86,400
            = ~34,722 QPS
            ~ 35K QPS (rounded)
  
  Step 4: Peak QPS
    Peak QPS = 35K x 3 = ~105K QPS
    (Peak multiplier of 3x accounts for prime-time hours)
  
  Step 5: Design QPS
    Design QPS = 105K x 1.5 = ~160K QPS
    (Safety margin for growth and unexpected spikes)
```

### Read vs Write Split

Most systems are read-heavy. Understanding the read/write ratio is crucial:

| System Type          | Typical Read:Write Ratio | Example                    |
|----------------------|--------------------------|----------------------------|
| Social media feed    | 100:1                    | Many reads, few posts      |
| E-commerce catalog   | 50:1                     | Many browses, few purchases|
| Chat application     | 1:1                      | Equal send and receive     |
| Logging system       | 1:10                     | Mostly writes (ingestion)  |
| Analytics dashboard  | 10:1                     | Periodic reads of batch data|

```
  Social Media Example (from above):
  
  Total QPS:   35K (average)
  Read:Write = 100:1
  
  Read QPS:  35K x (100/101) ~ 34,650 QPS
  Write QPS: 35K x (1/101)   ~    350 QPS
  
  This tells us: optimize heavily for READS (caching, read replicas)
```

---

## 4.5 Storage Estimation

### The Formula

```
  Daily Storage = (Number of new records per day) x (Average record size)
  
  Monthly Storage = Daily Storage x 30
  
  Yearly Storage = Daily Storage x 365
  
  Total Storage = Yearly Storage x Retention Period (years)
                  + Replication Factor (typically 3x)
```

### Estimating Record Size

Break each record into its components:

```
  Example: Social Media Post
  
  +---------------------------+--------+
  | Field                     | Size   |
  +---------------------------+--------+
  | post_id (UUID)            | 16 B   |
  | user_id (UUID)            | 16 B   |
  | text (max 280 chars)      | 280 B  |
  | timestamp                 | 8 B    |
  | likes_count               | 4 B    |
  | comments_count            | 4 B    |
  | media_url (if applicable) | 256 B  |
  | metadata (JSON)           | ~200 B |
  +---------------------------+--------+
  | TOTAL                     | ~784 B |
  +---------------------------+--------+
  
  Round up to ~1 KB per post (to account for indexing overhead)
```

### Worked Example: Social Media Storage

```
  Given:
    New posts per day:    200M (from our 100M DAU, avg 2 posts each)
    Average post size:    1 KB
    Retention:            5 years
    Replication factor:   3x
  
  Daily storage:
    200M x 1 KB = 200 GB/day
  
  Yearly storage:
    200 GB x 365 = 73 TB/year
  
  5-year storage:
    73 TB x 5 = 365 TB
  
  With replication (3x):
    365 TB x 3 = 1,095 TB ~ 1.1 PB
  
  For media (images):
    20% of posts have images, avg 200 KB each
    200M x 0.20 x 200 KB = 8 TB/day (stored in object storage like S3)
    8 TB x 365 x 5 = 14.6 PB over 5 years
```

> **Takeaway:** Media (images, videos) almost always dominates storage. Text data is
> relatively tiny in comparison. This is why media is typically stored in object storage
> (S3, GCS) separately from the primary database.

---

## 4.6 Bandwidth Estimation

### The Formula

```
  Incoming (Ingress) Bandwidth = Write QPS x Average request size
  
  Outgoing (Egress) Bandwidth = Read QPS x Average response size
```

### Worked Example

```
  Given (from our social media example):
    Write QPS:            350 (average), 1,050 (peak)
    Read QPS:             34,650 (average), 103,950 (peak)
    Average write payload: 1 KB (text post)
    Average read response: 10 KB (post + user info + metadata)
  
  Ingress (writes):
    Average: 350 x 1 KB = 350 KB/s ~ 0.35 MB/s
    Peak:    1,050 x 1 KB = 1,050 KB/s ~ 1 MB/s
  
  Egress (reads):
    Average: 34,650 x 10 KB = 346,500 KB/s ~ 340 MB/s
    Peak:    103,950 x 10 KB = 1,039,500 KB/s ~ 1 GB/s
  
  Note: These are for the API responses only. If media is served via CDN,
  the CDN handles that bandwidth separately.
```

### Bandwidth Context

| Bandwidth     | Context                                            |
|---------------|----------------------------------------------------|
| 1 MB/s        | A single modern server can easily handle this      |
| 100 MB/s      | Moderate load; standard NIC capacity               |
| 1 GB/s        | Heavy load; may need multiple servers or 10G NIC   |
| 10 GB/s       | Very heavy; requires load balancing and CDN         |
| 100 GB/s+     | Extreme; CDN is essential (e.g., Netflix, YouTube)  |

---

## 4.7 Memory Estimation for Caching

### The 80/20 Rule (Pareto Principle)

In most systems, **20% of the data** serves **80% of the requests**. Therefore, caching
the top 20% of hot data can dramatically reduce database load.

```
  +-------------------------------------------------------------------+
  |                    THE 80/20 RULE                                 |
  |                                                                   |
  |   Requests                                                        |
  |   100% |================================|                         |
  |    80% |==========================|     |  <-- 80% of requests    |
  |        |                          |     |      hit 20% of data    |
  |    20% |======|                   |     |                         |
  |        +------+-------------------+-----+                         |
  |        0%    20%                        100%  Data                |
  |                                                                   |
  |   Cache the top 20% to serve 80% of reads from memory!           |
  +-------------------------------------------------------------------+
```

### Cache Memory Formula

```
  Cache Memory = Daily Read Requests x Average Response Size x 0.20
  
  (Cache 20% of daily unique data to cover 80% of reads)
```

### Worked Example

```
  Given:
    Read QPS (average):    34,650
    Average response size: 10 KB
    Unique read requests:  ~80% (some requests are for the same data)
  
  Daily unique read data:
    34,650 QPS x 86,400 sec x 10 KB x 0.80 (unique ratio)
    = 34,650 x 86,400 x 10,000 x 0.80 bytes
    = ~24 TB of unique data read per day
  
  Cache 20% of that:
    24 TB x 0.20 = ~4.8 TB
  
  Hmm, that's a lot. Let's reconsider...
  
  Alternative approach: Cache the most recent / frequently accessed items
  
  If we cache the last 2 hours of hot content:
    Posts in last 2 hours: (200M posts/day / 12) ~ 16.7M posts
    Top 20%: 16.7M x 0.20 = 3.3M posts
    Cache size: 3.3M x 10 KB = 33 GB
  
  33 GB is very achievable with Redis (a single large instance or a small cluster).
```

> **Practical Tip:** In real systems, cache size estimation is iterative. Start with the
> 80/20 rule, then refine based on actual access patterns observed in production. Most
> teams start with a smaller cache and scale up based on cache hit rate metrics.

---

## 4.8 Number of Servers Estimation

### The Formula

```
  Number of Servers = Peak QPS / QPS per Server
  
  Where "QPS per Server" depends on:
  - CPU-bound work: 100-500 QPS per server (heavy computation)
  - I/O-bound work: 500-2,000 QPS per server (mostly DB/cache calls)
  - Simple proxying: 5,000-50,000 QPS per server (reverse proxy, API gateway)
```

### Server Capacity Rules of Thumb

| Workload Type           | QPS per Server (typical) | Examples                       |
|-------------------------|--------------------------|--------------------------------|
| CPU-intensive           | 100-500                  | Image processing, ML inference |
| Database-backed CRUD    | 500-1,000                | Standard web APIs              |
| Cache-backed reads      | 2,000-5,000              | Reading from Redis/Memcached   |
| Static content serving  | 10,000-50,000            | Nginx serving static files     |

### Worked Example

```
  Given:
    Peak QPS:             160K (design target)
    Workload type:        Mixed (30% DB-backed, 70% cache-backed)
    DB-backed capacity:   800 QPS/server
    Cache-backed capacity: 3,000 QPS/server
  
  DB-backed QPS:     160K x 0.30 = 48K QPS
  Cache-backed QPS:  160K x 0.70 = 112K QPS
  
  Servers for DB work:    48K / 800  = 60 servers
  Servers for cache work: 112K / 3,000 = ~38 servers
  
  Total app servers: 60 + 38 = 98 servers
  
  With redundancy (N+2 per availability zone, 3 AZs):
  ~100 servers across 3 AZs = ~34 servers per AZ
  With N+2: 36 servers per AZ = 108 total
  
  Round up: ~110 application servers
```

---

## 4.9 Worked Example: URL Shortener (100M URLs/day)

Let us perform a complete back-of-the-envelope estimation for a URL shortening service
that creates 100 million shortened URLs per day.

### Requirements

```
  - 100M new URLs created per day
  - Read:Write ratio = 100:1 (URLs are clicked far more than created)
  - URLs are retained for 5 years
  - Average long URL length: 200 characters (200 bytes)
  - Short URL length: 7 characters (7 bytes)
  - Redirect latency: < 50ms (p99)
  - Availability: 99.99%
```

### Traffic Estimation

```
  WRITES (URL creation):
    Write QPS = 100M / 86,400 ~ 1,157 QPS
    Peak Write QPS = 1,157 x 3 ~ 3,472 QPS
  
  READS (URL redirects):
    Read:Write = 100:1
    Read QPS = 1,157 x 100 = 115,741 QPS ~ 116K QPS
    Peak Read QPS = 116K x 3 ~ 348K QPS
```

### Storage Estimation

```
  Per-record storage:
  +---------------------------+--------+
  | Field                     | Size   |
  +---------------------------+--------+
  | short_url (7 chars)       | 7 B    |
  | long_url (avg 200 chars)  | 200 B  |
  | user_id                   | 16 B   |
  | created_at                | 8 B    |
  | expires_at                | 8 B    |
  | click_count               | 4 B    |
  +---------------------------+--------+
  | TOTAL                     | ~243 B |
  +---------------------------+--------+
  Round up to 300 bytes per record.
  
  Daily storage:
    100M x 300 B = 30 GB/day
  
  Yearly storage:
    30 GB x 365 = ~11 TB/year
  
  5-year storage:
    11 TB x 5 = 55 TB
  
  With 3x replication:
    55 TB x 3 = 165 TB
  
  Total unique URLs over 5 years:
    100M/day x 365 x 5 = 182.5 Billion URLs
    ~ 183 Billion URLs (need a key space that supports this)
  
  Short URL key space check:
    Using base62 (a-z, A-Z, 0-9), 7 characters:
    62^7 = 3.5 Trillion possible URLs
    183B << 3.5T --- more than enough headroom!
```

### Bandwidth Estimation

```
  Ingress (writes):
    Peak: 3,472 QPS x 300 B = ~1 MB/s (negligible)
  
  Egress (redirects):
    Each redirect response is small (~500 bytes with headers)
    Peak: 348K QPS x 500 B = 174 MB/s
```

### Cache Estimation

```
  Apply 80/20 rule to reads:
    Daily unique URL accesses: assume 20% of 183B total URLs are "hot"
    
  Alternative: Cache the hot URLs for the day
    Daily unique reads: 116K QPS x 86,400 ~ 10B redirect requests
    If 20% of URLs are accessed in a day: ~20M x 365 = 7.3B unique URLs/year
    
  Simpler approach:
    Cache top 20% of daily traffic:
    10B reads/day x 0.20 x 300 B / unique-factor
    
  Practical: Cache ~40M hot URLs
    40M x 300 B = 12 GB
    
  12 GB easily fits in a single Redis instance!
```

### Server Estimation

```
  Read operations (cache-backed redirect lookups):
    Peak Read QPS: 348K
    QPS per server: 3,000 (cache-backed)
    Servers needed: 348K / 3,000 = 116 servers
  
  With safety margin (1.3x): ~150 application servers
```

### Summary Table

```
  +----------------------------+-------------------+
  |  Metric                    |  Estimate         |
  +----------------------------+-------------------+
  |  Write QPS (avg / peak)    |  1.2K / 3.5K      |
  |  Read QPS (avg / peak)     |  116K / 348K      |
  |  Storage (5 years, 3x rep) |  165 TB           |
  |  Bandwidth (peak egress)   |  174 MB/s         |
  |  Cache memory              |  12 GB            |
  |  Application servers       |  ~150             |
  |  Short URL key space       |  62^7 = 3.5T      |
  +----------------------------+-------------------+
```

---

## 4.10 Worked Example: Chat Application (50M DAU)

### Requirements

```
  - 50M Daily Active Users
  - Each user sends 40 messages/day on average
  - Average message size: 200 bytes
  - 1:1 and group chats (avg group size: 10 members)
  - Messages retained forever
  - Delivery latency: < 500ms
  - Message delivery guarantee: at-least-once
  - Online/offline status tracking
```

### Traffic Estimation

```
  Total messages per day:
    50M users x 40 messages = 2 Billion messages/day
  
  Message Write QPS:
    2B / 86,400 = ~23,148 QPS
    Peak: 23,148 x 3 = ~70K QPS
  
  Message Read QPS:
    Each message is read by recipient(s).
    For 1:1 chats: each message is read once = same as write QPS
    For group chats (assume 30% of messages are in groups, avg 10 members):
      Group read amplification: 2B x 0.30 x 10 = 6B + 2B x 0.70 = 7.4B reads/day
    
    Read QPS: 7.4B / 86,400 = ~85,648 QPS
    Peak Read QPS: ~257K QPS
  
  Connection management (WebSockets):
    50M DAU, assume 30% online at peak = 15M concurrent connections
```

### Storage Estimation

```
  Per-message storage:
  +---------------------------+--------+
  | Field                     | Size   |
  +---------------------------+--------+
  | message_id (UUID)         | 16 B   |
  | sender_id (UUID)          | 16 B   |
  | chat_id (UUID)            | 16 B   |
  | content (avg 200 chars)   | 200 B  |
  | timestamp                 | 8 B    |
  | status (sent/delivered/   | 1 B    |
  |         read)             |        |
  | metadata                  | ~50 B  |
  +---------------------------+--------+
  | TOTAL                     | ~307 B |
  +---------------------------+--------+
  Round up to 350 bytes per message.
  
  Daily storage:
    2B messages x 350 B = 700 GB/day
  
  Yearly storage:
    700 GB x 365 = ~255 TB/year
  
  5-year storage (with 3x replication):
    255 TB x 5 x 3 = 3,825 TB ~ 3.8 PB
```

### Bandwidth Estimation

```
  Ingress (message sends):
    Peak: 70K QPS x 350 B = ~24.5 MB/s
  
  Egress (message delivery):
    Peak: 257K QPS x 350 B = ~90 MB/s
  
  WebSocket overhead:
    15M connections x heartbeat every 30s x 64 B
    = 15M / 30 x 64 B = 32 MB/s for heartbeats alone
  
  Total peak egress: ~122 MB/s
```

### Server Estimation

```
  WebSocket servers:
    Each server handles ~50,000 concurrent connections (typical)
    15M connections / 50,000 = 300 WebSocket servers
  
  API servers (message processing):
    Peak QPS: 70K (writes) + 257K (reads) = 327K total
    QPS per server: 1,000 (message processing involves DB writes)
    Servers: 327K / 1,000 = 327 ~ 330 API servers
  
  Total: ~300 WebSocket + ~330 API = ~630 servers
  With redundancy: ~700 servers
```

### Cache Estimation

```
  Cache recent messages (last 30 days):
    Daily new data: 700 GB
    30 days: 700 GB x 30 = 21 TB
    Cache 20%: 21 TB x 0.20 = 4.2 TB
    
  This requires a Redis cluster with ~4.2 TB capacity.
  With ~64 GB per Redis node: 4,200 / 64 = ~66 Redis nodes
  
  Alternatively, cache only last 24 hours (most active chats):
    700 GB x 0.20 = 140 GB
    This is much more manageable: 3-4 Redis nodes
```

### Summary Table

```
  +-------------------------------------+-------------------+
  |  Metric                             |  Estimate         |
  +-------------------------------------+-------------------+
  |  Messages per day                   |  2 Billion        |
  |  Write QPS (avg / peak)             |  23K / 70K        |
  |  Read QPS (avg / peak)              |  86K / 257K       |
  |  Concurrent WebSocket connections   |  15M (peak)       |
  |  Storage per year (with replication)|  765 TB           |
  |  Peak bandwidth (egress)            |  122 MB/s         |
  |  WebSocket servers                  |  ~300             |
  |  API servers                        |  ~330             |
  |  Cache (last 24h, 20%)              |  140 GB           |
  |  Redis nodes (for cache)            |  3-4              |
  +-------------------------------------+-------------------+
```

---

## 4.11 Common Pitfalls in Estimation

### Pitfall 1: Forgetting the Peak Multiplier

Average QPS is not what you design for. Peak traffic (typically 2-5x average) determines
your infrastructure needs.

```
  WRONG:  "We need 1,000 QPS, so let's provision for 1,000 QPS"
  RIGHT:  "Average is 1,000 QPS, peak is ~3,000 QPS, design for 4,500 QPS
           (3x peak + 50% safety margin)"
```

### Pitfall 2: Ignoring Replication

Storage estimates must account for replication. Most databases replicate data 3x:

```
  Raw data:        100 TB
  With replication: 300 TB  <-- This is what you actually need
```

### Pitfall 3: Confusing Bits and Bytes

Network bandwidth is often measured in **bits** per second, while storage is in **bytes**.
There are 8 bits in a byte.

```
  "100 Mbps network" = 100 Megabits/s = 12.5 Megabytes/s
  
  A 1 Gbps connection can transfer ~125 MB/s (not 1 GB/s!)
```

### Pitfall 4: Not Accounting for Metadata and Indexing Overhead

Raw data size is not the only storage cost. Indexes, metadata, and file system overhead
typically add 20-50% to the raw data size:

```
  Raw data:           100 TB
  Index overhead:     +30%  = 30 TB
  Metadata:           +10%  = 10 TB
  File system overhead: +5% = 5 TB
  Actual storage needed: ~145 TB
  With replication (3x): ~435 TB
```

### Pitfall 5: Assuming Linear Growth

Real traffic grows exponentially, not linearly. If you have 1M users today and expect
10x growth in 2 years, your Year 2 requirements are 10x Year 1, not 2x.

```
  Year 1: 1M DAU      ->  100 QPS
  Year 2: 10M DAU     ->  1,000 QPS   (10x, not 200 QPS)
  Year 3: 50M DAU     ->  5,000 QPS   (50x Year 1)
```

### Pitfall 6: Over-Precise Estimates

Back-of-the-envelope means **rough**. Do not calculate to 5 decimal places:

```
  WRONG:  "We need exactly 34,722.222 QPS"
  RIGHT:  "We need ~35K QPS"
```

Round aggressively. If your estimate is 47 servers, call it 50. If storage is 73.2 TB,
call it 75 TB or even 100 TB. The goal is the right order of magnitude.

---

## 4.12 Estimation Cheat Sheet

Use this quick-reference during your HLD work:

```
+===================================================================+
||                  ESTIMATION CHEAT SHEET                         ||
+===================================================================+
||                                                                 ||
||  TIME CONVERSIONS:                                              ||
||    1 day    = 86,400 seconds   (~10^5)                          ||
||    1 month  = 2,500,000 seconds (~2.5 x 10^6)                  ||
||    1 year   = 31,500,000 seconds (~3 x 10^7)                   ||
||                                                                 ||
||  QUICK QPS:                                                     ||
||    1M req/day   = ~12 QPS                                       ||
||    10M req/day  = ~120 QPS                                      ||
||    100M req/day = ~1,200 QPS                                    ||
||    1B req/day   = ~12,000 QPS                                   ||
||                                                                 ||
||  STORAGE:                                                       ||
||    1 char = 1 byte  |  1 KB ~ 1,000 chars  |  1 MB ~ 1M chars  ||
||    1 photo ~ 200 KB |  1 min video ~ 10 MB                     ||
||                                                                 ||
||  SERVER CAPACITY (per standard server):                         ||
||    CPU-heavy:     100-500 QPS                                   ||
||    DB-backed:     500-1,000 QPS                                 ||
||    Cache-backed:  2,000-5,000 QPS                               ||
||    Static:        10,000-50,000 QPS                             ||
||                                                                 ||
||  WEBSOCKETS:                                                    ||
||    Per server:    10,000-65,000 concurrent connections          ||
||                                                                 ||
||  REDIS:                                                         ||
||    Single node:   Up to ~64 GB memory                           ||
||    QPS:           ~100,000 operations/sec per node              ||
||                                                                 ||
||  COMMON MULTIPLIERS:                                            ||
||    Peak:          2-5x average                                  ||
||    Replication:   3x raw storage                                ||
||    Cache 80/20:   Cache 20% of data to serve 80% of reads      ||
||    Safety margin: 1.5-2x peak                                   ||
||                                                                 ||
+===================================================================+
```

---

## 4.13 Key Takeaways

```
+-------------------------------------------------------------------+
|                        KEY TAKEAWAYS                              |
+-------------------------------------------------------------------+
|                                                                   |
|  1. Estimation is about being within an ORDER OF MAGNITUDE,       |
|     not about precision. Round aggressively.                      |
|                                                                   |
|  2. Memorize the powers of 2, seconds-in-a-day (~86,400), and    |
|     the latency numbers hierarchy (L1 -> RAM -> SSD -> HDD ->    |
|     Network).                                                     |
|                                                                   |
|  3. The standard estimation flow is:                              |
|     Users -> DAU -> QPS -> Peak QPS -> Design QPS                |
|     Then derive: Storage, Bandwidth, Cache, Servers              |
|                                                                   |
|  4. Always account for:                                           |
|     - Peak multiplier (2-5x average)                             |
|     - Replication factor (typically 3x)                          |
|     - Read/write ratio (determines optimization focus)           |
|     - Safety margin (1.5-2x for growth)                          |
|                                                                   |
|  5. The 80/20 rule for caching: cache the hottest 20% of data    |
|     to serve 80% of read requests from memory.                   |
|                                                                   |
|  6. Media (images, video) dominates storage. Separate media      |
|     storage estimates from metadata/text estimates.              |
|                                                                   |
|  7. Common pitfalls: forgetting peak QPS, ignoring replication,  |
|     confusing bits/bytes, over-precise calculations, and         |
|     assuming linear growth.                                      |
|                                                                   |
|  8. Practice estimation regularly. Work through examples like    |
|     URL shorteners, chat apps, social media, and video           |
|     streaming platforms until the process is second nature.      |
|                                                                   |
|  9. Estimation drives architecture: your numbers determine       |
|     whether you need 1 server or 1,000, whether a single        |
|     database suffices or you need sharding, and whether a        |
|     simple cache works or you need a distributed cluster.        |
|                                                                   |
+-------------------------------------------------------------------+
```

---

**You have completed the foundational chapters!** With an understanding of what HLD is
(Chapter 1), how to think architecturally (Chapter 2), how to gather requirements
(Chapter 3), and how to estimate capacity (Chapter 4), you are ready to dive into
designing specific systems in the chapters ahead.
