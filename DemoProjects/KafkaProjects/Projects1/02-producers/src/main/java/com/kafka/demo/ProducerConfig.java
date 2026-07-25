package com.kafka.demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Central place to build producer Properties.
 * Every demo class uses this so config stays in one spot.
 */
public class ProducerConfig {

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String TOPIC             = "demo-topic";

    public static Properties baseProperties() {
        Properties props = new Properties();

        // Where is the Kafka cluster?
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);

        // How to convert keys/values to bytes
        props.put("key.serializer",   StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        return props;
    }

    public static KafkaProducer<String, String> createProducer() {
        return new KafkaProducer<>(baseProperties());
    }
}
