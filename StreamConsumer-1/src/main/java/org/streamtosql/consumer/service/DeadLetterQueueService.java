package org.streamtosql.consumer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.streamtosql.consumer.dto.BaseMessage;

//@Service
public class DeadLetterQueueService {

    private final KafkaTemplate<String, BaseMessage> kafkaTemplate;

    public DeadLetterQueueService(KafkaTemplate<String, BaseMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToDLQ(String topic, String key, String value) {
//        kafkaTemplate.send(topic, key, value);
//        System.out.println("Sent failed message to DLQ: " + value);
    }
}
