package org.streamtosql.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.streamtosql.consumer.dto.BaseMessage;
import org.streamtosql.consumer.dto.Footer;
import org.streamtosql.consumer.dto.Header;
import org.streamtosql.consumer.dto.OrderItems;
import org.streamtosql.consumer.model.OrderItemEntity;
import org.streamtosql.consumer.model.OrderItemId;
import org.streamtosql.consumer.model.RedisFailureLog;
import org.streamtosql.consumer.repository.OrderItemRepository;
import org.streamtosql.consumer.repository.RedisFailureLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageAggregator {

    private final RedisTemplate<String, String> redisTemplate;
    private final RetryTemplate redisRetryTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisFailureLogRepository redisFailureLogRepository;
    private final OrderItemRepository orderItemRepository;

    public void storeMessage(BaseMessage message) {
        String correlationId = message.getCorrelationId();

        try {
            String json = objectMapper.writeValueAsString(message);

            // ✅ Write Header or Footer as hash entry
            if (message instanceof Header) {
                retryPutHash("group:" + correlationId, "header", json);
            } else if (message instanceof Footer) {
                retryPutHash("group:" + correlationId, "footer", json);
            }
            // ✅ Write OrderItems as list entry
            else if (message instanceof OrderItems) {
                retryPushList("orderItems:" + correlationId, json);
            }

            checkAndProcessIfComplete(correlationId);

        } catch (Exception e) {
            logRedisFailure(message, e);
            throw new RuntimeException("❌ Redis write failed after retries for correlationId=" + correlationId, e);
        }
    }

    private void checkAndProcessIfComplete(String correlationId) throws JsonProcessingException {
        String groupKey = "group:" + correlationId;
        String listKey = "orderItems:" + correlationId;

        // ✅ Read all parts with retries
        String headerJson = retryGetHash(groupKey, "header");
        String footerJson = retryGetHash(groupKey, "footer");
        List<String> items = retryGetListRange(listKey, 0, -1);

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

                // Clean up Redis after success
                retryDeleteKey(groupKey);
                retryDeleteKey(listKey);
            }
        }
    }

    // ✅ New method: log failure into DB
    private void logRedisFailure(BaseMessage message, Exception exception) {
        try {
            String correlationId = message.getCorrelationId();
            String payload = objectMapper.writeValueAsString(message);

            String redisKey = (message instanceof OrderItems)
                    ? "orderItems:" + correlationId
                    : "group:" + correlationId;

            RedisFailureLog failureLog = new RedisFailureLog();
            failureLog.setCorrelationId(correlationId);
            failureLog.setMessageKey(null); // Can be added if Kafka key is available
            failureLog.setMessageType(message.getDataTypeEnum().name());
            failureLog.setPayload(payload);
            failureLog.setRedisKey(redisKey);
            failureLog.setErrorMessage(exception.getMessage());
            failureLog.setStatus("PENDING");
            failureLog.setRetryCount(0);
            failureLog.setCreatedAt(java.time.LocalDateTime.now());

            redisFailureLogRepository.save(failureLog);

        } catch (Exception ex) {
            throw new RuntimeException("❌ Failed to log Redis failure to database", ex);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    private void processGroup(Header header, List<OrderItems> items, Footer footer) {
        log.info("✅ Aggregated group complete. Persisting OrderItems for correlationId={}", header.getCorrelationId());

        // ✅ Map DTOs to Entities
        List<OrderItemEntity> entities = items.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(new OrderItemId(header.getCorrelationId(), item.getOrderId()))
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        // ✅ Batch insert
        orderItemRepository.saveAll(entities);

        log.info("✅ Successfully inserted {} OrderItems to DB for correlationId={}",
                entities.size(), header.getCorrelationId());
        /*System.out.println("✅ Completed Message Group [correlationId: " + header.getCorrelationId() + "]");
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

        System.out.println("--------------------------------------------------\n");*/
    }


    /* To reduce duplication
    @Component
    public class RedisRetryExecutor {

        private final RetryTemplate retryTemplate;

        public RedisRetryExecutor(RetryTemplate retryTemplate) {
            this.retryTemplate = retryTemplate;
        }

        public void executeVoid(Runnable redisAction) {
            retryTemplate.execute(context -> {
                redisAction.run();
                return null;
            });
        }

        public <T> T execute(Supplier<T> redisSupplier) {
            return retryTemplate.execute(context -> redisSupplier.get());
        }
    }*/

    // Reusable Redis Retry Wrappers (SRP + DRY)
    private void retryPutHash(String redisKey, String field, String value) {
        redisRetryTemplate.execute(context -> {
            redisTemplate.opsForHash().put(redisKey, field, value);
            return null;
        }
        );
    }

    private String retryGetHash(String redisKey, String field) {
        return redisRetryTemplate.execute(context ->
                (String) redisTemplate.opsForHash().get(redisKey, field)
        );
    }

    private void retryPushList(String listKey, String value) {
        redisRetryTemplate.execute(context ->
                redisTemplate.opsForList().rightPush(listKey, value)
        );
    }

    private List<String> retryGetListRange(String listKey, long start, long end) {
        return redisRetryTemplate.execute(context ->
                redisTemplate.opsForList().range(listKey, start, end)
        );
    }

    private void retryDeleteKey(String redisKey) {
        redisRetryTemplate.execute(context ->
                redisTemplate.delete(redisKey)
        );
    }
}
