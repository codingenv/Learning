package com.kafka.demo;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * MODULE 3 — CONSUMERS
 *
 * Covers:
 *   • Basic consumer loop (subscribe → poll → process → commit)
 *   • Auto-commit vs manual commit
 *   • Graceful shutdown using a shutdown hook
 *   • Consumer config explained
 *
 * KAFKA CONSUMER LIFECYCLE:
 *   1. Create consumer with group.id
 *   2. Subscribe to topics (or assign specific partitions)
 *   3. Poll in a loop — this also sends heartbeats to the broker
 *   4. Process records
 *   5. Commit offsets (auto or manual)
 *   6. Close consumer (triggers rebalance)
 *
 * RUN:  ./gradlew :03-consumers:run -PmainClass=com.kafka.demo.BasicConsumer
 */
public class BasicConsumer {

    private static final Logger log = LoggerFactory.getLogger(BasicConsumer.class);
    private static final String TOPIC      = "demo-basic";
    private static final String GROUP_ID   = "demo-consumer-group";

    // Volatile so the shutdown hook thread sees the update
    private static volatile boolean keepRunning = true;

    public static void main(String[] args) {
        Properties props = consumerConfig();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Graceful shutdown: Ctrl+C calls consumer.wakeup() which throws WakeupException
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received — stopping consumer...");
            keepRunning = false;
            consumer.wakeup();  // interrupt the blocking poll()
        }));

        try {
            // Subscribe to topic(s). Kafka will assign partitions during rebalance.
            consumer.subscribe(List.of(TOPIC));
            log.info("Subscribed to topic: {}", TOPIC);

            while (keepRunning) {
                // poll() does TWO things:
                //   1. Fetches records from Kafka (up to max.poll.records)
                //   2. Sends heartbeat to the group coordinator
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    log.info("Polled {} records", records.count());
                }

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Received:  topic={} partition={} offset={} key={} value={}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.key(),
                            record.value()
                    );

                    // Process the record here...
                    processRecord(record);
                }

                // MANUAL COMMIT: only commit after successful processing
                // If processing fails before commit, records will be re-delivered.
                // This gives AT-LEAST-ONCE delivery semantics.
                if (!records.isEmpty()) {
                    consumer.commitSync();   // blocks until broker acknowledges
                    log.info("Offsets committed.");
                }
            }

        } catch (org.apache.kafka.common.errors.WakeupException e) {
            // Expected on shutdown — wakeup() throws this to break out of poll()
            log.info("WakeupException caught — shutting down gracefully.");
        } catch (Exception e) {
            log.error("Unexpected error", e);
        } finally {
            // Commit any remaining offsets and leave the consumer group cleanly.
            // This triggers an immediate rebalance rather than waiting for session timeout.
            consumer.close();
            log.info("Consumer closed.");
        }
    }

    private static void processRecord(ConsumerRecord<String, String> record) {
        // Simulate work
        log.debug("  Processing: {}", record.value());
    }

    // -------------------------------------------------------------------------

    private static Properties consumerConfig() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Deserializers must match the producer's serializers
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // All consumers with the same group.id share the partitions
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // Where to start if no committed offset exists for this group:
        //   earliest = read from beginning of topic
        //   latest   = read only new messages written after the consumer starts
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Disable auto-commit so WE control when offsets are committed
        // (gives at-least-once semantics — process then commit)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Max records returned per poll() call
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        // Heartbeat interval must be < session.timeout.ms / 3
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        // If no heartbeat within this window, broker assumes consumer is dead
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);

        // Max time between poll() calls before consumer is considered dead
        // If processing takes longer than this, increase max.poll.interval.ms
        // or reduce max.poll.records
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300_000);

        return props;
    }
}
