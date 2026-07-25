package com.kafka.demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * MODULE 2 — Custom Partitioner
 *
 * CONCEPT: By default Kafka uses a hash of the record key to choose a partition.
 * You can override this with a custom Partitioner to implement any routing logic:
 *   • VIP customers → dedicated partition (lower latency)
 *   • Geographic routing → EU records to partition 0, US to partition 1
 *   • Load-based routing based on current partition depth
 *
 * This demo routes "premium" orders to partition 0 and all others round-robin.
 *
 * RUN:  ./gradlew :02-producers:run -PmainClass=com.kafka.demo.CustomPartitionerDemo
 */
public class CustomPartitionerDemo {

    private static final Logger log = LoggerFactory.getLogger(CustomPartitionerDemo.class);
    private static final String TOPIC = "demo-basic";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        // Register our custom partitioner
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, PremiumPartitioner.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            String[] orderTypes = {"premium", "standard", "premium", "standard", "premium"};

            for (int i = 0; i < orderTypes.length; i++) {
                String key   = orderTypes[i];
                String value = "order-" + i;

                RecordMetadata meta = producer.send(
                        new ProducerRecord<>(TOPIC, key, value)).get();

                log.info("Sent key={} value={} → partition={}", key, value, meta.partition());
            }
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Custom partitioner: premium orders always land on partition 0.
     * All other orders are distributed across partitions 1..N-1.
     */
    public static class PremiumPartitioner implements Partitioner {

        private int roundRobinCounter = 0;

        @Override
        public int partition(String topic, Object key, byte[] keyBytes,
                             Object value, byte[] valueBytes, Cluster cluster) {
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            int numPartitions = partitions.size();

            if ("premium".equals(key)) {
                return 0;  // dedicated fast lane
            }

            // Round-robin over partitions 1..N-1 (skip 0 which is for premium)
            if (numPartitions <= 1) return 0;
            return 1 + (roundRobinCounter++ % (numPartitions - 1));
        }

        @Override public void close() {}
        @Override public void configure(Map<String, ?> configs) {}
    }
}
