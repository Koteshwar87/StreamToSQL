package org.streamtosql.consumer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;
import org.streamtosql.consumer.model.BaseMessage;
import org.streamtosql.consumer.model.Footer;
import org.streamtosql.consumer.model.Header;
import org.streamtosql.consumer.model.OrderItems;

@Service
@Slf4j
public class KafkaConsumerService {

//    @Value("${spring.kafka.topic.order-items}")  // Load topic name from application.yml
//    private String orderItemsTopic;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaConsumerService initialized!");
    }

    /*@Autowired
    private KafkaListenerEndpointRegistry registry;

    @PostConstruct
    public void checkListeners() {
        log.info("🟢 Registered Listeners: {}", registry.getListenerContainers().size());
        registry.getListenerContainers().forEach(container ->
                log.info("🟢 Listener ID: {}, State: {}", container.getListenerId(), container.isRunning() ? "RUNNING" : "NOT RUNNING"));
    }*/

//    @KafkaListener(topics = "#{__listener.orderItemsTopic}", groupId = "order-items-consumer-group",
    @KafkaListener(
            topics = "order-items-topic",
            groupId = "order-items-consumer-group",
            containerFactory = "orderItemsKafkaListenerContainerFactory"
    )
    public void consume(BaseMessage message) {
        log.info("🔹 Received message: {}", message);

        if (message instanceof Header header) {
            processHeader(header);
        } else if (message instanceof OrderItems orderItems) {
            processData(orderItems);
        } else if (message instanceof Footer footer) {
            processFooter(footer);
        } else {
            log.warn("⚠️ Unknown message type: {}", message.getClass().getSimpleName());
        }
    }

    private void processHeader(Header header) {
        System.out.println("✅ Processing Header: " + header);
    }

    private void processData(OrderItems data) {
        System.out.println("✅ Processing Data: " + data);
    }

    private void processFooter(Footer footer) {
        System.out.println("✅ Processing Footer: " + footer);
    }
}