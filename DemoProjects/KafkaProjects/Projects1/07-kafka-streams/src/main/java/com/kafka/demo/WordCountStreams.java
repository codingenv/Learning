package com.kafka.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * MODULE 7 — KAFKA STREAMS
 *
 * WHAT IS KAFKA STREAMS?
 *   A client library for building stateful, fault-tolerant stream processing
 *   applications directly on top of Kafka. No separate cluster needed — it runs
 *   as a library inside your application.
 *
 * KEY CONCEPTS:
 *   Stream (KStream)    — unbounded sequence of records; each record is independent
 *   Table  (KTable)     — changelog stream; each record is an update to a key
 *   GlobalKTable        — replicated to all instances (for lookups/joins)
 *   Topology            — the DAG of stream processing steps
 *   State Store         — local RocksDB store for stateful ops (counts, joins)
 *   Changelog topic     — Kafka topic backing a state store (for fault tolerance)
 *
 * STREAM PROCESSING OPERATIONS:
 *   Stateless: filter, map, flatMap, branch, merge, foreach, peek
 *   Stateful:  count, aggregate, reduce, join, windowed aggregations
 *
 * THIS DEMO:
 *   Classic word-count. Reads sentences from "streams-input", counts words,
 *   writes results to "streams-wordcount".
 *
 *   Input:  "hello world" → "hello kafka" → "hello world kafka"
 *   Output: "hello":3, "world":2, "kafka":2
 *
 * PREREQUISITE: Create topics first:
 *   kafka-topics.sh --create --topic streams-input     --partitions 1 --replication-factor 1
 *   kafka-topics.sh --create --topic streams-wordcount --partitions 1 --replication-factor 1
 *
 * RUN:  ./gradlew :07-kafka-streams:run -PmainClass=com.kafka.demo.WordCountStreams
 */
public class WordCountStreams {

    private static final Logger log = LoggerFactory.getLogger(WordCountStreams.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Default serdes for keys and values
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Build the processing topology
        Topology topology = buildTopology();
        log.info("Topology:\n{}", topology.describe());

        KafkaStreams streams = new KafkaStreams(topology, props);

        // Graceful shutdown
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down streams...");
            streams.close();
            latch.countDown();
        }));

        try {
            streams.start();
            log.info("Streams started. Waiting for shutdown signal...");
            latch.await();
        } catch (Exception e) {
            log.error("Error", e);
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------

    static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Step 1: Read from input topic (key=null, value=sentence)
        KStream<String, String> textLines = builder.stream("streams-input");

        // Step 2: Split each sentence into words, lowercase, use word as key
        KStream<String, String> words = textLines
                .peek((k, v) -> log.info("Input: key={} value={}", k, v))
                .flatMapValues(line -> Arrays.asList(line.toLowerCase().split("\\W+")))
                .filter((key, word) -> !word.isBlank())
                .selectKey((key, word) -> word);  // now the key IS the word

        // Step 3: Count occurrences of each word
        // groupByKey + count() maintains a state store (RocksDB locally, changelog in Kafka)
        KTable<String, Long> wordCounts = words
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .count(Materialized.as("word-counts-store"));

        // Step 4: Write results to output topic (key=word, value=count)
        wordCounts
                .toStream()
                .peek((word, count) -> log.info("Count: '{}' = {}", word, count))
                .to("streams-wordcount", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }
}
