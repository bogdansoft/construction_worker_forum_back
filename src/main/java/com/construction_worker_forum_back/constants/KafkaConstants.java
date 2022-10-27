package com.construction_worker_forum_back.constants;

public class KafkaConstants {
    private KafkaConstants() {
        throw new IllegalStateException("Utility class.");
    }

    public static final String KAFKA_TOPIC = "kafka-chat";
    public static final String GROUP_ID = "kafka-sandbox";
    public static final String KAFKA_BROKER = "localhost:9092";
}
