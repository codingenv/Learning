# Chapter 9: Storage Systems

## Introduction — Storage is the Foundation

Every system ultimately needs to **persist data**. Whether it's user uploads (images, videos), application state (databases), log files, backups, or machine learning datasets — storage is the foundation on which everything else is built.

In system design, choosing the right storage technology is about understanding trade-offs between:
- **Latency** — How fast can you read/write?
- **Throughput** — How much data can you move per second?
- **Durability** — Will the data survive hardware failures?
- **Cost** — How much per GB per month?
- **Access patterns** — Random vs. sequential, read-heavy vs. write-heavy?

This chapter covers the three fundamental types of storage (block, file, object), distributed file systems, data lakes vs. data warehouses, storage tiering, and how to choose storage for different system design scenarios.

---

## Types of Storage: Block, File, and Object

These are the three foundational storage abstractions in modern computing:

```
  +--------------------+  +--------------------+  +--------------------+
  |   BLOCK STORAGE    |  |   FILE STORAGE     |  |   OBJECT STORAGE   |
  |                    |  |                    |  |                    |
  |  Fixed-size blocks |  |  Hierarchical      |  |  Flat namespace    |
  |  (512B - 4KB)      |  |  directories       |  |  with metadata     |
  |                    |  |  and files          |  |                    |
  |  /dev/sda1         |  |  /home/user/        |  |  bucket/key        |
  |  [blk][blk][blk]   |  |    docs/            |  |  {data + metadata} |
  |  [blk][blk][blk]   |  |      file.txt       |  |                    |
  |                    |  |    images/           |  |  HTTP API (REST)   |
  |  Raw disk access   |  |      photo.jpg      |  |  PUT, GET, DELETE   |
  +--------------------+  +--------------------+  +--------------------+
  |  DB, VMs, boot vol |  |  Shared file sys   |  |  Images, videos,   |
  |  High-performance  |  |  NAS, home dirs    |  |  backups, archives |
  +--------------------+  +--------------------+  +--------------------+
```

### Comprehensive Comparison

| Feature | Block Storage | File Storage | Object Storage |
|---|---|---|---|
| **Abstraction** | Raw blocks (no filesystem) | Files in directories (hierarchy) | Objects in buckets (flat namespace) |
| **Access Method** | SCSI, iSCSI, FC, NVMe | NFS, SMB/CIFS, POSIX | HTTP REST API (PUT/GET/DELETE) |
| **Latency** | Very low (<1ms SSD) | Low-Medium (1-10ms) | Medium (10-100ms) |
| **Throughput** | Very high | Medium-High | Very high (parallel) |
| **Max Size** | Limited by volume (16 TB typical) | Limited by filesystem | Unlimited (5 TB per object in S3) |
| **Modifiable** | Yes (any byte, any block) | Yes (edit in place) | **No** — immutable, replace entire object |
| **Metadata** | None (raw bytes) | Basic (filename, perms, timestamps) | Rich custom metadata (key-value pairs) |
| **Scalability** | Vertical (bigger disk/array) | Moderate | **Massive** (exabyte scale) |
| **Cost** | $$$ (most expensive) | $$ | $ (cheapest, especially archive) |
| **Durability** | Hardware RAID (99.999%) | Depends on implementation | 99.999999999% (11 nines for S3) |
| **Use Cases** | Databases, VMs, boot volumes | Shared files, CMS, media processing | Static assets, backups, data lakes |

---

## Block Storage — The Performance Tier

Block storage divides data into fixed-size **blocks** (typically 512 bytes to 4 KB) and stores them on raw disk. The storage device has no understanding of files — it only reads and writes numbered blocks. The **operating system's filesystem** (ext4, XFS, NTFS) sits on top, organizing blocks into files.

### Why Block Storage?

- **Lowest latency** — Direct disk access without filesystem overhead
- **Databases need it** — PostgreSQL, MySQL, Oracle write directly to block devices
- **VMs need it** — Virtual machine disk images sit on block storage
- **Boot volumes** — Operating systems boot from block storage

### Cloud Block Storage

| Service | Provider | Performance Tiers | Max IOPS | Max Throughput |
|---|---|---|---|---|
| **EBS** (Elastic Block Store) | AWS | gp3, io2, st1, sc1 | 256,000 (io2) | 4,000 MB/s |
| **Persistent Disk** | GCP | pd-standard, pd-ssd, pd-extreme | 120,000 | 2,400 MB/s |
| **Managed Disks** | Azure | Standard HDD/SSD, Premium SSD, Ultra | 160,000 | 4,000 MB/s |

### EBS Volume Types (AWS Example)

```
  +-------------------------------------------------------------------+
  |                    AWS EBS Volume Types                            |
  +-------------------------------------------------------------------+
  |                                                                   |
  |  gp3 (General Purpose SSD)   - Default choice for most workloads  |
  |    3,000 IOPS baseline, up to 16,000                              |
  |    125 MB/s baseline, up to 1,000 MB/s                            |
  |    Cost: ~$0.08/GB/month                                          |
  |                                                                   |
  |  io2 Block Express (Provisioned IOPS) - Mission-critical DBs      |
  |    Up to 256,000 IOPS, 4,000 MB/s                                 |
  |    Sub-millisecond latency                                        |
  |    Cost: ~$0.125/GB + $0.065/IOPS/month                           |
  |                                                                   |
  |  st1 (Throughput Optimized HDD) - Big data, data warehouses       |
  |    500 IOPS max, 500 MB/s throughput                              |
  |    Cost: ~$0.045/GB/month                                         |
  |                                                                   |
  |  sc1 (Cold HDD) - Infrequent access, archives                    |
  |    250 IOPS max, 250 MB/s throughput                              |
  |    Cost: ~$0.015/GB/month                                         |
  |                                                                   |
  +-------------------------------------------------------------------+
```

### SAN (Storage Area Network)

In enterprise data centers, block storage is often provided by a **SAN** — a dedicated high-speed network connecting servers to shared storage arrays.

```
  +--------+     +--------+     +--------+
  |Server 1|     |Server 2|     |Server 3|
  +---+----+     +---+----+     +---+----+
      |              |              |
      +--------------+--------------+
                     |
              +------v------+
              |  SAN Fabric |   (Fibre Channel or iSCSI)
              | (FC Switch) |
              +------+------+
                     |
              +------v------+
              | Storage     |   (Dell EMC, NetApp, Pure Storage)
              | Array       |
              | RAID 5/6/10 |
              | 100+ disks  |
              +-------------+
```

---

## File Storage — Shared Access

File storage provides a **hierarchical filesystem** (directories and files) accessible over a network protocol. Multiple clients can read and write to the same filesystem concurrently.

### Use Cases

- **Shared home directories** — Multiple users accessing the same files
- **Content management** — Web servers serving files from a shared store
- **Media processing** — Video transcoding pipelines reading/writing shared files
- **Machine learning** — Training data shared across multiple compute nodes
- **Legacy applications** — Apps that expect a POSIX filesystem

### Network File Protocols

| Protocol | Full Name | Platform | Use Case |
|---|---|---|---|
| **NFS** | Network File System | Linux/Unix | Shared Linux filesystems |
| **SMB/CIFS** | Server Message Block | Windows | Windows file shares |
| **AFP** | Apple Filing Protocol | macOS | Legacy macOS file sharing |

### Cloud File Storage

| Service | Provider | Protocol | Features |
|---|---|---|---|
| **EFS** (Elastic File System) | AWS | NFSv4 | Auto-scaling, multi-AZ, serverless |
| **FSx for Lustre** | AWS | Lustre | HPC, ML training (high throughput) |
| **FSx for Windows** | AWS | SMB | Windows workloads, Active Directory |
| **Filestore** | GCP | NFS | Managed NFS for GKE and Compute |
| **Azure Files** | Azure | SMB, NFS | Hybrid cloud, Azure integration |

### When to Use File Storage vs. Object Storage

```
  Need POSIX filesystem semantics?
  (open, read, write, seek, close)
      |
      +-- Yes --> File Storage (EFS, NFS)
      |           - Edit files in place
      |           - Random access within files
      |           - Directory structure
      |
      +-- No  --> Object Storage (S3)
                  - Write once, read many
                  - HTTP API access
                  - Massive scale, low cost
```

---

## Object Storage — The Scale Champion

Object storage stores data as **objects** in a **flat namespace** (no directory hierarchy, though "/" in key names simulates folders). Each object consists of:

1. **Data** — The actual binary content (up to 5 TB in S3)
2. **Metadata** — Key-value pairs describing the object (content-type, custom tags)
3. **Unique identifier** — The object key (e.g., `images/2024/01/photo.jpg`)

### Why Object Storage Dominates the Cloud

- **Virtually unlimited scale** — S3 stores over 100 trillion objects
- **11 nines of durability** — 99.999999999% (you'd lose 1 object in 10,000 objects stored for 10 million years)
- **Low cost** — $0.023/GB/month (S3 Standard), $0.004/GB/month (Glacier)
- **HTTP API** — Simple PUT/GET/DELETE; works from anywhere
- **Built-in versioning** — Keep every version of every object
- **Event notifications** — Trigger Lambda/functions on upload

### Cloud Object Storage Services

| Service | Provider | Max Object Size | Durability | Storage Classes |
|---|---|---|---|---|
| **S3** | AWS | 5 TB | 99.999999999% | Standard, IA, Glacier, Deep Archive |
| **GCS** | Google Cloud | 5 TB | 99.999999999% | Standard, Nearline, Coldline, Archive |
| **Azure Blob** | Microsoft | 4.75 TB (block) | 99.999999999% | Hot, Cool, Cold, Archive |
| **MinIO** | Self-hosted | No limit | Configurable | N/A (compatible with S3 API) |

### Object Storage Access Pattern

```
  Upload (PUT):
  +--------+   PUT /bucket/images/photo.jpg   +--------+
  | Client +---------------------------------->| S3     |
  |        |   + binary data                   |        |
  |        |   + metadata: {content-type:      | Stores |
  |        |     image/jpeg, user-id: 123}     | across |
  |        |<----------------------------------+ 3+ AZs |
  |        |   200 OK + ETag                   +--------+
  +--------+

  Download (GET):
  +--------+   GET /bucket/images/photo.jpg    +--------+
  | Client +---------------------------------->| S3     |
  |        |<----------------------------------+        |
  |        |   200 OK + binary data + metadata +--------+
  +--------+
```

### Pre-Signed URLs — Secure Direct Access

Instead of routing all uploads/downloads through your application server, generate **pre-signed URLs** that grant temporary, direct access to S3:

```
  1. Client requests upload URL from your API
  +--------+   "I want to upload photo.jpg"   +----------+
  | Client +--------------------------------->| Your API |
  |        |<---------------------------------+          |
  |        |   Pre-signed PUT URL             | Generates|
  |        |   (expires in 15 min)            | signed   |
  +--------+                                  | URL      |
       |                                      +----------+
       | 2. Client uploads directly to S3
       |      PUT pre-signed-url + binary data
       v
  +--------+
  |   S3   |   (No load on your API server!)
  +--------+
```

---

## HDFS — Hadoop Distributed File System

HDFS is a **distributed file system** designed to store **very large files** (GB to TB) across a cluster of commodity machines. It's the storage layer of the Hadoop ecosystem.

### Architecture

```
                    +------------------+
                    |    NameNode      |   (Master — metadata)
                    |                  |
                    | - File -> block  |
                    |   mapping        |
                    | - Block ->       |
                    |   DataNode map   |
                    | - Namespace      |
                    |   (directory     |
                    |    tree)         |
                    +--------+---------+
                             |
              +--------------+--------------+
              |              |              |
     +--------v---+  +------v-----+  +-----v------+
     | DataNode 1 |  | DataNode 2 |  | DataNode 3 |
     |            |  |            |  |            |
     | [Block A1] |  | [Block A2] |  | [Block A3] |
     | [Block B2] |  | [Block B1] |  | [Block B3] |  <- 3x replication
     | [Block C1] |  | [Block C3] |  | [Block C2] |
     +------------+  +------------+  +------------+
```

### Key Concepts

| Concept | Description |
|---|---|
| **NameNode** | Master server; stores metadata (file names, directory structure, block locations). Single point of failure (mitigated by Secondary NameNode or HA NameNode). |
| **DataNode** | Worker servers; store actual data blocks. Send heartbeats to NameNode. |
| **Block** | Default 128 MB (configurable). Large blocks reduce NameNode metadata load and optimize for sequential reads. |
| **Replication** | Each block is replicated 3x (default) across different DataNodes and racks for durability. |
| **Write-once** | Files are immutable once written (append-only). Optimized for batch processing, not random updates. |

### HDFS vs. Object Storage (S3)

| Feature | HDFS | S3 |
|---|---|---|
| **Architecture** | Self-managed cluster | Fully managed service |
| **Optimized for** | Batch processing (MapReduce, Spark) | General-purpose object storage |
| **Latency** | Low (data locality — compute near data) | Higher (network round-trip) |
| **Cost** | Higher (dedicated cluster) | Lower (pay-per-use) |
| **Scalability** | Limited by cluster size | Virtually unlimited |
| **Integration** | Hadoop ecosystem | Universal (any language/framework) |
| **Trend** | Declining (replaced by cloud storage) | Growing (S3 as universal storage) |

> **Industry trend:** Many organizations are migrating from HDFS to S3 (or GCS/ADLS) as the storage layer for big data, using compute engines like Spark, Presto, and Trino to query data directly on object storage.

---

## Data Lake vs. Data Warehouse

These are two architectures for storing and analyzing large volumes of data:

```
  +------- Data Lake --------+     +---- Data Warehouse ------+
  |                          |     |                          |
  | Raw, unstructured data   |     | Structured, curated data|
  | Schema-on-read           |     | Schema-on-write          |
  | Store everything         |     | Store what's needed      |
  |                          |     |                          |
  | +-----+ +------+ +----+ |     | +----+ +----+ +----+    |
  | |JSON | |Parquet| |CSV | |     | |Fact| |Dim | |Dim |    |
  | +-----+ +------+ +----+ |     | |Tbl | |Tbl | |Tbl |    |
  | +-----+ +------+ +----+ |     | +----+ +----+ +----+    |
  | |Logs | |Images| |Avro| |     | Star/Snowflake schema   |
  | +-----+ +------+ +----+ |     |                          |
  | Object Storage (S3)      |     | Redshift, BigQuery,     |
  | Delta Lake, Iceberg      |     | Snowflake               |
  +---------------------------+     +--------------------------+
```

### Detailed Comparison

| Dimension | Data Lake | Data Warehouse |
|---|---|---|
| **Data format** | Raw — JSON, CSV, Parquet, Avro, images, video | Structured — tables with defined schema |
| **Schema** | Schema-on-read (define at query time) | Schema-on-write (define before loading) |
| **Data quality** | Raw, uncurated (can become "data swamp") | Curated, cleaned, validated |
| **Storage** | Object storage (S3, GCS, ADLS) | Specialized columnar storage |
| **Users** | Data scientists, ML engineers | Business analysts, BI teams |
| **Processing** | Spark, Presto, Trino, Hive | SQL queries via warehouse engine |
| **Cost** | Very low (object storage rates) | Higher (compute + storage) |
| **Latency** | Seconds to minutes | Sub-second to seconds (optimized) |
| **Examples** | S3 + Delta Lake, GCS + BigLake | Snowflake, Redshift, BigQuery |

### The Lakehouse — Best of Both Worlds

Modern architectures combine data lake and data warehouse:
- **Delta Lake** (Databricks) — ACID transactions on S3/ADLS
- **Apache Iceberg** — Open table format for huge analytic datasets
- **Apache Hudi** — Incremental data processing on data lakes

---

## Storage Classes and Tiering

Not all data needs the same performance or availability. **Storage tiering** moves data to cheaper storage as it ages.

### S3 Storage Classes (AWS Example)

```
  Access Frequency    Storage Class          Cost/GB/month   Retrieval Cost
  ==================  =====================  ==============  ==============
  
  FREQUENT            S3 Standard            $0.023          Free
       |              (hot data)
       |
       |              S3 Standard-IA         $0.0125         $0.01/GB
       |              (infrequent access)
       |
       |              S3 One Zone-IA         $0.01           $0.01/GB
       |              (non-critical data)
       |
       |              S3 Glacier Instant     $0.004          $0.03/GB
       |              (archive, ms access)
       |
       |              S3 Glacier Flexible    $0.0036         Mins to hours
       |              (archive)
       |
  RARE                S3 Glacier Deep        $0.00099        12-48 hours
                      Archive
                      (cold data)
```

### Lifecycle Policies — Automated Tiering

```
  Object uploaded
       |
       | Day 0-30: S3 Standard ($0.023/GB)
       |   (frequently accessed)
       v
       | Day 30: Transition to S3 Standard-IA ($0.0125/GB)
       |   (still needed, but less frequently)
       v
       | Day 90: Transition to S3 Glacier Instant ($0.004/GB)
       |   (rarely accessed, but need fast retrieval)
       v
       | Day 365: Transition to S3 Glacier Deep Archive ($0.00099/GB)
       |   (compliance/regulatory retention)
       v
       | Day 2555 (7 years): Delete object
       |   (retention period expired)
```

**Real-world savings:** A company storing 100 TB of data could save **$20,000+/month** by implementing proper lifecycle policies instead of keeping everything in S3 Standard.

---

## Replication and Durability

### How S3 Achieves 11 Nines of Durability

S3's durability of **99.999999999%** (11 nines) means that if you store **10 million objects**, you can expect to lose **one object every 10,000 years**.

```
  How S3 stores your data:
  
  Object: photo.jpg (10 MB)
       |
       | Erasure coding + replication
       v
  +-----+  +-----+  +-----+  +-----+  +-----+  +-----+
  |Frag1|  |Frag2|  |Frag3|  |Frag4|  |Frag5|  |Frag6|
  | AZ-1|  | AZ-1|  | AZ-2|  | AZ-2|  | AZ-3|  | AZ-3|
  +-----+  +-----+  +-----+  +-----+  +-----+  +-----+
  
  - Data is split into fragments using erasure coding
  - Fragments distributed across 3+ Availability Zones
  - Even if an entire AZ is destroyed, data can be reconstructed
  - Background integrity checks continuously verify data
  - Corrupted fragments are automatically repaired
```

### Replication vs. Erasure Coding

| Approach | How It Works | Storage Overhead | Durability | Use Case |
|---|---|---|---|---|
| **3x Replication** | Store 3 full copies | 200% overhead | High | HDFS, Redis, Cassandra |
| **Erasure Coding** | Split into k data + m parity fragments; any k of (k+m) reconstruct data | ~50% overhead | Very high | S3, Azure Blob, Ceph |

```
  3x Replication:                 Erasure Coding (4+2):
  
  [Full Copy 1]                   [Data 1] [Data 2] [Data 3] [Data 4]
  [Full Copy 2]                   [Parity1] [Parity2]
  [Full Copy 3]
                                  Any 4 of 6 fragments reconstruct data
  12 MB for a 4 MB file           6 MB for a 4 MB file
  (3x overhead)                   (1.5x overhead)
```

---

## Content-Addressable Storage and Deduplication

### Content-Addressable Storage (CAS)

In CAS, data is stored and retrieved by its **content hash** rather than a location or filename.

```
  File content: "Hello, World!"
  SHA-256 hash: a591a6d40bf420...e671cf8
  
  Storage: hash -> content
  a591a6d40bf420...e671cf8 -> "Hello, World!"
  
  Two users upload identical files?
  Both get the SAME hash -> stored only ONCE!
```

**Used in:** Git (content-addressable object store), Docker image layers, backup systems, IPFS.

### Deduplication

Deduplication identifies and eliminates **duplicate copies** of data.

| Type | How It Works | Savings | Use Case |
|---|---|---|---|
| **File-level** | Hash entire files; skip duplicates | Moderate | Backup systems |
| **Block-level** | Hash fixed-size blocks; deduplicate blocks | High | VM storage, enterprise backup |
| **Byte-level** | Variable-length chunking + hashing | Highest | Advanced backup (Borg, Restic) |

```
  Without dedup:                With dedup:
  
  VM 1: [OS][App][Data1]       [OS]  <- shared
  VM 2: [OS][App][Data2]       [App] <- shared
  VM 3: [OS][App][Data3]       [Data1] [Data2] [Data3] <- unique
  
  Total: 9 blocks               Total: 5 blocks (44% savings)
```

---

## Choosing Storage for System Design Scenarios

### Decision Matrix

| Scenario | Storage Type | Specific Service | Why |
|---|---|---|---|
| **Database storage** | Block | EBS io2 / Persistent SSD | Low-latency random I/O for B-tree operations |
| **User profile images** | Object | S3 + CloudFront CDN | Scalable, cheap, HTTP-accessible, CDN-cacheable |
| **Video streaming platform** | Object + CDN | S3 + CloudFront / Netflix Open Connect | Store originals in S3, deliver via CDN |
| **Shared config files** | File | EFS / NFS | Multiple servers need POSIX filesystem access |
| **ML training data** | Object or File | S3 (with Lustre for HPC) | Large datasets, parallel reads across compute |
| **Log aggregation** | Object | S3 with lifecycle policies | Write-once, time-based tiering, cheap archive |
| **Database backups** | Object | S3 Glacier | Infrequent access, low cost, high durability |
| **Real-time analytics** | Block + Object | EBS (hot) + S3 (archive) | Fast processing, then archive for cost |
| **Container images** | Object (registry) | ECR / Docker Hub (backed by S3) | Immutable images, layered dedup |

### Example: Designing Storage for an Image Sharing App

```
  +--------+     Upload     +----------+      Store      +--------+
  | Mobile +--------------->| API      +----------------->| S3     |
  | App    |   (pre-signed  | Server   |   (original)     | Bucket |
  +--------+    URL)        +----+-----+                  +---+----+
                                 |                            |
                                 | Trigger                    |
                                 v                            |
                          +------+------+                     |
                          | Lambda /    |  Generate            |
                          | Image       |  thumbnails          |
                          | Processor   +---> S3 (thumbnails)  |
                          +-------------+                      |
                                                               |
  +--------+    Download   +----------+    Fetch from    +-----v----+
  | Mobile |<--------------| CDN      |<-----------------| S3       |
  | App    |   (cached at  | CloudFrnt|   (origin)       | Origin   |
  +--------+    edge)      +----------+                   +----------+
  
  Storage Design:
  - Originals: S3 Standard (first 30 days) -> S3 IA (after 30 days)
  - Thumbnails: S3 Standard (always, frequently accessed)
  - Deleted photos: S3 Glacier (kept 30 days for recovery)
  - Pre-signed URLs for direct upload (bypass API server)
  - CloudFront CDN for global, low-latency delivery
```

---

## Key Takeaways

```
+-------------------------------------------------------------------+
|              Storage Systems — Key Takeaways                      |
+-------------------------------------------------------------------+
|                                                                   |
|  1. Three types of storage: Block (databases, VMs), File          |
|     (shared filesystems), Object (images, backups, data lakes).   |
|     Object storage is the default for most new workloads.         |
|                                                                   |
|  2. Object storage (S3) offers unmatched durability (11 nines),   |
|     virtually unlimited scale, and very low cost. Use it for      |
|     anything that doesn't need in-place modification.             |
|                                                                   |
|  3. Block storage (EBS) is essential for databases and VMs        |
|     where low-latency random I/O is critical.                     |
|                                                                   |
|  4. HDFS is declining in favor of cloud object storage +          |
|     compute engines (Spark on S3). But understand its             |
|     architecture for interviews.                                  |
|                                                                   |
|  5. Storage tiering (hot/warm/cold/archive) can reduce costs      |
|     by 80%+. Implement lifecycle policies to automate             |
|     transitions.                                                  |
|                                                                   |
|  6. Erasure coding provides high durability with lower storage    |
|     overhead than simple replication (1.5x vs 3x).                |
|                                                                   |
|  7. Pre-signed URLs let clients upload/download directly from     |
|     object storage, reducing load on your application servers.    |
|                                                                   |
|  8. In system design interviews, always discuss storage           |
|     durability, access patterns, cost optimization, and CDN       |
|     integration for static content.                               |
|                                                                   |
+-------------------------------------------------------------------+
```

---

*Next Chapter: [Chapter 10 — Message Queues and Streaming](./10-message-queues.md)*
