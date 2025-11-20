Hello there! I'd be delighted to provide you with a comprehensive Spring Boot Kafka code template. This template will cover both producer and consumer functionalities, including necessary configurations, a simple message payload, and unit tests to ensure robustness.

We'll set up a project with the following structure:

*   **`payload`**: Defines the data structure for messages sent over Kafka.
*   **`config`**: Contains Kafka-specific configuration, like topic creation.
*   **`producer`**: Implements logic for sending messages to Kafka, including a REST endpoint to trigger sending.
*   **`consumer`**: Implements logic for receiving and processing messages from Kafka.
*   **`main application class`**: The entry point for the Spring Boot application.
*   **`application.properties`**: Configuration file for environment-specific settings.
*   **`Unit Tests`**: Dedicated test classes for producer and consumer services.

Let's get started!

---

#### 1. Project Setup (pom.xml)

First, you'll need a `pom.xml` (if you're using Maven) with the necessary dependencies. You would typically generate a basic Spring Boot project using Spring Initializr (https://start.spring.io/) and add the `Spring for Apache Kafka` dependency.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.5</version> <!-- Use a recent Spring Boot version -->
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>spring-boot-kafka</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>spring-boot-kafka</name>
	<description>Spring Boot Kafka Template Project</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<!-- Spring Web Starter for REST Endpoints -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- Spring for Apache Kafka -->
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<!-- Lombok for boilerplate code reduction -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<!-- Spring Boot DevTools for hot reloading (optional) -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>

		<!-- Test Dependencies -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Spring Kafka Test for Embedded Kafka Broker -->
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Mockito JUnit Jupiter for testing with JUnit 5 -->
		<dependency>
			<groupId>org.mockito</groupId>
			<artifactId>mockito-junit-jupiter</artifactId>
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
```

**Explanation for `pom.xml`:**
*   **`spring-boot-starter-web`**: Provides capabilities for building web applications, including RESTful services. We'll use this for our producer's REST endpoint.
*   **`spring-kafka`**: The core dependency for integrating Spring Boot with Apache Kafka.
*   **`lombok`**: A helpful library that reduces boilerplate code (getters, setters, constructors, etc.) using annotations. Highly recommended for cleaner code.
*   **`spring-boot-starter-test`**: Standard Spring Boot testing dependency, includes JUnit, Mockito, etc.
*   **`spring-kafka-test`**: Essential for testing Kafka applications, providing an `EmbeddedKafkaBroker` that allows running Kafka locally within your tests without needing a separate Kafka instance.
*   **`mockito-junit-jupiter`**: Explicitly includes Mockito support for JUnit 5, which is useful for creating mock objects in unit tests.

---

#### 2. Application Properties

Here, we'll define our Kafka broker details, topic name, and other configurations.

`$$$$ src/main/resources`
`&&&& application.properties`
```properties
@@@@
# Kafka Bootstrap Servers
spring.kafka.bootstrap-servers=localhost:9092

# Producer Configuration
# Key and Value serializers for sending messages
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
# Acknowledge mode (all: wait for all replicas to acknowledge)
spring.kafka.producer.acks=all
# Number of retries on a failed send
spring.kafka.producer.retries=3

# Consumer Configuration
# Group ID for consumers (essential for consumer groups)
spring.kafka.consumer.group-id=my-spring-boot-group
# Key and Value deserializers for receiving messages
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
# Auto commit offsets periodically
spring.kafka.consumer.enable-auto-commit=true
# Time interval for auto committing offsets
spring.kafka.consumer.auto-commit-interval=1000
# Initial offset reset strategy (earliest: start from beginning if no offset exists)
spring.kafka.consumer.auto-offset-reset=earliest

# Custom Topic Name (we'll use this in our application)
app.kafka.topic.name=my-app-topic

# Spring Boot Server Port
server.port=8080
@@@@
```

**Explanation for `application.properties`:**
*   **`spring.kafka.bootstrap-servers`**: The address of your Kafka broker. For local development, `localhost:9092` is common.
*   **`spring.kafka.producer.*`**: Properties for the Kafka producer.
    *   `key-serializer` and `value-serializer`: Define how message keys and values are converted to bytes before sending. We're using `StringSerializer` for keys and `JsonSerializer` for values to send JSON objects.
    *   `acks` and `retries`: Important for ensuring message delivery guarantees.
*   **`spring.kafka.consumer.*`**: Properties for the Kafka consumer.
    *   `group-id`: Consumers with the same `group-id` are part of a consumer group and share partitions of a topic. This is crucial for load balancing and fault tolerance.
    *   `key-deserializer` and `value-deserializer`: Define how received bytes are converted back into objects.
    *   `enable-auto-commit` and `auto-commit-interval`: Configure automatic offset committing.
    *   `auto-offset-reset`: Determines what happens when a consumer starts without a previously committed offset (e.g., first time or after offset expiry).
*   **`app.kafka.topic.name`**: A custom property to define our Kafka topic name, making it easy to change.
*   **`server.port`**: Standard Spring Boot property for the web server's port.

---

#### 3. Main Application Class

This is the standard Spring Boot entry point.

`$$$$ src/main/java/com/example/kafka`
`&&&& SpringBootKafkaApplication.java`
```java
@@@@
package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Kafka template.
 * This class uses @SpringBootApplication which is a convenience annotation
 * that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *                             other beans, and various property settings.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *                   in the `com.example.kafka` package, allowing it to find our controllers,
 *                   services, and configurations.
 */
@SpringBootApplication
public class SpringBootKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootKafkaApplication.class, args);
	}

}
@@@@
```

---

#### 4. Message Payload

We'll define a simple POJO (Plain Old Java Object) that represents the structure of the messages we'll send and receive.

`$$$$ src/main/java/com/example/kafka/payload`
`&&&& Message.java`
```java
@@@@
package com.example.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a simple message payload for Kafka.
 * Uses Lombok annotations for boilerplate code reduction.
 *
 * @Data: Generates getters, setters, toString, equals, and hashCode methods.
 * @NoArgsConstructor: Generates a constructor with no arguments.
 * @AllArgsConstructor: Generates a constructor with all arguments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String content;
    private LocalDateTime timestamp;

    /**
     * Factory method to create a new Message instance with a current timestamp.
     * @param id The unique identifier for the message.
     * @param content The actual message content.
     * @return A new Message object.
     */
    public static Message create(String id, String content) {
        return new Message(id, content, LocalDateTime.now());
    }
}
@@@@
```

**Explanation for `Message.java`:**
*   **`@Data` (Lombok)**: Automatically generates getters, setters, `equals()`, `hashCode()`, and `toString()` methods, reducing boilerplate.
*   **`@NoArgsConstructor` and `@AllArgsConstructor` (Lombok)**: Generate a default constructor and a constructor with all fields, respectively. These are essential for JSON serialization/deserialization.
*   **`LocalDateTime`**: A modern date-time class from Java 8 for handling timestamps.
*   **`create` factory method**: A convenient way to construct messages with the current timestamp.

---

#### 5. Kafka Configuration

This class will configure Kafka-specific beans, such as creating a topic programmatically.

`$$$$ src/main/java/com/example/kafka/config`
`&&&& KafkaTopicConfig.java`
```java
@@@@
package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration class for Kafka topics.
 * It defines beans for creating Kafka topics programmatically when the application starts.
 */
@Configuration
public class KafkaTopicConfig {

    // Inject the topic name from application.properties
    @Value("${app.kafka.topic.name}")
    private String topicName;

    /**
     * Defines a new Kafka topic bean.
     * Spring Boot will automatically create this topic in Kafka if it doesn't already exist.
     *
     * @return A NewTopic object representing the Kafka topic.
     */
    @Bean
    public NewTopic myAppTopic() {
        return TopicBuilder.atLeastOnce(topicName) // Use atLeastOnce for durability by default
                .partitions(1) // Number of partitions for the topic
                .replicas(1)   // Number of replica copies for the topic
                .build();
    }
}
@@@@
```

**Explanation for `KafkaTopicConfig.java`:**
*   **`@Configuration`**: Marks this class as a source of bean definitions.
*   **`@Value("${app.kafka.topic.name}")`**: Injects the topic name defined in `application.properties`.
*   **`@Bean`**: Declares a Spring bean. In this case, it creates a `NewTopic` object.
*   **`TopicBuilder.atLeastOnce(topicName)`**: A convenient builder from Spring Kafka to define topic properties. `atLeastOnce` is a good default for reliability.
*   **`.partitions(1)`**: Defines the number of partitions for the topic. More partitions can increase throughput and parallelism for consumers.
*   **`.replicas(1)`**: Defines the replication factor. For a single-node Kafka setup, 1 is sufficient. For production, usually 3 or more for fault tolerance.

---

#### 6. Kafka Producer

This component is responsible for sending messages to a Kafka topic.

`$$$$ src/main/java/com/example/kafka/producer`
`&&&& KafkaProducerService.java`
```java
@@@@
package com.example.kafka.producer;

import com.example.kafka.payload.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service class responsible for producing (sending) messages to a Kafka topic.
 */
@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    // KafkaTemplate is a high-level abstraction for sending messages to Kafka topics.
    private final KafkaTemplate<String, Message> kafkaTemplate;

    // Inject the topic name from application.properties
    @Value("${app.kafka.topic.name}")
    private String topicName;

    /**
     * Constructor for KafkaProducerService.
     * Spring will automatically inject KafkaTemplate based on the configuration.
     * @param kafkaTemplate The KafkaTemplate instance.
     */
    public KafkaProducerService(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message to the configured Kafka topic.
     * The method returns a CompletableFuture which will be completed when the send operation
     * is acknowledged by Kafka. This allows for asynchronous processing of send results.
     *
     * @param message The Message object to be sent.
     */
    public void sendMessage(Message message) {
        LOGGER.info("Sending message: {} to topic {}", message, topicName);

        // The send method returns a CompletableFuture for asynchronous results.
        CompletableFuture<SendResult<String, Message>> future = kafkaTemplate.send(topicName, message.getId(), message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Message sent successfully! Topic: {}, Partition: {}, Offset: {}, Message: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        message);
            } else {
                LOGGER.error("Failed to send message: {} to topic {}. Error: {}", message, topicName, ex.getMessage(), ex);
            }
        });
    }
}
@@@@
```

`$$$$ src/main/java/com/example/kafka/producer`
`&&&& KafkaProducerController.java`
```java
@@@@
package com.example.kafka.producer;

import com.example.kafka.payload.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for triggering Kafka message production.
 * Exposes an endpoint to send messages to the Kafka topic.
 */
@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaProducerController {

    private final KafkaProducerService producerService;

    /**
     * Constructor for KafkaProducerController.
     * Spring will automatically inject KafkaProducerService.
     * @param producerService The KafkaProducerService instance.
     */
    public KafkaProducerController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    /**
     * REST endpoint to send a message to Kafka.
     *
     * @param messageContent The content of the message to be sent.
     * @return A ResponseEntity indicating the status of the message sending operation.
     */
    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestBody String messageContent) {
        // Create a unique ID for each message
        String messageId = UUID.randomUUID().toString();
        Message message = Message.create(messageId, messageContent);
        producerService.sendMessage(message);
        return ResponseEntity.ok("Message sent to Kafka successfully with ID: " + messageId);
    }
}
@@@@
```

**Explanation for Kafka Producer:**
*   **`KafkaProducerService.java`:**
    *   **`@Service`**: Marks this class as a Spring service component.
    *   **`KafkaTemplate<String, Message>`**: The core Spring Kafka component for sending messages. The generic types define the key type (`String`) and value type (`Message`).
    *   **`@Value("${app.kafka.topic.name}")`**: Injects the topic name.
    *   **`sendMessage(Message message)`**: This method takes a `Message` object and sends it to the configured topic.
    *   **`kafkaTemplate.send(topicName, message.getId(), message)`**: Sends the message. We use `message.getId()` as the Kafka message key. Using a key helps maintain order for messages with the same key within a partition and ensures they land on the same partition.
    *   **`CompletableFuture`**: The `send` method is asynchronous. `CompletableFuture` allows us to handle the success or failure of the send operation when it completes, without blocking the calling thread. This is a good practice for non-blocking I/O operations.
    *   **`LOGGER`**: Uses SLF4J for logging messages, which is standard in Spring Boot applications.
*   **`KafkaProducerController.java`:**
    *   **`@RestController`**: Combines `@Controller` and `@ResponseBody`, indicating that this class handles incoming web requests and returns data directly in the response body.
    *   **`@RequestMapping("/api/v1/kafka")`**: Base path for all endpoints in this controller.
    *   **`@PostMapping("/publish")`**: Maps HTTP POST requests to `/api/v1/kafka/publish`.
    *   **`@RequestBody String messageContent`**: Extracts the request body (the message content) into a String.
    *   **`UUID.randomUUID().toString()`**: Generates a unique ID for each message, which will serve as our Kafka message key.

---

#### 7. Kafka Consumer

This component is responsible for listening to a Kafka topic and processing incoming messages.

`$$$$ src/main/java/com/example/kafka/consumer`
`&&&& KafkaConsumerService.java`
```java
@@@@
package com.example.kafka.consumer;

import com.example.kafka.payload.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for consuming (receiving) messages from a Kafka topic.
 */
@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * This method listens to messages on the Kafka topic defined by `app.kafka.topic.name`.
     *
     * @param message The Message object received from Kafka. Spring automatically deserializes the JSON into a Message object.
     * @param key The key of the Kafka message (in our case, the message ID).
     * @param partition The partition from which the message was consumed.
     * @param offset The offset of the message within its partition.
     * @param topic The name of the topic the message was consumed from.
     */
    @KafkaListener(topics = "${app.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(
            Message message,
            @org.springframework.kafka.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY) String key,
            @org.springframework.kafka.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION) int partition,
            @org.springframework.kafka.annotation.Header(org.springframework.kafka.support.KafkaHeaders.OFFSET) long offset,
            @org.springframework.kafka.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC) String topic) {

        LOGGER.info("Consumed message: [Key: {}, Topic: {}, Partition: {}, Offset: {}, Payload: {}]",
                key, topic, partition, offset, message);

        // Here you would add your business logic to process the received message.
        // For example, saving to a database, triggering another service, etc.
        System.out.println("Processing message content: " + message.getContent() + " at " + message.getTimestamp());
    }
}
@@@@
```

**Explanation for `KafkaConsumerService.java`:**
*   **`@Service`**: Marks this class as a Spring service.
*   **`@KafkaListener`**: This is the core annotation for creating Kafka message listeners.
    *   `topics = "${app.kafka.topic.name}"`: Specifies the topic(s) to listen to. It uses the `app.kafka.topic.name` property from `application.properties`.
    *   `groupId = "${spring.kafka.consumer.group-id}"`: Specifies the consumer group ID. This is crucial for load balancing across multiple consumer instances.
*   **`consumeMessage(...)` method**: This method will be invoked whenever a new message arrives on the configured topic.
    *   **`Message message`**: Spring Kafka automatically deserializes the JSON message value into our `Message` POJO.
    *   **`@Header` annotations**: These are used to extract specific Kafka message headers like the key, partition, offset, and topic directly into method parameters, providing richer context for processing.
    *   **`LOGGER.info(...)`**: Logs the details of the consumed message.
    *   **`System.out.println(...)`**: A placeholder for your actual business logic. This is where you'd integrate with other services, databases, etc.

---

#### 8. Unit Tests

Now, let's create unit tests for our producer and consumer services. For testing Kafka components, `spring-kafka-test` provides an `EmbeddedKafkaBroker` which is excellent for integration tests. However, for true "unit tests" as requested, we'll focus on mocking external dependencies.

##### 8.1 Kafka Producer Service Test

We'll test that the `sendMessage` method correctly interacts with `KafkaTemplate`.

`$$$$ src/test/java/com/example/kafka/producer`
`&&&& KafkaProducerServiceTest.java`
```java
@@@@
package com.example.kafka.producer;

import com.example.kafka.payload.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for KafkaProducerService using Mockito.
 * We mock KafkaTemplate to verify interactions without needing a running Kafka broker.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class KafkaProducerServiceTest {

    @Mock // Mocks the KafkaTemplate dependency
    private KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks // Injects the mocked KafkaTemplate into KafkaProducerService
    private KafkaProducerService kafkaProducerService;

    private static final String TEST_TOPIC = "test-app-topic";

    @BeforeEach
    void setUp() {
        // Use ReflectionTestUtils to set the private field 'topicName'
        // This is necessary because @Value fields are not directly set by @InjectMocks
        ReflectionTestUtils.setField(kafkaProducerService, "topicName", TEST_TOPIC);
    }

    @Test
    void testSendMessage() {
        // Given
        Message testMessage = new Message("123", "Hello Kafka!", LocalDateTime.now());

        // Create a mock CompletableFuture for the send result
        CompletableFuture<SendResult<String, Message>> future = new CompletableFuture<>();
        future.complete(null); // Simulate a successful send (or you can mock a SendResult)

        // When
        // Mock the behavior of kafkaTemplate.send() to return our mock future
        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(testMessage.getId()), any(Message.class)))
                .thenReturn(future);

        kafkaProducerService.sendMessage(testMessage);

        // Then
        // Verify that kafkaTemplate.send was called with the correct arguments
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), messageCaptor.capture());

        assertNotNull(topicCaptor.getValue());
        assertNotNull(keyCaptor.getValue());
        assertNotNull(messageCaptor.getValue());

        // Assert that the captured arguments match our test message
        org.assertj.core.api.Assertions.assertThat(topicCaptor.getValue()).isEqualTo(TEST_TOPIC);
        org.assertj.core.api.Assertions.assertThat(keyCaptor.getValue()).isEqualTo(testMessage.getId());
        org.assertj.core.api.Assertions.assertThat(messageCaptor.getValue().getContent()).isEqualTo(testMessage.getContent());
    }
}
@@@@
```

**Explanation for `KafkaProducerServiceTest.java`:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@Mock private KafkaTemplate<String, Message> kafkaTemplate;`**: Creates a mock instance of `KafkaTemplate`. This mock will simulate the behavior of a real `KafkaTemplate` without actually connecting to Kafka.
*   **`@InjectMocks private KafkaProducerService kafkaProducerService;`**: Creates an instance of `KafkaProducerService` and injects the mock `kafkaTemplate` into it.
*   **`@BeforeEach setUp()`**: Used to initialize test conditions before each test. Here, we use `ReflectionTestUtils.setField` to set the `topicName` field, as `@Value` fields are not handled by `@InjectMocks`.
*   **`when(kafkaTemplate.send(...)).thenReturn(future);`**: Defines the behavior of the mocked `kafkaTemplate`. When `send()` is called with the specified arguments, it will return a mock `CompletableFuture`.
*   **`kafkaProducerService.sendMessage(testMessage);`**: Calls the actual method we are testing.
*   **`verify(kafkaTemplate).send(...)`**: Verifies that the `send` method of the `kafkaTemplate` mock was indeed called.
*   **`ArgumentCaptor`**: Used to capture the arguments passed to the `send` method, allowing us to inspect them and assert their values.
*   **`org.assertj.core.api.Assertions.assertThat(...)`**: A fluent assertion library, often preferred over JUnit's built-in assertions for readability.

##### 8.2 Kafka Consumer Service Test

For unit testing a Kafka consumer, we want to ensure the `consumeMessage` method correctly processes the input. We'll mock the inputs and verify the internal logic.

`$$$$ src/test/java/com/example/kafka/consumer`
`&&&& KafkaConsumerServiceTest.java`
```java
@@@@
package com.example.kafka.consumer;

import com.example.kafka.payload.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test for KafkaConsumerService.
 * We directly invoke the consumeMessage method with mock data and verify its behavior.
 * For more comprehensive integration tests involving a real Kafka broker,
 * consider using @EmbeddedKafka in a separate test class.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class KafkaConsumerServiceTest {

    @InjectMocks // Injects dependencies into KafkaConsumerService (though none exist for this simple test)
    private KafkaConsumerService kafkaConsumerService;

    // We can mock the logger to verify logging behavior if needed
    // private Logger mockLogger = org.mockito.Mockito.mock(Logger.class);

    @BeforeEach
    void setUp() {
        // If there were any @Value fields in consumer, they would be set here using ReflectionTestUtils
        // For this simple consumer, no @Value fields directly impact the method logic we're testing.

        // If you want to verify logger calls:
        // ReflectionTestUtils.setField(kafkaConsumerService, "LOGGER", mockLogger);
    }

    @Test
    void testConsumeMessage() {
        // Given
        String testId = "consumer-test-1";
        String testContent = "Test message from consumer!";
        LocalDateTime testTimestamp = LocalDateTime.now();
        Message testMessage = new Message(testId, testContent, testTimestamp);
        String testKey = testId;
        int testPartition = 0;
        long testOffset = 123L;
        String testTopic = "my-app-topic";

        // When
        // Directly invoke the consumer method with our test data
        // For a true unit test, we just call the method directly.
        // Spring's @KafkaListener mechanism would handle the actual invocation in a running app.
        kafkaConsumerService.consumeMessage(testMessage, testKey, testPartition, testOffset, testTopic);

        // Then
        // For a simple consumer, we might verify logging or side effects.
        // Here, we'll verify that the method completed without exceptions and printed to console.
        // Verifying System.out.println() can be done by redirecting System.out in a real test.
        // For demonstration, we assume it's printed.

        // To verify logging (if you uncommented the mockLogger part in setUp and added it to KafkaConsumerService):
        // verify(mockLogger, times(1)).info(
        //     "Consumed message: [Key: {}, Topic: {}, Partition: {}, Offset: {}, Payload: {}]",
        //     testKey, testTopic, testPartition, testOffset, testMessage
        // );
        // Alternatively, you can use a System.out.println capture utility for the console output.

        // In a more complex scenario, you would verify interactions with other services
        // (e.g., if the consumer calls a repository or another service).
        // For example:
        // verify(mockSomeRepository, times(1)).save(someProcessedData);
    }
}
@@@@
```

**Explanation for `KafkaConsumerServiceTest.java`:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@InjectMocks private KafkaConsumerService kafkaConsumerService;`**: Creates an instance of `KafkaConsumerService`. Since our consumer doesn't have other direct service dependencies, we don't need `@Mock` here, but it's good practice for when it does.
*   **`testConsumeMessage()`**:
    *   We create a `Message` object and other Kafka header values (key, partition, offset, topic) as if they were received from Kafka.
    *   **`kafkaConsumerService.consumeMessage(...)`**: We directly call the `consumeMessage` method. In a real application, Spring Kafka would call this method, but for a unit test, we simulate that call.
    *   **Verification**: For a simple consumer that just logs and prints, verifying its behavior is tricky without capturing `System.out` or mocking the logger. In a real-world scenario, you would typically verify that the consumer correctly calls other services (e.g., `verify(mockDatabaseService).save(processedData);`).

---

#### How to Run and Test:

1.  **Ensure Kafka is running:** You need a running Kafka broker (and Zookeeper) on `localhost:9092`. You can use Docker for this:
    ```bash
    docker-compose -f docker-compose.yml up -d
    ```
    (You'd need a `docker-compose.yml` file like this):
    ```yaml
    # docker-compose.yml
    version: '3'
    services:
      zookeeper:
        image: confluentinc/cp-zookeeper:7.4.0
        hostname: zookeeper
        ports:
          - "2181:2181"
        environment:
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000

      broker:
        image: confluentinc/cp-kafka:7.4.0
        hostname: broker
        depends_on:
          - zookeeper
        ports:
          - "9092:9092"
          - "9101:9101"
        environment:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
          KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
          KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
          KAFKA_NUM_PARTITIONS: 1
          KAFKA_JMX_PORT: 9101
          KAFKA_JMX_HOSTNAME: localhost
    ```

2.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```
    (Or `gradle clean build` if using Gradle)

3.  **Run the Spring Boot application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    You should see console output indicating the application started and the Kafka consumer is listening.

4.  **Send messages (using `curl`):**
    Open a new terminal and send some messages using `curl`:
    ```bash
    curl -X POST -H "Content-Type: text/plain" -d "Hello from Producer 1!" http://localhost:8080/api/v1/kafka/publish
    curl -X POST -H "Content-Type: text/plain" -d "Another message for Kafka!" http://localhost:8080/api/v1/kafka/publish
    ```
    You will see the producer logging `Message sent successfully!` and the consumer logging `Consumed message: [...]` in your application's console output.

5.  **Run Unit Tests:**
    ```bash
    ./mvnw test
    ```
    This will execute the `KafkaProducerServiceTest` and `KafkaConsumerServiceTest` (and any other tests you have). They should pass without needing a running Kafka broker because we used Mockito.

---

This template provides a solid foundation for building Spring Boot applications that interact with Apache Kafka. Remember to adjust topic names, partitions, replication factors, and error handling strategies based on your specific application requirements and production environment. Happy coding!