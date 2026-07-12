# AWS Solutions Architect Certification - Study Guide

**Target Exam:** AWS Solutions Architect - Associate (SAA-C03)  
**Audience:** Cloud beginners with enterprise architecture background  
**Difficulty Level:** Beginner → Intermediate  
**Estimated Study Time:** 6-8 weeks

---

## 📑 Table of Contents (Index)

1. [Introduction to Cloud & AWS](#introduction-to-cloud--aws)
2. [AWS Fundamentals](#aws-fundamentals)
3. [AWS Compute Services](#aws-compute-services) ⭐ **FOCUS AREA**
   - [EC2 (Elastic Compute Cloud)](#ec2-elastic-compute-cloud)
   - [Lambda (Serverless Compute)](#lambda-serverless-compute)
   - [Elastic Beanstalk](#elastic-beanstalk)
   - [ECS & EKS (Container Services)](#ecs--eks-container-services)
   - [Elasticsearch (Search & Analytics)](#elasticsearch-search--analytics)
   - [Auto Scaling](#auto-scaling)
4. [Key Concepts Mapping](#key-concepts-mapping)
5. [Exam Tips & Quick Reference](#exam-tips--quick-reference)

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
