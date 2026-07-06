# Chapter 10: Message Queues and Streaming

## Introduction — Why Asynchronous Processing?

In a typical synchronous architecture, Service A calls Service B and **waits** for a response. This creates **tight coupling**: if Service B is slow or down, Service A is also affected. As systems grow, synchronous chains of service calls become fragile, slow, and hard to scale.

**Asynchronous processing** decouples producers from consumers using an intermediary — a **message queue** or **event stream**. The producer sends a message and moves on immediately. The consumer processes it whenever it's ready.

```
  Synchronous (tightly coupled):
  +------+   Request   +------+   Request   +------+
  |Svc A +------------>|Svc B +------------>|Svc C |
  |      |<------------|      |<------------|      |
  |      |  Response   |      |  Response   |      |
  +------+  (waiting)  +------+  (waiting)  +------+
  
  If Svc C is slow, Svc B is slow, and Svc A is slow.
  If Svc C is down, the entire chain fails.


  Asynchronous (decoupled):
  +------+   Publish   +----------+   Consume   +------+
  |Svc A +------------>|  Message +------------>|Svc B |
  |      |  (fire &    |  Queue   |  (when      |      |
  |      |   forget)   |          |   ready)    |      |
  +------+             +----------+             +------+
  
  Svc A doesn't wait. If Svc B is slow or down, 
  messages queue up and are processed when Svc B recovers.
```

### Benefits of Asynchronous Processing

| Benefit | Description |
|---|---|
| **Decoupling** | Producer and consumer can be developed, deployed, and scaled independently |
| **Resilience** | If the consumer is down, messages are buffered; no data loss |
| **Scalability** | Add more consumers to handle increased load |
| **Load leveling** | Absorb traffic spikes; consumers process at their own pace |
| **Ordering** | Can guarantee message ordering within partitions |
| **Auditability** | Message log provides a record of all events |

---

## Message Queue vs Event Streaming

These are two fundamentally different paradigms, though they're often conflated.

### Message Queue (Traditional)

A message queue delivers messages to **one consumer** per message. Once consumed, the message is **deleted** from the queue. Think of it as a **task queue**.

```
  Producer              Queue              Consumer
  +------+  Enqueue   +------+  Dequeue   +------+
  |      +----------->| Msg1 +----------->|      |
  |      |            | Msg2 |            |      |
  |      |            | Msg3 |   (Msg1 is |      |
  +------+            +------+   deleted   +------+
                                 after ACK)
```

**Examples:** RabbitMQ, Amazon SQS, ActiveMQ, IBM MQ

### Event Streaming

An event stream is an **append-only log** of events. Messages are **not deleted** after consumption. Multiple consumers can read the same events independently, each tracking their own position (offset).

```
  Producer         Event Stream (Log)        Consumer Group A
  +------+ Append  +--+--+--+--+--+--+      +------+
  |      +-------->|E1|E2|E3|E4|E5|E6|----->|      | (offset: 4)
  +------+         +--+--+--+--+--+--+      +------+
                    ^                  ^
                    |                  |      Consumer Group B
                    oldest          newest   +------+
                    (retained)               |      | (offset: 2)
                                             +------+
  
  Events are retained for a configurable period (days/weeks/forever).
  Each consumer group tracks its own offset independently.
```

**Examples:** Apache Kafka, Amazon Kinesis, Apache Pulsar, Redpanda

### Comparison Table

| Feature | Message Queue | Event Streaming |
|---|---|---|
| **Message lifecycle** | Deleted after consumption | Retained (configurable period) |
| **Consumer model** | One consumer per message | Multiple consumers independently |
| **Replay** | Not possible (message gone) | Replay from any offset |
| **Ordering** | Queue-level (FIFO) | Partition-level |
| **Throughput** | Moderate (1K–100K msg/s) | Very high (millions msg/s) |
| **Use case** | Task distribution, work queues | Event sourcing, analytics, log aggregation |
| **Analogy** | Email (read and delete) | Newspaper (everyone reads the same edition) |

---

## Point-to-Point vs Pub/Sub Messaging

### Point-to-Point

One producer sends to one queue, one consumer receives each message. Used for **task distribution**.

```
  +----------+     +-------+     +----------+
  | Producer +---->| Queue +---->| Consumer |
  +----------+     +-------+     +----------+
  
  Each message is delivered to exactly ONE consumer.
  If multiple consumers listen, messages are distributed (load balanced).
  
  +----------+     +-------+     +----------+
  | Producer +---->| Queue +--+->| Consumer1|  (gets Msg1, Msg3)
  +----------+     |       |  |  +----------+
                   |  Msg1 |  |  +----------+
                   |  Msg2 |  +->| Consumer2|  (gets Msg2, Msg4)
                   |  Msg3 |     +----------+
                   |  Msg4 |
                   +-------+
```

### Publish/Subscribe (Pub/Sub)

One producer publishes to a **topic**, and all subscribed consumers receive a **copy** of every message.

```
  +----------+     +-------+     +----------+
  | Producer +---->| Topic +--+->|Subscriber|  (gets ALL messages)
  +----------+     |       |  |  |    A     |
                   |       |  |  +----------+
                   |       |  |  +----------+
                   |       |  +->|Subscriber|  (gets ALL messages)
                   |       |     |    B     |
                   +-------+     +----------+
  
  Use case: Order placed -> notify Inventory, Shipping, Analytics
```

### Hybrid: Consumer Groups (Kafka Model)

Kafka combines both: within a **consumer group**, messages are load-balanced (point-to-point). Across consumer groups, messages are broadcast (pub/sub).

```
  +----------+     +-------+     Consumer Group A (order-service)
  | Producer +---->| Topic |     +----------+
  +----------+     |       +--+->|Consumer A1| (partition 0)
                   |       |  |  +----------+
                   | P0:   |  |  +----------+
                   | [E1]  |  +->|Consumer A2| (partition 1)
                   | [E3]  |     +----------+
                   |       |
                   | P1:   |     Consumer Group B (analytics-service)
                   | [E2]  |     +----------+
                   | [E4]  +--+->|Consumer B1| (partition 0)
                   |       |  |  +----------+
                   +-------+  |  +----------+
                              +->|Consumer B2| (partition 1)
                                 +----------+
  
  Group A and Group B BOTH receive all messages (pub/sub).
  Within each group, partitions are distributed (point-to-point).
```

---

## Apache Kafka — Deep Dive

Kafka is the dominant event streaming platform, used by LinkedIn, Netflix, Uber, and thousands of other companies. It handles **trillions of messages per day** at some organizations.

### Core Architecture

```
  +------- Kafka Cluster -------+
  |                             |
  |  +--------+   +--------+   |
  |  |Broker 1|   |Broker 2|   |   (3+ brokers for HA)
  |  |        |   |        |   |
  |  | Topic A|   | Topic A|   |   (partitions distributed
  |  |  P0,P2 |   |  P1    |   |    across brokers)
  |  |        |   |        |   |
  |  | Topic B|   | Topic B|   |
  |  |  P0    |   |  P1,P2 |   |
  |  +--------+   +--------+   |
  |                             |
  |  +--------+                 |
  |  |Broker 3|   ZooKeeper /  |
  |  |        |   KRaft        |   (metadata management)
  |  | Topic A|   (consensus)  |
  |  |  P0rep |                |
  |  +--------+                 |
  +-----------------------------+
```

### Topics and Partitions

A **topic** is a named stream of events (like a database table). Each topic is divided into **partitions** for parallelism.

```
  Topic: "orders"
  
  Partition 0:  [Order1] [Order4] [Order7] [Order10] ...
  Partition 1:  [Order2] [Order5] [Order8] [Order11] ...
  Partition 2:  [Order3] [Order6] [Order9] [Order12] ...
  
  - Messages within a partition are strictly ordered
  - Messages across partitions have no ordering guarantee
  - Partition key determines which partition a message goes to
    e.g., hash(user_id) % num_partitions
```

### Consumer Groups and Offsets

```
  Topic: "orders" (3 partitions)
  
  Consumer Group: "order-processor" (3 consumers)
  
  +----+     +----+     +----+
  | P0 |     | P1 |     | P2 |
  +--+-+     +--+-+     +--+-+
     |          |          |
     v          v          v
  +----+     +----+     +----+
  | C0 |     | C1 |     | C2 |     Each consumer owns 1 partition
  +----+     +----+     +----+     (max parallelism = num partitions)
  
  Offset tracking:
  P0: [E0, E1, E2, E3, E4, E5, ...]
                    ^
                    |
              Consumer C0's committed offset = 3
              (has processed E0, E1, E2; will read E3 next)
```

**Key rules:**
- Each partition is consumed by **exactly one** consumer in a group
- Adding consumers beyond the partition count results in **idle consumers**
- If a consumer dies, its partitions are **rebalanced** to other consumers

### Replication

Each partition has one **leader** and N-1 **followers** (replicas). Producers write to the leader; followers replicate.

```
  Partition 0: Leader on Broker 1, Replicas on Broker 2 & 3
  
  Producer --> Broker 1 (Leader P0)
                 |
                 +---> Broker 2 (Follower P0) -- replicates
                 +---> Broker 3 (Follower P0) -- replicates
  
  If Broker 1 dies:
  Broker 2 or 3 is elected new leader (automatic failover)
```

**`acks` configuration:**
- `acks=0` — Don't wait for any acknowledgment (fastest, risk of data loss)
- `acks=1` — Wait for leader acknowledgment (balanced)
- `acks=all` — Wait for all replicas to acknowledge (safest, slowest)

---

## RabbitMQ — Deep Dive

RabbitMQ is a traditional message broker implementing the **AMQP** (Advanced Message Queuing Protocol). It excels at complex routing, low-latency message delivery, and task distribution.

### Architecture: Exchanges, Queues, and Bindings

```
  Producer                        RabbitMQ Broker                     Consumer
  +------+    Publish    +--------+    Binding    +-------+   Consume  +------+
  |      +-------------->|Exchange+-------------->| Queue +----------->|      |
  +------+   to exchange |        |  (routing     |       |  from     +------+
             with        +--------+   rules)      +-------+  queue
             routing key
```

### Exchange Types

| Exchange Type | Routing Logic | Use Case |
|---|---|---|
| **Direct** | Message routed to queue with matching routing key | Task queue with specific categories |
| **Fanout** | Message sent to ALL bound queues (broadcast) | Notifications to all subscribers |
| **Topic** | Pattern matching on routing key (wildcards: `*`, `#`) | Selective subscription (e.g., `logs.*.error`) |
| **Headers** | Match on message headers (not routing key) | Complex routing rules |

```
  Direct Exchange:                    Fanout Exchange:
  
  routing_key="pdf"                   (ignores routing key)
       |                                    |
  +----v----+                         +-----v-----+
  | Exchange|                         |  Exchange  |
  +----+----+                         +--+--+--+--+
       |                                 |  |  |
  +----v----+                    +-------+ |  +-------+
  |pdf_queue|                    |Queue A| |  |Queue C|
  +---------+                    +-------+ |  +-------+
  (only PDF tasks)                    +----v--+
                                      |Queue B|
                                      +-------+
                                      (all queues get all messages)


  Topic Exchange:
  
  routing_key="logs.payment.error"
       |
  +----v----+
  | Exchange|
  +--+-+-+--+
     | | |
     | | +---> Queue A (binding: "logs.payment.*")    -> MATCH
     | +-----> Queue B (binding: "logs.#")            -> MATCH
     +-------> Queue C (binding: "logs.auth.error")   -> NO MATCH
```

### Acknowledgments

RabbitMQ requires consumers to **acknowledge** messages. Unacknowledged messages are redelivered.

```
  1. Consumer receives message
  2. Consumer processes message
  3a. Success: Consumer sends ACK -> message removed from queue
  3b. Failure: Consumer sends NACK/REJECT -> message requeued or dead-lettered
  3c. Timeout: No ACK received -> message redelivered to another consumer
```

---

## Amazon SQS / SNS

### Amazon SQS (Simple Queue Service)

Fully managed message queue — no infrastructure to manage.

| Feature | SQS Standard | SQS FIFO |
|---|---|---|
| **Ordering** | Best-effort (may be out of order) | Strict FIFO ordering |
| **Delivery** | At-least-once (may duplicate) | Exactly-once processing |
| **Throughput** | Unlimited | 3,000 msg/s (with batching) |
| **Deduplication** | No built-in | 5-minute deduplication window |
| **Use case** | General decoupling, high throughput | Order-sensitive workflows |

### Amazon SNS (Simple Notification Service)

Pub/Sub messaging service — publish to a topic, deliver to multiple subscribers.

```
  +----------+     +-----------+     +----------+
  | Producer +---->| SNS Topic +--+->| SQS Queue|  (for processing)
  +----------+     |           |  |  +----------+
                   |           |  |  +----------+
                   |           |  +->| Lambda   |  (serverless processing)
                   |           |  |  +----------+
                   |           |  |  +----------+
                   |           |  +->| Email    |  (notifications)
                   +-----------+  |  +----------+
                                  |  +----------+
                                  +->| HTTP     |  (webhook)
                                     +----------+
```

**Common pattern: SNS + SQS (Fanout)**
Publish to SNS topic -> fan out to multiple SQS queues -> each queue has its own consumer group.

---

## Delivery Guarantees

One of the most important concepts in messaging systems:

### The Three Guarantees

| Guarantee | Description | Implementation | Risk |
|---|---|---|---|
| **At-most-once** | Message delivered 0 or 1 times. Fire-and-forget. | No retries, no ACK | Message loss (acceptable for metrics, logs) |
| **At-least-once** | Message delivered 1 or more times. May have duplicates. | ACK + retry on failure | Duplicate processing (most common choice) |
| **Exactly-once** | Message delivered exactly 1 time. No loss, no duplicates. | Idempotent producers + transactional consumers | Complex, performance overhead |

```
  At-most-once:          At-least-once:          Exactly-once:
  
  Send -> Done           Send -> ACK?            Send -> ACK?
  (no retry)              |                       |
                     No ACK? Retry!          No ACK? Retry!
                          |                  + deduplication
                     May duplicate           + transactions
                                             + idempotency
```

### Why Exactly-Once is Hard

True exactly-once delivery requires coordination between the messaging system and the consumer's data store. In practice, most systems achieve **"effectively exactly-once"** by combining:
1. **At-least-once delivery** (retry until ACK)
2. **Idempotent consumers** (processing the same message twice has the same effect)

**Kafka's approach:** Idempotent producers (dedup on broker side) + transactional writes (atomic produce + consume offset commit).

---

## Message Ordering Guarantees

| System | Ordering Guarantee |
|---|---|
| **Kafka** | Ordered **within a partition** (not across partitions) |
| **RabbitMQ** | Ordered within a single queue (if single consumer) |
| **SQS Standard** | Best-effort ordering (no guarantee) |
| **SQS FIFO** | Strict FIFO within a **message group** |
| **Kinesis** | Ordered within a **shard** (partition) |

### How to Guarantee Ordering

Use a **partition key** that groups related messages to the same partition:

```
  Partition key = user_id
  
  User 123's events: [login] -> [purchase] -> [logout]
  All go to Partition 2 (hash("123") % N = 2)
  -> Guaranteed to be processed in order
  
  User 456's events may go to a different partition
  -> No ordering guarantee relative to User 123's events
  -> But User 456's own events are in order
```

---

## Dead Letter Queues (DLQ)

A **Dead Letter Queue** is a separate queue where messages that **cannot be processed** are sent after repeated failures. This prevents **poison messages** from blocking the entire queue.

```
  Main Queue                 Processing                Dead Letter Queue
  +-------+   Consume   +----------+   Failure     +-------+
  | Msg A +------------>| Consumer |   (3 retries)  | Msg A |
  | Msg B |             |          +--------------->| (failed)|
  | Msg C |             | Process  |                |       |
  +-------+             +----------+                +-------+
  
  Msg A fails 3 times -> moved to DLQ
  Msg B and Msg C continue processing normally
  
  DLQ is monitored:
  - Alert operations team
  - Manual investigation
  - Retry after fixing the bug
```

### DLQ Best Practices

1. **Set a max retry count** (e.g., 3-5 retries before moving to DLQ)
2. **Add metadata** — Include failure reason, timestamp, original queue, retry count
3. **Monitor DLQ size** — Alert when messages appear in the DLQ
4. **Retention period** — Keep DLQ messages long enough for investigation (14-30 days)
5. **Replay mechanism** — Build tooling to replay DLQ messages back to the main queue after fixing bugs

---

## Backpressure Handling

**Backpressure** occurs when producers generate messages faster than consumers can process them. Without handling, this leads to queue overflow, OOM errors, or unbounded latency.

### Strategies

| Strategy | How It Works | Trade-off |
|---|---|---|
| **Buffer (queue it)** | Queue absorbs the burst; consumers catch up later | Increased latency during spikes; queue size may grow |
| **Drop** | Drop messages when queue is full | Data loss (acceptable for metrics, real-time data) |
| **Throttle producer** | Reject or slow down producer when queue is full | Producer must handle rejections; may block upstream |
| **Scale consumers** | Auto-scale consumers based on queue depth | Takes time to scale; cost increases |
| **Rate limiting** | Limit producer's publish rate | Prevents overload but may lose data at source |

```
  Backpressure Handling Flow:
  
  Queue Depth < Threshold?
      |
      +-- Yes --> Normal processing
      |
      +-- No  --> Queue Depth < Critical?
                      |
                      +-- Yes --> Scale up consumers (auto-scaling)
                      |           + Alert operations
                      |
                      +-- No  --> Throttle producers
                                  + Emergency scale consumers
                                  + Consider dropping low-priority messages
```

---

## Idempotency — Why Consumers Must Be Idempotent

In an **at-least-once** delivery system (the most common), consumers **will** receive duplicate messages. If the consumer is not idempotent, duplicates cause incorrect behavior.

### What is Idempotency?

An operation is **idempotent** if performing it multiple times has the same effect as performing it once.

```
  Idempotent:                          NOT Idempotent:
  
  SET balance = 100                    INCREMENT balance BY 10
  (doing it 3 times: balance = 100)    (doing it 3 times: balance += 30)
  
  SET status = "shipped"               INSERT INTO orders (...) 
  (doing it 3 times: status=shipped)   (doing it 3 times: 3 orders created!)
```

### Making Consumers Idempotent

| Technique | How It Works |
|---|---|
| **Idempotency key** | Include a unique ID in each message; consumer checks if already processed before acting |
| **Upsert instead of insert** | Use `INSERT ... ON CONFLICT UPDATE` instead of `INSERT` |
| **Deduplication table** | Store processed message IDs in a DB table; skip if exists |
| **Conditional updates** | `UPDATE orders SET status='shipped' WHERE id=123 AND status='processing'` |
| **Version/sequence number** | Only process messages with a higher version than what's stored |

```
  Idempotent Consumer Pattern:
  
  1. Receive message (id: "msg-abc-123", payload: {order_id: 456, action: "ship"})
  2. Check dedup table: SELECT 1 FROM processed_messages WHERE id = 'msg-abc-123'
  3a. Found? -> Skip (already processed)
  3b. Not found? -> Process message, then INSERT INTO processed_messages (id) VALUES ('msg-abc-123')
  4. Acknowledge message
```

---

## Real-World Patterns

### Pattern 1: Event Notification

Lightweight event that says "something happened" — consumer fetches details from the source.

```
  Order Service          Event Bus          Notification Service
  +-----------+  Event   +-------+  Event   +-----------+
  |           +--------->|       +--------->|           |
  | Order     |  {type:  | Kafka |          | Reads     |
  | Created   |  "order_ |       |          | order     |
  |           |  created"|       |          | details   |
  +-----------+  id: 123}+-------+          | from API  |
                                            +-----------+
```

### Pattern 2: Event-Carried State Transfer

The event carries the **full state** needed by consumers, so they don't need to call back to the source.

```
  Order Service          Event Bus          Analytics Service
  +-----------+  Event   +-------+  Event   +-----------+
  |           +--------->|       +--------->|           |
  | Order     |  {type:  | Kafka |          | Has all   |
  | Created   |  "order_ |       |          | data it   |
  |           |  created"|       |          | needs in  |
  +-----------+  id: 123,+-------+          | the event |
                 items:                     +-----------+
                 [{...}],
                 total: 99.99,
                 user: {...}}
```

### Pattern 3: Event Sourcing

Store **all state changes as events** in an append-only log. Current state is derived by replaying events.

```
  Event Store (append-only log):
  
  [AccountCreated: {id: 1, name: "Alice"}]
  [Deposited: {id: 1, amount: 1000}]
  [Withdrawn: {id: 1, amount: 200}]
  [Deposited: {id: 1, amount: 500}]
  
  Current state (derived by replay):
  Account 1: { name: "Alice", balance: 1300 }
  
  Benefits:
  - Complete audit trail
  - Time travel (rebuild state at any point in time)
  - Different read models from the same events (CQRS)
  - Debug by replaying events
```

---

## Choosing Between Kafka, RabbitMQ, and SQS

### Decision Table

| Criterion | Kafka | RabbitMQ | Amazon SQS |
|---|---|---|---|
| **Primary model** | Event streaming (log) | Message queue (broker) | Message queue (managed) |
| **Throughput** | Millions of msg/s | Tens of thousands msg/s | Unlimited (auto-scales) |
| **Message retention** | Configurable (days/weeks/forever) | Until consumed | 4 days (up to 14) |
| **Replay** | Yes (from any offset) | No | No |
| **Ordering** | Per-partition | Per-queue | FIFO queues only |
| **Routing** | Topic/partition based | Complex routing (exchanges) | Simple queue-based |
| **Latency** | ~5ms (batched) | ~1ms | ~20-50ms |
| **Ops overhead** | High (self-managed) or Confluent Cloud | Medium | None (fully managed) |
| **Delivery** | At-least-once / Exactly-once | At-least-once | At-least-once / Exactly-once (FIFO) |
| **Best for** | Event sourcing, log aggregation, stream processing, high throughput | Task queues, complex routing, RPC, low-latency delivery | Simple decoupling, serverless, AWS-native |

### Quick Decision Guide

```
  What do you need?
       |
       +-- Event log + replay + high throughput? --> Kafka
       |
       +-- Complex routing + low latency + priority? --> RabbitMQ
       |
       +-- Managed + simple + serverless? --> Amazon SQS
       |
       +-- Pub/Sub fanout in AWS? --> SNS + SQS
       |
       +-- Stream processing in AWS? --> Kinesis
```

### When to Use Each — Real Scenarios

| Scenario | Recommended | Reason |
|---|---|---|
| Real-time analytics pipeline | Kafka | High throughput, retention, stream processing |
| Background job processing | RabbitMQ or SQS | Task distribution, simple queue semantics |
| Microservice event bus | Kafka | Event sourcing, multiple consumer groups |
| Email notification queue | SQS | Simple, managed, no infrastructure |
| Order processing pipeline | Kafka or SQS FIFO | Ordering within customer, durability |
| IoT sensor data ingestion | Kafka or Kinesis | High write throughput, time-series |
| Chat message delivery | RabbitMQ | Low latency, per-user routing |
| Audit log / compliance | Kafka | Immutable log, long retention, replay |

---

## Key Takeaways

```
+-------------------------------------------------------------------+
|         Message Queues & Streaming — Key Takeaways                |
+-------------------------------------------------------------------+
|                                                                   |
|  1. Async messaging decouples services, improves resilience,      |
|     and enables independent scaling. Use it whenever services     |
|     don't need synchronous responses.                             |
|                                                                   |
|  2. Message queues (RabbitMQ, SQS) are for task distribution.     |
|     Event streams (Kafka) are for event logs with replay.         |
|     Choose based on whether you need message retention.           |
|                                                                   |
|  3. Kafka's partition model provides both pub/sub (across         |
|     consumer groups) and load balancing (within a group).         |
|     Number of partitions = max parallelism.                       |
|                                                                   |
|  4. At-least-once delivery is the practical default.              |
|     Make consumers idempotent to handle duplicates safely.        |
|                                                                   |
|  5. Dead Letter Queues are essential for handling poison           |
|     messages. Always configure DLQs in production.                |
|                                                                   |
|  6. Ordering is guaranteed within a partition/queue, not          |
|     across. Use partition keys to group related messages.         |
|                                                                   |
|  7. Backpressure handling (scaling consumers, throttling          |
|     producers) is critical for production stability.              |
|                                                                   |
|  8. Event sourcing + CQRS is a powerful pattern enabled by        |
|     event streaming. Consider it for audit-heavy,                 |
|     complex-domain applications.                                  |
|                                                                   |
|  9. For AWS-native, fully managed simplicity, use SQS + SNS.     |
|     For high-throughput event streaming, use Kafka.               |
|     For complex routing, use RabbitMQ.                            |
|                                                                   |
+-------------------------------------------------------------------+
```

---

*Previous Chapter: [Chapter 9 — Storage Systems](./09-storage.md)*
