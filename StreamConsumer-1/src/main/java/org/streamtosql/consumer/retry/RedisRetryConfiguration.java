package org.streamtosql.consumer.retry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RedisRetryConfiguration {
    @Bean
    public RetryTemplate redisRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Retry Policy: retry 3 times max
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        // Backoff Policy: exponential delay between retries
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500); // 0.5 second
        backOffPolicy.setMultiplier(2);        // Doubles on each retry
        backOffPolicy.setMaxInterval(3000);    // Max 3 seconds

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
