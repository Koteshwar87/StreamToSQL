package org.streamtosql.consumer.configuration;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.streamtosql.consumer.model.BaseMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaConsumerConfig initialized!");
    }

    @Bean
    public ConsumerFactory<String, BaseMessage> orderItemsConsumerFactory() {
        System.out.println("✅ Creating ConsumerFactory");
        Map<String, Object> config = new HashMap<>();

        // ✅ Kafka Bootstrap Server
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // ✅ Consumer Group ID
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-items-consumer-group");

        // ✅ Consumer should start from the earliest offset
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // ✅ Disabling Auto Commit to Handle Manual Acknowledgment
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // ✅ Using String Deserializer for the Key
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // ✅ Fix: Ensure `ErrorHandlingDeserializer` is used correctly
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // ✅ Security: Trusted Packages
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "org.streamtosql.consumer.model");

        // ✅ Fix: Disable Type Headers to Prevent Class Name Issues
//        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // ✅ Fix: Construct `JsonDeserializer` Properly and Avoid Auto-Configuration
        JsonDeserializer<BaseMessage> jsonDeserializer =
                new JsonDeserializer<>(BaseMessage.class, false);  // ✅ Pass `false` to disable auto-configuration
        jsonDeserializer.addTrustedPackages("org.streamtosql.consumer.model");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer) // ✅ Ensures proper deserialization
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BaseMessage> orderItemsKafkaListenerContainerFactory() {
        System.out.println("✅ Creating KafkaListenerContainerFactory");
        ConcurrentKafkaListenerContainerFactory<String, BaseMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // ✅ Setting the Consumer Factory
        factory.setConsumerFactory(orderItemsConsumerFactory());
        // ✅ Manual acknowledgment mode
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
