# Chapter 32: Design a Notification System

## 1. Problem Statement

Design a scalable notification system that can send millions of notifications per day
across multiple channels: push notifications (iOS/Android), SMS, and email. The system
must support user preferences, templates, scheduling, priority levels, rate limiting,
deduplication, and reliable delivery tracking.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Multi-channel | Send via push (iOS APNs, Android FCM), SMS, email |
| FR2 | Templates | Reusable notification templates with variable substitution |
| FR3 | User Preferences | Users control which channels and types they receive |
| FR4 | Scheduling | Send immediately or at a scheduled time |
| FR5 | Priority Levels | Urgent (security alerts), normal, low (marketing) |
| FR6 | Rate Limiting | Prevent notification fatigue (max N per user per day) |
| FR7 | Deduplication | Never send the same notification twice |
| FR8 | Delivery Tracking | Track sent, delivered, opened, clicked, failed |
| FR9 | Bulk Send | Send to millions of users (e.g., product announcement) |
| FR10 | Quiet Hours | Respect user's do-not-disturb settings |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 100M notifications/day across all channels |
| NFR2 | Latency | Urgent notifications delivered in < 1 second |
| NFR3 | Reliability | No notification loss, at-least-once delivery |
| NFR4 | Availability | 99.99% uptime |
| NFR5 | Throughput | Handle 10K notifications/sec sustained |
| NFR6 | Idempotency | Same trigger event must not produce duplicate notifications |

---

## 4. Capacity Estimation

### 4.1 Traffic

```
Total: 100M notifications/day

Breakdown by channel:
  Push:  60M/day (60%) = ~700/sec
  Email: 30M/day (30%) = ~350/sec
  SMS:   10M/day (10%) = ~115/sec
  Total: ~1,165/sec average
  Peak:  ~5,000/sec (3-5x during marketing campaigns)

Bulk campaigns:
  Marketing email to 50M users = burst over 2 hours
  = 50M / 7200 sec = ~7,000/sec burst
```

### 4.2 Storage

```
Notification Log:
  Each record: ~500 bytes (id, user, channel, content, status, timestamps)
  100M/day * 500 bytes = 50 GB/day
  Retain 90 days: 4.5 TB
  
Templates: ~10,000 templates * 5 KB = 50 MB (trivial)
User Preferences: 100M users * 200 bytes = 20 GB
```

---

## 5. Types of Notifications and Third-Party Providers

### 5.1 Push Notifications

```
iOS: Apple Push Notification Service (APNs)
  - HTTP/2 API
  - Device token-based routing
  - Max payload: 4 KB
  - Certificate or token-based authentication

Android: Firebase Cloud Messaging (FCM)
  - HTTP v1 API (REST)
  - Registration token per device
  - Max payload: 4 KB
  - Supports topics and conditions

Web Push:
  - VAPID (Voluntary Application Server Identification)
  - Service Worker-based
  - Works even when browser is closed

  +------------------+     +------------------+
  | Our Push Worker  | --> | APNs (Apple)     | --> iOS devices
  |                  | --> | FCM (Google)     | --> Android devices
  |                  | --> | Web Push (VAPID) | --> Browsers
  +------------------+     +------------------+
```

### 5.2 SMS

```
Providers: Twilio, AWS SNS, Vonage (Nexmo)

Twilio API:
  POST https://api.twilio.com/2010-04-01/Accounts/{sid}/Messages.json
  Body: { "To": "+1234567890", "From": "+0987654321", "Body": "Your code is 123456" }

Considerations:
  - Cost: $0.0075-$0.05 per SMS (varies by country)
  - Delivery receipts via webhooks
  - Short codes for high throughput
  - Opt-in/opt-out compliance (TCPA, GDPR)
```

### 5.3 Email

```
Providers: AWS SES, SendGrid, Mailgun, Postmark

SendGrid API:
  POST https://api.sendgrid.com/v3/mail/send
  Body: { "personalizations": [...], "from": {...}, "content": [...] }

Considerations:
  - Reputation management (sender score, DKIM, SPF, DMARC)
  - Bounce handling (hard bounce = remove, soft bounce = retry)
  - Unsubscribe links (CAN-SPAM compliance)
  - HTML rendering across email clients
  - Rate limits from provider (e.g., SES: 50K emails/sec)
```

---

## 6. High-Level Architecture

```
+------------------------------------------------------------------------+
|                    TRIGGER SOURCES                                       |
|  (Microservices, Cron Jobs, Admin Dashboard, Event Stream)              |
+---+----+----+---+------------------------------------------------------+
    |    |    |   |
    v    v    v   v
+------------------+
| Notification     |  <-- Single entry point for all notifications
| Service API      |
| (REST + gRPC)    |
+--------+---------+
         |
    +----v---------+
    | Validation   |  Check: valid user? valid channel? valid template?
    | & Enrichment |  Enrich: resolve template, fetch user preferences
    +----+---------+
         |
    +----v---------+
    | Preference   |  Filter: user opted-out? quiet hours? channel disabled?
    | Filter       |
    +----+---------+
         |
    +----v---------+
    | Deduplication|  Check: was this exact notification already sent?
    | Check        |  (idempotency key in Redis)
    +----+---------+
         |
    +----v---------+
    | Rate Limiter |  Check: user exceeded daily/hourly limit?
    +----+---------+
         |
    +----v---------+
    | Priority     |  Route to appropriate priority queue:
    | Router       |  URGENT → high-priority queue
    +----+---------+  NORMAL → standard queue
         |            LOW    → low-priority queue (batch-friendly)
    +----v---+----v----+----v----+
    | HIGH   | NORMAL  |  LOW   |
    | QUEUE  | QUEUE   | QUEUE  |  (Kafka topics or SQS queues)
    +---+----+----+----+---+----+
        |         |        |
    +---v----+ +--v---+ +--v---+
    |Workers | |Workrs| |Workrs|  (Different pool sizes per priority)
    |  (10)  | | (50) | | (20) |
    +---+----+ +--+---+ +--+---+
        |         |        |
    +---v---------v--------v---+
    |   Channel Router         |  Route to correct channel worker
    +---+--------+--------+---+
        |        |        |
    +---v---+ +--v---+ +--v----+
    | Push  | | SMS  | | Email |
    |Worker | |Worker| |Worker |
    +---+---+ +--+---+ +--+----+
        |        |        |
    +---v---+ +--v---+ +--v------+
    | APNs  | |Twilio| | SendGrid|  Third-party delivery
    | FCM   | |      | | / SES   |
    +---+---+ +--+---+ +--+------+
        |        |        |
    +---v--------v--------v---+
    | Delivery Tracking        |  Log: sent, delivered, opened, failed
    | (Callback/Webhook        |
    |  handlers)               |
    +--------------------------+
```

---

## 7. Notification Creation Flow (Detailed)

### 7.1 Step-by-Step

```
1. TRIGGER: Upstream service generates an event
   Example: "User completed purchase" → send receipt via email + push

2. API CALL:
   POST /api/v1/notifications
   {
     "user_id": 123456,
     "event_type": "order_completed",
     "template_id": "order_receipt_v2",
     "channels": ["push", "email"],
     "priority": "normal",
     "data": {
       "order_id": "ORD-789",
       "total": "$49.99",
       "items": ["Widget A", "Widget B"]
     },
     "idempotency_key": "order-ORD-789-receipt"
   }

3. VALIDATION:
   - user_id exists? ✓
   - template_id exists? ✓
   - channels valid? ✓
   - data has all required template variables? ✓

4. TEMPLATE RENDERING:
   Template: "Hi {{name}}, your order {{order_id}} for {{total}} is confirmed!"
   Rendered: "Hi Alice, your order ORD-789 for $49.99 is confirmed!"

5. PREFERENCE CHECK:
   - User has push enabled? ✓
   - User has email enabled? ✓
   - User subscribed to "order" notifications? ✓
   - Is it quiet hours? No ✓

6. DEDUPLICATION:
   Check Redis: EXISTS "dedup:order-ORD-789-receipt"
   - Not found → proceed (set key with TTL 24h)
   - Found → skip (duplicate!)

7. RATE LIMIT CHECK:
   Redis: INCR "ratelimit:123456:daily" → 12 (under limit of 50)
   Proceed ✓

8. ENQUEUE:
   - Push notification → Kafka topic "notifications.push.normal"
   - Email notification → Kafka topic "notifications.email.normal"

9. WORKER PROCESSES:
   - Push worker: format payload, send to APNs/FCM
   - Email worker: render HTML template, send to SendGrid

10. DELIVERY TRACKING:
    - Log status: "sent" with timestamp
    - On callback from provider: update to "delivered" or "failed"
```

---

## 8. Template Engine

### 8.1 Template System

```
Templates support:
  - Variable substitution: {{variable_name}}
  - Conditionals: {{#if condition}} ... {{/if}}
  - Loops: {{#each items}} ... {{/each}}
  - Localization: {{t "greeting"}} → "Hello" / "Hola" / "Bonjour"
  - Channel-specific formatting (push = short, email = HTML, SMS = plain)

Template Storage:
  CREATE TABLE notification_templates (
      template_id    VARCHAR(64)  PRIMARY KEY,
      event_type     VARCHAR(64)  NOT NULL,
      channel        ENUM('push', 'email', 'sms'),
      subject        TEXT,        -- for email
      body           TEXT         NOT NULL,
      locale         VARCHAR(10)  DEFAULT 'en',
      version        INT          DEFAULT 1,
      is_active      BOOLEAN      DEFAULT TRUE,
      created_at     TIMESTAMP,
      updated_at     TIMESTAMP,
      UNIQUE (event_type, channel, locale, version)
  );

Example Templates:

  Push (short):
    "Your order {{order_id}} is confirmed! Total: {{total}}"

  Email (HTML):
    "<h1>Order Confirmed</h1>
     <p>Hi {{name}},</p>
     <p>Your order <strong>{{order_id}}</strong> for <strong>{{total}}</strong>
        has been confirmed.</p>
     <h3>Items:</h3>
     <ul>
       {{#each items}}<li>{{this}}</li>{{/each}}
     </ul>"

  SMS (plain):
    "Order {{order_id}} confirmed. Total: {{total}}. Track: {{tracking_url}}"
```

---

## 9. User Preference Service

### 9.1 Preference Model

```sql
CREATE TABLE user_notification_preferences (
    user_id           BIGINT       NOT NULL,
    channel           ENUM('push', 'email', 'sms'),
    event_category    VARCHAR(50),  -- 'orders', 'marketing', 'security', 'social'
    enabled           BOOLEAN      DEFAULT TRUE,
    PRIMARY KEY (user_id, channel, event_category)
);

CREATE TABLE user_quiet_hours (
    user_id           BIGINT       PRIMARY KEY,
    quiet_start       TIME,        -- e.g., 22:00
    quiet_end         TIME,        -- e.g., 08:00
    timezone          VARCHAR(50), -- e.g., 'America/New_York'
    enabled           BOOLEAN      DEFAULT FALSE
);

CREATE TABLE user_devices (
    user_id           BIGINT       NOT NULL,
    device_id         VARCHAR(64)  NOT NULL,
    device_token      VARCHAR(255),           -- APNs/FCM token
    platform          ENUM('ios', 'android', 'web'),
    app_version       VARCHAR(20),
    last_active       TIMESTAMP,
    PRIMARY KEY (user_id, device_id)
);
```

### 9.2 Preference Check Flow

```
  Notification Request
        |
   +----v---------+
   | Fetch user's |
   | preferences  |
   +----+---------+
        |
   +----v------------------------------+
   | Channel: push                      |
   | Category: orders                   |
   | Enabled: true ✓                    |
   +----+-------------------------------+
        |
   +----v------------------------------+
   | Quiet hours: 22:00 - 08:00 EST    |
   | Current time: 14:30 EST           |
   | Within quiet hours? No ✓          |
   +----+-------------------------------+
        |
   +----v------------------------------+
   | Exception: URGENT priority         |
   | bypasses quiet hours and some      |
   | preference filters (security       |
   | alerts always delivered)           |
   +------------------------------------+
```

---

## 10. Deduplication

### 10.1 Why Deduplication Matters

```
Scenario: User completes a purchase. The order service fires an event.
Due to a retry (network timeout), the event fires TWICE.
Without dedup, user gets TWO receipt notifications.

Solution: Idempotency key

  Every notification request includes an idempotency_key:
    "idempotency_key": "order-ORD-789-receipt"

  Before processing:
    1. Check Redis: EXISTS "dedup:{idempotency_key}"
    2. If exists: SKIP (already processed)
    3. If not: SET "dedup:{idempotency_key}" EX 86400 (24h TTL)
              Process the notification

  Redis Command:
    SET dedup:order-ORD-789-receipt "1" NX EX 86400
    NX = only set if not exists (atomic check-and-set)
    Returns OK if set (not duplicate) or nil (duplicate)
```

### 10.2 Additional Dedup Layers

```
Layer 1: Idempotency key (at API level) — prevents duplicate triggers
Layer 2: Content hash dedup — prevent same content to same user within X hours
          Key: "content_dedup:{user_id}:{hash(channel + content)}"
          TTL: 4 hours
Layer 3: Provider-level dedup — some providers deduplicate on their end
```

---

## 11. Rate Limiting Per User

### 11.1 Rate Limiting Strategy

```
Prevent notification fatigue:

Limits:
  - Push:   max 10/hour, max 30/day per user
  - Email:  max 5/day per user (non-transactional)
  - SMS:    max 3/day per user

Exceptions:
  - URGENT priority (security alerts, 2FA codes): NO rate limit
  - Transactional (order receipts, shipping updates): higher limits

Implementation (Redis sliding window):

  -- Check push rate limit for user 123456
  Key: "ratelimit:push:123456:hourly"
  
  MULTI
    INCR ratelimit:push:123456:hourly
    EXPIRE ratelimit:push:123456:hourly 3600
  EXEC
  
  If count > 10: REJECT (rate limited)
  If count <= 10: PROCEED

  -- Also check daily limit
  Key: "ratelimit:push:123456:daily"
  If count > 30: REJECT
```

### 11.2 Rate Limit Response

```
When rate limited:
  - Low priority: silently drop (don't even queue)
  - Normal priority: queue for later delivery (when window resets)
  - Urgent priority: deliver anyway (bypass rate limit)

  API Response when rate limited:
  HTTP 429 Too Many Requests
  {
    "error": "rate_limited",
    "message": "User 123456 has exceeded push notification limit",
    "retry_after": 1800  // seconds until rate limit resets
  }
```

---

## 12. Priority Levels and Queue Architecture

### 12.1 Priority Queues

```
Three priority levels with separate queues and worker pools:

  URGENT (P0):
    - Security alerts (password changed, suspicious login)
    - 2FA codes
    - Payment confirmations
    - Delivery: < 1 second
    - Worker pool: 10 dedicated workers (always ready)
    - Queue: separate Kafka topic with low consumer lag alert

  NORMAL (P1):
    - Transactional (order confirmation, shipping update)
    - Social (new follower, message received)
    - Delivery: < 30 seconds
    - Worker pool: 50 workers (auto-scale)
    - Queue: standard Kafka topic

  LOW (P2):
    - Marketing emails
    - Weekly digests
    - Product announcements
    - Delivery: within hours (batched)
    - Worker pool: 20 workers
    - Queue: standard Kafka topic, processed during off-peak

  +-------------+     +-------------------+     +------------------+
  | URGENT (P0) | --> | 10 Workers        | --> | Immediate Send   |
  | Kafka topic |     | (dedicated, fast) |     | (<1 sec)         |
  +-------------+     +-------------------+     +------------------+

  +-------------+     +-------------------+     +------------------+
  | NORMAL (P1) | --> | 50 Workers        | --> | Send < 30 sec    |
  | Kafka topic |     | (auto-scale)      |     |                  |
  +-------------+     +-------------------+     +------------------+

  +-------------+     +-------------------+     +------------------+
  | LOW (P2)    | --> | 20 Workers        | --> | Batch send       |
  | Kafka topic |     | (off-peak)        |     | (during off-peak)|
  +-------------+     +-------------------+     +------------------+
```

---

## 13. Retry Mechanism with Exponential Backoff

### 13.1 Retry Strategy

```
When a third-party provider fails (APNs timeout, Twilio 5xx, etc.):

Retry Policy:
  Max retries: 5
  Backoff: exponential with jitter

  Attempt 1: immediate
  Attempt 2: wait 1 sec + random(0, 500ms)
  Attempt 3: wait 2 sec + random(0, 1000ms)
  Attempt 4: wait 4 sec + random(0, 2000ms)
  Attempt 5: wait 8 sec + random(0, 4000ms)
  
  After 5 failures: move to Dead Letter Queue (DLQ)

  Worker Processing:
    try:
      response = send_to_provider(notification)
      if response.success:
        log_status("delivered")
      elif response.retryable:  # 429, 503, timeout
        requeue_with_backoff(notification, attempt + 1)
      else:  # 400, 404 (bad token, invalid recipient)
        log_status("permanently_failed")
        move_to_dlq(notification)
    except Timeout:
      requeue_with_backoff(notification, attempt + 1)
```

### 13.2 Dead Letter Queue (DLQ)

```
Notifications that fail all retries go to DLQ:

  Failed Notification --> DLQ (Kafka topic "notifications.dlq")
  
  DLQ Processing:
    - Manual review by ops team
    - Automated analysis: why did it fail?
      - Invalid device token → remove from user_devices
      - User uninstalled app → mark device inactive
      - Provider outage → bulk retry when provider recovers
    - Alert on DLQ growth (spike indicates provider issue)

  DLQ Dashboard:
    +-------------------+--------+------------------+
    | Channel           | Count  | Top Error        |
    +-------------------+--------+------------------+
    | Push (APNs)       | 1,234  | InvalidToken     |
    | Push (FCM)        | 456    | NotRegistered    |
    | Email (SendGrid)  | 89     | BounceDetected   |
    | SMS (Twilio)      | 23     | InvalidNumber    |
    +-------------------+--------+------------------+
```

---

## 14. Delivery Tracking and Analytics

### 14.1 Notification Lifecycle States

```
  CREATED --> QUEUED --> PROCESSING --> SENT --> DELIVERED --> OPENED --> CLICKED
                                        |                       |
                                        v                       v
                                     FAILED                  BOUNCED
                                        |                  (email only)
                                        v
                                    DLQ (dead letter)

State tracking in database:
  CREATE TABLE notification_log (
      notification_id  BIGINT       PRIMARY KEY,
      user_id          BIGINT       NOT NULL,
      channel          ENUM('push', 'email', 'sms'),
      event_type       VARCHAR(64),
      template_id      VARCHAR(64),
      priority         ENUM('urgent', 'normal', 'low'),
      status           ENUM('created','queued','processing',
                            'sent','delivered','opened',
                            'clicked','failed','bounced'),
      content_preview  VARCHAR(255),
      provider         VARCHAR(20),          -- 'apns', 'fcm', 'twilio', 'sendgrid'
      provider_msg_id  VARCHAR(128),         -- provider's tracking ID
      error_message    TEXT,
      created_at       TIMESTAMP,
      sent_at          TIMESTAMP,
      delivered_at     TIMESTAMP,
      opened_at        TIMESTAMP,
      clicked_at       TIMESTAMP,
      INDEX idx_user (user_id, created_at DESC),
      INDEX idx_status (status, created_at)
  );
```

### 14.2 Tracking Mechanisms

```
Push Delivery Confirmation:
  - APNs: delivery receipt not guaranteed (fire-and-forget)
  - FCM: supports delivery receipts via callback
  - Track "opened" via app event when user taps notification

Email Tracking:
  - Open tracking: embed 1x1 pixel image with unique URL
    <img src="https://track.example.com/open/{notification_id}" width="1" height="1">
    When image loads → user opened email
  - Click tracking: wrap links with redirect URL
    https://track.example.com/click/{notification_id}?url={original_url}
    When clicked → log click, redirect to original URL
  - Bounce tracking: SendGrid/SES webhook on bounce events

SMS Delivery:
  - Twilio sends delivery receipts via webhook
  - Status: queued → sent → delivered / failed / undelivered
```

---

## 15. Scheduled Notifications

### 15.1 Scheduling Architecture

```
Two approaches:

Approach 1: Delay Queues
  - Use SQS delay queues or Kafka with delayed consumption
  - Set message visibility timeout to scheduled time
  - Simple but limited to max delay (SQS: 15 minutes)

Approach 2: Scheduler Service (for longer delays)
  - Store scheduled notifications in database
  - Cron job scans for notifications due in next minute
  - Moves them from scheduled table to Kafka queue

  CREATE TABLE scheduled_notifications (
      id               BIGINT       PRIMARY KEY,
      notification     JSON         NOT NULL,
      scheduled_at     TIMESTAMP    NOT NULL,
      status           ENUM('pending', 'queued', 'cancelled'),
      created_at       TIMESTAMP,
      INDEX idx_schedule (status, scheduled_at)
  );

  Cron (runs every 30 seconds):
    SELECT * FROM scheduled_notifications
    WHERE status = 'pending'
      AND scheduled_at <= NOW() + INTERVAL 30 SECOND
    ORDER BY scheduled_at
    LIMIT 1000;

    For each: publish to Kafka, update status = 'queued'

  +------------------+     +------------------+     +-----------+
  | Scheduled        | --> | Scheduler Cron   | --> | Kafka     |
  | Notifications DB |     | (every 30 sec)   |     | Queue     |
  +------------------+     +------------------+     +-----------+
```

### 15.2 Use Cases

```
- "Send welcome email 24 hours after signup"
- "Send abandoned cart reminder after 2 hours"
- "Send weekly digest every Monday at 9 AM user's local time"
- "Send appointment reminder 1 hour before"
```

---

## 16. Bulk Notification Sending

```
Scenario: Send marketing email to 50M users

Challenges:
  - Can't load 50M users into memory
  - Can't send 50M at once (provider rate limits)
  - Some users have opted out

Pipeline:
  1. Admin creates campaign: "New Feature Announcement"
  2. System queries eligible users (opted-in, active, etc.)
  3. Stream users from DB in batches of 10,000
  4. For each batch:
     a. Check preferences (filter out opted-out)
     b. Render template per user (personalization)
     c. Publish to Kafka in batches
  5. Workers consume and send at controlled rate
  6. Track campaign-level metrics (sent, opened, clicked)

  Admin Dashboard        Campaign Service        User DB         Kafka
       |                       |                    |              |
       |--create campaign----->|                    |              |
       |                       |--stream users----->|              |
       |                       |<--batch of 10K ----|              |
       |                       |--filter prefs----->|              |
       |                       |--render template-->|              |
       |                       |--publish batch---->|              |-->Workers
       |                       |                    |              |
       |                       |<--batch of 10K ----|              |
       |                       |--filter, render--->|              |
       |                       |--publish batch---->|              |-->Workers
       |                       | ... (repeat) ...   |              |

  Rate Control:
    - Workers throttled to provider rate limits
    - SendGrid: 10K emails/sec → configure worker pool accordingly
    - Spread over 2 hours to avoid spikes
```

---

## 17. Full System Architecture

```
+------------------------------------------------------------------------+
|                    TRIGGER SOURCES                                       |
|  +------------+  +------------+  +-----------+  +------------+          |
|  | Order Svc  |  | Auth Svc   |  | Marketing |  | Cron Jobs  |          |
|  | (purchase) |  | (2FA, pwd) |  | (campaign)|  | (digest)   |          |
|  +-----+------+  +-----+------+  +-----+-----+  +-----+------+         |
|        |               |               |               |                |
+--------+---------------+---------------+---------------+----------------+
         |               |               |               |
         +-------+-------+-------+-------+               |
                 |                                        |
         +-------v--------+                    +----------v--------+
         | Notification    |                    | Scheduler Service |
         | Service API     |                    | (delay queue +    |
         | (validate,      |                    |  cron for future) |
         |  template,      |                    +---+---------------+
         |  preferences,   |                        |
         |  dedup, rate    |<-----------------------+
         |  limit)         |
         +-------+---------+
                 |
    +------------+------------+
    |            |            |
+---v---+  +----v----+  +----v----+
|URGENT |  |NORMAL   |  | LOW    |
|Queue  |  |Queue    |  | Queue  |
|(Kafka)|  |(Kafka)  |  |(Kafka) |
+---+---+  +----+----+  +---+----+
    |           |            |
+---v---+  +---v----+  +----v---+
|Workers|  |Workers |  |Workers |
|(10)   |  |(50)    |  |(20)    |
+---+---+  +---+----+  +---+----+
    |          |            |
    +----------+------------+
               |
    +----------v-----------+
    |   Channel Router     |
    +--+--------+--------+-+
       |        |        |
  +----v--+ +---v---+ +--v-----+
  | Push  | | SMS   | | Email  |
  |Worker | |Worker | | Worker |
  +---+---+ +---+---+ +---+----+
      |         |          |
  +---v---+ +---v---+ +---v------+
  | APNs  | |Twilio | | SendGrid |
  | FCM   | |       | | / SES    |
  +---+---+ +---+---+ +---+------+
      |         |          |
      +----+----+----+-----+
           |         |
   +-------v---+ +---v---------+
   | Delivery  | | DLQ         |
   | Tracker   | | (failed     |
   | (webhook  | |  after 5    |
   |  handler) | |  retries)   |
   +-----------+ +-------------+

  +-------------------+     +-------------------+
  | Notification Log  |     | User Preferences  |
  | (PostgreSQL /     |     | (PostgreSQL +     |
  |  TimescaleDB)     |     |  Redis cache)     |
  +-------------------+     +-------------------+

  +-------------------+     +-------------------+
  | Template Store    |     | Analytics         |
  | (PostgreSQL +     |     | Dashboard         |
  |  Redis cache)     |     | (Grafana/Metabase)|
  +-------------------+     +-------------------+
```

---

## 18. Database Design Summary

```
+--------------------+-------------------+----------------------------------+
| Data               | Database          | Reason                           |
+--------------------+-------------------+----------------------------------+
| Notification log   | PostgreSQL +      | Structured, queryable,           |
|                    | TimescaleDB       | time-series partitioning         |
+--------------------+-------------------+----------------------------------+
| User preferences   | PostgreSQL +      | Relational, cached in Redis      |
|                    | Redis cache       | for fast lookups                 |
+--------------------+-------------------+----------------------------------+
| Templates          | PostgreSQL +      | Version-controlled, cached       |
|                    | Redis cache       |                                  |
+--------------------+-------------------+----------------------------------+
| Device tokens      | PostgreSQL        | Structured, updated on app       |
|                    |                   | launch                           |
+--------------------+-------------------+----------------------------------+
| Rate limit state   | Redis             | Fast counters, TTL-based         |
+--------------------+-------------------+----------------------------------+
| Dedup keys         | Redis             | Fast existence check, TTL        |
+--------------------+-------------------+----------------------------------+
| Scheduled notifs   | PostgreSQL        | Queryable by scheduled_at        |
+--------------------+-------------------+----------------------------------+
| Message queues     | Kafka             | Reliable, ordered, scalable      |
+--------------------+-------------------+----------------------------------+
```

---

## 19. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Queue system | SQS | Kafka | **Kafka** | Higher throughput, replay capability |
| Priority handling | Single queue + priority field | Separate queues per priority | **Separate queues** | Urgent never waits behind low |
| Dedup mechanism | Database check | Redis (NX + TTL) | **Redis** | Fast, atomic, auto-expire |
| Template engine | Server-side (Handlebars) | Client-side | **Server-side** | Consistent across channels |
| Delivery tracking | Polling provider | Webhooks/callbacks | **Webhooks** | Real-time, push-based |
| Rate limiting | In-memory per server | Centralized Redis | **Redis** | Consistent across all servers |
| Retry strategy | Fixed interval | Exponential backoff + jitter | **Exp. backoff** | Prevents thundering herd |
| Notification storage | NoSQL | PostgreSQL + partitioning | **PostgreSQL** | Rich querying for analytics |

---

## 20. Monitoring and Alerting

```
Key Metrics to Monitor:

  +-------------------------------+------------------+------------------+
  | Metric                        | Warning          | Critical         |
  +-------------------------------+------------------+------------------+
  | Queue depth (urgent)          | > 100            | > 1,000          |
  | Queue depth (normal)          | > 10,000         | > 100,000        |
  | Delivery success rate         | < 98%            | < 95%            |
  | Average delivery latency      | > 5 sec          | > 30 sec         |
  | DLQ growth rate               | > 100/hour       | > 1,000/hour     |
  | Provider error rate (per svc) | > 2%             | > 10%            |
  | Rate limit rejections         | > 1%             | > 5%             |
  +-------------------------------+------------------+------------------+

  Dashboard: Grafana with Prometheus metrics
  Alerting: PagerDuty for critical, Slack for warnings
```

---

## 21. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. Single entry point API — all notification triggers go          |
|    through one service for consistent handling                    |
|                                                                    |
| 2. Priority queues ensure URGENT notifications (2FA, security)   |
|    are never delayed by marketing bulk sends                     |
|                                                                    |
| 3. Deduplication with idempotency keys prevents duplicate         |
|    notifications — essential with distributed retries             |
|                                                                    |
| 4. Rate limiting per user prevents notification fatigue —        |
|    respect user attention, or they'll uninstall your app         |
|                                                                    |
| 5. User preferences are a FIRST-CLASS feature — opt-in/out,     |
|    channel selection, quiet hours                                 |
|                                                                    |
| 6. Template engine separates content from delivery logic —       |
|    marketing can update templates without code changes            |
|                                                                    |
| 7. Exponential backoff with jitter for retries — prevents        |
|    thundering herd on provider recovery                          |
|                                                                    |
| 8. Dead Letter Queue captures permanently failed notifications   |
|    — essential for debugging and compliance                      |
|                                                                    |
| 9. Delivery tracking via webhooks provides real-time status —    |
|    measure open rates, click rates, bounce rates                 |
|                                                                    |
| 10. Bulk sends require streaming + throttling — never load       |
|     50M users into memory, respect provider rate limits          |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 31 - Ride-Sharing Service](./31-ride-sharing.md) | Next: [Chapter 33 - File Storage System](./33-file-storage.md)*
