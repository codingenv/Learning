package com.kafka.demo;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * MODULE 5 — PARTITIONS, OFFSETS & LAG
 *
 * CONCEPTS COVERED:
 *   • Listing all partitions of a topic with their leader and ISR
 *   • Reading earliest / latest offsets (watermarks)
 *   • Computing consumer lag per partition
 *   • Lag = latest_offset - committed_offset
 *
 * CONSUMER LAG:
 *   Lag tells you how far behind a consumer group is from the tip of the log.
 *   High lag = consumer can't keep up with producer throughput.
 *   Solutions: add more consumers (up to partition count), optimise processing,
 *              reduce max.poll.records, use async processing.
 *
 * RUN:  ./gradlew :05-partitions-offsets:run -PmainClass=com.kafka.demo.PartitionOffsetExplorer
 */
public class PartitionOffsetExplorer {

    private static final Logger log = LoggerFactory.getLogger(PartitionOffsetExplorer.class);
    private static final String BOOTSTRAP = "localhost:9092";
    private static final String TOPIC     = "demo-basic";
    private static final String GROUP_ID  = "demo-consumer-group";

    public static void main(String[] args) throws Exception {
        explorePartitions();
        measureConsumerLag();
    }

    // ---- Explore partition metadata ----------------------------------------

    private static void explorePartitions() throws ExecutionException, InterruptedException {
        log.info("\n=== Partition Exploration ===");

        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);

        try (AdminClient admin = AdminClient.create(adminProps);
             KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps())) {

            // Describe the topic to get partition metadata
            TopicDescription desc = admin.describeTopics(List.of(TOPIC))
                    .allTopicNames().get().get(TOPIC);

            log.info("Topic: {} | Partitions: {}", TOPIC, desc.partitions().size());

            List<TopicPartition> tps = new ArrayList<>();
            for (var pi : desc.partitions()) {
                log.info("  Partition {}  leader=broker{}  replicas={}  isr={}",
                        pi.partition(),
                        pi.leader() != null ? pi.leader().id() : "?",
                        pi.replicas().stream().map(n -> "broker" + n.id()).toList(),
                        pi.isr().stream().map(n -> "broker" + n.id()).toList()
                );
                tps.add(new TopicPartition(TOPIC, pi.partition()));
            }

            // Query watermarks (earliest and latest offsets)
            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(tps);
            Map<TopicPartition, Long> endOffsets       = consumer.endOffsets(tps);

            log.info("\nOffset Watermarks:");
            for (TopicPartition tp : tps) {
                long begin = beginningOffsets.get(tp);
                long end   = endOffsets.get(tp);
                log.info("  Partition {}  earliest={}  latest={}  total-records={}",
                        tp.partition(), begin, end, end - begin);
            }
        }
    }

    // ---- Compute consumer lag ----------------------------------------------

    private static void measureConsumerLag() throws ExecutionException, InterruptedException {
        log.info("\n=== Consumer Lag for group '{}' ===", GROUP_ID);

        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);

        try (AdminClient admin = AdminClient.create(adminProps);
             KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps())) {

            // Get committed offsets for the consumer group
            Map<TopicPartition, OffsetAndMetadata> committed =
                    admin.listConsumerGroupOffsets(GROUP_ID)
                            .partitionsToOffsetAndMetadata().get();

            if (committed.isEmpty()) {
                log.info("  No committed offsets for group '{}' — has it consumed anything?", GROUP_ID);
                return;
            }

            // Get the latest offsets (end of log)
            List<TopicPartition> tps = new ArrayList<>(committed.keySet());
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(tps);

            long totalLag = 0;
            for (TopicPartition tp : tps) {
                if (!tp.topic().equals(TOPIC)) continue;
                long committedOffset = committed.get(tp).offset();
                long latestOffset    = endOffsets.get(tp);
                long lag             = latestOffset - committedOffset;
                totalLag += lag;

                log.info("  Partition {}  committed={}  latest={}  LAG={}{}",
                        tp.partition(), committedOffset, latestOffset, lag,
                        lag > 100 ? "  *** HIGH LAG ***" : "");
            }
            log.info("  TOTAL LAG for topic '{}': {}", TOPIC, totalLag);
        }
    }

    private static Properties consumerProps() {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "lag-explorer-temp");
        return p;
    }
}
