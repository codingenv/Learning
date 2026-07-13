# Kubernetes in the Cloud: From Basics to Interview-Ready
### A Complete Guide for Developers Preparing for Technical Interviews

---

## Table of Contents

### Part I — Foundations
1. [What is Kubernetes?](#chapter-1-what-is-kubernetes)
2. [Kubernetes vs Docker Swarm](#chapter-2-kubernetes-vs-docker-swarm)
3. [Kubernetes Architecture](#chapter-3-kubernetes-architecture)
4. [Control Plane Deep Dive](#chapter-4-control-plane-deep-dive)
5. [Worker Nodes Deep Dive](#chapter-5-worker-nodes-deep-dive)

### Part II — Core Objects
6. [Pods — The Atomic Unit](#chapter-6-pods-the-atomic-unit)
7. [ReplicaSets](#chapter-7-replicasets)
8. [Deployments](#chapter-8-deployments)
9. [StatefulSets](#chapter-9-statefulsets)
10. [DaemonSets and Jobs](#chapter-10-daemonsets-and-jobs)

### Part III — Networking
11. [Kubernetes Networking Model](#chapter-11-kubernetes-networking-model)
12. [Services](#chapter-12-services)
13. [Ingress and Ingress Controllers](#chapter-13-ingress-and-ingress-controllers)
14. [Network Policies](#chapter-14-network-policies)
15. [DNS in Kubernetes](#chapter-15-dns-in-kubernetes)

### Part IV — Storage
16. [Persistent Volumes and Claims](#chapter-16-persistent-volumes-and-claims)
17. [Storage Classes](#chapter-17-storage-classes)
18. [Cloud Storage Integration](#chapter-18-cloud-storage-integration)
19. [ConfigMaps and Secrets](#chapter-19-configmaps-and-secrets)

### Part V — Scheduling and Resources
20. [Resource Requests and Limits](#chapter-20-resource-requests-and-limits)
21. [Node Selectors and Affinity](#chapter-21-node-selectors-and-affinity)
22. [Taints and Tolerations](#chapter-22-taints-and-tolerations)
23. [Horizontal Pod Autoscaler](#chapter-23-horizontal-pod-autoscaler)
24. [Vertical Pod Autoscaler and KEDA](#chapter-24-vertical-pod-autoscaler-and-keda)

### Part VI — Cloud Kubernetes (Managed Services)
25. [Amazon EKS](#chapter-25-amazon-eks)
26. [Google GKE](#chapter-26-google-gke)
27. [Azure AKS](#chapter-27-azure-aks)
28. [Cloud Load Balancers and Ingress](#chapter-28-cloud-load-balancers-and-ingress)
29. [Managed Node Groups and Spot Instances](#chapter-29-managed-node-groups-and-spot-instances)
30. [Cloud IAM and Kubernetes RBAC](#chapter-30-cloud-iam-and-kubernetes-rbac)

### Part VII — Security
31. [RBAC — Role-Based Access Control](#chapter-31-rbac-role-based-access-control)
32. [Service Accounts](#chapter-32-service-accounts)
33. [Pod Security](#chapter-33-pod-security)
34. [Secrets Management in the Cloud](#chapter-34-secrets-management-in-the-cloud)
35. [Network Policies for Security](#chapter-35-network-policies-for-security)

### Part VIII — Operations
36. [Health Probes](#chapter-36-health-probes)
37. [Rolling Updates and Rollbacks](#chapter-37-rolling-updates-and-rollbacks)
38. [Namespaces and Multi-Tenancy](#chapter-38-namespaces-and-multi-tenancy)
39. [Monitoring and Observability](#chapter-39-monitoring-and-observability)
40. [Logging Architecture](#chapter-40-logging-architecture)

### Part IX — Advanced Topics
41. [Helm — Kubernetes Package Manager](#chapter-41-helm-kubernetes-package-manager)
42. [GitOps with ArgoCD / Flux](#chapter-42-gitops-with-argocd-flux)
43. [Service Mesh — Istio Basics](#chapter-43-service-mesh-istio-basics)

### Part X — Interview Preparation
44. [Top 60 Kubernetes Interview Questions](#chapter-44-top-60-kubernetes-interview-questions)
45. [Cloud-Specific Scenarios](#chapter-45-cloud-specific-scenarios)
46. [kubectl Quick Reference Cheat Sheet](#chapter-46-kubectl-quick-reference-cheat-sheet)

---

# PART I — FOUNDATIONS

---

## Chapter 1: What is Kubernetes?

Kubernetes (K8s) is an open-source **container orchestration platform** originally developed by Google, now maintained by the CNCF (Cloud Native Computing Foundation). It automates the deployment, scaling, and management of containerized applications across a cluster of machines.

### The Problem Kubernetes Solves

Docker solved the "works on my machine" problem. But running containers in production introduces a new set of challenges:

- How do you keep containers running if they crash?
- How do you scale from 2 to 200 instances?
- How do you roll out new versions without downtime?
- How do you distribute traffic across containers?
- How do you manage storage, secrets, and configuration?
- How do you run containers across multiple machines?

Kubernetes answers all of these.

### What Kubernetes Does

| Capability | Description |
|---|---|
| **Self-healing** | Restarts crashed containers, replaces failed nodes |
| **Auto-scaling** | Scales pods up/down based on CPU/memory/custom metrics |
| **Rolling updates** | Deploys new versions with zero downtime |
| **Load balancing** | Distributes traffic across pod instances |
| **Service discovery** | Pods find each other by service name via DNS |
| **Storage orchestration** | Automatically mounts cloud storage volumes |
| **Secret management** | Stores and injects secrets and config safely |
| **Bin packing** | Fits containers on nodes efficiently |

### The Name

"Kubernetes" is Greek for **helmsman** or **pilot** — the person who steers the ship. The logo is a ship's wheel with seven spokes (a reference to the "Borg" project at Google that inspired it). K8s is the abbreviation: K + 8 letters + s.

### Interview Tip
> Define Kubernetes as: *"An open-source container orchestration platform that automates deployment, scaling, and management of containerized applications. It ensures containers run reliably at scale across a cluster of machines."*

---

## Chapter 2: Kubernetes vs Docker Swarm

Both orchestrate containers but differ significantly in scope and capability.

| Feature | Docker Swarm | Kubernetes |
|---|---|---|
| Setup complexity | Simple | Complex |
| Learning curve | Low | High |
| Auto-scaling | No | Yes (HPA, VPA) |
| Rolling updates | Basic | Advanced (strategies, canary) |
| Self-healing | Basic | Advanced |
| Storage | Limited | Extensive (PV, PVC, StorageClass) |
| Networking | Simple overlay | CNI plugins, NetworkPolicy |
| RBAC | Basic | Full-featured |
| Cloud integration | Minimal | Deep (EKS, GKE, AKS) |
| Ecosystem | Small | Massive (Helm, Istio, ArgoCD) |
| Industry adoption | Declining | Dominant |

### Interview Tip
> Kubernetes is the clear industry winner. Swarm is simpler but lacks the features needed for large-scale production. Know the differences and state why you'd choose Kubernetes for anything beyond small/dev setups.

---

## Chapter 3: Kubernetes Architecture

A Kubernetes cluster has two types of nodes:

- **Control Plane (Master)** — manages the cluster state
- **Worker Nodes** — run the actual application containers

```
┌─────────────────────────────────────────────────────┐
│                   CONTROL PLANE                      │
│  ┌──────────┐  ┌──────────┐  ┌────────────────────┐│
│  │kube-      │  │etcd      │  │kube-controller-    ││
│  │apiserver  │  │(state DB)│  │manager             ││
│  └──────────┘  └──────────┘  └────────────────────┘│
│  ┌──────────┐  ┌──────────────────────────────────┐ │
│  │kube-     │  │cloud-controller-manager           │ │
│  │scheduler │  │(cloud provider integration)       │ │
│  └──────────┘  └──────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
         │                │               │
┌────────┴──┐    ┌────────┴──┐   ┌────────┴──┐
│ WORKER 1  │    │ WORKER 2  │   │ WORKER 3  │
│ kubelet   │    │ kubelet   │   │ kubelet   │
│ kube-proxy│    │ kube-proxy│   │ kube-proxy│
│ container │    │ container │   │ container │
│ runtime   │    │ runtime   │   │ runtime   │
│ [Pod][Pod]│    │ [Pod][Pod]│   │ [Pod]     │
└───────────┘    └───────────┘   └───────────┘
```

---

## Chapter 4: Control Plane Deep Dive

### kube-apiserver

The **API server** is the front door to the cluster. All operations go through it — kubectl, controllers, kubelets all talk to the API server. It validates and persists state to etcd.

```bash
# Everything goes through the API
kubectl get pods   # -> HTTP GET /api/v1/namespaces/default/pods
```

### etcd

A distributed key-value store that is the **single source of truth** for all cluster state. Every object (pods, deployments, services) is stored here. In production, etcd runs as a cluster (3 or 5 nodes) for high availability.

**Critical:** Back up etcd regularly. Losing etcd data means losing the entire cluster state.

```bash
# etcd backup (example)
etcdctl snapshot save backup.db \
  --endpoints=https://127.0.0.1:2379 \
  --cert=/etc/etcd/server.crt \
  --key=/etc/etcd/server.key
```

### kube-scheduler

Watches for new pods with no assigned node and selects the best node based on:
- Resource requirements (CPU, memory)
- Node affinity / anti-affinity rules
- Taints and tolerations
- Available capacity

### kube-controller-manager

Runs multiple controllers in a single process:
- **Node controller** — monitors node health
- **Deployment controller** — maintains desired replica count
- **Endpoints controller** — manages Service → Pod mappings
- **Namespace controller** — manages namespace lifecycle

### cloud-controller-manager

Integrates Kubernetes with the underlying cloud provider API. Handles:
- Provisioning cloud load balancers (when you create a Service of type LoadBalancer)
- Managing cloud storage volumes
- Node lifecycle in the cloud (adding/removing cloud instances)

---

## Chapter 5: Worker Nodes Deep Dive

### kubelet

An agent on every worker node. It:
- Registers the node with the API server
- Watches for pod specs assigned to its node
- Starts/stops containers via the container runtime
- Reports pod and node status back to the API server

### kube-proxy

Maintains network rules on each node. Implements Kubernetes Services by managing iptables or IPVS rules for routing traffic to the correct pods.

### Container Runtime

The software that actually runs containers. Kubernetes uses the **Container Runtime Interface (CRI)** to support multiple runtimes:
- **containerd** — most common (used by EKS, GKE, AKS)
- **CRI-O** — lightweight, designed for K8s
- **Docker** — deprecated as K8s runtime since v1.24

### Node Status

```bash
kubectl get nodes              # List all nodes
kubectl describe node <name>   # Detailed node info
kubectl get nodes -o wide      # Show IPs, OS, container runtime
```

---

# PART II — CORE OBJECTS

---

## Chapter 6: Pods — The Atomic Unit

A **Pod** is the smallest deployable unit in Kubernetes. A pod wraps one or more containers that share:
- The same network namespace (same IP address and port space)
- The same storage volumes
- The same lifecycle

### Why Not Run Containers Directly?

Kubernetes doesn't manage containers directly — it manages Pods. This allows for **sidecar patterns**: containers that work alongside the main container (log shippers, proxies, init containers).

### Pod YAML

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
  labels:
    app: myapp
    env: production
spec:
  containers:
  - name: app
    image: nginx:1.25
    ports:
    - containerPort: 80
    resources:
      requests:
        memory: "64Mi"
        cpu: "250m"
      limits:
        memory: "128Mi"
        cpu: "500m"
    env:
    - name: APP_ENV
      value: "production"
    readinessProbe:
      httpGet:
        path: /health
        port: 80
      initialDelaySeconds: 5
      periodSeconds: 10
```

### Pod Lifecycle Phases

| Phase | Meaning |
|---|---|
| `Pending` | Pod accepted by cluster, containers not yet running (being scheduled or pulled) |
| `Running` | At least one container is running |
| `Succeeded` | All containers exited with code 0 |
| `Failed` | At least one container exited with non-zero code |
| `Unknown` | Pod state cannot be determined (node communication issue) |

### Pod Commands

```bash
kubectl get pods                         # List pods
kubectl get pods -o wide                 # With node/IP info
kubectl describe pod <name>              # Detailed info + events
kubectl logs <pod>                       # Container logs
kubectl logs <pod> -c <container>        # Multi-container pod
kubectl logs -f <pod>                    # Follow logs
kubectl exec -it <pod> -- bash           # Shell into pod
kubectl exec -it <pod> -c <container> -- bash  # Specific container
kubectl delete pod <name>                # Delete pod
kubectl apply -f pod.yaml                # Create/update from file
```

### Init Containers

Init containers run **before** app containers, in sequence, and must complete successfully:

```yaml
spec:
  initContainers:
  - name: wait-for-db
    image: busybox
    command: ['sh', '-c', 'until nc -z db 5432; do sleep 2; done']
  containers:
  - name: app
    image: myapp
```

---

## Chapter 7: ReplicaSets

A **ReplicaSet** ensures a specified number of pod replicas are running at all times. If a pod dies, the ReplicaSet creates a replacement. If too many pods run, it removes extras.

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: myapp-rs
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: app
        image: nginx:1.25
```

### Interview Tip
> In practice, you rarely create ReplicaSets directly. You create **Deployments**, which manage ReplicaSets for you and add update/rollback capabilities.

---

## Chapter 8: Deployments

A **Deployment** is the standard way to run stateless applications. It manages ReplicaSets and provides:
- Declarative updates
- Rolling updates (zero downtime)
- Rollback to previous versions
- Scaling

### Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1    # Max pods unavailable during update
      maxSurge: 1          # Max extra pods during update
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: app
        image: myapp:v2.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: "100m"
            memory: "128Mi"
          limits:
            cpu: "500m"
            memory: "256Mi"
```

### Deployment Commands

```bash
kubectl create deployment myapp --image=nginx
kubectl apply -f deployment.yaml
kubectl get deployments
kubectl describe deployment myapp

# Scaling
kubectl scale deployment myapp --replicas=5

# Update image
kubectl set image deployment/myapp app=myapp:v2.0

# Rollout status
kubectl rollout status deployment/myapp

# Rollout history
kubectl rollout history deployment/myapp

# Rollback
kubectl rollout undo deployment/myapp
kubectl rollout undo deployment/myapp --to-revision=2
```

### Deployment Strategies

**RollingUpdate (default):** New pods created before old ones removed. Zero downtime but briefly runs two versions simultaneously.

**Recreate:** All old pods killed before new ones created. Causes downtime but ensures only one version runs at a time.

---

## Chapter 9: StatefulSets

**StatefulSets** are for applications that require:
- Stable, persistent storage per pod
- Stable network identity (predictable hostname)
- Ordered deployment and scaling

Use for: databases (PostgreSQL, MySQL, Cassandra), message queues (Kafka), distributed caches (Redis Cluster).

### StatefulSet Characteristics

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: "postgres"    # Headless service name
  replicas: 3
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:      # Each pod gets its own PVC
  - metadata:
      name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: gp2
      resources:
        requests:
          storage: 10Gi
```

### StatefulSet Pod Identity

Pods get **predictable names**: `postgres-0`, `postgres-1`, `postgres-2`

DNS entries (via headless service): `postgres-0.postgres.default.svc.cluster.local`

### StatefulSet vs Deployment

| Feature | Deployment | StatefulSet |
|---|---|---|
| Pod identity | Random hash | Stable, sequential |
| Storage | Shared or none | Individual PVC per pod |
| Scaling order | Any order | Ordered (0, 1, 2...) |
| Use for | Stateless apps | Databases, queues |

---

## Chapter 10: DaemonSets and Jobs

### DaemonSet

Ensures **one pod runs on every node** (or a subset of nodes). Used for:
- Log collectors (Fluentd, Filebeat)
- Monitoring agents (Prometheus node-exporter, Datadog agent)
- Network plugins
- Storage plugins

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: fluentd
spec:
  selector:
    matchLabels:
      app: fluentd
  template:
    metadata:
      labels:
        app: fluentd
    spec:
      tolerations:                 # Run on master nodes too
      - key: node-role.kubernetes.io/control-plane
        effect: NoSchedule
      containers:
      - name: fluentd
        image: fluent/fluentd:v1.16
        volumeMounts:
        - name: varlog
          mountPath: /var/log
      volumes:
      - name: varlog
        hostPath:
          path: /var/log
```

### Job

Runs a pod to **completion** (for batch processing):

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: data-migration
spec:
  completions: 1
  parallelism: 1
  backoffLimit: 3            # Retry 3 times on failure
  template:
    spec:
      restartPolicy: OnFailure
      containers:
      - name: migrate
        image: myapp:latest
        command: ["python", "migrate.py"]
```

### CronJob

Runs a Job on a schedule:

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cleanup
spec:
  schedule: "0 2 * * *"     # Every day at 2am (cron format)
  jobTemplate:
    spec:
      template:
        spec:
          restartPolicy: OnFailure
          containers:
          - name: cleanup
            image: myapp:latest
            command: ["python", "cleanup.py"]
```

---

# PART III — NETWORKING

---

## Chapter 11: Kubernetes Networking Model

Kubernetes networking has three fundamental rules:

1. **Every pod gets its own IP address**
2. **Pods on any node can communicate with any pod on any other node without NAT**
3. **Agents on a node can communicate with all pods on that node**

This flat network model is called the **IP-per-Pod** model.

### CNI (Container Network Interface)

CNI plugins implement the networking rules:

| Plugin | Description | Used by |
|---|---|---|
| **Calico** | Policy + routing (BGP or overlay) | Many cloud providers, self-hosted |
| **Flannel** | Simple overlay network | Lighter deployments |
| **Cilium** | eBPF-based, policy, observability | EKS, GKE (modern) |
| **Weave** | Encrypted mesh overlay | Self-hosted |
| **AWS VPC CNI** | Native AWS VPC networking | EKS |
| **Azure CNI** | Native Azure networking | AKS |

### Cloud CNI Plugins

**AWS VPC CNI:** Pods get real VPC IP addresses — they're directly routable within the VPC. Makes AWS network policies and security groups work at the pod level.

**Azure CNI:** Two modes — overlay (efficient IP usage) and flat (pods get VNet IPs). Azure Network Policy works natively.

---

## Chapter 12: Services

A **Service** provides a stable network endpoint (IP + DNS name) for a set of pods. Since pod IPs change when pods restart, Services provide a fixed address.

### Service Types

**ClusterIP (default)** — internal cluster IP, only reachable within the cluster:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: ClusterIP
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
```

**NodePort** — exposes service on each node's IP at a static port (30000-32767):
```yaml
spec:
  type: NodePort
  ports:
  - port: 80
    targetPort: 8080
    nodePort: 30080    # Optional; auto-assigned if omitted
```

**LoadBalancer** — provisions a cloud load balancer:
```yaml
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  # Cloud controller creates: AWS ALB/NLB, GCP LB, Azure LB
```

**ExternalName** — maps a service to an external DNS name:
```yaml
spec:
  type: ExternalName
  externalName: my-database.us-east-1.rds.amazonaws.com
```

**Headless Service** — no ClusterIP, used with StatefulSets for direct pod DNS:
```yaml
spec:
  clusterIP: None    # Headless
  selector:
    app: postgres
```

### Service Discovery

Every service gets a DNS name: `<service>.<namespace>.svc.cluster.local`

```bash
# Within the same namespace
curl http://my-service/api

# Cross-namespace
curl http://my-service.other-namespace.svc.cluster.local/api
```

### Endpoints

A Service creates **Endpoints** objects — the list of pod IPs that match the selector:

```bash
kubectl get endpoints my-service
# NAME         ENDPOINTS                                  AGE
# my-service   10.0.0.5:8080,10.0.0.6:8080,10.0.0.7:8080  5d
```

---

## Chapter 13: Ingress and Ingress Controllers

**Ingress** is an API object that manages external HTTP/HTTPS access to services within a cluster. It provides:
- URL path-based routing
- Host-based routing (virtual hosting)
- TLS termination
- Load balancing

An **Ingress Controller** is the component that implements the Ingress rules (nginx, Traefik, HAProxy, AWS ALB, GCP HTTPS LB).

### Ingress YAML

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - api.example.com
    secretName: tls-secret
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /users
        pathType: Prefix
        backend:
          service:
            name: user-service
            port:
              number: 80
      - path: /orders
        pathType: Prefix
        backend:
          service:
            name: order-service
            port:
              number: 80
```

### Cloud-Specific Ingress Controllers

**AWS ALB Ingress Controller (AWS Load Balancer Controller):**
```yaml
annotations:
  kubernetes.io/ingress.class: alb
  alb.ingress.kubernetes.io/scheme: internet-facing
  alb.ingress.kubernetes.io/target-type: ip
  alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:...
```

**GKE Ingress (Google Cloud Load Balancer):**
```yaml
annotations:
  kubernetes.io/ingress.class: gce
  kubernetes.io/ingress.global-static-ip-name: my-static-ip
```

---

## Chapter 14: Network Policies

**NetworkPolicy** controls which pods can talk to which pods (and external endpoints). By default, all pods can communicate with all other pods.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-frontend-to-backend
  namespace: production
spec:
  podSelector:
    matchLabels:
      tier: backend
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          tier: frontend
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          tier: database
    ports:
    - protocol: TCP
      port: 5432
```

### Default Deny All (Zero Trust Starting Point)

```yaml
# Deny all ingress to all pods in a namespace
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-ingress
spec:
  podSelector: {}    # Matches all pods
  policyTypes:
  - Ingress
```

---

## Chapter 15: DNS in Kubernetes

Kubernetes runs **CoreDNS** (in most modern clusters) as the cluster DNS server. Every pod is configured to use it.

### DNS Naming Convention

```
<service>.<namespace>.svc.<cluster-domain>
# Example:
my-service.default.svc.cluster.local

# Pod DNS (less common):
<pod-ip-dashes>.<namespace>.pod.cluster.local
# Example:
10-0-0-5.default.pod.cluster.local
```

### DNS Search Path

Within the same namespace, you can use just the service name:
```bash
# All three resolve to the same service
curl http://my-service
curl http://my-service.default
curl http://my-service.default.svc.cluster.local
```

---

# PART IV — STORAGE

---

## Chapter 16: Persistent Volumes and Claims

**The Storage Architecture:**
- **PersistentVolume (PV)** — a piece of storage provisioned by an admin or dynamically
- **PersistentVolumeClaim (PVC)** — a request for storage by a user
- **StorageClass** — defines how storage is provisioned dynamically

### PersistentVolume (Admin creates)

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 10Gi
  accessModes:
  - ReadWriteOnce     # RWO = single node read/write
  persistentVolumeReclaimPolicy: Retain  # Retain | Delete | Recycle
  storageClassName: gp2
  awsElasticBlockStore:  # AWS EBS example
    volumeID: vol-0123456789abcdef0
    fsType: ext4
```

### PersistentVolumeClaim (User creates)

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
  - ReadWriteOnce
  storageClassName: gp2
  resources:
    requests:
      storage: 10Gi
```

### Using PVC in a Pod

```yaml
spec:
  volumes:
  - name: storage
    persistentVolumeClaim:
      claimName: my-pvc
  containers:
  - name: app
    image: myapp
    volumeMounts:
    - name: storage
      mountPath: /app/data
```

### Access Modes

| Mode | Abbreviation | Description |
|---|---|---|
| ReadWriteOnce | RWO | Read/write by single node |
| ReadOnlyMany | ROX | Read-only by many nodes |
| ReadWriteMany | RWX | Read/write by many nodes |
| ReadWriteOncePod | RWOP | Read/write by single pod |

---

## Chapter 17: Storage Classes

**StorageClass** enables **dynamic provisioning** — Kubernetes automatically creates a PV when a PVC is created.

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  iopsPerGB: "10"
  encrypted: "true"
  kmsKeyId: arn:aws:kms:us-east-1:111122223333:key/1234
reclaimPolicy: Delete        # Delete PV when PVC deleted
allowVolumeExpansion: true   # Allow resizing
volumeBindingMode: WaitForFirstConsumer  # Don't bind until pod scheduled
```

### Cloud Storage Classes

**AWS EKS:**
```yaml
provisioner: ebs.csi.aws.com    # EBS CSI driver
parameters:
  type: gp3                     # gp2 | gp3 | io1 | io2 | st1 | sc1
  encrypted: "true"
```

**GKE:**
```yaml
provisioner: pd.csi.storage.gke.io
parameters:
  type: pd-ssd                  # pd-standard | pd-ssd | pd-balanced
```

**AKS:**
```yaml
provisioner: disk.csi.azure.com
parameters:
  skuName: Premium_LRS          # Standard_LRS | Premium_LRS | UltraSSD_LRS
```

---

## Chapter 18: Cloud Storage Integration

### AWS EKS Storage Options

| Storage Type | Use Case | K8s Integration |
|---|---|---|
| EBS (gp3/io2) | Block storage, databases | PVC with EBS CSI |
| EFS | Shared file storage (RWX) | PVC with EFS CSI |
| S3 | Object storage | Mountpoint for S3 CSI |
| FSx for Lustre | HPC workloads | PVC with FSx CSI |

```yaml
# EFS StorageClass (supports ReadWriteMany)
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: efs-sc
provisioner: efs.csi.aws.com
parameters:
  provisioningMode: efs-ap
  fileSystemId: fs-0123456789abcdef0
  directoryPerms: "700"
```

### GKE Storage Options

| Storage Type | Use Case | K8s Integration |
|---|---|---|
| Persistent Disk (pd-ssd) | Block storage | PVC with PD CSI |
| Filestore | NFS, shared storage (RWX) | PVC with Filestore CSI |
| Cloud Storage (GCS) | Object storage | GCS Fuse CSI |

### AKS Storage Options

| Storage Type | Use Case | K8s Integration |
|---|---|---|
| Azure Disk | Block storage | PVC with Azure Disk CSI |
| Azure Files | Shared storage (RWX) | PVC with Azure Files CSI |
| Azure Blob | Object storage | PVC with Blob CSI |

---

## Chapter 19: ConfigMaps and Secrets

### ConfigMap

Stores non-sensitive configuration data:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  database.url: "postgres://db:5432/myapp"
  log.level: "info"
  feature.flags: |
    dark_mode=true
    new_ui=false
```

**Using ConfigMap in a pod:**
```yaml
spec:
  containers:
  - name: app
    # As environment variables
    envFrom:
    - configMapRef:
        name: app-config
    # Or mount as files
    volumeMounts:
    - name: config
      mountPath: /app/config
  volumes:
  - name: config
    configMap:
      name: app-config
```

### Secret

Stores sensitive data (base64-encoded by default, not encrypted at rest unless you configure it):

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: cG9zdGdyZXM=      # base64: postgres
  password: c3VwZXJzZWNyZXQ=  # base64: supersecret
```

**Creating secrets:**
```bash
# From literal values (recommended)
kubectl create secret generic db-secret \
  --from-literal=username=postgres \
  --from-literal=password=supersecret

# From file
kubectl create secret generic tls-secret \
  --from-file=tls.crt=cert.pem \
  --from-file=tls.key=key.pem

# TLS secret type
kubectl create secret tls my-tls \
  --cert=cert.pem --key=key.pem
```

**Using Secrets:**
```yaml
spec:
  containers:
  - name: app
    env:
    - name: DB_PASSWORD
      valueFrom:
        secretKeyRef:
          name: db-secret
          key: password
```

### Interview Tip
> Default Kubernetes Secrets are **only base64-encoded**, not encrypted. In production, enable **encryption at rest** in etcd, or use external secret stores (AWS Secrets Manager via External Secrets Operator, HashiCorp Vault, etc.).

---

# PART V — SCHEDULING AND RESOURCES

---

## Chapter 20: Resource Requests and Limits

### Requests vs Limits

**Requests** — what the container is guaranteed (used for scheduling decisions).
**Limits** — the maximum the container can use (enforced at runtime).

```yaml
resources:
  requests:
    cpu: "250m"      # 250 millicores = 0.25 CPU cores
    memory: "256Mi"  # 256 mebibytes
  limits:
    cpu: "1000m"     # 1 CPU core
    memory: "512Mi"
```

### CPU Units

- `1` = 1 CPU core
- `1000m` = 1 CPU core
- `500m` = 0.5 CPU core
- `100m` = 0.1 CPU core (10% of one core)

CPU is **compressible** — exceeding the limit throttles (slows) the container.
Memory is **incompressible** — exceeding the limit kills the container (OOMKilled).

### Quality of Service Classes

Kubernetes assigns a QoS class based on resources:

| QoS Class | Condition | Eviction Priority |
|---|---|---|
| **Guaranteed** | requests == limits for all containers | Last to be evicted |
| **Burstable** | requests < limits, or some containers have no limits | Middle |
| **BestEffort** | No requests or limits set | First to be evicted |

### LimitRange

Sets default and maximum resource limits for a namespace:

```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: default-limits
spec:
  limits:
  - type: Container
    default:
      cpu: "500m"
      memory: "256Mi"
    defaultRequest:
      cpu: "100m"
      memory: "128Mi"
    max:
      cpu: "2"
      memory: "1Gi"
```

### ResourceQuota

Limits total resource consumption within a namespace:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: production-quota
spec:
  hard:
    pods: "50"
    requests.cpu: "20"
    requests.memory: 40Gi
    limits.cpu: "40"
    limits.memory: 80Gi
    persistentvolumeclaims: "20"
    services.loadbalancers: "5"
```

---

## Chapter 21: Node Selectors and Affinity

### nodeSelector (Simple)

```yaml
spec:
  nodeSelector:
    disktype: ssd          # Node must have this label
    kubernetes.io/arch: amd64
```

```bash
# Label a node
kubectl label node worker-1 disktype=ssd
```

### Node Affinity (Advanced)

```yaml
spec:
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:  # Hard rule
        nodeSelectorTerms:
        - matchExpressions:
          - key: topology.kubernetes.io/zone
            operator: In
            values:
            - us-east-1a
            - us-east-1b
      preferredDuringSchedulingIgnoredDuringExecution: # Soft rule
      - weight: 100
        preference:
          matchExpressions:
          - key: disktype
            operator: In
            values:
            - ssd
```

### Pod Anti-Affinity (Spread Pods Across Nodes)

```yaml
spec:
  affinity:
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchLabels:
            app: myapp
        topologyKey: kubernetes.io/hostname
        # No two myapp pods on the same node
```

### Topology Spread Constraints

More flexible than affinity for spreading pods across zones/nodes:

```yaml
spec:
  topologySpreadConstraints:
  - maxSkew: 1
    topologyKey: topology.kubernetes.io/zone
    whenUnsatisfiable: DoNotSchedule
    labelSelector:
      matchLabels:
        app: myapp
```

---

## Chapter 22: Taints and Tolerations

**Taints** are applied to nodes to **repel** pods. **Tolerations** are applied to pods to allow scheduling on tainted nodes.

### Taint Effects

| Effect | Behavior |
|---|---|
| `NoSchedule` | Pods without toleration won't be scheduled here |
| `PreferNoSchedule` | Try to avoid scheduling here |
| `NoExecute` | Evict existing pods without toleration; don't schedule new ones |

```bash
# Add a taint to a node
kubectl taint nodes node1 special=true:NoSchedule

# Remove a taint
kubectl taint nodes node1 special=true:NoSchedule-
```

### Toleration in Pod Spec

```yaml
spec:
  tolerations:
  - key: "special"
    operator: "Equal"
    value: "true"
    effect: "NoSchedule"
  # Or tolerate all taints on a node:
  - operator: "Exists"
    effect: "NoSchedule"
```

### Common Cloud Use Case: Dedicated Node Pools

```bash
# Taint GPU nodes so only GPU workloads run there
kubectl taint nodes gpu-node-1 gpu=true:NoSchedule

# GPU workload pod tolerates the taint
tolerations:
- key: "gpu"
  operator: "Equal"
  value: "true"
  effect: "NoSchedule"
nodeSelector:
  accelerator: nvidia-tesla-k80
```

---

## Chapter 23: Horizontal Pod Autoscaler

**HPA** automatically scales the number of pod replicas based on observed metrics.

### HPA Based on CPU

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  minReplicas: 2
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70   # Target 70% CPU
  - type: Resource
    resource:
      name: memory
      target:
        type: AverageValue
        averageValue: 200Mi
```

### HPA Based on Custom Metrics

```yaml
metrics:
- type: External
  external:
    metric:
      name: sqs_approximate_number_of_visible_messages
      selector:
        matchLabels:
          queue: order-processor
    target:
      type: AverageValue
      averageValue: 10    # Scale so each pod handles ~10 messages
```

### HPA Commands

```bash
kubectl get hpa
kubectl describe hpa myapp-hpa
kubectl autoscale deployment myapp --cpu-percent=70 --min=2 --max=10
```

### Requirements for HPA

- **Metrics Server** must be installed in the cluster
- Resource **requests must be set** on containers (HPA calculates utilization against requests)

---

## Chapter 24: Vertical Pod Autoscaler and KEDA

### Vertical Pod Autoscaler (VPA)

VPA automatically adjusts CPU and memory **requests** for pods based on historical usage. It doesn't scale replicas — it resizes individual pods.

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: myapp-vpa
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  updatePolicy:
    updateMode: "Auto"    # Auto | Recreate | Initial | Off
  resourcePolicy:
    containerPolicies:
    - containerName: app
      minAllowed:
        cpu: "100m"
        memory: "128Mi"
      maxAllowed:
        cpu: "2"
        memory: "2Gi"
```

### KEDA (Kubernetes Event-Driven Autoscaling)

KEDA extends Kubernetes with **event-driven** autoscaling — scale pods based on message queue depth, database query results, Prometheus metrics, etc.

```yaml
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: order-processor-scaler
spec:
  scaleTargetRef:
    name: order-processor
  minReplicaCount: 0          # Scale to zero when idle!
  maxReplicaCount: 50
  triggers:
  - type: aws-sqs-queue
    metadata:
      queueURL: https://sqs.us-east-1.amazonaws.com/123/orders
      queueLength: "5"        # One pod per 5 messages
      awsRegion: us-east-1
```

---

# PART VI — CLOUD KUBERNETES (MANAGED SERVICES)

---

## Chapter 25: Amazon EKS

**Amazon Elastic Kubernetes Service (EKS)** is AWS's managed Kubernetes offering. AWS manages the control plane; you manage the worker nodes (or use managed node groups or Fargate).

### EKS Components

```
┌─────────────────────────────────────┐
│  AWS Managed Control Plane          │
│  kube-apiserver, etcd, controllers  │
│  (runs in AWS-managed VPC, HA)      │
└─────────────────────────────────────┘
             │ AWS API
┌────────────┴────────────────────────┐
│  Your AWS Account                   │
│  ┌──────────────┐  ┌─────────────┐  │
│  │ Managed Node │  │  Fargate    │  │
│  │ Groups       │  │  Profiles   │  │
│  │ (EC2 ASGs)   │  │  (Serverless│  │
│  └──────────────┘  └─────────────┘  │
└─────────────────────────────────────┘
```

### Creating an EKS Cluster

```bash
# Using eksctl (recommended)
eksctl create cluster \
  --name my-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 3 \
  --nodes-min 1 \
  --nodes-max 10 \
  --managed

# Configure kubectl
aws eks update-kubeconfig \
  --region us-east-1 \
  --name my-cluster
```

### EKS Add-ons

Managed add-ons that AWS keeps updated:

```bash
# Common add-ons
aws eks create-addon --cluster-name my-cluster --addon-name vpc-cni
aws eks create-addon --cluster-name my-cluster --addon-name coredns
aws eks create-addon --cluster-name my-cluster --addon-name kube-proxy
aws eks create-addon --cluster-name my-cluster --addon-name aws-ebs-csi-driver
```

### EKS with IAM (IRSA — IAM Roles for Service Accounts)

This is a critical EKS concept — allows pods to have AWS IAM permissions without static credentials:

```bash
# Create IAM role for service account
eksctl create iamserviceaccount \
  --cluster my-cluster \
  --namespace production \
  --name s3-reader \
  --attach-policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess \
  --approve
```

```yaml
# Pod uses the service account
spec:
  serviceAccountName: s3-reader
  # Pod now has S3 read permissions via IRSA
```

### EKS Fargate

Run pods serverlessly — no EC2 nodes to manage:

```bash
# Create Fargate profile
eksctl create fargateprofile \
  --cluster my-cluster \
  --name app-profile \
  --namespace production
```

---

## Chapter 26: Google GKE

**Google Kubernetes Engine (GKE)** is Google Cloud's managed Kubernetes service — considered the most mature since Kubernetes originated at Google.

### GKE Features

- **Autopilot mode** — fully managed nodes (Google manages worker nodes)
- **Standard mode** — you manage nodes
- **Release channels** — Rapid, Regular, Stable
- **Binary Authorization** — only trusted images can be deployed
- **Workload Identity** — pod-to-GCP-API auth without static credentials

### Creating a GKE Cluster

```bash
# Standard cluster
gcloud container clusters create my-cluster \
  --region us-central1 \
  --num-nodes 3 \
  --machine-type n2-standard-4 \
  --enable-autoscaling \
  --min-nodes 1 \
  --max-nodes 10

# Autopilot cluster (recommended)
gcloud container clusters create-auto my-cluster \
  --region us-central1

# Configure kubectl
gcloud container clusters get-credentials my-cluster \
  --region us-central1
```

### Workload Identity (GKE's version of IRSA)

```bash
# Bind Kubernetes service account to GCP service account
gcloud iam service-accounts add-iam-policy-binding \
  my-gsa@my-project.iam.gserviceaccount.com \
  --role roles/iam.workloadIdentityUser \
  --member "serviceAccount:my-project.svc.id.goog[namespace/ksa-name]"

kubectl annotate serviceaccount ksa-name \
  --namespace namespace \
  iam.gke.io/gcp-service-account=my-gsa@my-project.iam.gserviceaccount.com
```

### GKE Autopilot vs Standard

| Feature | Autopilot | Standard |
|---|---|---|
| Node management | Google manages | You manage |
| Billing | Per pod | Per node |
| Node configuration | No access | Full access |
| DaemonSets | Limited | Full support |
| Spot/Preemptible | Supported | Full support |
| Best for | Dev/small teams | Large production |

---

## Chapter 27: Azure AKS

**Azure Kubernetes Service (AKS)** is Microsoft Azure's managed Kubernetes offering, with deep integration with Azure Active Directory and Azure services.

### Creating an AKS Cluster

```bash
# Create resource group
az group create --name my-rg --location eastus

# Create AKS cluster
az aks create \
  --resource-group my-rg \
  --name my-cluster \
  --node-count 3 \
  --node-vm-size Standard_D4s_v3 \
  --enable-cluster-autoscaler \
  --min-count 1 \
  --max-count 10 \
  --enable-addons monitoring \
  --generate-ssh-keys

# Configure kubectl
az aks get-credentials \
  --resource-group my-rg \
  --name my-cluster
```

### AKS Workload Identity

```bash
# Enable workload identity on cluster
az aks update --resource-group my-rg --name my-cluster \
  --enable-workload-identity --enable-oidc-issuer

# Create managed identity and federated credential
az identity create --name my-identity --resource-group my-rg
az identity federated-credential create \
  --name my-fed-cred \
  --identity-name my-identity \
  --resource-group my-rg \
  --issuer $AKS_OIDC_ISSUER \
  --subject system:serviceaccount:namespace:ksa-name
```

### AKS Virtual Nodes (Azure Container Instances)

AKS can burst workloads to Azure Container Instances for scale-out spikes without pre-provisioning nodes.

---

## Chapter 28: Cloud Load Balancers and Ingress

### AWS: Application Load Balancer (ALB) with AWS Load Balancer Controller

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/certificate-arn: arn:aws:acm:us-east-1:...
    alb.ingress.kubernetes.io/healthcheck-path: /health
    alb.ingress.kubernetes.io/listen-ports: '[{"HTTP":80,"HTTPS":443}]'
    alb.ingress.kubernetes.io/ssl-redirect: "443"
spec:
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp
            port:
              number: 80
```

### GKE: Google Cloud Load Balancer

```yaml
annotations:
  kubernetes.io/ingress.class: gce
  networking.gke.io/managed-certificates: my-cert
  kubernetes.io/ingress.global-static-ip-name: my-ip
```

### cert-manager: Automatic TLS Certificates

```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: my-cert
spec:
  secretName: my-tls-secret
  issuerRef:
    name: letsencrypt-prod
    kind: ClusterIssuer
  dnsNames:
  - api.example.com
```

---

## Chapter 29: Managed Node Groups and Spot Instances

### AWS EKS Managed Node Groups

```bash
eksctl create nodegroup \
  --cluster my-cluster \
  --name on-demand-ng \
  --node-type m5.xlarge \
  --nodes 3 \
  --nodes-min 1 \
  --nodes-max 20 \
  --managed

# Spot instances node group (for cost savings)
eksctl create nodegroup \
  --cluster my-cluster \
  --name spot-ng \
  --spot \
  --instance-types m5.xlarge,m5.2xlarge,m4.xlarge \
  --nodes-min 0 \
  --nodes-max 50
```

### Cluster Autoscaler

Automatically adjusts the number of nodes:

```yaml
# Deployment for Cluster Autoscaler (AWS)
- --cloud-provider=aws
- --node-group-auto-discovery=asg:tag=k8s.io/cluster-autoscaler/enabled,k8s.io/cluster-autoscaler/my-cluster
- --balance-similar-node-groups
- --skip-nodes-with-system-pods=false
```

### Karpenter (Modern Node Provisioner for AWS)

Karpenter replaces Cluster Autoscaler with faster, more flexible provisioning:

```yaml
apiVersion: karpenter.sh/v1alpha5
kind: Provisioner
metadata:
  name: default
spec:
  requirements:
  - key: karpenter.sh/capacity-type
    operator: In
    values: ["spot", "on-demand"]
  - key: kubernetes.io/arch
    operator: In
    values: ["amd64", "arm64"]
  limits:
    resources:
      cpu: 1000
  ttlSecondsAfterEmpty: 30       # Remove empty node after 30s
```

---

## Chapter 30: Cloud IAM and Kubernetes RBAC

### AWS IAM to EKS Access

EKS uses `aws-auth` ConfigMap to map IAM users/roles to Kubernetes RBAC:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: aws-auth
  namespace: kube-system
data:
  mapRoles: |
    - rolearn: arn:aws:iam::111122223333:role/NodeGroupRole
      username: system:node:{{EC2PrivateDNSName}}
      groups:
      - system:bootstrappers
      - system:nodes
    - rolearn: arn:aws:iam::111122223333:role/DevTeamRole
      username: dev-user
      groups:
      - developers
```

### Azure AAD Integration with AKS

```bash
# Enable AAD integration
az aks update \
  --resource-group my-rg \
  --name my-cluster \
  --enable-azure-rbac
```

---

# PART VII — SECURITY

---

## Chapter 31: RBAC — Role-Based Access Control

RBAC controls who can do what in the Kubernetes cluster.

### Four RBAC Objects

| Object | Scope | Description |
|---|---|---|
| `Role` | Namespace | Permissions within one namespace |
| `ClusterRole` | Cluster-wide | Permissions across all namespaces |
| `RoleBinding` | Namespace | Grants Role to a user/group |
| `ClusterRoleBinding` | Cluster-wide | Grants ClusterRole to a user/group |

### Role Example

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-reader
  namespace: production
rules:
- apiGroups: [""]         # "" = core API group
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
- apiGroups: ["apps"]
  resources: ["deployments"]
  verbs: ["get", "list", "watch", "update", "patch"]
```

### RoleBinding

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods-binding
  namespace: production
subjects:
- kind: User
  name: jane
  apiGroup: rbac.authorization.k8s.io
- kind: Group
  name: developers
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

### ClusterRole Example

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: secret-reader
rules:
- apiGroups: [""]
  resources: ["secrets"]
  verbs: ["get", "list"]
- apiGroups: [""]
  resources: ["nodes"]
  verbs: ["get", "list", "watch"]
```

### RBAC Commands

```bash
# Check what permissions a user has
kubectl auth can-i list pods --as jane
kubectl auth can-i create deployments --as jane --namespace production

# List all roles
kubectl get roles --all-namespaces
kubectl get clusterroles

# View role rules
kubectl describe role pod-reader -n production
```

---

## Chapter 32: Service Accounts

**Service Accounts** are identities for pods to authenticate to the Kubernetes API (and, via IRSA/Workload Identity, to cloud APIs).

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-app-sa
  namespace: production
  annotations:
    # AWS IRSA annotation
    eks.amazonaws.com/role-arn: arn:aws:iam::111122223333:role/MyAppRole
```

```yaml
# Use in pod
spec:
  serviceAccountName: my-app-sa
```

### Default Service Account

Every namespace has a `default` service account. Pods use it if no other is specified. Best practice: create dedicated service accounts per application with minimal permissions.

---

## Chapter 33: Pod Security

### Pod Security Admission (PSA) — replaces PodSecurityPolicy (PSP)

PSA enforces security standards at the namespace level:

```bash
# Label namespace with security level
kubectl label namespace production \
  pod-security.kubernetes.io/enforce=restricted \
  pod-security.kubernetes.io/warn=baseline
```

Security levels:
- **privileged** — no restrictions
- **baseline** — minimal restrictions (prevents known privilege escalations)
- **restricted** — heavily restricted, follows security best practices

### Security Context

```yaml
spec:
  securityContext:
    runAsNonRoot: true
    runAsUser: 1000
    runAsGroup: 3000
    fsGroup: 2000
    seccompProfile:
      type: RuntimeDefault
  containers:
  - name: app
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop:
        - ALL
        add:
        - NET_BIND_SERVICE
```

---

## Chapter 34: Secrets Management in the Cloud

### External Secrets Operator

Syncs secrets from cloud secret managers into Kubernetes Secrets:

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: aws-secretsmanager
    kind: ClusterSecretStore
  target:
    name: db-secret      # Creates this K8s Secret
  data:
  - secretKey: password
    remoteRef:
      key: production/db/password   # AWS Secrets Manager path
```

### AWS Secrets Manager Integration

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ClusterSecretStore
metadata:
  name: aws-secretsmanager
spec:
  provider:
    aws:
      service: SecretsManager
      region: us-east-1
      auth:
        jwt:
          serviceAccountRef:
            name: external-secrets-sa
```

### HashiCorp Vault with Kubernetes

```yaml
# Vault sidecar injector annotation
annotations:
  vault.hashicorp.com/agent-inject: "true"
  vault.hashicorp.com/role: "myapp"
  vault.hashicorp.com/agent-inject-secret-config.txt: "secret/data/myapp/config"
```

---

## Chapter 35: Network Policies for Security

Zero-trust network model — deny all, then explicitly allow:

```yaml
# Step 1: Default deny all
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
---
# Step 2: Allow app to reach database
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-app-to-db
spec:
  podSelector:
    matchLabels:
      tier: database
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          tier: app
    ports:
    - port: 5432
```

---

# PART VIII — OPERATIONS

---

## Chapter 36: Health Probes

Kubernetes supports three types of probes:

### Liveness Probe
Tells Kubernetes whether to **restart** the container. If it fails, the container is killed and restarted.

### Readiness Probe
Tells Kubernetes whether to **send traffic** to the pod. If it fails, the pod is removed from service endpoints (but not restarted).

### Startup Probe
Protects slow-starting containers from liveness probe failures during initialization.

```yaml
containers:
- name: app
  livenessProbe:
    httpGet:
      path: /healthz
      port: 8080
    initialDelaySeconds: 10
    periodSeconds: 15
    failureThreshold: 3

  readinessProbe:
    httpGet:
      path: /ready
      port: 8080
    initialDelaySeconds: 5
    periodSeconds: 10
    successThreshold: 1
    failureThreshold: 3

  startupProbe:
    httpGet:
      path: /healthz
      port: 8080
    failureThreshold: 30    # 30 * 10s = 5 minutes startup time
    periodSeconds: 10
```

### Probe Types

```yaml
# HTTP
livenessProbe:
  httpGet:
    path: /health
    port: 8080
    httpHeaders:
    - name: Authorization
      value: Bearer mytoken

# TCP socket
livenessProbe:
  tcpSocket:
    port: 3306

# Command execution
livenessProbe:
  exec:
    command:
    - sh
    - -c
    - redis-cli ping | grep PONG

# gRPC
livenessProbe:
  grpc:
    port: 9090
    service: liveness
```

---

## Chapter 37: Rolling Updates and Rollbacks

### Rolling Update Process

```yaml
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 25%  # Up to 25% of pods can be unavailable
      maxSurge: 25%        # Up to 25% extra pods can be created
```

During rolling update:
1. Kubernetes creates new pods (respecting maxSurge)
2. Waits for new pods to be ready (readinessProbe)
3. Terminates old pods (respecting maxUnavailable)
4. Repeats until all pods are updated

### Rollout Commands

```bash
# Deploy new version
kubectl set image deployment/myapp app=myapp:v2.0

# Watch rollout progress
kubectl rollout status deployment/myapp

# Pause rollout (manual canary)
kubectl rollout pause deployment/myapp

# Resume rollout
kubectl rollout resume deployment/myapp

# View history
kubectl rollout history deployment/myapp

# View a specific revision
kubectl rollout history deployment/myapp --revision=3

# Rollback to previous version
kubectl rollout undo deployment/myapp

# Rollback to specific revision
kubectl rollout undo deployment/myapp --to-revision=2
```

### Graceful Shutdown

```yaml
spec:
  terminationGracePeriodSeconds: 60  # Give app 60s to finish requests
  containers:
  - name: app
    lifecycle:
      preStop:
        exec:
          command: ["/bin/sh", "-c", "sleep 5"]  # Wait for LB to remove pod
```

---

## Chapter 38: Namespaces and Multi-Tenancy

**Namespaces** provide virtual clusters within a physical cluster — logical isolation for teams, environments, or applications.

```bash
# Create namespace
kubectl create namespace staging

# List namespaces
kubectl get namespaces

# Work in a namespace
kubectl get pods -n staging
kubectl get all -n production

# Set default namespace for kubectl session
kubectl config set-context --current --namespace=staging

# Delete namespace (deletes everything in it!)
kubectl delete namespace staging
```

### Namespace-based Environment Separation

```
my-cluster
├── namespace: production    (prod resources, strict RBAC)
├── namespace: staging       (pre-prod, limited access)
├── namespace: development   (dev access, ResourceQuota limits)
└── namespace: monitoring    (Prometheus, Grafana)
```

### Cross-namespace Communication

```bash
# Service in another namespace
curl http://service-name.other-namespace.svc.cluster.local
```

---

## Chapter 39: Monitoring and Observability

### Prometheus + Grafana Stack

The standard Kubernetes monitoring stack:

```bash
# Install via Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace
```

This installs:
- **Prometheus** — time-series metrics database
- **Grafana** — visualization dashboards
- **Alertmanager** — alert routing
- **kube-state-metrics** — Kubernetes object metrics
- **node-exporter** — host-level metrics

### ServiceMonitor (Tell Prometheus what to scrape)

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: myapp-monitor
spec:
  selector:
    matchLabels:
      app: myapp
  endpoints:
  - port: metrics
    path: /metrics
    interval: 30s
```

### Cloud-Native Monitoring

**AWS:** CloudWatch Container Insights for EKS
**GCP:** Cloud Monitoring with GKE (built-in)
**Azure:** Azure Monitor for AKS (built-in)

```bash
# Enable CloudWatch Container Insights (EKS)
eksctl utils update-cluster-logging \
  --cluster my-cluster \
  --enable-types all

# GKE metrics are automatically collected
```

### Key Metrics to Know

| Metric | What it tells you |
|---|---|
| `kube_pod_status_phase` | Pod phases (running, pending, failed) |
| `kube_deployment_status_replicas_ready` | Ready vs desired replicas |
| `container_cpu_usage_seconds_total` | CPU usage |
| `container_memory_working_set_bytes` | Memory usage |
| `kube_node_status_condition` | Node health |
| `kube_hpa_status_current_replicas` | HPA scaling activity |

---

## Chapter 40: Logging Architecture

### Log Flow in Kubernetes

```
Application stdout/stderr
       |
       v
Container runtime (containerd)
       |
       v
Node log files (/var/log/pods/...)
       |
       v
Log collector DaemonSet (Fluentd / Fluent Bit / Promtail)
       |
       v
Log aggregation (Elasticsearch, Loki, CloudWatch, Datadog)
       |
       v
Visualization (Kibana, Grafana, CloudWatch Logs Insights)
```

### Fluent Bit DaemonSet for EKS → CloudWatch

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: fluent-bit
  namespace: amazon-cloudwatch
spec:
  selector:
    matchLabels:
      name: fluent-bit
  template:
    spec:
      serviceAccountName: fluent-bit
      containers:
      - name: fluent-bit
        image: public.ecr.aws/aws-observability/aws-for-fluent-bit:latest
        volumeMounts:
        - name: varlog
          mountPath: /var/log
        envFrom:
        - configMapRef:
            name: fluent-bit-cluster-info
```

### kubectl Logging Commands

```bash
kubectl logs <pod>                         # Current logs
kubectl logs <pod> --previous             # Previous container logs (after restart)
kubectl logs <pod> -c <container>         # Specific container
kubectl logs -f <pod>                     # Follow live
kubectl logs <pod> --since=1h            # Last hour
kubectl logs --selector app=myapp        # All pods matching label
```

---

# PART IX — ADVANCED TOPICS

---

## Chapter 41: Helm — Kubernetes Package Manager

**Helm** is the package manager for Kubernetes. It packages K8s YAML files into **charts** — versioned, configurable packages.

### Key Concepts

| Concept | Description |
|---|---|
| **Chart** | Package of K8s resources (like an npm package) |
| **Repository** | Collection of charts (like npm registry) |
| **Release** | Installed instance of a chart in a cluster |
| **Values** | Configuration parameters for a chart |

### Installing Helm Charts

```bash
# Add a repository
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Search for charts
helm search repo postgresql

# Install a chart
helm install my-postgres bitnami/postgresql \
  --namespace databases \
  --create-namespace \
  --set auth.postgresPassword=secretpassword \
  --set primary.persistence.size=20Gi

# Install with values file
helm install my-postgres bitnami/postgresql \
  --namespace databases \
  -f custom-values.yaml

# List releases
helm list --all-namespaces

# Upgrade a release
helm upgrade my-postgres bitnami/postgresql \
  --set image.tag=15.0

# Rollback
helm rollback my-postgres 1

# Uninstall
helm uninstall my-postgres -n databases
```

### Creating a Chart

```bash
helm create mychart
# Creates:
# mychart/
#   Chart.yaml          # Chart metadata
#   values.yaml         # Default configuration
#   templates/          # K8s YAML templates
#     deployment.yaml
#     service.yaml
#     ingress.yaml
#     _helpers.tpl       # Template helpers
```

### values.yaml Example

```yaml
replicaCount: 3

image:
  repository: myapp
  tag: "1.0.0"
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 80

ingress:
  enabled: true
  host: api.example.com

resources:
  requests:
    cpu: 100m
    memory: 128Mi
  limits:
    cpu: 500m
    memory: 256Mi
```

### Deployment Template

```yaml
# templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "mychart.fullname" . }}
  labels:
    {{- include "mychart.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      {{- include "mychart.selectorLabels" . | nindent 6 }}
  template:
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        ports:
        - containerPort: 80
```

---

## Chapter 42: GitOps with ArgoCD / Flux

**GitOps** is a deployment model where Git is the single source of truth. Changes to K8s manifests in Git automatically sync to the cluster.

### ArgoCD

ArgoCD continuously watches a Git repository and syncs the cluster state to match it.

```bash
# Install ArgoCD
kubectl create namespace argocd
kubectl apply -n argocd -f \
  https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Access UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# Login
argocd login localhost:8080
```

### ArgoCD Application

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: myapp
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/myorg/myapp-config
    targetRevision: main
    path: kubernetes/production
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true          # Delete resources removed from Git
      selfHeal: true       # Auto-fix drift
    syncOptions:
    - CreateNamespace=true
```

### GitOps Workflow

```
Developer pushes to Git
        |
        v
CI Pipeline runs (tests, build, push image)
        |
        v
Update image tag in Git (via bot/PR)
        |
        v
ArgoCD detects change
        |
        v
ArgoCD syncs cluster (rolling update)
        |
        v
Cluster matches Git state
```

---

## Chapter 43: Service Mesh — Istio Basics

A **service mesh** adds observability, traffic management, and security to service-to-service communication — without changing application code.

### What a Service Mesh Provides

| Feature | Description |
|---|---|
| **mTLS** | Mutual TLS encryption between all services |
| **Traffic management** | Canary, blue/green, circuit breaker, retries |
| **Observability** | Distributed tracing, metrics, service graph |
| **Access control** | Fine-grained service-to-service authorization |

### Istio Architecture

- **Data plane:** Envoy sidecar proxies injected into every pod
- **Control plane (Istiod):** Configuration, certificate management, service discovery

```bash
# Install Istio
istioctl install --set profile=production

# Enable sidecar injection for a namespace
kubectl label namespace production istio-injection=enabled
```

### Traffic Management

```yaml
# VirtualService — routing rules
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: myapp
spec:
  hosts:
  - myapp
  http:
  - match:
    - headers:
        canary:
          exact: "true"
    route:
    - destination:
        host: myapp
        subset: v2
  - route:
    - destination:
        host: myapp
        subset: v1
      weight: 90
    - destination:
        host: myapp
        subset: v2
      weight: 10    # 10% canary traffic to v2
```

---

# PART X — INTERVIEW PREPARATION

---

## Chapter 44: Top 60 Kubernetes Interview Questions

### Fundamentals

**Q1. What is Kubernetes and what problem does it solve?**
Kubernetes is an open-source container orchestration platform that automates deployment, scaling, and management of containerized applications across a cluster. It solves production-scale container challenges: self-healing, auto-scaling, rolling updates, service discovery, storage orchestration, and secret management.

**Q2. What is the difference between a Pod and a Container?**
A Pod is the smallest deployable unit in Kubernetes — it wraps one or more containers that share the same network namespace (IP), storage volumes, and lifecycle. A container is the actual running process. Kubernetes doesn't manage containers directly; it manages Pods.

**Q3. What are the main components of the Kubernetes control plane?**
kube-apiserver (REST gateway for all operations), etcd (distributed key-value store, cluster state), kube-scheduler (assigns pods to nodes), kube-controller-manager (runs controllers for nodes, deployments, endpoints), cloud-controller-manager (cloud provider integration).

**Q4. What is etcd in Kubernetes?**
etcd is a distributed, consistent key-value store that serves as the backing store for all Kubernetes cluster state and configuration. All objects (pods, services, deployments) are stored in etcd. It must be backed up regularly — losing etcd means losing all cluster state.

**Q5. What is a kubelet?**
The kubelet is an agent running on every worker node. It registers the node with the API server, watches for pod specs assigned to its node, starts/stops containers via the container runtime, and reports pod and node health status back to the API server.

**Q6. What is the difference between Deployment and StatefulSet?**
Deployments are for stateless applications — pods get random names, share nothing, can be scaled in any order. StatefulSets are for stateful applications (databases) — pods get stable names (postgres-0, postgres-1), each gets its own PersistentVolumeClaim, and scaling is ordered.

**Q7. What is a ReplicaSet?**
A ReplicaSet ensures a specified number of pod replicas are running at all times. If a pod fails, it creates a replacement. You rarely create ReplicaSets directly — Deployments manage them for you and add rolling update capabilities.

**Q8. What is a DaemonSet?**
A DaemonSet ensures one pod runs on every node in the cluster (or a subset). Used for log collectors (Fluentd), monitoring agents (Prometheus node-exporter), and network plugins.

**Q9. What is a Kubernetes Namespace?**
Namespaces provide virtual clusters within a physical cluster. They isolate resources, allow applying RBAC and ResourceQuotas per team or environment. Common pattern: namespaces per environment (production, staging, development) or per team.

**Q10. What is kube-proxy?**
kube-proxy runs on every worker node and maintains network rules (iptables or IPVS). It implements Kubernetes Services by routing traffic to the correct pods across the cluster.

---

### Networking

**Q11. Explain Kubernetes networking model.**
Every pod gets its own IP. Pods can communicate with any other pod on any node without NAT (flat network). This is implemented by CNI plugins (Calico, Cilium, AWS VPC CNI, etc.). Services provide stable IP/DNS endpoints on top of changing pod IPs.

**Q12. What are the types of Kubernetes Services?**
ClusterIP (internal only, default), NodePort (exposes on each node's port, 30000-32767), LoadBalancer (provisions cloud load balancer), ExternalName (maps to external DNS), Headless (no ClusterIP, direct pod DNS).

**Q13. What is an Ingress?**
An Ingress is an API object that manages external HTTP/HTTPS access to services, providing URL routing, host-based routing, and TLS termination. An Ingress Controller (nginx, ALB, GKE LB) implements the rules.

**Q14. What is a NetworkPolicy?**
NetworkPolicy controls ingress and egress traffic between pods. By default, all pods can communicate freely. NetworkPolicy allows you to restrict this — for example, only allow frontend pods to talk to backend pods on port 8080.

**Q15. How does service discovery work in Kubernetes?**
Kubernetes runs CoreDNS. Every service gets a DNS name: `<service>.<namespace>.svc.cluster.local`. Pods in the same namespace can use just the service name. The search path resolves short names automatically.

---

### Storage

**Q16. What is a PersistentVolume (PV)?**
A PersistentVolume is a piece of storage in the cluster that has been provisioned by an admin or dynamically via a StorageClass. It's a resource in the cluster like a node, independent of any pod lifecycle.

**Q17. What is a PersistentVolumeClaim (PVC)?**
A PVC is a request for storage by a user. It binds to a suitable PV. The pod references the PVC, not the PV directly. PVCs abstract the details of how storage is provided.

**Q18. What is a StorageClass?**
A StorageClass defines how storage should be dynamically provisioned (cloud disk type, IOPS, encryption). When a PVC requests a StorageClass, Kubernetes automatically creates a PV. Example: `gp3` on EKS creates an AWS EBS gp3 volume.

**Q19. What are the access modes for PersistentVolumes?**
ReadWriteOnce (RWO) — single node read/write. ReadOnlyMany (ROX) — multiple nodes read-only. ReadWriteMany (RWX) — multiple nodes read/write (requires NFS, EFS, Azure Files, etc.). ReadWriteOncePod (RWOP) — single pod read/write.

**Q20. What is the difference between ConfigMap and Secret?**
ConfigMap stores non-sensitive configuration (connection strings, feature flags). Secret stores sensitive data (passwords, API keys) in base64-encoded form. Both can be mounted as files or injected as environment variables. Secrets are base64-encoded, not encrypted by default — encryption at rest must be explicitly enabled.

---

### Scheduling

**Q21. How does the Kubernetes scheduler work?**
The scheduler watches for new pods with no assigned node. It filters nodes (removes nodes that can't run the pod based on resources, taints, affinity) then scores the remaining nodes. The highest-scoring node is selected.

**Q22. What is the difference between resource requests and limits?**
Requests are what a container is guaranteed — used by the scheduler to find a node with enough capacity. Limits are the maximum the container can use at runtime. Exceeding CPU limit causes throttling; exceeding memory limit causes OOMKill.

**Q23. What are taints and tolerations?**
Taints are applied to nodes to repel pods. Tolerations in pod specs allow pods to schedule on tainted nodes. Use case: dedicated node pools for specific workloads (GPU nodes, spot instances, sensitive workloads).

**Q24. What is node affinity?**
Node affinity allows pod scheduling rules based on node labels — required (hard) or preferred (soft). Stricter alternative to nodeSelector. Example: require pods to run in specific availability zones.

**Q25. What is the Horizontal Pod Autoscaler?**
HPA automatically scales pod replicas based on CPU utilization, memory, or custom metrics. It requires Metrics Server and resource requests to be set. Example: scale from 2 to 20 replicas when CPU exceeds 70%.

---

### Security

**Q26. What is RBAC in Kubernetes?**
Role-Based Access Control restricts who can do what. Roles (namespaced) and ClusterRoles (cluster-wide) define permissions. RoleBindings and ClusterRoleBindings grant those roles to users, groups, or service accounts.

**Q27. What is a ServiceAccount?**
A ServiceAccount is an identity for a pod to authenticate to the Kubernetes API. Pods use service accounts to get tokens, which are mounted automatically. In cloud environments (IRSA, Workload Identity), service accounts can also provide cloud IAM credentials.

**Q28. How do you secure pod-to-pod communication?**
Use NetworkPolicy to restrict which pods can communicate. For encryption in transit, use a service mesh (Istio, Linkerd) which provides mutual TLS (mTLS) between pods. Apply Pod Security Admission to restrict privilege escalation.

**Q29. What is IRSA in EKS?**
IAM Roles for Service Accounts — a mechanism that allows Kubernetes service accounts to assume IAM roles, giving pods specific AWS permissions without static credentials. The pod gets a signed web identity token that it exchanges for temporary AWS credentials.

**Q30. How should you handle secrets in Kubernetes?**
Never put secrets in container images or source code. Use Kubernetes Secrets with encryption at rest enabled. For production, use External Secrets Operator to sync from AWS Secrets Manager/GCP Secret Manager/Azure Key Vault. Consider Vault for complex secret management.

---

### Cloud Kubernetes

**Q31. What is EKS?**
Amazon Elastic Kubernetes Service — AWS's managed Kubernetes. AWS manages the control plane. Workers run on managed node groups (EC2 ASGs), self-managed EC2, or Fargate (serverless). Deep integration with AWS services (ALB, IAM, EBS, EFS, CloudWatch).

**Q32. What is GKE Autopilot?**
GKE Autopilot is a fully managed GKE mode where Google manages both the control plane and the worker nodes. You pay per pod resource request, not per node. No node maintenance, automatic scaling, security hardening built in.

**Q33. What is the difference between EKS Fargate and managed node groups?**
Managed node groups run pods on EC2 instances you configure. Fargate is serverless — pods run on AWS-managed infrastructure, no nodes to manage. Fargate pods are isolated per-pod (stronger isolation, but higher cost and no DaemonSets).

**Q34. What is the Cluster Autoscaler?**
Cluster Autoscaler automatically adjusts the number of nodes in a node group based on pod scheduling needs. It adds nodes when pods can't be scheduled due to insufficient resources, and removes underutilized nodes.

**Q35. What is Karpenter?**
Karpenter is a modern node provisioner for AWS (and Azure in preview) that replaces Cluster Autoscaler. It's faster (seconds vs minutes), selects optimal instance types dynamically, and can consolidate nodes to reduce cost.

---

### Operations

**Q36. What is the difference between liveness, readiness, and startup probes?**
Liveness probe: should the container be restarted? If it fails, Kubernetes kills and restarts the container. Readiness probe: should traffic be sent here? If it fails, pod is removed from Service endpoints. Startup probe: protects slow-starting containers from liveness failures during initial startup.

**Q37. How do rolling updates work?**
Kubernetes creates new pods (up to maxSurge extra), waits for readiness, then terminates old pods (keeping at least `replicas - maxUnavailable` running). This continues until all pods run the new version. Zero downtime if readiness probes are configured correctly.

**Q38. How do you roll back a deployment?**
`kubectl rollout undo deployment/myapp` rolls back to the previous revision. `kubectl rollout undo deployment/myapp --to-revision=2` rolls back to a specific revision. Kubernetes keeps rollout history (configurable with `revisionHistoryLimit`).

**Q39. What is Helm?**
Helm is the Kubernetes package manager. Charts are versioned packages of K8s YAML templates. Releases are installed chart instances. Values parameterize the chart for different environments. Helm simplifies deploying complex applications (databases, monitoring stacks) and managing their lifecycles.

**Q40. What is GitOps?**
GitOps is a deployment approach where Git is the single source of truth for cluster state. Tools like ArgoCD or Flux watch a Git repository and automatically sync the cluster to match. Changes are made via Git commits (PRs), providing audit trail, rollback, and consistency.

---

### Advanced

**Q41. What is a service mesh and why use it?**
A service mesh (Istio, Linkerd) injects sidecar proxies into pods to manage service-to-service communication. Benefits: mutual TLS encryption, advanced traffic management (canary, circuit breaking, retries), distributed tracing, and access control — all without changing application code.

**Q42. What is a CRD (Custom Resource Definition)?**
A CRD extends the Kubernetes API with custom resource types. Many tools use CRDs: cert-manager adds `Certificate`, ArgoCD adds `Application`, Prometheus adds `ServiceMonitor`. CRDs allow Kubernetes to manage non-standard resources using standard kubectl commands.

**Q43. What is the difference between `kubectl apply` and `kubectl create`?**
`kubectl create` creates resources imperatively — fails if the resource already exists. `kubectl apply` is declarative — creates the resource if it doesn't exist, updates it if it does. Always use `apply` in production and CI/CD.

**Q44. How does Kubernetes handle pod failure?**
If a pod fails: the container is restarted (respecting restartPolicy). If the node fails: the controller (Deployment, ReplicaSet) creates replacement pods on healthy nodes. The scheduler places the new pods on available nodes. StatefulSets preserve pod identity during rescheduling.

**Q45. What is a headless service?**
A headless service has `clusterIP: None`. Instead of a single virtual IP, DNS resolves to all pod IPs directly. Used with StatefulSets for direct pod addressing. `postgres-0.postgres.default.svc.cluster.local` resolves to the specific pod's IP.

**Q46. What is init container?**
Init containers run before app containers, in sequence, and must complete successfully. Used for: waiting for dependencies (database ready), database migrations, fetching configuration from a remote source, setting up volumes.

**Q47. What is a PodDisruptionBudget (PDB)?**
A PDB limits the number of pods that can be voluntarily disrupted at once (during node drains, upgrades). Ensures minimum availability during maintenance. Example: guarantee at least 2 of 3 replicas always available.

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: myapp-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: myapp
```

**Q48. What is resource quotas?**
ResourceQuota limits total resource consumption within a namespace — total CPU, memory, pods count, PVCs, services. Prevents one team from using all cluster resources.

**Q49. What is the Kubernetes admission controller?**
Admission controllers intercept API requests before they are persisted to etcd. They can validate (ValidatingAdmissionWebhook) or mutate (MutatingAdmissionWebhook) resources. Examples: enforce resource limits, inject sidecars, validate image signing.

**Q50. How is Kubernetes different from Docker Swarm?**
Kubernetes has a much richer feature set: HPA, VPA, advanced scheduling (affinity, taints), RBAC, NetworkPolicy, StorageClass, CRDs, a massive ecosystem. Swarm is simpler but lacks most of these features. Kubernetes is the industry standard for production container orchestration.

**Q51. Explain the lifecycle of a Kubernetes deployment update.**
1. User runs `kubectl set image` or `kubectl apply`. 2. API server validates and stores new spec. 3. Deployment controller detects change, creates new ReplicaSet. 4. Scheduler assigns new pods to nodes. 5. Kubelet starts new containers. 6. Readiness probe passes. 7. Service starts routing traffic to new pods. 8. Old pods gracefully terminated. 9. Repeats until rollout complete.

**Q52. What is the difference between NodePort and LoadBalancer services?**
NodePort exposes a service on each node's IP at a fixed port (30000-32767) — useful for development, but requires knowing node IPs and firewall rules. LoadBalancer provisions a cloud load balancer (ALB, GCP LB, Azure LB) with a stable external IP — the production-ready approach.

**Q53. What is a Kubernetes operator?**
An operator is a pattern for extending Kubernetes with domain-specific knowledge. It packages a custom controller with CRDs to manage stateful applications (databases, message queues). Examples: PostgreSQL Operator, Kafka Operator, cert-manager. It automates human operational knowledge.

**Q54. How do you debug a pod that won't start?**
1. `kubectl describe pod <name>` — check Events section for errors (image pull failed, insufficient resources, volume mount issues). 2. `kubectl logs <pod> --previous` — logs from previous crash. 3. Check node with `kubectl describe node` — out of resources? 4. Try `kubectl run debug --image=busybox -it --rm` to test from inside the cluster.

**Q55. What is the purpose of labels and selectors?**
Labels are key-value pairs attached to objects. Selectors filter objects by labels. Services use selectors to find their pods. Deployments use selectors to manage their pods. This loose coupling lets you change topology without modifying object definitions.

**Q56. What is a Kubernetes Context?**
A context in kubeconfig combines a cluster, user, and namespace. It defines where kubectl sends requests. `kubectl config get-contexts` lists all contexts. `kubectl config use-context my-cluster` switches contexts. Critical when working with multiple clusters.

**Q57. What is PodAffinity vs PodAntiAffinity?**
PodAffinity attracts pods together — schedule this pod on a node where other matching pods exist (colocation). PodAntiAffinity repels pods apart — don't schedule on the same node as other matching pods (spread for HA). Both have required and preferred rules.

**Q58. What is a Job vs CronJob?**
A Job runs a pod to completion — used for batch processing, data migration, one-time tasks. If the pod fails, the Job retries. A CronJob creates Jobs on a cron schedule — used for regular tasks like backups, cleanup, reporting.

**Q59. What happens when a node runs out of memory?**
The kernel's OOM killer starts killing processes. Kubernetes evicts pods in order: BestEffort first, then Burstable (pods that exceeded requests), then Guaranteed (pods at requests/limits). The kubelet also has eviction thresholds that trigger graceful pod eviction before kernel OOM.

**Q60. How does Kubernetes ensure high availability?**
Control plane: multiple API server instances behind a load balancer, etcd cluster (3/5 nodes), multiple controller manager/scheduler instances (leader election). Worker nodes: replicas spread across nodes/AZs via PodAntiAffinity and topology spread constraints. Storage: cloud persistent volumes survive node failures.

---

## Chapter 45: Cloud-Specific Scenarios

### Scenario 1: "How would you deploy a stateless Node.js app on EKS?"

*Strong answer:*
"Build the Docker image, push to ECR. Create a Kubernetes Deployment with 3 replicas, resource requests/limits set, readiness and liveness probes pointing to a /health endpoint. Create a ClusterIP Service. Deploy an AWS Load Balancer Controller and create an Ingress with ALB annotations for HTTPS termination, pointing to the Service. Use IRSA to give the pod any AWS permissions it needs. Store secrets in AWS Secrets Manager and use External Secrets Operator to sync them into Kubernetes Secrets."

### Scenario 2: "Production database in Kubernetes — how?"

*Strong answer:*
"Use a StatefulSet with a headless Service. Each pod gets its own PVC via volumeClaimTemplates backed by a StorageClass using EBS gp3 volumes (or equivalent). Set proper resource limits, liveness/readiness probes, and a PodDisruptionBudget to prevent simultaneous disruptions. For PostgreSQL specifically, I'd consider the Postgres Operator (CloudNativePG) which handles replication and failover. In most production setups, I'd recommend managed RDS/Cloud SQL and only run Kubernetes databases for dev or when the team has strong DB expertise."

### Scenario 3: "Traffic spikes — how do you handle autoscaling?"

*Strong answer:*
"At the pod level: HPA watching CPU, or custom metrics via KEDA (SQS queue depth, request rate). Set appropriate min/max replicas with sensible CPU targets. At the node level: Cluster Autoscaler or Karpenter to add/remove nodes. Ensure pods have resource requests set (HPA needs them). Consider a mix of on-demand and spot instances — on-demand for the baseline, spot for burst. Test the scaling chain end-to-end — HPA scales pods, but if nodes are full and Cluster Autoscaler is slow, there's a gap."

### Scenario 4: "How would you do a zero-downtime deployment?"

*Strong answer:*
"Use a Deployment with RollingUpdate strategy. Set maxUnavailable: 0 and maxSurge: 1 for strict zero-downtime. Critical: ensure readiness probes are configured — Kubernetes only sends traffic to ready pods. Add a preStop hook with a small sleep to give the load balancer time to deregister the pod. Set terminationGracePeriodSeconds to allow in-flight requests to complete. Test with a canary: route 10% of traffic to the new version before full rollout."

### Scenario 5: "How do you debug a pod in CrashLoopBackOff?"

*Strong answer:*
"First, `kubectl describe pod` to see the exit code and recent events. Exit 137 = OOMKilled (increase memory limit or fix memory leak). Exit 1 = application error. Then `kubectl logs pod --previous` to read the logs from the crashed container. If the app exits too fast to debug, override the command: `kubectl run debug --image=myimage -it -- /bin/sh` to get a shell. Check environment variables and mounted secrets/configmaps are correct. Look for CrashLoopBackOff timing — if it's a db connection error, check the database is reachable and credentials are correct."

---

## Chapter 46: kubectl Quick Reference Cheat Sheet

### Cluster Info

```bash
kubectl cluster-info
kubectl get nodes
kubectl get nodes -o wide
kubectl describe node <name>
kubectl top nodes
```

### Pods

```bash
kubectl get pods                          # Default namespace
kubectl get pods -n <namespace>           # Specific namespace
kubectl get pods --all-namespaces         # All namespaces
kubectl get pods -o wide                  # With node/IP info
kubectl get pods -l app=myapp             # By label selector
kubectl describe pod <name>               # Detailed info + events
kubectl logs <pod>                        # Logs
kubectl logs <pod> -c <container>         # Multi-container
kubectl logs -f <pod>                     # Follow
kubectl logs --previous <pod>             # Previous container
kubectl exec -it <pod> -- bash            # Shell
kubectl exec <pod> -- env                 # Print env vars
kubectl delete pod <name>
kubectl delete pod <name> --grace-period=0  # Force delete
```

### Deployments

```bash
kubectl get deployments
kubectl describe deployment <name>
kubectl apply -f deployment.yaml
kubectl set image deployment/<name> container=image:tag
kubectl scale deployment <name> --replicas=5
kubectl rollout status deployment/<name>
kubectl rollout history deployment/<name>
kubectl rollout undo deployment/<name>
kubectl rollout undo deployment/<name> --to-revision=2
kubectl rollout pause deployment/<name>
kubectl rollout resume deployment/<name>
```

### Services & Ingress

```bash
kubectl get services
kubectl get svc
kubectl describe service <name>
kubectl get ingress
kubectl describe ingress <name>
kubectl port-forward svc/<name> 8080:80  # Local port forward
kubectl port-forward pod/<name> 8080:80
```

### Storage

```bash
kubectl get pv                            # PersistentVolumes
kubectl get pvc                           # PersistentVolumeClaims
kubectl get sc                            # StorageClasses
kubectl describe pvc <name>
```

### Config & Secrets

```bash
kubectl get configmap
kubectl get cm
kubectl describe cm <name>
kubectl create cm myconfig --from-literal=key=value
kubectl create cm myconfig --from-file=./config.yaml

kubectl get secrets
kubectl create secret generic mysecret --from-literal=password=s3cr3t
kubectl describe secret <name>
```

### RBAC

```bash
kubectl get roles --all-namespaces
kubectl get clusterroles
kubectl get rolebindings
kubectl get clusterrolebindings
kubectl describe role <name> -n <namespace>
kubectl auth can-i list pods --as <user>
kubectl auth can-i '*' '*'            # Check if current user is admin
```

### Namespaces

```bash
kubectl get namespaces
kubectl create namespace myns
kubectl config set-context --current --namespace=myns
kubectl delete namespace myns
```

### Labels & Annotations

```bash
kubectl label node <node> disktype=ssd
kubectl label pod <pod> version=v2
kubectl annotate pod <pod> description="My app"
kubectl get pods --show-labels
kubectl get pods -l "env in (production,staging)"
```

### Debugging

```bash
kubectl describe <resource> <name>             # Events + config
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl get events -n <namespace>
kubectl top pods                               # CPU/memory usage
kubectl top pods --containers                  # Per-container
kubectl top nodes

# Debug container (K8s 1.23+)
kubectl debug <pod> -it --image=busybox --copy-to=debug-pod

# Get raw API output
kubectl get pod <name> -o yaml
kubectl get pod <name> -o json | jq '.status'

# Diff what would change
kubectl diff -f deployment.yaml
```

### Context Management (Multiple Clusters)

```bash
kubectl config get-contexts
kubectl config current-context
kubectl config use-context <name>
kubectl config set-context --current --namespace=production

# Quick namespace switch (install kubens)
kubens production
```

### Helm Quick Reference

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm search repo <keyword>
helm install <release> <chart> -n <namespace> -f values.yaml
helm upgrade <release> <chart> --set image.tag=v2
helm list --all-namespaces
helm rollback <release> <revision>
helm uninstall <release> -n <namespace>
helm template <release> <chart> -f values.yaml   # Render templates
helm lint <chart-dir>
helm package <chart-dir>
```

### Interview Vocabulary Quick Reference

| Term | Definition |
|---|---|
| **Pod** | Smallest deployable unit; one or more containers sharing network/storage |
| **Deployment** | Manages stateless app replicas with rolling update capability |
| **StatefulSet** | Manages stateful apps with stable identity and per-pod storage |
| **DaemonSet** | Ensures one pod runs on every (or specific) node |
| **Service** | Stable network endpoint for a set of pods |
| **Ingress** | HTTP/HTTPS routing to services; requires Ingress Controller |
| **PV / PVC** | Cluster storage (PV) requested by a pod (PVC) |
| **StorageClass** | Defines dynamic storage provisioning (EBS, GCE PD) |
| **ConfigMap** | Non-sensitive configuration as K8s object |
| **Secret** | Sensitive data as base64-encoded K8s object |
| **Namespace** | Virtual cluster for isolation; RBAC and quota boundary |
| **HPA** | Horizontal Pod Autoscaler; scales replica count |
| **Node Affinity** | Scheduling rule: run on nodes with matching labels |
| **Taint / Toleration** | Node repels pods; pod tolerates the taint to be scheduled |
| **RBAC** | Role-Based Access Control; who can do what |
| **ServiceAccount** | Pod identity for K8s API (and cloud IAM via IRSA/WI) |
| **NetworkPolicy** | Firewall rules between pods |
| **Helm** | Kubernetes package manager; charts, values, releases |
| **GitOps** | Git as source of truth; ArgoCD/Flux syncs cluster |
| **CNI** | Container Network Interface; implements pod networking |
| **CRI** | Container Runtime Interface; abstraction over containerd/CRI-O |
| **CRD** | Custom Resource Definition; extends K8s API |
| **Operator** | CRD + controller encoding domain-specific operational knowledge |
| **IRSA** | IAM Roles for Service Accounts (EKS); pod-level AWS IAM |
| **Workload Identity** | GKE/AKS equivalent of IRSA for pod-level cloud IAM |
| **Karpenter** | AWS node provisioner; faster and smarter than Cluster Autoscaler |
| **etcd** | Distributed KV store; K8s source of truth — back it up! |
| **mTLS** | Mutual TLS; encrypted + authenticated pod-to-pod comms (Istio) |

---

## Final Interview Advice

When answering Kubernetes questions, always structure your answer as:
1. **What it is** — clean one-sentence definition
2. **Why it exists** — what problem it solves
3. **How it works** — mechanics
4. **Production considerations** — trade-offs, gotchas, cloud specifics

**The ten topics most likely in cloud K8s interviews:**
- Pod vs Deployment vs StatefulSet vs DaemonSet
- Service types and when to use each
- How HPA works (and what it needs to work)
- RBAC + ServiceAccounts + IRSA/Workload Identity
- PV/PVC/StorageClass — dynamic provisioning
- Liveness vs Readiness vs Startup probes
- Rolling updates and rollback
- Namespaces for multi-tenancy
- EKS/GKE/AKS managed services fundamentals
- Helm and GitOps pattern

*Kubernetes in the Cloud: From Basics to Interview-Ready*
*46 Chapters · 200+ YAML Examples · 60 Interview Q&As*
