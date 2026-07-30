# Hitachi Interview Prep — JVM Internals, GC, HashMap, Design Patterns

> Senior Engineer (16+ years) level — expect deep follow-ups on every answer.

---

## TABLE OF CONTENTS

1. [JVM Memory Architecture](#1-jvm-memory-architecture)
2. [Class Loading Subsystem](#2-class-loading-subsystem)
3. [JIT Compilation](#3-jit-compilation)
4. [Garbage Collection — Core Concepts](#4-garbage-collection--core-concepts)
5. [GC Algorithms in Detail](#5-gc-algorithms-in-detail)
6. [GC Tuning & Diagnostics](#6-gc-tuning--diagnostics)
7. [HashMap Internals](#7-hashmap-internals)
8. [ConcurrentHashMap Internals](#8-concurrenthashmap-internals)
9. [Design Patterns](#9-design-patterns)
10. [High-Level Design Questions](#10-high-level-design-questions)

---

## 1. JVM Memory Architecture

### Q1. Describe the JVM memory model and all its regions.

**Answer:**

The JVM memory is divided into two broad buckets: **per-thread** areas and **shared** areas.

```
┌────────────────────────────────────────────────────────┐
│                    JVM Process Memory                  │
│                                                        │
│  ┌─────────────────────────────────────────────────┐  │
│  │                  HEAP (shared)                  │  │
│  │  ┌──────────────────┐  ┌─────────────────────┐  │  │
│  │  │   Young Gen      │  │    Old Gen (Tenured) │  │  │
│  │  │  ┌────┐ ┌──┐ ┌──┐│  │                     │  │  │
│  │  │  │Eden│ │S0│ │S1││  │                     │  │  │
│  │  │  └────┘ └──┘ └──┘│  │                     │  │  │
│  │  └──────────────────┘  └─────────────────────┘  │  │
│  └─────────────────────────────────────────────────┘  │
│                                                        │
│  ┌─────────────────────────────────────────────────┐  │
│  │         Metaspace (off-heap, since Java 8)      │  │
│  │  Class metadata, method bytecode, static vars   │  │
│  └─────────────────────────────────────────────────┘  │
│                                                        │
│  Per-Thread Areas (one per Java thread):               │
│  ┌────────────┐  ┌────────────┐  ┌──────────────────┐ │
│  │   Stack    │  │   PC Reg   │  │  Native Method   │ │
│  │ (frames)   │  │            │  │  Stack           │ │
│  └────────────┘  └────────────┘  └──────────────────┘ │
│                                                        │
│  ┌─────────────────────────────────────────────────┐  │
│  │     Code Cache (JIT compiled native code)       │  │
│  └─────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

**Heap — Young Generation:**
- **Eden space**: All new objects are allocated here (TLAB — Thread-Local Allocation Buffer for fast allocation).
- **Survivor 0 & S1**: Objects that survive a Minor GC are copied between S0 and S1 each cycle. After `MaxTenuringThreshold` (default 15) cycles, they promote to Old Gen.

**Heap — Old Generation (Tenured):**
- Long-lived objects. Collected by Major/Full GC. Much larger than Young Gen.

**Metaspace (Java 8+, replaced PermGen):**
- Class metadata, bytecode, runtime constant pool, static variables (reference in heap, metadata in Metaspace).
- Backed by native memory — grows dynamically unless `-XX:MaxMetaspaceSize` is set.
- PermGen had a fixed size (`-XX:MaxPermSize`) and caused `OutOfMemoryError: PermGen space`. Metaspace fixes this.

**JVM Stack (per-thread):**
- Each method call creates a **stack frame** containing: local variable array, operand stack, frame data (constant pool reference, return address).
- `StackOverflowError` when limit exceeded.

**PC Register:** Holds address of current bytecode instruction for the thread.

**Code Cache:** Stores JIT-compiled native machine code. Controlled by `-XX:ReservedCodeCacheSize`.

---

### Q2. What is TLAB and why is it important?

**Answer:**

**Thread-Local Allocation Buffer (TLAB)** is a per-thread chunk of Eden space. Instead of synchronizing all threads on a single bump pointer into Eden, each thread gets its own private TLAB.

- Object allocation = just a pointer bump inside TLAB (essentially free, ~ns).
- When TLAB is exhausted, thread requests a new one from Eden (with a lock, but rare).
- No false sharing, no lock contention during normal allocation.
- Controlled by `-XX:+UseTLAB` (on by default), size via `-XX:TLABSize`.

---

### Q3. What happened to PermGen in Java 8? What are the implications?

**Answer:**

PermGen was removed and replaced with **Metaspace**:

| | PermGen | Metaspace |
|---|---|---|
| Location | JVM Heap | Native (OS) memory |
| Size | Fixed (`-XX:MaxPermSize`) | Grows dynamically |
| OOM cause | Class metadata overflow | Native OOM (rare) |
| GC trigger | Full GC | Full GC can trigger cleanup |

**Implications:**
- Class metadata is now limited only by native memory — less OOM for dynamic class generation (reflection, proxies, OSGi).
- Set `-XX:MaxMetaspaceSize` in production to cap it and get early OOM rather than starving the OS.
- Memory leak due to classloader leaks now shows up as native memory growth, harder to spot without tools like NMT (`-XX:NativeMemoryTracking=detail`).

---

## 2. Class Loading Subsystem

### Q4. Explain the class loading process and the delegation model.

**Answer:**

Class loading has three phases: **Loading → Linking → Initialization**

**Loading:** The ClassLoader reads the `.class` file and creates a `Class` object.

**Linking:**
1. *Verification* — bytecode is verified for correctness (no stack underflow, type safety).
2. *Preparation* — static variables are allocated and set to default values (0, null, false).
3. *Resolution* — symbolic references (class/method/field names in bytecode) are resolved to direct references.

**Initialization:** Static initializer blocks and `static` field assignments run.

**Parent Delegation Model:**
```
Bootstrap ClassLoader  (loads rt.jar / java.* — written in C++)
        ↑
Extension ClassLoader  (loads jre/lib/ext — Java)
        ↑
Application ClassLoader (loads classpath — Java)
        ↑
Custom ClassLoaders
```

When a class is requested:
1. Check if already loaded (cache lookup).
2. Delegate to parent first.
3. Only load yourself if parent cannot find it.

**Why:** Prevents user code from replacing `java.lang.String` etc.

**Breaking the delegation model:** Override `loadClass()` instead of `findClass()` (frameworks like OSGi, app servers do this for isolation).

---

### Q5. What causes `ClassCastException` even with the same class name?

**Answer:**

When the same class is loaded by **two different ClassLoader instances**, JVM treats them as distinct types. Casting between them throws `ClassCastException` even though the class name and bytecode are identical.

Common in: OSGi, application servers (multiple WAR deployments), plugin systems.

**Fix:** Ensure shared classes are loaded by a common parent classloader.

---

## 3. JIT Compilation

### Q6. How does JIT work? What are C1 and C2 compilers?

**Answer:**

---

#### The Problem JIT Solves

Java bytecode (`.class` files) is platform-neutral — it cannot run directly on CPU. The JVM has two ways to execute it:

1. **Interpretation:** Execute one bytecode instruction at a time. Zero startup cost but slow — every instruction has interpreter overhead.
2. **Ahead-of-Time (AOT) compilation:** Compile everything before running (GraalVM native-image). Fast runtime but long build time, no runtime profile information.

**JIT (Just-In-Time) compilation** is the middle ground: start interpreting, profile which code is actually hot, then compile only those hot methods to native machine code at runtime. This gives you:
- Fast startup (no compilation upfront).
- Near-native peak throughput (compiled code runs without interpreter overhead).
- Profile-guided optimization (compiler uses real runtime data — impossible with AOT).

---

#### The JIT Pipeline: From Bytecode to Native Code

```
.java → javac → .class (bytecode)
                    ↓
              JVM Interpreter  ← cold path (Tier 0)
                    ↓ (method becomes "hot")
              C1 Compiler      ← warm path (Tiers 1–3)
                    ↓ (method stays hot + profile data collected)
              C2 Compiler      ← fully optimized native code (Tier 4)
                    ↓
              Code Cache       ← native code stored here
```

---

#### What Makes a Method "Hot"?

The JVM counts **invocation events** for each method using internal counters:
- **Method invocation counter** — incremented each time the method is called.
- **Back-edge counter** — incremented each time a loop iterates (back-edge = jump back to loop start).

When either counter exceeds a threshold (default: **10,000 for C1**, **~10,000–15,000 for C2** depending on tier), the method is queued for compilation.

You can inspect this with:
```bash
-XX:+PrintCompilation
# Output format:
# timestamp  compile-id  flags  method-name  size  deopt-info
   1234       47          %      com.example.Foo::processLoop @ 5  (142 bytes)
# % means OSR compilation (see below)
```

---

#### Tiered Compilation (Java 8+ default: `-XX:+TieredCompilation`)

Tiered compilation was the key innovation in Java 7/8 — it uses both C1 and C2 together, instead of choosing one.

| Tier | Executor | Profiling | Purpose |
|------|----------|-----------|---------|
| **0** | Interpreter | None | Cold start — zero cost |
| **1** | C1 | None | Simple methods; compile fast, run faster |
| **2** | C1 | Limited (call/backedge counts) | Moderate methods |
| **3** | C1 | Full (branch, type profiles) | Hot methods — build rich profile for C2 |
| **4** | C2 | Consumes C1 profile | Fully optimized native — peak performance |

A typical lifecycle of a hot method:
```
Interpreter (Tier 0)
  → C1 no-profile (Tier 1)  [fast, rough optimization]
  → C1 full-profile (Tier 3) [profiling: recording branch outcomes, call targets]
  → C2 (Tier 4)             [uses that profile for aggressive optimization]
```

Some simple/trivial methods stay at Tier 1 or 2 permanently (no need for C2).

---

#### C1 — Client Compiler (Fast Compiler)

**Goal:** Compile quickly. Spend minimal time in compiler so the app warms up fast.

**How it works:**
- Converts bytecode to a platform-independent **High-level IR (HIR)** — basically a Control Flow Graph.
- Does basic optimizations: constant propagation, null-check elimination, simple inlining.
- Emits native code quickly — compilation latency is in the **milliseconds**.

**Key characteristic:** C1 does NOT do heavy global optimizations. It gets the code off the interpreter quickly. The native code it produces is maybe 2–5x faster than interpreter, not 10–50x like C2.

**When to use C1 alone:** `-client` flag (deprecated in Java 11) or short-lived CLI apps where startup matters more than peak throughput.

---

#### C2 — Server Compiler (Optimizing Compiler)

**Goal:** Produce the fastest possible native code using every optimization trick available. Compilation time is secondary.

**How it works:**
- Converts C1's profiling data + bytecode into an **ideal graph** (Sea of Nodes IR).
- Applies deep global optimizations.
- Emits highly optimized native code — compilation latency is in the **tens to hundreds of milliseconds**.

**Key C2 Optimizations in depth:**

**1. Method Inlining (most important optimization)**
```java
// Source code
int result = add(a, b);

// After inlining, the call disappears entirely:
int result = a + b;  // no call overhead, enables further optimization
```
- Without inlining, every method call has: push args to stack, create stack frame, jump to method, return, pop frame.
- With inlining, the callee's code is inserted at the call site. This is the **root that enables almost all other optimizations** — you can't optimize across call boundaries without it.
- C2 inlines aggressively up to a method size limit (`-XX:MaxInlineSize=35` bytecodes by default; `-XX:FreqInlineSize=325` for frequently called methods).
- **Polymorphic inlining:** C1 profiles which concrete type is passed (e.g., 95% `ArrayList`, 5% `LinkedList`). C2 can inline the common case with a type guard:
  ```
  if (list.class == ArrayList) { <inlined ArrayList.get> }
  else { virtual dispatch fallback }
  ```

**2. Escape Analysis**

C2 analyzes whether an object allocated inside a method is accessible outside it (i.e., "escapes").

**Three escape states:**
- **No escape:** Object is only used inside this method (and inlined callees). C2 can:
  - **Stack allocate** the object (no heap allocation, no GC pressure).
  - **Scalar replace** the object — decompose its fields into individual local variables (no object at all).
- **Argument escape:** Object passed to another method but doesn't escape that method chain. C2 may still optimize.
- **Global escape:** Object stored in heap, static field, or returned — C2 cannot eliminate it.

```java
// Example: no-escape object
void processPoint() {
    Point p = new Point(3, 4);  // p never leaves this method
    double dist = Math.sqrt(p.x * p.x + p.y * p.y);
    // After escape analysis + scalar replacement:
    // Point p is ELIMINATED. x=3, y=4 become local int vars.
    // No heap allocation. No GC. Zero object overhead.
}
```

**Lock elision:** If an object doesn't escape, its monitor (synchronized) is also local to the thread — the lock can never be contested. C2 eliminates it entirely:
```java
synchronized (new Object()) {  // lock on non-escaping object
    // ... all synchronization removed by C2
}
```

**3. Loop Optimizations**

- **Loop unrolling:** Repeat loop body multiple times per iteration, reducing branch/counter overhead.
  ```java
  // Original:         Unrolled 4x:
  for (int i=0;        for (int i=0;
       i<100; i++)          i<100; i+=4) {
      sum += a[i];         sum += a[i];
                           sum += a[i+1];
                           sum += a[i+2];
                           sum += a[i+3];
                       }
  ```
- **Loop vectorization (SIMD):** C2 can convert scalar loops into CPU vector instructions (SSE, AVX) that process 4–8 values per instruction. This is why `Arrays.fill()` is dramatically faster than a hand-written loop in some JVMs.
- **Loop invariant code motion:** Move computations that don't change per iteration outside the loop.
- **Range check elimination:** In a loop over an array, C2 proves the index is always in bounds and eliminates per-element `ArrayIndexOutOfBoundsException` checks.

**4. Dead Code Elimination**

```java
boolean DEBUG = false;
if (DEBUG) {
    expensiveLogging();  // C2 proves this is never reached; removes it entirely
}
```

**5. Constant Folding & Propagation**

```java
int x = 3 * 4;     // → x = 12 at compile time, no multiply at runtime
int y = x + 1;     // → y = 13
```

**6. Devirtualization**

Virtual method calls (interface/abstract) require a vtable lookup at runtime. If C1's profiling shows that 100% of calls go to `ArrayList.get()`, C2 emits a direct call (or inlines it) with a type check guard — no vtable overhead.

**7. Intrinsics**

Certain standard library methods are replaced by hand-written, CPU-specific machine code sequences called **intrinsics**. The JIT recognizes these methods by name and swaps in the intrinsic automatically.

Examples:
- `Math.sqrt()` → CPU's `FSQRT` or `SQRTSD` instruction (one cycle).
- `String.equals()` → SSE/AVX vector byte comparison.
- `System.arraycopy()` → `REPNZ MOVSD` or `memcpy`-equivalent.
- `Arrays.fill()` → SIMD vector fill.
- `Integer.bitCount()` → CPU's `POPCNT` instruction.
- `CRC32`, `SHA` → CPU hardware accelerated instructions.

Without intrinsics, these would be plain Java loops, far slower.

---

#### OSR — On-Stack Replacement

Normal JIT: wait until the current call to a method finishes, then next call uses compiled code.

**Problem:** A method with a huge loop runs for minutes entirely in the interpreter.

**OSR solution:** JVM detects the back-edge counter exceeds the threshold *while the loop is running*. It compiles the loop mid-execution and **replaces the interpreter frame with a compiled frame on the stack** — the loop continues in native code from the exact same iteration.

```bash
# OSR compilations are shown with '%' in PrintCompilation:
1234  47 %  com.example.DataProcessor::crunchNumbers @ 42 (385 bytes)
#                                                     ^^^
#                              OSR entry point is bytecode offset 42 (the loop back-edge)
```

---

#### Deoptimization — When JIT Assumptions Break

C2 makes **speculative optimizations** based on profiling. For example:
- "I've only ever seen `ArrayList` here, so I'll inline `ArrayList.get()` with a type check."
- "This field has never been null, so I'll remove the null check."

If those assumptions later turn out to be wrong (e.g., a `LinkedList` is now passed), the compiled code must be **deoptimized**:

1. JVM detects the violated assumption (via an uncommon trap).
2. The compiled frame is converted back to an interpreter frame (**deopt**).
3. Execution continues in the interpreter.
4. The method may be re-profiled and re-compiled with a more conservative version.

```bash
# You can see deoptimizations with:
-XX:+PrintDeoptimization
# Or via jstat or JFR (Java Flight Recorder)
```

**Common deopt reasons:**
- `unstable_if` — branch taken differently than profiled.
- `class_check` — type assumption violated.
- `null_check` — null encountered where null wasn't expected.
- `range_check` — array index out of expected bounds.

Frequent deoptimization is a performance red flag — it means the JIT is wasting cycles compiling, deoptimizing, and recompiling.

---

#### Code Cache

Compiled native code is stored in the **Code Cache** — a fixed-size off-heap region.

```bash
-XX:ReservedCodeCacheSize=256m    # Default ~240MB in modern JDK
-XX:InitialCodeCacheSize=2m
```

If the Code Cache fills up, JIT compilation stops and methods fall back to interpreter. This causes sudden, severe throughput degradation that looks like a memory leak but isn't.

Symptoms: `warning: CodeCache is full. Compiler has been disabled.` in logs.

Monitor with:
```bash
jcmd <pid> Compiler.codecache
# or
-XX:+PrintCodeCache  (on JVM exit)
```

---

#### Practical Flags Reference

```bash
# Enable/disable JIT
-Xint              # interpreter only (for debugging — very slow)
-Xcomp             # compile everything on first call (no profiling — often slower than tiered!)
-XX:+TieredCompilation  # default ON in Java 8+

# Compilation thresholds
-XX:CompileThreshold=10000          # pre-tiered: invocations before C2
-XX:Tier3InvocationThreshold=200    # invocations before Tier 3
-XX:Tier4InvocationThreshold=5000   # invocations before Tier 4

# Inlining
-XX:MaxInlineSize=35        # max bytecode size to inline (default 35)
-XX:FreqInlineSize=325      # max size for frequently-called methods
-XX:MaxInlineLevel=9        # max call chain depth for inlining

# Diagnostics
-XX:+PrintCompilation                        # log every JIT compilation event
-XX:+UnlockDiagnosticVMOptions
-XX:+PrintInlining                           # show what got inlined
-XX:+PrintEscapeAnalysis                     # escape analysis decisions
-XX:+PrintEliminateAllocations               # show scalar-replaced objects
-XX:+LogCompilation -XX:LogFile=jit.log      # XML log for JITWatch tool
```

**JITWatch** (open source tool by AdoptOpenJDK) can visualize the `LogCompilation` output — shows bytecode, inlining tree, and generated assembly side by side.

---

#### Summary: C1 vs C2 at a Glance

| Aspect | C1 (Client) | C2 (Server) |
|--------|------------|------------|
| Compilation speed | Fast (ms) | Slow (10s–100s ms) |
| Code quality | Good (~2–5x over interpreter) | Excellent (~10–50x) |
| Profiling | Counts + type profiles | Consumes C1 profiles |
| Optimizations | Basic inlining, null-check elim | All of C1 + escape analysis, loop opts, SIMD, devirt, intrinsics |
| Use case | Warm-up speed | Peak throughput |
| In tiered | Tiers 1–3 | Tier 4 |

In production long-running services (application servers, microservices), both compilers work together — C1 gets code running fast, C2 squeezes out maximum throughput once profiles are stable.

---

## 4. Garbage Collection — Core Concepts

### Q7. Explain GC roots. What are they?

**Answer:**

GC roots are the starting points for object reachability analysis. Any object reachable from a GC root is considered live.

**GC Roots include:**
1. **Local variables and operands on JVM stacks** (all active threads).
2. **Static variables** of loaded classes.
3. **JNI references** (native code holding Java objects).
4. **Active Java threads** themselves.
5. **System class loader and loaded classes.**
6. **Monitors** (objects used as synchronization locks currently held).

Objects not reachable from any root are eligible for collection.

---

### Q8. Explain Minor GC, Major GC, and Full GC. When does each happen?

**Answer:**

| GC Type | What it collects | Trigger |
|---------|-----------------|---------|
| **Minor GC** | Young Gen (Eden + Survivors) | Eden is full |
| **Major GC** | Old Gen | Old Gen fills up (GC-specific triggers) |
| **Full GC** | Entire heap + Metaspace | OOM risk, explicit `System.gc()`, concurrent mode failure (CMS), etc. |

**Minor GC details:**
- Stop-The-World (STW) pause, but short (Eden is small, GC is fast).
- Uses **copy collection**: live objects copied to Survivor or promoted to Old Gen; entire Eden is then "wiped" (bump pointer reset).
- Young Gen design exploits "weak generational hypothesis" — most objects die young.

**Full GC is expensive** because it must scan the entire heap. Minimize by tuning heap ratios and choosing appropriate GC algorithm.

---

### Q9. What is Stop-The-World (STW) and why does it happen?

**Answer:**

A **STW pause** is when the JVM suspends all application threads so GC can safely inspect/move objects.

**Why needed:** If application threads continue modifying object graphs while GC is running, GC cannot get a consistent view — it might miss live objects (live object collected = crash) or follow stale references.

**Safe points:** JVM doesn't just freeze threads arbitrarily. Threads reach a **safepoint** — a specific point in bytecode execution where the JVM's internal state is consistent enough to be paused. Common safepoints: loop back-edges, method calls, return statements.

**Time to safepoint (TTSP):** A thread stuck in a long JNI call or a counted loop without a back-edge can delay safepoint. This is why very long JNI calls can cause unexpectedly long GC pauses.

Modern collectors (G1, ZGC, Shenandoah) minimize STW by doing most work **concurrently** (while app runs), with only short STW phases for tasks that truly need consistency.

---

## 5. GC Algorithms in Detail

### Q10. Compare CMS, G1, ZGC, and Shenandoah.

**Answer:**

#### CMS (Concurrent Mark Sweep) — deprecated in Java 9, removed in 14

- Mostly concurrent Old Gen collection.
- Phases: Initial Mark (STW) → Concurrent Mark → Remark (STW) → Concurrent Sweep.
- **Problems:** Fragmentation (no compaction), "concurrent mode failure" (promotion fails, triggers Full GC), requires more CPU.

#### G1 (Garbage First) — default since Java 9

```
Heap divided into equal-sized regions (~1-32 MB each).
Each region can be Eden, Survivor, Old, or Humongous.
```

- **Key idea:** Prioritize collecting regions with most garbage first (hence "Garbage First").
- Predictable pause time goals via `-XX:MaxGCPauseMillis=200` (soft target).
- **Phases:**
  1. *Young GC (STW):* Evacuates Eden + Survivor regions.
  2. *Concurrent Marking:* Mark live objects across heap concurrently.
  3. *Mixed GC:* Collect Young + some Old regions in same pause.
  4. *Full GC (fallback):* Single-threaded (Java 10: parallel), triggered when concurrent can't keep up.
- **Humongous objects:** Objects > 50% region size go to special Humongous regions; can be a performance issue.
- Tuning: `-XX:G1HeapRegionSize`, `-XX:MaxGCPauseMillis`, `-XX:G1NewSizePercent`.

#### ZGC (Z Garbage Collector) — production-ready Java 15+

- **Goal:** Sub-millisecond STW pauses regardless of heap size (tested at 16TB).
- Uses **colored pointers** (load barriers in object references) to track object state without STW.
- Concurrent relocation — objects are moved while the app runs using **load barriers** that forward stale references on-the-fly.
- STW pauses: only 3 very short phases (Initial Mark, Pause Mark Start, Pause Relocate Start).
- Higher CPU overhead due to barriers; higher memory overhead.

#### Shenandoah — Red Hat, available in OpenJDK

- Similar goals to ZGC; concurrent compaction via **Brooks forwarding pointers** (extra word per object).
- Available as backport to older JDK versions.

#### Summary Table

| GC | Pause Goal | Throughput | Heap Size | Java Version |
|----|-----------|-----------|----------|-------------|
| Serial | N/A (STW all) | High single-thread | Small | Any |
| Parallel | Throughput | Highest | Medium-Large | Any |
| G1 | ~200ms target | Good | Medium-Very Large | 9+ (default) |
| ZGC | <1ms | Slightly lower | Any (up to TB) | 15+ |
| Shenandoah | <1ms | Slightly lower | Any | OpenJDK |

---

### Q11. What is a write barrier and a load barrier? Why do concurrent collectors need them?

**Answer:**

When GC runs concurrently with the application, the app can mutate the object graph while GC is scanning it. **Barriers** are small pieces of code injected by the JIT to notify GC of such mutations.

**Write barrier:** Executed when the app writes a reference into a field. Tells GC "this reference changed" so GC can re-examine. Used by G1's **Remembered Sets** to track cross-region references (Old → Young).

**Load barrier:** Executed when the app reads a reference from an object. ZGC uses this to check if the read reference points to a relocated object and transparently forwards it. This enables concurrent relocation without STW.

**Remembered Set (G1):** Each region maintains a card table of incoming references from other regions. This allows Minor GC to find all Young Gen roots from Old Gen without scanning all of Old Gen.

---

## 6. GC Tuning & Diagnostics

### Q12. How do you diagnose a GC problem in production?

**Answer:**

**Step 1 — Enable GC logs:**
```bash
# Java 11+
-Xlog:gc*:file=/logs/gc.log:time,uptime,pid:filecount=5,filesize=20m

# Java 8
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/logs/gc.log
```

**Step 2 — Identify the problem pattern:**

| Symptom | GC Log Signal | Likely Cause |
|---------|--------------|--------------|
| Frequent Minor GCs | Eden fills fast | Allocation rate too high, Eden too small |
| Long pause | Full GC / concurrent mode failure | Old Gen too small, memory leak |
| Growing heap + OOM | Old Gen never shrinks | Memory leak, finalizer queue backlog |
| Increasing GC frequency | Shorter and shorter intervals | Heap nearing capacity |

**Step 3 — Heap dump for memory leaks:**
```bash
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/
jmap -dump:format=b,file=heap.hprof <pid>
```
Analyze with **Eclipse MAT** or **VisualVM** — look for dominator tree, retained heap.

**Step 4 — Tools:**
- `jstat -gcutil <pid> 1000` — live GC stats every second.
- `jcmd <pid> GC.heap_info`
- GCViewer, GCEasy for log visualization.

**Step 5 — Common tuning levers (G1):**
```bash
-Xms4g -Xmx4g          # Same min/max avoids resize pauses
-XX:MaxGCPauseMillis=100
-XX:G1HeapRegionSize=8m
-XX:G1NewSizePercent=20
-XX:G1MaxNewSizePercent=40
-XX:ConcGCThreads=4
```

---

### Q13. What is promotion failure and concurrent mode failure?

**Answer:**

**Promotion failure (G1/Young GC):** During a Minor GC, objects surviving N GCs need to be promoted to Old Gen, but Old Gen has insufficient contiguous space. Results in a Full GC fallback.

**Causes:** Old Gen too small, too many long-lived objects, large objects, fragmentation.
**Fix:** Increase `-Xmx`, tune `-XX:G1NewSizePercent` to reduce Young Gen size (fewer promotions at once), set `-XX:MaxTenuringThreshold` higher.

**Concurrent mode failure (CMS-specific):** CMS runs concurrently, but if Old Gen fills up before concurrent collection finishes, CMS falls back to a single-threaded Full GC. Very expensive.

**Causes:** Allocation rate too high, CMS started too late.
**Fix (CMS):** `-XX:CMSInitiatingOccupancyFraction=70` (start CMS earlier).

---

## 7. HashMap Internals

### Q14. Explain the internal data structure of HashMap in Java 8+.

**Answer:**

```
HashMap internal structure (Java 8+):
───────────────────────────────────────
Node<K,V>[] table   (array, size always power of 2)

table[0] → null
table[1] → Node{hash, key, value, next} → Node → ...  (linked list)
table[2] → TreeNode{...} → TreeNode{...}              (red-black tree, when bucket >= 8)
...
table[n-1] → null
```

**Key fields:**
- `table`: array of `Node<K,V>` (buckets). Initially null, lazy-initialized on first `put`.
- `size`: number of key-value mappings.
- `threshold`: `capacity * loadFactor` — when `size` exceeds this, resize.
- `loadFactor`: default `0.75` (space-time tradeoff).
- `modCount`: structural modification counter for `ConcurrentModificationException` in iterators.

**Node types:**
- `Node<K,V>`: standard linked list node.
- `TreeNode<K,V>`: when a bucket has >= 8 entries AND table.length >= 64, the list is converted to a Red-Black Tree (O(log n) lookup).
- Reverts to linked list when size drops to <= 6.

---

### Q15. Walk through what happens during `put(key, value)`.

**Answer:**

```java
map.put("foo", 42);
```

1. **Compute hash:**
   ```java
   static final int hash(Object key) {
       int h;
       return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
   }
   ```
   XOR with upper 16 bits (**hash spreading**) — reduces collisions when table is small, because only the lower bits of hash determine the bucket index.

2. **Find bucket index:**
   ```java
   index = (n - 1) & hash   // n = table.length (power of 2)
   ```
   Bit AND instead of modulo — works only because n is always a power of 2.

3. **If table is null or empty:** `resize()` (initialize to default capacity 16).

4. **If bucket is empty:** Insert new `Node` directly.

5. **If bucket is non-empty:**
   - If first node matches (`hash` equals AND `key.equals(key)`): update value.
   - Else traverse list/tree:
     - Found matching node: update.
     - Not found: append new node to list tail (Java 8: **tail insertion**, vs Java 7: head insertion).
   - If list length reaches 8: `treeifyBin()` → convert to Red-Black Tree (only if `table.length >= 64`, else resize).

6. **If `++size > threshold`:** `resize()` — double capacity.

7. **`afterNodeInsertion()`** hook for `LinkedHashMap`.

---

### Q16. How does resize() work? What is rehashing?

**Answer:**

---

#### Why resize at all?

HashMap is a hash table backed by an array (`Node<K,V>[] table`). As more entries are added, more buckets get occupied. When too many entries share the same bucket, lookup degrades from O(1) toward O(n) because you must traverse the bucket's linked list.

The **load factor** (default `0.75`) controls the trade-off:
- **Low load factor (e.g. 0.25):** Fewer collisions, but wastes memory (array mostly empty).
- **High load factor (e.g. 0.99):** Dense packing, saves memory, but many collisions and slow lookups.
- **0.75** is the empirically tuned default — at 75% occupancy the expected bucket list length stays near 1.

**Resize trigger:**
```
threshold = capacity × loadFactor
           = 16 × 0.75 = 12    (initial defaults)

When size > threshold → resize() is called
```

---

#### Phase 1 — Allocate the new array

`resize()` doubles the capacity (always a power of 2):

```
Before resize:            After resize:
capacity  = 16            capacity  = 32
threshold = 12            threshold = 24   (32 × 0.75)
table     = Node[16]      table     = Node[32]
```

The new array is allocated but all slots are null. The old array still exists until migration is complete.

Why always a power of 2? Because the bucket index is computed as:
```java
index = (capacity - 1) & hash
```
Bit-AND only works correctly as a modulo substitute when capacity is a power of 2. For capacity 16: `(16-1) & hash = 0b1111 & hash` — uses the lowest 4 bits of hash. For capacity 32: `0b11111 & hash` — uses the lowest 5 bits.

---

#### Phase 2 — Rehashing: moving every node to its new position

Every entry in the old table must be re-placed into the new table. This is **rehashing**.

**Naive approach (Java 7 and earlier):**
```java
// For every node, recompute: newIndex = (newCapacity - 1) & hash
// Problem: must call hashCode() again or re-read stored hash for every node
// Also: Java 7 used head-insertion, which REVERSED the order of the chain
//       This caused an infinite loop bug under concurrent access (famous Java 7 bug)
```

**Java 8 optimization — the single-bit trick:**

When capacity doubles from `N` to `2N`, the new index formula uses one additional bit of the hash compared to the old formula:

```
Old capacity = 16 = 0b 0001 0000
Old mask     = 15 = 0b 0000 1111   → uses bits [3:0] of hash

New capacity = 32 = 0b 0010 0000
New mask     = 31 = 0b 0001 1111   → uses bits [4:0] of hash
                            ^
                    this ONE new bit is the only difference
```

So for any given entry, its new index is determined entirely by **bit 4** (the position of `oldCapacity` in binary):

```
if (hash & oldCapacity == 0)  →  bit 4 is 0  →  newIndex = oldIndex
if (hash & oldCapacity != 0)  →  bit 4 is 1  →  newIndex = oldIndex + oldCapacity
```

No need to recompute `hashCode()`. No modulo. Just one AND with `oldCap`.

---

#### Step-by-step visual example

Suppose capacity grows from **8 → 16**.

Entry hashes (last 4 bits matter for old; last 5 for new):

```
Key     hash (binary)    Old index        New index
                         (hash & 0b0111)  (hash & 0b1111)
────────────────────────────────────────────────────────
"apple"  ...0 0101         5               5    ← bit3=0, stays
"grape"  ...0 1101         5              13    ← bit3=1, moves to 5+8=13
"mango"  ...1 0011         3               3    ← bit3=0, stays
"plum"   ...1 1011         3              11    ← bit3=1, moves to 3+8=11
```

Visualization:
```
Old table (capacity=8):          New table (capacity=16):
[0]                              [0]
[1]                              [1]
[2]                              [2]
[3] → mango → plum               [3] → mango         (bit3=0, stay)
[4]                              [4]
[5] → apple → grape              [5] → apple         (bit3=0, stay)
[6]                              [6]
[7]                              [7]
                                 ...
                                 [11] → plum          (bit3=1, moved from 3)
                                 [13] → grape         (bit3=1, moved from 5)
                                 [15]
```

Each bucket's linked list is split into **two sub-lists** — "lo" (stays) and "hi" (moves) — using tail insertion to preserve original order:

```java
// Simplified from JDK source (HashMap.resize):
Node<K,V> loHead = null, loTail = null;  // stays at oldIndex
Node<K,V> hiHead = null, hiTail = null;  // moves to oldIndex + oldCap

for (Node<K,V> e = oldBucket; e != null; e = e.next) {
    if ((e.hash & oldCap) == 0) {        // bit check
        if (loTail == null) loHead = e;
        else loTail.next = e;
        loTail = e;
    } else {
        if (hiTail == null) hiHead = e;
        else hiTail.next = e;
        hiTail = e;
    }
}
if (loTail != null) { loTail.next = null; newTab[j] = loHead; }
if (hiTail != null) { hiTail.next = null; newTab[j + oldCap] = hiHead; }
```

Key detail: `loTail.next = null` and `hiTail.next = null` are required to terminate the new lists — otherwise old next pointers would still chain nodes that belong in different buckets.

---

#### What happens to TreeNodes (Red-Black Trees) during resize?

When a bucket is a Red-Black Tree (treeified), resize handles it differently:

1. The tree nodes are split into "lo" and "hi" groups using the same bit-check trick.
2. If the resulting group has **≤ 6 nodes** → convert back to a linked list (**untreeify**).
3. If the resulting group has **> 6 nodes** → rebuild as a new Red-Black Tree.

This is why both thresholds exist:
- Treeify at **8** (when list gets long enough to need a tree).
- Untreeify at **6** (hysteresis gap prevents thrashing between tree and list on resize).

---

#### When is resize() called?

| Trigger | Condition |
|---------|-----------|
| First `put()` | `table == null` → initialize to capacity 16 |
| Normal growth | `++size > threshold` after any `put()` |
| Treeify guard | Bucket wants to treeify but `table.length < 64` → resize instead |

The third case is subtle: `treeifyBin()` refuses to treeify if the overall table is still small. Instead of building a tree for a small table, it's better to resize (reduce collisions by spreading entries across more buckets).

---

#### Performance cost of resize

Resize is an **O(n)** operation — every entry must be visited and moved. For a map with 1 million entries, a resize touches all 1 million nodes.

**How to avoid unnecessary resizes:**

If you know the expected number of entries upfront, pass the initial capacity to the constructor:
```java
// Without hint: will resize at 12, 24, 48, 96... entries → many resizes
Map<String, Integer> map = new HashMap<>();

// With hint: sets initial capacity so first resize happens AFTER inserting ~75 entries
// Formula: initialCapacity = expectedEntries / loadFactor + 1
Map<String, Integer> map = new HashMap<>(128);  // for ~96 expected entries

// Even better: Guava does the math for you
Map<String, Integer> map = Maps.newHashMapWithExpectedSize(96);
// Guava internally computes: capacity = ceil(expectedSize / 0.75) rounded to power of 2
```

---

#### Java 7 vs Java 8 rehashing comparison

| Aspect | Java 7 | Java 8 |
|--------|--------|--------|
| Insertion order in new bucket | **Reversed** (head insertion) | **Preserved** (tail insertion) |
| Concurrent resize bug | Infinite loop possible (cycle in list) | Fixed by tail insertion + careful ordering |
| Rehashing method | Full recompute of `(newCap-1) & hash` | Single bit check `(hash & oldCap)` |
| Tree support | No | Yes — tree buckets split and untreeified if needed |

The Java 7 infinite loop was a notorious bug: two threads resizing simultaneously with head-insertion could create a cycle in the linked list, causing `get()` to spin forever. This is why **HashMap is not thread-safe** — use `ConcurrentHashMap` for concurrent access.

---

### Q17. Why was the Red-Black Tree added in Java 8? What was the problem before?

**Answer:**

**Java 7 problem:** With many hash collisions (or malicious inputs crafting hash collisions), all keys could land in the same bucket — turning O(1) `get`/`put` into O(n). This enabled **Hash DoS attacks** against web applications (query string parameters as HashMap keys).

**Java 8 fix:** When a bucket grows beyond 8 nodes, convert to a Red-Black Tree → O(log n) worst case.

**Why Red-Black Tree specifically?** Balanced BST guarantees O(log n). Red-Black Tree is preferred over AVL because it requires fewer rotations on insert/delete (better write performance), while both give O(log n) lookups.

**Treeification threshold = 8:** Chosen based on Poisson distribution of bin occupancy at 0.75 load factor — probability of a bin having 8+ entries by random hashing is ~0.00000006. So in practice treeification almost never happens with good `hashCode()` implementations.

---

### Q18. What happens when keys have poor `hashCode()` — all returning the same value?

**Answer:**

All keys map to the same bucket. In Java 7: O(n) list. In Java 8: O(n) until 8 entries, then O(log n) Red-Black Tree.

**But there's a subtlety with treeification:** TreeNode ordering requires keys to implement `Comparable`. If they don't, HashMap falls back to `System.identityHashCode()` for tiebreaking. This still works but the tree may be unbalanced.

**Lesson:** Always override both `hashCode()` and `equals()` consistently; distribute hash bits well. Use `Objects.hash()` or IDE-generated implementations.

---

### Q19. What is the difference between `HashMap`, `LinkedHashMap`, `TreeMap`, and `Hashtable`?

**Answer:**

| | HashMap | LinkedHashMap | TreeMap | Hashtable |
|---|---|---|---|---|
| Order | No guarantee | Insertion or access order | Sorted by key | No guarantee |
| Null keys | 1 allowed | 1 allowed | No (NPE) | No |
| Null values | Allowed | Allowed | Allowed | No |
| Thread safety | No | No | No | Yes (synchronized) |
| Performance | O(1) avg | O(1) avg | O(log n) | O(1) avg but locked |
| Underlying | Hash table | Hash table + doubly-linked list | Red-Black Tree | Hash table |

**LinkedHashMap** maintains a doubly-linked list across all entries. `accessOrder=true` enables LRU cache behavior — `removeEldestEntry()` hook to evict.

---

## 8. ConcurrentHashMap Internals

### Q20. How does ConcurrentHashMap work in Java 8? How is it different from Java 7?

**Answer:**

**Java 7 ConcurrentHashMap:** Used **segments** (default 16), each a mini-HashMap with its own `ReentrantLock`. Concurrency level = number of segments.

**Java 8 ConcurrentHashMap:** Dropped segments entirely. Uses the same `Node[] table` as HashMap but with **fine-grained synchronization:**

- **Reads:** Lock-free. `table` is `volatile`; individual nodes use `volatile` fields. `get()` never locks.
- **Writes (no collision):** Uses **CAS (Compare-And-Swap)** to atomically insert into an empty bucket — no lock.
- **Writes (collision):** `synchronized` on the **first node of the bucket** (bin-level lock). Only the specific bucket is locked, not the whole map.
- **Resize:** Uses `ForwardingNode` — during resize, each migrated bucket gets a `ForwardingNode`. Threads encountering a `ForwardingNode` during `put` help with the resize (**cooperative transfer**).

**`size()` and `mappingCount()`:** Use `CounterCell` distributed counters (similar to `LongAdder`) to avoid contention on a single size field.

**Atomics:** `putIfAbsent`, `computeIfAbsent`, `merge` are atomic — useful for cache patterns.

---

## 9. Design Patterns

### Q21. Explain Singleton pattern. What are the pitfalls and the best implementation?

**Answer:**

**Naive (broken in multi-threaded):**
```java
public class Singleton {
    private static Singleton instance;
    public static Singleton getInstance() {
        if (instance == null)          // Race condition!
            instance = new Singleton();
        return instance;
    }
}
```

**Double-Checked Locking (broken without `volatile` pre-Java 5):**
```java
private static volatile Singleton instance;  // volatile is REQUIRED

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null)
                instance = new Singleton();
        }
    }
    return instance;
}
```
`volatile` prevents reordering: without it, the JIT could publish a partially-constructed object.

**Best: Initialization-on-demand holder (Bill Pugh idiom):**
```java
public class Singleton {
    private Singleton() {}

    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```
- Lazy initialization (class loaded only when `getInstance()` first called).
- Thread-safe by class loading guarantees (JVM ensures class initialization is single-threaded).
- No synchronization overhead.

**Best: Enum Singleton (Effective Java recommendation):**
```java
public enum Singleton {
    INSTANCE;
    // methods here
}
```
- Serialization-safe (prevents creating new instance via deserialization).
- Reflection-safe (enums can't be instantiated via reflection).

**Pitfalls:**
- Singletons make unit testing hard (tight coupling, hard to mock).
- In OSGi/app servers, multiple classloaders can create multiple "singletons."
- Prefer dependency injection over Singleton pattern in modern code.

---

### Q22. Explain the differences between Factory Method, Abstract Factory, and Builder patterns.

**Answer:**

**Factory Method:** Defines an interface for creating an object, but lets subclasses decide which class to instantiate.
```java
abstract class Dialog {
    abstract Button createButton();  // Factory Method
    void render() { createButton().render(); }
}
class WindowsDialog extends Dialog {
    Button createButton() { return new WindowsButton(); }
}
```

**Abstract Factory:** Creates families of related objects without specifying concrete classes.
```java
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
class MacFactory implements GUIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}
```
Key difference from Factory Method: Abstract Factory creates multiple related products; Factory Method creates one product via subclass specialization.

**Builder:** Constructs complex objects step by step. Useful when object has many optional parameters.
```java
Person person = new Person.Builder("Alice")
    .age(30)
    .email("alice@example.com")
    .build();
```
Solves the **telescoping constructor anti-pattern** (constructors with many optional params).

**When to use:**
- Factory Method: When you know the type at compile time via subclassing.
- Abstract Factory: When you need to create families of related objects and swap entire families.
- Builder: When constructing objects with many optional fields or a complex multi-step construction process.

---

### Q23. Explain Strategy, Template Method, and Command patterns with real examples.

**Answer:**

**Strategy — define a family of algorithms, encapsulate each, make them interchangeable:**
```java
interface SortStrategy { void sort(int[] arr); }

class Context {
    private SortStrategy strategy;
    Context(SortStrategy s) { this.strategy = s; }
    void sort(int[] arr) { strategy.sort(arr); }
}
// Switch strategy at runtime: new Context(new QuickSort())
```
Real example: `Comparator` in Java's sort API, payment methods in checkout.

**Template Method — define skeleton of algorithm in base class, defer some steps to subclasses:**
```java
abstract class DataMiner {
    final void mine() {       // template method
        openFile();
        extractData();        // hook
        parseData();          // hook
        analyze();
        closeFile();
    }
    abstract void extractData();
    abstract void parseData();
}
```
Real example: `HttpServlet.service()` calls `doGet()`/`doPost()`.

Difference from Strategy: Template Method uses **inheritance**; Strategy uses **composition**. Prefer Strategy (more flexible, testable).

**Command — encapsulate a request as an object:**
```java
interface Command { void execute(); void undo(); }

class PasteCommand implements Command {
    private Editor editor;
    public void execute() { editor.paste(); }
    public void undo() { editor.delete(); }
}
// CommandQueue enables undo/redo, logging, scheduling
```
Real example: UI action buttons, transaction logs, job queues.

---

### Q24. Observer vs Event Bus vs Reactive Streams — when to use each?

**Answer:**

**Observer (GoF):** Subject holds references to Observers, calls `update()` directly. Simple, synchronous, tight coupling between subject and observers.
- Use: Simple event notification in same module/class.
- Problem: Memory leaks (observers not unregistered), blocking if observer is slow.

**Event Bus (Guava EventBus, Spring ApplicationEventPublisher):** Decoupled — publishers don't know subscribers. Bus dispatches to registered subscribers.
- Use: Loose coupling within an application, cross-module events.
- Problem: Hard to trace who handles an event; async buses hide errors.

**Reactive Streams (RxJava, Project Reactor):** Asynchronous, non-blocking, backpressure-aware streams.
- Use: High-throughput event streams, async pipelines, WebFlux applications.
- Problem: Steep learning curve, debugging stack traces are complex.

---

### Q25. Explain Proxy pattern and its real-world uses in Java frameworks.

**Answer:**

Proxy provides a surrogate/placeholder that controls access to the real object.

**Types:**
- **Virtual proxy:** Lazy initialization (create expensive object only when needed).
- **Protection proxy:** Access control.
- **Remote proxy:** Local representative for remote object (RMI).
- **Caching proxy:** Cache results.
- **Logging/monitoring proxy:** Intercept calls for cross-cutting concerns.

**Java dynamic proxies:**
```java
// JDK Dynamic Proxy (interface-based)
MyService proxy = (MyService) Proxy.newProxyInstance(
    MyService.class.getClassLoader(),
    new Class[]{MyService.class},
    (p, method, args) -> {
        System.out.println("Before: " + method.getName());
        Object result = method.invoke(realService, args);
        System.out.println("After: " + method.getName());
        return result;
    });
```

**Real-world uses:**
- **Spring AOP:** `@Transactional`, `@Cacheable`, `@Async` — Spring wraps your bean in a proxy.
- **Hibernate:** Lazy-loaded entities are proxies; accessing a field triggers a DB query.
- **Mockito:** Test mocks are proxies.
- **Spring Data repositories:** Interface-only — Spring generates a proxy at startup.

**CGLIB vs JDK Proxy:** JDK proxy requires an interface. CGLIB creates a subclass (used by Spring when no interface exists). Cannot proxy `final` classes/methods.

---

## 10. High-Level Design Questions

### Q26. Design a thread-safe LRU Cache.

**Answer:**

```java
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> cache;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int capacity) {
        this.capacity = capacity;
        // accessOrder=true: get() moves entry to tail
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public V get(K key) {
        lock.writeLock().lock();  // write lock because get() mutates order
        try {
            return cache.getOrDefault(key, null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

**Alternative — `ConcurrentHashMap` + `ConcurrentLinkedDeque`** for higher concurrency (but `LinkedHashMap` is simpler and correct for most use cases).

**For very high concurrency:** Caffeine library uses a **Window TinyLFU** policy with lock-free reads and async eviction — far superior to a synchronized `LinkedHashMap`.

---

### Q27. Design a Rate Limiter.

**Answer:**

**Algorithms:**

**Token Bucket (most common):**
- Bucket holds max N tokens; tokens added at rate R/second.
- Each request consumes 1 token. If empty, request is rejected/waited.
- Allows bursts up to bucket capacity.

```java
class TokenBucket {
    private final long maxTokens;
    private final long refillRatePerSecond;
    private long tokens;
    private long lastRefillTime;

    synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) { tokens--; return true; }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = (now - lastRefillTime) / 1000;
        tokens = Math.min(maxTokens, tokens + elapsed * refillRatePerSecond);
        lastRefillTime = now;
    }
}
```

**Sliding Window Log:** Maintain a log of request timestamps; count requests in the last window. More accurate but higher memory.

**Sliding Window Counter:** Hybrid — track current and previous window counts, interpolate. Memory-efficient, slightly approximate.

**Distributed Rate Limiter:** Use Redis with `INCR` + expiry (fixed window) or Lua scripts for token bucket across instances. Libraries: Resilience4j, Bucket4j (Redis-backed).

---

### Q28. Design a Pub/Sub system (simplified Kafka-like).

**Answer:**

**Core components:**
```
Producers → [Broker: Topic/Partitions] → Consumers (Consumer Groups)
```

**Key design decisions:**

**Topic partitioning:** Each topic split into N partitions for parallelism. Messages keyed on a field hash to the same partition (ordering guarantee within partition).

**Consumer groups:** Each partition consumed by exactly one consumer within a group. Multiple groups can read the same topic independently (broadcast).

**Offset tracking:** Each consumer tracks its position (offset) per partition. Can replay, skip ahead, or commit periodically. Store offsets in a dedicated topic or external store.

**Persistence:** Messages written to append-only log files on disk. Retention by time or size. Sequential disk I/O is fast (matches HDD/SSD sequential write speed).

**At-least-once vs exactly-once:**
- At-least-once: Consumer commits offset after processing. On failure, may reprocess.
- Exactly-once: Requires idempotent producers (sequence numbers) + transactional consumers (atomic offset commit + side-effect).

**Backpressure:** If consumers are slow, lag (offset difference) grows. Monitor consumer group lag for alerting.

---

### Q29. How would you design a system to handle 1 million concurrent WebSocket connections?

**Answer:**

**Key challenge:** Traditional thread-per-connection model fails — 1M threads would consume ~1TB RAM (1MB stack each).

**Solution: Non-blocking I/O (NIO) / Async model:**
- Use Netty, Vert.x, or Spring WebFlux (Project Reactor + Netty).
- Single event loop thread can multiplex thousands of connections via OS `epoll`/`kqueue`.
- Connections are just file descriptors; no thread allocated per connection.

**Architecture layers:**
```
Clients → Load Balancer (Layer 4, sticky sessions or consistent hash)
        → WebSocket Gateway Nodes (Netty-based)
        → Message Bus (Kafka / Redis Pub-Sub)
        → Backend Services
```

**Connection state management:**
- In-memory: Map from `connectionId → Channel` (Netty Channel) on each node.
- Cross-node messaging: To send to a user connected to node B from node A, use Redis Pub/Sub or Kafka to route.

**OS-level tuning:**
- Increase file descriptor limit: `ulimit -n 1048576`.
- Increase `net.core.somaxconn` (backlog queue).
- TCP keep-alive tuning.
- Each Netty event loop thread can handle ~50k-100k connections → need ~10-20 threads for 1M.

---

### Q30. What is the difference between optimistic and pessimistic locking? When to use each?

**Answer:**

**Pessimistic Locking:**
- Assume conflicts will happen. Lock the resource before reading.
- In DB: `SELECT ... FOR UPDATE` — row-level lock held until transaction commits.
- In Java: `synchronized`, `ReentrantLock`.
- Pros: Prevents conflicts entirely. Consistent view guaranteed.
- Cons: Reduces concurrency (others blocked), deadlock risk, overhead even when no conflict.

**Optimistic Locking:**
- Assume conflicts are rare. Read without locking; detect conflict on write.
- Add a `version` column. On update: `UPDATE ... WHERE id=? AND version=?`. If 0 rows affected → conflict → retry.
- In JPA: `@Version` annotation.
- In Java: `AtomicInteger.compareAndSet()`.
- Pros: Higher throughput (no blocking on reads), no deadlocks.
- Cons: Must handle retry logic; wasteful if conflicts are frequent.

**When to use:**

| Scenario | Recommendation |
|----------|---------------|
| Read-heavy, rare writes | Optimistic |
| Write-heavy, frequent conflicts | Pessimistic |
| Long transactions (report generation) | Pessimistic |
| Short transactions, high concurrency | Optimistic |
| Distributed systems | Optimistic (with version/ETag) |

---

### Q31. Explain the concept of happens-before in Java Memory Model.

**Answer:**

The **Java Memory Model (JMM)** defines when one thread's writes are guaranteed to be visible to another thread's reads. The **happens-before (HB)** relationship provides this guarantee.

**Key happens-before rules:**

1. **Program order rule:** Each action in a thread HB every subsequent action in that thread.
2. **Monitor lock rule:** `unlock()` of a monitor HB every subsequent `lock()` of that monitor.
3. **Volatile variable rule:** Write to a `volatile` variable HB every subsequent read of that variable.
4. **Thread start rule:** `Thread.start()` HB every action in the started thread.
5. **Thread termination rule:** All actions in thread T HB `Thread.join()` on T returning.
6. **Transitivity:** If A HB B and B HB C, then A HB C.

**Practical impact:**
```java
volatile boolean started = false;
int x = 0;

// Thread 1:
x = 42;
started = true;   // volatile write

// Thread 2:
if (started) {    // volatile read — establishes HB
    // x == 42 is GUARANTEED (HB transitivity through volatile)
    System.out.println(x);
}
```

Without `volatile`, Thread 2 might read `x = 0` (stale value from CPU cache or reordering).

---

### Q32. What are the SOLID principles? Give a Java violation and fix for each.

**Answer:**

**S — Single Responsibility Principle:**
- Violation: `UserService` that validates, saves to DB, and sends emails.
- Fix: Split into `UserValidator`, `UserRepository`, `EmailService`.

**O — Open/Closed Principle:**
- Violation: `if (shape instanceof Circle) ... else if (shape instanceof Rectangle)...` in `AreaCalculator`.
- Fix: `interface Shape { double area(); }` — each shape implements `area()`. Adding shapes doesn't modify `AreaCalculator`.

**L — Liskov Substitution Principle:**
- Violation: `Square extends Rectangle` — `setWidth(5)` on a `Square` also sets height, breaking `Rectangle` contracts.
- Fix: Don't extend; use a common `Shape` interface. Or make `Rectangle` immutable.

**I — Interface Segregation Principle:**
- Violation: `interface Worker { void work(); void eat(); }` — robots implement `Worker` but throw on `eat()`.
- Fix: `interface Workable { void work(); }` and `interface Eatable { void eat(); }`.

**D — Dependency Inversion Principle:**
- Violation: `OrderService` directly instantiates `MySQLRepository`.
- Fix: `OrderService` depends on `OrderRepository` interface; inject via constructor. Swap impl without changing `OrderService`.

---

## Quick Reference — JVM Flags

```bash
# Heap
-Xms512m -Xmx4g
-XX:NewRatio=2                    # Old:Young ratio
-XX:SurvivorRatio=8               # Eden:Survivor ratio

# GC Selection
-XX:+UseG1GC
-XX:+UseZGC
-XX:+UseShenandoahGC

# G1 Tuning
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=8m
-XX:G1NewSizePercent=20
-XX:G1MaxNewSizePercent=40
-XX:ConcGCThreads=4
-XX:ParallelGCThreads=8

# GC Logging (Java 11+)
-Xlog:gc*:file=/gc.log:time,uptime:filecount=5,filesize=20m

# Diagnostics
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/dumps/
-XX:NativeMemoryTracking=detail

# Compilation
-XX:+PrintCompilation
-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
```

---

## Key Numbers to Remember

| Fact | Value |
|------|-------|
| Default HashMap capacity | 16 |
| Default load factor | 0.75 |
| Treeification threshold | 8 entries |
| Untreeification threshold | 6 entries |
| Default MaxTenuringThreshold | 15 |
| G1 default region size | Computed (~1-32MB) |
| TLAB default | Enabled |
| Default thread stack size | 512KB (64-bit) |

---

*Good luck with your Hitachi interview!*
