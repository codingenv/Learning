# Java Interview Questions and Answers
## A 15-Year Veteran's Deep Dive (Java 8 through Java 21)

> *All answers reflect production experience from 15 years of building enterprise Java systems. I prioritise the "why" over the "what" — knowing syntax is table stakes; understanding trade-offs is what separates senior engineers.*

---

## Table of Contents

**Part 1: Core Java Fundamentals**
1. [Object-Oriented Programming Concepts](#1-object-oriented-programming-concepts)
2. [Java Memory Management and Garbage Collection](#2-java-memory-management-and-garbage-collection)
3. [Collections Framework](#3-collections-framework)
   - [Q16: HashMap internals — load factor, hash collisions](#q16-hashmap-internals--load-factor-hash-collisions)
   - [Q16 Extended: HashMap Internals — Complete Deep Dive](#q16-extended-hashmap-internals--complete-deep-dive)
   - [Q20A: HashMap — Complete Interview Deep Dive (16+ years level)](#q20a-hashmap--complete-interview-deep-dive)
   - [Q20B: HashSet — How it uses HashMap internally](#q20b-hashset--how-it-uses-hashmap-internally)
   - [Q20C: ConcurrentHashMap — Internal mechanics and all interview questions](#q20c-concurrenthashmap--internal-mechanics)
   - [Q20D: WeakHashMap — When and why to use it](#q20d-weakhashmap--when-and-why)
4. [Multithreading and Concurrency](#4-multithreading-and-concurrency)
5. [Exception Handling](#5-exception-handling)
6. [Generics](#6-generics)
7. [Functional Programming and Lambdas](#7-functional-programming-and-lambdas)
8. [Java 9+ Language Features](#8-java-9-language-features)

**Part 2: Advanced Java Concepts**
9. [Spring Framework and Dependency Injection](#9-spring-framework-and-dependency-injection)
10. [REST APIs and Web Services](#10-rest-apis-and-web-services)
11. [Database and SQL](#11-database-and-sql)
12. [Performance Optimization and Profiling](#12-performance-optimization-and-profiling)
13. [Design Patterns](#13-design-patterns)
14. [Testing and Quality Assurance](#14-testing-and-quality-assurance)
15. [Microservices and Distributed Systems](#15-microservices-and-distributed-systems)

**Part 3: Java Version Evolution**
16. [Java 8 Features Deep Dive](#16-java-8-features-deep-dive)
17. [Java 9–11 Features](#17-java-911-features)
18. [Java 12–16 Features](#18-java-1216-features)
19. [Java 17 and 21 — LTS Deep Dive](#19-java-17-and-21--lts-deep-dive)
20. [Future Java and Best Practices](#20-future-java-and-best-practices)

**Part 4: Real-World Scenarios**
21. [System Design Interview Questions](#21-system-design-interview-questions)
22. [Production Debugging and Troubleshooting](#22-production-debugging-and-troubleshooting)
23. [Code Quality and Best Practices](#23-code-quality-and-best-practices)
24. [Interview Soft Skills and Communication](#24-interview-soft-skills-and-communication)

---

# Part 1: Core Java Fundamentals

---

## 1. Object-Oriented Programming Concepts

---

### Q1: What are the four pillars of OOP? Explain each with real examples.

**The four pillars are Encapsulation, Inheritance, Polymorphism, and Abstraction.**

**1. Encapsulation** — Bundling data (fields) and behaviour (methods) into a class, and restricting direct access to internal state. In practice this is achieved via private fields + public getters/setters.

```java
public class BankAccount {
    private double balance;  // hidden from outside world

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.balance += amount;
    }

    public double getBalance() { return balance; }
    // No public setter for balance — only controlled mutation via deposit/withdraw
}
```

The invariant (`balance >= 0`) is always enforced. Without encapsulation, any caller could write `account.balance = -1000`.

**2. Inheritance** — Reusing and extending behaviour from a parent class. Enables the IS-A relationship.

```java
public abstract class Vehicle {
    protected int speed;
    public abstract void accelerate();
    public void brake() { speed = 0; }
}

public class Car extends Vehicle {
    @Override
    public void accelerate() { speed += 10; }
}
```

**3. Polymorphism** — One interface, multiple implementations. A `Vehicle` reference can point to a `Car`, `Truck`, or `Motorcycle` and dispatch the correct `accelerate()` at runtime.

```java
List<Vehicle> fleet = List.of(new Car(), new Truck(), new Motorcycle());
fleet.forEach(Vehicle::accelerate);  // correct implementation called for each type
```

**4. Abstraction** — Hiding implementation complexity behind a clean interface. The user of `BankAccount` doesn't need to know how balance is stored (double, BigDecimal, in a database row). They only interact with `deposit()`, `withdraw()`, `getBalance()`.

---

### Q2: Explain encapsulation. Why is it important?

Encapsulation is more than just making fields private. It's about **controlling invariants and coupling**.

**Why it matters in production:**
- **Invariant protection:** Logic that enforces business rules lives in one place. When `setAge(-5)` is called, the setter validates and throws — callers don't scatter validation logic.
- **Reduced coupling:** Internals can change (e.g., from `double` to `BigDecimal` for currency) without breaking callers.
- **Testability:** Encapsulated classes have clear seams for unit testing.

```java
// BAD — anemic model, no encapsulation
public class Order {
    public List<Item> items;  // public mutable list
    public double total;      // public mutable field — no consistency guarantee
}

// GOOD — encapsulated
public class Order {
    private final List<Item> items = new ArrayList<>();
    private double total;

    public void addItem(Item item) {
        Objects.requireNonNull(item);
        items.add(item);
        total += item.getPrice();  // total stays consistent
    }

    public double getTotal() { return total; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
}
```

*Production pitfall:* Returning mutable internal collections breaks encapsulation. Always return unmodifiable views or defensive copies.

---

### Q3: What is inheritance? Why doesn't Java support multiple inheritance of classes?

Inheritance lets a subclass acquire the fields and methods of a superclass. Java supports **single inheritance of classes** but **multiple inheritance of types** (interfaces).

**The Diamond Problem** is why multiple class inheritance is excluded:

```
         Animal
        /      \
     Dog        Cat
        \      /
         ??DogCat??

If Dog.speak() returns "Woof" and Cat.speak() returns "Meow",
which speak() does DogCat inherit? Ambiguous.
```

Java resolves this for interfaces via **default method resolution rules** (Java 8+): explicit override > most specific interface > compile error.

```java
interface A { default void greet() { System.out.println("A"); } }
interface B { default void greet() { System.out.println("B"); } }

class C implements A, B {
    @Override
    public void greet() { A.super.greet(); }  // must explicitly choose
}
```

**When NOT to use inheritance:** Prefer composition when the IS-A relationship is not semantically true. The classic mistake is extending `ArrayList` to make a `Stack` — now `Stack` exposes all 40+ `ArrayList` methods including `add(int index, E element)` which violates stack semantics.

---

### Q4: What is polymorphism? Compile-time vs runtime?

**Polymorphism = "many forms"**. A single method name behaves differently based on context.

**Compile-time (Static) — Method Overloading:**
```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public String add(String a, String b) { return a + b; }
}
// Resolved at compile time based on argument types
```

**Runtime (Dynamic) — Method Overriding:**
```java
class Shape { public double area() { return 0; } }
class Circle extends Shape {
    private double r;
    public Circle(double r) { this.r = r; }
    @Override public double area() { return Math.PI * r * r; }
}
class Rectangle extends Shape {
    private double w, h;
    public Rectangle(double w, double h) { this.w = w; this.h = h; }
    @Override public double area() { return w * h; }
}

Shape s = new Circle(5);
s.area();  // calls Circle.area() at runtime — dynamic dispatch via vtable
```

*Production insight:* Dynamic dispatch has a near-zero overhead since JVM JIT inlines monomorphic call sites. Avoid the antipattern of using `instanceof` chains instead of polymorphism.

```java
// ANTI-PATTERN
if (shape instanceof Circle c) { return Math.PI * c.getR() * c.getR(); }
else if (shape instanceof Rectangle r) { return r.getW() * r.getH(); }

// CORRECT — open for extension without modification
shapes.forEach(s -> System.out.println(s.area()));
```

---

### Q5: Abstract classes vs interfaces. When to use which?

| | Abstract Class | Interface |
|---|---|---|
| **Can have state** | Yes (instance fields) | No (only `static final`) |
| **Constructor** | Yes | No |
| **Method implementations** | Yes (abstract + concrete) | Yes (default/static since Java 8) |
| **Multiple inheritance** | No | Yes |
| **Access modifiers** | Any | `public` (implicitly) |

**Decision rule from 15 years of production code:**
- **Interface:** Define a contract/capability that can be applied to unrelated classes (`Serializable`, `Comparable`, `AutoCloseable`). Also when multiple inheritance is needed.
- **Abstract class:** When sharing implementation and state across a hierarchy (`AbstractList`, `AbstractHttpServlet`). When you need a template method pattern.

```java
// Abstract class: shares state + template method
public abstract class DataProcessor {
    private final String name;  // shared state
    public DataProcessor(String name) { this.name = name; }

    public final void process(Data data) {  // template method — final, can't override
        validate(data);
        transform(data);
        persist(data);
    }
    protected abstract void validate(Data data);
    protected abstract void transform(Data data);
    private void persist(Data data) { /* shared impl */ }
}

// Interface: pure contract, no state
public interface Cacheable {
    String getCacheKey();
    Duration getTtl();
}
```

*Java 8+ blur:* Default methods make interfaces more powerful, but they still can't hold instance state. When in doubt: start with an interface; migrate to abstract class only when shared state becomes necessary.

---

### Q6: Explain the Liskov Substitution Principle (LSP).

**"Objects of a subtype must be substitutable for objects of the supertype without breaking correctness."** — Barbara Liskov, 1987.

**LSP violation — the classic example:**
```java
// Appears logical: Square IS-A Rectangle
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    @Override public void setWidth(int w)  { this.width = this.height = w; }  // breaks LSP
    @Override public void setHeight(int h) { this.width = this.height = h; }
}

// Caller that works for Rectangle BREAKS for Square:
void doubleWidth(Rectangle r) {
    r.setWidth(r.width * 2);
    // Expected: area doubles. For Square: width AND height double → area quadruples!
}
```

**LSP-compliant fix:**
```java
interface Shape { int area(); }
class Rectangle implements Shape { /* own setWidth/setHeight */ }
class Square implements Shape { /* own setSize */ }
// No inheritance relationship — substitutability is preserved
```

*Production impact:* LSP violations surface as unexpected `ClassCastException` or incorrect behaviour when code processes a `List<Animal>` and one implementation behaves differently. Always test subclasses with the same test suite as the parent.

---

### Q7: Composition over inheritance.

Inheritance creates tight coupling between parent and child. Composition lets you assemble behaviour from pluggable pieces.

```java
// INHERITANCE — tight coupling
class LoggingEmailService extends EmailService {
    @Override public void send(Email email) {
        log.info("Sending: " + email.getSubject());
        super.send(email);
    }
    // Now LoggingEmailService is forever coupled to EmailService's internal impl
}

// COMPOSITION — loose coupling
class LoggingEmailService implements EmailService {
    private final EmailService delegate;
    private final Logger log;

    public LoggingEmailService(EmailService delegate, Logger log) {
        this.delegate = delegate;
        this.log = log;
    }

    @Override public void send(Email email) {
        log.info("Sending: " + email.getSubject());
        delegate.send(email);
    }
}
// Can wrap ANY EmailService implementation (SMTP, SES, SendGrid) — open/closed
```

*Real-world usage:* Java's own `BufferedInputStream` wraps any `InputStream` — composition makes it work with file streams, network streams, byte array streams without a deep inheritance hierarchy.

---

## 2. Java Memory Management and Garbage Collection

---

### Q8: Explain the Java memory model: heap, stack, metaspace.

```
JVM MEMORY LAYOUT
═══════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────────────────────────┐
  │                        HEAP                             │
  │  ┌─────────────────────┐  ┌──────────────────────────┐  │
  │  │   Young Generation  │  │    Old Generation        │  │
  │  │  ┌───┐ ┌───┐ ┌───┐  │  │   (Tenured Space)        │  │
  │  │  │ E │ │S0 │ │S1 │  │  │   Long-lived objects     │  │
  │  │  │den│ │   │ │   │  │  │   survive here           │  │
  │  │  └───┘ └───┘ └───┘  │  └──────────────────────────┘  │
  │  └─────────────────────┘                                │
  └─────────────────────────────────────────────────────────┘

  ┌───────────────────┐  ┌─────────────────────────────────┐
  │     STACK         │  │         METASPACE               │
  │  Per-thread        │  │  Class metadata, method bytecode│
  │  Stack frames      │  │  Static variables, interned str │
  │  Local variables   │  │  (replaced PermGen in Java 8)   │
  │  Method call chain │  └─────────────────────────────────┘
  └───────────────────┘

  ┌─────────────────────────────────────────────────────────┐
  │                CODE CACHE (JIT compiled code)           │
  └─────────────────────────────────────────────────────────┘
```

- **Eden Space:** New object allocation happens here (cheap bump-pointer allocation).
- **Survivor spaces (S0/S1):** Objects that survive a minor GC are copied between S0 and S1. After N survivals (default 15), they promote to Old Gen.
- **Old Generation:** Long-lived objects. Full GC here is expensive (Stop-the-World).
- **Metaspace:** Unbounded by default (unlike PermGen, which had a fixed 256MB — a common cause of `OutOfMemoryError: PermGen space` pre-Java 8).
- **Stack:** Per-thread, holds stack frames. Each frame holds local variables, operand stack, return address. `StackOverflowError` = deep recursion.

---

### Q9: How does garbage collection work in Java?

GC reclaims heap memory for objects that are **no longer reachable** from any GC root (thread stacks, static fields, JNI references).

**Mark-Sweep-Compact (conceptual):**
```
1. MARK:    Walk all GC roots, mark all reachable objects
2. SWEEP:   Free memory of unreachable objects
3. COMPACT: Move remaining objects together to eliminate fragmentation
```

**Minor GC (Young Gen — fast, milliseconds):**
```
Allocation fails in Eden →
  Walk Eden + S0 live objects →
  Copy survivors to S1 + promote aged objects to Old Gen →
  Clear Eden + S0 →
  Swap S0/S1 labels
```

**Major/Full GC (Old Gen — slow, hundreds of ms to seconds):**
- Triggered when Old Gen fills up
- Stop-the-World pause affects all application threads
- G1GC tries to do incremental collection to minimise pause

---

### Q10: Explain GC algorithms: Serial, Parallel, G1GC, ZGC, Shenandoah.

| GC | Threads | Pause | Best For |
|---|---|---|---|
| **Serial** | 1 | High | Single-core, embedded, tiny heaps |
| **Parallel** | Multi | Medium | Throughput-focused batch jobs |
| **G1GC** | Multi | Low (~200ms) | General purpose, heap 4GB-16GB (default Java 9+) |
| **ZGC** | Multi | Ultra-low (<1ms) | Large heaps (TB), latency-critical services (Java 15+ production) |
| **Shenandoah** | Multi | Ultra-low (<1ms) | Same as ZGC, Red Hat's alternative |

**G1GC — Region-Based Heap:**
```
G1 divides heap into equal-sized regions (~2MB each)
Regions are dynamically assigned as Eden, Survivor, Old, or Humongous

Mixed collection: collects highest-garbage Old regions first ("garbage first")
Concurrent marking: runs alongside application threads
Evacuation pause: STW, copies live objects to free regions
```

**ZGC (Java 21 — generational ZGC is the new default candidate):**
- Almost all work is concurrent — only a few milliseconds STW for root scanning
- Uses load barriers (code injected at every object field read) to track object moves
- Handles 8MB to 16TB heaps with sub-millisecond pauses

**Choosing a GC:**
```
Batch/throughput job (no latency SLA) → Parallel GC (-XX:+UseParallelGC)
Web service (heap < 8GB, p99 < 200ms)  → G1GC (default)
Low-latency service (p99 < 10ms)        → ZGC (-XX:+UseZGC) or Shenandoah
```

---

### Q11: Can Java have memory leaks? Provide examples.

Yes. A Java memory leak is when objects are **reachable but never used**. GC cannot collect reachable objects.

**Example 1: Static collection accumulating data**
```java
public class SessionManager {
    private static final Map<String, UserSession> sessions = new HashMap<>();

    public void addSession(String id, UserSession s) {
        sessions.put(id, s);  // sessions grow forever — no eviction
    }
    // FIX: use a cache with TTL (Caffeine, Guava Cache) or WeakHashMap
}
```

**Example 2: Listener/callback not deregistered**
```java
public class EventBus {
    private List<EventListener> listeners = new ArrayList<>();
    public void register(EventListener l) { listeners.add(l); }
    // If unregister() is never called, listeners hold strong references
    // to their enclosing objects — e.g., an entire UI component graph
}
```

**Example 3: Inner class holding outer class reference**
```java
class Outer {
    byte[] bigData = new byte[1024 * 1024];

    Runnable getTask() {
        return new Runnable() {  // anonymous inner class holds implicit ref to Outer
            @Override public void run() { /* doesn't use bigData */ }
        };
    }
    // FIX: use static nested class or lambda (lambdas don't capture outer class if not needed)
}
```

**Example 4: ThreadLocal not removed**
```java
static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[64 * 1024]);

// In a thread pool, threads are reused — ThreadLocal survives the request!
// Each pooled thread holds a 64KB buffer forever
// FIX: always call BUFFER.remove() after the request completes
```

---

### Q12: Weak, Soft, and Phantom references. When to use each?

```
REFERENCE STRENGTH (strongest → weakest)
Strong > Soft > Weak > Phantom

Strong:  obj = new Object()         — GC never collects while reachable
Soft:    new SoftReference<>(obj)   — GC collects when memory is low (cache use)
Weak:    new WeakReference<>(obj)   — GC collects at next minor GC
Phantom: new PhantomReference<>(obj)— GC collects after finalization (cleanup hooks)
```

```java
// Weak reference — canonical use: caches, canonicalizing maps
WeakHashMap<Session, Data> sessionCache = new WeakHashMap<>();
// When Session object has no other strong refs, entry is automatically removed

// Soft reference — memory-sensitive cache
SoftReference<BufferedImage> imageCache = new SoftReference<>(loadImage("logo.png"));
// JVM clears this before throwing OutOfMemoryError → safe in-memory image cache
BufferedImage img = imageCache.get();
if (img == null) img = loadImage("logo.png");  // reload on cache miss

// Phantom reference — resource cleanup (Java 9+ Cleaner is preferred)
Cleaner cleaner = Cleaner.create();
cleaner.register(resource, () -> resource.closeNativeHandle());
```

---

### Q13: GC tuning — key JVM flags.

```bash
# G1GC tuning
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200         # target max pause (G1 adapts to meet this)
-XX:G1HeapRegionSize=8m          # region size (1-32MB, power of 2)
-XX:G1NewSizePercent=20          # min young gen % of heap
-XX:G1MaxNewSizePercent=60       # max young gen % of heap
-XX:ParallelGCThreads=8          # GC worker threads
-XX:ConcGCThreads=4              # concurrent marking threads

# ZGC (Java 15+ production)
-XX:+UseZGC
-XX:+ZGenerational               # Java 21 generational ZGC (best option)
-XX:SoftMaxHeapSize=12g          # ZGC releases memory back to OS

# Logging
-Xlog:gc*:file=/var/log/app/gc.log:time,uptime:filecount=10,filesize=50m

# Sizing
-Xms4g -Xmx4g                   # equal Xms=Xmx to prevent heap resizing pauses
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m
```

**Golden rule from production:** Set `-Xms` equal to `-Xmx` to prevent the JVM from repeatedly growing the heap (expensive `mmap` calls). For microservices, tune `MaxGCPauseMillis` first; reduce heap if pause SLA is tight.

---

## 3. Collections Framework

---

### Q14: Explain the Collections hierarchy.

```
java.lang.Iterable
    └── java.util.Collection
            ├── List         (ordered, allows duplicates)
            │     ├── ArrayList
            │     ├── LinkedList
            │     └── Vector (legacy, synchronized)
            │
            ├── Set          (no duplicates)
            │     ├── HashSet
            │     ├── LinkedHashSet (insertion-order)
            │     └── TreeSet (sorted)
            │
            └── Queue        (FIFO / priority)
                  ├── LinkedList
                  ├── ArrayDeque
                  └── PriorityQueue

java.util.Map (key-value, NOT Collection)
    ├── HashMap
    ├── LinkedHashMap (insertion-order)
    ├── TreeMap (sorted by key)
    ├── ConcurrentHashMap
    └── WeakHashMap
```

---

### Q15: ArrayList vs LinkedList.

| Operation | ArrayList | LinkedList |
|---|---|---|
| `get(i)` | **O(1)** | O(n) |
| `add(end)` | **O(1) amortised** | O(1) |
| `add(middle)` | O(n) | O(n) (find) + O(1) (link) |
| `remove(i)` | O(n) | O(n) (find) + O(1) (unlink) |
| Memory | Compact (array) | 2 pointers per node (~24 bytes overhead) |
| Cache locality | **Excellent** | Poor (nodes scattered in heap) |

**Production reality:** Use `ArrayList` in 95% of cases. `LinkedList` is almost never faster in practice due to poor cache locality. The only genuine use case for `LinkedList` is as a **Deque** where you add/remove from both ends — even then, `ArrayDeque` is faster.

```java
// Prefer ArrayDeque over LinkedList for queue/deque operations
Deque<Task> workQueue = new ArrayDeque<>();  // cache-friendly, no boxing overhead
workQueue.addLast(task);
Task next = workQueue.removeFirst();
```

---

### Q16: HashMap internals — load factor, hash collisions.

**Internal structure (Java 8+):**
```
HashMap = array of buckets (default capacity 16)
Each bucket holds:
  - null (empty)
  - single Entry (no collision)
  - LinkedList of Entries (collision, ≤ 8 entries)
  - Red-Black Tree (treeified when bucket size > 8, capacity > 64)

Treeification prevents O(n) worst case on hash collision attacks
Before Java 8: always linked list → DoS possible via hash flooding
```

**Load factor:**
```
load factor = n / capacity  (default = 0.75)
When load factor exceeds threshold → resize (double capacity + rehash all entries)

Low load factor (0.25): fewer collisions, 4x memory usage
High load factor (0.90): more collisions, less memory
0.75 is the empirical sweet spot (Poisson distribution analysis)
```

```java
// Pre-size HashMap when final size is known — avoids rehashing
int expectedEntries = 10_000;
Map<String, Order> orders = new HashMap<>(expectedEntries * 4 / 3 + 1);
// capacity = expectedEntries / loadFactor = expectedEntries * 4/3

// Java 19+ convenience method
Map<String, Order> orders = HashMap.newHashMap(expectedEntries);
```

**Good `hashCode()` contract:**
```java
@Override
public int hashCode() {
    return Objects.hash(id, type, createdAt);  // use Objects.hash for composite keys
}

// CRITICAL: if a.equals(b) then a.hashCode() == b.hashCode()
// BUT NOT the reverse (collisions are allowed, just costly)
```

---

### Q17: HashMap vs LinkedHashMap vs TreeMap.

| | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| **Order** | None | Insertion (or access) | Sorted by key |
| `get/put` | **O(1)** | **O(1)** | O(log n) |
| `min/max key` | No | No | `firstKey()/lastKey()` |
| **Use case** | General cache | LRU cache, ordered output | Range queries, sorted iteration |

```java
// LRU cache using LinkedHashMap (access-ordered)
class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private final int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // true = access-order
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

// Range query with TreeMap
TreeMap<LocalDate, StockPrice> prices = new TreeMap<>();
// Get all prices in the last 7 days:
prices.subMap(LocalDate.now().minusDays(7), true, LocalDate.now(), true);
```

---

### Q18: ConcurrentHashMap vs synchronized HashMap — Complete Deep Dive

---

#### The Problem With synchronized HashMap

Imagine a busy server where 100 threads are reading/writing a shared map at the same time.

```java
// Option 1: plain HashMap — NOT thread-safe
Map<String, Integer> map = new HashMap<>();
// Two threads putting simultaneously can corrupt internal state:
// - Both resize at the same time → circular linked list → infinite loop (Java 7)
// - Lost updates, wrong values (Java 8+)

// Option 2: Collections.synchronizedMap — safe but slow
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
```

**How `synchronizedMap` works internally:**
```java
// Every single method is wrapped with synchronized(mutex):
public V get(Object key) {
    synchronized(mutex) { return map.get(key); }
}
public V put(K key, V value) {
    synchronized(mutex) { return map.put(key, value); }
}
// mutex = the map itself (by default)
```

**The problem:**

```
Thread-1  get("A") ──► LOCKS entire map ──► reads ──► UNLOCKS
Thread-2  get("B") ──►                      WAITS until T1 done
Thread-3  put("C") ──►                      WAITS until T2 done
Thread-4  get("D") ──►                      WAITS until T3 done
...
```

Even 100 threads doing **reads only** are blocked one at a time. A read should never block another read — there's no state change. But `synchronizedMap` doesn't know the difference.

---

#### ConcurrentHashMap — The Solution

The core idea: **different parts of the map can be accessed simultaneously**.

---

#### Java 7: Segment-Based Locking

```
ConcurrentHashMap (Java 7)
═══════════════════════════════

Segment[0]  Segment[1]  Segment[2]  ...  Segment[15]
   🔒           🔒           🔒                🔒
[bucket]    [bucket]    [bucket]         [bucket]
[bucket]    [bucket]    [bucket]         [bucket]
[bucket]    [bucket]    [bucket]         [bucket]

Default: 16 segments → 16 independent locks
```

- Map is divided into 16 **Segments** (default concurrencyLevel=16).
- Each Segment is essentially a mini `HashMap` with its own `ReentrantLock`.
- `put("A", ...)` → locks only the segment that "A" hashes into.
- `put("B", ...)` → if "B" hashes into a different segment → runs **simultaneously**.
- **16× better throughput** than synchronizedMap under high concurrency.
- `get()` was mostly lock-free (volatile reads).

**Limitation of Java 7:** Still 16 locks. If all writes happen to hash into the same segment, you're back to serial writes.

---

#### Java 8: Bucket-Level Locking (Current Implementation)

Java 8 completely rewrote ConcurrentHashMap. No more Segments. The locking granularity is now **one lock per bucket** — potentially thousands of locks.

```
ConcurrentHashMap (Java 8)
═══════════════════════════════════════════════════════

Node[] table  (default 16 buckets, grows as needed)

 [0]   [1]   [2]   [3]   [4]   [5]   [6]   [7] ...
  │     │     │     │     │
  A     B     C     ∅     D→E→F  ← collision chain
  ↓
  G                        ↓
  ↓               (this bucket is treeified
  ∅               when chain length > 8)

Thread-1 writes to bucket[2] → locks bucket[2] head node only
Thread-2 writes to bucket[5] → locks bucket[5] head node only
Thread-3 reads  from bucket[0] → NO LOCK at all (volatile read)
All three run simultaneously ✓
```

---

#### The Three Locking Strategies in Java 8

**Case 1: Bucket is empty → CAS (no lock at all)**

```java
// CAS = Compare And Swap — a single CPU instruction
// "If the current value at this memory address is X, set it to Y atomically"

if (bucket[index] == null) {
    // Try to insert with CAS — no synchronized block needed
    if (casTabAt(table, index, null, newNode)) {
        break;  // CAS succeeded — done!
    }
    // If CAS failed, another thread inserted between our check and swap → retry
}
```

```
Thread-1: bucket[3] == null? YES → CAS(bucket[3], null, NodeA) → SUCCESS → done
Thread-2: bucket[7] == null? YES → CAS(bucket[7], null, NodeB) → SUCCESS → done
Both complete without ANY lock acquisition.
```

**Case 2: Bucket has nodes → synchronized on the head node**

```java
synchronized (firstNodeInBucket) {
    // traverse linked list or tree
    // find key, update value OR append new node
}
// Only threads touching THIS SPECIFIC BUCKET are serialized.
// All other 15 (or 15,000) buckets remain fully accessible.
```

**Case 3: Read (get) → completely lock-free**

```java
// Node fields are volatile:
volatile V val;
volatile Node<K,V> next;

// get() just reads volatile fields — no synchronization needed
// Java memory model guarantees: volatile write happens-before volatile read
// So any thread that wrote a value, and then another thread reads it → sees the latest value
public V get(Object key) {
    int h = spread(key.hashCode());
    Node<K,V> e = tabAt(table, (table.length - 1) & h);  // volatile read
    while (e != null) {
        if (e.hash == h && key.equals(e.key)) return e.val;  // volatile read
        e = e.next;  // volatile read
    }
    return null;
}
// Zero locks. Any number of threads can read simultaneously.
```

---

#### Visual: What Happens Under Concurrent Load

```
10 threads: T1-T5 doing get(), T6-T10 doing put() on different keys

T1  get("apple")  ──► reads bucket[2]  ──► no lock  ──► DONE instantly
T2  get("banana") ──► reads bucket[7]  ──► no lock  ──► DONE instantly
T3  get("cherry") ──► reads bucket[11] ──► no lock  ──► DONE instantly
T4  get("date")   ──► reads bucket[4]  ──► no lock  ──► DONE instantly
T5  get("elderb") ──► reads bucket[2]  ──► no lock  ──► DONE instantly

T6  put("fig",1)  ──► bucket[9] empty  ──► CAS       ──► DONE (no lock)
T7  put("grape",1)──► bucket[3] empty  ──► CAS       ──► DONE (no lock)
T8  put("kiwi",1) ──► bucket[9] exists ──► lock[9]   ──► DONE
T9  put("lime",1) ──► bucket[3] exists ──► lock[3]   ──► DONE
T10 put("mango",1)──► bucket[9] exists ──► WAITS for T8 to release lock[9]

All 10 threads run concurrently except T10 waits briefly for T8.
In synchronizedMap: all 10 would be fully serialized.
```

---

#### How size() Works — The LongAdder Trick

```java
// Can't use a single int counter — incrementing it would need global locking

// ConcurrentHashMap uses a LongAdder-like approach:
// baseCount (volatile long) + CounterCell[] (array of longs, one per CPU core)

// On increment:
// 1. Try to CAS baseCount. If CAS succeeds, done.
// 2. If CAS fails (contention), hash this thread to a CounterCell and increment it.

// On size():
// sum = baseCount + sum(all CounterCells)

// WHY: under high contention, 100 threads incrementing ONE counter = 99 retries per op
// With 8 CounterCells: ~12 threads per cell → much less contention
// Trade-off: size() is approximate (counts are spread across cells, no atomic snapshot)
```

---

#### Concurrent Resize — Cooperative Transfer

```
Normal HashMap resize: one thread does ALL the work (stops the world for that thread)

ConcurrentHashMap resize: ALL threads help!

When resize is triggered:
1. New table (double size) is created
2. A "transfer index" tracks which buckets still need to be moved
3. The resizing thread picks up a chunk of buckets to transfer
4. Other threads that try to write during resize detect a ForwardingNode sentinel
   (a special node with hash = MOVED = -1)
5. Those threads call helpTransfer() — they pick up their own chunk of buckets to move
6. Once all buckets are moved, the table reference is swapped atomically

Result: resize is parallel, no thread is fully blocked
A ForwardingNode in a bucket means: "this bucket is done, look in the new table"
```

---

#### ConcurrentHashMap vs synchronizedMap — Side by Side

```
OPERATION          synchronizedMap          ConcurrentHashMap
──────────────────────────────────────────────────────────────
get()              LOCKS entire map         Lock-free (volatile reads)
put() empty bucket LOCKS entire map         CAS (no lock)
put() non-empty    LOCKS entire map         Locks ONE bucket only
Iteration          Must sync externally     Weakly consistent, no lock
size()             Exact (locked)           Approximate (LongAdder)
null keys/values   Allowed                  NOT allowed
Null semantics     get(k)==null ambiguous   Always means "key absent"
Throughput (reads) Serialized               Fully parallel
Throughput (writes) Serialized             Parallel across buckets
Compound ops       Need external sync       putIfAbsent/compute/merge
```

---

#### Interview Answer — The 60-Second Version

*"The core difference is lock granularity. `synchronizedMap` wraps every operation in a single lock on the entire map — so all threads are serialized, even concurrent reads. `ConcurrentHashMap` uses three strategies: reads are completely lock-free using volatile fields, writes to empty buckets use CAS (a single CPU instruction, no lock), and writes to non-empty buckets lock only the head of that specific bucket. So if you have 100 buckets, 100 threads can write simultaneously with zero contention. In Java 7 this was done with 16 Segments; Java 8 eliminated Segments entirely and made it per-bucket. The trade-off is that `size()` is approximate, and null keys/values are banned because in a concurrent environment you can't distinguish 'key is absent' from 'key maps to null' without a compound check-then-get."*

---

### Q19: Comparable vs Comparator.

```java
// Comparable — natural order, baked into the class
public class Employee implements Comparable<Employee> {
    private String name;
    private int salary;

    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.salary, other.salary);  // natural: by salary
    }
}

// Comparator — external, multiple orderings, Java 8 composable
Comparator<Employee> byName    = Comparator.comparing(Employee::getName);
Comparator<Employee> bySalary  = Comparator.comparingInt(Employee::getSalary).reversed();
Comparator<Employee> combined  = bySalary.thenComparing(byName);

employees.sort(combined);

// Null-safe comparator
Comparator<Employee> safeByDept =
    Comparator.comparing(Employee::getDepartment, Comparator.nullsLast(Comparator.naturalOrder()));
```

*Rule of thumb:* Use `Comparable` for the single "natural" ordering (e.g., `String` by lexicographic order). Use `Comparator` for any alternative or context-specific orderings.

---

### Q20: Fail-fast vs fail-safe iterators.

**Fail-fast:** Throws `ConcurrentModificationException` if the collection is structurally modified during iteration (checks `modCount`).

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    if (s.equals("b")) list.remove(s);  // ConcurrentModificationException!
}

// SAFE: use Iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("b")) it.remove();  // safe — updates modCount
}

// Or Java 8+ removeIf:
list.removeIf(s -> s.equals("b"));
```

**Fail-safe:** Iterates over a copy (snapshot), no exception but may not see latest writes.

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    list.add("d");  // no exception — iterator operates on snapshot at time of iteration start
}
// list now has extra "d" entries but iterator didn't see them
// Best for: read-heavy, rarely-written collections (event listeners, observer lists)
```

---

## HashMap, HashSet, ConcurrentHashMap, and WeakHashMap — Complete Deep Dive

---

### Q20A: HashMap — Complete Interview Deep Dive

#### Internal Structure

```
HashMap = Node<K,V>[] table  (default capacity: 16)

Each bucket can hold:
  null               → empty slot
  single Node        → no collision
  LinkedList of Nodes → collision (bucket size ≤ 8)
  TreeNode (Red-Black Tree) → treeified when bucket size > 8 AND table size ≥ 64

Node structure:
  int    hash;   // cached hash, avoids recompute on resize
  K      key;
  V      value;
  Node   next;   // linked list pointer
```

#### How `put(K key, V value)` Works Step by Step

```java
// Step 1: hash the key
int h = key.hashCode();
int hash = h ^ (h >>> 16);   // XOR upper 16 bits into lower 16 (spread high bits)
// WHY: table index = hash & (capacity - 1). Without spreading, high bits are ignored.
// Keys with same lower 16 bits would all land in same bucket → clustering

// Step 2: compute bucket index
int index = hash & (capacity - 1);  // capacity always power-of-2, so this = hash % capacity

// Step 3: handle bucket
if (bucket[index] == null) {
    bucket[index] = new Node(hash, key, value, null);  // no collision, done
} else {
    // traverse chain: find existing key or append new node
    for (Node e = bucket[index]; e != null; e = e.next) {
        if (e.hash == hash && (e.key == key || key.equals(e.key))) {
            e.value = value;   // update existing key
            return;
        }
    }
    // key not found → append (Java 8: append to tail, not head)
    // if bucket size ≥ 8 → treeify
}

// Step 4: resize if needed
if (++size > threshold) resize();  // threshold = capacity * loadFactor (default 0.75)
// resize doubles capacity, rehashes all entries (expensive O(n) operation)
```

#### Resize (Rehash) — The Hidden Cost

```
Initial: capacity=16, threshold=12  (16 × 0.75)
After 12th put → resize to 32, threshold=24
After 24th put → resize to 64, threshold=48
...

Each resize:
  - Allocates new array double the size
  - Iterates all existing nodes
  - Recomputes bucket index for each (hash & newCapacity-1)
  - O(n) time — can cause latency spikes under load

PRE-SIZE when final size is known:
Map<String, Order> map = new HashMap<>(10_000 * 4/3 + 1);  // no resize up to 10K entries
// Java 19+:
Map<String, Order> map = HashMap.newHashMap(10_000);  // static factory, handles the math
```

#### Treeification — Why and When

```
Before Java 8: collision bucket was always a LinkedList
Problem: hash flooding attack — attacker crafts keys with identical hashCode()
         → all keys land in one bucket → O(1) becomes O(n) → DoS

Java 8 fix: when bucket size > 8 AND table capacity ≥ 64:
  LinkedList → Red-Black Tree (self-balancing BST)
  O(n) worst case → O(log n) worst case

When bucket size drops below 6 during removal: un-treeify back to linked list
Why 8/6 and not same threshold? Hysteresis — avoids thrashing between tree and list
```

#### `equals()` and `hashCode()` Contract — Critical

```java
// THE CONTRACT: if a.equals(b) → a.hashCode() == b.hashCode()
// Violation example:
class BrokenKey {
    String name;
    @Override public boolean equals(Object o) {
        return o instanceof BrokenKey bk && bk.name.equals(this.name);
    }
    // NO hashCode override → uses Object.hashCode() (identity-based)
}

Map<BrokenKey, String> map = new HashMap<>();
BrokenKey k1 = new BrokenKey("user1");
map.put(k1, "Alice");

BrokenKey k2 = new BrokenKey("user1");  // logically equal to k1
map.get(k2);  // returns NULL — k2 has different hashCode → different bucket!

// Correct implementation:
@Override public int hashCode() { return Objects.hash(name); }
// Now k1.hashCode() == k2.hashCode() && k1.equals(k2) → get(k2) returns "Alice"
```

#### All HashMap Interview Questions (16+ Year Level)

**Q: What is the default initial capacity and load factor of HashMap?**
- Default capacity: **16** (must be power of 2 for bitwise index calculation)
- Default load factor: **0.75** (empirically optimal — balances space vs time using Poisson distribution)
- Threshold = capacity × loadFactor = 16 × 0.75 = **12** (resize triggers at 12th entry)

**Q: Why must HashMap capacity always be a power of 2?**
- Index = `hash & (capacity - 1)`. When capacity is power of 2, `capacity - 1` is all 1s in binary.
- This makes `&` equivalent to `% capacity` but 5-10× faster (no division).
- If you pass `new HashMap<>(10)`, Java rounds up to 16 internally.

**Q: What happens when two keys have the same hashCode?**
- Called a **hash collision**. Both keys land in the same bucket.
- Java 8+: stored as a linked list (≤8 entries) or Red-Black Tree (>8 entries).
- `equals()` is used to distinguish the two keys within the bucket.

**Q: Can HashMap have null keys and null values?**
- **Yes** — one null key (stored in bucket 0), multiple null values.
- `HashTable` does NOT allow null keys or values (legacy, synchronized).
- `ConcurrentHashMap` does NOT allow null keys or values (ambiguity — can't distinguish "absent" from "value is null").

**Q: Is HashMap ordered?**
- No. Iteration order is undefined and can change on resize.
- Use `LinkedHashMap` for insertion order, `TreeMap` for sorted order.

**Q: Is HashMap thread-safe?**
- No. Concurrent modifications can cause:
  - Infinite loop (pre-Java 8, two threads resizing simultaneously → circular linked list)
  - Data loss (Java 8+, but still not safe)
- Use `ConcurrentHashMap` for thread safety.

**Q: What is the time complexity of HashMap operations?**

| Operation | Average | Worst Case (all keys same bucket) |
|---|---|---|
| `get` | O(1) | O(log n) with Java 8 treeification |
| `put` | O(1) | O(log n) |
| `remove` | O(1) | O(log n) |
| Iteration | O(n + capacity) | O(n + capacity) |

**Q: What is the difference between `get()` returning null vs key not present?**
```java
map.put("key", null);
map.get("key");       // returns null — key EXISTS with null value
map.get("other");     // returns null — key DOES NOT EXIST

// To distinguish:
map.containsKey("key");   // true
map.containsKey("other"); // false
map.getOrDefault("key", "default");   // returns null (value is null)
map.getOrDefault("other", "default"); // returns "default"
```

**Q: How do you iterate a HashMap? What's the most efficient way?**
```java
Map<String, Integer> map = new HashMap<>();

// 1. entrySet() — MOST EFFICIENT for key+value access
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + "=" + entry.getValue());
}

// 2. forEach (Java 8)
map.forEach((k, v) -> System.out.println(k + "=" + v));

// 3. keySet() — only if you need keys (avoids Entry object creation)
for (String key : map.keySet()) { ... }

// AVOID: map.keySet() then map.get(key) — O(1) per get but wastes time vs entrySet()
```

**Q: Explain `compute()`, `computeIfAbsent()`, `computeIfPresent()`, `merge()`**
```java
Map<String, List<String>> grouped = new HashMap<>();

// computeIfAbsent — create value only if key absent (great for multi-maps)
grouped.computeIfAbsent("fruits", k -> new ArrayList<>()).add("apple");
grouped.computeIfAbsent("fruits", k -> new ArrayList<>()).add("mango");
// grouped = {fruits=[apple, mango]}

// compute — update value for existing key
Map<String, Integer> wordCount = new HashMap<>();
wordCount.compute("hello", (k, v) -> v == null ? 1 : v + 1);

// merge — combine old and new value with a function (cleaner for counting)
wordCount.merge("hello", 1, Integer::sum);  // same as compute above, cleaner

// Real use: word frequency counter
String[] words = "the cat sat on the mat".split(" ");
Map<String, Long> freq = new HashMap<>();
for (String w : words) freq.merge(w, 1L, Long::sum);
// freq = {the=2, cat=1, sat=1, on=1, mat=1}
```

**Q: How would you sort a HashMap by value?**
```java
Map<String, Integer> scores = Map.of("Alice", 90, "Bob", 85, "Charlie", 95);

// Sort by value descending
List<Map.Entry<String, Integer>> sorted = scores.entrySet().stream()
    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
    .collect(Collectors.toList());
// Result: Charlie=95, Alice=90, Bob=85

// Or maintain insertion order with LinkedHashMap
Map<String, Integer> sortedMap = scores.entrySet().stream()
    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (e1, e2) -> e1,
        LinkedHashMap::new
    ));
```

**Q: What's the difference between `HashMap.put()` and `HashMap.putIfAbsent()`?**
```java
map.put("key", "new");           // always overwrites, returns old value
map.putIfAbsent("key", "new");   // only inserts if key absent, returns existing value
// putIfAbsent is NOT atomic in HashMap — use ConcurrentHashMap for thread safety
```

---

### Q20B: HashSet — How it Uses HashMap Internally

#### Internal Structure — HashSet IS a HashMap

```java
// HashSet source code (simplified):
public class HashSet<E> {
    private transient HashMap<E, Object> map;
    private static final Object PRESENT = new Object();  // dummy value

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;  // PRESENT is the same object for all entries
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }
    // Iteration: just iterates map.keySet()
}
```

**Key insight:** HashSet stores elements as **keys** of a HashMap. The value is always a single shared dummy object `PRESENT`. This means HashSet has the same performance characteristics and memory overhead as HashMap.

#### HashSet Interview Questions

**Q: How does HashSet ensure no duplicates?**
- Delegates to `HashMap.put(element, PRESENT)`.
- `put` checks `equals()` on existing keys in the same bucket.
- If `equals()` returns true, the key already exists → `put` returns old value (not null) → `add()` returns false.

**Q: What are the time complexities?**
- `add`, `remove`, `contains`: O(1) average, O(log n) worst case (same as HashMap)
- Iteration: O(n + capacity)

**Q: HashSet vs LinkedHashSet vs TreeSet?**

| | HashSet | LinkedHashSet | TreeSet |
|---|---|---|---|
| Order | None | Insertion order | Sorted (natural or Comparator) |
| `add/contains` | O(1) | O(1) | O(log n) |
| Backed by | HashMap | LinkedHashMap | Red-Black Tree (TreeMap) |
| Allows null | Yes (one) | Yes (one) | No (null can't be compared) |

```java
// TreeSet — sorted, range operations
TreeSet<Integer> set = new TreeSet<>(Set.of(5, 2, 8, 1, 9));
set.first();          // 1
set.last();           // 9
set.headSet(5);       // [1, 2]
set.tailSet(5);       // [5, 8, 9]
set.subSet(2, 8);     // [2, 5]
set.floor(6);         // 5 (largest element ≤ 6)
set.ceiling(6);       // 8 (smallest element ≥ 6)
```

**Q: Can you store mutable objects in a HashSet?**
```java
// DANGEROUS — mutating a key after insertion corrupts the Set
class Point {
    int x, y;
    @Override public int hashCode() { return Objects.hash(x, y); }
    @Override public boolean equals(Object o) { ... }
}

Set<Point> set = new HashSet<>();
Point p = new Point(1, 2);
set.add(p);
set.contains(p);  // true

p.x = 99;         // mutate → hashCode changes!
set.contains(p);  // FALSE — p is now in the wrong bucket

// Rule: only use immutable objects (String, Integer, records) as HashSet elements / HashMap keys
```

---

### Q20C: ConcurrentHashMap — Internal Mechanics

#### Java 7 vs Java 8 Architecture

```
JAVA 7 — Segment-based locking:
  ConcurrentHashMap = 16 Segments (default concurrency level)
  Each Segment = mini HashMap with its own ReentrantLock
  Lock granularity: 1 segment = 1/16 of the map
  put() locks the segment, get() is lock-free (volatile reads)

JAVA 8 — Bucket-level locking (current):
  ConcurrentHashMap = Node<K,V>[] table  (same as HashMap structure)
  Locking: synchronized on the FIRST node of each bucket (not a Segment)
  get(): completely lock-free (reads volatile fields)
  put(): CAS for empty bucket, synchronized(firstNode) for non-empty
  Concurrency: effectively one lock per bucket (thousands of locks vs 16)
```

#### How `put()` Works in Java 8 ConcurrentHashMap

```java
// Simplified ConcurrentHashMap.put() logic:

// Case 1: table not initialized → CAS to init
if (table == null) initTable();  // uses CAS to ensure only one thread initializes

// Case 2: bucket is empty → CAS insert (no lock needed)
if (bucket[index] == null) {
    if (casTabAt(table, index, null, new Node(hash, key, value))) {
        break;  // CAS succeeded → done, no lock held
    }
    // CAS failed → another thread inserted → retry loop
}

// Case 3: bucket is being resized (MOVED sentinel node)
if (fh == MOVED) helpTransfer(table, node);  // participate in resize

// Case 4: bucket has a node → lock the head node and traverse
synchronized (firstNode) {
    // traverse linked list or tree, insert/update
    // only this bucket is locked — all other buckets remain accessible
}
```

#### Why ConcurrentHashMap Doesn't Allow null

```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
map.put(null, "value");   // NullPointerException
map.put("key", null);     // NullPointerException

// WHY? Ambiguity problem:
// In a multi-threaded scenario:
String val = map.get("key");
if (val == null) {
    // Is "key" absent? OR does "key" map to null?
    // In HashMap: map.containsKey("key") tells you — but this is TWO operations
    // In ConcurrentHashMap: between get() and containsKey(), another thread could change state
    // So null is banned to eliminate ambiguity — absence is always unambiguous
}
```

#### ConcurrentHashMap — All Interview Questions

**Q: How does ConcurrentHashMap achieve thread safety without locking the entire map?**
- Reads (`get`, `containsKey`): completely lock-free using `volatile` fields on nodes
- Writes to empty bucket: lock-free using CAS (`compareAndSwapObject`)
- Writes to non-empty bucket: fine-grained `synchronized` on the bucket's head node only
- Result: different threads can read/write to different buckets simultaneously

**Q: Is `size()` accurate in ConcurrentHashMap?**
```java
// NO — size() is approximate under concurrent modification
// Uses a LongAdder-like mechanism: sum of base count + array of CounterCell values
// Returns a snapshot; by the time you read it, the real size may have changed

// For accurate count under concurrent use, don't rely on size()
// Use ConcurrentHashMap as a concurrent set where membership matters, not count
long count = map.mappingCount();  // returns long (Java 8+) — still approximate
```

**Q: What is the difference between `putIfAbsent()` and `computeIfAbsent()` in ConcurrentHashMap?**
```java
// putIfAbsent — always creates the value, only puts if key absent
// Value creation happens OUTSIDE the atomic operation
ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();
map.putIfAbsent("key", new ArrayList<>());  // creates ArrayList even if key exists!
// Then the created ArrayList is thrown away if key already present — wasteful

// computeIfAbsent — ATOMIC: only creates value if key is absent
map.computeIfAbsent("key", k -> new ArrayList<>());  // value created only if needed
// ALSO: holds the bucket lock during the entire compute — prevents duplicate creation
// This makes computeIfAbsent the correct choice for expensive value construction
```

**Q: How do you atomically update a value in ConcurrentHashMap?**
```java
ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<>();

// WRONG — not atomic (get + put are two separate operations)
counter.put("x", counter.getOrDefault("x", 0) + 1);

// CORRECT options:
counter.merge("x", 1, Integer::sum);          // atomic merge
counter.compute("x", (k, v) -> v == null ? 1 : v + 1);  // atomic compute

// For high-frequency counters, prefer LongAdder approach:
ConcurrentHashMap<String, LongAdder> freq = new ConcurrentHashMap<>();
freq.computeIfAbsent("word", k -> new LongAdder()).increment();
// LongAdder uses internal striping → lower contention than AtomicInteger
```

**Q: ConcurrentHashMap vs Collections.synchronizedMap(HashMap)?**

| | ConcurrentHashMap | synchronizedMap |
|---|---|---|
| Locking | Per-bucket (fine-grained) | Entire map (coarse) |
| `get()` | Lock-free | Blocks all others |
| Null keys/values | Not allowed | Allowed |
| `size()` | Approximate | Exact (but requires lock) |
| Iteration | Weakly consistent | Requires external sync for safe iteration |
| Performance | High throughput | Low throughput under contention |

```java
// synchronizedMap — must manually sync for compound operations:
Map<String, Integer> sync = Collections.synchronizedMap(new HashMap<>());
synchronized(sync) {
    if (!sync.containsKey("key")) sync.put("key", 1);  // check-then-act
}

// ConcurrentHashMap — compound ops are atomic, no external sync needed:
ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
chm.putIfAbsent("key", 1);   // atomic
```

**Q: What is a "weakly consistent" iterator in ConcurrentHashMap?**
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1); map.put("b", 2); map.put("c", 3);

// Iterating ConcurrentHashMap never throws ConcurrentModificationException
// But may or may not reflect modifications that happen AFTER the iterator was created:
for (String key : map.keySet()) {
    map.put("d", 4);      // safe — no exception
    map.remove("a");      // safe — but iterator may or may not visit "a" after this
}
// "weakly consistent" = reflects some but not necessarily all concurrent changes
```

**Q: How does ConcurrentHashMap resize?**
- It does a **concurrent transfer** — multiple threads can help move buckets simultaneously.
- A `ForwardingNode` (with special hash `MOVED`) is placed in each bucket after it's transferred.
- Other threads that attempt to write during resize detect `MOVED` and `helpTransfer()`.
- This avoids the blocking rehash that HashMap does.

**Q: When would you use ConcurrentHashMap over HashMap?**
- Any time multiple threads read or write the same map concurrently.
- Replacing a `HashMap` protected by a global lock — ConcurrentHashMap will perform better under contention.
- Building caches, counters, session stores, feature flags in multi-threaded services.

---

### Q20D: WeakHashMap — When and Why

#### How WeakHashMap Works

```java
// Regular HashMap:
Map<Object, String> map = new HashMap<>();
Object key = new Object();
map.put(key, "value");
key = null;   // key variable no longer points to the object
// BUT the HashMap still holds a STRONG reference to the key object
// The key object is NOT garbage collected — map holds it alive forever

// WeakHashMap:
Map<Object, String> wmap = new WeakHashMap<>();
Object key = new Object();
wmap.put(key, "value");
key = null;   // only the wmap entry holds a reference now — but it's a WEAK reference
System.gc();  // GC can collect the key object (and the entry disappears)
wmap.size();  // may be 0 — entry was automatically removed!
```

#### Internal Mechanism

```java
// WeakHashMap stores keys wrapped in WeakReference:
// WeakReference<K> weakKey = new WeakReference<>(key, referenceQueue);

// When GC collects the key:
//   1. GC clears the WeakReference (weakKey.get() returns null)
//   2. The WeakReference is enqueued in the ReferenceQueue
//   3. On next WeakHashMap operation (put/get/size), it polls the queue
//   4. Stale entries with collected keys are removed from the table

// This cleanup is LAZY — it happens on the next operation, not immediately on GC
```

#### Use Cases and Anti-Patterns

```java
// USE CASE 1: Canonical mapping / caching where key is the owner
// Classic example: per-object metadata (without modifying the object)
WeakHashMap<Widget, ButtonListener> listeners = new WeakHashMap<>();
listeners.put(widget, new ButtonListener(widget));
// When widget is GC'd (no other references), listener entry auto-removed
// Without WeakHashMap, listeners map would keep widget alive forever → memory leak

// USE CASE 2: Object identity caches
static final WeakHashMap<BigInteger, WeakReference<BigInteger[]>> cache = new WeakHashMap<>();

// USE CASE 3: Replacing equals-based comparison with identity-based cache
// (WeakHashMap uses identity equality == for keys when keys don't override equals/hashCode)

// COMMON ANTI-PATTERN: using String literals as keys
WeakHashMap<String, Object> bad = new WeakHashMap<>();
bad.put("constant", new Object());
// String literals are interned — they NEVER get GC'd (strong reference in string pool)
// This entry will NEVER be evicted → WeakHashMap provides zero benefit

// ANTI-PATTERN: WeakHashMap as thread-safe cache
// WeakHashMap is NOT thread-safe. Use Collections.synchronizedMap() or dedicated cache libs
Map<Key, Value> safe = Collections.synchronizedMap(new WeakHashMap<>());
// For production: use Caffeine with weakKeys()
Cache<Key, Value> cache = Caffeine.newBuilder().weakKeys().build();
```

#### WeakHashMap Interview Questions

**Q: When should you use WeakHashMap over HashMap?**
- When the map should not prevent its keys from being garbage collected.
- When the map's entries are only meaningful as long as the key object is alive elsewhere.
- Typical: per-object metadata, association tables, non-intrusive caches.
- Atypical: if you need a general-purpose cache, use Caffeine/Guava which give you LRU + TTL + WeakKeys together.

**Q: WeakHashMap vs SoftReference-based cache?**
```java
// WeakHashMap keys: collected at next GC (aggressive)
// SoftReference values: collected only when memory pressure is high (gentle)

// For a memory-sensitive cache, combine:
// - WeakHashMap for automatic key cleanup
// - SoftReference for values to keep them as long as memory allows
Map<Key, SoftReference<Value>> cache = new WeakHashMap<>();
Value v = Optional.ofNullable(cache.get(key))
    .map(SoftReference::get)
    .orElseGet(() -> {
        Value loaded = load(key);
        cache.put(key, new SoftReference<>(loaded));
        return loaded;
    });
```

**Q: Is WeakHashMap thread-safe?**
- No. It has the same thread-safety issues as HashMap.
- Additionally, GC can modify the map at any time (by clearing entries) — making it even more dangerous to iterate without synchronization.

**Q: What is the relationship between WeakHashMap and memory leaks?**
- WeakHashMap is a cure for the "listener/cache memory leak" pattern.
- Without it: `static Map<Widget, Data>` holds Widget alive forever even after the widget is gone from the UI.
- With WeakHashMap: when the Widget loses its last strong reference elsewhere, the map entry is automatically removed.

---

#### Quick Comparison — All Map Types

| | HashMap | LinkedHashMap | TreeMap | ConcurrentHashMap | WeakHashMap | Hashtable |
|---|---|---|---|---|---|---|
| Order | None | Insertion/Access | Sorted | None | None | None |
| Null key | Yes (1) | Yes (1) | No | No | Yes | No |
| Null values | Yes | Yes | Yes | No | Yes | No |
| Thread-safe | No | No | No | Yes | No | Yes (legacy) |
| Performance | O(1) | O(1) | O(log n) | O(1) | O(1) | O(1) synchronized |
| Backed by | Array+LL/Tree | Array+LL | Red-Black Tree | Array+LL/Tree | WeakRef array | Array+LL |
| Use case | General | LRU cache, ordered | Range queries | Concurrent access | Auto-evicting cache | Legacy only |

---

## 4. Multithreading and Concurrency

---

### Q21: What is a thread? How do you create threads in Java?

A thread is an independent path of execution scheduled by the OS. Every Java program starts with the main thread; you create additional threads to parallelize work.

**Three ways to create threads:**

```java
// 1. Extend Thread (rarely preferred — uses up your single inheritance)
class PrintTask extends Thread {
    @Override public void run() { System.out.println("Hello from " + getName()); }
}
new PrintTask().start();

// 2. Implement Runnable (preferred pre-Java 8)
Runnable task = () -> System.out.println("Hello from " + Thread.currentThread().getName());
new Thread(task).start();

// 3. Use ExecutorService (production standard — manage lifecycle, pooling)
ExecutorService pool = Executors.newFixedThreadPool(4);
pool.submit(() -> System.out.println("Pooled thread"));
pool.shutdown();

// 4. Virtual threads (Java 21 — preferred for I/O-bound work)
Thread.ofVirtual().start(() -> System.out.println("Virtual thread"));
// Or via executor:
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> System.out.println("Virtual"));
}
```

*Difference:* `start()` creates a new thread and calls `run()` on it. Calling `run()` directly executes in the **current** thread — a common beginner mistake.

---

### Q22: Thread lifecycle.

```
Thread Lifecycle
════════════════════════════════════════════════════════

   new Thread()
        │
        ▼
     [NEW] ──── start() ──── [RUNNABLE]
                                  │
                            scheduler picks it
                                  │
                          [RUNNING] ──── yield() ──── [RUNNABLE]
                                  │
                    ┌─────────────┼──────────────────┐
                    │             │                  │
               sleep(n)     wait()/join()        I/O block
                    │             │                  │
              [TIMED_WAITING] [WAITING]         [BLOCKED]
                    │             │                  │
               timeout      notify()/join     lock acquired / I/O done
                    │             │                  │
                    └─────────────┴──────────────────┘
                                  │
                               [RUNNABLE]
                                  │
                            run() returns
                                  │
                            [TERMINATED]
```

Key distinctions:
- `BLOCKED`: waiting to acquire a monitor lock
- `WAITING`: waiting indefinitely for `notify()`/`join()`
- `TIMED_WAITING`: waiting with a timeout (`sleep`, `wait(ms)`, `join(ms)`)

---

### Q23: synchronized keyword — how does it work?

Every Java object has a monitor (mutex). `synchronized` acquires the monitor before entering and releases on exit (even if an exception is thrown).

```java
class Counter {
    private int count = 0;

    // Synchronized method — acquires monitor on `this`
    public synchronized void increment() { count++; }

    // Synchronized block — finer granularity
    public void incrementWithBlock() {
        synchronized(this) { count++; }
        // other non-critical work here — no lock held
    }

    // Static synchronized — acquires monitor on Counter.class
    public static synchronized void staticOp() { /* class-level lock */ }
}
```

**Compound check-then-act must be atomic:**
```java
// BUG — two separate synchronized operations, not atomic together
public synchronized boolean isEmpty() { return list.isEmpty(); }
public synchronized void add(T item) { list.add(item); }

// CORRECT — make the compound operation atomic
public synchronized void addIfEmpty(T item) {
    if (list.isEmpty()) list.add(item);
}
```

---

### Q24: volatile keyword — what does it guarantee?

`volatile` provides two guarantees:
1. **Visibility:** Writes to a `volatile` field are immediately visible to all threads (no CPU cache staleness).
2. **Happens-before:** A write to `volatile x` happens-before every subsequent read of `volatile x`.

```java
class StopFlag {
    private volatile boolean stopped = false;  // without volatile, thread may cache this

    public void stop() { stopped = true; }  // write visible to all threads immediately

    public void run() {
        while (!stopped) {   // always reads from main memory
            doWork();
        }
    }
}
```

**What volatile does NOT guarantee:** Atomicity. `volatile int counter; counter++` is NOT atomic — it's a read-modify-write, which can be interrupted.

```java
volatile int counter = 0;
counter++;  // WRONG — not atomic even with volatile

// CORRECT: use AtomicInteger
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // CAS-based, lock-free, atomic

// Or LongAdder for high-contention counters (better throughput than AtomicLong)
LongAdder hits = new LongAdder();
hits.increment();  // internally striped across multiple cells
```

---

### Q25: Java Memory Model — happens-before relationships.

The JMM defines when one thread's write is guaranteed to be visible to another thread's read.

**Happens-before rules:**
```
1. Program order:        x = 1; y = 2;  → x=1 HB y=2 (within same thread)
2. Monitor lock:         unlock(m) HB lock(m) (next locker sees all writes)
3. volatile write:       write(v) HB read(v) (subsequent read sees the write)
4. Thread start:         start(t) HB any action in t
5. Thread join:          all actions in t HB join(t) returns
6. Transitivity:         if A HB B and B HB C, then A HB C
```

**Without happens-before:**
```java
int x = 0;
boolean initialized = false;

// Thread 1
x = 42;
initialized = true;

// Thread 2 — may see initialized=true but x=0 (reordering is allowed!)
if (initialized) {
    System.out.println(x);  // could print 0 — no happens-before!
}

// FIX: make initialized volatile
volatile boolean initialized = false;
// Now Thread 2's read of initialized=true HB-after Thread 1's write
// And x=42 HB initialized=true (program order within Thread 1)
// So x=42 is visible to Thread 2
```

---

### Q26: ReentrantLock vs synchronized.

```java
// synchronized — simple but inflexible
synchronized(lock) { doWork(); }

// ReentrantLock — flexible
ReentrantLock lock = new ReentrantLock(true); // fair=true → FIFO ordering
lock.lock();
try {
    doWork();
} finally {
    lock.unlock();  // MUST be in finally — synchronized auto-releases on exception
}

// Timed lock — avoid deadlock by giving up after timeout
if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
    try { doWork(); }
    finally { lock.unlock(); }
} else {
    // fallback — couldn't acquire lock
}

// Interruptible lock — can cancel waiting threads
lock.lockInterruptibly();
```

**ReadWriteLock — allow concurrent reads:**
```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();

// Multiple threads can hold readLock simultaneously
readLock.lock();
try { return cache.get(key); }
finally { readLock.unlock(); }

// writeLock is exclusive — all readers/writers blocked
writeLock.lock();
try { cache.put(key, value); }
finally { writeLock.unlock(); }
```

**StampedLock (Java 8+) — optimistic reads:**
```java
StampedLock sl = new StampedLock();

// Optimistic read — no lock acquisition, just stamp check
long stamp = sl.tryOptimisticRead();
double x = this.x, y = this.y;
if (!sl.validate(stamp)) {  // if a write happened, fall back to read lock
    stamp = sl.readLock();
    try { x = this.x; y = this.y; }
    finally { sl.unlockRead(stamp); }
}
```

---

### Q27: Deadlock — detection and prevention.

**Deadlock conditions (all four must hold):**
```
1. Mutual exclusion:  Resources are exclusively held
2. Hold and wait:     Thread holds a resource while waiting for another
3. No preemption:     Resources cannot be forcibly taken
4. Circular wait:     T1 waits for T2, T2 waits for T1
```

```java
// Classic deadlock
Object lockA = new Object(), lockB = new Object();

Thread t1 = new Thread(() -> {
    synchronized(lockA) {
        Thread.sleep(10);
        synchronized(lockB) { /* work */ }  // waits for B while holding A
    }
});
Thread t2 = new Thread(() -> {
    synchronized(lockB) {
        Thread.sleep(10);
        synchronized(lockA) { /* work */ }  // waits for A while holding B
    }
});
```

**Prevention — lock ordering:**
```java
// ALWAYS acquire locks in the same order (break circular wait)
Object first  = System.identityHashCode(lockA) < System.identityHashCode(lockB) ? lockA : lockB;
Object second = (first == lockA) ? lockB : lockA;

synchronized(first) {
    synchronized(second) { /* safe */ }
}
```

**Detection:**
```bash
# Generate thread dump
kill -3 <pid>             # UNIX (sends SIGQUIT)
jstack <pid>              # JDK tool, shows all threads + locks
jcmd <pid> Thread.print   # JDK 7+ alternative

# Thread dump shows deadlock:
# "Thread-1" waiting to lock <0x...> held by "Thread-2"
# "Thread-2" waiting to lock <0x...> held by "Thread-1"
```

---

### Q28: ExecutorService and ThreadPoolExecutor.

```java
// Core thread pool constructor — don't use Executors factory methods in production
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    4,                          // corePoolSize   — always-alive threads
    8,                          // maximumPoolSize — max threads under load
    60, TimeUnit.SECONDS,       // keepAliveTime   — idle threads above core are terminated
    new ArrayBlockingQueue<>(1000),  // workQueue   — bounded is critical in production
    new ThreadFactory() { /* named threads */ },
    new ThreadPoolExecutor.CallerRunsPolicy()  // rejection policy
);
```

**Rejection policies:**
| Policy | Behaviour |
|---|---|
| `AbortPolicy` (default) | Throws `RejectedExecutionException` |
| `CallerRunsPolicy` | Caller thread executes the task (back-pressure) |
| `DiscardPolicy` | Silently drops the task |
| `DiscardOldestPolicy` | Drops oldest queued task, retries new one |

**Thread pool sizing rule:**
```
CPU-bound tasks:  pool size = N_CPU + 1  (one extra for cache misses)
I/O-bound tasks:  pool size = N_CPU * (1 + wait_time / compute_time)
Example: 8 CPUs, 90% I/O wait: pool size = 8 * (1 + 9) = 80 threads
Java 21 virtual threads: just use newVirtualThreadPerTaskExecutor() for I/O tasks
```

---

### Q29: CompletableFuture — asynchronous pipelines.

```java
// Chained async pipeline
CompletableFuture<OrderConfirmation> pipeline =
    CompletableFuture
        .supplyAsync(() -> fetchUser(userId), ioPool)           // async
        .thenApplyAsync(user -> fetchOrders(user), ioPool)      // async transform
        .thenApply(orders -> validateOrders(orders))            // sync transform
        .thenCompose(orders -> placeOrder(orders))              // flatMap (async)
        .exceptionally(ex -> {                                  // error handling
            log.error("Order failed", ex);
            return OrderConfirmation.failed(ex.getMessage());
        })
        .whenComplete((result, ex) -> audit(result, ex));       // side effect

// Wait for all
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
all.join();

// Wait for first
CompletableFuture<String> first = CompletableFuture.anyOf(f1, f2, f3)
    .thenApply(r -> (String) r);

// Timeout (Java 9+)
future.orTimeout(5, TimeUnit.SECONDS)
      .exceptionally(ex -> defaultValue);
```

---

### Q30: Double-checked locking — is it safe in Java?

Prior to Java 5, double-checked locking was **broken** due to the JMM. Java 5+ (with `volatile`) makes it safe:

```java
// BROKEN (pre-Java 5 or without volatile)
class Singleton {
    private static Singleton instance;
    public static Singleton getInstance() {
        if (instance == null) {              // check 1 (no lock)
            synchronized(Singleton.class) {
                if (instance == null) {      // check 2 (with lock)
                    instance = new Singleton();
                    // JIT can reorder: allocate memory → assign ref → init fields
                    // Another thread may see non-null but uninitialized object!
                }
            }
        }
        return instance;
    }
}

// CORRECT — volatile prevents reordering
class Singleton {
    private static volatile Singleton instance;  // volatile is the fix
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized(Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// BETTER — initialization-on-demand holder (no explicit synchronization needed)
class Singleton {
    private Singleton() {}
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();  // class loading is thread-safe
    }
    public static Singleton getInstance() { return Holder.INSTANCE; }
}
```

---

### Q31: Virtual Threads (Java 21) — Project Loom.

Virtual threads are **lightweight threads** managed by the JVM, not the OS. They enable writing synchronous-style code that scales like async code.

```
Platform threads: mapped 1:1 to OS threads (~1MB stack, ~10K limit per JVM)
Virtual threads:  many-to-1 mapped to carrier (OS) threads (~few KB, millions possible)

Platform thread: blocked on I/O → OS thread sits idle (wasted resource)
Virtual thread:  blocked on I/O → carrier thread is unmounted → free to run another vthread
```

```java
// Before Java 21: async/reactive to avoid blocking
CompletableFuture.supplyAsync(() -> httpClient.get(url1))
    .thenCombine(CompletableFuture.supplyAsync(() -> httpClient.get(url2)), this::merge);

// Java 21: synchronous-style, same scalability with virtual threads
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    var f1 = executor.submit(() -> httpClient.get(url1));
    var f2 = executor.submit(() -> httpClient.get(url2));
    return merge(f1.get(), f2.get());
}
// JVM parks the virtual thread during get(), unmounts carrier thread, no OS thread wasted
```

**Key constraints:**
- Avoid `synchronized` on virtual threads (pins the carrier thread) — use `ReentrantLock` instead
- Thread pools with virtual threads are anti-pattern — create a new virtual thread per task
- CPU-bound work still needs platform thread pools — virtual threads help only for I/O-bound tasks

---

## 5. Exception Handling

---

### Q32: Exception hierarchy.

```
java.lang.Throwable
    ├── Error              (JVM-level — do NOT catch)
    │     ├── OutOfMemoryError
    │     └── StackOverflowError
    └── Exception
          ├── IOException        (checked)
          ├── SQLException       (checked)
          └── RuntimeException   (unchecked)
                ├── NullPointerException
                ├── IllegalArgumentException
                └── IllegalStateException
```

**Checked:** Compiler enforces handling. Use for recoverable conditions (file not found, network timeout).
**Unchecked:** Programming bugs — caller cannot reasonably recover (null pointer, bad index).

---

### Q33: try-with-resources (Java 7+).

```java
// Before Java 7 — verbose, error-prone
Connection conn = null;
PreparedStatement ps = null;
try {
    conn = ds.getConnection();
    ps = conn.prepareStatement(sql);
    ps.execute();
} finally {
    if (ps   != null) try { ps.close();   } catch (SQLException ignored) {}
    if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
}

// Java 7+ try-with-resources — clean, guaranteed close()
try (Connection conn = ds.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.execute();
}
// Resources closed in reverse declaration order, even on exception
// Suppressed exceptions preserved on the primary exception
```

**Suppressed exceptions:**
```java
try (var r = new FailingResource()) {
    throw new RuntimeException("primary");
    // close() also throws → attached as suppressed, not lost
}
catch (RuntimeException e) {
    for (Throwable s : e.getSuppressed()) log.warn("Suppressed: " + s.getMessage());
    throw e;
}
```

---

### Q34: Production exception handling best practices.

```java
// 1. NEVER swallow
catch (Exception e) { }  // CRIME — silently hides bugs

// 2. Log with context, wrap with meaningful message
catch (SQLException e) {
    log.error("Failed to save order id={}", orderId, e);
    throw new OrderPersistenceException("Order save failed: " + orderId, e);
}

// 3. Fail fast with meaningful message
public void setAge(int age) {
    if (age < 0 || age > 150)
        throw new IllegalArgumentException("Invalid age: " + age + ". Expected [0,150].");
}

// 4. Don't use exceptions for flow control
// BAD:  try { Integer.parseInt(s); } catch (NumberFormatException e) { ... }
// GOOD: if (s.matches("\\d+")) { Integer.parseInt(s); }

// 5. Custom exception — include business context
public class InsufficientFundsException extends RuntimeException {
    private final String accountId;
    private final BigDecimal required, available;
    public InsufficientFundsException(String accountId, BigDecimal required, BigDecimal available) {
        super(String.format("Account %s: required %.2f, available %.2f", accountId, required, available));
        this.accountId = accountId;
        this.required = required;
        this.available = available;
    }
    // getters for structured error handling
}
```

---

## 6. Generics

---

### Q35: Type erasure — implications.

Generics are compile-time only. At runtime `List<String>` and `List<Integer>` are both `List`. The compiler:
- Replaces type parameters with bounds (or `Object`)
- Inserts casts at call sites
- Generates bridge methods for overriding

```java
// Cannot do due to erasure:
// new T()                    — type unknown at runtime
// new T[]                    — cannot create generic arrays
// instanceof List<String>    — illegal; use instanceof List<?>
// T.class                    — no generic class literal

// Type erasure proof
List<String> strings = new ArrayList<>();
List<Integer> ints   = new ArrayList<>();
strings.getClass() == ints.getClass();  // true — both just ArrayList
```

---

### Q36: Wildcards and PECS.

```java
// Upper bounded (? extends) — can READ, cannot write: Producer Extends
public double sum(List<? extends Number> list) {
    return list.stream().mapToDouble(Number::doubleValue).sum();
    // list.add(1);  COMPILE ERROR — cannot write
}
sum(List.of(1, 2, 3));    // List<Integer>  ✓
sum(List.of(1.1, 2.2));   // List<Double>   ✓

// Lower bounded (? super) — can WRITE, read as Object: Consumer Super
public void fillWithZeros(List<? super Integer> list, int count) {
    for (int i = 0; i < count; i++) list.add(0);
    // Object o = list.get(0);  only Object ref available
}
fillWithZeros(new ArrayList<Integer>(), 5);  // ✓
fillWithZeros(new ArrayList<Number>(), 5);   // ✓
fillWithZeros(new ArrayList<Object>(), 5);   // ✓

// PECS applied: Collections.copy signature
// public static <T> void copy(List<? super T> dest, List<? extends T> src)
// dest is a consumer → super; src is a producer → extends

// Bounded generic method with multiple bounds
public <T extends Serializable & Comparable<T>> T processAndSort(List<T> items) {
    return Collections.min(items);
}
```

---

## 7. Functional Programming and Lambdas

---

### Q37: Functional interfaces and lambdas.

```java
// Built-in functional interfaces
Function<String, Integer>        parse  = Integer::parseInt;       // T → R
Predicate<String>                blank  = String::isBlank;          // T → boolean
Consumer<String>                 print  = System.out::println;      // T → void
Supplier<LocalDate>              today  = LocalDate::now;           // () → T
BiFunction<Integer,Integer,Integer> add = Integer::sum;             // (T,U) → R
UnaryOperator<String>            upper  = String::toUpperCase;      // T → T

// Method reference types
//  1. Static:           Integer::parseInt
//  2. Bound instance:   str::toUpperCase   (str is a specific instance)
//  3. Unbound instance: String::toUpperCase (first arg becomes receiver)
//  4. Constructor:      ArrayList::new

// Lambda captures — variable must be effectively final
String prefix = "Hello, ";
Consumer<String> greet = name -> System.out.println(prefix + name);
// prefix = "Hi";  // ERROR — would break effective-final requirement
```

---

### Q38: Stream API — lazy pipelines, map vs flatMap, collectors.

```java
// Pipeline is LAZY — nothing executes until a terminal operation
List<String> result = employees.stream()
    .filter(e -> e.getSalary() > 50_000)        // lazy (intermediate)
    .sorted(Comparator.comparing(Employee::getName))  // lazy
    .map(Employee::getName)                      // lazy
    .collect(Collectors.toList());               // terminal → triggers pipeline

// Short-circuit: stops at first match
boolean anyHighEarner = employees.stream()
    .anyMatch(e -> e.getSalary() > 100_000);

// map: 1-to-1 transform
List<String> names = employees.stream().map(Employee::getName).toList();  // Java 16+

// flatMap: 1-to-many, then flatten
List<String> allSkills = employees.stream()
    .flatMap(e -> e.getSkills().stream())
    .distinct().toList();

// Collectors
Map<Department, List<Employee>> byDept =
    employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));

Map<Department, Long> headcount =
    employees.stream().collect(
        Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

String csv = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));

// parallel streams — only when: large data (100K+), CPU-bound, stateless, no shared state
long sum = LongStream.rangeClosed(1, 10_000_000).parallel().sum();
// Benchmark with JMH — parallel is often slower for small lists
```

---

### Q39: Optional — correct usage patterns.

```java
// BAD
optional.get();                              // throws NoSuchElementException without check
if (optional.isPresent()) optional.get();   // verbose, defeats the purpose

// GOOD — idiomatic Optional
String name   = findUser(id).map(User::getName).orElse("Anonymous");
User   user   = findUser(id).orElseGet(() -> userService.createGuest()); // lazy default
User   user2  = findUser(id).orElseThrow(() -> new UserNotFoundException(id));

findUser(id).ifPresentOrElse(
    u -> audit(u),
    () -> log.warn("User not found: {}", id)
);

// Chaining — filter + map
Optional<String> email = findUser(id)
    .filter(User::isActive)
    .map(User::getEmail)
    .filter(e -> e.contains("@"));

// flatMap — avoid Optional<Optional<T>>
Optional<Department> dept = findEmployee(id)
    .flatMap(Employee::getDepartment);  // Employee::getDepartment returns Optional<Department>

// Rules:
// - Optional is a RETURN TYPE only
// - Never use Optional as a field type or method parameter
// - Never do optional.get() without a guard
```

---

## 8. Java 9+ Language Features

---

### Q40: Records (Java 16), Sealed Classes (Java 17), Pattern Matching (Java 21).

```java
// Records — immutable data carriers (auto-generates constructor, accessors, equals, hashCode, toString)
public record OrderItem(String productId, int quantity, BigDecimal price) {
    public OrderItem {  // compact constructor — validation runs before field assignment
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        price = price.setScale(2, RoundingMode.HALF_UP);  // normalize
    }
    public BigDecimal total() { return price.multiply(BigDecimal.valueOf(quantity)); }
}
// Records: implicitly final, cannot extend other classes, can implement interfaces

// Sealed classes — closed type hierarchy
public sealed interface Shape permits Circle, Rectangle, Triangle {}
public record Circle(double radius)      implements Shape {}
public record Rectangle(double w, double h) implements Shape {}
public final class Triangle              implements Shape { /* fields */ }

// Exhaustive switch — compiler verifies all permitted subtypes covered
double area(Shape s) {
    return switch (s) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.w() * r.h();
        case Triangle t  -> heronFormula(t);
    };  // no default needed — sealed ensures completeness
}

// switch expressions (Java 14 — finalized)
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    case THURSDAY, SATURDAY     -> 8;
    case WEDNESDAY              -> 9;
};  // compile error if any enum value missing

// Text blocks (Java 15)
String json = """
        {
          "name": "Alice",
          "age": 30
        }
        """;  // indentation stripped based on closing """ position

// Pattern matching for instanceof (Java 16+)
if (obj instanceof String s && s.length() > 5) {
    System.out.println(s.toUpperCase());  // s scoped to this block
}

// Switch with patterns and guards (Java 21)
String format(Object obj) {
    return switch (obj) {
        case Integer i when i < 0 -> "Negative: " + i;
        case Integer i            -> "Positive: " + i;
        case String s             -> "String: " + s;
        case null                 -> "null";
        default                   -> "Other: " + obj;
    };
}

// Record patterns — destructure records in switch (Java 21)
String describe(Shape s) {
    return switch (s) {
        case Circle(double r) when r > 100  -> "Large circle r=" + r;
        case Circle(double r)               -> "Circle r=" + r;
        case Rectangle(double w, double h)  -> w + "x" + h + " rectangle";
        case Triangle t                     -> "Triangle";
    };
}
```

---

# Part 2: Advanced Java Concepts

---

## 9. Spring Framework and Dependency Injection

---

### Q41: Core IoC container and constructor injection.

```java
// IoC: you describe WHAT you need; Spring creates and wires concrete implementations

// Field injection (BAD — hides deps, not testable without Spring, no final)
@Service
public class OrderService {
    @Autowired private OrderRepository repo;
}

// Constructor injection (PREFERRED — explicit, immutable, testable)
@Service
public class OrderService {
    private final OrderRepository repo;
    private final PaymentGateway payment;

    public OrderService(OrderRepository repo, PaymentGateway payment) {
        this.repo    = Objects.requireNonNull(repo);
        this.payment = Objects.requireNonNull(payment);
    }
    // Test: new OrderService(mockRepo, mockPayment) — no Spring needed
}
```

---

### Q42: Spring bean scopes.

| Scope | When | Notes |
|---|---|---|
| `singleton` (default) | One instance per Spring context | Thread-safety is your responsibility |
| `prototype` | New instance per injection | `@Lookup` or `ObjectProvider` to inject into singleton |
| `request` | One per HTTP request | Web context only |
| `session` | One per HTTP session | Web context only |

---

### Q43: @Transactional — propagation, isolation, self-invocation pitfall.

```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation   = Isolation.READ_COMMITTED,
    timeout     = 30,
    rollbackFor = InsufficientFundsException.class
)
public void transfer(String from, String to, BigDecimal amount) {
    accountRepo.debit(from, amount);
    accountRepo.credit(to, amount);
}

// SELF-INVOCATION PITFALL — @Transactional uses Spring AOP proxy
@Service
public class OrderService {
    public void process(Order o) {
        this.save(o);  // WRONG — calls concrete method directly, proxy bypassed!
    }
    @Transactional
    public void save(Order o) { repo.save(o); }
}
// FIX: inject self via ApplicationContext, or move save() to a separate @Service
```

**Propagation:** `REQUIRED` (default): join or create. `REQUIRES_NEW`: always create new, suspend existing. `NESTED`: savepoint within existing.

---

### Q44: N+1 query problem and solutions.

```java
// N+1: 1 query loads 1000 orders → 1000 queries load each order's items
List<Order> orders = repo.findAll();
for (Order o : orders) { o.getItems().size(); }  // 1001 queries total!

// FIX 1: JPQL fetch join
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
List<Order> findByStatusWithItems(@Param("status") String status);

// FIX 2: EntityGraph
@EntityGraph(attributePaths = {"items", "items.product"})
List<Order> findAll();

// FIX 3: Batch size (Hibernate — loads items in batches of 50)
@OneToMany
@BatchSize(size = 50)
private List<OrderItem> items;

// Detection
logging.level.org.hibernate.SQL=DEBUG
// Count SQL statements per request; anything > 5 is suspicious
```

---

### Q45: Spring AOP — pointcut, join point, advice.

```java
@Aspect @Component
public class AuditAspect {

    // Pointcut: defines WHERE to intercept
    @Pointcut("@annotation(com.example.Auditable)")
    public void auditableMethods() {}

    // Advice: defines WHAT to do
    @Around("auditableMethods()")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();  // join point: actual method invocation
            auditLog.record(method, "SUCCESS", System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            auditLog.record(method, "FAILURE: " + e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }
    }
}
// AOP limitation: only intercepts calls through the Spring proxy (no self-invocation)
```

---

## 10. REST APIs and Web Services

---

### Q46: HTTP methods, status codes, idempotency.

| Method | Idempotent | Safe | Use Case |
|---|---|---|---|
| GET | Yes | Yes | Read resource |
| POST | No | No | Create resource |
| PUT | Yes | No | Full replace |
| PATCH | No | No | Partial update |
| DELETE | Yes | No | Delete |

```java
@RestController @RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return orderService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody CreateOrderRequest req,
            UriComponentsBuilder ucb) {
        OrderDto dto = orderService.create(req);
        URI location = ucb.path("/api/v1/orders/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(location).body(dto);  // 201 + Location header
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable String id, @RequestBody StatusUpdate update) {
        orderService.updateStatus(id, update.status());
    }
}
```

**Status codes:**
- `2xx` — success: 200 OK, 201 Created, 204 No Content
- `3xx` — redirection: 301 Moved Permanently, 304 Not Modified
- `4xx` — client error: 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 429 Too Many Requests
- `5xx` — server error: 500 Internal Server Error, 503 Service Unavailable

---

### Q47: REST versioning, pagination, and error handling.

```java
// URI versioning (recommended)
@RequestMapping("/api/v2/users")

// Pagination — use cursor-based for large datasets
@GetMapping
public Page<OrderDto> listOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt,desc") String sort) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort.split(",")));
    return orderService.findAll(pageRequest).map(orderMapper::toDto);
}
// Response includes: content, totalElements, totalPages, number, size

// Global error handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(OrderNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(404);
        pd.setTitle("Order Not Found");
        pd.setDetail(ex.getMessage());
        return pd;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(400);
        pd.setTitle("Validation Failed");
        pd.setProperty("errors", ex.getBindingResult().getFieldErrors()
            .stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList());
        return pd;
    }
}
```

---

## 11. Database and SQL

---

### Q48: ACID and isolation levels.

```
A — Atomicity:    All ops succeed or all roll back (debit + credit are atomic)
C — Consistency:  DB moves from one valid state to another (total money conserved)
I — Isolation:    Concurrent transactions don't interfere
D — Durability:   Committed data survives crashes (WAL — Write-Ahead Log)
```

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom |
|---|---|---|---|
| READ_UNCOMMITTED | Possible | Possible | Possible |
| READ_COMMITTED (default) | No | Possible | Possible |
| REPEATABLE_READ | No | No | Possible |
| SERIALIZABLE | No | No | No |

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void processInventory(String productId) {
    int stock = inventory.getStock(productId);   // read 1
    doExpensiveWork();
    int again = inventory.getStock(productId);   // read 2 — same value guaranteed
    // at READ_COMMITTED, another TX could have updated stock between reads
}
```

---

### Q49: Database indexing — B-tree structure.

```
B-TREE INDEX

Root:  [50 | 100 | 150]
         /    |    |   \
Leaf: [10,20] [60,70] [110,120] [160,170]  ← linked list for range scans

Lookup: O(log n)
Range:  O(log n + k)  (navigate to start, scan leaf chain)
Insert: O(log n)      (may split node)
```

```sql
-- Composite index: left-most prefix rule
CREATE INDEX idx_dept_salary ON employees(department, salary);
-- Used for: WHERE department = ?
-- Used for: WHERE department = ? AND salary > ?
-- NOT used: WHERE salary > ?  (no left-most prefix)

-- Covering index: avoids heap fetch (index-only scan)
CREATE INDEX idx_cov ON employees(department, salary, name);
SELECT name, salary FROM employees WHERE department = 'ENG';  -- index-only scan
```

---

### Q50: HikariCP and connection pool sizing.

```yaml
spring.datasource.hikari:
  maximum-pool-size: 20
  minimum-idle: 5
  connection-timeout: 30000       # ms to wait for connection from pool
  idle-timeout: 600000
  max-lifetime: 1800000
  leak-detection-threshold: 60000 # log if connection not returned in 60s
```

**Formula:** `pool_size = N_cores × 2 + N_spindles` (HikariCP recommendation).
For 4-core + 1 SSD: `4 × 2 + 1 = 9 connections`. More connections → more context switching, not more throughput.

---

## 12. Performance Optimization and Profiling

---

### Q51: Profiling and benchmarking.

```bash
# async-profiler — low overhead, CPU + allocation flamegraph
java -agentpath:libAsyncProfiler.so=start,event=cpu,file=profile.html MyApp

# JFR — built-in, production-safe
-XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr
jfr print --events jdk.CPUSample profile.jfr

# GC logging
-Xlog:gc*:file=/var/log/gc.log:time,uptime:filecount=10,filesize=50m
```

```java
// JMH — never use System.currentTimeMillis() for micro-benchmarks
@BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5) @Measurement(iterations = 10) @Fork(2)
@State(Scope.Benchmark)
public class StringBenchmark {
    @Benchmark
    public String plusConcat() {
        String s = "";
        for (int i = 0; i < 1000; i++) s += i;  // O(n²) — N intermediate strings
        return s;
    }
    @Benchmark
    public String sbConcat() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) sb.append(i);
        return sb.toString();
    }
    // Result: StringBuilder ~50-100× faster for N=1000
}
```

---

### Q52: Caching strategies.

```java
// Spring @Cacheable — simple declarative caching
@Service public class ProductService {
    @Cacheable(value = "products", key = "#id",
               condition = "#id != null", unless = "#result == null")
    public Product findById(String id) {
        return repo.findById(id).orElseThrow();
    }

    @CacheEvict(value = "products", key = "#product.id")
    public Product update(Product product) { return repo.save(product); }
}

// Redis with Caffeine (L1 local + L2 distributed)
// L1: Caffeine (in-process, nanoseconds) → L2: Redis (network, microseconds) → DB
```

**Cache eviction policies:**
- **LRU (Least Recently Used):** Evict item not accessed longest — good general purpose
- **LFU (Least Frequently Used):** Evict least-used item — better for non-uniform access
- **TTL:** Time-based expiry — guarantees data freshness (essential for mutable data)

---

## 13. Design Patterns

---

### Q53: Singleton — all thread-safe implementations.

```java
// 1. Eager initialization (thread-safe — class loading is atomic)
public class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private AppConfig() {}
    public static AppConfig getInstance() { return INSTANCE; }
}

// 2. Initialization-on-demand holder (BEST — lazy, no sync overhead)
public class ConnectionPool {
    private ConnectionPool() {}
    private static class Holder {
        static final ConnectionPool INSTANCE = new ConnectionPool();
    }
    public static ConnectionPool getInstance() { return Holder.INSTANCE; }
}

// 3. Enum singleton (serialization-safe, reflection-safe)
public enum Registry { INSTANCE; public void register(Service s) { ... } }

// 4. Double-checked locking (volatile required — Java 5+)
public class LazyService {
    private static volatile LazyService instance;
    public static LazyService getInstance() {
        if (instance == null) {
            synchronized (LazyService.class) {
                if (instance == null) instance = new LazyService();
            }
        }
        return instance;
    }
}
```

---

### Q54: Builder pattern.

```java
// Solves: telescoping constructors (7+ params) and invalid intermediate state
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final int maxConnections;
    private final Duration timeout;
    private final boolean sslEnabled;

    private DatabaseConfig(Builder b) {
        this.host           = Objects.requireNonNull(b.host, "host required");
        this.port           = b.port;
        this.database       = Objects.requireNonNull(b.database, "database required");
        this.maxConnections = b.maxConnections;
        this.timeout        = b.timeout;
        this.sslEnabled     = b.sslEnabled;
    }

    public static class Builder {
        private final String host, database;  // required
        private int port = 5432;
        private int maxConnections = 10;
        private Duration timeout = Duration.ofSeconds(30);
        private boolean sslEnabled = false;

        public Builder(String host, String database) { this.host = host; this.database = database; }
        public Builder port(int p)            { this.port = p;           return this; }
        public Builder maxConnections(int m)  { this.maxConnections = m; return this; }
        public Builder timeout(Duration t)    { this.timeout = t;        return this; }
        public Builder sslEnabled()           { this.sslEnabled = true;  return this; }
        public DatabaseConfig build()         { return new DatabaseConfig(this); }
    }
}

DatabaseConfig config = new DatabaseConfig.Builder("db.company.com", "orders")
    .port(5432).maxConnections(20).sslEnabled().build();
```

---

### Q55: Strategy, Observer, and Decorator patterns.

```java
// Strategy — swap algorithm at runtime without if/else chains
interface PricingStrategy { BigDecimal calculate(Order order); }
class StandardPricing implements PricingStrategy { ... }
class VolumePricing    implements PricingStrategy { ... }
class LoyaltyPricing   implements PricingStrategy { ... }

// Observer — decouple event producers from consumers
@Component
public class OrderService {
    private final ApplicationEventPublisher publisher;

    public Order place(OrderRequest req) {
        Order order = processOrder(req);
        publisher.publishEvent(new OrderPlacedEvent(order));  // Spring event bus
        return order;
    }
}
@EventListener class InventoryHandler { void on(OrderPlacedEvent e) { ... } }
@EventListener class EmailHandler     { void on(OrderPlacedEvent e) { ... } }
// All decoupled — add new listener without touching OrderService

// Decorator — add behaviour without modifying class (same interface)
interface OrderRepository { Order save(Order o); }
class JpaOrderRepository implements OrderRepository { ... }
class CachingOrderRepository implements OrderRepository {
    private final OrderRepository delegate;
    private final Cache cache;
    public Order save(Order o) {
        Order saved = delegate.save(o);
        cache.put(saved.getId(), saved);
        return saved;
    }
}
class LoggingOrderRepository implements OrderRepository {
    private final OrderRepository delegate;
    public Order save(Order o) {
        log.info("Saving order {}", o.getId());
        return delegate.save(o);
    }
}
// Chain: new LoggingOrderRepository(new CachingOrderRepository(new JpaOrderRepository()))
```

---

### Q56: Proxy pattern.

```java
// Virtual proxy — lazy load expensive resource
public class LazyImageProxy implements Image {
    private final String path;
    private RealImage realImage;  // null until first access

    public LazyImageProxy(String path) { this.path = path; }

    @Override
    public void display() {
        if (realImage == null) realImage = new RealImage(path);  // load on first use
        realImage.display();
    }
}

// Spring AOP is essentially a dynamic proxy:
// JDK dynamic proxy (interface-based) or CGLIB (class-based subclassing)
// Every @Transactional, @Cacheable, @Async annotation creates a proxy wrapper
```

---

## 14. Testing and Quality Assurance

---

### Q57: JUnit 5 and Mockito patterns.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock UserRepository userRepo;
    @Mock InventoryService inventory;
    @InjectMocks OrderService orderService;

    @Test
    void placeOrder_success_returnsConfirmedStatus() {
        // Given
        when(userRepo.findById("u1")).thenReturn(Optional.of(new User("u1", "Alice", true)));
        when(inventory.reserve("sku1", 2)).thenReturn(true);

        // When
        OrderConfirmation result = orderService.placeOrder("u1", "sku1", 2);

        // Then
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(inventory).reserve("sku1", 2);
        verifyNoMoreInteractions(inventory);
    }

    @Test
    void placeOrder_inactiveUser_throwsException() {
        when(userRepo.findById("u2")).thenReturn(Optional.of(new User("u2", "Bob", false)));
        assertThatThrownBy(() -> orderService.placeOrder("u2", "sku1", 1))
            .isInstanceOf(UserNotActiveException.class);
        verifyNoInteractions(inventory);
    }

    @ParameterizedTest
    @CsvSource({"1, 10.00, 10.00", "3, 5.00, 15.00", "0, 5.00, 0.00"})
    void calculateTotal(int qty, double price, double expected) {
        assertThat(OrderCalculator.total(qty, price)).isEqualTo(expected);
    }
}
```

---

### Q58: TestContainers for integration tests.

```java
@SpringBootTest @Testcontainers
class OrderRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("orders_test");

    @DynamicPropertySource
    static void configureProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",      postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired OrderRepository repo;

    @Test @Transactional
    void saveOrder_persistsAllFields() {
        Order order = new Order("u1", List.of(new OrderItem("sku1", 2)));
        Order saved = repo.save(order);

        Order found = repo.findById(saved.getId()).orElseThrow();
        assertThat(found.getUserId()).isEqualTo("u1");
        assertThat(found.getItems()).hasSize(1);
    }
}
```

---

### Q59: Test pyramid and code coverage.

```
TEST PYRAMID

        /\
       /  \   End-to-End (5%)  — Selenium, Cucumber — slow, fragile, expensive
      /    \
     /------\  Integration (20%) — TestContainers, MockMvc — moderate speed
    /        \
   /----------\ Unit tests (75%) — JUnit + Mockito — fast, isolated, cheap

Code coverage (JaCoCo):
  - 80% line coverage = good baseline
  - 100% coverage ≠ good tests (can have 100% coverage with weak assertions)
  - Focus on: branch coverage, mutation testing (PITest)

TDD cycle:
  RED   → write a failing test
  GREEN → write minimal code to pass
  REFACTOR → clean up without breaking tests
```

---

## 15. Microservices and Distributed Systems

---

### Q60: Circuit breaker with Resilience4j.

```java
@Service
public class PaymentService {
    private final CircuitBreaker cb = CircuitBreaker.ofDefaults("payment");
    private final PaymentGatewayClient client;

    public PaymentResult charge(PaymentRequest req) {
        return CircuitBreaker.decorateSupplier(cb, () -> client.charge(req)).get();
    }
}
```

```yaml
resilience4j.circuitbreaker.instances.payment:
  slidingWindowSize: 10
  minimumNumberOfCalls: 5
  failureRateThreshold: 50         # OPEN when 50% of last 10 calls fail
  waitDurationInOpenState: 30s
  permittedNumberOfCallsInHalfOpenState: 3
```

```
State machine:
CLOSED → (failure rate > threshold) → OPEN → (wait duration) → HALF_OPEN
HALF_OPEN → (test calls succeed) → CLOSED
HALF_OPEN → (test calls fail)    → OPEN
```

---

### Q61: Saga pattern and eventual consistency.

```
CHOREOGRAPHY SAGA (event-driven, no central coordinator)
Order Svc →[OrderPlaced]→ Inventory Svc →[Reserved]→ Payment Svc
Payment fails → [PaymentFailed] → Inventory Svc: compensate (release)
                               → Order Svc: compensate (cancel order)

ORCHESTRATION SAGA (central coordinator)
Saga Orchestrator → 1.CreateOrder → 2.ReserveInventory → 3.ProcessPayment
                 ← PaymentFailed ← compensate in reverse order

KEY INSIGHT:
  Sagas provide eventual consistency, NOT ACID isolation.
  During a saga, intermediate states ARE visible to other transactions.
  Design domain to tolerate: "order_placed", "payment_pending", "confirmed" states.
  Use idempotency keys on all service calls (retry-safe compensations).
```

---

### Q62: Kafka-based async communication.

```java
// Producer
@Service
public class OrderService {
    private final KafkaTemplate<String, OrderEvent> kafka;

    public Order place(OrderRequest req) {
        Order order = createOrder(req);
        kafka.send("orders.placed", order.getId(), new OrderPlacedEvent(order));
        return order;
    }
}

// Consumer
@KafkaListener(topics = "orders.placed", groupId = "inventory-service")
public void onOrderPlaced(OrderPlacedEvent event) {
    inventoryService.reserve(event.orderId(), event.items());
}

// Key properties for reliability:
// producer: acks=all, retries=3, enable.idempotence=true
// consumer: enable.auto.commit=false, isolation.level=read_committed (for exactly-once)
// consumer: manually commit after successful processing
```

---

# Part 3: Java Version Evolution

---

## 16. Java 8 Features Deep Dive

---

### Q63: Major Java 8 features overview.

| Feature | Impact |
|---|---|
| Lambda expressions | Functional programming, eliminates anonymous class boilerplate |
| Stream API | Declarative, lazy data processing pipeline |
| Optional | Null-safe API design |
| Default methods | Interface evolution without breaking existing implementations |
| `java.time` API | Immutable, correct replacement for `Date`/`Calendar` |
| CompletableFuture | Composable async pipelines |
| Base64 built-in | No more Apache Commons dependency |

---

### Q64: New Date/Time API (java.time).

```java
// Pre-Java 8 problems:
// - java.util.Date is mutable (not thread-safe)
// - months are 0-based (Calendar.JANUARY = 0)
// - year is 1900-based (new Date(124, 0, 15) = 2024-01-15)

// Java 8 java.time — immutable, thread-safe, self-explanatory
LocalDate      date    = LocalDate.of(2024, Month.JANUARY, 15);  // date only, no time, no zone
LocalTime      time    = LocalTime.of(14, 30, 0);                // time only
LocalDateTime  dt      = LocalDateTime.of(date, time);           // no timezone
ZonedDateTime  zdt     = ZonedDateTime.of(dt, ZoneId.of("America/New_York")); // full
Instant        instant = Instant.now();                          // UTC timestamp

// Arithmetic — always returns new instance (immutable)
LocalDate tomorrow  = LocalDate.now().plusDays(1);
LocalDate lastDay   = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

// Duration (time-based) vs Period (date-based)
Duration d = Duration.between(startInstant, endInstant);  // hours, minutes, seconds
Period   p = Period.between(LocalDate.of(2020, 1, 1), LocalDate.now());
System.out.println(p.getYears() + " years, " + p.getMonths() + " months");

// Parsing/formatting
LocalDate parsed    = LocalDate.parse("2024-01-15");
String    formatted = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

// Migration from legacy Date
LocalDateTime ldt = legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
Date          old = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
```

---

### Q65: Stream API deep dive — collect vs reduce.

```java
// reduce: fold elements into a single value
int sum = IntStream.rangeClosed(1, 100).reduce(0, Integer::sum);
Optional<Integer> product = Stream.of(1,2,3,4,5).reduce((a, b) -> a * b);

// collect: mutable reduction — accumulate into a container
List<String>          list   = stream.collect(Collectors.toList());
Set<String>           set    = stream.collect(Collectors.toSet());
Map<String, Employee> byId   = employees.stream()
    .collect(Collectors.toMap(Employee::getId, e -> e));

// Custom collector
Collector<Employee, ?, Map<String, BigDecimal>> salaryByDept =
    Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.reducing(BigDecimal.ZERO, Employee::getSalary, BigDecimal::add)
    );

// When to use:
// reduce: stateless, simple aggregation (sum, product, max)
// collect: stateful accumulation into mutable containers (lists, maps, strings)
```

---

### Q66: What are lambda expressions? How do they work under the hood?

A lambda expression is an anonymous function -- a concise way to represent a single-method interface (functional interface). The compiler desugars lambdas via `invokedynamic` (not anonymous inner classes), so each lambda class is created lazily at runtime, and the same lambda can be reused across calls if it captures no state.

**Pre-Java 8 approach:**
```java
// Anonymous inner class -- 5 lines of boilerplate for 1 line of logic
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});
```

**Java 8 lambda -- same logic, 1 line:**
```java
Collections.sort(names, (a, b) -> a.compareTo(b));
// Or using a method reference:
names.sort(String::compareTo);
```

**Lambda syntax breakdown:**
```java
// (parameters) -> expression
// (parameters) -> { statements; }

Runnable r     = () -> System.out.println("No params");
Comparator<String> c = (a, b) -> a.compareTo(b);  // inferred types
Function<Integer, Integer> sq = (Integer x) -> x * x;  // explicit type
Consumer<String> printer = s -> {  // single param, no parens needed
    String upper = s.toUpperCase();
    System.out.println(upper);
};
```

**Variable capture rules:**
```java
String prefix = "Hello";       // effectively final -- OK to capture
// prefix = "Hi";              // would cause compile error if uncommented

Consumer<String> greeter = name -> System.out.println(prefix + " " + name);

// Instance fields and static fields can always be captured
class Greeter {
    private String suffix = "!";
    Consumer<String> greet = name -> System.out.println(name + suffix);  // OK
}
```

**Key points:**
- Lambdas can only implement **functional interfaces** (exactly one abstract method)
- Captured local variables must be **effectively final** (assigned once)
- `this` inside a lambda refers to the **enclosing class instance** (unlike anonymous inner class)
- Under the hood: `invokedynamic` + `LambdaMetafactory`, NOT new class file per lambda

**Common interview question:** Why are captured variables required to be effectively final?
> Because lambdas may execute in a different thread (e.g., passed to an executor). Allowing mutation would create race conditions with no compiler protection. Effectively final ensures the lambda sees a consistent snapshot.

---

### Q67: What are functional interfaces? Know the core four.

A functional interface has **exactly one abstract method** (SAM -- Single Abstract Method). It can have any number of `default` and `static` methods. Annotated with `@FunctionalInterface` (optional but recommended -- compiler enforces the contract).

**The four core functional interfaces in `java.util.function`:**

| Interface | Signature | Description | Example use |
|---|---|---|---|
| `Predicate<T>` | `boolean test(T t)` | Boolean-valued function | Filtering, validation |
| `Function<T, R>` | `R apply(T t)` | Transform T to R | Mapping, conversion |
| `Consumer<T>` | `void accept(T t)` | Side-effect on T | Printing, saving |
| `Supplier<T>` | `T get()` | Provide a value | Lazy initialisation |

**Predicate -- boolean logic:**
```java
Predicate<String> isLong     = s -> s.length() > 10;
Predicate<String> startsWithA = s -> s.startsWith("A");

// Composition -- and, or, negate
Predicate<String> longAndStartsA = isLong.and(startsWithA);
Predicate<String> longOrStartsA  = isLong.or(startsWithA);
Predicate<String> notLong        = isLong.negate();

List<String> filtered = names.stream()
    .filter(longAndStartsA)
    .toList();
```

**Function -- transformation:**
```java
Function<String, Integer> length     = String::length;
Function<Integer, String> intToStr   = Object::toString;

// Compose: g(f(x)) -- f runs first, then g
Function<String, String> lengthToStr = length.andThen(intToStr);

// compose: f(g(x)) -- g runs first
Function<String, String> same = intToStr.compose(length);

// BiFunction: two inputs
BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
String result = repeat.apply("abc", 3);  // "abcabcabc"
```

**Consumer -- side effects:**
```java
Consumer<String> print  = System.out::println;
Consumer<String> log    = s -> logger.info("Processing: {}", s);

// Chain consumers with andThen
Consumer<String> printAndLog = print.andThen(log);
names.forEach(printAndLog);

// BiConsumer: two inputs, void return
BiConsumer<String, Integer> mapPut = map::put;
```

**Supplier -- lazy production:**
```java
Supplier<LocalDate>   today  = LocalDate::now;    // not evaluated until get()
Supplier<List<String>> newList = ArrayList::new;   // factory

// Key use case: Optional.orElseGet() -- only called if value absent
Optional<User> user = cache.get(id);
User u = user.orElseGet(() -> database.findById(id));  // DB only if cache miss
```

**Other important variants:**

```java
// Primitive specialisations (avoid boxing overhead)
IntPredicate   positive = n -> n > 0;
IntFunction<String> toHex = Integer::toHexString;
IntUnaryOperator  doubler = n -> n * 2;
IntBinaryOperator adder   = Integer::sum;
ToIntFunction<String> strLen = String::length;

// UnaryOperator<T> -- Function<T,T> (same type in and out)
UnaryOperator<String> upper = String::toUpperCase;

// BinaryOperator<T> -- BiFunction<T,T,T>
BinaryOperator<Integer> max = Integer::max;
```

**Custom functional interface:**
```java
@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;  // abstract method

    // Can have default methods
    default T getOrNull() {
        try { return get(); }
        catch (Exception e) { return null; }
    }
}

// Usage: wrap checked exceptions for use in streams
ThrowingSupplier<Connection> getConn = () -> dataSource.getConnection();
```

---

### Q68: What are the four types of method references?

Method references are a shorthand for lambdas that just delegate to an existing method.

| Type | Syntax | Lambda equivalent |
|---|---|---|
| Static method | `ClassName::staticMethod` | `(args) -> ClassName.staticMethod(args)` |
| Instance method (bound) | `instance::method` | `(args) -> instance.method(args)` |
| Instance method (unbound) | `ClassName::instanceMethod` | `(obj, args) -> obj.method(args)` |
| Constructor | `ClassName::new` | `(args) -> new ClassName(args)` |

```java
// 1. Static method reference
Function<String, Integer> parseInt = Integer::parseInt;
// equivalent: s -> Integer.parseInt(s)

// 2. Bound instance method reference (specific object)
String   prefix = "Hello, ";
Function<String, String> greeter = prefix::concat;
// equivalent: s -> prefix.concat(s)  (prefix is fixed/bound)

// 3. Unbound instance method reference (receiver is the first parameter)
Function<String, String>    toUpper = String::toUpperCase;
// equivalent: s -> s.toUpperCase()
BiPredicate<String, String> startsWith = String::startsWith;
// equivalent: (s, prefix) -> s.startsWith(prefix)

// 4. Constructor reference
Supplier<ArrayList<String>>   listFactory = ArrayList::new;
Function<Integer, int[]>      arrayFactory = int[]::new;
BiFunction<String, Integer, StringBuilder> sbFactory = StringBuilder::new;
// equivalent: n -> new int[n]

// Real-world pattern: convert stream of strings to objects
List<User> users = names.stream()
    .map(User::new)          // calls User(String name) constructor
    .toList();
```

**Tricky interview question:** When can you NOT use a method reference?
```java
// 1. When lambda does more than forward (adds logic)
names.stream().map(s -> s.trim().toLowerCase());  // cannot simplify to method ref

// 2. When the method is overloaded and inference is ambiguous
stream.map(String::valueOf);  // OK if unambiguous
stream.map(Integer::toString); // ambiguous: toString() vs toString(int radix)

// 3. When you need to handle a checked exception
stream.map(s -> {
    try { return Files.readString(Path.of(s)); }
    catch (IOException e) { throw new RuntimeException(e); }
});  // cannot use Files::readString directly -- throws checked IOException
```

---

### Q69: Streams API -- complete deep dive

**What is a Stream?**
A `Stream<T>` is a **lazy, declarative pipeline** for processing sequences of elements. Key properties:
- **Not a data structure** -- does not store elements
- **Lazy** -- intermediate operations don't execute until a terminal operation is called
- **Consumed once** -- a stream cannot be reused after a terminal operation
- **Potentially unbounded** -- can represent infinite sequences

**Stream pipeline anatomy:**
```
Source --> [Intermediate ops (lazy)] --> Terminal op (eager, triggers execution)
 |                |                            |
List, Set,    filter, map,              collect, count,
Array, IO     sorted, limit,            reduce, forEach,
              flatMap, peek             findFirst, anyMatch
```

**Creating streams:**
```java
// From collections
Stream<String> s1 = list.stream();
Stream<String> s2 = list.parallelStream();

// From arrays
Stream<String>  s3 = Arrays.stream(array);
IntStream       s4 = Arrays.stream(intArray);

// Static factories
Stream<String>  s5 = Stream.of("a", "b", "c");
Stream<String>  s6 = Stream.empty();
Stream<String>  s7 = Stream.ofNullable(nullableValue);  // Java 9+

// Infinite streams
Stream<Integer> naturals  = Stream.iterate(1, n -> n + 1);
Stream<Double>  randoms   = Stream.generate(Math::random);

// Range (primitive -- avoids boxing)
IntStream range      = IntStream.range(1, 100);      // 1..99
IntStream rangeClosed = IntStream.rangeClosed(1, 100); // 1..100

// From IO
Stream<String> lines = Files.lines(Path.of("file.txt"));  // lazy file reading
```

**Intermediate operations:**

```java
List<Employee> employees = getEmployees();

// filter -- keeps elements matching predicate
employees.stream()
    .filter(e -> e.getSalary() > 50000)
    ...

// map -- transforms each element (1:1)
employees.stream()
    .map(Employee::getName)          // Stream<Employee> -> Stream<String>
    ...

// flatMap -- transforms each element to a stream, flattens (1:many)
employees.stream()
    .flatMap(e -> e.getProjects().stream())  // Stream<Employee> -> Stream<Project>
    ...

// distinct -- removes duplicates (uses equals/hashCode)
employees.stream()
    .map(Employee::getDepartment)
    .distinct()
    ...

// sorted -- natural order or custom Comparator
employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary).reversed()
                      .thenComparing(Employee::getName))
    ...

// limit / skip -- pagination
employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .skip(pageNumber * pageSize)   // skip first N elements
    .limit(pageSize)               // take next N elements
    ...

// peek -- for debugging (does not consume the stream)
employees.stream()
    .filter(e -> e.getSalary() > 50000)
    .peek(e -> logger.debug("After filter: {}", e.getName()))
    .map(Employee::getName)
    ...

// mapToInt / mapToLong / mapToDouble -- avoids boxing
int totalSalary = employees.stream()
    .mapToInt(Employee::getSalary)  // Stream<Employee> -> IntStream
    .sum();                         // IntStream.sum() -- no boxing
```

**Terminal operations:**

```java
// collect -- gather into container (most common)
List<String>             names    = stream.collect(Collectors.toList());
List<String>             immutable = stream.collect(Collectors.toUnmodifiableList());
Set<String>              set      = stream.collect(Collectors.toSet());
String                   joined   = stream.collect(Collectors.joining(", ", "[", "]"));
Map<String, Long>        freq     = stream.collect(Collectors.groupingBy(s -> s, Collectors.counting()));
Map<Boolean, List<Emp>>  split    = stream.collect(Collectors.partitioningBy(e -> e.getSalary() > 50000));

// reduce -- fold into single value
int    sum  = IntStream.rangeClosed(1, 100).reduce(0, Integer::sum);
Optional<Integer> max = stream.reduce(Integer::max);

// count, sum, min, max, average
long   count   = stream.filter(e -> e.isActive()).count();
OptionalInt min = intStream.min();
IntSummaryStatistics stats = intStream.summaryStatistics();
// stats.getCount(), getSum(), getMin(), getMax(), getAverage()

// findFirst / findAny
Optional<Employee> first = stream.filter(e -> e.getDept().equals("IT")).findFirst();
Optional<Employee> any   = parallelStream.filter(e -> e.getDept().equals("IT")).findAny();
// findFirst: deterministic (respects encounter order)
// findAny:   faster for parallel streams (no order guarantee needed)

// anyMatch / allMatch / noneMatch (short-circuit)
boolean hasHighEarner = employees.stream().anyMatch(e -> e.getSalary() > 200000);
boolean allActive     = employees.stream().allMatch(Employee::isActive);
boolean noneInactive  = employees.stream().noneMatch(e -> !e.isActive());

// forEach / forEachOrdered
stream.forEach(System.out::println);             // order not guaranteed in parallel
parallelStream.forEachOrdered(System.out::println);  // ordered but slower
```

---

### Q70: map vs flatMap -- the most asked Stream question

**`map`** transforms each element 1-to-1. Returns `Stream<Stream<T>>` if you try to return a stream from each element.

**`flatMap`** transforms each element to a stream, then flattens one level. Returns `Stream<T>`.

```java
List<List<Integer>> nested = List.of(
    List.of(1, 2, 3),
    List.of(4, 5, 6),
    List.of(7, 8, 9)
);

// map -- gives Stream<List<Integer>>, NOT what you want
Stream<List<Integer>> wrong = nested.stream().map(List::stream);

// flatMap -- flattens to Stream<Integer>
List<Integer> flat = nested.stream()
    .flatMap(Collection::stream)
    .toList();
// [1, 2, 3, 4, 5, 6, 7, 8, 9]

// Real-world example: each employee has multiple skills
List<String> allSkills = employees.stream()
    .flatMap(e -> e.getSkills().stream())  // Stream<Employee> -> Stream<String>
    .distinct()
    .sorted()
    .toList();

// Practical: find all orders across all customers
List<Order> allOrders = customers.stream()
    .flatMap(c -> c.getOrders().stream())
    .filter(o -> o.getStatus() == PENDING)
    .toList();

// Optional.flatMap vs Optional.map
Optional<String> city = Optional.of(user)
    .map(User::getAddress)             // Optional<Optional<Address>> -- WRONG
    .flatMap(a -> a.map(Address::getCity));  // correct with flatMap

// Better: chain flatMaps
Optional<String> city2 = Optional.of(user)
    .flatMap(u -> u.getAddress())       // Optional<Address>
    .flatMap(a -> a.getCity());         // Optional<String>
```

**Interview rule:** Use `flatMap` whenever your mapping function returns a container type (`Optional`, `List`, `Stream`). Use `map` for plain transformations.

---

### Q71: Collectors deep dive -- groupingBy, partitioningBy, toMap

```java
// groupingBy -- split stream into groups (like SQL GROUP BY)
Map<String, List<Employee>> byDept =
    employees.stream()
        .collect(Collectors.groupingBy(Employee::getDepartment));

// groupingBy with downstream collector
Map<String, Long> countByDept =
    employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.counting()));

Map<String, Double> avgSalaryByDept =
    employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.averagingInt(Employee::getSalary)));

Map<String, Optional<Employee>> highestPaidByDept =
    employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.maxBy(Comparator.comparing(Employee::getSalary))));

// Multi-level grouping (nested maps)
Map<String, Map<String, List<Employee>>> byDeptAndCity =
    employees.stream()
        .collect(Collectors.groupingBy(Employee::getDepartment,
            Collectors.groupingBy(Employee::getCity)));

// partitioningBy -- split into exactly two groups (true/false)
Map<Boolean, List<Employee>> seniorJunior =
    employees.stream()
        .collect(Collectors.partitioningBy(e -> e.getExperience() >= 5));
List<Employee> senior = seniorJunior.get(true);
List<Employee> junior = seniorJunior.get(false);

// toMap -- when you need a specific key-value mapping
Map<Long, Employee> byId =
    employees.stream()
        .collect(Collectors.toMap(Employee::getId, e -> e));

// toMap with merge function (handles duplicate keys)
Map<String, Integer> wordFreq =
    words.stream()
        .collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));

// toMap with specific Map implementation (LinkedHashMap preserves insertion order)
Map<String, Employee> ordered =
    employees.stream()
        .collect(Collectors.toMap(
            Employee::getName,
            e -> e,
            (e1, e2) -> e1,        // on duplicate key: keep first
            LinkedHashMap::new));   // use LinkedHashMap

// joining -- concatenate strings
String csv  = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", "));

String bracketed = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// [Alice, Bob, Charlie]

// Collectors.toUnmodifiableList/Map (Java 10+) -- prefer over Collections.unmodifiable*
List<String> immutableNames =
    employees.stream()
        .map(Employee::getName)
        .collect(Collectors.toUnmodifiableList());
```

---

### Q72: Stream laziness -- why it matters for performance

Streams are **lazily evaluated**: intermediate operations build a pipeline definition; nothing executes until a terminal operation is invoked.

```java
// Does NOT print anything -- no terminal op
Stream<String> lazy = names.stream()
    .filter(s -> { System.out.println("filter: " + s); return s.length() > 3; })
    .map(s -> { System.out.println("map: " + s); return s.toUpperCase(); });

// Terminal op triggers execution
lazy.findFirst();
// Only processes elements until first match -- may not process all elements!

// Short-circuit example:
List<Integer> big = List.of(1,2,3,4,5,6,7,8,9,10);
Optional<Integer> first = big.stream()
    .filter(n -> n > 3)    // processes: 1 (no), 2 (no), 3 (no), 4 (yes) -- stops
    .findFirst();          // short-circuit: stops at first match
// Only 4 elements processed, not 10

// Fusion -- adjacent map operations are merged
Stream.of("a", "b", "c")
    .map(String::toUpperCase)
    .map(s -> s + "!")
    // JVM may fuse these into a single pass -- no intermediate collection created
    .toList();
```

**Ordering matters for performance:**
```java
// SLOW: filter after sorted -- sorts all 1M elements, then filters
employees.stream()
    .sorted(...)
    .filter(e -> e.getSalary() > 100000)
    .toList();

// FAST: filter before sorted -- sorts only matching elements
employees.stream()
    .filter(e -> e.getSalary() > 100000)  // reduces to small subset first
    .sorted(...)
    .toList();

// SLOW: distinct after map when many duplicates
names.stream().map(String::toLowerCase).distinct().count();

// Stateful operations (sorted, distinct, limit) break lazy fusion for parallel streams
```

---

### Q73: Parallel Streams -- when to use, when NOT to use

Parallel streams split the source into sub-streams, process them on the **ForkJoinPool.commonPool()** (shared across the JVM), then combine results.

```java
// Sequential
long count = names.stream().filter(s -> s.length() > 5).count();

// Parallel -- automatically uses ForkJoinPool
long countP = names.parallelStream().filter(s -> s.length() > 5).count();

// Convert between sequential and parallel
stream.parallel();    // make parallel
stream.sequential();  // make sequential
stream.isParallel();  // check
```

**WHEN parallel streams help:**
- Large data sets (typically 10,000+ elements)
- CPU-bound operations (no I/O waiting)
- Stateless, independent operations (no shared mutable state)
- Operations with high per-element cost

**WHEN parallel streams HURT:**
```java
// 1. Small data -- thread overhead exceeds computation benefit
List<Integer> small = List.of(1, 2, 3, 4, 5);
small.parallelStream().sum();  // SLOWER than sequential

// 2. I/O-bound operations -- threads block, others starve the common pool
files.parallelStream().map(file -> readFromDatabase(file))...
// DANGER: starves ForkJoinPool.commonPool() for the whole JVM

// 3. Ordered operations -- findFirst, forEachOrdered are expensive parallel
names.parallelStream().findFirst();  // parallel then re-order = overhead

// 4. Stateful lambdas -- race condition
List<Integer> results = new ArrayList<>();
// BUG: ArrayList is not thread-safe
numbers.parallelStream().filter(n -> n > 5).forEach(results::add);

// CORRECT: use collect() which is thread-safe
List<Integer> safe = numbers.parallelStream()
    .filter(n -> n > 5)
    .collect(Collectors.toList());

// 5. Sources with poor splittability
// LinkedList: bad splittable source (no random access)
// ArrayList, Arrays: good splittable sources
```

**Custom ForkJoinPool (isolate from common pool):**
```java
// Avoid blocking common pool for the whole JVM
ForkJoinPool customPool = new ForkJoinPool(4);
try {
    List<Result> results = customPool.submit(
        () -> largeList.parallelStream()
            .map(this::heavyComputation)
            .toList()
    ).get();
} finally {
    customPool.shutdown();
}
```

**Performance guideline:**
> Parallel streams are worth considering when: N * Q > 10000, where N = number of elements and Q = cost per element in "operations". Below this threshold, the overhead of splitting and combining typically exceeds the gain.

---

### Q74: Optional -- complete guide

`Optional<T>` is a container that may or may not hold a non-null value. It is designed for **return types only** -- not for fields, parameters, or collections.

**Creating Optional:**
```java
Optional<String> present = Optional.of("hello");         // throws NPE if null
Optional<String> nullable = Optional.ofNullable(maybeNull); // empty if null
Optional<String> empty   = Optional.empty();
```

**The critical difference: orElse vs orElseGet:**

```java
User user = getUser();

// orElse: ALWAYS evaluates the default expression
// Even if Optional is present, createDefaultUser() IS called
User result1 = Optional.ofNullable(user)
    .orElse(createDefaultUser());  // createDefaultUser() executes regardless!

// orElseGet: LAZILY evaluates -- only calls supplier if Optional is empty
// createDefaultUser() is NOT called if Optional has a value
User result2 = Optional.ofNullable(user)
    .orElseGet(() -> createDefaultUser());  // only if absent

// RULE: use orElseGet when default requires computation, DB call, or object creation
// Use orElse only for constants or simple values already computed
```

**orElseThrow:**
```java
// Java 10+: no-arg version throws NoSuchElementException
User user1 = optionalUser.orElseThrow();

// Custom exception
User user2 = optionalUser
    .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
```

**map, flatMap, filter:**
```java
// map: transform the value if present (returns Optional of result)
Optional<String> upperName = optionalUser
    .map(User::getName)              // Optional<String>
    .map(String::toUpperCase);       // Optional<String>

// flatMap: use when mapping function itself returns Optional
// Avoids Optional<Optional<T>>
Optional<Address> address = optionalUser
    .flatMap(User::getAddress);      // User.getAddress() returns Optional<Address>

// Chaining flatMap for deep navigation (safe null traversal)
Optional<String> city = optionalUser
    .flatMap(User::getAddress)       // Optional<Address>
    .flatMap(Address::getCity)       // Optional<String>
    .map(String::toUpperCase);

// filter: apply predicate, returns empty Optional if predicate fails
Optional<User> activeUser = optionalUser
    .filter(User::isActive);

// ifPresent: execute if value present (void, no return)
optionalUser.ifPresent(u -> sendWelcomeEmail(u.getEmail()));

// ifPresentOrElse (Java 9+): handle both cases
optionalUser.ifPresentOrElse(
    u -> logger.info("Found user: {}", u.getName()),
    () -> logger.warn("User not found"));

// or (Java 9+): return another Optional if empty
Optional<User> found = primaryCache.findUser(id)
    .or(() -> secondaryCache.findUser(id))   // try secondary if primary empty
    .or(() -> database.findUser(id));         // try DB if both caches empty

// stream (Java 9+): bridge to Stream API
long count = optionalUser.stream()  // 0 or 1 element stream
    .flatMap(u -> u.getOrders().stream())
    .count();
```

**Common Optional anti-patterns:**
```java
// ANTI-PATTERN 1: Optional.get() without isPresent check
// Just as bad as null -- throws NoSuchElementException
String bad = optional.get();  // potentially throws

// ANTI-PATTERN 2: Optional as method parameter
public void process(Optional<User> user) { ... }  // wrong -- use overloading

// ANTI-PATTERN 3: Optional in collections
List<Optional<User>> wrong = new ArrayList<>();  // wrong -- just use null or skip

// ANTI-PATTERN 4: Optional for primitive fields
public class User {
    private Optional<String> middleName;  // wrong -- use @Nullable or just String
}

// ANTI-PATTERN 5: isPresent + get() = old null check rewritten
if (optional.isPresent()) {
    User u = optional.get();  // verbose -- same as null check
    process(u);
}
// CORRECT: use map / ifPresent / orElse
optional.ifPresent(this::process);

// CORRECT USAGE: Optional as return type only
public Optional<User> findById(Long id) { ... }
```

---

### Q75: Default and static methods in interfaces

**Default methods** allow interfaces to have method implementations without breaking existing implementors -- enabling backward-compatible API evolution.

```java
// Pre-Java 8 problem: adding method to interface breaks ALL implementations
// Java 8 solution: default method
public interface Collection<E> {
    // Before Java 8: adding removeIf() would break every custom Collection
    // Java 8: default method -- existing implementations inherit it
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}

// Custom interface with default method
public interface Printable {
    String getContent();  // abstract -- must implement

    default void print() {                          // default -- optional override
        System.out.println(getContent());
    }

    default void printFormatted(String format) {
        System.out.printf(format, getContent());
    }
}

// Override if needed
class Report implements Printable {
    private String content;
    @Override public String getContent() { return content; }
    // Uses inherited print() or override it
}
```

**Static methods in interfaces:**
```java
public interface Validator<T> {
    boolean validate(T value);

    // Static factory methods (Java 8+)
    static <T> Validator<T> of(Predicate<T> predicate) {
        return predicate::test;  // method reference to lambda
    }

    // Combine validators
    default Validator<T> and(Validator<T> other) {
        return value -> this.validate(value) && other.validate(value);
    }
}

// Usage
Validator<String> notEmpty   = Validator.of(s -> !s.isEmpty());
Validator<String> maxLen     = Validator.of(s -> s.length() <= 100);
Validator<String> emailValid = notEmpty.and(maxLen).and(s -> s.contains("@"));
```

**Diamond problem with default methods:**
```java
interface A { default void greet() { System.out.println("A"); } }
interface B { default void greet() { System.out.println("B"); } }

// Compile error: class must override ambiguous default
class C implements A, B {
    @Override
    public void greet() {
        A.super.greet();  // explicitly choose A's implementation
    }
}

// Resolution rules:
// 1. Class method > interface default (class always wins)
// 2. More specific interface > less specific (sub-interface wins)
// 3. If still ambiguous: compiler error (must override explicitly)
```

---

### Q76: Common interview coding challenges -- Streams

**Challenge 1: Find second highest salary**
```java
// Common approach
Optional<Integer> secondHighest = employees.stream()
    .mapToInt(Employee::getSalary)
    .boxed()
    .distinct()                // handle duplicates
    .sorted(Comparator.reverseOrder())
    .skip(1)
    .findFirst();

secondHighest.ifPresentOrElse(
    s -> System.out.println("Second highest: " + s),
    () -> System.out.println("Not enough distinct salaries"));
```

**Challenge 2: Group anagrams**
```java
List<String> words = List.of("eat", "tea", "tan", "ate", "nat", "bat");

Map<String, List<String>> anagramGroups = words.stream()
    .collect(Collectors.groupingBy(w -> {
        char[] chars = w.toCharArray();
        Arrays.sort(chars);
        return new String(chars);  // sorted chars as key
    }));
// {aet=[eat, tea, ate], ant=[tan, nat], abt=[bat]}
```

**Challenge 3: Word frequency count**
```java
String text = "to be or not to be that is the question";

Map<String, Long> frequency = Arrays.stream(text.split("\\s+"))
    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

// Top 3 most frequent
frequency.entrySet().stream()
    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
    .limit(3)
    .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
```

**Challenge 4: Flatten nested structure and aggregate**
```java
// Each department has a list of employees; find total salary bill per department
Map<String, Integer> totalSalaryByDept = departments.stream()
    .collect(Collectors.toMap(
        Department::getName,
        dept -> dept.getEmployees().stream()
            .mapToInt(Employee::getSalary)
            .sum()));
```

**Challenge 5: Partition students by pass/fail**
```java
Map<Boolean, List<Student>> result = students.stream()
    .collect(Collectors.partitioningBy(s -> s.getScore() >= 60));

List<Student> passed = result.get(true);
List<Student> failed = result.get(false);
System.out.printf("Pass: %d, Fail: %d%n", passed.size(), failed.size());
```

**Challenge 6: Find first non-repeating character**
```java
String s = "leetcode";

Optional<Character> first = s.chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()))
    .entrySet().stream()
    .filter(e -> e.getValue() == 1)
    .map(Map.Entry::getKey)
    .findFirst();
// 'l'
```

---

### Q77: Tricky interview questions and gotchas

**Q: What happens when you reuse a stream?**
```java
Stream<String> stream = list.stream();
stream.filter(s -> s.length() > 3).toList();  // OK
stream.count();  // IllegalStateException: stream has already been operated upon or closed
// Streams are single-use. Create a new stream for each pipeline.
```

**Q: Stream vs Iterator -- key differences**

| | Stream | Iterator |
|---|---|---|
| Traversal | Internal (you define what, not how) | External (you control the loop) |
| Parallelism | Built-in (parallelStream) | Manual, complex |
| Lazy | Yes | No |
| Functional | Yes (map, filter, reduce) | No |
| Reusable | No (single-use) | Varies |

**Q: Difference between map() and peek()**
```java
// map: transforms elements, returns new stream with transformed values
// peek: non-interfering, for debugging/side-effects, passes SAME elements through
names.stream()
    .peek(s -> logger.debug("Before: {}", s))  // side-effect only
    .map(String::toUpperCase)                  // actual transformation
    .peek(s -> logger.debug("After: {}", s))
    .toList();
// peek is ONLY for debugging. Never use peek for logic -- not guaranteed to execute
// (e.g., peek after count() never runs if stream is empty)
```

**Q: Why should you avoid forEach for logic?**
```java
// WRONG: using forEach with external state mutation
List<String> results = new ArrayList<>();
names.stream()
    .filter(s -> s.length() > 3)
    .forEach(results::add);  // mutable, not thread-safe in parallel

// RIGHT: use collect
List<String> results = names.stream()
    .filter(s -> s.length() > 3)
    .collect(Collectors.toList());
```

**Q: Why is sorted() expensive in parallel streams?**
> `sorted()` is a stateful intermediate operation -- it must see ALL elements before it can produce any output. In a parallel stream, this forces a merge of all sub-streams before sorting can begin, effectively negating the parallelism benefit. Consider using `Collections.sort()` for large sort-only operations.

**Q: What is a spliterator?**
> A `Spliterator` (splittable iterator) is the underlying mechanism for parallel stream splitting. It knows how to divide its element source into parts for parallel processing and reports characteristics (ORDERED, DISTINCT, SORTED, SIZED, NONNULL, IMMUTABLE, CONCURRENT, SUBSIZED). `ArrayList` has an efficient `ArraySpliterator`; `LinkedList` does not support efficient splitting.

**Q: Difference between findFirst() and findAny()?**
```java
// findFirst: always returns the first element in encounter order
// Sequential: deterministic
// Parallel: forces ordering re-assembly -- expensive

// findAny: returns any element (non-deterministic)
// Sequential: same as findFirst (finds first in practice)
// Parallel: much faster -- no need to coordinate order between threads

// Rule: use findFirst when order matters, findAny when you just need any match (parallel)
```

---

### Q78: Streams performance best practices

```java
// 1. Prefer primitive streams to avoid boxing overhead
// BAD: boxes each int to Integer
int sum = list.stream().mapToObj(n -> n).reduce(0, Integer::sum);
// GOOD: no boxing
int sum = list.stream().mapToInt(Integer::intValue).sum();

// 2. Use lazy short-circuit operations
// BAD: processes entire stream
boolean found = employees.stream()
    .filter(e -> expensiveCheck(e))
    .count() > 0;
// GOOD: stops at first match
boolean found = employees.stream().anyMatch(e -> expensiveCheck(e));

// 3. Filter early, map late
// BAD: maps all, then filters
employees.stream().map(Employee::getDetails).filter(d -> d.isActive())...
// GOOD: filter first (reduces elements), map smaller set
employees.stream().filter(Employee::isActive).map(Employee::getDetails)...

// 4. Close streams that wrap I/O resources
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(l -> l.contains("ERROR")).forEach(logger::error);
}  // auto-closed

// 5. Avoid stateful lambdas in parallel streams
List<Integer> bad = new ArrayList<>();
numbers.parallelStream().forEach(bad::add);  // race condition

// 6. Prefer collect over reduce for mutable containers
// reduce is better for immutable accumulation (sum, product, concatenation of immutables)
// collect is better for mutable containers (List, Map, StringBuilder)

// 7. toList() (Java 16+) is preferred over collect(Collectors.toList())
// Returns unmodifiable list, slightly more efficient
List<String> names = stream.map(Employee::getName).toList();  // Java 16+
```

---

### Java 8 Summary -- Quick Revision Table

| Feature | One-liner | Key interview point |
|---|---|---|
| Lambda | Anonymous function for SAM interface | Captured vars must be effectively final |
| Functional Interface | `@FunctionalInterface` with exactly 1 abstract method | `Predicate`, `Function`, `Consumer`, `Supplier` |
| Method Reference | Shorthand for simple lambda delegation | 4 types: static, bound, unbound, constructor |
| Stream | Lazy, functional data processing pipeline | Intermediate (lazy) vs terminal (eager) |
| `map` vs `flatMap` | 1:1 transform vs 1:many + flatten | Use `flatMap` when mapping returns container |
| `orElse` vs `orElseGet` | Always evaluates vs lazy evaluation | Use `orElseGet` for expensive defaults |
| `collect` vs `reduce` | Mutable accumulation vs immutable fold | `groupingBy`, `partitioningBy`, `toMap` |
| `findFirst` vs `findAny` | Ordered vs any-match | `findAny` faster in parallel streams |
| Default method | Interface method with body | Diamond: must override; class wins over interface |
| Optional | Null-safe return wrapper | Never use as field/parameter/collection element |
| Parallel stream | ForkJoinPool-based parallel processing | Hurts for small data, I/O-bound, stateful ops |


## 17. Java 9–11 Features

---

### Q79: Java 9 — Modules, collection factories, Stream enhancements.

```java
// module-info.java
module com.company.orders {
    requires java.sql;
    requires transitive com.company.common;
    exports com.company.orders.api;
    opens com.company.orders.model to com.fasterxml.jackson.databind;
}

// Collection factory methods (immutable — null not allowed)
List<String>        list = List.of("a", "b", "c");
Set<Integer>        set  = Set.of(1, 2, 3);
Map<String,Integer> map  = Map.of("k1", 1, "k2", 2);

// Stream enhancements
Stream.of(1,2,3,4,5).takeWhile(n -> n < 4).forEach(System.out::print); // 1,2,3
Stream.of(1,2,3,4,5).dropWhile(n -> n < 4).forEach(System.out::print); // 4,5

Stream.ofNullable(nullableValue)  // empty stream if null — avoids NPE
    .map(String::toUpperCase).findFirst();

// Optional.stream() — bridge Optional to Stream
employees.stream()
    .flatMap(e -> e.getManager().stream())  // Optional<Manager> → Stream<Manager>
    .collect(Collectors.toList());
```

---

### Q80: Java 10 — var, Java 11 — HttpClient and String methods.

```java
// Java 10 var — local variable type inference (compile-time, not dynamic)
var list   = new ArrayList<String>();     // ArrayList<String>
var map    = new HashMap<String, List<Order>>();
for (var entry : map.entrySet()) {        // Map.Entry<String, List<Order>>
    System.out.println(entry.getKey());
}
// Cannot use: var x; | var n = null; | method params | fields | return types

// Java 11 — HttpClient
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .build();

// Sync
HttpResponse<String> resp = client.send(
    HttpRequest.newBuilder().uri(URI.create("https://api.example.com")).GET().build(),
    HttpResponse.BodyHandlers.ofString()
);

// Async
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)
    .thenAccept(System.out::println);

// Java 11 String methods
"  hello  ".strip();        // Unicode-aware (handles \u2000 etc.) vs trim() (ASCII-only)
"  ".isBlank();             // true
"hello\nworld".lines()      // Stream<String> ["hello", "world"]
"ab".repeat(3);             // "ababab"
"hello".stripLeading();     // "hello" (already no leading space)
Path.of("file.txt").readString();  // Java 11 — read entire file as String
```

---

## 18. Java 12–16 Features

---

### Q81: switch expressions (Java 14), text blocks (Java 15), records (Java 16).

```java
// Switch expression (Java 14 — no fall-through, compile-time exhaustiveness check)
// OLD switch statement:
int numLetters;
switch (day) {
    case MONDAY: case FRIDAY: numLetters = 6; break;
    case TUESDAY: numLetters = 7; break;
    // missing break → silent fall-through bug!
    default: numLetters = -1;
}

// NEW switch expression:
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY   -> 6;
    case TUESDAY                  -> 7;
    case THURSDAY, SATURDAY       -> 8;
    case WEDNESDAY                -> 9;
};  // compiler error if any case missing

// Multi-statement case with yield:
int result = switch (op) {
    case "DIVIDE" -> {
        if (b == 0) throw new ArithmeticException("Division by zero");
        yield a / b;  // yield = "return value from expression block"
    }
    default -> throw new IllegalArgumentException("Unknown: " + op);
};

// Text blocks (Java 15) — indentation stripped, trailing newline included
String sql = """
        SELECT u.id, u.name, o.total
        FROM users u
        JOIN orders o ON u.id = o.user_id
        WHERE u.active = true
          AND o.created_at > :since
        ORDER BY o.total DESC
        LIMIT :limit
        """;  // closing """ position controls indentation stripping

// Records (Java 16) — immutable data carriers
public record PageRequest(int page, int size, String sortBy) {
    public PageRequest {  // compact constructor
        if (page < 0)  throw new IllegalArgumentException("page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be 1-100");
        sortBy = sortBy != null ? sortBy : "id";
    }
    public int offset() { return page * size; }
}
// Auto-generated: canonical constructor, page(), size(), sortBy(), equals(), hashCode(), toString()
// Records are final — cannot be subclassed
```

---

## 19. Java 17 and 21 — LTS Deep Dive

---

### Q82: Java 17 — what makes it the enterprise standard.

Java 17 (Sept 2021, LTS) was the graduation of features previewed in Java 12–16:

```java
// Sealed classes (finalized — complete closed hierarchy)
public sealed interface Result<T> permits Success, Failure, Pending {}
public record Success<T>(T value) implements Result<T> {}
public record Failure<T>(String error, Throwable cause) implements Result<T> {}
public record Pending<T>(String requestId) implements Result<T> {}

// Domain modelling without casting:
<T> String summarise(Result<T> r) {
    return switch (r) {
        case Success<T> s  -> "OK: " + s.value();
        case Failure<T> f  -> "ERROR: " + f.error();
        case Pending<T> p  -> "PENDING: " + p.requestId();
    };
}

// Strong JDK encapsulation (--add-opens no longer works for internal APIs)
// Many frameworks (Spring 6, Hibernate 6) were updated to be Java 17 compatible
// Performance: ~15-20% faster than Java 11 in typical web workloads
```

**Why upgrade from Java 11 → 17:**
- Sealed classes + records + pattern matching — cleaner domain models
- Better GC: ZGC and Shenandoah production-stable
- Text blocks, switch expressions — less boilerplate
- Security: deprecated dangerous APIs removed
- Long-term support until 2029 (Oracle) or longer (Adoptium)

---

### Q83: Java 21 — Virtual Threads, Structured Concurrency, Sequenced Collections.

```java
// Virtual Threads (JEP 444 — finalized)
// Key insight: "virtual thread per request" model replaces thread pool model for I/O

// Platform thread pool (OLD way — limits concurrency to pool size)
ExecutorService pool = Executors.newFixedThreadPool(200);
// 200 OS threads max, each ~1MB stack → 200MB base memory
// If all 200 are blocking on DB/HTTP → 201st request waits in queue

// Virtual thread per task (NEW way — no concurrency limit)
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 100_000).forEach(i ->
        exec.submit(() -> {
            Thread.sleep(100);  // blocks virtual thread, NOT the OS thread
            return process(i);
        })
    );
}
// 100K virtual threads run with only ~8-16 OS (carrier) threads

// Structured Concurrency (JEP 453 — preview in 21)
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<User>    user  = scope.fork(() -> fetchUser(id));
    Future<Orders>  orders = scope.fork(() -> fetchOrders(id));
    Future<Account> acct  = scope.fork(() -> fetchAccount(id));

    scope.join().throwIfFailed();
    // If any fork fails → all are cancelled → no leaked threads
    return new Dashboard(user.resultNow(), orders.resultNow(), acct.resultNow());
}

// Sequenced Collections — uniform first/last API
List<String> list = new ArrayList<>(List.of("a","b","c"));
list.getFirst();    // "a" — replaces list.get(0)
list.getLast();     // "c" — replaces list.get(list.size()-1)
list.addFirst("z"); list.addLast("x");
List<String> rev = list.reversed();  // reversed view (no copy)

LinkedHashMap<String,Integer> lhm = new LinkedHashMap<>();
lhm.put("one", 1); lhm.put("two", 2); lhm.put("three", 3);
lhm.firstEntry();   // "one"=1
lhm.lastEntry();    // "three"=3
```

**Virtual threads vs reactive (WebFlux/RxJava):**
| | Virtual Threads | Reactive |
|---|---|---|
| Code style | Synchronous (readable) | Callback/operator chains (complex) |
| Stack traces | Normal, readable | Reactor-specific, cryptic |
| Learning curve | Low | High |
| Existing code | Mostly compatible | Requires full rewrite |
| CPU-bound | No advantage | No advantage |
| I/O throughput | Equivalent | Equivalent |

*Production recommendation:* Use virtual threads for new Java 21 services. Consider reactive only for complex backpressure scenarios.

---

## 20. Future Java and Best Practices

---

### Q84: JDK distributions and upgrade best practices.

**JDK distributions:**
| Distribution | License | LTS | Recommended For |
|---|---|---|---|
| Eclipse Temurin | Apache 2.0 | Yes (8,11,17,21) | Default choice |
| Amazon Corretto | Apache 2.0 | Yes | AWS workloads |
| Oracle JDK | Commercial | Yes | Oracle support contracts |
| Azul Zulu | Mixed | Yes | Commercial support needed |
| GraalVM CE | GPLv2 | Yes | Native image, polyglot |

**Java upgrade strategy:**
```
1. Start with a new project — adopt the new LTS immediately
2. Upgrade existing projects:
   a. Use --add-opens flags to identify encapsulation violations
   b. Run with target JDK in test environment for 1-2 sprints
   c. Update framework versions (Spring 6+ for Java 17+)
   d. Fix deprecated API usage (use jdeprscan)
   e. Test GC behaviour — ZGC may need tuning
3. Never skip LTS versions: 8 → 11 → 17 → 21 (not 8 → 21 directly in one step)
```

---

### Q85: JIT vs AOT, GraalVM Native Image.

```
JIT (Just-In-Time) — Default
  Startup: 300-500ms (interpreter + C1 + C2 compilation)
  Peak throughput: very high (profile-guided optimization)
  Memory: 200-400MB RSS minimum
  Best for: long-running services (API servers, batch jobs)

AOT (Ahead-of-Time) — GraalVM Native Image
  Startup: 5-15ms
  Memory: 30-100MB RSS
  Peak throughput: 10-20% lower (no JIT profiling)
  Build time: 2-5 minutes
  Best for: serverless (Lambda, Cloud Run), CLI tools, microservices with fast cold start SLA
  Limitations: reflection/dynamic class loading must be configured; no agent attachment

Build native image:
  ./mvnw -Pnative native:compile   (Spring Boot 3+ native support)
  docker build -f Dockerfile.native .
```

---

# Part 4: Real-World Scenarios

---

## 21. System Design Interview Questions

---

### Q86: Design a thread-safe bounded blocking queue from scratch.

```java
// Classic interview question — tests lock/condition variable knowledge
public class BoundedBlockingQueue<T> {
    private final Object[] elements;
    private int head, tail, size;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        elements = new Object[capacity];
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (size == elements.length) notFull.await();  // release lock and wait
            elements[tail] = item;
            tail = (tail + 1) % elements.length;
            size++;
            notEmpty.signal();  // wake one waiting consumer
        } finally { lock.unlock(); }
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) notEmpty.await();
            T item = (T) elements[head];
            elements[head] = null;  // prevent memory leak
            head = (head + 1) % elements.length;
            size--;
            notFull.signal();  // wake one waiting producer
            return item;
        } finally { lock.unlock(); }
    }
}
// Key points: use while (not if) for wait condition — protects against spurious wakeups
// Two separate Conditions: prevents waking up the wrong waiting thread
```

---

### Q87: Design a distributed rate limiter.

```java
// Token Bucket (in-process)
public class TokenBucketLimiter {
    private final long capacity;
    private final double refillRate;  // tokens per second
    private double tokens;
    private long lastRefill = System.nanoTime();

    public TokenBucketLimiter(long capacity, double rps) {
        this.capacity = capacity; this.refillRate = rps; this.tokens = capacity;
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1) { tokens--; return true; }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        tokens = Math.min(capacity, tokens + (now - lastRefill) / 1e9 * refillRate);
        lastRefill = now;
    }
}

// Distributed (Redis + Lua for atomicity)
// Sliding window counter:
// KEYS[1] = ratelimit:<userId>
// ARGV[1] = window_ms  ARGV[2] = max_requests
//
// local count = redis.call('INCR', KEYS[1])
// if count == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]) end
// return count > tonumber(ARGV[2]) and 0 or 1

// Spring Gateway built-in rate limiter:
// spring.cloud.gateway.routes[0].filters[0]=RequestRateLimiter=10, 20, #{@userKeyResolver}
```

---

### Q88: Design a URL shortener service.

```
REQUIREMENTS
  Functional: shorten URL, redirect short → long, optional custom alias
  Non-functional: 1B links, 100M daily redirects, low latency (<50ms p99)

HIGH-LEVEL DESIGN
  ┌─────┐   POST /shorten   ┌─────────────────┐   write   ┌─────────┐
  │     │ ────────────────► │  URL Service     │ ────────► │  DB     │
  │ CLI │                   │  (Spring Boot)   │           │(MySQL + │
  │     │ ◄──────────────── │  generate shortId│           │ Redis)  │
  └─────┘   {shortId}       └─────────────────┘           └─────────┘
                                     │ GET /{shortId}
                                     ▼
                             Cache hit → 301 redirect
                             Cache miss → DB lookup → cache → redirect

SHORT ID GENERATION
  Base62 encoding of auto-increment ID: [0-9a-zA-Z] → 62^6 = 56B URLs for 6-char codes
  Or: MD5(longUrl + salt), take first 6 chars (collision handled with counter suffix)

DATABASE SCHEMA
  urls(id BIGINT PK, short_id VARCHAR(8) UNIQUE INDEX, long_url TEXT,
       created_at TIMESTAMP, user_id BIGINT, expires_at TIMESTAMP, clicks BIGINT)

CACHING
  Redis: short_id → long_url (TTL = 24h for popular links)
  CDN: cache 301 redirects at edge for zero-server-load on top 1% links

SCALING
  Read-heavy: read replicas + Redis cluster
  Write: horizontal sharding by short_id hash
  Analytics: async click counting via Kafka → ClickHouse
```

---

### Q89: Design a database connection pool.

```java
public class ConnectionPool {
    private final BlockingQueue<Connection> pool;
    private final List<Connection> allConnections = new ArrayList<>();
    private final String url, user, password;
    private final int maxSize;

    public ConnectionPool(String url, String user, String password,
                          int minIdle, int maxSize) throws SQLException {
        this.url = url; this.user = user; this.password = password;
        this.maxSize = maxSize;
        this.pool = new LinkedBlockingQueue<>(maxSize);
        // Pre-create minIdle connections
        for (int i = 0; i < minIdle; i++) {
            Connection conn = createConnection();
            pool.offer(conn);
            allConnections.add(conn);
        }
    }

    public Connection borrow(long timeout, TimeUnit unit) throws Exception {
        Connection conn = pool.poll(timeout, unit);
        if (conn == null) {
            // Try to grow pool
            synchronized(this) {
                if (allConnections.size() < maxSize) {
                    conn = createConnection();
                    allConnections.add(conn);
                    return conn;
                }
            }
            throw new TimeoutException("Connection pool exhausted after " + timeout + " " + unit);
        }
        if (!isValid(conn)) {
            conn.close(); allConnections.remove(conn);
            return borrow(timeout, unit);
        }
        return conn;
    }

    public void release(Connection conn) {
        if (conn != null && !conn.isClosed()) pool.offer(conn);
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    private boolean isValid(Connection c) {
        try { return c.isValid(1); } catch (SQLException e) { return false; }
    }
}
```

---

## 22. Production Debugging and Troubleshooting

---

### Q90: OutOfMemoryError — diagnosis and resolution.

```bash
# Step 1: Enable automatic heap dump on OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/dumps/

# Step 2: Capture on-demand heap dump (if not crashed yet)
jcmd <pid> GC.heap_dump /var/dumps/heap-$(date +%Y%m%d%H%M%S).hprof

# Step 3: Eclipse MAT analysis
# Open heap dump → "Leak Suspects" → shows what's holding the most memory
# "Dominator tree" → shows what's preventing GC
# "OQL" → query objects: SELECT * FROM java.util.HashMap$Entry

# Step 4: Fix based on OOM type:
OOM: Java heap space       → increase -Xmx OR fix memory leak
OOM: GC overhead exceeded  → heap too small OR pathological object creation
OOM: Metaspace             → classloader leak (dynamic proxies, frameworks)
OOM: Direct buffer memory  → NIO/Netty buffer leak → fix AutoCloseable usage
```

---

### Q91: Thread dump analysis.

```bash
# Capture 3 thread dumps 10s apart (to identify stuck threads vs genuinely blocked)
for i in 1 2 3; do jstack -l <pid> > threads_$i.txt; sleep 10; done

# Key patterns:
# 1. Deadlock — JStack explicitly reports it at the bottom
#    "Thread-A" waiting for lock held by "Thread-B"
#    "Thread-B" waiting for lock held by "Thread-A"
#    Found 1 deadlock.
#
# 2. Thread pool saturation — all worker threads in WAITING/BLOCKED
#    "http-nio-8080-exec-1..20" all WAITING → pool full, requests queuing
#
# 3. Hot loop — same thread shows same stack in all 3 dumps
#    RUNNABLE, same frame → infinite loop or very tight loop
#
# 4. Lock contention
#    Many threads BLOCKED on same lock object address
#    → reduce lock scope, use finer-grained locking, or ConcurrentHashMap

# Fast tools
jcmd <pid> Thread.print -l  # JDK thread dump
kill -3 <pid>               # SIGQUIT on Linux — dumps to stdout
```

---

### Q92: High latency diagnosis checklist.

```
LATENCY INVESTIGATION CHECKLIST

1. Is it consistent or spiky?
   Spiky → GC pauses (check GC logs for long STW)
   Consistent → lock contention, slow DB, downstream service

2. Check GC:
   -Xlog:gc*:file=gc.log
   Look for: full GC > 1s, young GC > 200ms
   Fix: tune G1GC, increase heap, switch to ZGC

3. Check thread dump:
   Many BLOCKED threads → lock contention
   Many WAITING threads → downstream is slow (DB, API)

4. Check DB:
   Enable slow query log (> 100ms)
   EXPLAIN ANALYZE on suspicious queries
   Check connection pool exhaustion (metrics: pool.pending > 0)

5. Check downstream services:
   distributed tracing (Jaeger, Zipkin, AWS X-Ray)
   p99 latency per service hop

6. Check application metrics:
   JVM heap used, GC pause time, active threads
   HTTP response time histogram (Prometheus/Micrometer)
   DB query histogram
```

---

## 23. Code Quality and Best Practices

---

### Q80: SOLID principles with Java examples.

```java
// S — Single Responsibility: one reason to change
// BAD: OrderProcessor handles business logic + email + PDF + persistence
// GOOD: OrderService, OrderEmailService, OrderReportService, OrderRepository

// O — Open/Closed: open for extension, closed for modification
interface DiscountStrategy { BigDecimal apply(Order o); }
class SeasonalDiscount implements DiscountStrategy { ... }
class LoyaltyDiscount  implements DiscountStrategy { ... }
// New discount type = new class, NO change to existing code

// L — Liskov Substitution: subtypes must be substitutable
// VIOLATION: Square extends Rectangle — setWidth changes both dimensions → breaks caller
// FIX: Shape interface, Rectangle and Square are independent implementations

// I — Interface Segregation: don't force clients to depend on methods they don't use
// BAD: interface Worker { void work(); void eat(); void rest(); }
// Robot implements Worker but doesn't eat → forced to throw UnsupportedOperationException
// GOOD: interface Workable { void work(); }  interface Feedable { void eat(); }

// D — Dependency Inversion: depend on abstractions, not concretions
// BAD:
class OrderService { private MySqlOrderRepository repo = new MySqlOrderRepository(); }
// GOOD:
class OrderService {
    private final OrderRepository repo;  // interface
    public OrderService(OrderRepository repo) { this.repo = repo; }
    // Now testable with InMemoryOrderRepository, MongoOrderRepository, etc.
}
```

---

### Q81: Technical debt management and refactoring.

**Technical debt** = the cost of rework caused by taking shortcuts today.

```
TYPES:
  Intentional (prudent):  "Ship now, refactor in sprint 3" — tracked, planned
  Intentional (reckless): "We don't have time for tests" — no plan, compounds
  Unintentional:          Poor design noticed after the fact

MANAGING TECHNICAL DEBT:
  1. Make it visible:  Tech debt backlog with estimated fix cost
  2. Boy Scout Rule:   Leave code slightly cleaner with every PR
  3. Budget 20%:       Reserve sprint capacity for debt reduction
  4. Set thresholds:   SonarQube quality gates: no new critical issues allowed

REFACTORING SAFELY:
  1. Write characterisation tests FIRST (capture current behaviour)
  2. Refactor in small, verifiable steps
  3. Run tests after each step
  4. Never refactor + add features in the same commit

SIGNALS THAT DEBT IS CRITICAL:
  - Cyclomatic complexity > 15 (SonarQube reports this)
  - Class > 500 lines / method > 50 lines
  - Test coverage < 30%
  - Every bug fix breaks 2 other things
```

---

### Q82: Clean code naming and best practices.

```java
// Naming conventions (from 15 years of code review)
// Classes: nouns (OrderProcessor, UserValidator, PaymentGateway)
// Methods: verbs (processOrder, validateUser, chargeCard)
// Booleans: is/has/can/should prefix (isActive, hasPermission, canRetry)
// Constants: SCREAMING_SNAKE_CASE
// Avoid: data, info, manager, handler (too vague)

// Method length: if it doesn't fit on one screen, split it
// Cyclomatic complexity: if > 5 conditions in one method, extract

// Self-documenting code vs comments
// BAD comment: // increment counter
count++;
// GOOD comment: explains WHY, not WHAT
// Retry up to 3 times because the payment gateway occasionally returns 503
// on first attempt during deployment windows
for (int attempt = 0; attempt < 3; attempt++) { ... }

// Avoid magic numbers
if (age > 18) { ... }              // what is 18?
if (age > LEGAL_ADULT_AGE) { ... } // self-documenting

// Immutability preference
// Prefer final fields, Collections.unmodifiableList(), List.copyOf()
// Return defensive copies of mutable collections
public List<Order> getOrders() {
    return Collections.unmodifiableList(orders);  // not the internal list
}
```

---

## 24. Interview Soft Skills and Communication

---

### Q83: How to approach an unfamiliar problem in an interview.

**STRUCTURED APPROACH (use every time):**

```
1. CLARIFY — spend 2-3 minutes before writing any code
   "What are the input constraints? Can the list be empty? Null?"
   "What are the scale requirements? Is this called once or millions of times?"
   "Should I optimise for time, space, or readability?"
   "Is this single-threaded or concurrent?"

2. THINK ALOUD — narrate your reasoning continuously
   "My first instinct is a HashMap for O(1) lookup..."
   "But that uses O(n) space, which might be a problem for large inputs..."
   "Let me think if a two-pointer approach would work here..."

3. START SIMPLE, THEN OPTIMISE
   Brute force first: "This is O(n²) and O(1) space — is that acceptable?"
   Then optimise: "I can trade space for time by sorting first..."

4. WRITE CLEAN CODE DURING INTERVIEWS
   - Meaningful variable names (not i, j, temp for complex logic)
   - Extract helper methods ("let me extract this into isValidEmail()")
   - Handle edge cases explicitly: null input, empty list, single element

5. TEST YOUR OWN SOLUTION
   "Let me trace through with [1, 2, 3] as input..."
   "Edge case: what if the list is empty? My code returns 0, which is correct."
   "Edge case: what if all elements are the same?"
```

---

### Q84: When you don't know the answer.

```
HONESTY > BLUFFING — senior interviewers respect intellectual honesty

Script for "I don't know":
  "I haven't worked with X directly, but I can reason from first principles:
   X seems similar to Y, which works by [mechanism]. I'd expect X to..."

  "I'm not certain about the exact API, but the general approach would be..."

  "I know the concept but I'd need to check the documentation for the specific
   syntax. Can I describe the approach and you verify I'm on the right track?"

What NOT to do:
  × Make up an API that doesn't exist
  × Give a confident wrong answer
  × Go silent — keep talking, show your reasoning process

What TO do:
  ✓ Show you know HOW to find the answer (docs, StackOverflow, experimentation)
  ✓ Make analogies to things you do know
  ✓ Ask clarifying questions to narrow the problem space
```

---

### Q85: Discussing trade-offs and communicating solutions.

```
TRADE-OFF FRAMEWORK — use for every technical decision

NEVER say: "X is always better than Y"
ALWAYS say: "X is better WHEN [condition], but Y is preferable WHEN [other condition]"

Examples:
  "ArrayList is better for random access O(1), but if I'm frequently
   inserting at the front, ArrayDeque would be more appropriate."

  "Microservices give us independent deployment and scaling, but they add
   distributed systems complexity — network failures, eventual consistency,
   distributed tracing. For a 5-person team, a modular monolith might be
   a better starting point."

  "Redis caching reduces DB load, but introduces cache invalidation complexity
   and eventual consistency. For a read-heavy, slowly-changing dataset, it's
   worth it. For financial data requiring strong consistency, I'd skip it."

TIME MANAGEMENT IN INTERVIEWS:
  - Algorithm questions: 5 min clarify, 5 min plan, 20 min code, 5 min test
  - System design: 5 min requirements, 10 min HLD, 20 min deep dive on one component
  - If stuck: "I'm going to take 30 seconds to think through this quietly"
  - Always leave 5 minutes to review your code for bugs
```

---

### Q16 Extended: HashMap Internals — Complete Deep Dive

**Explain how HashMap works internally — a comprehensive guide covering all aspects.**

#### HashMap Architecture & Implementation

**Explain how HashMap works internally.**

HashMap is a hash table implementation that stores key-value pairs using **bucket-based hashing**. Understanding its internals is critical for writing efficient Java code and acing system design interviews.

---

#### **Internal Structure Overview**

```java
// Simplified internal structure
public class HashMap<K,V> {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    transient Node<K,V>[] table;  // array of buckets
    transient int size;            // number of entries
    int threshold;                 // size at which to resize (capacity × load factor)
    transient int modCount;        // tracks structural modifications
}
```

**The bucket array:**
```
HashMap with capacity 16:

Index:  0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15
       ┌──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┐
       │  │  │  │  │  │  │  │  │  │  │  │  │  │  │  │  │
       └──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┘
        ↓
      Node (Entry)
    ┌───────────────┐
    │ hash  │ key   │
    │ value │ next  │ ← linked list for collision handling
    └───────────────┘
```

---

#### **How Hashing Works**

1. **Hash Computation:**
   ```java
   // Step 1: Call hashCode() on key
   int hash = key.hashCode();
   
   // Step 2: Spread the hash — redistribute bits to reduce collisions
   // Java 8+ uses a better spread function:
   static final int hash(Object key) {
       int h;
       return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
       // XOR with right-shifted bits spreads high bits to low bits
   }
   
   // Step 3: Map to bucket index using modulo
   int index = hash & (capacity - 1);
   // Using & instead of % because capacity is always a power of 2
   ```

2. **Example walkthrough:**
   ```
   Insert: map.put("Alice", 30)
   
   1. hashCode("Alice") → 64578211 (example)
   2. Spread bits: 64578211 ^ (64578211 >>> 16) → 64578326
   3. Capacity = 16 (binary: 10000)
      Index = 64578326 & 15 → 6
   4. Insert at bucket[6]
   ```

---

#### **Collision Handling — Evolution (Java 7 → Java 8+)**

**Java 7 and earlier: Always Linked List**
```java
// When two keys hash to the same index → collision
//
//  bucket[6]:  Node1 → Node2 → Node3 → null
//               (k1)   (k2)   (k3)
//
// Worst case: all entries in one bucket → O(n) lookup
// Vulnerable to hash flooding DoS attacks
```

**Java 8+: Linked List + Red-Black Tree**
```java
// When bucket size exceeds threshold (8 entries) AND capacity >= 64:
// Convert linked list to a Red-Black Tree
//
// bucket[6]:  Red-Black Tree
//             (O(log n) lookup instead of O(n))

static class Node<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;  // for linked list
}

static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;
    boolean red;
    
    // Red-Black Tree operations: O(log n)
}
```

**Why treeify at 8?**
```
With a uniform hash function, the probability of a bucket
having 8 or more entries follows Poisson(0.5).
  - P(>= 8) ≈ 6.3 × 10^-7  (extremely rare)

If you see a bucket with 8 entries in a uniform distribution,
it likely indicates either:
  1. Hash collision attack (bad hash function)
  2. Extremely unlucky randomness

Better to spend O(log n) per operation than risk O(n).
```

---

#### **Put Operation Deep Dive**

```java
// Simplified Java 8+ put() logic
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab;
    int n, i;
    
    // Step 1: Initialize or resize if needed
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    
    // Step 2: Compute bucket index and check if empty
    if ((tab[i = (n - 1) & hash]) == null) {
        tab[i] = newNode(hash, key, value, null);  // O(1)
    } else {
        Node<K,V> e;
        K k;
        
        // Step 3a: Check if first node matches (common case)
        Node<K,V> p = tab[i];
        if (p.hash == hash && ((k = p.key) == key || key.equals(k)))
            e = p;
        
        // Step 3b: Navigate through linked list or tree
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            // Linear search through linked list
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // If bucket size exceeded 7 (will be 8), treeify
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                    break;
                p = e;
            }
        }
        
        // Step 4: Update existing key
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    
    // Step 5: Update size and check if resize needed
    ++modCount;
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

**Constants in Java 8+:**
```java
static final int TREEIFY_THRESHOLD = 8;      // switch to tree at 8 nodes
static final int UNTREEIFY_THRESHOLD = 6;    // switch back to list at 6 nodes
static final int MIN_TREEIFY_CAPACITY = 64;  // minimum capacity to treeify
```

---

#### **Get Operation**

```java
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab;
    Node<K,V> first;
    int n;
    K k;
    
    // Step 1: Find bucket
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        
        // Step 2: Check first node (most common case)
        if (first.hash == hash && ((k = first.key) == key || key.equals(k)))
            return first;
        
        // Step 3: Search subsequent nodes
        Node<K,V> e;
        if ((e = first.next) != null) {
            // Step 3a: Red-Black Tree search O(log n)
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            
            // Step 3b: Linked list search O(k) where k = collisions
            do {
                if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

**Time complexity:**
- Best case: O(1) — hash maps directly to bucket
- Average case: O(1) — with good hash function and distribution
- Worst case (pre-Java 8): O(n) — all entries in one bucket
- Worst case (Java 8+): O(log n) — tree height in one bucket

---

#### **Resize (Rehashing) Operation**

```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    
    // Step 1: Calculate new capacity (always power of 2)
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;  // cannot grow further
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1;  // double threshold
    }
    else if (oldThr > 0)
        newCap = oldThr;
    else {
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (ft < MAXIMUM_CAPACITY) ? (int)ft : Integer.MAX_VALUE;
    }
    threshold = newThr;
    
    // Step 2: Create new table
    Node<K,V>[] newTab = new Node[newCap];
    table = newTab;
    
    // Step 3: Rehash all entries into new buckets
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                else {
                    // Rehash linked list
                    Node<K,V> loHead = null, loTail = null;  // hash & oldCap == 0
                    Node<K,V> hiHead = null, hiTail = null;  // hash & oldCap != 0
                    Node<K,V> next;
                    
                    // Smart rehashing: no need to recalculate hash
                    // Just check if (hash & oldCap) is 0 or 1
                    do {
                        next = e.next;
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null) loHead = e;
                            else loTail.next = e;
                            loTail = e;
                        } else {
                            if (hiTail == null) hiHead = e;
                            else hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    
                    // Place in new buckets
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

**Key insight on rehashing:**
```
When capacity doubles from 16 to 32:
  Old indices use:  hash & 15 (mask: 01111)
  New indices use:  hash & 31 (mask: 11111)

If bit 5 (oldCap) is 0: new index = old index
If bit 5 (oldCap) is 1: new index = old index + oldCap

This is why entries split into two chains:
  - Low group: stays at same index
  - High group: moves to index + oldCap

No need to recalculate hash!
```

---

#### **The Load Factor (0.75) Deep Dive**

```
load_factor = number_of_entries / capacity

DEFAULT_LOAD_FACTOR = 0.75f
```

**Why 0.75?**

1. **Mathematical Optimality:**
   ```
   With Poisson distribution of hash collisions,
   average cost per operation = 1 + load_factor / 2
   
   LF = 0.5:  avg cost = 1.25 (but 50% empty space wasted)
   LF = 0.75: avg cost = 1.375 (17% wasted space)
   LF = 0.9:  avg cost = 1.45 (10% wasted space)
   
   0.75 is the empirical sweet spot balancing:
   - Collision probability (grows with LF)
   - Memory efficiency (decreases with LF)
   - Rehashing frequency (increases with LF)
   ```

2. **Collision Statistics:**
   ```
   With 16 slots and load factor 0.75:
     16 × 0.75 = 12 entries
   
   Expected collisions with uniform distribution:
   Using birthday paradox: ~√n ≈ √16 ≈ 4 collisions expected
   
   Average chain length = (number of entries) / (number of slots)
                       = 12 / 16 = 0.75
   
   Cost of get() with 0.75 LF: O(1) on average (1.375 comparisons)
   ```

3. **Rehashing Overhead:**
   ```
   Resizing is O(n) — all entries rehashed
   At LF = 0.75, resize happens after 0.75×capacity insertions
   
   Amortized cost of put():
   - If resize happens every k insertions:
     Amortized cost = (k + n) / k  where n = resize cost
     With LF = 0.75: close to O(1) amortized
   
   Too low LF (0.25):  resize too often, waste memory
   Too high LF (0.9):  more collisions, longer chains
   ```

**Production implications:**
```java
// Pre-size HashMap when you know expected size
int expectedSize = 10_000;
Map<String, Data> cache = new HashMap<>(expectedSize * 4 / 3 + 1);
// Capacity = expectedSize / loadFactor = 10_000 / 0.75 = 13,334
// Round up to next power of 2: 16,384

// Avoids multiple resizes during initial loads
```

---

#### **What Happens During Resizing?**

Resizing is a critical operation that can cause:

1. **Performance Impact:**
   ```
   When size > threshold (capacity × load_factor):
     1. Create new array of 2× size
     2. Rehash ALL entries (O(n) operation)
     3. Update all bucket indices
   
   If you insert 1 million entries without pre-sizing:
   - n=16  → resize when size=12   (cost: 12)
   - n=32  → resize when size=24   (cost: 24)
   - n=64  → resize when size=48   (cost: 48)
   - ...
   - n=524288 → resize when size=393216  (cost: 393,216)
   
   Total rehashing cost = ~786K operations
   Could have avoided with: new HashMap<>(1_000_000 * 4/3)
   ```

2. **Thread-Safety During Resize (Why HashMap is NOT thread-safe):**
   ```java
   // Two threads resizing simultaneously
   Thread 1: Reading from oldTab[5] → Node A → Node B
   Thread 2: Resizing, moving Node A and B to newTab
   
   Thread 1: Tries to access Node A.next
   Thread 2: Deleted Node A.next, now newTab[j] = Node B
   
   Result: ConcurrentModificationException or infinite loop!
   
   In Java 7, hash collision during resize could create a cycle:
     A → B → A (infinite loop in get())
   ```

3. **Untreeifying on Resize:**
   ```java
   // If a Red-Black Tree has < 6 entries after resize,
   // convert back to linked list for memory efficiency
   
   static final int UNTREEIFY_THRESHOLD = 6;
   
   This saves memory for maps that transiently had many collisions
   but don't normally.
   ```

---

#### **Why Does Java 8 Use Red-Black Trees?**

**The Problem (Pre-Java 8):**
```
Hash collision attack vulnerability:

Attacker crafts keys that all hash to the same bucket:
  HashMap<String, Integer> map = new HashMap<>();
  
  // Keys specifically designed to collide
  String key1 = "Aa";   // hash = 2030
  String key2 = "BB";   // hash = 2030 (same!)
  
  map.put(key1, 1);
  map.put(key2, 2);
  map.put(key3, 3);
  // ...1000 keys all in bucket[0]
  
  // Now get() is O(n) instead of O(1)
  // Can slow down entire application
  // DoS attack vector!
```

**The Solution: Red-Black Trees (Java 8+)**

```java
// When any bucket exceeds 8 entries:
// 1. Convert to Red-Black Tree
// 2. O(log n) operations guaranteed, even with collisions

static class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent, left, right, prev;
    boolean red;  // Red-Black Tree property
    
    // Balanced tree ensures height ≤ 1.44 * log(n)
    // So even with 1000 collisions: get() is O(log 1000) ≈ 10 operations
    // Instead of O(1000) with linked list
}
```

**Red-Black Tree Properties:**
```
1. Every node is red or black
2. Root is black
3. All leaves (null) are black
4. If node is red, children must be black
5. All paths from node to leaves have same # of black nodes

These constraints guarantee:
  - Height ≤ 1.44 × log(n + 1)
  - Operations: O(log n) worst-case
  - Self-balancing: no manual rebalancing needed
```

**Performance Trade-off:**
```
Red-Black Tree vs Linked List:

Linked List:
  - get(): O(k) where k = collisions in bucket
  - put(): O(k)
  - Iterator: Simpler, faster for small k

Red-Black Tree:
  - get(): O(log k) — more predictable
  - put(): O(log k)
  - Iterator: More complex, overhead for small k

When k < 8: Use linked list (simpler)
When k >= 8: Use tree (protection against attack)
```

**Treeification Conditions (Both must be true):**
```java
if (binCount >= TREEIFY_THRESHOLD - 1)  // 8+ entries in bucket
    if (tab.length >= MIN_TREEIFY_CAPACITY)  // capacity >= 64
        treeifyBin(tab, hash);
```

Why `capacity >= 64`?
```
If capacity = 16 and you have 8 collisions in one bucket:
  Likely cause: capacity is too small, not DoS attack
  
Better to resize() instead of treeifying:
  Resizing distributes entries across more buckets
  More efficient than maintaining trees in small map
  
If capacity = 64 and you have 8 collisions:
  Likely a deliberate collision attack
  Use tree to protect against DoS
```

---

#### **Time Complexities**

| Operation | Best | Average | Worst (Java 7) | Worst (Java 8+) | Notes |
|---|---|---|---|---|---|
| `get(key)` | O(1) | O(1) | O(n) | O(log n) | Hash collision |
| `put(key, val)` | O(1) | O(1) | O(n) | O(log n) | Includes hashing |
| `remove(key)` | O(1) | O(1) | O(n) | O(log n) | Find + remove |
| `containsKey()` | O(1) | O(1) | O(n) | O(log n) | Uses get() |
| `keySet().iterate()` | O(n) | O(n) | O(n) | O(n) | Must visit all |
| `resize()` | O(n) | O(n) | O(n) | O(n) | Rehash all entries |

**Average case explanation:**
```
With a uniform hash function and load factor 0.75:
  Expected collisions per bucket = λ = load_factor ≈ 0.75
  Expected chain length = 1 + λ = 1.75
  
Expected comparisons = O(1 + λ) = O(1)

This is why HashMap operations are O(1) "on average"
The average is taken over all keys AND all hash values
```

---

#### **Why Isn't HashMap Thread-Safe?**

HashMap is **not thread-safe** due to:

1. **Race Condition in put():**
   ```java
   // Thread 1
   if ((tab[i] = newNode(hash, key, value, null)) == null)
       tab[i] = newNode(...);  // write to shared array
   
   // Thread 2 (simultaneously)
   if ((tab[i] = newNode(...)) == null)
       tab[i] = newNode(...);  // write to same array
   
   // What if both write at same time?
   // JMM doesn't guarantee visibility without synchronization
   ```

2. **Compound Operations Not Atomic:**
   ```java
   map.put("key", 1);
   map.put("key", 2);  // overwrites first put
   
   Two threads can get interleaved:
   T1: get(hash) → bucket 5 → null
   T2: resize() → rehash all entries
   T1: newNode() → tab[5] = Node  // might overwrite T2's data
   ```

3. **Resize Creating Infinite Loops (Java 7):**
   ```
   During resize, linked list order can reverse:
   Old bucket: A → B → C
   After resize, same bucket might become: C → B → A
   
   With two threads resizing simultaneously:
     T1: reading A → B
     T2: resized, A → C → B
     T1: now tries to read B.next = A
     
   Creates cycle: A → B → A (infinite loop on get())
   ```

4. **Visibility Issues:**
   ```
   HashMap uses:
   transient Node<K,V>[] table;  // NOT volatile
   transient int size;            // NOT volatile
   
   Without volatile:
   Thread 1: writes to table[5]
   Thread 2: may not see the write (cached in CPU register)
   
   Result: Lost updates, inconsistent state
   ```

---

#### **How ConcurrentHashMap Solves These Issues**

ConcurrentHashMap provides **thread-safe** operations with fine-grained locking:

**1. Segment-Based Locking (Java 7 and earlier):**
```java
// Java 7: 16 independent segments (default)
// Each segment is a mini-HashMap with its own lock
//
// put(key1) locks segment[0]
// put(key2) locks segment[1]
// → Different threads can write simultaneously
//
// Performance: up to 16 concurrent writers

final class Segment<K,V> extends ReentrantLock {
    volatile HashEntry<K,V>[] table;
    volatile int count;  // size of this segment
    int modCount;
    int threshold;
}
```

**2. Bucket-Level Locking (Java 8+):**
```java
// Java 8+: synchronized on individual bucket head node
// More fine-grained than segment locking
//
// Lock held only during critical section:
// synchronized(table[bucket]) { /* modify */ }
//
// Allows unlimited concurrent readers
// Writers lock only the bucket they're modifying

// Also uses CAS (Compare-And-Swap) for:
// - Initializing table
// - Finding first node in bucket
// - Atomic updates without locks
```

**3. Volatile Visibility:**
```java
// ConcurrentHashMap uses volatile for:
volatile Node<K,V>[] table;
volatile int size;
volatile int baseCount;  // LongAdder-based counter

// Ensures all writes are immediately visible to other threads
```

**4. Atomic Compound Operations:**
```java
// putIfAbsent — atomic, no race condition
V oldValue = map.putIfAbsent("key", "value");
// If key already exists, doesn't overwrite
// Guaranteed atomic even with multiple threads

// computeIfAbsent — load & compute atomically
map.computeIfAbsent("key", k -> expensiveComputation(k));

// merge — increment atomically
map.merge("counter", 1L, Long::sum);
```

**5. Safe Iteration (No ConcurrentModificationException):**
```java
// ConcurrentHashMap iteration is weakly consistent:
// It may or may not see insertions/deletions after iteration starts
// But will NOT throw ConcurrentModificationException

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
for (Map.Entry<String, Integer> e : map.entrySet()) {
    map.put("new_key", 99);  // safe — won't throw exception
}
// Iterator may or may not see the new entry, but no exception
```

**Comparison:**

| Feature | HashMap | ConcurrentHashMap |
|---|---|---|
| Thread-safe | ✗ | ✓ |
| Locking | None | Bucket-level (Java 8+) |
| Read performance | O(1)* | O(1)* (no lock) |
| Write performance | O(1)* | O(1)* (lock one bucket) |
| Iteration | Fail-fast | Weakly consistent |
| Size tracking | Accurate | Approximate |

*amortised with good hash function

**When to use which:**

```java
// Single-threaded code or external sync → HashMap
HashMap<String, Integer> map = new HashMap<>();
Collections.synchronizedMap(new HashMap<>());  // DON'T use

// Multi-threaded, concurrent reads/writes → ConcurrentHashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Multi-threaded, mostly reads → ReadWriteLock + HashMap
ReadWriteLock lock = new ReentrantReadWriteLock();
// or just use ConcurrentHashMap for simplicity
```

**Performance characteristics:**

```
Single writer, single reader:
  HashMap: ~1ns get, 1ns put
  ConcurrentHashMap: ~1.5ns get, 2ns put (lock overhead)

Single writer, 8 readers:
  HashMap: 5× slower (contention on synchronized)
  ConcurrentHashMap: ~1.5ns (no lock)

8 writers, 8 readers:
  HashMap: completely broken (not thread-safe)
  ConcurrentHashMap: scales linearly (each writer locks different bucket)
```

---

## Appendix: Quick Reference Tables

---

### Java Version Feature Timeline

| Version | LTS | Key Features |
|---|---|---|
| **Java 8** | Yes (EOL 2030) | Lambdas, Streams, Optional, default methods, java.time, CompletableFuture |
| **Java 9** | No | JPMS modules, List.of(), takeWhile/dropWhile, private interface methods |
| **Java 10** | No | `var` local type inference |
| **Java 11** | Yes (EOL 2026) | HttpClient, String::strip/isBlank/lines/repeat, Files::readString |
| **Java 14** | No | Records (preview), switch expressions (final) |
| **Java 15** | No | Text blocks (final), sealed classes (preview), ZGC production |
| **Java 16** | No | Records (final), pattern matching instanceof (final), Stream.toList() |
| **Java 17** | Yes (EOL 2029) | Sealed classes (final), enhanced pattern matching, JDK internals encapsulated |
| **Java 21** | Yes (EOL 2031) | Virtual threads, structured concurrency, record patterns, sequenced collections, pattern matching switch (final) |

---

### Collections Complexity Reference

| Collection | get(i) | add (end) | remove(i) | contains | Notes |
|---|---|---|---|---|---|
| `ArrayList` | O(1) | O(1)* | O(n) | O(n) | Best default list |
| `LinkedList` | O(n) | O(1) | O(1) | O(n) | Use as Deque only |
| `ArrayDeque` | O(1) | O(1)* | O(1) | O(n) | Best stack/queue |
| `HashMap` | — | O(1)* | O(1)* | O(1)* | Best default map |
| `LinkedHashMap` | — | O(1)* | O(1)* | O(1)* | Insertion/access order |
| `TreeMap` | — | O(log n) | O(log n) | O(log n) | Sorted, range queries |
| `HashSet` | — | O(1)* | O(1)* | O(1)* | Best default set |
| `TreeSet` | — | O(log n) | O(log n) | O(log n) | Sorted |
| `PriorityQueue` | O(1) peek | O(log n) | O(log n) | O(n) | Min-heap |

*amortised

---

### Thread Safety Reference

| Class | Thread Safe | Notes |
|---|---|---|
| `ArrayList`, `HashMap`, `HashSet` | No | Single-thread or external sync |
| `Vector`, `Hashtable` | Yes (coarse) | Legacy — avoid |
| `Collections.synchronizedList()` | Yes (coarse) | Compound ops need external sync |
| `CopyOnWriteArrayList` | Yes | Best for read-heavy, rarely-written lists |
| `ConcurrentHashMap` | Yes (fine-grained) | Production standard |
| `AtomicInteger`, `AtomicLong` | Yes (CAS) | Lock-free, low-overhead counter |
| `LongAdder` | Yes (striped CAS) | Better throughput than AtomicLong under contention |
| `BlockingQueue` implementations | Yes | Producer-consumer patterns |

---

### GC Algorithm Reference

| GC | JVM Flag | Pause | Heap | Best For |
|---|---|---|---|---|
| Serial | `-XX:+UseSerialGC` | High | Small | Embedded, single-core |
| Parallel | `-XX:+UseParallelGC` | Medium | Any | Throughput-focused batch |
| G1GC | `-XX:+UseG1GC` | Low (~200ms) | 4GB–32GB | General purpose (default Java 9+) |
| ZGC | `-XX:+UseZGC` | Ultra-low (<1ms) | 8MB–16TB | Low-latency services (Java 15+ prod) |
| Shenandoah | `-XX:+UseShenandoahGC` | Ultra-low (<1ms) | Any | Same as ZGC (Red Hat) |

---

*End of Document — Java Interview Questions: A 15-Year Veteran's Deep Dive (Java 8 through Java 21)*
