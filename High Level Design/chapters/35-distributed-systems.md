# Chapter 35: Distributed Systems — The Complete Guide

## Introduction

> *"A distributed system is one in which the failure of a computer you didn't even know
> existed can render your own computer unusable."* — Leslie Lamport

Every large-scale system you use today — Google Search, Amazon, Netflix, WhatsApp, Uber —
is a distributed system. Understanding distributed systems is not optional for modern
software engineers; it is **the** core competency that separates junior from senior engineers.

This chapter is a comprehensive guide to distributed systems: what they are, why they exist,
the fundamental principles that govern them, the failure modes that haunt them, the algorithms
that tame them, and the design patterns that make them work in production.

> **How this chapter relates to others:** This chapter covers the **foundational theory and
> principles** of distributed systems. For specific implementations, refer to:
> - [Chapter 11 — Data Partitioning & Sharding](./11-data-partitioning.md)
> - [Chapter 12 — Data Replication](./12-data-replication.md)
> - [Chapter 13 — Consistency & Consensus](./13-consistency.md)
> - [Chapter 24 — Distributed Transactions](./24-distributed-transactions.md)
> - [Chapter 25 — Consensus Algorithms](./25-consensus.md)

---

## 35.1 What Is a Distributed System?

A **distributed system** is a collection of independent computers (nodes) that **communicate
over a network** and **coordinate their actions** to appear to the end user as a single
coherent system.

```
  Single Machine:                    Distributed System:
  
  +-------------------+              +--------+    +--------+    +--------+
  | One Computer      |              | Node A |<-->| Node B |<-->| Node C |
  | - One CPU         |              |        |    |        |    |        |
  | - One Memory      |              +---+----+    +---+----+    +---+----+
  | - One Disk        |                  |             |             |
  | - Everything local|                  +------+------+------+------+
  +-------------------+                         |   Network   |
                                                +-------------+
  
  Key difference: In a distributed system, nodes do NOT share memory or
  a global clock. They communicate ONLY by passing messages over an
  unreliable network.
```

### Three Defining Characteristics

```
  1. NO SHARED MEMORY
  +-------------------------------------------------------------------+
  | Node A has its own RAM. Node B has its own RAM. They CANNOT read  |
  | each other's memory directly. All communication happens via       |
  | network messages (RPC, HTTP, TCP, etc.).                          |
  |                                                                   |
  | Implication: You can't just "lock a variable" across nodes.       |
  | You need distributed locks, consensus protocols, or CRDTs.       |
  +-------------------------------------------------------------------+
  
  2. NO GLOBAL CLOCK
  +-------------------------------------------------------------------+
  | Each node has its own clock, and clocks DRIFT apart over time.    |
  | There is no way to precisely answer "what happened first?"        |
  | across nodes without special algorithms.                          |
  |                                                                   |
  | Implication: You need logical clocks (Lamport, Vector Clocks),    |
  | hybrid clocks, or TrueTime (Google Spanner) to order events.     |
  +-------------------------------------------------------------------+
  
  3. PARTIAL FAILURE
  +-------------------------------------------------------------------+
  | Parts of the system can fail independently. Node A might be       |
  | working perfectly while Node B has crashed and Node C has a       |
  | network partition. Unlike a single machine where everything       |
  | fails together.                                                    |
  |                                                                   |
  | Implication: The system must detect, tolerate, and recover from   |
  | partial failures without human intervention.                      |
  +-------------------------------------------------------------------+
```

### Why Build Distributed Systems?

```
  +-------------------------------------------------------------------+
  | Reason              | Explanation                                  |
  +-------------------------------------------------------------------+
  | SCALABILITY         | One machine has finite CPU, RAM, disk, and   |
  |                     | network. Distributed systems scale by adding |
  |                     | more machines (horizontal scaling).           |
  +-------------------------------------------------------------------+
  | FAULT TOLERANCE     | If one machine dies, others continue serving. |
  |                     | No single point of failure.                  |
  +-------------------------------------------------------------------+
  | LATENCY REDUCTION   | Place data/compute closer to users.          |
  |                     | A server in Mumbai serves Indian users faster |
  |                     | than one in Virginia.                        |
  +-------------------------------------------------------------------+
  | DATA LOCALITY       | Legal requirements (GDPR) may mandate that   |
  |                     | data stays in specific regions.              |
  +-------------------------------------------------------------------+
  | ORGANIZATIONAL      | Different teams own different services.       |
  |                     | Independent deployment and scaling.          |
  +-------------------------------------------------------------------+
  | COST EFFICIENCY     | Many commodity machines are cheaper than one  |
  |                     | ultra-powerful mainframe (at scale).         |
  +-------------------------------------------------------------------+
```

---

## 35.2 The 8 Fallacies of Distributed Computing

In 1994, Peter Deutsch (and later James Gosling) enumerated 8 false assumptions that
developers new to distributed systems commonly make. These fallacies have caused countless
production outages. **Every distributed system engineer must internalize them.**

```
  +-------------------------------------------------------------------+
  |            THE 8 FALLACIES OF DISTRIBUTED COMPUTING               |
  +-------------------------------------------------------------------+
  |                                                                   |
  |  1. The network is reliable.                                      |
  |  2. Latency is zero.                                              |
  |  3. Bandwidth is infinite.                                        |
  |  4. The network is secure.                                        |
  |  5. Topology doesn't change.                                      |
  |  6. There is one administrator.                                   |
  |  7. Transport cost is zero.                                       |
  |  8. The network is homogeneous.                                   |
  |                                                                   |
  +-------------------------------------------------------------------+
```

### Fallacy 1: "The Network Is Reliable"

```
  Reality: Networks fail ALL THE TIME.
  
  +--------+     Packet sent     +--------+
  | Node A | ------------------> | Node B |
  +--------+         X           +--------+
                     |
             Packet lost! Reasons:
             - Network switch failure
             - Cable damage
             - Router crash
             - Congestion/buffer overflow
             - Cloud provider network issue
             - Firewall misconfiguration
  
  Node A sent the message. Did Node B receive it?
  Node A has NO WAY TO KNOW without an acknowledgment.
  And what if the acknowledgment itself is lost?
  
  Consequences:
  - Messages can be lost, duplicated, delayed, or reordered
  - You MUST implement retries with idempotency
  - You MUST implement timeouts (but: is the server slow or dead?)
  - You MUST handle duplicate messages gracefully
  
  Real-world: AWS reports network-related incidents monthly.
  Google's Spanner team found that ~1 in 10,000 network links
  has issues at any given time across their global network.
```

### Fallacy 2: "Latency Is Zero"

```
  Reality: Every network call adds latency.
  
  +---------------------------------------------+
  | Operation                    | Latency       |
  +---------------------------------------------+
  | In-memory function call      | ~1 nanosecond |
  | Same-machine IPC             | ~10 microseconds|
  | Same-datacenter network call | ~0.5 ms       |
  | Cross-datacenter (same region)| ~5-20 ms     |
  | Cross-continent              | ~50-150 ms    |
  | With TLS handshake           | +50-100 ms    |
  +---------------------------------------------+
  
  Consequence: A function that makes 10 sequential network calls
  within the same datacenter adds ~5ms of PURE NETWORK latency.
  In a microservice architecture with 6 services deep, that's
  6 * 0.5ms = 3ms minimum — and that's WITHOUT processing time.
  
  Design implications:
  - Batch network calls (don't make 100 calls when 1 will do)
  - Use async communication where possible
  - Co-locate services that communicate frequently
  - Cache aggressively to avoid remote calls
  - Monitor P99 latency, not just P50 (tail latency kills)
```

### Fallacy 3: "Bandwidth Is Infinite"

```
  Reality: Bandwidth is finite, shared, and expensive.
  
  A 10 Gbps link between two data centers sounds like a lot.
  But when 1000 services each send 100 MB/s of data, that's 100 GB/s.
  
  Design implications:
  - Don't transfer unnecessary data (SELECT * when you need 2 columns)
  - Compress data in transit (gzip, Brotli, protobuf over JSON)
  - Use pagination instead of returning all results
  - Implement data locality (process data where it lives)
  - Use CDNs to offload bandwidth from origin
```

### Fallacy 4: "The Network Is Secure"

```
  Reality: The network is hostile.
  
  - Man-in-the-middle attacks can intercept traffic
  - DNS spoofing can redirect traffic
  - Internal networks are NOT safe (zero-trust model)
  
  Design implications:
  - Encrypt everything in transit (mTLS between services)
  - Authenticate every request (JWT, API keys, certificates)
  - Implement network segmentation
  - Never trust input from the network (validate everything)
```

### Fallacy 5: "Topology Doesn't Change"

```
  Reality: Servers are added, removed, and moved constantly.
  
  - Auto-scaling adds and removes instances dynamically
  - Deployments replace old instances with new ones
  - Network routes change due to failures or optimization
  - Cloud providers migrate VMs between physical hosts
  
  Design implications:
  - Use service discovery (Consul, etcd, Kubernetes DNS)
  - Don't hardcode IP addresses or hostnames
  - Design for dynamic membership (nodes join/leave)
  - Use load balancers to abstract backend topology
```

### Fallacy 6: "There Is One Administrator"

```
  Reality: Distributed systems span organizational boundaries.
  
  - Your app depends on AWS (their ops team), Cloudflare (their team),
    Stripe (their team), a database managed by your DBA team,
    and services owned by 5 different product teams.
  - Each has different SLAs, deployment schedules, and priorities.
  
  Design implications:
  - Build for independent failure of each dependency
  - Use circuit breakers for external services
  - Define and enforce SLAs at every boundary
  - Implement graceful degradation (work without non-critical deps)
```

### Fallacy 7: "Transport Cost Is Zero"

```
  Reality: Serialization, deserialization, and transport consume resources.
  
  Sending a 1 KB JSON payload over HTTP:
  - Serialize object to JSON: ~0.1 ms CPU
  - TLS encryption: ~0.05 ms CPU
  - TCP transmission: ~0.5 ms network
  - TLS decryption: ~0.05 ms CPU
  - Deserialize JSON to object: ~0.1 ms CPU
  
  Total cost: ~0.8 ms (vs. ~0.001 ms for a local function call)
  
  Design implications:
  - Use efficient serialization (protobuf, flatbuffers over JSON)
  - Reuse connections (connection pooling, HTTP/2 multiplexing)
  - Consider gRPC for internal service-to-service communication
  - Batch small messages into larger ones where possible
```

### Fallacy 8: "The Network Is Homogeneous"

```
  Reality: Your system runs on a mix of hardware, OS, and network equipment.
  
  - Linux servers, Windows machines, ARM-based devices
  - 1 Gbps, 10 Gbps, and 100 Gbps network links
  - WiFi, cellular, fiber, satellite connections
  - Different MTU sizes, different congestion control algorithms
  
  Design implications:
  - Use standard protocols (HTTP, gRPC, AMQP)
  - Don't assume byte order (use network byte order)
  - Test on different network conditions (simulate slow, lossy networks)
  - Design for the SLOWEST participant (weak link)
```

---

## 35.3 Failure Modes in Distributed Systems

Understanding how systems fail is the first step to building systems that tolerate failure.

### Failure Types — From Mildest to Worst

```
  +-------------------------------------------------------------------+
  | Failure Type      | Description                 | Severity         |
  +-------------------------------------------------------------------+
  | Fail-Stop         | Node stops and stays stopped | Easiest to handle|
  |                   | (clean crash). Other nodes   |                  |
  |                   | can detect it's down.        |                  |
  +-------------------------------------------------------------------+
  | Crash-Recovery    | Node crashes but eventually  | Common           |
  |                   | restarts. May have partial   |                  |
  |                   | state from before crash.     |                  |
  +-------------------------------------------------------------------+
  | Omission Failure  | Node fails to send or receive| Harder to detect |
  |                   | some messages (network drops).|                 |
  |                   | Node itself is still running.|                  |
  +-------------------------------------------------------------------+
  | Timing Failure    | Node responds, but too late  | Tricky           |
  |                   | (beyond the expected timeout).|                 |
  |                   | Is it failed? Or just slow?  |                  |
  +-------------------------------------------------------------------+
  | Byzantine Failure | Node behaves ARBITRARILY —   | HARDEST to handle|
  |                   | sends wrong data, lies, acts |                  |
  |                   | maliciously. Other nodes     |                  |
  |                   | cannot trust its messages.   |                  |
  +-------------------------------------------------------------------+
  
  The difficulty spectrum:
  
  Fail-Stop < Crash-Recovery < Omission < Timing < Byzantine
  (easy)                                                (hard)
  
  Most systems assume crash-recovery + omission failures.
  Byzantine tolerance is needed only for untrusted environments (blockchain).
```

### The Impossibility of Distinguishing Crash from Slow

```
  This is the FUNDAMENTAL dilemma of distributed systems:
  
  Node A sends a request to Node B. No response after 5 seconds.
  
  Possibility 1: Node B has CRASHED
  +--------+     Request     +--------+
  | Node A | --------------> | Node B |
  +--------+                 | CRASHED|
                             +--------+
  Correct action: Mark B as dead, failover to Node C.
  
  Possibility 2: Node B is SLOW (but alive)
  +--------+     Request     +--------+
  | Node A | --------------> | Node B |
  +--------+                 | Working|
                             | slowly |
                             +--------+
  Correct action: Wait longer. DO NOT failover (B is still processing!).
  
  Possibility 3: The NETWORK is partitioned
  +--------+     Request     +--------+
  | Node A | ------X-------> | Node B |
  +--------+   (dropped)     | HEALTHY|
                             +--------+
  Correct action: Depends on your consistency model (CP or AP).
  
  THE PROBLEM: Node A CANNOT TELL THE DIFFERENCE between these three.
  
  This is why:
  - Timeouts are an imperfect heuristic
  - Heartbeats reduce but don't eliminate ambiguity
  - Consensus protocols (Raft, Paxos) require a QUORUM to agree on "who is alive"
  - Google uses TrueTime (GPS + atomic clocks) to bound uncertainty
```

---

## 35.4 Failure Detection

Since you can't always tell if a node has failed, distributed systems use **failure detection
algorithms** to make probabilistic judgments.

### Heartbeat-Based Detection

```
  Simplest approach: Every node periodically sends "I'm alive" heartbeats.
  
  +--------+     Heartbeat (every 1s)     +--------+
  | Node A | ---> "I'm alive" ----------> | Monitor|
  +--------+                               +--------+
  | Node B | ---> "I'm alive" ----------> |        |
  +--------+                               |        |
  | Node C | ---> (no heartbeat for 5s) -> | Node C |
  +--------+                               | DEAD?  |
                                           +--------+
  
  Configuration:
  - Heartbeat interval: 1 second
  - Timeout threshold: 5 seconds (5 missed heartbeats)
  
  Trade-off:
  - Short timeout: fast detection, but more FALSE POSITIVES
    (a temporarily slow node is marked dead)
  - Long timeout: fewer false positives, but SLOW detection
    (a truly dead node is still receiving traffic for 30+ seconds)
  
  Used by: Kubernetes (kubelet heartbeats), Cassandra, most systems
```

### Phi Accrual Failure Detector

```
  A more sophisticated approach that provides a CONTINUOUS suspicion level
  (phi value) instead of a binary alive/dead decision.
  
  How it works:
  1. Monitor heartbeat ARRIVAL TIMES
  2. Build a statistical model of heartbeat intervals
  3. If a heartbeat is "late," calculate phi (suspicion level):
     phi = -log10(probability that heartbeat will still arrive)
  
  phi = 1:  10% chance heartbeat won't arrive  (probably alive)
  phi = 3:  0.1% chance                        (suspicious)
  phi = 5:  0.001% chance                      (very likely dead)
  phi = 8:  0.000001% chance                   (almost certainly dead)
  
  Normal operation (heartbeats arrive every ~1s):
  
  Heartbeats: |1.0s|1.1s|0.9s|1.0s|...|3.5s|  <- late!
                                        phi = 4.2 (suspicious!)
  
  Heartbeats: |1.0s|1.1s|0.9s|1.0s|...|8.0s|  <- very late!
                                        phi = 8.7 (dead!)
  
  Advantages over simple heartbeats:
  + Adapts to network conditions (if heartbeats are always jittery, phi
    threshold adjusts — doesn't false-positive on normal jitter)
  + Provides a CONTINUOUS suspicion level (not just alive/dead)
  + Application can choose its own phi threshold based on needs
  
  Used by: Akka (Scala/Java), Cassandra, Erlang/OTP
```

### Gossip-Based Failure Detection (SWIM Protocol)

```
  Instead of a centralized monitor, every node monitors a RANDOM subset
  of other nodes. This is scalable and avoids single-point-of-failure.
  
  SWIM (Scalable Weakly-consistent Infection-style Membership):
  
  Round 1: Node A randomly picks Node C to probe
  +---+     "ping"      +---+
  | A | --------------> | C |
  |   | <-------------- |   |
  +---+     "ack"       +---+     OK! C is alive.
  
  Round 2: Node A randomly picks Node E to probe
  +---+     "ping"      +---+
  | A | ------X-------> | E |     No response!
  +---+                  +---+
  
  Node A asks Node B and Node D to check E (indirect probe):
  +---+    "ping-req(E)"  +---+     "ping"     +---+
  | A | ----------------> | B | --------------> | E |
  +---+                   +---+                 +---+
                          +---+     "ping"     +---+
  | A | ----------------> | D | ------X------> | E |
  +---+                   +---+                 +---+
  
  If at least one indirect probe succeeds: E is alive (A's network to E is flaky).
  If ALL indirect probes fail: E is marked as SUSPECT.
  After a timeout, SUSPECT -> DEAD. Membership update gossipped to all nodes.
  
  How gossip spreads the failure info:
  
  Time 0: Only Node A knows E is dead
  Time 1: A tells B and C (via piggybacked gossip)
  Time 2: A, B, C know. B tells D, C tells F
  Time 3: A, B, C, D, F know. D tells G...
  Time N: All nodes know (O(log N) rounds to reach everyone)
  
  Properties:
  + O(1) network load per node per round (probe one random peer)
  + Failure information spreads in O(log N) rounds
  + No single point of failure (no central monitor)
  + False positive rate is low (indirect probes reduce false alarms)
  
  Used by: HashiCorp Consul/Serf, Cassandra (modified gossip), DynamoDB
```

---

## 35.5 Time, Clocks, and Ordering of Events

In a single machine, you can use the system clock to order events. In a distributed system,
clocks are **unreliable** and **unsynchronized.** This is one of the deepest challenges.

### The Problem with Physical Clocks

```
  Node A's clock: 10:00:00.000
  Node B's clock: 10:00:00.150  (150ms ahead of A!)
  
  Event X happens on Node A at A's time 10:00:00.100
  Event Y happens on Node B at B's time 10:00:00.050
  
  Question: Did X happen before Y?
  
  By A's clock: X at 10:00:00.100
  By B's clock: Y at 10:00:00.050
  
  Naive answer: Y happened first (10:00:00.050 < 10:00:00.100)
  Actual answer: X happened first! (A's real time was 10:00:00.100,
                 B's real time was 10:00:00.050 - 0.150 = 09:59:59.900)
  
  Physical clocks are inaccurate because:
  - Crystal oscillators drift: ~200 parts per million (17 seconds per day)
  - NTP synchronization has ~1-10ms accuracy (best case)
  - NTP can even make clocks go BACKWARDS (after correction)
  - VMs suffer additional clock skew from CPU time-slicing
```

### NTP (Network Time Protocol)

```
  NTP synchronizes clocks over the network with a hierarchy of time sources:
  
  Stratum 0: Atomic clocks, GPS receivers (nanosecond accuracy)
       |
  Stratum 1: Servers directly connected to Stratum 0 (microsecond)
       |
  Stratum 2: Servers syncing from Stratum 1 (millisecond)
       |
  Stratum 3: Your typical server (1-10ms accuracy)
  
  NTP clock correction:
  - If clock is slightly ahead/behind: SLEW (gradually adjust speed)
  - If clock is far off: STEP (jump to correct time — dangerous for apps!)
  
  Key insight: NTP gives you APPROXIMATE time agreement, not EXACT.
  For ordering events, you need something better.
```

### Lamport Clocks — Logical Ordering

```
  Leslie Lamport (1978) showed that you don't need PHYSICAL time to order
  events. You just need a LOGICAL clock that respects causality:
  
  "If event A happened before event B, then A's timestamp < B's timestamp."
  
  Algorithm (per node):
  1. Each node maintains a counter C
  2. Before each local event: C = C + 1
  3. When sending a message: attach C to the message
  4. When receiving a message with timestamp T: C = max(C, T) + 1
  
  Example:
  
  Node A (C=0)              Node B (C=0)              Node C (C=0)
       |                         |                         |
  a1 (C=1)                      |                         |
       |                         |                         |
  a2 (C=2) --send msg(C=2)-->  |                         |
       |                    b1 (C=3)  max(0,2)+1=3        |
       |                         |                         |
       |                    b2 (C=4) --send msg(C=4)-->   |
       |                         |                    c1 (C=5)
       |                         |                         |
  a3 (C=3)                      |                         |
  
  Lamport timestamps guarantee:
  If A "happened-before" B, then timestamp(A) < timestamp(B).
  
  BUT: If timestamp(A) < timestamp(B), we CANNOT conclude A happened before B.
  (Concurrent events may have any ordering of timestamps.)
  
  Used by: Many distributed systems as a cheap ordering mechanism.
```

### Vector Clocks — Detecting Concurrency

```
  Vector clocks extend Lamport clocks to detect CONCURRENT events
  (events that are causally unrelated).
  
  Each node maintains a VECTOR of counters, one per node in the system.
  
  Algorithm:
  1. Node i increments its own position in the vector before each event
  2. When sending: attach the full vector
  3. When receiving vector V: merge = max(local[j], V[j]) for all j, then increment own
  
  Example (3 nodes: A, B, C):
  
  Node A              Node B              Node C
  [0,0,0]             [0,0,0]             [0,0,0]
     |                    |                    |
  a1 [1,0,0]              |                    |
     |                    |                    |
  a2 [2,0,0] --msg-->    |                    |
     |               b1 [2,1,0]                |
     |                    |                    |
     |               b2 [2,2,0] --msg-->       |
     |                    |               c1 [2,2,1]
     |                    |                    |
  a3 [3,0,0]              |                    |
  
  Comparing vector clocks:
  - [2,2,0] < [2,2,1]  (B:b2 happened-before C:c1)  — CAUSAL ORDER
  - [3,0,0] vs [2,2,1] — NEITHER is less than the other — CONCURRENT!
    (A:a3 and C:c1 are concurrent events)
  
  Rule: V1 < V2 iff every element of V1 <= corresponding element of V2,
        AND at least one element is strictly less.
        If neither V1 < V2 nor V2 < V1, events are CONCURRENT.
  
  Used by: Amazon DynamoDB (conflict detection), Riak, CRDTs
  
  Downside: Vector size grows with number of nodes. Not practical for
  systems with thousands of nodes. Use Dotted Version Vectors or
  Hybrid Logical Clocks instead.
```

### Hybrid Logical Clocks (HLC)

```
  HLC combines physical time with logical counters:
  
  HLC = (physical_time, logical_counter)
  
  - physical_time: from NTP-synchronized clock
  - logical_counter: ensures causal ordering within the same physical_time
  
  Algorithm:
  On local event or send:
    pt = max(local.pt, physical_clock())
    if pt == local.pt: lc = local.lc + 1
    else: lc = 0
    local = (pt, lc)
  
  On receive message with timestamp (msg.pt, msg.lc):
    pt = max(local.pt, msg.pt, physical_clock())
    if pt == local.pt == msg.pt: lc = max(local.lc, msg.lc) + 1
    elif pt == local.pt: lc = local.lc + 1
    elif pt == msg.pt: lc = msg.lc + 1
    else: lc = 0
    local = (pt, lc)
  
  Benefits:
  + Bounded divergence from physical time (unlike pure Lamport clocks)
  + Respects causality (like vector clocks)
  + Compact: just (timestamp, counter) — no vector needed
  + Compatible with NTP (timestamps are close to real time)
  
  Used by: CockroachDB, MongoDB, YugabyteDB
```

### Google TrueTime — Bounded Uncertainty

```
  Google's Spanner uses TrueTime, which provides a TIME INTERVAL
  instead of a single timestamp:
  
  TrueTime.now() = [earliest, latest]
  
  "The actual time is guaranteed to be within this interval."
  
  How TrueTime achieves this:
  - GPS receivers in every data center (synchronized to atomic clocks)
  - Atomic clocks as backup (in case GPS fails)
  - Combined uncertainty: typically ~1-7 milliseconds
  
  Usage in Spanner:
  1. Transaction commits at timestamp T
  2. Spanner waits until TrueTime.now().earliest > T
  3. This guarantees NO other transaction can have a lower timestamp
  4. Result: externally consistent global transactions
  
  +-------------------------------------------------------------------+
  | Clock Technology  | Accuracy       | Use Case                     |
  +-------------------------------------------------------------------+
  | NTP               | 1-10 ms        | General purpose              |
  | PTP (Precision TP)| 1-100 μs       | HFT, telecom                 |
  | GPS + Atomic      | 1-7 ms (bound) | Google Spanner (TrueTime)    |
  | Lamport Clock     | N/A (logical)  | Causal ordering              |
  | Vector Clock      | N/A (logical)  | Concurrency detection        |
  | Hybrid Logical    | Near real-time | CockroachDB, MongoDB         |
  +-------------------------------------------------------------------+
```

---

## 35.6 Gossip Protocols

Gossip protocols (also called **epidemic protocols**) spread information through a distributed
system the way gossip spreads in a social network — each node tells a few random peers,
and information spreads exponentially.

### How Gossip Works

```
  Round 0: Only Node A has the information
  
  [A*] [B] [C] [D] [E] [F] [G] [H]        * = has info
  
  Round 1: A randomly tells 2 peers (B, E)
  
  [A*] [B*] [C] [D] [E*] [F] [G] [H]      3 nodes know
  
  Round 2: A, B, E each tell 2 random peers
  
  [A*] [B*] [C*] [D*] [E*] [F*] [G] [H]   6 nodes know
  
  Round 3: Most nodes gossip
  
  [A*] [B*] [C*] [D*] [E*] [F*] [G*] [H*] All nodes know!
  
  Convergence: O(log N) rounds for N nodes
  For 1000 nodes: ~10 rounds to reach everyone
  For 1,000,000 nodes: ~20 rounds
  
  Properties:
  + Scalable: each node sends fixed number of messages per round
  + Fault tolerant: works even if many nodes fail (probabilistic guarantee)
  + Decentralized: no leader, no coordinator
  + Eventually consistent: all nodes WILL converge to the same state
  
  - Redundant messages: same info may be sent multiple times
  - Non-deterministic: exact convergence time is probabilistic
  - Eventually consistent: NOT immediately consistent
```

### Types of Gossip

```
  1. ANTI-ENTROPY (Full State Exchange)
     Two nodes exchange their COMPLETE state and reconcile differences.
     Guarantees eventual convergence but high bandwidth.
     Used by: Dynamo, Cassandra (Merkle tree-based anti-entropy)
  
  2. RUMOR MONGERING (Epidemic Dissemination)
     Nodes spread "rumors" (updates) to random peers.
     When a node receives a rumor it already has, it probabilistically
     stops spreading it (to reduce redundancy).
     Used by: SWIM failure detection, cluster membership
  
  3. AGGREGATION GOSSIP
     Each node has a VALUE (e.g., its CPU load).
     Through gossip, nodes compute a GLOBAL AGGREGATE (e.g., average CPU).
     Each round: node exchanges value with a random peer, both update
     their local estimate to the average of the two.
     After O(log N) rounds: all nodes have a good estimate of the global average.
     Used by: Monitoring, load balancing, resource allocation
```

### Real-World Gossip Applications

```
  +-------------------------------------------------------------------+
  | System        | What Is Gossipped                                  |
  +-------------------------------------------------------------------+
  | Cassandra     | Cluster membership, token ring, schema changes,    |
  |               | node health, data center topology                   |
  +-------------------------------------------------------------------+
  | Consul/Serf   | Node membership (join/leave/fail), user events,    |
  |               | service health status                               |
  +-------------------------------------------------------------------+
  | DynamoDB      | Membership changes, partition assignment            |
  +-------------------------------------------------------------------+
  | Redis Cluster | Cluster state, slot assignment, failover decisions |
  +-------------------------------------------------------------------+
  | Blockchain    | Transaction propagation, block propagation          |
  +-------------------------------------------------------------------+
```

---

## 35.7 Distributed Coordination

Multiple nodes need to coordinate actions: elect leaders, acquire locks, agree on
configuration, and maintain membership. These are **coordination problems.**

### Leader Election

```
  Problem: Multiple nodes must agree on ONE leader who performs special duties
  (e.g., handling writes, coordinating tasks, managing resources).
  
  Approach 1: Bully Algorithm
  - Highest-ID node becomes leader
  - If leader fails, next highest-ID detects via timeout and claims leadership
  - Simple but: network partitions can cause SPLIT-BRAIN (two leaders!)
  
  Approach 2: Raft Leader Election (the standard)
  
  Term 1: Node C is leader
  +-----+     +-----+     +-----+
  | A   |     | B   |     | C   |  <- LEADER
  |follw|     |follw|     |leader|
  +-----+     +-----+     +-----+
  
  Node C crashes! After election timeout:
  
  +-----+     +-----+     +-----+
  | A   |     | B   |     | C   |
  |candi|     |follw|     | DEAD |
  | date|     |     |     |      |
  +-----+     +-----+     +-----+
  
  Node A starts election for Term 2:
  A -> B: "Vote for me in term 2?"
  B -> A: "Yes" (B hasn't voted for anyone in term 2 yet)
  
  A receives majority (2 of 3 = quorum) -> A becomes leader!
  
  Term 2: Node A is leader
  +-----+     +-----+     +-----+
  | A   |     | B   |     | C   |
  |LEADER|    |follw|     | DEAD |
  +-----+     +-----+     +-----+
  
  Key properties:
  - At most ONE leader per term (each node votes only once per term)
  - Requires majority quorum (no split-brain with odd number of nodes)
  - Election timeout is randomized to prevent ties
  
  Used by: etcd (Raft), ZooKeeper (ZAB), Consul (Raft), CockroachDB (Raft)
```

### Distributed Locks

```
  Problem: Two services must not process the same order simultaneously.
  
  Naive approach: Single Redis lock
  
  SET lock:order:42 "service-a" NX EX 30
  # NX = only if not exists, EX = expires in 30 seconds
  
  Problems with single-node Redis lock:
  - If Redis crashes, lock is lost -> two holders possible
  - If lock holder crashes, lock expires after TTL -> another process grabs it
  - If lock holder is slow (GC pause), lock expires while STILL PROCESSING
  
  Fencing Tokens (the solution to expired locks):
  
  +--------------------------------------------------------------------+
  | Service A acquires lock with fencing token #33                      |
  | Service A is slow (GC pause)... lock expires                       |
  | Service B acquires lock with fencing token #34                      |
  | Service A wakes up, tries to write with token #33                  |
  | Storage server: "Token #33 < #34. REJECTED. Stale lock holder!"   |
  +--------------------------------------------------------------------+
  
  Timeline:
  Service A: lock(token=33) ... [GC pause] ... write(token=33) -> REJECTED
  Service B:                  lock(token=34) ... write(token=34) -> ACCEPTED
  
  The storage server tracks the highest token it has seen.
  Any write with a LOWER token is rejected (stale lock holder).
  
  Redlock (distributed lock across multiple Redis masters):
  
  To acquire a lock:
  1. Get current time T1
  2. Try to SET NX EX on N Redis masters (e.g., 5 masters)
  3. If lock acquired on MAJORITY (>= N/2 + 1 = 3), AND
     elapsed time (T2 - T1) < lock TTL -> lock is acquired!
  4. If failed, release lock on ALL masters
  
  +-------+  +-------+  +-------+  +-------+  +-------+
  | Redis |  | Redis |  | Redis |  | Redis |  | Redis |
  |   1   |  |   2   |  |   3   |  |   4   |  |   5   |
  | LOCKED|  | LOCKED|  | LOCKED|  | failed|  | failed|
  +-------+  +-------+  +-------+  +-------+  +-------+
  3 of 5 locked = MAJORITY -> lock acquired!
```

### ZooKeeper and etcd — Coordination Services

```
  ZooKeeper and etcd are specialized distributed systems that provide
  coordination primitives for other distributed systems.
  
  +-------------------------------------------------------------------+
  | Primitive           | ZooKeeper           | etcd                   |
  +-------------------------------------------------------------------+
  | Data model          | Hierarchical (tree) | Flat key-value         |
  |                     | /app/config/db_url  | /app/config/db_url     |
  +-------------------------------------------------------------------+
  | Consensus           | ZAB protocol        | Raft protocol          |
  +-------------------------------------------------------------------+
  | Watch/Notify        | Watcher callbacks   | Watch API (streaming)  |
  +-------------------------------------------------------------------+
  | Ephemeral nodes     | Yes (auto-delete on | Lease-based (similar)  |
  |                     | session disconnect) |                        |
  +-------------------------------------------------------------------+
  | Typical cluster     | 3 or 5 nodes        | 3 or 5 nodes           |
  +-------------------------------------------------------------------+
  | Written in          | Java                | Go                     |
  +-------------------------------------------------------------------+
  | Used by             | Kafka, Hadoop,      | Kubernetes, CoreDNS,   |
  |                     | HBase, Solr         | Vitess, CockroachDB    |
  +-------------------------------------------------------------------+
  
  Common use cases:
  1. Configuration management (dynamic config pushed to all services)
  2. Service discovery (register/deregister service instances)
  3. Leader election (ephemeral nodes + watches)
  4. Distributed locks (sequential ephemeral znodes)
  5. Cluster membership (who is alive?)
  6. Barriers and queues (synchronize distributed tasks)
```

---

## 35.8 Network Partitions and Split-Brain

A **network partition** occurs when the network divides a cluster into two or more groups
that can communicate internally but NOT with each other.

```
  Normal operation:
  +---+   +---+   +---+   +---+   +---+
  | A |---| B |---| C |---| D |---| E |
  +---+   +---+   +---+   +---+   +---+
  
  Network partition:
  +---+   +---+     X     +---+   +---+
  | A |---| B |     X     | C |---| D |
  +---+   +---+     X     +---+   +---+
                    X
  Partition 1       X     Partition 2
  (A, B)            X     (C, D, E — has node E too)
  
  Both partitions are internally healthy.
  But they CANNOT communicate with each other.
```

### The Split-Brain Problem

```
  If both partitions have a leader:
  
  Partition 1: "B is our leader!" -> accepts writes
  Partition 2: "D is our leader!" -> accepts writes
  
  Result: TWO independent leaders accepting CONFLICTING writes!
  When the partition heals, the data has DIVERGED. This is a DISASTER
  for systems requiring consistency.
  
  Real-world example:
  - Database master in Partition 1 writes: user.balance = $900
  - Database master in Partition 2 writes: user.balance = $1100
  - Partition heals: which balance is correct? BOTH are "valid."
  
  Prevention strategies:
  
  1. QUORUM-BASED (most common):
     A leader needs votes from a MAJORITY (N/2 + 1) to operate.
     With 5 nodes, need 3 votes.
     Only ONE partition can have a majority -> only ONE leader.
     
     Partition 1 (2 nodes): Cannot form quorum -> becomes READ-ONLY
     Partition 2 (3 nodes): Has quorum -> continues operating
  
  2. FENCING (STONITH — Shoot The Other Node In The Head):
     When a new leader is elected, the old leader is FORCIBLY shut down
     (via management interface, power control, etc.) to guarantee
     there's only one leader.
     Used by: Pacemaker (Linux HA), some database clusters
  
  3. LEASE-BASED:
     Leader holds a TIME-BOUNDED LEASE.
     Lease must be renewed periodically.
     If leader can't reach quorum to renew lease, it steps down.
     New leader cannot be elected until old lease expires.
     
     Used by: Google Chubby, etcd, ZooKeeper (session timeouts)
```

---

## 35.9 Byzantine Fault Tolerance

**Byzantine failures** are the worst kind: a node doesn't just crash, it actively sends
**wrong, contradictory, or malicious data** to other nodes.

```
  The Byzantine Generals Problem (Lamport, 1982):
  
  3 generals must agree on "ATTACK" or "RETREAT."
  One general is a TRAITOR who sends conflicting messages.
  
  General A (loyal): "ATTACK"
  General B (loyal): "ATTACK"  
  General C (TRAITOR): tells A "ATTACK", tells B "RETREAT"
  
  A thinks: 2 ATTACK, 0 RETREAT -> ATTACK
  B thinks: 1 ATTACK, 1 RETREAT -> ??? (no consensus!)
  
  Result: Loyal generals take different actions = DISASTER.
  
  Theorem: To tolerate f Byzantine faults, you need at least 3f + 1 nodes.
  - Tolerate 1 Byzantine node: need 4 nodes
  - Tolerate 2 Byzantine nodes: need 7 nodes
```

### When Do You Need BFT?

```
  +-------------------------------------------------------------------+
  | Environment              | BFT Needed? | Why                      |
  +-------------------------------------------------------------------+
  | Public blockchain        | YES         | Nodes are untrusted,     |
  |                          |             | financially incentivized |
  |                          |             | to cheat                 |
  +-------------------------------------------------------------------+
  | Private enterprise       | NO          | Nodes controlled by one  |
  | cluster                  |             | organization, trusted    |
  +-------------------------------------------------------------------+
  | Cloud infrastructure     | RARELY      | Cloud provider is trusted|
  | (AWS, GCP, Azure)        |             | (crash faults, not       |
  |                          |             |  Byzantine)              |
  +-------------------------------------------------------------------+
  | Financial systems with   | SOMETIMES   | Regulatory compliance    |
  | multiple parties         |             | may require BFT          |
  +-------------------------------------------------------------------+
  
  Most distributed systems (databases, message queues, service meshes)
  assume CRASH FAULTS only. BFT is dramatically more expensive:
  
  Crash fault tolerance: Need 2f + 1 nodes to tolerate f failures
  Byzantine fault tolerance: Need 3f + 1 nodes to tolerate f failures
  
  BFT message complexity: O(n^2) per consensus round
  Crash FT (Raft): O(n) per consensus round
```

---

## 35.10 Distributed System Design Patterns

These patterns are the building blocks for solving recurring problems in distributed systems.

### Pattern 1: Write-Ahead Log (WAL)

```
  Problem: A node crashes mid-operation. How do you recover?
  
  Solution: Before modifying state, write the INTENT to a durable log.
  On recovery, replay the log to restore state.
  
  Operation:
  1. Write to WAL: "UPDATE balance SET amount=900 WHERE user_id=42"
  2. Apply the change to in-memory state
  3. Acknowledge to client
  
  Crash at step 2? On restart, replay WAL -> state is recovered.
  
  Used by: PostgreSQL (WAL), MySQL (redo log), Kafka (commit log),
           etcd (Raft log), every serious database
```

### Pattern 2: Quorum

```
  Problem: How to ensure read/write consistency across replicas?
  
  Solution: Require a MINIMUM number of nodes to agree on reads and writes.
  
  W + R > N  guarantees overlap between write and read sets.
  
  N = 3 replicas
  W = 2 (write to at least 2 replicas)
  R = 2 (read from at least 2 replicas)
  W + R = 4 > 3 = N -> guaranteed to read latest write
  
  Write:                          Read:
  Client -> [R1: write OK]       Client -> [R1: v2] <-- latest
  Client -> [R2: write OK]       Client -> [R2: v1] <-- stale
  Client -> [R3: timeout, skip]  -> Return v2 (higher version wins)
  
  2 of 3 written (W=2 satisfied)  2 of 3 read (R=2 satisfied)
                                   At least 1 overlap -> get latest!
  
  Common configurations:
  +-------+----+----+-------------------------------------------+
  | N     | W  | R  | Trade-off                                 |
  +-------+----+----+-------------------------------------------+
  | 3     | 2  | 2  | Balanced consistency + availability       |
  | 3     | 3  | 1  | Strong writes, fast reads                 |
  | 3     | 1  | 3  | Fast writes, strong reads                 |
  | 3     | 1  | 1  | Maximum availability, eventual consistency|
  +-------+----+----+-------------------------------------------+
  
  Used by: Cassandra, DynamoDB, Riak
```

### Pattern 3: Consistent Hashing

```
  Problem: Distribute data across N nodes. When nodes join/leave,
  minimize data movement.
  
  Hash ring: keys and nodes mapped to positions on a ring.
  Each key is assigned to the NEXT node clockwise on the ring.
  Adding a node only affects keys between it and its predecessor.
  
  See Chapter 11 for detailed explanation with diagrams.
  
  Used by: DynamoDB, Cassandra, Memcached, load balancers
```

### Pattern 4: Merkle Tree (Hash Tree)

```
  Problem: How to detect differences between two large datasets
  efficiently? (e.g., which data is out of sync between replicas)
  
  Solution: Build a tree of hashes. Compare from root down.
  If roots match -> data is identical (no need to compare further).
  If roots differ -> traverse down to find exactly which blocks differ.
  
  Dataset: [A, B, C, D]
  
                  Hash(H12 + H34) = ROOT
                  /              \
          H12 = Hash(H1+H2)    H34 = Hash(H3+H4)
          /          \          /          \
    H1=Hash(A)  H2=Hash(B)  H3=Hash(C)  H4=Hash(D)
       |           |           |           |
       A           B           C           D
  
  Comparing two replicas:
  Replica 1 root: abc123         Replica 2 root: abc123
  -> MATCH! Data is identical. Done! (Only 1 comparison needed)
  
  Replica 1 root: abc123         Replica 2 root: xyz789
  -> DIFFER! Compare children:
     H12 matches -> A and B are synced
     H34 differs -> Compare H3 and H4
       H3 matches -> C is synced
       H4 differs -> D is out of sync! Transfer only D.
  
  Efficiency: O(log N) comparisons instead of O(N) full data scan.
  To sync 1 billion records, only ~30 hash comparisons needed
  to identify the exact records that differ.
  
  Used by: Cassandra (anti-entropy repair), DynamoDB, Git,
           BitTorrent, IPFS, blockchain (every block links hashes)
```

### Pattern 5: Sidecar

```
  Problem: You want to add cross-cutting concerns (logging, monitoring,
  auth, networking) without modifying application code.
  
  Solution: Deploy a helper process alongside each application instance.
  
  +------------------------------------------------------------+
  | Pod / VM                                                    |
  | +--------------------+    +--------------------------+     |
  | | Application        |    | Sidecar Proxy            |     |
  | | (your code, any    |<-->| (Envoy, Linkerd)         |     |
  | |  language)         |    |                          |     |
  | | Knows nothing      |    | Handles:                 |     |
  | | about networking   |    | - Service discovery      |     |
  | +--------------------+    | - Load balancing         |     |
  |                           | - mTLS encryption        |     |
  |                           | - Retries + circuit break|     |
  |                           | - Metrics + tracing      |     |
  |                           +--------------------------+     |
  +------------------------------------------------------------+
  
  Used by: Istio (Envoy sidecar), Linkerd, Consul Connect
```

### Pattern 6: Circuit Breaker

```
  Problem: A downstream service is failing. Continuing to call it:
  - Wastes resources on doomed requests
  - Can cascade the failure to YOUR service (thread pool exhaustion)
  
  Solution: Track failures. If too many, STOP calling for a while.
  
  States:
  CLOSED (normal) -> failures exceed threshold -> OPEN (reject all calls)
  OPEN -> after timeout -> HALF-OPEN (allow ONE test request)
  HALF-OPEN -> test succeeds -> CLOSED (back to normal)
  HALF-OPEN -> test fails -> OPEN (wait longer)
  
  +--------+    failures > threshold    +------+
  | CLOSED |  ========================> | OPEN |
  | (allow |                            |(deny |
  |  all)  |  <======================== | all) |
  +--------+    test request succeeds   +--+---+
       ^                                   |
       |         timeout expires           |
       |                                   v
       |                              +---------+
       +-------- test succeeds -------| HALF-   |
                                      | OPEN    |
                 test fails --------->| (allow  |
                 (back to OPEN)       |  one)   |
                                      +---------+
  
  Used by: Netflix Hystrix, Resilience4j, Polly (.NET)
```

### Pattern 7: Saga Pattern

```
  Problem: Distributed transaction across multiple services
  (can't use 2PC due to availability concerns).
  
  Solution: Break into a sequence of local transactions, each with
  a compensating action (undo).
  
  Order Saga:
  1. Create Order (Payment Service)
     Compensate: Cancel Order
  2. Reserve Inventory (Inventory Service)
     Compensate: Release Inventory
  3. Charge Payment (Payment Service)
     Compensate: Refund Payment
  4. Ship Order (Shipping Service)
     Compensate: Cancel Shipment
  
  If step 3 fails:
  -> Compensate step 2: Release Inventory
  -> Compensate step 1: Cancel Order
  -> User notified: "Payment failed, order cancelled"
  
  See Chapter 24 for detailed saga patterns (choreography vs orchestration).
```

### Pattern 8: Event Sourcing

```
  Problem: Storing only current state loses HISTORY. Can't answer
  "what did the system look like at 3:00 PM yesterday?"
  
  Solution: Store every STATE CHANGE as an immutable EVENT.
  Current state = replay all events from the beginning.
  
  Events (immutable log):
  1. AccountCreated { id: 42, name: "Alice" }
  2. MoneyDeposited { id: 42, amount: 1000 }
  3. MoneyWithdrawn { id: 42, amount: 200 }
  4. MoneyDeposited { id: 42, amount: 500 }
  
  Current state: replay 1->2->3->4 -> balance = $1300
  State at event 2: replay 1->2 -> balance = $1000
  
  Used by: Event-driven systems, banking (audit trail), CQRS systems
  See Chapter 22 for event-driven architecture details.
```

### Pattern 9: CQRS (Command Query Responsibility Segregation)

```
  Problem: Reads and writes have different scalability and optimization needs.
  
  Solution: Separate the WRITE model (commands) from the READ model (queries).
  
  +--------+    Command     +-------+    Events    +-------+
  | Client |  (write)  -->  | Write |  -------->  | Event |
  +--------+                | Model |              | Store |
                            +-------+              +---+---+
                                                       |
  +--------+    Query       +-------+    Projections   |
  | Client |  (read)  -->   | Read  | <---------------+
  +--------+                | Model |
                            +-------+
  
  Write model: optimized for validation, business rules, consistency
  Read model: optimized for queries, denormalized, fast reads
  
  See Chapter 23 for detailed CQRS patterns.
```

### Pattern 10: Outbox Pattern

```
  Problem: Dual writes — updating a database AND publishing an event.
  If the DB write succeeds but the event publish fails (or vice versa),
  the system becomes inconsistent.
  
  Solution: Write the event to an OUTBOX TABLE in the SAME database
  transaction. A separate process reads the outbox and publishes events.
  
  +--------+    Single DB Transaction     +--------+
  | App    | --> UPDATE orders SET ...     | DB     |
  | Server | --> INSERT INTO outbox(event) |        |
  +--------+    COMMIT                    +---+----+
                                              |
               +------------------------------+
               |
  +------------v----------+     +-----------+
  | Outbox Publisher       |---->| Kafka /   |
  | (reads outbox table,  |     | RabbitMQ  |
  |  publishes events,    |     +-----------+
  |  marks as published)  |
  +------------------------+
  
  The database transaction guarantees atomicity:
  BOTH the state change AND the outbox event are committed or neither is.
  
  See Chapter 24 for detailed outbox pattern implementation.
```

### Complete Pattern Catalog — Quick Reference

```
  +----------------------------+-----------------------------------------------+
  | Pattern                    | When to Use                                   |
  +----------------------------+-----------------------------------------------+
  | Write-Ahead Log (WAL)      | Crash recovery, durability                    |
  | Quorum                     | Tunable consistency in replicated systems     |
  | Consistent Hashing         | Data distribution with minimal reshuffling    |
  | Merkle Tree                | Efficient data sync between replicas          |
  | Sidecar                    | Cross-cutting concerns without code changes   |
  | Circuit Breaker            | Prevent cascading failures                    |
  | Saga                       | Distributed transactions without 2PC          |
  | Event Sourcing             | Audit trail, temporal queries, replay         |
  | CQRS                       | Separate read/write optimization              |
  | Outbox                     | Reliable event publishing + DB consistency    |
  | Bulkhead                   | Isolate failures (separate thread pools)      |
  | Retry with Backoff         | Transient failure recovery                    |
  | Idempotency Key            | Safe retries (prevent duplicate processing)   |
  | Leader Election             | Single coordinator for writes/tasks           |
  | Lease                      | Time-bounded resource ownership               |
  | Fencing Token              | Prevent stale clients from writing             |
  | Bloom Filter               | Probabilistic set membership (avoid disk I/O) |
  | Strangler Fig              | Incremental migration from monolith           |
  | Anti-Corruption Layer      | Isolate new system from legacy system         |
  | Ambassador                 | Offload connectivity concerns to proxy        |
  +----------------------------+-----------------------------------------------+
```

---

## 35.11 Observability in Distributed Systems

When a request spans 10 services across 3 data centers, how do you debug a slow response?
**Observability** provides the answer through three pillars: metrics, logs, and traces.

### The Three Pillars

```
  +-------------------------------------------------------------------+
  |                    OBSERVABILITY                                   |
  +-------------------------------------------------------------------+
  |                                                                   |
  |  METRICS              LOGS                 TRACES                  |
  |  (What happened?)     (Why did it happen?) (Where did it happen?) |
  |                                                                   |
  |  - Request rate        - Error messages     - End-to-end request  |
  |  - Error rate          - Stack traces        flow across services |
  |  - Latency (p50/p99)  - Audit events       - Per-service timing  |
  |  - CPU, memory, disk  - Debug info          - Bottleneck ID       |
  |  - Queue depth         - Structured JSON    - Dependency map      |
  |                                                                   |
  |  Tools:                Tools:               Tools:                |
  |  Prometheus            ELK Stack            Jaeger                |
  |  Grafana               Splunk               Zipkin                |
  |  Datadog               Datadog              Datadog               |
  |  CloudWatch            CloudWatch           AWS X-Ray             |
  +-------------------------------------------------------------------+
```

### Distributed Tracing — Following a Request Across Services

```
  User -> API Gateway -> Auth Service -> Order Service -> Payment -> DB
  
  Without tracing: "The request took 2.5 seconds. Which service was slow?"
  
  With tracing (each service adds a span):
  
  Trace ID: abc-123-def (propagated through ALL services)
  
  |------ API Gateway (50ms) -------------------------------------------|
     |-- Auth Service (15ms) --|
                                |-- Order Service (200ms) --------------|
                                   |-- DB Query (150ms) --|
                                                           |-- Payment (2000ms) --|
  
  Total: 2265ms. Root cause: Payment service took 2000ms!
  
  How it works:
  1. First service generates a Trace ID (unique per request)
  2. Each service creates a SPAN (start time, end time, service name)
  3. Trace ID + Span ID propagated via HTTP headers:
     traceparent: 00-abc123def-span456-01
  4. Each service sends its span to a tracing backend (Jaeger, Zipkin)
  5. Backend assembles the full trace from all spans
  
  OpenTelemetry standard headers:
  traceparent: 00-<trace-id>-<span-id>-<flags>
  tracestate: vendor-specific data
```

### Correlation IDs

```
  A simpler alternative to full tracing: propagate a single REQUEST ID
  through all services and include it in every log line.
  
  API Gateway generates: X-Request-ID: req-abc-123
  
  Auth Service log:  [req-abc-123] Validated JWT for user 42
  Order Service log: [req-abc-123] Creating order for user 42, product 99
  Payment log:       [req-abc-123] Charging $49.99 to card ending 4242
  Payment log:       [req-abc-123] ERROR: Payment gateway timeout after 2000ms
  
  To debug: grep "req-abc-123" across ALL service logs.
  
  Implementation:
  1. API Gateway generates UUID and adds X-Request-ID header
  2. Every service reads X-Request-ID from incoming request
  3. Every service includes it in ALL log lines
  4. Every outgoing request forwards X-Request-ID header
  5. Centralized logging (ELK/Splunk) allows searching by request ID
```

---

## 35.12 MapReduce and Distributed Batch Processing

When you need to process petabytes of data, no single machine can do it. **MapReduce** is
the foundational pattern for distributed batch processing.

### How MapReduce Works

```
  Problem: Count word frequency across 1 TB of documents stored on 1000 servers.
  
  Phase 1: MAP (parallel, on each machine where data lives)
  
  Machine 1: "the cat sat on the mat"
  -> Map output: [(the,1), (cat,1), (sat,1), (on,1), (the,1), (mat,1)]
  
  Machine 2: "the dog sat on the log"
  -> Map output: [(the,1), (dog,1), (sat,1), (on,1), (the,1), (log,1)]
  
  Phase 2: SHUFFLE (group by key, redistribute across machines)
  
  All (the, *) -> Reducer 1: [(the,1), (the,1), (the,1), (the,1)]
  All (cat, *) -> Reducer 2: [(cat,1)]
  All (sat, *) -> Reducer 2: [(sat,1), (sat,1)]
  All (dog, *) -> Reducer 3: [(dog,1)]
  
  Phase 3: REDUCE (aggregate per key)
  
  Reducer 1: (the, [1,1,1,1]) -> (the, 4)
  Reducer 2: (cat, [1]) -> (cat, 1), (sat, [1,1]) -> (sat, 2)
  Reducer 3: (dog, [1]) -> (dog, 1)
  
  +--------+     +--------+     +--------+
  | Data 1 |     | Data 2 |     | Data 3 |
  | MAP    |     | MAP    |     | MAP    |
  +---+----+     +---+----+     +---+----+
      |              |              |
      +----- SHUFFLE (by key) ------+
      |              |              |
  +---v----+     +---v----+     +---v----+
  | REDUCE |     | REDUCE |     | REDUCE |
  | key:the|     | key:cat|     | key:dog|
  | =4     |     | sat=2  |     | =1     |
  +--------+     +--------+     +--------+
  
  Used by: Hadoop MapReduce, Google's original MapReduce (2004 paper)
```

### Beyond MapReduce

```
  MapReduce was revolutionary but has limitations:
  - Writes intermediate results to disk (slow)
  - Multi-stage jobs require chaining MapReduce steps (complex)
  - Not suitable for iterative algorithms (ML training)
  
  Modern alternatives:
  +-------------------------------------------------------------------+
  | System          | Improvement over MapReduce                       |
  +-------------------------------------------------------------------+
  | Apache Spark    | In-memory processing (10-100x faster), DAG-based |
  |                 | execution, supports SQL, ML, streaming           |
  +-------------------------------------------------------------------+
  | Apache Flink    | True stream processing (not micro-batches),      |
  |                 | exactly-once semantics, event-time processing    |
  +-------------------------------------------------------------------+
  | Google Dataflow | Unified batch + stream model, auto-scaling,      |
  | (Apache Beam)   | serverless, portable across runners             |
  +-------------------------------------------------------------------+
  | Presto/Trino    | Interactive SQL queries on distributed data,     |
  |                 | no ETL needed, federated queries across sources  |
  +-------------------------------------------------------------------+
```

---

## 35.13 Real-World Distributed Systems

### Google's Infrastructure Stack

```
  +-------------------------------------------------------------------+
  |                    GOOGLE'S STACK                                  |
  +-------------------------------------------------------------------+
  |                                                                   |
  | Borg (2004) -> Kubernetes (2014, open-sourced)                    |
  |   Cluster management, container orchestration                     |
  |                                                                   |
  | GFS (2003) -> Colossus                                            |
  |   Distributed file system for petabyte-scale storage              |
  |                                                                   |
  | MapReduce (2004) -> Flume -> Cloud Dataflow                       |
  |   Batch and stream processing                                     |
  |                                                                   |
  | Bigtable (2006) -> Cloud Bigtable                                 |
  |   Wide-column store for structured data at massive scale          |
  |                                                                   |
  | Chubby -> etcd (spiritual successor in open-source)               |
  |   Distributed lock service using Paxos consensus                  |
  |                                                                   |
  | Spanner (2012) -> Cloud Spanner                                   |
  |   Globally distributed SQL database with TrueTime                 |
  |   First system to achieve external consistency at global scale    |
  |                                                                   |
  | Pub/Sub (internal) -> Cloud Pub/Sub                               |
  |   Global message bus for event-driven architectures               |
  +-------------------------------------------------------------------+
  
  Key lesson from Google: EVERY major open-source distributed system
  was inspired by a Google paper. Read the papers.
```

### Amazon's Distributed Architecture

```
  +-------------------------------------------------------------------+
  |                    AMAZON / AWS STACK                              |
  +-------------------------------------------------------------------+
  |                                                                   |
  | Dynamo (2007 paper) -> DynamoDB                                   |
  |   Leaderless replication, consistent hashing, vector clocks,      |
  |   tunable consistency (eventually consistent by default)          |
  |   Key innovation: "always writable" even during partitions        |
  |                                                                   |
  | S3 (2006)                                                         |
  |   Object storage with 11 9's of durability                       |
  |   Stores trillions of objects, petabytes of data                  |
  |   Uses consistent hashing for distribution                       |
  |                                                                   |
  | SQS (2006)                                                        |
  |   Distributed message queue. At-least-once delivery.              |
  |   Designed for decoupling microservices.                          |
  |                                                                   |
  | Kinesis                                                           |
  |   Real-time stream processing (like Kafka, AWS-managed)           |
  |                                                                   |
  | Aurora                                                            |
  |   Distributed MySQL/PostgreSQL with storage-compute separation    |
  |   6-way replication across 3 AZs, 4/6 write quorum, 3/6 read    |
  +-------------------------------------------------------------------+
  
  Key lesson: Amazon's "two-pizza teams" each own a service.
  Services communicate ONLY via APIs (no shared databases).
  This forced them to solve distributed systems problems early.
```

### Meta (Facebook) Distributed Infrastructure

```
  +-------------------------------------------------------------------+
  |                    META'S STACK                                    |
  +-------------------------------------------------------------------+
  |                                                                   |
  | TAO (The Associations and Objects)                                |
  |   Distributed cache for the social graph                          |
  |   Billions of nodes and edges, cached across thousands of         |
  |   Memcached servers with read-after-write consistency             |
  |                                                                   |
  | Cassandra (co-created by Facebook, then open-sourced)             |
  |   Wide-column store for Inbox Search (initially)                  |
  |                                                                   |
  | RocksDB (created by Facebook, open-sourced)                       |
  |   Embedded key-value store, LSM-tree based                       |
  |   Used inside MySQL (MyRocks), Kafka, CockroachDB                |
  |                                                                   |
  | Thrift (created by Facebook, open-sourced)                        |
  |   Cross-language RPC framework (predecessor to gRPC concepts)    |
  |                                                                   |
  | ZippyDB                                                           |
  |   Distributed key-value store built on RocksDB + Paxos           |
  |   Used for: durable message queues, counters, metadata storage   |
  +-------------------------------------------------------------------+
  
  Key lesson: Meta invested heavily in CACHING. Their architecture
  philosophy: "Cache everything, compute at the edge, tolerate
  eventual consistency for social data."
```

---

## Key Takeaways

```
+-----------------------------------------------------------------------+
|              Distributed Systems — Key Takeaways                       |
+-----------------------------------------------------------------------+
|                                                                        |
|  1. A distributed system has NO shared memory, NO global clock, and   |
|     PARTIAL failures. These three properties make everything hard.     |
|                                                                        |
|  2. Internalize the 8 Fallacies: the network is unreliable, latency   |
|     is non-zero, bandwidth is finite, the network is insecure,        |
|     topology changes, there are many administrators, transport has     |
|     cost, and the network is heterogeneous.                           |
|                                                                        |
|  3. You CANNOT distinguish a crashed node from a slow node from a     |
|     network partition. This fundamental ambiguity drives the need      |
|     for timeouts, heartbeats, quorums, and consensus protocols.       |
|                                                                        |
|  4. Failure detection is probabilistic, not certain. Use heartbeats    |
|     for simple systems, phi accrual for adaptive detection, and       |
|     SWIM gossip for large-scale peer-to-peer membership.             |
|                                                                        |
|  5. Physical clocks are unreliable across nodes. Use Lamport clocks   |
|     for causal ordering, vector clocks for concurrency detection,     |
|     HLC for practical systems, and TrueTime for global consistency.   |
|                                                                        |
|  6. Gossip protocols spread information in O(log N) rounds. They are  |
|     scalable, fault-tolerant, and decentralized — ideal for cluster   |
|     membership, failure detection, and state dissemination.           |
|                                                                        |
|  7. Distributed coordination (leader election, locks) requires        |
|     consensus. Use ZooKeeper or etcd for coordination primitives.     |
|     Use fencing tokens to prevent stale lock holders from writing.    |
|                                                                        |
|  8. Network partitions cause split-brain. Prevent with quorum-based   |
|     voting (only majority partition can operate). Byzantine fault     |
|     tolerance is needed only for untrusted environments.              |
|                                                                        |
|  9. Master the design patterns: WAL for recovery, Quorum for          |
|     consistency, Circuit Breaker for resilience, Saga for distributed |
|     transactions, Event Sourcing for audit trails, Outbox for         |
|     reliable event publishing, Sidecar for cross-cutting concerns.    |
|                                                                        |
| 10. Observability is non-negotiable. Implement distributed tracing    |
|     (OpenTelemetry), correlation IDs in logs, and comprehensive       |
|     metrics (request rate, error rate, latency percentiles).          |
|                                                                        |
| 11. Study the foundational systems: Google (Spanner, Bigtable, GFS), |
|     Amazon (Dynamo, S3, Aurora), Meta (TAO, RocksDB). Every major     |
|     open-source distributed system was inspired by their papers.      |
|                                                                        |
| 12. The golden rule: don't distribute unless you must. A single       |
|     well-tuned PostgreSQL server handles more than most people think. |
|     Distribution adds enormous complexity — make sure the benefits    |
|     (scale, fault tolerance, latency) justify the cost.              |
|                                                                        |
+-----------------------------------------------------------------------+
```

---

*This chapter provides the theoretical foundation. For specific implementations, see:
[Ch 11 — Partitioning](./11-data-partitioning.md) |
[Ch 12 — Replication](./12-data-replication.md) |
[Ch 13 — Consistency](./13-consistency.md) |
[Ch 24 — Distributed Transactions](./24-distributed-transactions.md) |
[Ch 25 — Consensus Algorithms](./25-consensus.md)*
