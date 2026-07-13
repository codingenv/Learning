# Docker: From Basics to Interview-Ready
### A Complete Guide for Developers Preparing for Technical Interviews

---

## Table of Contents

### Part I — Foundations
1. [What is Docker?](#chapter-1-what-is-docker)
2. [Virtualization vs Containerization](#chapter-2-virtualization-vs-containerization)
3. [Docker Architecture](#chapter-3-docker-architecture)
4. [Installing Docker](#chapter-4-installing-docker)
5. [Your First Container](#chapter-5-your-first-container)

### Part II — Images
6. [Understanding Docker Images](#chapter-6-understanding-docker-images)
7. [Writing Dockerfiles](#chapter-7-writing-dockerfiles)
8. [Image Layers and Caching](#chapter-8-image-layers-and-caching)
9. [Multi-Stage Builds](#chapter-9-multi-stage-builds)
10. [Image Best Practices](#chapter-10-image-best-practices)

### Part III — Containers
11. [Container Lifecycle](#chapter-11-container-lifecycle)
12. [Running Containers](#chapter-12-running-containers)
13. [Container Networking Basics](#chapter-13-container-networking-basics)
14. [Container Storage and Volumes](#chapter-14-container-storage-and-volumes)
15. [Environment Variables and Configs](#chapter-15-environment-variables-and-configs)

### Part IV — Networking
16. [Docker Network Types](#chapter-16-docker-network-types)
17. [Bridge Networks](#chapter-17-bridge-networks)
18. [Host and None Networks](#chapter-18-host-and-none-networks)
19. [Container-to-Container Communication](#chapter-19-container-to-container-communication)
20. [Port Binding and Exposure](#chapter-20-port-binding-and-exposure)

### Part V — Volumes and Storage
21. [Volumes vs Bind Mounts vs tmpfs](#chapter-21-volumes-vs-bind-mounts-vs-tmpfs)
22. [Named Volumes](#chapter-22-named-volumes)
23. [Bind Mounts](#chapter-23-bind-mounts)
24. [Volume Drivers](#chapter-24-volume-drivers)

### Part VI — Docker Compose
25. [Introduction to Docker Compose](#chapter-25-introduction-to-docker-compose)
26. [Compose File Structure](#chapter-26-compose-file-structure)
27. [Multi-Container Applications](#chapter-27-multi-container-applications)
28. [Compose Networking](#chapter-28-compose-networking)
29. [Compose Volumes and Environment](#chapter-29-compose-volumes-and-environment)
30. [Compose Commands Reference](#chapter-30-compose-commands-reference)

### Part VII — Registry and Distribution
31. [Docker Hub](#chapter-31-docker-hub)
32. [Pushing and Pulling Images](#chapter-32-pushing-and-pulling-images)
33. [Private Registries](#chapter-33-private-registries)
34. [Image Tagging Strategies](#chapter-34-image-tagging-strategies)

### Part VIII — Intermediate Concepts
35. [Docker Security Basics](#chapter-35-docker-security-basics)
36. [Resource Limits and Constraints](#chapter-36-resource-limits-and-constraints)
37. [Health Checks](#chapter-37-health-checks)
38. [Logging and Monitoring](#chapter-38-logging-and-monitoring)
39. [Docker in CI/CD](#chapter-39-docker-in-cicd)
40. [Introduction to Docker Swarm](#chapter-40-introduction-to-docker-swarm)

### Part IX — Interview Preparation
41. [Top 50 Docker Interview Questions](#chapter-41-top-50-docker-interview-questions)
42. [Common Scenarios and Answers](#chapter-42-common-scenarios-and-answers)
43. [Quick Reference Cheat Sheet](#chapter-43-quick-reference-cheat-sheet)

---

# PART I — FOUNDATIONS

---

## Chapter 1: What is Docker?

Docker is an open-source platform that enables developers to package applications and their dependencies into lightweight, portable units called **containers**. These containers can run consistently across any environment — from a developer's laptop to a production cloud server.

### The Problem Docker Solves

Before Docker, the classic development problem was: *"It works on my machine."* This happened because:

- Different operating systems handle libraries differently
- Software versions conflict between projects
- Setting up development environments was slow and error-prone
- Staging and production environments rarely matched development

Docker solves this by bundling your application with everything it needs — runtime, libraries, configuration — into a single container that behaves identically everywhere.

### Key Docker Concepts at a Glance

| Concept | Description |
|---|---|
| **Image** | A read-only template used to create containers |
| **Container** | A running instance of an image |
| **Dockerfile** | A script of instructions to build an image |
| **Registry** | A storage system for Docker images (e.g. Docker Hub) |
| **Volume** | Persistent storage for containers |
| **Network** | Communication layer between containers |

### Why Docker Matters in 2024

- Used in over **80% of production deployments** via Kubernetes or similar
- Core skill for DevOps, backend, and full-stack roles
- Foundation for microservices architecture
- Enables reproducible builds in CI/CD pipelines

### Interview Tip
> When asked "What is Docker?", always mention: **portability**, **isolation**, and **efficiency**. Say: *"Docker packages applications into containers that run consistently across environments, solving the 'works on my machine' problem."*

---

## Chapter 2: Virtualization vs Containerization

This is one of the **most common interview questions**. You must be able to explain this clearly.

### Virtual Machines (VMs)

A Virtual Machine runs a complete OS on top of a **hypervisor** (like VMware or VirtualBox). Each VM includes:
- Full OS kernel
- System libraries
- Application code

```
┌────────────────────────────────────┐
│         App A        │   App B     │
│  Bins/Libs  │  Bins/Libs           │
│   Guest OS  │  Guest OS            │
│─────────────────────────────────── │
│           Hypervisor               │
│           Host OS                  │
│           Hardware                 │
└────────────────────────────────────┘
```

**Pros of VMs:**
- Strong isolation — each VM has its own kernel
- Can run different OS types on same host

**Cons of VMs:**
- Large footprint — GBs per VM
- Slow to start (minutes)
- High resource overhead

### Containers

Containers share the **host OS kernel** but are isolated at the process level using Linux features: **namespaces** and **cgroups**.

```
┌────────────────────────────────────┐
│   Container A  │   Container B     │
│  App + Libs    │  App + Libs       │
│─────────────────────────────────── │
│         Docker Engine              │
│         Host OS Kernel             │
│         Hardware                   │
└────────────────────────────────────┘
```

**Pros of Containers:**
- Lightweight — MBs, not GBs
- Start in seconds or milliseconds
- High density — run many more containers than VMs on same hardware
- Consistent environments

**Cons of Containers:**
- Weaker isolation than VMs (shared kernel)
- Linux containers cannot run natively on Windows without a shim layer

### Side-by-Side Comparison

| Feature | VM | Container |
|---|---|---|
| Startup time | Minutes | Seconds/ms |
| Size | GBs | MBs |
| OS | Full OS per VM | Shared host kernel |
| Isolation | Strong (hypervisor) | Process-level |
| Portability | Limited | High |
| Resource usage | High | Low |

### The Two Linux Technologies Under the Hood

**Namespaces** — provide isolation for:
- `pid` — process IDs
- `net` — network interfaces
- `mnt` — mount points
- `uts` — hostname
- `ipc` — inter-process communication
- `user` — user IDs

**cgroups (control groups)** — limit and measure resources:
- CPU usage
- Memory
- Disk I/O
- Network bandwidth

### Interview Tip
> The key answer: *"Containers share the host OS kernel using namespaces and cgroups, making them faster and lighter than VMs which each require a full OS."*

---

## Chapter 3: Docker Architecture

Docker uses a **client-server architecture**.

### Components

**Docker Client (`docker`)**
The CLI tool you use to interact with Docker. When you type `docker run`, the client sends this command to the Docker daemon.

**Docker Daemon (`dockerd`)**
The background service that manages Docker objects — images, containers, networks, volumes. It listens for Docker API requests.

**Docker REST API**
The interface between the client and daemon. You can also call this API directly from applications.

**Docker Registry**
Stores Docker images. Docker Hub is the default public registry.

### Architecture Diagram

```
Docker Client               Docker Host              Docker Registry
─────────────               ───────────              ───────────────
docker build  ──────────►  Docker Daemon  ◄────────► Docker Hub
docker pull                  │                       Private Registry
docker run                   ├── Containers
                             ├── Images
                             ├── Networks
                             └── Volumes
```

### How `docker run nginx` Works

1. You type: `docker run nginx`
2. Docker client sends request to Docker daemon
3. Daemon checks if `nginx` image exists locally
4. If not, daemon pulls it from Docker Hub registry
5. Daemon creates a container from the image
6. Daemon starts the container

### containerd and runc

Modern Docker is built on layered components:

- **containerd** — high-level container runtime managing lifecycle
- **runc** — low-level runtime that actually creates and runs containers using Linux kernel features
- **Docker Engine** — ties everything together with the Docker API

This matters in interviews because Kubernetes can use `containerd` directly, bypassing Docker entirely.

### Interview Tip
> Know that Docker is a client-server architecture. The **daemon** does the work, the **client** sends commands, and the **registry** stores images.

---

## Chapter 4: Installing Docker

### Docker Desktop (Mac/Windows)

Docker Desktop installs the Docker Engine inside a lightweight Linux VM, giving you the full Docker experience on non-Linux systems.

```bash
# Verify installation
docker --version
# Docker version 24.0.5, build ced0996

docker info
# Shows system-wide information

docker run hello-world
# Tests that installation works
```

### Linux Installation (Ubuntu)

```bash
# Update package index
sudo apt-get update

# Install prerequisites
sudo apt-get install ca-certificates curl gnupg

# Add Docker's GPG key
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Add Docker repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Run Docker without sudo
sudo usermod -aG docker $USER
newgrp docker
```

### Key Commands to Verify

```bash
docker version          # Client and server versions
docker info             # System info (containers running, storage driver, etc.)
docker system df        # Disk usage by Docker objects
```

---

## Chapter 5: Your First Container

### Running Hello World

```bash
docker run hello-world
```

What happens:
1. Docker looks for `hello-world` image locally — not found
2. Pulls `hello-world:latest` from Docker Hub
3. Creates a container
4. Runs it — prints a message
5. Container exits

### Running an Interactive Container

```bash
# Run Ubuntu interactively
docker run -it ubuntu bash

# Inside the container:
ls /
cat /etc/os-release
exit
```

- `-i` — interactive (keep STDIN open)
- `-t` — allocate a pseudo-TTY

### Running a Web Server

```bash
# Run nginx in the background
docker run -d -p 8080:80 nginx

# -d = detached (background)
# -p 8080:80 = map host port 8080 to container port 80
```

Visit `http://localhost:8080` to see the nginx welcome page.

### Basic Container Management

```bash
docker ps                    # List running containers
docker ps -a                 # List all containers (including stopped)
docker stop <id>             # Stop a container
docker start <id>            # Start a stopped container
docker restart <id>          # Restart a container
docker rm <id>               # Remove a stopped container
docker rm -f <id>            # Force remove (even if running)
docker logs <id>             # View container logs
docker exec -it <id> bash    # Open shell in running container
```

---

# PART II — IMAGES

---

## Chapter 6: Understanding Docker Images

A Docker image is a **read-only template** that contains everything needed to run an application: code, runtime, libraries, environment variables, and config files.

### Image Layers

Images are built in **layers** — each instruction in a Dockerfile creates a new layer. Layers are:
- **Read-only** — never modified after creation
- **Cached** — reused across builds and images
- **Stacked** — each layer sits on top of previous ones

```
┌─────────────────────────┐  <- Writable layer (container)
├─────────────────────────┤  <- Layer 4: COPY app files
├─────────────────────────┤  <- Layer 3: RUN npm install
├─────────────────────────┤  <- Layer 2: COPY package.json
├─────────────────────────┤  <- Layer 1: FROM node:18
└─────────────────────────┘  <- Base layer
```

### Inspecting Images

```bash
docker images               # List local images
docker image ls             # Same thing
docker inspect nginx        # Detailed JSON info about image
docker history nginx        # Show layers of an image
docker image rm nginx       # Remove an image
docker rmi nginx            # Shorthand for remove image
```

### Image Naming and Tags

Format: `registry/repository:tag`

Examples:
```
nginx                       # = docker.io/library/nginx:latest
nginx:1.25                  # specific version
myuser/myapp:v1.0           # user's image on Docker Hub
gcr.io/myproject/myapp:prod # Google Container Registry
```

Always use **specific tags** in production — never `latest` in Dockerfiles for production images.

### Union File System

Docker images use a Union File System (OverlayFS on most Linux systems). When a container runs:
- All image layers remain **read-only**
- A thin **writable layer** is added on top
- Changes go into this writable layer
- When container is deleted, writable layer is lost

This is why you need **volumes** for persistent data.

---

## Chapter 7: Writing Dockerfiles

A Dockerfile is a text file containing instructions to build a Docker image.

### Basic Dockerfile Example

```dockerfile
# Use an official Python runtime as base image
FROM python:3.11-slim

# Set working directory inside container
WORKDIR /app

# Copy dependency files first (for caching)
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application source code
COPY . .

# Expose the port the app runs on
EXPOSE 5000

# Command to run when container starts
CMD ["python", "app.py"]
```

### Dockerfile Instructions Reference

| Instruction | Purpose | Example |
|---|---|---|
| `FROM` | Base image | `FROM node:18-alpine` |
| `WORKDIR` | Set working directory | `WORKDIR /app` |
| `COPY` | Copy files from host | `COPY . .` |
| `ADD` | Copy + extract archives | `ADD app.tar.gz /app` |
| `RUN` | Execute command during build | `RUN npm install` |
| `CMD` | Default command at runtime | `CMD ["node", "server.js"]` |
| `ENTRYPOINT` | Fixed entry command | `ENTRYPOINT ["nginx", "-g"]` |
| `EXPOSE` | Document port | `EXPOSE 8080` |
| `ENV` | Set environment variable | `ENV NODE_ENV=production` |
| `ARG` | Build-time variable | `ARG VERSION=1.0` |
| `VOLUME` | Declare mount point | `VOLUME /data` |
| `USER` | Set user to run as | `USER node` |
| `LABEL` | Add metadata | `LABEL version="1.0"` |

### CMD vs ENTRYPOINT

This is a **very common interview question**.

**CMD** — default command, easily overridden:
```dockerfile
CMD ["nginx", "-g", "daemon off;"]
# Override: docker run myimage echo hello
```

**ENTRYPOINT** — fixed command, arguments appended:
```dockerfile
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]
# docker run myimage -t  =>  runs: nginx -t
```

**Rule of thumb:**
- Use `ENTRYPOINT` when the container has a single, fixed purpose
- Use `CMD` for default arguments that can be overridden
- Use both together for containers that work like executables

### RUN vs CMD vs ENTRYPOINT

| | `RUN` | `CMD` | `ENTRYPOINT` |
|---|---|---|---|
| **When** | Build time | Run time (default) | Run time (always) |
| **Purpose** | Install/setup | Default arguments | Main process |
| **Overridable** | N/A | Yes (`docker run img cmd`) | Partially (`--entrypoint`) |

### Shell Form vs Exec Form

```dockerfile
# Shell form — runs via /bin/sh -c
RUN apt-get install -y curl
CMD python app.py

# Exec form — runs directly, preferred
RUN ["apt-get", "install", "-y", "curl"]
CMD ["python", "app.py"]
```

**Prefer exec form** for CMD and ENTRYPOINT — it receives signals correctly (important for graceful shutdown).

### Building an Image

```bash
# Build from Dockerfile in current directory
docker build -t myapp:1.0 .

# Build with a different Dockerfile
docker build -f Dockerfile.prod -t myapp:prod .

# Build with build arguments
docker build --build-arg VERSION=2.0 -t myapp:2.0 .

# No cache rebuild
docker build --no-cache -t myapp:fresh .
```

---

## Chapter 8: Image Layers and Caching

Understanding layer caching is critical for writing efficient Dockerfiles.

### How Caching Works

Docker caches each layer. If nothing has changed in a layer, Docker reuses the cached version instead of rebuilding it.

**Cache invalidation rule:** Once a layer changes, all subsequent layers are rebuilt.

### Bad Dockerfile (Cache-unfriendly)

```dockerfile
FROM node:18
WORKDIR /app
COPY . .              # Copies everything — any change invalidates next layer
RUN npm install       # Reinstalls all packages every time any file changes
EXPOSE 3000
CMD ["node", "server.js"]
```

### Good Dockerfile (Cache-optimized)

```dockerfile
FROM node:18
WORKDIR /app
COPY package*.json .  # Only copy dependency files first
RUN npm install       # This layer only rebuilds when package.json changes
COPY . .              # Source code changes don't affect the install layer
EXPOSE 3000
CMD ["node", "server.js"]
```

**Key principle:** Put instructions that change least frequently first. Put instructions that change most frequently last.

### Layer Caching Order (from least to most changing)

1. Base image (`FROM`)
2. System packages (`RUN apt-get install`)
3. Dependency files and install (`COPY package.json` + `RUN npm install`)
4. Application source (`COPY . .`)
5. Build step (`RUN npm run build`)

### Inspecting Layers

```bash
# View layer history and sizes
docker history myapp:1.0

# Detailed layer inspection
docker inspect myapp:1.0 | jq '.[0].RootFS.Layers'
```

### .dockerignore

Like `.gitignore` for Docker — prevents files from being sent to the build context:

```
# .dockerignore
node_modules
.git
*.log
.env
dist
coverage
.DS_Store
```

Without `.dockerignore`, `COPY . .` would copy everything including `node_modules` (often hundreds of MB).

---

## Chapter 9: Multi-Stage Builds

Multi-stage builds allow you to use multiple `FROM` statements in one Dockerfile, keeping the final image lean.

### The Problem: Fat Build Images

A Go application might need Go compiler tools (700MB) just to produce a small 10MB binary. Without multi-stage builds, you'd ship the compiler too.

### Multi-Stage Build Example (Go)

```dockerfile
# Stage 1: Build
FROM golang:1.21 AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN go build -o main .

# Stage 2: Run (tiny final image)
FROM alpine:3.18
WORKDIR /app
COPY --from=builder /app/main .
EXPOSE 8080
CMD ["./main"]
```

Result: ~15MB image instead of ~700MB.

### Multi-Stage Build Example (Node.js)

```dockerfile
# Stage 1: Build React app
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Key Multi-Stage Syntax

```dockerfile
# Name a stage with AS
FROM node:18 AS dependencies

# Copy from a named stage
COPY --from=dependencies /app/node_modules ./node_modules

# Copy from an external image
COPY --from=nginx:alpine /etc/nginx/nginx.conf /etc/nginx/nginx.conf

# Build a specific target stage
# docker build --target dependencies -t myapp:deps .
```

### Interview Tip
> *"Multi-stage builds separate the build environment from the runtime environment, dramatically reducing final image size and attack surface."*

---

## Chapter 10: Image Best Practices

### Use Official Images as Base

```dockerfile
# Good — official, maintained, minimal
FROM node:18-alpine
FROM python:3.11-slim
FROM ubuntu:22.04

# Avoid unless necessary
FROM ubuntu:latest     # 'latest' is unpredictable
```

### Minimize Layers

```dockerfile
# Bad — 3 layers for 3 RUN commands
RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y wget

# Good — 1 layer, cache cleaned in same layer
RUN apt-get update && \
    apt-get install -y \
      curl \
      wget && \
    rm -rf /var/lib/apt/lists/*
```

### Don't Run as Root

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY . .
RUN npm install

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

CMD ["node", "server.js"]
```

### Keep Images Small

- Use `alpine` variants (5MB base vs 70MB for debian)
- Use `slim` variants for Python/Ruby images
- Remove build tools after use
- Use multi-stage builds
- Clean package manager cache

### Scan Images for Vulnerabilities

```bash
docker scout cves myimage:latest  # Docker Scout
trivy image myimage:latest        # Trivy (open source)
```

---

# PART III — CONTAINERS

---

## Chapter 11: Container Lifecycle

A container can be in one of these states:

```
        docker run
             |
             v
    ┌─────────────────┐
    │    CREATED      │ <- docker create (not yet started)
    └────────┬────────┘
             │ docker start
             v
    ┌─────────────────┐
    │    RUNNING      │ <-- docker restart
    └────────┬────────┘
      ┌──────┼──────┐
      │      │      │
  pause    stop    kill
      │      │      │
      v      v      v
   PAUSED  EXITED  EXITED
             │
         docker rm
             │
           GONE
```

### Container Lifecycle Commands

```bash
# Create without starting
docker create --name mycontainer nginx

# Start a created/stopped container
docker start mycontainer

# Run (create + start)
docker run nginx

# Pause (freeze processes with SIGSTOP)
docker pause mycontainer

# Unpause
docker unpause mycontainer

# Stop (send SIGTERM, then SIGKILL after timeout)
docker stop mycontainer
docker stop -t 30 mycontainer   # 30 second timeout

# Kill (send SIGKILL immediately)
docker kill mycontainer

# Remove stopped container
docker rm mycontainer

# Remove all stopped containers
docker container prune
```

### Container Exit Codes

| Code | Meaning |
|---|---|
| 0 | Success — container exited normally |
| 1 | General error in application |
| 126 | Command not executable |
| 127 | Command not found |
| 130 | Container killed with Ctrl+C (SIGINT) |
| 137 | Container killed with SIGKILL (docker kill or OOM) |
| 143 | Container terminated with SIGTERM (docker stop) |

---

## Chapter 12: Running Containers

### docker run Options

```bash
docker run [OPTIONS] IMAGE [COMMAND] [ARGS]
```

**Common Options:**

```bash
# Naming
docker run --name web nginx

# Detached mode
docker run -d nginx

# Interactive + TTY
docker run -it ubuntu bash

# Port mapping
docker run -p 8080:80 nginx          # host:container
docker run -p 127.0.0.1:8080:80 nginx  # bind to specific interface
docker run -P nginx                   # auto-map all EXPOSE ports

# Environment variables
docker run -e DB_HOST=localhost -e DB_PORT=5432 myapp
docker run --env-file .env myapp

# Volume mounts
docker run -v myvolume:/data myapp
docker run -v /host/path:/container/path myapp
docker run --mount type=bind,source=/host,target=/container myapp

# Network
docker run --network mynetwork myapp

# Resource limits
docker run --memory 512m --cpus 1.5 myapp

# Restart policy
docker run --restart always nginx
docker run --restart on-failure:3 myapp  # retry 3 times

# Remove on exit
docker run --rm myapp

# Run as user
docker run --user 1000:1000 myapp

# Working directory
docker run -w /app myapp
```

### Executing Commands in Running Containers

```bash
# Open a bash shell
docker exec -it mycontainer bash

# Run a single command
docker exec mycontainer cat /etc/nginx/nginx.conf

# Run as root even if container runs as non-root
docker exec -u root -it mycontainer bash

# Copy files to/from containers
docker cp mycontainer:/app/logs/error.log ./error.log
docker cp ./config.json mycontainer:/app/config.json
```

### Monitoring Containers

```bash
# Real-time stats
docker stats
docker stats mycontainer

# Container logs
docker logs mycontainer
docker logs -f mycontainer         # Follow (like tail -f)
docker logs --tail 100 mycontainer # Last 100 lines
docker logs --since 1h mycontainer # Last 1 hour

# Processes inside container
docker top mycontainer

# Inspect container details
docker inspect mycontainer
docker inspect --format '{{.NetworkSettings.IPAddress}}' mycontainer
```

---

## Chapter 13: Container Networking Basics

By default, containers are isolated from the host and each other. Docker provides several networking modes to control this.

### Port Binding

```bash
# Map host port 8080 to container port 80
docker run -p 8080:80 nginx

# Map to specific host interface
docker run -p 127.0.0.1:8080:80 nginx  # localhost only

# Map all EXPOSE ports to random host ports
docker run -P nginx

# Check what ports are mapped
docker port mycontainer
```

### Container IP Address

Each container in a Docker network gets its own IP address.

```bash
# Get container IP
docker inspect mycontainer | grep IPAddress

# Or formatted
docker inspect --format '{{.NetworkSettings.IPAddress}}' mycontainer
```

---

## Chapter 14: Container Storage and Volumes

By default, container storage is **ephemeral** — data is lost when the container is removed. Volumes solve this.

### Three Storage Options

**1. Volumes** — managed by Docker
```bash
docker run -v myvolume:/app/data myapp
```

**2. Bind Mounts** — host directory mapped into container
```bash
docker run -v /host/path:/container/path myapp
```

**3. tmpfs** — stored in host memory only
```bash
docker run --tmpfs /tmp myapp
```

(Covered in detail in Part V)

---

## Chapter 15: Environment Variables and Configs

### Setting Environment Variables

```bash
# Single variable
docker run -e APP_PORT=3000 myapp

# Multiple variables
docker run -e DB_HOST=db -e DB_PORT=5432 -e DB_NAME=mydb myapp

# From a file
docker run --env-file .env myapp
```

### .env File Format

```bash
# .env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mydb
DB_USER=admin
SECRET_KEY=mysecretkey123
```

### Accessing in Dockerfile

```dockerfile
# Hardcoded (not recommended for secrets)
ENV NODE_ENV=production

# From build argument
ARG DB_HOST
ENV DB_HOST=$DB_HOST
```

### Build Args vs ENV

| Feature | ARG | ENV |
|---|---|---|
| Available during | Build only | Build + Runtime |
| Visible in image | Via `docker history` | Yes |
| Override with | `--build-arg` | `-e` flag |
| Use case | Build configuration | Runtime configuration |

**Never put secrets in ENV in Dockerfiles** — they're visible in `docker history`. Use Docker secrets or external secret management.

---

# PART IV — NETWORKING

---

## Chapter 16: Docker Network Types

Docker has five built-in network drivers:

| Driver | Description | Use Case |
|---|---|---|
| `bridge` | Default isolated network | Development, single host |
| `host` | Container uses host network | Performance-critical apps |
| `none` | No networking | Fully isolated containers |
| `overlay` | Multi-host networking | Docker Swarm clusters |
| `macvlan` | Container gets own MAC address | Legacy apps needing direct network access |

### Managing Networks

```bash
# List networks
docker network ls

# Create a network
docker network create mynetwork
docker network create --driver bridge mynetwork

# Inspect a network
docker network inspect mynetwork

# Connect a running container
docker network connect mynetwork mycontainer

# Disconnect
docker network disconnect mynetwork mycontainer

# Remove network
docker network rm mynetwork

# Remove unused networks
docker network prune
```

---

## Chapter 17: Bridge Networks

Bridge is the **default network driver**. When you run a container without specifying a network, it joins the default `bridge` network.

### Default Bridge vs User-Defined Bridge

| Feature | Default Bridge | User-Defined Bridge |
|---|---|---|
| DNS resolution | No (IP only) | Yes (by container name) |
| Isolation | All containers can talk | Only same network |
| Recommended | No | Yes |

### Creating a User-Defined Bridge

```bash
# Create
docker network create --driver bridge app-network

# Run containers on it
docker run -d --name db --network app-network postgres
docker run -d --name web --network app-network -p 8080:80 nginx

# web can now reach db at db:5432
# (DNS resolution by container name)
```

### Why User-Defined Bridges Are Better

1. **Automatic DNS** — containers find each other by name
2. **Better isolation** — only containers on same network communicate
3. **On-the-fly connection** — connect/disconnect without restart

---

## Chapter 18: Host and None Networks

### Host Network

Container shares the host's network stack directly — no isolation.

```bash
docker run --network host nginx
# nginx binds to port 80 on the host directly
# No -p needed (and -p is ignored)
```

**Use case:** Performance-critical scenarios where port translation overhead matters.

**Caution:** Any port the container uses is directly on the host — potential conflicts.

### None Network

Container has no network access.

```bash
docker run --network none myapp
```

**Use case:** Batch processing jobs that don't need network access, or security-sensitive workloads.

---

## Chapter 19: Container-to-Container Communication

### Same Network

Containers on the same user-defined network can communicate by name:

```bash
docker network create mynet
docker run -d --name db --network mynet postgres
docker run -d --name app --network mynet myapp
# app can connect to db at hostname "db"
```

### Linking (Legacy — Deprecated)

```bash
docker run --link db:database myapp
# Avoid this — use networks instead
```

### Multiple Networks

A container can be on multiple networks simultaneously:

```bash
docker network connect frontend-net myapp
docker network connect backend-net myapp
```

---

## Chapter 20: Port Binding and Exposure

### EXPOSE vs -p

**EXPOSE in Dockerfile:** Documentation only — tells Docker which port the app listens on. Does NOT publish the port.

```dockerfile
EXPOSE 3000    # Documents intent, does nothing at runtime alone
```

**-p at runtime:** Actually binds host port to container port.

```bash
docker run -p 3000:3000 myapp   # Now accessible from outside
```

### Port Mapping Formats

```bash
-p 8080:80              # host_port:container_port
-p 127.0.0.1:8080:80   # ip:host_port:container_port
-p 8080:80/udp          # UDP instead of TCP
-P                      # Auto-map all EXPOSE ports to random high ports
```

---

# PART V — VOLUMES AND STORAGE

---

## Chapter 21: Volumes vs Bind Mounts vs tmpfs

### When Container Data is Ephemeral

Every change inside a container goes into its **writable layer**. When the container is deleted, that layer is gone.

```bash
docker run --rm ubuntu bash -c "echo 'hello' > /data/test.txt"
# File is gone — container was removed
```

### The Three Persistence Options

**Volumes** — Docker manages the storage location
```bash
docker run -v myvolume:/app/data myapp
# Data lives at: /var/lib/docker/volumes/myvolume/_data
```

**Bind Mounts** — You specify exact host path
```bash
docker run -v /home/user/data:/app/data myapp
# Directly maps host directory
```

**tmpfs** — In-memory, never written to disk
```bash
docker run --tmpfs /app/temp myapp
```

### Comparison Table

| Feature | Volumes | Bind Mounts | tmpfs |
|---|---|---|---|
| Host location | Docker managed | User specified | Memory only |
| Portability | High | Low (path dependent) | N/A |
| Performance | Good | Good | Best |
| Persistence | Yes | Yes | No |
| Shared | Yes | Yes | No |
| Backup | Easy | Manual | N/A |
| Best for | Databases, app data | Dev, config files | Secrets, temp files |

---

## Chapter 22: Named Volumes

Named volumes are the **recommended way** to persist data in Docker.

### Creating and Using Volumes

```bash
# Create a volume explicitly
docker volume create mydata

# Use in a container
docker run -d -v mydata:/var/lib/mysql mysql

# Volume is automatically created if it doesn't exist
docker run -d -v dbdata:/var/lib/postgresql/data postgres

# List volumes
docker volume ls

# Inspect a volume
docker volume inspect mydata

# Remove a volume
docker volume rm mydata

# Remove all unused volumes
docker volume prune
```

### Backing Up a Volume

```bash
# Backup volume to tar file on host
docker run --rm \
  -v mydata:/source:ro \
  -v $(pwd):/backup \
  ubuntu tar czf /backup/mydata-backup.tar.gz -C /source .

# Restore
docker run --rm \
  -v mydata:/target \
  -v $(pwd):/backup \
  ubuntu tar xzf /backup/mydata-backup.tar.gz -C /target
```

---

## Chapter 23: Bind Mounts

Bind mounts map a host directory or file directly into a container.

### Common Use Cases

```bash
# Development — live code reloading
docker run -v $(pwd):/app node:18 npm start

# Configuration files
docker run -v /etc/myapp/config.yaml:/app/config.yaml myapp

# Logs — access from host
docker run -v /var/log/myapp:/app/logs myapp

# Read-only mount
docker run -v /host/config:/app/config:ro myapp
```

### Modern Syntax: --mount

```bash
# -v shorthand (older)
docker run -v /host/path:/container/path myapp

# --mount (explicit, recommended)
docker run --mount type=bind,source=/host/path,target=/container/path myapp

# With options
docker run --mount type=bind,source=/host/path,target=/container/path,readonly myapp
```

### Security Consideration

Bind mounts give the container full access to the host path. Be careful:
- Not mounting sensitive host directories
- Using `:ro` when write access isn't needed
- Containers running as root can modify host files

---

## Chapter 24: Volume Drivers

The default volume driver is `local` — data stored on the host filesystem. Other drivers enable shared/cloud storage:

```bash
# NFS volume
docker volume create --driver local \
  --opt type=nfs \
  --opt o=addr=192.168.1.100,rw \
  --opt device=:/data/shared \
  nfs-volume
```

Enterprise plugins: `rexray/ebs` (AWS), Azure File Storage driver, Google Persistent Disk driver.

---

# PART VI — DOCKER COMPOSE

---

## Chapter 25: Introduction to Docker Compose

Docker Compose is a tool for defining and running **multi-container applications** using a YAML file.

### Why Compose?

Running a typical web app requires multiple containers:
- Application server
- Database
- Cache (Redis)
- Reverse proxy (nginx)
- Background workers

Starting each manually with `docker run` is tedious and error-prone. Compose manages them all together.

### Installation

Docker Compose v2 is bundled with Docker Desktop. On Linux:

```bash
# Included with Docker Engine as plugin
docker compose version
# Docker Compose version v2.21.0
```

Note: The older `docker-compose` (v1) is a separate binary. The new `docker compose` (v2) is a plugin — note the space, not hyphen.

---

## Chapter 26: Compose File Structure

### Basic Structure

```yaml
# docker-compose.yml
version: '3.9'    # Compose file format version

services:         # Container definitions
  web:
    image: nginx
    ports:
      - "8080:80"

  app:
    build: .
    environment:
      - NODE_ENV=production

  db:
    image: postgres:15
    volumes:
      - dbdata:/var/lib/postgresql/data

volumes:          # Named volume definitions
  dbdata:

networks:         # Custom network definitions
  default:
    driver: bridge
```

### Full Service Definition

```yaml
services:
  app:
    image: myapp:latest
    # OR build from Dockerfile:
    build:
      context: ./app
      dockerfile: Dockerfile.prod
      args:
        NODE_ENV: production

    container_name: myapp-container

    ports:
      - "3000:3000"
      - "127.0.0.1:3001:3001"

    environment:
      NODE_ENV: production
      DB_HOST: db
    env_file:
      - .env

    volumes:
      - ./src:/app/src
      - appdata:/app/data

    networks:
      - frontend
      - backend

    depends_on:
      db:
        condition: service_healthy

    restart: unless-stopped

    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

---

## Chapter 27: Multi-Container Applications

### Real-World Example: Node.js + PostgreSQL + Redis

```yaml
version: '3.9'

services:
  app:
    build: .
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_NAME: myapp
      DB_USER: postgres
      DB_PASSWORD: secret
      REDIS_URL: redis://cache:6379
    depends_on:
      db:
        condition: service_healthy
      cache:
        condition: service_started
    networks:
      - app-network

  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network

  cache:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redisdata:/data
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - app
    networks:
      - app-network

volumes:
  pgdata:
  redisdata:

networks:
  app-network:
    driver: bridge
```

---

## Chapter 28: Compose Networking

By default, Compose creates a **single network** for all services. Services reach each other using their **service name** as hostname.

```yaml
services:
  web:
    image: nginx
    # Can reach 'app' at http://app:3000

  app:
    image: myapp
    # Can reach 'db' at postgresql://db:5432

  db:
    image: postgres
```

### Multiple Networks for Isolation

```yaml
services:
  frontend:
    networks:
      - public

  backend:
    networks:
      - public
      - private

  database:
    networks:
      - private     # Only accessible to backend

networks:
  public:
  private:
    internal: true  # No external access
```

---

## Chapter 29: Compose Volumes and Environment

### Variable Substitution

```yaml
services:
  app:
    image: myapp:${APP_VERSION:-latest}
    ports:
      - "${HOST_PORT:-3000}:3000"
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
```

```bash
# .env file (automatically loaded by Compose)
APP_VERSION=1.2.3
HOST_PORT=8080
DB_PASSWORD=supersecret
```

### Override Files

```bash
# Apply multiple files — later files override earlier
docker compose -f docker-compose.yml -f docker-compose.prod.yml up
```

```yaml
# docker-compose.override.yml (auto-applied in dev)
services:
  app:
    volumes:
      - ./src:/app/src   # live reload in dev only
    environment:
      - DEBUG=true
```

---

## Chapter 30: Compose Commands Reference

```bash
# Start all services
docker compose up

# Start in background
docker compose up -d

# Build images before starting
docker compose up --build

# Stop and remove containers
docker compose down

# Stop and remove containers + volumes
docker compose down -v

# View running services
docker compose ps

# View logs
docker compose logs
docker compose logs -f app

# Run command in a service container
docker compose exec app bash
docker compose exec db psql -U postgres

# Scale a service
docker compose up --scale app=3

# Restart a service
docker compose restart app

# Validate compose file
docker compose config

# Pull images
docker compose pull

# Build images only
docker compose build
```

---

# PART VII — REGISTRY AND DISTRIBUTION

---

## Chapter 31: Docker Hub

Docker Hub is the default public registry for Docker images.

### Key Concepts

- **Official Images** — maintained by Docker and trusted vendors (nginx, postgres, node)
- **Verified Publisher** — commercial software from trusted companies
- **Community Images** — user-contributed (use with caution)

### Docker Hub Naming

```
docker.io/library/nginx:1.25     # Official image (shorthand: nginx:1.25)
docker.io/username/myapp:latest  # User image (shorthand: username/myapp:latest)
```

---

## Chapter 32: Pushing and Pulling Images

### Pulling Images

```bash
# Pull latest
docker pull nginx

# Pull specific version
docker pull nginx:1.25-alpine

# Pull from different registry
docker pull gcr.io/google-containers/pause:3.1
```

### Pushing to Docker Hub

```bash
# Login
docker login

# Tag your image with your username
docker tag myapp:latest username/myapp:1.0
docker tag myapp:latest username/myapp:latest

# Push
docker push username/myapp:1.0
docker push username/myapp:latest

# Logout
docker logout
```

### Image Digest (Immutable Reference)

```bash
# Pull by digest (guaranteed to be the exact same image)
docker pull nginx@sha256:abc123...

# Get digest of local image
docker inspect --format='{{index .RepoDigests 0}}' nginx
```

---

## Chapter 33: Private Registries

### Running a Local Registry

```bash
# Start a local registry
docker run -d -p 5000:5000 --name registry registry:2

# Tag for local registry
docker tag myapp:latest localhost:5000/myapp:latest

# Push to local registry
docker push localhost:5000/myapp:latest

# Pull from local registry
docker pull localhost:5000/myapp:latest
```

### Common Enterprise Registries

| Registry | Provider |
|---|---|
| Amazon ECR | AWS |
| Google Artifact Registry | GCP |
| Azure Container Registry | Azure |
| GitHub Container Registry | GitHub |
| GitLab Registry | GitLab |
| Harbor | Open source, self-hosted |

### Logging into a Private Registry

```bash
# AWS ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

# Generic
docker login registry.example.com
```

---

## Chapter 34: Image Tagging Strategies

### Semantic Versioning

```bash
docker tag myapp:1.2.3 myapp:1.2
docker tag myapp:1.2.3 myapp:1
docker tag myapp:1.2.3 myapp:latest
```

### Git-based Tagging

```bash
GIT_SHA=$(git rev-parse --short HEAD)
docker build -t myapp:$GIT_SHA .
docker tag myapp:$GIT_SHA myapp:latest
```

### Environment Tags

```bash
docker tag myapp:1.2.3 myapp:staging
docker tag myapp:1.2.3 myapp:production
```

---

# PART VIII — INTERMEDIATE CONCEPTS

---

## Chapter 35: Docker Security Basics

### Principle of Least Privilege

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY . .
RUN npm ci --production

RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app

USER appuser
CMD ["node", "server.js"]
```

### Read-Only Container Filesystem

```bash
# Mount filesystem as read-only
docker run --read-only myapp

# Allow specific writable paths
docker run --read-only --tmpfs /tmp --tmpfs /var/run myapp
```

### Capabilities

Linux capabilities give fine-grained control over kernel privileges:

```bash
# Drop all capabilities, add only needed ones
docker run --cap-drop ALL --cap-add NET_BIND_SERVICE nginx

# Key capabilities:
# CAP_NET_BIND_SERVICE — bind ports below 1024
# CAP_NET_ADMIN — configure network
# CAP_SYS_ADMIN — various sys admin tasks (avoid!)
# CAP_CHOWN — change file ownership
```

### Secrets Management

```bash
# Docker secrets (Swarm mode)
echo "mysecretpassword" | docker secret create db_password -

# Access in container at /run/secrets/db_password
docker service create \
  --secret db_password \
  myapp
```

**Rule:** Never put secrets in ENV in Dockerfiles — they appear in `docker history`. Use Docker secrets, Kubernetes secrets, or external vault solutions.

---

## Chapter 36: Resource Limits and Constraints

### Memory Limits

```bash
# Limit container to 512MB RAM
docker run --memory 512m myapp

# Also set swap limit (total = memory + swap)
docker run --memory 512m --memory-swap 1g myapp

# Disable swap
docker run --memory 512m --memory-swap 512m myapp
```

### CPU Limits

```bash
# Limit to 1.5 CPUs
docker run --cpus 1.5 myapp

# CPU shares (relative weight, default 1024)
docker run --cpu-shares 512 myapp   # Half the default weight

# Bind to specific CPUs
docker run --cpuset-cpus "0,2" myapp    # Use CPU 0 and 2
docker run --cpuset-cpus "0-3" myapp    # Use CPUs 0 through 3
```

### Viewing Resource Usage

```bash
# Real-time stats
docker stats

# One-shot stats for specific container
docker stats mycontainer --no-stream

# Output format:
# CONTAINER  CPU %  MEM USAGE / LIMIT  MEM %  NET I/O  BLOCK I/O
```

---

## Chapter 37: Health Checks

Health checks tell Docker whether a container is actually working, not just running.

### In Dockerfile

```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1
```

### At Runtime

```bash
docker run \
  --health-cmd="curl -f http://localhost:8080/health || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  myapp
```

### Health States

- `starting` — container just started, health check hasn't run yet
- `healthy` — health check passes
- `unhealthy` — health check failed enough times

```bash
# View health status
docker ps        # Shows (healthy) or (unhealthy) in STATUS column
docker inspect --format='{{.State.Health.Status}}' mycontainer
```

### In Docker Compose

```yaml
services:
  db:
    image: postgres
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    depends_on:
      db:
        condition: service_healthy  # Wait for DB to be healthy
```

---

## Chapter 38: Logging and Monitoring

### Docker Logging Drivers

| Driver | Description |
|---|---|
| `json-file` | Default, logs to JSON files on host |
| `syslog` | Writes to syslog |
| `journald` | Uses systemd journal |
| `gelf` | Graylog Extended Log Format |
| `fluentd` | Fluentd aggregation |
| `awslogs` | Amazon CloudWatch Logs |
| `splunk` | Splunk HTTP Event Collector |
| `none` | Disables logging |

### Configuring Logging

```bash
# Set for a single container
docker run \
  --log-driver json-file \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  myapp

# Configure globally in /etc/docker/daemon.json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

### Viewing Logs

```bash
docker logs mycontainer           # All logs
docker logs -f mycontainer        # Follow
docker logs --tail 50 mycontainer # Last 50 lines
docker logs --since 2h mycontainer
docker logs -t mycontainer        # Include timestamps
```

---

## Chapter 39: Docker in CI/CD

Docker is central to modern CI/CD pipelines.

### Typical Pipeline

```
Developer pushes code
        |
        v
CI Server (GitHub Actions / Jenkins / GitLab CI)
        |
        +-- docker build -t myapp:$SHA .
        +-- docker run tests
        +-- docker push myapp:$SHA
        +-- docker push myapp:staging
        |
        v
Staging Environment
        |
        v
Production (after approval)
```

### GitHub Actions Example

```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            username/myapp:latest
            username/myapp:${{ github.sha }}
```

---

## Chapter 40: Introduction to Docker Swarm

Docker Swarm is Docker's native container orchestration system — simpler alternative to Kubernetes.

### Key Concepts

| Concept | Description |
|---|---|
| **Swarm** | A cluster of Docker nodes |
| **Node** | A Docker host in the swarm |
| **Manager** | Orchestrates the swarm, maintains desired state |
| **Worker** | Runs containers |
| **Service** | Desired state declaration (image + replicas + config) |
| **Task** | A single running container in a service |
| **Stack** | Multi-service app defined in a Compose file |

### Setting Up a Swarm

```bash
# Initialize swarm on first manager
docker swarm init --advertise-addr 192.168.1.10

# Get worker join token
docker swarm join-token worker

# Join as worker (run on worker node)
docker swarm join --token SWMTKN-1-abc... 192.168.1.10:2377

# List nodes
docker node ls
```

### Deploying Services

```bash
# Deploy a service with 3 replicas
docker service create --name web --replicas 3 -p 80:80 nginx

# Scale service
docker service scale web=5

# Update service (rolling update)
docker service update --image nginx:1.25 web

# List services
docker service ls

# List tasks (containers) in a service
docker service ps web

# Remove service
docker service rm web
```

### Deploying a Stack

```bash
# Deploy multi-service app from Compose file
docker stack deploy -c docker-compose.yml mystack

# List stacks
docker stack ls

# List services in stack
docker stack services mystack

# Remove stack
docker stack rm mystack
```

### Swarm vs Kubernetes

| Feature | Swarm | Kubernetes |
|---|---|---|
| Complexity | Simple | Complex |
| Learning curve | Low | High |
| Features | Basic | Extensive |
| Auto-scaling | No | Yes |
| Self-healing | Yes | Yes |
| Ecosystem | Smaller | Huge |
| Use case | Small/medium | Large scale |

---

# PART IX — INTERVIEW PREPARATION

---

## Chapter 41: Top 50 Docker Interview Questions

### Beginner Questions

**Q1. What is Docker?**
Docker is an open-source platform for developing, shipping, and running applications in containers. Containers package code and dependencies together, ensuring consistent behavior across environments.

**Q2. What is a container?**
A container is a lightweight, isolated runtime environment that packages an application with its dependencies. It shares the host OS kernel but uses namespaces and cgroups for isolation.

**Q3. What is the difference between a container and a virtual machine?**
VMs run a full OS on a hypervisor (GBs, minutes to start). Containers share the host kernel and use OS-level isolation (MBs, seconds to start). Containers are faster and more efficient; VMs offer stronger isolation.

**Q4. What is a Docker image?**
A Docker image is a read-only template containing the application code, runtime, libraries, and configurations needed to create a container. Images are built in layers.

**Q5. What is a Dockerfile?**
A Dockerfile is a text script containing instructions for building a Docker image. Each instruction creates a new read-only layer.

**Q6. What is Docker Hub?**
Docker Hub is the default public container registry where Docker images are stored and shared. It hosts official images (nginx, postgres, node) and user-contributed images.

**Q7. What is the difference between CMD and ENTRYPOINT?**
`CMD` provides default commands that can be overridden at `docker run`. `ENTRYPOINT` defines the main process that always runs; CMD then provides default arguments to it. Use ENTRYPOINT for containers with a fixed purpose.

**Q8. What is a Docker volume?**
A Docker volume is a mechanism for persisting data generated by containers. Volume data survives container restarts and deletion, unlike the container's writable layer.

**Q9. How do you list running containers?**
`docker ps` lists running containers. `docker ps -a` lists all containers including stopped ones.

**Q10. What does `docker run -d` do?**
The `-d` flag runs the container in detached (background) mode. The container runs as a background process and the terminal is freed.

---

### Intermediate Questions

**Q11. Explain Docker networking.**
Docker has five network drivers: bridge (default, isolated containers on same host), host (container uses host network), none (no network), overlay (multi-host), and macvlan. User-defined bridge networks provide DNS resolution by container name.

**Q12. What is Docker Compose?**
Docker Compose is a tool for defining and running multi-container applications using a `docker-compose.yml` file. It manages multiple services, networks, and volumes as a single application.

**Q13. What is a multi-stage build?**
A multi-stage build uses multiple FROM instructions in one Dockerfile. Earlier stages handle building/compiling; only necessary artifacts are copied to the final minimal stage, dramatically reducing image size.

**Q14. What is the difference between COPY and ADD in a Dockerfile?**
`COPY` simply copies files from host to container. `ADD` can also extract tar archives and download from URLs. Prefer `COPY` unless you specifically need ADD's extra features.

**Q15. What is the `.dockerignore` file?**
A `.dockerignore` file excludes files and directories from the Docker build context. It prevents large or sensitive files (like `node_modules`, `.git`, `.env`) from being sent to the daemon during builds.

**Q16. How do layers and caching work in Docker?**
Each Dockerfile instruction creates a layer. Docker caches layers and reuses them if nothing changed. When a layer changes, all subsequent layers are rebuilt. Order instructions from least-to-most frequently changing to maximize cache effectiveness.

**Q17. What is the difference between `docker stop` and `docker kill`?**
`docker stop` sends SIGTERM allowing graceful shutdown (default 10s timeout, then SIGKILL). `docker kill` immediately sends SIGKILL. Use `stop` for graceful shutdown.

**Q18. How do you pass environment variables to a container?**
Using `-e VAR=value` for individual variables, `--env-file filename` for a file of variables, or `ENV` in Dockerfile for build-time defaults.

**Q19. What is `docker exec`?**
`docker exec` runs a command inside a running container. Common use: `docker exec -it mycontainer bash` to open an interactive shell.

**Q20. How do you view Docker container logs?**
`docker logs mycontainer`. Use `-f` to follow, `--tail N` for last N lines, `--since` for time-based filtering.

**Q21. What is the difference between volumes and bind mounts?**
Volumes are managed by Docker (stored in Docker's directory), portable, and recommended for production. Bind mounts map a specific host path into a container, suitable for development.

**Q22. What is Docker's default network driver?**
The `bridge` driver is the default. Containers on the default bridge network can communicate by IP address. On user-defined bridge networks, they can also communicate by container name (DNS).

**Q23. How do you remove all stopped containers?**
`docker container prune`. Or: `docker rm $(docker ps -aq)`.

**Q24. How do you build a Docker image?**
`docker build -t imagename:tag .` — The `.` is the build context (current directory). Use `-f` to specify a different Dockerfile.

**Q25. What restart policies does Docker support?**
- `no` — never restart (default)
- `always` — always restart
- `on-failure` — restart only on non-zero exit code
- `unless-stopped` — always restart unless manually stopped

**Q26. What is `docker inspect`?**
Returns detailed low-level JSON information about Docker objects (containers, images, networks, volumes). Useful for debugging configuration issues.

**Q27. How do containers communicate in Docker Compose?**
Compose creates a default network. All services are on this network and can reach each other using the service name as hostname. Example: service `db` is reachable at `db:5432`.

**Q28. What is `depends_on` in Docker Compose?**
`depends_on` controls service startup order in Compose. However, it only waits for the container to start, not for the service inside to be ready. Use `condition: service_healthy` with health checks for true readiness.

**Q29. How do you scale services in Docker Compose?**
`docker compose up --scale servicename=N`. For Swarm: `docker service scale servicename=N`.

**Q30. What is the difference between `docker run` and `docker start`?**
`docker run` creates and starts a new container from an image. `docker start` starts an existing stopped container.

---

### Advanced Questions

**Q31. How does Docker achieve container isolation?**
Docker uses two Linux kernel features: **namespaces** (isolate PID, network, filesystem, users, hostname) and **cgroups** (limit and measure CPU, memory, disk I/O, network). Each container gets its own namespace for isolation.

**Q32. What is a Docker Registry?**
A registry stores Docker images. Docker Hub is the public default. Private registries (ECR, GCR, ACR, Harbor) are used in enterprise. Images are organized as repositories with version tags.

**Q33. Explain the Docker build context.**
The build context is the directory (or URL) whose files are sent to the Docker daemon when building. It's the last argument in `docker build`. Large contexts slow builds — use `.dockerignore` to exclude unnecessary files.

**Q34. What is the difference between `ARG` and `ENV` in Dockerfile?**
`ARG` is available only during build time (passed with `--build-arg`). `ENV` is available at both build and runtime. ARG values are visible in `docker history`; avoid using them for secrets.

**Q35. What is Docker Content Trust (DCT)?**
Docker Content Trust uses digital signatures to verify the integrity and publisher of Docker images. Enable it with `DOCKER_CONTENT_TRUST=1`. It prevents pulling unsigned images.

**Q36. What are Docker namespaces?**
Namespaces provide isolation: `pid` (process tree), `net` (network stack), `mnt` (filesystem mounts), `uts` (hostname), `ipc` (inter-process communication), `user` (user IDs). Each container runs in its own namespace set.

**Q37. What are cgroups?**
Control groups (cgroups) limit and monitor resource usage per process group. Docker uses them to enforce memory, CPU, and I/O limits on containers.

**Q38. What is the overlay filesystem?**
OverlayFS is the default storage driver. It overlays image layers (read-only) with a container's writable layer. Reads look through layers from top to bottom; writes go to the top writable layer (copy-on-write).

**Q39. How do you optimize Docker image size?**
Use Alpine base images, multi-stage builds, combine RUN commands (reducing layers), add a `.dockerignore`, remove package manager caches, avoid installing unnecessary packages, and run as non-root.

**Q40. What is Docker Swarm vs Kubernetes?**
Both orchestrate containers across multiple hosts. Swarm is simpler, built into Docker, easier to set up. Kubernetes is more complex but offers auto-scaling, richer ecosystem, better for large-scale production. Kubernetes is the industry standard for production.

**Q41. What is a Docker stack?**
A Docker stack is a multi-service application deployed to Swarm using a Compose file. `docker stack deploy -c docker-compose.yml mystack`. Stacks extend Compose for production Swarm deployments.

**Q42. What is the difference between `EXPOSE` and `-p`?**
`EXPOSE` in Dockerfile is documentation — it tells users which ports the app uses but doesn't publish them. `-p` at `docker run` actually binds a host port to the container port, making it accessible.

**Q43. How does Docker handle DNS?**
On user-defined networks, Docker runs an embedded DNS server. Containers resolve each other by service/container name. On the default bridge network, there's no DNS — containers must use IP addresses.

**Q44. What is a dangling image?**
Images with no name and no tag — shown as `<none>:<none>` in `docker images`. They're created when a new build replaces an existing tag. Remove with `docker image prune`.

**Q45. What is `docker system prune`?**
Removes all unused Docker objects: stopped containers, unused networks, dangling images, and build cache. Add `-a` to remove all unused images (not just dangling). Add `-f` to skip confirmation.

**Q46. How do you secure a Docker deployment?**
Run containers as non-root, use read-only filesystems where possible, drop Linux capabilities, scan images for vulnerabilities, use secrets management (not ENV for passwords), keep images updated, use network policies to restrict traffic.

**Q47. What is a Docker health check and why use it?**
A health check runs a command periodically to verify the container is working correctly. Orchestrators (Swarm, Kubernetes) use health status for routing traffic and restarting unhealthy containers. A container can be "running" but not "healthy" (e.g., web server running but returning 500 errors).

**Q48. What is the difference between `docker compose up` and `docker compose start`?**
`up` creates and starts containers from scratch (creating new containers if needed). `start` only starts existing stopped containers — it won't create new ones.

**Q49. What are the pros and cons of using Docker?**

**Pros:** Consistent environments across dev/staging/prod, fast startup compared to VMs, efficient resource usage, easy scaling and deployment, strong ecosystem, great for microservices.

**Cons:** Additional complexity layer, not ideal for GUI applications, learning curve, weaker isolation than VMs, stateful apps require careful volume management.

**Q50. What happens when you run `docker run hello-world`?**
1. Docker client sends request to Docker daemon
2. Daemon checks for `hello-world` image locally — not found
3. Daemon pulls `hello-world:latest` from Docker Hub
4. Daemon creates a container from the image
5. Container starts, runs its process (prints message), and exits
6. Output is streamed to the Docker client

---

## Chapter 42: Common Scenarios and Answers

### Scenario 1: "How would you containerize a Node.js app?"

*Strong answer:*
"First, I'd create a Dockerfile with `node:18-alpine` as the base — alpine for smaller size. I'd set a WORKDIR, copy `package*.json` first and run `npm ci` to leverage layer caching, then copy source code. I'd use `npm ci --production` to exclude dev dependencies. For a proper setup, I'd use a multi-stage build to keep the image lean, create a non-root user, add a `.dockerignore` to exclude `node_modules` and `.git`, and expose the app's port. Then I'd use Docker Compose locally to wire it up with a database."

### Scenario 2: "Our container keeps restarting. How do you debug it?"

*Strong answer:*
"First I'd run `docker ps -a` to see the exit code — that already tells a lot. Exit 137 means OOM kill; exit 1 means application error. Then `docker logs mycontainer` to read the application output. If the container starts briefly, I'd use `docker logs --tail 50`. I might also try `docker run -it --entrypoint bash myimage` to get a shell without running the main process, to inspect the filesystem. Check `docker inspect mycontainer` for resource limit hits and health check failures."

### Scenario 3: "Two containers need to share data. How?"

*Strong answer:*
"Use a named volume. Create it with `docker volume create shared-data`, then mount it in both containers at `docker run -v shared-data:/app/data`. Both containers read and write to the same underlying directory. In Docker Compose, define the volume under `volumes:` and reference it in both service definitions."

### Scenario 4: "How would you reduce a large Docker image size?"

*Strong answer:*
"Multi-stage builds first — separate the build environment from the runtime environment. Then switch to an Alpine base image. Next, consolidate `RUN` commands to reduce layer count and clean up package caches in the same layer. Add a thorough `.dockerignore` to reduce build context. Run `docker history` to identify which layers are bloated and focus there first."

### Scenario 5: "How do you handle secrets in Docker?"

*Strong answer:*
"I never put secrets in Dockerfiles, environment variables in the image, or images themselves — they show up in `docker history`. For local dev, I use a `.env` file with Docker Compose (and `.gitignore` it). For production, I'd use Docker Secrets in Swarm mode — secrets are encrypted at rest and in transit, mounted as files in the container. In Kubernetes, I'd use Kubernetes Secrets or an external secret manager like HashiCorp Vault or AWS Secrets Manager, injected at runtime."

---

## Chapter 43: Quick Reference Cheat Sheet

### Essential Commands

```bash
# Images
docker images                               # List images
docker pull nginx:alpine                    # Pull image
docker build -t myapp:1.0 .                 # Build image
docker push username/myapp:1.0              # Push to registry
docker rmi myapp:1.0                        # Remove image
docker image prune                          # Remove dangling images
docker image prune -a                       # Remove all unused images

# Containers
docker run -d -p 8080:80 --name web nginx   # Run detached
docker run -it ubuntu bash                  # Run interactive
docker run --rm myapp                       # Remove on exit
docker ps                                   # List running
docker ps -a                                # List all
docker stop web                             # Graceful stop
docker kill web                             # Force stop
docker rm web                               # Remove stopped
docker rm -f web                            # Force remove
docker exec -it web bash                    # Shell into container
docker logs -f web                          # Follow logs
docker inspect web                          # Detailed info
docker stats                                # Live resource usage
docker top web                              # Processes in container
docker cp web:/app/log.txt .                # Copy from container

# Networks
docker network ls                           # List networks
docker network create mynet                 # Create network
docker network inspect mynet                # Inspect network
docker network rm mynet                     # Remove network

# Volumes
docker volume ls                            # List volumes
docker volume create mydata                 # Create volume
docker volume inspect mydata                # Inspect volume
docker volume rm mydata                     # Remove volume
docker volume prune                         # Remove unused volumes

# System
docker system df                            # Disk usage
docker system prune                         # Remove unused objects
docker system prune -a                      # Remove all unused
docker info                                 # System info

# Compose
docker compose up -d                        # Start all services
docker compose down                         # Stop and remove
docker compose down -v                      # Also remove volumes
docker compose ps                           # List services
docker compose logs -f app                  # Follow service logs
docker compose exec app bash                # Shell into service
docker compose build                        # Build images
docker compose pull                         # Pull images
docker compose config                       # Validate file
```

### Dockerfile Quick Reference

```dockerfile
FROM node:18-alpine              # Base image
WORKDIR /app                     # Set working dir
COPY package*.json ./            # Copy files
RUN npm ci --production          # Run command (build time)
COPY . .                         # Copy remaining files
EXPOSE 3000                      # Document port
ENV NODE_ENV=production          # Set environment variable
ARG BUILD_VERSION                # Build argument
USER node                        # Switch user
VOLUME /app/data                 # Declare volume mount
HEALTHCHECK --interval=30s \
  CMD curl -f http://localhost:3000/health || exit 1
ENTRYPOINT ["node"]              # Fixed executable
CMD ["server.js"]                # Default arguments
```

### Common Flags Reference

| Flag | Command | Meaning |
|---|---|---|
| `-d` | `run` | Detached (background) |
| `-it` | `run`, `exec` | Interactive + TTY |
| `--rm` | `run` | Remove on exit |
| `-p HOST:CONT` | `run` | Port mapping |
| `-v VOL:PATH` | `run` | Volume mount |
| `-e VAR=VAL` | `run` | Environment variable |
| `--name` | `run` | Container name |
| `--network` | `run` | Attach to network |
| `--restart` | `run` | Restart policy |
| `-f` | `logs` | Follow log stream |
| `-a` | `ps` | Show all containers |
| `--no-cache` | `build` | Disable layer cache |
| `-t` | `build` | Tag the image |
| `-f` | `build` | Specify Dockerfile |

### Interview Vocabulary Quick Reference

| Term | Definition |
|---|---|
| **Container** | Running instance of an image; isolated process |
| **Image** | Read-only template; built from Dockerfile |
| **Layer** | Single instruction result; stacked to form image |
| **Registry** | Image storage server (Docker Hub, ECR) |
| **Dockerfile** | Build script for images |
| **Docker Compose** | Multi-container app definition tool |
| **Volume** | Persistent storage managed by Docker |
| **Bind Mount** | Host directory mapped into container |
| **Namespace** | Linux isolation primitive (PID, NET, MNT...) |
| **cgroup** | Linux resource limiting primitive |
| **Bridge Network** | Default isolated container network |
| **Overlay Network** | Multi-host Swarm network |
| **Health Check** | Command that verifies container is working |
| **Multi-stage Build** | Multiple FROM in one Dockerfile; lean final image |
| **Dangling Image** | Untagged, unreferenced image layer |
| **Entrypoint** | Fixed command that always runs in container |
| **containerd** | High-level container runtime |
| **runc** | Low-level runtime; creates containers using kernel |
| **OverlayFS** | Union filesystem used by Docker for image layers |
| **Docker Swarm** | Docker's native container orchestration |

---

## Final Interview Advice

When answering Docker questions, always frame your answers around **three pillars**:
1. What the concept **IS**
2. **WHY** it exists (what problem it solves)
3. **HOW** you've used it or would use it

Concrete examples beat abstract definitions every time.

**The five topics most likely to come up:**
- Containers vs VMs (namespaces + cgroups)
- CMD vs ENTRYPOINT
- Volumes vs Bind Mounts
- How Docker networking works (bridge + DNS)
- Multi-stage builds and image optimization

Practice running commands in a real Docker environment — hands-on familiarity shows immediately in interviews.

---

*Docker: From Basics to Interview-Ready*
*43 Chapters | 200+ Commands | 50 Interview Q&As*
