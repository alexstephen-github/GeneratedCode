package com.example.springbootkafkatemplate.config;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka // Enables detection of @KafkaListener annotations
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Configures the Kafka ConsumerFactory.
     * This factory is responsible for creating Kafka Consumer instances.
     * It sets up the necessary properties for the consumer.
     *
     * @return A ConsumerFactory for MyMessage objects.
     */
    @Bean
    public ConsumerFactory<String, MyMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // Use StringDeserializer for keys
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Use JsonDeserializer for values, specifying the target type (MyMessage.class)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Important for JsonDeserializer: specify the trusted packages or a type mapper.
        // If not specified, deserialization might fail for custom types.
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.springbootkafkatemplate.model");
        // Or for all packages:
        // props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Optionally configure other consumer properties
        // props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(), // Key deserializer instance
                new JsonDeserializer<>(MyMessage.class)); // Value deserializer instance (specifying target type)
    }

    /**
     * Configures the ConcurrentKafkaListenerContainerFactory.
     * This factory is used by @KafkaListener to create the underlying consumer containers.
     * It connects the ConsumerFactory to the listener infrastructure.
     *
     * @param consumerFactory The ConsumerFactory to use for creating consumers.
     * @return A ConcurrentKafkaListenerContainerFactory for MyMessage objects.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MyMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, MyMessage> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, MyMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // Set concurrency if you want more than one consumer thread listening to partitions.
        // factory.setConcurrency(3); // Example: 3 listener threads
        return factory;
    }
}
