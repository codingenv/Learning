package com.kafka.demo;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * MODULE 4 — CONSUMER GROUPS & REBALANCING
 *
 * This demo starts multiple consumers in the SAME group on separate threads
 * to show partition assignment and automatic rebalancing.
 *
 * KEY CONCEPTS:
 *   • Partitions are distributed evenly across consumers in a group
 *   • Adding/removing consumers triggers a rebalance
 *   • During rebalance, consumption pauses
 *   • Cooperative rebalancing (incremental) reduces pauses vs eager rebalancing
 *
 * REBALANCE PROTOCOLS:
 *   EAGER (default before Kafka 2.4):
 *     All consumers drop ALL partitions, then re-assign from scratch.
 *     Causes a "stop-the-world" pause.
 *
 *   COOPERATIVE / INCREMENTAL (Kafka 2.4+):
 *     Only the partitions that need to move are revoked.
 *     Other partitions continue to be consumed without interruption.
 *     Enable with: partition.assignment.strategy = CooperativeStickyAssignor
 *
 * ASSIGNMENT STRATEGIES:
 *   RangeAssignor       — assigns contiguous partition ranges per topic (default)
 *   RoundRobinAssignor  — distributes partitions in round-robin order
 *   StickyAssignor      — minimizes partition movement on rebalance (eager)
 *   CooperativeStickyAssignor — minimizes movement + cooperative (recommended)
 *
 * RUN:  ./gradlew :04-consumer-groups:run -PmainClass=com.kafka.demo.ConsumerGroupDemo
 */
public class ConsumerGroupDemo {

    private static final Logger log = LoggerFactory.getLogger(ConsumerGroupDemo.class);
    private static final String TOPIC    = "demo-basic";     // must have 3 partitions
    private static final String GROUP_ID = "cg-demo";
    private static final int NUM_CONSUMERS = 3;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CONSUMERS);

        log.info("Starting {} consumers in group '{}'", NUM_CONSUMERS, GROUP_ID);
        log.info("Topic '{}' has 3 partitions — each consumer should get 1 partition", TOPIC);

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            final int consumerId = i;
            executor.submit(() -> runConsumer(consumerId));
        }

        // Run for 20 seconds, then shutdown
        Thread.sleep(20_000);
        log.info("Shutting down all consumers...");
        executor.shutdownNow();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        log.info("Done.");
    }

    private static void runConsumer(int id) {
        Properties props = consumerConfig();
        String consumerLabel = "Consumer-" + id;

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(TOPIC), new LoggingRebalanceListener(consumerLabel));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, String> r : records) {
                    log.info("[{}] partition={} offset={} key={} value={}",
                            consumerLabel, r.partition(), r.offset(), r.key(), r.value());
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (org.apache.kafka.common.errors.WakeupException e) {
            // shutdown signal
        } catch (Exception e) {
            log.error("[{}] Error: {}", consumerLabel, e.getMessage());
        }

        log.info("[{}] Stopped.", consumerLabel);
    }

    // -------------------------------------------------------------------------

    private static Properties consumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Use CooperativeStickyAssignor to avoid "stop-the-world" rebalances
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");

        return props;
    }

    static class LoggingRebalanceListener implements ConsumerRebalanceListener {
        private final String label;

        LoggingRebalanceListener(String label) { this.label = label; }

        @Override
        public void onPartitionsRevoked(java.util.Collection<org.apache.kafka.common.TopicPartition> partitions) {
            log.info("[{}] *** REBALANCE: Revoking partitions: {}", label, partitions);
        }

        @Override
        public void onPartitionsAssigned(java.util.Collection<org.apache.kafka.common.TopicPartition> partitions) {
            log.info("[{}] *** REBALANCE: Assigned partitions: {}", label, partitions);
        }
    }
}
