# Chapter 8: Databases

## Introduction — The Role of Databases in System Design

If caching is about speed, **databases** are about **durability and truth**. The database is the system of record — the authoritative source of your application's state. Choosing the right database is one of the most consequential decisions in system design, because changing it later is extremely costly.

Every system design question ultimately comes down to: *How do we store, retrieve, and manage data efficiently and reliably?*

This chapter covers the full landscape of database technologies, when to use each, and the fundamental concepts you need for system design.

---

## Relational Databases (SQL)

Relational databases store data in **tables** (relations) with **rows** and **columns**. They enforce a strict **schema** and support powerful querying via **SQL (Structured Query Language)**.

### Popular Relational Databases

| Database | Strengths | Used By |
|---|---|---|
| **PostgreSQL** | Advanced features, JSON support, extensibility, MVCC | Instagram, Reddit, Uber |
| **MySQL** | Mature, fast reads, large ecosystem | Facebook, Twitter, GitHub |
| **Oracle** | Enterprise-grade, PL/SQL, RAC clustering | Banks, telecom, government |
| **SQL Server** | Deep Windows integration, BI tools | Microsoft ecosystem |
| **SQLite** | Embedded, zero-config, single-file | Mobile apps, IoT, testing |

### ACID Properties — The Gold Standard

Relational databases guarantee **ACID** properties for transactions:

| Property | Definition | Example |
|---|---|---|
| **Atomicity** | All operations in a transaction succeed or all fail — no partial state | Transfer $100: debit AND credit both happen, or neither does |
| **Consistency** | Database moves from one valid state to another; constraints are enforced | Foreign key, unique, check constraints are never violated |
| **Isolation** | Concurrent transactions don't interfere with each other | Two users buying the last item don't both succeed |
| **Durability** | Once committed, data survives crashes, power failures | Committed transactions are written to disk (WAL) |

### Isolation Levels

Different isolation levels trade off between consistency and performance:

```
  Strictness    Isolation Level        Phenomena Prevented         Performance
  ▲             +--------------------+                             ▼
  |             | Serializable       | Dirty, Non-repeatable,     | Slowest
  |             |                    | Phantom reads prevented     |
  |             +--------------------+                             |
  |             | Repeatable Read    | Dirty, Non-repeatable       |
  |             | (MySQL default)    | reads prevented             |
  |             +--------------------+                             |
  |             | Read Committed     | Dirty reads prevented       |
  |             | (PostgreSQL def.)  |                             |
  |             +--------------------+                             |
  |             | Read Uncommitted   | Nothing prevented           | Fastest
  ▼             +--------------------+                             ▲
```

---

## NoSQL Databases

**NoSQL** (Not Only SQL) databases emerged to handle use cases where relational databases struggle: massive scale, flexible schemas, high write throughput, and distributed architectures.

### The Four Types of NoSQL Databases

```
  +--------------------+    +--------------------+
  |  Key-Value Stores  |    |  Document Stores   |
  |                    |    |                    |
  | key -> value       |    | key -> JSON doc    |
  | (opaque blob)      |    | (queryable fields) |
  |                    |    |                    |
  | Redis, DynamoDB,   |    | MongoDB, CouchDB,  |
  | Riak, etcd         |    | Firestore          |
  +--------------------+    +--------------------+

  +--------------------+    +--------------------+
  | Column-Family      |    |  Graph Databases   |
  |                    |    |                    |
  | row -> {col_fam:   |    | nodes + edges +    |
  |   {col: val, ...}} |    | properties         |
  |                    |    |                    |
  | Cassandra, HBase,  |    | Neo4j, Neptune,    |
  | ScyllaDB           |    | JanusGraph         |
  +--------------------+    +--------------------+
```

### 1. Key-Value Stores

The simplest NoSQL model. Data is stored as **key-value pairs** where the value is opaque to the database.

```
  PUT("user:1001", "{name: 'Alice', email: 'alice@example.com'}")
  GET("user:1001") -> "{name: 'Alice', email: 'alice@example.com'}"
  DELETE("user:1001")
```

| Database | Notable Features | Use Cases |
|---|---|---|
| **Redis** | In-memory, rich data structures, pub/sub | Caching, sessions, rate limiting, leaderboards |
| **DynamoDB** | Fully managed, auto-scaling, single-digit ms latency | Serverless backends, gaming, IoT |
| **etcd** | Distributed, consistent (Raft consensus) | Kubernetes config, service discovery |
| **Riak** | Highly available, eventually consistent | Shopping carts, user preferences |

**When to use:** Simple lookups by key, caching, session storage, shopping carts, config storage.

### 2. Document Stores

Extend key-value stores by making the value a **structured document** (usually JSON/BSON) that can be **queried, indexed, and partially updated**.

```json
// MongoDB document
{
  "_id": "order_1001",
  "customer": {
    "name": "Alice",
    "email": "alice@example.com"
  },
  "items": [
    {"product": "Laptop", "price": 999.99, "qty": 1},
    {"product": "Mouse", "price": 29.99, "qty": 2}
  ],
  "status": "shipped",
  "created_at": "2024-01-15T10:30:00Z"
}
```

| Database | Notable Features | Use Cases |
|---|---|---|
| **MongoDB** | Flexible schema, rich queries, aggregation pipeline | Content management, product catalogs, user profiles |
| **CouchDB** | Multi-master replication, HTTP API | Offline-first apps, mobile sync |
| **Firestore** | Real-time sync, serverless | Mobile apps, real-time collaboration |

**When to use:** Semi-structured data, rapid iteration (schema flexibility), content management, event logging, embedded documents (avoid JOINs).

### 3. Column-Family Stores

Organize data into **rows** and **column families**. Optimized for **high write throughput** and **wide rows** with many columns.

```
  Row Key: "user:1001"
  +-------------------+----------------------------+-----------------------+
  | Column Family:    | Column Family:             | Column Family:        |
  | "profile"         | "activity"                 | "preferences"         |
  |                   |                            |                       |
  | name: "Alice"     | login:2024-01-15: "NYC"    | theme: "dark"         |
  | email: "a@e.com"  | login:2024-01-14: "SFO"    | lang: "en"            |
  | age: 30           | purchase:2024-01-13: "$99"  | notifications: "on"   |
  +-------------------+----------------------------+-----------------------+
```

| Database | Notable Features | Use Cases |
|---|---|---|
| **Apache Cassandra** | Masterless, linearly scalable, tunable consistency | Time-series, messaging, IoT, recommendations |
| **Apache HBase** | Hadoop integration, strong consistency | Analytics, Hadoop ecosystem |
| **ScyllaDB** | C++ rewrite of Cassandra, 10x faster | Low-latency, high-throughput workloads |

**When to use:** Time-series data, high write throughput, known query patterns, wide rows, multi-region deployments.

### 4. Graph Databases

Store data as **nodes** (entities), **edges** (relationships), and **properties**. Optimized for traversing relationships.

```
       +--------+    FRIENDS_WITH    +--------+
       | Alice  +-------------------->| Bob    |
       | (User) |                    | (User) |
       +---+----+                    +---+----+
           |                             |
           | LIKES                       | PURCHASED
           |                             |
       +---v--------+              +----v-------+
       | "Inception"|              | "Laptop"   |
       | (Movie)    |              | (Product)  |
       +------------+              +------------+
  
  Query: "Find all friends of Alice who purchased products 
          that Alice also liked" -- trivial in a graph DB, 
          nightmare in SQL with multiple JOINs
```

| Database | Notable Features | Use Cases |
|---|---|---|
| **Neo4j** | Cypher query language, ACID, native graph storage | Social networks, recommendations, knowledge graphs |
| **Amazon Neptune** | Managed, supports Gremlin and SPARQL | AWS-native graph workloads |
| **JanusGraph** | Distributed, supports Cassandra/HBase backend | Large-scale graph analytics |

**When to use:** Social networks, recommendation engines, fraud detection, knowledge graphs, network topology.

---

## SQL vs NoSQL — Comprehensive Comparison

| Dimension | SQL (Relational) | NoSQL |
|---|---|---|
| **Schema** | Fixed schema, predefined tables | Flexible / schema-less |
| **Query Language** | SQL (standardized) | Database-specific APIs/query languages |
| **Scalability** | Primarily vertical (scale up) | Primarily horizontal (scale out) |
| **Data Model** | Tables with rows and columns | Key-value, document, column, graph |
| **Transactions** | Full ACID transactions | Varies: some support ACID, many use BASE |
| **JOINs** | Powerful, multi-table JOINs | Generally not supported or expensive |
| **Consistency** | Strong consistency (default) | Tunable: strong to eventual |
| **Maturity** | 40+ years, well-understood | Younger, rapidly evolving |
| **Best for** | Complex queries, transactions, structured data | High scale, flexible schema, specific access patterns |
| **Examples** | PostgreSQL, MySQL, Oracle | MongoDB, Cassandra, Redis, Neo4j |

### When to Choose SQL

- You need complex transactions (banking, e-commerce orders)
- You need complex JOINs across multiple entities
- Your data is highly structured and relational
- You need strong consistency guarantees
- Your query patterns are diverse and evolving

### When to Choose NoSQL

- You need horizontal scalability for massive datasets
- Your schema changes frequently (rapid development)
- You have a known, specific access pattern (key lookup, time-series)
- You need high write throughput
- You're building for eventual consistency (social feeds, logging)

---

## ACID vs BASE

These are the two consistency models in distributed databases:

| Property | ACID | BASE |
|---|---|---|
| **Full name** | Atomicity, Consistency, Isolation, Durability | Basically Available, Soft state, Eventually consistent |
| **Consistency** | Strong — reads always return latest write | Eventual — reads may return stale data temporarily |
| **Availability** | May sacrifice availability for consistency | Prioritizes availability over consistency |
| **Use case** | Financial transactions, inventory management | Social feeds, analytics, product catalogs |
| **Trade-off** | Higher latency, lower throughput | Lower latency, higher throughput |
| **CAP theorem** | Chooses CP (Consistency + Partition tolerance) | Chooses AP (Availability + Partition tolerance) |

```
  +------- CAP Theorem --------+
  |                             |
  |  Consistency (C)            |
  |      /          \           |
  |     /  You can   \          |
  |    /   only pick  \         |
  |   /     TWO of     \        |
  |  /      THREE        \      |
  | /                      \    |
  | Availability (A) ---- Partition |
  |                    Tolerance (P)|
  +-------------------------------+
  
  CP: PostgreSQL, MongoDB (default), HBase
  AP: Cassandra, DynamoDB, CouchDB
  CA: (Not possible in distributed systems — network partitions always happen)
```

---

## Database Indexing — Deep Dive

Indexes are **auxiliary data structures** maintained by the database engine that speed up data
retrieval at the cost of additional storage and slower writes. Understanding how indexes work
internally is critical for designing systems that perform at scale.

> **Analogy:** An index in a database works exactly like an index in a textbook. Instead of
> reading every page to find "B-Tree," you go to the index at the back, look up "B-Tree,"
> and jump directly to page 294. The database index does the same thing for your rows.

---

### The Problem: Full Table Scan

Without an index, every query requires scanning the **entire table** — a Full Table Scan.

```
  Query: SELECT * FROM users WHERE email = 'alice@example.com'
  
  Without index: FULL TABLE SCAN
  +----+----------+---------------------+
  | id | name     | email               |  The database reads every
  +----+----------+---------------------+  single row from disk
  |  1 | Bob      | bob@example.com     |  until it finds the match.
  |  2 | Charlie  | charlie@example.com |
  |  3 | Alice    | alice@example.com   |  <- Found! But had to scan 3 rows
  | .. | ...      | ...                 |
  | 1M | Zara     | zara@example.com    |  Worst case: scan all 1M rows
  +----+----------+---------------------+
  
  Cost: O(n)
  
  For 10 million rows, this means:
  - Reading ~10 million rows from disk pages
  - Each disk page is 8KB (PostgreSQL) or 16KB (MySQL InnoDB)
  - If each row is 200 bytes, ~40 rows per page
  - 10M rows / 40 = 250,000 disk page reads
  - Even with SSD (~0.1ms per random read) = 25 seconds!
```

This is **completely unacceptable** in production. Indexes solve this problem.

---

### How a B+ Tree Index Works (The Most Common Index)

Almost every relational database (PostgreSQL, MySQL, Oracle, SQL Server) uses **B+ Trees** as
the default index structure. Understanding B+ Trees is essential.

> **Note:** Most people say "B-Tree index" but databases actually use **B+ Trees**. The
> distinction matters and is explained below.

#### B-Tree vs B+ Tree

```
  B-Tree:                                B+ Tree (what databases use):
  - Data stored in ALL nodes             - Data stored ONLY in leaf nodes
  - Leaf nodes not linked                - Leaf nodes form a LINKED LIST
  - Less predictable performance         - Consistent O(log n) for all queries
                                         - Excellent for RANGE queries
  
  B-Tree:                     B+ Tree:
       [50|data]                    [50]
      /         \                  /     \
  [20|data] [80|data]         [20]       [80]
                              /   \      /   \
                          [10,20]->[50,80]->[90,100]
                           data    data      data
                           ^^^^^^^^^^^^^^^^^^^^
                           Linked list of leaves!
```

#### B+ Tree Internal Structure

```
  CREATE INDEX idx_email ON users(email);
  
  B+ Tree Index (order = 3, simplified):
  
  LEVEL 0 (Root):
  +------------------+
  |   [jackson]      |  -- Only stores keys and pointers
  |  /           \   |  -- NO actual row data here
  +-/-------------\--+
   /               \
  
  LEVEL 1 (Internal Nodes):
  +-----------+          +---------------+
  | [clark,   |          | [patel,       |
  |  garcia]  |          |  williams]    |
  +--/--|--\--+          +--/--|------\--+
    /   |   \              /   |       \
  
  LEVEL 2 (Leaf Nodes — contain actual data pointers):
  +--------+   +--------+   +--------+   +--------+   +--------+
  | adams  |-->| clark  |-->| garcia |-->| patel  |-->|williams|
  | brown  |   | davis  |   | jackson|   | smith  |   | zhang  |
  | [ptr]  |   | [ptr]  |   | [ptr]  |   | [ptr]  |   | [ptr]  |
  +--------+   +--------+   +--------+   +--------+   +--------+
     |  |         |  |         |  |         |  |         |  |
     v  v         v  v         v  v         v  v         v  v
   Row Row      Row Row      Row Row      Row Row      Row Row
   #5  #12      #3  #8       #7  #1       #9  #2       #4  #6
  
  Key properties:
  1. ALL data pointers are at leaf level (uniform access time)
  2. Leaf nodes are linked (makes range scans efficient)
  3. Tree is ALWAYS balanced (same depth for all leaves)
  4. Each node = one disk page (8KB/16KB)
```

#### Why B+ Tree is Brilliant for Databases

```
  Property 1: LOGARITHMIC SEARCH
  +----------------------------------------------------------+
  | Rows in Table | B+ Tree Depth | Disk Reads to Find 1 Row |
  |---------------|---------------|---------------------------|
  |         1,000 |             2 |                         2 |
  |       100,000 |             3 |                         3 |
  |    10,000,000 |             4 |                         4 |
  | 1,000,000,000 |             5 |                         5 |
  +----------------------------------------------------------+
  
  To search 1 BILLION rows, you need only 5 disk reads!
  
  Why? Each node holds ~500 keys (for 8KB page, ~16 byte key).
  Branching factor = 500.
  500^4 = 62.5 billion keys addressable with depth 4.
  
  Property 2: RANGE SCANS USE LINKED LEAVES
  
  Query: SELECT * FROM users WHERE email BETWEEN 'clark' AND 'patel'
  
  Step 1: B+ Tree lookup for 'clark' -> 3 disk reads to find leaf
  Step 2: Follow linked list: clark -> garcia -> jackson -> patel
  Step 3: No need to go back up the tree!
  
  This is why B+ Trees (not hash indexes) are the default — they
  handle BOTH equality AND range queries efficiently.
```

---

### Clustered Index vs Non-Clustered Index

This is one of the **most frequently asked** database interview questions and one of the most
misunderstood concepts.

#### Clustered Index (Primary Index)

A clustered index **determines the physical order of data on disk.** The table data itself is
stored inside the leaf nodes of the B+ Tree. There can be **only ONE** clustered index per
table because data can only be physically sorted one way.

```
  Clustered Index on user_id:
  
  B+ Tree where LEAF NODES ARE THE TABLE DATA:
  
       Internal:      [500]
                     /      \
                  [200]     [800]
                 /    \     /    \
  
       Leaves (= actual table rows, sorted by user_id):
       +------------------+    +------------------+    +------------------+
       | user_id: 100     |--->| user_id: 200     |--->| user_id: 300     |
       | name: "Alice"    |    | name: "Bob"      |    | name: "Charlie"  |
       | email: "a@e.com" |    | email: "b@e.com" |    | email: "c@e.com" |
       +------------------+    +------------------+    +------------------+
       
  The data IS the index. No separate lookup needed.
  
  MySQL/InnoDB: PRIMARY KEY = clustered index (always)
  PostgreSQL:   Uses a HEAP table + separate index (no clustered index by default,
                but you can use CLUSTER command to reorder once)
  SQL Server:   PRIMARY KEY = clustered index (by default, configurable)
```

#### Non-Clustered Index (Secondary Index)

A non-clustered index is a **separate structure** that contains the indexed column(s) and a
**pointer** back to the actual row. There can be **many** non-clustered indexes per table.

```
  Table (clustered by user_id):
  +----+----------+---------------------+--------+
  | id | name     | email               | city   |
  +----+----------+---------------------+--------+
  |  1 | Alice    | alice@example.com   | NYC    |  <- Page 1
  |  2 | Bob      | bob@example.com     | SFO    |  <- Page 1
  |  3 | Charlie  | charlie@example.com | NYC    |  <- Page 2
  |  4 | Diana    | diana@example.com   | LON    |  <- Page 2
  +----+----------+---------------------+--------+
  
  Non-Clustered Index on email:
  B+ Tree:
           [charlie@...]
          /              \
  [alice@..., bob@...]  [charlie@..., diana@...]
       |         |            |            |
       v         v            v            v
    id=1       id=2         id=3         id=4
  (Page 1)   (Page 1)    (Page 2)     (Page 2)
  
  Query: WHERE email = 'diana@example.com'
  Step 1: Traverse B+ Tree -> find pointer to id=4, Page 2
  Step 2: Go to Page 2, read row with id=4 (this is the "bookmark lookup")
  
  This second step is called a "BOOKMARK LOOKUP" or "KEY LOOKUP" 
  -- it's an extra I/O cost that clustered indexes don't have.
```

#### The Critical Difference

```
  +---------------------+-------------------------------------+------------------------------------+
  | Aspect              | Clustered Index                     | Non-Clustered Index                |
  +---------------------+-------------------------------------+------------------------------------+
  | Physical ordering   | YES - data is physically sorted     | NO - separate structure            |
  | Per table           | Only ONE                            | Many (up to ~250 in most DBs)      |
  | Leaf nodes contain  | Actual row data                     | Index key + pointer to row         |
  | Lookup cost         | Direct (data is right there)        | Extra "bookmark lookup" to row     |
  | Best for            | Range scans on primary key          | Frequent lookups on non-PK columns |
  | Insert cost         | Higher (must maintain physical order)| Lower (only update the index)     |
  | Storage             | IS the table (no extra space)       | Additional disk space required     |
  +---------------------+-------------------------------------+------------------------------------+
```

---

### Covering Index (Index-Only Scan)

A **covering index** contains ALL the columns needed by a query, so the database never needs
to access the actual table. This is a **massive performance optimization.**

```
  Query: SELECT name, email FROM users WHERE city = 'NYC'
  
  Regular Index on city:
    1. Scan index -> find city = 'NYC' -> get row pointers
    2. For EACH matching row, go to the table to fetch name and email
    3. 1000 matching rows = 1000 extra random I/O operations!
  
  Covering Index:
    CREATE INDEX idx_city_covering ON users(city) INCLUDE (name, email);
    -- Or in MySQL: CREATE INDEX idx_city ON users(city, name, email);
  
    1. Scan index -> find city = 'NYC' -> name and email are RIGHT HERE in the index
    2. No need to go to the table at all!
    3. 1000 matching rows = 0 extra I/O operations!
  
  Covering Index leaf node:
  +-------+----------+---------------------+
  | city  | name     | email               |  <-- All needed data is in the index
  +-------+----------+---------------------+
  | NYC   | Alice    | alice@example.com   |
  | NYC   | Charlie  | charlie@example.com |
  | SFO   | Bob      | bob@example.com     |
  +-------+----------+---------------------+
  
  PostgreSQL EXPLAIN output for covering index:
  "Index Only Scan using idx_city_covering on users"
      ^^^^^^^^^^ -- "Index Only" means covering index was used!
  
  vs. non-covering:
  "Index Scan using idx_city on users"
      ^^^^^^^^^^ -- "Index Scan" means bookmark lookup was needed
```

> **Pro Tip:** If your EXPLAIN plan shows "Index Scan" followed by heavy table lookups,
> consider making it a covering index. This is the single most impactful optimization for
> read-heavy queries on large tables.

---

### Composite (Multi-Column) Index and the Left-Prefix Rule

A composite index indexes **multiple columns together.** The order of columns in the index
matters enormously.

```
  CREATE INDEX idx_country_city_zip ON addresses(country, city, zip_code);
  
  B+ Tree sorts by: country FIRST, then city WITHIN country, then zip WITHIN city.
  
  Index sorted order:
  +----------+----------+----------+
  | country  | city     | zip_code |
  +----------+----------+----------+
  | India    | Delhi    | 110001   |
  | India    | Delhi    | 110002   |
  | India    | Mumbai   | 400001   |
  | UK       | London   | EC1A     |
  | UK       | London   | SW1A     |
  | US       | Chicago  | 60601    |
  | US       | NYC      | 10001    |
  | US       | NYC      | 10002    |
  +----------+----------+----------+
```

#### The Left-Prefix Rule (Extremely Important!)

The composite index can only be used if the query filters on a **left prefix** of the index columns.

```
  Index: (country, city, zip_code)
  
  +----------------------------------------------------------------+--------+
  | Query WHERE clause                                             | Uses   |
  |                                                                | Index? |
  +----------------------------------------------------------------+--------+
  | WHERE country = 'US'                                           |  YES   |
  | WHERE country = 'US' AND city = 'NYC'                          |  YES   |
  | WHERE country = 'US' AND city = 'NYC' AND zip_code = '10001'   |  YES   |
  | WHERE city = 'NYC'                                             |  NO!   |
  | WHERE zip_code = '10001'                                       |  NO!   |
  | WHERE city = 'NYC' AND zip_code = '10001'                      |  NO!   |
  | WHERE country = 'US' AND zip_code = '10001'                    | PARTIAL|
  +----------------------------------------------------------------+--------+
  
  Why does (country, zip_code) only get PARTIAL use?
  - The B+ Tree can use the index to filter country = 'US'
  - But then it must scan ALL cities within 'US' to find zip_code = '10001'
  - It cannot "skip" the city level in the sorted order
  
  Think of it like a phone book sorted by (Last Name, First Name):
  - Looking up "Smith" -> fast (left prefix)
  - Looking up "Smith, John" -> fast (full prefix)
  - Looking up everyone named "John" regardless of last name -> FULL SCAN!
```

#### Column Order Strategy for Composite Indexes

```
  Rule of thumb for column ordering:
  
  1. Equality columns FIRST    (WHERE country = 'US')
  2. Range columns LAST        (WHERE age BETWEEN 25 AND 35)
  3. High-selectivity first    (columns that filter out more rows come first)
  
  Example:
    Query: WHERE status = 'active' AND created_at > '2024-01-01' AND country = 'US'
    
    BAD index:  (status, created_at, country)
    - status = 'active' -> uses index (equality)
    - created_at > '2024-01-01' -> uses index (range) BUT...
    - country = 'US' -> CANNOT use index! (after a range, index scan stops)
    
    GOOD index: (status, country, created_at)
    - status = 'active' -> uses index (equality)
    - country = 'US' -> uses index (equality)
    - created_at > '2024-01-01' -> uses index (range, and it's last — perfect!)
```

---

### Partial Index (Filtered Index)

A partial index only indexes **rows that match a condition.** This saves storage and improves
performance when you only query a subset of data.

```
  -- Instead of indexing ALL 10 million orders:
  CREATE INDEX idx_orders_status ON orders(created_at);
  -- Index size: ~200 MB, indexes all 10M rows
  
  -- Index only the unshipped orders (which is what you actually query):
  CREATE INDEX idx_unshipped_orders ON orders(created_at)
  WHERE status = 'pending' OR status = 'processing';
  -- Index size: ~5 MB (only ~250K unshipped orders)
  
  Query: SELECT * FROM orders WHERE status = 'pending' ORDER BY created_at;
  -> Uses the tiny 5 MB partial index instead of the 200 MB full index!
  
  Real-world examples:
  - Index only "active" users (90% of users are inactive/deleted)
  - Index only "unread" messages (most messages are read)
  - Index only "pending" payments (most payments are settled)
  - Index only rows WHERE deleted_at IS NULL (soft delete pattern)
```

---

### Hash Index

Hash indexes use a **hash table** for O(1) equality lookups. They are faster than B+ Trees
for exact-match queries but **cannot** handle range queries, sorting, or partial matching.

```
  Hash Index on user_id:
  
  hash(101) = bucket 7 -> [user_id=101, ptr to row]
  hash(202) = bucket 3 -> [user_id=202, ptr to row]
  hash(303) = bucket 7 -> [user_id=303, ptr to row]  (hash collision, chained)
  
  Lookup: WHERE user_id = 202
  1. Compute hash(202) = bucket 3
  2. Go directly to bucket 3
  3. Return row pointer
  Cost: O(1) — even faster than B+ Tree's O(log n)
  
  But:
  WHERE user_id > 200    -> CANNOT USE hash index (no ordering)
  ORDER BY user_id       -> CANNOT USE hash index (no ordering)
  WHERE user_id LIKE '2%' -> CANNOT USE hash index (no prefix matching)
```

| Feature | B+ Tree Index | Hash Index |
|---|---|---|
| Equality (`=`) | O(log n) | O(1) |
| Range (`>`, `<`, `BETWEEN`) | Excellent | Not supported |
| Sorting (`ORDER BY`) | Supported | Not supported |
| Prefix matching (`LIKE 'abc%'`) | Supported | Not supported |
| Storage | More (tree overhead) | Less |
| Use case | General purpose (default) | Key-value lookups, joins on equality |

> **In Practice:** PostgreSQL supports hash indexes but recommends B-Tree for most use cases.
> MySQL/InnoDB does not support hash indexes on disk (only MEMORY engine). Redis is essentially
> a giant hash index.

---

### Full-Text Index (Inverted Index)

For searching text content (articles, product descriptions, messages), regular B+ Tree indexes
are useless because they cannot match words within a string.

```
  Regular B-Tree on "description" column:
  WHERE description = 'red running shoes'   -> Works (exact match only)
  WHERE description LIKE '%running%'        -> FULL TABLE SCAN! (leading wildcard)
  
  Full-Text Index (Inverted Index):
  
  Document 1: "red running shoes for men"
  Document 2: "blue running shorts"
  Document 3: "red hiking boots for women"
  
  Inverted Index:
  +----------+------------------+
  | Term     | Document IDs     |
  +----------+------------------+
  | blue     | [2]              |
  | boots    | [3]              |
  | for      | [1, 3]          |
  | hiking   | [3]              |
  | men      | [1]              |
  | red      | [1, 3]          |
  | running  | [1, 2]          |
  | shoes    | [1]              |
  | shorts   | [2]              |
  | women    | [3]              |
  +----------+------------------+
  
  Query: "red running" -> red AND running -> intersect [1,3] ∩ [1,2] = [1]
  Result: Document 1 — "red running shoes for men"
  
  This is exactly how Elasticsearch, Solr, and PostgreSQL full-text search work!
```

---

### Index Types — Complete Reference

| Index Type | Data Structure | Best For | Not Suitable For | Example |
|---|---|---|---|---|
| **B+ Tree** | Balanced tree | Range queries, equality, sorting, prefix matching | Full-text search | `WHERE age BETWEEN 25 AND 35` |
| **Hash** | Hash table | Exact equality lookups only | Range queries, sorting | `WHERE id = 12345` |
| **Bitmap** | Bit arrays | Low-cardinality columns, data warehousing | High-cardinality, OLTP | `WHERE gender = 'M' AND status = 'active'` |
| **Full-text** | Inverted index | Text search, natural language queries | Exact numeric lookups | `WHERE body @@ 'distributed systems'` |
| **GiST** | Generalized search tree | Geometry, ranges, nearest-neighbor | Simple equality | PostGIS `ST_DWithin(point, location, 1000)` |
| **GIN** | Generalized inverted | JSON fields, arrays, full-text | Simple columns | `WHERE tags @> '{"urgent"}'` |
| **BRIN** | Block range index | Naturally ordered data (timestamps), huge tables | Random data | `WHERE created_at > '2024-01-01'` on time-series |
| **Covering** | B+ Tree + included cols | Queries where all columns are in the index | Write-heavy tables | `SELECT name, email WHERE city = 'NYC'` |
| **Partial** | Any (with filter) | Queries on a subset of rows | Queries on all rows | `WHERE status = 'pending'` |
| **Composite** | Multi-column B+ Tree | Multi-column filter queries | Queries not using left prefix | `WHERE country = 'US' AND city = 'NYC'` |

---

### Index Trade-offs

```
+--------------------------------------+--------------------------------------+
|           Advantages                 |           Disadvantages              |
+--------------------------------------+--------------------------------------+
| Faster reads (10x to 10,000x)       | Slower writes (each INSERT/UPDATE    |
|                                      |   must update ALL indexes on table)  |
| Enable efficient sorting             | Additional storage space (indexes    |
|   (avoid expensive filesort)         |   can be 10-50% of table size)      |
| Support range queries and            | Index maintenance overhead during    |
|   multi-column filtering             |   bulk operations (VACUUM, rebuild) |
| Enforce uniqueness constraints       | Too many indexes = dramatically     |
| Enable covering index scans          |   slow writes (each write updates   |
|   (skip table access entirely)       |   EVERY index)                      |
| Reduce lock contention               | Wrong index = query planner picks   |
|   (less data scanned = shorter locks)|   suboptimal plan (worse than none) |
+--------------------------------------+--------------------------------------+
```

#### How Many Indexes Is Too Many?

```
  Rule of Thumb:
  
  +----------------------------+----------------------------------+
  | Workload                   | Recommended Index Count          |
  +----------------------------+----------------------------------+
  | OLTP (transactions)        | 3-7 indexes per table            |
  | OLAP (analytics)           | Fewer indexes, more partitioning |
  | Read-heavy (90% reads)     | More indexes acceptable          |
  | Write-heavy (high inserts) | Minimal indexes, batch reindex   |
  +----------------------------+----------------------------------+
  
  Warning signs of over-indexing:
  - INSERT latency increasing over time
  - Index storage exceeds table storage
  - pg_stat_user_indexes shows indexes with 0 scans
  - MySQL slow query log shows "index_merge" (sign of missing composite index)
```

> **Golden Rules of Indexing:**
> 1. Index columns used in WHERE, JOIN, and ORDER BY clauses
> 2. Use composite indexes instead of multiple single-column indexes
> 3. Put equality columns before range columns in composite indexes
> 4. Use covering indexes for your most critical queries
> 5. Use partial indexes when you only query a subset of rows
> 6. Monitor unused indexes and drop them (`pg_stat_user_indexes` in PostgreSQL)
> 7. NEVER create indexes blindly — always verify with EXPLAIN ANALYZE

---

## Database Partitioning

**Database partitioning** splits a single large table into smaller, more manageable pieces
called **partitions**, all within the same database instance. This is different from sharding
(which distributes data across multiple database servers).

> **Key Distinction:**
> - **Partitioning** = one database server, table split into segments (logical division)
> - **Sharding** = multiple database servers, each holding a portion of the data (physical division)
> - Partitioning is a prerequisite step that can later evolve into sharding.

### Why Partition?

```
  Imagine a table with 500 million rows:
  
  WITHOUT partitioning:
  +----------------------------------------------------------+
  | orders (500 million rows, 200 GB)                        |
  | Every query touches this MASSIVE single table             |
  | Full table scan = 200 GB of I/O                          |
  | Index maintenance = slow (B+ Tree with depth 6+)         |
  | VACUUM/maintenance = locks entire table for hours         |
  +----------------------------------------------------------+
  
  WITH partitioning (by year):
  +------------------+ +------------------+ +------------------+
  | orders_2022      | | orders_2023      | | orders_2024      |
  | (150M rows, 60GB)| | (200M rows, 80GB)| | (150M rows, 60GB)|
  +------------------+ +------------------+ +------------------+
  
  Query: WHERE created_at > '2024-06-01'
  -> Only scans orders_2024 (60 GB instead of 200 GB)
  -> Index is smaller and faster
  -> VACUUM only needs to touch the active partition
  -> Old partitions can be archived or moved to cold storage
```

### Types of Partitioning

#### 1. Range Partitioning

Split data based on **value ranges** of a column. Most common for time-based data.

```
  CREATE TABLE orders (
      order_id    BIGINT,
      customer_id INT,
      amount      DECIMAL(10,2),
      created_at  TIMESTAMP
  ) PARTITION BY RANGE (created_at);
  
  CREATE TABLE orders_2022 PARTITION OF orders
      FOR VALUES FROM ('2022-01-01') TO ('2023-01-01');
  CREATE TABLE orders_2023 PARTITION OF orders
      FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');
  CREATE TABLE orders_2024 PARTITION OF orders
      FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
  
  How the query planner uses this:
  
  SELECT * FROM orders WHERE created_at = '2024-07-15';
  
  Query Planner:
  "created_at = '2024-07-15' falls in range ['2024-01-01', '2025-01-01')"
  -> PRUNE orders_2022 (skip entirely)
  -> PRUNE orders_2023 (skip entirely)
  -> SCAN orders_2024 only!  <-- This is called "partition pruning"
  
  Best for:
  - Time-series data (logs, events, transactions)
  - Data with a natural ordering (dates, IDs, versions)
  - Archival patterns (drop old partitions instead of DELETE)
```

#### 2. List Partitioning

Split data based on **discrete values** of a column.

```
  CREATE TABLE customers (
      customer_id  INT,
      name         VARCHAR(100),
      country      VARCHAR(3),
      email        VARCHAR(255)
  ) PARTITION BY LIST (country);
  
  CREATE TABLE customers_us PARTITION OF customers
      FOR VALUES IN ('US', 'CA', 'MX');           -- North America
  CREATE TABLE customers_eu PARTITION OF customers
      FOR VALUES IN ('UK', 'DE', 'FR', 'ES');     -- Europe
  CREATE TABLE customers_apac PARTITION OF customers
      FOR VALUES IN ('IN', 'JP', 'AU', 'SG');     -- Asia-Pacific
  
  Query: SELECT * FROM customers WHERE country = 'IN';
  -> Only scans customers_apac partition!
  
  Best for:
  - Geographic data (region-based queries)
  - Category-based data (product types, departments)
  - Multi-tenant applications (tenant_id based partitioning)
  - Compliance requirements (GDPR — keep EU data separate)
```

#### 3. Hash Partitioning

Split data based on a **hash function** applied to a column. Ensures even distribution
when there's no natural range or list.

```
  CREATE TABLE sessions (
      session_id  UUID,
      user_id     INT,
      data        JSONB,
      expires_at  TIMESTAMP
  ) PARTITION BY HASH (session_id);
  
  CREATE TABLE sessions_p0 PARTITION OF sessions
      FOR VALUES WITH (MODULUS 4, REMAINDER 0);
  CREATE TABLE sessions_p1 PARTITION OF sessions
      FOR VALUES WITH (MODULUS 4, REMAINDER 1);
  CREATE TABLE sessions_p2 PARTITION OF sessions
      FOR VALUES WITH (MODULUS 4, REMAINDER 2);
  CREATE TABLE sessions_p3 PARTITION OF sessions
      FOR VALUES WITH (MODULUS 4, REMAINDER 3);
  
  Distribution: hash(session_id) % 4 = 0,1,2, or 3
  Each partition gets ~25% of rows (very even distribution)
  
  Best for:
  - Even data distribution (no hotspots)
  - When there's no natural range for partitioning
  - Load balancing across storage devices
  
  Downside:
  - Range queries must scan ALL partitions (no pruning for ranges)
  - Adding partitions requires rehashing and data movement
```

#### 4. Composite (Sub-Partitioning)

Combine two partitioning strategies — partition by range first, then sub-partition by hash.

```
  orders
  ├── orders_2024 (RANGE on created_at)
  │   ├── orders_2024_p0 (HASH on customer_id, remainder 0)
  │   ├── orders_2024_p1 (HASH on customer_id, remainder 1)
  │   ├── orders_2024_p2 (HASH on customer_id, remainder 2)
  │   └── orders_2024_p3 (HASH on customer_id, remainder 3)
  ├── orders_2023 (RANGE on created_at)
  │   ├── orders_2023_p0
  │   ├── orders_2023_p1
  │   ├── orders_2023_p2
  │   └── orders_2023_p3
  
  Query: WHERE created_at = '2024-07-15' AND customer_id = 42
  -> Prune to orders_2024 (range)
  -> Prune to orders_2024_p2 (hash of customer_id)
  -> Scan only ONE sub-partition out of 8+ total!
```

### Partitioning vs. Sharding — Side by Side

```
  +-------------------+-----------------------------------+-----------------------------------+
  | Aspect            | Partitioning                      | Sharding                          |
  +-------------------+-----------------------------------+-----------------------------------+
  | Location          | Same database server              | Multiple database servers         |
  | Transparency      | Transparent to application        | Application must be shard-aware   |
  |                   | (single table, auto-routing)      | (route queries to correct shard)  |
  | Scaling           | Vertical (bigger machine)         | Horizontal (more machines)        |
  | JOINs             | Work normally across partitions   | Expensive cross-shard JOINs      |
  | Transactions      | Normal ACID transactions          | Distributed transactions needed   |
  | Maintenance       | Per-partition VACUUM, REINDEX     | Per-shard maintenance             |
  | Complexity        | Low (database handles it)         | High (application + infra)        |
  | When to use       | 10M - 1B rows on one server      | Beyond single server capacity    |
  +-------------------+-----------------------------------+-----------------------------------+
  
  Progression path:
  Single Table -> Partitioned Table -> Sharded across servers
  (simple)        (medium complexity)   (high complexity)
  
  Always try partitioning BEFORE sharding. Sharding is the last resort.
```

### Partition Pruning — The Key Benefit

```
  Partition pruning is the database's ability to skip irrelevant partitions entirely.
  
  Table: orders (500M rows, partitioned by month — 60 partitions over 5 years)
  
  Query: SELECT SUM(amount) FROM orders WHERE created_at >= '2024-06-01';
  
  WITHOUT partitioning:
    -> Full table scan: 500M rows, 200 GB I/O
    -> Time: ~45 minutes
  
  WITH partitioning + pruning:
    -> Prune 54 partitions (2020-2024 May)
    -> Scan only 6 partitions (2024 Jun - Dec): ~50M rows, 20 GB I/O
    -> Time: ~4.5 minutes (10x faster!)
  
  PostgreSQL EXPLAIN shows:
    ->  Append
      ->  Seq Scan on orders_2024_06 (actual rows=8000000)
      ->  Seq Scan on orders_2024_07 (actual rows=8500000)
      ... (only relevant partitions listed)
      
      "Subplans Removed: 54"  <-- 54 partitions were PRUNED!
```

---

## Database Sharding — Deep Dive

When a single database server — even with partitioning, read replicas, and caching — cannot
handle the load, you need **sharding**: distributing data across **multiple independent
database servers** (shards), each holding a portion of the total data.

> **When to shard:**
> - Single server storage exceeded (>5 TB)
> - Write throughput exceeds single server capacity (>50K writes/sec)
> - Read replicas + caching are insufficient
> - You need geographic data locality
>
> **When NOT to shard:**
> - You haven't tried: indexing, query optimization, connection pooling, read replicas, caching, or partitioning
> - Your data fits on one server with room to grow
> - Your team lacks the operational expertise for distributed databases

### How Sharding Works

```
  Application Layer          Routing Layer              Data Layer
  
  +------------+         +------------------+     +----------+
  | App Server |-------->| Shard Router /   |---->| Shard 0  |
  +------------+         | Proxy            |     | users    |
  +------------+         |                  |     | id: 0-999|
  | App Server |-------->| Routes queries   |     +----------+
  +------------+         | based on shard   |     +----------+
  +------------+         | key              |---->| Shard 1  |
  | App Server |-------->|                  |     | users    |
  +------------+         | Examples:        |     | id:1000- |
                         | - Vitess (MySQL) |     |     1999 |
                         | - Citus (PG)     |     +----------+
                         | - ProxySQL       |     +----------+
                         | - App-level      |---->| Shard 2  |
                         +------------------+     | users    |
                                                  | id:2000- |
                                                  |     2999 |
                                                  +----------+
```

### Sharding Strategies

#### 1. Key-Based (Hash) Sharding

```
  shard_id = hash(shard_key) % num_shards
  
  Example: 4 shards, shard_key = user_id
  
  user_id = 12345 -> hash(12345) = 948372 -> 948372 % 4 = 0 -> Shard 0
  user_id = 67890 -> hash(67890) = 283746 -> 283746 % 4 = 2 -> Shard 2
  user_id = 11111 -> hash(11111) = 571924 -> 571924 % 4 = 0 -> Shard 0
  
  Pros:
  + Even data distribution
  + Simple to implement
  + No lookup table needed
  
  Cons:
  - Adding/removing shards = MASSIVE data migration
    (changing from 4 to 5 shards remaps ~80% of data)
  - Range queries become scatter-gather
  - Use CONSISTENT HASHING to mitigate the reshuffling problem
```

#### 2. Range-Based Sharding

```
  Shard 0: user_id     1 -  999,999
  Shard 1: user_id 1,000,000 - 1,999,999
  Shard 2: user_id 2,000,000 - 2,999,999
  
  Pros:
  + Range queries on shard key are efficient (single shard)
  + Easy to understand and implement
  + New ranges = new shard (no data migration)
  
  Cons:
  - Hotspot risk: newest users (highest IDs) all go to the latest shard
  - Uneven distribution if ID ranges have different data volumes
  - "Append-heavy" problem: one shard gets all the writes
```

#### 3. Directory-Based Sharding

```
  +----------------------------+
  | Lookup Service             |
  |----------------------------|
  | shard_key    | shard       |
  |--------------|-------------|
  | user_id: 1-500  | shard_0 |
  | user_id: 501-900| shard_1 |
  | user_id: VIPs   | shard_2 |  <- Special routing for VIP users
  | user_id: 901+   | shard_1 |
  +----------------------------+
  
  Pros:
  + Maximum flexibility (arbitrary routing rules)
  + Easy rebalancing (just update the lookup table)
  + Can handle special cases (VIP users, hot accounts)
  
  Cons:
  - Lookup service is a single point of failure (must be HA)
  - Extra network hop on every query
  - Lookup table must be in-memory for performance (Redis, etc.)
```

#### 4. Geographic Sharding

```
  User in Mumbai -> DNS/Load Balancer -> India Shard (ap-south-1)
  User in London -> DNS/Load Balancer -> Europe Shard (eu-west-1)
  User in NYC    -> DNS/Load Balancer -> US Shard (us-east-1)
  
  Pros:
  + Low latency (data is close to the user)
  + Data sovereignty compliance (GDPR, data residency laws)
  + Natural isolation (regional failure doesn't affect other regions)
  
  Cons:
  - Cross-region queries are slow (e.g., "all users globally")
  - Uneven distribution (US shard might be 10x larger)
  - Users who travel: which shard is "home"?
```

### Choosing the Right Shard Key

The shard key is the **single most important decision** in a sharded system. A bad shard key
can make the entire system unusable.

```
  Good Shard Key checklist:
  +--------------------------------------------------------------------+
  | [x] High cardinality (millions of unique values)                   |
  | [x] Even distribution (no single value dominates)                  |
  | [x] Query-aligned (most queries include this column)               |
  | [x] Immutable (values don't change — moving rows between shards    |
  |     is extremely expensive)                                        |
  | [x] Co-locates related data (user's orders on same shard as user)  |
  +--------------------------------------------------------------------+
  
  Examples by domain:
  +-------------------+-------------------+-------------------------------------+
  | Domain            | Good Shard Key    | Why                                 |
  +-------------------+-------------------+-------------------------------------+
  | Social Media      | user_id           | Most queries are per-user           |
  | E-Commerce        | customer_id       | Orders, cart, history per customer  |
  | Chat Application  | channel_id        | Messages loaded per channel         |
  | Multi-Tenant SaaS | tenant_id         | Data isolation per tenant           |
  | IoT Platform      | device_id         | Readings per device                 |
  | Gaming            | player_id         | Game state per player               |
  +-------------------+-------------------+-------------------------------------+
  
  BAD Shard Keys:
  +-------------------+-----------------------------------------------------+
  | Bad Key           | Why                                                 |
  +-------------------+-----------------------------------------------------+
  | created_at        | All current writes go to one shard (hot partition)  |
  | country           | Only ~200 values; US = 50% of traffic               |
  | status            | 3-5 values = 3-5 shards max, uneven distribution    |
  | auto_increment_id | Latest shard always gets all writes                 |
  | email             | Queries by email are common but email can change    |
  +-------------------+-----------------------------------------------------+
```

### Cross-Shard Operations — The Hard Problems

```
  Problem 1: CROSS-SHARD JOINS
  
  SELECT u.name, o.total 
  FROM users u JOIN orders o ON u.id = o.user_id
  WHERE o.product_id = 42;
  
  If users and orders are sharded by user_id:
  -> product_id = 42 could be on ANY shard
  -> Must query ALL shards, gather results, join in application
  
  Solutions:
  a) Denormalize: store user_name in orders table (avoid the JOIN)
  b) Co-locate: shard both tables by user_id (same user's data on same shard)
  c) Global table: replicate small tables (products) to all shards
  
  ---
  
  Problem 2: CROSS-SHARD AGGREGATIONS
  
  SELECT country, COUNT(*) FROM users GROUP BY country;
  
  -> Must query ALL shards, aggregate partial results in app
  -> Solution: Maintain materialized aggregation tables, or use a data warehouse
  
  ---
  
  Problem 3: CROSS-SHARD TRANSACTIONS
  
  Transfer $100 from User A (Shard 1) to User B (Shard 3):
  -> Need distributed transaction (2PC or Saga pattern)
  -> 2PC: Coordinator asks both shards to prepare, then commit
  -> Saga: Debit A, then credit B; if credit fails, compensate by re-crediting A
  -> Both are complex and slow compared to single-shard transactions
  
  ---
  
  Problem 4: CROSS-SHARD UNIQUE CONSTRAINTS
  
  Ensure email is unique across ALL shards?
  -> Database can't enforce this — it only sees its own shard
  -> Solutions:
     a) Separate "email uniqueness" service with a global index
     b) Hash email to determine shard (all emails for same hash go to same shard)
     c) Check-then-insert with a distributed lock (slow but correct)
```

### Resharding — The Dreaded Migration

```
  Scenario: You started with 4 shards, now you need 8.
  
  Naive approach (hash % N):
    hash(key) % 4 != hash(key) % 8 for most keys
    -> ~75% of ALL data must be moved!
    -> During migration: reads may go to wrong shard, writes may be lost
  
  Better approach: Consistent Hashing
    -> Only ~K/N keys need to move (K = total keys, N = total nodes)
    -> Going from 4 to 8 shards: ~50% of keys move (to the new shards)
    -> Much more manageable than 75%
  
  Best approach: Logical Shards (what Instagram, Vitess do)
    -> Create 1000 logical shards mapped to 4 physical servers
    -> To add capacity: move some logical shards to new servers
    -> No data reshuffling within logical shards!
    
    Before (4 physical servers):
      Server 1: [LS_001 - LS_250]
      Server 2: [LS_251 - LS_500]
      Server 3: [LS_501 - LS_750]
      Server 4: [LS_751 - LS_1000]
    
    After (8 physical servers):
      Server 1: [LS_001 - LS_125]
      Server 2: [LS_126 - LS_250]
      Server 3: [LS_251 - LS_375]
      ...and so on
    
    Each logical shard is moved as a whole unit — no per-row rehashing!
```

> **Cross-reference:** For a deeper treatment of consistent hashing, virtual nodes, and
> shard rebalancing strategies, see [Chapter 11 — Data Partitioning and Sharding](./11-data-partitioning.md).

---

## Searching Records from a Table with Millions of Rows

This is one of the most practical questions in database engineering: **"I have a table with
50 million rows. How do I find records quickly?"** The answer depends on the type of search.

### Strategy 1: Indexed Lookup (The First Line of Defense)

For any query you run frequently, the answer almost always starts with: **create the right index.**

```
  Table: users (50 million rows)
  
  Query: SELECT * FROM users WHERE email = 'alice@example.com';
  
  Step 1: Check if index exists
    EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'alice@example.com';
    
    WITHOUT index:
    -> Seq Scan on users (cost=0.00..1,250,000.00 rows=1 width=120)
       Filter: (email = 'alice@example.com')
       Rows Removed by Filter: 49,999,999
       Planning Time: 0.1 ms
       Execution Time: 45,230 ms      <-- 45 SECONDS! Unacceptable.
    
    Step 2: Create index
    CREATE UNIQUE INDEX idx_users_email ON users(email);
    
    WITH index:
    -> Index Scan using idx_users_email on users (cost=0.56..8.58 rows=1 width=120)
       Index Cond: (email = 'alice@example.com')
       Planning Time: 0.1 ms
       Execution Time: 0.035 ms       <-- 0.035 MILLISECONDS! 1,000,000x faster!
```

### Strategy 2: Composite Index for Multi-Column Filters

```
  Query: SELECT * FROM orders 
         WHERE customer_id = 42 
         AND status = 'shipped' 
         AND created_at > '2024-01-01'
         ORDER BY created_at DESC
         LIMIT 20;
  
  Bad approach: Separate indexes on each column
    -> Database picks ONE index, then filters the rest with table scan
    -> Still slow for 50M rows
  
  Good approach: Composite index matching the query
    CREATE INDEX idx_orders_compound 
    ON orders(customer_id, status, created_at DESC);
    
    -> Equality columns first: customer_id, status
    -> Range/sort column last: created_at DESC
    -> Single B+ Tree traversal finds exactly the 20 rows needed
    -> Execution time: < 1ms
```

### Strategy 3: Pagination on Large Result Sets

When results are large, you must paginate. There are two approaches, and one is dramatically
better than the other.

```
  OFFSET-BASED PAGINATION (simple but TERRIBLE at scale):
  
  Page 1: SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 0;     -- Fast
  Page 2: SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 20;    -- Fast
  Page 100: SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 1980; -- Okay
  Page 50000: SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 999980;
  
  Problem: The database must SKIP 999,980 rows to get to page 50,000!
  It actually reads and discards all those rows. O(offset + limit).
  
  At offset = 1,000,000: query takes 10+ seconds even with an index.
  
  ---
  
  CURSOR-BASED (KEYSET) PAGINATION (the correct approach):
  
  Page 1: 
    SELECT * FROM orders ORDER BY id LIMIT 20;
    -> Returns rows id=1 to id=20
    -> Client saves last_id = 20
  
  Page 2: 
    SELECT * FROM orders WHERE id > 20 ORDER BY id LIMIT 20;
    -> B+ Tree seeks directly to id=21 (O(log n)), returns 20 rows
    -> Client saves last_id = 40
  
  Page 50000:
    SELECT * FROM orders WHERE id > 999980 ORDER BY id LIMIT 20;
    -> SAME PERFORMANCE as Page 1! O(log n) seek + 20 rows read
    -> Execution time: < 1ms regardless of "page number"
  
  Why it's faster:
  OFFSET:  Scan 1M rows -> discard 999,980 -> return 20
  CURSOR:  B+ Tree seek to id > 999,980 -> return next 20 rows
```

### Strategy 4: Partitioning for Time-Range Queries

```
  Query: "Find all transactions from the last 7 days"
  Table: transactions (500 million rows, 5 years of data)
  
  WITHOUT partitioning:
  -> Even with an index on created_at, the B+ Tree traversal returns 
     ~2 million rows and must fetch each from scattered disk pages
  -> Time: ~5-15 seconds
  
  WITH partitioning (monthly partitions):
  -> Partition pruning: only scan current month's partition (~8M rows)
  -> Index within partition is tiny and fits in memory
  -> Time: ~200ms
  
  WITH partitioning + BRIN index (Block Range INdex):
  -> BRIN index on created_at stores min/max per disk block
  -> Skips blocks that don't contain the target date range
  -> Time: ~50ms
```

### Strategy 5: Full-Text Search for Text Queries

```
  Query: "Find products matching 'wireless bluetooth headphones noise cancelling'"
  Table: products (10 million rows)
  
  BAD: LIKE with wildcards
    SELECT * FROM products WHERE description LIKE '%wireless%bluetooth%';
    -> Full table scan. LIKE with leading wildcard CANNOT use B-Tree index.
    -> Time: 30+ seconds
  
  GOOD: Full-text search index
    -- PostgreSQL:
    ALTER TABLE products ADD COLUMN search_vector tsvector;
    UPDATE products SET search_vector = to_tsvector('english', name || ' ' || description);
    CREATE INDEX idx_fts ON products USING GIN(search_vector);
    
    SELECT * FROM products 
    WHERE search_vector @@ to_tsquery('english', 'wireless & bluetooth & headphones')
    ORDER BY ts_rank(search_vector, to_tsquery('english', 'wireless & bluetooth & headphones')) DESC
    LIMIT 20;
    -> Time: ~10ms (with relevance ranking!)
  
  BEST: Dedicated search engine (for complex search needs)
    -> Elasticsearch / OpenSearch
    -> Supports: fuzzy matching, synonyms, faceted search, highlighting
    -> Real-time indexing of database changes via CDC (Change Data Capture)
    -> Sub-10ms search across billions of documents
```

### Strategy 6: Bloom Filters for Existence Checks

When you need to check **"does this record exist?"** across millions of records without
querying the database:

```
  Problem: Check if a username is taken (50 million users)
  
  Without Bloom Filter:
    SELECT 1 FROM users WHERE username = 'alice';
    -> Database query every time someone types a username
    -> Millions of queries during registration spikes
  
  With Bloom Filter:
    A Bloom Filter is a probabilistic data structure that can tell you:
    - "Definitely NOT in the set" (100% certain)
    - "PROBABLY in the set" (small false positive rate, ~1%)
    
    +--------------------------------------------------+
    | Bloom Filter (compact bit array, e.g., 64 MB)    |
    | Loaded in application memory                     |
    +--------------------------------------------------+
    
    Registration flow:
    1. User types "alice"
    2. Check Bloom Filter in memory (nanoseconds)
       -> "Definitely not taken" -> allow (no DB query needed!)
       -> "Probably taken" -> verify with DB query (only ~1% of the time)
    3. Result: 99% of checks avoid the database entirely
    
    Used by:
    - Google Bigtable, Apache Cassandra (avoid disk reads for non-existent keys)
    - Chrome (check URLs against malware database)
    - Medium (check if user has already read an article)
```

### Strategy 7: Caching Hot Query Results

```
  For frequently accessed records:
  
  +------------+     Cache      +----------+
  | App Server | --- Miss ----> | Database |
  |            |                | (50M     |
  |            | <-- Result --- | rows)    |
  |            |                +----------+
  |            |--- Store in ->
  |            |     Redis/     +----------+
  |            |     Memcached  | Cache    |
  +------------+                | (Redis)  |
        |                       +----------+
        |--- Cache Hit -------->|  O(1)   |
        |    (next time)        | lookup   |
        |<-- Result ------------|          |
                                +----------+
  
  Pattern: Cache-Aside (Lazy Loading)
  1. Check cache first
  2. If miss -> query DB -> store in cache with TTL
  3. If hit -> return cached result (no DB query)
  
  For 50M row tables with 80/20 rule (20% of data serves 80% of queries):
  -> Cache the hot 20% (10M rows) in Redis
  -> Cache hit rate: ~80-95%
  -> Database load reduced by 80-95%
```

### Strategy 8: Read Replicas for Read-Heavy Workloads

```
  For tables queried by many concurrent users:
  
  Writes:                         Reads:
  +--------+                      +--------+   +----------+
  | App    |--write-->| Primary  |   | App    |-->| Replica 1|
  +--------+          | Database |   +--------+   +----------+
                      |  (leader)|               | Replica 2|
                      +----+-----+               +----------+
                           |                     | Replica 3|
                           +-- replication -->   +----------+
  
  - Primary handles all writes (INSERT, UPDATE, DELETE)
  - Replicas handle all reads (SELECT)
  - Write throughput: 1x (single primary)
  - Read throughput: 4x (1 primary + 3 replicas)
  - Each replica has the full 50M rows, fully indexed
```

### Complete Decision Tree: How to Search Millions of Records

```
  "I need to find records in a table with 50 million rows"
        |
        +-- What type of search?
              |
              +-- Exact match on a column (email, ID, etc.)
              |     -> Create B+ Tree index on that column
              |     -> If searching by primary key, already indexed (clustered)
              |     -> Expected time: < 1ms
              |
              +-- Multi-column filter
              |     -> Create composite index (equality cols first, range cols last)
              |     -> Consider covering index to avoid table lookups
              |     -> Expected time: < 5ms
              |
              +-- Time-range queries (last 7 days, last month, etc.)
              |     -> Partition table by time (range partitioning)
              |     -> Add index within partition on the time column
              |     -> Expected time: 50-200ms
              |
              +-- Full-text search (search product descriptions, articles)
              |     -> Use GIN/full-text index (PostgreSQL) or Elasticsearch
              |     -> Expected time: 10-50ms
              |
              +-- Existence check (does this record exist?)
              |     -> Use Bloom filter in application memory
              |     -> Expected time: nanoseconds (no DB query!)
              |
              +-- Paginated results (page through 50M rows)
              |     -> Use cursor-based pagination (WHERE id > last_id)
              |     -> NEVER use OFFSET for deep pages
              |     -> Expected time: < 1ms per page
              |
              +-- Complex analytics (aggregations, GROUP BY, etc.)
              |     -> Use columnar storage or data warehouse (ClickHouse, BigQuery)
              |     -> Materialized views for repeated aggregations
              |     -> Expected time: seconds (but on MUCH more data)
              |
              +-- Geospatial queries (find users within 5km)
                    -> Use spatial index (GiST in PostgreSQL, R-Tree in MySQL)
                    -> Expected time: 10-50ms
```

---

## Query Optimization and Execution Plans

Understanding how the database **executes your query** is essential for performance tuning.
The query planner is the "brain" of the database — it decides the most efficient way to
retrieve your data.

### The Query Execution Pipeline

```
  SQL Query
      |
      v
  +-------------------+
  | 1. Parser         |  -- Checks syntax, validates table/column names
  +-------------------+
      |
      v
  +-------------------+
  | 2. Query Rewriter |  -- Expands views, applies rules, simplifies
  +-------------------+
      |
      v
  +-------------------+
  | 3. Query Planner  |  -- THE MOST IMPORTANT STEP
  |    (Optimizer)    |  -- Considers multiple execution strategies
  |                   |  -- Picks the one with lowest estimated cost
  +-------------------+
      |
      v
  +-------------------+
  | 4. Executor       |  -- Actually runs the chosen plan
  +-------------------+
      |
      v
  Result Set
```

### Reading EXPLAIN Plans

Every serious database query optimization starts with **EXPLAIN.** This is the single most
powerful tool for understanding database performance.

```sql
  -- PostgreSQL
  EXPLAIN ANALYZE SELECT * FROM orders 
  WHERE customer_id = 42 AND status = 'shipped' 
  ORDER BY created_at DESC LIMIT 10;
```

```
  EXPLAIN ANALYZE output:
  
  Limit  (cost=0.56..35.42 rows=10 width=120) (actual time=0.031..0.048 rows=10 loops=1)
    ->  Index Scan using idx_orders_cust_status_date on orders
          (cost=0.56..1250.42 rows=358 width=120) 
          (actual time=0.030..0.045 rows=10 loops=1)
          Index Cond: ((customer_id = 42) AND (status = 'shipped'))
          Scan Direction: Backward
  Planning Time: 0.152 ms
  Execution Time: 0.071 ms
  
  How to read this:
  +---------------------------------------------------------------------+
  | (cost=0.56..35.42 rows=10 width=120)                                |
  |   ^startup    ^total    ^estimated  ^avg row                        |
  |    cost        cost      rows        size (bytes)                   |
  |                                                                     |
  | (actual time=0.031..0.048 rows=10 loops=1)                          |
  |   ^actual      ^actual    ^actual    ^times this                    |
  |    startup      total      rows       node was                      |
  |    time(ms)     time(ms)   returned   executed                      |
  +---------------------------------------------------------------------+
```

### Common Scan Types (from fastest to slowest)

```
  +--------------------------------------------------------------+
  | Scan Type             | What It Does        | Performance    |
  +--------------------------------------------------------------+
  | Index Only Scan       | Reads ONLY the      | FASTEST        |
  |  (Covering Index)     | index, not table    | O(log n)       |
  +--------------------------------------------------------------+
  | Index Scan            | Uses index to find  | FAST           |
  |                       | rows, then fetches  | O(log n) + I/O |
  |                       | from table          |                |
  +--------------------------------------------------------------+
  | Bitmap Index Scan     | Builds a bitmap of  | MEDIUM         |
  |                       | matching pages,     | Good for       |
  |                       | then reads pages    | many matches   |
  +--------------------------------------------------------------+
  | Sequential Scan       | Reads EVERY row     | SLOWEST        |
  |  (Full Table Scan)    | in the table        | O(n)           |
  +--------------------------------------------------------------+
  
  Note: The query planner may CHOOSE a Seq Scan even when an index exists
  if it estimates that >5-10% of the table will be returned. This is correct
  behavior — random I/O (index) is slower than sequential I/O (seq scan)
  when reading a large portion of the table.
```

### Common Query Anti-Patterns (Performance Killers)

```
  1. FUNCTION ON INDEXED COLUMN (kills index usage)
  
     BAD:  WHERE YEAR(created_at) = 2024
           -> Cannot use index on created_at (function wraps the column)
     GOOD: WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'
           -> Uses index on created_at (direct comparison)
  
  2. IMPLICIT TYPE CONVERSION
  
     BAD:  WHERE phone_number = 1234567890  (phone_number is VARCHAR)
           -> Database converts EVERY row's phone_number to integer for comparison
           -> Full table scan!
     GOOD: WHERE phone_number = '1234567890'
           -> Direct string comparison, uses index
  
  3. LEADING WILDCARD IN LIKE
  
     BAD:  WHERE email LIKE '%@gmail.com'
           -> Cannot use B-Tree index (no known prefix)
           -> Full table scan!
     GOOD: WHERE email_domain = 'gmail.com'  (store domain separately)
           -> Or use full-text index / reverse index
  
  4. SELECT * WHEN YOU ONLY NEED 2 COLUMNS
  
     BAD:  SELECT * FROM users WHERE city = 'NYC'
           -> Must read all columns from table (even with index)
     GOOD: SELECT name, email FROM users WHERE city = 'NYC'
           -> With covering index on (city) INCLUDE (name, email)
           -> Index-only scan, no table access!
  
  5. OFFSET FOR DEEP PAGINATION
  
     BAD:  LIMIT 20 OFFSET 1000000 (reads 1M rows, discards 999,980)
     GOOD: WHERE id > last_seen_id ORDER BY id LIMIT 20
  
  6. N+1 QUERY PROBLEM
  
     BAD:  Loop over 100 orders, query customer for each:
           SELECT * FROM customers WHERE id = 1;
           SELECT * FROM customers WHERE id = 2;
           ... (100 separate queries!)
     GOOD: Single query with JOIN or IN:
           SELECT * FROM customers WHERE id IN (1, 2, 3, ..., 100);
           -> 1 query instead of 100
  
  7. MISSING INDEX ON JOIN COLUMN
  
     BAD:  SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id
           WHERE o.status = 'pending';
           -> If customer_id has no index: nested loop with full scan
     GOOD: CREATE INDEX idx_orders_customer ON orders(customer_id);
           -> Hash join or indexed nested loop
```

---

## Database Locking and Concurrency Control

When multiple transactions access the same data simultaneously, the database must ensure
correctness. This is done through **locking** and **concurrency control** mechanisms.

### Lock Granularity

```
  +----------------------------------------------------------------------+
  | Lock Level       | What It Locks    | Concurrency | Overhead         |
  +----------------------------------------------------------------------+
  | Table Lock       | Entire table     | Very Low    | Very Low         |
  |                  |                  | (blocks all)| (one lock)       |
  +----------------------------------------------------------------------+
  | Page Lock        | A disk page      | Medium      | Medium           |
  |                  | (~50-100 rows)   |             |                  |
  +----------------------------------------------------------------------+
  | Row Lock         | Single row       | High        | High             |
  |                  |                  | (blocks one)| (lock per row)   |
  +----------------------------------------------------------------------+
  
  Most modern databases (PostgreSQL, MySQL/InnoDB) use ROW-LEVEL locking.
  SQLite and MyISAM use TABLE-LEVEL locking.
```

### Optimistic vs Pessimistic Concurrency Control

```
  PESSIMISTIC LOCKING (Lock first, then work):
  
  Transaction A:                          Transaction B:
  BEGIN;                                  BEGIN;
  SELECT * FROM products                  
  WHERE id = 1 FOR UPDATE;               SELECT * FROM products
  -- Row is LOCKED                        WHERE id = 1 FOR UPDATE;
  -- A can read and write                 -- BLOCKED! Waits for A's lock...
  UPDATE products SET stock = stock - 1   -- ...still waiting...
  WHERE id = 1;                           -- ...still waiting...
  COMMIT;                                 -- Lock released! B proceeds now.
  -- Lock released                        UPDATE products SET stock = stock - 1
                                          WHERE id = 1;
                                          COMMIT;
  
  Pros: Guarantees no conflicts (data is locked)
  Cons: Blocks other transactions (reduced throughput)
  Best for: High contention (many writers on same rows)
  
  ---
  
  OPTIMISTIC LOCKING (Work first, check for conflicts at commit):
  
  Transaction A:                          Transaction B:
  BEGIN;                                  BEGIN;
  SELECT * FROM products                  SELECT * FROM products
  WHERE id = 1;                           WHERE id = 1;
  -- Reads: stock=10, version=5           -- Reads: stock=10, version=5
  -- (no lock acquired)                   -- (no lock acquired)
  ...processes...                         ...processes...
  UPDATE products                         UPDATE products
  SET stock = 9, version = 6             SET stock = 9, version = 6
  WHERE id = 1 AND version = 5;           WHERE id = 1 AND version = 5;
  -- Success! 1 row updated              -- FAILS! 0 rows updated (version
  COMMIT;                                 --   is now 6, not 5)
                                          -- Application retries with fresh data
  
  Pros: No blocking, higher throughput
  Cons: Retries needed on conflict (wasted work)
  Best for: Low contention (rare conflicts), read-heavy workloads
```

### Deadlocks

```
  Transaction A:                     Transaction B:
  LOCK row 1 (success)              LOCK row 2 (success)
  LOCK row 2 (waiting for B...)     LOCK row 1 (waiting for A...)
  
          DEADLOCK!
          Neither can proceed.
  
  +--------+    wants     +--------+
  |  Tx A  | ----------> | Row 2  |
  |  holds |             | held   |
  | Row 1  | <---------- | by B   |
  +--------+    wants    +--------+
  
  Resolution:
  - Database detects the deadlock cycle (timeout or wait-for graph)
  - One transaction is chosen as the "victim" and rolled back
  - The other transaction proceeds
  
  Prevention strategies:
  1. Always lock resources in the SAME ORDER (e.g., by ID ascending)
  2. Keep transactions short (less time holding locks)
  3. Use optimistic locking for low-contention scenarios
  4. Set lock_timeout to avoid infinite waits
```

### MVCC (Multi-Version Concurrency Control)

Most modern databases (PostgreSQL, MySQL/InnoDB, Oracle) use **MVCC** instead of pure locking.
MVCC allows **readers and writers to not block each other.**

```
  Traditional Locking:
    Writer locks row -> Reader BLOCKED until writer commits
    (Readers and writers block each other)
  
  MVCC:
    Writer creates a NEW VERSION of the row
    Reader sees the OLD VERSION (snapshot)
    Neither blocks the other!
  
  How it works (simplified):
  
  Time T1: Row = {id: 1, name: "Alice", version: 100}
  
  Time T2: Transaction A starts (snapshot at version 100)
           Transaction B starts UPDATE name = "Bob" WHERE id = 1
           -> Creates new version: {id: 1, name: "Bob", version: 101}
           -> Old version {version: 100} is kept for Transaction A
  
  Time T3: Transaction A reads row id=1
           -> Sees {name: "Alice"} (version 100 — its snapshot)
           Transaction B commits
  
  Time T4: Transaction A reads row id=1 again
           -> Still sees {name: "Alice"} (repeatable read — snapshot consistency)
           -> New transaction would see {name: "Bob"} (version 101)
  
  Cleanup: Old versions are garbage-collected by VACUUM (PostgreSQL) 
           or purge thread (MySQL/InnoDB) after all transactions that 
           could see them have finished.
  
  Key benefit: READ QUERIES NEVER BLOCK. This is why PostgreSQL and MySQL
  can handle thousands of concurrent readers without performance degradation.
```

---

## Denormalization

**Normalization** eliminates data redundancy by splitting data into related tables. **Denormalization** intentionally adds redundancy to improve read performance.

### Normalized (3NF)

```
  orders table:             order_items table:        products table:
  +----+---------+          +----+--------+------+    +----+--------+-------+
  | id | user_id |          | id | ord_id | p_id |    | id | name   | price |
  +----+---------+          +----+--------+------+    +----+--------+-------+
  |  1 |    101  |          |  1 |    1   |  501 |    |501 | Laptop | 999   |
  |  2 |    102  |          |  2 |    1   |  502 |    |502 | Mouse  |  29   |
  +----+---------+          +----+--------+------+    +----+--------+-------+
  
  Query requires 3-way JOIN to get order details with product names.
```

### Denormalized

```
  orders_denormalized table:
  +----+---------+----------+--------+-------+
  | id | user_id | product  | p_name | price |
  +----+---------+----------+--------+-------+
  |  1 |    101  |    501   | Laptop |  999  |
  |  1 |    101  |    502   | Mouse  |   29  |
  |  2 |    102  |    501   | Laptop |  999  |
  +----+---------+----------+--------+-------+
  
  Single table read — no JOINs needed. Much faster!
  But: product name/price is duplicated. If price changes, 
  must update ALL rows.
```

### When to Denormalize

- **Read-heavy workloads** (read:write ratio > 10:1)
- **Performance-critical queries** that require multiple JOINs
- **Reporting and analytics** (data warehouse / OLAP)
- **NoSQL databases** where JOINs aren't available

---

## Database Connection Pooling

Opening a database connection is **expensive** — it involves TCP handshake, authentication, memory allocation. Connection pooling maintains a **pool of reusable connections**.

```
  Without Pooling:                    With Pooling:
  
  Request 1: Open conn -> query       Request 1 ----+
             -> close conn            Request 2 ----+--> Connection Pool
  Request 2: Open conn -> query       Request 3 ----+   (10 connections)
             -> close conn                               |  |  |  |
  Request 3: Open conn -> query                         DB DB DB DB
             -> close conn
  
  Each open/close: ~5-20ms            Reuse existing connection: ~0.1ms
```

**Popular connection poolers:**
- **HikariCP** — Java (fastest, most popular)
- **PgBouncer** — PostgreSQL-specific connection pooler
- **ProxySQL** — MySQL proxy with connection pooling
- **c3p0** — Java (older, still used)

**Key configuration:**
- **Min pool size:** Minimum connections kept alive (e.g., 5)
- **Max pool size:** Maximum connections allowed (e.g., 20)
- **Connection timeout:** How long to wait for a free connection
- **Idle timeout:** How long an idle connection stays in the pool

---

## Choosing the Right Database — Decision Framework

```
                          What's your primary need?
                                    |
                    +---------------+---------------+
                    |               |               |
              Transactions?    Flexible Schema?   Relationships?
              Complex JOINs?   High Write Scale?  Graph Traversal?
                    |               |               |
                    v               v               v
              +----------+   +------------+   +----------+
              |   SQL    |   |   NoSQL    |   |  Graph   |
              | Postgres |   |            |   |  Neo4j   |
              | MySQL    |   +------+-----+   |  Neptune |
              +----------+          |         +----------+
                                    |
                    +---------------+---------------+
                    |               |               |
              Simple KV?      Documents?      Time-Series /
              Caching?        Content Mgmt?   Wide Rows?
                    |               |               |
                    v               v               v
              +----------+   +----------+    +----------+
              | Key-Value|   | Document |    | Column   |
              | Redis    |   | MongoDB  |    | Cassandra|
              | DynamoDB |   | CouchDB  |    | HBase    |
              +----------+   +----------+    +----------+
```

### Quick Decision Table

| Scenario | Recommended Database | Reason |
|---|---|---|
| E-commerce orders | PostgreSQL | ACID transactions, complex queries |
| User sessions | Redis | Fast key-value lookups, TTL support |
| Product catalog | MongoDB | Flexible schema, nested attributes |
| Social network feed | Cassandra | High write throughput, time-series |
| Recommendation engine | Neo4j | Relationship traversal |
| Real-time analytics | ClickHouse / Druid | Columnar, fast aggregations |
| Chat messages | Cassandra / ScyllaDB | High write, partition by conversation |
| Financial ledger | PostgreSQL / CockroachDB | Strong ACID, audit trail |
| IoT sensor data | TimescaleDB / InfluxDB | Time-series optimized |
| Search / full-text | Elasticsearch | Inverted index, relevance scoring |

---

## NewSQL Databases

NewSQL databases aim to provide the **scalability of NoSQL** with the **ACID guarantees of SQL**. They distribute data across nodes while maintaining full SQL support and transactions.

| Database | Architecture | Key Feature | Used By |
|---|---|---|---|
| **Google Spanner** | Globally distributed, TrueTime clocks | Externally consistent global transactions | Google (AdWords, Play Store) |
| **CockroachDB** | Distributed PostgreSQL-compatible | Survives node/region failures, serializable isolation | DoorDash, Netflix |
| **TiDB** | MySQL-compatible, distributed | HTAP (analytics + transactions), Raft consensus | PingCAP, banking |
| **YugabyteDB** | PostgreSQL-compatible, distributed | Multi-region, tunable consistency | Kroger, Wells Fargo |
| **VoltDB** | In-memory, distributed | Ultra-low latency transactions | Telecom, gaming |

### Google Spanner — The Breakthrough

Spanner uses **TrueTime** (GPS + atomic clocks) to achieve **externally consistent** globally distributed transactions — something previously thought impossible.

```
  +-------------+     +-------------+     +-------------+
  |  US-East    |     |  EU-West    |     |  AP-South   |
  |  Spanner    |<--->|  Spanner    |<--->|  Spanner    |
  |  Node       |     |  Node       |     |  Node       |
  |  +--------+ |     |  +--------+ |     |  +--------+ |
  |  |TrueTime| |     |  |TrueTime| |     |  |TrueTime| |
  |  | GPS +   | |     |  | GPS +   | |     |  | GPS +   | |
  |  | Atomic  | |     |  | Atomic  | |     |  | Atomic  | |
  |  | Clocks  | |     |  | Clocks  | |     |  | Clocks  | |
  |  +--------+ |     |  +--------+ |     |  +--------+ |
  +-------------+     +-------------+     +-------------+
  
  Global transactions with strong consistency across continents!
```

---

## Time-Series Databases

Optimized for **time-stamped data** — metrics, sensor data, stock prices, log events.

| Database | Architecture | Best For |
|---|---|---|
| **InfluxDB** | Custom storage engine, flux query language | DevOps monitoring, IoT |
| **TimescaleDB** | PostgreSQL extension with hypertables | Time-series with SQL, hybrid workloads |
| **Prometheus** | Pull-based metrics collection | Kubernetes monitoring, alerting |
| **QuestDB** | Column-oriented, SQL | High-ingestion financial data |

### Why Not Just Use a Regular Database?

Time-series databases optimize for:
- **High write throughput** — millions of data points per second
- **Time-range queries** — "Get all metrics from the last 24 hours"
- **Automatic data compaction and downsampling** — Aggregate old data to save space
- **Built-in time functions** — Moving averages, rate of change, windowed aggregations

---

## Key Takeaways

```
+---------------------------------------------------------------------+
|                   Databases — Key Takeaways                          |
+---------------------------------------------------------------------+
|                                                                      |
|  1. SQL databases (PostgreSQL, MySQL) are the default choice         |
|     for most applications. They provide ACID, complex queries,       |
|     and a mature ecosystem. Start here unless you have a specific    |
|     reason not to.                                                   |
|                                                                      |
|  2. NoSQL databases solve specific problems: Redis for caching,      |
|     MongoDB for flexible documents, Cassandra for high write         |
|     scale, Neo4j for graph traversals. Pick based on access          |
|     patterns, not hype.                                              |
|                                                                      |
|  3. ACID vs BASE is a spectrum, not a binary choice. Many            |
|     databases offer tunable consistency. Understand where your       |
|     application falls on this spectrum.                              |
|                                                                      |
|  4. Indexing is the #1 database optimization. Use B+ Tree            |
|     indexes for range queries, hash for equality, full-text          |
|     (inverted index) for search. Understand clustered vs             |
|     non-clustered, covering indexes, and composite index             |
|     column ordering (equality first, range last).                    |
|                                                                      |
|  5. To search millions of rows efficiently: create the right         |
|     index, use composite indexes for multi-column filters,           |
|     partition for time-range queries, use cursor-based pagination    |
|     (never OFFSET for deep pages), and cache hot data.               |
|                                                                      |
|  6. Always use EXPLAIN ANALYZE before and after optimization.        |
|     Learn to read execution plans — they tell you exactly what       |
|     the database is doing and where time is spent.                   |
|                                                                      |
|  7. Partitioning splits a table within one server. Sharding          |
|     splits data across multiple servers. Always try partitioning,    |
|     read replicas, and caching BEFORE sharding. Sharding adds       |
|     enormous complexity (cross-shard joins, distributed              |
|     transactions, resharding).                                       |
|                                                                      |
|  8. The shard key is the most critical decision in a sharded         |
|     system. It must have high cardinality, even distribution,        |
|     and align with your most common query patterns.                  |
|                                                                      |
|  9. Denormalize when reads vastly outnumber writes and               |
|     JOIN performance is a bottleneck. Accept data duplication        |
|     as a trade-off for read speed.                                   |
|                                                                      |
| 10. Connection pooling is essential for production systems.          |
|     Use HikariCP (Java), PgBouncer (PostgreSQL).                     |
|                                                                      |
| 11. Understand concurrency control: MVCC allows readers and          |
|     writers to coexist without blocking. Use optimistic locking      |
|     for low-contention, pessimistic for high-contention.             |
|                                                                      |
| 12. NewSQL (CockroachDB, Spanner) bridges the gap between           |
|     SQL guarantees and NoSQL scalability — consider these for        |
|     globally distributed ACID requirements.                          |
|                                                                      |
| 13. Avoid common anti-patterns: functions on indexed columns,        |
|     leading wildcards in LIKE, SELECT *, N+1 queries, implicit       |
|     type conversions, and OFFSET-based deep pagination.              |
|                                                                      |
+---------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 9 — Storage Systems](./09-storage.md)*
