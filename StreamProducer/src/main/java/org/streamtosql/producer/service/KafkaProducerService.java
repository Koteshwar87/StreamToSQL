package org.streamtosql.producer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.streamtosql.producer.model.*;

import java.util.Random;
import java.util.UUID;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.topic.order-items}")
    private String orderItemsTopic;

    private final KafkaTemplate<String, BaseMessage> kafkaTemplate;
    private final Random random = new Random();

    public KafkaProducerService(KafkaTemplate<String, BaseMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessages() throws InterruptedException {
        for (int i = 1; i <= 5; i++) {
            String correlationId = UUID.randomUUID().toString();  // 🔄 Unique ID per message group

            // Header
            Header header = new Header();
            header.setId((long) i);
            header.setDataTypeEnum(DataTypeEnum.HEADER);
            header.setCategoryEnum(CategoryEnum.ORDER_ITEMS);
            header.setCorrelationId(correlationId);
            kafkaTemplate.send(orderItemsTopic, correlationId, header); // ✅ use correlationId as Kafka key
            System.out.println("Produced Header: " + header);
            Thread.sleep(50);

            // OrderItems (random 1–100)
            int itemCount = new Random().nextInt(100) + 1;
            for (int j = 1; j <= itemCount; j++) {
                OrderItems orderItems = new OrderItems();
                orderItems.setId((long) j);
                orderItems.setDataTypeEnum(DataTypeEnum.DATA);
                orderItems.setOrderId("ORD" + i + "-" + j);
                orderItems.setProductId("PROD" + j);
                orderItems.setQuantity(j);
                orderItems.setPrice(10.50 * j);
                orderItems.setCorrelationId(correlationId);
                kafkaTemplate.send(orderItemsTopic, correlationId, orderItems); // ✅ same key
                System.out.println("Produced OrderItem: " + orderItems);
                Thread.sleep(100);
            }

            // Footer
            Footer footer = new Footer();
            footer.setId((long) i);
            footer.setDataTypeEnum(DataTypeEnum.FOOTER);
            footer.setCategoryEnum(CategoryEnum.ORDER_ITEMS);
            footer.setCount(itemCount);
            footer.setCorrelationId(correlationId);
            kafkaTemplate.send(orderItemsTopic, correlationId, footer); // ✅ same key
            System.out.println("Produced Footer: " + footer + "correlation_id: " + correlationId);
            Thread.sleep(50);
        }
    }

}
