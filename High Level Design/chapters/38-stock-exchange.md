# Chapter 38: Design a Stock Exchange (NYSE / NASDAQ / Binance)

## 1. Problem Statement

Design an electronic trading platform that matches buy and sell orders for financial
instruments (stocks, options, crypto) in real-time. The exchange is the central nervous
system of capital markets — it must guarantee absolute correctness (no phantom trades, no
double-fills), strict fairness (time-priority ordering), and ultra-low latency (matching
in microseconds, not milliseconds).

This is not a typical web application. A stock exchange operates closer to an embedded
real-time system than a CRUD app. Every microsecond counts. A single bug can cause
millions of dollars in losses. The matching engine — the heart of the exchange — is
single-threaded by design, processes millions of events per second, and must be
deterministically replayable for audit and disaster recovery.

**Scale reference points:**

```
NYSE:      ~6 billion shares traded per day
NASDAQ:    peaks at 15M+ messages/sec
Binance:   100,000 orders/sec sustained, 1.4M orders/sec peak
CME Group: 35 billion messages/day across all asset classes
IEX:       built their matching engine in ~10,000 lines of Java
```

---

## 2. Functional Requirements

| ID  | Requirement           | Description                                                    |
|-----|-----------------------|----------------------------------------------------------------|
| FR1 | Place Order           | Submit market, limit, stop-loss, and other order types         |
| FR2 | Cancel Order          | Cancel an open (unfilled) order by order ID                    |
| FR3 | Modify Order          | Amend price or quantity of an existing order                   |
| FR4 | Order Matching        | Automatically match compatible buy/sell orders                 |
| FR5 | Real-time Price Feed  | Stream live bid/ask/last-trade/volume to subscribers           |
| FR6 | Trade Execution       | Generate trade records when orders match                       |
| FR7 | Trade Settlement      | Transfer ownership of shares and funds (T+1 or T+2)           |
| FR8 | Order Book Visibility | Display current bids and asks with depth (Level 2 data)       |
| FR9 | Account Management    | Manage portfolios, balances, margin, and positions             |
| FR10| Trade History         | Query past trades filtered by symbol, date, or account         |
| FR11| Market Data History   | Historical OHLCV (candlestick) data for charting              |
| FR12| Notifications         | Alert users on fills, partial fills, cancellations, triggers   |

---

## 3. Non-Functional Requirements

| ID   | Requirement         | Target                                                       |
|------|---------------------|--------------------------------------------------------------|
| NFR1 | Ultra-low Latency   | < 100 microseconds for order matching (core engine)          |
| NFR2 | High Throughput     | Millions of orders per second per symbol                     |
| NFR3 | Strong Consistency  | Exact financial correctness — no phantom or duplicate trades |
| NFR4 | Fault Tolerance     | 99.999% uptime (< 5.26 minutes downtime/year)               |
| NFR5 | Fairness            | Strict price-time priority (FIFO at each price level)        |
| NFR6 | Audit Trail         | Every event immutably logged with nanosecond timestamps      |
| NFR7 | Determinism         | Replay the same input sequence → identical output            |
| NFR8 | Regulatory          | Comply with SEC/FINRA rules (or equivalent for crypto)       |
| NFR9 | Security            | Encrypted transport, DDoS protection, access controls        |

---

## 4. Capacity Estimation

### 4.1 Order Volume

```
Daily orders:         5,000,000 orders/day
Trading hours:        6.5 hours = 23,400 seconds (US equities)
Average rate:         5,000,000 / 23,400 ≈ 214 orders/sec
Peak multiplier:      ~250x average (opening/closing auction surges)
Peak rate:            ~50,000 orders/sec

Per order payload:    ~200 bytes (symbol, side, type, price, qty, timestamp, account)
Daily order data:     5M × 200 B = 1 GB/day
Annual order data:    1 GB × 252 trading days ≈ 252 GB/year
```

### 4.2 Market Data

```
Symbols traded:       ~8,000 (US equities) to 500,000+ (crypto pairs)
Price updates/sec:    10,000,000 (aggregate across all symbols at peak)
Update payload:       ~100 bytes (symbol, bid, ask, last, volume, timestamp)
Peak bandwidth:       10M × 100 B = 1 GB/sec raw market data

Subscribers:          100,000 institutional + millions retail
Fan-out factor:       Each update → 10,000+ consumers
Total egress:         Terabits/sec (solved via multicast + edge caching)
```

### 4.3 Storage

```
Order history:        252 GB/year (hot storage, queryable)
Trade history:        ~100 GB/year (subset of orders that matched)
Market data (ticks):  ~5 TB/year (every price change, every symbol)
OHLCV candles:        ~50 GB/year (1-min bars, all symbols)
Audit log:            ~1 TB/year (every gateway event, every state change)
Total:                ~7 TB/year → manageable with time-series DBs
```

---

## 5. Order Types

### 5.1 Market Order

Execute immediately at the **best available price**. No price guarantee — you get
whatever the market offers. Used when speed of execution matters more than price.

```
Example: "Buy 100 shares of AAPL at market"
→ Fills at the best ask price(s) currently in the order book
→ If best ask is $150.00 for 60 shares and next is $150.05 for 40 shares,
  you get 60 @ $150.00 + 40 @ $150.05
```

### 5.2 Limit Order

Execute at the **specified price or better**. A buy limit at $150 will only fill at
$150 or lower. Provides price protection but no execution guarantee.

```
Example: "Buy 100 AAPL @ $150.00 limit"
→ If ask ≤ $150.00: fills immediately (fully or partially)
→ If ask > $150.00: rests in order book at $150.00 until filled or cancelled
```

### 5.3 Stop-Loss Order

Becomes a **market order** when the price hits a trigger (stop) price. Used to limit
losses on an existing position.

```
Example: "Sell 100 AAPL stop @ $140.00"
→ If AAPL drops to $140.00: triggers a market sell order
→ Execution price could be $140.00, $139.95, or worse (slippage)
```

### 5.4 Stop-Limit Order

Becomes a **limit order** (not market) when the stop price is triggered. Combines
stop-loss protection with price control.

```
Example: "Sell 100 AAPL stop $140.00 limit $139.50"
→ If AAPL drops to $140.00: triggers a limit sell @ $139.50
→ Won't fill below $139.50 — but may not fill at all if price crashes past it
```

### 5.5 Iceberg (Hidden Quantity) Order

Only a small "visible" portion is shown in the order book. When the visible portion
fills, the next slice is automatically placed. Used by institutions to avoid signaling
large intent to the market.

```
Example: "Buy 10,000 AAPL @ $150.00 limit, display 500"
→ Order book shows 500 shares at $150.00
→ When those 500 fill, next 500 automatically appear
→ Repeats until all 10,000 are filled
```

### 5.6 Time-in-Force Modifiers

```
┌──────────────────────┬────────────────────────────────────────────────┐
│ Fill-or-Kill (FOK)   │ Must fill ENTIRELY and immediately, or cancel │
│                      │ the whole order. No partial fills allowed.     │
├──────────────────────┼────────────────────────────────────────────────┤
│ Immediate-or-Cancel  │ Fill as much as possible immediately, cancel  │
│ (IOC)                │ any unfilled remainder. Partial fills OK.     │
├──────────────────────┼────────────────────────────────────────────────┤
│ Good-Till-Cancelled  │ Stays in the order book until filled or       │
│ (GTC)                │ explicitly cancelled. Can persist for days.   │
├──────────────────────┼────────────────────────────────────────────────┤
│ Day Order            │ Expires at market close if not filled.        │
│                      │ Most common default for equities.             │
└──────────────────────┴────────────────────────────────────────────────┘
```

### 5.7 Order Type Comparison

```
┌──────────────┬───────────┬───────────┬──────────┬────────────┬──────────────┐
│ Order Type   │ Price     │ Execution │ Rests in │ Partial    │ Risk         │
│              │ Control?  │ Guarantee?│ Book?    │ Fill?      │              │
├──────────────┼───────────┼───────────┼──────────┼────────────┼──────────────┤
│ Market       │ No        │ Yes*      │ No       │ Yes        │ Slippage     │
│ Limit        │ Yes       │ No        │ Yes      │ Yes        │ No fill      │
│ Stop-Loss    │ No        │ Yes*      │ No**     │ Yes        │ Slippage     │
│ Stop-Limit   │ Yes       │ No        │ No**     │ Yes        │ No fill      │
│ Iceberg      │ Yes       │ No        │ Partial  │ Yes        │ Info leakage │
│ FOK          │ Yes       │ All/None  │ No       │ No         │ No fill      │
│ IOC          │ Yes       │ Partial   │ No       │ Yes        │ Partial fill │
│ GTC          │ Yes       │ No        │ Yes      │ Yes        │ Stale orders │
└──────────────┴───────────┴───────────┴──────────┴────────────┴──────────────┘
  * Execution guaranteed if liquidity exists
 ** Rests in a separate trigger book until stop price is hit
```

---

## 6. The Order Book — The Heart of the Exchange

### 6.1 What Is an Order Book?

The order book is a real-time, sorted ledger of all outstanding buy (bid) and sell (ask)
orders for a single financial instrument. It is the central data structure of any exchange.

```
                         ORDER BOOK: AAPL
  ═══════════════════════════════════════════════════════
         BIDS (Buy Orders)    │    ASKS (Sell Orders)
  ───────────────────────────┼─────────────────────────
   Buyers willing to pay     │   Sellers willing to sell
   (sorted HIGH → LOW)       │   (sorted LOW → HIGH)
  ───────────────────────────┼─────────────────────────
                              │
   Qty     Price      Time   │   Price     Qty     Time
  ───────────────────────────┼─────────────────────────
   200    $150.10    09:30:01│  $150.15    300    09:30:00
   500    $150.05    09:30:02│  $150.20    150    09:30:01
   100    $150.05    09:30:05│  $150.20    400    09:30:03
   800    $150.00    09:30:00│  $150.25    600    09:30:02
   350    $150.00    09:30:03│  $150.30   1000    09:30:01
  ───────────────────────────┼─────────────────────────
   Best Bid: $150.10         │  Best Ask: $150.15
                              │
            Spread = $150.15 - $150.10 = $0.05
  ═══════════════════════════════════════════════════════

  Price-Time Priority:
  • At $150.05 bid: the 500-share order (09:30:02) gets filled BEFORE
    the 100-share order (09:30:05) because it arrived first.
  • At $150.20 ask: the 150-share order (09:30:01) gets filled BEFORE
    the 400-share order (09:30:03).
```

### 6.2 Order Book Data Structure

```
              Order Book Internal Structure
  ┌─────────────────────────────────────────────────┐
  │                  OrderBook                       │
  │                                                  │
  │  ┌─────────────────────┐ ┌────────────────────┐ │
  │  │    Bid Side          │ │    Ask Side        │ │
  │  │  (TreeMap: desc)     │ │  (TreeMap: asc)    │ │
  │  │                      │ │                    │ │
  │  │  $150.10 ──→ [queue] │ │  $150.15 ──→[queue]│ │
  │  │  $150.05 ──→ [queue] │ │  $150.20 ──→[queue]│ │
  │  │  $150.00 ──→ [queue] │ │  $150.25 ──→[queue]│ │
  │  │   ...                │ │   ...              │ │
  │  └─────────────────────┘ └────────────────────┘ │
  │                                                  │
  │  ┌──────────────────────────────────────────┐   │
  │  │  Order ID Index (HashMap<OrderID, Order>) │   │
  │  │  O(1) lookup for cancel/modify            │   │
  │  └──────────────────────────────────────────┘   │
  └─────────────────────────────────────────────────┘

  Each Price Level:
  ┌──────────────────────────────────────────────────┐
  │ Price Level: $150.05           Total Qty: 600    │
  │                                                  │
  │  ┌─────┐    ┌─────┐    ┌─────┐                  │
  │  │ 500 │◄──►│ 100 │◄──►│ ... │   (doubly-linked │
  │  │09:02│    │09:05│    │     │    list = FIFO)   │
  │  └─────┘    └─────┘    └─────┘                  │
  │  ▲ head                         tail ▲           │
  │  (first to fill)         (last to fill)          │
  └──────────────────────────────────────────────────┘
```

**Why these data structures?**

| Structure                | Purpose                          | Complexity      |
|--------------------------|----------------------------------|-----------------|
| TreeMap (Red-Black Tree) | Sorted price levels              | O(log P) insert |
| Doubly-Linked List       | FIFO queue at each price level   | O(1) add/remove |
| HashMap (Order ID → ref) | Direct order lookup for cancels  | O(1) lookup     |

- **P** = number of distinct price levels (typically 100–10,000)
- Each order node lives in both the linked list AND the hash map

### 6.3 Order Book Operations Complexity

```
┌────────────────────┬────────────┬─────────────────────────────────┐
│ Operation          │ Complexity │ How                             │
├────────────────────┼────────────┼─────────────────────────────────┤
│ Add order          │ O(log P)   │ Find/create price level in tree │
│                    │            │ + O(1) append to FIFO queue     │
├────────────────────┼────────────┼─────────────────────────────────┤
│ Cancel order       │ O(1)       │ HashMap lookup → remove from    │
│                    │            │ linked list → if list empty,    │
│                    │            │ remove price level O(log P)     │
├────────────────────┼────────────┼─────────────────────────────────┤
│ Match (top-of-book)│ O(1)       │ Peek best bid/ask from tree     │
│                    │            │ + dequeue head of linked list   │
├────────────────────┼────────────┼─────────────────────────────────┤
│ Get best bid/ask   │ O(1)*      │ Cached: tree.first()/tree.last()│
├────────────────────┼────────────┼─────────────────────────────────┤
│ Get depth (L2)     │ O(D)       │ Iterate D price levels          │
└────────────────────┴────────────┴─────────────────────────────────┘
  * O(log P) worst case, but typically cached for O(1) access
```

---

## 7. Matching Engine — The Core Algorithm

### 7.1 Price-Time Priority (FIFO) Matching

The matching engine is the single most critical component. It enforces **price-time
priority**: orders at a better price always match first, and among orders at the same
price, the earliest order matches first.

```
  Matching Rule: Price-Time Priority (FIFO)
  ──────────────────────────────────────────

  Step 1: New order arrives (e.g., Buy 300 @ Market)

  Step 2: Look at the OPPOSITE side of the book
          (Buy order → check Ask side)

  Step 3: Can the incoming order match the best ask?
          ├── Yes: Execute trade at the resting order's price
          │        ├── Full fill: remove resting order from book
          │        └── Partial fill: reduce resting order quantity
          │        └── Continue matching if incoming qty remains
          └── No:  Place incoming order into the Bid side

  Step 4: If incoming order still has remaining quantity:
          ├── Market order: keep matching deeper into the book
          ├── Limit order:  if no more matchable prices, rest in book
          └── FOK order:    cancel entirely (all-or-nothing failed)

  Step 5: Publish trade(s) and updated market data
```

### 7.2 Matching Engine Pseudocode

```
function processOrder(incomingOrder):
    if incomingOrder.side == BUY:
        oppositeSide = orderBook.asks      // sorted ascending (cheapest first)
    else:
        oppositeSide = orderBook.bids      // sorted descending (highest first)

    while incomingOrder.remainingQty > 0:
        bestLevel = oppositeSide.peekBest()

        if bestLevel == null:
            break   // no liquidity on opposite side

        if incomingOrder.type == LIMIT:
            if incomingOrder.side == BUY and bestLevel.price > incomingOrder.price:
                break   // ask price too high for this buy limit
            if incomingOrder.side == SELL and bestLevel.price < incomingOrder.price:
                break   // bid price too low for this sell limit

        // Match against orders at this price level (FIFO)
        while incomingOrder.remainingQty > 0 and bestLevel.hasOrders():
            restingOrder = bestLevel.peekFirst()    // oldest order at this price
            fillQty = min(incomingOrder.remainingQty, restingOrder.remainingQty)
            fillPrice = restingOrder.price          // always at the resting order's price

            // Generate trade
            trade = Trade(
                buyOrderId  = (BUY side order).id,
                sellOrderId = (SELL side order).id,
                symbol      = incomingOrder.symbol,
                price       = fillPrice,
                quantity    = fillQty,
                timestamp   = now()
            )
            publishTrade(trade)

            // Update quantities
            incomingOrder.remainingQty  -= fillQty
            restingOrder.remainingQty   -= fillQty

            if restingOrder.remainingQty == 0:
                bestLevel.removeFirst()             // fully filled — remove
                orderIndex.remove(restingOrder.id)

        if bestLevel.isEmpty():
            oppositeSide.removeLevel(bestLevel.price)

    // Handle remaining quantity of incoming order
    if incomingOrder.remainingQty > 0:
        if incomingOrder.type == MARKET:
            cancel(incomingOrder)       // market orders don't rest in book
        elif incomingOrder.timeInForce == IOC or incomingOrder.timeInForce == FOK:
            cancel(incomingOrder)       // IOC/FOK: cancel unfilled remainder
        else:
            addToBook(incomingOrder)    // limit order rests in book

function addToBook(order):
    if order.side == BUY:
        sameSide = orderBook.bids
    else:
        sameSide = orderBook.asks

    level = sameSide.getOrCreate(order.price)   // O(log P)
    level.appendToQueue(order)                  // O(1)
    orderIndex.put(order.id, order)             // O(1)
    publishOrderBookUpdate()
```

### 7.3 Example: Walk Through a Series of Orders

Let's trace 10 orders for symbol **AAPL** arriving sequentially. The order book starts
empty.

```
═══════════════════════════════════════════════════════════════════
  Order #1: SELL 100 AAPL @ $150.20 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  No bids to match against → rests in ask side

  BIDS                  │  ASKS
  (empty)               │  $150.20  100  [Order#1]
═══════════════════════════════════════════════════════════════════
  Order #2: SELL 200 AAPL @ $150.25 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  No bids → rests in ask side

  BIDS                  │  ASKS
  (empty)               │  $150.20  100  [#1]
                        │  $150.25  200  [#2]
═══════════════════════════════════════════════════════════════════
  Order #3: SELL 150 AAPL @ $150.20 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  No bids → rests at same price level as #1 (behind it in FIFO)

  BIDS                  │  ASKS
  (empty)               │  $150.20  100  [#1] → 150 [#3]
                        │  $150.25  200  [#2]
═══════════════════════════════════════════════════════════════════
  Order #4: BUY 300 AAPL @ $150.10 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  Best ask = $150.20 > buy limit $150.10 → no match, rests in bids

  BIDS                  │  ASKS
  $150.10  300  [#4]    │  $150.20  100 [#1] → 150 [#3]
                        │  $150.25  200 [#2]
═══════════════════════════════════════════════════════════════════
  Order #5: BUY 500 AAPL @ $150.05 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  Best ask $150.20 > $150.05 → no match

  BIDS                  │  ASKS
  $150.10  300  [#4]    │  $150.20  100 [#1] → 150 [#3]
  $150.05  500  [#5]    │  $150.25  200 [#2]
═══════════════════════════════════════════════════════════════════
  Order #6: BUY 120 AAPL @ $150.20 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  Best ask = $150.20 ≤ buy limit $150.20 → MATCH!

  Match with #1: fill 100 @ $150.20  ← #1 fully filled (removed)
  Remaining: 120 - 100 = 20

  Match with #3: fill 20 @ $150.20   ← #3 partially filled (130 left)

  ★ TRADES GENERATED:
    Trade A: Buy#6 ↔ Sell#1, 100 shares @ $150.20
    Trade B: Buy#6 ↔ Sell#3,  20 shares @ $150.20

  BIDS                  │  ASKS
  $150.10  300  [#4]    │  $150.20  130 [#3]    (#1 gone, #3 reduced)
  $150.05  500  [#5]    │  $150.25  200 [#2]
═══════════════════════════════════════════════════════════════════
  Order #7: BUY 50 AAPL @ Market
───────────────────────────────────────────────────────────────────
  Market order → takes best available ask regardless of price
  Best ask = $150.20 (Order #3 has 130 remaining)

  Match with #3: fill 50 @ $150.20   ← #3 has 80 left

  ★ TRADE: Buy#7 ↔ Sell#3, 50 shares @ $150.20

  BIDS                  │  ASKS
  $150.10  300  [#4]    │  $150.20   80  [#3]
  $150.05  500  [#5]    │  $150.25  200  [#2]
═══════════════════════════════════════════════════════════════════
  Order #8: CANCEL Order #5
───────────────────────────────────────────────────────────────────
  HashMap lookup → remove from bid side

  BIDS                  │  ASKS
  $150.10  300  [#4]    │  $150.20   80  [#3]
                        │  $150.25  200  [#2]
═══════════════════════════════════════════════════════════════════
  Order #9: SELL 400 AAPL @ $150.05 (Limit, GTC)
───────────────────────────────────────────────────────────────────
  Sell limit $150.05 → match with best bid if bid ≥ $150.05
  Best bid = $150.10 ≥ $150.05 → MATCH!

  Match with #4: fill 300 @ $150.10  ← #4 fully filled
  Remaining: 400 - 300 = 100
  No more bids ≥ $150.05 → remaining 100 rests at $150.05

  ★ TRADE: Sell#9 ↔ Buy#4, 300 shares @ $150.10

  BIDS                  │  ASKS
  (empty)               │  $150.05  100  [#9]    ← new ask!
                        │  $150.20   80  [#3]
                        │  $150.25  200  [#2]
═══════════════════════════════════════════════════════════════════
  Order #10: BUY 250 AAPL @ $150.25 (Limit, IOC)
───────────────────────────────────────────────────────────────────
  IOC = Immediate-or-Cancel → fill what you can, cancel rest
  Buy limit $150.25 → match asks ≤ $150.25

  Match #9: fill 100 @ $150.05   ← #9 fully filled
  Match #3: fill  80 @ $150.20   ← #3 fully filled
  Remaining: 250 - 100 - 80 = 70
  Next ask $150.25 ≤ $150.25 → match!
  Match #2: fill  70 @ $150.25   ← #2 partially filled (130 left)

  ★ TRADES:
    Trade: Buy#10 ↔ Sell#9,  100 shares @ $150.05
    Trade: Buy#10 ↔ Sell#3,   80 shares @ $150.20
    Trade: Buy#10 ↔ Sell#2,   70 shares @ $150.25

  IOC: fully filled, no remainder to cancel.

  BIDS                  │  ASKS
  (empty)               │  $150.25  130  [#2]
═══════════════════════════════════════════════════════════════════
```

---

## 8. System Architecture

### 8.1 Component Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                        STOCK EXCHANGE                            │
│                                                                  │
│  ┌──────────┐    A stock exchange has distinct layers:           │
│  │ Clients  │                                                    │
│  │(Brokers, │    1. Gateway Layer      — entry point             │
│  │ Traders, │    2. Sequencer          — assigns global order    │
│  │ Market   │    3. Matching Engine    — the core brain          │
│  │ Makers)  │    4. Market Data        — publishes prices        │
│  └──────────┘    5. Post-Trade         — settlement, clearing    │
│                  6. Risk & Compliance  — guards and audits       │
└──────────────────────────────────────────────────────────────────┘
```

**Component breakdown:**

| Component              | Responsibility                                            |
|------------------------|-----------------------------------------------------------|
| **Gateway**            | Accepts client connections, validates orders, rate limits  |
| **Sequencer**          | Assigns a monotonic global sequence number to each event  |
| **Matching Engine**    | Matches buy/sell orders, maintains order book (per symbol) |
| **Market Data Publisher** | Broadcasts price updates (multicast + WebSocket)       |
| **Order Management (OMS)** | Tracks order lifecycle (open, partial, filled, cancelled) |
| **Risk Management**    | Pre-trade and post-trade risk checks                      |
| **Trade Settlement**   | Transfers shares and funds between accounts (T+1/T+2)     |
| **Audit / Compliance** | Immutable event log for regulatory reporting              |

### 8.2 Why a Single-Threaded Matching Engine?

This is the most counter-intuitive design choice and the most important one:

```
  ┌─────────────────────────────────────────────────────────────┐
  │              WHY SINGLE-THREADED?                            │
  │                                                             │
  │  1. NO LOCKS           Multi-threaded = locks = contention  │
  │                        = unpredictable latency spikes       │
  │                                                             │
  │  2. DETERMINISTIC      Same input → same output. Always.    │
  │                        Critical for replay & disaster       │
  │                        recovery on the hot standby.         │
  │                                                             │
  │  3. FAIRNESS           Sequential processing = perfect      │
  │                        FIFO ordering. No thread can "cut    │
  │                        in line" due to scheduling luck.     │
  │                                                             │
  │  4. FAST ENOUGH        One modern CPU core can do 1M+       │
  │                        matches/sec. That's more than any    │
  │                        single symbol needs.                 │
  │                                                             │
  │  5. PARALLELISM VIA    AAPL has its own matching engine.    │
  │     PARTITIONING       GOOGL has its own. TSLA has its own. │
  │                        Symbols are independent → scale out  │
  │                        by adding more cores/machines.       │
  └─────────────────────────────────────────────────────────────┘

  Analogy: Redis is single-threaded for the same reason.
           LMAX Disruptor (used in real exchanges) is single-threaded.
           The key insight: removing coordination overhead
           (locks, CAS, memory barriers) makes one thread faster
           than many threads fighting over shared state.
```

**How one thread handles the load:**

```
Symbols on NYSE:         ~3,500 listed
Matching engines needed: ~3,500 (one per symbol)
CPU cores per server:    64-128 (modern server)
Servers needed:          ~30-50 (with headroom)

Even at peak: the hottest symbol (e.g., AAPL, SPY) sees
~50,000 orders/sec — well within a single core's capacity.
```

### 8.3 Full Architecture Diagram

```
 Clients (Brokers / Traders / Algo Systems)
   │          │           │           │
   │ FIX/TCP  │ REST/WS   │ FIX/TCP   │
   ▼          ▼           ▼           ▼
┌──────────────────────────────────────────────────────┐
│                   GATEWAY LAYER                       │
│  ┌─────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐ │
│  │ Gateway │ │ Gateway │ │ Gateway  │ │ Gateway  │ │
│  │  Node 1 │ │  Node 2 │ │  Node 3  │ │  Node N  │ │
│  └────┬────┘ └────┬────┘ └────┬─────┘ └────┬─────┘ │
│       │   Auth │ Validate │ Rate Limit   │          │
│       │   Schema check │ Normalize      │           │
└───────┼────────┼─────────┼──────────────┼───────────┘
        │        │         │              │
        ▼        ▼         ▼              ▼
┌──────────────────────────────────────────────────────┐
│                PRE-TRADE RISK CHECK                   │
│  • Sufficient funds / margin?                         │
│  • Position limits within bounds?                     │
│  • Order size within allowed range?                   │
│  • Fat-finger check (price vs. market)?               │
│  ─── REJECT if any check fails ───                   │
└────────────────────┬─────────────────────────────────┘
                     │ (validated orders)
                     ▼
┌──────────────────────────────────────────────────────┐
│                    SEQUENCER                          │
│  Assigns global monotonic sequence number:           │
│    Order → { seqNum: 1000001, timestamp: ...}        │
│  Writes to durable event log (Write-Ahead Log)       │
│  Single point of serialization → guarantees fairness │
└────────────────────┬─────────────────────────────────┘
                     │ (sequenced events)
                     ▼
┌──────────────────────────────────────────────────────┐
│              MATCHING ENGINE CLUSTER                  │
│                                                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐             │
│  │  AAPL    │ │  GOOGL   │ │  TSLA    │  ...        │
│  │ Engine   │ │ Engine   │ │ Engine   │             │
│  │(1 thread)│ │(1 thread)│ │(1 thread)│             │
│  │          │ │          │ │          │             │
│  │ OrderBook│ │ OrderBook│ │ OrderBook│             │
│  │ in-memory│ │ in-memory│ │ in-memory│             │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘             │
│       │             │            │                    │
│  Each engine: single-threaded, lock-free, in-memory  │
│  Routes by symbol hash: hash(symbol) % N             │
└───────┼─────────────┼────────────┼───────────────────┘
        │  Trades     │            │
        ▼             ▼            ▼
┌──────────────────────────────────────────────────────┐
│              OUTPUT EVENT STREAMS                     │
│                                                       │
│  ┌──────────────────┐    ┌────────────────────┐      │
│  │ Trade Reports     │    │ Order Book Updates  │      │
│  │ (executions)      │    │ (new/cancel/modify) │      │
│  └────────┬──────────┘    └────────┬───────────┘      │
└───────────┼─────────────────────────┼────────────────┘
            │                         │
     ┌──────┴──────┐          ┌──────┴──────┐
     ▼             ▼          ▼             ▼
┌─────────┐ ┌──────────┐ ┌─────────┐ ┌──────────────┐
│  OMS    │ │Settlement│ │ Market  │ │  Audit Log   │
│ (Order  │ │ Service  │ │ Data    │ │ (Immutable   │
│  Mgmt)  │ │ (T+1)   │ │Publisher│ │  Event Store)│
└─────────┘ └──────────┘ └────┬────┘ └──────────────┘
                               │
              ┌────────────────┼────────────────┐
              ▼                ▼                ▼
        ┌──────────┐    ┌──────────┐     ┌──────────┐
        │ Multicast│    │WebSocket │     │REST/Poll │
        │  (UDP)   │    │  Feeds   │     │  API     │
        │ Institu- │    │ Retail   │     │ Mobile   │
        │ tional   │    │ Clients  │     │ Apps     │
        └──────────┘    └──────────┘     └──────────┘
```

---

## 9. Market Data Distribution

Market data is the lifeblood of all participants — traders, market makers, and algorithms
all depend on real-time price information.

### 9.1 Data Types

```
Level 1 (L1) — Top of Book:
  { symbol: "AAPL", bid: 150.10, bidSize: 200, ask: 150.15, askSize: 300,
    last: 150.12, lastSize: 50, volume: 1234567, timestamp: ... }

Level 2 (L2) — Depth of Book:
  { symbol: "AAPL", bids: [
      {price: 150.10, qty: 200}, {price: 150.05, qty: 600}, ...
    ], asks: [
      {price: 150.15, qty: 300}, {price: 150.20, qty: 550}, ...
    ]}

Level 3 (L3) — Full Order Book:
  Every individual order at every price level (exchange internal use)

Trade Tick:
  { symbol: "AAPL", price: 150.12, qty: 50, buyerId: ..., sellerId: ...,
    timestamp: 1700000000.123456789 }
```

### 9.2 Distribution Architecture

```
  Matching Engines (produce events)
       │          │          │
       ▼          ▼          ▼
  ┌───────────────────────────────┐
  │     Market Data Aggregator    │
  │  • Combines all symbol feeds  │
  │  • Applies throttling/batch   │
  │  • Formats into wire protocol │
  └──────┬──────────────┬─────────┘
         │              │
    ┌────┴────┐    ┌────┴─────────────────────┐
    ▼         ▼    ▼                           ▼
┌────────┐ ┌────────────┐ ┌────────────┐ ┌─────────┐
│Multicast│ │ WebSocket  │ │  Message   │ │  REST   │
│  UDP   │ │  Gateway   │ │  Queue     │ │  API    │
│        │ │            │ │ (Kafka)    │ │ (poll)  │
│ Instit.│ │  Retail    │ │ Analytics  │ │ Mobile  │
│ ≤5 μs  │ │  ~1-10 ms  │ │ Archival  │ │ ~100ms  │
└────────┘ └────────────┘ └────────────┘ └─────────┘

Multicast (UDP):
  • One-to-many: single packet reaches all subscribers on the network
  • Used by institutional traders and co-located HFT firms
  • No TCP handshake overhead → lowest possible latency
  • Unreliable: lost packets detected via sequence gaps → retransmit via TCP

WebSocket:
  • Persistent bidirectional connection
  • Server pushes updates as they happen
  • Used by retail brokers (Robinhood, E-Trade, etc.)
  • ~1-10ms latency (acceptable for retail)
```

### 9.3 Fan-Out Challenge

```
10M updates/sec × 100 bytes each = 1 GB/sec raw
× 100,000 subscribers = 100 TB/sec if unicast (impossible)

Solution: Hierarchical fan-out + multicast

  Matching Engine
       │ (1 copy)
       ▼
  ┌─────────────┐
  │ Aggregator   │ ← 1 GB/sec in
  └──────┬───────┘
    ┌────┼────┬────────┐
    ▼    ▼    ▼        ▼
  Edge Edge Edge ... Edge    ← 16-64 edge nodes
  Srv  Srv  Srv      Srv
   │    │    │        │
   ▼    ▼    ▼        ▼
  1K   1K   1K  ... clients  ← each edge serves 1K-10K clients

  + UDP multicast for institutional = zero fan-out cost
```

---

## 10. Risk Management

### 10.1 Pre-Trade Risk Checks

These checks happen **before** the order reaches the matching engine. They must be
extremely fast (< 10 microseconds) to avoid adding latency.

```
  Incoming Order
       │
       ▼
  ┌──────────────────────────────────────────┐
  │          PRE-TRADE RISK ENGINE           │
  │                                          │
  │  ┌──────────────────────────────────┐   │
  │  │ 1. FUNDS CHECK                   │   │
  │  │    Buy order: balance ≥ price×qty │   │
  │  │    Margin order: margin ≥ req.   │   │
  │  └──────────┬───────────────────────┘   │
  │             │ PASS                       │
  │  ┌──────────▼───────────────────────┐   │
  │  │ 2. POSITION LIMIT               │   │
  │  │    Total position ≤ max allowed  │   │
  │  │    (per symbol and portfolio)    │   │
  │  └──────────┬───────────────────────┘   │
  │             │ PASS                       │
  │  ┌──────────▼───────────────────────┐   │
  │  │ 3. ORDER SIZE LIMIT             │   │
  │  │    Qty ≤ max order size          │   │
  │  │    Notional ≤ max notional       │   │
  │  └──────────┬───────────────────────┘   │
  │             │ PASS                       │
  │  ┌──────────▼───────────────────────┐   │
  │  │ 4. FAT-FINGER CHECK             │   │
  │  │    |order price - market price|  │   │
  │  │    ≤ allowed deviation (e.g. 5%) │   │
  │  │    Catches typos like $15 vs $150│   │
  │  └──────────┬───────────────────────┘   │
  │             │ PASS                       │
  │  ┌──────────▼───────────────────────┐   │
  │  │ 5. RATE LIMIT                   │   │
  │  │    Orders/sec ≤ max rate         │   │
  │  │    Prevents runaway algorithms   │   │
  │  └──────────┬───────────────────────┘   │
  │             │ PASS                       │
  │             ▼                            │
  │        ✓ APPROVED → Send to Sequencer   │
  │                                          │
  │  Any check FAIL → REJECT + notify client │
  └──────────────────────────────────────────┘
```

### 10.2 Circuit Breakers

Circuit breakers halt trading when prices move too fast, preventing flash crashes:

```
┌───────────────────────────────────────────────────────────┐
│                  CIRCUIT BREAKER LEVELS                    │
│                                                           │
│  Level 1: Price drops  7% from prior close → halt 15 min │
│  Level 2: Price drops 13% from prior close → halt 15 min │
│  Level 3: Price drops 20% from prior close → halt for day│
│                                                           │
│  Per-Stock LULD (Limit Up-Limit Down):                   │
│  If stock moves > X% in 5 min → 5-minute trading pause   │
│                                                           │
│  Flash Crash Example (May 6, 2010):                      │
│  DJIA dropped ~1000 points in minutes, recovered quickly  │
│  → Led to modern circuit breaker rules                    │
└───────────────────────────────────────────────────────────┘
```

### 10.3 Post-Trade Risk

```
After each trade executes:
  • Update real-time P&L for all affected accounts
  • Recalculate portfolio exposure (delta, gamma, VaR)
  • Check margin requirements — issue margin call if needed
  • Monitor correlated risk across instruments
  • Flag suspicious patterns (wash trading, spoofing, layering)
```

---

## 11. Fault Tolerance and Recovery

An exchange cannot afford downtime. Every second of outage costs millions in lost
trading activity and damages market confidence.

### 11.1 Primary-Secondary Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                  HIGH AVAILABILITY DESIGN                    │
│                                                             │
│  ┌───────────────┐          ┌───────────────┐              │
│  │   PRIMARY      │  event   │  SECONDARY    │              │
│  │   Matching     │  stream  │  Matching     │              │
│  │   Engine       │────────►│  Engine       │              │
│  │                │          │  (Hot Standby)│              │
│  │  ┌──────────┐ │          │  ┌──────────┐ │              │
│  │  │ OrderBook│ │          │  │ OrderBook│ │              │
│  │  │ (active) │ │          │  │ (shadow) │ │              │
│  │  └──────────┘ │          │  └──────────┘ │              │
│  │   Seq#: 50001 │          │   Seq#: 50001 │              │
│  └───────┬───────┘          └───────────────┘              │
│          │                          ▲                       │
│          │                          │                       │
│          ▼                  (same sequence =                │
│  ┌───────────────┐          same state)                    │
│  │  Write-Ahead  │                                         │
│  │  Log (WAL)    │  Durable, replicated event log          │
│  │  (persistent) │  Every order, cancel, trade recorded    │
│  └───────────────┘                                         │
└─────────────────────────────────────────────────────────────┘

Failover Process:
  1. Heartbeat monitor detects primary failure (< 1 sec)
  2. Secondary verifies it has consumed all events up to last seq#
  3. Secondary promotes itself to primary
  4. Gateway switches traffic to new primary
  5. Total failover time: ~1-3 seconds
```

### 11.2 Deterministic Replay

```
The KEY property enabling fault tolerance:

  Same input sequence → Same output (trades, book state)

  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │  Event #1   │────►│  Event #2   │────►│  Event #3   │──► ...
  │ Buy 100@150 │     │ Sell 50@149 │     │ Cancel #1   │
  └─────────────┘     └─────────────┘     └─────────────┘

  Primary processes events 1,2,3 → produces state S
  Secondary replays  events 1,2,3 → produces IDENTICAL state S

  This works because:
  • Matching engine is single-threaded (no race conditions)
  • Events are globally sequenced (no reordering)
  • No external dependencies during matching (pure function)
  • Timestamps come from the sequencer, not wall clock
```

### 11.3 Recovery Strategies

```
┌──────────────────────────────────────────────────────────┐
│              RECOVERY OPTIONS                             │
│                                                          │
│  Fast Recovery (< 5 sec):                                │
│  ├─ Hot standby continuously replays events              │
│  └─ Promotion is near-instant                            │
│                                                          │
│  Medium Recovery (< 1 min):                              │
│  ├─ Load latest state snapshot (taken every N minutes)   │
│  └─ Replay events from snapshot's seq# to current        │
│                                                          │
│  Full Recovery (< 10 min):                               │
│  ├─ Start from empty state                               │
│  └─ Replay entire day's event log from seq# 0            │
│                                                          │
│  Cross-DC Failover (< 30 sec):                           │
│  ├─ Events replicated to secondary data center           │
│  ├─ Secondary DC has warm standby engines                │
│  └─ DNS/anycast switch directs traffic to backup DC      │
│                                                          │
│  Snapshot Strategy:                                       │
│  ├─ Serialize full order book state every 60 seconds     │
│  ├─ Store with the seq# at time of snapshot              │
│  └─ Recovery = load snapshot + replay events since       │
└──────────────────────────────────────────────────────────┘
```

---

## 12. Latency Optimization

In exchange design, microseconds matter. High-frequency trading (HFT) firms invest
millions to shave nanoseconds. The exchange itself must be faster than all participants.

### 12.1 Optimization Techniques

```
┌──────────────────────────────────────────────────────────────┐
│             LATENCY OPTIMIZATION STACK                        │
│                                                              │
│  ┌──────────────────────────────────────────────┐           │
│  │  NETWORK LAYER                                │           │
│  │  • Kernel bypass (DPDK — Data Plane Dev Kit)  │           │
│  │  • FPGA-based network cards (Xilinx/Intel)    │           │
│  │  • Direct NIC-to-application data path        │           │
│  │  • Saves: ~10-50 μs per packet (no syscalls)  │           │
│  └──────────────────────────────────────────────┘           │
│  ┌──────────────────────────────────────────────┐           │
│  │  MEMORY LAYER                                 │           │
│  │  • Pre-allocated memory pools (no malloc)     │           │
│  │  • Memory-mapped files for event log          │           │
│  │  • Huge pages (2MB/1GB) to reduce TLB misses │           │
│  │  • NUMA-aware allocation (local memory only)  │           │
│  └──────────────────────────────────────────────┘           │
│  ┌──────────────────────────────────────────────┐           │
│  │  CPU LAYER                                    │           │
│  │  • CPU core pinning (isolcpus)               │           │
│  │  • Disable hyper-threading on critical cores  │           │
│  │  • Lock-free ring buffers (LMAX Disruptor)    │           │
│  │  • Branch prediction-friendly code paths      │           │
│  └──────────────────────────────────────────────┘           │
│  ┌──────────────────────────────────────────────┐           │
│  │  LANGUAGE & RUNTIME                           │           │
│  │  • C / C++ / Rust (no GC pauses)             │           │
│  │  • Avoid heap allocation in hot path          │           │
│  │  • If Java: Azul Zing (pauseless GC) or      │           │
│  │    off-heap memory (Unsafe / ByteBuffer)      │           │
│  └──────────────────────────────────────────────┘           │
│  ┌──────────────────────────────────────────────┐           │
│  │  PHYSICAL LAYER                               │           │
│  │  • Co-location: traders rent rack space IN    │           │
│  │    the same data center as the exchange       │           │
│  │  • Equal cable lengths for fairness           │           │
│  │  • IEX's "speed bump": 350μs delay coil to   │           │
│  │    level the playing field for all traders     │           │
│  └──────────────────────────────────────────────┘           │
└──────────────────────────────────────────────────────────────┘
```

### 12.2 Latency Budget Breakdown

```
Typical order-to-trade latency breakdown:

  ┌──────────────────────────┬─────────────┐
  │ Component                │ Latency     │
  ├──────────────────────────┼─────────────┤
  │ Network (client → GW)    │ 5-500 μs *  │
  │ Gateway validation       │ 2-5 μs      │
  │ Risk check               │ 3-10 μs     │
  │ Sequencer                │ 1-2 μs      │
  │ Matching engine          │ 1-10 μs     │
  │ Trade report to client   │ 5-500 μs *  │
  ├──────────────────────────┼─────────────┤
  │ Total (co-located)       │ ~12-30 μs   │
  │ Total (remote client)    │ ~1-50 ms    │
  └──────────────────────────┴─────────────┘
  * Depends on physical distance to exchange

  For reference:
  • NYSE Arca:     ~20 μs median order-to-ack
  • NASDAQ:        ~40 μs median
  • IEX:           ~350 μs (intentional speed bump)
  • Binance:       ~5 ms (crypto, less latency-sensitive)
```

### 12.3 The LMAX Disruptor Pattern

Many exchanges use a ring buffer pattern for inter-component communication:

```
  ┌──────────────────────────────────────────────┐
  │            RING BUFFER (Lock-Free)            │
  │                                               │
  │    ┌───┬───┬───┬───┬───┬───┬───┬───┐        │
  │    │ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │        │
  │    └───┴───┴───┴───┴───┴───┴───┴───┘        │
  │              ▲           ▲                    │
  │              │           │                    │
  │         Writer Ptr  Reader Ptr                │
  │         (producer)  (consumer)                │
  │                                               │
  │  • Pre-allocated fixed-size array             │
  │  • Single writer, multiple readers            │
  │  • No locks: CAS on sequence counter only     │
  │  • Cache-line padded to prevent false sharing │
  │  • Throughput: 100M+ events/sec               │
  └──────────────────────────────────────────────┘

  Data Flow:
  Gateway → [Ring Buffer] → Matching Engine → [Ring Buffer] → Market Data
                                            → [Ring Buffer] → Trade Settlement
```

---

## 13. Key Takeaways

```
┌──────────────────────────────────────────────────────────────────┐
│                        KEY TAKEAWAYS                             │
│                                                                  │
│  1. THE MATCHING ENGINE IS SINGLE-THREADED BY DESIGN.            │
│     No locks = no contention = deterministic = fair.             │
│     Parallelism comes from partitioning by symbol,               │
│     not from multi-threading within a symbol.                    │
│                                                                  │
│  2. THE ORDER BOOK IS THE CORE DATA STRUCTURE.                   │
│     TreeMap of price levels + FIFO queue per level +             │
│     HashMap for O(1) cancel. Elegantly simple, blazing fast.    │
│                                                                  │
│  3. CORRECTNESS TRUMPS EVERYTHING.                               │
│     A bug in matching can cause millions in losses. Strong       │
│     consistency, deterministic replay, and comprehensive         │
│     audit trails are non-negotiable.                             │
│                                                                  │
│  4. LATENCY IS MEASURED IN MICROSECONDS, NOT MILLISECONDS.       │
│     Kernel bypass, pre-allocated memory, CPU pinning, FPGA       │
│     network cards — every layer is optimized. This is not        │
│     a web application.                                           │
│                                                                  │
│  5. DETERMINISTIC REPLAY ENABLES FAULT TOLERANCE.                │
│     Same sequence of events → same state. Hot standbys           │
│     replay the event stream and can promote instantly.           │
│     No complex distributed consensus needed.                     │
│                                                                  │
│  6. THE SEQUENCER IS THE FAIRNESS GATEKEEPER.                    │
│     A single global sequence number ensures strict               │
│     time-priority ordering. Without it, network jitter           │
│     could let slower orders "cut in line."                       │
│                                                                  │
│  7. MARKET DATA FAN-OUT IS A MASSIVE SCALE PROBLEM.              │
│     10M updates/sec to 100K+ subscribers. Solved via             │
│     UDP multicast for institutions + hierarchical                │
│     WebSocket fan-out for retail.                                │
│                                                                  │
│  8. RISK MANAGEMENT IS THE SAFETY NET.                           │
│     Pre-trade checks (funds, limits, fat-finger),                │
│     post-trade monitoring (P&L, exposure), and                   │
│     circuit breakers (halt on extreme moves) protect             │
│     both participants and the market itself.                     │
└──────────────────────────────────────────────────────────────────┘
```

---

*This chapter is part of [Part VII — Real-World Case Studies](../INDEX.md)*
