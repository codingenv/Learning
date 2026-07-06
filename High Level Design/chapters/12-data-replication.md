# Chapter 12: Data Replication

## Introduction

If partitioning is about **splitting** data across machines, replication is about **copying**
data across machines. Replication keeps identical copies of the same data on multiple nodes so
that if one machine dies, goes offline, or becomes unreachable, the system continues to operate.

Replication serves three critical purposes:
- **High availability** — the system keeps working even when nodes fail
- **Durability** — data survives hardware failures (disk crashes, data center outages)
- **Read scalability** — spread read queries across multiple copies

This chapter covers the three fundamental replication architectures, their trade-offs, and how
real-world systems implement them.

---

## 12.1 Why Replicate Data?

| Goal                  | How Replication Achieves It                                    |
|-----------------------|----------------------------------------------------------------|
| **Availability**      | If one replica fails, others serve requests                    |
| **Durability**        | Data exists on multiple disks/machines/data centers            |
| **Read throughput**   | Read queries can be spread across replicas                     |
| **Latency reduction** | Place replicas closer to users geographically                  |
| **Disaster recovery** | Survive entire data center or region failures                  |

**Critical distinction:** Replication is NOT the same as backup. Replication provides real-time
copies for availability. Backups provide point-in-time snapshots for recovery from logical errors
(accidental deletion, bugs, corruption).

---

## 12.2 Single-Leader (Master-Slave) Replication

The most common and simplest replication strategy. One node is the **leader** (master/primary);
all other nodes are **followers** (slaves/replicas/secondaries).

### How It Works

```
                    Writes (INSERT, UPDATE, DELETE)
                              |
                              v
                    +-------------------+
                    |     LEADER        |
                    |   (Primary)       |
                    |                   |
                    | Write-Ahead Log   |
                    +-------------------+
                      /       |        \
                     /        |         \     Replication Stream
                    v         v          v    (WAL / binlog / oplog)
           +----------+ +----------+ +----------+
           | Follower | | Follower | | Follower |
           |    1     | |    2     | |    3     |
           +----------+ +----------+ +----------+
                ^             ^            ^
                |             |            |
              Reads         Reads        Reads
           (Queries)     (Queries)    (Queries)
```

**Rules:**
1. ALL writes go to the leader
2. The leader writes changes to its **replication log** (write-ahead log, binlog, oplog)
3. Followers consume the log and apply the same changes
4. Reads can go to the leader OR any follower

### Synchronous vs. Asynchronous Replication

```
SYNCHRONOUS:
  Client --> Leader: "INSERT INTO users ..."
  Leader --> Follower 1: "Apply this change"
  Leader --> Follower 2: "Apply this change"
  Follower 1 --> Leader: "Done"
  Follower 2 --> Leader: "Done"
  Leader --> Client: "OK, committed"         <-- waits for ALL followers

ASYNCHRONOUS:
  Client --> Leader: "INSERT INTO users ..."
  Leader: writes to local storage
  Leader --> Client: "OK, committed"         <-- returns immediately
  Leader --> Follower 1: "Apply this change" <-- happens in background
  Leader --> Follower 2: "Apply this change" <-- happens in background

SEMI-SYNCHRONOUS:
  Client --> Leader: "INSERT INTO users ..."
  Leader --> Follower 1: "Apply this change"
  Follower 1 --> Leader: "Done"
  Leader --> Client: "OK, committed"         <-- waits for ONE follower
  Leader --> Follower 2: "Apply this change" <-- background for the rest
```

| Mode              | Durability       | Write Latency | Availability Impact           |
|-------------------|------------------|---------------|-------------------------------|
| Synchronous       | Strongest        | Highest       | One slow follower blocks all  |
| Semi-synchronous  | Strong           | Medium        | Blocked only if sync replica  |
|                   |                  |               | is down                       |
| Asynchronous      | Risk of data loss| Lowest        | Follower failure has no       |
|                   | on leader crash  |               | impact on writes              |

### Replication Lag

With asynchronous replication, followers may be **seconds or even minutes behind** the leader.
This creates consistency anomalies:

```
Timeline:
  t=0: Client writes "name=Alice" to Leader
  t=1: Leader acknowledges write to client
  t=2: Client reads from Follower 2 --> gets OLD data (name not yet updated)
  t=3: Follower 2 receives and applies the change
  t=4: Client reads from Follower 2 --> gets "name=Alice" (correct)

  The client wrote data and then couldn't read its own write!
```

### Solutions for Replication Lag

| Pattern                        | How It Works                                          |
|--------------------------------|-------------------------------------------------------|
| **Read-after-write consistency** | After a write, read from leader for a short window  |
| **Monotonic reads**            | Pin a user's reads to the same replica (sticky sessions)|
| **Causal consistency**         | Track dependencies; don't show effect before cause    |
| **Synchronous read replica**   | At least one follower is synchronous (guaranteed fresh)|

### Pros and Cons of Single-Leader

| Pros                                        | Cons                                          |
|---------------------------------------------|-----------------------------------------------|
| Simple to understand and implement          | Leader is a write bottleneck                  |
| No write conflicts (single authority)       | Leader failure requires failover              |
| Well-supported by all major databases       | Replication lag with async replication         |
| Easy to reason about consistency            | Cross-region writes are slow (remote leader)  |

---

## 12.3 Multi-Leader Replication

Multiple nodes accept writes simultaneously. Each leader replicates its changes to all other leaders.

### Architecture

```
           Data Center 1                    Data Center 2
        +----------------+              +----------------+
        |   Leader A     | <==========> |   Leader B     |
        |   (accepts     |  bi-dir      |   (accepts     |
        |    writes)     |  replication  |    writes)     |
        +----------------+              +----------------+
           /        \                      /        \
          v          v                    v          v
     +--------+ +--------+         +--------+ +--------+
     | Follow | | Follow |         | Follow | | Follow |
     | er A1  | | er A2  |         | er B1  | | er B2  |
     +--------+ +--------+         +--------+ +--------+
```

### Use Cases

1. **Multi-datacenter operation:** Each DC has a local leader for low-latency writes
2. **Offline-capable applications:** Each device has a local "leader" (phone, laptop)
   that syncs when reconnected — Google Docs, Notion, calendar apps
3. **Collaborative editing:** Multiple users editing the same document simultaneously

### The Conflict Problem

When two leaders accept conflicting writes to the same record:

```
Timeline:
  t=0: Leader A: UPDATE users SET name='Alice' WHERE id=1
  t=0: Leader B: UPDATE users SET name='Alicia' WHERE id=1
  t=1: Leader A receives B's change -- CONFLICT! name='Alice' or 'Alicia'?
  t=1: Leader B receives A's change -- CONFLICT! name='Alicia' or 'Alice'?
```

### Conflict Resolution Strategies

#### 1. Last-Write-Wins (LWW)

Attach a timestamp to every write. The write with the latest timestamp wins.

```
Leader A: {name: 'Alice',  timestamp: 1000}
Leader B: {name: 'Alicia', timestamp: 1001}  <-- wins (later timestamp)

Result on all nodes: name = 'Alicia'
```

**Problem:** LWW silently discards data. If both writes are meaningful, one is lost forever.
Also, clock synchronization across data centers is imperfect.

#### 2. Custom Conflict Resolution

Let application code decide how to merge conflicts:

```python
def resolve_conflict(version_a, version_b):
    # Application-specific logic
    if version_a.updated_by == 'admin':
        return version_a  # Admin writes always win
    if version_b.timestamp > version_a.timestamp:
        return version_b
    return merge(version_a, version_b)  # Custom merge logic
```

#### 3. CRDTs (Conflict-free Replicated Data Types)

Data structures mathematically guaranteed to converge without conflicts:

```
G-Counter (Grow-only counter):
  Each node maintains its own counter:

  Node A: {A: 5, B: 0, C: 0}  -- A has seen 5 increments
  Node B: {A: 3, B: 7, C: 0}  -- B has seen 7 increments, knows A had 3
  Node C: {A: 5, B: 4, C: 2}  -- C has seen 2 increments

  To merge: take MAX of each node's counter:
  Merged:  {A: 5, B: 7, C: 2}  -- total count = 14

  This always converges to the correct value, regardless of message ordering!
```

**Used by:** Redis (CRDTs for distributed counters), Riak, Figma (for collaborative editing).

---

## 12.4 Leaderless Replication

No single node is the authority. Clients write to **multiple replicas simultaneously** and read
from **multiple replicas simultaneously**, using quorum rules to ensure correctness.

### Dynamo-Style Architecture

```
  Client Write (W=2 out of N=3 replicas):

  Client --+--> Replica 1: "Write name=Alice"  --> OK
           +--> Replica 2: "Write name=Alice"  --> OK     } W=2 ACKs received
           +--> Replica 3: "Write name=Alice"  --> FAIL   } (write succeeds)

  Client Read (R=2 out of N=3 replicas):

  Client --+--> Replica 1: "Read name" --> "Alice" (v2)
           +--> Replica 2: "Read name" --> "Alice" (v2)   } R=2 responses
           +--> Replica 3: "Read name" --> "Bob"   (v1)   } Take latest version

  Quorum condition: R + W > N ensures overlap
  With N=3, W=2, R=2: 2 + 2 > 3 ✓ (guaranteed to read at least one up-to-date copy)
```

### Quorum Parameters Explained

| Configuration | Behavior                                             | Use Case                      |
|---------------|------------------------------------------------------|-------------------------------|
| R=1, W=N      | Fast reads, slow writes, high write durability       | Read-heavy workloads          |
| R=N, W=1      | Slow reads, fast writes, risk of reading stale data  | Write-heavy workloads         |
| R=N/2+1, W=N/2+1 | Balanced reads and writes                        | General purpose               |
| R=1, W=1      | Fastest but NO consistency guarantee (R+W ≤ N)       | Best-effort, analytics        |

### Read Repair

When a client reads from multiple replicas and detects stale data:

```
Client reads from 3 replicas:
  Replica 1: name="Alice" (version 5)  <-- latest
  Replica 2: name="Alice" (version 5)  <-- latest
  Replica 3: name="Bob"   (version 3)  <-- STALE!

Client returns "Alice" to application AND sends a write to Replica 3:
  "Hey Replica 3, update to name=Alice version 5"

This passively repairs stale data on every read.
```

### Anti-Entropy

A background process continuously compares data across replicas and fixes inconsistencies:

```
Anti-Entropy Process (runs periodically):

  Compare Replica 1 <-> Replica 2:
    - Hash tree (Merkle tree) comparison
    - Find differing segments
    - Sync the differences

  Compare Replica 2 <-> Replica 3:
    - Same process
    - Eventually all replicas converge
```

**Merkle trees** make this efficient: instead of comparing every key, compare hash summaries
of key ranges. Only drill down into ranges where hashes differ.

### Sloppy Quorum and Hinted Handoff

When a required replica is temporarily unavailable:

```
Normal quorum (strict): Write to nodes {A, B, C}
  Node C is down!
  Strict quorum: REJECT the write (only 2 of 3 available)

Sloppy quorum: Write to {A, B, D} where D is a temporary stand-in
  Node D holds a "hint": "This data belongs to C. Send it when C recovers."

When C comes back online:
  D --> C: "Here are the writes you missed" (hinted handoff)
  D deletes the temporary data
```

**Trade-off:** Sloppy quorum improves availability but weakens consistency guarantees. The
data on node D doesn't satisfy the true quorum — it's just a temporary measure.

---

## 12.5 Chain Replication

An alternative model where replicas form a **chain**. Writes enter at the **head** and propagate
through the chain. Reads are served from the **tail** (which has the most up-to-date confirmed data).

```
  Write                                                          Read
  ---->  +--------+     +--------+     +--------+     +--------+  <----
         |  HEAD  | --> | Node 2 | --> | Node 3 | --> |  TAIL  |
         +--------+     +--------+     +--------+     +--------+
           (write        (forward       (forward       (serve reads;
            entry)        to next)       to next)       all data is
                                                        committed)
```

| Pros                                         | Cons                                          |
|----------------------------------------------|-----------------------------------------------|
| Strong consistency (reads from tail are       | Write latency = sum of all hop latencies     |
| guaranteed to reflect all committed writes)  | Chain length limits throughput                 |
| Simple failure handling (remove failed node) | Head failure = write unavailability            |
| High read throughput (tail is dedicated)     | More complex than single-leader for failures   |

**Used by:** Microsoft Azure Storage, HDFS (pipeline replication), Ceph.

---

## 12.6 Comparison: Replication Architectures

| Feature                | Single-Leader        | Multi-Leader         | Leaderless            |
|------------------------|----------------------|----------------------|-----------------------|
| Write path             | Leader only          | Any leader           | Any replica (quorum)  |
| Read path              | Leader or follower   | Local leader/follower| Any replica (quorum)  |
| Consistency            | Strong (sync) or     | Eventual; conflicts  | Eventual; tunable     |
|                        | eventual (async)     | must be resolved     | via quorum params     |
| Write latency          | Low (local leader)   | Low (local leader)   | Medium (wait for W)   |
| Conflict handling      | No conflicts         | Must resolve         | Must resolve (LWW,    |
|                        |                      | (LWW, CRDTs, custom) | version vectors)      |
| Failover complexity    | Leader election      | Simpler (other       | No failover needed    |
|                        | required             | leaders available)   |                       |
| Multi-DC support       | Cross-DC writes slow | Native multi-DC      | Native multi-DC       |
| Implementation examples| PostgreSQL, MySQL,   | CouchDB, Tungsten    | Cassandra, DynamoDB,  |
|                        | MongoDB              | Replicator           | Riak, Voldemort       |

---

## 12.7 Real-World Replication

### Amazon DynamoDB

- **Architecture:** Leaderless replication within a partition
- **Quorum:** Configurable consistency — `EVENTUAL` (R=1) or `STRONG` (R=N, reads from leader)
- **Replication factor:** 3 replicas across Availability Zones
- **Conflict resolution:** Last-writer-wins using vector clocks for conflict detection
- **Global Tables:** Multi-region, multi-leader replication for global applications

### Apache Cassandra

- **Architecture:** Leaderless, Dynamo-inspired
- **Tunable consistency:** `ONE`, `QUORUM`, `ALL`, `LOCAL_QUORUM`, `EACH_QUORUM`
  ```
  // Write with quorum consistency
  INSERT INTO users (id, name) VALUES (1, 'Alice')
  USING CONSISTENCY QUORUM;

  // Fast read with eventual consistency
  SELECT * FROM users WHERE id = 1
  USING CONSISTENCY ONE;
  ```
- **Replication strategy:**
  - `SimpleStrategy` — replicate to next N nodes on the ring
  - `NetworkTopologyStrategy` — specify replication factor per data center
- **Anti-entropy:** Merkle tree-based `nodetool repair`

### PostgreSQL

- **Architecture:** Single-leader (streaming replication)
- **Replication log:** Write-Ahead Log (WAL)
- **Modes:**
  - Asynchronous (default) — minimal write latency
  - Synchronous — `synchronous_commit = on` with `synchronous_standby_names`
- **Logical replication:** Publish/subscribe model for selective table replication
- **Failover:** Tools like Patroni, pg_auto_failover for automatic leader election
  ```
  -- Check replication status
  SELECT * FROM pg_stat_replication;

  -- Check replication lag
  SELECT now() - pg_last_xact_replay_timestamp() AS replication_lag;
  ```

---

## 12.8 Replication Lag: A Deeper Look

Replication lag is the time between a write being committed on the leader and that write being
visible on a follower. Even small lag causes surprising anomalies:

### Anomaly 1: Reading Your Own Writes

```
User: POST "Hello World"       --> Leader (committed)
User: GET /my/posts             --> Follower (hasn't received write yet)
User sees: empty feed           --> "Where did my post go?!"
```

**Fix:** After writes, route that user's reads to the leader for a few seconds.

### Anomaly 2: Non-Monotonic Reads

```
User reads from Follower A:    --> Sees post from 10:00 AM
User reads from Follower B:    --> Sees NO post (Follower B is behind)
User reads from Follower A:    --> Sees post from 10:00 AM again

The post "disappeared" and then "reappeared" — the user went back in time!
```

**Fix:** Sticky sessions — pin each user to a specific follower for the duration of their session.

### Anomaly 3: Causality Violation

```
User A posts: "Can anyone help me move this weekend?"
User B replies: "Sure, I'll bring my truck!"

A follower might show B's reply BEFORE A's question — because B's write
was replicated first. This violates causal ordering.
```

**Fix:** Track causal dependencies using version vectors or logical timestamps.

---

## 12.9 Key Takeaways

1. **Replication serves three purposes:** availability (survive failures), durability
   (don't lose data), and read scalability (spread read load).

2. **Single-leader replication** is the simplest and most common approach. All writes go
   through one node, eliminating write conflicts. Most relational databases default to this.

3. **Synchronous replication** guarantees durability but increases write latency.
   **Asynchronous replication** is faster but risks data loss if the leader crashes.
   **Semi-synchronous** is the practical middle ground.

4. **Multi-leader replication** is essential for multi-datacenter deployments where you
   need low-latency writes in every region. The price: you must handle write conflicts.

5. **Conflict resolution is hard.** Last-write-wins is simple but loses data. CRDTs
   converge automatically but only work for specific data structures. Custom resolution
   puts the burden on application developers.

6. **Leaderless replication** (Dynamo-style) uses quorum reads/writes for tunable
   consistency. The formula `R + W > N` guarantees you'll read at least one current copy.

7. **Replication lag** causes subtle bugs: stale reads, non-monotonic reads, and causality
   violations. Use read-after-write consistency, sticky sessions, and causal tracking to
   mitigate these issues.

8. **Anti-entropy** (background sync) and **read repair** (sync on read) help leaderless
   systems converge without relying solely on quorum guarantees.

9. **Chain replication** provides strong consistency with simple mechanics — write at the
   head, read at the tail — at the cost of write latency proportional to chain length.

10. **Choose your replication architecture based on your requirements:**
    - Need simplicity and strong consistency? → Single-leader
    - Need multi-DC writes with low latency? → Multi-leader
    - Need maximum availability and partition tolerance? → Leaderless

---

*Next chapter: [Chapter 13 — Consistency Models and Consensus](./13-consistency.md)*
