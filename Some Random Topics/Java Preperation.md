# Java Interview Preparation

## 1. Streams in java?

## 2. Suppose there are 20 threads. 1 thread has executed its task and sitting idle. How the system will know that this thread is idle?

Here's how to check a Java thread pool in VisualVM:

1. Start your Java application, then open VisualVM. Under Applications → Local, find your process and double-click it. If it doesn't appear, make sure VisualVM can access the JVM running your app; applications running as a service or under another user may need extra setup.
2. Open the Threads tab. You'll see a timeline of thread activity and a list of threads. Find your worker by its name—pools often name workers like `pool-1-thread-1`. The thread names come from your application or pool configuration.
3. Wait until the task has finished. If the worker is waiting for another task, its status may show as WAITING or TIMED_WAITING. Select it to inspect its activity and details.
4. Click **Thread Dump** to capture a snapshot of all threads and their stack traces. Look for your worker's name and state:
   - A stack containing `LinkedBlockingQueue.take()` often means the pool worker is waiting for a task.
   - `LockSupport.park()` often means it is parked while waiting.
   - `BLOCKED` usually means it is waiting to acquire a lock.
   - A thread in `RUNNABLE` may be doing work, but that state alone doesn't guarantee it is using CPU.
5. Take another dump a few seconds later. Comparing snapshots helps distinguish a worker that stays waiting for work from one that is stuck or repeatedly blocked. VisualVM's Threads tab provides thread activity timelines and lets you capture thread dumps without stopping the application. VisualVM Threads documentation

For a pool you control, the application can also report its own metrics—for example, `ThreadPoolExecutor.getActiveCount()` and `getQueue().size()`. VisualVM shows thread state; pool metrics tell you whether the pool has queued tasks waiting to run.

## 3. What are cache eviction policies?

A cache eviction policy decides which cached item to remove when the cache is full and needs room for something new.

Common policies include:
- **LRU (Least Recently Used):** Remove the item that hasn't been accessed for the longest time. Useful when recently used items are likely to be used again.
- **LFU (Least Frequently Used):** Remove the item accessed the fewest times. Useful when popular items tend to stay popular.
- **FIFO (First In, First Out):** Remove the oldest item added, regardless of how often it was used.
- **TTL (Time to Live):** Expire items after a set duration. Often combined with another policy, such as LRU.
- **Random:** Remove a randomly chosen item. Simple and sometimes effective for large caches.

## 4. Suppose you have 5 tyres in the car and you want to use each of them equally to travel 1000 km. How will you achieve this?

Assuming the car uses 4 tyres at a time, rotate all 5 so each tyre spends the same distance on the car.

Across 1,000 km, the car uses 4 × 1,000 = 4,000 tyre-km. Split equally among 5 tyres: 800 km per tyre.

One simple schedule: drive 200 km, then swap the spare with one tyre on the car. Repeat, rotating a different tyre out each time. After five 200 km legs, each tyre will have been used for four legs (800 km) and served as the spare for one leg (200 km).

## 5. LRU Cache Program

```java
import java.util.HashMap;

public class LruCache {
	private final int capacity;
	private final HashMap<Integer, Node> map = new HashMap<>();

	private final Node dummyHead = new Node(0, 0);
	private final Node dummyTail = new Node(0, 0);

	private static class Node {
		int key;
		int value;
		Node prev;
		Node next;

		Node(int key, int value) {
			this.key = key;
			this.value = value;
		}
	}

	public LruCache(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}
		this.capacity = capacity;

		dummyHead.next = dummyTail;
		dummyTail.prev = dummyHead;
	}

	public int get(int key) {
		Node node = map.get(key);

		if (node == null) {
			return -1;
		}

		remove(node);
		addAfterHead(node);
		return node.value;
	}

	public void put(int key, int value) {
		Node node = map.get(key);

		if (node != null) {
			node.value = value;
			remove(node);
			addAfterHead(node);
			return;
		}

		node = new Node(key, value);
		map.put(key, node);
		addAfterHead(node);

		if (map.size() > capacity) {
			Node leastRecentlyUsed = dummyTail.prev;
			remove(leastRecentlyUsed);
			map.remove(leastRecentlyUsed.key);
		}
	}

	//-------- Helper Method-------------

	private void addAfterHead(Node node) {
		node.prev = dummyHead;
		node.next = dummyHead.next;

		dummyHead.next.prev = node;
		dummyHead.next = node;
	}

	private void remove(Node node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
	}

	public static void main(String[] args) {
		LruCache cache = new LruCache(2);

		cache.put(1, 100);
		cache.put(2, 200);
		cache.get(1);       // Key 1 becomes most recently used
		cache.put(3, 300);  // Evicts key 2

		System.out.println(cache.get(2)); // -1
		System.out.println(cache.get(1)); // 100
		System.out.println(cache.get(3)); // 300
	}
}
```

## 6. Value based sorting in HashMap

```java
class Employee {
	int age;
	String name;

	Employee(int age, String name) {
		this.age = age;
		this.name = name;
	}
}

public class Main {
	public static void main(String[] args) {
		HashMap<String, Employee> employees = new HashMap<>();

		employees.put("E1", new Employee(30, "Zara"));
		employees.put("E2", new Employee(25, "Arun"));
		employees.put("E3", new Employee(40, "Maya"));

		List<Map.Entry<String, Employee>> sorted =
				new ArrayList<>(employees.entrySet());

		Collections.sort(sorted, new Comparator<Map.Entry<String, Employee>>() {
			@Override
			public int compare(	Map.Entry<String, Employee> e1, Map.Entry<String, Employee> e2) {
				return e1.getValue().name.compareTo(e2.getValue().name);
			}
		});

		for (Map.Entry<String, Employee> entry : sorted) {
			System.out.println(entry.getKey() + " -> " + entry.getValue().name + ", age " + entry.getValue().age);
		}
	}
}
```

## 7. Singleton class with doubly lock

```java
public class Singleton {
	private static volatile Singleton instance;

	private Singleton() {}

	public static Singleton getInstance() {
		if (instance == null) {                 // First check: no lock
			synchronized (Singleton.class) {    // Acquire one lock
				if (instance == null) {         // Second check: inside lock
					instance = new Singleton();
				}
			}
		}
		return instance;
	}
}
```

There are not two locks here. There is one lock, acquired only by the thread that sees `instance == null`. The two `if` checks are needed because another thread may create the instance while the current thread is waiting for the lock.

`volatile` is required so other threads see the fully constructed instance. In many cases, the simpler initialization-on-demand holder idiom is preferable:

```java
public class Singleton {
	private Singleton() {}

	private static class Holder {
		private static final Singleton INSTANCE = new Singleton();
	}

	public static Singleton getInstance() {
		return Holder.INSTANCE;
	}
}
```

## 8. Explain Factory design pattern

The Factory pattern puts object creation in one place. Code that needs an object asks the factory for it instead of directly calling a specific implementation's constructor.

```java
interface Notification {
	void send(String message);
}

class EmailNotification implements Notification {
	public void send(String message) {
		System.out.println("Email: " + message);
	}
}

class SmsNotification implements Notification {
	public void send(String message) {
		System.out.println("SMS: " + message);
	}
}

class NotificationFactory {
	public static Notification create(String type) {
		if ("email".equalsIgnoreCase(type)) {
			return new EmailNotification();
		} else if ("sms".equalsIgnoreCase(type)) {
			return new SmsNotification();
		}

		throw new IllegalArgumentException("Unknown notification type: " + type);
	}
}

public class Main {
	public static void main(String[] args) {
		Notification notification = NotificationFactory.create("email");
		notification.send("Your order has shipped");
	}
}
```

Main works with the `Notification` interface. The factory chooses which implementation to create, so callers don't need to know the implementation's constructor.

## 9. What is builder design pattern?

The Builder pattern creates an object step by step. It's useful when an object has several optional fields, because it avoids long constructors with many parameters.

```java
class Employee {
	private final String name;
	private final int age;
	private final String department;

	private Employee(Builder builder) {
		this.name = builder.name;
		this.age = builder.age;
		this.department = builder.department;
	}

	public static class Builder {
		private final String name; // Required
		private int age;           // Optional
		private String department; // Optional

		public Builder(String name) {
			this.name = name;
		}

		public Builder age(int age) {
			this.age = age;
			return this;
		}

		public Builder department(String department) {
			this.department = department;
			return this;
		}

		public Employee build() {
			return new Employee(this);
		}
	}

	@Override
	public String toString() {
		return "Employee{name='" + name + "', age=" + age
				+ ", department='" + department + "'}";
	}
}

public class Main {
	public static void main(String[] args) {
		Employee employee = new Employee.Builder("Asha")
				.age(30)
				.department("Engineering")
				.build();

		System.out.println(employee);
	}
}
```

The Builder collects the values, and `build()` creates the Employee. The required name is passed to the builder constructor; optional values can be set fluently.

## 10. Why Strings are immutable in java?

Java String objects are immutable: once created, their contents cannot be changed.
Operations such as `toUpperCase()` return a new string instead of modifying the original.

```java
String text = "hello";
String upper = text.toUpperCase();

System.out.println(text);  // hello
System.out.println(upper); // HELLO
```

Immutability is useful because it:
- Makes strings safe to share. Multiple parts of a program can use the same string without one changing it unexpectedly.
- Supports security. Values like file paths, class names, and URLs can't be changed after they're checked.
- Allows string pooling. Java can reuse string literals safely because their contents won't change.
- Makes strings suitable as map keys. A string's hash code and equality won't change while it's used as a key.
- Simplifies concurrency. Reading an immutable string from multiple threads needs no synchronization.

When you need to build or modify text repeatedly, use `StringBuilder`, then convert the result to a String.

## 11. ArrayList Vs LinkedList

| # | Aspect | ArrayList | LinkedList |
|---|---|---|---|
| 1 | Internal Storage | Stores elements in a resizable array. | Stores elements in linked nodes. Each node points to the previous and next node. |
| 2 | Access by Index | Fast, O(1). | Slow, O(n), because it must walk through the nodes. |
| 3 | Adding at the End | Usually O(1) amortized. Occasionally, it must grow its internal array. | O(1). |
| 4 | Inserting or Removing in the Middle | O(n), because elements may need to shift. | O(n) to reach the position, then O(1) to update the links. |
| 5 | Memory | Uses less memory in most cases. | Uses more memory because each node stores links. |

**When to use**

Use `ArrayList` by default, especially when you often access elements by index.
Use `LinkedList` when you frequently add or remove elements at the beginning or end.
For queue or deque operations, consider `ArrayDeque`.

**Example**

```java
List<String> names = new ArrayList<>();
names.add("Asha");
names.add("Ravi");

String firstName = names.get(0); // Fast with ArrayList
```

## 12. How locking is done in concurrent hash map?

In Java, `ConcurrentHashMap` allows multiple threads to read and update the map safely without locking the entire map for every operation.

In modern Java (Java 8 and later), broadly:
- Reads are usually non-blocking. A `get()` generally does not acquire a lock.
- Updates use a combination of CAS (an atomic compare-and-set operation) and synchronized locking on a particular bucket/bin when needed.
- Threads updating different bins can often proceed concurrently. Updates to the same bin may need to wait for one another.
- During resizing, threads can help transfer entries to the new table.

This is an implementation detail and can vary by Java version. The main takeaway is that `ConcurrentHashMap` uses finer-grained coordination than one lock for the whole map, while still providing thread-safe operations.

**Example:**

```java
ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
counts.put("apples", 1);
counts.compute("apples", (key, value) -> value == null ? 1 : value + 1);
```

Use atomic map methods such as `compute`, `merge`, or `putIfAbsent` when an update depends on the current value. A separate `get()` followed by `put()` is not atomic as a pair.

## 13. How JWT works in java?

JWT (JSON Web Token) is a compact token an application can issue after authentication. A Java API can then verify the token on later requests instead of asking the user to log in again for each request.

A JWT has three Base64URL-encoded parts: `header.payload.signature`
- **Header:** identifies the token type and signing algorithm.
- **Payload:** contains claims, such as a user ID and expiry time.
- **Signature:** lets the server detect whether the header or payload was changed.

The payload is encoded, not encrypted. Don't put passwords or other secrets in it.

**Typical flow**

1. The client sends login credentials to the Java application.
2. The application verifies them and creates a signed JWT.
3. The client sends it on later requests:
   ```
   Authorization: Bearer <token>
   ```
4. A Java filter or security framework verifies the signature and expiry. If valid, it uses the claims to identify the user and authorize the request.

In a real app, use a Java security framework such as Spring Security to validate the token in a request filter. Keep signing keys out of source control, use HTTPS, set reasonable expiry times, and handle invalid or expired tokens by returning 401 Unauthorized.

## 14. Difference between Kafka and RabbitMQ

Both Kafka and RabbitMQ send messages between applications, but they use different models.

| # | Aspect | Kafka | RabbitMQ |
|---|---|---|---|
| 1 | Core Model | A persistent, partitioned event log. | A message broker that routes messages to queues. |
| 2 | How Messages Are Read | Consumers track their position in a topic using offsets. Events are retained according to a configured retention policy, so consumers can reread them while they are still retained. | Consumers receive messages from queues. After a message is acknowledged, it is normally removed from the queue. |
| 3 | Multiple Consumers | Different consumer groups can independently read the same topic. | Exchanges route messages to one or more queues. Consumers read from those queues. |
| 4 | Common Use Cases | High-volume event streams, event replay, analytics, and sending the same stream to multiple systems. | Task queues, work distribution, and flexible message routing. |
| 5 | Ordering | Events are ordered within each topic partition. | Messages are generally delivered in queue order, but multiple consumers and message redelivery can affect the order observed by consumers. |

**Rule of thumb**

Choose Kafka when you need a durable event stream that multiple systems can read or replay.
Choose RabbitMQ when you need flexible routing and queues to distribute work among workers.
Both can support reliable systems. Delivery guarantees, ordering, and performance depend on configuration and how consumers handle failures.

## 15. Java MultiThreading questions

### 15.1 Thread pools and executors

**A. How would you size a thread pool for CPU-bound work versus I/O-bound work?**

CPU-bound tasks spend most of their time using the processor. Too many threads cause extra context switching without increasing throughput.

A common starting point is: Thread pool size = number of available CPU cores
Sometimes use cores + 1 to allow another thread to run when one is briefly blocked. Measure under realistic load to choose the final size.

In Java:
```java
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService pool = Executors.newFixedThreadPool(cores);
```

**IO-BOUND WORK**

I/O-bound tasks spend much of their time waiting for databases, network calls, or files. While one thread waits, another can use the CPU, so the pool can be larger than the number of cores.

A rough starting formula is:
Threads = CPU cores × (1 + wait time / compute time)
For example, with 8 cores and tasks that spend about 4 times as long waiting as computing:
Threads = 8 × (1 + 4) = 40
Treat this as a starting estimate, not a fixed rule. Measure throughput and latency, and account for limits such as database connections, downstream service capacity, memory, and queue size.

**PRACTICAL GUIDELINES**
- Use separate pools for workloads with different behavior, such as CPU-heavy processing and database calls.
- Use bounded queues so overload does not build an unbounded backlog.
- Monitor active threads, queue length, task wait time, execution time, rejections, CPU, and downstream connection usage.
- Tune with load tests that reflect production traffic.
- For Java 21 and later, virtual threads can simplify handling many blocking tasks, but they do not increase CPU capacity or remove database and service limits.

**B. What happens when a ThreadPoolExecutor's work queue is full?**

When the queue is full, ThreadPoolExecutor tries to create another worker up to maximumPoolSize.
If it cannot create one, the task is rejected.
The configured RejectedExecutionHandler decides what happens: throw, run in the caller, discard, or discard an older queued task.
Choose a policy carefully; rejection can signal that the system is overloaded

**C. How do corePoolSize, maximumPoolSize, keepAliveTime, and the queue interact?**

ThreadPoolExecutor handles a new task in this order:
1. If running threads are fewer than corePoolSize, it starts a new thread.
2. Otherwise, it queues the task if the queue has space.
3. If the queue is full, it starts threads up to maximumPoolSize.
4. If the queue is full and the pool is at maximum size, it rejects the task.
5. keepAliveTime controls how long excess threads (above corePoolSize) wait when idle before being removed.

Example: corePoolSize = 2, maximumPoolSize = 4, queue capacity = 2.
- Tasks 1 and 2 start two threads.
- Tasks 3 and 4 wait in the queue.
- Task 5 starts a third thread; task 6 starts a fourth.
- Task 7 is rejected because the queue and pool are full.
- When the pool is quiet, the extra threads above 2 are removed after keepAliveTime.

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
		2,                      // corePoolSize
		4,                      // maximumPoolSize
		30, TimeUnit.SECONDS,   // keepAliveTime
		new ArrayBlockingQueue<>(2),
		new ThreadPoolExecutor.AbortPolicy()
);
```

**D. When would you use a bounded queue? How would you choose its capacity?**

Use a bounded queue to cap how many tasks can wait, preventing a traffic spike or slow workers from causing unbounded memory growth and increasing latency.
Choose its capacity based on load testing, acceptable wait time, task memory usage, and downstream limits such as database connections. A larger queue absorbs short bursts but can increase waiting time; a smaller queue applies backpressure sooner through rejection or caller-runs behavior.
Monitor queue length, task wait time, rejections, and worker utilization, then tune the capacity against your latency and throughput goals.

**E. Which rejection policy would you use, and what are the trade-offs?**

ThreadPoolExecutor provides four built-in rejection policies:
1. **AbortPolicy:** Throws RejectedExecutionException. Use when the caller should know the task was rejected. The caller must handle the exception.
2. **CallerRunsPolicy:** Runs the task on the submitting thread. This slows task submission and applies backpressure, but can make request threads slow or block.
3. **DiscardPolicy:** Silently drops the task. Use only when losing the task is acceptable.
4. **DiscardOldestPolicy:** Drops the oldest queued task, then retries submission. This can lose important work and change task order.

For important tasks, I'd usually start with AbortPolicy and handle rejection explicitly, for example by returning an overload response or retrying safely. Use CallerRunsPolicy when slowing producers is an acceptable way to apply backpressure.

**F. How do you diagnose a system where the executor has many queued tasks but few active workers?**

Check the worker threads' thread dump and state to see what they're doing. Few active workers with a growing queue may mean tasks are blocked, waiting on a lock or I/O, or the pool is undersized.
Compare task arrival rate with completion rate, and inspect task duration, queue wait time, executor configuration, and downstream limits such as database connections. Use thread dumps, metrics, and profiling to locate the bottleneck before increasing the pool size.

**G. How do you prevent one slow task from starving unrelated work?**

Use separate thread pools or executors for workloads with different latency and resource needs, so slow tasks cannot fill the queue used by fast tasks. Set timeouts and cancellation for tasks that can hang, and use bounded queues with an appropriate rejection or backpressure policy. Monitor queue wait times and task durations to catch starvation early.

**H. When would you use separate executors for different workloads?**

Use separate executors when workloads have different resource needs or can block one another—for example, CPU-heavy processing, database calls, and slow external API requests. This prevents a saturated or slow workload from consuming all threads and queue space needed by another.
Give each executor an appropriate pool size, bounded queue, timeout, and rejection policy. Also respect shared limits, such as database connection counts; separate pools isolate workloads but do not create more downstream capacity.

**J. How do virtual threads change the way you design a thread-per-request application?**

Virtual threads make it practical to use one thread per request, even when requests spend much of their time waiting on blocking I/O. This can simplify code compared with callback-based or reactive designs.
Create a virtual thread per task instead of sizing a large platform-thread pool. Virtual threads do not make CPU-bound work faster, and they do not increase database or downstream-service capacity, so use connection pools, timeouts, and concurrency limits where needed.
Avoid using thread pools just to limit virtual-thread count; use a semaphore or other resource-specific limit. Be cautious with ThreadLocal state and blocking while holding locks, and check your Java version's guidance on virtual-thread pinning.

**F. How virtual threads works in java and why it fast?**

A virtual thread is a lightweight Java thread managed by the JVM rather than being tied to one operating-system thread for its entire lifetime.

How it works:
1. The JVM runs a virtual thread on an OS-backed platform thread, called a carrier.
2. When the virtual thread reaches a supported blocking operation, such as waiting for I/O, the JVM can suspend it and free the carrier to run another virtual thread.
3. When the I/O is ready, the JVM schedules the virtual thread to continue, possibly on a different carrier.

This helps with applications that handle many concurrent requests that spend much of their time waiting. The JVM can manage many virtual threads using a much smaller number of carrier threads, with less per-thread overhead than creating a platform thread for every request. Oracle's Java 21 virtual-thread guide

Important: Virtual threads are not faster at executing code. They improve scalability and throughput for I/O-heavy work; they do not make CPU-heavy work run faster or increase the capacity of a database or remote service. Java 21 API documentation

Example (Java 21+):
```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
	executor.submit(() -> callDatabase());
	executor.submit(() -> callRemoteService());
}
```
The JVM starts a virtual thread for each task. For limited resources such as database connections, still use a connection pool or another concurrency limit.

### 15.2 Synchronization and locking

**A. Explain the Java Memory Model and the happens-before relationship.**

The Java Memory Model (JMM) defines how threads interact through shared memory. It specifies when a thread's writes become visible to other threads and which instruction reorderings are allowed. It gives Java's concurrency rules; it is not simply a description of where data physically lives.

A happens-before relationship means one action is guaranteed to be visible and ordered before another action. If there is no such relationship between conflicting access to shared data, the program has a data race, and another thread may see stale or unexpected values.

Common happens-before rules:
- **Program order:** Actions in a single thread happen-before later actions in that thread.
- **Monitor lock:** Releasing a synchronized lock happens-before another thread subsequently acquires that same lock.
- **Volatile:** Writing a volatile variable happens-before another thread subsequently reads that variable.
- **Thread start:** Actions before thread.start() happen-before actions in the started thread.
- **Thread join:** Actions in a thread happen-before another thread successfully returns from join() on it.
- **Transitivity:** If A happens-before B, and B happens-before C, then A happens-before C.

Example using volatile:
```java
class Example {
	private int value;
	private volatile boolean ready;

	void writer() {
		value = 42;
		ready = true;
	}

	void reader() {
		if (ready) {
			System.out.println(value); // Guaranteed to see 42
		}
	}
}
```
When the reader sees ready == true, the volatile write/read relationship makes the earlier value = 42 write visible too.

Interview summary: The JMM defines visibility and ordering rules. Happens-before is the guarantee that makes one thread's writes visible to another; synchronized, volatile, start(), and join() establish such guarantees.

**B. What guarantees does volatile provide, and what does it not provide?**

Volatile provides visibility and ordering guarantees for a variable shared between threads.
- A write to a volatile variable becomes visible to threads that subsequently read that variable.
- It prevents certain reordering across the volatile read or write, so actions before a volatile write are visible after another thread reads that value.

volatile does not make compound operations atomic. For example, count++ is a read, increment, and write, so concurrent increments can be lost even if count is volatile.

Use volatile for simple state flags or values when one thread writes and others read. Use synchronized, a lock, or an atomic class such as AtomicInteger when you need atomic updates. Java Language Specification: happens-before

**C. Why is count++ not thread-safe even when count is volatile?**

count++ is a read–modify–write operation, not one atomic action. Two threads can both read the same value, increment it, and write back the same result, losing one increment. volatile makes each read and write visible, but does not make the whole sequence atomic.

Use AtomicInteger for atomic increments:
```java
AtomicInteger count = new AtomicInteger();
count.incrementAndGet();
```

**D. When would you use synchronized, ReentrantLock, ReadWriteLock, or StampedLock?**

Use synchronized by default for simple mutual exclusion. Choose a more specialized lock when you need a feature it provides.
- **synchronized:** Protect a short critical section with one exclusive lock. Java acquires and releases it automatically, including when an exception occurs. It's usually the clearest option. Oracle synchronization guide
- **ReentrantLock:** Use when you need capabilities such as timed or interruptible lock acquisition, tryLock(), fairness options, or multiple Condition variables. Always release it in a finally block.
- **ReadWriteLock:** Use when reads are frequent, writes are relatively rare, and concurrent reads can safely proceed together. Writes still require exclusive access; measure first, since the extra coordination may not help every workload.
- **StampedLock:** Consider for specialized, read-heavy code where optimistic reads may reduce contention. It is more complex and not reentrant, so use it only when you can validate optimistic reads correctly. Java 21 StampedLock API

Rule of thumb: start with synchronized; switch when you have a specific requirement or measured contention that another lock addresses.

**E. How would you detect and prevent deadlocks?**

**DETECTING DEADLOCKS**
- Take a thread dump using VisualVM, jstack, or Java Flight Recorder.
- Look for threads in BLOCKED state, each waiting for a lock held by another thread.
- A deadlock report often shows a circular chain: Thread A holds Lock 1 and waits for Lock 2, while Thread B holds Lock 2 and waits for Lock 1.
- Monitor lock contention and blocked-thread counts in production.

**PREVENTING DEADLOCKS**
- Acquire multiple locks in a consistent global order in every code path.
- Keep synchronized sections short; avoid network calls or callbacks while holding locks.
- Use tryLock() with a timeout when appropriate, and handle failure safely.
- Avoid nested locks where possible.
- Review lock ownership and release behavior carefully.

Java's ThreadMXBean.findDeadlockedThreads() can also detect cycles involving monitor and ownable synchronizers. A thread dump helps identify which locks and code paths are involved.

**F. What are lock contention, lock convoying, and priority inversion?**

**LOCK CONTENTION**
Multiple threads try to acquire the same lock. Only one can hold it, so the others wait. This can reduce throughput and increase latency.

**LOCK CONVOYING**
A group of threads repeatedly waits for the same lock. When the lock is released, threads take turns acquiring it, creating a queue and limiting progress. Scheduling overhead can make this worse.

**PRIORITY INVERSION**
A high-priority thread waits for a lock held by a low-priority thread. A medium-priority thread can keep the low-priority thread from running, indirectly delaying the high-priority thread.

In practice, reduce contention by keeping critical sections short, avoiding unnecessary shared state, and measuring lock waits before changing the locking strategy.

**G. How can lock scope be reduced safely?**

Reduce lock scope by locking only the shared state that must be protected, and keeping unrelated work outside the critical section.
1. Identify the shared data and the invariant that must stay consistent.
2. Do independent or slow work, such as I/O or calculations, before acquiring the lock.
3. Lock, check the current state, and make the smallest required update.
4. Unlock, then do follow-up work outside the lock.

Be careful not to move a check outside the lock if another thread could change the data before the update. Keep related reads and writes under the same lock when they must be atomic together.

**H. What are the risks of calling external code while holding a lock?**

Calling external code while holding a lock can:
- Cause deadlocks if that code tries to acquire another lock.
- Hold the lock for an unpredictable time, blocking other threads.
- Trigger callbacks that re-enter your code and create unexpected behavior.
- Increase latency or cause a thread-pool bottleneck if the call performs slow I/O.

Keep the critical section limited to the shared-state update. If you need to call external code, capture the required data under the lock, release it, then make the call. Ensure the state remains valid if another thread changes it in between.

### 15.3 Concurrent collections

**A. When should you use ConcurrentHashMap.compute(), merge(), or putIfAbsent()?**

Use these methods when the update depends on whether a key already has a value. Each performs the operation atomically for that key in a ConcurrentHashMap.

putIfAbsent(key, value)
Use when you want to add a value only if the key is not already present.
```java
map.putIfAbsent("A", 1);
```
compute(key, function): Use when the new value depends on the key and its current value. Return null from the function to remove the mapping.
```java
map.compute("A", (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
```
merge(key, value, function): Use when you have a value to add or combine with the current value. If the key is absent, the supplied value is inserted; otherwise, the function combines the old and supplied values.
```java
map.merge("A", 1, Integer::sum);
```
Keep the functions short and avoid blocking or updating other mappings in the same map from inside them.

**B. What consistency guarantees do concurrent collections provide during iteration?**

- Concurrent collections generally allow iteration while other threads update them, without throwing ConcurrentModificationException. The exact guarantee depends on the collection:
- ConcurrentHashMap iterators are weakly consistent. They can reflect some updates made during iteration, but they do not provide a single, atomic snapshot of the map.
- CopyOnWriteArrayList iterators use a snapshot of the array taken when the iterator is created. They don't see later changes.
- Synchronized collections still require you to synchronize on the collection while iterating to prevent concurrent modification during the traversal.

So, safe iteration does not necessarily mean the iterator sees one consistent point-in-time view. Check the specific collection's contract when that matters.

**C. How would you implement a bounded, thread-safe LRU cache?**

**D. When would you choose BlockingQueue, ConcurrentLinkedQueue, or a custom queue?**

BlockingQueue
Choose it for producer-consumer workflows where consumers should wait when the queue is empty, or producers should wait when a bounded queue is full. It also provides built-in coordination methods such as put(), take(), and timed offer() or poll(). Examples include ArrayBlockingQueue and LinkedBlockingQueue.

ConcurrentLinkedQueue
Choose it for a thread-safe, non-blocking FIFO queue when producers and consumers should not wait inside queue operations. It is unbounded, so control growth elsewhere if producers can outpace consumers.

Custom queue
Choose one only when standard queues cannot meet a specific requirement, such as specialized ordering or coordination. Custom concurrent queues are difficult to implement correctly; first check whether an existing queue can be composed with other concurrency tools.

**E. How would you safely publish an object to other threads?**

Safe publication means making a fully initialized object visible to other threads through a mechanism that establishes a happens-before relationship.

Common approaches:
- Store it in a volatile field.
- Assign it while holding a lock, then have readers access it under the same lock.
- Store it in a thread-safe collection, such as ConcurrentHashMap.
- Publish it through a properly initialized static field.
- Start a thread after constructing the object; actions before Thread.start() are visible to that thread.

Also, avoid letting this escape from the constructor before initialization is complete. If the object is immutable and its fields are final, that helps, but the reference still needs to be published through a safe mechanism.

### 15.4 Async programming and CompletableFuture

- How do thenApply(), thenCompose(), and thenCombine() differ?
- How do you handle exceptions and timeouts in a CompletableFuture pipeline?
- Which executor runs an async stage when none is specified?
- How would you prevent blocking work from running on the common ForkJoinPool?
- How do you cancel a task, and what does cancellation guarantee?
- How would you limit concurrency when calling a downstream service asynchronously?
- How do you propagate tracing, security context, or request context across async tasks?

### 15.5 Production diagnosis

- A service's latency rises, CPU is low, and the thread pool is saturated. How do you investigate?
- How would you distinguish threads waiting for work from threads blocked on a lock?
- What do RUNNABLE, BLOCKED, WAITING, and TIMED_WAITING mean in a thread dump?
- How do you use thread dumps, Java Flight Recorder, and VisualVM to investigate contention?
- What metrics would you expose for executors and task queues?
- How would you investigate high context switching or excessive thread creation?
- How do you identify a thread leak?
- What would you investigate if a thread dump shows many threads waiting in socket reads?

### 15.6 Design and failure scenarios

- How would you design a bounded producer-consumer system with backpressure?
- How do you prevent overload from propagating through multiple services?
- How would you make task processing safe when a worker crashes halfway through?
- How would you handle retries without causing duplicate side effects?
- How do you preserve ordering while processing tasks concurrently?
- How would you shut down an executor gracefully while still handling in-flight work?
- How do you avoid lost updates when several threads modify shared state?
- How would you design a rate limiter or semaphore-based concurrency limit?
- How do you prevent a slow downstream dependency from exhausting all request threads?

### 15.7 Coding and system-design exercises

- Implement a bounded blocking queue using wait() and notifyAll().
- Implement a thread-safe LRU cache and explain its locking strategy.
- Write a task scheduler that supports cancellation and graceful shutdown.
- Implement a rate limiter and compare token-bucket and semaphore approaches.
- Design a service that processes millions of records with bounded memory and controlled concurrency.
- Given a thread dump, identify the likely bottleneck and propose a fix.

## 16. Java Collection questions

### 16.1 Collections design and trade-offs
- How do you choose between ArrayList, LinkedList, ArrayDeque, and CopyOnWriteArrayList?
- What are the time and memory trade-offs of HashMap, TreeMap, and LinkedHashMap?
- When would you use a Set instead of a List?
- How do collection interfaces differ from their implementations?
- How do you choose a collection based on access patterns, ordering, concurrency, and memory use?

### 16.2 HashMap and hashing
- Explain how HashMap stores and retrieves entries.
- What happens when two keys have the same hash code?
- Why must equal objects have equal hash codes?
- What can go wrong if a key is mutated after being inserted into a HashMap?
- How does HashMap resize, and what is the cost of resizing?
- Why should you provide an initial capacity when the expected map size is known?
- What are tree bins, and when can they be used?
- How would you investigate poor HashMap performance in production?
- When would you choose IdentityHashMap or WeakHashMap?

### 16.3 Iterators and collection behavior
- What is the difference between fail-fast and weakly consistent iterators?
- Is ConcurrentModificationException a thread-safety mechanism?
- How do Iterator.remove() and Collection.remove() differ during iteration?
- How do subList views behave if the original list is modified?
- What does it mean for a collection to be backed by another collection?
- How do equals() and hashCode() affect Set and Map behavior?

### 16.4 Concurrent collections
- How does ConcurrentHashMap support concurrent reads and updates?
- What atomicity guarantees do compute(), merge(), and putIfAbsent() provide?
- Why is get() followed by put() not atomic?
- How does CopyOnWriteArrayList handle reads and writes?
- When would you use BlockingQueue, ConcurrentLinkedQueue, or a synchronized collection?
- What iteration guarantees do concurrent collections provide?
- How would you choose a concurrent collection for a high-read, low-write workload?

### 16.5 Ordering and sorting
- How do Comparable and Comparator differ?
- What can happen if a Comparator is inconsistent with equals()?
- How does TreeMap use its comparator to determine key equality?
- How would you sort a map by its values?
- How would you maintain a top-K list efficiently as new values arrive?
- How do stable sorting and comparator transitivity matter in real systems?

### 16.6 Immutability and API design
- What is the difference between an unmodifiable collection and an immutable collection?
- How do List.of(), Collections.unmodifiableList(), and defensive copies differ?
- How would you safely expose a collection from a mutable class?
- What risks arise from using mutable objects as map keys?
- How would you design a collection API that prevents callers from modifying internal state?

### 16.7 Performance and memory
- How would you estimate the memory usage of a large HashMap?
- What causes excessive allocation when processing collections?
- When should you use primitive collections or arrays instead of boxed collections?
- How would you process a very large dataset without loading it all into memory?
- What are the performance costs of repeated resizing?
- How do you benchmark collection performance reliably?

### 16.8 Production scenarios
- A HashMap lookup becomes slow as the application grows. How would you diagnose it?
- A service has high memory usage from collections. How would you find the source?
- A cache backed by a ConcurrentHashMap grows without limit. How would you control its size and expiration?
- A collection is being modified while another thread iterates over it. What designs could solve this?
- A batch job processes millions of records and runs out of memory. How would you redesign it?
- A TreeSet unexpectedly drops values. What comparator or equality issues would you investigate?

### 16.9 Coding and design exercises
- Implement an LRU cache using a HashMap and a doubly linked list.
- Implement a bounded queue and explain its concurrency behavior.
- Find the top K most frequent elements in a large input.
- Remove duplicates while preserving insertion order.
- Merge two sorted collections efficiently.
- Design an in-memory cache with eviction, expiration, and thread-safe access.
- Given a workload description, choose suitable collections and justify the choices.

## 17. Java 8 to java 17 new feature list

### JAVA 8
1. **Lambda expressions** — Write concise implementations of functional interfaces.
2. **Functional interfaces** — Interfaces with one abstract method, such as Runnable, Predicate, Function, and Consumer.
3. **Stream API** — Process collections with operations such as filter, map, flatMap, and reduce.
4. **Method references** — Refer to an existing method using syntax such as String::valueOf.
5. **Optional** — Represent a value that may be absent and reduce some null checks.
6. **Default and static methods in interfaces** — Add behavior to interfaces while supporting existing implementations.
7. **New Date and Time API** — Use classes such as LocalDate, LocalDateTime, and ZonedDateTime.

### JAVA 9
8. **Collection factory methods** — Create unmodifiable collections with List.of(), Set.of(), and Map.of().
9. **Module system** — Organize code into named modules with explicit dependencies and exports.
10. **Private interface methods** — Share implementation between interface default methods.

### JAVA 10
11. **Local variable type inference** — Use var for local variables when the type is clear from the initializer.

### JAVA 11
12. **Standard HTTP Client** — Use HttpClient for synchronous and asynchronous HTTP requests.
13. **New String and Files methods** — Examples include String.isBlank(), strip(), lines(), repeat(), and Files.readString().

### JAVA 14
14. **Switch expressions** — Use switch as an expression that returns a value.
15. **Helpful NullPointerException messages** — Get more specific information about which expression was null.

### JAVA 15
16. **Text blocks** — Write multiline string literals more readably.

### JAVA 16
17. **Records** — Define concise data carrier classes. Records became a standard feature in Java 16.
18. **Pattern matching for instanceof** — Test a type and bind the matched value in one step.
19. **Stream.toList()** — Collect stream results into an unmodifiable list.

### JAVA 17
20. **Sealed classes** — Restrict which classes or interfaces may extend or implement a type.
21. **Strong encapsulation of JDK internals** — Internal JDK APIs are more strongly restricted.

### TOP FEATURES TO STUDY FIRST
- Lambda expressions and functional interfaces
- Streams, especially map(), flatMap(), reduce(), and collectors
- Optional and common pitfalls
- Default methods in interfaces
- Date and Time API
- var
- Records
- Pattern matching for instanceof
- Switch expressions
- Sealed classes

## 18. Exception related interview questions

1. What is the difference between checked and unchecked exceptions?
2. What is the difference between Exception and Error?
3. What are try, catch, and finally used for?
4. Can a finally block fail to execute? When?
5. What happens if both the try block and finally block return a value?
6. Can we have a try block without a catch block?
7. Can one try block have multiple catch blocks? What order should they be in?
8. What happens if a parent exception is caught before its child exception?
9. What is the difference between throw and throws?
10. Can a method override another method and declare a broader checked exception?
11. What are the rules for exceptions in method overriding?
12. What is exception propagation?
13. How do you create a custom checked exception? How does it differ from a custom unchecked exception?
14. What is try-with-resources? How does AutoCloseable work with it?
15. What are suppressed exceptions in try-with-resources?
16. What is the difference between final, finally, and finalize()?
17. What is exception chaining? When would you use a cause?
18. What is the difference between getMessage() and getCause()?
19. What happens when an exception is thrown inside a catch or finally block?
20. How should exceptions be handled in a layered application?
21. When should you catch an exception, and when should you let it propagate?
22. Why is catching Exception or Throwable broadly often discouraged?
23. What is the danger of swallowing an exception?
24. How do you preserve the original exception when wrapping it in another exception?
25. How do exceptions affect transaction rollback in frameworks such as Spring?
26. How should REST APIs translate exceptions into HTTP responses?
27. How do you log exceptions without logging the same failure multiple times?
28. What is the difference between fail-fast validation and exception-based control flow?
29. How do checked exceptions interact with lambda expressions and streams?
30. How would you design retries for transient failures without retrying permanent failures?
31. How do you handle exceptions from CompletableFuture or asynchronous tasks?
32. How do you diagnose an exception that occurs only under production load?
33. What information should a useful exception message contain?
34. What is the performance cost of creating and throwing exceptions?
35. How do you avoid exposing sensitive information in exception messages or API responses?

**PRACTICE SCENARIOS**
- A finally block throws an exception while another exception is already being handled. Which exception reaches the caller?
- A method catches an exception, logs it, and continues. What risks does this create?
- A database call times out. How do you decide whether to retry?
- A stream lambda calls a method that throws a checked exception. How can you handle it cleanly?
- A service catches an exception and returns null. What problems might that cause?
- A transaction fails with a checked exception. Will it roll back automatically in your framework?
- Two resources are used in try-with-resources, and both fail to close. How are the exceptions reported?

For senior-level interviews, explain the decision behind the handling strategy: whether to recover, retry, wrap, translate, or propagate the exception.

## 19. String related interview questions

1. Why are String objects immutable in Java?
2. What is the String Constant Pool?
3. What is the difference between:
   ```java
   String a = "hello";
   String b = new String("hello");
   ```
4. What is the difference between == and equals() when comparing strings?
5. What does the intern() method do?
6. How does Java calculate a string's hash code?
7. Why is String commonly used as a HashMap key?
8. What is the difference between String, StringBuilder, and StringBuffer?
9. Is StringBuffer thread-safe? What does that mean?
10. Why can repeated string concatenation in a loop be inefficient?
11. How does the compiler handle string concatenation?
12. What are text blocks, and when would you use them?
13. What is the difference between isEmpty() and isBlank()?
14. What is the difference between trim() and strip()?
15. How do substring() and subSequence() differ?
16. How would you reverse a string in Java?
17. How would you check whether a string is a palindrome?
18. How would you find the first non-repeated character in a string?
19. How would you count the frequency of each character?
20. How would you check whether two strings are anagrams?
21. How do you remove duplicate characters while preserving their order?
22. How would you find the longest substring without repeating characters?
23. How do you split a string while preserving trailing empty values?
24. How would you compare strings without considering case?
25. What issues can arise when comparing strings with different locales?
26. How do regular expressions work with String.matches() and Pattern?
27. Why should passwords or secrets generally not be stored as String?
28. What is the difference between UTF-16 code units, Unicode code points, and user-perceived characters?
29. How would you safely build a string from many pieces of data?
30. How would you avoid exposing sensitive data when logging strings?

For senior-level interviews, be ready to discuss immutability, pooling, Unicode, performance, and security—not just string manipulation problems.

## 20. JVM and Memory related interview questions

1. What are the main components of the JVM?
2. What is the difference between the JDK, JRE, and JVM?
3. What are the main runtime memory areas in Java?
4. What is stored in the heap?
5. What is stored in a thread's stack?
6. What is the difference between heap memory and stack memory?
7. Where are class metadata and static fields stored in modern Java?
8. What is the difference between PermGen and Metaspace?
9. What causes a StackOverflowError?
10. What causes an OutOfMemoryError?
11. How does garbage collection work at a high level?
12. What are the young generation and old generation?
13. What are minor, major, and full garbage collections?
14. What are GC roots?
15. What are strong, soft, weak, and phantom references?
16. Can System.gc() guarantee that garbage collection will run?
17. What are stop-the-world pauses?
18. How do throughput, latency, and memory footprint affect garbage collector choice?
19. What are the differences between G1, ZGC, and Shenandoah?
20. What is a memory leak in Java, even though Java has garbage collection?
21. How would you investigate steadily increasing heap usage in production?
22. How do you take and analyze a heap dump?
23. What can a dominator tree and retained size show?
24. How would you distinguish a memory leak from temporary high memory usage?
25. What is the difference between heap memory and native memory?
26. What can cause native memory usage to grow while heap usage stays stable?
27. What are direct byte buffers, and how do they use memory?
28. What is class loading?
29. What are the bootstrap, platform, and application class loaders?
30. What is class-loader delegation, and why does Java use it?
31. What can cause a class-loader memory leak?
32. What is JIT compilation?
33. What is the difference between interpreted and compiled execution?
34. What is escape analysis?
35. What is the difference between -Xms and -Xmx?
36. How would you choose heap settings for a production service?
37. Which JVM metrics would you monitor in production?
38. How would you investigate frequent or long GC pauses?
39. Which tools would you use to diagnose JVM memory and GC issues?
40. How do Java Flight Recorder and Java Mission Control help diagnose performance problems?
41. What information can GC logs provide?
42. How would you investigate OutOfMemoryError: Java heap space?
43. How would you investigate OutOfMemoryError: Metaspace?
44. How would you investigate OutOfMemoryError: Direct buffer memory?
45. How does object allocation rate affect garbage collection?
46. What is object pooling, and when can it make performance or memory usage worse?
47. How can large temporary objects affect garbage collection?
48. What is the difference between shallow size and retained size?
49. How do finalize(), Cleaner, and try-with-resources differ?
50. Application response time rises after deployment, but heap usage looks normal. What JVM-level issues would you investigate?

## 21. SOLID principles in Java

SOLID is a set of five principles for designing maintainable, flexible object-oriented software.

**S - SINGLE RESPONSIBILITY PRINCIPLE**
A class should have one main responsibility and one main reason to change.
Example: Keep invoice calculations separate from saving invoices to a database.

**O - OPEN/CLOSED PRINCIPLE**
Software entities should be open for extension but closed for modification.
Example: Add a new payment type by implementing a PaymentMethod interface rather than changing existing payment logic.

**L - LISKOV SUBSTITUTION PRINCIPLE**
A subtype should be usable wherever its parent type is expected without breaking correct behavior.
Example: Any implementation of a Bird interface should honor the behaviors promised by that interface. If some birds cannot fly, flying should not be required by the general Bird type.

**I - INTERFACE SEGREGATION PRINCIPLE**
Clients should not be forced to depend on methods they do not use.
Example: Prefer separate Printable and Faxable interfaces over one large interface that requires every device to implement both.

**D - DEPENDENCY INVERSION PRINCIPLE**
High-level code should depend on abstractions rather than concrete low-level implementations.
Example: A service should depend on a PaymentProcessor interface, which can be implemented by different payment providers.

**INTERVIEW SUMMARY**
- SRP: One main responsibility per class.
- OCP: Extend behavior without changing stable existing code.
- LSP: Subtypes should honor the behavior of their parent types.
- ISP: Prefer small, focused interfaces.
- DIP: Depend on abstractions, not concrete implementations.
