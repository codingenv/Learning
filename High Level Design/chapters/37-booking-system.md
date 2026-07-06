# Chapter 37 — Design a Booking System

> *Hotel rooms, movie tickets, flights, restaurant tables — they all share one brutal
> constraint: the same resource cannot be sold twice. This chapter dissects the
> concurrency problem at the heart of every booking platform and builds a system
> that handles millions of searches while guaranteeing zero double-bookings.*

---

## 1. Problem Statement

Design a **general-purpose booking platform** where users can search for
available resources, view details, and make reservations — all in real time.

The resources could be:
- **Hotel rooms** (Airbnb — 7M+ listings, Booking.com — 28M+ listings)
- **Movie tickets** (BookMyShow, Fandango)
- **Flight seats** (Expedia, Kayak)
- **Restaurant tables** (OpenTable, Resy)
- **Event tickets** (Ticketmaster — handles 500M+ tickets/year)

### The Core Challenge

```
  User A (New York)                    User B (London)
       |                                    |
       |  "Book Room 42, Dec 25"            |  "Book Room 42, Dec 25"
       v                                    v
  +------------------+               +------------------+
  |   API Server 1   |               |   API Server 2   |
  +------------------+               +------------------+
            \                                 /
             v                               v
       +--------------------------------------+
       |         SAME DATABASE ROW            |
       |   Room 42 | Dec 25 | status=OPEN    |
       +--------------------------------------+
                       ???
       Both requests arrive within milliseconds.
       Who gets the room? How do we prevent BOTH
       from succeeding?
```

This **concurrent write to the same resource** is THE defining problem of
booking systems. Every architectural decision flows from how we solve it.

---

## 2. Functional Requirements

| # | Requirement              | Description                                          |
|---|--------------------------|------------------------------------------------------|
| 1 | **Search availability**  | Search by date range, location, price, filters       |
| 2 | **View listing details** | Photos, descriptions, reviews, calendar availability |
| 3 | **Make a reservation**   | Select dates, reserve resource, pay                  |
| 4 | **Cancel booking**       | User-initiated cancel with refund policy             |
| 5 | **Payment integration**  | Stripe/PayPal, hold + charge pattern                 |
| 6 | **Confirmation**         | Email + push notification on booking/cancel          |
| 7 | **Booking history**      | Past + upcoming bookings for the user                |
| 8 | **Calendar availability**| Visual calendar showing open/booked dates            |
| 9 | **Reviews & ratings**    | Post-stay reviews for listings                       |
|10 | **Host management**      | Hosts can list, update, block dates                  |

---

## 3. Non-Functional Requirements

| Requirement              | Target                        | Why                                    |
|--------------------------|-------------------------------|----------------------------------------|
| **Strong consistency**   | Zero double-bookings          | Core business invariant                |
| **High availability**    | 99.99% for search             | Search is read-heavy, must stay up     |
| **Low latency (search)** | < 200ms p99                   | Users abandon slow search results      |
| **Low latency (book)**   | < 2s end-to-end               | Including payment hold                 |
| **Surge handling**       | 100x normal traffic           | Concert tickets, flash sales           |
| **Data durability**      | Zero booking loss             | Financial + legal obligation           |
| **Idempotency**          | Retry-safe booking operations | Network failures must not double-charge|

### The Consistency-Availability Trade-off

```
              SEARCH PATH               BOOKING PATH
            (Read-Heavy)              (Write-Critical)

        +------------------+      +------------------+
        | High Availability|      |Strong Consistency|
        |   Eventual OK    |      |   ACID Required  |
        +------------------+      +------------------+
                |                         |
        Can serve stale data       MUST be correct
        (listing showed avail      (cannot sell same
         but booked moments        room to two people)
         ago — OK, fail at
         booking step)
```

---

## 4. Capacity Estimation

### 4.1 Traffic Estimates

```
  Daily Active Users:     100,000
  Daily Searches:         500,000   (5 searches/user)
  Daily Bookings:          10,000   (2% conversion)
  Daily Cancellations:      1,000   (10% cancel rate)

  Searches/sec (avg):     ~6/sec      Peak (10x): ~60/sec
  Bookings/sec (avg):     ~0.12/sec   Peak (10x): ~1.2/sec
```

### 4.2 Storage Estimates

```
  Listings:       500K × 5 KB       = 2.5 GB text  (+2.5 TB images on CDN)
  Bookings:       3.65M/yr × 1 KB   = 3.65 GB/year (5-yr: ~18 GB)
  Availability:   500K × 365 × 100B = ~18 GB  (182.5M rows)
  Users:          1M × 2 KB         = 2 GB
```

### 4.3 Flash Sale / Surge Traffic

```
  SCENARIO: Popular concert — 50,000 tickets, 500,000 users
  ─────────────────────────────────────────────────────────
  All 500K users hit "Book" within 60 seconds
  Booking attempts/sec: 500,000 / 60 = ~8,333/sec → 100x normal peak!

  ┌──────────────────────────────────────────┐
  │        TRAFFIC SPIKE PROFILE             │
  │  req/s                                   │
  │  8000 │        ████                      │
  │  6000 │      ████████                    │
  │  4000 │    ████████████                  │
  │  2000 │  ████████████████                │
  │   100 │──████████████████████────────    │
  │       └──┬──┬──┬──┬──┬──┬──┬──┬──┬──    │
  │         -2  0  2  4  6  8  10 12 14 min  │
  │              ↑                           │
  │         Sale opens                       │
  └──────────────────────────────────────────┘
```

---

## 5. Data Model

### 5.1 Database Schema

```sql
CREATE TABLE users (
    user_id       BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    phone         VARCHAR(20),
    role          VARCHAR(20) DEFAULT 'guest',   -- guest, host, admin
    created_at    TIMESTAMP DEFAULT NOW()
);

CREATE TABLE listings (
    listing_id    BIGSERIAL PRIMARY KEY,
    host_id       BIGINT REFERENCES users(user_id),
    title         VARCHAR(500) NOT NULL,
    description   TEXT,
    listing_type  VARCHAR(50) NOT NULL,           -- hotel_room, ticket, flight
    location_lat  DECIMAL(10,8),
    location_lng  DECIMAL(11,8),
    city          VARCHAR(100),
    country       VARCHAR(100),
    base_price    DECIMAL(10,2) NOT NULL,
    currency      VARCHAR(3) DEFAULT 'USD',
    max_guests    INT DEFAULT 1,
    amenities     JSONB,                          -- {"wifi":true, "pool":true}
    status        VARCHAR(20) DEFAULT 'active',
    created_at    TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_listings_city   ON listings(city);
CREATE INDEX idx_listings_type   ON listings(listing_type);

-- ═══ THE CRITICAL TABLE ═══
CREATE TABLE availability (
    availability_id BIGSERIAL PRIMARY KEY,
    listing_id      BIGINT REFERENCES listings(listing_id),
    date            DATE NOT NULL,
    total_slots     INT NOT NULL DEFAULT 1,       -- 1 for hotel, 500 for movie
    booked_slots    INT NOT NULL DEFAULT 0,
    price_override  DECIMAL(10,2),                -- dynamic pricing per date
    status          VARCHAR(20) DEFAULT 'open',   -- open, closed, blocked
    version         INT NOT NULL DEFAULT 0,       -- for optimistic locking!
    CONSTRAINT uq_listing_date UNIQUE (listing_id, date),
    CONSTRAINT chk_slots CHECK (booked_slots <= total_slots AND booked_slots >= 0)
);

CREATE TABLE bookings (
    booking_id      BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) UNIQUE NOT NULL, -- prevents double-booking on retry
    user_id         BIGINT REFERENCES users(user_id),
    listing_id      BIGINT REFERENCES listings(listing_id),
    check_in        DATE NOT NULL,
    check_out       DATE NOT NULL,
    num_guests      INT DEFAULT 1,
    total_price     DECIMAL(10,2) NOT NULL,
    status          VARCHAR(20) DEFAULT 'pending', -- pending, confirmed, cancelled
    created_at      TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_bookings_user    ON bookings(user_id);
CREATE INDEX idx_bookings_listing ON bookings(listing_id);

CREATE TABLE payments (
    payment_id      BIGSERIAL PRIMARY KEY,
    booking_id      BIGINT REFERENCES bookings(booking_id),
    idempotency_key VARCHAR(255) UNIQUE NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    currency        VARCHAR(3) DEFAULT 'USD',
    payment_method  VARCHAR(50),                  -- stripe, paypal
    stripe_charge_id VARCHAR(255),
    status          VARCHAR(20) DEFAULT 'pending', -- pending, captured, refunded
    created_at      TIMESTAMP DEFAULT NOW()
);
```

### 5.2 Entity Relationship Diagram

```
  ┌──────────────┐       ┌───────────────────┐       ┌──────────────┐
  │    USERS     │       │     LISTINGS      │       │ AVAILABILITY │
  ├──────────────┤       ├───────────────────┤       ├──────────────┤
  │ user_id  PK  │──┐    │ listing_id    PK  │──────▶│ listing_id FK│
  │ email        │  │    │ host_id       FK  │◀──┐   │ date         │
  │ full_name    │  │    │ title, price      │   │   │ total_slots  │
  │ role         │  │    │ location, amenity │   │   │ booked_slots │
  └──────────────┘  │    └───────────────────┘   │   │ version (OL) │
                    │                            │   └──────────────┘
                    │    ┌───────────────────┐   │   ┌──────────────┐
                    │    │    BOOKINGS       │   │   │   PAYMENTS   │
                    │    ├───────────────────┤   │   ├──────────────┤
                    └───▶│ user_id       FK  │   │   │ payment_id PK│
                         │ listing_id    FK  │───┘   │ booking_id FK│
                         │ idempotency_key   │──────▶│ amount       │
                         │ check_in/out      │       │ status       │
                         │ total_price       │       └──────────────┘
                         └───────────────────┘
```

### 5.3 Database Choice Rationale

```
  ┌─────────────────────┬──────────────────┬──────────────────────────────┐
  │     Data Store      │    Technology    │           Purpose            │
  ├─────────────────────┼──────────────────┼──────────────────────────────┤
  │ Bookings/Payments   │ PostgreSQL       │ ACID txns, strong consistency│
  │ Availability        │                  │ Row-level locks, constraints │
  ├─────────────────────┼──────────────────┼──────────────────────────────┤
  │ Search index        │ Elasticsearch    │ Full-text, geo, facets       │
  ├─────────────────────┼──────────────────┼──────────────────────────────┤
  │ Cache + Locks       │ Redis            │ Fast lookups, TTL-based hold │
  ├─────────────────────┼──────────────────┼──────────────────────────────┤
  │ Listing images      │ S3 + CloudFront  │ Object storage + CDN         │
  ├─────────────────────┼──────────────────┼──────────────────────────────┤
  │ Event streaming     │ Apache Kafka     │ Async notifications, CDC     │
  └─────────────────────┴──────────────────┴──────────────────────────────┘
```

---

## 6. The Core Problem: Preventing Double Booking

This is **THE most important section** of any booking system design interview.

> **For any given resource and time slot, the number of confirmed bookings
> must NEVER exceed the available inventory.**

### 6.1 Approach 1: Pessimistic Locking (SELECT ... FOR UPDATE)

The database acquires an **exclusive lock** on the row. No other transaction
can read-for-update or modify it until the lock is released.

```sql
BEGIN;
SELECT booked_slots, total_slots FROM availability
WHERE listing_id = 42 AND date = '2025-12-25'
FOR UPDATE;                                -- ← EXCLUSIVE ROW LOCK

-- Application: if booked_slots < total_slots then proceed
UPDATE availability SET booked_slots = booked_slots + 1
WHERE listing_id = 42 AND date = '2025-12-25';

INSERT INTO bookings (user_id, listing_id, check_in, ...)
VALUES (101, 42, '2025-12-25', ...);
COMMIT;                                    -- ← Lock released
```

**Two Concurrent Requests — Pessimistic Locking:**

```
  User A                     Database                     User B
    │                           │                            │
    │  SELECT ... FOR UPDATE    │                            │
    │──────────────────────────▶│  ┌────────────────────┐    │
    │  ◀── slots=0/1 (open)    │  │ ROW LOCKED by A    │    │
    │                           │  └────────────────────┘    │
    │                           │    SELECT ... FOR UPDATE   │
    │                           │◀──────────────────────────│
    │                           │  ┌────────────────────┐    │
    │  UPDATE booked=1          │  │  B is BLOCKED!     │    │
    │──────────────────────────▶│  │  Waiting for lock  │    │
    │  INSERT booking           │  │        ⏳          │    │
    │──────────────────────────▶│  └────────────────────┘    │
    │  COMMIT ✅                │                            │
    │──────────────────────────▶│  Lock released → B runs    │
    │                           │  ──▶ slots=1/1 (FULL!)     │
    │                           │  ROLLBACK ❌               │
  BOOKED ✅                                           REJECTED ❌
```

| Pros                            | Cons                               |
|---------------------------------|------------------------------------|
| Simple, strong guarantee        | Blocks concurrent transactions     |
| Works out of the box in PG      | Deadlock risk with multiple rows   |
| Easy to reason about            | Throughput bottleneck under load   |

### 6.2 Approach 2: Optimistic Locking (Version Column)

No locks during read. Check a `version` column at write time. If someone else
modified the row, our update affects 0 rows → **retry**.

```sql
-- Step 1: Read (no lock)
SELECT booked_slots, total_slots, version FROM availability
WHERE listing_id = 42 AND date = '2025-12-25';
-- Returns: booked_slots=0, total_slots=1, version=5

-- Step 2: Update with version check
UPDATE availability
SET booked_slots = booked_slots + 1, version = version + 1
WHERE listing_id = 42 AND date = '2025-12-25'
  AND version = 5;                     -- ← only if unchanged!

-- rows_affected = 1 → SUCCESS | rows_affected = 0 → CONFLICT, RETRY
```

**Two Concurrent Requests — Optimistic Locking:**

```
  User A                     Database                     User B
    │                           │                            │
    │  SELECT (no lock)         │     SELECT (no lock)       │
    │──────────────────────────▶│◀──────────────────────────│
    │  ◀── version=5, slots=0  │  ──▶ version=5, slots=0   │
    │                           │                            │
    │  Both see SAME state — both think room is open!       │
    │                           │                            │
    │  UPDATE WHERE version=5   │                            │
    │──────────────────────────▶│                            │
    │  ◀── rows_affected=1 ✅  │                            │
    │  (version now = 6)        │     UPDATE WHERE version=5 │
    │                           │◀──────────────────────────│
    │                           │  ──▶ rows_affected=0 ❌   │
    │                           │     (version is 6 now!)    │
    │                           │     RETRY → sees FULL      │
  BOOKED ✅                                           REJECTED ❌
```

| Pros                            | Cons                               |
|---------------------------------|------------------------------------|
| No blocking — high throughput   | Requires retry logic               |
| No deadlocks possible           | Many retries under high contention |

### 6.3 Approach 3: Database Constraints (UNIQUE Constraint)

For **one-booking-per-slot** scenarios (specific seats), use UNIQUE constraints:

```sql
CREATE TABLE seat_bookings (
    booking_id  BIGSERIAL PRIMARY KEY,
    listing_id  BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    show_date   DATE NOT NULL,
    user_id     BIGINT NOT NULL,
    CONSTRAINT uq_seat UNIQUE (listing_id, seat_number, show_date)
);

-- User A: INSERT ... VALUES (42, 'A15', '2025-12-25', 101);  → SUCCESS ✅
-- User B: INSERT ... VALUES (42, 'A15', '2025-12-25', 202);  → UNIQUE VIOLATION ❌
```

| Pros                          | Cons                                  |
|-------------------------------|---------------------------------------|
| Database enforces correctness | Limited to one-booking-per-slot model |
| Simplest implementation       | Can't handle partial inventory easily |

### 6.4 Approach 4: Distributed Lock (Redis)

Use Redis to create a **temporary hold** while the user completes payment:

```
  SET lock:listing:42:date:2025-12-25 user_101 NX EX 600
  │                                    │       │  │   │
  │                                    │       │  │   └─ 600s = 10 min TTL
  │                                    │       │  └───── EX = set expiry
  │                                    │       └──────── NX = only if NOT exists
  │                                    └──────────────── value = who holds it
  └───────────────────────────────────────────────────── key
```

```
  User A                    Redis                     User B
    │  SET ... NX EX 600      │                          │
    │────────────────────────▶│                          │
    │  ◀── OK (lock acquired) │                          │
    │                         │   SET ... NX EX 600      │
    │  [Fills payment form    │◀────────────────────────│
    │   for up to 10 min]     │  ──▶ nil (LOCK EXISTS)   │
    │                         │   "Temporarily held by   │
    │  Payment succeeds →     │    another user"         │
    │  Confirm in DB →        │                          │
    │  DEL lock:listing:...   │                          │
    │────────────────────────▶│  Lock released           │
    │                         │                          │
    │  ═══ IF timeout/fail: TTL auto-expires in 10 min ═══
```

| Pros                          | Cons                               |
|-------------------------------|------------------------------------|
| Great UX — time to pay        | Redis is not ACID — edge cases     |
| Auto-cleanup via TTL          | Must still confirm in database     |

### 6.5 Recommended: Optimistic Locking + Temporary Hold + Idempotency

The **production-grade approach** combines multiple strategies:

```
  ┌─────────────────────────────────────────────────────────────┐
  │              RECOMMENDED APPROACH (LAYERED)                 │
  │                                                             │
  │  Layer 1: Redis Temporary Hold                              │
  │  ├── Acquire hold (SET NX EX 600)                           │
  │  ├── Provides good UX (10 min to complete payment)          │
  │  └── Auto-expires if user abandons                          │
  │                                                             │
  │  Layer 2: Optimistic Locking in PostgreSQL                  │
  │  ├── UPDATE ... WHERE version = X                           │
  │  ├── Final source of truth                                  │
  │  └── Handles edge cases where Redis lock leaks              │
  │                                                             │
  │  Layer 3: Idempotency Key                                   │
  │  ├── Client sends unique key per booking attempt            │
  │  ├── UNIQUE constraint on bookings.idempotency_key          │
  │  └── Safe to retry on network failure                       │
  │                                                             │
  │  Together: Redis hold → Optimistic DB write → Idempotent    │
  └─────────────────────────────────────────────────────────────┘
```

---

## 7. Search and Discovery

### Search Architecture

```
  ┌──────────┐     ┌──────────────┐     ┌───────────────────────┐
  │  Client   │────▶│  API Gateway │────▶│    Search Service     │
  │ (filters) │     └──────────────┘     │                       │
  └──────────┘                          │  ┌─────────────────┐  │
                                        │  │  Redis Cache    │  │
     Filters:                           │  │  (5 min TTL)    │──┼─▶ HIT → return
     - City: "Paris"                    │  └────────┬────────┘  │
     - Dates: Dec 24-27                 │       MISS│           │
     - Guests: 2                        │           ▼           │
     - Price: $50-$200                  │  ┌─────────────────┐  │
     - Amenities: wifi, pool            │  │  Elasticsearch  │  │
                                        │  │  (full-text,    │  │
                                        │  │   geo, facets)  │  │
                                        │  └────────┬────────┘  │
                                        │           ▼           │
                                        │  ┌─────────────────┐  │
                                        │  │ Merge with      │  │
                                        │  │ availability    │  │
                                        │  │ (Redis/DB)      │  │
                                        │  └─────────────────┘  │
                                        └───────────────────────┘
```

### Elasticsearch Query Example

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "city": "Paris" } },
        { "range": { "max_guests": { "gte": 2 } } },
        { "range": { "base_price": { "gte": 50, "lte": 200 } } }
      ],
      "filter": [
        { "terms": { "amenities": ["wifi", "pool"] } },
        { "geo_distance": { "distance": "10km",
            "location": { "lat": 48.8566, "lon": 2.3522 } } }
      ]
    }
  },
  "sort": [{ "base_price": "asc" }], "from": 0, "size": 20
}
```

### Keeping Elasticsearch in Sync

```
  PostgreSQL                   Kafka                    Elasticsearch
  ┌──────────┐          ┌──────────────┐          ┌──────────────┐
  │ listings │──CDC────▶│  listing.    │──consume─▶│  ES Index   │
  │  table   │          │  changes    │           │  (listings) │
  └──────────┘          └──────────────┘          └──────────────┘
  Change Data Capture (Debezium) — eventual consistency is OK for search.
```

---

## 8. Booking Flow (Step by Step)

### Complete Booking Sequence

```
  ┌────────┐ ┌──────────┐ ┌───────┐ ┌─────┐ ┌────────┐ ┌───────┐ ┌───────┐
  │ Client │ │API Server│ │ Redis │ │ DB  │ │Payment │ │Notify │ │ Kafka │
  └───┬────┘ └────┬─────┘ └───┬───┘ └──┬──┘ └───┬────┘ └───┬───┘ └───┬───┘
      │           │            │        │        │          │         │
      │ ① Select  │            │        │        │          │         │
      │  listing  │            │        │        │          │         │
      │──────────▶│            │        │        │          │         │
      │           │ ② Check    │        │        │          │         │
      │           │  avail.    │        │        │          │         │
      │           │───────────▶│        │        │          │         │
      │           │ HIT/MISS ◀─│───────▶│        │          │         │
      │ ◀─ Avail! │            │        │        │          │         │
      │           │            │        │        │          │         │
      │ ③ Confirm │            │        │        │          │         │
      │──────────▶│ ④ Hold     │        │        │          │         │
      │           │───────────▶│        │        │          │         │
      │           │ SET NX OK ◀│        │        │          │         │
      │ ◀─ Payment form (10m) │        │        │          │         │
      │           │            │        │        │          │         │
      │ ⑤ Pay     │            │        │        │          │         │
      │──────────▶│ ⑥ Charge   │        │        │          │         │
      │           │────────────┼────────┼───────▶│          │         │
      │           │ ◀── Authorized ─────┤        │          │         │
      │           │ ⑦ Confirm  │        │        │          │         │
      │           │  DB (OL)   │        │        │          │         │
      │           │────────────┼───────▶│        │          │         │
      │           │  UPDATE avail WHERE version=X│          │         │
      │           │  INSERT booking     │        │          │         │
      │           │ ◀──── COMMIT        │        │          │         │
      │           │ ⑧ Release  │        │        │          │         │
      │           │───────────▶│ DEL    │        │          │         │
      │           │ ⑨ Event    │        │        │          │         │
      │           │────────────┼────────┼────────┼──────────┼────────▶│
      │           │            │        │        │     ◀── consume ──│
      │           │            │        │        │    ⑩ Email+Push   │
      │ ◀─ Booking confirmed!  │        │        │          │         │
```

### Failure Handling Matrix

```
  ┌──────────────────────┬──────────────────────────────────────────┐
  │ Failure Point        │ Recovery Action                          │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ Redis hold fails     │ "Temporarily held" — retry in minutes   │
  │ (already held)       │                                         │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ Payment declined     │ Release Redis hold, show error, retry   │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ Payment timeout      │ Async check: charged → book, else hold  │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ DB commit fails      │ Refund payment, release hold, apologize │
  │ (version conflict)   │                                         │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ User abandons        │ Redis TTL auto-expires in 10 minutes    │
  ├──────────────────────┼──────────────────────────────────────────┤
  │ Server crash mid-    │ Idempotency key prevents double-booking │
  │ booking              │ on retry with same key                  │
  └──────────────────────┴──────────────────────────────────────────┘
```

---

## 9. Payment Integration

### Payment Flow with Stripe

```
  ┌────────┐   ┌──────────────┐   ┌────────────────┐   ┌─────────┐
  │ Client │   │Booking Svc   │   │Payment Svc     │   │ Stripe  │
  └───┬────┘   └──────┬───────┘   └───────┬────────┘   └────┬────┘
      │  Book request  │                   │                  │
      │───────────────▶│  Create intent    │                  │
      │                │──────────────────▶│  PaymentIntent   │
      │                │                   │─────────────────▶│
      │                │                   │  ◀── client_secret
      │                │  ◀── client_secret│                  │
      │  ◀── client_secret                 │                  │
      │  Confirm (client-side SDK)         │                  │
      │────────────────┼──────────────────┼─────────────────▶│
      │                │                   │  Webhook:        │
      │                │                   │  payment_intent  │
      │                │                   │  .succeeded      │
      │                │                   │◀─────────────────│
      │                │  Payment OK       │                  │
      │                │◀──────────────────│                  │
      │                │  Confirm booking  │                  │
      │  ◀── Confirmed │                   │                  │
```

### Idempotency for Payment Safety

```
  ┌────────────────────────────────────────────────────────────────┐
  │  WHY IDEMPOTENCY KEYS MATTER                                  │
  │  WITHOUT idempotency:                                         │
  │    Req 1: Charge $200 → SUCCESS (client doesn't know)         │
  │    Req 2: Charge $200 → SUCCESS (DOUBLE CHARGE! 💀)           │
  │                                                                │
  │  WITH idempotency:                                            │
  │    Req 1: Charge $200, key="abc-123" → SUCCESS                │
  │    Req 2: Charge $200, key="abc-123" → Returns same result    │
  │                                                                │
  │  Key = SHA256(user_id + listing_id + dates)                   │
  │  Stored as UNIQUE constraint in payments table                │
  └────────────────────────────────────────────────────────────────┘
```

---

## 10. Notification System

```
  ┌──────────────┐    ┌──────────────┐    ┌──────────────────────────┐
  │   Booking    │    │    Kafka     │    │  Notification Service    │
  │   Service    │───▶│  Topics:     │───▶│                          │
  └──────────────┘    │  - confirmed │    │  ┌────────────────────┐  │
  ┌──────────────┐    │  - cancelled │    │  │  Event Router      │  │
  │   Payment    │───▶│  - payment.  │    │  │  confirmed → email │  │
  │   Service    │    │    failed    │    │  │             + push │  │
  └──────────────┘    │  - reminder  │    │  │  cancelled → email │  │
  ┌──────────────┐    │    .24h      │    │  │  reminder  → push  │  │
  │  Scheduler   │───▶└──────────────┘    │  │             + SMS  │  │
  │ (24h before) │                        │  └────────┬───────────┘  │
  └──────────────┘                        │           │              │
                                          │  ┌────────┴───────────┐  │
                                          │  │ Email (SES)        │  │
                                          │  │ Push  (FCM/APNs)   │  │
                                          │  │ SMS   (Twilio)     │  │
                                          │  └────────────────────┘  │
                                          └──────────────────────────┘
```

| Event               | Email | Push | SMS | In-App |
|---------------------|:-----:|:----:|:---:|:------:|
| Booking confirmed   |  ✅   |  ✅  | ❌  |   ✅   |
| Booking cancelled   |  ✅   |  ✅  | ❌  |   ✅   |
| Payment failed      |  ✅   |  ✅  | ✅  |   ✅   |
| Reminder (24h)      |  ❌   |  ✅  | ✅  |   ✅   |
| Refund processed    |  ✅   |  ✅  | ❌  |   ✅   |

---

## 11. Handling Flash Sales / Surge Traffic

### The Problem

```
  ╔═══════════════════════════════════════════════════════════════╗
  ║  Taylor Swift concert — 50,000 tickets, 2M users waiting     ║
  ║  500,000 click "Buy" in 60 seconds = ~8,333 req/sec          ║
  ║  Normal peak: ~1.2/sec → That's a 7,000x spike!              ║
  ║  Without prep: DB melts, double-bookings, PR disaster.       ║
  ╚═══════════════════════════════════════════════════════════════╝
```

### Solution 1: Virtual Waiting Queue

```
  500K users click "Buy"
        │
        ▼
  ┌───────────────────┐
  │  Load Balancer    │  Throttle to 10K/sec
  └────────┬──────────┘
           ▼
  ┌───────────────────┐
  │  Queue Service    │
  │  Redis Sorted Set │──▶ "You are #47,832 in queue"
  │  ZADD queue       │    "Estimated wait: 8 minutes"
  │   {timestamp}     │
  │   {user_id}       │
  └────────┬──────────┘
           │  Dequeue 500 users per batch / 30 sec
           ▼
  ┌───────────────────┐
  │  Booking Service  │──▶ Manageable DB load
  │  10 min per batch │    Total: ~50 min to drain queue
  └───────────────────┘
```

### Solution 2: Pre-warm Inventory in Redis (Atomic Counters)

```
  BEFORE SALE:  SET ticket:concert:789:count 50000

  DURING SALE (each attempt):
    result = DECR ticket:concert:789:count

    if result >= 0  → Reserved! Proceed to payment.
    else            → INCR (undo), "SOLD OUT"

  DECR is ATOMIC in Redis → no race conditions!
  Single-threaded Redis handles 100K+ ops/sec.
  Only confirmed reservations hit the database.
```

### Solution 3: Rate Limiting at Entry Point

```
  ┌──────────┐     ┌──────────────┐     ┌──────────────┐
  │  Users   │────▶│   Nginx /    │────▶│  Application │
  │ 500K/min │     │ API Gateway  │     │   Servers    │
  └──────────┘     │              │     └──────────────┘
                   │ • 1 req/sec  │     Excess → HTTP 429
                   │   per user   │     "Please wait and
                   │ • 10K total  │      try again"
                   │ • Token      │
                   │   bucket     │
                   └──────────────┘
```

---

## 12. Scaling Strategy

### Multi-Layer Scaling

```
  ┌─────────────────────────────────────────────────────────────────┐
  │  Layer 1: CDN — Static assets, listing photos, 80%+ traffic off │
  │  Layer 2: API Gateway — Rate limiting, auth, routing            │
  │  Layer 3: Stateless App Servers (K8s HPA)                       │
  │  ├── Search: 4-20 pods  Booking: 2-10  Payment: 2-8            │
  │  Layer 4: Redis Cluster                                         │
  │  ├── Availability cache (30s), Search cache (5min), Locks       │
  │  Layer 5: Database                                              │
  │  ├── PG primary + read replicas, ES cluster (3+ nodes)          │
  │  └── Sharding by region or listing_id                           │
  └─────────────────────────────────────────────────────────────────┘
```

### Database Sharding + Read Replicas

```
  SHARDING BY REGION:
  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
  │   Shard 1    │  │   Shard 2    │  │   Shard 3    │
  │   Americas   │  │ Europe/Africa│  │  Asia-Pacific│
  └──────────────┘  └──────────────┘  └──────────────┘
  Pros: Locality    Cons: Cross-region = scatter-gather

  READ REPLICAS:
  ┌───────────┐   ┌──────────┐  ◀── Search Service
  │ Primary   │──▶│ Replica 1│  ◀── Analytics
  │ (Writer)  │──▶│ Replica 2│  ◀── Booking History
  └───────────┘   └──────────┘
       ▲
   Booking Svc / Payment Svc (writes ONLY to primary)
```

---

## 13. Full System Architecture

```
  ┌──────────────────────────────────────────────────────────────────────┐
  │                      COMPLETE BOOKING SYSTEM                        │
  │                                                                     │
  │  ┌─────────┐  ┌──────────┐  ┌──────────┐                           │
  │  │ Web App │  │Mobile App│  │3rd Party │                           │
  │  └────┬────┘  └────┬─────┘  └────┬─────┘                           │
  │       └──────────┬─┴─────────────┘                                  │
  │                  ▼                                                  │
  │         ┌──────────────┐                                            │
  │         │  CloudFront  │──── Static assets, images                  │
  │         └──────┬───────┘                                            │
  │                ▼                                                    │
  │         ┌──────────────┐                                            │
  │         │ API Gateway  │──── Auth, rate limit, routing              │
  │         └──────┬───────┘                                            │
  │      ┌─────────┼──────────┬──────────────┐                          │
  │      ▼         ▼          ▼              ▼                          │
  │ ┌─────────┐┌─────────┐┌─────────┐┌──────────┐                      │
  │ │ Search  ││ Booking ││ Payment ││  User    │                      │
  │ │ Service ││ Service ││ Service ││ Service  │                      │
  │ └────┬────┘└────┬────┘└────┬────┘└────┬─────┘                      │
  │      │          │          │          │                             │
  │      ▼          ▼          │          │                             │
  │ ┌─────────┐┌──────────┐   │          │                             │
  │ │Elastic  ││  Redis   │◀──┘          │                             │
  │ │Search   ││ Cluster  │              │                             │
  │ │Cluster  ││(Cache+   │              │                             │
  │ │         ││ Locks)   │              │                             │
  │ └─────────┘└──────────┘              │                             │
  │                                      │                             │
  │ ┌────────────────────────────────────┘                             │
  │ │                                                                  │
  │ ▼                                                                  │
  │ ┌───────────────────────────────────────────────┐                   │
  │ │           PostgreSQL Cluster                  │                   │
  │ │  ┌────────┐   ┌─────────┐   ┌─────────┐      │                   │
  │ │  │Primary │──▶│Replica 1│   │Replica 2│      │                   │
  │ │  │(Write) │──▶│ (Read)  │   │ (Read)  │      │                   │
  │ │  └────────┘   └─────────┘   └─────────┘      │                   │
  │ └───────────────────────────────────────────────┘                   │
  │                                                                     │
  │ ┌───────────────────────────────────────────────┐                   │
  │ │              Apache Kafka                     │                   │
  │ │  booking.confirmed | booking.cancelled        │                   │
  │ │  payment.processed | listing.updated (CDC)    │                   │
  │ └──────────────────┬────────────────────────────┘                   │
  │         ┌──────────┼──────────┐                                     │
  │         ▼          ▼          ▼                                     │
  │  ┌───────────┐┌──────────┐┌──────────┐                              │
  │  │Notif. Svc ││Analytics ││ ES Sync  │                              │
  │  │Email/Push ││Pipeline  ││(Debezium)│                              │
  │  │SMS        ││Dashboard ││ PG → ES  │                              │
  │  └───────────┘└──────────┘└──────────┘                              │
  │                                                                     │
  │ ┌──────────┐                                                        │
  │ │   S3     │──── Listing images, backups                            │
  │ └──────────┘                                                        │
  └──────────────────────────────────────────────────────────────────────┘
```

### Request Flow Summary

```
  SEARCH:  Client → CDN → Gateway → Search Svc → Redis (hit?)
           → Elasticsearch → Merge availability → Return

  BOOK:    Client → Gateway → Booking Svc → Redis (hold)
           → Payment Svc (Stripe) → PostgreSQL (OL write)
           → Kafka → Notification Svc → Email/Push

  CANCEL:  Client → Gateway → Booking Svc → PostgreSQL (update)
           → Payment Svc (refund) → Kafka → Notification Svc
```

---

## 14. Key Takeaways

```
  ╔════════════════════════════════════════════════════════════════════╗
  ║                     8 KEY TAKEAWAYS                               ║
  ╠════════════════════════════════════════════════════════════════════╣
  ║                                                                   ║
  ║  1. DOUBLE-BOOKING IS THE #1 ENEMY                                ║
  ║     Use optimistic locking + DB constraints as your safety net.   ║
  ║     This is what interviewers care about most.                    ║
  ║                                                                   ║
  ║  2. SEPARATE READ AND WRITE PATHS                                 ║
  ║     Search tolerates eventual consistency (ES + Redis cache).     ║
  ║     Bookings require ACID transactions (PostgreSQL).              ║
  ║                                                                   ║
  ║  3. TEMPORARY HOLDS ENABLE GOOD UX                                ║
  ║     Redis with TTL holds a resource while user pays. Auto-expire  ║
  ║     prevents ghost bookings. 10-min window is standard.           ║
  ║                                                                   ║
  ║  4. IDEMPOTENCY KEYS PREVENT DISASTER                             ║
  ║     Every booking and payment operation MUST be idempotent.       ║
  ║     Unique key per operation with a UNIQUE DB constraint.         ║
  ║                                                                   ║
  ║  5. FLASH SALES NEED SPECIAL ARCHITECTURE                         ║
  ║     Pre-warm inventory in Redis (atomic DECR), virtual queues,    ║
  ║     and rate-limit at the gateway for 100x traffic spikes.        ║
  ║                                                                   ║
  ║  6. PAYMENTS ARE ASYNCHRONOUS                                     ║
  ║     Use webhook-based confirmation from Stripe/PayPal. Design     ║
  ║     for failures, timeouts, and refunds as first-class flows.     ║
  ║                                                                   ║
  ║  7. CACHE AGGRESSIVELY, INVALIDATE CAREFULLY                      ║
  ║     Cache search (5 min TTL), availability (30 sec), statics      ║
  ║     (CDN). Booking-critical data always hits PostgreSQL.          ║
  ║                                                                   ║
  ║  8. EVENT-DRIVEN FOR EVERYTHING NON-CRITICAL                      ║
  ║     Notifications, analytics, search sync, and audit logs flow    ║
  ║     through Kafka. Decouple from the booking critical path.       ║
  ╚════════════════════════════════════════════════════════════════════╝
```

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
