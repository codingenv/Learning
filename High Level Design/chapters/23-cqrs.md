# Chapter 23: CQRS (Command Query Responsibility Segregation)

## Introduction

CQRS (Command Query Responsibility Segregation) is an architectural pattern that separates
the **read** (query) and **write** (command) operations of a system into distinct models.
Instead of using the same data model for both reading and writing, CQRS uses two separate
models optimized for their respective purposes.

The term was coined by **Greg Young** and is an evolution of Bertrand Meyer's
**Command Query Separation** (CQS) principle — which states that a method should either
change state (command) or return data (query), never both.

CQRS extends this principle from the method level to the **architectural level**, creating
separate read and write subsystems.

---

## 23.1 Why CQRS? The Problem with a Single Model

In a traditional CRUD application, the same model is used for both reading and writing:

```
+--------+                  +------------------+              +----------+
| Client | -- Read/Write -> | Single Model     | ----------> | Database |
+--------+                  | (same DTO, same  |              | (single  |
                            |  queries, same   |              |  schema) |
                            |  tables)         |              +----------+
                            +------------------+
```

### This works fine for simple applications. But problems emerge at scale:

**1. Reads and writes have different optimization needs**
- **Writes** need normalization, validation, business logic, ACID guarantees
- **Reads** need denormalization, joins, projections, speed

**2. Read-to-write ratio is often heavily skewed**
- Many systems are 90-99% reads, 1-10% writes
- Scaling both through the same model is wasteful

**3. Complex queries fight complex domain logic**
- Domain models optimized for business invariants make bad query models
- Query models with lots of joins make domain logic unclear

**4. Different storage technologies may be optimal**
- Writes might suit a relational DB (strong consistency, transactions)
- Reads might suit Elasticsearch (full-text search), Redis (cache), or a
  document store (denormalized views)

---

## 23.2 What Is CQRS?

CQRS splits the system into two sides:

```
                    COMMANDS                              QUERIES
                 (Create, Update, Delete)              (Read, Search, List)
                       |                                      |
                       v                                      v
              +------------------+                   +------------------+
              |   COMMAND SIDE   |                   |   QUERY SIDE     |
              |                  |                   |                  |
              | - Validation     |                   | - Denormalized   |
              | - Business Logic |                   |   read models    |
              | - Domain Model   |                   | - Optimized for  |
              | - Write to DB    |                   |   specific views |
              +--------+---------+                   +--------+---------+
                       |                                      ^
                       v                                      |
              +------------------+    Sync Events    +------------------+
              |   WRITE DB       | ----------------> |   READ DB        |
              | (normalized,     |   (projections)   | (denormalized,   |
              |  transactional)  |                   |  fast queries)   |
              +------------------+                   +------------------+
```

### Command Side (Write Model)
- Accepts **commands**: `CreateOrder`, `CancelOrder`, `UpdateShippingAddress`
- Commands represent an **intent** to change state
- Validates commands against business rules
- Executes domain logic (aggregates, entities)
- Persists state changes to the write database
- Publishes events about what changed

### Query Side (Read Model)
- Accepts **queries**: `GetOrderById`, `ListOrdersByCustomer`, `SearchProducts`
- Returns data optimized for display / API responses
- Uses **denormalized** data structures (no complex joins)
- Can use different storage technology (Redis, Elasticsearch, materialized views)
- Updated asynchronously from the write side via events/projections

---

## 23.3 CQRS Architecture in Detail

```
   Client (Web/Mobile/API)
         |              |
    Commands        Queries
         |              |
         v              v
  +-----------+  +-----------+
  | Command   |  | Query     |
  | Handler   |  | Handler   |
  +-----------+  +-----------+
         |              |
         v              |
  +-----------+         |
  | Domain    |         |
  | Model     |         |
  | (Aggregate|         |
  |  Root)    |         |
  +-----------+         |
         |              |
         v              v
  +-----------+  +-----------+
  | Write DB  |  | Read DB   |
  | (Postgres)|  | (Redis /  |
  |           |  |  Elastic) |
  +-----------+  +-----------+
         |              ^
         |   Events/    |
         +-- Projections+
```

### Step-by-Step Flow

**Write Path (Command):**
1. Client sends a command: `POST /orders` with order details
2. Command handler receives and validates the command
3. Domain model (aggregate) applies business rules
4. Changes are persisted to the write database
5. Domain events are published: `OrderCreated`

**Read Path (Query):**
1. Client sends a query: `GET /orders/123`
2. Query handler reads from the read database (already denormalized)
3. Returns data directly — no complex joins or business logic
4. Very fast, often from cache or pre-computed views

**Synchronization:**
1. When the write side publishes `OrderCreated` event
2. A **projection** (event handler) consumes the event
3. The projection updates the read database with the denormalized view
4. Next query reflects the new data

---

## 23.4 CQRS Without Event Sourcing (Simpler Variant)

CQRS does **not** require Event Sourcing. You can implement CQRS with a simple
relational database on the write side.

```
  Command:  CreateOrder
       |
       v
  +---------------+        Events          +------------------+
  | Write DB      | ---"OrderCreated"----> | Projection       |
  | (PostgreSQL)  |                        | Service          |
  | - orders      |                        |                  |
  | - order_items |                        +--------+---------+
  | - customers   |                                 |
  +---------------+                                 v
                                           +------------------+
                                           | Read DB          |
                                           | (Redis / Mongo)  |
                                           | - order_summary  |
                                           |   {orderId,      |
                                           |    customerName, |
                                           |    totalAmount,  |
                                           |    status,       |
                                           |    itemCount}    |
                                           +------------------+
```

### How to Publish Events Without Event Sourcing

**Option 1: Outbox Pattern**
Write an event to an `outbox` table in the same transaction as the data change.
A background process polls the outbox and publishes to the message broker.

```sql
BEGIN TRANSACTION;
  INSERT INTO orders (...) VALUES (...);
  INSERT INTO outbox (event_type, payload) VALUES ('OrderCreated', '{...}');
COMMIT;
-- Background worker polls outbox and publishes to Kafka/RabbitMQ
```

**Option 2: Change Data Capture (CDC)**
Use a tool like Debezium to capture database changes and publish them as events.

**Option 3: Application-level events**
After writing to the DB, publish the event in the application code. (Risk: if the
app crashes between DB write and event publish, the event is lost.)

---

## 23.5 CQRS with Event Sourcing (The Full Pattern)

When combined with Event Sourcing, CQRS reaches its full power. The write side stores
**events** instead of current state. The read side builds materialized views from those events.

```
                        COMMAND SIDE                              QUERY SIDE
                             |                                        |
  Command: PlaceOrder        |     Query: GetOrderDetails             |
             |               |              |                         |
             v               |              v                         |
     +---------------+       |      +-----------------+               |
     |   Command     |       |      |  Query Handler  |               |
     |   Handler     |       |      +---------+-------+               |
     +-------+-------+       |                |                       |
             |               |                v                       |
             v               |      +-----------------+               |
     +---------------+       |      |   READ MODEL    |               |
     |   Aggregate   |       |      |   (Denormalized)|               |
     |   (Order)     |       |      |                 |               |
     +-------+-------+       |      | OrderSummary:   |               |
             |               |      | - orderId       |               |
             | Produces      |      | - customerName  |               |
             | Events        |      | - totalAmount   |               |
             v               |      | - status        |               |
     +---------------+       |      | - itemCount     |               |
     |  EVENT STORE  |       |      +---------+-------+               |
     |               |       |                ^                       |
     | OrderPlaced   |       |                |                       |
     | ItemAdded     |  Events Published      |                       |
     | ItemRemoved   | ------+----------> Projection                  |
     | OrderShipped  |       |           (Event Handler)              |
     +---------------+       |                                        |
```

### How It Works

**1. Command arrives:** `PlaceOrder { customerId, items[] }`

**2. Aggregate processes command:**
```
OrderAggregate.handle(PlaceOrder):
  - Validate: customer exists, items are available
  - Produce event: OrderPlaced { orderId, customerId, items, total }
```

**3. Events stored in event store:**
```
Stream: Order-12345
  [1] OrderPlaced    { customerId: C1, total: 99.99, items: [...] }
  [2] PaymentReceived { paymentId: P1, amount: 99.99 }
  [3] OrderShipped   { trackingNumber: "TRK-789" }
```

**4. Projection builds read model:**
```
On OrderPlaced:
  INSERT INTO order_summary (orderId, customer, total, status)
  VALUES ('12345', 'John', 99.99, 'PLACED');

On OrderShipped:
  UPDATE order_summary SET status = 'SHIPPED', tracking = 'TRK-789'
  WHERE orderId = '12345';
```

**5. Query reads from read model:**
```sql
SELECT * FROM order_summary WHERE orderId = '12345';
-- Returns: { orderId: 12345, customer: John, total: 99.99, status: SHIPPED, tracking: TRK-789 }
```

---

## 23.6 Eventual Consistency

In CQRS, the read model is updated **asynchronously** from the write model.
This means there is a **time window** where the read model does not yet reflect
the latest write. This is **eventual consistency**.

```
Timeline:
  T0: Client sends PlaceOrder command
  T1: Write side persists OrderPlaced event  (write DB updated)
  T2: Event published to message bus
  T3: Projection consumes event, updates read DB  (read DB updated)
  
  Between T1 and T3: Read model is STALE (doesn't show new order)
  After T3: Read model is CONSISTENT
  
  Typical lag: 10ms - 500ms (depends on infrastructure)
```

### Handling Eventual Consistency in the UI

**Strategy 1: Optimistic UI**
After the user creates an order, show a success message and optimistically display
the new order in the UI — without waiting for the read model to update.

**Strategy 2: Read-your-own-writes**
After a write, query the write model (or a special "recent writes" cache) for a
short period to guarantee the user sees their own changes.

**Strategy 3: Polling / WebSocket**
The client polls for the updated read model or subscribes to real-time updates
via WebSocket.

**Strategy 4: Version-based queries**
The write side returns a version number. The read side rejects queries for data
older than that version, retrying until the projection catches up.

```
Write response:  { orderId: 123, version: 5 }
Query request:   GET /orders/123?minVersion=5
Read side:       Current version is 4 → wait/retry → version 5 arrives → return data
```

---

## 23.7 When to Use CQRS

### Good Fit

| Scenario                                  | Why CQRS Helps                                        |
|-------------------------------------------|--------------------------------------------------------|
| **High read-to-write ratio**              | Scale read side independently (add replicas, caches)   |
| **Complex domain with rich business logic** | Write model stays clean; read model stays simple     |
| **Different query patterns**              | Build multiple read models for different consumers     |
| **Need different storage technologies**   | Write: Postgres. Read: Elasticsearch + Redis           |
| **Event Sourcing**                        | CQRS is the natural complement to Event Sourcing       |
| **Collaborative domains**                 | Multiple users editing; read views per user role        |
| **Reporting / Analytics**                 | Read models can be pre-aggregated for dashboards        |

### Example: E-Commerce Product Catalog

```
Write Model (Admin):                    Read Model (Customer-facing):
+------------------+                    +------------------------+
| products         |                    | product_catalog_view   |
|   id             |   Events           |   id                   |
|   name           |  -------->         |   name                 |
|   description    |                    |   description          |
|   price          |                    |   price                |
|   category_id    |                    |   category_name        |
|   brand_id       |                    |   brand_name           |
+------------------+                    |   avg_rating           |
| categories       |                    |   review_count         |
|   id, name       |                    |   in_stock             |
+------------------+                    |   image_urls[]         |
| brands           |                    |   related_products[]   |
|   id, name       |                    +------------------------+
+------------------+
| reviews          |                    Read Model (Search):
|   id, product_id |                    +------------------------+
|   rating, text   |                    | Elasticsearch Index    |
+------------------+                    |   name, description,   |
                                        |   category, brand,     |
                                        |   price_range,         |
                                        |   tags[], facets       |
                                        +------------------------+

                                        Read Model (Recommendations):
                                        +------------------------+
                                        | Redis / Graph DB       |
                                        |   product_id,          |
                                        |   frequently_bought,   |
                                        |   similar_products,    |
                                        |   category_trending    |
                                        +------------------------+
```

One write model → **three different read models**, each optimized for its consumer.

---

## 23.8 When NOT to Use CQRS

CQRS adds significant complexity. **Don't use it when:**

| Scenario                               | Why CQRS Is Overkill                                  |
|----------------------------------------|--------------------------------------------------------|
| **Simple CRUD application**            | A single model with REST endpoints works fine          |
| **Low traffic**                        | No need to separately scale reads and writes           |
| **Simple domain logic**                | No benefit from separating read/write models           |
| **Small team**                         | Operational overhead of two models isn't justified      |
| **Strong consistency required**        | Eventual consistency is unacceptable for the use case  |
| **Rapid prototyping / MVP**            | Ship fast first, add CQRS later if needed              |

> **Rule of Thumb:** If your system is primarily CRUD with simple queries,
> CQRS will add complexity without proportional benefit. Apply CQRS **selectively**
> to the parts of your system that need it, not everywhere.

---

## 23.9 Implementation Considerations

### Event Versioning

Over time, event schemas evolve. Old events in the event store don't match new schemas.

**Strategies:**
1. **Weak schema**: Use JSON with tolerant readers (ignore unknown fields)
2. **Upcasting**: Transform old events to new format at read time
3. **Versioned events**: `OrderPlacedV1`, `OrderPlacedV2` as separate types
4. **Schema registry**: Enforce compatibility rules (Avro + Confluent Schema Registry)

```
Version 1 Event:
  OrderPlaced { orderId, items[], total }

Version 2 Event (added shippingAddress):
  OrderPlaced { orderId, items[], total, shippingAddress }

Upcaster:
  If event version == 1:
    Set shippingAddress = null  (or look up from customer profile)
```

### Projection Rebuilding

If a projection has a bug, or you need a new read model, you can **replay all events**
from the event store to rebuild the projection from scratch.

```
Event Store:
  [1] OrderPlaced
  [2] PaymentReceived
  [3] OrderShipped
  [4] OrderPlaced  (another order)
  ...
  [1,000,000] ...

Rebuild:
  1. Drop the read model table/index
  2. Replay all events through the projection
  3. Read model is rebuilt with correct logic
  
  Time: minutes to hours depending on event count
```

**Optimization:**
- Use snapshots to speed up replay
- Run rebuilds in parallel per aggregate/partition
- Build new projections alongside old ones (blue-green projections)

### Multiple Read Models

One of CQRS's superpowers is supporting **multiple read models** from the same events:

```
Events from Write Side:
         |
         +---> Projection A: SQL DB (for API queries)
         |
         +---> Projection B: Elasticsearch (for search)
         |
         +---> Projection C: Redis (for caching)
         |
         +---> Projection D: Data Warehouse (for analytics)
```

Each projection is an independent consumer of the event stream.

---

## 23.10 CQRS Frameworks and Tools

| Technology                  | Language  | Notes                                    |
|-----------------------------|-----------|------------------------------------------|
| **Axon Framework**          | Java      | Full CQRS + Event Sourcing framework     |
| **EventStoreDB**            | Any       | Purpose-built event store with projections|
| **Marten**                  | .NET      | Event sourcing + document DB (Postgres)  |
| **Eventuous**               | .NET      | Lightweight CQRS/ES library              |
| **Commanded**               | Elixir    | CQRS/ES framework for Elixir            |
| **Apache Kafka + KStreams** | Any       | Event streaming with materialized views  |

---

## 23.11 Real-World Examples

### Banking / Financial Systems
- **Write side**: Process deposits, withdrawals, transfers with strict business rules
- **Read side**: Account balance views, transaction history, monthly statements
- Event sourcing provides a complete, auditable ledger of all financial transactions

### E-Commerce (Amazon-scale)
- **Write side**: Order creation, inventory updates, pricing changes
- **Read side**: Product catalog (Elasticsearch), recommendation engine, order tracking
- Different teams own different read models

### Social Media (Twitter/X)
- **Write side**: Post a tweet (validate, store)
- **Read side**: Timeline service (pre-computed, denormalized feeds per user)
- Massive read-to-write ratio (millions of reads per tweet)

### Healthcare
- **Write side**: Patient encounters, prescriptions, lab orders
- **Read side**: Patient summary views, clinical dashboards, billing reports
- Event sourcing provides complete medical history audit trail

---

## 23.12 CQRS Decision Framework

```
                        Start Here
                            |
                            v
                  Is it a simple CRUD app?
                   /                  \
                 YES                   NO
                  |                     |
                  v                     v
            Don't use CQRS      Do reads and writes have
                                very different needs?
                                 /                \
                               YES                 NO
                                |                   |
                                v                   v
                        Do you need         Consider simpler
                        multiple read       alternatives first
                        models?             (caching, indexes)
                         /        \
                       YES         NO
                        |           |
                        v           v
                  CQRS + Event   CQRS without
                  Sourcing       Event Sourcing
                  (full power)   (simpler variant)
```

---

## 23.13 Key Takeaways

1. **CQRS separates the read model from the write model**, allowing each to be
   optimized, scaled, and evolved independently.

2. **The write side** handles commands, validates business rules, and persists state.
   **The read side** serves queries from denormalized, pre-computed views.

3. **CQRS without Event Sourcing** is a simpler variant — just separate your read/write
   databases and sync them via events (Outbox Pattern, CDC).

4. **CQRS with Event Sourcing** is the full pattern — events are the source of truth,
   and projections build read models from those events.

5. **Eventual consistency** between read and write sides is inherent. Use optimistic UI,
   read-your-own-writes, or polling to handle the lag.

6. **Multiple read models** from the same event stream is one of CQRS's greatest strengths.
   Different consumers get data in the format they need.

7. **Projection rebuilding** from the event store lets you fix bugs in read models
   or create entirely new views without changing the write side.

8. **CQRS adds complexity.** Don't apply it to simple CRUD systems. Use it selectively
   for the parts of your system with high read/write asymmetry or complex domain logic.

9. **Event versioning** and **schema evolution** must be planned from the start to
   avoid pain as the system evolves.

10. **Real-world CQRS** powers banking ledgers, e-commerce catalogs, social media
    timelines, and healthcare records — anywhere reads and writes have fundamentally
    different requirements.

---

## Further Reading

- *Implementing Domain-Driven Design* by Vaughn Vernon (Ch. 4 on CQRS)
- Greg Young's CQRS Documents: https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf
- Martin Fowler: "CQRS" — https://martinfowler.com/bliki/CQRS.html
- Microsoft: CQRS Pattern — https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs

---

*Previous: [Chapter 22 — Event-Driven Architecture](22-event-driven.md)*
*Next: [Chapter 24 — Distributed Transactions](24-distributed-transactions.md)*
