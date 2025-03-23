package org.streamtosql.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.streamtosql.consumer.model.BaseMessage;

@Service
@Slf4j
public class KafkaConsumerService {

    private final RedisMessageAggregator aggregator;

    public KafkaConsumerService(RedisMessageAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @KafkaListener(topics = "order-items-topic", groupId = "order-items-consumer-group",
            containerFactory = "orderItemsKafkaListenerContainerFactory")
    public void consume(BaseMessage message) {
        log.info("🔹 Received: {}", message.getClass().getSimpleName());
        aggregator.storeMessage(message);
    }
}