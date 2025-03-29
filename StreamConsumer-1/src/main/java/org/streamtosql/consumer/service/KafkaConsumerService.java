package org.streamtosql.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.streamtosql.consumer.dto.BaseMessage;

@Service
@Slf4j
public class KafkaConsumerService {

    private final RedisMessageAggregator aggregator;

    public KafkaConsumerService(RedisMessageAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @KafkaListener(topics = "order-items-topic", groupId = "order-items-consumer-group",
            containerFactory = "orderItemsKafkaListenerContainerFactory")
    public void consume(BaseMessage message, Acknowledgment ack) {
        try {
            log.info("🔹 Received: {}", message.getClass().getSimpleName());

            aggregator.storeMessage(message); // ✅ Redis write

            ack.acknowledge();  // ✅ Only after Redis write succeeds
            log.info("✅ Acknowledged message with correlationId={}", message.getCorrelationId());

        } catch (Exception e) {
            log.error("❌ Failed to process message: {}", message, e);
            // Don't acknowledge = message will be retried
        }
    }
}