# Chapter 22: Event-Driven Architecture

## Introduction

Event-Driven Architecture (EDA) is a software design paradigm where the flow of the program
is determined by **events** — significant changes in state. Instead of services directly
calling each other, they communicate by producing and consuming events.

In an event-driven system, events are **first-class citizens**. An event represents something
that **happened** in the past: "OrderPlaced", "PaymentProcessed", "UserRegistered". Services
react to events they care about, creating a loosely coupled, highly scalable architecture.

EDA is foundational to modern distributed systems, powering real-time analytics, microservices
communication, IoT processing, and financial trading platforms.

---

## 22.1 What Is an Event?

An event is an **immutable record** of something that happened at a specific point in time.

```json
{
  "eventId": "evt-a1b2c3d4",
  "eventType": "OrderPlaced",
  "timestamp": "2024-01-15T10:30:00Z",
  "source": "order-service",
  "data": {
    "orderId": "ord-12345",
    "customerId": "cust-67890",
    "items": [
      { "productId": "prod-111", "quantity": 2, "price": 29.99 },
      { "productId": "prod-222", "quantity": 1, "price": 49.99 }
    ],
    "totalAmount": 109.97
  },
  "metadata": {
    "correlationId": "corr-xyz",
    "userId": "user-abc"
  }
}
```

### Key Properties of Events
- **Immutable**: Once created, events never change
- **Past tense**: Events describe what happened (OrderPlaced, not PlaceOrder)
- **Self-describing**: Contain enough context for consumers to process them
- **Timestamped**: Record when the event occurred

---

## 22.2 Event Types

### Domain Events
Events that originate from the core business domain. They represent state transitions
within a bounded context.

```
Examples:
  - OrderPlaced
  - PaymentAuthorized
  - InventoryReserved
  - ShipmentDispatched
```

### Integration Events
Events published **between bounded contexts** or services. They cross service boundaries
and form the public contract between systems.

```
Order Context  --"OrderPlaced"--> Payment Context
                                  Inventory Context
                                  Notification Context
```

**Best Practice:** Integration events should contain only the data needed by consumers,
not the entire internal domain model.

### Notification Events
Lightweight events that signal something happened but carry **minimal data**.
Consumers must call back to the source service for details.

```json
{
  "eventType": "OrderPlaced",
  "orderId": "ord-12345"
}
// Consumer calls: GET /orders/ord-12345 for full details
```

---

## 22.3 Core Components of EDA

```
+----------------+     +-------------------+     +------------------+
|    EVENT       |     |   EVENT CHANNEL   |     |    EVENT         |
|   PRODUCERS    |---->|   (Broker/Bus)    |---->|   CONSUMERS      |
|                |     |                   |     |                  |
| - Order Svc    |     | - Kafka Topic     |     | - Inventory Svc  |
| - Payment Svc  |     | - RabbitMQ Queue  |     | - Analytics Svc  |
| - User Svc     |     | - SNS Topic       |     | - Notification   |
+----------------+     +-------------------+     +------------------+
```

### Event Producers
Services that detect state changes and publish events. A producer does not know
or care who consumes its events.

### Event Channels
The messaging infrastructure that transports events. This can be:
- **Message Queues** (point-to-point): RabbitMQ, SQS
- **Event Streams** (pub-sub with retention): Kafka, Kinesis, Pulsar
- **Event Buses** (fan-out): EventBridge, SNS

### Event Consumers
Services that subscribe to events and react to them. A consumer processes events
independently and at its own pace.

---

## 22.4 EDA Patterns

### Pattern 1: Event Notification

The simplest form of EDA. A service publishes a lightweight notification that
something happened. Consumers may call back for details.

```
Order Service                           Inventory Service
     |                                        |
     |--"OrderPlaced {orderId: 123}"--------->|
     |                                        |
     |        GET /orders/123                 |
     |<---------------------------------------|
     |        {full order details}            |
     |--------------------------------------->|
```

**Pros:**
- Simple to implement
- Events stay small
- Source of truth remains in the producer

**Cons:**
- Consumers must make additional calls (coupling)
- Source service must handle callback load
- Higher overall network traffic

### Pattern 2: Event-Carried State Transfer

Events carry **all the data** consumers need. No callback required.

```
Order Service publishes:
{
  "eventType": "OrderPlaced",
  "orderId": "ord-123",
  "customerId": "cust-456",
  "items": [...],
  "shippingAddress": {...},
  "totalAmount": 109.97
}

Inventory Service has everything it needs — no callback.
```

**Pros:**
- Consumers are fully decoupled — no callbacks needed
- Lower latency (no additional network calls)
- Consumers can build local caches/copies of data

**Cons:**
- Events are larger
- Data duplication across services
- Risk of stale data if events are missed

### Pattern 3: Event Sourcing

Instead of storing the **current state** of an entity, you store the **sequence of
events** that led to the current state. The current state is derived by replaying events.

```
Traditional Approach (State-Based):
  Account table:  { id: A1, balance: 150 }

Event Sourcing Approach:
  Event Store:
    1. AccountCreated  { id: A1, initialBalance: 0 }
    2. MoneyDeposited  { id: A1, amount: 200 }
    3. MoneyWithdrawn  { id: A1, amount: 50 }
    
  Current State (derived): balance = 0 + 200 - 50 = 150
```

#### How Event Sourcing Works

```
+--------+    Command     +-----------+    Events    +-------------+
| Client | ------------> | Aggregate | ----------> | Event Store  |
+--------+               +-----------+              +------+------+
                              ^                            |
                              |     Replay events          |
                              +----------------------------+
                                                           |
                                                           | Publish
                                                           v
                                                   +---------------+
                                                   | Event Bus     |
                                                   | (for consumers)|
                                                   +---------------+
```

#### Event Store
A specialized database optimized for appending events and reading event streams.
Examples: EventStoreDB, Axon Server, or even Kafka used as an event store.

```
+------------------------------------------------------------------+
|                        EVENT STORE                                |
|------------------------------------------------------------------|
| Stream: Account-A1                                               |
|------------------------------------------------------------------|
| Seq | Event Type       | Data                    | Timestamp     |
|-----|------------------|-------------------------|---------------|
|  1  | AccountCreated   | {balance: 0}            | 2024-01-01    |
|  2  | MoneyDeposited   | {amount: 200}           | 2024-01-02    |
|  3  | MoneyWithdrawn   | {amount: 50}            | 2024-01-03    |
|  4  | MoneyDeposited   | {amount: 100}           | 2024-01-05    |
+------------------------------------------------------------------+
```

#### Replaying Events
To get the current state, replay all events for an entity from the beginning.
As event counts grow, this becomes slow. The solution: **snapshots**.

#### Snapshots
Periodically save the current state as a snapshot. When rebuilding, start from
the latest snapshot instead of the very first event.

```
Events:  1 -> 2 -> 3 -> ... -> 999 -> [SNAPSHOT at event 1000] -> 1001 -> 1002

To rebuild state: Load snapshot (event 1000) + replay events 1001, 1002
Instead of:       Replay all 1002 events from scratch
```

#### Benefits of Event Sourcing
- **Complete audit trail**: Every change is recorded forever
- **Temporal queries**: "What was the state at 3pm last Tuesday?"
- **Debug-friendly**: Replay events to reproduce bugs
- **Event replay**: Rebuild read models or fix bugs by replaying from a point in time

#### Challenges of Event Sourcing
- **Event schema evolution**: What happens when event structure changes?
  - Use versioning: `OrderPlacedV1`, `OrderPlacedV2`
  - Use upcasters to transform old events to new format
- **Storage growth**: Events accumulate forever. Snapshots help but storage still grows.
- **Complexity**: Developers must think in events, not state mutations
- **Eventual consistency**: Read models may lag behind the write side

---

## 22.5 Choreography vs Orchestration

When multiple services must collaborate (e.g., processing an order), there are two
approaches to coordinate them.

### Choreography

Each service listens for events and reacts independently. There is **no central
coordinator**. Services emit events that trigger other services.

```
Order         Payment       Inventory     Shipping      Notification
Service       Service       Service       Service       Service
  |               |             |             |              |
  |--OrderPlaced->|             |             |              |
  |               |--PaymentOK->|             |              |
  |               |             |--Reserved-->|              |
  |               |             |             |--Shipped---->|
  |               |             |             |              |--EmailSent
```

**Pros:**
- Loose coupling — services don't know about each other
- Easy to add new consumers
- No single point of failure (no coordinator)
- Each service evolves independently

**Cons:**
- Hard to understand the overall flow (no single place to see it)
- Difficult to monitor and debug
- Risk of cyclic event dependencies
- Hard to implement complex business logic with conditions

### Orchestration

A **central orchestrator** (coordinator) tells each service what to do and when.
It manages the entire workflow.

```
                   +-----------------+
                   |  Order Saga     |
                   |  Orchestrator   |
                   +--------+--------+
                            |
          +-----------------+------------------+
          |                 |                  |
  1. Create Order   2. Process Payment  3. Reserve Inventory
          |                 |                  |
          v                 v                  v
    +----------+     +-----------+     +------------+
    |  Order   |     |  Payment  |     | Inventory  |
    |  Service |     |  Service  |     |  Service   |
    +----------+     +-----------+     +------------+
                            |
                     4. Ship Order
                            |
                            v
                     +------------+
                     | Shipping   |
                     | Service    |
                     +------------+
```

**Pros:**
- Clear, visible workflow — easy to understand
- Centralized monitoring and error handling
- Complex conditional logic is straightforward
- Easier to implement sagas with compensating actions

**Cons:**
- Orchestrator becomes a coupling point
- Risk of putting too much logic in the orchestrator
- Orchestrator is a potential single point of failure
- Tighter coupling between orchestrator and services

### Comparison Table

| Aspect                | Choreography                      | Orchestration                     |
|-----------------------|-----------------------------------|-----------------------------------|
| **Coupling**          | Very loose                        | Moderate (via orchestrator)       |
| **Visibility**        | Low (distributed flow)            | High (centralized flow)           |
| **Complexity**        | Complex at scale (event chains)   | Complex orchestrator logic        |
| **Failure Handling**  | Hard to coordinate                | Centralized compensation          |
| **Adding Services**   | Easy (just subscribe)             | Requires orchestrator changes     |
| **Debugging**         | Difficult                         | Easier (single log source)        |
| **Best For**          | Simple event flows                | Complex multi-step workflows      |
| **Examples**          | Event-driven microservices        | Order processing sagas            |

> **In Practice:** Many systems use a **hybrid approach** — choreography for simple
> event flows and orchestration for complex business processes.

---

## 22.6 Event Bus / Event Broker Technologies

### Apache Kafka

The most popular event streaming platform.

```
+------------+     +-------------------------------------------+     +-------------+
|  Producer  |---->|              KAFKA CLUSTER                 |---->|  Consumer   |
+------------+     |                                           |     |  Group A    |
                   |  Topic: "orders"                          |     +-------------+
+------------+     |  +----------+ +----------+ +----------+   |     +-------------+
|  Producer  |---->|  |Partition0| |Partition1| |Partition2|   |---->|  Consumer   |
+------------+     |  | msg1,4,7 | | msg2,5,8 | | msg3,6,9 |   |     |  Group B    |
                   |  +----------+ +----------+ +----------+   |     +-------------+
                   +-------------------------------------------+
```

**Key Features:**
- Durable, ordered, partitioned event streams
- High throughput (millions of events/sec)
- Consumer groups for parallel processing
- Configurable retention (days, weeks, forever)
- Exactly-once semantics (with transactions)

### Amazon EventBridge

A serverless event bus from AWS.

**Key Features:**
- Schema registry for event discovery
- Content-based filtering (rules engine)
- Integrates with 100+ AWS services
- Pay-per-event pricing

### NATS

A lightweight, high-performance messaging system.

**Key Features:**
- Extremely fast (low latency)
- Simple pub-sub and request-reply
- JetStream for persistence and exactly-once delivery
- Ideal for edge computing and IoT

| Feature            | Kafka                  | EventBridge            | NATS                |
|--------------------|------------------------|------------------------|---------------------|
| **Type**           | Event stream           | Event bus              | Message system      |
| **Ordering**       | Per partition          | Best-effort            | Per subject         |
| **Retention**      | Configurable (days+)   | None (pass-through)    | JetStream: config.  |
| **Throughput**     | Very high              | Moderate               | Very high           |
| **Hosting**        | Self-hosted / managed  | Fully managed (AWS)    | Self-hosted / cloud |
| **Best For**       | Stream processing      | AWS event routing      | Lightweight pub-sub |

---

## 22.7 Idempotent Event Consumers

In distributed systems, events may be delivered **more than once** due to retries,
network issues, or consumer crashes. Consumers must be **idempotent** — processing
the same event multiple times produces the same result.

### Strategies for Idempotency

**1. Idempotency Key / Event ID Tracking**
```
Consumer receives event:
  1. Check if event_id exists in processed_events table
  2. If yes → skip (already processed)
  3. If no → process event, store event_id in processed_events table
```

**2. Natural Idempotency**
Design operations to be naturally idempotent:
```
Instead of:  balance = balance + 100  (NOT idempotent)
Use:         SET balance = 250         (idempotent — same result if repeated)
```

**3. Conditional Updates**
```sql
UPDATE inventory
SET quantity = quantity - 1
WHERE product_id = 'P1' AND version = 5;
-- Only succeeds if version matches; a retry won't double-decrement
```

---

## 22.8 Event Ordering and Exactly-Once Processing

### Event Ordering

Many systems require events to be processed **in order** (e.g., account debits and
credits). Strategies:

1. **Partition by entity key**: In Kafka, use the entity ID (e.g., orderId) as the
   partition key. All events for the same entity go to the same partition, ensuring order.

```
orderId: A --> Partition 0 (ordered within partition)
orderId: B --> Partition 1
orderId: C --> Partition 0
```

2. **Sequence numbers**: Include a sequence number in events. Consumers detect gaps
   or out-of-order delivery and reorder.

3. **Single partition**: Use a single partition (sacrifices parallelism for strict order).

### Exactly-Once Processing

Truly "exactly once" is theoretically impossible in distributed systems, but we can
achieve **effectively exactly once** through:

1. **At-least-once delivery + Idempotent consumers** = effectively exactly-once
2. **Kafka transactions**: Produce and consume in a transactional context
3. **Deduplication**: Track processed message IDs to discard duplicates

---

## 22.9 CQRS + Event Sourcing (Brief Preview)

Event-Driven Architecture naturally leads to the **CQRS** pattern:

```
                Commands                              Queries
                   |                                     |
                   v                                     v
           +---------------+                    +-----------------+
           |  Write Model  |                    |  Read Model     |
           | (Event Source) |                    | (Denormalized)  |
           +-------+-------+                    +--------+--------+
                   |                                     ^
                   |          Events                     |
                   +------------------------------------>+
                              (Projections update
                               the read model)
```

- **Write side**: Accepts commands, validates, produces events → stored in event store
- **Read side**: Consumes events, builds denormalized views optimized for queries
- **Separation**: Each side can scale, optimize, and use different storage independently

> This pattern is covered in depth in **Chapter 23: CQRS**.

---

## 22.10 Real-World Event-Driven Architectures

### Amazon

Amazon's entire order processing pipeline is event-driven:
- **Order Placed** → triggers payment processing, fraud detection, inventory reservation
- **Payment Confirmed** → triggers fulfillment, warehouse picking
- **Item Shipped** → triggers tracking, notification, delivery estimation
- Uses **Amazon EventBridge** and **SQS/SNS** extensively

Each team owns their events and consumers. New teams can subscribe to existing event
streams without modifying producers.

### LinkedIn

LinkedIn uses **Apache Kafka** (which they created) as the central nervous system:

```
+------------+     +--------+     +----------------+
| User       |     |        |     | Search Index   |
| Activity   |---->| KAFKA  |---->| (Elasticsearch)|
+------------+     |        |     +----------------+
                   |        |     +----------------+
+------------+     |        |---->| Recommendations|
| Profile    |---->|        |     | Engine         |
| Updates    |     |        |     +----------------+
+------------+     |        |     +----------------+
                   |        |---->| Analytics      |
+------------+     |        |     | (Hadoop)       |
| Messaging  |---->|        |     +----------------+
+------------+     +--------+     +----------------+
                                  | Notification   |
                                  | Service        |
                                  +----------------+
```

LinkedIn processes **7 trillion messages per day** through Kafka, powering:
- News feed ranking
- People You May Know recommendations
- Search index updates
- Analytics and reporting
- Email and push notifications

### Uber

Uber's trip lifecycle is entirely event-driven:
- **RideRequested** → match with drivers
- **DriverAssigned** → notify rider, start ETA
- **TripStarted** → begin fare calculation, tracking
- **TripCompleted** → process payment, request rating
- Uses Apache Kafka for event streaming

---

## 22.11 Best Practices for Event-Driven Architecture

1. **Design events as contracts**: Events are your public API. Version them carefully.

2. **Use schema registries**: Enforce event schemas (Confluent Schema Registry, AWS
   Glue Schema Registry) to prevent breaking changes.

3. **Keep events small**: Include only what consumers need. Use references (IDs)
   for large data.

4. **Make consumers idempotent**: Always. No exceptions.

5. **Use dead-letter queues (DLQ)**: Route failed events to a DLQ for investigation
   instead of blocking the consumer.

6. **Monitor event lag**: Track how far behind consumers are from producers. Alert
   if lag grows.

7. **Design for failure**: Events will be late, duplicated, or out of order.
   Build resilience into consumers.

8. **Avoid event chains that are too long**: If Event A triggers B triggers C triggers
   D triggers E, debugging becomes extremely difficult.

```
Good:  OrderPlaced --> [3 independent consumers]
Bad:   A --> B --> C --> D --> E --> F --> G (long chain, fragile)
```

---

## 22.12 Key Takeaways

1. **Event-Driven Architecture** decouples services through asynchronous events,
   enabling scalability, resilience, and loose coupling.

2. **Events are immutable facts** about what happened. They are first-class citizens
   in EDA.

3. **Three core patterns**: Event Notification (lightweight signals), Event-Carried
   State Transfer (full data in events), and Event Sourcing (state as event sequence).

4. **Event Sourcing** stores all state changes as events, providing a complete audit
   trail and the ability to replay/rebuild state.

5. **Choreography** (decentralized, event-reactive) works for simple flows;
   **Orchestration** (centralized coordinator) works for complex business processes.

6. **Idempotency is mandatory**: Consumers must handle duplicate events gracefully.

7. **Kafka** is the dominant event streaming platform for high-throughput use cases.

8. **Event ordering** can be guaranteed per partition key. Use entity IDs as
   partition keys.

9. **Schema evolution** is a critical concern — use schema registries and versioning
   strategies from day one.

10. **CQRS + Event Sourcing** is a powerful combination where the write side produces
    events and the read side builds optimized query models from those events.

---

## Further Reading

- *Designing Event-Driven Systems* by Ben Stopford (free from Confluent)
- *Building Event-Driven Microservices* by Adam Bellemare
- Martin Fowler: "What do you mean by Event-Driven?"
- Confluent Kafka documentation: https://docs.confluent.io

---

*Previous: [Chapter 21 — Microservices Architecture](21-microservices.md)*
*Next: [Chapter 23 — CQRS](23-cqrs.md)*
