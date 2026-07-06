# Chapter 16: Rate Limiting and Throttling

## Introduction

In December 2023, a poorly written script accidentally hammered a public API with 50,000
requests per second. The API's database buckled under the load, and the service went down —
not just for the script's owner, but for every legitimate user.

This is exactly the scenario **rate limiting** prevents. Rate limiting controls how many
requests a client can make in a given time window, protecting your system from abuse, ensuring
fair resource allocation, and maintaining availability for all users.

> **Key Insight:** Rate limiting isn't just about blocking bad actors. It's about protecting
> the system as a whole and ensuring a fair, predictable experience for every user.

---

## 16.1 Rate Limiting vs Throttling vs Load Shedding

These three terms are often confused. Let's clarify:

| Concept             | Definition                                                    | Analogy                        |
|---------------------|---------------------------------------------------------------|--------------------------------|
| **Rate Limiting**   | Rejecting requests that exceed a predefined limit per client  | "You can only enter 5 times/hr"|
| **Throttling**      | Slowing down (not rejecting) requests that exceed the limit   | "Please wait in this queue"    |
| **Load Shedding**   | Dropping requests when the *server* is overloaded, regardless | "Building is full, no one else |
|                     | of per-client limits                                          | can enter right now"           |

```
  Rate Limiting:    Client A sends 100 req/s, limit is 10 req/s
                    -> 10 accepted, 90 rejected with 429

  Throttling:       Client A sends 100 req/s, limit is 10 req/s
                    -> 10 processed immediately, rest queued and processed at 10/s

  Load Shedding:    Server at 95% CPU, all clients affected
                    -> New requests dropped with 503 regardless of client limits
```

---

## 16.2 Why Rate Limiting Matters

### Problems Without Rate Limiting

1. **Denial of Service (DoS)** — A single client can overwhelm your system
2. **Resource starvation** — One heavy user consumes all capacity, starving others
3. **Cascading failures** — Overloaded services trigger downstream failures
4. **Unpredictable costs** — Cloud bills explode from uncontrolled traffic
5. **API abuse** — Scrapers, bots, and competitors abuse your endpoints

### Who Needs Rate Limiting?

- **Public APIs** — Absolutely essential (GitHub, Twitter, Stripe all rate limit)
- **Internal microservices** — Protects against retry storms and misbehaving services
- **Authentication endpoints** — Prevents brute-force attacks (login, OTP verification)
- **Resource-intensive endpoints** — Search, reports, data exports

---

## 16.3 Rate Limiting Algorithms

### Algorithm 1: Token Bucket

The **Token Bucket** is one of the most popular rate limiting algorithms. It allows bursts
of traffic up to a maximum, while maintaining a steady average rate.

**How it works:**
- A bucket holds tokens (up to a maximum capacity)
- Tokens are added at a fixed **refill rate**
- Each request consumes one token
- If the bucket is empty, the request is rejected

```
  Token Bucket Visualization:
  
  Bucket Capacity: 5 tokens
  Refill Rate: 1 token/second
  
  Time 0s: [*][*][*][*][*]  (full — 5 tokens)
  
  Burst of 3 requests:
  Time 1s: [*][*][ ][ ][ ]  (2 tokens left)
  
  1 second passes, 1 token refilled:
  Time 2s: [*][*][*][ ][ ]  (3 tokens)
  
  5 rapid requests:
  Time 2s: [ ][ ][ ][ ][ ]  (0 tokens — next request rejected!)
  
  Wait 5 seconds:
  Time 7s: [*][*][*][*][*]  (full again)
```

**Pseudocode:**

```python
class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity = capacity          # Max tokens
        self.tokens = capacity            # Current tokens
        self.refill_rate = refill_rate    # Tokens added per second
        self.last_refill = now()

    def allow_request(self):
        self._refill()
        if self.tokens >= 1:
            self.tokens -= 1
            return True      # Request allowed
        return False         # Request rejected (429)

    def _refill(self):
        elapsed = now() - self.last_refill
        new_tokens = elapsed * self.refill_rate
        self.tokens = min(self.capacity, self.tokens + new_tokens)
        self.last_refill = now()
```

**Pros:** Allows controlled bursts, smooth, memory-efficient (just 2 values per client)
**Cons:** Slightly more complex than simple counters

**Used by:** Amazon API Gateway, Stripe

---

### Algorithm 2: Leaky Bucket

The **Leaky Bucket** processes requests at a fixed rate, queuing excess requests. Think of
it as a bucket with a small hole at the bottom — water (requests) leaks out at a constant rate.

```
  Leaky Bucket Visualization:
  
  Incoming requests:    |||||||||| (bursty)
                          |
                    +-----v-----+
                    |            |  <- Queue (bucket)
                    |  ||||||||  |     Max size: 10
                    |            |
                    +-----+-----+
                          |
                      [drip drip]  <- Fixed output rate: 2 req/s
                          |
                    +-----v-----+
                    |  Server   |
                    +-----------+
```

**Pseudocode:**

```python
class LeakyBucket:
    def __init__(self, capacity, leak_rate):
        self.capacity = capacity        # Max queue size
        self.queue = []
        self.leak_rate = leak_rate      # Requests processed per second

    def allow_request(self, request):
        self._leak()
        if len(self.queue) < self.capacity:
            self.queue.append(request)
            return True      # Queued for processing
        return False         # Queue full, rejected

    def _leak(self):
        # Remove processed requests based on elapsed time
        elapsed = now() - self.last_leak
        to_remove = int(elapsed * self.leak_rate)
        self.queue = self.queue[to_remove:]
        self.last_leak = now()
```

**Pros:** Guarantees a constant output rate (no bursts reach the server)
**Cons:** Bursts get queued (higher latency), not ideal when bursts are acceptable

**Used by:** Network traffic shaping (e.g., Nginx `limit_req` with `burst`)

---

### Algorithm 3: Fixed Window Counter

The simplest algorithm: count requests in fixed time windows and reject when the count
exceeds the limit.

```
  Fixed Window: 10 requests per minute
  
  Window 1 (00:00-00:59)    Window 2 (01:00-01:59)
  |=====8 requests=====|    |===4 requests===|
  
  Problem — Boundary Burst:
  
  Window 1               Window 2
  |          |||||||||| | ||||||||||          |
  00:00      00:50  00:59 01:00  01:10       01:59
  
  8 requests at 00:50 + 8 requests at 01:05 = 16 requests in 15 seconds!
  Both windows show "within limit" individually, but the user sent
  16 requests in a very short period — double the intended rate.
```

**Pseudocode:**

```python
class FixedWindowCounter:
    def __init__(self, limit, window_size_seconds):
        self.limit = limit
        self.window_size = window_size_seconds
        self.counters = {}  # window_key -> count

    def allow_request(self, client_id):
        window_key = int(now() / self.window_size)
        key = f"{client_id}:{window_key}"
        
        self.counters[key] = self.counters.get(key, 0) + 1
        
        if self.counters[key] <= self.limit:
            return True
        return False
```

**Pros:** Dead simple, minimal memory (one counter per window per client)
**Cons:** Boundary burst problem allows 2x the intended rate at window edges

---

### Algorithm 4: Sliding Window Log

Tracks the **exact timestamp** of every request and counts requests within a sliding window.

```
  Sliding Window Log (limit: 5 requests per 60 seconds)
  
  Request Log for Client A:
  [10:00:15, 10:00:23, 10:00:45, 10:00:52, 10:00:58]
  
  New request arrives at 10:01:10:
  
  1. Remove entries older than (10:01:10 - 60s) = 10:00:10
     -> Remove nothing (10:00:15 is within window)
     -> Log: [10:00:15, 10:00:23, 10:00:45, 10:00:52, 10:00:58]
  
  2. Count = 5, which equals limit
     -> REJECT (429 Too Many Requests)
  
  New request arrives at 10:01:20:
  
  1. Remove entries older than (10:01:20 - 60s) = 10:00:20
     -> Remove 10:00:15
     -> Log: [10:00:23, 10:00:45, 10:00:52, 10:00:58]
  
  2. Count = 4, which is under limit
     -> ALLOW, add 10:01:20 to log
```

**Pseudocode:**

```python
class SlidingWindowLog:
    def __init__(self, limit, window_size_seconds):
        self.limit = limit
        self.window_size = window_size_seconds
        self.logs = {}  # client_id -> sorted list of timestamps

    def allow_request(self, client_id):
        current_time = now()
        window_start = current_time - self.window_size
        
        if client_id not in self.logs:
            self.logs[client_id] = []
        
        # Remove expired entries
        self.logs[client_id] = [
            ts for ts in self.logs[client_id] if ts > window_start
        ]
        
        if len(self.logs[client_id]) < self.limit:
            self.logs[client_id].append(current_time)
            return True
        return False
```

**Pros:** Perfectly accurate, no boundary burst problem
**Cons:** High memory usage (stores every timestamp), expensive cleanup operations

---

### Algorithm 5: Sliding Window Counter (Recommended)

The **best balance** of accuracy and efficiency. It combines fixed window counters with
a weighted calculation based on the overlap with the previous window.

```
  Sliding Window Counter:
  Limit: 10 requests per minute
  
  Previous Window (00:00-00:59): 8 requests
  Current Window  (01:00-01:59): 3 requests
  
  Current time: 01:15 (25% into current window, 75% overlap with previous)
  
  Weighted count = (previous_count * overlap%) + current_count
                 = (8 * 0.75) + 3
                 = 6 + 3
                 = 9
  
  9 < 10 (limit) -> ALLOW
  
  +------------------+------------------+
  |  Previous Window |  Current Window  |
  |  8 requests      |  3 requests      |
  +--+---------------+---------+--------+
     |<--- 75% overlap --->|
     |<-- sliding window -->|
```

**Pseudocode:**

```python
class SlidingWindowCounter:
    def __init__(self, limit, window_size_seconds):
        self.limit = limit
        self.window_size = window_size_seconds
        self.windows = {}  # (client_id, window_key) -> count

    def allow_request(self, client_id):
        current_time = now()
        current_window = int(current_time / self.window_size)
        previous_window = current_window - 1
        
        # How far into the current window are we? (0.0 to 1.0)
        window_progress = (current_time % self.window_size) / self.window_size
        
        prev_count = self.windows.get((client_id, previous_window), 0)
        curr_count = self.windows.get((client_id, current_window), 0)
        
        # Weighted estimate of requests in the sliding window
        estimated_count = (prev_count * (1 - window_progress)) + curr_count
        
        if estimated_count < self.limit:
            self.windows[(client_id, current_window)] = curr_count + 1
            return True
        return False
```

**Pros:** Low memory (just 2 counters per client), accurate (no boundary burst), fast
**Cons:** Approximate (not exact), but close enough for practical use

---

### Algorithm Comparison Table

| Algorithm             | Accuracy  | Memory     | Burst Handling | Complexity | Best For              |
|-----------------------|-----------|------------|----------------|------------|-----------------------|
| Token Bucket          | Good      | Very Low   | Allows bursts  | Medium     | API rate limiting     |
| Leaky Bucket          | Good      | Medium     | Smooths bursts | Medium     | Traffic shaping       |
| Fixed Window Counter  | Low       | Very Low   | Boundary burst! | Low       | Simple/non-critical   |
| Sliding Window Log    | Perfect   | High       | No burst issue | High       | When accuracy critical|
| Sliding Window Counter| High      | Low        | No burst issue | Medium     | General purpose (best)|

---

## 16.4 Where to Implement Rate Limiting

```
  Client --> [CDN/WAF] --> [API Gateway] --> [Application] --> [Database]
               (1)            (2)              (3)
  
  (1) Edge/CDN Layer:     DDoS protection, IP-based blocking
  (2) API Gateway Layer:  Per-client/key rate limiting (most common)
  (3) Application Layer:  Business-logic rate limits (e.g., 5 password attempts)
```

| Location           | Pros                                         | Cons                                   |
|--------------------|----------------------------------------------|----------------------------------------|
| **Client-side**    | Reduces unnecessary requests                 | Easily bypassed, can't trust client    |
| **CDN/WAF edge**   | Blocks attacks before reaching infrastructure| Limited customization, coarse-grained  |
| **API Gateway**    | Centralized, consistent, per-key/user limits | Single point, may add latency          |
| **Application**    | Full business context, fine-grained          | Must implement in every service        |
| **Middleware**      | Reusable across services (e.g., Express middleware) | Still per-service deployment    |

> **Best Practice:** Implement rate limiting at the **API gateway** for general protection
> and at the **application layer** for business-specific rules. Use CDN/WAF for DDoS.

---

## 16.5 Distributed Rate Limiting

In a distributed system with multiple server instances, rate limiting becomes challenging
because request counts must be shared across nodes.

### The Problem

```
  Without Shared State:
  
  Client sends 10 req/s (limit: 5 req/s)
  
  Server 1 sees: 3 req/s  -> "Under limit, allow all"
  Server 2 sees: 3 req/s  -> "Under limit, allow all"
  Server 3 sees: 4 req/s  -> "Under limit, allow all"
  
  Result: 10 req/s allowed! Limit completely bypassed.
```

### Solution: Redis-Based Distributed Rate Limiting

Use a centralized store (Redis) to maintain shared counters:

```
  +----------+     +----------+     +----------+
  | Server 1 +---->|          |<----+ Server 3 |
  +----------+     |  Redis   |     +----------+
                   |  Cluster |
  +----------+     |          |
  | Server 2 +---->| counter: |
  +----------+     | user123  |
                   | = 47     |
                   +----------+
```

**Redis Sliding Window Implementation:**

```python
import redis

def is_rate_limited(client_id, limit, window_seconds):
    r = redis.Redis()
    key = f"rate_limit:{client_id}"
    current_time = time.time()
    window_start = current_time - window_seconds

    pipe = r.pipeline()
    # Remove old entries
    pipe.zremrangebyscore(key, 0, window_start)
    # Count entries in current window
    pipe.zcard(key)
    # Add current request
    pipe.zadd(key, {str(current_time): current_time})
    # Set expiry on the key
    pipe.expire(key, window_seconds)
    
    results = pipe.execute()
    request_count = results[1]
    
    return request_count >= limit
```

### Challenges with Distributed Rate Limiting

| Challenge               | Description                                    | Mitigation                     |
|-------------------------|------------------------------------------------|--------------------------------|
| **Latency**             | Network round-trip to Redis on every request   | Local cache + async sync       |
| **Redis failure**       | If Redis is down, rate limiting stops working   | Fail-open or fail-closed policy|
| **Race conditions**     | Concurrent requests may read stale counts      | Use Redis Lua scripts (atomic) |
| **Clock skew**          | Different servers have different times          | Use Redis server time, not local|
| **Multi-region**        | Syncing counters across regions adds latency   | Per-region limits or async sync|

### Lua Script for Atomic Rate Limiting in Redis

```lua
-- Atomic sliding window rate limiter in Redis
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Remove expired entries
redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

-- Count current entries
local count = redis.call('ZCARD', key)

if count < limit then
    redis.call('ZADD', key, now, now .. math.random())
    redis.call('EXPIRE', key, window)
    return 0  -- Allowed
else
    return 1  -- Rate limited
end
```

---

## 16.6 Rate Limit Headers

Communicate rate limit status to clients via HTTP response headers:

```
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 67
X-RateLimit-Reset: 1640995200
```

| Header                      | Description                                        | Example          |
|-----------------------------|----------------------------------------------------|------------------|
| `X-RateLimit-Limit`        | Maximum requests allowed in the window             | `100`            |
| `X-RateLimit-Remaining`    | Requests remaining in current window               | `67`             |
| `X-RateLimit-Reset`        | Unix timestamp when the window resets              | `1640995200`     |
| `Retry-After`              | Seconds to wait before retrying (on 429)           | `30`             |

### 429 Too Many Requests Response

```json
HTTP/1.1 429 Too Many Requests
Retry-After: 30
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1640995200
Content-Type: application/json

{
    "error": {
        "code": "RATE_LIMITED",
        "message": "Rate limit exceeded. Please retry after 30 seconds.",
        "retry_after": 30
    }
}
```

---

## 16.7 Rate Limiting Dimensions

You can rate limit by different identifiers depending on the use case:

| Dimension        | Use Case                                    | Example Limit              |
|------------------|---------------------------------------------|----------------------------|
| **IP Address**   | Anonymous users, DDoS protection            | 100 requests/minute/IP     |
| **User ID**      | Authenticated user fairness                 | 1,000 requests/hour/user   |
| **API Key**      | Third-party developer plans                 | 10,000 requests/day/key    |
| **Endpoint**     | Protect expensive operations                | 10 requests/minute to /search |
| **Organization** | Enterprise plan limits                      | 100,000 requests/day/org   |
| **Geographic**   | Regional compliance or protection           | Stricter limits from high-risk regions |

### Tiered Rate Limiting Example

```
  Free Tier:         100 requests/hour,    1,000/day
  Basic Tier:      1,000 requests/hour,   10,000/day
  Pro Tier:       10,000 requests/hour,  100,000/day
  Enterprise:     Custom limits with dedicated capacity
```

---

## 16.8 Handling Rate-Limited Requests (Client Side)

### Exponential Backoff with Jitter

When a client receives a `429`, it should retry intelligently:

```
  Attempt 1: Wait 1s    + random(0, 0.5s)  = ~1.3s
  Attempt 2: Wait 2s    + random(0, 1.0s)  = ~2.7s
  Attempt 3: Wait 4s    + random(0, 2.0s)  = ~5.1s
  Attempt 4: Wait 8s    + random(0, 4.0s)  = ~10.3s
  Attempt 5: Wait 16s   + random(0, 8.0s)  = ~20.7s
  (Cap at max backoff, e.g., 60s)
```

**Why jitter?** Without jitter, all rate-limited clients retry at the same time, creating
a **thundering herd** that overwhelms the server again.

```python
import time
import random

def retry_with_backoff(func, max_retries=5, base_delay=1, max_delay=60):
    for attempt in range(max_retries):
        try:
            return func()
        except RateLimitError as e:
            if attempt == max_retries - 1:
                raise
            
            # Exponential backoff with full jitter
            delay = min(max_delay, base_delay * (2 ** attempt))
            jitter = random.uniform(0, delay)
            
            print(f"Rate limited. Retrying in {jitter:.1f}s (attempt {attempt + 1})")
            time.sleep(jitter)
```

---

## 16.9 Real-World Rate Limiting Examples

### GitHub API

```
  Authenticated:    5,000 requests/hour (per user token)
  Unauthenticated:  60 requests/hour (per IP)
  Search API:       30 requests/minute
  GraphQL:          5,000 points/hour (query complexity-based)
  
  Headers returned:
  X-RateLimit-Limit: 5000
  X-RateLimit-Remaining: 4987
  X-RateLimit-Reset: 1372700873
```

### Twitter (X) API

```
  v2 Basic:     10,000 tweets read/month
  v2 Pro:       1,000,000 tweets read/month
  Per endpoint: 15-900 requests per 15-minute window
  
  App-level vs User-level limits applied separately
```

### Stripe API

```
  Live mode:    100 read requests/second, 100 write requests/second
  Test mode:    25 requests/second
  
  Stripe uses Token Bucket algorithm
  Returns: Retry-After header on 429
```

### Rate Limiting Architecture at Scale (Real-World Pattern)

```
  +--------+     +---------+     +-------------+     +-----------+
  | Client +---->| CDN/WAF +---->| API Gateway +---->| Service   |
  +--------+     +----+----+     +------+------+     +-----+-----+
                      |                 |                   |
                 DDoS defense     +-----+------+      Business
                 IP blocking      | Redis Rate |      logic limits
                                  | Limiter    |
                                  +------------+
  
  Layer 1 (CDN):       Block known bad IPs, basic DDoS protection
  Layer 2 (Gateway):   Per-key, per-user rate limits via Redis
  Layer 3 (Service):   Business rules (e.g., max 5 login attempts)
```

---

## 16.10 Advanced Topics

### Rate Limiting in Microservices

In a microservice architecture, consider:

1. **Gateway-level limiting** — Protects the entire system
2. **Service-to-service limiting** — Prevents one service from overwhelming another
3. **Retry budgets** — Each service limits how many requests it will retry (e.g., 20%)
   to prevent retry storms

### Adaptive Rate Limiting

Instead of fixed limits, adjust dynamically based on server health:

```
  Server Load < 50%:   Allow 1000 req/s per client
  Server Load 50-80%:  Allow 500 req/s per client
  Server Load > 80%:   Allow 100 req/s per client
  Server Load > 95%:   Activate load shedding (reject new requests)
```

### Cost of NOT Rate Limiting

| Risk                    | Impact                                           | Severity |
|-------------------------|--------------------------------------------------|----------|
| DDoS attack             | Complete service outage                          | Critical |
| Scraping/data theft     | Competitive data loss, legal liability            | High     |
| Runaway automation      | Unpredictable cloud bills ($100K+ incidents)     | High     |
| Noisy neighbor          | Other users experience degraded performance       | Medium   |
| Brute-force attacks     | Account compromises                               | High     |

---

## Key Takeaways

1. **Rate limiting protects systems** from abuse, ensures fairness, and prevents cascading
   failures. It's a fundamental requirement for any production API.

2. **Sliding Window Counter** is the recommended algorithm for most use cases — it's
   accurate, memory-efficient, and avoids the boundary-burst problem of fixed windows.

3. **Token Bucket** is ideal when you want to allow controlled bursts while maintaining
   an average rate limit.

4. **Implement rate limiting at the API gateway** for centralized enforcement, and at the
   **application layer** for business-specific rules.

5. **Distributed rate limiting** requires a shared store (Redis) with atomic operations
   (Lua scripts) to prevent race conditions.

6. **Always return rate limit headers** (`X-RateLimit-*`, `Retry-After`) so clients can
   implement proper backoff behavior.

7. **Clients should use exponential backoff with jitter** when retrying rate-limited
   requests to avoid thundering herd problems.

8. **Rate limit by the right dimension** — IP for anonymous traffic, API key for developer
   plans, user ID for authenticated fairness, endpoint for expensive operations.

9. **Consider adaptive rate limiting** that adjusts based on server health for more
   resilient systems.

10. **In system design interviews**, mention rate limiting when discussing API design,
    security, or resilience. Show you understand the algorithms and trade-offs.

---

*Next Chapter: [Chapter 17 - Resilience Patterns](./17-resilience-patterns.md)*
