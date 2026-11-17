# Java Streams — Interview Prep (Senior / 16+ YOE)

> Focus: Java 8+ Streams API. Questions move from fundamentals to internals and
> performance/design judgement — the kind senior-level interviewers probe for.
> Each answer is detailed enough to speak from directly in the interview.

---

## 1. What is a Stream in Java? How is it different from a Collection?

A `Stream` is **not a data structure** — it's an abstraction for processing a
sequence of elements with a pipeline of computations (map, filter, reduce,
etc.), evaluated **lazily**.

Key differences from a `Collection`:

| Collection | Stream |
|---|---|
| Stores data in memory | Doesn't store data — pulls from a source (Collection, array, I/O channel, generator) |
| Eager — all elements exist now | Lazy — nothing happens until a terminal operation is invoked |
| Can be iterated multiple times | **Consumed once** — reusing a stream throws `IllegalStateException` |
| External iteration (`for`, `Iterator`) | Internal iteration (library controls the loop) |
| Mutable | Operations produce a *new* stream; original data source isn't mutated by the stream itself |

```java
List<String> names = List.of("Tom", "Amy", "Bob");
Stream<String> s = names.stream();
s.forEach(System.out::println);
s.forEach(System.out::println); // throws IllegalStateException: stream has already been operated upon or closed
```

---

## 2. What is the difference between intermediate and terminal operations?

- **Intermediate operations** (`map`, `filter`, `sorted`, `distinct`, `flatMap`,
  `limit`, `skip`, `peek`) return a new `Stream` and are **lazy** — they just
  build up a pipeline description, they don't execute anything.
- **Terminal operations** (`collect`, `forEach`, `reduce`, `count`, `anyMatch`,
  `findFirst`, `toArray`, `sum` on primitive streams) trigger the **actual
  traversal** of the source and execution of the whole pipeline. After a
  terminal op runs, the stream is **closed/consumed**.

A stream pipeline with no terminal operation does nothing at all:

```java
list.stream().filter(x -> { System.out.println("never printed"); return true; });
// no output — nothing executes without a terminal operation
```

---

## 3. Explain lazy evaluation in Streams with an example.

Streams build a pipeline of operations but don't execute element-by-element
through each stage; instead, for each element, the **entire pipeline is
applied in one pass** before moving to the next element (this is different
from how people often picture it). Nothing runs until a terminal op is
called, and operations like `limit` can let the pipeline **short-circuit**
without processing the whole source.

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

Optional<Integer> result = nums.stream()
        .peek(n -> System.out.println("peek: " + n))
        .filter(n -> n % 2 == 0)
        .peek(n -> System.out.println("after filter: " + n))
        .findFirst();
```

Output:
```
peek: 1
peek: 2
after filter: 2
```
It stops at `2` because `findFirst` is short-circuiting — it never even looks
at 3, 4, 5. This demonstrates **element-by-element, demand-driven pipelining**,
not "run filter on everything, then run peek on everything."

---

## 4. What's the difference between `map()` and `flatMap()`?

- `map()` — transforms each element **1-to-1**: `Stream<T> -> Stream<R>`.
- `flatMap()` — transforms each element into a **stream of elements**, then
  flattens all those streams into a single stream: `Stream<T> -> Stream<Stream<R>> -> Stream<R>`.

Classic use case: flattening a list of lists.

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));

List<Integer> flat = nested.stream()
        .flatMap(List::stream)
        .collect(Collectors.toList());
// [1, 2, 3, 4, 5]
```

If you'd used `map(List::stream)` instead, you'd get a `Stream<Stream<Integer>>`
— not useful directly.

Another common one — splitting sentences into words:

```java
List<String> sentences = List.of("hello world", "java streams");
List<String> words = sentences.stream()
        .flatMap(s -> Arrays.stream(s.split(" ")))
        .collect(Collectors.toList());
// [hello, world, java, streams]
```

---

## 5. What is the difference between `Collectors.toList()` and `collect(Collector)` vs `reduce()`?

- `reduce()` is a **general-purpose fold** — combines stream elements into a
  single result using an associative accumulator function. Best for
  immutable accumulation (sums, min/max, concatenation of immutable types).
- `collect()` with `Collectors` is designed for **mutable reduction** —
  accumulating into a mutable container (`List`, `Set`, `Map`, `StringBuilder`)
  efficiently, especially in parallel streams, because it uses a mutable
  container + combiner instead of creating a new immutable object per step.

```java
// reduce — fine for immutable sum
int sum = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);

// collect — correct tool for building a List
List<Integer> doubled = Stream.of(1, 2, 3, 4)
        .map(x -> x * 2)
        .collect(Collectors.toList());
```

Using `reduce` to build a `List` (anti-pattern) would mean creating a new
list on every element — O(n²) copying and it breaks down badly in parallel
streams because it's not a true mutable-combiner reduction.

---

## 6. Explain `reduce()` method overloads in detail.

Three overloads:

```java
Optional<T> reduce(BinaryOperator<T> accumulator);

T reduce(T identity, BinaryOperator<T> accumulator);

<U> U reduce(U identity,
             BiFunction<U, ? super T, U> accumulator,
             BinaryOperator<U> combiner);
```

1. **No identity** — returns `Optional<T>` because the stream could be empty.
   ```java
   Optional<Integer> max = Stream.of(3, 1, 4, 1, 5).reduce(Integer::max);
   ```

2. **With identity** — returns `T` directly (identity is the fallback/seed
   and also the neutral element: `combine(identity, x) == x`).
   ```java
   int sum = Stream.of(1, 2, 3).reduce(0, Integer::sum);
   ```

3. **Three-arg (identity, accumulator, combiner)** — needed when the
   accumulator produces a **different type** than the stream elements,
   which matters for parallel execution where partial results (type `U`)
   must be combined together via `combiner`.
   ```java
   int totalLength = Stream.of("a", "bb", "ccc")
           .reduce(0, (partialLen, str) -> partialLen + str.length(), Integer::sum);
   ```
   In sequential streams the combiner is never actually invoked (only one
   partial result exists), but it's **required** for correctness when the
   stream runs in parallel — each thread/sub-task accumulates its own `U`
   and the combiner merges them.

**Interviewer follow-up trap:** "Why does the 3-arg reduce need the combiner
if sequential streams don't call it?" — Because the API must guarantee
correct behavior whether the stream is sequential or parallel; the contract
is defined for both.

---

## 7. What's the difference between `findFirst()` and `findAny()`? Why would `findAny()` exist?

- `findFirst()` — always returns the **first element encountered in
  encounter order**, even in a parallel stream (it has to coordinate across
  threads to guarantee order, which has a performance cost).
- `findAny()` — returns **any** matching element, with no ordering guarantee.
  In parallel streams, this is much cheaper because the first thread to
  find a match can return immediately — no cross-thread coordination to
  preserve order.

Use `findAny()` when element order genuinely doesn't matter (e.g., "does any
record match this filter") — especially in parallel pipelines, for
performance.

---

## 8. Explain `Collectors.groupingBy()` with examples, including the 2-arg and 3-arg forms.

`groupingBy` is the stream equivalent of SQL's `GROUP BY`.

```java
record Employee(String name, String dept, double salary) {}

List<Employee> emps = List.of(
        new Employee("A", "IT", 90000),
        new Employee("B", "IT", 75000),
        new Employee("C", "HR", 60000)
);

// 1-arg: classifier only -> Map<K, List<V>>
Map<String, List<Employee>> byDept = emps.stream()
        .collect(Collectors.groupingBy(Employee::dept));

// 2-arg: classifier + downstream collector -> Map<K, D>
Map<String, Double> avgSalaryByDept = emps.stream()
        .collect(Collectors.groupingBy(Employee::dept,
                Collectors.averagingDouble(Employee::salary)));

// 3-arg: classifier + map factory + downstream collector
Map<String, Long> countByDeptSorted = emps.stream()
        .collect(Collectors.groupingBy(Employee::dept,
                TreeMap::new,                 // use TreeMap -> sorted by key
                Collectors.counting()));
```

Common downstream collectors used with `groupingBy`: `counting()`,
`summingDouble/Int/Long`, `averagingX`, `mapping()`, `toList()`,
`maxBy/minBy`, `joining()`, `toSet()`.

Nested grouping (multi-level group-by):
```java
Map<String, Map<Boolean, List<Employee>>> byDeptThenHighPaid = emps.stream()
        .collect(Collectors.groupingBy(Employee::dept,
                Collectors.partitioningBy(e -> e.salary() > 70000)));
```

---

## 9. `groupingBy` vs `partitioningBy` — when would you use one over the other?

- `groupingBy` — classifies elements into **arbitrary number of groups**
  based on a classifier function's return value (any type `K`).
- `partitioningBy` — classifies into **exactly two groups**, based on a
  `Predicate` — always produces `Map<Boolean, List<T>>` with **both keys
  always present** (`true`/`false`), even if one group is empty.

```java
Map<Boolean, List<Integer>> evenOdd = Stream.of(1, 2, 3, 4)
        .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1, 3], true=[2, 4]}
```

Use `partitioningBy` when the split is genuinely binary (pass/fail,
valid/invalid) — it's slightly more efficient than `groupingBy` with a
boolean classifier and guarantees both keys exist, simplifying downstream
code (no need for `getOrDefault`).

---

## 10. Explain `Collectors.toMap()` and the duplicate-key problem.

```java
Map<String, Integer> nameToAge = people.stream()
        .collect(Collectors.toMap(Person::name, Person::age));
```

**Problem:** if two elements map to the same key, `toMap` throws
`IllegalStateException: Duplicate key` by default.

**Fix** — supply a merge function (3-arg form):

```java
Map<String, Integer> nameToAge = people.stream()
        .collect(Collectors.toMap(
                Person::name,
                Person::age,
                (existing, incoming) -> existing // keep first on conflict
        ));
```

4-arg form also lets you choose the `Map` implementation (e.g., `TreeMap`,
`LinkedHashMap` for insertion order):

```java
Map<String, Integer> sorted = people.stream()
        .collect(Collectors.toMap(Person::name, Person::age,
                (a, b) -> a, TreeMap::new));
```

---

## 11. What is `Collectors.teeing()` (Java 12+)? Give a real use case.

`teeing` applies **two independent collectors** to the same stream and then
merges their results with a `BiFunction`. Useful when you need two separate
aggregates computed in a single pass instead of iterating the stream twice
(or awkwardly combining via a custom collector).

```java
record MinMax(int min, int max) {}

MinMax result = Stream.of(5, 3, 9, 1, 7)
        .collect(Collectors.teeing(
                Collectors.minBy(Integer::compareTo),
                Collectors.maxBy(Integer::compareTo),
                (min, max) -> new MinMax(min.get(), max.get())
        ));
// MinMax[min=1, max=9]
```

Another real case: computing average without `averagingX` by combining
`summingInt` and `counting` in one pass.

---

## 12. How do you write a custom `Collector`? When would you need one?

Needed when none of the built-in `Collectors` fit — e.g., collecting into a
custom immutable data structure, or implementing specialized aggregation
logic that benefits from parallel-safe combining.

`Collector<T, A, R>` requires 5 components: `supplier`, `accumulator`,
`combiner`, `finisher`, and `characteristics`.

```java
Collector<String, StringBuilder, String> customJoiner = Collector.of(
        StringBuilder::new,                           // supplier
        (sb, s) -> sb.append(s).append("|"),          // accumulator
        (sb1, sb2) -> sb1.append(sb2),                 // combiner (for parallel)
        sb -> sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "" // finisher
);

String joined = Stream.of("a", "b", "c").collect(customJoiner);
// "a|b|c"
```

In practice, for simple joining you'd just use `Collectors.joining("|")` —
but this pattern generalizes to any custom mutable-reduction need (e.g.
building a histogram, a trie, a custom immutable collection).

---

## 13. What's the difference between `Stream.of()`, `Stream.generate()`, and `Stream.iterate()`?

- `Stream.of(T...)` — fixed finite stream from given elements.
- `Stream.generate(Supplier<T>)` — **infinite** stream; each element produced
  independently by the supplier (no relation between elements) — must be
  limited with `limit()` or it runs forever.
  ```java
  Stream.generate(Math::random).limit(5).forEach(System.out::println);
  ```
- `Stream.iterate(seed, UnaryOperator<T>)` — infinite stream where each
  element is derived from the previous one (`seed, f(seed), f(f(seed)), ...`).
  ```java
  Stream.iterate(1, n -> n * 2).limit(5).forEach(System.out::println);
  // 1 2 4 8 16
  ```
- Java 9+ added a 3-arg `iterate(seed, hasNext predicate, next function)` that
  acts like a bounded for-loop and doesn't need `limit()`:
  ```java
  Stream.iterate(1, n -> n <= 100, n -> n * 2).forEach(System.out::println);
  // 1 2 4 8 16 32 64
  ```

---

## 14. What are the Stream short-circuiting operations, and why do they matter for performance?

Short-circuiting operations can terminate processing **before examining the
entire stream**:
- `anyMatch`, `allMatch`, `noneMatch` — stop as soon as the result is
  determined.
- `findFirst`, `findAny` — stop once a match is found.
- `limit(n)` — intermediate op that is also short-circuiting; it tells the
  pipeline it only needs the first `n` elements.

Why it matters: on a large or infinite stream, short-circuiting avoids
processing the whole source, which is critical for correctness (infinite
streams) and for performance (e.g., `anyMatch` on a 10-million-element
stream can return after the first match instead of scanning everything).

```java
boolean hasNegative = hugeList.stream().anyMatch(n -> n < 0); // stops early
```

Operations like `sorted()`, `distinct()`, `count()` (on some sources) are
generally **not** short-circuiting since they often need to see the whole
stream — though the JDK does have optimizations (e.g., `count()` can skip
actual traversal if the size is known from the source and no filtering
occurred).

---

## 15. Explain stateless vs stateful intermediate operations and why it matters for parallel streams.

- **Stateless** (`map`, `filter`, `peek`) — the result of processing one
  element doesn't depend on any other element. These parallelize cleanly.
- **Stateful** (`sorted`, `distinct`, `limit`, `skip`) — require knowledge
  of other elements (e.g., `sorted` needs to see everything before it can
  emit anything; `distinct` must track what's already been seen).

Stateful ops can be **much more expensive in parallel streams** because they
may require full buffering/merging across threads, and `limit`/`skip` on an
**ordered** parallel stream can force extra coordination to preserve
encounter order (which hurts parallel speed-up). If you don't need
ordering, calling `.unordered()` before such ops can significantly improve
parallel performance.

---

## 16. How do parallel streams work internally? What is a `Spliterator`?

`stream()` gives a sequential stream; `parallelStream()` (or
`.parallel()`) gives one that, on a terminal op, uses the **Fork/Join
framework** (`ForkJoinPool.commonPool()` by default) to split the work.

The mechanism that makes splitting possible is the **`Spliterator`**
("splittable iterator") — each source (ArrayList, HashSet, etc.) provides
one via `Collection.spliterator()`. A `Spliterator` supports:
- `tryAdvance()` — sequential element access, like `Iterator`.
- `trySplit()` — splits itself into two `Spliterator`s (one returned,
  current one shrinks) so each half can be processed independently/in
  parallel.
- `estimateSize()` and `characteristics()` (ORDERED, SIZED, SUBSIZED,
  DISTINCT, SORTED, NONNULL, IMMUTABLE, CONCURRENT) — the pipeline uses
  these to decide splitting strategy and which optimizations are valid.

Array-backed sources (`ArrayList`, arrays) split very efficiently (cheap
index-based division). `LinkedList` and I/O-based sources split poorly
(have to walk the list to find a split point), making them bad candidates
for parallel streams.

---

## 17. When should you AVOID using parallel streams? (Important senior-level question)

Parallel streams are **not automatically faster** — and in many real
systems, they hurt throughput. Avoid or be careful when:

1. **Small data sets** — the overhead of task splitting/thread
   coordination exceeds the benefit. Rule of thumb from Doug Lea's
   analysis: usually need 10,000+ elements doing non-trivial work per
   element before parallel pays off.
2. **I/O-bound or blocking operations** inside the stream (DB calls, HTTP
   calls, file I/O) — parallel streams use the shared
   `ForkJoinPool.commonPool()`, so blocking in a stream operation **starves
   that pool for the whole JVM**, potentially stalling unrelated code
   (including other libraries' parallel streams, like `CompletableFuture`
   default-executor work).
3. **Stateful lambdas / side effects / shared mutable state** — race
   conditions are easy to introduce (e.g., a lambda that updates a shared
   `ArrayList` or counter without synchronization). Streams don't protect
   you from this.
4. **Sources that don't split well** (`LinkedList`, `Iterator`-based
   streams, `BufferedReader.lines()`) — poor spliterator characteristics
   mean little real parallelism.
5. **Operations needing strict ordering** (`findFirst`, ordered `limit`) —
   parallel gains are reduced because of coordination overhead to preserve
   order.
6. **Already inside a parallel context** (e.g., inside a
   `parallelStream()` already, or inside a web request thread pool that's
   already saturated) — competing for the same common pool causes
   contention, not speed-up.

**Mitigation if you must use it with blocking work:** run it in a custom
`ForkJoinPool` rather than the shared common pool, e.g.:
```java
ForkJoinPool customPool = new ForkJoinPool(4);
customPool.submit(() -> list.parallelStream().map(this::callRemoteService).collect(toList())).get();
```

---

## 18. What happens if you use a non-thread-safe collection inside `forEach` on a parallel stream?

Classic bug:

```java
List<Integer> results = new ArrayList<>();
list.parallelStream().forEach(results::add); // BROKEN — ArrayList is not thread-safe
```

This causes `ArrayIndexOutOfBoundsException`, lost updates, or silent data
corruption because multiple threads call `add()` concurrently, racing on
internal array resizing.

**Fix:** use `collect(Collectors.toList())` (a proper parallel-safe
collector with combiner semantics), or use a thread-safe/concurrent
collector:

```java
List<Integer> results = list.parallelStream()
        .collect(Collectors.toList()); // correct
```

This is a favorite interview "gotcha" question because it tests whether you
really understand *why* `collect()` exists as a separate concept from
`forEach` + manual accumulation.

---

## 19. What is the difference between `forEach()` and `forEachOrdered()`?

- `forEach()` — in a parallel stream, does **not guarantee encounter
  order**; elements may be processed by different threads in any order,
  which is often the whole point (max throughput).
- `forEachOrdered()` — forces processing in encounter order even in a
  parallel stream, but this **sacrifices the parallelism benefit** for that
  operation since it reintroduces coordination overhead.

In a sequential stream, both behave identically (order is always
preserved).

---

## 20. Explain primitive streams (`IntStream`, `LongStream`, `DoubleStream`). Why do they exist?

Generic `Stream<T>` boxes primitives (e.g., `Stream<Integer>`), which has
real overhead: boxing/unboxing cost and extra memory per element (an
`Integer` object vs a raw `int`).

`IntStream`, `LongStream`, `DoubleStream` avoid boxing entirely and provide
specialized terminal operations not on the generic `Stream`: `sum()`,
`average()`, `max()`, `min()`, `summaryStatistics()`.

```java
int total = list.stream()
        .mapToInt(Employee::getAge)   // Stream<Employee> -> IntStream (unboxed)
        .sum();

IntSummaryStatistics stats = list.stream()
        .mapToInt(Employee::getAge)
        .summaryStatistics();
stats.getMax(); stats.getMin(); stats.getAverage(); stats.getCount();
```

Converting back: `.boxed()` turns `IntStream` back into `Stream<Integer>`
when you need generic stream operations like `collect(toList())`.

---

## 21. What is `Optional`, and how does it relate to Streams? What are common misuses?

`Optional<T>` represents a value that may or may not be present — streams
return it from operations like `findFirst`, `max`, `min`, `reduce`
(no-identity overload) to force the caller to explicitly handle the
"empty" case instead of risking a `NullPointerException`.

**Good usage:**
```java
Optional<Employee> highestPaid = employees.stream()
        .max(Comparator.comparingDouble(Employee::salary));

highestPaid.ifPresentOrElse(
        e -> System.out.println("Highest paid: " + e.name()),
        () -> System.out.println("No employees")
);

String name = highestPaid.map(Employee::name).orElse("N/A");
```

**Common misuses interviewers probe for:**
- Calling `.get()` without checking `.isPresent()` — defeats the entire
  purpose; it just moves the NPE risk to an `NoSuchElementException`.
- Using `Optional` as a field type or method parameter — it's designed as a
  **return type** for "might not have a result," not for general null
  handling in domain models (adds serialization overhead, unclear intent).
- Wrapping collections in `Optional` (`Optional<List<T>>`) — just return an
  empty list instead; empty collection is already a well-defined "no data"
  signal.
- Using `Optional.of(value)` when `value` could be null — throws NPE
  immediately; should use `Optional.ofNullable(value)` instead.

---

## 22. What is `peek()` actually for, and why is it dangerous to misuse?

`peek()` is designed **purely for debugging** — to observe elements flowing
through the pipeline without modifying them, typically for logging.

**Danger:** `peek()` is an intermediate, lazy operation — the JDK is
explicitly allowed to **skip calling it** if the implementation can
determine the result without it (e.g., if a downstream short-circuiting
operation like `findFirst` already has its answer, or in some JIT/stream
fusion optimizations). It should **never be used for side effects that
matter to program correctness** (e.g., mutating external state that the
program depends on, or as a substitute for `forEach`).

```java
// BAD — using peek for logic, not just observation
list.stream().peek(x -> counter.incrementAndGet()).collect(toList());

// GOOD — observational debugging only
list.stream()
    .peek(x -> log.debug("processing {}", x))
    .filter(...)
    .collect(toList());
```

---

## 23. Why can't you use a checked exception inside a lambda passed to a Stream operation? How do you handle it?

Functional interfaces used by Streams (`Function`, `Predicate`, `Consumer`,
etc.) don't declare any checked exceptions in their method signatures, so a
lambda body that calls a method throwing a checked exception (like
`IOException`) won't compile unless handled.

**Common approaches:**

1. **Wrap in try/catch and rethrow as unchecked:**
```java
List<String> lines = paths.stream()
        .map(path -> {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        })
        .collect(Collectors.toList());
```

2. **Write a small helper that converts a checked-exception-throwing
   functional interface into a standard one:**
```java
static <T, R> Function<T, R> wrap(CheckedFunction<T, R> f) {
    return t -> {
        try { return f.apply(t); }
        catch (Exception e) { throw new RuntimeException(e); }
    };
}
```

3. Libraries like `ThrowingFunction` (from Project Lombok or similar
   utility libraries) do this generically.

This is a good one to mention proactively — shows real production
experience, not just textbook knowledge.

---

## 24. What's the difference between `Comparator.comparing()`, `thenComparing()`, and sorting streams by multiple fields?

```java
record Employee(String dept, String name, double salary) {}

List<Employee> sorted = employees.stream()
        .sorted(Comparator.comparing(Employee::dept)
                .thenComparing(Employee::name)
                .thenComparing(Employee::salary, Comparator.reverseOrder()))
        .collect(Collectors.toList());
```

- `Comparator.comparing(keyExtractor)` — builds a comparator from a key.
- `thenComparing(...)` — tie-breaker, applied only when the previous
  comparator considers two elements equal.
- `reverseOrder()` / `.reversed()` — flips direction for descending sort.
- `Comparator.nullsFirst/nullsLast` — handle null-safe sorting when the key
  extractor can return null.

---

## 25. How would you remove duplicates from a stream of custom objects based on a specific field (not full object equality)?

`distinct()` uses the object's `equals()`/`hashCode()`, which often isn't
what you want for a POJO without proper equals override, or when you want
"distinct by one field."

**Common technique** — a stateful filter using a `ConcurrentHashMap` (or
`HashSet`) as a "seen" tracker:

```java
static <T, K> Predicate<T> distinctByKey(Function<T, K> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
}

List<Employee> distinctByDept = employees.stream()
        .filter(distinctByKey(Employee::dept))
        .collect(Collectors.toList());
```

**Caution:** this filter is **stateful** (keeps mutable `seen` state across
calls) — safe-ish for sequential streams, but risky/undefined with
unordered parallel streams unless you reason carefully about thread safety
(hence `ConcurrentHashMap.newKeySet()` rather than a plain `HashSet`).
Mention this nuance — it signals depth.

---

## 26. What is the difference between `Stream.concat()` behavior pitfalls, and why should you be careful combining many streams?

```java
Stream<Integer> combined = Stream.concat(stream1, stream2);
```

**Pitfall:** repeatedly `concat`-ing many streams (e.g., in a loop building
up from dozens/hundreds of sub-streams) can lead to **deeply nested stream
pipelines** causing `StackOverflowError` at traversal time, because each
`concat` wraps spliterators recursively. For combining many sources,
prefer `flatMap` over a collection of sources, or collect each into a list
first and concatenate the lists.

```java
// risky for large N
Stream<T> result = sources.stream().reduce(Stream.empty(), Stream::concat);

// safer
Stream<T> result = sources.stream().flatMap(Function.identity());
```

---

## 27. Can you reuse a Stream? What happens if you try, and how do you work around it?

No — once a terminal operation runs, the stream is considered
**consumed/closed**. Reusing it throws:
```
java.lang.IllegalStateException: stream has already been operated upon or closed
```

**Workaround:** don't store the `Stream` itself; store the **source**
(Collection, Supplier of a stream) and create a fresh stream each time you
need to traverse it:

```java
Supplier<Stream<String>> streamSupplier = () -> names.stream();
long count = streamSupplier.get().count();
List<String> upper = streamSupplier.get().map(String::toUpperCase).collect(toList());
```

---

## 28. How do you convert a Stream to an array, and what's the gotcha with generic types?

```java
String[] arr = names.stream().toArray(String[]::new); // need the generator for correct runtime type
Object[] arr2 = names.stream().toArray(); // works but loses specific type info (Object[])
```

For primitives: `IntStream.toArray()` returns `int[]` directly (no boxing
needed since it's already a primitive stream).

The gotcha: `toArray()` with no args returns `Object[]` due to type erasure
— you can't get a properly-typed generic array without supplying the array
constructor reference.

---

## 29. Explain how `Stream` + `Files.lines()` is used for processing large files, and what to watch for.

```java
try (Stream<String> lines = Files.lines(Path.of("big.log"))) {
    long errorCount = lines.filter(l -> l.contains("ERROR")).count();
}
```

- `Files.lines()` returns a **lazily-read stream backed by a file handle** —
  it must be closed (hence try-with-resources) or you leak file descriptors.
- Because reading is I/O-bound and sequential by nature (and a
  `BufferedReader`-backed spliterator doesn't split well), **parallelizing
  this stream rarely helps** and can even hurt due to I/O contention.
- If an exception occurs mid-stream (e.g., malformed line causes a
  processing exception), the try-with-resources still ensures the
  underlying file is closed.

---

## 30. What are the main performance pitfalls of streams compared to classic loops, and when would you NOT use streams at all?

Senior-level "judgment" question — show you don't treat streams as
always-superior:

1. **Autoboxing overhead** — using `Stream<Integer>` instead of `IntStream`
   for numeric work adds boxing/unboxing cost; can matter in hot loops.
2. **Lambda/method-reference overhead** — slightly higher than a raw loop
   due to functional interface dispatch (usually negligible, but matters
   in extremely hot paths — profile before assuming).
3. **Harder debugging** — stack traces through stream pipelines are noisier;
   breakpoints inside lambdas are less ergonomic than in plain loops.
4. **Readability trade-off** — deeply chained stream pipelines with
   multiple `flatMap`/`collect` nesting can become *less* readable than an
   imperative loop with early-exit logic (e.g., multiple break/continue
   conditions, especially when mixing index-based access or needing to
   mutate multiple collections).
5. **Short-circuit-sensitive logic mixed with side effects** can create
   subtle, hard-to-trace bugs since you can't easily reason about which
   elements were actually touched (back to the `peek` caveat).
6. **Need for index while iterating** — Streams (over `Collection`s) don't
   give you an element index naturally; you'd resort to `IntStream.range()`
   workarounds that are often clunkier than just using a plain `for` loop.

**Rule of thumb:** use streams when expressing a transformation/aggregation
pipeline (filter/map/group/reduce) makes the *intent* clearer, and fall
back to a plain loop when you need fine-grained control flow, index access,
multiple simultaneous accumulations, or you're in a verified hot path where
profiling shows streams cost measurably more.

---

## Quick-fire Rapid Recall Table

| Concept | One-liner |
|---|---|
| Stream | Lazy pipeline over a data source, not a data structure |
| Intermediate op | Lazy, returns a Stream (map, filter, sorted...) |
| Terminal op | Triggers execution, consumes the stream (collect, forEach, reduce...) |
| map vs flatMap | 1-to-1 vs 1-to-many + flatten |
| reduce vs collect | Immutable fold vs mutable reduction |
| groupingBy vs partitioningBy | N groups by key vs exactly 2 groups by predicate |
| findFirst vs findAny | Ordered, costs more in parallel vs unordered, cheaper in parallel |
| Spliterator | Enables splitting a source for parallel processing |
| peek() | Debug-only; JVM may skip it — never use for required side effects |
| Parallel streams | Not always faster; avoid with blocking I/O, small data, poor-split sources |
| Optional | Return type for "may be absent"; don't overuse as field/param type |
| Stream reuse | Not allowed — IllegalStateException after terminal op |

---

## Suggested Practice Before the Interview

1. Be ready to **write code live** for: group employees by department and
   get average salary; find second-highest salary using streams; word
   frequency count from a sentence using `flatMap` + `groupingBy` +
   `counting`.
2. Be ready to **explain trade-offs out loud**, not just syntax — senior
   interviews weight judgment (parallel streams, readability, performance)
   heavily over API trivia.
3. Have a real **production anecdote** ready: a time a stream pipeline
   caused a bug (e.g., parallel stream + shared mutable state, or a
   `peek()` misuse) — this signals genuine hands-on depth for a 16-year
   profile.

---

# Core Java, Concurrency & Data Structures

> Follow-on set — connection pooling, `HashMap` internals, LRU cache design,
> and thread pools. These are classic "explain the internals + design it
> yourself" questions used to probe depth beyond API usage.

---

## 31. What is the String pool in Java?

The **String pool** (a.k.a. the **String intern pool** or **String
constant pool**) is a special memory region the JVM uses to **cache a
single shared copy of each distinct `String` literal/interned value**, so
that identical strings aren't duplicated in memory every time they appear.

**Where it lives:** before Java 7, it lived in **PermGen**. From **Java
7 onward it was moved into the regular heap**, which matters because it
means string-pool contents are now subject to normal garbage collection
(unreferenced interned strings can be collected) rather than living in the
fixed-size PermGen space that used to cause `OutOfMemoryError: PermGen
space` from excessive interning.

**How it gets populated:**
- Any **string literal** in source code (`"hello"`) is automatically
  placed in the pool at class-load time.
- Compile-time constant expressions (`"hel" + "lo"`, both literals) are
  folded by the compiler into a single literal and pooled too.
- Calling `.intern()` explicitly on **any** `String` (including one built
  at runtime) adds it to the pool if an equal string isn't already there,
  and returns the pooled reference.

**The classic `==` vs `.equals()` interview trap:**

```java
String a = "hello";              // literal -> goes into the pool
String b = "hello";              // same literal -> JVM reuses the SAME pooled object
System.out.println(a == b);      // true — both references point to the one pooled instance

String c = new String("hello");  // explicitly allocates a NEW object on the heap, outside the pool
System.out.println(a == c);      // false — different object references
System.out.println(a.equals(c)); // true — same character content

String d = c.intern();           // looks up "hello" in the pool, returns the pooled reference
System.out.println(a == d);      // true — now pointing at the same pooled object
```

**Why it exists — the core benefit:** Strings are **immutable** in Java,
which is exactly what makes sharing them safely possible — since a pooled
`String` can never be mutated out from under another reference holding the
same object, many references can safely point to one copy. Given how
heavily string literals are reused across a typical codebase (log
messages, keys, config names, SQL fragments, JSON field names), pooling
meaningfully cuts memory footprint and avoids redundant allocation.

**Practical implications worth mentioning:**
- Never rely on `==` to compare string *content* — always use `.equals()`
  (or `.equalsIgnoreCase()`). `==` on strings only reliably works for
  literals/interned values, and that's an implementation detail you
  shouldn't lean on in application logic.
- Calling `.intern()` indiscriminately on many **unique**, large,
  runtime-generated strings (e.g., interning every row of unique IDs read
  from a DB) can backfire — it grows the pool itself and defeats GC of
  those strings for as long as the pool (and thus the class loader that
  effectively anchors it) is alive, trading one memory problem for
  another. Only intern when you *know* there's real duplication to
  collapse.
- `Integer`, `Long`, etc. have an analogous (much smaller) **cache** for
  autoboxing small values (`Integer` caches -128 to 127 via
  `Integer.valueOf()`), which produces the same `==` surprise
  (`Integer.valueOf(100) == Integer.valueOf(100)` is `true`, but
  `Integer.valueOf(200) == Integer.valueOf(200)` is `false`) — a good
  related gotcha to bring up proactively since it shows the same
  "immutable value + caching" pattern recurring elsewhere in the JDK.

---

## 32. What is a connection pool in Java, and how would you design your own?

A **connection pool** is a different concept from the String pool above —
it's a cache of reusable, pre-established **resource connections**, most
commonly **database connections** (JDBC), though the same pattern applies
to HTTP connections, thread pools, etc. Instead of opening a brand-new
`java.sql.Connection` for every request (expensive — TCP handshake, DB
auth, session setup, often tens of milliseconds), the application
**borrows** a connection from a pre-created pool, uses it, and **returns**
it when done, so it can be reused by the next request.

**Why it exists — the core problem it solves:**
- Creating/destroying a DB connection is costly (network round trip +
  authentication + resource allocation on the DB server).
- Under load, naive "open a connection per request" quickly exhausts DB-side
  connection limits and kills throughput/latency.
- Pooling amortizes that setup cost across many logical requests and caps
  how many concurrent connections hit the DB, protecting it from overload.

**Key configuration knobs** (as seen in HikariCP, Apache Commons DBCP,
c3p0 — the real-world pools you'd mention by name in an interview):
- `minimumIdle` / `maximumPoolSize` — floor/ceiling on pooled connections.
- `connectionTimeout` — how long a caller waits for a free connection
  before failing.
- `idleTimeout` / `maxLifetime` — recycle connections periodically (avoids
  stale connections, DB-side session timeouts, memory leaks in drivers).
- `validationQuery` / `connectionTestQuery` (or JDBC4 `isValid()`) — health
  check before handing out a "borrowed" connection, in case it went stale
  (e.g., DB restarted, network blip).
- Leak detection threshold — logs a warning if a connection is checked out
  for longer than expected (usually means code forgot to close/return it).

```java
// Typical usage with a pooled DataSource (HikariCP)
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
config.setUsername("user");
config.setPassword("pass");
config.setMaximumPoolSize(20);
config.setConnectionTimeout(30_000);

HikariDataSource dataSource = new HikariDataSource(config);

try (Connection conn = dataSource.getConnection()) {   // borrow
    // use conn
}                                                        // auto-returned to pool on close()
```

Calling `conn.close()` on a pooled connection does **not** actually close
the physical socket — the pool's `Connection` wrapper intercepts `close()`
and returns the connection to the pool instead.

### Designing it yourself

Interviewers want to see you reason about: **thread safety**, **bounded
size**, **borrow/return semantics**, **timeouts**, and **validation** — not
necessarily a production-grade library. A `BlockingQueue` is the natural
backbone since it already gives you thread-safe, blocking producer/consumer
semantics.

```java
public class SimpleConnectionPool implements AutoCloseable {

    private final BlockingQueue<Connection> pool;
    private final String url, user, password;
    private final int maxSize;

    public SimpleConnectionPool(String url, String user, String password, int size) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = size;
        this.pool = new LinkedBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.offer(createNewConnection());
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Borrow a connection, waiting up to timeoutMs if none are free. */
    public Connection borrow(long timeoutMs) throws InterruptedException, SQLException {
        Connection conn = pool.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (conn == null) {
            throw new RuntimeException("Timed out waiting for a connection");
        }
        if (!isValid(conn)) {               // health check before handing out
            conn = createNewConnection();   // replace a dead connection
        }
        return conn;
    }

    /** Return a connection to the pool for reuse. */
    public void release(Connection conn) {
        if (conn != null) {
            pool.offer(conn);               // never blocks since pool size is bounded by design
        }
    }

    private boolean isValid(Connection conn) {
        try {
            return conn.isValid(2); // 2-second validation timeout
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void close() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
    }
}
```

**Design points worth saying out loud in the interview:**
- `BlockingQueue` gives bounded capacity + thread-safe hand-off for free —
  no need to hand-roll wait/notify.
- **Borrow with timeout** prevents a caller from hanging forever if the
  pool is exhausted (surfacing backpressure instead of silent deadlock).
- **Validate before handing out** — a connection that's been idle could be
  dead (DB restarted, firewall dropped it); replace it transparently.
- **Never create connections unbounded** — size is fixed at construction;
  if all are checked out, callers wait rather than the DB getting swarmed.
- A production version would also add: idle-timeout eviction (a background
  thread recycling long-idle connections), leak detection (track borrow
  time, warn if held too long), and wrapping the returned `Connection` in a
  proxy so an accidental `conn.close()` call from caller code returns it to
  the pool instead of actually closing the socket (this is what
  HikariCP/DBCP do).

---

## 33. How does `HashMap` work in Java?

Internally, `HashMap<K,V>` is backed by an **array of buckets**
(`Node<K,V>[] table`), where each bucket holds a linked list (or, since
Java 8, a balanced tree for heavily-collided buckets) of entries that
hashed to that index.

**`put(key, value)` step by step:**
1. Compute `hash(key)` — `HashMap` applies a **supplemental hash spreading
   function**: `h = key.hashCode(); h ^= (h >>> 16);` — XORing the high
   bits into the low bits so that even if `hashCode()` only varies in high
   bits, bucket distribution (which uses low bits) stays good.
2. Compute the bucket index: `index = hash & (table.length - 1)` — this
   only works as a correct modulo because **table length is always a power
   of two** (bitwise AND with `length-1` is equivalent to `% length` and
   much faster).
3. If the bucket is empty, insert a new `Node` there.
4. If not empty (collision), walk the bucket's list/tree:
   - If a `Node` with an **equal key** (`hash` matches **and**
     `key.equals()` returns true) is found, its value is **overwritten**.
   - Otherwise append a new `Node` to the end of the list (or insert into
     the tree if the bucket has been "treeified").
5. If a bucket's chain grows to **`TREEIFY_THRESHOLD` (8)** entries *and*
   the table has at least 64 buckets, that bucket is converted from a
   linked list into a **red-black tree** — turns worst-case lookup in a
   heavily-collided bucket from O(n) into O(log n). (Below 64 buckets, it
   resizes instead of treeifying.)
6. After insertion, if `size > capacity * loadFactor` (default load factor
   **0.75**), the table **resizes** — doubles capacity and **rehashes**
   every entry into the new, larger table (an O(n) operation, which is why
   you should size a `HashMap` up front with the constructor if you know
   roughly how many entries it'll hold, to avoid repeated resizes).

**`get(key)`:** same hash → bucket-index computation, then walks the
bucket's chain/tree comparing `hash` first (cheap int comparison) and only
calling `equals()` when hashes match, returning the value on match or
`null` if not found.

**Why both `hashCode()` and `equals()` matter:** the map uses `hashCode()`
to find the *candidate* bucket quickly, then `equals()` to confirm an exact
match within that bucket. Overriding one without the other breaks the
contract — e.g., two "equal" objects with different `hashCode()`s may land
in different buckets and the map will never find each other's entry.

**Key design details worth citing:**
- Initial default capacity: **16**; default load factor: **0.75**.
- Capacity is always kept a **power of two** specifically so `hash &
  (capacity-1)` works as a fast modulo and so the resize-rehashing trick
  (splitting each old bucket into exactly two new buckets without
  recomputing every hash from scratch) is efficient.
- `HashMap` permits **one null key** and multiple null values; `Hashtable`
  permits neither.
- Not thread-safe — concurrent structural modification (e.g., two threads
  both triggering a resize) can corrupt the internal linked structure or
  even cause an infinite loop in pre-Java-8 implementations. Use
  `ConcurrentHashMap` for concurrent access (it uses finer-grained locking
  per bin plus CAS operations rather than one global lock, unlike the
  old `Hashtable`/`Collections.synchronizedMap`, which just synchronizes
  every method on one lock).

```java
Map<String, Integer> map = new HashMap<>();
map.put("apple", 1);   // hash("apple") -> spread -> bucket index -> insert Node
map.put("apple", 2);   // same key -> equals() matches -> value overwritten, not duplicated
Integer v = map.get("apple"); // same hash/index lookup -> 2
```

---

## 34. Design an LRU (Least Recently Used) cache.

**Requirement:** fixed-capacity cache supporting `get(key)` and
`put(key, value)` in **O(1)** time, evicting the **least recently used**
entry when capacity is exceeded, and any `get`/`put` on a key marks it as
"recently used."

**Approach 1 — the one-liner using `LinkedHashMap` (great to mention first,
since it shows you know the JDK, before writing the "from scratch" version):**

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        // initialCapacity, loadFactor, accessOrder=true -> reorders on access, not just insertion
        super(16, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity; // tells LinkedHashMap to evict the LRU entry automatically
    }
}

LRUCache<Integer, String> cache = new LRUCache<>(3);
cache.put(1, "a"); cache.put(2, "b"); cache.put(3, "c");
cache.get(1);              // marks 1 as recently used
cache.put(4, "d");         // evicts 2 (least recently used), not 1
```

`LinkedHashMap` with `accessOrder=true` already maintains a **doubly
linked list across entries in access order** internally — which is exactly
the data structure an LRU cache needs — so `removeEldestEntry` is all you
need to hook eviction.

**Approach 2 — "design it from scratch" (what interviewers usually actually
want to see, to test your data-structure reasoning):** combine a `HashMap`
for O(1) key lookup with a **doubly linked list** for O(1) reordering
(move-to-front on access) and O(1) eviction from the tail.

```java
class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0); // dummy head (most recently used side)
    private final Node tail = new Node(0, 0); // dummy tail (least recently used side)

    static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;      // least recently used = right before dummy tail
            remove(lru);
            map.remove(lru.key);
        }
        Node fresh = new Node(key, value);
        map.put(key, fresh);
        addToFront(fresh);
    }

    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}
```

**Why each piece is there (say this explicitly — it's the point of the
question):**
- `HashMap<K, Node>` → **O(1)** lookup of a node by key (instead of
  scanning the list).
- **Doubly** linked list (not singly) → O(1) removal of an arbitrary node
  without traversal, since each node already knows its `prev`.
- **Dummy head/tail sentinels** → removes edge-case branching for
  empty-list / single-node removal/insertion (no null-checks needed at the
  boundaries).
- Eviction = remove `tail.prev` (whatever sits right before the dummy
  tail) — O(1), no scanning needed to find "the LRU one."

This is also a good moment to mention the real-world analog: **Caffeine**
and Guava's `CacheBuilder` implement far more sophisticated versions of
this (with size-based + time-based eviction, weak/soft references,
async refresh) — worth name-dropping to show awareness beyond the textbook
exercise.

---

## 35. How does a thread pool work in Java?

A thread pool (typically `java.util.concurrent.ThreadPoolExecutor`, usually
obtained via `Executors.newFixedThreadPool()` etc., or `ExecutorService` in
general) maintains a **set of reusable worker threads** that pull tasks
from a **work queue** and execute them, instead of spawning a brand-new
`Thread` per task (thread creation/teardown is expensive — OS-level
resource allocation, stack memory — and unbounded thread creation under
load can crash the JVM/OS).

**`ThreadPoolExecutor`'s constructor parameters — the heart of the
question:**
```java
new ThreadPoolExecutor(
    corePoolSize,      // threads kept alive even when idle
    maximumPoolSize,    // hard ceiling on total threads
    keepAliveTime, unit, // how long idle threads beyond corePoolSize survive before dying
    workQueue,           // BlockingQueue holding tasks waiting for a free thread
    threadFactory,       // controls thread creation (naming, daemon flag, priority)
    rejectedExecutionHandler // policy when both pool and queue are "full"
);
```

**The actual task-submission algorithm (the classic interview "gotcha" —
most people get the order wrong):**
1. If **fewer than `corePoolSize`** threads exist, **start a new thread**
   for the task immediately — even if other core threads are idle.
   (Counter-intuitive: it prefers spinning up a new core thread over
   reusing an idle one, until core size is reached.)
2. Once `corePoolSize` threads all exist, **new tasks go into the work
   queue** instead of spawning more threads — as long as the queue has
   capacity.
3. Only when the **queue is full** does the pool create **additional
   threads beyond `corePoolSize`**, up to `maximumPoolSize`.
4. If the queue is full **and** the pool is already at `maximumPoolSize`,
   the task is handed to the **`RejectedExecutionHandler`** — default
   policy (`AbortPolicy`) throws `RejectedExecutionException`; other
   built-ins: `CallerRunsPolicy` (runs the task on the *submitting*
   thread — a natural backpressure mechanism), `DiscardPolicy`,
   `DiscardOldestPolicy`.
5. Threads beyond `corePoolSize` that sit idle for longer than
   `keepAliveTime` are terminated and removed, shrinking the pool back
   toward `corePoolSize`.

**This means:** with an **unbounded queue** (like the default
`LinkedBlockingQueue` in `Executors.newFixedThreadPool()`), the pool will
**never grow past `corePoolSize`**, because step 2 never "fails" — tasks
just keep queuing — so `maximumPoolSize` is effectively irrelevant in that
configuration. This is exactly why `newFixedThreadPool()`'s core and max
size are set equal by the JDK's factory method.

**Common `Executors` factory presets (and what's actually happening
underneath each):**
- `newFixedThreadPool(n)` — core = max = n, unbounded `LinkedBlockingQueue`.
- `newCachedThreadPool()` — core = 0, max = `Integer.MAX_VALUE`,
  `SynchronousQueue` (hands off directly, no queueing) — creates a thread
  per task if none idle, reuses idle threads otherwise, kills idle ones
  after 60s. Dangerous under sustained load — effectively unbounded thread
  creation.
- `newSingleThreadExecutor()` — core = max = 1 — serial execution, but
  tasks are still queued/async relative to the caller.
- `newScheduledThreadPool(n)` — for delayed/periodic task execution.
- (Since Java 19) `Executors.newVirtualThreadPerTaskExecutor()` — not a
  pool at all in the traditional sense; spins up a **virtual thread**
  (Project Loom) per task — worth mentioning if discussing modern
  alternatives to pooling for I/O-bound workloads.

---

## 36. What are the drawbacks of using a thread pool in Java?

Senior-level "judgment" question again — show you've been burned by these
in production, not just that you can define a thread pool.

1. **Thread starvation / deadlock from dependent tasks.** If a task
   submitted to the pool itself submits another task to the *same* pool
   and blocks waiting for its result (`Future.get()`), and the pool is
   already saturated with such waiting tasks, you can deadlock — all
   threads are blocked waiting on tasks that have no free thread to run
   on. Classic fix: use separate pools for dependent stages, or ensure
   tasks never block waiting on same-pool sub-tasks.
2. **Unbounded queue → `OutOfMemoryError`.** `newFixedThreadPool`'s
   default unbounded `LinkedBlockingQueue` means if producers submit work
   faster than the pool can drain it, the queue grows without bound and
   can eventually exhaust heap memory — with no backpressure signal to the
   caller until it's too late. Prefer a **bounded** queue + a sensible
   `RejectedExecutionHandler` (e.g., `CallerRunsPolicy`) so the system
   degrades gracefully under overload instead of OOMing.
3. **Sizing is hard and workload-dependent.** Too few threads underutilize
   CPU for CPU-bound work; too many threads cause excessive context
   switching and cache thrashing. For **I/O-bound** tasks, you typically
   want far more threads than CPU cores (since threads spend most time
   blocked waiting on I/O, not computing); for **CPU-bound** tasks, pool
   size near `Runtime.availableProcessors()` is usually right. Getting
   this wrong is one of the most common real-world performance bugs.
4. **`ThreadLocal` leaks.** Because worker threads are **reused** across
   many tasks (unlike a fresh `Thread` per task, which dies and releases
   everything), any `ThreadLocal` value set by one task but not cleaned up
   silently leaks into the *next* task that happens to run on that same
   worker thread — a subtle source of stale/incorrect state and memory
   leaks, especially nasty in app-server/framework code (e.g., security
   context or MDC/logging-context leaking between unrelated requests).
5. **Silent task failure.** If a `Runnable` submitted via `execute()`
   throws an uncaught exception, it's typically swallowed (logged by the
   default `UncaughtExceptionHandler`, or in some pool configs just lost)
   and the **thread is replaced silently** — easy to miss in production
   without deliberate monitoring/alerting. (Submitting via `submit()`
   instead captures the exception in the returned `Future`, but only if
   someone actually calls `.get()` on it and handles the
   `ExecutionException`.)
6. **Forgetting to shut the pool down.** An `ExecutorService` that's never
   `shutdown()`/`shutdownNow()` keeps its threads alive indefinitely (they
   aren't daemon threads by default), which can **prevent JVM shutdown**
   entirely in a standalone app, or leak thread resources over the
   lifetime of a long-running service that keeps creating pools without
   closing old ones.
7. **Context-switching overhead and cache locality loss** when the pool is
   oversized relative to available cores — more threads than useful
   parallelism just adds scheduler overhead without improving throughput.
8. **Poison-pill / long-running task monopolization.** A single
   misbehaving task that runs far longer than expected (e.g., an
   accidental infinite loop, or a blocking call with no timeout) ties up
   one worker thread indefinitely; if that happens repeatedly, it can
   slowly starve the pool of available threads even though
   `maximumPoolSize` is never technically "full" in the traditional
   rejection sense — tasks just queue forever behind the stuck ones.

**Mitigations worth mentioning as a set:** bounded queues with explicit
rejection policy, dedicated pools per workload type (don't share one pool
across unrelated concerns), monitoring (`getActiveCount()`,
`getQueue().size()`, custom metrics), timeouts on blocking operations
inside tasks, and always pairing `ExecutorService` creation with a
guaranteed `shutdown()` (try/finally or a managed lifecycle/DI container).

---

## 37. Design a custom String pool in Java. What design choices matter?

This is the natural follow-up to Q31 — it tests whether you understand the
built-in pool's guarantees (content-equality → identity, thread safety,
GC-ability) well enough to **rebuild them deliberately** and reason about
knobs the JVM-global pool doesn't give you: scoping, bounded memory, and
instrumentation.

**What any String pool must guarantee:**
1. **Content equality → identity** — interning two equal-content strings
   returns the *same* object reference.
2. **Thread safety** — concurrent interning must never leave two different
   "canonical" instances alive for the same content.
3. **No uncontrolled leak** — shouldn't keep every ever-interned string
   alive forever by default (the exact criticism leveled at indiscriminate
   `.intern()` use in Q31).

### Approach 1 — minimal correct version (say this first)

The core trick is a map used as a **set-with-lookup**: key and value are
the same object, so a lookup returns the canonical instance.

```java
public final class StringPool {
    private final ConcurrentHashMap<String, String> pool = new ConcurrentHashMap<>();

    public String intern(String s) {
        return pool.computeIfAbsent(s, key -> key);
    }

    public int size() {
        return pool.size();
    }
}
```

**Why `computeIfAbsent` and not `get` + `put`:** a plain
"if absent, then put" is a classic **check-then-act race** — two threads
can both see "not present" and each insert a *different* object for equal
content, breaking the identity guarantee. `computeIfAbsent` is atomic per
key on `ConcurrentHashMap`, so only one thread's string ever "wins" and
becomes canonical; the other thread transparently gets that same instance
back.

This is functionally what `String.intern()` itself does conceptually —
just scoped to an instance **you** own instead of the JVM-wide pool. Fine
when the vocabulary is small and known in advance (protocol keywords,
enum-like tokens). **Drawback:** entries live as long as the pool instance
does — feed it unbounded/unique runtime strings (user input, generated
IDs) and it becomes an unbounded memory leak, same failure mode called out
against careless `.intern()` in Q31.

### Approach 2 — weak-reference based, to avoid the leak

Mirrors what the JVM pool gained in Java 7: pooled entries should be
**collectible** once nothing outside the pool references them anymore.

```java
public final class WeakStringPool {
    private final ConcurrentHashMap<String, KeyedWeakReference> pool = new ConcurrentHashMap<>();
    private final ReferenceQueue<String> staleRefs = new ReferenceQueue<>();

    public String intern(String s) {
        Objects.requireNonNull(s);
        expungeStaleEntries();

        KeyedWeakReference existingRef = pool.get(s);
        String existing = (existingRef != null) ? existingRef.get() : null;
        if (existing != null) {
            return existing;
        }

        KeyedWeakReference ref = pool.computeIfAbsent(s,
                key -> new KeyedWeakReference(key, staleRefs));
        String canonical = ref.get();
        if (canonical == null) {
            // Reference died between computeIfAbsent and get() — retry.
            pool.remove(s, ref);
            return intern(s);
        }
        return canonical;
    }

    private void expungeStaleEntries() {
        Reference<? extends String> ref;
        while ((ref = staleRefs.poll()) != null) {
            KeyedWeakReference keyed = (KeyedWeakReference) ref;
            pool.remove(keyed.key, keyed);
        }
    }

    public int size() {
        return pool.size();
    }

    private static final class KeyedWeakReference extends WeakReference<String> {
        final String key;
        KeyedWeakReference(String key, ReferenceQueue<String> queue) {
            super(key, queue);
            this.key = key;
        }
    }
}
```

**Why not just a plain `WeakHashMap<String, String>`?** `WeakHashMap`
makes the *key* weak, but the *value* you store is the same object as the
key — the map itself ends up holding a strong reference back to the exact
instance it was supposed to let go of, so the entry can never actually
become collectible. This is a well-known interner gotcha: you need the
**value** to be weak (via an explicit `WeakReference`), not the key. This
is also exactly how Guava's `Interners.newWeakInterner()` is implemented
under the hood — worth name-dropping.

**Why the `ReferenceQueue` matters:** once a `WeakReference`'s referent is
collected, the map entry itself doesn't disappear automatically — it
becomes a dead entry (reference cleared, key string still sitting in the
map). The `ReferenceQueue` lets the pool proactively remove those dead
entries (polled cheaply on each `intern()` call here, or on a background
thread for low-traffic pools) instead of silently accumulating garbage
entries forever.

### Approach 3 — bounded LRU variant

When you want a **hard memory cap** instead of depending on GC timing —
e.g. interning high-cardinality but mostly-repeating values under load
(HTTP header values, log tag names) where predictable footprint matters
more than perfect reclamation — reuse the LRU pattern from Q34:
`LinkedHashMap` with `accessOrder=true` plus `removeEldestEntry`, sized to
a fixed capacity.

**Key difference from a normal LRU *cache*, worth saying explicitly:**
evicting an entry from a *pool* is always safe — worst case, you lose the
sharing optimization and create one duplicate object next time that value
reappears. Evicting from an LRU *cache* (Q34) loses data that may be
expensive to recompute. A String pool can tolerate a much more aggressive
eviction policy than a cache can, because "wrong" eviction only costs a
missed optimization, never correctness.

### Design choices to narrate out loud

- **Content-based keys** — rely on `String`'s own `equals()`/`hashCode()`;
  no custom hashing needed, unlike a general-purpose object pool.
- **`computeIfAbsent` for atomicity** — the single most important line;
  without it the whole identity guarantee is a race condition.
- **Weak vs. strong vs. bounded** is the real design decision: GC-driven
  cleanup (don't fight the JVM, same philosophy as the real pool post-Java
  7) vs. a deterministic cap (predictable footprint, needs tuning). Pick
  based on whether input cardinality is bounded/known.
- **Scoping** — unlike the JVM-wide intern pool, a custom pool instance can
  be created per-parser, per-request, or per-session and **discarded
  wholesale** when that scope ends, letting GC reclaim everything at once.
  This is the main reason to build your own instead of just calling
  `.intern()` — you get a lifecycle you control.
- **Instrumentation** — expose `size()`/hit-rate metrics; the built-in pool
  gives you none of that visibility.

### When you'd actually reach for this in real code

Parsers and decoders that see the same token content repeatedly across
thousands of parsed objects — JSON field names, XML tag/attribute names,
HTTP header names, CSV column headers. Interning collapses all repeats to
one shared instance for the duration of a parse session, and then the
**entire pool** is discarded together when that session ends — something
you can't do with the global, JVM-lifetime `.intern()` pool.
