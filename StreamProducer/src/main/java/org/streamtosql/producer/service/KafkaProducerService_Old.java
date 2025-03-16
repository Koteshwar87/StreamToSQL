/*
package org.streamtosql.producer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.streamtosql.producer.model.OrderEvent;

import java.util.Random;
import java.util.UUID;

@Service
public class KafkaProducerService_Old {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final Random random = new Random();

    public KafkaProducerService_Old(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void generateAndSendMessages(String topic) {
        new Thread(() -> {
            while (true) {
                try {
                    OrderEvent event = createRandomOrder();
                    kafkaTemplate.send(topic, event.getOrderId(), event);
                    System.out.println("Produced: " + event);

                    Thread.sleep(2000); // Sleep 2 seconds between messages
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Producer thread interrupted", e);
                }
            }
        }).start();
    }

    private OrderEvent createRandomOrder() {
        String orderId = UUID.randomUUID().toString();
        String[] products = {"Laptop", "Phone", "Tablet", "Headphones", "Smartwatch"};
        String product = products[random.nextInt(products.length)];
        double price = 50 + (1000 * random.nextDouble());
        int quantity = 1 + random.nextInt(5);

        return new OrderEvent(orderId, product, price, quantity);
    }
}
*/
