package com.example.springbootkafkatemplate.config;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Configures the Kafka ProducerFactory.
     * This factory is responsible for creating Kafka Producer instances.
     * It sets up the necessary properties for the producer.
     *
     * @return A ProducerFactory for MyMessage objects.
     */
    @Bean
    public ProducerFactory<String, MyMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Use JsonSerializer to serialize MyMessage objects to JSON.
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Optionally configure other producer properties, e.g., acks, retries, batch size
        // configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // configProps.put(ProducerConfig.RETRIES_CONFIG, 0);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configures the KafkaTemplate.
     * KafkaTemplate is a high-level abstraction for sending messages to Kafka topics.
     * It simplifies the process of sending messages and handles low-level details.
     *
     * @param producerFactory The ProducerFactory to use for creating producers.
     * @return A KafkaTemplate for sending MyMessage objects.
     */
    @Bean
    public KafkaTemplate<String, MyMessage> kafkaTemplate(ProducerFactory<String, MyMessage> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
