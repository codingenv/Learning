# Chapter 34: Design a Typeahead / Autocomplete System

## 1. Problem Statement

Design a typeahead (autocomplete) system that provides real-time search suggestions as
users type. As a user types each character, the system should return the top 5-10 most
relevant suggestions within 100 milliseconds. The system must handle billions of queries
per day, support multiple languages, incorporate trending and personalized results, and
filter inappropriate content.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Prefix Suggestions | As user types a prefix, show top 5-10 matching suggestions |
| FR2 | Ranking | Suggestions ranked by popularity (search frequency) |
| FR3 | Trending Boost | Recently trending queries ranked higher |
| FR4 | Personalization | Boost suggestions based on user's search history |
| FR5 | Multi-language | Support autocomplete in multiple languages and scripts |
| FR6 | Spell Correction | Suggest corrections for common misspellings |
| FR7 | Content Filtering | Filter out inappropriate, offensive, or dangerous suggestions |
| FR8 | Real-time Update | New trending queries appear within minutes |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Latency | Response in < 100ms (ideally < 50ms) |
| NFR2 | Scale | 10B search queries/day, 5B typeahead requests/day |
| NFR3 | Availability | 99.99% uptime |
| NFR4 | Freshness | Trending queries reflected within 15 minutes |
| NFR5 | Throughput | 60K typeahead requests/sec |
| NFR6 | Global | Low latency for users worldwide |

---

## 4. Capacity Estimation

### 4.1 Traffic

```
Search queries: 10B/day
  Average query: user types 4 characters → 4 typeahead requests per query
  But many users type fast, debouncing reduces to ~2 requests/query
  
Typeahead requests: 10B * 2 = 20B/day
  = 20,000,000,000 / 86,400 ~ 230,000 requests/sec
  Peak: ~600,000 requests/sec

BUT: Most requests are for popular prefixes that are heavily cached.
  Cache hit rate: 80-90%
  Backend requests: ~30,000/sec
```

### 4.2 Storage

```
Unique search queries:
  ~5 billion unique queries (with duplicates removed)
  After filtering (minimum 10 searches): ~500M meaningful queries
  
  Average query length: 15 characters (UTF-8, avg 1.5 bytes/char)
  Each query entry: 30 bytes (query) + 8 bytes (count) + 8 bytes (metadata) = ~50 bytes
  
  500M * 50 bytes = 25 GB (for the query database)

Trie storage:
  Much larger due to node overhead
  Estimated: 50-100 GB (fits in memory of a few servers)
  
  With top-K precomputed at each node:
  Each node stores top 10 suggestions * 50 bytes = 500 bytes/node
  Estimated nodes: 200M (from 500M queries)
  200M * 500 bytes = 100 GB additional
  
  Total trie size: ~150-200 GB (across sharded trie servers)
```

---

## 5. Data Collection: Query Aggregation

### 5.1 Collecting Search Queries

```
Every search query is a data point for autocomplete:

  User searches "how to cook pasta"
  → This query's frequency count increases by 1

  But we can't update the trie on every single query (too expensive).
  Instead, aggregate in batches:

  Raw Query Stream (real-time):
    "how to cook pasta"     → Kafka topic "search-queries"
    "how to tie a tie"      → Kafka topic "search-queries"
    "best restaurants NYC"  → Kafka topic "search-queries"
    ...

  Aggregation Pipeline:
    +------------+     +------------------+     +------------------+
    | Kafka      | --> | Stream Processor | --> | Aggregated       |
    | (raw       |     | (Flink / Spark   |     | Query Counts     |
    |  queries)  |     |  Streaming)      |     | (per time window)|
    +------------+     +------------------+     +------------------+

  Time-windowed aggregation:
    5-minute window:  count per query in last 5 minutes (for trending)
    1-hour window:    hourly roll-up
    1-day window:     daily roll-up (for overall popularity)
    
  Result:
    +-------------------------+----------+-------------+
    | query                   | count_5m | count_daily |
    +-------------------------+----------+-------------+
    | "how to cook pasta"     | 342      | 45,230      |
    | "how to tie a tie"      | 128      | 28,100      |
    | "best restaurants NYC"  | 890      | 12,450      |
    +-------------------------+----------+-------------+
```

### 5.2 Query Normalization

```
Before aggregating, normalize queries:
  1. Lowercase: "How To Cook Pasta" → "how to cook pasta"
  2. Trim whitespace: "  pasta recipes  " → "pasta recipes"
  3. Remove special characters: "pasta!!! recipes???" → "pasta recipes"
  4. Unicode normalization: "cafe\u0301" → "cafe" (NFC form)
  5. Optional: spelling correction for aggregation
     "pasat recipies" → "pasta recipes" (aggregate with correct spelling)
```

---

## 6. Trie Data Structure

### 6.1 How a Trie Works

```
A Trie (prefix tree) is an n-ary tree where each node represents a character.
Path from root to any node spells out a prefix.

Example: Insert "tree", "try", "true", "trend"

                    (root)
                      |
                      t
                      |
                      r
                    / | \
                   e  u  y
                  / \  |
                 e   n e
                     |
                     d

  Searching prefix "tr":
    1. Start at root
    2. Follow 't' → found
    3. Follow 'r' → found
    4. At node 'r', we have children: 'e', 'u', 'y'
    5. Collect all words in subtrees: tree, trend, true, try
    6. Return top-K by frequency
```

### 6.2 Trie Node Structure

```
Basic trie node:
  {
    children: Map<char, TrieNode>,   // child nodes
    is_end: boolean,                 // marks end of a complete query
    frequency: int,                  // how often this query was searched
    query: string                    // full query (only at leaf/end nodes)
  }

Example node for "tre" in path root→t→r→e:
  {
    children: {'e': <node>, 'n': <node>},
    is_end: false,          // "tre" itself is not a valid suggestion
    frequency: 0,
    query: null
  }

Node for "tree" in path root→t→r→e→e:
  {
    children: {},
    is_end: true,
    frequency: 45230,       // searched 45,230 times
    query: "tree"
  }
```

### 6.3 Finding Top-K Suggestions (Naive)

```
Naive approach:
  1. Navigate to the prefix node (e.g., "tr")
  2. DFS/BFS traverse the entire subtree
  3. Collect all end nodes with their frequencies
  4. Sort by frequency, return top 10

  Time complexity: O(P + N) where P = prefix length, N = nodes in subtree
  
  Problem: If prefix is short (e.g., "t"), subtree could have MILLIONS of nodes!
  Way too slow for 100ms latency requirement.
```

---

## 7. Optimization: Precompute Top-K at Each Node

### 7.1 The Key Optimization

```
Precompute and cache the top 10 suggestions at EVERY trie node:

Node at "tr":
  {
    children: {'e': ..., 'u': ..., 'y': ...},
    top_suggestions: [
      ("tree removal", 89000),
      ("travel deals", 78000),
      ("trending now", 67000),
      ("trump news", 55000),
      ("true crime", 42000),
      ("truck rental", 38000),
      ("treasure hunt", 25000),
      ("trial", 20000),
      ("treadmill", 18000),
      ("trivago", 15000)
    ]
  }

Now, finding suggestions for prefix "tr":
  1. Navigate to node "tr": O(2) — just follow 2 characters
  2. Return top_suggestions: O(1) — already precomputed!
  3. Total: O(P) where P = prefix length
  
  For prefix "tre":
    Navigate to node "tre": O(3)
    Return its precomputed top_suggestions: O(1)
    
  BLAZING FAST — perfect for <100ms requirement
```

### 7.2 Building the Trie with Top-K

```
Build process (bottom-up):

  1. Insert all queries with frequencies into trie
  2. For each leaf node (end of query): 
     top_suggestions = [(query, frequency)]
  3. For each internal node (bottom-up):
     Merge top_suggestions from all children
     Keep only top 10 by frequency
  
  This is done OFFLINE during the trie build phase, not at query time.

Space overhead:
  Each node stores 10 suggestions * ~50 bytes = 500 bytes extra
  For 200M nodes: 100 GB additional
  But: only the top few million nodes are frequently accessed (cache-friendly)
```

### 7.3 Trie Depth Limiting

```
Optimization: Limit trie depth to 20 characters
  - Very few queries exceed 20 characters
  - Reduces trie size significantly
  - For longer queries: truncate and match on first 20 chars

Additional optimization: Prune nodes with very low frequency
  - If a node's best suggestion has frequency < threshold (e.g., 10)
  - Don't store that branch (too obscure to suggest)
  - Reduces trie from 200M nodes to ~50M useful nodes
```

---

## 8. Ranking Suggestions

### 8.1 Ranking Signals

```
Composite score for each suggestion:

  Score = w1 * popularity + w2 * freshness + w3 * personalization + w4 * context

Components:
  1. Popularity (w1 = 0.5):
     - Historical search frequency (all-time or recent 30 days)
     - Normalized: frequency / max_frequency
     
  2. Freshness / Trending (w2 = 0.2):
     - Recent surge in searches
     - trending_score = count_last_1h / avg_hourly_count
     - High ratio = trending upward
     
  3. Personalization (w3 = 0.2):
     - User's own search history
     - If user frequently searches "python programming",
       boost "python..." suggestions for this user
     - Computed at query time, merged with base results
     
  4. Context (w4 = 0.1):
     - Time of day (morning: boost "coffee", "breakfast")
     - Location (in NYC: boost "NYC" related queries)
     - Device (mobile: boost shorter queries)
     - Current page/app context
```

### 8.2 Trending Score Calculation

```
  Trending detection:
    
    For each query, track search count per hour:
    
    "taylor swift tickets":
      Hour -24: 500/hr (normal baseline)
      Hour -2:  800/hr (increasing)
      Hour -1:  2,500/hr (SPIKE!)
      Hour  0:  5,000/hr (TRENDING!)
    
    trending_score = current_hour_count / avg_last_24h_hourly_count
                   = 5,000 / 500 = 10.0 (very high → trending!)
    
    If trending_score > 3.0: mark as "trending"
    Boost these queries in autocomplete results
```

---

## 9. Data Pipeline: End-to-End

### 9.1 Offline Pipeline (Trie Building)

```
The trie is NOT built in real-time. It's a batch process:

  +----------+     +----------------+     +--------------+
  | Raw Query|     | MapReduce /    |     | Trie Builder |
  | Logs     | --> | Spark Job      | --> | (builds trie |
  | (S3/HDFS)|     | (aggregate     |     |  from sorted |
  |          |     |  counts,       |     |  query-count |
  |          |     |  weekly/daily) |     |  pairs)      |
  +----------+     +----------------+     +------+-------+
                                                 |
                                          +------v-------+
                                          | Serialize    |
                                          | Trie to disk |
                                          | (protobuf /  |
                                          |  flatbuffers)|
                                          +------+-------+
                                                 |
                                          +------v-------+
                                          | Deploy to    |
                                          | Trie Servers |
                                          | (rolling     |
                                          |  update)     |
                                          +--------------+

Schedule:
  Full rebuild: daily (using last 30 days of data)
  Trending update: every 15 minutes (overlay on base trie)
```

### 9.2 Near-Real-Time Pipeline (Trending)

```
For trending queries that need to appear within 15 minutes:

  Search queries --> Kafka --> Flink (5-min windows) --> Trending Cache (Redis)
  
  Trie servers check Redis for trending overlay:
    1. Lookup prefix in trie → base top-10 suggestions
    2. Check Redis "trending:{prefix}" → any trending additions
    3. Merge: insert trending queries, re-rank, return top 10
    
  This avoids rebuilding the entire trie for trending changes.

  +-------------+     +--------+     +---------+     +------------+
  | Kafka       | --> | Flink  | --> | Redis   | --> | Trie Server|
  | (queries)   |     | (5-min |     |(trending|     | (merge with|
  |             |     | window)|     | cache)  |     |  base trie)|
  +-------------+     +--------+     +---------+     +------------+
```

---

## 10. Serving Architecture

### 10.1 Query Flow

```
User types "how to c" in search box:

  Browser           API Gateway       Trie Server       Redis (trending)
    |                    |                |                    |
    |--GET /suggest?     |                |                    |
    |  q=how+to+c ------>|                |                    |
    |                    |--route to----->|                    |
    |                    |  shard by      |                    |
    |                    |  prefix hash   |                    |
    |                    |                |--lookup "how to c"|
    |                    |                |  in trie           |
    |                    |                |                    |
    |                    |                |--check trending--->|
    |                    |                |<--trending results-|
    |                    |                |                    |
    |                    |                |--merge & rank----->|
    |                    |                |                    |
    |                    |<--top 10-------|                    |
    |<--suggestions------|                |                    |
    |  ["how to cook...",|                |                    |
    |   "how to code..." |                |                    |
    |   ...]             |                |                    |
    |                    |                |                    |
    | (total: ~30-50ms)  |                |                    |
```

### 10.2 Client-Side Behavior

```
Client-side optimization:

  1. Debouncing:
     - Don't send request on EVERY keystroke
     - Wait 100-200ms after last keystroke before sending
     - Reduces requests by 50-70%
     
  2. Client-side caching:
     - Cache results in browser/app memory
     - "how t" results cached → when user types "how to",
       filter client-side before server request
     - If client has results for "how", can filter for "how t" locally
     
  3. Abort previous request:
     - If user types fast: "h", "ho", "how"
     - Abort the request for "h" and "ho" when "how" is typed
     - Don't waste bandwidth on stale prefixes
     
  4. Pre-fetch:
     - After receiving results for "how", pre-fetch "how " (with space)
     - Anticipate the next character

  function onInput(prefix) {
    clearTimeout(debounceTimer);
    abortPreviousRequest();
    
    // Check local cache first
    if (cache.has(prefix)) {
      showSuggestions(cache.get(prefix));
      return;
    }
    
    debounceTimer = setTimeout(() => {
      fetch(`/suggest?q=${prefix}`)
        .then(results => {
          cache.set(prefix, results);
          showSuggestions(results);
        });
    }, 150);  // 150ms debounce
  }
```

---

## 11. Caching Strategy

### 11.1 Multi-Level Caching

```
Level 1: Browser/Client Cache
  - Cache results in memory (JavaScript Map)
  - Cache the most recent 50 prefix results
  - Instant response for backspace/re-typing

Level 2: CDN Edge Cache
  - Cache popular prefixes at CDN edge (CloudFront, Akamai)
  - Top 10,000 prefixes cover 90%+ of requests
  - TTL: 5-15 minutes (balance freshness vs hit rate)
  - Cache key: "suggest/{prefix}" or "suggest/{prefix}/{locale}"
  
  Highly effective because:
    - Everyone searching "how to" gets the SAME suggestions
    - Popular prefixes are extremely cacheable
    - CDN responds in <10ms (edge location)

Level 3: Application-level Cache (Redis)
  - Cache results for top 100K prefixes
  - TTL: 5 minutes
  - Trie server checks Redis before traversing trie
  - Reduces trie server load by 80%

Level 4: Trie Server (in-memory)
  - The trie itself IS the cache of precomputed results
  - Trie kept in memory on each server
  - Each lookup is O(prefix_length) — extremely fast

  User → CDN Edge → Redis Cache → Trie Server (memory)
           90%        8%              2%
         hit rate   hit rate       actual trie lookup
```

### 11.2 Cache Invalidation

```
When trending queries change or trie is updated:

  1. CDN: Set short TTL (5-15 min), let entries expire naturally
     For urgent updates: CDN purge API for specific prefixes
  
  2. Redis: Update keys when trending cache changes
     Publish to Redis Pub/Sub → all app servers invalidate local cache
  
  3. Trie update: Rolling deployment
     Build new trie → deploy to trie servers one by one
     During rollout: some servers serve old trie, some new
     (Acceptable: small inconsistency for a few minutes)
```

---

## 12. Trie Storage and Persistence

### 12.1 In-Memory Trie

```
The trie MUST be in memory for fast lookups:

  Trie server memory layout:
    - Trie data structure: ~100-200 GB
    - Server RAM: 256 GB (leaves room for OS, etc.)
    
  Startup:
    1. Load serialized trie from disk (SSD) or S3
    2. Deserialize into memory
    3. Time: 2-5 minutes for 100GB trie
    4. Server ready to serve

  Serialization format:
    - Protocol Buffers (compact, fast to deserialize)
    - Or custom binary format (even faster)
    - Saved to SSD and S3 for redundancy
```

### 12.2 Trie Persistence

```
  Trie Build Pipeline:
    Spark Job → Trie Builder → Serialize → Upload to S3
    
  Deployment:
    S3 → Download to trie server SSD → Load into memory
    
  Versioning:
    trie_v20240115_001.bin (daily version)
    trie_v20240115_trending_042.bin (trending overlay, every 15 min)

  Recovery:
    If trie server crashes:
      1. New server pulls latest trie from S3
      2. Loads into memory (2-5 min)
      3. Ready to serve
      4. During recovery: other shards handle extra load
```

---

## 13. Trie Update Strategy

### 13.1 Periodic Full Rebuild

```
Approach: Rebuild entire trie daily with fresh data

  Schedule: Every day at 2 AM (off-peak)
  Input: Aggregated query counts from last 30 days
  Output: New serialized trie file
  
  Steps:
    1. Spark job aggregates query counts
    2. Trie builder creates new trie with precomputed top-K
    3. Serialize to S3
    4. Rolling deployment to trie servers:
       - Take server out of rotation
       - Load new trie
       - Put server back in rotation
       - Repeat for next server

  Pros: Simple, consistent, clean data
  Cons: 24-hour delay for new queries (mitigated by trending overlay)
```

### 13.2 Incremental Updates (Alternative)

```
For more real-time updates:

  Option: Apply delta updates to in-memory trie

  Every 15 minutes:
    1. Flink produces delta: {queries_with_changed_counts}
    2. Trie server receives delta via Kafka
    3. For each changed query:
       a. Update frequency in trie node
       b. Recalculate top-K for affected nodes (propagate up)
    
  Pros: Near-real-time suggestions
  Cons: Complex, trie drift over time (need periodic full rebuild)
  
  Recommendation: Full rebuild daily + trending overlay every 15 min
    (Best balance of simplicity and freshness)
```

---

## 14. Sharding the Trie

### 14.1 Sharding by Prefix Range

```
Split the trie across multiple servers by prefix:

  Shard 1: prefixes starting with a-f
  Shard 2: prefixes starting with g-m
  Shard 3: prefixes starting with n-s
  Shard 4: prefixes starting with t-z
  Shard 5: prefixes starting with 0-9, special chars

  +----------+  +----------+  +----------+  +----------+
  | Shard 1  |  | Shard 2  |  | Shard 3  |  | Shard 4  |
  | a-f      |  | g-m      |  | n-s      |  | t-z      |
  | "apple"  |  | "google" |  | "python" |  | "twitter"|
  | "best"   |  | "how to" |  | "react"  |  | "uber"   |
  | "coffee" |  | "java"   |  | "sql"    |  | "weather"|
  +----------+  +----------+  +----------+  +----------+

Problem: Uneven distribution!
  "how to..." queries are FAR more frequent than "x..." queries
  Shard 2 gets overwhelmed

Solution: Weight-based splitting
  Measure query volume per prefix range
  Split hot ranges into smaller shards:
  
  Shard 1: a-c     (balanced)
  Shard 2: d-f     (balanced)
  Shard 3: g-h     (smaller range, "how to" is heavy)
  Shard 4: i-m     (balanced)
  ...
```

### 14.2 Consistent Hashing (Alternative)

```
Use consistent hashing on the first 2-3 characters of the prefix:

  hash("ho") % N_shards → shard 7
  hash("wh") % N_shards → shard 3

  Pros:
    - Better load distribution
    - Easy to add/remove shards
    
  Cons:
    - Related prefixes may end up on different shards
    - Slightly more complex routing

Routing layer:
  API Gateway looks at first 2 chars of prefix
  Routes to correct shard based on hash ring
  
  Each shard has 2-3 replicas for redundancy
```

---

## 15. Filtering Inappropriate Suggestions

### 15.1 Content Filtering Pipeline

```
Inappropriate suggestions must NEVER appear:

  Filtering at multiple stages:

  1. Blocklist (hard filter):
     - Maintain a list of banned queries (profanity, violence, etc.)
     - During trie build: exclude all blocklisted queries
     - Updated by content moderation team
     - ~100K blocked terms

  2. ML Classification:
     - Train a classifier: safe vs unsafe
     - Run on all queries during trie build
     - Flag borderline queries for human review
     - Categories: hate speech, adult content, violence, self-harm

  3. Real-time filter:
     - Even with pre-filtering, new inappropriate queries can emerge
     - Trie server checks bloom filter of blocked terms
     - If query matches bloom filter → exclude from results
     - Bloom filter: compact, fast (1-2 microseconds per check)

  4. Manual review queue:
     - Queries flagged by ML but not blocked
     - Human reviewers approve or block
     - Decisions fed back into blocklist

  Build Pipeline:
    Queries → Remove blocklisted → ML classify → Build trie
                                        |
                                   Flag uncertain → Human review → Update blocklist
```

---

## 16. Multi-Language Support

```
Challenges:
  - Different scripts: Latin, CJK, Arabic, Cyrillic, etc.
  - CJK: no clear word boundaries (no spaces)
  - Arabic/Hebrew: right-to-left
  - Accent handling: "cafe" should match "cafe"

Solutions:

  1. Per-Language Tries:
     - Build separate trie for each major language
     - Route based on user's locale or detected input language
     - "en" trie, "es" trie, "zh" trie, "ja" trie, etc.
     
  2. Unicode Normalization:
     - Normalize to NFC form
     - Strip accents for matching: "cafe" → "cafe" (add "cafe" as alias)
     - Case-fold for consistent matching
     
  3. CJK Handling:
     - Character-level trie (each Chinese character is a node)
     - Or: use n-gram based approach instead of word-level trie
     - Pinyin support for Chinese: type "zhongguo" → suggest "中国"
     
  4. Transliteration:
     - Support Latin input for non-Latin languages
     - "sushi" → "寿司" (Japanese)
     - "namaste" → "नमस्ते" (Hindi)

  Routing:
    Request includes locale header: Accept-Language: zh-CN
    Route to Chinese trie shard
    
    Or: auto-detect from input characters
    Unicode range detection: CJK Unified Ideographs → Chinese/Japanese trie
```

---

## 17. Personalization Layer

```
Personalization: Boost suggestions based on user's history

  User A frequently searches for:
    "python tutorial", "python documentation", "python flask"
    
  When User A types "py":
    Base suggestions: ["pypl stock", "pyramids", "python snake"]
    Personalized:     ["python tutorial", "python flask", "python docs"]

Implementation:

  1. User Search History:
     - Store last 1000 searches per user (Redis)
     - Key: "history:{user_id}" → sorted set of (query, timestamp)
     
  2. At query time:
     a. Get base suggestions from trie (top 20 candidates)
     b. Get user's recent searches matching the prefix (top 5)
     c. Score each candidate:
        final_score = base_score * 0.7 + personal_score * 0.3
     d. Re-rank and return top 10

  3. Personal suggestions limited to:
     - Only queries the user has searched before
     - No cross-user personalization (privacy)
     - User can clear history to reset personalization

  Flow:
    Prefix "py" → Trie: [top 20 base results]
                → Redis: [user's matching history: "python tutorial", ...]
                → Merge & re-rank → Top 10 personalized results
```

---

## 18. Full System Architecture

```
+------------------------------------------------------------------------+
|                    CLIENTS (Web Browser / Mobile App)                    |
|  +------------------+  +------------------+  +------------------+      |
|  | Debounce (150ms) |  | Client Cache     |  | Abort stale     |      |
|  | Keypress handler |  | (LRU, 50 entries)|  | requests        |      |
|  +------------------+  +------------------+  +------------------+      |
+-----------------------------------+------------------------------------+
                                    |
                            +-------v--------+
                            | CDN Edge       |
                            | (Cache popular |
                            |  prefixes,     |
                            |  TTL: 10 min)  |
                            +-------+--------+
                                    |  (cache miss)
                            +-------v--------+
                            | Load Balancer  |
                            +-------+--------+
                                    |
                            +-------v--------+
                            | API Gateway    |
                            | (rate limit,   |
                            |  auth, route   |
                            |  by prefix     |
                            |  to shard)     |
                            +-------+--------+
                                    |
              +---------------------+---------------------+
              |                     |                     |
      +-------v-------+    +-------v-------+    +-------v-------+
      | Trie Server   |    | Trie Server   |    | Trie Server   |
      | Shard 1 (a-h) |    | Shard 2 (i-p) |    | Shard 3 (q-z) |
      | (in-memory    |    | (in-memory    |    | (in-memory    |
      |  trie +       |    |  trie +       |    |  trie +       |
      |  local cache) |    |  local cache) |    |  local cache) |
      +---+---+-------+    +-------+-------+    +-------+-------+
          |   |                     |
     +----+   +-------+            |
     |                |            |
+----v-----+   +------v------+    |
| Redis    |   | Redis       |    |
| (trending|   | (user search|    |
|  overlay)|   |  history for|    |
|          |   |  personal.) |    |
+----------+   +-------------+    |

OFFLINE DATA PIPELINE:
+------------------------------------------------------------------------+
|                                                                         |
|  +----------+    +----------+    +-------------+    +--------------+   |
|  | Search   | -> | Kafka    | -> | Flink       | -> | Aggregated   |  |
|  | Logs     |    | (raw     |    | (aggregate  |    | Counts DB    |  |
|  | (events) |    | queries) |    | per window) |    | (S3 / HDFS)  |  |
|  +----------+    +----------+    +------+------+    +------+-------+  |
|                                         |                   |          |
|                                  +------v------+    +-------v------+  |
|                                  | Trending    |    | Trie Builder |  |
|                                  | Cache       |    | (daily Spark |  |
|                                  | Update      |    |  job)        |  |
|                                  | (15 min)    |    +------+-------+  |
|                                  +------+------+           |          |
|                                         |           +------v-------+  |
|                                         v           | Serialized   |  |
|                                  +------+------+    | Trie → S3    |  |
|                                  | Redis       |    | → Deploy to  |  |
|                                  | (trending)  |    |   Servers    |  |
|                                  +-------------+    +--------------+  |
|                                                                         |
+------------------------------------------------------------------------+

  +------------------+
  | Content Filter   |  (Blocklist + ML classifier + Bloom filter)
  | Service          |
  +------------------+
```

---

## 19. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Data structure | Trie (prefix tree) | Inverted index (Elasticsearch) | **Trie** | Purpose-built for prefix search, O(P) lookup |
| Top-K computation | At query time (DFS) | Precomputed at each node | **Precomputed** | O(P) vs O(P+N), essential for <100ms |
| Trie update | Real-time incremental | Periodic full rebuild | **Daily rebuild + trending overlay** | Simple + fresh enough |
| Trending | Batch (hourly) | Stream processing (Flink) | **Stream (15-min windows)** | Fast enough for trending |
| Sharding | By prefix range | Consistent hashing | **Prefix range** (weighted) | Locality, simple routing |
| Caching | Server-side only | Multi-level (CDN+Redis+client) | **Multi-level** | 90%+ hit rate at CDN edge |
| Personalization | No personalization | User history re-ranking | **User history** | Better UX with privacy |
| Storage | Disk-based (mmap) | Fully in-memory | **In-memory** | Speed is everything for typeahead |
| Filtering | Post-query filter | Pre-build exclusion | **Both** | Pre-build for known, post-query for new |

---

## 20. Performance Optimization Summary

```
End-to-end latency budget (target: <100ms total):

  Client debounce:            150ms (before request sent)
  Network to CDN edge:        5-20ms (user to nearest PoP)
  CDN cache hit:              1-2ms  (90% of requests end here!)
  
  CDN cache miss path:
  Network CDN to origin:      10-30ms
  API Gateway routing:        1-2ms
  Trie server lookup:         1-5ms (O(prefix_length))
  Redis trending check:       1-2ms
  Personalization merge:      1-2ms
  Response serialization:     1ms
  Network back to CDN:        10-30ms
  Network back to user:       5-20ms
  
  Total (cache miss): ~40-80ms (within 100ms budget)
  Total (cache hit):  ~10-25ms (excellent!)
```

---

## 21. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. TRIE is the ideal data structure for prefix-based autocomplete|
|    — navigate to prefix in O(P), return precomputed results      |
|                                                                    |
| 2. PRECOMPUTE top-K suggestions at every trie node —             |
|    this is THE critical optimization (O(P) vs O(P+N))            |
|                                                                    |
| 3. Two-pipeline architecture: daily FULL rebuild for base data   |
|    + 15-minute TRENDING overlay for freshness                    |
|                                                                    |
| 4. Multi-level caching (browser → CDN → Redis → in-memory trie) |
|    achieves 90%+ cache hit rate at CDN edge                      |
|                                                                    |
| 5. Client-side debouncing (150ms) reduces server requests by     |
|    50-70% — essential for cost and performance                   |
|                                                                    |
| 6. Trie must be IN-MEMORY for speed — 100-200GB fits on a       |
|    single server, shard across 3-5 for redundancy and scale      |
|                                                                    |
| 7. Content filtering is NON-NEGOTIABLE — blocklist + ML          |
|    classifier + bloom filter at serving time                     |
|                                                                    |
| 8. Personalization via user search history re-ranking adds       |
|    significant value with minimal complexity                     |
|                                                                    |
| 9. Sharding by prefix range works well but must be WEIGHTED     |
|    to account for hot prefixes ("how to", "what is")             |
|                                                                    |
| 10. The entire system is optimized for ONE metric: latency.      |
|     Every design decision prioritizes sub-100ms response time    |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 33 - File Storage System](./33-file-storage.md)*
