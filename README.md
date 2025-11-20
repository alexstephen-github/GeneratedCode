Hello there! I'd be happy to help you generate a comprehensive code template for a Spring Boot REST API. This template will include a typical structure with a Model, DTO, Repository, Service, and Controller layer, along with global exception handling, and importantly, unit tests for each layer.

We'll use a simple `Product` entity as an example.

### Project Structure Overview

Here's the logical structure we'll follow, assuming `com.example.restapi` as the base package:

```
src/main/java
└── com/example/restapi
    ├── SpringBootRestApiApplication.java
    ├── config
    │   └── OpenApiConfig.java (Optional but good practice for API docs)
    ├── controller
    │   └── ProductController.java
    ├── dto
    │   └── ProductDTO.java
    ├── exception
    │   ├── ResourceNotFoundException.java
    │   └── GlobalExceptionHandler.java
    ├── model
    │   └── Product.java
    ├── repository
    │   └── ProductRepository.java
    ├── service
    │   ├── ProductService.java (Interface)
    │   └── impl
    │       └── ProductServiceImpl.java (Implementation)
src/test/java
└── com/example/restapi
    ├── controller
    │   └── ProductControllerTest.java
    ├── repository
    │   └── ProductRepositoryTest.java
    ├── service
    │   └── ProductServiceTest.java
```

### Maven `pom.xml` (Dependencies)

While not a code file to be generated with the delimiters, it's crucial to list the essential dependencies you'll need in your `pom.xml` for this template to work.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Use the latest stable version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>spring-boot-rest-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-rest-api</name>
    <description>Demo project for Spring Boot REST API</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId> <!-- Optional, for JWT or basic auth -->
        </dependency>

        <!-- Database Driver (H2 for in-memory, convenient for development/testing) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- PostgreSQL driver example: -->
        <!--
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        -->

        <!-- Lombok (for boilerplate reduction like getters/setters) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- OpenAPI (Swagger UI) for API Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version> <!-- Use a compatible version -->
        </dependency>

        <!-- Spring Boot Test Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
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

---

### Application Entry Point

$$$$
&&&&SpringBootRestApiApplication.java
@@@@
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
@@@@

**Explanation:**
This is the heart of your Spring Boot application. The `main` method uses `SpringApplication.run()` to launch the application. The `@SpringBootApplication` annotation simplifies configuration by combining `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

---

### Configuration (OpenAPI/Swagger UI)

$$$$config
&&&&OpenApiConfig.java
@@@@
package com.example.restapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger UI).
 * This sets up metadata for your API documentation.
 *
 * Once the application is running, you can access Swagger UI at:
 * http://localhost:8080/swagger-ui.html or http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Spring Boot Product REST API")
                        .description("A sample Spring Boot REST API for managing products.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Product Wiki Documentation")
                        .url("https://example.com/products/wiki"));
    }
}
@@@@

**Explanation:**
This class configures OpenAPI (formerly Swagger), which automatically generates interactive API documentation for your endpoints. This makes it easy for developers to understand and test your API. After running the application, navigate to `http://localhost:8080/swagger-ui.html` (or similar path depending on Spring Doc version) in your browser.

---

### Model (Entity)

$$$$model
&&&&Product.java
@@@@
package com.example.restapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 *
 * {@code @Entity}: Marks this class as a JPA entity, meaning it maps to a database table.
 * {@code @Table}: Specifies the table name in the database.
 * {@code @Data} (Lombok): Generates getters, setters, toString, equals, and hashCode methods.
 * {@code @NoArgsConstructor} (Lombok): Generates a constructor with no arguments.
 * {@code @AllArgsConstructor} (Lombok): Generates a constructor with all arguments.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * The unique identifier for the product.
     * {@code @Id}: Marks this field as the primary key.
     * {@code @GeneratedValue}: Configures the way the primary key is generated.
     *   - GenerationType.IDENTITY: The database assigns the primary key (e.g., auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     * {@code @Column}: Specifies column details like name, length, and nullability.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * A description of the product.
     */
    @Column(length = 500)
    private String description;

    /**
     * The price of the product.
     */
    @Column(nullable = false)
    private Double price;
}
@@@@

**Explanation:**
The `Product` class is our JPA entity. It represents the data structure as it's stored in the database.
- `@Entity`: Tells JPA that this class is an entity.
- `@Table`: Specifies the database table name.
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok): These annotations drastically reduce boilerplate code for getters, setters, constructors, etc.
- `@Id` and `@GeneratedValue`: Define the primary key and its generation strategy (e.g., auto-incremented by the database).

---

### Data Transfer Object (DTO)

$$$$dto
&&&&ProductDTO.java
@@@@
package com.example.restapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Product.
 * Used for receiving product data from API requests (e.g., POST, PUT).
 * It typically excludes the 'id' field as it's generated by the database for new products.
 *
 * {@code @Data}, {@code @NoArgsConstructor}, {@code @AllArgsConstructor} (Lombok) for boilerplate.
 * Validation annotations (e.g., {@code @NotBlank}, {@code @NotNull}, {@code @DecimalMin})
 * ensure data integrity before processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    /**
     * The name of the product. Cannot be null or empty.
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    /**
     * A description of the product. Optional, but has a size constraint if provided.
     */
    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    private String description;

    /**
     * The price of the product. Cannot be null and must be a positive number.
     */
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    private Double price;
}
@@@@

**Explanation:**
The `ProductDTO` (Data Transfer Object) is used for data exchange between the client and the server.
- It often contains only the fields relevant for client interaction, e.g., for creating a new product, the client doesn't provide the `id`.
- `@NotBlank`, `@NotNull`, `@Size`, `@DecimalMin`: These are Jakarta Bean Validation annotations that enforce constraints on the incoming data. Spring Boot automatically validates DTOs when they are marked with `@Valid` in controller methods.

---

### Repository Layer

$$$$repository
&&&&ProductRepository.java
@@@@
package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 *
 * {@code @Repository}: Indicates that this is a DAO (Data Access Object) component.
 * Extends {@code JpaRepository<Product, Long>}:
 * - Provides standard CRUD (Create, Read, Update, Delete) operations for the Product entity.
 * - The first type parameter is the entity type (Product).
 * - The second type parameter is the type of the entity's primary key (Long).
 * Spring Data JPA automatically provides an implementation at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be added here if needed,
    // Spring Data JPA can infer queries from method names (e.g., findByNameContaining)
}
@@@@

**Explanation:**
The `ProductRepository` is an interface that extends `JpaRepository`.
- `@Repository`: Marks this as a repository component.
- `JpaRepository<Product, Long>`: This powerful interface from Spring Data JPA provides ready-to-use CRUD (Create, Read, Update, Delete) methods for our `Product` entity (identified by its `Long` ID) without writing any boilerplate code.

---

### Service Layer (Interface)

$$$$service
&&&&ProductService.java
@@@@
package com.example.restapi.service;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.model.Product;

import java.util.List;

/**
 * Interface for the Product Service.
 * Defines the business logic operations available for Product entities.
 *
 * Using an interface promotes loose coupling and allows for multiple implementations
 * (e.g., a mock implementation for testing, a database implementation).
 */
public interface ProductService {

    /**
     * Retrieves all products.
     * @return A list of all products.
     */
    List<Product> getAllProducts();

    /**
     * Retrieves a product by its ID.
     * @param id The ID of the product to retrieve.
     * @return The found product.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    Product getProductById(Long id);

    /**
     * Creates a new product.
     * @param productDTO The DTO containing the data for the new product.
     * @return The created product entity.
     */
    Product createProduct(ProductDTO productDTO);

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productDTO The DTO containing the updated data.
     * @return The updated product entity.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    Product updateProduct(Long id, ProductDTO productDTO);

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    void deleteProduct(Long id);
}
@@@@

**Explanation:**
The `ProductService` interface defines the contract for our business logic. It clearly outlines what operations can be performed on products. This separation of concerns is a good practice as it makes the application more modular and testable.

---

### Service Layer (Implementation)

$$$$service/impl
&&&&ProductServiceImpl.java
@@@@
package com.example.restapi.service.impl;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import com.example.restapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the ProductService interface.
 * Contains the business logic for managing products.
 *
 * {@code @Service}: Marks this class as a Spring service component.
 * {@code @Transactional}: Ensures that all operations within a method run within a single transaction.
 *   If any part of the method fails, the entire transaction is rolled back.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection of ProductRepository.
     * Spring automatically injects an instance of ProductRepository.
     */
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     * {@code @Transactional(readOnly = true)}: Optimization for read-only operations,
     * signaling to the underlying JPA provider that no modifications will occur.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     * Uses {@code orElseThrow} to throw a custom exception if the product is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Creates a new product by converting the DTO to an entity and saving it.
     */
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     * Retrieves the product, updates its fields from the DTO, and saves the changes.
     */
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());

        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product by its ID.
     * Checks if the product exists before attempting to delete.
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
@@@@

**Explanation:**
`ProductServiceImpl` provides the concrete implementation of the `ProductService` interface.
- `@Service`: Marks this class as a service component, making it eligible for Spring's dependency injection.
- `@Autowired`: Used for constructor injection of `ProductRepository`, allowing the service to interact with the database.
- `@Transactional`: Ensures that operations are performed within a database transaction. If a method completes successfully, the transaction commits; if an exception occurs, it rolls back, maintaining data consistency. `readOnly = true` is an optimization for methods that only read data.
- It translates `ProductDTO` to `Product` entity for persistence and handles `ResourceNotFoundException`.

---

### Exception Handling (Custom Exception)

$$$$exception
&&&&ResourceNotFoundException.java
@@@@
package com.example.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a resource (e.g., a Product) was not found.
 *
 * {@code @ResponseStatus(HttpStatus.NOT_FOUND)}: When this exception is thrown
 * from a controller method, Spring will automatically respond with an HTTP 404 Not Found status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
@@@@

**Explanation:**
`ResourceNotFoundException` is a custom exception.
- `@ResponseStatus(HttpStatus.NOT_FOUND)`: This annotation is crucial. When this exception is thrown in a controller or service layer, Spring will automatically map it to an HTTP 404 (Not Found) status code in the API response.

---

### Exception Handling (Global)

$$$$exception
&&&&GlobalExceptionHandler.java
@@@@
package com.example.restapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the REST API.
 * This class centralizes the handling of exceptions across all controllers.
 *
 * {@code @ControllerAdvice}: A specialization of {@code @Component} that makes a class
 * able to handle exceptions across the whole application.
 *
 * Extends {@code ResponseEntityExceptionHandler}: Provides a convenient base class
 * for {@code @ControllerAdvice} classes to handle standard Spring MVC exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@code ResourceNotFoundException}.
     *
     * @param ex The ResourceNotFoundException instance.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a custom error message and HTTP 404 status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "NOT_FOUND"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles generic {@code Exception} (catch-all for unhandled exceptions).
     *
     * @param ex The Exception instance.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a custom error message and HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@code MethodArgumentNotValidException} for validation errors.
     * This method overrides the default handling provided by {@code ResponseEntityExceptionHandler}.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @param headers The HTTP headers.
     * @param status The HTTP status.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a map of validation errors and HTTP 400 status.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                "BAD_REQUEST",
                errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * A simple structure to hold error details for API responses.
     */
    private static class ErrorDetails {
        private LocalDateTime timestamp;
        private String message;
        private String details;
        private String errorCode;
        private Map<String, String> validationErrors; // For MethodArgumentNotValidException

        public ErrorDetails(LocalDateTime timestamp, String message, String details, String errorCode) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.errorCode = errorCode;
        }

        public ErrorDetails(LocalDateTime timestamp, String message, String details, String errorCode, Map<String, String> validationErrors) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.errorCode = errorCode;
            this.validationErrors = validationErrors;
        }

        // Getters for all fields
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
        public String getErrorCode() { return errorCode; }
        public Map<String, String> getValidationErrors() { return validationErrors; }
    }
}
@@@@

**Explanation:**
`GlobalExceptionHandler` centralizes error handling for all controllers.
- `@ControllerAdvice`: This annotation makes the class capable of handling exceptions across the entire application.
- `@ExceptionHandler`: Annotates methods that will handle specific exceptions.
    - `handleResourceNotFoundException`: Catches our custom `ResourceNotFoundException` and returns a formatted JSON error response with HTTP 404.
    - `handleGlobalException`: A catch-all for any unhandled `Exception`, returning HTTP 500 (Internal Server Error).
- `handleMethodArgumentNotValid`: This method overrides Spring Boot's default handling for validation errors (e.g., when `@Valid` fails on a `ProductDTO`). It extracts all validation errors and returns them in a structured map with HTTP 400 (Bad Request).
- `ErrorDetails`: A simple inner class to provide a consistent JSON structure for error responses.

---

### Controller Layer

$$$$controller
&&&&ProductController.java
@@@@
package com.example.restapi.controller;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.model.Product;
import com.example.restapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing products.
 * Exposes API endpoints for CRUD operations on Product resources.
 *
 * {@code @RestController}: Combines {@code @Controller} and {@code @ResponseBody}.
 *   {@code @Controller}: Marks the class as a Spring MVC controller.
 *   {@code @ResponseBody}: Indicates that the return value of the methods should be bound
 *     directly to the web response body.
 * {@code @RequestMapping("/api/products")}: Maps all requests starting with /api/products to this controller.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for dependency injection of ProductService.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     * GET /api/products
     * @return A {@code ResponseEntity} containing a list of products and HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Retrieves a single product by its ID.
     * GET /api/products/{id}
     * {@code @PathVariable}: Binds the path variable from the URL to the method parameter.
     * @param id The ID of the product to retrieve.
     * @return A {@code ResponseEntity} containing the product and HTTP 200 OK status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Creates a new product.
     * POST /api/products
     * {@code @RequestBody}: Binds the HTTP request body to the ProductDTO object.
     * {@code @Valid}: Triggers validation of the ProductDTO based on its annotations.
     * {@code @ResponseStatus(HttpStatus.CREATED)}: Sets the default response status to 201 Created.
     * @param productDTO The DTO containing the data for the new product.
     * @return A {@code ResponseEntity} containing the created product and HTTP 201 CREATED status.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Updates an existing product.
     * PUT /api/products/{id}
     * @param id The ID of the product to update.
     * @param productDTO The DTO containing the updated data.
     * @return A {@code ResponseEntity} containing the updated product and HTTP 200 OK status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/products/{id}
     * {@code @ResponseStatus(HttpStatus.NO_CONTENT)}: Sets the default response status to 204 No Content.
     * @param id The ID of the product to delete.
     * @return A {@code ResponseEntity} with no content and HTTP 204 NO_CONTENT status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
@@@@

**Explanation:**
The `ProductController` exposes the REST API endpoints.
- `@RestController`: A convenience annotation that combines `@Controller` and `@ResponseBody`. It means every method implicitly returns a JSON or XML response.
- `@RequestMapping("/api/products")`: Specifies the base URL path for all endpoints in this controller.
- `@Autowired`: Injects the `ProductService`.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Map HTTP methods to specific controller methods.
- `@PathVariable`: Extracts values from the URL path (e.g., `id`).
- `@RequestBody`: Binds the HTTP request body (e.g., JSON payload) to a Java object (`ProductDTO`).
- `@Valid`: Triggers validation on the `ProductDTO` using the annotations defined within it. If validation fails, a `MethodArgumentNotValidException` is thrown, which is handled by `GlobalExceptionHandler`.
- `ResponseEntity`: Allows for full control over the HTTP response, including status code, headers, and body.

---

### Unit Test: Repository Layer

$$$$src/test/java/com/example/restapi/repository
&&&&ProductRepositoryTest.java
@@@@
package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProductRepository}.
 *
 * {@code @DataJpaTest}: A specialized annotation for JPA tests.
 *   - It disables full auto-configuration and instead applies only configuration relevant to JPA tests.
 *   - By default, tests annotated with {@code @DataJpaTest} are transactional and roll back at the end
 *     of each test method. This means your test data won't pollute the database.
 *   - It uses an in-memory database (like H2) if available on the classpath.
 */
@DataJpaTest
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager; // Used to manually persist and retrieve entities for tests

    private Product product1;
    private Product product2;

    /**
     * Set up common test data before each test method.
     */
    @BeforeEach
    void setUp() {
        // Clear the database before each test to ensure isolation
        productRepository.deleteAll();

        product1 = new Product(null, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(null, "Mouse", "Wireless gaming mouse", 50.00);

        // Persist entities using TestEntityManager to ensure they are properly managed
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
    }

    @Test
    @DisplayName("Should save a product")
    void givenProductObject_whenSave_thenReturnSavedProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 100.00);

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Keyboard");
    }

    @Test
    @DisplayName("Should find all products")
    void givenProductsInDb_whenFindAll_thenReturnAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).containsExactlyInAnyOrder(product1, product2);
    }

    @Test
    @DisplayName("Should find product by ID")
    void givenProductId_whenFindById_thenReturnProduct() {
        // When
        Optional<Product> foundProduct = productRepository.findById(product1.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void givenInvalidProductId_whenFindById_thenReturnEmptyOptional() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L); // Non-existent ID

        // Then
        assertThat(foundProduct).isNotPresent();
    }

    @Test
    @DisplayName("Should update a product")
    void givenProductObject_whenUpdateProduct_thenReturnUpdatedProduct() {
        // Given
        product1.setName("Updated Laptop");
        product1.setPrice(1300.00);

        // When
        Product updatedProduct = productRepository.save(product1);

        // Then
        assertThat(updatedProduct.getName()).isEqualTo("Updated Laptop");
        assertThat(updatedProduct.getPrice()).isEqualTo(1300.00);
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void givenProductId_whenDeleteById_thenRemoveProduct() {
        // When
        productRepository.deleteById(product1.getId());
        Optional<Product> foundProduct = productRepository.findById(product1.getId());

        // Then
        assertThat(foundProduct).isNotPresent();
        assertThat(productRepository.findAll()).hasSize(1); // Only product2 should remain
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void givenProductId_whenExistsById_thenReturnTrue() {
        // When
        boolean exists = productRepository.existsById(product1.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist by ID")
    void givenInvalidProductId_whenExistsById_thenReturnFalse() {
        // When
        boolean exists = productRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
@@@@

**Explanation:**
- `@DataJpaTest`: This is a powerful annotation for testing JPA repositories. It sets up an in-memory database (like H2) and configures only the JPA-related components, making tests fast and isolated.
- `TestEntityManager`: Provided by `@DataJpaTest`, it allows direct interaction with the persistence context, useful for setting up test data.
- `@BeforeEach`: Ensures that initial test data is set up before each test method, guaranteeing test isolation.
- `assertThat` (from AssertJ): A fluent assertion library that makes assertions very readable.
- These tests cover basic CRUD operations, ensuring the repository methods work as expected with the underlying database.

---

### Unit Test: Service Layer

$$$$src/test/java/com/example/restapi/service
&&&&ProductServiceTest.java
@@@@
package com.example.restapi.service;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import com.example.restapi.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProductServiceImpl}.
 *
 * {@code @ExtendWith(MockitoExtension.class)}: Enables Mockito annotations like {@code @Mock} and {@code @InjectMocks}.
 *   This automatically initializes mocks before each test method.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductServiceImpl
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless gaming mouse", 50.00);
        productDTO = new ProductDTO("Keyboard", "Mechanical keyboard", 100.00);
    }

    @Test
    @DisplayName("Should return all products")
    void givenProductsExist_whenGetAllProducts_thenReturnAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<Product> products = productService.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).containsExactlyInAnyOrder(product1, product2);
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @Test
    @DisplayName("Should return product by ID when found")
    void givenProductId_whenGetProductById_thenReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        Product foundProduct = productService.getProductById(1L);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by ID not found")
    void givenInvalidProductId_whenGetProductById_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create a new product")
    void givenProductDTO_whenCreateProduct_thenReturnCreatedProduct() {
        // Given
        Product productToSave = new Product(null, productDTO.getName(), productDTO.getDescription(), productDTO.getPrice());
        when(productRepository.save(any(Product.class))).thenReturn(product1); // Simulate saving and returning product1 (with ID)

        // When
        Product createdProduct = productService.createProduct(productDTO);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(productDTO.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update an existing product")
    void givenProductIdAndProductDTO_whenUpdateProduct_thenReturnUpdatedProduct() {
        // Given
        Product updatedProduct = new Product(1L, "Updated Laptop", "Updated description", 1300.00);
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated description", 1300.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        Product result = productService.updateProduct(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getPrice()).isEqualTo(1300.00);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent product")
    void givenInvalidProductIdAndProductDTO_whenUpdateProduct_thenThrowResourceNotFoundException() {
        // Given
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated description", 1300.00);
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(999L, updateDTO));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class)); // save should not be called
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void givenProductId_whenDeleteProduct_thenDoNothing() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L); // Mock void method

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void givenInvalidProductId_whenDeleteProduct_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong()); // deleteById should not be called
    }
}
@@@@

**Explanation:**
- `@ExtendWith(MockitoExtension.class)`: Integrates Mockito with JUnit 5.
- `@Mock`: Creates a mock object of `ProductRepository`. We don't want to hit the real database during service tests.
- `@InjectMocks`: Injects the mock `ProductRepository` into the `ProductServiceImpl` instance that we are testing.
- `when(...).thenReturn(...)`: This is how we define the behavior of our mock objects. When a specific method call occurs on the mock (e.g., `productRepository.findById(1L)`), it should return a predefined value.
- `doNothing().when(...)`: Used for mocking void methods.
- `verify(...)`: Used to verify that a method on a mock object was called a certain number of times or with specific arguments.
- `assertThrows(...)`: JUnit 5 assertion for testing if an expected exception is thrown.
- These tests focus solely on the business logic within the `ProductService`, isolating it from external dependencies like the database by mocking the repository.

---

### Unit Test: Controller Layer

$$$$src/test/java/com/example/restapi/controller
&&&&ProductControllerTest.java
@@@@
package com.example.restapi.controller;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link ProductController}.
 *
 * {@code @WebMvcTest(ProductController.class)}: Focuses on Spring MVC components.
 *   - It auto-configures Spring MVC infrastructure and limits the beans to only those needed
 *     for testing controllers.
 *   - It does NOT load the full application context (e.g., service, repository layers are not loaded).
 *   - Used with {@code MockMvc} to send HTTP requests to the controller.
 * {@code @MockBean}: Adds mock objects to the Spring application context for dependencies
 *   that the controller needs (e.g., ProductService).
 */
@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests

    @MockBean // Creates a mock instance of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

    private Product product1;
    private Product product2;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless gaming mouse", 50.00);
        productDTO = new ProductDTO("Keyboard", "Mechanical keyboard", 100.00);
    }

    @Test
    @DisplayName("Should create product and return 201 CREATED")
    void givenProductDTO_whenCreateProduct_thenReturnSavedProduct() throws Exception {
        // Given
        given(productService.createProduct(any(ProductDTO.class)))
                .willReturn(new Product(3L, productDTO.getName(), productDTO.getDescription(), productDTO.getPrice()));

        // When
        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));

        // Then
        response.andDo(print()) // Print the request and response details
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.description", is(productDTO.getDescription())))
                .andExpect(jsonPath("$.price", is(productDTO.getPrice())));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST for invalid product creation")
    void givenInvalidProductDTO_whenCreateProduct_thenReturnBadRequest() throws Exception {
        // Given
        ProductDTO invalidProductDTO = new ProductDTO("", null, 0.0); // Invalid data

        // When
        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.validationErrors.name", is("Product name is required")))
                .andExpect(jsonPath("$.validationErrors.price", is("Product price must be greater than 0")));
    }


    @Test
    @DisplayName("Should get all products and return 200 OK")
    void givenListOfProducts_whenGetAllProducts_thenReturnProductsList() throws Exception {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        given(productService.getAllProducts()).willReturn(products);

        // When
        ResultActions response = mockMvc.perform(get("/api/products"));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()", is(products.size())))
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));
    }

    @Test
    @DisplayName("Should get product by ID and return 200 OK")
    void givenProductId_whenGetProductById_thenReturnProduct() throws Exception {
        // Given
        given(productService.getProductById(1L)).willReturn(product1);

        // When
        ResultActions response = mockMvc.perform(get("/api/products/{id}", 1L));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is(product1.getName())))
                .andExpect(jsonPath("$.price", is(product1.getPrice())));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when product by ID not found")
    void givenInvalidProductId_whenGetProductById_thenReturnNotFound() throws Exception {
        // Given
        given(productService.getProductById(anyLong())).willThrow(new ResourceNotFoundException("Product not found"));

        // When
        ResultActions response = mockMvc.perform(get("/api/products/{id}", 999L));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    @DisplayName("Should update product and return 200 OK")
    void givenUpdateProductDTO_whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO updatedDTO = new ProductDTO("Updated Laptop", "Updated desc", 1300.00);
        Product updatedProduct = new Product(1L, "Updated Laptop", "Updated desc", 1300.00);

        given(productService.updateProduct(anyLong(), any(ProductDTO.class))).willReturn(updatedProduct);

        // When
        ResultActions response = mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())))
                .andExpect(jsonPath("$.price", is(updatedProduct.getPrice())));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when updating non-existent product")
    void givenInvalidProductId_whenUpdateProduct_thenReturnNotFound() throws Exception {
        // Given
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated desc", 1300.00);
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).updateProduct(anyLong(), any(ProductDTO.class));

        // When
        ResultActions response = mockMvc.perform(put("/api/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    @DisplayName("Should delete product and return 204 NO CONTENT")
    void givenProductId_whenDeleteProduct_thenReturnsNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(anyLong()); // Mock void method

        // When
        ResultActions response = mockMvc.perform(delete("/api/products/{id}", 1L));

        // Then
        response.andDo(print())
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deleting non-existent product")
    void givenInvalidProductId_whenDeleteProduct_thenReturnNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(anyLong());

        // When
        ResultActions response = mockMvc.perform(delete("/api/products/{id}", 999L));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }
}
@@@@

**Explanation:**
- `@WebMvcTest(ProductController.class)`: This annotation is specific for testing Spring MVC controllers. It initializes the web layer without spinning up the entire application context, making tests faster.
- `MockMvc`: Provided by `@WebMvcTest`, it allows you to perform HTTP requests to your controller and assert the results without starting a real HTTP server.
- `@MockBean`: Creates a mock of `ProductService` and injects it into the controller. We mock the service because we only want to test the controller's logic (how it handles requests, status codes, and marshaling JSON), not the service's underlying business logic (which is tested separately in `ProductServiceTest`).
- `ObjectMapper`: Used to convert Java objects to JSON strings (for request bodies) and vice versa.
- `mockMvc.perform(...)`: Executes a mock HTTP request.
- `ResultActions.andExpect(...)`: Used for making assertions on the response (e.g., status code, JSON payload).
- `jsonPath(...)`: Used for asserting specific values within the JSON response.
- `BDDMockito.given(...)`: A more readable way to set up mock behavior, following the BDD (Behavior-Driven Development) style (Given-When-Then).

---

This complete template provides a robust foundation for your Spring Boot REST API, including best practices for layering, DTOs, exception handling, and thorough unit testing. Remember to configure your `application.properties` or `application.yml` for database connection details.