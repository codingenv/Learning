# Enterprise Database Systems: A Practical Guide to Selection, Optimization, and Scaling

> *After 15+ years of building enterprise systems, I've learned one brutal truth: most database performance problems are architectural, not operational. The wrong database, no amount of tuning will save you. The right database, poorly configured, will still hurt you. This guide covers both.*

---

## Table of Contents

**Part 1: Choosing Right**
1. [Database Landscape — Types, Internals & Taxonomy](#1-database-landscape--types-internals--taxonomy)
2. [Merits and Demerits — Decision Matrix](#2-merits-and-demerits--decision-matrix)
3. [When to Use Which Database — Decision Framework](#3-when-to-use-which-database--decision-framework)

**Part 2: Operating Right**
4. [Database Performance Metrics — What Actually Matters](#4-database-performance-metrics--what-actually-matters)
5. [Read-Write Synchronization — Consistency Internals](#5-read-write-synchronization--consistency-internals)
6. [Read-Heavy vs Write-Heavy — Architecture Patterns](#6-read-heavy-vs-write-heavy--architecture-patterns)
7. [Database Replication — Internals and Failure Modes](#7-database-replication--internals-and-failure-modes)

**Part 3: Optimizing Right**
8. [Caching Strategies — Patterns and Production Pitfalls](#8-caching-strategies--patterns-and-production-pitfalls)
9. [Cache-Database Synchronization — The Hard Part](#9-cache-database-synchronization--the-hard-part)
10. [Indexing Strategies — Internals and Design Patterns](#10-indexing-strategies--internals-and-design-patterns)
11. [Sharding — Strategies, Mechanics, and Gotchas](#11-sharding--strategies-mechanics-and-gotchas)

**Part 4: Scaling Right**
12. [Advanced Topics — Connection Pools, Transactions, Backups](#12-advanced-topics--connection-pools-transactions-backups)
13. [Distributed Transactions — 2PC, Saga, CQRS](#13-distributed-transactions--2pc-saga-cqrs)
14. [Polyglot Persistence — Multi-Database Architecture](#14-polyglot-persistence--multi-database-architecture)
15. [Common Pitfalls, Anti-Patterns, and Real-World Case Studies](#15-common-pitfalls-anti-patterns-and-real-world-case-studies)

---

## 1. Database Landscape — Types, Internals & Taxonomy

### 1.1 The Storage Engine is What You're Really Choosing

Most developers pick databases by brand (PostgreSQL, MongoDB, Cassandra) without understanding the storage engine underneath — but the storage engine determines your performance ceiling, failure characteristics, and scaling model.

There are two dominant storage engine architectures used across almost all databases:

```
B+ TREE STORAGE (Relational DBs, MongoDB's WiredTiger)
─────────────────────────────────────────────────────
  Leaf pages form a linked list (sequential scan)
  Internal nodes hold separator keys
  All data in leaf nodes

  Read:   O(log N) — excellent
  Write:  In-place update → random I/O → slower on large datasets
  Scan:   Excellent (leaf pages linked)
  Space:  Compaction needed to reclaim deleted space

LSM TREE STORAGE (Cassandra, RocksDB, LevelDB)
─────────────────────────────────────────────────────
  Level 0: In-memory memtable (fast writes)
  Level 1-N: Sorted String Tables (SSTables) on disk

  Write:  Sequential append to memtable → flush to SSTable
          → No random I/O → extremely fast writes
  Read:   Check memtable + each SSTable level (+ bloom filters)
          → Slower reads, especially as data ages
  Compaction: Background merge of SSTables (expensive I/O)

  WHY CASSANDRA IS WRITE-FAST: All writes are sequential appends.
  WHY READS DEGRADE: Must check multiple SSTables; compaction helps.
```

**WAL — Write-Ahead Log (Every ACID database uses this):**
```
WHAT HAPPENS ON EVERY WRITE IN POSTGRESQL:
  1. Write the change to WAL (sequential, fast disk write)
  2. Update the in-memory buffer pool (dirty page)
  3. Dirty pages flushed to data files periodically (checkpoints)

WHY WAL MATTERS:
  - On crash: replay WAL to recover uncommitted transactions
  - On replica: ship WAL to replica (streaming replication)
  - fsync=on: WAL flushed to disk before ack (safe, slow)
  - fsync=off: WAL buffered by OS (fast, data loss on crash)

Practical implication:
  PostgreSQL with SSD + fsync=on:   ~2-5ms write latency
  PostgreSQL with HDD + fsync=on:   ~5-20ms write latency
  PostgreSQL with fsync=off:        ~0.1-0.5ms write latency (DANGEROUS)
```

**MVCC — Multi-Version Concurrency Control:**
```
HOW POSTGRESQL HANDLES CONCURRENT READS AND WRITES:
  - Every row has: xmin (created by txn), xmax (deleted by txn)
  - Each transaction sees a SNAPSHOT of data at its start time
  - Readers NEVER block writers; writers NEVER block readers

EXAMPLE:
  Row: {id=1, name="Alice", xmin=100, xmax=null}

  T200: UPDATE users SET name='Bob' WHERE id=1
    → INSERT new row:  {id=1, name="Bob",   xmin=200, xmax=null}
    → Mark old row:    {id=1, name="Alice",  xmin=100, xmax=200}

  T150 (started before T200):
    → Sees name="Alice" (xmin=100 < T150, xmax=200 > T150)

  T250 (started after T200):
    → Sees name="Bob" (xmin=200 < T250, xmax=null)

IMPLICATION: Old row versions accumulate → VACUUM needed
  PostgreSQL VACUUM: marks old versions as reclaimable
  AUTOVACUUM: runs automatically, but tune it or table bloat happens
```

---

### 1.2 Relational Databases (ACID)

**PostgreSQL — The Architect's Choice:**
```
INTERNALS:
  Storage:        B+ tree (heap files)
  Concurrency:    MVCC (readers never block writers)
  Transactions:   Full ACID, isolation levels
  Replication:    WAL-based streaming replication
  Indexing:       B-tree, Hash, GIN, GiST, BRIN, Bloom

PRODUCTION TUNING (starting point for 32GB RAM server):
  shared_buffers        = 8GB      # 25% of RAM
  effective_cache_size  = 24GB     # 75% of RAM (hint to planner)
  work_mem              = 64MB     # per sort/hash per connection
  maintenance_work_mem  = 2GB      # VACUUM, CREATE INDEX
  checkpoint_completion_target = 0.9
  wal_buffers           = 64MB
  max_connections       = 200      # more = more memory, less concurrency
  random_page_cost      = 1.1      # SSD (default 4 = HDD)
```

**MySQL InnoDB — The Workhorse:**
```
INTERNALS:
  Storage:        B+ tree + clustered index (data stored with PK)
  Concurrency:    MVCC (similar to PostgreSQL)
  Transactions:   Full ACID
  Replication:    binlog-based (row, statement, or mixed format)
  Buffer Pool:    Single large LRU cache for data + indexes

PRODUCTION TUNING (32GB RAM):
  innodb_buffer_pool_size  = 24G      # 75% of RAM (most important!)
  innodb_log_file_size     = 2G       # larger = fewer checkpoints
  innodb_flush_method      = O_DIRECT # bypass OS cache (if enough RAM)
  innodb_io_capacity       = 2000     # IOPS of your SSD
  max_connections          = 500
  query_cache_size         = 0        # disable (deprecated, causes contention)
```

**Key architectural difference: MySQL's clustered index:**
```java
// In MySQL InnoDB, PRIMARY KEY = index = data storage
// Reading by PK is fastest — data is physically ordered by PK
// Secondary index lookup = secondary index → PK → clustered index (two lookups)

// This means:
// 1. Choose your PK wisely (auto-increment = sequential writes, good)
// 2. UUID as PK = random inserts = poor write performance
// 3. Large PKs = all secondary indexes carry the full PK value

// PostgreSQL uses heap storage (data separate from indexes)
// Any index access = index → heap (two lookups, always)
// But PostgreSQL can do "index-only scans" with covering indexes
```

---

### 1.3 NoSQL — Document Stores

**MongoDB — WiredTiger Internals:**
```
STORAGE ENGINE (WiredTiger, default since MongoDB 3.2):
  Data stored in B+ trees
  Compression: Snappy by default (configurable)
  MVCC: document-level versioning
  Write concern: majority (wait for >N/2 replicas) vs 0 (fire-and-forget)
  Read concern: local (might read stale) vs majority (safe) vs linearizable

DOCUMENT STRUCTURE MATTERS:
  Each document is a BSON blob. Field names stored with every document.
  "user_name" in 1M documents = 1M copies of "user_name"
  → Use short field names if storage matters (u vs user_name)

WHEN MONGODB BEATS POSTGRESQL:
  - Schema evolution (add/remove fields without migrations)
  - Hierarchical data (avoid joins, embed subdocuments)
  - Horizontal write scaling (sharding built-in)
  - Development velocity (no schema upfront)

WHEN POSTGRESQL BEATS MONGODB:
  - Multi-document ACID (complex transactions)
  - Ad-hoc queries (SQL > aggregation pipeline for complex queries)
  - Storage efficiency (normalized > denormalized)
  - Reporting and analytics
```

---

### 1.4 NoSQL — Key-Value Stores

**Redis — Deep Internals:**
```
DATA STRUCTURES AND WHEN TO USE THEM:

  String (SET/GET):
    - Simple caching, counters (INCR)
    - TTL-based expiration
    - SETNX for distributed locks

  Hash (HSET/HGET):
    - User sessions, partial object updates
    - Don't fetch entire object to update one field

  List (LPUSH/RPUSH/LRANGE):
    - Activity feeds, message queues
    - Bounded lists (LTRIM to cap size)

  Set (SADD/SMEMBERS):
    - Unique visitor tracking
    - Tag systems, friendship graphs

  Sorted Set (ZADD/ZRANGE):
    - Leaderboards (score-based ranking)
    - Rate limiting (sliding window with ZRANGEBYSCORE)
    - Priority queues

  Stream (XADD/XREAD):  [Redis 5+]
    - Persistent message queues, event sourcing
    - Consumer groups, exactly-once semantics

PERSISTENCE OPTIONS:
  RDB (Redis Database):
    - Point-in-time snapshot
    - Fast restart, but data loss between snapshots
    - Good for caching (data loss acceptable)

  AOF (Append-Only File):
    - Log every write operation
    - fsync every second (default) or every write
    - Larger file, slower startup, near-zero data loss
    - Good for session storage

  RDB + AOF: Best of both worlds for critical data

MEMORY MANAGEMENT:
  maxmemory 4gb
  maxmemory-policy allkeys-lru   # Evict LRU keys when full
  # Policies: allkeys-lru, volatile-lru, allkeys-lfu, volatile-ttl

REDIS CLUSTER:
  - Shards data across 16384 hash slots
  - Minimum 3 masters + 3 replicas for HA
  - Hash tags: {user}:123 and {user}:456 → same slot (allows MGET)
  - Limitation: no transactions across different keys in different slots
```

**Redis Distributed Lock (Redlock algorithm):**
```java
@Service
public class RedisDistributedLock {
    private final StringRedisTemplate redis;
    private static final String LOCK_KEY = "lock:";
    private static final long LOCK_TTL_MS = 10_000;

    public Optional<String> acquire(String resource) {
        String lockToken = UUID.randomUUID().toString();
        Boolean acquired = redis.opsForValue().setIfAbsent(
            LOCK_KEY + resource,
            lockToken,
            Duration.ofMillis(LOCK_TTL_MS)
        );
        return Boolean.TRUE.equals(acquired) ? Optional.of(lockToken) : Optional.empty();
    }

    public boolean release(String resource, String lockToken) {
        // Atomic: check token AND delete (Lua script prevents race condition)
        String script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """;
        Long result = redis.execute(
            new DefaultRedisScript<>(script, Long.class),
            List.of(LOCK_KEY + resource),
            lockToken
        );
        return Long.valueOf(1L).equals(result);
    }
}
```

---

### 1.5 NoSQL — Wide-Column Stores

**Apache Cassandra — Internals That Matter in Production:**
```
DATA MODEL: Partition Key + Clustering Key
  CREATE TABLE user_events (
      user_id  UUID,
      event_ts TIMESTAMP,
      event    TEXT,
      PRIMARY KEY (user_id, event_ts)  -- user_id = partition, event_ts = clustering
  );

  PARTITION KEY: determines which node stores the data (hash ring)
  CLUSTERING KEY: physical sort order within partition

  ALL QUERIES MUST INCLUDE PARTITION KEY → no full-table scans!
  Cassandra is purpose-built for specific access patterns, not ad-hoc queries.

LSM TREE IN CASSANDRA:
  Write path:
    1. Write to CommitLog (WAL equivalent, for crash recovery)
    2. Write to Memtable (in-memory)
    3. Memtable full → flush to SSTable (immutable, sorted by partition key)
    4. SSTables accumulate → compaction merges them

  Read path:
    1. Check Bloom filter (is this key possibly in this SSTable?)
    2. Check SSTable index summary
    3. Read data from SSTable
    → More SSTables = slower reads = why compaction matters

TOMBSTONES: The Hidden Performance Killer
  DELETE in Cassandra = insert a tombstone marker (not actual deletion)
  Tombstones accumulate until compaction GC grace period (default 10 days)
  Too many tombstones → ReadTimeoutException
  Fix: design to avoid deletes, or tune GC grace period

CONSISTENCY LEVELS:
  ONE   → fastest, weakest (any one replica responds)
  QUORUM → majority (reads latest write if W+R > N)
  ALL    → slowest, strongest (all replicas must respond)
  LOCAL_QUORUM → quorum within local DC (for multi-region)

Real production setting: W=LOCAL_QUORUM + R=LOCAL_QUORUM → strong consistency
```

---

### 1.6 Elasticsearch — Inverted Index Internals

```
HOW FULL-TEXT SEARCH ACTUALLY WORKS:
  Document: "Spring Boot makes Java easy"
  Analysis: tokenize → lowercase → remove stop words → stem
  Tokens:   ["spring", "boot", "make", "java", "easi"]

  Inverted Index:
    "spring"  → [doc1, doc5, doc12]
    "boot"    → [doc1, doc3]
    "java"    → [doc1, doc7, doc9]
    "easi"    → [doc1, doc15]

  Query: "Spring Java" → find intersection([doc1,doc5,doc12], [doc1,doc7,doc9])
  Result: [doc1] — fastest possible lookup, regardless of corpus size

SEGMENT ARCHITECTURE:
  Each shard has multiple segments (immutable, like Cassandra SSTables)
  Writes go to in-memory buffer → refresh (every 1s by default) → segment
  Segment merge: background process (expensive, tune carefully)

  Near-real-time: data searchable after refresh (~1 second delay)
  Not real-time: suitable for search, not for financial ledgers

MAPPING: Define field types upfront (or face surprises)
  {
    "mappings": {
      "properties": {
        "name":       { "type": "text" },        // analyzed, searchable
        "email":      { "type": "keyword" },      // exact match only
        "created_at": { "type": "date" },
        "price":      { "type": "scaled_float", "scaling_factor": 100 }
      }
    }
  }

  KEYWORD vs TEXT:
    TEXT:    "Spring Boot" → ["spring", "boot"] → full-text search
    KEYWORD: "Spring Boot" → "Spring Boot" → exact match, sorting, aggregations
```

---

### 1.7 Time-Series Databases (InfluxDB, TimescaleDB)

```
WHY REGULAR DATABASES FAIL FOR TIME-SERIES:
  Problem: IoT sensor emits 1000 readings/second
           Standard table grows 86M rows/day
           PostgreSQL heap: random I/O on inserts (index maintenance)
           Result: write latency climbs, index bloat

INFLUXDB INTERNALS:
  Data model: measurement + tags + fields + timestamp
  Storage:    TSM (Time-Structured Merge) — like LSM but time-ordered
  Compression: delta encoding on timestamps (only store differences)
               XOR encoding on float values (GORILLA algorithm, ~12x compression)
  Retention:  Automatically delete data older than policy

TIMESCALEDB INTERNALS:
  Built on PostgreSQL (hypertable = partitioned by time automatically)
  Each chunk = one time range (e.g., 1 day) = one PostgreSQL table
  Queries: only scan relevant time chunks (not entire table)
  INSERT performance: always writing to current chunk = sequential I/O
  Compression: columnar storage on cold chunks (10-100x compression)

  -- Create hypertable
  CREATE TABLE sensor_data (
      time         TIMESTAMPTZ NOT NULL,
      sensor_id    INTEGER,
      temperature  DOUBLE PRECISION
  );
  SELECT create_hypertable('sensor_data', 'time');
  -- TimescaleDB handles the rest
```

---

## 2. Merits and Demerits — Decision Matrix

### 2.1 Full Comparison Table

| Dimension | PostgreSQL | MongoDB | Redis | Cassandra | Elasticsearch | InfluxDB |
|---|---|---|---|---|---|---|
| **Consistency** | Strong (ACID) | Eventual/Configurable | Eventual | Tunable (ONE→ALL) | Eventual | Eventual |
| **Scalability** | Vertical (read replicas) | Horizontal | Horizontal (cluster) | Horizontal | Horizontal | Horizontal |
| **Write throughput** | 10K-100K TPS | 20K-200K TPS | 100K-1M TPS | 100K-500K TPS | 10K-50K TPS | 500K+ TPS |
| **Read throughput** | 10K-100K QPS | 10K-100K QPS | 100K-1M QPS | 50K-200K QPS | 10K-100K QPS | 100K QPS |
| **Write latency** | 1-5ms | 1-3ms | <1ms | 1-2ms | 5-50ms | <1ms |
| **Read latency (p99)** | 5-20ms | 5-20ms | 1-5ms | 5-20ms | 10-100ms | 5-50ms |
| **Storage efficiency** | High | Medium | Low (memory) | Medium | Low | High (compression) |
| **Multi-record txn** | ✓ Full ACID | ✓ (4.0+ multi-doc) | Partial (MULTI) | ✗ | ✗ | ✗ |
| **Query flexibility** | Excellent (SQL) | Good (aggregation) | Poor | Poor | Full-text | Time-based |
| **Operational complexity** | Low | Medium | Low (single) / High (cluster) | High | High | Medium |
| **Best use case** | OLTP, complex queries | Flexible schema, hierarchical | Caching, sessions | High-volume events | Search, analytics | Metrics, monitoring |

### 2.2 Deep Trade-off Analysis

**When PostgreSQL is the Wrong Choice:**
```
❌ You're writing 500K events/second from IoT sensors
   → PostgreSQL MVCC + WAL cannot sustain this write rate
   → Use InfluxDB or Cassandra

❌ You need to serve 100M users with sub-10ms reads across 50 regions
   → PostgreSQL vertical scaling doesn't get you there
   → Use DynamoDB Global Tables or Cassandra multi-region

❌ Your data is a graph (users → friends → groups → events)
   → Joins get exponentially slower (6-hop traversal = 6 joins)
   → Use Neo4j (traversal = O(1) per hop regardless of graph size)
```

**When MongoDB is the Wrong Choice:**
```
❌ You need to transfer funds between accounts (multi-document ACID)
   → MongoDB multi-document transactions are possible (4.0+) but slow
   → Use PostgreSQL (born for this)

❌ Your access patterns are unknown (reporting database)
   → MongoDB aggregation pipeline < SQL flexibility for ad-hoc queries
   → Use PostgreSQL

❌ You have tight storage budget and 100% known schema
   → Denormalization = storage waste, BSON overhead per document
   → Use PostgreSQL (normalized = efficient)
```

**When Redis is the Wrong Choice:**
```
❌ Primary data store with 500GB dataset
   → All data in RAM = $50K+ hardware cost
   → Use as cache layer over PostgreSQL, not primary store

❌ You need complex queries over your cached data
   → Redis is key lookups, not query engine
   → Keep queryable data in PostgreSQL

❌ Data loss is unacceptable (financial records)
   → Redis AOF + fsync=always still loses data if power fails during write
   → Use PostgreSQL with synchronous_commit=on
```

---

## 3. When to Use Which Database — Decision Framework

### 3.1 Decision Tree

```
START: What's your primary constraint?

├─ CORRECTNESS (data must be right, transactions required)
│  └─ PostgreSQL or MySQL
│     Consider: Are you OK with schema migrations on every deploy?
│     Alternative: PostgreSQL with JSONB columns for flexible parts
│
├─ SCALE (millions of writes/second, global distribution)
│  ├─ Write-heavy → Cassandra or DynamoDB
│  ├─ Read-heavy → Redis cache + PostgreSQL/MongoDB
│  └─ Both → CQRS: Cassandra for writes, Elasticsearch for reads
│
├─ SEARCH (users need to search text, not exact lookup)
│  └─ Elasticsearch (primary search) + database (source of truth)
│
├─ SPEED (sub-millisecond latency required)
│  └─ Redis (always, for caching/sessions/counters)
│
├─ FLEXIBLE SCHEMA (schema changes weekly, startup pace)
│  └─ MongoDB, but plan for eventual migration to PostgreSQL as schema stabilizes
│
└─ TIME-SERIES (metrics, IoT, monitoring)
   └─ InfluxDB or TimescaleDB (not regular tables — they won't scale)
```

### 3.2 Scenario-Based Selection With Architectural Rationale

**Scenario 1: E-Commerce Platform (10M users, 100K orders/day)**
```
System            Database          Rationale
──────────────────────────────────────────────────────────────────
Product catalog   PostgreSQL        SKU data is structured, need ACID on inventory
Inventory         PostgreSQL        Stock deduction = transaction, can't lose writes
Orders            PostgreSQL        Financial records, multi-row ACID essential
User sessions     Redis             Read every request, can be recreated if lost
Product search    Elasticsearch     Full-text, facets, autocomplete
Price history     TimescaleDB       Time-series, analytics, append-only
Recommendations   Neo4j             "customers also bought" = graph traversal
Audit trail       PostgreSQL        Append-only, needs SQL reporting
Cache             Redis             Category pages, product pages (TTL=10min)
Analytics         Redshift/BigQuery OLAP queries on large datasets
```

**Scenario 2: Global Social Media (500M users, 5B writes/day)**
```
System            Database          Rationale
──────────────────────────────────────────────────────────────────
User profiles     Cassandra         Simple reads by user_id, high availability
Feed posts        Cassandra         High write volume, time-ordered by user
Notifications     Redis Streams     Real-time delivery, ephemeral
Search            Elasticsearch     Full-text search, hashtags
Graph (follows)   Cassandra         Fan-out on write (precompute timelines)
Auth tokens       Redis             Sub-millisecond auth on every request
Media metadata    MongoDB           Flexible (photos vs videos vs stories)
Metrics/Analytics InfluxDB          Engagement metrics per second
```

**Scenario 3: Financial Trading Platform (extreme consistency)**
```
System            Database          Rationale
──────────────────────────────────────────────────────────────────
Positions         PostgreSQL        ACID, exact balances, audit trail required
Trade history     PostgreSQL        Immutable, append-only, strict consistency
Market data       TimescaleDB       Tick data, millions of prices/second
Order book        Redis             Sub-millisecond lookups, sorted sets
Risk engine       PostgreSQL        Complex queries, real-time risk calc
Compliance        PostgreSQL        SQL reporting, exact audit trail
```

---

## 4. Database Performance Metrics — What Actually Matters

### 4.1 Latency — The Real Story

Most developers look at average latency. Wrong metric.

```
AVERAGE LATENCY IS LYING TO YOU:
  Requests: [1ms, 1ms, 1ms, 1ms, 1000ms]
  Average = 200ms
  But 80% of requests finished in 1ms!

USE PERCENTILES:
  p50:  50% complete within this time (median)
  p95:  95% complete within this time
  p99:  99% complete within this time
  p999: 99.9% complete within this time (1 in 1000)

TYPICAL DATABASE LATENCY TARGETS:
  Operation             p50     p95     p99    p999
  ──────────────────────────────────────────────────
  Redis GET             <1ms    1ms     2ms    5ms
  PostgreSQL PK lookup  1ms     5ms     20ms   100ms
  PostgreSQL complex    5ms     50ms    200ms  1000ms
  MongoDB doc find      1ms     5ms     20ms   100ms
  Cassandra partition   2ms     10ms    30ms   200ms
  Elasticsearch query   5ms     50ms    200ms  1000ms

IF YOUR P99 IS HIGH, INVESTIGATE:
  - Lock contention (SHOW PROCESSLIST / pg_stat_activity)
  - Missing index (full table scan on p99 spike)
  - GC pause (database's own GC, not Java's)
  - Disk I/O saturation (iostat, check await time)
  - Connection pool exhaustion (HikariCP metrics)
```

**Measuring in Spring Boot (Micrometer):**
```java
@Configuration
public class DataSourceMetricsConfig {
    @Bean
    public DataSource dataSource(MeterRegistry meterRegistry) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setMetricRegistry(meterRegistry);  // Exposes pool metrics
        return new HikariDataSource(config);
    }
}

// Custom query timing
@Repository
public class ProductRepository {
    private final JdbcTemplate jdbc;
    private final Timer queryTimer;

    public ProductRepository(JdbcTemplate jdbc, MeterRegistry registry) {
        this.jdbc = jdbc;
        this.queryTimer = Timer.builder("db.query")
            .tag("query", "findActiveProducts")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
    }

    public List<Product> findActive() {
        return queryTimer.recordCallable(() ->
            jdbc.query("SELECT * FROM products WHERE status='ACTIVE'",
                       new BeanPropertyRowMapper<>(Product.class))
        );
    }
}
```

### 4.2 Throughput — Where Bottlenecks Hide

```
THROUGHPUT BOTTLENECK ANALYSIS:

  Application → Connection Pool → Database → Disk/Network

  1. CONNECTION POOL EXHAUSTION (most common bottleneck)
     Symptom: HikariCP.pendingThreads > 0, latency spikes
     Fix: Increase pool size OR reduce query latency

     Formula: pool size = (core_count * 2) + effective_spindle_count
     For 8-core server with SSD: pool = (8 * 2) + 1 = 17
     For 8-core server with HDD: pool = (8 * 2) + 4 = 20

     Rule: Don't just add connections. More connections = more context switches.
     PostgreSQL with 500 connections = worse than 50 connections + PgBouncer.

  2. LOCK CONTENTION
     Symptom: pg_stat_activity shows many 'waiting' transactions
     Fix: Reduce transaction size, use SKIP LOCKED for queue processing

  3. I/O SATURATION
     Symptom: iostat shows await > 10ms, %util > 80%
     Fix: Add read replicas, tune buffer pool size, upgrade to faster disk

  4. CPU SATURATION (rarely the bottleneck in databases)
     Symptom: CPU > 90%, query plan shows sequential scans
     Fix: Add index, tune query plan
```

### 4.3 The Reliability Metrics That Actually Matter

```
RECOVERY OBJECTIVES (define these before any incident, not during):
  RTO (Recovery Time Objective): How long before service is restored?
    Critical (payment processing): < 1 minute
    Standard (content delivery): < 15 minutes
    Low priority (reporting): < 4 hours

  RPO (Recovery Point Objective): How much data loss is acceptable?
    Financial systems: 0 (synchronous replication required)
    User data: < 1 minute (asynchronous replication acceptable)
    Analytics: < 1 hour (periodic backups acceptable)

REPLICATION LAG — Track this in production:
  -- PostgreSQL: check replica lag
  SELECT client_addr,
         write_lag,
         flush_lag,
         replay_lag
  FROM pg_stat_replication;

  -- Alert if replay_lag > 30 seconds (replica is falling behind)

CACHE HIT RATIO — Directly impacts latency:
  Target: > 95% for cache-aside, > 99% for write-through
  PostgreSQL buffer pool hit rate:
  SELECT blks_hit * 100.0 / (blks_hit + blks_read) AS hit_rate
  FROM pg_stat_database
  WHERE datname = current_database();

  If hit rate < 90% → increase shared_buffers or move hot data to Redis
```

---

## 5. Read-Write Synchronization — Consistency Internals

### 5.1 Isolation Levels — What They Actually Prevent

```
READ PHENOMENA:
  Dirty Read:        Read uncommitted data from another transaction
  Non-repeatable:    Same query returns different rows within same transaction
  Phantom Read:      New rows appear in range query within same transaction
  Lost Update:       Two transactions both update based on same read

ISOLATION LEVEL vs PHENOMENON:
  Level               Dirty  Non-rep  Phantom  Lost Update
  ─────────────────────────────────────────────────────────
  READ UNCOMMITTED    ✓      ✓        ✓        ✓  (worst)
  READ COMMITTED      ✗      ✓        ✓        ✓  (default PostgreSQL = READ COMMITTED)
  REPEATABLE READ     ✗      ✗        ✓        ✗  (default MySQL InnoDB)
  SERIALIZABLE        ✗      ✗        ✗        ✗  (safest, slowest)

PRACTICAL GUIDANCE:
  READ COMMITTED:     Fine for most application reads (reports, lookups)
  REPEATABLE READ:    Use when you read → compute → write based on same data
                      (e.g., inventory check + reserve)
  SERIALIZABLE:       Bank transfers, financial operations
                      Cost: 3-5x slower than READ COMMITTED

  DON'T BLINDLY USE SERIALIZABLE:
  E-commerce checkout flow on SERIALIZABLE → dramatic throughput drop
  Better: use READ COMMITTED + application-level optimistic locking
```

**Setting isolation in Spring:**
```java
// Per-transaction isolation
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void reserveInventory(String productId, int quantity) {
    Product product = productRepository.findById(productId);
    // Same read within this transaction always returns same value
    if (product.getStock() >= quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}

// Optimistic locking (better than SERIALIZABLE for high-concurrency)
@Entity
public class Product {
    @Id
    private String id;
    private int stock;

    @Version
    private Long version;  // JPA handles optimistic lock automatically
}

// Service with retry
@Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3,
           backoff = @Backoff(delay = 50, multiplier = 2))
@Transactional
public void reserveInventory(String productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
    if (product.getStock() < quantity) {
        throw new InsufficientStockException(productId, quantity);
    }
    product.setStock(product.getStock() - quantity);
    // If another transaction updated product.version, JPA throws
    // OptimisticLockingFailureException → @Retryable retries
    productRepository.save(product);
}
```

### 5.2 Pessimistic vs Optimistic Locking — Production Decision

```
PESSIMISTIC LOCKING:
  Strategy: Lock the row before reading it (SELECT FOR UPDATE)
  Best for: HIGH CONTENTION — same rows updated frequently
            Example: bank account debited many times/second

  PROS:
    - Guaranteed no conflicts
    - Simpler error handling (no retry logic)

  CONS:
    - Locks held during computation (other transactions wait)
    - Deadlock risk if acquiring locks in different orders
    - Poor performance under high concurrency

OPTIMISTIC LOCKING:
  Strategy: Read freely, check version on write (fail if changed)
  Best for: LOW CONTENTION — conflicts are rare
            Example: user updates their own profile

  PROS:
    - No locks held = high concurrency
    - No deadlocks

  CONS:
    - Retry logic needed
    - Under high contention: many retries = thundering herd

DECISION RULE:
  Collision rate < 5%  → Optimistic (better concurrency)
  Collision rate > 20% → Pessimistic (retries cost more than locks)
  Use metrics to decide: count OptimisticLockingFailureExceptions/second
```

### 5.3 The SELECT FOR UPDATE Pattern (Skip Locked for Queues)

```java
// Standard queue processing — one record at a time
@Transactional
public Optional<Job> pollNextJob() {
    return jobRepository.findFirstByStatusOrderByCreatedAt(
        JobStatus.PENDING
    );  // ← PROBLEM: multiple workers pick same job = duplicates
}

// CORRECT: Use SKIP LOCKED for concurrent workers
@Transactional
public Optional<Job> pollNextJob() {
    // Spring Data JPA @Lock + SKIP LOCKED
    return jobRepository.findFirstAvailableJob();
}

// Repository:
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query(value = """
        SELECT * FROM jobs
        WHERE status = 'PENDING'
        ORDER BY created_at ASC
        LIMIT 1
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    Optional<Job> findFirstAvailableJob();
}
// SKIP LOCKED: skip rows already locked by other workers
// Result: 10 workers can each process different jobs concurrently
// No duplicate processing, no blocking
```

### 5.4 Event Sourcing — When the Audit Trail IS the Database

```java
// Traditional: store current state (mutable)
// Problem: "What was the balance 3 days ago?" → impossible

// Event Sourcing: store events (immutable), derive state
@Entity
public class AccountEvent {
    @Id
    @GeneratedValue
    private Long id;
    private String accountId;
    private String type;       // OPENED, CREDITED, DEBITED, CLOSED
    private BigDecimal amount;
    private Instant occurredAt;
    // Immutable — never UPDATE this table
}

@Service
public class AccountService {
    private final AccountEventRepository eventRepo;

    // Write: append-only
    @Transactional
    public void credit(String accountId, BigDecimal amount) {
        eventRepo.save(new AccountEvent(accountId, "CREDITED", amount, Instant.now()));
    }

    // Read: replay events (or use materialized snapshot for performance)
    public BigDecimal getBalance(String accountId) {
        return eventRepo.findByAccountIdOrderByOccurredAt(accountId)
            .stream()
            .reduce(BigDecimal.ZERO, (balance, event) -> switch (event.getType()) {
                case "CREDITED" -> balance.add(event.getAmount());
                case "DEBITED"  -> balance.subtract(event.getAmount());
                default          -> balance;
            });
    }

    // Point-in-time query (impossible with mutable state)
    public BigDecimal getBalanceAt(String accountId, Instant at) {
        return eventRepo.findByAccountIdAndOccurredAtBefore(accountId, at)
            .stream()
            .reduce(BigDecimal.ZERO, (balance, event) -> switch (event.getType()) {
                case "CREDITED" -> balance.add(event.getAmount());
                case "DEBITED"  -> balance.subtract(event.getAmount());
                default          -> balance;
            });
    }
}
```

---

## 6. Read-Heavy vs Write-Heavy — Architecture Patterns

### 6.1 Understanding Your Workload First

```
BEFORE CHOOSING A PATTERN, MEASURE YOUR WORKLOAD:

  # PostgreSQL: check read vs write ratio
  SELECT
    tup_returned + tup_fetched AS reads,
    tup_inserted + tup_updated + tup_deleted AS writes,
    ROUND(100.0 * (tup_returned + tup_fetched) /
          NULLIF(tup_returned + tup_fetched + tup_inserted + tup_updated + tup_deleted, 0), 2) AS read_pct
  FROM pg_stat_user_tables
  WHERE relname = 'products';

CLASSIFY YOUR WORKLOAD:
  > 90% reads:  Read-heavy → caching, read replicas, denormalization
  > 90% writes: Write-heavy → Cassandra, batching, append-only
  Mixed:        CQRS, or tuned PostgreSQL with careful indexing
```

### 6.2 Read-Heavy Architecture (90% reads, 10% writes)

```
LAYER 1: CDN (static content, edge caching)
    │
    ▼
LAYER 2: Application Cache (in-memory, per-JVM)
  - Caffeine for local cache (microsecond latency)
  - 1000-10000 entries, 30-60 second TTL
    │
    ▼
LAYER 3: Distributed Cache (Redis)
  - Shared across all application instances
  - 100K-1M entries, minutes-to-hours TTL
    │
    ▼
LAYER 4: Read Replicas
  - 2-5 PostgreSQL/MySQL replicas
  - Route 80-90% of reads to replicas
    │
    ▼
LAYER 5: Primary Database (writes + critical reads)
```

**Multi-layer cache in Spring:**
```java
@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisFactory) {
        // L1: Caffeine (local, per-JVM, fastest)
        CaffeineCacheManager caffeine = new CaffeineCacheManager();
        caffeine.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofSeconds(30))
            .recordStats());

        // L2: Redis (distributed, shared, slower)
        RedisCacheManager redis = RedisCacheManager.builder(redisFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)))
            .build();

        // Compose: check L1 first, then L2, then database
        return new CompositeCacheManager(caffeine, redis);
    }
}

@Service
public class ProductService {

    @Cacheable(cacheNames = "products", key = "#id",
               unless = "#result == null")
    public Product getProduct(String id) {
        return productRepository.findById(id)  // only called on cache miss
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @CacheEvict(cacheNames = "products", key = "#product.id")
    @Transactional
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
}
```

**Read Replica Routing in Spring:**
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("primaryDs") DataSource primary,
            @Qualifier("replicaDs") DataSource replica) {
        return new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                // ReadOnlyTransactionTemplate → route to replica
                return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                    ? "replica" : "primary";
            }
            { setTargetDataSources(Map.of("primary", primary, "replica", replica));
              setDefaultTargetDataSource(primary); }
        };
    }
}

// Usage: @Transactional(readOnly = true) → replica
//        @Transactional → primary
@Service
public class ReportService {
    @Transactional(readOnly = true)  // Routes to replica
    public List<SalesReport> generateReport(LocalDate from, LocalDate to) {
        return reportRepository.findSalesBetween(from, to);
    }
}
```

### 6.3 Write-Heavy Architecture (90% writes, 10% reads)

```
WRITE PATH OPTIMIZATION TECHNIQUES:

1. ASYNC WRITES (Kafka decoupling)
   HTTP Request → Kafka → Consumer → Database
   - Response time: ~5ms (just Kafka publish)
   - Database throughput: consumer batches writes
   - Risk: writes may be delayed

2. BATCH WRITES
   100 individual inserts:    100ms total
   1 batch insert of 100:     5ms total   (20x faster)

3. WRITE-OPTIMIZED STORAGE (Cassandra LSM)
   All writes sequential → no random I/O → constant write latency

4. PARTITIONING BY TIME
   INSERT always goes to current partition → sequential I/O
```

**High-throughput write service with Kafka:**
```java
@Service
public class EventIngestionService {
    private final KafkaTemplate<String, Event> kafka;

    // Fire-and-forget write: 5ms response time
    public CompletableFuture<Void> publishEvent(Event event) {
        return kafka.send("events", event.getUserId(), event)
            .thenApply(result -> null);
    }
}

// Consumer: batch-write to database
@Component
public class EventConsumer {
    private final EventRepository repository;

    @KafkaListener(topics = "events",
                   containerFactory = "batchListenerFactory")
    public void consumeBatch(List<Event> events) {
        // 1000 events in one batch insert → 10-50ms vs 1000 individual inserts
        repository.saveAll(events);
    }
}

// Kafka config for batch consumption
@Bean
public ConcurrentKafkaListenerContainerFactory<String, Event> batchListenerFactory(
        ConsumerFactory<String, Event> consumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Event>();
    factory.setConsumerFactory(consumerFactory);
    factory.setBatchListener(true);
    factory.getContainerProperties().setPollTimeout(3000);
    return factory;
}
```

### 6.4 CQRS — Command Query Responsibility Segregation

```
WHEN TO USE CQRS:
  - Write model optimized for consistency (PostgreSQL, strong ACID)
  - Read model optimized for query performance (Elasticsearch, Redis, MongoDB)
  - Different scaling requirements (write: 1K TPS, read: 100K QPS)

CQRS ARCHITECTURE:
  Command (write) side:
    Client → Command Handler → Domain Model → Write Store (PostgreSQL)
                                              → Publish Event (Kafka)

  Query (read) side:
    Client → Query Handler ← Read Model (Elasticsearch/Redis/MongoDB)
                              ↑
                         Event Processor (subscribes to Kafka, updates read model)
```

```java
// WRITE SIDE: domain model with business rules
@Service
public class OrderCommandService {
    private final OrderRepository orderRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(PlaceOrderCommand cmd) {
        Order order = Order.place(cmd.userId(), cmd.items());  // domain logic
        Order saved = orderRepo.save(order);
        eventPublisher.publishEvent(new OrderPlacedEvent(saved));
        return saved;
    }
}

// READ SIDE: denormalized for fast queries
@Component
public class OrderProjection {
    private final ElasticsearchTemplate esTemplate;

    @EventListener
    public void on(OrderPlacedEvent event) {
        // Denormalize for search
        OrderDocument doc = new OrderDocument(
            event.getOrderId(),
            event.getUserName(),       // denormalized from User
            event.getItemNames(),      // denormalized from Products
            event.getTotal(),
            event.getStatus()
        );
        esTemplate.save(doc);
    }
}

// QUERY SIDE: fast reads from Elasticsearch
@Service
public class OrderQueryService {
    private final ElasticsearchTemplate esTemplate;

    public List<OrderSummary> searchOrders(String userId, String keyword) {
        // Sub-10ms search across millions of orders
        var query = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(termQuery("userId", userId))
                .must(matchQuery("itemNames", keyword)))
            .build();
        return esTemplate.search(query, OrderDocument.class)
            .stream()
            .map(SearchHit::getContent)
            .map(this::toSummary)
            .toList();
    }
}
```

---

## 7. Database Replication — Internals and Failure Modes

### 7.1 How PostgreSQL Streaming Replication Works Internally

```
PRIMARY → writes WAL records → WAL sender process
  ↓
NETWORK (WAL records streamed continuously)
  ↓
REPLICA → WAL receiver process → WAL file → recovery process → data files

SYNCHRONIZATION MODES:
  async (default):   Primary doesn't wait for replica ACK
                     Replication lag: 0-500ms (depends on network)
                     Risk: failover = data loss (WAL not yet replicated)

  synchronous:       Primary waits for ONE replica to ACK before committing
    synchronous_commit = on     → WAL flushed to disk on replica
    synchronous_commit = remote_write → WAL received by replica (not flushed)
    synchronous_commit = remote_apply → WAL applied (readable on replica)
    Cost: every commit waits for network round trip (~2-5ms extra)

  When to use synchronous: financial systems, zero RPO requirement
  When to use async:        most web applications (accept ~500ms RPO)
```

### 7.2 Replication Topologies

**Primary-Replica:**
```
PRIMARY (read-write)
    │
    ├─► REPLICA-1 (read-only, async)   ← normal reads
    ├─► REPLICA-2 (read-only, async)   ← normal reads
    └─► REPLICA-3 (read-only, sync)    ← failover candidate

FAILOVER PROCEDURE (with Patroni/PgBouncer):
  1. Detect primary failure (health check, 30 second timeout)
  2. Elect new primary (synchronous replica preferred)
  3. Promote replica: pg_ctl promote
  4. Update PgBouncer config to point to new primary
  5. Remaining replicas follow new primary

RTO: ~30-60 seconds (automated), ~5-15 minutes (manual)
RPO: 0 (sync replica), up to replication lag (async replica)
```

**Cassandra Multi-Region (Active-Active):**
```
REGION: US-EAST                    REGION: EU-WEST
  Node 1 ──────────────────────────── Node 4
  Node 2 ──────────────────────────── Node 5
  Node 3 ──────────────────────────── Node 6

Replication factor: 6 (3 per DC)
Write: LOCAL_QUORUM → write to 2 of 3 nodes in local DC, then async to other DC
Read:  LOCAL_QUORUM → read from 2 of 3 nodes in local DC (fast, local)

USER IN US: reads from US nodes (~1ms), writes replicated async to EU
USER IN EU: reads from EU nodes (~1ms), writes replicated async to US

CONFLICT RESOLUTION: Last-Write-Wins (LWW) based on timestamp
  → Cassandra uses server-side timestamps
  → Concurrent writes from different DCs: higher timestamp wins
  → Never rely on client-side timestamps for conflict resolution
```

### 7.3 Replication Lag — Production Handling

```
THE PROBLEM IN REAL TERMS:
  User submits order → primary DB updated
  User immediately checks order status → reads from replica
  Replica lagging 200ms → "Order not found" → user confused

SOLUTIONS IN ORDER OF PREFERENCE:

1. SESSION CONSISTENCY (best UX, low cost)
   After a write, route subsequent reads to PRIMARY for 2-5 seconds
```

```java
@Service
public class SessionConsistencyService {
    private final UserRepository primaryRepo;
    private final UserRepository replicaRepo;
    private final RedisTemplate<String, Long> redis;

    @Transactional
    public User updateUser(User user) {
        User saved = primaryRepo.save(user);
        // Mark this user as "recently written" for 5 seconds
        redis.opsForValue().set("written:" + user.getId(), 1L, Duration.ofSeconds(5));
        return saved;
    }

    public User getUser(String userId) {
        // If recently written, read from primary (avoids stale read)
        boolean recentlyWritten = Boolean.TRUE.equals(
            redis.hasKey("written:" + userId));
        return recentlyWritten
            ? primaryRepo.findById(userId).orElseThrow()
            : replicaRepo.findById(userId).orElseThrow();
    }
}
```

```
2. REPLICA LAG MONITORING + ALERTS
   Alert if replication lag > 10 seconds (something is wrong)
   Circuit-break reads to replica if lag > threshold

3. SYNCHRONOUS REPLICATION FOR CRITICAL PATHS
   Orders, payments → synchronous_commit = remote_apply
   Profile reads → async replica
```

### 7.4 The Split-Brain Problem

```
SPLIT-BRAIN: Network partition causes both nodes to think they're primary

  Region A          Network           Region B
  [Primary]  ──── PARTITION ────  [Old Replica]
     ↓                                  ↓
 Accepts writes                    Promotes to primary
     ↓                                  ↓
 User 1 writes X=1             User 2 writes X=2

  When partition heals: TWO conflicting values for X!

PREVENTION:
  1. Odd number of nodes + majority quorum (Raft, Paxos)
     Can't elect primary unless majority nodes agree
     Loss of half nodes = read-only mode (safe, not split-brained)

  2. Fencing tokens
     New primary gets a higher token
     Old primary's writes rejected if token < current

  3. STONITH (Shoot The Other Node In The Head)
     Forcibly kill old primary before promoting replica
     Extreme but reliable (used in Kubernetes operator patterns)
```

---

## 8. Caching Strategies — Patterns and Production Pitfalls

### 8.1 The Four Caching Patterns (and when each one hurts you)

**Cache-Aside (Lazy Loading) — Most Common:**
```
Read path:   App → Redis (miss) → DB → populate Redis → return
Write path:  App → DB (only, no cache update)

PROS:
  - Cache only contains requested data (no waste)
  - DB failure doesn't affect cache reads (if data is already cached)
  - Fine-grained control over what gets cached

CONS:
  - Cache miss = 3 operations (cache + DB + write-back) = high latency on cold start
  - Thundering herd: cache expires, 1000 simultaneous requests all hit DB
  - Stale data window = TTL length
```

```java
@Service
public class ProductCacheService {
    private final RedisTemplate<String, Product> redis;
    private final ProductRepository db;

    public Product get(String id) {
        String key = "product:" + id;
        Product cached = redis.opsForValue().get(key);
        if (cached != null) return cached;

        // Cache miss — add jitter to TTL to prevent stampede
        Product product = db.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        Duration ttl = Duration.ofMinutes(30 + ThreadLocalRandom.current().nextInt(10));
        redis.opsForValue().set(key, product, ttl);
        return product;
    }
}
```

**Write-Through — Strong Consistency:**
```
Write path:  App → DB → Redis (synchronous)
Read path:   App → Redis (hit, always fresh) → DB (miss only)

PROS:
  - Cache always up to date after write
  - Fast reads (always cache hit after first write)

CONS:
  - Write latency = DB write + Redis write (two round trips)
  - Cache may store data never read (wasted memory)
  - Cold start: cache empty until data written at least once
```

```java
@Service
public class WriteThoughProductService {
    @Transactional
    public Product updateProduct(Product product) {
        Product saved = productRepository.save(product);  // DB write first
        redis.opsForValue().set("product:" + saved.getId(), saved,
            Duration.ofHours(1));                         // Then cache update
        return saved;
    }
}
```

**Write-Behind (Write-Back) — Maximum Write Throughput:**
```
Write path:  App → Redis (sync ACK) → async buffer → DB (batched writes)
Read path:   App → Redis

PROS:
  - Sub-millisecond write response time
  - Batch DB writes → high throughput

CONS:
  - Data loss risk: if Redis crashes before flush, writes are lost
  - Complexity: need reliable flush mechanism
  - NOT suitable for financial data

USE CASE: High-volume event counters, analytics aggregation, game scores
```

```java
@Service
public class WriteBehindCounterService {
    private final RedisTemplate<String, Long> redis;
    private final CounterRepository db;

    // Fast write: just increment in Redis
    public void incrementPageView(String pageId) {
        redis.opsForValue().increment("views:" + pageId);
    }

    // Periodic flush to DB (e.g., every 1 minute via @Scheduled)
    @Scheduled(fixedDelay = 60_000)
    public void flushToDb() {
        Set<String> keys = redis.keys("views:*");
        if (keys == null || keys.isEmpty()) return;
        keys.forEach(key -> {
            Long count = redis.opsForValue().getAndDelete(key);
            if (count != null && count > 0) {
                String pageId = key.replace("views:", "");
                db.incrementBy(pageId, count);  // One DB write per key
            }
        });
    }
}
```

**Refresh-Ahead — For Predictable Access Patterns:**
```
Before TTL expires → proactively refresh cache from DB
Goal: near-zero cache miss rate for hot data

PROS:
  - No latency spike on cache miss (data always fresh)
  - Good for data accessed every N seconds

CONS:
  - Requires predicting which keys are hot
  - May refresh stale data no one is reading

USE CASE: Hot products, trending posts, live sports scores
```

### 8.2 Thundering Herd — The Production Killer

```
SCENARIO:
  Product page cached with 30-minute TTL
  1000 users simultaneously hit the page at expiry
  → 1000 cache misses → 1000 DB queries → DB overload → cascading failure

THREE SOLUTIONS:
  1. TTL jitter (easy, partial fix)
     Duration ttl = base + random(0, 10 minutes)
     Different keys expire at different times → spread load

  2. Mutex / Single-flight (correct fix)
     Only ONE goroutine/thread queries DB on cache miss
     Others wait for the first to populate cache
```

```java
@Service
public class SingleFlightCacheService {
    private final RedisTemplate<String, Product> redis;
    private final ProductRepository db;
    private final ConcurrentHashMap<String, CompletableFuture<Product>> inflight
        = new ConcurrentHashMap<>();

    public Product get(String id) throws Exception {
        String key = "product:" + id;
        Product cached = redis.opsForValue().get(key);
        if (cached != null) return cached;

        // Single-flight: deduplicate concurrent DB fetches for same key
        CompletableFuture<Product> pending = new CompletableFuture<>();
        CompletableFuture<Product> existing = inflight.putIfAbsent(key, pending);

        if (existing != null) {
            return existing.get();  // Wait for in-flight request to finish
        }

        try {
            Product product = db.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
            redis.opsForValue().set(key, product, Duration.ofMinutes(30));
            pending.complete(product);
            return product;
        } catch (Exception e) {
            pending.completeExceptionally(e);
            throw e;
        } finally {
            inflight.remove(key);
        }
    }
}
```

### 8.3 Cache Eviction Policies — Production Configuration

```
Redis maxmemory-policy options:

  noeviction:     No eviction. Returns error when memory full.
                  Use: primary data store (never evict)

  allkeys-lru:    Evict least recently used keys (any key)
                  Use: general-purpose cache

  volatile-lru:   Evict LRU keys WITH expiration set
                  Use: mix of persistent + cached data

  allkeys-lfu:    Evict least frequently used keys (Redis 4+)
                  Use: when recency matters less than frequency (popular content)

  volatile-ttl:   Evict key with shortest remaining TTL first
                  Use: when you want soonest-to-expire keys evicted first

PRACTICAL ADVICE:
  Pure cache (all data reconstructable): allkeys-lru or allkeys-lfu
  Mixed (some keys must never evict):    volatile-lru (only evict keys with TTL)
  Session store (lose nothing):          noeviction + Redis persistence
```

---

## 9. Cache-Database Synchronization — The Hard Part

### 9.1 The Classic Race Condition

```
THE DOUBLE-WRITE PROBLEM:
  T1: Update DB (price = $20)
  T2: Update DB (price = $25)
  T2: Update Cache (price = $25)  ← T2 won the DB race
  T1: Update Cache (price = $20)  ← T1 lost the DB race but won the cache race
  
  RESULT: DB has $25, Cache has $20. INCONSISTENT FOREVER (until TTL).
  This happens under concurrent writes. Not theoretical — production reality.
```

```
THE DELETE-VS-UPDATE DEBATE:
  Option A: Update cache on write → race condition risk (above)
  Option B: DELETE (invalidate) cache on write → no stale reads
            Next read is a cache miss → DB lookup → fresh value
  
  WINNER: Cache invalidation (delete) is safer than cache update.
          Facebook's Memcached paper recommends this for most use cases.
```

### 9.2 CDC — Change Data Capture (The Right Way to Sync)

```
CDC: Listen to the database's own replication log for changes
     No application code changes needed
     Guaranteed to capture every change

DEBEZIUM + KAFKA ARCHITECTURE:
  PostgreSQL WAL → Debezium Connector → Kafka Topic → Cache Updater → Redis

  Advantage over application-level sync:
    - Captures ALL changes (including direct DB updates bypassing app)
    - Exactly-once delivery (with Kafka transactions)
    - No performance hit on write path (async)
```

```yaml
# docker-compose snippet: Debezium connector config
connector.class: io.debezium.connector.postgresql.PostgresConnector
database.hostname: postgres
database.port: 5432
database.dbname: mydb
table.include.list: public.products
plugin.name: pgoutput
```

```java
// Kafka consumer: process CDC events and update Redis
@Component
public class ProductCdcConsumer {
    private final RedisTemplate<String, Product> redis;

    @KafkaListener(topics = "dbserver1.public.products")
    public void onProductChange(ConsumerRecord<String, ProductCdcEvent> record) {
        ProductCdcEvent event = record.value();
        String key = "product:" + event.getPayload().getAfter().getId();

        if ("DELETE".equals(event.getPayload().getOp())) {
            redis.delete(key);
        } else {
            // Create (c) or Update (u)
            Product updated = mapToProduct(event.getPayload().getAfter());
            redis.opsForValue().set(key, updated, Duration.ofHours(1));
        }
    }
}
```

### 9.3 Two-Phase Cache Update (Transactional Outbox Pattern)

```
PROBLEM: How to atomically update DB AND fire cache invalidation event?
  - If DB update succeeds but cache invalidation fails → stale cache
  - If cache invalidation fires before DB commit → reads get old data

SOLUTION: Transactional Outbox
  1. Write to DB AND write to outbox table in SAME TRANSACTION
  2. Separate process reads outbox, fires cache invalidation
  → Atomic DB write, guaranteed cache notification (at-least-once)
```

```java
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue
    private Long id;
    private String entityType;   // "PRODUCT"
    private String entityId;
    private String eventType;    // "UPDATED", "DELETED"
    private boolean processed;
    private Instant createdAt;
}

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final OutboxRepository outboxRepo;

    @Transactional  // Single transaction: product + outbox
    public Product updateProduct(Product product) {
        Product saved = productRepo.save(product);
        outboxRepo.save(new OutboxEvent("PRODUCT", product.getId(), "UPDATED"));
        return saved;
        // If transaction rolls back, outbox event also rolls back
        // → Cache invalidation never fires on failed update
    }
}

@Scheduled(fixedDelay = 1000)
@Transactional
public void processOutbox() {
    List<OutboxEvent> pending = outboxRepo.findUnprocessedLimit(100);
    pending.forEach(event -> {
        redis.delete("product:" + event.getEntityId());
        event.setProcessed(true);
    });
    outboxRepo.saveAll(pending);
}
```

### 9.4 Cache Warming Strategies

```
COLD START PROBLEM:
  Deploy new service (or Redis restart) → all cache misses → DB overload

STRATEGIES:

1. PROACTIVE WARM-UP on startup
   Fetch top N hot items from DB, load into cache before serving traffic

2. LAZY WARM-UP with shadow traffic
   Route small % of traffic to new instance before full cutover
   Cache warms gradually

3. SNAPSHOT LOADING
   Persist cache to disk (Redis RDB), restore on restart
   Cache warm within seconds, not minutes
```

```java
@Component
public class CacheWarmer implements ApplicationListener<ApplicationReadyEvent> {
    private final ProductRepository db;
    private final RedisTemplate<String, Product> redis;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Warming cache with top 10K products...");
        db.findTopByOrderBySalesDesc(10_000)
            .forEach(p -> redis.opsForValue().set(
                "product:" + p.getId(), p, Duration.ofHours(1)));
        log.info("Cache warm-up complete");
    }
}
```

---

## 10. Indexing Strategies — Internals and Design Patterns

### 10.1 B+ Tree Index Internals

```
B+ TREE STRUCTURE (how your index actually works):
  Height-3 tree, 1000 keys per node, 100M rows:
    Level 0: 1 root node
    Level 1: ~100 child nodes
    Level 2: ~10,000 leaf nodes
    
    Index lookup: 3 page reads → 3ms (HDD) or 0.3ms (SSD)
    Full table scan: 100M rows / 1000 per page = 100K page reads → minutes!

SEQUENTIAL vs RANDOM I/O:
  Range scan on indexed column:  sequential read → fast
  Random access by PK:           typically cached → fast
  Non-indexed column filter:     sequential table scan → slow

  Example: PostgreSQL heap with 100M rows, 500 bytes each = 50GB table
  Without index: 50GB sequential read for WHERE email='user@example.com'
  With index:    3 index page reads + 1 heap page read = 4 reads
```

### 10.2 PostgreSQL Index Types — When to Use Each

```
B-TREE (default):
  Use: equality (=), range (<, >), LIKE 'prefix%'
  Don't use: LIKE '%suffix', full-text, geometric data

HASH:
  Use: equality only (=), faster than B-tree for pure equality
  Don't use: ranges, sorting
  NOTE: Not WAL-logged before PostgreSQL 10 (not safe for replication)

GIN (Generalized Inverted Index):
  Use: full-text search (tsvector), arrays (ANY/contains), JSONB
  Cost: slow to build/update, fast to query

GiST (Generalized Search Tree):
  Use: geometric data, PostGIS, range types, fuzzy text (pg_trgm)

BRIN (Block Range Index):
  Use: VERY large tables with natural sort order (e.g., timestamps, serial IDs)
  Size: tiny (only stores min/max per block range)
  When: table is naturally ordered AND you mostly query by range
  Example: logs table ordered by created_at → BRIN is 1000x smaller than B-tree
```

```sql
-- Full-text search index (GIN on tsvector)
CREATE INDEX idx_product_search
    ON products
    USING GIN(to_tsvector('english', name || ' ' || description));

-- Query using the GIN index
SELECT *
FROM products
WHERE to_tsvector('english', name || ' ' || description)
      @@ to_tsquery('english', 'wireless & headphones');

-- JSONB containment (GIN)
CREATE INDEX idx_metadata ON products USING GIN(metadata);
-- Query: SELECT * FROM products WHERE metadata @> '{"color":"red"}';

-- BRIN for time-series table (orders by created_at)
CREATE INDEX idx_orders_brin ON orders USING BRIN(created_at);
-- Tiny index, works only because orders are inserted in time order
```

### 10.3 Index Design: The Leftmost Prefix Rule

```
Composite index: CREATE INDEX idx ON orders(user_id, status, created_at)

WHICH QUERIES USE THIS INDEX:
  ✓ WHERE user_id = ?                         (leftmost prefix)
  ✓ WHERE user_id = ? AND status = ?          (leftmost prefix)
  ✓ WHERE user_id = ? AND status = ? AND created_at > ?  (all three)
  ✗ WHERE status = ?                          (no leftmost prefix)
  ✗ WHERE status = ? AND created_at > ?       (no user_id)
  ✓ WHERE user_id = ? AND created_at > ?      (user_id used, created_at used as filter)

RULE: Index skips over range-query columns for subsequent columns
  WHERE user_id = ? AND status LIKE 'A%' AND created_at = ?
    → user_id used (equality)
    → status used (range, but LIKE stops further index use)
    → created_at NOT used by index
```

### 10.4 EXPLAIN ANALYZE — Reading Query Plans Like a Pro

```sql
-- Always use EXPLAIN (ANALYZE, BUFFERS) in production diagnosis
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT o.id, u.name, SUM(o.total)
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.created_at > NOW() - INTERVAL '7 days'
GROUP BY o.id, u.name
ORDER BY SUM(o.total) DESC
LIMIT 10;

-- KEY THINGS TO LOOK FOR:
-- 1. Seq Scan on large tables = MISSING INDEX
-- 2. Rows estimate vs actual (large diff = stale statistics → ANALYZE)
-- 3. Nested Loop Join on large tables = consider Hash Join or add index
-- 4. Buffers: hit (cache hits) vs read (disk reads) → low hit rate = more RAM
-- 5. Sort using disk → increase work_mem

-- INTERPRETING:
--  Seq Scan (rows=100000000) ← ALARM: full table scan on 100M rows
--  Index Scan (rows=1)       ← GOOD: index used, 1 row read
--  Bitmap Index Scan         ← GOOD: multiple conditions combined via bitmap
--  Hash Join                 ← usually OK, check buffer usage
```

### 10.5 Index Maintenance — What PostgreSQL Doesn't Do for You

```
BLOAT: Deleted rows leave "dead tuples" in heap and indexes
       Result: index grows even as data is deleted
       Fix: VACUUM (reclaims space for reuse), VACUUM FULL (rewrites table)

       -- Check table bloat
       SELECT relname,
              n_live_tup,
              n_dead_tup,
              ROUND(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_pct
       FROM pg_stat_user_tables
       ORDER BY dead_pct DESC;

       -- If dead_pct > 10% on busy table → tune autovacuum or run manually:
       VACUUM (ANALYZE) orders;

AUTOVACUUM TUNING (for high-write tables):
  -- Speed up autovacuum for a specific table
  ALTER TABLE orders SET (
    autovacuum_vacuum_scale_factor = 0.01,   -- Trigger at 1% dead tuples (default 20%)
    autovacuum_vacuum_cost_delay = 2,        -- Less I/O throttling (default 20ms)
    autovacuum_analyze_scale_factor = 0.005  -- Analyze more frequently
  );

UNUSED INDEXES — find and drop them:
  SELECT indexrelname, idx_scan, idx_tup_read
  FROM pg_stat_user_indexes
  WHERE idx_scan = 0         -- Never used since last stats reset
    AND indexrelname NOT LIKE 'pg_%';
  -- Drop unused indexes → faster writes, less storage
```

---

## 11. Sharding — Strategies, Mechanics, and Gotchas

### 11.1 When NOT to Shard (Most People Shard Too Early)

```
BEFORE SHARDING, EXHAUST THESE OPTIONS:
  1. Add indexes (cheapest, biggest wins)
  2. Tune buffer pool / shared_buffers (fit hot data in RAM)
  3. Add read replicas (scale reads independently)
  4. Partition by time (PostgreSQL declarative partitioning)
  5. Archive old data (move old rows to cold storage)

SHARDING ADDS:
  - No cross-shard ACID transactions
  - Cross-shard queries require scatter-gather (slow)
  - Schema changes must run on all shards
  - Application must know about sharding topology

SHARD WHEN:
  - Write throughput exceeds single primary's capacity
  - Dataset doesn't fit in RAM on largest available server
  - Single-server compliance limitations (data residency)
```

### 11.2 Consistent Hashing — The Right Algorithm

```
NAIVE HASH SHARDING PROBLEM:
  shard = hash(key) % N
  Add a new shard (N+1):
    All assignments change → must move ~(N/(N+1)) = ~70-90% of data
    = full resharding = downtime

CONSISTENT HASHING:
  Imagine a ring from 0 to 2^32
  Place N virtual nodes on ring (each shard gets multiple positions)
  Key → hash → find next node clockwise on ring

  ADD SHARD:
    New shard gets some ring segments from neighbors
    Only keys in those segments move → ~1/N of data rebalanced (33% for 3 shards)

  REMOVE SHARD:
    Keys redistribute to next shard clockwise
    Only keys in removed shard move

VIRTUAL NODES (VNodes):
  Each physical shard has 100-200 virtual positions on ring
  Ensures even distribution even with different hardware sizes
  Large shard: more virtual nodes → gets more data

  Used by: Cassandra, DynamoDB, Redis Cluster (using hash slots, 16384 total)
```

```java
@Component
public class ConsistentHashRouter<T> {
    private final TreeMap<Long, T> ring = new TreeMap<>();
    private static final int VIRTUAL_NODES = 150;
    private final MessageDigest md5;

    public ConsistentHashRouter() throws NoSuchAlgorithmException {
        this.md5 = MessageDigest.getInstance("MD5");
    }

    public void addNode(T node) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            long hash = hash(node.toString() + "#" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            ring.remove(hash(node.toString() + "#" + i));
        }
    }

    public T getNode(String key) {
        if (ring.isEmpty()) throw new IllegalStateException("No nodes");
        long hash = hash(key);
        Map.Entry<Long, T> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();  // Wrap around ring
        return entry.getValue();
    }

    private long hash(String key) {
        byte[] digest = md5.digest(key.getBytes(StandardCharsets.UTF_8));
        return ((long)(digest[3] & 0xFF) << 24)
             | ((long)(digest[2] & 0xFF) << 16)
             | ((long)(digest[1] & 0xFF) << 8)
             | (digest[0] & 0xFF);
    }
}
```

### 11.3 Hot Spot Detection and Remediation

```
HOT SHARD PROBLEM:
  "Celebrity" user with 100M followers
  All write traffic for that user → single shard → overloaded

DETECTION:
  -- Cassandra: look at partition size
  nodetool toppartitions

  -- PostgreSQL: look at table access patterns
  SELECT schemaname, tablename, seq_scan, idx_scan
  FROM pg_stat_user_tables
  ORDER BY seq_scan DESC;

SOLUTIONS:

1. KEY SALTING (spread celebrity writes across sub-shards)
   key = "user:123"   → only 1 shard
   key = "user:123:0" or "user:123:1" ... "user:123:9"  → 10 shards
   Write: randomly pick suffix 0-9
   Read: read from all 10 keys, merge results

2. APPLICATION-LEVEL CACHE (for read hot spots)
   Celebrity user: cache their profile for 60s
   → DB shielded from 1M reads/minute → single cache hit serves all

3. RATE LIMITING at shard level
   Circuit-break requests to overloaded shard
   → Return cached/degraded response while shard recovers
```

### 11.4 Cross-Shard Queries

```
THE PROBLEM:
  "Find all premium users in US who made purchase in last 7 days"
  Users sharded by user_id (8 shards)
  → Must query ALL 8 shards
  → Merge and sort results
  → Latency = max(shard_latency) not avg(shard_latency) = SLOW

SOLUTIONS BY SEVERITY:

1. DENORMALIZE A SECONDARY STORE (preferred)
   Keep a separate Elasticsearch or denormalized PostgreSQL
   for cross-shard queries (CQRS pattern)

2. SCATTER-GATHER with parallelism
   Query all shards concurrently using CompletableFuture
   Acceptable if N shards is small (<= 8) and each shard is fast
```

```java
@Service
public class CrossShardQueryService {
    private final List<UserRepository> shards;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public List<User> findPremiumUsersInRegion(String region) {
        // Query all shards in parallel
        List<CompletableFuture<List<User>>> futures = shards.stream()
            .map(shard -> CompletableFuture.supplyAsync(
                () -> shard.findPremiumByRegion(region), executor))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)  // Wait for all
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .toList();
    }
}
```

---

## 12. Advanced Topics — Connection Pools, Transactions, Backups

### 12.1 Connection Pooling — The Most Misunderstood Component

```
CONNECTIONS ARE EXPENSIVE:
  TCP handshake:         1-3ms
  TLS negotiation:       5-20ms (if SSL)
  Authentication:        5-10ms
  PostgreSQL fork:       ~100ms (new process per connection)
  Total: 100-150ms per connection creation

CONNECTION POOL FIXES THIS:
  HikariCP: borrow from pool (microseconds), return after use
```

**HikariCP — Complete Production Configuration:**
```yaml
spring:
  datasource:
    hikari:
      # Pool sizing: (2 * cores) + effective_spindle_count
      # For 8-core + SSD: (2*8)+1 = 17. Use 20 with headroom.
      maximum-pool-size: 20

      # Min idle: keep 5 warm connections always ready
      minimum-idle: 5

      # Wait max 3s for connection from pool before throwing exception
      connection-timeout: 3000

      # Evict idle connection after 10 min (free DB resources)
      idle-timeout: 600000

      # Recycle connections after 30 min (prevent stale connections)
      max-lifetime: 1800000

      # Validate connection before using (catches broken TCP connections)
      connection-test-query: SELECT 1

      # Pool name (for Micrometer metrics)
      pool-name: HikariPool-Primary

      # Leak detection: warn if connection held > 5s (indicates bad code)
      leak-detection-threshold: 5000
```

```
PGBOUNCER — Why You Need It for PostgreSQL at Scale:

  Problem: PostgreSQL creates ONE OS PROCESS per connection
    50 connections → 50 processes → fine
    500 connections → 500 processes → 10GB RAM overhead + context switch hell

  PgBouncer: lightweight proxy that multiplexes thousands of app connections
             onto a small pool of actual PostgreSQL connections

  Transaction mode (recommended):
    App gets connection → runs transaction → returns to PgBouncer pool
    200 app threads share 20 PostgreSQL connections
    → PostgreSQL stays at 20 connections regardless of app concurrency

  Setup:
    App → PgBouncer (:5432) → PostgreSQL (:5433)
    Only change: app's JDBC URL points to PgBouncer port
```

### 12.2 Query Optimization — The EXPLAIN Workflow

```
FIND SLOW QUERIES:
  -- PostgreSQL: enable pg_stat_statements extension
  CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

  -- Top 10 slowest queries by total time
  SELECT query,
         calls,
         total_exec_time / calls AS avg_ms,
         rows / calls AS avg_rows,
         total_exec_time
  FROM pg_stat_statements
  ORDER BY total_exec_time DESC
  LIMIT 10;

  -- Top time wasters overall
  SELECT query, ROUND(total_exec_time::numeric, 2) AS total_ms,
         calls,
         ROUND(mean_exec_time::numeric, 2) AS mean_ms
  FROM pg_stat_statements
  WHERE calls > 100
  ORDER BY total_exec_time DESC LIMIT 20;
```

```
THE QUERY OPTIMIZATION LOOP:
  1. Find slow query via pg_stat_statements or slow query log
  2. EXPLAIN (ANALYZE, BUFFERS) the query
  3. Look for: Seq Scan, Hash Join on large tables, Sort using disk
  4. Fix:
     a. Seq Scan → add index on WHERE clause columns
     b. Bad estimate → ANALYZE table (update statistics)
     c. Sort on disk → increase work_mem
     d. Slow JOIN → add composite index on join column
  5. Re-run EXPLAIN to verify improvement
  6. Monitor in production (p99 latency should drop)
```

```sql
-- Example: N+1 problem fixed with JOIN
-- BAD:
SELECT * FROM orders WHERE user_id = 1;
-- Then for each order: SELECT * FROM order_items WHERE order_id = ?
-- N+1 queries for N orders

-- GOOD: Single query with JOIN
SELECT o.id, o.total, oi.product_id, oi.quantity
FROM orders o
JOIN order_items oi ON oi.order_id = o.id
WHERE o.user_id = 1;

-- Spring JPA: prevent N+1 with JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.userId = :userId")
List<Order> findOrdersWithItems(@Param("userId") String userId);
```

### 12.3 Backup and Recovery — Enterprise Standards

```
BACKUP STRATEGY (by tier):

Tier 1 — Zero data loss (financial, healthcare):
  - Synchronous standby (WAL-level replication, commit waits for replica ACK)
  - Continuous WAL archiving to S3 (pg_basebackup + WAL archiving)
  - Hourly snapshots (EBS, RDS automated)
  - RPO: 0 seconds (sync replica), RPO < 1s (WAL archiving)

Tier 2 — < 1 minute RPO (e-commerce, SaaS):
  - Asynchronous streaming replication (replica ~100ms lag)
  - Daily full backup + WAL archiving for PITR (Point-in-Time Recovery)
  - Test restore monthly

Tier 3 — < 1 hour RPO (analytics, non-critical):
  - Daily logical backup (pg_dump)
  - Weekly test restore

PITR (POINT-IN-TIME RECOVERY) — the crown jewel:
  Scenario: Developer runs DELETE FROM orders without WHERE at 14:32
  Action:   Restore base backup, replay WAL up to 14:31:59 → recover all data
  Setup:    archive_command in postgresql.conf + WAL continuous archiving to S3
```

```bash
# PostgreSQL PITR restore example
# 1. Restore base backup
pg_basebackup -h source-db -U replication -D /var/lib/postgresql/data

# 2. Create recovery config (PostgreSQL 12+: recovery.conf merged into postgresql.conf)
cat >> /var/lib/postgresql/data/postgresql.conf <<EOF
restore_command = 'aws s3 cp s3://my-wal-archive/%f %p'
recovery_target_time = '2024-01-15 14:31:59'
recovery_target_action = 'promote'
EOF

# 3. Start PostgreSQL — it will replay WAL until target time, then promote
pg_ctl start -D /var/lib/postgresql/data
```

### 12.4 Monitoring and Alerting — Critical Alerts

```
CRITICAL ALERTS (page on-call immediately):
  - Replication lag > 30 seconds
  - Connection pool wait time > 1 second
  - Disk space > 80% full
  - Query p99 latency > 5x baseline
  - Primary failover detected

WARNING ALERTS (investigate within 1 hour):
  - Dead tuple rate > 20% (autovacuum not keeping up)
  - Cache hit ratio < 95%
  - Index scans decreasing / seq scans increasing
  - Replica lag > 5 seconds

PROMETHEUS QUERIES (key database alerting rules):
```

```yaml
# prometheus/rules/database.yml
groups:
  - name: database
    rules:
      - alert: PostgresReplicationLagHigh
        expr: pg_replication_lag_seconds > 30
        for: 1m
        labels:
          severity: critical

      - alert: HikariConnectionPoolExhausted
        expr: hikaricp_pending_threads > 5
        for: 30s
        labels:
          severity: critical

      - alert: PostgresDeadTuplesHigh
        expr: |
          pg_stat_user_tables_n_dead_tup
          / pg_stat_user_tables_n_live_tup > 0.2
        for: 5m
        labels:
          severity: warning

      - alert: QueryLatencyHigh
        expr: |
          histogram_quantile(0.99, rate(db_query_latency_seconds_bucket[5m])) > 1
        for: 2m
        labels:
          severity: warning
```

---

## 13. Distributed Transactions — 2PC, Saga, CQRS

### 13.1 Why Distributed Transactions Are Hard

```
SCENARIO: Microservices architecture
  OrderService → Orders DB (PostgreSQL)
  PaymentService → Payments DB (PostgreSQL)
  InventoryService → Inventory DB (MySQL)

  User action: "Place order" must atomically:
    1. Deduct inventory
    2. Charge payment
    3. Create order record

  If #2 fails after #1 → need to undo inventory deduction
  If #3 fails after #1 and #2 → need to undo both

  THERE IS NO SINGLE TRANSACTION ACROSS 3 DIFFERENT DATABASES.
```

### 13.2 Two-Phase Commit (2PC) — Why We Don't Use It

```
2PC PROTOCOL:
  Phase 1 (Prepare):
    Coordinator → "Prepare to commit" → All participants
    Each participant: writes to WAL, locks resources, responds "READY" or "ABORT"

  Phase 2 (Commit or Rollback):
    If all READY → Coordinator sends COMMIT
    If any ABORT → Coordinator sends ROLLBACK

WHY 2PC IS DANGEROUS IN PRACTICE:
  1. Coordinator crash between Phase 1 and Phase 2:
     Participants hold locks indefinitely → blocked forever
  2. Network partition during Phase 2:
     Some commit, some don't → INCONSISTENCY
  3. Performance: all participants hold locks until coordinator responds
     → High latency, low throughput

USE 2PC ONLY IF:
  - Same database cluster, XA transactions
  - All participants are highly reliable
  - Not for cross-service / cross-cloud scenarios
```

### 13.3 Saga Pattern — The Production Answer

```
SAGA: Break distributed transaction into local transactions + compensations
  Each step has a forward action + compensating action (rollback)

CHOREOGRAPHY SAGA (event-driven, no coordinator):
  OrderService: CREATE order → publish OrderCreated
  InventoryService: receives OrderCreated → deduct stock → publish StockReserved
  PaymentService: receives StockReserved → charge card → publish PaymentProcessed
  OrderService: receives PaymentProcessed → CONFIRM order

  On failure:
  PaymentService fails → publish PaymentFailed
  InventoryService: receives PaymentFailed → RESTORE stock (compensation)
  OrderService: receives StockRestored → CANCEL order
```

```java
// Saga Step: Place Order (Orchestration variant)
@Service
public class PlaceOrderSaga {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final OrderRepository orders;
    private final KafkaTemplate<String, Object> kafka;

    @Transactional
    public Order execute(PlaceOrderCommand cmd) {
        Order order = null;
        boolean stockReserved = false;
        boolean paymentCharged = false;

        try {
            // Step 1: Reserve inventory
            inventory.reserve(cmd.getProductId(), cmd.getQuantity());
            stockReserved = true;

            // Step 2: Charge payment
            payment.charge(cmd.getUserId(), cmd.getTotal());
            paymentCharged = true;

            // Step 3: Create order
            order = orders.save(Order.create(cmd));
            kafka.send("order-events", new OrderPlacedEvent(order.getId()));
            return order;

        } catch (Exception e) {
            // COMPENSATIONS (in reverse order)
            if (paymentCharged) {
                try { payment.refund(cmd.getUserId(), cmd.getTotal()); }
                catch (Exception ex) { log.error("Refund failed — needs manual intervention", ex); }
            }
            if (stockReserved) {
                try { inventory.release(cmd.getProductId(), cmd.getQuantity()); }
                catch (Exception ex) { log.error("Stock release failed", ex); }
            }
            throw new OrderFailedException("Order placement failed", e);
        }
    }
}
```

```
CHOREOGRAPHY vs ORCHESTRATION:

  Choreography (event-driven):
    PROS: Loose coupling, each service decides on its own
    CONS: Hard to track overall saga state, spaghetti event flows

  Orchestration (centralized saga):
    PROS: Single place to see saga state, easier to debug
    CONS: Orchestrator becomes a central dependency

  RECOMMENDATION: Orchestration for critical flows (orders, payments)
                  Choreography for non-critical, high-volume flows (analytics)
```

---

## 14. Polyglot Persistence — Multi-Database Architecture

### 14.1 The Right Architecture

```
ENTERPRISE APPLICATION: E-COMMERCE PLATFORM
  Each component uses the database that best fits it.
  Single database = compromise everywhere.

  Application Layer
       │
       ├─ ORDER SERVICE      → PostgreSQL (ACID, financial correctness)
       │                       + Redis (order status cache, 30s TTL)
       │
       ├─ PRODUCT SERVICE    → PostgreSQL (source of truth)
       │                       + Elasticsearch (search, facets)
       │                       + Redis (product catalog cache, 1h TTL)
       │
       ├─ USER SERVICE       → PostgreSQL (auth, transactions)
       │                       + Redis (sessions, JWT blacklist)
       │
       ├─ FEED SERVICE       → Cassandra (high write volume, time-series feed)
       │                       + Redis (recent feed, 5min TTL)
       │
       ├─ ANALYTICS          → ClickHouse or BigQuery (OLAP, not OLTP)
       │                       + Kafka (ingest stream)
       │
       └─ MONITORING         → InfluxDB + Prometheus (metrics)
                               + Elasticsearch (logs, ELK stack)
```

### 14.2 Data Consistency Across Stores

```
THE FUNDAMENTAL PROBLEM:
  PostgreSQL (products) → Elasticsearch (search index) must stay in sync
  If ES update fails → search returns stale results

CORRECT APPROACH: CDC via Debezium (described in Section 9.2)
  Source of truth: PostgreSQL
  Derived stores: Elasticsearch, Redis, Analytics

  All derived stores are updated asynchronously via CDC/Kafka
  Application never writes directly to Elasticsearch
  On ES failure: Kafka retains events, ES catches up when recovered

CONSISTENCY MODEL:
  Reads from PostgreSQL: always consistent (source of truth)
  Reads from Elasticsearch: eventually consistent (seconds behind)
  Reads from Redis: eventually consistent (TTL-based, CDC-invalidated)

  Design your UI to handle this:
  - After placing an order, show PostgreSQL data (not search results)
  - Search results may lag 1-2 seconds after new products are added (expected)
```

### 14.3 Schema Evolution in Polyglot Environment

```
CHALLENGE: Add a new field to Product model

  Step 1: Add to PostgreSQL schema (nullable, backward compatible)
    ALTER TABLE products ADD COLUMN weight_kg NUMERIC(5,2);

  Step 2: Deploy new application code (reads weight_kg if present)

  Step 3: Update Elasticsearch mapping
    PUT /products/_mapping
    { "properties": { "weight_kg": { "type": "float" } } }

  Step 4: Re-index Elasticsearch from PostgreSQL (backfill)

  PITFALLS:
    - Elasticsearch mapping changes on existing fields = re-index required
    - Field type change in PostgreSQL + ES = requires careful migration
    - MongoDB: no migration needed (flexible schema, but no constraint enforcement)

BLUE-GREEN SCHEMA MIGRATION (zero-downtime PostgreSQL):
  1. Add new column (nullable)
  2. Double-write in application (old column + new column)
  3. Backfill new column for existing rows
  4. Switch reads to new column
  5. Remove writes to old column
  6. Drop old column
```

---

## 15. Common Pitfalls, Anti-Patterns, and Real-World Case Studies

### 15.1 The N+1 Problem — Still Killing Production

```
WHAT IT IS:
  "Fetch 100 orders" → 1 query
  "Fetch user for each order" → 100 queries
  = 101 queries instead of 1

DETECTION:
  Hibernate: enable SQL logging in dev
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true

  p6spy: log every query with execution time
    Find patterns: same query repeated N times with different params = N+1

SOLUTIONS:
  1. FETCH JOIN in JPQL
  2. EntityGraph
  3. Batch loading (hibernate.default_batch_fetch_size=100)
```

```java
// ❌ N+1: lazy loading triggers query per order
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> log.info(o.getUser().getName()));  // 1 query per order!

// ✓ JOIN FETCH: single query
@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.status = :status")
List<Order> findWithUser(@Param("status") OrderStatus status);

// ✓ EntityGraph: flexible
@EntityGraph(attributePaths = {"user", "items", "items.product"})
List<Order> findAll();

// ✓ Batch fetch size (set globally, cheapest fix)
spring.jpa.properties.hibernate.default_batch_fetch_size=100
// Hibernate loads users in batches of 100 instead of one-by-one
```

### 15.2 Long-Held Transactions

```
THE PROBLEM:
  @Transactional method does:
    1. Start transaction (takes locks)
    2. Call external API (200ms)
    3. Update database
    4. Commit

  During step 2, transaction holds NO LOCKS (optimistic)
  BUT in many codebases, steps 1-4 hold a DB connection from HikariCP

  With pool size 20, 20 concurrent requests × 200ms API call
  = all 20 connections held for 200ms
  → New requests wait (pool exhausted) → timeout → cascade failure

RULES:
  1. NEVER call external APIs inside @Transactional
  2. NEVER perform long computations inside @Transactional
  3. Keep transactions as short as possible
  4. Fetch data → commit → external call → (if needed) new transaction to update
```

```java
// ❌ WRONG: External API call inside transaction
@Transactional
public void processOrder(Order order) {
    // Holds DB connection and potential row locks
    PaymentResult result = paymentGateway.charge(order);  // 200ms call
    order.setPaymentId(result.getPaymentId());
    orderRepository.save(order);
}

// ✓ CORRECT: Separate transaction from external call
public void processOrder(Order order) {
    // Step 1: External call (no transaction)
    PaymentResult result = paymentGateway.charge(order);

    // Step 2: Short transaction to save result
    savePaymentResult(order.getId(), result);
}

@Transactional
private void savePaymentResult(String orderId, PaymentResult result) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    order.setPaymentId(result.getPaymentId());
    orderRepository.save(order);  // Transaction commits immediately
}
```

### 15.3 Hot Row Contention

```
SCENARIO:
  Flash sale: 10,000 users simultaneously checkout same product
  All try to UPDATE products SET stock = stock - 1 WHERE id = 'PRODUCT-1'
  → All queue on row lock for product-1
  → p99 latency = 5+ seconds, timeouts, error cascade

SOLUTIONS:

1. INVENTORY RESERVATION TABLE (avoid hot rows)
   Don't update stock count; insert reservation records
   Count reservations with COUNT() query periodically
   Better: use Cassandra for reservation (write-optimized)

2. VIRTUAL SHARDING OF HOT ROW
   Split stock=1000 across 10 rows (100 each)
   UPDATE SET stock=stock-1 WHERE product_id='P1' AND bucket=random(1,10) AND stock > 0
   10x parallelism → 10x throughput

3. DEFERRED CONSISTENCY
   Accept over-selling temporarily
   Reconcile after: cancel excess orders, refund customers
   Works only if business allows it (flash sales often do)
```

### 15.4 Missing Composite Index on Hot Query

```sql
-- SLOW: ORDER BY without index support
SELECT * FROM events
WHERE user_id = '123'
  AND event_type = 'CLICK'
ORDER BY occurred_at DESC
LIMIT 20;

-- Why slow: even if index on user_id exists,
-- sort requires fetching all 50K rows for user, then sorting

-- FIX: Composite index matching query exactly
CREATE INDEX idx_events_user_type_time
    ON events(user_id, event_type, occurred_at DESC);

-- Now: index scan returns rows in order → no sort needed
-- Query: O(log N) index lookup + 20 sequential leaf pages
```

---

### 15.5 Real-World Case Studies

**Case Study 1: E-Commerce Flash Sale Survival**
```
PROBLEM: Black Friday sale. 100K concurrent users. Product pages timing out.
         PostgreSQL p99 = 8 seconds. Checkout failing.

ROOT CAUSE:
  No Redis cache on product pages.
  Each request hitting primary DB.
  Primary DB CPU 100%, awaiting I/O.

INCIDENT FIX (in order):
  Step 1: Add Redis cache for product details (TTL = 5 min). 30 minutes.
          → 95% cache hit rate. DB CPU drops to 20%.
  Step 2: Warm up cache with top 10K products before next sale. 1 hour.
  Step 3: Add read replicas (2 replicas) for overflow reads. 2 hours.
          → Product page p99 = 50ms. Sale proceeds without issues.

PERMANENT FIX:
  - Implemented write-through cache on all product updates
  - Added CDN for static product images
  - Implemented circuit breaker on product service (fallback to cached data)
  - Load tested with k6 before next sale (simulated 200K concurrent users)
```

**Case Study 2: Cassandra Tombstone Crisis**
```
PROBLEM: IoT platform. Cassandra read timeouts. 40% of requests failing.
         Seemingly worse after compaction, not better.

ROOT CAUSE:
  Application deleted sensor readings older than 30 days via DELETE statements.
  Cassandra: each DELETE = tombstone marker.
  After 6 months: 10M tombstones per partition.
  Read: must scan all tombstones → ReadTimeoutException

  Tombstone threshold was 100K. Was exceeded. Reads aborting.

FIX:
  Step 1: Reduce gc_grace_seconds on table from 10 days to 1 day.
  Step 2: Run major compaction to clear tombstones immediately.
  Step 3: Switch DELETE strategy to TTL-based expiry.

  WRONG approach (what they were doing):
    DELETE FROM sensor_data WHERE device_id=? AND ts < ?;

  RIGHT approach:
    INSERT with TTL: INSERT INTO sensor_data(...) USING TTL 2592000; -- 30 days
    Cassandra automatically expires rows without tombstones

LESSON: In Cassandra, NEVER manually delete data if TTL works.
        Design schema with TTL from day one.
```

**Case Study 3: PostgreSQL Vacuum Crisis**
```
PROBLEM: Large SaaS platform. Orders table. DELETE latency grew from 2ms to 800ms.
         Table file size: 500GB for 50M rows. Should be ~50GB.

ROOT CAUSE:
  High UPDATE rate on orders table (status changes).
  AUTOVACUUM too slow (default settings, low priority).
  Dead tuples: 90% of table was dead rows (10x table bloat).
  PostgreSQL sequential scan now reading 500GB instead of 50GB.

FIX:
  -- Emergency: manual VACUUM FULL (locks table, rewrites it)
  VACUUM FULL orders;  -- Took 2 hours, table back to 50GB
  
  -- Permanent: aggressive autovacuum for orders table
  ALTER TABLE orders SET (
    autovacuum_vacuum_scale_factor = 0.01,   -- Vacuum at 1% dead tuples
    autovacuum_vacuum_cost_delay = 2,        -- Run faster
    autovacuum_analyze_scale_factor = 0.01
  );

LESSON: Monitor n_dead_tup / n_live_tup ratio weekly.
        Tune autovacuum per-table for high-churn tables.
        Never use VACUUM FULL in production without planned maintenance window.
```

**Case Study 4: Redis Memory Exhaustion**
```
PROBLEM: Redis at 100% memory. New writes failing with OOM errors.
         maxmemory-policy = noeviction (default).
         Application errors cascading.

ROOT CAUSE:
  Developer cached user activity logs in Redis (not appropriate for Redis).
  Keys: "activity:userId:timestamp" — millions of keys with no TTL.
  Memory grew until OOM.

IMMEDIATE FIX:
  redis-cli --scan --pattern "activity:*" | xargs redis-cli del
  → Freed 60% of memory in 30 seconds.

PERMANENT FIX:
  1. Changed maxmemory-policy to volatile-lru
     → Evicts LRU keys WITH TTL (protects critical keys without TTL)
  2. Added TTL to ALL cache keys (no key without TTL)
  3. Moved activity logs to Cassandra (correct database)
  4. Set up Redis memory alerts at 60%, 80%, 90%

LESSON: Every Redis key MUST have a TTL unless it is permanent config data.
        Redis is not a data store — it's a cache. Design for eviction.
```

---

## Summary: Database Architecture Checklist

Use this before every major system design decision:

**1. Data Model**
- [ ] Is the schema stable? → PostgreSQL. Evolves frequently? → MongoDB
- [ ] Is data relational? → PostgreSQL. Hierarchical? → MongoDB. Graph? → Neo4j

**2. Consistency Requirements**
- [ ] Financial/inventory? → ACID (PostgreSQL, strong isolation)
- [ ] Can tolerate eventual consistency? → Cassandra, MongoDB with careful design
- [ ] Write, then immediately read? → Implement read-your-write consistency

**3. Scale Requirements**
- [ ] Write throughput > 100K/s? → Cassandra, Kafka + time-series DB
- [ ] Read throughput > 100K/s? → Redis + read replicas
- [ ] Global distribution? → Cassandra multi-region or DynamoDB Global Tables

**4. Query Patterns**
- [ ] Defined access patterns? → Cassandra or DynamoDB (perfect)
- [ ] Ad-hoc queries / reporting? → PostgreSQL (flexible SQL)
- [ ] Full-text search? → Elasticsearch (always secondary, never primary)

**5. Operational Realities**
- [ ] Team expertise? → Don't introduce Cassandra if team knows only PostgreSQL
- [ ] Managed vs self-hosted? → Cloud managed (RDS, DynamoDB) = less ops overhead
- [ ] Budget? → Each additional database = monitoring, backup, on-call, training cost

**6. Non-Functional Requirements**
- [ ] RTO defined? → Configure synchronous replication, automated failover
- [ ] RPO defined? → WAL archiving + sync replication if RPO = 0
- [ ] Data residency (GDPR)? → Geo-sharding or region-locked instances

---

*The best architects I've worked with don't know every database — they know precisely what each database they've chosen is optimized for, and they design around those boundaries. That's the difference between a working system and a system that works under pressure.*
