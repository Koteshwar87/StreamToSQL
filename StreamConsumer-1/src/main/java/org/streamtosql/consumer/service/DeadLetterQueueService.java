package org.streamtosql.consumer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public DeadLetterQueueService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToDLQ(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
        System.out.println("Sent failed message to DLQ: " + value);
    }
}
