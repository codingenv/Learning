# AWS Solutions Architect Certification - Study Guide

**Target Exam:** AWS Solutions Architect - Associate (SAA-C03)  
**Audience:** Cloud beginners with enterprise architecture background  
**Difficulty Level:** Beginner → Intermediate  
**Estimated Study Time:** 6-8 weeks

---

## 📑 Table of Contents (Index)

1. [Introduction to Cloud & AWS](#introduction-to-cloud--aws)
2. [AWS Fundamentals](#aws-fundamentals)
3. [AWS Regions and Availability Zones](#aws-regions-and-availability-zones) ⭐ **CRITICAL**
   - [What are AWS Regions?](#what-are-aws-regions)
   - [What are Availability Zones?](#what-are-availability-zones)
   - [Region vs Availability Zone](#region-vs-availability-zone)
   - [Selecting Regions for Your Application](#selecting-regions-for-your-application)
   - [High Availability with Multi-AZ](#high-availability-with-multi-az)
   - [Disaster Recovery and Multi-Region](#disaster-recovery-and-multi-region)
   - [Regional Services vs Global Services](#regional-services-vs-global-services)
4. [AWS Compute Services](#aws-compute-services) ⭐ **FOCUS AREA**
   - [EC2 (Elastic Compute Cloud)](#ec2-elastic-compute-cloud)
   - [Lambda (Serverless Compute)](#lambda-serverless-compute)
   - [Elastic Beanstalk](#elastic-beanstalk)
   - [ECS & EKS (Container Services)](#ecs--eks-container-services)
   - [Elasticsearch (Search & Analytics)](#elasticsearch-search--analytics)
   - [Auto Scaling](#auto-scaling)
5. [Key Concepts Mapping](#key-concepts-mapping)
6. [Exam Tips & Quick Reference](#exam-tips--quick-reference)

---

## Introduction to Cloud & AWS

### What is Cloud Computing?

**Simple Definition:** Instead of buying and maintaining physical servers in your datacenter, you rent computing resources (servers, storage, databases) from a cloud provider (AWS) on-demand.

**Your Existing Knowledge Bridge:**
- You manage 100,000+ Dell servers in OpenManage Enterprise
- Cloud = AWS manages infrastructure, you focus on applications
- Scale from 1 server to 1,000 servers in minutes

### Why AWS?

- **On-Demand:** Pay only for what you use (per hour, per second)
- **Global:** 30+ regions worldwide
- **Reliable:** 99.99% uptime SLAs
- **Secure:** Encryption, IAM, compliance certifications
- **Wide Service Range:** 200+ services

### Key AWS Regions to Know

- `us-east-1` (N. Virginia) — Most services, lowest cost
- `eu-west-1` (Ireland) — Europe default
- `ap-south-1` (Mumbai) — India
- `ap-southeast-1` (Singapore) — SE Asia

---

## AWS Fundamentals

### Core Concepts

| Concept | Explanation | Your Analogy |
|---|---|---|
| **Region** | Geographic area with multiple datacenters | Dell datacenter location (US, Europe, India) |
| **Availability Zone (AZ)** | Isolated datacenter within a region | Individual server rack in your datacenter |
| **Service** | AWS offering (EC2, S3, RDS, Lambda) | Dell Enterprise Service (servers, storage) |
| **Console** | Web interface to manage AWS | Dell iDrac or OpenManage interface |
| **API** | Programmatic way to interact with AWS | REST API calls to infrastructure |
| **VPC** | Virtual Private Cloud — your isolated network | Private enterprise network |
| **Security Group** | Firewall rules for instances | Network firewall policies |

### AWS Account Structure

```
AWS Account (root)
├── VPC (Virtual Private Cloud)
│   ├── Subnet 1 (AZ1)
│   │   └── EC2 Instance
│   └── Subnet 2 (AZ2)
│       └── EC2 Instance
├── RDS Database
├── S3 Buckets
└── Lambda Functions
```

### Cost Model

**Pay-As-You-Go Pricing:**
- EC2: $0.05-0.20 per hour (depending on instance type)
- Lambda: $0.20 per million requests + compute time
- S3: $0.023 per GB stored
- RDS: Similar to EC2 + data transfer costs

---

## AWS Regions and Availability Zones

### What are AWS Regions?

#### Definition

**Simple:** A geographical area where AWS has deployed multiple isolated datacenters.

**Key Points:**
- AWS has **33+ Regions** worldwide (expanding)
- Each region is **completely independent**
- Different regions have **different availability, pricing, services**
- You choose region when launching resources

#### Global Map of AWS Regions

```
North America         Europe             Asia Pacific       South America
├─ us-east-1         ├─ eu-west-1      ├─ ap-south-1      └─ sa-east-1
├─ us-west-1         ├─ eu-central-1   ├─ ap-southeast-1       (São Paulo)
├─ us-west-2         ├─ eu-north-1     ├─ ap-northeast-1
├─ ca-central-1      └─ eu-west-2      └─ ap-northeast-2

Africa               Middle East        China (Special)
├─ af-south-1        ├─ me-south-1      ├─ cn-north-1
└─ (limited)         └─ (limited)       └─ cn-northwest-1
```

#### Your Bridge: Dell Datacenter Locations

| AWS Concept | Dell Equivalent |
|---|---|
| **Region (us-east-1)** | Dell datacenter in Virginia, USA |
| **Region (ap-south-1)** | Dell datacenter in Bangalore, India |
| **Region (eu-west-1)** | Dell datacenter in Ireland, Europe |
| **Multiple regions** | Dell's global datacenter footprint |

**You manage 100K+ servers globally. AWS Regions are similar — just in the cloud.**

#### Key Characteristics of Regions

1. **Isolation** — Region A resources cannot directly access Region B resources
   ```
   EC2 in us-east-1 ❌ Cannot directly access S3 bucket in eu-west-1
   (Must use APIs / data transfer over internet)
   ```

2. **Independent** — Issues in one region don't affect others
   ```
   Power outage in us-east-1 → All other regions unaffected
   ```

3. **Different Pricing** — Regions have different costs
   ```
   us-east-1:  t3.medium = $0.0416/hour
   eu-west-1: t3.medium = $0.0458/hour (10% more expensive)
   ap-south-1: t3.medium = $0.0375/hour (10% cheaper)
   ```

4. **Different Services** — Not all services available in all regions
   ```
   Some services only in: us-east-1, us-west-2, eu-west-1
   Limited services in: ap-south-1, sa-east-1
   ```

#### Choosing a Region: Factors

| Factor | Impact | Example |
|---|---|---|
| **Data Residency Laws** | CRITICAL | EU customer data must stay in Europe (GDPR) |
| **Latency** | High | Users in Mumbai → use ap-south-1 (low latency) |
| **Disaster Recovery** | Medium | Keep backup in different region |
| **Cost** | Low-Medium | us-east-1 is cheapest (oldest, most capacity) |
| **Service Availability** | Medium | New services launch in us-east-1 first |

**Real Example:** Your Defect Management System
```
Primary Region: ap-south-1 (Mumbai)
- Low latency for India users
- Cost-effective for India workload
- Meets data residency requirements

Backup Region: ap-southeast-1 (Singapore)
- Near primary for quick failover
- Different AWS infrastructure
- Minimal additional latency
```

---

### What are Availability Zones?

#### Definition

**Simple:** Isolated datacenters within a single AWS region.

**Key Points:**
- Each region has **2-4 Availability Zones (AZs)**
- Each AZ has **physically separate infrastructure**
- Connected by **low-latency, high-bandwidth network**
- You can distribute resources across AZs for resilience

#### Visual Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    AWS Region: ap-south-1 (Mumbai)          │
│                                                              │
│  ┌──────────────────┐   ┌──────────────────┐                │
│  │  Availability    │   │  Availability    │                │
│  │  Zone 1 (ap-1a)  │   │  Zone 2 (ap-1b)  │                │
│  │                  │   │                  │                │
│  │  ┌────────────┐  │   │  ┌────────────┐  │                │
│  │  │ EC2 Server │  │   │  │ EC2 Server │  │                │
│  │  └────────────┘  │   │  └────────────┘  │                │
│  │  ┌────────────┐  │   │  ┌────────────┐  │                │
│  │  │ RDS Master │  │   │  │ RDS Slave  │  │                │
│  │  └────────────┘  │   │  └────────────┘  │                │
│  │                  │   │                  │                │
│  └────────┬─────────┘   └────────┬─────────┘                │
│           │  High-speed network  │                          │
│           └──────────────────────┘                          │
│                                                              │
│  Completely separate:                                       │
│  - Physical infrastructure                                  │
│  - Power supplies                                           │
│  - Network connections                                      │
│  - But close proximity (1-10 km apart)                      │
└─────────────────────────────────────────────────────────────┘
```

#### AZ Naming Convention

**Format:** `[Region]-[AZ Letter]`

Examples:
```
Region: ap-south-1 (Asia Pacific - Mumbai)
├── ap-south-1a (AZ 1)
└── ap-south-1b (AZ 2)

Region: us-east-1 (N. Virginia)
├── us-east-1a
├── us-east-1b
├── us-east-1c
└── us-east-1d

Region: eu-west-1 (Ireland)
├── eu-west-1a
├── eu-west-1b
└── eu-west-1c
```

#### Your Bridge: Physical Datacenter Analogy

| AWS Concept | Dell Datacenter |
|---|---|
| **AZ (ap-south-1a)** | Building 1 at Bangalore datacenter |
| **AZ (ap-south-1b)** | Building 2 at Bangalore datacenter |
| **Both in Region** | Same city, 5-10 km apart |
| **Separate power** | Independent power grids |
| **Separate networks** | Different fiber connections |

**In your OpenManage Enterprise:**
- You might have servers in Rack 1 (AZ1) and Rack 2 (AZ2)
- If power fails to Rack 1, Rack 2 continues running
- AWS AZs are the same concept at larger scale

#### Characteristics of Availability Zones

| Characteristic | Benefit | Your Context |
|---|---|---|
| **Physically separate** | Isolated from each other | Like separate server racks |
| **Same region latency** | 1-2 milliseconds between AZs | Practical for replication |
| **Independent failure** | One AZ down ≠ Others affected | Like Rack 1 down ≠ Rack 2 down |
| **Shared regional services** | Can share RDS, ELB across AZs | Single database, multiple servers |
| **Network redundancy** | Multiple independent connections | No single point of network failure |

---

### Region vs Availability Zone

#### Quick Comparison

| Aspect | Region | Availability Zone |
|---|---|---|
| **Geographic Scale** | Continental (e.g., entire Europe) | City (e.g., Mumbai) |
| **Physical Distance** | 1,000+ km apart | 1-50 km apart |
| **Latency Between** | 50-200+ ms | 1-2 ms |
| **Independence** | Completely independent | Independent but connected |
| **Use Case** | Disaster recovery, multi-country | High availability, resilience |
| **Data Transfer Cost** | Expensive ($0.02/GB out) | Free (within region) |
| **Network Latency Impact** | High | Minimal |

#### Decision Tree: Region vs AZ

```
Question: "How do I ensure my app doesn't go down?"

├─ Option 1: Within same AZ
│   └─ Problem: Single point of failure (power outage, hardware failure)
│       → Not recommended for production
│
├─ Option 2: Across multiple AZs in same region
│   └─ Benefits: 
│       - Protects from AZ failure
│       - Low latency (1-2 ms)
│       - Same region (free data transfer)
│       - High availability (99.99%)
│   └─ Use: Primary database in AZ1, replica in AZ2
│       → Recommended for production
│
└─ Option 3: Across multiple regions
    └─ Benefits:
        - Protects from region failure
        - Disaster recovery (complete failure)
        - Geo-proximity (users in each region)
    └─ Tradeoff:
        - Higher latency (50+ ms)
        - Data transfer costs ($0.02/GB)
        - Complexity (manage multiple deployments)
    └─ Use: Only if required for compliance or disaster recovery
```

---

### Selecting Regions for Your Application

#### Step 1: Check Compliance Requirements

**Regulatory Requirements:**
```
EU (GDPR)         → Data must stay in eu-* regions
                    (eu-west-1, eu-central-1, eu-north-1)

India (India Cloud) → Data should be in ap-south-1 (if applicable)

China              → Special cn-* regions (completely separate)

USA (HIPAA)        → HIPAA-compliant regions (us-east-1, us-west-2)

Finance (PCI)      → PCI-compliant regions
```

**Your Scenario:**
- Company: Dell (US)
- Data: Mostly in India (Bangalore)
- Compliance: GDPR (if EU customers)
- **Decision: Primary ap-south-1 (Mumbai), Backup ap-southeast-1 (Singapore)**

#### Step 2: Consider User Latency

**Latency Impact:**
```
Latency < 100ms: Users don't notice
Latency 100-500ms: Sluggish, annoying
Latency > 500ms: Unusable

Golden Rule: Deploy in region closest to users
```

**Example Deployment:**
```
Your Users            Recommended Region       Latency
─────────────────────────────────────────────────────────
India (Delhi)        ap-south-1 (Mumbai)     5-10 ms
SE Asia              ap-southeast-1          10-30 ms
Europe               eu-west-1               50-100 ms
USA East             us-east-1               150-200 ms
USA West             us-west-2               200-250 ms
Australia            ap-southeast-2          100-150 ms
```

#### Step 3: Check Service Availability

**New AWS services launch first in:**
1. us-east-1 (most features)
2. us-west-2
3. eu-west-1
4. Later → other regions

**Action:** Check AWS region table → https://aws.amazon.com/about-aws/global-infrastructure/

#### Step 4: Compare Pricing

**Cost Variation by Region (t3.medium on-demand):**
```
us-east-1:      $0.0416/hour  (baseline, cheapest)
us-west-1:      $0.0583/hour  (+40%)
eu-west-1:      $0.0458/hour  (+10%)
ap-south-1:     $0.0375/hour  (-10%, cheapest in Asia)
ap-northeast-1: $0.0546/hour  (+31%)
```

**Impact:** 10% region cost difference = ~$26/month per running instance

#### Recommendation Matrix

| Scenario | Primary Region | Backup Region | Reason |
|---|---|---|---|
| **India startup** | ap-south-1 | ap-southeast-1 | Low cost, close to users, data residency |
| **EU company** | eu-west-1 | eu-central-1 | GDPR compliance, similar latency |
| **Global users** | us-east-1 + ap-south-1 + eu-west-1 | — | Geo-distributed for latency |
| **Cost-conscious** | us-east-1 | us-west-2 | Cheapest, good services availability |
| **High availability** | ap-south-1 (AZ1 + AZ2) | ap-southeast-1 | Multi-AZ for HA, multi-region for DR |

---

### High Availability with Multi-AZ

#### The Problem: Single AZ

**Scenario: Your Spring Boot app in single AZ**
```
Single AZ (ap-south-1a)
├── EC2 Instance 1: Spring Boot app
├── EC2 Instance 2: Spring Boot app
├── RDS Master: PostgreSQL
└── EBS Volume: Application data

Risk: AZ1 power failure
→ Everything down
→ Your defect management system offline
→ Customer escalations
→ Complete failure
```

#### The Solution: Multi-AZ Deployment

**High Availability Architecture:**
```
┌──────────────────────────────────────────────────────────────┐
│              AWS Region (ap-south-1)                         │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌───────────────────────┐   ┌───────────────────────┐     │
│  │  Availability Zone 1   │   │  Availability Zone 2   │     │
│  │  (ap-south-1a)        │   │  (ap-south-1b)        │     │
│  │                        │   │                        │     │
│  │  ┌────────────────┐    │   │  ┌────────────────┐    │     │
│  │  │  EC2 Instance  │    │   │  │  EC2 Instance  │    │     │
│  │  │ (Spring Boot)  │    │   │  │ (Spring Boot)  │    │     │
│  │  └────────┬───────┘    │   │  └────────┬───────┘    │     │
│  │           │            │   │           │            │     │
│  │  ┌────────▼─────────┐  │   │  ┌────────▼─────────┐  │     │
│  │  │ Subnet 1         │  │   │  │ Subnet 2         │  │     │
│  │  └──────────────────┘  │   │  └──────────────────┘  │     │
│  │                        │   │                        │     │
│  └────────┬───────────────┘   └────────┬───────────────┘     │
│           │                           │                      │
│           └─────────────┬─────────────┘                      │
│                         │                                   │
│                  ┌──────▼──────┐                            │
│                  │ Load Balancer│ (Application Load Balancer)│
│                  │   (ALB)      │                            │
│                  └──────┬──────┘                             │
│                         │                                   │
│              ┌──────────▼──────────┐                        │
│              │  RDS Multi-AZ       │                        │
│              │  ┌────────┐         │                        │
│              │  │ Master │ (AZ1)   │                        │
│              │  │        │ (Active)│                        │
│              │  └────────┘         │                        │
│              │  ┌────────┐         │                        │
│              │  │ Standby│ (AZ2)   │                        │
│              │  │(Synced)│(Passive)│                        │
│              │  └────────┘         │                        │
│              └─────────────────────┘                        │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### How It Works

**Normal Operation:**
```
User Request
→ Load Balancer
→ Routes to EC2 in AZ1 or AZ2 (health check)
→ Both instances serving traffic
→ RDS Master in AZ1 (primary writes)
→ RDS Standby in AZ2 (synchronous replication)
→ Response time: Normal
```

**Scenario: AZ1 Power Failure**
```
Power failure at AZ1 (ap-south-1a)
├── EC2 in AZ1: DOWN ❌
├── RDS Master in AZ1: DOWN ❌
│
→ AWS detects master failure
→ Automatic failover triggered (60-120 seconds)
│
├── Load Balancer: Stops routing to AZ1
├── EC2 in AZ2: Takes 100% traffic ✅
├── RDS Standby in AZ2: Promoted to Master ✅
│
→ Application continues with minimal downtime
→ No manual intervention needed
→ Users see brief pause (1-2 seconds)
```

#### RDS Multi-AZ Benefits

| Aspect | Single AZ | Multi-AZ |
|---|---|---|
| **Availability** | 99.5% | 99.95% |
| **Data Durability** | Backup only | Continuous replication |
| **Failover** | Manual (30 min+) | Automatic (1-2 min) |
| **Downtime on failure** | 30+ minutes | 1-2 minutes |
| **Data loss** | Hours | None (synchronous) |
| **Cost** | $X | $X × 1.5-2 |
| **Read Scaling** | Not possible | Read replicas possible |

#### Cost of High Availability

**RDS Example (PostgreSQL db.t3.medium):**
```
Single AZ:
- Instance: $0.158/hour
- Storage: $0.10/GB/month (100 GB = $10)
- Total: ~$125/month

Multi-AZ:
- Primary instance: $0.158/hour
- Standby instance: $0.158/hour (no charge, just infrastructure)
- Storage: $0.10/GB/month × 2 AZs = $20
- Total: ~$250/month (2x)

Trade-off: 2x cost for 99.95% vs 99.5% availability
Downtime savings: (0.05% - 0.05%) × 730 hours = 0 hours (Multi-AZ achieves 99.95%)
```

#### When to Use Multi-AZ

| Requirement | Use Multi-AZ |
|---|---|
| Production applications | ✅ YES (highly recommended) |
| Development/Testing | ❌ NO (costs too high) |
| Customer-facing APIs | ✅ YES (any downtime = lost revenue) |
| Batch processing | ⚠️ Optional (depends on SLA) |
| Microservices | ✅ YES (fault tolerance required) |

---

### Disaster Recovery and Multi-Region

#### The Limits of Multi-AZ

**Multi-AZ covers:** Datacenter failure within a region  
**Multi-AZ doesn't cover:** Regional failure (earthquakes, major outages, regional disasters)

**Scenario: What if entire Region fails?**
```
Flood in Mumbai datacenter (ap-south-1)
→ All AZ1, AZ2, AZ3 affected
→ Region completely offline
→ Multi-AZ doesn't help

Multi-Region solution needed
```

#### Multi-Region Disaster Recovery

**DR Architecture:**
```
┌─────────────────────────────────────────────────────────────┐
│                                                              │
│  Primary Region: ap-south-1 (Active)                        │
│  ├── EC2 instances (Spring Boot)                            │
│  ├── RDS Master (PostgreSQL)                                │
│  ├── S3 bucket                                              │
│  └── Application serving users                              │
│                                                              │
│     ▼ Replication (every minute)                            │
│                                                              │
│  DR Region: ap-southeast-1 (Standby)                        │
│  ├── EC2 instances (stopped, ready)                         │
│  ├── RDS Standby (read-only replica)                        │
│  ├── S3 bucket (cross-region replica)                       │
│  └── Ready to activate (manual or automatic)                │
│                                                              │
│  Scenario: Primary region fails                             │
│  └─→ Activate DR region                                     │
│      └─→ Start EC2 instances                                │
│      └─→ Promote RDS standby to master                      │
│      └─→ Update Route 53 DNS to DR region                   │
│      └─→ Users redirected to DR (failover complete)         │
└─────────────────────────────────────────────────────────────┘
```

#### DR Strategies

| Strategy | Recovery Time | Data Loss | Cost | Use Case |
|---|---|---|---|---|
| **Backup & Restore** | Hours | Hours | $ | Non-critical systems |
| **Pilot Light** | 15-30 min | Minimal | $$ | Important systems |
| **Warm Standby** | Minutes | Minimal | $$$ | Critical systems |
| **Hot Standby** | Seconds | None | $$$$ | Mission-critical |

**Your Defect Management System:**
```
RTO (Recovery Time Objective): 5 minutes
RPO (Recovery Point Objective): 1 minute (max data loss acceptable)
→ Use Warm Standby or Hot Standby in ap-southeast-1
```

#### Cross-Region Replication

**How data travels between regions:**
```
Primary Region (ap-south-1)
  │
  └─→ S3 Cross-Region Replication
        └─→ Data synced to ap-southeast-1 S3 bucket
        └─→ Takes seconds to minutes
        └─→ Cost: $0.02 per GB transferred
  
  └─→ RDS Read Replica (Cross-Region)
        └─→ Asynchronous replication
        └─→ Network latency: 100+ ms
        └─→ Cost: Extra instance + data transfer
  
  └─→ Route 53 (DNS)
        └─→ Global service (no region)
        └─→ Automatically routes users to healthy region
        └─→ Health checks: Every 10 seconds
```

---

### Regional Services vs Global Services

#### Global Services (No Region Selection)

These services are NOT region-specific:

| Service | Why Global | Use Case |
|---|---|---|
| **IAM** | User/role management across all regions | Authentication, permissions |
| **CloudFront** | CDN with edge locations worldwide | Content delivery, caching |
| **Route 53** | DNS service, routes users globally | Domain registration, DNS failover |
| **S3** | Bucket names globally unique | Storage (with cross-region replication) |
| **CloudTrail** | Audit trail across all regions | Compliance, security audits |
| **AWS Organizations** | Manage multiple AWS accounts | Enterprise account management |

**Example: IAM is Global**
```
Create user "prakash" in us-east-1 console
→ User is accessible in all regions
→ Create key in ap-south-1 console
→ Same key works everywhere
```

#### Regional Services (Must Choose Region)

These services ARE region-specific:

| Service | Region-Specific | Why |
|---|---|---|
| **EC2** | Yes | Instances run in specific AZ/Region |
| **RDS** | Yes | Database cluster in specific region |
| **S3 Buckets** | Partly | Name is global, data stored in region |
| **Lambda** | Yes | Function deployed to specific region |
| **ECS** | Yes | Cluster created in specific region |
| **ElastiCache** | Yes | Cluster in specific region |
| **DynamoDB** | Partly | Table in region (can replicate globally) |

**Example: EC2 is Regional**
```
Launch EC2 in us-east-1
→ Instance only in us-east-1
→ Cannot access instance from ap-south-1 console directly
→ Must explicitly create another instance in ap-south-1
```

#### Decision Tree: Global vs Regional

```
Service Decision:

IAM (global)
├── Create users/roles once
└── Works everywhere

EC2 (regional)
├── Create in ap-south-1
└── Also create in ap-southeast-1 for DR

S3 (bucket global, data regional)
├── Bucket name unique globally
├── Create in ap-south-1
└── Enable cross-region replication to ap-southeast-1

CloudFront (global CDN)
├── No region selection
├── Distributes content from edge locations
└── Caches from your origin (any region)
```

---

## AWS Compute Services

> **Compute** = Running applications and processing workloads on AWS

### EC2 (Elastic Compute Cloud)

#### What is EC2?

**Simple:** Virtual servers that you can launch and manage like physical servers.

**Your Experience Bridge:** EC2 is what you manage in OpenManage Enterprise — but virtual.

#### EC2 Key Components

##### 1. **Instance Type** (Like server SKU)

**Format:** `t3.medium` = `[family].[size]`

| Family | Use Case | Cost | Your Use |
|---|---|---|---|
| **t3** (Burstable) | Web apps, testing, light workloads | $ (Cheapest) | Dev/Test environments |
| **m5** (General Purpose) | Balanced CPU/Memory/Network | $$ | Production web servers |
| **c5** (Compute Optimized) | High CPU needs | $$$ | Batch processing, analytics |
| **r5** (Memory Optimized) | High RAM needs | $$$ | In-memory databases (Redis) |
| **p3** (GPU) | Machine learning, AI | $$$$ | AI training |

**Size progression:** `nano` < `micro` < `small` < `medium` < `large` < `xlarge` < `2xlarge` < `4xlarge`

**Example Decision Tree:**
```
Your Java Spring Boot app needs:
- 4 CPU cores, 8GB RAM, moderate traffic
→ t3.large or m5.large
→ Cost: ~$0.10/hour ($72/month)
```

##### 2. **AMI** (Amazon Machine Image)

**What:** Pre-configured OS + software bundle ready to launch.

**Examples:**
- Amazon Linux 2 (AWS-optimized, free)
- Ubuntu (community-supported)
- Windows Server (paid)
- Custom AMI (you create one and save it)

**Your Analogy:** Like a Dell server OS image you boot from.

##### 3. **Storage Options** (Root Volume)

| Storage | Use Case | Cost | Speed |
|---|---|---|---|
| **EBS gp3** (General Purpose) | Web servers, databases | $ | Medium (3,000-16,000 IOPS) |
| **EBS io1** (Provisioned IOPS) | High-performance databases | $$$ | High (up to 64,000 IOPS) |
| **Instance Store** | Temporary, fast cache | $ | Ultra-fast (ephemeral) |
| **EFS** (Elastic File System) | Shared across instances | $$ | Network storage |

**Key Point:** EBS volume persists even if instance stops. Instance Store data is lost.

##### 4. **Security Groups** (Firewall Rules)

**What:** Controls inbound/outbound traffic.

**Example:**
```
Port 80 (HTTP): Allow from 0.0.0.0/0 (anyone)
Port 443 (HTTPS): Allow from 0.0.0.0/0 (anyone)
Port 3306 (MySQL): Allow from 10.0.0.0/8 (internal only)
Port 22 (SSH): Allow from YOUR_IP (admin only)
```

##### 5. **Key Pair** (SSH Access)

**What:** Private/public key pair for secure login.

**Process:**
1. Create key pair (AWS generates `.pem` file)
2. Download & secure the private key
3. Use private key to SSH into instance
```bash
ssh -i my-key.pem ec2-user@54.123.45.67
```

#### EC2 Instance Lifecycle

```
┌─────────────┐
│  Pending    │ (Instance starting, 1-2 min)
└──────┬──────┘
       ↓
┌─────────────┐
│  Running    │ ← You're charged per hour here
└──────┬──────┘
       ↓
   [Stop]  [Terminate]
    │        │
    ↓        ↓
┌───────┐  ┌──────────┐
│Stopped│  │Terminated│ (Can't restart, data gone)
└───────┘  └──────────┘
  (Can restart later, EBS data preserved)
```

**Cost Implications:**
- Running: You pay per hour
- Stopped: You pay for EBS storage only (not compute)
- Terminated: You pay nothing, but data is gone

#### EC2 Pricing Models

| Model | Cost | Best For |
|---|---|---|
| **On-Demand** | Pay per hour | Variable workloads, testing |
| **Reserved Instance (1yr/3yr)** | 40-60% discount | Stable, predictable workloads |
| **Spot** | 70-90% discount | Batch jobs, fault-tolerant apps |
| **Savings Plan** | 30-50% discount | Flexible, any instance type |

**Example Calculation:**
```
t3.medium instance (On-Demand)
- Cost: $0.0416/hour
- Monthly (730 hours): $30.37
- Yearly: $364

Reserved Instance (1-year, all upfront)
- Cost: $165/year
- Savings: $199/year (55% discount)
```

#### EC2 Use Cases (Exam Focus)

| Use Case | Instance Type | Why |
|---|---|---|
| Web servers, APIs | t3/m5 | Balanced, cost-effective |
| Databases | r5 (memory) or i3 (storage) | High RAM, fast disk |
| Batch processing | c5 (compute) | CPU-intensive |
| Machine learning | p3 (GPU) | GPU acceleration |
| Development/Testing | t3.micro | Cheapest, eligible for free tier |

#### EC2 + Your Microservices Architecture

**Before (Dell OpenManage):**
```
Physical Servers (100,000+)
├── Server 1: Java Spring Boot app
├── Server 2: Kafka broker
├── Server 3: Database
└── ... (Manual scaling)
```

**After (AWS EC2):**
```
Auto Scaling Group
├── 3 EC2 instances (m5.large): Java Spring Boot app
├── 2 EC2 instances (r5.2xlarge): Kafka brokers + ZooKeeper
├── RDS (Managed Database)
└── Load Balancer (auto-routes traffic)
→ Automatic scaling based on CPU usage
```

**Key Benefits:**
- Horizontal scaling (add instances) instead of vertical (add RAM)
- Pay only for what you use
- Auto-healing (unhealthy instances replaced automatically)

---

### Lambda (Serverless Compute)

#### What is Lambda?

**Simple:** Run code without managing servers. Upload code, AWS handles scaling, servers, patching.

**Your Perspective:** "What if you didn't need to provision EC2 instances for simple tasks?"

#### Lambda Key Concepts

##### 1. **Function**

**What:** Container with your code + runtime.

**Supported Runtimes:**
- Python 3.11
- Java 17
- Node.js 18+
- Go 1.x
- .NET 6+
- Custom runtime

**Example Function (Java):**
```java
public class MyLambdaHandler {
    public ApiGatewayResponse handleRequest(Map<String, Object> input) {
        String message = "Hello from Lambda";
        return new ApiGatewayResponse(200, message);
    }
}
```

##### 2. **Trigger**

**What:** Event that invokes (runs) your Lambda function.

| Trigger | Example | Use Case |
|---|---|---|
| **API Gateway** | HTTP request `/get-user` | REST API backend |
| **S3** | File uploaded to bucket | Image processing, log analysis |
| **DynamoDB Streams** | New item in table | Real-time notifications |
| **SNS/SQS** | Message published | Async processing |
| **CloudWatch Events** | Every 5 minutes | Scheduled tasks |
| **Kinesis** | Stream event | Real-time data processing |

##### 3. **Execution Model**

**On-Demand (Serverless):**
```
Request → AWS launches container
         → Runs your code
         → Returns response
         → Container stops
         → You pay for execution time only ($0.0000002/ms)
```

**Key Difference from EC2:**
- EC2: Instance runs 24/7, you pay whether it's used or not
- Lambda: Function runs only when triggered, pay for actual execution

##### 4. **Concurrency** (How many can run simultaneously)

- **Default:** 1,000 concurrent functions
- **Limit:** Can be increased or reserved
- **Cold Start:** First request takes longer (100-300ms) because container needs to start

##### 5. **Timeout & Memory**

- **Memory:** 128 MB - 10 GB (CPU scales with memory)
- **Timeout:** 1 second - 15 minutes
- **Pricing:** Based on GB-seconds

**Cost Example:**
```
Function: 256 MB, runs for 2 seconds
Cost: 256MB × 2s = 512 MB-seconds
     = 0.512 GB-seconds × $0.0000166667 = $0.0000085

1 million invocations/month = $0.20 + execution costs
```

#### Lambda Use Cases

| Scenario | Solution | Why Lambda |
|---|---|---|
| Process images uploaded to S3 | Lambda + S3 trigger | No server to manage, auto-scaling |
| Scheduled daily report generation | Lambda + CloudWatch Events | Runs once/day, costs ~$0.01/month |
| Mobile app backend API | Lambda + API Gateway | Pay only for requests, scales to millions |
| Process Kafka messages | Lambda + self-managed trigger | Complex; usually use EC2 for Kafka |
| Real-time log analysis | Lambda + CloudWatch Logs | Automatic invocation on new logs |
| Data transformation in pipeline | Lambda | Lightweight, efficient |

#### Lambda vs EC2 Decision

| Factor | Lambda | EC2 |
|---|---|---|
| **Startup time** | 1-2 seconds | Minutes |
| **Execution duration** | Max 15 minutes | Unlimited |
| **Predictable workload** | ❌ Expensive | ✅ Cheap |
| **Spiky workload** | ✅ Cost-effective | ❌ Wasted capacity |
| **Real-time processing** | ✅ Good | ✅ Better |
| **Kafka consumer** | ⚠️ Possible but inefficient | ✅ Standard |
| **Background task** | ✅ Perfect | ❌ Overkill |
| **Server management** | ✅ None needed | ❌ You manage |

#### Lambda in Your Microservices

**Use Case: Defect Management System (Your Hackathon Project)**

**Before (on EC2):**
```
3 EC2 instances running 24/7
- Each instance: $0.10/hour
- Monthly cost: $216
- Utilization: 10% (mostly idle between defect notifications)
- Wasted money: ~$194/month
```

**After (with Lambda):**
```
Lambda function triggered by SNS (new defect)
- Process: Analyze logs, generate fix
- Execution: 30 seconds, 256 MB
- Monthly invocations: 500 (5/day average)
- Cost: (500 × 2s × 256MB) = 0.256 GB-seconds = $0.004
- Savings: $211.99/month
```

---

### Elastic Beanstalk

#### What is Elastic Beanstalk?

**Simple:** Platform that deploys, manages, and scales web applications automatically.

**Your Perspective:** "Like deploying to Tomcat, but AWS manages the infrastructure for you."

#### Key Concepts

##### 1. **Environment**

Two types:
- **Web Server Environment** → Apache/Nginx → Your app
- **Worker Environment** → No web server → Background jobs

##### 2. **Platform**

Pre-configured stacks:
- Java SE
- Python
- Node.js
- Go
- .NET
- Ruby

##### 3. **Deployment**

**Process:**
1. Create `.ebextensions/` config folder
2. Upload code (ZIP or Git)
3. Elastic Beanstalk provisions EC2 + Load Balancer
4. Monitors and auto-scales

##### 4. **Auto Scaling Configuration**

```
Min instances: 2
Max instances: 10
Trigger metric: CPU > 70%
Cooldown: 5 minutes
→ When CPU stays above 70% for 5 min, add instance
```

#### Elastic Beanstalk Use Cases

| Use Case | Why |
|---|---|
| Deploy Spring Boot REST API | No need to manage EC2, LB, auto-scaling manually |
| Multi-tier web app | Separates web tier and worker tier |
| Rapid application deployment | Focus on code, not infrastructure |
| Environment promotion | Dev → Staging → Production |

#### Elastic Beanstalk vs Lambda vs EC2

```
Complexity to Manage: EC2 > Elastic Beanstalk > Lambda
Control: EC2 > Elastic Beanstalk > Lambda
Cost (predictable): EC2 < Elastic Beanstalk ≈ Lambda
Cost (unpredictable): Lambda << Elastic Beanstalk < EC2
```

---

### ECS & EKS (Container Services)

#### What are Containers?

**Simple:** Lightweight virtualization. Package app + dependencies + runtime into one unit.

**Your Knowledge Bridge:** Like Docker containers you may have used.

**Container vs VM:**
```
VM (EC2):
┌─────────────────────────┐
│     Hypervisor          │
├─────────────────────────┤
│  Guest OS (Linux)       │ ← 1-2 GB, boots in seconds
│  Application            │
└─────────────────────────┘

Container (Docker):
┌─────────────────────────┐
│     Docker Engine       │
├─────────────────────────┤
│  Application + libs     │ ← 100-500 MB, milliseconds
└─────────────────────────┘
```

#### ECS (Elastic Container Service)

##### What is ECS?

**AWS-native container orchestration platform.**

**Analogy:** Like Kubernetes but AWS-built, simpler, tighter AWS integration.

##### ECS Launch Types

| Type | What Manages Servers | Cost |
|---|---|---|
| **EC2** | You provision EC2 instances, ECS schedules containers | You pay for EC2 |
| **Fargate** | AWS manages servers completely | Serverless, pay per container |

##### ECS Components

```
ECS Cluster (Virtual container environment)
├── Task Definition (Blueprint: image, memory, CPU, ports)
│   └── Example: "MySpringBootApp:v1.0"
│       - Image: myrepo/app:v1.0
│       - Memory: 512 MB
│       - CPU: 256
│       - Port mapping: 8080→80
├── Service (Runs multiple copies, manages scaling)
│   └── Example: "SpringBootService"
│       - Desired count: 3 (3 copies running)
│       - Load Balancer: Yes
│       - Auto-scaling: CPU > 70%
└── Task (Running instance)
    ├── Task 1 (Container running)
    ├── Task 2 (Container running)
    └── Task 3 (Container running)
```

##### ECS on Fargate (Serverless Containers)

**Pricing Model:**
```
Cost = vCPU/hour + GB-hours
Example: 0.5 vCPU + 1 GB RAM
- Running 3 copies 24/7
- Cost: (0.5 × $0.05 + 1 × $0.005) × 24 × 30 = $54/month
```

#### EKS (Elastic Kubernetes Service)

##### What is EKS?

**AWS-managed Kubernetes cluster.**

**Your Perspective:** Kubernetes is industry-standard container orchestration. EKS = AWS manages control plane.

##### EKS vs ECS

| Feature | ECS | EKS |
|---|---|---|
| **Complexity** | Simpler | More complex |
| **Learning curve** | Moderate | Steep |
| **Portability** | AWS-locked | Works anywhere |
| **Ecosystem** | Growing | Mature, large community |
| **Use at exam** | Mention ECS for simplicity | Mention EKS if portability needed |

##### When to Use EKS vs ECS

```
Use ECS if:
- You want simpler setup
- You're all-in on AWS
- Your team knows AWS better

Use EKS if:
- You want multi-cloud portability
- Your team knows Kubernetes
- You need Helm charts, operators
```

#### Containers at Scale: Your Defect Management System

**Without Containers:**
```
5 EC2 instances
- Each runs full application + dependencies
- Updating: Restart each instance, downtime possible
- Scaling: Manual or slow auto-scaling
- Cost: $0.50/hour ($360/month)
```

**With Containers (ECS Fargate):**
```
10 container tasks (smaller, efficient)
- Each task: 0.25 vCPU + 512 MB RAM
- Updating: New container version, zero downtime rolling update
- Scaling: Per-request auto-scaling
- Cost: $0.12/hour ($86/month)
```

---

### Elasticsearch (Search & Analytics)

#### What is Elasticsearch?

**Simple:** Search and analytics engine for logs, metrics, and data.

**Your Use Case Bridge:** Log aggregation and analysis from your 100,000+ servers.

#### Key Concepts

##### 1. **Index**

**What:** Collection of documents (like database table).

**Example:**
```
Index: "server-logs-2024-01"
├── Document 1: { server: "oms-001", error: "OutOfMemory", timestamp: "2024-01-15T10:30:00Z" }
├── Document 2: { server: "oms-002", error: "Timeout", timestamp: "2024-01-15T10:31:00Z" }
└── Document 3: { server: "oms-001", error: "Disk full", timestamp: "2024-01-15T10:32:00Z" }
```

##### 2. **Shard**

**What:** Partition of index data across nodes for parallel processing.

**Concept:**
```
If index has 1 million documents:
- 1 shard: 1 node processes all
- 5 shards: 5 nodes process 200K each → 5x faster search
```

##### 3. **Query Types**

**Full-Text Search:**
```
Query: "OutOfMemory error"
Results: All documents containing both words in any field
```

**Structured Query:**
```
Query: { 
  range: { timestamp: { gte: "2024-01-15", lte: "2024-01-16" } },
  term: { severity: "critical" }
}
Results: Documents in date range with critical severity
```

#### Amazon Elasticsearch Service (Now called OpenSearch)

**What AWS Offers:**
- Managed Elasticsearch/OpenSearch cluster
- Automatic backups, patching, scaling
- VPC integration (private network)
- Security: Encryption, IAM, IP whitelist

**Pricing:**
```
Example: 3-node cluster
- Node type: t3.small
- Storage: 100 GB
- Cost: ~$180/month + $1/GB beyond free tier
```

#### Use Cases at Your Scale

| Use Case | Benefit |
|---|---|
| **Centralized Log Aggregation** | Collect logs from 100K servers, search in seconds |
| **Real-time Monitoring** | Dashboard of system health, anomalies |
| **Audit Trails** | Compliance logging with full-text search |
| **Application Performance** | Track response times, errors, bottlenecks |
| **Security Analysis** | Hunt through security logs for threats |

#### Elasticsearch in Your Architecture

**Current (OpenManage Enterprise):**
```
Individual server logs
→ SSH to each server
→ Read local log files
→ Manual analysis
→ Time: Hours to find patterns
```

**With Elasticsearch:**
```
All server logs → Logstash/Filebeat → Elasticsearch → Kibana Dashboard
→ Search millions of logs in milliseconds
→ Visualize patterns automatically
→ Alerting on anomalies
→ Time: Minutes to root cause issues
```

---

### Auto Scaling

#### What is Auto Scaling?

**Simple:** Automatically increase/decrease number of instances based on demand.

**Your Need:** Your Spring Boot app traffic spikes 10x during defect alerts. Auto Scaling adds instances automatically.

#### Auto Scaling Components

##### 1. **Launch Configuration** (or Template)

**What:** Blueprint for launching instances.

```
Launch Configuration: "SpringBootApp-v1"
- AMI ID: ami-0c55b159cbfafe1f0 (Ubuntu 20.04)
- Instance Type: t3.medium
- Security Group: web-server-sg
- Key Pair: myapp-key
- EBS Volume: 20 GB
- User Data: #!/bin/bash
            apt-get install java
            systemctl start myapp
```

##### 2. **Auto Scaling Group**

**What:** Manages instances using the launch config + scaling policies.

```
Auto Scaling Group: "SpringBootApp-ASG"
- Min Size: 2 (always 2 instances running)
- Max Size: 10 (never more than 10)
- Desired Capacity: 3 (current target)
- Health Check: ELB (stop unhealthy instances)
- Scaling Policies: 
  - Scale UP: CPU > 70% for 2 minutes
  - Scale DOWN: CPU < 30% for 5 minutes
```

##### 3. **Scaling Policies**

**Target Tracking:** Maintain CPU at 70%
```
Actual CPU: 85%
→ Too high, add instances
→ Now running 5 instances (was 3)
→ CPU drops to 70% (target)
```

**Step Scaling:** Add instances based on metric bands
```
CPU: 70-80% → Add 1 instance
CPU: 80-90% → Add 2 instances
CPU: >90%   → Add 3 instances
```

**Scheduled Scaling:** Scale at specific times
```
Monday-Friday 9 AM: Desired = 10 (workday traffic)
Monday-Friday 9 PM: Desired = 2 (low traffic)
```

#### Auto Scaling Workflow

```
1. Load Balancer receives request
2. Distributes to available instances
3. Monitor CPU/memory/custom metrics
4. If CPU > 70%:
   - Launch new instance (2-3 minutes)
   - Register with Load Balancer
   - Start receiving traffic
5. If CPU < 30%:
   - Wait 5 minutes (cooldown)
   - Terminate instance
   - Adjust load balancer
```

#### Cost Impact of Auto Scaling

**Without Auto Scaling:**
```
Peak traffic: 1,000 requests/sec
- Need 10 instances to handle peak
- Cost 24/7: 10 × $0.05/hour = $0.50/hour = $360/month
- Average traffic: 100 requests/sec (10% utilization)
- Wasted: 9 instances idle = $324/month wasted
```

**With Auto Scaling:**
```
Peak traffic: 1,000 requests/sec → 10 instances
Off-peak: 100 requests/sec → 1 instance
- Average over month: 3 instances = $0.15/hour = $108/month
- Savings: $252/month (70% reduction!)
```

#### Auto Scaling + Your Microservices

**Scenario: Defect Management System**

```
Kafka triggers defect analysis
→ Lambda (fast) or EC2 consumer (sustained)
→ Database queries increase
→ RDS read replicas auto-scale
→ Elasticsearch cluster scales
→ S3 file uploads increase
→ Auto Scaling Group adds EC2 instances
→ All happens automatically
→ Return to baseline when load decreases
→ You only pay for what you use
```

---

## Key Concepts Mapping

### Your Microservices Knowledge → AWS Services

| Your Concept | AWS Equivalent | Notes |
|---|---|---|
| **Physical Servers** | EC2 Instances | Virtual servers with same capabilities |
| **Server Orchestration** | Auto Scaling Groups | Automatic scaling based on demand |
| **Message Broker (Kafka)** | SQS/SNS/Kinesis | AWS messaging services |
| **Database Server** | RDS / DynamoDB | Managed databases |
| **Load Balancer** | Application Load Balancer (ALB) | Distributes traffic |
| **Log Aggregation** | CloudWatch Logs / Elasticsearch | Centralized logging |
| **Deployment** | CodePipeline / Elastic Beanstalk | Automated deployment |
| **Container Orchestration** | ECS/EKS | Manage containerized apps |
| **Monitoring** | CloudWatch | Metrics, alarms, dashboards |
| **Security** | Security Groups / IAM | Network & access control |

### Architecture Migration Example

**Before: On-Premise (Your Current Dell Setup)**
```
┌──────────────────────────────────────────┐
│         Dell Datacenter                  │
├──────────────────────────────────────────┤
│ ┌──────────────┐                        │
│ │ 3 Web Servers│ (Spring Boot)          │
│ └──────┬───────┘                        │
│        │                                 │
│ ┌──────▼──────────────┐                 │
│ │ Load Balancer (F5)  │                 │
│ └──────┬──────────────┘                 │
│        │                                 │
│ ┌──────▼──────────────┐                 │
│ │ 2 Kafka Brokers    │                 │
│ └──────┬──────────────┘                 │
│        │                                 │
│ ┌──────▼──────────────┐                 │
│ │ PostgreSQL Database │                 │
│ └─────────────────────┘                 │
│                                          │
│ Manual scaling, patching, backups        │
│ CapEx + OpEx: $500K+/year               │
└──────────────────────────────────────────┘
```

**After: AWS Cloud**
```
┌──────────────────────────────────────────┐
│         AWS VPC (Virtual Network)        │
├──────────────────────────────────────────┤
│ ┌─────────────────────────────────────┐ │
│ │ Auto Scaling Group (Spring Boot)    │ │
│ │ • Min: 1, Max: 5, Desired: 2        │ │
│ │ • Automatically scales based on CPU  │ │
│ └──────────────┬──────────────────────┘ │
│                │                         │
│ ┌──────────────▼──────────────┐         │
│ │ Application Load Balancer   │         │
│ │ • Auto health checks         │         │
│ │ • Zero downtime deployments  │         │
│ └──────────────┬──────────────┘         │
│                │                         │
│ ┌──────────────▼──────────────┐         │
│ │ Managed Kafka (MSK)          │         │
│ │ • AWS manages patches, backups│        │
│ └──────────────┬──────────────┘         │
│                │                         │
│ ┌──────────────▼──────────────┐         │
│ │ RDS PostgreSQL (Multi-AZ)   │         │
│ │ • Automatic failover         │         │
│ │ • Read replicas for scale    │         │
│ └─────────────────────────────┘         │
│                                          │
│ Pay-as-you-go, auto-scaling, no OpEx    │
│ Est. cost: $80K-120K/year               │
└──────────────────────────────────────────┘
```

---

## Exam Tips & Quick Reference

### EC2 Exam Focus Points

**High Probability Questions:**
- Instance types and sizing (t3 vs m5 vs c5)
- Security groups and network access
- Pricing models (on-demand, reserved, spot)
- Auto Scaling configuration
- EBS vs Instance Store differences
- High availability with multi-AZ

**Common Scenarios:**
- "How to make MySQL database highly available?" → RDS Multi-AZ
- "Application sees 100x traffic spike twice a year" → Reserved Instances (baseline) + Spot (burst)
- "Need 99.99% uptime" → Multi-AZ Auto Scaling + ELB
- "Development environment, cost matters" → t3.micro on-demand

### Lambda Exam Focus Points

**Key Concepts:**
- When Lambda is cost-effective (spiky, event-driven)
- When Lambda is NOT suitable (long-running, Kafka processing)
- Cold starts and impact
- Concurrency limits
- Integration with API Gateway, S3, SNS/SQS

**Decision Point:**
```
If question asks: "Real-time Kafka processing"
→ Answer: EC2 or ECS (not Lambda)

If question asks: "Process uploaded files"
→ Answer: Lambda with S3 trigger

If question asks: "Scheduled daily report"
→ Answer: Lambda with CloudWatch Events
```

### Container Services Exam Focus

**Hierarchy:**
```
Elastic Beanstalk (Easiest for web apps)
    ↓
ECS Fargate (Containers, AWS managed)
    ↓
ECS EC2 (More control, manage instances)
    ↓
EKS (Kubernetes, portability)
```

**When to choose each:**
- **Elastic Beanstalk:** "Deploy my app, don't worry about infrastructure"
- **ECS Fargate:** "I have containers, AWS manage infrastructure"
- **ECS EC2:** "I have containers, I'll manage EC2 instances"
- **EKS:** "I need Kubernetes features or multi-cloud"

### Elasticsearch Exam Focus

**Key Points:**
- Search vs analytics use case
- Full-text search capabilities
- Integration with Kinesis for real-time
- Cost considerations (shards, nodes, storage)
- NOT for transactional data (use RDS)

---

## Quick Decision Tree for Compute Service Selection

```
┌─ START ─────────────────────────────────────────┐
│ "What workload am I running?"                  │
└─┬──────────────────────────────────────────────┘
  │
  ├─→ Web application (Spring Boot, Node.js)
  │   └─→ "Do I want to manage infrastructure?"
  │       ├─ NO (easy, I want it now)  → Elastic Beanstalk
  │       └─ YES (I have control)      → EC2 + ALB + Auto Scaling
  │
  ├─→ Microservices / Containerized app
  │   └─→ "Do I need Kubernetes features?"
  │       ├─ NO, AWS is fine          → ECS Fargate (easiest)
  │       ├─ YES, manage EC2          → ECS EC2
  │       └─ YES, need portability    → EKS
  │
  ├─→ Background/scheduled job
  │   └─→ "Does it run <15 min?"
  │       ├─ YES                       → Lambda
  │       └─ NO                        → EC2 or ECS worker
  │
  ├─→ Real-time event processing
  │   └─→ "From Kafka/Kinesis?"
  │       ├─ YES                       → EC2 consumer or Lambda
  │       └─ NO                        → Lambda + API Gateway
  │
  └─→ Log search / analytics
      └─→ Elasticsearch / OpenSearch
```

---

## Practice Exam Questions

### Question 1: Instance Sizing
A company runs a web application with variable traffic. Peak traffic is 1,000 requests/second, occurring only 2-3 hours daily. Average traffic is 100 requests/second. Which approach optimizes cost?

**A)** 10 x m5.large instances running 24/7  
**B)** 2 x m5.large reserved instances + Auto Scaling to 10 instances during peak  
**C)** 10 x t3.medium spot instances  
**D)** 1 x m5.4xlarge instance  

**Answer:** B  
**Explanation:** Reserved instances for baseline (2 instances = $120/month), Auto Scaling for peak (8 additional on-demand = $40 during peak 2-3 hours = $10/month extra). Total: ~$130/month vs $400/month for option A.

### Question 2: Lambda vs EC2
A team needs to process 10,000 log files daily, each taking 30 seconds to analyze. Files are uploaded to S3. Which is most cost-effective?

**A)** EC2 instance running 24/7  
**B)** Lambda triggered by S3  
**C)** Kubernetes cluster  
**D)** Elasticsearch only  

**Answer:** B  
**Explanation:** 10,000 × 30 seconds = 300,000 seconds/day = ~10,000 seconds execution/day.  
- Lambda: 10,000 seconds × 256 MB × 0.0000166667 = ~$0.04/day = $1.20/month
- EC2: $36/month (running 24/7)
- Savings: 97%

### Question 3: Container Service Selection
A startup wants to deploy microservices. They have Dockerfiles but no Kubernetes experience. They want minimal operations overhead.

**A)** ECS EC2 (manage instances)  
**B)** ECS Fargate  
**C)** EKS  
**D)** Elastic Beanstalk  

**Answer:** B  
**Explanation:** ECS Fargate is "containers without managing servers" — perfect for their situation. Fargate handles infrastructure, they focus on containers.

---

## Summary Table: Compute Services Comparison

| Service | Best For | Complexity | Cost | Learning Curve |
|---|---|---|---|---|
| **EC2** | Full control, traditional servers | High | Low (pay per hour) | Medium |
| **Lambda** | Event-driven, short-running | Low | Very Low (per request) | Medium |
| **Elastic Beanstalk** | Quick web app deployment | Low | Medium | Low |
| **ECS Fargate** | Containers, minimal ops | Medium | Medium | Medium |
| **ECS EC2** | Containers, more control | High | Low-Medium | High |
| **EKS** | Kubernetes, portability | Very High | Medium-High | Very High |
| **Elasticsearch** | Search & analytics | High | High | High |

---

## Next Steps

1. **Week 1-2:** Master EC2 fundamentals (instances, security groups, pricing)
2. **Week 2-3:** Understand Lambda use cases and cost model
3. **Week 3-4:** Learn containers (ECS/Fargate vs EKS decision trees)
4. **Week 4-5:** Elasticsearch and search use cases
5. **Week 5-6:** Auto Scaling deep dive
6. **Week 6-7:** Practice exam questions (Tutorials Dojo, AWS Practice Exam)
7. **Week 7-8:** Review weak areas, take practice exams until 80%+ score

---

**Last Updated:** 2024-01-15  
**Exam Target:** AWS Solutions Architect - Associate (SAA-C03)  
**Study Duration:** 6-8 weeks
