package com.kafka.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * MODULE 7 — WINDOWED AGGREGATIONS
 *
 * TIME CONCEPTS IN KAFKA STREAMS:
 *
 *   EVENT TIME   — the timestamp embedded in the record (when the event actually occurred)
 *                  Best for analytics but requires handling out-of-order events.
 *
 *   PROCESSING TIME — the wall-clock time when the record is processed
 *                  Simpler but skews results if consumers fall behind.
 *
 *   INGESTION TIME — the timestamp assigned by the Kafka broker when the record arrived
 *
 * WINDOW TYPES:
 *
 *   TUMBLING (TimeWindows):
 *     Fixed-size, non-overlapping windows. Like buckets.
 *     "Count events per 1-minute bucket"
 *     |──────1min──────|──────1min──────|──────1min──────|
 *
 *   HOPPING (TimeWindows with advanceBy):
 *     Fixed-size, overlapping windows.
 *     "Count events in every 1-min window, advancing every 30s"
 *     |──────1min──────|
 *            |──────1min──────|
 *                   |──────1min──────|
 *
 *   SLIDING (SlidingWindows):
 *     Defined by max time difference between records in a window.
 *     "All events within 5 minutes of each other"
 *
 *   SESSION (SessionWindows):
 *     Activity-based. Window closes after an inactivity gap.
 *     "Group events until there's a 30-second gap in activity"
 *
 * THIS DEMO: count page views per URL per 1-minute tumbling window.
 *
 * Input topic  "pageviews"  — key=userId, value=url
 * Output topic "pageview-counts" — key=url@window, value=count
 *
 * RUN:  ./gradlew :07-kafka-streams:run -PmainClass=com.kafka.demo.WindowedAggregationStreams
 */
public class WindowedAggregationStreams {

    private static final Logger log = LoggerFactory.getLogger(WindowedAggregationStreams.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "pageview-counter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Grace period: how long to accept late-arriving records
        // After windowSize + grace, late records are dropped
        Duration windowSize  = Duration.ofMinutes(1);
        Duration gracePeriod = Duration.ofSeconds(10);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> pageviews = builder.stream("pageviews");

        pageviews
            .selectKey((userId, url) -> url)     // re-key by URL
            .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
            .windowedBy(
                TimeWindows.ofSizeAndGrace(windowSize, gracePeriod)
            )
            .count(Materialized.as("pageview-window-store"))
            .toStream()
            .peek((windowedKey, count) -> {
                log.info("URL={} window=[{} → {}] count={}",
                        windowedKey.key(),
                        windowedKey.window().startTime(),
                        windowedKey.window().endTime(),
                        count);
            })
            .map((windowedKey, count) ->
                    KeyValue.pair(windowedKey.key() + "@" + windowedKey.window().start(), count))
            .to("pageview-counts", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
        }));

        streams.start();
        latch.await();
    }
}
