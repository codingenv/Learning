# Chapter 40 — Architecting an Event-Driven System

> *"Don't communicate by sharing memory; share memory by communicating."*
> *Adapted: Don't couple services by sharing databases; decouple them by sharing events.*

---

## 1. Problem Statement

Design an **event-driven architecture (EDA)** for a large-scale e-commerce
platform that fully decouples services through asynchronous event communication.

### The Core Challenge

```
  TRADITIONAL (Coupled)                 EVENT-DRIVEN (Decoupled)
  ========================              ============================
  Order ──REST──> Payment              Order ──publish──> [Event Bus]
    |                |                                        |
    |   REST         |  REST              subscribe           |  subscribe
    v                v                       |                |
  Inventory       Shipping              Payment          Inventory
    |                                      |                |
    |   REST                           publish           publish
    v                                      |                |
  Notification                         [Event Bus] <───────+
                                           |
                                       Shipping, Notification, Analytics
```

| Challenge                  | Why It's Hard                                          |
|----------------------------|--------------------------------------------------------|
| Reliable event delivery    | Network failures, broker crashes, consumer downtime    |
| Ordering guarantees        | Events may arrive out-of-order across partitions       |
| Exactly-once processing    | At-least-once + idempotent consumers = effectively once|
| Schema evolution           | Events evolve; old consumers must not break             |
| Observability              | Tracing a request across 10 asynchronous hops          |

### Real-World Examples

- **Amazon** — Order pipeline: millions of events/sec across hundreds of microservices
- **Uber** — Trip lifecycle events (TripRequested → DriverAssigned → TripCompleted)
- **Netflix** — Billions of events/day for analytics and recommendations via Kafka
- **Shopify** — Event-driven webhooks and internal eventing at massive scale

---

## 2. Requirements

### Functional Requirements

| ID   | Requirement             | Description                                        |
|------|-------------------------|----------------------------------------------------|
| FR-1 | Produce events          | Services publish domain events to the event bus    |
| FR-2 | Consume events          | Services subscribe and process events asynchronously|
| FR-3 | Event replay            | Replay events from any point in time (7-day window)|
| FR-4 | Dead letter queue       | Failed events routed to DLQ after N retries        |
| FR-5 | Schema registry         | Validate event schemas on produce and consume      |
| FR-6 | Event filtering/routing | Route events to specific consumers by type/content |
| FR-7 | Event correlation       | Track an event chain from trigger to final effect   |

### Non-Functional Requirements

| ID    | Requirement       | Target                                            |
|-------|-------------------|---------------------------------------------------|
| NFR-1 | Delivery guarantee| At-least-once (with idempotent consumers)         |
| NFR-2 | Event ordering    | Per-entity ordering (same order ID = ordered)     |
| NFR-3 | Latency           | < 100ms end-to-end (p95)                          |
| NFR-4 | Throughput        | 100,000 events/second sustained                   |
| NFR-5 | Retention         | 7-day retention for event replay                  |
| NFR-6 | Availability      | 99.99% uptime for the event backbone              |
| NFR-7 | Durability        | Zero event loss (replicated, persisted to disk)   |

---

## 3. Event Types and Design

### 3.1 Event Categories

```
+------------------------------------------------------------------+
|                     EVENT TAXONOMY                                |
+------------------------------------------------------------------+
|  DOMAIN EVENTS              INTEGRATION EVENTS                   |
|  (Business Facts)           (Cross-Service Contracts)            |
|  - OrderPlaced              - order.payment.requested            |
|  - PaymentCompleted         - inventory.reservation.confirmed    |
|  - ItemShipped              - shipping.label.generated           |
|                                                                   |
|  SYSTEM EVENTS              NOTIFICATION EVENTS                  |
|  (Infrastructure)           (User-Facing Alerts)                 |
|  - ServiceStarted           - EmailSent                          |
|  - HealthCheckFailed        - PushNotificationDelivered          |
|  - CircuitBreakerOpened     - SMSSent                            |
+------------------------------------------------------------------+
```

### 3.2 Event Schema Design (CloudEvents Specification)

```json
{
  "specversion": "1.0",
  "id": "evt-a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "source": "urn:ecommerce:order-service",
  "type": "com.ecommerce.order.OrderPlaced.v1",
  "time": "2024-11-15T10:30:00.000Z",
  "datacontenttype": "application/json",
  "subject": "order-12345",
  "data": {
    "orderId": "order-12345",
    "customerId": "cust-67890",
    "items": [
      { "sku": "WIDGET-001", "quantity": 2, "price": 29.99 }
    ],
    "totalAmount": 59.98,
    "currency": "USD"
  },
  "metadata": {
    "correlationId": "corr-aaaa-bbbb-cccc",
    "causationId": "cmd-place-order-xyz",
    "version": 1,
    "traceId": "trace-1111-2222-3333"
  }
}
```

| Field           | Purpose                                                    |
|-----------------|------------------------------------------------------------|
| `id`            | Globally unique event ID (UUID v4) for deduplication       |
| `source`        | URN identifying the producing service                      |
| `type`          | Fully qualified event type with version suffix             |
| `subject`       | The entity this event is about (for partition routing)     |
| `correlationId` | Links all events in a business transaction together        |
| `causationId`   | The specific command or event that caused THIS event       |

### 3.3 Event Versioning Strategy

```
  v1 (original)          v2 (add field)         v3 (deprecate field)
  +-----------------+    +-----------------+    +--------------------+
  | orderId         |    | orderId         |    | orderId            |
  | customerId      |    | customerId      |    | customerId         |
  | items[]         |    | items[]         |    | items[]            |
  | totalAmount     |    | totalAmount     |    | totalAmount        |
  |                 |    | currency (NEW)  |    | currency           |
  |                 |    | shippingAddr    |    | shippingAddress    |
  +-----------------+    +-----------------+    +--------------------+
  * deprecated fields kept but marked, never removed
```

**Rules:** (1) Add optional fields freely, (2) Never remove fields,
(3) Never rename fields, (4) Never change field types,
(5) Use a Schema Registry (Confluent / AWS Glue) to enforce rules.

### 3.4 Event Naming Conventions

Events are **facts about the past** — always use **past tense**:

```
  CORRECT (facts)               INCORRECT (commands)
  ================================  ============================
  OrderPlaced                       PlaceOrder
  PaymentCompleted                  ProcessPayment
  ItemShipped                       ShipItem
```

**Naming format:** `{Domain}.{Entity}.{Action}` — e.g., `com.ecommerce.order.OrderPlaced`

Include **correlationId** (links entire transaction) and **causationId**
(the specific event/command that caused this one).

---

## 4. Choosing the Event Backbone

### 4.1 Apache Kafka

```
  Producers                   Kafka Cluster                    Consumers
  +---------+                +--------------------+           +-----------+
  | Order   |--publish------>| Topic: orders      |--poll---->| Payment   |
  | Service |                | +--partition 0---+ |           | (group-1) |
  +---------+                | | msg | msg | .. | |           +-----------+
                             | +-----------------+ |           +-----------+
  +---------+                | +--partition 1---+ |           | Inventory |
  | Payment |--publish------>| | msg | msg | .. | |--poll---->| (group-2) |
  | Service |                | +-----------------+ |           +-----------+
  +---------+                +--------------------+           +-----------+
                              Partitioned, immutable,          | Analytics |
                              append-only commit log           | (group-3) |
                                                               +-----------+
```

**When to choose:** High throughput (100K+ events/sec), event replay needed,
stream processing required, strong ordering per partition.

### 4.2 RabbitMQ

```
  Producers                  RabbitMQ Broker                 Consumers
  +---------+               +----------------------+        +-----------+
  | Order   |--publish----->| Exchange (topic)     |        | Payment   |
  | Service |               |   +-> Queue: payment |--ack-->| Service   |
  +---------+               |   +-> Queue: invent. |--ack-->+-----------+
                            |   +-> Queue: notify  |--ack-->| Inventory |
  +---------+               |       (order.*)      |        | Service   |
  | Payment |--publish----->+----------------------+        +-----------+
  | Service |                Exchanges route messages
  +---------+                to queues by binding key
```

**When to choose:** Complex routing logic, priority queues, moderate
throughput (10K events/sec), request-reply patterns.

### 4.3 AWS SNS + SQS

```
  Producers              SNS Topic             SQS Queues        Consumers
  +---------+           +-----------+         +----------+      +---------+
  | Order   |--publish->| SNS Topic |--fan--->| SQS:     |----->| Payment |
  | Service |           | "orders"  |  out    | payment  |      | Lambda  |
  +---------+           |           |   |     +----------+      +---------+
                        |           |   +---->| SQS:     |----->| Invent. |
                        |           |   |     | inventory|      | Lambda  |
                        |           |   +---->| SQS:     |----->+---------+
                        +-----------+         | notify   |      | Notify  |
                                              +----------+      | Lambda  |
                                              | SQS DLQ  |      +---------+
                                              +----------+
```

**When to choose:** AWS-native, serverless architecture, simple fan-out,
minimal operational overhead desired.

### 4.4 Comparison Table

```
+---------------------+------------------+------------------+------------------+
| Dimension           | Apache Kafka     | RabbitMQ         | AWS SNS + SQS   |
+---------------------+------------------+------------------+------------------+
| Throughput          | 1M+ events/sec   | 10-50K events/s  | 100K+ events/sec |
| Ordering            | Per-partition     | Per-queue (FIFO) | FIFO SQS only   |
| Event Replay        | Yes (log-based)  | No (consumed=gone)| No (consumed)   |
| Delivery Guarantee  | At-least-once /  | At-least-once /  | At-least-once   |
|                     | Exactly-once     | At-most-once     | (SQS FIFO: EOS) |
| Latency (p50)       | 2-5ms            | 1-2ms            | 10-50ms         |
| Operational Cost    | High (clusters,  | Medium (simpler  | Near-zero       |
|                     | ZK/KRaft, tuning)| but still hosted)| (fully managed) |
| Dollar Cost         | Medium-High      | Low-Medium       | Pay-per-use     |
| Consumer Groups     | Native           | Via exchanges    | Multiple SQS    |
| Routing Flexibility | Topic + key      | Exchanges: topic,| SNS filter      |
|                     | based            | fanout, headers  | policies        |
| Best For            | High-throughput  | Complex routing  | Serverless AWS  |
|                     | event streaming  | moderate scale   | simple fan-out  |
+---------------------+------------------+------------------+------------------+
```

---

## 5. Event-Driven Patterns — Deep Dive

### 5.1 Event Notification (Fire-and-Forget)

```
  Order Service             Event Bus            Consumers (0..N)
  +---------------+        +-----------+        +------------------+
  | placeOrder()  |        |           |------->| Payment Service  |
  |   +--save DB  |        |           |        +------------------+
  |   +--publish -|------->| OrderPlaced        +------------------+
  |      event    |        |           |------->| Analytics Service|
  +---------------+        +-----------+        +------------------+

  - Producer is UNAWARE of consumers
  - Adding a new consumer requires ZERO changes to producer
```

**Pros:** Maximum decoupling. **Cons:** Event has only a reference (orderId);
consumers must call back for full details, creating runtime coupling.

### 5.2 Event-Carried State Transfer

```
  Order Service               Event Bus               Inventory Service
  +-------------------+      +-----------+           +-------------------+
  | placeOrder()      |      | OrderPlaced           | onOrderPlaced():  |
  |   +--save to DB   |      | {orderId, |           |   save full order |
  |   +--publish with |----->|  customer,|---------->|   to local DB     |
  |      FULL payload |      |  items[], |           |   (no callback!)  |
  +-------------------+      |  total}   |           +-------------------+
                             +-----------+
  Event contains COMPLETE data --> consumer builds LOCAL read model
```

**Pros:** Consumers fully autonomous, no callback, fast local reads.
**Cons:** Larger events; data may be slightly stale (eventual consistency).

### 5.3 Event Sourcing

```
  Event Store (append-only log for Order #12345)
  +---+------+--------------------------------------------+
  | # | Time | Event                                      |
  +---+------+--------------------------------------------+
  | 1 | T1   | OrderCreated  {items: [...], total: 59.98} |
  | 2 | T2   | PaymentAuthorized {authCode: "ABC123"}     |
  | 3 | T3   | InventoryReserved {warehouse: "WH-East"}   |
  | 4 | T4   | OrderConfirmed {}                          |
  | 5 | T5   | ItemShipped {tracking: "1Z999AA10"}        |
  +---+------+--------------------------------------------+
  Current State = Replay(Event 1 -> Event 5)

  SNAPSHOTS (avoid replaying from beginning):
  [E1] [E2] ... [E50]  [SNAPSHOT@50]  [E51] [E52] [E53]
                              |
                              v
                   { Full state at event 50 }
  Rebuild: Load Snapshot@50, then replay E51 + E52 + E53
```

**Pros:** Complete audit trail, time-travel debugging, rebuild any view.
**Cons:** Complex implementation, eventually consistent reads, snapshot overhead.

### 5.4 CQRS with Events

```
  WRITE SIDE (Commands)             READ SIDE (Queries)
  +-------------------+            +-------------------+
  |  Command Handler  |            |  Query Handler    |
  |  (business logic) |            |  (simple lookups) |
  +--------+----------+            +--------+----------+
           |                                ^
           v                                | read
  +-------------------+            +-------------------+
  |  Write Database   |            |  Read Database    |
  |  (normalized,     |            |  (denormalized,   |
  |   source of truth)|            |   query-optimized)|
  +--------+----------+            +-------------------+
           |                                ^
           | publish event                  | update projection
           v                                |
  +-------------------+--------------------+
  |   Event Bus  (OrderPlaced, PaymentDone...)
  +------------------------------------------------+
  Write: PostgreSQL | Read: Elasticsearch + Redis + DynamoDB
```

The read model is **eventually consistent** with the write model. For most
e-commerce queries, a few hundred ms of staleness is acceptable.

### 5.5 Saga Pattern (Choreography vs Orchestration)

#### Choreography (Decentralized)

```
  Order         Payment        Inventory       Shipping
  Service       Service        Service         Service
    |               |              |               |
    |--OrderPlaced->|              |               |
    |           [process]          |               |
    |               |--PaymentOK-->|               |
    |               |          [reserve]           |
    |               |              |--Reserved---->|
    |               |              |           [ship]
    |               |              |               |--Shipped-->

  COMPENSATION (Payment Fails):
    |--OrderPlaced->|              |               |
    |           [process]          |               |
    |               |--PaymentFailed               |
    |<--PaymentFailed              |               |
    |--OrderCancelled (compensating event)         |
```

#### Orchestration (Central Coordinator)

```
  Saga                Payment       Inventory      Shipping
  Orchestrator        Service       Service        Service
    |--ProcessPayment-->|              |              |
    |<--PaymentOK-------|              |              |
    |--ReserveInventory--------------->|              |
    |<--InventoryReserved-------------|              |
    |--ShipOrder-------------------------------------->|
    |<--ItemShipped------------------------------------|
    [Mark order COMPLETED]

  COMPENSATION (Inventory Fails):
    |--ProcessPayment-->|              |              |
    |<--PaymentOK-------|              |              |
    |--ReserveInventory--------------->|              |
    |<--InsufficientStock-------------|              |
    |--RefundPayment--->|              |              |
    |<--PaymentRefunded-|              |              |
    [Mark order CANCELLED]
```

| Factor              | Choreography          | Orchestration          |
|---------------------|-----------------------|------------------------|
| Number of steps     | 2-4 steps             | 5+ steps               |
| Compensation logic  | Simple                | Complex / branching    |
| Team autonomy       | High (each team owns) | Lower (shared orch.)   |
| Visibility          | Harder to trace       | Single view of flow    |
| Recommended for     | Simple workflows      | Complex order flows    |

---

## 6. Ensuring Reliable Event Delivery

### 6.1 The Dual-Write Problem

```
  Order Service attempts TWO writes:

  1. Write to Database       2. Publish to Event Bus
  +------------------+      +------------------+
  | INSERT INTO      |      | publish(         |
  |   orders (...)   |      |   "OrderPlaced") |
  +------------------+      +------------------+

  SCENARIO A: Publish fails after DB write
  [DB Write OK] + [Publish FAIL] --> LOST EVENT (order exists, nobody knows)

  SCENARIO B: DB fails after publish
  [DB FAIL] + [Publish OK] --> PHANTOM EVENT (event sent, order doesn't exist)
```

**Root cause:** Two systems (DB + broker) cannot be updated atomically. Solution below.

### 6.2 Outbox Pattern

```
  Order Service                         Outbox Publisher
  +---------------------------+        +--------------------+
  | BEGIN TRANSACTION         |        | Poll outbox table  |
  |   INSERT INTO orders      |        | every 100ms        |
  |     (id, items, total...) |        |   +--read unsent   |
  |   INSERT INTO outbox      |        |   +--publish to    |
  |     (id, type, payload)   |        |      Kafka         |
  | COMMIT  <-- atomic!       |        |   +--mark as sent  |
  +---------------------------+        +--------------------+
                                              |
  Database                                    v
  +---------------------------+        +----------+
  | orders table | outbox tbl |        | Kafka    |
  |  id | total  | id | type  |        | Topic:   |
  | 123 | 59.98  | a1 | Order |        | orders   |
  +---------------------------+        +----------+
```

**Guarantee:** Business write + outbox write in same transaction. Publisher
retries until event reaches the broker.

### 6.3 Change Data Capture (CDC)

```
  Order Service       Database            Debezium         Kafka
  +-------------+    +-----------+       +----------+    +--------+
  |  write to   |--->| PostgreSQL|       | Debezium |    |        |
  |  orders     |    | (WAL/     |------>| Connector|--->| Topic: |
  |  table      |    |  binlog)  |       | (reads   |    | orders |
  +-------------+    +-----------+       |  WAL)    |    |  .cdc  |
                                         +----------+    +--------+
  No outbox table, no polling, no application code changes!
  1. Service writes to DB normally  2. DB appends to WAL
  3. Debezium reads WAL in real-time  4. Publishes to Kafka
```

### 6.4 Transactional Outbox vs CDC

```
+---------------------+---------------------------+---------------------------+
| Dimension           | Transactional Outbox      | Change Data Capture       |
+---------------------+---------------------------+---------------------------+
| Application changes | Outbox table + publisher  | None (reads DB log)       |
| Delivery guarantee  | At-least-once             | At-least-once             |
| Latency             | Polling interval (50-500ms)| Near real-time (~10ms)   |
| Operational burden  | Low (just a table + cron) | Medium (Debezium cluster) |
| Schema control      | Full (you design events)  | Tied to DB schema         |
| Event granularity   | Custom (domain events)    | Row-level changes         |
| Recommended for     | Custom domain events      | Data replication / legacy |
+---------------------+---------------------------+---------------------------+
```

---

## 7. Handling Failures and Edge Cases

### 7.1 Idempotent Consumers

At-least-once delivery means duplicates **will** happen. Consumers MUST be idempotent.

```
  function handleOrderPlaced(event):
    // Step 1: Check deduplication store
    if (processedEvents.contains(event.id)):
      return ACK  // skip duplicate

    // Step 2: Process + record atomically
    BEGIN TRANSACTION
      processPayment(event.data)
      INSERT INTO processed_events (event.id, now())
    COMMIT
    return ACK

  Deduplication Store (TTL: 7 days):
  +--------------------------------+
  | event_id (PK)  | processed_at |
  | evt-a1b2c3d4   | 2024-11-15   |
  | evt-e5f6g7h8   | 2024-11-15   |
  +--------------------------------+
```

**Strategies:** (1) Deduplication table, (2) Natural idempotency (`SET x = 100`
vs `SET x = x - 10`), (3) Idempotency key in event header for DB upserts.

### 7.2 Dead Letter Queue (DLQ)

```
  Main Topic         Consumer              Retry/DLQ          Ops Team
  +---------+       +-----------+         +---------+        +--------+
  | Event A |------>| Process   |--FAIL-->| Retry 1 | (1s)  |        |
  |         |       |           |<--------|         |        |        |
  |         |       | Process   |--FAIL-->| Retry 2 | (5s)  |        |
  |         |       |           |<--------|         |        |        |
  |         |       | Process   |--FAIL-->| Retry 3 | (30s) |        |
  |         |       |  3x MAX   |         | Move to |        |        |
  |         |       |           |         | DLQ --->|------->| Alert! |
  +---------+       +-----------+         +---------+        | Investigate
                                                             | Fix & Replay
```

Retry with **exponential backoff**. After N failures → DLQ → alert → manual fix.

### 7.3 Poison Pill Messages

A **poison pill** is a malformed event that crashes the consumer on every attempt.

```
  Consumer                    Failure Tracker           Quarantine
  +---------------+          +------------------+      +----------+
  | Receive event |          | event_id | count |      | Poison   |
  |   +--try      |--fail-->| evt-bad  |   1   |      | Queue    |
  |   |           |--fail-->| evt-bad  |   2   |      |          |
  |   |           |--fail-->| evt-bad  |   3   |      |          |
  |   +--quarantine ------>| (>= MAX) |-------+----->| evt-bad  |
  |   +--continue next     +----------+       |      +----------+
  +---------------+                            Alert + investigate
```

### 7.4 Consumer Lag Monitoring

```
  Kafka Partition (Topic: orders, Partition 0)
  Offsets:    0  1  2  3  4  5  6  7  8  9  10 11 12 13 14
             [x][x][x][x][x][x][x][x][x][ ][ ][ ][ ][ ][ ]
                                       ^                    ^
                                Consumer Offset (8)    Latest Offset (14)
  Consumer Lag = 14 - 8 = 6 messages

  +-------------------+------------------+-----------------------+
  | Lag               | Severity         | Action                |
  +-------------------+------------------+-----------------------+
  | < 100             | Normal           | No action             |
  | 100 - 1,000       | Warning          | Investigate           |
  | 1,000 - 10,000    | Critical         | Scale consumers       |
  | > 10,000          | Emergency        | Page on-call engineer |
  +-------------------+------------------+-----------------------+
```

---

## 8. Event Ordering Guarantees

```
  1. PER-PARTITION ORDERING (recommended)
     Partition by entity ID (e.g., order_id):
     Partition 0: [Order-A Created] [Order-A Paid] [Order-A Shipped]  <-- ordered!
     Partition 1: [Order-B Created] [Order-B Paid]                    <-- ordered!
     Same entity = ordered. Cross-entity = may interleave (that's fine).

  2. GLOBAL ORDERING (single partition)
     Partition 0: [A Created] [B Created] [A Paid] [C Created] [B Paid]
     Everything ordered, but ONE consumer max. Limits throughput to ~10K/sec.

  3. CAUSAL ORDERING (correlation + causation IDs)
     OrderPlaced       correlationId: corr-123, causationId: null
       +--> PaymentProcessed  correlationId: corr-123, causationId: evt-OrderPlaced
              +--> InventoryReserved  correlationId: corr-123, causationId: evt-Payment
     Reconstruct causal chain even if events arrive out of order.
```

**Ordering matters for:** Same-entity state transitions, financial transactions,
inventory updates. **Doesn't matter for:** Analytics, notifications, independent entities.

---

## 9. Schema Evolution

```
  BACKWARD COMPATIBLE (new consumer reads old events)
  +------------------+     +------------------+
  | Old Event (v1)   | --> | New Consumer     |
  | { orderId,       |     | Handles missing  |
  |   total }        |     | "currency" field |
  +------------------+     +------------------+

  FORWARD COMPATIBLE (old consumer reads new events)
  +------------------+     +------------------+
  | New Event (v2)   | --> | Old Consumer     |
  | { orderId,       |     | Ignores unknown  |
  |   total,         |     | "currency" field |
  |   currency }     |     +------------------+
  +------------------+

  FULL COMPATIBLE = both directions (safest, recommended for production)
```

**Schema Registry Enforcement:**

```
  Producer          Schema Registry           Kafka
  +--------+       +------------------+      +-------+
  | Event  |------>| Validate v2      |      |       |
  | (v2)   |       | against v1       |      |       |
  |        |       |  Compatible? YES |----->| Store |
  |        |<------| 409 INCOMPATIBLE |      |       |
  +--------+       +------------------+      +-------+
```

| Strategy                      | Example                             |
|-------------------------------|-------------------------------------|
| Add optional fields           | Add `currency` with default `"USD"` |
| Never remove fields           | Deprecated? Keep it, add new field  |
| Never rename fields           | Add new name, populate both         |
| Version in type name          | `OrderPlaced.v1`, `OrderPlaced.v2` |
| Use Avro/Protobuf for schemas | Binary + schema evolution built-in  |

---

## 10. Complete System: E-Commerce Order Pipeline

### 10.1 Full Architecture

```
  E-COMMERCE EVENT-DRIVEN ORDER PIPELINE
  =========================================================================
                            +-------------------+
                            |   API Gateway     |
                            +--------+----------+
                                     |
                            +--------v----------+
                            |  Order Service    |
                            +--------+----------+
                                     | OrderPlaced event
                                     v
  +------------------------------------------------------------------+
  |                      KAFKA EVENT BACKBONE                        |
  |  Topic: order-events       Topic: payment-events                 |
  |  [OrderPlaced]             [PaymentCompleted] [PaymentFailed]    |
  |  [OrderConfirmed]          [RefundIssued]                        |
  |                                                                   |
  |  Topic: inventory-events   Topic: shipping-events                |
  |  [InventoryReserved]       [ShipmentCreated] [ItemShipped]       |
  |  [InventoryReleased]       [ItemDelivered]                       |
  |                                                                   |
  |  Topic: notification-events                                      |
  |  [EmailSent] [PushSent] [SMSSent]                                |
  +------------------------------------------------------------------+
       |            |             |              |             |
       v            v             v              v             v
  +---------+ +-----------+ +-----------+ +-----------+ +-----------+
  | Payment | | Inventory | | Shipping  | | Notify    | | Analytics |
  | Service | | Service   | | Service   | | Service   | | Service   |
  +---------+ +-----------+ +-----------+ +-----------+ +-----------+
       |            |             |              |             |
       v            v             v              v             v
  [ Stripe ] [ Warehouse ] [ FedEx/UPS ] [SendGrid/   ] [Snowflake/
  [  API   ] [  System   ] [   API     ] [ Twilio     ] [ClickHouse]
```

### 10.2 Successful Order Flow (Happy Path)

```
  Time  Service          Event Published           Kafka Topic
  ----- -------          ----------------          -----------
  T1    Order Service    OrderPlaced               order-events
  T2    Payment Service  PaymentCompleted          payment-events
  T3    Inventory Svc    InventoryReserved         inventory-events
  T4    Order Service    OrderConfirmed            order-events
  T5    Shipping Svc     ShipmentCreated           shipping-events
  T6    Notify Service   EmailSent("Confirmed!")   notification-events
  T7    Shipping Svc     ItemShipped               shipping-events
  T8    Notify Service   EmailSent("Shipped!")     notification-events
  T9    Analytics Svc    (consumes ALL events, builds dashboards)

  Total latency (T1 -> T6): ~500ms for order confirmation
```

### 10.3 Failed Payment Flow (Compensation)

```
  Time  Service          Event Published           Action
  ----- -------          ----------------          ------
  T1    Order Service    OrderPlaced               Create order (PENDING)
  T2    Payment Service  PaymentFailed             Card declined!
  T3    Inventory Svc    InventoryReleased         Un-reserve items (compensate)
  T4    Order Service    OrderCancelled            Mark order CANCELLED
  T5    Notify Service   EmailSent("Failed")       Inform customer
  T6    Analytics Svc    (records failure metrics)

  COMPENSATION CHAIN:
  OrderPlaced --> PaymentFailed --> InventoryReleased + OrderCancelled

  +---------------------+---------------------------+
  | Action              | Compensation              |
  +---------------------+---------------------------+
  | PaymentCompleted    | RefundIssued              |
  | InventoryReserved   | InventoryReleased         |
  | ShipmentCreated     | ShipmentCancelled         |
  | OrderConfirmed      | OrderCancelled            |
  +---------------------+---------------------------+
```

### 10.4 Event Flow Diagram (Sequence)

```
  Order      Payment     Inventory    Shipping    Notify     Analytics
  Service    Service     Service      Service     Service    Service
    |           |            |            |           |          |
    |--OrderPlaced---------->|            |           |          |
    |---------->|            |            |           |--------->|
    |      [charge card]     |            |           |          |
    |    PaymentCompleted    |            |           |          |
    |<----------|            |            |           |          |
    |           |----------->|            |           |--------->|
    |           |      [reserve stock]    |           |          |
    |           |   InventoryReserved     |           |          |
    |<--------------------------|         |           |          |
    |           |            |----------->|           |--------->|
    |  [confirm order]       |            |           |          |
    |--OrderConfirmed------->|            |           |          |
    |---------->|            |----------->|---------->|--------->|
    |           |            |       [ship item]      |          |
    |           |            |      ItemShipped       |          |
    |<-----------------------------------------|     |          |
    |           |            |            |---------->|--------->|
    v           v            v            v           v          v
```

---

## 11. Observability for Event-Driven Systems

### 11.1 Distributed Tracing Across Event Boundaries

```
  HTTP Request               Event                    Event
  (traceId: T1)             (traceId: T1)           (traceId: T1)
  API Gateway --> Order Svc --publish--> Payment Svc --publish-->
  spanId: S1     spanId: S2              spanId: S3

  In Jaeger / Datadog:
  +---------------------------------------------------------------+
  | Trace T1                                                      |
  | [S1: API Gateway         ]  12ms                              |
  |   [S2: Order Service     ]  8ms                               |
  |     [S3: Payment Service ]  45ms                              |
  |       [S4: Stripe API    ]  120ms                             |
  |     [S5: Inventory Svc   ]  15ms                              |
  |     [S7: Notification Svc]  22ms                              |
  +---------------------------------------------------------------+
```

**Key:** Propagate `traceId` + `correlationId` through every event — without
this, debugging across async boundaries is impossible.

### 11.2 Essential Dashboards

```
  +---------------------------+  +---------------------------+
  |  Events Published / sec   |  |  Events Consumed / sec    |
  |  12,847 +-----------+    |  |  12,532 +-----------+    |
  |         |  /\  /\   |    |  |         | /\   /\   |    |
  |         | /  \/  \  |    |  |         |/  \ /  \  |    |
  |         +-----------+    |  |         +-----------+    |
  +---------------------------+  +---------------------------+
  +---------------------------+  +---------------------------+
  |  Consumer Lag (messages)  |  |  DLQ Size (events)        |
  |  payment-svc: 12         |  |  payment-dlq: 3           |
  |  inventory-svc: 847  [!] |  |  inventory-dlq: 0         |
  |  shipping-svc: 5         |  |  shipping-dlq: 0          |
  |  analytics-svc: 2,341    |  |  notification-dlq: 1      |
  +---------------------------+  +---------------------------+
  +---------------------------+  +---------------------------+
  |  Processing Latency (p95) |  |  Failed Events (last 1h)  |
  |  payment-svc:   45ms     |  |  PaymentFailed: 47        |
  |  inventory-svc: 12ms     |  |  OutOfStock: 12           |
  |  shipping-svc:  89ms     |  |  SchemaValidation: 0      |
  +---------------------------+  +---------------------------+
```

### 11.3 Event Flow Visualization

```
  EVENT LINEAGE MAP (for correlationId: corr-123)
  =========================================================
  [OrderPlaced]
    +---> [PaymentCompleted] ----+
    +---> [InventoryReserved] ---+--> [OrderConfirmed]
                                        +---> [ShipmentCreated]
                                        |         +---> [ItemShipped]
                                        |                    +---> [ItemDelivered]
                                        +---> [EmailSent: "Order confirmed"]
                                        +---> [EmailSent: "Item shipped"]

  Built by following correlationId + causationId chains.
  Tools: custom UI, or adapt Jaeger trace view for events.
```

---

## 12. Key Takeaways

```
+====================================================================+
|                      KEY TAKEAWAYS                                  |
+====================================================================+

  1. EVENTS ARE FACTS, NOT COMMANDS
     Use past tense (OrderPlaced, not PlaceOrder). Events describe
     what already happened. They are immutable and undeniable.

  2. SOLVE THE DUAL-WRITE PROBLEM FIRST
     Never write to a database AND publish an event separately.
     Use Transactional Outbox or CDC to ensure atomicity.

  3. DESIGN FOR IDEMPOTENCY FROM DAY ONE
     At-least-once delivery is the reality. Every consumer must
     handle duplicate events. Store processed event IDs and check.

  4. CHOOSE YOUR BACKBONE WISELY
     Kafka for high-throughput streaming with replay. RabbitMQ for
     complex routing at moderate scale. SNS+SQS for serverless.

  5. USE SCHEMAS AND A REGISTRY
     Unvalidated events are ticking time bombs. Use a Schema
     Registry to enforce compatibility and catch breaking changes.

  6. CHOREOGRAPHY FOR SIMPLE FLOWS, ORCHESTRATION FOR COMPLEX
     Sagas via choreography for 2-4 steps. Beyond that, use an
     orchestrator for visibility and cleaner compensation logic.

  7. OBSERVABILITY IS NOT OPTIONAL
     Propagate correlationId and traceId through every event.
     Monitor consumer lag, DLQ size, and processing latency.

  8. PLAN FOR SCHEMA EVOLUTION FROM THE START
     Only add optional fields. Never remove or rename. Use versioned
     types. Test compatibility in CI/CD. Future you will be grateful.

+====================================================================+
```

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
