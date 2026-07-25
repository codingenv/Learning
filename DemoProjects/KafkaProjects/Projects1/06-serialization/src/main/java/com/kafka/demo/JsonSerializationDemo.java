package com.kafka.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * MODULE 6 — SERIALIZATION & DESERIALIZATION
 *
 * CONCEPTS:
 *   Kafka stores bytes on the wire. You need to convert your objects
 *   to/from bytes using Serializer / Deserializer pairs.
 *
 *   BUILT-IN SERIALIZERS (in kafka-clients):
 *     StringSerializer / StringDeserializer
 *     ByteArraySerializer / ByteArrayDeserializer
 *     IntegerSerializer / LongSerializer / DoubleSerializer
 *
 *   CUSTOM SERIALIZERS:
 *     • JSON (using Jackson) — human-readable, flexible schema, larger payload
 *     • Avro (with Schema Registry) — compact, schema-enforced, evolution support
 *     • Protobuf — compact, language-agnostic, schema evolution
 *
 *   SCHEMA EVOLUTION PROBLEM:
 *     What happens when you add/remove fields in your message type?
 *     - JSON: lenient (missing fields → null, extra fields ignored)
 *     - Avro: strict but controlled — schema registry enforces compatibility rules
 *
 * This demo shows custom JSON serializer/deserializer for a domain object.
 *
 * RUN:  ./gradlew :06-serialization:run -PmainClass=com.kafka.demo.JsonSerializationDemo
 */
public class JsonSerializationDemo {

    private static final Logger log = LoggerFactory.getLogger(JsonSerializationDemo.class);
    private static final String TOPIC = "demo-orders";

    public static void main(String[] args) throws Exception {
        boolean produceOnly = args.length > 0 && args[0].equals("produce-only");

        produceOrders();

        if (!produceOnly) {
            consumeOrders();
        } else {
            log.info("Produce-only mode — skipping consumer. Check Kafka UI → demo-orders → Partitions.");
        }
    }

    // ---- Domain object ------------------------------------------------------

    public static class Order {
        public String  orderId;
        public String  customerId;
        public double  amount;
        public String  status;
        public long    timestamp;

        // Jackson requires a no-arg constructor
        public Order() {}

        public Order(String orderId, String customerId, double amount, String status) {
            this.orderId    = orderId;
            this.customerId = customerId;
            this.amount     = amount;
            this.status     = status;
            this.timestamp  = System.currentTimeMillis();
        }

        @Override public String toString() {
            return String.format("Order{id=%s, customer=%s, amount=%.2f, status=%s}",
                    orderId, customerId, amount, status);
        }
    }

    // ---- Custom Serializer --------------------------------------------------

    public static class JsonSerializer<T> implements Serializer<T> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, T data) {
            if (data == null) return null;
            try {
                return mapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("JSON serialization failed", e);
            }
        }
    }

    // ---- Custom Deserializer ------------------------------------------------

    public static class JsonDeserializer<T> implements Deserializer<T> {
        private final ObjectMapper mapper = new ObjectMapper();
        private Class<T> targetType;

        public JsonDeserializer() {}
        public JsonDeserializer(Class<T> targetType) { this.targetType = targetType; }

        @Override
        @SuppressWarnings("unchecked")
        public void configure(Map<String, ?> configs, boolean isKey) {
            // targetType can also be passed via config map
            if (targetType == null) {
                String className = (String) configs.get("value.deserializer.target.type");
                if (className != null) {
                    try { targetType = (Class<T>) Class.forName(className); }
                    catch (ClassNotFoundException e) { throw new RuntimeException(e); }
                }
            }
        }

        @Override
        public T deserialize(String topic, byte[] data) {
            if (data == null) return null;
            try {
                return mapper.readValue(data, targetType);
            } catch (Exception e) {
                throw new RuntimeException("JSON deserialization failed", e);
            }
        }
    }

    // ---- Producer -----------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static void produceOrders() throws Exception {
        log.info("\n=== Producing Orders (JSON) ===");

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Use our custom JSON serializer for values
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        try (KafkaProducer<String, Order> producer = new KafkaProducer<>(props)) {
            Order[] orders = {
                new Order("ORD-001", "cust-A", 99.99,  "PLACED"),
                new Order("ORD-002", "cust-B", 249.00, "PLACED"),
                new Order("ORD-003", "cust-A", 15.50,  "SHIPPED"),
            };

            for (Order order : orders) {
                ProducerRecord<String, Order> record =
                        new ProducerRecord<>(TOPIC, order.customerId, order);

                RecordMetadata meta = producer.send(record).get();
                log.info("Sent {} → partition={} offset={}", order, meta.partition(), meta.offset());
            }
        }
    }

    // ---- Consumer -----------------------------------------------------------

    private static void consumeOrders() {
        log.info("\n=== Consuming Orders (JSON) ===");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Pass the target class for deserialization
        props.put("value.deserializer.target.type", Order.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "json-demo-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(TOPIC));

            // WHY A LOOP?
            // On the very first poll() after subscribe(), Kafka triggers a partition
            // rebalance — assigning partitions to this consumer. That handshake takes
            // a moment. A single poll() often returns 0 records because the assignment
            // isn't ready yet. We keep polling until we get records (or time out after
            // 10 attempts = ~10 seconds).
            ConsumerRecords<String, Order> records = ConsumerRecords.empty();
            int attempts = 0;
            while (records.isEmpty() && attempts < 10) {
                records = consumer.poll(Duration.ofSeconds(1));
                attempts++;
                log.info("Poll attempt {} — got {} records", attempts, records.count());
            }

            log.info("Total received: {} records", records.count());
            for (ConsumerRecord<String, Order> r : records) {
                log.info("  partition={} offset={} key={} → {}",
                        r.partition(), r.offset(), r.key(), r.value());
            }

            if (!records.isEmpty()) consumer.commitSync();
        }
    }
}
