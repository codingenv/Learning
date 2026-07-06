# Chapter 42 — Building AI/ML-Based Services

## 1. Problem Statement

Design backend systems that serve AI/ML models at **production scale** — handling
millions of users, sub-second latency, and infrastructure costs that can bankrupt
a startup overnight if mismanaged.

```
+-----------------------------------------------------------------------+
|                    AI/ML Services Landscape                           |
+-----------------------------------------------------------------------+
|  +-------------------+  +-------------------+  +-------------------+  |
|  | RECOMMENDATION    |  | GENERATIVE AI     |  | RAG PIPELINES     |  |
|  | ENGINES           |  | SERVICES          |  |                   |  |
|  | Netflix, YouTube, |  | ChatGPT, Claude,  |  | Enterprise know-  |  |
|  | Amazon, Spotify   |  | Copilot, Midj.    |  | ledge assistants  |  |
|  | "You might like"  |  | "Generate text/   |  | "Answer from your |  |
|  | "Because you      |  |  images/code"     |  |  private docs"    |  |
|  |  watched..."      |  |                   |  |                   |  |
|  +-------------------+  +-------------------+  +-------------------+  |
+-----------------------------------------------------------------------+
```

**Core Engineering Challenges:**

| Challenge              | Why It's Hard                                          |
|------------------------|--------------------------------------------------------|
| Model Serving Latency  | LLMs need seconds per response; GPUs are bottleneck    |
| Feature Freshness      | Stale features = stale recommendations                 |
| Scaling Inference      | GPUs are scarce, expensive, and hard to auto-scale     |
| Cost Management        | H100 GPU ~$3/hr; 1M users/day can exceed $100K/month  |
| Model Versioning       | Rolling back a bad model must be instant                |
| Data Pipeline Latency  | Training data must flow from events to model in hours   |

---

## 2. Part A: Recommendation System Design

### 2.1 Problem: Design a Recommendation Engine

> Design a system like Netflix's "You Might Also Like" or Amazon's
> "Customers Who Bought X Also Bought Y."

**Requirements:**
- Serve personalized recommendations to **200M+ users**
- Catalog of **millions of items** (movies, products, songs)
- Recommendations must update as user behavior changes (watched a new movie)
- Latency: **< 200ms** for recommendation API response
- Support multiple surfaces: homepage, detail page, search results, email digests

### 2.2 Recommendation Approaches

#### Collaborative Filtering

The foundational idea: **people who agreed in the past will agree in the future.**

```
USER-ITEM INTERACTION MATRIX
============================

              Movie A   Movie B   Movie C   Movie D   Movie E
  User 1     [  5  ]   [  3  ]   [  ?  ]   [  1  ]   [  ?  ]
  User 2     [  4  ]   [  ?  ]   [  4  ]   [  1  ]   [  ?  ]
  User 3     [  ?  ]   [  3  ]   [  5  ]   [  ?  ]   [  4  ]
  User 4     [  1  ]   [  ?  ]   [  ?  ]   [  5  ]   [  4  ]
  User 5     [  ?  ]   [  4  ]   [  4  ]   [  ?  ]   [  ?  ]

  [?] = missing rating we want to PREDICT
  Goal: fill in the blanks with predicted scores
```

**User-User Collaborative Filtering:**
- Find users similar to you (cosine similarity on rating vectors)
- Recommend items those similar users liked but you haven't seen
- Problem: doesn't scale to 200M users (pairwise comparisons explode)

**Item-Item Collaborative Filtering (Amazon's approach):**
- Pre-compute item-item similarity from co-occurrence in user histories
- "Users who bought X also bought Y" — directly from the matrix
- More stable than user-user (items change less than user tastes)

**Matrix Factorization (SVD, ALS):**
- Decompose the sparse user-item matrix into two low-rank matrices
- User matrix (200M x 128) and Item matrix (128 x 10M)
- Each user and item gets a **128-dimensional embedding**
- Prediction: dot product of user embedding and item embedding

```
MATRIX FACTORIZATION
====================

  User-Item Matrix  ≈  User Embeddings   x   Item Embeddings^T
   (200M x 10M)        (200M x 128)          (128 x 10M)

  [ 5  3  ?  1 ]     [ 0.2  0.8  ... ]     [ 0.3  0.1  ... ]^T
  [ 4  ?  4  1 ]  ≈  [ 0.3  0.7  ... ]  x  [ 0.7  0.4  ... ]
  [ ?  3  5  ? ]     [ 0.8  0.1  ... ]     [ 0.1  0.9  ... ]
  [ 1  ?  ?  5 ]     [ 0.9  0.2  ... ]     [ 0.5  0.3  ... ]

  Rating(user, item) ≈ dot(user_embedding, item_embedding)
```

#### Content-Based Filtering

Recommend items whose **features** are similar to items the user already liked.

```
CONTENT-BASED: ITEM FEATURE VECTORS
====================================
  "Inception":    [action=0.8, sci-fi=0.9, thriller=0.7, nolan=1.0]
  "Interstellar": [action=0.3, sci-fi=1.0, thriller=0.4, nolan=1.0]

  cosine_similarity = 0.82 --> recommend "Interstellar" to "Inception" fans
```

**Pros:** No cold-start for new items (features known from metadata).
**Cons:** Limited serendipity — never recommends outside the user's bubble.

#### Hybrid Approach (Netflix's Real Architecture)

Netflix uses an **ensemble** of dozens of models, blended by context:

```
NETFLIX HYBRID RECOMMENDATION ENSEMBLE
=======================================

  +-----------------------+
  | Collaborative Filter  |---> Score: 0.82
  +-----------------------+
  +-----------------------+
  | Content-Based Filter  |---> Score: 0.75
  +-----------------------+                    +-----------+    +--------+
  +-----------------------+                    | Ensemble  |--->| Final  |
  | Deep Neural Network   |---> Score: 0.91 ->| Blender   |    | Score: |
  +-----------------------+                    | (weighted |    | 0.87   |
  +-----------------------+                    |  average) |    +--------+
  | Trending / Popular    |---> Score: 0.60    +-----------+
  +-----------------------+
  +-----------------------+
  | Context-Aware (time,  |---> Score: 0.70
  | device, location)     |
  +-----------------------+
```

#### Deep Learning: Two-Tower Model

The modern workhorse for large-scale recommendations (YouTube, Instagram).

```
TWO-TOWER RETRIEVAL MODEL
==========================
  User Features              Item Features
  +-------------+            +-------------+
  | user_id     |            | item_id     |
  | age, gender |            | genre       |
  | watch_hist  |            | director    |
  | click_hist  |            | description |
  +------+------+            +------+------+
         |                          |
   +-----v------+            +-----v------+
   | User Tower  |            | Item Tower  |
   | (DNN 3-5L)  |            | (DNN 3-5L)  |
   +-----+------+            +------+------+
         |                          |
   +-----v------+            +-----v------+
   | User Embed  |            | Item Embed  |
   | (128-dim)   |            | (128-dim)   |
   +-----+------+            +------+------+
         +--------+    +-----------+
                  |    |
               +--v----v--+
               | Dot Prod/ |
               | Cosine Sim|
               +-----+-----+
                     |
                Relevance Score

  Training: maximize sim(user, clicked_item),
            minimize sim(user, random_item)
```

**Why Two Towers?**
- Item embeddings are **pre-computed** and stored in a vector index
- At query time, only the **user tower** runs (fast — milliseconds)
- Then do ANN search over pre-computed item embeddings

### 2.3 System Architecture for Recommendations

The industry-standard **two-phase architecture**:

```
RECOMMENDATION SYSTEM — FULL ARCHITECTURE
==========================================
  User opens Netflix homepage
         |
         v
  +------+-------+
  | API Gateway   |  (rate limiting, auth, routing)
  +------+-------+
         |
  +------v--------------+    +-------------------+
  | Feature Store        |<---| User Profile DB   |
  | (Online: Redis)      |<---| Click Stream      |
  | Get user features:   |<---| Watch History     |
  |  - recent watches    |    +-------------------+
  |  - preferred genres  |
  +------+--------------+
         |  user_embedding + context features
         v
  +------+------------------+
  | PHASE 1: CANDIDATE      |   Retrieve ~1,000 from 10M+
  | GENERATION               |
  |  ANN Index (FAISS/ScaNN) |<--- Pre-computed Item Embeddings
  |  + Trending + New items  |
  +---------+----------------+
            |  ~1,000 candidate items
            v
  +---------+----------------+
  | PHASE 2: RANKING         |   Score each candidate
  |  Features: user (50+),   |   using rich features
  |  item (100+), cross,     |
  |  context (device, time)  |
  |  Model: Deep ranker      |
  |  (Triton / TF Serving)   |
  +---------+----------------+
            |  Scored & sorted
            v
  +---------+----------------+
  | PHASE 3: RE-RANKING      |   Business logic
  |  - Diversity filter       |   - Freshness boost
  |  - Boost originals        |   - Remove already seen
  +---------+----------------+
            |
            v
  +---------+----------------+
  | RESPONSE: Top 20 items   |
  +---------------------------+

  Latency budget: < 200ms
    Phase 1 (ANN search):   ~20ms    Phase 2 (Rank 1K): ~80ms
    Phase 3 (Re-rank):      ~10ms    Feature fetch:     ~90ms
```

### 2.4 Feature Store

ML models need **features** from dozens of sources. A Feature Store provides a
single interface to fetch features for both training and serving.

```
FEATURE STORE ARCHITECTURE
===========================
  DATA SOURCES                      FEATURE STORE
  +---------------+
  | Clickstream   |--+          +-------------------+
  | (Kafka)       |  |  ingest  | OFFLINE STORE     |
  +---------------+  +-------->| (S3 / Hive)       |
  | User Profile  |--+         | - Historical data  |
  | (PostgreSQL)  |  |         | - Model TRAINING   |
  +---------------+  |         +-------------------+
  | Item Catalog  |--+
  | (DynamoDB)    |  |         +-------------------+
  +---------------+  +-------->| ONLINE STORE      |
  | Interaction   |   stream   | (Redis / DynamoDB)|
  | Events        |----------->| - Latest values    |
  | (Kafka)       |            | - SERVING (<5ms)   |
  +---------------+            +-------------------+
```

**Feature Freshness Spectrum:**

| Feature Type           | Update Frequency  | Example                    |
|------------------------|-------------------|----------------------------|
| Static                 | Rarely            | User age, item genre       |
| Batch                  | Daily / Hourly    | User avg rating, item pop  |
| Near-real-time         | Minutes           | Recent clicks, cart items  |
| Real-time              | Seconds           | Current session context    |

**Tools:** Feast (open source), Tecton (managed), AWS SageMaker Feature Store,
Google Vertex AI Feature Store.

### 2.5 Model Training Pipeline

```
ML TRAINING PIPELINE
=====================
  +------------+     +------------+     +--------------+
  | Raw Events |---->| Feature    |---->| Training     |
  | (S3/Kafka) |     | Engineering|     | Dataset      |
  +------------+     | (Spark)    |     | (Parquet/S3) |
                     +------------+     +------+-------+
                                               |
                     +-------------------------v-------+
                     | Model Training (PyTorch/TF)      |
                     | GPU cluster (8x A100)             |
                     | Experiment tracking: MLflow / W&B |
                     +----------------+----------------+
                                      |
                     +----------------v----------------+
                     | Offline Evaluation               |
                     | NDCG@10, MAP, Recall@K           |
                     +----------------+----------------+
                                      |
                            +---------v---------+
                            | Model Registry     |
                            | (MLflow / W&B)     |
                            +---------+----------+
                                      |
                  +-------------------v-------------------+
                  | DEPLOYMENT: Canary (1% -> 10% -> 100%)|
                  | Rollback: instant switch to previous   |
                  +---------------------------------------+
```

---

## 3. Part B: Generative AI Service Design

### 3.1 Problem: Design a ChatGPT-like Service

> Design a system that serves a large language model to millions of
> concurrent users with streaming responses and conversation memory.

**Requirements:**
- Serve models with 70B-400B+ parameters
- Support context windows of **100K+ tokens**
- Streaming responses (token by token, first token < 500ms)
- Handle **millions of concurrent conversations**
- Conversation history persistence (multi-turn)
- Cost target: < $0.01 per average conversation

**Why This Is Hard:**
- A 70B parameter model in FP16 needs **140 GB** of GPU memory
- A single H100 has 80 GB — you need **tensor parallelism** across 2+ GPUs
- Each user's request occupies GPU memory for the **entire generation** (seconds)
- GPU utilization is key: idle GPUs burn cash ($3/hr per H100)

### 3.2 Architecture for LLM Serving

```
GENERATIVE AI SERVICE — FULL ARCHITECTURE
==========================================
  +----------+
  | Client   |  (Web, Mobile, API)
  +----+-----+
       |  SSE for streaming
       v
  +----+----------+
  | API Gateway    |  Auth, rate limiting (tokens/min), validation
  +----+----------+
       |
  +----v----------+
  | Request Router |  Route by model, priority (paid/free), queue mgmt
  +----+----------+
       |
       +----+----+----+----+
       v    v    v    v    v
  +----+----+----+----+----+----+
  |   GPU INFERENCE CLUSTER      |
  |  +--------+   +--------+    |
  |  | Node 1 |   | Node 2 |    |  Serving: vLLM /
  |  | 8xH100 |   | 8xH100 |   |  TensorRT-LLM /
  |  +--------+   +--------+    |  Text Gen Inference
  |  +--------+   +--------+    |
  |  | Node 3 |   | Node 4 |    |
  |  | 4xH100 |   | 4xH100 |   |
  |  +--------+   +--------+    |
  +----+------------------------+
       |  Streaming tokens (SSE)
       v
  +----+----------+     +------------------+
  | Response       |     | Usage / Billing  |
  | Streamer +     |     | (Kafka -> DB)    |
  | Safety filter  |     +------------------+
  +----+----------+
       v
  +----+--------------+
  | Conversation Store |
  | Redis (TTL:24h) +  |
  | DynamoDB (perm)    |
  +--------------------+
```

### 3.3 Key Optimizations for LLM Serving

#### KV Cache

During autoregressive generation, the model computes **key-value attention
states** for every token. Without caching, generating token N recomputes
attention for all N-1 previous tokens — O(N^2) work.

```
KV CACHE — AVOID RECOMPUTATION
================================
  WITHOUT KV Cache:                      WITH KV Cache:
    Token 1: compute [tok_1]               Token 1: compute K1,V1 -> CACHE
    Token 2: compute [tok_1, tok_2]        Token 2: load K1V1, compute K2V2
    Token N: compute ALL N tokens          Token N: load cached, compute KN
    --> O(N^2) total work!                 --> O(N) total work!

  Memory cost: KV cache for 70B model, 100K context ≈ 30-40 GB per request
```

#### Continuous Batching (vLLM)

```
CONTINUOUS BATCHING
====================
  Static batching:
    [Req A(50tok), Req B(200tok), Req C(30tok)] -> wait for B. GPU idle!

  Continuous batching (vLLM):
    Step 1: [A, B, C] -> Step 5: A done, [D joins, B, C]
    Step 8: C done, [D, B, E joins] -> GPU NEVER idle
    Result: 2-3x higher throughput on same hardware.
```

#### PagedAttention (vLLM)

Manages KV cache like OS virtual memory — allocate **pages** on demand:

```
PAGEDATTENTION
===============
  Traditional: pre-allocate max_seq_len per request
    Req A: [====100K tokens allocated====]  (uses 2K -> 98% wasted!)

  PagedAttention: allocate pages on demand
    Req A: [page1][page2]                   (2K tokens, 2 pages)
    Req B: [page1][page2][page3][p4][p5]    (5K tokens, 5 pages)
    Result: serve 2-4x more concurrent requests on same GPU memory.
```

#### Full Optimization Comparison

| Technique             | Speedup    | Memory Saving | Quality Impact   |
|-----------------------|------------|---------------|------------------|
| KV Cache              | 5-10x      | -30-40 GB/req | None             |
| Continuous Batching   | 2-3x thru  | Minimal       | None             |
| PagedAttention        | 2-4x conc. | 60-80%        | None             |
| FP16 Quantization     | 1.5-2x     | 50%           | Negligible       |
| INT8 Quantization     | 2-3x       | 75%           | Minor (<1% loss) |
| INT4 Quantization     | 3-4x       | 87.5%         | Noticeable (2-5%)|
| Speculative Decoding  | 2-3x       | +small model  | None (verified)  |
| Prompt Caching        | 1.5-3x     | Varies        | None             |
| Tensor Parallelism    | Near linear| Split across  | None             |

#### Speculative Decoding

Use a **small, fast draft model** to generate N candidate tokens, then the
**large model verifies** them all in a single forward pass:

```
SPECULATIVE DECODING
=====================
  Without: Large model generates token by token (6 forward passes)
    "The" -> "cat" -> "sat" -> "on" -> "the" -> "mat"

  With: Draft model (7B) generates 6 tokens fast, large model (70B)
        verifies ALL 6 in ONE forward pass:
    Draft:  "The cat sat on the mat" (fast)
    Verify: All 6 ACCEPTED -> 6 tokens in ~1 pass (vs 6 passes)
    If token 4 rejected: keep 1-3, resample from 4. Still saved 3 passes!
```

#### Streaming with Server-Sent Events

```
SSE STREAMING — TOKEN BY TOKEN DELIVERY
=========================================
  Client                          Server
    |  POST /chat/completions       |
    |  {"messages":[...], "stream":true}
    |------------------------------>|
    |                               | [GPU starts generating]
    |  data: {"token": "The"}       |  ~300ms (time to first token)
    |<------------------------------|
    |  data: {"token": " capital"}  |  ~50ms (per token)
    |<------------------------------|
    |  ...                          |
    |  data: {"token": " Paris."}   |
    |<------------------------------|
    |  data: [DONE]                 |
    |<------------------------------|
  User sees text appear word by word — feels fast even if total takes seconds.
```

### 3.4 Scaling GPU Infrastructure

```
GPU SELECTION GUIDE
====================
  TRAINING:      H100 SXM (80GB, $3/hr) | A100 SXM (80GB, $2/hr) | H200 (141GB, $4.50/hr)
  INFERENCE:     H100 (big models) | A100 (7B-30B) | L4 (24GB, budget) | T4 (16GB, $0.35/hr)
  DEV/TEST:      T4 (cheapest GPU) | CPU inference (small models only)
```

**Auto-scaling:**

```
GPU AUTO-SCALING
================
  +------------------+     +-----------+     +-----------+
  | Request Queue    |---->| Scaling   |---->| GPU Pool  |
  | (depth monitor)  |     | Controller|     | Manager   |
  +------------------+     +-----------+     +-----------+

  Scale UP:  queue > 50 reqs OR GPU util > 80% for 5min
  Scale DOWN: queue < 5 for 10min OR GPU util < 30% for 15min
  Caveat: GPU instances take 3-5 min to start -> keep warm pool
```

**Cost Comparison: Cloud vs On-Premise**

| Factor            | Cloud (AWS/GCP/Azure) | On-Premise             |
|-------------------|-----------------------|------------------------|
| Upfront cost      | $0                    | $30K-$40K per H100     |
| Hourly cost       | $3/hr per H100        | ~$0.50/hr (amortized)  |
| Break-even        | -                     | ~12-18 months          |
| Scaling           | Minutes               | Weeks (order hardware) |
| Maintenance       | Provider handles      | Your team handles      |
| Best for          | Variable load, startup| Steady load, large co. |

### 3.5 Conversation Management

```
CONVERSATION MANAGEMENT
========================
  +--------------------+
  | Client sends msg   |
  +---------+----------+
            |
  +---------v----------+
  | Load history from  |  (Redis, keyed by session_id)
  | conversation store |
  +---------+----------+
            |
  +---------v--------------------------+
  | Assemble full prompt:              |
  | [SYSTEM] You are a helpful asst.   |  <- always included
  | [USER]   Hi, help me with geo.     |  <- turn 1
  | [ASST]   Sure! Ask me anything.    |  <- turn 1
  | [USER]   What's the capital of FR? |  <- current turn
  |                                    |
  | If tokens > context_limit:         |
  |   truncate oldest turns            |
  +------------------------------------+
            |
  +---------v----------+
  | LLM (stream) ->    |
  | Save to Redis(24h) |
  | + DynamoDB (perm)   |
  +--------------------+
```

**Token Counting:** Count tokens BEFORE sending (use tiktoken). Reserve tokens
for response (e.g., 4096). Truncate oldest turns until history fits.

### 3.6 Safety and Guardrails

```
SAFETY PIPELINE
================
  User Message
       |
  +----v-----------+
  | INPUT FILTER    |  Block prompt injection, harmful requests,
  | (classifier)    |  PII detection, language detection
  +----+-----------+
       |
       | PASS or BLOCK ("I can't help with that.")
       |
  +----v-----------+
  | LLM Generation  |
  +----+-----------+
       |
  +----v-----------+
  | OUTPUT FILTER   |  Block harmful content, check for PII,
  | (classifier)    |  verify relevance, content policy check
  +----+-----------+
       |
       v
  Response to User

  Additional: rate limiting (10 req/min free), token limits (100K/day),
  abuse detection, human review queue, audit logging
```

---

## 4. Part C: RAG (Retrieval Augmented Generation) Pipeline

### 4.1 Problem: Design an Enterprise Knowledge Assistant

> Your company has 10 million+ internal documents (Confluence, Google Docs,
> Slack threads, PDF manuals, code repos). Employees want to ask questions
> like "What's our vacation policy?" or "How do I configure the VPN?" and
> get accurate answers WITH citations.

**Why not just use an LLM?**
- LLMs don't know your private data (training cutoff, no access)
- LLMs hallucinate (confidently make up policies that don't exist)
- You need **grounded answers** with traceable sources

**Solution: RAG — Retrieve relevant documents, then Generate an answer.**

### 4.2 How RAG Works

```
RAG — RETRIEVAL AUGMENTED GENERATION
======================================
  OFFLINE — Index your documents:
  +----------+     +----------+     +----------+     +----------+
  | Documents|---->| Chunking |---->| Embedding|---->| Vector   |
  | (PDFs,   |     | (500-tok |     | Model    |     | Database |
  |  Docs)   |     |  chunks) |     | (text->  |     | (vectors |
  +----------+     +----------+     |  vector) |     |  + text) |
                                    +----------+     +----------+

  ONLINE — Answer user questions:
  +---------+     +----------+     +----------+     +----------+
  | User    |---->| Embed    |---->| Vector   |---->| Top-K    |
  | Question|     | Query    |     | Search   |     | Chunks   |
  +---------+     +----------+     +----------+     +----+-----+
                                                         |
  +------------------------------------------------------+
  |
  +----v-------------------------------------------+
  | PROMPT TO LLM:                                 |
  | System: Answer ONLY from context. Cite sources.|
  | Context:                                       |
  |   [1] "Employees get 20 days PTO..." (p.3)    |
  |   [2] "PTO requests: 2 weeks advance" (hr.md) |
  |   [3] "Unused PTO carries over 5 days" (p.5)  |
  | Question: What's our vacation policy?          |
  +----+-------------------------------------------+
       |
  +----v-------------------------------------------+
  | ANSWER: "Employees receive 20 days PTO [1].    |
  |  Requests need 2 weeks notice [2]. Up to 5     |
  |  days carry over [3]."                          |
  +------------------------------------------------+
```

### 4.3 Chunking Strategies

How you split documents into chunks **dramatically** affects retrieval quality.

```
CHUNKING STRATEGIES
====================
  FIXED-SIZE (simplest):
  [=== chunk 1 (500 tok) ===]
                     [=== chunk 2 (500 tok) ===]    <-- 50 token overlap
                                        [=== chunk 3 (500 tok) ===]

  SEMANTIC (smarter): split on section/paragraph boundaries
  [=== "Vacation Policy" section ===][=== "Sick Leave" section ===]

  PARENT-CHILD (best retrieval):
  +--- Parent chunk (2000 tokens — full section) ---------------+
  |  [Child 1 (500t)]  [Child 2 (500t)]  [Child 3 (500t)]      |
  |  Search on children (specific), return parent (more context)|
  +-------------------------------------------------------------+
```

**Chunking Strategy Comparison:**

| Strategy          | Retrieval Quality | Implementation | Best For               |
|-------------------|-------------------|----------------|------------------------|
| Fixed-size        | Moderate          | Trivial        | Getting started, mixed |
| Semantic          | Good              | Medium         | Well-structured docs   |
| Recursive         | Good              | Medium         | Variable-length docs   |
| Parent-child      | Excellent         | Complex        | Production systems     |
| Sentence-window   | Very Good         | Medium         | Precise retrieval      |

### 4.4 Embedding Models

The embedding model converts text into vectors. **Quality directly impacts
retrieval accuracy.**

| Model                        | Dimensions | MTEB Score | Cost               |
|------------------------------|------------|------------|--------------------|
| OpenAI text-embedding-3-lg   | 3072       | 64.6       | $0.13/1M tokens    |
| OpenAI text-embedding-3-sm   | 1536       | 62.3       | $0.02/1M tokens    |
| Cohere embed-v3              | 1024       | 64.5       | $0.10/1M tokens    |
| BGE-large-en-v1.5 (OSS)     | 1024       | 64.2       | Free (self-host)   |
| E5-mistral-7b-instruct (OSS)| 4096       | 66.6       | Free (needs GPU)   |
| GTE-large-en-v1.5 (OSS)     | 1024       | 65.4       | Free (self-host)   |

**Key Considerations:**
- **Fine-tuning** your embedding model on domain data improves retrieval 10-20%
- **Matryoshka embeddings**: train at 3072 dims, truncate to 256 at query time
  for faster search with minor quality loss
- **Asymmetric models**: separate query vs. document encoding (queries are short,
  documents are long)

### 4.5 Vector Databases

```
VECTOR DATABASE COMPARISON
===========================

  +-------------------------------------------------------------------+
  | Database    | Type      | Scale      | Hybrid | Filtering | Cost  |
  |-------------|-----------|------------|--------|-----------|-------|
  | Pinecone    | Managed   | Billions   | Yes    | Excellent | $$$   |
  | Weaviate    | OSS/Cloud | Billions   | Yes    | Good      | $$    |
  | Milvus      | OSS/Cloud | Billions   | Yes    | Good      | $$    |
  | Qdrant      | OSS/Cloud | Billions   | Yes    | Excellent | $$    |
  | pgvector    | Extension | Millions   | No*    | SQL-based | $     |
  | ChromaDB    | OSS       | Thousands  | No     | Basic     | Free  |
  +-------------------------------------------------------------------+
  * pgvector + pg_trgm can approximate hybrid search
```

**When to Use What:**

```
  Start here:
    "How many vectors do you have?"
           |
    +------+------+
    |             |
  < 1M          > 1M
    |             |
  pgvector      "Do you need managed service?"
  (simple,       |
   cheap)   +----+----+
            |         |
           Yes        No
            |         |
         Pinecone   Milvus or
         (easiest)  Qdrant
                    (self-host)
```

### 4.6 Advanced RAG Techniques

#### Hybrid Search

Combine **vector search** (semantic) with **keyword search** (BM25) for better
recall:

```
HYBRID SEARCH
==============

  Query: "How to configure VPN on macOS?"

  Vector Search (semantic):                Keyword Search (BM25):
  1. "Setting up VPN client" (0.89)       1. "VPN configuration macOS" (12.3)
  2. "Network proxy settings" (0.84)      2. "macOS VPN setup guide" (11.8)
  3. "Remote access guide" (0.81)         3. "Configure VPN on Mac" (11.2)

  Hybrid (Reciprocal Rank Fusion):
  1. "VPN configuration macOS"     <-- keyword hit + semantic relevance
  2. "Setting up VPN client"       <-- strong semantic match
  3. "macOS VPN setup guide"       <-- keyword hit
  4. "Configure VPN on Mac"        <-- keyword hit
  5. "Remote access guide"         <-- semantic relevance

  Vector search catches MEANING. BM25 catches EXACT TERMS.
  Together: best of both worlds.
```

#### Re-ranking with Cross-Encoders

```
RE-RANKING PIPELINE
====================

  User Query: "What is our vacation policy?"
       |
  +----v---------+
  | Bi-Encoder   |    Fast but approximate ranking
  | (embedding   |    Score = cosine(query_vec, chunk_vec)
  |  similarity) |    Retrieve top 20 chunks
  +----+---------+
       |
       | Top 20 chunks
       v
  +----+---------+
  | Cross-Encoder|    Slow but precise ranking
  | (Reranker)   |    Score = model(query + chunk) — sees both together
  |              |    Re-score all 20, return top 5
  | Models:      |
  |  Cohere      |
  |  Rerank v3   |
  |  bge-reranker|
  +----+---------+
       |
       | Top 5 chunks (much better quality)
       v
  Send to LLM for answer generation
```

#### Query Expansion

```
QUERY EXPANSION — LLM REWRITES QUERY FOR BETTER RETRIEVAL
===========================================================

  Original query: "PTO"
  Problem: too vague, embedding might miss relevant docs

  LLM expansion:
    Query 1: "paid time off policy"
    Query 2: "vacation days and leave policy"
    Query 3: "PTO accrual and carryover rules"

  Search all 3 expanded queries -> merge and deduplicate results
  -> much higher recall than searching just "PTO"
```

#### Agentic RAG

```
AGENTIC RAG — LLM DECIDES WHEN AND WHAT TO RETRIEVE
=====================================================
  User: "Compare our US and UK vacation policies"
       |
  Agent (LLM with tools):
    +---> search("US vacation policy")      -> [chunk1, chunk2]
    +---> search("UK vacation policy")      -> [chunk3, chunk4]
    +---> search("policy differences")      -> [chunk5]
    +---> Synthesize all into comparison table

  Key: LLM DECIDES what to search (not a fixed pipeline).
  Multi-hop: search -> read -> decide to search more.
```

### 4.7 RAG Evaluation

```
RAG EVALUATION FRAMEWORK
=========================
  RETRIEVAL METRICS (finding right docs?):
    Recall@K, MRR, nDCG@K

  GENERATION METRICS (good answers?):
    Faithfulness (matches sources?), Relevance, Completeness

  END-TO-END:
    Answer Accuracy, Citation Accuracy, User Satisfaction (thumbs up/down)
```

**RAGAS Framework** (automated): LLM evaluates faithfulness/relevance, creates
synthetic test datasets, tracks quality over time.

**Human Evaluation:** Sample 100 queries/week, label correct/partial/incorrect,
track trends to catch degradation early.

### 4.8 Complete RAG Architecture

```
PRODUCTION RAG SYSTEM — COMPLETE ARCHITECTURE
===============================================
  DOCUMENT INGESTION (offline / batch)
  +--------+   +----------+   +-----------+   +----------+   +--------+
  | Sources|-->| Doc      |-->| Chunking  |-->| Embedding|-->| Vector |
  | S3,    |   | Loader   |   | Semantic/ |   | OpenAI / |   | DB     |
  | Confl, |   | Parse,   |   | Parent-   |   | BGE /    |   | + meta |
  | GDocs, |   | OCR      |   | child     |   | Cohere   |   | data   |
  | Slack  |   +----------+   +-----------+   +----------+   +--------+
  +--------+
  Trigger: new doc uploaded, daily re-index, webhook

  QUERY PIPELINE (online / real-time)
  +---------+
  | User    |  "What's our vacation policy?"
  +----+----+
       |
  +----v---------+
  | Query        |  LLM rewrites for better retrieval
  | Expansion    |
  +----+---------+
       |
  +----v---------+     +----------------+
  | Hybrid       |<--->| Vector DB +    |
  | Retrieval    |     | BM25 Index     |
  | (RRF merge)  |     +----------------+
  | Top 20 chunks|
  +----+---------+
       |
  +----v---------+
  | Cross-Encoder|  Re-rank 20 -> top 5
  | Reranker     |
  +----+---------+
       |
  +----v---------+
  | LLM          |  System prompt + top 5 chunks + question
  | (stream SSE) |  -> Answer with citations
  +----+---------+
       |
  +----v---------+
  | Feedback     |  thumbs up/down -> improve retrieval
  +--------------+

  LATENCY: expansion ~200ms, retrieval ~100ms, rerank ~150ms,
           LLM first token ~400ms. Total: ~2-4s (streaming)
```

---

## 5. ML Infrastructure Patterns (Common Across All Three)

### 5.1 Model Registry

```
MODEL REGISTRY WORKFLOW
========================
  +------------+     +-----------+     +------------+     +-----------+
  | Experiment |---->| Register  |---->| Staging    |---->| Production|
  | (training) |     | v1.2.3    |     | (validate) |     | (serve)   |
  +------------+     +-----------+     +------------+     +-----------+
                                            |                  |
                                       Auto tests:        Monitoring:
                                       accuracy,          latency,
                                       latency,           accuracy,
                                       bias checks        drift

  Metadata: training data hash, hyperparams, metrics, model size, deps
```

**Tools:** MLflow, Weights & Biases, SageMaker Model Registry, Vertex AI.

### 5.2 Feature Store

Online (Redis, <5ms) + Offline (S3/Hive, batch training) with sync between them.
See Section 2.4 for detailed architecture. **Tools:** Feast, Tecton, SageMaker.

### 5.3 Model Serving

| Framework       | Best For          | Key Features                    |
|-----------------|-------------------|---------------------------------|
| TF Serving      | TensorFlow models | gRPC, batching, A/B, multi-model|
| Triton (NVIDIA) | Multi-framework   | Dynamic batching, GPU sharing   |
| vLLM            | LLM serving       | PagedAttention, continuous batch|
| TGI (HuggingFace)| LLM serving     | Easy setup, streaming, quant    |
| TorchServe      | PyTorch models    | Multi-model, scalable           |
| ONNX Runtime    | Cross-platform    | CPU optimized, model conversion |

**Batch vs Real-time:** Real-time for API serving (<100ms). Batch for daily
recommendations, scoring millions of items from S3 to S3.

### 5.4 A/B Testing for ML

```
A/B TESTING FOR ML MODELS
===========================
  +---------+     +------------+     +-----------+
  | Traffic |---->| Router     |---->| Model A   |  (Champion: 90%)
  | 100%    |     | (feature   |     | v2.1      |
  |         |     |  flags)    |     +-----------+
  +---------+     |            |---->+-----------+
                  +------------+     | Model B   |  (Challenger: 10%)
                                     | v2.2      |
                                     +-----------+

  Day 1:  1% canary (detect crashes)
  Day 3:  10% A/B (measure CTR, watch time, revenue)
  Day 7:  Analyze: CTR +2.3%, watch time +1.1% (p<0.05)
  Day 10: 50% (confirm at scale)
  Day 14: 100% (new champion)
```

### 5.5 Monitoring ML in Production

```
ML MONITORING — FOUR PILLARS
==============================
  +-------------------+     +-------------------+
  | 1. DATA DRIFT     |     | 2. MODEL PERF     |
  | Feature stats     |     | Online metrics    |
  | (mean, stddev),   |     | (CTR, conversion),|
  | missing values,   |     | prediction dist,  |
  | new categories    |     | error rates       |
  | Alert: KL-diverg  |     | Alert: metric     |
  |  > threshold      |     |  drop > 5% / 1hr  |
  +-------------------+     +-------------------+

  +-------------------+     +-------------------+
  | 3. INFRA HEALTH   |     | 4. FEATURE        |
  | Latency (p50/95/  |     |    FRESHNESS      |
  | 99), throughput,  |     | Last update time, |
  | GPU util, memory  |     | pipeline lag,     |
  | Alert: p99 > 500ms|     | batch job status  |
  | or GPU OOM        |     | Alert: > 2h stale |
  +-------------------+     +-------------------+

  Stack: Prometheus+Grafana, Evidently AI, W&B, PagerDuty
```

---

## 6. Architecture Comparison

```
THREE SYSTEMS COMPARED
=======================
  +------------------+------------------+------------------+
  |  RECOMMENDATIONS |  GENERATIVE AI   |  RAG PIPELINE    |
  +------------------+------------------+------------------+
  | Input: user ID   | Input: text      | Input: text      |
  | Output: items    | Output: text     | Output: text +   |
  |                  |  (streaming)     |  citations       |
  | Latency: <200ms  | Latency: 1-30s  | Latency: 2-5s   |
  | Compute: CPU +   | Compute: GPU    | Compute: GPU +   |
  |  light GPU       |  heavy          |  vector DB       |
  | Scale: 200M+    | Scale: 1M+      | Scale: 10M+     |
  |  users           |  concurrent     |  documents       |
  | Key DB: Feature  | Key DB: KV      | Key DB: Vector   |
  |  Store + ANN     |  Cache (GPU)    |  Database        |
  | Cost: compute    | Cost: GPU hours | Cost: GPU +      |
  |  at scale        |  ($3/hr/H100)   |  embedding cost  |
  +------------------+------------------+------------------+
```

---

## 7. Key Takeaways

1. **Two-phase retrieval + ranking is the backbone of recommendation systems.**
   Candidate generation (fast, approximate) narrows millions to thousands;
   ranking (slow, precise) selects the final results. This pattern applies
   beyond recommendations — search, ads, and even RAG use it.

2. **Feature Stores bridge ML training and serving.** The #1 cause of
   training-serving skew (model works in dev, fails in prod) is computing
   features differently in training vs. serving. A unified Feature Store
   eliminates this class of bugs.

3. **LLM serving is fundamentally a memory management problem.** KV cache,
   PagedAttention, and continuous batching are not optional optimizations —
   they are requirements for serving LLMs at any reasonable cost. Without them,
   you'll burn 3-4x the GPU budget.

4. **Quantization is the single biggest cost lever for LLM inference.** Going
   from FP16 to INT4 can reduce GPU costs by 75% with only 2-5% quality loss.
   Always benchmark quantization on YOUR specific use case before deciding.

5. **RAG is not "just vector search + LLM."** Production RAG requires hybrid
   search, re-ranking, query expansion, smart chunking, and continuous
   evaluation. Each component measurably impacts answer quality.

6. **Chunking strategy matters more than embedding model choice.** Bad chunking
   with a great embedding model will underperform good chunking with a decent
   model. Invest time in chunking strategy — especially parent-child chunking
   for complex documents.

7. **A/B testing for ML is non-negotiable.** Offline metrics (NDCG, accuracy)
   are necessary but not sufficient. You MUST validate in production with real
   users. Many models that look great offline fail when deployed.

8. **Monitor data drift, not just model accuracy.** By the time accuracy drops,
   the damage is done. Monitoring input distributions catches problems BEFORE
   they affect users — giving you time to retrain or roll back.

9. **GPU auto-scaling is harder than CPU auto-scaling.** GPU instances take 3-5
   minutes to start, models take minutes to load. Maintain warm pools, use
   predictive scaling (anticipate traffic patterns), and consider spot instances
   for non-latency-sensitive workloads.

10. **Cost management is an architectural concern, not an afterthought.** A
    naive LLM deployment can cost $100K+/month. Prompt caching, request
    batching, model quantization, tiered model routing (send simple queries to
    small models, complex to large), and aggressive caching can reduce costs by
    5-10x without measurable quality loss.

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
