CREATE TABLE redis_failure_log (
    id BIGSERIAL PRIMARY KEY,                                   -- ✅ PostgreSQL auto-increment
    correlation_id VARCHAR(100),                                -- Used for tracking full message group
    message_key VARCHAR(255),                                   -- Kafka message key, if available
    message_type VARCHAR(50),                                   -- HEADER / DATA / FOOTER
    payload JSONB NOT NULL,                                     -- ✅ Use JSONB in PostgreSQL for indexing/query
    redis_key VARCHAR(255),                                     -- Redis key that was being used
    error_message TEXT,                                         -- Root cause for failure
    retry_count INT DEFAULT 0,                                  -- Tracks number of retry attempts
    status VARCHAR(20) DEFAULT 'PENDING',                       -- PENDING / RETRIED / FAILED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,             -- Original failure timestamp
    last_retry_at TIMESTAMP NULL                                -- Last retry attempt timestamp
);

CREATE TABLE order_items (
    correlation_id VARCHAR(100) NOT NULL,
    order_id VARCHAR(100) NOT NULL,
    product_id VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (correlation_id, order_id)
);

