# Chapter 13: Consistency Models and Consensus

## Introduction

In a distributed system where data is replicated across multiple nodes, a fundamental question
arises: **when a client reads data, what guarantees does the system provide about the freshness
and correctness of that data?**

Consistency models define the contract between a distributed system and its clients. Stronger
consistency makes the system easier to reason about but harder to build and slower to operate.
Weaker consistency is easier to achieve with high availability and low latency but forces
application developers to handle anomalies.

This chapter explores the full spectrum of consistency models, the mechanisms that implement them
(vector clocks, CRDTs, consensus protocols), and how real-world systems navigate these trade-offs.

---

## 13.1 What Is Consistency in Distributed Systems?

**Consistency** in distributed systems is NOT the same as the "C" in ACID (which refers to
database invariants like foreign keys and constraints). Here, consistency means:

> **All nodes in the system agree on the current state of the data, and clients observe a
> coherent view of that state.**

The challenge: in a distributed system, updates take time to propagate. During that propagation
window, different nodes hold different versions of the data. The consistency model defines what
clients are allowed to observe during this window.

```
  Client writes X=5 to Node A at time t=0

  t=0:  Node A: X=5     Node B: X=1     Node C: X=1
  t=1:  Node A: X=5     Node B: X=5     Node C: X=1   (propagating...)
  t=2:  Node A: X=5     Node B: X=5     Node C: X=5   (converged)

  If a client reads from Node C at t=1, what should it see?
  - Strong consistency: X=5 (system blocks until all nodes agree)
  - Eventual consistency: X=1 (stale, but will eventually become X=5)
```

---

## 13.2 The Consistency Spectrum

Consistency models form a spectrum from strongest (most intuitive but most expensive) to weakest
(most performant but most surprising).

```
  STRONGEST                                                    WEAKEST
  (hardest to implement,                        (easiest to implement,
   highest latency,                               lowest latency,
   most intuitive)                                  most anomalies)
     |                                                        |
     v                                                        v
  +--------+--------+---------+-----------+----------+-----------+
  |Lineariz|Sequen- | Causal  | Read-Your-| Monotonic| Eventual  |
  |ability | tial   | Consis- | Writes    | Reads    | Consis-   |
  |        | Consis-| tency   | Consis-   |          | tency     |
  |        | tency  |         | tency     |          |           |
  +--------+--------+---------+-----------+----------+-----------+
```

### 13.2.1 Linearizability (Strong Consistency)

The strongest guarantee. The system behaves **as if there is only one copy of the data** and
every operation is atomic and instantaneous.

**Definition:** Once a write completes, ALL subsequent reads (by any client, on any node) MUST
return that written value or a later value. Operations appear to take effect at a single point
in time between their invocation and completion.

```
  Timeline:
  Client A: |---write(X=1)---|
  Client B:          |---read(X)---|
                                    ^
                                    Must return X=1 (the write completed before the read started)

  Client A: |---write(X=1)--------|
  Client B:      |---read(X)---|
                                ^
                                May return X=0 or X=1 (write and read overlap — either is valid)
                                BUT: if this read returns X=1, all subsequent reads must also
                                return X=1 or later.
```

**Implementations:**
- Single-node databases (trivially linearizable)
- Consensus protocols (Paxos, Raft) — used by etcd, ZooKeeper
- Google Spanner (TrueTime + Paxos)

**Cost:** Every write must be acknowledged by a majority of nodes before returning. This adds
latency proportional to the round-trip time between nodes (often 10-100ms for cross-region).

### 13.2.2 Sequential Consistency

Weaker than linearizability. All nodes see the **same order of operations**, but that order
doesn't have to match real-time ordering.

```
  Real-time order:
    Client A: write(X=1) at t=0
    Client B: write(X=2) at t=1

  Linearizable: everyone must see X=1 before X=2 (real-time order)
  Sequential:   everyone could see X=2 before X=1, as long as ALL nodes
                agree on the same order (e.g., all see X=2 then X=1)
```

**Analogy:** Imagine a single bulletin board where all operations are posted. Sequential
consistency means all observers read the same bulletin board, but the posting order might not
match wall-clock time.

### 13.2.3 Causal Consistency

Preserves the **cause-and-effect** relationships between operations. If operation A could have
influenced operation B (A "happened before" B), then everyone must see A before B. But
**concurrent** operations (neither caused the other) can be seen in different orders by
different nodes.

```
  Causal relationship:
    Client A: write(X=1)
    Client A: write(Y=2)    <-- A set Y after X, so these are causally related
    Everyone must see X=1 before Y=2

  Concurrent (no causal relationship):
    Client A: write(X=1) at t=0
    Client B: write(Y=2) at t=0    <-- independent, no causal link
    Different nodes may see these in different orders (X before Y, or Y before X)
```

**Used by:** MongoDB (causal consistency sessions), COPS (Clusters of Order-Preserving Servers).

### 13.2.4 Read-Your-Writes Consistency

After a client performs a write, that **same client** is guaranteed to see its own write in
subsequent reads. Other clients may still see stale data.

```
  Client A: write(name="Alice")
  Client A: read(name) --> "Alice"     (guaranteed to see own write)
  Client B: read(name) --> "Bob"       (may still see old value -- that's OK)
```

**Implementation:** Route the writing client's subsequent reads to the leader, or track the
write timestamp and only read from replicas that are caught up to that timestamp.

### 13.2.5 Monotonic Reads

Once a client reads a certain value, it will **never see an older value** in subsequent reads.
Time doesn't go backward for that client.

```
  Client A: read from Replica 1 --> name="Alice" (version 5)
  Client A: read from Replica 2 --> name="Alice" (version 5 or later)
                                     NEVER name="Bob" (version 3)
```

**Implementation:** Pin clients to the same replica (sticky sessions), or track the latest
version seen and only read from replicas at that version or later.

### 13.2.6 Eventual Consistency

The weakest useful guarantee. If no new writes are made, **eventually** all replicas will
converge to the same value. No guarantees about when, and no guarantees about what clients
see during the convergence window.

```
  t=0: write(X=5) to Node A
  t=1: Node A: X=5,  Node B: X=1,  Node C: X=1   (inconsistent)
  t=2: Node A: X=5,  Node B: X=5,  Node C: X=1   (still inconsistent)
  t=3: Node A: X=5,  Node B: X=5,  Node C: X=5   (converged!)

  Between t=0 and t=3, clients reading from B or C see stale data.
  But the system EVENTUALLY converges.
```

**Used by:** DNS, Amazon S3, Cassandra (default), DynamoDB (default reads).

---

## 13.3 Vector Clocks and Lamport Timestamps

Distributed systems lack a global clock. **Logical clocks** provide a way to determine the
order of events without relying on synchronized wall clocks.

### 13.3.1 Lamport Timestamps

Each node maintains a **counter**. The counter is incremented on every event and included in
every message. When a node receives a message, it updates its counter to:
`max(local_counter, received_counter) + 1`.

```
  Node A         Node B         Node C
  ------         ------         ------
  A1 (1)
      \
       -------> B1 (2)
                   \
                    --------> C1 (3)
  A2 (2)
  A3 (3)
                B2 (4)
                    \
                     -------> C2 (5)
       <-------
  A4 (5)        B3 (5)

  Rule: If event X happened before event Y, then timestamp(X) < timestamp(Y)
  BUT: If timestamp(X) < timestamp(Y), X did NOT necessarily happen before Y
        (could be concurrent!)
```

**Limitation:** Lamport timestamps give you a total order, but they can't tell you if two
events are **concurrent** (causally independent). For that, you need vector clocks.

### 13.3.2 Vector Clocks

Each node maintains a **vector** of counters — one for every node in the system.

```
System with 3 nodes: A, B, C
Vector clock = [A:count, B:count, C:count]

  Node A              Node B              Node C
  ------              ------              ------
  write X=1
  VC: [A:1,B:0,C:0]
        \
         ----------> receives, merges
                     VC: [A:1,B:1,C:0]
                     write X=2
                     VC: [A:1,B:2,C:0]
                            \
                             ----------> receives, merges
                                         VC: [A:1,B:2,C:1]
  write X=3
  VC: [A:2,B:0,C:0]     (concurrent with B's X=2!)
```

**Comparing vector clocks:**

```
  VC1 = [A:2, B:3, C:1]
  VC2 = [A:2, B:2, C:2]

  Is VC1 < VC2?  No (B:3 > B:2)
  Is VC2 < VC1?  No (C:2 > C:1)
  Therefore: VC1 and VC2 are CONCURRENT (conflict detected!)

  VC3 = [A:2, B:4, C:2]
  Is VC2 < VC3?  Yes (every component of VC2 <= corresponding component of VC3)
  Therefore: VC2 happened before VC3 (no conflict)
```

**Rules:**
- `VC1 < VC2` (VC1 happened before VC2) if all components of VC1 ≤ VC2 and at least one is strictly <
- If neither `VC1 < VC2` nor `VC2 < VC1`, they are **concurrent** → conflict!
- To merge concurrent versions: take element-wise maximum

**Used by:** Amazon DynamoDB (conflict detection), Riak (sibling resolution).

---

## 13.4 CRDTs — Conflict-free Replicated Data Types

CRDTs are data structures designed so that **concurrent updates always converge to the same
result**, without coordination. They achieve this by ensuring that the merge operation is
**commutative**, **associative**, and **idempotent**.

### 13.4.1 G-Counter (Grow-Only Counter)

```
  Each node maintains a map of {node_id: count}:

  Node A increments 3 times:   {A:3, B:0, C:0}
  Node B increments 5 times:   {A:0, B:5, C:0}
  Node C increments 2 times:   {A:0, B:0, C:2}

  Merge: element-wise MAX
  Result: {A:3, B:5, C:2}
  Total count = 3 + 5 + 2 = 10

  No matter what order the merges happen, the result is always the same!
```

### 13.4.2 PN-Counter (Positive-Negative Counter)

Two G-Counters: one for increments (P) and one for decrements (N).

```
  P-counter: {A:5, B:3} = 8 increments
  N-counter: {A:2, B:1} = 3 decrements
  Value = P - N = 8 - 3 = 5
```

### 13.4.3 LWW-Register (Last-Writer-Wins Register)

Each write is tagged with a timestamp. The value with the highest timestamp wins.

```
  Node A: {value: "Alice", timestamp: 100}
  Node B: {value: "Bob",   timestamp: 105}

  Merge: compare timestamps, take the highest
  Result: {value: "Bob", timestamp: 105}

  Trade-off: Simple convergence, but silently drops the "Alice" write.
```

### 13.4.4 OR-Set (Observed-Remove Set)

A set that supports both add and remove operations. Each element is tagged with a unique
identifier so that adds and removes can be distinguished even when concurrent.

```
  Node A: add("apple", tag=a1)     Set: {("apple",a1)}
  Node B: add("apple", tag=b1)     Set: {("apple",b1)}

  Merge: {("apple",a1), ("apple",b1)}

  Node A: remove("apple")  -- removes tag a1 only (the one A has seen)
  After merge: {("apple",b1)}  -- B's add survives because A didn't see it

  Semantics: "add wins over concurrent remove" -- an element is in the set
  if there exists at least one add tag that hasn't been removed.
```

### CRDT Summary Table

| CRDT Type      | Supported Ops      | Convergence Strategy      | Use Case                    |
|----------------|--------------------|---------------------------|-----------------------------|
| G-Counter      | Increment only     | Element-wise MAX          | Page view counters, likes   |
| PN-Counter     | Increment/Decrement| Two G-Counters (P - N)   | Stock counts, vote tallies  |
| LWW-Register   | Read/Write         | Highest timestamp wins    | User profile fields         |
| G-Set          | Add only           | Set union                 | Tags, bookmarks             |
| OR-Set         | Add/Remove         | Unique tags per add       | Shopping cart, todo lists    |
| LWW-Element-Set| Add/Remove         | Timestamp per element     | Collaborative collections   |

---

## 13.5 The Split-Brain Problem

A **split-brain** occurs when a network partition causes two parts of a cluster to each believe
they are the active leader, leading to conflicting writes and data divergence.

```
  Normal operation:
  +--------+     +--------+     +--------+
  | Node A | <-> | Node B | <-> | Node C |
  | LEADER |     |FOLLOWER|     |FOLLOWER|
  +--------+     +--------+     +--------+

  Network partition:
  +--------+     ||     +--------+     +--------+
  | Node A |     ||     | Node B | <-> | Node C |
  | LEADER |     ||     | new    |     |FOLLOWER|
  | (thinks|     ||     | LEADER |     | (of B) |
  |  it's  |     ||     | (elected    |         |
  |  still |     ||     |  by B+C)    |         |
  | leader)|     ||     +--------+     +--------+
  +--------+     ||

  SPLIT BRAIN: Both A and B accept writes!
  Client 1 --> Node A: SET balance = 100
  Client 2 --> Node B: SET balance = 200
  When partition heals: balance = 100 or 200? DATA CONFLICT!
```

### Prevention Strategies

| Strategy            | How It Works                                         | Used By              |
|---------------------|------------------------------------------------------|----------------------|
| **Fencing tokens**  | Each leader gets a monotonically increasing token.   | ZooKeeper, etcd      |
|                     | Stale leaders' tokens are rejected by storage.       |                      |
| **Quorum-based**    | Leader must maintain a quorum (majority) of nodes.   | Raft, Paxos          |
| **leader election** | Isolated leader with minority loses leadership.      |                      |
| **STONITH**         | "Shoot The Other Node In The Head" — physically      | Pacemaker, Corosync  |
|                     | power off the old leader via IPMI/BMC.               |                      |
| **Lease-based**     | Leader holds a time-limited lease; must renew it.    | Google Chubby,       |
| **leadership**      | If unable to renew (partitioned), it steps down.     | DynamoDB leases      |
| **Epoch numbers**   | Each leadership term gets an incrementing epoch.     | Kafka, Raft          |
|                     | Writes from old epochs are rejected.                 |                      |

### Fencing Tokens Example

```
  1. Leader A gets fencing token #33 from ZooKeeper
  2. Network partition occurs
  3. Leader B is elected, gets fencing token #34
  4. Leader A (partitioned) tries to write with token #33
  5. Storage rejects #33 because it has already seen #34
  6. Only Leader B's writes (with token #34) are accepted

  Storage:
    "I last saw token #34. Token #33 is stale. REJECTED."
```

---

## 13.6 Consistency in Practice

### Tunable Consistency in Apache Cassandra

Cassandra lets you choose consistency per query:

```sql
-- Strongest: all replicas must respond
SELECT * FROM users WHERE id = 1 USING CONSISTENCY ALL;

-- Quorum: majority must respond (recommended for most cases)
INSERT INTO users (id, name) VALUES (1, 'Alice') USING CONSISTENCY QUORUM;

-- Weakest: any one replica responds
SELECT * FROM users WHERE id = 1 USING CONSISTENCY ONE;

-- Local quorum: majority in the LOCAL data center (for multi-DC)
SELECT * FROM users WHERE id = 1 USING CONSISTENCY LOCAL_QUORUM;
```

**Common pattern:** Write with QUORUM + Read with QUORUM = strong consistency (R + W > N).

```
Cassandra with RF=3 (3 replicas):
  QUORUM = ceil(3/2) = 2

  Write QUORUM: write to 2 of 3 replicas
  Read  QUORUM: read from 2 of 3 replicas
  Overlap: at least 1 replica has the latest write (2+2 > 3)
```

### Strong Consistency in Google Spanner

Spanner achieves **external consistency** (linearizability across the globe) using:

1. **TrueTime API:** GPS-synchronized clocks with bounded uncertainty
   ```
   TrueTime returns an interval: [earliest, latest]
   True time is guaranteed to be within this interval
   Uncertainty is typically 1-7 milliseconds
   ```
2. **Commit wait:** After committing, the leader waits until the uncertainty interval has
   passed before reporting success
   ```
   Transaction commits at TrueTime [100ms, 107ms]
   Leader waits until real time > 107ms before acknowledging
   This ensures no other transaction can be assigned an earlier timestamp
   ```
3. **Paxos groups:** Each partition is replicated using Paxos for consensus

**Result:** Globally consistent reads without locks. A read at timestamp T is guaranteed to
see all writes committed before T, regardless of which data center serves the read.

---

## 13.7 Trade-offs: Consistency vs. Availability vs. Latency

### The CAP Theorem (Revisited)

In the presence of a **network partition** (P), a system must choose between:
- **Consistency (C):** Every read returns the most recent write
- **Availability (A):** Every request receives a response (not an error)

```
                          C
                         / \
                        /   \
                       / CP  \
                      / systems\
                     /  (HBase, \
                    /   Spanner) \
                   +------+------+
                  /        \      \
                 /    CA    \  AP  \
                / (single   \systems\
               /   node —   \(Cass- \
              /    trivial)  \andra, \
             /                \Dynamo)\
            +------------------+------+
            A                         P

  CA: Not possible in distributed systems (partitions always happen)
  CP: Consistent but may be unavailable during partitions
  AP: Available but may return stale data during partitions
```

### The PACELC Theorem

An extension of CAP that also considers the case when there is **no** partition:

```
  If (Partition) then (Consistency vs. Availability)
  Else           then (Consistency vs. Latency)

  System         | During Partition | Normal Operation
  -------------- | ---------------- | ----------------
  DynamoDB       | Choose A over C  | Choose L over C   (PA/EL)
  Cassandra      | Choose A over C  | Choose L over C   (PA/EL)
  MongoDB        | Choose C over A  | Choose C over L   (PC/EC)
  Google Spanner | Choose C over A  | Choose C over L   (PC/EC)
  PNUTS (Yahoo)  | Choose A over C  | Choose L over C   (PA/EL)
```

### Practical Decision Framework

```
Question 1: Can your application tolerate stale reads?
  |
  +-- No  --> You need strong consistency (CP system)
  |           Examples: banking, inventory, leader election
  |
  +-- Yes --> Eventual consistency is likely fine (AP system)
              Examples: social media feeds, analytics, DNS

Question 2: How much stale can you tolerate?
  |
  +-- Milliseconds --> Causal consistency or read-your-writes
  |                    (often achievable within a single data center)
  |
  +-- Seconds --> Eventual consistency with anti-entropy
  |
  +-- Minutes/Hours --> Pure eventual consistency
                        (DNS propagation, email delivery)

Question 3: Is this a multi-region deployment?
  |
  +-- No  --> Single-leader with synchronous replication gives you strong
  |           consistency with acceptable latency
  |
  +-- Yes --> Strong consistency across regions = high latency (100-300ms)
              Consider: causal consistency, or strong within region +
              eventual across regions
```

---

## 13.8 Consensus Protocols: A Brief Overview

**Consensus** is the problem of getting multiple nodes to agree on a single value. It's the
foundation of strong consistency, leader election, and distributed locks.

### Raft (Understandable Consensus)

```
  Election timeout:
  +----------+     +----------+     +----------+
  | Follower | --> | Candidate| --> |  Leader   |
  | (waiting)|     | (voting) |     | (serving) |
  +----------+     +----------+     +----------+
       ^                                  |
       |       heartbeat timeout          |
       +----------------------------------+

  Log replication:
  Leader: [Entry1, Entry2, Entry3, Entry4]
                     |
          +----------+----------+
          v          v          v
  Foll A: [E1, E2, E3, E4]   (up to date)
  Foll B: [E1, E2, E3]       (one behind)
  Foll C: [E1, E2]           (two behind)

  Leader commits Entry3 when majority (2 of 3) have it: Leader + Follower A
```

**Key properties:**
- **Safety:** Never returns an incorrect result
- **Liveness:** Eventually makes progress (assuming majority of nodes are alive)
- **Leader-based:** One leader at a time; simplifies reasoning

**Used by:** etcd, Consul, CockroachDB, TiKV.

### Paxos

The original consensus protocol. More general but harder to understand than Raft.

**Roles:** Proposer, Acceptor, Learner
**Phases:** Prepare → Promise → Accept → Accepted

**Used by:** Google Chubby, Google Spanner, Apache Mesos.

---

## 13.9 Key Takeaways

1. **Consistency is a spectrum**, not a binary choice. Understand the guarantees your
   application actually needs — most applications don't need linearizability everywhere.

2. **Linearizability** (strong consistency) makes a distributed system behave like a single
   machine. It's the most intuitive but also the most expensive in terms of latency and
   availability.

3. **Eventual consistency** is the weakest useful guarantee — the system will converge, but
   clients may see stale data during convergence. It enables high availability and low latency.

4. **Causal consistency** is often the sweet spot — it preserves cause-and-effect relationships
   (which humans expect) while allowing better performance than linearizability.

5. **Vector clocks** detect concurrent operations. If two vector clocks are incomparable,
   the events are concurrent and may conflict. Lamport timestamps give total order but
   can't detect concurrency.

6. **CRDTs** solve conflicts by making data structures that mathematically converge. They're
   powerful for specific use cases (counters, sets, registers) but can't solve all problems.

7. **Split-brain** is a critical failure mode where two leaders accept conflicting writes.
   Prevent it with fencing tokens, quorum-based leader election, or lease-based leadership.

8. **Tunable consistency** (Cassandra, DynamoDB) lets you choose per-query — strong consistency
   for critical operations, eventual consistency for latency-sensitive reads.

9. **The CAP theorem** says you must choose between consistency and availability during
   partitions. **PACELC** adds that even without partitions, there's a consistency-latency
   trade-off.

10. **Consensus protocols** (Raft, Paxos) are the building blocks of strong consistency.
    They ensure all nodes agree on a value, but require a majority of nodes to be available
    and add latency for coordination.

---

*Next chapter: [Chapter 14 — API Design](./14-api-design.md)*
