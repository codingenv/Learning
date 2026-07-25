package com.kafka.demo;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * MODULE 8 — KAFKA TRANSACTIONS & EXACTLY-ONCE SEMANTICS (EOS)
 *
 * DELIVERY GUARANTEES:
 *   At-most-once:   messages may be lost, never duplicated
 *   At-least-once:  messages never lost, may be duplicated (default with retries)
 *   Exactly-once:   no loss, no duplicates — the holy grail
 *
 * HOW KAFKA ACHIEVES EXACTLY-ONCE:
 *
 *   1. IDEMPOTENT PRODUCER (EOS v1):
 *      enable.idempotence=true
 *      Each message gets a producer-ID + sequence-number.
 *      Broker deduplicates retried messages within the same producer session.
 *      Prevents duplicates caused by network retries.
 *
 *   2. TRANSACTIONAL PRODUCER (EOS v2):
 *      transactional.id = "unique-app-id"
 *      Groups multiple sends (and consumer offset commits) into an atomic transaction.
 *      Consumers with isolation.level=read_committed only see committed transactions.
 *
 * TRANSACTIONAL FLOW:
 *   producer.initTransactions()
 *   loop:
 *     producer.beginTransaction()
 *     producer.send(...)           // one or many sends
 *     producer.sendOffsetsToTransaction(offsets, groupMetadata)  // optional: consume-transform-produce
 *     producer.commitTransaction() // OR abortTransaction() on failure
 *
 * CONSUME-TRANSFORM-PRODUCE (CTP) PATTERN:
 *   Read from topic A → transform → write to topic B
 *   The read offset commit and the write are atomic.
 *   Even if the process crashes mid-way, the consumer will re-read and
 *   produce the same record — but the broker rejects duplicate transactions.
 *
 * RUN:  ./gradlew :08-transactions:run -PmainClass=com.kafka.demo.TransactionalProducerDemo
 */
public class TransactionalProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(TransactionalProducerDemo.class);
    private static final String INPUT_TOPIC  = "demo-basic";
    private static final String OUTPUT_TOPIC = "demo-orders";

    public static void main(String[] args) {
        demonstrateTransactionalProducer();
        demonstrateConsumeTransformProduce();
    }

    // ---- Demo 1: Transactional producer (write to multiple topics atomically) ---

    private static void demonstrateTransactionalProducer() {
        log.info("\n=== Transactional Producer Demo ===");

        Properties props = transactionalProducerConfig("txn-demo-producer-1");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            // Must call initTransactions once before any transaction
            producer.initTransactions();

            // --- Successful Transaction ---
            log.info("Starting transaction 1 (success)...");
            producer.beginTransaction();
            try {
                producer.send(new ProducerRecord<>(OUTPUT_TOPIC, "key-1", "value-A"));
                producer.send(new ProducerRecord<>(OUTPUT_TOPIC, "key-2", "value-B"));
                producer.send(new ProducerRecord<>(INPUT_TOPIC, "key-3", "value-C"));
                // All three sends are ATOMIC — either all land or none do
                producer.commitTransaction();
                log.info("Transaction 1 committed — all 3 records visible to read_committed consumers");
            } catch (Exception e) {
                producer.abortTransaction();
                log.error("Transaction 1 aborted", e);
            }

            // --- Simulated Failed Transaction ---
            log.info("\nStarting transaction 2 (simulated failure)...");
            producer.beginTransaction();
            try {
                producer.send(new ProducerRecord<>(OUTPUT_TOPIC, "key-4", "value-D"));
                producer.send(new ProducerRecord<>(OUTPUT_TOPIC, "key-5", "value-E"));

                // Simulate a failure before commit
                if (true) throw new RuntimeException("Simulated processing error!");

                producer.commitTransaction();
            } catch (Exception e) {
                log.warn("Aborting transaction 2 due to: {}", e.getMessage());
                producer.abortTransaction();
                log.info("Transaction 2 aborted — key-4 and key-5 will NOT be visible");
            }
        }
    }

    // ---- Demo 2: Consume-Transform-Produce pattern -------------------------

    private static void demonstrateConsumeTransformProduce() {
        log.info("\n=== Consume-Transform-Produce (Exactly Once) ===");

        String groupId = "ctp-consumer-group";
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // MUST disable auto-commit — we commit offsets transactionally
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // read_committed: only see messages from committed transactions
        // read_uncommitted (default): see all messages including aborted ones
        consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        Properties producerProps = transactionalProducerConfig("ctp-producer-1");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            producer.initTransactions();
            consumer.subscribe(List.of(INPUT_TOPIC));

            for (int iteration = 0; iteration < 3; iteration++) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                if (records.isEmpty()) continue;

                producer.beginTransaction();
                try {
                    // Transform and produce each record
                    for (ConsumerRecord<String, String> r : records) {
                        String transformed = r.value().toUpperCase() + "-PROCESSED";
                        producer.send(new ProducerRecord<>(OUTPUT_TOPIC, r.key(), transformed));
                        log.info("CTP: {} → {}", r.value(), transformed);
                    }

                    // Commit the consumer offsets AS PART OF the transaction.
                    // This atomically marks these records as consumed AND writes the output.
                    Map<org.apache.kafka.common.TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    for (org.apache.kafka.common.TopicPartition tp : records.partitions()) {
                        List<ConsumerRecord<String, String>> partRecords = records.records(tp);
                        long lastOffset = partRecords.get(partRecords.size() - 1).offset();
                        offsets.put(tp, new OffsetAndMetadata(lastOffset + 1));
                    }

                    producer.sendOffsetsToTransaction(offsets, consumer.groupMetadata());
                    producer.commitTransaction();
                    log.info("CTP transaction committed for {} records", records.count());

                } catch (Exception e) {
                    log.error("CTP failed, aborting transaction", e);
                    producer.abortTransaction();
                }
            }
        }
    }

    // -------------------------------------------------------------------------

    private static Properties transactionalProducerConfig(String transactionalId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Required for transactions
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);

        // Idempotence is automatically enabled when transactional.id is set
        // but set explicitly for clarity
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return props;
    }
}
