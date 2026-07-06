# Chapter 17: Resilience Patterns

## Introduction

On November 25, 2020, a major AWS outage in the US-East-1 region took down a significant
portion of the internet — including services like Roku, Adobe, and Flickr. The root cause?
A capacity issue in Amazon Kinesis that cascaded through dependent services.

Distributed systems **will** fail. Networks partition. Servers crash. Dependencies become
unresponsive. The question isn't *if* failure will occur, but *when* — and whether your
system is designed to handle it gracefully.

**Resilience patterns** are architectural strategies that help systems withstand, adapt to,
and recover from failures without total collapse.

> **Key Insight:** Resilience is not about preventing failure. It's about designing systems
> that continue to operate (possibly in a degraded state) when failures inevitably occur.

---

## 17.1 Why Resilience Matters

### The Cost of Downtime

| Company        | Estimated Cost per Minute of Downtime |
|----------------|---------------------------------------|
| Amazon         | $220,000                              |
| Facebook       | $160,000                              |
| Google         | $150,000                              |
| Average Enterprise | $5,600                            |

### Common Failure Modes in Distributed Systems

```
  +----------+     +----------+     +----------+
  | Service  +--X--+ Service  +--?--+ Service  |
  |    A     |     |    B     |     |    C     |
  +----------+     +-----+----+     +----------+
                         |
                    +----+-----+
                    | Database |
                    | (slow!)  |
                    +----------+
  
  Failures:
  X = Network partition (A can't reach B)
  ? = Timeout (B waiting on slow C)
  ! = Database overloaded (B's requests pile up)
```

| Failure Mode            | Description                                          |
|-------------------------|------------------------------------------------------|
| **Network partition**   | Services can't communicate across network segments   |
| **Cascading failure**   | One service failure triggers failures in dependents  |
| **Thundering herd**     | Many clients retry simultaneously after a failure    |
| **Resource exhaustion** | Thread pools, connections, memory exhausted           |
| **Slow dependency**     | A downstream service responds slowly, blocking callers|
| **Data corruption**     | Inconsistent state due to partial failures           |

---

## 17.2 Circuit Breaker Pattern

The **Circuit Breaker** is the most important resilience pattern. It prevents a service
from repeatedly calling a failing dependency, giving the dependency time to recover.

### Analogy

Just like an electrical circuit breaker trips to prevent a short circuit from causing a fire,
a software circuit breaker "trips" to prevent cascading failures.

### Three States

```
  Circuit Breaker State Diagram:
  
                     Success
                  +----------+
                  |          |
                  v          |
             +----+----+    |
             | CLOSED  |----+
             | (normal)|
             +----+----+
                  |
                  | Failure threshold exceeded
                  | (e.g., 5 failures in 60s)
                  v
             +----+----+
             |  OPEN   |  <-- All requests immediately fail
             |(tripped)|      (fast fail, no network call)
             +----+----+
                  |
                  | Reset timeout expires
                  | (e.g., after 30 seconds)
                  v
           +------+-------+
           |  HALF-OPEN   |  <-- Allow ONE trial request through
           | (testing)    |
           +------+-------+
                  |
          +-------+-------+
          |               |
       Success          Failure
          |               |
          v               v
     +----+----+    +----+----+
     | CLOSED  |    |  OPEN   |
     | (reset) |    | (re-trip)|
     +---------+    +---------+
```

### Detailed Behavior

| State         | Behavior                                                     |
|---------------|--------------------------------------------------------------|
| **CLOSED**    | Requests pass through normally. Failures are counted.        |
|               | If failures exceed threshold, transition to OPEN.            |
| **OPEN**      | All requests **immediately fail** without calling the        |
|               | downstream service. Returns fallback/error response.         |
|               | After a timeout period, transitions to HALF-OPEN.            |
| **HALF-OPEN** | A **single probe request** is allowed through.               |
|               | If it succeeds -> CLOSED (recovered!)                        |
|               | If it fails -> OPEN (still broken, wait longer)              |

### Implementation

```python
import time
from enum import Enum

class State(Enum):
    CLOSED = "CLOSED"
    OPEN = "OPEN"
    HALF_OPEN = "HALF_OPEN"

class CircuitBreaker:
    def __init__(self, failure_threshold=5, reset_timeout=30,
                 success_threshold=1):
        self.state = State.CLOSED
        self.failure_count = 0
        self.success_count = 0
        self.failure_threshold = failure_threshold
        self.reset_timeout = reset_timeout      # seconds
        self.success_threshold = success_threshold
        self.last_failure_time = None

    def call(self, func, *args, **kwargs):
        if self.state == State.OPEN:
            if self._should_attempt_reset():
                self.state = State.HALF_OPEN
            else:
                raise CircuitOpenError("Circuit is OPEN, fast-failing")

        try:
            result = func(*args, **kwargs)
            self._on_success()
            return result
        except Exception as e:
            self._on_failure()
            raise

    def _on_success(self):
        if self.state == State.HALF_OPEN:
            self.success_count += 1
            if self.success_count >= self.success_threshold:
                self.state = State.CLOSED
                self.failure_count = 0
                self.success_count = 0

    def _on_failure(self):
        self.failure_count += 1
        self.last_failure_time = time.time()
        if self.state == State.HALF_OPEN:
            self.state = State.OPEN      # Back to open
            self.success_count = 0
        elif self.failure_count >= self.failure_threshold:
            self.state = State.OPEN

    def _should_attempt_reset(self):
        return (time.time() - self.last_failure_time) >= self.reset_timeout
```

### Libraries

| Language   | Library           | Status        | Notes                            |
|------------|-------------------|---------------|----------------------------------|
| Java       | Resilience4j      | Active        | Recommended for Java/Spring      |
| Java       | Netflix Hystrix   | Deprecated    | Pioneer, but no longer maintained|
| .NET       | Polly             | Active        | Comprehensive resilience library |
| Go         | sony/gobreaker    | Active        | Lightweight circuit breaker      |
| JavaScript | opossum           | Active        | Node.js circuit breaker          |
| Python     | pybreaker         | Active        | Simple and effective             |

---

## 17.3 Bulkhead Pattern

The **Bulkhead** pattern isolates failures so that a problem in one part of the system
doesn't bring down the entire system. Named after the watertight compartments in a ship
that prevent a single breach from sinking the whole vessel.

```
  Without Bulkhead:
  
  All requests share one thread pool (100 threads):
  
  +-------------------------------------------------+
  | Shared Thread Pool (100 threads)                |
  | [Payment][Payment][Payment]...[Search][Orders]  |
  +-------------------------------------------------+
  
  If Payment Service is slow, it hogs ALL 100 threads.
  Search and Orders are starved -> entire system down!
  
  
  With Bulkhead:
  
  +------------------+  +-----------------+  +----------------+
  | Payment Pool     |  | Search Pool     |  | Orders Pool    |
  | (30 threads)     |  | (40 threads)    |  | (30 threads)   |
  | [Pay][Pay][Pay]  |  | [S][S][S][S]    |  | [O][O][O]      |
  +------------------+  +-----------------+  +----------------+
  
  If Payment Service is slow, it only exhausts its 30 threads.
  Search and Orders continue working normally!
```

### Types of Bulkhead Isolation

| Type                    | Mechanism                               | Pros                        | Cons                     |
|-------------------------|-----------------------------------------|-----------------------------|--------------------------|
| **Thread Pool**         | Separate thread pool per dependency     | Strong isolation, timeouts  | Resource overhead (threads)|
| **Semaphore**           | Limit concurrent calls via semaphore    | Lightweight, no thread overhead| No timeout support     |
| **Process Isolation**   | Separate processes/containers per service| Strongest isolation        | Highest resource cost    |

### Thread Pool Bulkhead Example

```java
// Resilience4j Bulkhead Configuration
BulkheadConfig config = BulkheadConfig.custom()
    .maxConcurrentCalls(30)          // Max 30 concurrent requests
    .maxWaitDuration(Duration.ofMs(500))  // Wait max 500ms for a slot
    .build();

Bulkhead paymentBulkhead = Bulkhead.of("paymentService", config);

// Decorate the service call
Supplier<PaymentResult> decoratedCall = Bulkhead
    .decorateSupplier(paymentBulkhead, () -> paymentService.process(order));
```

---

## 17.4 Retry Pattern

Retries handle **transient failures** — temporary issues like network blips, brief
service restarts, or momentary overloads.

### Retry Strategies

```
  Strategy 1: Simple Retry (Dangerous!)
  
  Request --> Fail --> Retry --> Fail --> Retry --> Fail --> Give Up
  (Immediate retries can worsen overload — thundering herd!)
  
  
  Strategy 2: Exponential Backoff
  
  Request --> Fail --> Wait 1s --> Retry --> Fail --> Wait 2s --> Retry
      --> Fail --> Wait 4s --> Retry --> Fail --> Wait 8s --> Give Up
  
  
  Strategy 3: Exponential Backoff with Jitter (Recommended)
  
  Request --> Fail --> Wait 1s + rand(0,1s) --> Retry
      --> Fail --> Wait 2s + rand(0,2s) --> Retry
      --> Fail --> Wait 4s + rand(0,4s) --> Retry
  
  Jitter spreads retries over time, preventing thundering herd.
```

### Jitter Strategies Compared

```
  Without Jitter (All clients retry together):
  
  Time:   0s    1s    2s    4s    8s
          |XXXX |XXXX |XXXX |XXXX |XXXX
          (all clients retry at exactly these times)
  
  
  With Full Jitter (Clients spread out):
  
  Time:   0s    1s    2s    3s    4s    5s    6s    7s    8s
          |X X  |  XX |X    | X X | XX  |  X  | X   |X  X|
          (retries distributed, much less server pressure)
```

### Retry Budget

A **retry budget** limits the total percentage of requests that can be retries, preventing
retry storms at the system level.

```
  Retry Budget Example:
  
  Service handles 1,000 req/s normally
  Retry budget: 20% (max 200 retries/s allowed)
  
  If failure rate spikes:
  - 300 requests fail, all want to retry
  - Only 200 retries are allowed (budget cap)
  - 100 requests fail without retry
  
  This prevents the failure from amplifying 1,000 req/s into 2,000+ req/s
```

### What TO Retry vs What NOT to Retry

| Retry                          | Don't Retry                          |
|--------------------------------|--------------------------------------|
| 500 Internal Server Error      | 400 Bad Request (fix the request)    |
| 502 Bad Gateway                | 401 Unauthorized (fix credentials)   |
| 503 Service Unavailable        | 403 Forbidden (no permission)        |
| 429 Too Many Requests          | 404 Not Found (resource doesn't exist)|
| Connection timeout             | 409 Conflict (duplicate operation)   |
| Network error                  | Request validation failures          |

---

## 17.5 Timeout Pattern

Timeouts prevent your service from waiting indefinitely for an unresponsive dependency.
Without timeouts, threads/connections pile up, eventually exhausting resources.

### Types of Timeouts

```
  Client --> [Connection Timeout] --> Server
         <-- [Read/Response Timeout] <-- Server
  
  Connection Timeout:  How long to wait for the TCP connection to establish
                       Typical: 1-5 seconds
  
  Read Timeout:        How long to wait for the server to send a response
  (Response Timeout)   after the connection is established
                       Typical: 5-30 seconds (depends on operation)
```

### Setting Appropriate Timeouts

| Operation                    | Recommended Timeout | Rationale                       |
|------------------------------|--------------------|---------------------------------|
| Health check                 | 1-2 seconds        | Should be instant               |
| Internal service call        | 3-5 seconds        | Fast, on same network           |
| Database query               | 5-10 seconds       | Most queries are fast           |
| External API call            | 10-30 seconds      | Internet latency unpredictable  |
| File upload/download         | 60-300 seconds     | Large data transfer             |
| Long-running report          | Use async pattern  | Don't block on long tasks       |

### Timeout Anti-Patterns

```
  Anti-Pattern 1: No Timeout
  
  Service A --> Service B (hung, never responds)
  Service A waits... forever... thread blocked... pool exhausted... crash
  
  
  Anti-Pattern 2: Timeout Too High (60 seconds)
  
  Service A --> Service B (takes 55 seconds to fail)
  Users wait 55 seconds staring at a spinner. Terrible UX.
  Meanwhile, 55 seconds * 100 concurrent requests = 100 blocked threads.
  
  
  Anti-Pattern 3: Timeout Without Retry
  
  Service A --> Service B (timeout at 3 seconds)
  Timeout! --> Return error to user immediately
  (But the failure was transient — a retry would have succeeded!)
  
  
  Best Practice: Timeout + Retry + Circuit Breaker
  
  Service A --> Service B (timeout at 3s)
  Timeout! --> Retry with backoff (up to 2 attempts)
  Still failing? --> Circuit breaker opens
  --> Return fallback response
```

---

## 17.6 Fallback Pattern

When a dependency fails, provide an **alternative response** instead of returning an error.
This allows the system to continue operating in a degraded but functional state.

### Fallback Strategies

| Strategy                  | Example                                             | UX Impact       |
|---------------------------|-----------------------------------------------------|-----------------|
| **Default value**         | Show "0 items in cart" when cart service is down     | Minimal         |
| **Cached response**       | Return last known good response from cache           | Minor staleness |
| **Simplified response**   | Return basic product info when recommendations fail  | Reduced features|
| **Alternative service**   | Switch to backup payment processor                   | Transparent     |
| **Graceful error message**| "Feature temporarily unavailable, try again later"   | Honest UX       |

### Example: E-Commerce Product Page

```
  Normal Operation:
  
  Product Page = Product Info + Price + Reviews + Recommendations + Inventory
                  (Product DB)  (Pricing) (Reviews) (ML Service)   (Inventory)
  
  
  Degraded Operation (Reviews service down):
  
  Product Page = Product Info + Price + [Cached Reviews] + Recommendations + Inventory
                                         ^
                                         Fallback: show cached reviews
                                         or hide section entirely
  
  
  Heavily Degraded (Multiple services down):
  
  Product Page = Product Info + [Default Price] + [Hidden] + [Hidden] + "Check in store"
                                 ^                                        ^
                                 Fallback: show list price              Fallback message
```

### Implementation Pattern

```python
class ProductService:
    def get_product_page(self, product_id):
        # Core data (no fallback — this MUST work)
        product = self.product_db.get(product_id)
        
        # Non-critical data with fallbacks
        try:
            reviews = self.review_service.get_reviews(product_id)
        except (TimeoutError, ServiceUnavailableError):
            reviews = self.cache.get(f"reviews:{product_id}", default=[])
            # Log degradation for monitoring
            log.warn(f"Reviews fallback for {product_id}")
        
        try:
            recommendations = self.ml_service.get_recommendations(product_id)
        except Exception:
            recommendations = self.get_popular_products()  # Generic fallback
        
        try:
            inventory = self.inventory_service.check_stock(product_id)
        except Exception:
            inventory = {"status": "unknown", "message": "Check availability in store"}
        
        return ProductPage(product, reviews, recommendations, inventory)
```

---

## 17.7 Hedged Requests

**Hedged requests** send the same request to multiple replicas simultaneously and use
whichever response comes back first. This reduces tail latency.

```
  Normal Request:
  
  Client --> Server A --> Response (maybe slow: 500ms)
  
  
  Hedged Request:
  
  Client --> Server A --> Response (500ms)
        +--> Server B --> Response (50ms)  <-- Use this one! (fastest)
        +--> Server C --> Response (200ms)
  
  Cancel the slower requests once the fastest responds.
```

### When to Use Hedged Requests

| Use Case                                     | Not Appropriate                          |
|----------------------------------------------|------------------------------------------|
| Read-only requests (database reads, lookups) | Write operations (could cause duplicates)|
| Latency-critical paths (search, real-time)   | Expensive computations (wasteful)        |
| System has excess capacity                   | System already at capacity               |

### Hedging Strategy: Delayed Hedging

Instead of sending all requests simultaneously, wait briefly before hedging:

```
  1. Send request to Server A
  2. If no response in 50ms (P95 latency), send hedge to Server B
  3. If no response in 100ms, send hedge to Server C
  4. Use first response, cancel the rest
  
  Timeline:
  
  0ms     50ms    100ms    150ms
  |--------|--------|--------|
  [Request A sent]
           [Hedge B sent]
                    [Hedge C sent]
                         [B responds! Use it, cancel A and C]
```

> **Real-World:** Google uses hedged requests extensively in their infrastructure. Their
> paper "The Tail at Scale" (2013) describes how hedging reduces 99th percentile latency.

---

## 17.8 Load Shedding

When a service is overwhelmed, **load shedding** proactively drops requests to prevent
total collapse. It's the "oxygen mask" principle — protect the system so it can serve
*some* users rather than failing for *all* users.

### Load Shedding Strategies

```
  Strategy 1: Random Drop
  
  Server at 90% capacity --> Drop 20% of incoming requests randomly
  Pros: Simple, fair
  Cons: May drop important requests
  
  
  Strategy 2: Priority-Based
  
  Server at 90% capacity --> Drop low-priority requests first
  
  Priority Queue:
  [Critical: Payment]  --> Always process
  [High: Checkout]     --> Process if capacity allows
  [Medium: Search]     --> Shed when overloaded
  [Low: Analytics]     --> Shed first
  
  
  Strategy 3: LIFO (Last In, First Out) Shedding
  
  Drop the NEWEST requests (they haven't been waiting yet)
  Process the OLDEST requests (clients are already waiting)
  Reduces overall latency for requests that are served
```

### Implementation

```python
class LoadShedder:
    def __init__(self, max_concurrent=1000, shed_threshold=0.85):
        self.max_concurrent = max_concurrent
        self.shed_threshold = shed_threshold
        self.current_load = AtomicInteger(0)

    def handle_request(self, request):
        load_ratio = self.current_load.get() / self.max_concurrent
        
        if load_ratio > self.shed_threshold:
            priority = self._get_priority(request)
            if priority < Priority.HIGH:
                # Shed low/medium priority requests
                return Response(status=503, body="Service overloaded, try later",
                              headers={"Retry-After": "5"})
        
        self.current_load.increment()
        try:
            return self._process(request)
        finally:
            self.current_load.decrement()
```

---

## 17.9 Idempotency

**Idempotency** ensures that performing the same operation multiple times produces the
same result as performing it once. This makes retries safe.

### Why Idempotency Matters

```
  Without Idempotency:
  
  Client --> "Charge $100" --> Server (processes payment)
         <-- Timeout (response lost in network) --
  Client --> "Charge $100" --> Server (processes AGAIN!)
  
  Result: Customer charged $200! 
  
  
  With Idempotency:
  
  Client --> "Charge $100" (idempotency_key: "abc123") --> Server (processes)
         <-- Timeout --
  Client --> "Charge $100" (idempotency_key: "abc123") --> Server (sees duplicate)
         <-- Returns original result (no double charge) --
  
  Result: Customer charged $100 exactly once.
```

### Implementing Idempotency

```python
class IdempotentPaymentService:
    def __init__(self):
        self.processed_keys = {}  # idempotency_key -> response

    def charge(self, idempotency_key, amount, customer_id):
        # Check if we've already processed this request
        if idempotency_key in self.processed_keys:
            return self.processed_keys[idempotency_key]  # Return cached response
        
        # Process the payment
        result = self.payment_gateway.charge(amount, customer_id)
        
        # Store the result for deduplication
        self.processed_keys[idempotency_key] = result
        
        return result
```

### HTTP Methods and Idempotency

| Method    | Idempotent? | Safe? | Notes                                    |
|-----------|-------------|-------|------------------------------------------|
| GET       | Yes         | Yes   | Reading doesn't change state             |
| PUT       | Yes         | No    | "Set value to X" — same result each time |
| DELETE    | Yes         | No    | "Delete X" — already deleted is OK       |
| POST      | **No**      | No    | "Create X" — could create duplicates     |
| PATCH     | **No**      | No    | Depends on the operation                 |

> **Best Practice:** For non-idempotent operations (POST), require an **idempotency key**
> from the client. Stripe requires `Idempotency-Key` header for all POST requests.

---

## 17.10 Chaos Engineering

**Chaos Engineering** is the practice of intentionally injecting failures into a system
to test its resilience. If you don't test failure scenarios, you'll discover weaknesses
during actual outages — the worst possible time.

### Principles of Chaos Engineering

1. **Start with a hypothesis** — "If Service X goes down, users should see cached results"
2. **Introduce realistic failures** — Network delays, service crashes, disk full
3. **Run experiments in production** — Staging environments hide real-world issues
4. **Minimize blast radius** — Start small, with a subset of traffic
5. **Automate experiments** — Run chaos tests continuously, not just once

### Common Chaos Experiments

```
  +---------------------------------------------------+
  | Chaos Experiment Types                             |
  +---------------------------------------------------+
  | Infrastructure:                                    |
  |   - Kill a VM/container instance                   |
  |   - Fill disk to 100%                              |
  |   - Exhaust CPU or memory                          |
  |   - Terminate an entire availability zone          |
  |                                                    |
  | Network:                                           |
  |   - Add latency (200ms delay)                      |
  |   - Drop packets (10% packet loss)                 |
  |   - Partition network (split-brain scenario)       |
  |   - DNS failure                                    |
  |                                                    |
  | Application:                                       |
  |   - Kill a microservice instance                   |
  |   - Return errors from a dependency                |
  |   - Exhaust database connection pool               |
  |   - Corrupt cache entries                          |
  +---------------------------------------------------+
```

### Chaos Engineering Tools

| Tool                     | Company    | Description                                  |
|--------------------------|------------|----------------------------------------------|
| **Chaos Monkey**         | Netflix    | Randomly kills production instances           |
| **Chaos Kong**           | Netflix    | Simulates entire region failure               |
| **Litmus**               | CNCF       | Kubernetes-native chaos engineering           |
| **Gremlin**              | Gremlin    | Commercial chaos-as-a-service platform        |
| **AWS Fault Injection**  | AWS        | Managed fault injection for AWS resources     |
| **Chaos Mesh**           | PingCAP    | Kubernetes chaos engineering platform         |
| **Toxiproxy**            | Shopify    | TCP proxy for simulating network conditions   |

### Netflix's Chaos Engineering Practices

```
  Netflix Chaos Hierarchy:
  
  Level 1: Chaos Monkey      - Kill random instances
  Level 2: Chaos Gorilla     - Kill an entire availability zone
  Level 3: Chaos Kong        - Kill an entire AWS region
  Level 4: FIT (Failure      - Inject failures at the service level
            Injection Testing)  (specific endpoints, specific errors)
  
  Netflix runs these continuously in production, ensuring that:
  - Services handle instance failures (auto-scaling kicks in)
  - Traffic reroutes when a zone goes down
  - The system fails over to another region gracefully
```

---

## 17.11 Combining Resilience Patterns

In practice, resilience patterns work together. Here's how they compose:

```
  Request Flow with All Patterns:
  
  Client Request
       |
       v
  [Rate Limiter]  -----> 429 if over limit
       |
       v
  [Load Shedder]  -----> 503 if server overloaded
       |
       v
  [Bulkhead]      -----> Isolated thread pool for this dependency
       |
       v
  [Circuit Breaker] ---> Fast-fail if circuit is OPEN
       |
       v
  [Timeout]        -----> Cancel if too slow
       |
       v
  [Retry + Backoff] ----> Retry transient failures (with idempotency key)
       |
       v
  [Fallback]       -----> Return cached/default on final failure
       |
       v
  Response to Client
```

### Real-World Example: Payment Processing

```
  User clicks "Pay Now"
       |
       v
  API Gateway (rate limited: 100 req/min per user)
       |
       v
  Payment Service (bulkhead: 50 threads for payment provider)
       |
       v
  Circuit Breaker (threshold: 5 failures in 30s)
       |
       +-- CLOSED: Call payment provider
       |     |
       |     +-- Timeout: 10 seconds
       |     |
       |     +-- On failure: Retry once with backoff
       |     |     (Idempotency key ensures no double charge)
       |     |
       |     +-- On success: Return confirmation
       |
       +-- OPEN: Try fallback payment provider
       |     |
       |     +-- Fallback succeeds: Process payment
       |     |
       |     +-- Fallback fails: Queue for later processing
       |           (Return "Payment pending" to user)
       |
       v
  Response: "Payment confirmed" or "Payment pending, we'll email you"
```

---

## 17.12 Anti-Patterns to Avoid

| Anti-Pattern                         | Problem                                   | Solution                          |
|--------------------------------------|-------------------------------------------|-----------------------------------|
| **Retry without backoff**            | Amplifies load on failing service         | Use exponential backoff + jitter  |
| **No timeout**                       | Threads blocked indefinitely              | Always set timeouts               |
| **Retry non-idempotent operations**  | Duplicate side effects (double charges)   | Use idempotency keys              |
| **Circuit breaker too sensitive**    | Opens on minor blips                      | Tune thresholds carefully         |
| **Circuit breaker too insensitive**  | Doesn't trip until cascading failure      | Monitor and adjust                |
| **Ignoring partial failures**        | All-or-nothing approach                   | Use fallbacks for non-critical    |
| **Retry storms**                     | Every layer retries: 3 * 3 * 3 = 27 calls| Use retry budgets                 |
| **Cascading timeouts**               | A(30s) -> B(30s) -> C(30s) = 90s total   | Deadline propagation              |

### Retry Storm Amplification

```
  Without Retry Budget:
  
  Service A retries 3x --> Service B retries 3x --> Service C retries 3x
  
  1 original request becomes: 3 * 3 * 3 = 27 requests hitting Service C!
  
  If Service C is already overloaded, retries make it 27x worse.
  
  
  With Retry Budget (20%):
  
  Service A: 100 req/s, 20 retries max
  Service B: 120 req/s, 24 retries max
  Service C: 144 req/s total (manageable)
```

---

## Key Takeaways

1. **Circuit Breaker** is essential for any service calling external dependencies. It
   prevents cascading failures by fast-failing when a dependency is down.

2. **Bulkhead isolation** ensures that a slow or failing dependency doesn't exhaust your
   entire system's resources. Allocate separate thread pools or semaphores per dependency.

3. **Retry with exponential backoff and jitter** handles transient failures gracefully
   while avoiding thundering herd problems.

4. **Always set timeouts** — both connection and read timeouts. A missing timeout is a
   ticking time bomb that will eventually cause resource exhaustion.

5. **Fallback responses** allow graceful degradation. Not every part of a response is
   equally critical — degrade non-essential features before failing entirely.

6. **Idempotency keys** make retries safe for non-idempotent operations. Any operation
   that has side effects (payments, writes) needs idempotency.

7. **Load shedding** is your last line of defense — proactively drop requests when
   overloaded rather than letting the system collapse for everyone.

8. **Chaos Engineering** validates your resilience patterns in production. If you haven't
   tested a failure mode, assume your system doesn't handle it.

9. **Compose patterns together**: Rate Limiting -> Load Shedding -> Bulkhead -> Circuit
   Breaker -> Timeout -> Retry -> Fallback. Each layer catches a different failure mode.

10. **In system design interviews**, discuss resilience patterns when designing any
    distributed system. Show that you've thought about what happens when things go wrong,
    not just when everything works perfectly.

---

*Next Chapter: [Chapter 18 - Monitoring, Logging, and Observability](./18-monitoring.md)*
