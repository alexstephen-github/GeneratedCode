package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot application.
 * This class uses @SpringBootApplication which is a convenience annotation that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings. For example, if spring-webmvc is on the classpath,
 *   this annotation flags the application as a web application and sets up a DispatcherServlet.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the 'com.example.demo' package, allowing it to find and register controllers, services, etc.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
