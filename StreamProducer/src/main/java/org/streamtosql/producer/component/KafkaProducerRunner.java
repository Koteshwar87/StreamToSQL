package org.streamtosql.producer.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.streamtosql.producer.service.KafkaProducerService;

@Component
public class KafkaProducerRunner implements CommandLineRunner {

    private final KafkaProducerService producerService;

    public KafkaProducerRunner(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public void run(String... args) {
        String topic = "test-topic";
        producerService.generateAndSendMessages(topic);
    }
}
