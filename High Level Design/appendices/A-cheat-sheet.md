# Appendix A: HLD Cheat Sheet

> A comprehensive quick-reference card for High Level Design. Bookmark this page -- it distills the entire book into tables, numbers, and one-liners you can consult in seconds.

---

## 1. Component Selection Guide

Use the table below to decide **when** to introduce each component and **which technology** to reach for first.

### 1.1 Load Balancer

| Aspect | Details |
|---|---|
| **When to use** | Multiple server instances; need to distribute traffic, perform health checks, or terminate TLS centrally |
| **Technologies** | **L4:** AWS NLB, HAProxy (TCP mode) | **L7:** NGINX, AWS ALB, Envoy, Traefik |
| **Key configuration** | Algorithm (round-robin, least-connections, IP-hash, weighted), health-check interval, connection draining timeout, sticky sessions (only if stateful) |
| **Watch out for** | Single point of failure -- use active-passive or active-active pair; don't rely on sticky sessions if you can make services stateless |

### 1.2 Cache

| Aspect | Details |
|---|---|
| **When to use** | Read-heavy workload (read:write > 5:1), data that tolerates slight staleness, expensive computation results, session storage |
| **Redis vs Memcached** | **Redis:** rich data structures (sorted sets, streams, pub/sub), persistence, replication, Lua scripting, single-threaded but fast (~100K QPS). **Memcached:** simple key-value, multi-threaded, slightly higher throughput for plain string caching, no persistence |
| **Strategies** | **Cache-Aside (Lazy):** app reads cache first, fills on miss. **Write-Through:** write to cache + DB synchronously. **Write-Behind (Write-Back):** write to cache, async flush to DB. **Read-Through:** cache itself loads from DB on miss. **Refresh-Ahead:** proactively refresh before TTL expires |
| **Key configuration** | TTL, max memory + eviction policy (LRU, LFU, random), cluster size, replication factor |
| **Watch out for** | Cache stampede (use locking / request coalescing), hot key (replicate or shard), cold start, data consistency |

### 1.3 CDN (Content Delivery Network)

| Aspect | Details |
|---|---|
| **When to use** | Static assets (images, JS, CSS, video), global user base, need to reduce origin load and latency |
| **Push vs Pull** | **Pull CDN:** origin serves on first request, CDN caches it (simpler). **Push CDN:** you upload content to CDN proactively (better for large, infrequently changing files) |
| **Providers** | CloudFront (AWS), Cloud CDN (GCP), Azure CDN, Cloudflare, Akamai, Fastly |
| **Key configuration** | Cache TTL / cache-control headers, origin shield, custom domain + TLS, invalidation strategy, geo-restrictions |

### 1.4 Message Queue / Event Streaming

| Aspect | Details |
|---|---|
| **When to use** | Decouple producers from consumers, smooth traffic spikes, enable async processing, event-driven architectures, guaranteed delivery |
| **Kafka** | High throughput (~1M msgs/sec per broker), log-based, replay-able, partitioned, great for event streaming and data pipelines. Ordered within partition |
| **RabbitMQ** | Traditional message broker, rich routing (exchanges, bindings), supports AMQP, lower throughput (~50K msgs/sec) but flexible routing and per-message acknowledgement |
| **Amazon SQS** | Fully managed, practically unlimited throughput (scales automatically), at-least-once delivery, no ordering guarantee (standard) or FIFO option, simple to operate |
| **Key configuration** | Partitions/shards, replication factor, consumer groups, retention period, dead-letter queue, idempotent consumers |

### 1.5 Database: SQL vs NoSQL Decision Matrix

| Criterion | Choose SQL (Relational) | Choose NoSQL |
|---|---|---|
| **Data model** | Structured, relational, joins needed | Flexible schema, denormalized, document/key-value/graph/columnar |
| **Consistency** | Strong ACID transactions required | Eventual consistency acceptable (BASE) |
| **Scale pattern** | Vertical first; horizontal with read replicas or sharding | Horizontal from day one (built-in sharding) |
| **Query pattern** | Complex queries, aggregations, ad-hoc reporting | Simple lookups by key, high write throughput |
| **Examples** | PostgreSQL, MySQL, Aurora, CockroachDB | DynamoDB, Cassandra, MongoDB, Redis, Neo4j (graph), HBase (columnar) |
| **When both** | Use SQL as source of truth + NoSQL as read-optimized store (CQRS pattern) |

### 1.6 Search Engine

| Aspect | Details |
|---|---|
| **When to use** | Full-text search, fuzzy matching, faceted search, autocomplete, log analytics |
| **Technologies** | **Elasticsearch** (most popular, part of ELK stack), **OpenSearch** (AWS-managed fork), **Apache Solr**, **Meilisearch** (lightweight) |
| **Key configuration** | Index shards & replicas, mapping/schema, analyzers & tokenizers, refresh interval, circuit breakers |
| **Watch out for** | Not a primary data store -- always have a source of truth; reindex strategy; cluster sizing (memory-hungry) |

---

## 2. Scalability Patterns Quick Reference

| Pattern | What It Does | When to Use |
|---|---|---|
| **Horizontal Scaling** | Add more machines behind a load balancer | Stateless services, web/app tier |
| **Vertical Scaling** | Upgrade to a bigger machine (more CPU/RAM) | Quick fix, single-leader DB, before re-architecting |
| **Database Sharding** | Split data across multiple DB instances by a shard key | Single DB is a bottleneck, data too large for one node |
| **Read Replicas** | Replicate writes to follower DBs that serve reads | Read-heavy workloads (>80% reads) |
| **Caching** | Store frequently accessed data in memory (Redis, Memcached) | Reduce DB load, speed up reads |
| **Async Processing** | Offload work to background queues/workers | Long-running tasks, email sending, image processing |
| **Database Indexing** | Create B-tree / hash indexes on frequently queried columns | Slow queries on large tables |
| **Denormalization** | Pre-join / duplicate data to avoid expensive joins at read time | Read-heavy, high-scale read paths |
| **Data Partitioning** | Split data logically (by date, region, tenant) | Multi-tenant SaaS, time-series data |
| **CDN** | Serve static content from edge locations close to users | Global user base, media-heavy applications |
| **Connection Pooling** | Reuse DB/service connections | High request concurrency, reduce connection overhead |
| **Auto-scaling** | Dynamically add/remove instances based on metrics | Variable traffic patterns, cost optimization |

---

## 3. Consistency Models Quick Reference

| Model | Guarantee | Latency | Use Case |
|---|---|---|---|
| **Linearizability** | Reads see the most recent write; behaves like a single copy | Highest | Leader election, distributed locks |
| **Sequential Consistency** | All nodes see operations in the same order (but not necessarily real-time) | High | Replicated state machines |
| **Causal Consistency** | Causally related operations are seen in order; concurrent operations may differ | Medium | Social media feeds, collaborative editing |
| **Eventual Consistency** | All replicas converge eventually; reads may be stale | Lowest | DNS, CDN caches, shopping cart counts |
| **Read-Your-Writes** | A user always sees their own writes | Medium | User profile updates |
| **Monotonic Reads** | Once you've read a value, you won't see an older value | Medium | Session-scoped reads |
| **Strong Eventual (CRDTs)** | Eventual + conflict-free merging guaranteed | Low | Collaborative editors, counters |

---

## 4. Communication Protocols Quick Reference

| Protocol | Style | Best For | Payload | Latency |
|---|---|---|---|---|
| **REST** | Request-response, HTTP/1.1 or HTTP/2 | CRUD APIs, public APIs, browser clients | JSON (text) | Medium |
| **gRPC** | Request-response + streaming, HTTP/2 | Internal microservice-to-microservice, low-latency, polyglot | Protocol Buffers (binary) | Low |
| **GraphQL** | Request-response, HTTP | Client-driven queries, mobile apps needing flexible data fetching | JSON | Medium |
| **WebSocket** | Full-duplex, persistent connection | Real-time: chat, notifications, live dashboards, gaming | Any (text/binary) | Very Low |
| **Server-Sent Events (SSE)** | Server push, HTTP | One-way real-time: live feeds, stock tickers | Text (event stream) | Low |
| **Webhooks** | Server-to-server callback, HTTP | Event notifications between services (e.g., payment confirmed) | JSON | Varies |
| **Message Queue (async)** | Publish-subscribe / point-to-point | Decoupled async communication, event-driven architecture | Any | N/A (async) |

---

## 5. Common Capacity Numbers to Remember

### 5.1 Throughput Reference

| Component | Typical QPS / Throughput |
|---|---|
| Single web server (NGINX / Node.js / Go) | **1K -- 10K** requests/sec |
| Single relational DB (PostgreSQL/MySQL) | **1K -- 10K** queries/sec (depends on query complexity) |
| Redis (single instance) | **100K -- 200K** ops/sec |
| Memcached (single instance) | **100K -- 500K** ops/sec |
| Kafka (single broker) | **~1M** messages/sec (small messages) |
| Elasticsearch (single node) | **1K -- 10K** search queries/sec |
| DynamoDB (on-demand) | Practically unlimited (auto-scales) |
| S3 | **3,500** PUTs/sec and **5,500** GETs/sec per prefix |

### 5.2 Latency Reference Numbers (Approximate)

| Operation | Latency |
|---|---|
| L1 cache reference | **~1 ns** |
| L2 cache reference | **~4 ns** |
| Main memory (RAM) reference | **~100 ns** |
| SSD random read | **~16 us** |
| HDD random read (seek) | **~2-10 ms** |
| Read 1 MB sequentially from memory | **~3 us** |
| Read 1 MB sequentially from SSD | **~49 us** |
| Read 1 MB sequentially from HDD | **~825 us** |
| Round trip within same data center | **~0.5 ms** |
| Round trip CA to Netherlands | **~150 ms** |
| Packet round trip CA to Australia | **~200 ms** |
| TCP handshake | **~1-3 ms** (same region) |
| TLS handshake | **~5-50 ms** (same region) |
| Redis GET | **~0.1-0.5 ms** |
| Simple DB query (indexed) | **~1-5 ms** |
| API call to external service | **~50-300 ms** |

### 5.3 Storage Reference Numbers

| Data | Approximate Size |
|---|---|
| 1 character (ASCII) | 1 byte |
| 1 character (UTF-8, avg) | 1-4 bytes |
| UUID / GUID | 16 bytes (128 bits) |
| Typical JSON API response | 1-10 KB |
| Average web page | ~2 MB |
| Smartphone photo | 3-5 MB |
| 1 minute of HD video (compressed) | ~10-20 MB |
| 1 hour of HD video (compressed) | ~1-3 GB |

---

## 6. Powers of 2 Table

| Power | Exact Value | Approximate Size | Common Name |
|---|---|---|---|
| 2^10 | 1,024 | ~1 Thousand | 1 KB (Kilobyte) |
| 2^20 | 1,048,576 | ~1 Million | 1 MB (Megabyte) |
| 2^30 | 1,073,741,824 | ~1 Billion | 1 GB (Gigabyte) |
| 2^40 | 1,099,511,627,776 | ~1 Trillion | 1 TB (Terabyte) |
| 2^50 | 1,125,899,906,842,624 | ~1 Quadrillion | 1 PB (Petabyte) |

**Handy conversions:**

| Time Unit | Seconds |
|---|---|
| 1 minute | 60 |
| 1 hour | 3,600 |
| 1 day | 86,400 (~10^5) |
| 1 month | ~2,500,000 (~2.5 x 10^6) |
| 1 year | ~31,500,000 (~3 x 10^7) |

**Quick traffic math:**

| Daily Active Users | If each makes 10 req/day | QPS |
|---|---|---|
| 1 Million | 10M requests/day | ~116 req/sec |
| 10 Million | 100M requests/day | ~1,160 req/sec |
| 100 Million | 1B requests/day | ~11,600 req/sec |
| 1 Billion | 10B requests/day | ~116,000 req/sec |

> **Formula:** QPS = (DAU x requests_per_user) / 86,400. Peak QPS ~ 2x to 5x average.

---

## 7. The 8-Step System Design Approach

| Step | One-Liner | Time |
|---|---|---|
| **1. Clarify Requirements** | Ask questions to narrow scope; distinguish functional vs. non-functional needs | 3-5 min |
| **2. Estimate Scale** | Calculate DAU, QPS, storage, bandwidth to understand order of magnitude | 3-5 min |
| **3. Define API** | List the core API endpoints (REST or RPC) with inputs and outputs | 2-3 min |
| **4. Define Data Model** | Choose DB type, design key tables/collections, define schema and relationships | 3-5 min |
| **5. High-Level Design** | Draw the architecture with boxes and arrows: clients, LB, services, DB, cache, queue | 5-8 min |
| **6. Deep Dive** | Zoom into 2-3 critical components; discuss algorithms, data structures, trade-offs | 10-15 min |
| **7. Address Bottlenecks** | Identify single points of failure, hot spots, and apply scalability patterns | 3-5 min |
| **8. Wrap Up** | Summarize choices, mention monitoring / alerting / deployment, discuss future improvements | 2-3 min |

---

## 8. Common Mistakes and Corrections

| Mistake | Why It's Wrong | Correction |
|---|---|---|
| Jumping into solution immediately | You'll solve the wrong problem and miss constraints | Spend 3-5 min clarifying requirements first |
| Over-engineering from day one | Premature optimization wastes time and adds complexity | Start simple, then scale specific bottlenecks |
| Ignoring back-of-envelope math | Without numbers you can't justify design decisions | Always estimate QPS, storage, and bandwidth |
| Single database for everything | Won't scale; single point of failure | Plan for read replicas, caching, or sharding early |
| Choosing NoSQL "because it scales" | NoSQL isn't always the answer; you lose transactions and joins | Match the data model to access patterns |
| No caching layer | Every read hits the DB, wasting resources | Add cache-aside for read-heavy paths |
| Synchronous calls everywhere | Creates tight coupling and long tail latencies | Use async messaging for non-critical paths |
| Forgetting about failure modes | Real systems fail constantly | Design for retries, circuit breakers, DLQs, idempotency |
| Not considering data consistency | Can lead to lost writes or dirty reads | Explicitly choose consistency level per operation |
| Ignoring security & auth | Interviewers notice and it's critical in production | Mention auth (JWT/OAuth), rate limiting, encryption (TLS) |
| No monitoring / observability | You can't fix what you can't see | Plan for logging, metrics, tracing, alerting |
| Treating all traffic equally | Peak traffic will crush your system | Design for peak (2-5x avg); mention auto-scaling and load shedding |

---

## 9. Quick Decision Flowcharts (Text Form)

### Do I Need a Cache?

```
Is your read:write ratio > 5:1?
  YES -> Is latency critical?
           YES -> Add Redis / Memcached cache (cache-aside)
           NO  -> Consider read replicas first
  NO  -> Caching may not help much; focus on write optimization
```

### SQL or NoSQL?

```
Do you need ACID transactions?
  YES -> Do you need horizontal write-scaling on day one?
           YES -> Consider NewSQL (CockroachDB, Spanner)
           NO  -> Use PostgreSQL / MySQL
  NO  -> Is your data model flexible / document-oriented?
           YES -> MongoDB / DynamoDB
           NO  -> Is it key-value access?
                    YES -> Redis / DynamoDB
                    NO  -> Is it graph data?
                             YES -> Neo4j
                             NO  -> Is it wide-column / time-series?
                                      YES -> Cassandra / HBase / TimescaleDB
                                      NO  -> Default to PostgreSQL
```

### Sync or Async Communication?

```
Does the caller need an immediate response?
  YES -> Use REST / gRPC (synchronous)
  NO  -> Is ordering important?
           YES -> Use Kafka (partitioned, ordered within partition)
           NO  -> Use SQS / RabbitMQ
```

---

> **Tip:** Print this cheat sheet and keep it beside you during practice sessions. The numbers and patterns will become second nature.
