package org.streamtosql.producer.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.streamtosql.producer.service.KafkaProducerService;

@Component
public class KafkaProducerRunner implements CommandLineRunner {

//    private final KafkaProducerService_Old producerService;
    private final KafkaProducerService kafkaProducerService;

    public KafkaProducerRunner(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public void run(String... args) throws InterruptedException {
//        String topic = "test-topic";
//        producerService.generateAndSendMessages(topic);
        kafkaProducerService.sendMessages();
    }
}
