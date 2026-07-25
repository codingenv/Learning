package com.kafka.demo;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * MODULE 1 — AdminClient Demo
 *
 * Demonstrates programmatic topic management using KafkaAdminClient.
 *
 * CONCEPTS:
 *   • Creating topics with specific partition / replication settings
 *   • Listing topics
 *   • Describing topics (partition metadata, leader, ISR)
 *   • Deleting topics
 *   • Setting topic-level configurations (retention, compaction)
 *
 * PREREQUISITE: Kafka broker running on localhost:9092
 *
 * RUN:  ./gradlew :01-basics:run -PmainClass=com.kafka.demo.TopicAdminDemo
 */
public class TopicAdminDemo {

    private static final Logger log = LoggerFactory.getLogger(TopicAdminDemo.class);
    private static final String BOOTSTRAP = "localhost:9092";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);

        try (AdminClient admin = AdminClient.create(props)) {

            // ---- 1. Create topics ----
            createTopics(admin);

            // ---- 2. List topics ----
            listTopics(admin);

            // ---- 3. Describe topics ----
            describeTopics(admin, List.of("demo-basic", "demo-compacted"));

            // ---- 4. Delete topics ----
            log.info("\nCleaning up...");
            admin.deleteTopics(List.of("demo-basic", "demo-compacted")).all().get();
            log.info("Topics deleted.");
        }
    }

    // -------------------------------------------------------------------------

    private static void createTopics(AdminClient admin) throws ExecutionException, InterruptedException {
        log.info("\n--- Creating Topics ---");

        // Standard topic: 3 partitions, replication factor 1 (single-broker dev setup)
        NewTopic basicTopic = new NewTopic("demo-basic", 3, (short) 1);

        // Topic with custom retention: keep data for only 1 hour
        Map<String, String> retentionConfig = new HashMap<>();
        retentionConfig.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(60 * 60 * 1000L));
        retentionConfig.put(TopicConfig.RETENTION_BYTES_CONFIG, String.valueOf(100 * 1024 * 1024L)); // 100 MB

        NewTopic shortRetentionTopic = new NewTopic("demo-short-retention", 2, (short) 1)
                .configs(retentionConfig);

        // Log-compacted topic: instead of time/size retention, keep the LATEST value per key
        // Great for changelogs / state snapshots
        Map<String, String> compactionConfig = new HashMap<>();
        compactionConfig.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        compactionConfig.put(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_CONFIG, "0.1");

        NewTopic compactedTopic = new NewTopic("demo-compacted", 1, (short) 1)
                .configs(compactionConfig);

        CreateTopicsResult result = admin.createTopics(
                List.of(basicTopic, shortRetentionTopic, compactedTopic)
        );

        result.all().get(); // block until done
        log.info("Created: demo-basic (3 partitions), demo-short-retention, demo-compacted");
    }

    private static void listTopics(AdminClient admin) throws ExecutionException, InterruptedException {
        log.info("\n--- Listing Topics ---");
        Set<String> topics = admin.listTopics().names().get();
        topics.stream().sorted().forEach(t -> log.info("  Topic: {}", t));
    }

    private static void describeTopics(AdminClient admin, List<String> names)
            throws ExecutionException, InterruptedException {
        log.info("\n--- Describing Topics ---");

        Map<String, TopicDescription> descriptions =
                admin.describeTopics(names).allTopicNames().get();

        for (TopicDescription td : descriptions.values()) {
            log.info("\nTopic: {} (internal={})", td.name(), td.isInternal());
            td.partitions().forEach(p -> {
                log.info("  Partition {}  leader={}  replicas={}  isr={}",
                        p.partition(),
                        p.leader() != null ? p.leader().id() : "none",
                        p.replicas().stream().map(n -> String.valueOf(n.id())).toList(),
                        p.isr().stream().map(n -> String.valueOf(n.id())).toList()
                );
            });
        }
    }
}
