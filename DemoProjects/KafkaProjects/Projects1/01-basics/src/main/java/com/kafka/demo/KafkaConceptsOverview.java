package com.kafka.demo;

/**
 * MODULE 1 — KAFKA FUNDAMENTALS
 *
 * This class is intentionally documentation-heavy. Run it to print a concept
 * map to the console. No broker needed.
 *
 * KEY CONCEPTS COVERED:
 *   1. What is Kafka?
 *   2. Topics, Partitions, Segments
 *   3. Brokers and the Kafka Cluster
 *   4. Producers and Consumers
 *   5. Consumer Groups
 *   6. Offsets
 *   7. Replication (leaders / followers / ISR)
 *   8. ZooKeeper vs KRaft mode
 */
public class KafkaConceptsOverview {

    public static void main(String[] args) {
        printSection("1. WHAT IS APACHE KAFKA?");
        print("""
                Apache Kafka is a distributed, fault-tolerant, high-throughput event streaming
                platform. It was originally built at LinkedIn and open-sourced in 2011.

                Core use-cases:
                  • Messaging (decouple producers from consumers)
                  • Activity tracking (clickstreams, user events)
                  • Metrics / log aggregation
                  • Stream processing (Kafka Streams, ksqlDB)
                  • Event sourcing (durable log of state changes)
                  • Commit log (change-data capture / CDC)
                """);

        printSection("2. TOPICS, PARTITIONS & SEGMENTS");
        print("""
                TOPIC
                -----
                A topic is a named stream of records. Think of it like a database table
                or a folder in a filesystem. Topics are multi-subscriber — many consumers
                can read the same topic independently.

                PARTITION
                ---------
                Each topic is split into one or more partitions. A partition is an
                ordered, immutable sequence of records. Records within a partition are
                assigned monotonically increasing IDs called OFFSETS.

                  Topic: "orders"
                  ┌─────────────────────────────────────────────────────────────┐
                  │ Partition 0: [0]→"order#1"  [1]→"order#4"  [2]→"order#7"  │
                  │ Partition 1: [0]→"order#2"  [1]→"order#5"  [2]→"order#8"  │
                  │ Partition 2: [0]→"order#3"  [1]→"order#6"  [2]→"order#9"  │
                  └─────────────────────────────────────────────────────────────┘

                WHY PARTITIONS?
                  • Parallelism — multiple consumers can read different partitions simultaneously
                  • Scalability — partitions are distributed across brokers
                  • Ordering — order is guaranteed only within a partition

                SEGMENT
                -------
                Partitions are further split into segments (log files on disk).
                Kafka writes to the active segment and rolls to a new one when
                segment.bytes or segment.ms is exceeded. Old segments are deleted
                or compacted per retention policy.
                """);

        printSection("3. BROKERS AND THE KAFKA CLUSTER");
        print("""
                BROKER
                ------
                A broker is a single Kafka server process. It stores partition data on
                disk and serves producer/consumer requests.

                CLUSTER
                -------
                A Kafka cluster is a group of brokers. Each broker is identified by an
                integer id (broker.id). Brokers communicate with each other to replicate
                data and elect partition leaders.

                  Cluster with 3 brokers:
                  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
                  │  Broker 1    │  │  Broker 2    │  │  Broker 3    │
                  │  (leader P0) │  │  (leader P1) │  │  (leader P2) │
                  │  (replica P1)│  │  (replica P2)│  │  (replica P0)│
                  └──────────────┘  └──────────────┘  └──────────────┘

                BOOTSTRAP SERVERS
                -----------------
                Clients connect to one or more "bootstrap servers" to discover the
                full cluster topology. Only a few need to be listed — clients
                self-discover the rest.
                """);

        printSection("4. PRODUCERS");
        print("""
                A producer publishes (writes) records to topics.

                RECORD STRUCTURE:
                  • Topic       — destination topic
                  • Partition   — optional, determined by partitioner if omitted
                  • Key         — optional bytes; same key always goes to same partition
                  • Value       — the payload (bytes)
                  • Headers     — optional key-value metadata
                  • Timestamp   — event time or log-append time

                PARTITIONING STRATEGY:
                  1. If partition explicitly set → use it
                  2. If key is present         → hash(key) % numPartitions
                  3. If no key                 → sticky / round-robin (varies by version)

                PRODUCER ACKNOWLEDGEMENTS (acks):
                  acks=0  → fire-and-forget (fastest, data loss possible)
                  acks=1  → leader confirms (moderate, loss if leader crashes before replication)
                  acks=all→ all in-sync replicas confirm (safest, highest latency)

                BATCHING & LINGER:
                  batch.size   — max bytes per batch (default 16 KB)
                  linger.ms    — wait this long for more records before sending (default 0)
                  compression.type — none / gzip / snappy / lz4 / zstd
                """);

        printSection("5. CONSUMERS");
        print("""
                A consumer reads (pulls) records from topics.

                PULL MODEL:
                  Kafka consumers pull data at their own pace (unlike push-based systems).
                  This prevents consumers from being overwhelmed.

                OFFSET MANAGEMENT:
                  The offset is the position of the last consumed record in a partition.
                  Consumers commit offsets to Kafka so they can resume after a restart.

                  auto.offset.reset:
                    earliest — start from the oldest available record
                    latest   — start from the newest record (skip historical)
                    none     — throw exception if no committed offset found

                  enable.auto.commit = true  → Kafka commits every auto.commit.interval.ms
                  enable.auto.commit = false → You call consumer.commitSync() / commitAsync()
                """);

        printSection("6. CONSUMER GROUPS");
        print("""
                Consumer groups allow multiple consumers to share the work of reading a topic.

                RULES:
                  • Each partition is assigned to exactly ONE consumer in the group
                  • Multiple consumer groups can read the same topic independently
                  • If consumers > partitions, some consumers sit idle

                REBALANCING:
                  When a consumer joins/leaves a group, Kafka triggers a rebalance to
                  re-assign partitions. During a rebalance, consumption pauses.

                  Topic: "orders" (3 partitions)
                  Consumer Group: "order-processor"

                  Before rebalance (2 consumers):
                    Consumer A → P0, P1
                    Consumer B → P2

                  After adding Consumer C (rebalance):
                    Consumer A → P0
                    Consumer B → P1
                    Consumer C → P2
                """);

        printSection("7. REPLICATION");
        print("""
                REPLICATION FACTOR:
                  Each partition is replicated across N brokers (replication.factor).
                  One replica is the LEADER (handles all reads/writes).
                  The others are FOLLOWERS (replicate from leader).

                IN-SYNC REPLICAS (ISR):
                  ISR = set of replicas that are fully caught up with the leader.
                  min.insync.replicas controls how many must be in ISR for writes to succeed.

                LEADER ELECTION:
                  If a leader fails, Kafka elects a new leader from the ISR.
                  With KRaft (KIP-500), the controller is inside Kafka itself.

                UNCLEAN LEADER ELECTION:
                  unclean.leader.election.enable=false (default) — never elect an out-of-sync
                  replica; prefer availability loss over data loss.
                """);

        printSection("8. ZOOKEEPER vs KRAFT MODE");
        print("""
                ZOOKEEPER (legacy, Kafka < 3.x):
                  Kafka used ZooKeeper to manage cluster metadata (broker list, topic config,
                  partition leaders, ACLs). ZooKeeper is a separate cluster you had to run.

                KRAFT MODE (Kafka 3.3+ production-ready, Kafka 4.x default):
                  KRaft = Kafka Raft. Kafka manages its own metadata using an internal
                  Raft-based consensus protocol. No ZooKeeper dependency.
                  • Simpler operations
                  • Faster controller failover
                  • Scales to millions of partitions

                RUNNING KAFKA LOCALLY (KRaft, no ZooKeeper):
                  # Generate a cluster ID
                  kafka-storage.sh random-uuid

                  # Format storage
                  kafka-storage.sh format -t <uuid> -c config/kraft/server.properties

                  # Start the broker
                  kafka-server-start.sh config/kraft/server.properties

                  OR simply use Docker:
                  docker run -p 9092:9092 apache/kafka:3.7.0
                """);

        printSection("9. IMPORTANT CONFIGURATION SUMMARY");
        print("""
                BROKER CONFIG (server.properties):
                  broker.id, log.dirs, num.partitions, default.replication.factor
                  log.retention.hours, log.retention.bytes, log.segment.bytes
                  min.insync.replicas, unclean.leader.election.enable

                PRODUCER CONFIG:
                  bootstrap.servers, key.serializer, value.serializer
                  acks, retries, batch.size, linger.ms, compression.type
                  max.in.flight.requests.per.connection (set to 1 for strict ordering)
                  enable.idempotence=true → exactly-once producer semantics

                CONSUMER CONFIG:
                  bootstrap.servers, key.deserializer, value.deserializer
                  group.id, auto.offset.reset, enable.auto.commit
                  max.poll.records, session.timeout.ms, heartbeat.interval.ms
                """);
    }

    // -------------------------------------------------------------------------

    private static void printSection(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70));
    }

    private static void print(String text) {
        System.out.println(text);
    }
}
