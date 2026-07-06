# Chapter 41 — Handling Real-Time Streaming Data

## 1. Problem Statement

Modern systems generate **massive, continuous streams of data** that must be
processed in real time. Unlike traditional request-response architectures,
streaming platforms deal with unbounded datasets — data that never "ends."

**Real-world examples at scale:**

| Company  | Use Case                        | Scale                        |
|----------|---------------------------------|------------------------------|
| Uber     | Real-time trip tracking         | ~1 trillion events/day       |
| LinkedIn | Activity feed, metrics          | ~7 trillion events/day       |
| Netflix  | Real-time recommendations       | ~500 billion events/day      |
| Stripe   | Fraud detection on payments     | Millions of txns/sec         |
| Datadog  | Infrastructure monitoring       | Trillions of data points/day |

**Core use cases we must support:**

1. **Real-Time Analytics Dashboards** — Show live metrics (Uber's city view,
   Airbnb's booking pulse) with sub-second freshness
2. **IoT Sensor Platforms** — Ingest data from millions of connected devices,
   detect anomalies, trigger actuators
3. **Fraud Detection** — Score every credit card transaction in <100ms,
   block fraudulent ones before they complete
4. **Log Aggregation** — Collect logs from thousands of servers, make them
   searchable within seconds (like the ELK stack)

**The fundamental challenge:** Process millions of events per second with
sub-second end-to-end latency, while maintaining correctness guarantees
(no lost events, no double-counting), and scaling horizontally as load grows.

---

## 2. Requirements
### 2.1 Functional Requirements

| # | Requirement                                                      |
|---|------------------------------------------------------------------|
| 1 | Ingest data from multiple heterogeneous sources simultaneously   |
| 2 | Process, transform, enrich, and aggregate events in real time    |
| 3 | Store processed data for historical querying and dashboards      |
| 4 | Detect anomalies and trigger alerts within seconds               |
| 5 | Serve real-time dashboards with live-updating visualizations     |
| 6 | Support event replay — reprocess historical data on demand       |
| 7 | Join streaming data with reference/dimension data                |

### 2.2 Non-Functional Requirements

| Metric                | Target                                     |
|-----------------------|--------------------------------------------|
| End-to-end latency    | < 1 second (p95), < 5 seconds (p99)       |
| Ingestion throughput  | 1M+ events/sec sustained                  |
| Processing semantics  | Exactly-once or at-least-once              |
| Scalability           | Horizontal — add nodes to handle more load |
| Data retention        | 30 days hot, 1 year cold (object storage)  |
| Availability          | 99.99% uptime                              |
| Fault tolerance       | No data loss on node failure               |

---

## 3. Batch vs Stream Processing
### 3.1 Batch Processing

Batch processing collects data over a period, then processes it all at once.

```
  Time ─────────────────────────────────────────────►

  Events:  ● ● ● ● ● ● ● ● ● ● ● ● ● ● ● ● ● ●
           |<──── Batch 1 ────>|<──── Batch 2 ────>|
                    │                     │
                    ▼                     ▼
             ┌────────────┐       ┌────────────┐
             │ MapReduce/ │       │ MapReduce/ │
             │ Spark Job  │       │ Spark Job  │
             └─────┬──────┘       └─────┬──────┘
                   ▼                     ▼
              Results for            Results for
              Hour 1                 Hour 2
```

**Characteristics:**
- Processes data in large chunks (hourly, daily)
- Technologies: Hadoop MapReduce, Apache Spark (batch mode), Hive
- **High latency** (minutes to hours) but **very high throughput**
- Operates on **complete data** — all events for the period are available
- Simple to reason about — clear input boundaries

### 3.2 Stream Processing

Stream processing handles each event as it arrives, continuously.

```
  Time ─────────────────────────────────────────────►

  Events:  ● ─► ● ─► ● ─► ● ─► ● ─► ● ─► ● ─► ●
           │    │    │    │    │    │    │    │
           ▼    ▼    ▼    ▼    ▼    ▼    ▼    ▼
       ┌─────────────────────────────────────────┐
       │     Continuous Stream Processor          │
       │     (Flink / Kafka Streams / Spark SS)   │
       └─────────────┬───────────────────────────┘
                     │
                     ▼
              Continuous output
              (results update in real time)
```

**Characteristics:**
- Processes data as it arrives (per-event or micro-batch)
- Technologies: Apache Flink, Kafka Streams, Spark Structured Streaming
- **Low latency** (milliseconds to seconds)
- **Continuous processing** — never "finishes"
- Possibly **incomplete data** — late events may arrive after processing

### 3.3 Lambda Architecture

Lambda Architecture runs **both** batch and stream processing in parallel.

```
                         ┌─────────────────────────────────┐
                         │         DATA SOURCES             │
                         │  (logs, clicks, transactions)    │
                         └──────────────┬──────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
                    ▼                   ▼                   │
          ┌─────────────────┐  ┌─────────────────┐         │
          │   BATCH LAYER   │  │   SPEED LAYER   │         │
          │                 │  │                  │         │
          │ ● Stores master │  │ ● Processes new  │         │
          │   dataset       │  │   data in real   │         │
          │ ● Runs batch    │  │   time           │         │
          │   jobs (hourly) │  │ ● Approximate    │         │
          │ ● Complete &    │  │   but fast       │         │
          │   accurate      │  │ ● Compensates    │         │
          │                 │  │   for batch lag  │         │
          └────────┬────────┘  └────────┬─────────┘         │
                   │                    │                   │
                   ▼                    ▼                   │
          ┌──────────────────────────────────────┐         │
          │          SERVING LAYER               │         │
          │                                      │         │
          │  Merges batch views + real-time views │         │
          │  to answer queries                   │         │
          └──────────────────────────────────────┘
```

**The problem with Lambda:** You must maintain **two separate codepaths**
(batch + streaming) that produce the same results. This leads to:
- Double the development effort
- Subtle inconsistencies between batch and real-time results
- Complex merging logic in the serving layer
- Twice the operational burden

### 3.4 Kappa Architecture (The Modern Approach)

Kappa Architecture uses **only stream processing**, with an immutable
replayable event log (Kafka) as the single source of truth.

```
                    ┌───────────────────────────────┐
                    │         DATA SOURCES           │
                    └──────────────┬────────────────┘
                                   │
                                   ▼
                    ┌───────────────────────────────┐
                    │      IMMUTABLE EVENT LOG       │
                    │         (Apache Kafka)         │
                    │                               │
                    │  All events stored durably    │
                    │  Replayable from any offset   │
                    │  Retained for days/weeks      │
                    └──────────────┬────────────────┘
                                   │
                          ┌────────┴────────┐
                          │                 │
                          ▼                 ▼
                   ┌─────────────┐  ┌──────────────┐
                   │  STREAM     │  │  STREAM      │
                   │  JOB v1     │  │  JOB v2      │
                   │  (current)  │  │  (reprocess) │
                   └──────┬──────┘  └──────┬───────┘
                          │                │
                          ▼                ▼
                   ┌─────────────────────────────┐
                   │       SERVING LAYER          │
                   │  (Druid / ClickHouse / etc.) │
                   └─────────────────────────────┘
```

**How reprocessing works in Kappa:**
1. Deploy a new version of your stream job (v2)
2. Point it at the beginning of the Kafka log
3. Let it reprocess all historical events
4. Once caught up, swap traffic from v1 → v2
5. Shut down v1

**Why Kappa is recommended for new systems:**
- Single codebase — write processing logic once
- Simpler architecture, fewer moving parts
- Kafka provides the replayable log needed for reprocessing
- Easier to maintain, debug, and evolve

### 3.5 Lambda vs Kappa Comparison

| Aspect              | Lambda Architecture         | Kappa Architecture          |
|---------------------|-----------------------------|-----------------------------|
| Codepaths           | Two (batch + stream)        | One (stream only)           |
| Complexity          | High                        | Lower                       |
| Reprocessing        | Batch layer re-runs         | Replay Kafka log            |
| Accuracy            | Batch = accurate            | Stream = accurate           |
| Latency             | Speed layer = fast          | Stream = fast               |
| Maintenance         | Two systems to maintain     | One system                  |
| Prerequisite        | Any storage                 | Replayable log (Kafka)      |
| Recommendation      | Legacy / niche              | **Default for new systems** |

---

## 4. Data Ingestion Layer
### 4.1 Apache Kafka as the Backbone

Kafka is the de facto standard for streaming data ingestion. It is a
distributed, durable, replayable commit log.

```
  ┌──────────────── KAFKA TOPIC: "transactions" ──────────────────┐
  │                                                                │
  │  Partition 0: [msg0][msg3][msg6][msg9 ][msg12] ──────►        │
  │  Partition 1: [msg1][msg4][msg7][msg10][msg13] ──────►        │
  │  Partition 2: [msg2][msg5][msg8][msg11][msg14] ──────►        │
  │                                                                │
  └────────────────────────────────────────────────────────────────┘
          ▲  ▲  ▲                              │  │  │
          │  │  │                              ▼  ▼  ▼
     ┌────┴──┴──┴────┐                ┌────────┴──┴──┴────────┐
     │   PRODUCERS    │                │   CONSUMER GROUP       │
     │  (write msgs)  │                │  Consumer A → Part 0   │
     │                │                │  Consumer B → Part 1   │
     │                │                │  Consumer C → Part 2   │
     └────────────────┘                └───────────────────────┘
```

**Key Kafka concepts:**
- **Topic:** A named feed of events (like "transactions" or "clicks")
- **Partition:** A topic is split into partitions for parallelism
- **Consumer Group:** Multiple consumers that share the work, each reading
  from different partitions
- **Offset:** Each message in a partition has a sequential ID (offset)
- **Retention:** Messages are kept for a configurable period (e.g., 7 days)

**Why Kafka for streaming:**
- **Durable:** Data is replicated across brokers (default RF=3)
- **Replayable:** Consumers can seek back to any offset
- **High throughput:** Single cluster handles millions of msgs/sec
- **Decoupling:** Producers and consumers are fully independent

**Partition strategy:** By entity ID (e.g., `user_id`) → guarantees ordering
per entity. By hash → uniform distribution. By time → useful for retention.

### 4.2 Ingestion Patterns

```
  ┌──────────────────────────────────────────────────────────┐
  │                   INGESTION PATTERNS                     │
  │                                                          │
  │  1. DIRECT PRODUCER                                      │
  │     App ──── Kafka Producer API ────► Kafka              │
  │                                                          │
  │  2. AGENT-BASED                                          │
  │     Server ── Filebeat/Fluentd ────► Kafka               │
  │     (collects logs, metrics)                             │
  │                                                          │
  │  3. API GATEWAY BRIDGE                                   │
  │     Client ── HTTP POST ── API GW ──► Kafka              │
  │     (REST/gRPC to Kafka bridge)                          │
  │                                                          │
  │  4. CDC (Change Data Capture)                            │
  │     Database ── Debezium ──────────► Kafka               │
  │     (captures INSERT/UPDATE/DELETE)                      │
  │                                                          │
  │  5. IoT PROTOCOL BRIDGE                                  │
  │     Sensors ── MQTT Broker ────────► Kafka               │
  │     (MQTT to Kafka connector)                            │
  └──────────────────────────────────────────────────────────┘
```

### 4.3 Full Ingestion Architecture

```
  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐
  │ Mobile   │  │ Web App  │  │  IoT     │  │ Databases  │
  │ Apps     │  │ Servers  │  │ Sensors  │  │ (CDC)      │
  └────┬─────┘  └────┬─────┘  └────┬─────┘  └─────┬──────┘
       │              │              │               │
       ▼              ▼              ▼               ▼
  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐
  │ API     │  │ Fluentd  │  │ MQTT     │  │  Debezium  │
  │ Gateway │  │ Agent    │  │ Broker   │  │  Connector │
  └────┬────┘  └────┬─────┘  └────┬─────┘  └─────┬──────┘
       │             │              │               │
       └──────┬──────┴──────┬───────┴───────┬──────┘
              │             │               │
              ▼             ▼               ▼
  ┌──────────────────────────────────────────────────────┐
  │              APACHE KAFKA CLUSTER                    │
  │                                                      │
  │  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
  │  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │  ...      │
  │  └──────────┘  └──────────┘  └──────────┘           │
  │                                                      │
  │  Topics: transactions, user-events, sensor-data,     │
  │          db-changes, logs                            │
  └──────────────────────────────────────────────────────┘
              │             │               │
              ▼             ▼               ▼
        Stream Processing / Storage / Alerting
```

---

## 5. Stream Processing Engine — Deep Dive
### 5.1 Apache Flink (The Gold Standard)

Flink is the most capable stream processor for serious production workloads.

**Key features that set Flink apart:**

**Event-time processing:**
```
  Event occurs     Event arrives      Event processed
  at sensor        at Kafka           by Flink
       │                │                  │
  t=10:00:00       t=10:00:03         t=10:00:04
       │                │                  │
       │    network     │    consumer      │
       │◄── delay ─────►│◄── lag ─────────►│
       │                │                  │
  Flink uses t=10:00:00 (event time), NOT t=10:00:04 (processing time)
```

**Checkpointing for exactly-once semantics:**
```
  ┌─────────────────────────────────────────────────────┐
  │                FLINK CHECKPOINTING                   │
  │                                                      │
  │  Kafka ──► [Operator A] ──► [Operator B] ──► Sink   │
  │                │                  │                   │
  │          state: {count:42}  state: {sum:1000}        │
  │                │                  │                   │
  │                └─────────┬────────┘                   │
  │                          │ checkpoint                 │
  │                          ▼                            │
  │               ┌─────────────────────┐                │
  │               │   Durable Storage   │                │
  │               │  (S3 / HDFS / GCS)  │                │
  │               │                     │                │
  │               │  checkpoint-00042:  │                │
  │               │   kafka_offsets:    │                │
  │               │    part0=offset99   │                │
  │               │   op_A_state:       │                │
  │               │    {count:42}       │                │
  │               │   op_B_state:       │                │
  │               │    {sum:1000}       │                │
  │               └─────────────────────┘                │
  └─────────────────────────────────────────────────────┘

  On failure: restore state + replay Kafka from offset 99
  → exactly-once: no events lost or double-counted
```

**Savepoints** allow you to upgrade code, rescale, or migrate
without losing state — critical for production operations.

### 5.2 Apache Kafka Streams

Kafka Streams is a **library** (not a cluster) that runs inside your
application process. No separate infrastructure needed.

```
  ┌─────────────────────────────────────────────┐
  │            YOUR APPLICATION (JVM)            │
  │                                              │
  │  ┌────────────────────────────────────────┐  │
  │  │         Kafka Streams Library           │  │
  │  │                                         │  │
  │  │  KStream<K,V> ──► filter() ──► map()   │  │
  │  │       │              │           │      │  │
  │  │       │         groupByKey()     │      │  │
  │  │       │              │           │      │  │
  │  │       │         aggregate()      │      │  │
  │  │       │              │           │      │  │
  │  │       │         KTable<K,V>      │      │  │
  │  │       │         (materialized)   │      │  │
  │  │       │              │           │      │  │
  │  │  RocksDB state   Interactive    to()    │  │
  │  │  (local disk)    Queries        │       │  │
  │  │                  (REST API)     │       │  │
  │  └──────────────────────┬──────────┘       │  │
  │                         │                   │  │
  └─────────────────────────┼───────────────────┘  │
                            │                      │
                            ▼                      │
                   Output Kafka Topic              │
                                                   │
     Scale: just run more instances of your app ───┘
```

**Key concepts:**
- **KStream:** An unbounded stream of events (insert semantics)
- **KTable:** A changelog stream (upsert semantics, like a table)
- **Interactive Queries:** Query the local state store via REST API
- **Scaling:** Deploy more instances of your app — Kafka Streams
  automatically rebalances partitions across instances

### 5.3 Apache Spark Structured Streaming

Spark Structured Streaming uses a **micro-batch model** — it collects
events into small batches (100ms–seconds) and processes them together.

```
  Events:  ● ● ● │ ● ● ● │ ● ● ● │ ● ● ● │
                  │       │       │       │
           Batch 1│Batch 2│Batch 3│Batch 4│
                  │       │       │       │
                  ▼       ▼       ▼       ▼
           ┌──────────────────────────────────┐
           │     Spark Structured Streaming    │
           │                                   │
           │  Same DataFrame/SQL API as batch  │
           │  Integrates with Spark ML         │
           │  Supports joins, aggregations     │
           └──────────────────────────────────┘
```

**Best for:** Teams already using Spark for batch + ML that want to
add streaming without learning a new framework.

### 5.4 Comparison Table

| Feature               | Apache Flink          | Kafka Streams          | Spark Streaming       |
|-----------------------|-----------------------|------------------------|-----------------------|
| **Model**             | True per-event        | Per-event (library)    | Micro-batch           |
| **Latency**           | ~10ms                 | ~10ms                  | ~100ms+               |
| **Exactly-once**      | Yes (checkpoints)     | Yes (Kafka txns)       | Yes (write-ahead log) |
| **Windowing**         | Advanced (session,    | Basic (tumbling,       | Good (tumbling,       |
|                       | global, custom)       | sliding, session)      | sliding)              |
| **State management**  | RocksDB + checkpoints | RocksDB + changelogs   | In-memory/HDFS        |
| **Deployment**        | Dedicated cluster     | Embedded in app        | Spark cluster         |
| **Operational cost**  | Medium-High           | Low                    | High                  |
| **ML integration**    | Basic (PMML/ONNX)    | None built-in          | Excellent (Spark ML)  |
| **Language support**  | Java, Scala, Python   | Java, Scala            | Java, Scala, Python, R|
| **Best for**          | Complex event proc.   | Simple transformations | ML + streaming        |

**Rule of thumb:**
- **Flink** → Complex event processing, large state, advanced windowing
- **Kafka Streams** → Simple to medium transformations, microservice style
- **Spark Streaming** → When you already use Spark and need ML integration

---

## 6. Windowing and Time Semantics
### 6.1 Tumbling Windows

Fixed-size, non-overlapping windows. Each event belongs to exactly one window.

```
  Time ──────────────────────────────────────────────────►

  Events:   ● ●  ●   ● ● ●  ●  ● ●   ● ●  ●  ● ● ●
            │           │           │           │
  Window 1: |← 1 min ──|           |           |
  Window 2:             |← 1 min ──|           |
  Window 3:                         |← 1 min ──|
  Window 4:                                     |← 1 min ──

  Example: "Count transactions per 1-minute window"
    Window 1 (00:00-01:00): count = 3
    Window 2 (01:00-02:00): count = 4
    Window 3 (02:00-03:00): count = 3
    Window 4 (03:00-04:00): count = 5
```

**Use case:** Periodic aggregations — "events per minute", "revenue per hour."

### 6.2 Sliding Windows

Fixed-size windows that overlap, sliding by a specified interval.

```
  Time ──────────────────────────────────────────────────►

  Events:   ● ● ●  ● ● ● ●  ● ● ●  ● ● ●  ● ● ● ●

  Window A: |←──────── 5 min ─────────|
  Window B:      |←──────── 5 min ─────────|
  Window C:           |←──────── 5 min ─────────|
  Window D:                |←──────── 5 min ─────────|
            |←1m→|←1m→|

  Slides every 1 minute, window size = 5 minutes
  Each event belongs to MULTIPLE windows (up to 5 in this case)
```

**Use case:** Moving averages — "average response time over last 5 minutes,
updated every minute."

### 6.3 Session Windows

Gap-based windows. A window closes when there is no activity for a
specified gap duration. Window sizes vary.

```
  Time ──────────────────────────────────────────────────────────►

  User activity:  ● ● ●  ●               ● ●  ● ●         ● ● ●
                  │          │            │          │       │       │
  Session 1:      |── active ──|          |          |       |       |
                               30min gap
  Session 2:                              |── active ──|     |       |
                                                     30min gap
  Session 3:                                                 |─ active ─|

  Gap = 30 minutes of inactivity → close the session
  Session 1: 4 events, duration 3 min
  Session 2: 4 events, duration 4 min
  Session 3: 3 events, duration 2 min
```

**Use case:** User session analysis — "how long does a user session last?"
"how many pages per session?"

### 6.4 Event Time vs Processing Time

```
  REAL WORLD                                PROCESSING SYSTEM
  ──────────                                ──────────────────

  Event A occurs at 10:00:00  ─── network ──► Arrives at 10:00:02
  Event B occurs at 10:00:01  ─── delay   ──► Arrives at 10:00:05 (LATE!)
  Event C occurs at 10:00:02  ─── fast    ──► Arrives at 10:00:03

  Processing-time order:  A, C, B  (wrong! B was delayed)
  Event-time order:       A, B, C  (correct! based on when events occurred)
```

**Why event time matters:**
- Events frequently arrive **out of order** due to network delays
- Mobile/IoT devices may be offline and batch-send events later
- Processing-time windows would assign events to **wrong windows**

**Watermarks: "I believe all events before time T have arrived"**

```
  Event time ──────────────────────────────────────────────►

  Events arriving:  [t=5] [t=3] [t=7] [t=4] [t=9] [t=6] [t=12]
                                                          │
  Watermark:     W=2   W=3   W=3   W=3   W=4   W=4   W=6
                                                          │
                          ┌───────────────────────────────┘
                          │
                          ▼
            "All events with t <= 6 have likely arrived"
            → Safe to close the window [0:00 - 5:00]
```

**Late event handling strategies:**
1. **Drop:** Ignore events that arrive after the watermark (simplest)
2. **Update:** Recompute the window result (most accurate)
3. **Side-output:** Send late events to a separate stream for special handling

---

## 7. State Management in Streaming

Streaming applications are often **stateful** — they need to remember
information across events.

**Stateful operations:**
- Counts and sums (running aggregations)
- Joins (matching events from two streams)
- Pattern detection (sequence of events matching a rule)
- Sessionization (grouping events into sessions)
- Deduplication (remembering which events we've seen)

```
  ┌────────────────────────────────────────────────────────┐
  │              STATE MANAGEMENT IN FLINK                  │
  │                                                         │
  │  Events ──►┌──────────────────┐──► Output               │
  │            │   Operator        │                         │
  │            │                   │                         │
  │            │  ┌─────────────┐  │                         │
  │            │  │ State Store │  │                         │
  │            │  │             │  │                         │
  │            │  │ key1: 42    │  │   State Backend:        │
  │            │  │ key2: 17    │  │   ● HashMapStateBackend │
  │            │  │ key3: 99    │  │     (in-memory, fast,   │
  │            │  │ ...         │  │      limited by RAM)    │
  │            │  │ keyN: 5     │  │   ● EmbeddedRocksDB    │
  │            │  └──────┬──────┘  │     (on disk, larger    │
  │            │         │         │      state, slower)     │
  │            └─────────┼─────────┘                         │
  │                      │ checkpoint (async)                │
  │                      ▼                                   │
  │            ┌──────────────────┐                          │
  │            │  S3 / HDFS / GCS │                          │
  │            │  (durable copy)  │                          │
  │            └──────────────────┘                          │
  │                                                         │
  │  Recovery: restore state from checkpoint                │
  │            + replay Kafka from checkpointed offset      │
  └────────────────────────────────────────────────────────┘
```

**State size can be enormous:**
- Sessionization for 100M users → billions of keys
- Deduplication with 24h window → every event ID for 24 hours
- Feature computation for ML → sliding window features per entity

**Checkpointing frequency trade-off:**
- Frequent checkpoints (every 10s) → faster recovery, more I/O overhead
- Infrequent checkpoints (every 5min) → less overhead, longer recovery
- Typical production setting: every 30–60 seconds

---

## 8. Real-Time Storage and Serving Layer

### 8.1 For Time-Series Data

Time-series databases are optimized for timestamped data points.

```
  Processed stream ──► ┌──────────────────────┐
                       │   TIME-SERIES DB      │
                       │   (InfluxDB /          │
                       │    TimescaleDB /       │
                       │    QuestDB)            │
                       │                        │
                       │  Optimized for:        │
                       │  ● Time-range queries  │
                       │  ● Downsampling        │
                       │  ● Retention policies  │
                       │  ● High write speed    │
                       └───────────┬────────────┘
                                   │
                              Grafana dashboards
```

**Best for:** IoT sensor data, infrastructure metrics, time-based analytics.

### 8.2 For Real-Time Analytics (OLAP)

Real-time OLAP databases serve sub-second analytical queries on fresh data.

```
  Kafka ──► ┌──────────────────────────────────────┐
            │   REAL-TIME OLAP ENGINE               │
            │   (Apache Druid / ClickHouse /        │
            │    Apache Pinot)                      │
            │                                       │
            │  ● Columnar storage (fast scans)      │
            │  ● Pre-aggregation at ingestion       │
            │  ● Sub-second queries on billions     │
            │    of rows                            │
            │  ● Ingests directly from Kafka        │
            │  ● Powers real-time dashboards        │
            └────────────────┬──────────────────────┘
                             │
                        ┌────┴────┐
                        │ Superset │  (or Grafana, Tableau,
                        │ Looker   │   custom dashboards)
                        └──────────┘
```

**Who uses what:**
- **Apache Druid:** Airbnb, Netflix, Walmart — real-time dashboards
- **ClickHouse:** Cloudflare, Uber, GitLab — log analytics at scale
- **Apache Pinot:** LinkedIn, Uber, Stripe — user-facing analytics

### 8.3 For Search and Exploration

```
  Kafka ──► ┌──────────────────────────────────────┐
            │   ELASTICSEARCH                       │
            │                                       │
            │  ● Full-text search on events         │
            │  ● Log analytics (ELK stack)          │
            │  ● Anomaly exploration                │
            │  ● Flexible querying (KQL / Lucene)   │
            └────────────────┬──────────────────────┘
                             │
                        ┌────┴────┐
                        │ Kibana  │  (dashboards + search UI)
                        └─────────┘
```

### 8.4 Storage Decision Table

| Use Case                  | Best Storage             | Query Type             |
|---------------------------|--------------------------|------------------------|
| IoT sensor time-series    | TimescaleDB / InfluxDB   | Time-range, downsample |
| Real-time dashboards      | Druid / ClickHouse       | Aggregation, GROUP BY  |
| User-facing analytics     | Apache Pinot             | Low-latency OLAP       |
| Log search/exploration    | Elasticsearch            | Full-text search       |
| Feature store (ML)        | Redis / DynamoDB         | Key-value lookup       |
| Long-term archive         | S3 / GCS (Parquet)       | Batch queries (Spark)  |

---

## 9. Complete System: Real-Time Fraud Detection

This section walks through a full production architecture for detecting
fraudulent credit card transactions in real time.

**Goal:** Score every transaction in <100ms, block fraud before settlement.

```
  ┌──────────┐  ┌──────────┐  ┌──────────┐
  │ Payment  │  │ Payment  │  │ Payment  │
  │ Gateway  │  │ Gateway  │  │ Gateway  │
  │ (Visa)   │  │ (MC)     │  │ (Amex)   │
  └────┬─────┘  └────┬─────┘  └────┬─────┘
       └──────┬──────┴──────┬──────┘
              ▼             ▼
  ┌──────────────────────────────────────────┐
  │     KAFKA: "raw-transactions"             │
  │     (partitioned by card_id)              │
  └──────────────────┬───────────────────────┘
                     ▼
  ┌──────────────────────────────────────────────────────────┐
  │                  APACHE FLINK                              │
  │                                                            │
  │  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐  │
  │  │   Feature     │  │  ML Model    │  │  Rule Engine   │  │
  │  │   Extraction  │  │  Inference   │  │                │  │
  │  │ ● txn count  │  │ ● XGBoost / │  │ ● amount >    │  │
  │  │   last 1hr   │  │   Neural Net │  │   $10,000     │  │
  │  │ ● avg amount │  │ ● Score:     │  │ ● 3+ failed  │  │
  │  │ ● geo dist.  │  │   0.0 - 1.0  │  │   attempts   │  │
  │  │ ● merchant   │  │              │  │ ● velocity   │  │
  │  │   category   │  │              │  │   check      │  │
  │  └──────┬───────┘  └──────┬───────┘  └──────┬─────────┘  │
  │         └────────┬────────┴──────┬───────────┘             │
  │           ┌──────┴──────┐  ┌─────┴──────┐                  │
  │           │  APPROVE    │  │  FRAUD!    │                  │
  │           │  score<0.3  │  │  score>0.7 │                  │
  │           └──────┬──────┘  └─────┬──────┘                  │
  └──────────────────┼───────────────┼─────────────────────────┘
                     ▼               ▼
  ┌──────────────────────┐  ┌───────────────────────┐
  │ Kafka: "approved"    │  │ Kafka: "fraud-alerts"  │
  └──────────┬───────────┘  └──────────┬────────────┘
             ▼                         ▼
  ┌───────────────────┐    ┌────────────────────────┐
  │ ClickHouse        │    │ Alert Service           │
  │ (analytics)       │    │ ● Block transaction     │
  │ ● Fraud rates     │    │ ● Notify cardholder     │
  │ ● Trends          │    │ ● Flag for review       │
  └───────────────────┘    └────────────────────────┘
  ┌───────────────────┐    ┌────────────────────────┐
  │ Grafana           │    │ Elasticsearch           │
  │ (metrics dash)    │    │ (investigation/search)  │
  └───────────────────┘    └────────────────────────┘

  END-TO-END LATENCY BUDGET:
  ┌────────────────────────────────────────────────┐
  │ Kafka produce: ~5ms   │  ML inference: ~20ms   │
  │ Kafka consume: ~5ms   │  Rule engine:  ~5ms    │
  │ Feature extract: ~10ms│  Alert dispatch: ~5ms  │
  │ ────────────────────────────────────────────── │
  │ TOTAL: ~50ms (well under 100ms target)         │
  └────────────────────────────────────────────────┘
```

**Key design decisions:**
1. **Partition by card_id** — All transactions for one card go to the same
   partition → the Feature Extraction operator sees them in order
2. **State in Flink** — Transaction history (last 1hr) is kept in Flink's
   RocksDB state backend, checkpointed every 30s
3. **ML model served in-process** — Model loaded into Flink (ONNX/PMML),
   no network hop for scoring → <20ms inference
4. **Dual sink** — ClickHouse for analytics, Elasticsearch for investigation

---

## 10. Complete System: Real-Time IoT Platform

**Goal:** Ingest data from 1 million sensors, detect anomalies, visualize
in real time.

```
  ┌───────┐ ┌───────┐ ┌───────┐           ┌───────┐
  │Sensor │ │Sensor │ │Sensor │  ...      │Sensor │  (1M sensors)
  │  #1   │ │  #2   │ │  #3   │           │ #1M   │
  └───┬───┘ └───┬───┘ └───┬───┘           └───┬───┘
      └────┬────┴────┬────┴────────────┬───────┘
           ▼         ▼                 ▼
  ┌──────────────────────────────────────────────┐
  │       MQTT BROKER CLUSTER                     │
  │  (EMQX / HiveMQ) — millions of connections   │
  └──────────────────┬───────────────────────────┘
                     ▼
  ┌──────────────────────────────────────────────┐
  │  KAFKA CONNECT (MQTT Source Connector)        │
  │  Bridges MQTT topics → Kafka topics           │
  └──────────────────┬───────────────────────────┘
                     ▼
  ┌──────────────────────────────────────────────┐
  │       KAFKA: "sensor-readings"                │
  │  (partitioned by sensor_region)               │
  └──────────┬───────────┬───────────┬───────────┘
             ▼           ▼           ▼
  ┌────────────┐ ┌─────────────┐ ┌──────────────┐
  │ Flink #1:  │ │ Flink #2:   │ │ Flink #3:    │
  │ Aggregate  │ │ Anomaly     │ │ Geo-fence    │
  │ ● 1m avg  │ │ ● Z-score   │ │ ● Zone exit  │
  │ ● 5m roll │ │ ● Spike det │ │ ● Notify ops │
  │ ● hourly  │ │ ● Dead snsr │ │              │
  └─────┬──────┘ └─────┬───────┘ └──────┬───────┘
        ▼               ▼                ▼
  ┌───────────┐  ┌───────────┐  ┌──────────────┐
  │TimescaleDB│  │ PagerDuty │  │ Notification │
  │ (storage) │  │ (alerts)  │  │ (SMS/Push)   │
  └─────┬─────┘  └───────────┘  └──────────────┘
        ▼
  ┌──────────────────────────────────────────────┐
  │            GRAFANA DASHBOARDS                 │
  │  Temperature (Zone A)   ▁▃▅▇▅▃▁▃▅█          │
  │  Humidity (Zone B)      ▃▃▅▅▇▇▅▅▃▃          │
  │  Active Sensors: 999,847 / 1,000,000         │
  │  Alerts (24h): 142                           │
  └──────────────────────────────────────────────┘
  ┌──────────────────────────────────────────────┐
  │  COLD STORAGE (S3 / GCS)                      │
  │  Parquet files, 1-year retention              │
  └──────────────────────────────────────────────┘
```

**Scaling considerations for 1M sensors:**
- Each sensor sends ~1 reading/sec → 1M events/sec total
- MQTT broker cluster: 3-5 nodes handle millions of connections
- Kafka: 50+ partitions for `sensor-readings` topic
- Flink: scaled to ~20 TaskManagers (depends on event complexity)
- TimescaleDB: hypertables with time-based partitioning, auto-compression

---

## 11. Monitoring the Streaming Pipeline

A streaming pipeline is only as good as its monitoring. If you can't see it, you can't fix it.

### 11.1 Critical Metrics

```
  ┌─────────────────────────────────────────────────────────┐
  │          STREAMING PIPELINE HEALTH DASHBOARD             │
  │                                                          │
  │  CONSUMER LAG            ◄── MOST CRITICAL METRIC        │
  │  Lag = Latest Kafka Offset - Consumer's Offset           │
  │  Healthy: lag < 1,000  ✅   Warning: < 100K  ⚠️          │
  │  Critical: > 100,000   🔴   Growing = can't keep up      │
  │                                                          │
  │  THROUGHPUT (events/sec per stage)                        │
  │  Ingestion:   ████████████████████  1.2M/s               │
  │  Processing:  ███████████████████   1.1M/s               │
  │  Sink write:  ██████████████████    1.0M/s               │
  │  If processing < ingestion → backpressure building       │
  │                                                          │
  │  PROCESSING LATENCY                                      │
  │  Event-time skew = current_time - event_time             │
  │  p50: 200ms    p95: 800ms    p99: 2.1s                  │
  │  If p99 > SLA → investigate slow operators               │
  │                                                          │
  │  CHECKPOINT HEALTH                                       │
  │  Last: 15s ago   Duration: 3.2s   Size: 4GB             │
  │  Failures (24h): 0                                       │
  │  Failing → state too large or storage I/O slow           │
  │                                                          │
  │  BACKPRESSURE                                            │
  │  Operator A (parse):     OK   [▓▓░░░░░░░░] 20%          │
  │  Operator B (enrich):    OK   [▓▓▓▓░░░░░░] 40%          │
  │  Operator C (ML score):  HIGH [▓▓▓▓▓▓▓▓▓░] 90%  ◄──    │
  │  Operator D (sink):      OK   [▓▓▓░░░░░░░] 30%          │
  │  Bottleneck: ML scoring! → Increase parallelism          │
  └─────────────────────────────────────────────────────────┘
```

### 11.2 Alerting Rules

| Metric                          | Threshold        | Action                      |
|---------------------------------|------------------|-----------------------------|
| Consumer lag growing for 5 min  | > 50K messages   | Page on-call, scale out     |
| Checkpoint failure              | 2 consecutive    | Page on-call immediately    |
| Processing latency p99          | > 5 seconds      | Investigate slow operator   |
| Throughput drop                 | > 30% decrease   | Check upstream sources      |
| Flink TaskManager restarts      | > 3 in 10 min    | Check OOM / data skew       |
| Kafka broker disk usage         | > 80%            | Expand storage or retention |

---

## 12. Key Takeaways

```
  ┌──────────────────────────────────────────────────────────────┐
  │                      KEY TAKEAWAYS                            │
  │                                                               │
  │  1. Use KAPPA ARCHITECTURE for new systems — single stream    │
  │     processing codebase with Kafka as the replayable source   │
  │     of truth. Avoid Lambda's dual-codebase complexity.        │
  │                                                               │
  │  2. KAFKA is the universal backbone for streaming. Its        │
  │     durability, replayability, and decoupling make it the     │
  │     default choice for event ingestion and transport.         │
  │                                                               │
  │  3. Choose your STREAM PROCESSOR based on complexity: Kafka   │
  │     Streams for simple transforms, Flink for complex stateful │
  │     processing, Spark Streaming for tight ML integration.     │
  │                                                               │
  │  4. EVENT TIME > PROCESSING TIME. Always use event-time       │
  │     semantics with watermarks — processing time leads to      │
  │     incorrect results in virtually all real scenarios.         │
  │                                                               │
  │  5. STATE MANAGEMENT is the hardest part. Use checkpointing   │
  │     (Flink) or changelogs (Kafka Streams). Plan for state     │
  │     sizes — they can grow to terabytes with RocksDB.          │
  │                                                               │
  │  6. Choose STORAGE by query pattern: time-series DB for       │
  │     IoT/metrics, real-time OLAP (ClickHouse/Druid/Pinot)     │
  │     for dashboards, Elasticsearch for search/exploration.     │
  │                                                               │
  │  7. CONSUMER LAG is the single most important health metric.  │
  │     If lag is growing, scale out or find the bottleneck       │
  │     before the pipeline falls irrecoverably behind.           │
  │                                                               │
  │  8. Design for REPLAY from day one. Reprocessing historical   │
  │     events is essential for bug fixes, new features, and ML   │
  │     model retraining. Kafka retention + savepoints enable it. │
  └──────────────────────────────────────────────────────────────┘
```

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
