# Chapter 39: Designing a Highly Available Distributed Database

## 1. Problem Statement

Design a database that **survives node failures, network partitions, and even entire
datacenter outages** while maintaining strong consistency guarantees and a familiar
SQL interface. This is one of the hardest problems in distributed systems — and some
of the most impressive engineering achievements of the last decade have been in this space.

### The Core Tension: CAP Theorem

The CAP theorem (Brewer, 2000) states that a distributed data store can only provide
**two of the following three guarantees simultaneously**:

```
                    ┌──────────────────────┐
                    │     Consistency      │
                    │  (Every read gets    │
                    │   the latest write)  │
                    └──────────┬───────────┘
                               │
                  ┌────────────┼────────────┐
                  │            │            │
                  │    Pick    │    Pick    │
                  │     CP     │     CA     │
                  │            │            │
         ┌────────┴──────┐  ┌─┴───────────────┐
         │  Partition    │  │  Availability   │
         │  Tolerance    │  │  (Every request │
         │  (System works│  │   gets a non-   │
         │   despite net │  │   error resp.)  │
         │   partitions) │  │                 │
         └───────────────┘  └─────────────────┘

    CP Systems: Spanner, CockroachDB   (sacrifice availability briefly)
    AP Systems: Cassandra, DynamoDB    (sacrifice consistency)
    CA Systems: Traditional RDBMS      (single node, no partitions)
```

Since network partitions **will** happen in any distributed system, we effectively
choose between **CP** (consistency + partition tolerance) and **AP** (availability +
partition tolerance). Our design chooses **CP** — because for a database, returning
wrong data is worse than briefly being unavailable.

### Real-World Systems

| System       | Company  | Key Innovation                                    |
|-------------|----------|---------------------------------------------------|
| Spanner     | Google   | Global transactions via TrueTime (atomic clocks)  |
| CockroachDB | Cockroach Labs | PostgreSQL-compatible, no special hardware  |
| Aurora      | Amazon   | 6-way replication, storage-compute separation     |
| YugabyteDB  | Yugabyte | Hybrid row + document store                       |
| TiDB        | PingCAP  | MySQL-compatible, TiKV storage engine             |

---

## 2. Requirements

### 2.1 Functional Requirements

| ID  | Requirement             | Description                                            |
|-----|------------------------|--------------------------------------------------------|
| FR1 | ACID Transactions      | Full support for atomicity, consistency, isolation, durability |
| FR2 | SQL Interface          | Standard SQL with JOINs, aggregations, subqueries      |
| FR3 | Secondary Indexes      | Create indexes on any column, used in query planning    |
| FR4 | Cross-Shard Txns       | Transactions spanning multiple shards (ranges)          |
| FR5 | Online Schema Changes  | ALTER TABLE without downtime                            |
| FR6 | Point-in-Time Recovery | Restore database to any past timestamp                  |

### 2.2 Non-Functional Requirements

| ID   | Requirement             | Target                                              |
|------|------------------------|-----------------------------------------------------|
| NFR1 | Availability           | 99.999% (≤ 5.26 minutes downtime per year)          |
| NFR2 | Fault Tolerance        | Survive loss of any single node, rack, or datacenter |
| NFR3 | Read Consistency       | Linearizable reads (strongest guarantee)             |
| NFR4 | Read Latency           | < 10ms for same-region reads                         |
| NFR5 | Write Latency          | < 50ms for same-region writes                        |
| NFR6 | Scalability            | Linearly scale reads and writes by adding nodes      |
| NFR7 | Storage                | Petabyte-scale across the cluster                    |

---

## 3. Core Design Decisions

### 3.1 Data Distribution Strategy

Data must be split across nodes. Two dominant approaches:

```
  RANGE-BASED SHARDING                    HASH-BASED SHARDING
  ─────────────────────                    ───────────────────

  Key Space: [A ────────── Z]              Key Space: [A ────────── Z]

  ┌──────┬──────┬──────┬──────┐            hash(key) % N = shard
  │ A-F  │ G-L  │ M-R  │ S-Z  │
  │      │      │      │      │            ┌──────┬──────┬──────┬──────┐
  │Node 1│Node 2│Node 3│Node 4│            │hash=0│hash=1│hash=2│hash=3│
  └──────┴──────┴──────┴──────┘            │      │      │      │      │
                                           │Node 1│Node 2│Node 3│Node 4│
  Range scan "D" to "H":                   └──────┴──────┴──────┴──────┘
  Only touches Node 1 & Node 2
                                           Range scan "D" to "H":
  Adjacent keys are co-located             Touches ALL nodes (scattered)
```

| Criteria            | Range-Based           | Hash-Based           |
|--------------------|-----------------------|----------------------|
| Range scans        | Efficient (locality)  | Expensive (scatter)  |
| Hot spots          | Possible              | Rare (uniform hash)  |
| Distribution       | Can be uneven         | Naturally even       |
| Split/merge        | Natural (split range) | Complex (rehash)     |
| SQL friendliness   | Excellent             | Poor for ORDER BY    |

**Our choice: Range-based sharding** — because SQL databases fundamentally need
efficient range queries (`WHERE price BETWEEN 10 AND 50`, `ORDER BY created_at`).

#### Range Split Example

```
  BEFORE: Single range [A ────────────────── Z] on Node 1
          (range has grown to 512MB, threshold exceeded)

  AFTER:  Split into two ranges

  ┌─────────────────────┐    ┌─────────────────────┐
  │   Range 1: [A - M)  │    │   Range 2: [M - Z]  │
  │   ~256 MB            │    │   ~256 MB            │
  │   Leader: Node 1     │    │   Leader: Node 3     │
  │   Followers: 2, 4    │    │   Followers: 1, 5    │
  └─────────────────────┘    └─────────────────────┘

  Split key "M" chosen to divide data roughly in half.
```

### 3.2 Replication Strategy

Each range (shard) is replicated using the **Raft consensus protocol**. Every range
forms an independent Raft group with 3 or 5 replicas.

```
           Range 47: keys ["orders/2024-01" ... "orders/2024-06")

   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
   │   Node 1    │      │   Node 3    │      │   Node 5    │
   │             │      │             │      │             │
   │  ┌───────┐  │      │  ┌───────┐  │      │  ┌───────┐  │
   │  │LEADER │  │─────▶│  │FOLLWR │  │      │  │FOLLWR │  │
   │  │       │  │      │  │       │  │      │  │       │  │
   │  │Log: 1 │  │─────▶│  │Log: 1 │  │      │  │Log: 1 │  │
   │  │    2  │  │      │  │    2  │  │      │  │    2  │  │
   │  │    3  │  │      │  │    3  │  │      │  │    3  │  │
   │  │    4◄─┼──┼──┐   │  │    4  │  │      │  │       │  │
   │  └───────┘  │  │   │  └───────┘  │      │  └───────┘  │
   └─────────────┘  │   └─────────────┘      └─────────────┘
                    │
                    │   Entry 4 is replicated to Node 3 (2 of 3 = quorum)
                    │   Entry 4 is now COMMITTED
                    └── Node 5 will catch up asynchronously
```

**Write acknowledgment requires majority** (quorum):
- 3 replicas → need 2 acknowledgments (tolerates 1 failure)
- 5 replicas → need 3 acknowledgments (tolerates 2 failures)

**Read modes:**
- **Strong read:** Read from the leader (guaranteed latest data)
- **Stale/follower read:** Read from any replica (lower latency, may be stale)

### 3.3 Storage Engine

```
  LSM-TREE (Log-Structured Merge Tree)        B-TREE
  ─────────────────────────────────────        ──────

  Write: Append to MemTable (in-memory)        Write: Find page, update in-place
         └─▶ Flush to SSTable on disk                  └─▶ May split/merge pages
         └─▶ Background compaction
                                               ┌──────────┐
  ┌──────────┐   Level 0 (newest)              │   Root   │
  │ MemTable │──▶ ┌────┐ ┌────┐               └────┬─────┘
  └──────────┘    │SST1│ │SST2│                     │
                  └────┘ └────┘              ┌──────┴──────┐
                  Level 1                    │             │
                  ┌──────────────┐        ┌──┴──┐      ┌──┴──┐
                  │   SST merged │        │Page │      │Page │
                  └──────────────┘        └──┬──┘      └──┬──┘
                  Level 2                    │             │
                  ┌────────────────────┐  ┌──┴──┐      ┌──┴──┐
                  │   SST merged       │  │Leaf │      │Leaf │
                  └────────────────────┘  └─────┘      └─────┘

  Pros: Fast writes, good compression         Pros: Fast reads, predictable
  Cons: Read amplification, compaction CPU     Cons: Write amplification
```

| Criteria          | LSM-Tree (RocksDB)      | B-Tree (InnoDB)       |
|-------------------|-------------------------|-----------------------|
| Write throughput  | Excellent               | Good                  |
| Read latency      | Good (with bloom filter)| Excellent             |
| Space efficiency  | Excellent (compression) | Moderate              |
| Write amplification| Moderate (compaction)  | High (page rewrites)  |
| Used by           | CockroachDB, YugabyteDB, TiDB | MySQL, PostgreSQL |

**Our choice: LSM-Tree (RocksDB)** — write-optimized, excellent compression, and
the industry standard for modern distributed databases.

---

## 4. Architecture Overview

### 4.1 Two-Layer Architecture

```
  ┌─────────────────────────────────────────────────────────────────┐
  │                        SQL LAYER                                │
  │                                                                 │
  │  ┌──────────┐  ┌───────────┐  ┌────────────┐  ┌────────────┐  │
  │  │  Parser  │─▶│ Optimizer │─▶│  Dist.     │─▶│ Executor   │  │
  │  │  (SQL    │  │ (cost-    │  │  Planner   │  │ (parallel  │  │
  │  │   text)  │  │  based)   │  │  (which    │  │  across    │  │
  │  │          │  │           │  │   ranges?) │  │  ranges)   │  │
  │  └──────────┘  └───────────┘  └────────────┘  └────────────┘  │
  │                                                                 │
  ├─────────────────────────────────────────────────────────────────┤
  │                      TRANSACTION LAYER                          │
  │                                                                 │
  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
  │  │  Txn Manager │  │  Lock Table  │  │  Timestamp Oracle    │  │
  │  │  (begin,     │  │  (key-level  │  │  (hybrid logical     │  │
  │  │   commit,    │  │   locking)   │  │   clock / TrueTime)  │  │
  │  │   rollback)  │  │              │  │                      │  │
  │  └──────────────┘  └──────────────┘  └──────────────────────┘  │
  │                                                                 │
  ├─────────────────────────────────────────────────────────────────┤
  │                      STORAGE LAYER                              │
  │                                                                 │
  │  ┌──────────────────────────────────────────────────────────┐  │
  │  │                  Raft Consensus Groups                   │  │
  │  │                                                          │  │
  │  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐   │  │
  │  │  │Range 1  │  │Range 2  │  │Range 3  │  │Range N  │   │  │
  │  │  │[A - D)  │  │[D - K)  │  │[K - R)  │  │[R - Z]  │   │  │
  │  │  │3 replicas│ │3 replicas│ │3 replicas│ │3 replicas│   │  │
  │  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘   │  │
  │  └──────────────────────────────────────────────────────────┘  │
  │                                                                 │
  │  ┌──────────────────────────────────────────────────────────┐  │
  │  │                    RocksDB (per node)                    │  │
  │  │     MemTable  │  SST Level 0  │  SST Level 1  │  ...   │  │
  │  └──────────────────────────────────────────────────────────┘  │
  └─────────────────────────────────────────────────────────────────┘
```

### 4.2 Node Architecture (Symmetric Design)

Every node in the cluster is **identical** — there is no special master node. Any node
can accept any query and route it to the correct range leader.

```
  ┌────────────────────────────────────────────────────────────────────┐
  │                         CLUSTER (6 Nodes)                         │
  │                                                                    │
  │   Node 1              Node 2              Node 3                   │
  │  ┌──────────┐        ┌──────────┐        ┌──────────┐            │
  │  │ SQL + KV │        │ SQL + KV │        │ SQL + KV │            │
  │  │          │        │          │        │          │            │
  │  │ R1(L)    │        │ R1(F)    │        │ R1(F)    │            │
  │  │ R2(F)    │        │ R2(L)    │        │ R2(F)    │            │
  │  │ R3(F)    │        │ R3(F)    │        │ R3(L)    │            │
  │  │ R4(L)    │        │ R4(F)    │        │ R5(F)    │            │
  │  └──────────┘        └──────────┘        └──────────┘            │
  │                                                                    │
  │   Node 4              Node 5              Node 6                   │
  │  ┌──────────┐        ┌──────────┐        ┌──────────┐            │
  │  │ SQL + KV │        │ SQL + KV │        │ SQL + KV │            │
  │  │          │        │          │        │          │            │
  │  │ R2(F)    │        │ R3(F)    │        │ R4(F)    │            │
  │  │ R5(L)    │        │ R5(F)    │        │ R6(L)    │            │
  │  │ R6(F)    │        │ R6(F)    │        │ R1(F)    │            │
  │  └──────────┘        └──────────┘        └──────────┘            │
  │                                                                    │
  │   (L) = Leader   (F) = Follower   R1..R6 = Range/Shard IDs       │
  │                                                                    │
  │   Leaders are distributed across nodes for load balancing          │
  └────────────────────────────────────────────────────────────────────┘
```

Key properties:
- **No single point of failure**: losing any one node leaves every range with 2+ replicas
- **Leaders are spread**: each node leads roughly the same number of ranges
- **Any node is a gateway**: clients connect to any node via a load balancer

---

## 5. Write Path — Detailed Flow

```
  Client              Node 2            Node 1             Node 3
  (App)            (Gateway)        (Range Leader)       (Follower)
    │                  │                  │                  │
    │ INSERT INTO      │                  │                  │
    │ orders VALUES(.) │                  │                  │
    ├─────────────────▶│                  │                  │
    │            ┌─────┴─────┐            │                  │
    │            │1. Parse   │            │                  │
    │            │2. Plan    │            │                  │
    │            │3. Locate  │            │                  │
    │            │  range    │            │                  │
    │            │  leader   │            │                  │
    │            └─────┬─────┘            │                  │
    │                  │ Forward to leader│                  │
    │                  ├─────────────────▶│                  │
    │                  │           ┌──────┴──────┐          │
    │                  │           │4. Append to │          │
    │                  │           │   Raft log  │          │
    │                  │           └──────┬──────┘          │
    │                  │                  │ Replicate log   │
    │                  │                  ├─────────────────▶│
    │                  │                  ├──▶ Node 5 (F)   │
    │                  │                  │◀── ACK (Node 3) │
    │                  │           ┌──────┴──────┐          │
    │                  │           │5. Quorum    │          │
    │                  │           │   (2 of 3)  │          │
    │                  │           │6. Apply to  │          │
    │                  │           │   RocksDB   │          │
    │                  │           └──────┬──────┘          │
    │                  │◀────────────────┤                  │
    │◀─────────────────┤ Write committed │                  │
    │   OK (1 row)     │                  │                  │
```

### Write Path Steps Explained

| Step | Action                    | Detail                                          |
|------|--------------------------|--------------------------------------------------|
| 1    | Parse SQL                | Tokenize, build AST, validate syntax             |
| 2    | Plan & Optimize          | Determine target table, row key, index updates   |
| 3    | Locate range leader      | Look up range metadata to find which node leads  |
| 4    | Append to Raft log       | Leader proposes the write entry to its Raft group |
| 5    | Replicate to followers   | Entry sent in parallel to all followers           |
| 6    | Quorum acknowledgment    | Once majority responds, entry is committed        |
| 7    | Apply to RocksDB         | Write is applied to the LSM-tree state machine   |
| 8    | Respond to client        | Success flows back through the gateway node       |

**Typical latency (same region):** 5-15ms (Raft round-trip + RocksDB write)

---

## 6. Read Path — Detailed Flow

### 6.1 Strong Read (Linearizable)

```
  Client            Node 4            Node 1 (Range Leader)
    │                  │                  │
    │ SELECT * FROM    │                  │
    │ orders WHERE     │                  │
    │ id = 'ord-789'  │                  │
    ├─────────────────▶│ Forward to leader│
    │                  ├─────────────────▶│
    │                  │           ┌──────┴──────┐
    │                  │           │1. Verify    │
    │                  │           │   leader    │
    │                  │           │   lease     │
    │                  │           │2. Read from │
    │                  │           │   RocksDB   │
    │                  │           └──────┬──────┘
    │                  │◀────────────────┤
    │◀─────────────────┤  Result row     │
    │   {id: ord-789}  │                  │
```

### 6.2 Follower Read (Bounded Staleness)

```
  Client            Node 3 (Follower)
    │                  │
    │ SELECT ... AS OF │
    │ SYSTEM TIME '-5s'│
    ├─────────────────▶│
    │           ┌──────┴──────┐
    │           │1. Verify    │
    │           │   caught up │
    │           │   to timestamp│
    │           │2. Read local│
    │           │   RocksDB   │
    │           └──────┬──────┘
    │◀─────────────────┤
    │   {id: ord-789}  │
```

### Read Mode Comparison

| Mode              | Consistency    | Latency   | Load on Leader |
|-------------------|---------------|-----------|----------------|
| Leader read       | Linearizable  | ~5ms      | High           |
| Follower read     | Bounded stale | ~2ms      | None           |
| Leader lease read | Linearizable  | ~3ms      | Low            |

**Leader lease optimization:** The leader holds a time-bounded lease. During the
lease, it knows no other node can become leader, so it can serve reads locally
without an additional Raft round-trip.

---

## 7. Distributed Transactions

### 7.1 The Problem: Cross-Range Transactions

```
  Transaction: Transfer $100 from Account A to Account B

  Account A (key: "acct/A") ──▶ Range 1  (Leader: Node 1)
  Account B (key: "acct/B") ──▶ Range 3  (Leader: Node 3)

  Both ranges have DIFFERENT Raft groups with DIFFERENT leaders.
  We need ATOMIC commitment: either both succeed or both fail.

  ┌──────────┐                              ┌──────────┐
  │ Range 1  │      MUST BE ATOMIC          │ Range 3  │
  │          │◄────────────────────────────▶ │          │
  │ acct/A   │  debit A AND credit B        │ acct/B   │
  │ -$100    │  or NEITHER                  │ +$100    │
  └──────────┘                              └──────────┘
```

### 7.2 Two-Phase Commit (2PC) for Distributed Transactions

```
  Coordinator            Range 1 Leader       Range 3 Leader
  (Node 1)                (Node 1)             (Node 3)
     │                       │                     │
     │ Write intent:         │                     │
     │  acct/A -= 100        │                     │
     ├──────────────────────▶│                     │
     │                       │ (Raft replicate)    │
     │ Write intent:         │                     │
     │  acct/B += 100        │                     │
     ├───────────────────────┼────────────────────▶│
     │                       │                     │ (Raft replicate)
     │ ════════════ PHASE 1: PREPARE ═════════════ │
     │  Prepare?             │                     │
     ├──────────────────────▶│                     │
     │                  OK ◀─┤                     │
     │  Prepare?             │                     │
     ├───────────────────────┼────────────────────▶│
     │                       │                OK ◀─┤
     │ ════════════ PHASE 2: COMMIT  ═════════════ │
     │  Write txn record:    │                     │
     │  status = COMMITTED   │                     │
     ├──────────────────────▶│ (Raft replicate)    │
     │  Resolve intents:     │                     │
     ├──────────────────────▶│                     │
     ├───────────────────────┼────────────────────▶│
     │  COMMIT complete      │                     │
```

**Intent mechanism:** During the transaction, writes are stored as "intents"
(provisional values). Other transactions encountering an intent must check if
the owning transaction committed or aborted before proceeding.

### 7.3 Parallel Commits (CockroachDB Optimization)

Standard 2PC requires **two sequential rounds** of consensus:
1. Prepare (write intents) → wait for Raft
2. Commit (transaction record) → wait for Raft

**Parallel commits** reduce this to roughly **one round**:

```
  STANDARD 2PC                         PARALLEL COMMITS
  ─────────────                         ─────────────────

  Round 1: Write intents                Round 1: Write intents AND
           (Raft replicate)                      txn record = STAGING
                    ↓                             (all in parallel)
  Round 2: Write txn record                       ↓
           COMMITTED                    Transaction is implicitly
           (Raft replicate)             committed if ALL intents
                    ↓                   were successfully written.
  Total: 2 Raft round-trips            
                                        Total: ~1 Raft round-trip
                                        (intents + record in parallel)
```

This reduces same-region write latency from ~30ms to ~15ms for cross-range
transactions — a major real-world improvement.

---

## 8. Handling Failures

### 8.1 Node Failure

```
  BEFORE: Node 2 is leader for Range 5

   Node 1          Node 2 (DEAD)        Node 3
  ┌────────┐      ┌────────┐           ┌────────┐
  │ R5 (F) │      │ R5 (L) │ ╳         │ R5 (F) │
  └────────┘      └────────┘           └────────┘

  DETECTION: Followers stop receiving heartbeats (~3-10s timeout)
  ELECTION:  Replica with most up-to-date log wins

  AFTER: Node 3 elected as new leader

   Node 1          Node 2 (DEAD)        Node 3
  ┌────────┐      ┌────────┐           ┌────────┐
  │ R5 (F) │      │ R5 (?) │ ╳         │ R5 (L) │ ← NEW LEADER
  └────────┘      └────────┘           └────────┘

  REBALANCE: Range 5 under-replicated → new replica on Node 4

   Node 1          Node 4 (NEW)         Node 3
  ┌────────┐      ┌────────┐           ┌────────┐
  │ R5 (F) │      │ R5 (F) │ ← new    │ R5 (L) │
  └────────┘      └────────┘           └────────┘
```

**Client impact:** Writes to Range 5 are unavailable for ~3-10 seconds during
leader election. Clients automatically retry. Reads from followers may continue
during this window (stale reads only).

### 8.2 Network Partition

```
  Network partition splits the cluster:

  ┌─────────────────────┐     ╳ ╳ ╳     ┌─────────────────────┐
  │    MAJORITY SIDE    │    PARTITION   │    MINORITY SIDE    │
  │                     │               │                     │
  │  Node 1  Node 2    │               │  Node 3             │
  │  R1(L)   R1(F)     │               │  R1(F)              │
  │                     │               │                     │
  │  Has 2 of 3 = ✓    │               │  Has 1 of 3 = ✗    │
  │  QUORUM             │               │  NO QUORUM          │
  │  Can read + write   │               │  Read-only (stale)  │
  └─────────────────────┘               └─────────────────────┘

  When partition heals:
  ┌─────────────────────────────────────────────────────────────┐
  │  Node 3 catches up from Node 1's Raft log                  │
  │  All entries missed during partition are replayed           │
  │  Cluster returns to full health automatically              │
  └─────────────────────────────────────────────────────────────┘
```

### 8.3 Datacenter Failure

For surviving an entire datacenter failure, replicas must be spread across
**three or more datacenters**:

```
  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
  │   DC 1 (US-East) │  │  DC 2 (US-West)  │  │  DC 3 (EU-West)  │
  │                  │  │                  │  │                  │
  │  Node 1  Node 2  │  │  Node 3  Node 4  │  │  Node 5  Node 6  │
  │                  │  │                  │  │                  │
  │  R1(L)   R2(F)  │  │  R1(F)   R2(L)  │  │  R1(F)   R2(F)  │
  │  R3(F)   R4(L)  │  │  R3(L)   R4(F)  │  │  R3(F)   R4(F)  │
  └──────────────────┘  └──────────────────┘  └──────────────────┘

  IF DC 1 GOES DOWN ENTIRELY:
  ─────────────────────────────
  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
  │   DC 1 (DOWN)    │  │  DC 2 (US-West)  │  │  DC 3 (EU-West)  │
  │                  │  │                  │  │                  │
  │   ╳╳╳╳╳╳╳╳╳╳╳   │  │  R1(F→L) R2(L)  │  │  R1(F)   R2(F)  │
  │   ╳╳╳╳╳╳╳╳╳╳╳   │  │  R3(L)   R4(F→L)│  │  R3(F)   R4(F)  │
  │                  │  │                  │  │                  │
  └──────────────────┘  └──────────────────┘  └──────────────────┘

  DC 2 + DC 3 = 2 of 3 DCs = QUORUM for every range
  New leaders elected in surviving DCs
  System remains fully operational (higher latency for cross-DC writes)
```

**Replica placement rules** (configurable):
- At least one replica per datacenter
- No two replicas of the same range on the same rack
- Leader preference for the datacenter closest to the application tier

---

## 9. Automatic Rebalancing

The cluster continuously monitors range sizes and replica distribution, performing
three types of rebalancing operations:

### 9.1 Range Split

```
  TRIGGER: Range 12 exceeds 512MB threshold

  BEFORE:
  ┌──────────────────────────────────────┐
  │  Range 12: ["user/A" ... "user/Z"]   │
  │  Size: 620 MB  (exceeds 512 MB)      │
  │  Leader: Node 2                       │
  │  Followers: Node 4, Node 6            │
  └──────────────────────────────────────┘

  AFTER (split at median key "user/M"):
  ┌──────────────────┐  ┌──────────────────┐
  │  Range 12        │  │  Range 47 (new)  │
  │  ["user/A".."M") │  │  ["user/M".."Z"] │
  │  Size: ~310 MB   │  │  Size: ~310 MB   │
  │  Leader: Node 2  │  │  Leader: Node 2  │
  │  Fllwrs: 4, 6   │  │  Fllwrs: 4, 6   │
  └──────────────────┘  └──────────────────┘

  Range 47's leader may later move to a different node for balance.
```

### 9.2 Range Merge

```
  TRIGGER: Adjacent ranges 22 and 23 are both < 64MB

  BEFORE:
  ┌────────────────┐  ┌────────────────┐
  │  Range 22      │  │  Range 23      │
  │  ["k".."m")    │  │  ["m".."p")    │
  │  Size: 40 MB   │  │  Size: 30 MB   │
  └────────────────┘  └────────────────┘

  AFTER:
  ┌──────────────────────────────────────┐
  │  Range 22 (merged)                   │
  │  ["k" ... "p")                       │
  │  Size: 70 MB                         │
  └──────────────────────────────────────┘
```

### 9.3 Leader & Replica Rebalancing

```
  BEFORE: Node 1 overloaded (too many leaders)

   Node 1           Node 2           Node 3
  ┌────────┐       ┌────────┐       ┌────────┐
  │ R1 (L) │       │ R1 (F) │       │ R1 (F) │
  │ R2 (L) │       │ R2 (F) │       │ R2 (F) │
  │ R3 (L) │       │ R3 (F) │       │ R3 (F) │
  │ R4 (L) │       │ R4 (F) │       │ R4 (F) │
  └────────┘       └────────┘       └────────┘
  4 leaders         0 leaders        0 leaders   ← UNBALANCED

  AFTER: Leadership transferred to balance load

   Node 1           Node 2           Node 3
  ┌────────┐       ┌────────┐       ┌────────┐
  │ R1 (L) │       │ R2 (L) │       │ R3 (L) │
  │ R2 (F) │       │ R1 (F) │       │ R1 (F) │
  │ R3 (F) │       │ R3 (F) │       │ R4 (L) │
  │ R4 (F) │       │ R4 (F) │       │ R2 (F) │
  └────────┘       └────────┘       └────────┘
  1 leader          1 leader         2 leaders   ← BALANCED
```

**Leadership transfer** is lightweight — it does not require copying data, only
changing which replica processes writes (a Raft leadership transfer message).

---

## 10. Schema Changes (Online DDL)

Traditional databases lock the table during ALTER TABLE. In a distributed database
serving thousands of queries per second, that is unacceptable. Instead, schema
changes are treated as a **distributed state machine**:

```
  Phase 1: DELETE-ONLY          Phase 2: DELETE-AND-          Phase 3: PUBLIC
  (schema v1 → v1.5)           WRITE-ONLY (v1.5 → v2)       (v2 live)
                                
  ┌───────────────────┐        ┌───────────────────┐         ┌──────────────┐
  │ New column added  │        │ Backfill existing │         │ Column fully │
  │ to schema catalog │───────▶│ rows (background  │────────▶│ visible and  │
  │                   │        │ scan + write)     │         │ queryable    │
  │ Existing queries  │        │                   │         │              │
  │ don't see it yet  │        │ New writes include│         │ Schema change│
  │                   │        │ the new column    │         │ complete     │
  │ Deletes clean up  │        │                   │         │              │
  │ new column data   │        │ Reads ignore it   │         │              │
  └───────────────────┘        └───────────────────┘         └──────────────┘
        │                              │                            │
        │    Lease boundary            │    Lease boundary          │
        └──────────────────────────────┴────────────────────────────┘
```

**Why phases?** At any moment, different nodes may be running different schema
versions (because they learn about the change at different times). The phased
approach ensures:
- No node ever skips a phase (maximum one version apart)
- Data written by a newer version is correctly handled by older version nodes
- Zero downtime — queries continue throughout the process

---

## 11. Query Optimization in a Distributed Setting

Distributed query optimization adds complexity beyond single-node optimization:

### 11.1 Push Computation to Data

```
  NAIVE PLAN: Pull all data to coordinator, then filter

  SELECT * FROM orders WHERE region = 'US' AND total > 1000

  ┌──────────┐     ALL rows      ┌────────────┐
  │ Range 1  │──────────────────▶│            │
  │ Range 2  │──────────────────▶│ Coordinator│──▶ Filter ──▶ Result
  │ Range 3  │──────────────────▶│ (Node 1)   │
  └──────────┘  (millions rows)  └────────────┘
  Network: HUGE                   CPU: HUGE

  OPTIMIZED PLAN: Push filter to each range

  ┌──────────────┐  matching rows  ┌────────────┐
  │ R1: filter   │────────────────▶│            │
  │ R2: filter   │────────────────▶│ Coordinator│──▶ Result
  │ R3: filter   │────────────────▶│ (Node 1)   │
  └──────────────┘  (few rows)     └────────────┘
  Network: small                    CPU: distributed
```

### 11.2 Distributed Join Strategies

```
  SELECT o.*, c.name
  FROM orders o JOIN customers c ON o.customer_id = c.id
  WHERE o.date > '2024-01-01'

  Strategy 1: LOOKUP JOIN (small left side)
  ───────────────────────────────────────────
  For each order, lookup customer by ID
  Best when: left side is small (filtered)

  Strategy 2: HASH JOIN (both sides large)
  ─────────────────────────────────────────
  Build hash table of customers, stream orders through
  Best when: both sides are large, no useful index

  Strategy 3: MERGE JOIN (both sides sorted)
  ────────────────────────────────────────────
  Merge two sorted streams (like merge sort)
  Best when: both sides sorted on join key
```

### 11.3 Parallel Scan Across Ranges

```
  SELECT COUNT(*) FROM orders WHERE status = 'shipped'

  Table "orders" spans 4 ranges:

   Range 1         Range 2         Range 3         Range 4
  ┌───────┐       ┌───────┐       ┌───────┐       ┌───────┐
  │COUNT: │       │COUNT: │       │COUNT: │       │COUNT: │
  │ 2,341 │       │ 1,892 │       │ 3,105 │       │ 2,662 │
  └───┬───┘       └───┬───┘       └───┬───┘       └───┬───┘
      │               │               │               │
      └───────────┬───┴───────┬───────┘               │
                  │           │                        │
              ┌───┴───────────┴────────────────────────┘
              │
              ▼
         Coordinator: SUM(2341 + 1892 + 3105 + 2662) = 10,000
```

All four ranges are scanned **in parallel**, and only the partial counts
(not the raw rows) are sent to the coordinator.

---

## 12. Real-World Comparisons

| Feature              | Google Spanner     | CockroachDB       | YugabyteDB        | TiDB              | Amazon Aurora       |
|---------------------|--------------------|--------------------|--------------------|--------------------|---------------------|
| **Consensus**       | Paxos              | Raft               | Raft               | Raft               | Quorum (custom)     |
| **Storage Engine**  | Custom (Colossus)  | RocksDB (Pebble)   | RocksDB            | RocksDB (TiKV)     | Custom (log-based)  |
| **SQL Compat.**     | SQL (custom)       | PostgreSQL         | PostgreSQL         | MySQL              | MySQL / PostgreSQL  |
| **Global Txns**     | Yes (TrueTime)     | Yes (HLC)          | Yes (HLC)          | Yes (TSO)          | No (single region)  |
| **Clock**           | TrueTime (atomic)  | Hybrid Logical     | Hybrid Logical     | Timestamp Oracle   | N/A                 |
| **Sharding**        | Range              | Range              | Range + Hash       | Range              | N/A (shared storage)|
| **Open Source**     | No                 | Yes (BSL → Apache) | Yes (Apache 2.0)   | Yes (Apache 2.0)   | No                  |
| **Multi-Region**    | Native             | Native             | Native             | Experimental       | Limited (replicas)  |
| **Max Tested Scale**| Billions of rows   | Hundreds of TB     | Hundreds of TB     | Hundreds of TB     | 128 TB per instance |

### Clock Mechanisms Deep Dive

```
  TRUETIME (Google Spanner)              HYBRID LOGICAL CLOCK (CockroachDB)
  ──────────────────────────             ──────────────────────────────────

  Uses GPS + atomic clocks               Uses NTP + logical counter
  Returns: [earliest, latest]            Returns: (wall_time, logical_counter)
  Uncertainty: ~7ms window               Uncertainty: ~250ms (NTP skew)

  Commit wait: if txn commits at         Must restart txn if clock skew
  time T, wait until T + uncertainty     detected (uncertainty window).
  is in the past. Guarantees             Trades some latency for
  linearizability without                correctness without specialized
  communication.                         hardware.

  Requires: specialized hardware         Requires: nothing special
  (GPS receivers, atomic clocks)         (commodity servers with NTP)
```

---

## 13. Full System Architecture

```
  ┌─────────────────────────────────────────────────────────────────────┐
  │                       CLIENT APPLICATIONS                          │
  │    App Server 1    App Server 2    App Server 3    App Server N    │
  │    (PostgreSQL     (PostgreSQL     (PostgreSQL     (PostgreSQL     │
  │     driver)         driver)         driver)         driver)        │
  └───────┬────────────────┬───────────────┬───────────────┬──────────┘
          │                │               │               │
          ▼                ▼               ▼               ▼
  ┌───────────────────────────────────────────────────────────────────┐
  │                     LOAD BALANCER (L4/L7)                        │
  └───────┬────────────────┬───────────────┬───────────────┬─────────┘
          │                │               │               │
  ┌───────▼────────────────▼───────────────▼───────────────▼─────────┐
  │                    DISTRIBUTED DB CLUSTER                        │
  │                                                                   │
  │  ┌─────────────── DC 1 (US-East) ──────────────┐                │
  │  │  ┌──────────────┐     ┌──────────────┐      │                │
  │  │  │   NODE 1     │     │   NODE 2     │      │                │
  │  │  │ ┌──────────┐ │     │ ┌──────────┐ │      │                │
  │  │  │ │ SQL/Txn  │ │     │ │ SQL/Txn  │ │      │                │
  │  │  │ │ KV/Raft  │ │     │ │ KV/Raft  │ │      │                │
  │  │  │ │R1(L)R3(F)│ │     │ │R1(F)R2(L)│ │      │                │
  │  │  │ │R5(F)R7(L)│ │     │ │R4(F)R6(L)│ │      │                │
  │  │  │ │ RocksDB  │ │     │ │ RocksDB  │ │      │                │
  │  │  │ └──────────┘ │     │ └──────────┘ │      │                │
  │  │  └──────────────┘     └──────────────┘      │                │
  │  └─────────────────────────────────────────────┘                │
  │                                                                   │
  │  ┌─────────────── DC 2 (US-West) ──────────────┐                │
  │  │  ┌──────────────┐     ┌──────────────┐      │                │
  │  │  │   NODE 3     │     │   NODE 4     │      │                │
  │  │  │ ┌──────────┐ │     │ ┌──────────┐ │      │                │
  │  │  │ │ SQL/Txn  │ │     │ │ SQL/Txn  │ │      │                │
  │  │  │ │ KV/Raft  │ │     │ │ KV/Raft  │ │      │                │
  │  │  │ │R1(F)R3(L)│ │     │ │R2(F)R4(L)│ │      │                │
  │  │  │ │R5(L)R8(F)│ │     │ │R6(F)R7(F)│ │      │                │
  │  │  │ │ RocksDB  │ │     │ │ RocksDB  │ │      │                │
  │  │  │ └──────────┘ │     │ └──────────┘ │      │                │
  │  │  └──────────────┘     └──────────────┘      │                │
  │  └─────────────────────────────────────────────┘                │
  │                                                                   │
  │  ┌─────────────── DC 3 (EU-West) ──────────────┐                │
  │  │  ┌──────────────┐     ┌──────────────┐      │                │
  │  │  │   NODE 5     │     │   NODE 6     │      │                │
  │  │  │ ┌──────────┐ │     │ ┌──────────┐ │      │                │
  │  │  │ │ SQL/Txn  │ │     │ │ SQL/Txn  │ │      │                │
  │  │  │ │ KV/Raft  │ │     │ │ KV/Raft  │ │      │                │
  │  │  │ │R2(F)R5(F)│ │     │ │R3(F)R8(L)│ │      │                │
  │  │  │ │R7(F)R8(F)│ │     │ │R4(F)R6(F)│ │      │                │
  │  │  │ │ RocksDB  │ │     │ │ RocksDB  │ │      │                │
  │  │  │ └──────────┘ │     │ └──────────┘ │      │                │
  │  │  └──────────────┘     └──────────────┘      │                │
  │  └─────────────────────────────────────────────┘                │
  │                                                                   │
  │  ┌──────────────── CLUSTER METADATA ───────────────┐            │
  │  │  ┌─────────────┐ ┌──────────────┐ ┌──────────┐ │            │
  │  │  │ Range Map   │ │Node Liveness │ │ Schema   │ │            │
  │  │  │ key→range→  │ │ heartbeats,  │ │ Registry │ │            │
  │  │  │ node mapping│ │ epoch leases │ │ (tables) │ │            │
  │  │  └─────────────┘ └──────────────┘ └──────────┘ │            │
  │  │  (Stored as ranges, replicated via Raft)        │            │
  │  └─────────────────────────────────────────────────┘            │
  └───────────────────────────────────────────────────────────────────┘
```

### How a Query Flows Through the Full Architecture

```
  1. Client sends: SELECT * FROM users WHERE id = 42

  2. Load balancer routes to Node 3 (any node works)

  3. Node 3's SQL layer:
     ├── Parses SQL into AST
     ├── Resolves table "users" from schema registry
     ├── Optimizer: id is primary key → point lookup
     └── Distributed planner: key "users/42" → Range 2 → Leader is Node 2

  4. Node 3 sends KV request to Node 2 (Range 2 leader)

  5. Node 2 reads from local RocksDB (leader lease valid, no Raft needed)

  6. Result flows: Node 2 → Node 3 → Load Balancer → Client

  Total latency: ~3-8ms (same region)
```

---

## 14. Key Takeaways

1. **Range-based sharding is essential for SQL workloads.** It preserves data
   locality for range scans, ORDER BY, and co-located index lookups — operations
   that hash-based sharding scatters across every node in the cluster.

2. **Raft consensus per range gives independent fault tolerance.** Each range
   (shard) is an independent Raft group. A leader failure in one range does not
   affect other ranges. The cluster can tolerate simultaneous failures across
   different ranges as long as each range retains a majority.

3. **Symmetric node architecture eliminates single points of failure.** Every
   node runs the full stack (SQL, transaction, storage). There is no special
   "master" node. This simplifies operations, scaling, and failure recovery.

4. **Distributed transactions require careful coordination.** Two-phase commit
   (2PC) with write intents provides cross-range atomicity. Optimizations like
   parallel commits (CockroachDB) halve the latency cost of distributed
   transactions from two consensus rounds to approximately one.

5. **The storage engine choice has system-wide implications.** LSM-trees
   (RocksDB) dominate modern distributed databases because they optimize for
   write throughput and compression — critical when every write is amplified
   by Raft replication across multiple nodes.

6. **Multi-datacenter replication is the key to surviving catastrophic failures.**
   With 3 replicas across 3 datacenters, losing an entire DC still leaves a
   quorum (2 of 3). The tradeoff is higher write latency due to cross-DC
   consensus rounds — typically 50-100ms for global deployments.

7. **Online schema changes are a distributed state machine problem.** Phased
   rollout (delete-only → write-only → public) ensures that nodes running
   different schema versions never produce inconsistent data. This enables
   zero-downtime ALTER TABLE operations on tables serving live traffic.

8. **Push computation to data, not data to computation.** The distributed query
   optimizer must push filters, aggregations, and partial joins to the nodes
   holding the data. Pulling raw rows to a coordinator creates a network
   bottleneck that defeats the purpose of distribution.

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
