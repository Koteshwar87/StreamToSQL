package org.streamtosql.producer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.streamtosql.producer.model.*;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.topic.order-items}")
    private String orderItemsTopic;

    private final KafkaTemplate<String, BaseMessage> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, BaseMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessages() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            // Create Header message
            Header header = new Header();
            header.setId((long) i);
            header.setCategoryEnum(CategoryEnum.ORDER_ITEMS);
            kafkaTemplate.send(orderItemsTopic, header);
            System.out.println("Produced Header: " + header);
            Thread.sleep(1000);

            // Create OrderItems message
            OrderItems orderItems = new OrderItems();
            orderItems.setId((long) i);
            orderItems.setOrderId("ORD" + i);
            orderItems.setProductId("PROD" + i);
            orderItems.setQuantity(i);
            orderItems.setPrice(10.50 * i);
            kafkaTemplate.send(orderItemsTopic, orderItems);
            System.out.println("Produced OrderItems: " + orderItems);
            Thread.sleep(1000);

            // Create Footer message
            Footer footer = new Footer();
            footer.setId((long) i);
            footer.setCategoryEnum(CategoryEnum.ORDER_ITEMS);
            footer.setCount(1);
            kafkaTemplate.send(orderItemsTopic, footer);
            System.out.println("Produced Footer: " + footer);
            Thread.sleep(1000);
        }
    }
}
