# Chapter 31: Design a Ride-Sharing Service (Uber / Lyft)

## 1. Problem Statement

Design a ride-sharing platform like Uber or Lyft that connects riders with nearby drivers
in real-time. The system must handle millions of location updates per second, efficiently
match riders with optimal drivers, provide real-time trip tracking, calculate dynamic
fares, and process payments — all with extremely low latency.

---

## 2. Functional Requirements

| ID | Requirement | Description |
|----|-------------|-------------|
| FR1 | Request Ride | Rider enters pickup and dropoff locations, selects ride type |
| FR2 | Driver Matching | System matches rider with the best nearby available driver |
| FR3 | Real-time Tracking | Both rider and driver see each other's location in real-time |
| FR4 | ETA Calculation | Estimated time of arrival for driver to pickup and trip duration |
| FR5 | Fare Calculation | Calculate fare based on distance, time, ride type, and surge |
| FR6 | Surge Pricing | Dynamic pricing based on supply-demand imbalance |
| FR7 | Payments | Charge rider, pay driver after trip completion |
| FR8 | Ratings | Both rider and driver rate each other (1-5 stars) |
| FR9 | Trip History | View past trips with details and receipts |
| FR10 | Driver Mode | Drivers can go online/offline, accept/reject ride requests |

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR1 | Scale | 20M DAU riders, 5M active drivers |
| NFR2 | Trips | 10M rides/day, ~115 rides/sec |
| NFR3 | Location Updates | Drivers send updates every 3-5 seconds |
| NFR4 | Match Latency | Match rider to driver in < 5 seconds |
| NFR5 | Tracking Latency | Location updates reflected in < 2 seconds |
| NFR6 | Availability | 99.99% uptime |
| NFR7 | Geo Coverage | Multiple cities, expandable globally |

---

## 4. Capacity Estimation

### 4.1 Location Update Traffic

```
Active drivers at peak: 2M concurrent
Location update frequency: every 4 seconds

Location updates: 2,000,000 / 4 = 500,000 updates/sec
Each update: ~100 bytes (driver_id, lat, lng, timestamp, heading, speed)
Bandwidth: 500K * 100 bytes = 50 MB/sec ingress

This is the HIGHEST throughput operation in the entire system.
```

### 4.2 Ride Request Traffic

```
10M rides/day = ~115 rides/sec average
Peak: ~400 rides/sec (rush hour in major cities combined)

Each ride involves:
  1. Ride request (rider → server)
  2. Driver matching (server → driver query)
  3. ~5 real-time location updates during ride (per second for both)
  4. Fare calculation
  5. Payment processing
```

### 4.3 Storage

```
Trip Records:
  10M trips/day * 1 KB each = 10 GB/day
  Per year: ~3.6 TB (manageable in a relational DB)

Location History:
  500K updates/sec * 100 bytes = 50 MB/sec
  Per day: ~4.3 TB (store in time-series DB, expire after 30 days)
  
Driver/Rider Profiles:
  25M users * 2 KB = 50 GB (trivial)
```

---

## 5. Geospatial Indexing

The fundamental challenge: **How do we efficiently find nearby drivers?**

### 5.1 Approach 1: Geohash

```
Geohash encodes a 2D location (lat, lng) into a 1D string:

  (37.7749, -122.4194) → "9q8yyk" (precision level 6)

How it works:
  - Divide the world into a grid recursively
  - Each character narrows the area by 32x
  - Nearby locations share common prefixes

Precision levels:
  Length 4: ~39km x 19.5km  (city-level)
  Length 5: ~4.9km x 4.9km  (neighborhood)
  Length 6: ~1.2km x 0.6km  (few blocks) ← good for ride matching
  Length 7: ~153m x 153m    (very precise)

Finding nearby drivers:
  1. Compute rider's geohash at precision 6: "9q8yyk"
  2. Query drivers in geohash "9q8yyk" AND its 8 neighbors:
     "9q8yyh", "9q8yym", "9q8yys", "9q8yye", "9q8yy7", etc.
  3. This gives all drivers within ~1-2 km radius

  +--------+--------+--------+
  | 9q8yyh | 9q8yyk | 9q8yym |
  |        | RIDER  |        |
  +--------+--HERE--+--------+
  | 9q8yy5 | 9q8yy7 | 9q8yye |
  |        |        |        |
  +--------+--------+--------+
  | 9q8yy4 | 9q8yy6 | 9q8yyd |
  +--------+--------+--------+

Storage in Redis:
  Key: "drivers:geohash:{geohash_prefix}"
  Value: Set of {driver_id, lat, lng, status}
  
  Example:
    "drivers:geohash:9q8yyk" → {driver_123, driver_456, driver_789}

Pros:
  - Simple, well-understood
  - Efficient with Redis sorted sets
  - Easy to shard by geohash prefix

Cons:
  - Edge effects (drivers near cell boundaries)
  - Fixed grid (not adaptive to density)
  - Must search 9 cells (center + 8 neighbors)
```

### 5.2 Approach 2: QuadTree

```
A QuadTree recursively divides 2D space into 4 quadrants:

  +---------------------+
  |          |          |
  |   NW     |   NE     |
  |          |          |
  +----------+----------+
  |     |    |          |
  | SW  | SW |   SE     |
  | NW  | NE |          |
  +--+--+----+----------+
  |  |  |    |          |
  +--+--+----+          |
  |     |    |          |
  +-----+----+----------+

Rules:
  - Start with one cell covering the entire city
  - If a cell has > N drivers (e.g., 500), split into 4 children
  - Dense areas (downtown) → many small cells
  - Sparse areas (suburbs) → few large cells
  - Adaptive to driver density!

Finding nearby drivers:
  1. Locate the leaf cell containing the rider
  2. Get all drivers in that cell
  3. If not enough, expand to parent cell or adjacent cells
  4. Filter by actual distance (Haversine formula)

In-Memory Structure:
  Each QuadTree node:
    {
      bounds: {min_lat, max_lat, min_lng, max_lng},
      drivers: [d1, d2, ...],  // if leaf
      children: [NW, NE, SW, SE],  // if internal
      is_leaf: boolean
    }

Pros:
  - Adaptive to density (small cells in busy areas)
  - Efficient nearest-neighbor search
  
Cons:
  - More complex to implement
  - Must update tree as drivers move (remove from old cell, add to new)
  - Hard to distribute across servers
```

### 5.3 Approach 3: S2 Cells (Google's Approach)

```
Google's S2 geometry library:
  - Maps the sphere (Earth) to a cube, then to a Hilbert curve
  - Each cell has a unique 64-bit ID
  - Hierarchical: 30 levels of precision
  - Nearby cells have nearby IDs (Hilbert curve property)

Level mapping:
  Level 12: ~3.3 km² (neighborhood)
  Level 14: ~0.2 km² (few blocks)
  Level 16: ~0.01 km² (very precise)

Advantages over Geohash:
  - No edge effects (cells tile the sphere uniformly)
  - Better locality (Hilbert curve preserves proximity)
  - Efficient covering: describe any shape with minimal cells

Used by: Uber (H3), Google Maps, Foursquare
```

### 5.4 Our Choice: Geohash with Redis

```
For this design, we use Geohash + Redis for simplicity and proven scale.

Redis supports native geospatial operations:
  GEOADD drivers -122.4194 37.7749 "driver_123"
  GEORADIUS drivers -122.4194 37.7749 2 km COUNT 20 ASC
  → Returns 20 closest drivers within 2 km, sorted by distance
```

---

## 6. Location Service

### 6.1 Driver Location Update Flow

```
Every 3-5 seconds, each active driver sends their location:

  Driver App         Location Service        Redis (Geo)       Location History
      |                     |                    |                    |
      |--{lat, lng, ts}--->|                    |                    |
      |                     |--GEOADD ---------->|                    |
      |                     |  drivers {lng}     |                    |
      |                     |  {lat} driver_123  |                    |
      |                     |                    |                    |
      |                     |--publish to Kafka----------------->    |
      |                     |  (async, for history + analytics)      |
      |<--200 OK -----------|                    |                    |

Processing 500K updates/sec:
  - Location Service: stateless, horizontally scalable
  - Redis: cluster mode, sharded by city/region
  - Kafka: buffer for async downstream processing
```

### 6.2 Location Data Architecture

```
Real-time (current position):
  Redis GEOADD/GEORADIUS
  Key: "drivers:{city_id}"
  Fast reads for driver matching
  
Historical (trip tracking, analytics):
  Kafka → Time-series DB (InfluxDB / TimescaleDB)
  Retain: 30 days for trip replay, 1 year for analytics
  Schema:
    driver_id | timestamp | lat | lng | speed | heading | trip_id
```

---

## 7. Ride Matching Algorithm

### 7.1 Matching Flow

```
When a rider requests a ride:

  1. Rider sends: {pickup_lat, pickup_lng, dropoff_lat, dropoff_lng, ride_type}
  2. Find nearby available drivers (GEORADIUS, 2km radius, max 20)
  3. Filter: only drivers with matching ride_type and status = 'available'
  4. Rank drivers by composite score:
     Score = w1 * (1/ETA) + w2 * rating + w3 * acceptance_rate
  5. Send ride request to top-ranked driver
  6. Driver has 15 seconds to accept/decline
  7. If declined or timeout: send to next driver
  8. If all nearby drivers decline: expand radius and retry
  9. If no driver found: notify rider "no drivers available"

  Rider           Ride Service        Location (Redis)      Driver App
    |                  |                    |                    |
    |--request ride -->|                    |                    |
    |                  |--GEORADIUS 2km---->|                    |
    |                  |<--[d1,d2,d3,...]---|                    |
    |                  |                    |                    |
    |                  |--rank by ETA+rating|                    |
    |                  |--select best: d1   |                    |
    |                  |                    |                    |
    |                  |--ride request --------------------------->|
    |                  |                    |                    |
    |                  |<--ACCEPT --------------------------------|
    |                  |                    |                    |
    |<--driver matched-|                    |                    |
    |  {driver_info,   |                    |                    |
    |   ETA: 4 min}    |                    |                    |
```

### 7.2 Dispatch Optimization (Batch Matching)

```
Instead of matching one ride at a time, batch matching optimizes globally:

  Every 2 seconds:
    1. Collect all pending ride requests in a city
    2. Collect all available drivers
    3. Run optimization algorithm (Hungarian algorithm / min-cost matching)
    4. Minimize total wait time across ALL rides

  Why batch?
    - Greedy matching (one-at-a-time) may assign driver A to rider X,
      when A was better for rider Y who requests 1 second later
    - Batch matching finds globally optimal assignments
    
  Used by: Uber (using a custom optimization engine)
```

---

## 8. ETA Calculation

### 8.1 Routing Approaches

```
How to estimate "driver will arrive in 4 minutes"?

Approach 1: Straight-line distance (Haversine)
  - Fast but inaccurate (ignores roads, traffic)
  - Only for initial rough estimate

Approach 2: Road-network routing (Dijkstra / A*)
  - Build a graph: intersections = nodes, roads = edges
  - Edge weights: distance + speed limit + real-time traffic
  - A* search from driver to pickup point
  - More accurate but computationally expensive

Approach 3: Pre-computed routing (Contraction Hierarchies)
  - Pre-process the road graph offline
  - Create "shortcut" edges for highways/major roads
  - Query time reduced from seconds to milliseconds
  - Used by Google Maps, OSRM
  
Approach 4: ML-based ETA (Uber's approach)
  - Train model on historical trip data
  - Features: distance, time of day, day of week, weather, events
  - Learns city-specific patterns (traffic, construction, etc.)
  - Most accurate, accounts for real-world conditions

Our approach: Contraction Hierarchies + ML correction factor
```

### 8.2 ETA Service Architecture

```
  +------------------+     +------------------+     +------------------+
  | Request:         |     | Routing Engine   |     | ML ETA Model    |
  | driver_pos →     | --> | (OSRM /          | --> | (correction     |
  | pickup_pos       |     |  GraphHopper)    |     |  factor)        |
  +------------------+     | Route + base ETA |     | Adjusted ETA    |
                           +------------------+     +------------------+
                                    |                        |
                           +--------v--------+      +--------v--------+
                           | Road graph      |      | Feature store   |
                           | (pre-computed   |      | (traffic, time, |
                           |  contraction    |      |  weather)       |
                           |  hierarchies)   |      +-----------------+
                           +-----------------+
```

---

## 9. Ride Lifecycle

```
Complete lifecycle of a ride:

  REQUESTING ──> MATCHING ──> ACCEPTED ──> EN_ROUTE ──> ARRIVED ──> IN_TRIP ──> COMPLETED ──> PAYMENT
       |             |            |            |            |           |            |            |
       v             v            v            v            v           v            v            v
    Rider          Find        Driver       Driver      Driver      Rider        Trip        Charge
    submits       nearby       accepts,    drives to   arrives,    in car,     ends at      rider,
    request      drivers,     rider gets  pickup,     rider        GPS        dropoff      pay
                  rank &       driver     rider sees  notified    tracking                driver
                  dispatch     info+ETA   real-time              active

  State Machine:
    +------------+     +----------+     +----------+
    | REQUESTING | --> | MATCHING | --> | ACCEPTED |
    +------------+     +----+-----+     +----+-----+
                            |                |
                      (no driver)       +----v------+
                            |           | EN_ROUTE  |
                      +-----v----+      | (to       |
                      | CANCELLED|      |  pickup)  |
                      +----------+      +----+------+
                                             |
                                        +----v------+     +----------+
                                        | ARRIVED   | --> | IN_TRIP  |
                                        +----+------+     +----+-----+
                                             |                 |
                                        (rider no-show)   +---v--------+
                                             |             | COMPLETED  |
                                        +----v------+     +---+--------+
                                        | CANCELLED |         |
                                        | (no-show  |    +----v-------+
                                        |  fee)     |    | PAYMENT    |
                                        +-----------+    +----+-------+
                                                              |
                                                         +----v-------+
                                                         | RATED      |
                                                         +------------+

  Trip Record:
    trip_id, rider_id, driver_id, status, pickup_location,
    dropoff_location, pickup_time, dropoff_time, distance_km,
    duration_min, fare_amount, surge_multiplier, payment_status,
    rider_rating, driver_rating
```

---

## 10. Fare Calculation

### 10.1 Fare Components

```
Fare = Base Fare + (Distance Rate * distance_km) + (Time Rate * duration_min) 
       + Booking Fee + Tolls + Surge Multiplier

Example (UberX):
  Base Fare:     $2.50
  Distance Rate: $1.50/km
  Time Rate:     $0.25/min
  Booking Fee:   $1.75
  
  Trip: 8 km, 20 minutes, 1.5x surge
  
  Fare = ($2.50 + $1.50*8 + $0.25*20 + $1.75) * 1.5
       = ($2.50 + $12.00 + $5.00 + $1.75) * 1.5
       = $21.25 * 1.5
       = $31.88

  Upfront Pricing (shown before ride):
    - Estimate fare using predicted route + predicted duration
    - Lock the price (rider knows cost before accepting)
    - If actual route significantly deviates, may adjust
```

### 10.2 Fare Calculation Service

```
  Trip Completed --> Fare Service
                         |
                    +----v---------+
                    | Calculate:   |
                    | - Distance   | (from GPS trace)
                    | - Duration   | (pickup to dropoff time)
                    | - Surge      | (at time of request)
                    | - Tolls      | (from route data)
                    | - Promos     | (applied discounts)
                    +----+---------+
                         |
                    +----v---------+
                    | Final Fare   |
                    | $31.88       |
                    +----+---------+
                         |
                    +----v---------+
                    | Payment      |
                    | Service      |
                    | (charge      |
                    |  rider)      |
                    +--------------+
```

---

## 11. Surge Pricing

### 11.1 Supply-Demand Model

```
Surge = f(demand, supply) in a geographic area

  demand = ride requests per minute in area
  supply = available drivers in area

  If demand >> supply → surge multiplier increases
  If supply >> demand → no surge (1.0x)

Calculation:
  surge_ratio = demand / supply
  
  surge_ratio < 1.0  → multiplier = 1.0 (no surge)
  surge_ratio 1.0-1.5 → multiplier = 1.2
  surge_ratio 1.5-2.0 → multiplier = 1.5
  surge_ratio 2.0-3.0 → multiplier = 2.0
  surge_ratio > 3.0   → multiplier = 2.5 (cap)

  Surge Map:
  +--------+--------+--------+
  | 1.0x   | 1.2x   | 1.0x   |
  +--------+--------+--------+
  | 1.5x   | 2.0x   | 1.2x   |  ← Downtown during rush hour
  +--------+--------+--------+
  | 1.0x   | 1.2x   | 1.0x   |
  +--------+--------+--------+
```

### 11.2 Surge Pricing Service

```
  Location Updates      Ride Requests
       |                     |
  +----v------+        +----v------+
  | Count     |        | Count     |
  | available |        | requests  |
  | drivers   |        | per area  |
  | per area  |        |           |
  +----+------+        +----+------+
       |                     |
       +----------+----------+
                  |
           +------v------+
           | Surge       |
           | Calculator  |
           | (per area,  |
           |  every 2min)|
           +------+------+
                  |
           +------v------+
           | Surge Cache |
           | (Redis)     |
           | area→mult.  |
           +-------------+

  When ride requested:
    1. Get rider's geohash area
    2. Lookup surge multiplier from cache
    3. Show rider the surge price BEFORE they confirm
    4. Lock surge multiplier for the trip duration
```

---

## 12. Payment Service Integration

```
Payment Flow:

  1. Rider adds payment method (credit card, PayPal, etc.)
  2. Payment tokens stored securely (PCI-DSS compliant vault)
  3. When ride requested:
     a. Pre-authorize estimated fare on rider's card
  4. When ride completed:
     a. Calculate final fare
     b. Charge rider (capture the pre-authorization or new charge)
     c. Record transaction
  5. Weekly: batch payout to drivers (minus platform commission)

  Ride Complete --> Fare Service --> Payment Service --> Payment Gateway
                                         |                   |
                                    +---------+         (Stripe/
                                    | Ledger  |          Braintree)
                                    | (double-|
                                    |  entry  |
                                    |  book-  |
                                    |  keeping)|
                                    +---------+

  Double-Entry Ledger:
    Every transaction has two entries:
    Debit:  rider_account  -$31.88
    Credit: platform_account +$31.88 (split: $25.50 driver + $6.38 platform)
```

---

## 13. Real-Time Tracking

### 13.1 WebSocket-Based Tracking

```
During an active trip, both rider and driver see real-time positions:

  Driver App              Tracking Service             Rider App
      |                         |                         |
      |--location update------->|                         |
      |  (every 1-2 sec)       |                         |
      |                         |--push to rider -------->|
      |                         |  (via WebSocket)        |
      |                         |                         |
      |--location update------->|                         |
      |                         |--push to rider -------->|
      |                         |                         |

Implementation:
  - Driver: sends location via WebSocket every 1 second during trip
  - Server: receives, stores, and forwards to rider's WebSocket
  - Rider: renders driver's position on map in real-time
  
  Connection mapping (Redis):
    "trip:{trip_id}:rider_ws" → {ws_server: "ws-12", conn_id: "abc"}
    "trip:{trip_id}:driver_ws" → {ws_server: "ws-7", conn_id: "xyz"}
```

### 13.2 Tracking Data Storage

```
Store location trace for each trip (for fare calc, disputes, analytics):

  Table: trip_locations (TimescaleDB / Cassandra)
    trip_id     | timestamp           | lat       | lng        | speed | heading
    trip_abc123 | 2024-01-15 10:30:01 | 37.77490  | -122.41940 | 25    | 180
    trip_abc123 | 2024-01-15 10:30:02 | 37.77485  | -122.41935 | 26    | 182
    trip_abc123 | 2024-01-15 10:30:03 | 37.77478  | -122.41928 | 24    | 179
    ...

  Partitioned by trip_id, ordered by timestamp
  Used for: distance calculation, route replay, safety audits
```

---

## 14. Database Schema

### 14.1 Core Tables

```sql
CREATE TABLE riders (
    rider_id     BIGINT       PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(255) UNIQUE NOT NULL,
    phone        VARCHAR(20)  UNIQUE NOT NULL,
    rating       DECIMAL(2,1) DEFAULT 5.0,
    payment_id   VARCHAR(64),           -- tokenized payment method
    created_at   TIMESTAMP
);

CREATE TABLE drivers (
    driver_id    BIGINT       PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    phone        VARCHAR(20)  UNIQUE NOT NULL,
    email        VARCHAR(255) UNIQUE NOT NULL,
    license_num  VARCHAR(50)  NOT NULL,
    vehicle_id   BIGINT       REFERENCES vehicles(vehicle_id),
    rating       DECIMAL(2,1) DEFAULT 5.0,
    status       ENUM('offline', 'available', 'en_route', 'in_trip'),
    current_lat  DECIMAL(10,7),
    current_lng  DECIMAL(10,7),
    city_id      INT,
    created_at   TIMESTAMP
);

CREATE TABLE vehicles (
    vehicle_id   BIGINT       PRIMARY KEY,
    make         VARCHAR(50),
    model        VARCHAR(50),
    year         INT,
    color        VARCHAR(20),
    license_plate VARCHAR(15) UNIQUE NOT NULL,
    ride_types   JSON         -- ["UberX", "UberXL", "Comfort"]
);

CREATE TABLE trips (
    trip_id         BIGINT       PRIMARY KEY,
    rider_id        BIGINT       NOT NULL,
    driver_id       BIGINT,
    status          ENUM('requesting','matching','accepted',
                         'en_route','arrived','in_trip',
                         'completed','cancelled'),
    ride_type       VARCHAR(20),
    pickup_lat      DECIMAL(10,7),
    pickup_lng      DECIMAL(10,7),
    pickup_address  VARCHAR(255),
    dropoff_lat     DECIMAL(10,7),
    dropoff_lng     DECIMAL(10,7),
    dropoff_address VARCHAR(255),
    distance_km     DECIMAL(6,2),
    duration_min    DECIMAL(6,2),
    fare_amount     DECIMAL(10,2),
    surge_mult      DECIMAL(3,2) DEFAULT 1.00,
    payment_status  ENUM('pending','charged','refunded'),
    rider_rating    TINYINT,
    driver_rating   TINYINT,
    requested_at    TIMESTAMP,
    accepted_at     TIMESTAMP,
    pickup_at       TIMESTAMP,
    dropoff_at      TIMESTAMP,
    created_at      TIMESTAMP
);
```

---

## 15. Push Notifications

```
Notifications in the ride flow:

  1. RIDER notifications:
     - "Driver X accepted your ride"
     - "Driver arriving in 2 minutes"
     - "Driver has arrived"
     - "Trip completed. Fare: $31.88"
     - "Rate your driver"

  2. DRIVER notifications:
     - "New ride request near you" (with accept/decline)
     - "Navigate to pickup: 123 Main St"
     - "Rider cancelled the trip"
     - "Rate your rider"

  Delivery channels:
     - In-app via WebSocket (if app is open)
     - Push notification via APNs/FCM (if app is backgrounded)
     
  Priority: These are time-sensitive → high-priority push
```

---

## 16. Full System Architecture

```
+------------------------------------------------------------------------+
|               CLIENTS (Rider App / Driver App)                         |
+-----+----------------------------+-------------------+----------------+
      |                            |                   |
      | (HTTP/REST)                | (WebSocket)       | (location)
      |                            |                   |
+-----v--------+           +------v-------+    +------v-------+
| API Gateway  |           | WebSocket    |    | Location     |
| (Auth, Rate  |           | Gateway      |    | Service      |
|  Limit)      |           | (tracking)   |    | (500K/sec)   |
+-----+--------+           +------+-------+    +------+-------+
      |                            |                   |
+-----+---------+                  |           +-------v------+
|     |         |                  |           | Redis Geo    |
|     |         |                  |           | (driver      |
|     |         |                  |           |  positions)  |
|     |         |                  |           +--------------+
|     |         |                  |
| +---v----+ +--v-------+ +-------v-------+
| | Ride   | | Trip     | | Tracking      |
| | Service| | Service  | | Service       |
| | (match)| | (state   | | (real-time    |
| |        | |  machine)| |  position     |
| +---+----+ +----+-----+ |  relay)       |
|     |            |       +---------------+
|     |            |
| +---v-----------v----+    +------------------+
| | Trip Database      |    | Location History |
| | (PostgreSQL,       |    | (TimescaleDB /   |
| |  sharded by city)  |    |  Cassandra)      |
| +--------------------+    +------------------+
|
| +------------------+  +------------------+  +------------------+
| | ETA Service      |  | Fare Service     |  | Surge Service    |
| | (routing engine  |  | (calculate fare, |  | (supply-demand   |
| |  + ML model)     |  |  upfront pricing)|  |  per area)       |
| +------------------+  +------------------+  +------------------+
|
| +------------------+  +------------------+  +------------------+
| | Payment Service  |  | Rating Service   |  | Notification     |
| | (Stripe, ledger) |  | (1-5 stars)      |  | Service          |
| +------------------+  +------------------+  | (APNs, FCM)     |
|                                              +------------------+
+------------------------------------------------------------------------+

  +------------------+
  | Map / Routing    |  (OSRM / Google Maps Platform)
  | Service          |  External dependency for routes, ETA, geocoding
  +------------------+
```

---

## 17. Scaling Considerations

### 17.1 City-Based Sharding

```
Ride-sharing is inherently geographic. Shard everything by city:

  +------------------+  +------------------+  +------------------+
  | City: NYC        |  | City: SF         |  | City: London     |
  |                  |  |                  |  |                  |
  | Redis (drivers)  |  | Redis (drivers)  |  | Redis (drivers)  |
  | Trip DB shard    |  | Trip DB shard    |  | Trip DB shard    |
  | Surge calculator |  | Surge calculator |  | Surge calculator |
  | ETA model (NYC)  |  | ETA model (SF)   |  | ETA model (LDN) |
  +------------------+  +------------------+  +------------------+

Benefits:
  - Independent scaling per city
  - Failure isolation (NYC outage doesn't affect SF)
  - City-specific ML models (different traffic patterns)
  - Regulatory compliance (data residency)
```

### 17.2 Location Service Scaling

```
500K location updates/sec is the highest throughput requirement.

Architecture:
  - Stateless Location Service pods (50+ instances)
  - Each pod handles ~10K updates/sec
  - Redis Cluster for geo operations
    - Sharded by city/region
    - Each shard handles GEOADD/GEORADIUS operations
  - Kafka for async propagation to downstream consumers

  Location updates → Load Balancer → Location Service Pods → Redis Cluster
                                                          → Kafka → consumers
```

---

## 18. Safety Features

```
1. Trip Sharing:
   - Rider can share live trip link with trusted contacts
   - Shows real-time position on map

2. Emergency Button:
   - In-app SOS sends location to emergency services
   - Alerts Uber's safety team

3. Route Deviation Detection:
   - ML model monitors driver's route vs expected route
   - Alerts if significant deviation detected

4. Identity Verification:
   - Driver photo verification at random intervals
   - Rider PIN verification (driver confirms rider's PIN)

5. Speed Monitoring:
   - Flag trips with excessive speed
   - Post-trip safety review
```

---

## 19. Key Trade-offs and Decisions

| Decision | Option A | Option B | Our Choice | Why |
|----------|----------|----------|------------|-----|
| Geo-indexing | Geohash | QuadTree | **Geohash + Redis** | Simpler, Redis native support |
| Matching | Greedy (one-at-a-time) | Batch optimization | **Batch** at scale | Globally optimal |
| Routing engine | Google Maps API | Self-hosted OSRM | **Hybrid** | OSRM for basic, Google for complex |
| ETA | Distance-based | ML model | **ML + routing** | Most accurate |
| Location protocol | HTTP polling | WebSocket | **WebSocket** for trips | Real-time tracking needed |
| Trip DB | NoSQL | PostgreSQL | **PostgreSQL** (sharded) | ACID for financial data |
| Location history | PostgreSQL | TimescaleDB | **TimescaleDB** | Optimized for time-series |
| Sharding | Global | City-based | **City-based** | Natural geographic isolation |

---

## 20. Key Takeaways

```
+------------------------------------------------------------------+
| KEY TAKEAWAYS                                                     |
+------------------------------------------------------------------+
|                                                                    |
| 1. Geospatial indexing is the CORE challenge — use Geohash,      |
|    QuadTree, or S2 cells to find nearby drivers efficiently      |
|                                                                    |
| 2. Location updates (500K/sec) are the highest throughput         |
|    operation — Redis Geo commands handle this well                |
|                                                                    |
| 3. Ride matching should be BATCHED for global optimization,      |
|    not greedy one-at-a-time                                      |
|                                                                    |
| 4. The ride is a STATE MACHINE — track lifecycle from request    |
|    through payment with clear state transitions                  |
|                                                                    |
| 5. Surge pricing balances supply-demand — calculate per          |
|    geographic area every 1-2 minutes                             |
|                                                                    |
| 6. ETA requires sophisticated routing — combine pre-computed     |
|    road graphs with ML-based correction factors                  |
|                                                                    |
| 7. City-based sharding is natural — each city is an             |
|    independent unit with its own data and models                 |
|                                                                    |
| 8. Real-time tracking uses WebSocket relay — driver location     |
|    forwarded to rider with < 2 second latency                    |
|                                                                    |
| 9. Payments need ACID guarantees and double-entry bookkeeping    |
|    — use PostgreSQL for financial data, not NoSQL                |
|                                                                    |
| 10. Safety features are FIRST-CLASS concerns — trip sharing,     |
|     route deviation detection, emergency SOS                     |
|                                                                    |
+------------------------------------------------------------------+
```

---

*Previous: [Chapter 30 - Video Streaming](./30-video-streaming.md) | Next: [Chapter 32 - Notification System](./32-notification-system.md)*
