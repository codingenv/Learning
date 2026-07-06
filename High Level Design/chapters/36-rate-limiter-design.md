# Chapter 36 — Design a Rate Limiter

> *"A well-designed rate limiter is the bouncer at the club door — it keeps the party going*
> *for everyone by ensuring no single guest takes over the dance floor."*

Rate limiting is one of the most **frequently asked** system design questions and one
of the most **critical** infrastructure components in any production system. In this
chapter we build a rate limiter from first principles — exploring algorithms, distributed
challenges, and production-grade architecture.

---

## 1. Problem Statement

### What Is a Rate Limiter?

A **rate limiter** controls the number of requests a client can send to a server within
a defined time window. When the limit is exceeded, excess requests are **throttled** —
typically rejected with an HTTP `429 Too Many Requests` response.

```
    Normal Traffic                    Attack / Abuse
  ┌──────────────┐               ┌──────────────────┐
  │ ░░░░░░░░░░░░ │  ← OK        │ ████████████████ │  ← Overload!
  │ ░░░░░░░░░░░░ │               │ ████████████████ │
  │ ░░░░         │               │ ████████████████ │
  └──────────────┘               └──────────────────┘
     ~200 req/s                      ~50,000 req/s

          WITH Rate Limiter:
  ┌──────────────────┐       ┌───────────┐       ┌──────────────┐
  │  50,000 req/s    │──────>│  RATE      │──────>│  200 req/s   │
  │  (incoming)      │       │  LIMITER   │       │  (allowed)   │
  └──────────────────┘       └─────┬─────┘       └──────────────┘
                                   │
                                   v
                            ┌──────────────┐
                            │ 49,800 req/s │
                            │  (rejected   │
                            │   with 429)  │
                            └──────────────┘
```

### Why Is It Needed?

| Reason                     | Description                                                  |
|----------------------------|--------------------------------------------------------------|
| **DDoS Protection**        | Prevents malicious actors from overwhelming the system       |
| **Cost Control**           | Limits expensive API calls (e.g., $0.01/call AI endpoints)   |
| **API Abuse Prevention**   | Stops scrapers, bots, and credential-stuffing attacks        |
| **Fair Usage**             | Ensures all users get equitable access to shared resources   |
| **Revenue / Tiering**      | Enforces free vs. paid plan usage limits                     |
| **Cascade Prevention**     | Stops one misbehaving service from crashing dependencies     |

### Real-World Rate Limits

| Service          | Rate Limit                        | Window     | Identifier  |
|------------------|-----------------------------------|------------|-------------|
| **GitHub API**   | 5,000 requests                    | Per hour   | Auth token  |
| **Twitter/X**    | 300 tweets                        | Per 3 hrs  | User        |
| **Stripe API**   | 100 requests                      | Per second | API key     |
| **Google Maps**  | 50,000 requests                   | Per day    | API key     |
| **Discord**      | 50 messages                       | Per 10 sec | User+channel|
| **AWS API GW**   | 10,000 requests (default)         | Per second | Account     |

---

## 2. Functional Requirements

| #  | Requirement                        | Details                                                    |
|----|------------------------------------|------------------------------------------------------------|
| F1 | **Limit by identifier**            | Support IP address, user ID, API key, or custom key        |
| F2 | **Configurable rate limits**       | Rules defined per endpoint, user tier, HTTP method          |
| F3 | **HTTP 429 response**              | Return `429 Too Many Requests` when limit is exceeded      |
| F4 | **Rate limit headers**             | Include `X-RateLimit-Limit`, `X-RateLimit-Remaining`,      |
|    |                                    | `X-RateLimit-Reset` in every response                      |
| F5 | **Multiple simultaneous rules**    | Apply per-user AND per-IP AND global limits concurrently    |
| F6 | **Soft vs hard limits**            | Option for graceful degradation (queue) vs hard rejection   |
| F7 | **Whitelist / Blacklist**          | Bypass limits for trusted IPs; instant-block known abusers  |

### Example Response Headers

```
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 57
X-RateLimit-Reset: 1672531260
X-RateLimit-Policy: 100;w=60

---

HTTP/1.1 429 Too Many Requests
Retry-After: 23
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1672531260
Content-Type: application/json

{"error": "rate_limit_exceeded", "message": "Too many requests. Retry after 23 seconds."}
```

---

## 3. Non-Functional Requirements

| #   | Requirement                | Target                                                     |
|-----|----------------------------|------------------------------------------------------------|
| NF1 | **Low latency**            | < 1 ms added to each request (rate check must be fast)     |
| NF2 | **High availability**      | 99.99% uptime; failure should NOT block all traffic        |
| NF3 | **Fault tolerance**        | If rate limiter is down, fail-open (allow traffic through)  |
| NF4 | **Minimal memory**         | O(1) per client ideally, not O(n) per request              |
| NF5 | **Accurate counting**      | Minimize false positives (blocking legit users)            |
| NF6 | **Scalability**            | Handle millions of unique clients and 100K+ RPS            |
| NF7 | **Consistency**            | Distributed counters should be eventually consistent       |

### Fail-Open vs Fail-Closed

```
  Rate Limiter HEALTHY                 Rate Limiter DOWN
  ┌─────────────────┐                 ┌─────────────────┐
  │ Check limit     │                 │ FAIL-OPEN:      │
  │   ├─ Allow ✓    │                 │   Allow all ✓   │
  │   └─ Reject ✗   │                 │   (risky but    │
  └─────────────────┘                 │    available)   │
                                      ├─────────────────┤
                                      │ FAIL-CLOSED:    │
                                      │   Reject all ✗  │
                                      │   (safe but     │
                                      │    unavailable) │
                                      └─────────────────┘

  RECOMMENDATION: Fail-open in production. A brief period without
  rate limiting is better than a total outage.
```

---

## 4. Where to Place the Rate Limiter

### Placement Options

```
  Option A: Client-Side           Option B: Server-Side Middleware
  ┌────────┐                      ┌────────┐   ┌────────────┐   ┌────────┐
  │ Client │──self-throttle──X    │ Client │──>│ Rate Limit │──>│ Server │
  │(unrely)│                      │        │   │ Middleware  │   │        │
  └────────┘                      └────────┘   └────────────┘   └────────┘

  Option C: API Gateway           Option D: Standalone Service
  ┌────────┐   ┌───────────┐      ┌────────┐   ┌───────────┐   ┌────────┐
  │ Client │──>│ API GW    │      │ Client │──>│ Rate Limit│──>│ API GW │
  │        │   │ (has rate  │     │        │   │ Service   │   │        │
  │        │   │  limiting) │     │        │   │ (gRPC)    │   │        │
  └────────┘   └─────┬─────┘      └────────┘   └───────────┘   └────┬───┘
                     │                                               │
                     v                                               v
               ┌──────────┐                                    ┌──────────┐
               │  Server  │                                    │  Server  │
               └──────────┘                                    └──────────┘

  Option E: Cloud-Based (AWS WAF, Cloudflare, Akamai)
  ┌────────┐   ┌──────────────┐   ┌───────────┐   ┌────────┐
  │ Client │──>│ CDN / WAF    │──>│ API GW    │──>│ Server │
  │        │   │ (Cloudflare) │   │           │   │        │
  │        │   │ Rate limited │   │           │   │        │
  └────────┘   └──────────────┘   └───────────┘   └────────┘
```

### Comparison

| Placement               | Latency  | Control | Complexity | Bypass Risk |
|--------------------------|----------|---------|------------|-------------|
| Client-side              | None     | Low     | Low        | **High**    |
| Server-side middleware   | Very Low | High    | Medium     | Low         |
| API Gateway              | Low      | Medium  | Low        | Low         |
| Standalone service       | Medium   | **High**| High       | Low         |
| Cloud-based (WAF/CDN)   | Low      | Low     | **Lowest** | Low         |

### Recommendation

- **API Gateway** — Best for most companies. Kong, AWS API Gateway, and Envoy all
  have built-in rate limiting. Zero custom code.
- **Standalone service** — Best for complex multi-rule systems, A/B testing rate
  limits, or when you need fine-grained per-endpoint control.
- **Cloud-based** — Best first line of defense against volumetric DDoS; combine
  with server-side for application-layer limits.

---

## 5. Rate Limiting Algorithms — Deep Dive

### 5.1 Token Bucket

The **Token Bucket** is the most widely used algorithm (used by Amazon and Stripe).
Imagine a bucket that holds tokens. Tokens are added at a fixed rate. Each request
consumes one token. If the bucket is empty, the request is rejected.

```
  TOKEN BUCKET ALGORITHM
  ══════════════════════

  Refill: 4 tokens/sec         Bucket capacity: 10 tokens
  ┌─────────────────────────────────────────────────────────────┐
  │                                                             │
  │    Refiller (4/sec)                                         │
  │        │                                                    │
  │        ▼                    Bucket (max 10)                 │
  │    ┌───────┐           ┌─────────────────────┐              │
  │    │ + + + │──────────>│ ● ● ● ● ● ● ○ ○ ○ ○│              │
  │    │tokens │           │  6 tokens remaining  │              │
  │    └───────┘           └──────────┬──────────┘              │
  │                                   │                         │
  │                          Request arrives                    │
  │                                   │                         │
  │                          ┌────────▼────────┐                │
  │                          │ tokens > 0 ?    │                │
  │                          └───┬─────────┬───┘                │
  │                          YES │         │ NO                 │
  │                              ▼         ▼                    │
  │                        ┌─────────┐ ┌──────────┐             │
  │                        │ Allow   │ │ Reject   │             │
  │                        │ tokens--│ │ HTTP 429 │             │
  │                        └─────────┘ └──────────┘             │
  └─────────────────────────────────────────────────────────────┘

  Timeline Example:
  ─────────────────
  Time:  0s   0.25s  0.5s   0.75s  1.0s   1.25s
  Tokens: 10    9      7      6      10     8
  Reqs:   -1   -2     -1     +4     -2     -
                              refill!
```

**Parameters:**
- `bucket_size` — Maximum number of tokens (controls burst size)
- `refill_rate` — Tokens added per second (controls sustained throughput)

**Pseudocode:**

```python
class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity    = capacity       # max tokens
        self.tokens      = capacity       # current tokens
        self.refill_rate = refill_rate    # tokens per second
        self.last_refill = now()

    def allow_request(self):
        self._refill()
        if self.tokens >= 1:
            self.tokens -= 1
            return True   # ALLOW
        return False      # REJECT (429)

    def _refill(self):
        elapsed = now() - self.last_refill
        new_tokens = elapsed * self.refill_rate
        self.tokens = min(self.capacity, self.tokens + new_tokens)
        self.last_refill = now()
```

**Separate Buckets Per Rule:**

```
  User "alice" hitting 3 endpoints:
  ┌───────────────────────────────────────────────┐
  │  GET  /api/posts    → Bucket A  (100/min)     │
  │  POST /api/posts    → Bucket B  (10/min)      │
  │  GET  /api/search   → Bucket C  (30/min)      │
  │  GLOBAL (all reqs)  → Bucket D  (500/min)     │
  └───────────────────────────────────────────────┘
  Each bucket is independent. A request must pass ALL applicable buckets.
```

| Pros                              | Cons                                    |
|-----------------------------------|-----------------------------------------|
| ✅ Allows controlled bursts        | ❌ Two parameters to tune               |
| ✅ Memory efficient (2 vars/user)  | ❌ Distributed sync is non-trivial      |
| ✅ Simple implementation           |                                         |
| ✅ Used by AWS, Stripe, etc.       |                                         |

---

### 5.2 Leaky Bucket

The **Leaky Bucket** processes requests at a **fixed, constant rate** regardless of
incoming traffic volume. Think of it as a queue with a fixed drain rate — requests
flow in at any speed but leak out at a constant rate.

```
  LEAKY BUCKET ALGORITHM
  ══════════════════════

                      Incoming requests (variable rate)
                           │ │ │ │ │ │
                           ▼ ▼ ▼ ▼ ▼ ▼
                    ┌──────────────────────┐
                    │ ┌──┐┌──┐┌──┐┌──┐    │ ← Queue (FIFO)
     Overflow! ←────│ │R7││R6││R5││R4│    │    capacity = 4
     (rejected)     │ └──┘└──┘└──┘└──┘    │
                    └──────────┬───────────┘
                               │
                          Drain (fixed rate)
                          1 request / 100ms
                               │
                               ▼
                    ┌──────────────────────┐
                    │      Server          │
                    │   (constant load)    │
                    └──────────────────────┘

  Timeline:
  ─────────
  t=0ms:   [R1, R2, R3] arrive → Queue: [R1, R2, R3]  → Process R1
  t=100ms: [R4] arrives        → Queue: [R2, R3, R4]   → Process R2
  t=150ms: [R5, R6] arrive     → Queue: [R3, R4, R5, R6] (full!)
  t=160ms: [R7] arrives        → REJECTED (queue full)
  t=200ms: drain               → Queue: [R4, R5, R6]   → Process R3
```

**Pseudocode:**

```python
class LeakyBucket:
    def __init__(self, capacity, drain_rate):
        self.capacity   = capacity     # max queue size
        self.drain_rate = drain_rate   # requests per second
        self.queue      = deque()
        self.last_drain = now()

    def allow_request(self, request):
        self._drain()
        if len(self.queue) < self.capacity:
            self.queue.append(request)
            return True    # QUEUED (will be processed)
        return False       # REJECT (queue full, 429)

    def _drain(self):
        elapsed = now() - self.last_drain
        to_drain = int(elapsed * self.drain_rate)
        for _ in range(min(to_drain, len(self.queue))):
            self.queue.popleft()   # process request
        if to_drain > 0:
            self.last_drain = now()
```

| Pros                                     | Cons                                        |
|------------------------------------------|---------------------------------------------|
| ✅ Smooth, constant output rate           | ❌ Does NOT allow bursts                    |
| ✅ Predictable server load                | ❌ Old requests in queue delay new ones     |
| ✅ Simple to reason about                 | ❌ Requires a queue (more memory)           |
|                                          | ❌ No guarantee of timely processing        |

---

### 5.3 Fixed Window Counter

Divide time into **fixed windows** (e.g., 60-second intervals) and keep a counter
per window. If the counter exceeds the limit, reject requests until the next window.

```
  FIXED WINDOW COUNTER
  ════════════════════

  Limit: 5 requests per 60-second window

  Window 1 (00:00-01:00)      Window 2 (01:00-02:00)      Window 3 (02:00-03:00)
  ┌──────────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐
  │  ██ ██ ██ ██ ██      │    │  ██ ██ ██            │    │  ██ ██ ██ ██ ██      │
  │  1  2  3  4  5       │    │  1  2  3             │    │  1  2  3  4  5       │
  │  ─ ─ ─ ─ ─ LIMIT ─  │    │                      │    │  ─ ─ ─ ─ ─ LIMIT ─  │
  │  NEXT REQUEST → 429  │    │  Requests allowed ✓  │    │  NEXT REQUEST → 429  │
  └──────────────────────┘    └──────────────────────┘    └──────────────────────┘

  THE BOUNDARY BURST PROBLEM:
  ───────────────────────────
  Limit: 5 req / minute

        Window 1                      Window 2
  ├─────────────────────────┤├─────────────────────────┤
  │                    █████││█████                     │
  │              5 reqs at  ││5 reqs at                 │
  │              00:59      ││01:00                     │
  ├─────────────────────────┤├─────────────────────────┤
                         ▲  ▲
                         └──┘
                   10 requests in 2 seconds!
                   That is 2x the intended limit!
```

**Implementation with Redis:**

```python
class FixedWindowCounter:
    def __init__(self, redis, limit, window_sec):
        self.redis      = redis
        self.limit      = limit
        self.window_sec = window_sec

    def allow_request(self, client_id):
        window = int(now() / self.window_sec)
        key = f"rate:{client_id}:{window}"

        # INCR is atomic in Redis
        count = self.redis.incr(key)

        if count == 1:
            self.redis.expire(key, self.window_sec)

        if count <= self.limit:
            return True    # ALLOW
        return False       # REJECT
```

**Redis Commands (Atomic):**
```
MULTI
  INCR   rate_limit:user123:1672531200
  EXPIRE rate_limit:user123:1672531200 60
EXEC
```

| Pros                                  | Cons                                         |
|---------------------------------------|----------------------------------------------|
| ✅ Very simple implementation          | ❌ Boundary burst problem (2x limit)         |
| ✅ Memory efficient (1 counter/window) | ❌ Not smooth traffic shaping                |
| ✅ Fast (single Redis INCR)            |                                              |

---

### 5.4 Sliding Window Log

Keep a **sorted log of timestamps** for each request. To check the limit, count
how many timestamps fall within the current window.

```
  SLIDING WINDOW LOG
  ══════════════════

  Limit: 5 requests per 60 seconds
  Current time: 01:25

  Request Log (sorted set of timestamps):
  ┌────────────────────────────────────────────────────────┐
  │  00:20  00:45  00:58  01:02  01:15  01:20  01:24      │
  │  ╳      ╳      ╳      ✓      ✓      ✓      ✓         │
  │  expired expired expired  ← within last 60 sec →      │
  └────────────────────────────────────────────────────────┘
         remove these              count these = 4

  Sliding window: [00:25 ────────────────────────── 01:25]
                         ◄──── 60 seconds ────►

  New request at 01:25:
    1. Remove timestamps older than (01:25 - 60s) = 00:25
    2. Count remaining = 4
    3. 4 < 5 (limit) → ALLOW ✓
    4. Add 01:25 to the log

  After:
  ┌────────────────────────────────────────────────────────┐
  │  01:02  01:15  01:20  01:24  01:25                     │
  │  count = 5   → next request will be REJECTED           │
  └────────────────────────────────────────────────────────┘
```

**Implementation with Redis Sorted Set:**

```python
class SlidingWindowLog:
    def __init__(self, redis, limit, window_sec):
        self.redis      = redis
        self.limit      = limit
        self.window_sec = window_sec

    def allow_request(self, client_id):
        key = f"rate:{client_id}"
        current = now_ms()
        window_start = current - (self.window_sec * 1000)

        pipe = self.redis.pipeline()
        pipe.zremrangebyscore(key, 0, window_start)     # remove expired
        pipe.zadd(key, {str(current): current})         # add current
        pipe.zcard(key)                                  # count
        pipe.expire(key, self.window_sec)               # TTL
        results = pipe.execute()

        count = results[2]
        if count <= self.limit:
            return True    # ALLOW
        # Remove the just-added entry since we're rejecting
        self.redis.zrem(key, str(current))
        return False       # REJECT
```

| Pros                                      | Cons                                      |
|-------------------------------------------|-------------------------------------------|
| ✅ Very accurate — no boundary problem     | ❌ Memory-heavy: stores EVERY timestamp   |
| ✅ Precise sliding window                  | ❌ O(n) storage per client per window     |
|                                           | ❌ Expensive cleanup operations           |

**Memory Impact:** For 1M users, 1000 req/hr limit, storing 8-byte timestamps:
`1M × 1000 × 8 bytes = 8 GB` — often impractical!

---

### 5.5 Sliding Window Counter ⭐ (Recommended)

This is a **hybrid** of Fixed Window Counter and Sliding Window Log. It provides the
accuracy of a sliding window with the memory efficiency of fixed counters.

```
  SLIDING WINDOW COUNTER
  ══════════════════════

  Limit: 100 requests per 60 seconds
  Current time: 01:25 (25 seconds into current window)

   Previous Window (00:00 - 01:00)    Current Window (01:00 - 02:00)
  ┌──────────────────────────────┐   ┌──────────────────────────────┐
  │         84 requests          │   │  36 requests (so far)        │
  │                              │   │                              │
  └──────────────────────────────┘   └──────────────────────────────┘
                                          ▲
                                     we are here (01:25)

  Overlap of previous window with current sliding window:
  ═══════════════════════════════════════════════════════

  Sliding window: [00:25 ──────────────────────── 01:25]
                   │◄── 35 sec in prev ──►│◄ 25 sec ►│
                   │    window             │ current   │
                   │                       │ window    │

  overlap_percentage = (60 - 25) / 60 = 35/60 ≈ 0.583

  ┌─────────────────────────────────────────────────────────┐
  │                                                         │
  │  weighted_count = prev_count × overlap% + curr_count    │
  │  weighted_count = 84 × 0.583 + 36                      │
  │  weighted_count = 48.97 + 36                            │
  │  weighted_count = 84.97                                 │
  │                                                         │
  │  84.97 < 100 (limit) → ALLOW ✓                         │
  │                                                         │
  └─────────────────────────────────────────────────────────┘
```

**Implementation:**

```python
class SlidingWindowCounter:
    def __init__(self, redis, limit, window_sec):
        self.redis      = redis
        self.limit      = limit
        self.window_sec = window_sec

    def allow_request(self, client_id):
        current   = now()
        window    = int(current / self.window_sec)
        prev_win  = window - 1

        key_curr = f"rate:{client_id}:{window}"
        key_prev = f"rate:{client_id}:{prev_win}"

        # Get both counters
        curr_count = int(self.redis.get(key_curr) or 0)
        prev_count = int(self.redis.get(key_prev) or 0)

        # Calculate position in current window
        elapsed = current - (window * self.window_sec)
        overlap = (self.window_sec - elapsed) / self.window_sec

        # Weighted count
        weighted = prev_count * overlap + curr_count

        if weighted < self.limit:
            self.redis.incr(key_curr)
            self.redis.expire(key_curr, self.window_sec * 2)
            return True    # ALLOW
        return False       # REJECT
```

| Pros                                    | Cons                                        |
|-----------------------------------------|---------------------------------------------|
| ✅ Memory efficient (2 counters only)    | ❌ Approximate (assumes even distribution)  |
| ✅ Smooth — no boundary burst problem    | ❌ Slightly more complex than fixed window  |
| ✅ Fast (2 Redis GETs + 1 INCR)          |                                             |
| ✅ Best accuracy-to-memory ratio          |                                             |

> **This is the recommended algorithm for most production systems.**
> Cloudflare uses this approach for their global rate limiting infrastructure.

---

### Algorithm Comparison Table

```
┌─────────────────────┬───────────┬──────────┬────────────┬───────────┬──────────────┐
│     Algorithm       │  Memory   │ Accuracy │   Burst    │Complexity │   Best For   │
│                     │  (per     │          │  Handling  │           │              │
│                     │  client)  │          │            │           │              │
├─────────────────────┼───────────┼──────────┼────────────┼───────────┼──────────────┤
│ Token Bucket        │  O(1)     │ Good     │ ✅ Allows  │ Low       │ API rate     │
│                     │  8 bytes  │          │   bursts   │           │ limiting     │
├─────────────────────┼───────────┼──────────┼────────────┼───────────┼──────────────┤
│ Leaky Bucket        │  O(n)     │ Good     │ ❌ No      │ Low       │ Traffic      │
│                     │  queue    │          │   bursts   │           │ shaping      │
├─────────────────────┼───────────┼──────────┼────────────┼───────────┼──────────────┤
│ Fixed Window        │  O(1)     │ Low      │ ⚠️ 2x at  │ Very Low  │ Simple       │
│ Counter             │  4 bytes  │          │  boundary  │           │ throttling   │
├─────────────────────┼───────────┼──────────┼────────────┼───────────┼──────────────┤
│ Sliding Window      │  O(n)     │ ✅ Best  │ ✅ Smooth  │ Medium    │ When memory  │
│ Log                 │ per req   │          │            │           │ isn't issue  │
├─────────────────────┼───────────┼──────────┼────────────┼───────────┼──────────────┤
│ Sliding Window      │  O(1)     │ High     │ ✅ Smooth  │ Medium    │ ⭐Production │
│ Counter             │  8 bytes  │ (approx) │            │           │ recommended  │
└─────────────────────┴───────────┴──────────┴────────────┴───────────┴──────────────┘
```

---

## 6. Distributed Rate Limiting

This is the **hard problem**. When you have multiple API servers behind a load
balancer, how do you enforce a global rate limit?

### The Race Condition Problem

```
  WITHOUT distributed coordination:

  Client: user123 (limit: 10 req/min, currently at 9)

         ┌──────────────┐
         │ Load Balancer │
         └──┬────────┬──┘
            │        │
      ┌─────▼──┐  ┌──▼─────┐       Both servers see count = 9
      │Server 1│  │Server 2│       Both allow the request
      │count=9 │  │count=9 │       Both increment to 10
      │ALLOW!  │  │ALLOW!  │
      └────────┘  └────────┘       RESULT: 11 requests allowed!
                                    (limit was 10) ← BUG!
```

### Approach 1: Centralized Redis Counter

```
  ┌────────┐     ┌────────────┐     ┌────────────┐
  │Client  │────>│Load Balancer│────>│  Server 1  │──┐
  └────────┘     └────────────┘     └────────────┘  │
                        │           ┌────────────┐  │  ┌─────────────────┐
                        └──────────>│  Server 2  │──┼─>│ Redis (single)  │
                                    └────────────┘  │  │ INCR is atomic! │
                                    ┌────────────┐  │  │                 │
                                    │  Server 3  │──┘  │ count=9 → 10   │
                                    └────────────┘     │ ALLOW           │
                                                       │ count=10 → 11  │
                                                       │ REJECT (>10)   │
                                                       └─────────────────┘

  ✅ Accurate: Redis INCR is atomic (single-threaded)
  ❌ SPOF: If Redis goes down, rate limiting is lost
  ❌ Latency: Network round-trip to Redis for every request (~0.5ms)
```

### Approach 2: Sticky Sessions (Per-Server Limits)

```
  Limit: 100 req/min → Split across 4 servers → 25/server each

  ┌────────────┐
  │   Client   │ ──(hash by IP)──┐
  └────────────┘                 │
                                 ▼
  ┌──────────────────────────────────────────────┐
  │              Load Balancer                    │
  │         (consistent hashing)                  │
  └──┬─────────┬─────────┬─────────┬────────────┘
     │         │         │         │
  ┌──▼──┐  ┌──▼──┐  ┌──▼──┐  ┌──▼──┐
  │ S1  │  │ S2  │  │ S3  │  │ S4  │
  │25/m │  │25/m │  │25/m │  │25/m │
  │LOCAL│  │LOCAL│  │LOCAL│  │LOCAL│
  └─────┘  └─────┘  └─────┘  └─────┘

  ✅ No network hop (local counter)
  ❌ Uneven distribution → some servers undercount
  ❌ Server failure means its share of limit is lost
  ❌ Limits not truly global
```

### Approach 3: Redis Cluster with Lua Scripts (Recommended)

```
  ┌──────────┐   ┌──────────┐   ┌──────────┐
  │ Server 1 │   │ Server 2 │   │ Server 3 │
  └────┬─────┘   └────┬─────┘   └────┬─────┘
       │              │              │
       └──────────┬───┘──────────────┘
                  │
                  ▼
       ┌──────────────────────┐
       │   Redis Cluster      │
       │  ┌──────┐ ┌──────┐  │
       │  │Master│ │Master│  │
       │  │ (A-M)│ │ (N-Z)│  │
       │  └──┬───┘ └──┬───┘  │
       │     │        │      │
       │  ┌──▼───┐ ┌──▼───┐  │
       │  │Replica│ │Replica│ │
       │  └──────┘ └──────┘  │
       └──────────────────────┘

  Lua Script (runs atomically on Redis):
  ┌──────────────────────────────────────────────┐
  │  local key = KEYS[1]                         │
  │  local limit = tonumber(ARGV[1])             │
  │  local window = tonumber(ARGV[2])            │
  │                                              │
  │  local current = redis.call('INCR', key)     │
  │  if current == 1 then                        │
  │      redis.call('EXPIRE', key, window)       │
  │  end                                         │
  │                                              │
  │  if current > limit then                     │
  │      return 0  -- REJECTED                   │
  │  end                                         │
  │  return 1      -- ALLOWED                    │
  └──────────────────────────────────────────────┘
```

### Approach 4: Local Counters + Periodic Sync (Approximate)

```
  ┌──────────────────────────────────────────────────────────────┐
  │                                                              │
  │  Each server keeps a LOCAL counter and syncs periodically    │
  │                                                              │
  │  ┌─────────┐      ┌─────────┐      ┌─────────┐             │
  │  │Server 1 │      │Server 2 │      │Server 3 │             │
  │  │local: 23│      │local: 18│      │local: 31│             │
  │  └────┬────┘      └────┬────┘      └────┬────┘             │
  │       │  sync every     │  sync every    │  sync every      │
  │       │  5 seconds      │  5 seconds     │  5 seconds       │
  │       │                 │                │                   │
  │       └────────┬────────┘────────────────┘                  │
  │                ▼                                             │
  │       ┌──────────────┐                                      │
  │       │    Redis     │                                      │
  │       │ global: 72   │  (23 + 18 + 31)                     │
  │       │ limit: 100   │                                      │
  │       └──────────────┘                                      │
  │                                                              │
  │  Between syncs, each server uses:                           │
  │    local_limit = (global_limit - other_servers) / N         │
  │                                                              │
  │  ✅ Very low latency (local check)                          │
  │  ⚠️  Can exceed limit by up to N × sync_interval × rate    │
  │  ✅ Tolerates Redis downtime                                │
  └──────────────────────────────────────────────────────────────┘
```

### Distributed Approach Comparison

| Approach             | Accuracy  | Latency  | Fault Tolerance | Complexity |
|----------------------|-----------|----------|-----------------|------------|
| Centralized Redis    | ✅ Exact  | ~0.5ms   | ❌ Redis SPOF   | Low        |
| Sticky sessions      | ❌ Poor   | ~0ms     | ⚠️ Partial      | Low        |
| Redis Cluster + Lua  | ✅ Exact  | ~0.5ms   | ✅ HA           | Medium     |
| Local + Periodic     | ⚠️ Approx | ~0ms     | ✅ Best         | Medium     |

> **Production recommendation:** Redis Cluster + Lua scripts for accuracy-critical
> systems. Local counters + periodic sync for ultra-low-latency requirements.

---

## 7. Data Model and Storage

### Redis Data Structures by Algorithm

```
  TOKEN BUCKET (Hash per client):
  ─────────────────────────────────
  HSET rate:token:user123
       tokens      7.5
       last_refill 1672531260.123

  FIXED WINDOW COUNTER (String with TTL):
  ─────────────────────────────────────────
  SET  rate:fixed:user123:1672531260  42
  EXPIRE rate:fixed:user123:1672531260  60

  SLIDING WINDOW LOG (Sorted Set):
  ─────────────────────────────────
  ZADD rate:log:user123  1672531260.001  "req1"
  ZADD rate:log:user123  1672531260.150  "req2"
  ZADD rate:log:user123  1672531261.432  "req3"

  SLIDING WINDOW COUNTER (Two Strings):
  ──────────────────────────────────────
  SET  rate:swc:user123:27875520  42    # previous window
  SET  rate:swc:user123:27875521  18    # current window
```

### Key Design Pattern

```
  Key format: rate_limit:{identifier}:{rule_id}:{window}

  Examples:
  ┌─────────────────────────────────────────────────────────────┐
  │  rate_limit:ip:192.168.1.1:global:27875521                 │
  │  rate_limit:user:user123:api_post:27875521                 │
  │  rate_limit:apikey:sk_live_abc:search:27875521             │
  │  rate_limit:ip:10.0.0.1:login:27875521                    │
  └─────────────────────────────────────────────────────────────┘
```

### Memory Estimation

```
  Assumptions:
  - 10 million unique clients
  - Sliding Window Counter algorithm
  - 3 rules per client (per-user, per-IP, global)

  Per rule:
    Key:   ~50 bytes (including overhead)
    Value: ~16 bytes (counter + metadata)
    TTL:   ~8 bytes
    Total: ~74 bytes per key

  Total keys: 10M clients × 3 rules × 2 windows = 60M keys
  Memory:     60M × 74 bytes = ~4.4 GB

  ┌──────────────────────────────────────────────────┐
  │  Algorithm          │ Memory for 10M clients     │
  ├─────────────────────┼────────────────────────────┤
  │  Token Bucket       │  ~1.5 GB  (hash per user)  │
  │  Fixed Window       │  ~2.2 GB  (1 key/window)   │
  │  Sliding Window Log │  ~80 GB   (all timestamps) │
  │  Sliding Win Counter│  ~4.4 GB  (2 keys/rule)    │
  └─────────────────────┴────────────────────────────┘

  Recommendation: Redis Cluster with 3 nodes, each ~8 GB
```

---

## 8. Rate Limit Rules Engine

### Rule Configuration Format (YAML)

```yaml
# rate_limit_rules.yaml

rules:
  # Global rate limit for all users
  - id: global_limit
    description: "Global API rate limit"
    match:
      path: "/api/*"
    limit: 10000
    window: 60        # seconds
    identifier: global
    action: reject

  # Per-user limit for standard tier
  - id: standard_user
    description: "Standard tier user limit"
    match:
      path: "/api/*"
      user_tier: "standard"
    limit: 100
    window: 60
    identifier: user_id
    action: reject

  # Per-user limit for premium tier
  - id: premium_user
    description: "Premium tier user limit"
    match:
      path: "/api/*"
      user_tier: "premium"
    limit: 1000
    window: 60
    identifier: user_id
    action: reject

  # Strict limit on login endpoint (brute force protection)
  - id: login_limit
    description: "Login brute force protection"
    match:
      path: "/api/auth/login"
      method: "POST"
    limit: 5
    window: 300       # 5 minutes
    identifier: ip
    action: reject

  # Search endpoint (expensive operation)
  - id: search_limit
    description: "Search rate limit"
    match:
      path: "/api/search"
      method: "GET"
    limit: 30
    window: 60
    identifier: user_id
    action: queue     # soft limit — queue instead of reject
    priority: 10      # higher = evaluated first
```

### Rule Evaluation Order

```
  Request: POST /api/auth/login from IP 1.2.3.4, user "alice" (standard)

  ┌──────────────────────────────────────────────────────────────────┐
  │  Rule Evaluation Pipeline (most specific → least specific)      │
  │                                                                  │
  │  1. Check login_limit      (path + method specific)             │
  │     Key: rate_limit:ip:1.2.3.4:login_limit:554317              │
  │     Count: 3 < 5 → PASS ✓                                      │
  │                                                                  │
  │  2. Check standard_user    (user tier specific)                 │
  │     Key: rate_limit:user:alice:standard_user:27875521           │
  │     Count: 87 < 100 → PASS ✓                                   │
  │                                                                  │
  │  3. Check global_limit     (global catch-all)                   │
  │     Key: rate_limit:global:global_limit:27875521                │
  │     Count: 8421 < 10000 → PASS ✓                               │
  │                                                                  │
  │  ALL rules passed → ALLOW REQUEST                               │
  │  (If ANY rule fails → REJECT with that rule's action)          │
  └──────────────────────────────────────────────────────────────────┘
```

### Rule Matching Priority

```
  Priority (highest to lowest):
  ┌─────────────────────────────────────────────────────┐
  │  1. Exact path + method + user_tier  (most specific)│
  │  2. Exact path + method                             │
  │  3. Exact path + user_tier                          │
  │  4. Wildcard path + method                          │
  │  5. Wildcard path + user_tier                       │
  │  6. Wildcard path only                              │
  │  7. Global (least specific)                         │
  └─────────────────────────────────────────────────────┘

  A request must pass ALL matching rules, not just the first.
```

---

## 9. Handling Rate Limited Requests

### HTTP 429 Response

```
  HTTP/1.1 429 Too Many Requests
  Content-Type: application/json
  Retry-After: 23
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 0
  X-RateLimit-Reset: 1672531260
  X-RateLimit-RetryAfter: 23

  {
    "error": {
      "code": "RATE_LIMIT_EXCEEDED",
      "message": "Rate limit exceeded. Maximum 100 requests per 60 seconds.",
      "details": {
        "rule": "standard_user",
        "limit": 100,
        "window_seconds": 60,
        "retry_after_seconds": 23
      }
    }
  }
```

### Client-Side Handling: Exponential Backoff with Jitter

```
  EXPONENTIAL BACKOFF WITH JITTER
  ═══════════════════════════════

  Attempt 1: Wait 1s  + random(0, 0.5s)  → retry at ~1.3s
  Attempt 2: Wait 2s  + random(0, 1.0s)  → retry at ~2.7s
  Attempt 3: Wait 4s  + random(0, 2.0s)  → retry at ~5.2s
  Attempt 4: Wait 8s  + random(0, 4.0s)  → retry at ~10.1s
  Attempt 5: Wait 16s + random(0, 8.0s)  → retry at ~20.5s
  MAX:       Wait 32s (cap)

  ┌──────────────────────────────────────────────────────────┐
  │  Why jitter?                                             │
  │                                                          │
  │  WITHOUT jitter:          WITH jitter:                   │
  │  All clients retry        Clients retry at               │
  │  at the same time!        different times                │
  │                                                          │
  │  t=1s: ████████████       t=1.1s: ██                     │
  │  t=2s: ████████████       t=1.3s: ███                    │
  │  t=4s: ████████████       t=1.7s: ██                     │
  │  (thundering herd!)       t=2.1s: ████                   │
  │                           t=2.5s: ██                     │
  │                           (distributed nicely)           │
  └──────────────────────────────────────────────────────────┘
```

**Client Pseudocode:**

```python
def call_api_with_retry(request, max_retries=5):
    for attempt in range(max_retries):
        response = http.send(request)

        if response.status != 429:
            return response

        # Use Retry-After header if present
        retry_after = response.headers.get("Retry-After")
        if retry_after:
            wait = int(retry_after)
        else:
            # Exponential backoff with jitter
            base = min(2 ** attempt, 32)        # cap at 32s
            jitter = random.uniform(0, base / 2)
            wait = base + jitter

        log.warn(f"Rate limited. Retry in {wait:.1f}s (attempt {attempt+1})")
        sleep(wait)

    raise RateLimitError("Exceeded max retries")
```

### Graceful Degradation vs Hard Rejection

```
  ┌─────────────────────────────────────────────────────────────┐
  │                                                             │
  │  HARD REJECTION (default):                                  │
  │  ┌──────────┐     ┌───────────┐     ┌──────────────┐       │
  │  │ Request  │────>│ Over      │────>│ Return 429   │       │
  │  │          │     │ limit?    │ YES │ immediately   │       │
  │  └──────────┘     └───────────┘     └──────────────┘       │
  │                                                             │
  │  GRACEFUL DEGRADATION:                                      │
  │  ┌──────────┐     ┌───────────┐     ┌──────────────┐       │
  │  │ Request  │────>│ Over      │────>│ Serve cached │       │
  │  │          │     │ limit?    │ YES │ / reduced    │       │
  │  └──────────┘     └───────────┘     │ quality resp │       │
  │                                     └──────────────┘       │
  │  Examples:                                                  │
  │  - Return cached (possibly stale) data                     │
  │  - Reduce response fields (omit recommendations)           │
  │  - Lower image quality                                     │
  │  - Queue for later processing (return 202 Accepted)        │
  │                                                             │
  └─────────────────────────────────────────────────────────────┘
```

---

## 10. Full System Architecture

```
  ┌─────────────────────────────────────────────────────────────────────────┐
  │                      COMPLETE RATE LIMITER ARCHITECTURE                 │
  └─────────────────────────────────────────────────────────────────────────┘

                           ┌──────────┐
                           │  Client  │
                           │ (Mobile/ │
                           │  Web/API)│
                           └────┬─────┘
                                │
                                │  HTTPS
                                ▼
                    ┌───────────────────────┐
                    │    Load Balancer      │
                    │   (AWS ALB / Nginx)   │
                    └───────────┬───────────┘
                                │
               ┌────────────────┼────────────────┐
               │                │                │
               ▼                ▼                ▼
  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
  │  API Gateway   │ │  API Gateway   │ │  API Gateway   │
  │  Instance 1    │ │  Instance 2    │ │  Instance 3    │
  │                │ │                │ │                │
  │ ┌────────────┐ │ │ ┌────────────┐ │ │ ┌────────────┐ │
  │ │Rate Limiter│ │ │ │Rate Limiter│ │ │ │Rate Limiter│ │
  │ │ Middleware │ │ │ │ Middleware │ │ │ │ Middleware │ │
  │ └─────┬──────┘ │ │ └─────┬──────┘ │ │ └─────┬──────┘ │
  │       │        │ │       │        │ │       │        │
  │ ┌─────▼──────┐ │ │ ┌─────▼──────┐ │ │ ┌─────▼──────┐ │
  │ │Rules Cache │ │ │ │Rules Cache │ │ │ │Rules Cache │ │
  │ │  (local)   │ │ │ │  (local)   │ │ │ │  (local)   │ │
  │ └────────────┘ │ │ └────────────┘ │ │ └────────────┘ │
  └───────┬────────┘ └───────┬────────┘ └───────┬────────┘
          │                  │                  │
          │    ┌─────────────┼──────────────┐   │
          │    │             │              │   │
          └────┤   Rate Limit Check        ├───┘
               │             │              │
               ▼             ▼              ▼
  ┌──────────────────────────────────────────────────────┐
  │                  Redis Cluster                        │
  │                                                      │
  │  ┌────────────┐  ┌────────────┐  ┌────────────┐     │
  │  │  Master 1  │  │  Master 2  │  │  Master 3  │     │
  │  │ (slots     │  │ (slots     │  │ (slots     │     │
  │  │  0-5460)   │  │  5461-     │  │  10923-    │     │
  │  │            │  │  10922)    │  │  16383)    │     │
  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘     │
  │        │               │               │             │
  │  ┌─────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐     │
  │  │  Replica 1 │  │  Replica 2 │  │  Replica 3 │     │
  │  └────────────┘  └────────────┘  └────────────┘     │
  │                                                      │
  └──────────────────────────────────────────────────────┘

               ┌─────────────────────────┐
               │     Rules Service       │
               │                         │
               │  GET /rules             │
               │  PUT /rules/{id}        │
               │  POST /rules            │
               │  DELETE /rules/{id}     │
               └────────────┬────────────┘
                            │
                            ▼
               ┌─────────────────────────┐
               │   Rules Database        │
               │   (PostgreSQL)          │
               │                         │
               │  ┌───────────────────┐  │
               │  │  rate_limit_rules │  │
               │  │  ─────────────────│  │
               │  │  id               │  │
               │  │  name             │  │
               │  │  path_pattern     │  │
               │  │  method           │  │
               │  │  limit_count      │  │
               │  │  window_seconds   │  │
               │  │  identifier_type  │  │
               │  │  action           │  │
               │  │  priority         │  │
               │  │  enabled          │  │
               │  │  created_at       │  │
               │  │  updated_at       │  │
               │  └───────────────────┘  │
               └─────────────────────────┘
```

### Request Flow (Step by Step)

```
  ┌────────────────────────────────────────────────────────────────────┐
  │                        REQUEST FLOW                                │
  │                                                                    │
  │  1. Client sends request                                          │
  │         │                                                          │
  │         ▼                                                          │
  │  2. Load balancer routes to API Gateway instance                  │
  │         │                                                          │
  │         ▼                                                          │
  │  3. Rate Limiter middleware intercepts request                    │
  │         │                                                          │
  │         ├──> 3a. Extract identifier (IP, user ID, API key)        │
  │         │                                                          │
  │         ├──> 3b. Load matching rules from LOCAL cache             │
  │         │        (refreshed every 30s from Rules Service)         │
  │         │                                                          │
  │         ├──> 3c. For EACH matching rule:                          │
  │         │        Execute Lua script on Redis                      │
  │         │        ┌─────────────────────────────────────────┐      │
  │         │        │  INCR counter                           │      │
  │         │        │  Check against limit                    │      │
  │         │        │  Return (allowed, remaining, reset_at)  │      │
  │         │        └─────────────────────────────────────────┘      │
  │         │                                                          │
  │         ├──> 3d. If ANY rule exceeded → Return 429 + headers      │
  │         │                                                          │
  │         └──> 3e. If ALL rules pass → Forward to application       │
  │                  + add X-RateLimit-* headers to response          │
  │                                                                    │
  │  4. Application processes request                                 │
  │         │                                                          │
  │  5. Response returned to client with rate limit headers           │
  │                                                                    │
  │  Total added latency: ~0.5ms (Redis round-trip)                   │
  └────────────────────────────────────────────────────────────────────┘
```

### Monitoring and Alerting

```
  ┌──────────────────────────────────────────────────────────────┐
  │                    OBSERVABILITY                              │
  │                                                              │
  │  Metrics (Prometheus + Grafana):                             │
  │  ┌────────────────────────────────────────────────────────┐  │
  │  │ • rate_limiter_requests_total{status="allowed|rejected"}│ │
  │  │ • rate_limiter_latency_ms (histogram)                  │  │
  │  │ • rate_limiter_redis_errors_total                      │  │
  │  │ • rate_limiter_rules_evaluated_total                   │  │
  │  │ • rate_limiter_active_clients (gauge)                  │  │
  │  └────────────────────────────────────────────────────────┘  │
  │                                                              │
  │  Alerts:                                                     │
  │  ┌────────────────────────────────────────────────────────┐  │
  │  │ • 429 rate > 10% of traffic → investigate abuse        │  │
  │  │ • Redis latency > 2ms → check Redis cluster health     │  │
  │  │ • Redis connection errors → failover or fail-open      │  │
  │  │ • Single client > 80% of limit → warn client           │  │
  │  └────────────────────────────────────────────────────────┘  │
  └──────────────────────────────────────────────────────────────┘
```

---

## 11. Key Takeaways

```
  ┌──────────────────────────────────────────────────────────────────┐
  │                      KEY TAKEAWAYS                               │
  │                                                                  │
  │  1. ALGORITHM CHOICE MATTERS: Use Sliding Window Counter for    │
  │     most cases — best balance of accuracy and memory.           │
  │     Token Bucket if you need to allow controlled bursts.        │
  │                                                                  │
  │  2. DISTRIBUTED IS THE HARD PART: Single-server rate limiting   │
  │     is trivial. The challenge is coordination across servers.   │
  │     Redis Cluster + Lua scripts is the production standard.     │
  │                                                                  │
  │  3. FAIL-OPEN, NOT FAIL-CLOSED: If the rate limiter is down,   │
  │     allow traffic through. A brief period without rate limits   │
  │     is better than a complete outage for all users.             │
  │                                                                  │
  │  4. MULTIPLE RULES, NOT ONE: Real systems apply per-IP,        │
  │     per-user, per-endpoint, and global limits simultaneously.   │
  │     A request must pass ALL applicable rules.                   │
  │                                                                  │
  │  5. HEADERS ARE NOT OPTIONAL: Always return X-RateLimit-*      │
  │     headers so clients can self-throttle and provide a good     │
  │     developer experience. Include Retry-After on 429s.          │
  │                                                                  │
  │  6. REDIS IS YOUR FRIEND: In-memory, atomic operations,        │
  │     clustering, Lua scripting — Redis was practically built     │
  │     for rate limiting. Budget ~5 GB for 10M clients.            │
  │                                                                  │
  │  7. RULES ENGINE ADDS FLEXIBILITY: Externalize rate limit      │
  │     rules into a configuration service. This allows changing    │
  │     limits without code deployments — critical during           │
  │     incidents and traffic spikes.                                │
  │                                                                  │
  │  8. CLIENT-SIDE MATTERS TOO: Implement exponential backoff     │
  │     with jitter in all API clients. Without jitter, retries    │
  │     create thundering herds that amplify the problem.           │
  │                                                                  │
  └──────────────────────────────────────────────────────────────────┘
```

### Quick Decision Framework

```
  "Which algorithm should I use?"

  ┌──────────────────────────┐
  │ Need to allow bursts?    │
  │          │               │
  │     YES  │  NO           │
  │     ▼    │  ▼            │
  │  Token   │ Need precise  │
  │  Bucket  │ accuracy?     │
  │          │    │          │
  │          │YES │  NO      │
  │          │ ▼  │  ▼       │
  │          │Slid│ Sliding  │
  │          │Win │ Window   │
  │          │Log │ Counter ⭐│
  └──────────┴────┴──────────┘

  "Where should I put it?"

  Small startup → API Gateway (Kong, AWS API GW)
  Mid-size      → API Gateway + Redis middleware
  Large scale   → Dedicated rate limit service + Redis Cluster
  DDoS defense  → Cloudflare/AWS WAF (edge) + server-side
```

---

### Further Reading

| Resource                                  | Type            |
|-------------------------------------------|-----------------|
| Stripe's rate limiter blog post           | Case study      |
| Cloudflare "How We Built Rate Limiting"   | Architecture    |
| Redis documentation on Lua scripting      | Implementation  |
| RFC 6585 (HTTP 429 status code)           | Specification   |
| Google Cloud "Rate Limiting Strategies"   | Best practices  |
| Token Bucket on Wikipedia                 | Algorithm       |

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
