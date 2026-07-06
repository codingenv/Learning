# Chapter 1: Introduction to High Level Design

---

## 1.1 What is High Level Design?

**High Level Design (HLD)** is the process of defining the overall architecture and structure
of a software system without diving into the implementation details of individual components.
It is the blueprint that describes *what* the system will look like at a macro level --- which
components exist, how they interact, what technologies they use, and how data flows through
the system.

> **Definition:** High Level Design is an architectural document that translates business
> requirements into a system architecture composed of components, their interactions,
> data flows, technology choices, and non-functional guarantees.

### Purpose of HLD

HLD serves several critical purposes in software engineering:

1. **Communication** --- It provides a shared understanding among engineers, product managers,
   and stakeholders about what will be built.
2. **Decision Documentation** --- It captures *why* certain architectural decisions were made,
   including the trade-offs considered.
3. **Risk Identification** --- By thinking through the architecture early, teams can identify
   scalability bottlenecks, single points of failure, and security gaps before writing code.
4. **Alignment** --- It ensures that multiple teams working on different parts of the system
   are building toward a coherent whole.
5. **Estimation** --- It enables more accurate effort and cost estimation for the project.

### Audience of HLD

An HLD document is consumed by a wide range of stakeholders:

| Stakeholder            | What They Care About                                      |
|------------------------|-----------------------------------------------------------|
| Engineering Managers   | Feasibility, timeline, team allocation                    |
| Backend Engineers      | APIs, data models, service boundaries                     |
| Frontend Engineers     | API contracts, latency expectations                       |
| DevOps / SRE           | Deployment architecture, monitoring, scaling strategy     |
| Security Engineers     | Authentication, authorization, data encryption            |
| Product Managers       | Feature coverage, constraints, trade-offs                 |
| QA Engineers           | Testability, integration points, failure modes            |
| Executive Leadership   | Cost, timeline, business risk                             |

---

## 1.2 HLD vs Low Level Design (LLD)

One of the most common points of confusion is the distinction between High Level Design
and Low Level Design. They are complementary, not competing --- HLD comes first and sets
the stage for LLD.

```
+------------------------------------------------------+
|                  SOFTWARE DESIGN                     |
|                                                      |
|   +---------------------+  +---------------------+  |
|   |  HIGH LEVEL DESIGN  |  |  LOW LEVEL DESIGN   |  |
|   |  (The "What")       |  |  (The "How")        |  |
|   |                     |  |                     |  |
|   |  - Components       |  |  - Class diagrams   |  |
|   |  - Data flow        |  |  - Method signatures|  |
|   |  - API contracts    |  |  - Algorithms       |  |
|   |  - Tech choices     |  |  - Data structures  |  |
|   |  - NFRs             |  |  - Error handling   |  |
|   +---------------------+  +---------------------+  |
|                                                      |
+------------------------------------------------------+
```

### Detailed Comparison Table

| Aspect                  | High Level Design (HLD)              | Low Level Design (LLD)               |
|-------------------------|--------------------------------------|--------------------------------------|
| **Scope**               | Entire system or major subsystem     | Individual component or module       |
| **Abstraction Level**   | High --- boxes and arrows            | Low --- classes, functions, schemas   |
| **Audience**            | All stakeholders                     | Primarily developers                 |
| **Focus**               | *What* components exist and *why*    | *How* each component is implemented  |
| **Diagrams**            | System context, component, data flow | Class diagrams, sequence diagrams    |
| **Technology**          | Broad choices (e.g., "use Kafka")    | Specific configs (e.g., topic setup) |
| **Data Model**          | Entity-level (User, Order, Product)  | Table schemas, indexes, constraints  |
| **APIs**                | Endpoint listing with contracts      | Request/response schemas, validation |
| **Non-Functional Reqs** | Defined and addressed                | Implemented and tested               |
| **When Created**        | Before development begins            | During or just before development    |
| **Typical Length**       | 5-20 pages                           | 10-50+ pages per component           |

### A Simple Analogy

Think of building a house:

- **HLD** is the *architectural blueprint* --- it shows the number of rooms, their layout,
  where doors and windows go, the plumbing routes, and the electrical plan.
- **LLD** is the *construction plan* --- it specifies the exact type of brick, the wiring
  gauge, the pipe diameter, and the step-by-step assembly instructions.

You cannot start LLD without HLD, just as you cannot start construction without a blueprint.

---

## 1.3 Where HLD Fits in the SDLC

High Level Design is not an isolated activity. It fits into the broader Software Development
Lifecycle (SDLC) as a critical bridge between requirements and implementation.

```
+-------------+     +-----------+     +-----+     +-----+     +---------+     +------------+
| Requirements| --> |    HLD    | --> | LLD | --> |Build| --> | Testing | --> | Deployment |
|  Gathering  |     | (Arch.    |     |     |     |     |     |         |     |            |
|             |     |  Design)  |     |     |     |     |     |         |     |            |
+-------------+     +-----------+     +-----+     +-----+     +---------+     +------------+
      |                  |               |           |              |               |
      v                  v               v           v              v               v
  "What do we      "What does the   "How does    "Write the    "Does it       "Ship it to
   need to build?"  system look      each part    code"         work?"         production"
                     like?"          work?"
```

### Iterative Nature

In modern agile environments, HLD is not a one-time waterfall artifact. It is a **living
document** that evolves:

1. **Initial HLD** --- Created during sprint 0 or a design phase, covering the big picture.
2. **Refinement** --- As the team learns more during development, the HLD is updated.
3. **Versioning** --- Major architectural changes result in a new version of the HLD.

> **Best Practice:** Store your HLD in version control (e.g., Git) alongside the code.
> This ensures that architectural documentation evolves with the codebase.

---

## 1.4 Key Stakeholders Who Consume HLD Documents

Let us look deeper at each stakeholder group and what they extract from an HLD:

### Engineering Teams

- **Backend teams** focus on service boundaries, API contracts, database choices, and
  message queue interactions.
- **Frontend teams** focus on API endpoints, response formats, latency expectations,
  and authentication flows.
- **Mobile teams** care about API versioning, payload sizes (for bandwidth), and offline
  behavior.

### Operations and SRE

- Deployment topology (how many instances, which regions)
- Monitoring and alerting strategy
- Disaster recovery and failover mechanisms
- Scaling triggers (when to auto-scale, based on what metrics)

### Security

- Authentication and authorization mechanisms
- Data encryption (at rest, in transit)
- PII handling and compliance (GDPR, HIPAA)
- Rate limiting and DDoS mitigation

### Product and Business

- Feature coverage vs. what was requested
- Trade-offs made and their business impact
- Timeline implications of architectural choices

---

## 1.5 Characteristics of a Good HLD

Not all HLD documents are created equal. A **good** HLD has the following characteristics:

### 1. Clarity

- Uses simple, precise language
- Avoids unnecessary jargon
- Includes diagrams that a new team member can understand in 5 minutes
- Defines acronyms and domain-specific terms

### 2. Scalability Focus

- Addresses how the system handles 10x or 100x the current load
- Identifies bottlenecks and proposes mitigation strategies
- Considers both read-heavy and write-heavy scenarios

### 3. Trade-off Awareness

- Explicitly states what was chosen and what was rejected
- Documents the *reasoning* behind each trade-off
- Example: "We chose eventual consistency over strong consistency because the business
  can tolerate a 5-second delay in data propagation, and this choice improves write
  throughput by 10x."

### 4. Completeness Without Over-Engineering

- Covers all major components but does not prescribe implementation details
- Addresses the "happy path" and the "failure path"
- Includes non-functional requirements (latency, availability, etc.)

### 5. Modularity

- Components are described independently with clear interfaces
- Changes to one component should not require rewriting the entire HLD

### Checklist for a Good HLD

```
[ ] System context diagram included
[ ] All major components identified and described
[ ] Data flow between components is clear
[ ] API contracts (at least endpoint level) defined
[ ] Database / storage choices justified
[ ] Non-functional requirements addressed
[ ] Trade-offs documented with reasoning
[ ] Failure modes and mitigation strategies included
[ ] Capacity estimation provided
[ ] Security considerations addressed
[ ] Deployment and scaling strategy outlined
```

---

## 1.6 The Anatomy of an HLD Document

A well-structured HLD document typically follows this outline:

```
+-----------------------------------------------------------+
|                   HLD DOCUMENT STRUCTURE                  |
+-----------------------------------------------------------+
|                                                           |
|  1. OVERVIEW                                              |
|     - Problem statement                                   |
|     - Goals and non-goals                                 |
|     - Scope                                               |
|                                                           |
|  2. REQUIREMENTS                                          |
|     - Functional requirements                             |
|     - Non-functional requirements                         |
|     - Constraints                                         |
|                                                           |
|  3. CAPACITY ESTIMATION                                   |
|     - Traffic, storage, bandwidth estimates               |
|                                                           |
|  4. SYSTEM ARCHITECTURE                                   |
|     - System context diagram                              |
|     - Component diagram                                   |
|     - Technology choices                                  |
|                                                           |
|  5. DATA MODEL                                            |
|     - Entities and relationships                          |
|     - Database choice and justification                   |
|                                                           |
|  6. API DESIGN                                            |
|     - Endpoint listing                                    |
|     - Request/response overview                           |
|                                                           |
|  7. DATA FLOW                                             |
|     - How data moves through the system                   |
|     - Read path vs write path                             |
|                                                           |
|  8. NON-FUNCTIONAL DEEP DIVE                              |
|     - Scalability strategy                                |
|     - Availability and fault tolerance                    |
|     - Security                                            |
|     - Monitoring and alerting                             |
|                                                           |
|  9. TRADE-OFFS AND ALTERNATIVES                           |
|     - What was considered and rejected                    |
|     - Justification for choices                           |
|                                                           |
| 10. OPEN QUESTIONS                                        |
|     - Unresolved decisions                                |
|     - Items needing further investigation                 |
|                                                           |
+-----------------------------------------------------------+
```

### Section Deep Dive

#### 1. Overview

This is the "elevator pitch" for the system. In 2-3 paragraphs, explain:
- What problem are we solving?
- Who are the users?
- What are the explicit *non-goals* (things we will NOT build)?

#### 2. Requirements

Split into Functional Requirements (what the system does) and Non-Functional Requirements
(how well it does it). We dedicate an entire chapter to this topic later.

#### 3. Capacity Estimation

Back-of-the-envelope calculations for traffic, storage, and bandwidth. This grounds
the design in reality. We also dedicate a full chapter to estimation techniques.

#### 4-7. Core Architecture Sections

These form the heart of the HLD --- the diagrams, data models, APIs, and data flow.

#### 8. Non-Functional Deep Dive

This is where you address scalability, availability, security, and observability in detail.

#### 9. Trade-offs

Perhaps the most important section. This is where you demonstrate architectural maturity
by showing that you considered alternatives and made deliberate choices.

---

## 1.7 Common Mistakes in HLD

### Mistake 1: Jumping to Technology Before Understanding Requirements

> "Let's use Kubernetes and Kafka!" --- before knowing if the system has 100 or 100 million users.

Always start with requirements and constraints. Technology choices should be *derived* from
requirements, not the other way around.

### Mistake 2: Ignoring Non-Functional Requirements

A system that "works" but cannot handle the expected load, or goes down every week, is
a failed system. NFRs are not optional --- they are first-class requirements.

### Mistake 3: The "One Big Box" Diagram

```
  BAD:                              GOOD:
  +-------------------+             +---------+     +---------+
  |                   |             |  API    |---->|  Auth   |
  |   "The System"    |             | Gateway |     | Service |
  |                   |             +---------+     +---------+
  +-------------------+                  |
                                         v
  (This tells us nothing)           +---------+     +---------+
                                    |  Order  |---->|  DB     |
                                    | Service |     | (MySQL) |
                                    +---------+     +---------+
                                    
                                    (This tells a story)
```

### Mistake 4: No Trade-off Discussion

If your HLD presents only one option with no alternatives considered, reviewers will
question whether you explored the design space thoroughly.

### Mistake 5: Over-Engineering

Designing for 1 billion users when you expect 10,000 is wasteful. Design for the
*current scale* with a clear *path to growth*. Do not build a distributed system when
a single well-provisioned server suffices.

### Mistake 6: Treating HLD as a One-Time Document

HLD should be a living document. If the architecture changes significantly during
development, the HLD should be updated. Stale documentation is worse than no documentation
because it actively misleads.

---

## 1.8 Real-World Example: E-Commerce Platform HLD Overview

Let us walk through a simplified HLD for an e-commerce platform to illustrate the concepts.

### Problem Statement

Build an online marketplace where sellers can list products and buyers can browse, search,
add to cart, and purchase products with secure payment processing.

### Functional Requirements

- User registration and authentication
- Product catalog with search and filtering
- Shopping cart management
- Order placement and payment processing
- Order tracking and history

### Non-Functional Requirements

- **Availability:** 99.9% uptime (< 8.76 hours downtime/year)
- **Latency:** Product search < 200ms p99, Checkout < 500ms p99
- **Scale:** Support 1M DAU, 10K concurrent users
- **Consistency:** Strong consistency for orders and payments; eventual consistency for reviews

### System Architecture

```
                              +------------------+
                              |    CDN (Images,  |
                              |    Static Assets)|
                              +--------+---------+
                                       |
+----------+                 +---------v---------+
|  Mobile  |----+            |                   |
|  App     |    |            |   Load Balancer   |
+----------+    +----------->|   (e.g., Nginx)   |
                |            |                   |
+----------+   |            +---------+---------+
|  Web     |---+                      |
|  Browser |                +---------v---------+
+----------+                |   API Gateway     |
                            |  (Rate Limiting,  |
                            |   Auth, Routing)  |
                            +---------+---------+
                                      |
                 +--------------------+--------------------+
                 |                    |                    |
          +------v------+     +------v------+     +------v------+
          | User Service|     |Product Svc  |     |Order Service|
          | (Auth, Prof)|     |(Catalog,    |     |(Cart, Order,|
          |             |     | Search)     |     | Payment)    |
          +------+------+     +------+------+     +------+------+
                 |                   |                    |
          +------v------+     +------v------+     +------v------+
          |  Users DB   |     |Product DB   |     |  Orders DB  |
          |  (Postgres) |     |(Postgres +  |     |  (Postgres) |
          |             |     | Elastic     |     |             |
          +-------------+     | Search)     |     +------+------+
                              +-------------+            |
                                                  +------v------+
                                                  |  Payment    |
                                                  |  Gateway    |
                                                  | (Stripe/    |
                                                  |  PayPal)    |
                                                  +-------------+
```

### Key Trade-offs

| Decision                          | Choice Made         | Alternative Considered | Reasoning                                       |
|-----------------------------------|---------------------|------------------------|-------------------------------------------------|
| Database for products             | PostgreSQL + ES     | MongoDB                | Need full-text search; ES provides superior search|
| Communication between services    | REST (synchronous)  | gRPC, Message Queue    | Simpler for team's skill set; async for events   |
| Product image storage             | S3 + CDN            | Local file system      | Scalability and global distribution              |
| Session management                | JWT (stateless)     | Server-side sessions   | Horizontal scaling without sticky sessions       |
| Search engine                     | Elasticsearch       | Solr, Algolia          | Team expertise, open-source, strong community    |

### Data Flow: User Places an Order

```
  User        API Gateway    Order Svc    Product Svc    Payment GW     Orders DB
   |               |            |              |              |              |
   |-- POST /order>|            |              |              |              |
   |               |--validate->|              |              |              |
   |               |            |--check stock>|              |              |
   |               |            |<--stock ok---|              |              |
   |               |            |--process payment----------->|              |
   |               |            |<--payment confirmed---------|              |
   |               |            |--save order-------------------------------->|
   |               |            |<--order saved-------------------------------|
   |               |<--order id-|              |              |              |
   |<--201 Created-|            |              |              |              |
```

---

## 1.9 Key Takeaways

```
+-------------------------------------------------------------------+
|                        KEY TAKEAWAYS                              |
+-------------------------------------------------------------------+
|                                                                   |
|  1. HLD is the architectural blueprint of a system --- it answers |
|     "What does the system look like?" not "How is each part       |
|     implemented?"                                                 |
|                                                                   |
|  2. HLD and LLD are complementary. HLD comes first and provides  |
|     the framework within which LLD operates.                      |
|                                                                   |
|  3. A good HLD is clear, scalable, trade-off-aware, and          |
|     addresses both functional and non-functional requirements.    |
|                                                                   |
|  4. The anatomy of an HLD includes: overview, requirements,      |
|     estimation, architecture, data model, APIs, data flow,        |
|     NFR deep dive, and trade-offs.                                |
|                                                                   |
|  5. Common mistakes include: jumping to technology choices,       |
|     ignoring NFRs, vague diagrams, no trade-off discussion,      |
|     and over-engineering.                                         |
|                                                                   |
|  6. HLD is a living document that should evolve with the system. |
|     Store it in version control alongside the code.               |
|                                                                   |
|  7. Always start with requirements, then estimate, then design.  |
|     Never design in a vacuum.                                     |
|                                                                   |
+-------------------------------------------------------------------+
```

---

**Next Chapter:** [Chapter 2: Architectural Thinking](./02-architectural-thinking.md) --- where we
explore the mindset, principles, and trade-offs that guide architectural decisions.
