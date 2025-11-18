package com.example.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot REST API application.
 * This class uses the @SpringBootApplication annotation, which is a convenience annotation
 * that combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * It effectively sets up a Spring application context and starts the embedded web server.
 */
@SpringBootApplication
public class RestapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestapiApplication.class, args);
    }

}
