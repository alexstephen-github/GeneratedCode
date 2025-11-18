package com.example.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic test to ensure the Spring Boot application context loads successfully.
 * This is a good sanity check for your application configuration.
 */
@SpringBootTest
class RestapiApplicationTests {

    /**
     * Test method to load the application context.
     * If the context fails to load, this test will fail, indicating a configuration issue.
     */
    @Test
    void contextLoads() {
        // This test simply attempts to load the Spring application context.
        // If it loads without exceptions, the test passes.
    }

}
