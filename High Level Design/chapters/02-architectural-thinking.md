# Chapter 2: Architectural Thinking

---

## 2.1 What Makes a Good System Architect?

Being a system architect is not about memorizing design patterns or knowing every database
technology. It is about developing a **way of thinking** --- a mindset that balances competing
concerns and makes deliberate, defensible decisions under uncertainty.

### The Architect's Mindset

```
+---------------------------------------------------------------+
|                   THE ARCHITECT'S MINDSET                     |
+---------------------------------------------------------------+
|                                                               |
|   +-------------------+    +-------------------+              |
|   |   Think in        |    |   Embrace         |              |
|   |   Trade-offs      |    |   Uncertainty      |              |
|   |   (Nothing is     |    |   (You won't have  |              |
|   |    free)           |    |    all the answers)|              |
|   +-------------------+    +-------------------+              |
|                                                               |
|   +-------------------+    +-------------------+              |
|   |   Design for      |    |   Communicate     |              |
|   |   Failure          |    |   Clearly          |              |
|   |   (Everything      |    |   (If you can't   |              |
|   |    will break)     |    |    explain it,     |              |
|   +-------------------+    |    rethink it)     |              |
|                            +-------------------+              |
|                                                               |
|   +-------------------+    +-------------------+              |
|   |   Start Simple,   |    |   Question        |              |
|   |   Evolve Later    |    |   Assumptions     |              |
|   |   (YAGNI)          |    |   (Why this DB?   |              |
|   +-------------------+    |    Why not that?)  |              |
|                            +-------------------+              |
+---------------------------------------------------------------+
```

### Core Skills of a System Architect

| Skill                     | Description                                                    |
|---------------------------|----------------------------------------------------------------|
| **Abstraction**           | Ability to see the forest AND the trees; zoom in and out       |
| **Trade-off Analysis**    | Weighing pros and cons of competing approaches                 |
| **Communication**         | Explaining complex systems to technical and non-technical people|
| **Breadth of Knowledge**  | Familiarity with databases, caches, queues, protocols, etc.    |
| **Depth of Experience**   | Having built and operated systems at scale                     |
| **Pragmatism**            | Choosing "good enough" over "perfect" when appropriate         |
| **Curiosity**             | Continuously learning new technologies and patterns            |

---

## 2.2 Design Principles

Design principles are the guiding rules that help architects make consistent, high-quality
decisions. They are not rigid laws but heuristics that apply in most situations.

### KISS --- Keep It Simple, Stupid

> "Simplicity is the ultimate sophistication." --- Leonardo da Vinci

The simplest design that meets the requirements is usually the best. Complexity is the
enemy of reliability, maintainability, and velocity.

**Example:**
- **Bad:** Using a microservices architecture with Kubernetes, Kafka, and 12 services
  for an internal tool with 50 users.
- **Good:** A single monolithic application with a PostgreSQL database deployed on a
  single server.

### YAGNI --- You Aren't Gonna Need It

Do not build features or infrastructure "just in case." Build for today's requirements
with a *clear path* to extend for tomorrow's needs.

**Example:**
- **Bad:** Building a multi-region, globally distributed system before you have users
  outside a single city.
- **Good:** Deploying in a single region with a documented plan for multi-region expansion
  when international traffic exceeds 20% of total.

### DRY --- Don't Repeat Yourself

Every piece of knowledge should have a single, unambiguous, authoritative representation
within a system.

**At the HLD level**, DRY means:
- Do not have two services that are responsible for the same data entity.
- Do not have two sources of truth for user authentication.
- Centralize cross-cutting concerns (logging, auth, rate-limiting) in shared components.

> **Caution:** In a microservices architecture, DRY is sometimes intentionally violated
> to maintain service independence. This is a deliberate trade-off, not an oversight.

### Separation of Concerns (SoC)

Each component should have a single, well-defined responsibility and should not mix
unrelated functionality.

```
  BAD: One Monolithic Service              GOOD: Separated Concerns
  +---------------------------+            +----------+  +----------+
  |  Handles:                 |            |  Auth    |  | Product  |
  |  - Authentication         |            | Service  |  | Service  |
  |  - Product catalog        |            +----------+  +----------+
  |  - Order processing       |
  |  - Payment handling       |            +----------+  +----------+
  |  - Email notifications    |            |  Order   |  | Payment  |
  |  - Report generation      |            | Service  |  | Service  |
  +---------------------------+            +----------+  +----------+
                                           
                                           +----------+  +----------+
                                           | Notif.   |  | Report   |
                                           | Service  |  | Service  |
                                           +----------+  +----------+
```

### Single Responsibility Principle (SRP)

Closely related to SoC, SRP states that a component should have **one reason to change**.
If a component needs to change for two unrelated reasons, it should be split.

**At the system level:**
- The User Service should change only when user-related requirements change.
- The Payment Service should change only when payment logic changes.
- A change to how emails are sent should NOT require changes to the Order Service.

---

## 2.3 Scalability Dimensions

Scalability is the ability of a system to handle increased load without a proportional
decrease in performance. There are two primary dimensions of scaling.

### Vertical Scaling (Scale Up)

Adding more resources (CPU, RAM, disk) to a **single machine**.

```
  BEFORE                     AFTER (Vertical Scaling)
  +----------+               +------------------+
  |  Server  |               |     Server       |
  |  4 CPU   |     --->      |     32 CPU       |
  |  8 GB RAM|               |     128 GB RAM   |
  |  100 GB  |               |     2 TB SSD     |
  +----------+               +------------------+
```

**Advantages:**
- Simple --- no code changes required
- No distributed system complexity
- Strong consistency is trivial (single node)

**Disadvantages:**
- Hardware limits --- you cannot scale infinitely
- Single point of failure
- Expensive --- high-end machines cost disproportionately more
- Downtime during upgrade (usually)

### Horizontal Scaling (Scale Out)

Adding more **machines** to distribute the load.

```
  BEFORE                     AFTER (Horizontal Scaling)
  +----------+               +----------+  +----------+  +----------+
  |  Server  |               |  Server  |  |  Server  |  |  Server  |
  |  4 CPU   |     --->      |  4 CPU   |  |  4 CPU   |  |  4 CPU   |
  |  8 GB RAM|               |  8 GB RAM|  |  8 GB RAM|  |  8 GB RAM|
  +----------+               +----------+  +----------+  +----------+
                                       |        |        |
                              +--------v--------v--------v--------+
                              |          LOAD BALANCER             |
                              +-----------------------------------+
```

**Advantages:**
- Virtually unlimited scaling
- No single point of failure (with proper design)
- Cost-effective --- use commodity hardware
- Can scale incrementally

**Disadvantages:**
- Distributed system complexity (network partitions, consistency challenges)
- Requires stateless design (or external state management)
- Load balancing and service discovery needed
- Data partitioning and replication complexity

### Comparison Table

| Factor               | Vertical Scaling        | Horizontal Scaling          |
|----------------------|-------------------------|-----------------------------|
| **Complexity**       | Low                     | High                        |
| **Cost Curve**       | Exponential             | Linear                      |
| **Limit**            | Hardware maximum         | Virtually unlimited         |
| **Failure Impact**   | Total (SPOF)            | Partial (one node)          |
| **Data Consistency** | Simple                  | Complex (distributed)       |
| **Downtime to Scale**| Usually required         | Zero downtime possible      |
| **Best For**         | Small-medium workloads  | Large-scale, high-traffic   |

> **Best Practice:** Start with vertical scaling for simplicity. When you approach hardware
> limits or need fault tolerance, transition to horizontal scaling. Many successful
> companies (e.g., Stack Overflow) ran on remarkably few, well-optimized servers for years.

---

## 2.4 The CAP Theorem

The CAP Theorem, proposed by Eric Brewer in 2000 and proven by Seth Gilbert and Nancy Lynch
in 2002, is one of the most fundamental theorems in distributed systems.

### Statement

In a distributed data store, you can only guarantee **two out of three** properties
simultaneously:

```
                         Consistency (C)
                             /\
                            /  \
                           /    \
                          /  CA  \
                         /  (RDBMS)\
                        /    single \
                       /    node     \
                      /_______________\
                     /\              /\
                    /  \            /  \
                   / CP \          / AP \
                  / (HBase,\      /(Cassandra,\
                 / MongoDB) \    / DynamoDB)   \
                /____________\  /______________\
          Partition              Availability (A)
          Tolerance (P)
```

### Definitions

| Property                  | Definition                                                        |
|---------------------------|-------------------------------------------------------------------|
| **Consistency (C)**       | Every read receives the most recent write or an error.            |
| **Availability (A)**      | Every request receives a (non-error) response, without            |
|                           | guaranteeing it contains the most recent write.                   |
| **Partition Tolerance (P)**| The system continues to operate despite network partitions        |
|                           | (messages being dropped or delayed between nodes).                |

### Why You Must Choose P

In any real-world distributed system, **network partitions will happen**. Cables get cut,
switches fail, data centers lose connectivity. Therefore, P is not optional --- you must
have it. The real choice is between:

- **CP (Consistency + Partition Tolerance):** During a partition, the system may refuse
  requests (sacrificing availability) to maintain consistency.
- **AP (Availability + Partition Tolerance):** During a partition, the system continues
  to serve requests but may return stale data (sacrificing consistency).

### Real-World Examples

| System          | Type | Behavior During Partition                                    |
|-----------------|------|--------------------------------------------------------------|
| **HBase**       | CP   | Refuses writes to maintain consistency; unavailable regions  |
| **MongoDB**     | CP   | Primary node handles writes; secondaries may be unavailable  |
| **ZooKeeper**   | CP   | Requires majority quorum; minority partition is unavailable  |
| **Cassandra**   | AP   | Continues serving reads/writes; reconciles later             |
| **DynamoDB**    | AP   | Eventual consistency by default; always available             |
| **CouchDB**     | AP   | Continues operating; conflict resolution on sync             |
| **PostgreSQL**  | CA*  | Single-node: consistent and available, but no partition      |
|                 |      | tolerance (it's not distributed)                             |

> *CA systems only exist in non-distributed, single-node deployments. In a distributed
> context, CA is not practically achievable because partitions cannot be prevented.

### Practical Implications for HLD

When designing your system, ask:
1. **Can the business tolerate stale reads?** If yes, AP is viable.
2. **Must every read reflect the latest write?** If yes, CP is required.
3. **What is the blast radius of unavailability?** Can users retry?

**Example Trade-off:**
- A **banking system** (account balances) requires CP --- showing a stale balance could
  lead to overdrafts.
- A **social media feed** can use AP --- showing a slightly stale post count is acceptable.

---

## 2.5 The PACELC Theorem

The PACELC Theorem extends CAP by addressing what happens when there is **no** partition.

### Statement

> If there is a **P**artition, choose between **A**vailability and **C**onsistency.
> **E**lse (when the system is running normally), choose between **L**atency and **C**onsistency.

```
  +-------------------------------------------+
  |             PACELC Theorem                |
  |                                           |
  |   IF Partition:                           |
  |       Choose: Availability OR Consistency |
  |                                           |
  |   ELSE (normal operation):                |
  |       Choose: Latency OR Consistency      |
  |                                           |
  +-------------------------------------------+
```

### Why PACELC Matters

CAP only describes behavior during a partition, but partitions are rare. Most of the time,
the system is operating normally. PACELC captures the everyday trade-off between latency
and consistency:

- **Low latency** often requires reading from the nearest replica (which may be stale).
- **Strong consistency** often requires coordinating across replicas (which adds latency).

### PACELC Classification of Systems

| System       | Partition (PA/PC) | Else (EL/EC) | Full Classification |
|--------------|-------------------|--------------|---------------------|
| Cassandra    | PA                | EL           | PA/EL               |
| DynamoDB     | PA                | EL           | PA/EL               |
| MongoDB      | PC                | EC           | PC/EC               |
| HBase        | PC                | EC           | PC/EC               |
| PNUTS (Yahoo)| PC                | EL           | PC/EL               |
| Cosmos DB    | PA                | EL (tunable) | PA/EL (configurable)|

> **Key Insight:** PACELC helps you make better technology choices because it considers
> both the exceptional case (partitions) and the common case (normal operation).

---

## 2.6 Latency vs Throughput

These two metrics are often confused but measure fundamentally different things.

### Definitions

- **Latency:** The time it takes to complete a single request (measured in milliseconds).
- **Throughput:** The number of requests the system can handle per unit of time (measured
  in requests per second, or RPS).

### The Relationship

Latency and throughput are **not** simply inversely related. Improving one does not
automatically improve the other:

```
  High Throughput, Low Latency         High Throughput, High Latency
  (The Dream)                          (Batch Processing)
  +----+----+----+----+                +--------+--------+--------+
  | R1 | R2 | R3 | R4 |  <-- Fast     |   R1   |   R2   |   R3   |  <-- Many, but slow
  +----+----+----+----+                +--------+--------+--------+
  Time --->                            Time --->
  
  Low Throughput, Low Latency          Low Throughput, High Latency
  (Under-utilized)                     (The Nightmare)
  +----+    +----+                     +--------+         +--------+
  | R1 |    | R2 |      <-- Fast       |   R1   |         |   R2   |  <-- Slow AND few
  +----+    +----+                     +--------+         +--------+
  Time --->                            Time --->
```

### Trade-off Scenarios

| Scenario                                    | Latency Impact | Throughput Impact |
|---------------------------------------------|----------------|-------------------|
| Adding a cache layer                        | Decreases      | Increases         |
| Adding encryption to all requests           | Increases      | May decrease      |
| Batching writes to database                 | Increases      | Increases         |
| Adding more replicas for reads              | Decreases      | Increases         |
| Requiring synchronous replication           | Increases      | Decreases         |
| Using a CDN for static content              | Decreases      | Increases         |
| Adding request validation/authorization     | Increases      | May decrease      |

> **Design Tip:** In your HLD, specify latency requirements as percentiles (p50, p95, p99)
> rather than averages. Averages hide tail latency problems.

---

## 2.7 Availability: Nines and SLAs

Availability measures the proportion of time a system is operational and accessible.

### The Nines Table

| Availability | Common Name       | Downtime/Year  | Downtime/Month | Downtime/Week  |
|--------------|-------------------|----------------|----------------|----------------|
| 99%          | "Two nines"       | 3.65 days      | 7.31 hours     | 1.68 hours     |
| 99.9%        | "Three nines"     | 8.76 hours     | 43.83 minutes  | 10.08 minutes  |
| 99.95%       | "Three and a half"| 4.38 hours     | 21.92 minutes  | 5.04 minutes   |
| 99.99%       | "Four nines"      | 52.56 minutes  | 4.38 minutes   | 1.01 minutes   |
| 99.999%      | "Five nines"      | 5.26 minutes   | 26.30 seconds  | 6.05 seconds   |
| 99.9999%     | "Six nines"       | 31.56 seconds  | 2.63 seconds   | 0.60 seconds   |

### The Cost of Each Nine

Each additional "nine" of availability is **exponentially more expensive** to achieve:

```
  Cost ($)
    |
    |                                          *  (99.999%)
    |                                     *
    |                                *
    |                          *
    |                    *
    |              *
    |         *
    |     *
    |  *
    +--*------------------------------------------> Availability (%)
      99%   99.9%  99.95% 99.99% 99.999%
```

### SLA Implications

- **99.9%** is reasonable for most B2B SaaS products.
- **99.99%** is expected for critical infrastructure (payment systems, databases).
- **99.999%** is required for life-critical systems (healthcare, emergency services).

### How to Achieve Higher Availability

| Technique                   | Description                                              |
|-----------------------------|----------------------------------------------------------|
| Redundancy                  | Multiple instances of each component                     |
| Load balancing              | Distribute traffic; reroute on failure                   |
| Health checks               | Detect and remove unhealthy instances automatically      |
| Auto-scaling                | Add capacity under load; remove when idle                |
| Multi-region deployment     | Survive entire data center failures                      |
| Circuit breakers            | Prevent cascading failures between services              |
| Graceful degradation        | Serve reduced functionality instead of complete failure   |
| Database replication        | Primary-replica or multi-master setups                   |
| Chaos engineering           | Proactively test failure scenarios (Netflix Chaos Monkey) |

> **Important:** Availability of a system is the **product** of the availability of its
> components in series. If Service A (99.9%) calls Service B (99.9%), the combined
> availability is 99.9% x 99.9% = 99.8%. This means adding more services in the
> critical path *reduces* overall availability unless you add redundancy.

---

## 2.8 Stateless vs Stateful Architecture

### Stateful Architecture

A **stateful** service stores client session data locally. Each request from a client must
go to the **same server** that holds its session.

```
  +--------+         +----------+
  | User A |-------->| Server 1 |  (Session A stored here)
  +--------+         +----------+
  
  +--------+         +----------+
  | User B |-------->| Server 2 |  (Session B stored here)
  +--------+         +----------+
  
  Problem: If Server 1 dies, User A's session is LOST.
  Problem: Cannot freely route User A to Server 2.
```

### Stateless Architecture

A **stateless** service does not store any client session data locally. Each request
contains all the information needed to process it (or references an external state store).

```
  +--------+         +---------------+         +----------+
  | User A |-------->|               |-------->| Server 1 |
  +--------+         |               |         +----------+
                     | Load Balancer |
  +--------+         |               |         +----------+
  | User B |-------->|               |-------->| Server 2 |
  +--------+         +---------------+         +----------+
                                                    |
                     Any server can handle      +---v-------+
                     any request!               | External  |
                                                | State     |
                                                | (Redis /  |
                                                |  DB)      |
                                                +-----------+
```

### Comparison

| Aspect                | Stateful                        | Stateless                        |
|-----------------------|---------------------------------|----------------------------------|
| **Scaling**           | Hard (sticky sessions needed)   | Easy (any instance handles any)  |
| **Fault Tolerance**   | Poor (state lost on failure)    | Excellent (state is external)    |
| **Load Balancing**    | Complex (session affinity)      | Simple (round-robin works)       |
| **Deployment**        | Rolling deploys are complex     | Straightforward                  |
| **State Management**  | Internal (fast access)          | External (network hop required)  |
| **Complexity**        | Simpler initially               | Requires external store setup    |

> **Best Practice for HLD:** Design services to be **stateless** by default. Store
> session state in external stores like Redis or databases. This enables horizontal
> scaling, zero-downtime deployments, and fault tolerance.

### When Stateful Makes Sense

There are cases where stateful design is appropriate:
- **WebSocket connections** --- the connection inherently has state
- **In-memory caching** --- for performance (but should be backed by a shared cache)
- **Game servers** --- where real-time state must be co-located with processing
- **Stream processing** --- where partitioned state improves performance

Even in these cases, the state should be **recoverable** (e.g., from a checkpoint or
external store) so that failover is possible.

---

## 2.9 Coupling vs Cohesion

### Coupling

**Coupling** measures how much one component depends on another. **Low coupling** is desirable
because it means changes to one component do not ripple across the system.

```
  TIGHT COUPLING (Bad)                LOOSE COUPLING (Good)
  
  +--------+                          +--------+
  | Svc A  |-----+                    | Svc A  |
  +--------+     |                    +---+----+
       |         |                        |
       v         v                        v
  +--------+ +--------+              +--------+
  | Svc B  | | Svc C  |              | Message|
  +--------+ +--------+              | Queue  |
       |                              +---+----+
       v                                  |
  +--------+                     +--------+--------+
  | Svc D  |                     |        |        |
  +--------+                     v        v        v
                              +-----+ +-----+ +-----+
  (A knows about B, C, D;    |Svc B| |Svc C| |Svc D|
   B knows about D ---       +-----+ +-----+ +-----+
   changing D breaks B        
   which may break A)         (Services are independent;
                               communicate through queue)
```

### Types of Coupling (from tightest to loosest)

| Type                    | Description                                    | Example                        |
|-------------------------|------------------------------------------------|--------------------------------|
| **Content Coupling**    | One module modifies another's internals         | Direct DB access across services|
| **Common Coupling**     | Modules share global data                       | Shared mutable state            |
| **Control Coupling**    | One module controls another's flow              | Passing control flags           |
| **Stamp Coupling**      | Modules share a data structure                  | Passing entire objects          |
| **Data Coupling**       | Modules share only necessary data               | API with minimal parameters     |
| **Message Coupling**    | Modules communicate only through messages       | Event-driven architecture       |

### Cohesion

**Cohesion** measures how closely related the responsibilities within a single component are.
**High cohesion** is desirable because it means the component has a clear, focused purpose.

| Type                        | Description                                  | Quality    |
|-----------------------------|----------------------------------------------|------------|
| **Coincidental Cohesion**   | Parts are unrelated (utility grab-bag)       | Worst      |
| **Logical Cohesion**        | Parts are related by category, not function  | Poor       |
| **Temporal Cohesion**       | Parts are related by when they execute       | Below Avg  |
| **Procedural Cohesion**     | Parts follow a sequence of steps             | Average    |
| **Communicational Cohesion**| Parts operate on the same data               | Good       |
| **Sequential Cohesion**     | Output of one part is input to another       | Very Good  |
| **Functional Cohesion**     | All parts contribute to a single function    | Best       |

### The Goal

```
+-------------------------------------------+
|                                           |
|   AIM FOR:                                |
|                                           |
|   LOW Coupling  +  HIGH Cohesion          |
|                                           |
|   = Components that are:                  |
|     - Independent of each other           |
|     - Internally focused and purposeful   |
|     - Easy to change, test, and deploy    |
|     - Replaceable without system-wide     |
|       impact                              |
|                                           |
+-------------------------------------------+
```

---

## 2.10 Putting It All Together: Architectural Decision Framework

When faced with an architectural decision, use this framework:

```
  1. UNDERSTAND THE REQUIREMENT
     |
     v
  2. IDENTIFY THE OPTIONS (at least 2-3)
     |
     v
  3. EVALUATE EACH OPTION AGAINST:
     +-- Scalability (Can it handle 10x load?)
     +-- Availability (What happens when it fails?)
     +-- Consistency (Is data correctness critical?)
     +-- Latency (What is the p99 target?)
     +-- Complexity (Can the team build and maintain it?)
     +-- Cost (Infrastructure and operational cost)
     |
     v
  4. DOCUMENT THE TRADE-OFFS
     |
     v
  5. MAKE A DECISION AND JUSTIFY IT
     |
     v
  6. DOCUMENT "WHY NOT" FOR REJECTED OPTIONS
```

### Example: Choosing a Communication Pattern Between Services

| Criteria         | REST (Synchronous)    | Message Queue (Async)    | gRPC (Synchronous)     |
|------------------|-----------------------|--------------------------|------------------------|
| Latency          | Medium (~50-100ms)    | Higher (queuing delay)   | Low (~10-30ms)         |
| Coupling         | Moderate              | Very Low                 | Moderate               |
| Complexity       | Low                   | Medium                   | Medium                 |
| Reliability      | Caller retries needed | Built-in (queue persists)| Caller retries needed  |
| Debugging        | Easy (HTTP tools)     | Harder (async flows)     | Medium (binary format) |
| Team Familiarity | High                  | Medium                   | Low                    |
| **Decision**     |                       | **Chosen for events**    | **Chosen for svc-svc** |

**Reasoning:** Use gRPC for synchronous service-to-service calls (low latency, strong typing)
and a message queue (RabbitMQ/Kafka) for event-driven communication (decoupling, reliability).

---

## 2.11 Key Takeaways

```
+-------------------------------------------------------------------+
|                        KEY TAKEAWAYS                              |
+-------------------------------------------------------------------+
|                                                                   |
|  1. Architectural thinking is about trade-offs, not perfection.   |
|     Every decision has costs and benefits.                        |
|                                                                   |
|  2. Core principles --- KISS, YAGNI, DRY, SoC, SRP --- guide     |
|     you toward simple, maintainable designs.                      |
|                                                                   |
|  3. Prefer horizontal scaling for production systems, but start   |
|     simple with vertical scaling when appropriate.                |
|                                                                   |
|  4. CAP Theorem: In distributed systems, choose CP or AP.         |
|     PACELC extends this to normal operation (latency vs           |
|     consistency).                                                 |
|                                                                   |
|  5. Each "nine" of availability is exponentially more expensive.  |
|     Know what your business actually needs.                       |
|                                                                   |
|  6. Design stateless services by default. Externalize state to    |
|     shared stores (Redis, databases).                             |
|                                                                   |
|  7. Aim for low coupling and high cohesion. Use message queues    |
|     and well-defined APIs to decouple services.                   |
|                                                                   |
|  8. Always consider at least 2-3 alternatives before making an    |
|     architectural decision, and document why you rejected the     |
|     alternatives.                                                 |
|                                                                   |
|  9. Latency and throughput are different metrics. Optimize for    |
|     the one that matters most to your use case, and measure       |
|     latency in percentiles (p50, p95, p99).                      |
|                                                                   |
+-------------------------------------------------------------------+
```

---

**Next Chapter:** [Chapter 3: Requirements Gathering for System Design](./03-requirements-gathering.md)
--- where we learn how to extract, organize, and prioritize the requirements that drive
every architectural decision.
