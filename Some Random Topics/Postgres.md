# PostgreSQL & Database Fundamentals - Complete Guide

**A Practical Guide from a 16-Year Java Developer Perspective**

## Table of Contents

1. [SQL Basics](#sql-basics)
2. [Database Joins](#database-joins)
3. [Database Indexing](#database-indexing)
4. [Query Optimization](#query-optimization)
5. [Database Sharding](#database-sharding)
6. [PGBouncer Connection Pooling](#pgbouncer-connection-pooling)
7. [Java Developer Perspective](#java-developer-perspective)
8. [Interview Questions & Answers](#interview-questions--answers)

---

## SQL Basics

### CRUD Operations

#### **CREATE (INSERT)**
```sql
-- Single insert
INSERT INTO users (id, name, email, age)
VALUES (1, 'Alice', 'alice@example.com', 25);

-- Multiple inserts (efficient)
INSERT INTO users (id, name, email, age) VALUES
  (2, 'Bob', 'bob@example.com', 30),
  (3, 'Charlie', 'charlie@example.com', 28);

-- Insert with returning (useful for getting auto-generated IDs in Java)
INSERT INTO users (name, email, age)
VALUES ('David', 'david@example.com', 32)
RETURNING id;  -- Returns the generated ID
```

#### **READ (SELECT)**
```sql
-- Basic select
SELECT id, name, email FROM users WHERE age > 25;

-- Select with ordering and limit
SELECT id, name, age FROM users 
ORDER BY age DESC 
LIMIT 10;

-- Select with aggregation
SELECT COUNT(*) as total_users, AVG(age) as avg_age FROM users;

-- Select with grouping
SELECT department, COUNT(*) as dept_count, AVG(salary) as avg_salary
FROM employees
GROUP BY department
HAVING COUNT(*) > 5;
```

#### **UPDATE**
```sql
-- Simple update
UPDATE users SET email = 'newemail@example.com' WHERE id = 1;

-- Conditional update
UPDATE products SET stock = stock - 1 
WHERE id = 100 AND stock > 0;

-- Update multiple columns
UPDATE users 
SET email = 'updated@example.com', updated_at = NOW()
WHERE id = 1;

-- Update with RETURNING (great for Java apps)
UPDATE users 
SET email = 'new@example.com' 
WHERE id = 1
RETURNING id, email, updated_at;
```

#### **DELETE**
```sql
-- Simple delete
DELETE FROM users WHERE id = 1;

-- Delete with condition
DELETE FROM orders WHERE created_at < NOW() - INTERVAL '1 year';

-- Delete with RETURNING (to know what was deleted)
DELETE FROM users WHERE id = 1
RETURNING id, name, email;
```

### Transaction Control

```sql
-- Basic transaction
BEGIN;
  UPDATE accounts SET balance = balance - 100 WHERE id = 1;
  UPDATE accounts SET balance = balance + 100 WHERE id = 2;
COMMIT;

-- Transaction with rollback on error
BEGIN;
  UPDATE accounts SET balance = balance - 100 WHERE id = 1;
  -- If error occurs, automatic rollback
ROLLBACK;

-- Savepoint (nested transactions)
BEGIN;
  UPDATE users SET status = 'active' WHERE id = 1;
  SAVEPOINT sp1;
  UPDATE accounts SET balance = 0 WHERE user_id = 1;
  ROLLBACK TO sp1;  -- Rollback only the account update
COMMIT;  -- Commit the user status update
```

### Constraints & Data Integrity

```sql
-- Create table with constraints
CREATE TABLE users (
    id SERIAL PRIMARY KEY,                          -- Auto-increment, unique
    email VARCHAR(255) NOT NULL UNIQUE,            -- Unique, cannot be null
    name VARCHAR(255) NOT NULL,                    -- Cannot be null
    age INT CHECK (age >= 0 AND age <= 150),      -- Value validation
    country VARCHAR(2) DEFAULT 'US',               -- Default value
    created_at TIMESTAMP DEFAULT NOW(),            -- Current timestamp
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Foreign key constraint
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),    -- Must exist in users
    order_date TIMESTAMP DEFAULT NOW(),
    total DECIMAL(10, 2)
);
```

---

## Database Joins

### 1. INNER JOIN

**What it does**: Returns rows that exist in BOTH tables

```sql
SELECT u.id, u.name, o.order_id, o.total
FROM users u
INNER JOIN orders o ON u.id = o.user_id;
```

**Visual Representation**:
```
USERS TABLE              ORDERS TABLE
┌─────────┬───────────┐  ┌──────────┬─────────┬─────┐
│ id      │ name      │  │ order_id │ user_id │total│
├─────────┼───────────┤  ├──────────┼─────────┼─────┤
│ 1       │ Alice     │  │ 101      │ 1       │$50  │
│ 2       │ Bob       │  │ 102      │ 1       │$75  │
│ 3       │ Charlie   │  │ 103      │ 2       │$100 │
│ 4       │ David     │  │ 104      │ 5       │$200 │
└─────────┴───────────┘  └──────────┴─────────┴─────┘

INNER JOIN RESULT (users with orders):
┌───────┬─────────┬──────────┬─────┐
│ id    │ name    │ order_id │ total│
├───────┼─────────┼──────────┼─────┤
│ 1     │ Alice   │ 101      │ $50 │
│ 1     │ Alice   │ 102      │ $75 │
│ 2     │ Bob     │ 103      │$100 │
│ 5     │ (NULL)  │ 104      │$200 │  ← user_id 5 doesn't exist
└───────┴─────────┴──────────┴─────┘

Note: User 4 (David) NOT in result (no orders)
      Order 104 NOT in result (user_id 5 not in users)
```

**Use Case**: Find users who have placed orders
```
RESULT: Only Alice (2 orders), Bob (1 order)
        David appears in result ONLY if has orders
```

### 2. LEFT JOIN (LEFT OUTER JOIN)

**What it does**: Returns ALL rows from LEFT table + matching rows from RIGHT

```sql
SELECT u.id, u.name, o.order_id, o.total
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;
```

**Visual Representation**:
```
USERS (LEFT) TABLE       ORDERS (RIGHT) TABLE
┌─────────┬───────────┐  ┌──────────┬─────────┬─────┐
│ id      │ name      │  │ order_id │ user_id │ total│
├─────────┼───────────┤  ├──────────┼─────────┼─────┤
│ 1       │ Alice     │  │ 101      │ 1       │ $50 │
│ 2       │ Bob       │  │ 102      │ 1       │ $75 │
│ 3       │ Charlie   │  │ 103      │ 2       │$100 │
│ 4       │ David     │  │ 104      │ 5       │$200 │
└─────────┴───────────┘  └──────────┴─────────┴─────┘

LEFT JOIN RESULT (all users, matched orders):
┌───────┬─────────┬──────────┬──────┐
│ id    │ name    │ order_id │ total│
├───────┼─────────┼──────────┼──────┤
│ 1     │ Alice   │ 101      │ $50 │
│ 1     │ Alice   │ 102      │ $75 │
│ 2     │ Bob     │ 103      │$100 │
│ 3     │ Charlie │ NULL     │ NULL │ ← No orders, shows NULL
│ 4     │ David   │ NULL     │ NULL │ ← No orders, shows NULL
└───────┴─────────┴──────────┴──────┘

FLOW:
1. Start with ALL users from left table
2. Try to match with orders
3. If match found: show order data
4. If NO match: show NULL for order columns
```

**Use Case**: Find all users and their orders (including users with no orders)
```
RESULT: Alice (2 orders), Bob (1 order), Charlie (NULL), David (NULL)
        Useful for: "Show all customers and if they have orders"
```

### 3. RIGHT JOIN (RIGHT OUTER JOIN)

**What it does**: Returns ALL rows from RIGHT table + matching rows from LEFT

```sql
SELECT u.id, u.name, o.order_id, o.total
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id;
```

**Visual Representation**:
```
USERS (LEFT) TABLE       ORDERS (RIGHT) TABLE
┌─────────┬───────────┐  ┌──────────┬─────────┬─────┐
│ id      │ name      │  │ order_id │ user_id │ total│
├─────────┼───────────┤  ├──────────┼─────────┼─────┤
│ 1       │ Alice     │  │ 101      │ 1       │ $50 │
│ 2       │ Bob       │  │ 102      │ 1       │ $75 │
│ 3       │ Charlie   │  │ 103      │ 2       │$100 │
│ 4       │ David     │  │ 104      │ 5       │$200 │
└─────────┴───────────┘  └──────────┴─────────┴─────┘

RIGHT JOIN RESULT (all orders, matched users):
┌───────┬──────────┬──────────┬──────┐
│ id    │ name     │ order_id │ total│
├───────┼──────────┼──────────┼──────┤
│ 1     │ Alice    │ 101      │ $50 │
│ 1     │ Alice    │ 102      │ $75 │
│ 2     │ Bob      │ 103      │$100 │
│ NULL  │ NULL     │ 104      │$200 │ ← Order from non-existent user_id 5
└───────┴──────────┴──────────┴──────┘

FLOW:
1. Start with ALL orders from right table
2. Try to match with users
3. If match found: show user data
4. If NO match: show NULL for user columns
```

**Use Case**: Find all orders and their corresponding users (including orphaned orders)
```
RESULT: All 4 orders, but order 104 shows NULL for user info
        Useful for: "Show all orders, flag orphaned/invalid orders"
```

### 4. FULL OUTER JOIN

**What it does**: Returns ALL rows from BOTH tables (LEFT JOIN + RIGHT JOIN combined)

```sql
SELECT u.id, u.name, o.order_id, o.total
FROM users u
FULL OUTER JOIN orders o ON u.id = o.user_id;
```

**Visual Representation**:
```
USERS (LEFT) TABLE       ORDERS (RIGHT) TABLE
┌─────────┬───────────┐  ┌──────────┬─────────┬─────┐
│ id      │ name      │  │ order_id │ user_id │ total│
├─────────┼───────────┤  ├──────────┼─────────┼─────┤
│ 1       │ Alice     │  │ 101      │ 1       │ $50 │
│ 2       │ Bob       │  │ 102      │ 1       │ $75 │
│ 3       │ Charlie   │  │ 103      │ 2       │$100 │
│ 4       │ David     │  │ 104      │ 5       │$200 │
└─────────┴───────────┘  └──────────┴─────────┴─────┘

FULL OUTER JOIN RESULT (all users AND all orders):
┌───────┬─────────┬──────────┬──────┐
│ id    │ name    │ order_id │ total│
├───────┼─────────┼──────────┼──────┤
│ 1     │ Alice   │ 101      │ $50 │
│ 1     │ Alice   │ 102      │ $75 │
│ 2     │ Bob     │ 103      │$100 │
│ 3     │ Charlie │ NULL     │ NULL │ ← User with no orders
│ NULL  │ NULL    │ 104      │$200 │ ← Order from non-existent user
│ 4     │ David   │ NULL     │ NULL │ ← User with no orders
└───────┴─────────┴──────────┴──────┘

FLOW:
1. Include ALL rows from left table (users)
2. Include ALL rows from right table (orders)
3. Match where possible
4. Show NULL where no match
```

**Use Case**: Data reconciliation (find anomalies and orphaned records)
```
RESULT: Shows all users + all orders, revealing:
        - Users with no orders (Charlie, David with NULLs)
        - Orders from non-existent users (order 104)
        Useful for: "Audit all data, find data integrity issues"
```

### 5. CROSS JOIN

**What it does**: Cartesian product - every row from LEFT joined with every row from RIGHT

```sql
SELECT u.id, u.name, c.category_name
FROM users u
CROSS JOIN categories c;
```

**Visual Representation**:
```
USERS TABLE              CATEGORIES TABLE
┌────┬───────┐           ┌──────────────┐
│ id │ name  │           │ category_name│
├────┼───────┤           ├──────────────┤
│ 1  │ Alice │           │ Electronics  │
│ 2  │ Bob   │           │ Books        │
│ 3  │ Carol │           │ Clothing     │
└────┴───────┘           └──────────────┘

CROSS JOIN RESULT (3 users × 3 categories = 9 rows):
┌────┬───────┬──────────────┐
│ id │ name  │ category_name│
├────┼───────┼──────────────┤
│ 1  │ Alice │ Electronics  │
│ 1  │ Alice │ Books        │
│ 1  │ Alice │ Clothing     │
│ 2  │ Bob   │ Electronics  │
│ 2  │ Bob   │ Books        │
│ 2  │ Bob   │ Clothing     │
│ 3  │ Carol │ Electronics  │
│ 3  │ Carol │ Books        │
│ 3  │ Carol │ Clothing     │
└────┴───────┴──────────────┘

FORMULA: ROWS(LEFT) × ROWS(RIGHT) = TOTAL ROWS
         3 users × 3 categories = 9 rows
```

**Use Case**: Generate all combinations (e.g., every user with every product category)
```
RESULT: All possible user-category combinations
        Useful for: "Show all users interested in all categories"
```

### 6. SELF JOIN

**What it does**: Join a table with itself

```sql
-- Find employees and their managers
SELECT e.id as employee_id, e.name as employee_name,
       m.id as manager_id, m.name as manager_name
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;
```

**Visual Representation**:
```
EMPLOYEES TABLE
┌────┬───────────┬────────────┐
│ id │ name      │ manager_id │
├────┼───────────┼────────────┤
│ 1  │ Alice     │ NULL       │  ← CEO, no manager
│ 2  │ Bob       │ 1          │  ← Reports to Alice
│ 3  │ Carol     │ 1          │  ← Reports to Alice
│ 4  │ David     │ 2          │  ← Reports to Bob
│ 5  │ Eve       │ 3          │  ← Reports to Carol
└────┴───────────┴────────────┘

SELF JOIN RESULT:
┌──────────────┬──────────────┬──────────────┬────────────────┐
│ employee_id  │ employee_name│ manager_id   │ manager_name   │
├──────────────┼──────────────┼──────────────┼────────────────┤
│ 1            │ Alice        │ NULL         │ NULL           │
│ 2            │ Bob          │ 1            │ Alice          │
│ 3            │ Carol        │ 1            │ Alice          │
│ 4            │ David        │ 2            │ Bob            │
│ 5            │ Eve          │ 3            │ Carol          │
└──────────────┴──────────────┴──────────────┴────────────────┘

ORGANIZATION STRUCTURE:
        Alice (CEO)
        /      \
      Bob      Carol
      |        |
     David    Eve
```

**Use Case**: Hierarchical relationships (manager-employee, parent-child)

### 7. ANTI JOIN (NOT IN / NOT EXISTS)

**What it does**: Returns rows from LEFT table that have NO match in RIGHT table

```sql
-- Users who have NOT placed any orders
SELECT u.id, u.name
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = u.id
);

-- Equivalent using NOT IN
SELECT u.id, u.name
FROM users u
WHERE u.id NOT IN (
    SELECT DISTINCT user_id FROM orders WHERE user_id IS NOT NULL
);
```

**Visual Representation**:
```
USERS TABLE              ORDERS TABLE (user_ids)
┌────┬─────────┐         [1, 1, 2, 5]
│ id │ name    │         
├────┼─────────┤         
│ 1  │ Alice   │ X (in orders)
│ 2  │ Bob     │ X (in orders)
│ 3  │ Charlie │ ✓ NOT in orders
│ 4  │ David   │ ✓ NOT in orders
└────┴─────────┘         

ANTI JOIN RESULT:
┌────┬─────────┐
│ id │ name    │
├────┼─────────┤
│ 3  │ Charlie │
│ 4  │ David   │
└────┴─────────┘
```

**Use Case**: Find missing relationships, orphaned data
```
RESULT: Users with NO orders (Charlie, David)
        Useful for: "Find inactive customers", "Find unused categories"
```

### 8. SEMI JOIN (IN / EXISTS)

**What it does**: Returns rows from LEFT table that have at least ONE match in RIGHT table

```sql
-- Users who HAVE placed at least one order
SELECT u.id, u.name
FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o WHERE o.user_id = u.id
);

-- Equivalent using IN
SELECT u.id, u.name
FROM users u
WHERE u.id IN (
    SELECT DISTINCT user_id FROM orders
);
```

**Visual Representation**:
```
USERS TABLE              ORDERS TABLE (user_ids)
┌────┬─────────┐         [1, 1, 2, 5]
│ id │ name    │         
├────┼─────────┤         
│ 1  │ Alice   │ ✓ (in orders)
│ 2  │ Bob     │ ✓ (in orders)
│ 3  │ Charlie │ X (NOT in orders)
│ 4  │ David   │ X (NOT in orders)
└────┴─────────┘         

SEMI JOIN RESULT:
┌────┬────────┐
│ id │ name   │
├────┼────────┤
│ 1  │ Alice  │
│ 2  │ Bob    │
└────┴────────┘

Note: Only user info returned (no order details)
```

**Use Case**: Find existing relationships
```
RESULT: Users with at least one order (Alice, Bob)
        Useful for: "Find active customers", "Find used categories"
```

### Join Comparison Table

| Join Type | Returns From LEFT | Returns From RIGHT | Matching Rows | Unmatched LEFT | Unmatched RIGHT |
|-----------|-------------------|-------------------|---------------|----------------|-----------------|
| INNER JOIN | ✓ | ✓ | ✓ | ✗ | ✗ |
| LEFT JOIN | ✓ | ✓ | ✓ | ✓ (NULLs) | ✗ |
| RIGHT JOIN | ✓ | ✓ | ✓ | ✗ | ✓ (NULLs) |
| FULL OUTER | ✓ | ✓ | ✓ | ✓ (NULLs) | ✓ (NULLs) |
| CROSS JOIN | ✓ | ✓ | All combinations | N/A | N/A |
| ANTI JOIN | ✓ | ✗ | ✗ | ✓ (no match) | - |
| SEMI JOIN | ✓ | ✗ | ✓ (at least 1) | ✗ | - |

---

## Database Indexing

### What is an Index?

An index is a data structure that improves the speed of data retrieval. Think of it like a book's index - instead of reading every page, you look up a topic in the index.

```
WITHOUT INDEX:
┌─────────────────────────────────────────┐
│ users table (1,000,000 rows)            │
│  Scan all rows sequentially              │
│  → Full table scan (SLOW!)                │
│  → Time: O(n) where n = 1,000,000       │
└─────────────────────────────────────────┘

WITH INDEX on email:
┌──────────────────────────────────────┐
│ email_index (B-tree)                 │
│                                      │
│ alice@... → row 5000                 │
│ bob@...   → row 15000                │
│ charlie@...→ row 42000               │
│ ...                                  │
│ → Binary search (FAST!)              │
│ → Time: O(log n)                     │
└──────────────────────────────────────┘
```

### Index Types in PostgreSQL

#### 1. **B-Tree Index (Most Common)**

```sql
-- Create index
CREATE INDEX idx_users_email ON users(email);

-- Best for: Equality (=), range (<, >, <=, >=), pattern matching (LIKE)
SELECT * FROM users WHERE email = 'alice@example.com';  -- FAST
SELECT * FROM users WHERE age > 30;  -- FAST
SELECT * FROM users WHERE name LIKE 'A%';  -- FAST
```

**Structure**:
```
           ┌─────────┐
           │   Root  │
           │    M    │
           └────┬────┘
               / \
              /   \
       ┌─────┘     └─────┐
       │ A-L             │ M-Z
       │ ┌─────┐         │ ┌─────┐
       └→│ Leaf│         └→│ Leaf│
         │A-D  │           │M-P  │
         │...  │           │...  │
         └─────┘           └─────┘
         
Search: Look for "Alice"
1. Start at root (M)
2. A < M, go left
3. Found in left leaf (A-D)
4. Binary search within leaf
5. Time: O(log n)
```

#### 2. **Hash Index**

```sql
CREATE INDEX idx_users_status ON users USING HASH(status);

-- Best for: Exact equality only
SELECT * FROM users WHERE status = 'active';  -- FAST
SELECT * FROM users WHERE status LIKE 'act%'; -- NOT using index
```

**Use**: When you only need exact matches

#### 3. **GiST (Generalized Search Tree)**

```sql
-- For geometric data, full-text search, range data
CREATE INDEX idx_locations_geo ON locations USING GIST(location);

-- Find restaurants near me (within 5km)
SELECT * FROM restaurants 
WHERE location <-> point(40.7128, -74.0060) < 5;
```

#### 4. **GIN (Generalized Inverted Index)**

```sql
-- For array columns and full-text search
CREATE INDEX idx_tags ON articles USING GIN(tags);

-- Find articles with specific tags
SELECT * FROM articles WHERE tags @> ARRAY['database', 'performance'];

-- Full-text search
CREATE INDEX idx_content_fts ON articles USING GIN(to_tsvector('english', content));
SELECT * FROM articles 
WHERE to_tsvector('english', content) @@ to_tsquery('english', 'database');
```

**Use**: Arrays, JSON arrays, full-text search

#### 5. **BRIN (Block Range Index)**

```sql
CREATE INDEX idx_orders_date ON orders USING BRIN(created_at);

-- Good for large tables with columns that correlate with physical order
-- Much smaller than B-tree, good for very large tables
```

**Use**: Very large tables with naturally ordered data (time-series)

### Index Strategies & Best Practices

#### **Composite Index (Multi-column)**

```sql
-- Create index on multiple columns
CREATE INDEX idx_users_country_age ON users(country, age);

-- These queries benefit:
SELECT * FROM users WHERE country = 'US' AND age > 25;     -- GOOD
SELECT * FROM users WHERE country = 'US';                  -- GOOD
SELECT * FROM users WHERE age > 25;                        -- NOT using index!

-- This doesn't benefit (reversed):
SELECT * FROM users WHERE age > 25 AND country = 'US';     -- Still uses index, but order matters for range queries
```

**Column Order Matters**:
```
Index: (country, age)

Query: WHERE country = 'US' AND age > 25
       1. Find all 'US' entries
       2. Within US, find age > 25
       EFFICIENT ✓

Query: WHERE age > 25 AND country = 'US'
       Database reorders to match index
       STILL EFFICIENT ✓

Query: WHERE age > 25  (no country filter)
       Can't use index efficiently (needs full scan of age values)
       NOT EFFICIENT ✗
```

#### **Covering Index (Include clause)**

```sql
-- Include extra columns so query doesn't need table access
CREATE INDEX idx_users_email_cover 
  ON users(email) 
  INCLUDE (name, age);

-- Index covers the query (no table lookup needed)
SELECT name, age FROM users WHERE email = 'alice@example.com';  -- VERY FAST
-- The index has all needed columns, table doesn't need to be accessed!
```

#### **Partial Index (WHERE clause)**

```sql
-- Index only active users (saves space, faster inserts)
CREATE INDEX idx_active_users ON users(email) 
  WHERE status = 'active';

-- Benefit: Smaller index, only relevant data
SELECT * FROM users WHERE email = 'alice@example.com' AND status = 'active';
```

#### **Index on Expressions**

```sql
-- Index on computed values
CREATE INDEX idx_users_lower_email ON users(LOWER(email));

-- Case-insensitive search becomes fast
SELECT * FROM users WHERE LOWER(email) = 'alice@example.com';
```

### Index Performance Monitoring

```sql
-- See index sizes
SELECT schemaname, tablename, indexname, pg_size_pretty(pg_relation_size(indexrelid)) as size
FROM pg_indexes
ORDER BY pg_relation_size(indexrelid) DESC;

-- Find unused indexes
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
ORDER BY idx_scan ASC;

-- Check index bloat
SELECT current_database(), schemaname, tablename, indexname, 
       ROUND(100 * (CASE WHEN otta > 0 THEN sml.relpages::float/otta 
             ELSE 0.0 END)::numeric, 2) AS ratio
FROM pg_class
WHERE reltype = 0;

-- Analyze query plan
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'alice@example.com';

-- Example output:
-- Index Scan using idx_users_email on users  (cost=0.29..8.30 rows=1)
--   Index Cond: (email = 'alice@example.com')
```

### When NOT to Use Indexes

```
✗ Small tables (< 10,000 rows)
  → Table scan is often faster than index lookup

✗ Columns with very low selectivity (few distinct values)
  → SELECT * FROM users WHERE gender = 'M'
  → Might return 50% of table, full scan faster

✗ Columns that are frequently updated
  → Index maintenance overhead > query benefit

✗ Columns with NULL values
  → Some indexes ignore NULLs, query can't use index for NULL checks

✗ Very wide tables (many columns)
  → Index size becomes large, cache inefficiency
```

---

## Query Optimization

### Query Execution Plans

```sql
-- View execution plan WITHOUT running query
EXPLAIN SELECT * FROM users WHERE age > 25;

-- Output:
-- Seq Scan on users  (cost=0.00..35.50 rows=500)
--   Filter: (age > 25)
-- Cost estimate: 0.00..35.50 (startup..total)
-- Estimated rows: 500

-- View actual execution WITH statistics
EXPLAIN ANALYZE SELECT * FROM users WHERE age > 25;

-- Output includes:
-- Seq Scan on users  (cost=0.00..35.50 rows=500) (actual time=0.025..2.50 rows=487)
-- Shows: Estimated vs Actual (good for finding bad estimates)
```

### Common Performance Issues

#### **1. N+1 Query Problem (Java developer's pain!)**

```
❌ BAD (N+1 queries):
// In Java code
List<User> users = userRepository.findAll();  // 1 query: SELECT * FROM users
for (User user : users) {
    List<Order> orders = user.getOrders();    // N queries: SELECT * FROM orders WHERE user_id = ?
}
// Total: 1 + N queries (if 1000 users, 1001 queries!)

✓ GOOD (Single query with JOIN):
// In SQL
SELECT u.*, o.* FROM users u
LEFT JOIN orders o ON u.id = o.user_id;  // 1 query gets all data

// In Java (using Hibernate):
@Entity
public class User {
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;  // Eagerly load orders
}
```

#### **2. Selecting Unnecessary Columns**

```
❌ BAD:
SELECT * FROM large_users_table WHERE id = 1;
// 50 columns, most unused, wastes memory and I/O

✓ GOOD:
SELECT id, name, email FROM users WHERE id = 1;
// Only needed columns
```

#### **3. Missing Indexes on WHERE Clause**

```
❌ BAD (full table scan):
SELECT * FROM orders WHERE customer_email = 'alice@example.com';
-- Scans 1,000,000 rows to find maybe 10

✓ GOOD (create index):
CREATE INDEX idx_orders_email ON orders(customer_email);
SELECT * FROM orders WHERE customer_email = 'alice@example.com';
-- Index finds result in O(log n) time
```

#### **4. Expensive Operations in WHERE Clause**

```
❌ BAD (index can't be used):
SELECT * FROM users WHERE YEAR(created_at) = 2024;
-- Index on created_at can't be used (function applied)

✓ GOOD:
SELECT * FROM users 
WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01';
-- Index on created_at can be used!
```

#### **5. Not Using LIMIT**

```
❌ BAD (returns 1,000,000 rows):
SELECT * FROM log_entries;
-- Network overhead, memory explosion

✓ GOOD:
SELECT * FROM log_entries ORDER BY created_at DESC LIMIT 100;
-- Only 100 rows returned
```

### Optimization Checklist

```sql
-- 1. Add indexes on columns used in WHERE, JOIN, ORDER BY
CREATE INDEX idx_users_country ON users(country);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_created ON orders(created_at);

-- 2. Use EXPLAIN ANALYZE to verify plan
EXPLAIN ANALYZE SELECT * FROM users WHERE country = 'US';

-- 3. Check for seq scans on large tables
-- Output should show "Index Scan" not "Seq Scan" for WHERE clauses

-- 4. Use LIMIT for browsing data
SELECT * FROM events LIMIT 50;

-- 5. Aggregate at database, not in application
SELECT department, COUNT(*) as count, AVG(salary) as avg_salary
FROM employees
GROUP BY department;  -- In DB
-- NOT: fetch all employees in Java, aggregate in memory

-- 6. Use materialized views for complex aggregations
CREATE MATERIALIZED VIEW user_order_summary AS
SELECT u.id, u.name, COUNT(o.id) as order_count, SUM(o.total) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

SELECT * FROM user_order_summary WHERE order_count > 10;

-- 7. Batch operations
-- ❌ BAD: Multiple round trips
INSERT INTO users VALUES (...);
INSERT INTO users VALUES (...);
INSERT INTO users VALUES (...);

-- ✓ GOOD: Single batch
INSERT INTO users VALUES (...), (...), (...);
```

---

## Database Sharding

### What is Sharding?

Sharding is horizontal partitioning of data across multiple database servers. Instead of storing all data in one database, you split it across multiple databases.

```
BEFORE SHARDING (Single Database):
┌────────────────────────────────────┐
│    PostgreSQL Server               │
│  ┌──────────────────────────────┐  │
│  │ Users Table (100M rows)      │  │
│  │ Orders Table (500M rows)     │  │
│  │ Products Table (1M rows)     │  │
│  └──────────────────────────────┘  │
│                                    │
│  Bottlenecks:                      │
│  - Single server handles all ops   │
│  - Limited by server resources     │
│  - Slow queries on huge tables     │
└────────────────────────────────────┘

AFTER SHARDING (Multiple Databases):
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│ Shard 1        │  │ Shard 2        │  │ Shard 3        │
│ PostgreSQL     │  │ PostgreSQL     │  │ PostgreSQL     │
├────────────────┤  ├────────────────┤  ├────────────────┤
│ User IDs:      │  │ User IDs:      │  │ User IDs:      │
│ 1-33M          │  │ 33M-67M        │  │ 67M-100M       │
│                │  │                │  │                │
│ Order IDs:     │  │ Order IDs:     │  │ Order IDs:     │
│ 1-167M         │  │ 167M-333M      │  │ 333M-500M      │
│                │  │                │  │                │
│ Load:          │  │ Load:          │  │ Load:          │
│ 33% Users      │  │ 33% Users      │  │ 33% Users      │
│ 33% Orders     │  │ 33% Orders     │  │ 33% Orders     │
└────────────────┘  └────────────────┘  └────────────────┘

Benefits:
- Load distributed across 3 servers
- Each server handles 1/3 of data
- Queries faster on smaller datasets
- Horizontal scaling (add more shards)
```

### Sharding Strategies

#### **1. Range-Based Sharding**

```sql
-- Shard by user ID ranges
IF user_id BETWEEN 1 AND 33M → Shard 1
IF user_id BETWEEN 33M AND 67M → Shard 2
IF user_id BETWEEN 67M AND 100M → Shard 3

QUERY ROUTING (in Java code):
public Shard getShardForUser(long userId) {
    if (userId <= 33_000_000) return shard1;
    if (userId <= 67_000_000) return shard2;
    return shard3;
}

// Usage
User user = userService.getUser(userId);  // Automatically routes to correct shard

PROBLEM: Uneven distribution
- If new users mostly have IDs > 67M, Shard 3 becomes hot (overloaded)
```

#### **2. Hash-Based Sharding (RECOMMENDED)**

```
HASH FUNCTION: shard_id = hash(user_id) % number_of_shards

For 3 shards:
user_id = 1    → hash(1) = 42       → 42 % 3 = 0   → Shard 1
user_id = 2    → hash(2) = 15       → 15 % 3 = 0   → Shard 1
user_id = 3    → hash(3) = 78       → 78 % 3 = 0   → Shard 1
user_id = 4    → hash(4) = 91       → 91 % 3 = 1   → Shard 2
user_id = 5    → hash(5) = 34       → 34 % 3 = 1   → Shard 2
...

Java Implementation:
public Shard getShardForUser(long userId) {
    int shardId = Math.abs((int)userId.hashCode()) % NUM_SHARDS;
    return shards[shardId];
}

ADVANTAGES:
✓ Even distribution (hash function spreads uniformly)
✓ Easy to calculate which shard without lookup table
✓ Works well for growing data
```

#### **3. Directory-Based Sharding**

```
┌─────────────────────────────────┐
│ Lookup Service / Directory      │
├─────────────────────────────────┤
│ user_id → shard_id mapping      │
│                                 │
│ 1-1000       → Shard 1          │
│ 1001-2000    → Shard 1          │
│ 2001-3500    → Shard 2          │
│ 3501-5000    → Shard 3          │
│ ...                             │
└─────────────────────────────────┘

QUERY ROUTING:
1. Look up user_id in directory
2. Find which shard to use
3. Query that shard
4. Return result

ADVANTAGES:
✓ Can rebalance without changing hash function
✓ Can do complex sharding logic
✓ Easy to migrate data

DISADVANTAGES:
✗ Extra lookup overhead
✗ Directory can become bottleneck
✗ Must maintain consistency
```

### Sharding in Practice (Java)

```java
// Sharding Service Example
public class ShardingService {
    private List<PostgresqlDataSource> shards;
    private final int NUM_SHARDS = 3;
    
    // Hash-based routing
    public long getShardId(long userId) {
        return Math.abs(userId % NUM_SHARDS);
    }
    
    // Get connection to correct shard
    public DataSource getShardDataSource(long userId) {
        long shardId = getShardId(userId);
        return shards.get((int)shardId);
    }
    
    // Query example
    public User getUser(long userId) {
        DataSource shard = getShardDataSource(userId);
        
        try (Connection conn = shard.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultToUser(rs);
            }
        }
        return null;
    }
    
    // Cross-shard query (fan-out)
    public List<Order> getAllOrdersForUser(long userId) {
        List<Order> allOrders = new ArrayList<>();
        
        // Query all shards (if order is sharded separately)
        for (DataSource shard : shards) {
            try (Connection conn = shard.getConnection()) {
                String sql = "SELECT * FROM orders WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setLong(1, userId);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    allOrders.add(mapResultToOrder(rs));
                }
            }
        }
        return allOrders;
    }
    
    // Batch insert across shards
    public void insertUsers(List<User> users) {
        Map<Long, List<User>> shardedUsers = new HashMap<>();
        
        // Group by shard
        for (User user : users) {
            long shardId = getShardId(user.getId());
            shardedUsers.computeIfAbsent(shardId, k -> new ArrayList<>())
                       .add(user);
        }
        
        // Insert into each shard
        for (Map.Entry<Long, List<User>> entry : shardedUsers.entrySet()) {
            DataSource shard = shards.get(entry.getKey().intValue());
            insertUsersBatch(shard, entry.getValue());
        }
    }
}
```

### Challenges with Sharding

```
CHALLENGE 1: Joins Across Shards
❌ HARD:
SELECT u.name, COUNT(o.id) 
FROM users u
JOIN orders o ON u.id = o.user_id
WHERE u.country = 'US'
GROUP BY u.name

If users sharded by user_id but orders sharded by order_id,
you need to query multiple shards and join in application

✓ SOLUTION:
- Co-shard: Shard both users and orders by user_id
- Denormalize: Store user info in orders table
- Use separate analytics database

CHALLENGE 2: Hot Spots
Problem: If user_id 12345 has 1 million orders,
and all queries for orders go to same shard, it becomes overloaded

✓ SOLUTION:
- Add secondary sharding (hot_user_id_orders shard)
- Distribute hot data specially
- Use read replicas

CHALLENGE 3: Reshard (Add More Shards)
Problem: Need to split Shard 1 into Shard 3 and Shard 4

hash(user_id) % 3 → Shard 1, 2, or 3
hash(user_id) % 4 → Different mapping!

Old sharding breaks, need to migrate data

✓ SOLUTION:
- Use consistent hashing
- Use directory-based sharding
- Plan for resharding upfront
```

---

## PGBouncer Connection Pooling

### Why Connection Pooling?

```
WITHOUT POOLING (Expensive):
Java App                PostgreSQL
   |                       |
   |--create connection--→  | (TCP handshake, auth, setup)
   |  Query 1              |
   |--close connection---→  | (cleanup, close)
   |--create connection--→  | (TCP handshake, auth, setup)  
   |  Query 2              |
   |--close connection---→  | (cleanup)
   
Each query: ~100ms overhead just for connection setup!

WITH POOLING (Efficient):
Java App              PGBouncer              PostgreSQL
   |                    |                         |
   |--query 1--------→  |--use reused conn.---→  |
   |  (< 1ms)           |  (already open)         |
   |--query 2--------→  |--same connection-----→ |
   |  (< 1ms)           |  (already open)         |
   |--query 3--------→  |--same connection-----→ |
   |  (< 1ms)           |                         |

Multiple apps reuse same connections!
```

### PGBouncer Architecture

```
┌──────────────────────────────────────────┐
│ Client Applications                      │
│ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐     │
│ │Java1 │ │Java2 │ │Java3 │ │Java4 │     │
│ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘     │
└────┼─────────┼────────┼────────┼────────┘
     │         │        │        │
     └─────────┼────────┼────────┘
               │        │
        ┌──────▼────────▼─────────────┐
        │    PGBouncer (Pooler)       │
        ├─────────────────────────────┤
        │ Connection Pool             │
        │ ┌─────┐ ┌─────┐ ┌─────┐    │
        │ │Conn1│ │Conn2│ │Conn3│    │
        │ └──┬──┘ └──┬──┘ └──┬──┘    │
        │    │       │       │        │
        └────┼───────┼───────┼────────┘
             │       │       │
        ┌────▼───────▼───────▼──────┐
        │  PostgreSQL Server        │
        │  (only 3 connections!)    │
        └───────────────────────────┘
        
Benefits:
- 4 Java apps share 3 connections
- Database sees only 3 connections
- Apps still get fast responses
- Reduced DB resource usage
```

### PGBouncer Configuration

```ini
; /etc/pgbouncer/pgbouncer.ini

[databases]
# Database definitions
myapp_db = host=localhost port=5432 dbname=myapp

[pgbouncer]
# Listening address and port
listen_addr = 0.0.0.0
listen_port = 6432

# Pool mode
pool_mode = transaction  # One of: session, transaction, statement

# Connection pool size
max_client_conn = 1000       # Max connections from clients
default_pool_size = 25       # Connections to keep open to database
min_pool_size = 10           # Minimum connections
reserve_pool_size = 5        # Reserve connections for priority users

# Timeouts
server_lifetime = 3600       # Close connection after 1 hour
server_idle_in_transaction_session_timeout = 60  # Kill idle in transaction
query_timeout = 0            # Query timeout (0 = disabled)
client_idle_timeout = 600    # Close idle client connection

# Admin user
admin_users = admin
stats_users = admin

# Logging
log_connections = 1
log_disconnections = 1
logfile = /var/log/pgbouncer/pgbouncer.log
```

### Pool Modes Explained

#### **Session Mode (Sticky Connections)**

```
Client connects → PGBouncer allocates PostgreSQL connection
                  Connection stays allocated until client disconnects
                  
USE CASE: Web applications, each HTTP request gets different pooler thread
PROS: Supports all PostgreSQL features, can use prepared statements
CONS: Wastes connections, connections sit idle

Example:
┌─────────┐         ┌─────────┐         ┌─────────┐
│ Client1 │────────→│ PGConn1 │────────→│PostgreSQL
└─────────┘         └─────────┘         └─────────┘
                    (allocated)
                    
If Client1 is idle, PGConn1 still allocated!
```

#### **Transaction Mode (DEFAULT RECOMMENDED)**

```
Client sends query → PGBouncer allocates connection
                     Query executes
                     COMMIT/ROLLBACK received
                     Connection returned to pool
                     
USE CASE: Most applications
PROS: Efficient, connections reused quickly
CONS: Can't use cursors, some Postgres features limited

Example:
Client1: Query    → PGConn1 → Execute → Commit   → Released
Client2: Query    → PGConn1 → Execute → Commit   → Released
         (same connection reused!)

Multiple clients share fewer connections
```

#### **Statement Mode (Most Efficient)**

```
Client sends statement → PGBouncer allocates connection
                         Statement executed
                         Connection returned to pool
                         (No COMMIT needed)
                         
USE CASE: Microservices, auto-commit applications
PROS: Maximum connection reuse
CONS: No transaction support, very limited

Example:
Client1: Query1   → PGConn1 → Execute → Released
Client2: Query2   → PGConn1 → Execute → Released
Client1: Query3   → PGConn2 → Execute → Released (reused different connection)
         
Each statement gets its own connection briefly
```

### PGBouncer in Java Applications

```java
// HikariCP with PGBouncer (Recommended)
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:6432/myapp");
config.setUsername("user");
config.setPassword("password");

// These settings work well with PGBouncer
config.setMaximumPoolSize(20);           // Local app pool (smaller)
config.setMinimumIdle(5);                // Min idle connections
config.setConnectionTimeout(10000);      // 10 second timeout
config.setIdleTimeout(300000);           // 5 min idle timeout
config.setAutoCommit(false);             // Important for transaction mode!

HikariDataSource dataSource = new HikariDataSource(config);

// Usage
try (Connection conn = dataSource.getConnection()) {
    // Query
    // PGBouncer ensures efficient connection reuse
}

// PGBouncer Statistics (in psql)
// psql -h localhost -U pgbouncer -d pgbouncer
// SHOW STATS;  -- Connection statistics
// SHOW CLIENTS; -- Connected clients
```

### Monitoring PGBouncer

```sql
-- Connect to PGBouncer admin (port 6432)
psql -h localhost -U admin -d pgbouncer

-- Show statistics
SHOW STATS;
-- Output:
--  database | total_xact_count | total_query_count | ...
--  myapp    | 1000000          | 5000000           | ...

-- Show active connections
SHOW CLIENTS;
-- Shows all connected clients

-- Show pool status
SHOW POOLS;
-- database | user | cl_active | cl_waiting | sv_active | sv_idle | sv_used
-- myapp    | app  | 10        | 0          | 8         | 17      | 0

-- Configuration check
SHOW CONFIG;

-- Reload configuration
RELOAD;

-- Pause/resume
PAUSE;
RESUME;
```

### Performance Tuning

```
Scenario: Too many PostgreSQL connections

Problem:
default_pool_size = 100  (per database, per user)
Number of users = 10
Number of databases = 5
Total: 100 * 10 * 5 = 5000 connections! → PostgreSQL max_connections = 1000 → FAILURE!

Solution:
default_pool_size = 25   (per database, per user)
reserve_pool_size = 5    (emergency reserve)
Total: 25 * 10 * 5 + 5 = 1255 → Still over limit

Better solution:
- Reduce pool size to 15
- Consolidate databases
- Use statement mode for read-only queries
- Scale horizontally with multiple PGBouncer instances
```

---

## Java Developer Perspective

### Connection Management Anti-Patterns (Learned the Hard Way)

#### **Anti-Pattern 1: Not Using Connection Pooling**

```java
❌ BAD (Creates new connection each time):
public User getUser(long userId) {
    try {
        Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/myapp",
            "user",
            "password"
        );
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setLong(1, userId);
        // ... execute query ...
        conn.close();  // Connection created and destroyed each time!
    } catch (SQLException e) {
        // ...
    }
}

Issues:
- Thousands of TCP connections to database
- High latency (handshake overhead)
- Database connection limit exceeded
- Server runs out of resources

✓ GOOD (Use HikariCP):
private HikariDataSource dataSource;

public User getUser(long userId) {
    try (Connection conn = dataSource.getConnection()) {
        // Connection reused from pool
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setLong(1, userId);
        // ...
    } catch (SQLException e) {
        // ...
    }
}
```

#### **Anti-Pattern 2: Forgetting to Close Connections**

```java
❌ BAD (Resource leak):
public List<User> getAllUsers() {
    try {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet rs = stmt.executeQuery();
        
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getLong("id"), rs.getString("name")));
        }
        
        return users;  // Connection not closed! Leaked!
    } catch (SQLException e) {
        // ...
    }
}

Result: Connection pool exhausted after few queries

✓ GOOD (Try-with-resources):
public List<User> getAllUsers() {
    try (
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet rs = stmt.executeQuery()
    ) {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getLong("id"), rs.getString("name")));
        }
        return users;
        
    } catch (SQLException e) {
        // Automatically closes all resources
    }
}
```

#### **Anti-Pattern 3: Holding Connections in Transactions**

```java
❌ BAD (Long-held connection):
@Transactional
public void processOrder(Order order) {
    // Transaction starts, connection allocated
    
    // Update order status
    orderRepository.save(order);
    
    // Call slow external API (takes 30 seconds!)
    ExternalAPI.validateOrder(order);
    
    // Send email
    emailService.send(order.getEmail());
    
    // Transaction ends, connection released
    // But held for 30+ seconds while doing non-DB work!
}

Results:
- Connection pool exhausted
- Other requests waiting for connections
- Database connections idle

✓ GOOD (Transaction only for database):
public void processOrder(Order order) {
    // Start transaction, quick database operation
    updateOrderStatus(order);  // In transaction
    
    // Non-database work OUTSIDE transaction
    ExternalAPI.validateOrder(order);  // Connection released
    emailService.send(order.getEmail());  // Connection released
}

@Transactional
private void updateOrderStatus(Order order) {
    orderRepository.save(order);
}  // Transaction and connection released immediately
```

#### **Anti-Pattern 4: N+1 Queries**

```java
❌ BAD (N+1 queries):
@Transactional
public List<UserDTO> getAllUsersWithOrders() {
    List<User> users = userRepository.findAll();  // Query 1
    
    for (User user : users) {
        List<Order> orders = orderRepository.findByUserId(user.getId());  // Query N
        user.setOrders(orders);
    }
    
    return users.stream().map(this::toDTO).collect(toList());
}

// If 1000 users: 1 + 1000 = 1001 queries!

✓ GOOD (Single query with JOIN):
public List<UserDTO> getAllUsersWithOrders() {
    // Single query gets all data
    List<Object[]> data = entityManager.createQuery(
        "SELECT u, o FROM User u LEFT JOIN FETCH u.orders o"
    ).getResultList();
    
    // Group results and convert to DTO
    // 1 query instead of 1001!
}

// Or using Spring Data:
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();
```

#### **Anti-Pattern 5: Pagination Issues**

```java
❌ BAD (Fetch all, then paginate in memory):
public List<User> getUsersPage(int page, int size) {
    // Fetches ALL 1,000,000 users!
    List<User> allUsers = userRepository.findAll();
    
    // Paginate in memory (wasteful)
    return allUsers.stream()
        .skip((long) page * size)
        .limit(size)
        .collect(toList());
}

Results: Memory explosion, slow, timeout

✓ GOOD (Paginate in database):
public Page<User> getUsersPage(int page, int size) {
    // Database returns only requested page (e.g., 50 rows)
    return userRepository.findAll(PageRequest.of(page, size));
}

// SQL generated:
// SELECT * FROM users LIMIT 50 OFFSET 2000;
// Only fetches needed rows!
```

### Spring Data JPA Best Practices

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_country_age", columnList = "country,age")
})
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    // Lazy load by default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 1. Use projections to select only needed columns
    @Query("SELECT new map(u.id as id, u.name as name, u.email as email) " +
           "FROM User u WHERE u.id = ?1")
    Map<String, Object> findUserProjection(Long id);
    
    // 2. Fetch with JOIN to avoid N+1
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = ?1")
    User findByIdWithOrders(Long id);
    
    // 3. Use pagination
    Page<User> findAll(Pageable pageable);
    Page<User> findByCountry(Country country, Pageable pageable);
    
    // 4. Use @Query with LIMIT for top results
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC LIMIT 10")
    List<User> findRecentUsers();
    
    // 5. Batch insert
    @Modifying(clearAutomatically = true)
    @Query("INSERT INTO users (name, email, created_at) VALUES (?1, ?2, NOW())")
    int batchInsertUsers(String name, String email);
}

// Usage:
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public void processUsers() {
        // ✓ GOOD: Paginate to avoid memory issues
        Pageable pageable = PageRequest.of(0, 100);
        Page<User> page;
        int pageNum = 0;
        
        do {
            pageable = PageRequest.of(pageNum++, 100);
            page = userRepository.findAll(pageable);
            
            // Process batch
            page.getContent().forEach(this::processUser);
            
        } while (page.hasNext());
    }
}
```

### Debugging Database Issues in Java

```java
// Enable SQL logging
// application.yml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

// Result in logs:
// select user0_.id as id1_4_, user0_.email as email2_4_, ...
// from users user0_ where user0_.id=?
// binding parameter [1] as [BIGINT] - [123]

// Connection pool monitoring
@Component
public class ConnectionPoolMonitor {
    @Autowired
    private DataSource dataSource;
    
    @Scheduled(fixedDelay = 10000)  // Every 10 seconds
    public void printPoolStats() {
        HikariDataSource hds = (HikariDataSource) dataSource;
        System.out.println("Active: " + hds.getHikariPoolMXBean().getActiveConnections());
        System.out.println("Idle: " + hds.getHikariPoolMXBean().getIdleConnections());
        System.out.println("Total: " + hds.getHikariPoolMXBean().getTotalConnections());
        System.out.println("Pending: " + hds.getHikariPoolMXBean().getThreadsAwaitingConnection());
    }
}

// Slow query logging (PostgreSQL)
SET log_min_duration_statement = 1000;  -- Log queries over 1 second
SHOW log_statement;  -- View current settings

// Java: Enable slow query logging
// hikari.max-lifetime: 10 minutes
// hikari.connection-timeout: 10 seconds
// If a query takes > 10 seconds, HikariCP logs a warning
```

---

## Interview Questions & Answers

### Basic Questions

#### **Q1: What are the main differences between INNER JOIN and LEFT JOIN?**

**Answer**:

| Aspect | INNER JOIN | LEFT JOIN |
|--------|-----------|-----------|
| Matching Rows | ✓ Returns | ✓ Returns |
| Unmatched LEFT | ✗ Excludes | ✓ Shows with NULL |
| Unmatched RIGHT | ✗ Excludes | ✗ Excludes |
| Row Count | Smaller | Same or larger |

**Example**:
```
Users: Alice (id=1), Bob (id=2), Charlie (id=3)
Orders: Order1 (user_id=1), Order2 (user_id=1), Order3 (user_id=2), Order4 (user_id=5)

INNER JOIN: Returns only users with orders → Alice, Bob (2 users)
LEFT JOIN: Returns all users → Alice (2 orders), Bob (1 order), Charlie (NULL)
```

---

#### **Q2: When would you use denormalization in a database?**

**Answer**:

Denormalization means storing redundant data to improve query performance at the cost of data consistency.

**When to Use**:
```
1. Complex Joins on Large Tables
   ❌ Before:
   SELECT u.name, COUNT(o.id), SUM(o.total)
   FROM users u
   JOIN orders o ON u.id = o.user_id
   WHERE u.country = 'US'
   GROUP BY u.id, u.name
   → Slow for 1M users and 100M orders
   
   ✓ After (denormalized):
   users_stats table with pre-calculated:
   - user_id, name, order_count, total_spent
   → Fast aggregation queries

2. Read-Heavy Applications
   - E-commerce: Product cache in cart table
   - Social Media: Follower count in user table
   
3. Mobile Apps (Network Cost)
   - Store needed fields in denormalized table
   - Single query instead of multiple JOINs
```

**Cost**:
```
✗ Data Consistency Complexity
  - Update user's country: Must update 1 row
  - Update user's country in 1M order rows? Complex!
  
✗ Increased Storage
  - Duplication of data increases database size
  
✗ Update Complexity
  - Trigger logic to keep denormalized data in sync
  - Risk of inconsistency if triggers fail

✓ Solution: Use materialized views
CREATE MATERIALIZED VIEW user_order_stats AS
SELECT u.id, u.name, COUNT(o.id) as order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

REFRESH MATERIALIZED VIEW user_order_stats;  -- Update periodically
```

---

#### **Q3: Explain database indexes. When should you NOT create an index?**

**Answer**:

**What**: Index is a data structure (usually B-tree) for fast data retrieval.

**When to Create**:
```
✓ WHERE clause columns
  CREATE INDEX idx_users_email ON users(email);
  SELECT * FROM users WHERE email = '...';  → O(log n) instead of O(n)

✓ JOIN columns
  CREATE INDEX idx_orders_user_id ON orders(user_id);
  Speeds up JOIN matching

✓ ORDER BY / GROUP BY
  CREATE INDEX idx_orders_date ON orders(created_at);
  SELECT * FROM orders ORDER BY created_at;  → Uses index for sorting

✓ Foreign keys
  Automatically created for referential integrity
```

**When NOT to Create**:
```
✗ Small tables (< 10k rows)
  Table scan is faster than index lookup
  
✗ Very high cardinality on low selectivity
  SELECT * FROM users WHERE gender = 'M';  → Returns 50% of table
  Index might be slower than table scan
  
✗ Columns frequently updated
  Index update overhead > query benefit
  
✗ Columns with many NULLs
  Some indexes don't store NULLs
  
✗ Low selectivity columns
  Few distinct values, index not useful
```

---

#### **Q4: What is the N+1 query problem and how do you solve it?**

**Answer**:

**Problem**:
```
List<User> users = userRepository.findAll();  // Query 1
for (User user : users) {
    List<Order> orders = orderRepository.findByUserId(user.getId());  // Query N
}

For 1000 users: 1 + 1000 = 1001 queries!
Time: ~50ms per query = 50 seconds total
```

**Solutions**:

```
1. Eager Loading with JOIN FETCH
   @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
   List<User> findAllWithOrders();
   → 1 query, returns all data
   
2. Batch Loading
   SELECT * FROM orders WHERE user_id IN (?, ?, ?, ...)
   → Fewer queries, fewer round trips
   
3. Pagination
   SELECT * FROM orders WHERE user_id IN (...) LIMIT 100
   → Smaller result sets
   
4. Caching
   Store user's orders in cache
   → Avoid repeated database calls
   
5. GraphQL with DataLoader (if using GraphQL)
   Batches queries automatically
```

---

#### **Q5: Explain ACID properties in transactions.**

**Answer**:

**ACID = Atomicity, Consistency, Isolation, Durability**

```
1. ATOMICITY (All or Nothing)
   Transaction is "all or nothing" - either commits fully or rolls back
   
   Example:
   BEGIN;
     UPDATE accounts SET balance = balance - 100 WHERE id = 1;  -- Debit
     UPDATE accounts SET balance = balance + 100 WHERE id = 2;  -- Credit
   COMMIT;
   
   If any step fails, ENTIRE transaction rolls back
   ✓ Both updates succeed OR neither happens
   ✗ Cannot have debit without credit
   
2. CONSISTENCY (Data Integrity)
   Database moves from one consistent state to another
   
   Example: Sum of all accounts must remain the same
   
   Before: Account 1: $100, Account 2: $200 (Total: $300)
   Transfer $50 from Account 1 to Account 2
   After: Account 1: $50, Account 2: $250 (Total: $300)
   
   ✓ Constraints enforced, invariants maintained
   
3. ISOLATION (Transactions Don't Interfere)
   Concurrent transactions don't see partially committed data
   
   Transaction 1:
     UPDATE user SET balance = 500 WHERE id = 1;
   
   Transaction 2:
     SELECT balance FROM user WHERE id = 1;
     -- Sees either old value ($1000) or new value ($500)
     -- NOT intermediate uncommitted value
   
   Isolation Levels:
   - READ UNCOMMITTED: Can read dirty data (bad)
   - READ COMMITTED: Can't read uncommitted changes (good)
   - REPEATABLE READ: Phantom reads possible (better)
   - SERIALIZABLE: No interference at all (best, slowest)
   
4. DURABILITY (Permanent After Commit)
   Once COMMIT, data survives crashes, power failures, etc.
   
   // Write to WAL (Write-Ahead Log) on disk
   BEGIN;
     INSERT INTO orders VALUES (...);
   COMMIT;  // ✓ Written to disk, cannot be lost
   
   Even if server crashes next second, data is there
```

---

### Intermediate Questions

#### **Q6: How would you implement sharding in a system with 100 million users?**

**Answer**:

```
APPROACH 1: Range-Based Sharding
Shard 1: user_id 1-33M
Shard 2: user_id 33M-67M
Shard 3: user_id 67M-100M

PROBLEM: Hot spot if new users have high IDs
All queries for user_id > 67M → Shard 3 overloaded

APPROACH 2: Hash-Based Sharding (BETTER)
shard_id = hash(user_id) % 3

Hash distributes uniformly across shards

IMPLEMENTATION IN JAVA:
public class ShardingManager {
    private PostgresDatabase[] shards;
    private final int NUM_SHARDS = 3;
    
    public DatabaseConnection getConnection(long userId) {
        int shardId = Math.abs((int) userId.hashCode()) % NUM_SHARDS;
        return shards[shardId].connect();
    }
    
    // Insert user
    public void createUser(User user) {
        DatabaseConnection conn = getConnection(user.getId());
        conn.execute("INSERT INTO users VALUES (...)");
    }
}

CHALLENGES:
1. Cross-Shard Joins (Hard)
   Problem: Orders and Users on different shards
   Solution: Co-shard by user_id (orders also sharded by user_id)
   
2. Aggregations Across Shards (Hard)
   Problem: Get total orders across all shards
   Solution: Query all shards, aggregate in application
   
3. Resharding (Hard)
   Problem: Need to scale from 3 to 5 shards
   Solution: Consistent hashing or directory-based routing
   
4. Transactions Across Shards (Hard)
   Problem: User on Shard 1, Order on Shard 2, need atomic transaction
   Solution: Distributed transactions (2PC) - complex, risky
           OR: Accept eventual consistency
```

---

#### **Q7: Explain connection pooling and why it's important.**

**Answer**:

```
WITHOUT CONNECTION POOL:
Each query requires:
1. TCP connection setup (~5ms)
2. Database authentication (~5ms)
3. Query execution (~10ms)
4. TCP connection teardown (~5ms)
Total: ~25ms overhead per query!

WITH CONNECTION POOL:
Connection created once, reused for multiple queries
1. Get connection from pool (< 1ms, already open)
2. Query execution (~10ms)
3. Return connection to pool (< 1ms)
Total: ~11ms per query!

IMPROVEMENT: 60% faster!

POOL CONFIGURATION (HikariCP):
maximumPoolSize = 20         // Max concurrent connections
minimumIdle = 5              // Keep 5 open when idle
idleTimeout = 600000         // Recycle after 10 minutes
leakDetectionThreshold = 15000  // Log leaks > 15s

For 10,000 concurrent requests:
✗ No pool: 10,000 connections to database (crashes!)
✓ Pool: 20 connections shared by all 10,000 requests

Pool Size Calculation:
- If avg query = 10ms
- Max throughput = 1000ms / 10ms = 100 queries/sec
- Need at least 100 / CPU_cores connections

Typical formula:
connections = ((core_count * 2) + effective_spindle_count)

For 8 cores: (8 * 2) + 1 = 17 connections
```

---

#### **Q8: What is a query execution plan and how do you optimize based on it?**

**Answer**:

```
EXPLAIN output example:
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'alice@example.com';

Result:
Seq Scan on users (cost=0.00..35.50 rows=500)
  Filter: (email = 'alice@example.com')
  
Analysis:
- "Seq Scan" = Sequential scan (BAD, full table scan)
- "cost=0.00..35.50" = Estimated cost units
- "rows=500" = Estimated rows returned

OPTIMIZATION: Add index
CREATE INDEX idx_users_email ON users(email);

After index:
Index Scan using idx_users_email on users
  (cost=0.29..8.30 rows=1)
  Index Cond: (email = 'alice@example.com')

Improvement:
- "Index Scan" = Used index (GOOD)
- Cost reduced from 35.50 to 8.30
- Rows correct (1)
- Time: ~25x faster

OTHER OPTIMIZATIONS:

1. Bad selectivity (should avoid index)
   SELECT * FROM users WHERE gender = 'M';
   EXPLAIN shows: Seq Scan (right!)
   Adding index would slow things down
   
2. Missing index on JOIN
   SELECT u.name, o.total
   FROM users u
   JOIN orders o ON u.id = o.user_id
   
   EXPLAIN shows: Hash Join with Seq Scan on orders
   
   Problem: No index on orders.user_id
   Solution: CREATE INDEX idx_orders_user_id ON orders(user_id);
   Now uses Nested Loop Join with Index Scan (faster)
   
3. Incorrect JOIN order
   EXPLAIN shows: Seq Scan on large_table, then loop join with small_table
   
   Problem: Should join small table first
   Solution: Reorder tables in JOIN, or use explicit hints
```

---

#### **Q9: How do you handle distributed transactions across shards?**

**Answer**:

```
PROBLEM: User on Shard 1 wants to transfer money to User on Shard 2

❌ SOLUTION 1: Two-Phase Commit (2PC)
1. PREPARE: Ask both shards if they can commit
   - Shard 1: Lock user account, prepare debit
   - Shard 2: Lock user account, prepare credit
   
2. COMMIT: If both respond OK, commit on both
   - Shard 1: Debit $100
   - Shard 2: Credit $100
   
PROBLEMS:
- If Shard 2 fails after PREPARE, Shard 1 is locked
- Network partition: One shard can't reach coordinator
- Shard 1 locked for unknown duration
- Slow (locks held for duration of transaction)

✓ SOLUTION 2: Saga Pattern (Event-Driven)
1. Initiate transfer:
   Event: "Transfer $100 from User1 to User2"
   
2. Shard 1 receives event:
   - Debit $100 from User1
   - Emit: "Amount debited, awaiting credit"
   
3. Shard 2 receives event:
   - Credit $100 to User2
   - Emit: "Transfer complete"
   
4. On failure, compensate:
   If Shard 2 fails to credit:
   - Emit: "Refund $100 to User1"
   - Shard 1 reverses transaction

ADVANTAGES:
✓ No locks held across shards
✓ Handles network partitions
✓ Eventual consistency
✓ Easier to scale

DISADVANTAGES:
✗ Complex error handling
✗ Eventual consistency (not immediately consistent)

✓ SOLUTION 3: Co-shard Related Data
Problem solved by design!

If both users always on same shard:
- User1 and User2 on Shard 1
- Transfer happens locally in Shard 1
- No distributed transaction needed

Co-sharding strategy:
- Shard by company_id or organization_id
- Keep related data together
- Users of same company on same shard
- Orders and Payments together
```

---

#### **Q10: What are the common PostgreSQL performance issues in production?**

**Answer**:

```
1. MISSING INDEXES
Symptom: Queries take 10+ seconds
Diagnosis: EXPLAIN shows "Seq Scan" on large tables
Fix: 
  CREATE INDEX idx_column ON table(column);

2. CONNECTION POOL EXHAUSTION
Symptom: "too many connections" error, new queries rejected
Cause: Connections not closed properly, long-lived transactions
Diagnosis:
  SELECT count(*) FROM pg_stat_activity;
Fix:
  - Use PGBouncer
  - Use HikariCP with proper settings
  - Fix application connection leaks
  
3. SLOW QUERIES DUE TO DISK I/O
Symptom: High `ioread`, queries slow
Cause: Working set > RAM, lots of disk access
Diagnosis:
  - Check `shared_buffers` setting
  - Check `work_mem` setting
Fix:
  - Increase RAM
  - Increase `shared_buffers` (25% of RAM)
  - Add indexes to reduce I/O
  
4. LONG-RUNNING TRANSACTIONS
Symptom: Queries wait, WAL (write-ahead log) grows large
Cause: Open transaction holding locks
Diagnosis:
  SELECT * FROM pg_stat_activity WHERE state = 'active';
Fix:
  - Find long queries and optimize
  - Break into smaller transactions
  - Add indexes to speed up queries
  
5. TABLE BLOAT
Symptom: Table larger than expected, slow scans
Cause: Many UPDATEs/DELETEs create dead rows
Diagnosis:
  SELECT n_dead_tup FROM pg_stat_user_tables;
Fix:
  VACUUM ANALYZE table_name;  -- Manual cleanup
  OR set autovacuum = on;      -- Automatic cleanup
  
6. LOCK CONTENTION
Symptom: Queries wait, "ExclusiveLock" in logs
Cause: Multiple transactions updating same rows
Fix:
  - Optimize query logic to reduce lock time
  - Use lower isolation levels if possible
  - Consider sharding to reduce contention
```

---

### Advanced Questions

#### **Q11: Design a database for an e-commerce platform with 100M users, 1B products, and 50M daily orders.**

**Answer**:

```
SCHEMA DESIGN:

Users Table:
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    country_id INT,
    created_at TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_country (country_id)
);

Products Table (1B rows):
CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    category_id INT,
    price DECIMAL(10, 2),
    stock INT,
    INDEX idx_category (category_id),
    INDEX idx_price (price)
);

Orders Table (50M daily = 1.8B/year):
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    created_at TIMESTAMP,
    total DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

Order Items Table (each order has avg 3 items):
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
);

SHARDING STRATEGY:

Users: Shard by user_id (hash-based)
- 100M users / 10 shards = 10M users per shard
- Each shard: 100GB RAM
- Isolates user data, enables scaling

Products: NO SHARDING (replicated)
- 1B products fits in one database
- Replicate to 3 read replicas
- Each read replica handles queries
- Write goes to primary

Orders: Shard by user_id
- Order belongs to user_id
- Orders on same shard as user (co-sharding)
- Simplifies transactions
- User queries their own orders without cross-shard joins

REPLICATION:

Users Shards:
┌──────────────┐  Replication  ┌──────────────┐  ┌──────────────┐
│ Shard 1      │─────────────→ │ Shard 1 Read │  │ Shard 1 Read │
│ Primary      │               │ Replica 1    │  │ Replica 2    │
└──────────────┘               └──────────────┘  └──────────────┘

Products:
┌──────────────┐  Replication  ┌──────────────┐  ┌──────────────┐
│ Products DB  │─────────────→ │ Products     │  │ Products     │
│ Primary      │               │ Read Replica │  │ Read Replica │
└──────────────┘               └──────────────┘  └──────────────┘

QUERY ROUTING:

// Get user profile
GET /users/{user_id}
→ Router: shard_id = hash(user_id) % 10
→ Query Shard 1 primary (write-capable)

// Get user orders
GET /users/{user_id}/orders
→ Router: shard_id = hash(user_id) % 10
→ Query Shard 1 read replica (cheaper)

// Get product details
GET /products/{product_id}
→ No sharding, query any product replica

// Global search (all products)
GET /products/search
→ Query products database (pre-indexed, cached)

CACHING LAYER:

Redis Cache (in-memory):
- Cache hot products (top 100k by views)
- Cache user profiles
- Cache order summaries

Pattern:
1. Check cache
2. If miss, query database
3. Populate cache (TTL: 1 hour)

ANALYTICS / REPORTING:

Separate read-only analytics database:
- Copy of production data (daily backup)
- Heavy aggregations here
- Doesn't impact production

Query example (slow, but doesn't hurt users):
SELECT category, COUNT(*), AVG(price)
FROM products
GROUP BY category;

CONFIGURATION:

Connections per shard: 25 (with PGBouncer)
Total connections: 10 shards × 25 = 250 to users
                 + 25 to products
                 + 25 to analytics
Total: ~300 connections

Disk space:
- Users shards: 100GB × 10 = 1TB
- Products: 200GB × 3 = 600GB
- Orders: 500GB × 10 = 5TB
- Total: ~6.6TB

Memory:
- shared_buffers = 32GB per server
- work_mem = 256MB
- Total: ~400GB across cluster
```

---

#### **Q12: How do you optimize PostgreSQL for a read-heavy workload with analytics queries?**

**Answer**:

```
CONFIGURATION:

# postgresql.conf for read-heavy analytics

shared_buffers = 64GB           # 25% of 256GB RAM
effective_cache_size = 192GB    # 75% of 256GB RAM
work_mem = 512MB                # Per operation memory
maintenance_work_mem = 8GB      # For VACUUM, INDEX

random_page_cost = 1.1          # SSDs faster than HDDs
effective_io_concurrency = 200  # Parallel I/O ops

# Parallelization
max_parallel_workers_per_gather = 8
max_parallel_workers = 16
max_worker_processes = 16

# Connection pooling
max_connections = 200           # Default 100 per shard

QUERY OPTIMIZATION:

1. Materialized Views (Pre-computed Results)

CREATE MATERIALIZED VIEW product_category_stats AS
SELECT 
    category_id,
    COUNT(*) as product_count,
    AVG(price) as avg_price,
    MIN(price) as min_price,
    MAX(price) as max_price
FROM products
GROUP BY category_id;

CREATE INDEX idx_category_id ON product_category_stats(category_id);

REFRESH MATERIALIZED VIEW product_category_stats;  -- Refresh daily

// Query is now instant instead of scanning 1B rows!

2. Columnar Storage (for analytics)

-- Use CSTORE (Columnar Store) extension
CREATE TABLE products_columnar (
    product_id BIGINT,
    category_id INT,
    price DECIMAL(10, 2),
    stock INT
) WITH (fillfactor = 100, appendonly = true, compresstype = snappy);

SELECT COUNT(*) FROM products_columnar WHERE category_id = 5;
-- Scans only category_id column (not all columns)
-- 10-100x faster for analytics queries

3. Partitioning (Large Tables)

CREATE TABLE orders (
    id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    total DECIMAL(10, 2)
) PARTITION BY RANGE (YEAR(created_at));

CREATE TABLE orders_2024 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE orders_2023 PARTITION OF orders
    FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');

// Queries on specific year only scan relevant partition
SELECT * FROM orders WHERE created_at >= '2024-01-01'
-- Scans only orders_2024 partition, not entire table

4. Parallel Query Execution

SET max_parallel_workers_per_gather = 8;

SELECT category, COUNT(*), AVG(price)
FROM products
GROUP BY category;

// PostgreSQL splits work across 8 parallel workers
// Each worker processes portion of table
// Results merged at end

5. Statistical Aggregates (Pre-computed)

-- Instead of computing on demand
CREATE TABLE product_stats (
    category_id INT,
    day DATE,
    product_count INT,
    total_sales DECIMAL(15, 2),
    avg_price DECIMAL(10, 2),
    PRIMARY KEY (category_id, day)
);

-- Update daily via batch job
INSERT INTO product_stats
SELECT category_id, DATE(created_at), COUNT(*), SUM(total), AVG(price)
FROM orders
GROUP BY category_id, DATE(created_at);

// Query is instant
SELECT * FROM product_stats WHERE day = '2024-07-30';

REPLICATION FOR READ SCALING:

Primary Database (writes):
- Only accepts INSERT/UPDATE/DELETE
- Replicates to replicas

Read Replicas (queries):
- 3-5 read-only copies
- Distribute analytics queries
- Each replica can serve 1000s of queries

┌────────────┐     Replication Stream    ┌────────────┐
│  Primary   │────────────────────────→  │ Read Rep 1 │
│  (Writes)  │                           └────────────┘
└────────────┘                           ┌────────────┐
                                         │ Read Rep 2 │
                                         └────────────┘
                                         ┌────────────┐
                                         │ Read Rep 3 │
                                         └────────────┘

QUERY ROUTING:

// Analytics queries (slow, read-only)
SET route TO read_replica;
SELECT COUNT(*), AVG(price) FROM products;
→ Routed to least-loaded read replica

// Transactional queries (must be consistent)
SET route TO primary;
INSERT INTO orders VALUES (...);
→ Always goes to primary
```

---

## Summary Checklist

**Database Design**:
- [ ] Normalize schema (3NF minimum)
- [ ] Add appropriate indexes (WHERE, JOIN, ORDER BY)
- [ ] Plan for growth (sharding strategy)
- [ ] Set up replication (HA, read scaling)

**Query Optimization**:
- [ ] Use EXPLAIN ANALYZE to check plans
- [ ] Avoid N+1 queries (use JOINs or batching)
- [ ] Paginate large result sets
- [ ] Cache frequently accessed data

**Connection Management**:
- [ ] Use connection pooling (HikariCP)
- [ ] Don't hold connections for long transactions
- [ ] Close all resources (try-with-resources)
- [ ] Configure appropriate pool size

**Monitoring**:
- [ ] Track slow queries (log_min_duration_statement)
- [ ] Monitor connection usage
- [ ] Watch disk I/O and memory usage
- [ ] Set up alerts for anomalies

**Production Readiness**:
- [ ] Backup strategy (WAL archiving)
- [ ] Replication configured
- [ ] Monitoring and alerting
- [ ] Disaster recovery plan
- [ ] Regular VACUUM and ANALYZE

---

**Document Version**: 1.0  
**Last Updated**: 2026-07-30  
**Perspective**: 16 Years Java Development & Database Architecture  
**Tools Covered**: PostgreSQL, PGBouncer, HikariCP, Spring Data JPA
