# High Level Design - The Complete Book

> A comprehensive guide from fundamentals to real-world system design, covering every component needed to architect systems at scale.

---

## Table of Contents

### Part I - Foundations

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 1 | [Introduction to High Level Design](chapters/01-introduction.md) | `01-introduction.md` | What is HLD, HLD vs LLD, when and why it matters |
| 2 | [Architectural Thinking](chapters/02-architectural-thinking.md) | `02-architectural-thinking.md` | Trade-offs, CAP theorem, scalability dimensions, design principles |
| 3 | [Requirements Gathering](chapters/03-requirements-gathering.md) | `03-requirements-gathering.md` | Functional vs non-functional requirements, SLAs, constraints |
| 4 | [Back-of-the-Envelope Estimation](chapters/04-estimation.md) | `04-estimation.md` | Traffic, storage, bandwidth, memory estimation techniques |

### Part II - Core Building Blocks

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 5 | [DNS and Domain Resolution](chapters/05-dns.md) | `05-dns.md` | How DNS works, DNS load balancing, GeoDNS, TTL strategies |
| 6 | [Load Balancers](chapters/06-load-balancers.md) | `06-load-balancers.md` | L4 vs L7, algorithms, connection modes (proxy, NAT, DSR), stateless vs stateful handling, LB types (hardware, software, cloud, sidecar, API gateway), health checks, auto-scaling, connection draining |
| 7 | [Caching](chapters/07-caching.md) | `07-caching.md` | Cache strategies, eviction policies, Redis deep dive (data structures, persistence, Sentinel, Cluster, Pub/Sub, Streams), in-process caching (Caffeine, Guava), multi-level L1+L2 architecture, RabbitMQ/Kafka cache invalidation, CDC, distributed locking, rate limiting, capacity planning |
| 8 | [Databases](chapters/08-databases.md) | `08-databases.md` | SQL vs NoSQL, ACID vs BASE, indexing deep dive (B+ Tree, clustered vs non-clustered, covering, composite), database partitioning, sharding, searching millions of rows, query optimization, EXPLAIN plans, locking & MVCC |
| 9 | [Storage Systems](chapters/09-storage.md) | `09-storage.md` | Block, file, object storage, HDFS, S3, data lakes |
| 10 | [Message Queues & Streaming](chapters/10-message-queues.md) | `10-message-queues.md` | Kafka, RabbitMQ, pub/sub, event streaming, exactly-once delivery |

### Part III - Data & Communication

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 11 | [Data Partitioning & Sharding](chapters/11-data-partitioning.md) | `11-data-partitioning.md` | Horizontal/vertical partitioning, consistent hashing, shard strategies |
| 12 | [Data Replication](chapters/12-data-replication.md) | `12-data-replication.md` | Leader-follower, multi-leader, leaderless, quorum reads/writes |
| 13 | [Consistency & Consensus](chapters/13-consistency.md) | `13-consistency.md` | Strong, eventual, causal consistency, vector clocks, CRDTs |
| 14 | [API Design](chapters/14-api-design.md) | `14-api-design.md` | REST, GraphQL, gRPC, WebSockets, API versioning, pagination |

### Part IV - Reliability & Performance

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 15 | [Content Delivery Networks (CDN)](chapters/15-cdn.md) | `15-cdn.md` | Push vs pull CDN, Anycast & GeoDNS routing internals, HTTP/2/HTTP/3/QUIC, edge computing (Workers, Lambda@Edge), video streaming (HLS/DASH, Netflix Open Connect), CDN security (DDoS, WAF, origin cloaking), image optimization, API acceleration, failure modes, real-world architectures |
| 16 | [Rate Limiting & Throttling](chapters/16-rate-limiting.md) | `16-rate-limiting.md` | Token bucket, leaky bucket, sliding window, distributed rate limiting |
| 17 | [Resilience Patterns](chapters/17-resilience-patterns.md) | `17-resilience-patterns.md` | Circuit breaker, bulkhead, retry, timeout, fallback patterns |
| 18 | [Monitoring, Logging & Observability](chapters/18-monitoring.md) | `18-monitoring.md` | Metrics, logs, traces, alerting, SLI/SLO/SLA, distributed tracing |

### Part V - Security & Identity

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 19 | [Authentication & Authorization](chapters/19-auth.md) | `19-auth.md` | OAuth 2.0, OIDC, JWT, SAML, SSO, RBAC, ABAC, ReBAC, mTLS & SPIFFE/SPIRE, passwordless (WebAuthn, passkeys, magic links), microservices auth patterns (gateway, token propagation, token exchange), Zero Trust & BeyondCorp, policy engines (OPA, Cedar), security threats (CSRF, XSS, credential stuffing), token lifecycle management, auth architecture at scale |
| 20 | [Security Patterns](chapters/20-security.md) | `20-security.md` | Encryption at rest/transit, TLS, HTTPS, WAF, DDoS protection, zero trust |

### Part VI - Advanced Architectural Patterns

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 21 | [Microservices Architecture](chapters/21-microservices.md) | `21-microservices.md` | Service decomposition, service mesh, sidecar, API gateway |
| 22 | [Event-Driven Architecture](chapters/22-event-driven.md) | `22-event-driven.md` | Event sourcing, event bus, choreography vs orchestration |
| 23 | [CQRS Pattern](chapters/23-cqrs.md) | `23-cqrs.md` | Command Query Responsibility Segregation, read/write models |
| 24 | [Distributed Transactions](chapters/24-distributed-transactions.md) | `24-distributed-transactions.md` | 2PC, saga pattern, outbox pattern, compensating transactions |
| 25 | [Consensus Algorithms](chapters/25-consensus.md) | `25-consensus.md` | Paxos, Raft, ZAB, leader election, split-brain prevention |
| 26 | [Search Systems](chapters/26-search.md) | `26-search.md` | Inverted index, Elasticsearch, ranking, full-text search, typeahead |
| 35 | [Distributed Systems — The Complete Guide](chapters/35-distributed-systems.md) | `35-distributed-systems.md` | Fundamentals, 8 fallacies, failure modes & detection (heartbeats, phi accrual, SWIM), time & ordering (Lamport clocks, vector clocks, HLC, TrueTime), gossip protocols, distributed coordination (leader election, locks, ZooKeeper, etcd), network partitions & split-brain, Byzantine fault tolerance, 20+ design patterns (WAL, quorum, circuit breaker, saga, event sourcing, outbox, sidecar), observability & tracing, MapReduce, real-world systems (Google, Amazon, Meta) |

### Part VII - Real-World Case Studies

| # | Chapter | File | Description |
|---|---------|------|-------------|
| 27 | [Design a URL Shortener](chapters/27-url-shortener.md) | `27-url-shortener.md` | TinyURL/Bitly - hashing, base62, redirection, analytics |
| 28 | [Design a Social Media Feed](chapters/28-social-feed.md) | `28-social-feed.md` | Twitter/X - fan-out, timeline, ranking, celebrity problem |
| 29 | [Design a Chat System](chapters/29-chat-system.md) | `29-chat-system.md` | WhatsApp/Slack - WebSocket, presence, delivery receipts, groups |
| 30 | [Design a Video Streaming Platform](chapters/30-video-streaming.md) | `30-video-streaming.md` | YouTube/Netflix - transcoding, adaptive bitrate, recommendation |
| 31 | [Design a Ride-Sharing Service](chapters/31-ride-sharing.md) | `31-ride-sharing.md` | Uber/Lyft - geospatial indexing, matching, ETA, surge pricing |
| 32 | [Design a Notification System](chapters/32-notification-system.md) | `32-notification-system.md` | Push, SMS, email - priority, dedup, rate limiting, templates |
| 33 | [Design a File Storage System](chapters/33-file-storage.md) | `33-file-storage.md` | Dropbox/Google Drive - sync, chunking, dedup, conflict resolution |
| 34 | [Design a Typeahead / Autocomplete](chapters/34-typeahead.md) | `34-typeahead.md` | Google search bar - trie, ranking, personalization, real-time update |
| 36 | [Design a Rate Limiter](chapters/36-rate-limiter-design.md) | `36-rate-limiter-design.md` | Token bucket, leaky bucket, sliding window algorithms, distributed rate limiting with Redis Lua scripts, rules engine, 429 handling, API gateway placement |
| 37 | [Design a Booking System](chapters/37-booking-system.md) | `37-booking-system.md` | Airbnb/BookMyShow — preventing double-booking (pessimistic/optimistic locking, Redis distributed lock), search with Elasticsearch, payment integration, flash sale queues |
| 38 | [Design a Stock Exchange](chapters/38-stock-exchange.md) | `38-stock-exchange.md` | NYSE/NASDAQ — order book data structure, price-time priority matching engine, single-threaded design, market data distribution, circuit breakers, microsecond latency optimization |
| 39 | [Design a Highly Available Distributed Database](chapters/39-distributed-database.md) | `39-distributed-database.md` | CockroachDB/Spanner — range-based sharding, Raft consensus per range, distributed transactions (2PC + parallel commits), LSM-Tree storage, online DDL, multi-DC failover |
| 40 | [Design an Event-Driven System](chapters/40-event-driven-system.md) | `40-event-driven-system.md` | E-commerce event backbone — Kafka vs RabbitMQ vs SNS+SQS, event sourcing, CQRS, saga (choreography/orchestration), outbox pattern, CDC, idempotent consumers, schema evolution |
| 41 | [Design a Real-Time Streaming Platform](chapters/41-realtime-streaming.md) | `41-realtime-streaming.md` | Lambda vs Kappa architecture, Flink vs Kafka Streams vs Spark Streaming, windowing (tumbling/sliding/session), watermarks, fraud detection pipeline, IoT platform, state management |
| 42 | [Design AI/ML-Based Services](chapters/42-ai-ml-services.md) | `42-ai-ml-services.md` | Recommendation engine (two-tower model, ANN, feature store), generative AI serving (KV cache, vLLM, PagedAttention, GPU scaling), RAG pipeline (chunking, vector DBs, re-ranking, agentic RAG) |

### Part VIII - Appendices

| # | Chapter | File | Description |
|---|---------|------|-------------|
| A | [HLD Cheat Sheet](appendices/A-cheat-sheet.md) | `A-cheat-sheet.md` | Quick-reference card of all components and when to use them |
| B | [System Design Interview Framework](appendices/B-interview-framework.md) | `B-interview-framework.md` | Step-by-step approach for system design interviews |
| C | [Glossary](appendices/C-glossary.md) | `C-glossary.md` | Definitions of all key terms used in this book |
| D | [References & Further Reading](appendices/D-references.md) | `D-references.md` | Books, papers, blogs, and courses for deeper learning |

---

## How to Use This Book

1. **Beginners**: Start with Part I (Foundations), then work through Part II (Building Blocks) one chapter at a time.
2. **Intermediate**: Skim Part I-II, deep-dive into Part III-IV for data/reliability patterns.
3. **Advanced / Interview Prep**: Jump to Part VI (Advanced Patterns) and Part VII (Case Studies). Use Appendix B for interview structure.
4. **Quick Reference**: Use Appendix A (Cheat Sheet) and Appendix C (Glossary) as lookup tools.

---

*Each chapter is self-contained with diagrams (described in ASCII/text), real-world examples, trade-off analysis, and key takeaways.*
