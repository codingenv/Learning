package com.kafka.demo;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * MODULE 3 — Advanced Offset Management
 *
 * CONCEPTS:
 *   • Per-partition offset commit (fine-grained control)
 *   • Seeking to specific offsets (replay / skip)
 *   • Assign vs Subscribe (manual partition assignment)
 *   • ConsumerRebalanceListener (save offsets on rebalance)
 *
 * DELIVERY SEMANTICS:
 *   At-most-once:   commit BEFORE processing (may lose data if crash)
 *   At-least-once:  commit AFTER processing  (may duplicate if crash mid-process) ← common default
 *   Exactly-once:   transactional producer + consumer read_committed + idempotent processing
 *
 * RUN:  ./gradlew :03-consumers:run -PmainClass=com.kafka.demo.ManualOffsetConsumer
 */
public class ManualOffsetConsumer {

    private static final Logger log = LoggerFactory.getLogger(ManualOffsetConsumer.class);
    private static final String TOPIC    = "demo-basic";
    private static final String GROUP_ID = "manual-offset-group";

    public static void main(String[] args) {
        demonstratePerPartitionCommit();
        demonstrateSeek();
    }

    // ---- Demo 1: Per-partition offset commit --------------------------------
    // Instead of committing all partitions at once, commit each partition
    // as soon as its batch is processed. Reduces potential re-processing window.

    private static void demonstratePerPartitionCommit() {
        Properties props = consumerConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID + "-per-partition");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(TOPIC), new RebalanceListener(consumer));

            for (int poll = 0; poll < 3; poll++) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                // Process and commit partition by partition
                Map<TopicPartition, OffsetAndMetadata> toCommit = new HashMap<>();

                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<String, String>> partRecords = records.records(partition);

                    for (ConsumerRecord<String, String> r : partRecords) {
                        log.info("[PerPartition] partition={} offset={} value={}",
                                r.partition(), r.offset(), r.value());
                    }

                    if (!partRecords.isEmpty()) {
                        long lastOffset = partRecords.get(partRecords.size() - 1).offset();
                        // Commit offset = lastOffset + 1 (next record to read)
                        toCommit.put(partition, new OffsetAndMetadata(lastOffset + 1));
                    }
                }

                if (!toCommit.isEmpty()) {
                    consumer.commitSync(toCommit);
                    log.info("[PerPartition] Committed: {}", toCommit);
                }
            }
        }
    }

    // ---- Demo 2: Seek to a specific offset ----------------------------------
    // Use seek() to replay from a specific offset or skip ahead.
    // Common use-cases: replaying failed events, skipping a poison pill message.

    private static void demonstrateSeek() {
        log.info("\n=== Demonstrating seek() ===");
        Properties props = consumerConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID + "-seek");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // ASSIGN: manually assign specific partitions (no group rebalancing)
            // Use this when you need full control over which partitions to read.
            TopicPartition p0 = new TopicPartition(TOPIC, 0);
            consumer.assign(List.of(p0));

            // Seek to beginning of partition 0 (replay all)
            consumer.seekToBeginning(List.of(p0));
            log.info("Seeked to beginning of partition 0");

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            log.info("Records from beginning: {}", records.count());
            records.forEach(r -> log.info("  offset={} value={}", r.offset(), r.value()));

            // Seek to a specific offset
            consumer.seek(p0, 2L);  // start reading from offset 2
            log.info("Seeked partition 0 to offset 2");

            records = consumer.poll(Duration.ofSeconds(2));
            log.info("Records from offset 2: {}", records.count());
            records.forEach(r -> log.info("  offset={} value={}", r.offset(), r.value()));

            // Seek to end (skip everything, only process new records)
            consumer.seekToEnd(List.of(p0));
            log.info("Seeked to end — will only receive new records");
        }
    }

    // -------------------------------------------------------------------------

    /**
     * ConsumerRebalanceListener is called before partitions are revoked (on rebalance).
     * Use it to commit offsets for the revoked partitions before they're reassigned.
     */
    static class RebalanceListener implements ConsumerRebalanceListener {
        private static final Logger log = LoggerFactory.getLogger(RebalanceListener.class);
        private final KafkaConsumer<?, ?> consumer;

        RebalanceListener(KafkaConsumer<?, ?> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            // Called BEFORE partitions are revoked. Commit your current offsets here.
            log.info("Partitions revoked: {} — committing offsets now", partitions);
            consumer.commitSync();
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            log.info("Partitions assigned: {}", partitions);
        }
    }

    private static Properties consumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }
}
