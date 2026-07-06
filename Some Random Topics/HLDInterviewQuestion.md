# High-Level Design (HLD) Interview Questions

### A Practical Workbook of System Design Problems, Solved Step by Step

---

> **Who this book is for:** Engineers preparing for High-Level Design / System Design interviews who want full, worked solutions -- not hand-wavy bullet points. Each problem is solved the way you should solve it on a whiteboard: requirements first, capacity math, an architecture diagram, then a defense of every design choice and its trade-offs.
>
> **How each problem is structured:** Problem statement -> clarifying questions -> functional & non-functional requirements -> capacity estimation (the math) -> API design -> high-level architecture (ASCII diagram) -> component deep-dive -> data flow -> the architectural patterns used and *why* -> bottlenecks & scaling -> failure handling -> trade-offs -> summary.
>
> **A note on diagrams:** All diagrams are text-based (ASCII) so they render anywhere. Arrows show the direction of a request or data flow.

---

## Index

1. **[Problem 1: Design a Scalable Search REST API](#problem-1-design-a-scalable-search-rest-api)** -- a string-search API serving **1M concurrent users**, searching **millions of records**, with a **2-3 second** response SLA.
   - 1.1 Problem Statement
   - 1.2 Step 1: Clarifying Questions
   - 1.3 Step 2: Requirements (Functional & Non-Functional)
   - 1.4 Step 3: Capacity Estimation (Back-of-the-Envelope)
   - 1.5 Step 4: API Design
   - 1.6 Step 5: High-Level Architecture
   - 1.7 Step 6: Component Deep-Dive
   - 1.8 Step 7: The Search Engine (Why Not SQL `LIKE`?)
   - 1.9 Step 8: End-to-End Request Flow
   - 1.10 Step 9: Architectural Patterns Used (and Why)
   - 1.11 Step 10: Scaling to 1M Concurrent Users
   - 1.12 Step 11: Meeting the 2-3 Second Latency SLA
   - 1.13 Step 12: Failure Handling & Resilience
   - 1.14 Step 13: Trade-offs & Alternatives
   - 1.15 Problem 1 Summary
2. **[Problem 2: How Caching Enhances System Performance (Deep Dive with Redis)](#problem-2-how-caching-enhances-system-performance-deep-dive-with-redis)** -- why caching works, where to place it, the strategies, and a full internals tour of **Redis**.
   - 2.1 The Question
   - 2.2 Why Caching Works: The Core Principle
   - 2.3 How Caching Enhances Performance (the Concrete Wins)
   - 2.4 Where Caching Lives: The Layers
   - 2.5 Caching Strategies (Patterns)
   - 2.6 Cache Eviction Policies
   - 2.7 Why Redis? What It Actually Is
   - 2.8 Redis Internals -- Single-Threaded Event Loop
   - 2.9 Redis Internals -- Data Structures
   - 2.10 Redis Internals -- Memory Management & Eviction
   - 2.11 Redis Internals -- Persistence (RDB & AOF)
   - 2.12 Redis Internals -- Expiration (TTL) Mechanics
   - 2.13 Redis Internals -- Replication, Sentinel & Cluster
   - 2.14 Redis Internals -- Transactions, Pipelining, Lua, Pub/Sub
   - 2.15 The Hard Problems: Stampede, Penetration, Avalanche, Consistency
   - 2.16 Trade-offs & When NOT to Cache
   - 2.17 Problem 2 Summary
3. **[Problem 3: Design a Highly Available Distributed System (99.99% Uptime)](#problem-3-design-a-highly-available-distributed-system-9999-uptime)** -- a global, low-latency system engineered for **four nines**, graceful failure handling, and minimal disruption.
   - 3.1 Problem Statement
   - 3.2 Step 1: What "99.99% Uptime" Actually Means (the Math)
   - 3.3 Step 2: Clarifying Questions
   - 3.4 Step 3: Requirements (Functional & Non-Functional)
   - 3.5 Step 4: The Core Principle -- Eliminate Every Single Point of Failure
   - 3.6 Step 5: High-Level Global Architecture
   - 3.7 Step 6: Redundancy at Every Layer
   - 3.8 Step 7: Global Low Latency (Serving Users Worldwide)
   - 3.9 Step 8: Data Layer -- Replication, Consistency & CAP
   - 3.10 Step 9: Failure Detection & Health Checking
   - 3.11 Step 10: Graceful Failure Handling (Resilience Patterns)
   - 3.12 Step 11: Deployment Without Downtime
   - 3.13 Step 12: Observability, Alerting & On-Call
   - 3.14 Step 13: Testing Resilience (Chaos Engineering)
   - 3.15 Step 14: The Availability Math of the Whole System
   - 3.16 Step 15: Trade-offs & Pitfalls
   - 3.17 Problem 3 Summary
4. **[Problem 4: Design a Payment System](#problem-4-design-a-payment-system)** -- a correct, consistent, secure system for moving money: idempotency, the ledger, the saga/2-phase flow, PCI/security, reconciliation, and exactly-once semantics.
   - 4.1 Problem Statement
   - 4.2 Step 1: Why Payments Are Different (Correctness Over Everything)
   - 4.3 Step 2: Clarifying Questions & Scope
   - 4.4 Step 3: Requirements (Functional & Non-Functional)
   - 4.5 Step 4: Key Concepts & Glossary
   - 4.6 Step 5: Capacity Estimation
   - 4.7 Step 6: API Design
   - 4.8 Step 7: High-Level Architecture
   - 4.9 Step 8: The Double-Entry Ledger (Heart of the System)
   - 4.10 Step 9: Idempotency -- Never Charge Twice
   - 4.11 Step 10: The Payment Flow (Authorize -> Capture) & Saga
   - 4.12 Step 11: Consistency, Exactly-Once & the Outbox Pattern
   - 4.13 Step 12: Handling External PSPs (Failures, Timeouts, Webhooks)
   - 4.14 Step 13: Reconciliation (Trust but Verify)
   - 4.15 Step 14: Security, PCI-DSS & Fraud
   - 4.16 Step 15: Trade-offs & Pitfalls
   - 4.17 Problem 4 Summary
5. **[Problem 5: Design a Recommendation System](#problem-5-design-a-recommendation-system)** -- a scalable, low-latency personalization system using event pipelines, candidate generation, ranking, embeddings, offline/online features, and feedback loops.
   - 5.1 Problem Statement
   - 5.2 Step 1: Clarifying Questions & Scope
   - 5.3 Step 2: Requirements (Functional & Non-Functional)
   - 5.4 Step 3: Recommendation Approaches
   - 5.5 Step 4: Capacity Estimation
   - 5.6 Step 5: API Design
   - 5.7 Step 6: High-Level Architecture
   - 5.8 Step 7: Data Collection & Event Pipeline
   - 5.9 Step 8: Candidate Generation
   - 5.10 Step 9: Ranking & Personalization
   - 5.11 Step 10: Feature Store, Embeddings & Vector Search
   - 5.12 Step 11: Online Serving Path
   - 5.13 Step 12: Feedback Loop, Evaluation & A/B Testing
   - 5.14 Step 13: Cold Start, Diversity & Exploration
   - 5.15 Step 14: Scaling, Reliability & Latency
   - 5.16 Step 15: Trade-offs & Pitfalls
   - 5.17 Problem 5 Summary
6. **[Problem 6: Database CPU is at 100% — How Do You Troubleshoot?](#problem-6-database-cpu-is-at-100--how-do-you-troubleshoot)** -- a practical diagnostic and remediation framework: identify the culprit query, understand root causes, apply quick wins, and implement long-term fixes.
   - 6.1 Problem Statement
   - 6.2 Step 1: Clarifying Questions & Context
   - 6.3 Step 2: Immediate Diagnostics (First 5 Minutes)
   - 6.4 Step 3: Identify the Culprit Query
   - 6.5 Step 4: Understand the Root Cause
   - 6.6 Step 5: Quick Wins (Immediate Mitigation)
   - 6.7 Step 6: Deep Dive: Query Optimization
   - 6.8 Step 7: Index Strategy
   - 6.9 Step 8: Connection Pool & Resource Limits
   - 6.10 Step 9: Caching & Query Reduction
   - 6.11 Step 10: Scaling & Sharding
   - 6.12 Step 11: Monitoring & Alerting
   - 6.13 Step 12: Post-Incident Review & Prevention
   - 6.14 Problem 6 Summary
7. **[Problem 7: Design a Search Engine](#problem-7-design-a-search-engine)** -- a web-scale search engine covering crawling, indexing, ranking, query serving, and relevance: how billions of pages become sub-second search results.
   - 7.1 Problem Statement
   - 7.2 Step 1: Clarifying Questions & Scope
   - 7.3 Step 2: Requirements (Functional & Non-Functional)
   - 7.4 Step 3: Capacity Estimation
   - 7.5 Step 4: High-Level Architecture
   - 7.6 Step 5: Web Crawler
   - 7.7 Step 6: URL Frontier & Politeness
   - 7.8 Step 7: Document Processing & Content Extraction
   - 7.9 Step 8: Inverted Index (Heart of the Search Engine)
   - 7.10 Step 9: Ranking & Relevance (TF-IDF, BM25, PageRank)
   - 7.11 Step 10: Query Processing & Serving
   - 7.12 Step 11: Spell Correction, Suggestions & Query Understanding
   - 7.13 Step 12: Caching, Sharding & Replication
   - 7.14 Step 13: Freshness, Re-crawling & Incremental Indexing
   - 7.15 Step 14: Scaling, Reliability & Latency
   - 7.16 Step 15: Trade-offs & Pitfalls
   - 7.17 Problem 7 Summary
8. **[Problem 8: Your System is Handling 10x Traffic Overnight — What Do You Do?](#problem-8-your-system-is-handling-10x-traffic-overnight--what-do-you-do)** -- a real-world incident response and capacity crisis: triage, stabilize, scale, and build lasting resilience for traffic surges.
   - 8.1 Problem Statement
   - 8.2 Step 1: Clarifying the Situation (War Room Minute Zero)
   - 8.3 Step 2: Immediate Triage (First 10 Minutes)
   - 8.4 Step 3: Where Does 10x Traffic Break Things?
   - 8.5 Step 4: Quick Wins — Survive the Next Hour
   - 8.6 Step 5: Scaling the Compute Layer
   - 8.7 Step 6: Scaling the Data Layer
   - 8.8 Step 7: Caching Under Pressure
   - 8.9 Step 8: Rate Limiting, Load Shedding & Back-Pressure
   - 8.10 Step 9: CDN, Edge & Static Offload
   - 8.11 Step 10: Queue & Async Processing
   - 8.12 Step 11: Communication & Incident Management
   - 8.13 Step 12: Post-Crisis — Building for the Next 10x
   - 8.14 Step 13: Trade-offs & Pitfalls
   - 8.15 Problem 8 Summary
9. **[Problem 9: Latency Increased from 100 ms to 800 ms — Root Cause Approach](#problem-9-latency-increased-from-100-ms-to-800-ms--root-cause-approach)** -- a systematic diagnostic framework for chasing latency regressions across the stack: where time hides, how to measure it, and how to fix it.
   - 9.1 Problem Statement
   - 9.2 Step 1: Clarifying the Symptom (What Exactly Got Slower?)
   - 9.3 Step 2: The Latency Mental Model (Where Time Hides)
   - 9.4 Step 3: Immediate Data Gathering (First 10 Minutes)
   - 9.5 Step 4: Correlate with Changes (The Usual Suspects)
   - 9.6 Step 5: Narrow the Blast Radius (Which Tier?)
   - 9.7 Step 6: Network & Infrastructure Layer
   - 9.8 Step 7: Application Layer (Code, Threads, GC)
   - 9.9 Step 8: Database & Storage Layer
   - 9.10 Step 9: Cache Layer (Misses, Evictions, Cold Start)
   - 9.11 Step 10: External Dependencies & Third-Party Calls
   - 9.12 Step 11: Distributed Tracing — The Silver Bullet
   - 9.13 Step 12: Common Root Causes (Cheat Sheet)
   - 9.14 Step 13: Fix, Validate & Prevent
   - 9.15 Step 14: Trade-offs & Pitfalls
   - 9.16 Problem 9 Summary
10. **[Problem 10: How Do You Design Secure Authentication & Authorization (OAuth, JWT)?](#problem-10-how-do-you-design-secure-authentication--authorization-oauth-jwt)** -- identity, tokens, protocols, and access control at scale: OAuth 2.0 flows, JWT internals, RBAC/ABAC, session management, and common security pitfalls.
   - 10.1 Problem Statement
   - 10.2 Step 1: Authentication vs. Authorization (The Distinction That Matters)
   - 10.3 Step 2: Clarifying Questions & Scope
   - 10.4 Step 3: Requirements (Functional & Non-Functional)
   - 10.5 Step 4: High-Level Architecture
   - 10.6 Step 5: OAuth 2.0 — The Industry Standard
   - 10.7 Step 6: OpenID Connect (OIDC) — Identity on Top of OAuth
   - 10.8 Step 7: JWT Internals — Structure, Signing & Validation
   - 10.9 Step 8: Token Lifecycle — Issuance, Refresh & Revocation
   - 10.10 Step 9: Session Management — Stateful vs. Stateless
   - 10.11 Step 10: Authorization Models — RBAC, ABAC & Policy Engines
   - 10.12 Step 11: API Gateway & Middleware Enforcement
   - 10.13 Step 12: Security Hardening & Common Attacks
   - 10.14 Step 13: Scaling Authentication (Multi-Region, SSO, Federation)
   - 10.15 Step 14: Trade-offs & Pitfalls
   - 10.16 Problem 10 Summary
11. **[Problem 11: Design a Kafka-Based System](#problem-11-design-a-kafka-based-system)** -- event streaming at scale: Kafka internals, topic design, partitioning, consumer groups, exactly-once semantics, schema evolution, and production-grade patterns.
   - 11.1 Problem Statement
   - 11.2 Step 1: Clarifying Questions & Scope
   - 11.3 Step 2: Why Kafka? (When to Choose It)
   - 11.4 Step 3: Kafka Architecture — Core Concepts
   - 11.5 Step 4: Topic Design & Partitioning Strategy
   - 11.6 Step 5: Producers — Delivery Guarantees & Best Practices
   - 11.7 Step 6: Consumers & Consumer Groups
   - 11.8 Step 7: Exactly-Once Semantics (EOS)
   - 11.9 Step 8: Schema Management & Evolution (Avro, Schema Registry)
   - 11.10 Step 9: High-Level Architecture (End-to-End Example)
   - 11.11 Step 10: Data Retention, Compaction & Tiered Storage
   - 11.12 Step 11: Monitoring, Alerting & Operations
   - 11.13 Step 12: Failure Handling & Resilience
   - 11.14 Step 13: Scaling Kafka in Production
   - 11.15 Step 14: Common Patterns (CQRS, Event Sourcing, Saga, Dead Letter)
   - 11.16 Step 15: Trade-offs & Pitfalls
   - 11.17 Problem 11 Summary
12. **[Problem 12: What Are Retry, Circuit Breaker & Bulkhead Patterns?](#problem-12-what-are-retry-circuit-breaker--bulkhead-patterns)** -- resilience engineering for distributed systems: how to survive downstream failures with retries, circuit breakers, bulkheads, timeouts, fallbacks, and rate limiters.
   - 12.1 Problem Statement
   - 12.2 Step 1: Why Resilience Patterns? (The Cascade Failure Problem)
   - 12.3 Step 2: The Retry Pattern
   - 12.4 Step 3: The Circuit Breaker Pattern
   - 12.5 Step 4: The Bulkhead Pattern
   - 12.6 Step 5: The Timeout Pattern
   - 12.7 Step 6: The Fallback Pattern
   - 12.8 Step 7: The Rate Limiter Pattern
   - 12.9 Step 8: Combining Patterns (The Resilience Stack)
   - 12.10 Step 9: Implementation — Resilience4j (Java / Spring Boot)
   - 12.11 Step 10: Observability for Resilience Patterns
   - 12.12 Step 11: Testing Resilience (Chaos Engineering)
   - 12.13 Step 12: Trade-offs & Pitfalls
   - 12.14 Problem 12 Summary
13. **[Problem 13: Design a Caching Strategy (Multi-Level Caching)](#problem-13-design-a-caching-strategy-multi-level-caching)** -- caching from browser to CDN to application to distributed cache to database: when to cache, what to cache, how to invalidate, and the patterns that make or break performance at scale.
   - 13.1 Problem Statement
   - 13.2 Step 1: Why Cache? (The Numbers That Matter)
   - 13.3 Step 2: Clarifying Questions & Scope
   - 13.4 Step 3: The Multi-Level Cache Architecture
   - 13.5 Step 4: Level 0 — Browser & Client-Side Cache
   - 13.6 Step 5: Level 1 — CDN & Edge Cache
   - 13.7 Step 6: Level 2 — API Gateway & Reverse Proxy Cache
   - 13.8 Step 7: Level 3 — Application In-Process Cache (L1)
   - 13.9 Step 8: Level 4 — Distributed Cache (Redis / Memcached)
   - 13.10 Step 9: Level 5 — Database Query Cache & Materialized Views
   - 13.11 Step 10: Cache Invalidation Strategies
   - 13.12 Step 11: Cache Write Patterns (Write-Through, Write-Behind, Write-Around)
   - 13.13 Step 12: Cache Stampede, Hot Keys & Thundering Herd
   - 13.14 Step 13: Cache Sizing, Eviction & TTL Strategy
   - 13.15 Step 14: Consistency — Cache vs. Source of Truth
   - 13.16 Step 15: Monitoring & Observability
   - 13.17 Step 16: Trade-offs & Pitfalls
   - 13.18 Problem 13 Summary
14. **[Problem 14: How to Handle Cache Invalidation Challenges?](#problem-14-how-to-handle-cache-invalidation-challenges)** -- the hardest problem in distributed caching: race conditions, stale reads, double deletes, cross-layer invalidation, and production-grade solutions for every failure mode.
   - 14.1 Problem Statement
   - 14.2 Step 1: Why Cache Invalidation Is Hard
   - 14.3 Step 2: The Six Core Invalidation Strategies (Deep Dive)
   - 14.4 Step 3: The Classic Race Condition — Read-Update-Cache
   - 14.5 Step 4: Delete vs. Update — Which Is Safer?
   - 14.6 Step 5: Double-Delete (Delayed Invalidation) Pattern
   - 14.7 Step 6: Event-Driven Invalidation (CDC + Kafka)
   - 14.8 Step 7: Cross-Layer Invalidation (L1 → L4 → CDN)
   - 14.9 Step 8: Invalidation in Microservices (Who Owns the Cache?)
   - 14.10 Step 9: Cache Versioning & Lease-Based Invalidation
   - 14.11 Step 10: Negative Caching & Cache Penetration
   - 14.12 Step 11: Bulk Invalidation & Cache Flush Strategies
   - 14.13 Step 12: Observability for Invalidation Health
   - 14.14 Step 13: Trade-offs & Pitfalls
   - 14.15 Problem 14 Summary
15. **[Problem 15: How Do You Design Backward-Compatible APIs?](#problem-15-how-do-you-design-backward-compatible-apis)** -- versioning strategies, contract evolution, breaking vs. non-breaking changes, deprecation workflows, and the patterns that let you ship API changes without breaking existing clients.
   - 15.1 Problem Statement
   - 15.2 Step 1: Why Backward Compatibility Matters
   - 15.3 Step 2: Breaking vs. Non-Breaking Changes
   - 15.4 Step 3: API Versioning Strategies
   - 15.5 Step 4: Additive-Only Design (The Golden Rule)
   - 15.6 Step 5: Request & Response Evolution Patterns
   - 15.7 Step 6: Deprecation Lifecycle & Sunset Headers
   - 15.8 Step 7: Schema Evolution (Database ↔ API Alignment)
   - 15.9 Step 8: Contract Testing (Consumer-Driven Contracts)
   - 15.10 Step 9: API Gateways & Compatibility Layers
   - 15.11 Step 10: gRPC & Protobuf Backward Compatibility
   - 15.12 Step 11: Event / Message Schema Evolution (Kafka, AsyncAPI)
   - 15.13 Step 12: Observability & Safe Rollout
   - 15.14 Step 13: Trade-offs & Pitfalls
   - 15.15 Problem 15 Summary
16. **[Problem 16: REST vs gRPC — Where and Why?](#problem-16-rest-vs-grpc--where-and-why)** -- when to use REST, when to use gRPC, when to use both: protocol mechanics, performance benchmarks, streaming, code generation, and the decision framework for choosing the right API style.
   - 16.1 Problem Statement
   - 16.2 Step 1: REST — What It Actually Is (and Isn't)
   - 16.3 Step 2: gRPC — What It Actually Is
   - 16.4 Step 3: Head-to-Head Comparison
   - 16.5 Step 4: Performance — HTTP/1.1 vs HTTP/2 vs HTTP/3
   - 16.6 Step 5: Serialization — JSON vs Protocol Buffers
   - 16.7 Step 6: Streaming — The gRPC Superpower
   - 16.8 Step 7: Code Generation & Developer Experience
   - 16.9 Step 8: Error Handling & Status Codes
   - 16.10 Step 9: When to Choose REST
   - 16.11 Step 10: When to Choose gRPC
   - 16.12 Step 11: The Hybrid Architecture (REST + gRPC)
   - 16.13 Step 12: Migration — REST to gRPC (or Vice Versa)
   - 16.14 Step 13: Trade-offs & Pitfalls
   - 16.15 Problem 16 Summary
17. **[Problem 17: How Do You Design Monitoring + Alerting Systems?](#problem-17-how-do-you-design-monitoring--alerting-systems)** -- the three pillars of observability (metrics, logs, traces), pipeline architecture, alert design that avoids fatigue, SLI/SLO/SLA, dashboards, on-call runbooks, and the infrastructure that lets you detect, diagnose, and resolve incidents in minutes.
   - 17.1 Problem Statement
   - 17.2 Step 1: The Three Pillars of Observability
   - 17.3 Step 2: Metrics Pipeline Architecture
   - 17.4 Step 3: Logging Pipeline Architecture
   - 17.5 Step 4: Distributed Tracing
   - 17.6 Step 5: SLI, SLO, SLA — Defining What "Healthy" Means
   - 17.7 Step 6: Alert Design — Avoiding Alert Fatigue
   - 17.8 Step 7: Dashboard Design
   - 17.9 Step 8: On-Call & Incident Response
   - 17.10 Step 9: Anomaly Detection & AIOps
   - 17.11 Step 10: Scaling the Observability Stack
   - 17.12 Step 11: Cost Management
   - 17.13 Step 12: Trade-offs & Pitfalls
   - 17.14 Problem 17 Summary
18. **[Problem 18: Design a System for High Write Throughput (>1M Writes/Sec)](#problem-18-design-a-system-for-high-write-throughput-1m-writessec)** -- write-optimised storage engines, LSM trees vs B-trees, batching, sharding, append-only architectures, Kafka as a write buffer, time-series and event ingestion at scale, and the trade-offs you pay for extreme write performance.
   - 18.1 Problem Statement
   - 18.2 Step 1: Why Writes Are the Hard Problem
   - 18.3 Step 2: Storage Engine Internals — LSM Trees vs B-Trees
   - 18.4 Step 3: Write-Ahead Log (WAL) — The Durability Foundation
   - 18.5 Step 4: Batching & Buffering — Amortising Disk I/O
   - 18.6 Step 5: Sharding for Write Scalability
   - 18.7 Step 6: Kafka as a Write Buffer (Decouple Ingestion from Storage)
   - 18.8 Step 7: Append-Only & Immutable Architectures
   - 18.9 Step 8: Time-Series & Event Ingestion at Scale
   - 18.10 Step 9: Compaction, Merge & Background Maintenance
   - 18.11 Step 10: Replication Under High Write Load
   - 18.12 Step 11: Capacity Estimation for 1M Writes/Sec
   - 18.13 Step 12: Trade-offs & Pitfalls
   - 18.14 Problem 18 Summary
19. **[Problem 19: Design a Log Processing System (Like ELK / Splunk)](#problem-19-design-a-log-processing-system-like-elk--splunk)** -- end-to-end log pipeline architecture, collection agents, parsing and enrichment, indexing strategies (inverted index vs label-based), search and analytics, retention lifecycle, multi-tenancy, and the cost/performance trade-offs at 10 TB/day scale.
   - 19.1 Problem Statement
   - 19.2 Step 1: Log Data Characteristics & Scale
   - 19.3 Step 2: Collection — Getting Logs Off the Machines
   - 19.4 Step 3: Transport — Kafka as the Central Nervous System
   - 19.5 Step 4: Parsing, Enrichment & Transformation
   - 19.6 Step 5: Indexing — The Core of Search
   - 19.7 Step 6: Storage Architecture & Tiering
   - 19.8 Step 7: Query & Search Engine
   - 19.9 Step 8: Alerting on Logs
   - 19.10 Step 9: Multi-Tenancy & Access Control
   - 19.11 Step 10: High Availability & Disaster Recovery
   - 19.12 Step 11: Capacity Estimation for 10 TB/Day
   - 19.13 Step 12: Trade-offs & Pitfalls
   - 19.14 Problem 19 Summary
20. **[Appendix A: A Reusable HLD Interview Framework](#appendix-a-a-reusable-hld-interview-framework)**
21. **[Appendix B: Numbers Every Engineer Should Know](#appendix-b-numbers-every-engineer-should-know)**

---

# Problem 1: Design a Scalable Search REST API

## 1.1 Problem Statement

> Design a REST API that returns string search results. It must:
> - Handle **1 million users at a time** (concurrent).
> - Search across a database holding **millions of records**.
> - Return a response within **2-3 seconds**.
>
> Use appropriate architectural patterns to make it happen.

This is a classic "design a search service" HLD question (think: product search on an e-commerce site, document search, or an autocomplete/full-text search backend). The interesting constraints are the **scale (1M concurrent)** and the **latency SLA (2-3s)**, which together rule out naive approaches.

---

## 1.2 Step 1: Clarifying Questions

**Never start designing before clarifying.** In an interview, asking these signals seniority. Reasonable assumptions are stated so we can proceed.

```
  Question                                  | Assumption we proceed with
  ------------------------------------------|------------------------------------
  What are we searching (products, docs,    | Product catalog: each record has
  users)?                                   | id, title, description, tags, price.
  Full-text search or exact match?          | Full-text + prefix (autocomplete-like)
  How many records?                         | ~50 million records, ~5 KB each.
  Is "1M users at a time" concurrent        | 1M CONCURRENT active users.
  connections or total users?               |
  Read-heavy or write-heavy?                | Heavily READ-dominated (search).
  How fresh must results be?                | Near-real-time; seconds of lag is OK.
  Personalized or same for everyone?        | Mostly generic ranking (some boosting).
  Pagination needed?                        | Yes -- return top N (e.g., 20) per page.
  Global or single region?                  | Global users -> multi-region edge.
```

The two answers that dominate the design: **(a) the workload is read-heavy** (so we cache aggressively and use read-optimized stores) and **(b) it is full-text search at scale** (so we use a search engine, not a relational `LIKE`).

---

## 1.3 Step 2: Requirements

**Functional requirements:**
- Accept a search query string (plus optional filters and pagination).
- Return a ranked list of matching records (top N), with basic metadata.
- Support typo tolerance / partial matches (nice-to-have for relevance).

**Non-functional requirements (the hard part):**
- **Scalability:** 1,000,000 concurrent users.
- **Latency:** p99 response time within **2-3 seconds** (we will actually target much lower, since 2-3s is generous).
- **High availability:** ~99.9%+; the service must survive instance and zone failures.
- **Eventual consistency is acceptable:** newly added records appearing in search a few seconds late is fine.
- **Read-optimized:** the system is overwhelmingly reads.

---

## 1.4 Step 3: Capacity Estimation (Back-of-the-Envelope)

This math drives every later decision. **Always do it.**

**From concurrent users to requests per second (RPS):**
```
  Assume 1,000,000 concurrent users.
  Not every user searches every second. Assume each active user issues
  one search every ~10 seconds on average (thinking, reading results).

  Peak RPS = 1,000,000 / 10 = 100,000 requests/second.

  Add headroom for spikes (x1.5) -> design for ~150,000 RPS.
```

**Storage:**
```
  50,000,000 records x 5 KB/record = 250 GB of source data.
  A search index (inverted index) is typically a fraction of raw text;
  budget a few hundred GB across the search cluster (with replicas).
```

**Cache sizing (the key lever):**
```
  Search traffic follows a power law: a small set of popular queries
  ("iphone", "shoes") make up most traffic.
  If the top ~20% of queries serve ~80% of traffic, caching those few
  hundred-thousand hot queries absorbs the vast majority of load.

  Cache entry ~ query string + serialized top-20 results (~10 KB).
  Caching 1,000,000 hot queries ~ 10 GB -> easily fits in a Redis cluster.
```

**Bandwidth (egress):**
```
  150,000 RPS x ~10 KB response = ~1.5 GB/s egress at peak.
  -> CDN + multiple app instances; this is significant but manageable.
```

**Conclusion from the math:** A single server is impossible. We need **horizontal scale-out**, an **aggressive cache** (because of the power-law query distribution), and a **purpose-built search engine** with **read replicas**. The 2-3s SLA is comfortable *if* we cache well and never do full-table scans.

---

## 1.5 Step 4: API Design

A clean, cacheable REST contract. Search is a **GET** (idempotent, cacheable).

```
  GET /api/v1/search?q=running+shoes&page=1&size=20&filter=category:footwear

  Headers:
    Authorization: Bearer <JWT>        (if auth is required)
    Accept: application/json

  200 OK
  {
    "query": "running shoes",
    "page": 1,
    "size": 20,
    "totalHits": 1287,
    "tookMs": 42,
    "results": [
      { "id": "P123", "title": "Trail Running Shoes",
        "snippet": "...lightweight running shoes...", "score": 9.7,
        "price": 89.99 },
      ... up to 20 ...
    ]
  }
```

**Design choices:**
- **GET (not POST)** so responses are cacheable by the CDN and gateway, and URLs are shareable.
- **Pagination** (`page`, `size`) -- never return millions of rows; cap `size`.
- **Versioned path** (`/v1/`) for backward-compatible evolution.
- **`tookMs`** in the response aids client-side observability.
- For very deep pagination, prefer **search-after / cursor** tokens over large `page` offsets (offset pagination degrades at depth).

---

## 1.6 Step 5: High-Level Architecture

```
HIGH-LEVEL ARCHITECTURE: SCALABLE SEARCH API

            +-------------------+
            |   1M Users        |
            | (web / mobile)    |
            +---------+---------+
                      | HTTPS
                      v
            +-------------------+
            |       CDN /       |   caches popular query responses
            |   Edge (CloudFront|   at the edge, close to users
            |   / Cloudflare)   |
            +---------+---------+
                      | (cache miss)
                      v
            +-------------------+
            |  Load Balancer    |   L7, TLS termination, health checks
            |  (ALB / Nginx)    |
            +---------+---------+
                      |
                      v
            +-------------------+
            |   API Gateway     |   authN/JWT, rate limiting, routing
            +---------+---------+
                      |
                      v
        +-----------------------------+
        |   Search Service (stateless)|   N auto-scaled instances
        |   instance 1 ... instance N |   behind the LB
        +------+---------------+------+
               |  1. check      |  3. on miss, query engine
               v  cache         v
        +-------------+   +--------------------------+
        | Redis Cache |   |   Search Engine Cluster  |
        | (hot query  |   | (Elasticsearch / OpenSrch|
        |  results)   |   |  / Solr) -- sharded +    |
        +-------------+   |  replicated inverted idx |
                          +-----------+--------------+
                                      ^
                                      | async indexing (eventual)
                          +-----------+--------------+
                          |  Indexing Pipeline       |
                          |  (Kafka -> Indexer)      |
                          +-----------+--------------+
                                      ^
                                      | CDC / events on writes
                          +-----------+--------------+
                          |  Primary Database         |
                          |  (source of truth,        |
                          |   PostgreSQL, sharded)     |
                          +--------------------------+

   Cross-cutting: Observability (metrics, logs, distributed tracing),
                  Auto-scaling, Multi-AZ / Multi-region replication.
```

**The core idea in one sentence:** Most requests are served from the **edge/Redis cache** in milliseconds; cache misses hit a **horizontally scaled, replicated search engine** (never the primary DB directly); the search index is kept up to date **asynchronously** from the primary database. This is what makes 1M concurrent users at 2-3s feasible.

---

## 1.7 Step 6: Component Deep-Dive

**1. CDN / Edge cache.** Popular search responses are cached at edge locations geographically close to users. A globally popular query ("iphone") may be served entirely from the edge, never touching our origin. This slashes latency (no cross-continent round trip) and offloads enormous traffic. We set a short TTL (e.g., 30-60s) so results stay reasonably fresh.

**2. Load Balancer.** Terminates TLS, spreads traffic across Search Service instances, performs health checks (removing unhealthy instances), and operates across multiple availability zones for fault tolerance.

**3. API Gateway.** Single entry point handling cross-cutting concerns: **authentication** (validate JWT once), **rate limiting** (protect against abuse / a single client flooding us), routing, and request metrics. (See the API Gateway pattern.)

**4. Search Service (stateless application tier).** The brain of the request. For each query it: (a) normalizes the query (lowercase, trim, strip noise), (b) checks **Redis** for a cached result, (c) on a miss, queries the **search engine**, (d) writes the result back to Redis with a TTL, and (e) returns the response. Being **stateless** is critical -- any instance can serve any request, so we can add/remove instances freely (horizontal scaling + auto-scaling).

**5. Redis cache (distributed, in-memory).** Stores serialized results for hot queries. Because search traffic is power-law distributed, this cache absorbs the majority of reads in **sub-millisecond** time. Configured as a cluster (sharded + replicated) for capacity and availability, with an **LRU eviction** policy and per-entry **TTL**.

**6. Search Engine cluster (Elasticsearch / OpenSearch / Solr).** The purpose-built full-text search store. It maintains an **inverted index** (word -> list of documents containing it) enabling fast text search, relevance ranking (BM25/TF-IDF), typo tolerance, and filtering. It is **sharded** (data split across nodes for capacity/parallelism) and **replicated** (each shard has copies for availability and read throughput). This is what searches "millions of records" in milliseconds.

**7. Primary Database (source of truth).** A relational DB (e.g., PostgreSQL), possibly sharded, holds the authoritative records. **Crucially, search queries do NOT hit this directly** -- it is optimized for writes/consistency, not full-text search at scale.

**8. Indexing Pipeline (async).** When records are created/updated in the primary DB, changes flow -- via **Change Data Capture (CDC)** or domain **events** through **Kafka** -- to an indexer that updates the search engine. This keeps the index **eventually consistent** with the source of truth without slowing down writes or reads. (This is essentially the **CQRS** pattern: the DB is the write model; the search engine is the read model.)

---

## 1.8 Step 7: The Search Engine -- Why Not SQL `LIKE`?

A junior answer is "just run `SELECT * FROM products WHERE title LIKE '%shoes%'`." In an interview, explaining why this fails is high-value:

```
  Why SQL LIKE '%term%' does NOT scale for search:

  - A leading-wildcard LIKE '%term%' CANNOT use a normal B-tree index,
    so it forces a FULL TABLE SCAN -> O(N) over millions of rows per query.
  - At 150,000 RPS, full scans would melt the database instantly.
  - No relevance ranking, no typo tolerance, no stemming
    ("running" should match "run"), no multi-field scoring.
  - The primary DB is tuned for transactional writes & consistency,
    not for high-throughput text retrieval.
```

**The solution: an inverted index** (the data structure behind every search engine):

```
  INVERTED INDEX (word -> documents containing it)

    "running" -> [ doc1, doc7, doc42, doc88, ... ]
    "shoes"   -> [ doc7, doc42, doc91, ... ]

  Query "running shoes":
    - look up "running" -> set A
    - look up "shoes"   -> set B
    - intersect A and B -> docs containing BOTH (doc7, doc42 ...)
    - rank by relevance score (BM25) -> return top 20

  This is O(matching docs), NOT O(all docs). Millisecond search over
  tens of millions of records.
```

This is why we offload search to **Elasticsearch/OpenSearch/Solr** rather than the relational DB.

---

## 1.9 Step 8: End-to-End Request Flow

```
  HOT QUERY (cache hit) -- the common case:
   1. User -> CDN edge.  Popular query cached at edge? -> return in ~tens of ms. DONE.
   2. (If edge miss) -> LB -> API Gateway (validate JWT, rate-limit)
   3. -> Search Service: normalize query, check Redis.
   4. Redis HIT -> return cached top-20 in ~1-5 ms. DONE.

  COLD QUERY (cache miss) -- the worst case we must keep under 2-3s:
   1..3 as above, but Redis MISS.
   4. Search Service -> Search Engine cluster: execute query against the
      inverted index (sharded, parallel) -> top-20 ranked results in ~20-80 ms.
   5. Search Service writes result -> Redis (TTL ~60s) so the NEXT identical
      query is a hit.
   6. Return response to user. Total: well under 1s typically.

  WRITE / INDEXING (asynchronous, off the request path):
   A. New/updated record written to Primary DB (the source of truth).
   B. Change emitted via CDC/Kafka.
   C. Indexer consumes it -> updates the search engine's inverted index.
   D. Within a few seconds, the record becomes searchable (eventual consistency).
```

Notice the request path is **short and cache-first**, and indexing is **completely off the request path** -- writes never slow down searches.

---

## 1.10 Step 9: Architectural Patterns Used (and Why)

The question explicitly asks to "use all architectural patterns." Here is each pattern applied, with its justification:

```
  Pattern                    | Where / Why it is used here
  ---------------------------|--------------------------------------------------
  API Gateway                | Single entry point: JWT auth, rate limiting,
                             | routing. Keeps the Search Service focused.
  Load Balancing             | Spread 150k RPS across N stateless instances;
                             | health-checked, multi-AZ.
  Horizontal Scaling +       | Stateless Search Service scales out; auto-scaler
  Auto-scaling               | adds/removes instances with traffic.
  Cache-Aside (Lazy Loading) | Search Service checks Redis first; on miss,
                             | queries engine and populates cache. Absorbs the
                             | power-law hot-query traffic. THE key scaler.
  CDN / Edge caching         | Serve globally popular queries near the user;
                             | massive latency + load reduction.
  CQRS (read/write split)    | Primary DB = write model (source of truth);
                             | Search engine = denormalized read model,
                             | optimized for queries.
  Database Sharding +        | Search index and primary DB sharded for capacity
  Replication                | & parallelism; replicas for availability + read
                             | throughput.
  Event-Driven / CDC         | Async indexing pipeline (Kafka) keeps the search
  + Async indexing           | index up to date without blocking reads/writes.
  Circuit Breaker            | Search Service wraps calls to the engine; trips
                             | on failure to fail fast + serve a fallback.
  Bulkhead                   | Isolate resource pools (e.g., search calls vs
                             | indexing) so one overload can't sink everything.
  Retry + Timeout            | Transient engine glitches retried with backoff;
                             | every remote call has a strict timeout.
  Rate Limiting / Throttling | Protect the system from abusive clients & spikes.
  Observability              | Metrics (RPS, p99, cache hit ratio), logs,
                             | distributed tracing across every hop.
```

**The three patterns that make the numbers work:** **Cache-Aside + CDN** (serve most traffic from memory/edge), **CQRS with a dedicated search engine** (fast full-text reads), and **horizontal scaling with a load balancer** (spread the rest). The resilience patterns (circuit breaker, bulkhead, retry, timeout) keep it from collapsing under partial failure.

---

## 1.11 Step 10: Scaling to 1M Concurrent Users

```
  Layer-by-layer scaling:

  Edge:        CDN scales virtually infinitely; serves hot queries globally.
  App tier:    Stateless Search Service -> add instances horizontally;
               auto-scale on CPU / RPS. No instance holds state, so scaling
               is trivial and instant.
  Cache:       Redis CLUSTER -> shard keys across nodes for capacity;
               replicas for HA. Sub-ms reads absorb the bulk of traffic.
  Search:      Elasticsearch/OpenSearch -> add SHARDS for capacity and
               REPLICAS for read throughput + availability. More replicas
               = more concurrent queries served in parallel.
  Primary DB:  Off the read path; shard by key + read replicas for the
               write/index workload only.
  Region:      Deploy multi-region; route users to the nearest region
               (GeoDNS) for low latency and disaster resilience.
```

**Key enabler: statelessness.** Because the Search Service stores no session state (state lives in Redis / the search engine), we can run thousands of identical instances behind the load balancer and scale elastically. **Cache hit ratio is the single most important metric** -- a 90%+ hit ratio means the expensive search engine only sees ~10% of traffic.

---

## 1.12 Step 11: Meeting the 2-3 Second Latency SLA

The SLA is **2-3 seconds**, which is generous -- our design targets **well under 1 second** for almost all requests, leaving large headroom.

```
  Latency budget (cold query, worst case):

    CDN/LB/Gateway overhead .............. ~20-50 ms
    Redis lookup (miss) .................. ~1-5 ms
    Search engine query (sharded) ........ ~20-100 ms
    Serialization + network back ......... ~20-50 ms
    --------------------------------------------------
    Typical total ........................ ~100-250 ms  (<< 2-3s)

  Hot query (cache hit): ~1-50 ms (often served at the edge).
```

**Techniques that protect the SLA:**
- **Cache-first** so most requests never touch the search engine.
- **Strict timeouts** on the search-engine call (e.g., 1s) + a **circuit breaker** so a slow engine fails fast instead of blowing the budget.
- **Pagination** (return only top 20) -- never serialize huge result sets.
- **Connection pooling** to the engine and Redis to avoid per-request connection setup.
- **Async indexing** so writes never compete with the read path.
- The generous 2-3s SLA means even degraded conditions (cache cold, one AZ down) stay within budget.

---

## 1.13 Step 12: Failure Handling & Resilience

```
  Failure scenario               | Mitigation
  -------------------------------|------------------------------------------
  A Search Service instance dies | LB health check removes it; auto-scaler
                                 | replaces it. Stateless -> no impact.
  Redis node down                | Cluster replicas take over; worst case,
                                 | misses fall through to the search engine.
  Search engine slow/overloaded  | Circuit breaker trips -> fail fast; serve
                                 | stale-but-cached results or a graceful
                                 | "try again" instead of hanging.
  Search engine node down        | Shard replicas serve the data; cluster
                                 | reroutes. No data loss.
  Indexing pipeline lag          | Eventual consistency -- search results are
                                 | slightly stale; acceptable per requirements.
  Entire AZ/region outage        | Multi-AZ + multi-region; GeoDNS reroutes
                                 | users to a healthy region.
  Traffic spike beyond capacity  | Rate limiting + auto-scaling + CDN absorb;
                                 | bulkheads stop one hotspot starving others.
```

**Graceful degradation principle:** when the search engine is unavailable, it is better to return slightly **stale cached results** (or a clear "results temporarily limited" message) than to fail the whole request. Combined with **retry + timeout + circuit breaker + bulkhead**, the system degrades smoothly instead of collapsing.

---

## 1.14 Step 13: Trade-offs & Alternatives

A senior answer always acknowledges trade-offs:

- **Eventual consistency vs freshness:** We accept that a newly added record takes a few seconds to become searchable (async indexing). This is the price of decoupling writes from reads. If strict real-time indexing were required, we'd pay with higher write latency and complexity.
- **Caching vs staleness:** Short TTLs keep results fresh but lower the hit ratio; long TTLs maximize hit ratio but risk stale results. Tune the TTL (e.g., 30-60s) to balance the two.
- **Cost vs performance:** A CDN, a large Redis cluster, and a replicated search cluster cost real money. They are justified by the 1M-user scale; for a smaller service this would be over-engineering.
- **Search engine vs database:** We introduce an entire second data store (the search engine) and the complexity of keeping it in sync. Justified because relational `LIKE` cannot scale; not justified for a tiny dataset.
- **Complexity:** This architecture has many moving parts (gateway, cache, engine, pipeline). For a startup or small dataset, a single PostgreSQL with its built-in full-text search (`tsvector`/GIN index) behind a small cache may be entirely sufficient -- **do not over-engineer**.

---

## 1.15 Problem 1 Summary

- The two constraints that shape everything: **read-heavy at 1M concurrent users** and a **2-3s SLA** over **millions of records**.
- Capacity math: ~**150,000 peak RPS** -> impossible on one server -> **scale out + cache + search engine**.
- **Cache-first architecture:** CDN edge + Redis serve the power-law hot queries in milliseconds; only ~10% of traffic reaches the search engine.
- **CQRS:** the primary DB is the write source of truth; a sharded, replicated **search engine with an inverted index** is the read-optimized model -- never run `LIKE` over millions of rows.
- **Async indexing** (CDC/Kafka) keeps the index eventually consistent without slowing reads or writes.
- **Stateless app tier + load balancer + auto-scaling** absorbs the remaining traffic; **resilience patterns** (circuit breaker, bulkhead, retry, timeout, rate limiting) keep it alive under failure.
- The design comfortably beats the 2-3s SLA (typically <250 ms), with graceful degradation and multi-region availability.

---

# Problem 2: How Caching Enhances System Performance (Deep Dive with Redis)

## 2.1 The Question

> How is caching used to enhance system performance? Give a detailed explanation. Use Redis as the example, and cover its internals so the answer can be defended in depth.

This is both a conceptual question ("why and how does caching help?") and a technology-depth question ("prove you actually understand Redis, not just that it is fast"). A strong answer moves from **principle -> strategy -> internals -> failure modes**. That is exactly the structure below.

---

## 2.2 Why Caching Works: The Core Principle

A **cache** is a small, fast store that holds a copy of data that is expensive to fetch or compute, so future requests for that data are served quickly. Caching works because of two empirical facts about almost every real system:

- **Locality of reference.** Requests are not uniformly random. The same data is requested repeatedly over short windows (**temporal locality**), and related data tends to be requested together (**spatial locality**).
- **The 80/20 (power-law) distribution.** A small fraction of the data accounts for the majority of accesses. On an e-commerce site, a few thousand "hot" products generate most of the traffic; the "long tail" is rarely touched. Caching just the hot set absorbs most of the load.

The other half of the principle is the **speed gap between storage layers** -- the deeper you go, the slower it gets, by orders of magnitude:

```
  THE LATENCY HIERARCHY (why caching is a huge win)

    CPU register / L1 cache .......... ~1 ns
    RAM (in-memory cache, e.g. Redis local) ~100 ns
    Redis over network (same DC) ..... ~0.5-1 ms
    SSD random read .................. ~100-150 us
    Database query (indexed) ......... ~5-30 ms
    Database query (complex join/scan) ~100 ms - seconds
    Cross-region network round trip .. ~50-150 ms

  RAM is ~100,000x faster than a cross-continent hop and ~100-1000x
  faster than a non-trivial DB query. Serving a request from cache instead
  of recomputing it from the database is the single highest-leverage
  performance optimization in most systems.
```

**In one line:** caching trades a little **memory and some staleness risk** for **massive reductions in latency and load**.

---

## 2.3 How Caching Enhances Performance (the Concrete Wins)

```
  Benefit              | Mechanism
  ---------------------|------------------------------------------------------
  Lower latency        | Serve from RAM (sub-ms) instead of DB (ms-seconds).
  Higher throughput    | Each cached hit frees the DB to do other work; the
                       | system handles far more RPS with the same DB.
  Reduced DB load      | A 90% hit ratio means the DB sees only 10% of reads,
                       | preventing it from becoming the bottleneck.
  Lower cost           | Fewer/smaller DB instances and less compute needed.
  Better availability  | Cache can serve (slightly stale) data even if the DB
                       | is briefly down -> graceful degradation.
  Smoother spikes      | A cache absorbs traffic bursts that would otherwise
                       | overwhelm the database.
```

**The single metric that captures it all: cache hit ratio.**
```
  hit ratio = cache hits / (cache hits + cache misses)

  Effective average latency = (hit_ratio x cache_latency)
                            + (miss_ratio x backend_latency)

  Example: hit ratio 95%, cache 1 ms, DB 50 ms
    = 0.95 x 1 + 0.05 x 50 = 0.95 + 2.5 = ~3.45 ms average
  vs 50 ms with no cache  ->  ~14x faster, and the DB load drops by 95%.
```
Driving the hit ratio up (good key design, right TTLs, enough memory) is the main lever for cache effectiveness.

---

## 2.4 Where Caching Lives: The Layers

Caching is not one thing -- it exists at every layer of a system. A senior answer names several:

```
  CACHING LAYERS (client -> server)

  +-------------------+
  | 1. Client / Browser| HTTP cache, localStorage. Zero network cost.
  +-------------------+
  | 2. CDN / Edge      | Cache static + cacheable responses near the user.
  +-------------------+
  | 3. API Gateway /   | Reverse-proxy response cache (e.g., Nginx, Varnish).
  |    Reverse proxy   |
  +-------------------+
  | 4. Application     | In-process cache (e.g., Caffeine/Guava in the JVM).
  |    (local cache)   | Fastest, but per-instance and not shared.
  +-------------------+
  | 5. Distributed     | Redis / Memcached -- SHARED across all app instances.
  |    cache           | This is the focus of this question.
  +-------------------+
  | 6. Database        | DB buffer pool / query cache, materialized views.
  +-------------------+
```

**Local (in-process) vs distributed (Redis) cache -- a key trade-off:**
- **Local cache** (e.g., Caffeine in the JVM heap) is the fastest (nanoseconds, no network) but is **not shared** -- each instance has its own copy, leading to duplication and **inconsistency** across instances, and it is lost on restart.
- **Distributed cache** (Redis) is shared by all instances, so it is **consistent** and survives instance restarts, at the cost of a network hop (still sub-millisecond). Many systems use **both**: a small local L1 cache in front of a shared Redis L2 ("near cache").

---

## 2.5 Caching Strategies (Patterns)

*How* the application and cache interact is a design decision. The main strategies:

**1. Cache-Aside (Lazy Loading) -- the most common.** The application manages the cache. On read: check cache; on a miss, read the DB, populate the cache, return. On write: update the DB and **invalidate** (delete) the cache entry.

```
  CACHE-ASIDE READ:
    1. value = redis.get(key)
    2. if value != null  -> return value          (HIT)
    3. value = db.query(...)                       (MISS)
    4. redis.set(key, value, ttl)
    5. return value
```
- *Pros:* only requested data is cached (memory-efficient); cache failure does not break reads (you fall through to the DB); simple.
- *Cons:* first request for any key is always a miss (cold start); risk of stale data if invalidation is missed; on a write you must invalidate carefully.

**2. Read-Through.** The cache itself (via a library/provider) loads from the DB on a miss; the app only talks to the cache. Cleaner app code, but requires a cache that supports a loader.

**3. Write-Through.** On write, write to the cache **and** the DB synchronously, in line. Cache is always fresh, but writes are slower (two writes), and you cache data that may never be read.

**4. Write-Behind (Write-Back).** Write to the cache immediately and **asynchronously** flush to the DB later (batched). Very fast writes and great for write-heavy/bursty loads, but **risk of data loss** if the cache dies before flushing, and added complexity.

**5. Write-Around.** Write directly to the DB, bypassing the cache; the cache is populated only on later reads (cache-aside on read). Good when written data is rarely re-read soon, avoids polluting the cache with cold data.

```
  STRATEGY SELECTION
  +----------------+----------------------------+---------------------------+
  | Strategy       | Best for                   | Main risk                 |
  +----------------+----------------------------+---------------------------+
  | Cache-Aside    | Read-heavy, general purpose| Stale data, cold misses   |
  | Read-Through   | Read-heavy, clean app code | Needs provider support    |
  | Write-Through  | Read-after-write freshness | Slower writes             |
  | Write-Behind   | Write-heavy, bursty        | Data loss if cache fails  |
  | Write-Around   | Write-once, rarely re-read | More read misses          |
  +----------------+----------------------------+---------------------------+
```

For most read-heavy systems (like the search API in Problem 1), **Cache-Aside + TTL** is the default choice.

---

## 2.6 Cache Eviction Policies

A cache has finite memory. When full, it must evict something. The policy matters:

```
  Policy | Evicts                          | Good when
  -------|---------------------------------|--------------------------------
  LRU    | Least Recently Used             | General purpose (temporal
         |                                 | locality) -- the usual default.
  LFU    | Least Frequently Used           | Stable popularity sets; protects
         |                                 | long-term hot keys from a burst.
  FIFO   | Oldest inserted                 | Rarely ideal; simple.
  Random | A random key                    | Cheap; surprisingly OK at scale.
  TTL    | Whatever expires first (time)   | Data with a natural freshness
         |                                 | window.
```

Redis supports several of these directly (covered in 2.10). **LRU** is the most common default; **LFU** (added in Redis 4.0) is better when a brief burst of one-off keys would otherwise evict your genuinely popular keys.

---

## 2.7 Why Redis? What It Actually Is

**Redis (REmote DImentary Server)** is an open-source, **in-memory data structure store** used as a cache, database, and message broker. Saying "Redis is a key-value cache" undersells it -- the differentiator is that values are **rich data structures**, not just opaque blobs.

```
  Redis at a glance:
   - In-memory  -> microsecond-to-sub-millisecond operations.
   - Single-threaded command execution -> no locks, atomic operations.
   - Rich data types -> strings, hashes, lists, sets, sorted sets, etc.
   - Optional persistence (RDB + AOF) -> can survive restarts.
   - Replication, Sentinel (HA), and Cluster (sharding) for scale.
   - Extensible: transactions, Lua scripting, pub/sub, streams.
```

**Redis vs Memcached** (a common interview follow-up):
```
  +------------------+---------------------------+--------------------------+
  | Aspect           | Redis                     | Memcached                |
  +------------------+---------------------------+--------------------------+
  | Data types       | Rich (hash, set, zset...) | Strings/blobs only       |
  | Persistence      | Yes (RDB/AOF)             | No                       |
  | Replication/HA   | Yes (Sentinel/Cluster)    | No (client-side only)    |
  | Threading        | Single-threaded core      | Multi-threaded           |
  | Extras           | Pub/sub, Lua, streams,    | None                     |
  |                  | geo, TTL, atomic ops      |                          |
  +------------------+---------------------------+--------------------------+
  Use Redis when you need data structures, persistence, or HA; Memcached
  for the simplest, purely volatile, multi-threaded string cache.
```

---

## 2.8 Redis Internals -- Single-Threaded Event Loop

The most important and counter-intuitive Redis internal: **command execution is single-threaded.** One thread executes all commands, one at a time, via an **event loop** (using epoll/kqueue I/O multiplexing).

```
  REDIS EVENT LOOP (simplified)

    many client connections
            |
            v
   +-------------------------+
   | I/O multiplexer (epoll) |  watches all sockets for readiness
   +-----------+-------------+
               |  ready events, one at a time
               v
   +-------------------------+
   | Single command-exec     |  executes each command ATOMICALLY,
   | thread (event loop)     |  start to finish, no interruption
   +-------------------------+
```

**Why single-threaded is actually fast (and an advantage here):**
- **No lock contention, no context switching** between threads for command execution.
- **Every command is inherently atomic** -- since only one runs at a time, there are no race conditions on a single command (this is why `INCR`, `SETNX`, etc. are safe without explicit locks).
- The bottleneck is **memory bandwidth and network I/O, not CPU**, so one thread saturates a core's useful work; Redis hits **100k+ ops/sec** easily on one core.

**Nuance (shows depth):** "single-threaded" refers to **command execution**. Modern Redis (6.0+) added **multi-threaded I/O** for reading/parsing requests and writing responses across sockets, while the actual command execution remains single-threaded. Background tasks (RDB save, AOF rewrite, lazy/async key deletion via `UNLINK`) also run in separate threads/processes. **Implication you must state:** never run an O(N) command like `KEYS *` on a large dataset in production -- it blocks the single execution thread and stalls **every** other client. Use `SCAN` (cursor-based, incremental) instead.

---

## 2.9 Redis Internals -- Data Structures

Redis stores everything as **key -> value**, where the key is a string and the value is one of several types. Each type maps to efficient internal encodings (Redis switches encoding based on size for memory efficiency).

```
  Type        | Use case                         | Example commands
  ------------|----------------------------------|------------------------
  String      | Cached blob, counter, flag       | SET, GET, INCR, SETNX
  Hash        | Object with fields (a user)      | HSET, HGET, HGETALL
  List        | Queue / stack / recent items     | LPUSH, RPOP, LRANGE
  Set         | Unique members, membership test  | SADD, SISMEMBER, SINTER
  Sorted Set  | Leaderboard, ranking, rate limit | ZADD, ZRANGE, ZRANGEBYSCORE
  (ZSet)      | by score                         |
  Bitmap      | Compact booleans (daily actives) | SETBIT, BITCOUNT
  HyperLogLog | Approx. unique counts (cardinality)| PFADD, PFCOUNT
  Geospatial  | Nearby / radius search           | GEOADD, GEOSEARCH
  Streams     | Append-only log / event queue    | XADD, XREAD, consumer grps
```

**Why this matters for performance:** you can do work **inside Redis atomically** instead of pulling data to the app, mutating, and writing back. Examples: a **Sorted Set** gives you a real-time leaderboard or a sliding-window rate limiter in one atomic op; a **Hash** lets you update one field of a cached object without re-serializing the whole thing; **HyperLogLog** counts millions of unique visitors in ~12 KB. Internal encodings (e.g., `ziplist`/`listpack` for small collections, `hashtable`/`skiplist` for large) keep small structures extremely memory-compact.

---

## 2.10 Redis Internals -- Memory Management & Eviction

Redis is memory-bound, so memory policy is central.

```
  maxmemory <bytes>          -> hard cap on memory Redis will use.
  maxmemory-policy <policy>  -> what to do when the cap is hit.

  Eviction policies:
    noeviction       -> reject writes when full (return errors). Safe but
                        breaks writes -- use for a DB, not a pure cache.
    allkeys-lru      -> evict least-recently-used key from ALL keys.
    allkeys-lfu      -> evict least-frequently-used from ALL keys (4.0+).
    allkeys-random   -> evict a random key.
    volatile-lru     -> LRU, but only among keys that have a TTL set.
    volatile-lfu     -> LFU among keys with a TTL.
    volatile-ttl     -> evict the key with the nearest expiry.
    volatile-random  -> random among keys with a TTL.
```

**For a pure cache, `allkeys-lru` (or `allkeys-lfu`) is the standard choice** -- it lets Redis evict cold data to make room, keeping hot data resident. `volatile-*` policies only consider keys with a TTL, which is useful when Redis mixes cache data (with TTL) and persistent data (no TTL) in the same instance.

**Approximated LRU/LFU (depth point):** Redis does **not** maintain a perfect global LRU list (too expensive in memory). Instead it uses **sampled/approximated LRU** -- it samples a handful of keys (configurable, `maxmemory-samples`) and evicts the best candidate among them. This trades perfect accuracy for speed and low overhead, and is "good enough" in practice. LFU uses a probabilistic counter with time-based decay.

---

## 2.11 Redis Internals -- Persistence (RDB & AOF)

Although in-memory, Redis can persist to disk so data survives restarts. Two mechanisms, often combined:

```
  RDB (Redis Database snapshots):
   - Point-in-time SNAPSHOT of the dataset, written periodically (e.g.,
     "save after 60s if >=1000 keys changed").
   - Done by FORKING a child process (copy-on-write) so the main thread
     keeps serving -- the child writes the snapshot to disk.
   + Compact, fast restart, great for backups.
   - Can LOSE data since the last snapshot (e.g., last few minutes).

  AOF (Append Only File):
   - Logs EVERY write command to a file, appended as it happens.
   - fsync policy controls durability vs speed:
        always   -> fsync every command (safest, slowest)
        everysec -> fsync once per second (good balance; <=1s data loss)
        no       -> let the OS decide (fastest, least safe)
   - AOF is periodically REWRITTEN/compacted (also via a forked child)
     to keep the file from growing unbounded.
   + Much better durability (down to ~1s of loss).
   - Larger files, slightly slower than RDB.
```

```
  PERSISTENCE TRADE-OFF
   - Pure cache (data is rebuildable from DB) -> persistence often OFF
     for max speed; losing the cache just causes a cold-start.
   - Redis as a primary/semi-durable store -> use AOF (everysec) + RDB
     together: AOF for durability, RDB for fast restarts/backups.
```

The key insight: persistence uses **fork + copy-on-write** so snapshotting does not block command execution -- though a fork on a very large dataset can cause a brief latency spike and needs memory headroom.

---

## 2.12 Redis Internals -- Expiration (TTL) Mechanics

You set a key to expire with `EXPIRE key seconds` or `SET key val EX seconds`. How does Redis actually remove expired keys? **Two complementary strategies:**

```
  1. LAZY (passive) expiration:
     - When a client ACCESSES a key, Redis checks if it has expired.
     - If expired, it deletes it then and returns "not found".
     - Cheap, but an expired key never accessed lingers in memory.

  2. ACTIVE (periodic) expiration:
     - A background cycle (~10x/second) SAMPLES keys that have a TTL,
       deletes the expired ones among the sample, and repeats if a high
       fraction were expired.
     - Probabilistic sweep -> reclaims memory from keys never re-accessed
       without scanning the entire keyspace (which would block).
```

**Why two?** Lazy alone would leak memory (untouched expired keys never freed); a full active scan would block the single thread. The combination keeps memory bounded with minimal CPU. **Interview-ready takeaway:** TTL is the simplest, most robust cache-freshness tool -- prefer a sensible TTL over relying solely on manual invalidation, because invalidation bugs are a top cause of stale-cache incidents.

---

## 2.13 Redis Internals -- Replication, Sentinel & Cluster

A single Redis node is a single point of failure and capacity limit. Three mechanisms address this:

```
  1. REPLICATION (master-replica):
     - One master handles writes; replicas asynchronously copy its data.
     - Replicas serve READS -> scale read throughput + provide redundancy.
     - Async -> a small replication lag (replicas can be slightly stale).

           writes -> [ MASTER ] --async copy--> [ replica1 ]
                                            \--> [ replica2 ]  (serve reads)

  2. SENTINEL (high availability):
     - A set of Sentinel processes MONITOR the master/replicas.
     - On master failure, they reach quorum and AUTOMATICALLY PROMOTE a
       replica to master (automatic failover) + notify clients.
     - Solves availability, NOT capacity (data still fits on one master).

  3. CLUSTER (horizontal scaling / sharding):
     - Data is partitioned across multiple masters using 16384 HASH SLOTS.
     - slot = CRC16(key) mod 16384 ; each master owns a range of slots.
     - Each master can have its own replicas (HA + scale together).
     - Scales WRITES and DATASET SIZE beyond one machine's RAM.

        key -> CRC16 -> slot 8200 -> Master B (owns slots 5461-10922)
```

**Depth points to mention:**
- **Hash tags:** `{user123}:profile` and `{user123}:cart` -- the `{...}` forces both keys into the **same slot**, so multi-key operations (and transactions) work across them in Cluster mode.
- Cluster replication is **asynchronous**, so Redis Cluster favors availability/performance over strict consistency -- a failover can lose the last few writes (acceptable for a cache).
- Choose: **Sentinel** when one node holds all your data but you need automatic failover; **Cluster** when the dataset or write load exceeds a single node.

---

## 2.14 Redis Internals -- Transactions, Pipelining, Lua, Pub/Sub

```
  TRANSACTIONS (MULTI/EXEC):
   - MULTI queues commands; EXEC runs them sequentially & atomically
     (no other client interleaves). Not rollback-style -- it is "all run
     together." WATCH adds optimistic locking (abort if a key changed).

  PIPELINING:
   - Send many commands WITHOUT waiting for each reply, then read all
     replies at once. Eliminates per-command round-trip latency ->
     huge throughput gain for bulk operations. (A network optimization,
     not atomicity.)

  LUA SCRIPTING (EVAL):
   - Run a Lua script server-side ATOMICALLY (single-threaded guarantees
     no interleaving). Perfect for read-modify-write logic like rate
     limiting or atomic "check-and-set" without race conditions.

  PUB/SUB:
   - Lightweight publish/subscribe messaging (SUBSCRIBE/PUBLISH).
     Fire-and-forget (no persistence) -- use Streams for durable queues.
```

These features let you push **logic into Redis atomically**, which is both faster (fewer round trips) and safer (no races) than doing it in the app -- a recurring theme of why Redis boosts performance.

---

## 2.15 The Hard Problems: Stampede, Penetration, Avalanche, Consistency

Senior answers must cover cache **failure modes** and their fixes:

```
  1. CACHE STAMPEDE / "thundering herd" (dog-piling):
     - A hot key expires; thousands of concurrent requests all miss at
       once and hammer the DB simultaneously.
     - Fixes: a MUTEX/LOCK so only ONE request rebuilds the value while
       others wait/serve stale; "early/probabilistic recomputation"
       (refresh just before expiry); or never let hot keys expire (refresh
       in background).

  2. CACHE PENETRATION:
     - Queries for data that DOES NOT EXIST (often malicious) always miss
       the cache and hit the DB every time.
     - Fixes: cache the NEGATIVE result ("not found") with a short TTL;
       use a BLOOM FILTER to reject keys that definitely do not exist.

  3. CACHE AVALANCHE:
     - Many keys expire at the SAME instant (or Redis goes down), so a
       flood of misses overwhelms the DB at once.
     - Fixes: add RANDOM JITTER to TTLs so they expire at different times;
       ensure Redis HA (Sentinel/Cluster); add a circuit breaker in front
       of the DB.

  4. CACHE CONSISTENCY (the hardest):
     - The cache and DB can diverge (stale data) after a write.
     - Common approach: "Cache-Aside + invalidate on write" -- update the
       DB, then DELETE the cache key (delete, do not update, to avoid
       race conditions). Accept brief staleness; use short TTL as a
       safety net. Strong consistency is expensive -- usually you choose
       eventual consistency for caches deliberately.
```

Naming these four (stampede, penetration, avalanche, consistency) and their mitigations is what separates a strong answer from a basic one.

---

## 2.16 Trade-offs & When NOT to Cache

```
  Trade-off / cost          | Detail
  --------------------------|--------------------------------------------
  Staleness                 | Cached data can be out of date. Tune TTL /
                            | invalidation to the business tolerance.
  Complexity                | Invalidation is "one of the two hard things
                            | in CS." More moving parts to reason about.
  Extra infrastructure      | Redis cluster to run, monitor, secure, pay for.
  Cold start                | After a flush/restart, all misses hit the DB
                            | (mitigate with cache warming/pre-loading).
  Memory cost               | RAM is pricier than disk; cache only what pays
                            | for itself (hot data).
```

**When NOT to cache:**
- **Write-heavy data that is rarely read** -- caching adds invalidation cost with little hit benefit.
- **Data requiring strict, real-time consistency** (e.g., a bank account balance shown at the moment of transfer) where any staleness is unacceptable -- or use write-through with great care.
- **Highly unique, never-repeated queries** -- if every request is different, the hit ratio is ~0 and the cache is pure overhead.
- **Small/fast datasets** where the DB already answers in microseconds -- caching adds complexity for no gain.

---

## 2.17 Problem 2 Summary

- **Caching works** because of **locality + power-law access** and the **huge latency gap** between RAM and disk/DB; it cuts latency, boosts throughput, offloads the DB, lowers cost, and improves availability. The headline metric is **cache hit ratio**.
- Caching exists at **many layers** (client, CDN, proxy, app-local, distributed, DB); **distributed (Redis)** is shared and consistent across instances, **local** is fastest but per-instance.
- Choose a **strategy** (Cache-Aside is the default for read-heavy systems) and an **eviction policy** (LRU/LFU) to fit the workload.
- **Redis internals to know:** single-threaded atomic command execution on an **event loop** (avoid `KEYS`, use `SCAN`); **rich data structures**; **approximated LRU/LFU** eviction under `maxmemory`; **RDB+AOF** persistence via fork/copy-on-write; **lazy + active TTL expiration**; **replication, Sentinel (HA), and Cluster (16384 hash-slot sharding)**; and **transactions, pipelining, Lua, pub/sub** for atomic server-side logic.
- Always address the **failure modes**: **stampede, penetration, avalanche, and consistency** -- with mutex/locks, negative caching/bloom filters, TTL jitter + HA, and delete-on-write invalidation.
- Caching is a deliberate **trade-off** (staleness + complexity for speed) -- do not cache write-heavy, strictly-consistent, or never-repeated data.

---

# Problem 3: Design a Highly Available Distributed System (99.99% Uptime)

## 3.1 Problem Statement

> Design a highly available distributed system that guarantees **99.99% uptime (four nines)**. The system should serve **global users with low latency**, **handle failures gracefully**, and ensure **minimal service disruption**.

This is an availability-and-resilience question. Unlike a feature question, the "what" matters less than the "how do you keep it up." A strong answer is built on one obsession: **there is no single point of failure (SPOF) anywhere**, and the system **degrades gracefully** rather than failing hard. We will quantify the target, then engineer every layer to meet it.

---

## 3.2 Step 1: What "99.99% Uptime" Actually Means (the Math)

Always anchor the answer in the number. "Nines" translate to a concrete **error budget** -- the maximum time the system may be down:

```
  AVAILABILITY -> ALLOWED DOWNTIME (the "error budget")

  Nines     | Availability | Downtime/year | Downtime/month | Downtime/day
  ----------|--------------|---------------|----------------|-------------
  Two 9s    | 99%          | ~3.65 days    | ~7.2 hours     | ~14.4 min
  Three 9s  | 99.9%        | ~8.76 hours   | ~43.8 min      | ~1.44 min
  FOUR 9s   | 99.99%       | ~52.6 minutes | ~4.38 min      | ~8.6 sec
  Five 9s   | 99.999%      | ~5.26 minutes | ~26 sec        | ~0.86 sec
```

**Four nines = only ~52.6 minutes of downtime for the ENTIRE year.** That is the headline constraint. The implications are immediate and severe:
- **Manual recovery is too slow.** If a human must be paged, diagnose, and fix, you can blow the whole annual budget in one incident. Recovery must be **automatic**.
- **Planned maintenance counts.** You cannot take the system down to deploy -- deployments must be **zero-downtime**.
- **Every component must be redundant.** A single instance with even 99.9% availability already exceeds the budget by itself.

**Two more definitions to state (they show maturity):**
```
  MTBF (Mean Time Between Failures) -> how often things break.
  MTTR (Mean Time To Recovery)      -> how fast you recover.

  Availability ~ MTBF / (MTBF + MTTR)

  At four nines, you cannot make MTBF infinite (things WILL fail), so the
  winning lever is driving MTTR toward ZERO via AUTOMATIC failover.
```

**Key insight:** four nines is achieved not by preventing all failures (impossible in a distributed system) but by **detecting and recovering from them automatically, in seconds, with redundancy absorbing the impact.**

---

## 3.3 Step 2: Clarifying Questions

```
  Question                                 | Assumption we proceed with
  -----------------------------------------|-------------------------------
  What does the system do?                 | A global web/API platform
                                           | (read-heavy, e.g. e-commerce).
  Does "uptime" mean every request         | SLO: 99.99% of requests succeed
  succeeds, or the system is reachable?    | within the latency target.
  Acceptable latency?                      | p99 < 200 ms for global users.
  Read-heavy or write-heavy?               | Read-heavy (~95% reads).
  Consistency needs?                       | Eventual consistency acceptable
                                           | for most data; strong only where
                                           | required (e.g., payments).
  Global footprint?                        | Users on multiple continents.
  Budget for redundancy?                   | Cost is justified by the SLA.
```

The two answers that shape everything: **global users** (so we need multi-region + edge) and **eventual consistency is mostly acceptable** (so we can replicate aggressively and favor availability under partition -- see CAP in 3.9).

---

## 3.4 Step 3: Requirements (Functional & Non-Functional)

**Functional:** serve user requests (read/write) correctly via an API.

**Non-functional (these dominate):**
- **Availability:** 99.99% (the primary goal).
- **Latency:** low, globally -- p99 < ~200 ms regardless of user location.
- **Fault tolerance:** survive failures of instances, zones, and **entire regions** without user-visible outage.
- **Graceful degradation:** under partial failure, serve reduced functionality rather than fail completely.
- **Zero-downtime deployments:** ship changes without taking the system down.
- **Scalability:** handle load spikes (so overload does not become an outage).

---

## 3.5 Step 4: The Core Principle -- Eliminate Every Single Point of Failure

A **single point of failure (SPOF)** is any component whose failure takes down the system. The entire design is the systematic removal of SPOFs through **redundancy** (N+1 or more of everything) and **automatic failover**.

```
  THE GOLDEN RULES OF HIGH AVAILABILITY

  1. Redundancy everywhere   -> never one of anything (N+1, multi-AZ, multi-region).
  2. No SPOF                 -> every component has a healthy standby/peer.
  3. Automatic failover      -> detect failure & reroute in SECONDS, no humans.
  4. Statelessness           -> any instance serves any request -> easy failover.
  5. Graceful degradation    -> partial failure = reduced service, not an outage.
  6. Isolate blast radius    -> contain failures (bulkheads, cells, zones).
  7. Zero-downtime deploys   -> never planned downtime.
  8. Detect fast, recover fast -> drive MTTR toward zero.
```

Every later section is an application of these rules to a specific layer.

---

## 3.6 Step 5: High-Level Global Architecture

```
HIGHLY AVAILABLE, GLOBAL ARCHITECTURE (multi-region, active-active)

                         +---------------------+
                         |    Global Users     |
                         +----------+----------+
                                    |
                         +---------------------+
                         |  GeoDNS / Anycast   |  routes each user to the
                         | (Route53 / Cloud DNS|  NEAREST healthy region;
                         |  + health checks)   |  fails over if region down
                         +----+-----------+----+
                              |           |
              +---------------+           +----------------+
              v  (Region A: US)                   (Region B: EU)  v
   +-------------------------+              +-------------------------+
   |   CDN / Edge (PoPs)     |              |   CDN / Edge (PoPs)     |
   +-----------+-------------+              +-----------+-------------+
               |                                        |
   +-----------v-------------+              +-----------v-------------+
   | Global LB (Anycast)     |              | Global LB               |
   +-----------+-------------+              +-----------+-------------+
               |                                        |
   +-----------v-------------+              +-----------v-------------+
   | Regional LB (multi-AZ)  |              | Regional LB (multi-AZ)  |
   +--+-------------------+--+              +--+-------------------+--+
      |                   |                    |                   |
  +---v---+           +---v---+            +---v---+           +---v---+
  | AZ-1  |           | AZ-2  |            | AZ-1  |           | AZ-2  |
  | app   |           | app   |            | app   |           | app   |
  | (N    |           | (N    |            | (N    |           | (N    |
  | stateless         | stateless          | stateless         | stateless
  | instances)        | instances)         | instances)        | instances)
  +---+---+           +---+---+            +---+---+           +---+---+
      |                   |                    |                   |
      +--------+----------+                    +--------+----------+
               v                                        v
      +-------------------+                    +-------------------+
      | Data layer        | <==== async  ====> | Data layer        |
      | (replicated DB    |   cross-region      | (replica /        |
      |  + cache, multi-AZ|   replication        |  active-active)   |
      +-------------------+                    +-------------------+

   Cross-cutting: health checks at every tier, observability,
   automatic failover, message queues to decouple/absorb spikes.
```

**The shape in words:** **GeoDNS** sends each user to the nearest **region**; within a region, traffic flows through **edge -> global LB -> regional LB -> stateless app instances spread across multiple Availability Zones (AZs)**; data is **replicated within and across regions**. Failure at *any* level reroutes around the dead component automatically.

---

## 3.7 Step 6: Redundancy at Every Layer

Walk each layer and remove its SPOF. This is the heart of the answer.

```
  Layer            | SPOF risk                | How we make it redundant
  -----------------|--------------------------|---------------------------------
  DNS              | One DNS provider/record  | GeoDNS with health checks +
                   |                          | low TTL; often 2 DNS providers.
  Load balancer    | A single LB instance     | Redundant LBs (active-active),
                   |                          | Anycast IPs; cloud LBs are HA.
  App tier         | One instance / one AZ    | N+1 STATELESS instances across
                   |                          | >=2-3 AZs; auto-scaling group
                   |                          | replaces dead instances.
  Availability Zone| Whole datacenter fails   | Deploy across >=3 AZs; LB routes
                   |                          | only to healthy AZs.
  Region           | Entire region outage     | Multi-region (active-active or
                   |                          | active-passive) + GeoDNS failover.
  Database         | Single DB node           | Primary + replicas, multi-AZ,
                   |                          | automatic failover (e.g., Aurora,
                   |                          | Patroni); sharding for scale.
  Cache            | Single cache node        | Redis Cluster/Sentinel, replicas.
  Message broker   | Single broker            | Kafka/RabbitMQ clusters, replicated
                   |                          | partitions/quorum.
```

**Statelessness is the enabler.** Because app instances hold no session state (state lives in the replicated DB / distributed cache / a token like JWT), **any instance can serve any request**. That is what makes "kill an instance and reroute instantly" possible -- the foundation of fast failover and horizontal scaling.

**AZ vs Region (define both, it is commonly asked):**
- An **Availability Zone (AZ)** is an isolated datacenter within a region (independent power, cooling, network). Spreading across AZs survives a datacenter failure with **near-zero latency** between AZs.
- A **Region** is a geographic location containing multiple AZs. Multi-region survives a whole-region disaster and serves global users with low latency, but cross-region replication has **real latency** (tens to >100 ms), forcing consistency trade-offs.

---

## 3.8 Step 7: Global Low Latency (Serving Users Worldwide)

Availability and latency are linked: a slow system that times out is effectively "down." To serve global users fast:

```
  1. GeoDNS / Geo-routing:
     Route each user to the NEAREST region (by latency/geo). A user in
     Europe hits the EU region, not a US datacenter across the ocean.

  2. Anycast:
     The same IP is announced from many locations; network routing sends
     the user to the closest one automatically. Used by CDNs and global LBs.

  3. CDN / Edge PoPs:
     Cache static assets AND cacheable responses at hundreds of edge
     Points of Presence physically near users -> requests never cross
     continents. Biggest single latency win for global reads.

  4. Multi-region active-active:
     Each region runs a full stack and serves its local users. No single
     "home" region everyone must reach.

  5. Read replicas close to users + caching (Problem 2):
     Serve reads from the nearest replica/cache to cut round trips.
```

```
  WHY GLOBAL ROUTING ALSO BOOSTS AVAILABILITY:
   - If the EU region goes down, GeoDNS health checks detect it and reroute
     EU users to the US region. Higher latency, but STILL UP.
   - Latency-based routing + health checks = low latency AND failover in one.
```

---

## 3.9 Step 8: Data Layer -- Replication, Consistency & CAP

The data layer is the hardest part of HA because **data has state** (unlike stateless app servers, you cannot just spin up a fresh copy with no history). 

**Replication** is mandatory -- multiple copies of the data so no single node loss means data loss or downtime:
```
  - Synchronous replication:  primary waits for replicas to confirm a write.
       + No data loss on failover (replicas are current).
       - Higher write latency; cross-region sync replication is often too slow.
  - Asynchronous replication: primary acks immediately, replicas catch up.
       + Fast writes, works across regions.
       - A failover can lose the last few un-replicated writes.
```

**Automatic database failover:** a healthy replica is promoted to primary automatically when the primary fails (e.g., Aurora, Patroni/Postgres, MongoDB replica sets, Redis Sentinel). This is what keeps DB downtime within budget -- manual DB failover is far too slow for four nines.

**The CAP theorem (you must mention it for a distributed data question):**
```
  CAP: during a network PARTITION (P), you can guarantee only ONE of:
     - Consistency (C): every read sees the latest write, or
     - Availability (A): every request gets a (possibly stale) response.

  For a 99.99%-uptime, global, read-heavy system, we generally choose
  AP (availability + partition tolerance) with EVENTUAL CONSISTENCY for
  most data -> the system stays UP and serves slightly stale data during
  a partition, rather than refusing requests.

  For the few operations needing strong consistency (payments, inventory
  decrement), use CP there specifically (consensus/quorum, e.g. Raft) and
  accept lower availability for just those paths.
```

**PACELC (bonus depth):** extends CAP -- *else* (E), even without a partition, you trade **latency (L) vs consistency (C)**. Globally, strong consistency costs latency (cross-region coordination), so we lean toward latency for most reads.

**Practical data choices:** sharding/partitioning for scale, quorum reads/writes where consistency matters, idempotent writes so retries are safe, and a durable **message queue** to absorb write spikes and decouple components (a slow consumer never takes down the producer).

---

## 3.10 Step 9: Failure Detection & Health Checking

You cannot fail over from a failure you have not detected. Fast, accurate detection is the trigger for everything:

```
  - HEALTH CHECKS at every tier:
      Liveness  -> "is the process alive?" (restart if not)
      Readiness -> "can it serve traffic right now?" (remove from LB if not)
    Load balancers poll these and STOP routing to unhealthy instances
    within seconds.

  - HEARTBEATS / GOSSIP:
      Nodes periodically signal "I'm alive." Missing heartbeats -> mark
      the node down (used by clusters: Redis, Cassandra, Kafka).

  - DEEP vs SHALLOW checks:
      Shallow: process responds. Deep: it can actually reach its DB/deps.
      Use deep checks so a node that "responds" but cannot serve is removed.

  - OUTLIER DETECTION:
      Automatically eject instances showing high error rates / latency,
      even if their health check passes.
```

The principle: **detect in seconds, automatically, and remove/replace the bad component before users notice.**

---

## 3.11 Step 10: Graceful Failure Handling (Resilience Patterns)

When a dependency fails, the system must **degrade gracefully**, never cascade into a full outage. The standard resilience patterns (also covered in the Microservices book) are how:

```
  Pattern          | What it does for availability
  -----------------|-----------------------------------------------------
  Timeout          | Never wait forever on a dead dependency; fail fast.
  Retry + backoff  | Absorb TRANSIENT failures (with jitter to avoid storms).
  Circuit Breaker  | Stop calling a failing dependency -> fail fast + serve
                   | a fallback -> PREVENTS CASCADING FAILURE (the #1 cause
                   | of total outages).
  Bulkhead         | Isolate resource pools so one overloaded dependency
                   | cannot starve everything (like ship compartments).
  Fallback /       | Serve cached/stale/default data when a dependency is
  graceful degrade | down (e.g., generic recommendations instead of error).
  Rate limiting /  | Reject/queue excess load so a spike degrades gracefully
  load shedding    | instead of crashing the whole system.
  Idempotency      | Safe retries -> no double effects during recovery.
```

```
  GRACEFUL DEGRADATION EXAMPLE (an e-commerce product page):
   - Recommendations service down? -> show generic popular items.
   - Reviews service down?         -> hide the reviews section.
   - Core "view product + buy"     -> STAYS UP.
   The user gets a slightly reduced page, NOT an error. Availability preserved.
```

**Blast-radius isolation / cell-based architecture (advanced):** partition the system into independent **cells** (each a full stack serving a subset of users). A failure in one cell affects only that subset, not everyone -- this caps the worst-case impact of any single failure, which is critical for high nines.

---

## 3.12 Step 11: Deployment Without Downtime

For four nines, **planned downtime is not allowed** -- you must deploy while serving traffic. (See the Microservices book's deployment chapter for depth.)

```
  - Rolling deployment: replace instances a few at a time; service stays up.
  - Blue-Green: stand up the new version (green) alongside old (blue),
    switch traffic instantly, roll back instantly if needed.
  - Canary: release to 1% -> 10% -> 100%, watching metrics; auto-rollback
    on error-rate/latency regression -> limits blast radius of a bad deploy.
  - Feature flags: deploy code "dark," enable gradually, disable instantly
    without a redeploy.
  - Backward-compatible changes: especially DB migrations (expand/contract
    pattern) so old and new versions run simultaneously during rollout.
```

A bad deployment is one of the most common causes of self-inflicted outages -- canary + automatic rollback is the main defense.

---

## 3.13 Step 12: Observability, Alerting & On-Call

You cannot keep a system up if you are blind to it. Observability is what makes MTTR small.

```
  - METRICS (RED/USE): request rate, error rate, latency (p50/p95/p99),
    saturation per service -> dashboards + automated alerts.
  - LOGS: centralized, structured, correlated by trace ID.
  - DISTRIBUTED TRACING: follow one request across all services to find
    WHERE a failure or latency originates in seconds.
  - SLO + ERROR BUDGET: define the 99.99% SLO explicitly; track the error
    budget; alert on BURN RATE (how fast you are consuming the budget).
  - ALERTING on SYMPTOMS users feel (high error rate, high p99), not just
    raw CPU -> page a human only when automation cannot self-heal.
  - RUNBOOKS + automated remediation for known failure modes.
```

The goal of observability for HA: **detect and pinpoint failures fast enough that automatic recovery (or, rarely, a paged human) restores service within the tiny error budget.**

---

## 3.14 Step 13: Testing Resilience (Chaos Engineering)

A system is only as available as you have *proven* it to be. You cannot claim four nines without testing failure:

```
  - CHAOS ENGINEERING: deliberately inject failures in production-like
    (or production) environments to verify the system self-heals.
    Pioneered by Netflix's "Chaos Monkey" (randomly kills instances) and
    the Simian Army (kills zones/regions, adds latency).
  - GAME DAYS: planned exercises where the team simulates an outage
    (e.g., "Region A is gone") and validates failover + runbooks.
  - LOAD/STRESS TESTING: confirm the system degrades gracefully (sheds
    load) instead of collapsing under spikes.
  - FAILOVER DRILLS: regularly force DB and region failovers so they are
    proven and fast (an untested failover is a future outage).
```

**Principle:** failures are inevitable, so practice them on your terms. If killing a random instance or region causes no user-visible impact, your redundancy and failover actually work.

---

## 3.15 Step 14: The Availability Math of the Whole System

A subtle but crucial point that impresses interviewers -- **redundancy multiplies availability, serial dependencies divide it:**

```
  COMPONENTS IN SERIES (a request needs ALL of them) -> multiply:
     If a request passes through 4 components each 99.9% available:
       0.999^4 = 0.996  -> only 99.6% (WORSE than any single component!)
     Lesson: long dependency chains ERODE availability. Keep chains short;
     make non-critical dependencies optional (graceful degradation).

  REDUNDANT COMPONENTS IN PARALLEL (any ONE suffices) -> failure multiplies:
     Two redundant instances each 99% available (1% fail):
       combined failure = 0.01 x 0.01 = 0.0001 -> 99.99% available!
     Three of them -> 99.9999%. Lesson: REDUNDANCY is how cheap parts
     become a highly available whole.
```

**Two design conclusions from the math:**
1. **Add redundancy (parallelism)** to push each layer's availability up.
2. **Shorten critical serial chains** and make non-essential dependencies **optional**, so a failure there degrades rather than fails the request.

---

## 3.16 Step 15: Trade-offs & Pitfalls

A senior answer is honest about costs:

```
  Trade-off / pitfall        | Detail
  ---------------------------|--------------------------------------------
  Cost                       | Multi-region active-active roughly multiplies
                             | infrastructure spend. Four nines is expensive;
                             | five nines is dramatically more. Match the SLA
                             | to real business need.
  Consistency vs availability| Choosing AP/eventual consistency means users
                             | may briefly see stale data. Acceptable for most
                             | reads, not for money/inventory.
  Complexity                 | Multi-region, failover, replication, chaos
                             | testing add huge operational complexity and
                             | demand strong DevOps maturity.
  Cross-region latency       | Sync replication across regions is often too
                             | slow -> async -> potential small data loss on
                             | failover (RPO > 0).
  Failover risk              | Failover itself can fail or cause split-brain;
                             | needs fencing/quorum and regular testing.
  Over-engineering           | Not every system needs four nines. An internal
                             | tool at 99.5% is fine and far cheaper.
```

**Two metrics to define when discussing disaster recovery:**
```
  RPO (Recovery Point Objective) -> how much DATA you can afford to lose
                                    (drives sync vs async replication).
  RTO (Recovery Time Objective)  -> how FAST you must recover
                                    (drives automatic failover design).
  Four nines effectively demands RTO in seconds and a small RPO.
```

---

## 3.17 Problem 3 Summary

- **Four nines = ~52.6 min/year of allowed downtime** -> recovery must be **automatic** (drive MTTR -> 0), since failures are inevitable (`Availability ~ MTBF/(MTBF+MTTR)`).
- The core obsession: **eliminate every SPOF** via **redundancy at every layer** (DNS, LB, app, AZ, region, DB, cache, broker) plus **automatic failover**; **statelessness** makes failover and scaling trivial.
- **Global low latency** via **GeoDNS + Anycast + CDN edge + multi-region active-active** -- which doubles as region-level failover.
- **Data layer:** replication (sync vs async trade-off), automatic DB failover, and a deliberate **CAP** choice -- **AP/eventual consistency** for most data, **CP** only where correctness demands it (payments).
- **Detect fast** (health checks, heartbeats, outlier detection) and **degrade gracefully** (timeouts, retries, **circuit breakers** to stop cascades, bulkheads, fallbacks, load shedding, cell isolation).
- **Zero-downtime deploys** (rolling/blue-green/canary + feature flags + backward-compatible migrations); **observability + SLO/error-budget** alerting; **chaos engineering** to prove it all works.
- Remember the math: **serial dependencies erode availability (multiply down); parallel redundancy builds it (failures multiply down)** -- so add redundancy and shorten critical chains.
- It is a **trade-off**: four nines is costly and complex; define **RPO/RTO** and match the target to real business need rather than over-engineering.

---

# Problem 4: Design a Payment System

## 4.1 Problem Statement

> Design a payment system that lets users pay for goods/services -- for example, the checkout flow of an e-commerce platform. It must move money **correctly**, integrate with external payment providers, and never lose or duplicate a transaction.

This is the classic "correctness-critical" HLD question. Unlike a search or feed system where a dropped request is a minor annoyance, in payments **a single bug means real money is lost, double-charged, or stuck**. The interviewer is testing whether you prioritize **correctness, consistency, idempotency, auditability, and security** over raw throughput. Get those right and you pass; talk only about scaling and you fail.

---

## 4.2 Step 1: Why Payments Are Different (Correctness Over Everything)

Lead with this framing -- it shows you understand the domain:

```
  WHAT MAKES PAYMENTS HARD (vs a typical CRUD/search system):

  1. MONEY MUST BALANCE. Every cent is accounted for. No "approximately."
  2. NO DOUBLE-CHARGE. A retry/network glitch must NEVER charge twice.
  3. NO LOST PAYMENT. A crash mid-flow must never leave money in limbo.
  4. STRONG CONSISTENCY where it counts -- not eventual consistency for
     a balance debit. (Contrast with Problem 3's mostly-AP choice.)
  5. AUDITABILITY. Every state change is permanently recorded & traceable
     (regulators, disputes, accounting).
  6. EXTERNAL DEPENDENCIES you do not control (banks, card networks, PSPs)
     that are slow, fail, and are asynchronous.
  7. SECURITY & COMPLIANCE (PCI-DSS) -- you handle the most sensitive data.
  8. IRREVERSIBILITY. You cannot "undo" a sent bank transfer; you can only
     issue a COMPENSATING transaction (a refund).
```

**The guiding principle:** in payments, it is almost always better to **fail safe and retry than to risk an incorrect state**. We design so that the worst common case is a *delayed* or *temporarily-pending* payment, never a *lost* or *duplicated* one.

---

## 4.3 Step 2: Clarifying Questions & Scope

```
  Question                                  | Assumption we proceed with
  ------------------------------------------|----------------------------------
  What kind of payments?                    | Card payments at e-commerce
                                            | checkout (extensible to wallets).
  Do we store card numbers ourselves?       | NO -- we tokenize via a PSP
                                            | (Stripe/Adyen) to minimize PCI scope.
  Are we the merchant, or building a PSP     | Merchant-side payment service that
  like Stripe?                              | orchestrates payments via a PSP.
  Which currencies / regions?               | Multi-currency, global (keep it
                                            | general).
  Do we need payouts/refunds?               | Yes -- refunds and (briefly) payouts.
  Synchronous "approved now" or async?      | Authorize synchronously; settlement
                                            | is asynchronous (reality of cards).
  Expected scale?                           | Moderate: ~1000s of TPS peak. Scale
                                            | matters, but correctness dominates.
```

**Key scoping decision: do NOT store raw card data.** We **tokenize** through a PCI-compliant PSP so the sensitive PAN (card number) never touches our servers. This single decision removes most of our PCI-DSS burden and is the correct real-world choice. State this early.

---

## 4.4 Step 3: Requirements (Functional & Non-Functional)

**Functional:**
- Initiate a payment for an order (authorize + capture).
- Integrate with one or more **Payment Service Providers (PSPs)**.
- Support **refunds** (full/partial) and cancellations.
- Maintain an immutable **ledger** of all money movements.
- Notify other services (Order, Notification) of payment outcomes.
- Provide payment status lookup and a **reconciliation** process.

**Non-functional (correctness-first):**
- **Consistency & correctness:** no double-charge, no lost payment; money always balances.
- **Idempotency:** safe to retry any operation.
- **Durability:** once accepted, a payment record is never lost.
- **Auditability:** complete, immutable history of every state transition.
- **Security & PCI-DSS compliance.**
- **High availability** (it is a revenue path) and reasonable latency (authorize within a couple of seconds).

---

## 4.5 Step 4: Key Concepts & Glossary

Defining these signals domain knowledge and makes the rest of the answer precise:

```
  Term            | Meaning
  ----------------|----------------------------------------------------------
  PSP / Gateway   | Payment Service Provider (Stripe, Adyen, Braintree) that
                  | talks to card networks/banks on your behalf.
  Authorization   | Bank confirms funds exist & places a HOLD. Money not yet
                  | moved. (e.g., the "pending" charge you see.)
  Capture         | Actually move the held funds to the merchant. Often done
                  | at shipment, can be hours/days after auth.
  Settlement      | Banks/networks transfer the real money in batches (async,
                  | T+1 / T+2 days).
  Clearing        | Reconciling and finalizing transactions between banks.
  Tokenization    | Replacing the card number (PAN) with a safe token so we
                  | never store raw card data.
  Idempotency Key | A unique client-supplied id so repeats of the SAME request
                  | execute only ONCE.
  Ledger          | Immutable, double-entry record of all money movements.
  Reconciliation  | Comparing our records against the PSP's to catch mismatches.
  Chargeback      | A customer disputes a charge; funds are reversed by the bank.
```

**Auth-and-capture (two-step) vs sale (one-step):** most systems **authorize** at checkout (hold funds, validate the card instantly) and **capture** later (e.g., when the item ships). This avoids charging for goods you cannot fulfill. Mention this -- it is the real-world pattern.

---

## 4.6 Step 5: Capacity Estimation

```
  Assume a large merchant: ~10 million payments/day.
    Average TPS = 10,000,000 / 86,400 ~ 116 TPS.
    Peak (e.g., flash sale, x10) ~ 1,000-2,000 TPS.

  Storage (the ledger grows forever -- it is immutable & audited):
    Each payment -> several ledger entries + events. Say ~5 KB total/payment.
    10M/day x 5 KB = ~50 GB/day -> ~18 TB/year.
    -> Plan for partitioning/archival; NEVER delete (regulatory retention).

  Observation: payment TPS is MODEST compared to a search system. The
  challenge is NOT throughput -- it is CORRECTNESS, durability, and the
  long-lived, ever-growing audit data.
```

This reinforces the framing: payments are a **consistency and durability** problem, not a raw-scale problem.

---

## 4.7 Step 6: API Design

The idempotency key is the most important API detail:

```
  POST /api/v1/payments
  Headers:
    Authorization: Bearer <JWT>
    Idempotency-Key: 7f3c1e9a-...   <-- client-generated UNIQUE id per attempt
  Body:
  {
    "orderId": "ORD-9981",
    "amount": 4999,                 <-- store money as INTEGER minor units
    "currency": "USD",             |     (cents). NEVER use float for money.
    "paymentToken": "tok_visa_xxx", <-- tokenized card from the PSP, not a PAN
    "captureMode": "AUTO"           <-- AUTO (sale) or MANUAL (auth then capture)
  }

  201 Created
  {
    "paymentId": "PAY-abc123",
    "status": "AUTHORIZED",         <-- PENDING|AUTHORIZED|CAPTURED|FAILED|REFUNDED
    "amount": 4999, "currency": "USD",
    "createdAt": "..."
  }

  Other endpoints:
    GET  /api/v1/payments/{id}                 -> status lookup
    POST /api/v1/payments/{id}/capture         -> capture an authorized payment
    POST /api/v1/payments/{id}/refund          -> refund (also idempotent)
    POST /api/v1/webhooks/psp                   -> async callbacks from the PSP
```

**Two non-negotiable API rules to call out:**
1. **Represent money as integer minor units (cents)** -- never floating point, which has rounding errors. (Or a fixed-precision decimal type.)
2. **Every state-changing endpoint takes an Idempotency-Key** so retries are safe.

---

## 4.8 Step 7: High-Level Architecture

```
PAYMENT SYSTEM ARCHITECTURE

   +------------------+
   |  Client / Order  |  checkout: "pay for order ORD-9981"
   |  Service         |  (sends an Idempotency-Key)
   +--------+---------+
            | HTTPS
            v
   +------------------+
   |   API Gateway    |  authN/JWT, rate limit, TLS
   +--------+---------+
            v
   +-----------------------------+        +--------------------------+
   |     Payment Service         |        |   Idempotency Store      |
   | (orchestrator / state mgr)  |<------>| (key -> result, dedup)   |
   +----+-------------------+----+        +--------------------------+
        |                   |
        | writes (ACID)     | publish events (via Outbox)
        v                   v
  +-------------+    +--------------------+
  |  Ledger DB  |    |  Message Broker    |---> Order Service (mark paid)
  | (double-    |    |  (Kafka)           |---> Notification Service (email)
  |  entry,     |    +--------------------+---> Analytics / Risk
  |  strong     |
  |  consistency|            ^  async webhooks (payment.captured, etc.)
  |  ACID)      |            |
  +------+------+    +-------+----------+
         |           |  PSP Adapter      |  retries, timeouts, circuit breaker
         |           | (anti-corruption  |
         |           |  layer per PSP)   |
         |           +-------+----------+
         |                   | HTTPS
         |                   v
         |          +--------------------+      +---------------------+
         |          |  External PSP      |----> | Card Networks/Banks |
         |          | (Stripe/Adyen)     |      | (Visa, issuer)      |
         |          +--------------------+      +---------------------+
         v
  +-------------------+
  | Reconciliation    |  daily: compare Ledger vs PSP settlement reports
  | Service (batch)   |
  +-------------------+
```

**Component roles:**
- **Payment Service:** the orchestrator. Owns the payment state machine, enforces idempotency, writes the ledger, and talks to the PSP via the adapter.
- **Ledger DB:** a **strongly consistent, ACID** relational store (e.g., PostgreSQL). This is deliberately *not* eventually consistent -- money requires ACID.
- **Idempotency Store:** maps each idempotency key to its result so duplicate requests return the original response instead of re-charging.
- **PSP Adapter:** an **anti-corruption layer** wrapping each external provider, with timeouts, retries, and a circuit breaker. Swappable per PSP.
- **Message Broker (Kafka):** publishes payment events to other services **asynchronously** via the Outbox pattern.
- **Reconciliation Service:** a batch job that compares our ledger to the PSP's settlement files to catch any divergence.

---

## 4.9 Step 8: The Double-Entry Ledger (Heart of the System)

The **ledger** is the single most important concept in a payment system. Borrowed from centuries-old accounting, **double-entry bookkeeping** records every money movement as **two balanced entries** -- a debit and a credit -- that always sum to zero.

```
  DOUBLE-ENTRY: every transaction touches >=2 accounts and MUST balance.

  Customer pays $49.99 for an order:
    +-------------------------------+--------+--------+
    | Account                       | Debit  | Credit |
    +-------------------------------+--------+--------+
    | customer_payable (their card) | 4999   |        |
    | merchant_receivable           |        | 4999   |
    +-------------------------------+--------+--------+
    Sum of debits (4999) == Sum of credits (4999)  -> BALANCED. Valid.

  A refund is NOT a deletion -- it is a NEW, reversing transaction:
    +-------------------------------+--------+--------+
    | merchant_receivable           | 4999   |        |
    | customer_payable              |        | 4999   |
    +-------------------------------+--------+--------+
```

**Critical ledger properties (state these explicitly):**
- **Immutable / append-only:** you never UPDATE or DELETE a ledger entry. Corrections are new reversing entries. This gives a perfect, tamper-evident audit trail.
- **Always balances:** the invariant "sum(debits) == sum(credits)" lets you detect corruption instantly.
- **Source of truth for balances:** an account balance is *derived* by summing its entries (often with periodic snapshots for performance) -- you do not store a mutable balance you might corrupt.
- **Written in an ACID transaction:** the debit and credit are committed atomically together, or not at all.

This append-only, double-entry design is essentially **event sourcing for money** and is why real payment systems (Stripe, banks) use it.

---

## 4.10 Step 9: Idempotency -- Never Charge Twice

The single most important reliability mechanism. The scenario: a client sends "charge $50," the charge succeeds, but the **response is lost** to a network timeout. The client retries. Without protection, the customer is **charged twice**.

```
  IDEMPOTENCY FLOW (server side):

   1. Request arrives with Idempotency-Key = K.
   2. Look up K in the Idempotency Store.
        - FOUND (completed)   -> return the STORED original response. STOP.
                                 (No second charge. This is the key win.)
        - FOUND (in-progress) -> reject/wait (a concurrent duplicate).
        - NOT FOUND           -> reserve K (atomic insert), proceed.
   3. Execute the payment (call PSP, write ledger) exactly once.
   4. Store the result under K.
   5. Return the response.
```

**Implementation details to mention:**
- The "reserve K" step must be **atomic** (e.g., a unique constraint / `INSERT ... ON CONFLICT`) so two concurrent duplicates cannot both proceed -- this prevents a race.
- Idempotency keys have a **retention window** (e.g., 24h) after which they expire.
- Pass an idempotency key **to the PSP too** (Stripe/Adyen support this), so even the external charge is deduplicated end to end.
- Combine with **at-least-once** delivery elsewhere: because consumers may receive duplicate events, all downstream effects must be idempotent as well.

---

## 4.11 Step 10: The Payment Flow (Authorize -> Capture) & Saga

A checkout spans multiple services and the external PSP. Because there is **no distributed ACID transaction** across the Order service, Payment service, and the bank, we use a **Saga** (a sequence of local transactions with compensations) -- see the Microservices book, Saga chapter.

```
  HAPPY-PATH PAYMENT FLOW (auth + capture):

   1. Order Service -> Payment Service: pay(order, amount, token, key=K)
   2. Payment Service: create Payment row (status=PENDING) + ledger pending
        entry, in ONE ACID transaction. (Idempotency checked on K.)
   3. Payment Service -> PSP Adapter -> PSP: AUTHORIZE (hold funds).
   4. PSP responds APPROVED.
   5. Payment Service: status=AUTHORIZED, write balanced ledger entries
        (ACID). Publish "PaymentAuthorized" via Outbox.
   6. Later (e.g., on shipment): CAPTURE -> PSP moves the funds.
        status=CAPTURED, ledger updated, publish "PaymentCaptured".
   7. Order Service consumes the event -> marks order PAID.

  FAILURE HANDLING (saga compensation):
   - Auth DECLINED at step 4 -> status=FAILED; publish "PaymentFailed";
        Order Service cancels the order. (Nothing to compensate -- no funds moved.)
   - Crash AFTER auth but BEFORE marking AUTHORIZED -> on recovery, the
        payment is in PENDING; a reconciliation/retry job queries the PSP by
        the idempotency key to learn the true outcome and fixes the state.
   - Capture fails repeatedly -> VOID the authorization (compensation) so the
        customer's held funds are released.
   - Need to undo a completed capture -> issue a REFUND (a compensating
        transaction; you cannot delete the original).
```

**Key point:** compensations in payments are **business reversals** (void, refund), not database rollbacks, because the money movement happened in an external system. The saga state machine plus idempotency makes the whole flow recoverable from any crash point.

---

## 4.12 Step 11: Consistency, Exactly-Once & the Outbox Pattern

A subtle but critical problem: the Payment Service must do two things when a payment succeeds -- (a) **commit to the ledger DB** and (b) **publish an event** to Kafka. If it commits the DB then crashes before publishing (or vice versa), the system is inconsistent: money moved but the Order service never hears about it (or an event fires for a payment that was rolled back). This is the **dual-write problem**.

```
  THE TRANSACTIONAL OUTBOX PATTERN (solves dual-write):

   1. In ONE ACID transaction on the Ledger DB:
        - write the payment/ledger rows, AND
        - INSERT the event into an "outbox" table.
      (Both commit together or not at all -- atomic.)

   2. A separate Relay process (or CDC, e.g., Debezium) reads the outbox
      table and publishes the events to Kafka, marking them sent.

   3. If publishing fails, the relay retries (the event is safely in the DB).
      -> Guarantees the event is published IF AND ONLY IF the payment committed.

   Result: no lost events, no phantom events. "Effectively exactly-once."
```

**On "exactly-once":** true exactly-once delivery is impossible over an unreliable network. What we achieve is **at-least-once delivery + idempotent consumers = effectively-once processing.** Every consumer (Order, Notification) deduplicates by payment/event id. Combined with the outbox, this gives end-to-end correctness without phantom or lost effects.

---

## 4.13 Step 12: Handling External PSPs (Failures, Timeouts, Webhooks)

The PSP is the part you do not control -- it is slow, occasionally down, and partly asynchronous. Design defensively:

```
  - TIMEOUTS on every PSP call (never hang forever).
  - RETRY with backoff + jitter, ALWAYS with the SAME idempotency key so a
    retry never creates a second charge.
  - CIRCUIT BREAKER around the PSP -> fail fast if it is down; optionally
    fail over to a SECONDARY PSP (multi-PSP routing) for availability.
  - THE "IN-DOUBT" CASE (most important): a PSP call times out -> you do
    NOT know if the charge succeeded. NEVER assume. Resolve it by:
       (a) querying the PSP by idempotency key for the true status, or
       (b) waiting for the PSP's webhook, or
       (c) catching it in reconciliation.
    Leave the payment PENDING until the truth is known -- fail safe.

  - WEBHOOKS: PSPs notify outcomes asynchronously (payment.captured,
    payment.failed, chargeback.created). Webhook handling must be:
       * idempotent (the PSP may send the same webhook multiple times),
       * signature-verified (authenticate it really came from the PSP),
       * tolerant of out-of-order delivery.
```

This "never assume, always verify" handling of the in-doubt timeout is exactly what interviewers probe -- it is where naive designs double-charge or lose money.

---

## 4.14 Step 13: Reconciliation (Trust but Verify)

Even with perfect code, your records can drift from the PSP's (a missed webhook, an in-doubt timeout, a PSP-side adjustment). **Reconciliation** is the safety net that guarantees long-run correctness.

```
  RECONCILIATION (typically a daily batch job):

   1. The PSP provides a SETTLEMENT REPORT/file of everything it processed
      (each charge, refund, fee, payout) for the period.
   2. The job compares, line by line, the PSP report against OUR ledger.
   3. It flags MISMATCHES:
        - In PSP but not us  -> we missed a webhook; create the ledger entry.
        - In us but not PSP  -> our auth never settled; investigate/void.
        - Amount differs     -> fees/FX; record the adjustment.
   4. Unresolved mismatches -> alert humans / an exceptions queue.
```

**Principle:** the external system (PSP/bank) plus reconciliation is the ultimate source of truth for money. Reconciliation turns "we think it's correct" into "we have proven it balances." Every serious payment system has it.

---

## 4.15 Step 14: Security, PCI-DSS & Fraud

Payments handle the most sensitive data, so security is a first-class requirement, not an afterthought:

```
  - TOKENIZATION (the big one): never store raw card numbers (PAN). Use the
    PSP to exchange the card for a TOKEN. This keeps card data OUT of your
    systems and drastically shrinks PCI-DSS scope.
  - PCI-DSS compliance: the standard for handling cardholder data. By
    tokenizing, you qualify for the simplest compliance level (SAQ-A).
  - ENCRYPTION: TLS in transit; AES-256 at rest for any sensitive fields.
  - SECRETS MANAGEMENT: PSP API keys in a vault (HashiCorp Vault/KMS),
    never in code or config.
  - AUTHN/AUTHZ: strong auth on all endpoints; least-privilege internal access.
  - AUDIT LOG: immutable record of who did what (the ledger helps here).
  - FRAUD DETECTION: a risk-scoring step (velocity checks, device
    fingerprinting, ML models, 3-D Secure / OTP step-up) before authorizing
    suspicious transactions.
  - IDEMPOTENCY also defends against duplicate-submit abuse.
```

State tokenization explicitly -- it is the defining security decision and the first thing a payments interviewer wants to hear.

---

## 4.16 Step 15: Trade-offs & Pitfalls

```
  Decision / pitfall          | Discussion
  ----------------------------|-------------------------------------------
  Strong vs eventual          | Ledger writes are STRONGLY consistent (ACID).
  consistency                 | Cross-service coordination is eventual (saga
                              | + events). Pick consistency per boundary.
  Float for money             | PITFALL: never use float/double -> rounding
                              | errors. Use integer minor units or decimals.
  Assuming PSP success        | PITFALL: a timeout != failure. Always verify
                              | the in-doubt case; never silently retry-charge.
  Mutable balance column      | PITFALL: storing a balance you UPDATE can be
                              | corrupted. Derive it from the immutable ledger.
  Skipping idempotency        | PITFALL: guarantees eventual double-charges.
  Skipping reconciliation     | PITFALL: silent drift from the PSP goes unnoticed.
  Storing card data           | PITFALL: massive PCI burden + breach risk.
                              | Tokenize instead.
  Synchronous everything      | Authorize sync (user waits) but make settlement,
                              | notifications, analytics async for resilience.
```

**When to keep it simpler:** a small merchant can often just use a hosted PSP checkout (Stripe Checkout) and store only the PSP's payment id + status -- the full ledger/reconciliation machinery is for when you are processing significant volume or building a platform. Mentioning this shows judgment.

---

## 4.17 Problem 4 Summary

- Payments are a **correctness, consistency, and auditability** problem -- not a throughput problem. The mantra: **never double-charge, never lose a payment, always balance, fail safe.**
- The **double-entry, append-only ledger** (debits == credits, immutable, ACID) is the heart of the system; balances are **derived**, never mutated.
- **Idempotency keys** (atomic reservation + stored results, passed through to the PSP) prevent double-charges on retries; pair with **at-least-once + idempotent consumers** for effectively-once processing.
- Use **authorize -> capture**, orchestrated as a **saga** with **compensations** (void/refund) since there is no distributed ACID across services and the bank.
- Solve the dual-write problem with the **Transactional Outbox** so events fire **iff** the ledger commits -- no lost or phantom events.
- Treat the **PSP** defensively: timeouts, same-key retries, circuit breaker, and a strict **"never assume, always verify"** rule for in-doubt timeouts (query/webhook/reconcile).
- **Reconciliation** against the PSP's settlement report is the safety net that proves long-run correctness.
- **Security/PCI:** **tokenize** to keep card data out of your systems, encrypt everything, vault your secrets, and add fraud scoring (3-D Secure).
- Money is **integer minor units**, never float; strong consistency at the ledger, eventual across services.

---

# Problem 5: Design a Recommendation System

## 5.1 Problem Statement

> Design a recommendation system that suggests relevant items to users -- for example products in an e-commerce app, movies in a streaming platform, posts in a social feed, or songs in a music app.

A recommendation system is not just a search system. Search is **user says what they want**; recommendation is **the system predicts what the user may want next**. A strong HLD answer should cover the full lifecycle: **collect user behavior -> build features/embeddings -> generate candidate items -> rank them -> serve with low latency -> learn from feedback -> evaluate with A/B tests**.

---

## 5.2 Step 1: Clarifying Questions & Scope

```
  Question                                  | Assumption we proceed with
  ------------------------------------------|----------------------------------
  What are we recommending?                 | Products in an e-commerce app.
  Where are recommendations shown?          | Home page, product page, cart page.
  Personalized or generic?                  | Personalized when possible; fallback
                                            | to popular/trending for cold users.
  Scale?                                    | 100M users, 100M items, millions of
                                            | daily interactions.
  Latency target?                           | p99 < 200 ms for online serving.
  Freshness?                                | Near-real-time for user actions;
                                            | full model retrain daily/hourly.
  Main goal?                                | Increase click-through rate (CTR),
                                            | conversion, and long-term engagement.
  Can we use ML?                            | Yes -- hybrid ML + rules system.
```

**Important scoping decision:** recommendation has two very different systems:
- **Offline/nearline intelligence pipeline:** heavy data processing and model training.
- **Online serving system:** low-latency API that returns recommendations in milliseconds.

Do not put heavy ML computation directly in the request path unless it is very small and optimized.

---

## 5.3 Step 2: Requirements (Functional & Non-Functional)

**Functional requirements:**
- Recommend top N items for a user/context.
- Support multiple surfaces: home page, item detail page, cart, email.
- Use user behavior: views, clicks, purchases, likes, dwell time, skips.
- Support fallbacks for anonymous/new users and new items.
- Track impressions and feedback so the system can learn.

**Non-functional requirements:**
- **Low latency:** p99 < 200 ms for online recommendation calls.
- **High availability:** recommendation should not break the core app; if it fails, show fallback content.
- **Scalability:** handle high QPS across many users and items.
- **Freshness:** recent user actions should influence recommendations quickly.
- **Relevance:** recommendations should be accurate, diverse, and not repetitive.
- **Explainability/debuggability:** ability to answer \"why was this recommended?\"

---

## 5.4 Step 3: Recommendation Approaches

There are three major approaches. Real systems combine them.

```
  Approach                 | Idea                                  | Strength / weakness
  -------------------------|---------------------------------------|----------------------------
  Popular / trending       | Recommend globally popular items.     | Simple, strong cold-start
                           |                                       | fallback; not personalized.
  Content-based filtering  | Recommend items similar to what the   | Works for new-ish users if
                           | user liked, using item attributes.    | item metadata is good.
  Collaborative filtering  | Users who behaved similarly liked     | Powerful personalization;
                           | similar items.                        | cold-start problem.
  Hybrid                   | Combine popularity, content,          | Most real-world systems.
                           | collaborative, business rules, ML.    |
```

**Example:** if a user bought a running shoe, content-based methods recommend similar running shoes; collaborative filtering recommends socks, fitness watches, or hydration packs because similar users bought those too. The hybrid model combines both.

---

## 5.5 Step 4: Capacity Estimation

```
  Assume:
    100M monthly users
    10M daily active users
    Each active user requests recommendations ~20 times/day

  Average recommendation QPS:
    10M x 20 / 86,400 ~ 2,315 QPS

  Peak QPS (x10 during sales/events):
    ~25,000 QPS

  Items:
    100M items
    Item embedding: 256 dimensions x 4 bytes = 1 KB per item
    100M x 1 KB = ~100 GB embeddings
    With indexes/metadata/replicas -> several hundred GB to TB scale.

  Events:
    10M users x 100 actions/day = 1B events/day
    Event pipeline must handle huge write volume.
```

**Conclusion:** online recommendation QPS is manageable with caching and horizontal scaling. The bigger scale challenge is the **data pipeline**: collecting, processing, storing, and learning from billions of events.

---

## 5.6 Step 5: API Design

```
  GET /api/v1/recommendations?userId=U123&surface=home&limit=20

  Headers:
    Authorization: Bearer <JWT>
    X-Request-Id: req-123

  200 OK
  {
    "userId": "U123",
    "surface": "home",
    "requestId": "req-123",
    "strategy": "personalized_hybrid_v3",
    "results": [
      {
        "itemId": "P9981",
        "score": 0.973,
        "reason": "similar users bought this",
        "trackingToken": "imp_abc_1"
      }
    ]
  }

  Event tracking:
    POST /api/v1/events
    {
      "userId": "U123",
      "eventType": "IMPRESSION|CLICK|ADD_TO_CART|PURCHASE",
      "itemId": "P9981",
      "surface": "home",
      "trackingToken": "imp_abc_1",
      "timestamp": "..."
    }
```

**Why tracking tokens matter:** the system must know which recommendation was shown and whether the user clicked/purchased it. Without impression tracking, you cannot correctly measure CTR or train models.

---

## 5.7 Step 6: High-Level Architecture

```
RECOMMENDATION SYSTEM ARCHITECTURE

             +------------------+
             | Web / Mobile App |
             +--------+---------+
                      |
                      v
             +------------------+
             |   API Gateway    |
             | auth, rate limit |
             +--------+---------+
                      |
                      v
       +------------------------------+
       | Recommendation Service       |  low-latency online serving
       | candidate fetch + ranking    |
       +----+------------+------------+
            |            |
            |            v
            |     +------------------+
            |     | Online Feature   |  Redis / low-latency KV
            |     | Store            |
            |     +------------------+
            |
            v
   +-------------------+      +---------------------+
   | Candidate Store   |      | Vector DB / ANN     |
   | precomputed lists |      | item/user embeddings|
   +-------------------+      +---------------------+
            |
            v
   +-------------------+
   | Cache (Redis/CDN) |  cached top recs per user/surface
   +-------------------+

  ---------------------- offline / nearline pipeline ----------------------

   +-------------+      +----------------+      +----------------------+
   | User Events |----->| Kafka / Stream |----->| Real-time processors |
   | clicks, buy |      | ingestion      |      | session features     |
   +-------------+      +----------------+      +----------+-----------+
                                                         |
                                                         v
                                              +----------------------+
                                              | Feature Store        |
                                              | online + offline     |
                                              +----------+-----------+
                                                         |
          +----------------------+     +-----------------v-----------+
          | Data Lake / Warehouse|<----| Batch ETL / Spark/Flink     |
          +----------+-----------+     +-----------------------------+
                     |
                     v
          +----------------------+
          | Model Training       | collaborative, content, ranking ML
          +----------+-----------+
                     |
                     v
          +----------------------+
          | Model Registry /     |
          | Deployment           |
          +----------------------+
```

**Architecture summary:** offline systems learn from historical behavior and precompute candidates/features; online systems fetch candidates, enrich with real-time features, rank quickly, apply business rules, cache, and return results.

---

## 5.8 Step 7: Data Collection & Event Pipeline

Recommendations are only as good as the data. Track both **positive** and **negative/weak** signals:

```
  Signal             | Meaning / weight
  -------------------|---------------------------------------
  Impression          | Item was shown. Needed for CTR math.
  Click               | Weak positive interest.
  Add to cart         | Stronger positive intent.
  Purchase            | Strongest positive signal.
  Dwell time          | User spent time viewing the item.
  Skip / hide         | Negative feedback.
  Return / refund     | Negative or quality signal.
```

```
EVENT PIPELINE

  Client -> Event Collector -> Kafka -> Stream Processor -> Feature Store
                                      -> Data Lake / Warehouse
                                      -> Training datasets
```

**Important details:**
- Events should be **append-only** and immutable.
- Use a schema registry so event formats evolve safely.
- Deduplicate events using eventId/trackingToken.
- Partition Kafka by `userId` for ordered user activity processing.
- Store raw events in a data lake for replay and model retraining.

---

## 5.9 Step 8: Candidate Generation

You cannot rank 100M items per request. The first stage narrows the universe from **100M items -> a few hundred/thousand candidates**.

```
  Candidate sources:

  1. User-based collaborative filtering:
     \"Users similar to you liked these items.\"

  2. Item-based collaborative filtering:
     \"Users who viewed/bought this item also viewed/bought these.\"

  3. Content-based:
     Similar category, brand, tags, text embeddings, image embeddings.

  4. Vector similarity / ANN:
     Find nearest items to the user's embedding in vector space.

  5. Trending / popular:
     Category-level or global fallback.

  6. Business rules:
     Sponsored items, inventory constraints, margin boosting, regional rules.
```

```
CANDIDATE GENERATION FLOW

  userId + context
        |
        +--> precomputed user recommendations from Candidate Store
        +--> similar items from Vector DB / ANN index
        +--> trending/category fallback
        +--> sponsored/business candidates
        |
        v
  merge + deduplicate + filter unavailable/blocked items
        |
        v
  top ~500 candidates sent to ranking
```

**Candidate generation should be fast and broad.** It optimizes for recall: do not miss potentially good items. Ranking later optimizes precision.

---

## 5.10 Step 9: Ranking & Personalization

Ranking sorts candidates by predicted usefulness to the user. A typical ranking model predicts one or more probabilities:

```
  score = w1 * P(click)
        + w2 * P(add_to_cart)
        + w3 * P(purchase)
        + w4 * business_value
        - w5 * user_fatigue
```

Features used by the ranker:
```
  User features:
    age bucket, location, language, long-term interests, recent session intent

  Item features:
    category, brand, price, popularity, rating, availability, margin

  User-item features:
    similarity(user_embedding, item_embedding), previously viewed,
    same brand affinity, price affinity

  Context features:
    time of day, device, page/surface, location, season, campaign
```

**Post-ranking rules:** after ML scoring, apply product rules:
- remove out-of-stock items
- remove items already purchased recently
- diversify categories/brands
- enforce safety/compliance rules
- insert sponsored items carefully

```
  Candidate generation = recall-oriented
  Ranking              = precision-oriented
  Re-ranking/rules     = business + diversity + safety
```

---

## 5.11 Step 10: Feature Store, Embeddings & Vector Search

**Feature Store:** a central system that stores ML features consistently for training and serving.

```
  Offline Feature Store:
    - historical features for model training
    - backed by warehouse/data lake
    - high throughput, not low latency

  Online Feature Store:
    - latest features for real-time serving
    - backed by Redis/Cassandra/DynamoDB
    - low-latency reads (milliseconds)
```

**Why feature consistency matters:** if training uses one definition of \"user_7_day_click_count\" and serving uses another, the model behaves poorly. This is called **training-serving skew**.

**Embeddings:** dense numeric vectors representing users/items.
```
  item_embedding(shoe)  = [0.12, -0.44, 0.09, ...]
  user_embedding(U123)  = [0.10, -0.40, 0.15, ...]

  Similar vectors mean similar preference/item meaning.
```

**Vector DB / ANN search:** exact nearest-neighbor search over 100M vectors is too expensive. Use Approximate Nearest Neighbor indexes (HNSW, IVF, PQ) in systems like FAISS, Milvus, Pinecone, Elasticsearch/OpenSearch vector search.

```
  User embedding -> ANN index -> top 1000 nearest item vectors -> candidates
```

This is how modern recommenders quickly retrieve semantically relevant candidates.

---

## 5.12 Step 11: Online Serving Path

The online path must be extremely fast:

```
ONLINE RECOMMENDATION REQUEST

  1. Client -> Recommendation Service: GET /recommendations?userId=U123
  2. Check Redis cache for (userId, surface, context).
       HIT -> return immediately.
  3. MISS -> fetch candidates:
       - precomputed candidates from Candidate Store
       - vector candidates from ANN index
       - trending fallback
  4. Fetch online features from Feature Store.
  5. Rank candidates using loaded model.
  6. Apply filters/diversity/business rules.
  7. Store result in Redis with short TTL.
  8. Return top N + tracking tokens.
  9. Client later sends impression/click/purchase events.
```

```
LATENCY BUDGET (target p99 < 200 ms)

  Gateway/network             ~10-20 ms
  Redis cache hit             ~1-5 ms
  Candidate fetch             ~20-50 ms
  Feature fetch               ~10-30 ms
  Ranking model inference     ~10-50 ms
  Re-ranking/serialization    ~10-20 ms
  -------------------------------------
  Total on cache miss         ~60-170 ms
```

**Key design rule:** keep the ranking model small enough for real-time inference. Huge deep models can run offline to precompute candidates, while online ranking must be optimized and bounded.

---

## 5.13 Step 12: Feedback Loop, Evaluation & A/B Testing

Recommendation systems improve only if they learn from feedback.

```
FEEDBACK LOOP

  Recommend items -> user sees impressions -> user clicks/buys/skips
        ^                                             |
        |                                             v
  model deployment <--- model training <--- event pipeline / data lake
```

**Offline metrics:**
- Precision@K: how many of top K were relevant?
- Recall@K: how many relevant items did we retrieve?
- NDCG@K: ranking quality with position weighting.
- MAP/MRR: ranking quality for ordered lists.

**Online metrics (more important):**
- CTR (click-through rate)
- conversion rate
- revenue per session
- add-to-cart rate
- dwell time / engagement
- long-term retention

**A/B testing:** never ship a recommendation model just because offline metrics improved. Split traffic:
```
  90% users -> current model
  10% users -> new model
  Compare CTR, conversion, revenue, latency, complaints.
```

**Guardrail metrics:** watch for harm -- latency, error rate, diversity, return/refund rate, user complaints. A model can raise clicks while lowering long-term trust.

---

## 5.14 Step 13: Cold Start, Diversity & Exploration

**Cold-start problems:**
- **New user:** no history. Use location, device, onboarding preferences, trending/category popular items, contextual recommendations.
- **New item:** no interactions. Use content metadata (category, brand, text/image embeddings), seller/category boost, controlled exploration.

**Diversity and freshness:** recommending 20 nearly identical items feels bad. Apply re-ranking:
```
  - limit same category/brand repetition
  - mix familiar + novel items
  - include fresh/trending items
  - avoid repeatedly showing ignored items
```

**Exploration vs exploitation:**
- Exploitation: show items we are confident the user likes.
- Exploration: occasionally show uncertain/new items to learn preferences and avoid filter bubbles.

Common approach: **epsilon-greedy** or **multi-armed bandits** -- reserve a small percentage of slots for exploration while optimizing the rest.

---

## 5.15 Step 14: Scaling, Reliability & Latency

```
  Scaling technique             | Why it is used
  ------------------------------|--------------------------------------------
  Stateless serving tier         | horizontally scale recommendation service.
  Redis cache                    | cache per-user/surface recommendations.
  Precomputed candidates         | avoid expensive work in request path.
  ANN/vector index sharding      | scale similarity search across 100M items.
  Online feature store           | low-latency feature retrieval.
  Kafka event ingestion          | absorb billions of events/day.
  Batch + stream processing      | combine historical learning with real-time
                                | freshness.
  Circuit breakers/fallbacks     | if ML/vector service fails, return trending.
  Model versioning/registry      | safe rollout and rollback of models.
```

**Graceful degradation:**
```
  If ranking model unavailable -> use precomputed recommendations.
  If candidate store unavailable -> use trending/category popular items.
  If user features unavailable -> use non-personalized recommendations.
  If recommendation service down -> the page still loads without that module.
```

Recommendation quality may degrade, but the product should not be down. This is a classic example of making a non-critical dependency optional.

---

## 5.16 Step 15: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Relevance vs latency             | Bigger models may improve quality but miss
                                  | the 200 ms serving SLA.
  Personalization vs privacy       | More data improves recommendations, but user
                                  | consent, retention limits, and anonymization
                                  | matter.
  Exploration vs exploitation      | Too much exploitation creates filter bubbles;
                                  | too much exploration hurts immediate CTR.
  Offline metrics vs real impact   | Offline accuracy can improve while online
                                  | conversion drops. A/B test everything.
  Popularity bias                  | Popular items get more exposure, making them
                                  | even more popular. Need exploration/fairness.
  Cold start                       | New users/items lack behavior; need content
                                  | features and trending fallbacks.
  Feedback loops                   | Model may reinforce its own previous choices.
                                  | Track impressions, not only clicks.
  Stale features                   | Old user preferences cause bad recs; combine
                                  | long-term and session features.
```

**When to keep it simple:** for a small product, start with **popular/trending + item-to-item similarity**. Add collaborative filtering, embeddings, and ML ranking only when you have enough data and enough traffic to measure improvements reliably.

---

## 5.17 Problem 5 Summary

- A recommendation system predicts what a user may want next; it is built around the loop **collect behavior -> learn candidates/features -> rank -> serve -> measure -> improve**.
- Use a **two-stage design**: candidate generation narrows millions of items to hundreds/thousands; ranking scores those candidates with user/item/context features.
- Real systems are **hybrid**: popularity, content-based, collaborative filtering, embeddings/vector search, business rules, and fallbacks.
- The architecture separates **offline/nearline ML pipelines** (Kafka, data lake, Spark/Flink, training, model registry) from the **online serving path** (Recommendation Service, Redis cache, online feature store, candidate store, vector DB).
- **Feature stores** prevent training-serving skew; **embeddings + ANN/vector search** enable fast semantic retrieval at large item scale.
- Online serving must be bounded by a latency budget (e.g., p99 < 200 ms), so use caching, precomputed candidates, sharded ANN indexes, and small optimized ranking models.
- Close the loop with **impression/click/purchase events**, offline metrics, and especially **A/B testing** with guardrails.
- Handle **cold start, diversity, exploration, popularity bias, and graceful degradation**. If recommender components fail, return trending items rather than breaking the product.
- Start simple; add ML complexity only when you have enough data, traffic, and measurement discipline to prove it helps.

---

# Problem 6: Database CPU is at 100% — How Do You Troubleshoot?

## 6.1 Problem Statement

> Your production database is running at 100% CPU. The application is slow, users are complaining, and you have 15 minutes to stabilize the system before it crashes. Walk through your troubleshooting approach: how do you identify the root cause, apply immediate fixes, and prevent recurrence?

This is a **practical, real-world scenario** that tests your ability to:
- Stay calm and methodical under pressure.
- Use database tools and metrics effectively.
- Distinguish between symptoms and root causes.
- Apply quick wins vs. long-term solutions.
- Communicate clearly with the team.

---

## 6.2 Step 1: Clarifying Questions & Context

Before diving into diagnostics, gather context:

```
  Question                              | Why it matters
  --------------------------------------|--------------------------------------
  Which database? (MySQL, Postgres,     | Different tools and metrics.
  Oracle, MongoDB, etc.)                |
  What changed recently?                | Deploy, config change, data growth,
                                        | traffic spike, or unexpected query?
  How long has it been at 100%?         | Is it sustained or spiking?
  Are there errors in logs?             | Timeouts, deadlocks, OOM?
  Is it read-heavy or write-heavy?      | Affects diagnosis approach.
  Do you have query logs enabled?       | Critical for identifying culprits.
  Is there a replica or standby?        | Can you failover to buy time?
  What is the current connection count? | Connection exhaustion?
```

**Assumption for this walkthrough:** MySQL/Postgres, query logs enabled, no immediate failover available.

---

## 6.3 Step 2: Immediate Diagnostics (First 5 Minutes)

**Goal:** get a snapshot of what is consuming CPU.

```
MYSQL / POSTGRES QUICK CHECKS

1. Check overall CPU and memory:
   $ top -b -n 1 | grep mysqld
   $ ps aux | grep postgres

2. Check active connections:
   MySQL:    SHOW PROCESSLIST;
   Postgres: SELECT * FROM pg_stat_activity WHERE state = 'active';

3. Check slow query log (if enabled):
   MySQL:    SHOW VARIABLES LIKE 'slow_query_log';
             SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;
   Postgres: SELECT query, calls, total_time FROM pg_stat_statements
             ORDER BY total_time DESC LIMIT 10;

4. Check for locks/waits:
   MySQL:    SHOW ENGINE INNODB STATUS;
   Postgres: SELECT * FROM pg_locks WHERE NOT granted;

5. Check disk I/O:
   $ iostat -x 1
   Look for high %util, high await times.

6. Check table/index sizes:
   MySQL:    SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
             FROM information_schema.tables WHERE table_schema = 'your_db'
             ORDER BY size_mb DESC;
```

**Output:** you should see one or more of:
- A specific query running repeatedly with high CPU.
- Many connections in \"Sending data\" or \"Copying to tmp table\" state.
- High disk I/O (full table scans).
- Lock contention (many waiting queries).

---

## 6.4 Step 3: Identify the Culprit Query

Once you spot a hot query, get its details:

```
MYSQL:
  SELECT * FROM INFORMATION_SCHEMA.PROCESSLIST
  WHERE ID = <connection_id>;

  EXPLAIN <query>;
  EXPLAIN FORMAT=JSON <query>;

POSTGRES:
  SELECT query, calls, total_time, mean_time FROM pg_stat_statements
  WHERE query LIKE '%<pattern>%'
  ORDER BY total_time DESC;

  EXPLAIN (ANALYZE, BUFFERS) <query>;
```

**Red flags in EXPLAIN output:**
- Full table scan (type = ALL)
- No index used (key = NULL)
- Temporary table creation (Extra = \"Using temporary\")
- File sort (Extra = \"Using filesort\")
- High rows examined vs. rows returned ratio

**Example culprit:**
```
SELECT * FROM orders
WHERE customer_id = 123 AND created_at > '2026-01-01'
ORDER BY created_at DESC;

EXPLAIN shows:
  type: ALL (full table scan)
  rows: 50,000,000 (scanning entire table)
  Extra: Using filesort (sorting in memory/disk)
```

This query is scanning 50M rows and sorting them — CPU killer.

---

## 6.5 Step 4: Understand the Root Cause

Common root causes of high CPU:

```
  Root cause                    | Symptoms / diagnosis
  ------------------------------|----------------------------------------------
  Missing index                 | Full table scan, high rows examined.
  Bad query plan                | Index exists but optimizer chose poorly.
  Inefficient join              | Cartesian product or nested loop on large
                                | tables.
  Sorting/grouping large sets   | Using filesort / temporary table.
  Subqueries in WHERE/SELECT    | Executed for every row.
  N+1 queries                   | Application fetches one row, then queries
                                | for each row.
  Sudden data growth            | Old query now scans 10x more rows.
  Connection exhaustion         | Too many idle/waiting connections.
  Lock contention               | Queries waiting for locks, CPU spinning.
  Unoptimized regex/LIKE        | LIKE '%pattern%' on large text columns.
  Materialized view stale       | Recomputing expensive aggregates.
```

**In the example above:** missing index on `(customer_id, created_at)`.

---

## 6.6 Step 5: Quick Wins (Immediate Mitigation)

While you plan a permanent fix, buy time:

```
  Quick win                                  | How to apply
  -------------------------------------------|-------------------------------------
  Kill the culprit query                     | MySQL: KILL <connection_id>;
                                             | Postgres: SELECT pg_terminate_backend(<pid>);
  Increase query timeout                     | SET SESSION max_execution_time = 5000;
  Reduce result set (LIMIT)                  | Add LIMIT to queries if safe.
  Disable slow query logging (if it's        | SET GLOBAL slow_query_log = OFF;
  causing overhead)                          |
  Increase buffer pool / work_mem            | SET GLOBAL innodb_buffer_pool_size = ...;
                                             | (requires restart for MySQL)
  Scale horizontally (read replicas)         | Route read traffic to replica.
  Reduce concurrent connections              | Close idle connections, rate-limit.
  Restart database (last resort)             | Clears caches, resets state, buys time
                                             | but loses in-flight transactions.
```

**For the example:** kill the runaway query, then add the index.

---

## 6.7 Step 6: Deep Dive: Query Optimization

Once immediate pressure is relieved, optimize the query:

```
BEFORE:
  SELECT * FROM orders
  WHERE customer_id = 123 AND created_at > '2026-01-01'
  ORDER BY created_at DESC;

AFTER (add index):
  CREATE INDEX idx_orders_customer_created
  ON orders(customer_id, created_at DESC);

  SELECT id, customer_id, created_at, amount
  FROM orders
  WHERE customer_id = 123 AND created_at > '2026-01-01'
  ORDER BY created_at DESC;
```

**Optimization techniques:**

```
  Technique                           | Example
  ------------------------------------|----------------------------------------
  Add covering index                  | Index includes all columns in SELECT.
  Use composite index                 | Index on (col1, col2) for WHERE + ORDER BY.
  Rewrite subquery as JOIN            | Subqueries are often re-executed per row.
  Partition large table               | Partition by date, customer_id, etc.
  Denormalize (carefully)             | Store computed values to avoid joins.
  Rewrite LIKE '%x%' as LIKE 'x%'     | Prefix search can use index.
  Use UNION instead of OR             | Can use different indexes per branch.
  Aggregate in application            | Move GROUP BY from DB to app if possible.
  Batch operations                    | Instead of 1000 single inserts, do bulk.
```

---

## 6.8 Step 7: Index Strategy

Indexes are the most common fix for CPU-heavy queries, but they have trade-offs:

```
  Index type              | Use case                        | Cost
  ------------------------|--------------------------------|------------------
  B-tree (default)        | Range queries, sorting,         | Slower writes,
                          | equality lookups.               | storage.
  Hash                    | Exact match only (no range).    | Cannot sort.
  Full-text              | Text search (LIKE, MATCH).      | Slower writes.
  Bitmap                 | Low-cardinality columns.        | Not all DBs.
  Partial/conditional    | Index only rows matching        | Complex to
                          | a WHERE clause.                 | maintain.
```

**Index design rules:**

```
  1. Index columns used in WHERE, JOIN, ORDER BY.
  2. Put most selective columns first (customer_id before status).
  3. Keep indexes narrow (fewer columns = faster).
  4. Avoid redundant indexes (if (a, b) exists, (a) is redundant).
  5. Monitor index usage; drop unused indexes.
  6. Rebuilding indexes can reclaim space and improve performance.
```

**Check index effectiveness:**

```
MYSQL:
  SELECT * FROM sys.schema_unused_indexes;
  SELECT * FROM sys.statements_with_full_table_scans;

POSTGRES:
  SELECT schemaname, tablename, indexname, idx_scan
  FROM pg_stat_user_indexes
  ORDER BY idx_scan ASC;
```

---

## 6.9 Step 8: Connection Pool & Resource Limits

High CPU can also be caused by connection exhaustion or resource contention:

```
  Problem                              | Solution
  -------------------------------------|-----------------------------------
  Too many connections                 | Reduce max_connections or use
                                       | connection pooling (PgBouncer,
                                       | ProxySQL, HikariCP).
  Idle connections consuming memory    | Set idle_timeout to close stale
                                       | connections.
  Connection pool misconfigured        | Tune pool size: typically
                                       | (cores * 2) + spare.
  Threads per query too high           | Reduce max_parallel_workers_per_query.
  Memory per query too high            | Reduce work_mem (Postgres) or
                                       | sort_buffer_size (MySQL).
```

**Example connection pool config (HikariCP / Java):**
```
maximumPoolSize = (CPU cores * 2) + 5
minimumIdle = maximumPoolSize / 2
idleTimeout = 10 minutes
maxLifetime = 30 minutes
```

---

## 6.10 Step 9: Caching & Query Reduction

Reduce database load by caching results:

```
  Caching strategy                    | Implementation
  ------------------------------------|-----------------------------------
  Query result caching                | Redis, Memcached.
  Application-level caching           | In-process cache (Caffeine, Guava).
  Database query cache (deprecated)   | MySQL query cache (removed in 8.0).
  Materialized views                  | Pre-compute expensive aggregates.
  Read replicas + caching             | Cache on replicas, serve reads from
                                       | cache.
```

**Example: cache hot queries in Redis**
```
GET user:123:orders -> MISS
  SELECT * FROM orders WHERE customer_id = 123
  SET user:123:orders <result> EX 3600
  RETURN <result>

GET user:123:orders -> HIT (next time)
  RETURN <cached_result>
```

---

## 6.11 Step 10: Scaling & Sharding

If optimization and caching are not enough, scale:

```
  Scaling approach                    | When to use
  ------------------------------------|-----------------------------------
  Read replicas                       | Read-heavy workload; replicate and
                                       | route reads to replicas.
  Sharding (horizontal partitioning)  | Data too large for single machine;
                                       | shard by customer_id, user_id, etc.
  Vertical scaling                    | Buy bigger hardware (CPU, RAM, SSD).
  Caching layer (Redis cluster)       | Reduce DB load with distributed cache.
  Write-ahead log (WAL) tuning        | Optimize durability vs. performance.
```

**Sharding example:**
```
  Shard 1: customer_id % 10 == 0..2
  Shard 2: customer_id % 10 == 3..5
  Shard 3: customer_id % 10 == 6..9

  Query for customer_id = 123:
    shard = 123 % 10 = 3 -> Shard 2
```

---

## 6.12 Step 11: Monitoring & Alerting

Prevent future incidents with proactive monitoring:

```
  Metric                              | Alert threshold
  ------------------------------------|-----------------------------------
  Database CPU                        | > 80% for > 5 minutes
  Query execution time (p99)          | > 1 second
  Slow query rate                     | > 10 per second
  Connection count                    | > 80% of max
  Disk I/O utilization                | > 80%
  Replication lag (if replica)        | > 10 seconds
  Index fragmentation                 | > 30%
  Table bloat / dead rows             | > 20%
```

**Monitoring tools:**
- MySQL: Percona Monitoring and Management (PMM), Datadog, New Relic.
- Postgres: pgAdmin, pg_stat_statements, Datadog, New Relic.
- Generic: Prometheus + Grafana, CloudWatch, Datadog.

**Set up alerts:**
```
IF database_cpu > 80% FOR 5 minutes
  THEN page on-call engineer
```

---

## 6.13 Step 12: Post-Incident Review & Prevention

After the incident is resolved:

```
  Action                              | Purpose
  ------------------------------------|-----------------------------------
  Root cause analysis (RCA)           | Understand what happened and why.
  Identify systemic issues            | Was this preventable?
  Implement permanent fix             | Index, query rewrite, caching, etc.
  Add monitoring/alerting             | Catch similar issues earlier.
  Update runbooks                     | Document troubleshooting steps.
  Load test                           | Ensure fix handles peak load.
  Blameless postmortem                | Learn without blame; improve process.
```

**RCA template:**
```
  Timeline:
    14:00 - CPU spike to 100%
    14:05 - On-call paged
    14:10 - Identified culprit query
    14:15 - Killed query, CPU dropped
    14:30 - Added index
    14:45 - Verified fix, monitoring in place

  Root cause:
    Missing index on (customer_id, created_at) caused full table scan
    of 50M rows. Recent data growth (10x) made the problem visible.

  Why not caught earlier:
    - No slow query logging enabled
    - No monitoring on query execution time
    - No load testing before production

  Permanent fixes:
    1. Add index on (customer_id, created_at)
    2. Enable slow query logging (threshold 1 second)
    3. Add Datadog monitoring for query latency
    4. Quarterly load testing

  Prevention:
    - Code review checklist: verify indexes for new queries
    - Automated slow query detection in CI/CD
    - Capacity planning: monitor data growth trends
```

---

## 6.14 Problem 6 Summary

- **Stay methodical under pressure:** clarify context, gather diagnostics, identify the culprit, understand the root cause, apply quick wins, then plan permanent fixes.
- **Use database tools effectively:** PROCESSLIST, EXPLAIN, slow query logs, pg_stat_statements, iostat, top. Know your database's diagnostic commands.
- **Distinguish symptoms from causes:** high CPU is a symptom; missing index, bad query plan, lock contention, or connection exhaustion are root causes.
- **Quick wins buy time:** kill runaway queries, increase timeouts, route to replicas, reduce connections. These are temporary; plan permanent fixes in parallel.
- **Query optimization is the most common fix:** add indexes on WHERE/JOIN/ORDER BY columns, rewrite subqueries as JOINs, use covering indexes, partition large tables.
- **Indexes have trade-offs:** faster reads, slower writes, storage cost. Monitor index usage; drop unused indexes.
- **Connection pooling and resource limits matter:** tune pool size, idle timeout, max connections, work_mem. Misconfiguration causes CPU spikes.
- **Caching reduces load:** Redis, Memcached, materialized views, application-level caches. Cache hot queries and aggregate results.
- **Scale when optimization is insufficient:** read replicas for read-heavy workloads, sharding for large data, vertical scaling for CPU/memory.
- **Monitor and alert proactively:** CPU, query latency, slow query rate, connection count, disk I/O, replication lag. Catch issues before users notice.
- **Post-incident review prevents recurrence:** RCA, systemic improvements, runbook updates, load testing, blameless postmortem.

---

# Problem 7: Design a Search Engine

## 7.1 Problem Statement

> Design a web-scale search engine that crawls billions of pages on the internet, indexes their content, and returns the most relevant results to a user query in under one second.

This is one of the classic system design questions. It tests whether you understand:
- **Distributed crawling** at massive scale.
- **Inverted indexes** and how they turn unstructured text into fast lookups.
- **Ranking algorithms** (TF-IDF, BM25, PageRank) and why relevance is hard.
- **Low-latency query serving** over petabytes of indexed data.
- Trade-offs between **freshness, completeness, relevance, and cost**.

Note: Problem 1 in this workbook covers a **search REST API** over a single database. This problem covers the **full search engine pipeline** -- crawling the open web, building the index, ranking, and serving.

---

## 7.2 Step 1: Clarifying Questions & Scope

```
  Question                                  | Assumption we proceed with
  ------------------------------------------|----------------------------------
  What are we searching?                    | Public web pages (HTML).
  Scale of the web?                         | ~5 billion indexable pages.
  Query volume?                             | ~100,000 queries per second (QPS).
  Latency target?                           | p99 < 500 ms for query results.
  Do we need images/video/news?             | Text search only (core problem).
  Personalization?                          | Not in scope; focus on relevance.
  Ads?                                      | Not in scope.
  Autocomplete / spell check?              | Yes, basic query understanding.
  How fresh must results be?               | Most pages re-crawled within days
                                            | to weeks; breaking news within hours.
  Geographic scope?                        | Global, multi-datacenter.
```

**Scoping decision:** a search engine has two very different halves:
- **Offline pipeline:** crawl -> parse -> index -> rank (heavy batch/stream processing).
- **Online serving:** receive query -> look up index -> rank results -> return (low-latency).

Both must be designed, but they scale differently.

---

## 7.3 Step 2: Requirements (Functional & Non-Functional)

**Functional requirements:**
- Accept a text query and return a ranked list of web page results (title, snippet, URL).
- Support pagination (page 1, page 2, ...).
- Provide spell correction and query suggestions.
- Crawl and index billions of web pages continuously.
- Keep the index reasonably fresh (days to weeks for most pages; hours for popular/news pages).

**Non-functional requirements:**
- **Low latency:** p99 < 500 ms for query serving.
- **High availability:** search must be available 99.99% of the time.
- **Scalability:** handle 100K+ QPS and billions of indexed documents.
- **Relevance:** return the most useful results, not just any matching results.
- **Freshness:** new/updated content should appear in results within a reasonable time.
- **Fault tolerance:** crawler and indexer failures should not affect serving.

---

## 7.4 Step 3: Capacity Estimation

```
  Web scale:
    ~5 billion pages to crawl and index.
    Average page size: ~100 KB (HTML + text).
    Raw crawl data: 5B x 100 KB = ~500 TB.
    After extraction/compression: ~50-100 TB of clean text + metadata.

  Index size:
    Inverted index is typically 20-30% of raw text size.
    ~10-30 TB for the inverted index.
    With replicas (3x) -> ~30-90 TB distributed across index servers.

  Crawling:
    To recrawl 5B pages every 2 weeks:
      5B / (14 days x 86,400 sec) ~ 4,100 pages/sec average.
    Peak (with parallelism): ~50,000-100,000 fetches/sec across all crawlers.

  Query serving:
    100,000 QPS.
    Each query touches a few hundred index shards (fan-out).
    Each shard returns top results in <50 ms.

  Storage:
    Raw HTML store: ~500 TB (compressed, in object storage).
    Inverted index: ~30-90 TB (across shards, SSDs).
    URL database: 5B URLs x 500 bytes = ~2.5 TB.
    PageRank/link graph: 5B nodes, ~100B edges -> ~1-5 TB.
```

**Conclusion:** this is a **storage and throughput** problem. The index must be sharded across thousands of machines; the crawler must be massively distributed; query serving requires fan-out, caching, and replication.

---

## 7.5 Step 4: High-Level Architecture

```
SEARCH ENGINE ARCHITECTURE

  OFFLINE PIPELINE (crawl -> index)
  =================================

  +-------------+      +----------------+      +---------------------+
  | Seed URLs   |----->| URL Frontier   |----->| Distributed Crawler |
  +-------------+      | (priority queue)|      | (1000s of workers)  |
                        +----------------+      +----------+----------+
                              ^                            |
                              |                            v
                        +-----+--------+          +------------------+
                        | URL Filter   |          | Raw HTML Store   |
                        | (dedup,      |<---------| (S3 / HDFS)      |
                        |  robots.txt) |          +--------+---------+
                        +--------------+                   |
                                                           v
                                                  +------------------+
                                                  | Document         |
                                                  | Processor        |
                                                  | (parse, extract, |
                                                  |  normalize)      |
                                                  +--------+---------+
                                                           |
                                                           v
                                                  +------------------+
                                                  | Indexer           |
                                                  | (build inverted   |
                                                  |  index segments)  |
                                                  +--------+---------+
                                                           |
                                                           v
                                                  +------------------+
                                                  | Index Segments   |
                                                  | (sharded, on SSD)|
                                                  +------------------+

          +------------------+
          | Link Graph       |------> PageRank computation (batch)
          | (adjacency list) |
          +------------------+


  ONLINE SERVING (query -> results)
  =================================

          +------------------+
          | User / Browser   |
          +--------+---------+
                   |
                   v
          +------------------+
          |   API Gateway /  |
          |   Load Balancer  |
          +--------+---------+
                   |
                   v
          +------------------+
          | Query Service    |  parse query, spell check, expand
          +--------+---------+
                   |
                   v
          +------------------+
          | Index Router /   |  fan-out query to index shards
          | Scatter-Gather   |
          +--------+---------+
                   |
          +--------+--------+--------+
          |        |        |        |
          v        v        v        v
       +------+ +------+ +------+ +------+
       |Shard | |Shard | |Shard | |Shard |  (1000s of shards)
       |  1   | |  2   | |  3   | |  N   |
       +------+ +------+ +------+ +------+
          |        |        |        |
          +--------+--------+--------+
                   |
                   v
          +------------------+
          | Result Merger /  |  merge, re-rank, deduplicate
          | Ranker           |
          +--------+---------+
                   |
                   v
          +------------------+
          | Snippet Generator|  extract relevant snippet from doc
          +--------+---------+
                   |
                   v
          +------------------+
          | Cache (Redis)    |  cache popular query results
          +------------------+
                   |
                   v
          +------------------+
          | Response to user |  title, URL, snippet, page links
          +------------------+
```

---

## 7.6 Step 5: Web Crawler

The crawler discovers and downloads web pages. It is the data source for everything else.

```
CRAWLER ARCHITECTURE

  URL Frontier -> Pick URL -> DNS resolve -> Fetch (HTTP GET)
       ^                                          |
       |                                          v
       +--- extract links from page ------- Parse HTML
                                                  |
                                                  v
                                            Store raw HTML
                                            in blob storage
```

**Key crawler components:**

```
  Component                | Responsibility
  -------------------------|------------------------------------------------
  URL Frontier             | Priority queue of URLs to crawl next.
  DNS Resolver (cached)    | Resolve hostnames; cache aggressively to avoid
                           | hammering DNS servers.
  HTTP Fetcher             | Download pages with timeouts, retries, and
                           | redirect following.
  Robots.txt Parser        | Respect crawl rules per domain.
  HTML Parser              | Extract text, links, metadata from raw HTML.
  URL Deduplication        | Avoid crawling the same URL twice (Bloom filter
                           | or hash set).
  Content Deduplication    | Detect near-duplicate pages (SimHash, MinHash).
```

**Distributed crawling:** run thousands of crawler workers, each pulling URLs from the frontier. Partition the frontier by domain so one worker handles one domain at a time (avoids hammering a single site).

---

## 7.7 Step 6: URL Frontier & Politeness

The URL Frontier decides **what to crawl next** and **how fast**.

```
URL FRONTIER

  +-------------------+
  | Priority Queues   |  high-priority: news sites, popular domains
  | (by importance)   |  low-priority: deep links, old pages
  +--------+----------+
           |
           v
  +-------------------+
  | Politeness Queues |  one queue per domain; rate-limited
  | (by domain)       |  respect robots.txt crawl-delay
  +--------+----------+
           |
           v
  +-------------------+
  | Worker picks URL  |  fetch, parse, extract links, enqueue new URLs
  +-------------------+
```

**Politeness rules:**
- **robots.txt:** always obey. If disallowed, do not crawl.
- **Crawl delay:** wait N seconds between requests to the same domain.
- **Rate limiting:** cap requests per domain per minute.
- **User-Agent:** identify your crawler so site owners can contact you.

**Priority factors:**
- PageRank / domain authority (important pages first).
- Freshness need (news sites crawled more often).
- Change frequency (pages that change often get re-crawled sooner).

---

## 7.8 Step 7: Document Processing & Content Extraction

After fetching raw HTML, the document processor extracts useful content:

```
RAW HTML -> Document Processor -> Clean document record

  Steps:
  1. Strip HTML tags, scripts, styles, ads, navigation.
  2. Extract title, headings, body text, meta description.
  3. Detect language.
  4. Normalize text: lowercase, remove punctuation, stem/lemmatize.
  5. Tokenize into terms (words).
  6. Extract outgoing links (for link graph / PageRank).
  7. Detect duplicates (SimHash fingerprint).
  8. Store clean document record with metadata.
```

**Output document record:**
```
  {
    "docId": "d_123456",
    "url": "https://example.com/article/42",
    "title": "How Search Engines Work",
    "text": "A search engine crawls the web...",
    "tokens": ["search", "engine", "crawl", "web", ...],
    "outLinks": ["https://example.com/page2", ...],
    "language": "en",
    "fetchTime": "2026-05-31T12:00:00Z",
    "simHash": "0xABCD1234"
  }
```

---

## 7.9 Step 8: Inverted Index (Heart of the Search Engine)

The **inverted index** is the core data structure. It maps every **term** to the list of **documents** that contain it.

```
FORWARD INDEX (what we have after parsing):
  doc1 -> ["search", "engine", "web", "crawl"]
  doc2 -> ["search", "query", "ranking"]
  doc3 -> ["web", "crawl", "spider"]

INVERTED INDEX (what we build):
  "search"  -> [doc1, doc2]
  "engine"  -> [doc1]
  "web"     -> [doc1, doc3]
  "crawl"   -> [doc1, doc3]
  "query"   -> [doc2]
  "ranking" -> [doc2]
  "spider"  -> [doc3]
```

Each entry in the inverted index is called a **posting list**. A posting contains:
```
  term -> [(docId, termFrequency, positions), ...]

  Example:
  "search" -> [(doc1, tf=3, positions=[5, 42, 101]),
                (doc2, tf=1, positions=[8])]
```

**Why it is fast:** to find documents matching "search engine", intersect posting lists for "search" and "engine":
```
  "search"  -> [doc1, doc2]
  "engine"  -> [doc1]
  Intersection: [doc1]
```

This is O(n + m) where n and m are posting list lengths -- much faster than scanning billions of documents.

**Index storage:**
- Posting lists are **sorted by docId** for fast intersection (merge join).
- Compressed using **variable-byte encoding**, **PForDelta**, or **Roaring bitmaps** to reduce size.
- Stored in **immutable segments** on SSDs (like Lucene/Elasticsearch segments).
- **Sharded** across thousands of machines by term range or document range.

---

## 7.10 Step 9: Ranking & Relevance (TF-IDF, BM25, PageRank)

Finding matching documents is easy. **Ranking them by relevance** is the hard part.

**TF-IDF (Term Frequency - Inverse Document Frequency):**
```
  TF(t, d)  = (number of times term t appears in document d) / (total terms in d)
  IDF(t)    = log(N / df(t))
              where N = total documents, df(t) = documents containing t

  TF-IDF(t, d) = TF(t, d) x IDF(t)

  Intuition:
    - A term appearing many times in a document is important for that document (TF).
    - A term appearing in few documents is more discriminating (IDF).
    - "the" has high TF but very low IDF (appears everywhere).
    - "kubernetes" has moderate TF but high IDF (rare term, very informative).
```

**BM25 (Best Matching 25):** the industry-standard improvement over TF-IDF:
```
  BM25(t, d) = IDF(t) x [ (TF(t,d) x (k1 + 1)) / (TF(t,d) + k1 x (1 - b + b x |d|/avgdl)) ]

  where:
    k1 = 1.2 (term frequency saturation)
    b  = 0.75 (document length normalization)
    |d| = document length, avgdl = average document length

  Key improvement over TF-IDF:
    - TF saturation: diminishing returns after a term appears many times.
    - Length normalization: longer documents do not unfairly dominate.
```

**PageRank:** a query-independent score based on the **link graph**:
```
  Core idea: a page is important if many important pages link to it.

  PageRank(A) = (1 - d) + d x SUM( PageRank(T) / outlinks(T) )
                for all pages T that link to A

  where d = 0.85 (damping factor)

  Computed offline as a batch job over the entire link graph.
  Used as a static quality signal combined with BM25.
```

**Combined ranking score:**
```
  finalScore(query, doc) = w1 x BM25(query, doc)
                         + w2 x PageRank(doc)
                         + w3 x freshness(doc)
                         + w4 x domain_authority(doc)
                         + w5 x click_through_rate(query, doc)
                         - penalty(spam, thin_content, duplicates)
```

Modern search engines also use **machine-learned ranking** (Learning to Rank / LTR) where hundreds of features are combined by a trained model (gradient-boosted trees, neural rankers) to produce the final score.

---

## 7.11 Step 10: Query Processing & Serving

When a user types a query, this is the online path:

```
QUERY PROCESSING PIPELINE

  1. Receive query "how do search engnes work"
  2. Spell correction -> "how do search engines work"
  3. Tokenize -> ["how", "do", "search", "engines", "work"]
  4. Remove stop words -> ["search", "engines", "work"]
  5. Stem/lemmatize -> ["search", "engine", "work"]
  6. Query expansion (synonyms) -> add "find", "motor"
  7. Fan-out to index shards (scatter)
  8. Each shard:
       - look up posting lists for each term
       - intersect / union depending on AND/OR semantics
       - score matching docs using BM25 + PageRank
       - return top K results
  9. Merge results from all shards (gather)
  10. Re-rank merged results (global ranking pass)
  11. Generate snippets for top results
  12. Return response
```

**Scatter-gather pattern:**
```
  Query Service
       |
       +---> Shard 1: top 10 results for "search engine work"
       +---> Shard 2: top 10 results
       +---> Shard 3: top 10 results
       +---> ...
       +---> Shard N: top 10 results
       |
       v
  Merge top 10 globally from all shard results
```

Each shard holds a partition of the full index. Two sharding strategies:
- **Document-partitioned:** each shard holds a subset of documents (all terms for those docs). Query goes to all shards.
- **Term-partitioned:** each shard holds a subset of terms. Query goes only to relevant shards. Harder to balance.

Most real search engines use **document-partitioned** sharding because it is simpler to balance and each shard can independently score documents.

---

## 7.12 Step 11: Spell Correction, Suggestions & Query Understanding

**Spell correction:**
```
  User types: "seach engne"
  System suggests: "search engine"

  Techniques:
  1. Edit distance (Levenshtein): find dictionary words within 1-2 edits.
  2. N-gram overlap: break words into character n-grams, match candidates.
  3. Noisy channel model: P(correction | misspelling) x P(correction).
  4. Query log mining: "seach" was corrected to "search" by 95% of users.
```

**Autocomplete / query suggestions:**
```
  User types: "how do sea"
  System suggests: "how do search engines work"
                   "how do sea turtles breathe"

  Backed by:
  - Trie / prefix tree of popular queries.
  - Weighted by query frequency and recency.
  - Served from a fast in-memory store.
```

**Query understanding:**
```
  "apple" -> is it the fruit or the company?
  Techniques:
  - Entity recognition (knowledge graph lookup).
  - Query classification (category: tech, food, etc.).
  - Intent detection (informational, navigational, transactional).
  - Context from user location, language, recent searches.
```

---

## 7.13 Step 12: Caching, Sharding & Replication

**Caching:**
```
  Layer                     | What is cached                  | TTL
  --------------------------|---------------------------------|----------
  Query result cache        | Full result page for popular    | 1-5 min
                            | queries (Redis / Memcached).    |
  Posting list cache        | Frequently accessed posting     | Minutes
                            | lists kept in memory.           |
  Snippet cache             | Pre-generated snippets for      | Hours
                            | top results.                    |
  DNS cache                 | Resolved hostnames for crawler. | Hours
  Autocomplete cache        | Trie of popular query prefixes. | Minutes
```

Query result caching is extremely effective because query distributions follow a **power law**: a small fraction of queries account for a large fraction of traffic.

**Sharding the index:**
```
  5 billion documents / 500,000 docs per shard = ~10,000 shards.

  Each shard:
    - Holds ~500K documents and their posting lists.
    - Fits on a single machine (SSD + RAM for hot posting lists).
    - Independently scores and returns top K results.

  Query fan-out:
    - Query goes to all 10,000 shards (or a subset if tiered).
    - Each shard returns top 10-20 results.
    - Merger selects global top 10.
```

**Replication:**
```
  Each shard is replicated 3x for:
    - Availability: if one replica fails, others serve.
    - Load distribution: spread query load across replicas.
    - Geographic distribution: replicas in different datacenters.

  Total machines for index serving:
    10,000 shards x 3 replicas = 30,000 index servers.
```

---

## 7.14 Step 13: Freshness, Re-crawling & Incremental Indexing

The web changes constantly. A search engine must balance **freshness** with **cost**.

**Re-crawl strategy:**
```
  Page category             | Re-crawl frequency
  --------------------------|---------------------------------
  News / trending pages     | Minutes to hours.
  Popular / high-PageRank   | Daily to weekly.
  Average pages             | Weekly to monthly.
  Deep / rarely changing    | Monthly or less.
```

**How to decide re-crawl priority:**
- **Change detection:** track Last-Modified / ETag headers; use content hashes to detect actual changes.
- **Historical change rate:** pages that change often are re-crawled more often.
- **Importance:** high-PageRank pages are prioritized.
- **Sitemap.xml:** site owners publish change frequency hints.

**Incremental indexing:**
```
  Batch index rebuild (full re-index):
    - Rebuild the entire inverted index from scratch periodically.
    - Expensive but produces a clean, optimized index.

  Incremental updates:
    - New/updated pages are added to small "delta" index segments.
    - Query serving merges results from the main index + delta segments.
    - Periodically, delta segments are merged into the main index.
    - Similar to how Lucene/Elasticsearch segment merging works.

  MAIN INDEX (large, optimized)  +  DELTA INDEX (small, recent)
       |                                    |
       +-------- merge results at query time --------+
```

---

## 7.15 Step 14: Scaling, Reliability & Latency

```
  Challenge                       | Solution
  --------------------------------|--------------------------------------------
  100K QPS query serving          | 10,000+ shards x 3 replicas, load-balanced.
  5B pages to crawl               | 1000s of crawler workers, partitioned by
                                  | domain.
  50+ TB inverted index           | Sharded across SSDs; hot posting lists
                                  | cached in RAM.
  Sub-second query latency        | Scatter-gather with aggressive timeouts;
                                  | cache popular queries; tiered index
                                  | (serve top tier first, deeper tiers only
                                  | if needed).
  Crawler failures                | Retry with backoff; re-enqueue failed URLs.
  Index server failures           | Replicas serve queries; failed shard is
                                  | rebuilt from replicated data.
  Stale results                   | Incremental indexing + delta segments.
  Spam / SEO manipulation         | Spam classifiers, link-graph analysis,
                                  | manual penalties.
```

**Tiered index (latency optimization):**
```
  Tier 1: Top 100M most important documents (fast, in-memory index).
  Tier 2: Next 1B documents (SSD-based index).
  Tier 3: Remaining 4B documents (deeper storage, slower).

  Most queries are answered by Tier 1 alone.
  Only if Tier 1 has insufficient results, fan out to Tier 2/3.
  This dramatically reduces average latency and resource usage.
```

**Graceful degradation:**
```
  If some shards are slow -> return results from responding shards
                             (partial results are better than timeout).
  If ranking model unavailable -> fall back to BM25 + PageRank.
  If spell correction unavailable -> serve raw query results.
  If autocomplete unavailable -> user types full query manually.
```

---

## 7.16 Step 15: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Freshness vs cost                | Crawling more often improves freshness but
                                   | costs bandwidth, storage, and compute. Use
                                   | priority-based re-crawl.
  Completeness vs relevance        | Indexing every page on the web includes junk.
                                   | Quality filters and spam detection are
                                   | essential.
  Index size vs query latency      | Larger index means more shards, more fan-out,
                                   | higher tail latency. Use tiered index.
  Precision vs recall              | Strict AND semantics give precision but miss
                                   | relevant results; OR gives recall but noisy
                                   | results. Use AND with fallback to OR.
  PageRank manipulation            | Link farms and SEO spam try to game rankings.
                                   | Need spam classifiers and link-graph analysis.
  Crawl politeness vs coverage     | Respecting robots.txt and rate limits slows
                                   | crawling. But violating them gets you blocked.
  Centralized vs distributed       | Centralized URL frontier is simpler but a
    URL frontier                   | bottleneck. Distributed frontier is harder
                                   | to deduplicate but scales.
  Document-partitioned vs          | Document-partitioned is simpler but every
    term-partitioned sharding      | query fans out to all shards. Term-partitioned
                                   | reduces fan-out but is harder to balance.
```

**When to keep it simple:** for a smaller-scale search (intranet, e-commerce catalog), you do not need a custom crawler or inverted index. Use **Elasticsearch / OpenSearch** which provides inverted indexing, BM25 scoring, sharding, and replication out of the box. Build a custom engine only at true web scale.

---

## 7.17 Problem 7 Summary

- A search engine has two halves: the **offline pipeline** (crawl -> parse -> index -> rank) and the **online serving path** (query -> lookup -> rank -> return).
- The **web crawler** is a massively distributed system with a **URL frontier** (priority + politeness queues), DNS caching, robots.txt compliance, and content/URL deduplication.
- **Document processing** strips HTML, extracts text/links, normalizes tokens, detects language, and fingerprints content for deduplication.
- The **inverted index** is the core data structure: it maps each term to a sorted, compressed posting list of documents. Lookups are fast set intersections, not full scans.
- **Ranking** combines **BM25** (term relevance), **PageRank** (link authority), freshness, domain quality, and increasingly **machine-learned ranking models** (LTR).
- **Query processing** includes spell correction, stop-word removal, stemming, query expansion, and intent understanding before hitting the index.
- Online serving uses the **scatter-gather** pattern: fan out the query to thousands of index shards, each returns top K, then merge and re-rank globally.
- **Caching** (query results, posting lists, snippets) is critical because query distributions follow a power law.
- The index is **sharded** across thousands of machines and **replicated 3x** for availability and load distribution. A **tiered index** serves most queries from the top tier alone.
- **Freshness** is maintained through priority-based re-crawling and **incremental/delta indexing** merged at query time.
- Trade-offs include freshness vs. cost, completeness vs. relevance, index size vs. latency, and crawl politeness vs. coverage.
- For smaller-scale search, use **Elasticsearch/OpenSearch** rather than building a custom engine. Custom engines are justified only at billions-of-pages scale.

---

# Problem 8: Your System is Handling 10x Traffic Overnight — What Do You Do?

## 8.1 Problem Statement

> You are paged at 2 AM. Your system, which normally handles 5,000 RPS, is now receiving 50,000 RPS. Latency is spiking, error rates are climbing, and users are complaining. This is not a DDoS attack — it is legitimate traffic (a viral event, a flash sale, a celebrity mention, a breaking news story). Walk through how you stabilize the system, scale it, and build resilience so this never catches you off guard again.

This question tests:
- **Incident response** under pressure — structured thinking, not panic.
- **Systems knowledge** — which components break first at 10x load and why.
- **Practical scaling** — what you can actually do in minutes vs. hours vs. days.
- **Architectural foresight** — designing for elasticity before the next surge.

It bridges system design and operational maturity — interviewers want to see that you can both **architect** and **operate** at scale.

---

## 8.2 Step 1: Clarifying the Situation (War Room Minute Zero)

Before touching anything, understand the situation:

```
  Question                                | Why it matters
  ----------------------------------------|--------------------------------------
  Is this legitimate traffic or an attack? | DDoS needs a WAF/mitigation, not
                                           | scaling. Verify with traffic patterns.
  What triggered the surge?                | Flash sale (predictable end), viral
                                           | post (unpredictable), TV mention,
                                           | partner integration, bot scraping?
  Which services are affected?             | Is it the whole stack or a single
                                           | microservice / API endpoint?
  What is the current error rate?          | 1% errors vs. 50% errors demand
                                           | different urgency levels.
  What is the user impact?                 | Timeouts? 5xx errors? Slow pages?
                                           | Payment failures?
  How long will it last?                   | Flash sale ends in 4 hours vs.
                                           | sustained growth.
  Do we have auto-scaling enabled?         | If yes, why hasn't it kicked in?
                                           | Limits? Cooldown? Bottleneck elsewhere?
```

**Rule of thumb:** spend the first 2 minutes understanding the blast radius. Do not start changing things blindly.

---

## 8.3 Step 2: Immediate Triage (First 10 Minutes)

**Goal:** identify what is broken and what is merely slow.

```
TRIAGE CHECKLIST

  1. Check dashboards (Grafana / Datadog / CloudWatch):
     - Request rate (QPS) per service.
     - Error rate (5xx, 4xx).
     - Latency (p50, p95, p99).
     - CPU, memory, disk I/O on all tiers.
     - Database connections (active, waiting, max).
     - Queue depth (Kafka lag, SQS depth).
     - Cache hit rate.

  2. Identify the bottleneck tier:
     Is it the load balancer? -> connection limits.
     Is it the application? -> CPU/memory saturated, thread pool exhausted.
     Is it the database? -> connection pool maxed, CPU at 100%, slow queries.
     Is it the cache? -> eviction storm, cache miss avalanche.
     Is it a downstream service? -> dependency timeout cascading up.
     Is it DNS / CDN? -> edge capacity.

  3. Check recent deployments:
     Was anything deployed in the last 24 hours?
     Could a bug be amplifying traffic (retry storms, infinite loops)?
```

```
TYPICAL BOTTLENECK CASCADE AT 10x TRAFFIC

  10x requests hit load balancer
       |
       v
  Application servers saturate CPU / threads
       |
       v
  Database connection pool exhausted (max connections hit)
       |
       v
  Queries queue up, latency spikes
       |
       v
  Application timeouts -> retries -> amplification -> cascading failure
```

**The most common first failure:** the **database** or a **downstream dependency** runs out of connections or CPU, causing upstream services to time out and retry, which makes everything worse.

---

## 8.4 Step 3: Where Does 10x Traffic Break Things?

Each layer has a breaking point. Know them:

```
  Layer                  | Typical 1x capacity       | What breaks at 10x
  -----------------------|---------------------------|----------------------------
  DNS / CDN              | Very high (managed)       | Usually fine. Check CDN
                         |                           | plan limits.
  Load balancer          | ~50K-100K concurrent      | Connection limits, health
                         | connections (managed ALB) | check storms.
  API Gateway            | Rate limits, throttling   | Configured limits hit;
                         |                           | starts rejecting requests.
  Application servers    | N instances x M threads   | CPU saturated; thread pool
                         | = N*M concurrent requests | exhausted; GC pressure.
  Cache (Redis)          | ~100K ops/sec per node    | Eviction storm if memory
                         |                           | full; hot key contention.
  Database (primary)     | ~5K-20K QPS depending     | Connection pool maxed;
                         | on query complexity       | CPU 100%; replication lag.
  Message queue (Kafka)  | High throughput           | Consumer lag grows;
                         |                           | processing falls behind.
  External APIs / PSPs   | Their rate limits         | 429 errors; timeouts.
  Disk / storage         | IOPS limits               | I/O wait; write stalls.
```

**Key insight:** systems rarely fail uniformly. One layer becomes the bottleneck, and its failure cascades. Find and fix **that** layer first.

---

## 8.5 Step 4: Quick Wins — Survive the Next Hour

These actions can be taken in minutes without code changes:

```
  Quick win                              | How / why
  ---------------------------------------|-------------------------------------
  1. Scale out app servers               | Add instances (auto-scale group,
                                         | Kubernetes HPA, manual).
  2. Increase DB connection limits       | Raise max_connections if headroom
                                         | exists. Use PgBouncer / ProxySQL.
  3. Enable / warm cache                 | Pre-populate cache for hot keys.
                                         | Increase Redis maxmemory.
  4. Enable CDN for static assets        | Offload images, JS, CSS to CDN.
  5. Turn on rate limiting               | Protect critical endpoints; reject
                                         | or queue excess traffic gracefully.
  6. Disable non-critical features       | Feature flags: turn off analytics,
                                         | recommendations, heavy reports.
  7. Enable circuit breakers             | Stop calling failing dependencies;
                                         | return fallback / cached responses.
  8. Increase timeouts (carefully)       | Prevent premature retries, but do
                                         | not set too high or threads pile up.
  9. Reduce retry aggressiveness         | Exponential backoff + jitter; cap
                                         | max retries.
  10. Redirect read traffic to replicas  | Route reads to DB read replicas.
```

**Priority order:** (1) stop the bleeding (rate limit, circuit break, feature flag), (2) add capacity (scale out, cache, replica), (3) optimize (queries, connection pools).

---

## 8.6 Step 5: Scaling the Compute Layer

```
SCALING APPLICATION SERVERS

  Stateless services (most web/API servers):
    - Horizontal scaling: add more instances behind the load balancer.
    - Kubernetes: increase replica count or HPA target.
    - Cloud auto-scaling: adjust ASG min/max/desired.
    - Time to effect: 1-5 minutes (container), 3-10 minutes (VM).

  Stateful services (WebSocket, session-sticky):
    - Harder to scale; need session externalization (Redis sessions).
    - Drain connections before removing instances.

  Serverless / Lambda:
    - Scales automatically but has concurrency limits.
    - Request limit increase from cloud provider (may take time).
```

```
THREAD / CONNECTION POOL TUNING

  Before:
    maxThreads = 200
    maxConnections = 50 (to DB)
    -> 200 threads competing for 50 DB connections -> contention.

  Quick fix:
    Scale out app servers (more total threads).
    Add connection pooler (PgBouncer) to multiplex.
    Do NOT just raise maxThreads without adding DB capacity — it shifts
    the bottleneck to the database.
```

---

## 8.7 Step 6: Scaling the Data Layer

The database is almost always the hardest layer to scale quickly.

```
  Strategy                        | Time to implement | Effect
  --------------------------------|-------------------|----------------------
  Read replicas (route reads)     | Minutes-hours     | Offload 80%+ of reads.
  Connection pooler (PgBouncer,   | Minutes           | Multiplex app connections
  ProxySQL)                       |                   | into fewer DB connections.
  Vertical scaling (bigger box)   | Minutes (cloud)   | More CPU/RAM for the
                                  |                   | primary. Downtime risk.
  Query optimization + indexes    | Minutes-hours     | Reduce per-query CPU.
  Caching hot queries in Redis    | Minutes-hours     | Avoid hitting DB at all
                                  |                   | for repeated reads.
  Sharding (horizontal partition) | Days-weeks        | Long-term; not a quick
                                  |                   | fix during an incident.
```

```
READ REPLICA ROUTING

  Application / ORM / Proxy routes:
    - All writes -> primary
    - All reads  -> replica pool (round-robin)

  Caution:
    - Replication lag means replicas may serve stale data.
    - Acceptable for most reads (product pages, search results).
    - Not acceptable for reads-after-writes (order status, balance).
    - For critical reads-after-writes: read from primary or use
      "read-your-writes" consistency (session-sticky to primary
      for N seconds after a write).
```

---

## 8.8 Step 7: Caching Under Pressure

Caching is your biggest force multiplier during a traffic spike:

```
  Caching action                     | Effect
  -----------------------------------|--------------------------------------
  Increase Redis maxmemory           | Avoid eviction storms.
  Pre-warm cache for hot keys        | Prevent thundering herd on cold start.
  Add local in-process cache         | Caffeine / Guava: sub-millisecond,
  (L1 cache)                         | reduces Redis calls by 50-80%.
  Increase TTL for stable data       | Fewer cache misses, less DB load.
  Cache empty results (negative      | Prevent cache penetration for
  caching)                           | non-existent keys.
  Use stale-while-revalidate         | Serve stale cache while refreshing
                                     | in background. Never block on miss.
```

**Thundering herd / cache stampede:**
```
  Problem:
    A popular cache key expires. 10,000 concurrent requests all miss
    the cache simultaneously and hit the database. DB collapses.

  Solutions:
    1. Lock-based refresh: only one thread refreshes; others wait or
       get stale value.
    2. Probabilistic early expiration: each request has a small
       probability of refreshing before TTL expires.
    3. Background refresh: a separate process refreshes expiring keys
       before they expire (no user request blocks).
    4. Never-expire + async update: cache never expires; a background
       job keeps it fresh.
```

---

## 8.9 Step 8: Rate Limiting, Load Shedding & Back-Pressure

When you cannot scale fast enough, **protect what you have**:

```
RATE LIMITING

  Goal: cap traffic to what the system can handle; reject excess gracefully.

  Strategies:
    - Per-user / per-IP rate limit (token bucket, sliding window).
    - Per-endpoint rate limit (protect expensive endpoints).
    - Global rate limit (cap total RPS at system capacity).

  Response for rejected requests:
    HTTP 429 Too Many Requests
    Retry-After: 5

  Implementation:
    - API Gateway level (Kong, Envoy, AWS API Gateway).
    - Application level (Resilience4j RateLimiter, Guava RateLimiter).
    - Redis-based distributed rate limiter (INCR + EXPIRE).
```

```
LOAD SHEDDING

  Goal: when overloaded, drop low-priority work to protect high-priority work.

  Example priorities:
    CRITICAL: checkout, payment, login.
    HIGH:     search, product pages.
    MEDIUM:   recommendations, reviews.
    LOW:      analytics, logging, non-essential background jobs.

  Under 10x load:
    - Serve CRITICAL and HIGH at full quality.
    - Degrade MEDIUM (return cached / simplified results).
    - Drop LOW entirely (disable analytics tracking, pause batch jobs).
```

```
BACK-PRESSURE

  Goal: slow down producers when consumers cannot keep up.

  Techniques:
    - Bounded queues: reject new messages when queue is full.
    - Flow control: consumer signals producer to slow down.
    - HTTP 503 with Retry-After: tell clients to back off.
    - Kafka consumer lag alerting: if lag > threshold, scale consumers
      or pause non-critical topics.
```

---

## 8.10 Step 9: CDN, Edge & Static Offload

Push as much traffic as possible to the edge so it never hits your origin:

```
  Offload target                  | How
  --------------------------------|--------------------------------------
  Static assets (JS, CSS, images) | Serve from CDN (CloudFront, Akamai,
                                  | Cloudflare). Cache-Control: max-age.
  API responses (cacheable)       | CDN caching for public GET endpoints
                                  | with short TTL (10-60 seconds).
  HTML pages (SSR)                | Edge-side rendering or stale-while-
                                  | revalidate at CDN.
  DNS                             | Use a managed DNS provider with
                                  | global anycast (Route 53, Cloudflare).
```

**CDN impact at 10x traffic:**
```
  Without CDN:
    50,000 RPS -> all hit your origin servers.

  With CDN (90% cache hit rate):
    50,000 RPS -> 5,000 hit origin, 45,000 served from edge.
    Your origin only sees 1x traffic, not 10x.
```

This is often the single most effective action you can take.

---

## 8.11 Step 10: Queue & Async Processing

Not everything needs to happen synchronously in the request path:

```
  Synchronous (must do now)            | Asynchronous (can defer)
  -------------------------------------|-------------------------------
  Render product page                  | Send order confirmation email.
  Process payment                      | Update analytics/dashboards.
  Authenticate user                    | Generate PDF invoice.
  Return search results                | Update recommendation model.
  Show order status                    | Sync data to warehouse.
```

**Under 10x load, aggressively move work to async:**
```
  Before: API -> process payment -> send email -> update analytics -> respond
  After:  API -> process payment -> enqueue(email, analytics) -> respond

  The queue absorbs the burst. Workers process at their own pace.
```

```
QUEUE SIZING

  Normal: 100 messages/sec produced, 100/sec consumed -> lag ~0.
  10x:    1,000 messages/sec produced, 100/sec consumed -> lag grows.

  Options:
    1. Scale consumers (add more workers / partitions).
    2. Increase queue retention (let lag grow safely).
    3. Drop non-critical messages (analytics) to prioritize critical ones.
```

---

## 8.12 Step 11: Communication & Incident Management

Technical fixes are half the battle. Communication is the other half.

```
  Action                              | Who / when
  ------------------------------------|--------------------------------------
  Declare an incident                 | Incident commander, immediately.
  Page the right people               | On-call SRE, backend lead, DBA,
                                      | infra team.
  Status page update                  | "We are experiencing high traffic.
                                      | Some users may see slow load times."
  Internal Slack/Teams channel        | Dedicated #incident channel.
  Regular updates (every 15-30 min)   | Even if nothing changed.
  Customer support heads-up           | So CS can respond to user complaints.
  Business stakeholder update         | Revenue impact, ETA for resolution.
  Post-incident: all-clear            | "Service restored at 04:30 UTC."
```

**Incident roles:**
- **Incident Commander:** coordinates response, makes decisions, communicates.
- **Technical Lead:** drives diagnostics and fixes.
- **Communications Lead:** updates status page, Slack, stakeholders.
- **Scribe:** logs timeline and actions for the postmortem.

---

## 8.13 Step 12: Post-Crisis — Building for the Next 10x

Once the incident is resolved, prevent recurrence:

```
  Long-term improvement                | Details
  -------------------------------------|-------------------------------------
  Auto-scaling with proper limits      | Set HPA/ASG to scale to 10x+ with
                                       | tested limits. Load test regularly.
  Capacity planning                    | Right-size for peak, not average.
                                       | Budget for 3-5x headroom.
  Load testing / chaos engineering     | Run 10x load tests quarterly.
                                       | Use Locust, k6, Gatling, JMeter.
  CDN-first architecture               | Serve everything cacheable from CDN.
  Database read replicas always on     | Do not wait for an incident to add
                                       | replicas.
  Connection pooling always on         | PgBouncer/ProxySQL in front of every
                                       | database.
  Feature flags for degradation        | Every non-critical feature has a
                                       | kill switch.
  Rate limiting by default             | All public APIs have rate limits.
  Queue-based async for non-critical   | Email, analytics, reports are always
                                       | async.
  Runbooks                             | Step-by-step guide for "10x traffic"
                                       | scenario, tested quarterly.
  Cost alerting                        | Auto-scaling can be expensive;
                                       | set budget alerts.
```

**Capacity planning formula:**
```
  Provision for:
    peak_traffic x safety_margin x growth_buffer

  Example:
    Normal peak: 10,000 RPS
    Safety margin: 3x (handle 30,000 RPS)
    Growth buffer: 1.5x (handle 45,000 RPS)
    -> Provision infra for ~45,000 RPS at all times.
    -> Auto-scale to 100,000+ RPS for extreme surges.
```

**Postmortem template:**
```
  Title: 10x Traffic Surge — [Date]

  Timeline:
    02:00 - Traffic spike detected by monitoring.
    02:05 - On-call paged; incident declared.
    02:10 - Identified DB connection exhaustion.
    02:15 - Enabled PgBouncer; scaled app servers.
    02:25 - Enabled rate limiting on non-critical APIs.
    02:30 - CDN cache warmed; origin traffic dropped to 2x.
    02:45 - System stable at 50,000 RPS.
    03:00 - All-clear declared.

  Root cause:
    Viral social media post drove 10x traffic. DB connection pool
    (max 200) exhausted; app server threads blocked waiting for
    connections; cascading timeouts caused retry storms.

  What worked:
    - Monitoring detected spike within 2 minutes.
    - PgBouncer reduced effective DB connections.
    - CDN offloaded 90% of read traffic.

  What did not work:
    - Auto-scaling was configured but had a 10-minute cooldown.
    - No rate limiting on public APIs.
    - No feature flags for non-critical services.

  Action items:
    1. Reduce auto-scale cooldown to 2 minutes.
    2. Add rate limiting to all public endpoints.
    3. Add feature flags for recommendations, analytics.
    4. Run quarterly 10x load tests.
    5. Add PgBouncer to all services permanently.
```

---

## 8.14 Step 13: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Scaling too fast = cost spike    | Auto-scaling to 10x means 10x cost. Set
                                   | budget alerts and max limits.
  Rate limiting too aggressive     | Rejecting real users loses revenue. Use
                                   | priority-based shedding, not blanket limits.
  Adding replicas with lag         | Read replicas have replication lag. Stale
                                   | data is acceptable for most reads but not
                                   | for order status or balance checks.
  Caching stale data               | Longer TTLs reduce DB load but risk serving
                                   | outdated prices/inventory. Use short TTLs
                                   | for volatile data.
  Disabling features permanently   | Feature flags should be temporary. Re-enable
                                   | after scaling. Do not let "temporary" become
                                   | permanent technical debt.
  Ignoring downstream limits       | Scaling your system does not scale third-party
                                   | APIs (PSPs, email providers). They will rate
                                   | limit you.
  Retry storms                     | Under load, aggressive retries multiply
                                   | traffic. Use exponential backoff + jitter +
                                   | circuit breakers.
  Panic-driven changes             | Making untested changes at 2 AM causes new
                                   | bugs. Follow runbooks; do not improvise.
  No postmortem                    | If you do not learn from the incident, it
                                   | will happen again.
```

---

## 8.15 Problem 8 Summary

- **Stay calm and structured:** clarify the situation, triage dashboards, identify the bottleneck tier, then act. Do not change things blindly.
- **The usual first failure is the database:** connection pool exhaustion, CPU saturation, or slow queries. Use connection poolers (PgBouncer/ProxySQL), read replicas, and query caching as first-line defenses.
- **Quick wins buy time:** rate limit, feature-flag off non-critical services, enable circuit breakers, scale out stateless tiers, warm caches, offload to CDN.
- **CDN is the biggest force multiplier:** a 90% cache hit rate at the CDN means your origin only sees 1x traffic even under a 10x surge.
- **Caching under pressure:** pre-warm hot keys, increase TTLs, add local L1 caches, use stale-while-revalidate, and guard against thundering herd.
- **Rate limiting and load shedding protect the system:** prioritize critical paths (checkout, payment) and degrade or drop low-priority work (analytics, recommendations).
- **Move non-critical work to async queues:** email, analytics, reports can wait. The queue absorbs the burst; workers catch up later.
- **Communication matters as much as fixes:** declare an incident, assign roles, update the status page, and post regular updates.
- **Build for the next 10x after the crisis:** auto-scaling with tested limits, capacity planning (3-5x headroom), quarterly load tests, CDN-first architecture, connection pooling always on, feature flags for degradation, and runbooks.
- **Always do a blameless postmortem:** document the timeline, root cause, what worked, what did not, and concrete action items with owners and deadlines.

---

# Problem 9: Latency Increased from 100 ms to 800 ms — Root Cause Approach

## 9.1 Problem Statement

> Your API's p50 latency was a steady 100 ms for months. Starting yesterday, it climbed to 800 ms. No deployment was made in the last 48 hours. Users are complaining about slowness. Walk through your systematic approach to find and fix the root cause.

This question tests:
- **Structured diagnostic thinking** — not guessing, but methodically narrowing down.
- **Full-stack awareness** — network, application, database, cache, external services.
- **Observability maturity** — what you measure, how you correlate, and what tools you reach for.
- **Communication** — explaining your reasoning to an interviewer as you go.

It is a favorite of FAANG/big-tech interviews because it separates engineers who **debug by method** from those who **debug by luck**.

---

## 9.2 Step 1: Clarifying the Symptom (What Exactly Got Slower?)

Before diving into metrics, ask precise questions:

```
  Question                                  | Why it matters
  ------------------------------------------|--------------------------------------
  Which endpoint / API?                     | All endpoints or one specific route?
                                            | One route = localized issue.
  Which percentile?                         | p50 moved (median, everyone is slow)
                                            | vs. p99 moved (only tail is slow).
  All users or a subset?                    | One region? One client version?
                                            | One user segment?
  When exactly did it start?                | Correlate with deployments, config
                                            | changes, traffic shifts, incidents.
  Is it constant 800 ms or intermittent?    | Constant = structural change.
                                            | Intermittent = contention, GC, retry.
  Did traffic volume change?                | More traffic can push latency up
                                            | even without code changes.
  Any recent infra changes?                 | Cloud provider maintenance, network
                                            | changes, certificate rotation, DNS.
```

**Key distinction:** if p50 moved, the **common path** got slower (most requests affected). If only p99 moved, look for tail-latency causes (GC, lock contention, cold cache, one slow downstream).

---

## 9.3 Step 2: The Latency Mental Model (Where Time Hides)

Every request spends time somewhere. Break it down:

```
REQUEST LATENCY BUDGET (100 ms target)

  Client -> [DNS + TCP + TLS]  -> Load Balancer -> Application -> Database
              ~5 ms                  ~1 ms           ~20 ms       ~30 ms
                                                       |
                                                       +-> Cache (Redis)  ~1 ms
                                                       +-> External API   ~40 ms
                                                       +-> Serialization  ~3 ms
                                                                          ------
                                                              Total:     ~100 ms

  If total jumped to 800 ms, where did the extra 700 ms go?
  Exactly ONE (or a few) of these segments grew. Find it.
```

```
TIME DECOMPOSITION

  Total latency = network_in + LB_processing + app_processing + db_query
                + cache_lookup + external_call + serialization + network_out

  At 100 ms everything is balanced.
  At 800 ms, one term dominates. Your job is to find which one.
```

**Analogy:** it is like a hospital visit that used to take 30 minutes and now takes 3 hours. Was it the waiting room? The doctor? The lab? The pharmacy? You time each step.

---

## 9.4 Step 3: Immediate Data Gathering (First 10 Minutes)

```
DASHBOARD CHECKLIST

  Metric                          | What to look for
  --------------------------------|--------------------------------------
  Request rate (QPS)              | Did traffic increase? 2x traffic can
                                  | double latency if at capacity.
  Error rate (5xx, timeouts)      | Errors + retries add latency.
  Latency breakdown (p50/p95/p99) | Which percentile moved? How much?
  CPU utilization (app servers)   | >80% = contention, context switching.
  Memory usage / GC pauses        | GC stop-the-world events add 100s of ms.
  Thread pool / connection pool   | Exhausted pool = requests queue.
  Database query latency          | Slow queries or lock waits?
  Database CPU / connections      | Maxed out?
  Cache hit rate                  | Drop from 95% to 60% = many more DB calls.
  External API latency            | Third-party got slower?
  Network latency (ping, tracert) | Increased round-trip time?
  Disk I/O (iowait)               | Storage bottleneck?
  DNS resolution time             | DNS issues add 100s of ms.
  TLS handshake time              | Certificate issues or misconfig.
```

**Golden rule:** look at **time-series graphs** with the regression start time marked. The metric that changed at the same time as latency is your prime suspect.

---

## 9.5 Step 4: Correlate with Changes (The Usual Suspects)

Even if "no deployment was made," something changed. Find it:

```
  Change category                  | Examples
  ---------------------------------|---------------------------------------------
  Code deployment                  | Feature flag turned on, A/B test activated,
                                   | config change pushed, library version bump.
  Infrastructure                   | VM migration, cloud provider maintenance,
                                   | auto-scaling event, AZ failover, new node
                                   | joined/left cluster.
  Database                         | Schema migration, index dropped/created,
                                   | statistics stale, table grew past threshold,
                                   | autovacuum running.
  Cache                            | Redis restart (cold cache), eviction policy
                                   | change, maxmemory reached, key expiry storm.
  Traffic pattern                  | New client/bot, partner integration went
                                   | live, geographic shift, new heavy query.
  External dependency              | Third-party API degraded, DNS provider
                                   | slow, CDN config change.
  Certificate / TLS                | Cert renewal changed provider, OCSP
                                   | stapling broken, CRL check added latency.
  OS / runtime                     | Kernel update, JVM version change, GC
                                   | config altered, container resource limits.
```

**Pro tip:** check the **change log / audit trail** for config management (Terraform, Ansible, feature flags, Kubernetes manifests) — these are the "no deployment" deployments that people forget about.

---

## 9.6 Step 5: Narrow the Blast Radius (Which Tier?)

Use a **binary search** approach to isolate the slow tier:

```
TIER ISOLATION STRATEGY

  1. Check load balancer access logs:
     - LB latency vs. upstream latency.
     - If LB latency is low but upstream is high -> problem is behind the LB.
     - If LB latency itself is high -> network or LB issue.

  2. Check application-level timing:
     - Instrument request handler: total time, DB time, cache time,
       external call time, serialization time.
     - Which sub-timer grew?

  3. The elimination table:

     Test                              | If normal     | If slow
     ----------------------------------|---------------|------------------
     Ping / curl to LB                 | Network OK    | Network issue
     Simple health-check endpoint      | App starts OK | App overloaded
     Query DB directly (psql/mysql)    | DB is fine    | DB is the issue
     Redis PING + GET known key        | Cache OK      | Cache issue
     Curl external API directly        | Ext API OK    | Ext API degraded
     Run same request with cache warm  | Cache helps   | Not a cache issue
     Run same request at off-peak      | Load-related  | Structural issue
```

```
DECISION TREE

  Is LB -> app latency high?
    YES -> App or downstream problem.
      Is DB query latency high?
        YES -> DB problem (query plan, lock, connection, index).
        NO  -> Is cache hit rate low?
          YES -> Cache problem (cold, eviction, miss storm).
          NO  -> Is external API latency high?
            YES -> External dependency degraded.
            NO  -> App-layer problem (GC, thread pool, CPU, code regression).
    NO -> Network or LB problem.
```

---

## 9.7 Step 6: Network & Infrastructure Layer

If the problem is below the application:

```
  Symptom                          | Likely cause                 | Fix
  ---------------------------------|------------------------------|------------------
  Increased RTT (ping / tracert)   | Network congestion, routing  | Check provider
                                   | change, cross-AZ traffic.    | status; move
                                   |                              | traffic same-AZ.
  TCP retransmissions              | Packet loss, NIC saturation. | Check interface
                                   |                              | errors, MTU.
  TLS handshake slow               | OCSP check, cert chain too   | Enable OCSP
                                   | long, weak cipher.           | stapling, shorten
                                   |                              | chain.
  DNS resolution slow              | DNS provider issue, cache    | Use local DNS
                                   | miss, TTL too low.           | cache, increase
                                   |                              | TTL.
  Load balancer queue time         | Connection limit hit,        | Scale LB, check
                                   | health checks failing.       | config.
  Cross-region latency spike       | Traffic routing changed,     | Check DNS/CDN
                                   | CDN config.                  | routing rules.
```

**Quick test:** run the same request **from the same datacenter** as the server (bypassing external network). If latency drops, the problem is network/edge.

---

## 9.8 Step 7: Application Layer (Code, Threads, GC)

If the app tier is the bottleneck:

```
  Symptom                          | Likely cause                 | Diagnosis
  ---------------------------------|------------------------------|------------------
  CPU > 80%                        | Hot loop, unoptimized code,  | Profile (async-
                                   | regex backtracking, JSON     | profiler, JFR).
                                   | serialization.               |
  GC pauses (Java / .NET)          | Heap too small, object       | GC logs, increase
                                   | churn, memory leak.          | heap, tune GC.
  Thread pool exhausted            | All threads waiting on slow  | Thread dump;
                                   | I/O or lock.                 | increase pool or
                                   |                              | fix blocking call.
  Connection pool exhausted        | DB or HTTP client pool       | Pool metrics;
                                   | maxed; requests queue.       | increase pool or
                                   |                              | reduce hold time.
  Lock contention                  | Synchronized block, DB row   | Thread dump;
                                   | lock, distributed lock.      | reduce critical
                                   |                              | section.
  Increased payload size           | Response grew (new field,    | Check response
                                   | missing pagination, N+1).    | size over time.
  Logging overhead                 | Debug logging accidentally   | Check log level;
                                   | enabled in production.       | switch to async
                                   |                              | logging.
  Feature flag / A/B test          | New code path slower than    | Check flag state;
                                   | old path.                    | compare latency
                                   |                              | per variant.
```

```
GC INVESTIGATION (Java / JVM)

  1. Check GC logs for long pauses:
     - G1/ZGC: look for "pause" > 100 ms.
     - Old gen full GC: can cause 500ms+ pauses.

  2. Check heap usage trend:
     - Steadily rising = memory leak.
     - Sawtooth but higher peaks = more allocation pressure.

  3. Quick wins:
     - Increase -Xmx (more heap).
     - Switch to low-pause GC (ZGC, Shenandoah).
     - Fix object churn (reuse buffers, reduce allocations).
```

```
THREAD DUMP ANALYSIS

  1. Take 3 thread dumps 5 seconds apart.
  2. Look for threads stuck in the same place across all 3 dumps.
  3. Common stuck locations:
     - java.net.SocketInputStream.read -> waiting on network I/O.
     - java.util.concurrent.locks -> lock contention.
     - com.zaxxer.hikari.pool -> waiting for DB connection.
     - sun.misc.Unsafe.park -> thread pool queue full.
  4. If many threads are stuck on the same lock/resource,
     that is your bottleneck.
```

---

## 9.9 Step 8: Database & Storage Layer

Database is the most common latency culprit:

```
  Symptom                          | Likely cause                 | Fix
  ---------------------------------|------------------------------|------------------
  Slow query (EXPLAIN shows        | Missing index, stale stats,  | Add index, ANALYZE
  full table scan)                 | table grew beyond threshold. | table, rewrite
                                   |                              | query.
  Lock waits (row/table locks)     | Long transaction holding     | Shorten txns,
                                   | locks; deadlocks.            | use optimistic
                                   |                              | locking.
  Connection pool maxed            | Too many concurrent queries;  | Add PgBouncer,
                                   | slow queries hold connections.| fix slow queries.
  Replication lag                  | Writes overwhelming replica; | Scale replicas,
                                   | queries routed to lagging    | reduce write
                                   | replica return stale/slow.   | volume.
  Disk I/O (iowait high)          | Data outgrew RAM; DB reading | Add RAM, move to
                                   | from disk instead of cache.  | SSD, partition.
  Autovacuum / maintenance         | PostgreSQL autovacuum running | Schedule during
                                   | on large table.              | off-peak, tune
                                   |                              | autovacuum params.
  Query plan regression            | Optimizer chose a worse plan  | Pin plan, update
                                   | after ANALYZE or stats       | stats, use hints.
                                   | change.                      |
  N+1 query pattern                | ORM loading related entities | Batch fetch, JOIN,
                                   | one by one (100 queries      | eager load.
                                   | instead of 1).               |
```

```
DATABASE DIAGNOSTIC COMMANDS

  PostgreSQL:
    SELECT * FROM pg_stat_activity WHERE state = 'active';
    SELECT * FROM pg_stat_statements ORDER BY mean_exec_time DESC LIMIT 10;
    SELECT * FROM pg_locks WHERE NOT granted;
    EXPLAIN (ANALYZE, BUFFERS) SELECT ...;

  MySQL:
    SHOW PROCESSLIST;
    SHOW ENGINE INNODB STATUS;
    SELECT * FROM performance_schema.events_statements_summary_by_digest
      ORDER BY avg_timer_wait DESC LIMIT 10;
    EXPLAIN SELECT ...;
```

---

## 9.10 Step 9: Cache Layer (Misses, Evictions, Cold Start)

A cache regression can silently add hundreds of milliseconds:

```
  Scenario                         | What happens                  | Fix
  ---------------------------------|-------------------------------|------------------
  Cache hit rate dropped           | More requests hit the DB.     | Find why: eviction,
  (e.g., 95% -> 60%)              | Each miss adds ~30 ms.        | TTL change, key
                                   | 35% more misses x 30 ms =    | pattern change.
                                   | ~10 ms avg increase per req.  |
  Redis restart / failover         | All keys lost; cold cache.    | Pre-warm cache on
                                   | Every request hits DB.        | startup; stale-
                                   |                               | while-revalidate.
  maxmemory reached                | Eviction storm; hot keys      | Increase memory;
                                   | evicted, re-fetched, evicted. | audit key sizes.
  Hot key                          | Single key hit by 10K QPS;    | Local L1 cache;
                                   | Redis single-threaded =>      | replicate hot keys;
                                   | queuing.                      | hash-tag sharding.
  Key size grew                    | Serialized value grew (list   | Compress values;
                                   | with 10K items); GET takes    | paginate large
                                   | longer.                       | values.
  Network to cache                 | Redis in different AZ; RTT    | Co-locate app and
                                   | added 2 ms x 20 calls =      | cache in same AZ;
                                   | 40 ms extra.                  | pipeline calls.
  TTL too short                    | Keys expire too fast; low     | Increase TTL for
                                   | hit rate; thundering herd.    | stable data.
```

**Quick test:** temporarily increase cache TTL to 1 hour for a non-critical endpoint. If latency drops, the cache layer is the culprit.

---

## 9.11 Step 10: External Dependencies & Third-Party Calls

Your system is only as fast as its slowest dependency:

```
  Symptom                          | Likely cause                 | Fix
  ---------------------------------|------------------------------|------------------
  External API latency increased   | Third-party degradation,     | Check their status
                                   | rate limiting, DNS change.   | page; add timeout +
                                   |                              | circuit breaker.
  Timeout waiting for response     | External service overloaded  | Reduce timeout;
                                   | or network issue.            | return cached /
                                   |                              | fallback response.
  Sequential external calls        | Calling 3 APIs one after     | Parallelize calls
                                   | another: 3 x 200 ms = 600ms.| (CompletableFuture,
                                   |                              | async).
  DNS resolution for external host | DNS cache expired; resolver  | Cache DNS locally;
                                   | slow.                        | use IP-based
                                   |                              | connection pooling.
  TLS renegotiation                | Connection not reused; full   | Enable HTTP
                                   | TLS handshake every request. | keep-alive,
                                   |                              | connection pool.
```

```
CIRCUIT BREAKER FOR SLOW DEPENDENCIES

  Normal state: calls go through; latency monitored.
  Slow state detected: if p95 > threshold for N calls:
    -> OPEN circuit: stop calling; return fallback/cached result.
    -> After cooldown: HALF-OPEN: try one call.
       -> If fast: CLOSE circuit, resume.
       -> If still slow: stay OPEN.

  This prevents one slow dependency from dragging your entire system to 800 ms.
```

---

## 9.12 Step 11: Distributed Tracing — The Silver Bullet

If you have distributed tracing (Jaeger, Zipkin, Datadog APM, OpenTelemetry), this is the fastest path to root cause:

```
WHAT A TRACE SHOWS

  Trace for GET /api/products (total: 820 ms)
  |
  +-- [gateway]         12 ms
  +-- [product-service]  8 ms
  |   +-- [Redis GET]    2 ms   (cache miss)
  |   +-- [PostgreSQL]  650 ms  <-- THIS IS THE PROBLEM
  |   |   +-- SELECT * FROM products WHERE category = 'electronics'
  |   |       (full table scan, missing index on category)
  |   +-- [serialize]    5 ms
  +-- [recommendation]  140 ms  (parallel call, not on critical path)
  +-- [response]         3 ms
```

**How tracing pinpoints the problem:**
- Shows **exact time** spent in each service and each I/O call.
- Reveals **sequential vs. parallel** calls.
- Highlights the **single span** that grew (650 ms query that used to be 30 ms).
- Links to the **exact query, endpoint, or external call** responsible.

**If you do not have tracing:** add manual timing logs at each boundary (entry, DB call, cache call, external call, exit). Less elegant but still effective.

```
MANUAL TIMING (if no tracing)

  long start = System.nanoTime();
  // ... DB call ...
  long dbTime = System.nanoTime() - start;
  log.info("db_time_ms={}", dbTime / 1_000_000);

  // ... cache call ...
  long cacheTime = ...;
  log.info("cache_time_ms={}", ...);

  // Add these to structured logs; query by request ID.
```

---

## 9.13 Step 12: Common Root Causes (Cheat Sheet)

A quick-reference of the most frequent causes of latency regressions:

```
  Rank | Root cause                           | Frequency
  -----|--------------------------------------|----------
   1   | Database: missing index / plan       | Very common
       | regression / table growth.           |
   2   | Cache: hit rate drop / cold cache /  | Very common
       | eviction storm.                      |
   3   | GC pauses (JVM): heap pressure,      | Common
       | memory leak.                         |
   4   | Thread/connection pool exhaustion:    | Common
       | requests queue behind slow I/O.      |
   5   | External dependency degradation:     | Common
       | third-party API slower.              |
   6   | N+1 query: ORM fetching related      | Common
       | entities one by one.                 |
   7   | Traffic increase: more load without  | Moderate
       | scaling.                             |
   8   | Network: cross-AZ latency, DNS,      | Moderate
       | TLS issues.                          |
   9   | Feature flag / config change:        | Moderate
       | new code path, debug logging.        |
  10   | Noisy neighbor (shared infra):       | Moderate
       | co-tenant stealing CPU/IO.           |
  11   | Lock contention: synchronized block  | Less common
       | or DB row lock under concurrency.    |
  12   | Payload size increase: response      | Less common
       | grew, serialization slower.          |
```

---

## 9.14 Step 13: Fix, Validate & Prevent

**Fix:**
```
  Root cause identified            | Fix
  ---------------------------------|---------------------------------------------
  Missing index                    | CREATE INDEX CONCURRENTLY, ANALYZE.
  Query plan regression            | Update stats, pin plan, rewrite query.
  N+1 queries                      | Batch fetch, JOIN, @EntityGraph.
  Cache hit rate drop              | Increase TTL, pre-warm, increase memory.
  GC pauses                        | Increase heap, switch to ZGC, fix leak.
  Thread pool exhaustion           | Increase pool, fix blocking call, add async.
  External API slow                | Add circuit breaker, timeout, fallback.
  Traffic increase                 | Scale out, add caching, rate limit.
  Network / DNS                    | Co-locate in same AZ, cache DNS, fix TLS.
  Config / feature flag            | Roll back flag, fix config.
```

**Validate the fix:**
```
  1. Deploy fix to one instance / canary.
  2. Compare latency (p50, p95, p99) before and after.
  3. Verify no increase in error rate.
  4. Gradually roll out to all instances.
  5. Monitor for 24 hours to confirm stability.
```

**Prevent recurrence:**
```
  Prevention measure                | Details
  ----------------------------------|--------------------------------------------
  Latency SLO + alerting            | Alert if p50 > 150 ms or p99 > 500 ms.
  Automated canary analysis         | Compare latency metrics during deployment;
                                    | auto-rollback if regression detected.
  Query review in CI/CD             | EXPLAIN all new queries; flag full scans.
  Cache hit rate monitoring         | Alert if hit rate drops below 90%.
  Load testing before release       | Run load test against staging; catch
                                    | regressions before production.
  Distributed tracing always on     | Instrument all services with OpenTelemetry;
                                    | sample at 1-10% in production.
  Dependency SLO tracking           | Monitor external API latency; alert if
                                    | degraded.
  GC monitoring                     | Alert on GC pauses > 100 ms.
  Connection pool monitoring        | Alert if pool utilization > 80%.
  Runbook for latency regression    | Step-by-step guide: check dashboards,
                                    | correlate changes, isolate tier, fix.
```

---

## 9.15 Step 14: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Adding indexes to fix queries    | Fixes reads but slows writes and costs
                                   | storage. Always measure write impact.
  Increasing cache TTL             | Reduces load but risks serving stale data.
                                   | Use short TTLs for volatile data.
  Increasing thread/conn pool      | Helps throughput but shifts bottleneck to
                                   | the database if DB cannot handle more
                                   | concurrent queries.
  Adding circuit breakers           | Protects your system but degrades user
                                   | experience when circuit is open. Provide
                                   | meaningful fallbacks.
  Distributed tracing overhead     | Adds 1-5% latency overhead and storage
                                   | cost. Sample at 1-10% in production;
                                   | 100% in staging.
  Over-caching                     | Caching everything hides bugs and makes
                                   | invalidation complex. Cache deliberately.
  Blaming the network              | Network is rarely the root cause but
                                   | always the first suspect. Prove it with
                                   | data before escalating to infra team.
  Fixing symptoms not root cause   | Adding more instances hides a missing
                                   | index. Fix the root cause; scale as a
                                   | temporary measure.
  Chasing p99 vs. p50              | Fixing p99 may require different tools
                                   | (GC tuning, tail-at-scale hedging) than
                                   | fixing p50 (query optimization, caching).
```

---

## 9.16 Problem 9 Summary

- **Start with the symptom:** which endpoint, which percentile, which users, when did it start. p50 vs. p99 tells you whether the common path or the tail got slower.
- **Know where time hides:** decompose latency into network, LB, app, DB, cache, external call, and serialization. The segment that grew is your target.
- **Gather data immediately:** dashboards for QPS, error rate, latency breakdown, CPU, memory, GC, thread pool, cache hit rate, DB connections, disk I/O. Look at time-series graphs aligned to the regression start.
- **Correlate with changes:** deployments, config/feature flags, infra changes, DB migrations, cache restarts, traffic shifts, external dependency degradation. Something always changed — find it.
- **Narrow by tier:** use a decision tree — LB latency, app timing, DB query time, cache hit rate, external call duration. Binary search to the slow layer.
- **Database is the #1 suspect:** missing index, plan regression, N+1 queries, lock waits, connection pool exhaustion, autovacuum. Use EXPLAIN ANALYZE and pg_stat_statements.
- **Cache regressions are silent killers:** a hit-rate drop from 95% to 60% can add hundreds of milliseconds. Check for eviction storms, cold cache, key-size growth, TTL changes.
- **GC pauses (JVM):** check GC logs for long pauses; increase heap, switch to ZGC, fix memory leaks and object churn.
- **Distributed tracing is the silver bullet:** a single trace shows exactly which span grew and why. Invest in OpenTelemetry if you do not have it.
- **Fix, validate, prevent:** fix the root cause (not the symptom), canary the fix, monitor for 24 hours, then add latency SLOs, automated canary analysis, cache/hit-rate alerts, query review in CI/CD, and a runbook.

---

# Problem 10: How Do You Design Secure Authentication & Authorization (OAuth, JWT)?

## 10.1 Problem Statement

> Design a secure, scalable authentication and authorization system for a platform with millions of users, multiple client types (web, mobile, third-party), and dozens of microservices. Cover identity verification, token management, access control, and the security considerations that keep real systems safe.

This question tests whether you understand:
- The **difference** between authentication (who are you?) and authorization (what can you do?).
- **OAuth 2.0** grant types and when to use each one.
- **JWT** structure, signing, validation, and the trade-offs of stateless tokens.
- **Token lifecycle** — issuance, refresh, rotation, and revocation.
- **Authorization models** — RBAC, ABAC, and policy engines.
- **Security hardening** — CSRF, XSS, token theft, replay attacks.
- How all of this **scales** across microservices, regions, and federation.

---

## 10.2 Step 1: Authentication vs. Authorization (The Distinction That Matters)

```
  Concept           | Question it answers   | Example
  -------------------|-----------------------|-------------------------------
  Authentication     | "Who are you?"        | User logs in with email +
  (AuthN)            |                       | password; system verifies
                     |                       | identity.
  Authorization      | "What can you do?"    | Verified user tries to delete
  (AuthZ)            |                       | a resource; system checks if
                     |                       | they have the 'admin' role.
```

```
FLOW

  1. User presents credentials (password, MFA, social login).
  2. AuthN service verifies identity -> issues a token.
  3. User sends token with every request.
  4. AuthZ service / middleware checks: does this token grant access
     to this resource + this action?
```

**Common mistake in interviews:** conflating the two. Authentication proves identity; authorization enforces permissions. They are separate concerns with separate infrastructure.

---

## 10.3 Step 2: Clarifying Questions & Scope

```
  Question                                  | Assumption
  ------------------------------------------|----------------------------------
  How many users?                           | 50M registered, 5M DAU.
  Client types?                             | Web (SPA), mobile (iOS/Android),
                                            | server-to-server, third-party.
  Do we support social login (Google, etc.)?| Yes, via OAuth 2.0 / OIDC.
  Multi-factor authentication (MFA)?        | Yes, TOTP / SMS as second factor.
  Single Sign-On (SSO) across products?     | Yes, via centralized IdP.
  How many microservices?                   | 30+ services, each needs to
                                            | verify tokens.
  Compliance requirements?                  | GDPR (data privacy), SOC 2,
                                            | PCI-DSS for payment flows.
  Multi-region?                             | Yes, users worldwide.
```

**Scoping decision:** we design a centralized **Identity Provider (IdP)** that issues tokens, and a distributed **authorization layer** that each service enforces locally.

---

## 10.4 Step 3: Requirements (Functional & Non-Functional)

**Functional requirements:**
- User registration (email/password, social login).
- Login with password + optional MFA.
- Issue access tokens and refresh tokens.
- Token validation by any microservice without calling the IdP on every request.
- Role-based and attribute-based access control.
- Token refresh without re-login.
- Token revocation (logout, password change, compromise).
- Third-party OAuth 2.0 access (authorize external apps to act on behalf of users).
- SSO across multiple applications.

**Non-functional requirements:**
- **Low latency:** token validation < 1 ms (local, no network call).
- **High availability:** auth service must be 99.99% available (login is critical path).
- **Scalability:** handle 50K+ login attempts/sec at peak.
- **Security:** resist token theft, replay, CSRF, XSS, brute force, credential stuffing.
- **Compliance:** GDPR, SOC 2, audit logging of all auth events.

---

## 10.5 Step 4: High-Level Architecture

```
AUTH SYSTEM ARCHITECTURE

  +------------------+
  | Client (Web/App) |
  +--------+---------+
           |
           | 1. Login (credentials)
           v
  +------------------+       +------------------+
  |  API Gateway     |------>| Identity Provider|
  |  (TLS, rate      |       | (IdP / Auth      |
  |   limit)         |<------| Service)         |
  +--------+---------+       +--------+---------+
           |                          |
           | 2. Access Token (JWT)    | Stores:
           |    + Refresh Token       | - User credentials (hashed)
           v                          | - Roles, permissions
  +------------------+               | - MFA secrets
  | Microservice A   |               | - Refresh tokens
  | (validates JWT   |               | - Audit log
  |  locally)        |               +------------------+
  +--------+---------+                        |
           |                                  v
           | 3. AuthZ check             +------------------+
           |    (role/permission         | User Database    |
           |     from JWT claims)        | (PostgreSQL)     |
           v                             +------------------+
  +------------------+
  | Microservice B   |
  | (validates JWT   |    +------------------+
  |  locally)        |    | Token Revocation  |
  +------------------+    | Store (Redis)     |
                          +------------------+

  +------------------+
  | JWKS Endpoint    |  Public keys for JWT verification.
  | (/.well-known/   |  Services fetch & cache these keys.
  | jwks.json)       |
  +------------------+
```

**Key design decisions:**
- **Centralized IdP** issues tokens; **decentralized validation** (each service validates JWTs locally using cached public keys).
- **No network call per request** for token validation — just a local cryptographic signature check.
- **Redis** for fast token revocation checks (blacklist).

---

## 10.6 Step 5: OAuth 2.0 — The Industry Standard

OAuth 2.0 is a **delegation protocol** — it lets a user grant a third-party application limited access to their resources without sharing their password.

```
OAUTH 2.0 ROLES

  Resource Owner:  The user who owns the data.
  Client:          The application requesting access (web app, mobile app,
                   third-party service).
  Authorization    The server that authenticates the user and issues tokens
  Server (AS):     (our IdP).
  Resource Server: The API that holds the user's data (our microservices).
```

**Grant types (flows):**

```
  Grant type                | When to use                    | Security level
  --------------------------|--------------------------------|---------------
  Authorization Code        | Web apps with a backend.       | High (most
  (+ PKCE)                  | The standard flow.             | secure).
  Authorization Code + PKCE | SPAs, mobile apps (public      | High (replaces
                            | clients with no secret).       | implicit flow).
  Client Credentials        | Server-to-server (no user).    | High (machine
                            | Service accounts.              | identity).
  Resource Owner Password   | Legacy; user gives password    | Low (avoid).
  (ROPC)                    | directly to client. Avoid.     |
  Device Code               | Smart TVs, CLI tools (no       | Medium.
                            | browser on device).            |
```

**Authorization Code flow (the standard):**

```
  1. Client redirects user to Authorization Server:
     GET /authorize?response_type=code&client_id=X&redirect_uri=Y&scope=Z&state=S

  2. User authenticates (login + MFA) on the AS.

  3. AS redirects back to client with an authorization code:
     302 Redirect to Y?code=AUTH_CODE&state=S

  4. Client exchanges code for tokens (server-side, with client_secret):
     POST /token
       grant_type=authorization_code
       code=AUTH_CODE
       client_id=X
       client_secret=SECRET
       redirect_uri=Y

  5. AS responds with:
     {
       "access_token": "eyJhbGci...",
       "token_type": "Bearer",
       "expires_in": 900,
       "refresh_token": "dGhpcyBpcyBh..."
     }

  6. Client uses access_token to call Resource Server:
     GET /api/resource
     Authorization: Bearer eyJhbGci...
```

**PKCE (Proof Key for Code Exchange):**
```
  Added for public clients (SPAs, mobile) that cannot keep a client_secret.

  1. Client generates:
     code_verifier = random_string(43-128 chars)
     code_challenge = BASE64URL(SHA256(code_verifier))

  2. Step 1 includes: &code_challenge=X&code_challenge_method=S256

  3. Step 4 includes: &code_verifier=original_string

  4. AS verifies: SHA256(code_verifier) == code_challenge.
     This proves the same client that started the flow is finishing it.
```

---

## 10.7 Step 6: OpenID Connect (OIDC) — Identity on Top of OAuth

OAuth 2.0 is about **authorization** (access to resources). It does not define how to get user **identity**. OIDC adds that layer.

```
  OAuth 2.0 alone:
    "This token grants access to /api/photos with scope=read."
    But WHO is the user? OAuth does not say.

  OIDC adds:
    An ID Token (JWT) with user identity claims:
    {
      "sub": "user_12345",
      "name": "Prakash Ranjan",
      "email": "prakash@example.com",
      "iss": "https://auth.example.com",
      "aud": "client_app_id",
      "iat": 1717200000,
      "exp": 1717203600
    }

    Plus a /userinfo endpoint for additional profile data.
```

```
  OIDC SCOPES

  Scope      | What it adds to the ID token
  -----------|-----------------------------------
  openid     | Required. Enables OIDC.
  profile    | name, family_name, picture, etc.
  email      | email, email_verified.
  phone      | phone_number, phone_number_verified.
  address    | Postal address.
```

**Key point:** in a modern system, you use **OIDC for authentication** (get user identity via ID token) and **OAuth 2.0 for authorization** (get access token for API access). They work together.

---

## 10.8 Step 7: JWT Internals — Structure, Signing & Validation

A JWT (JSON Web Token) is a compact, URL-safe token with three parts:

```
JWT STRUCTURE

  Header.Payload.Signature

  eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.
  eyJzdWIiOiJ1c2VyXzEyMzQ1IiwibmFtZSI6IlByYWthc2giLCJyb2xlIjoiYWRtaW4i
  LCJpYXQiOjE3MTcyMDAwMDAsImV4cCI6MTcxNzIwMDkwMH0.
  <signature_bytes>
```

```
  HEADER (Base64URL decoded):
  {
    "alg": "RS256",        // Signing algorithm.
    "typ": "JWT",
    "kid": "key-2024-06"   // Key ID (which public key to use for verification).
  }

  PAYLOAD (Base64URL decoded — the "claims"):
  {
    "sub": "user_12345",       // Subject (user ID).
    "name": "Prakash",
    "email": "p@example.com",
    "role": "admin",           // Custom claim for authorization.
    "permissions": ["read", "write", "delete"],
    "iss": "https://auth.example.com",  // Issuer.
    "aud": "https://api.example.com",   // Audience (intended recipient).
    "iat": 1717200000,                  // Issued at (Unix timestamp).
    "exp": 1717200900,                  // Expires at (15 minutes later).
    "jti": "unique-token-id-abc"        // JWT ID (for revocation tracking).
  }

  SIGNATURE:
  RS256(
    base64UrlEncode(header) + "." + base64UrlEncode(payload),
    private_key
  )
```

**Signing algorithms:**

```
  Algorithm | Type        | Key                          | Use case
  ----------|-------------|------------------------------|---------------------
  HS256     | Symmetric   | Shared secret (both sides    | Simple, single-
            |             | know the same key).          | service systems.
  RS256     | Asymmetric  | Private key signs; public    | Microservices (any
            |             | key verifies.                | service can verify
            |             |                              | without the secret).
  ES256     | Asymmetric  | ECDSA. Smaller keys, faster. | Modern, preferred
            |             |                              | for performance.
```

**Why RS256/ES256 for microservices:** the IdP keeps the **private key** secret and signs tokens. Every microservice has the **public key** (fetched from the JWKS endpoint) and can verify tokens locally — no network call to the IdP.

**JWT validation checklist (every service, every request):**
```
  1. Decode the header; find the "kid" (key ID).
  2. Fetch the matching public key from cached JWKS.
  3. Verify the signature (cryptographic check).
  4. Check "exp" — is the token expired?
  5. Check "iss" — is it from our trusted IdP?
  6. Check "aud" — is this token meant for this service?
  7. Check "nbf" (not before) if present.
  8. Check revocation (optional; see next section).
  9. Extract claims (role, permissions) for authorization.
```

---

## 10.9 Step 8: Token Lifecycle — Issuance, Refresh & Revocation

```
TOKEN TYPES

  Token          | Lifetime    | Purpose                      | Storage
  ---------------|-------------|------------------------------|------------------
  Access Token   | Short       | Authorize API requests.      | Memory (JS var),
  (JWT)          | (5-15 min)  | Sent with every request.     | NOT localStorage.
  Refresh Token  | Long        | Get a new access token       | HttpOnly Secure
                 | (7-30 days) | without re-login.            | cookie, or secure
                 |             |                              | server-side storage.
  ID Token       | Short       | User identity (OIDC).        | Used once at login;
  (JWT)          | (5-15 min)  | Not sent to APIs.            | not stored long-term.
```

**Refresh flow:**
```
  1. Access token expires (after 15 min).
  2. Client sends refresh token to IdP:
     POST /token
       grant_type=refresh_token
       refresh_token=dGhpcyBpcyBh...
       client_id=X

  3. IdP validates refresh token (exists, not revoked, not expired).
  4. IdP issues a NEW access token + optionally a NEW refresh token
     (refresh token rotation).
  5. Client uses new access token for API calls.
```

**Refresh token rotation:**
```
  Each refresh gives a NEW refresh token and invalidates the old one.

  Why: if an attacker steals a refresh token and uses it, the real user's
  next refresh will fail (old token invalidated) -> triggers alert / re-login.
  This limits the window of a stolen refresh token.
```

**Revocation:**
```
  Problem: JWTs are stateless. Once issued, they are valid until expiry.
  You cannot "un-sign" a token. So how do you revoke access?

  Solutions:
  1. Short-lived access tokens (5-15 min):
     The most effective approach. Even if stolen, the token expires quickly.

  2. Token blacklist (Redis):
     On logout / password change / compromise, add the JWT's "jti" to a
     Redis set with TTL = remaining token lifetime.
     Every service checks: is this jti blacklisted? (fast Redis lookup).

  3. Refresh token revocation:
     Delete the refresh token from the database. The user cannot get new
     access tokens. Existing access tokens expire within minutes.

  4. Version / epoch in claims:
     Store a "token_version" per user. On password change, increment it.
     Services compare JWT's version with the stored version.
     Requires one DB/cache lookup per request.

  Recommended: short-lived access tokens (15 min) + refresh token revocation
  + optional jti blacklist for immediate revocation of compromised tokens.
```

---

## 10.10 Step 9: Session Management — Stateful vs. Stateless

```
  Approach           | How it works                     | Pros / Cons
  -------------------|----------------------------------|------------------------
  Stateful sessions  | Server stores session in Redis   | + Easy revocation.
  (traditional)      | or DB. Client has a session ID   | + Server controls
                     | cookie.                          |   session state.
                     |                                  | - Network call per
                     |                                  |   request (lookup).
                     |                                  | - Session store is
                     |                                  |   a scaling bottleneck.
  Stateless tokens   | JWT contains all claims. Server  | + No server-side
  (JWT)              | validates locally (signature +   |   storage.
                     | expiry).                         | + Scales infinitely.
                     |                                  | - Hard to revoke.
                     |                                  | - Token size grows
                     |                                  |   with claims.
  Hybrid             | Short-lived JWT for API auth +   | + Best of both worlds.
                     | server-side refresh token store  | + Revocable via
                     | for session management.          |   refresh token.
                     |                                  | - More complexity.
```

**Recommended:** the **hybrid approach** is industry standard:
- **Access token (JWT):** short-lived, stateless, validated locally. No server lookup.
- **Refresh token:** stored server-side (Redis/DB), long-lived, revocable. This is where you manage the "session."
- **Logout:** delete the refresh token. Access token expires within minutes.

---

## 10.11 Step 10: Authorization Models — RBAC, ABAC & Policy Engines

**RBAC (Role-Based Access Control):**
```
  Assign roles to users; assign permissions to roles.

  User -> Role -> Permissions

  Example:
    User "Prakash" -> Role "editor" -> Permissions: [read, write]
    User "Admin"   -> Role "admin"  -> Permissions: [read, write, delete, manage_users]

  Implementation:
    JWT claim: { "role": "editor" }
    Service checks: if (user.role == "editor" && action == "write") -> allow.

  Pros: simple, well-understood, easy to audit.
  Cons: role explosion if fine-grained control is needed.
```

**ABAC (Attribute-Based Access Control):**
```
  Access decisions based on attributes of the user, resource, action, and context.

  Policy: ALLOW if
    user.department == "engineering" AND
    resource.classification != "top_secret" AND
    action == "read" AND
    context.time.hour BETWEEN 9 AND 18 AND
    context.ip IN corporate_network

  More flexible than RBAC but more complex to implement and audit.
```

**Policy engines (externalized authorization):**
```
  Instead of hard-coding authorization in every service, use a centralized
  policy engine:

  Service -> "Can user X do action Y on resource Z?" -> Policy Engine -> YES/NO

  Tools:
    - OPA (Open Policy Agent): Rego language, sidecar or library.
    - AWS Cedar: policy language by Amazon.
    - Casbin: library-based, multiple languages.
    - Zanzibar (Google) / SpiceDB: relationship-based access control (ReBAC).

  Benefits:
    - Policies are code (version-controlled, testable).
    - Decoupled from services (change policy without redeploying).
    - Consistent enforcement across all services.
```

```
AUTHORIZATION IN MICROSERVICES

  Option A: Each service checks claims from JWT locally.
    Simple, fast, but policies are scattered across services.

  Option B: API Gateway enforces coarse authorization (role check).
    Services enforce fine-grained authorization internally.

  Option C: Policy engine sidecar (OPA) at each service.
    Centralized policy definition, distributed enforcement.
    Best for complex authorization requirements.

  Recommended: Gateway does coarse AuthZ (valid token, basic role).
  Each service does fine-grained AuthZ (resource-level permissions).
```

---

## 10.12 Step 11: API Gateway & Middleware Enforcement

```
API GATEWAY AUTH FLOW

  Client -> API Gateway -> Microservice

  Gateway responsibilities:
    1. TLS termination (HTTPS).
    2. Extract Bearer token from Authorization header.
    3. Validate JWT signature (using cached JWKS public key).
    4. Check token expiry.
    5. Verify issuer and audience.
    6. (Optional) Check token blacklist in Redis.
    7. Coarse authorization: does this role have access to this route?
    8. Forward request + decoded claims to downstream service.

  If validation fails:
    401 Unauthorized (bad/expired token).
    403 Forbidden (valid token, insufficient permissions).
```

```
MIDDLEWARE PATTERN (per-service)

  @Component
  public class JwtAuthFilter extends OncePerRequestFilter {

      @Override
      protected void doFilterInternal(request, response, chain) {
          String token = extractBearerToken(request);
          if (token == null) { send401(response); return; }

          Claims claims = validateJwt(token);  // Local, no network call.
          if (claims == null) { send401(response); return; }

          SecurityContext ctx = buildSecurityContext(claims);
          SecurityContextHolder.setContext(ctx);

          chain.doFilter(request, response);
      }
  }

  // Then in controllers:
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/users/{id}")
  public void deleteUser(@PathVariable Long id) { ... }
```

---

## 10.13 Step 12: Security Hardening & Common Attacks

```
  Attack                      | How it works                   | Defense
  ----------------------------|--------------------------------|---------------------
  Token theft (XSS)           | Attacker injects JS that reads | Do NOT store tokens
                              | token from localStorage.       | in localStorage. Use
                              |                                | HttpOnly cookies.
  CSRF (Cross-Site Request    | Attacker tricks user's browser | SameSite=Strict
  Forgery)                    | into sending a request with    | cookie attribute;
                              | the session cookie.            | CSRF token.
  Credential stuffing         | Attacker tries leaked          | Rate limit login;
                              | username/password combos.      | MFA; CAPTCHA.
  Brute force                 | Attacker guesses passwords.    | Account lockout
                              |                                | after N attempts;
                              |                                | rate limit; MFA.
  Token replay                | Attacker reuses a captured     | Short-lived tokens;
                              | token.                         | TLS everywhere;
                              |                                | token binding.
  JWT algorithm confusion     | Attacker changes "alg" to      | Always verify alg
  (alg=none / HS256 with      | "none" or HS256 using the      | on server side;
  public key as secret)       | public key as HMAC secret.     | reject "none"; use
                              |                                | allowlist of algs.
  Refresh token theft          | Attacker steals refresh token. | Refresh token
                              |                                | rotation; bind to
                              |                                | device fingerprint.
  Privilege escalation        | User modifies JWT claims       | Signature prevents
                              | (e.g., changes role to admin). | tampering. Always
                              |                                | verify signature.
  Open redirect               | Attacker manipulates           | Validate redirect
                              | redirect_uri in OAuth flow.    | URIs against
                              |                                | whitelist.
```

**Password storage:**
```
  NEVER store plaintext passwords.

  Use: bcrypt, scrypt, or Argon2id (memory-hard, slow by design).

  Salting: each password gets a unique random salt (bcrypt includes this).

  Example:
    stored_hash = bcrypt("user_password", cost=12)
    // $2a$12$LJ3m4ys3Lk0hZ3YBxJYq8e.dKv6Oe7Mx.Gk3F2xQa9iB8p6W1YZWK

  On login:
    bcrypt.verify("user_password", stored_hash) -> true / false.
```

**Token storage best practices:**
```
  Client type       | Access token storage        | Refresh token storage
  ------------------|-----------------------------|--------------------------
  Web (SPA)         | In-memory JS variable.      | HttpOnly, Secure,
                    | NOT localStorage.           | SameSite=Strict cookie.
  Mobile (native)   | Secure storage (Keychain    | Secure storage (Keychain
                    | on iOS, Keystore on         | on iOS, Keystore on
                    | Android).                   | Android).
  Server-to-server  | Environment variable or     | N/A (use client
                    | secrets manager (Vault).    | credentials grant).
```

---

## 10.14 Step 13: Scaling Authentication (Multi-Region, SSO, Federation)

```
SCALING THE IDP

  Challenge                         | Solution
  ----------------------------------|--------------------------------------------
  High login QPS (50K+/sec)         | Stateless IdP (horizontal scaling behind
                                    | LB). Token issuance is CPU-bound (signing).
  JWT validation at scale           | Every service validates locally (no IdP
                                    | call). Cache JWKS public keys (5-15 min TTL).
  Refresh token storage             | Redis cluster with replication. Partition
                                    | by user_id for even distribution.
  Multi-region                      | Deploy IdP in each region. Sync user DB
                                    | across regions (CockroachDB, DynamoDB
                                    | Global Tables). JWKS keys are global.
  Token revocation at scale         | Redis-based jti blacklist replicated
                                    | across regions. Short TTL = small set.
```

**SSO (Single Sign-On):**
```
  Problem: user logs into App A. They should not have to log in again for App B.

  Solution: centralized IdP session.
    1. User logs into App A -> redirected to IdP -> authenticates -> gets token.
    2. IdP sets a session cookie (IdP domain).
    3. User goes to App B -> redirected to IdP -> IdP sees existing session
       cookie -> issues token for App B without re-login.

  Protocols:
    - OIDC: standard for web SSO.
    - SAML 2.0: enterprise SSO (XML-based, older but widely used).

  Providers: Okta, Auth0, Keycloak (self-hosted), Azure AD, AWS Cognito.
```

**Federation (trusting external identity providers):**
```
  Your system trusts tokens from external IdPs (Google, Microsoft, corporate AD).

  Flow:
    1. User clicks "Log in with Google."
    2. OAuth 2.0 / OIDC flow with Google as the Authorization Server.
    3. Your IdP receives an ID token from Google with the user's identity.
    4. Your IdP maps Google identity to a local user account (or creates one).
    5. Your IdP issues YOUR access token (with your claims, roles).
    6. Downstream services only see your token — they do not know about Google.

  This decouples your internal auth from external providers.
```

---

## 10.15 Step 14: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  JWT size vs. claims              | More claims in the JWT = more data per
                                   | request (header overhead). Keep JWTs lean;
                                   | put only essential claims. Fetch details
                                   | from a service if needed.
  Short token TTL vs. UX           | Shorter TTL = more secure but more refresh
                                   | requests. 5-15 min is the sweet spot.
  Stateless JWT vs. revocation     | Pure stateless = cannot revoke until expiry.
                                   | Add jti blacklist or accept the risk with
                                   | short TTLs.
  Symmetric (HS256) vs.            | HS256 requires shared secret (leaks if one
  asymmetric (RS256/ES256)         | service is compromised). RS256/ES256 is
                                   | safer for microservices: only IdP has the
                                   | private key.
  Centralized vs. distributed      | Centralized IdP is a single point of
  IdP                              | failure. Mitigate with multi-region
                                   | deployment, replicas, and cached JWKS.
  RBAC vs. ABAC                    | RBAC is simpler but can lead to role
                                   | explosion. ABAC is flexible but complex
                                   | to audit. Start with RBAC; add ABAC when
                                   | needed.
  Build vs. buy IdP                | Building an IdP is hard and error-prone.
                                   | Use Keycloak, Auth0, Okta, Cognito unless
                                   | you have a strong reason to build your own.
  Storing tokens in localStorage   | Accessible to XSS. Use HttpOnly cookies
                                   | for refresh tokens and in-memory for
                                   | access tokens.
  Not validating audience (aud)    | A token for Service A can be used on
                                   | Service B. Always check "aud".
  Not rotating signing keys        | If the private key is compromised, all
                                   | tokens are forgeable. Rotate keys
                                   | periodically; JWKS supports multiple kids.
```

---

## 10.16 Problem 10 Summary

- **Authentication proves identity; authorization enforces permissions.** They are separate concerns with separate infrastructure. Do not conflate them.
- **OAuth 2.0** is the industry-standard delegation protocol. Use the **Authorization Code + PKCE** flow for web and mobile apps; **Client Credentials** for server-to-server.
- **OIDC** adds an identity layer on top of OAuth 2.0, providing an **ID token** with user claims (sub, name, email).
- **JWT** is a self-contained, signed token with header, payload (claims), and signature. Use **RS256 or ES256** (asymmetric) for microservices so any service can verify without the signing secret.
- **Token lifecycle:** short-lived access tokens (5-15 min) + long-lived refresh tokens (stored server-side) + refresh token rotation. Revocation via refresh token deletion + optional jti blacklist in Redis.
- **Hybrid session management** is the industry standard: stateless JWT for API auth, server-side refresh token for session control.
- **RBAC** for most systems (roles -> permissions); **ABAC / policy engines** (OPA, Cedar) for complex, fine-grained access control. Gateway does coarse AuthZ; services do fine-grained AuthZ.
- **API Gateway** handles TLS, JWT validation, coarse authorization, and forwards decoded claims to services. Each service does resource-level permission checks.
- **Security hardening:** never store tokens in localStorage (use HttpOnly cookies); defend against XSS, CSRF, credential stuffing, JWT algorithm confusion, open redirect; store passwords with bcrypt/Argon2id; rotate signing keys.
- **Scaling:** stateless IdP scales horizontally; JWT validation is local (no IdP call); JWKS keys cached at each service; refresh tokens in Redis cluster; multi-region IdP with synced user DB.
- **SSO** via centralized IdP session + OIDC; **federation** by trusting external IdPs (Google, Azure AD) and mapping to internal identities.
- **Build vs. buy:** use Keycloak, Auth0, Okta, or Cognito unless you have a compelling reason to build your own IdP. Auth is security-critical and hard to get right.

---

# Problem 11: Design a Kafka-Based System

## 11.1 Problem Statement

> Design an event-driven architecture using Apache Kafka for a large-scale e-commerce platform. The system must handle order events, inventory updates, payment processing, notifications, and analytics — all flowing through Kafka. Walk through topic design, partitioning, producer/consumer patterns, delivery guarantees, schema evolution, failure handling, and operational concerns.

This question tests:
- **Kafka internals knowledge** — brokers, partitions, replication, consumer groups, offsets.
- **System design judgment** — when to use Kafka vs. alternatives, topic modeling, partition key choices.
- **Reliability thinking** — exactly-once semantics, idempotency, dead letter queues, data loss prevention.
- **Operational maturity** — monitoring, scaling, schema evolution, multi-DC deployment.

It is one of the most common HLD interview topics because Kafka sits at the center of modern distributed systems.

---

## 11.2 Step 1: Clarifying Questions & Scope

```
  Question                                  | Assumption
  ------------------------------------------|----------------------------------
  What events flow through the system?      | Orders, payments, inventory,
                                            | notifications, analytics, user
                                            | activity.
  Event throughput?                         | 500K events/sec at peak.
  Acceptable end-to-end latency?            | < 2 seconds for critical events
                                            | (order, payment). < 30 sec for
                                            | analytics.
  Delivery guarantee needed?                | At-least-once for most; exactly-
                                            | once for payments/inventory.
  How many consuming services?              | 20+ microservices.
  Data retention?                           | 7 days hot, 90 days cold
                                            | (tiered storage / S3).
  Multi-datacenter / multi-region?          | Yes, active-active in 2 regions.
  Schema management?                        | Avro + Schema Registry.
  Existing tech stack?                      | Java/Spring Boot microservices,
                                            | PostgreSQL, Redis, Kubernetes.
```

---

## 11.3 Step 2: Why Kafka? (When to Choose It)

```
  Requirement                      | Kafka fit?  | Alternative
  ---------------------------------|-------------|-----------------------------
  High throughput event streaming   | Excellent   | Pulsar, Redpanda.
  (100K+ events/sec)               |             |
  Durable, replayable log          | Excellent   | Pulsar (similar model).
  Multiple consumers per event     | Excellent   | RabbitMQ (needs exchange
  (fan-out)                        |             | configuration).
  Strict ordering per key          | Excellent   | SQS FIFO (limited
                                   |             | throughput).
  Simple task queue                | Overkill    | RabbitMQ, SQS.
  (point-to-point, low volume)     |             |
  Request-reply / RPC              | Poor fit    | gRPC, REST, RabbitMQ.
  Real-time stream processing      | Good        | Kafka Streams, Flink.
  (aggregation, windowing)         |             |
  Event sourcing / audit log       | Excellent   | EventStoreDB.
  Sub-millisecond latency          | Not ideal   | Redis Streams, ZeroMQ.
```

**Rule of thumb:** choose Kafka when you need a **durable, high-throughput, replayable event log** with **multiple independent consumers**. Do not use it as a simple task queue or RPC mechanism.

---

## 11.4 Step 3: Kafka Architecture — Core Concepts

```
KAFKA CLUSTER

  +--------+    +--------+    +--------+
  |Broker 1|    |Broker 2|    |Broker 3|
  |        |    |        |    |        |
  | P0(L)  |    | P0(F)  |    | P0(F)  |   <- Topic "orders", Partition 0
  | P1(F)  |    | P1(L)  |    | P1(F)  |   <- Topic "orders", Partition 1
  | P2(F)  |    | P2(F)  |    | P2(L)  |   <- Topic "orders", Partition 2
  +--------+    +--------+    +--------+
       L = Leader, F = Follower (replica)

  +---------------------+
  | ZooKeeper / KRaft    |  Metadata management, leader election.
  | (KRaft in new Kafka) |  KRaft = ZooKeeper-less mode (Kafka 3.3+).
  +---------------------+

  +---------------------+
  | Schema Registry     |  Schema storage (Avro, Protobuf, JSON Schema).
  | (Confluent)         |  Compatibility enforcement.
  +---------------------+
```

**Core concepts:**

```
  Concept          | Definition
  -----------------|----------------------------------------------------------
  Topic            | A named category/feed of events (like a database table).
                   | Example: "orders", "payments", "inventory-updates".
  Partition        | A topic is split into N partitions for parallelism.
                   | Each partition is an ordered, immutable append-only log.
  Offset           | A sequential ID for each message within a partition.
                   | Consumers track their offset to know where they are.
  Broker           | A Kafka server. The cluster has multiple brokers.
  Leader/Follower  | Each partition has 1 leader (handles reads/writes) and
                   | N-1 followers (replicas for fault tolerance).
  Replication      | replication.factor=3 means 3 copies of each partition.
  Factor           | Tolerates N-1 broker failures (2 with RF=3).
  ISR              | In-Sync Replicas. Followers that are caught up with the
                   | leader. Only ISR members can become the new leader.
  Producer         | Publishes messages to a topic.
  Consumer         | Reads messages from a topic.
  Consumer Group   | A group of consumers that divide partitions among
                   | themselves for parallel processing.
  Segment          | Partitions are stored as segment files on disk.
                   | Old segments are deleted/compacted per retention policy.
```

```
PARTITION AS AN APPEND-ONLY LOG

  Partition 0:
  +-----+-----+-----+-----+-----+-----+-----+-----+
  |  0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  | <- Offsets
  +-----+-----+-----+-----+-----+-----+-----+-----+
  ^                                             ^
  oldest                                     newest
  (may be deleted by retention)          (latest write)

  Writes append to the end. Reads are sequential.
  This is why Kafka is fast: sequential disk I/O + OS page cache.
```

---

## 11.5 Step 4: Topic Design & Partitioning Strategy

**Topic design principles:**

```
  Principle                         | Guidance
  ----------------------------------|------------------------------------------
  One topic per event type          | "orders", "payments", "inventory-updates",
                                    | "notifications", "user-activity".
  Avoid mega-topics                 | Do NOT put all events in one topic.
                                    | Different consumers need different events.
  Separate commands from events     | "place-order" (command) vs. "order-placed"
                                    | (event/fact). Events are past tense.
  Consider consumer needs           | If two services need different retention
                                    | or throughput, use separate topics.
  Use a naming convention           | <domain>.<entity>.<action>
                                    | e.g., "ecommerce.orders.placed"
                                    | e.g., "ecommerce.inventory.updated"
```

**Partition count:**

```
  How many partitions?

  Rule of thumb:
    target_throughput / throughput_per_partition = partition_count

  Example:
    - Target: 100K msgs/sec for "orders" topic.
    - One partition can handle ~10K msgs/sec (with typical message size).
    - 100K / 10K = 10 partitions.

  Guidelines:
    - Start with 2x your current need (room to grow).
    - More partitions = more parallelism BUT more overhead
      (file handles, memory, rebalance time).
    - You CAN increase partitions later but CANNOT decrease them.
    - Too many partitions (1000+) increases leader election time
      and end-to-end latency.
```

**Partition key selection (critical for ordering):**

```
  Key                          | Ordering guarantee            | Use case
  -----------------------------|-------------------------------|-------------------
  order_id                     | All events for one order go   | Order lifecycle:
                               | to same partition -> ordered. | placed, paid,
                               |                               | shipped, delivered.
  user_id                      | All events for one user go    | User activity
                               | to same partition -> ordered. | tracking, session.
  inventory_item_id            | All updates for one SKU go    | Stock level changes.
                               | to same partition -> ordered. |
  null (no key)                | Round-robin across partitions.| When ordering does
                               | No ordering guarantee.        | not matter (logs).
  payment_id                   | All events for one payment    | Payment lifecycle.
                               | go to same partition.         |

  CRITICAL: messages with the same key always go to the same partition
  (via hash(key) % partition_count). This gives per-key ordering.
  You do NOT get global ordering across partitions.
```

---

## 11.6 Step 5: Producers — Delivery Guarantees & Best Practices

```
PRODUCER DELIVERY SEMANTICS

  acks setting  | Meaning                           | Trade-off
  --------------|-----------------------------------|------------------------
  acks=0        | Fire and forget. Do not wait for  | Fastest. Data loss
                | broker acknowledgment.            | possible.
  acks=1        | Wait for leader to write to its   | Good throughput.
                | local log. Do not wait for        | Data loss if leader
                | replicas.                         | crashes before replication.
  acks=all (-1) | Wait for ALL in-sync replicas to  | Slowest but no data loss
                | write. The gold standard.         | (with min.insync.replicas
                |                                   | >= 2).
```

```
RECOMMENDED PRODUCER CONFIG (production)

  acks = all
  min.insync.replicas = 2          (at least 2 replicas must ack)
  retries = Integer.MAX_VALUE      (retry indefinitely on transient errors)
  enable.idempotence = true        (prevent duplicate messages on retry)
  max.in.flight.requests = 5       (with idempotence, safe for ordering)
  compression.type = lz4           (good balance of speed and ratio)
  linger.ms = 5                    (batch messages for 5 ms for throughput)
  batch.size = 64KB                (batch size before sending)
  buffer.memory = 64MB             (total producer buffer)
```

```
IDEMPOTENT PRODUCER

  Problem: if a producer retries a failed send, the broker may have
  actually received it the first time -> duplicate message.

  Solution: enable.idempotence = true
    - Producer assigns a sequence number to each message.
    - Broker deduplicates: if it already has sequence N, it ignores
      the retry.
    - This gives exactly-once semantics at the producer level
      (within a single producer session).

  Combined with acks=all, this ensures:
    - No data loss (all replicas ack).
    - No duplicates (idempotent producer).
```

```
PRODUCER BEST PRACTICES

  1. Always set a partition key for ordered events.
  2. Use acks=all + min.insync.replicas=2 for critical data.
  3. Enable idempotence to prevent duplicates.
  4. Use compression (lz4 or zstd) to reduce network/disk usage.
  5. Batch messages (linger.ms + batch.size) for throughput.
  6. Handle send failures with callbacks and dead-letter logic.
  7. Use async send with callbacks for non-blocking throughput.
  8. Monitor producer metrics: record-send-rate, record-error-rate,
     request-latency-avg.
```

---

## 11.7 Step 6: Consumers & Consumer Groups

```
CONSUMER GROUP MODEL

  Topic "orders" (3 partitions):

  Consumer Group "order-service":
    Consumer A -> Partition 0
    Consumer B -> Partition 1
    Consumer C -> Partition 2

  Consumer Group "analytics-service":
    Consumer D -> Partition 0, 1
    Consumer E -> Partition 2

  Key rules:
  - Each partition is assigned to exactly ONE consumer within a group.
  - Adding more consumers than partitions = idle consumers.
  - Different groups get the SAME messages independently (fan-out).
  - Consumers track their offset per partition per group.
```

```
OFFSET MANAGEMENT

  Offset commit strategies:

  Strategy              | How it works                     | Risk
  ----------------------|----------------------------------|-------------------
  Auto-commit           | Kafka commits offsets            | At-least-once.
  (enable.auto.commit   | periodically (default: 5 sec).   | If consumer crashes
  = true)               |                                  | after commit but
                        |                                  | before processing,
                        |                                  | messages are lost.
  Manual commit         | App commits after processing.    | At-least-once.
  (commitSync /         | If crash before commit,          | May reprocess
  commitAsync)          | messages are redelivered.        | messages on restart.
  Manual + idempotent   | Commit after processing.         | Effectively
  consumer              | Consumer logic is idempotent     | exactly-once
                        | (handles duplicates gracefully). | (at-least-once +
                        |                                  | idempotent handler).

  Recommended: manual commit + idempotent consumer logic.
```

```
CONSUMER REBALANCING

  When a consumer joins or leaves the group, partitions are reassigned.

  Rebalance triggers:
  - Consumer joins the group (scale up).
  - Consumer leaves / crashes (heartbeat timeout).
  - New partitions added to the topic.

  Rebalance strategies:
  - Eager (default): all consumers stop, all partitions reassigned.
    Causes a processing pause.
  - Cooperative (incremental): only affected partitions are reassigned.
    Other partitions keep processing. Use this in production.

  Config:
    partition.assignment.strategy = CooperativeStickyAssignor
    session.timeout.ms = 30000       (detect dead consumer)
    heartbeat.interval.ms = 10000    (heartbeat frequency)
    max.poll.interval.ms = 300000    (max time between polls)
    max.poll.records = 500           (batch size per poll)
```

---

## 11.8 Step 7: Exactly-Once Semantics (EOS)

```
DELIVERY GUARANTEES SPECTRUM

  At-most-once:  Messages may be lost, never duplicated.
                 (acks=0, auto-commit before processing)

  At-least-once: Messages are never lost, may be duplicated.
                 (acks=all, manual commit after processing)

  Exactly-once:  Messages are never lost, never duplicated.
                 (transactional producer + consumer, or
                  at-least-once + idempotent consumer)
```

**Kafka Transactions (built-in EOS):**

```
  Use case: consume from topic A, process, produce to topic B,
  commit offset — all atomically.

  Producer config:
    transactional.id = "order-processor-1"
    enable.idempotence = true  (required for transactions)

  Code pattern:
    producer.initTransactions();

    while (true) {
        records = consumer.poll(Duration.ofMillis(100));
        producer.beginTransaction();
        try {
            for (record : records) {
                // Process and produce result to output topic.
                producer.send(new ProducerRecord("output-topic", result));
            }
            // Commit consumer offsets as part of the transaction.
            producer.sendOffsetsToTransaction(offsets, consumerGroupId);
            producer.commitTransaction();
        } catch (Exception e) {
            producer.abortTransaction();
        }
    }

  Consumer config:
    isolation.level = read_committed
    (Only reads messages from committed transactions.)
```

**Practical EOS (the simpler approach):**

```
  In most real systems, full Kafka transactions are not needed.
  Instead, use at-least-once delivery + idempotent consumer:

  1. Producer: acks=all, enable.idempotence=true.
  2. Consumer: manual commit after processing.
  3. Consumer logic: idempotent.
     - Use a unique event ID (idempotency key).
     - Before processing, check: "Have I already processed this event?"
     - Store the event ID in a database (dedup table) or use
       database upsert (INSERT ... ON CONFLICT DO NOTHING).
     - If already processed, skip.

  This is simpler, works across systems (not just Kafka-to-Kafka),
  and is what most production systems use.
```

---

## 11.9 Step 8: Schema Management & Evolution (Avro, Schema Registry)

```
  Problem: producers and consumers evolve independently. If the producer
  adds a field, old consumers must not break. If a consumer expects a
  field that was removed, it must handle the absence.

  Solution: Schema Registry + Avro (or Protobuf).
```

```
SCHEMA REGISTRY ARCHITECTURE

  Producer                    Schema Registry              Consumer
  --------                    ---------------              --------
  1. Define Avro schema.      Stores schemas with          1. Fetch schema by ID
  2. Register schema          version numbers and          from registry.
     with registry.           compatibility rules.         2. Deserialize message
  3. Serialize message                                     using the schema.
     with schema ID in     +---> Schema v1                 3. Handle new/removed
     the header.           |     Schema v2                 fields gracefully.
  4. Send to Kafka.        |     Schema v3
                           +---------------------+
```

```
AVRO SCHEMA EXAMPLE

  {
    "type": "record",
    "name": "OrderEvent",
    "namespace": "com.example.ecommerce",
    "fields": [
      {"name": "order_id",   "type": "string"},
      {"name": "user_id",    "type": "string"},
      {"name": "amount",     "type": "double"},
      {"name": "currency",   "type": "string", "default": "USD"},
      {"name": "status",     "type": "string"},
      {"name": "timestamp",  "type": "long"}
    ]
  }
```

```
COMPATIBILITY MODES

  Mode                | Rule                              | Use case
  --------------------|-----------------------------------|--------------------
  BACKWARD            | New schema can read old data.      | Default. Consumers
                      | Can add fields with defaults;     | upgrade first.
                      | can remove fields.                |
  FORWARD             | Old schema can read new data.      | Producers upgrade
                      | Can remove fields with defaults;  | first.
                      | can add fields.                   |
  FULL                | Both backward and forward          | Safest for
                      | compatible.                       | independent
                      |                                   | upgrades.
  NONE                | No compatibility check.            | Dangerous. Avoid
                      |                                   | in production.

  Recommended: FULL compatibility. This allows producers and consumers
  to be upgraded independently without breaking each other.
```

**Schema evolution rules:**
```
  Safe changes (FULL compatible):
    - Add a field WITH a default value.
    - Remove a field that HAS a default value.

  Unsafe changes (breaking):
    - Remove a field without a default.
    - Change a field's type.
    - Rename a field.
    - Add a required field without a default.

  Strategy: always add optional fields with defaults.
  Never remove required fields. Use deprecation annotations.
```

---

## 11.10 Step 9: High-Level Architecture (End-to-End Example)

```
E-COMMERCE EVENT-DRIVEN ARCHITECTURE

  +------------------+       +------------------+
  | Order Service    |------>| Topic:           |
  | (Producer)       |       | orders.placed    |
  +------------------+       +--------+---------+
                                      |
                 +--------------------+--------------------+
                 |                    |                    |
                 v                    v                    v
  +------------------+  +------------------+  +------------------+
  | Payment Service  |  | Inventory Service|  | Notification Svc |
  | (Consumer Group) |  | (Consumer Group) |  | (Consumer Group) |
  +--------+---------+  +--------+---------+  +------------------+
           |                      |
           v                      v
  +------------------+  +------------------+
  | Topic:           |  | Topic:           |
  | payments.processed| | inventory.updated|
  +--------+---------+  +------------------+
           |
           v
  +------------------+       +------------------+
  | Order Service    |       | Analytics Service|
  | (Consumer:       |       | (Consumer Group: |
  | update status)   |       | reads ALL topics)|
  +------------------+       +------------------+
                                      |
                                      v
                              +------------------+
                              | Data Warehouse   |
                              | (ClickHouse /    |
                              | Snowflake)       |
                              +------------------+
```

**Event flow for a single order:**
```
  1. User places order -> Order Service publishes to "orders.placed"
     key: order_id.
  2. Payment Service consumes "orders.placed":
     - Processes payment.
     - Publishes to "payments.processed" (key: order_id).
  3. Inventory Service consumes "orders.placed":
     - Reserves inventory.
     - Publishes to "inventory.updated" (key: item_id).
  4. Notification Service consumes "orders.placed":
     - Sends confirmation email to user.
  5. Order Service consumes "payments.processed":
     - Updates order status to PAID.
     - Publishes to "orders.status-changed".
  6. Analytics Service consumes all topics:
     - Aggregates data into the data warehouse.
```

---

## 11.11 Step 10: Data Retention, Compaction & Tiered Storage

```
RETENTION POLICIES

  Policy               | How it works                     | Use case
  ----------------------|----------------------------------|---------------------
  Time-based            | Delete segments older than       | Event streams:
  (retention.ms)        | retention.ms. Default: 7 days.   | orders, payments.
  Size-based            | Delete oldest segments when      | Bounded storage.
  (retention.bytes)     | total size exceeds limit.        |
  Compaction            | Keep only the LATEST value       | Stateful data:
  (cleanup.policy=      | for each key. Delete older       | user profiles,
  compact)              | versions of the same key.        | inventory levels,
                        |                                  | config.
  Compact + delete      | Compact AND delete old segments. | Compacted data
  (cleanup.policy=      |                                  | with a retention
  compact,delete)       |                                  | window.
```

```
LOG COMPACTION

  Before compaction:
  +-----+-----+-----+-----+-----+-----+-----+-----+
  | K:A | K:B | K:A | K:C | K:B | K:A | K:C | K:B |
  | V:1 | V:1 | V:2 | V:1 | V:2 | V:3 | V:2 | V:3 |
  +-----+-----+-----+-----+-----+-----+-----+-----+

  After compaction:
  +-----+-----+-----+
  | K:A | K:C | K:B |
  | V:3 | V:2 | V:3 |
  +-----+-----+-----+
  (Only latest value per key is kept.)

  Use case: "inventory-levels" topic with key=item_id.
  After compaction, you have the latest level for every item.
  New consumers can bootstrap state from this compacted topic.
```

```
TIERED STORAGE (Kafka 3.6+)

  Problem: keeping 90 days of data on broker disks is expensive.
  Solution: tiered storage offloads old segments to S3/GCS/Azure Blob.

  +--------+    +--------+    +--------+
  | Broker |    | Broker |    | Broker |
  | (hot   |    | (hot   |    | (hot   |
  |  data  |    |  data  |    |  data  |
  | 7 days)|    | 7 days)|    | 7 days)|
  +---+----+    +---+----+    +---+----+
      |             |             |
      +-------------+-------------+
                    |
                    v
            +---------------+
            | S3 / GCS      |
            | (cold data    |
            | 8-90 days)    |
            +---------------+

  Consumers read recent data from brokers (fast) and old data
  from object storage (slower but cheap).
```

---

## 11.12 Step 11: Monitoring, Alerting & Operations

```
KEY KAFKA METRICS

  Metric                            | Alert threshold       | What it means
  ----------------------------------|----------------------|--------------------
  Under-replicated partitions       | > 0                  | A replica is behind
                                    |                      | the leader. Risk of
                                    |                      | data loss if leader
                                    |                      | fails.
  ISR shrink rate                   | > 0 sustained        | Replicas falling out
                                    |                      | of sync. Disk / net
                                    |                      | issue.
  Consumer lag                      | > threshold          | Consumer is behind.
  (records-lag-max)                 | (e.g., > 10K)        | Processing too slow
                                    |                      | or consumer is down.
  Consumer group rebalance rate     | Frequent             | Consumers joining /
                                    |                      | leaving too often.
  Request latency (produce/fetch)   | p99 > threshold      | Broker overloaded.
  Disk usage                        | > 80%                | Need more storage
                                    |                      | or shorter retention.
  Active controller count           | != 1                 | No controller (bad)
                                    |                      | or split-brain.
  Unclean leader election rate      | > 0                  | Non-ISR replica
                                    |                      | became leader.
                                    |                      | Possible data loss.
  Producer error rate               | > 0                  | Producers failing
                                    |                      | to send messages.
```

```
MONITORING STACK

  Kafka JMX metrics -> Prometheus (scrape) -> Grafana (dashboard)
                                            -> AlertManager (alerts)

  Consumer lag: Burrow (LinkedIn) or Kafka Lag Exporter (Prometheus).

  Essential dashboards:
  1. Cluster health: broker count, controller, ISR, under-replicated.
  2. Throughput: messages/sec in and out, bytes/sec in and out.
  3. Consumer lag: per consumer group, per partition.
  4. Latency: produce latency, fetch latency, end-to-end latency.
  5. Disk: usage per broker, segment count, log flush latency.
```

---

## 11.13 Step 12: Failure Handling & Resilience

```
  Failure scenario                  | What happens                  | Mitigation
  ----------------------------------|-------------------------------|-------------------
  Broker crash                      | Leader partitions failover    | RF=3, min.insync
                                    | to ISR replicas. Temporary    | .replicas=2.
                                    | latency spike.                | Rack-aware
                                    |                               | replication.
  Consumer crash                    | Rebalance; partitions         | Cooperative
                                    | reassigned to surviving       | rebalancing.
                                    | consumers.                    | Idempotent
                                    |                               | consumer logic.
  Producer cannot reach broker      | Retries with backoff.         | retries=MAX,
                                    | Messages buffered in          | buffer.memory
                                    | producer buffer.              | large enough.
  Network partition                 | ISR shrinks. If ISR < min     | unclean.leader
                                    | .insync.replicas, producers   | .election=false
                                    | get errors (safe).            | (prevent data
                                    |                               | loss).
  Disk full on broker               | Broker stops accepting        | Monitor disk.
                                    | writes.                       | Retention policy.
                                    |                               | Tiered storage.
  Consumer poison pill              | Bad message crashes consumer  | Dead letter queue.
  (bad message)                     | in a loop.                    | Error handler.
  Schema incompatibility            | Consumer cannot deserialize   | Schema Registry
                                    | message.                      | with FULL compat.
  Consumer too slow                 | Lag grows; data may expire    | Scale consumers.
  (processing bottleneck)           | before consumption.           | Increase retention.
```

**Dead Letter Queue (DLQ) pattern:**

```
  Consumer reads from "orders.placed":
    try {
        process(record);
        commitOffset(record);
    } catch (TransientException e) {
        retry(record, maxRetries=3);
    } catch (PermanentException e) {
        // Cannot process this message. Do not block the queue.
        producer.send("orders.placed.dlq", record);
        commitOffset(record);  // Move past the bad message.
        alert("Message sent to DLQ", record);
    }

  DLQ topic ("orders.placed.dlq"):
    - Holds unprocessable messages.
    - Ops team investigates and reprocesses (or discards).
    - Set long retention (30+ days).
```

---

## 11.14 Step 13: Scaling Kafka in Production

```
SCALING STRATEGIES

  Dimension              | How to scale                     | Considerations
  -----------------------|----------------------------------|-------------------
  Throughput             | Add partitions to the topic.     | Cannot reduce
                         | Add more brokers.                | partitions later.
                         | Add more consumers (up to        | Rebalance cost.
                         | partition count).                |
  Storage                | Add brokers (distribute data).   | Rebalance moves
                         | Enable tiered storage.           | data (costly).
                         | Reduce retention.                |
  Consumer processing    | Add consumers to the group       | Max consumers =
                         | (up to partition count).         | partition count.
                         | Increase max.poll.records.       | Batch processing.
                         | Use parallel processing within   | Ordering concerns.
                         | the consumer.                    |
  Multi-region           | MirrorMaker 2 (MM2) or           | Async replication.
                         | Confluent Cluster Linking.       | Eventual
                         | Active-passive or active-active. | consistency.
  Broker capacity        | Vertical: bigger disks, more     | Diminishing
                         | RAM (OS page cache). Horizontal: | returns on
                         | add brokers + rebalance.         | vertical.
```

```
MULTI-REGION DEPLOYMENT

  Option A: Active-Passive
    Region 1 (Primary): producers write here.
    Region 2 (DR): MirrorMaker 2 replicates topics.
    Failover: switch producers and consumers to Region 2.

  Option B: Active-Active
    Region 1: producers write local topics.
    Region 2: producers write local topics.
    MirrorMaker 2 replicates both ways (with topic prefixes
    to avoid infinite loops: "region1.orders", "region2.orders").
    Consumers read from both local and replicated topics.

  Option C: Confluent Cluster Linking
    Direct broker-to-broker replication. Lower latency than MM2.
    Consumers can failover transparently.

  Trade-off: active-active has lower RTO/RPO but is more complex
  (duplicate detection, conflict resolution).
```

---

## 11.15 Step 14: Common Patterns (CQRS, Event Sourcing, Saga, Dead Letter)

**CQRS (Command Query Responsibility Segregation):**

```
  Write side:
    Commands -> Write Service -> PostgreSQL (source of truth)
                              -> Kafka event ("order-placed")

  Read side:
    Kafka event -> Read Service -> Elasticsearch / Redis / Materialized View

  Queries go to the read side (optimized for reads).
  Writes go to the write side (optimized for writes).
  Kafka bridges the two asynchronously.

  Benefit: scale reads and writes independently. Read model can be
  denormalized for fast queries.
  Cost: eventual consistency between write and read sides.
```

**Event Sourcing:**

```
  Instead of storing current state, store the sequence of events:

  Order aggregate:
    Event 1: OrderPlaced {order_id: 123, items: [...], total: 99.00}
    Event 2: PaymentReceived {order_id: 123, payment_id: 456}
    Event 3: OrderShipped {order_id: 123, tracking: "XYZ"}

  Current state = replay all events from the beginning.

  Kafka as the event store:
    - Topic "orders.events" with cleanup.policy=compact (keep all events).
    - Actually, for event sourcing, use delete policy with long retention
      or a dedicated event store (EventStoreDB).
    - Kafka is a good event bus but not ideal as the sole event store
      (limited querying, no strong consistency per aggregate).
```

**Saga (distributed transaction orchestration):**

```
  Problem: order involves Payment, Inventory, and Shipping services.
  No distributed transaction. Use a Saga.

  Choreography-based Saga (via Kafka):
    1. Order Service -> "orders.placed" ->
    2. Payment Service -> processes -> "payments.processed" ->
    3. Inventory Service -> reserves stock -> "inventory.reserved" ->
    4. Shipping Service -> schedules -> "shipping.scheduled" ->
    5. Order Service -> "orders.completed"

    If Payment fails:
    2b. Payment Service -> "payments.failed" ->
    3b. Order Service -> "orders.cancelled"
        Inventory Service -> compensate (unreserve stock).

  Orchestration-based Saga:
    A central Saga Orchestrator sends commands to each service
    via Kafka and tracks the state machine. Simpler to reason about
    but the orchestrator is a single point of coordination.
```

**Outbox Pattern (reliable event publishing):**

```
  Problem: service writes to DB and publishes to Kafka. If Kafka
  publish fails after DB commit, data is inconsistent.

  Solution: Transactional Outbox.
    1. Service writes to DB AND inserts an event into an "outbox"
       table in the SAME database transaction.
    2. A separate process (Debezium CDC or poller) reads the outbox
       table and publishes to Kafka.
    3. Once published, mark the outbox row as sent.

    This guarantees: if the DB write succeeds, the event WILL be
    published (eventually). Atomic within the DB transaction.

  Debezium CDC (Change Data Capture):
    Reads the database's WAL/binlog and publishes changes to Kafka.
    No polling needed. Near real-time. The most popular approach.
```

---

## 11.16 Step 15: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Too many partitions              | More parallelism but more file handles,
                                   | longer rebalances, higher end-to-end
                                   | latency. Start moderate; increase later.
  Choosing the wrong key           | Wrong partition key -> hot partition (one
                                   | partition gets most traffic) or broken
                                   | ordering. Analyze key distribution.
  Relying on Kafka for RPC         | Kafka is not designed for request-reply.
                                   | Use gRPC/REST for synchronous calls.
  Ignoring consumer lag            | Lag grows silently. Set alerts. If lag
                                   | exceeds retention, messages are LOST.
  Not using Schema Registry        | Schema changes break consumers. Always
                                   | use Avro/Protobuf + Schema Registry with
                                   | compatibility checks.
  Exactly-once misconceptions      | Kafka EOS only works within Kafka (topic
                                   | to topic). For external systems (DB), you
                                   | need idempotent consumers.
  Under-replication ignored        | Under-replicated partitions = data loss
                                   | risk. Alert immediately and investigate.
  unclean.leader.election=true     | Allows data loss. Set to false in
                                   | production. Accept unavailability over
                                   | data loss.
  No dead letter queue             | Poison pill messages block the consumer
                                   | forever. Always implement a DLQ.
  Skipping the outbox pattern      | Direct DB + Kafka writes are not atomic.
                                   | Use the outbox pattern or Debezium CDC.
  Over-engineering with Kafka      | Not every service needs Kafka. Simple
                                   | HTTP calls or a task queue (RabbitMQ/SQS)
                                   | may be simpler and sufficient.
  Not planning for multi-DC        | Retrofitting multi-DC replication is hard.
                                   | Plan topic naming and replication strategy
                                   | early (even if single-DC initially).
```

---

## 11.17 Problem 11 Summary

- **Choose Kafka when you need a durable, high-throughput, replayable event log with multiple independent consumers.** Do not use it for simple task queues or RPC.
- **Core model:** topics are split into partitions; each partition is an ordered, append-only log. Replication (RF=3) provides fault tolerance. Consumer groups provide parallel consumption with fan-out across groups.
- **Topic design:** one topic per event type, use a naming convention (`domain.entity.action`), choose partition count based on throughput needs (target / per-partition throughput).
- **Partition key is critical:** it determines ordering and load distribution. Use the entity ID (order_id, user_id) that needs ordering. Same key = same partition = ordered.
- **Producers:** use `acks=all` + `min.insync.replicas=2` + `enable.idempotence=true` for no data loss and no duplicates. Batch and compress for throughput.
- **Consumers:** use manual offset commits + idempotent processing logic. Use CooperativeStickyAssignor for smooth rebalances. Max consumers per group = partition count.
- **Exactly-once:** use Kafka transactions for Kafka-to-Kafka flows. For external systems, use at-least-once + idempotent consumers (dedup table, upsert).
- **Schema evolution:** use Avro + Schema Registry with FULL compatibility. Only add optional fields with defaults. Never remove required fields.
- **Retention:** time-based for event streams, compaction for stateful data (latest value per key), tiered storage for long-term retention without expensive broker disks.
- **Monitor:** under-replicated partitions, consumer lag, producer errors, disk usage, request latency. Use Prometheus + Grafana + Burrow.
- **Failure handling:** RF=3 for broker failures, DLQ for poison pills, idempotent consumers for duplicate handling, outbox pattern (Debezium CDC) for reliable DB-to-Kafka publishing.
- **Patterns:** CQRS (separate read/write models bridged by Kafka), event sourcing (event log as source of truth), Saga (choreography or orchestration for distributed transactions), outbox (atomic DB + event publishing).
- **Multi-region:** MirrorMaker 2 or Cluster Linking for active-passive or active-active replication. Plan topic naming early to avoid conflicts.

---

# Problem 12: What Are Retry, Circuit Breaker & Bulkhead Patterns?

## 12.1 Problem Statement

> In a microservices architecture with 30+ services, downstream failures are inevitable — network blips, slow dependencies, crashed pods, overloaded databases. Explain the retry, circuit breaker, and bulkhead patterns. When do you use each one? How do they interact? What happens when you get them wrong?

This question tests:
- **Understanding of failure modes** in distributed systems — transient vs. permanent, slow vs. down.
- **Pattern knowledge** — not just definitions, but when, why, and how each pattern applies.
- **Practical judgment** — configuring retries without causing retry storms, choosing circuit breaker thresholds, sizing bulkhead pools.
- **Systems thinking** — how these patterns combine into a resilience stack.

It is a must-know topic because every production microservices system uses these patterns (or suffers without them).

---

## 12.2 Step 1: Why Resilience Patterns? (The Cascade Failure Problem)

```
THE PROBLEM: CASCADE FAILURE

  Service A -> Service B -> Service C -> Database

  1. Database slows down (disk I/O spike).
  2. Service C waits for DB -> threads blocked -> response time 10x.
  3. Service B waits for C -> its threads blocked too -> queue builds.
  4. Service A waits for B -> its threads blocked -> all threads exhausted.
  5. Service A cannot serve ANY requests (including those that do NOT
     depend on B or C).
  6. Load balancer health check fails -> Service A marked unhealthy.
  7. User-facing impact: entire platform down because ONE database
     had a disk spike.

  Time from root cause to full outage: seconds to minutes.
```

```
WITHOUT RESILIENCE PATTERNS

  Service A (200 threads)
    |
    +---> Service B (normal: 20ms)  -> uses 5 threads
    +---> Service C (normal: 30ms)  -> uses 10 threads
    +---> Service D (SLOW: 30sec)   -> uses ??? threads
    |
    All 200 threads blocked waiting on D.
    Services B and C (healthy) cannot be reached.
    Entire Service A is DOWN because of ONE slow dependency.

WITH RESILIENCE PATTERNS

  Service A (200 threads)
    |
    +---> Service B (retry + timeout)     -> handled
    +---> Service C (circuit breaker)     -> fast-fail when C is down
    +---> Service D (bulkhead: max 20     -> only 20 threads stuck;
    |     threads + timeout + fallback)      180 threads serve B & C
    |
    Service D is slow, but A keeps serving B and C traffic.
    Fallback returns cached/default data for D's functionality.
```

**The core principle:** a failure in one dependency must NOT take down unrelated functionality. Resilience patterns **isolate failures** and **degrade gracefully**.

---

## 12.3 Step 2: The Retry Pattern

**What it does:** automatically re-attempts a failed operation, hoping the failure is transient (network blip, brief overload, temporary error).

```
RETRY BASICS

  Request -> Fail (503) -> Wait -> Retry 1 -> Fail (503) -> Wait -> Retry 2 -> Success (200)

  Without retry: user sees an error.
  With retry: user gets a successful response (slightly delayed).
```

**When to retry:**
```
  Retry-safe (idempotent)          | NOT retry-safe
  ---------------------------------|--------------------------------
  GET /api/users/123               | POST /api/orders (creates order)
  PUT /api/users/123 (full replace)| POST /api/payments (charges card)
  DELETE /api/items/456            | Non-idempotent mutations without
  Read from database               | idempotency keys.
  Fetch from cache                 |
  GET from external API            |

  Rule: only retry IDEMPOTENT operations (same request, same result).
  For non-idempotent operations, use an idempotency key so the server
  can detect and ignore duplicate requests.
```

**Retry strategies:**
```
  Strategy                  | How it works                      | Use case
  --------------------------|-----------------------------------|-------------------
  Fixed delay               | Wait N ms between retries.        | Simple, but can
                            | Retry after 500ms, 500ms, 500ms.  | cause thundering
                            |                                   | herd.
  Exponential backoff       | Wait 2^attempt * base_ms.         | The standard.
                            | 100ms, 200ms, 400ms, 800ms...    | Gives the server
                            |                                   | time to recover.
  Exponential backoff       | Add random(0, delay) to each      | Best practice.
  + jitter                  | wait. Spreads retries in time.    | Prevents
                            | Avoids synchronized retry bursts. | thundering herd.
  Linear backoff            | Wait attempt * base_ms.           | Moderate increase.
                            | 200ms, 400ms, 600ms, 800ms.      |
  Immediate retry (once)    | Retry once with no delay.         | Very transient
                            |                                   | errors only.
```

**Retry configuration (recommended):**
```
  maxRetries = 3                      (do not retry forever)
  initialDelay = 100ms               (start small)
  multiplier = 2.0                   (exponential growth)
  maxDelay = 5000ms                  (cap the wait time)
  jitter = true                      (randomize to prevent herd)
  retryOn = [503, 429, IOException,  (only transient errors)
             TimeoutException]
  doNotRetryOn = [400, 401, 403,     (permanent errors — no point retrying)
                  404, 422]
```

**The retry storm problem:**
```
  WARNING: retries can AMPLIFY failure.

  Normal: 1000 RPS to Service B.
  Service B goes slow.
  With 3 retries: 1000 * 4 = 4000 RPS to Service B.
  Service B is now 4x more overloaded.
  Every retry fails -> more retries -> more load -> total collapse.

  This is called a RETRY STORM. It turns a partial failure into
  a complete failure.

  Mitigations:
  1. Exponential backoff + jitter (spread retries over time).
  2. Retry budget: limit total retries to 10-20% of original traffic.
     If 20% of requests are already retries, stop retrying new failures.
  3. Circuit breaker (see next section): stop calling entirely
     when failure rate is high.
  4. Max retries cap (3 is usually enough).
```

---

## 12.4 Step 3: The Circuit Breaker Pattern

**What it does:** monitors the failure rate of a downstream call. When failures exceed a threshold, it **stops calling** the downstream service entirely (fails fast) and returns an error or fallback immediately. After a cooldown, it tentatively allows one call through to check if the service has recovered.

**Named after electrical circuit breakers:** when current is too high, the breaker trips and stops the flow to prevent damage.

```
CIRCUIT BREAKER STATES

  +--------+     failure rate     +---------+     cooldown     +-----------+
  | CLOSED |  >= threshold (50%) | OPEN    |  timer expires   | HALF-OPEN |
  | (normal|--------------------->| (fast   |------------------>| (testing) |
  |  flow) |                     |  fail)  |                  |           |
  +--------+                     +---------+                  +-----------+
      ^                                                            |
      |                    success rate                             |
      |                  >= threshold (60%)                         |
      +------------------------------------------------------------+
      |                                                            |
      |                    failure detected                         |
      |                    in half-open state                       |
      |                         +----------------------------------+
      |                         |
      |                         v
      |                    +---------+
      +--------------------| OPEN    |
                           +---------+
```

```
STATE DETAILS

  CLOSED (normal operation):
    - All requests pass through.
    - Failures are counted in a sliding window.
    - If failure rate >= threshold -> transition to OPEN.

  OPEN (circuit tripped):
    - ALL requests are rejected immediately (fast-fail).
    - No call is made to the downstream service.
    - Returns error or fallback response.
    - A timer runs (e.g., 30 seconds).
    - After timer expires -> transition to HALF-OPEN.

  HALF-OPEN (testing recovery):
    - A LIMITED number of requests (e.g., 5) are allowed through.
    - If enough succeed (e.g., 60% success rate) -> CLOSED (recovered).
    - If failures continue -> back to OPEN (still broken).
```

**Circuit breaker configuration:**
```
  slidingWindowType = COUNT_BASED       (or TIME_BASED)
  slidingWindowSize = 100               (last 100 calls)
  failureRateThreshold = 50            (50% failure -> open)
  slowCallRateThreshold = 80           (80% slow calls -> open)
  slowCallDurationThreshold = 2000ms   (calls > 2s are "slow")
  minimumNumberOfCalls = 20            (need 20 calls before evaluating)
  waitDurationInOpenState = 30s        (cooldown before half-open)
  permittedNumberOfCallsInHalfOpen = 5 (test calls in half-open)
  automaticTransition = true           (auto-transition after timer)
```

**What counts as a failure:**
```
  Counted as failure:             | NOT counted as failure:
  --------------------------------|--------------------------------
  HTTP 500, 502, 503, 504         | HTTP 400, 401, 403, 404, 422
  Connection timeout              | (client errors — not the
  Read timeout                    | downstream service's fault)
  Connection refused              |
  IOException                     | Successful responses (200, 201)
  CircuitBreakerOpenException     | (circuit already open)
```

**Why circuit breakers matter:**
```
  Without circuit breaker:
    Service A calls Service B (which is down).
    Each call waits for timeout (e.g., 5 seconds).
    1000 RPS * 5 sec = 5000 threads blocked.
    Service A is effectively down.

  With circuit breaker:
    After 50% of 100 calls fail, circuit OPENS.
    All subsequent calls fail instantly (< 1ms) with fallback.
    Service A stays healthy.
    Service B has time to recover without being hammered.
    After 30 seconds, circuit goes HALF-OPEN and tests recovery.
```

---

## 12.5 Step 4: The Bulkhead Pattern

**What it does:** isolates different parts of the system into separate resource pools (thread pools, connection pools, semaphores) so that a failure in one pool does not exhaust resources for others.

**Named after ship bulkheads:** watertight compartments that prevent a hull breach from flooding the entire ship.

```
BULKHEAD: RESOURCE ISOLATION

  WITHOUT BULKHEAD (shared thread pool):

    Service A (200 threads shared)
      +---> Service B calls: using 20 threads
      +---> Service C calls: using 10 threads
      +---> Service D calls (SLOW): using 170 threads  <-- PROBLEM
      |
      0 threads left for B and C -> entire service blocked.

  WITH BULKHEAD (isolated pools):

    Service A
      +---> [Pool: B] 50 threads max -> Service B calls
      +---> [Pool: C] 30 threads max -> Service C calls
      +---> [Pool: D] 20 threads max -> Service D calls (SLOW)
      |                                  Only 20 threads stuck.
      +---> [Pool: general] 100 threads -> other work
      |
      Service D is slow, but B, C, and general work continue normally.
      D's impact is CONTAINED to its 20-thread pool.
```

**Bulkhead types:**
```
  Type                    | How it works                      | Use case
  ------------------------|-----------------------------------|-------------------
  Thread pool isolation   | Separate thread pool per           | Blocking I/O calls.
                          | downstream dependency. Each pool  | Heavy isolation.
                          | has a fixed max size. Calls       | Higher overhead
                          | execute on the pool's threads.    | (thread context
                          |                                   | switching).
  Semaphore isolation     | A counter limits concurrent       | Non-blocking or
                          | calls. No separate threads.       | lightweight calls.
                          | Caller's thread is used.          | Lower overhead.
                          | If semaphore full, reject/queue.  | Simpler.
  Connection pool per     | Separate HTTP connection pool     | HTTP client
  dependency              | for each downstream service.      | isolation. Prevents
                          | If one pool is exhausted, others  | one slow service
                          | still have connections.           | from consuming all
                          |                                   | connections.
```

**Bulkhead configuration:**
```
  Thread pool bulkhead:
    maxThreadPoolSize = 20          (max concurrent calls to dependency)
    coreThreadPoolSize = 10         (base threads kept alive)
    queueCapacity = 50              (waiting queue when pool is full)
    keepAliveDuration = 30s         (idle thread lifetime)

  Semaphore bulkhead:
    maxConcurrentCalls = 20         (max concurrent calls)
    maxWaitDuration = 500ms         (max time to wait for permit)

  When to choose which:
    - Thread pool: when calls are blocking (JDBC, synchronous HTTP).
    - Semaphore: when calls are non-blocking (WebClient, async HTTP).
    - Semaphore is simpler and has less overhead. Prefer it unless
      you need full thread isolation.
```

**Sizing bulkheads:**
```
  Formula:
    pool_size = target_RPS * avg_latency_seconds

  Example:
    Service D: 100 RPS, avg latency 200ms.
    pool_size = 100 * 0.2 = 20 threads.
    Add 20-50% buffer: 25 threads.

  If Service D becomes slow (2 seconds):
    100 RPS * 2s = 200 threads needed — but bulkhead caps at 25.
    175 requests/sec are rejected (fast-fail) instead of blocking.
    This is EXACTLY what we want: contain the blast radius.
```

---

## 12.6 Step 5: The Timeout Pattern

**What it does:** sets a maximum duration for a call. If the call does not complete within the timeout, it is cancelled and an error is returned.

```
TIMEOUT IS THE SIMPLEST RESILIENCE PATTERN

  Without timeout:
    Service A calls Service B.
    Service B hangs (GC pause, deadlock, network issue).
    Service A's thread waits FOREVER.
    Repeat for all threads -> Service A is dead.

  With timeout:
    Service A calls Service B with timeout = 2 seconds.
    If B does not respond in 2 seconds -> TimeoutException.
    Thread is freed. Error or fallback returned to caller.
```

**Timeout configuration:**
```
  Layer                    | Recommended timeout       | Notes
  -------------------------|---------------------------|------------------------
  HTTP client (connection) | 1-3 seconds               | Time to establish TCP
                           |                           | connection.
  HTTP client (read/write) | 2-10 seconds              | Time to receive
                           |                           | response. Depends on
                           |                           | expected latency.
  Database query           | 5-30 seconds              | Depends on query
                           |                           | complexity.
  Circuit breaker timeout  | 2-5 seconds               | Total call duration
                           |                           | including retries.
  API gateway              | 10-30 seconds             | Overall request
                           |                           | timeout for the client.
  Message queue consumer   | 30-300 seconds            | Processing timeout.
                           |                           | Depends on work.

  Rule of thumb:
    timeout = p99_latency * 2-3x (of the dependency in normal conditions)

  Example:
    Service B normally responds in 200ms (p99 = 500ms).
    Set timeout = 500ms * 2 = 1 second.
    Anything beyond 1 second is "too slow" and should be cut off.
```

**Timeout ordering (layered timeouts):**
```
  CRITICAL: outer timeout > inner timeout

  Client request timeout = 10 seconds
    |
    +-> API Gateway timeout = 8 seconds
         |
         +-> Service A overall timeout = 5 seconds
              |
              +-> Retry (3 attempts, each with 1.5s timeout)
                   |
                   +-> Circuit breaker timeout = 1.5 seconds
                        |
                        +-> HTTP client timeout = 1 second

  If inner timeouts are LARGER than outer:
    The outer timeout fires first.
    Inner call is still running (resource leak).
    Retries never get a chance to execute.
    The pattern stack is broken.
```

---

## 12.7 Step 6: The Fallback Pattern

**What it does:** provides an alternative response when the primary operation fails (error, timeout, circuit open). Ensures the user gets something useful instead of an error.

```
FALLBACK STRATEGIES

  Strategy               | How it works                     | Example
  -----------------------|----------------------------------|---------------------
  Cached value           | Return the last known good       | Product catalog:
                         | response from cache.             | return cached price
                         |                                  | if pricing service
                         |                                  | is down.
  Default value          | Return a static default.         | Recommendation
                         |                                  | engine down: return
                         |                                  | "most popular items."
  Degraded service       | Return a simplified version      | Search: return basic
                         | of the response.                 | keyword match if
                         |                                  | ML ranking is down.
  Empty / neutral        | Return empty list or neutral     | Analytics widget
  response               | value. UI hides the section.     | down: show nothing
                         |                                  | instead of an error.
  Queue for later        | Accept the request, queue it,    | Order placed but
                         | process when service recovers.   | notification service
                         |                                  | down: queue the
                         |                                  | email for later.
  Error with context     | Return a user-friendly error     | "Recommendations
                         | that explains degraded mode.     | temporarily
                         |                                  | unavailable."
```

```
FALLBACK DECISION TREE

  Is the failed dependency on the CRITICAL path?
    YES (e.g., payment) -> Cannot fallback. Return error. Retry.
    NO  (e.g., recommendations, analytics) -> Use fallback.

  Is there a cached value available?
    YES -> Return cached value (best UX).
    NO  -> Is a default value acceptable?
      YES -> Return default (e.g., popular items).
      NO  -> Return degraded response or error with context.
```

---

## 12.8 Step 7: The Rate Limiter Pattern

**What it does:** limits the number of calls to a dependency within a time window. Prevents your service from overwhelming a downstream dependency.

```
RATE LIMITER VS. BULKHEAD

  Bulkhead: limits CONCURRENT calls (how many at the same time).
  Rate limiter: limits CALLS PER SECOND (throughput over time).

  Example:
    Bulkhead: max 20 concurrent calls to Service B.
    Rate limiter: max 100 calls/second to Service B.

  A slow service can exhaust a bulkhead (20 concurrent calls, all slow).
  A rate limiter prevents sending too much traffic regardless of latency.
  Use BOTH together for comprehensive protection.
```

```
RATE LIMITER ALGORITHMS

  Algorithm             | How it works                      | Pros / Cons
  ----------------------|-----------------------------------|-------------------
  Token bucket          | Tokens added at a fixed rate.     | Allows bursts up
                        | Each call consumes a token.       | to bucket size.
                        | No token = rejected.              | Simple, popular.
  Sliding window        | Count calls in a sliding time     | Smooth rate. No
                        | window.                           | bursts. More memory.
  Fixed window          | Count calls in fixed intervals.   | Simple. Can allow
                        | Reset count at interval boundary. | 2x rate at boundary.
  Leaky bucket          | Calls enter a queue (bucket).     | Smooth output rate.
                        | Processed at a fixed rate.        | Rejects if full.
                        | Overflow rejected.                |

  Recommended: token bucket for most cases (allows brief bursts).
```

**Rate limiter configuration:**
```
  limitForPeriod = 100              (max calls per period)
  limitRefreshPeriod = 1s           (refresh period)
  timeoutDuration = 500ms           (max wait for a permit)

  Use case:
    External API allows 100 calls/second.
    Set rate limiter to 90 calls/second (10% safety margin).
    If your service tries to exceed 90 calls/sec, excess calls
    wait up to 500ms for a permit, then fail if none available.
```

---

## 12.9 Step 8: Combining Patterns (The Resilience Stack)

These patterns are most effective **combined**, not used in isolation. The order matters:

```
THE RESILIENCE STACK (execution order)

  Incoming request
       |
       v
  [Rate Limiter]        Limits calls/second to downstream.
       |                If rate exceeded -> reject immediately.
       v
  [Bulkhead]            Limits concurrent calls. Isolates resource pool.
       |                If pool full -> reject / queue.
       v
  [Circuit Breaker]     Checks if circuit is open.
       |                If OPEN -> fast-fail (no call made) -> fallback.
       v
  [Retry]               Wraps the actual call with retry logic.
       |                On failure -> wait -> retry (up to maxRetries).
       v
  [Timeout]             Wraps each individual call attempt.
       |                If call takes too long -> cancel -> TimeoutException.
       v
  [Actual HTTP Call]    The real network call to the downstream service.
       |
       v
  [Fallback]            If all above fail -> return cached / default / error.
```

```
WHY THIS ORDER?

  1. Rate Limiter FIRST: no point entering the bulkhead or circuit
     breaker if we are already over the rate limit.

  2. Bulkhead SECOND: acquire a thread/semaphore permit before
     checking the circuit. This ensures resource isolation.

  3. Circuit Breaker THIRD: if the circuit is open, fail fast
     without consuming a retry attempt or making a network call.

  4. Retry FOURTH: if the call fails, retry within the circuit
     breaker's window. If retries exhaust, circuit breaker records
     the failure.

  5. Timeout FIFTH (wraps each call): each retry attempt has its
     own timeout. A slow call is cut off and retried.

  6. Fallback LAST: if everything fails (retries exhausted, circuit
     open), provide an alternative response.
```

```
EXAMPLE: CALLING A RECOMMENDATION SERVICE

  Rate Limiter: max 200 calls/sec (recommendation service limit).
  Bulkhead: max 15 concurrent calls (semaphore, non-blocking).
  Circuit Breaker: open at 50% failure rate (100-call window).
  Retry: 2 retries, exponential backoff 100ms/200ms, jitter.
  Timeout: 500ms per attempt.
  Fallback: return cached "popular items" list.

  Normal flow: rate OK -> bulkhead has permit -> circuit closed ->
    call succeeds in 80ms -> return recommendations.

  Degraded flow: rate OK -> bulkhead has permit -> circuit closed ->
    call fails (503) -> retry 1 (100ms wait) -> call succeeds -> return.

  Failed flow: rate OK -> bulkhead has permit -> circuit OPEN ->
    fast-fail (no call made) -> fallback -> return popular items.

  User experience: always gets recommendations (real or fallback).
  System impact: recommendation service outage does NOT affect
  checkout, search, or any other functionality.
```

---

## 12.10 Step 9: Implementation — Resilience4j (Java / Spring Boot)

Resilience4j is the standard resilience library for Java (successor to Hystrix).

```
DEPENDENCY (Spring Boot + Resilience4j)

  // build.gradle
  implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
  implementation 'io.github.resilience4j:resilience4j-all:2.2.0'
  implementation 'org.springframework.boot:spring-boot-starter-aop'
```

```
APPLICATION.YML CONFIGURATION

  resilience4j:
    circuitbreaker:
      instances:
        recommendationService:
          slidingWindowSize: 100
          failureRateThreshold: 50
          slowCallRateThreshold: 80
          slowCallDurationThreshold: 2s
          waitDurationInOpenState: 30s
          permittedNumberOfCallsInHalfOpenState: 5
          minimumNumberOfCalls: 20
          recordExceptions:
            - java.io.IOException
            - java.util.concurrent.TimeoutException
          ignoreExceptions:
            - com.example.BusinessException

    retry:
      instances:
        recommendationService:
          maxAttempts: 3
          waitDuration: 100ms
          enableExponentialBackoff: true
          exponentialBackoffMultiplier: 2
          retryExceptions:
            - java.io.IOException
            - java.util.concurrent.TimeoutException

    bulkhead:
      instances:
        recommendationService:
          maxConcurrentCalls: 15
          maxWaitDuration: 500ms

    timelimiter:
      instances:
        recommendationService:
          timeoutDuration: 1s
          cancelRunningFuture: true

    ratelimiter:
      instances:
        recommendationService:
          limitForPeriod: 200
          limitRefreshPeriod: 1s
          timeoutDuration: 500ms
```

```
ANNOTATED SERVICE (declarative)

  @Service
  public class RecommendationClient {

      @CircuitBreaker(name = "recommendationService",
                      fallbackMethod = "fallbackRecommendations")
      @Retry(name = "recommendationService")
      @Bulkhead(name = "recommendationService")
      @TimeLimiter(name = "recommendationService")
      @RateLimiter(name = "recommendationService")
      public CompletableFuture<List<Product>> getRecommendations(String userId) {
          return CompletableFuture.supplyAsync(() ->
              restClient.get()
                  .uri("/api/recommendations/{userId}", userId)
                  .retrieve()
                  .body(new ParameterizedTypeReference<>() {})
          );
      }

      // Fallback method — same signature + Throwable parameter.
      public CompletableFuture<List<Product>> fallbackRecommendations(
              String userId, Throwable t) {
          log.warn("Recommendation service unavailable for user {}. "
                 + "Returning popular items. Error: {}", userId, t.getMessage());
          return CompletableFuture.completedFuture(popularItemsCache.get());
      }
  }
```

```
PROGRAMMATIC API (when you need more control)

  CircuitBreaker cb = CircuitBreaker.of("recService", circuitBreakerConfig);
  Retry retry = Retry.of("recService", retryConfig);
  Bulkhead bulkhead = Bulkhead.of("recService", bulkheadConfig);
  TimeLimiter timeLimiter = TimeLimiter.of(timeLimiterConfig);

  Supplier<List<Product>> supplier = () -> restClient.getRecommendations(userId);

  // Decorate with all patterns (order matters!):
  Supplier<List<Product>> decorated = Decorators.ofSupplier(supplier)
      .withRateLimiter(rateLimiter)
      .withBulkhead(bulkhead)
      .withCircuitBreaker(cb)
      .withRetry(retry)
      .withFallback(List.of(TimeoutException.class, IOException.class),
                    t -> popularItemsCache.get())
      .decorate();

  List<Product> result = decorated.get();
```

---

## 12.11 Step 10: Observability for Resilience Patterns

You cannot manage what you cannot measure. Every resilience pattern must be **observable**.

```
KEY METRICS TO MONITOR

  Pattern          | Metrics                           | Alert on
  -----------------|-----------------------------------|------------------------
  Circuit Breaker  | State (closed/open/half-open),    | State = OPEN.
                   | failure rate, slow call rate,     | Failure rate > 30%.
                   | calls not permitted.              |
  Retry            | Retry count, retry success rate,  | Retry rate > 20% of
                   | retries exhausted.                | total calls (retry
                   |                                   | storm indicator).
  Bulkhead         | Available permits, rejected       | Available permits = 0
                   | calls, queue depth.               | (pool exhausted).
  Timeout          | Timeout count, timeout rate.      | Timeout rate > 10%.
  Rate Limiter     | Available permits, rejected       | High rejection rate.
                   | calls, wait time.                 |
  Fallback         | Fallback invocation count,        | Sustained fallback
                   | fallback success/failure.         | usage > 5 minutes.
```

```
RESILIENCE4J + MICROMETER + PROMETHEUS + GRAFANA

  Resilience4j auto-publishes metrics via Micrometer:

  // application.yml
  resilience4j:
    circuitbreaker:
      metrics:
        enabled: true
    retry:
      metrics:
        enabled: true

  management:
    endpoints:
      web:
        exposure:
          include: health, metrics, prometheus

  Metrics exposed:
    resilience4j_circuitbreaker_state
    resilience4j_circuitbreaker_failure_rate
    resilience4j_circuitbreaker_calls_total{kind="successful|failed|not_permitted"}
    resilience4j_retry_calls_total{kind="successful_without_retry|successful_with_retry|failed_with_retry|failed_without_retry"}
    resilience4j_bulkhead_available_concurrent_calls
    resilience4j_timelimiter_calls_total{kind="successful|timeout|failed"}
    resilience4j_ratelimiter_available_permissions

  Dashboard: Grafana dashboard for Resilience4j (template ID: 15694).
```

---

## 12.12 Step 11: Testing Resilience (Chaos Engineering)

```
HOW TO TEST RESILIENCE PATTERNS

  Test type                  | What to test                   | Tools
  ---------------------------|--------------------------------|-------------------
  Unit test                  | Retry logic, fallback returns  | JUnit + Resilience4j
                             | correct value, circuit breaker | test utilities.
                             | transitions.                   |
  Integration test           | Inject failures (WireMock      | WireMock, Testcontainers,
                             | returns 503), verify fallback. | MockServer.
  Chaos engineering          | Kill a downstream service in   | Chaos Monkey (Netflix),
                             | staging/production. Verify     | Litmus, Gremlin,
                             | graceful degradation.          | AWS FIS.
  Load test with failure     | Run load test, inject          | Gatling + WireMock
                             | failures mid-test. Verify no   | (inject 503 after
                             | cascade failure.               | 5 min).
  Game day                   | Team exercise: simulate a      | Manual or automated
                             | major outage. Practice         | failure injection.
                             | incident response.             |

  What to verify:
  1. Circuit breaker OPENS when downstream fails.
  2. Retries succeed on transient errors.
  3. Retries do NOT cause a retry storm.
  4. Bulkhead contains the blast radius (other dependencies OK).
  5. Fallback returns acceptable data.
  6. System recovers automatically when downstream comes back.
  7. Alerts fire when circuit opens.
  8. Latency stays acceptable during degraded mode.
```

---

## 12.13 Step 12: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Retrying non-idempotent ops      | Can cause duplicate orders, double charges.
                                   | Only retry idempotent operations or use
                                   | idempotency keys.
  Retry storms                     | Retries amplify load on a struggling service.
                                   | Use exponential backoff + jitter + retry
                                   | budgets + circuit breaker.
  Circuit breaker too sensitive    | Opens on minor blips, causing unnecessary
                                   | fallbacks. Set minimumNumberOfCalls high
                                   | enough and use sliding window.
  Circuit breaker too insensitive  | Does not open until massive failure. By
                                   | then, threads are exhausted. Tune threshold
                                   | and slow-call detection.
  Bulkhead too small               | Rejects legitimate traffic even when
                                   | downstream is healthy. Size based on
                                   | expected RPS * latency + buffer.
  Bulkhead too large               | Does not protect — too many threads can
                                   | still be consumed. Defeats the purpose.
  Timeout too short                | Cuts off legitimate slow calls (large
                                   | payloads, complex queries). Set based on
                                   | p99 latency * 2-3x.
  Timeout too long                 | Threads blocked for too long. System
                                   | becomes unresponsive under load.
  Fallback hiding real problems    | If fallback always works, nobody notices
                                   | the dependency is broken. Alert on sustained
                                   | fallback usage.
  Wrong pattern order              | Retry outside circuit breaker wastes retries
                                   | on an open circuit. Follow the stack order:
                                   | rate limiter > bulkhead > CB > retry > timeout.
  No observability                 | Patterns work silently. Without metrics and
                                   | alerts, you cannot tune them or know when
                                   | they activate. Always instrument.
  Over-engineering simple calls    | Not every dependency needs the full stack.
                                   | A health check endpoint needs a timeout,
                                   | not a circuit breaker + bulkhead + retry.
  Ignoring downstream SLAs         | Your timeout and retry config must respect
                                   | the downstream service's SLA. If their SLA
                                   | is 500ms p99, your timeout should be ~1s.
```

---

## 12.14 Problem 12 Summary

- **Cascade failures are the #1 threat** in microservices: one slow dependency can exhaust all threads and take down unrelated functionality. Resilience patterns prevent this.
- **Retry** re-attempts transient failures with exponential backoff + jitter. Only retry idempotent operations. Cap at 3 retries. Use retry budgets to prevent retry storms.
- **Circuit breaker** monitors failure rates and stops calling a broken dependency (fast-fail). Three states: CLOSED (normal), OPEN (fast-fail), HALF-OPEN (testing recovery). Prevents thread exhaustion and gives the dependency time to recover.
- **Bulkhead** isolates resource pools per dependency (thread pool or semaphore). A slow dependency only consumes its allocated pool, not the shared pool. Size = RPS * latency + buffer.
- **Timeout** sets a maximum call duration. Without it, threads wait forever. Set to p99 * 2-3x. Ensure outer timeouts > inner timeouts.
- **Fallback** provides cached, default, or degraded responses when the primary call fails. Use for non-critical dependencies. Alert on sustained fallback usage.
- **Rate limiter** caps calls/second to protect downstream services from overload. Use token bucket. Combine with bulkhead (concurrent) for comprehensive protection.
- **The resilience stack** (execution order): Rate Limiter → Bulkhead → Circuit Breaker → Retry → Timeout → Actual Call → Fallback. Order matters.
- **Resilience4j** is the standard Java library. Use annotations (`@CircuitBreaker`, `@Retry`, `@Bulkhead`, `@TimeLimiter`, `@RateLimiter`) or the programmatic `Decorators` API. Configure in `application.yml`.
- **Observability is mandatory:** monitor circuit state, retry rate, bulkhead utilization, timeout rate, fallback invocations. Use Micrometer + Prometheus + Grafana. Alert on circuit open and sustained fallback.
- **Test resilience:** unit tests for pattern logic, integration tests with WireMock (inject 503), chaos engineering (kill dependencies), load tests with failure injection. Verify graceful degradation and automatic recovery.
- **Key pitfalls:** retrying non-idempotent ops, retry storms, wrong pattern order, timeouts too short/long, fallback hiding real problems, no observability, over-engineering simple calls.

---

# Problem 13: Design a Caching Strategy (Multi-Level Caching)

## 13.1 Problem Statement

> Your e-commerce platform handles 100K RPS at peak. The database can serve 5K QPS. Design a multi-level caching strategy that bridges this gap, covering every layer from the browser to the database. Walk through what to cache at each level, invalidation strategies, write patterns, consistency trade-offs, and the failure modes that break caching in production.

This question tests:
- **Layered thinking** — understanding that caching is not just "add Redis"; it spans 6+ layers.
- **Invalidation mastery** — "there are only two hard things in CS: cache invalidation and naming things."
- **Consistency judgment** — knowing when stale data is acceptable and when it is not.
- **Operational awareness** — cache stampedes, hot keys, cold starts, eviction storms.

---

## 13.2 Step 1: Why Cache? (The Numbers That Matter)

```
LATENCY COMPARISON

  Operation                          | Latency
  -----------------------------------|------------------
  L1 CPU cache reference             | 0.5 ns
  L2 CPU cache reference             | 7 ns
  RAM reference                      | 100 ns
  In-process cache (Caffeine)        | ~100-500 ns
  Redis GET (same AZ)                | 0.1-0.5 ms
  SSD random read                    | 0.1 ms
  Network round trip (same DC)       | 0.5 ms
  HDD random read                    | 5-10 ms
  PostgreSQL indexed query           | 1-10 ms
  PostgreSQL full table scan         | 100-1000+ ms
  Cross-region network round trip    | 50-150 ms
  CDN cache hit                      | 5-50 ms (edge)

  Key insight:
    In-process cache: ~0.5 ms
    Redis: ~0.5 ms
    Database: ~5-50 ms (indexed) to ~500 ms+ (complex)

    A 95% cache hit rate means:
    95% of requests at ~0.5 ms + 5% at ~10 ms = ~1 ms average.
    Without cache: 100% at ~10 ms = ~10 ms average.
    That is a 10x latency improvement.
```

```
THROUGHPUT MATH

  Database: 5K QPS max (PostgreSQL on good hardware).
  Traffic: 100K RPS at peak.
  Gap: 100K / 5K = 20x more traffic than DB can handle.

  With 95% cache hit rate:
    Only 5% of requests hit the DB: 100K * 0.05 = 5K QPS.
    DB is exactly at capacity. 

  With 90% cache hit rate:
    10% hit DB: 100K * 0.10 = 10K QPS.
    DB is 2x overloaded. System crashes.

  Lesson: the difference between 90% and 95% hit rate
  is the difference between outage and smooth operation.
  Every percentage point of cache hit rate matters at scale.
```

---

## 13.3 Step 2: Clarifying Questions & Scope

```
  Question                                  | Assumption
  ------------------------------------------|----------------------------------
  Read/write ratio?                         | 95:5 (read-heavy, ideal for cache).
  Data freshness tolerance?                 | Product catalog: 1-5 min stale OK.
                                            | Prices: 30 sec stale OK.
                                            | Inventory count: 5 sec stale OK.
                                            | User cart/session: 0 sec (real-time).
                                            | Payment: 0 sec (never cache).
  How many unique items?                    | 10M products, 50M users.
  Hot data distribution?                    | 80/20 rule: 20% of products get
                                            | 80% of traffic.
  Cache budget (memory)?                    | 64 GB Redis cluster.
                                            | 2 GB per app instance (L1).
  Multi-region?                             | Yes, 3 regions.
  Existing infrastructure?                  | CDN (CloudFront), Redis cluster,
                                            | Spring Boot, PostgreSQL.
```

---

## 13.4 Step 3: The Multi-Level Cache Architecture

```
MULTI-LEVEL CACHE (L0 through L5)

  User's Browser
       |
  [L0] Browser Cache (HTTP cache headers: Cache-Control, ETag)
       |
  [L1] CDN / Edge Cache (CloudFront, Fastly, Akamai)
       |
  [L2] API Gateway / Reverse Proxy Cache (Nginx, Varnish)
       |
  [L3] Application In-Process Cache (Caffeine, Guava — per instance)
       |
  [L4] Distributed Cache (Redis / Memcached — shared across instances)
       |
  [L5] Database Query Cache / Materialized Views (PostgreSQL, MySQL)
       |
  [Source of Truth] Database (PostgreSQL)


  Request flow:
  1. Check L0 (browser). Hit? -> Done (0 ms, no network).
  2. Check L1 (CDN). Hit? -> Done (5-50 ms from edge).
  3. Check L2 (gateway). Hit? -> Done (1-5 ms, same DC).
  4. Check L3 (in-process). Hit? -> Done (~0.5 ms, no network).
  5. Check L4 (Redis). Hit? -> Done (~0.5 ms, same AZ).
  6. Check L5 (DB cache). Hit? -> Done (1-5 ms).
  7. Query database. Populate L4 (and optionally L3). Return.
```

```
CACHE HIT WATERFALL

  Level   | Hit rate  | Remaining traffic  | Latency
  --------|-----------|--------------------|-----------
  L0      | 30%       | 70K RPS            | 0 ms
  L1      | 60% of 70K| 28K RPS           | 10 ms
  L2      | 20% of 28K| 22.4K RPS         | 2 ms
  L3      | 50% of 22K| 11.2K RPS         | 0.5 ms
  L4      | 80% of 11K| 2.2K RPS          | 0.5 ms
  L5      | 30% of 2K | 1.5K RPS          | 2 ms
  DB      | ---       | 1.5K QPS          | 5-10 ms

  Result: 100K RPS reduced to 1.5K DB QPS. DB easily handles this.
  Each layer peels off traffic. No single layer does all the work.
```

---

## 13.5 Step 4: Level 0 — Browser & Client-Side Cache

```
HTTP CACHE HEADERS

  Cache-Control: public, max-age=300
    Browser caches the response for 5 minutes.
    No request to server at all.

  Cache-Control: private, max-age=60
    Only the user's browser caches (not CDN).
    For user-specific data (cart, profile).

  Cache-Control: no-cache
    Browser must revalidate with the server every time (ETag/If-None-Match).
    Server can respond 304 Not Modified (no body, saves bandwidth).

  Cache-Control: no-store
    Never cache. For sensitive data (payment, PII).

  ETag + If-None-Match (conditional GET):
    1. Server returns: ETag: "abc123" with the response.
    2. Next request: If-None-Match: "abc123".
    3. If data unchanged: 304 Not Modified (no body).
    4. If changed: 200 OK with new data and new ETag.

  Stale-While-Revalidate:
    Cache-Control: max-age=60, stale-while-revalidate=300
    Serve stale content immediately while revalidating in background.
    User gets instant response; cache is refreshed asynchronously.
```

```
WHAT TO CACHE AT L0

  Cache (long TTL):       | Do NOT cache:
  ------------------------|-------------------------------
  Static assets (JS, CSS, | User-specific dynamic data
  images, fonts).         | (cart, checkout state).
  Product images.         | Real-time prices.
  API responses for       | Payment/order pages.
  catalog browsing.       | Personalized content.
  Public page content.    |
```

---

## 13.6 Step 5: Level 1 — CDN & Edge Cache

```
CDN CACHING

  CDN nodes are deployed at edge locations worldwide.
  Requests hit the nearest edge node first.

  User (Mumbai) -> CDN Edge (Mumbai) -> Origin (US-East)
    If edge has cached response -> return immediately (10 ms).
    If not -> fetch from origin, cache at edge, return (200 ms first time).

  CDN cache key: URL + query params + Vary headers.
  CDN respects Cache-Control headers from origin.
```

```
CDN CONFIGURATION

  What to cache:              | CDN TTL          | Notes
  ----------------------------|------------------|------------------------
  Static assets               | 1 year           | Versioned URLs (hash in
  (JS, CSS, images)           |                  | filename for busting).
  Product catalog pages       | 5-15 min         | Tolerate slight staleness.
  API GET responses           | 30-300 sec       | Vary by Accept, Auth header.
  Search results              | 30-60 sec        | Only for common queries.

  Do NOT CDN cache:
  - POST/PUT/DELETE requests.
  - Authenticated user data (unless using Vary: Authorization carefully).
  - Real-time data (inventory count, live prices).
  - Responses with Set-Cookie headers.
```

```
CDN CACHE INVALIDATION

  1. TTL-based: let cache expire naturally. Simplest.
  2. Purge API: CloudFront invalidation, Fastly instant purge.
     Use when content changes and you cannot wait for TTL.
  3. Versioned URLs: /static/app.a1b2c3.js
     Deploy new version -> new URL -> old cache irrelevant.
     Best for static assets.
  4. Surrogate keys (Fastly, Varnish):
     Tag responses: Surrogate-Key: product-123
     Purge by tag: purge all responses tagged "product-123".
     Best for dynamic content invalidation.
```

---

## 13.7 Step 6: Level 2 — API Gateway & Reverse Proxy Cache

```
GATEWAY/PROXY CACHE

  Nginx or Varnish sits in front of application servers.
  Caches full HTTP responses for matching requests.

  Client -> Nginx (cache) -> App Server -> Redis -> DB

  If Nginx has cached response -> return without hitting app.
  Reduces load on app servers AND downstream services.

  Nginx config example:
    proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:64m
                     max_size=10g inactive=10m;
    proxy_cache_valid 200 5m;
    proxy_cache_valid 404 1m;
    proxy_cache_key "$request_method$request_uri$args";
    proxy_cache_use_stale error timeout http_500 http_502 http_503;

  proxy_cache_use_stale: serve stale content on error — avoids
  user-facing errors when origin is temporarily down.
```

```
WHEN TO USE L2

  Good for:
  - High-traffic GET endpoints with identical responses.
  - Public APIs with predictable cache keys.
  - Serving stale content during origin failures.

  Not good for:
  - Personalized responses (different per user).
  - Frequently changing data (invalidation overhead).
  - POST/mutation endpoints.
```

---

## 13.8 Step 7: Level 3 — Application In-Process Cache (L1)

```
IN-PROCESS CACHE (Caffeine, Guava Cache)

  Lives inside the JVM. Each app instance has its own cache.
  No network call. Fastest lookup after CPU cache.

  Caffeine cache (Java):
    Cache<String, Product> productCache = Caffeine.newBuilder()
        .maximumSize(10_000)            // Max entries.
        .expireAfterWrite(5, MINUTES)   // TTL.
        .refreshAfterWrite(1, MINUTES)  // Async refresh (serve stale + refresh).
        .recordStats()                  // Metrics.
        .build(key -> productService.fetchFromRedisOrDb(key));
```

```
L1 CACHE CHARACTERISTICS

  Pros:
  - Fastest possible access (~100-500 ns).
  - No network dependency.
  - Survives Redis outage (local data).

  Cons:
  - Each instance has its own copy -> inconsistency between instances.
  - Limited by JVM heap (2-4 GB typical).
  - Cache cold on restart / deployment.
  - Cannot share across instances.

  Best for:
  - Hot, frequently read, slowly changing data.
  - Config, feature flags, reference data, catalog metadata.
  - Small datasets that fit in memory.

  NOT good for:
  - Large datasets (millions of entries).
  - Data that must be consistent across instances (use Redis).
  - User sessions (use Redis).
```

```
L1 + L4 (TWO-TIER) PATTERN

  Read path:
  1. Check L1 (Caffeine). Hit? -> return.
  2. Check L4 (Redis). Hit? -> populate L1, return.
  3. Query DB. Populate L4 and L1. Return.

  This gives:
  - Sub-millisecond for the hottest data (L1).
  - Low-millisecond for warm data (L4).
  - DB only hit for truly cold data.

  Consistency between L1 instances:
  - Option A: Short L1 TTL (1-5 min). Accept staleness.
  - Option B: Redis Pub/Sub invalidation. On write, publish
    invalidation event. All instances evict from L1.
  - Option C: Versioned cache. Store version in Redis.
    L1 checks version on access; if stale, refetch.
```

---

## 13.9 Step 8: Level 4 — Distributed Cache (Redis / Memcached)

```
REDIS VS. MEMCACHED

  Feature               | Redis                          | Memcached
  -----------------------|--------------------------------|--------------------
  Data structures        | Strings, hashes, lists, sets,  | Strings only.
                         | sorted sets, streams, etc.     |
  Persistence            | RDB snapshots, AOF log.        | None (volatile).
  Replication             | Master-replica, Redis Cluster. | No built-in.
  Pub/Sub                | Yes.                           | No.
  Lua scripting          | Yes.                           | No.
  Eviction policies      | 8 policies (LRU, LFU, etc.).  | LRU only.
  Threading              | Single-threaded (6.x has I/O   | Multi-threaded.
                         | threads).                      |
  Max value size         | 512 MB.                        | 1 MB default.
  Cluster mode           | Yes (hash slots, auto-shard).  | Client-side
                         |                                | sharding.

  Choose Redis: rich data structures, Pub/Sub, persistence, scripting.
  Choose Memcached: simple key-value, multi-threaded, slightly faster
  for pure string GET/SET at very high throughput.

  In practice: Redis wins for almost all use cases.
```

```
REDIS DEPLOYMENT (production)

  Redis Cluster (6 nodes: 3 masters + 3 replicas):
    - 16,384 hash slots distributed across 3 masters.
    - Each master has 1 replica for failover.
    - Automatic failover: if master fails, replica promotes.

  +----------+    +----------+    +----------+
  | Master 1 |    | Master 2 |    | Master 3 |
  | Slots    |    | Slots    |    | Slots    |
  | 0-5460   |    | 5461-10922|   | 10923-16383|
  +----+-----+    +----+-----+    +----+-----+
       |              |              |
  +----+-----+   +----+-----+   +----+-----+
  | Replica 1|   | Replica 2|   | Replica 3|
  +----------+   +----------+   +----------+

  Memory: 64 GB total (across cluster).
  Maxmemory-policy: allkeys-lfu (evict least frequently used).
```

```
REDIS CACHING PATTERNS

  Cache-Aside (Lazy Loading):
    1. App checks Redis.
    2. Miss -> query DB -> write to Redis -> return.
    3. Next request -> Redis hit.

    Pros: only caches data that is actually requested.
    Cons: first request always misses (cold cache).

  Read-Through:
    1. App calls cache.get(key).
    2. Cache checks itself. Miss -> cache fetches from DB, stores, returns.
    3. App does not know about the DB.

    Pros: simpler app code (cache handles DB fetch).
    Cons: cache library must support read-through.

  Cache warming / Pre-loading:
    On startup or before peak traffic, pre-populate Redis with hot data.
    Prevents cold-cache stampedes.

    SELECT * FROM products WHERE category IN ('popular', 'deals')
    -> MSET to Redis.
```

---

## 13.10 Step 9: Level 5 — Database Query Cache & Materialized Views

```
DATABASE-LEVEL CACHING

  PostgreSQL:
    - Shared buffers (in-memory cache for table/index pages).
    - OS page cache (Linux caches frequently read files).
    - Materialized views: precomputed query results stored as tables.

  Materialized View:
    CREATE MATERIALIZED VIEW product_summary AS
      SELECT p.id, p.name, p.price, c.name AS category,
             AVG(r.rating) AS avg_rating, COUNT(r.id) AS review_count
      FROM products p
      JOIN categories c ON p.category_id = c.id
      LEFT JOIN reviews r ON p.id = r.product_id
      GROUP BY p.id, p.name, p.price, c.name;

    REFRESH MATERIALIZED VIEW CONCURRENTLY product_summary;
    -- Run every 5 minutes via cron or pg_cron.

  Benefits: complex JOINs and aggregations precomputed.
  Read the view instead of running the expensive query.
  "CONCURRENTLY" allows reads during refresh.
```

```
WHEN TO USE DATABASE-LEVEL CACHING

  Good for:
  - Complex aggregation queries (dashboards, reports).
  - Data that changes infrequently (product catalog summary).
  - When adding Redis is overkill for the use case.

  Not good for:
  - High-throughput caching (Redis is faster and scales better).
  - Real-time data (materialized views refresh on a schedule).
  - Per-user or per-session data.
```

---

## 13.11 Step 10: Cache Invalidation Strategies

```
THE HARDEST PROBLEM IN CACHING

  "There are only two hard things in Computer Science:
   cache invalidation and naming things." — Phil Karlton

  The question is: when data changes in the DB, how do you ensure
  the cache does not serve stale data?
```

```
INVALIDATION STRATEGIES

  Strategy                 | How it works                     | Trade-off
  -------------------------|----------------------------------|---------------------
  TTL-based expiry         | Key expires after N seconds.     | Simple. But serves
                           | Cache refilled on next read.     | stale data until
                           |                                  | TTL expires.
  Explicit invalidation    | On write, delete the cache key.  | Fresh data on next
  (cache-aside delete)     | Next read refills from DB.       | read. But cache miss
                           |                                  | after every write.
  Publish/subscribe        | On write, publish invalidation   | Near-real-time.
  invalidation             | event. All instances/layers      | More complex. Need
                           | evict the key.                   | reliable messaging.
  Version-based            | Store version number with cached | No stale reads.
                           | data. On write, increment        | Extra lookup to
                           | version. Reader checks version.  | check version.
  Write-through            | Write to cache AND DB together.  | Cache always fresh.
                           | Cache is always up-to-date.      | Slower writes.
  Event-driven (CDC)       | Debezium captures DB changes     | Eventually consistent.
                           | and publishes to Kafka. Consumer | Decoupled. Best for
                           | updates/invalidates cache.       | complex systems.
```

```
CHOOSING AN INVALIDATION STRATEGY

  Data type                  | Recommended strategy
  ---------------------------|--------------------------------------
  Product catalog            | TTL (5 min) + explicit invalidation
                             | on admin update.
  Prices                     | Short TTL (30 sec) OR write-through.
  Inventory count            | Event-driven (CDC) or very short TTL
                             | (5 sec). Cannot tolerate long staleness.
  User profile               | Explicit invalidation on update.
  User session               | Write-through (always in sync).
  Feature flags / config     | Pub/Sub invalidation + short L1 TTL.
  Search results             | TTL (30-60 sec). Invalidation too
                             | complex for search indexes.
  Static assets (CDN)        | Versioned URLs (best) or CDN purge API.
```

---

## 13.12 Step 11: Cache Write Patterns (Write-Through, Write-Behind, Write-Around)

```
WRITE-THROUGH

  App -> Write to Cache -> Cache writes to DB -> Return.

  Cache and DB are always in sync.
  Every write updates both.

  Pros: cache is always fresh. Read-after-write consistent.
  Cons: slower writes (two writes: cache + DB). Write amplification
  if data is rarely read after writing.

  Use for: session data, user preferences, frequently read after write.
```

```
WRITE-BEHIND (WRITE-BACK)

  App -> Write to Cache -> Return immediately.
  Cache asynchronously writes to DB (in batch, with delay).

  Pros: fastest write latency (only cache write). Batch writes
  reduce DB load. Good for high-volume writes.
  Cons: data loss risk if cache crashes before flushing to DB.
  Consistency gap: DB may be behind cache.

  Use for: analytics counters, view counts, non-critical data
  that can tolerate loss. NOT for financial data.
```

```
WRITE-AROUND

  App -> Write directly to DB -> Do NOT update cache.
  Cache is only populated on reads (cache-aside).

  Pros: no write amplification (cache not written on every update).
  Good when written data is rarely read immediately.
  Cons: read-after-write may return stale cached data.

  Use for: write-heavy data that is rarely read (logs, audit trails).
  Most common pattern: write-around + cache-aside for reads.
```

```
PATTERN COMPARISON

  Pattern         | Write latency | Read consistency | Data loss risk
  ----------------|---------------|------------------|-----------------
  Write-through   | High (2x)     | Strong           | None
  Write-behind    | Low (cache)   | Eventual         | Yes (cache crash)
  Write-around    | Medium (DB)   | Eventual         | None
  Cache-aside     | Medium (DB)   | Eventual         | None
  (write: DB only,|               | (stale until TTL |
   read: cache    |               |  or invalidation)|
   then DB)       |               |                  |
```

---

## 13.13 Step 12: Cache Stampede, Hot Keys & Thundering Herd

**Cache stampede (thundering herd):**
```
  What happens:
  1. A popular cache key expires.
  2. 1000 concurrent requests arrive for that key.
  3. ALL 1000 requests see a cache miss.
  4. ALL 1000 requests query the database.
  5. Database gets 1000 identical queries at once -> overloaded.
  6. This can cascade and bring the system down.

  Solutions:

  1. Lock-based (mutex):
     Only ONE request fetches from DB. Others wait for the cache
     to be repopulated.
     Redis: SET lock_key NX EX 5 (acquire lock with TTL).
     If lock acquired -> fetch from DB -> write to cache -> release.
     If lock not acquired -> wait 50ms -> retry cache.

  2. Probabilistic early expiry:
     Each reader has a small probability of refreshing the cache
     BEFORE it expires. Spreads refresh load over time.
     refresh_probability = max(0, (remaining_ttl - threshold) / window)

  3. Background refresh (stale-while-revalidate):
     Cache returns stale value immediately.
     A background thread/task refreshes the cache.
     User always gets a fast response.

  4. Never-expire + async refresh:
     Cache key never expires. A background job refreshes it
     on a schedule. Readers always get a hit.
     Best for hot, predictable data.
```

**Hot key problem:**
```
  A single cache key receives disproportionate traffic.
  Example: viral product page, trending topic, flash sale.

  Redis is single-threaded per shard. One hot key = one shard
  under extreme load -> queuing -> latency spike.

  Solutions:
  1. L1 (in-process) cache: hot key served from JVM memory.
     No Redis call at all for the hottest keys.
  2. Key replication: store copies as key:1, key:2, ... key:N.
     Readers randomly pick a copy. Load spread across shards.
  3. Read replicas: Redis read replicas serve hot reads.
  4. Rate limit: if one key gets 10K RPS, is that legitimate?
     If bot traffic, rate-limit at the gateway.
```

---

## 13.14 Step 13: Cache Sizing, Eviction & TTL Strategy

```
CACHE SIZING

  Formula:
    cache_size = number_of_items * avg_item_size * overhead_factor

  Example (product cache):
    10M products * 2 KB avg (JSON) * 1.5 (Redis overhead) = 30 GB.
    But 80/20 rule: only 20% are frequently accessed.
    Effective hot set: 2M * 2 KB * 1.5 = 6 GB.
    A 16 GB Redis instance is comfortable.

  Monitor and adjust:
    - Track hit rate. If < 90%, cache is too small (evicting hot data).
    - Track eviction rate. High evictions = need more memory.
    - Track memory usage. If near maxmemory, increase or tune TTLs.
```

```
EVICTION POLICIES

  Policy                | How it works                     | Best for
  ----------------------|----------------------------------|--------------------
  LRU (Least Recently   | Evict the key not accessed for   | General purpose.
  Used)                 | the longest time.                | Good default.
  LFU (Least Frequently | Evict the key accessed the       | Keeps hot keys.
  Used)                 | fewest times.                    | Better for skewed
                        |                                  | access patterns.
  TTL-based             | Evict keys that have expired.    | Natural expiry.
  Random                | Evict a random key.              | Simple. Okay for
                        |                                  | uniform access.
  allkeys-lru           | LRU across ALL keys.             | When all keys are
                        |                                  | cache candidates.
  volatile-lru          | LRU only among keys WITH a TTL.  | When some keys must
                        |                                  | not be evicted.
  allkeys-lfu           | LFU across ALL keys.             | Best for caching
                        |                                  | (Redis 4.0+).
  noeviction            | Return error when memory full.   | When data loss is
                        |                                  | unacceptable.

  Recommended: allkeys-lfu for caching workloads.
  LFU keeps frequently accessed data even if it was accessed
  a while ago (unlike LRU which evicts based on recency alone).
```

```
TTL STRATEGY

  Data type                  | TTL             | Reasoning
  ---------------------------|-----------------|-----------------------------
  Static config / flags      | 5-15 min        | Changes rarely; periodic
                             |                 | refresh is fine.
  Product catalog            | 5-10 min        | Admin updates are infrequent.
  Prices                     | 30-60 sec       | Changes more often; shorter
                             |                 | TTL limits staleness.
  Search results             | 30-60 sec       | Tolerate slight staleness.
  Inventory count            | 5-10 sec        | Must be reasonably fresh.
  User session               | 30-60 min       | Matches session timeout.
  API rate limit counters    | Window size      | 1 sec / 1 min / 1 hour.
  CDN static assets          | 1 year           | Use versioned URLs to bust.

  Anti-pattern: same TTL for all keys.
  Different data has different freshness requirements.
  Set TTLs deliberately, not uniformly.
```

---

## 13.15 Step 14: Consistency — Cache vs. Source of Truth

```
THE FUNDAMENTAL TRADE-OFF

  Stronger consistency -> more cache invalidation -> more DB load -> higher latency.
  Weaker consistency  -> longer TTLs -> better performance -> stale data risk.

  You must choose per data type.
```

```
CONSISTENCY MODELS FOR CACHING

  Model                    | How it works                     | Use case
  -------------------------|----------------------------------|---------------------
  Strong (read-your-writes)| After a write, subsequent reads  | User profile update:
                           | ALWAYS return the new value.     | user must see their
                           | Requires write-through or        | own changes.
                           | immediate invalidation + refill. |
  Eventual (bounded)       | After a write, reads may return  | Product catalog:
                           | stale data for up to TTL seconds.| 5 min stale is OK.
                           | Eventually consistent.           |
  Best-effort              | No guarantee. Data may be stale  | Analytics, counts,
                           | indefinitely until TTL or        | recommendations.
                           | explicit invalidation.           |
```

```
READ-YOUR-WRITES CONSISTENCY

  Problem:
    User updates profile name to "Prakash."
    Immediately refreshes page.
    Cache still has old name "Old Name" (TTL not expired).
    User sees stale data. Frustrating.

  Solutions:
  1. Write-through: write updates cache AND DB. Next read -> fresh.
  2. Delete-on-write: delete cache key on write. Next read -> DB -> cache.
  3. Client-side hint: after write, client adds header "X-Cache-Bypass: true."
     Server skips cache, reads from DB for this one request.
  4. Version stamp: write increments version in Redis.
     Reader compares version. If cached version < current, refetch from DB.
```

---

## 13.16 Step 15: Monitoring & Observability

```
KEY CACHE METRICS

  Metric                          | Alert threshold          | What it means
  --------------------------------|--------------------------|-------------------
  Cache hit rate                  | < 90% (warn), < 80%     | Cache is not
                                  | (critical)               | effective. Check
                                  |                          | sizing, TTLs, keys.
  Cache miss rate                 | Inverse of hit rate.     | High miss rate =
                                  |                          | cold cache or wrong
                                  |                          | data cached.
  Eviction rate                   | > 0 sustained            | Cache is full.
                                  |                          | Need more memory
                                  |                          | or shorter TTLs.
  Memory usage                    | > 80% of maxmemory       | Approaching capacity.
  Latency (GET/SET)               | p99 > 2 ms               | Redis overloaded or
                                  |                          | network issue.
  Connection count                | > 80% of max connections | Connection pool
                                  |                          | exhaustion risk.
  Key count / DB size             | Monitor trend            | Unbounded growth =
                                  |                          | missing TTLs.
  Stale serves                    | Monitor count            | How often stale
  (stale-while-revalidate)        |                          | data is served.
```

```
MONITORING STACK

  Redis:
    INFO command -> memory, connections, keyspace, hit/miss.
    Redis Exporter -> Prometheus -> Grafana dashboard.
    SLOWLOG -> identify slow commands.
    CLIENT LIST -> connection tracking.

  Application (Caffeine L1):
    cache.stats() -> hitCount, missCount, evictionCount, loadTime.
    Micrometer CaffeineCacheMetrics -> Prometheus.

  CDN:
    CloudFront / Fastly dashboards -> hit rate, origin requests,
    error rate, bandwidth saved.

  Essential alerts:
  1. Cache hit rate < 90%.
  2. Redis memory > 80%.
  3. Eviction rate > threshold.
  4. Redis latency p99 > 2 ms.
  5. CDN origin request rate spike (CDN cache miss storm).
```

---

## 13.17 Step 16: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Staleness vs. performance        | Longer TTL = better hit rate but more stale
                                   | data. Choose TTL based on data type, not
                                   | one-size-fits-all.
  Cache-aside vs. write-through    | Cache-aside is simpler but allows stale reads
                                   | after writes. Write-through ensures freshness
                                   | but doubles write latency.
  In-process (L1) inconsistency    | Each app instance has its own L1. After a
                                   | write, other instances serve stale L1 data.
                                   | Mitigate with short TTL or Pub/Sub invalidation.
  Cold cache on deploy/restart     | New instances start with empty L1 and may
                                   | trigger a cache stampede. Pre-warm or use
                                   | stale-while-revalidate.
  Cache stampede / thundering herd | Popular key expires -> all requests hit DB.
                                   | Use mutex, probabilistic refresh, or
                                   | background refresh.
  Hot key                          | One key gets extreme traffic -> Redis shard
                                   | overloaded. Use L1 cache, key replication,
                                   | or read replicas.
  Caching too much                 | Caching rarely-read data wastes memory and
                                   | increases eviction of hot data. Cache only
                                   | hot, frequently read data.
  Caching too little               | Hit rate too low -> DB overloaded. Identify
                                   | hot queries and cache them.
  Missing TTLs                     | Keys without TTL accumulate forever. Redis
                                   | fills up. Always set a TTL.
  Cache poisoning                  | A bad value gets cached (error response, null).
                                   | Do NOT cache error responses. Validate before
                                   | caching.
  Negative caching                 | Cache "key not found" results to prevent
                                   | repeated DB lookups for non-existent keys.
                                   | Use short TTL (30 sec) to avoid permanent
                                   | "not found" for newly created data.
  Over-invalidation                | Invalidating too aggressively reduces hit rate.
                                   | Find the right balance between freshness and
                                   | performance.
  Ignoring CDN                     | CDN is the biggest force multiplier. A 90%
                                   | CDN hit rate means origin sees 10% of traffic.
                                   | Always use CDN for static and semi-static data.
  Not monitoring                   | Cache silently degrades (hit rate drops,
                                   | evictions increase). Without monitoring, you
                                   | only notice when the DB crashes.
```

---

## 13.18 Problem 13 Summary

- **Caching is multi-layered:** L0 (browser) → L1 (CDN) → L2 (gateway) → L3 (in-process) → L4 (Redis) → L5 (DB materialized views). Each layer peels off traffic; no single layer does all the work.
- **The numbers matter:** the difference between 90% and 95% hit rate is the difference between DB overload and smooth operation. Every percentage point counts at scale.
- **Browser cache (L0):** use `Cache-Control`, `ETag`, `stale-while-revalidate` headers. Zero latency, zero network. Cache static assets for a year with versioned URLs.
- **CDN (L1):** edge caching for static and semi-static content. 60-90% hit rate is typical. Invalidate via TTL, purge API, versioned URLs, or surrogate keys.
- **In-process cache (L3):** Caffeine for sub-millisecond access to hot data. Limited by JVM heap. Inconsistent across instances — use short TTLs or Pub/Sub invalidation.
- **Distributed cache (L4):** Redis for shared, consistent caching across instances. Use Redis Cluster for HA. `allkeys-lfu` eviction. Cache-aside is the most common pattern.
- **Invalidation is the hardest problem:** TTL-based (simple, eventual), explicit delete (fresh, more misses), Pub/Sub (near-real-time, complex), event-driven CDC (decoupled, scalable). Choose per data type.
- **Write patterns:** write-through (strong consistency, slower writes), write-behind (fast writes, data loss risk), write-around (no write amplification, stale reads), cache-aside (most common, eventual consistency).
- **Cache stampede:** when a hot key expires and N requests all hit the DB. Mitigate with mutex locks, probabilistic early expiry, background refresh, or never-expire + async refresh.
- **Hot keys:** one key overloads one Redis shard. Mitigate with L1 in-process cache, key replication, or read replicas.
- **TTLs are not one-size-fits-all:** static config (5-15 min), prices (30-60 sec), inventory (5-10 sec), sessions (30-60 min), CDN assets (1 year). Set deliberately per data type.
- **Consistency trade-off:** stronger consistency = more invalidation = more DB load. Choose read-your-writes for user-facing writes; eventual for catalog/search/analytics.
- **Monitor everything:** cache hit rate (alert < 90%), eviction rate, memory usage, latency, CDN origin request rate. Without monitoring, cache silently degrades until the DB crashes.

---

# Problem 14: How to Handle Cache Invalidation Challenges?

## 14.1 Problem Statement

> Your distributed system has multiple cache layers (in-process, Redis, CDN) and dozens of microservices. Data changes originate from multiple services, admin tools, batch jobs, and third-party integrations. Users report seeing stale data — old prices, outdated inventory, incorrect profile information — even minutes after updates. Walk through every cache invalidation challenge you have encountered and the production-grade solutions for each.

This question tests:
- **Deep understanding of cache consistency** — not just "delete the key," but the race conditions, ordering problems, and failure modes that make invalidation the hardest problem in distributed systems.
- **Cross-layer thinking** — invalidation must ripple through L1 (in-process), L4 (Redis), L1 (CDN), and sometimes the browser.
- **Microservices awareness** — who owns the cache? Who is responsible for invalidating when data changes in another service?
- **Production experience** — the difference between textbook invalidation and what actually breaks at scale.

---

## 14.2 Step 1: Why Cache Invalidation Is Hard

```
THE FUNDAMENTAL PROBLEM

  Source of truth: Database (PostgreSQL).
  Cached copies: in-process (per instance), Redis (shared), CDN (edge nodes).

  When data changes in the DB, ALL cached copies must be updated or removed.
  If ANY copy remains stale, users see inconsistent data.

  Why this is hard:
  1. Multiple layers: data is cached at 3-6 different levels.
  2. Multiple instances: 20 app instances each have their own L1 cache.
  3. Multiple regions: CDN has 200+ edge nodes worldwide.
  4. Multiple writers: different services, admin tools, batch jobs
     can all modify the same data.
  5. Concurrency: reads and writes happen simultaneously.
     Race conditions between "read old value" and "write new value."
  6. Ordering: invalidation events may arrive out of order.
  7. Failure: invalidation messages can be lost (network failure,
     service crash, Kafka consumer lag).
  8. Atomicity: you cannot atomically update DB + cache + CDN.
     There is always a window of inconsistency.
```

```
THE COST OF GETTING IT WRONG

  Scenario                        | Impact
  --------------------------------|------------------------------------------
  Stale price displayed           | Customer pays wrong price -> revenue loss
                                  | or legal issue.
  Stale inventory (shows "in      | Customer orders out-of-stock item ->
  stock" when sold out)           | order cancelled -> bad experience.
  Stale user profile              | User sees old name/email after update ->
                                  | frustration, support tickets.
  Stale permissions               | User still has access after revocation ->
                                  | security breach.
  Stale feature flag              | Old code path active after flag change ->
                                  | incidents, rollback failures.
  Cache permanently stale         | Cache never refreshes (missing TTL, lost
  (phantom stale entry)           | invalidation) -> permanent bad data.
```

---

## 14.3 Step 2: The Six Core Invalidation Strategies (Deep Dive)

```
STRATEGY 1: TTL-BASED EXPIRY

  How it works:
    Every cached entry has a time-to-live (TTL).
    After TTL expires, the entry is removed.
    Next read fetches from DB and repopulates cache.

  Implementation:
    SET product:123 "{...}" EX 300   // Expires in 5 minutes.

  Strengths:
    - Simplest to implement. No invalidation logic needed.
    - Self-healing: even if invalidation fails, TTL eventually fixes it.
    - Works across all cache layers (browser, CDN, Redis, L1).

  Weaknesses:
    - Serves stale data for up to TTL duration after a write.
    - TTL too short -> low hit rate, high DB load.
    - TTL too long -> prolonged staleness.

  When to use:
    - As a SAFETY NET behind every other strategy.
    - Always set a TTL, even when using explicit invalidation.
    - Appropriate as the sole strategy for data where bounded
      staleness (5 sec to 5 min) is acceptable.
```

```
STRATEGY 2: EXPLICIT DELETE (CACHE-ASIDE INVALIDATION)

  How it works:
    On write, explicitly delete the cache key.
    Next read sees a cache miss, fetches from DB, repopulates.

  Implementation:
    // Write path:
    db.update("UPDATE products SET price=99.99 WHERE id=123");
    redis.del("product:123");   // Invalidate cache.

  Strengths:
    - Next read gets fresh data from DB.
    - Simple, widely used.

  Weaknesses:
    - Race condition (see Section 14.4).
    - Cache miss after every write (latency spike for next reader).
    - If delete fails (Redis down), cache stays stale.

  Best practice:
    - Delete AFTER DB write (not before).
    - Combine with TTL as safety net.
    - Retry delete on failure (idempotent operation).
```

```
STRATEGY 3: WRITE-THROUGH (UPDATE CACHE ON WRITE)

  How it works:
    On write, update BOTH DB and cache atomically (or sequentially).

  Implementation:
    db.update("UPDATE products SET price=99.99 WHERE id=123");
    redis.set("product:123", newProduct, EX, 300);

  Strengths:
    - Cache is always up-to-date. No stale read window.
    - Read-after-write consistency.

  Weaknesses:
    - Slower writes (two writes: DB + cache).
    - Race condition if two concurrent writes update cache in
      wrong order (last-writer-wins, but may not be the latest DB value).
    - Write amplification: caches data even if nobody reads it.

  When to use:
    - User profile, session data, settings — data that is always
      read immediately after writing.
```

```
STRATEGY 4: PUBLISH/SUBSCRIBE INVALIDATION

  How it works:
    On write, publish an invalidation event to a channel.
    All subscribers (app instances, cache layers) evict the key.

  Implementation:
    // Writer:
    db.update("UPDATE products SET price=99.99 WHERE id=123");
    redis.publish("cache-invalidation", "product:123");

    // All app instances subscribe:
    redis.subscribe("cache-invalidation", key -> {
        localCache.invalidate(key);    // Evict from L1.
        redis.del(key);                // Evict from L4 (if needed).
    });

  Strengths:
    - Near-real-time invalidation across all instances.
    - Solves the L1 inconsistency problem (all instances notified).

  Weaknesses:
    - Redis Pub/Sub is fire-and-forget. If a subscriber is down
      when the event is published, it misses the invalidation.
    - No persistence of events. No replay.
    - Need TTL as safety net for missed events.

  Better alternative: use Kafka instead of Redis Pub/Sub for
  durable, replayable invalidation events (see Strategy 6).
```

```
STRATEGY 5: VERSION-BASED INVALIDATION

  How it works:
    Store a version number alongside the cached data.
    On write, increment the version in a central store (Redis, DB).
    On read, compare cached version with current version.
    If cached version < current version, refetch from DB.

  Implementation:
    // Write path:
    db.update("UPDATE products SET price=99.99, version=version+1 WHERE id=123");
    redis.incr("product:123:version");

    // Read path:
    cachedProduct = localCache.get("product:123");
    currentVersion = redis.get("product:123:version");
    if (cachedProduct == null || cachedProduct.version < currentVersion) {
        product = db.query("SELECT * FROM products WHERE id=123");
        redis.set("product:123", product, EX, 300);
        localCache.put("product:123", product);
    }

  Strengths:
    - No stale reads (version check catches every update).
    - Works across L1 and L4 (version is the single source of truth).

  Weaknesses:
    - Extra Redis call on every read (check version).
    - More complex implementation.

  When to use:
    - When L1 cache consistency is critical but you cannot
      afford to invalidate all L1 instances via Pub/Sub.
```

```
STRATEGY 6: EVENT-DRIVEN INVALIDATION (CDC + KAFKA)

  How it works:
    Debezium (CDC) captures every DB change from the WAL/binlog.
    Publishes change events to Kafka.
    A consumer reads Kafka and invalidates/updates the cache.

  Architecture:
    DB (WAL) -> Debezium -> Kafka topic "db.products" -> Cache Updater
                                                          -> redis.del()
                                                          -> redis.publish()
                                                          -> CDN purge API

  Strengths:
    - Decoupled: writers do not need cache-awareness.
    - Reliable: Kafka is durable and replayable.
    - Handles all writers (app, admin tool, batch job, migration).
    - Can invalidate multiple cache layers from one event.

  Weaknesses:
    - Eventual consistency (Kafka consumer lag: typically ms to sec).
    - Infrastructure complexity (Debezium, Kafka, consumer service).
    - Ordering challenges if processing multiple partitions.

  When to use:
    - Large systems with many services writing to the same DB.
    - When you cannot modify every writer to add cache invalidation.
    - When you need a single, reliable invalidation pipeline.
    - The GOLD STANDARD for production systems at scale.
```

---

## 14.4 Step 3: The Classic Race Condition — Read-Update-Cache

```
THE RACE CONDITION (cache-aside + explicit delete)

  Thread A (reader)                Thread B (writer)
  ----------------                 -----------------
  1. GET product:123 from Redis
     -> MISS (cache empty or expired)
                                   2. UPDATE products SET price=99.99
                                      WHERE id=123  (DB updated)
                                   3. DEL product:123 from Redis
                                      (invalidate cache — but cache
                                       is already empty, so no effect)
  4. SELECT * FROM products
     WHERE id=123
     -> Returns OLD value (price=89.99)
     (DB read happened BEFORE the
      write committed, or read from
      a read replica with lag)
  5. SET product:123 = {price:89.99}
     in Redis (TTL 300s)
     -> Cache now has STALE data!

  Result: cache has old price (89.99) even though DB has new price (99.99).
  This stale entry will persist for up to TTL (5 minutes).

  This is the MOST COMMON cache invalidation bug.
```

```
SOLUTIONS TO THE RACE CONDITION

  1. Double-Delete (Delayed Invalidation) — see Section 14.6.
     Delete cache on write. Wait a short delay. Delete again.
     The second delete catches the stale write from the reader.

  2. Write-through instead of delete.
     On write, SET the new value in cache (not delete).
     But this has its own race condition with concurrent writes.

  3. Lease-based invalidation (Facebook Memcache paper).
     When a reader gets a cache miss, it receives a "lease" (token).
     When it writes back to cache, it must present the lease.
     If the cache key was invalidated between miss and write,
     the lease is revoked, and the stale write is rejected.

  4. Version-based (Section 14.10).
     Cache stores version. If the cached version < DB version
     at write time, the stale write is detected and discarded.

  5. Accept bounded staleness.
     Short TTL (30 sec) means the stale data self-corrects quickly.
     For many use cases, this is acceptable and much simpler.
```

---

## 14.5 Step 4: Delete vs. Update — Which Is Safer?

```
DELETE THE CACHE KEY (recommended)

  On write: redis.del("product:123")

  Why:
  - Idempotent: deleting twice is fine.
  - Simple: no serialization, no building the cache object.
  - Next reader fetches fresh data from DB.
  - Lower risk of writing a stale value to cache.

  Risk:
  - Cache miss after every write (one cold read).
  - Race condition if a concurrent reader writes stale data
    back to cache after the delete (see Section 14.4).
```

```
UPDATE THE CACHE KEY

  On write: redis.set("product:123", newProduct)

  Why:
  - No cache miss after write. Readers always get a hit.
  - Read-after-write consistency (if no concurrent writes).

  Risk:
  - Concurrent writes can overwrite with stale data.

    Thread A: UPDATE price=99, SET cache={price:99}
    Thread B: UPDATE price=109, SET cache={price:109}

    If Thread B's DB write happens first but Thread A's cache
    write happens last: cache has price=99, DB has price=109.
    -> STALE.

  - More complex: writer must build the full cache object.
  - Write amplification: updates cache even if nobody reads it.
```

```
VERDICT

  DELETE is safer for most systems:
  - Simpler, idempotent, lower risk of stale writes.
  - Combined with TTL, the cold read after delete is brief.

  UPDATE is better when:
  - Read-after-write consistency is critical.
  - There is a single writer (no concurrent write conflicts).
  - You add a version check (reject updates with version < current).

  In practice: use DELETE + TTL + double-delete for most cases.
  Use write-through (UPDATE) only for session/profile data
  where read-after-write matters and writes are serialized.
```

---

## 14.6 Step 5: Double-Delete (Delayed Invalidation) Pattern

```
THE DOUBLE-DELETE PATTERN

  Problem: the race condition in Section 14.4. A reader writes
  stale data to cache after the writer's delete.

  Solution: delete the cache key TWICE — once immediately, and
  once after a short delay.

  Writer flow:
    1. DELETE cache key.               // First delete.
    2. UPDATE database.                // Write new value to DB.
    3. Wait 500ms - 2 seconds.         // Short delay.
    4. DELETE cache key again.         // Second delete (catches stale writes).

  Why it works:
    - Step 1: clears any existing stale cache.
    - Step 2: writes to DB.
    - Between steps 2 and 4, a reader might fetch old data from
      a read replica and write it to cache.
    - Step 4: the second delete removes that stale entry.
    - After step 4, the next reader fetches from DB and gets
      the correct new value.

  The delay must be longer than:
    - DB read replica lag (typically 10-100ms).
    - The time for a reader to complete its DB query and write
      to cache (typically 5-50ms).
    - A safe value: 500ms to 2 seconds.
```

```
IMPLEMENTATION

  // Synchronous first delete + async delayed second delete.
  public void updateProduct(Product product) {
      redis.del("product:" + product.getId());      // First delete.
      db.update(product);                             // DB write.

      // Schedule delayed second delete (non-blocking).
      scheduler.schedule(() -> {
          redis.del("product:" + product.getId());  // Second delete.
      }, 1, TimeUnit.SECONDS);
  }

  // Alternative: publish delayed invalidation to a queue.
  public void updateProduct(Product product) {
      redis.del("product:" + product.getId());
      db.update(product);
      kafka.send("cache-invalidation-delayed",
                 new InvalidationEvent("product:" + product.getId()),
                 delayMs = 1000);
  }
```

```
TRADE-OFFS

  Pros:
  - Eliminates the most common race condition.
  - Simple to implement.
  - Works with cache-aside pattern.

  Cons:
  - Between the DB write and the second delete (1 second window),
    stale data may still be served.
  - Two cache misses per write (first delete + second delete).
  - If the delayed delete fails (process crash, scheduler failure),
    you fall back to TTL-based expiry (safety net).
```

---

## 14.7 Step 6: Event-Driven Invalidation (CDC + Kafka)

```
CDC-BASED INVALIDATION (production-grade)

  This is the gold standard for large systems with multiple writers.

  Architecture:

  +------------+     +----------+     +-------------------+
  | PostgreSQL |     | Debezium |     | Kafka topic:      |
  | (WAL)      |---->| (CDC)    |---->| db.public.products|
  +------------+     +----------+     +--------+----------+
                                               |
                         +---------------------+---------------------+
                         |                     |                     |
                         v                     v                     v
                  +-------------+       +-------------+       +----------+
                  | Cache       |       | Search      |       | CDN      |
                  | Invalidator |       | Indexer     |       | Purger   |
                  | Service     |       | (Elastic)   |       | Service  |
                  +------+------+       +-------------+       +-----+----+
                         |                                          |
                    +----+----+                               +-----+-----+
                    | Redis   |                               | CloudFront|
                    | DEL key |                               | Purge API |
                    +---------+                               +-----------+
                    | L1 Pub/Sub                              |
                    | invalidation                            |
                    +---------+
```

```
HOW IT WORKS

  1. Any writer (app, admin, batch job) writes to PostgreSQL.
     No cache logic in the writer.

  2. Debezium reads the PostgreSQL WAL (Write-Ahead Log).
     It captures every INSERT, UPDATE, DELETE as a change event.

  3. Debezium publishes the event to Kafka:
     topic: "db.public.products"
     key: product_id
     value: { before: {price: 89.99}, after: {price: 99.99}, op: "u" }

  4. Cache Invalidator Service consumes the event:
     - redis.del("product:" + event.key)
     - redis.publish("cache-invalidation", "product:" + event.key)
       (so all L1 instances evict their local copy)

  5. CDN Purger Service consumes the same event:
     - cloudfront.createInvalidation("/api/products/" + event.key)
     - or uses surrogate key purge (Fastly).

  6. Search Indexer consumes the same event:
     - Updates Elasticsearch index.

  Benefits:
  - Single invalidation pipeline handles all writers.
  - Writers do not need cache awareness.
  - Kafka is durable and replayable (if consumer falls behind,
    it catches up without losing events).
  - Multiple consumers can react to the same event.
```

```
HANDLING CDC EVENT ORDERING

  Problem: events for the same key may be processed out of order
  if Kafka topic has multiple partitions.

  Solution:
  - Partition key = entity ID (product_id).
  - All events for the same product go to the same partition.
  - Same partition = strict ordering.
  - Consumer processes events sequentially within a partition.

  Edge case: if you use multiple consumers per partition (parallel
  processing), ordering is broken. Use single-consumer-per-partition
  for invalidation topics.
```

---

## 14.8 Step 7: Cross-Layer Invalidation (L1 → L4 → CDN)

```
THE CROSS-LAYER PROBLEM

  Data changes in DB. You must invalidate:
  1. L1 (in-process cache) — every app instance has its own copy.
  2. L4 (Redis) — shared distributed cache.
  3. CDN (CloudFront, Fastly) — edge nodes worldwide.
  4. Browser (user's local cache) — cannot directly invalidate.

  Each layer has a different invalidation mechanism.
  Missing any layer = stale data for some users.
```

```
CROSS-LAYER INVALIDATION STRATEGY

  Layer    | Invalidation mechanism              | Latency
  ---------|-------------------------------------|-------------------
  L1       | Redis Pub/Sub or Kafka consumer     | < 100 ms
  (in-     | in each instance evicts local key.  |
  process) | Safety net: short TTL (1-5 min).    |
  ---------|-------------------------------------|-------------------
  L4       | Redis DEL (from CDC consumer or     | < 10 ms
  (Redis)  | explicit delete in writer).         |
  ---------|-------------------------------------|-------------------
  CDN      | Purge API (CloudFront invalidation, | 1-30 seconds
           | Fastly instant purge) or surrogate  | (CloudFront ~15s,
           | key purge. Safety net: short TTL.   | Fastly ~150ms)
  ---------|-------------------------------------|-------------------
  Browser  | Cannot push invalidation. Must rely | Up to max-age
           | on short Cache-Control max-age or   | duration.
           | ETag revalidation (no-cache).       |
```

```
ORCHESTRATING CROSS-LAYER INVALIDATION

  On data change (CDC event or explicit write):

  Step 1: DEL from Redis (L4).          <- Immediate.
  Step 2: PUBLISH to Pub/Sub channel.   <- All L1 instances evict.
  Step 3: Call CDN purge API.           <- 1-30 second propagation.
  Step 4: Browser? Cannot push.         <- Rely on short max-age or ETag.

  Order matters: invalidate inner layers first (Redis, L1),
  then outer layers (CDN). This minimizes the stale window.

  If CDN purge fails:
  - Retry with exponential backoff.
  - CDN TTL is the safety net (e.g., 5 min max-age).
  - Log and alert on sustained purge failures.
```

---

## 14.9 Step 8: Invalidation in Microservices (Who Owns the Cache?)

```
THE OWNERSHIP PROBLEM

  Product Service owns the "products" table.
  Pricing Service owns the "prices" table.
  Order Service reads product + price data (and caches it).

  When Product Service updates a product, who invalidates
  Order Service's cache?

  Option A: Product Service explicitly calls Order Service
            to invalidate its cache.
            Problem: tight coupling. Product Service must know
            about every consumer's cache.

  Option B: Product Service publishes a domain event.
            Order Service subscribes and invalidates its own cache.
            Better: loose coupling via events.

  Option C: CDC pipeline. All DB changes flow through Kafka.
            Each service has its own CDC consumer that invalidates
            its own cache. No service-to-service coupling.
            Best: fully decoupled.
```

```
CACHE OWNERSHIP RULES

  Rule                                | Rationale
  ------------------------------------|---------------------------------------
  The service that CACHES the data    | Only the caching service knows its
  is responsible for invalidating it. | cache key structure, TTLs, and layers.
  ------------------------------------|---------------------------------------
  The service that OWNS the data      | The owner must announce changes
  is responsible for announcing       | (via domain events or CDC) so that
  changes.                            | downstream caches can react.
  ------------------------------------|---------------------------------------
  Never reach into another service's  | Breaks encapsulation. If Cache Service
  cache directly.                     | X deletes keys in Service Y's Redis,
                                      | you have a distributed coupling
                                      | nightmare.
  ------------------------------------|---------------------------------------
  Use domain events, not cache events.| Publish "ProductPriceChanged," not
                                      | "InvalidateCache:product:123."
                                      | Let each consumer decide how to react.
```

```
EXAMPLE: EVENT-DRIVEN MICROSERVICE INVALIDATION

  Product Service:
    -> Updates product in DB.
    -> Publishes event: "ProductUpdated" { id: 123, name: "...", price: 99 }

  Order Service (subscriber):
    -> Receives "ProductUpdated" event.
    -> Invalidates its own cache: redis.del("order-product:123")
    -> Evicts L1: localCache.invalidate("order-product:123")

  Search Service (subscriber):
    -> Receives "ProductUpdated" event.
    -> Re-indexes product in Elasticsearch.

  No service touches another service's cache. Each handles its own.
```

---

## 14.10 Step 9: Cache Versioning & Lease-Based Invalidation

```
CACHE VERSIONING

  Concept: every cached entry has a version. On read, compare the
  cached version with the current version in a central store.

  Implementation:
    // Write:
    db.update(product);  // product.version is auto-incremented.
    redis.set("product:123:version", product.version);

    // Read:
    cached = localCache.get("product:123");
    currentVersion = redis.get("product:123:version");
    if (cached == null || cached.version < currentVersion) {
        product = db.findById(123);
        redis.set("product:123", product, EX, 300);
        localCache.put("product:123", product);
    }
    return cached;  // or product if refetched.

  Strengths:
  - L1 instances always check version before serving.
  - No stale reads (version is always current).
  - Simpler than Pub/Sub (no messaging infrastructure for L1).

  Weaknesses:
  - Extra Redis GET on every read (version check).
  - Slightly higher latency for reads.
  - Version counter must be monotonically increasing.
```

```
LEASE-BASED INVALIDATION (Facebook Memcache paper)

  Problem: reader gets a cache miss, fetches from DB (old data
  from read replica), writes stale data to cache.

  Solution: memcache issues a "lease" (token) on cache miss.
  The reader must present the lease when writing back. If the
  key was invalidated between miss and write-back, the lease
  is revoked and the stale write is rejected.

  Flow:
    1. Reader: GET product:123 -> MISS. Memcache returns lease_token=abc.
    2. Writer: UPDATE product in DB. DELETE product:123 from cache.
       -> Memcache revokes all outstanding leases for product:123.
    3. Reader: SET product:123 = {old_data} with lease_token=abc.
       -> Memcache REJECTS the write (lease revoked). Stale data NOT cached.
    4. Next reader: GET product:123 -> MISS. New lease issued.
       Fetches from DB (which now has new data). Writes to cache. Fresh.

  This ELIMINATES the classic race condition entirely.

  Implementation in Redis (approximation):
    // On cache miss, set a lease:
    String leaseId = UUID.randomUUID().toString();
    redis.set("product:123:lease", leaseId, NX, EX, 10);

    // After DB fetch, only write if lease is still yours:
    String currentLease = redis.get("product:123:lease");
    if (leaseId.equals(currentLease)) {
        redis.set("product:123", product, EX, 300);
        redis.del("product:123:lease");
    }
    // If lease was cleared by an invalidation, write is skipped.

    // On invalidation (writer):
    redis.del("product:123");          // Delete cached value.
    redis.del("product:123:lease");    // Revoke outstanding leases.
```

---

## 14.11 Step 10: Negative Caching & Cache Penetration

```
CACHE PENETRATION

  Problem: requests for data that does NOT exist (e.g., product ID
  that was never created). Every request is a cache miss because
  there is nothing to cache. Every request hits the DB.

  If an attacker sends 100K requests/sec for random non-existent IDs,
  the cache is useless. All traffic hits the DB. DB is overloaded.

  This is called CACHE PENETRATION.
```

```
SOLUTION 1: NEGATIVE CACHING (cache the absence)

  On DB miss (key not found), cache a sentinel value:
    redis.set("product:999999", "NULL", EX, 30);

  On read:
    cached = redis.get("product:999999");
    if ("NULL".equals(cached)) {
        return null;  // Known non-existent. Do not query DB.
    }

  TTL must be SHORT (30-60 seconds) because:
  - If the product is created later, the negative cache entry
    must expire to allow discovery.
  - Long TTL = permanently "not found" for newly created data.
```

```
SOLUTION 2: BLOOM FILTER (probabilistic existence check)

  A Bloom filter is a space-efficient data structure that tells you:
  - "Definitely NOT in the set" (100% accurate).
  - "Probably in the set" (small false positive rate).

  Implementation:
    // On startup, load all existing product IDs into Bloom filter.
    BloomFilter<String> productExists = BloomFilter.create(
        Funnels.stringFunnel(UTF_8), 10_000_000, 0.01);  // 1% FP rate.

    // On read:
    if (!productExists.mightContain("product:" + id)) {
        return null;  // Definitely does not exist. Skip DB + cache.
    }
    // Proceed with normal cache-aside logic.

  Strengths:
  - O(1) lookup, tiny memory footprint (~10 MB for 10M items).
  - Blocks 99%+ of non-existent key queries.

  Weaknesses:
  - False positives (1%): some non-existent keys pass through.
  - Must be updated when new items are created.
  - Cannot remove items (unless using Counting Bloom Filter).

  Use BOTH negative caching AND Bloom filter for defense-in-depth.
```

---

## 14.12 Step 11: Bulk Invalidation & Cache Flush Strategies

```
WHEN BULK INVALIDATION IS NEEDED

  Scenario                          | Example
  ----------------------------------|------------------------------------
  Price change across category      | All electronics products get a 10%
                                    | discount. 50K cache keys affected.
  Schema migration                  | Product JSON format changed. All
                                    | cached entries are in old format.
  Bug fix                           | Wrong prices were cached for 10
                                    | minutes due to a bug. Flush all.
  Data sync from external system    | Bulk import from ERP updated 100K
                                    | products.
  Feature flag change               | New UI feature enabled. Cached
                                    | pages have old layout.
```

```
BULK INVALIDATION STRATEGIES

  Strategy                    | How it works                  | Risk
  ----------------------------|-------------------------------|-------------------
  FLUSHALL (nuclear option)   | Delete ALL keys in Redis.     | Massive cache miss
                              |                               | storm -> DB overload
                              |                               | -> potential outage.
                              |                               | NEVER in production.
  Key prefix scan + delete    | SCAN for keys matching        | Slow if many keys.
                              | pattern (e.g., product:*)     | SCAN is O(N).
                              | and delete each.              | Can take minutes.
  Tag-based invalidation      | Associate keys with tags.     | Requires tag tracking
                              | Invalidate all keys with a    | (set of keys per
                              | given tag.                    | tag). More memory.
  Version-based (namespace)   | Use version prefix in key:    | No deletion needed.
                              | "v3:product:123". Increment   | Old keys evicted by
                              | version -> all old keys are   | LRU/LFU/TTL.
                              | effectively invalidated.      | Simple, fast.
  CDC-driven bulk             | Debezium captures all rows    | Takes time (one
                              | changed by the bulk update.   | event per row).
                              | Invalidator processes events. | Backpressure needed.
```

```
VERSION-BASED BULK INVALIDATION (recommended)

  // Global version stored in Redis:
  redis.set("cache:version:products", "v3");

  // Cache key includes version:
  String version = redis.get("cache:version:products");  // "v3"
  String cacheKey = version + ":product:" + id;          // "v3:product:123"

  // On bulk change, increment version:
  redis.set("cache:version:products", "v4");

  // Now all "v3:*" keys are unreachable. They will be evicted
  // by Redis LRU/LFU/TTL over time. No explicit deletion needed.

  Pros:
  - Instant: one SET command invalidates all products.
  - No SCAN, no delete loop, no key enumeration.
  - Old keys are naturally evicted (no memory leak if TTL is set).

  Cons:
  - Extra Redis GET per read (fetch version).
  - Brief memory overhead (old and new keys coexist until old evicts).
```

---

## 14.13 Step 12: Observability for Invalidation Health

```
INVALIDATION-SPECIFIC METRICS

  Metric                               | Alert on               | Meaning
  -------------------------------------|------------------------|-------------------
  Invalidation event lag (Kafka)       | > 5 seconds            | CDC consumer is
                                       |                        | behind. Stale data
                                       |                        | being served.
  Invalidation success rate            | < 99%                  | Cache deletes are
                                       |                        | failing. Redis issue.
  Stale read rate (version mismatch)   | > 5%                   | L1 caches serving
                                       |                        | outdated versions.
  Double-delete second-delete success  | < 100%                 | Delayed deletes
                                       |                        | are failing.
  CDN purge latency                    | > 30 seconds           | CDN invalidation
                                       |                        | is slow.
  CDN purge failure rate               | > 0 sustained          | CDN invalidation
                                       |                        | is broken.
  Pub/Sub subscriber count             | < expected instance    | An instance is not
                                       | count                  | receiving L1
                                       |                        | invalidation events.
  Negative cache hit rate              | Track trend            | How often "not found"
                                       |                        | is served from cache.
  Cache miss rate after invalidation   | Spike after            | Expected spike after
                                       | bulk invalidation      | bulk flush. Monitor
                                       |                        | DB load during spike.
```

```
DEBUGGING STALE DATA

  User reports: "I see the old price even after I updated it."

  Investigation steps:
  1. Check DB: SELECT price FROM products WHERE id=123;
     -> Is the DB updated? If not, write failed. Fix upstream.

  2. Check Redis: GET product:123
     -> Is Redis stale? If yes, invalidation failed or delayed.

  3. Check L1: call /debug/cache/product:123 on each instance.
     -> Is any instance serving a stale L1 value?

  4. Check CDN: curl -I https://cdn.example.com/api/products/123
     -> Is CDN serving a cached stale response? Check Age header.

  5. Check invalidation pipeline:
     - Kafka consumer lag for CDC topic?
     - Pub/Sub subscriber count?
     - Any errors in invalidation service logs?

  6. Check TTLs: TTL product:123
     -> Does the key have a TTL? If not, it will never expire.
     -> This is a common bug: missing TTL = permanently stale.
```

---

## 14.14 Step 13: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Simplicity vs. freshness         | TTL-only is simple but serves stale data.
                                   | CDC+Kafka is fresh but complex. Choose based
                                   | on your team's ability to operate the pipeline.
  Delete vs. update                | Delete is safer (idempotent). Update gives
                                   | read-after-write consistency but risks stale
                                   | overwrites with concurrent writes.
  Double-delete delay              | Too short: misses stale writes. Too long:
                                   | stale window is longer. 500ms-2s is typical.
  CDC consumer lag                 | During traffic spikes, CDC consumer may fall
                                   | behind. TTL is the safety net. Monitor lag.
  Missing TTL (the silent killer)  | Every cache key MUST have a TTL. Without it,
                                   | a missed invalidation event = permanently
                                   | stale data. TTL is your last line of defense.
  L1 invalidation gaps             | Redis Pub/Sub is fire-and-forget. If an
                                   | instance is down during the event, it misses
                                   | the invalidation. Short L1 TTL (1-5 min)
                                   | is the safety net.
  CDN purge rate limits            | CloudFront allows 1000 invalidation paths
                                   | per month for free. Beyond that, costs increase.
                                   | Use surrogate keys (Fastly) or versioned URLs
                                   | instead of path-based purges.
  Bulk flush = DB storm            | Flushing 100K keys at once causes 100K cache
                                   | misses -> 100K DB queries simultaneously.
                                   | Use version-based invalidation (no flush) or
                                   | rate-limit the flush with gradual expiry.
  Cache poisoning on failure       | If DB returns an error and you cache the
                                   | error response, the cache is poisoned.
                                   | NEVER cache error responses. Validate before
                                   | caching.
  Invalidation ordering            | If two updates to the same key produce events
                                   | that are processed out of order, the cache
                                   | ends up with the older value. Use partition key
                                   | = entity ID in Kafka for ordering.
  Over-invalidation                | Invalidating too aggressively (every field
                                   | change triggers a purge) reduces cache hit rate
                                   | and increases DB load. Invalidate only when
                                   | cached fields change.
  Coupling through cache           | If Service A directly deletes keys in Service
                                   | B's Redis, changes to B's key structure break A.
                                   | Use domain events. Each service manages its
                                   | own cache.
```

---

## 14.15 Problem 14 Summary

- **Cache invalidation is the hardest problem in distributed caching** because data is cached at multiple layers (L1, Redis, CDN, browser), modified by multiple writers, and subject to race conditions, ordering issues, and delivery failures.
- **Six strategies:** TTL-based (simplest, eventual), explicit delete (cache-aside, risk of race condition), write-through (fresh, slower writes), Pub/Sub (near-real-time, fire-and-forget), version-based (no stale reads, extra lookup), CDC+Kafka (gold standard, decoupled, durable).
- **Always set a TTL** on every cache key, regardless of which strategy you use. TTL is the last line of defense when invalidation fails.
- **The classic race condition:** reader fetches old data from DB (or read replica), writes it to cache AFTER the writer's delete. Solutions: double-delete, lease-based invalidation, version check, or accept bounded staleness.
- **Delete is safer than update:** idempotent, simpler, avoids concurrent-write overwrites. Use update (write-through) only when read-after-write consistency is critical and writes are serialized.
- **Double-delete:** delete cache on write, wait 500ms-2s, delete again. Catches stale writes from concurrent readers. Simple and effective.
- **CDC + Kafka is the gold standard** for production: Debezium captures all DB changes, publishes to Kafka, consumers invalidate cache/CDN/search. Writers need no cache awareness. Durable and replayable.
- **Cross-layer invalidation order:** Redis first, L1 Pub/Sub second, CDN purge third. Inner layers first, outer layers second.
- **Microservices rule:** the service that owns the data announces changes via domain events. The service that caches the data is responsible for its own invalidation. Never reach into another service's cache.
- **Lease-based invalidation** (Facebook Memcache paper): eliminates the race condition by revoking outstanding leases on invalidation, preventing stale writes.
- **Cache penetration:** requests for non-existent keys bypass cache. Defend with negative caching (cache "NULL" with short TTL) AND Bloom filters (block 99%+ of invalid keys).
- **Bulk invalidation:** never FLUSHALL. Use version-based namespace invalidation (increment version prefix, old keys become unreachable and are evicted by LRU/TTL).
- **Monitor:** invalidation event lag, success rate, stale read rate, CDN purge latency, Pub/Sub subscriber count, missing TTLs. Debug stale data by checking each layer: DB → Redis → L1 → CDN.

---

# Problem 15: How Do You Design Backward-Compatible APIs?

## 15.1 Problem Statement

> Your platform exposes REST and gRPC APIs consumed by 50+ internal microservices and 200+ external third-party integrations. You need to ship a major feature that changes the structure of several API resources. Some clients were onboarded three years ago and still run on the original contract. How do you evolve your APIs without breaking any existing consumer? Walk through versioning strategies, contract evolution patterns, deprecation workflows, and the testing and observability needed to ship safely.

This question tests:
- **API design maturity** — understanding that APIs are contracts, not just endpoints.
- **Evolution strategy** — knowing multiple versioning approaches and their trade-offs.
- **Organizational empathy** — external consumers cannot be forced to upgrade on your schedule.
- **Production discipline** — deprecation is not just documentation; it requires tooling, metrics, and communication.

---

## 15.2 Step 1: Why Backward Compatibility Matters

```
THE COST OF A BREAKING CHANGE

  Scenario                               | Impact
  ---------------------------------------|-------------------------------------------
  Remove a response field                | Client's deserialization fails (NPE, crash).
  Rename a field                         | Client reads null instead of value. Silent
                                         | data corruption.
  Change a field type (string -> int)    | Client throws ClassCastException or
                                         | JSON parse error.
  Change URL path structure              | Client gets 404 on every request.
  Change error response format           | Client's error handling breaks. Retries
                                         | endlessly or swallows real errors.
  Require a new mandatory field in       | All existing client requests fail with
  request body                           | 400 Bad Request.
  Tighten validation (accept less)       | Previously valid requests are rejected.
  Change authentication scheme           | All existing clients get 401 Unauthorized.
```

```
WHY YOU CANNOT JUST "TELL THEM TO UPGRADE"

  - External partners have their own release cycles (quarterly, annually).
  - Mobile apps in the field cannot be force-updated.
  - IoT devices may never be updated.
  - Internal services may depend on your API transitively (A -> B -> C).
    You change C, and A breaks even though A never directly integrated.
  - Contractual SLAs may guarantee API stability for years.
  - Trust: breaking an API once means partners always fear your changes.

  Rule: YOUR API IS YOUR PROMISE. Breaking it breaks trust.
```

---

## 15.3 Step 2: Breaking vs. Non-Breaking Changes

```
NON-BREAKING CHANGES (safe to ship anytime)

  Change                                  | Why safe
  ----------------------------------------|----------------------------------
  Add a new OPTIONAL field to request     | Old clients omit it; server uses
                                          | default value.
  Add a new field to response             | Old clients ignore unknown fields
                                          | (if following Postel's Law).
  Add a new endpoint / resource           | Does not affect existing endpoints.
  Add a new enum value to response        | Old clients treat it as unknown
                                          | (if designed tolerantly).
  Add a new optional query parameter      | Old clients do not send it.
  Widen validation (accept more)          | Previously valid requests still work.
  Add a new HTTP method to existing path  | Does not affect existing methods.
  Increase rate limits                    | Only makes things better.
  Add new optional HTTP headers           | Old clients do not send them.
```

```
BREAKING CHANGES (require versioning or migration)

  Change                                  | Why breaking
  ----------------------------------------|----------------------------------
  Remove a field from response            | Client expects it; gets null/error.
  Rename a field                          | Equivalent to remove + add.
  Change a field type                     | Deserialization fails.
  Remove an endpoint                      | Client gets 404.
  Change URL structure                    | Client gets 404.
  Make an optional request field required | Old requests fail validation.
  Tighten validation (accept less)        | Previously valid requests rejected.
  Change error response format/codes      | Client error handling breaks.
  Change authentication/authorization     | Client gets 401/403.
  Change pagination structure             | Client's pagination logic breaks.
  Change default values                   | Client behavior silently changes.
  Remove an enum value                    | Client may send/expect that value.
```

```
POSTEL'S LAW (THE ROBUSTNESS PRINCIPLE)

  "Be conservative in what you send, be liberal in what you accept."
                                                    — Jon Postel, RFC 761

  For API producers:
  - Send only well-defined, documented fields.
  - Do not remove fields without deprecation.
  - Do not change field semantics silently.

  For API consumers:
  - Ignore unknown fields (do not fail on extra fields).
  - Do not depend on field ordering.
  - Handle missing optional fields gracefully.
  - Treat unknown enum values as a default / unknown.

  If ALL consumers follow Postel's Law, then adding fields to
  responses is always safe. This is the foundation of
  backward-compatible API evolution.
```

---

## 15.4 Step 3: API Versioning Strategies

```
STRATEGY 1: URI PATH VERSIONING

  /api/v1/products/123
  /api/v2/products/123

  Pros:
  - Explicit. Easy to understand. Easy to route.
  - Different versions can coexist on different server deployments.
  - Clear in logs, docs, and debugging.

  Cons:
  - URL changes break bookmarks and caches.
  - Proliferation of versions (v1, v2, v3...) is hard to maintain.
  - Tempts teams to bump versions for every change (version inflation).

  When to use:
  - Public APIs (Stripe, GitHub, Google).
  - When major structural changes are rare (1-2 per year).

  Best practice: keep only 2-3 active versions max. Sunset old ones.
```

```
STRATEGY 2: HEADER VERSIONING (Accept / Custom Header)

  Accept: application/vnd.myapp.v2+json
  -- or --
  X-API-Version: 2

  Pros:
  - URL stays the same. Clean URI.
  - Version is metadata, not part of resource identity.

  Cons:
  - Less visible (not in URL, harder to test in browser).
  - Requires client to set headers explicitly.
  - CDN caching is harder (must Vary on header).

  When to use:
  - Internal APIs where clients are controlled.
  - When you want stable URLs (bookmarks, SEO).
```

```
STRATEGY 3: QUERY PARAMETER VERSIONING

  /api/products/123?version=2

  Pros:
  - Simple. Easy to test (just change query param).
  - URL stays mostly the same.

  Cons:
  - Mixes versioning with query logic.
  - Easy to forget/omit the parameter.
  - CDN caching must include the param in cache key.

  When to use:
  - Quick prototyping or internal tools.
  - Not recommended for public APIs.
```

```
STRATEGY 4: NO EXPLICIT VERSIONING (EVOLUTION-FIRST)

  Use additive-only changes. Never break. Never version.
  Add fields, add endpoints, deprecate old fields, remove after
  all consumers migrate.

  Pros:
  - No version management overhead.
  - Forces discipline: every change must be non-breaking.
  - Simplest client experience (no version to manage).

  Cons:
  - Cannot make structural changes easily.
  - Accumulates deprecated fields over time (API bloat).
  - Requires excellent contract testing to ensure nothing breaks.

  When to use:
  - Internal APIs with strong contract testing.
  - GraphQL (inherently versionless: clients request only the
    fields they need).

  This is the PREFERRED approach when possible.
```

```
VERSIONING COMPARISON

  Strategy            | Visibility | Cacheability | Maintenance | Best for
  --------------------|------------|--------------|-------------|----------
  URI path (/v1/)     | High       | Easy         | High        | Public APIs
  Header (Accept)     | Low        | Hard (Vary)  | Medium      | Internal APIs
  Query param (?v=2)  | Medium     | Medium       | Medium      | Prototyping
  Evolution (no ver.) | N/A        | Easy         | Low         | Internal, GraphQL

  Recommendation:
  - Public APIs: URI path versioning (v1, v2).
  - Internal APIs: evolution-first (no explicit versions).
  - Fall back to URI path when a truly breaking change is unavoidable.
```

---

## 15.5 Step 4: Additive-Only Design (The Golden Rule)

```
THE GOLDEN RULE

  Only ADD. Never REMOVE. Never RENAME. Never change types.

  If you follow this single rule, your API is backward-compatible
  by default. No versioning needed.
```

```
ADDITIVE-ONLY PATTERNS

  1. Add new optional fields to requests:
     Before: { "name": "Widget" }
     After:  { "name": "Widget", "description": "A fine widget" }
     Old clients omit "description." Server uses default (null or "").

  2. Add new fields to responses:
     Before: { "id": 123, "name": "Widget" }
     After:  { "id": 123, "name": "Widget", "category": "tools" }
     Old clients ignore "category" (Postel's Law).

  3. Add new endpoints:
     Existing: GET /api/products
     New:      GET /api/products/{id}/reviews
     Old clients never call the new endpoint. No impact.

  4. Add new enum values:
     Before: status: "active" | "inactive"
     After:  status: "active" | "inactive" | "archived"
     Old clients must handle unknown enum values gracefully
     (treat as default or log a warning).

  5. Add new optional query parameters:
     Before: GET /api/products?category=tools
     After:  GET /api/products?category=tools&sort=price
     Old clients do not send "sort." Server uses default sort.
```

```
WHAT TO DO WHEN YOU MUST BREAK

  Sometimes a breaking change is truly necessary (e.g., security fix,
  fundamental model change). In that case:

  1. Create a new version (v2).
  2. Keep v1 running alongside v2 (parallel operation).
  3. Announce deprecation of v1 with a sunset date.
  4. Give consumers 6-12 months to migrate (external APIs).
  5. Monitor v1 usage. Reach out to remaining consumers.
  6. Shut down v1 only when traffic is zero (or contractually allowed).
```

---

## 15.6 Step 5: Request & Response Evolution Patterns

```
PATTERN 1: FIELD ALIASING (rename without breaking)

  Problem: you want to rename "userName" to "username" (camelCase issue).

  Solution: accept BOTH in requests. Return BOTH in responses.

  Request:
    { "userName": "prakash" }    // Old clients
    { "username": "prakash" }    // New clients
    // Server accepts both. Prefers "username" if both present.

  Response:
    { "userName": "prakash", "username": "prakash" }
    // Old clients read "userName." New clients read "username."

  After all old clients migrate: stop sending "userName" in response.
  (Monitor usage first.)
```

```
PATTERN 2: FIELD EXPANSION (change type without breaking)

  Problem: "address" was a string, now needs to be an object.

  Before: { "address": "123 Main St, NYC" }
  After:  { "address": "123 Main St, NYC",
            "addressDetail": { "street": "123 Main St",
                               "city": "NYC", "zip": "10001" } }

  Old clients read "address" (still a string, unchanged).
  New clients read "addressDetail" (rich object).
  Server populates BOTH from the same source.

  Never change the type of an existing field. Add a new field instead.
```

```
PATTERN 3: NULLABLE NEW FIELDS

  When adding a new field to a response, make it nullable initially.
  Clients that are not aware of it get null/missing. No crash.

  Response v1: { "id": 1, "name": "Widget", "price": 9.99 }
  Response v1+: { "id": 1, "name": "Widget", "price": 9.99,
                  "discountedPrice": null }

  Later, when discount logic is live:
  Response v1+: { "id": 1, "name": "Widget", "price": 9.99,
                  "discountedPrice": 7.99 }

  Clients that understand "discountedPrice" use it. Others ignore it.
```

```
PATTERN 4: ENVELOPE STABILITY

  Keep the top-level response structure (envelope) stable forever.

  {
    "data": { ... },       // Payload. Can evolve.
    "meta": { ... },       // Pagination, version, etc.
    "errors": [ ... ]      // Error details.
  }

  Rules:
  - Never change the envelope keys ("data", "meta", "errors").
  - Never change the error structure.
  - Inner content of "data" can evolve additively.
  - Pagination format in "meta" must never change.
```

---

## 15.7 Step 6: Deprecation Lifecycle & Sunset Headers

```
DEPRECATION LIFECYCLE

  Phase 1: ANNOUNCE
    - Mark field/endpoint as deprecated in OpenAPI spec.
    - Add @Deprecated annotation in code.
    - Add "Deprecation" HTTP header to responses.
    - Notify consumers via email, changelog, developer portal.
    - Timeline: announce 6-12 months before removal (external).
                announce 1-3 months before removal (internal).

  Phase 2: WARN
    - Log every request that uses deprecated field/endpoint.
    - Return "Sunset" header with the removal date.
    - Include deprecation notice in response body (optional).
    - Actively contact top consumers who still use deprecated features.

  Phase 3: MONITOR
    - Track usage metrics. How many requests/day hit deprecated features?
    - If usage > 0, do NOT remove. Reach out to remaining consumers.
    - Set an internal threshold: remove when usage < N requests/day
      for M consecutive days.

  Phase 4: REMOVE
    - Remove the deprecated feature.
    - Return 410 Gone (not 404) for removed endpoints.
      410 tells the client: "This existed but was intentionally removed."
    - Keep the 410 response for at least 6 months so clients get
      a clear signal instead of a confusing 404.
```

```
SUNSET AND DEPRECATION HEADERS (RFC 8594, draft-ietf)

  HTTP/1.1 200 OK
  Deprecation: Sun, 01 Jun 2025 00:00:00 GMT
  Sunset: Mon, 01 Dec 2025 00:00:00 GMT
  Link: <https://api.example.com/docs/migration-guide>; rel="successor-version"

  Deprecation: date when the feature was marked deprecated.
  Sunset: date when the feature will be removed.
  Link (successor-version): URL to migration guide or new version.

  Clients can parse these headers and log warnings automatically.
  Client libraries (e.g., Stripe SDK) surface deprecation warnings
  in developer consoles.
```

```
OPENAPI DEPRECATION

  paths:
    /api/v1/products:
      get:
        summary: List products
        deprecated: true
        description: |
          DEPRECATED: Use /api/v2/products instead.
          Will be removed on 2025-12-01.
        x-sunset: "2025-12-01"

  components:
    schemas:
      Product:
        properties:
          userName:
            type: string
            deprecated: true
            description: "Use 'username' instead."
          username:
            type: string
```

---

## 15.8 Step 7: Schema Evolution (Database ↔ API Alignment)

```
THE PROBLEM

  API contract evolution and database schema evolution must be
  coordinated. A database column rename breaks the API if the API
  directly exposes DB columns.

  Rule: NEVER expose your database schema directly as your API contract.
  Always have a mapping layer (DTO / view model) between DB and API.
```

```
EXPAND-CONTRACT MIGRATION PATTERN

  Goal: rename DB column "user_name" to "username" without breaking API.

  Phase 1: EXPAND
    - Add new column "username" to DB.
    - Backfill: UPDATE users SET username = user_name;
    - App writes to BOTH columns on every write.
    - API continues to read from "user_name" (no API change yet).

  Phase 2: MIGRATE
    - Switch API to read from "username."
    - App still writes to BOTH columns (safety net).
    - Verify no consumer depends on old behavior.

  Phase 3: CONTRACT
    - Stop writing to "user_name."
    - Drop the old column (after confirming no reads).
    - This phase can be weeks or months after Phase 2.

  The API contract was never broken. Consumers saw no change.
  The DB schema was safely evolved underneath.
```

```
DTO MAPPING LAYER

  // Database entity (internal, can change).
  @Entity
  public class ProductEntity {
      private Long id;
      private String productName;  // DB column name.
      private BigDecimal unitPrice;
  }

  // API DTO (external contract, must be stable).
  public class ProductResponse {
      private Long id;
      private String name;         // API field name (different from DB).
      private BigDecimal price;
  }

  // Mapper (decouples DB from API).
  public ProductResponse toResponse(ProductEntity entity) {
      return new ProductResponse(
          entity.getId(),
          entity.getProductName(),  // Maps DB "productName" to API "name."
          entity.getUnitPrice()     // Maps DB "unitPrice" to API "price."
      );
  }

  If DB column "productName" is renamed to "name," only the mapper
  changes. The API contract is unaffected.
```

---

## 15.9 Step 8: Contract Testing (Consumer-Driven Contracts)

```
THE PROBLEM

  Unit tests verify YOUR code works.
  Integration tests verify YOUR service works.
  Neither verifies that YOUR API change does not break YOUR CONSUMERS.

  You need a test that says: "if I change this response field,
  which consumer will break?"

  This is CONTRACT TESTING.
```

```
CONSUMER-DRIVEN CONTRACTS (Pact)

  How it works:
  1. CONSUMER defines a contract: "I expect GET /products/123 to
     return { id: 123, name: string, price: number }."
  2. Contract is stored in a Pact Broker (shared registry).
  3. PRODUCER runs the contract as a test: "does my API satisfy
     the consumer's expectations?"
  4. If the producer's response matches the contract -> PASS.
  5. If the producer removes "name" field -> FAIL.
     The test tells you exactly which consumer will break.

  Flow:
    Consumer writes contract -> Pact Broker <- Producer verifies contract

  Benefits:
  - You know BEFORE deploying whether your change is safe.
  - Each consumer defines only the fields IT cares about.
    If consumer A uses { id, name } and consumer B uses { id, price },
    you can remove "description" safely (no consumer depends on it).
  - Runs in CI/CD pipeline. No manual testing needed.
```

```
PACT EXAMPLE (Spring Boot)

  // Consumer side (Order Service):
  @Pact(consumer = "OrderService", provider = "ProductService")
  public RequestResponsePact productPact(PactDslWithProvider builder) {
      return builder
          .given("product 123 exists")
          .uponReceiving("a request for product 123")
          .path("/api/products/123")
          .method("GET")
          .willRespondWith()
          .status(200)
          .body(newJsonBody(o -> {
              o.numberType("id", 123);
              o.stringType("name", "Widget");
              o.decimalType("price", 9.99);
          }).build())
          .toPact();
  }

  // Producer side (Product Service):
  @Provider("ProductService")
  @PactBroker(url = "https://pact-broker.internal")
  public class ProductPactVerificationTest {
      @TestTemplate
      @ExtendWith(PactVerificationInvocationContextProvider.class)
      void verifyPact(PactVerificationContext context) {
          context.verifyInteraction();
      }
  }

  If Product Service removes "name" from its response, the Pact
  verification test FAILS because Order Service's contract requires it.
```

```
SCHEMA VALIDATION (OpenAPI-based)

  Alternative to Pact: validate every response against OpenAPI schema.

  // In CI pipeline:
  openapi-diff old-spec.yaml new-spec.yaml --fail-on-incompatible

  Tools:
  - openapi-diff: detects breaking changes between two OpenAPI specs.
  - Spectral: lints OpenAPI specs for best practices.
  - Swagger Validator: validates requests/responses against spec at runtime.

  Integrate into CI:
  - On every PR that modifies the API spec, run openapi-diff.
  - If a breaking change is detected -> block the PR.
  - Require explicit "BREAKING CHANGE" label to override.
```

---

## 15.10 Step 9: API Gateways & Compatibility Layers

```
API GATEWAY AS VERSION ROUTER

  Client -> API Gateway -> routes to correct backend version.

  Gateway config:
    /api/v1/products -> product-service-v1 (legacy deployment)
    /api/v2/products -> product-service-v2 (new deployment)
    /api/v3/products -> product-service-v3 (latest)

  Benefits:
  - Multiple API versions served simultaneously.
  - Old versions can be separate deployments or the same service
    with internal branching.
  - Gateway handles routing, rate limiting, auth per version.
```

```
ADAPTER / TRANSLATION LAYER

  Instead of maintaining separate codebases for v1 and v2,
  use a translation layer:

  Client (v1 request) -> Gateway -> Translator -> Service (v2 internally)
                                        |
                         Translates v1 request to v2 format.
                         Translates v2 response to v1 format.

  Implementation:
    @RestController
    @RequestMapping("/api/v1/products")
    public class ProductV1Controller {

        @Autowired
        private ProductV2Service productService;  // v2 internally.

        @GetMapping("/{id}")
        public ProductV1Response getProduct(@PathVariable Long id) {
            ProductV2Response v2 = productService.getProduct(id);
            return translateToV1(v2);  // Map v2 fields to v1 contract.
        }

        private ProductV1Response translateToV1(ProductV2Response v2) {
            return new ProductV1Response(
                v2.getId(),
                v2.getFullName(),      // v2 renamed "name" to "fullName"
                v2.getBasePrice()      // v2 renamed "price" to "basePrice"
            );
        }
    }

  Benefits:
  - Single codebase. Business logic lives in v2 only.
  - v1 is a thin translation layer.
  - Easy to deprecate: remove the v1 controller when ready.
```

```
FEATURE FLAGS FOR API CHANGES

  Instead of versioning, use feature flags to gradually roll out
  API changes:

  if (featureFlag.isEnabled("new-product-response", clientId)) {
      return newProductResponse(product);    // New structure.
  } else {
      return legacyProductResponse(product); // Old structure.
  }

  Enable the flag per-client, per-percentage, or per-region.
  Monitor error rates after each rollout increment.

  Benefits:
  - Gradual rollout (1% -> 10% -> 50% -> 100%).
  - Instant rollback (disable flag).
  - No URL versioning needed.

  Risks:
  - Code branching complexity. Must clean up flags after full rollout.
  - Testing both paths is required.
```

---

## 15.11 Step 10: gRPC & Protobuf Backward Compatibility

```
PROTOBUF FIELD RULES

  Protocol Buffers have built-in backward compatibility rules:

  SAFE changes:
  - Add a new field (with a new field number).
  - Deprecate a field (mark as deprecated, but do not remove).
  - Change field name (wire format uses field numbers, not names).

  UNSAFE changes:
  - Remove a field (old clients expect it).
  - Change a field number (breaks wire format).
  - Change a field type (deserialization fails).
  - Reuse a deleted field number (data corruption).

  // Example evolution:
  // v1:
  message Product {
    int64 id = 1;
    string name = 2;
    double price = 3;
  }

  // v2 (backward compatible):
  message Product {
    int64 id = 1;
    string name = 2;
    double price = 3;
    string category = 4;          // New field. Old clients ignore it.
    string description = 5;       // New field.
    reserved 6;                   // Reserve field number for future.
  }
```

```
PROTOBUF RESERVED FIELDS

  When you remove a field, RESERVE its number to prevent reuse:

  message Product {
    int64 id = 1;
    string name = 2;
    // Field 3 was "price" (removed).
    reserved 3;
    reserved "price";
    double basePrice = 4;     // New field with new number.
  }

  If someone accidentally reuses field number 3, the protobuf
  compiler will throw an error. This prevents data corruption.
```

```
gRPC SERVICE EVOLUTION

  // v1:
  service ProductService {
    rpc GetProduct(GetProductRequest) returns (ProductResponse);
  }

  // v2 (backward compatible):
  service ProductService {
    rpc GetProduct(GetProductRequest) returns (ProductResponse);
    rpc SearchProducts(SearchRequest) returns (SearchResponse);  // New RPC.
  }

  Adding new RPCs is always safe.
  Removing RPCs is a breaking change (clients get UNIMPLEMENTED error).

  For breaking changes in gRPC:
  - Create a new service: ProductServiceV2.
  - Or use a new package: api.v2.ProductService.
  - Run both versions in the same server binary.
```

---

## 15.12 Step 11: Event / Message Schema Evolution (Kafka, AsyncAPI)

```
THE PROBLEM

  APIs are not only REST/gRPC. Events (Kafka messages) are also
  contracts between producers and consumers.

  If Producer changes the event schema, all consumers may break.
  Kafka events are often consumed by many services — the blast
  radius of a schema change is large.
```

```
SCHEMA REGISTRY (Confluent Schema Registry)

  All Kafka event schemas are registered in a central Schema Registry.
  The registry enforces compatibility rules BEFORE a producer can
  publish a new schema version.

  Compatibility modes:
  - BACKWARD: new schema can read data written by old schema.
    (Add optional fields, remove fields with defaults.)
  - FORWARD: old schema can read data written by new schema.
    (Remove optional fields, add fields with defaults.)
  - FULL: both backward AND forward compatible.
    (Only add/remove optional fields with defaults.)
  - NONE: no compatibility check. Dangerous.

  Recommended: FULL compatibility for all production topics.

  Flow:
    Producer -> Schema Registry (validate schema) -> Kafka
    Consumer -> Schema Registry (fetch schema) -> deserialize
```

```
AVRO SCHEMA EVOLUTION

  Avro is the most common Kafka serialization format.

  // v1:
  {
    "type": "record",
    "name": "ProductEvent",
    "fields": [
      { "name": "id", "type": "long" },
      { "name": "name", "type": "string" },
      { "name": "price", "type": "double" }
    ]
  }

  // v2 (backward + forward compatible):
  {
    "type": "record",
    "name": "ProductEvent",
    "fields": [
      { "name": "id", "type": "long" },
      { "name": "name", "type": "string" },
      { "name": "price", "type": "double" },
      { "name": "category", "type": ["null", "string"], "default": null }
    ]
  }

  Rules:
  - New fields MUST have a default value (null, "", 0).
  - Never remove a field without a default in the old schema.
  - Never change a field type.
  - Never rename a field (add new + deprecate old).
```

```
ASYNCAPI SPECIFICATION

  AsyncAPI is the OpenAPI equivalent for event-driven APIs.
  Define your Kafka topics, message schemas, and compatibility
  rules in a single spec.

  asyncapi: '2.6.0'
  info:
    title: Product Events
    version: '2.0.0'
  channels:
    product.updated:
      subscribe:
        message:
          schemaFormat: 'application/vnd.apache.avro+json;version=1.9.0'
          payload:
            $ref: '#/components/schemas/ProductEvent'

  Use asyncapi-diff to detect breaking changes between versions.
```

---

## 15.13 Step 12: Observability & Safe Rollout

```
API VERSION USAGE METRICS

  Metric                                | Alert on               | Action
  --------------------------------------|------------------------|-------------------
  Requests per version                  | v1 usage > 0 after     | Contact remaining
  (v1 vs. v2 vs. v3)                   | sunset date            | consumers.
  Deprecated field usage count          | > 0 sustained          | Track who still
  (per field, per consumer)             |                        | uses deprecated
                                        |                        | fields.
  Error rate by API version             | Spike after deploy     | Possible breaking
                                        |                        | change. Rollback.
  Consumer SDK version distribution     | Old SDK versions       | Push SDK upgrade
                                        | > 30% of traffic       | to consumers.
  Contract test pass rate               | < 100%                 | Breaking change
                                        |                        | detected. Block
                                        |                        | deployment.
  Sunset header response count          | Track trend            | Consumers are
                                        |                        | being warned.
```

```
SAFE ROLLOUT CHECKLIST

  Before deploying an API change:
  1. [ ] openapi-diff confirms no breaking changes.
  2. [ ] All Pact contract tests pass.
  3. [ ] Schema Registry accepts new event schema (compatibility check).
  4. [ ] Deprecated features have Sunset headers and documentation.
  5. [ ] Feature flag is ready for gradual rollout (if needed).
  6. [ ] Monitoring dashboards track per-version error rates.
  7. [ ] Rollback plan: can revert deployment in < 5 minutes.
  8. [ ] Migration guide published for consumers (if breaking).
  9. [ ] Top consumers notified (if breaking).
  10. [ ] Canary deployment: new version serves 1% traffic first.
```

```
CANARY DEPLOYMENT FOR API CHANGES

  1. Deploy new API version to 1 canary instance.
  2. Route 1% of traffic to canary (via gateway weight).
  3. Monitor error rates, latency, consumer complaints.
  4. If clean for 1 hour -> increase to 10% -> 50% -> 100%.
  5. If errors spike -> route 100% back to old version (instant).

  Canary catches issues that contract tests miss:
  - Edge cases in real production data.
  - Consumers that do not follow Postel's Law.
  - Unexpected interactions with other services.
```

---

## 15.14 Step 13: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Version proliferation            | Too many active versions (v1, v2, v3, v4)
                                   | multiplies maintenance. Keep max 2-3 active.
                                   | Aggressively sunset old versions.
  Evolution vs. versioning         | Evolution (additive-only) avoids versioning
                                   | overhead but accumulates deprecated fields.
                                   | Versioning is cleaner but more expensive.
  Exposing DB schema as API        | Any DB change breaks the API. Always use a
                                   | DTO mapping layer to decouple DB from API.
  Renaming fields                  | A rename = remove old + add new = breaking.
                                   | Use aliasing (return both old and new) until
                                   | all consumers migrate.
  Changing types silently          | Never change a field's type. Add a new field
                                   | with the new type instead.
  Missing contract tests           | Without Pact or OpenAPI diff, you only
                                   | discover breaking changes in production.
                                   | Integrate into CI/CD as a mandatory gate.
  Ignoring event schemas           | Kafka events are API contracts too. Use
                                   | Schema Registry with FULL compatibility mode.
  Skipping deprecation lifecycle   | Removing a feature without warning breaks
                                   | trust. Always: announce -> warn -> monitor ->
                                   | remove. Never skip phases.
  No Sunset header                 | Consumers have no way to know when a feature
                                   | will be removed. Always send Sunset header.
  Feature flag debt                | Using flags for API branching is powerful but
                                   | creates code complexity. Clean up flags
                                   | promptly after full rollout.
  Tight coupling via shared models | If Consumer imports Producer's response class
                                   | directly, any change breaks compilation.
                                   | Consumers should own their own DTOs.
  Not monitoring deprecated usage  | You cannot sunset what you cannot measure.
                                   | Log every deprecated field/endpoint usage
                                   | with consumer identity.
```

---

## 15.15 Problem 15 Summary

- **Your API is your promise.** Breaking it breaks trust, causes outages for consumers, and creates support burden. Backward compatibility is a non-negotiable discipline for any API with external or multi-team consumers.
- **Breaking vs. non-breaking:** adding optional fields, new endpoints, and new enum values is safe. Removing fields, renaming, changing types, tightening validation, and changing URL structure is breaking.
- **Postel's Law:** "Be conservative in what you send, be liberal in what you accept." If all consumers ignore unknown fields, additive changes are always safe.
- **Versioning strategies:** URI path (public APIs, explicit), header (internal, clean URLs), query param (prototyping), evolution-first/no version (preferred when possible, requires discipline).
- **The golden rule — additive only:** only add, never remove, never rename, never change types. This single rule makes versioning unnecessary for most changes.
- **Evolution patterns:** field aliasing (return both old and new names), field expansion (add new structured field alongside old flat field), nullable new fields, stable envelope structure.
- **Deprecation lifecycle:** announce (6-12 months ahead) → warn (Sunset header, logging) → monitor (track usage to zero) → remove (return 410 Gone, not 404). Never skip phases.
- **Schema evolution:** never expose DB schema as API. Use DTO mapping layer. Use expand-contract migration for DB column renames.
- **Contract testing:** Pact (consumer-driven contracts) catches breaking changes before deployment. OpenAPI-diff in CI blocks PRs with incompatible changes. Both are mandatory for production APIs.
- **API gateway:** routes different versions to different backends. Translation layer lets you maintain one codebase with thin v1 adapters. Feature flags enable gradual rollout without URL versioning.
- **gRPC/Protobuf:** safe to add fields (new numbers), rename fields (wire uses numbers), add RPCs. Unsafe to remove fields, change numbers/types, reuse deleted numbers. Always `reserved` deleted field numbers.
- **Event schema evolution:** Kafka events are contracts. Use Schema Registry with FULL compatibility mode. Avro: new fields must have defaults. Use asyncapi-diff to detect breaking changes.
- **Observability:** track per-version request counts, deprecated field usage per consumer, error rates by version, contract test pass rate. Canary deploy API changes (1% → 10% → 100%) with instant rollback.

---

# Problem 16: REST vs gRPC — Where and Why?

## 16.1 Problem Statement

> Your company runs 80+ microservices. Some are public-facing APIs consumed by web and mobile clients. Others are internal services that communicate at high throughput with strict latency budgets. The platform team asks you to define when a service should expose REST, when it should use gRPC, and when it should support both. Walk through the protocol mechanics, performance trade-offs, developer experience, streaming capabilities, and provide a decision framework the entire engineering organization can follow.

This question tests:
- **Protocol depth** — understanding HTTP/1.1 vs HTTP/2, JSON vs Protobuf at the wire level, not just "REST is JSON, gRPC is fast."
- **Architectural judgment** — knowing that neither is universally better; the right choice depends on the use case.
- **Practical experience** — streaming, code generation, browser support, debugging, observability differences.
- **System design maturity** — the ability to design a hybrid architecture that uses both where each excels.

---

## 16.2 Step 1: REST — What It Actually Is (and Isn't)

```
REST (Representational State Transfer)

  REST is an ARCHITECTURAL STYLE, not a protocol.
  It uses HTTP as its transport and defines constraints:

  1. Client-server separation.
  2. Stateless: each request contains all info needed.
  3. Cacheable: responses indicate cacheability (Cache-Control).
  4. Uniform interface: resources identified by URIs,
     manipulated via standard HTTP methods (GET, POST, PUT, DELETE).
  5. Layered system: client does not know if it is talking to
     the origin server or a proxy/CDN/gateway.
  6. (Optional) Code on demand.

  In practice, "REST API" usually means:
  - HTTP/1.1 (sometimes HTTP/2).
  - JSON request/response bodies.
  - URL-based resource addressing.
  - Standard HTTP methods and status codes.
```

```
REST REQUEST/RESPONSE EXAMPLE

  Request:
    GET /api/v1/products/123 HTTP/1.1
    Host: api.example.com
    Accept: application/json
    Authorization: Bearer eyJhbGciOiJSUzI1...

  Response:
    HTTP/1.1 200 OK
    Content-Type: application/json
    Cache-Control: public, max-age=300

    {
      "id": 123,
      "name": "Widget",
      "price": 9.99,
      "category": "tools",
      "inStock": true
    }

  Human-readable. Debuggable with curl, browser, Postman.
  Self-describing (Content-Type, status codes, headers).
```

```
WHAT REST IS NOT

  Common misconceptions:
  - "REST = HTTP + JSON" -> No. REST is an architectural style.
    You can have REST over other protocols (rare).
    You can have HTTP APIs that are not RESTful (most are not).
  - "REST requires CRUD mapping" -> No. REST maps HTTP methods
    to resource operations, but not every API is CRUD.
  - "REST is slow" -> No. REST over HTTP/2 with JSON is fast.
    The perceived slowness is usually JSON serialization overhead,
    not the protocol itself.
  - "REST is always the right choice" -> No. For high-throughput
    internal service-to-service calls, gRPC is often better.
```

---

## 16.3 Step 2: gRPC — What It Actually Is

```
gRPC (Google Remote Procedure Call)

  gRPC is an open-source RPC FRAMEWORK built on:
  - HTTP/2 as transport (mandatory).
  - Protocol Buffers (Protobuf) as serialization format (default).
  - Code generation for client and server stubs.

  Key properties:
  - Binary protocol (Protobuf) -> smaller payloads, faster serialization.
  - HTTP/2 -> multiplexing, header compression, server push.
  - Strongly typed contracts (.proto files).
  - 4 communication patterns: unary, server streaming,
    client streaming, bidirectional streaming.
  - Code generation in 12+ languages from a single .proto file.
  - Built-in deadlines, cancellation, metadata.
```

```
PROTOBUF DEFINITION (.proto)

  syntax = "proto3";

  package api.product.v1;

  service ProductService {
    rpc GetProduct(GetProductRequest) returns (ProductResponse);
    rpc ListProducts(ListProductsRequest) returns (stream ProductResponse);
    rpc CreateProduct(CreateProductRequest) returns (ProductResponse);
  }

  message GetProductRequest {
    int64 id = 1;
  }

  message ProductResponse {
    int64 id = 1;
    string name = 2;
    double price = 3;
    string category = 4;
    bool in_stock = 5;
  }

  message ListProductsRequest {
    string category = 1;
    int32 page_size = 2;
    string page_token = 3;
  }

  message CreateProductRequest {
    string name = 1;
    double price = 2;
    string category = 3;
  }
```

```
gRPC CALL FLOW

  1. Developer writes .proto file (contract definition).
  2. protoc compiler generates:
     - Server interface (Java, Go, Python, C++, etc.).
     - Client stub (type-safe, auto-generated).
  3. Server implements the generated interface.
  4. Client calls the stub as if calling a local method:

     ProductResponse product = productStub.getProduct(
         GetProductRequest.newBuilder().setId(123).build()
     );

  5. Under the hood:
     Client -> serialize to Protobuf binary -> HTTP/2 frame ->
     Network -> HTTP/2 frame -> deserialize Protobuf -> Server
     Server -> serialize response -> HTTP/2 frame -> Client

  The developer never writes HTTP handling, serialization,
  or URL routing. Everything is generated.
```

---

## 16.4 Step 3: Head-to-Head Comparison

```
REST vs gRPC COMPARISON

  Dimension             | REST                          | gRPC
  ----------------------|-------------------------------|-------------------------------
  Protocol              | HTTP/1.1 (usually), HTTP/2    | HTTP/2 (mandatory)
  Serialization         | JSON (text, ~human-readable)  | Protobuf (binary, compact)
  Contract              | OpenAPI/Swagger (optional)     | .proto file (mandatory)
  Code generation       | Optional (OpenAPI Generator)  | Built-in (protoc compiler)
  Typing                | Loosely typed (JSON)          | Strongly typed (Protobuf)
  Streaming             | Not native (SSE, WebSocket    | Native: server, client,
                        | are workarounds)              | bidirectional streaming
  Browser support       | Native (fetch, XMLHttpRequest)| Requires grpc-web proxy
  Cacheability          | Built-in (HTTP cache headers) | Not cacheable by default
                        |                               | (POST-based, binary body)
  Debuggability         | Easy (curl, browser, Postman) | Hard (binary, need grpcurl
                        |                               | or Bloom RPC)
  Payload size          | Larger (JSON text + keys)     | 3-10x smaller (binary,
                        |                               | no field names on wire)
  Serialization speed   | Slower (JSON parse/stringify) | 5-10x faster (binary codec)
  Latency               | Higher (text parsing, no      | Lower (binary, multiplexing,
                        | multiplexing on HTTP/1.1)     | header compression)
  Throughput             | Good (thousands RPS)          | Excellent (tens of thousands
                        |                               | RPS per connection)
  Learning curve        | Low (everyone knows HTTP+JSON)| Medium (Protobuf, proto files,
                        |                               | code gen toolchain)
  Ecosystem             | Massive (every language/tool)  | Growing (12+ languages,
                        |                               | but fewer tools)
  Load balancing        | Simple (L7 HTTP)              | Requires gRPC-aware LB
                        |                               | (Envoy, Linkerd, Istio)
  Error handling        | HTTP status codes + body      | gRPC status codes + details
```

---

## 16.5 Step 4: Performance — HTTP/1.1 vs HTTP/2 vs HTTP/3

```
HTTP/1.1 (REST default)

  - One request per TCP connection at a time.
  - Head-of-line blocking: request 2 waits for request 1 to finish.
  - Workaround: browsers open 6-8 parallel connections.
  - Text-based headers (repeated on every request, no compression).
  - Keep-alive reuses connections but still one-at-a-time.

  Throughput ceiling: ~6-8 concurrent requests per host (browser).
  Server: can handle more, but each needs a full TCP connection.
```

```
HTTP/2 (gRPC mandatory, REST optional)

  - Multiplexing: many requests on ONE TCP connection simultaneously.
  - No head-of-line blocking at HTTP level (still at TCP level).
  - HPACK header compression (typically 85-95% smaller headers).
  - Server push (rarely used in practice).
  - Binary framing (more efficient than text).

  Throughput: hundreds of concurrent streams per connection.
  Latency: significantly lower for concurrent requests.

  REST can also use HTTP/2 (Spring Boot, Nginx support it).
  But gRPC REQUIRES HTTP/2, so it always gets these benefits.
```

```
HTTP/3 (QUIC)

  - Uses QUIC (UDP-based) instead of TCP.
  - No head-of-line blocking at transport level.
  - Faster connection establishment (0-RTT).
  - Better performance on lossy networks (mobile, WiFi).

  gRPC over HTTP/3: experimental support in some implementations.
  REST over HTTP/3: supported by modern browsers and CDNs.

  Not yet widely adopted for service-to-service communication.
```

```
PERFORMANCE BENCHMARK (typical)

  Scenario: 10,000 requests, 1 KB payload, same-DC network.

  Protocol          | Avg latency | Throughput   | Payload size
  ------------------|-------------|--------------|---------------
  REST/HTTP1.1+JSON | 2.5 ms      | 8,000 RPS    | 1,200 bytes
  REST/HTTP2+JSON   | 1.5 ms      | 15,000 RPS   | 1,200 bytes
  gRPC/HTTP2+Proto  | 0.5 ms      | 40,000 RPS   | 350 bytes

  gRPC is ~3-5x faster than REST/HTTP1.1 for internal calls.
  The difference comes from:
  1. Binary serialization (Protobuf vs JSON): ~5-10x faster.
  2. HTTP/2 multiplexing: fewer connections, less overhead.
  3. Smaller payloads: 3-10x smaller on wire.
  4. No text parsing: no JSON.parse() overhead.

  Note: for simple CRUD with small payloads, the difference
  may not matter (both are sub-millisecond). The gap widens
  with larger payloads, higher throughput, and streaming.
```

---

## 16.6 Step 5: Serialization — JSON vs Protocol Buffers

```
JSON

  {
    "id": 123,
    "name": "Widget",
    "price": 9.99,
    "category": "tools",
    "inStock": true
  }

  Size: ~95 bytes (text, includes field names).

  Pros:
  - Human-readable. Easy to debug, log, inspect.
  - Self-describing (field names in every message).
  - Universal: every language, every tool, every browser.
  - Schema-optional (flexible, but also a weakness).

  Cons:
  - Text-based: larger on wire, slower to parse.
  - Field names repeated in every message (waste).
  - No native types for dates, bytes, enums (everything is string/number).
  - Schema-optional: no compile-time safety without extra tooling.
```

```
PROTOCOL BUFFERS (Protobuf)

  Binary encoding of the same data: ~35 bytes.
  (Field numbers instead of names, varint encoding for integers,
   no whitespace, no quotes, no delimiters.)

  Pros:
  - 3-10x smaller payloads than JSON.
  - 5-10x faster serialization/deserialization.
  - Strongly typed: schema (.proto) is the contract.
  - Compile-time safety: generated code catches type errors.
  - Backward/forward compatible (field numbers, not names).

  Cons:
  - Not human-readable (binary). Cannot inspect with eyes.
  - Requires .proto file and code generation toolchain.
  - Harder to debug (need protobuf decode tools).
  - Schema is mandatory (less flexible for ad-hoc usage).
```

```
SIZE COMPARISON (real-world payloads)

  Payload                  | JSON size  | Protobuf size | Ratio
  -------------------------|------------|---------------|--------
  Simple object (5 fields) | 95 bytes   | 35 bytes      | 2.7x
  Nested object (20 fields)| 800 bytes  | 200 bytes     | 4x
  List of 100 items        | 12 KB      | 3 KB          | 4x
  Large analytics event    | 5 KB       | 600 bytes     | 8x
  Image metadata batch     | 50 KB      | 8 KB          | 6x

  The savings matter most at high throughput:
  10,000 RPS * 12 KB JSON = 120 MB/s network.
  10,000 RPS * 3 KB Proto = 30 MB/s network.
  That is 90 MB/s saved -> significant at scale.
```

---

## 16.7 Step 6: Streaming — The gRPC Superpower

```
gRPC STREAMING PATTERNS

  1. UNARY (request-response, like REST):
     Client sends 1 request -> Server sends 1 response.
     rpc GetProduct(Request) returns (Response);

  2. SERVER STREAMING:
     Client sends 1 request -> Server sends N responses (stream).
     rpc ListProducts(Request) returns (stream Response);

     Use case: large result sets, real-time feeds, log tailing.
     Client reads responses one by one as they arrive.

  3. CLIENT STREAMING:
     Client sends N requests (stream) -> Server sends 1 response.
     rpc UploadMetrics(stream MetricPoint) returns (UploadSummary);

     Use case: batch uploads, telemetry, file upload in chunks.
     Server processes the stream and returns a summary.

  4. BIDIRECTIONAL STREAMING:
     Client sends N requests AND Server sends N responses simultaneously.
     rpc Chat(stream ChatMessage) returns (stream ChatMessage);

     Use case: chat, multiplayer games, collaborative editing,
     real-time dashboards with bidirectional updates.
     Both sides send/receive independently on the same connection.
```

```
STREAMING COMPARISON

  Feature                | REST                          | gRPC
  -----------------------|-------------------------------|----------------------------
  Server push            | SSE (Server-Sent Events):     | Native server streaming.
                         | text-based, unidirectional.    | Binary, efficient,
                         | Limited browser support.       | multiplexed.
  Bidirectional          | WebSocket: separate protocol,  | Native bidirectional
                         | no HTTP semantics, complex     | streaming. Same HTTP/2
                         | load balancing.                | connection. Full gRPC
                         |                                | semantics (deadlines,
                         |                                | metadata, cancellation).
  Backpressure           | Not built-in (WebSocket).      | Built-in flow control
                         | Must implement manually.       | (HTTP/2 flow control).
  Multiplexing           | WebSocket: one stream per      | Multiple streams on one
                         | connection. Need multiple      | HTTP/2 connection.
                         | connections for multiple       |
                         | streams.                       |
```

```
SERVER STREAMING EXAMPLE (Java / Spring gRPC)

  // Proto definition:
  rpc ListProducts(ListProductsRequest) returns (stream ProductResponse);

  // Server implementation:
  @Override
  public void listProducts(ListProductsRequest request,
                            StreamObserver<ProductResponse> responseObserver) {
      List<Product> products = productRepo.findByCategory(request.getCategory());
      for (Product p : products) {
          responseObserver.onNext(toProto(p));  // Send each product as it is ready.
      }
      responseObserver.onCompleted();  // Signal end of stream.
  }

  // Client consumption:
  Iterator<ProductResponse> products = productStub.listProducts(request);
  while (products.hasNext()) {
      ProductResponse p = products.next();  // Received as stream.
      process(p);
  }

  No pagination needed. Server streams results as they are fetched.
  Client processes them incrementally. Memory efficient for large sets.
```

---

## 16.8 Step 7: Code Generation & Developer Experience

```
gRPC CODE GENERATION

  .proto file -> protoc compiler -> generated code in N languages.

  From ONE .proto file, you get:
  - Java classes (request/response POJOs + service interface).
  - Go structs + service interface.
  - Python classes.
  - C++ classes.
  - TypeScript types (for grpc-web).
  - ... 12+ languages.

  Benefits:
  - Single source of truth: .proto file IS the contract.
  - Type safety: compiler catches mismatches.
  - No hand-written serialization/deserialization code.
  - No hand-written HTTP client code.
  - Version control the .proto file -> contract is in Git.

  Developer workflow:
  1. Define/update .proto file.
  2. Run protoc (or Gradle/Maven plugin).
  3. Implement server interface.
  4. Client uses generated stub — call remote service like a local method.
```

```
REST CODE GENERATION (OpenAPI)

  OpenAPI spec (.yaml) -> OpenAPI Generator -> generated code.

  Benefits:
  - Similar to gRPC: generate client/server from spec.
  - Wide language support.

  Differences from gRPC codegen:
  - OpenAPI is OPTIONAL (most REST APIs do not have one).
  - Generated code quality varies (less mature than protoc).
  - JSON has no native type system -> generated types may be loose.
  - OpenAPI spec is often written AFTER the API (not code-first).
    With gRPC, the .proto file IS the code-first contract.

  In practice: gRPC codegen is more reliable and more widely
  adopted than OpenAPI codegen.
```

```
DEVELOPER EXPERIENCE COMPARISON

  Task                     | REST                         | gRPC
  -------------------------|------------------------------|---------------------------
  Define contract          | Write OpenAPI YAML (optional)| Write .proto file (required)
  Generate code            | OpenAPI Generator (optional) | protoc (mandatory, reliable)
  Make a call              | HTTP client (RestTemplate,   | Generated stub (type-safe,
                           | WebClient, fetch, axios)     | auto-complete in IDE)
  Debug a call             | curl, Postman, browser       | grpcurl, BloomRPC, Evans
  Inspect payload          | Read JSON (human-readable)   | Decode Protobuf (binary,
                           |                              | need decode tool)
  Add a field              | Update JSON + docs           | Update .proto, regenerate
  Error handling           | Parse HTTP status + JSON body| gRPC status + details
  API documentation        | Swagger UI (auto-generated)  | protoc-gen-doc, buf docs
  Testing                  | Any HTTP test tool           | gRPC testing requires
                           |                              | gRPC-specific tools
```

---

## 16.9 Step 8: Error Handling & Status Codes

```
REST ERROR HANDLING

  HTTP status codes (well-known, standardized):
    200 OK, 201 Created, 204 No Content
    400 Bad Request, 401 Unauthorized, 403 Forbidden
    404 Not Found, 409 Conflict, 422 Unprocessable Entity
    429 Too Many Requests
    500 Internal Server Error, 502 Bad Gateway, 503 Service Unavailable

  Error response body (custom, varies per API):
    {
      "error": {
        "code": "PRODUCT_NOT_FOUND",
        "message": "Product with id 123 not found",
        "details": [
          { "field": "id", "reason": "does not exist" }
        ]
      }
    }

  Problems:
  - No standard error body format (RFC 7807 exists but not universally adopted).
  - Different APIs use different error structures.
  - Status codes are HTTP-level, not application-level.
```

```
gRPC ERROR HANDLING

  gRPC status codes (16 codes, well-defined):
    OK (0), CANCELLED (1), UNKNOWN (2), INVALID_ARGUMENT (3),
    DEADLINE_EXCEEDED (4), NOT_FOUND (5), ALREADY_EXISTS (6),
    PERMISSION_DENIED (7), RESOURCE_EXHAUSTED (8),
    FAILED_PRECONDITION (9), ABORTED (10), OUT_OF_RANGE (11),
    UNIMPLEMENTED (12), INTERNAL (13), UNAVAILABLE (14),
    DATA_LOSS (15), UNAUTHENTICATED (16)

  Rich error details (google.rpc.Status):
    Status {
      code: NOT_FOUND,
      message: "Product 123 not found",
      details: [
        ErrorInfo { reason: "PRODUCT_NOT_FOUND", domain: "api.example.com" },
        BadRequest.FieldViolation { field: "id", description: "does not exist" }
      ]
    }

  Benefits:
  - Standardized codes (same across all gRPC APIs).
  - Rich, typed error details (Protobuf messages).
  - Client can programmatically inspect error details.
  - Deadlines and cancellation built into the framework.
```

```
STATUS CODE MAPPING (REST <-> gRPC)

  gRPC code            | HTTP equivalent     | Meaning
  ---------------------|---------------------|----------------------------
  OK                   | 200                 | Success
  INVALID_ARGUMENT     | 400                 | Bad request
  UNAUTHENTICATED      | 401                 | Missing/invalid credentials
  PERMISSION_DENIED    | 403                 | Not authorized
  NOT_FOUND            | 404                 | Resource not found
  ALREADY_EXISTS       | 409                 | Conflict
  RESOURCE_EXHAUSTED   | 429                 | Rate limited
  CANCELLED            | 499 (client closed) | Client cancelled
  DEADLINE_EXCEEDED    | 504                 | Timeout
  UNAVAILABLE          | 503                 | Service unavailable
  INTERNAL             | 500                 | Internal error
  UNIMPLEMENTED        | 501                 | Not implemented

  grpc-gateway (REST <-> gRPC proxy) maps these automatically.
```

---

## 16.10 Step 9: When to Choose REST

```
USE REST WHEN:

  1. PUBLIC-FACING APIs (external consumers):
     - Browsers natively support HTTP + JSON (fetch API).
     - No Protobuf toolchain required for consumers.
     - curl-friendly. Easy for third-party developers.
     - Swagger UI for documentation and testing.

  2. CRUD-HEAVY RESOURCE APIs:
     - REST's resource model (GET /products/123) maps naturally.
     - Standard HTTP methods (GET, POST, PUT, DELETE).
     - HTTP caching (Cache-Control, ETag) works out of the box.

  3. BROAD ECOSYSTEM REQUIREMENTS:
     - Every language, framework, tool supports HTTP + JSON.
     - API gateways (Kong, Apigee) are REST-native.
     - CDN caching works seamlessly.
     - OAuth 2.0 / OIDC flows are HTTP-based.

  4. DEVELOPER EXPERIENCE IS PRIORITY:
     - New developers can call the API with curl in 30 seconds.
     - No code generation setup needed.
     - Easy to prototype and iterate.

  5. BROWSER-FIRST APPLICATIONS:
     - Web apps, SPAs, PWAs.
     - No gRPC-web proxy needed.
     - Native browser caching, CORS, cookies.
```

---

## 16.11 Step 10: When to Choose gRPC

```
USE gRPC WHEN:

  1. INTERNAL SERVICE-TO-SERVICE COMMUNICATION:
     - Both client and server are services you control.
     - No browser involved.
     - Performance matters (latency, throughput, bandwidth).
     - Type safety across services (shared .proto files).

  2. HIGH-THROUGHPUT / LOW-LATENCY REQUIREMENTS:
     - > 10,000 RPS per connection.
     - Latency budget < 5 ms per call.
     - Large payload volumes where 3-10x size reduction matters.
     - Network bandwidth is a constraint or cost concern.

  3. STREAMING USE CASES:
     - Real-time data feeds (metrics, logs, events).
     - Server-side streaming for large result sets.
     - Bidirectional streaming (chat, collaboration, gaming).
     - Long-lived connections with flow control.

  4. POLYGLOT MICROSERVICES:
     - Services in different languages (Java, Go, Python, C++).
     - .proto file is the single contract for all languages.
     - Generated stubs ensure consistency across languages.

  5. STRICT CONTRACT ENFORCEMENT:
     - .proto file is version-controlled and code-reviewed.
     - Breaking changes detected at compile time.
     - Backward compatibility enforced by Protobuf wire format.

  6. MOBILE BACKENDS (internal):
     - Smaller payloads -> less data usage for mobile clients.
     - Faster serialization -> better battery life.
     - gRPC supports Android and iOS natively.
```

---

## 16.12 Step 11: The Hybrid Architecture (REST + gRPC)

```
THE COMMON PATTERN

  Most production systems use BOTH REST and gRPC:

  External clients (browser, mobile, third-party)
       |
       | REST (HTTP/1.1 + JSON)
       |
  +----v-----------+
  | API Gateway     |  (Kong, Envoy, AWS ALB)
  | (REST -> gRPC   |
  |  translation)   |
  +----+----+-------+
       |    |
       | gRPC (HTTP/2 + Protobuf)
       |    |
  +----v--+ +--v----+    +--------+    +--------+
  |Product| |Order  |    |Payment |    |Inventory|
  |Service| |Service|--->|Service |--->|Service  |
  +-------+ +-------+    +--------+    +--------+
       Internal service-to-service: gRPC

  External -> Gateway: REST (browser-friendly, cacheable).
  Gateway -> Services: gRPC (fast, type-safe, streaming).
  Service -> Service: gRPC (internal, high-throughput).
```

```
grpc-gateway (REST <-> gRPC PROXY)

  grpc-gateway generates a REST reverse proxy from .proto annotations:

  service ProductService {
    rpc GetProduct(GetProductRequest) returns (ProductResponse) {
      option (google.api.http) = {
        get: "/api/v1/products/{id}"
      };
    }
    rpc CreateProduct(CreateProductRequest) returns (ProductResponse) {
      option (google.api.http) = {
        post: "/api/v1/products"
        body: "*"
      };
    }
  }

  The proxy:
  - Accepts REST requests (GET /api/v1/products/123).
  - Translates to gRPC call (GetProduct({id: 123})).
  - Translates gRPC response to JSON.
  - Returns REST response to client.

  Benefits:
  - Write service ONCE in gRPC.
  - Get REST API for FREE (auto-generated proxy).
  - Single source of truth (.proto file).
  - External clients use REST; internal clients use gRPC.
```

```
ENVOY AS gRPC-WEB PROXY

  For browser clients that need gRPC:

  Browser -> Envoy (grpc-web proxy) -> gRPC service

  Envoy translates:
  - grpc-web (HTTP/1.1 compatible) -> native gRPC (HTTP/2).
  - Handles CORS, authentication, load balancing.

  grpc-web limitations:
  - No client streaming (unary and server streaming only).
  - Requires proxy (Envoy, Nginx with grpc-web module).
  - Not as well supported as native REST in browsers.

  Recommendation: use REST for browser clients unless you have
  a strong streaming requirement and can accept the proxy overhead.
```

---

## 16.13 Step 12: Migration — REST to gRPC (or Vice Versa)

```
MIGRATING FROM REST TO gRPC (internal services)

  Phase 1: DEFINE
    - Write .proto files that mirror your existing REST API.
    - Map REST resources to gRPC services and RPCs.
    - Map JSON fields to Protobuf messages.

  Phase 2: DUAL SUPPORT
    - Service supports BOTH REST and gRPC simultaneously.
    - REST handler and gRPC handler call the same business logic.
    - Or use grpc-gateway to auto-generate REST from gRPC.
    - Consumers migrate at their own pace.

  Phase 3: MIGRATE CONSUMERS
    - Internal consumers switch from REST client to gRPC stub.
    - Monitor REST traffic. When zero -> remove REST handler.
    - External consumers can stay on REST (via grpc-gateway).

  Phase 4: OPTIMIZE
    - Add streaming where it makes sense.
    - Tune HTTP/2 settings (max concurrent streams, keepalive).
    - Add deadline propagation across services.
```

```
MIGRATION PITFALLS

  Pitfall                               | Mitigation
  --------------------------------------|------------------------------------
  REST returns null fields; Protobuf    | Use wrapper types (google.protobuf
  has no null (default is 0/""/false).  | .Int64Value) or optional keyword.
  REST allows ad-hoc query params;      | Define all query fields in the
  gRPC does not.                        | request message explicitly.
  REST uses HTTP status codes; gRPC     | Map codes carefully. Use
  uses gRPC status codes.               | grpc-gateway's default mapping.
  REST errors have custom JSON bodies;  | Use google.rpc.Status with typed
  gRPC has structured error details.    | error detail messages.
  REST is cacheable by default; gRPC    | Add caching at the application
  is not (POST-based, binary).          | layer or use a caching proxy.
  REST clients exist in all languages;  | Ensure protoc plugins are available
  gRPC clients need code generation.    | for all consumer languages.
  Load balancers may not support gRPC.  | Use gRPC-aware LBs (Envoy, Linkerd,
                                        | Istio, AWS ALB with gRPC support).
```

---

## 16.14 Step 13: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  "gRPC is always faster"          | For simple CRUD with small payloads, REST
                                   | over HTTP/2 is nearly as fast. gRPC's
                                   | advantage grows with payload size, throughput,
                                   | and streaming needs.
  Browser support                  | gRPC needs grpc-web + proxy. REST works
                                   | natively. For browser-first apps, REST wins.
  Debuggability                    | REST: curl, Postman, browser DevTools.
                                   | gRPC: grpcurl, BloomRPC, Evans. Harder to
                                   | inspect binary traffic in logs and proxies.
  Caching                          | REST has built-in HTTP caching. gRPC does
                                   | not (POST-based, binary). Need application-
                                   | level caching for gRPC.
  Learning curve                   | REST: everyone knows HTTP + JSON. gRPC:
                                   | need to learn Protobuf, proto files, code
                                   | generation, gRPC semantics.
  Load balancing                   | REST: any L7 LB. gRPC: needs gRPC-aware LB
                                   | (Envoy, Linkerd). Standard HTTP LBs may not
                                   | handle HTTP/2 multiplexing correctly.
  Contract strictness              | gRPC forces a contract (.proto). REST allows
                                   | contract-less development. Strictness is a
                                   | feature for large orgs, a burden for small
                                   | teams prototyping.
  Null handling                    | Protobuf has no null. Default values (0, "",
                                   | false) are indistinguishable from "not set"
                                   | in proto3. Use optional keyword or wrapper
                                   | types for nullable semantics.
  Tooling maturity                 | REST tooling is vast and mature. gRPC tooling
                                   | is improving but still less comprehensive
                                   | (fewer API gateways, monitoring tools, etc.).
  Vendor lock-in                   | Neither is vendor-locked. But switching from
                                   | one to the other is a significant migration.
  Over-engineering                 | Using gRPC for a simple CRUD API with 3
                                   | consumers adds complexity without benefit.
                                   | Match the tool to the problem.
  Ignoring hybrid approach         | Most teams should use BOTH: REST for external,
                                   | gRPC for internal. Choosing one exclusively
                                   | means fighting against use cases where the
                                   | other is clearly better.
```

---

## 16.15 Problem 16 Summary

- **REST is an architectural style** using HTTP + JSON. Human-readable, browser-native, cacheable, universally supported. Best for public APIs, CRUD-heavy resources, and browser-first applications.
- **gRPC is an RPC framework** using HTTP/2 + Protobuf. Binary, strongly typed, code-generated, streaming-native. Best for internal service-to-service, high-throughput, low-latency, and streaming use cases.
- **Performance:** gRPC is 3-5x faster than REST/HTTP1.1+JSON for typical payloads. Protobuf is 3-10x smaller and 5-10x faster to serialize than JSON. HTTP/2 multiplexing eliminates head-of-line blocking.
- **Streaming is gRPC's superpower:** server streaming, client streaming, and bidirectional streaming are native. REST requires SSE (limited) or WebSocket (separate protocol, complex LB).
- **Code generation:** gRPC generates type-safe client stubs and server interfaces from .proto files in 12+ languages. OpenAPI codegen exists for REST but is optional and less mature.
- **Browser support:** REST wins. gRPC requires grpc-web + Envoy proxy. For browser-first apps, always use REST.
- **Caching:** REST has built-in HTTP caching (Cache-Control, ETag, CDN). gRPC is not cacheable by default (POST-based, binary). Cache at the application layer for gRPC.
- **Debuggability:** REST is trivially debuggable (curl, Postman, browser). gRPC requires specialized tools (grpcurl, BloomRPC) for binary inspection.
- **The hybrid architecture is the answer:** REST for external/browser-facing APIs, gRPC for internal service-to-service. Use grpc-gateway to generate REST proxies from .proto definitions — write once, serve both.
- **Load balancing:** gRPC requires gRPC-aware L7 load balancers (Envoy, Linkerd, Istio). Standard HTTP LBs may not handle HTTP/2 multiplexing correctly.
- **Error handling:** gRPC has 16 standardized status codes + typed error details (google.rpc.Status). REST uses HTTP status codes + custom JSON bodies (less standardized).
- **Decision framework:** use REST for public APIs, browser clients, CRUD, caching-heavy workloads, broad ecosystem needs. Use gRPC for internal service calls, high throughput (>10K RPS), latency budgets (<5ms), streaming, polyglot microservices, strict contracts. Use both for production systems.

---

# Problem 17: How Do You Design Monitoring + Alerting Systems?

## 17.1 Problem Statement

> Your organization runs 200+ microservices across three regions, handling 500K RPS at peak. Last month, an outage lasted 47 minutes before anyone noticed — the alert was buried in noise. You are asked to design the monitoring, alerting, and observability infrastructure from scratch. It must detect anomalies within 60 seconds, alert the right person with actionable context, and provide dashboards that let engineers diagnose root cause in under 5 minutes. Walk through the architecture, data pipelines, alert design, SLI/SLO framework, and the operational processes that make it all work.

This question tests:
- **Observability depth** — understanding the three pillars (metrics, logs, traces) and how they interrelate.
- **Pipeline architecture** — designing high-throughput data pipelines that handle millions of data points per second.
- **Alert design maturity** — knowing that bad alerts are worse than no alerts (alert fatigue kills incident response).
- **Operational thinking** — SLI/SLO/SLA, on-call processes, runbooks, incident management — the human side of monitoring.

---

## 17.2 Step 1: The Three Pillars of Observability

```
THE THREE PILLARS

  Pillar          | What it captures            | Example tools
  ----------------|-----------------------------|-----------------------------
  METRICS         | Numeric measurements over   | Prometheus, Datadog,
                  | time (counters, gauges,     | CloudWatch, Graphite,
                  | histograms).                | InfluxDB, Victoria Metrics
  LOGS            | Discrete events with        | ELK (Elasticsearch +
                  | context (structured or      | Logstash + Kibana), Loki,
                  | unstructured text).         | Splunk, Fluentd, Datadog
  TRACES          | End-to-end request path     | Jaeger, Zipkin, Tempo,
                  | across services (spans,     | OpenTelemetry, Datadog
                  | timing, dependencies).      | APM, AWS X-Ray

  Each pillar answers a different question:
  - Metrics: "WHAT is happening?" (CPU at 95%, error rate 2.3%)
  - Logs:    "WHY did it happen?" (NullPointerException at line 42)
  - Traces:  "WHERE in the call chain?" (ProductService -> DB took 800ms)

  You need ALL THREE. Metrics detect. Logs explain. Traces locate.
```

```
HOW THE PILLARS CONNECT

  Alert fires: "Error rate > 1% on OrderService" (METRIC)
       |
       v
  Engineer opens dashboard, sees error spike at 14:32 (METRIC)
       |
       v
  Clicks on error spike -> jumps to logs filtered by
  service=OrderService, time=14:32-14:35 (LOG)
       |
       v
  Sees: "Connection refused: PaymentService:8080" (LOG)
       |
       v
  Clicks trace ID from log -> opens distributed trace (TRACE)
       |
       v
  Trace shows: OrderService -> PaymentService (FAILED, 0ms)
               PaymentService was not responding.
       |
       v
  Root cause: PaymentService OOM-killed at 14:31. Auto-restarted at 14:35.

  The three pillars form a DIAGNOSTIC CHAIN:
  metric (detect) -> log (explain) -> trace (locate)
```

```
OPENTELEMETRY — THE UNIFIED STANDARD

  OpenTelemetry (OTel) provides a single SDK for all three pillars:

  - Metrics API: counters, gauges, histograms.
  - Logging API: structured log records.
  - Tracing API: spans, context propagation.
  - Auto-instrumentation: HTTP, gRPC, JDBC, Kafka, Redis — no code changes.
  - Exporters: send data to any backend (Prometheus, Jaeger, Loki, Datadog).

  Architecture:
    App (OTel SDK) -> OTel Collector -> Backend (Prometheus / Jaeger / Loki)

  The OTel Collector is the CENTRAL HUB:
  - Receives data from all services.
  - Processes (filter, sample, enrich).
  - Exports to one or more backends.
  - Decouples apps from backends (switch backends without code changes).

  Recommendation: USE OPENTELEMETRY for all new instrumentation.
  It is the CNCF standard and avoids vendor lock-in.
```

---

## 17.3 Step 2: Metrics Pipeline Architecture

```
METRIC TYPES

  1. COUNTER: monotonically increasing value.
     - http_requests_total, errors_total, bytes_sent_total.
     - Always increases. Rate of change is the useful signal.
     - Example: rate(http_requests_total[5m]) = RPS over last 5 min.

  2. GAUGE: value that goes up and down.
     - cpu_usage_percent, memory_used_bytes, active_connections.
     - Snapshot of current state.
     - Example: cpu_usage_percent = 78.3 (right now).

  3. HISTOGRAM: distribution of values in configurable buckets.
     - http_request_duration_seconds (with buckets: 10ms, 50ms,
       100ms, 250ms, 500ms, 1s, 5s).
     - Enables percentile calculation: p50, p95, p99.
     - Example: http_request_duration_seconds{quantile="0.99"} = 0.45
       means 99% of requests complete in under 450ms.

  4. SUMMARY: like histogram but calculates quantiles client-side.
     - Less flexible than histogram (cannot aggregate across instances).
     - Prefer histograms in most cases.
```

```
METRICS PIPELINE

  +----------+    +-----------+    +------------+    +----------+
  | Services |    | OTel      |    | Prometheus |    | Grafana  |
  | (OTel    |--->| Collector |--->| (TSDB)     |--->| (Dashbd) |
  | SDK)     |    | (process, |    | (store,    |    | (viz,    |
  |          |    |  filter,  |    |  query,    |    |  alert)  |
  +----------+    |  batch)   |    |  alert)    |    +----------+
                  +-----------+    +------------+
                                         |
                                   +-----v------+
                                   | Alertmanager|
                                   | (route,     |
                                   |  group,     |
                                   |  silence)   |
                                   +-------------+

  Flow:
  1. Services emit metrics via OTel SDK (or Prometheus client library).
  2. OTel Collector receives, batches, filters, and forwards.
  3. Prometheus scrapes metrics (pull model) every 15-30 seconds.
     Or: services push to Collector -> Collector remote-writes to Prometheus.
  4. Prometheus stores as time series. Evaluates alert rules.
  5. Alertmanager routes alerts to PagerDuty / Slack / email.
  6. Grafana queries Prometheus for dashboards.
```

```
PROMETHEUS PULL vs PUSH

  PULL (Prometheus default):
  - Prometheus scrapes /metrics endpoint of each service.
  - Simple. Service does not need to know about Prometheus.
  - Works well for long-running services.
  - Does not work well for short-lived jobs (use Pushgateway).

  PUSH (via OTel Collector / remote write):
  - Services push metrics to a collector/gateway.
  - Works for serverless, short-lived jobs, batch processes.
  - Collector aggregates and remote-writes to Prometheus/Thanos.

  Recommendation: pull for long-running services, push for ephemeral workloads.
  OTel Collector supports both and can translate between them.
```

```
CARDINALITY — THE METRICS KILLER

  Cardinality = number of unique time series.

  High cardinality kills your metrics system:
  - Each unique label combination = a new time series.
  - user_id as a label on every metric -> millions of time series.
  - Prometheus stores each series separately. Memory explodes.

  Rules:
  - NEVER use user_id, request_id, or session_id as a metric label.
  - Limit labels to bounded sets: method (GET/POST), status (2xx/4xx/5xx),
    service, endpoint, region, pod.
  - Monitor: prometheus_tsdb_head_series. Alert if > threshold.
  - Use logs for high-cardinality data (user_id belongs in logs, not metrics).

  Example of BAD metric:
    http_requests_total{user_id="12345", path="/api/products/67890"}
    -> millions of time series. Prometheus OOM.

  Example of GOOD metric:
    http_requests_total{service="product", method="GET", status="200"}
    -> bounded. ~hundreds of time series.
```

---

## 17.4 Step 3: Logging Pipeline Architecture

```
STRUCTURED LOGGING

  UNSTRUCTURED (bad):
    2024-01-15 14:32:01 ERROR Failed to process order 12345 for user 67890

  STRUCTURED (good):
    {
      "timestamp": "2024-01-15T14:32:01.123Z",
      "level": "ERROR",
      "service": "order-service",
      "traceId": "abc123def456",
      "spanId": "span789",
      "userId": "67890",
      "orderId": "12345",
      "message": "Failed to process order",
      "error": "PaymentService: Connection refused",
      "duration_ms": 342
    }

  Why structured:
  - Machine-parseable (filter by userId, orderId, traceId).
  - Consistent format across all services.
  - Enables correlation: log -> trace (via traceId).
  - Searchable in Elasticsearch, Loki, Splunk.
```

```
LOGGING PIPELINE

  +----------+    +-----------+    +---------------+    +--------+
  | Services |    | Log       |    | Elasticsearch |    | Kibana |
  | (stdout) |--->| Collector |--->| (or Loki)     |--->| (or    |
  |          |    | (Fluentd, |    | (index, store,|    | Grafana|
  |          |    |  Filebeat,|    |  search)      |    | )      |
  +----------+    |  Vector)  |    +---------------+    +--------+
                  +-----------+
                       |
                  (filter, parse, enrich, buffer, retry)

  Flow:
  1. Services write logs to stdout (container best practice).
  2. Container runtime (Docker/K8s) captures stdout to files.
  3. Log collector (DaemonSet: Fluentd/Filebeat/Vector) tails files.
  4. Collector parses (JSON), enriches (add pod/namespace labels),
     filters (drop debug in prod), buffers (backpressure).
  5. Ships to Elasticsearch (full-text search) or Loki (label-indexed).
  6. Kibana/Grafana for search, visualization, correlation.
```

```
ELASTICSEARCH vs LOKI

  Dimension           | Elasticsearch (ELK)         | Loki (Grafana)
  --------------------|-----------------------------|-----------------------------
  Indexing            | Full-text index on all fields| Index labels only (like
                      | (inverted index).           | Prometheus). Log content
                      |                             | is NOT indexed.
  Query speed         | Fast for any field search.  | Fast for label-filtered,
                      |                             | slower for full-text grep.
  Storage cost        | High (full-text index is    | Low (no content index,
                      | 1.5-2x raw data size).      | compressed chunks).
  Operations          | Complex (JVM tuning, shard  | Simple (stateless queriers,
                      | management, cluster health).| object storage backend).
  Cost at scale       | Expensive (10 TB/day =      | Cheap (same data = 3-5x
                      | significant infra).          | less storage + compute).
  Best for            | Rich search, compliance,    | Cost-effective logging,
                      | security analytics.         | Kubernetes-native, Grafana.

  Recommendation:
  - Small-medium: Loki (simpler, cheaper, integrates with Grafana).
  - Large / compliance: Elasticsearch (richer querying, established).
```

```
LOG RETENTION STRATEGY

  Tier                | Duration    | Storage          | Access
  --------------------|-------------|------------------|------------------
  Hot (recent)        | 7-14 days   | SSD / ES cluster | Real-time search
  Warm (recent past)  | 30-90 days  | HDD / cheaper    | Slower search
                      |             | nodes            |
  Cold (archive)      | 1-7 years   | S3 / Glacier     | Restore on demand
                      |             |                  | (compliance)
  Delete              | After cold  | —                | —

  Rules:
  - Only index what you need to search. Archive the rest.
  - Drop debug/trace logs in production (or sample 1%).
  - Retain ERROR/WARN logs longer than INFO.
  - Compliance may require years of retention (healthcare, finance).
```

---

## 17.5 Step 4: Distributed Tracing

```
WHY DISTRIBUTED TRACING

  In monolith: a stack trace shows the full call path.
  In microservices: a request crosses 5-15 services. Stack trace
  shows only ONE service. You cannot see the full picture.

  Distributed tracing solves this:
  - Assigns a unique TRACE ID to each request at the edge.
  - Each service creates a SPAN (a unit of work) with timing.
  - Spans are linked by trace ID and parent-child relationships.
  - The full trace shows the end-to-end request path across all services.
```

```
TRACE ANATOMY

  Trace ID: abc123

  +------------------------------------------------------------------+
  | API Gateway (span 1)                               200ms total   |
  |  +------------------------------------------------------------+  |
  |  | OrderService (span 2)                          180ms        |  |
  |  |  +------------------------------------------------------+  |  |
  |  |  | ProductService (span 3)                  25ms         |  |  |
  |  |  +------------------------------------------------------+  |  |
  |  |  +------------------------------------------------------+  |  |
  |  |  | InventoryService (span 4)                40ms         |  |  |
  |  |  +------------------------------------------------------+  |  |
  |  |  +------------------------------------------------------+  |  |
  |  |  | PaymentService (span 5)                  95ms ← SLOW |  |  |
  |  |  |  +--------------------------------------------------+|  |  |
  |  |  |  | Stripe API (span 6)                   80ms        ||  |  |
  |  |  |  +--------------------------------------------------+|  |  |
  |  |  +------------------------------------------------------+  |  |
  |  +------------------------------------------------------------+  |
  +------------------------------------------------------------------+

  The trace shows:
  - Total latency: 200ms.
  - PaymentService is the bottleneck (95ms of 200ms).
  - Stripe API call inside PaymentService is the root cause (80ms).
  - Without tracing, you would only see "OrderService is slow."
```

```
CONTEXT PROPAGATION

  How trace ID flows across services:

  Client -> API Gateway -> OrderService -> PaymentService -> Stripe

  Each hop adds a header:
    traceparent: 00-abc123-span1-01
    (W3C Trace Context standard)

  OTel SDK auto-injects this header for:
  - HTTP calls (RestTemplate, WebClient, fetch).
  - gRPC calls (metadata).
  - Kafka messages (headers).
  - JDBC calls (span wrapping).

  No manual header passing needed with OTel auto-instrumentation.
```

```
SAMPLING STRATEGIES

  At 500K RPS, storing every trace is expensive.
  Sampling reduces cost while preserving diagnostic value.

  Strategy               | How it works                    | Trade-off
  -----------------------|---------------------------------|--------------------
  Head-based sampling    | Decide at the start: keep or    | Simple. But may
  (probability)          | drop. E.g., keep 1% of traces. | miss rare errors.
  Tail-based sampling    | Decide AFTER trace completes.   | Captures errors
                         | Keep errors, slow traces,       | and outliers.
                         | drop normal traces.             | Requires buffering.
  Rate-limited sampling  | Keep first N traces/second.     | Predictable cost.
                         | Drop the rest.                  | May miss patterns.
  Always-on for errors   | Sample normal at 1%.            | Best of both:
  + sample normal        | Keep 100% of error traces.      | low cost + full
                         |                                 | error visibility.

  Recommendation: tail-based sampling with always-on for errors.
  OTel Collector supports tail-based sampling natively.
```

---

## 17.6 Step 5: SLI, SLO, SLA — Defining What "Healthy" Means

```
DEFINITIONS

  SLI (Service Level Indicator):
    A measurable metric that indicates service health.
    Examples:
    - Request latency p99 < 500ms.
    - Error rate (5xx / total) < 0.1%.
    - Availability = successful requests / total requests.

  SLO (Service Level Objective):
    A TARGET for an SLI over a time window.
    Examples:
    - "99.9% of requests complete in < 500ms over 30 days."
    - "Error rate < 0.1% over 7 days."
    - "99.95% availability per month."

  SLA (Service Level Agreement):
    A CONTRACT with customers that specifies consequences for
    missing the SLO (refunds, credits, penalties).
    - SLA is a business document. SLO is an engineering target.
    - SLA thresholds are looser than SLOs (buffer for safety).
      Example: SLO = 99.95% availability. SLA = 99.9% availability.
```

```
ERROR BUDGET

  Error budget = 1 - SLO.

  If SLO = 99.9% availability per month:
  - Error budget = 0.1% = 43.2 minutes of downtime allowed.
  - If you have used 30 minutes this month, you have 13.2 minutes left.

  Error budget policies:
  - Budget remaining > 50%: ship features aggressively.
  - Budget remaining 10-50%: proceed cautiously, extra testing.
  - Budget exhausted (0%): FREEZE deployments. Focus on reliability.
    No new features until the budget resets next month.

  Error budget is the NEGOTIATION TOOL between product and engineering:
  - Product wants features (risk of breaking things).
  - Engineering wants reliability (risk of shipping nothing).
  - Error budget says: "you can break things THIS MUCH and no more."
```

```
CHOOSING THE RIGHT SLIs

  Service type          | Primary SLI              | Target SLO
  ----------------------|--------------------------|--------------------
  API service           | Latency p99              | < 500ms, 99.9%
                        | Error rate (5xx)         | < 0.1%
                        | Availability             | 99.95%
  Data pipeline         | Freshness (lag)          | < 5 minutes
                        | Correctness              | 99.99%
                        | Throughput               | > N events/sec
  Storage service       | Durability               | 99.999999999% (11 9s)
                        | Read latency p99         | < 10ms
                        | Availability             | 99.99%
  Batch job             | Completion rate          | 100% within SLA window
                        | Processing time          | < 2 hours
  User-facing web       | Time to first byte (TTFB)| < 200ms
                        | Page load time           | < 3 seconds
                        | Core Web Vitals (LCP)    | < 2.5 seconds

  Rules:
  - Measure from the CLIENT's perspective (not the server's).
  - Use the WORST percentile that matters (p99, not p50).
  - Keep SLIs to 3-5 per service. More = noise.
```

---

## 17.7 Step 6: Alert Design — Avoiding Alert Fatigue

```
THE ALERT FATIGUE PROBLEM

  Symptom: engineers ignore alerts because there are too many.
  Cause: every metric has an alert. Most are noise.
  Result: real incidents are buried. MTTD (mean time to detect) = hours.

  The fix: FEWER, BETTER alerts.

  Rules for good alerts:
  1. ALERT ON SLOs, NOT METRICS.
     Bad:  "CPU > 80%." (So what? Service may be fine.)
     Good: "Error budget burn rate > 2x." (Service is degrading.)

  2. ACTIONABLE.
     Every alert must have a clear action.
     If you cannot write a runbook for it, do not alert on it.

  3. NO DUPLICATE ALERTS.
     If OrderService is down, do not also alert on:
     - OrderService CPU = 0%
     - OrderService memory = 0%
     - OrderService health check failed
     - OrderService connection pool empty
     That is ONE incident, not four. Group and deduplicate.

  4. SEVERITY LEVELS.
     - P1 (Critical): pages on-call. Customer impact NOW.
       Example: SLO violation, data loss, security breach.
     - P2 (Warning): Slack notification. Degradation, approaching SLO.
       Example: error rate rising, latency increasing.
     - P3 (Info): dashboard only. No notification.
       Example: deployment completed, scaling event.

  5. ALERT ON SYMPTOMS, NOT CAUSES.
     Symptom: "Users are getting errors." (alert on this)
     Cause:   "Database CPU is high." (investigate this)
     Alert on what the USER experiences, not what the INFRA is doing.
```

```
BURN-RATE ALERTING (Google SRE)

  Instead of alerting on instantaneous threshold violations,
  alert on the RATE at which you are consuming your error budget.

  Error budget = 43.2 minutes/month (99.9% SLO).

  Burn rate = actual error rate / allowed error rate.
  - Burn rate 1x: consuming budget at exactly the allowed rate.
    Will exhaust in 30 days. No alert.
  - Burn rate 2x: consuming budget at 2x the allowed rate.
    Will exhaust in 15 days. Warning.
  - Burn rate 10x: consuming budget at 10x the allowed rate.
    Will exhaust in 3 days. Page on-call.
  - Burn rate 100x: consuming budget at 100x the allowed rate.
    Will exhaust in 7 hours. CRITICAL page.

  Multi-window burn rate alerts:
  - Short window (5 min) + burn rate 14x -> PAGE NOW.
    (Fast, severe incident.)
  - Medium window (1 hour) + burn rate 6x -> PAGE.
    (Sustained degradation.)
  - Long window (6 hours) + burn rate 2x -> WARN (Slack).
    (Slow burn, investigate during business hours.)

  This approach:
  - Catches real incidents quickly (short window, high burn rate).
  - Ignores transient spikes (short window alone is not enough).
  - Detects slow degradation (long window, low burn rate).
  - Reduces false positives dramatically.
```

```
ALERTMANAGER ROUTING

  Alertmanager receives alerts from Prometheus and routes them:

  routes:
    - match:
        severity: critical
      receiver: pagerduty-oncall
      group_by: [service, alertname]
      group_wait: 30s           # Wait 30s to batch related alerts.
      group_interval: 5m        # Send updates every 5 min.
      repeat_interval: 4h       # Re-alert every 4 hours if unresolved.

    - match:
        severity: warning
      receiver: slack-alerts
      group_by: [service]
      group_wait: 2m
      repeat_interval: 24h

    - match:
        severity: info
      receiver: null             # No notification. Dashboard only.

  Silences:
  - During deployments: silence alerts for the deploying service for 10 min.
  - During maintenance: silence all non-critical alerts.
  - After incident: silence resolved alerts to prevent noise.

  Inhibition:
  - If "service_down" is firing, inhibit all other alerts for that service.
    (No need to alert on latency/errors if the service is completely down.)
```

---

## 17.8 Step 7: Dashboard Design

```
THE FOUR GOLDEN SIGNALS (Google SRE)

  Every service dashboard should show these four signals:

  1. LATENCY: time to serve a request.
     - Show p50, p95, p99.
     - Separate successful vs failed request latency.

  2. TRAFFIC: demand on the system.
     - RPS (requests per second).
     - By endpoint, by status code.

  3. ERRORS: rate of failed requests.
     - 5xx rate, 4xx rate (separately).
     - Error rate as percentage of total traffic.

  4. SATURATION: how "full" the system is.
     - CPU utilization, memory utilization.
     - Thread pool usage, connection pool usage.
     - Queue depth, disk I/O.
```

```
DASHBOARD HIERARCHY

  Level 1: EXECUTIVE OVERVIEW (for everyone)
    - Overall system health: green/yellow/red.
    - SLO compliance across all services.
    - Active incidents count.
    - Error budget remaining per service.

  Level 2: SERVICE DASHBOARD (for service owners)
    - Four golden signals for this service.
    - Dependency health (services this service calls).
    - Recent deployments (correlated with metric changes).
    - Top 5 slowest endpoints.

  Level 3: DEEP DIVE (for debugging)
    - Per-endpoint latency histograms.
    - Database query performance.
    - Cache hit/miss rates.
    - JVM/GC metrics, thread pools, connection pools.
    - Kafka consumer lag.

  Level 4: INFRASTRUCTURE (for platform team)
    - Kubernetes: pod restarts, node CPU/memory, scheduling failures.
    - Network: packet loss, DNS resolution time.
    - Storage: IOPS, disk latency, replication lag.

  Rule: every dashboard has a PURPOSE and an AUDIENCE.
  Do not put JVM metrics on the executive dashboard.
```

```
GRAFANA BEST PRACTICES

  - Use CONSISTENT panel layouts across all service dashboards.
    (Same order: latency, traffic, errors, saturation.)
  - Add ANNOTATIONS for deployments, config changes, incidents.
    (Vertical lines on the timeline: "v2.3.1 deployed at 14:00.")
  - Use TEMPLATE VARIABLES for service, environment, region.
    (One dashboard template, reusable for all services.)
  - Set MEANINGFUL THRESHOLDS (red/yellow/green zones on gauges).
  - Link dashboards to LOGS and TRACES (click metric -> filtered logs).
  - Show COMPARISON with previous period (today vs yesterday).
  - Keep panels to 8-12 per dashboard. More = overwhelming.
```

---

## 17.9 Step 8: On-Call & Incident Response

```
INCIDENT SEVERITY LEVELS

  Level  | Definition                         | Response
  -------|------------------------------------|--------------------------
  SEV-1  | Major customer impact.             | Page on-call immediately.
         | Revenue loss, data loss,           | War room. All hands.
         | security breach.                   | Communicate externally.
  SEV-2  | Significant degradation.           | Page on-call.
         | Elevated errors, high latency.     | Incident channel.
         | Partial feature unavailability.    | Fix within 1 hour.
  SEV-3  | Minor impact.                      | Notify via Slack.
         | Non-critical feature degraded.     | Fix within business day.
         | Workaround available.              |
  SEV-4  | No customer impact.                | Track in ticket.
         | Internal tooling issue.            | Fix within sprint.
         | Cosmetic bug.                      |
```

```
INCIDENT RESPONSE PROCESS

  1. DETECT (automated):
     - Alert fires from burn-rate violation.
     - PagerDuty pages on-call engineer.
     - Slack incident channel created automatically.

  2. TRIAGE (< 5 minutes):
     - On-call acknowledges alert.
     - Opens service dashboard. Identifies affected service.
     - Determines severity (SEV-1/2/3/4).
     - If SEV-1/2: declare incident, start incident channel.

  3. DIAGNOSE (< 15 minutes):
     - Check recent deployments (was anything deployed?).
     - Check four golden signals (latency, traffic, errors, saturation).
     - Check dependency health (is a downstream service failing?).
     - Jump from metrics -> logs -> traces to find root cause.
     - Use runbook for the specific alert.

  4. MITIGATE (as fast as possible):
     - Rollback deployment (if deployment-related).
     - Scale up (if capacity-related).
     - Failover to backup region (if region failure).
     - Toggle feature flag (if feature-related).
     - Restart service (if memory leak / deadlock).
     Goal: STOP THE BLEEDING. Root cause analysis comes later.

  5. RESOLVE:
     - Confirm metrics return to normal.
     - Close incident channel.
     - Create post-incident review (PIR) ticket.

  6. POST-INCIDENT REVIEW (within 48 hours):
     - Timeline of events.
     - Root cause analysis (5 whys).
     - What went well, what did not.
     - Action items (prevent recurrence).
     - BLAMELESS. Focus on systems, not people.
```

```
RUNBOOK TEMPLATE

  Alert: OrderService Error Rate > SLO Burn Rate 10x
  Severity: P1 (Critical)

  1. VERIFY:
     - Dashboard: https://grafana.internal/d/order-service
     - Check: error rate panel. Confirm spike is real (not a metric glitch).

  2. RECENT CHANGES:
     - Deployments: https://argocd.internal/order-service
     - Config changes: https://consul.internal/order-service
     - If deployed in last 30 min -> ROLLBACK FIRST, investigate later.

  3. COMMON CAUSES:
     a. Database connection pool exhausted:
        - Check: order_db_pool_active / order_db_pool_max.
        - Fix: increase pool size or identify long-running queries.
     b. PaymentService timeout:
        - Check: PaymentService dashboard.
        - Fix: if PaymentService is down, enable fallback (queue orders).
     c. Kafka consumer lag:
        - Check: kafka_consumer_lag{group="order-processor"}.
        - Fix: scale consumer group. Check for poison pill messages.

  4. ESCALATION:
     - If not resolved in 15 min -> escalate to senior on-call.
     - If data loss suspected -> escalate to data engineering + management.

  Every alert MUST have a runbook. No runbook = do not alert.
```

---

## 17.10 Step 9: Anomaly Detection & AIOps

```
STATIC THRESHOLDS vs ANOMALY DETECTION

  STATIC THRESHOLDS:
  - "Alert if CPU > 80%."
  - Simple. Predictable. Easy to understand.
  - Problem: what if normal CPU on weekends is 30% and weekdays is 70%?
    80% threshold misses weekend anomalies and causes weekday false alarms.

  ANOMALY DETECTION:
  - "Alert if CPU deviates > 3 standard deviations from the predicted
    value for this time of day and day of week."
  - Learns seasonal patterns (daily, weekly cycles).
  - Detects anomalies relative to EXPECTED behavior, not absolute thresholds.

  Tools:
  - Datadog: built-in anomaly detection algorithms.
  - Prometheus + ML: use recording rules + external ML models.
  - AWS CloudWatch: anomaly detection bands.
  - Custom: Prophet (Facebook), ARIMA, Holt-Winters for time series forecasting.
```

```
WHEN TO USE EACH

  Use static thresholds for:
  - Hard limits: disk > 90%, memory > 95%, error rate > 1%.
  - SLO-based alerts (burn rate).
  - Binary states: service up/down, certificate expiring.

  Use anomaly detection for:
  - Traffic patterns (seasonal, weekly cycles).
  - Latency trends (gradual degradation).
  - Business metrics (order volume, revenue).
  - Detecting "unknown unknowns" (patterns you did not anticipate).

  Recommendation: START with static thresholds + burn rate.
  ADD anomaly detection for traffic, latency, and business metrics
  after the basics are solid. Do not skip fundamentals for AI.
```

---

## 17.11 Step 10: Scaling the Observability Stack

```
SCALING METRICS (Prometheus)

  Problem: single Prometheus server hits limits at ~10M time series.
  At 200+ services with thousands of metrics each, you exceed this fast.

  Solutions:

  1. FEDERATION:
     - Regional Prometheus servers scrape local services.
     - Global Prometheus federates (scrapes aggregated metrics).
     - Simple. But global server is still a bottleneck.

  2. THANOS:
     - Adds long-term storage (S3) and global query view.
     - Prometheus -> Thanos Sidecar -> S3 (long-term).
     - Thanos Querier: query across all Prometheus instances.
     - Thanos Compactor: downsample old data (reduce storage).
     - Production-proven at large scale.

  3. VICTORIA METRICS:
     - Drop-in replacement for Prometheus (compatible API).
     - Better performance and compression than Prometheus.
     - Cluster mode for horizontal scaling.
     - Lower resource usage (RAM, CPU, disk).

  4. MIMIR (Grafana):
     - Horizontally scalable, multi-tenant Prometheus backend.
     - Object storage backend (S3, GCS).
     - Grafana-native. Integrates with Grafana Cloud.
```

```
SCALING LOGS

  Problem: 200 services * 1000 log lines/sec = 200K logs/sec.
  At 500 bytes/log -> 100 MB/s -> 8.6 TB/day raw logs.

  Solutions:
  - SAMPLING: log 100% of errors, 10% of warnings, 1% of info.
  - FILTERING: drop health checks, readiness probes, debug logs in prod.
  - AGGREGATION: aggregate identical logs (e.g., "Connection refused"
    appeared 50,000 times in 5 minutes -> store once with count).
  - TIERED STORAGE: hot (7 days, SSD) -> warm (30 days, HDD) ->
    cold (1 year, S3). Auto-migrate with ILM (Index Lifecycle Management).
  - LOKI: inherently cheaper than Elasticsearch for large volumes
    (no full-text indexing, compressed chunks on object storage).
```

```
SCALING TRACES

  Problem: 500K RPS * full trace = impossible to store affordably.

  Solutions:
  - TAIL-BASED SAMPLING (in OTel Collector):
    Keep 100% of error/slow traces. Sample 1% of normal traces.
    Reduces volume by 50-99x while preserving diagnostic value.
  - GRAFANA TEMPO: stores traces in object storage (S3).
    No indexing. Find traces by trace ID (from logs).
    Very cheap storage. But cannot search traces without a trace ID.
  - JAEGER WITH ELASTICSEARCH: richer query capabilities but
    higher storage cost (indexed).
  - Recommendation: Tempo + trace IDs in logs (find trace via log search).
```

---

## 17.12 Step 11: Cost Management

```
OBSERVABILITY COST BREAKDOWN

  Component          | Cost driver                | Typical cost
  -------------------|----------------------------|--------------------
  Metrics storage    | Time series count * retention| $0.10-$0.30 per
                     | duration.                  | 1000 series/month.
  Log storage        | Volume ingested * retention.| $1-$3 per GB
                     |                            | ingested.
  Trace storage      | Spans ingested * retention. | $0.30-$1.50 per
                     |                            | million spans.
  Alerting           | Alert evaluations/month.   | Usually included.
  Dashboards         | Users * queries/day.       | Usually included.

  At scale (200 services, 500K RPS):
  - Metrics: ~5M time series = $500-$1500/month.
  - Logs: ~8.6 TB/day * 14 days = 120 TB = $5K-$15K/month.
  - Traces: ~500K spans/sec * 1% sampled = 5K spans/sec
            = 13B spans/month = $4K-$20K/month.

  LOGS ARE THE BIGGEST COST. Always.
```

```
COST OPTIMIZATION STRATEGIES

  Strategy                           | Impact
  -----------------------------------|--------------------------------------
  Sample logs (1% info, 100% error)  | 50-90% log volume reduction.
  Drop health check / probe logs     | 10-30% log volume reduction.
  Use Loki instead of Elasticsearch  | 3-5x cheaper storage.
  Tail-based trace sampling          | 50-99% trace volume reduction.
  Reduce metric cardinality          | Prevents OOM + reduces cost.
  Tiered storage (hot -> warm -> S3) | 5-10x cheaper for old data.
  Aggregate duplicate logs           | 10-50% volume reduction.
  Shorten retention (30 days -> 14)  | 50% storage reduction.
  Use recording rules (pre-aggregate)| Fewer queries, less compute.
  Self-hosted vs SaaS trade-off     | Self-hosted is cheaper at scale
                                     | but requires operational expertise.
```

---

## 17.13 Step 12: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Alert fatigue                    | Too many alerts = engineers ignore ALL alerts.
                                   | Use burn-rate alerting. Fewer, actionable alerts.
                                   | Every alert must have a runbook.
  Cardinality explosion            | High-cardinality labels (user_id, request_id)
                                   | crash Prometheus. Use labels with bounded
                                   | values only. Put high-cardinality data in logs.
  Observability as afterthought    | Adding monitoring after launch is 10x harder.
                                   | Instrument from day 1. Use OTel auto-
                                   | instrumentation for zero-effort baseline.
  Logs as the only signal          | Logs alone cannot detect issues proactively.
                                   | Metrics detect, logs explain, traces locate.
                                   | You need all three pillars.
  SLOs too tight                   | 99.99% SLO = 4.3 min/month error budget.
                                   | Most teams cannot sustain this. Start with
                                   | 99.9% and tighten only when ready.
  SLOs too loose                   | 99% SLO = 7.3 hours/month allowed downtime.
                                   | No urgency to fix issues. Users leave before
                                   | you notice. Match SLO to user expectations.
  Vendor lock-in                   | Datadog, New Relic, Splunk are powerful but
                                   | expensive and proprietary. OTel + open-source
                                   | backends (Prometheus, Loki, Tempo) avoid lock-in.
  Cost explosion at scale          | Logs grow linearly with traffic. Without
                                   | sampling, filtering, and tiered storage, costs
                                   | become the #1 infra expense.
  Missing trace correlation        | Logs without trace IDs are useless for
                                   | cross-service debugging. Always include
                                   | traceId and spanId in every log line.
  No runbooks                      | Alerts without runbooks are noise. Engineers
                                   | waste time figuring out what to do. Every alert
                                   | must link to a runbook with clear steps.
  Dashboards without audience      | A dashboard for "everything" helps no one.
                                   | Design dashboards for specific roles:
                                   | executive, service owner, debugger, platform.
  Ignoring error budgets           | Without error budgets, there is no objective
                                   | way to balance features vs reliability.
                                   | Error budget is the negotiation tool.
```

---

## 17.14 Problem 17 Summary

- **Three pillars of observability:** metrics detect ("what is happening"), logs explain ("why"), traces locate ("where in the call chain"). You need all three. OpenTelemetry is the CNCF standard that unifies them.
- **Metrics pipeline:** Services → OTel Collector → Prometheus (TSDB) → Grafana (dashboards) + Alertmanager (alerts). Use counters, gauges, and histograms. Guard against cardinality explosion — never use user_id or request_id as metric labels.
- **Logging pipeline:** Services (stdout) → Log collector (Fluentd/Vector) → Elasticsearch or Loki → Kibana/Grafana. Always use structured logging (JSON). Include traceId in every log line. Loki is 3-5x cheaper than Elasticsearch.
- **Distributed tracing:** Assigns trace ID at the edge, each service creates spans. Shows end-to-end request path and bottlenecks. Use tail-based sampling (keep 100% errors, sample 1% normal). Grafana Tempo for cost-effective storage.
- **SLI/SLO/SLA:** SLI is the metric (latency p99, error rate), SLO is the target (99.9% over 30 days), SLA is the contract with customers. Error budget (1 - SLO) is the negotiation tool between features and reliability.
- **Alert design:** alert on SLOs (burn rate), not raw metrics. Use multi-window burn-rate alerting (Google SRE). Fewer, actionable alerts. Every alert must have a runbook. Severity levels: P1 pages on-call, P2 Slack, P3 dashboard only.
- **Dashboard design:** follow the four golden signals (latency, traffic, errors, saturation). Dashboard hierarchy: executive overview → service dashboard → deep dive → infrastructure.
- **Incident response:** detect (automated alert) → triage (<5 min) → diagnose (metrics → logs → traces, <15 min) → mitigate (rollback/scale/failover) → resolve → post-incident review (blameless, 48 hours).
- **Anomaly detection:** start with static thresholds + burn rate. Add anomaly detection for traffic patterns, latency trends, and business metrics after fundamentals are solid.
- **Scaling:** Thanos/VictoriaMetrics/Mimir for metrics beyond single Prometheus. Loki for cost-effective logs. Tempo + tail-based sampling for traces. Tiered storage for retention.
- **Cost management:** logs are the biggest cost. Sample (1% info, 100% errors), filter (drop health checks), aggregate duplicates, tier storage (hot → warm → cold). At 200 services, observability can cost $10K-$35K/month — optimize aggressively.

---

# Problem 18: Design a System for High Write Throughput (>1M Writes/Sec)

## 18.1 Problem Statement

> Your company operates an IoT platform that ingests telemetry from 50 million connected devices. Each device reports sensor readings every second during peak windows. The system must sustain **>1 million writes per second** with durability guarantees, support analytical queries on recent data with <5 second freshness, and retain raw data for 90 days. Walk through the storage engine choices, ingestion pipeline, sharding strategy, and the architectural patterns that make million-write-per-second systems possible.

This question tests:
- **Storage engine depth** — understanding LSM trees, B-trees, WAL, and why they behave differently under write load.
- **Pipeline architecture** — designing ingestion pipelines that decouple producers from storage.
- **Sharding and partitioning** — distributing writes across nodes without hot spots.
- **Trade-off awareness** — knowing what you sacrifice for extreme write throughput (read latency, space amplification, compaction storms).

---

## 18.2 Step 1: Why Writes Are the Hard Problem

```
READS vs WRITES — FUNDAMENTAL ASYMMETRY

  READS:
  - Can be served from cache (Redis, CDN, in-process).
  - Can be served from replicas (scale out read replicas).
  - Can be retried safely (idempotent by nature).
  - Can tolerate slight staleness in many cases.

  WRITES:
  - Must hit durable storage (disk, WAL).
  - Must be serialised for consistency (within a partition).
  - Cannot be cached away — every write must be persisted.
  - Replicas INCREASE write cost (every write goes to N replicas).
  - Contention on indexes, locks, and I/O bandwidth.

  At 1M writes/sec with 500-byte records:
  - Raw throughput: 500 MB/s sustained.
  - With 3x replication: 1.5 GB/s across the cluster.
  - With indexes: additional random I/O per write.
  - With WAL: additional sequential I/O per write.

  Reads scale horizontally (add replicas).
  Writes scale by PARTITIONING (shard the data).
```

```
WRITE AMPLIFICATION

  Write amplification = total bytes written to disk / bytes of user data.

  Sources:
  1. WAL: every write is first written to the WAL (1x amplification).
  2. Memtable flush: data is written again when flushed to SSTable (2x).
  3. Compaction: SSTables are rewritten during merge (3-10x for LSM).
  4. Replication: data written to N replicas (Nx).
  5. Indexes: each index entry is an additional write.

  Example: 500 bytes of user data may cause 5-30 KB of total disk I/O.

  Write amplification = 10-60x in the worst case (LSM with many levels).

  This is THE key metric for write-heavy systems.
  Lower write amplification = higher sustainable throughput.
```

```
THE BOTTLENECK CHAIN

  For 1M writes/sec, you must eliminate EVERY bottleneck:

  1. Network ingestion: can the network absorb 500 MB/s?
     -> Multiple ingestion nodes, load balanced.
  2. Serialisation: can you serialise 1M records/sec?
     -> Binary format (Protobuf, Avro), not JSON.
  3. Disk I/O: can disks sustain the write bandwidth?
     -> NVMe SSDs (3-7 GB/s sequential write).
     -> Sequential writes only (no random I/O).
  4. CPU: can you process 1M records/sec?
     -> Batch processing, avoid per-record overhead.
  5. Memory: can you buffer enough data in memory?
     -> Large memtables, write buffers.
  6. Coordination: can you avoid locks and contention?
     -> Partition-level isolation, lock-free structures.
```

---

## 18.3 Step 2: Storage Engine Internals — LSM Trees vs B-Trees

```
B-TREE (traditional RDBMS: PostgreSQL, MySQL InnoDB)

  Structure:
  - Balanced tree of pages (4-16 KB each).
  - Data sorted by primary key.
  - Updates are IN-PLACE: find the page, modify it, write it back.

  Write path:
  1. Find the correct leaf page (O(log N) random reads).
  2. Modify the page in buffer pool (memory).
  3. Write WAL entry (sequential).
  4. Eventually flush dirty page to disk (random I/O).

  Write performance:
  - Each write = 1 random I/O (page write) + 1 sequential I/O (WAL).
  - Random I/O is the bottleneck.
  - On SSD: ~100K-200K random IOPS -> ~100K-200K writes/sec per node.
  - With indexes: each index adds another random I/O.
  - With secondary indexes: write amplification increases.

  Strengths:
  - Excellent read performance (data is sorted, in-place).
  - Predictable read latency.
  - Mature, battle-tested, ACID transactions.

  Weakness:
  - Random I/O limits write throughput.
  - NOT suitable for >100K writes/sec per node without extreme tuning.
```

```
LSM TREE (Log-Structured Merge Tree: Cassandra, ScyllaDB, RocksDB,
          LevelDB, HBase, ClickHouse)

  Structure:
  - Writes go to an in-memory buffer (MEMTABLE).
  - When memtable is full, it is flushed to disk as an immutable
    sorted file (SSTABLE / Sorted String Table).
  - Background COMPACTION merges SSTables into larger sorted files.

  Write path:
  1. Write to WAL (sequential append — fast).
  2. Insert into memtable (in-memory sorted structure — fast).
  3. When memtable is full (~64-256 MB):
     flush to disk as SSTable (sequential write — fast).
  4. Background compaction merges SSTables (sequential I/O).

  ALL writes are SEQUENTIAL. No random I/O on the write path.

  Write performance:
  - Sequential writes on NVMe SSD: 3-7 GB/s.
  - At 500 bytes/record: 6M-14M writes/sec per disk (theoretical max).
  - Practical: 500K-2M writes/sec per node (with compaction overhead).
  - 10-50x faster writes than B-tree for the same hardware.

  Weakness:
  - Read amplification: may need to check multiple SSTables.
  - Space amplification: duplicate data during compaction.
  - Compaction storms: background I/O can interfere with writes.
```

```
LSM vs B-TREE COMPARISON

  Dimension             | B-Tree                    | LSM Tree
  ----------------------|---------------------------|---------------------------
  Write I/O pattern     | Random                    | Sequential (append-only)
  Write throughput      | 100K-200K/sec per node    | 500K-2M/sec per node
  Write amplification   | 1-3x (low)                | 10-30x (high, compaction)
  Read latency          | Predictable (1-2 I/Os)    | Variable (check N SSTables)
  Space amplification   | ~1x (in-place)            | 1.1-2x (during compaction)
  Point reads           | Excellent                 | Good (with bloom filters)
  Range scans           | Excellent                 | Good (after compaction)
  Concurrency           | Locking (page-level)      | Lock-free writes (memtable)
  Best for              | Read-heavy, OLTP          | Write-heavy, time-series,
                        |                           | event ingestion, IoT

  For >1M writes/sec: LSM TREE is the only viable choice.
  B-trees cannot sustain this on commodity hardware.
```

---

## 18.4 Step 3: Write-Ahead Log (WAL) — The Durability Foundation

```
WAL MECHANICS

  Every durable write system uses a WAL:

  1. Client sends write.
  2. Server appends record to WAL (sequential write to disk).
  3. Server acknowledges write to client.
  4. Later, data is applied to the main data structure (memtable/page).

  The WAL guarantees:
  - If the server crashes after step 2, the write is recoverable.
  - On restart: replay the WAL to reconstruct in-memory state.
  - WAL is append-only -> sequential I/O -> FAST.

  WAL is the MINIMUM write cost. Every durable system pays it.
```

```
WAL OPTIMISATION FOR HIGH THROUGHPUT

  1. GROUP COMMIT (batching WAL writes):
     - Instead of fsync after every write, batch N writes
       and fsync once.
     - Trade-off: slight increase in durability window
       (lose last batch on crash) vs massive throughput gain.
     - PostgreSQL: commit_delay = 10ms (batch commits).
     - Kafka: linger.ms = 5 (batch producer sends).
     - Impact: 10-100x throughput improvement.

  2. WAL ON SEPARATE DISK:
     - Dedicate an NVMe SSD for WAL only.
     - No contention with data I/O.
     - Sequential write throughput: 3-7 GB/s on NVMe.

  3. WAL COMPRESSION:
     - Compress WAL entries (LZ4, Snappy).
     - Reduces I/O volume by 2-5x.
     - LZ4 compression: ~4 GB/s throughput (negligible CPU cost).

  4. ASYNC WAL (for non-critical data):
     - Do not fsync at all. Acknowledge immediately.
     - Data loss window: last few seconds on crash.
     - Acceptable for metrics, telemetry, analytics (not payments).
```

---

## 18.5 Step 4: Batching & Buffering — Amortising Disk I/O

```
THE BATCHING PRINCIPLE

  Per-record I/O cost:
  - 1 write at a time: 1 fsync per write. ~10K fsync/sec on SSD.
    -> 10K writes/sec. Far from 1M.

  - Batch of 1000 writes: 1 fsync per batch. Same 10K fsync/sec.
    -> 10M writes/sec (1000x improvement).

  Batching amortises the fixed cost of disk I/O across many records.
  This is THE most important optimisation for write throughput.
```

```
BATCHING AT EVERY LAYER

  Layer                  | Batching mechanism            | Typical batch
  -----------------------|-------------------------------|---------------
  Client / producer      | Kafka producer linger.ms      | 5-50 ms
                         | Buffer records, send in batch.| 1000-10000 records
  Ingestion service      | In-memory buffer, flush on    | 10K-100K records
                         | size or time threshold.       | or 1-5 seconds
  WAL                    | Group commit (batch fsync).   | 10-100 ms window
  Memtable               | Accumulate in memory.         | 64-256 MB
                         | Flush as one SSTable.         |
  Network                | TCP Nagle / batch RPC.        | MTU-sized batches
  Replication            | Batch replication log entries. | 100-1000 entries

  Every layer that batches reduces per-record overhead.
  End-to-end batching can reduce I/O by 100-1000x.
```

```
WRITE BUFFER SIZING

  Memtable / write buffer size affects:
  - Larger buffer -> fewer flushes -> less I/O -> higher throughput.
  - Larger buffer -> more data at risk on crash (before flush).
  - Larger buffer -> more memory consumption.
  - Larger buffer -> larger SSTables -> fewer files -> faster reads.

  Typical sizing:
  - RocksDB: write_buffer_size = 128-256 MB.
  - Cassandra: memtable_heap_space = 1/4 of heap.
  - ClickHouse: max_insert_block_size = 1M rows.
  - ScyllaDB: memtable total = 50% of shard memory.

  Rule: maximise buffer size within your memory budget.
  More memory for buffers = higher write throughput.
```

---

## 18.6 Step 5: Sharding for Write Scalability

```
WHY SHARDING IS MANDATORY

  Single-node write limit (LSM tree on NVMe SSD): ~1-2M writes/sec.
  Target: >1M writes/sec with 3x replication.
  Effective write load per node: 3M writes/sec (with replication).

  Single node cannot do this. You MUST shard.

  With 10 shards: each shard handles 100K writes/sec.
  With 3x replication: each shard = 300K writes/sec total.
  Well within single-node capacity.

  Sharding converts a single-writer problem into N independent
  single-writer problems. Each shard is an independent LSM tree.
```

```
SHARD KEY SELECTION

  The shard key determines which node receives each write.
  A bad shard key creates HOT SPOTS (one shard gets most writes).

  GOOD shard keys (for write throughput):
  - device_id (IoT): evenly distributed across devices.
  - user_id (events): evenly distributed across users.
  - hash(entity_id): guaranteed uniform distribution.
  - (timestamp, device_id): time-bucketed with device-level distribution.

  BAD shard keys:
  - timestamp alone: all writes go to the "current" shard.
    The latest time bucket is a HOT SPOT.
  - country_code: 50% of traffic may go to one country.
  - sequential ID: all writes go to the shard owning the latest range.

  For TIME-SERIES data:
  - Use COMPOSITE key: (metric_name, device_id, time_bucket).
  - Distributes writes across shards even within the same second.
  - Each shard handles a subset of devices for a time window.
```

```
CONSISTENT HASHING FOR WRITE DISTRIBUTION

  ring:  0 ─────── node A ─────── node B ─────── node C ─────── 0
              ▲              ▲              ▲
              │              │              │
         hash(key1)     hash(key2)     hash(key3)

  - Hash the shard key -> position on the ring.
  - Write goes to the next node clockwise.
  - Adding/removing a node moves only ~1/N of the data.

  Virtual nodes (vnodes):
  - Each physical node has 128-256 virtual positions on the ring.
  - Ensures even distribution even with few physical nodes.
  - Cassandra, ScyllaDB, DynamoDB use this approach.
```

---

## 18.7 Step 6: Kafka as a Write Buffer (Decouple Ingestion from Storage)

```
THE PATTERN

  Producers -> Kafka (write buffer) -> Consumers -> Database

  Why Kafka in front of the database?

  1. ABSORB BURSTS:
     - Producers write to Kafka at peak rate (1M/sec).
     - Kafka handles it easily (sequential append to log).
     - Consumers read from Kafka at a sustainable rate.
     - If consumers are slow, Kafka buffers the backlog.
     - Database never sees a traffic spike — only steady load.

  2. DECOUPLE INGESTION FROM STORAGE:
     - Producers do not wait for database writes.
     - Acknowledgement is fast (Kafka append = ~2ms).
     - Database can be slower (batch inserts, compaction).
     - If database is down, Kafka retains data (configurable retention).

  3. FAN-OUT:
     - One write to Kafka can feed multiple consumers:
       - Consumer 1: writes to time-series DB (ClickHouse).
       - Consumer 2: writes to data lake (S3 / Parquet).
       - Consumer 3: feeds real-time dashboards.
       - Consumer 4: triggers alerts on anomalous values.
```

```
KAFKA WRITE PERFORMANCE

  Kafka's write path is essentially a WAL:
  - Producer sends batch of records.
  - Broker appends to partition log (sequential write).
  - Broker replicates to follower brokers (sequential write).
  - Acknowledgement sent to producer.

  Performance:
  - Single partition: 50K-200K messages/sec (depending on message size).
  - Single broker (multiple partitions): 500K-1M messages/sec.
  - Cluster of 10 brokers: 5M-10M messages/sec.

  At 500 bytes/message, 10 brokers:
  - Throughput: ~5M messages/sec = 2.5 GB/sec.
  - Latency: 2-5ms (acks=1), 5-15ms (acks=all).

  Kafka is THE standard write buffer for high-throughput systems.
```

```
KAFKA PRODUCER TUNING FOR 1M WRITES/SEC

  Key settings:
  - batch.size = 64KB-1MB (larger batches = fewer network calls).
  - linger.ms = 5-50 (wait up to N ms to fill a batch).
  - compression.type = lz4 (2-5x compression, fast CPU).
  - buffer.memory = 128MB-512MB (client-side buffer).
  - acks = 1 (leader acknowledges, not all replicas).
    For durability-critical: acks = all + min.insync.replicas = 2.
  - max.in.flight.requests.per.connection = 5 (pipeline requests).

  Partitioning:
  - 100-500 partitions per topic for high throughput.
  - Partition by device_id or sensor_id (even distribution).
  - More partitions = more parallelism = higher throughput.
  - But too many partitions (>10K) increases metadata overhead.
```

---

## 18.8 Step 7: Append-Only & Immutable Architectures

```
APPEND-ONLY DESIGN

  Instead of UPDATE and DELETE, only APPEND new records.

  Traditional:  INSERT, UPDATE, DELETE -> random I/O, locking, contention.
  Append-only:  INSERT only           -> sequential I/O, lock-free, fast.

  Examples:
  - Event sourcing: store events, not state.
    Instead of: UPDATE account SET balance = 50
    Store:      INSERT event (account_id=1, type=DEBIT, amount=50)
  - Kafka topics: append-only logs.
  - Time-series: sensor readings are always new points, never updates.
  - Immutable data lakes: Parquet files on S3, never modified.

  Benefits:
  - Sequential writes only (maximum disk throughput).
  - No locking or contention (append to different partitions).
  - Natural audit trail (every event is preserved).
  - Replay-ability (rebuild state from events).

  Cost:
  - More storage (raw events + materialised views).
  - Read complexity (must materialise current state from events).
  - Compaction needed (merge/deduplicate over time).
```

```
IMMUTABLE DATA ARCHITECTURE

  +----------+     +---------+     +----------------+
  | Producers|---->| Kafka   |---->| Raw Data Lake  |
  |          |     | (events)|     | (S3/Parquet)   |
  +----------+     +---------+     +------+---------+
                        |                 |
                        v                 v
                  +-----------+    +------------+
                  | Real-Time |    | Batch ETL  |
                  | Consumer  |    | (Spark)    |
                  | (ClickHouse)   | -> Warehouse|
                  +-----------+    +------------+

  - Raw events are NEVER modified after write.
  - Real-time path: Kafka -> ClickHouse (last 7 days, fast queries).
  - Batch path: Kafka -> S3 -> Spark -> Data Warehouse (full history).
  - Corrections: append a correction event, never modify the original.

  This architecture naturally supports 1M+ writes/sec because:
  - All writes are appends (sequential).
  - No coordination between real-time and batch paths.
  - Each path scales independently.
```

---

## 18.9 Step 8: Time-Series & Event Ingestion at Scale

```
TIME-SERIES DATABASES

  Time-series data (IoT, metrics, logs) has unique properties:
  - Write-heavy: 99% writes, 1% reads.
  - Append-only: new points, rarely updates.
  - Time-ordered: queries are almost always by time range.
  - High cardinality: millions of unique series (device_id x metric).
  - Retention-based: old data is deleted after N days.

  Database         | Engine   | Write throughput        | Best for
  -----------------|----------|------------------------|-------------------
  ClickHouse       | MergeTree| 1M+ rows/sec per node  | Analytics + TSDB
  TimescaleDB      | B-tree   | 100K-300K rows/sec     | SQL + time-series
                   | (Postgres|                        | (smaller scale)
                   | based)   |                        |
  InfluxDB         | TSM (LSM)| 500K-1M points/sec     | Pure time-series
  QuestDB          | Append   | 1M+ rows/sec           | Fast ingestion,
                   | only     |                        | SQL interface
  ScyllaDB         | LSM      | 1M+ rows/sec           | Wide-column TSDB
  Victoria Metrics | LSM      | 1M+ samples/sec        | Prometheus-compatible
```

```
CLICKHOUSE — THE WRITE THROUGHPUT CHAMPION

  Why ClickHouse excels at high write throughput:

  1. COLUMNAR STORAGE:
     - Data stored by column, not by row.
     - Compression is 5-20x better (similar values together).
     - Less I/O per write (only write the columns you have).

  2. MERGE TREE ENGINE (LSM variant):
     - Writes go to in-memory buffer.
     - Flushed as sorted "parts" (like SSTables).
     - Background merges combine parts.
     - All I/O is sequential.

  3. BATCH INSERTS:
     - Optimal: insert 10K-1M rows per batch.
     - INSERT INTO events VALUES (...), (...), ...  -- 100K rows at once.
     - Avoid single-row inserts (per-insert overhead kills throughput).

  4. PARTITIONING BY TIME:
     - Partition by month/week/day.
     - Old partitions are dropped instantly (no row-by-row delete).
     - Retention is a metadata operation, not a scan.

  Performance at scale:
  - Single node: 1-2M rows/sec insert.
  - 10-node cluster: 10-20M rows/sec.
  - Compression: 10-20x (500 bytes -> 25-50 bytes on disk).
  - Query: aggregations on billions of rows in seconds (columnar scan).
```

```
INGESTION ARCHITECTURE FOR 1M WRITES/SEC

  50M devices -> Load Balancer -> Ingestion Layer (stateless)
                                       |
                                 +-----v------+
                                 | Kafka      |
                                 | (100 parts)|
                                 +-----+------+
                                       |
                        +--------------+---------------+
                        |              |               |
                  +-----v---+   +-----v----+   +------v-----+
                  |ClickHouse|  | S3/Parquet|  | Alert      |
                  | (7 days) |  | (90 days) |  | Engine     |
                  +----------+  +-----------+  +------------+

  Flow:
  1. Devices send readings via MQTT/HTTP to ingestion layer.
  2. Ingestion layer validates, enriches, batches (1000-10000 records).
  3. Batched records published to Kafka (100 partitions).
  4. ClickHouse consumer: batch inserts every 1 second (10K-100K rows).
  5. S3 consumer: writes Parquet files every 5 minutes.
  6. Alert engine: stream-processes for threshold violations.
```

---

## 18.10 Step 9: Compaction, Merge & Background Maintenance

```
WHY COMPACTION MATTERS

  LSM trees accumulate SSTables over time:
  - 100 flushes = 100 SSTables on disk.
  - A read must check multiple SSTables (read amplification).
  - Disk space grows (old versions of updated keys are still on disk).

  Compaction merges SSTables:
  - Combines multiple small SSTables into fewer large ones.
  - Removes deleted keys (tombstones) and old versions.
  - Maintains sorted order for efficient reads.

  Without compaction: reads degrade, disk fills up.
  With too much compaction: I/O bandwidth consumed, writes slow down.
```

```
COMPACTION STRATEGIES

  Strategy          | How it works                  | Write amp | Read amp
  ------------------|-------------------------------|-----------|----------
  Size-tiered       | Merge SSTables of similar     | Low       | High
  (STCS)            | size. Simple. Default in      | (10-20x)  | (check many
                    | Cassandra.                    |           | SSTables)
  Leveled           | SSTables organised in levels. | High      | Low
  (LCS)             | Each level is 10x larger.     | (20-30x)  | (1-2 SSTables
                    | Guarantees bounded read amp.  |           | per read)
  FIFO              | No compaction. Old SSTables   | 1x        | High
                    | are dropped by TTL.           | (none)    | (check all)
                    | Best for time-series with     |           |
                    | retention.                    |           |
  Time-window       | Compaction within time buckets.| Low      | Low (within
  (TWCS)            | Best for time-series data.    |           | time range)
                    | Old buckets are dropped whole.|           |

  For >1M writes/sec (time-series / IoT):
  - Use TWCS or FIFO: minimal compaction overhead.
  - Data is naturally time-ordered. Old buckets are dropped, not merged.
  - Write amplification stays near 1x (minimal rewriting).
```

```
COMPACTION TUNING

  Problem: compaction can consume 50-80% of disk I/O, starving writes.

  Mitigations:
  - Rate-limit compaction I/O: max_compaction_throughput_mb = 128 (RocksDB).
  - Dedicate CPU cores to compaction (separate thread pool).
  - Schedule heavy compaction during off-peak hours.
  - Use NVMe SSDs: enough I/O bandwidth for writes AND compaction.
  - Monitor: pending compaction bytes. Alert if growing faster than
    compaction can process (compaction debt).

  Rule: compaction must keep up with write rate.
  If compaction falls behind -> SSTables accumulate -> reads degrade
  -> eventually disk fills up -> system fails.
```

---

## 18.11 Step 10: Replication Under High Write Load

```
REPLICATION MODELS

  Model                | Write cost         | Durability           | Consistency
  ---------------------|--------------------|-----------------------|-------------
  Synchronous (acks=all)| Nx (N replicas)  | Highest (no data loss)| Strong
  Semi-synchronous     | 2x (leader +       | High (1 replica ack) | Eventual
  (acks=1 + async)     | 1 sync replica)    |                      |
  Asynchronous         | 1x (leader only,   | Lower (data loss on  | Eventual
  (acks=1)             | async replication)  | leader failure)      |

  For >1M writes/sec:
  - Synchronous replication (acks=all) is expensive:
    1M writes/sec * 3 replicas = 3M writes/sec total cluster I/O.
  - Semi-synchronous (acks=1 + 1 sync) is the common trade-off:
    Leader + 1 sync replica ack before responding.
    Third replica is async (best-effort).
  - For telemetry/metrics: async replication is often acceptable
    (losing last few seconds of sensor data is tolerable).
```

```
REPLICATION LAG UNDER HIGH WRITE LOAD

  Problem: at 1M writes/sec, replication lag can grow:
  - Network bandwidth between leader and follower may saturate.
  - Follower's disk I/O may not keep up.
  - Follower is also running compaction (competing for I/O).

  Mitigations:
  - Dedicated replication network (separate NIC for replication traffic).
  - Compress replication stream (LZ4: 2-5x reduction, fast).
  - Batch replication entries (ship 100-1000 entries per RPC).
  - Monitor replication lag (alert if > N seconds).
  - If lag > threshold: stop serving reads from that replica
    (stale reads can cause application errors).

  Kafka replication:
  - Follower brokers fetch from leader in batches.
  - replica.fetch.max.bytes = 10MB (large fetch batches).
  - num.replica.fetchers = 4 (parallel fetch threads).
  - Under-replicated partitions metric: must be 0 in steady state.
```

---

## 18.12 Step 11: Capacity Estimation for 1M Writes/Sec

```
SCENARIO: IoT Telemetry Platform

  Given:
  - 50M devices.
  - Peak: 1M writes/sec (not all devices report simultaneously).
  - Record size: 500 bytes (device_id, timestamp, 10 sensor values).
  - Retention: 90 days.
  - Replication factor: 3.
  - Compression ratio: 10x (columnar + LZ4).

  RAW THROUGHPUT:
  - 1M writes/sec * 500 bytes = 500 MB/sec raw ingestion.
  - With 3x replication: 1.5 GB/sec total cluster writes.

  DAILY VOLUME:
  - 1M writes/sec * 86,400 sec/day = 86.4 billion records/day.
  - 86.4B * 500 bytes = 43.2 TB/day (raw).
  - With 10x compression: 4.32 TB/day on disk.
  - With 3x replication: 12.96 TB/day total.

  90-DAY RETENTION:
  - 12.96 TB/day * 90 days = 1.17 PB total storage.

  CLUSTER SIZING (ClickHouse example):
  - Each node: 8 NVMe SSDs * 4 TB = 32 TB usable.
  - Nodes needed for storage: 1,170 TB / 32 TB = ~37 nodes.
  - Each node handles: 1M / 37 = ~27K writes/sec (well within capacity).
  - Add 50% buffer for compaction: ~55 nodes.
  - Add read capacity: ~60-70 nodes total.

  KAFKA CLUSTER:
  - 10-15 brokers (each handles ~100K writes/sec with replication).
  - 100-200 partitions (for parallelism).
  - Retention: 24-72 hours (buffer, not long-term).
  - Storage: 500 MB/sec * 86,400 sec * 3 days * 3 replicas / 10 compression
    = ~40 TB.

  INGESTION LAYER:
  - Stateless HTTP/MQTT receivers.
  - Each instance: ~50K writes/sec (parsing, validation, Kafka produce).
  - Instances needed: 1M / 50K = 20 instances.
  - With 2x headroom: 40 instances.
```

```
COST ESTIMATE (approximate, cloud)

  Component             | Count        | Monthly cost
  ----------------------|--------------|-------------------
  ClickHouse nodes      | 60 nodes     | $60K-$120K
  (i3en.3xlarge equiv.) | (NVMe SSD)   |
  Kafka brokers         | 15 brokers   | $15K-$25K
  (d3en.2xlarge equiv.) |              |
  Ingestion instances   | 40 instances | $8K-$15K
  (c6i.2xlarge equiv.)  |              |
  Network (inter-AZ)    | ~2 GB/sec    | $10K-$20K
  S3 (cold storage)     | ~1 PB        | $20K-$25K
  Total                 |              | $113K-$205K/month

  Optimisations:
  - Reserved instances: 30-50% savings.
  - Spot instances for ingestion layer: 60-70% savings.
  - Tiered storage: move data > 7 days to S3 (ClickHouse S3 engine).
  - Higher compression: Zstd instead of LZ4 for cold data (15-25x).
```

---

## 18.13 Step 12: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  Write speed vs read speed        | LSM trees optimise writes at the cost of
                                   | read amplification. Every SSTable may need
                                   | checking. Use Bloom filters (reduce reads to
                                   | 1-2 SSTables) and compaction to mitigate.
  Write speed vs durability        | Async WAL (no fsync) is fastest but risks
                                   | data loss. Group commit is the sweet spot:
                                   | batch fsyncs for 10-100x throughput with
                                   | bounded loss window (10-50 ms).
  Write amplification              | LSM compaction rewrites data 10-30x. This
                                   | consumes disk I/O and SSD endurance. Use
                                   | time-window compaction for time-series data
                                   | (near 1x amplification).
  Compaction storms                | When compaction falls behind, it suddenly
                                   | needs massive I/O to catch up. Writes slow
                                   | down or stall. Monitor compaction debt.
                                   | Rate-limit compaction during peak hours.
  Hot spots                        | Bad shard key sends all writes to one node.
                                   | Use hash-based partitioning or composite keys
                                   | (entity_id + time_bucket) for even distribution.
  Single-row inserts               | Inserting one row at a time wastes I/O on
                                   | per-insert overhead. Always batch (1K-100K
                                   | rows per insert). This is the #1 mistake.
  Kafka as the database            | Kafka is a LOG, not a database. It is a write
                                   | buffer and transport layer. Do not query
                                   | Kafka directly for analytical workloads.
                                   | Consume into a proper database.
  Over-replication                 | RF=3 triples write load. For non-critical
                                   | telemetry, RF=2 may be acceptable. For
                                   | financial data, RF=3 with acks=all is mandatory.
  Ignoring compression             | Without compression, 1M writes/sec at 500
                                   | bytes = 43 TB/day. With 10x compression =
                                   | 4.3 TB/day. Compression is not optional.
  Neglecting retention / TTL       | Without automatic data expiry, storage grows
                                   | unbounded. Use partition-based TTL (drop old
                                   | partitions) for instant, O(1) deletion.
  Network as bottleneck            | 1.5 GB/sec (with replication) requires 10 Gbps
                                   | network minimum. Use 25 Gbps NICs and
                                   | dedicated replication networks.
  Mixing OLTP and write-heavy      | Do not put high-write telemetry in the same
                                   | database as low-write OLTP transactions. They
                                   | have opposite optimisation profiles. Use
                                   | separate systems for each.
```

---

## 18.14 Problem 18 Summary

- **Writes are the hard problem:** reads scale with caches and replicas; writes must hit durable storage. At 1M writes/sec, every layer must be optimised: network, serialisation, disk I/O, CPU, memory, coordination.
- **LSM trees are the only viable storage engine** for >1M writes/sec. All writes are sequential (append to WAL, flush memtable as SSTable). B-trees are limited by random I/O (~200K writes/sec per node). LSM trees achieve 500K-2M writes/sec per node.
- **Write-Ahead Log (WAL):** the durability foundation. Group commit (batch fsyncs) is the single most impactful optimisation — 10-100x throughput improvement over per-write fsync.
- **Batching at every layer:** client batching (Kafka linger.ms), ingestion batching, WAL group commit, memtable accumulation. End-to-end batching reduces per-record I/O by 100-1000x. Single-row inserts are the #1 mistake.
- **Sharding is mandatory:** single-node limits are ~1-2M writes/sec. Shard by hash(entity_id) or composite key (entity_id + time_bucket). Never shard by timestamp alone (hot spot). Consistent hashing with virtual nodes for even distribution.
- **Kafka as write buffer:** decouple ingestion from storage. Kafka absorbs bursts (sequential log append = ~2ms), consumers write to database at sustainable rate. Fan-out to multiple consumers (TSDB, data lake, alert engine).
- **Append-only architectures:** INSERT only, never UPDATE. Sequential I/O, lock-free, natural audit trail. Pair with event sourcing or immutable data lake patterns.
- **Time-series databases:** ClickHouse (1M+ rows/sec per node, columnar, 10-20x compression), InfluxDB, QuestDB, ScyllaDB. Batch inserts (10K-1M rows per batch) are critical.
- **Compaction:** LSM trees require background compaction to merge SSTables. Time-window compaction (TWCS) is best for time-series: minimal write amplification, old buckets dropped whole. Monitor compaction debt — if it falls behind, the system degrades.
- **Replication:** semi-synchronous (acks=1 + 1 sync replica) balances durability and throughput. Async replication for non-critical telemetry. Monitor replication lag. Dedicate network bandwidth for replication traffic.
- **Capacity estimation:** 1M writes/sec at 500 bytes = 43 TB/day raw, ~4.3 TB/day compressed (10x), ~13 TB/day with RF=3. 90-day retention = ~1.2 PB. ~60-70 ClickHouse nodes + 15 Kafka brokers + 40 ingestion instances = $113K-$205K/month cloud cost.

---

# Problem 19: Design a Log Processing System (Like ELK / Splunk)

## 19.1 Problem Statement

> Your organisation runs 500+ microservices across three regions. Engineers currently SSH into individual machines to grep logs — this does not scale. You are asked to design a centralised log processing system that collects logs from every service, parses and enriches them, stores them for search and analytics, and supports real-time alerting on log patterns. The system must handle **10 TB of raw logs per day**, support full-text search with sub-second latency on recent data, retain logs for 90 days (hot) and 1 year (cold for compliance), and serve 200 engineering teams with access control. Walk through the end-to-end architecture.

This question tests:
- **Pipeline design** — building a reliable, high-throughput data pipeline from source to search.
- **Storage trade-offs** — understanding inverted indexes, columnar storage, label-based indexing, and when each is appropriate.
- **Search engine internals** — how full-text search works at scale (inverted index, tokenisation, relevance scoring).
- **Operational maturity** — retention policies, multi-tenancy, cost management, and the operational burden of running a log platform.

---

## 19.2 Step 1: Log Data Characteristics & Scale

```
LOG DATA PROPERTIES

  Logs are unlike most other data:

  1. WRITE-HEAVY, READ-RARELY:
     - 99% of logs are never read by a human.
     - They are only searched during incidents or audits.
     - But when you need them, you need them FAST.

  2. APPEND-ONLY:
     - Logs are never updated or deleted (until retention expiry).
     - Natural fit for sequential write / LSM-based systems.

  3. SEMI-STRUCTURED:
     - Mix of structured fields (timestamp, level, service, traceId)
       and unstructured text (message body, stack traces).

  4. TIME-ORDERED:
     - Almost all queries filter by time range first.

  5. HIGH CARDINALITY:
     - Unique values: traceId, requestId, userId, IP address.
     - Searchable but terrible as pre-aggregation index keys.

  6. BURSTY:
     - During incidents, log volume can spike 10-100x.
     - The system must absorb bursts without dropping logs.
```

```
SCALE ESTIMATION

  500 services * 200 instances = 100,000 instances.
  Average: ~15 log lines/sec per instance, 500 bytes each.
  Sustained: 100,000 * 15 * 500 bytes = ~750 MB/sec = ~65 TB/day raw.
  With sampling (10% INFO): ~10-15 TB/day actual ingested volume.

  For this design: 10 TB/day ingested, ~120 MB/sec sustained, 1.2 GB/sec peak.
```

---

## 19.3 Step 2: Collection — Getting Logs Off the Machines

```
COLLECTION ARCHITECTURE

  Apps (stdout) -> Container runtime (log files) -> Agent (DaemonSet)
       -> Kafka (transport buffer) -> Parsing fleet -> Elasticsearch / S3

  Key decisions:
  - Apps write to STDOUT (container best practice, 12-factor).
  - Agent runs as DaemonSet (one per node, not per container).
  - Agent tails log files, parses minimally, and ships to Kafka.
```

### Detailed Architecture Explanation

This architecture describes a modern, scalable log collection pipeline for containerized applications (Kubernetes).

#### Block Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        LOG COLLECTION PIPELINE                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────┐    ┌──────────────────┐    ┌──────────────────┐              │
│  │   Apps   │───▶│ Container Runtime│───▶│  Log Files on    │              │
│  │ (stdout) │    │  (Docker/containerd)  │     Host Disk     │              │
│  └──────────┘    └──────────────────┘    └──────────────────┘              │
│                                                │                             │
│                                                ▼                             │
│  ┌──────────────────────────────────────────────────────────┐              │
│  │              KUBERNETES CLUSTER                          │              │
│  │  ┌──────────────────────────────────────────────────┐    │              │
│  │  │  Agent (DaemonSet) - One per Node               │    │              │
│  │  │  ┌──────────────────────────────────────────┐  │    │              │
│  │  │  │ • Tails log files from container runtime │  │    │              │
│  │  │  │ • Minimal parsing (adds metadata)       │  │    │              │
│  │  │  │ • Ships to Kafka                         │  │    │              │
│  │  │  └──────────────────────────────────────────┘  │    │              │
│  │  └──────────────────────────────────────────────────┘    │              │
│  └──────────────────────────────────────────────────────────┘              │
│                                                │                             │
│                                                ▼                             │
│  ┌──────────────────────────────────────────────────────────┐              │
│  │              KAFKA (Transport Buffer)                     │              │
│  │  ┌──────────────────────────────────────────────────┐    │              │
│  │  │ • Decouples producers from consumers            │    │              │
│  │  │ • Absorbs bursts (10-100x spikes)               │    │              │
│  │  │ • Enables fan-out to multiple consumers         │    │              │
│  │  │ • Provides replay capability                    │    │              │
│  │  └──────────────────────────────────────────────────┘    │              │
│  └──────────────────────────────────────────────────────────┘              │
│                                                │                             │
│                                                ▼                             │
│  ┌──────────────────────────────────────────────────────────┐              │
│  │              PARSING FLEET                               │              │
│  │  ┌──────────────────────────────────────────────────┐    │              │
│  │  │ • Heavy parsing (grok patterns, field extraction)│    │              │
│  │  │ • Scales independently                           │    │              │
│  │  │ • Vector/Logstash for parsing                    │    │              │
│  │  └──────────────────────────────────────────────────┘    │              │
│  └──────────────────────────────────────────────────────────┘              │
│                                                │                             │
│                              ┌─────────────────┴─────────────────┐          │
│                              ▼                                   ▼          │
│  ┌─────────────────────────┐                    ┌─────────────────────────┐ │
│  │   ELASTICSEARCH         │                    │         S3              │ │
│  │   (Hot Data)            │                    │      (Cold Data)        │ │
│  │   • Real-time search    │                    │   • Long-term archive   │ │
│  │   • Interactive queries │                    │   • Cost-effective      │ │
│  │   • Recent logs (7-30d) │                    │   • All logs (90d+)     │ │
│  └─────────────────────────┘                    └─────────────────────────┘ │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### Data Flow Breakdown

**1. Apps (stdout)**
- Applications write logs to standard output (STDOUT) instead of log files
- Follows the **12-factor app methodology** principle #11: "Treat logs as event streams"
- STDOUT is the container best practice because it decouples apps from log storage concerns

**2. Container Runtime (log files)**
- The container runtime (Docker, containerd) automatically captures STDOUT and writes it to log files on the host machine
- Example: `/var/lib/docker/containers/<container_id>/<container_id>-json.log`
- Creates a standardized location where logs persist on disk

**3. Agent (DaemonSet)**
- A lightweight log collection agent runs as a **Kubernetes DaemonSet** (one agent per node, not per container)
- More efficient than sidecar containers (one per pod)
- The agent tails the log files from the container runtime
- Performs minimal parsing (adds metadata like pod name, namespace, labels)
- Ships logs to Kafka for transport

**4. Kafka (transport buffer)**
- Acts as a high-throughput message broker and buffer
- Decouples log producers (agents) from consumers (parsers)
- Provides durability and backpressure handling
- Can handle ingestion spikes without losing logs

**5. Parsing Fleet**
- Dedicated service(s) that perform heavy parsing (e.g., grok patterns, field extraction)
- Separates parsing from collection for better scalability
- Can scale independently based on parsing complexity

**6. Elasticsearch / S3**
- Elasticsearch: For searchable, real-time log analysis (hot data)
- S3: For cost-effective long-term archival (cold data)
- Enables tiered storage strategy

#### Why This Architecture Works

- **Scalability**: Each layer can scale independently
- **Reliability**: Kafka provides buffering and durability
- **Efficiency**: DaemonSet model reduces resource overhead (1 agent vs 100 sidecars per node)
- **Flexibility**: Can change parsing without touching agents
- **Observability**: Clear separation of concerns makes debugging easier

This design is commonly used in large-scale Kubernetes deployments handling hundreds of terabytes of logs daily.

```
LOG COLLECTION AGENTS

  Agent             | Language | Strengths                    | Weaknesses
  ------------------|----------|------------------------------|-------------------
  Fluent Bit        | C        | Ultra-lightweight (2 MB).    | Fewer plugins than
  (CNCF)            |          | K8s-native. Fast.            | Fluentd.
  Fluentd           | Ruby/C   | Rich plugin ecosystem.       | Higher memory (~100 MB).
  Vector            | Rust     | High performance. Unified    | Newer ecosystem.
                    |          | logs + metrics + traces.     |
  Filebeat          | Go       | Lightweight, Elastic-native. | Elastic-centric.
  Logstash          | JVM      | Powerful parsing (grok).     | Heavy (~500 MB RAM).
                    |          |                              | Not per-node agent.

  Recommendation: Fluent Bit as DaemonSet -> Kafka -> Vector/Logstash for parsing.
```

```
AGENT RELIABILITY

  1. BACKPRESSURE: disk buffer if Kafka is slow (5 GB limit).
  2. AT-LEAST-ONCE: track file offsets, resume on restart.
  3. LOG ROTATION: detect rotation, finish old file before new.
  4. MULTI-LINE: group Java stack traces via start-pattern regex.
```

---

## 19.4 Step 3: Transport — Kafka as the Central Nervous System

```
WHY KAFKA FOR LOG TRANSPORT

  1. DECOUPLE: agents write at their rate; indexers read at theirs.
  2. ABSORB BURSTS: 10-100x spike during incidents buffered in Kafka.
  3. FAN-OUT: one topic, multiple consumers (ES, S3, alerting, metrics).
  4. REPLAY: reindex from Kafka if indexer has a bug.
```

```
KAFKA TOPIC DESIGN

  Recommended: topic per environment (logs-prod, logs-staging, logs-dev).
  Partition by hash(service_name) for locality.
  Partition count: 200-500 per topic.

  Kafka sizing for 10 TB/day:
  - Ingestion: ~120 MB/sec. With RF=3: 360 MB/sec cluster writes.
  - Retention: 72 hours. Storage: ~31 TB (compressed).
  - Brokers: 5-10.
```

### Detailed Kafka Explanation

Kafka serves as the central nervous system of the log infrastructure, acting as a high-throughput, distributed commit log that decouples log producers (agents) from consumers (indexers, archivers, alerting systems).

#### How Kafka is Used in Log Pipeline

**1. Decoupling Producers and Consumers**
- Log agents (Fluent Bit DaemonSet) write to Kafka at their own pace
- Multiple consumers read independently: Elasticsearch indexers, S3 archivers, alerting systems, metrics aggregators
- If Elasticsearch goes down, agents continue writing to Kafka without backpressure
- When Elasticsearch recovers, it can catch up by reading from Kafka

**2. Burst Absorption**
- During incidents, log volume can spike 10-100x (e.g., 120 MB/sec → 12 GB/sec)
- Kafka buffers these spikes in memory and disk
- Consumers can process at their sustained rate while Kafka absorbs the excess
- 72-hour retention provides a large buffer window

**3. Fan-Out Pattern**
- Single Kafka topic serves multiple downstream systems
- Each consumer group reads independently with its own offset
- Example consumer groups:
  - `elasticsearch-indexer`: Reads all logs for indexing
  - `s3-archiver`: Reads all logs for long-term storage
  - `alerting-engine`: Reads all logs for real-time alerts
  - `metrics-aggregator`: Reads all logs for log-based metrics

**4. Replay Capability**
- If Elasticsearch indexing has a bug (e.g., incorrect field mapping), fix the indexer
- Reset consumer offset to reprocess logs from Kafka
- No need to regenerate logs from applications
- Critical for data integrity and debugging

#### Kafka Internals Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        KAFKA CLUSTER ARCHITECTURE                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │   PRODUCER  │    │   PRODUCER  │    │   PRODUCER  │    │   PRODUCER  │     │
│  │  (Agent #1) │    │  (Agent #2) │    │  (Agent #3) │    │  (Agent #N) │     │
│  │  DaemonSet  │    │  DaemonSet  │    │  DaemonSet  │    │  DaemonSet  │     │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘     │
│         │                  │                  │                  │             │
│         └──────────────────┴──────────────────┴──────────────────┘             │
│                            │                                                  │
│                            ▼                                                  │
│  ┌───────────────────────────────────────────────────────────────────────┐    │
│  │                     TOPIC: logs-prod                                   │    │
│  │                                                                       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │    │
│  │  │ Partition 0 │  │ Partition 1 │  │ Partition 2 │  │ Partition N │  │    │
│  │  │ (hash: A-D) │  │ (hash: E-H) │  │ (hash: I-L) │  │ (hash: ...) │  │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  │    │
│  │         │                  │                  │                  │      │    │
│  │         ▼                  ▼                  ▼                  ▼      │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │              SEGMENTS (Time-based log files)                    │   │    │
│  │  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │   │    │
│  │  │  │ Segment  │  │ Segment  │  │ Segment  │  │ Segment  │ ...    │   │    │
│  │  │  │   00001  │  │   00002  │  │   00003  │  │   00004  │        │   │    │
│  │  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │   │    │
│  │  │      │            │            │            │                   │   │    │
│  │  │      ▼            ▼            ▼            ▼                   │   │    │
│  │  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │   │    │
│  │  │  │   Index  │  │   Index  │  │   Index  │  │   Index  │        │   │    │
│  │  │  │  File    │  │  File    │  │  File    │  │  File    │        │   │    │
│  │  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                       │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │              OFFSET TRACKING (per consumer group)               │   │    │
│  │  │  Consumer Group: elasticsearch-indexer                          │   │    │
│  │  │  Partition 0: offset=1500234  Partition 1: offset=1498721      │   │    │
│  │  │  Partition 2: offset=1501100  Partition N: offset=1499987      │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  └───────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  ┌───────────────────────────────────────────────────────────────────────┐    │
│  │                      KAFKA BROKERS (5-10 nodes)                        │    │
│  │                                                                       │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐              │    │
│  │  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │  │ Broker N │              │    │
│  │  │ Leader:  │  │ Leader:  │  │ Leader:  │  │ Leader:  │              │    │
│  │  │ P0, P5   │  │ P1, P6   │  │ P2, P7   │  │ P...    │              │    │
│  │  │ Replica: │  │ Replica: │  │ Replica: │  │ Replica: │              │    │
│  │  │ All P    │  │ All P    │  │ All P    │  │ All P    │              │    │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘              │    │
│  │       │              │              │              │                │    │
│  │       └──────────────┴──────────────┴──────────────┘                │    │
│  │                      Replication (RF=3)                               │    │
│  └───────────────────────────────────────────────────────────────────────┘    │
│                            │                                                  │
│                            ▼                                                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │  CONSUMER   │    │  CONSUMER   │    │  CONSUMER   │    │  CONSUMER   │     │
│  │   GROUP A   │    │   GROUP B   │    │   GROUP C   │    │   GROUP D   │     │
│  │ (ES Indexer)│    │ (S3 Archiver)│    │ (Alerting)  │    │ (Metrics)    │     │
│  │ Offset: X    │    │ Offset: Y    │    │ Offset: Z    │    │ Offset: W    │     │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘     │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

#### Kafka Core Concepts

**Topics**
- Logical channel or feed for log streams
- One topic per environment: `logs-prod`, `logs-staging`, `logs-dev`
- Isolates environments for security and operational independence

**Partitions**
- Topics are split into partitions for parallelism
- 200-500 partitions per topic for this scale
- Partition by `hash(service_name)` ensures all logs from same service go to same partition
- Benefits:
  - Ordered logs per service within a partition
  - Parallel consumption (each partition consumed by one consumer in a group)
  - Horizontal scaling across brokers

**Segments**
- Each partition is split into time-based segment files (default 1 GB or 7 days)
- Segments are immutable - once written, never modified
- Old segments are deleted based on retention policy (72 hours)
- Enables efficient time-based queries and cleanup

**Offsets**
- Each message in a partition has a unique, monotonically increasing offset
- Consumers track their position per partition using offsets
- Consumer groups maintain independent offsets
- Enables replay by resetting offsets

**Replication**
- Replication Factor (RF=3) means each partition is stored on 3 brokers
- One broker is leader, others are followers
- Leader handles all reads/writes, followers replicate asynchronously
- Provides fault tolerance - if broker fails, another takes over

**Consumer Groups**
- Group of consumers that coordinate to consume a topic
- Each partition is consumed by exactly one consumer in a group
- Multiple groups can read the same topic independently
- Example: ES indexer group and S3 archiver group both read `logs-prod`

#### Kafka Sizing Justification

**Ingestion Rate**
- 120 MB/sec sustained, 1.2 GB/sec peak
- With RF=3: 360 MB/sec cluster writes sustained, 3.6 GB/sec peak
- Network bandwidth: 10 GbE network handles this easily

**Storage**
- 10 TB/day × 3 days retention = 30 TB raw
- With compression (snappy/lz4, ~2:1 ratio): ~15 TB
- With RF=3: ~45 TB total cluster storage
- 5-10 brokers with 8-12 TB disks each provides sufficient capacity

**Broker Count**
- 5-10 brokers for this workload
- More brokers = better parallelism and fault tolerance
- Fewer brokers = larger disks needed per broker
- 9 brokers (3 per AZ in 3 AZ region) is common production pattern

---

## 19.5 Step 4: Parsing, Enrichment & Transformation

```
PARSING PIPELINE

  Raw log -> Timestamp extraction -> Field extraction (JSON/grok)
         -> Enrichment (K8s metadata, team, geo-IP, deploy version)
         -> Type coercion -> Filtering (drop health checks, DEBUG)
         -> Sampling (10% INFO, 100% ERROR) -> PII redaction
         -> Output to parsed Kafka topic

  Parsing fleet: stateless workers reading from Kafka.
  - Vector: ~50-100 MB/sec per worker. 2-3 workers for 120 MB/sec.
  - Logstash: ~5-10 MB/sec per worker. 12-24 workers needed.
  - With 2x headroom: 5 Vector or 25 Logstash workers.
```

---

## 19.6 Step 5: Indexing — The Core of Search

```
INVERTED INDEX (Elasticsearch / Lucene)

  Doc 1: "Order 12345 created for user 67890"
  Doc 2: "Order 12345 payment failed"

  Inverted index:
    "order"   -> [doc1, doc2]
    "12345"   -> [doc1, doc2]
    "created" -> [doc1]
    "failed"  -> [doc2]

  Search "order failed": intersect [doc1,doc2] ∩ [doc2] = [doc2].
  O(1) per term lookup — searching 1B logs is as fast as 1000.
```

```
ELASTICSEARCH CLUSTER ARCHITECTURE

  - 3 dedicated master nodes (cluster state, shard placement).
  - Hot data nodes (10-20, SSD): recent data 0-7 days, active indexing.
  - Warm data nodes (10-15, HDD): 7-30 days, read-only.
  - Cold/frozen nodes (S3): 30-90 days, searchable snapshots.
  - Index pattern: logs-YYYY.MM.DD (one per day, easy retention/tiering).
```

```
ELASTICSEARCH vs LOKI vs CLICKHOUSE

  Dimension         | Elasticsearch       | Loki                | ClickHouse
  ------------------|---------------------|---------------------|-------------------
  Indexing          | Full inverted index | Labels only. Content| Columnar + sparse.
                    | on ALL fields.      | NOT indexed.        | No full-text index.
  Search speed      | Fast any field.     | Fast label filter.  | Fast structured SQL.
                    | Sub-second on 1B.   | Slow full-text grep.| Slower arbitrary text.
  Storage cost      | HIGH (3-4x raw).    | LOW (~0.5x raw).    | LOW (~0.1x raw).
  Ops complexity    | HIGH (JVM, shards). | LOW (stateless, S3).| MEDIUM (MergeTree).
  Best for          | Full-text search,   | Cost-sensitive,     | Structured log
                    | compliance.         | K8s-native, Grafana.| analytics, SQL.

  Recommendation: ES for full-text search; Loki for budget; ClickHouse for SQL analytics.
```

---

## 19.7 Step 6: Storage Architecture & Tiering

```
TIERED STORAGE

  Tier     | Duration  | Storage     | Cost/GB/mo | Speed
  ---------|-----------|-------------|------------|------------
  Hot      | 0-7 days  | SSD (ES)    | $0.20-0.40 | Sub-second
  Warm     | 7-30 days | HDD (ES)    | $0.05-0.10 | 1-5 sec
  Cold     | 30-90 days| S3 snapshot | $0.01-0.03 | 10-60 sec
  Archive  | 90d-1 yr  | S3 Glacier  | $0.004     | Hours
  Delete   | > 1 year  | —           | —          | —

  Index Lifecycle Management (ILM) automates all transitions.
```

```
INDEX DESIGN

  - Index-per-day: logs-prod-2024.01.15.
  - Target 20-50 GB per shard (ES sweet spot).
  - Mapping: "keyword" for exact-match (service, level, traceId),
    "text" for full-text (message), "flattened" for dynamic JSON.
  - Mapping explosion prevention: strict mappings, total_fields.limit = 2000.
  - Compression: LZ4 default, best_compression for warm/cold.
```

---

## 19.8 Step 7: Query & Search Engine

```
QUERY PATTERNS

  1. TIME-RANGE + KEYWORD (~70%): "ERROR logs from order-service, last 30 min."
  2. FULL-TEXT SEARCH (~20%): "NullPointerException across all services."
  3. AGGREGATION (~5%): "Count errors per service per hour, last 24h."
  4. TRACE CORRELATION (~5%): "All logs with traceId=abc123."
```

```
QUERY OPTIMISATION

  - Time range is primary filter (narrows to 1-N daily indices).
  - Index routing by service_name (search only relevant shards).
  - Filter (exact, cached) before query (full-text, scored).
  - Warn users when querying cold/archive tiers (slow).
  - Query timeout: 30 sec max. Circuit breaker: 70% heap limit.
```

---

## 19.9 Step 8: Alerting on Logs

```
LOG-BASED ALERTING

  Streaming alerts (real-time, Kafka consumer):
  - Pattern match on FATAL, OutOfMemoryError, data corruption.
  - Latency: seconds. Tools: custom consumer, Flink, ksqlDB.

  Scheduled alerts (periodic ES query):
  - "Count ERROR > 100 in 5 min for same service."
  - Latency: minutes. Tools: ElastAlert, ES Watcher, Grafana alerting.

  Anti-patterns:
  - Alerting on every ERROR -> fatigue.
  - No deduplication -> same alert fires 100x.
  - Prefer metric-based alerts where possible (faster, cheaper).
```

---

## 19.10 Step 9: Multi-Tenancy & Access Control

```
ACCESS CONTROL

  Approach 1: INDEX-PER-TEAM
    Pros: strong isolation, per-team retention.
    Cons: index proliferation (200 teams * 365 days = 73K indices).

  Approach 2: SHARED INDEX + DOCUMENT-LEVEL SECURITY (DLS)
    Pros: fewer indices. Cons: DLS query overhead, trust tagging.

  Approach 3: LOKI NATIVE MULTI-TENANCY (X-Scope-OrgID header)
    Pros: built-in, isolated storage. Cons: Loki-only.

  Recommendation: <20 teams: shared+DLS. 20-100: index-per-team. >100: Loki.

  Resource isolation:
  - Query timeout (30s), circuit breaker (70% heap), rate limiting.
  - Per-team ingestion quotas. Dedicated coordinator nodes.
```

---

## 19.11 Step 10: High Availability & Disaster Recovery

```
HA AT EVERY LAYER

  Component        | HA mechanism                    | RPO / RTO
  -----------------|---------------------------------|--------------------
  Agent (DaemonSet)| Per-node, disk buffer.          | RPO=0, RTO=0
  Kafka            | RF=3, min.insync.replicas=2.    | RPO=0, RTO<30s
  Parsing fleet    | Stateless, Kafka rebalance.     | RPO=0, RTO<1min
  Elasticsearch    | Multi-AZ, replica shards,       | RPO=0, RTO<5min
                   | 3 dedicated masters.            |
  S3 archive       | 11 nines durability.            | RPO=0, RTO=hours

  Daily ES snapshots to S3 (incremental). 30-day snapshot retention.
```

---

## 19.12 Step 11: Capacity Estimation for 10 TB/Day

```
COMPONENT SIZING

  Kafka: 5-10 brokers, ~31 TB storage (72h retention, RF=3, compressed).
  Parsing: 5 Vector workers (or 25 Logstash).
  ES hot (0-7d): 25 nodes (64 GB RAM, 8 TB SSD each). ~140 TB on disk.
  ES warm (7-30d): 15 nodes (64 GB RAM, 12 TB HDD each). ~160 TB.
  ES cold (30-90d): searchable snapshots on S3. ~100 TB.
  S3 archive (90d-1yr): S3 Glacier. ~450 TB.
```

```
COST ESTIMATE

  Component             | Count         | Monthly cost
  ----------------------|---------------|-------------------
  ES hot (SSD)          | 25 nodes      | $25K-$50K
  ES warm (HDD)         | 15 nodes      | $8K-$15K
  ES masters            | 3 nodes       | $1K-$2K
  Kafka brokers         | 10            | $8K-$15K
  Parsing fleet         | 5-10          | $2K-$5K
  S3 cold + archive     | ~550 TB       | $4K-$6K
  Total                 |               | $49K-$94K/month

  Loki alternative: $15K-$30K/month (3-5x cheaper, no full-text index).
```

---

## 19.13 Step 12: Trade-offs & Pitfalls

```
  Trade-off / pitfall              | Discussion
  ---------------------------------|---------------------------------------------
  ES vs Loki                       | ES: full-text, expensive. Loki: cheap, slow
                                   | grep. Choose based on search needs + budget.
  Index everything vs selective    | Full indexing: flexible, 3-4x storage. Selective:
                                   | cheap, but cannot search non-indexed fields.
  Mapping explosion                | Dynamic mappings + messy logs = thousands of
                                   | fields -> cluster instability. Use strict
                                   | mappings + flattened type.
  Hot retention too long           | 90 days on SSD = 10x cost vs tiered. Use ILM.
  No backpressure                  | 10-100x spike without Kafka buffer -> logs
                                   | dropped. Always buffer through Kafka.
  Parsing bottleneck               | Logstash is slow (JVM). Vector/Fluent Bit are
                                   | 10x faster. Size parsing fleet for peak.
  No structured logging            | Unstructured text requires grok regex parsing
                                   | (slow, fragile). Mandate JSON structured logs.
  PII in logs                      | User emails, IPs, tokens in logs -> compliance
                                   | risk. Redact at parsing stage before indexing.
  Single-tenant cluster            | 200 teams with no isolation -> one heavy query
                                   | starves everyone. Use DLS, quotas, rate limits.
  No log gap detection             | Service stops logging = silent failure. Alert
                                   | on "zero logs for service X in 5 minutes."
  Ignoring cost                    | Logs are the #1 infra cost at scale. Sample
                                   | INFO, drop DEBUG, tier aggressively, consider
                                   | Loki for 3-5x savings.
```

---

## 19.14 Problem 19 Summary

- **Log data** is write-heavy, read-rarely, append-only, semi-structured, time-ordered, high-cardinality, and bursty. 99% of logs are never read, but during incidents you need sub-second search.
- **Collection:** Fluent Bit DaemonSet (2 MB, lightweight) tails container stdout logs. At-least-once delivery with disk-backed buffers. Handles log rotation and multi-line grouping.
- **Transport:** Kafka decouples collection from indexing. Absorbs 10-100x burst spikes. Enables fan-out (ES, S3, alerting, metrics). Enables replay for reindexing. 5-10 brokers for 10 TB/day.
- **Parsing:** stateless fleet (Vector or Logstash) reads from Kafka, extracts fields (JSON/grok), enriches (K8s metadata, team, geo-IP), filters (drop health checks, DEBUG), samples (10% INFO, 100% ERROR), redacts PII.
- **Indexing:** Elasticsearch inverted index enables O(1) full-text search on billions of logs. Index-per-day pattern for easy retention and tiering. Strict mappings prevent mapping explosion.
- **Storage tiering:** hot (SSD, 0-7d, sub-second) → warm (HDD, 7-30d, 1-5s) → cold (S3 snapshot, 30-90d, 10-60s) → archive (Glacier, 90d-1yr, hours) → delete. ILM automates transitions.
- **Search:** 70% of queries are time-range + keyword filter (fast). Full-text search via inverted index. Route queries by service for shard-level targeting. Timeout and circuit breaker for resource protection.
- **Alerting:** streaming (Kafka consumer) for FATAL/security patterns (seconds). Scheduled (ES query) for aggregate thresholds (minutes). Prefer metric-based alerts where possible.
- **Multi-tenancy:** index-per-team or shared index + document-level security. Per-team quotas, query timeouts, rate limits. Loki has native multi-tenancy.
- **HA:** replicated at every layer (agent disk buffer, Kafka RF=3, stateless parsers, ES replica shards, S3 11-nines). No single point of failure.
- **Cost:** ES-based at 10 TB/day = $49K-$94K/month. Loki alternative = $15K-$30K/month (3-5x cheaper). Logs are the #1 infra cost — sample, filter, tier aggressively.

---

# Appendix A: A Reusable HLD Interview Framework

Use this checklist for *any* system design question to stay structured:

```
  1. CLARIFY      - Ask about scope, scale, read/write ratio, consistency,
                    latency, regions. State your assumptions.
  2. REQUIREMENTS - List functional + non-functional (scale, latency, HA).
  3. ESTIMATE     - Do the math: users -> RPS, storage, bandwidth, cache size.
                    This justifies every later decision.
  4. API          - Define the key endpoints (the contract).
  5. HIGH-LEVEL   - Draw the boxes: client -> CDN -> LB -> gateway -> services
     DIAGRAM        -> caches -> datastores -> async pipelines.
  6. DEEP-DIVE    - Pick the hard component(s) and detail them (data model,
                    index, partitioning).
  7. SCALE        - Explain how each layer scales (stateless, shard, replicate,
                    cache, CDN, auto-scale).
  8. BOTTLENECKS  - Identify the single points of failure and hotspots.
     & FAILURE      Apply resilience patterns + redundancy.
  9. TRADE-OFFS   - State what you gave up (consistency, cost, complexity)
                    and why it is acceptable.
```

**Golden rules:** Always start with requirements and math, never jump to a solution. **Caching and read-replicas** solve most read-heavy problems. **Statelessness** is what makes horizontal scaling possible. And always call out where you would **avoid over-engineering** for a smaller scale.

---

# Appendix B: Numbers Every Engineer Should Know

Handy latency/throughput intuition for back-of-the-envelope estimates:

```
  Operation                          | Approx. latency
  -----------------------------------|------------------
  L1 cache reference                 | ~1 ns
  Main memory (RAM) reference        | ~100 ns
  Redis GET (same datacenter)        | ~0.5-1 ms
  SSD random read                    | ~100-150 us
  Search engine query (indexed)      | ~10-100 ms
  Network round trip within a region | ~0.5-1 ms
  Network round trip cross-continent | ~50-150 ms
  Reading 1 MB sequentially from SSD | ~1 ms

  Rules of thumb:
   - Memory is ~100,000x faster than a cross-continent network hop ->
     CACHE and serve from the EDGE.
   - 1 server handles ~ a few thousand simple RPS -> 150k RPS needs
     many instances + caching.
   - A day has 86,400 seconds -> divide daily volume by ~86,400 for
     average RPS (then multiply for peak).
```

---

## Closing

This workbook solves nineteen system design problems the way you should in an interview: **clarify, estimate, diagram, justify, and trade off.** The same framework (Appendix A) and number sense (Appendix B) apply to any HLD question -- design a URL shortener, a rate limiter, a news feed, a chat system. Master the framework once and every problem becomes a structured conversation rather than a guessing game.
