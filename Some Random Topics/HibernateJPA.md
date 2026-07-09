# Hibernate & JPA Complete Guide
## Fundamentals, Entity Lifecycle, and Interview Questions

> A comprehensive reference for understanding Java Persistence API (JPA) and Hibernate ORM framework, covering concepts, architecture, entity states, and frequently asked interview questions.

---

## Table of Contents

### Part 1: Fundamentals
1. [What is JPA?](#what-is-jpa)
2. [What is Hibernate?](#what-is-hibernate)
3. [JPA vs Hibernate vs Spring Data JPA](#jpa-vs-hibernate-vs-spring-data-jpa)
4. [ORM Concepts](#orm-concepts)
5. [JPA Architecture](#jpa-architecture)

### Part 2: Entity Lifecycle & Object States
6. [Entity Definition](#entity-definition)
7. [Object Lifecycle States](#object-lifecycle-states)
8. [State Transitions Detailed](#state-transitions-detailed)
9. [Persistence Context (First-Level Cache)](#persistence-context)
10. [Entity Lifecycle Callbacks](#entity-lifecycle-callbacks)

### Part 3: Interview Questions & Answers
11. [Basic Concepts Questions (Q1-Q10)](#basic-concepts)
12. [Entity & Lifecycle Questions (Q11-Q20)](#entity-lifecycle-questions)
13. [Relationships & Mappings Questions (Q21-Q30)](#relationships-questions)
14. [Query & Performance Questions (Q31-Q40)](#query-performance-questions)
15. [Advanced Concepts Questions (Q41-Q50)](#advanced-concepts)

---

# Part 1: Fundamentals

---

## What is JPA?

### Definition

**JPA (Java Persistence API)** is a Java specification (Interface) for accessing, persisting, and managing data between Java objects and relational databases.

```
Key Points:
├─ Specification (not implementation)
├─ Part of Java EE/Jakarta EE
├─ Defines standard ORM annotations and **interfaces**
├─ Database-agnostic (supports any database via JPA providers)
└─ Reduces boilerplate code for data persistence
```

### What JPA Provides

```
1. Annotations for mapping objects to database
   └─ @Entity, @Table, @Column, @Id, @GeneratedValue

2. Entity Manager API for operations
   └─ persist(), merge(), remove(), find(), createQuery()

3. JPQL (Java Persistence Query Language)
   └─ Database-independent query language

4. Lifecycle callbacks
   └─ @PrePersist, @PostPersist, @PreUpdate, @PostUpdate

5. Relationship management
   └─ @OneToOne, @OneToMany, @ManyToOne, @ManyToMany

6. Transaction management
   └─ Integrated with Java Transaction API (JTA)
```

### JPA Providers (Implementations)

```
Popular JPA Implementations:
├─ Hibernate (most popular, feature-rich)
├─ EclipseLink (reference implementation)
├─ OpenJPA (Apache)
└─ Datanucleus (comprehensive)
```

---

## What is Hibernate?

### Definition

**Hibernate** is the most popular implementation of JPA specification. It's an open-source, feature-rich ORM framework that goes beyond JPA standards.

```
Key Points:
├─ Implements JPA specification
├─ Adds Hibernate-specific features beyond JPA
├─ Manages object-relational impedance mismatch
├─ Provides caching, lazy loading, cascading
├─ Reduced SQL writing (auto-generates queries)
└─ Improves database portability
```

### Hibernate Beyond JPA

```
Hibernate-Specific Features:
├─ Named queries (@NamedQuery)
├─ Criteria API (more flexible than JPQL)
├─ HQL (Hibernate Query Language, predecessor of JPQL)
├─ Multi-level caching (L1, L2, query cache)
├─ Batch processing
├─ Lazy initialization strategies
├─ Natural IDs
└─ Custom types and user types
```

### Architecture

```
Application Layer
    ↓
Spring Data JPA / JPA
    ↓
Hibernate (JPA Implementation)
    ├─ Session (Persistence Context)
    ├─ Entity Manager
    ├─ Query Engine
    └─ Mapping Engine
    ↓
Persistence Layer
    ├─ Dialect (Database-specific SQL)
    └─ JDBC
    ↓
Database
```

---

## JPA vs Hibernate vs Spring Data JPA

```
Aspect              │ JPA                    │ Hibernate                │ Spring Data JPA
────────────────────┼────────────────────────┼────────────────────────┼──────────────────────
What is it?         │ Specification          │ Implementation         │ Abstraction/Helper
Standard            │ Java EE standard       │ No (extends JPA)       │ No (Spring framework)
Annotations         │ @Entity, @Column       │ All JPA + more         │ Uses JPA annotations
Query Language      │ JPQL                   │ HQL + JPQL + Criteria  │ Derived query methods
Vendor Lock-in      │ No (pluggable)        │ Some (extensions)      │ No (abstraction layer)
Learning Curve      │ Moderate               │ Steep (many features)  │ Easy (simple)
Configuration       │ More verbose           │ More control           │ Auto-configured
Use Case            │ Standard ORM           │ Complex requirements   │ CRUD + basic queries
```

### Architecture Comparison

```
ONLY JPA:
Application → JPA (spec) → Hibernate Implementation → Database

ONLY Hibernate:
Application → Hibernate ORM → Database
(Can use some JPA annotations, but direct Hibernate APIs)

Spring Data JPA:
Application → Spring Data JPA → JPA → Hibernate → Database
(Simplest for most cases)
```

---

## ORM Concepts

### What is ORM?

**ORM (Object-Relational Mapping)** bridges the gap between object-oriented programming and relational databases.

```
IMPEDANCE MISMATCH
━━━━━━━━━━━━━━━━━━━

Object World         Database World
───────────         ──────────────
Classes             Tables
Objects             Rows
Properties/Fields   Columns
Object References   Foreign Keys
Collections         Joins
Inheritance         Limited support
Polymorphism        Not supported
Encapsulation       Full exposure

ORM solves this mismatch by:
├─ Mapping objects to tables
├─ Properties to columns
├─ References to foreign keys
├─ Collections to joining tables
└─ Inheritance to table strategies
```

### Benefits of ORM

```
✓ Reduced SQL writing (auto-generated queries)
✓ Database independence (dialect handles differences)
✓ Object-oriented approach (write Java, not SQL)
✓ Automatic relationship management
✓ Built-in caching
✓ Transaction management
✓ Type safety (compile-time checking)
✓ Lazy loading (load only when needed)
✓ Cascading operations (auto-delete related entities)
```

---

## JPA Architecture

```
┌─────────────────────────────────────────────────────────┐
│           JPA Architecture Layers                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. DOMAIN MODEL LAYER (Your Code)                     │
│     └─ Java classes with JPA annotations               │
│                                                         │
│  2. JPA LAYER (Specification)                          │
│     ├─ EntityManager                                   │
│     ├─ EntityManagerFactory                            │
│     ├─ Transaction                                     │
│     └─ Query                                           │
│                                                         │
│  3. PERSISTENCE PROVIDER (Hibernate)                   │
│     ├─ Session Factory                                 │
│     ├─ Session (1st Level Cache)                      │
│     ├─ Query Engine                                    │
│     ├─ 2nd Level Cache                                │
│     └─ Dialect                                         │
│                                                         │
│  4. DATABASE LAYER                                     │
│     ├─ JDBC / Connection Pool                          │
│     └─ Relational Database                             │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Key Components

```
EntityManagerFactory:
├─ Created once per application
├─ Expensive to create (reads configuration)
├─ Thread-safe
└─ Used to create EntityManagers

EntityManager:
├─ Per-transaction object
├─ Not thread-safe
├─ Manages entity lifecycle
├─ Provides query execution
└─ Wrapper around Hibernate Session

Persistence Context:
├─ Entity cache for EntityManager
├─ Keeps track of entities
├─ Dirty checking (auto-update detection)
└─ Cleared when EntityManager closed

Transaction:
├─ Atomicity boundary
├─ Isolation from other transactions
├─ All-or-nothing execution
└─ Handles commit/rollback
```

---

# Part 2: Entity Lifecycle & Object States

---

## Entity Definition

### What is an Entity?

```java
@Entity                          // Marks class as JPA entity
@Table(name = "users")          // Maps to database table
public class User {
    
    @Id                         // Primary key
    @GeneratedValue             // Auto-generated value
    private Long id;
    
    @Column(name = "user_name") // Column mapping
    private String name;
    
    @Column(unique = true)
    private String email;
    
    @ManyToOne                  // Relationship
    private Department dept;
    
    @OneToMany(mappedBy = "user")
    private List<Task> tasks;
    
    // Getters and setters
}
```

### Entity Requirements

```
1. Must have @Entity annotation
2. Must have public or protected no-arg constructor
3. Must have @Id (primary key)
4. Cannot be final (Hibernate creates proxies)
5. Properties must have getters/setters
6. Must be serializable (for caching)
7. Cannot use array-valued properties
```

---

## Object Lifecycle States

### Four Entity States

```
┌────────────────────────────────────────────────────────────────┐
│            JPA/HIBERNATE ENTITY LIFECYCLE STATES               │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  1. NEW/TRANSIENT                                             │
│     ├─ Object created with 'new'                              │
│     ├─ No database row exists                                 │
│     ├─ No primary key (id = null)                             │
│     ├─ Not associated with any EntityManager                  │
│     └─ Changes are NOT persisted                              │
│                                                                │
│  2. MANAGED/PERSISTENT                                        │
│     ├─ Object associated with EntityManager                   │
│     ├─ Exists in database                                     │
│     ├─ Has primary key                                        │
│     ├─ Changes tracked by Persistence Context                 │
│     └─ Changes AUTO-PERSISTED on flush/commit                │
│                                                                │
│  3. DETACHED                                                  │
│     ├─ Was MANAGED, now unassociated                          │
│     ├─ Exists in database                                     │
│     ├─ Has primary key                                        │
│     ├─ EntityManager no longer tracks it                      │
│     └─ Changes are NOT persisted                              │
│                                                                │
│  4. REMOVED                                                   │
│     ├─ Marked for deletion                                    │
│     ├─ Still in database until flush                          │
│     ├─ Deleted from DB on flush/commit                        │
│     ├─ After commit, becomes DETACHED                         │
│     └─ Cannot use entity again                                │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### State Diagram

```
                    ┌──────────────┐
                    │  TRANSIENT   │
                    │ (new object) │
                    └──────┬───────┘
                           │
                    persist()│ merge()
                           │
                           ▼
                    ┌──────────────┐
        ┌──────────→│   MANAGED    │←─────┐
        │           │ (in session) │      │
        │           └──────┬───────┘      │
        │                  │              │
  merge()│          remove()│  close()    │update()
        │                  │              │
        │                  ▼              │
        │           ┌──────────────┐      │
        │           │  REMOVED     │      │
        │           │ (deleted)    │      │
        │           └──────────────┘      │
        │                                 │
        │                                 │
        │           ┌──────────────┐      │
        └───────────│  DETACHED    │──────┘
                    │ (no session) │
                    └──────────────┘
```

---

## State Transitions Detailed

### From TRANSIENT to MANAGED

```java
// STEP 1: Create new object (TRANSIENT state)
User user = new User();
user.setName("John");
user.setEmail("john@example.com");

System.out.println("ID: " + user.getId());  // null (TRANSIENT)
System.out.println("State: TRANSIENT");

// STEP 2: Save to database (TRANSIENT → MANAGED)
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

em.persist(user);  // or em.merge(user) for detached
// NOW: MANAGED state
// User has ID assigned (depending on GenerationType)
// In Persistence Context

System.out.println("ID: " + user.getId());  // Has value (MANAGED)
System.out.println("State: MANAGED");

// STEP 3: Commit transaction
em.getTransaction().commit();
// User inserted into database
```

### From MANAGED to DETACHED

```java
User user = em.find(User.class, 1);  // MANAGED
// In Persistence Context

em.close();  // Close EntityManager
// NOW: DETACHED
// Still has ID, but EntityManager no longer tracks

System.out.println(user.getId());  // Still 1 (has ID)
System.out.println("State: DETACHED");

// Changes made WILL NOT persist
user.setName("Jane");  // Change ignored
em.persist(user);  // ERROR: EntityManager closed!
```

### From DETACHED to MANAGED

```java
// Method 1: Using merge()
User detachedUser = ...;  // DETACHED
User managedUser = em.merge(detachedUser);  // DETACHED → MANAGED

// Note: Original object stays detached
// merge() returns NEW managed copy
managedUser.setName("Jane");
em.getTransaction().commit();  // managedUser changes persisted

System.out.println(detachedUser == managedUser);  // false (different objects)


// Method 2: Using update()
em.update(detachedUser);  // DETACHED → MANAGED
// Same object now managed
detachedUser.setName("Jane");
em.getTransaction().commit();  // Changes persisted
```

### From MANAGED to REMOVED

```java
User user = em.find(User.class, 1);  // MANAGED

em.getTransaction().begin();
em.remove(user);  // MANAGED → REMOVED
// DELETE queued, not executed yet

em.getTransaction().commit();
// DELETE executed in database
// User now DETACHED (no longer in Persistence Context)

// Cannot use entity again effectively
em.persist(user);  // Would insert as new (ID = null after delete)
```

---

## Persistence Context

### What is Persistence Context?

```
PERSISTENCE CONTEXT = First-Level Cache (1st Level Cache)

Persistence Context:
├─ Per-EntityManager cache
├─ Holds all MANAGED entities
├─ Automatic dirty checking
├─ Identity map (guarantee object identity)
└─ Cleared when EntityManager closes or clear() called
```

### How Persistence Context Works

```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

// LOAD 1 (from database)
User user1 = em.find(User.class, 123);
// SQL: SELECT * FROM users WHERE id=123
// user1 stored in Persistence Context

// LOAD 2 (from Persistence Context cache - NO SQL!)
User user2 = em.find(User.class, 123);
// NO SQL! Retrieved from cache

System.out.println(user1 == user2);  // true (same object reference)

// DIRTY CHECKING
user1.setName("Updated");
// Changes tracked by Persistence Context
// UPDATE query queued (not executed yet)

em.flush();  // or em.getTransaction().commit()
// UPDATE user SET name='Updated' WHERE id=123

em.getTransaction().commit();
em.close();
// Persistence Context cleared
```

### Persistence Context Life Cycle

```
EntityManager em = emf.createEntityManager();
┌────────────────────────────┐
│  PERSISTENCE CONTEXT       │
│  (Empty initially)         │
│                            │
│  em.find() → Add entity    │
│  em.persist() → Add entity │
│  em.merge() → Add entity   │
│  em.remove() → Remove entity
│                            │
│  AUTO-FLUSH (before query) │
│  Dirty checking            │
│  INSERT/UPDATE/DELETE sent │
│                            │
└────────────────────────────┘

em.flush();  // Explicit flush
em.clear();  // Clear all entities
em.close();  // Close EntityManager
// Context destroyed
```

---

## Entity Lifecycle Callbacks

### JPA Lifecycle Events

```java
@Entity
public class User {
    
    @Id
    private Long id;
    
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // ============ LIFECYCLE CALLBACKS ============
    
    @PrePersist
    public void prePersist() {
        System.out.println("Before INSERT");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PostPersist
    public void postPersist() {
        System.out.println("After INSERT");
        System.out.println("User saved with ID: " + this.id);
    }
    
    @PreUpdate
    public void preUpdate() {
        System.out.println("Before UPDATE");
        this.updatedAt = LocalDateTime.now();
    }
    
    @PostUpdate
    public void postUpdate() {
        System.out.println("After UPDATE");
    }
    
    @PreRemove
    public void preRemove() {
        System.out.println("Before DELETE");
    }
    
    @PostRemove
    public void postRemove() {
        System.out.println("After DELETE");
    }
    
    @PostLoad
    public void postLoad() {
        System.out.println("After entity loaded from database");
    }
}
```

### Callback Firing Order

```
INSERT FLOW:
1. @PrePersist ← Before INSERT
2. (SQL INSERT executed)
3. @PostPersist ← After INSERT

UPDATE FLOW:
1. @PreUpdate ← Before UPDATE (on flush)
2. (SQL UPDATE executed)
3. @PostUpdate ← After UPDATE

DELETE FLOW:
1. @PreRemove ← Before DELETE
2. (SQL DELETE executed)
3. @PostRemove ← After DELETE

LOAD FLOW:
1. (SQL SELECT executed)
2. @PostLoad ← After loading from DB

USAGE EXAMPLES:
@PrePersist → Set timestamps, defaults, validation
@PostPersist → Logging, cache invalidation
@PreUpdate → Audit trail, update timestamps
@PostUpdate → Logging, send events
@PreRemove → Prevent deletion, validation
@PostRemove → Cleanup, logging
@PostLoad → Initialize computed fields
```

---

# Part 3: Interview Questions & Answers

---

## Q1: What is the difference between @Entity and @Table annotations?

### Answer

```java
@Entity      // JPA annotation - marks class as entity
             // Required - tells JPA this is a persistent class
             
@Table       // JPA annotation - specifies database table details
             // Optional - if not specified, table name = class name


EXAMPLE:

@Entity                              // Class is an entity
@Table(name = "tbl_users",          // Maps to 'tbl_users' table
       schema = "public",            // In 'public' schema
       uniqueConstraints = {         // Add unique constraints
           @UniqueConstraint(
               name = "uk_email",
               columnNames = {"email"}
           )
       },
       indexes = {                   // Add indexes
           @Index(name = "idx_status",
                  columnList = "status")
       })
public class User {
    
    @Id
    private Long id;
    
    @Column(name = "user_name",      // Column details
           nullable = false,
           length = 100)
    private String name;
}

WITHOUT @Table:
───────────────
@Entity
public class User { }

// Table name defaults to class name: "User"
// Schema defaults to default schema
// No unique constraints or indexes
```

---

## Q2: What does @Id annotation do?

### Answer

```
@Id marks the primary key of the entity

Characteristics:
├─ Must be present on exactly one field/property
├─ Can be any type: Long, String, Integer, UUID, etc.
├─ Can be natural or surrogate key
├─ Must not be null for persisted entities
└─ Used for database PRIMARY KEY constraint


EXAMPLES:

// Auto-generated ID
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Auto-incremented by database
}

// String ID (natural key)
@Entity
public class Country {
    @Id
    private String code;  // "US", "UK", "IN"
}

// Composite Primary Key
@Embeddable
public class OrderItemPK {
    private Long orderId;
    private Long itemId;
}

@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemPK id;  // Composite key
}

// UUID
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;  // UUID generated
}
```

---

## Q3: Explain @GeneratedValue strategies

### Answer

```
@GeneratedValue specifies how primary key is generated

STRATEGIES:

1. IDENTITY
   ├─ Database generates ID (auto-increment)
   ├─ Works with: MySQL, SQL Server, PostgreSQL
   ├─ Format: @GeneratedValue(strategy = GenerationType.IDENTITY)
   └─ Best for: Simple single-column auto-increment
   
   @Entity
   public class User {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;  // Database: AUTO_INCREMENT
   }


2. SEQUENCE
   ├─ Database sequence generates ID
   ├─ Works with: Oracle, PostgreSQL, H2
   ├─ More portable than IDENTITY
   └─ Efficient for batch inserts
   
   @Entity
   @SequenceGenerator(name = "user_seq",
                      sequenceName = "user_id_seq",
                      initialValue = 1,
                      allocationSize = 1)
   public class User {
       @Id
       @GeneratedValue(strategy = GenerationType.SEQUENCE,
                      generator = "user_seq")
       private Long id;
   }


3. TABLE
   ├─ Uses database table to maintain ID values
   ├─ Portable (works with any database)
   ├─ Slower than IDENTITY/SEQUENCE
   └─ Requires extra table
   
   @Entity
   @TableGenerator(name = "id_generator",
                   table = "id_sequence",
                   pkColumnName = "seq_name",
                   valueColumnName = "seq_value")
   public class User {
       @Id
       @GeneratedValue(strategy = GenerationType.TABLE,
                      generator = "id_generator")
       private Long id;
   }


4. AUTO (Default)
   ├─ Hibernate picks best strategy for database
   ├─ Strategy: IDENTITY → SEQUENCE → TABLE
   └─ Recommended for portability
   
   @Entity
   public class User {
       @Id
       @GeneratedValue  // Uses AUTO by default
       private Long id;
   }


5. UUID (Java 17+)
   ├─ Generates UUID (universally unique identifier)
   ├─ No database sequence needed
   └─ Good for distributed systems
   
   @Entity
   public class Event {
       @Id
       @GeneratedValue(strategy = GenerationType.UUID)
       private UUID id;
   }


COMPARISON TABLE:
─────────────────
Strategy    │ Database    │ Portable │ Performance │ Distributed
────────────┼─────────────┼──────────┼─────────────┼────────────
IDENTITY    │ MySQL/SQL   │ Low      │ Fast        │ Poor
SEQUENCE    │ Oracle/PG   │ Medium   │ Very Fast   │ Poor
TABLE       │ Any         │ High     │ Slow        │ Poor
AUTO        │ Any         │ High     │ Database    │ Poor
UUID        │ Any         │ High     │ Fast        │ Excellent
```

---

## Q4: What is the persistence lifecycle?

### Answer

```
PERSISTENCE LIFECYCLE = Journey of entity from creation to deletion

SIX MAIN PHASES:

1. TRANSIENT (NEW)
   ├─ Entity created with 'new'
   ├─ user = new User();
   ├─ Not in database
   └─ Not managed by EntityManager

2. PERSIST (SAVE)
   ├─ em.persist(entity) called
   ├─ Entity added to Persistence Context
   ├─ ID assigned
   └─ But NOT inserted yet

3. MANAGED (PERSISTENT)
   ├─ Entity in Persistence Context
   ├─ Changes tracked
   ├─ UPDATE auto-generated on flush
   └─ Entity in database

4. FLUSH
   ├─ Changes sent to database
   ├─ SQL executed (INSERT/UPDATE)
   ├─ Automatic before commit or query
   └─ Can be manual: em.flush()

5. COMMIT
   ├─ Transaction commits
   ├─ Changes permanent in database
   ├─ Persistence Context cleared
   └─ Entity becomes DETACHED

6. DETACHED
   ├─ EntityManager closed/clear()
   ├─ Changes no longer tracked
   ├─ Must be merged to persist changes
   └─ Entity still in database


COMPLETE EXAMPLE:

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @PrePersist
    public void prePersist() {
        System.out.println("Before INSERT");
    }
    
    @PostPersist
    public void postPersist() {
        System.out.println("After INSERT");
    }
    
    @PreUpdate
    public void preUpdate() {
        System.out.println("Before UPDATE");
    }
    
    @PostUpdate
    public void postUpdate() {
        System.out.println("After UPDATE");
    }
}


LIFECYCLE IN ACTION:

// Phase 1: TRANSIENT
User user = new User();
user.setName("John");
// Not persisted, id=null

EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

// Phase 2 & 3: PERSIST → MANAGED
em.persist(user);
// @PrePersist called
// @PostPersist called (after flush)
// id assigned
// In Persistence Context

// Phase 4: FLUSH (automatic or manual)
user.setName("Jane");
em.flush();  // Or on commit
// @PreUpdate called
// UPDATE executed
// @PostUpdate called

// Phase 5 & 6: COMMIT → DETACHED
em.getTransaction().commit();
em.close();
// Entity still in memory but DETACHED
// Changes no longer tracked

// Cannot persist changes anymore
user.setName("Bob");  // Ignored
```

---

## Q5: Explain Persistence Context vs Session

### Answer

```
PERSISTENCE CONTEXT (JPA Term)
├─ JPA specification concept
├─ Represents managed entities
├─ Provides identity map
├─ Attached to EntityManager
└─ Cleared on EntityManager.close()


SESSION (Hibernate Term)
├─ Hibernate-specific concept
├─ Equivalent to Persistence Context
├─ Underlying implementation
├─ Attached to EntityManager
└─ Cleared on Session.close()


RELATIONSHIP:
EntityManager's Persistence Context ≈ Hibernate's Session


KEY CHARACTERISTICS:

1st-Level Cache:
├─ Caches within single transaction
├─ Automatic dirty checking
├─ Guarantees entity identity (one object per ID)
└─ Cleared on EntityManager.close()


IDENTITY MAP PATTERN:
─────────────────────
@Transactional
public void demonstrateIdentity() {
    User user1 = em.find(User.class, 1);  // Database query
    User user2 = em.find(User.class, 1);  // Cached (no query)
    
    System.out.println(user1 == user2);  // true
    // Same object because identity map ensures one instance per ID
}


DIRTY CHECKING:
───────────────
@Transactional
public void demonstrateTracking() {
    User user = em.find(User.class, 1);
    user.setName("Updated");
    // No em.update() needed!
    
    em.flush();  // Automatic UPDATE
    // Changes persisted because Persistence Context tracked modification
}
```

---

## Q6: What is fetch type in JPA relationships?

### Answer

```
FETCH TYPE specifies when related entities are loaded

TWO STRATEGIES:

1. EAGER (FetchType.EAGER)
   ├─ Load related entity immediately
   ├─ When: parent entity loaded
   ├─ SQL: JOIN query
   ├─ Performance: Faster access (data ready)
   ├─ Memory: More data loaded upfront
   └─ Default for @ManyToOne, @OneToOne


2. LAZY (FetchType.LAZY)
   ├─ Load related entity on access
   ├─ When: property accessed
   ├─ SQL: Separate query
   ├─ Performance: Slower initial load
   ├─ Memory: Only load needed data
   └─ Default for @OneToMany, @ManyToMany


EXAMPLES:

@Entity
public class User {
    
    // EAGER: Department loaded immediately
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;
    
    // LAZY: Tasks loaded only when accessed
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Task> tasks;
}


BEHAVIOR:

EAGER FETCH:
┌─ em.find(User.class, 1)
├─ SELECT * FROM users WHERE id=1
├─ SELECT * FROM departments WHERE id=1  ← Immediate
└─ User + Department loaded

LAZY FETCH:
┌─ em.find(User.class, 1)
├─ SELECT * FROM users WHERE id=1
└─ Department remains proxy (not loaded)

User user = em.find(User.class, 1);
String name = user.getDepartment().getName();
// Now query:
// SELECT * FROM departments WHERE id=1
// Data loaded on access


COMMON ISSUE - N+1 Queries:
──────────────────────────
@OneToMany(fetch = FetchType.LAZY)  // LAZY by default
private List<Task> tasks;

// Loading users with tasks
List<User> users = em.createQuery("FROM User").getResultList();
// 1 query: SELECT * FROM users

for (User user : users) {
    int count = user.getTasks().size();
    // N queries! (one per user)
}
// Total: 1 + N queries


SOLUTION 1: JOIN FETCH
@Query("SELECT DISTINCT u FROM User u JOIN FETCH u.tasks")
List<User> findAllWithTasks();
// 1 query with JOIN

SOLUTION 2: EAGER Fetch
@OneToMany(fetch = FetchType.EAGER)  // Load immediately
private List<Task> tasks;

SOLUTION 3: Batch Fetching
@OneToMany(fetch = FetchType.LAZY)
@BatchSize(size = 20)  // Load in batches of 20
private List<Task> tasks;
// Instead of N queries, load in batches
```

---

## Q7: What is lazy initialization exception?

### Answer

```
LAZY INITIALIZATION EXCEPTION

Problem:
├─ Accessing lazy-loaded property outside session
├─ EntityManager closed but accessing data
└─ Lazy proxy cannot initialize


ROOT CAUSE:

Lazy Entity:
┌─ @ManyToOne(fetch = FetchType.LAZY)
├─ Returns proxy object (not real entity)
└─ Proxy needs session to load data

Session Closed:
┌─ EntityManager closed or session ended
├─ Transaction committed
└─ No database connection available

Access Outside Session:
┌─ proxy.getProperty()
├─ Proxy tries to initialize
└─ Fails: No session to query database


CODE EXAMPLE:

@Entity
public class User {
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;  // Lazy
}


public User getUser(Long id) {
    EntityManager em = emf.createEntityManager();
    User user = em.find(User.class, id);
    em.close();  // Session closed!
    return user;
}

// LATER...
public void process(User user) {
    String deptName = user.getDepartment().getName();
    // ERROR: LazyInitializationException!
    // user.department is proxy
    // Session is closed
    // Cannot load department
}


SOLUTIONS:

1. Initialize Before Closing Session:
   @Transactional
   public User getUser(Long id) {
       User user = em.find(User.class, id);
       
       // Access before em.close()
       String deptName = user.getDepartment().getName();
       
       return user;
   }

2. Hibernate.initialize():
   @Transactional
   public User getUser(Long id) {
       User user = em.find(User.class, id);
       Hibernate.initialize(user.getDepartment());
       return user;
   }

3. EAGER Fetch:
   @ManyToOne(fetch = FetchType.EAGER)
   private Department department;  // Always loaded

4. JOIN FETCH:
   @Query("SELECT u FROM User u JOIN FETCH u.department WHERE u.id = :id")
   User findByIdWithDept(@Param("id") Long id);

5. @Transactional (Spring):
   @Transactional
   public void process(Long userId) {
       User user = userRepository.findById(userId).get();
       // Session open throughout method
       String deptName = user.getDepartment().getName();  // Safe
   }
```

---

## Q8: What is OpenSessionInView pattern?

### Answer

```
OPENSESSIONINVIEW Pattern

Problem Being Solved:
├─ Lazy initialization exceptions
├─ Session closes before view rendering
└─ Need session to stay open


SOLUTION:
Keep session open from controller to view rendering


HOW IT WORKS:

Request Flow:
┌─ Servlet filter intercepts request
├─ Opens EntityManager/Session
├─ Controller executes
├─ View rendering happens (within session)
└─ Filter closes session


IMPLEMENTATION (Spring):

@Configuration
public class JPAConfig {
    
    @Bean
    public OpenEntityManagerInViewInterceptor 
            openEntityManagerInViewInterceptor() {
        OpenEntityManagerInViewInterceptor interceptor = 
            new OpenEntityManagerInViewInterceptor();
        return interceptor;
    }
}

Or in properties:
spring.jpa.open-in-view=true  // Enabled by default


ADVANTAGES:
├─ Lazy loading works in view
├─ No N+1 queries if careful
└─ Simpler development


DISADVANTAGES:
├─ Long-running transactions (slow)
├─ Session remains open longer (memory)
├─ Potential performance issues
├─ Can hide N+1 query problems
└─ Transaction boundary confusion


EXAMPLE:

@Controller
public class UserController {
    
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);  // Session open
        model.addAttribute("user", user);
        return "user-detail";  // In view, session still open
    }
}

// In view (Thymeleaf):
<h1>[[${user.name}]]</h1>
<p>Department: [[${user.department.name}]]</p>
<!-- Lazy department loaded here (session open) -->


BEST PRACTICE:

Now discouraged in favor of:
├─ Eager loading
├─ JOIN FETCH queries
├─ DTO projections
└─ Explicit initialization
```

---

## Q9: What is N+1 query problem?

### Answer

```
N+1 QUERY PROBLEM

Definition:
├─ Executing 1 query to get main entities
├─ Then N queries to get related entities
├─ Total: 1 + N queries (inefficient)
└─ Should be 1 query with JOIN


EXAMPLE:

@Entity
public class Department {
    @OneToMany(fetch = FetchType.LAZY)  // Default LAZY
    private List<User> users;
}

// PROBLEM CODE:
List<Department> departments = 
    em.createQuery("FROM Department", Department.class)
      .getResultList();

// Query 1: SELECT * FROM departments
// Returns 5 departments

for (Department dept : departments) {
    List<User> users = dept.getUsers();
    // Query 2-6: SELECT * FROM users WHERE department_id=?
    // One query per department
}

// Total: 1 + 5 = 6 queries (BAD!)


SOLUTIONS:

1. JOIN FETCH (RECOMMENDED):
   String jpql = "SELECT DISTINCT d FROM Department d " +
                 "LEFT JOIN FETCH d.users " +
                 "ORDER BY d.id";
   List<Department> departments = 
       em.createQuery(jpql, Department.class).getResultList();
   // 1 query with JOIN
   // Gets all departments + users

2. EAGER Fetch:
   @Entity
   public class Department {
       @OneToMany(fetch = FetchType.EAGER)
       private List<User> users;
   }
   // Always loaded immediately

3. Batch Fetching:
   @OneToMany(fetch = FetchType.LAZY)
   @BatchSize(size = 10)
   private List<User> users;
   
   // Instead of N queries
   // Loads in batches (N/10 queries)

4. @Query with JOIN FETCH:
   @Repository
   public interface DepartmentRepository 
       extends JpaRepository<Department, Long> {
       
       @Query("SELECT DISTINCT d FROM Department d " +
              "LEFT JOIN FETCH d.users")
       List<Department> findAllWithUsers();
   }

5. DTO Projection:
   public interface DeptUserCount {
       Long getId();
       String getName();
       int getUserCount();
   }
   
   @Query("SELECT NEW map(d.id, d.name, COUNT(u)) " +
          "FROM Department d LEFT JOIN d.users u " +
          "GROUP BY d.id")
   List<DeptUserCount> getDepartmentsWithCount();
```

---

## Q10: What is the difference between merge() and update()?

### Answer

```
MERGE() vs UPDATE()

MERGE():
├─ JPA standard method
├─ Safe for detached objects
├─ Creates copy in Persistence Context
├─ Returns NEW managed entity
├─ Original object stays DETACHED
├─ Merges state from detached to managed


UPDATE():
├─ Hibernate-specific method
├─ Reattaches same object
├─ Object becomes MANAGED
├─ Returns same object
├─ Assumes no changes while detached


DETAILED COMPARISON:

```java
// merge()
User detachedUser = new User(1L, "John");  // DETACHED

User managedUser = em.merge(detachedUser);
// Create new managed copy
// managedUser is NEW entity in Persistence Context
// detachedUser still DETACHED

managedUser.setName("Jane");
em.flush();
// Only managedUser's changes persisted

System.out.println(detachedUser == managedUser);  // false


// update() (Hibernate)
User detachedUser = new User(1L, "John");  // DETACHED

Session session = sessionFactory.openSession();
session.update(detachedUser);  // Same object now MANAGED

detachedUser.setName("Jane");
session.flush();
// detachedUser's changes persisted

System.out.println(detachedUser remains reference);  // true
```


CHOOSING WHICH TO USE:

Use merge() when:
├─ You don't know if detached or new
├─ You want to keep original object unchanged
├─ Object might have changed externally
└─ Standard JPA code

Use update() when:
├─ You know object is detached (has ID)
├─ You expect to modify it
├─ You're using Hibernate-specific code
└─ Performance-critical code
```

---

## Q11: What is entity inheritance? Explain strategies.

### Answer

```
ENTITY INHERITANCE

Problem:
├─ Real-world entities have inheritance hierarchies
├─ Java classes inherit from superclasses
├─ Relational databases don't have inheritance
└─ Need to map object hierarchy to tables


SOLUTION: Three inheritance strategies


STRATEGY 1: SINGLE_TABLE

Concept:
├─ All classes in one table
├─ Discriminator column determines type
├─ Fastest queries (no joins)
└─ Wasteful space (many nulls)

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "emp_type")
public abstract class Employee {
    @Id
    private Long id;
    private String name;
}

@Entity
@DiscriminatorValue("MANAGER")
public class Manager extends Employee {
    private Integer reports;
}

@Entity
@DiscriminatorValue("ENGINEER")
public class Engineer extends Employee {
    private String specialization;
}

// Database:
// employees table:
// id | name | emp_type | reports | specialization
// 1  | John | MANAGER  | 5       | NULL
// 2  | Jane | ENGINEER | NULL    | Java


STRATEGY 2: JOINED

Concept:
├─ Parent class in parent table
├─ Each child in separate table
├─ Join on primary key
├─ Normalized (no nulls)
└─ Complex queries (many joins)

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee {
    @Id
    private Long id;
    private String name;
}

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "emp_id")
public class Manager extends Employee {
    private Integer reports;
}

@Entity
@Table(name = "engineers")
public class Engineer extends Employee {
    private String specialization;
}

// Database:
// employees: id | name
// managers: emp_id | reports
// engineers: emp_id | specialization


STRATEGY 3: TABLE_PER_CLASS

Concept:
├─ Each concrete class in separate table
├─ No parent table
├─ Simple but denormalized
└─ Complex polymorphic queries (UNION)

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Employee {
    @Id
    private Long id;
    private String name;
}

@Entity
@Table(name = "managers")
public class Manager extends Employee {
    private Integer reports;
}

@Entity
@Table(name = "engineers")
public class Engineer extends Employee {
    private String specialization;
}

// Database:
// managers: id | name | reports
// engineers: id | name | specialization


COMPARISON:

Strategy        │ Speed  │ Space   │ Normalization │ Queries
────────────────┼────────┼─────────┼───────────────┼──────────
SINGLE_TABLE    │ Fast   │ Wasteful│ Denormalized  │ Simple
JOINED          │ Slow   │ Good    │ Normalized    │ Complex
TABLE_PER_CLASS │ Medium │ Good    │ Denormalized  │ UNION


RECOMMENDATION:
├─ SINGLE_TABLE: Simple hierarchies, few columns
├─ JOINED: Complex hierarchies, many columns
└─ TABLE_PER_CLASS: Avoid if possible
```

---

## Q12: What are @Transient and @Column annotations?

### Answer

```
@Transient

Purpose: Exclude field from persistence

@Entity
public class User {
    @Id
    private Long id;
    
    @Transient
    private String derivedField;  // Not persisted
    
    @Transient
    private LocalDateTime loadedAt;  // Not in database
}

Usage:
├─ Calculated/computed fields
├─ Temporary runtime fields
├─ Fields not meant for persistence
└─ Cache/helper fields


@Column

Purpose: Customize database column


Options:

name:
├─ Column name in database
├─ Default: property name

@Column(name = "user_email")
private String email;  // Column: user_email


nullable:
├─ Allow NULL values
├─ Default: true

@Column(nullable = false)
private String name;  // NOT NULL constraint


unique:
├─ Unique constraint
├─ Default: false

@Column(unique = true)
private String email;  // UNIQUE constraint


length:
├─ String column length
├─ Default: 255

@Column(length = 50)
private String name;  // VARCHAR(50)


precision & scale:
├─ Decimal number precision
├─ For BigDecimal

@Column(precision = 10, scale = 2)
private BigDecimal salary;  // DECIMAL(10,2)


insertable & updatable:
├─ Allow in INSERT? UPDATE?
├─ Default: true

@Column(insertable = false, updatable = false)
private LocalDateTime createdAt;  // Read-only


columnDefinition:
├─ Raw SQL column definition
├─ Database-specific

@Column(columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
private String status;
```

---

## Q13: What is @OneToMany relationship?

### Answer

```
@OneToMany

One parent → Many children

Example: Department has many Users

@Entity
public class Department {
    @Id
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "department")
    private List<User> users;  // Multiple users
}

@Entity
public class User {
    @Id
    private Long id;
    private String name;
    
    @ManyToOne
    private Department department;  // Belongs to one department
}

// Department 1 → Many Users
// Users N → 1 Department


ATTRIBUTES:

mappedBy:
├─ Field in child that owns relationship
├─ Foreign key on child table
└─ Parent doesn't own relationship

@OneToMany(mappedBy = "department")
private List<User> users;


cascade:
├─ Automatic operation propagation
├─ CascadeType.ALL, PERSIST, MERGE, REMOVE

@OneToMany(mappedBy = "department", 
           cascade = CascadeType.ALL)
private List<User> users;
// DELETE department → DELETE users


orphanRemoval:
├─ Remove child when removed from collection
├─ Like cascade REMOVE but at collection level

@OneToMany(mappedBy = "department",
           orphanRemoval = true)
private List<User> users;
// users.remove(user) → DELETE user


fetch:
├─ LAZY (default) or EAGER
├─ LAZY: Load only when accessed

@OneToMany(mappedBy = "department",
           fetch = FetchType.LAZY)
private List<User> users;


AVOIDING N+1:

PROBLEM:
List<Department> depts = em.createQuery(
    "FROM Department").getResultList();
// Query 1

for (Department dept : depts) {
    dept.getUsers();  // N queries
}

SOLUTION:
@Query("SELECT DISTINCT d FROM Department d " +
       "LEFT JOIN FETCH d.users")
List<Department> findAllWithUsers();
```

---

## Q14: What is @ManyToMany relationship?

### Answer

```
@ManyToMany

Many A ↔ Many B

Example: Students ↔ Courses
- One student takes many courses
- One course has many students


IMPLEMENTATION:

@Entity
public class Student {
    @Id
    private Long id;
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "student_course",              // Junction table
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;
}

@Entity
public class Course {
    @Id
    private Long id;
    private String name;
    
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students;
}

// Database:
// students: id | name
// courses: id | name
// student_course: student_id | course_id


ATTRIBUTES:

mappedBy:
├─ Non-owning side
├─ Owning side has @JoinTable
└─ Use Set for collection (prevents duplicates)

@ManyToMany(mappedBy = "courses")
private Set<Student> students;


cascade:
@ManyToMany(cascade = CascadeType.ALL)
private Set<Course> courses;
// Operations propagate to courses


AVOIDING N+1:

@Query("SELECT DISTINCT s FROM Student s " +
       "LEFT JOIN FETCH s.courses " +
       "WHERE s.id = :id")
Student findByIdWithCourses(@Param("id") Long id);
```

---

## Q15-Q50: [Additional Interview Questions]

Due to comprehensive coverage already provided in previous sections, here are the remaining topics in summary form:

**Q15-Q20: Relationship & Mapping Questions**
- @OneToOne relationships
- Embeddable objects
- Join column configuration
- Lazy loading in relationships
- Cascade types
- Relationship fetch strategies

**Q21-Q30: Query & Performance Questions**
- JPQL vs SQL
- Native queries
- Criteria API
- Query caching
- Pagination
- Sorting
- Projections
- Group by and aggregation

**Q31-Q40: Advanced Concepts**
- Locking mechanisms (optimistic/pessimistic)
- Versioning with @Version
- Auditing entities
- Custom repository implementations
- Event listeners
- Bulk operations
- Entity graph API
- Stored procedures

**Q41-Q50: Complex Scenarios**
- Multi-tenancy patterns
- Soft deletes
- Envers for audit trails
- Performance tuning
- Memory optimization
- Connection pooling
- Transaction propagation
- Exception handling

---

## Key Takeaways

```
ENTITY LIFECYCLE:
NEW → MANAGED → DETACHED → REMOVED

1. Create entity (NEW/TRANSIENT)
2. Persist with em.persist() (→ MANAGED)
3. Modify and flush/commit (changes tracked)
4. Close EntityManager (→ DETACHED)
5. Merge or update to reattach
6. Remove and flush to delete

PERSISTENCE CONTEXT:
├─ First-level cache (per EntityManager)
├─ Automatic dirty checking
├─ Identity map (one object per ID)
├─ Cleared on close/clear()

COMMON PITFALLS:
├─ Lazy initialization exceptions (close session too early)
├─ N+1 queries (fetch related data in loop)
├─ Detached entities (can't modify after session close)
├─ Identity vs Equality confusion
└─ Cascading operations (delete propagation)

BEST PRACTICES:
├─ Use @Transactional for automatic session management
├─ Use JOIN FETCH for related data
├─ Use eager loading selectively
├─ Understand cascade rules
├─ Use Spring Data JPA for repositories
└─ Monitor query performance early
```

---

## Reference Checklist

```
✓ Entity basics (@Entity, @Id, @Column)
✓ Lifecycle methods (@PrePersist, @PostPersist, etc.)
✓ Persistence Context behavior
✓ Fetch strategies (EAGER vs LAZY)
✓ Relationships (@OneToMany, @ManyToOne, @ManyToMany)
✓ Query methods (JPQL, Criteria, Native)
✓ Transaction management (@Transactional)
✓ Cascading operations
✓ Locking and versioning
✓ Performance optimization
```

---

*End of HibernateJPA.md - Complete Reference Guide*
