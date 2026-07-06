# Chapter 25: Consensus Algorithms

## Introduction

In a distributed system, multiple nodes (servers) must work together to provide a
reliable service. But nodes can crash, networks can partition, and messages can be
delayed or lost. Despite all this, the nodes need to **agree on something** — a
value, a leader, a configuration, the order of operations.

**Consensus** is the process by which distributed nodes agree on a single value or
decision. It is one of the most fundamental problems in distributed computing and
underpins critical infrastructure: distributed databases, configuration management,
leader election, and distributed locking.

This chapter explores the theory and practice of consensus algorithms, from the
foundational (and notoriously difficult) Paxos to the more approachable Raft and
ZAB, and their real-world applications.

---

## 25.1 What Is Consensus?

Consensus is the problem of getting `N` nodes to agree on a single value, even when
some nodes may fail.

### Formal Requirements

| Property             | Description                                                |
|----------------------|------------------------------------------------------------|
| **Agreement**        | All correct (non-faulty) nodes decide on the same value    |
| **Validity**         | The decided value was proposed by some node                |
| **Termination**      | Every correct node eventually decides a value              |
| **Integrity**        | Each node decides at most once                             |

### Why Is Consensus Needed?

| Use Case                    | What Nodes Must Agree On                        |
|-----------------------------|-------------------------------------------------|
| **Leader Election**         | Which node is the current leader                |
| **Configuration Mgmt**     | Current cluster configuration (membership)      |
| **Distributed Locks**      | Which node holds the lock                       |
| **Atomic Broadcast**       | The order of messages delivered to all nodes     |
| **State Machine Replication** | The sequence of commands applied to state     |
| **Distributed Transactions** | Whether to commit or abort                    |

---

## 25.2 The FLP Impossibility Theorem

In 1985, Fischer, Lynch, and Paterson proved that **in an asynchronous distributed
system where even one node can crash, it is impossible to guarantee consensus**.

### What This Means

- In a purely asynchronous system (no time bounds on message delivery), you cannot
  distinguish between a **slow node** and a **crashed node**
- Any algorithm that waits for all responses might wait forever
- Any algorithm that proceeds without all responses might violate agreement

### Practical Implications

Real systems work around FLP by:
1. **Using timeouts** (partial synchrony model) — if no response within X ms, assume
   the node is down. This can lead to mistakes (a slow node mistaken for dead), but
   progress is made.
2. **Randomization** — algorithms like randomized consensus break symmetry using
   coin flips, achieving consensus with high probability.
3. **Failure detectors** — unreliable but useful heuristics about which nodes are alive.

> **Key Insight:** FLP proves that **deterministic** consensus is impossible in a
> **purely asynchronous** system. Practical algorithms (Paxos, Raft) assume partial
> synchrony and use timeouts to ensure progress.

---

## 25.3 Paxos

**Paxos** was invented by Leslie Lamport in 1989 (published in 1998). It is the
foundational consensus algorithm and has influenced all subsequent designs.

### Roles

| Role          | Description                                                  |
|---------------|--------------------------------------------------------------|
| **Proposer**  | Proposes a value for the nodes to agree on                   |
| **Acceptor**  | Votes on proposals and remembers what it voted for           |
| **Learner**   | Learns the decided value once consensus is reached           |

A single node can play multiple roles simultaneously.

### Basic Paxos (Single-Decree)

Basic Paxos reaches consensus on a **single value**. It uses two phases:

#### Phase 1: Prepare

```
Proposer                          Acceptors (majority needed)
   |                               A1      A2      A3
   |                               |       |       |
   |-- Prepare(n=1) ------------->|       |       |
   |-- Prepare(n=1) ---------------------->|       |
   |-- Prepare(n=1) ------------------------------>|
   |                               |       |       |
   |<-- Promise(n=1, no prev) ----|       |       |
   |<-- Promise(n=1, no prev) ------------|       |
   |<-- Promise(n=1, no prev) --------------------|
   |                               |       |       |
   | (Received promises from majority: 3/3)        |
```

**Prepare(n):** Proposer sends a proposal number `n` to all acceptors.
**Promise:** Acceptor promises not to accept any proposal with number < `n`.
If the acceptor already accepted a value for a previous proposal, it returns
that value.

#### Phase 2: Accept

```
Proposer                          Acceptors
   |                               A1      A2      A3
   |                               |       |       |
   |-- Accept(n=1, v="X") ------->|       |       |
   |-- Accept(n=1, v="X") ---------------->|       |
   |-- Accept(n=1, v="X") ------------------------>|
   |                               |       |       |
   |<-- Accepted(n=1, v="X") -----|       |       |
   |<-- Accepted(n=1, v="X") -------------|       |
   |<-- Accepted(n=1, v="X") ---------------------|
   |                               |       |       |
   | CONSENSUS REACHED: value = "X"                |
   |                               |       |       |
   | Notify Learners: value = "X"                  |
```

**Accept(n, v):** Proposer sends the value to all acceptors with proposal number `n`.
**Accepted:** Acceptor accepts the value if it hasn't promised to a higher proposal.

### Why Paxos Is Famously Difficult

Lamport's original paper used a metaphor of legislators on the Greek island of Paxos,
which was unconventional and confusing. But the real difficulty lies in:

1. **Competing proposers**: Multiple proposers can create **dueling proposals** that
   prevent progress (livelock).
2. **Edge cases**: What if a proposer crashes between Phase 1 and Phase 2? What if
   an acceptor already accepted a different value?
3. **Gap between theory and implementation**: Basic Paxos specifies consensus on one
   value, but real systems need consensus on a **sequence** of values (log replication).
4. **Many variants**: Multi-Paxos, Fast Paxos, Cheap Paxos, Flexible Paxos — each
   addressing different limitations.

### Multi-Paxos

In practice, you need consensus on a **sequence** of values (like a replicated log).
Multi-Paxos optimizes for this by:

1. **Electing a stable leader** (distinguished proposer)
2. The leader skips Phase 1 for subsequent proposals (it already has promises)
3. Only Phase 2 is needed for each new log entry → much faster

```
Multi-Paxos with stable leader:

Leader (Proposer)              Acceptors
   |                           A1    A2    A3
   |                           |     |     |
   | Phase 1 (once, at start)  |     |     |
   |-- Prepare(n=1) --------->|     |     |
   |<-- Promise --------------|     |     |
   |      (repeated for all)   |     |     |
   |                           |     |     |
   | Phase 2 (for each value)  |     |     |
   |-- Accept(n=1, v1) ------>|     |     |
   |-- Accept(n=1, v2) ------>|     |     |
   |-- Accept(n=1, v3) ------>|     |     |
   |   ... (no Phase 1 needed)|     |     |
```

---

## 25.4 Raft

**Raft** was designed by Diego Ongaro and John Ousterhout in 2013 with the explicit
goal of being **easy to understand**. It achieves the same guarantees as Multi-Paxos
but with a clearer, more structured design.

### Core Concepts

Raft divides consensus into three sub-problems:
1. **Leader Election** — choosing a single leader
2. **Log Replication** — leader replicates log entries to followers
3. **Safety** — ensuring correctness (committed entries are never lost)

### Node States

Every node in a Raft cluster is in one of three states:

```
                    +-------------------------------------------+
                    |                                           |
                    |  Times out,              Receives votes   |
                    |  starts election         from majority    |
                    v                                           |
              +-----------+         +-----------+         +-----------+
              |           |         |           |         |           |
 Start -----> | FOLLOWER  |-------->| CANDIDATE |-------->|  LEADER   |
              |           |         |           |         |           |
              +-----------+         +-----------+         +-----------+
                    ^                     |                     |
                    |                     |                     |
                    |   Discovers         |   Discovers         |
                    |   higher term       |   higher term       |
                    +---------------------+---------------------+
```

### Leader Election

**Terms:** Raft divides time into **terms** — sequential integers. Each term has
at most one leader. Terms act as a **logical clock**.

```
Term 1          Term 2          Term 3          Term 4
+------------+  +------------+  +------+        +------------+
| Leader: S1 |  | Leader: S3 |  | No   |        | Leader: S2 |
| (normal    |  | (normal    |  |leader|        | (normal    |
|  operation)|  |  operation)|  | (elec|        |  operation)|
+------------+  +------------+  | tion)|        +------------+
                                | split|
                                +------+
```

**Election Process:**

1. A follower's **election timeout** expires (hasn't heard from leader)
2. Follower becomes a **Candidate**, increments its term, votes for itself
3. Candidate sends **RequestVote** RPCs to all other nodes
4. Each node votes for **at most one** candidate per term (first-come-first-served)
5. If candidate receives votes from a **majority**, it becomes **Leader**
6. If no majority (split vote), a new election starts with a new term

```
Node S1 (Follower)     Node S2 (Follower)     Node S3 (Follower)
     |                      |                      |
  [timeout expires]         |                      |
     |                      |                      |
  Becomes CANDIDATE         |                      |
  Term: 2                   |                      |
  Votes for self            |                      |
     |                      |                      |
     |--RequestVote(T=2)--->|                      |
     |--RequestVote(T=2)----------------------->  |
     |                      |                      |
     |<--VoteGranted--------|                      |
     |<--VoteGranted-----------------------------|
     |                      |                      |
  Received 3/3 votes (including self)              |
  Becomes LEADER (Term 2)                          |
     |                      |                      |
     |--Heartbeat---------->|                      |
     |--Heartbeat------------------------------>  |
```

**Randomized Timeouts:** To prevent repeated split votes, each node uses a
**random** election timeout (e.g., 150-300ms). This ensures nodes don't all
start elections simultaneously.

### Log Replication

Once a leader is elected, it handles all client requests and replicates log
entries to followers.

```
Leader (S1)                 Follower (S2)           Follower (S3)
   |                            |                        |
   |<--Client: SET x=5          |                        |
   |                            |                        |
   | Append to local log:       |                        |
   | [idx:1, term:2, SET x=5]   |                        |
   |                            |                        |
   |--AppendEntries(idx:1)----->|                        |
   |--AppendEntries(idx:1)------------------------------>|
   |                            |                        |
   |<--ACK---------------------|                        |
   |<--ACK----------------------------------------------|
   |                            |                        |
   | Majority ACKed (3/3)       |                        |
   | COMMIT entry idx:1         |                        |
   | Apply to state machine     |                        |
   |                            |                        |
   |--Commit notification------>|  (apply to state machine)
   |--Commit notification------------------------------>|
   |                            |                        |
   |--Client response: OK       |                        |
```

**Log Structure:**

```
Leader's Log:
+-------+--------+-----------+
| Index | Term   | Command   |
+-------+--------+-----------+
|   1   |   1    | SET x=1   |
|   2   |   1    | SET y=2   |
|   3   |   2    | SET x=5   |  <-- committed (majority ACKed)
|   4   |   2    | SET z=3   |  <-- uncommitted (waiting for ACKs)
+-------+--------+-----------+
```

### Safety Properties

**Election Safety:** At most one leader per term. Guaranteed by:
- Each node votes at most once per term
- A leader needs a majority of votes

**Leader Completeness:** If a log entry is committed in term `T`, it will be present
in the log of all leaders in terms > `T`. Guaranteed by:
- A candidate must have all committed entries to win an election
- Voters reject candidates with less up-to-date logs

**Log Matching:** If two logs contain an entry with the same index and term, all
preceding entries are identical. Guaranteed by:
- AppendEntries RPC includes the previous entry's index and term
- Followers reject entries that don't match

### Membership Changes

When adding or removing nodes from the cluster, Raft uses a **joint consensus**
approach:
1. The leader proposes a configuration change as a special log entry
2. During transition, decisions require majorities from **both** old and new configurations
3. Once committed, the new configuration takes effect

This ensures safety during membership changes — no split-brain is possible.

---

## 25.5 ZAB (ZooKeeper Atomic Broadcast)

**ZAB** is the consensus protocol used by **Apache ZooKeeper**. It was designed
specifically for ZooKeeper's primary-backup system architecture.

### How ZAB Works

ZAB has two modes:

**1. Recovery Mode (Leader Election)**
When the system starts or the leader fails, ZAB elects a new leader:
- Nodes exchange their latest transaction ID (zxid)
- The node with the most up-to-date log is preferred
- Once a leader is elected, it synchronizes all followers

**2. Broadcast Mode (Normal Operation)**
Once a leader is elected:

```
Leader                       Followers
  |                          F1    F2    F3
  |                          |     |     |
  |<-- Client Write Request  |     |     |
  |                          |     |     |
  | Create proposal (zxid)   |     |     |
  |                          |     |     |
  |-- PROPOSE(zxid, data) -->|     |     |
  |-- PROPOSE(zxid, data) -------->|     |
  |-- PROPOSE(zxid, data) ------------->|
  |                          |     |     |
  |<-- ACK ------------------|     |     |
  |<-- ACK ------------------------|     |
  |                          |     |     |
  | Majority ACKed           |     |     |
  |                          |     |     |
  |-- COMMIT(zxid) --------->|     |     |
  |-- COMMIT(zxid) --------------->|     |
  |-- COMMIT(zxid) ------------------>  |
  |                          |     |     |
  | Apply to state           |     |     |
```

### ZAB vs Paxos

ZAB is NOT Paxos, though they solve similar problems:
- **Paxos** is designed for consensus on individual values
- **ZAB** is designed for **atomic broadcast** — delivering messages in total order
- ZAB guarantees **FIFO ordering** of proposals from the same leader
- ZAB has an explicit leader election and recovery phase

---

## 25.6 Comparison: Paxos vs Raft vs ZAB

| Feature                | Paxos                    | Raft                     | ZAB                       |
|------------------------|--------------------------|--------------------------|---------------------------|
| **Year**               | 1989 (pub. 1998)         | 2013                     | 2008                      |
| **Goal**               | General consensus        | Understandable consensus | ZooKeeper replication     |
| **Leader**             | Optional (Multi-Paxos)   | Required (always)        | Required (always)         |
| **Leader Election**    | Not specified             | Clear algorithm          | Explicit phase            |
| **Log Replication**    | Implicit in Multi-Paxos  | Explicit, structured     | Atomic broadcast          |
| **Understandability**  | Very difficult            | Designed to be easy      | Moderate                  |
| **Ordering**           | Per-slot                 | Log-based                | FIFO + total order        |
| **Recovery**           | Complex                  | Log-based, straightforward| Explicit recovery phase  |
| **Implementations**    | Google Chubby, Spanner   | etcd, Consul, CockroachDB| Apache ZooKeeper          |
| **Paper Clarity**      | Notoriously unclear      | Excellent                | Good                      |

---

## 25.7 Practical Uses of Consensus

### etcd (Uses Raft)

**etcd** is a distributed key-value store that powers **Kubernetes**.

```
Kubernetes Architecture:
+------------------+
| kubectl          |
+--------+---------+
         |
+--------v---------+
| kube-apiserver    |
+--------+---------+
         |
+--------v---------+     Uses Raft for
|     etcd          |     replication
| +-node1-+ +-node2-+ +-node3-+
| | Leader | |Follower| |Follower|
| +--------+ +--------+ +--------+
+------------------+
```

**What etcd stores:**
- Cluster configuration (what pods should be running where)
- Service discovery information
- Secrets and config maps
- Lease / lock information

**Why Raft:** etcd needs strong consistency for Kubernetes configuration.
A stale read could cause pods to be scheduled on non-existent nodes.

### Apache ZooKeeper (Uses ZAB)

**ZooKeeper** is a centralized service for distributed coordination.

```
ZooKeeper Data Model (ZNode tree):
/
├── /config
│   ├── /config/database_url    = "postgres://..."
│   └── /config/feature_flags   = "{...}"
├── /locks
│   ├── /locks/order-processing = (ephemeral, owned by Node A)
│   └── /locks/batch-job        = (ephemeral, owned by Node B)
├── /election
│   ├── /election/leader-0001   = "node-A" (elected leader)
│   ├── /election/leader-0002   = "node-B"
│   └── /election/leader-0003   = "node-C"
└── /services
    ├── /services/payment/instance-1 = "10.0.0.1:8080"
    └── /services/payment/instance-2 = "10.0.0.2:8080"
```

**Used by:** Kafka (broker coordination), HBase (master election),
Hadoop (resource management), Solr (cluster state)

### Consul (Uses Raft)

**Consul** by HashiCorp provides service discovery, health checking,
and distributed KV store.

```
Consul Cluster:
+---server---+    +---server---+    +---server---+
| Consul     |    | Consul     |    | Consul     |
| (Leader)   |<-->| (Follower) |<-->| (Follower) |
| Raft       |    | Raft       |    | Raft       |
+-----+------+    +-----+------+    +-----+------+
      |                  |                  |
+-----v------+    +------v-----+    +------v-----+
| Agent      |    | Agent      |    | Agent      |
| (Client)   |    | (Client)   |    | (Client)   |
| + Services |    | + Services |    | + Services |
+------------+    +------------+    +------------+
```

---

## 25.8 Leader Election Patterns

### The Split-Brain Problem

When a network partition occurs, nodes on each side of the partition may
independently elect a leader, resulting in **two leaders** (split-brain).

```
Normal:
  [Node A: Leader] <---> [Node B] <---> [Node C]

Partition:
  [Node A: Leader]    |    [Node B: NEW Leader] <---> [Node C]
                      |
  (network partition) |
                      
  TWO LEADERS! Split-brain! Conflicting writes!
```

### Fencing Tokens

To prevent split-brain damage, use **fencing tokens** — monotonically increasing
numbers assigned when a leader is elected.

```
Epoch 1: Leader A gets fencing token = 1
  A writes to storage with token 1 → ACCEPTED

Network partition: B becomes leader
Epoch 2: Leader B gets fencing token = 2
  B writes to storage with token 2 → ACCEPTED

A recovers (still thinks it's leader):
  A writes to storage with token 1 → REJECTED (token 1 < current token 2)
```

The storage system tracks the highest token it has seen and rejects writes
from outdated leaders.

### Quorum-Based Leader Election

To prevent split-brain, require a **majority quorum** to elect a leader.
In a cluster of N nodes, a leader needs `floor(N/2) + 1` votes.

```
5-node cluster: needs 3 votes to elect leader

Partition: [A, B] | [C, D, E]
  - Side [A, B]: only 2 nodes → cannot form majority (need 3) → NO leader
  - Side [C, D, E]: 3 nodes → CAN form majority → elect leader
  
  Result: AT MOST ONE leader at any time ✓
```

This is why clusters use **odd numbers** of nodes (3, 5, 7):
- 3 nodes: tolerates 1 failure (need 2 for quorum)
- 5 nodes: tolerates 2 failures (need 3 for quorum)
- 7 nodes: tolerates 3 failures (need 4 for quorum)

| Cluster Size | Quorum | Failures Tolerated |
|-------------|--------|-------------------|
| 1           | 1      | 0                 |
| 2           | 2      | 0 (worse than 1!) |
| 3           | 2      | 1                 |
| 4           | 3      | 1 (same as 3)     |
| 5           | 3      | 2                 |
| 7           | 4      | 3                 |

> **Why not even-sized clusters?** A 4-node cluster tolerates only 1 failure
> (same as 3 nodes) but requires more resources. Odd sizes maximize fault
> tolerance per node.

---

## 25.9 Consensus in Modern Databases

Many modern distributed databases use consensus internally:

| Database         | Consensus Protocol | Use                                  |
|------------------|--------------------|--------------------------------------|
| **CockroachDB**  | Raft               | Log replication across ranges        |
| **TiDB/TiKV**   | Raft               | Distributed KV storage layer         |
| **etcd**         | Raft               | KV store for Kubernetes              |
| **YugabyteDB**   | Raft               | Tablet replication                   |
| **Google Spanner**| Paxos             | Global distributed database          |
| **Google Chubby** | Paxos             | Distributed lock service             |
| **Kafka (KRaft)**| Raft               | Metadata management (replacing ZK)   |

### Google Spanner: Paxos at Global Scale

Spanner uses Paxos groups to replicate data across **continents**:

```
Spanner Paxos Group:
+--US East---+     +--Europe---+     +--Asia----+
| Replica 1  |<--->| Replica 2 |<--->| Replica 3|
| (Leader)   |     | (Follower)|     | (Follower)|
+------------+     +-----------+     +----------+

- Writes go through Paxos (majority of replicas)
- Reads from leader (or stale reads from followers)
- TrueTime API for global ordering of transactions
```

---

## 25.10 Key Takeaways

1. **Consensus** is the fundamental problem of getting distributed nodes to agree on
   a value. It underpins leader election, configuration management, distributed locks,
   and state machine replication.

2. **FLP Impossibility** proves deterministic consensus is impossible in asynchronous
   systems. Practical algorithms use timeouts (partial synchrony) to make progress.

3. **Paxos** is the foundational consensus algorithm but is notoriously difficult to
   understand and implement correctly. Multi-Paxos optimizes for repeated consensus
   with a stable leader.

4. **Raft** was designed for understandability. It separates consensus into leader
   election, log replication, and safety — each clearly defined. It is the most
   popular choice for new systems.

5. **ZAB** is ZooKeeper's protocol, optimized for atomic broadcast with FIFO ordering.
   It powers the coordination layer for many distributed systems.

6. **Leader election** requires quorum-based voting to prevent split-brain. Use odd-sized
   clusters (3, 5, 7) for maximum fault tolerance.

7. **Fencing tokens** prevent stale leaders from causing damage after a partition heals.

8. **Practical implementations**: etcd (Raft) powers Kubernetes, ZooKeeper (ZAB) powers
   Kafka and Hadoop, Consul (Raft) powers service discovery.

9. **Modern databases** (CockroachDB, Spanner, TiDB) use consensus algorithms internally
   for data replication and consistency.

10. **Consensus is expensive** — it requires multiple network round trips and majority
    agreement. Use it only where strong consistency is necessary, and design your
    system to minimize the number of consensus operations.

---

## Further Reading

- *Designing Data-Intensive Applications* by Martin Kleppmann (Ch. 8-9)
- Raft Paper: "In Search of an Understandable Consensus Algorithm" — Ongaro & Ousterhout
- Raft Visualization: https://thesecretlivesofdata.com/raft/
- Lamport's Paxos Paper: "The Part-Time Parliament"
- ZAB Paper: "Zab: High-performance broadcast for primary-backup systems"
- etcd documentation: https://etcd.io/docs/

---

*Previous: [Chapter 24 — Distributed Transactions](24-distributed-transactions.md)*
*Next: [Chapter 26 — Search Systems](26-search.md)*
