package com.example.springbootkafkatemplate.producer;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, MyMessage> kafkaTemplate;

    @Value("${app.kafka.topic.name}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, MyMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a MyMessage object to the configured Kafka topic.
     * The message key is derived from the MyMessage's ID.
     *
     * @param messageContent The content of the message to be sent.
     * @return A CompletableFuture that completes with the SendResult when the message is sent.
     */
    public CompletableFuture<SendResult<String, MyMessage>> sendMessage(String messageContent) {
        // Create a MyMessage instance with a unique ID and current timestamp
        MyMessage myMessage = new MyMessage(
                UUID.randomUUID().toString(),
                messageContent,
                LocalDateTime.now()
        );

        // KafkaTemplate's send method returns a ListenableFuture, which can be converted to CompletableFuture
        // for easier asynchronous processing.
        CompletableFuture<SendResult<String, MyMessage>> future = kafkaTemplate.send(topicName, myMessage.getId(), myMessage)
                .completable();

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Message sent successfully: [Key: {}, Value: {}, Topic: {}, Partition: {}, Offset: {}]",
                        result.getProducerRecord().key(),
                        result.getProducerRecord().value(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                LOGGER.error("Failed to send message: [Key: {}, Value: {}] due to: {}",
                        myMessage.getId(), myMessage, ex.getMessage());
            }
        });

        return future;
    }
}
