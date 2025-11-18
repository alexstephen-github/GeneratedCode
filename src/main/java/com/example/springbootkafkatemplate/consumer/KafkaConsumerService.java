package com.example.springbootkafkatemplate.consumer;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Listens for messages on the configured Kafka topic.
     * The @KafkaListener annotation handles subscribing to the topic and deserializing the message.
     *
     * @param message The deserialized MyMessage object received from Kafka.
     * @param key     The key of the Kafka record.
     * @param partition The partition from which the message was received.
     * @param offset    The offset of the message within the partition.
     */
    @KafkaListener(topics = "${app.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(
            @Payload MyMessage message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        LOGGER.info("Received Message in group '{}': [Key: {}, Partition: {}, Offset: {}, Message: {}]",
                groupId, key, partition, offset, message);

        // Here you would typically process the message, e.g., save to a database,
        // call another service, perform business logic, etc.
        // For demonstration, we just log it.
    }

    // You can also access the group ID directly if needed (e.g., from properties)
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
}
