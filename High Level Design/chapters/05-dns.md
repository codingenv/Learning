# Chapter 5: DNS and Domain Resolution

## Introduction — The Phonebook of the Internet

Every time you type `www.google.com` into your browser, something remarkable happens behind the scenes. Your computer has no idea where `www.google.com` lives — it only understands IP addresses like `142.250.80.46`. The **Domain Name System (DNS)** is the distributed, hierarchical system that translates human-readable domain names into machine-readable IP addresses.

Think of DNS as the **phonebook of the internet**. Just as you look up a person's name to find their phone number, DNS looks up a domain name to find its IP address. Without DNS, you'd need to memorize IP addresses for every website you visit.

### Why DNS Matters in System Design

- **It's the first step in every web request** — DNS resolution happens before any HTTP connection is established
- **It enables load balancing** — DNS can distribute traffic across multiple servers
- **It enables geographic routing** — DNS can route users to the nearest data center
- **It provides failover** — DNS can redirect traffic when servers go down
- **It's a potential bottleneck and single point of failure** — DNS outages can take down entire services

> **Real-world impact:** In October 2021, Facebook experienced a massive outage because their DNS records were withdrawn from the internet via a BGP update. For over 6 hours, billions of users couldn't access Facebook, Instagram, or WhatsApp — all because DNS couldn't resolve their domain names.

---

## DNS Hierarchy

DNS is organized as a **hierarchical, distributed database**. No single server holds all DNS records. Instead, the responsibility is distributed across millions of servers organized in a tree structure.

```
                         +------------------+
                         |   Root Servers   |   (13 logical root server clusters)
                         |    (. root)      |   (e.g., a.root-servers.net)
                         +--------+---------+
                                  |
              +-------------------+-------------------+
              |                   |                   |
     +--------v-------+  +-------v--------+  +-------v--------+
     |  .com TLD      |  |  .org TLD      |  |  .io TLD       |
     |  Servers       |  |  Servers       |  |  Servers       |
     +--------+-------+  +-------+--------+  +-------+--------+
              |                   |                   |
    +---------+---------+        |            +------+-------+
    |                   |        |            |              |
+---v------+   +--------v--+ +--v--------+ +-v--------+ +---v------+
|google.com|   |amazon.com | |wikipedia  | |github.io | |stripe.io |
|Auth NS   |   |Auth NS    | |.org       | |Auth NS   | |Auth NS   |
+----------+   +-----------+ |Auth NS    | +----------+ +----------+
                              +-----------+
```

### The Four Players in DNS Resolution

| Component | Role | Example | Cache TTL |
|---|---|---|---|
| **DNS Recursive Resolver** | Your "agent" — takes your query and does the legwork | ISP's resolver, Google (8.8.8.8), Cloudflare (1.1.1.1) | Hours to days |
| **Root Name Server** | Knows where to find TLD servers | 13 clusters (a–m.root-servers.net) | 48 hours |
| **TLD Name Server** | Knows authoritative servers for domains under its TLD | Verisign manages `.com` and `.net` | 24–48 hours |
| **Authoritative Name Server** | Holds the actual DNS records for a domain | `ns1.google.com` for google.com | Configured by domain owner |

### Root Servers — The Starting Point

There are **13 logical root server clusters**, labeled A through M. In reality, each "root server" is a cluster of hundreds of servers distributed globally using **anycast** routing. Together, they handle over 10 billion queries per day.

Root servers don't know specific domain IP addresses. They only know where to find the **TLD servers** (`.com`, `.org`, `.net`, `.io`, etc.).

### TLD (Top-Level Domain) Servers

TLD servers are responsible for all domains under a specific extension:

- **Generic TLDs (gTLDs):** `.com`, `.org`, `.net`, `.info`, `.io`
- **Country-code TLDs (ccTLDs):** `.us`, `.uk`, `.de`, `.jp`, `.in`
- **Sponsored TLDs:** `.edu`, `.gov`, `.mil`

The `.com` TLD alone has over **160 million** registered domains.

### Authoritative Name Servers

These servers hold the **actual DNS records** for a domain. When you register `myapp.com` with a registrar (e.g., GoDaddy, Namecheap), you configure the authoritative name servers, which then hold your A records, CNAME records, MX records, etc.

---

## DNS Resolution Flow — Step by Step

Let's trace what happens when you type `www.example.com` in your browser:

```
   User's Browser           Recursive Resolver        Root Server
   +----------+             +---------------+         +-----------+
   |          |  1. Query   |               | 2. Ask  |           |
   |  Type    +------------>| Where is      +-------->| I don't   |
   |  www.    |             | www.example   |         | know, but |
   | example  |             | .com?         |<--------+ ask .com  |
   | .com     |             |               | 3. Ref  | TLD at    |
   |          |             |               |         | x.x.x.x  |
   +----------+             +-------+-------+         +-----------+
                                    |
                                    | 4. Ask .com TLD
                                    v
                            +---------------+
                            |  .com TLD     |
                            |  Server       |
                            |               |
                            | "example.com  |
                            |  NS is at     |
                            |  y.y.y.y"     |
                            +-------+-------+
                                    |
                                    | 5. Referral
                                    v
                            +---------------+         +----------+
                            |               | 6. Ask  |          |
                            | Recursive     +-------->| Auth NS  |
                            | Resolver      |         | for      |
                            |               |<--------+ example  |
                            |               | 7. IP:  | .com     |
                            +-------+-------+ 93.184  |          |
                                    |         .216.34 +----------+
                           8. Return|
                              IP    |
                                    v
                            +-------+-------+
                            |   Browser     |
                            | connects to   |
                            | 93.184.216.34 |
                            +---------------+
```

### Step-by-Step Breakdown

1. **Browser cache check** — Browser checks its own DNS cache (Chrome: `chrome://net-internals/#dns`)
2. **OS cache check** — Operating system checks its DNS cache (`/etc/hosts` file, systemd-resolved)
3. **Recursive resolver query** — If not cached, the OS sends the query to the configured recursive resolver
4. **Root server query** — Resolver asks a root server: "Where is `www.example.com`?"
5. **Root server referral** — Root server responds: "I don't know, but `.com` TLD is at this IP"
6. **TLD server query** — Resolver asks the `.com` TLD: "Where is `example.com`?"
7. **TLD server referral** — TLD responds: "The authoritative NS for `example.com` is at this IP"
8. **Authoritative server query** — Resolver asks the authoritative NS: "What's the IP for `www.example.com`?"
9. **Answer returned** — Authoritative NS responds with the IP address
10. **Resolver caches and returns** — Resolver caches the result and returns it to the client

> **Performance note:** A full DNS resolution (cold cache) can take **20–120ms**. With caching, subsequent lookups take **<1ms**.

---

## DNS Record Types

DNS records are stored as **Resource Records (RRs)** on authoritative name servers. Each record has a **name**, **type**, **TTL**, and **value**.

### Common Record Types

| Record Type | Purpose | Example | When to Use |
|---|---|---|---|
| **A** | Maps domain to IPv4 address | `example.com -> 93.184.216.34` | Primary website hosting |
| **AAAA** | Maps domain to IPv6 address | `example.com -> 2606:2800:220:1:...` | IPv6 support |
| **CNAME** | Alias — points domain to another domain | `www.example.com -> example.com` | Subdomains, CDN integration |
| **MX** | Mail exchange server for the domain | `example.com -> mail.example.com (pri: 10)` | Email routing |
| **NS** | Specifies authoritative name servers | `example.com -> ns1.example.com` | Domain delegation |
| **TXT** | Arbitrary text data | `example.com -> "v=spf1 include:_spf.google.com"` | SPF, DKIM, domain verification |
| **SRV** | Service location (host + port) | `_sip._tcp.example.com -> 5060 sipserver.example.com` | Service discovery (VoIP, LDAP) |
| **SOA** | Start of Authority — domain metadata | TTL, admin email, serial number | Zone management |
| **PTR** | Reverse DNS — IP to domain | `34.216.184.93.in-addr.arpa -> example.com` | Email verification, logging |

### CNAME vs A Record — A Common Confusion

```
# A Record — directly maps to IP
example.com.        300   IN   A      93.184.216.34

# CNAME — alias that points to another domain name
www.example.com.    300   IN   CNAME  example.com.
blog.example.com.   300   IN   CNAME  myapp.herokuapp.com.

# Important: CNAME requires an extra resolution step!
# www.example.com -> (CNAME) -> example.com -> (A) -> 93.184.216.34
```

**Key rule:** You CANNOT place a CNAME at the zone apex (e.g., `example.com` itself). CNAMEs are only for subdomains. Some providers offer "ALIAS" or "ANAME" records to work around this.

### MX Records — Email Routing

```
example.com.    300   IN   MX   10  mail1.example.com.
example.com.    300   IN   MX   20  mail2.example.com.
example.com.    300   IN   MX   30  mail3.example.com.
```

The number is the **priority** (lower = higher priority). If `mail1` is down, the sending server tries `mail2`, then `mail3`.

---

## TTL (Time-To-Live) — DNS Caching

Every DNS record has a **TTL** value (in seconds) that tells resolvers how long to cache the record before asking again.

### Caching at Every Level

```
+----------+    +----------+    +-----------+    +----------+    +------------+
| Browser  |--->| OS       |--->| Recursive |--->| TLD      |--->|Authoritative|
| Cache    |    | Cache    |    | Resolver  |    | Server   |    | Server     |
| (60s)    |    | (varies) |    | Cache     |    | Cache    |    | (source)   |
|          |    |          |    | (TTL)     |    | (48hrs)  |    |            |
+----------+    +----------+    +-----------+    +----------+    +------------+
```

### TTL Trade-offs

| TTL Duration | Pros | Cons | Good For |
|---|---|---|---|
| **Short (30–300s)** | Fast propagation of changes, quick failover | More DNS queries, higher latency | Active failover, A/B testing |
| **Medium (300–3600s)** | Balanced between freshness and efficiency | Moderate propagation delay | Most web applications |
| **Long (3600–86400s)** | Fewer DNS queries, lower latency | Slow propagation, hard to change quickly | Stable services, MX records |

> **Best practice:** Before a planned migration, reduce TTL to 60s a few days in advance. After migration is complete and verified, increase TTL back to a longer duration.

---

## DNS-Based Load Balancing

DNS can be used as a simple but effective load balancing mechanism.

### Round-Robin DNS

The simplest form — return multiple A records for the same domain, and clients pick one (usually the first).

```
example.com.    300   IN   A   10.0.0.1
example.com.    300   IN   A   10.0.0.2
example.com.    300   IN   A   10.0.0.3

# DNS server rotates the order with each query:
# Query 1: [10.0.0.1, 10.0.0.2, 10.0.0.3]
# Query 2: [10.0.0.2, 10.0.0.3, 10.0.0.1]
# Query 3: [10.0.0.3, 10.0.0.1, 10.0.0.2]
```

**Limitations:**
- No health checking — traffic still sent to dead servers
- No awareness of server load
- Clients may cache and stick to one IP
- Uneven distribution due to caching at various levels

### Weighted DNS

Assign weights to records to control traffic distribution:

```
# Send 70% traffic to server 1, 30% to server 2
example.com.    300   IN   A   10.0.0.1   ; weight 70
example.com.    300   IN   A   10.0.0.2   ; weight 30
```

Useful for **canary deployments** — send a small percentage of traffic to a new version.

### GeoDNS — Geographic Routing

GeoDNS resolves the same domain to **different IP addresses** based on the **geographic location** of the requesting resolver.

```
                        +-------------------+
                        |  GeoDNS Server    |
                        |  api.example.com  |
                        +---------+---------+
                                  |
              +-------------------+-------------------+
              |                   |                   |
    +---------v--------+ +-------v--------+ +--------v--------+
    | US-East DC       | | EU-West DC     | | AP-South DC     |
    | 10.1.0.1         | | 10.2.0.1       | | 10.3.0.1        |
    | (US users)       | | (EU users)     | | (Asia users)    |
    +------------------+ +----------------+ +-----------------+
```

**How it works:**
1. User in Tokyo queries `api.example.com`
2. GeoDNS identifies the resolver's IP as being in Asia
3. Returns the IP of the Asia-Pacific data center
4. User connects to the nearest server with lowest latency

**Providers:** AWS Route 53 (Geolocation Routing), Cloudflare, NS1, Dyn

### DNS Failover and Health Checks

Modern DNS providers support **health checks** — they periodically probe your servers and remove unhealthy ones from DNS responses.

```
              DNS Provider (e.g., Route 53)
              +---------------------------+
              | Health Check Module        |
              |                           |
              | Check: HTTP GET /health   |
              | Interval: 30s             |
              | Threshold: 3 failures     |
              +--+----------+----------+--+
                 |          |          |
            +----v---+ +---v----+ +---v----+
            |Server A| |Server B| |Server C|
            |  OK    | | FAIL   | |  OK    |
            +--------+ +--------+ +--------+

   DNS Response: [Server A IP, Server C IP]
   (Server B removed from rotation)
```

**Failover patterns:**
- **Active-Active:** All healthy servers receive traffic
- **Active-Passive:** Secondary server only receives traffic when primary fails
- **Latency-based:** Route to the server with lowest measured latency

---

## DNS Security

DNS was designed in the 1980s **without security in mind**. This makes it vulnerable to several attacks.

### Common DNS Attacks

| Attack | Description | Impact |
|---|---|---|
| **DNS Spoofing/Poisoning** | Inject false records into resolver cache | Users redirected to malicious sites |
| **DNS Amplification DDoS** | Use open resolvers to amplify attack traffic | Target overwhelmed with DNS responses |
| **DNS Hijacking** | Compromise authoritative NS or registrar account | Complete domain takeover |
| **DNS Tunneling** | Encode data in DNS queries to bypass firewalls | Data exfiltration |

### DNSSEC (DNS Security Extensions)

DNSSEC adds **cryptographic signatures** to DNS records, allowing resolvers to verify that records haven't been tampered with.

```
  +------------------+        +------------------+
  | Authoritative NS |        | Recursive        |
  |                  |        | Resolver         |
  | Record: A 1.2.3.4|       |                  |
  | Signature: RRSIG |------->| Verify signature |
  | Public Key: DNSKEY|       | using DNSKEY     |
  |                  |        | and trust chain  |
  +------------------+        +------------------+
```

**How DNSSEC works:**
1. Zone owner signs DNS records with a **private key**
2. The **DNSKEY** record contains the public key
3. **RRSIG** records contain signatures for each record set
4. **DS** (Delegation Signer) records in the parent zone establish a **chain of trust**
5. Resolvers validate the chain from root to the authoritative zone

### DNS over HTTPS (DoH) and DNS over TLS (DoT)

Traditional DNS queries are sent in **plaintext over UDP port 53**, allowing anyone on the network to see what domains you're visiting.

| Feature | Traditional DNS | DNS over TLS (DoT) | DNS over HTTPS (DoH) |
|---|---|---|---|
| **Port** | UDP/53 | TCP/853 | TCP/443 |
| **Encryption** | None | TLS | HTTPS (TLS) |
| **Privacy** | None — queries visible | Encrypted, but identifiable as DNS | Blends with regular HTTPS traffic |
| **Blocking** | Easy to block/monitor | Can be blocked on port 853 | Very hard to block (same port as HTTPS) |
| **Support** | Universal | Growing | Firefox, Chrome, major resolvers |

---

## Real-World: How Large-Scale Services Use DNS

### Multi-Region Deployment with DNS

Consider a global e-commerce platform with data centers in 3 regions:

```
                    +---------------------+
                    |   DNS Provider      |
                    |   (Route 53)        |
                    |                     |
                    | GeoDNS + Health     |
                    | Checks + Failover   |
                    +----+-----+-----+---+
                         |     |     |
           +-------------+     |     +-------------+
           |                   |                   |
   +-------v-------+  +-------v-------+  +--------v------+
   |  US-East-1    |  |  EU-West-1    |  |  AP-South-1   |
   |  ALB          |  |  ALB          |  |  ALB          |
   |  10 servers   |  |  8 servers    |  |  6 servers    |
   +---------------+  +---------------+  +---------------+

   DNS Configuration:
   shop.example.com  GeoDNS
     US users  -> us-alb.example.com   (ALIAS -> ALB DNS)
     EU users  -> eu-alb.example.com   (ALIAS -> ALB DNS)
     AP users  -> ap-alb.example.com   (ALIAS -> ALB DNS)
     Default   -> us-alb.example.com   (fallback)
```

### DNS Design Patterns at Scale

1. **Low TTL for dynamic services:** Use 60s TTL for services that need quick failover
2. **High TTL for static assets:** Use 86400s TTL for CDN CNAMEs pointing to static content
3. **Separate domains for different services:** `api.example.com`, `cdn.example.com`, `mail.example.com`
4. **Use multiple NS providers:** Some organizations use two DNS providers for redundancy
5. **Pre-warm DNS caches:** Before launches, ensure DNS records are propagated globally

### Netflix's DNS Strategy

Netflix uses a sophisticated DNS strategy:
- **Route 53** for primary DNS with health checks
- **GeoDNS** to route users to the nearest **Open Connect** CDN server
- **Low TTLs** on records pointing to dynamic services
- **DNS-based failover** between AWS regions
- **Separate domains** for streaming (`nflxvideo.net`) vs. the main site (`netflix.com`)

---

## Common DNS Pitfalls in System Design

1. **Ignoring DNS propagation delay** — Changes don't take effect instantly; old cached records may persist
2. **Using DNS as the sole load balancer** — DNS lacks health awareness (unless using smart DNS providers)
3. **Not accounting for DNS resolution time** — Cold DNS lookups add 50–200ms to the first request
4. **CNAME chains** — Multiple CNAME hops increase resolution time (each hop requires another lookup)
5. **Forgetting about DNS in disaster recovery** — If your DNS provider goes down, nothing works
6. **Not monitoring DNS** — Track resolution time, query volume, and error rates

---

## Key Takeaways

```
+-------------------------------------------------------------------+
|                    DNS — Key Takeaways                             |
+-------------------------------------------------------------------+
|                                                                   |
|  1. DNS is a hierarchical, distributed system — not a single      |
|     server. It has Root, TLD, and Authoritative layers.           |
|                                                                   |
|  2. DNS resolution involves multiple steps, but caching at        |
|     every level (browser, OS, resolver) makes it fast.            |
|                                                                   |
|  3. TTL is a critical trade-off: short TTL = fast changes but     |
|     more load; long TTL = good performance but slow updates.      |
|                                                                   |
|  4. GeoDNS enables routing users to the nearest data center       |
|     — essential for global, low-latency applications.             |
|                                                                   |
|  5. DNS failover with health checks provides basic HA, but        |
|     is limited by TTL — not instant failover.                     |
|                                                                   |
|  6. DNSSEC, DoH, and DoT address DNS security and privacy,       |
|     but adoption is still growing.                                |
|                                                                   |
|  7. In system design interviews, always mention DNS as the        |
|     first step in request flow, and consider GeoDNS for           |
|     multi-region architectures.                                   |
|                                                                   |
+-------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 6 — Load Balancers](./06-load-balancers.md)*
