# Chapter 30: Design a Video Streaming Platform (YouTube / Netflix)

## 1. Problem Statement

Design a video streaming platform like YouTube or Netflix that allows users to upload
videos, stream them with adaptive quality, search for content, and receive personalized
recommendations. The system must handle massive storage, global content distribution,
and concurrent streaming for hundreds of millions of users.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Upload Video | Users can upload videos of various formats and sizes |
| FR2 | Stream Video | Users can watch videos with adaptive bitrate streaming |
| FR3 | Search | Search videos by title, description, tags, captions |
| FR4 | Recommendations | Personalized video suggestions for each user |
| FR5 | Comments/Likes | Users can comment on and like/dislike videos |
| FR6 | Channels | Users can create channels and subscribe |
| FR7 | Playlists | Users can create and share playlists |
| FR8 | Watch History | Track and display user's viewing history |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 1B total users, 100M DAU |
| NFR2 | Upload Volume | 500 hours of video uploaded per minute |
| NFR3 | Streaming Latency | Playback start < 2 seconds |
| NFR4 | Availability | 99.99% uptime for streaming |
| NFR5 | Global | Content served from edge locations worldwide |
| NFR6 | Quality | Support 360p to 4K resolution, adaptive bitrate |

---

## 4. Capacity Estimation

### 4.1 Upload Traffic

```
500 hours of video uploaded per minute

Video specs (average):
  Duration: 5 minutes average
  Original file: 500 MB average (before transcoding)

Upload rate:
  500 hours/min = 30,000 hours/hour = 720,000 hours/day
  720,000 hours / 5 min avg = 8.64M videos/day
  = 100 videos/sec upload rate

Storage per day (originals):
  8.64M videos * 500 MB = 4.32 PB/day (originals only!)
  
After transcoding (multiple resolutions + codecs):
  Typically 3-5x the original size
  ~15-20 PB/day total storage
```

### 4.2 Streaming Traffic

```
100M DAU * 5 videos/day * 5 min avg = 2.5B video views/day
= ~29,000 concurrent streams (average)
Peak: ~100,000 concurrent streams

Bandwidth per stream:
  SD (480p):  1.5 Mbps
  HD (1080p): 5 Mbps
  4K:         20 Mbps
  Average:    ~4 Mbps

Total egress bandwidth:
  Average: 29,000 * 4 Mbps = 116 Gbps
  Peak:    100,000 * 4 Mbps = 400 Gbps
  (Served primarily from CDN edge, not origin)
```

### 4.3 Metadata Storage

```
Per video metadata: ~2 KB (title, description, tags, stats)
8.64M videos/day * 2 KB = 17 GB/day metadata
Per year: ~6 TB metadata (easily fits in a database cluster)
```

---

## 5. Video Upload Pipeline

### 5.1 End-to-End Upload Flow

```
  User                Client App          Upload Service        Object Store
    |                     |                     |                    |
    |--select file------->|                     |                    |
    |                     |--request upload----->|                    |
    |                     |<--upload URL + ID----|                    |
    |                     |                     |                    |
    |                     |--upload chunks ----------------------------->|
    |                     |  (resumable,        |                    |
    |                     |   multipart)        |                    |
    |                     |                     |                    |
    |                     |--upload complete---->|                    |
    |                     |                     |--trigger pipeline-->|
    |                     |<--202 Accepted------|                    |
    |                     |   "Processing..."   |                    |

                    +------------------+
                    | Message Queue    |
                    | (video.uploaded) |
                    +--------+---------+
                             |
              +--------------+--------------+
              |              |              |
     +--------v----+  +-----v-------+  +---v-----------+
     | Transcoding |  | Thumbnail   |  | Content       |
     | Service     |  | Generator   |  | Moderation    |
     |             |  |             |  | (AI/ML)       |
     +------+------+  +------+------+  +-------+-------+
            |                |                  |
            v                v                  v
     +------------+   +------------+    +-------------+
     | Transcoded |   | Thumbnail  |    | Approval /  |
     | Segments   |   | Storage    |    | Rejection   |
     | to CDN     |   | (S3+CDN)  |    |             |
     +------------+   +------------+    +-------------+
```

### 5.2 Resumable Upload

```
Why resumable uploads?
  - Large files (multi-GB) frequently fail mid-upload
  - Poor network connections
  - User can pause and resume

Implementation (similar to Google's Resumable Upload Protocol):
  1. Client: POST /api/v1/upload/init -> returns upload_id
  2. Client splits file into 8MB chunks
  3. Client uploads each chunk: PUT /api/v1/upload/{upload_id}/chunk/{n}
  4. Server tracks which chunks received
  5. On failure, client queries: GET /api/v1/upload/{upload_id}/status
     Server responds with list of received chunks
  6. Client resumes from last missing chunk
  7. Client: POST /api/v1/upload/{upload_id}/complete
```

---

## 6. Video Transcoding

### 6.1 Why Transcode?

```
Users upload in various formats: MP4, AVI, MOV, MKV, WebM
Users watch on various devices: phones, tablets, TVs, browsers
Users have different bandwidths: 3G, 4G, WiFi, fiber

We need to transcode each video into:
  - Multiple resolutions: 360p, 480p, 720p, 1080p, 4K
  - Multiple codecs: H.264 (compatibility), H.265/HEVC (efficiency), VP9, AV1
  - Multiple bitrates per resolution (for adaptive streaming)
  - Audio: AAC, Opus at various bitrates
```

### 6.2 Transcoding Pipeline (DAG-based)

```
A video's transcoding is a Directed Acyclic Graph (DAG) of tasks:

  Original Video
       |
       +---> Extract Audio ---> Transcode Audio (AAC 128k, 64k)
       |
       +---> Split into segments (10-second chunks)
                |
                +---> Transcode 360p (H.264, 500kbps)
                +---> Transcode 480p (H.264, 1Mbps)
                +---> Transcode 720p (H.264, 2.5Mbps)
                +---> Transcode 1080p (H.264, 5Mbps)
                +---> Transcode 4K (H.265, 15Mbps)
                |
                +---> Each resolution segment transcoded in PARALLEL
                |
                +---> Generate manifest files (HLS .m3u8 / DASH .mpd)
                |
                +---> Upload all segments to CDN origin

  DAG Execution:
  +----------+     +-----------+     +----------+     +---------+
  | Extract  | --> | Split     | --> | Transcode| --> | Package |
  | metadata |     | segments  |     | (parallel|     | (HLS/   |
  |          |     |           |     |  per res)|     |  DASH)  |
  +----------+     +-----------+     +----------+     +---------+
       |                                                    |
       +---> Extract   +-----------+               +--------v--------+
             Audio --> | Transcode | ------------> | Upload to CDN   |
                       | Audio     |               | Origin (S3)     |
                       +-----------+               +-----------------+
```

### 6.3 Parallel Transcoding Architecture

```
  +------------------+
  | Task Scheduler   |  (Manages DAG execution)
  | (Airflow / Step  |
  |  Functions)      |
  +--------+---------+
           |
    +------v------+    +------+------+    +------+------+
    | Transcoding |    | Transcoding |    | Transcoding |
    | Worker Pool |    | Worker Pool |    | Worker Pool |
    | (GPU inst.) |    | (GPU inst.) |    | (GPU inst.) |
    | 360p tasks  |    | 720p tasks  |    | 1080p tasks |
    +-------------+    +-------------+    +-------------+

Worker Specs:
  - GPU-accelerated instances (NVIDIA T4/A100)
  - Each worker handles one resolution of one segment
  - 10-sec segment of 1080p: ~30 seconds to transcode
  - Horizontal scaling: spin up more workers during peak upload times
  
  Queues (per priority):
    High:   Premium/verified creators
    Medium: Regular uploads
    Low:    Re-transcoding old content
```

---

## 7. Adaptive Bitrate Streaming (ABR)

### 7.1 How ABR Works

```
The video player automatically adjusts quality based on:
  - Available bandwidth
  - Buffer health
  - Device capabilities

Protocols:
  HLS (HTTP Live Streaming) - Apple, widely supported
  DASH (Dynamic Adaptive Streaming over HTTP) - MPEG standard

How it works:
  1. Video split into small segments (2-10 seconds each)
  2. Each segment encoded at multiple bitrates/resolutions
  3. Manifest file lists all available segments and qualities
  4. Player downloads manifest, picks initial quality
  5. Player monitors download speed
  6. If bandwidth drops: switch to lower quality next segment
  7. If bandwidth improves: switch to higher quality

  Bandwidth   Quality
  ----+--------+----
      | 4K     |   High bandwidth detected
      |--------|
      | 1080p  |   Bandwidth dropped
      |--------|
      | 720p   |   Bandwidth dropped more
      |--------|
      | 1080p  |   Bandwidth recovered
      +--------+
```

### 7.2 HLS Manifest Example

```
#EXTM3U
#EXT-X-STREAM-INF:BANDWIDTH=500000,RESOLUTION=640x360
360p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=1000000,RESOLUTION=854x480
480p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=2500000,RESOLUTION=1280x720
720p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
1080p/playlist.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=15000000,RESOLUTION=3840x2160
4k/playlist.m3u8

Each resolution's playlist:
#EXTM3U
#EXT-X-TARGETDURATION:10
#EXTINF:10.0,
segment_001.ts
#EXTINF:10.0,
segment_002.ts
#EXTINF:10.0,
segment_003.ts
...
```

---

## 8. Video Storage Architecture

```
Three tiers of storage:

Tier 1: CDN Edge (Hot)
  - Most popular videos cached at 200+ edge locations globally
  - Serves 90%+ of streaming requests
  - TTL-based eviction, popularity-driven caching
  
Tier 2: CDN Origin / Regional Storage (Warm)
  - All transcoded videos stored in regional S3 buckets
  - 3-5 regions worldwide
  - CDN pulls from nearest origin on cache miss

Tier 3: Archive Storage (Cold)
  - Original uploaded files
  - Old videos with zero views
  - S3 Glacier / deep archive
  - Can re-transcode from original if needed

  +----------+     +-------------+     +---------------+
  | CDN Edge |<----| CDN Origin  |<----| Deep Archive  |
  | (200+    |     | (S3,        |     | (S3 Glacier,  |
  |  PoPs)   |     |  regional)  |     |  original     |
  | HOT      |     | WARM        |     |  files)       |
  +----------+     +-------------+     +---------------+
    90% hits         9% hits              1% (restore)
```

---

## 9. Streaming Flow (Watch Path)

```
User clicks "Play" on a video:

  Client          CDN Edge        CDN Origin       Video Metadata
    |                 |                |                |
    |--GET /video/123/metadata------->|                |
    |<--{title, manifest_url, ...}----|                |
    |                 |                |                |
    |--GET manifest.m3u8->|           |                |
    |                 |--cache miss?-->|                |
    |<--manifest------|<--manifest----|                |
    |                 |                |                |
    | (Player selects 720p based on bandwidth)         |
    |                 |                |                |
    |--GET 720p/seg_001.ts->|         |                |
    |<--segment data--------|         |                |
    |                 |                |                |
    |--GET 720p/seg_002.ts->|         |                |
    |<--segment data--------|         |                |
    |                 |                |                |
    | (Bandwidth drops, switch to 480p)                |
    |--GET 480p/seg_003.ts->|         |                |
    |<--segment data--------|         |                |
    |   ...                  |         |                |
```

---

## 10. Metadata Service

### 10.1 Video Metadata Schema

```sql
CREATE TABLE videos (
    video_id        BIGINT       PRIMARY KEY,
    channel_id      BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    tags            JSON,                    -- ["music", "rock", "live"]
    category        VARCHAR(50),
    duration_sec    INT,
    upload_status   ENUM('uploading', 'processing', 'ready', 'failed'),
    visibility      ENUM('public', 'unlisted', 'private'),
    manifest_url    VARCHAR(500),            -- HLS/DASH manifest
    thumbnail_urls  JSON,                    -- multiple sizes
    view_count      BIGINT       DEFAULT 0,
    like_count      BIGINT       DEFAULT 0,
    dislike_count   BIGINT       DEFAULT 0,
    comment_count   BIGINT       DEFAULT 0,
    created_at      TIMESTAMP,
    published_at    TIMESTAMP,
    INDEX idx_channel (channel_id, published_at DESC),
    INDEX idx_category (category, view_count DESC)
);

CREATE TABLE channels (
    channel_id      BIGINT       PRIMARY KEY,
    user_id         BIGINT       UNIQUE NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    subscriber_count BIGINT      DEFAULT 0,
    video_count     INT          DEFAULT 0,
    created_at      TIMESTAMP
);
```

### 10.2 Comments Schema (NoSQL - DynamoDB or Cassandra)

```
Table: comments
  Partition Key: video_id
  Sort Key: comment_id (Snowflake, time-sorted)

  Attributes:
    video_id     (S)
    comment_id   (S)   -- Snowflake ID for time ordering
    user_id      (N)
    content      (S)
    parent_id    (S)   -- for threaded replies (NULL if top-level)
    like_count   (N)
    created_at   (N)

  Query: Get latest comments for a video
    SELECT * FROM comments
    WHERE video_id = '123'
    ORDER BY comment_id DESC
    LIMIT 50;

  Why NoSQL for comments?
    - Massive scale (billions of comments)
    - Simple access pattern (by video_id)
    - No joins needed
    - Horizontal scaling
```

---

## 11. Thumbnail Generation

```
For each uploaded video, generate thumbnails:

  1. Extract frames at regular intervals (every 10% of duration)
  2. Apply scene detection to find visually interesting frames
  3. Generate 3 candidate thumbnails
  4. Optional: ML model ranks thumbnails by "click-through" prediction
  5. Creator can choose or upload custom thumbnail
  6. Generate multiple sizes:
     - Small:  120 x 90   (search results, sidebar)
     - Medium: 320 x 180  (grid view)
     - Large:  480 x 360  (featured)
     - HD:     1280 x 720 (video page, embeds)

  Pipeline:
    Video uploaded --> FFmpeg frame extraction --> ML ranking --> S3 + CDN
```

---

## 12. Search and Discovery

### 12.1 Search Architecture

```
Search Stack: Elasticsearch cluster

Indexed fields:
  - title (high weight)
  - description (medium weight)
  - tags (high weight)
  - channel name (medium weight)
  - auto-generated captions (low weight)
  - category

Ranking Signals:
  - Text relevance (BM25 score)
  - View count (popularity)
  - Recency (freshness boost)
  - Engagement rate (likes/views ratio)
  - Channel authority (subscriber count)
  - User's watch history (personalization)

  User query ---> Search Service ---> Elasticsearch ---> Ranked results
                                          |
                                     Index updated
                                     in near-real-time
                                     via Kafka consumer
```

### 12.2 Search Indexing Pipeline

```
  Video published ---> Kafka topic "video-published"
                          |
                    +-----v------+
                    | Search     |
                    | Indexer    |
                    | (consumer)|
                    +-----+------+
                          |
                    +-----v------+
                    | Elastic-   |
                    | search     |
                    | Cluster    |
                    +------------+
```

---

## 13. Recommendation Engine (High Level)

```
Two main approaches:

1. Collaborative Filtering:
   "Users who watched X also watched Y"
   - User-item interaction matrix
   - Matrix factorization (ALS, SVD)
   - Find similar users, recommend their top videos

2. Content-Based Filtering:
   "Videos similar to what you've watched"
   - Video feature vectors (category, tags, description embeddings)
   - Compute cosine similarity between videos
   - Recommend similar videos

3. Hybrid (YouTube's actual approach):
   Two-stage pipeline:
   
   Stage 1: Candidate Generation (narrow down from millions to hundreds)
     - Collaborative filtering
     - Content-based similarity
     - Trending/popular videos
     - Subscription feed
   
   Stage 2: Ranking (rank hundreds to show top 20-50)
     - Deep neural network
     - Features: user history, video features, context (time, device)
     - Optimizes for: watch time, engagement, diversity

  All videos          Candidate            Ranking           Top 20
  (millions)    ---> Generation  ---->    Model     ---->  Displayed
                     (hundreds)          (scored)
```

---

## 14. CDN Strategy for Global Distribution

```
CDN Architecture:

  +------------------+
  | Origin Servers   |  (S3 buckets in 3-5 regions)
  | (US, EU, Asia)   |
  +--------+---------+
           |
  +--------v---------+
  | CDN Mid-Tier     |  (Regional PoPs, ~20 locations)
  | Cache (warm)     |
  +--------+---------+
           |
  +--------v---------+
  | CDN Edge         |  (200+ PoPs worldwide)
  | Cache (hot)      |  Closest to users
  +------------------+

Cache Strategy:
  - Popular videos (top 10%) cached at ALL edges → 90% cache hit
  - Moderate videos cached at regional mid-tier
  - Long-tail videos served from origin (cache miss)
  
  Cache Key: {video_id}/{resolution}/{segment_number}
  Example:   v123/1080p/segment_042.ts

  TTL: 
    Popular videos: 24 hours at edge
    Others: 2 hours at edge, 24 hours at mid-tier

  Pre-warming:
    For anticipated viral content (new movie trailer, music video):
    Push to all edge locations BEFORE release
```

---

## 15. Video Deduplication

```
Problem: Users re-upload the same content (duplicates waste storage + transcoding)

Solution: Content fingerprinting

  1. During upload, compute perceptual hash of video:
     - Extract keyframes at fixed intervals
     - Compute pHash (perceptual hash) of each frame
     - Generate audio fingerprint (similar to Shazam)
  
  2. Compare fingerprints against database:
     - Exact match: reject as duplicate
     - Near match (>90% similarity): flag for review
     
  3. If duplicate detected:
     - Point to existing transcoded content
     - Save storage and transcoding costs

  Upload --> Fingerprint --> Check DB --> Duplicate?
                                            |
                                      YES: Reject/Link
                                      NO:  Proceed with pipeline
```

---

## 16. Copyright Detection (Content ID)

```
YouTube's Content ID system:

  1. Rights holders upload reference files (music, movies, etc.)
  2. System creates audio + video fingerprints of reference content
  3. Every uploaded video is fingerprinted and compared
  4. On match:
     a. BLOCK: Prevent the video from being published
     b. MONETIZE: Allow but divert ad revenue to rights holder
     c. TRACK: Allow and provide analytics to rights holder

  Reference DB:
    100M+ reference files → fingerprint database
    
  Matching Pipeline:
    New upload → Extract fingerprints → Compare against reference DB
                                            |
                                      Match found?
                                      YES: Apply rights holder's policy
                                      NO:  Publish normally

  This runs BEFORE the video becomes public (during processing stage)
```

---

## 17. View Count Architecture

```
Challenge: Accurately counting views at massive scale (billions/day)

Naive approach: UPDATE videos SET view_count = view_count + 1
  Problem: Hot videos get millions of concurrent increments → DB bottleneck

Solution: Approximate counting with eventual consistency

  1. Client sends "view" event
  2. Event published to Kafka topic "video-views"
  3. Stream processor (Flink) aggregates counts in time windows
  4. Periodically flush aggregated counts to database
  5. Cache current count in Redis for display

  Client ---> API ---> Kafka ---> Flink ---> DB (batch update)
                                    |
                                    +---> Redis (real-time display)

  Deduplication:
    - Don't count repeat views from same user within 30 seconds
    - Use Bloom filter or HyperLogLog for approximate unique counting
    - Flink dedup: {user_id, video_id, 30s_window} -> count once
```

---

## 18. Full System Architecture

```
+------------------------------------------------------------------------+
|                      CLIENTS (Web / iOS / Android / Smart TV)           |
+-----+-------------------------+---------------------------+------------+
      |                         |                           |
      | (upload)                | (stream)                  | (browse/search)
      |                         |                           |
+-----v--------+        +------v-------+            +------v-------+
| Upload       |        | CDN Edge     |            | API Gateway  |
| Service      |        | (200+ PoPs)  |            | (REST)       |
| (resumable)  |        +------+-------+            +------+-------+
+-----+--------+               |                           |
      |                 +------v-------+         +---------+---------+
      |                 | CDN Origin   |         |         |         |
+-----v--------+        | (S3 regional)|    +----v--+ +---v---+ +---v----+
| Object Store |        +--------------+    | Video | |Search | |Recom-  |
| (S3 - orig.) |                            | Meta  | |Svc    | |mend.   |
+--------------+                            | Svc   | |(ES)   | |Engine  |
      |                                     +---+---+ +---+---+ +---+----+
+-----v-----------+                             |         |         |
| Message Queue   |                         +---v---------v---------v---+
| (video.uploaded)|                         |      PostgreSQL           |
+-----+-----------+                         |   (metadata, users,      |
      |                                     |    channels)             |
+-----v-----------+                         +---------------------------+
| Transcoding     |
| Pipeline        |    +------------------+    +-------------------+
| (DAG scheduler  |    | Comment Service  |    | View Count        |
|  + GPU workers) |    | (DynamoDB)       |    | (Kafka + Flink    |
+-----------------+    +------------------+    |  + Redis)         |
      |                                        +-------------------+
+-----v-----------+
| Thumbnail       |    +------------------+
| Generator       |    | Content ID /     |
+-----------------+    | Copyright Check  |
                       +------------------+
+------------------+
| Notification     |   +------------------+
| Service          |   | Analytics        |
| (new video,      |   | (Spark/Flink     |
|  subscription)   |   |  + data lake)    |
+------------------+   +------------------+
```

---

## 19. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Streaming protocol | HLS | DASH | **Both** (HLS primary) | HLS has wider device support |
| Video codec | H.264 only | Multiple (H.264+H.265+AV1) | **Multiple** | Balance compatibility vs efficiency |
| Transcoding | On-demand | Pre-transcode all | **Pre-transcode** | Latency at play time matters most |
| CDN | Single provider | Multi-CDN | **Multi-CDN** | Redundancy + cost optimization |
| Comments DB | SQL | NoSQL (DynamoDB) | **NoSQL** | Scale, simple access pattern |
| View counting | Sync DB update | Async stream processing | **Async** | Can't block on every view |
| Thumbnails | User-uploaded only | Auto-generated + user | **Both** | Better UX, higher CTR |
| Search | SQL LIKE | Elasticsearch | **Elasticsearch** | Full-text search at scale |
| Recommendations | Rules-based | ML-based (deep learning) | **ML-based** | Much better quality |

---

## 20. Cost Optimization Strategies

```
1. Storage Tiering:
   - Hot: Popular videos on SSD-backed CDN edge
   - Warm: All transcoded on standard S3
   - Cold: Originals + rarely-viewed on S3 Glacier
   - Auto-tier based on view count (move to cold after 90 days of zero views)

2. Transcoding Optimization:
   - Only transcode to 4K if original is 4K+
   - Prioritize popular resolutions (720p, 1080p)
   - Use spot/preemptible GPU instances (60-70% cost savings)
   - Queue-based: transcode lower resolutions first (faster availability)

3. CDN Cost:
   - Multi-CDN strategy: route to cheapest CDN per region
   - Origin shield: reduce origin fetches with mid-tier cache
   - Long TTLs for video segments (content doesn't change)

4. Encoding Efficiency:
   - Two-pass encoding for better quality-per-bit
   - Per-title encoding: analyze content complexity, adjust bitrate
     Simple scene: lower bitrate sufficient
     Complex scene: higher bitrate needed
   - Saves 20-30% bandwidth vs fixed bitrate ladder
```

---

## 21. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. Video upload is a PIPELINE, not a single operation —          |
|    upload, transcode, moderate, distribute to CDN                |
|                                                                    |
| 2. Adaptive Bitrate Streaming (HLS/DASH) is essential —          |
|    split videos into segments, encode at multiple qualities      |
|                                                                    |
| 3. Transcoding is the most compute-intensive part —              |
|    use GPU workers, DAG-based parallel processing                |
|                                                                    |
| 4. CDN is the hero — 90%+ of streaming traffic served from      |
|    edge locations, not origin servers                             |
|                                                                    |
| 5. Three-tier storage: CDN edge (hot) → S3 (warm) → Glacier     |
|    (cold). Tier based on view frequency                          |
|                                                                    |
| 6. View counting at scale needs stream processing (Kafka +       |
|    Flink), not direct DB increments                              |
|                                                                    |
| 7. Recommendation engine is a two-stage pipeline:                |
|    candidate generation (broad) → ranking model (precise)        |
|                                                                    |
| 8. Content deduplication and copyright detection happen           |
|    BEFORE publishing — saves storage and avoids legal issues     |
|                                                                    |
| 9. Resumable uploads are critical — large files on poor          |
|    connections need retry capability                              |
|                                                                    |
| 10. Cost optimization (storage tiering, spot instances,           |
|     per-title encoding) is crucial at YouTube/Netflix scale      |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 29 - Chat System](./29-chat-system.md) | Next: [Chapter 31 - Ride-Sharing Service](./31-ride-sharing.md)*
