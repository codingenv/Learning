package com.kafka.demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * MODULE 2 — PRODUCERS
 *
 * Demonstrates the three producer send patterns:
 *   1. Fire-and-forget (async, no callback)
 *   2. Async with callback
 *   3. Synchronous (blocking)
 *
 * Also shows:
 *   • How to set a message key (controls partition routing)
 *   • Producer configuration tuning (acks, retries, batching)
 *   • Idempotent producer (exactly-once at producer level)
 *
 * PREREQUISITE: Kafka running on localhost:9092
 *               Topic "demo-basic" must exist (run TopicAdminDemo first)
 *
 * RUN:  ./gradlew :02-producers:run -PmainClass=com.kafka.demo.BasicProducer
 */
public class BasicProducer {

    private static final Logger log = LoggerFactory.getLogger(BasicProducer.class);
    private static final String TOPIC = "demo-basic";

    public static void main(String[] args) throws Exception {
        Properties props = producerConfig();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            // ---- Pattern 1: Fire and forget ----
            // Fastest. No guarantee of delivery. Good only for non-critical metrics.
            log.info("=== Pattern 1: Fire and Forget ===");
            ProducerRecord<String, String> record1 = new ProducerRecord<>(TOPIC, "fire-and-forget");
            producer.send(record1);   // returns a Future but we ignore it
            log.info("Sent without waiting for ack");

            // ---- Pattern 2: Async with Callback ----
            // Non-blocking. Callback fires on success OR failure.
            log.info("\n=== Pattern 2: Async with Callback ===");
            for (int i = 0; i < 5; i++) {
                String key   = "user-" + (i % 3);   // 3 keys → 3 partition buckets
                String value = "event-" + i;

                producer.send(
                    new ProducerRecord<>(TOPIC, key, value),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("Send failed", exception);
                        } else {
                            log.info("ACK  topic={} partition={} offset={} key={}",
                                    metadata.topic(),
                                    metadata.partition(),   // note: same key → same partition
                                    metadata.offset(),
                                    key);
                        }
                    }
                );
            }

            // ---- Pattern 3: Synchronous ----
            // Blocks the calling thread until the broker acknowledges.
            // Use when you need ordered processing and can accept the latency cost.
            log.info("\n=== Pattern 3: Synchronous (blocking) ===");
            ProducerRecord<String, String> record3 =
                    new ProducerRecord<>(TOPIC, "sync-key", "synchronous-value");
            try {
                RecordMetadata meta = producer.send(record3).get();   // blocks here
                log.info("Sync ACK → partition={} offset={}", meta.partition(), meta.offset());
            } catch (ExecutionException e) {
                log.error("Sync send failed: {}", e.getCause().getMessage());
            }

            // Flush ensures all buffered records are sent before close()
            producer.flush();
            log.info("\nAll records sent. Producer closing.");
        }
    }

    // -------------------------------------------------------------------------

    private static Properties producerConfig() {
        Properties props = new Properties();

        // Where to find the cluster
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // How to convert key/value objects to bytes
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // acks=all  → safest (leader + all ISR replicas must acknowledge)
        // acks=1    → leader only (default before Kafka 3.0)
        // acks=0    → no acknowledgement (fastest, data loss possible)
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Retry on transient failures (network blip, leader election)
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Idempotent producer: prevents duplicate records during retries.
        // Requires: acks=all, retries>0, max.in.flight.requests.per.connection ≤ 5
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Batching — wait up to 20ms to accumulate records before sending
        props.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        // Max bytes per batch (default 16384 = 16 KB)
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);

        // Compress the batch to reduce network I/O and disk usage
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return props;
    }
}
