Hello there! I'd be glad to help you with a complete Spring Boot Kafka code template, including producer, consumer, a simple data model, configurations, REST endpoint, and unit tests.

This template will cover:
*   **Data Model:** A simple POJO to represent the message payload.
*   **Kafka Producer:**
    *   Configuration for `ProducerFactory` and `KafkaTemplate`.
    *   A service to send messages to a Kafka topic.
    *   A REST controller to trigger message sending.
*   **Kafka Consumer:**
    *   Configuration for `ConsumerFactory` and `ConcurrentKafkaListenerContainerFactory`.
    *   A service with a `@KafkaListener` to consume messages from a Kafka topic.
*   **Application Properties:** Essential Kafka configurations.
*   **Unit Tests:** Dedicated tests for both producer and consumer services.

Let's get started!

****
&&&& pom.xml
@@@@
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Use the latest stable Spring Boot version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>springboot-kafka-template</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springboot-kafka-template</name>
    <description>Spring Boot Kafka Template Project</description>

    <properties>
        <java.version>17</java.version> <!-- Ensure this matches your JDK version -->
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter for REST endpoints -->
        <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Kafka for Kafka integration -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- Lombok for boilerplate code reduction (optional but highly recommended) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Spring Kafka Test for unit and integration testing Kafka components -->
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
*   **`spring-boot-starter-web`**: Provides necessary dependencies for building RESTful applications.
*   **`spring-kafka`**: The core library for integrating Spring applications with Apache Kafka.
*   **`lombok`**: A handy library to reduce boilerplate code (e.g., getters, setters, constructors).
*   **`spring-boot-starter-test`**: Includes JUnit, Mockito, Spring Test, etc., for testing Spring Boot applications.
*   **`spring-kafka-test`**: Provides utilities like `EmbeddedKafkaBroker` for testing Kafka-related components.

****
&&&& application.properties
@@@@
# Kafka Bootstrap Servers
# Point this to your Kafka broker(s). For local development, 'localhost:9092' is common.
spring.kafka.bootstrap-servers=localhost:9092

# Producer Configuration
# Key serializer: Converts the key object to bytes for Kafka. StringSerializer is suitable for string keys.
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
# Value serializer: Converts the value object (our MyMessage) to bytes. JsonSerializer is often used for POJOs.
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
# Acknowledgment level for the producer. 'all' ensures high data durability.
spring.kafka.producer.acks=all

# Consumer Configuration
# Key deserializer: Converts bytes from Kafka back to the key object.
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
# Value deserializer: Converts bytes from Kafka back to our MyMessage object.
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
# Group ID for the consumer. All consumers with the same group ID belong to the same consumer group.
spring.kafka.consumer.group-id=my-spring-kafka-group
# What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server.
# 'earliest': Automatically reset the offset to the earliest offset.
# 'latest': Automatically reset the offset to the latest offset.
spring.kafka.consumer.auto-offset-reset=earliest
# Enable auto commit of offsets.
spring.kafka.consumer.enable-auto-commit=true
# Auto commit interval (if enable-auto-commit is true).
spring.kafka.consumer.auto-commit-interval=1000ms

# Application Specific Kafka Topic Configuration
# Define the name of the Kafka topic our application will use.
app.kafka.topic.name=my-spring-boot-topic
@@@@
**Explanation:**
*   **`spring.kafka.bootstrap-servers`**: The address of your Kafka broker. Change this if your Kafka is running elsewhere.
*   **`spring.kafka.producer.*`**: Properties for the Kafka producer. We use `StringSerializer` for keys and `JsonSerializer` for values, which will convert our Java objects to JSON strings before sending to Kafka.
*   **`spring.kafka.consumer.*`**: Properties for the Kafka consumer. We use `StringDeserializer` for keys and `JsonDeserializer` for values to convert JSON strings from Kafka back to Java objects.
*   **`spring.kafka.consumer.group-id`**: Essential for consumer groups. All consumers with the same `group-id` are part of the same group and share partitions.
*   **`app.kafka.topic.name`**: A custom property to define our topic name, making it easily configurable.

****
$$$$ src/main/java/com/example/springbootkafkatemplate
&&&& SpringbootKafkaTemplateApplication.java
@@@@
package com.example.springbootkafkatemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootKafkaTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootKafkaTemplateApplication.class, args);
    }

}
@@@@
**Explanation:**
*   This is the standard entry point for any Spring Boot application. `@SpringBootApplication` combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/model
&&&& MyMessage.java
@@@@
package com.example.springbootkafkatemplate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields as arguments
public class MyMessage {
    private String id;
    private String message;
    private LocalDateTime timestamp;
}
@@@@
**Explanation:**
*   This is our simple data transfer object (DTO).
*   **`@Data` (Lombok)**: Automatically generates boilerplate methods like getters, setters, `equals()`, `hashCode()`, and `toString()`.
*   **`@NoArgsConstructor` (Lombok)**: Creates a constructor with no arguments, necessary for JSON deserialization.
*   **`@AllArgsConstructor` (Lombok)**: Creates a constructor with all fields as arguments, useful for creating instances.
*   **`LocalDateTime`**: Used to include a timestamp, good practice for messages.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/config
&&&& KafkaProducerConfig.java
@@@@
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
@@@@
**Explanation:**
*   **`@Configuration`**: Marks this class as a source of bean definitions.
*   **`@Value("${spring.kafka.bootstrap-servers}")`**: Injects the Kafka broker address from `application.properties`.
*   **`producerFactory()`**: Defines a `ProducerFactory` bean. This factory creates `org.apache.kafka.clients.producer.Producer` instances. It's configured with:
    *   `BOOTSTRAP_SERVERS_CONFIG`: Kafka broker address.
    *   `KEY_SERIALIZER_CLASS_CONFIG`: Specifies how message keys (`String` in our case) are serialized.
    *   `VALUE_SERIALIZER_CLASS_CONFIG`: Specifies how message values (`MyMessage` objects) are serialized. We use `JsonSerializer` which converts the object to a JSON string.
*   **`kafkaTemplate()`**: Defines a `KafkaTemplate` bean. This is the main class used by Spring applications to send messages to Kafka. It wraps the `ProducerFactory` and simplifies the sending process.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/producer
&&&& KafkaProducerService.java
@@@@
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
@@@@
**Explanation:**
*   **`@Service`**: Marks this class as a Spring service component.
*   **`@Value("${app.kafka.topic.name}")`**: Injects the topic name from `application.properties`.
*   **`KafkaTemplate<String, MyMessage> kafkaTemplate`**: The injected KafkaTemplate for sending messages. The generics indicate the key type (`String`) and value type (`MyMessage`).
*   **`sendMessage(String messageContent)`**:
    *   Creates a `MyMessage` instance with a unique ID and current timestamp.
    *   `kafkaTemplate.send(topicName, myMessage.getId(), myMessage)`: Sends the message. The first argument is the topic, the second is the message key (which helps with partition ordering), and the third is the message payload.
    *   The `send` method is asynchronous and returns a `CompletableFuture<SendResult>`.
    *   `whenComplete` is used to log the outcome of the send operation (success or failure) once it's complete. This is good practice for asynchronous operations.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/controller
&&&& MessageController.java
@@@@
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
@@@@
**Explanation:**
*   **`@RestController`**: Combines `@Controller` and `@ResponseBody`, indicating that this class handles REST requests and directly returns data.
*   **`@RequestMapping("/api/kafka")`**: Sets the base path for all endpoints in this controller.
*   **`@PostMapping("/send")`**: Maps HTTP POST requests to `/api/kafka/send` to the `sendMessage` method.
*   **`@RequestBody String messageRequest`**: The payload of the POST request will be bound to the `messageRequest` string.
*   The method delegates to `kafkaProducerService.sendMessage()` and then uses `thenApply` and `exceptionally` to handle the asynchronous result, returning an appropriate `ResponseEntity`.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/config
&&&& KafkaConsumerConfig.java
@@@@
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
@@@@
**Explanation:**
*   **`@EnableKafka`**: This annotation is crucial; it enables Spring's Kafka listener infrastructure, allowing the use of `@KafkaListener` annotations.
*   **`@Value`**: Injects `bootstrapServers` and `groupId` from `application.properties`.
*   **`consumerFactory()`**: Defines a `ConsumerFactory` bean.
    *   It's configured with `BOOTSTRAP_SERVERS_CONFIG`, `GROUP_ID_CONFIG`.
    *   `KEY_DESERIALIZER_CLASS_CONFIG` and `VALUE_DESERIALIZER_CLASS_CONFIG` specify how to convert incoming bytes back into `String` and `MyMessage` objects, respectively.
    *   **`JsonDeserializer.TRUSTED_PACKAGES`**: Very important! When using `JsonDeserializer`, you must explicitly tell it which packages contain the classes it's allowed to deserialize. Without this, you'll get `java.lang.IllegalArgumentException: The class 'com.example.springbootkafkatemplate.model.MyMessage' is not in the trusted packages: [java.util, java.lang]`.
*   **`kafkaListenerContainerFactory()`**: Defines a `ConcurrentKafkaListenerContainerFactory` bean.
    *   This factory is used by `@KafkaListener` annotations to create `ConcurrentMessageListenerContainer` instances, which manage the consumer threads and message processing.
    *   It links to the `ConsumerFactory` and can be used to set concurrency levels.

****
$$$$ src/main/java/com/example/springbootkafkatemplate/consumer
&&&& KafkaConsumerService.java
@@@@
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
@@@@
**Explanation:**
*   **`@Service`**: Marks this class as a Spring service.
*   **`@KafkaListener`**: The core annotation for consuming Kafka messages.
    *   `topics = "${app.kafka.topic.name}"`: Specifies the topic(s) to listen to. The value is pulled from `application.properties`.
    *   `groupId = "${spring.kafka.consumer.group-id}"`: Specifies the consumer group ID.
    *   `containerFactory = "kafkaListenerContainerFactory"`: Points to the `ConcurrentKafkaListenerContainerFactory` bean defined in `KafkaConsumerConfig`.
*   **`@Payload MyMessage message`**: The deserialized `MyMessage` object is injected directly into the method.
*   **`@Header(KafkaHeaders.RECEIVED_KEY) String key`**, etc.: Allows injecting specific Kafka record headers (like key, partition, offset) into the method parameters.
*   The `listen` method simply logs the received message. In a real application, this is where your business logic for processing messages would reside.

****
$$$$ src/test/java/com/example/springbootkafkatemplate/producer
&&&& KafkaProducerServiceTest.java
@@@@
package com.example.springbootkafkatemplate.producer;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, MyMessage> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    // Use reflection to set the topicName value
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Since @Value is not processed in unit tests, we manually set it
        java.lang.reflect.Field field = KafkaProducerService.class.getDeclaredField("topicName");
        field.setAccessible(true);
        field.set(kafkaProducerService, "test-topic");
    }

    @Test
    void testSendMessageSuccess() {
        String messageContent = "Test Message Content";

        // Mock the ListenableFuture and CompletableFuture returned by kafkaTemplate.send
        ListenableFuture<SendResult<String, MyMessage>> listenableFuture = mock(ListenableFuture.class);
        CompletableFuture<SendResult<String, MyMessage>> completableFuture = new CompletableFuture<>();
        when(listenableFuture.completable()).thenReturn(completableFuture);

        // When kafkaTemplate.send is called, return our mocked listenableFuture
        when(kafkaTemplate.send(eq("test-topic"), any(String.class), any(MyMessage.class)))
                .thenReturn(listenableFuture);

        // Simulate a successful send by completing the future
        completableFuture.complete(mock(SendResult.class)); // We don't need a real SendResult for this test

        // Call the service method
        CompletableFuture<SendResult<String, MyMessage>> resultFuture = kafkaProducerService.sendMessage(messageContent);

        // Verify that kafkaTemplate.send was called
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MyMessage> messageCaptor = ArgumentCaptor.forClass(MyMessage.class);
        verify(kafkaTemplate, times(1)).send(eq("test-topic"), keyCaptor.capture(), messageCaptor.capture());

        // Assertions for the captured arguments
        assertNotNull(keyCaptor.getValue());
        assertNotNull(messageCaptor.getValue());
        assertTrue(messageCaptor.getValue().getMessage().contains(messageContent));

        // Verify the returned future completes successfully
        assertTrue(resultFuture.isDone());
        assertNotNull(resultFuture.join()); // join() blocks and returns the result
    }

    @Test
    void testSendMessageFailure() {
        String messageContent = "Test Message Content";
        RuntimeException ex = new RuntimeException("Kafka send failed");

        ListenableFuture<SendResult<String, MyMessage>> listenableFuture = mock(ListenableFuture.class);
        CompletableFuture<SendResult<String, MyMessage>> completableFuture = new CompletableFuture<>();
        when(listenableFuture.completable()).thenReturn(completableFuture);

        when(kafkaTemplate.send(eq("test-topic"), any(String.class), any(MyMessage.class)))
                .thenReturn(listenableFuture);

        // Simulate a failed send by completing the future exceptionally
        completableFuture.completeExceptionally(ex);

        // Call the service method
        CompletableFuture<SendResult<String, MyMessage>> resultFuture = kafkaProducerService.sendMessage(messageContent);

        // Verify that kafkaTemplate.send was called
        verify(kafkaTemplate, times(1)).send(eq("test-topic"), any(String.class), any(MyMessage.class));

        // Verify the returned future completes exceptionally
        assertTrue(resultFuture.isCompletedExceptionally());
        try {
            resultFuture.join(); // This will throw CompletionException
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Kafka send failed"));
        }
    }
}
@@@@
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@Mock KafkaTemplate<String, MyMessage> kafkaTemplate`**: Creates a mock instance of `KafkaTemplate`. We don't want to actually send messages to Kafka during a unit test.
*   **`@InjectMocks KafkaProducerService kafkaProducerService`**: Injects the mocked `kafkaTemplate` into `kafkaProducerService`.
*   **`@BeforeEach`**:
    *   Since `@Value` annotations are not processed in standard JUnit tests (which don't run a full Spring context), we manually set the `topicName` field using reflection. This ensures the `kafkaProducerService` has the topic name configured for testing.
*   **`testSendMessageSuccess()`**:
    *   Mocks the `ListenableFuture` and `CompletableFuture` returned by `kafkaTemplate.send()`.
    *   Uses `when().thenReturn()` to specify the behavior of `kafkaTemplate.send()`.
    *   `completableFuture.complete(mock(SendResult.class))` simulates a successful message send.
    *   `verify()` is used to assert that `kafkaTemplate.send()` was called with the expected arguments.
    *   `ArgumentCaptor` helps capture the arguments passed to the mocked method for detailed assertions.
    *   Asserts that the returned `CompletableFuture` is done and contains a result.
*   **`testSendMessageFailure()`**:
    *   Similar setup, but `completableFuture.completeExceptionally(ex)` is used to simulate a failed message send.
    *   Asserts that the returned `CompletableFuture` completes exceptionally and captures the exception.

****
$$$$ src/test/java/com/example/springbootkafkatemplate/consumer
&&&& KafkaConsumerServiceTest.java
@@@@
package com.example.springbootkafkatemplate.consumer;

import com.example.springbootkafkatemplate.model.MyMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    // InjectMocks attempts to inject mocks into this instance
    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    // We can spy on the logger to verify logging calls
    @Mock
    private Logger logger;

    // Use reflection to set the groupId value, as @Value is not processed in unit tests
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Inject the mocked logger into the service
        java.lang.reflect.Field loggerField = KafkaConsumerService.class.getDeclaredField("LOGGER");
        loggerField.setAccessible(true);
        loggerField.set(null, logger); // Static field, so null for object

        // Manually set the groupId field
        java.lang.reflect.Field groupIdField = KafkaConsumerService.class.getDeclaredField("groupId");
        groupIdField.setAccessible(true);
        groupIdField.set(kafkaConsumerService, "test-group");
    }

    @Test
    void testListen() {
        // Prepare test data
        MyMessage testMessage = new MyMessage("id-123", "Hello from Kafka!", LocalDateTime.now());
        String testKey = "id-123";
        int testPartition = 0;
        long testOffset = 12345L;

        // Call the listener method directly
        kafkaConsumerService.listen(testMessage, testKey, testPartition, testOffset);

        // Verify that the logger's info method was called
        // We capture the arguments to assert the log content
        ArgumentCaptor<String> formatCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> arg1Captor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> arg2Captor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> arg3Captor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> arg4Captor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> arg5Captor = ArgumentCaptor.forClass(Object.class);


        verify(logger).info(formatCaptor.capture(),
                arg1Captor.capture(), arg2Captor.capture(), arg3Captor.capture(), arg4Captor.capture(), arg5Captor.capture());

        // Assert on the captured arguments to verify the log message content
        assertEquals("test-group", arg1Captor.getValue());
        assertEquals(testKey, arg2Captor.getValue());
        assertEquals(testPartition, arg3Captor.getValue());
        assertEquals(testOffset, arg4Captor.getValue());
        assertEquals(testMessage, arg5Captor.getValue());
    }
}
@@@@
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Enables Mockito.
*   **`@InjectMocks KafkaConsumerService kafkaConsumerService`**: Creates an instance of the service under test.
*   **`@Mock Logger logger`**: We mock the `LOGGER` to verify that messages are logged correctly without actually writing to the console or file during the test.
*   **`@BeforeEach`**:
    *   Similar to the producer test, we manually set the `groupId` using reflection.
    *   We also use reflection to set the static `LOGGER` field in `KafkaConsumerService` to our mocked `logger` instance.
*   **`testListen()`**:
    *   A `MyMessage` is created.
    *   The `listen` method of `kafkaConsumerService` is *directly invoked*. This simulates Kafka delivering a message to the listener.
    *   `verify(logger).info(...)` is used to check that the `info` method of the mocked `logger` was called.
    *   `ArgumentCaptor` is used to capture all arguments passed to `logger.info()`. This allows us to assert the exact content of the log message, including the message object, key, partition, and offset.

This completes the Spring Boot Kafka template with both producer and consumer, including all necessary configuration, a data model, a REST endpoint for the producer, and comprehensive unit tests for both services.

To run this:
1.  Ensure you have a Kafka broker running (e.g., via Docker or a local installation).
    *   A quick way to start Kafka with Docker:
        ```bash
        docker compose -f docker-compose-kafka.yml up -d
        ```
        You'd need a `docker-compose-kafka.yml` like this:
        ```yaml
        version: '3'
        services:
          zookeeper:
            image: confluentinc/cp-zookeeper:7.5.3
            hostname: zookeeper
            container_name: zookeeper
            ports:
              - "2181:2181"
            environment:
              ZOOKEEPER_CLIENT_PORT: 2181
              ZOOKEEPER_TICK_TIME: 2000

          kafka:
            image: confluentinc/cp-kafka:7.5.3
            hostname: kafka
            container_name: kafka
            depends_on:
              - zookeeper
            ports:
              - "9092:9092"
              - "9093:9093"
            environment:
              KAFKA_BROKER_ID: 1
              KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
              KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
              KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
              KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
              KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
              KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
              KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
        ```
2.  Build the project using Maven (`mvn clean install`).
3.  Run the Spring Boot application (`java -jar target/springboot-kafka-template-0.0.1-SNAPSHOT.jar` or directly from your IDE).
4.  Send a POST request to `http://localhost:8080/api/kafka/send` with a plain string body (e.g., "Hello Kafka from Postman!").
    *   Example using `curl`:
        ```bash
        curl -X POST -H "Content-Type: text/plain" -d "This is a test message from curl!" http://localhost:8080/api/kafka/send
        ```
5.  You should see the producer logs confirming the message sent, and the consumer logs confirming the message received.