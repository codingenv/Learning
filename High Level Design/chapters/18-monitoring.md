# Chapter 18: Monitoring, Logging, and Observability

## Introduction

In 2017, GitLab experienced a major database outage. An engineer accidentally deleted the
production database during a maintenance window. Recovery took 18 hours. What made it worse?
The team discovered that five out of their five backup strategies had been silently failing.
No alerts had been set up to detect backup failures.

This is the nightmare scenario that **observability** prevents. In a distributed system with
dozens of services, databases, and queues, you can't fix what you can't see. Monitoring,
logging, and tracing are the eyes and ears of your production infrastructure.

> **Key Insight:** Observability is not about collecting data. It's about being able to ask
> arbitrary questions about your system's behavior and get answers — especially questions
> you didn't anticipate needing to ask.

---

## 18.1 Monitoring vs Observability

These terms are related but distinct:

| Concept           | Definition                                                       |
|-------------------|------------------------------------------------------------------|
| **Monitoring**    | Tracking predefined metrics and alerting when thresholds breach  |
|                   | "Tell me when X goes wrong"                                     |
| **Observability** | The ability to understand a system's internal state from its     |
|                   | external outputs. "Help me figure out WHY X went wrong"          |

Monitoring tells you **something is broken**. Observability helps you understand **why**.

---

## 18.2 The Three Pillars of Observability

```
  +------------------------------------------------------------------+
  |                    OBSERVABILITY                                   |
  |                                                                    |
  |  +--------------+    +---------------+    +-------------------+   |
  |  |   METRICS    |    |    LOGS       |    |  DISTRIBUTED      |   |
  |  |              |    |               |    |  TRACES           |   |
  |  | Numeric data |    | Event records |    | Request flow      |   |
  |  | over time    |    | with context  |    | across services   |   |
  |  |              |    |               |    |                   |   |
  |  | "What is     |    | "What         |    | "Where did the    |   |
  |  |  happening?" |    |  happened?"   |    |  request go?"     |   |
  |  +--------------+    +---------------+    +-------------------+   |
  |                                                                    |
  |  Tools:              Tools:              Tools:                    |
  |  Prometheus          ELK Stack           Jaeger                   |
  |  Grafana             Splunk              Zipkin                   |
  |  Datadog             Fluentd             OpenTelemetry            |
  |  CloudWatch          Loki                AWS X-Ray                |
  +------------------------------------------------------------------+
```

Each pillar answers different questions:

| Question                                           | Pillar           |
|----------------------------------------------------|------------------|
| "What's the current request rate?"                 | Metrics          |
| "What was the error message for request #12345?"   | Logs             |
| "Which service caused the 2-second delay?"         | Traces           |
| "Is the system healthy right now?"                 | Metrics          |
| "What happened at 3:42 AM when the alert fired?"   | Logs             |
| "Why is this one user's request slow?"             | Traces           |

---

## 18.3 Pillar 1: Metrics

Metrics are **numeric measurements** collected over time. They're efficient to store,
query, and aggregate — the backbone of real-time monitoring.

### Metric Types

| Type          | Description                                | Example                        |
|---------------|--------------------------------------------|--------------------------------|
| **Counter**   | Monotonically increasing value             | Total HTTP requests: 1,234,567 |
|               | (only goes up, resets on restart)          | Total errors: 42               |
| **Gauge**     | Value that goes up AND down                | Current CPU usage: 73%         |
|               |                                            | Active connections: 847        |
| **Histogram** | Distribution of values in predefined       | Request latency: 50th=12ms,    |
|               | buckets                                    | 95th=89ms, 99th=340ms         |
| **Summary**   | Similar to histogram, but calculates       | Request latency: avg=15ms,     |
|               | quantiles on the client side               | median=12ms                    |

### Counter vs Gauge — Understanding the Difference

```
  Counter (Total Requests):           Gauge (Active Connections):
  
  ^                    /              ^
  |                  /                |        /\
  |               /                   |      /    \      /\
  |            /                      |    /        \  /    \
  |         /                         |  /            /      \
  |      /                            |/                      \
  |   /                               |
  +--+--+--+--+--+--+-->             +--+--+--+--+--+--+-->
     Time (always increasing)            Time (fluctuates)
```

### The RED Method (For Services)

The **RED method** provides the essential metrics for any request-driven service:

| Metric       | What It Measures                    | Example                           |
|--------------|-------------------------------------|-----------------------------------|
| **R**ate     | Requests per second                 | 2,500 req/s                       |
| **E**rrors   | Failed requests per second          | 12 errors/s (0.48% error rate)    |
| **D**uration | Latency distribution (histograms)   | p50=15ms, p95=89ms, p99=340ms    |

```
  RED Dashboard Layout:
  
  +------------------+  +------------------+  +------------------+
  | REQUEST RATE     |  | ERROR RATE       |  | LATENCY (p99)    |
  |                  |  |                  |  |                  |
  |  2,500 req/s     |  |  0.48%           |  |  340ms           |
  |  ~~~~~~~~~~~~    |  |  ~~~~~~~~~~~~    |  |  ~~~~~~~~~~~~    |
  |  /\  /\  /\     |  |      __          |  |          /\      |
  | /  \/  \/  \    |  |  ___/  \___      |  |  ___/\_/  \__   |
  +------------------+  +------------------+  +------------------+
```

### The USE Method (For Resources)

The **USE method** is for infrastructure resources (CPU, memory, disk, network):

| Metric            | What It Measures                    | Alert Threshold (Typical)   |
|-------------------|-------------------------------------|-----------------------------|
| **U**tilization   | How busy the resource is            | > 80% sustained             |
| **S**aturation    | How much extra work is queued       | > 0 (any queue buildup)     |
| **E**rrors        | Number of error events              | > 0 (investigate any errors)|

```
  USE Method Applied to a Database:
  
  Resource: PostgreSQL Connection Pool
  
  Utilization:  75/100 connections in use (75%)  [OK]
  Saturation:   12 queries waiting for a connection  [WARNING!]
  Errors:       0 connection errors  [OK]
  
  Interpretation: Pool is 75% utilized but queries are queuing.
  Action: Increase pool size or optimize slow queries.
```

### Prometheus Metrics Example

```yaml
# Counter — total HTTP requests
http_requests_total{method="GET", endpoint="/api/users", status="200"} 145892
http_requests_total{method="GET", endpoint="/api/users", status="500"} 23

# Gauge — current active connections
active_connections{service="user-service"} 847

# Histogram — request duration in seconds
http_request_duration_seconds_bucket{le="0.01"} 24054    # < 10ms
http_request_duration_seconds_bucket{le="0.05"} 33421    # < 50ms
http_request_duration_seconds_bucket{le="0.1"}  38123    # < 100ms
http_request_duration_seconds_bucket{le="0.5"}  40001    # < 500ms
http_request_duration_seconds_bucket{le="1.0"}  40053    # < 1s
http_request_duration_seconds_bucket{le="+Inf"} 40070    # All
```

### Metrics Tools Comparison

| Tool              | Type           | Strengths                          | Weaknesses                     |
|-------------------|----------------|------------------------------------|--------------------------------|
| **Prometheus**    | Open-source    | Pull-based, PromQL, huge ecosystem | Local storage, not for logs    |
| **Grafana**       | Open-source    | Beautiful dashboards, multi-source | Visualization only (no storage)|
| **Datadog**       | Commercial     | Full platform (metrics+logs+traces)| Expensive at scale             |
| **CloudWatch**    | AWS-managed    | Deep AWS integration               | AWS-only, limited PromQL       |
| **InfluxDB**      | Open-source    | Purpose-built time-series DB       | Smaller ecosystem than Prom    |
| **New Relic**     | Commercial     | APM + infrastructure monitoring    | Can be costly                  |

---

## 18.4 Pillar 2: Logging

Logs are **discrete event records** — the most detailed source of information about what
happened in your system.

### Structured vs Unstructured Logging

```
  Unstructured Log (Hard to parse and search):
  
  2024-01-15 14:23:45 ERROR Failed to process payment for user 12345.
  Amount: $99.99. Error: Connection timeout to payment gateway.
  
  
  Structured Log (JSON — Easy to parse, index, and search):
  
  {
    "timestamp": "2024-01-15T14:23:45.123Z",
    "level": "ERROR",
    "service": "payment-service",
    "instance": "payment-3a8b",
    "correlation_id": "req-7f3a-4b2c-9d1e",
    "user_id": 12345,
    "event": "payment_failed",
    "amount": 99.99,
    "currency": "USD",
    "error": "Connection timeout",
    "dependency": "stripe-api",
    "latency_ms": 30000,
    "retry_count": 3
  }
```

> **Best Practice:** Always use **structured logging** (JSON). It makes logs searchable,
> filterable, and aggregatable. Unstructured logs become unmanageable at scale.

### Log Levels

| Level     | When to Use                                        | Example                           |
|-----------|----------------------------------------------------|-----------------------------------|
| **TRACE** | Extremely fine-grained debugging                   | "Entering method processOrder()"  |
| **DEBUG** | Detailed diagnostic information                    | "Cache miss for key: user:123"    |
| **INFO**  | Normal operational events                          | "Order #456 processed successfully"|
| **WARN**  | Potential issues, recoverable problems             | "Retry 2/3 for payment gateway"   |
| **ERROR** | Errors that affect a specific operation             | "Payment failed for order #456"   |
| **FATAL** | System-wide critical failures                      | "Database connection pool exhausted"|

```
  Log Level Pyramid:
  
          /\
         /  \     FATAL  (almost never — system is dying)
        /    \
       / WARN \    ERROR  (investigate, but system continues)
      / ERROR  \
     /  INFO    \  WARN   (watch for patterns)
    /   DEBUG    \
   /    TRACE     \ INFO   (normal operations — bulk of production logs)
  +----------------+
                    DEBUG/TRACE (development only — too verbose for production)
  
  Production logging level: INFO (with ability to temporarily enable DEBUG)
```

### Centralized Logging Architecture

```
  +----------+     +----------+     +----------+
  | Service  |     | Service  |     | Service  |
  |    A     |     |    B     |     |    C     |
  +----+-----+     +----+-----+     +----+-----+
       |                |                |
       | (logs)         | (logs)         | (logs)
       |                |                |
  +----v-----+     +----v-----+     +----v-----+
  | Log Agent|     | Log Agent|     | Log Agent|
  | (Filebeat|     | (Filebeat|     | (Filebeat|
  |  Fluentd)|     |  Fluentd)|     |  Fluentd)|
  +----+-----+     +----+-----+     +----+-----+
       |                |                |
       +-------+--------+--------+-------+
               |                 |
        +------v------+  +------v------+
        |   Message   |  |   Message   |   (Buffer for reliability)
        |   Queue     |  |   Queue     |
        |   (Kafka)   |  |   (Kafka)   |
        +------+------+  +------+------+
               |                 |
        +------v-----------------v------+
        |      Log Processing           |
        |      (Logstash / Fluentd)     |
        |      - Parse, transform       |
        |      - Enrich with metadata   |
        |      - Filter noise           |
        +------+------------------------+
               |
        +------v-----------+
        |   Elasticsearch  |    (Storage + Search)
        |   / Opensearch   |
        +------+-----------+
               |
        +------v-----------+
        |   Kibana /       |    (Visualization + Search UI)
        |   Grafana Loki   |
        +------------------+
```

### ELK Stack vs Alternatives

| Tool Stack          | Components                            | Pros                         | Cons                      |
|---------------------|---------------------------------------|------------------------------|---------------------------|
| **ELK Stack**       | Elasticsearch, Logstash, Kibana       | Powerful search, mature      | Resource-heavy, complex   |
| **EFK Stack**       | Elasticsearch, Fluentd, Kibana        | Fluentd lighter than Logstash| Still needs Elasticsearch |
| **Grafana Loki**    | Loki + Grafana                        | Lightweight, labels like Prom| Less powerful full-text    |
| **Splunk**          | Splunk Enterprise/Cloud               | Best search, enterprise grade| Very expensive             |
| **Datadog Logs**    | Datadog platform                      | Unified with metrics/traces  | Cost scales with volume   |

### Correlation IDs

A **Correlation ID** (or Request ID) is a unique identifier that follows a request across
all services. This is essential for debugging in microservice architectures.

```
  Request Flow with Correlation ID:
  
  Client Request
  X-Correlation-ID: req-7f3a-4b2c-9d1e
       |
       v
  API Gateway (logs: correlation_id=req-7f3a-4b2c-9d1e)
       |
       v
  User Service (logs: correlation_id=req-7f3a-4b2c-9d1e)
       |
       +---> Order Service (logs: correlation_id=req-7f3a-4b2c-9d1e)
       |         |
       |         +---> Payment Service (logs: correlation_id=req-7f3a-4b2c-9d1e)
       |         |
       |         +---> Inventory Service (logs: correlation_id=req-7f3a-4b2c-9d1e)
       |
       v
  Response to Client
  
  
  Now you can search for "req-7f3a-4b2c-9d1e" across ALL services
  and see the complete request journey:
  
  14:23:45.100 [API Gateway]       Received GET /api/orders/456
  14:23:45.105 [User Service]      Authenticated user 12345
  14:23:45.120 [Order Service]     Fetching order #456
  14:23:45.125 [Payment Service]   Checking payment status
  14:23:45.890 [Payment Service]   ERROR: Stripe timeout (765ms)
  14:23:45.891 [Order Service]     WARN: Payment check failed, using cache
  14:23:45.895 [API Gateway]       Response 200 (795ms, degraded)
```

### Log Aggregation at Scale

At large scale (millions of log events per second), you need to manage costs and performance:

| Strategy                   | Description                                      | Impact                  |
|----------------------------|--------------------------------------------------|-------------------------|
| **Sampling**               | Keep 1 in 100 logs for high-volume endpoints     | 99% storage reduction   |
| **Log levels in prod**     | Only INFO and above in production                | Reduces noise           |
| **Structured fields**      | Index key fields, store message as unindexed     | Faster queries          |
| **Retention policies**     | Hot: 7 days, Warm: 30 days, Cold: 1 year        | Tiered storage costs    |
| **Drop noise**             | Filter health check logs, readiness probes       | Less noise              |

---

## 18.5 Pillar 3: Distributed Tracing

In a monolith, a stack trace shows you the full request path. In microservices, a request
passes through many services — distributed tracing reconstructs that journey.

### Key Concepts

| Concept           | Definition                                                      |
|-------------------|-----------------------------------------------------------------|
| **Trace**         | The entire journey of a single request through the system       |
| **Span**          | A single operation within a trace (one service, one DB call)    |
| **Trace ID**      | Unique identifier for the entire trace                          |
| **Span ID**       | Unique identifier for each span                                 |
| **Parent Span ID**| Links a span to its parent (builds the tree structure)          |
| **Trace Context** | Headers that propagate trace/span IDs across service boundaries |

### Trace Visualization

```
  Trace ID: abc-123
  
  [API Gateway]         |=====================|                    (100ms total)
                        |                     |
  [User Service]        |==|                                       (10ms)
                           |
  [Order Service]          |=============|                         (60ms)
                           |             |
  [Payment Service]        |  |=======|  |                         (35ms)
                           |  |       |  |
  [Stripe API]            |  | |===|  | |                         (20ms)
                           |             |
  [Inventory Service]      |        |==| |                         (12ms)
  
  
  Span Breakdown:
  +-----------+------------------+----------+---------+----------+
  | Span ID   | Service          | Duration | Status  | Parent   |
  +-----------+------------------+----------+---------+----------+
  | span-001  | API Gateway      | 100ms    | OK      | (root)   |
  | span-002  | User Service     | 10ms     | OK      | span-001 |
  | span-003  | Order Service    | 60ms     | OK      | span-001 |
  | span-004  | Payment Service  | 35ms     | OK      | span-003 |
  | span-005  | Stripe API       | 20ms     | OK      | span-004 |
  | span-006  | Inventory Service| 12ms     | OK      | span-003 |
  +-----------+------------------+----------+---------+----------+
```

### Trace Context Propagation

Trace context is propagated via HTTP headers using the W3C Trace Context standard:

```
  Service A --> Service B --> Service C
  
  HTTP Headers:
  traceparent: 00-abc123-span001-01
               ^  ^       ^       ^
               |  |       |       |
          version |   span-id   flags(sampled)
              trace-id
  
  
  Service A creates:   traceparent: 00-abc123-span001-01
  Service B receives:  traceparent: 00-abc123-span001-01
  Service B creates:   traceparent: 00-abc123-span002-01  (new span, same trace)
  Service C receives:  traceparent: 00-abc123-span002-01
```

### Distributed Tracing Tools

| Tool               | Type          | Protocol Support        | Best For                     |
|--------------------|---------------|-------------------------|------------------------------|
| **Jaeger**         | Open-source   | OpenTelemetry, Zipkin   | Kubernetes-native, CNCF      |
| **Zipkin**         | Open-source   | Zipkin, OpenTelemetry   | Simple setup, well-documented|
| **OpenTelemetry**  | Open standard | Vendor-neutral          | Future-proof, multi-vendor   |
| **AWS X-Ray**      | AWS-managed   | X-Ray SDK               | AWS-native applications      |
| **Datadog APM**    | Commercial    | OpenTelemetry, DD agent | Full-platform integration    |
| **Tempo (Grafana)**| Open-source   | OpenTelemetry           | Grafana ecosystem            |

### OpenTelemetry — The Future Standard

**OpenTelemetry (OTel)** is the emerging vendor-neutral standard for observability. It
provides a single set of APIs, SDKs, and tools for metrics, logs, and traces.

```
  OpenTelemetry Architecture:
  
  +----------------+     +----------------+     +----------------+
  | Service A      |     | Service B      |     | Service C      |
  | +------------+ |     | +------------+ |     | +------------+ |
  | | OTel SDK   | |     | | OTel SDK   | |     | | OTel SDK   | |
  | +-----+------+ |     | +-----+------+ |     | +-----+------+ |
  +-------+--------+     +-------+--------+     +-------+--------+
          |                       |                       |
          +----------+------------+-----------+-----------+
                     |                        |
              +------v------+          +------v------+
              | OTel        |          | OTel        |
              | Collector   |          | Collector   |
              | (Agent)     |          | (Gateway)   |
              +------+------+          +------+------+
                     |                        |
           +---------+---------+    +---------+---------+
           |         |         |    |         |         |
        +--v--+  +---v--+  +--v-+  +--v--+  +--v---+  +--v--+
        |Jaeger|  |Prom  |  |Loki|  |Tempo|  |Datadog|  |X-Ray|
        +------+  +------+  +----+  +-----+  +------+  +-----+
  
  Benefits: One SDK, multiple backends. Switch vendors without code changes.
```

---

## 18.6 SLI, SLO, and SLA

### Definitions

```
  +------------------------------------------------------------------+
  |                                                                    |
  |  SLI (Service Level Indicator)                                    |
  |  "What we measure"                                                 |
  |  Example: Percentage of requests completing in < 200ms            |
  |                                                                    |
  |  SLO (Service Level Objective)                                    |
  |  "What we aim for"                                                 |
  |  Example: 99.9% of requests should complete in < 200ms           |
  |                                                                    |
  |  SLA (Service Level Agreement)                                    |
  |  "What we promise (with consequences)"                            |
  |  Example: If uptime drops below 99.9%, customer gets credits      |
  |                                                                    |
  +------------------------------------------------------------------+
  
  Relationship:  SLI (measurement) --> SLO (target) --> SLA (contract)
                 Technical            Engineering       Business/Legal
```

### Common SLIs

| SLI Category      | Metric                                           | Example SLO             |
|--------------------|--------------------------------------------------|-------------------------|
| **Availability**   | % of successful requests (non-5xx)               | >= 99.9%                |
| **Latency**        | % of requests faster than threshold              | p99 < 500ms             |
| **Throughput**     | Requests processed per second                    | >= 10,000 req/s         |
| **Error rate**     | % of requests resulting in errors                | < 0.1%                  |
| **Durability**     | % of data not lost (for storage systems)         | >= 99.999999999% (11 9s)|
| **Freshness**      | % of data updated within expected time           | 95% within 1 minute     |

### The Nines of Availability

| Availability | Annual Downtime    | Monthly Downtime | Called         |
|--------------|--------------------|------------------|----------------|
| 99%          | 3 days, 15 hours   | 7.3 hours        | "Two nines"    |
| 99.9%        | 8 hours, 46 minutes| 43.8 minutes     | "Three nines"  |
| 99.95%       | 4 hours, 23 minutes| 21.9 minutes     | "Three and half"|
| 99.99%       | 52 minutes, 36 sec | 4.4 minutes      | "Four nines"   |
| 99.999%      | 5 minutes, 16 sec  | 26.3 seconds     | "Five nines"   |

### Error Budgets

An **error budget** is the allowed amount of unreliability within an SLO period. It
creates a balance between reliability and development velocity.

```
  SLO: 99.9% availability per month
  
  Error Budget = 100% - 99.9% = 0.1%
  
  In a month with 30 days:
  Error Budget = 30 * 24 * 60 * 0.001 = 43.2 minutes of downtime allowed
  
  
  Error Budget Policy:
  
  Budget remaining > 50%:  Ship features aggressively
  Budget remaining 20-50%: Ship carefully, extra testing
  Budget remaining < 20%:  Freeze features, focus on reliability
  Budget exhausted (0%):   Full freeze, only reliability work allowed
  
  
  Error Budget Burn Rate:
  
  +100%|########                                   |
       |######                                     |
   50% |####                                       | <- Budget burning fast!
       |##                                         |    Slow down deployments
    0% |#_________________________________________|
       Day 1    Day 7   Day 14   Day 21   Day 30
```

---

## 18.7 Alerting

### Alert Fatigue

**Alert fatigue** is the #1 problem with monitoring systems. Too many alerts cause teams
to ignore them — including the critical ones.

```
  Bad Alerting:
  
  3:00 AM  [ALERT] CPU at 71% on web-server-3        (who cares?)
  3:01 AM  [ALERT] Disk at 62% on db-replica-2       (not urgent)
  3:05 AM  [ALERT] 404 rate increased by 5%          (probably fine)
  3:12 AM  [ALERT] Response time p99 at 520ms        (slightly above normal)
  3:15 AM  [ALERT] PRODUCTION DATABASE DOWN           <-- MISSED because of
  3:16 AM  [ALERT] Memory at 65% on cache-server-1        alert fatigue!
  
  
  Good Alerting:
  
  3:15 AM  [PAGE - CRITICAL] Error rate > 5% for 3 minutes
           Affected: payment-service
           Dashboard: https://grafana.internal/d/payments
           Runbook: https://wiki.internal/runbooks/payment-errors
```

### Alerting Best Practices

| Practice                         | Description                                         |
|----------------------------------|-----------------------------------------------------|
| **Alert on symptoms, not causes**| Alert: "Error rate > 5%" not "CPU > 80%"           |
| **Use multi-window alerts**      | "Error rate > 1% for 5 min AND > 0.5% for 30 min" |
| **Include context in alerts**    | Dashboard link, runbook link, affected service      |
| **Tiered severity**              | Page for critical, ticket for warning, log for info |
| **Require runbooks**             | Every alert must have a documented response         |
| **Review alerts quarterly**      | Delete stale alerts, tune noisy ones                |

### Alert Severity Levels

| Severity     | Response Time    | Channel                  | Example                         |
|--------------|------------------|---------------------------|---------------------------------|
| **P1/SEV1**  | Immediate (5 min)| PagerDuty phone call      | Production outage               |
| **P2/SEV2**  | 30 minutes       | Slack notification        | Elevated error rate             |
| **P3/SEV3**  | Business hours   | Email / ticket            | Disk approaching capacity       |
| **P4/SEV4**  | Next sprint      | Dashboard / backlog       | Non-critical performance dip    |

### Alerting Tools

| Tool            | Type          | Strengths                               |
|-----------------|---------------|-----------------------------------------|
| **PagerDuty**   | Commercial    | Sophisticated escalation, on-call mgmt  |
| **OpsGenie**    | Atlassian     | Good Jira integration, affordable       |
| **Alertmanager**| Open-source   | Native Prometheus integration            |
| **Grafana Alerts**| Open-source | Unified with dashboards                 |
| **Datadog**     | Commercial    | Full-platform alerting                   |

---

## 18.8 Health Checks and Readiness Probes

Health checks allow infrastructure (load balancers, orchestrators) to determine if a
service instance is functioning correctly.

### Types of Health Checks

| Type                | Purpose                                          | Kubernetes Equivalent  |
|---------------------|--------------------------------------------------|------------------------|
| **Liveness check**  | "Is the process alive and not deadlocked?"       | `livenessProbe`        |
| **Readiness check** | "Can this instance accept traffic?"              | `readinessProbe`       |
| **Startup check**   | "Has the application finished initializing?"     | `startupProbe`         |

```
  Health Check Flow:
  
  Load Balancer / Kubernetes
       |
       | GET /health/live    (every 10s)
       | GET /health/ready   (every 5s)
       |
       v
  +----+----------------+
  | Service Instance    |
  |                     |
  | /health/live:       |
  |   - Process running |    --> 200 OK (alive)
  |   - Not deadlocked  |    --> 500 (restart me!)
  |                     |
  | /health/ready:      |
  |   - DB connected    |    --> 200 OK (send traffic)
  |   - Cache warmed    |    --> 503 (don't send traffic yet)
  |   - Dependencies OK |
  +---------------------+
```

### Health Check Response Example

```json
GET /health/ready

HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "UP",
  "timestamp": "2024-01-15T14:23:45Z",
  "checks": {
    "database": {
      "status": "UP",
      "latency_ms": 3
    },
    "redis": {
      "status": "UP",
      "latency_ms": 1
    },
    "payment_gateway": {
      "status": "DEGRADED",
      "latency_ms": 2500,
      "message": "Response time elevated"
    }
  },
  "version": "2.4.1",
  "uptime_seconds": 86432
}
```

---

## 18.9 Dashboarding Best Practices

### Dashboard Hierarchy

```
  Level 1: Executive Dashboard (1 screen)
  +-----------------------------------------------+
  | System Health: GREEN                           |
  | Active Users: 45,231 | Revenue: $12.4K/min    |
  | Error Rate: 0.02%    | p99 Latency: 180ms     |
  +-----------------------------------------------+
  
  Level 2: Service Dashboard (per service)
  +-----------------------------------------------+
  | User Service                                   |
  | RED Metrics: Rate | Errors | Duration          |
  | Dependencies: DB (OK) | Cache (OK) | Auth (OK)|
  | Instance Health: 8/8 healthy                   |
  +-----------------------------------------------+
  
  Level 3: Debug Dashboard (detailed)
  +-----------------------------------------------+
  | User Service - Deep Dive                       |
  | CPU/Memory per instance                        |
  | Query latency by endpoint                      |
  | Connection pool utilization                    |
  | GC pauses and thread counts                    |
  +-----------------------------------------------+
```

### Dashboard Design Principles

| Principle                          | Description                                       |
|------------------------------------|---------------------------------------------------|
| **Answer one question per panel**  | Each graph should answer a specific question       |
| **Use consistent time ranges**     | All panels on a dashboard should share time range  |
| **Show context**                   | Include SLO targets as reference lines             |
| **Progressive disclosure**         | High-level overview -> click to drill down         |
| **Include annotations**            | Mark deployments, incidents on graphs              |
| **Avoid vanity metrics**           | "Total users ever" is useless; "Active users/min"  |
|                                    | is actionable                                      |

---

## 18.10 Putting It All Together — Observability Architecture

```
  Complete Observability Stack:
  
  +----------+  +----------+  +----------+  +----------+
  | Service  |  | Service  |  | Service  |  | Service  |
  |    A     |  |    B     |  |    C     |  |    D     |
  | +------+ |  | +------+ |  | +------+ |  | +------+ |
  | | OTel | |  | | OTel | |  | | OTel | |  | | OTel | |
  | | SDK  | |  | | SDK  | |  | | SDK  | |  | | SDK  | |
  | +--+---+ |  | +--+---+ |  | +--+---+ |  | +--+---+ |
  +----+-----+  +----+-----+  +----+-----+  +----+-----+
       |              |              |              |
       +-----------+--+-----------+--+-----------+--+
                   |              |              |
            +------v------+ +----v----+  +------v------+
            | Metrics     | | Logs    |  | Traces      |
            | (Prometheus)| | (Loki)  |  | (Tempo)     |
            +------+------+ +----+----+  +------+------+
                   |              |              |
                   +-------+------+------+------+
                           |             |
                    +------v------+ +----v---------+
                    | Grafana     | | Alertmanager |
                    | Dashboards  | | + PagerDuty  |
                    +-------------+ +--------------+
  
  Incident Response Flow:
  
  1. Alert fires: "Error rate > 5% on Order Service"
  2. Engineer opens Grafana dashboard -> sees spike at 3:42 AM
  3. Checks logs (Loki): filters by service=order-service, level=ERROR
  4. Finds: "Connection refused: payment-service:8080"
  5. Opens trace (Tempo): sees spans failing at payment-service
  6. Checks payment-service metrics: all instances show 0 connections
  7. Root cause: Payment service deployment failed, no healthy instances
  8. Resolution: Rollback payment service deployment
  9. Post-mortem: Add readiness probe to prevent bad deployments
```

---

## 18.11 Common Anti-Patterns

| Anti-Pattern                            | Problem                                  | Solution                          |
|-----------------------------------------|------------------------------------------|-----------------------------------|
| **Log everything**                      | Storage costs explode, signal lost       | Log levels + sampling             |
| **Alert on every metric**               | Alert fatigue, team ignores alerts       | Alert on symptoms, SLO-based      |
| **No correlation IDs**                  | Can't trace requests across services     | Generate ID at edge, propagate    |
| **Metrics without labels**              | Can't drill down ("which endpoint?")     | Use meaningful labels/tags        |
| **Dashboards with 50 panels**           | Information overload, nothing stands out | Focus on RED/USE, progressive drill|
| **No runbooks for alerts**              | On-call engineer doesn't know what to do | Every alert gets a runbook        |
| **Monitoring only happy paths**         | Failures go undetected                   | Monitor errors, timeouts, queues  |
| **Not testing observability**           | Discover monitoring gaps during incidents| Chaos engineering + game days     |

---

## Key Takeaways

1. **The three pillars of observability** — Metrics, Logs, and Traces — each serve a
   distinct purpose. You need all three for comprehensive visibility.

2. **Metrics** (Prometheus + Grafana) give you real-time system health. Use the **RED
   method** for services (Rate, Errors, Duration) and **USE method** for resources
   (Utilization, Saturation, Errors).

3. **Structured logging** (JSON) is essential at scale. Always include correlation IDs,
   timestamps, service names, and meaningful context.

4. **Distributed tracing** (Jaeger, OpenTelemetry) reconstructs request paths across
   services. It's indispensable for diagnosing latency issues in microservices.

5. **OpenTelemetry** is the future standard — invest in it for vendor-neutral, unified
   instrumentation across metrics, logs, and traces.

6. **SLOs and error budgets** create an objective framework for balancing reliability and
   feature velocity. Every service should have defined SLOs.

7. **Alert on symptoms, not causes.** Alert when users are impacted (high error rate, slow
   responses), not when a specific resource metric changes.

8. **Combat alert fatigue** ruthlessly — every alert should be actionable, have a runbook,
   and page the right person at the right severity level.

9. **Health checks** (liveness + readiness) are essential for container orchestration and
   load balancing. Make them meaningful — check real dependencies, not just "process alive."

10. **In system design interviews**, discuss observability when designing any production
    system. Mention metrics, logging, tracing, SLOs, and alerting to show you think about
    operational excellence, not just the happy path.

---

*Previous Chapter: [Chapter 17 - Resilience Patterns](./17-resilience-patterns.md)*
