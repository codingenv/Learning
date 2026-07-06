# Chapter 7: Caching

## Introduction — Why Caching?

In distributed systems, the most expensive operations are **network calls** and **disk I/O**. A database query might take 5–50ms. A call to an external API might take 100–500ms. If your application serves 10,000 requests per second and each request hits the database, that's 10,000 database queries per second — a recipe for high latency and eventual database collapse.

**Caching** stores copies of frequently accessed data in a **faster storage layer** (usually memory) so that future requests can be served without hitting the slower backend. It's one of the most impactful performance optimizations in system design.

### The Numbers That Matter

```
+--------------------------------+------------------+
|        Operation               |  Approximate Time|
+--------------------------------+------------------+
| L1 cache reference             |        0.5 ns    |
| L2 cache reference             |          7 ns    |
| RAM reference                  |        100 ns    |
| SSD random read                |     16,000 ns    |
| HDD random read                |  2,000,000 ns    |
| Network round-trip (same DC)   |    500,000 ns    |
| Network round-trip (cross-DC)  | 150,000,000 ns   |
+--------------------------------+------------------+

  Memory is ~200x faster than SSD.
  SSD is ~125x faster than HDD.
  Local access is ~300x faster than cross-datacenter.
```

> **Rule of thumb:** If you can serve a response from memory instead of disk/database, you can improve latency by **10–100x** and dramatically reduce backend load.

---

## Cache Hit vs Cache Miss

Understanding these two fundamental concepts is essential:

```
  CACHE HIT (fast path)                    CACHE MISS (slow path)
  +---------+   1. Request  +-------+      +---------+   1. Request  +-------+
  |         +-------------->| Cache |      |         +-------------->| Cache |
  | Client  |               |       |      | Client  |               |       |
  |         |<--------------+ Found!|      |         |               | Not   |
  |         |   2. Response |       |      |         |               | Found |
  +---------+   (fast!)     +-------+      |         |   2. Fetch   +---+---+
                                           |         |   from DB        |
                                           |         |<---------+   +---v---+
                                           |         | 4. Return|   |  DB   |
                                           +---------+ response |   |       |
                                                     |          +---+       |
                                                     | 3. Store    +-------+
                                                     |    in cache
```

### Key Metrics

| Metric | Definition | Target |
|---|---|---|
| **Hit Rate** | % of requests served from cache | > 90% for most systems |
| **Miss Rate** | % of requests that must go to the backend | < 10% |
| **Hit Ratio** | hits / (hits + misses) | 0.9–0.99 for well-tuned caches |
| **Eviction Rate** | How often items are removed from cache | Should be stable, not spiking |
| **Latency (hit)** | Time to serve from cache | < 1ms (local), < 5ms (network cache) |
| **Latency (miss)** | Time to serve on cache miss | 5–100ms (depends on backend) |

> **Example:** If your cache hit rate is 95% and cache latency is 2ms while DB latency is 50ms, your average response time is: `0.95 * 2ms + 0.05 * 50ms = 1.9ms + 2.5ms = 4.4ms` — **11x faster** than always hitting the DB.

---

## Caching Strategies

Choosing the right caching strategy depends on your **read/write ratio**, **consistency requirements**, and **data access patterns**.

### 1. Cache-Aside (Lazy Loading)

The **most common** caching strategy. The application manages the cache explicitly.

```
  Read Path:                              Write Path:
  +--------+   1. Check   +-------+      +--------+  1. Write  +------+
  | App    +------------->| Cache |      | App    +----------->| DB   |
  | Server |              |       |      | Server |            |      |
  |        |  2a. HIT     |       |      |        | 2. Delete  +------+
  |        |<-------------+       |      |        +---> +-------+
  |        |              +---+---+      +--------+     | Cache |
  |        |  2b. MISS        |                         | (key) |
  |        |                  |                         +-------+
  |        |  3. Query  +-----v--+
  |        +----------->|   DB   |
  |        |<-----------+        |
  |        | 4. Response+--------+
  |        |
  |        | 5. Store in cache
  |        +-----------> +-------+
  +--------+             | Cache |
                         +-------+
```

**How it works:**
1. Application first checks the cache
2. **Cache hit:** Return cached data
3. **Cache miss:** Query the database, store result in cache, return to client
4. On **write:** Update database, then **invalidate** (delete) the cache entry

**Pros:**
- Only requested data is cached (no cache pollution)
- Cache failure doesn't break the system (just slower)
- Simple to implement

**Cons:**
- First request always misses (cold start penalty)
- Potential for stale data between write and cache invalidation
- Application must manage cache logic (two code paths)

### 2. Read-Through

Similar to Cache-Aside, but the **cache itself** is responsible for loading data from the database on a miss.

```
  +--------+   1. Read    +-------+   2. Cache miss:   +------+
  | App    +------------->| Cache |   auto-fetch from  | DB   |
  | Server |              |       +-------------------->|      |
  |        |<-------------+ 3. Return data             |      |
  |        | (from cache) | (cached for next time)     +------+
  +--------+              +-------+
```

**Pros:**
- Application code is cleaner — doesn't know about DB
- Cache handles data loading logic
- Consistent caching behavior

**Cons:**
- First read is still a miss
- Data model must match between cache and DB
- Less flexibility in how data is loaded

### 3. Write-Through

Every write goes to **both** the cache and the database **synchronously**. Data is always consistent between cache and DB.

```
  +--------+   1. Write   +-------+   2. Write to DB   +------+
  | App    +------------->| Cache +-------------------->| DB   |
  | Server |              |       |                     |      |
  |        |<-------------+ 3. Confirm (both written)   +------+
  +--------+              +-------+
```

**Pros:**
- Cache is always up-to-date — no stale data
- Reads after writes are guaranteed fresh
- Simple consistency model

**Cons:**
- **Higher write latency** — every write hits both cache and DB
- Cache may fill with data that's rarely read (cache pollution)
- Wastes cache space on infrequently accessed data

### 4. Write-Behind (Write-Back)

Writes go to the **cache first**, and the cache **asynchronously** flushes writes to the database in the background.

```
  +--------+   1. Write   +-------+    3. Async batch    +------+
  | App    +------------->| Cache |    write to DB        | DB   |
  | Server |              |       +- - - - - - - - - - -->|      |
  |        |<-------------+ 2. ACK |   (later, in bulk)   |      |
  +--------+   (fast!)    +-------+                       +------+
```

**Pros:**
- **Extremely fast writes** — only writes to memory
- Reduces database load — batches writes
- Absorbs traffic spikes

**Cons:**
- **Risk of data loss** — if cache crashes before flushing to DB
- Complexity — need to handle failures, retries, ordering
- Eventual consistency between cache and DB

### 5. Write-Around

Writes go **directly to the database**, bypassing the cache. The cache is only populated on reads (via Cache-Aside or Read-Through).

```
  Write Path:                            Read Path:
  +--------+   1. Write   +------+      +--------+   1. Read   +-------+
  | App    +------------->| DB   |      | App    +------------>| Cache |
  | Server |              |      |      | Server |             |       |
  |        |<-------------+      |      |        | 2. MISS     +---+---+
  +--------+              +------+      |        +------------>| DB |
                                        |        |<------------+    |
                                        |        | 3. Populate +----+
                                        |        +---> +-------+
                                        +--------+     | Cache |
                                                       +-------+
```

**Pros:**
- Cache not polluted with write-heavy data
- Good when writes are much more frequent than reads for the same key

**Cons:**
- Higher read latency on cache misses after writes
- Recently written data isn't immediately available in cache

### Strategy Comparison Table

| Strategy | Read Latency | Write Latency | Consistency | Data Loss Risk | Best For |
|---|---|---|---|---|---|
| **Cache-Aside** | Miss penalty on first read | DB write speed | Eventual | None | General purpose, read-heavy |
| **Read-Through** | Miss penalty on first read | DB write speed | Eventual | None | Clean API, read-heavy |
| **Write-Through** | Always fast (cache hit) | Slower (double write) | Strong | None | Read-after-write consistency |
| **Write-Behind** | Always fast (cache hit) | Very fast (cache only) | Eventual | **Yes** (cache crash) | Write-heavy, high throughput |
| **Write-Around** | Miss penalty after writes | DB write speed | Eventual | None | Write-heavy, rarely re-read |

---

## Cache Eviction Policies

Caches have **limited memory**. When the cache is full and a new item needs to be added, an eviction policy decides which existing item to remove.

### Common Eviction Policies

| Policy | Full Name | How It Works | Best For |
|---|---|---|---|
| **LRU** | Least Recently Used | Evicts the item that hasn't been accessed for the longest time | General purpose (most popular) |
| **LFU** | Least Frequently Used | Evicts the item with the fewest accesses | Frequency-based access patterns |
| **FIFO** | First In, First Out | Evicts the oldest item | Simple, time-ordered data |
| **TTL** | Time-To-Live | Items expire after a set duration | Data with known freshness |
| **Random** | Random Eviction | Randomly removes an item | Simple, surprisingly effective |
| **LRU-K** | LRU with K references | Considers the Kth-to-last access time | Resist scan pollution |

### LRU — The Default Choice

LRU is the most widely used eviction policy. It's based on the principle of **temporal locality**: recently accessed items are likely to be accessed again soon.

```
  Cache (capacity: 4)
  
  Access: A, B, C, D, E (E causes eviction)
  
  State after A:  [A]
  State after B:  [B, A]
  State after C:  [C, B, A]
  State after D:  [D, C, B, A]   <- cache full
  State after E:  [E, D, C, B]   <- A evicted (least recently used)
  Access A again: [A, E, D, C]   <- B evicted, A is now most recent
```

**Implementation:** LRU is typically implemented with a **HashMap + Doubly Linked List** for O(1) get and put operations.

### LFU — Frequency-Based

LFU keeps track of how many times each item is accessed and evicts the least frequently used item.

```
  Cache (capacity: 3)
  
  Item A: accessed 100 times
  Item B: accessed 5 times    <- evicted first (lowest frequency)
  Item C: accessed 50 times
```

**Problem:** "Cache pollution" — an item accessed many times in the past but no longer relevant stays in cache forever. Solution: **Windowed LFU** — only count accesses within a time window.

---

## Cache Levels

Caching happens at **multiple levels** in a system. Each level trades off capacity, latency, and complexity.

```
  +------------------------------------------------------------+
  |                     CACHING LAYERS                         |
  +------------------------------------------------------------+
  |                                                            |
  |  +------------------+  Fastest, smallest                   |
  |  | 1. Browser Cache |  (HTTP cache headers, localStorage)  |
  |  +--------+---------+                                      |
  |           |                                                |
  |  +--------v---------+  Edge caching, globally distributed  |
  |  | 2. CDN Cache     |  (CloudFront, Cloudflare, Akamai)   |
  |  +--------+---------+                                      |
  |           |                                                |
  |  +--------v---------+  Reverse proxy cache                 |
  |  | 3. API Gateway / |  (Nginx, Varnish, Kong)              |
  |  |    Reverse Proxy |                                      |
  |  +--------+---------+                                      |
  |           |                                                |
  |  +--------v---------+  Application-level cache             |
  |  | 4. Application   |  (In-process: HashMap, Guava)        |
  |  |    Cache         |  (Distributed: Redis, Memcached)     |
  |  +--------+---------+                                      |
  |           |                                                |
  |  +--------v---------+  Database's internal cache           |
  |  | 5. Database      |  (MySQL query cache, PG buffer pool) |
  |  |    Query Cache   |                                      |
  |  +--------+---------+                                      |
  |           |                                                |
  |  +--------v---------+  OS and hardware caching             |
  |  | 6. OS / Disk     |  (page cache, SSD cache, CPU cache)  |
  |  |    Cache         |                                      |
  |  +------------------+  Slowest, largest                    |
  |                                                            |
  +------------------------------------------------------------+
```

### Browser Cache

Controlled via HTTP cache headers:
- **`Cache-Control: max-age=3600`** — Cache for 1 hour
- **`ETag`** — Conditional request, server returns 304 Not Modified if unchanged
- **`Last-Modified`** — Similar to ETag but date-based

### CDN Cache

CDNs cache static and dynamic content at edge locations worldwide:
- **Static:** Images, CSS, JS, videos (high TTL, e.g., 1 year with cache-busting via URL versioning)
- **Dynamic:** API responses (low TTL, e.g., 5–60 seconds)
- **Cache key:** Usually URL + query string + selected headers

### Application-Level Cache

Two flavors:
1. **In-process cache** — HashMap, ConcurrentHashMap, Guava Cache, Caffeine (Java)
   - Ultra-fast (nanoseconds), but limited to a single process and lost on restart
2. **Distributed cache** — Redis, Memcached
   - Shared across all application instances, survives restarts, but adds network latency

---

## Distributed Caching: Redis vs Memcached

Both are in-memory key-value stores used as application-level caches, but they have significant differences.

### Detailed Comparison

| Feature | Redis | Memcached |
|---|---|---|
| **Data structures** | Strings, Lists, Sets, Sorted Sets, Hashes, Streams, Bitmaps, HyperLogLog | Simple key-value (strings only) |
| **Persistence** | RDB snapshots, AOF log | None (pure cache) |
| **Replication** | Master-replica with automatic failover (Redis Sentinel / Cluster) | None built-in |
| **Clustering** | Redis Cluster (auto-sharding) | Client-side sharding only |
| **Max value size** | 512 MB | 1 MB (default, configurable) |
| **Threading** | Single-threaded (event loop) + I/O threads in 6.0+ | Multi-threaded |
| **Memory efficiency** | Higher overhead due to data structures | More memory-efficient for simple strings |
| **Pub/Sub** | Yes | No |
| **Lua scripting** | Yes | No |
| **TTL granularity** | Per-key, millisecond precision | Per-key, second precision |
| **Use cases** | Session store, leaderboards, rate limiting, queues, caching | Pure caching, simple key-value |
| **Managed services** | AWS ElastiCache (Redis), Azure Cache for Redis | AWS ElastiCache (Memcached) |

### When to Choose Which?

- **Choose Redis** when you need data structures beyond simple strings, persistence, replication, pub/sub, or Lua scripting
- **Choose Memcached** when you need a simple, multi-threaded cache with maximum memory efficiency for string key-value pairs

> **Industry trend:** Redis has largely won the "Redis vs Memcached" debate. Most new systems choose Redis due to its versatility. Memcached is still used in legacy systems and specific use cases where its multi-threaded nature provides a performance edge.

---

## Redis — Deep Dive

Redis (Remote Dictionary Server) is far more than a simple cache. It is an **in-memory data
structure store** that can serve as a cache, database, message broker, streaming engine, and
more. Understanding Redis deeply is essential for system design.

### Why Redis Is So Fast

```
  Redis processes ~100,000 to 1,000,000 operations per second on a single node.
  How? Every design decision optimizes for speed:
  
  1. IN-MEMORY STORAGE
     +----------------------------------------------------------+
     | All data lives in RAM                                    |
     | RAM access: ~100 nanoseconds                              |
     | SSD access: ~16,000 nanoseconds (160x slower)            |
     | Network to DB: ~500,000 nanoseconds (5000x slower)       |
     +----------------------------------------------------------+
  
  2. SINGLE-THREADED EVENT LOOP
     +----------------------------------------------------------+
     | One thread handles ALL commands sequentially              |
     | No locks, no context switching, no thread synchronization |
     |                                                           |
     |  Event Loop:                                              |
     |  while (true) {                                           |
     |    events = poll(all_client_sockets)  // epoll/kqueue     |
     |    for event in events:                                   |
     |      read_command(event.socket)                           |
     |      execute_command()    // O(1) for most operations     |
     |      write_response(event.socket)                         |
     |  }                                                        |
     |                                                           |
     | Why single-threaded is FASTER for Redis:                  |
     | - No mutex/lock overhead                                  |
     | - No CPU cache invalidation from multi-threading          |
     | - Commands are so fast (~1 microsecond) that one core     |
     |   can handle 100K+ commands/sec easily                    |
     | - Bottleneck is NETWORK, not CPU                          |
     +----------------------------------------------------------+
     
     Note: Redis 6.0+ uses I/O threads for network read/write
     (parsing requests, sending responses) but command execution
     is still single-threaded. This improves throughput by 2x.
  
  3. EFFICIENT DATA STRUCTURES
     +----------------------------------------------------------+
     | Redis uses specialized C data structures:                 |
     | - SDS (Simple Dynamic Strings) — O(1) length, safe       |
     | - Ziplist — compact encoding for small lists/hashes       |
     | - Intset — compact encoding for small integer sets        |
     | - Skiplist — O(log n) sorted set operations               |
     | - Hashtable — O(1) lookups for large datasets             |
     | These auto-convert based on data size for optimal         |
     | memory vs speed trade-off.                                |
     +----------------------------------------------------------+
  
  4. NON-BLOCKING I/O (epoll/kqueue)
     +----------------------------------------------------------+
     | Uses OS-level multiplexing (epoll on Linux, kqueue on     |
     | macOS) to handle thousands of connections on one thread.  |
     | Each client connection is a file descriptor; epoll        |
     | notifies Redis when data is ready to read.                |
     +----------------------------------------------------------+
```

### Redis Data Structures — Complete Guide

Redis provides **10+ data structures**, each optimized for specific use cases. This is what
makes Redis far more powerful than a simple key-value cache.

#### 1. Strings — The Foundation

The simplest data type. A string value can hold text, numbers, or binary data (up to 512 MB).

```
  Commands:
  SET user:1001:name "Alice"                    # Store a string
  GET user:1001:name                            # -> "Alice"
  SET counter 0                                 # Numeric string
  INCR counter                                  # -> 1 (atomic increment)
  INCRBY counter 10                             # -> 11
  DECR counter                                  # -> 10
  MSET k1 "v1" k2 "v2" k3 "v3"                 # Set multiple keys at once
  MGET k1 k2 k3                                # Get multiple keys at once
  SETEX session:abc123 3600 '{"user":"alice"}'  # Set with 1 hour TTL
  SETNX lock:order:42 "worker-1"               # Set only if NOT exists (for locking)
  
  Real-world use cases:
  +----------------------------------------------------------+
  | Use Case          | Example                               |
  +----------------------------------------------------------+
  | Simple caching     | Cache DB query results as JSON strings|
  | Counters          | Page views, API call counts            |
  | Rate limiting     | INCR + EXPIRE for sliding window       |
  | Session storage   | SETEX with JSON payload and TTL        |
  | Distributed locks | SETNX + EXPIRE (SET NX EX pattern)     |
  | Feature flags     | SET feature:dark_mode "enabled"        |
  +----------------------------------------------------------+
```

#### 2. Lists — Ordered Sequences

Doubly-linked lists of strings. Support push/pop from both ends (O(1)).

```
  Commands:
  LPUSH notifications:user:42 "New message from Bob"     # Push to head
  LPUSH notifications:user:42 "Order shipped"             # Push to head
  RPUSH notifications:user:42 "Sale: 50% off"             # Push to tail
  LRANGE notifications:user:42 0 9                        # Get first 10 items
  LPOP notifications:user:42                               # Pop from head
  RPOP notifications:user:42                               # Pop from tail
  LLEN notifications:user:42                               # Get list length
  BRPOP queue:tasks 30                                     # Blocking pop (wait 30s)
  
  Internal encoding:
  - Small lists (< 128 elements, each < 64 bytes): ZIPLIST (compact, contiguous)
  - Large lists: QUICKLIST (linked list of ziplists)
  
  Real-world use cases:
  +----------------------------------------------------------+
  | Use Case              | How                               |
  +----------------------------------------------------------+
  | Activity feed/timeline| LPUSH new events, LRANGE to read  |
  | Message queue (simple)| LPUSH to enqueue, BRPOP to dequeue|
  | Recent items          | LPUSH + LTRIM to keep last N items |
  | Undo/redo stack       | LPUSH to add, LPOP to undo        |
  +----------------------------------------------------------+
  
  Example: Keep last 100 notifications
  LPUSH notifications:user:42 "new event"
  LTRIM notifications:user:42 0 99      # Keep only first 100
```

#### 3. Sets — Unique Unordered Collections

Unordered collection of unique strings. Supports set operations (union, intersection, diff).

```
  Commands:
  SADD tags:article:101 "redis" "caching" "performance"  # Add members
  SMEMBERS tags:article:101                                # Get all members
  SISMEMBER tags:article:101 "redis"                       # -> 1 (true)
  SCARD tags:article:101                                   # -> 3 (count)
  SINTER tags:article:101 tags:article:102                 # Intersection
  SUNION tags:article:101 tags:article:102                 # Union
  SDIFF tags:article:101 tags:article:102                  # Difference
  SRANDMEMBER tags:article:101 3                           # 3 random members
  
  Real-world use cases:
  +----------------------------------------------------------+
  | Use Case              | How                               |
  +----------------------------------------------------------+
  | Tags / categories     | SADD tags per item, SINTER to find|
  |                       | items with common tags             |
  | Unique visitors       | SADD daily:visitors:2024-01-15    |
  |                       | "user:42" -> SCARD for count       |
  | Friend lists          | SADD friends:alice "bob" "charlie" |
  |                       | SINTER friends:alice friends:bob   |
  |                       | -> mutual friends!                 |
  | Deduplication         | SISMEMBER to check before process  |
  +----------------------------------------------------------+
```

#### 4. Sorted Sets (ZSets) — The Power Data Structure

Like Sets, but every member has a **score** (float). Members are automatically sorted by score.
This is Redis's most powerful and versatile data structure.

```
  Commands:
  ZADD leaderboard 1500 "alice"     # Add alice with score 1500
  ZADD leaderboard 2300 "bob"       # Add bob with score 2300
  ZADD leaderboard 1800 "charlie"   # Add charlie with score 1800
  ZRANGE leaderboard 0 -1 WITHSCORES    # All members, lowest first
  ZREVRANGE leaderboard 0 2 WITHSCORES  # Top 3, highest first
  -> "bob" 2300, "charlie" 1800, "alice" 1500
  ZRANK leaderboard "alice"          # -> 0 (rank, 0-indexed, lowest score)
  ZREVRANK leaderboard "bob"         # -> 0 (rank by highest score)
  ZSCORE leaderboard "alice"         # -> 1500
  ZINCRBY leaderboard 500 "alice"    # -> 2000 (atomic score increment)
  ZRANGEBYSCORE leaderboard 1000 2000  # Members with score between 1000-2000
  ZCOUNT leaderboard 1000 2000        # Count members in score range
  
  Internal encoding:
  - Small sorted sets: ZIPLIST
  - Large sorted sets: SKIPLIST + HASHTABLE
    -> Skiplist gives O(log n) range queries
    -> Hashtable gives O(1) score lookups by member
  
  Real-world use cases:
  +------------------------------------------------------------------+
  | Use Case                    | How                                 |
  +------------------------------------------------------------------+
  | Leaderboard / Rankings      | ZADD score, ZREVRANGE for top N     |
  | Priority queue              | ZADD with priority as score,        |
  |                             | ZPOPMIN to get highest priority     |
  | Rate limiting (sliding win.)| ZADD with timestamp as score,       |
  |                             | ZRANGEBYSCORE + ZREMRANGEBYSCORE    |
  | Delayed job scheduling      | ZADD with execution_time as score,  |
  |                             | ZRANGEBYSCORE 0 now to get due jobs |
  | Real-time trending topics   | ZINCRBY for each mention,           |
  |                             | ZREVRANGE for top trending          |
  | Timeline / feed             | ZADD with timestamp as score,       |
  |                             | ZREVRANGE for latest posts          |
  +------------------------------------------------------------------+
  
  Example: Real-time Gaming Leaderboard
  ZADD game:leaderboard 15000 "player:alice"
  ZADD game:leaderboard 23000 "player:bob"
  ZINCRBY game:leaderboard 500 "player:alice"    # Alice scores 500 more
  ZREVRANGE game:leaderboard 0 9 WITHSCORES      # Top 10 players
  ZREVRANK game:leaderboard "player:alice"        # Alice's rank
```

#### 5. Hashes — Field-Value Maps

A hash is a map of field-value pairs — like a mini key-value store inside a key. Perfect for
representing objects.

```
  Commands:
  HSET user:1001 name "Alice" email "alice@e.com" age 30 city "NYC"
  HGET user:1001 name                   # -> "Alice"
  HGETALL user:1001                     # -> all fields and values
  HMGET user:1001 name email            # -> "Alice", "alice@e.com"
  HINCRBY user:1001 age 1              # -> 31 (atomic increment)
  HDEL user:1001 city                   # Delete a field
  HEXISTS user:1001 email               # -> 1 (true)
  HLEN user:1001                        # -> 3 (number of fields)
  
  Why Hashes instead of Strings?
  
  BAD (separate keys per field):
  SET user:1001:name "Alice"           # 1 key
  SET user:1001:email "alice@e.com"    # 1 key
  SET user:1001:age "30"               # 1 key
  -> 3 keys, 3 network round trips to get all, more memory overhead
  
  GOOD (single hash):
  HSET user:1001 name "Alice" email "alice@e.com" age 30
  HGETALL user:1001
  -> 1 key, 1 network round trip, MUCH less memory overhead
  
  Memory savings: For small hashes (< 128 fields), Redis uses ZIPLIST
  encoding which is extremely compact. 100 hashes with 10 fields each
  use ~10x LESS memory than 1000 individual string keys.
  
  Real-world use cases:
  +----------------------------------------------------------+
  | Use Case              | How                               |
  +----------------------------------------------------------+
  | User profiles         | HSET user:{id} field value        |
  | Product details       | HSET product:{id} name price desc |
  | Session data          | HSET session:{id} user_id cart ...|
  | Configuration         | HSET config max_retries 3 timeout |
  | Shopping cart          | HSET cart:{uid} product_id qty    |
  +----------------------------------------------------------+
```

#### 6. Streams — Append-Only Log (Redis 5.0+)

An append-only log data structure designed for **event streaming** and **message processing.**
Streams are Redis's answer to Kafka-like functionality.

```
  Commands:
  XADD events * sensor_id "temp-1" value "72.5" unit "F"
  -> "1609459200000-0" (auto-generated ID: timestamp-sequence)
  
  XADD events * sensor_id "temp-2" value "68.3" unit "F"
  -> "1609459200001-0"
  
  XLEN events                          # -> 2
  XRANGE events - +                    # All entries (oldest to newest)
  XRANGE events 1609459200000 +        # Entries from timestamp onward
  XREAD COUNT 10 BLOCK 5000 STREAMS events $
  # Read new entries, block up to 5 seconds if none available
  
  Consumer Groups (like Kafka consumer groups):
  XGROUP CREATE events mygroup $ MKSTREAM
  XREADGROUP GROUP mygroup consumer-1 COUNT 10 BLOCK 5000 STREAMS events >
  XACK events mygroup "1609459200000-0"     # Acknowledge processing
  
  How Streams differ from Lists:
  +------------------+--------------------------+--------------------------+
  | Feature          | List                     | Stream                   |
  +------------------+--------------------------+--------------------------+
  | Ordering         | By insertion position    | By timestamp ID          |
  | Consumer groups  | No                       | Yes (like Kafka)         |
  | Acknowledgment   | No (pop = consumed)      | Yes (XACK)               |
  | Re-reading       | No (popped items gone)   | Yes (items persist)      |
  | Trimming         | Manual (LTRIM)           | MAXLEN or MINID auto-trim|
  | Blocking read    | BRPOP (one consumer)     | XREADGROUP (many)        |
  +------------------+--------------------------+--------------------------+
  
  Real-world use cases:
  - Event sourcing (immutable event log)
  - IoT sensor data ingestion
  - Activity feeds and audit logs
  - Lightweight message queue (when Kafka is overkill)
```

#### 7. HyperLogLog — Probabilistic Counting

Counts **approximate unique elements** using only ~12 KB of memory regardless of how many
elements are added. Standard error rate: 0.81%.

```
  Commands:
  PFADD daily:visitors:2024-01-15 "user:42"
  PFADD daily:visitors:2024-01-15 "user:99"
  PFADD daily:visitors:2024-01-15 "user:42"    # Duplicate, not counted
  PFCOUNT daily:visitors:2024-01-15             # -> 2 (approximately)
  
  PFMERGE weekly:visitors daily:visitors:2024-01-15 daily:visitors:2024-01-16
  # Merge multiple HLLs into one
  
  Why not use a Set?
  SET with 10 million unique visitors = ~400 MB memory
  HyperLogLog with 10 million unique visitors = 12 KB memory!
  (33,000x less memory, with only ~0.81% error)
  
  Real-world use cases:
  - Unique visitor counting (web analytics)
  - Unique search query counting
  - Unique IP addresses per day
  - Cardinality estimation for any large dataset
```

#### 8. Bitmaps — Bit-Level Operations

Strings treated as arrays of bits. Extremely memory-efficient for boolean data.

```
  Commands:
  SETBIT user:1001:logins 0 1         # Day 0: logged in
  SETBIT user:1001:logins 1 0         # Day 1: did not log in
  SETBIT user:1001:logins 2 1         # Day 2: logged in
  GETBIT user:1001:logins 2           # -> 1
  BITCOUNT user:1001:logins           # -> 2 (days logged in)
  
  BITOP AND result key1 key2          # Bitwise AND across keys
  BITOP OR result key1 key2           # Bitwise OR
  
  Memory: Tracking 365 days of login for 1 user = 46 bytes!
  Tracking 365 days for 10 million users = 460 MB (vs. 36 GB with sets)
  
  Real-world use cases:
  - Daily active users (1 bit per user per day)
  - Feature flags (1 bit per user per feature)
  - Online/offline status (1 bit per user)
  - Bloom filters (Redis has RedisBloom module)
```

#### Data Structures Summary

```
  +----------------+------------------+---------------------+------------------+
  | Data Structure | Time Complexity  | Memory Efficiency   | Best Use Case    |
  +----------------+------------------+---------------------+------------------+
  | String         | O(1) get/set     | Moderate            | Simple caching   |
  | List           | O(1) push/pop    | Good (quicklist)    | Queues, feeds    |
  | Set            | O(1) add/check   | Good (intset)       | Tags, uniqueness |
  | Sorted Set     | O(log n) add/rank| Moderate            | Leaderboards     |
  | Hash           | O(1) per field   | Excellent (ziplist) | Objects, profiles|
  | Stream         | O(1) append      | Good                | Event logs       |
  | HyperLogLog    | O(1)             | Excellent (12 KB)   | Unique counting  |
  | Bitmap         | O(1) per bit     | Excellent           | Boolean arrays   |
  +----------------+------------------+---------------------+------------------+
```

---

### Redis Persistence — Surviving Restarts

Redis is an in-memory store, but it offers persistence to survive restarts and crashes.

#### RDB (Redis Database) Snapshots

```
  RDB creates point-in-time snapshots of the entire dataset at configured intervals.
  
  Configuration (redis.conf):
    save 900 1       # Snapshot if >= 1 key changed in 900 seconds (15 min)
    save 300 10      # Snapshot if >= 10 keys changed in 300 seconds (5 min)
    save 60 10000    # Snapshot if >= 10000 keys changed in 60 seconds (1 min)
  
  How it works:
  1. Redis forks a child process (copy-on-write via OS fork())
  2. Child process writes entire dataset to a .rdb file on disk
  3. Parent process continues serving requests (unblocked!)
  4. When child finishes, it replaces the old .rdb file atomically
  
  Timeline:
  T=0        T=5min      T=10min     T=15min
  |           |           |           |
  [data]     [SNAPSHOT]  [data]     [SNAPSHOT]
              |                       |
              v                       v
           dump.rdb              dump.rdb (updated)
  
  Pros:
  + Compact single file (easy to backup, transfer)
  + Fast restarts (load .rdb file directly into memory)
  + No write amplification (single file, not per-operation)
  + Fork is fast (copy-on-write; parent doesn't pause)
  
  Cons:
  - DATA LOSS: Up to N minutes of data lost between snapshots
    (if crash happens between snapshots, all writes since last snapshot are lost)
  - Fork can be slow with large datasets (10+ GB = seconds to fork)
```

#### AOF (Append-Only File)

```
  AOF logs every write command to a file. On restart, Redis replays the log.
  
  Configuration:
    appendonly yes
    appendfsync always     # fsync after EVERY command (safest, slowest)
    appendfsync everysec   # fsync every second (good balance, DEFAULT)
    appendfsync no         # OS decides when to fsync (fastest, risky)
  
  How it works:
  
  Client commands:              AOF File (append-only):
  SET user:1 "Alice"    --->   *3\r\n$3\r\nSET\r\n$6\r\nuser:1\r\n$5\r\nAlice
  INCR counter           --->   *2\r\n$4\r\nINCR\r\n$7\r\ncounter
  LPUSH list "a"         --->   *3\r\n$5\r\nLPUSH\r\n$4\r\nlist\r\n$1\r\na
  
  AOF Rewrite (compaction):
  Over time, AOF grows large. Redis rewrites it by reading current state
  and generating the minimal set of commands to recreate it.
  
  Before rewrite (100,000 lines):
    SET counter 0
    INCR counter         (x 99,999 times)
  
  After rewrite (1 line):
    SET counter 99999
  
  Pros:
  + Minimal data loss (at most 1 second with appendfsync everysec)
  + Human-readable log (useful for debugging)
  + Auto-rewrite prevents unbounded growth
  
  Cons:
  - Larger file than RDB (text-based log of all commands)
  - Slower restart (must replay all commands)
  - Write amplification (every write = disk I/O)
```

#### Hybrid Persistence (RDB + AOF) — The Recommended Approach

```
  Redis 4.0+ supports hybrid persistence:
  
  aof-use-rdb-preamble yes
  
  How it works:
  1. AOF rewrite starts -> generates RDB snapshot as the PREAMBLE of the AOF file
  2. New commands after the rewrite are appended as normal AOF entries
  
  AOF File:
  +----------------------------------+
  | [RDB snapshot data]              |  <- Fast to load (binary format)
  | [AOF commands since snapshot]    |  <- Small, only recent changes
  +----------------------------------+
  
  Restart:
  1. Load RDB preamble (fast, bulk loading)
  2. Replay AOF tail (only a few seconds of commands)
  = FAST restart + MINIMAL data loss (best of both worlds)
  
  +------------------+----------+-----------+------------------+
  | Approach         | Data Loss| Restart   | Disk Usage       |
  +------------------+----------+-----------+------------------+
  | RDB only         | Up to    | Fast      | Small            |
  |                  | 15 min   |           |                  |
  | AOF only         | ~1 sec   | Slow      | Large            |
  | Hybrid (RDB+AOF) | ~1 sec   | Fast      | Medium           |
  | No persistence   | ALL data | N/A       | None (pure cache)|
  +------------------+----------+-----------+------------------+
  
  Recommendation:
  - Production cache with data you can afford to lose: RDB only
  - Production data store (sessions, queues): Hybrid (RDB + AOF)
  - Pure volatile cache (can repopulate from DB): No persistence
```

---

### Redis High Availability — Sentinel and Cluster

#### Redis Sentinel (HA without Sharding)

Redis Sentinel provides **automatic failover** — if the master node goes down, Sentinel promotes
a replica to master automatically.

```
  Architecture:
  
  +------------+     +------------+     +------------+
  | Sentinel 1 |     | Sentinel 2 |     | Sentinel 3 |
  | (monitor)  |     | (monitor)  |     | (monitor)  |
  +-----+------+     +------+-----+     +------+-----+
        |                   |                   |
        +---monitoring------+---monitoring------+
        |                   |                   |
  +-----v------+     +-----v------+     +------v-----+
  |   MASTER   |---->|  Replica 1 |     |  Replica 2 |
  | (read+write)|     | (read only)|     | (read only)|
  | 10.0.0.1   |     | 10.0.0.2   |     | 10.0.0.3   |
  +------------+     +------------+     +------------+
  
  Normal operation:
  - Writes go to Master
  - Reads can go to Master or Replicas (read scaling)
  - Sentinels continuously monitor Master health
  
  Failover (Master crashes):
  1. Sentinels detect Master is unreachable (configurable threshold)
  2. Sentinels reach QUORUM (majority agree Master is down)
  3. One Sentinel is elected leader for the failover
  4. Leader promotes Replica 1 to new Master
  5. Replica 2 is reconfigured to replicate from new Master
  6. Clients are notified of the new Master address
  
  After failover:
  +------------+     +------------+     +------------+
  |   OLD      |     | NEW MASTER |     |  Replica 2 |
  |  MASTER    |     | (was R1)   |<----| (follows   |
  |  (DOWN)    |     | 10.0.0.2   |     |  new master|
  +------------+     +------------+     +------------+
  
  Failover time: typically 5-30 seconds
  
  Configuration:
  sentinel monitor mymaster 10.0.0.1 6379 2    # Monitor master, quorum = 2
  sentinel down-after-milliseconds mymaster 5000 # 5s to consider down
  sentinel failover-timeout mymaster 60000       # 60s failover timeout
```

#### Redis Cluster (HA + Sharding)

Redis Cluster distributes data across **multiple master nodes** using **hash slots.** It provides
both sharding (horizontal scaling) and high availability (automatic failover).

```
  Architecture:
  
  Hash Slot Range: 0 to 16383 (16384 total slots)
  
  +-------------------+    +-------------------+    +-------------------+
  | Master 1          |    | Master 2          |    | Master 3          |
  | Slots: 0-5460     |    | Slots: 5461-10922 |    | Slots: 10923-16383|
  |   |               |    |   |               |    |   |               |
  |   +-> Replica 1A  |    |   +-> Replica 2A  |    |   +-> Replica 3A  |
  +-------------------+    +-------------------+    +-------------------+
  
  How data is distributed:
  slot = CRC16(key) % 16384
  
  SET user:alice "data"
  -> CRC16("user:alice") = 10239
  -> 10239 % 16384 = 10239
  -> Slot 10239 is on Master 2 -> routed to Master 2
  
  SET user:bob "data"
  -> CRC16("user:bob") = 3876
  -> Slot 3876 is on Master 1 -> routed to Master 1
  
  Client routing:
  1. Client sends command to ANY node
  2. If node owns the slot -> executes command
  3. If node does NOT own the slot -> returns MOVED redirect:
     -MOVED 10239 10.0.0.2:6379
  4. Client updates its slot mapping and retries
  5. Smart clients cache the slot map to avoid redirects
  
  Failover within Cluster:
  - If Master 2 crashes -> Replica 2A is promoted to Master
  - Slots 5461-10922 are now served by the new Master (was Replica 2A)
  - Other masters and replicas are unaffected
  
  Scaling the cluster:
  Adding Master 4:
  1. Add new node to cluster
  2. Redis Cluster redistributes hash slots:
     Master 1: 0-4095     (was 0-5460)
     Master 2: 4096-8191  (was 5461-10922)
     Master 3: 8192-12287 (was 10923-16383)
     Master 4: 12288-16383 (new)
  3. Data in moved slots is migrated in the background
  4. Zero downtime! Clients follow MOVED redirects during migration.
```

#### Sentinel vs Cluster

```
  +------------------+------------------------------+------------------------------+
  | Feature          | Redis Sentinel               | Redis Cluster                |
  +------------------+------------------------------+------------------------------+
  | Sharding         | No (single dataset)          | Yes (hash slot sharding)     |
  | Max data size    | Limited to one server's RAM   | Sum of all masters' RAM      |
  | Write scaling    | No (single master)           | Yes (writes to any master)   |
  | Read scaling     | Yes (read from replicas)     | Yes (replicas per master)    |
  | Failover         | Sentinel-managed             | Cluster-managed              |
  | Multi-key ops    | All keys on one node (fine)  | Only if keys on SAME slot    |
  | Complexity       | Low                          | Medium-High                  |
  | When to use      | Dataset < single server RAM  | Dataset > single server RAM  |
  |                  | Need HA for existing setup   | Need horizontal write scaling|
  +------------------+------------------------------+------------------------------+
```

---

### Redis Pub/Sub — Real-Time Messaging

Redis Pub/Sub allows publishers to send messages to channels without knowing who (if anyone)
is listening, and subscribers to receive messages from channels of interest.

```
  Publisher                    Redis                    Subscribers
  
  +--------+   PUBLISH        +---------+   message    +----------+
  | App A  |---"news"-------->| Channel |------------->| Worker 1 |
  | (pub)  |   "breaking      | "news"  |              | (sub)    |
  +--------+    news!"        +---------+              +----------+
                                   |      message      +----------+
                                   +------------------>| Worker 2 |
                                                       | (sub)    |
                                                       +----------+
  
  Commands:
  Subscriber:  SUBSCRIBE news sports weather
  Publisher:   PUBLISH news "Breaking: Redis 8.0 released!"
  -> Both Worker 1 and Worker 2 receive the message
  
  Pattern subscribe:
  PSUBSCRIBE user:*:events
  -> Receives messages from user:42:events, user:99:events, etc.
  
  Key characteristics:
  - Fire-and-forget: if no subscribers are listening, message is LOST
  - No persistence: messages are NOT stored (unlike Streams)
  - No acknowledgment: publisher doesn't know if anyone received it
  - Fan-out: one message goes to ALL subscribers of that channel
  
  Use cases:
  - Real-time notifications (chat indicators, typing status)
  - Cache invalidation across application servers
  - Event broadcasting (config changes, feature flag updates)
  - Live dashboards (push updates to all connected clients)
  
  NOT suitable for:
  - Reliable message delivery (use Redis Streams or RabbitMQ instead)
  - Message history (messages are not stored)
  - Work queues (all subscribers get all messages, no load distribution)
```

---

### Redis Pipelining and Transactions

#### Pipelining — Batch Commands for Speed

```
  WITHOUT pipelining (10 commands, 10 round trips):
  
  Client                          Redis
  SET k1 v1 ---------------------->
  <------------------------------ OK
  SET k2 v2 ---------------------->
  <------------------------------ OK
  SET k3 v3 ---------------------->
  <------------------------------ OK
  ... (7 more round trips)
  
  Total time: 10 * RTT (e.g., 10 * 0.5ms = 5ms)
  
  WITH pipelining (10 commands, 1 round trip):
  
  Client                          Redis
  SET k1 v1 ---+
  SET k2 v2 ---+
  SET k3 v3 ---+--(all sent at once)-->  Process all 10
  ...          ---+                      commands
  SET k10 v10 ---+
  <-----(all responses at once)--------- OK, OK, OK... (10x OK)
  
  Total time: 1 * RTT + processing (e.g., 0.5ms + 0.01ms = 0.51ms)
  
  Speedup: ~10x for 10 commands, ~100x for 100 commands!
  
  Implementation (Python with redis-py):
    pipe = redis.pipeline()
    for i in range(1000):
        pipe.set(f"key:{i}", f"value:{i}")
    pipe.execute()  # All 1000 commands sent in one round trip
```

#### Transactions (MULTI/EXEC)

```
  Redis transactions group commands that execute ATOMICALLY (all or nothing).
  
  MULTI                               # Start transaction
  SET account:alice:balance 900       # Queued (not executed yet)
  SET account:bob:balance 1100        # Queued
  EXEC                                # Execute all queued commands atomically
  
  If another client modifies the keys between MULTI and EXEC,
  use WATCH for optimistic locking:
  
  WATCH account:alice:balance         # Watch for changes
  current = GET account:alice:balance # Read current value (1000)
  MULTI
  SET account:alice:balance 900       # Deduct 100
  SET account:bob:balance 1100        # Add 100
  EXEC
  # If another client modified alice's balance after WATCH,
  # EXEC returns nil (transaction aborted). Client retries.
  
  Note: Redis transactions are NOT like SQL transactions.
  - No rollback (if SET fails, other commands still execute)
  - No isolation levels (other clients see intermediate state during EXEC)
  - Use Lua scripts for true atomic operations with logic
```

#### Lua Scripting — Atomic Operations with Logic

```
  Lua scripts execute ATOMICALLY on the Redis server. No other command
  can run while a Lua script is executing. This is the most powerful
  way to implement complex atomic operations.
  
  Example: Atomic rate limiter
  
  local key = KEYS[1]
  local limit = tonumber(ARGV[1])
  local window = tonumber(ARGV[2])
  
  local current = tonumber(redis.call('GET', key) or '0')
  if current >= limit then
      return 0  -- Rate limit exceeded
  end
  redis.call('INCR', key)
  if current == 0 then
      redis.call('EXPIRE', key, window)
  end
  return 1  -- Allowed
  
  EVALSHA <sha> 1 rate_limit:user:42 100 60
  # Allow 100 requests per 60 seconds for user 42
  
  Why Lua scripts over transactions:
  - Can include IF/ELSE logic (transactions cannot)
  - Truly atomic (no interleaving)
  - Can read and write in the same operation
  - Cached on server (EVALSHA uses SHA hash, no re-sending script)
```

---

## In-Process Caching — Local Memory Caches

Before data reaches Redis/Memcached (network cache), you can cache it **inside your application
process** in local memory. This is the fastest possible cache — no network hop at all.

### In-Process vs Distributed Cache

```
  +-------------------------------------------------------------------+
  | In-Process Cache              | Distributed Cache (Redis)         |
  +-------------------------------------------------------------------+
  | Data in application memory    | Data in separate Redis server     |
  | Access time: ~50 nanoseconds  | Access time: ~0.5-2 milliseconds  |
  | No network hop                | Network round trip required       |
  | Per-instance (not shared)     | Shared across all instances       |
  | Lost on restart/deploy        | Survives app restarts             |
  | Size limited by app heap      | Size limited by Redis server RAM  |
  | Consistency: HARD (each       | Consistency: EASIER (single       |
  |   instance has its own copy)  |   source of truth)                |
  +-------------------------------------------------------------------+
  
  When to use in-process:
  - EXTREMELY hot data (called 1000s of times/second per instance)
  - Configuration data (rarely changes)
  - Reference/lookup data (country codes, currency rates)
  - Data that can tolerate staleness across instances
  
  When NOT to use in-process:
  - Data that must be consistent across all app instances
  - Large datasets (will consume app heap memory)
  - Data that changes frequently (stale copies everywhere)
```

### Popular In-Process Cache Libraries

```
  Java:
  +-------------------+--------------------------------------------------+
  | Library           | Key Features                                     |
  +-------------------+--------------------------------------------------+
  | Caffeine          | Near-optimal hit rate (TinyLFU eviction),        |
  |                   | async loading, time-based expiration,            |
  |                   | THE default choice for Java. 15M+ ops/sec.      |
  +-------------------+--------------------------------------------------+
  | Guava Cache       | Google's cache from Guava library. Good but      |
  |                   | Caffeine is its successor with better perf.      |
  +-------------------+--------------------------------------------------+
  | EHCache           | Mature, supports heap + off-heap + disk tiers.   |
  |                   | Supports distributed mode with Terracotta.       |
  +-------------------+--------------------------------------------------+
  
  Python:
  +-------------------+--------------------------------------------------+
  | Library           | Key Features                                     |
  +-------------------+--------------------------------------------------+
  | cachetools        | TTL, LRU, LFU implementations. Simple.           |
  | functools.lru_cache| Built-in Python decorator. Basic LRU.           |
  +-------------------+--------------------------------------------------+
  
  Go:
  +-------------------+--------------------------------------------------+
  | Library           | Key Features                                     |
  +-------------------+--------------------------------------------------+
  | BigCache          | No GC overhead (stores data as byte slices).     |
  | Ristretto         | Dgraph's cache with TinyLFU admission policy.   |
  +-------------------+--------------------------------------------------+
  
  .NET:
  +-------------------+--------------------------------------------------+
  | Library           | Key Features                                     |
  +-------------------+--------------------------------------------------+
  | MemoryCache       | Built-in .NET. CacheItemPolicy for expiration.   |
  | LazyCache         | Thread-safe wrapper, prevents stampede.           |
  +-------------------+--------------------------------------------------+
  
  Example: Caffeine (Java)
  
  LoadingCache<String, User> userCache = Caffeine.newBuilder()
      .maximumSize(10_000)                    // Max 10K entries
      .expireAfterWrite(Duration.ofMinutes(5)) // 5 min TTL
      .refreshAfterWrite(Duration.ofMinutes(1)) // Async refresh after 1 min
      .recordStats()                           // Enable hit/miss metrics
      .build(userId -> userService.getUser(userId)); // Loader function
  
  User user = userCache.get("user:42");  // Cache hit or auto-load
  // First call: loads from userService (cache miss)
  // Second call: returns from cache (nanoseconds, no network)
```

---

## Multi-Level Caching Architecture (L1 + L2)

Production systems often combine **in-process (L1)** and **distributed (L2)** caches for
maximum performance.

```
  Multi-Level Cache Architecture:
  
  +--------+     +------------------+     +------------------+     +----------+
  | Client |---->| Application      |---->| Redis (L2)       |---->| Database |
  +--------+     | Server           |     | Distributed Cache|     |          |
                 |                  |     |                  |     |          |
                 | +-------------+  |     |                  |     |          |
                 | | Caffeine    |  |     |                  |     |          |
                 | | (L1 Cache)  |  |     |                  |     |          |
                 | | In-Process  |  |     |                  |     |          |
                 | +-------------+  |     |                  |     |          |
                 +------------------+     +------------------+     +----------+
  
  Request flow:
  1. Check L1 (Caffeine, in-process)
     -> HIT: Return immediately (~50 nanoseconds) DONE!
     -> MISS: Continue to step 2
  
  2. Check L2 (Redis, distributed)
     -> HIT: Return data, store in L1 for next time (~1 ms)
     -> MISS: Continue to step 3
  
  3. Query Database
     -> Store result in L2 (Redis) with TTL
     -> Store result in L1 (Caffeine) with shorter TTL
     -> Return data (~10-50 ms)
  
  Performance characteristics:
  +------------------+------------------+------------------+
  | Level            | Latency          | Hit Rate (target)|
  +------------------+------------------+------------------+
  | L1 (Caffeine)    | ~50 nanoseconds  | 50-70%           |
  | L2 (Redis)       | ~1 millisecond   | 90-95% of L1     |
  |                  |                  | misses           |
  | Database         | ~10-50 ms        | Remaining 5-10%  |
  +------------------+------------------+------------------+
  
  Effective hit rate:
  - L1 hit rate: 60%
  - L2 hit rate (of L1 misses): 90%
  - Combined hit rate: 60% + (40% * 90%) = 60% + 36% = 96%
  - Only 4% of requests hit the database!
  
  The consistency challenge:
  Problem: Server A updates a record in DB and Redis (L2).
           But Server B still has the OLD value in its L1 cache.
  
  Solutions:
  a) Short L1 TTL (10-60 seconds) — accept brief staleness
  b) Redis Pub/Sub to broadcast invalidation to all servers:
     Server A updates -> publishes "invalidate:user:42" to Redis channel
     All servers subscribe -> delete "user:42" from their L1 cache
  c) Versioned keys: cache_key = "user:42:v5" — version changes on update
```

---

## Redis Practical Patterns

### Pattern 1: Distributed Locking with Redis

When multiple application instances need to coordinate exclusive access to a shared resource.

```
  Problem: Two servers processing the same order simultaneously.
  
  Server A: Process order #42 -> charge credit card
  Server B: Process order #42 -> charge credit card  (DOUBLE CHARGE!)
  
  Solution: Distributed lock with Redis
  
  Acquire lock:
  SET lock:order:42 "server-a-uuid" NX EX 30
  # NX = only if not exists (atomic check-and-set)
  # EX 30 = auto-expire in 30 seconds (prevents dead locks)
  
  If SET returns OK -> lock acquired, proceed with processing
  If SET returns nil -> lock held by someone else, wait/retry
  
  Release lock (Lua script for atomicity):
  if redis.call("GET", KEYS[1]) == ARGV[1] then
      return redis.call("DEL", KEYS[1])
  else
      return 0
  end
  # Only release if WE hold the lock (check value matches our UUID)
  # Prevents: Server A's lock expires, Server B acquires, 
  #           Server A finishes and deletes Server B's lock!
  
  Redlock (distributed lock across multiple Redis masters):
  For critical operations, acquire locks on N/2+1 independent Redis masters.
  Tolerates failure of any minority of Redis nodes.
  
  Timeline:
  Server A: SET lock NX EX 30 -> "OK"    (lock acquired)
  Server B: SET lock NX EX 30 -> nil     (lock denied, waiting)
  Server A: ... processing order #42 ...
  Server A: DEL lock (if still owner)     (lock released)
  Server B: SET lock NX EX 30 -> "OK"    (lock acquired now)
```

### Pattern 2: Rate Limiting with Redis

```
  Fixed Window Rate Limiter:
  
  Key: rate_limit:{user_id}:{window}
  
  function is_allowed(user_id, limit, window_seconds):
      key = f"rate_limit:{user_id}:{current_minute()}"
      current = INCR key
      if current == 1:
          EXPIRE key window_seconds    # Set TTL on first request
      return current <= limit
  
  Example: 100 requests per minute
  INCR rate_limit:user42:2024-01-15T10:30    -> 1 (first request, allowed)
  EXPIRE rate_limit:user42:2024-01-15T10:30 60
  INCR rate_limit:user42:2024-01-15T10:30    -> 2 (allowed)
  ... 
  INCR rate_limit:user42:2024-01-15T10:30    -> 101 (DENIED! Over limit)
  
  Sliding Window Rate Limiter (using Sorted Set):
  
  ZADD rate_limit:user42 {timestamp} {unique_request_id}
  ZREMRANGEBYSCORE rate_limit:user42 0 {timestamp - window}
  ZCARD rate_limit:user42   -> current request count in window
  
  If count >= limit -> deny request
  Else -> allow request
  
  This gives a TRUE sliding window (no boundary issues).
```

### Pattern 3: Session Management with Redis

```
  Login -> Create Session:
  session_id = generate_uuid()
  HSET session:{session_id} user_id "42" name "Alice" role "admin"
  EXPIRE session:{session_id} 3600    # 1 hour TTL
  Set-Cookie: session_id={session_id}; HttpOnly; Secure
  
  Each Request -> Read Session:
  session_data = HGETALL session:{cookie.session_id}
  if session_data is empty -> redirect to login (expired/invalid)
  EXPIRE session:{session_id} 3600    # Refresh TTL (sliding expiration)
  
  Logout -> Destroy Session:
  DEL session:{session_id}
  
  Why Redis for sessions:
  - Sub-millisecond access (faster than DB)
  - Automatic expiry via TTL (no cleanup jobs needed)
  - Shared across all app servers (no sticky sessions)
  - Hash data structure is perfect for session key-value pairs
  - Atomic operations prevent race conditions
  
  Capacity planning:
  - Average session size: ~1 KB (10-20 fields)
  - 1 million concurrent sessions: ~1 GB Redis memory
  - Redis can handle 100K+ session operations per second easily
```

### Pattern 4: Leaderboard with Redis Sorted Set

```
  Gaming Leaderboard (10 million players):
  
  Player scores a point:
  ZINCRBY global:leaderboard 1 "player:alice"    # O(log n)
  
  Get top 10 players:
  ZREVRANGE global:leaderboard 0 9 WITHSCORES    # O(log n + 10)
  -> ["player:bob", 15000, "player:charlie", 14500, ...]
  
  Get player's rank:
  ZREVRANK global:leaderboard "player:alice"      # O(log n)
  -> 42 (alice is ranked #43, 0-indexed)
  
  Get players ranked 100-110:
  ZREVRANGE global:leaderboard 100 110 WITHSCORES
  
  Get players with score between 10000-15000:
  ZRANGEBYSCORE global:leaderboard 10000 15000
  
  Performance: All operations are O(log n).
  For 10 million players: log2(10M) = ~23 comparisons. Instant.
  
  Alternative (without Redis): SQL query
  SELECT player_id, score FROM leaderboard ORDER BY score DESC LIMIT 10;
  -> With 10M rows and proper index: ~10-50ms
  -> With Redis: ~0.1ms (100-500x faster)
  -> And Redis supports REAL-TIME updates (no query re-execution)
```

---

## Message-Driven Cache Invalidation (RabbitMQ, Kafka, and Event Bus)

One of the hardest problems in caching is **keeping the cache in sync with the database.**
When data changes in the database, the cache must be invalidated or updated. Message queues
(RabbitMQ, Kafka) play a critical role in solving this reliably at scale.

### The Cache Invalidation Problem at Scale

```
  Scenario: E-commerce site with 20 application servers, all caching product data in Redis.
  
  Problem: Product price changes from $99 to $79.
  
  Simple approach (direct invalidation):
  App Server A receives the update request:
  1. UPDATE products SET price = 79 WHERE id = 42;
  2. DEL product:42 from Redis;
  
  Seems fine, but what about:
  - Race condition: Another server reads old data BETWEEN step 1 and 2?
  - App crash: Server A crashes AFTER step 1 but BEFORE step 2?
    -> Database has $79, cache still shows $99 (stale forever!)
  - Multiple caches: What about L1 caches on 20 servers?
  - Multiple regions: What about Redis in US-East and EU-West?
```

### Solution: Event-Driven Invalidation via Message Queue

```
  Architecture:
  
  +--------+  1. Write  +------+  2. CDC/Trigger  +----------+  3. Publish  
  | App    +----------->| DB   +----------------->| Message  +------------>
  | Server |            |      |  (Change Data    | Queue    |  invalidation
  +--------+            +------+   Capture)        | (Rabbit  |  event
                                                   | MQ or    |
                                                   | Kafka)   |
                                                   +----+-----+
                                                        |
                        +-------------------------------+-----+
                        |               |                     |
                  4a. Consume    4b. Consume            4c. Consume
                        |               |                     |
                  +-----v----+   +------v-----+   +----------v---+
                  | App Srv 1|   | App Srv 2  |   | App Srv 3    |
                  | DEL L1   |   | DEL L1     |   | DEL L1       |
                  | cache    |   | cache      |   | cache        |
                  +----------+   +------------+   +--------------+
                        |               |                     |
                        +-------+-------+---------------------+
                                |
                          +-----v------+
                          | Redis (L2) |
                          | DEL cache  |
                          +------------+
  
  Flow:
  1. Application writes to database
  2. Database change is captured (CDC via Debezium, DB trigger, or application event)
  3. Change event published to message queue (RabbitMQ or Kafka)
  4. All application servers consume the event and:
     a) Delete/update their L1 in-process cache
     b) Delete/update the shared L2 Redis cache
  5. Next read request will cache-miss and fetch fresh data from DB
```

### RabbitMQ for Cache Invalidation

RabbitMQ is a **message broker** that implements the AMQP protocol. It is widely used for
reliable message delivery between services, including cache invalidation.

```
  RabbitMQ Core Concepts:
  
  +------------+     +----------+     +---------+     +------------+
  | Producer   |---->| Exchange |---->| Queue   |---->| Consumer   |
  | (publishes |     | (routes  |     | (stores |     | (processes |
  |  messages) |     |  msgs)   |     |  msgs)  |     |  messages) |
  +------------+     +----------+     +---------+     +------------+
  
  Exchange Types:
  +-------------------+--------------------------------------------------+
  | Type              | Routing Behavior                                 |
  +-------------------+--------------------------------------------------+
  | Direct            | Route to queue with matching routing key         |
  |                   | "cache.invalidate.product" -> product queue      |
  +-------------------+--------------------------------------------------+
  | Fanout            | Route to ALL bound queues (broadcast)            |
  |                   | Every app server gets every invalidation event   |
  +-------------------+--------------------------------------------------+
  | Topic             | Route based on pattern matching                  |
  |                   | "cache.invalidate.product.*" matches             |
  |                   | "cache.invalidate.product.42"                    |
  +-------------------+--------------------------------------------------+
  | Headers           | Route based on message headers (rarely used)     |
  +-------------------+--------------------------------------------------+
  
  Cache Invalidation with RabbitMQ:
  
  1. Producer (the writing app server):
     channel.basic_publish(
         exchange='cache_invalidation',    # Fanout exchange
         routing_key='',
         body='{"type": "product", "id": 42, "action": "updated"}'
     )
  
  2. Exchange (fanout): broadcasts to ALL bound queues
  
  3. Each app server has its own queue bound to the exchange:
     Queue: cache_invalidation_server_1
     Queue: cache_invalidation_server_2
     Queue: cache_invalidation_server_3
  
  4. Consumer (every app server):
     def on_message(message):
         event = json.loads(message.body)
         if event['action'] == 'updated':
             local_cache.delete(f"product:{event['id']}")  # L1
             redis.delete(f"product:{event['id']}")         # L2
         message.ack()  # Acknowledge processing
  
  Why RabbitMQ for this:
  + Message acknowledgment (guaranteed delivery)
  + Fanout exchange = perfect for broadcasting to all servers
  + Persistent queues survive RabbitMQ restarts
  + Dead letter queue for failed messages
  + Prefetch control (don't overwhelm consumers)
```

### RabbitMQ vs Kafka for Cache Invalidation

```
  +-------------------+--------------------------------+--------------------------------+
  | Feature           | RabbitMQ                       | Kafka                          |
  +-------------------+--------------------------------+--------------------------------+
  | Model             | Message broker                 | Distributed log                |
  |                   | (push to consumers)            | (consumers pull from log)      |
  +-------------------+--------------------------------+--------------------------------+
  | Message lifetime  | Deleted after consumption      | Retained for configured period |
  +-------------------+--------------------------------+--------------------------------+
  | Ordering          | Per-queue FIFO                 | Per-partition ordering          |
  +-------------------+--------------------------------+--------------------------------+
  | Throughput        | ~50K msgs/sec per node         | ~1M+ msgs/sec per node         |
  +-------------------+--------------------------------+--------------------------------+
  | Consumer groups   | Competing consumers per queue  | Consumer groups per topic      |
  +-------------------+--------------------------------+--------------------------------+
  | Replay            | No (message consumed = gone)   | Yes (re-read from any offset)  |
  +-------------------+--------------------------------+--------------------------------+
  | Best for cache    | Small-medium systems           | Large-scale systems            |
  | invalidation      | (< 100 servers)                | (100+ servers, high volume)    |
  +-------------------+--------------------------------+--------------------------------+
  | Complexity        | Lower                          | Higher                         |
  +-------------------+--------------------------------+--------------------------------+
  
  Recommendation:
  - RabbitMQ: When you need reliable cache invalidation for 5-50 app servers.
    Simple to operate, message-level acknowledgment, routing flexibility.
  - Kafka: When you have 100+ consumers, need event replay capability,
    or already use Kafka for event streaming. Higher throughput.
  - Redis Pub/Sub: When you need fire-and-forget invalidation and can
    tolerate missed messages (simplest, but unreliable).
```

### Change Data Capture (CDC) — The Source-of-Truth Approach

```
  CDC captures database changes at the WAL (Write-Ahead Log) level, 
  ensuring NO change is missed — even if the application forgets to 
  publish an invalidation event.
  
  +--------+  Write  +------+  WAL/Binlog  +---------+  Events  +-------+
  | App    +-------->| DB   +------------->| Debezium+--------->| Kafka |
  | Server |         | (PG/ |  stream      | (CDC    |          |       |
  +--------+         | MySQL|              |  tool)  |          +---+---+
                     +------+              +---------+              |
                                                                    |
                                                           +--------v-------+
                                                           | Cache Invalidation
                                                           | Service         |
                                                           | DEL from Redis  |
                                                           +-----------------+
  
  How Debezium CDC works:
  1. Debezium connects to the database's replication stream
     (PostgreSQL logical replication, MySQL binlog)
  2. Every INSERT, UPDATE, DELETE is captured as an event:
     {
       "op": "u",  // u=update, c=create, d=delete
       "before": {"id": 42, "price": 99},
       "after": {"id": 42, "price": 79},
       "source": {"table": "products", "ts_ms": 1705312800000}
     }
  3. Event published to Kafka topic: "dbserver.public.products"
  4. Cache invalidation service consumes the event and invalidates cache
  
  Why CDC is the gold standard:
  + Captures ALL changes (even direct SQL updates, bulk imports)
  + No application code changes needed
  + Exactly-once delivery with Kafka
  + Can rebuild entire cache from event log (replay)
  
  Popular CDC tools:
  - Debezium (open-source, supports PostgreSQL, MySQL, MongoDB, SQL Server)
  - AWS DMS (Database Migration Service — also supports CDC)
  - Maxwell (MySQL binlog -> Kafka)
  - pg_logical (PostgreSQL native logical replication)
```

---

## Cache Sizing and Capacity Planning

### How to Estimate Cache Size

```
  Formula:
  Cache Size = (Number of items to cache) × (Average item size) × (Overhead factor)
  
  Example: E-commerce product cache
  
  Items to cache:
  - Total products: 5 million
  - Hot products (80/20 rule): 1 million (20% of products get 80% of traffic)
  - Decision: Cache the hot 1 million products
  
  Average item size:
  - Product JSON: ~500 bytes
  - Redis key overhead: ~50 bytes (key name + internal metadata)
  - Redis hash overhead: ~100 bytes (ziplist for small hashes)
  - Total per item: ~650 bytes
  
  Overhead factor: 1.2 (Redis memory fragmentation, ~20% overhead)
  
  Cache Size = 1,000,000 × 650 bytes × 1.2
             = 780,000,000 bytes
             = ~780 MB
  
  Recommendation: Provision 1 GB Redis instance (leave headroom for growth)
```

### Memory Optimization Techniques

```
  1. Use Hashes for small objects (Redis ziplist encoding):
     Instead of: SET user:1:name "Alice"
                 SET user:1:email "alice@e.com"
                 Memory: ~200 bytes per field (key overhead)
     
     Use:        HSET user:1 name "Alice" email "alice@e.com"
                 Memory: ~80 bytes total (ziplist packs fields)
                 Savings: 60%+ for small objects
  
  2. Use short key names:
     Instead of: user_session_data:1001:full_profile
     Use:        s:1001:p (if documented)
     Savings: 30+ bytes per key × millions of keys = significant
  
  3. Compress large values:
     Instead of: SET product:42 '{...1KB JSON...}'
     Use:        SET product:42 <gzip compressed bytes>
     Savings: 50-80% for JSON/text data
     Trade-off: CPU cost for compress/decompress
  
  4. Use appropriate data types:
     - Integer values: Redis stores efficiently (8 bytes)
     - Bitmaps: 1 bit per flag instead of "true"/"false" strings
     - HyperLogLog: 12 KB for cardinality instead of full Set
  
  5. Set maxmemory and eviction policy:
     maxmemory 4gb
     maxmemory-policy allkeys-lru
     # When memory is full, evict least recently used keys
     
     Available policies:
     +----------------------+----------------------------------------------+
     | Policy               | Behavior                                     |
     +----------------------+----------------------------------------------+
     | noeviction           | Return error on writes (don't evict)         |
     | allkeys-lru          | Evict LRU keys from ALL keys (most common)  |
     | volatile-lru         | Evict LRU keys ONLY from keys with TTL set  |
     | allkeys-lfu          | Evict LFU keys from ALL keys                 |
     | volatile-lfu         | Evict LFU keys with TTL set                  |
     | allkeys-random       | Evict random keys                            |
     | volatile-random      | Evict random keys with TTL                   |
     | volatile-ttl         | Evict keys with shortest remaining TTL       |
     +----------------------+----------------------------------------------+
```

### Cache Monitoring — Essential Metrics

```
  +----------------------------+-------------------------------------------+
  | Metric                     | What It Tells You                         |
  +----------------------------+-------------------------------------------+
  | Hit Rate                   | % requests served from cache. Target >90% |
  |                            | Low = wrong keys cached or wrong TTL      |
  +----------------------------+-------------------------------------------+
  | Miss Rate                  | % requests going to backend. Target <10%  |
  |                            | High = cache warming issue or cold start  |
  +----------------------------+-------------------------------------------+
  | Memory Usage               | Current vs max memory. Alert at 80%       |
  +----------------------------+-------------------------------------------+
  | Eviction Rate              | Keys evicted per second. Should be steady  |
  |                            | Spike = cache too small or TTL too long   |
  +----------------------------+-------------------------------------------+
  | Connected Clients          | Number of active connections              |
  +----------------------------+-------------------------------------------+
  | Commands/sec               | Throughput. Watch for sudden drops/spikes |
  +----------------------------+-------------------------------------------+
  | Latency (p50, p99)        | Tail latency. p99 > 5ms = investigate     |
  +----------------------------+-------------------------------------------+
  | Keyspace misses vs hits    | Redis INFO stats: keyspace_hits,          |
  |                            | keyspace_misses                           |
  +----------------------------+-------------------------------------------+
  
  Redis monitoring commands:
  INFO stats           # Hit/miss counts, commands processed
  INFO memory          # Memory usage, fragmentation ratio
  INFO clients         # Connected clients, blocked clients
  SLOWLOG GET 10       # Last 10 slow commands (> 10ms default)
  MONITOR              # Real-time stream of all commands (DEBUG ONLY!)
  CLIENT LIST          # All connected clients with details
```

---

## Cache Consistency Problems

Caching introduces one of the hardest problems in computer science: **cache invalidation**.

> *"There are only two hard things in Computer Science: cache invalidation and naming things."*
> — Phil Karlton

### Problem 1: Stale Data

When the source data changes but the cache still holds the old value.

```
  Time 0: Cache[user:123] = {name: "Alice", age: 25}
  Time 1: DB update: user:123 age -> 26
  Time 2: Cache still returns {name: "Alice", age: 25}  <- STALE!
  Time 3: Cache TTL expires, next read fetches fresh data
```

**Solutions:**
- Short TTLs (accept brief staleness)
- Active invalidation on writes (delete or update cache entry)
- Event-driven invalidation (DB change stream triggers cache update)

### Problem 2: Thundering Herd

When a popular cache key expires, hundreds of concurrent requests **simultaneously** hit the database to reload it.

```
  Time 0: Cache[popular_item] expires
  
  Thread 1: cache miss -> query DB
  Thread 2: cache miss -> query DB     All at the
  Thread 3: cache miss -> query DB     same instant!
  Thread 4: cache miss -> query DB     DB overwhelmed!
  Thread 5: cache miss -> query DB
  ...
  Thread 100: cache miss -> query DB
```

### Problem 3: Cache Stampede

Similar to thundering herd but happens during **cache warming** or **cold start** — when the cache is empty and all requests hit the backend.

---

## Solutions to Cache Consistency Problems

### 1. Cache Locking (Mutex)

Only one thread fetches from DB on a miss; others wait or return stale data.

```
  Thread 1: cache miss -> acquire lock -> query DB -> populate cache -> release lock
  Thread 2: cache miss -> lock busy -> wait/retry -> cache hit (populated by T1)
  Thread 3: cache miss -> lock busy -> wait/retry -> cache hit (populated by T1)
```

**Implementation with Redis:**
```
SET cache_lock:popular_item "locked" NX EX 10  # Acquire lock (NX = only if not exists)
# If lock acquired: fetch from DB, populate cache, release lock
# If lock not acquired: wait and retry, or return stale data
```

### 2. Probabilistic Early Expiration

Proactively refresh cache entries **before** they expire using a probabilistic approach. Each request has a small chance of triggering a background refresh based on how close the TTL is to expiring.

```
  remaining_ttl = expiry_time - current_time
  random_value = random(0, 1)
  
  if random_value < (1 / remaining_ttl_factor):
      refresh_cache_in_background()
```

This spreads out cache refreshes over time, preventing a sudden stampede.

### 3. Cache Warming

Pre-populate the cache **before** traffic arrives. Essential during:
- Application startup / deployment
- Cache server replacement
- New region/data center launch

```
  Deployment Pipeline:
  1. Deploy new application version
  2. Run cache warming script (loads popular keys from DB)
  3. Enable traffic to new instances
```

### 4. Stale-While-Revalidate

Serve the **stale (expired) value** immediately while refreshing the cache in the background.

```
  Thread 1: cache expired -> return stale data -> trigger async refresh
  Thread 2: cache expired -> return stale data (same stale)
  Background: fetch from DB -> update cache
  Thread 3: cache hit -> return fresh data
```

**Pros:** Users never wait for a cache miss
**Cons:** Brief window of stale data

---

## Cache Invalidation Patterns

### 1. TTL-Based Expiration
Simplest approach — set a TTL and let entries expire automatically.
- **Good for:** Data that changes infrequently or where brief staleness is acceptable
- **Risk:** Stale data during the TTL window

### 2. Event-Driven Invalidation
Database changes trigger cache invalidation via events (CDC, message queue).

```
  +--------+   Write   +------+   Change Event   +--------+   Invalidate   +-------+
  | App    +---------->| DB   +------------------>| Event  +--------------->| Cache |
  | Server |           |      |   (CDC/Trigger)   | Bus    |               |       |
  +--------+           +------+                   +--------+               +-------+
```

### 3. Write-Through Invalidation
Cache is updated synchronously on every write (as discussed above).

### 4. Versioned Keys
Include a version number in the cache key. When data changes, increment the version.

```
  cache_key = "user:123:v5"    # Current version
  # After update:
  cache_key = "user:123:v6"    # New version (old key ignored, eventually evicted)
```

---

## Real-World Caching Examples

### Twitter's Timeline Cache

Twitter caches pre-computed home timelines for each user:
- **Fan-out on write:** When a user tweets, the tweet is pushed to the timeline cache of all followers
- **Redis** stores each user's timeline as a sorted set (tweet IDs sorted by timestamp)
- **Challenge:** Celebrities with millions of followers cause massive fan-out; solved by hybrid approach (fan-out on read for celebrities)

### Facebook's Memcached at Scale

Facebook built **TAO** (The Associations and Objects), a distributed caching layer:
- **Billions** of key-value pairs cached across thousands of Memcached servers
- **Read-after-write consistency** ensured by routing writes and subsequent reads to the same region
- **Lease mechanism** to prevent thundering herd
- **Multi-region replication** with invalidation via message bus

### Netflix's EVCache

Netflix built **EVCache** (Ephemeral Volatile Cache) on top of Memcached:
- **Zone-aware replication** — data replicated across AWS Availability Zones
- **Global replication** — cache entries replicated across regions for low-latency reads
- **Billions** of items cached; serves **30+ million requests per second**

---

## Anti-Patterns to Avoid

1. **Caching everything** — Only cache data that's read frequently and expensive to compute
2. **No TTL** — Always set a TTL as a safety net, even if you actively invalidate
3. **Ignoring cache size** — Monitor memory usage; unbounded caches cause OOM crashes
4. **Caching mutable data without invalidation** — Leads to stale data and bugs
5. **Single point of failure** — Use Redis Cluster or replicated setup
6. **Complex cache keys** — Keep keys simple and predictable; use a consistent naming convention

---

## Key Takeaways

```
+---------------------------------------------------------------------+
|                    Caching — Key Takeaways                           |
+---------------------------------------------------------------------+
|                                                                      |
|  1. Cache-Aside is the most common and flexible strategy.            |
|     Write-Through for read-after-write consistency.                  |
|     Write-Behind for write-heavy workloads (accept data loss risk).  |
|                                                                      |
|  2. Redis is far more than a cache — it's a data structure server.   |
|     Strings for caching, Hashes for objects, Sorted Sets for         |
|     leaderboards, Lists for queues, Streams for event logs,          |
|     HyperLogLog for cardinality, Pub/Sub for messaging.              |
|                                                                      |
|  3. Redis is fast because: in-memory storage, single-threaded        |
|     event loop (no locks), efficient C data structures (ziplist,     |
|     skiplist), and non-blocking I/O (epoll). 100K-1M ops/sec.       |
|                                                                      |
|  4. For Redis persistence: use hybrid RDB+AOF in production.         |
|     RDB for fast restarts, AOF for minimal data loss (~1 second).    |
|     For pure caching, no persistence is fine (repopulate from DB).   |
|                                                                      |
|  5. Redis Sentinel for HA (automatic failover, read replicas).       |
|     Redis Cluster for HA + horizontal scaling (hash slot sharding).  |
|     Choose Sentinel when data fits one server, Cluster when it       |
|     doesn't.                                                         |
|                                                                      |
|  6. Use multi-level caching: L1 (in-process: Caffeine/Guava,        |
|     nanoseconds) + L2 (Redis, milliseconds). Combined hit rate       |
|     of 96%+ is achievable, with only 4% of requests reaching DB.    |
|                                                                      |
|  7. Cache invalidation is the hardest problem. Use event-driven      |
|     invalidation via message queues (RabbitMQ fanout or Kafka) or    |
|     CDC (Debezium) for reliable, scalable invalidation.              |
|                                                                      |
|  8. RabbitMQ excels at cache invalidation broadcasting via fanout    |
|     exchanges. Each app server gets its own queue, receives all      |
|     invalidation events, and clears its local + shared cache.        |
|                                                                      |
|  9. Redis practical patterns: distributed locks (SET NX EX),         |
|     rate limiting (INCR + EXPIRE or Sorted Set sliding window),      |
|     sessions (HSET + EXPIRE), leaderboards (ZADD + ZREVRANGE).      |
|                                                                      |
| 10. Capacity planning: cache the hot 20% of data (80/20 rule).      |
|     Estimate: items * avg_size * 1.2 overhead. Monitor hit rate,     |
|     eviction rate, memory usage, and p99 latency.                    |
|                                                                      |
| 11. LRU is the default eviction policy. Set TTLs as a safety net    |
|     even with active invalidation. Use allkeys-lru eviction          |
|     policy in Redis for cache workloads.                             |
|                                                                      |
| 12. Thundering herd and cache stampede are real production issues.   |
|     Mitigate with: cache locking (SET NX), probabilistic early       |
|     expiration, cache warming on deploy, stale-while-revalidate.    |
|                                                                      |
+---------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 8 — Databases](./08-databases.md)*
