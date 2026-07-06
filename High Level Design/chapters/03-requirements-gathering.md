# Chapter 3: Requirements Gathering for System Design

---

## 3.1 Why Requirements Gathering is the FIRST Step

Every great system begins with a deep understanding of what it must do and how well it must
do it. **Requirements gathering is the single most important step in High Level Design.**
A beautifully architected system that solves the wrong problem is a failure.

```
+-------------------------------------------------------------------+
|                    THE GOLDEN RULE                                |
|                                                                   |
|  "A system designed without clear requirements is a solution      |
|   looking for a problem."                                         |
|                                                                   |
|   Requirements --> Estimation --> Architecture --> Implementation |
|       ^                                                           |
|       |                                                           |
|       +---- ALWAYS start here                                    |
+-------------------------------------------------------------------+
```

### What Happens When You Skip Requirements Gathering

| Symptom                                | Root Cause                                    |
|----------------------------------------|-----------------------------------------------|
| System is over-engineered              | Did not understand the actual scale            |
| System cannot handle production load   | Did not estimate traffic or storage            |
| Key features are missing               | Did not ask the right questions                |
| Architecture does not fit use case     | Assumed requirements instead of discovering    |
| Team disagrees on what to build        | No shared understanding of requirements        |
| Constant rework and scope changes      | Requirements were never pinned down            |

### The Requirements Funnel

Requirements flow from broad business needs to specific technical constraints:

```
  +---------------------------------------------+
  |          BUSINESS REQUIREMENTS               |   "We need an online store"
  +---------------------------------------------+
                     |
                     v
  +---------------------------------------------+
  |        FUNCTIONAL REQUIREMENTS (FR)          |   "Users can search products,
  |                                               |    add to cart, and checkout"
  +---------------------------------------------+
                     |
                     v
  +---------------------------------------------+
  |     NON-FUNCTIONAL REQUIREMENTS (NFR)        |   "99.9% availability,
  |                                               |    < 200ms search latency"
  +---------------------------------------------+
                     |
                     v
  +---------------------------------------------+
  |             CONSTRAINTS                       |   "Must use AWS, team of 5,
  |                                               |    launch in 3 months"
  +---------------------------------------------+
                     |
                     v
  +---------------------------------------------+
  |          CAPACITY ESTIMATION                  |   "1M DAU, 100 QPS avg,
  |                                               |    500 QPS peak, 10 TB storage"
  +---------------------------------------------+
```

---

## 3.2 Functional Requirements (FR)

Functional requirements describe **what the system should do** --- the features and behaviors
that users and other systems can observe.

### How to Express Functional Requirements

Good functional requirements are:
- **Specific** --- not vague
- **Testable** --- you can verify if they are met
- **User-centric** --- described from the user's perspective
- **Prioritized** --- labeled as must-have, should-have, or nice-to-have

### The MoSCoW Framework

| Priority       | Description                                            | Example                         |
|----------------|--------------------------------------------------------|---------------------------------|
| **Must Have**  | Core functionality; system is useless without it       | User can create an account      |
| **Should Have**| Important but not critical for launch                  | User can reset password via email|
| **Could Have** | Nice to have; include if time permits                  | User can set a profile picture  |
| **Won't Have** | Explicitly out of scope for this version               | User can log in with biometrics |

### Examples: Functional Requirements for Different Systems

#### URL Shortener
```
FR-1: Users can submit a long URL and receive a shortened URL (Must Have)
FR-2: Users can click a shortened URL and be redirected to the original URL (Must Have)
FR-3: Users can set a custom alias for the shortened URL (Should Have)
FR-4: Users can set an expiration time for a shortened URL (Should Have)
FR-5: Users can view click analytics for their shortened URLs (Could Have)
FR-6: Users can create an account to manage their URLs (Could Have)
```

#### Chat Application
```
FR-1: Users can send 1-to-1 text messages in real time (Must Have)
FR-2: Users can create group chats with up to 500 members (Must Have)
FR-3: Users can see online/offline status of contacts (Should Have)
FR-4: Users can send images and files up to 50 MB (Should Have)
FR-5: Users can see read receipts (Could Have)
FR-6: Users can react to messages with emojis (Could Have)
FR-7: Users can make voice/video calls (Won't Have --- Phase 2)
```

#### Social Media Feed
```
FR-1: Users can create posts with text and images (Must Have)
FR-2: Users can follow/unfollow other users (Must Have)
FR-3: Users see a personalized feed of posts from followed users (Must Have)
FR-4: Users can like and comment on posts (Must Have)
FR-5: Users can share/repost content (Should Have)
FR-6: Users can search for other users and posts (Should Have)
FR-7: Users can receive push notifications for interactions (Could Have)
```

---

## 3.3 Non-Functional Requirements (NFR)

Non-functional requirements describe **how well** the system performs --- the quality
attributes that determine user satisfaction and operational viability.

> **Critical Insight:** NFRs are often MORE important than FRs in system design interviews
> and real-world architectures. A system that has all features but is slow, unreliable,
> or insecure will fail.

### The Key Non-Functional Requirements

```
+-------------------------------------------------------------------+
|                NON-FUNCTIONAL REQUIREMENTS                        |
|                                                                   |
|   +-------------+  +-------------+  +-------------+              |
|   | Scalability |  |Availability |  |  Latency    |              |
|   | (How much   |  |(How often   |  | (How fast   |              |
|   |  load?)     |  | is it up?)  |  |  is it?)    |              |
|   +-------------+  +-------------+  +-------------+              |
|                                                                   |
|   +-------------+  +-------------+  +-------------+              |
|   | Consistency |  | Durability  |  |  Security   |              |
|   | (How correct|  |(Will data   |  | (How safe   |              |
|   |  is data?)  |  | survive?)   |  |  is it?)    |              |
|   +-------------+  +-------------+  +-------------+              |
|                                                                   |
|   +-------------+  +-------------+  +-------------+              |
|   |Maintainab.  |  |  Testability|  | Observab.   |              |
|   |(How easy to |  |(How easy to |  | (Can we see |              |
|   | change?)    |  | verify?)    |  |  what's     |              |
|   +-------------+  +-------------+  |  happening?)|              |
|                                      +-------------+              |
+-------------------------------------------------------------------+
```

### Deep Dive into Each NFR

#### 1. Scalability

- **Question:** How many users, requests, and data volume must the system support?
- **Metrics:** DAU, QPS (average and peak), data growth rate
- **Design Impact:** Choice of database, caching strategy, service architecture

| Scale Tier  | Users (DAU)    | Typical Architecture                              |
|-------------|----------------|----------------------------------------------------|
| Small       | < 10K          | Single server, monolith, relational DB              |
| Medium      | 10K - 1M       | Load-balanced servers, read replicas, caching       |
| Large       | 1M - 100M      | Microservices, sharding, CDN, message queues        |
| Massive     | > 100M         | Multi-region, custom infrastructure, edge computing |

#### 2. Availability

- **Question:** What percentage of uptime is required?
- **Metrics:** Uptime percentage (nines), MTTR (Mean Time To Recovery)
- **Design Impact:** Redundancy, failover strategy, multi-region deployment

#### 3. Latency

- **Question:** How fast must the system respond?
- **Metrics:** p50, p95, p99 response times
- **Design Impact:** Caching, CDN, database indexing, geographic distribution

| Use Case                | Acceptable p99 Latency |
|-------------------------|------------------------|
| Search autocomplete     | < 50ms                 |
| Web page load           | < 200ms                |
| API response             | < 500ms                |
| File upload processing  | < 5 seconds            |
| Report generation       | < 30 seconds           |
| Batch data processing   | < 1 hour               |

#### 4. Consistency

- **Question:** How important is it that all users see the same data at the same time?
- **Metrics:** Consistency model (strong, eventual, causal)
- **Design Impact:** Database choice, replication strategy, caching invalidation

| Use Case                | Required Consistency | Reasoning                          |
|-------------------------|---------------------|------------------------------------|
| Bank account balance    | Strong              | Incorrect balance = financial loss  |
| Shopping cart            | Strong              | Items must not disappear            |
| Social media likes count| Eventual            | Slight delay is acceptable          |
| Analytics dashboard     | Eventual            | Data can be minutes behind          |
| Inventory count         | Strong              | Overselling is costly               |
| User profile updates    | Eventual            | Brief staleness is acceptable       |

#### 5. Durability

- **Question:** Can data EVER be lost?
- **Metrics:** RPO (Recovery Point Objective --- max acceptable data loss in time)
- **Design Impact:** Backup strategy, replication, write-ahead logging

| Durability Level | RPO               | Technique                                |
|------------------|-------------------|------------------------------------------|
| Best-effort      | Hours of data loss| Daily backups                             |
| Standard         | Minutes           | Synchronous replication + WAL             |
| High             | Zero data loss    | Multi-region synchronous replication      |

#### 6. Security

- **Question:** What are the security and compliance requirements?
- **Concerns:** Authentication, authorization, encryption, PII handling, compliance
- **Design Impact:** Auth service, encryption layers, audit logging, data residency

---

## 3.4 Constraints

Constraints are the **boundaries** within which your design must operate. Unlike requirements
(which describe what you want), constraints describe what you **cannot change**.

### Types of Constraints

| Constraint Type     | Examples                                                        |
|---------------------|-----------------------------------------------------------------|
| **Technology**      | Must use AWS (company policy); must use Java (team skill set)   |
| **Budget**          | Infrastructure budget capped at $10K/month                      |
| **Timeline**        | Must launch MVP in 3 months                                     |
| **Team**            | Team of 5 engineers; no dedicated SRE                           |
| **Regulatory**      | Must comply with GDPR; data must stay in EU                     |
| **Legacy**          | Must integrate with existing Oracle database                    |
| **Organizational**  | Must follow company's microservices guidelines                  |

### How Constraints Shape Design

```
  Constraint: "Team of 3 junior engineers, 3-month deadline"
  
  Implication:
  +---------------------------+       +---------------------------+
  | DON'T design:             |       | DO design:                |
  |                           |       |                           |
  | - Complex microservices   |       | - Simple monolith         |
  | - Custom distributed DB   |       | - Managed DB (RDS)        |
  | - Kubernetes from scratch |       | - Simple deployment (ECS) |
  | - Event sourcing + CQRS   |       | - Standard REST APIs      |
  +---------------------------+       +---------------------------+
```

> **Key Insight:** The "best" architecture is not always the most sophisticated one.
> It is the one that best fits the constraints while meeting the requirements.

---

## 3.5 Capacity Estimation

Capacity estimation translates requirements into **numbers** that drive design decisions.
(We dedicate the entire next chapter to detailed estimation techniques.)

### The Core Numbers to Estimate

```
  +-------------------+
  |   Total Users     |  e.g., 100M registered users
  +---------+---------+
            |
            v
  +-------------------+
  | Daily Active Users|  e.g., 10M DAU (10% of total)
  | (DAU)             |
  +---------+---------+
            |
            v
  +-------------------+
  | Queries Per Second|  e.g., 10M users / 86,400 sec ~ 115 QPS (average)
  | (QPS)             |       Peak: 115 * 3 ~ 350 QPS
  +---------+---------+
            |
            v
  +---+-----+-----+---+
  |   |           |   |
  v   v           v   v
Storage  Bandwidth  Memory  Servers
```

### Quick Estimation Template

| Metric              | Formula                                         | Example              |
|---------------------|-------------------------------------------------|----------------------|
| **DAU**             | Total users x active ratio                      | 100M x 10% = 10M    |
| **Average QPS**     | DAU x actions/day / 86,400                      | 10M x 10 / 86400    |
| **Peak QPS**        | Average QPS x peak multiplier (2-5x)            | 1,157 x 3 = 3,472   |
| **Storage/day**     | Writes/day x avg record size                    | 1M x 1 KB = 1 GB    |
| **Storage/year**    | Storage/day x 365                               | 1 GB x 365 = 365 GB |
| **Bandwidth**       | QPS x avg response size                         | 3,472 x 10 KB = 34 MB/s|
| **Cache memory**    | Daily reads x avg size x 20% (80/20 rule)       | Varies               |

---

## 3.6 SLA, SLO, and SLI

These three terms are often confused. Understanding their hierarchy is essential.

```
  +-------------------------------------------------------------------+
  |                                                                   |
  |  SLI (Service Level INDICATOR)                                    |
  |  = The METRIC you measure                                        |
  |  Example: "Request latency (p99)"                                |
  |                                                                   |
  |       |                                                           |
  |       v                                                           |
  |                                                                   |
  |  SLO (Service Level OBJECTIVE)                                    |
  |  = The TARGET you set for the SLI                                |
  |  Example: "p99 latency < 200ms"                                  |
  |                                                                   |
  |       |                                                           |
  |       v                                                           |
  |                                                                   |
  |  SLA (Service Level AGREEMENT)                                    |
  |  = The CONTRACT with consequences if SLO is not met              |
  |  Example: "If p99 > 200ms for > 0.1% of the month,              |
  |            customer receives 10% service credit"                 |
  |                                                                   |
  +-------------------------------------------------------------------+
```

### Detailed Definitions

| Term | Full Name                  | What It Is                                           | Who Defines It     |
|------|----------------------------|------------------------------------------------------|--------------------|
| SLI  | Service Level Indicator    | A quantitative measure of service behavior           | Engineering team   |
| SLO  | Service Level Objective    | A target value or range for an SLI                   | Engineering + PM   |
| SLA  | Service Level Agreement    | A formal agreement with business consequences         | Business + Legal   |

### Common SLIs and Their SLOs

| SLI                        | Typical SLO                    | Measurement Method                |
|----------------------------|--------------------------------|-----------------------------------|
| Availability               | 99.9% of requests succeed      | Success rate over rolling window  |
| Request latency (p50)      | < 50ms                         | Server-side instrumentation       |
| Request latency (p99)      | < 200ms                        | Server-side instrumentation       |
| Error rate                 | < 0.1% of requests             | HTTP 5xx / total requests         |
| Throughput                 | > 10,000 RPS                   | Load balancer metrics             |
| Data freshness             | < 5 seconds behind primary     | Replication lag monitoring        |

### How SLAs Tie to NFRs

Your Non-Functional Requirements should be expressed as SLOs:

```
  NFR: "The system should be highly available"
       (Vague --- how do you measure this?)

  Better: SLO: "99.9% of requests return a non-error response within 500ms"
          SLI: Percentage of requests with status 2xx and latency < 500ms
          SLA: "If monthly availability drops below 99.9%, affected
                customers receive a 10% credit on their next invoice"
```

### Error Budgets

An **error budget** is the complement of your SLO. If your SLO is 99.9% availability,
your error budget is 0.1% --- meaning you can afford 0.1% of requests to fail per month.

```
  Monthly requests: 10,000,000
  SLO: 99.9%
  Error budget: 10,000,000 x 0.1% = 10,000 failed requests allowed
  
  If you've used 8,000 of your error budget:
  +================================================================+
  |################                              | 8,000 / 10,000  |
  +================================================================+
  
  Remaining budget: 2,000 failures --- proceed with caution!
```

> **Practice:** Teams with remaining error budget can deploy risky changes. Teams
> that have exhausted their error budget should focus on reliability improvements.

---

## 3.7 Asking the Right Clarifying Questions

In both real-world design and interviews, you must **ask questions** before designing.
Here is a comprehensive checklist organized by category.

### Functional Questions

```
[ ] Who are the users? (end users, internal teams, other services)
[ ] What are the core use cases? (list the top 3-5)
[ ] What actions can users perform? (CRUD operations)
[ ] Are there different user roles? (admin, regular, guest)
[ ] What are the input/output formats? (text, images, video)
[ ] Are there any real-time requirements? (chat, notifications)
[ ] What existing systems must we integrate with?
[ ] What is explicitly OUT of scope?
```

### Scale Questions

```
[ ] How many total users are expected?
[ ] What is the expected DAU?
[ ] What is the read-to-write ratio?
[ ] How much data will be stored? For how long?
[ ] Are there seasonal traffic patterns? (Black Friday, etc.)
[ ] What is the expected growth rate? (10x in 2 years?)
```

### Performance Questions

```
[ ] What is the acceptable latency for key operations?
[ ] What throughput must the system sustain?
[ ] Are there batch processing requirements?
[ ] What is the target availability?
```

### Data Questions

```
[ ] Is strong consistency required, or is eventual consistency acceptable?
[ ] What is the acceptable data loss window? (RPO)
[ ] How long must data be retained?
[ ] Are there data residency requirements? (GDPR, regional laws)
[ ] What is the data access pattern? (random vs sequential, point vs range)
```

### Constraint Questions

```
[ ] Is there a preferred technology stack?
[ ] What is the team size and skill set?
[ ] What is the budget for infrastructure?
[ ] What is the timeline?
[ ] Are there regulatory/compliance requirements?
```

---

## 3.8 Example: Requirements for a Social Media Platform

Let us walk through a complete requirements gathering exercise for a Twitter-like social
media platform.

### Functional Requirements

| ID    | Requirement                                        | Priority   |
|-------|----------------------------------------------------|------------|
| FR-1  | Users can create an account and log in              | Must Have  |
| FR-2  | Users can create posts (280 characters + 1 image)   | Must Have  |
| FR-3  | Users can follow/unfollow other users               | Must Have  |
| FR-4  | Users see a timeline of posts from followed users   | Must Have  |
| FR-5  | Users can like and reply to posts                   | Must Have  |
| FR-6  | Users can search for users and posts                | Should Have|
| FR-7  | Users receive notifications for likes/replies       | Should Have|
| FR-8  | Users can send direct messages                      | Could Have |
| FR-9  | Users can create polls in posts                     | Could Have |
| FR-10 | Users can share posts (retweet)                     | Should Have|

### Non-Functional Requirements

| NFR                 | Specification                                            |
|---------------------|----------------------------------------------------------|
| **Availability**    | 99.95% (< 4.38 hours downtime/year)                     |
| **Latency**         | Timeline load: < 200ms p99; Post creation: < 500ms p99  |
| **Scalability**     | 100M total users, 25M DAU, 500M posts/day               |
| **Consistency**     | Timeline: eventual (< 5s delay); Follows: strong         |
| **Durability**      | Zero data loss for posts; RPO = 0                        |
| **Security**        | OAuth 2.0, encrypted at rest and in transit, rate-limited|

### Constraints

| Constraint          | Detail                                                    |
|---------------------|-----------------------------------------------------------|
| **Cloud Provider**  | AWS (company standard)                                    |
| **Team Size**       | 15 engineers across 3 squads                              |
| **Timeline**        | MVP in 6 months                                           |
| **Budget**          | $50K/month infrastructure                                 |
| **Compliance**      | GDPR (EU users), CCPA (California users)                  |

### Capacity Estimation (Quick)

```
  Total Users:     100M
  DAU:             25M (25%)
  
  Posts:
    Posts/day:     500M
    Post QPS:      500M / 86,400 ~ 5,787 QPS (avg)
    Peak QPS:      5,787 x 3 ~ 17,360 QPS
  
  Timeline Reads:
    Avg reads/user: 20 timeline loads/day
    Read QPS:       25M x 20 / 86,400 ~ 5,787 QPS
    Peak Read QPS:  5,787 x 3 ~ 17,360 QPS
  
  Storage:
    Avg post size:  280 bytes text + 500 bytes metadata = ~800 bytes
    Posts/day:      500M x 800 B = 400 GB/day
    Posts/year:     400 GB x 365 = ~146 TB/year
    + Images:       If 20% of posts have images (avg 200 KB each):
                    500M x 0.2 x 200 KB = 20 TB/day
  
  Bandwidth:
    Outgoing:       17,360 QPS x 5 KB avg response = ~85 MB/s peak
```

### Derived Design Decisions

Based on these requirements and estimates, we can already make some architectural decisions:

| Requirement/Constraint          | Derived Design Decision                              |
|---------------------------------|------------------------------------------------------|
| 500M posts/day                  | Need a write-optimized datastore (Cassandra / NoSQL) |
| Timeline < 200ms               | Pre-compute timelines (fan-out on write) + cache     |
| 99.95% availability             | Multi-AZ deployment, redundancy at every layer       |
| 20 TB/day images               | Object storage (S3) + CDN for serving                |
| 100M users + search            | Elasticsearch for full-text search                   |
| Real-time notifications        | WebSocket or SSE for push notifications              |
| GDPR compliance                | Data residency in EU, right-to-be-forgotten support  |
| 15 engineers, 3 squads         | 3-5 core services (not 20 microservices)             |

---

## 3.9 Requirements Documentation Template

Use this template when documenting requirements for your HLD:

```
+-------------------------------------------------------------------+
|                  REQUIREMENTS DOCUMENT                            |
+-------------------------------------------------------------------+
|                                                                   |
|  PROJECT: [Name]                                                  |
|  DATE: [Date]                                                     |
|  AUTHOR: [Name]                                                   |
|  STATUS: [Draft / In Review / Approved]                           |
|                                                                   |
|  1. OVERVIEW                                                      |
|     [2-3 sentence description of the system]                      |
|                                                                   |
|  2. FUNCTIONAL REQUIREMENTS                                       |
|     [Table with ID, Description, Priority]                        |
|                                                                   |
|  3. NON-FUNCTIONAL REQUIREMENTS                                   |
|     - Availability: [target]                                      |
|     - Latency: [p50, p95, p99 targets]                           |
|     - Scalability: [DAU, QPS, storage]                            |
|     - Consistency: [model and rationale]                          |
|     - Durability: [RPO/RTO]                                       |
|     - Security: [auth, encryption, compliance]                   |
|                                                                   |
|  4. CONSTRAINTS                                                   |
|     [Table of constraints and their impact]                       |
|                                                                   |
|  5. CAPACITY ESTIMATION                                           |
|     [Traffic, storage, bandwidth calculations]                    |
|                                                                   |
|  6. ASSUMPTIONS                                                   |
|     [Explicit list of things assumed to be true]                  |
|                                                                   |
|  7. OUT OF SCOPE                                                  |
|     [Explicit list of what will NOT be built]                     |
|                                                                   |
|  8. OPEN QUESTIONS                                                |
|     [Questions that still need answers]                           |
|                                                                   |
+-------------------------------------------------------------------+
```

---

## 3.10 Common Mistakes in Requirements Gathering

### Mistake 1: Assuming Instead of Asking

> **Bad:** "Obviously we need to support 1 billion users."
> **Good:** "What is the expected user base in Year 1? Year 3?"

### Mistake 2: Ignoring Non-Functional Requirements

Many engineers focus only on features and forget about latency, availability, and
security. NFRs should be discussed alongside FRs from the very beginning.

### Mistake 3: Not Prioritizing Requirements

Without prioritization, teams try to build everything at once and deliver nothing.
Use MoSCoW or a simple P0/P1/P2 system:

| Priority | Meaning                  | Action                              |
|----------|--------------------------|-------------------------------------|
| P0       | Critical, blocks launch  | Must be in MVP                      |
| P1       | Important, high impact   | Should be in MVP if time permits    |
| P2       | Nice to have             | Plan for post-launch                |

### Mistake 4: Gold-Plating Requirements

Adding unnecessary complexity to requirements:

> **Gold-plated:** "The search system must support fuzzy matching, faceted search,
> geospatial queries, and natural language processing."
>
> **Right-sized:** "The search system must support keyword search on product names
> and descriptions, with result ranking by relevance."

### Mistake 5: Not Documenting Assumptions

Every requirement has implicit assumptions. Make them explicit:

```
  Assumption: Peak traffic occurs between 6 PM - 10 PM local time
  Assumption: Average post size does not exceed 1 KB of text
  Assumption: Images are resized client-side before upload
  Assumption: 80% of traffic is reads, 20% is writes
```

If an assumption turns out to be wrong, you know exactly which parts of the design
need to be revisited.

---

## 3.11 Key Takeaways

```
+-------------------------------------------------------------------+
|                        KEY TAKEAWAYS                              |
+-------------------------------------------------------------------+
|                                                                   |
|  1. Requirements gathering is the FIRST and MOST IMPORTANT step  |
|     in system design. Never skip it.                              |
|                                                                   |
|  2. Separate requirements into Functional (what it does),         |
|     Non-Functional (how well it does it), and Constraints         |
|     (boundaries you cannot change).                               |
|                                                                   |
|  3. Prioritize requirements using MoSCoW or P0/P1/P2.            |
|     You cannot build everything at once.                          |
|                                                                   |
|  4. NFRs drive architecture more than FRs. Scalability,           |
|     availability, latency, and consistency requirements           |
|     determine your technology choices.                            |
|                                                                   |
|  5. Capacity estimation translates requirements into numbers      |
|     (QPS, storage, bandwidth) that guide sizing decisions.        |
|                                                                   |
|  6. Understand the SLA > SLO > SLI hierarchy. Express NFRs       |
|     as measurable SLOs with specific SLIs.                        |
|                                                                   |
|  7. Always ask clarifying questions before designing.             |
|     Use the checklist to ensure you cover functional, scale,      |
|     performance, data, and constraint dimensions.                 |
|                                                                   |
|  8. Document your assumptions explicitly. When assumptions        |
|     change, you know which design decisions to revisit.           |
|                                                                   |
|  9. Error budgets connect SLOs to engineering decisions:          |
|     budget remaining = safe to deploy; budget exhausted =         |
|     focus on reliability.                                         |
|                                                                   |
| 10. Requirements are a living artifact. Revisit them as you       |
|     learn more during design and implementation.                  |
|                                                                   |
+-------------------------------------------------------------------+
```

---

**Next Chapter:** [Chapter 4: Back-of-the-Envelope Estimation](./04-estimation.md) --- where we
master the art of quickly estimating traffic, storage, bandwidth, and compute requirements
to ground our architectural decisions in reality.
