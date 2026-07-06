# Appendix C: Glossary

> An alphabetical reference of 120+ terms used throughout this book. Each entry provides a concise definition (1-3 sentences) to help you quickly recall concepts during study or interviews.

---

## A

**ACID**
A set of properties guaranteeing reliable database transactions: **A**tomicity (all or nothing), **C**onsistency (valid state transitions), **I**solation (concurrent transactions don't interfere), **D**urability (committed data survives crashes). ACID is the hallmark of relational databases like PostgreSQL and MySQL.

**Active-Active**
A high-availability configuration where multiple nodes simultaneously handle requests. If one node fails, the others continue serving traffic with no failover delay. Contrast with Active-Passive.

**Active-Passive**
A high-availability configuration where a standby node takes over only when the primary (active) node fails. The passive node is idle during normal operation, leading to simpler consistency but slower failover.

**API Gateway**
A single entry point that sits in front of multiple backend services, handling cross-cutting concerns such as authentication, rate limiting, request routing, protocol translation, and response aggregation. Common implementations include Kong, AWS API Gateway, and NGINX.

**Availability**
The proportion of time a system is operational and accessible, typically expressed as a percentage (e.g., 99.99% = "four nines" = ~52 minutes of downtime per year). Higher availability usually requires redundancy, failover mechanisms, and geographic distribution.

**Auto-Scaling**
The ability to automatically add or remove compute instances based on real-time demand metrics (CPU, memory, request count). Cloud providers offer this natively (AWS Auto Scaling, GCP Managed Instance Groups) to handle traffic spikes cost-effectively.

## B

**Back Pressure**
A flow-control mechanism where a downstream system signals an upstream system to slow down when it's overwhelmed. This prevents cascading failures by letting producers know that consumers can't keep up.

**BASE**
An alternative to ACID for distributed systems: **B**asically **A**vailable, **S**oft state, **E**ventually consistent. BASE trades strict consistency for higher availability and partition tolerance, common in NoSQL databases.

**Bloom Filter**
A space-efficient probabilistic data structure that tests whether an element is a member of a set. It can produce false positives but never false negatives. Used to avoid expensive disk lookups (e.g., in LSM-tree databases like Cassandra and LevelDB).

**Blue-Green Deployment**
A release strategy where two identical production environments ("blue" and "green") exist side by side. New code is deployed to the inactive environment; once verified, traffic is switched over, enabling instant rollback by switching back.

**Bulkhead**
A resilience pattern that isolates different parts of a system so that a failure in one component doesn't cascade to others. Named after ship bulkheads that contain flooding, it's implemented via separate thread pools, process isolation, or service segmentation.

## C

**Cache Aside (Lazy Loading)**
A caching strategy where the application checks the cache first; on a miss, it reads from the database, stores the result in the cache, and returns it. The cache is populated on demand, keeping it lean but requiring a cache miss on the first read.

**Cache Stampede (Thundering Herd)**
A situation where many requests simultaneously miss the cache (often when a popular key expires), all hitting the database at once. Mitigated by request coalescing, locking, or staggered TTLs.

**Canary Deployment**
A release strategy where a new version is rolled out to a small subset of users or servers first. If no issues arise, the rollout gradually expands to the full fleet. This limits the blast radius of bugs.

**CAP Theorem**
States that a distributed system can provide at most two of three guarantees simultaneously: **C**onsistency (every read gets the latest write), **A**vailability (every request gets a response), and **P**artition tolerance (the system works despite network splits). Since network partitions are inevitable, the real choice is between consistency and availability during a partition.

**CDN (Content Delivery Network)**
A geographically distributed network of edge servers that cache and serve content (images, videos, static files) close to end users. CDNs reduce latency, offload traffic from the origin server, and improve reliability. Major providers include Cloudflare, Akamai, and AWS CloudFront.

**Circuit Breaker**
A resilience pattern that prevents a service from repeatedly calling a failing downstream dependency. After a threshold of failures, the circuit "opens" and requests fail fast (or return a fallback). After a timeout, it "half-opens" to test recovery.

**Consistent Hashing**
A technique for distributing data across nodes such that adding or removing a node only requires remapping a small fraction of keys (approximately K/N, where K is the number of keys and N is the number of nodes). Essential for distributed caches and databases.

**Consensus Algorithm**
A protocol that enables distributed nodes to agree on a single value or state, even in the presence of failures. Examples include Paxos, Raft, and Zab (ZooKeeper). Consensus is the foundation of leader election and replicated state machines.

**CQRS (Command Query Responsibility Segregation)**
An architectural pattern that separates read (query) models from write (command) models. Writes go to an optimized write store, while reads are served from a denormalized read store, allowing each to be scaled and optimized independently.

**CRDT (Conflict-free Replicated Data Type)**
A data structure that can be replicated across multiple nodes and updated independently and concurrently without coordination, with a mathematically guaranteed merge function that resolves conflicts automatically. Used in collaborative editing and distributed counters.

## D

**DAG (Directed Acyclic Graph)**
A graph with directed edges and no cycles, commonly used to model task dependencies in workflow engines (e.g., Apache Airflow), build systems, and data pipelines.

**Data Lake**
A centralized repository that stores raw data at any scale in its native format (structured, semi-structured, or unstructured). Unlike a data warehouse, data is schema-on-read rather than schema-on-write. Commonly built on S3, HDFS, or Azure Data Lake Storage.

**Dead Letter Queue (DLQ)**
A special queue where messages that cannot be processed successfully after multiple retries are sent. DLQs prevent poison messages from blocking the main processing queue and provide a way to inspect and reprocess failed messages.

**Denormalization**
The practice of duplicating or pre-computing data across tables to reduce the number of joins needed at read time. It trades write complexity and storage for faster reads, and is common in read-heavy systems and NoSQL databases.

**DNS (Domain Name System)**
The internet's phone book: a hierarchical, distributed system that translates human-readable domain names (e.g., www.example.com) into IP addresses. DNS resolution adds latency to the first request and can be used for simple load balancing (DNS round-robin).

**Durability**
The guarantee that once a transaction is committed, its data will persist even in the case of system crashes, power failures, or hardware faults. Achieved through write-ahead logs, replication, and backups.

## E

**EDA (Event-Driven Architecture)**
An architectural pattern where components communicate by producing and consuming events (immutable records of state changes). EDA enables loose coupling, high scalability, and real-time processing. Core technologies include Kafka, RabbitMQ, and AWS EventBridge.

**Elasticsearch**
A distributed, RESTful search and analytics engine built on Apache Lucene. It excels at full-text search, log analytics, and real-time indexing. Commonly used as part of the ELK stack (Elasticsearch, Logstash, Kibana) or OpenSearch.

**Eventual Consistency**
A consistency model where, after a write, all replicas will eventually converge to the same value, but reads may temporarily return stale data. This model prioritizes availability and performance, and is common in DNS, CDN caches, and NoSQL databases.

**Event Sourcing**
A pattern where state changes are stored as an immutable, append-only sequence of events rather than overwriting the current state. The current state is derived by replaying events. This provides a complete audit trail and enables temporal queries.

## F

**Failover**
The process of automatically switching to a standby system when the primary system fails. Failover can be manual or automatic and applies to databases, servers, DNS, and entire regions.

**Fan-out**
The process of distributing a single event or message to multiple recipients. **Fan-out on write** pre-distributes data to all recipients at write time (fast reads). **Fan-out on read** assembles data from multiple sources at read time (less write amplification).

**Fault Tolerance**
A system's ability to continue operating correctly even when some of its components fail. Achieved through redundancy (replication), isolation (bulkheads), and graceful degradation (serving partial results).

**Feature Flag (Feature Toggle)**
A mechanism to enable or disable features in production without deploying new code. Feature flags support canary releases, A/B testing, and gradual rollouts.

## G

**Geohash**
A hierarchical spatial data structure that encodes geographic coordinates (latitude, longitude) into a short alphanumeric string. Nearby locations share a common prefix, making it efficient for proximity searches. Used in location-based services and databases like Redis.

**gRPC**
A high-performance, open-source RPC framework developed by Google that uses HTTP/2 for transport and Protocol Buffers for serialization. It supports unary, client-streaming, server-streaming, and bidirectional streaming calls. Ideal for low-latency microservice communication.

**GraphQL**
A query language for APIs (developed by Facebook) that lets clients request exactly the data they need in a single request. Unlike REST, the client defines the response shape, reducing over-fetching and under-fetching. Trade-offs include added server complexity and potential for expensive queries.

## H

**HDFS (Hadoop Distributed File System)**
A distributed file system designed to store very large files across clusters of commodity hardware with high fault tolerance. HDFS splits files into large blocks (128 MB default) and replicates them across nodes.

**Health Check**
A periodic probe (HTTP endpoint, TCP ping, or script) that monitoring systems use to determine whether a service instance is alive and ready to accept traffic. Load balancers use health checks to route traffic away from unhealthy instances.

**Horizontal Scaling (Scaling Out)**
Adding more machines to a system to handle increased load, as opposed to upgrading a single machine (vertical scaling). Horizontal scaling is the foundation of cloud-native architectures and requires stateless services or distributed state management.

**Hot Spot (Hot Key)**
A disproportionate concentration of traffic or data on a single node or partition, leading to performance bottlenecks. Hot spots arise from skewed data distribution or viral content. Mitigation strategies include key salting, caching, and request distribution.

## I

**Idempotency**
The property of an operation where performing it multiple times produces the same result as performing it once. Critical in distributed systems for safe retries -- if a request times out, the client can retry without causing duplicate side effects (e.g., double charging).

**Inverted Index**
A data structure that maps content (words, terms) to their locations in a set of documents. It's the core data structure behind full-text search engines like Elasticsearch, Solr, and Lucene, enabling fast keyword lookups across millions of documents.

## J

**JWT (JSON Web Token)**
A compact, URL-safe token format for securely transmitting claims between parties. A JWT is digitally signed (and optionally encrypted), allowing the recipient to verify its authenticity without contacting the issuer. Commonly used for stateless authentication in APIs.

## K

**Kafka (Apache Kafka)**
A distributed event streaming platform capable of handling millions of events per second. Kafka uses a log-based architecture with partitioned topics, consumer groups, and configurable retention. It serves as the backbone for event-driven architectures, data pipelines, and real-time analytics.

## L

**Latency**
The time elapsed between initiating a request and receiving the first byte of the response. Measured in percentiles (p50, p95, p99), latency is affected by network distance, processing time, queue depth, and serialization overhead.

**Leader Election**
A coordination process by which distributed nodes agree on a single leader responsible for certain operations (e.g., writes, coordination). Implemented via consensus algorithms (Raft, Paxos) or coordination services (ZooKeeper, etcd).

**Leaky Bucket**
A rate limiting algorithm that processes requests at a fixed rate, regardless of burst patterns. Requests enter a queue (the "bucket"); if the bucket is full, new requests are dropped. It smooths out traffic but doesn't allow any bursting.

**Linearizability**
The strongest consistency guarantee: every operation appears to take effect atomically at some point between its invocation and completion. A linearizable system behaves as if there's a single copy of the data. Expensive to achieve in distributed systems.

**Load Balancer**
A component that distributes incoming network traffic across multiple servers to ensure no single server bears too much load. Load balancers improve availability and throughput. They operate at Layer 4 (TCP/UDP) or Layer 7 (HTTP/application).

**Load Shedding**
The practice of intentionally dropping or rejecting a portion of incoming requests when a system is overloaded, to preserve the ability to serve the remaining requests successfully. Prioritizes important traffic over less critical requests.

**LSM Tree (Log-Structured Merge Tree)**
A data structure optimized for write-heavy workloads. Writes go to an in-memory buffer (memtable), which is periodically flushed to disk as sorted files (SSTables) and merged in the background. Used by Cassandra, RocksDB, LevelDB, and HBase.

## M

**MapReduce**
A programming model for processing large data sets in parallel across a cluster. Data is processed in two phases: **Map** (filter and transform) and **Reduce** (aggregate). Pioneered by Google and implemented in Hadoop, though largely superseded by Apache Spark.

**Message Broker**
A middleware component that translates messages between sender and receiver protocols, routes messages, and provides queuing. Examples include RabbitMQ, ActiveMQ, and Amazon SQS. Brokers enable asynchronous, decoupled communication between services.

**Microservices**
An architectural style where an application is structured as a collection of small, independently deployable services, each owning its own data and communicating via APIs or events. Benefits include independent scaling and deployment; costs include distributed systems complexity.

**Monolith**
An architectural style where the entire application is built and deployed as a single unit. Simpler to develop and test initially, but harder to scale, deploy, and maintain as the codebase grows. Many systems start as monoliths and evolve to microservices.

**mTLS (Mutual TLS)**
A security protocol where both the client and server authenticate each other using TLS certificates. Unlike standard TLS (where only the server proves its identity), mTLS ensures that both parties are trusted. Common in service mesh architectures (e.g., Istio).

## N

**NAT (Network Address Translation)**
A method of mapping private IP addresses to public IP addresses, allowing multiple devices on a private network to share a single public IP. NAT is fundamental to how corporate networks and cloud VPCs connect to the internet.

**NoSQL**
A broad category of databases that don't use the traditional relational model. Types include key-value (Redis, DynamoDB), document (MongoDB), wide-column (Cassandra, HBase), and graph (Neo4j). NoSQL databases are designed for specific access patterns, horizontal scalability, and flexible schemas.

## O

**OAuth 2.0**
An authorization framework that allows third-party applications to access a user's resources without exposing their credentials. OAuth defines grant types (authorization code, client credentials, etc.) and access tokens. It's the standard behind "Sign in with Google/GitHub."

**Observability**
The ability to understand the internal state of a system from its external outputs. The three pillars of observability are **metrics** (numerical measurements), **logs** (event records), and **traces** (request flow across services). Tools include Prometheus, Grafana, Datadog, and Jaeger.

**Optimistic Concurrency Control**
A concurrency strategy that allows multiple transactions to proceed without locking, detecting conflicts at commit time. If a conflict is detected (e.g., a version number has changed), the transaction is retried. Works well when conflicts are rare.

**Outbox Pattern**
A pattern for reliably publishing events in a microservices architecture. Instead of publishing an event directly (which risks dual-write failures), the service writes the event to an "outbox" table in the same database transaction, and a separate process polls the outbox and publishes events.

## P

**PACELC Theorem**
An extension of CAP that states: if there is a **P**artition, choose between **A**vailability and **C**onsistency; **E**lse (when the system is running normally), choose between **L**atency and **C**onsistency. PACELC captures the everyday latency-consistency trade-off, not just during partitions.

**Pagination**
A technique for breaking large result sets into smaller pages to reduce response size and improve performance. Common strategies include **offset-based** (LIMIT/OFFSET -- simple but slow for deep pages), **cursor-based** (keyset pagination -- efficient for large data sets), and **token-based**.

**Partitioning (Sharding)**
The process of splitting a large data set across multiple database instances (shards), each holding a subset of the data. Partitioning strategies include hash-based (uniform distribution), range-based (sequential scans), and directory-based (lookup table).

**Paxos**
One of the earliest consensus algorithms for achieving agreement among distributed nodes, even with failures. Paxos is notoriously difficult to understand and implement correctly, which led to the development of Raft as a more understandable alternative.

**PoP (Point of Presence)**
A physical location (data center or edge node) where a CDN, ISP, or cloud provider has servers. PoPs are distributed globally to serve content with low latency by being geographically close to users.

**Pub/Sub (Publish/Subscribe)**
A messaging pattern where publishers send messages to a topic (not directly to subscribers), and all subscribers to that topic receive the messages. This decouples producers from consumers. Implemented by Kafka topics, Google Cloud Pub/Sub, AWS SNS, and Redis Pub/Sub.

## Q

**Quadtree**
A tree data structure where each internal node has exactly four children, used to partition a two-dimensional space recursively. Quadtrees are used in location-based services for spatial indexing, enabling efficient queries like "find all restaurants within 1 km."

**Quorum**
The minimum number of nodes that must agree on an operation for it to be considered successful. In a system with N replicas, a common quorum is W + R > N (where W = write quorum, R = read quorum), ensuring that reads and writes overlap on at least one node.

## R

**Raft**
A consensus algorithm designed to be more understandable than Paxos while providing the same guarantees. Raft uses leader election, log replication, and safety rules to ensure all nodes agree on the same sequence of commands. Used in etcd, CockroachDB, and TiKV.

**Rate Limiting**
The practice of controlling the number of requests a client can make to a service within a given time window. Protects services from abuse, DDoS attacks, and traffic spikes. Common algorithms include token bucket, leaky bucket, fixed window, and sliding window.

**Read Replica**
A copy of a database that receives replicated writes from the primary (leader) and serves read queries. Read replicas scale read throughput horizontally but introduce replication lag (eventual consistency between primary and replica).

**Redis**
An in-memory data structure store used as a cache, message broker, and database. Redis supports strings, hashes, lists, sets, sorted sets, streams, and more. It achieves ~100K-200K operations per second and offers optional persistence (RDB snapshots, AOF).

**Replication**
The process of copying data from one database node (leader) to one or more other nodes (followers) to improve availability, durability, and read throughput. Replication can be synchronous (strong consistency, higher latency) or asynchronous (eventual consistency, lower latency).

**REST (Representational State Transfer)**
An architectural style for designing networked APIs using standard HTTP methods (GET, POST, PUT, DELETE) and resource-oriented URLs. REST is stateless, cacheable, and the most common API style for public-facing services.

**Retry (with Exponential Backoff)**
A resilience strategy where failed requests are retried after increasing delays (e.g., 1s, 2s, 4s, 8s) to avoid overwhelming a recovering service. Often combined with jitter (random variation) to prevent synchronized retry storms.

**Reverse Proxy**
A server that sits in front of backend servers and forwards client requests to them. Unlike a forward proxy (which acts on behalf of the client), a reverse proxy acts on behalf of the server. It provides load balancing, caching, TLS termination, and security.

## S

**Saga**
A pattern for managing distributed transactions across multiple microservices without two-phase commit. A saga is a sequence of local transactions where each step publishes an event that triggers the next step, and compensating transactions undo previous steps if something fails. Two types: choreography (event-driven) and orchestration (central coordinator).

**Scalability**
A system's ability to handle increased load by adding resources. **Vertical scalability** means upgrading to a more powerful machine. **Horizontal scalability** means adding more machines. A scalable system maintains performance as load grows.

**Service Discovery**
The mechanism by which services in a distributed system locate each other dynamically at runtime, rather than using hard-coded addresses. Implemented via client-side discovery (e.g., with a registry like Consul or etcd) or server-side discovery (e.g., via a load balancer).

**Service Mesh**
A dedicated infrastructure layer for managing service-to-service communication in a microservices architecture. A service mesh (e.g., Istio, Linkerd) handles traffic management, security (mTLS), observability, and resilience (retries, circuit breakers) via sidecar proxies.

**Sharding**
See **Partitioning**. The terms are often used interchangeably. Sharding specifically emphasizes distributing data across multiple database instances to scale horizontally.

**SLA / SLO / SLI**
**SLA (Service Level Agreement):** A contractual commitment to a certain level of service (e.g., 99.9% uptime). **SLO (Service Level Objective):** An internal target (usually stricter than the SLA). **SLI (Service Level Indicator):** The actual measured metric (e.g., percentage of requests served in < 200ms).

**Snowflake ID**
A distributed unique ID generation scheme (originated at Twitter) that produces 64-bit IDs composed of a timestamp, machine/datacenter ID, and sequence number. Snowflake IDs are roughly time-sortable and don't require coordination between nodes.

**Split Brain**
A failure scenario in distributed systems where network partitioning causes two or more parts of the system to believe they are the leader, leading to conflicting writes and data divergence. Mitigated by quorum-based leader election and fencing tokens.

**SQL (Structured Query Language)**
The standard language for managing and querying relational databases. SQL databases (PostgreSQL, MySQL, Oracle, SQL Server) use tables with fixed schemas, support ACID transactions, and excel at complex queries involving joins.

**SSO (Single Sign-On)**
An authentication scheme that allows users to log in once and access multiple applications without re-authenticating. SSO is typically implemented via protocols like SAML, OAuth 2.0, or OpenID Connect.

**Sticky Session (Session Affinity)**
A load balancing technique that routes all requests from a given user to the same backend server. While it simplifies session management, it reduces the effectiveness of load balancing and makes failover harder. Prefer stateless services with externalized session storage (e.g., Redis).

**Strangler Fig Pattern**
A migration strategy for incrementally replacing a legacy monolith with new microservices. New functionality is built as a separate service, and traffic is gradually redirected from the monolith to the new service, until the monolith is fully replaced.

**Surge Pricing**
A dynamic pricing model (popularized by Uber) that increases prices during periods of high demand to balance supply and demand. From a systems perspective, it involves real-time demand estimation, pricing algorithms, and geospatial analysis.

## T

**Throughput**
The number of operations or requests a system can handle per unit of time (e.g., requests per second, transactions per second, megabytes per second). Throughput and latency are often inversely related under load.

**TLS (Transport Layer Security)**
A cryptographic protocol that provides privacy and data integrity between two communicating applications. TLS encrypts data in transit and is the "S" in HTTPS. TLS 1.3 is the latest version, offering improved security and reduced handshake latency.

**Token Bucket**
A rate limiting algorithm where a bucket holds a fixed number of tokens, and tokens are added at a fixed rate. Each request consumes one token; if the bucket is empty, the request is rejected. Token bucket allows controlled bursting (up to the bucket's capacity).

**Trie (Prefix Tree)**
A tree data structure where each node represents a character, and paths from root to nodes represent prefixes. Tries enable efficient prefix matching and are used in autocomplete systems, IP routing tables, and spell checkers.

**TTL (Time to Live)**
A mechanism that specifies how long a piece of data should be considered valid. After the TTL expires, the data is evicted from cache or deleted. TTLs are used in caching (Redis, CDN), DNS records, and message queues.

## V

**Vector Clock**
A data structure used in distributed systems to track causality between events. Each node maintains a vector of logical timestamps, one per node. Vector clocks detect concurrent events and causal relationships, enabling conflict detection in eventually consistent systems (e.g., Amazon Dynamo).

**Vertical Scaling (Scaling Up)**
Increasing the capacity of a single machine by adding more CPU, RAM, disk, or network bandwidth. Vertical scaling is simpler than horizontal scaling but has physical limits and creates a single point of failure.

**VPC (Virtual Private Cloud)**
An isolated virtual network within a public cloud provider (AWS, GCP, Azure) that gives you control over IP addressing, subnets, routing, and security. VPCs provide network-level isolation for your cloud resources.

## W

**WAF (Web Application Firewall)**
A security layer that monitors, filters, and blocks HTTP traffic to/from a web application based on a set of rules. WAFs protect against common attacks like SQL injection, cross-site scripting (XSS), and DDoS. Examples include AWS WAF, Cloudflare WAF, and ModSecurity.

**WebSocket**
A communication protocol providing full-duplex, persistent connections over a single TCP connection. After an HTTP upgrade handshake, client and server can send messages in both directions at any time. Ideal for real-time applications like chat, gaming, and live dashboards.

**Write-Ahead Log (WAL)**
A technique where every modification to the database is first recorded in an append-only log before being applied to the actual data files. WAL ensures durability (changes can be replayed after a crash) and is used by virtually all relational databases, Kafka, and etcd.

## Z

**Zero Trust**
A security model that assumes no implicit trust based on network location. Every request must be authenticated, authorized, and encrypted, regardless of whether it originates inside or outside the network perimeter. Implemented via mTLS, identity-aware proxies, and fine-grained access control.

**ZooKeeper (Apache ZooKeeper)**
A centralized coordination service for distributed applications, providing distributed configuration management, naming, synchronization (locks), and leader election. ZooKeeper uses the Zab consensus protocol and is used by Kafka (older versions), HBase, and Solr.

---

> **Tip:** Use this glossary as a quick refresher before interviews. If you can explain each term in your own words, you have a solid grasp of the system design vocabulary.
