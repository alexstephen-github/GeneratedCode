package com.example.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot application.
 *
 * {@code @SpringBootApplication} is a convenience annotation that adds:
 * - {@code @Configuration}: Tags the class as a source of bean definitions for the application context.
 * - {@code @EnableAutoConfiguration}: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings.
 * - {@code @ComponentScan}: Tells Spring to look for other components, configurations, and services
 *   in the 'com.example.restapi' package, allowing it to find controllers, services, etc.
 */
@SpringBootApplication
public class SpringBootRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRestApiApplication.class, args);
    }

}
