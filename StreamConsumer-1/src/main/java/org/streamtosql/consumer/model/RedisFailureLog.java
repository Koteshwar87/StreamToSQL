package org.streamtosql.consumer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "redis_failure_log")
@Data
@NoArgsConstructor
public class RedisFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "message_key")
    private String messageKey;

    @Column(name = "message_type")
    private String messageType;

    @Column(columnDefinition = "json")
    private String payload;

    @Column(name = "redis_key")
    private String redisKey;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;
}