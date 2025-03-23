package org.streamtosql.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.streamtosql.consumer.model.BaseMessage;
import org.streamtosql.consumer.model.Footer;
import org.streamtosql.consumer.model.Header;
import org.streamtosql.consumer.model.OrderItems;

import java.util.List;

@Service
public class RedisMessageAggregator {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageAggregator(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeMessage(BaseMessage message) {
        String correlationId = message.getCorrelationId();
        String key = "group:" + correlationId;

        try {
            String json = objectMapper.writeValueAsString(message);

            if (message instanceof Header) {
                redisTemplate.opsForHash().put(key, "header", json);
            } else if (message instanceof Footer) {
                redisTemplate.opsForHash().put(key, "footer", json);
            } else if (message instanceof OrderItems) {
                String listKey = "orderItems:" + correlationId;
                redisTemplate.opsForList().rightPush(listKey, json);
            }

            checkAndProcessIfComplete(correlationId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    private void checkAndProcessIfComplete(String correlationId) throws JsonProcessingException {
        String key = "group:" + correlationId;
        String headerJson = (String) redisTemplate.opsForHash().get(key, "header");
        String footerJson = (String) redisTemplate.opsForHash().get(key, "footer");
        List<String> items = redisTemplate.opsForList().range("orderItems:" + correlationId, 0, -1);

        if (headerJson != null && footerJson != null && items != null) {
            Footer footer = objectMapper.readValue(footerJson, Footer.class);
            if (footer.getCount() == items.size()) {
                Header header = objectMapper.readValue(headerJson, Header.class);
                List<OrderItems> orderItemsList = items.stream()
                        .map(json -> {
                            try {
                                return objectMapper.readValue(json, OrderItems.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();

                processGroup(header, orderItemsList, footer);

                // Clean up Redis
                redisTemplate.delete(key);
                redisTemplate.delete("orderItems:" + correlationId);
            }
        }
    }

    private void processGroup(Header header, List<OrderItems> items, Footer footer) {
        System.out.println("✅ Completed Message Group [correlationId: " + header.getCorrelationId() + "]");
        System.out.println("--------------------------------------------------");

        // 🔹 Print Header
        System.out.println("🟩 Header:");
        System.out.println("  ID           : " + header.getId());
        System.out.println("  Category     : " + header.getCategoryEnum());
        System.out.println("  DataType     : " + header.getDataTypeEnum());

        // 🔹 Print OrderItems
        System.out.println("🟦 Order Items (" + items.size() + "):");
        for (int i = 0; i < items.size(); i++) {
            OrderItems item = items.get(i);
            System.out.println("  - Item " + (i + 1) + ":");
            System.out.println("      Order ID   : " + item.getOrderId());
            System.out.println("      Product ID : " + item.getProductId());
            System.out.println("      Quantity   : " + item.getQuantity());
            System.out.println("      Price      : " + item.getPrice());
        }

        // 🔹 Print Footer
        System.out.println("🟥 Footer:");
        System.out.println("  ID           : " + footer.getId());
        System.out.println("  DataType     : " + footer.getDataTypeEnum());
        System.out.println("  Category     : " + footer.getCategoryEnum());
        System.out.println("  Total Count  : " + footer.getCount());

        System.out.println("--------------------------------------------------\n");
    }

}
