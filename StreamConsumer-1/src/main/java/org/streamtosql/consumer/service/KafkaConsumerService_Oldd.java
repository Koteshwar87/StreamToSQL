package org.streamtosql.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.streamtosql.consumer.model.BaseMessage;
import org.streamtosql.consumer.repository.EntityJdbcRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaConsumerService_Oldd {

    private final EntityJdbcRepository repository;
    private final DeadLetterQueueService deadLetterQueueService;

    public KafkaConsumerService_Oldd(EntityJdbcRepository repository, DeadLetterQueueService dlqService) {
        this.repository = repository;
        this.deadLetterQueueService = dlqService;
    }

    @KafkaListener(
            topics = "existing-topic-name",
            groupId = "my-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        List<BaseMessage> batch = new ArrayList<>();

        for (ConsumerRecord<String, String> record : records) {
//            batch.add(new KafkaMessage(1l, "Test", "Value"));
        }

        try {
//            repository.batchInsert(batch);
            ack.acknowledge(); // Commit offset only after successful processing
        } catch (Exception e) {
            System.err.println("Batch insert failed, sending to DLQ...");
            for (ConsumerRecord<String, String> record : records) {
                deadLetterQueueService.sendToDLQ("my-topic-dlq", record.key(), record.value());
            }
        }
    }
}
