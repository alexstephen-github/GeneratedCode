package com.example.springbootkafkatemplate.controller;

import com.example.springbootkafkatemplate.model.MyMessage;
import com.example.springbootkafkatemplate.producer.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/kafka")
public class MessageController {

    private final KafkaProducerService kafkaProducerService;

    public MessageController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * REST endpoint to send a message to Kafka.
     *
     * @param messageRequest A simple string representing the message content.
     * @return A ResponseEntity indicating the status of the message sending operation.
     */
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<String>> sendMessage(@RequestBody String messageRequest) {
        // Delegate the message sending to the KafkaProducerService
        return kafkaProducerService.sendMessage(messageRequest)
                .thenApply(result -> ResponseEntity.ok("Message sent successfully with ID: " + result.getProducerRecord().key()))
                .exceptionally(ex -> ResponseEntity.internalServerError().body("Failed to send message: " + ex.getMessage()));
    }
}
