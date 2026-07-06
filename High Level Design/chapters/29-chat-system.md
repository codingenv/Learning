# Chapter 29: Design a Chat System (WhatsApp / Slack)

## 1. Problem Statement

Design a real-time messaging system like WhatsApp or Slack that supports one-on-one
conversations, group chats, online/offline presence, read receipts, push notifications,
and media sharing. The system must deliver messages with extremely low latency, handle
millions of concurrent connections, and ensure no messages are ever lost.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | 1-on-1 Chat | Send and receive messages between two users |
| FR2 | Group Chat | Create groups, add/remove members, send messages (up to 500 members) |
| FR3 | Online Status | Show online/offline/last-seen status for contacts |
| FR4 | Read Receipts | Double-check marks: delivered (to server), read (by recipient) |
| FR5 | Push Notifications | Notify offline users of new messages |
| FR6 | Media Sharing | Send images, videos, documents, voice messages |
| FR7 | Message History | Persistent message storage, scroll back through history |
| FR8 | Multi-device Sync | Messages synced across phone, tablet, desktop |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 500M total users, 50M DAU, 10M concurrent connections |
| NFR2 | Latency | Message delivery < 100ms for online users |
| NFR3 | Reliability | Zero message loss — guaranteed delivery |
| NFR4 | Ordering | Messages within a conversation must be ordered correctly |
| NFR5 | Availability | 99.99% uptime |
| NFR6 | Privacy | End-to-end encryption for 1-on-1 chats |

---

## 4. Capacity Estimation

### 4.1 Traffic

```
Users: 500M total, 50M DAU, 10M concurrent connections

Messages:
  50M DAU * 40 messages/day = 2B messages/day
  = 2,000,000,000 / 86,400 ~ 23,000 messages/sec
  Peak: ~70,000 messages/sec

Group Messages:
  Average group size: 10 members
  If 20% of messages are group messages:
    400M group messages/day * 10 fan-out = 4B delivery events/day
    ~ 46,000 deliveries/sec additional
```

### 4.2 Storage

```
Message Storage:
  Each message: ~200 bytes (text + metadata)
  2B messages/day * 200 bytes = 400 GB/day
  Per year: ~146 TB
  5 years: ~730 TB

Media Storage:
  5% of messages have media
  100M media messages/day * 500KB avg = 50 TB/day
  (Stored in object storage, not chat DB)

Connection State:
  10M concurrent connections
  Each connection state: ~500 bytes
  = 5 GB (fits in memory across WS servers)
```

### 4.3 Bandwidth

```
Incoming messages: 23,000/sec * 200 bytes = 4.6 MB/sec
Outgoing messages: 23,000/sec * 200 bytes * avg 1.5 recipients = 6.9 MB/sec
  (Factor in group fan-out increases this)
Media bandwidth: separate path via CDN
```

---

## 5. Communication Protocol

### 5.1 Why WebSocket?

```
Protocol Comparison:

+------------------+------------------+------------------+------------------+
| Feature          | HTTP Polling     | Long Polling     | WebSocket        |
+------------------+------------------+------------------+------------------+
| Connection       | New per request  | Held open        | Persistent       |
| Latency          | High (interval)  | Medium           | Very Low (<10ms) |
| Server push      | No               | Simulated        | Native           |
| Overhead         | High (headers)   | Medium           | Low (framing)    |
| Bidirectional    | No               | No               | Yes              |
| Scalability      | Poor             | OK               | Excellent        |
+------------------+------------------+------------------+------------------+

Decision: WebSocket for primary communication
          Long polling as fallback for restricted networks
          HTTP for non-real-time operations (login, profile, history)
```

### 5.2 Connection Lifecycle

```
1. Client authenticates via HTTPS (gets JWT token)
2. Client opens WebSocket connection to gateway
   ws://chat.example.com/ws?token=<jwt>
3. Server validates token, establishes connection
4. Server registers connection in Connection Registry
5. Bidirectional messages flow
6. Heartbeat every 30 seconds to keep connection alive
7. On disconnect, mark user offline after 30s grace period

  Client                    WS Gateway             Connection Registry
    |                           |                         |
    |--- HTTPS /auth ---------> |                         |
    |<-- JWT token -------------|                         |
    |                           |                         |
    |--- WS handshake -------->|                         |
    |    (upgrade: websocket)  |                         |
    |<-- 101 Switching --------|                         |
    |                           |                         |
    |                           |--- register ----------->|
    |                           |    {user_id: conn_id,   |
    |                           |     ws_server: srv-3}   |
    |                           |                         |
    |<-- connected ------------|                         |
    |                           |                         |
    |<-- ping (30s) -----------|                         |
    |--- pong ---------------->|                         |
```

---

## 6. Connection Management

### 6.1 WebSocket Server Fleet

```
10M concurrent connections
Each WS server handles ~50K connections (with proper tuning)
Need: 10M / 50K = 200 WebSocket servers

Connection Registry (Redis):
  Maps user_id -> {ws_server_id, connection_id}
  
  Key: "conn:{user_id}"
  Value: {"server": "ws-server-47", "conn_id": "abc123", "connected_at": 1705312200}

  When sending a message to User B:
    1. Lookup "conn:{user_B_id}" in Redis
    2. If exists: route message to ws-server-47
    3. If not exists: user is offline, queue for push notification

Load Balancing:
  - Sticky sessions (client reconnects to same server when possible)
  - Consistent hashing by user_id for initial assignment
  - Health checks remove unhealthy servers from rotation
```

### 6.2 Architecture of WS Server Fleet

```
                    +------------------+
                    | Load Balancer    |
                    | (L4 / TCP-aware) |
                    +--------+---------+
                             |
          +------------------+------------------+
          |                  |                  |
  +-------v------+  +-------v------+  +-------v------+
  | WS Server 1  |  | WS Server 2  |  | WS Server N  |
  | (50K conns)  |  | (50K conns)  |  | (50K conns)  |
  +-------+------+  +-------+------+  +-------+------+
          |                  |                  |
          +------------------+------------------+
                             |
                    +--------v---------+
                    | Connection       |
                    | Registry (Redis) |
                    | {user -> server} |
                    +------------------+
```

---

## 7. Message Flow: 1-on-1 Chat

### 7.1 Both Users Online

```
Alice sends "Hello" to Bob (both online):

  Alice       WS-Server-3     Chat Service     WS-Server-7       Bob
    |              |                |                |              |
    |--"Hello"--->|                |                |              |
    |              |--store msg--->|                |              |
    |              |               |--write to DB-->|              |
    |              |               |--ack---------->|              |
    |              |<--msg stored--|                |              |
    |<--sent ack---|               |                |              |
    |              |               |                |              |
    |              |               |--route to Bob->|              |
    |              |               | (lookup conn   |              |
    |              |               |  registry)     |              |
    |              |               |                |--"Hello"--->|
    |              |               |                |<--delivered--|
    |              |               |<--delivered----|              |
    |              |<--delivered---|                |              |
    |<--delivered--|               |                |              |
    |              |               |                |              |
    |   (Bob reads the message)    |                |              |
    |              |               |                |<--read------|
    |              |               |<--read---------|              |
    |              |<--read--------|                |              |
    |<--read-------|               |                |              |

Message States:
  SENT      -> Server received (single check mark)
  DELIVERED -> Recipient's device received (double check mark)
  READ      -> Recipient opened the conversation (blue check marks)
```

### 7.2 Recipient Offline

```
Alice sends "Hello" to Bob (Bob is offline):

  Alice       WS-Server-3     Chat Service     Push Service    Bob's Phone
    |              |                |                |              |
    |--"Hello"--->|                |                |              |
    |              |--store msg--->|                |              |
    |              |               |--write to DB-->|              |
    |<--sent ack---|               |                |              |
    |              |               |--lookup Bob--->|              |
    |              |               |  (offline!)    |              |
    |              |               |--push notify-->|              |
    |              |               |                |--APNs/FCM-->|
    |              |               |                |              |
    | (Later, Bob comes online)    |                |              |
    |              |               |                |    Bob       |
    |              |               |                |     |        |
    |              |               |<--sync req-----|     |        |
    |              |               |--undelivered-->|     |        |
    |              |               |   messages     |     |        |
    |              |               |                |--msgs------->|
    |              |               |<--delivered----|              |
    |<--delivered--|               |                |              |
```

---

## 8. Message Storage

### 8.1 Storage Model: Conversation-Based

```
Approach: Store messages per conversation (not per user)

Each conversation has a unique conversation_id:
  1-on-1: hash(min(user_A, user_B), max(user_A, user_B))
  Group:  group_id

Messages stored as time-series within a conversation:

  +-------------------+
  | Conversation:     |
  | conv_abc123       |
  +-------------------+
  | msg_1 | 10:00:01  | Alice: "Hey!"
  | msg_2 | 10:00:03  | Bob:   "Hi there"
  | msg_3 | 10:00:05  | Alice: "How are you?"
  | msg_4 | 10:00:08  | Bob:   "Good, thanks!"
  +-------------------+
```

### 8.2 Database Choice: Wide-Column Store (Cassandra / HBase)

```
Why Wide-Column Store?

| Requirement          | Why Cassandra/HBase                    |
|---------------------|----------------------------------------|
| Write-heavy          | Optimized for sequential writes        |
| Time-series data     | Excellent for time-ordered messages    |
| Horizontal scaling   | Easy to add nodes                      |
| High availability    | Multi-DC replication                   |
| Range queries        | Efficient "get messages from X to Y"   |
| No complex joins     | Messages don't need joins              |

Cassandra Table Design:

  CREATE TABLE messages (
      conversation_id  TEXT,
      message_id       TIMEUUID,      -- time-sorted UUID
      sender_id        BIGINT,
      content          TEXT,
      media_url        TEXT,
      message_type     TEXT,           -- 'text', 'image', 'video', etc.
      created_at       TIMESTAMP,
      PRIMARY KEY (conversation_id, message_id)
  ) WITH CLUSTERING ORDER BY (message_id DESC);

  -- Fetch latest 50 messages:
  SELECT * FROM messages 
  WHERE conversation_id = 'conv_abc123' 
  LIMIT 50;

  -- Pagination (scroll back):
  SELECT * FROM messages
  WHERE conversation_id = 'conv_abc123'
    AND message_id < <last_seen_message_id>
  LIMIT 50;
```

### 8.3 Conversation Metadata

```sql
-- Stored in MySQL/PostgreSQL (relational)
CREATE TABLE conversations (
    conversation_id   VARCHAR(64)  PRIMARY KEY,
    type              ENUM('direct', 'group'),
    group_name        VARCHAR(100),
    group_icon_url    VARCHAR(255),
    created_by        BIGINT,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP     -- last message timestamp
);

CREATE TABLE conversation_members (
    conversation_id   VARCHAR(64)  NOT NULL,
    user_id           BIGINT       NOT NULL,
    role              ENUM('admin', 'member'),
    joined_at         TIMESTAMP,
    last_read_msg_id  TIMEUUID,    -- for read receipts
    muted_until       TIMESTAMP,
    PRIMARY KEY (conversation_id, user_id),
    INDEX idx_user_convs (user_id, conversation_id)
);

-- "User's conversation list" query:
SELECT c.* FROM conversations c
JOIN conversation_members cm ON c.conversation_id = cm.conversation_id
WHERE cm.user_id = ?
ORDER BY c.updated_at DESC;
```

---

## 9. Message ID Generation

### 9.1 Requirements for Message IDs

```
1. Globally unique
2. Time-sortable (for message ordering within a conversation)
3. Generated without coordination (distributed)
4. Compact (minimize storage)
```

### 9.2 Snowflake-like ID

```
+---+----------------------+----------+------------+
| 1 | 41 bits              | 10 bits  | 12 bits    |
| 0 | timestamp (ms)       | server   | sequence   |
|   | since custom epoch   | ID       | number     |
+---+----------------------+----------+------------+

Total: 64 bits

Properties:
  - Time-sortable: higher IDs = later messages
  - Unique: server_id + sequence prevents collisions
  - Fast: ~4M IDs/sec per server
  - Compact: 8 bytes

Alternative: Cassandra TIMEUUID (128 bits, time-based UUID v1)
  - Native Cassandra support
  - Auto time-ordered
  - Slightly larger but simpler to use
```

---

## 10. Group Chat: Fan-Out to Members

### 10.1 Group Message Flow

```
Alice sends message to Group "Engineering" (50 members):

1. Alice's WS server receives the message
2. Chat Service validates Alice is a group member
3. Store message once in messages table (conversation_id = group_id)
4. Lookup all group members from conversation_members table
5. For each member:
   a. Check Connection Registry: is member online?
   b. ONLINE: Route message to their WS server
   c. OFFLINE: Queue push notification
6. Send delivery acknowledgment back to Alice

  Alice    Chat Service    Group Service     Connection Registry    Members
    |           |               |                    |                |
    |--msg---->|               |                    |                |
    |           |--store msg-->| (Cassandra)        |                |
    |           |--get members->|                    |                |
    |           |<--[B,C,D...]-|                    |                |
    |           |                                    |                |
    |           |---lookup B ------------------>     |                |
    |           |<--B: ws-server-7, online ----->    |                |
    |           |---route msg to ws-server-7 ------->|---msg to B -->|
    |           |                                    |                |
    |           |---lookup C ------------------>     |                |
    |           |<--C: offline ------------------    |                |
    |           |---push notification to C --------->|  (APNs/FCM)   |
    |           |                                    |                |
    |           | ... (repeat for all members) ...   |                |
    |<--sent --|                                    |                |
```

### 10.2 Optimizing Group Fan-Out

```
For large groups (100+ members):
  - Use async fan-out via message queue (Kafka)
  - Don't block the sender while delivering to all members
  - Batch lookups to Connection Registry
  - Group members on same WS server: single delivery

  Chat Service ---> Kafka topic "group-fanout" ---> Fan-out Workers
                                                        |
                                                  +-----------+
                                                  | Batch     |
                                                  | lookup    |
                                                  | online    |
                                                  | members   |
                                                  +-----------+
                                                        |
                                              +---------+---------+
                                              |                   |
                                        Online members      Offline members
                                        (route to WS)      (push notification)
```

---

## 11. Presence / Online Status Service

### 11.1 Heartbeat-Based Presence

```
How it works:
  1. When user connects via WebSocket, mark as "online"
  2. Client sends heartbeat every 30 seconds
  3. Server updates "last_heartbeat" timestamp
  4. If no heartbeat for 60 seconds, mark as "offline"
  5. Broadcast status change to user's contacts

Presence Store (Redis):
  Key: "presence:{user_id}"
  Value: {"status": "online", "last_seen": 1705312200, "ws_server": "ws-47"}
  TTL: 90 seconds (auto-expire if no heartbeat)

  Client heartbeat -> Server -> Redis SETEX "presence:{user_id}" 90 {...}
```

### 11.2 Broadcasting Status Changes

```
When User A comes online:
  1. Get A's contact list (frequent contacts, not all 500M users!)
  2. For each contact who is currently online:
     - Notify them that A is now online
  3. Get A's online contacts:
     - Send A the list of currently-online contacts

Optimization:
  - Only broadcast to "frequent contacts" (people A chats with regularly)
  - Don't broadcast in large groups (too much traffic)
  - Rate-limit presence updates (debounce: max 1 update per 10 seconds)
  - For group chats: fetch presence on-demand when user opens the group

  +----------+    heartbeat    +----------+     update     +--------+
  |  Client  | -------------> | Presence | ------------> | Redis  |
  |          |                | Service  |               |        |
  +----------+                +----+-----+               +--------+
                                   |
                              (status changed?)
                                   |
                         +---------v---------+
                         | Notification to   |
                         | online contacts   |
                         | via WebSocket     |
                         +-------------------+
```

---

## 12. Read Receipts and Delivery Receipts

```
Message Lifecycle:

  SENT ---------> DELIVERED ---------> READ
  (server ack)    (device received)    (user opened chat)

Implementation:

  1. SENT: Server stores message, sends ack to sender
     - Update: message status = 'sent' in DB
     - Notify sender: single check mark

  2. DELIVERED: Recipient's device confirms receipt
     - Recipient's WS server sends "delivered" event
     - Update: message status = 'delivered'
     - Notify sender: double check mark
     - For offline users: "delivered" sent when they reconnect and sync

  3. READ: Recipient opens the conversation
     - Client sends "read" event with last_read_message_id
     - Update: conversation_members.last_read_msg_id
     - Notify sender: blue check marks
     - All messages up to last_read_msg_id are marked as read

Storage:
  conversation_members table:
    last_read_msg_id = <latest message ID the user has seen>
  
  To check if a message is "read" by Bob:
    Bob's last_read_msg_id >= message_id → READ
    Bob's last_read_msg_id < message_id → DELIVERED or SENT

  Group read receipts:
    Track per-member last_read_msg_id
    "Read by 5 of 10" calculated on demand
```

---

## 13. Push Notification Integration

```
When recipient is offline:

  Chat Service ---> Notification Service ---> Push Provider ---> Device

  Push Providers:
    iOS:     Apple Push Notification Service (APNs)
    Android: Firebase Cloud Messaging (FCM)
    Web:     Web Push (VAPID)

  Notification Payload:
    {
      "to": "<device_token>",
      "notification": {
        "title": "Alice",
        "body": "Hey, how are you?",
        "badge": 3
      },
      "data": {
        "conversation_id": "conv_abc123",
        "message_id": "msg_xyz789",
        "sender_id": 12345
      }
    }

  Flow:
    1. Chat Service detects recipient offline (Connection Registry miss)
    2. Publish to Kafka topic "push-notifications"
    3. Push Worker consumes event
    4. Lookup user's device tokens from User Device Table
    5. Check user's notification preferences (muted? quiet hours?)
    6. Send to APNs/FCM
    7. Log delivery status

  Device Token Management:
    CREATE TABLE user_devices (
        user_id       BIGINT    NOT NULL,
        device_id     VARCHAR(64) NOT NULL,
        device_token  VARCHAR(255),
        platform      ENUM('ios', 'android', 'web'),
        last_active   TIMESTAMP,
        PRIMARY KEY (user_id, device_id)
    );
```

---

## 14. Media Handling

### 14.1 Media Upload Flow

```
1. Client requests pre-signed upload URL from Media Service
2. Client uploads media directly to object storage (S3)
3. S3 triggers processing pipeline:
   a. Images: generate thumbnails, compress
   b. Videos: transcode to multiple resolutions
   c. Documents: virus scan, generate preview
4. Processing complete → media URL returned
5. Client sends message with media_url reference

  Client         Media Service        S3            Processing
    |                 |                |                |
    |--request URL-->|                |                |
    |<--presigned----|                |                |
    |   upload URL   |                |                |
    |                 |                |                |
    |--upload file ------------------>|                |
    |                 |                |--trigger------>|
    |                 |                |                |--thumbnail
    |                 |                |                |--compress
    |                 |                |                |--virus scan
    |                 |                |<--store--------|
    |<--media_url ----|                |                |
    |                 |                |                |
    |--send msg with media_url (via WebSocket)         |
```

### 14.2 Media Delivery

```
Recipient receives message with media_url:
  1. Client shows placeholder/blurred preview
  2. Client downloads media from CDN (not directly from S3)
  3. CDN caches popular media at edge locations
  4. Progressive download for large files
  
  Recipient --> CDN (edge) --> S3 (origin)
                  |
            Cache HIT? Serve directly
            Cache MISS? Fetch from S3, cache at edge
```

---

## 15. End-to-End Encryption (E2EE) Overview

```
Signal Protocol (used by WhatsApp):

Key Exchange:
  1. Each user generates an identity key pair (long-term)
  2. Each user generates ephemeral pre-keys (one-time use)
  3. Pre-keys uploaded to server
  4. When Alice wants to message Bob:
     a. Alice fetches Bob's pre-key bundle from server
     b. Alice performs X3DH key agreement → shared secret
     c. Alice encrypts message with shared secret
     d. Server CANNOT read the message

  Alice                   Server                    Bob
    |                        |                        |
    |--fetch Bob's keys---->|                        |
    |<--Bob's pre-key-------|                        |
    |   bundle              |                        |
    |                        |                        |
    |--encrypted msg------->|--encrypted msg-------->|
    |  (server can't read)  |  (server can't read)  |
    |                        |                        |

Impact on System Design:
  - Server stores encrypted blobs (cannot index or search content)
  - Media is encrypted before upload
  - Group chats: Sender-Keys protocol (encrypt once, decrypt per member)
  - Key management adds complexity but essential for privacy
```

---

## 16. Message Synchronization Across Devices

```
User has multiple devices (phone + tablet + desktop):

Sync Strategy:
  1. Each device maintains a local sequence number (last_sync_seq)
  2. When device connects, sends its last_sync_seq to server
  3. Server returns all messages with seq > last_sync_seq
  4. Device applies updates and advances its sequence number

  Phone (seq: 1050)       Server (seq: 1075)      Desktop (seq: 1040)
       |                       |                        |
       |--sync from 1050 ---->|                        |
       |<--msgs 1051-1075 ----|                        |
       |  (25 new messages)   |                        |
       |                       |                        |
       |                       |<--sync from 1040 -----|
       |                       |--msgs 1041-1075 ----->|
       |                       |  (35 new messages)    |

Per-User Sync Cursor:
  CREATE TABLE sync_cursors (
      user_id    BIGINT    NOT NULL,
      device_id  VARCHAR(64) NOT NULL,
      last_seq   BIGINT    NOT NULL,
      updated_at TIMESTAMP,
      PRIMARY KEY (user_id, device_id)
  );
```

---

## 17. Full System Architecture

```
+------------------------------------------------------------------------+
|                      CLIENTS (iOS / Android / Web / Desktop)            |
+-----------------------------------+------------------------------------+
                                    |
                            +-------v--------+
                            | Load Balancer  |
                            | (L4 TCP/L7)    |
                            +-------+--------+
                                    |
                  +-----------------+-----------------+
                  |                                   |
          +-------v--------+                 +-------v--------+
          | API Gateway    |                 | WebSocket      |
          | (HTTP - REST)  |                 | Gateway Fleet  |
          | Auth, Profile, |                 | (200 servers,  |
          | History, Media |                 |  50K conn each)|
          +-------+--------+                 +-------+--------+
                  |                                   |
                  |                           +-------v--------+
                  |                           | Connection     |
                  |                           | Registry       |
                  |                           | (Redis)        |
                  |                           | {user->server} |
                  |                           +----------------+
                  |                                   |
          +-------v--------+                 +-------v--------+
          | Chat Service   |<--------------->| Message Router |
          | (business      |                 | (route to      |
          |  logic)        |                 |  correct WS    |
          +---+----+---+---+                 |  server)       |
              |    |   |                     +----------------+
              |    |   |
    +---------+  +-+   +----------+
    |            |                |
+---v------+ +--v--------+ +----v-----------+
| Message  | | Presence  | | Group          |
| Store    | | Service   | | Service        |
|(Cassandra| | (Redis    | | (members,      |
| cluster) | |  + heart- | |  fan-out)      |
|          | |  beat)    | |                |
+----------+ +-----------+ +----------------+

    +------------------+    +------------------+
    | Media Service    |    | Push Notification|
    | (S3 upload,      |    | Service          |
    |  CDN delivery)   |    | (APNs, FCM)     |
    +------------------+    +------------------+

    +------------------+    +------------------+
    | User Service     |    | Notification     |
    | (profiles,       |    | Queue (Kafka)    |
    |  contacts)       |    |                  |
    +------------------+    +------------------+

    +------------------+
    | Sync Service     |
    | (multi-device    |
    |  synchronization)|
    +------------------+
```

---

## 18. Database Architecture Summary

```
+-------------------+------------------+----------------------------------+
| Data              | Database         | Reason                           |
+-------------------+------------------+----------------------------------+
| Messages          | Cassandra        | Write-heavy, time-series,        |
|                   |                  | horizontal scaling               |
+-------------------+------------------+----------------------------------+
| User profiles     | PostgreSQL       | Structured, ACID, relational     |
+-------------------+------------------+----------------------------------+
| Conversations &   | PostgreSQL       | Relational queries (user's       |
| Members           |                  | conversation list)               |
+-------------------+------------------+----------------------------------+
| Connection state  | Redis            | In-memory, fast lookup,          |
|                   |                  | auto-expiry with TTL             |
+-------------------+------------------+----------------------------------+
| Presence/Online   | Redis            | Fast reads, TTL-based expiry     |
+-------------------+------------------+----------------------------------+
| Device tokens     | PostgreSQL       | Structured, infrequent updates   |
+-------------------+------------------+----------------------------------+
| Media files       | S3 + CDN         | Blob storage, globally cached    |
+-------------------+------------------+----------------------------------+
| Search index      | Elasticsearch    | Full-text message search         |
| (if not E2EE)     |                  | (only for non-encrypted chats)   |
+-------------------+------------------+----------------------------------+
```

---

## 19. Scaling Considerations

### 19.1 WebSocket Server Scaling

```
Challenge: 10M concurrent connections

Solution:
  - Fleet of 200+ WS servers (50K connections each)
  - Auto-scale based on connection count
  - Graceful shutdown: drain connections before termination
  - Connection rebalancing after scaling events

  When adding a new WS server:
    1. New server registers with load balancer
    2. New connections routed to new server
    3. No need to migrate existing connections
    
  When removing a WS server:
    1. Stop accepting new connections
    2. Send "reconnect" signal to all connected clients
    3. Clients reconnect to other servers via load balancer
    4. Shut down after all connections drained
```

### 19.2 Message Store Scaling

```
Cassandra Cluster:
  - Partition key: conversation_id
  - All messages in a conversation on same partition
  - Conversations distributed across cluster via consistent hashing
  
  Cluster sizing:
    730 TB over 5 years
    With replication factor 3: 2.2 PB total
    50 nodes * 50 TB each = 2.5 PB capacity
    
  Compaction: Time-window compaction strategy
    (Optimized for time-series write patterns)
```

---

## 20. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Protocol | HTTP Long Polling | WebSocket | **WebSocket** | True bidirectional, low overhead |
| Message store | MongoDB | Cassandra | **Cassandra** | Better write performance, time-series |
| Presence | Polling-based | Heartbeat + Redis | **Heartbeat** | Real-time, efficient |
| Group fan-out | Sync (in-request) | Async (Kafka) | **Async** for large groups | Don't block sender |
| Message ordering | Global sequence | Per-conversation | **Per-conversation** | Simpler, sufficient |
| Encryption | Server-side only | End-to-end | **E2EE** | Privacy is essential |
| Multi-device sync | Push all messages | Pull-based with cursor | **Pull with cursor** | Handles offline devices |
| Media | Inline in message | Object storage + URL | **Object storage** | Don't bloat message DB |

---

## 21. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. WebSocket is the right protocol for real-time chat —          |
|    persistent, bidirectional, low-overhead connections            |
|                                                                    |
| 2. Connection Registry (Redis) maps users to WS servers —        |
|    critical for routing messages to the right server              |
|                                                                    |
| 3. Wide-column stores (Cassandra) excel at message storage —     |
|    write-optimized, time-series friendly, horizontally scalable  |
|                                                                    |
| 4. Group chat requires async fan-out for large groups to         |
|    avoid blocking the sender                                     |
|                                                                    |
| 5. Presence detection uses heartbeats + Redis TTL — simple       |
|    and effective, auto-expires on disconnect                     |
|                                                                    |
| 6. Read receipts are tracked per-member with last_read_msg_id    |
|    — don't store per-message read status                         |
|                                                                    |
| 7. Push notifications handle offline delivery — queue messages   |
|    and notify via APNs/FCM                                       |
|                                                                    |
| 8. Media goes through object storage (S3) + CDN, never through  |
|    the WebSocket connection or message database                  |
|                                                                    |
| 9. E2E encryption means server CANNOT read messages — this       |
|    impacts search, moderation, and backup features               |
|                                                                    |
| 10. Multi-device sync uses pull-based cursors — each device      |
|     tracks its own sync position independently                   |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 28 - Social Media Feed](./28-social-feed.md) | Next: [Chapter 30 - Video Streaming](./30-video-streaming.md)*
