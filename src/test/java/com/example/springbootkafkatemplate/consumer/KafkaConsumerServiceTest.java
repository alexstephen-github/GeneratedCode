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
