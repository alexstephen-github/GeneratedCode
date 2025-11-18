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
