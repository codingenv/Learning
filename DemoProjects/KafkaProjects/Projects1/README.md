# Kafka Learning Project

A concept-focused Java + Gradle project to learn Apache Kafka from the ground up.

## Quick Start

```bash
# 1. Start Kafka (Docker)
docker-compose up -d

# 2. (Optional) Open Kafka UI in browser
#    http://localhost:8080

# 3. Build the project
./gradlew build
```

## Modules

| Module | Concept | Main Class |
|--------|---------|------------|
| `01-basics` | Topics, Partitions, Replication, KRaft vs ZooKeeper | `KafkaConceptsOverview` |
| `01-basics` | AdminClient — create/describe/delete topics | `TopicAdminDemo` |
| `02-producers` | Fire-forget / Async callback / Sync, acks, batching | `BasicProducer` |
| `02-producers` | Custom partitioner (VIP routing) | `CustomPartitionerDemo` |
| `03-consumers` | Poll loop, manual commit, shutdown hook | `BasicConsumer` |
| `03-consumers` | Per-partition commit, seek, assign vs subscribe | `ManualOffsetConsumer` |
| `04-consumer-groups` | Multi-consumer, rebalancing, CooperativeStickyAssignor | `ConsumerGroupDemo` |
| `05-partitions-offsets` | Watermarks, consumer lag computation | `PartitionOffsetExplorer` |
| `06-serialization` | Custom JSON serializer/deserializer | `JsonSerializationDemo` |
| `07-kafka-streams` | Topology, KStream, KTable, word count | `WordCountStreams` |
| `07-kafka-streams` | Tumbling windows, hopping windows | `WindowedAggregationStreams` |
| `08-transactions` | Idempotent producer, transactions, Consume-Transform-Produce | `TransactionalProducerDemo` |

## Running a Module

```bash
# Print concept overview (no Kafka needed)
./gradlew :01-basics:run -PmainClass=com.kafka.demo.KafkaConceptsOverview

# Create topics via AdminClient
./gradlew :01-basics:run -PmainClass=com.kafka.demo.TopicAdminDemo

# Produce messages
./gradlew :02-producers:run -PmainClass=com.kafka.demo.BasicProducer

# Consume messages (Ctrl+C to stop)
./gradlew :03-consumers:run -PmainClass=com.kafka.demo.BasicConsumer
```

## Key Kafka Concepts At a Glance

```
Producer → [Topic: P0, P1, P2] → Consumer Group
                  ↕
              Broker(s)
              (replicated)
```

- **Topic** — named stream; split into partitions
- **Partition** — ordered, immutable log; enables parallelism
- **Offset** — record position within a partition
- **Consumer Group** — partitions shared across group members
- **Rebalance** — triggered when group membership changes
- **ISR** — In-Sync Replicas; set of replicas up-to-date with leader

## Delivery Semantics

| Guarantee | How | Risk |
|-----------|-----|------|
| At-most-once | Commit before process | Data loss |
| At-least-once | Commit after process | Duplicates |
| Exactly-once | Transactional producer + `read_committed` | Higher latency |

## Useful CLI Commands (inside container)

```bash
docker exec -it kafka-demo bash

# List topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Create a topic
kafka-topics.sh --bootstrap-server localhost:9092 --create \
  --topic my-topic --partitions 3 --replication-factor 1

# Describe a topic
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic demo-basic

# Produce from console
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic demo-basic

# Consume from console
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic demo-basic --from-beginning

# Check consumer group lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group demo-consumer-group
```
