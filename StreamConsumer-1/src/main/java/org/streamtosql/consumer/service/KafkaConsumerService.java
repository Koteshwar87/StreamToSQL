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
        try {
            System.out.println("🔹 Received Message from Kafka: " + message);  // ✅ Log raw message

            JsonNode jsonNode = objectMapper.readTree(String.valueOf(message));
            String dataType = jsonNode.get("dataType").asText();  // ✅ Read "dataType" field

            // ✅ Process message based on dataType
            switch (dataType) {
                case "HEADER":
                    Header header = objectMapper.treeToValue(jsonNode, Header.class);
                    processHeader(header);
                    break;
                case "DATA":
                    OrderItems data = objectMapper.treeToValue(jsonNode, OrderItems.class);
                    processData(data);
                    break;
                case "FOOTER":
                    Footer footer = objectMapper.treeToValue(jsonNode, Footer.class);
                    processFooter(footer);
                    break;
                default:
                    System.err.println("⚠️ Unknown dataType received: " + dataType);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to process message: " + e.getMessage());
            e.printStackTrace();
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