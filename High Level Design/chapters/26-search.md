# Chapter 26: Search Systems

## Introduction

Search is one of the most ubiquitous features in modern software. Whether it's finding
products on Amazon, searching for code on GitHub, querying logs in a monitoring system,
or typing a question into Google — search is everywhere.

Building an effective search system involves far more than running `SELECT * FROM products
WHERE name LIKE '%widget%'`. Real search systems handle millions of documents, return
results in milliseconds, rank results by relevance, support autocomplete and typo
correction, and scale horizontally across clusters.

This chapter covers the fundamentals of search — from inverted indexes and text analysis
to Elasticsearch architecture, relevance scoring, autocomplete systems, and scaling
strategies.

---

## 26.1 Why Search Is a Common System Design Component

Almost every system needs some form of search:

| System Type          | Search Use Case                                        |
|----------------------|--------------------------------------------------------|
| **E-Commerce**       | Product search, filtering, faceted navigation          |
| **Social Media**     | People search, post/content search, hashtag search     |
| **SaaS/B2B**        | Full-text search across documents, tickets, records    |
| **Log/Monitoring**   | Search across billions of log entries (ELK stack)      |
| **Code Platforms**   | Code search across repositories (GitHub, Sourcegraph)  |
| **Knowledge Bases**  | Article/documentation search                           |
| **Maps**            | Location/place name search (geocoding)                 |

A relational database's `LIKE '%term%'` is insufficient because:
- It performs a **full table scan** (O(n) per query) — doesn't scale
- It doesn't support **relevance ranking** — which result is best?
- It doesn't handle **typos**, **synonyms**, or **stemming**
- It doesn't support **faceted filtering** or **aggregations** efficiently

---

## 26.2 Full-Text Search Fundamentals

### Text Analysis Pipeline

Before a document can be searched, its text must be **analyzed** — broken down
into searchable tokens.

```
Original Text: "The quick brown foxes jumped over the lazy dogs!"
                                |
                        +-------v--------+
                        |  TOKENIZATION  |
                        |  Split into    |
                        |  words/tokens  |
                        +-------+--------+
                                |
            ["The", "quick", "brown", "foxes", "jumped", "over", "the", "lazy", "dogs"]
                                |
                        +-------v--------+
                        |  LOWERCASING   |
                        +-------+--------+
                                |
            ["the", "quick", "brown", "foxes", "jumped", "over", "the", "lazy", "dogs"]
                                |
                        +-------v--------+
                        | STOP WORD      |
                        | REMOVAL        |
                        | (the, over, a) |
                        +-------+--------+
                                |
            ["quick", "brown", "foxes", "jumped", "lazy", "dogs"]
                                |
                        +-------v--------+
                        |  STEMMING /    |
                        | LEMMATIZATION  |
                        +-------+--------+
                                |
            ["quick", "brown", "fox", "jump", "lazi", "dog"]
```

### Key Concepts

**Tokenization:** Splitting text into individual terms. Handles punctuation,
special characters, CamelCase splitting, etc.

**Stop Words:** Common words (the, a, an, is, are, etc.) that add little
search value. Removing them reduces index size and improves relevance.

**Stemming:** Reducing words to their root form using algorithmic rules:
- "running" → "run"
- "foxes" → "fox"
- "jumped" → "jump"
- Porter Stemmer, Snowball Stemmer are common algorithms

**Lemmatization:** Similar to stemming but uses a dictionary to find the
actual root word (lemma):
- "better" → "good" (stemming would leave "better")
- "mice" → "mouse"
- More accurate but slower than stemming

---

## 26.3 The Inverted Index

The **inverted index** is the core data structure of search engines. It maps
every term to the list of documents containing that term.

### How It Works

```
Documents:
  Doc 1: "The quick brown fox"
  Doc 2: "The lazy brown dog"
  Doc 3: "The fox jumped over the dog"

After analysis (tokenize, lowercase, remove stop words, stem):
  Doc 1: [quick, brown, fox]
  Doc 2: [lazi, brown, dog]
  Doc 3: [fox, jump, dog]

INVERTED INDEX:
+----------+------------------+
|   Term   |  Document IDs    |
+----------+------------------+
| brown    |  [1, 2]          |
| dog      |  [2, 3]          |
| fox      |  [1, 3]          |
| jump     |  [3]             |
| lazi     |  [2]             |
| quick    |  [1]             |
+----------+------------------+

Search for "brown fox":
  "brown" → [1, 2]
  "fox"   → [1, 3]
  Intersection: [1]   → Doc 1 matches both terms
  Union: [1, 2, 3]    → All docs match at least one term
```

### Inverted Index with Positions (for phrase queries)

```
+----------+----------------------------------+
|   Term   |  Postings (docId: [positions])   |
+----------+----------------------------------+
| brown    |  {1: [2], 2: [1]}               |
| dog      |  {2: [2], 3: [3]}               |
| fox      |  {1: [3], 3: [1]}               |
| jump     |  {3: [2]}                        |
+----------+----------------------------------+

Phrase search "brown fox":
  "brown" at Doc 1, position 2
  "fox" at Doc 1, position 3
  Positions are adjacent (2, 3) → MATCH for phrase "brown fox"
```

### Why Inverted Index Is Fast

- **O(1) lookup** per term (hash map or sorted tree)
- **Boolean operations** (AND, OR, NOT) on document ID lists
- Pre-computed at index time, so queries are fast
- Contrast with SQL `LIKE '%term%'` which is O(n) full scan

---

## 26.4 Elasticsearch

**Elasticsearch** is the most widely used search engine, built on top of
**Apache Lucene** (the core indexing and search library).

### Architecture

```
+------------------------------------------------------------------+
|                     ELASTICSEARCH CLUSTER                         |
|                                                                  |
|  +---Node 1 (Master)---+  +---Node 2 (Data)----+               |
|  |                      |  |                     |               |
|  | Shard 0 (Primary)    |  | Shard 0 (Replica)  |               |
|  | Shard 1 (Replica)    |  | Shard 1 (Primary)  |               |
|  +----------------------+  +---------------------+               |
|                                                                  |
|  +---Node 3 (Data)-----+  +---Node 4 (Data)----+               |
|  |                      |  |                     |               |
|  | Shard 2 (Primary)    |  | Shard 2 (Replica)  |               |
|  | Shard 3 (Replica)    |  | Shard 3 (Primary)  |               |
|  +----------------------+  +---------------------+               |
|                                                                  |
+------------------------------------------------------------------+
```

### Key Concepts

| Concept        | Description                                                 |
|----------------|-------------------------------------------------------------|
| **Cluster**    | A group of nodes working together                           |
| **Node**       | A single Elasticsearch server instance                      |
| **Index**      | A collection of documents (like a database table)           |
| **Document**   | A single JSON record within an index                        |
| **Shard**      | A subset of an index. Each shard is a Lucene index          |
| **Primary Shard** | The original shard that accepts writes                   |
| **Replica Shard** | A copy of a primary shard for HA and read scaling        |
| **Mapping**    | Schema definition (field types, analyzers)                  |

### Document Indexing

```json
PUT /products/_doc/1
{
  "name": "Wireless Bluetooth Headphones",
  "description": "High-quality noise-cancelling headphones with 30-hour battery",
  "price": 79.99,
  "category": "Electronics",
  "brand": "AudioPro",
  "rating": 4.5,
  "in_stock": true,
  "tags": ["wireless", "bluetooth", "noise-cancelling"]
}
```

When indexed, Elasticsearch:
1. Analyzes text fields (tokenize, stem, etc.)
2. Builds/updates the inverted index
3. Stores the original document (for retrieval)
4. Routes to the correct shard (based on document ID hash)

### Querying

```json
GET /products/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "description": "wireless headphones" } }
      ],
      "filter": [
        { "range": { "price": { "lte": 100 } } },
        { "term": { "in_stock": true } }
      ]
    }
  },
  "sort": [
    { "_score": "desc" },
    { "rating": "desc" }
  ],
  "size": 10
}
```

### Relevance Scoring

Elasticsearch uses **BM25** (Best Match 25) as its default scoring algorithm.
BM25 is an improvement over the classic **TF-IDF** model.

#### TF-IDF (Term Frequency - Inverse Document Frequency)

```
TF-IDF(term, doc) = TF(term, doc) × IDF(term)

TF (Term Frequency):
  How often does the term appear in this document?
  TF = count(term in doc) / total_terms_in_doc

IDF (Inverse Document Frequency):
  How rare is the term across all documents?
  IDF = log(total_docs / docs_containing_term)

Example:
  Query: "bluetooth headphones"
  
  Doc A: "Bluetooth headphones with long battery life"
    TF("bluetooth") = 1/6 = 0.17
    TF("headphones") = 1/6 = 0.17
    
  Doc B: "Bluetooth Bluetooth headphones premium Bluetooth audio"
    TF("bluetooth") = 3/6 = 0.50
    TF("headphones") = 1/6 = 0.17
    
  IDF("bluetooth") = log(1000/200) = 0.70  (common term)
  IDF("headphones") = log(1000/50) = 1.30  (rarer term)
  
  Score A = (0.17 × 0.70) + (0.17 × 1.30) = 0.34
  Score B = (0.50 × 0.70) + (0.17 × 1.30) = 0.57
  
  Doc B ranks higher (more "bluetooth" mentions)
```

#### BM25 (Improvement over TF-IDF)

BM25 adds **saturation** — after a term appears many times, additional occurrences
contribute diminishing returns. It also normalizes by **document length**.

```
BM25 scoring curve vs TF-IDF:

Score
  ^
  |         BM25 (saturates)
  |        _______________
  |      /
  |    /       TF-IDF (linear, keeps growing)
  |   /       /
  |  /      /
  | /     /
  |/    /
  +--+--+--+--+--+--+--> Term Frequency
  0  1  2  3  4  5  6
```

### Near Real-Time Search

Elasticsearch is **near real-time** — documents become searchable within ~1 second
of indexing. This is because:

1. Documents are first written to an **in-memory buffer**
2. Periodically (every 1 second by default), the buffer is **refreshed** into a
   new **segment** (a small Lucene index)
3. Segments are searchable immediately after refresh
4. Segments are periodically **merged** into larger segments (background process)

```
Index Buffer (in memory) --refresh (1s)--> New Segment (searchable)
                                              |
Existing Segments:  [Seg 1] [Seg 2] [Seg 3] [Seg 4]
                           |         |
                     merge |         |
                           v         |
                       [Merged Seg]  [Seg 4]
```

---

## 26.5 Search System Design

### Indexing Pipeline

```
+-------------+     +-----------+     +------------+     +---------------+
| Data Source  | --> | Transform | --> | Analyze    | --> | Elasticsearch |
|             |     |           |     |            |     |    Index      |
| - Database  |     | - Clean   |     | - Tokenize |     |               |
| - Files     |     | - Enrich  |     | - Stem     |     |  Inverted     |
| - APIs      |     | - Flatten |     | - Filter   |     |  Index        |
| - Streams   |     | - Merge   |     |            |     |               |
+-------------+     +-----------+     +------------+     +---------------+
```

**Data Sources:**
- Database CDC (Debezium) for real-time sync
- Batch ETL jobs for bulk re-indexing
- API webhooks for external data
- Kafka consumers for event-driven indexing

**Transform:**
- Clean HTML, remove special characters
- Enrich with additional data (e.g., add category name from category ID)
- Flatten nested structures for efficient indexing
- Merge data from multiple sources

### Query Processing Pipeline

```
User Query: "wirless headfones under $50"
       |
       v
+------------------+
| Query Parsing    |  Parse into structured query
| - Tokenize       |  "wirless" "headfones" "under" "$50"
+--------+---------+
         |
         v
+------------------+
| Spell Correction |  "wirless" → "wireless"
| / Did You Mean   |  "headfones" → "headphones"
+--------+---------+
         |
         v
+------------------+
| Query Expansion  |  "headphones" → "headphones", "earphones", "earbuds"
| (Synonyms)       |  (synonym expansion)
+--------+---------+
         |
         v
+------------------+
| Search Execution |  Run against inverted index
| (ES/Lucene)      |  Apply filters (price < 50)
+--------+---------+
         |
         v
+------------------+
| Ranking          |  BM25 score + custom boosting
|                  |  + popularity + recency
+--------+---------+
         |
         v
+------------------+
| Post-processing  |  Highlighting, snippets
|                  |  Facet counts, pagination
+--------+---------+
         |
         v
    Search Results
```

---

## 26.6 Autocomplete / Typeahead

Autocomplete suggests completions as the user types. This is one of the most
latency-sensitive features — results must appear within **50-100ms**.

### Trie Data Structure

A **trie** (prefix tree) is the classic data structure for autocomplete:

```
                    (root)
                   /  |  \
                  h   s   w
                 /    |    \
                e     e     i
               / \    |      \
              a   l   a       r
             /    |    \       \
            d    l      r       e
           /      \      \       \
          p        o      c       l
         /                 \       \
        h                   h       e
        |                            \
        o                             s
        |                              \
        n                               s
        |
        e
        |
        s
        
  Suggestions for "hea":
    h → e → a → d → p → h → o → n → e → s  → "headphones"
    h → e → a → d → s → e → t               → "headset"
```

### Prefix Matching with Ranking

Each node in the trie can store the **top-K most popular completions** for
that prefix:

```
Trie Node for prefix "hea":
  +-------+
  | "hea" |
  | Top completions:              |
  |   1. "headphones"    (score: 95000) |
  |   2. "headset"       (score: 42000) |
  |   3. "health"        (score: 38000) |
  |   4. "hearing aids"  (score: 12000) |
  +-------+
```

### Elasticsearch Autocomplete

Elasticsearch provides built-in autocomplete with:

**1. Prefix Query**
```json
{
  "query": {
    "prefix": {
      "name": {
        "value": "wire"
      }
    }
  }
}
```

**2. Completion Suggester (optimized for speed)**
```json
PUT /products
{
  "mappings": {
    "properties": {
      "suggest": {
        "type": "completion"
      }
    }
  }
}

POST /products/_search
{
  "suggest": {
    "product-suggest": {
      "prefix": "wire",
      "completion": {
        "field": "suggest",
        "size": 5,
        "fuzzy": {
          "fuzziness": 1
        }
      }
    }
  }
}
```

The completion suggester uses an **FST (Finite State Transducer)** in memory
for sub-millisecond lookups.

### Real-Time Updates for Autocomplete

As users search, update suggestion rankings:
1. Track search query frequency (e.g., in Redis sorted sets)
2. Periodically rebuild the trie/FST with updated frequencies
3. Use time-decay to favor recent queries over old ones

```
Redis Sorted Set:
  ZINCRBY search:suggestions 1 "wireless headphones"
  ZINCRBY search:suggestions 1 "wireless keyboard"
  
  ZREVRANGE search:suggestions 0 9  → Top 10 suggestions by frequency
```

---

## 26.7 Faceted Search and Filtering

Faceted search allows users to **narrow results** by categories, attributes,
or ranges. This is essential for e-commerce.

```
Search: "laptop"

Results (245 found):
  +--Filters (Facets)--------+    +--Results---------------+
  |                           |    |                        |
  | Brand:                    |    | 1. Dell XPS 13         |
  |   [ ] Dell (45)          |    |    $999 - ★★★★☆        |
  |   [x] Apple (38)         |    |                        |
  |   [ ] Lenovo (32)        |    | 2. MacBook Air M2      |
  |   [ ] HP (28)            |    |    $1,199 - ★★★★★      |
  |                           |    |                        |
  | Price Range:              |    | 3. MacBook Pro 14"     |
  |   [ ] Under $500 (12)    |    |    $1,999 - ★★★★★      |
  |   [x] $500-$1000 (89)    |    |                        |
  |   [ ] $1000-$2000 (98)   |    +------------------------+
  |   [ ] Over $2000 (46)    |
  |                           |
  | RAM:                      |
  |   [ ] 8GB (67)           |
  |   [ ] 16GB (112)         |
  |   [ ] 32GB (66)          |
  +---------------------------+
```

### Elasticsearch Aggregations

```json
GET /products/_search
{
  "query": { "match": { "name": "laptop" } },
  "aggs": {
    "brands": {
      "terms": { "field": "brand.keyword", "size": 10 }
    },
    "price_ranges": {
      "range": {
        "field": "price",
        "ranges": [
          { "to": 500 },
          { "from": 500, "to": 1000 },
          { "from": 1000, "to": 2000 },
          { "from": 2000 }
        ]
      }
    },
    "avg_rating": {
      "avg": { "field": "rating" }
    }
  }
}
```

---

## 26.8 Search Relevance Tuning

Out-of-the-box BM25 scoring often isn't enough. Real-world search needs
**custom relevance tuning**.

### Boosting

Increase the importance of certain fields:

```json
{
  "query": {
    "multi_match": {
      "query": "wireless headphones",
      "fields": [
        "name^3",
        "description^1",
        "tags^2"
      ]
    }
  }
}
```

Name matches are 3x more important than description matches.

### Function Score (Custom Ranking Signals)

Combine text relevance with business signals:

```json
{
  "query": {
    "function_score": {
      "query": { "match": { "name": "headphones" } },
      "functions": [
        {
          "field_value_factor": {
            "field": "popularity",
            "modifier": "log1p",
            "factor": 0.5
          }
        },
        {
          "gauss": {
            "date_published": {
              "origin": "now",
              "scale": "30d",
              "decay": 0.5
            }
          }
        }
      ],
      "score_mode": "multiply",
      "boost_mode": "multiply"
    }
  }
}
```

This boosts results by **popularity** and **recency**.

### Synonyms

Define synonym mappings so users find results regardless of terminology:

```
"wireless" → "wireless", "cordless", "Bluetooth"
"laptop"   → "laptop", "notebook", "portable computer"
"TV"       → "TV", "television", "flat screen"
```

### Fuzzy Matching (Typo Tolerance)

Handle misspellings by allowing edits (insertions, deletions, substitutions):

```json
{
  "query": {
    "match": {
      "name": {
        "query": "headfones",
        "fuzziness": "AUTO"
      }
    }
  }
}
```

`"headfones"` matches `"headphones"` (edit distance: 1)

---

## 26.9 Pagination in Search Results

### Offset-Based Pagination (Simple but Limited)

```json
{ "from": 0,  "size": 10 }   // Page 1
{ "from": 10, "size": 10 }   // Page 2
{ "from": 20, "size": 10 }   // Page 3
```

**Problem:** Deep pagination is expensive. `from: 10000, size: 10` requires
Elasticsearch to fetch and sort 10,010 results, then discard the first 10,000.
Elasticsearch limits this to 10,000 by default.

### Search After (Cursor-Based Pagination)

Use the sort values of the **last result** as a cursor for the next page:

```json
// Page 1:
{ "size": 10, "sort": [{ "_score": "desc" }, { "_id": "asc" }] }

// Response includes last result's sort values: [0.85, "doc-xyz"]

// Page 2:
{
  "size": 10,
  "sort": [{ "_score": "desc" }, { "_id": "asc" }],
  "search_after": [0.85, "doc-xyz"]
}
```

**Pros:** Efficient for any depth, no 10K limit
**Cons:** Can only go forward (no "jump to page 50"), sort values can shift

### Scroll API (for Bulk Export)

For exporting all matching documents (not for user-facing pagination):
```json
POST /products/_search?scroll=5m
{ "size": 1000, "query": { "match_all": {} } }
```

| Method         | Best For                    | Max Depth   | Random Access |
|----------------|-----------------------------|-------------|---------------|
| **from/size**  | First few pages             | ~10,000     | Yes           |
| **search_after** | Deep pagination           | Unlimited   | No (forward)  |
| **Scroll**     | Bulk data export            | Unlimited   | No            |

---

## 26.10 Scaling Search

### Sharding Strategies

Distribute documents across shards for parallel processing:

```
Index: "products" (3 primary shards, 1 replica each)

+---Shard 0---+    +---Shard 1---+    +---Shard 2---+
| docs 0-33K  |    | docs 33K-66K|    | docs 66K-100K|
| (Primary)   |    | (Primary)   |    | (Primary)    |
| Node 1      |    | Node 2      |    | Node 3       |
+-------------+    +-------------+    +--------------+

+---Shard 0---+    +---Shard 1---+    +---Shard 2---+
| (Replica)   |    | (Replica)   |    | (Replica)    |
| Node 2      |    | Node 3      |    | Node 1       |
+-------------+    +-------------+    +--------------+
```

**Query flow (scatter-gather):**
1. Query hits any node (coordinating node)
2. Coordinating node sends query to all shards
3. Each shard returns its top-N results
4. Coordinating node merges and re-ranks global top-N

**Shard sizing guidelines:**
- Each shard: 10-50 GB ideal
- Too many shards: overhead per shard (memory, file handles)
- Too few shards: large shards are slow to search and recover

### Index Lifecycle Management

For time-series data (logs, events):

```
Hot Phase:     Active index, receives writes, fast SSD storage
               Index: logs-2024.01.15
               
Warm Phase:    No longer receives writes, still queried
               Index: logs-2024.01.14 (moved to cheaper storage)
               
Cold Phase:    Rarely queried, compressed, cheapest storage
               Index: logs-2024.01.01
               
Delete Phase:  Index deleted after retention period
               Index: logs-2023.12.01 (deleted)
```

### Replica Sets for Read Scaling

Add replicas to handle more search queries:

```
More replicas = more search throughput (reads distributed across replicas)
Fewer replicas = faster indexing (fewer copies to write)

Production recommendation:
  - At least 1 replica for high availability
  - 2+ replicas for read-heavy workloads
```

---

## 26.11 Alternatives to Elasticsearch

| Search Engine    | Type          | Key Differentiator                           |
|------------------|---------------|----------------------------------------------|
| **Apache Solr**  | Open source   | Mature, rich features, XML-heavy config      |
| **Meilisearch**  | Open source   | Fast, easy to set up, typo-tolerant by default|
| **Typesense**    | Open source   | Developer-friendly, easy tuning, fast        |
| **Algolia**      | SaaS          | Hosted, instant search, great DX, expensive  |
| **OpenSearch**   | Open source   | AWS fork of Elasticsearch (Apache 2.0 license)|
| **Vespa**        | Open source   | Yahoo's engine, good for ML-powered ranking  |
| **Zinc**         | Open source   | Lightweight ES alternative, Go-based         |

### When to Choose What

| Scenario                              | Recommendation                          |
|---------------------------------------|-----------------------------------------|
| Full-featured enterprise search       | Elasticsearch / OpenSearch              |
| Quick prototype, small dataset        | Meilisearch or Typesense               |
| Don't want to manage infrastructure   | Algolia (SaaS)                          |
| Log analytics (ELK stack)             | Elasticsearch / OpenSearch              |
| ML-powered ranking at scale           | Vespa                                   |
| Need Apache 2.0 license              | OpenSearch (AWS fork)                   |

---

## 26.12 Real-World Search Systems

### How E-Commerce Search Works (Amazon-like)

```
User types: "running shoes size 10"
       |
       v
+--Autocomplete Service--+     (as-you-type suggestions)
|  "running shoes"       |
|  "running shoes men"   |
|  "running shoes women" |
+------------------------+
       |
       v (user selects / presses Enter)
+--Query Understanding---+
|  Parse: "running shoes" (product) + "size 10" (attribute filter)
|  Expand: "running shoes" → "running shoes", "jogging shoes"
|  Spell check: OK
+------------------------+
       |
       v
+--Search + Ranking------+
|  Stage 1: Recall (find candidates from inverted index)     |
|    → 50,000 matching products                              |
|  Stage 2: Coarse ranking (BM25 + basic features)           |
|    → Top 1,000 candidates                                  |
|  Stage 3: Fine ranking (ML model: clicks, purchases,       |
|           reviews, price, seller quality, personalization)  |
|    → Top 100 ranked results                                |
|  Stage 4: Business rules (sponsored products, promotions)  |
|    → Final ranked list                                     |
+------------------------+
       |
       v
+--Results Page----------+
|  Products + Facets + Sponsored + "Did you mean..."         |
+------------------------+
```

### How Google Indexes the Web (High Level)

```
+---Web Crawler (Googlebot)---+
| Discovers URLs, fetches pages|
| Follows links, respects robots.txt|
+-------------+---------------+
              |
              v
+---Indexing Pipeline----------+
| Parse HTML → extract text    |
| Identify language            |
| Extract links → crawl queue  |
| Build inverted index         |
| Compute PageRank             |
+-------------+----------------+
              |
              v
+---Serving Infrastructure-----+
| Distributed index across     |
| thousands of servers         |
| (sharded by document hash)   |
+-------------+----------------+
              |
              v
+---Query Processing-----------+
| Spell correction             |
| Query understanding (intent) |
| Knowledge Graph lookup       |
| Retrieve from index          |
| Rank (200+ signals + ML)     |
| Personalization              |
+------------------------------+
```

**Scale of Google Search:**
- Indexes **hundreds of billions** of web pages
- Serves **8.5 billion** searches per day
- Returns results in **< 0.5 seconds**
- Uses **custom hardware** and **proprietary infrastructure**

---

## 26.13 Key Takeaways

1. **Full-text search** requires specialized infrastructure beyond SQL `LIKE` queries.
   Inverted indexes enable fast, scalable text search with O(1) term lookups.

2. **Text analysis** (tokenization, stemming, stop words) transforms raw text into
   searchable tokens. Good analysis is the foundation of good search.

3. **The inverted index** maps terms to document IDs, enabling fast boolean and
   ranked retrieval. It is the core data structure of all search engines.

4. **Elasticsearch** is the dominant search engine: clustered, sharded, replicated,
   with near real-time indexing and powerful query DSL.

5. **BM25** is the standard relevance scoring algorithm. It improves on TF-IDF with
   term frequency saturation and document length normalization.

6. **Autocomplete** requires sub-100ms latency. Use tries, FSTs (Elasticsearch
   completion suggester), or prefix queries with popularity ranking.

7. **Faceted search** uses aggregations to let users filter results by category,
   price range, brand, etc. Essential for e-commerce.

8. **Relevance tuning** goes beyond BM25: field boosting, function scores (popularity,
   recency), synonyms, and fuzzy matching for typo tolerance.

9. **Pagination**: Use `from/size` for shallow pages, `search_after` for deep
   pagination, and Scroll API for bulk exports.

10. **Scaling search** involves proper shard sizing (10-50GB), replica sets for
    read throughput, and index lifecycle management for time-series data.

11. **Real-world search** combines multiple stages: recall (find candidates),
    ranking (ML models), business rules (sponsorship), and personalization.

---

## Further Reading

- *Elasticsearch: The Definitive Guide* (official, available free online)
- *Introduction to Information Retrieval* by Manning, Raghavan, Schutze (free online at Stanford)
- Elasticsearch documentation: https://www.elastic.co/guide/
- Algolia blog on search relevance: https://www.algolia.com/blog/
- Meilisearch documentation: https://docs.meilisearch.com

---

*Previous: [Chapter 25 — Consensus Algorithms](25-consensus.md)*
