Hello there! I'd be happy to help you with a complete code template for a Spring Boot Kafka application. This template will include a producer to send messages and a consumer to receive them, along with necessary configurations, a simple data model, and unit tests for both producer and consumer components.

This setup assumes you have a Kafka broker running, either locally or accessible via a network.

---

### Project Overview

This Spring Boot application will demonstrate:
*   **Kafka Producer:** An HTTP endpoint to send custom `Message` objects to a Kafka topic.
*   **Kafka Consumer:** A listener that consumes `Message` objects from the same Kafka topic.
*   **Custom Data Model:** A `Message` class with `String` content and a `LocalDateTime` timestamp.
*   **Configuration:** Separate configurations for Kafka producer and consumer properties, including serialization/deserialization.
*   **Unit Tests:** Tests for the producer service and consumer listener.

Let's get started with the code!

---

#### 1. `pom.xml` (Maven Dependencies)

This file defines all the necessary dependencies for our Spring Boot Kafka application.

`$$$$ `
`&&&& pom.xml`
`@@@@
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.5</version> <!-- Use a recent stable Spring Boot version -->
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>my-kafka-app</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>my-kafka-app</name>
	<description>Spring Boot Kafka Template</description>
	<properties>
		<java.version>17</java.version> <!-- Ensure this matches your JDK version -->
	</properties>
	<dependencies>
		<!-- Spring Boot Web Starter for REST endpoints -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- Spring Kafka Starter for Kafka integration -->
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<!-- Lombok for boilerplate code (getters, setters, constructors) -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<!-- Spring Boot Test Starter for testing -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Spring Kafka Test for testing Kafka components -->
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
@@@@
**Explanation:**
*   **`spring-boot-starter-web`**: Provides necessary dependencies for building RESTful web applications.
*   **`spring-kafka`**: The core Spring Kafka integration library.
*   **`lombok`**: A utility to reduce boilerplate code (getters, setters, constructors).
*   **`spring-boot-starter-test`**: Standard testing dependencies for Spring Boot applications.
*   **`spring-kafka-test`**: Specific utilities for testing Kafka applications, including `EmbeddedKafkaBroker`.

---

#### 2. `application.properties` (Configuration)

This file holds the configuration properties for our application, including Kafka broker details and topic names.

`$$$$ src/main/resources`
`&&&& application.properties`
`@@@@
# Kafka Broker Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.properties.schema.registry.url=http://localhost:8081 # If using Confluent Schema Registry

# Producer Configuration
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.acks=all
spring.kafka.producer.retries=0
spring.kafka.producer.batch-size=16384
spring.kafka.producer.linger-ms=1
spring.kafka.producer.buffer-memory=33554432

# Consumer Configuration
spring.kafka.consumer.group-id=my_spring_boot_app_group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=* # Important for JSON deserialization

# Define Kafka Topic
app.kafka.topic.name=my-app-topic
@@@@
**Explanation:**
*   **`spring.kafka.bootstrap-servers`**: Address of your Kafka broker. Change `localhost:9092` if your broker is elsewhere.
*   **`spring.kafka.producer.*`**: Properties for the Kafka producer, including serializers. We use `JsonSerializer` to send custom objects.
*   **`spring.kafka.consumer.*`**: Properties for the Kafka consumer, including `group-id` and deserializers. `JsonDeserializer` is used to convert JSON back into our `Message` object.
*   **`spring.kafka.consumer.properties.spring.json.trusted.packages=*`**: This is crucial when using `JsonDeserializer` to tell Spring which packages are safe to deserialize JSON objects into. Using `*` trusts all, but in a production environment, you might want to specify exact packages (e.g., `com.example.kafka.model`).
*   **`app.kafka.topic.name`**: A custom property to define our Kafka topic name, making it easy to change.

---

#### 3. `MyKafkaAppApplication.java` (Main Application Class)

The standard Spring Boot entry point.

`$$$$ src/main/java/com/example/kafka`
`&&&& MyKafkaAppApplication.java`
`@@@@
package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyKafkaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyKafkaAppApplication.class, args);
	}

}
@@@@
**Explanation:**
*   `@SpringBootApplication`: This annotation is a convenience annotation that adds:
    *   `@Configuration`: Tags the class as a source of bean definitions for the application context.
    *   `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
    *   `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com.example.kafka` package, allowing it to find our controllers, services, and listeners.

---

#### 4. `model/Message.java` (Data Model)

A simple POJO representing the message structure we'll send and receive.

`$$$$ src/main/java/com/example/kafka/model`
`&&&& Message.java`
`@@@@
package com.example.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class Message {
    private String content;
    private LocalDateTime timestamp;
}
@@@@
**Explanation:**
*   **`@Data`**: A Lombok annotation that automatically generates standard methods like `getters`, `setters`, `equals`, `hashCode`, and `toString`.
*   **`@NoArgsConstructor`**: Lombok annotation for a constructor without any arguments. Essential for JSON deserialization.
*   **`@AllArgsConstructor`**: Lombok annotation for a constructor with all fields as arguments. Useful for creating instances easily.
*   **`LocalDateTime`**: Used for a precise timestamp.

---

#### 5. `config/KafkaProducerConfig.java` (Producer Configuration)

Configures the `ProducerFactory` and `KafkaTemplate` for sending messages.

`$$$$ src/main/java/com/example/kafka/config`
`&&&& KafkaProducerConfig.java`
`@@@@
package com.example.kafka.config;

import com.example.kafka.model.Message;
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

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Additional producer properties can be added here
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
@@@@
**Explanation:**
*   **`@Configuration`**: Indicates that this class provides bean definitions.
*   **`@Value("${spring.kafka.bootstrap-servers}")`**: Injects the Kafka broker address from `application.properties`.
*   **`producerFactory()`**:
    *   Creates a `Map` of producer configurations, including `BOOTSTRAP_SERVERS_CONFIG` and the `Serializer` classes for both key and value.
    *   Uses `StringSerializer` for keys and `JsonSerializer` for `Message` objects.
    *   Returns a `DefaultKafkaProducerFactory` which is responsible for creating producer instances.
*   **`kafkaTemplate()`**:
    *   Creates and returns a `KafkaTemplate` instance, which is the high-level API for sending messages to Kafka. It uses the `ProducerFactory` we defined.

---

#### 6. `config/KafkaConsumerConfig.java` (Consumer Configuration)

Configures the `ConsumerFactory` and `ConcurrentKafkaListenerContainerFactory` for receiving messages.

`$$$$ src/main/java/com/example/kafka/config`
`&&&& KafkaConsumerConfig.java`
`@@@@
package com.example.kafka.config;

import com.example.kafka.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Message> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Using ErrorHandlingDeserializer to wrap JsonDeserializer for better error handling
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Crucial for JSON deserialization

        // Optionally, specify the target type for the JsonDeserializer if not using trusted packages wildcard
        // props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.kafka.model.Message");

        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Message.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Set to true to automatically commit offsets after each message is processed
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
@@@@
**Explanation:**
*   **`@Configuration`**: Marks this as a configuration class.
*   **`@Value`**: Injects Kafka broker and group ID from properties.
*   **`consumerFactory()`**:
    *   Sets up consumer properties including `BOOTSTRAP_SERVERS_CONFIG`, `GROUP_ID_CONFIG`, and deserializers.
    *   Uses `StringDeserializer` for keys and `ErrorHandlingDeserializer` wrapping `JsonDeserializer` for values. `ErrorHandlingDeserializer` ensures that if a message cannot be deserialized, it won't crash the consumer, but rather log the error and allow the consumer to process subsequent messages.
    *   `JsonDeserializer.TRUSTED_PACKAGES`: Tells `JsonDeserializer` which classes are safe to instantiate from JSON. `*` is convenient for development.
*   **`kafkaListenerContainerFactory()`**:
    *   This factory creates the listener containers that host the `@KafkaListener` methods.
    *   It uses the `consumerFactory()` we defined.
    *   **`setAckMode(ContainerProperties.AckMode.RECORD)`**: Configures the acknowledgment mode. `RECORD` means the offset is committed after each record is successfully processed. Other options include `BATCH`, `TIME`, etc.

---

#### 7. `service/KafkaProducerService.java` (Producer Service)

A service that uses `KafkaTemplate` to send `Message` objects.

`$$$$ src/main/java/com/example/kafka/service`
`&&&& KafkaProducerService.java`
`@@@@
package com.example.kafka.service;

import com.example.kafka.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j // Lombok annotation for a logger
public class KafkaProducerService {

    @Value("${app.kafka.topic.name}")
    private String topicName;

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String messageContent) {
        Message message = new Message(messageContent, LocalDateTime.now());

        CompletableFuture<SendResult<String, Message>> future = kafkaTemplate.send(topicName, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}] to topic=[{}]",
                        messageContent,
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().topic());
            } else {
                log.error("Unable to send message=[{}] due to: {}", messageContent, ex.getMessage());
            }
        });
    }
}
@@@@
**Explanation:**
*   **`@Service`**: Marks this class as a Spring service component.
*   **`@Slf4j`**: Lombok annotation that provides a `log` field for logging.
*   **`@Value("${app.kafka.topic.name}")`**: Injects the topic name from `application.properties`.
*   **`KafkaTemplate<String, Message> kafkaTemplate`**: Injects the configured `KafkaTemplate`.
*   **`sendMessage(String key, String messageContent)`**:
    *   Creates a `Message` object with the given content and current timestamp.
    *   Calls `kafkaTemplate.send()` to send the message. We're using a `key` here for partitioning (messages with the same key go to the same partition).
    *   The `send()` method returns a `ListenableFuture` (or `CompletableFuture` in newer Spring versions) for asynchronous processing. We use `whenComplete` to log the result or any error.

---

#### 8. `controller/KafkaController.java` (REST Controller)

An HTTP endpoint to trigger message sending.

`$$$$ src/main/java/com/example/kafka/controller`
`&&&& KafkaController.java`
`@@@@
package com.example.kafka.controller;

import com.example.kafka.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService producerService;

    public KafkaController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendMessageToKafka(
            @RequestParam("message") String message,
            @RequestParam(value = "key", required = false) String key) {
        
        // If no key is provided, Kafka will assign one or use a round-robin strategy.
        // For simplicity, we can default to a generic key if not provided for now,
        // or let the producer service handle null key (which KafkaTemplate supports).
        // Let's ensure a key for demonstration, otherwise null is fine.
        if (key == null || key.isEmpty()) {
            key = "default-key"; // Or generate a UUID, or let Kafka handle it
        }

        producerService.sendMessage(key, message);
        return ResponseEntity.ok("Message sent to Kafka successfully: " + message);
    }
}
@@@@
**Explanation:**
*   **`@RestController`**: Combines `@Controller` and `@ResponseBody`, indicating that this class handles web requests and returns data directly.
*   **`@RequestMapping("/kafka")`**: Maps all requests to this controller under the `/kafka` path.
*   **`@PostMapping("/publish")`**: Maps HTTP POST requests to `/kafka/publish` to this method.
*   **`@RequestParam`**: Extracts `message` and `key` parameters from the request. `key` is optional.
*   It injects `KafkaProducerService` and calls its `sendMessage` method to send the user-provided message.

---

#### 9. `listener/KafkaConsumerListener.java` (Consumer Listener)

A component that listens for messages on the configured Kafka topic.

`$$$$ src/main/java/com/example/kafka/listener`
`&&&& KafkaConsumerListener.java`
`@@@@
package com.example.kafka.listener;

import com.example.kafka.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerListener {

    @KafkaListener(topics = "${app.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Message message, Acknowledgment acknowledgment) {
        log.info("Received message: {} at {}", message.getContent(), message.getTimestamp());
        // Process the message here
        // For demonstration, we just log it.

        // Manually acknowledge the message if AckMode is not RECORD or BATCH
        // For AckMode.RECORD, Spring Kafka automatically commits after method returns
        acknowledgment.acknowledge();
    }
}
@@@@
**Explanation:**
*   **`@Component`**: Marks this class as a Spring component, so it's picked up by component scanning.
*   **`@Slf4j`**: Provides a logger.
*   **`@KafkaListener`**: The core annotation for Kafka consumers.
    *   **`topics`**: Specifies the topic(s) to listen to, pulled from `application.properties`.
    *   **`groupId`**: Specifies the consumer group ID, also from `application.properties`.
    *   **`containerFactory`**: Points to the `ConcurrentKafkaListenerContainerFactory` bean that defines the common configuration for the listener container (e.g., deserializers, error handling).
*   **`listen(Message message, Acknowledgment acknowledgment)`**:
    *   This method will be invoked whenever a new `Message` is available on the configured topic. Spring Kafka automatically deserializes the JSON payload into a `Message` object.
    *   **`Acknowledgment acknowledgment`**: Provided by Spring Kafka, allows manual acknowledgment of messages. While `AckMode.RECORD` (set in `KafkaConsumerConfig`) handles automatic acknowledgment upon successful method completion, explicitly calling `acknowledgment.acknowledge()` can be useful for finer control, especially in more complex scenarios or different `AckMode` settings. It's often included for clarity or future flexibility.

---

#### 10. `test/service/KafkaProducerServiceTest.java` (Producer Service Unit Test)

Unit test for `KafkaProducerService`, mocking `KafkaTemplate`.

`$$$$ src/test/java/com/example/kafka/service`
`&&&& KafkaProducerServiceTest.java`
`@@@@
package com.example.kafka.service;

import com.example.kafka.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture; // For mockito.when() return type
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private final String TOPIC = "test-topic";

    @BeforeEach
    void setUp() {
        // Set the topic name via reflection or a setter if it's private and no constructor injection
        // For simplicity, if @Value is used, we can directly set it here for test
        // In a real scenario, you might have a test config for @Value or use constructor injection
        try {
            java.lang.reflect.Field topicNameField = KafkaProducerService.class.getDeclaredField("topicName");
            topicNameField.setAccessible(true);
            topicNameField.set(kafkaProducerService, TOPIC);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Mock the return of kafkaTemplate.send() to immediately complete successfully
        SettableListenableFuture<SendResult<String, Message>> future = new SettableListenableFuture<>();
        future.set(mock(SendResult.class)); // Just return a mocked SendResult
        when(kafkaTemplate.send(anyString(), anyString(), any(Message.class))).thenReturn(future);
    }

    @Test
    void testSendMessage() {
        String key = "testKey";
        String content = "Test message content";

        kafkaProducerService.sendMessage(key, content);

        // Verify that kafkaTemplate.send was called exactly once
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq(key), messageCaptor.capture());

        // Assert on the captured message object
        Message sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(content, sentMessage.getContent());
        assertNotNull(sentMessage.getTimestamp());
        
        // Also verify the future completion was handled (though the actual completion logic is in the service)
        // For unit tests, we're primarily checking method invocation and arguments.
    }
}
@@@@
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Enables Mockito annotations for JUnit 5.
*   **`@Mock private KafkaTemplate<String, Message> kafkaTemplate`**: Creates a mock instance of `KafkaTemplate`.
*   **`@InjectMocks private KafkaProducerService kafkaProducerService`**: Injects the mocked `KafkaTemplate` into `KafkaProducerService`.
*   **`@Captor private ArgumentCaptor<Message> messageCaptor`**: Used to capture the `Message` argument passed to `kafkaTemplate.send()` for verification.
*   **`setUp()`**: Initializes the `topicName` field in the service (since it's typically injected via `@Value`) and mocks the `kafkaTemplate.send()` method to return an immediately completed `ListenableFuture`. This prevents actual Kafka communication during the unit test.
*   **`testSendMessage()`**:
    *   Calls `kafkaProducerService.sendMessage()`.
    *   **`verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq(key), messageCaptor.capture())`**: Verifies that the `send` method of `kafkaTemplate` was called exactly once with the correct topic, key, and captures the `Message` object.
    *   Assertions check the content and timestamp of the captured `Message` object.

---

#### 11. `test/listener/KafkaConsumerListenerTest.java` (Consumer Listener Unit Test)

Unit test for `KafkaConsumerListener`, directly invoking the listener method.

`$$$$ src/test/java/com/example/kafka/listener`
`&&&& KafkaConsumerListenerTest.java`
`@@@@
package com.example.kafka.listener;

import com.example.kafka.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class KafkaConsumerListenerTest {

    private KafkaConsumerListener kafkaConsumerListener;

    @Captor
    private ArgumentCaptor<String> logMessageCaptor; // To capture log messages for verification

    private Acknowledgment acknowledgment; // Mock Acknowledgment

    @BeforeEach
    void setUp() {
        // We initialize the listener directly as it has no dependencies for simple unit test
        kafkaConsumerListener = new KafkaConsumerListener();
        acknowledgment = Mockito.mock(Acknowledgment.class); // Mock Acknowledgment

        // Mock static Slf4j logger for capturing log output if needed, though for this simple test it's not strictly necessary.
        // For more advanced logger mocking, consider libraries like LogCaptor or AOP.
    }

    @Test
    void testListenWithMessage() {
        String testContent = "Hello from Kafka test!";
        LocalDateTime testTimestamp = LocalDateTime.now().minusMinutes(5); // Example timestamp
        Message testMessage = new Message(testContent, testTimestamp);

        // Call the listener method directly
        kafkaConsumerListener.listen(testMessage, acknowledgment);

        // Verify that acknowledge() was called
        verify(acknowledgment).acknowledge();

        // For a more robust test, you might want to capture and verify log messages
        // This usually requires a separate logging test setup or mocking the logger directly.
        // For this simple case, we trust the log.info() call would happen.
    }

    @Test
    void testListenWithDifferentMessageContent() {
        String differentContent = "Another message!";
        LocalDateTime differentTimestamp = LocalDateTime.now();
        Message differentMessage = new Message(differentContent, differentTimestamp);

        kafkaConsumerListener.listen(differentMessage, acknowledgment);

        verify(acknowledgment).acknowledge();
    }
}
@@@@
**Explanation:**
*   This is a pure unit test for `KafkaConsumerListener`. It does not require an embedded Kafka broker.
*   We directly instantiate `KafkaConsumerListener` because it has no direct dependencies injected via its constructor that we need to mock for its `listen` method.
*   **`Acknowledgment acknowledgment`**: A mock `Acknowledgment` object is created and passed to the `listen` method.
*   **`testListenWithMessage()`**:
    *   A `Message` object is created.
    *   The `kafkaConsumerListener.listen()` method is called directly with the test message and the mocked `Acknowledgment`.
    *   **`verify(acknowledgment).acknowledge()`**: We verify that the `acknowledge()` method on the mocked `Acknowledgment` was called. This confirms that our listener correctly processed the message and attempted to acknowledge it.

**Note on Consumer Unit Testing:**
For a comprehensive *integration test* of the consumer, you would typically use Spring Boot's `@EmbeddedKafka` to spin up an in-memory Kafka broker, then send messages to it via a `KafkaTemplate`, and finally verify that the `@KafkaListener` correctly receives and processes those messages. The above unit test focuses on the listener method's internal logic, independent of the Kafka infrastructure.

---

### How to Run This Application

1.  **Ensure Kafka is Running:** Make sure your Kafka broker and Zookeeper are running (e.g., using Docker or a local installation). The `bootstrap-servers` in `application.properties` should point to your Kafka instance (default is `localhost:9092`).
2.  **Compile and Run:**
    *   Open a terminal in your project's root directory.
    *   Run `mvn clean install` to build the project.
    *   Run `mvn spring-boot:run` to start the Spring Boot application.
3.  **Send a Message (Producer):**
    *   Once the application starts, open your browser or use `curl` or Postman to send a POST request:
        ```bash
        curl -X POST "http://localhost:8080/kafka/publish?message=Hello%20Kafka%20from%20Spring%20Boot!&key=myKey"
        ```
    *   You should see a `Message sent to Kafka successfully` response.
4.  **Observe Consumer Output:**
    *   Look at the console where your Spring Boot application is running. You should see a log message from the `KafkaConsumerListener` indicating that it received the message:
        ```
        Received message: Hello Kafka from Spring Boot! at 2024-05-15T10:30:00.123
        ```
5.  **Run Unit Tests:**
    *   From your project's root directory: `mvn test`

This template provides a solid foundation for building Kafka-enabled microservices with Spring Boot! Feel free to customize the `Message` model, add more complex business logic, or integrate with other Spring features.