# Chapter 24: Distributed Transactions

## Introduction

In a monolithic application with a single database, transactions are straightforward:
begin a transaction, perform multiple operations, commit or rollback. The database
guarantees **ACID** properties (Atomicity, Consistency, Isolation, Durability).

But in a distributed system — microservices with separate databases — a single business
operation may span **multiple services and databases**. How do you ensure that either
**all** services complete their part, or **none** do?

This is the problem of **distributed transactions**, and it is one of the hardest
challenges in distributed systems design.

---

## 24.1 The Problem

Consider an e-commerce order workflow:

```
Place Order (single business operation):
  1. Order Service    → Create order record
  2. Payment Service  → Charge customer's credit card
  3. Inventory Service → Reserve items in warehouse
  4. Shipping Service  → Schedule delivery

What if step 2 succeeds but step 3 fails (items out of stock)?
  - The customer was charged but won't receive their items!
  - We need to UNDO the payment (refund)
  
What if step 3 succeeds but step 4 fails?
  - Items are reserved but never shipped
  - We need to UNDO the reservation (release inventory)
```

In a monolith with one database, you'd wrap all this in a single transaction:
```sql
BEGIN TRANSACTION;
  INSERT INTO orders ...;
  INSERT INTO payments ...;
  UPDATE inventory SET ...;
  INSERT INTO shipments ...;
COMMIT;  -- All succeed, or all fail
```

In microservices, **this is not possible**. Each service has its own database.
There is no shared transaction coordinator.

---

## 24.2 Why Distributed Transactions Are Hard

### Network Partitions
Services communicate over the network. The network can fail at any point:
- A request may be sent but never received
- A response may be lost (did it succeed or fail?)
- A service may be down when you need it

### Partial Failures
In a distributed system, **some operations may succeed while others fail**.
Unlike a local transaction where everything rolls back atomically, you must
explicitly handle partial success.

### No Global Clock
Services don't share a clock. You can't establish a global ordering of events
without additional coordination.

### CAP Theorem Constraints
You can only have two of three: Consistency, Availability, Partition Tolerance.
In practice, partitions happen, so you choose between consistency and availability.

---

## 24.3 Two-Phase Commit (2PC)

The **Two-Phase Commit** protocol is the classic approach to distributed transactions.
A **coordinator** (transaction manager) orchestrates the commit across multiple
participants (services/databases).

### Phase 1: Prepare (Vote)

The coordinator asks all participants: "Can you commit?"

```
+-------------+       "Prepare"       +----------------+
| Coordinator | --------------------> | Participant A  |
|             |       "Prepare"       | (Order DB)     |
|             | --------------------> +----------------+
|             |       "Prepare"       +----------------+
|             | --------------------> | Participant B  |
|             |                       | (Payment DB)   |
|             |                       +----------------+
|             |                       +----------------+
|             |                       | Participant C  |
|             |                       | (Inventory DB) |
+-------------+                       +----------------+

Each participant:
  1. Executes the transaction locally (but doesn't commit)
  2. Writes to a durable log (WAL)
  3. Responds: "YES, I can commit" or "NO, I cannot"
```

### Phase 2: Commit (or Abort)

If **all** participants voted YES → Coordinator sends "COMMIT"
If **any** participant voted NO → Coordinator sends "ABORT"

```
All voted YES:
+-------------+       "COMMIT"       +----------------+
| Coordinator | --------------------> | Participant A  | --> COMMIT locally
|             | --------------------> | Participant B  | --> COMMIT locally
|             | --------------------> | Participant C  | --> COMMIT locally
+-------------+                       +----------------+

Any voted NO:
+-------------+       "ABORT"        +----------------+
| Coordinator | --------------------> | Participant A  | --> ROLLBACK locally
|             | --------------------> | Participant B  | --> ROLLBACK locally
|             | --------------------> | Participant C  | --> ROLLBACK locally
+-------------+                       +----------------+
```

### Problems with 2PC

| Problem                    | Description                                           |
|----------------------------|-------------------------------------------------------|
| **Blocking**               | If coordinator crashes after Phase 1, participants    |
|                            | are stuck holding locks, waiting indefinitely          |
| **Coordinator SPOF**       | Single point of failure — if it dies, all are blocked |
| **Latency**                | Two round trips across network + lock holding time    |
| **Lock Contention**        | Participants hold locks during entire protocol         |
| **Not partition tolerant** | Network partition can cause inconsistency             |
| **Reduced availability**   | All participants must be available simultaneously     |

### Three-Phase Commit (3PC)

3PC adds a **pre-commit** phase to reduce the blocking problem:
1. **CanCommit?** — Coordinator asks if participants can commit
2. **PreCommit** — Participants prepare and acknowledge
3. **DoCommit** — Coordinator tells participants to commit

3PC reduces (but doesn't eliminate) the blocking problem. It is still impractical
for microservices because:
- Still requires all participants to be available
- Doesn't handle network partitions well
- More complex and slower than 2PC

### Why 2PC/3PC Are Rarely Used in Microservices

- Microservices use **heterogeneous databases** (Postgres, MongoDB, Redis) that
  don't support a common transaction protocol
- The **blocking nature** kills performance and availability
- It **violates service autonomy** — services can't be deployed independently
  if they're locked into a distributed transaction protocol
- Modern microservices favor **eventual consistency** over strong consistency

> **Key Insight:** 2PC works well within a single data center with homogeneous
> databases (e.g., multiple Oracle DBs with XA transactions). It does NOT work
> well across microservices with different databases.

---

## 24.4 The Saga Pattern

The **Saga pattern** is the preferred approach for managing distributed transactions
in microservices. It was originally described by Hector Garcia-Molina and Kenneth Salem
in a 1987 paper.

### What Is a Saga?

A saga is a **sequence of local transactions**. Each local transaction updates a
single service's database. If a step fails, previously completed steps are undone
by executing **compensating transactions**.

```
SAGA: Place Order
  
  T1: Create Order (Order Service)
  T2: Process Payment (Payment Service)
  T3: Reserve Inventory (Inventory Service)
  T4: Schedule Shipping (Shipping Service)

If T3 fails (out of stock):
  C2: Refund Payment (compensating for T2)
  C1: Cancel Order (compensating for T1)
```

### Compensating Transactions

A compensating transaction **undoes** the effect of a previous transaction.
It is NOT a rollback — it is a new transaction that semantically reverses the effect.

| Original Transaction              | Compensating Transaction            |
|-----------------------------------|--------------------------------------|
| Create Order                      | Cancel Order                         |
| Charge Payment                    | Refund Payment                       |
| Reserve Inventory                 | Release Inventory                    |
| Schedule Shipping                 | Cancel Shipping                      |
| Send Confirmation Email           | Send Cancellation Email              |

> **Important:** Some actions cannot be compensated (e.g., sending a physical letter).
> These are called **pivot transactions** — once executed, the saga must move forward.

---

## 24.5 Choreography-Based Saga

In a choreography-based saga, each service **publishes domain events** that trigger
the next step. There is **no central coordinator**.

```
Order       Payment      Inventory     Shipping     Notification
Service     Service      Service       Service      Service
  |             |            |             |              |
  |--OrderCreated-->         |             |              |
  |             |            |             |              |
  |        [Process Payment] |             |              |
  |             |            |             |              |
  |       PaymentProcessed-->|             |              |
  |             |            |             |              |
  |             |     [Reserve Inventory]  |              |
  |             |            |             |              |
  |             |    InventoryReserved---->|              |
  |             |            |             |              |
  |             |            |    [Schedule Shipping]     |
  |             |            |             |              |
  |             |            |     ShipmentScheduled----->|
  |             |            |             |              |
  |             |            |             |     [Send Confirmation]
```

### Failure Scenario (Choreography)

```
Order       Payment      Inventory
Service     Service      Service
  |             |            |
  |--OrderCreated-->         |
  |             |            |
  |       PaymentProcessed-->|
  |             |            |
  |             |  [OUT OF STOCK!]
  |             |            |
  |        <--InventoryFailed|
  |             |            |
  |      [Refund Payment]    |
  |             |            |
  |<--PaymentRefunded        |
  |             |            |
  |  [Cancel Order]          |
  |             |            |
  |--OrderCancelled--------->|  (notify all interested parties)
```

### Pros of Choreography
- **Loose coupling**: Services don't know about each other directly
- **Simple**: No central coordinator to build and maintain
- **Independent**: Each service manages its own logic
- **Scalable**: Easy to add new steps (just subscribe to events)

### Cons of Choreography
- **Hard to track**: No single place to see the overall saga status
- **Debugging nightmare**: Events flow through multiple services
- **Cyclic dependencies**: Service A events trigger B, which triggers A again
- **Complex failure handling**: Compensating events must be carefully designed
- **No clear ownership**: Who is responsible for the overall business process?

---

## 24.6 Orchestration-Based Saga

In an orchestration-based saga, a **central orchestrator** (saga coordinator)
tells each service what to do and when.

```
                    +---------------------+
                    |   ORDER SAGA        |
                    |   ORCHESTRATOR      |
                    |                     |
                    |  State Machine:     |
                    |  1. Create Order    |
                    |  2. Process Payment |
                    |  3. Reserve Stock   |
                    |  4. Schedule Ship   |
                    +----------+----------+
                               |
          +--------------------+--------------------+
          |                    |                    |
  1. CreateOrder     2. ProcessPayment    3. ReserveInventory
          |                    |                    |
          v                    v                    v
    +-----------+       +-----------+       +------------+
    |  Order    |       |  Payment  |       | Inventory  |
    |  Service  |       |  Service  |       |  Service   |
    +-----------+       +-----------+       +------------+
                               |
                      4. ScheduleShipping
                               |
                               v
                        +------------+
                        | Shipping   |
                        | Service    |
                        +------------+
```

### Orchestrator State Machine

The orchestrator is implemented as a **state machine** that tracks the saga's progress:

```
+-------------------+
|   SAGA STATES     |
+-------------------+
|                   |
| ORDER_PENDING ----+--> CreateOrder ---> ORDER_CREATED
|                   |                         |
|                   |                    ProcessPayment
|                   |                         |
|                   |                  PAYMENT_PROCESSED
|                   |                         |
|                   |                  ReserveInventory
|                   |                         |
|                   |              INVENTORY_RESERVED
|                   |                         |
|                   |               ScheduleShipping
|                   |                         |
|                   |              SHIPPING_SCHEDULED
|                   |                         |
|                   |                    COMPLETED
|                   |
| On any failure:   |
| COMPENSATING -----+--> Run compensating transactions in reverse
|                   |                         |
|                   |                      FAILED
+-------------------+
```

### Failure Scenario (Orchestration)

```
Orchestrator          Order Svc      Payment Svc     Inventory Svc
     |                    |               |                |
     |--CreateOrder------>|               |                |
     |<--OrderCreated-----|               |                |
     |                    |               |                |
     |--ProcessPayment--->|               |                |
     |                    |--charge------->|                |
     |<--PaymentOK--------|               |                |
     |                    |               |                |
     |--ReserveInventory->|               |                |
     |                    |               |--reserve------->|
     |<--OUT OF STOCK-----|               |                |
     |                    |               |                |
     | [COMPENSATE!]      |               |                |
     |                    |               |                |
     |--RefundPayment---->|               |                |
     |                    |--refund------->|                |
     |<--PaymentRefunded--|               |                |
     |                    |               |                |
     |--CancelOrder------>|               |                |
     |<--OrderCancelled---|               |                |
     |                    |               |                |
     | [SAGA FAILED]      |               |                |
```

### Pros of Orchestration
- **Clear visibility**: Orchestrator shows the entire saga state
- **Easy to reason about**: One place to see the business process
- **Centralized error handling**: Compensation logic is in one place
- **Better for complex workflows**: Conditional logic, branching, retries
- **Easy to monitor**: Saga state is tracked centrally

### Cons of Orchestration
- **Coupling**: Orchestrator knows about all participating services
- **Single point of failure**: Orchestrator must be highly available
- **Risk of "god" orchestrator**: Too much logic in one place
- **Latency**: All communication goes through the orchestrator

### Comparison Table

| Aspect                | Choreography                     | Orchestration                   |
|-----------------------|----------------------------------|---------------------------------|
| **Coordination**      | Decentralized (events)           | Centralized (orchestrator)      |
| **Coupling**          | Very loose                       | Moderate (orchestrator knows all)|
| **Visibility**        | Low (distributed flow)           | High (centralized state)        |
| **Complexity**        | Grows with number of services    | Contained in orchestrator       |
| **Error Handling**    | Each service handles its own     | Centralized in orchestrator     |
| **Adding Steps**      | Add subscriber (easy)            | Modify orchestrator (moderate)  |
| **Debugging**         | Difficult (event chains)         | Easier (saga state machine)     |
| **Best For**          | 2-3 services, simple flows       | 4+ services, complex workflows  |
| **Examples**          | Simple approval workflows        | Order processing, booking       |

---

## 24.7 Forward Recovery vs Backward Recovery

When a saga step fails, there are two strategies:

### Backward Recovery (Compensating)
Undo all completed steps by executing compensating transactions in reverse order.
This is the most common approach.

```
T1 → T2 → T3 → T4 (FAIL)
         <-- C3 <-- C2 <-- C1
```

### Forward Recovery (Retry)
Instead of undoing, **retry** the failed step until it succeeds. This works when
the failure is transient (e.g., a temporary network issue).

```
T1 → T2 → T3 → T4 (FAIL) → retry T4 → retry T4 → T4 (SUCCESS)
```

**When to use forward recovery:**
- The failure is likely transient (timeout, temporary unavailability)
- The step is **past the pivot point** (cannot be compensated)
- Retrying is cheaper than compensating all previous steps

**Pivot Transaction:**
A step in the saga that, once completed, means the saga **must succeed** (forward
recovery only). Steps before the pivot can be compensated; steps after must retry.

```
T1 → T2 → [T3: PIVOT] → T4 → T5
  <----compensate----|    |---retry--->
```

---

## 24.8 The Outbox Pattern

### The Dual Write Problem

A common pitfall in event-driven systems: a service needs to both **write to the
database** and **publish an event** to a message broker. These are two separate
systems — what if one succeeds and the other fails?

```
Service:
  1. Save order to database     ✓ SUCCESS
  2. Publish OrderCreated event  ✗ FAILED (broker down)
  
  Result: Order exists in DB but no event was published.
  Downstream services never know about the order!

Or worse:
  1. Publish OrderCreated event  ✓ SUCCESS
  2. Save order to database      ✗ FAILED (DB error)
  
  Result: Event was published but order doesn't exist!
  Downstream services process a phantom order!
```

### The Outbox Pattern Solution

Instead of writing to the DB and publishing to the broker separately, write the
event to an **outbox table** in the **same database transaction** as the business data.

```
+-------------------------------------------------------------------+
|  DATABASE TRANSACTION (Atomic)                                    |
|                                                                   |
|  1. INSERT INTO orders (id, customer, total)                      |
|     VALUES ('ord-123', 'cust-456', 99.99);                        |
|                                                                   |
|  2. INSERT INTO outbox (id, event_type, payload, published)       |
|     VALUES ('evt-789', 'OrderCreated', '{...}', false);           |
|                                                                   |
|  COMMIT;  -- Both succeed or both fail (ACID guaranteed)          |
+-------------------------------------------------------------------+
```

A separate **relay process** reads the outbox table and publishes events to the
message broker:

```
+----------+    Same DB     +------------+    Relay     +--------+    Publish    +--------+
|  Order   | -- TX -------> |  orders    | <-----------  | Outbox | -----------> | Kafka  |
|  Service |                |  outbox    |    Process    | Relay  |              | Topic  |
+----------+                +------------+    (polls)   +--------+              +--------+
```

### Relay Strategies

**1. Polling Publisher**
A background job periodically queries the outbox table for unpublished events.

```sql
SELECT * FROM outbox WHERE published = false ORDER BY created_at;
-- For each event: publish to Kafka, then mark as published
UPDATE outbox SET published = true WHERE id = 'evt-789';
```

**Pros:** Simple to implement
**Cons:** Polling interval introduces latency; DB load from frequent polling

**2. Change Data Capture (CDC)**
Use a tool like **Debezium** to capture database changes (inserts to the outbox
table) and stream them to Kafka in real-time.

```
+----------+     +------------+     +-----------+     +---------+
|  Order   | --> | PostgreSQL | --> | Debezium  | --> |  Kafka  |
|  Service |     |  (outbox)  |     | (CDC)     |     |  Topic  |
+----------+     +------------+     +-----------+     +---------+
                  DB transaction      Reads WAL
                  log (WAL)           in real-time
```

**Pros:** Real-time (low latency), no polling overhead
**Cons:** More infrastructure to manage (Debezium, Kafka Connect)

---

## 24.9 Idempotency in Distributed Transactions

In a saga, compensating transactions or retries may cause the same operation to be
executed **multiple times**. Every operation in a saga must be **idempotent**.

### Idempotency Strategies

**1. Idempotency Keys**
```
Client → POST /payments { orderId: "123", idempotencyKey: "idem-abc" }

Payment Service:
  1. Check: does idem-abc already exist in processed_keys table?
  2. If yes → return previous result (no duplicate charge)
  3. If no → process payment, store idem-abc in processed_keys table
```

**2. Conditional State Checks**
```
Reserve Inventory:
  UPDATE inventory 
  SET reserved = reserved + 5
  WHERE product_id = 'P1' 
    AND status != 'ALREADY_RESERVED_FOR_ORDER_123';
```

**3. Version/Sequence Numbers**
```
Process only if sequence matches:
  UPDATE account 
  SET balance = balance - 50 
  WHERE id = 'A1' AND version = 7;
  -- Won't re-execute if version has already advanced
```

---

## 24.10 Real-World Example: Order Processing Saga

Let's walk through a complete orchestration-based saga for order processing.

```
+--------------------------------------------------------------------+
|  ORDER SAGA ORCHESTRATOR                                           |
+--------------------------------------------------------------------+
|  Saga ID: saga-001                                                 |
|  Order ID: ord-123                                                 |
|  Status: IN_PROGRESS                                               |
|                                                                    |
|  Steps:                                                            |
|  +------+--------------------+-----------+------------------------+|
|  | Step | Action             | Status    | Compensating Action    ||
|  +------+--------------------+-----------+------------------------+|
|  |  1   | Create Order       | COMPLETED | Cancel Order           ||
|  |  2   | Process Payment    | COMPLETED | Refund Payment         ||
|  |  3   | Reserve Inventory  | FAILED    | Release Inventory      ||
|  |  4   | Schedule Shipping  | PENDING   | Cancel Shipping        ||
|  +------+--------------------+-----------+------------------------+|
|                                                                    |
|  Action: COMPENSATING (running compensations in reverse)           |
|  - Step 2: Refund Payment → IN_PROGRESS                           |
|  - Step 1: Cancel Order → PENDING                                  |
+--------------------------------------------------------------------+
```

### Detailed Flow

```
Step 1: Create Order
  Order Service → INSERT INTO orders (id, status) VALUES ('ord-123', 'PENDING')
  Response: OrderCreated { orderId: 'ord-123' }
  Orchestrator: Step 1 = COMPLETED

Step 2: Process Payment
  Payment Service → Charge credit card $99.99, record payment
  Response: PaymentProcessed { paymentId: 'pay-456', amount: 99.99 }
  Orchestrator: Step 2 = COMPLETED

Step 3: Reserve Inventory
  Inventory Service → Attempt to reserve 2x Widget, 1x Gadget
  Response: InventoryFailed { reason: 'Gadget out of stock' }
  Orchestrator: Step 3 = FAILED → INITIATE COMPENSATION

Compensation Step C2: Refund Payment
  Payment Service → Refund $99.99 to credit card
  Response: PaymentRefunded { refundId: 'ref-789' }
  Orchestrator: Compensation C2 = COMPLETED

Compensation Step C1: Cancel Order
  Order Service → UPDATE orders SET status = 'CANCELLED' WHERE id = 'ord-123'
  Response: OrderCancelled { orderId: 'ord-123' }
  Orchestrator: Compensation C1 = COMPLETED

Saga Final Status: COMPENSATED (all compensations completed)
```

---

## 24.11 Saga Frameworks and Tools

| Framework / Tool         | Language / Platform | Notes                              |
|--------------------------|---------------------|------------------------------------|
| **Temporal**             | Any (polyglot)      | Workflow engine, great for sagas   |
| **Axon Framework**       | Java                | Built-in saga support with events  |
| **MassTransit**          | .NET                | Saga state machines for .NET       |
| **Eventuate Tram**       | Java                | Chris Richardson's saga framework   |
| **Cadence** (Uber)       | Any                 | Predecessor to Temporal            |
| **AWS Step Functions**   | Serverless          | State machine as a service         |
| **Apache Camel**         | Java                | Integration patterns with saga EIP |

---

## 24.12 Summary: Choosing the Right Approach

```
  Do you need strong consistency across services?
       |                        |
      YES                      NO (eventual consistency OK)
       |                        |
  Is it within a single        Use Saga Pattern
  data center with              |
  homogeneous DBs?        +-----+-----+
       |         |         |           |
      YES        NO    Simple flow   Complex flow
       |         |     (2-3 svcs)    (4+ svcs)
    Use 2PC    Use     |              |
    (XA)      Saga   Choreography  Orchestration
                     Saga           Saga
```

| Approach          | Consistency  | Availability | Complexity | Use Case                    |
|-------------------|-------------|-------------|------------|------------------------------|
| **2PC/XA**        | Strong      | Low          | Medium     | Homogeneous DBs, one DC     |
| **Saga (Choreo)** | Eventual    | High         | Medium     | Simple multi-service flows   |
| **Saga (Orch)**   | Eventual    | High         | High       | Complex business processes   |
| **Outbox + CDC**  | Eventual    | High         | Medium     | Reliable event publishing    |

---

## 24.13 Key Takeaways

1. **Distributed transactions are fundamentally hard** because of network partitions,
   partial failures, and the absence of a shared transaction coordinator.

2. **Two-Phase Commit (2PC)** provides strong consistency but is blocking, slow, and
   impractical for microservices with heterogeneous databases.

3. **The Saga pattern** is the preferred approach: a sequence of local transactions
   with compensating actions to undo completed steps on failure.

4. **Choreography-based sagas** use events for decentralized coordination — simple
   but hard to track. **Orchestration-based sagas** use a central coordinator —
   clearer but more coupled.

5. **Compensating transactions** are not rollbacks — they are new transactions that
   semantically reverse the effect. Design every saga step with its compensation.

6. **The Outbox Pattern** solves the dual-write problem by writing events to an outbox
   table in the same database transaction, then relaying them to the message broker.

7. **Change Data Capture (CDC)** with tools like Debezium provides real-time outbox
   relay without polling.

8. **Idempotency is essential** in every saga step. Use idempotency keys, conditional
   updates, and version numbers.

9. **Forward recovery** (retry) is preferred when failures are transient and you've
   passed the pivot point. **Backward recovery** (compensate) handles permanent failures.

10. **Real-world sagas** power order processing, booking systems, payment flows,
    and any multi-service business transaction in microservices architectures.

---

## Further Reading

- *Microservices Patterns* by Chris Richardson (Ch. 4: Sagas)
- *Designing Data-Intensive Applications* by Martin Kleppmann (Ch. 9: Consistency)
- Saga Pattern — Microsoft Docs: https://docs.microsoft.com/en-us/azure/architecture/reference-architectures/saga/saga
- Temporal.io documentation: https://docs.temporal.io
- Debezium documentation: https://debezium.io/documentation/

---

*Previous: [Chapter 23 — CQRS](23-cqrs.md)*
*Next: [Chapter 25 — Consensus Algorithms](25-consensus.md)*
