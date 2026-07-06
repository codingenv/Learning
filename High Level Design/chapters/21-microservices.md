# Chapter 21: Microservices Architecture

## Introduction

Microservices architecture is a design approach where a single application is composed of many
small, independently deployable services. Each service runs in its own process, communicates
over lightweight protocols (typically HTTP/REST or messaging), and is built around a specific
business capability.

This architectural style emerged as a response to the limitations of large monolithic
applications, where a single codebase and deployment unit grew unwieldy as teams and features
scaled. Companies like Netflix, Amazon, Uber, and Spotify popularized this pattern after
experiencing the pain of monolithic deployments firsthand.

In this chapter, we will explore the principles, patterns, trade-offs, and practical
considerations of microservices architecture.

---

## 21.1 Monolith vs Microservices

Before diving into microservices, it is essential to understand what they replace and when
each architectural style is appropriate.

### Monolithic Architecture

A **monolith** is a single deployable unit containing all application logic. The entire
codebase is compiled, tested, and deployed as one artifact (e.g., a single WAR/JAR file,
a single Docker image, a single binary).

```
+----------------------------------------------------------+
|                    MONOLITH APPLICATION                   |
|                                                          |
|  +------------+  +------------+  +-----------------+     |
|  |   User     |  |  Product   |  |   Order         |     |
|  |   Module   |  |  Module    |  |   Module        |     |
|  +------------+  +------------+  +-----------------+     |
|  +------------+  +------------+  +-----------------+     |
|  |  Payment   |  | Inventory  |  |  Notification   |     |
|  |  Module    |  |  Module    |  |  Module         |     |
|  +------------+  +------------+  +-----------------+     |
|                                                          |
|           Shared Database (Single Schema)                |
|  +----------------------------------------------------+  |
|  |              PostgreSQL / MySQL                     |  |
|  +----------------------------------------------------+  |
+----------------------------------------------------------+
```

### Comparison Table

| Aspect                  | Monolith                           | Microservices                          |
|-------------------------|------------------------------------|----------------------------------------|
| **Deployment**          | Single unit, all-or-nothing        | Independent per service                |
| **Codebase**            | One large codebase                 | Many small codebases                   |
| **Scaling**             | Scale entire application           | Scale individual services              |
| **Technology**          | Single tech stack                  | Polyglot (different stacks per svc)    |
| **Team Structure**      | Large teams, shared ownership      | Small teams, service ownership         |
| **Data Management**     | Single shared database             | Database per service                   |
| **Complexity**          | Simple to develop initially        | Operational complexity from day one    |
| **Latency**             | In-process calls (fast)            | Network calls (slower)                 |
| **Testing**             | Easier end-to-end tests            | Requires contract/integration tests    |
| **Fault Isolation**     | One bug can crash everything       | Failures are isolated per service      |
| **Time to Market**      | Slower as app grows                | Faster for independent features        |
| **DevOps Requirements** | Minimal                            | Significant (CI/CD, monitoring, etc.)  |

### When to Use Which

**Choose a Monolith when:**
- You are a small team (< 10 developers)
- The domain is not well understood yet
- You are building an MVP or prototype
- You want simplicity and fast initial development
- You have limited DevOps maturity

**Choose Microservices when:**
- You have multiple autonomous teams
- Different parts of the system have different scaling needs
- You need independent deployment cycles
- The domain is well understood and can be cleanly decomposed
- You have mature DevOps practices (CI/CD, monitoring, container orchestration)

> **Key Insight**: Many successful microservices architectures started as monoliths.
> Start monolithic, identify boundaries, then extract services when the pain of the
> monolith outweighs the complexity of microservices.

---

## 21.2 Characteristics of Microservices

### Single Responsibility
Each microservice should do **one thing well**. It encapsulates a single business capability
or bounded context. For example, in an e-commerce system:
- **User Service** — registration, authentication, profile management
- **Product Service** — catalog, product details, search
- **Order Service** — order creation, order status, order history
- **Payment Service** — payment processing, refunds
- **Notification Service** — email, SMS, push notifications

### Independent Deployment
Each service can be deployed independently without affecting others. This enables:
- Faster release cycles
- Reduced risk per deployment
- Canary releases and blue-green deployments per service

### Own Data Store
Each service **owns its data** and exposes it only through its API. No service should
directly access another service's database. This is the **Database per Service** pattern.

```
+----------------+     +----------------+     +------------------+
|  User Service  |     | Order Service  |     | Payment Service  |
|                |     |                |     |                  |
|  +----------+  |     |  +----------+  |     |  +------------+  |
|  | User DB  |  |     |  | Order DB |  |     |  | Payment DB |  |
|  | (Postgres)|  |     |  | (MongoDB)|  |     |  | (Postgres) |  |
|  +----------+  |     |  +----------+  |     |  +------------+  |
+----------------+     +----------------+     +------------------+
```

### Decentralized Governance
Teams choose their own technology stack, frameworks, and data stores. There is no
mandate that every service must use the same language or database.

### Designed for Failure
Microservices assume the network is unreliable. They implement:
- **Circuit breakers** — stop calling a failing service
- **Retries with backoff** — retry transient failures
- **Timeouts** — don't wait forever
- **Bulkheads** — isolate failures so they don't cascade

### Smart Endpoints, Dumb Pipes
Business logic lives in services, not in the communication infrastructure.
Communication channels (HTTP, message queues) are kept simple.

---

## 21.3 Service Decomposition Strategies

One of the hardest problems in microservices is deciding **how to split** your system.

### By Business Capability

Align services with business functions. Ask: "What does the business do?"

```
Business Capabilities:
  +-- Customer Management  --> User Service
  +-- Product Catalog      --> Product Service
  +-- Order Fulfillment    --> Order Service
  +-- Payment Processing   --> Payment Service
  +-- Shipping & Delivery  --> Shipping Service
  +-- Marketing            --> Promotion Service
```

### By Subdomain (DDD Bounded Contexts)

Use Domain-Driven Design (DDD) to identify **bounded contexts** — areas of the domain
with their own ubiquitous language and model.

```
+---------------------+    +---------------------+    +---------------------+
|   Sales Context     |    |  Inventory Context   |    |  Shipping Context   |
|                     |    |                      |    |                     |
| - Order             |    | - Stock Item         |    | - Shipment          |
| - Customer          |    | - Warehouse          |    | - Carrier           |
| - Line Item         |    | - Reservation        |    | - Tracking          |
| - Discount          |    | - Replenishment      |    | - Delivery Route    |
+---------------------+    +---------------------+    +---------------------+
```

**Note:** "Customer" in the Sales context may have different attributes than "Customer"
in a Support context. Each bounded context defines its own model.

### Common Decomposition Anti-Patterns

- **Too fine-grained**: Creating a service per entity (e.g., AddressService) leads to
  excessive network calls and tight coupling.
- **Shared data coupling**: Two services that always need the same data probably belong
  together.
- **Distributed monolith**: Services that must be deployed together are not truly
  independent. This is the worst of both worlds.

---

## 21.4 Inter-Service Communication

### Synchronous Communication

#### REST (HTTP/JSON)
The most common approach. Services expose HTTP endpoints and communicate via
request/response.

```
Order Service --HTTP POST /payments--> Payment Service
              <--HTTP 200 OK----------
```

**Pros:** Simple, widely understood, great tooling
**Cons:** Tight coupling (caller waits for response), cascade failures

#### gRPC (Protocol Buffers)
A high-performance RPC framework using Protocol Buffers for serialization.

```
Order Service --gRPC call--> Payment Service
              <--response---
```

**Pros:** Fast (binary serialization), strong typing via proto files, HTTP/2 multiplexing,
bidirectional streaming
**Cons:** Less human-readable, requires proto file management, browser support is limited

| Feature         | REST/HTTP          | gRPC                   |
|-----------------|--------------------|------------------------|
| Protocol        | HTTP/1.1 or 2      | HTTP/2                 |
| Serialization   | JSON (text)        | Protobuf (binary)      |
| Performance     | Good               | Excellent              |
| Streaming       | Limited            | Full bidirectional     |
| Browser Support | Excellent          | Requires grpc-web      |
| Tooling         | Postman, curl      | grpcurl, BloomRPC      |
| Contract        | OpenAPI/Swagger    | .proto files           |

### Asynchronous Communication

#### Message Queues (Point-to-Point)
A producer sends a message to a queue. A single consumer picks it up.

```
Order Service --msg--> [  Order Queue  ] --msg--> Payment Service
```

**Technologies:** RabbitMQ, Amazon SQS, ActiveMQ

#### Event Bus / Pub-Sub (One-to-Many)
A producer publishes an event. Multiple consumers (subscribers) receive it.

```
                          +---> Inventory Service
Order Service --event-->  | Event Bus (Kafka/SNS)
                          +---> Notification Service
                          +---> Analytics Service
```

**Technologies:** Apache Kafka, Amazon SNS/EventBridge, NATS, Redis Streams

**Pros of Asynchronous:** Loose coupling, resilience (messages buffered), natural
load leveling
**Cons of Asynchronous:** Eventual consistency, harder to debug, message ordering
challenges

---

## 21.5 API Gateway Pattern

An **API Gateway** is the single entry point for all client requests. It routes requests
to the appropriate microservice and can handle cross-cutting concerns.

```
                    +-------------------+
   Clients          |   API GATEWAY     |
  (Web, Mobile) --> |                   |
                    | - Routing         |
                    | - Authentication  |
                    | - Rate Limiting   |
                    | - Load Balancing  |
                    | - Response Agg.   |
                    | - SSL Termination |
                    +---+---+---+---+---+
                        |   |   |   |
              +---------+   |   |   +---------+
              |             |   |             |
         +----v---+  +-----v--+ +---v----+ +--v--------+
         | User   |  | Product| | Order  | | Payment   |
         | Service|  | Service| | Service| | Service   |
         +--------+  +--------+ +--------+ +-----------+
```

### Responsibilities

| Concern              | Description                                              |
|----------------------|----------------------------------------------------------|
| **Routing**          | Route `/api/users/*` to User Service, `/api/orders/*` to Order Service |
| **Authentication**   | Validate JWT tokens, API keys before forwarding requests |
| **Rate Limiting**    | Protect services from excessive requests                 |
| **Response Aggregation** | Combine data from multiple services into one response |
| **Protocol Translation** | Accept REST from clients, call gRPC internally       |
| **Caching**          | Cache responses to reduce backend load                   |
| **Circuit Breaking** | Stop forwarding to unhealthy services                    |

### Popular API Gateways

| Gateway          | Notes                                                  |
|------------------|--------------------------------------------------------|
| **Kong**         | Open-source, plugin-based, built on Nginx              |
| **AWS API GW**   | Managed, integrates with Lambda, supports REST/WebSocket |
| **Zuul**         | Netflix OSS, Java-based, works with Spring Cloud       |
| **Envoy**        | High-performance L4/L7 proxy, used in service meshes   |
| **APISIX**       | Apache project, dynamic routing, plugin architecture   |

### Backend for Frontend (BFF)
A variant where you create **separate gateways** for different client types:

```
  Mobile App --> [Mobile BFF] --> Microservices
  Web App    --> [Web BFF]    --> Microservices
  Admin      --> [Admin BFF]  --> Microservices
```

Each BFF is tailored to the specific needs and screen sizes of its client.

---

## 21.6 Service Discovery

In a dynamic environment where services scale up/down and IP addresses change,
services need a way to **find each other**.

### Client-Side Discovery
The client queries a **service registry** to find available instances, then
load-balances across them.

```
+--------+     1. Query     +------------------+
| Order  |  ------------->  | Service Registry |
| Service|  <-------------  | (Eureka/Consul)  |
+--------+  2. Return IPs  +------------------+
    |
    | 3. Direct call to chosen instance
    v
+----------------+
| Payment Service|  (one of many instances)
+----------------+
```

**Examples:** Netflix Eureka + Ribbon, HashiCorp Consul

### Server-Side Discovery
The client sends requests to a **load balancer** or **DNS**, which resolves to a
healthy instance. The client doesn't need to know about the registry.

```
+--------+     1. Request       +---------------+     2. Forward    +----------------+
| Order  |  ----------------->  | Load Balancer |  ------------->  | Payment Service|
| Service|  <-----------------  | / DNS         |  <-------------  | (instance)     |
+--------+     4. Response      +---------------+     3. Response   +----------------+
```

**Examples:** Kubernetes DNS + kube-proxy, AWS ALB + Cloud Map

| Aspect               | Client-Side                      | Server-Side                    |
|-----------------------|----------------------------------|--------------------------------|
| **Complexity**        | Client needs discovery logic     | Client is simple               |
| **Load Balancing**    | Client-side (more flexible)      | Server-side (centralized)      |
| **Language Support**  | Needs library per language       | Language agnostic              |
| **Infrastructure**    | Lighter                          | Requires LB infrastructure     |
| **Example**           | Eureka + Ribbon                  | Kubernetes Services            |

---

## 21.7 Service Mesh

A **service mesh** is a dedicated infrastructure layer that handles service-to-service
communication. It uses a **sidecar proxy** deployed alongside each service instance.

```
+----------------------------------+    +----------------------------------+
|         Pod A                    |    |         Pod B                    |
|  +-------------+ +------------+ |    | +------------+ +-------------+  |
|  | Order       | | Sidecar    | |    | | Sidecar    | | Payment     |  |
|  | Service     |<>| Proxy     |<-------->| Proxy     |<>| Service     |  |
|  |             | | (Envoy)   | |    | | (Envoy)    | |             |  |
|  +-------------+ +------------+ |    | +------------+ +-------------+  |
+----------------------------------+    +----------------------------------+
                         |                         |
                         v                         v
                  +--------------------------------------+
                  |         Control Plane                 |
                  |   (Istio Pilot / Linkerd Controller)  |
                  |   - Traffic management                |
                  |   - Security policies                 |
                  |   - Observability config              |
                  +--------------------------------------+
```

### What Problems Does a Service Mesh Solve?

| Problem                   | How Service Mesh Helps                              |
|---------------------------|-----------------------------------------------------|
| **Mutual TLS (mTLS)**     | Automatic encryption between services               |
| **Traffic Management**    | Canary releases, traffic splitting, retries          |
| **Observability**         | Distributed tracing, metrics, access logs            |
| **Resilience**            | Circuit breaking, timeouts, retries — without code   |
| **Access Control**        | Service-to-service authorization policies            |

### Popular Service Meshes

| Mesh         | Sidecar Proxy | Key Feature                              |
|--------------|---------------|------------------------------------------|
| **Istio**    | Envoy         | Feature-rich, complex, widely adopted    |
| **Linkerd**  | linkerd-proxy | Lightweight, simple, Rust-based proxy    |
| **Consul Connect** | Envoy   | Integrates with HashiCorp ecosystem      |

> **When to use:** If you have 50+ microservices and need consistent security,
> observability, and traffic management without modifying application code.

---

## 21.8 Data Management in Microservices

### Database per Service (Recommended)
Each service owns its database. Other services access data only through APIs.

**Pros:** Loose coupling, independent scaling, technology freedom
**Cons:** Cross-service queries are hard, data consistency is eventual

### Shared Database (Anti-Pattern)
Multiple services read/write to the same database.

**Why it's bad:**
- Schema changes affect all services
- No independent deployment
- Performance contention
- Tight coupling through shared tables

### Strategies for Cross-Service Data

1. **API Composition**: A service calls multiple services and joins data in memory
2. **CQRS**: Maintain denormalized read models that combine data from multiple services
3. **Event-Driven Sync**: Services publish events when data changes; other services
   maintain local copies of the data they need

---

## 21.9 Distributed Tracing

In a microservices architecture, a single user request may traverse 5-10 services.
Debugging failures requires **distributed tracing**.

```
User Request (trace-id: abc123)
   |
   v
[API Gateway] --> span-1 (10ms)
   |
   v
[Order Service] --> span-2 (25ms)
   |        |
   |        v
   |   [Inventory Service] --> span-3 (15ms)
   |
   v
[Payment Service] --> span-4 (50ms)
   |
   v
[Notification Service] --> span-5 (5ms)

Total request time: 105ms
Bottleneck identified: Payment Service (50ms)
```

### Key Concepts
- **Trace**: The entire journey of a request across services
- **Span**: A single operation within a trace (one service's work)
- **Trace ID**: Unique identifier propagated across all services
- **Span ID**: Unique identifier for each span within a trace

### Tools
| Tool            | Type          | Notes                              |
|-----------------|---------------|------------------------------------|
| **Jaeger**      | Open source   | CNCF project, Uber-originated      |
| **Zipkin**      | Open source   | Twitter-originated                 |
| **AWS X-Ray**   | Managed       | Integrates with AWS services       |
| **Datadog APM** | Commercial    | Full observability platform        |
| **OpenTelemetry** | Standard    | Vendor-neutral instrumentation     |

---

## 21.10 Challenges of Microservices

| Challenge                 | Description                                          |
|---------------------------|------------------------------------------------------|
| **Network Latency**       | Inter-service calls add latency vs in-process calls  |
| **Data Consistency**      | No ACID transactions across services                 |
| **Debugging**             | Distributed systems are harder to debug              |
| **Deployment Complexity** | Need CI/CD per service, container orchestration      |
| **Testing**               | Integration tests across services are complex        |
| **Service Sprawl**        | Too many services become hard to manage              |
| **Distributed Tracing**   | Required to understand request flows                 |
| **Security**              | More network surface area to secure                  |
| **Data Duplication**      | Services may maintain copies of shared data          |
| **Organizational**        | Need team autonomy, clear service ownership          |

---

## 21.11 Migration Strategy: Strangler Fig Pattern

Named after the strangler fig tree that grows around a host tree until it replaces it.

### How It Works

1. **Identify** a module in the monolith to extract
2. **Build** the new microservice alongside the monolith
3. **Route** traffic for that feature to the new service (via API Gateway or proxy)
4. **Remove** the old code from the monolith once the new service is proven
5. **Repeat** for the next module

```
Phase 1: Monolith handles everything
+---------------------------+
|        MONOLITH           |
| [Users][Orders][Payments] |
+---------------------------+

Phase 2: Extract Payment Service
+---------------------------+     +-------------------+
|        MONOLITH           |     | Payment Service   |
| [Users][Orders]           |     | (new)             |
+---------------------------+     +-------------------+
         ^                               ^
         |         API Gateway           |
         +---------- routes ------------>+
           /users, /orders                /payments

Phase 3: Extract Order Service
+---------------------------+     +-------------------+
|        MONOLITH           |     | Payment Service   |
| [Users]                   |     +-------------------+
+---------------------------+     | Order Service     |
                                  +-------------------+

Phase 4: Monolith is fully decomposed (or remains as a small core)
```

### Tips for Migration
- Start with the **least coupled** module
- Use **feature flags** to control traffic routing
- Run the old and new path in **parallel** and compare results (shadow testing)
- Keep a **shared database** temporarily and migrate data ownership gradually

---

## 21.12 Complete Microservices Architecture Diagram

```
                            +------------------+
          Clients           |   CDN (static)   |
      (Web, Mobile, IoT)    +------------------+
              |
              v
     +-------------------+
     |    API GATEWAY     |
     | (Kong / Envoy)     |
     | - Auth (JWT)       |
     | - Rate Limiting    |
     | - Routing          |
     +---+---+---+---+---+
         |   |   |   |
    +----+   |   |   +----+
    |        |   |        |
    v        v   v        v
+-------+ +-------+ +--------+ +-----------+
| User  | |Product| | Order  | | Payment   |
| Svc   | | Svc   | | Svc    | | Svc       |
+---+---+ +---+---+ +---+----+ +-----+-----+
    |         |          |            |
    v         v          v            v
+------+  +------+  +--------+  +----------+
|UserDB|  |ProdDB|  |OrderDB |  |PaymentDB |
|Postgr|  |Mongo |  |Postgr  |  |Postgr    |
+------+  +------+  +--------+  +----------+

    |         |          |            |
    +---------+----------+------------+
              |
              v
     +------------------+
     |   MESSAGE BUS    |
     |  (Kafka/RabbitMQ)|
     +--------+---------+
              |
    +---------+---------+
    |                   |
    v                   v
+----------+    +---------------+
|Inventory |    | Notification  |
|  Svc     |    |    Svc        |
+----+-----+    +-------+-------+
     |                  |
+----+-----+    +-------+-------+
|InvDB     |    | Email/SMS/Push|
|Redis     |    | (SendGrid,    |
+----------+    |  Twilio)      |
                +---------------+

   Observability Stack:
   +--------------------------------------------+
   | Prometheus (Metrics) | Jaeger (Tracing)     |
   | Grafana (Dashboards) | ELK Stack (Logging)  |
   +--------------------------------------------+
```

---

## 21.13 Key Takeaways

1. **Microservices decompose a system into small, independently deployable services**
   each owning a single business capability and its data.

2. **Start with a monolith** unless you have strong reasons and mature DevOps
   practices. Use the Strangler Fig pattern to migrate incrementally.

3. **Service decomposition** should follow business capabilities or DDD bounded
   contexts, not technical layers.

4. **Communication** can be synchronous (REST, gRPC) or asynchronous (message queues,
   event bus). Prefer async for loose coupling and resilience.

5. **API Gateway** is the front door — handles routing, auth, rate limiting,
   and aggregation.

6. **Service Discovery** (Consul, Eureka, Kubernetes DNS) enables dynamic routing
   as services scale and move.

7. **Service Mesh** (Istio, Linkerd) provides mTLS, traffic management, and
   observability as infrastructure — no application code changes needed.

8. **Database per Service** is essential. Shared databases create tight coupling
   and negate microservices benefits.

9. **Distributed tracing** (Jaeger, Zipkin, OpenTelemetry) is mandatory — you
   cannot debug distributed systems without it.

10. **Microservices trade development simplicity for operational complexity.**
    Invest in automation, monitoring, and team autonomy.

---

## Further Reading

- *Building Microservices* by Sam Newman
- *Microservices Patterns* by Chris Richardson
- *Domain-Driven Design* by Eric Evans
- Martin Fowler's microservices articles: https://martinfowler.com/microservices/
- The Twelve-Factor App: https://12factor.net

---

*Next Chapter: [Chapter 22 — Event-Driven Architecture](22-event-driven.md)*
