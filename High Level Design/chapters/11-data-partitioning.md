# Chapter 11: Data Partitioning and Sharding

## Introduction

Every successful application eventually faces a fundamental scaling challenge: **a single database
server cannot handle the growing volume of data and traffic forever.** When your tables reach
billions of rows, when write throughput exceeds what one machine can handle, or when your dataset
no longer fits on a single disk, you need to **partition** your data across multiple machines.

Data partitioning (often called **sharding** when applied horizontally) is the practice of splitting
a large dataset into smaller, more manageable pieces and distributing them across multiple database
nodes. This chapter covers the strategies, trade-offs, and real-world practices behind partitioning.

---

## 11.1 Why Partition Data?

| Problem                        | How Partitioning Helps                              |
|-------------------------------|-----------------------------------------------------|
| Dataset too large for one disk | Spread data across multiple machines                |
| Read/write throughput ceiling  | Parallelize queries across partitions               |
| Single point of failure        | Isolate failures to individual partitions           |
| Memory constraints             | Each node only needs to cache its own partition     |
| Geographic latency             | Place data closer to users in specific regions      |

**Key insight:** Partitioning turns a vertical scaling problem (bigger machine) into a horizontal
scaling problem (more machines) — which is far more cost-effective at scale.

---

## 11.2 Vertical Partitioning

Vertical partitioning splits a table **by columns** — different groups of columns are stored in
different databases or services.

### Example: User Profile vs. User Posts

```
BEFORE (single monolithic table):
+------------------------------------------------------------------+
| users                                                            |
|------------------------------------------------------------------|
| user_id | name | email | avatar_url | bio | post_id | post_text  |
+------------------------------------------------------------------+

AFTER (vertically partitioned):

+---------------------------------+    +-------------------------------+
| user_profiles_db                |    | user_posts_db                 |
|---------------------------------|    |-------------------------------|
| user_id | name | email | avatar |    | post_id | user_id | post_text|
+---------------------------------+    +-------------------------------+
         (reads: high, writes: low)          (reads: high, writes: high)
```

### When to Use Vertical Partitioning

- **Columns have different access patterns:** Profile data is read-heavy; post data is write-heavy.
- **Columns have vastly different sizes:** BLOBs (images, documents) can be moved to a separate store.
- **Microservice decomposition:** Each service owns its own data (bounded context in DDD).

### Pros and Cons

| Pros                                        | Cons                                          |
|---------------------------------------------|-----------------------------------------------|
| Natural fit for microservices architecture  | Cross-partition JOINs become expensive        |
| Each partition can scale independently      | Application logic must handle data assembly   |
| Simpler per-partition schemas               | Transactions across partitions are complex    |
| Can use different storage engines per group | More operational complexity                   |

---

## 11.3 Horizontal Partitioning (Sharding)

Horizontal partitioning (sharding) splits a table **by rows** — each shard contains a subset of
the rows with the same schema.

```
                    Full Users Table
    +------+----------+-------------------+--------+
    | id   | name     | email             | region |
    +------+----------+-------------------+--------+
    | 1    | Alice    | alice@mail.com    | US     |
    | 2    | Bob      | bob@mail.com      | EU     |
    | 3    | Charlie  | charlie@mail.com  | US     |
    | 4    | Diana    | diana@mail.com    | APAC   |
    | 5    | Eve      | eve@mail.com      | EU     |
    | 6    | Frank    | frank@mail.com    | APAC   |
    +------+----------+-------------------+--------+

                  After Sharding (by region):

  Shard 1 (US)              Shard 2 (EU)           Shard 3 (APAC)
  +----+---------+       +----+-------+          +----+--------+
  | 1  | Alice   |       | 2  | Bob   |          | 4  | Diana  |
  | 3  | Charlie |       | 5  | Eve   |          | 6  | Frank  |
  +----+---------+       +----+-------+          +----+--------+
```

---

## 11.4 Sharding Strategies

### 11.4.1 Range-Based Sharding

Assign rows to shards based on **value ranges** of the shard key.

```
Shard Key: last_name

  Shard 1: A - G          Shard 2: H - N         Shard 3: O - Z
  +-----------+           +-----------+           +-----------+
  | Adams     |           | Hamilton  |           | Patel     |
  | Brown     |           | Johnson   |           | Roberts   |
  | Clark     |           | Miller    |           | Smith     |
  | Garcia    |           | Nelson    |           | Williams  |
  +-----------+           +-----------+           +-----------+
```

| Pros                                    | Cons                                              |
|-----------------------------------------|---------------------------------------------------|
| Simple to implement and understand      | Prone to **hotspots** (uneven distribution)       |
| Efficient range queries                 | Shard 3 might get 60% of traffic if names skew   |
| Easy to add new ranges                  | Rebalancing requires data migration               |

### 11.4.2 Hash-Based Sharding

Apply a hash function to the shard key, then use modulo to determine the shard.

```
shard_number = hash(shard_key) % number_of_shards

Example with 3 shards:
  hash("alice")   = 948372  -> 948372 % 3 = 0  -> Shard 0
  hash("bob")     = 283746  -> 283746 % 3 = 0  -> Shard 0
  hash("charlie") = 571924  -> 571924 % 3 = 1  -> Shard 1
  hash("diana")   = 195837  -> 195837 % 3 = 0  -> Shard 0
  hash("eve")     = 462819  -> 462819 % 3 = 0  -> Shard 0  <-- hotspot!
```

| Pros                                     | Cons                                             |
|------------------------------------------|--------------------------------------------------|
| More uniform distribution than range     | Range queries become scatter-gather operations   |
| Easy to compute shard from key           | Adding/removing shards = massive data reshuffling|
| No need for a lookup table               | hash(key) % N changes when N changes!            |

**The fundamental problem with `hash(key) % N`:** If you change N (add or remove a shard),
almost ALL keys get reassigned. Going from 3 to 4 shards remaps ~75% of your data.

### 11.4.3 Directory-Based Sharding

Maintain a **lookup table** (directory) that maps each key (or key range) to a shard.

```
+------------------+          +-----------+
|  Application     |          | Directory |
|                  | -------> | Service   |
| "Where is        |          +-----------+
|  user_id=42?"   |               |
+------------------+               v
                          +----------------+
                          | Lookup Table   |
                          |----------------|
                          | key   | shard  |
                          |-------|--------|
                          | 1-100 | shard1 |
                          | 101+  | shard2 |
                          | VIPs  | shard3 |
                          +----------------+
```

| Pros                                     | Cons                                              |
|------------------------------------------|---------------------------------------------------|
| Maximum flexibility in shard assignment  | Directory is a single point of failure            |
| Easy to rebalance without rehashing      | Extra network hop for every query                 |
| Can handle special cases (VIP routing)   | Directory must be highly available and fast        |

### 11.4.4 Geographic Sharding

Route data to shards based on **geographic location** of the user or data origin.

```
                     Global Load Balancer
                            |
              +-------------+-------------+
              |             |             |
        +-----------+ +-----------+ +-----------+
        | US-East   | | EU-West   | | APAC      |
        | Shard     | | Shard     | | Shard     |
        |           | |           | |           |
        | Users in  | | Users in  | | Users in  |
        | Americas  | | Europe    | | Asia-Pac  |
        +-----------+ +-----------+ +-----------+
```

**Use cases:** GDPR compliance (EU data stays in EU), latency optimization, data sovereignty laws.

---

## 11.5 Consistent Hashing

Consistent hashing solves the **rehashing problem** of `hash(key) % N`. When a node is
added or removed, only **K/N keys** need to be remapped (where K = total keys, N = number of nodes),
instead of almost all of them.

### How It Works

1. Arrange the hash space as a **ring** (0 to 2^32 - 1, wrapping around).
2. Hash each **node** onto the ring.
3. Hash each **key** onto the ring.
4. Each key is assigned to the **first node encountered clockwise** from the key's position.

```
                         Node A (hash=30)
                            *
                       /         \
                     /             \
                   /                 \
        Key k3   *                    \
       (hash=350)|                     |
                 |     HASH RING       |
                 |    (0 to 360)       |
                 |                     |
        Node C   *                    * Node B
       (hash=270) \                  /  (hash=120)
                    \              /
                      \          /
                        \      /
                          *
                     Key k1 (hash=200)
                     -> assigned to Node C
                        (first node clockwise)

  Key k3 (hash=350) -> assigned to Node A (wrap around past 0)
  Key k1 (hash=200) -> assigned to Node C (next node at 270)
```

### Adding a Node

When **Node D** is added at position 180:

```
  BEFORE:                              AFTER:
  Key k1 (200) -> Node C (270)        Key k1 (200) -> Node C (270)  [unchanged]
  Key k2 (150) -> Node C (270)        Key k2 (150) -> Node D (180)  [remapped!]

  Only keys between Node B (120) and Node D (180) are remapped.
  All other keys stay on their original nodes.
```

### Virtual Nodes (Vnodes)

**Problem:** With few physical nodes, the ring can be unbalanced — one node might own 60% of the
key space while another owns 10%.

**Solution:** Each physical node gets **multiple positions** (virtual nodes) on the ring.

```
  Physical Node A gets virtual nodes: A1(30), A2(150), A3(300)
  Physical Node B gets virtual nodes: B1(80), B2(200), B3(330)

  Ring:   0---A1(30)---B1(80)---A2(150)---B2(200)---A3(300)---B3(330)---0
                |          |         |          |         |          |
          Keys 331-30  Keys 31-80  81-150   151-200   201-300   301-330

  Node A owns: {331-30} + {81-150} + {201-300}  ~= 50%
  Node B owns: {31-80}  + {151-200} + {301-330}  ~= 50%
```

With 100-200 virtual nodes per physical node, the distribution becomes very even.

**Used by:** Amazon DynamoDB, Apache Cassandra, Riak, Memcached (ketama).

---

## 11.6 Problems with Sharding

Sharding introduces significant complexity. Here are the key challenges:

### 11.6.1 Cross-Shard Queries

```
-- This simple query becomes a nightmare with sharding:
SELECT * FROM orders WHERE user_id = 42 AND product_category = 'electronics'

-- If sharded by user_id, electronics products are spread across ALL shards
-- The query becomes a "scatter-gather" operation:

  App Server
      |
      +---> Shard 1: "Any electronics orders for user 42?" -> [results]
      +---> Shard 2: "Any electronics orders for user 42?" -> [results]
      +---> Shard 3: "Any electronics orders for user 42?" -> [results]
      |
      +---> Merge all results
```

### 11.6.2 Cross-Shard JOINs

JOINs across shards require fetching data from multiple nodes and joining in the application layer.
This is expensive and slow.

**Strategies to mitigate:**
- **Denormalize data** — store redundant copies to avoid joins
- **Application-level joins** — fetch from each shard and join in code
- **Co-locate related data** — ensure related entities are on the same shard

### 11.6.3 Cross-Shard Transactions

ACID transactions across shards require **two-phase commit (2PC)** or **saga patterns**, both of
which add latency and complexity.

```
Two-Phase Commit:
  Coordinator: "Prepare to commit?"
      |
      +---> Shard 1: "Prepared" (locks held)
      +---> Shard 2: "Prepared" (locks held)
      |
  Coordinator: "Commit!"
      |
      +---> Shard 1: "Committed"
      +---> Shard 2: "Committed"

  Problem: If coordinator crashes after "Prepare" but before "Commit",
           all shards are stuck holding locks indefinitely.
```

### 11.6.4 Hotspots

Even with hash-based sharding, some keys may receive disproportionate traffic.

**Example:** A celebrity with 100M followers posts on a social media platform. The shard holding
that user's data gets hammered while other shards idle.

**Mitigations:**
- Add a random suffix to hot keys (split across multiple shards)
- Cache hot data aggressively
- Rate-limit reads on hot partitions
- Use dedicated shards for hot entities

---

## 11.7 Rebalancing Shards

As data grows or nodes are added/removed, shards must be **rebalanced**.

### Strategies

| Strategy              | How It Works                                      | Impact                          |
|-----------------------|---------------------------------------------------|---------------------------------|
| Fixed partitions      | Create many more partitions than nodes; move whole | Low overhead, Cassandra uses    |
|                       | partitions to new nodes                           | this approach                   |
| Dynamic splitting     | Split a partition when it exceeds a size threshold | Adapts to data growth, HBase   |
| Proportional to nodes | Each node gets a fixed number of partitions        | Simple, even distribution       |

### Fixed Partitions Example

```
BEFORE (3 nodes, 12 partitions):
  Node 1: [P1, P2, P3, P4]
  Node 2: [P5, P6, P7, P8]
  Node 3: [P9, P10, P11, P12]

AFTER adding Node 4 (rebalanced):
  Node 1: [P1, P2, P3]
  Node 2: [P5, P6, P7]
  Node 3: [P9, P10, P11]
  Node 4: [P4, P8, P12]    <-- stole one partition from each node
```

**Key principle:** Move the minimum amount of data needed. Never reshuffle everything.

---

## 11.8 Shard Key Selection Best Practices

The **shard key** (partition key) is the single most important design decision in a sharded system.

### Good Shard Key Properties

| Property              | Why It Matters                                        | Example                       |
|-----------------------|-------------------------------------------------------|-------------------------------|
| High cardinality      | More unique values = more even distribution           | user_id (millions of values)  |
| Even distribution     | No value dominates the key space                      | UUID > country_code           |
| Query alignment       | Most queries include the shard key                    | Shard by user_id if most      |
|                       |                                                       | queries are per-user          |
| Low cross-shard need  | Related data should live on the same shard             | Shard orders by user_id, not  |
|                       |                                                       | by order_id                   |

### Bad Shard Key Examples

| Bad Shard Key     | Why It's Bad                                                     |
|-------------------|------------------------------------------------------------------|
| `country_code`    | Only ~200 values; US shard would be 10x larger than others       |
| `created_date`    | All today's writes go to one shard (hot partition)               |
| `boolean_field`   | Only 2 values — you can only have 2 shards                      |
| `auto_increment`  | Latest shard always gets all the writes                          |

### Compound Shard Keys

Combine fields to improve distribution:
```
shard_key = (user_id, month)

This allows:
  - Even distribution across shards (high cardinality from user_id)
  - Time-based queries within a user's data (month component)
  - Avoids the "all recent data on one shard" problem
```

---

## 11.9 Real-World Examples

### How Instagram Shards PostgreSQL

Instagram (pre-acquisition) needed to shard their PostgreSQL database for photos, likes, and
comments. Their approach:

1. **Logical shards:** Created thousands of logical shards (PostgreSQL schemas)
2. **Physical mapping:** Mapped logical shards to physical servers using a lookup
3. **Shard key:** Used `user_id` as the primary shard key
4. **ID generation:** Embedded shard information in IDs using a custom scheme:
   ```
   ID structure (64 bits):
   | 41 bits: timestamp | 13 bits: logical shard ID | 10 bits: auto-increment |

   This means:
   - You can extract the shard ID from any ID without a lookup
   - IDs are roughly time-sorted (useful for feeds)
   - Each shard can generate 1024 IDs per millisecond
   ```
5. **Co-location:** All of a user's photos, likes, and comments live on the same shard
6. **Result:** Scaled to billions of photos across dozens of PostgreSQL servers

### How Discord Shards Cassandra

Discord stores **trillions of messages** across Cassandra clusters:

1. **Partition key:** `(channel_id, bucket)` where bucket is a time window
   ```
   Partition key = (channel_id, bucket)
   Clustering key = (message_id)    -- sorted within partition

   Bucket = message_created_at truncated to 10-day windows
   ```
2. **Why not just `channel_id`?** Large channels (millions of messages) would create
   massive partitions. The time bucket keeps partitions manageable (~100MB each).
3. **Why not just `message_id`?** Loading a channel's messages would require scatter-gather
   across ALL nodes. Co-locating by channel keeps reads fast.
4. **Compaction strategy:** Used `TimeWindowCompactionStrategy` to efficiently expire old data.
5. **Migration:** Eventually migrated from Cassandra to ScyllaDB for better tail latencies
   (P99), keeping the same sharding model.

---

## 11.10 Decision Framework: When and How to Shard

```
  Is your database struggling?
        |
        +-- No --> Don't shard. Use read replicas, caching, query optimization.
        |
        +-- Yes
              |
              +-- Is it a read problem?
              |       |
              |       +-- Yes --> Add read replicas first. Shard only if replicas aren't enough.
              |       +-- No (write problem)
              |               |
              |               +-- Is it a single table?
              |                       |
              |                       +-- Yes --> Horizontal partitioning (sharding)
              |                       +-- No  --> Consider vertical partitioning first
              |
              +-- Choose sharding strategy:
                    |
                    +-- Need range queries?       --> Range-based or Directory-based
                    +-- Need even distribution?   --> Hash-based or Consistent hashing
                    +-- Need geographic locality? --> Geographic sharding
                    +-- Need maximum flexibility? --> Directory-based
```

---

## 11.11 Key Takeaways

1. **Partition only when necessary.** Sharding adds enormous complexity — exhaust vertical
   scaling, read replicas, and caching first.

2. **Vertical partitioning** (splitting by columns/tables) is simpler and aligns naturally
   with microservice boundaries. Start here before horizontal sharding.

3. **Horizontal partitioning** (sharding by rows) is the heavy-duty solution for datasets
   that outgrow a single node's capacity.

4. **Hash-based sharding** provides good distribution but loses range query ability.
   **Range-based sharding** preserves range queries but risks hotspots.

5. **Consistent hashing** is the gold standard for distributed systems — it minimizes data
   movement when nodes are added or removed. Virtual nodes improve balance.

6. **The shard key is everything.** Choose a key with high cardinality, even distribution,
   and alignment with your most common query patterns.

7. **Sharding makes many things hard:** cross-shard joins, transactions, global queries,
   and schema migrations all become significantly more complex.

8. **Co-locate related data.** If users always query their own data, shard by `user_id`
   so all of a user's data lives on one shard.

9. **Plan for rebalancing.** Use fixed partitions (more partitions than nodes) so you can
   rebalance by moving whole partitions without reshuffling.

10. **Study real systems.** Instagram's PostgreSQL sharding and Discord's Cassandra
    partitioning are excellent case studies for practical shard key design.

---

*Next chapter: [Chapter 12 — Data Replication](./12-data-replication.md)*
