# Microservices Architecture & Design Patterns

### A Practical, In-Depth Guide -- From Fundamentals to Production

---

> **Who this book is for:** Experienced Java/backend engineers who understand basic system design (HTTP, databases, REST APIs, threads) but are new to microservices. You do not need prior microservices experience -- but you should be comfortable reading Java and Spring-style code.
>
> **What you will be able to do after reading:** Explain microservices deeply, apply the major design patterns, design a production-ready microservices system, and confidently migrate a monolith into microservices without repeating the industry's most expensive mistakes.
>
> **How this book is structured:** We start with foundations (Chapters 1-4), then dedicate a full chapter to each of the 10 most important patterns (Chapters 5-14), then a multi-chapter migration playbook (Chapters 15-18), advanced production topics (Chapters 19-23), and finally a decision framework with real case studies (Chapter 24).
>
> **A note on diagrams:** Every diagram is text-based (ASCII) so it renders anywhere. Read them slowly -- the arrows show the direction of calls or data flow.

---

## Table of Contents

**Part I -- Foundations**
1. Introduction to Microservices
2. Monolithic Architecture (the Foundation)
3. Core Principles of Microservices
4. Microservices Architecture Overview

**Part II -- Core Design Patterns**
5. API Gateway Pattern
6. Service Discovery Pattern
7. Circuit Breaker Pattern
8. Saga Pattern (Orchestration vs Choreography)
9. Database per Service Pattern
10. Event-Driven Architecture Pattern
11. CQRS (Command Query Responsibility Segregation)
12. Strangler Fig Pattern
13. Bulkhead Pattern
14. Retry Pattern

**Part III -- Monolith to Microservices Migration**
15. Migration Strategy: Identifying Boundaries
16. Data Separation and API Extraction
17. Migration Challenges and What to Keep in Check
18. Common Migration Mistakes

**Part IV -- Advanced Production Topics**
19. Observability (Logging, Metrics, Tracing)
20. Service Mesh
21. Security in Microservices (OAuth2, JWT)
22. Deployment Strategies (Blue-Green, Canary)
23. Scaling Strategies

**Part V -- Decision and Case Studies**
24. Bringing It All Together

---

# Part I -- Foundations

## Chapter 1: Introduction to Microservices

### 1.1 What Is Microservices Architecture?

**Microservices architecture** is a style of building a single application as a **suite of small, independently deployable services**, each running in its own process and communicating over the network (usually via HTTP/REST, gRPC, or asynchronous messaging). Each service is built around a specific **business capability**, owns its own data, and can be developed, deployed, and scaled independently of the others.

That is a dense definition, so let us unpack the key phrases, because every word was chosen deliberately:

- **Small services:** Each service is focused on doing one business thing well -- for example, "manage user accounts" or "process payments." "Small" refers to *scope of responsibility*, not lines of code.
- **Independently deployable:** This is the single most important property. You can deploy a change to the Payment service without rebuilding, retesting, or redeploying the Order service. This is what unlocks team autonomy and fast release cycles.
- **Own process / own data:** Each service runs separately and owns its own database. No other service is allowed to reach into its tables directly.
- **Communicate over the network:** Because services are separate processes (often on separate machines), they talk via network calls, not in-memory method calls. This is a profound shift with deep consequences (latency, partial failure) that we will return to repeatedly.
- **Organized around business capabilities:** Services map to *what the business does*, not to technical layers. There is no "database service" or "UI service"; there is an "Orders service," a "Catalog service," and so on.

**A concrete e-commerce example.** Imagine an online store. In a microservices design you might have:

```
  User Service        -> registration, profiles, authentication
  Catalog Service     -> product listings, search, inventory counts
  Cart Service        -> shopping cart contents
  Order Service       -> placing and tracking orders
  Payment Service     -> charging cards, refunds
  Shipping Service    -> arranging delivery
  Notification Service-> emails, SMS, push notifications
```

Each is a separate application, with its own codebase, its own database, its own deployment pipeline, and ideally its own small team.

### 1.2 Monolith vs SOA vs Microservices

To understand microservices, you must understand what came before. These three styles form a historical and conceptual progression.

**Monolithic architecture.** The entire application -- user interface logic, business logic, and data-access logic -- is built and deployed as a **single unit**. All modules run in the same process and typically share one database. This was (and often still is) the default way to build software.

**Service-Oriented Architecture (SOA).** An enterprise approach popular in the 2000s. It broke applications into services, but these services were often **large, coarse-grained**, communicated through a heavy central component called an **Enterprise Service Bus (ESB)**, and frequently still **shared databases**. SOA introduced the idea of services, but the centralized ESB became a bottleneck and a single point of coupling.

**Microservices.** A refinement of the service idea, characterized by **fine-grained services, smart endpoints and dumb pipes** (logic lives in services, not in a heavy bus), **decentralized data** (a database per service), and a strong emphasis on **independent deployability**.

```
COMPARISON AT A GLANCE

  +------------------+------------------+-------------------+-------------------+
  | Aspect           | Monolith         | SOA               | Microservices     |
  +------------------+------------------+-------------------+-------------------+
  | Granularity      | One big unit     | Coarse services   | Fine-grained      |
  | Communication    | In-process calls | Heavy ESB         | Lightweight       |
  |                  |                  | (central bus)     | (REST/gRPC/msg)   |
  | Data             | One shared DB    | Often shared DBs  | DB per service    |
  | Deployment       | All at once      | Partly indep.     | Fully independent |
  | Coupling         | Tight            | Medium (via ESB)  | Loose             |
  | Team ownership   | Shared codebase  | Mixed             | Per-service teams |
  +------------------+------------------+-------------------+-------------------+
```

**The key distinction between SOA and microservices** is often summarized as: *"Microservices is SOA done right, with decentralized data and dumb pipes."* SOA centralized logic in the bus; microservices push logic to the edges (the services themselves) and keep the communication layer simple.

### 1.3 Why Microservices Became Popular

Microservices did not become popular by accident or fashion. They solved real, painful problems that large organizations hit as their monoliths and teams grew. The main drivers:

**1. Independent deployability and speed.** In a large monolith, every change -- no matter how small -- requires rebuilding and redeploying the *entire* application. Teams must coordinate releases, leading to slow, risky, "big bang" deployments. Microservices let each team ship its service on its own schedule, many times a day.

**2. Team autonomy and scale of organization.** This connects to **Conway's Law**: organizations design systems that mirror their communication structure. A 500-engineer company cannot all work effectively in one codebase. Splitting into services lets small teams (the famous Amazon "two-pizza teams") own a service end to end with minimal cross-team coordination.

**3. Independent and targeted scalability.** In a monolith, if only the search feature is under heavy load, you must scale the *entire* application to handle it -- wasting resources. With microservices, you scale only the Search service. This granular scaling saves significant cost and is far more responsive.

**4. Technology flexibility (polyglot).** Each service can use the best tool for its job. The Payment service might be Java/Spring for its mature ecosystem; a real-time recommendation service might be Go or Python. You are not locked into one stack for the whole system.

**5. Fault isolation.** If one service crashes or degrades, well-designed microservices can contain the failure so it does not take down the whole system. (This requires patterns like Circuit Breaker and Bulkhead, covered later -- it is not automatic.)

**6. The enabling technology arrived.** Microservices became *practical* only when supporting technology matured: cloud computing (cheap, elastic infrastructure), containers (Docker) for consistent packaging, orchestration (Kubernetes) for running many services, and DevOps/CI-CD culture for automated deployment. Without these, the operational cost of running dozens of services would be prohibitive.

**An honest caveat up front.** Microservices are not free. They trade the *simplicity* of a monolith for *operational complexity*: distributed systems are genuinely hard. You inherit network latency, partial failures, eventual consistency, distributed debugging, and a much heavier infrastructure burden. A recurring theme of this book is: **adopt microservices to solve scaling and organizational problems you actually have -- not because they are trendy.** Chapter 24 gives you a concrete decision checklist.

### 1.4 Key Characteristics of Microservices

Let us crystallize the defining characteristics. A system is genuinely "microservices" when it exhibits most of these:

**1. Independent deployability.** Each service can be deployed without coordinating with others. (The litmus test of a real microservices architecture.)

**2. Organized around business capabilities.** Services map to business domains (Orders, Payments), not technical layers (controllers, repositories).

**3. Decentralized data management.** Each service owns its data and its database. No shared database, no reaching into another service's tables.

**4. Decentralized governance.** Teams choose their own tools, languages, and release cadence within reason. No central architecture board dictating every detail.

**5. Loose coupling, high cohesion.** Services know as little as possible about each other (loose coupling), while everything inside a service is tightly related to one capability (high cohesion).

**6. Smart endpoints, dumb pipes.** Business logic lives in the services; the communication layer (message broker, HTTP) is kept simple and free of logic.

**7. Designed for failure.** The network *will* fail, services *will* go down. Microservices are built with resilience patterns (timeouts, retries, circuit breakers) as first-class concerns.

**8. Automation and observability.** Because there are many moving parts, automated deployment (CI/CD) and strong observability (logging, metrics, tracing) are not optional -- they are prerequisites.

### Chapter 1 Summary

- **Microservices** structure an application as small, independently deployable services, each owning a business capability and its own data, communicating over the network.
- The progression **Monolith -> SOA -> Microservices** reflects a move toward finer granularity, decentralized data, and lightweight communication.
- Microservices became popular to solve real problems: slow deployments, team-scaling limits, inefficient scaling, and technology lock-in -- enabled by cloud, containers, and DevOps.
- Defining traits: independent deployability, business-capability alignment, decentralized data and governance, loose coupling, design-for-failure, and heavy automation/observability.
- **They are not free:** you trade monolith simplicity for distributed-systems complexity. Use them deliberately.

---

## Chapter 2: Monolithic Architecture (the Foundation)

Before you can appreciate microservices, you must deeply understand the monolith -- not as a villain, but as a legitimate and often *correct* architecture. Many successful companies run on monoliths, and many microservices disasters began as premature escapes from perfectly good monoliths.

### 2.1 What Is a Monolith?

A **monolithic application** is built and deployed as a **single, unified unit**. All of the application's functionality -- the web/UI layer, the business logic, and the data-access layer -- lives in one codebase, compiles into one deployable artifact (for Java, typically a single WAR or JAR), runs in one process, and usually talks to one shared database.

```
TYPICAL MONOLITH STRUCTURE

         +-----------------------------------------+
         |            Monolithic Application        |
         |  +-----------------------------------+  |
         |  |  Presentation Layer (Controllers) |  |
         |  +-----------------------------------+  |
         |  |  Business Logic Layer (Services)  |  |
         |  |   Users | Orders | Payments | ... |  |
         |  +-----------------------------------+  |
         |  |  Data Access Layer (Repositories) |  |
         |  +-----------------------------------+  |
         +-------------------|---------------------+
                             |
                    +-----------------+
                    |  Single Shared  |
                    |    Database     |
                    +-----------------+
```

Crucially, the internal modules (Users, Orders, Payments) call each other through ordinary **in-process method calls** -- fast, reliable, transactional. This simplicity is the monolith's great strength.

A monolith is **not** automatically "bad" or "messy." A *well-structured* monolith -- often called a **modular monolith** -- has clean internal module boundaries, and is frequently the smartest choice for a new product.

### 2.2 Advantages of a Monolith

**1. Simplicity of development.** One codebase, one IDE project, one build. A new developer can clone the repo and run the whole system locally in minutes. There is no service discovery, no network plumbing, no distributed configuration.

**2. Simple, reliable communication.** Modules talk via direct method calls. There is no network latency, no serialization, and no partial-failure handling needed between modules.

**3. Strong, easy transactions.** Because everything shares one database, you get **ACID transactions** for free. Placing an order and decrementing inventory can happen in a single database transaction that either fully succeeds or fully rolls back. (In microservices, this becomes the painful Saga problem -- see Chapter 8.)

**4. Easy testing.** End-to-end testing is straightforward: spin up one application and one database. No need to coordinate many services and their dependencies.

**5. Simple deployment (at small scale).** One artifact to deploy. No orchestration, no service mesh, minimal infrastructure.

**6. Easier debugging.** A single process means a single log stream and a single stack trace. You can step through a request end to end in a debugger.

### 2.3 Disadvantages of a Monolith

The monolith's troubles appear as the application and team **grow large**:

**1. Scaling is all-or-nothing.** You cannot scale just the hot part. If checkout is overloaded, you must run more copies of the *entire* application, wasting memory and CPU on everything else.

**2. Slow, risky deployments.** Any change requires redeploying the whole app. A one-line fix means a full build, full regression test, and a full redeploy. Release cadence slows to weekly or monthly, and each release is risky because so much changes at once.

**3. Tight coupling and eroding boundaries.** Over years, modules tend to reach into each other. Without strict discipline, the codebase becomes a "big ball of mud" where everything depends on everything, and a change in one area unexpectedly breaks another.

**4. Technology lock-in.** The entire app is stuck on one language/framework version. Upgrading the framework is a massive, all-at-once project. You cannot adopt a better tool for one feature.

**5. Limited team scalability.** Many developers in one codebase causes merge conflicts, coordination overhead, and fear of changing shared code. Productivity per engineer drops as the team grows.

**6. A single bug can sink the ship.** A memory leak or an unbounded loop in one minor feature can crash the entire process, taking down every feature at once.

### 2.4 Real-World Examples

- **Early-stage startups:** Almost every successful company started as a monolith (including Amazon, Netflix, and Twitter). It is the fastest way to find product-market fit.
- **Basecamp / Shopify:** Famously run large, successful **modular monoliths** ("the majestic monolith"), deliberately choosing not to fragment into microservices.
- **Stack Overflow:** Historically served enormous traffic from a remarkably simple monolithic architecture, proving that monoliths can scale far further than people assume with good engineering.
- **Internal tools and CRUD apps:** Most line-of-business applications are best served by a monolith forever.

### 2.5 When the Monolith Is Still the Best Choice

This is one of the most important sections in the book. **Default to a monolith** when:

- **You are a startup or building a new product.** You do not yet know your domain boundaries. Microservices boundaries drawn too early are almost always wrong and extremely expensive to redraw. Start with a (modular) monolith and extract services later when boundaries become clear.
- **Your team is small.** With under ~15-20 engineers, the coordination problems microservices solve barely exist, while the operational overhead they add is significant.
- **Your domain is simple or well understood and stable.** If the application is not complex, the monolith's simplicity is a feature, not a limitation.
- **You lack DevOps maturity.** If you do not have solid CI/CD, containerization, monitoring, and on-call practices, running many services will overwhelm you.
- **Performance demands tight, transactional, low-latency operations.** In-process calls and ACID transactions are hard to beat.

**The pragmatic path most experts now recommend:** Start with a **well-structured modular monolith**. Keep clean internal boundaries. Extract a service *only* when a specific, concrete pressure justifies it (a part that needs independent scaling, a team that needs autonomy, a component with a different technology need). This "monolith first" approach avoids the most common and costly microservices mistake: adopting them too early.

### Chapter 2 Summary

- A **monolith** is a single deployable unit containing all layers, usually with one shared database and fast in-process communication.
- **Strengths:** simplicity, easy ACID transactions, simple testing/deployment/debugging -- especially at small scale.
- **Weaknesses (at large scale):** all-or-nothing scaling, slow risky deployments, tight coupling, technology lock-in, and limited team scalability.
- Monoliths remain the **right default** for startups, small teams, simple domains, and organizations without DevOps maturity.
- The recommended path is **monolith first**, then extract services when concrete pressures justify it.

---

## Chapter 3: Core Principles of Microservices

Patterns and tools are downstream of principles. If you get the principles right, the patterns become obvious; if you get them wrong, no tool will save you. This chapter covers the foundational principles that make microservices work.

### 3.1 Single Responsibility Principle (at the Service Level)

You know the Single Responsibility Principle (SRP) for classes: *a class should have one reason to change.* In microservices, we apply the same idea at the **service level**: **a service should own exactly one business capability and have a single reason to change.**

This means a service should be cohesive -- everything in it relates to one capability. The Payment service handles charging, refunds, and payment records. It does **not** also manage user profiles or product catalogs. If a change to shipping logic forces you to redeploy the Payment service, your boundaries are wrong.

**How to judge it:** Ask, "What business capability does this service provide?" If the answer needs the word "and" repeatedly ("it manages users *and* orders *and* inventory"), the service is doing too much. Conversely, if you cannot describe a complete capability without calling three other services for every operation, your services are too small (over-fragmented).

### 3.2 Bounded Context (Domain-Driven Design)

The single most valuable concept for drawing service boundaries comes from **Domain-Driven Design (DDD)**: the **Bounded Context**.

A **bounded context** is an explicit boundary within which a particular domain model and its language are consistent and well-defined. The crucial insight: **the same word can mean different things in different contexts.**

**Example -- the word "Customer" in e-commerce:**
- In the **Sales** context, a "Customer" has a credit limit, a sales rep, and a negotiation history.
- In the **Shipping** context, a "Customer" is essentially a delivery address and contact phone number.
- In the **Support** context, a "Customer" is a list of tickets and a satisfaction score.

These are *different models* of the same real-world person. Trying to build one giant universal "Customer" object that serves all contexts produces a bloated, tangled mess that couples everything together. Instead, **each bounded context gets its own model** -- and bounded contexts map naturally onto **service boundaries**.

```
BOUNDED CONTEXTS MAP TO SERVICES

  +------------------+   +------------------+   +------------------+
  |  Sales Context   |   | Shipping Context |   | Support Context  |
  |  (Sales Service) |   |(Shipping Service)|   |(Support Service) |
  |                  |   |                  |   |                  |
  | Customer =       |   | Customer =       |   | Customer =       |
  |  credit limit,   |   |  address,        |   |  tickets,        |
  |  sales rep       |   |  phone           |   |  satisfaction    |
  +------------------+   +------------------+   +------------------+
```

**The practical rule:** Identify your bounded contexts first (through domain analysis with business experts), and let them guide your service boundaries. Boundaries drawn along bounded contexts tend to be stable because they reflect genuine differences in the business. Boundaries drawn along technical lines (or guesses) tend to be wrong.

### 3.3 Decentralization

Microservices push **decentralization** in two dimensions:

**1. Decentralized governance.** There is no central authority dictating that every service must use the same language, framework, or database. Teams choose what fits their service. This enables the "polyglot" benefit and removes a coordination bottleneck. (In practice, most organizations standardize on a small set of "paved road" technologies to keep operational sanity -- decentralization is a spectrum, not anarchy.)

**2. Decentralized data management.** This is the big one, and it gets its own principle below. Each service manages its own data; there is no central shared database that all services depend on.

The philosophy is **"smart endpoints and dumb pipes."** Intelligence and business logic live *inside* the services (the smart endpoints). The communication infrastructure between them (HTTP, a message broker) is kept deliberately simple and logic-free (the dumb pipes). This is the opposite of SOA's heavy, logic-laden ESB.

### 3.4 Data Ownership per Service (Database per Service)

This principle is so important it gets a full pattern chapter later (Chapter 9), but the core rule is stated here because it is foundational:

> **Each service owns its data exclusively. No other service may access that data directly -- only through the owning service's API.**

This means:
- The Order service has its own Order database. The Payment service has its own Payment database.
- The Shipping service does **not** run a SQL query against the Order database. If it needs order data, it **calls the Order service's API** (or listens to events the Order service publishes).

**Why this strictness?** Because the moment two services share a database, you have re-created the monolith's tight coupling in a more dangerous, distributed form. A schema change by one team silently breaks another. Independent deployability -- the whole point -- is destroyed. Shared databases are the number-one way microservices migrations fail.

**The hard consequence:** giving up the single shared database means giving up easy cross-service ACID transactions. Maintaining consistency across services now requires patterns like **Saga** (Chapter 8) and **Event-Driven Architecture** (Chapter 10), and accepting **eventual consistency**. This trade-off is the central difficulty of microservices -- and we will spend significant time on it.

### 3.5 API-First Design

In a microservices world, a service's **API is its public contract** -- the only way the outside world (other services, gateways, clients) interacts with it. Therefore the API deserves to be designed *first and carefully*, before implementation.

**API-first design** means:
- **Define the contract before coding.** Agree on the endpoints, request/response shapes, and error semantics (often using a specification like OpenAPI/Swagger) before building the internals.
- **Treat the API as a stable promise.** Consumers depend on it; you cannot break it casually.
- **Version your APIs.** When you must make breaking changes, introduce a new version (`/v2/...`) and support the old one during a transition window. This preserves **backward compatibility** and independent deployability.
- **Hide internals.** The API exposes *capabilities*, not your database schema. Consumers should never know or depend on how you store data internally -- that freedom to change internals is precisely what microservices buy you.

**Analogy:** Think of each service as a company and its API as the legal contract it signs with partners. You can reorganize your company internally however you like, but you must honor your contracts. Break a contract and you break your partners' businesses.

### Chapter 3 Summary

- **Service-level SRP:** each service owns one business capability with a single reason to change.
- **Bounded Context (DDD)** is the best tool for drawing boundaries; the same term (e.g., "Customer") means different things in different contexts, and each context becomes a service.
- **Decentralization** applies to both governance (teams pick their tools) and data, following "smart endpoints, dumb pipes."
- **Database per service:** each service exclusively owns its data; others access it only via API or events. This destroys shared-DB coupling but forces eventual consistency and Saga-style transactions.
- **API-first design** treats each service's API as a versioned, stable contract that hides internal implementation.

---

## Chapter 4: Microservices Architecture Overview

Now we assemble the principles into a concrete, high-level architecture you will see (in some form) in nearly every production microservices system. We will present the standard topology, then explain every component in detail and walk through how a real request flows through it.

### 4.1 The High-Level Architecture

```
HIGH-LEVEL MICROSERVICES ARCHITECTURE

                          +-------------------+
                          |      Clients      |
                          | (web, mobile, 3p) |
                          +---------+---------+
                                    |  HTTPS
                                    v
                          +-------------------+
                          |    API Gateway    |  <- single entry point
                          | auth, routing,    |     routing, security,
                          | rate limiting     |     aggregation
                          +---------+---------+
                                    |
                  +-----------------+------------------+
                  |                 |                  |
                  v                 v                  v
          +--------------+  +--------------+   +--------------+
          |  Service A   |  |  Service B   |   |  Service C   |
          | (Users)      |  | (Orders)     |   | (Payments)   |
          +------+-------+  +------+-------+   +------+-------+
                 |                 |                  |
                 v                 v                  v
          +----------+      +----------+       +----------+
          |   DB A    |     |   DB B    |      |   DB C    |  <- DB per service
          +----------+      +----------+       +----------+
                 |                 |                  |
                 +--------+--------+---------+--------+
                          |                  |
                          v                  v
                   +---------------------------------+
                   |        Message Broker           |  <- async events
                   |   (Kafka / RabbitMQ)            |
                   +---------------------------------+

   Supporting infrastructure (cross-cutting, used by all):
   +----------------+  +----------------+  +-----------------------+
   | Service        |  | Config Server  |  | Observability stack   |
   | Discovery      |  | (central config|  | (logs, metrics,       |
   | (registry)     |  |  per env)      |  |  distributed tracing) |
   +----------------+  +----------------+  +-----------------------+
```

This diagram captures the canonical shape: clients enter through a gateway, the gateway routes to services, each service owns its database, and services collaborate both synchronously (direct calls) and asynchronously (via the message broker), all supported by discovery, configuration, and observability infrastructure.

### 4.2 Component-by-Component Explanation

Let us examine each component in detail -- what it does, why it exists, and what happens without it.

**1. Clients.** Web browsers (SPAs), mobile apps, and third-party API consumers. They should **not** know about your internal service topology. They talk to one stable address: the API Gateway. This decoupling lets you reorganize services internally without breaking clients.

**2. API Gateway.** The **single entry point** for all external traffic. It is a reverse proxy that sits in front of your services and handles **cross-cutting concerns** so individual services do not have to:
- **Routing:** maps incoming paths (`/orders/**`) to the correct internal service.
- **Authentication/authorization:** validates tokens (JWT) once, at the edge.
- **Rate limiting and throttling:** protects services from abuse and overload.
- **TLS termination, request/response transformation, and API composition** (aggregating data from several services into one response).

Without a gateway, every client would need to know every service's address, and every service would have to re-implement auth, rate limiting, and CORS. (Full treatment in Chapter 5.)

**3. Services (A, B, C ...).** The heart of the system. Each is an independent application owning one business capability (Users, Orders, Payments). Each exposes an API, owns its data, and is independently deployable and scalable. Services collaborate to fulfill business processes.

**4. Database per Service.** Each service has its **own private database** (DB A, DB B, DB C). These can even be different database technologies (PostgreSQL for orders, a document store for the catalog, Redis for sessions). No service touches another's database directly. (Chapter 9.)

**5. Message Broker (Kafka / RabbitMQ).** The backbone of **asynchronous, event-driven** communication. When the Order service completes an order, it publishes an `OrderPlaced` event to the broker. The Payment, Shipping, and Notification services subscribe and react independently. This **decouples** services in time (the publisher does not wait) and in knowledge (the publisher does not know who consumes). (Chapters 10.)

**6. Service Discovery (Registry).** In the cloud, service instances come and go and their network addresses change constantly (autoscaling, restarts, failures). A **service registry** (e.g., Eureka, Consul, or Kubernetes' built-in DNS) keeps a live directory of "which service instances are healthy and at what address," so callers can find them dynamically instead of using hard-coded IPs. (Chapter 6.)

**7. Config Server.** Centralized, environment-specific configuration (database URLs, feature flags, timeouts) served to all services, so configuration is managed consistently and can change without rebuilding services (e.g., Spring Cloud Config).

**8. Observability stack.** Because a single user request may traverse many services, you cannot debug with one log file. You need **centralized logging** (aggregate logs from all services), **metrics** (latency, error rates, throughput per service), and **distributed tracing** (follow one request across all the services it touches via a shared trace ID). Without this, a distributed system is effectively undebuggable. (Chapter 19.)

### 4.3 Service Communication Flow

Services communicate in two fundamental styles, and choosing the right one per interaction is a core design skill.

**Synchronous communication (request/response).** The caller sends a request and **waits** for a response. Typically HTTP/REST or gRPC.
- *Use when:* the caller needs an immediate answer to proceed (e.g., "is this user authorized?", "what is this product's price?").
- *Downside:* **temporal coupling** -- if the callee is slow or down, the caller is blocked or fails. Chains of synchronous calls multiply latency and failure probability. This is exactly why we need Circuit Breakers, Timeouts, and Retries (Chapters 7, 14).

**Asynchronous communication (event-driven).** The caller publishes a message/event to a broker and **does not wait**; interested services consume it on their own time.
- *Use when:* the work can happen independently and you want decoupling and resilience (e.g., after an order is placed, send a confirmation email, update analytics, notify the warehouse).
- *Upside:* services are decoupled; if the Notification service is briefly down, events queue up and are processed when it recovers -- the Order service is unaffected.
- *Downside:* **eventual consistency** and harder reasoning -- you must design for out-of-order, duplicate, and delayed messages.

### 4.4 A Worked Example: Placing an Order

Let us trace a realistic "place order" flow to see synchronous and asynchronous communication working together.

```
REQUEST FLOW: "Place Order"

  1. Client --> API Gateway:  POST /orders  (with JWT)
  2. Gateway validates JWT, routes to --> Order Service
  3. Order Service (SYNC) --> Payment Service: "charge $50"
        - waits for an immediate yes/no
  4. Payment Service responds: "payment approved"
  5. Order Service saves the order in DB B (status = CONFIRMED)
  6. Order Service (ASYNC) --> Message Broker: publish "OrderPlaced" event
  7. Gateway returns 201 Created to the Client   <-- client is done quickly
  ---- meanwhile, independently and asynchronously ----
  8. Shipping Service     consumes "OrderPlaced" -> schedules delivery
  9. Notification Service consumes "OrderPlaced" -> emails the customer
 10. Analytics Service    consumes "OrderPlaced" -> updates dashboards
```

Notice the deliberate design choices:
- **Payment is synchronous** (step 3-4) because the order cannot be confirmed without knowing the payment succeeded -- the caller needs an immediate answer.
- **Shipping, notification, and analytics are asynchronous** (steps 8-10) because they can happen independently after the fact. The customer should not wait for an email to be sent before seeing "order confirmed." This also means a failure in the Notification service never blocks order placement.

This blend -- synchronous where an immediate answer is required, asynchronous everywhere else -- is the hallmark of a well-designed microservices system.

### Chapter 4 Summary

- The canonical architecture: **Clients -> API Gateway -> Services (each with its own DB) -> Message Broker**, supported by **service discovery, config server, and an observability stack**.
- The **API Gateway** centralizes cross-cutting concerns (routing, auth, rate limiting) so services stay focused.
- **Database per service** and a **message broker** enable loose coupling and event-driven collaboration.
- **Service discovery, config, and observability** are not optional extras -- they are required infrastructure for any real microservices system.
- Communication is **synchronous** when an immediate answer is needed and **asynchronous** otherwise; great designs blend both, as shown in the order-placement flow.

---

# Part II -- Core Design Patterns

The following ten chapters each cover one essential microservices pattern using a consistent structure: **problem statement, why it is needed, real-world analogy, detailed explanation, architecture diagram, request/response flow, Spring Boot-style implementation, advantages, disadvantages/trade-offs, and when NOT to use it.**

---

## Chapter 5: API Gateway Pattern

### 5.1 Problem Statement

You have a dozen microservices. How does a client -- a mobile app or web SPA -- talk to them? The naive answer is "let the client call each service directly." This quickly becomes a disaster:

- The client must **know the network address of every service** and keep up as they change.
- Each service must independently implement **authentication, authorization, rate limiting, CORS, and TLS** -- duplicated everywhere, inconsistently.
- A mobile screen that needs data from five services must make **five separate round trips** over a slow mobile network.
- Exposing every internal service directly to the internet is a **security nightmare** (huge attack surface).
- You can never **refactor or split a service** without breaking every client that depended on its address.

### 5.2 Why It Is Needed

The **API Gateway** introduces a **single, stable entry point** between clients and your services. It is a reverse proxy that handles cross-cutting concerns once, at the edge, and routes requests to the appropriate internal service. It decouples clients from your internal topology, centralizes security, and reduces client-server chatter.

### 5.3 Real-World Analogy

Think of a **hotel front desk / concierge**. Guests do not wander into the kitchen, the laundry, or the maintenance room directly. They go to the **front desk**, which authenticates them (checks their room key), and routes their requests to the right department ("I need extra towels" -> housekeeping; "book me a taxi" -> concierge). The guest deals with one friendly, consistent point of contact, and the hotel's internal departments are shielded and free to reorganize.

### 5.4 Detailed Explanation

The gateway sits at the network edge and typically provides:

- **Request routing:** Maps external routes to internal services (`/api/orders/**` -> Order Service). This is its core job.
- **Authentication and authorization:** Validates JWTs/API keys once. Downstream services can then trust the request (often with the verified identity forwarded in a header).
- **Rate limiting and throttling:** Caps requests per client to prevent abuse and protect backends.
- **Load balancing:** Distributes requests across multiple instances of a service (often in cooperation with service discovery).
- **API composition / aggregation:** Combines responses from several services into one, reducing client round trips (especially valuable for mobile).
- **TLS termination, response caching, request/response transformation, and observability** (a natural place to inject trace IDs and collect metrics).

**Backends for Frontends (BFF) variant.** A common refinement is to run a separate gateway per client type -- one for web, one for mobile, one for third parties -- because each has different needs (a mobile app wants smaller, aggregated payloads). Each BFF is tailored to its client.

### 5.5 Architecture Diagram

```
API GATEWAY PATTERN

   +-----------+   +-----------+   +-----------+
   | Web App   |   | Mobile    |   | 3rd-party |
   +-----+-----+   +-----+-----+   +-----+-----+
         |               |               |
         +---------------+---------------+
                         |  HTTPS (one stable endpoint)
                         v
              +---------------------------+
              |        API Gateway        |
              |  - routing                |
              |  - authN / authZ (JWT)    |
              |  - rate limiting          |
              |  - aggregation            |
              |  - TLS termination        |
              +-------------+-------------+
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
  +-----------+      +-----------+       +-----------+
  | User Svc  |      | Order Svc |       | Payment   |
  +-----------+      +-----------+       +-----------+
```

### 5.6 Example Request/Response Flow

```
  1. Mobile app -> Gateway:   GET /api/profile-page  (Authorization: Bearer <JWT>)
  2. Gateway validates the JWT signature & expiry (rejects with 401 if invalid)
  3. Gateway performs API composition for this screen:
        a. -> User Service:    GET /users/42        => { name, avatar }
        b. -> Order Service:   GET /orders?user=42   => [ last 3 orders ]
        c. -> Loyalty Service: GET /points/42        => { points: 1200 }
  4. Gateway merges a+b+c into ONE JSON response
  5. Gateway -> Mobile app:   200 OK  { profile, recentOrders, points }

  Result: the client made ONE network call instead of THREE.
```

### 5.7 Spring Boot-Style Implementation

Using **Spring Cloud Gateway** (the modern, reactive choice), routing is configured declaratively:

```yaml
# application.yml -- Spring Cloud Gateway routes
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://ORDER-SERVICE        # "lb://" = load-balance via discovery
          predicates:
            - Path=/api/orders/**         # match these paths
          filters:
            - StripPrefix=1               # remove "/api" before forwarding
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
```

A custom global filter for authentication:

```java
@Component
public class AuthFilter implements GlobalFilter {

    private final JwtValidator jwtValidator;
    public AuthFilter(JwtValidator jwtValidator) { this.jwtValidator = jwtValidator; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());

        if (token == null || !jwtValidator.isValid(token)) {
            // Reject at the edge -- the request never reaches any service.
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // Forward the verified user id so downstream services can trust it.
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id", jwtValidator.getUserId(token))
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }
}
```

### 5.8 Advantages

- **Single entry point:** clients depend on one stable address, not the whole topology.
- **Centralized cross-cutting concerns:** auth, rate limiting, TLS, and logging done once.
- **Reduced client chatter:** aggregation cuts round trips (big mobile win).
- **Security:** internal services are not exposed directly to the internet.
- **Flexibility:** you can refactor, split, or relocate services without touching clients.

### 5.9 Disadvantages / Trade-offs

- **Potential single point of failure:** if the gateway goes down, everything is unreachable -- so it must be highly available (run multiple instances behind a load balancer).
- **Performance bottleneck risk:** all traffic flows through it; it must be scaled and tuned carefully.
- **Added latency:** one extra network hop on every request.
- **Operational and development overhead:** another component to build, deploy, secure, and monitor; risk of it becoming a "god component" if business logic creeps in.

### 5.10 When NOT to Use It

- For a **single service or a simple system** with one client, a gateway is overkill.
- When you have **very few services** and clients can reasonably talk to them directly behind a simple load balancer.
- Avoid putting **business logic** in the gateway -- if you find yourself doing that, you are recreating the SOA ESB anti-pattern. Keep it to cross-cutting concerns.

### Chapter 5 Summary

- The **API Gateway** is the single, stable entry point that handles routing, security, rate limiting, and aggregation so services do not have to.
- It decouples clients from internal topology and shrinks the attack surface.
- Trade-offs: it can become a bottleneck or single point of failure and adds a hop -- so make it highly available and keep it free of business logic.

---

## Chapter 6: Service Discovery Pattern

### 6.1 Problem Statement

In the cloud, service instances are **ephemeral**. They scale up and down with load, restart after crashes, and get rescheduled to different hosts. Their IP addresses and ports change constantly. So when the Order service needs to call the Payment service, **how does it find a healthy Payment instance's current address?** Hard-coding IPs is impossible -- they change. A static config file is stale within minutes.

### 6.2 Why It Is Needed

**Service Discovery** provides a dynamic, always-current directory of "which service instances exist, where they are, and which are healthy." Services register themselves on startup and deregister on shutdown; callers look up targets by **logical name** ("PAYMENT-SERVICE") rather than by address, and the discovery system returns a live, healthy instance.

### 6.3 Real-World Analogy

Think of a **phone book or contacts directory that updates itself in real time.** Instead of memorizing everyone's constantly-changing phone numbers, you look up a person by **name** and the directory gives you their current number -- automatically removing numbers that have been disconnected. Service discovery is that self-updating directory for services.

### 6.4 Detailed Explanation

There are two main models:

**1. Client-side discovery.** The calling service queries the **service registry** directly, gets a list of healthy instances, and picks one (applying its own load-balancing logic). Example: Netflix **Eureka** + a client-side load balancer (Ribbon/Spring Cloud LoadBalancer).

```
CLIENT-SIDE DISCOVERY

   +--------------+   1. "where is PAYMENT-SERVICE?"   +-------------+
   | Order Service|---------------------------------->| Service     |
   | (has LB      |<----------------------------------| Registry    |
   |  logic)      |   2. [10.0.0.5, 10.0.0.6, ...]    | (Eureka)    |
   +------+-------+                                    +-------------+
          | 3. picks an instance & calls it directly
          v
   +----------------+
   | Payment :10.0.0.5|
   +----------------+
```

**2. Server-side discovery.** The caller simply calls a stable address (a load balancer or the platform's DNS), and the **infrastructure** does the lookup and routing. Example: **Kubernetes** -- you call `payment-service` (a stable DNS name / virtual IP), and Kubernetes routes to a healthy pod. The caller needs no discovery logic at all.

```
SERVER-SIDE DISCOVERY (e.g., Kubernetes)

   +--------------+   call "payment-service"   +------------------+
   | Order Service|--------------------------->| Platform LB/DNS  |
   +--------------+                            | (k8s Service)    |
                                               +--------+---------+
                                                        | routes to a healthy pod
                          +----------------+   +----------------+
                          | Payment pod 1  |   | Payment pod 2  |
                          +----------------+   +----------------+
```

**Service registry and health checks.** At the core is the **registry** (Eureka, Consul, etcd, or Kubernetes' built-in store). Instances register on startup and send periodic **heartbeats**; if heartbeats stop, the registry marks the instance unhealthy and stops routing to it. **Self-registration** (the service registers itself) and **third-party registration** (a separate agent registers it) are the two registration styles.

**Modern note:** On Kubernetes, server-side discovery is built in and is the default approach -- you rarely run Eureka yourself anymore. Eureka-style client-side discovery remains common in non-Kubernetes Spring Cloud deployments and is excellent for understanding the concept.

### 6.5 Example Flow

```
  1. Payment Service starts up
       -> registers with the registry as "PAYMENT-SERVICE" at 10.0.0.5:8080
       -> begins sending heartbeats every 30s
  2. Order Service needs to charge a card
       -> asks the registry for healthy "PAYMENT-SERVICE" instances
       -> registry returns [10.0.0.5:8080, 10.0.0.6:8080]
  3. Order Service load-balances -> calls 10.0.0.6:8080
  4. Later, 10.0.0.6 crashes -> stops heartbeating
       -> registry removes it after the timeout
       -> future lookups return only healthy instances
```

### 6.6 Spring Boot-Style Implementation

Registering a service with Eureka is largely declarative:

```java
@SpringBootApplication
@EnableDiscoveryClient   // register with the service registry on startup
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
```

```yaml
# application.yml of the Payment Service
spring:
  application:
    name: payment-service        # the logical name others will look up
eureka:
  client:
    service-url:
      defaultZone: http://discovery-server:8761/eureka/
```

Calling by logical name (discovery + load balancing handled for you):

```java
@Service
public class OrderService {

    private final WebClient.Builder webClientBuilder;
    public OrderService(WebClient.Builder b) { this.webClientBuilder = b; }

    public PaymentResult charge(ChargeRequest req) {
        // "PAYMENT-SERVICE" is the logical name, NOT an IP.
        // @LoadBalanced WebClient resolves it via discovery to a real instance.
        return webClientBuilder.build()
                .post()
                .uri("http://PAYMENT-SERVICE/payments")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(PaymentResult.class)
                .block();
    }
}
```

### 6.7 Advantages

- **Dynamic and elastic:** handles autoscaling, restarts, and failures without manual config changes.
- **Decoupling:** callers use logical names; they never hard-code addresses.
- **Built-in health awareness:** unhealthy instances are automatically removed from rotation.
- **Enables load balancing** across instances naturally.

### 6.8 Disadvantages / Trade-offs

- **The registry is critical infrastructure:** it must be highly available (usually clustered), or it becomes a single point of failure.
- **Added complexity:** another system to run and monitor (unless the platform, like Kubernetes, provides it).
- **Consistency lag:** there is a small window where the registry's view is stale (an instance just died but is not yet removed), so callers still need resilience patterns (retries, circuit breakers).

### 6.9 When NOT to Use It

- When you run on a **platform that already provides discovery** (Kubernetes) -- do not bolt on a second system like Eureka; use the built-in mechanism.
- For a **tiny, fixed set of services** with stable addresses, a simple load balancer or static config may suffice.

### Chapter 6 Summary

- **Service discovery** is a self-updating directory that lets services find each other by **logical name** despite constantly changing addresses.
- **Client-side** discovery (Eureka) puts lookup/LB logic in the caller; **server-side** discovery (Kubernetes) hides it in the infrastructure.
- The **registry** tracks instances via heartbeats and removes unhealthy ones; it must be highly available.
- On Kubernetes, discovery is built in -- prefer it over running your own.

---

## Chapter 7: Circuit Breaker Pattern

### 7.1 Problem Statement

In a distributed system, services depend on each other over the network. What happens when a downstream service (say, Payment) becomes **slow or unresponsive**? Every call to it hangs until it times out. Threads in the *calling* service pile up waiting. Soon the caller exhausts its threads and **it too** becomes unresponsive. Its callers then fail, and so on. One slow service triggers a **cascading failure** that takes down the entire system. This is one of the most common and catastrophic failure modes in microservices.

### 7.2 Why It Is Needed

The **Circuit Breaker** prevents cascading failures by **monitoring calls to a remote service and "tripping" (opening) when failures exceed a threshold.** While open, calls **fail fast** (immediately, without waiting) instead of piling up, optionally returning a fallback. This protects the caller's resources and gives the struggling service time to recover.

### 7.3 Real-World Analogy

It is named after the **electrical circuit breaker** in your home. When there is a dangerous surge or short circuit, the breaker trips and cuts the power, **protecting your appliances and preventing a fire**. Once the problem is fixed, you flip it back on. A software circuit breaker does exactly this: it cuts off calls to a failing service to protect the rest of the system, then restores them once the service is healthy again.

### 7.4 Detailed Explanation: The Three States

The circuit breaker is a state machine with three states:

```
CIRCUIT BREAKER STATE MACHINE

        failures exceed threshold
   +-------+ -----------------------> +--------+
   |CLOSED |                          |  OPEN  |
   |(normal|                          |(fail   |
   | flow) | <----------------------- | fast)  |
   +-------+    test call succeeds     +--------+
       ^                                   |
       |                                   | after a cooldown timeout
       |          test call fails          v
       +-------------------------- +-------------+
                                   | HALF-OPEN   |
                                   |(trial call) |
                                   +-------------+
```

- **CLOSED (normal):** Calls flow through to the downstream service. The breaker counts failures. If the failure rate exceeds a threshold (e.g., "more than 50% of the last 20 calls failed"), it trips to OPEN.
- **OPEN (failing fast):** Calls are **not** sent downstream. They fail immediately (or return a fallback) without waiting. This stops the resource pile-up. After a configured **cooldown** (e.g., 10 seconds), the breaker moves to HALF-OPEN.
- **HALF-OPEN (testing recovery):** The breaker lets a **limited number of trial calls** through. If they succeed, the downstream service has recovered -> back to CLOSED. If they fail, it returns to OPEN for another cooldown.

**Fallbacks.** When the breaker is OPEN, you often return a **graceful fallback** instead of an error: a cached value, a default, or a "try again later" message. For example, if the Recommendations service is down, show generic popular items instead of failing the whole page.

### 7.5 Architecture Diagram

```
CIRCUIT BREAKER IN ACTION

  CLOSED state (healthy):
     Order Svc --[call]--> [CB: CLOSED] --[call]--> Payment Svc  (OK)

  OPEN state (Payment is failing):
     Order Svc --[call]--> [CB: OPEN] --X (blocked)    Payment Svc (down)
                                |
                                v
                          returns FALLBACK immediately
                          (no waiting, no thread pile-up)
```

### 7.6 Example Flow

```
  - Calls 1-15: Payment Service responds normally (CB CLOSED).
  - Payment Service starts failing (DB outage).
  - Calls 16-25: 60% fail -> exceeds 50% threshold -> CB trips to OPEN.
  - Calls 26-100 (next 10s): CB is OPEN -> each returns instantly with a
       fallback ("Payment temporarily unavailable, order queued").
       The Order Service's threads are NOT blocked. System stays alive.
  - After 10s cooldown: CB -> HALF-OPEN, lets 3 trial calls through.
  - Trial calls succeed (Payment recovered) -> CB -> CLOSED. Normal flow resumes.
```

### 7.7 Spring Boot-Style Implementation

Using **Resilience4j** (the standard choice since Hystrix retired):

```java
@Service
public class PaymentClient {

    private final WebClient webClient;
    public PaymentClient(WebClient webClient) { this.webClient = webClient; }

    // Wrap the remote call with a circuit breaker; name maps to config below.
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResult charge(ChargeRequest req) {
        return webClient.post()
                .uri("http://PAYMENT-SERVICE/payments")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(PaymentResult.class)
                .block();
    }

    // Fallback MUST have the same signature + a Throwable parameter.
    public PaymentResult paymentFallback(ChargeRequest req, Throwable t) {
        // Graceful degradation: queue the payment for later, inform the user.
        return PaymentResult.pending("Payment service busy; we will retry shortly.");
    }
}
```

```yaml
# application.yml -- Resilience4j circuit breaker config
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        sliding-window-size: 20            # look at the last 20 calls
        failure-rate-threshold: 50         # trip if >50% fail
        wait-duration-in-open-state: 10s   # cooldown before HALF-OPEN
        permitted-number-of-calls-in-half-open-state: 3
```

### 7.8 Advantages

- **Prevents cascading failures:** contains the blast radius of a failing service.
- **Fail fast:** no thread pile-ups waiting on dead services; the caller stays responsive.
- **Graceful degradation:** fallbacks keep the user experience partially functional.
- **Auto-recovery:** HALF-OPEN automatically detects when the service is healthy again.

### 7.9 Disadvantages / Trade-offs

- **Tuning is hard:** thresholds and timeouts that are too sensitive cause false trips; too lax and they do not protect. Requires real-world tuning.
- **Fallback design effort:** meaningful fallbacks require thought; a bad fallback can hide real problems or serve stale/incorrect data.
- **Added complexity and testing burden:** you must test the open/half-open behavior, which is easy to overlook.
- **Masking issues:** if you always fall back silently, you might not notice a chronically failing dependency. Pair with alerting.

### 7.10 When NOT to Use It

- For **purely asynchronous / event-driven** interactions, a circuit breaker is usually unnecessary -- the broker already decouples timing (back-pressure and retries are handled differently).
- For **non-critical, infrequent calls** where a simple timeout suffices, a full breaker may be over-engineering.
- When you have **no sensible fallback** and failing fast offers no benefit over a plain timeout (though it usually still helps avoid pile-ups).

### Chapter 7 Summary

- The **Circuit Breaker** stops cascading failures by tripping OPEN when a downstream service fails too often, making calls **fail fast** instead of piling up.
- Three states: **CLOSED** (normal), **OPEN** (fail fast / fallback), **HALF-OPEN** (test recovery).
- Pair it with **fallbacks** for graceful degradation and with **alerting** so silent fallbacks do not hide chronic problems.
- Implement with **Resilience4j**; tune thresholds against real traffic.

---

## Chapter 8: Saga Pattern (Orchestration vs Choreography)

### 8.1 Problem Statement

In a monolith, "place an order" can update the orders table, decrement inventory, and record a payment **inside one ACID transaction** -- all or nothing. But with **database per service**, these live in *three different databases* owned by three services. There is no shared transaction. If payment succeeds but inventory reservation fails, you have **inconsistent data**: a paid order with no stock. How do you maintain consistency across services **without distributed (two-phase commit) transactions**, which are slow, lock resources, and do not scale?

### 8.2 Why It Is Needed

The **Saga pattern** manages data consistency across services using a **sequence of local transactions**, where each step publishes an event (or triggers a command) that starts the next step. If any step fails, the saga executes **compensating transactions** -- explicit "undo" operations -- to roll back the previously completed steps. It trades immediate (ACID) consistency for **eventual consistency**, which is the realistic option in distributed systems.

### 8.3 Real-World Analogy

Think of **booking a vacation** with separate steps: book a flight, book a hotel, rent a car. These are independent bookings (different companies/databases). If the car rental fails after you have booked the flight and hotel, you do not magically un-book everything in one atomic action. Instead, you perform **compensations**: cancel the hotel reservation and cancel the flight (possibly incurring a cancellation fee). Each step has a corresponding "undo." That is exactly a saga with compensating transactions.

### 8.4 Detailed Explanation: Two Coordination Styles

A saga can be coordinated in two ways:

**1. Choreography (event-based, decentralized).** There is no central coordinator. Each service listens for events and reacts by doing its local transaction and publishing the next event. The workflow "emerges" from services reacting to each other.

```
SAGA -- CHOREOGRAPHY (services react to events)

  Order Svc: create order (PENDING) --publish--> [OrderCreated]
                                                      |
  Payment Svc consumes OrderCreated -> charge --publish--> [PaymentCompleted]
                                                      |
  Inventory Svc consumes PaymentCompleted -> reserve --publish--> [StockReserved]
                                                      |
  Order Svc consumes StockReserved -> mark order CONFIRMED

  On failure (e.g., Inventory fails):
  Inventory --publish--> [StockFailed]
     -> Payment Svc consumes it -> REFUND (compensation)
     -> Order Svc consumes it   -> mark order CANCELLED (compensation)
```

**2. Orchestration (command-based, centralized).** A central **orchestrator** (often the Order service or a dedicated "Order Saga" component) explicitly tells each service what to do, step by step, and decides what to do on failure.

```
SAGA -- ORCHESTRATION (a coordinator directs each step)

                 +---------------------------+
                 |     Order Orchestrator    |
                 +-------------+-------------+
        1. charge |     3. reserve |    5. confirm/compensate
                  v               v
            +-----------+   +-------------+   +-----------+
            | Payment   |   | Inventory   |   | Shipping  |
            +-----------+   +-------------+   +-----------+

  The orchestrator calls each service, waits for results, and on any
  failure issues compensating commands (refund, release stock) in reverse.
```

**Comparison:**

```
  +------------------+----------------------------+---------------------------+
  | Aspect           | Choreography               | Orchestration             |
  +------------------+----------------------------+---------------------------+
  | Control          | Decentralized (events)     | Centralized (orchestrator)|
  | Coupling         | Loose                      | Higher (to orchestrator)  |
  | Visibility       | Hard to see whole flow     | Flow is explicit/clear    |
  | Complexity       | Spreads across services    | Concentrated in one place |
  | Best for         | Simple sagas (2-3 steps)   | Complex sagas (many steps)|
  | Risk             | Cyclic event chains,       | Orchestrator can become a |
  |                  | hard to debug              | bottleneck / god service  |
  +------------------+----------------------------+---------------------------+
```

**Rule of thumb:** Use **choreography** for simple workflows with few participants; use **orchestration** when the workflow is complex, has many steps, or needs clear central visibility and control.

### 8.5 Example Flow (Orchestration, with failure)

```
  Happy path:
   1. Orchestrator: createOrder(PENDING)
   2. -> Payment: chargeCard()         => success
   3. -> Inventory: reserveStock()     => success
   4. -> Shipping: scheduleDelivery()  => success
   5. Orchestrator: markOrder(CONFIRMED)

  Failure at step 3 (out of stock):
   3. -> Inventory: reserveStock()     => FAILURE
   4. Orchestrator runs compensations IN REVERSE:
        -> Payment: refundCard()       (undo step 2)
   5. Orchestrator: markOrder(CANCELLED)
   6. Publish OrderCancelled -> Notification emails the customer
```

### 8.6 Spring Boot-Style Implementation (Orchestration sketch)

```java
@Service
public class OrderSagaOrchestrator {

    private final PaymentClient payment;
    private final InventoryClient inventory;
    private final OrderRepository orders;

    public void processOrder(Order order) {
        orders.save(order.withStatus(PENDING));   // local txn 1
        try {
            payment.charge(order.getPaymentInfo());        // step 2
            inventory.reserve(order.getItems());           // step 3
            orders.updateStatus(order.getId(), CONFIRMED);  // local txn (final)
        } catch (PaymentFailedException e) {
            orders.updateStatus(order.getId(), CANCELLED);  // nothing to compensate yet
        } catch (InventoryFailedException e) {
            // Compensate the steps that already succeeded, in reverse order.
            payment.refund(order.getPaymentInfo());         // COMPENSATION for step 2
            orders.updateStatus(order.getId(), CANCELLED);
        }
    }
}
```

**Key implementation concerns (true for any saga):**
- **Idempotency:** steps and compensations may be retried after failures; they must be safe to run more than once (e.g., "refund" must not double-refund). Use idempotency keys.
- **Compensations are business-level undos, not rollbacks:** a refund is a *new* transaction, not a database rollback. Some actions cannot be undone (an email was sent) -- design for this (e.g., send a correction).
- **Saga state must be persisted** so it survives crashes and can resume.

### 8.7 Advantages

- **Maintains consistency across services** without distributed locks or 2PC.
- **Loose coupling and resilience** (especially choreography): services collaborate via events.
- **Scalability:** no global locks; each step is a fast local transaction.

### 8.8 Disadvantages / Trade-offs

- **Only eventual consistency:** there are brief windows where data is inconsistent (an order is PENDING while payment is processing). The business must tolerate this.
- **High complexity:** you must design compensations for every step and handle partial failures, retries, and idempotency.
- **Hard to debug** (especially choreography): the workflow is spread across services and events.
- **No isolation:** unlike ACID, intermediate states are visible; you may need "semantic locks" (e.g., an order in PENDING is not yet shippable).

### 8.9 When NOT to Use It

- When operations are confined to a **single service** -- just use a normal local ACID transaction; no saga needed.
- When the business **cannot tolerate any temporary inconsistency** for the operation (rare, but reconsider your boundaries -- maybe those steps belong in one service).
- For **simple, read-only** or non-transactional flows.

### Chapter 8 Summary

- A **Saga** maintains cross-service consistency via a sequence of **local transactions**, with **compensating transactions** to undo on failure -- trading ACID for **eventual consistency**.
- **Choreography** (event-driven, decentralized) suits simple flows; **orchestration** (central coordinator) suits complex flows needing visibility.
- Critical concerns: **idempotency, persisted saga state, and business-level compensations**.
- It is complex -- use it only when a business operation genuinely spans multiple services.

---

## Chapter 9: Database per Service Pattern

### 9.1 Problem Statement

If multiple microservices read and write the **same shared database**, have you really built microservices? No -- you have built a distributed monolith with the worst of both worlds. A schema change by one team breaks others. Services cannot be deployed independently because they are coupled through the database. One service's heavy query slows everyone. So the question is: **how should data be organized so services are truly independent?**

### 9.2 Why It Is Needed

The **Database per Service** pattern mandates that **each service owns its own private database, and no other service may access it directly.** Other services get that data only through the owning service's **API** or by consuming **events** it publishes. This is the structural foundation that makes loose coupling and independent deployability *real* rather than aspirational.

### 9.3 Real-World Analogy

Think of **departments in a company with their own private filing cabinets.** The HR department keeps employee records; the Finance department keeps salary and tax records. Finance does **not** rummage through HR's cabinet directly. If Finance needs an employee's start date, it **asks HR** (via a formal request -- the API). This keeps each department in control of its own records, free to reorganize its filing system without disrupting others.

### 9.4 Detailed Explanation

The pattern has a strict rule and several flavors:

**The rule:** A service's database is an **implementation detail hidden behind its API.** No shared tables. No foreign keys across service boundaries. No "just this once" direct query into another service's tables.

**Flavors of separation (increasing strictness):**
- **Private tables per service** within a shared database server (weakest; shares infrastructure but enforces logical ownership).
- **Schema per service** (a dedicated schema per service in one DB server).
- **Database server per service** (strongest; full physical isolation, enables different DB technologies).

**Polyglot persistence.** Because each service owns its storage, it can choose the **right database for its needs**:
- Order service -> **PostgreSQL** (relational, transactional).
- Product catalog -> **MongoDB / Elasticsearch** (flexible documents, rich search).
- Shopping cart / sessions -> **Redis** (fast key-value, TTL).
- Social graph / recommendations -> **Neo4j** (graph).

```
DATABASE PER SERVICE (with polyglot persistence)

   +-------------+   +--------------+   +-------------+   +-------------+
   | User Svc    |   | Catalog Svc  |   | Order Svc   |   | Cart Svc    |
   +------+------+   +------+-------+   +------+------+   +------+------+
          |                 |                 |                 |
          v                 v                 v                 v
   +-------------+   +--------------+   +-------------+   +-------------+
   | PostgreSQL  |   | MongoDB /    |   | PostgreSQL  |   |   Redis     |
   | (users)     |   | Elasticsearch|   | (orders)    |   | (carts)     |
   +-------------+   +--------------+   +-------------+   +-------------+

   Cross-service data access ONLY via APIs or events -- never direct DB reads.
```

**The hard part -- queries that span services.** In a monolith you would `JOIN` orders and users in one SQL query. You cannot do that here (different databases). Solutions:
- **API composition:** the caller (or gateway) queries each service and joins the results in memory. Simple, but inefficient for large datasets.
- **CQRS with a read model / materialized view:** maintain a separate, denormalized read database that subscribes to events from multiple services and pre-joins the data for fast queries (Chapter 11).
- **Data duplication via events:** a service keeps a local copy of the small slice of another service's data it needs, kept up to date by consuming that service's events. (E.g., the Order service stores the customer's name locally so it does not call the User service on every read.)

### 9.5 Example Flow (cross-service data need)

```
  Need: Show an "order details" page with the order AND the customer's name.

  Option A -- API composition (synchronous):
    1. Client/Gateway -> Order Service:  GET /orders/99      => { items, total, userId=42 }
    2. Client/Gateway -> User Service:   GET /users/42       => { name: "Asha" }
    3. Compose into one response.

  Option B -- Local duplication (event-driven):
    - Order Service already stored customerName="Asha" when the order was placed
      (it had consumed a "UserUpdated" event earlier).
    1. Client -> Order Service: GET /orders/99 => { items, total, customerName }
    - No call to User Service needed at read time (faster, more resilient).
```

### 9.6 Implementation Notes (Spring Boot style)

```java
// Each service has its OWN datasource config -- no shared DB.
// Order Service application.yml:
//   spring.datasource.url: jdbc:postgresql://order-db:5432/orders
// User Service application.yml:
//   spring.datasource.url: jdbc:postgresql://user-db:5432/users
//
// The Order entity references the user ONLY by id -- there is NO JPA
// relationship/foreign key crossing the service boundary.

@Entity
public class Order {
    @Id @GeneratedValue
    private Long id;

    private Long userId;          // a plain id, NOT a @ManyToOne to a User entity
    private String customerName;  // optionally duplicated locally from UserUpdated events

    @ElementCollection
    private List<OrderLine> lines;
    private BigDecimal total;
    private OrderStatus status;
}
```

### 9.7 Advantages

- **True loose coupling and independent deployability:** schema changes do not ripple across services.
- **Polyglot persistence:** the best storage technology per service.
- **Independent scaling of data stores:** scale the order DB without touching the catalog DB.
- **Fault isolation:** one database's outage does not directly corrupt or block others.

### 9.8 Disadvantages / Trade-offs

- **No cross-service ACID transactions:** consistency now requires **Sagas** and **eventual consistency** (Chapter 8).
- **Cross-service queries are hard:** no `JOIN`s; you need API composition, CQRS, or data duplication.
- **Data duplication and synchronization:** keeping duplicated data consistent via events adds complexity.
- **Operational overhead:** many databases to provision, back up, monitor, and secure.

### 9.9 When NOT to Use It

- In a **monolith or modular monolith** -- a single well-structured database is simpler and gives you ACID for free.
- When data is **highly interconnected and constantly queried together** with strong consistency needs -- forcing separation may create more pain than value (and may signal that those parts belong in the same service).
- Early in a migration, you may temporarily share a database while you carefully separate schemas -- but treat this as a transitional state, not the destination.

### Chapter 9 Summary

- **Database per service** gives each service a private database accessed only via its API or events -- the structural foundation of real microservices independence.
- It enables **polyglot persistence** and independent scaling/deployment.
- The cost is steep: **no cross-service ACID**, harder queries, and data synchronization -- addressed by Sagas, CQRS, and event-driven duplication.
- Never share a database across services in the target architecture; doing so recreates a distributed monolith.

---

## Chapter 10: Event-Driven Architecture Pattern

### 10.1 Problem Statement

If services communicate only through **synchronous** calls (HTTP request/response), they become tightly coupled in time: the caller must wait for the callee, and if the callee is down, the caller fails. A chain of synchronous calls (A calls B calls C calls D) multiplies latency and failure probability -- if each has 99.9% uptime, four in a chain gives noticeably worse combined availability. How can services collaborate **without** being blocked by, or even aware of, each other?

### 10.2 Why It Is Needed

**Event-Driven Architecture (EDA)** has services communicate by **producing and consuming events** through a **message broker**, rather than calling each other directly. A service that does something interesting **publishes an event** ("OrderPlaced") and moves on; other services **subscribe** and react on their own time. This decouples services in **time** (the producer does not wait) and in **knowledge** (the producer does not know or care who consumes), yielding resilience and scalability.

### 10.3 Real-World Analogy

Think of a **newspaper / magazine subscription** or a **bulletin board**. The publisher prints the news once and posts it; it does not phone each reader individually. Subscribers read it whenever they like. New subscribers can start receiving issues without the publisher changing anything. The publisher and subscribers are completely decoupled -- exactly the EDA model (publish/subscribe).

### 10.4 Detailed Explanation

**Core concepts:**
- **Event:** an immutable record that **something happened** in the past (`OrderPlaced`, `PaymentCompleted`, `InventoryLow`). Events are facts, named in past tense.
- **Producer (publisher):** the service that emits the event. It does not know the consumers.
- **Consumer (subscriber):** a service that reacts to events it cares about.
- **Message broker:** the infrastructure that transports events (Kafka, RabbitMQ, AWS SNS/SQS, Google Pub/Sub).

**Two broker styles:**
- **Message queues (e.g., RabbitMQ):** typically a message is delivered to **one** consumer and removed once processed. Great for distributing work (task queues).
- **Event streams / logs (e.g., Kafka):** events are kept in an ordered, durable **log**; **many** consumer groups can each read independently, and consumers can **replay** history. Great for event broadcasting, event sourcing, and analytics.

**Event notification vs event-carried state transfer:**
- **Notification (thin event):** "OrderPlaced, id=99" -- consumers call back to get details. Less data on the wire, but adds coupling/queries.
- **State transfer (fat event):** the event carries the data consumers need (`{id:99, items:[...], total:50, customerName:"Asha"}`). Consumers do not need to call back; this maximizes decoupling and enables local data duplication (Chapter 9), at the cost of larger events.

**Event Sourcing (related advanced idea).** Instead of storing only the *current state*, store the **full sequence of events** that led to it. The current state is derived by replaying events. This gives a complete audit log and time travel, and pairs naturally with CQRS (Chapter 11) -- but it is advanced and adds real complexity; adopt deliberately.

### 10.5 Architecture Diagram

```
EVENT-DRIVEN ARCHITECTURE (publish / subscribe)

     +--------------+   publish "OrderPlaced"
     | Order Service|-----------------------+
     +--------------+                       |
                                            v
                              +-----------------------------+
                              |       Message Broker        |
                              |   topic: "order-events"     |
                              +--+---------+----------+-----+
                                 |         |          |
                consume          |         |          |  consume
                                 v         v          v
                       +-----------+ +-----------+ +-------------+
                       | Payment   | | Shipping  | | Notification|
                       | Service   | | Service   | | Service     |
                       +-----------+ +-----------+ +-------------+

   Each consumer reacts independently. The producer waits for none of them.
   If Notification is down, its events queue up and are processed on recovery.
```

### 10.6 Example Flow

```
  1. Customer places order. Order Service saves it and publishes:
        topic "order-events" <- OrderPlaced { id:99, items, total:50, userId:42 }
  2. Order Service immediately returns 201 to the client (does NOT wait).
  3. Independently and in parallel, consumers react:
        - Payment Service:      charges the card
        - Shipping Service:     reserves a delivery slot
        - Notification Service: emails the confirmation
        - Analytics Service:    increments daily sales
  4. If Shipping is temporarily down, the event waits in the broker;
     when Shipping restarts, it processes the backlog. No data lost,
     Order Service unaffected.
```

### 10.7 Spring Boot-Style Implementation (Kafka)

Producer:

```java
@Service
public class OrderService {

    private final KafkaTemplate<String, OrderPlaced> kafka;
    private final OrderRepository repo;

    public Order placeOrder(Order order) {
        Order saved = repo.save(order.withStatus(CONFIRMED));   // local transaction
        // Publish a "fat" event carrying the data consumers need.
        kafka.send("order-events",
                   new OrderPlaced(saved.getId(), saved.getItems(),
                                   saved.getTotal(), saved.getUserId()));
        return saved;   // return immediately; consumers react asynchronously
    }
}
```

Consumer:

```java
@Service
public class NotificationConsumer {

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void onOrderPlaced(OrderPlaced event) {
        // React independently. Must be IDEMPOTENT: the same event may be
        // delivered more than once ("at-least-once" delivery).
        if (alreadyNotified(event.orderId())) return;
        emailService.sendOrderConfirmation(event.userId(), event.orderId());
        markNotified(event.orderId());
    }
}
```

**The dual-write problem and the Outbox pattern.** Notice a subtle danger in the producer: it saves to the DB *and* publishes to Kafka. If it saves but crashes before publishing, the event is lost (or vice versa) -- the two systems are out of sync. The standard fix is the **Transactional Outbox**: within the same DB transaction, write the event to an `outbox` table; a separate process reads the outbox and publishes to Kafka reliably. This guarantees the event is published if and only if the data was saved.

### 10.8 Advantages

- **Loose coupling:** producers and consumers do not know each other; add new consumers with zero changes to producers.
- **Resilience:** a down consumer does not affect the producer; events buffer and are processed on recovery.
- **Scalability and responsiveness:** producers are not blocked; consumers scale independently.
- **Extensibility and auditability:** easy to add new reactions; event logs (Kafka) provide history and replay.

### 10.9 Disadvantages / Trade-offs

- **Eventual consistency:** the system is not immediately consistent; you must design for it.
- **Harder to reason about and debug:** flow is implicit and distributed; you need distributed tracing and good tooling.
- **Delivery semantics:** brokers usually guarantee **at-least-once** delivery, so consumers must be **idempotent** and handle duplicates and out-of-order events.
- **Operational complexity:** running and tuning Kafka/RabbitMQ is non-trivial.
- **The dual-write problem:** requires patterns like the Outbox to stay reliable.

### 10.10 When NOT to Use It

- When you need an **immediate, synchronous answer** to proceed (e.g., "is this user authenticated?", "what is the price right now?") -- use a synchronous call.
- For **simple systems** where the added broker and eventual-consistency complexity is not justified.
- When the team **lacks the tooling/maturity** (tracing, idempotency discipline) to operate event-driven systems safely.

### Chapter 10 Summary

- **EDA** decouples services via **events** through a **message broker**, removing temporal and knowledge coupling.
- Choose **queues** (one consumer, work distribution) vs **streams/logs** (many consumers, replay) appropriately; prefer **state-carrying events** for maximum decoupling.
- Consumers must be **idempotent** (at-least-once delivery); use the **Outbox pattern** to avoid the dual-write problem.
- The price is **eventual consistency** and harder debugging -- use it where async collaboration is genuinely beneficial.

---

## Chapter 11: CQRS (Command Query Responsibility Segregation)

### 11.1 Problem Statement

A single data model is forced to serve two very different masters: **writes** (commands that change state -- must enforce business rules, validation, and consistency) and **reads** (queries that fetch data -- must be fast, often aggregating and denormalizing data for display). Optimizing one hurts the other. A normalized schema great for safe writes requires expensive `JOIN`s for reads; a denormalized schema great for fast reads makes writes complex and error-prone. With database-per-service, reads that need data from several services are especially painful. **How do we optimize reads and writes independently?**

### 11.2 Why It Is Needed

**CQRS** separates the model for **changing** data (the **Command** side) from the model for **reading** data (the **Query** side). Each can be designed, optimized, scaled, and even stored differently. Commands go through a write model that enforces rules; queries hit one or more read models shaped exactly for the queries they serve.

### 11.3 Real-World Analogy

Think of a **library**. There is the **back office** where librarians carefully catalog new books, update records, and enforce rules (the write side -- accurate, controlled, slower). And there is the **public catalog / search terminal** out front, optimized purely for fast lookups by title, author, or subject (the read side). The search terminal is a denormalized, indexed *view* built from the authoritative records. Readers never modify data through the search terminal; staff never make customers wait while they reorganize the archive.

### 11.4 Detailed Explanation

```
CQRS OVERVIEW

   Commands (writes)                         Queries (reads)
   change state                              never change state
        |                                          ^
        v                                          |
  +--------------+    publishes events     +----------------+
  | Write Model  | ----------------------> |  Read Model(s) |
  | (normalized, |   (e.g., via broker)    | (denormalized, |
  |  rules,      |                         |  fast, indexed,|
  |  validation) |                         |  per-query)    |
  +------+-------+                         +-------+--------+
         |                                         |
         v                                         v
   +-----------+                            +--------------+
   | Write DB  |                            |  Read DB     |
   | (e.g.     |                            | (e.g. Elastic|
   |  Postgres)|                            |  / Mongo /   |
   +-----------+                            |  Redis)      |
                                            +--------------+
```

- **Command side:** Handles create/update/delete. Validates and enforces invariants, writes to the write store, and **publishes events** describing what changed.
- **Query side:** Maintains one or more **read models** (materialized views) optimized for specific queries. It **subscribes to the events** from the command side and updates its denormalized views accordingly.
- **Synchronization is asynchronous:** the read model is updated *after* the write, so it is **eventually consistent** -- there is a brief lag where a just-written change is not yet visible in queries.

**CQRS levels:** It is a spectrum. At its simplest, CQRS just means *separate read and write code paths/objects* against the **same** database (low cost, often enough). At its fullest, it means **separate databases** for reads and writes, synchronized via events (high power, high complexity). Choose the level that fits.

**Relationship to Event Sourcing.** CQRS pairs naturally with Event Sourcing (Chapter 10): the event store is the write side; read models are projections built by replaying events. But CQRS does **not** require event sourcing -- they are independent choices often used together.

**Relationship to database-per-service queries.** CQRS is the principled answer to "how do I query data spanning multiple services?" Build a read model that subscribes to events from all relevant services and pre-joins the data into a query-optimized view.

### 11.5 Example Flow

```
  WRITE:
   1. Client -> POST /products/99/price { price: 25 }   (a Command)
   2. Command handler validates, updates the Write DB (Postgres).
   3. Publishes event: ProductPriceChanged { id:99, price:25 }

  READ MODEL UPDATE (async):
   4. Query side consumes ProductPriceChanged.
   5. Updates the denormalized "product-search" view in Elasticsearch
      (price + name + category + rating, all pre-joined).

  READ:
   6. Client -> GET /search?q=shoes  (a Query)
   7. Served directly from Elasticsearch -- fast, no JOINs, no write-DB load.

  (Eventual consistency: between steps 3 and 5 there is a short window where
   the search view shows the old price.)
```

### 11.6 Spring Boot-Style Implementation (sketch)

```java
// ----- COMMAND side (write model) -----
@Service
public class ProductCommandService {
    private final ProductRepository writeRepo;          // Postgres
    private final KafkaTemplate<String, Object> kafka;

    public void changePrice(Long id, BigDecimal newPrice) {
        Product p = writeRepo.findById(id).orElseThrow();
        p.changePrice(newPrice);                         // enforces business rules
        writeRepo.save(p);
        kafka.send("product-events", new ProductPriceChanged(id, newPrice));
    }
}

// ----- QUERY side (read model) -----
@Service
public class ProductReadModelUpdater {
    private final ProductSearchRepository searchRepo;    // Elasticsearch

    @KafkaListener(topics = "product-events", groupId = "search-projection")
    public void on(ProductPriceChanged e) {
        ProductView view = searchRepo.findById(e.id()).orElse(new ProductView(e.id()));
        view.setPrice(e.price());                        // update denormalized view
        searchRepo.save(view);
    }
}

@RestController
public class ProductQueryController {
    private final ProductSearchRepository searchRepo;
    @GetMapping("/search")
    public List<ProductView> search(@RequestParam String q) {
        return searchRepo.search(q);                     // fast read, no write-DB hit
    }
}
```

### 11.7 Advantages

- **Independent optimization:** reads and writes are tuned and scaled separately (read-heavy systems scale the read side).
- **Performance:** queries hit denormalized, indexed views -- no expensive joins, no contention with writes.
- **Flexibility:** multiple read models for different query needs; ideal for cross-service queries.
- **Pairs well with EDA/Event Sourcing** for auditability and replayable projections.

### 11.8 Disadvantages / Trade-offs

- **Significant complexity:** two models, synchronization logic, and more moving parts.
- **Eventual consistency:** the read side lags the write side; the UI must handle "your change may take a moment to appear."
- **Data duplication:** the same data exists in write and read stores.
- **Harder to develop, test, and debug.**

### 11.9 When NOT to Use It

- For **simple CRUD** applications where reads and writes are similar and modest -- plain repositories are far simpler.
- When the domain **cannot tolerate even brief read staleness** for the affected data.
- As a **default** -- CQRS is a targeted tool for specific hotspots (a heavily read part, or a complex cross-service query), not a blanket architecture. Apply it selectively to the parts that need it.

### Chapter 11 Summary

- **CQRS** separates the **write model** (rules, validation, normalized) from the **read model(s)** (denormalized, fast, query-shaped), synchronized **asynchronously** via events.
- It enables independent optimization/scaling and is the clean solution for cross-service queries.
- Costs: complexity, data duplication, and **eventual consistency**.
- Apply selectively to hotspots; it ranges from "separate code paths" to "separate databases" -- pick the level you need.

---

## Chapter 12: Strangler Fig Pattern

### 12.1 Problem Statement

You have a large, business-critical **legacy monolith**. You want to move to microservices, but a **"big bang" rewrite** -- rebuild everything new, then switch over in one go -- is extraordinarily risky. Big-bang rewrites routinely run over budget, take years, and frequently fail outright; meanwhile the business still needs the old system running and evolving. **How do you migrate incrementally, safely, with the ability to roll back, and without freezing the business?**

### 12.2 Why It Is Needed

The **Strangler Fig pattern** migrates a system **incrementally** by gradually building new services *around* the edges of the monolith and **routing traffic to them piece by piece**, until the monolith's responsibilities have been fully taken over and it can be retired. At every step the system is fully working, and each small move is reversible. It de-risks migration by replacing the scary one-shot cutover with many small, safe steps.

### 12.3 Real-World Analogy

The pattern is named after the **strangler fig tree**. This plant germinates in the branches of a host tree and grows its roots **downward around** the host. Over years, the fig grows larger and stronger while the original tree inside gradually dies and decomposes -- leaving a fully self-supporting fig tree in the exact shape of the original. Software migration mirrors this: the new system grows around the old one until the old one is gone, with the system always standing.

Another analogy: **renovating a house room by room while still living in it** -- you never demolish the whole house and live on the street; you redo the kitchen, then a bathroom, then a bedroom, staying functional throughout.

### 12.4 Detailed Explanation

The mechanism relies on an **interception layer** -- usually a proxy or the **API Gateway** -- placed in front of the monolith. This layer routes each incoming request either to the **old monolith** or to a **new microservice**, based on configurable rules. Migration proceeds in a loop:

1. **Pick a slice** of functionality to extract (start with something low-risk and loosely coupled -- e.g., notifications or a read-only report).
2. **Build a new microservice** that implements that slice.
3. **Reroute** the relevant requests at the proxy from the monolith to the new service.
4. **Verify** in production (often with techniques like running both old and new in parallel and comparing -- "parallel run").
5. **Remove** the now-dead code from the monolith.
6. **Repeat** for the next slice, until the monolith is empty and can be deleted.

```
STRANGLER FIG MIGRATION (over time)

  Phase 1 (start):
     Client -> [ Proxy / Gateway ] -> Monolith (does everything)

  Phase 2 (extract "Notifications"):
     Client -> [ Proxy ] --/notify--> Notification Service (NEW)
                          \--others--> Monolith (everything else)

  Phase 3 (extract "Orders" too):
     Client -> [ Proxy ] --/notify--> Notification Service
                          --/orders--> Order Service (NEW)
                          \--others--> Monolith (shrinking)

  Phase N (done):
     Client -> [ Proxy ] -> several microservices ; Monolith retired
```

**The interception layer is key** -- it makes the migration **invisible to clients** (they keep calling the same gateway) and makes each move **instantly reversible** (just flip the route back if something breaks).

### 12.5 Example Flow

```
  Goal: extract the read-only "Product Reviews" feature first (low risk).

  1. Build Review Service (new), backed by its own database; backfill data.
  2. At the gateway, add a route: GET /products/*/reviews -> Review Service.
     All other routes still go to the monolith.
  3. Canary: send 5% of review traffic to the new service; compare results
     and error rates against the monolith.
  4. Ramp to 100% over a few days. Monitor closely.
  5. Delete the reviews code from the monolith.
  6. Move on to the next slice (e.g., search), and repeat.
```

### 12.6 Implementation Notes (gateway routing)

```yaml
# Spring Cloud Gateway -- route some paths to NEW services, rest to the monolith
spring:
  cloud:
    gateway:
      routes:
        - id: reviews-new          # already-migrated slice -> new service
          uri: lb://REVIEW-SERVICE
          predicates:
            - Path=/products/*/reviews
        - id: legacy-monolith      # everything else still goes to the monolith
          uri: http://monolith:8080
          predicates:
            - Path=/**
# As you migrate more slices, add more specific routes above the catch-all.
```

A common challenge during migration is **shared data**: while a slice is being extracted, both the monolith and the new service may need the same data. Techniques include keeping the data in the monolith temporarily and exposing it via an API, synchronizing via events, or a transitional shared-database phase (to be eliminated). The **Anti-Corruption Layer** (a DDD concept) is often used so the new service is not polluted by the monolith's legacy data model -- it translates between the two.

### 12.7 Advantages

- **Low risk:** small, incremental, reversible steps instead of a perilous big-bang.
- **Continuous delivery of value:** the business keeps running and evolving throughout.
- **Learning as you go:** early extractions teach you about boundaries before you tackle the hard parts.
- **Easy rollback:** if a new service misbehaves, reroute back to the monolith instantly.

### 12.8 Disadvantages / Trade-offs

- **Long-lived hybrid state:** for a period (sometimes years) you run **both** the monolith and microservices, plus the routing layer -- more to operate and reason about.
- **The interception layer adds complexity** and must be highly available.
- **Shared-data handling is tricky** during the transition.
- **Risk of stalling:** organizations sometimes get "stuck" half-migrated and never finish, living indefinitely with the complexity of both worlds.

### 12.9 When NOT to Use It

- For a **small or simple monolith** that could be rewritten quickly and safely -- the overhead of the strangler approach may not be worth it.
- When the legacy system is **so entangled** that no clean slice can be carved out incrementally (rare, but then significant refactoring must precede extraction).
- When the monolith is **adequate** and there is no real driver to migrate -- do not migrate for its own sake.

### Chapter 12 Summary

- The **Strangler Fig** pattern migrates incrementally by building new services around a monolith and **rerouting traffic slice by slice** via a proxy/gateway, until the monolith is retired.
- It replaces the high-risk big-bang rewrite with **small, reversible steps**, keeping the business running throughout.
- Key tools: an **interception layer** (gateway), **canary rollouts**, and an **Anti-Corruption Layer** for clean data translation.
- Watch out for the long hybrid period and the danger of never finishing.

---

## Chapter 13: Bulkhead Pattern

### 13.1 Problem Statement

A single service often serves multiple types of work or multiple clients using a **shared pool of resources** (threads, connections). If one type of work misbehaves -- say, calls to a slow downstream dependency pile up -- it can **consume the entire shared resource pool**, starving all other work. A problem isolated to *one* feature ends up taking down the *whole* service. How do we **contain** a failure or overload to just the part that caused it?

### 13.2 Why It Is Needed

The **Bulkhead pattern** **partitions resources into isolated pools** so that a failure or saturation in one partition cannot exhaust the resources of the others. Each type of work (or each downstream dependency, or each client tier) gets its own dedicated allocation. If one pool is exhausted, the others keep functioning.

### 13.3 Real-World Analogy

The pattern is named after the **bulkheads in a ship's hull**. A ship is divided into multiple watertight compartments. If the hull is breached and one compartment floods, the bulkheads **contain the water** to that compartment, and the ship stays afloat. Without bulkheads, a single breach would flood the entire hull and sink the ship. (The Titanic's bulkheads famously did not extend high enough, so flooding spilled over from one compartment to the next.)

### 13.4 Detailed Explanation

Bulkheads can be applied at several levels:

**1. Thread pool isolation (within a service).** Assign separate thread pools (or connection pools) to different operations or dependencies. Calls to the slow Inventory service use Pool A; calls to the fast Pricing service use Pool B. If Inventory hangs and exhausts Pool A, Pricing calls in Pool B are unaffected.

```
BULKHEAD -- THREAD POOL ISOLATION

  Without bulkheads (shared pool of 50 threads):
     Inventory calls hang -> consume all 50 threads
     -> Pricing & Catalog calls also starve -> WHOLE service unresponsive

  With bulkheads:
     +------------------+   +------------------+   +------------------+
     | Pool A (20 thr)  |   | Pool B (20 thr)  |   | Pool C (10 thr)  |
     | Inventory calls  |   | Pricing calls    |   | Catalog calls    |
     +------------------+   +------------------+   +------------------+
     Inventory hangs -> only Pool A is exhausted.
     Pricing (B) and Catalog (C) keep working normally.
```

**2. Service instance isolation.** Dedicate separate service instances (or clusters) to different client tiers. For example, "premium" customers are served by one set of instances and "free" customers by another, so a flood of free-tier traffic cannot degrade premium customers.

**3. Connection pool isolation.** Separate database/HTTP connection pools per dependency so one exhausted pool does not block others.

**Relationship to the Circuit Breaker.** They are complementary resilience patterns. The **bulkhead limits how many resources a dependency can consume** (isolation), while the **circuit breaker stops calling a dependency that is failing** (fail-fast). Used together: the bulkhead caps the damage while the breaker reacts to it. Many libraries (Resilience4j) provide both.

### 13.5 Example Flow

```
  Service "Checkout" calls two dependencies:
    - Inventory Service (occasionally slow)
    - Pricing Service   (fast, critical for showing totals)

  Setup: Inventory calls -> bulkhead pool of max 20 concurrent calls.
         Pricing  calls  -> bulkhead pool of max 30 concurrent calls.

  Incident: Inventory becomes very slow.
    - Up to 20 Checkout threads wait on Inventory; the 21st Inventory call
      is rejected immediately (or queued briefly) -- it CANNOT grab more.
    - Pricing's 30-slot pool is untouched -> price lookups stay fast.
    - Result: the cart still shows prices; only the stock check degrades,
      and the service as a whole stays alive.
```

### 13.6 Spring Boot-Style Implementation (Resilience4j Bulkhead)

```java
@Service
public class CheckoutService {

    // Limit concurrent calls to Inventory to its own pool.
    @Bulkhead(name = "inventory", type = Bulkhead.Type.THREADPOOL,
              fallbackMethod = "inventoryFallback")
    public Stock checkStock(Long productId) {
        return inventoryClient.getStock(productId);
    }

    // Pricing uses a SEPARATE bulkhead -> isolated from inventory issues.
    @Bulkhead(name = "pricing", type = Bulkhead.Type.THREADPOOL)
    public Price getPrice(Long productId) {
        return pricingClient.getPrice(productId);
    }

    public Stock inventoryFallback(Long productId, Throwable t) {
        return Stock.unknown();   // degrade gracefully: "stock status unavailable"
    }
}
```

```yaml
# application.yml -- separate, isolated pools
resilience4j:
  thread-pool-bulkhead:
    instances:
      inventory:
        max-thread-pool-size: 20
        queue-capacity: 10
      pricing:
        max-thread-pool-size: 30
        queue-capacity: 10
```

### 13.7 Advantages

- **Fault isolation:** a failing/slow dependency cannot consume all resources and sink the whole service.
- **Graceful degradation:** unaffected features keep working at full capacity.
- **Predictable resource usage:** each partition has a known, bounded allocation.
- **Complements circuit breakers** for layered resilience.

### 13.8 Disadvantages / Trade-offs

- **Resource under-utilization:** partitioning means a busy pool cannot borrow idle capacity from another pool -- you trade efficiency for isolation.
- **Tuning complexity:** sizing each pool correctly requires understanding traffic patterns; wrong sizes cause either waste or unnecessary rejections.
- **More configuration and moving parts** to manage and monitor.

### 13.9 When NOT to Use It

- For **small, simple services** with a single type of work and one dependency -- partitioning adds overhead with little benefit.
- When **resource efficiency is paramount** and the workloads are uniform and well-behaved.
- When the isolation can be achieved more simply at the **infrastructure level** (e.g., separate deployments/pods per workload) -- sometimes that is cleaner than in-process pools.

### Chapter 13 Summary

- The **Bulkhead pattern** isolates resources into separate pools (threads, connections, instances) so one overloaded/failing partition cannot starve the rest -- like watertight compartments on a ship.
- Apply at thread-pool, connection-pool, or service-instance levels; it **complements the circuit breaker**.
- Trade-off: isolation reduces resource sharing efficiency and adds tuning complexity.
- Use it where a service has multiple workloads/dependencies of differing reliability or priority.

---

## Chapter 14: Retry Pattern

### 14.1 Problem Statement

Networks are unreliable. In a distributed system, many failures are **transient** -- a brief network blip, a momentary timeout, a target instance restarting, or a short-lived overload. If your service gives up and fails the entire operation on the *first* hiccup, you turn countless recoverable, momentary glitches into real user-facing errors. How do you handle these **temporary** faults gracefully?

### 14.2 Why It Is Needed

The **Retry pattern** automatically **re-attempts a failed operation** a limited number of times, expecting that a transient fault will clear. Done well -- with backoff and jitter, only on retryable errors, and only on idempotent operations -- it dramatically improves reliability with minimal effort. Done badly, it amplifies outages. This chapter covers both.

### 14.3 Real-World Analogy

Think of **calling someone whose line is busy.** You do not conclude they no longer exist and never call again. You **wait a bit and try again** -- and if it is still busy, you wait a little longer before the next attempt. Eventually you get through. You also would not redial 100 times per second (that helps no one); you space out your attempts. That spacing is exactly **backoff**.

### 14.4 Detailed Explanation

A good retry strategy has several essential elements:

**1. Retry only transient, retryable errors.** Retry on timeouts, connection resets, HTTP 503 (Service Unavailable), or 429 (Too Many Requests). Do **not** retry on permanent errors like HTTP 400 (Bad Request) or 401 (Unauthorized) -- retrying a fundamentally invalid request just wastes resources and delays the inevitable failure.

**2. Limit the number of attempts.** Always cap retries (e.g., 3 attempts). Infinite retries can hang operations forever and amplify load.

**3. Use exponential backoff.** Increase the wait between attempts exponentially (e.g., 1s, 2s, 4s, 8s). This gives the struggling dependency time to recover instead of hammering it.

**4. Add jitter (randomness).** If many clients fail at the same instant and all retry after exactly 2s, they create a synchronized **retry storm** (the "thundering herd") that re-overloads the dependency. Adding random jitter spreads the retries out.

```
RETRY WITH EXPONENTIAL BACKOFF + JITTER

   Attempt 1 --fail--> wait ~1s  (+ random jitter)
   Attempt 2 --fail--> wait ~2s  (+ random jitter)
   Attempt 3 --fail--> wait ~4s  (+ random jitter)
   Attempt 4 --fail--> give up -> surface error / fallback
```

**5. Only retry IDEMPOTENT operations.** This is critical. If an operation is not idempotent, a retry can cause damage. Consider "charge the customer $50": if the first call actually succeeded but the *response* was lost, a retry would **charge the customer twice**. Solutions: design operations to be idempotent, or use an **idempotency key** (the server recognizes the repeated key and does not perform the action twice). Reads (GET) are naturally idempotent; writes need care.

**Retry + Circuit Breaker together.** Retries handle *brief* transient faults; circuit breakers handle *sustained* failures. Combine them: retry a couple of times for transient blips, but if failures persist, the circuit breaker trips and stops retrying entirely (so you do not keep retrying a service that is clearly down). The breaker prevents retries from becoming a self-inflicted DDoS.

### 14.5 Example Flow

```
  Order Service calls Payment Service:

   1. Attempt 1 -> network timeout (transient).
   2. Wait 1s (+jitter). Attempt 2 -> HTTP 503 (Payment briefly overloaded).
   3. Wait 2s (+jitter). Attempt 3 -> SUCCESS (Payment recovered). Done.

  Contrast -- non-retryable:
   1. Attempt 1 -> HTTP 400 (invalid card number).
   2. Do NOT retry -- the request is permanently invalid. Fail immediately.

  Contrast -- sustained outage (with circuit breaker):
   - Retries 1-3 all fail; this keeps happening across many requests.
   - Circuit breaker trips OPEN -> further calls fail fast, NO more retries,
     until the cooldown and a successful trial call.
```

### 14.6 Spring Boot-Style Implementation (Resilience4j / Spring Retry)

```java
@Service
public class PaymentClient {

    // Retry only on transient exceptions, with exponential backoff + jitter.
    @Retry(name = "paymentRetry", fallbackMethod = "fallback")
    @CircuitBreaker(name = "paymentService")   // breaker guards the retries
    public PaymentResult charge(ChargeRequest req) {
        // IMPORTANT: include an idempotency key so a retried charge
        // is not processed twice by the Payment Service.
        return webClient.post()
                .uri("http://PAYMENT-SERVICE/payments")
                .header("Idempotency-Key", req.idempotencyKey())
                .bodyValue(req)
                .retrieve()
                .bodyToMono(PaymentResult.class)
                .block();
    }

    public PaymentResult fallback(ChargeRequest req, Throwable t) {
        return PaymentResult.pending("Could not reach payments; will retry later.");
    }
}
```

```yaml
# application.yml -- retry configuration
resilience4j:
  retry:
    instances:
      paymentRetry:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2     # 1s, 2s, 4s
        enable-randomized-wait: true          # jitter to avoid retry storms
        retry-exceptions:                      # ONLY these are retried
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        ignore-exceptions:                     # never retry these
          - com.example.InvalidRequestException
```

### 14.7 Advantages

- **Improved reliability:** transparently absorbs transient faults, turning would-be errors into successes.
- **Better user experience:** users never see momentary glitches.
- **Simple and high-leverage:** a little configuration yields large reliability gains.

### 14.8 Disadvantages / Trade-offs

- **Can amplify outages:** naive retries (no backoff, no breaker) bombard a struggling service and cause **retry storms**, making an outage worse.
- **Increased latency:** retries with backoff delay the final outcome (success or failure), which can be bad for latency-sensitive paths.
- **Danger on non-idempotent operations:** retries can cause duplicate side effects (double charge) without idempotency safeguards.
- **Resource consumption:** holding threads/connections during waits ties up resources (mitigate with async/reactive retries).

### 14.9 When NOT to Use It

- For **non-idempotent operations without an idempotency safeguard** -- the risk of duplicate side effects is too high.
- For **permanent errors** (validation failures, auth errors) -- retrying is pointless.
- On **highly latency-sensitive paths** where waiting through backoff is worse than failing fast.
- When the failure is clearly **sustained** -- rely on the circuit breaker instead of retrying into a wall.

### Chapter 14 Summary

- The **Retry pattern** re-attempts operations that hit **transient** faults, sharply improving reliability.
- Do it right: **limited attempts, exponential backoff, jitter, retry only retryable errors, and only idempotent operations** (use idempotency keys).
- **Combine with a circuit breaker** so retries do not hammer a service that is genuinely down.
- Done badly (no backoff/breaker, non-idempotent), retries **amplify** outages -- so apply the safeguards.

---

# Part III -- Monolith to Microservices Migration

This is where theory meets reality. Migrating a live monolith to microservices is one of the hardest, highest-stakes activities in software engineering. The next four chapters give you a practical, step-by-step playbook, the challenges you will face, the safeguards to keep in place, and the mistakes that have sunk many migrations.

---

## Chapter 15: Migration Strategy -- Identifying Boundaries

### 15.1 The Mindset: Evolution, Not Revolution

The single most important principle of migration: **do it incrementally.** Never attempt a "big bang" rewrite. Instead, use the **Strangler Fig pattern** (Chapter 12) to peel off one capability at a time while the monolith keeps serving production traffic. Each step is small, verifiable, and reversible.

The first and hardest intellectual task is **finding the right boundaries** -- deciding what becomes a service. Get this wrong and you build a "distributed monolith" (all the complexity of microservices, none of the independence). Get it right and services evolve independently for years.

### 15.2 Step 1 -- Identify Service Boundaries Using Domain-Driven Design

Boundaries should follow the **business domain**, not technical layers. Use DDD's **bounded contexts** (Chapter 3) as your primary guide. Practical techniques:

**Event Storming.** Gather domain experts and engineers in a room (physical or virtual) and map out all the **domain events** ("OrderPlaced," "PaymentReceived," "ItemShipped") on a timeline. Clusters of closely related events and the commands/data around them reveal natural bounded contexts -- and thus candidate services.

**Analyze nouns and capabilities.** List the core business capabilities (Catalog, Cart, Ordering, Payment, Shipping, Customer, Notifications). Each capability is a candidate service. Watch how data and behavior cluster around each.

**Look at how the business is organized.** Teams and departments often already reflect bounded contexts. Conway's Law cuts both ways -- align services with teams that can own them.

```
FROM CAPABILITIES TO CANDIDATE SERVICES (e-commerce)

  Business capability      ->   Candidate service     ->   Owns data
  ------------------------      ------------------          --------------
  Manage accounts          ->   User Service         ->   users, auth
  Browse/search products   ->   Catalog Service      ->   products, stock
  Hold items pre-purchase  ->   Cart Service         ->   carts
  Place/track orders       ->   Order Service        ->   orders
  Take money               ->   Payment Service      ->   payments
  Deliver goods            ->   Shipping Service     ->   shipments
  Tell customers things    ->   Notification Service ->   message log
```

### 15.3 Step 2 -- Prioritize What to Extract First

Do **not** start with the hardest, most tangled core (e.g., Orders, which touches everything). Start with slices that maximize learning and minimize risk. Good first candidates:

- **Loosely coupled and on the edges:** Notifications, reporting, search, recommendations. They have few dependencies on the rest of the monolith.
- **Read-mostly or stateless:** fewer data-consistency headaches.
- **Has a distinct scaling need:** if one feature needs to scale very differently (e.g., search during sales), extracting it delivers immediate, visible value.
- **High rate of change:** a feature changed constantly by a dedicated team benefits most from independent deployability.

Extracting an easy slice first builds the **migration machinery** (gateway routing, CI/CD for services, observability) and the **team's confidence and skills** before tackling the core domain.

### 15.4 Step 3 -- Establish the Migration Infrastructure

Before extracting anything, put the enabling infrastructure in place:
- **An interception layer** (API Gateway / reverse proxy) in front of the monolith, so you can reroute traffic per route (Chapter 12).
- **CI/CD pipelines** capable of building, testing, and deploying independent services.
- **Containerization** (Docker) and an orchestration target (Kubernetes) if that is your platform.
- **Observability** (centralized logging, metrics, distributed tracing -- Chapter 19) so you can see what is happening across the hybrid system.

Without this scaffolding, every extraction is flying blind.

### 15.5 Step 4 -- The Extraction Loop (Strangler Fig in Practice)

For each chosen slice, repeat the disciplined loop:

1. **Define the new service's API** (API-first) and its data ownership.
2. **Build the service** with its own database.
3. **Migrate/replicate the data** it needs (see Chapter 16).
4. **Reroute traffic** at the gateway -- ideally gradually (canary: 1% -> 10% -> 100%).
5. **Run in parallel and verify** (compare outputs of old vs new; watch metrics and errors).
6. **Remove the dead code** from the monolith once the new service is fully trusted.
7. **Repeat.**

### Chapter 15 Summary

- Migrate **incrementally** (Strangler Fig), never big-bang.
- Find boundaries using **DDD bounded contexts**, **event storming**, and **business capabilities** -- not technical layers.
- **Extract easy, loosely-coupled edge slices first** to build machinery and confidence; save the tangled core for later.
- Stand up the **migration infrastructure** (gateway, CI/CD, containers, observability) before extracting.
- Follow the disciplined **extraction loop** with gradual rerouting and verification.

---

## Chapter 16: Data Separation and API Extraction

Splitting code is the easy part. **Splitting the database is the hard part** -- it is where most migrations stall or fail. This chapter focuses on the two hardest activities: separating data and extracting clean APIs.

### 16.1 Why Data Separation Is the Crux

In a monolith, everything shares one database with foreign keys and joins binding tables together. Microservices demand **database per service** (Chapter 9). Untangling a shared schema -- with its cross-table foreign keys, joins, and shared transactions -- into independent, service-owned databases is intricate and risky, because the data is live and the business cannot stop.

### 16.2 Data Separation Strategies

**Strategy 1 -- Separate the schema logically first, physically later.** Within the existing database, first carve the tables into per-service groupings (schemas). Remove cross-group foreign keys and joins, replacing them with application-level references (store an `order.user_id` as a plain value, not an enforced FK to the users table). Only after the logical separation is clean do you move each group to its own physical database. This staged approach reduces risk.

**Strategy 2 -- Break foreign-key relationships deliberately.** Cross-service foreign keys must go. Where the monolith did a `JOIN` between `orders` and `users`, the new design either:
- has the Order service **call the User service's API** to fetch the needed user data, or
- has the Order service **keep a local copy** of the small slice of user data it needs (e.g., `customer_name`), kept current via **events** (Chapter 10).

**Strategy 3 -- Synchronize data during transition.** While both monolith and new service need the same data, keep them in sync. Common approaches: the new service subscribes to the monolith's change events; or use **Change Data Capture (CDC)** (e.g., Debezium reading the database's transaction log) to stream changes from the monolith's DB into the new service's DB.

**Strategy 4 -- Migrate the data itself.** When the new service owns its data, you must move the historical data. Options: a one-time bulk **backfill**, or a **dual-write + backfill** period where writes go to both old and new stores while you copy history, then cut over reads.

```
DATA SEPARATION: BREAKING A SHARED TABLE RELATIONSHIP

  BEFORE (monolith, one DB):
     orders ----FK----> users        (JOIN orders, users)

  AFTER (two services, two DBs):
     Order DB                 User DB
     +----------+             +----------+
     | orders   |             | users    |
     | user_id  |  (plain id, |          |
     | cust_name| <--- kept   +----------+
     +----------+   in sync via UserUpdated events; OR
                    Order Service calls User Service API on demand)
```

### 16.3 The Shared-Database Anti-Pattern During Migration

It is acceptable, **temporarily**, for a new service and the monolith to share a database while you separate schemas -- but treat this as a **transitional state with an explicit end date**, never the destination. A permanent shared database recreates tight coupling and defeats the entire migration. Many "failed microservices" are actually distributed systems that never finished separating their data.

### 16.4 API Extraction

As you carve out a service, you must define and expose its **API** -- the only way others (including the still-existing monolith) will access its data and behavior.

**Steps for clean API extraction:**
1. **Identify the seams.** Find where the monolith's modules currently call the code you are extracting. Those call sites become API calls.
2. **Define the API contract first** (API-first, Chapter 3) -- endpoints, request/response shapes, error semantics, versioning. Use OpenAPI.
3. **Introduce an Anti-Corruption Layer (ACL).** Place a translation layer between the monolith and the new service so the new service is **not polluted** by the monolith's legacy data model. The ACL translates between the old model and the new clean model, protecting the new service's design.
4. **Replace internal calls with API calls** inside the monolith, pointing at the new service (often behind a feature flag so you can switch back).
5. **Version from day one** so future changes do not break consumers.

```
ANTI-CORRUPTION LAYER DURING EXTRACTION

   Monolith (legacy model)                New Service (clean model)
   +-------------------+    +--------+    +---------------------+
   | old "Customer"    |--> |  ACL   |--> | clean "Customer"    |
   | (messy, 40 fields)|    |translate|   | (focused, 6 fields) |
   +-------------------+    +--------+    +---------------------+
   The ACL keeps the legacy mess from leaking into the new design.
```

### Chapter 16 Summary

- **Data separation is the crux** of migration and where most failures happen.
- Strategies: **logical-then-physical** schema split, **break cross-service foreign keys** (API calls or event-synced local copies), **synchronize during transition** (events or CDC), and **migrate data** (backfill / dual-write).
- A shared database during migration is acceptable only as a **temporary, time-boxed** state -- never permanent.
- Extract clean **APIs** with API-first contracts, **versioning**, and an **Anti-Corruption Layer** to keep legacy models from polluting new services.

---

## Chapter 17: Migration Challenges and What to Keep in Check

Migration introduces a whole class of problems that simply did not exist in the monolith. This chapter catalogs the major challenges and the safeguards you must keep in place throughout.

### 17.1 The Before and After

```
BEFORE (Monolith):

        +--------+
        |   UI   |
        +---+----+
            |
   +--------v---------+
   |  Monolith App    |   (Users, Orders, Payments, Shipping all in one process)
   +--------+---------+
            |
   +--------v---------+
   | Single Database  |   (one schema, ACID transactions, joins everywhere)
   +------------------+


AFTER (Microservices):

        +--------+
        |   UI   |
        +---+----+
            |
   +--------v---------+
   |   API Gateway    |   (routing, auth, rate limiting)
   +--------+---------+
            |
   +--------+--------------------------------+
   |                  |                       |
   v                  v                       v
+--------------+ +---------------+    +----------------+
| User Service | | Order Service |    | Payment Service|
|   DB1        | |   DB2         |    |   DB3          |
+------+-------+ +-------+-------+    +--------+-------+
       |                 |                    |
       +--------+--------+----------+---------+
                |                   |
                v                   v
         +-----------------------------------+
         |          Message Queue            |  (async events between services)
         +-----------------------------------+
```

The shift from the left diagram to the right introduces every challenge below.

### 17.2 Detailed Migration Challenges

**1. Data consistency issues.** The monolith gave you ACID across the whole domain. Now data is spread across databases and updated asynchronously. You must embrace **eventual consistency** and use **Sagas** (Chapter 8) for multi-service operations. There will be brief windows where data is inconsistent (an order is PENDING while payment processes). The business and UI must tolerate this.

**2. Distributed transactions.** You can no longer wrap "place order + charge payment + reserve stock" in one transaction. Two-phase commit (2PC) is slow, locks resources, and scales poorly -- avoid it. Use **Sagas with compensating transactions** instead, accepting their complexity (idempotency, compensation design, persisted saga state).

**3. Increased latency.** In the monolith, module calls were in-process (nanoseconds). Now they are network calls (milliseconds), with serialization overhead. A request that fans out across several services can become noticeably slower, and **chains of synchronous calls compound latency**. Mitigate by: minimizing synchronous call chains, preferring async where possible, aggregating at the gateway, caching, and using efficient protocols (gRPC).

**4. Service dependencies and partial failure.** Services depend on each other, and **any dependency can fail at any time**. A failure in one service can ripple outward. You must design for partial failure with **timeouts, retries (Chapter 14), circuit breakers (Chapter 7), and bulkheads (Chapter 13)**. In a monolith, if the process is up, every module is up; in microservices, "up" is a spectrum.

**5. Monitoring and debugging complexity.** A single user action may touch ten services. When something breaks, **which service failed? where is the latency?** You cannot grep one log file. You need **distributed tracing** (a trace ID following the request across all services), **centralized logging**, and **per-service metrics** (Chapter 19). Without observability, a microservices system is effectively undebuggable.

**6. Testing complexity.** Testing one service in isolation is easy, but testing **interactions** across services is hard. You need **contract testing** (verify services honor their API contracts -- e.g., with Pact), integration testing across services, and end-to-end tests in a realistic environment.

**7. Operational and infrastructure overhead.** Dozens of services means dozens of pipelines, deployments, dashboards, and on-call surfaces. This demands real **DevOps maturity** and automation.

### 17.3 What to Keep in Check During Migration

These are the guardrails to enforce continuously, not afterthoughts:

**1. Avoid over-fragmentation (too many services).** The most common over-correction. Tiny "nano-services" create more network calls, more operational overhead, and more distributed-consistency problems than they solve. If two services always change and deploy together, or one cannot do anything useful without synchronously calling the other on every request, they probably should be **one** service. **Prefer fewer, well-bounded services**; you can always split later.

**2. Ensure proper observability from day one.** Do not bolt on monitoring after problems appear. Establish **logging (with correlation/trace IDs), metrics, tracing, and alerting** before and during extraction, so the hybrid system is visible at every step.

**3. Maintain backward compatibility.** During migration, old and new must coexist. **Version APIs**, never break existing contracts without a transition window, and use techniques like **parallel runs** (run old and new simultaneously, compare results) before cutting over. Clients must never be forced to change in lockstep with your internal refactoring.

**4. Deployment pipeline readiness.** Each service needs **automated, independent CI/CD**. If deploying a service is manual or risky, you lose the core benefit of microservices (independent deployability) and gain only the costs.

**5. Security concerns (authentication and authorization).** The monolith had one security perimeter. Now you have many services and network calls between them. Centralize **authentication** at the gateway (validate JWTs once), propagate verified identity, secure **service-to-service** communication (mTLS, internal tokens), and never trust the network. (Chapter 21.)

**6. Network resilience.** Treat the network as **unreliable by default**. Every remote call needs a **timeout**; critical calls need **retries with backoff, circuit breakers, and bulkheads**. Design every service to degrade gracefully when its dependencies misbehave.

### Chapter 17 Summary

- Migration introduces **eventual consistency, distributed transactions (Sagas), higher latency, partial failure, and debugging/testing/operational complexity** -- none of which existed in the monolith.
- Keep firm guardrails: **avoid over-fragmentation**, build **observability from day one**, preserve **backward compatibility** (versioning, parallel runs), ensure **independent CI/CD**, harden **security** (gateway auth, mTLS), and design for **network resilience** (timeouts, retries, breakers, bulkheads).
- These challenges are manageable -- but only with deliberate engineering discipline.

---

## Chapter 18: Common Migration Mistakes

Learning from others' expensive failures is cheaper than making your own. This chapter catalogs the most common and damaging migration mistakes -- and how to avoid each.

### 18.1 Mistake 1 -- Breaking Up the Monolith Too Early

**The mistake:** A startup or small team, excited by microservices, splits into many services before they understand their domain or have product-market fit.

**Why it hurts:** Early on, **domain boundaries are unknown and unstable**. Boundaries drawn too early are almost always wrong, and moving functionality *between* services later is far harder than moving it between modules in a monolith. Meanwhile, the small team drowns in operational overhead (many pipelines, distributed debugging) instead of building features.

**The fix:** **Monolith first.** Start with a well-structured (modular) monolith, discover your true boundaries through real usage, and extract services only when a concrete pressure (scaling, team autonomy, differing tech needs) justifies it. As Martin Fowler put it: "almost all the successful microservice stories have started with a monolith that got too big and was broken up."

### 18.2 Mistake 2 -- Ignoring Domain Boundaries (Wrong Decomposition)

**The mistake:** Splitting services along **technical layers** (a "database service," a "business-logic service," a "UI service") or arbitrary lines instead of business capabilities/bounded contexts.

**Why it hurts:** Technically-split services are **not independently deployable** because a single business change cuts across all of them -- changing one feature forces coordinated changes and deployments across multiple services. This is the worst of both worlds.

**The fix:** Decompose along **business capabilities / bounded contexts** (DDD). A correct service can fulfill its capability largely on its own. Use event storming to find the seams.

### 18.3 Mistake 3 -- Tight Coupling Between Services (the Distributed Monolith)

**The mistake:** Services that are technically separate but deeply intertwined -- they share a database, or every operation requires a long chain of synchronous calls to many other services, or they must always be deployed together.

**Why it hurts:** You get **all the complexity of distribution** (network, latency, partial failure) **and all the coupling of a monolith** (cannot deploy independently) -- the dreaded **distributed monolith**, which is strictly worse than either pure approach.

**The fix:** Enforce **loose coupling**: strict **database per service** (no shared DB), prefer **asynchronous events** over synchronous chains, and verify that each service can be **deployed independently**. If you cannot deploy a service without deploying others, you have a coupling problem to fix.

### 18.4 Mistake 4 -- The Big-Bang Rewrite

**The mistake:** Trying to rebuild the entire system as microservices in one large project, then switching over all at once.

**Why it hurts:** Big-bang rewrites are notorious for blowing budgets and timelines, and many never ship. Meanwhile the business is frozen, and there is no safe rollback.

**The fix:** **Strangler Fig** (Chapter 12) -- incremental, reversible, value-delivering migration with the system always running.

### 18.5 Mistake 5 -- Neglecting Observability and Automation

**The mistake:** Extracting services without first investing in distributed tracing, centralized logging, metrics, and automated CI/CD.

**Why it hurts:** You lose the ability to debug and operate the system. Problems become invisible until they are outages; manual deployment makes "independent deployability" a fiction.

**The fix:** Build the **observability and automation scaffolding before/while extracting**, not after.

### 18.6 Mistake 6 -- Over-Fragmentation (Nano-Services)

**The mistake:** Creating too many tiny services, each doing almost nothing, requiring constant chatter to accomplish anything.

**Why it hurts:** Explosion of network calls, latency, operational overhead, and distributed-consistency problems -- with little benefit.

**The fix:** **Right-size services** around cohesive capabilities. Start coarser; split only when a clear need emerges. Fewer, well-bounded services beat many anemic ones.

### 18.7 Mistake 7 -- Distributed Transactions Done Wrong

**The mistake:** Trying to preserve monolith-style ACID across services (e.g., via 2PC), or ignoring consistency entirely and hoping for the best.

**Why it hurts:** 2PC is slow and fragile at scale; ignoring consistency corrupts data.

**The fix:** Embrace **eventual consistency** and design **Sagas with idempotent steps and compensations** (Chapter 8) where operations span services.

### 18.8 A Migration Anti-Pattern Checklist

```
RED FLAGS YOUR "MICROSERVICES" ARE REALLY A DISTRIBUTED MONOLITH

  [ ] Multiple services share one database / tables.
  [ ] You must deploy several services together to ship one change.
  [ ] A typical request fans out into long synchronous call chains.
  [ ] Services are split by technical layer, not business capability.
  [ ] There is no distributed tracing; debugging means guessing.
  [ ] Teams must constantly coordinate to change "their own" services.
  [ ] You started splitting before you understood the domain.

  Any of these checked => stop and fix the coupling before continuing.
```

### Chapter 18 Summary

- The biggest mistakes: **migrating too early**, **ignoring domain boundaries**, **tight coupling (distributed monolith)**, **big-bang rewrites**, **neglecting observability/automation**, **over-fragmentation**, and **mishandling distributed transactions**.
- The cures are consistent: **monolith first**, **decompose by bounded context**, **enforce loose coupling and DB-per-service**, **migrate incrementally (Strangler Fig)**, **invest in observability/automation early**, **right-size services**, and **use Sagas for cross-service consistency**.
- Use the red-flag checklist to catch a distributed monolith before it hardens.

---

# Part IV -- Advanced Production Topics

Patterns make a system work; the topics in this part make it work **in production** -- observable, secure, deployable, and scalable. These are not optional polish; they are what separates a demo from a real system.

---

## Chapter 19: Observability (Logging, Metrics, Tracing)

### 19.1 Why Observability Is Non-Negotiable

In a monolith, debugging means reading one log file and one stack trace. In microservices, a single user request may pass through ten services across many machines. When it fails or slows down, the question "what happened?" has no easy answer unless you have built **observability** in. Observability is the ability to understand the **internal state** of your system from the **outside**, using its outputs. It rests on three pillars.

### 19.2 The Three Pillars

```
THE THREE PILLARS OF OBSERVABILITY

  +----------------+   +----------------+   +-----------------------+
  |    LOGS        |   |    METRICS     |   |       TRACES          |
  | discrete events|   | numeric        |   | the journey of ONE    |
  | with context   |   | measurements   |   | request across many   |
  | "what happened"|   | over time      |   | services              |
  |                |   | "how much/many"|   | "where did it go &    |
  |                |   |                |   |  where was the delay" |
  +----------------+   +----------------+   +-----------------------+
```

**1. Logging.** Discrete, timestamped records of events. In microservices, logs must be **centralized** (aggregated from all services into one searchable place -- e.g., the ELK stack: Elasticsearch + Logstash + Kibana, or Loki/Grafana) and **structured** (JSON, not free text) so they can be queried. Crucially, every log line must carry a **correlation ID / trace ID** so you can filter all logs belonging to one request across all services.

**2. Metrics.** Numeric measurements aggregated over time: request rate, error rate, latency percentiles (p50/p95/p99), CPU/memory, queue depth. Collected by systems like **Prometheus** and visualized in **Grafana**. A widely used framework is the **RED method** (Rate, Errors, Duration per service) and **USE** (Utilization, Saturation, Errors per resource). Metrics power **dashboards and alerts**.

**3. Distributed tracing.** The killer capability for microservices. A **trace** follows a single request as it flows through every service, recording each hop as a **span** with timing. A shared **trace ID** is generated at the gateway and propagated (via headers) through every downstream call. Tools: **OpenTelemetry** (the standard instrumentation), with backends like **Jaeger** or **Zipkin**. Tracing answers "which service in the chain caused the latency or error?" instantly.

```
A DISTRIBUTED TRACE (one request, trace-id = abc123)

  Gateway      |======================================| 320ms
   Order Svc     |==============================|       250ms
    Payment Svc     |========|                          60ms
    Inventory Svc            |==================|        140ms  <-- the bottleneck
    Kafka publish                              |==|       15ms

  One glance shows Inventory is the slow span. No guessing.
```

### 19.3 Spring Boot-Style Implementation

```java
// Spring Boot 3 uses Micrometer + Micrometer Tracing (OpenTelemetry/Brave).
// Dependencies: spring-boot-starter-actuator, micrometer-tracing-bridge-otel,
//               micrometer-registry-prometheus.

// Metrics are exposed automatically at /actuator/prometheus.
// Trace IDs are auto-generated and propagated across RestClient/WebClient/Kafka.

@RestController
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/orders")
    public Order place(@RequestBody Order order) {
        // The trace id is automatically included in this log line by the
        // logging pattern, so it correlates with logs in every other service.
        log.info("Placing order for userId={}", order.getUserId());
        return orderService.place(order);
    }
}
```

```yaml
# application.yml -- expose metrics + sample all traces (lower in prod)
management:
  endpoints.web.exposure.include: health,info,prometheus
  tracing.sampling.probability: 1.0    # 100% sampling in dev; e.g., 0.1 in prod
logging:
  pattern:
    level: "%5p [${spring.application.name},%X{traceId:-},%X{spanId:-}]"
```

### 19.4 Health Checks and Alerting

- **Health checks:** each service exposes `/actuator/health` (liveness and readiness probes) so orchestrators (Kubernetes) know whether to route traffic to it or restart it.
- **Alerting:** define alerts on symptoms users feel (high error rate, high p99 latency, saturation), not just raw resource use. Alert on **SLO violations** (e.g., "99.9% of requests succeed within 300ms").

### Chapter 19 Summary

- Observability is **mandatory** for microservices: without it, the system is undebuggable.
- The **three pillars**: **logs** (centralized, structured, trace-tagged), **metrics** (Prometheus/Grafana; RED/USE), and **distributed tracing** (OpenTelemetry + Jaeger/Zipkin) to follow one request across services.
- Propagate a **trace ID** everywhere; add **health checks** and **SLO-based alerting**.

---

## Chapter 20: Service Mesh

### 20.1 The Problem It Solves

We have seen that every service needs cross-cutting capabilities for service-to-service communication: **service discovery, load balancing, retries, timeouts, circuit breaking, mTLS encryption, and traffic metrics/tracing.** Implementing all of these in **every service**, in **every language**, via libraries (like Resilience4j) leads to duplicated effort, inconsistency, and tight coupling between business code and networking concerns. **How do we provide these capabilities uniformly without burdening each service?**

### 20.2 What a Service Mesh Is

A **service mesh** is a dedicated **infrastructure layer** that handles service-to-service communication, moving the networking concerns **out of the application code** and into the platform. It works by deploying a lightweight network proxy -- a **sidecar** -- alongside each service instance. All traffic in and out of the service flows through its sidecar. The sidecars form the **data plane**; a central **control plane** configures them.

```
SERVICE MESH (sidecar model)

   +-------------------------+        +-------------------------+
   |  Pod: Order Service     |        |  Pod: Payment Service   |
   |  +-------+  +---------+  |        |  +---------+  +-------+  |
   |  | Order |  | Sidecar |==========>| Sidecar  |  |Payment|  |
   |  | app   |  | proxy   |  | mTLS,  |  | proxy   |  | app   |  |
   |  +-------+  +---------+  | retries|  +---------+  +-------+  |
   +-------------------------+        +-------------------------+
                  ^                                  ^
                  |        configured by             |
              +-------------------------------------------+
              |            CONTROL PLANE                  |
              |  (traffic rules, security, telemetry)     |
              +-------------------------------------------+

   The apps just send normal requests; the sidecars handle mTLS, retries,
   timeouts, circuit breaking, load balancing, and emit metrics/traces.
```

### 20.3 What It Provides

- **Traffic management:** load balancing, intelligent routing, canary/blue-green traffic splitting, retries, timeouts, circuit breaking -- configured centrally, no code changes.
- **Security:** automatic **mutual TLS (mTLS)** between all services (encrypted, authenticated service-to-service traffic), plus fine-grained access policies.
- **Observability:** consistent metrics, logs, and traces for all traffic, generated by the sidecars uniformly across all languages.

Popular meshes: **Istio** (with Envoy proxies), **Linkerd** (lighter weight), and cloud-managed offerings.

### 20.4 Trade-offs

- **Pros:** uniform, language-agnostic resilience/security/observability; business code stays clean; centralized control.
- **Cons:** **significant operational complexity** (a mesh is a substantial system to run and debug); **latency and resource overhead** from sidecars on every pod; a steep learning curve. A mesh is usually justified only at **larger scale** (many services, multiple languages). For a handful of services, library-based resilience (Resilience4j) is simpler.

### Chapter 20 Summary

- A **service mesh** moves service-to-service networking concerns (mTLS, retries, timeouts, circuit breaking, telemetry) out of application code into **sidecar proxies** managed by a **control plane**.
- It delivers **uniform, language-agnostic** traffic management, security, and observability.
- It adds real **complexity and overhead** -- adopt it at scale (many services/languages), not for small systems.

---

## Chapter 21: Security in Microservices (OAuth2, JWT)

### 21.1 The Challenge

A monolith has one security perimeter and shares the logged-in user's session in memory. Microservices have **many services, many network hops, and no shared memory.** Each service may need to know **who** the user is and **what** they are allowed to do, and the network between services must not be blindly trusted. Security must be **distributed** yet consistent.

### 21.2 Authentication vs Authorization

- **Authentication (authN):** *Who are you?* Verifying identity (login).
- **Authorization (authZ):** *What are you allowed to do?* Checking permissions.

### 21.3 The Standard Approach: OAuth2 / OpenID Connect + JWT

The industry standard is **token-based** security using **OAuth2** (an authorization framework) and **OpenID Connect** (an identity layer on top of OAuth2), issuing **JWTs**.

**JWT (JSON Web Token):** a compact, **self-contained**, digitally **signed** token carrying claims (user id, roles, expiry). Because it is signed (by an Identity Provider) and self-contained, any service can **validate it locally** -- verifying the signature without a database lookup or a call back to the auth server. This statelessness is exactly what distributed systems need.

```
TOKEN-BASED SECURITY FLOW

  1. User logs in  -> Identity Provider (IdP / Auth Server, e.g., Keycloak)
  2. IdP issues a signed JWT  { sub: user42, roles:[USER], exp: ... }
  3. Client sends the JWT on every request:  Authorization: Bearer <JWT>
  4. API Gateway validates the JWT signature & expiry (rejects if invalid).
  5. Gateway forwards the request (and verified identity) to services.
  6. Each service can also validate the JWT locally and enforce authZ
     (e.g., "only ADMIN may call this endpoint") using the token's claims.
```

### 21.4 Key Practices

**1. Authenticate at the edge (gateway).** Validate tokens once at the API Gateway so invalid requests never reach services. Forward the verified identity downstream.

**2. Defense in depth -- services still verify.** Do not rely solely on the gateway. Services should also validate the JWT and enforce their own authorization (zero-trust: never assume the network is safe).

**3. Secure service-to-service communication.** Internal calls must be authenticated and encrypted too. Use **mTLS** (often via a service mesh, Chapter 20) and/or service-level tokens (the OAuth2 **client-credentials** flow for machine-to-machine calls). Never assume "internal == trusted."

**4. Short-lived tokens + refresh tokens.** Access tokens (JWTs) should be short-lived (minutes) to limit damage if leaked; use longer-lived **refresh tokens** to get new ones. JWT revocation is hard precisely because they are self-contained -- short lifetimes mitigate this.

**5. Centralize identity, manage secrets.** Use a dedicated **Identity Provider** (Keycloak, Auth0, Okta, Cognito) rather than rolling your own. Store secrets/keys in a **secrets manager** (HashiCorp Vault, cloud KMS), never in code or config files.

### 21.5 Spring Boot-Style Implementation

```java
// A resource service validating JWTs with Spring Security.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enables @PreAuthorize for fine-grained authZ
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated())
            // Validate incoming JWTs against the IdP's public keys.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}

@RestController
public class AdminController {
    // Authorization based on a claim in the validated JWT.
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) { /* ... */ }
}
```

```yaml
# application.yml -- where to fetch the IdP's signing keys for JWT validation
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://idp.example.com/realms/myrealm
```

### Chapter 21 Summary

- Microservices need **distributed, consistent** security across many services and network hops.
- The standard is **OAuth2/OpenID Connect** issuing signed, self-contained **JWTs** that services can validate **locally and statelessly**.
- Practices: **authenticate at the gateway** but also **verify in each service** (zero-trust), secure **service-to-service** calls with **mTLS / client-credentials**, use **short-lived tokens + refresh**, and centralize identity with a proper **IdP** and **secrets manager**.

---

## Chapter 22: Deployment Strategies (Blue-Green, Canary)

### 22.1 Why Deployment Strategy Matters

Independent deployability is the whole point of microservices -- but deploying frequently is only safe if you can deploy **without downtime** and **without risking a bad release** taking down production. Smart deployment strategies let you release often and roll back instantly.

### 22.2 Rolling Deployment

The default in Kubernetes. Replace old instances with new ones **gradually**, a few at a time, so the service stays available throughout. Simple and resource-efficient, but during the rollout both versions serve traffic simultaneously (so versions must be compatible), and rollback means rolling back instance by instance.

### 22.3 Blue-Green Deployment

Run **two identical environments**: **Blue** (current, live) and **Green** (new version). Deploy and fully test the new version in Green while Blue serves all production traffic. When Green is verified, **switch all traffic** from Blue to Green at once (via the load balancer/router). If anything goes wrong, **switch back to Blue instantly** -- rollback is immediate.

```
BLUE-GREEN DEPLOYMENT

  Before switch:           After switch:
   Router -> BLUE (v1) live  Router -> GREEN (v2) live
            GREEN (v2) idle           BLUE  (v1) idle (kept for instant rollback)
```

- **Pros:** zero-downtime, instant rollback, new version fully tested in a production-like environment before going live.
- **Cons:** requires **double the resources** (two full environments) during deployment; database schema changes shared between the two environments need care.

### 22.4 Canary Deployment

Release the new version to a **small subset** of users/traffic first (the "canary" -- named after canaries in coal mines that warned of danger). Monitor it closely; if metrics are healthy, **gradually increase** the share (5% -> 25% -> 100%). If problems appear, route the canary traffic back to the stable version, affecting only a small fraction of users.

```
CANARY DEPLOYMENT (gradual traffic shift)

   95% traffic -> v1 (stable)        50% -> v1        0%  -> v1
    5% traffic -> v2 (canary)  -->   50% -> v2  -->  100% -> v2
   monitor metrics at each step; roll back instantly if errors rise
```

- **Pros:** limits the **blast radius** of a bad release to a few users; enables real-world testing and **A/B testing**; gradual confidence-building.
- **Cons:** more complex traffic routing (often needs a service mesh or smart gateway); requires strong observability to judge canary health; multiple versions run at once.

### 22.5 Choosing a Strategy

```
  +------------------+--------------------+----------------------------+
  | Strategy         | Rollback           | Best for                   |
  +------------------+--------------------+----------------------------+
  | Rolling          | Slow (instance by  | Default, compatible changes|
  |                  | instance)          |                            |
  | Blue-Green       | Instant (flip back)| Critical releases needing  |
  |                  |                    | full pre-prod verification |
  | Canary           | Fast (small blast  | Risky changes; data-driven |
  |                  | radius)            | rollouts; A/B testing      |
  +------------------+--------------------+----------------------------+
```

A common companion technique is the **feature flag/toggle**: deploy code "dark" (disabled), then turn the feature on for chosen users at runtime -- decoupling *deployment* from *release*.

### Chapter 22 Summary

- Safe, frequent deployment is what makes independent deployability worthwhile.
- **Rolling** (gradual instance replacement) is the simple default; **Blue-Green** (two environments, instant switch/rollback) suits critical releases; **Canary** (gradual traffic shift) limits blast radius and enables data-driven rollouts.
- **Feature flags** decouple deploying code from releasing features.

---

## Chapter 23: Scaling Strategies

### 23.1 Vertical vs Horizontal Scaling

- **Vertical scaling (scale up):** give an instance more CPU/RAM. Simple, but has a hard ceiling and a single point of failure.
- **Horizontal scaling (scale out):** run **more instances** behind a load balancer. This is the microservices way -- it scales further and improves availability. It requires services to be **stateless** (no in-memory session state) so any instance can handle any request.

```
HORIZONTAL SCALING

   Load Balancer
      |    |    |
      v    v    v
   [Svc][Svc][Svc]   add more identical instances as load rises
```

### 23.2 The Scale Cube (three dimensions of scaling)

```
  X-axis: run multiple identical COPIES behind a load balancer (cloning).
  Y-axis: split by FUNCTION -> separate services (this IS microservices).
  Z-axis: split by DATA -> sharding/partitioning (each instance handles
          a subset of the data, e.g., users A-M vs N-Z).
```

Microservices already give you the Y-axis. You then add the X-axis (multiple instances per service) and, for data-heavy services, the Z-axis (sharding the database).

### 23.3 Auto-Scaling

The cloud-native superpower: automatically add instances when load rises and remove them when it falls. In **Kubernetes**, the **Horizontal Pod Autoscaler (HPA)** scales pods based on CPU, memory, or custom metrics (e.g., requests per second, queue length). This is **targeted scaling** -- the key microservices benefit: scale only the hot service (e.g., Search during a sale), not the whole system.

### 23.4 Supporting Techniques

- **Statelessness:** keep services stateless; push session/state to a shared store (Redis) or use stateless tokens (JWT). Stateless services scale freely.
- **Caching:** reduce load and latency with caches at multiple levels -- client, CDN, API gateway, in-service (Caffeine), and distributed (Redis). Caching is often the cheapest, highest-impact scaling lever.
- **Asynchronous processing & back-pressure:** offload heavy work to message queues so spikes are absorbed by the queue instead of overwhelming services (Chapter 10).
- **Database scaling:** read replicas (offload reads), sharding/partitioning (split data), and CQRS read models (Chapter 11) for read-heavy workloads. The database is often the real bottleneck.

### Chapter 23 Summary

- Prefer **horizontal scaling** (more stateless instances behind a load balancer) over vertical.
- The **scale cube**: X (clone instances), Y (split by function = microservices), Z (shard by data).
- Use **auto-scaling** (Kubernetes HPA) to scale **only the hot services** automatically.
- Enablers: **statelessness, caching, async processing, and database scaling** (replicas/sharding/CQRS).

---

# Part V -- Decision and Case Studies

## Chapter 24: Bringing It All Together

### 24.1 Monolith vs Microservices -- The Honest Comparison

```
  +------------------------+-------------------------+-------------------------+
  | Dimension              | Monolith                | Microservices           |
  +------------------------+-------------------------+-------------------------+
  | Initial development    | Fast & simple           | Slower (infra overhead) |
  | Deployment             | One unit, all at once   | Independent per service |
  | Scaling                | All-or-nothing          | Targeted per service    |
  | Technology choice      | One stack               | Polyglot                |
  | Data consistency       | Easy (ACID)             | Hard (eventual, Sagas)  |
  | Fault isolation        | Poor (one crash = all)  | Good (if designed well) |
  | Team autonomy          | Low (shared codebase)   | High (per-service teams)|
  | Operational complexity | Low                     | High                    |
  | Debugging              | Easy (one process)      | Hard (distributed)      |
  | Latency (internal)     | Nanoseconds (in-proc)   | Milliseconds (network)  |
  | Best team size         | Small to medium         | Large / many teams      |
  +------------------------+-------------------------+-------------------------+
```

The headline: microservices trade **simplicity** for **independent scalability and team autonomy**. You are not buying a better architecture in the abstract -- you are buying a specific set of trade-offs that only pay off under specific conditions.

### 24.2 When NOT to Use Microservices

Be honest with yourself. **Avoid microservices when:**
- You are a **startup or early-stage product** without proven product-market fit or stable domain boundaries.
- Your **team is small** (under ~15-20 engineers) -- the coordination problems microservices solve barely exist for you.
- Your **domain is simple** or the application is small -- the monolith's simplicity is a feature.
- You **lack DevOps maturity** -- no solid CI/CD, containerization, monitoring, or on-call discipline.
- Your application demands **tight, low-latency, strongly-consistent transactions** that are awkward to split.
- You are adopting them mainly because they are **fashionable** -- the worst reason.

**Default to a modular monolith. Earn your way into microservices by hitting real pain.**

### 24.3 The Decision Checklist

```
SHOULD YOU USE MICROSERVICES? -- score the YES answers

  [ ] Is your engineering org large (many teams) or growing fast?
  [ ] Do different parts of the system have very different scaling needs?
  [ ] Is the monolith's deployment cadence painfully slow / risky?
  [ ] Do you have mature CI/CD, containers, and observability already?
  [ ] Are your domain boundaries well understood and stable?
  [ ] Do you need independent technology choices for different components?
  [ ] Can the business tolerate eventual consistency where needed?
  [ ] Do you have the on-call / operational capacity for a distributed system?

  Mostly YES  -> microservices (or selective extraction) likely pays off.
  Mostly NO   -> stay with a (modular) monolith; revisit later.
```

A nuanced middle path many succeed with: the **modular monolith** -- clean module boundaries inside a single deployable -- which gives much of the structure of microservices with little of the operational cost, and a clear extraction path when you genuinely need it.

### 24.4 Real-World Case Studies (Simplified)

**Netflix -- the canonical microservices story.** In 2008 Netflix suffered a major monolith database outage that halted DVD shipping for days. They embarked on a multi-year migration to the cloud (AWS) and to hundreds of microservices. Drivers: massive global scale, the need for extreme availability, and rapid independent team delivery. Netflix's engineers created or popularized many tools and patterns in this book (Eureka for discovery, Hystrix for circuit breaking, Zuul for the gateway, chaos engineering via Chaos Monkey to test resilience). **Lesson:** at Netflix's scale and availability needs, microservices were essential -- and they invested enormously in the supporting tooling and culture to make them work.

**Amazon -- from monolith to services and the "two-pizza team."** In the early 2000s Amazon's monolithic application became a bottleneck for its growing engineering organization. They reorganized into small, autonomous **"two-pizza teams"** (small enough to be fed by two pizzas), each owning a service end-to-end with its own data, communicating only through well-defined APIs. This organizational mandate (services with hard API boundaries, no backdoor data access) is a textbook example of Conway's Law applied deliberately. It later enabled AWS itself. **Lesson:** microservices are as much an **organizational** strategy (team autonomy, ownership) as a technical one.

**Uber -- and the cautionary note.** Uber famously moved from a monolith to thousands of microservices to support rapid growth and many teams. But they also publicly discussed the **downsides** they hit -- extreme operational complexity, hard-to-trace cross-service issues, and proliferation of services -- and later worked to **consolidate** toward better-sized, well-bounded services ("macroservices"/domain-oriented architecture). **Lesson:** more services is not better; **right-sized** services aligned to domains beat both a rigid monolith and an explosion of nano-services.

**The common thread.** Every one of these companies **started as a monolith** and migrated only when scale and organizational pressure demanded it -- and each invested heavily in the tooling, culture, and discipline (observability, automation, resilience) that microservices require. None adopted microservices on day one for a small product.

### 24.5 Final Guidance: Designing a Production-Ready System

Bringing the whole book together, a production-ready microservices system typically combines:
- **Sound boundaries** from DDD bounded contexts (Chapter 3, 15) -- the foundation everything rests on.
- **An API Gateway** (Chapter 5) for a single, secure entry point.
- **Service Discovery** (Chapter 6), usually via your platform (Kubernetes).
- **Resilience patterns everywhere remote calls happen:** **Circuit Breaker, Bulkhead, Retry** with timeouts (Chapters 7, 13, 14).
- **Database per service** (Chapter 9) with **Sagas** (Chapter 8) and **Event-Driven** collaboration (Chapter 10) for cross-service consistency; **CQRS** (Chapter 11) for demanding read/query needs.
- **Security** via OAuth2/JWT and mTLS (Chapter 21).
- **Observability** as a first-class concern -- logs, metrics, tracing (Chapter 19).
- **Safe deployment** (blue-green/canary) and **auto-scaling** (Chapters 22, 23).
- And if you are migrating, the **Strangler Fig** approach (Chapter 12, 15-18) to get there incrementally and safely.

### 24.6 Closing Thoughts

Microservices are a powerful tool for the **right problems at the right scale**, not a universal best practice. Used deliberately -- with sound boundaries, resilience, observability, automation, and the organizational structure to match -- they let large organizations move fast, scale precisely, and stay resilient. Used reflexively or prematurely, they impose enormous complexity for little gain.

The mark of a senior engineer is not knowing *how* to build microservices, but knowing *when* to -- and having the discipline to start simple, measure real pain, and evolve the architecture to meet genuine needs. Build the simplest thing that solves your problem today, keep clean boundaries so you can evolve tomorrow, and reach for the patterns in this book when -- and only when -- the problem in front of you calls for them.

### Chapter 24 Summary

- Microservices trade **simplicity** for **independent scaling and team autonomy** -- a good deal only under specific conditions (large org, differing scaling needs, DevOps maturity, stable boundaries).
- **Do not** use them for early-stage products, small teams, simple domains, or out of fashion. **Default to a modular monolith** and extract when real pain justifies it.
- Use the **decision checklist** to judge readiness honestly.
- **Netflix, Amazon, and Uber** all started as monoliths, migrated under genuine pressure, invested heavily in tooling/culture, and learned to **right-size** services.
- A production-ready system combines sound DDD boundaries with gateway, discovery, resilience, DB-per-service, events/sagas, security, observability, safe deployment, and scaling -- adopted incrementally via Strangler Fig.

---

## Closing

You now have an end-to-end map of microservices: the foundations and principles, the ten essential design patterns (each with diagrams, flows, and Spring Boot-style code), a practical migration playbook, the advanced production concerns, and a clear-eyed framework for deciding **whether and when** to use microservices at all.

The best next step is to **practice**: take a small modular monolith, identify one clean bounded context, and extract it into a service behind a gateway -- with its own database, a circuit breaker on its remote calls, events for collaboration, and tracing across the boundary. Doing this once, end to end, will teach you more than re-reading any chapter. Build deliberately, measure honestly, and let real needs -- not trends -- drive your architecture.

