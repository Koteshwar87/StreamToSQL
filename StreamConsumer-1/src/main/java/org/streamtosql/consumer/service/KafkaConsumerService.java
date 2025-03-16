package org.streamtosql.consumer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.streamtosql.consumer.model.BaseMessage;

@Service
public class KafkaConsumerService {

    @Value("${spring.kafka.topic.order-items}")  // Load topic name from application.yml
    private String orderItemsTopic;

//    @KafkaListener(topics = "#{__listener.orderItemsTopic}", groupId = "order-items-consumer-group",
            @KafkaListener(topics = "order-items-topic", groupId = "order-items-consumer-group",
            containerFactory = "orderItemsKafkaListenerContainerFactory")
    public void consume(BaseMessage message) {
        System.out.println("Received message from topic " + orderItemsTopic + ": " + message);
    }
}