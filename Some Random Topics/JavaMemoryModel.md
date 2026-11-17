# Java Memory Management Model - Complete Guide

## Table of Contents
1. [Sample Java Program](#sample-java-program-with-memory-area-annotations)
2. [Memory Allocation Flow](#memory-allocation-flow)
3. [Detailed Memory Breakdown](#detailed-memory-breakdown)
4. [Step-by-Step Execution](#step-by-step-execution)
5. [Garbage Collection](#garbage-collection)
6. [Memory Regions Quick Reference](#memory-regions-quick-reference)
7. [Common Memory Issues](#common-memory-issues)
8. [Memory Configuration (JVM Flags)](#memory-configuration-jvm-flags)

---

## Sample Java Program with Memory Area Annotations

```java
// Class definition -> loaded into the METHOD AREA
public class MemoryDemo {

    // Static variable -> METHOD AREA, initialized at class load, lives for the JVM's lifetime
    static String staticVar = "I am static";

    // main() bytecode -> METHOD AREA. Calling main() pushes a new frame onto the STACK.
    public static void main(String[] args) {

        // new Person(...) -> allocates on the HEAP; person1 (the reference) lives on the STACK
        Person person1 = new Person("Alice", 25);

        // A second, independent object + reference pair
        Person person2 = new Person("Bob", 30);

        // No new object here - person3 copies person1's reference, so @0x1000 now has 2 refs
        Person person3 = person1;

        // Clears only the person1 reference. @0x1000 is still reachable via person3,
        // so it is NOT eligible for GC yet - see the reference diagram above.
        person1 = null;

        // Passes the person2 reference by value into a new STACK frame for printInfo()
        printInfo(person2);

        // main() returns -> its stack frame (and every local reference) is freed.
        // @0x1000, @0x2000, @0x3000, @0x4000 are now all unreachable -> eligible for GC.
    }

    // printInfo()'s bytecode -> METHOD AREA; parameter p -> STACK, Person object -> HEAP
    static void printInfo(Person p) {
        System.out.println("Name: " + p.name + ", Age: " + p.age);
        // Returning here frees printInfo()'s stack frame; p is removed, control returns to main()
    }
}

// Class definition -> METHOD AREA. The field *blueprint* is shared; each Person
// *instance*'s actual name/age values live on the HEAP, one copy per object.
class Person {
    String name;
    int age;

    // Constructor bytecode -> METHOD AREA. Running it: allocate on HEAP, bind 'this'
    // + parameters on the STACK, then copy the parameter values into the heap object's fields.
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

### Memory Location Summary Table

| Code Element | Stored In | When Allocated | When Freed | Thread-Safe |
|--------------|-----------|----------------|-----------|------------|
| `static String staticVar` | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `class Person` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `main()` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `printInfo()` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| Local variable `person1` | STACK[main] | main() call | main() return | Yes (per-thread) |
| Local variable `person2` | STACK[main] | main() call | main() return | Yes (per-thread) |
| Local variable `person3` | STACK[main] | main() call | main() return | Yes (per-thread) |
| `Person("Alice", 25)` object | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| `Person("Bob", 30)` object | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| String `"Alice"` | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| String `"Bob"` | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| Parameter `p` in printInfo | STACK[printInfo] | printInfo() call | printInfo() return | Yes (per-thread) |

---

## Memory Allocation Flow

### Overall JVM Memory Structure

![JVM memory structure overview](jmm-memory-structure.svg)

- **Method area** (shared across all threads): class structures for `Person` and `MemoryDemo`, method bytecode, the constant pool, and `staticVar = "I am static"`.
- **Heap** (shared across all threads, GC-managed): `Person` objects at `@0x1000`/`@0x2000` and the `String` objects they reference at `@0x3000`/`@0x4000`.
- **Stack (main)** (per thread): `person1`, `person2`, `person3` references, each pointing into the heap (or `null`).
- **Stack (printInfo)** (per thread, created only while that method is executing): parameter `p`, pointing at the same heap object as `person2`.

---

## Detailed Memory Breakdown

### 1. METHOD AREA (Class Data)
**Location**: Shared across all threads  
**When Allocated**: At class loading time  
**Freed**: When JVM shuts down or class is unloaded

| Region | Contents |
|---|---|
| Class `Person` | Fields `String name`, `int age`; constructor `Person(String, int)`; bytecode |
| Class `MemoryDemo` | Static var `staticVar`; methods `main()`, `printInfo()` |
| Static variables | `staticVar → "I am static"` (the reference lives here, the `String` object itself is on the heap) |
| String literal pool | `"Alice"`, `"Bob"`, `"I am static"` |

### 2. HEAP (Objects)
**Location**: Shared across all threads  
**When Allocated**: When `new` keyword is used  
**Freed**: By Garbage Collector when no references exist

| Address | Object | Fields |
|---|---|---|
| `@0x1000` | `Person` | `name → @0x3000` (ref), `age = 25` (primitive) |
| `@0x2000` | `Person` | `name → @0x4000` (ref), `age = 30` (primitive) |
| `@0x3000` | `String` | `value = "Alice"`, `length = 5` |
| `@0x4000` | `String` | `value = "Bob"`, `length = 3` |

See §"Garbage Collection" below for how and when these get reclaimed.

### 3. STACK (Method Execution)
**Location**: Per thread (separate for each thread)  
**When Allocated**: When method is called  
**Freed**: When method returns

Each method call pushes a new **stack frame** holding its local variables and a return address; the frame is popped the instant the method returns.

| Frame | Local variables | Return address |
|---|---|---|
| `main()` | `args → @0x5000`, `person1 → @0x1000`, `person2 → @0x2000`, `person3 → @0x1000` | — (JVM entry point) |
| `printInfo()` (pushed on top while `main()` is calling it) | `p → @0x2000` | line 5 of `main()` |

---

## Step-by-Step Execution

### Line-by-Line Memory Changes

#### **Line 1: `Person person1 = new Person("Alice", 25);`**

1. Class loader loads `Person.class` (if not already loaded) → method area now has the `Person` class structure.
2. Create `String "Alice"` → heap `@0x3000`.
3. Create `Person` object → heap `@0x1000 → Person {name: @0x3000, age: 25}`.
4. Store the reference on the stack → `STACK[main]: person1 = @0x1000`.

#### **Line 2: `Person person2 = new Person("Bob", 30);`**

1. Create `String "Bob"` → heap `@0x4000`.
2. Create `Person` object → heap `@0x2000 → Person {name: @0x4000, age: 30}`.
3. Store the reference on the stack → `STACK[main]: person2 = @0x2000`.

Heap now holds two independent `Person` objects; the stack holds two independent references.

#### **Line 3: `Person person3 = person1;`**

1. Read the value of the `person1` reference (`@0x1000`) — not the object itself.
2. Assign that same reference to `person3` → `STACK[main]: person3 = @0x1000`.

No new object is created. `@0x1000` now has **2 references** (`person1`, `person3`); `@0x2000` still has 1 (`person2`).

#### **Line 4: `person1 = null;`**

1. Clear the `person1` reference → `STACK[main]: person1 = null`.

`@0x1000` drops to **1 reference** (`person3`) — still alive and **not** eligible for GC. `@0x2000` is unaffected (still 1 reference, `person2`). Setting a variable to `null` only clears *that* reference; the object survives as long as any other reference remains, as shown below:

![Stack references into the heap after person1 is nulled](jmm-references.svg)

#### **Line 5: `printInfo(person2);`**

1. Call `printInfo(person2)` → a new stack frame is pushed for `printInfo()`.
2. The reference is passed by value → `STACK[printInfo]: p = @0x2000` (same address as `person2`, a second reference to the *same* object).
3. Inside `printInfo`, `p.name` / `p.age` follow the reference from stack → heap → object fields.
4. Output: `Name: Bob, Age: 30`.

The heap is unchanged by this call — `printInfo` only reads through an additional reference to the existing `@0x2000` object.

#### **After main() Returns**

1. `printInfo()` returns → its stack frame (`p`) is freed.
2. `main()` returns → its stack frame (`person1`, `person2`, `person3`) is freed — every reference that ever pointed into the heap is now gone.
3. With no GC roots pointing at them, `@0x1000`, `@0x2000`, `@0x3000`, and `@0x4000` are all unreachable.
4. The next garbage collection cycle reclaims all four — see the **Garbage Collection** section below for exactly how (mark/sweep/compact, and which generation each object would actually be sitting in by this point).

---

## Garbage Collection

Garbage collection (GC) is the JVM automatically reclaiming heap memory occupied by objects that the running program can no longer reach. There's no manual `free()` — the tradeoff is occasional, unpredictable pauses while the collector does its work.

### Reachability and GC roots

An object is **garbage** the moment nothing reachable from a **GC root** points to it anymore — not when a variable is set to `null`, but when *every* path to it is gone (as seen in Line 4/5 above, where `@0x1000` survived `person1 = null` because `person3` still reached it).

GC roots are the fixed starting points the collector traces from: local variables and parameters on every thread's stack, static variables in the method area, active JNI (native) references, and objects currently locked for synchronization. Anything not reachable by following references from a root, transitively, is collectible.

### The core algorithm: Mark, Sweep, Compact

![Mark, sweep and compact phases](jmm-gc-mark-sweep-compact.svg)

1. **Mark** — starting from the GC roots, traverse every reference and mark each object reached as live. Anything left unmarked afterward is garbage.
2. **Sweep** — walk the heap and reclaim the memory of every unmarked object, making it available for new allocations.
3. **Compact** — slide the surviving (marked) objects together to eliminate the gaps sweep left behind, so free memory is one contiguous block instead of scattered fragments. This step is optional per-collector (some collectors compact, some don't, some only compact occasionally) because it costs extra CPU time to relocate objects and fix up every reference that pointed at them.

This is the algorithm underneath every HotSpot collector discussed below — they differ mainly in *where* they run it (which generation), *how many threads* run it concurrently, and *whether the application has to pause* while it happens.

### The generational hypothesis

Nearly every real JVM collector is built on one empirical observation: **most objects die young.** Short-lived temporaries (loop variables, intermediate strings, request-scoped objects) vastly outnumber long-lived ones (caches, connection pools, singletons). So instead of scanning the *entire* heap on every collection, the heap is split into generations, and the young, short-lived one is collected far more often and far more cheaply than the old one.

![Generational heap layout: young generation, old generation, metaspace](jmm-gc-heap-generations.svg)

#### Young generation
- **Eden**: where (almost) every new object is allocated. Allocation here is a simple pointer bump — very fast.
- **Survivor spaces (S0, S1)**: two equally-sized regions; at any moment one is active and one is empty. Objects that survive a collection are copied into the empty one; the two swap roles every cycle, so one is always "to-space."
- Sized relatively small on purpose — a small region fills up (and triggers a collection) quickly, which is fine because most of what's in it is already dead.

#### Old generation (tenured)
- Holds objects that have survived enough young-generation collections to be considered long-lived (see tenuring below), plus objects large enough to be allocated directly into the old generation.
- Collected far less often, but a collection here is far more expensive — it's a much bigger region to mark/sweep/compact.

#### Metaspace (not part of the heap)
- Class metadata — the method area's modern implementation (replacing the old fixed-size "PermGen" since Java 8). It lives in **native memory**, not the heap, and grows dynamically (bounded by `-XX:MaxMetaspaceSize`), which is why class-loading-heavy apps (lots of dynamic proxies, app servers redeploying) used to hit `OutOfMemoryError: PermGen space` and now instead exhaust native memory if classes leak.

### Minor GC and promotion (tenuring)

![Minor GC and promotion flow](jmm-gc-minor-gc-promotion.svg)

1. New objects are allocated in **Eden**.
2. When Eden fills up, a **Minor GC** runs: a **stop-the-world** pause (all application threads freeze) during which every *live* object in Eden and the active survivor space is copied into the *other*, currently-empty survivor space.
3. Each object that survives gets its **age** incremented by 1 (tracked in its object header).
4. Once an object's age reaches the **tenuring threshold** (`-XX:MaxTenuringThreshold`, default 15, dynamically adjusted by most collectors), it is **promoted** straight into the old generation instead of being copied again.
5. Dead objects are never copied at all — this is why minor GC is cheap: its cost is proportional to the (usually small) number of *survivors*, not to the size of Eden.

### Major / Full GC

A **major GC** collects the old generation (and usually implies the young generation is collected too); a **full GC** collects the *entire* heap plus, often, metaspace. Both are dramatically more expensive than a minor GC because the old generation is large and mostly made of long-lived objects, so the mark phase has far more live data to traverse. Full GCs are the main source of the "stop-the-world pause" complaints that show up in production incident reports — most GC tuning work is really about *avoiding* full GCs, not making them faster.

### GC algorithms (the ones to know by name)

| Collector | Approach | Pause behavior | When it's used |
|---|---|---|---|
| **Serial GC** (`-XX:+UseSerialGC`) | Single-threaded mark-sweep-compact for both generations | Stop-the-world, can be long | Small heaps, single-core machines, simple CLI tools |
| **Parallel GC** (`-XX:+UseParallelGC`) | Same algorithm as Serial, but multiple threads do the marking/copying in parallel | Stop-the-world, but shorter wall-clock than Serial | Default on older JDKs; throughput-oriented batch jobs that can tolerate pauses |
| **CMS** (`-XX:+UseConcurrentMarkSweepGC`, deprecated since Java 9, removed in Java 14) | Marks and sweeps the old generation *concurrently* with the application, pausing only briefly | Short pauses, but doesn't compact (fragmentation risk) and competes with the app for CPU | Was the standard low-pause choice before G1 matured |
| **G1 (Garbage First)** (`-XX:+UseG1GC`, **default since Java 9**) | Splits the heap into many fixed-size regions (not one contiguous young/old split) and always collects the regions with the most garbage first | Stop-the-world but tightly bounded — you set a target with `-XX:MaxGCPauseMillis` and G1 picks how many regions to collect to hit it | The default general-purpose choice for most services today |
| **ZGC** / **Shenandoah** (`-XX:+UseZGC`, `-XX:+UseShenandoahGC`) | Fully concurrent mark *and* compact, using colored pointers / load barriers so the app keeps running during almost all phases | Sub-millisecond pauses, largely independent of heap size | Very large heaps (10s–100s of GB) or strict latency SLAs |

### G1's region-based heap

![G1 region-based heap](jmm-gc-g1-regions.svg)

G1's key difference from Serial/Parallel/CMS: instead of one contiguous Eden, one contiguous Survivor area, and one contiguous Old generation, the heap is divided into many equal-sized regions (typically 1–32 MB each), and each region is independently labeled Eden, Survivor, Old, or **Humongous** (for objects larger than ~50% of a region size, which skip the young generation and go straight to dedicated regions). G1 tracks how much garbage is in each region and always collects the regions with the most reclaimable garbage first — hence "Garbage First" — which is what lets it hit a target pause time instead of pausing for as long as a full generation happens to need.

### Choosing / tuning a collector (quick reference)

```bash
-XX:+UseG1GC              # G1 — good general-purpose default
-XX:+UseSerialGC          # Serial — tiny heaps / single core
-XX:+UseParallelGC        # Parallel — throughput over latency
-XX:+UseZGC               # ZGC — very large heap, low-latency requirement

-XX:MaxGCPauseMillis=200  # G1: target pause time (a goal, not a hard guarantee)
-XX:NewRatio=2            # Old:Young size ratio (2 → old gen is 2x young gen)
-XX:SurvivorRatio=8       # Eden:Survivor size ratio within young gen
-XX:MaxTenuringThreshold=15  # Max age before forced promotion to old gen
```

---

## Memory Regions Quick Reference

| Region | Stores | Thread-Safe | Size | Life Cycle |
|--------|--------|-------------|------|-----------|
| **Method Area** | Classes, methods, static vars | Yes (shared) | Fixed | Class load → JVM exit |
| **Heap** | Objects, arrays | No (needs sync) | Dynamic | Object creation → GC |
| **Stack** | Primitives, references | Yes (per thread) | Limited | Method call → return |
| **PC Register** | Bytecode address | Yes (per thread) | Small | Method call → return |
| **Native Stack** | Native method calls | Per thread | System | Call → return |

---

## Key Concepts Illustrated

### 1. Reference vs Object
```
Stack contains REFERENCE (pointer/address):
    person1 → @0x1000

Heap contains ACTUAL OBJECT:
    @0x1000 → Person {name="Alice", age=25}

Multiple references can point to same object:
    person1 → @0x1000
    person3 → @0x1000 (same object!)
```

### 2. Primitives vs Objects
```
Primitives (stored on STACK):
    int age = 25;           // age is on stack
    int[] arr = new int[3]; // arr ref on stack, array on heap

Objects (reference on STACK, object on HEAP):
    Person p = new Person();  // p ref on stack
                              // Person obj on heap
```

### 3. String Literal Pool
```
String s1 = "Hello";  // Created in pool, @0x5000
String s2 = "Hello";  // Reuses same object, @0x5000
String s3 = new String("Hello");  // New object, @0x6000

s1 == s2  // true (same reference)
s1.equals(s3)  // true (same content)
s1 == s3  // false (different objects)
```

---

## Common Memory Issues

### 1. Stack Overflow
```
❌ Infinite Recursion:
void recursiveMethod() {
    recursiveMethod();  // Stack grows infinitely!
}

RESULT: StackOverflowError
    Every method call adds frame to stack
    Stack has limited size
    Eventually exceeds capacity
```

### 2. Memory Leak
```
❌ Static Collection that grows unbounded:
static List<String> list = new ArrayList<>();

public void addToList(String item) {
    list.add(item);  // Keeps growing
    // Items never removed - never garbage collected!
}

RESULT: OutOfMemoryError (Heap exhausted)
```

### 3. Heap Exhaustion
```
❌ Creating too many objects:
List<byte[]> list = new ArrayList<>();
while(true) {
    list.add(new byte[1024*1024]);  // 1MB objects
}

RESULT: OutOfMemoryError: Java heap space
    Heap fills up faster than GC can free
    Or objects have references preventing GC
```

---

## Memory Configuration (JVM Flags)

```bash
# Set heap size
-Xms512m          # Initial heap size: 512MB
-Xmx1024m         # Maximum heap size: 1GB

# Example:
java -Xms512m -Xmx1024m -cp . MemoryDemo

# Stack size
-Xss1m            # Thread stack size: 1MB

# Metaspace (Method Area)
-XX:MetaspaceSize=64m      # Initial metaspace
-XX:MaxMetaspaceSize=256m   # Maximum metaspace

# Garbage Collector selection
-XX:+UseG1GC              # G1 Garbage Collector
-XX:+UseSerialGC          # Serial GC
-XX:+UseParallelGC        # Parallel GC
```

---

## Best Practices

### ✅ DO:
1. **Nullify references** when object is no longer needed
   ```java
   Person p = new Person();
   // ... use p ...
   p = null;  // Help GC identify as eligible
   ```

2. **Use try-with-resources** for automatic cleanup
   ```java
   try (FileReader fr = new FileReader("file.txt")) {
       // Use fr
   }  // Automatically closed
   ```

3. **Keep scope tight** for automatic stack cleanup
   ```java
   if (condition) {
       int temp = 100;  // Freed when block ends
   }
   ```

4. **Avoid static collections** without bounds
   ```java
   static List<String> list = new ArrayList<>();
   // Add logic to remove old items!
   ```

### ❌ DON'T:
1. Don't rely on `System.gc()` - it's only a hint
2. Don't create objects in tight loops unnecessarily
3. Don't hold references to large objects longer than needed
4. Don't ignore memory warning signs
5. Don't assume `new` keyword is free

---

## Monitoring & Tools

### View Memory Usage in Code:
```java
Runtime runtime = Runtime.getRuntime();

long totalMemory = runtime.totalMemory();      // Total allocated
long freeMemory = runtime.freeMemory();        // Currently free
long maxMemory = runtime.maxMemory();          // Maximum available
long usedMemory = totalMemory - freeMemory;   // Currently used

System.out.println("Used: " + usedMemory / 1024 / 1024 + " MB");
System.out.println("Free: " + freeMemory / 1024 / 1024 + " MB");
System.out.println("Max: " + maxMemory / 1024 / 1024 + " MB");
```

### External Tools:
- **JProfiler**: Visual memory profiling
- **YourKit**: Real-time monitoring
- **Eclipse MAT**: Memory leak detection
- **JConsole**: Built-in JVM monitoring
- **VisualVM**: Visual tool for JVM

---

## Summary Diagram

![Complete Java memory model architecture](jmm-architecture-summary.svg)

Source files are loaded by the class loader into the **method area**; the **execution engine** (interpreter + JIT) runs the bytecode, reading/writing thread-local **stack** frames and shared **heap** objects; the **garbage collector** reclaims heap objects no root can reach anymore, using the generational mark/sweep/compact machinery described above.

---

## Conclusion

Java's memory management consists of:

1. **METHOD AREA**: Static, shared, class-level data
2. **HEAP**: Dynamic, shared, garbage-collected objects
3. **STACK**: Fast, thread-local, method variables
4. **EXECUTION ENGINE**: JVM that runs your code
5. **GARBAGE COLLECTOR**: Automatic cleanup of unused objects

Understanding this model helps you write efficient Java programs and debug memory issues effectively.

---

**Document Version**: 1.0  
**Last Updated**: 2026-07-30  
**References**: JVM Specification, Java Memory Model Documentation
