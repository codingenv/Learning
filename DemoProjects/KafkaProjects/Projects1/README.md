# Kafka Learning Project

A concept-focused Java + Gradle project to learn Apache Kafka from the ground up.
Built with Java 21, Gradle 8, and Apache Kafka 3.7 (KRaft mode — no ZooKeeper needed).

---

## Table of Contents

1. [What is Apache Kafka?](#1-what-is-apache-kafka)
2. [Core Concepts](#2-core-concepts)
   - Topics, Partitions, Segments
   - Brokers and Clusters
   - Producers
   - Consumers
   - Consumer Groups and Rebalancing
   - Offsets
   - Replication
   - ZooKeeper vs KRaft
3. [Project Structure](#3-project-structure)
4. [Prerequisites](#4-prerequisites)
5. [Setup — Start Kafka with Docker](#5-setup--start-kafka-with-docker)
6. [Running the Demos — Step by Step](#6-running-the-demos--step-by-step)
   - Step 1: Concepts Overview (no Kafka needed)
   - Step 2: Create Topics (AdminClient)
   - Step 3: Send Messages (Producer)
   - Step 4: Read Messages (Consumer)
   - Step 5: Custom Partitioner
   - Step 6: JSON Serialization
   - Step 7: Consumer Groups and Rebalancing
   - Step 8: Partitions and Offsets Explorer
   - Step 9: Transactions
7. [Understanding the Output](#7-understanding-the-output)
8. [Delivery Semantics](#8-delivery-semantics)
9. [Kafka UI — Visual Dashboard](#9-kafka-ui--visual-dashboard)
10. [Useful CLI Commands](#10-useful-cli-commands)
11. [Key Configuration Reference](#11-key-configuration-reference)

---

## 1. What is Apache Kafka?

Apache Kafka is a **distributed, fault-tolerant, high-throughput event streaming platform**.
Originally built at LinkedIn and open-sourced in 2011, it is now the industry standard for:

- **Messaging** — decouple producers from consumers
- **Activity tracking** — clickstreams, user events, audit logs
- **Metrics and log aggregation** — collect data from many services into one place
- **Stream processing** — real-time transformations with Kafka Streams or ksqlDB
- **Event sourcing** — durable log of every state change in your system
- **Change Data Capture (CDC)** — replicate database changes to other systems

The key difference between Kafka and a traditional message queue (like RabbitMQ):

| Traditional Queue | Kafka |
|-------------------|-------|
| Message deleted after consumed | Message stays in log (retention period) |
| One consumer gets each message | Many consumer groups read independently |
| Push-based delivery | Pull-based (consumer controls pace) |
| No replay | Full replay from any offset |
| Harder to scale | Scales horizontally via partitions |

---

## 2. Core Concepts

### Topics, Partitions, and Segments

**Topic** — A named stream of records. Think of it like a database table or a folder.
Topics are multi-subscriber — many independent consumer groups can read the same topic.

**Partition** — Each topic is split into one or more partitions.
A partition is an **ordered, immutable sequence of records**.
Records within a partition are assigned monotonically increasing IDs called **offsets**.

```
Topic: "orders"  (3 partitions)
┌─────────────────────────────────────────────────────────────┐
│ Partition 0: [0]→"order#1"  [1]→"order#4"  [2]→"order#7"  │
│ Partition 1: [0]→"order#2"  [1]→"order#5"  [2]→"order#8"  │
│ Partition 2: [0]→"order#3"  [1]→"order#6"  [2]→"order#9"  │
└─────────────────────────────────────────────────────────────┘
```

Why partitions matter:
- **Parallelism** — multiple consumers can read different partitions simultaneously
- **Scalability** — partitions are distributed across brokers
- **Ordering** — order is guaranteed ONLY within a single partition, not across partitions

**Segment** — Partitions are further split into segment files on disk.
Kafka writes to the active segment and rolls to a new one when `segment.bytes` or
`segment.ms` is exceeded. Old segments are deleted or compacted per retention policy.

---

### Brokers and Clusters

**Broker** — A single Kafka server process. It stores partition data on disk and serves
producer and consumer requests.

**Cluster** — A group of brokers. Each broker is identified by an integer `broker.id`.

```
Cluster with 3 brokers:
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Broker 1    │  │  Broker 2    │  │  Broker 3    │
│  leader P0   │  │  leader P1   │  │  leader P2   │
│  replica P1  │  │  replica P2  │  │  replica P0  │
└──────────────┘  └──────────────┘  └──────────────┘
```

**Bootstrap Servers** — Clients connect to one or more bootstrap servers to discover
the full cluster. Only a few need to be listed — clients self-discover the rest via metadata.

---

### Producers

A producer publishes (writes) records to topics.

**Record structure:**
- `topic` — destination topic
- `partition` — optional; determined by partitioner if omitted
- `key` — optional bytes; same key always goes to same partition
- `value` — the payload (bytes)
- `headers` — optional key-value metadata
- `timestamp` — event time or log-append time

**Partitioning strategy (in order of priority):**
1. If partition explicitly set → use it
2. If key is present → `hash(key) % numPartitions`
3. If no key → sticky / round-robin

**Producer acknowledgements (`acks`):**

| acks | Meaning | Risk |
|------|---------|------|
| `0` | Fire and forget — no wait | Data loss possible |
| `1` | Leader confirms write | Loss if leader crashes before replication |
| `all` | All in-sync replicas confirm | Safest, highest latency |

**Three send patterns:**

```java
// Pattern 1 — Fire and forget (async, no callback)
producer.send(record);

// Pattern 2 — Async with callback
producer.send(record, (metadata, exception) -> {
    log.info("partition={} offset={}", metadata.partition(), metadata.offset());
});

// Pattern 3 — Synchronous (blocks until broker acknowledges)
RecordMetadata meta = producer.send(record).get();
```

---

### Consumers

A consumer reads (pulls) records from topics.

**Pull model** — Kafka consumers pull data at their own pace.
This prevents consumers from being overwhelmed (unlike push-based systems).

**Consumer lifecycle:**
1. Create consumer with `group.id`
2. `subscribe()` to topics (or `assign()` specific partitions)
3. Call `poll()` in a loop — this ALSO sends heartbeats to the broker
4. Process records
5. Commit offsets (auto or manual)
6. `close()` — triggers rebalance so other consumers pick up the partitions

**`auto.offset.reset` config:**

| Value | Behaviour |
|-------|-----------|
| `earliest` | Start from the oldest available record in the topic |
| `latest` | Start from the newest record (skip historical data) |
| `none` | Throw exception if no committed offset found |

**Offset commit strategies:**

| Strategy | Config | Behaviour |
|----------|--------|-----------|
| Auto-commit | `enable.auto.commit=true` | Kafka commits every `auto.commit.interval.ms` |
| Manual sync | `enable.auto.commit=false` + `commitSync()` | Blocks until broker acknowledges commit |
| Manual async | `enable.auto.commit=false` + `commitAsync()` | Non-blocking, callback on completion |

---

### Consumer Groups and Rebalancing

Consumer groups allow multiple consumers to share the work of reading a topic.

**Rules:**
- Each partition is assigned to **exactly ONE consumer** in the group at any time
- Multiple consumer groups can read the same topic **independently**
- If consumers > partitions, some consumers sit idle

```
Topic: "orders" (3 partitions)
Consumer Group: "order-processor"

2 consumers:              3 consumers:
  Consumer A → P0, P1      Consumer A → P0
  Consumer B → P2          Consumer B → P1
                           Consumer C → P2
```

**Rebalancing** — When a consumer joins or leaves the group, Kafka triggers a rebalance
to re-assign partitions. During a rebalance, consumption pauses.

The rebalance protocol (what you see as a 10-second delay on startup):

```
Consumer                    Group Coordinator (Broker)
   |                                |
   |--- JoinGroup request --------->|
   |<-- "you are the leader" -------|
   |--- SyncGroup (assignment) ---->|
   |<-- partition assignments ------|
   |                                |
   ↓  NOW the consumer can fetch data
```

---

### Offsets

The offset is the position of the last consumed record in a partition.
Consumers commit offsets to Kafka (stored in `__consumer_offsets` internal topic)
so they can resume from the correct position after a restart.

```
Partition 0:  [0] [1] [2] [3] [4] [5] [6] [7] [8]
                                    ↑
                            committed offset = 5
                            (consumer will resume from 5 on restart)
```

**Important:** Offsets are **per consumer group per partition**.
Two different consumer groups tracking the same topic have completely independent offsets.

---

### Replication

**Replication factor** — Each partition is replicated across N brokers.
One replica is the **Leader** (handles all reads and writes).
Others are **Followers** (replicate from the leader).

**In-Sync Replicas (ISR)** — Set of replicas fully caught up with the leader.
`min.insync.replicas` controls how many must be in ISR for writes to succeed.

**Leader election** — If a leader fails, Kafka elects a new leader from the ISR.

---

### ZooKeeper vs KRaft Mode

| ZooKeeper (legacy) | KRaft Mode (Kafka 3.3+) |
|-------------------|------------------------|
| Separate ZooKeeper cluster needed | Kafka manages its own metadata |
| Complex operations | Simpler — one system to run |
| Slow controller failover | Fast controller failover |
| Limited partition scalability | Scales to millions of partitions |

This project uses **KRaft mode** — no ZooKeeper needed.

---

## 3. Project Structure

```
Projects1/
├── docker-compose.yml          ← Kafka broker + Kafka UI
├── build.gradle                ← Root: common dependencies for all modules
├── settings.gradle             ← Lists all submodules
│
├── 01-basics/                  ← Topics, AdminClient, concepts overview
├── 02-producers/               ← Producer patterns, custom partitioner
├── 03-consumers/               ← Consumer loop, manual offsets
├── 04-consumer-groups/         ← Rebalancing demo (3 consumers, 3 partitions)
├── 05-partitions-offsets/      ← Seek, replay, lag explorer
├── 06-serialization/           ← JSON serializer/deserializer with Jackson
├── 07-kafka-streams/           ← Word count, windowed aggregation
└── 08-transactions/            ← Exactly-once, transactional producer
```

Each module has its own `build.gradle` with named Gradle tasks so you never
need to pass class names on the command line.

---

## 4. Prerequisites

- **Java 21** — `java -version` should show 21+
- **Docker Desktop** — running in the system tray (green icon)
- **WSL 2** — required on Windows (installed via `wsl --install`)

Verify Docker is running:
```bash
docker run hello-world
```

---

## 5. Setup — Start Kafka with Docker

```bash
cd E:\Prakash\Github\KafkaProjects\Projects1
docker compose up -d
```

Check both containers are running:
```bash
docker compose ps
```

Expected output:
```
NAME         IMAGE                           STATUS
kafka-demo   apache/kafka:3.7.0              Up
kafka-ui     provectuslabs/kafka-ui:latest   Up
```

Open **http://localhost:8080** in your browser — you will see the Kafka UI dashboard.

To stop everything:
```bash
docker compose down
```

---

## 6. Running the Demos — Step by Step

Follow these steps **in order**. Each step builds on the previous one.

---

### Step 1 — Concepts Overview (no Kafka needed)

```bash
.\gradlew :01-basics:runConcepts
```

**What it does:** Prints a detailed concept map to the console covering all Kafka
fundamentals — topics, partitions, brokers, producers, consumers, offsets, replication,
KRaft vs ZooKeeper. No broker connection needed. Good first read.

---

### Step 2 — Create Topics via AdminClient

```bash
.\gradlew :01-basics:runAdmin
```

**What it does:** Connects to Kafka using `KafkaAdminClient` — a special client just for
management operations (not for producing or consuming). It:

1. **Creates 3 topics:**
   - `demo-basic` — 3 partitions, replication factor 1
   - `demo-short-retention` — data deleted after 1 hour, max 100 MB
   - `demo-compacted` — keeps only the LATEST value per key (like a key-value store)

2. **Lists all topics** — you will see internal Kafka topics like `__consumer_offsets` too

3. **Describes topics** — shows partition metadata:
   ```
   Topic: demo-basic
     Partition 0  leader=1  replicas=[1]  isr=[1]
     Partition 1  leader=1  replicas=[1]  isr=[1]
     Partition 2  leader=1  replicas=[1]  isr=[1]
   ```
   - `leader=1` → broker 1 handles reads/writes for this partition
   - `replicas=[1]` → partition stored on broker 1 only (single broker dev setup)
   - `isr=[1]` → In-Sync Replicas = just broker 1 (fully caught up)

4. **Deletes topics** — cleanup after demo

After running → check **http://localhost:8080 → Topics** to see the topics created.

---

### Step 3 — Send Messages (Producer)

```bash
.\gradlew :02-producers:runBasicProducer
```

**What it does:** Sends messages to `demo-basic` using 3 different patterns:

**Pattern 1 — Fire and forget:**
```java
producer.send(record);  // async, no confirmation waited
```
Fastest. Message goes into an internal memory buffer. A background I/O thread sends it.
You do not wait for confirmation — data loss possible if broker is down.

**Pattern 2 — Async with callback:**
```java
producer.send(record, (metadata, exception) -> {
    log.info("ACK partition={} offset={}", metadata.partition(), metadata.offset());
});
```
Non-blocking. The callback fires once the broker acknowledges. You see in the logs
which partition and offset each message landed on.

**Pattern 3 — Synchronous:**
```java
RecordMetadata meta = producer.send(record).get();  // .get() BLOCKS
```
Slowest. Your thread waits until the broker confirms. You know immediately if it failed.

**What to observe:**
- Same key always lands on the same partition: `hash("user-0") % 3 = 2` → always partition 2
- Offsets increment with each run — they NEVER reset. Kafka is a persistent log.

After running → **http://localhost:8080 → Topics → demo-basic → Messages** to see all messages.

---

### Step 4 — Read Messages (Consumer)

Open a **new PowerShell window** and run:

```bash
cd E:\Prakash\Github\KafkaProjects\Projects1
.\gradlew :03-consumers:runBasicConsumer
```

**What it does:** Starts a consumer that reads from `demo-basic` using a proper
production-style poll loop with manual offset commit and graceful shutdown.

**The poll loop explained:**
```java
while (keepRunning) {
    // poll() does TWO things simultaneously:
    //   1. Fetches records from Kafka (up to max.poll.records)
    //   2. Sends heartbeat to Group Coordinator (proves consumer is alive)
    ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

    for (ConsumerRecord<String,String> record : records) {
        processRecord(record);
    }

    // Commit AFTER processing — at-least-once delivery
    consumer.commitSync();
}
```

**Key configs in this consumer:**

| Config | Value | Meaning |
|--------|-------|---------|
| `auto.offset.reset` | `earliest` | Start from beginning if no committed offset |
| `enable.auto.commit` | `false` | We commit manually after processing |
| `session.timeout.ms` | `10000` | Consumer declared dead if no heartbeat for 10s |
| `max.poll.records` | `100` | Max records per poll() call |

**Try this:** Keep the consumer running in window 2, then run the producer again in
window 1. You will see messages appear in the consumer **in real time**.

Press **Ctrl+C** to stop — notice it logs "Shutdown signal received" and closes cleanly.
This is `consumer.wakeup()` breaking out of `poll()` gracefully.

---

### Step 5 — Custom Partitioner

```bash
.\gradlew :02-producers:runCustomPartitioner
```

**What it does:** Overrides Kafka's default key-hash partitioner with custom business logic.
`premium` orders always go to partition 0 (dedicated fast lane).
`standard` orders are distributed round-robin across all other partitions.

**Expected output:**
```
Sent key=premium  → partition=0   ← always 0
Sent key=standard → partition=1
Sent key=premium  → partition=0   ← always 0
Sent key=standard → partition=2
Sent key=premium  → partition=0   ← always 0
```

**Real-world uses of custom partitioners:**
- VIP customers → dedicated partition with more consumer threads
- Geographic routing → EU records to partition 0, US to partition 1
- Hot-key mitigation → spread a high-volume key across multiple partitions

---

### Step 6 — JSON Serialization

```bash
.\gradlew :06-serialization:runJsonSerialization
```

**What it does:** Real applications send Java objects, not plain strings. This demo
produces `Order` objects serialized to JSON bytes, then consumes and deserializes them
back to `Order` objects. Uses custom `Serializer` / `Deserializer` classes backed by Jackson.

**The flow:**
```
Producer side:                          Consumer side:
Order object                            byte[]
   ↓  JsonSerializer                       ↓  JsonDeserializer
{"orderId":"ORD-001","amount":99.99}    Order object
   ↓                                       ↑
   └──────── Kafka stores bytes ───────────┘
```

Kafka itself never knows about `Order` — it only stores and delivers raw bytes.
The serializer/deserializer is entirely on the client side.

**Why the consumer polls multiple times before getting records:**
```
Poll attempt 1 — got 0 records   ← rebalance still happening
Poll attempt 2 — got 0 records   ← partition assignment in progress
Poll attempt 3 — got 0 records   ← waiting for broker
Poll attempt 4 — got 6 records   ← partitions assigned, data arrives
```
On first `subscribe()`, Kafka runs the rebalance protocol (2-3 seconds).
During that handshake `poll()` returns empty. Only after partitions are assigned
does data start flowing. Always poll in a loop — never just once.

**Why you see 6 records instead of 3:**
The topic `demo-orders` accumulated messages from multiple runs. With
`auto.offset.reset=earliest` and no prior committed offset, the consumer reads
everything from the beginning — including messages from previous runs.
This demonstrates: **Kafka is a persistent log. Messages are NOT deleted when consumed.**

To run produce-only (watch count grow in Kafka UI without consuming):
```bash
.\gradlew :06-serialization:runJsonProducerOnly
```

Run this 3 times, then go to **Kafka UI → Topics → demo-orders → Partitions tab**
and watch the End Offset grow by 3 each time.

---

### Step 7 — Consumer Groups and Rebalancing

This is the most visual demo. It shows Kafka's horizontal scaling live.

```bash
.\gradlew :04-consumer-groups:runConsumerGroupDemo
```

**What it does:** Starts 3 consumer threads all sharing `group.id = "cg-demo"`.
Kafka assigns exactly 1 partition per consumer.

**What you will see:**

**Phase 1 — All 3 consumers join (10 second delay):**
```
Starting 3 consumers in group 'cg-demo'
Topic 'demo-basic' has 3 partitions — each consumer should get 1 partition
```
The 10-second gap is the rebalance protocol running.

**Phase 2 — Partition assignment logged:**
```
[Consumer-0] *** REBALANCE: Assigned partitions: [demo-basic-2]
[Consumer-1] *** REBALANCE: Assigned partitions: [demo-basic-0]
[Consumer-2] *** REBALANCE: Assigned partitions: [demo-basic-1]
```
3 consumers, 3 partitions, 1 each — perfectly balanced.

**Phase 3 — Each consumer reads ONLY its own partition:**
```
[Consumer-1] partition=0 offset=0 key=premium  value=order-0
[Consumer-1] partition=0 offset=1 key=premium  value=order-2
[Consumer-2] partition=1 offset=0 key=user-0   value=event-0
[Consumer-0] partition=2 offset=0 key=null     value=fire-and-forget
```
Consumer-1 ONLY reads partition 0. Consumer-2 ONLY reads partition 1. And so on.
**No two consumers ever read the same message.** Work is split automatically.

**Phase 4 — Shutdown errors (expected, not a real problem):**
```
Interrupted while waiting for consumer heartbeat thread to close
Failed to close coordinator with a timeout(ms)=30000
```
The demo shuts down via `thread.interrupt()` which interrupts the consumer's closing
handshake mid-way. The consumers DO stop correctly — BUILD SUCCESSFUL confirms this.
In production use `consumer.wakeup()` for graceful shutdown (as shown in module 3).

**The big picture:**
```
Topic: demo-basic (3 partitions)
┌──────────────┬──────────────┬──────────────┐
│ Partition 0  │ Partition 1  │ Partition 2  │
└──────┬───────┴──────┬───────┴──────┬───────┘
       │              │              │
  Consumer-1     Consumer-2     Consumer-0
  (thread-2)     (thread-3)     (thread-1)
        All in group: cg-demo
```

**Scaling rule:** To process a topic twice as fast, add more consumers to the same group.
Kafka rebalances automatically. Maximum useful consumers = number of partitions.

---

### Step 8 — Partitions and Offsets Explorer

```bash
.\gradlew :05-partitions-offsets:runPartitionExplorer
```

**What it does:** Explores partition metadata and demonstrates `seek()` —
one of Kafka's most powerful features.

Shows:
- Beginning offset and end offset per partition
- Consumer lag (how many messages behind the consumer is)
- `seekToBeginning()` — rewind to replay all messages from offset 0
- `seek(partition, offset)` — jump to any specific offset
- `seekToEnd()` — skip to the latest position

**Real-world use of seek:**
- Replay last 24 hours of events after a processing bug is fixed
- Skip ahead past a batch of corrupted messages
- Time-based replay using `offsetsForTimes()`

---

### Step 9 — Transactions (Exactly-Once)

```bash
.\gradlew :08-transactions:runTransactionalProducer
```

**What it does:** Sends a batch of messages inside a Kafka **transaction**.
Either ALL messages commit or NONE do — no partial writes ever reach consumers.

**The three delivery guarantees compared:**

| Guarantee | How to achieve | What can go wrong |
|-----------|---------------|-------------------|
| At-most-once | Commit offset before processing | Message processed 0 times if consumer crashes |
| At-least-once | Commit offset after processing | Message processed 2+ times if commit fails |
| Exactly-once | Transactional producer + `isolation.level=read_committed` | Higher latency, more complex |

**Transactional producer config:**
```java
props.put("enable.idempotence", true);         // dedup retries at producer level
props.put("transactional.id", "my-tx-id-1");  // unique per producer instance
props.put("acks", "all");
```

**Transaction flow:**
```java
producer.initTransactions();
try {
    producer.beginTransaction();
    producer.send(record1);
    producer.send(record2);
    producer.send(record3);
    producer.commitTransaction();   // all 3 visible atomically
} catch (Exception e) {
    producer.abortTransaction();    // none of them visible
}
```

**Real-world use:** Financial transfers, order processing pipelines,
any scenario where partial writes would leave data in an inconsistent state.

---

## 7. Understanding the Output

### Why offsets are not 0 on subsequent runs

Offsets are forever-increasing counters. They never reset between runs:
```
Partition 2 across runs:
  offset 0 → ORD-001  (run 1)
  offset 1 → ORD-003  (run 1)
  offset 2 → ORD-001  (run 2)   ← same data, new offset
  offset 3 → ORD-003  (run 2)
```

### Why same key always goes to same partition

```
hash("cust-A") % 3 = 2  → always partition 2
hash("cust-B") % 3 = 0  → always partition 0
```

This guarantees all messages for the same customer/entity are in order and
processed by the same consumer — critical for stateful processing.

### Why the consumer reads old messages on restart

With `auto.offset.reset=earliest` and no previously committed offset (new group.id),
the consumer starts from offset 0. This is intentional for learning — you see all history.

In production set `auto.offset.reset=latest` so a new consumer ignores historical data.

### Why the first few polls return 0 records

```
Poll attempt 1 — got 0 records  ← rebalance in progress
Poll attempt 2 — got 0 records  ← partition assignment being negotiated
Poll attempt 4 — got 6 records  ← ready
```
First `subscribe()` triggers a partition rebalance (2-5 seconds). Poll in a loop — never
assume the first poll will return data.

---

## 8. Delivery Semantics

| Guarantee | Producer config | Consumer pattern | Risk |
|-----------|----------------|-----------------|------|
| At-most-once | `acks=0` or `acks=1` | Commit BEFORE processing | Data loss on crash |
| At-least-once | `acks=all` | Commit AFTER processing | Duplicates on retry |
| Exactly-once | `enable.idempotence=true` + `transactional.id` | `isolation.level=read_committed` | Higher latency |

Most production systems use **at-least-once** and make their processing idempotent
(safe to apply the same message twice). True exactly-once is reserved for financial
or critical consistency scenarios.

---

## 9. Kafka UI — Visual Dashboard

Open **http://localhost:8080** in your browser after starting Docker.

| Section | What you see |
|---------|-------------|
| **Brokers** | Your single broker (id=1), its listeners, config |
| **Topics** | All topics, partition count, replication factor |
| **Topics → Partitions** | Start offset, end offset per partition (total message count) |
| **Topics → Messages** | Browse actual messages with key, value, offset, timestamp |
| **Consumer Groups** | All groups, their lag per partition, committed offsets |

**To see total message count:** Go to Topics → select topic → Partitions tab.
Look at End Offset. This grows with every producer run regardless of whether
consumers have read those messages.

**To see consumer lag:** Go to Consumer Groups → select group → you see how many
messages the consumer is behind per partition.

---

## 10. Useful CLI Commands

All commands run inside the Kafka container:

```bash
docker exec -it kafka-demo bash
```

```bash
# List all topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Create a topic manually
kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic my-topic --partitions 3 --replication-factor 1

# Describe a topic (partitions, leaders, ISR)
kafka-topics.sh --bootstrap-server localhost:9092 \
  --describe --topic demo-basic

# Delete a topic
kafka-topics.sh --bootstrap-server localhost:9092 \
  --delete --topic my-topic

# Produce messages from the console (type a message, press Enter)
kafka-console-producer.sh --bootstrap-server localhost:9092 \
  --topic demo-basic

# Produce with keys (separate key and value with a colon)
kafka-console-producer.sh --bootstrap-server localhost:9092 \
  --topic demo-basic \
  --property "parse.key=true" \
  --property "key.separator=:"

# Consume from the beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic demo-basic --from-beginning

# Consume and show keys
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic demo-basic --from-beginning \
  --property "print.key=true" \
  --property "key.separator= → "

# Check consumer group lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group demo-consumer-group

# List all consumer groups
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

# Reset consumer group offset to beginning (replay all messages)
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group demo-consumer-group \
  --topic demo-basic \
  --reset-offsets --to-earliest --execute
```

---

## 11. Key Configuration Reference

### Broker (server.properties)

| Config | Default | Description |
|--------|---------|-------------|
| `num.partitions` | 1 | Default partitions for new topics |
| `default.replication.factor` | 1 | Default replication for new topics |
| `log.retention.hours` | 168 (7 days) | How long to keep messages |
| `log.retention.bytes` | -1 (unlimited) | Max size of topic log |
| `log.segment.bytes` | 1 GB | Roll to new segment at this size |
| `min.insync.replicas` | 1 | Min replicas in ISR for writes to succeed |
| `auto.create.topics.enable` | true | Auto-create topics on first produce |

### Producer

| Config | Description |
|--------|-------------|
| `bootstrap.servers` | Comma-separated list of broker addresses |
| `key.serializer` | Class to convert key to bytes |
| `value.serializer` | Class to convert value to bytes |
| `acks` | Acknowledgement level: `0`, `1`, or `all` |
| `retries` | Number of retry attempts on transient failure |
| `enable.idempotence` | Prevent duplicate records on retry |
| `batch.size` | Max bytes per batch (default 16 KB) |
| `linger.ms` | Wait this long for more records before sending |
| `compression.type` | `none` / `gzip` / `snappy` / `lz4` / `zstd` |

### Consumer

| Config | Description |
|--------|-------------|
| `bootstrap.servers` | Comma-separated list of broker addresses |
| `key.deserializer` | Class to convert bytes back to key |
| `value.deserializer` | Class to convert bytes back to value |
| `group.id` | Consumer group name |
| `auto.offset.reset` | `earliest` / `latest` / `none` |
| `enable.auto.commit` | Auto-commit offsets (default true) |
| `max.poll.records` | Max records per poll() call |
| `session.timeout.ms` | Consumer declared dead after this with no heartbeat |
| `heartbeat.interval.ms` | How often to send heartbeat (must be < session.timeout/3) |
| `max.poll.interval.ms` | Max time between polls before consumer is dropped from group |
