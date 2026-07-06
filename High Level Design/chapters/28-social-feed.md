# Chapter 28: Design a Social Media Feed (Twitter / X)

## 1. Problem Statement

Design a social media platform similar to Twitter/X where users can post short messages
(tweets), follow other users, and view a personalized home timeline. The system must handle
hundreds of millions of users, deliver timelines with low latency, and gracefully handle
the "celebrity problem" where a single user may have tens of millions of followers.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Post Tweet | Users can create tweets (280 chars + media) |
| FR2 | Follow/Unfollow | Users can follow and unfollow other users |
| FR3 | Home Timeline | Aggregated feed of tweets from followed users, sorted by time |
| FR4 | User Timeline | All tweets from a specific user |
| FR5 | Like/Retweet | Users can like and retweet posts |
| FR6 | Search | Full-text search for tweets and users |
| FR7 | Trending Topics | Display currently trending hashtags/topics |
| FR8 | Notifications | Notify users of likes, retweets, mentions, new followers |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 500M total users, 200M DAU |
| NFR2 | Availability | 99.99% uptime |
| NFR3 | Timeline Latency | Home timeline loads in < 200ms |
| NFR4 | Write Latency | Tweet post acknowledged in < 500ms |
| NFR5 | Consistency | Eventual consistency acceptable (seconds, not minutes) |
| NFR6 | Media Support | Images (up to 5MB), videos (up to 512MB) |

---

## 4. Capacity Estimation

### 4.1 Traffic

```
Users:
  500M total users, 200M DAU
  Average user follows 200 people

Tweets:
  200M DAU * 2 tweets/day avg = 400M tweets/day
  = 400,000,000 / 86,400 ~ 4,600 tweets/sec (writes)
  Peak: ~15,000 tweets/sec

Timeline Reads:
  200M DAU * 20 timeline loads/day = 4B timeline reads/day
  = 4,000,000,000 / 86,400 ~ 46,000 reads/sec
  Peak: ~150,000 reads/sec

Read:Write Ratio ~ 10:1 for timelines (but fan-out amplifies writes)
```

### 4.2 Storage

```
Tweet Storage:
  Each tweet: ~300 bytes (text + metadata)
  400M tweets/day * 300 bytes = 120 GB/day
  Per year: ~44 TB
  5 years: ~220 TB

Media Storage:
  10% of tweets have images: 40M images/day * 200KB avg = 8 TB/day
  1% have video: 4M videos/day * 50MB avg = 200 TB/day
  Total media: ~208 TB/day (object storage, CDN-cached)

Social Graph:
  500M users * 200 avg follows = 100B edges
  Each edge: 16 bytes (follower_id + followee_id)
  = ~1.6 TB
```

### 4.3 Timeline Cache

```
Each user's home timeline: top 800 tweet IDs cached
  Tweet ID: 8 bytes
  800 tweets * 8 bytes = 6.4 KB per user

Active users: 200M
Cache size: 200M * 6.4 KB = ~1.3 TB
  (Fits in a Redis cluster with ~20 nodes of 64GB each)
```

---

## 5. Data Model

### 5.1 Core Tables

```sql
-- Users Table
CREATE TABLE users (
    user_id       BIGINT       PRIMARY KEY,
    username      VARCHAR(15)  UNIQUE NOT NULL,
    display_name  VARCHAR(50),
    bio           VARCHAR(160),
    profile_img   VARCHAR(255),
    followers_count BIGINT     DEFAULT 0,
    following_count BIGINT     DEFAULT 0,
    is_verified   BOOLEAN      DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL
);

-- Tweets Table
CREATE TABLE tweets (
    tweet_id      BIGINT       PRIMARY KEY,  -- Snowflake ID (time-sorted)
    user_id       BIGINT       NOT NULL,
    content       VARCHAR(280),
    media_urls    JSON,                       -- array of media URLs
    reply_to      BIGINT,                     -- NULL if original tweet
    retweet_of    BIGINT,                     -- NULL if original tweet
    like_count    BIGINT       DEFAULT 0,
    retweet_count BIGINT       DEFAULT 0,
    reply_count   BIGINT       DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL,
    INDEX idx_user_time (user_id, created_at DESC)
);

-- Follows Table (Social Graph)
CREATE TABLE follows (
    follower_id   BIGINT       NOT NULL,
    followee_id   BIGINT       NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    PRIMARY KEY (follower_id, followee_id),
    INDEX idx_followee (followee_id, follower_id)
);

-- Likes Table
CREATE TABLE likes (
    user_id       BIGINT       NOT NULL,
    tweet_id      BIGINT       NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    PRIMARY KEY (user_id, tweet_id),
    INDEX idx_tweet (tweet_id, user_id)
);
```

### 5.2 Storage Choices

```
+-------------------+---------------------------+---------------------------+
| Data              | Storage                   | Reason                    |
+-------------------+---------------------------+---------------------------+
| User profiles     | MySQL / PostgreSQL        | Structured, relational    |
| Tweets            | MySQL (sharded by user_id)| Time-ordered per user     |
| Social graph      | MySQL or Graph DB (Neo4j) | Adjacency queries         |
| Timeline cache    | Redis (sorted sets)       | Fast reads, sorted by time|
| Media files       | S3 / Object Storage       | Blob storage + CDN        |
| Search index      | Elasticsearch             | Full-text search          |
| Analytics/Trends  | Apache Kafka + Flink      | Stream processing         |
+-------------------+---------------------------+---------------------------+
```

---

## 6. The Core Problem: Home Timeline Generation

The most critical design challenge is: **How do we generate a user's home timeline?**

A user follows 200 people on average. To build their home timeline, we need the most recent
tweets from all 200 people, merged and sorted. Three approaches:

### 6.1 Approach 1: Fan-Out on Read (Pull Model)

```
When User A requests their home timeline:
  1. Get list of users A follows: [B, C, D, ...]
  2. For each followed user, fetch their recent tweets
  3. Merge all tweets, sort by time
  4. Return top N tweets

  User A ---> Timeline Service ---> "Who does A follow?" ---> [B, C, D]
                    |
                    +---> Fetch tweets from B (recent 20)
                    +---> Fetch tweets from C (recent 20)
                    +---> Fetch tweets from D (recent 20)
                    |
                    +---> Merge & Sort ---> Return timeline

Pros:
  - Simple to implement
  - No write amplification
  - Works well for users with few follows

Cons:
  - SLOW at read time (merge N lists for every request)
  - If user follows 500 people, need 500 DB queries
  - Timeline load latency is HIGH (unacceptable at scale)
  - Can't meet <200ms SLA
```

### 6.2 Approach 2: Fan-Out on Write (Push Model)

```
When User B posts a tweet:
  1. Get all of B's followers: [A, C, D, ...] (could be millions)
  2. Push tweet_id into each follower's timeline cache
  3. When A requests timeline, just read from A's pre-built cache

  User B posts tweet ---> Fan-out Service ---> "Who follows B?" ---> [A, C, D, ...]
                              |
                              +---> Push tweet_id to A's timeline cache
                              +---> Push tweet_id to C's timeline cache
                              +---> Push tweet_id to D's timeline cache

  A's Timeline Cache (Redis Sorted Set):
    Key: "timeline:{user_A_id}"
    Score: tweet timestamp
    Member: tweet_id

    ZADD timeline:A <timestamp> <tweet_id>

  User A reads timeline:
    ZREVRANGE timeline:A 0 49  (get top 50 tweets by time, DESC)

Pros:
  - Read is BLAZING fast (just Redis ZREVRANGE)
  - Meet <200ms SLA easily
  - Pre-computed, ready to serve

Cons:
  - MASSIVE write amplification for celebrities
    Justin Bieber: 80M followers → 1 tweet = 80M cache writes!
    At 4,600 tweets/sec, some are from celebrities
  - Wasted writes for inactive users (who never check their timeline)
  - High memory usage for timeline caches
```

### 6.3 Approach 3: Hybrid (Twitter's Actual Approach)

```
Key Insight: Use PUSH for normal users, PULL for celebrities

Celebrity Threshold: users with > 10,000 followers

Normal User Posts:
  → Fan-out on write to all followers' timeline caches (PUSH)

Celebrity Posts:
  → Do NOT fan-out on write
  → Store in celebrity tweets cache only

When User A Reads Timeline:
  1. Read pre-built cache (normal follows' tweets)         [FAST]
  2. Fetch latest tweets from A's celebrity follows          [FEW queries]
  3. Merge the two sets and sort                            [SMALL merge]
  4. Return timeline

  +-------------------+          +-------------------+
  | Pre-built Cache   |          | Celebrity Tweets  |
  | (pushed tweets    | + merge  | (pulled on read,  |
  |  from normal      |<-------->|  only 5-10 celeb  |
  |  users A follows) |          |  accounts)        |
  +-------------------+          +-------------------+
           |                              |
           +-------> MERGED TIMELINE <----+
                     (sorted by time)

Why this works:
  - Most users follow ~5-10 celebrities max
  - Pulling from 5-10 celebrity caches is fast
  - Eliminates 80M write fan-out for celebrity tweets
  - Normal users (99%) still get push model (fast reads)
```

---

## 7. Tweet Posting Flow

```
  Client                API Gateway        Tweet Service       Fan-out Service
    |                       |                   |                    |
    |--- POST /tweet ------>|                   |                    |
    |                       |--- validate ----->|                    |
    |                       |                   |--- store tweet --->| DB
    |                       |                   |--- media upload -->| S3
    |                       |                   |                    |
    |                       |<-- 201 Created ---|                    |
    |<-- 201 Created -------|                   |                    |
    |                       |                   |                    |
    |                       |            (async via Kafka)           |
    |                       |                   |--- fan-out msg -->|
    |                       |                   |                    |
    |                       |                   |          +---------v----------+
    |                       |                   |          | For each follower: |
    |                       |                   |          | ZADD timeline:X    |
    |                       |                   |          | <ts> <tweet_id>    |
    |                       |                   |          +--------------------+
```

### 7.1 Fan-Out Service Detail

```
Fan-Out Service:
  1. Consume tweet event from Kafka
  2. Check if author is a celebrity (followers > 10K threshold)
     - If YES: store in celebrity cache only, skip fan-out
     - If NO: proceed with fan-out
  3. Fetch author's follower list (paginated)
  4. For each follower:
     a. Check if follower is active (DAU) — skip inactive users
     b. ZADD timeline:{follower_id} {tweet_timestamp} {tweet_id}
     c. ZREMRANGEBYRANK timeline:{follower_id} 0 -(MAX_TIMELINE_SIZE+1)
        (trim to keep only last 800 tweets)
  5. Update search index (Elasticsearch)
  6. Update trending topics aggregator
```

---

## 8. Timeline Service Architecture

```
  +----------+    GET /timeline     +------------------+
  |  Client  | ------------------> | Timeline Service  |
  +----------+                     +--------+---------+
                                            |
                              +-------------+-------------+
                              |                           |
                    +---------v---------+       +---------v---------+
                    | User's Timeline   |       | Celebrity Tweets  |
                    | Cache (Redis)     |       | Cache (Redis)     |
                    | ZREVRANGE         |       | ZREVRANGE per     |
                    | timeline:{uid}    |       | celeb followed    |
                    | 0 49              |       |                   |
                    +---------+---------+       +---------+---------+
                              |                           |
                              +-------------+-------------+
                                            |
                                    +-------v--------+
                                    | Merge & Sort   |
                                    | by timestamp   |
                                    +-------+--------+
                                            |
                                    +-------v--------+
                                    | Hydrate Tweets |
                                    | (fetch full    |
                                    |  tweet objects) |
                                    +-------+--------+
                                            |
                                    +-------v--------+
                                    | Hydrate Users  |
                                    | (author info,  |
                                    |  profile pics)  |
                                    +-------+--------+
                                            |
                                    +-------v--------+
                                    | Return to      |
                                    | Client         |
                                    +----------------+
```

---

## 9. Media Storage

### 9.1 Image Upload Flow

```
1. Client requests a pre-signed upload URL
2. Client uploads image directly to S3
3. S3 triggers Lambda/worker to:
   a. Generate thumbnails (150x150, 300x300, 600x600)
   b. Run content moderation (nudity/violence detection)
   c. Store all sizes in S3
   d. Update tweet record with media URLs
4. CDN serves images from edge locations

  Client --> Pre-signed URL --> S3 (original)
                                  |
                            +-----v------+
                            | Image      |
                            | Processing |
                            +-----+------+
                                  |
                     +------------+------------+
                     |            |            |
                  Thumb-150   Thumb-300   Thumb-600
                     |            |            |
                     +------+-----+-----+-----+
                            |           |
                         S3 Bucket    CDN
```

### 9.2 Video Upload

```
Videos follow a similar pipeline but with transcoding:
  Upload → Transcode (multiple resolutions) → CDN distribution
  (Detailed in Chapter 30: Video Streaming)
```

---

## 10. Social Graph Storage

### 10.1 Graph Representation

```
Storage: Adjacency list in sharded MySQL or dedicated graph store

For "who does User A follow?":
  SELECT followee_id FROM follows WHERE follower_id = A;
  (Sharded by follower_id for fast read)

For "who follows User A?" (fan-out query):
  SELECT follower_id FROM follows WHERE followee_id = A;
  (Secondary index or separate table sharded by followee_id)

Optimization: Cache follow lists in Redis
  Key: "following:{user_id}" -> Set of followee_ids
  Key: "followers:{user_id}" -> Set of follower_ids (for small accounts)
  
  For large accounts (celebrities), paginate follower reads from DB
```

### 10.2 Social Graph at Scale

```
100 billion edges (follow relationships)
Each edge: ~16 bytes
Raw storage: ~1.6 TB

Sharding Strategy:
  - Shard follows table by follower_id
  - This optimizes "who do I follow?" queries
  - For "who follows me?", maintain reverse index
    sharded by followee_id

  +------------------+     +------------------+
  | follows_by_user  |     | followers_of     |
  | (shard by        |     | (shard by        |
  |  follower_id)    |     |  followee_id)    |
  |                  |     |                  |
  | follower | ee    |     | followee | er    |
  | A        | B     |     | B        | A     |
  | A        | C     |     | C        | A     |
  +------------------+     +------------------+
```

---

## 11. Search and Trending Topics

### 11.1 Search Architecture

```
Search Index: Elasticsearch cluster
  - Tweets indexed by content, hashtags, user mentions
  - Users indexed by username, display_name, bio
  - Near-real-time indexing via Kafka consumer

Search Flow:
  Client ---> Search Service ---> Elasticsearch ---> Results
                                      |
                                   Ranking:
                                   - Relevance (TF-IDF/BM25)
                                   - Recency
                                   - Engagement (likes, retweets)
                                   - Author authority (verified, followers)
```

### 11.2 Trending Topics

```
Pipeline:
  Tweet stream ---> Kafka ---> Flink/Storm ---> Trending Topics

Trending Algorithm:
  1. Extract hashtags and keywords from tweet stream
  2. Count frequency in sliding time windows (1hr, 4hr, 24hr)
  3. Calculate "velocity" (rate of increase, not just raw count)
  4. Filter by region/country for local trends
  5. Apply editorial filtering (remove sensitive content)
  6. Cache top 50 trends per region in Redis (TTL: 5 min)

  +--------+    +-------+    +----------+    +--------+    +-------+
  | Tweets | -> | Kafka | -> | Flink    | -> | Redis  | -> | API   |
  | Stream |    |       |    | (count & |    | (top   |    | Serve |
  |        |    |       |    |  rank)   |    | trends)|    |       |
  +--------+    +-------+    +----------+    +--------+    +-------+
```

---

## 12. Notification Integration

```
Events that trigger notifications:
  - Someone likes your tweet
  - Someone retweets your tweet
  - Someone follows you
  - Someone mentions you (@username)
  - Someone replies to your tweet

Flow:
  Event (like/retweet/follow/mention)
    ---> Kafka topic "notifications"
    ---> Notification Service
    ---> Check user preferences (push, email, in-app)
    ---> Route to appropriate channel:
         - Push: APNs (iOS) / FCM (Android)
         - In-app: Write to notification inbox (DB)
         - Email: SendGrid/SES (for digest emails)

  (Detailed in Chapter 32: Notification System)
```

---

## 13. Full System Architecture

```
+------------------------------------------------------------------------+
|                         CLIENTS (Web / iOS / Android)                   |
+-----------------------------------+------------------------------------+
                                    |
                            +-------v--------+
                            |   API Gateway  |
                            | (Rate Limit,   |
                            |  Auth, Route)  |
                            +-------+--------+
                                    |
          +----------+--------------+--------------+----------+
          |          |              |              |          |
    +-----v----+ +--v---+  +------v------+ +-----v----+ +---v-------+
    | Tweet    | | User | | Timeline    | | Search  | | Notific-  |
    | Service  | | Svc  | | Service     | | Service | | ation Svc |
    +----+-----+ +--+---+ +------+------+ +----+----+ +-----+-----+
         |          |             |              |           |
         |    +-----v------+     |         +----v----+      |
         |    | User DB    |     |         | Elastic |      |
         |    | (MySQL)    |     |         | Search  |      |
         |    +------------+     |         +---------+      |
         |                       |                          |
    +----v---------+    +--------v---------+    +-----------v-+
    | Tweet DB     |    | Timeline Cache   |    | Notification|
    | (MySQL,      |    | (Redis Cluster)  |    | Queue       |
    |  sharded)    |    |                  |    | (Kafka)     |
    +--------------+    | +timeline:{uid}  |    +-------------+
         |              | (sorted set of   |
         |              |  tweet_ids)      |
    +----v---------+    +--------+---------+
    | Media Store  |             |
    | (S3 + CDN)   |    +--------v---------+
    +--------------+    | Celebrity Cache  |
                        | (Redis)          |
         +---------+    +------------------+
         | Social  |
         | Graph   |    +------------------+
         | DB      |    | Fan-out Workers  |
         | (MySQL/ |    | (consume from    |
         |  Redis) |    |  Kafka, push to  |
         +---------+    |  timeline caches)|
                        +------------------+

    +------------------+    +------------------+
    | Trending Topics  |    | Analytics        |
    | (Flink + Redis)  |    | Pipeline         |
    +------------------+    +------------------+
```

---

## 14. User Timeline vs Home Timeline

```
User Timeline (Profile page - "What has User B posted?"):
  - Simple query: SELECT * FROM tweets WHERE user_id = B ORDER BY created_at DESC
  - Sharded by user_id, so single shard query
  - Cache in Redis: "user_timeline:{user_id}" 
  - Invalidate on new tweet

Home Timeline (Feed - "What should User A see?"):
  - Complex: aggregation of tweets from all followed users
  - Hybrid approach (push normal + pull celebrity)
  - Cached in Redis sorted set: "timeline:{user_id}"
  - Updated by fan-out service on new tweets
```

---

## 15. Tweet ID Generation

```
Need: Globally unique, time-sortable IDs (for timeline ordering)

Solution: Snowflake-like ID generation

  +---+--------------------+----------+------------+
  | 1 |   41 bits          | 10 bits  | 12 bits    |
  | 0 | timestamp (ms)     | machine  | sequence   |
  |   | since epoch        | ID       | number     |
  +---+--------------------+----------+------------+
  
  Total: 64 bits (fits in BIGINT)
  
  - Timestamp: milliseconds since custom epoch
    41 bits → ~69 years
  - Machine ID: 10 bits → 1024 machines
  - Sequence: 12 bits → 4096 IDs per ms per machine
  
  Throughput: 1024 machines * 4096/ms = 4M IDs/sec (way more than needed)
  
  Benefit: IDs are sortable by time without additional timestamp column
```

---

## 16. Scaling Considerations

### 16.1 Database Sharding

```
Tweets Table:
  Shard by user_id (all tweets from same user on same shard)
  - Optimizes user timeline queries
  - Use consistent hashing for shard assignment

Social Graph:
  Two copies: shard by follower_id AND shard by followee_id
  - Optimizes both "who do I follow" and "who follows me"

Users Table:
  Shard by user_id
  - Username lookups via global secondary index or lookup table
```

### 16.2 Caching Layers

```
Layer 1: CDN (CloudFront/Akamai)
  - Static assets (profile images, media)
  - Cache trending content at edge

Layer 2: Application Cache (Redis Cluster)
  - Timeline caches (sorted sets)
  - User profile cache
  - Social graph cache (follow lists)
  - Trending topics cache

Layer 3: Database Query Cache
  - MySQL query cache (limited usefulness)
  - Read replicas for read-heavy queries
```

### 16.3 Handling Hotspots

```
Celebrity Problem:
  - Don't fan-out celebrity tweets (hybrid approach)
  - Celebrity profile pages: cache aggressively
  - Viral tweets: cache in separate "hot tweets" cache
  
Thundering Herd (major event, everyone refreshes):
  - Cache stampede protection (lock + single flight)
  - Pre-warm caches before known events
  - Rate limit timeline refreshes per user
```

---

## 17. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Timeline generation | Fan-out on read | Fan-out on write | **Hybrid** | Best of both worlds |
| Celebrity threshold | 1K followers | 10K followers | **10K** | Balances read/write load |
| Tweet storage | NoSQL | Sharded MySQL | **Sharded MySQL** | Proven at Twitter's scale |
| Timeline cache | Redis sorted set | Memcached list | **Redis sorted set** | Native sorted operations |
| ID generation | UUID | Snowflake | **Snowflake** | Time-sortable, compact |
| Consistency | Strong | Eventual | **Eventual** | Acceptable for social media |
| Media storage | Inline in DB | Object storage | **Object storage (S3)** | Scalable, CDN-friendly |
| Search engine | Solr | Elasticsearch | **Elasticsearch** | Better real-time indexing |

---

## 18. Failure Scenarios

```
Scenario 1: Fan-out Service falls behind
  Mitigation:
  - Kafka provides buffering (retain messages for hours/days)
  - Scale fan-out workers horizontally
  - Priority queue: VIP users' fan-outs processed first
  - Skip inactive users' timeline caches

Scenario 2: Redis timeline cache node dies
  Mitigation:
  - Redis Cluster with replication (1 primary + 2 replicas)
  - On cache miss, rebuild timeline from DB (slow but correct)
  - Async background job rebuilds timeline cache for affected users

Scenario 3: Tweet DB shard goes down
  Mitigation:
  - MySQL with semi-synchronous replication
  - Promote replica to primary
  - Missing tweets temporarily invisible (eventual consistency)

Scenario 4: Viral tweet causes read storm
  Mitigation:
  - Hot tweet cache (separate from timeline cache)
  - CDN caching for tweet content
  - Rate limiting per-user timeline refreshes
```

---

## 19. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. Home timeline is the HARDEST problem — use a HYBRID approach  |
|    (push for normal users, pull for celebrities)                  |
|                                                                    |
| 2. Fan-out on write gives O(1) read latency but has massive      |
|    write amplification for celebrity tweets                       |
|                                                                    |
| 3. The "celebrity problem" must be explicitly addressed —         |
|    80M fan-out writes per tweet is not feasible                   |
|                                                                    |
| 4. Redis sorted sets are perfect for timeline caching             |
|    (ZADD, ZREVRANGE operations)                                   |
|                                                                    |
| 5. Snowflake IDs give time-sortable, unique, 64-bit IDs          |
|    without coordination overhead                                  |
|                                                                    |
| 6. Eventual consistency is FINE for social media — users          |
|    won't notice a 2-second delay in timeline updates              |
|                                                                    |
| 7. Media must be separated from tweet data — use S3 + CDN        |
|                                                                    |
| 8. Fan-out is async (Kafka) — tweet posting returns instantly,   |
|    fan-out happens in background workers                          |
|                                                                    |
| 9. Search and trending are separate subsystems with their own     |
|    dedicated infrastructure (Elasticsearch + Flink)               |
|                                                                    |
| 10. Always design for the hot path: timeline reads at 150K/sec   |
|     must be served from cache, never from database                |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 27 - URL Shortener](./27-url-shortener.md) | Next: [Chapter 29 - Chat System](./29-chat-system.md)*
