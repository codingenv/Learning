# Appendix B: System Design Interview Framework

> A battle-tested, step-by-step playbook for the 45-60 minute system design interview. Follow this framework to stay structured, communicate clearly, and demonstrate senior-level thinking.

---

## Overview: Time Allocation (45-Minute Interview)

| Step | Activity | Time | % of Interview |
|---|---|---|---|
| 1 | Understand the Problem & Scope | 3-5 min | ~8% |
| 2 | Define Requirements | 3-5 min | ~8% |
| 3 | Back-of-Envelope Estimation | 3-5 min | ~8% |
| 4 | High-Level Design | 10-15 min | ~30% |
| 5 | Deep Dive | 10-15 min | ~30% |
| 6 | Wrap Up | 3-5 min | ~8% |

> **Golden rule:** Never spend more than 10 minutes before you start drawing. Interviewers want to see a design, not a requirements document.

---

## Step 1: Understand the Problem & Scope (3-5 min)

### Goal
Align with the interviewer on **what** you're building. Narrow an impossibly broad prompt ("Design Twitter") into a focused set of features.

### Clarifying Questions Checklist

Ask these categories of questions (not all -- pick the most relevant):

**Users & Scale**
- [ ] Who are the users? (consumers, businesses, internal teams)
- [ ] How many daily active users (DAU)?
- [ ] What's the expected growth rate?
- [ ] Is this a global service or single-region?

**Core Features**
- [ ] What are the most important features to focus on?
- [ ] Can I prioritize 2-3 core features and leave the rest out of scope?
- [ ] Are there any features you'd specifically like me to address?

**Technical Constraints**
- [ ] Any existing tech stack constraints?
- [ ] Do we need to integrate with existing systems?
- [ ] Any specific latency requirements? (e.g., < 200ms p99)
- [ ] What availability target? (99.9%, 99.99%?)

**Data Characteristics**
- [ ] What's the read vs. write ratio?
- [ ] How large is the data set expected to be?
- [ ] Is data consistency critical, or is eventual consistency acceptable?
- [ ] Any data retention or compliance requirements?

### Identify Core Features vs. Nice-to-Have

After asking questions, explicitly state your scope:

> *"Based on our discussion, I'll focus on these core features for the design: [Feature A], [Feature B], [Feature C]. I'll mention [Feature D] and [Feature E] as extensions if we have time. Does that sound right?"*

**Example for "Design Twitter":**

| Core (in scope) | Nice-to-Have (mention if time) |
|---|---|
| Post a tweet | Direct messages |
| News feed (home timeline) | Search |
| Follow / unfollow users | Trending topics |
| | Notifications |
| | Media upload (images/video) |

---

## Step 2: Define Requirements (3-5 min)

### Functional Requirements Template

List the specific things the system must do:

```
FR1: Users can [action] [object]
FR2: The system should [behavior] when [condition]
FR3: Users can [query/view] [data] filtered by [criteria]
```

**Example -- URL Shortener:**
- FR1: Given a long URL, generate a unique short URL
- FR2: Given a short URL, redirect to the original long URL
- FR3: Users can optionally set a custom alias
- FR4: Users can set an expiration time for a short URL
- FR5: Analytics: track click count per short URL

### Non-Functional Requirements Template

| Category | Requirement | Target |
|---|---|---|
| **Availability** | System uptime | 99.99% (~52 min downtime/year) |
| **Latency** | p50 read latency | < 100 ms |
| **Latency** | p99 read latency | < 500 ms |
| **Throughput** | Read QPS | ~10,000 |
| **Throughput** | Write QPS | ~1,000 |
| **Scalability** | Handle growth to | 100M DAU |
| **Durability** | Data must not be lost | 99.999999999% (11 nines) |
| **Consistency** | Consistency model | Eventual (reads may be slightly stale) |
| **Security** | Auth, rate limiting, encryption | Yes |

### Constraints

Note any hard constraints:
- Budget limitations (prefer managed services vs. self-hosted)
- Regulatory (GDPR, HIPAA, data residency)
- Legacy system integrations
- Team expertise

---

## Step 3: Back-of-Envelope Estimation (3-5 min)

### Goal
Establish the **order of magnitude** for traffic, storage, and bandwidth. This drives architectural decisions (do we need sharding? how many servers?).

### Traffic Estimation Template

```
Given:
  DAU = ___
  Actions per user per day = ___

Calculations:
  Total requests/day  = DAU x actions_per_user = ___
  Average QPS         = total_requests / 86,400 = ___
  Peak QPS            = Average QPS x peak_factor (2-5x) = ___

Read vs Write:
  Read QPS  = Total QPS x read_ratio = ___
  Write QPS = Total QPS x write_ratio = ___
```

### Storage Estimation Template

```
Given:
  New records per day  = ___
  Average record size  = ___ bytes
  Retention period     = ___ years

Calculations:
  Daily storage   = records_per_day x record_size = ___
  Annual storage  = daily_storage x 365 = ___
  Total storage   = annual_storage x retention_years = ___
  With replication (3x) = total_storage x 3 = ___
```

### Bandwidth Estimation Template

```
Incoming (write):
  Write bandwidth = write_QPS x average_write_size = ___

Outgoing (read):
  Read bandwidth  = read_QPS x average_response_size = ___
```

### Worked Example: URL Shortener

```
Assumptions:
  - 100M DAU
  - Each user creates 0.1 short URLs/day and reads 5 short URLs/day
  
Write QPS:
  - 100M x 0.1 = 10M writes/day
  - 10M / 86,400 ~ 116 writes/sec
  - Peak: ~500 writes/sec

Read QPS:
  - 100M x 5 = 500M reads/day  
  - 500M / 86,400 ~ 5,800 reads/sec
  - Peak: ~20,000 reads/sec
  - Read:Write ratio ~ 50:1 (very read-heavy -> great caching candidate)

Storage (5 years):
  - Each URL record ~ 500 bytes (short URL + long URL + metadata)
  - 10M/day x 500 bytes = 5 GB/day
  - 5 years: 5 GB x 365 x 5 ~ 9 TB
  - With 3x replication: ~27 TB (manageable)

Bandwidth:
  - Write: 500 writes/sec x 500 bytes ~ 250 KB/s (negligible)
  - Read: 20K reads/sec x 500 bytes ~ 10 MB/s (very manageable)
```

---

## Step 4: High-Level Design (10-15 min)

### Goal
Draw the architecture diagram. This is the **most important** part of the interview. Think out loud as you draw.

### Step 4a: Define the API

List the 3-5 core API endpoints:

```
POST /api/v1/urls
  Request:  { "long_url": "https://...", "custom_alias": "abc", "ttl": 3600 }
  Response: { "short_url": "https://tinyurl.com/abc" }

GET /{short_url_key}
  Response: 301 Redirect to long_url

GET /api/v1/urls/{short_url_key}/stats
  Response: { "click_count": 12345, "created_at": "..." }
```

### Step 4b: Draw the Architecture

Follow this general structure (adapt per problem):

```
                        +----------+
                        |  Clients |
                        | (Mobile, |
                        |  Web)    |
                        +----+-----+
                             |
                        +----v-----+
                        |   CDN    |  <-- Static assets
                        +----+-----+
                             |
                        +----v-----+
                        |   Load   |
                        | Balancer |
                        +----+-----+
                             |
                   +---------+---------+
                   |                   |
              +----v----+        +----v----+
              | App     |        | App     |
              | Server  |        | Server  |
              | (x N)   |        | (x N)   |
              +----+----+        +----+----+
                   |                   |
              +----v-------------------v----+
              |           Cache             |
              |     (Redis Cluster)         |
              +-------------+---------------+
                            |
              +-------------v---------------+
              |         Database            |
              | (Primary + Read Replicas)   |
              +-----------------------------+
```

### Step 4c: Identify Major Components

For each box in your diagram, state:
1. **What it does** (one sentence)
2. **Technology choice** (with brief justification)
3. **How data flows** through it

### Step 4d: Define Data Flow

Walk through 1-2 key user flows end to end:

> *"When a user creates a short URL: the client sends a POST to the load balancer, which routes to an app server. The app server generates a unique ID, stores the mapping in the database, updates the cache, and returns the short URL."*

---

## Step 5: Deep Dive (10-15 min)

### Goal
Demonstrate depth. Pick 2-3 components and go deep on the design, algorithms, and trade-offs.

### What to Deep Dive On

Pick based on the interviewer's interest (they'll usually guide you) or choose the **hardest / most interesting** parts:

| System | Good Deep Dive Topics |
|---|---|
| URL Shortener | ID generation (hash vs counter vs Snowflake), hash collision resolution |
| News Feed | Fan-out on write vs fan-out on read, ranking algorithm |
| Chat System | WebSocket connection management, message ordering, offline delivery |
| Rate Limiter | Token bucket vs sliding window algorithm, distributed rate limiting |
| Search | Inverted index, ranking, typeahead with Trie |
| Notification | Push vs pull, priority queue, delivery guarantees |

### Deep Dive Template

For each component you deep dive on:

1. **State the problem** clearly
2. **Present 2-3 approaches** with pros/cons
3. **Choose one** and justify why
4. **Discuss edge cases**: What if it fails? What about race conditions? How does it scale?

### Discuss Trade-Offs

Always frame decisions as trade-offs:

> *"We could use fan-out on write, which pre-computes the feed and gives O(1) reads, but costs more storage and write amplification. Alternatively, fan-out on read computes the feed dynamically, saving storage but increasing read latency. Given our requirement for fast reads and the fact that most users have < 1000 followers, I'd choose fan-out on write for most users and fan-out on read for celebrity accounts (hybrid approach)."*

### Handle Edge Cases

Proactively address:
- **What happens when a node fails?** (replication, failover)
- **What about hot spots?** (caching, sharding strategy)
- **What about concurrent writes?** (locking, optimistic concurrency, CRDTs)
- **What about data loss?** (WAL, replication, backups)
- **What if traffic spikes 10x?** (auto-scaling, rate limiting, load shedding)

---

## Step 6: Wrap Up (3-5 min)

### Goal
Show maturity by thinking about production readiness and future evolution.

### Summarize Design Choices

> *"To summarize: we built a URL shortening service using a Base62-encoded Snowflake ID for short URL generation, a Redis cache for the read-heavy access pattern (50:1 read-write ratio), PostgreSQL with read replicas for durable storage, and a CDN for serving redirects from edge locations."*

### Discuss Bottlenecks and Improvements

Identify what you'd improve with more time:
- Current bottleneck: single point of failure in [component]
- Next improvement: add [solution]
- Future scale challenge: when we hit 1B users, we'd need to [plan]

### Mention Operational Aspects

| Aspect | What to Mention |
|---|---|
| **Monitoring** | Key metrics to track (QPS, latency p50/p99, error rate, cache hit ratio) |
| **Alerting** | Alert on error rate > 1%, latency p99 > 500ms, disk usage > 80% |
| **Logging** | Structured logging, centralized log aggregation (ELK / Datadog) |
| **Deployment** | Blue-green or canary deployment, feature flags, rollback strategy |
| **Security** | Rate limiting, authentication, input validation, DDoS protection |
| **Disaster Recovery** | Multi-region replication, backup strategy, RTO/RPO targets |

---

## Example Walkthrough: Design a Rate Limiter

### Step 1: Clarify

> *"Is this a rate limiter for an API gateway, or a standalone service? Should it be distributed across multiple servers? What's the rate limiting granularity -- per user, per IP, or per API key? What should happen when the limit is exceeded -- return 429 or queue the request?"*

**Agreed scope:** Distributed rate limiter for an API gateway, per-user limiting, returns HTTP 429 when exceeded.

### Step 2: Requirements

**Functional:**
- FR1: Limit each user to N requests per time window
- FR2: Return HTTP 429 with Retry-After header when limit exceeded
- FR3: Support configurable rules (different limits per API endpoint)

**Non-Functional:**
- Low latency (< 1ms overhead per request)
- Highly available (if rate limiter is down, allow traffic -- fail open)
- Distributed (works across multiple API gateway instances)
- Accurate (minimal race conditions in counting)

### Step 3: Estimation

```
- 10M DAU, avg 100 API calls/user/day
- Total: 1B requests/day ~ 12K QPS avg, ~50K QPS peak
- Rate limiter must handle 50K lookups/sec with < 1ms latency
- Storage per user: user_id (8 bytes) + counter (8 bytes) + timestamp (8 bytes) = ~24 bytes
- 10M users x 24 bytes = 240 MB (fits in a single Redis instance)
```

### Step 4: High-Level Design

```
Client --> API Gateway --> Rate Limiter Middleware --> Backend Services
                               |
                          Redis Cluster
                          (counters per user)
                               |
                          Rules Config DB
                          (rate limit rules)
```

**API:**
- Rate limiter is middleware, not a separate API
- Called internally: `is_allowed(user_id, endpoint) -> bool`

**Data Model (Redis):**
```
Key:   rate_limit:{user_id}:{endpoint}:{window}
Value: counter (integer)
TTL:   window_size (e.g., 60 seconds)
```

### Step 5: Deep Dive -- Rate Limiting Algorithms

| Algorithm | How It Works | Pros | Cons |
|---|---|---|---|
| **Fixed Window** | Count requests in fixed time windows (e.g., 0:00-0:59, 1:00-1:59) | Simple, low memory | Burst at window boundary (2x limit) |
| **Sliding Window Log** | Store timestamp of each request; count within last N seconds | Accurate | High memory (stores every timestamp) |
| **Sliding Window Counter** | Weighted average of current + previous window counts | Good accuracy, low memory | Approximate |
| **Token Bucket** | Bucket holds tokens; refilled at fixed rate; request consumes a token | Allows controlled bursts, smooth | Slightly more complex |
| **Leaky Bucket** | Requests enter a queue; processed at fixed rate | Smooths traffic perfectly | No burst tolerance; queue management |

**Choice: Sliding Window Counter** -- best balance of accuracy and memory efficiency.

**Race condition handling:** Use Redis Lua script for atomic read-increment-expire:
```lua
local count = redis.call('INCR', KEYS[1])
if count == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[1])
end
return count
```

### Step 6: Wrap Up

- **Bottleneck:** Redis is a single point of failure -> use Redis Cluster with replication
- **Improvement:** Add local in-memory rate limiting (token bucket) as first line of defense to reduce Redis calls
- **Monitoring:** Track rate-limit hits per endpoint, false positive rate, Redis latency
- **Failure mode:** If Redis is down, fail open (allow all requests) to avoid cascading failure

---

## Common Interview Mistakes and How to Avoid Them

| # | Mistake | Impact | How to Avoid |
|---|---|---|---|
| 1 | **Diving into solution immediately** | Solving the wrong problem | Force yourself to ask 3-5 questions first |
| 2 | **Silent design** | Interviewer can't assess your thinking | Narrate your thought process constantly |
| 3 | **Trying to cover everything** | Shallow design, no depth | Focus on 2-3 core features and go deep |
| 4 | **Not drawing a diagram** | Hard to follow, appears unstructured | Always draw, even if it's rough |
| 5 | **Ignoring numbers** | Can't justify design decisions | Always do back-of-envelope math |
| 6 | **One-sided trade-offs** | Appears you don't know alternatives | Present 2-3 options, compare, then decide |
| 7 | **Perfectionism** | Spending too long on one component | Timebox yourself; move on and come back |
| 8 | **Ignoring failure scenarios** | Design seems naive | Proactively discuss: "What if this fails?" |
| 9 | **Not asking about scale** | Could over-engineer or under-engineer | Always clarify: "How many users? How much data?" |
| 10 | **Designing for Google scale from the start** | Overcomplicates the design unnecessarily | Start simple, add complexity only where numbers demand it |

---

## Tips for Communication During the Interview

### Do's

- **Think out loud.** The interviewer cares more about your process than the final answer.
- **Use the whiteboard / drawing tool.** Always have a visual diagram.
- **State your assumptions.** "I'm assuming we have 10M DAU -- does that sound right?"
- **Frame trade-offs explicitly.** "Option A gives us X but costs Y. Option B gives us Z but costs W. I'd choose A because..."
- **Check in with the interviewer.** "Should I go deeper on the caching layer, or move on to the database design?"
- **Use concrete numbers.** "With 10K QPS and 500-byte payloads, that's 5 MB/s of bandwidth."
- **Mention what you're NOT designing.** Show you're aware of scope.

### Don'ts

- **Don't memorize solutions.** Interviewers can tell. Instead, understand principles and derive the design.
- **Don't argue with the interviewer.** If they push back, listen -- they might be guiding you.
- **Don't say "I don't know" and stop.** Instead say "I'm not sure, but my instinct is X because Y. Let me reason through it..."
- **Don't use buzzwords without understanding.** Only mention technologies you can explain.
- **Don't forget the user.** Always start from the user's perspective and work inward.

### Power Phrases

| Situation | What to Say |
|---|---|
| Starting the problem | *"Before I jump in, let me make sure I understand the requirements..."* |
| Making a choice | *"I see two options here. Option A is... Option B is... I'd go with A because..."* |
| Unsure about something | *"I'd want to benchmark this in practice, but my estimate is..."* |
| Interviewer asks a hard question | *"That's a great question. Let me think about that..."* (then think, don't panic) |
| Running out of time | *"With more time, I'd also address [X, Y, Z]. Let me quickly outline..."* |
| Wrapping up | *"To summarize the key decisions: we chose [A] for [reason], [B] for [reason]..."* |

---

## Quick Checklist: Before You Submit Your Design

- [ ] Did I clarify requirements and scope?
- [ ] Did I do back-of-envelope estimation?
- [ ] Is there a clear architecture diagram?
- [ ] Did I define the core APIs?
- [ ] Did I choose a database and justify it?
- [ ] Did I address the read/write pattern (caching, replicas)?
- [ ] Did I discuss at least 2 trade-offs explicitly?
- [ ] Did I handle failure scenarios (what if X goes down)?
- [ ] Did I mention monitoring, logging, and alerting?
- [ ] Did I consider security (auth, rate limiting, encryption)?
- [ ] Is the design scalable to 10x the current load?

---

> **Remember:** A system design interview is a conversation, not an exam. The interviewer wants to see how you think, communicate, and make decisions under uncertainty. Show your process, not just your answer.
