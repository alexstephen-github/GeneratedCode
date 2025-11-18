Here's a complete Spring Boot REST API code template, designed to be friendly, helpful, and follow good practices, including unit tests and explanations.

This template provides a basic "Product Management" API with standard CRUD (Create, Read, Update, Delete) operations.

---

This template assumes you have Maven installed and have a Java Development Kit (JDK) 17 or higher.

To use this template:
1.  Create an empty folder for your project.
2.  Copy the `pom.xml` and `application.properties` files into the root of this folder.
3.  Create the `src/main/java/com/example/restapi` and `src/test/java/com/example/restapi` folder structures.
4.  Copy the respective `.java` files into their designated subfolders.
5.  Open a terminal in the project root and run `mvn spring-boot:run`.
6.  You can then access the API using tools like Postman, curl, or your web browser. For example, `http://localhost:8080/api/v1/products`.

---

$$$$
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
    <artifactId>restapi</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>restapi</name>
    <description>Spring Boot REST API Template</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <!-- Spring Boot Web Starter: For building RESTful applications -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Data JPA Starter: For database interaction using JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database: In-memory database for development/testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok: Reduces boilerplate code (getters, setters, constructors) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Validation Starter: For request body validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Test Starter: For unit and integration testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
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
This `pom.xml` defines your project's dependencies and build configuration.
*   `spring-boot-starter-parent`: Provides sensible defaults for a Spring Boot project.
*   `spring-boot-starter-web`: Enables building web applications with Spring MVC.
*   `spring-boot-starter-data-jpa`: Simplifies database access with Spring Data JPA and Hibernate.
*   `h2`: An embedded in-memory database, great for development and testing as it doesn't require a separate database server. Data is lost on application restart.
*   `lombok`: A library that auto-generates boilerplate code (like getters, setters, constructors), making your code cleaner.
*   `spring-boot-starter-validation`: Adds support for Java Bean Validation (JSR-380) for input validation.
*   `spring-boot-starter-test`: Contains essential utilities for testing Spring Boot applications.

---

$$$$
&&&& application.properties
@@@@
# H2 Database Configuration (in-memory, for development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# JPA and Hibernate configuration
# 'update' will update the schema based on your entities. Use 'create' to drop and recreate.
# In production, use 'none' or 'validate' and manage schema migrations with tools like Flyway/Liquibase.
spring.jpa.hibernate.ddl-auto=update

# Enable H2 Console for easily viewing the in-memory database
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true # Allows remote connections (use with caution in prod)

# Logging
logging.level.org.hibernate.SQL=debug # Log SQL queries
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=trace # Log query parameters
@@@@
**Explanation:**
This file configures your application.
*   **H2 Database:** Sets up the in-memory H2 database.
*   `spring.jpa.hibernate.ddl-auto=update`: Tells Hibernate to automatically update the database schema based on your JPA entities. For production, you'd typically disable this or set it to `validate` and use dedicated schema migration tools.
*   `spring.h2.console.enabled=true`: Enables the H2 web console, accessible at `http://localhost:8080/h2-console` after the application starts. You can use `jdbc:h2:mem:testdb` as the JDBC URL to connect.
*   **Logging:** Configures Hibernate to log generated SQL queries and their parameters, which is very useful for debugging.

---

$$$$ src/main/java/com/example/restapi
&&&& RestapiApplication.java
@@@@
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
@@@@
**Explanation:**
This is the main class that starts your Spring Boot application.
*   `@SpringBootApplication`: This powerful annotation combines three others:
    *   `@Configuration`: Tags the class as a source of bean definitions.
    *   `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
    *   `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com.example.restapi` package (and its sub-packages).
*   `SpringApplication.run()`: This static method bootstraps the application, creates and refreshes the application context, and starts the embedded web server (Tomcat by default).

---

$$$$ src/main/java/com/example/restapi/model
&&&& Product.java
@@@@
package com.example.restapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 * This is the domain model class.
 *
 * Lombok annotations:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @NoArgsConstructor: Generates a constructor with no arguments.
 * - @AllArgsConstructor: Generates a constructor with all arguments.
 *
 * JPA annotations:
 * - @Entity: Marks this class as a JPA entity, meaning it maps to a database table.
 * - @Table: Specifies the name of the database table (optional, defaults to class name).
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * @Id: Marks this field as the primary key.
     * @GeneratedValue: Specifies the primary key generation strategy.
     *                  IDENTITY uses an auto-incrementing column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product. Cannot be null.
     * @Column(nullable = false): Ensures this column cannot be null in the database.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Price of the product. Cannot be null.
     */
    @Column(nullable = false)
    private Double price;
}
@@@@
**Explanation:**
This is your **Entity** class, which maps to a table in your database.
*   `@Entity`: Marks this class as a JPA entity.
*   `@Table(name = "products")`: Specifies the name of the database table.
*   `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok): Automatically generate boilerplate code (getters, setters, constructors).
*   `@Id`: Marks the `id` field as the primary key.
*   `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Configures the database to automatically generate the `id` for new entities (e.g., using an auto-increment column).
*   `@Column(nullable = false)`: Ensures the `name` and `price` fields cannot be null in the database.

---

$$$$ src/main/java/com/example/restapi/dto
&&&& ProductRequestDTO.java
@@@@
package com.example.restapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating a product.
 * This object is used to receive data from incoming API requests.
 * It includes validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    /**
     * Name of the product. Must not be null or empty.
     * @NotBlank: Validates that the annotated string is not null and not empty or whitespace.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * Description of the product. Can be null or empty.
     */
    private String description;

    /**
     * Price of the product. Must not be null and must be greater than or equal to 0.0.
     * @NotNull: Validates that the annotated value is not null.
     * @DecimalMin: Validates that the annotated number has a value greater than or equal to the specified minimum.
     */
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product price must be non-negative")
    private Double price;
}
@@@@
**Explanation:**
This is a **Data Transfer Object (DTO)** specifically for *requests* to create or update a product.
*   **Purpose:** DTOs are used to transfer data between process layers (e.g., controller to service) and often differ from domain entities. They help decouple your API contract from your internal domain model.
*   **Validation:** It uses Bean Validation annotations (`@NotBlank`, `@NotNull`, `@DecimalMin`) to ensure that incoming data meets specific criteria before it's processed by the service layer. If validation fails, Spring automatically returns a `400 Bad Request` error.
*   Notice it does *not* include the `id` field, as the ID is typically generated by the database upon creation.

---

$$$$ src/main/java/com/example/restapi/dto
&&&& ProductResponseDTO.java
@@@@
package com.example.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for sending product data in API responses.
 * This object is used to format data before sending it back to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    /**
     * Unique identifier for the product.
     */
    private Long id;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Price of the product.
     */
    private Double price;
}
@@@@
**Explanation:**
This DTO is used to *send* product data back in API responses.
*   It includes the `id` field, which is generated and returned after a product is created.
*   It mirrors the `Product` entity but is still a separate DTO to maintain flexibility. For example, if you later wanted to add a `creationDate` to the response but not expose it in the request, or vice-versa.

---

$$$$ src/main/java/com/example/restapi/exception
&&&& ResourceNotFoundException.java
@@@@
package com.example.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a requested resource was not found.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND): This annotation causes Spring to respond
 * with an HTTP 404 (Not Found) status code when this exception is thrown
 * from a controller method.
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
This is a custom exception.
*   `@ResponseStatus(HttpStatus.NOT_FOUND)`: This is very convenient. When this exception is thrown from any Spring controller method, Spring will automatically set the HTTP response status code to `404 Not Found`. This avoids manual `try-catch` blocks and `ResponseEntity` manipulation for common error scenarios.

---

$$$$ src/main/java/com/example/restapi/repository
&&&& ProductRepository.java
@@@@
package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Spring Data JPA automatically provides the implementation for this interface at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom query methods here if needed,
    // e.g., List<Product> findByNameContainingIgnoreCase(String name);
}
@@@@
**Explanation:**
This is your **Repository** interface.
*   `@Repository`: A specialization of `@Component` that indicates that an annotated class is a "Repository". This makes it eligible for Spring's component scanning.
*   `extends JpaRepository<Product, Long>`: This is the core of Spring Data JPA. By extending `JpaRepository`, you automatically get common CRUD operations for your `Product` entity (e.g., `save`, `findById`, `findAll`, `deleteById`). The `Product` is the entity type, and `Long` is the type of its primary key.
*   You don't need to write any implementation code; Spring Data JPA generates it at runtime.

---

$$$$ src/main/java/com/example/restapi/service
&&&& ProductService.java
@@@@
package com.example.restapi.service;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Product-related business logic.
 * Handles operations like creating, retrieving, updating, and deleting products.
 *
 * @Service: Marks this class as a Spring Service component, making it eligible
 *           for Spring's component scanning and dependency injection.
 * @RequiredArgsConstructor: Lombok annotation that generates a constructor with
 *                           required arguments (final fields), enabling constructor injection.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository; // Injected via constructor by Lombok

    /**
     * Retrieves all products.
     * @return A list of ProductResponseDTOs.
     */
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO) // Map each Product entity to a ProductResponseDTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its ID.
     * @param id The ID of the product to retrieve.
     * @return The ProductResponseDTO if found.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     */
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    /**
     * Creates a new product.
     * @param productRequestDTO The DTO containing product creation data.
     * @return The ProductResponseDTO of the newly created product.
     */
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = mapToEntity(productRequestDTO); // Convert DTO to Entity
        Product savedProduct = productRepository.save(product); // Save to database
        return mapToDTO(savedProduct); // Convert saved Entity back to DTO for response
    }

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productRequestDTO The DTO containing updated product data.
     * @return The ProductResponseDTO of the updated product.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     */
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields from DTO to existing entity
        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(productRequestDTO.getPrice());

        Product updatedProduct = productRepository.save(existingProduct); // Save the updated entity
        return mapToDTO(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Helper method to convert Product entity to ProductResponseDTO.
     * @param product The Product entity.
     * @return The corresponding ProductResponseDTO.
     */
    private ProductResponseDTO mapToDTO(Product product) {
        return new ProductResponseDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }

    /**
     * Helper method to convert ProductRequestDTO to Product entity.
     * Note: ID is not set here as it's generated by the database.
     * @param productRequestDTO The ProductRequestDTO.
     * @return The corresponding Product entity.
     */
    private Product mapToEntity(ProductRequestDTO productRequestDTO) {
        return new Product(null, productRequestDTO.getName(), productRequestDTO.getDescription(), productRequestDTO.getPrice());
    }
}
@@@@
**Explanation:**
This is your **Service** layer. It contains the business logic.
*   `@Service`: Marks this class as a Spring Service. Services typically encapsulate business logic and act as an intermediary between controllers and repositories.
*   `@RequiredArgsConstructor` (Lombok): Generates a constructor that injects `ProductRepository` (because it's marked `final`). This is the recommended way for dependency injection in Spring.
*   **Methods:** Implement the core CRUD operations.
    *   `getAllProducts()`: Retrieves all products and maps them to `ProductResponseDTO`.
    *   `getProductById()`: Retrieves a product by ID, throwing a `ResourceNotFoundException` if not found.
    *   `createProduct()`: Takes a `ProductRequestDTO`, converts it to a `Product` entity, saves it, and then converts the saved entity back to a `ProductResponseDTO` for the response.
    *   `updateProduct()`: Finds an existing product, updates its fields from the `ProductRequestDTO`, saves the changes, and returns the updated DTO.
    *   `deleteProduct()`: Deletes a product by ID, after checking if it exists.
*   **DTO Mapping:** Includes private helper methods (`mapToDTO`, `mapToEntity`) to convert between `Product` entities and `ProductRequestDTO`/`ProductResponseDTO`. This is crucial for separating your domain model from your API contract.

---

$$$$ src/main/java/com/example/restapi/controller
&&&& ProductController.java
@@@@
package com.example.restapi.controller;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product-related API endpoints.
 * Handles HTTP requests and delegates business logic to the ProductService.
 *
 * @RestController: Combines @Controller and @ResponseBody, meaning every method
 *                  returns a domain object instead of a view, and the domain
 *                  object is automatically marshaled into JSON/XML.
 * @RequestMapping("/api/v1/products"): Base URL for all endpoints in this controller.
 * @RequiredArgsConstructor: Lombok annotation for constructor injection of ProductService.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService; // Injected via constructor

    /**
     * Handles GET requests to retrieve all products.
     * GET /api/v1/products
     * @return ResponseEntity with a list of ProductResponseDTOs and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK
    }

    /**
     * Handles GET requests to retrieve a product by ID.
     * GET /api/v1/products/{id}
     * @param id The ID of the product to retrieve.
     * @return ResponseEntity with the ProductResponseDTO and HTTP status 200 (OK).
     *         Returns 404 (Not Found) if the product does not exist (handled by ResourceNotFoundException).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product); // Returns 200 OK
    }

    /**
     * Handles POST requests to create a new product.
     * POST /api/v1/products
     * @param productRequestDTO The ProductRequestDTO containing data for the new product.
     *                          @Valid: Triggers validation on the incoming DTO.
     * @return ResponseEntity with the created ProductResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        // Using .created() to return 201 Created status and include Location header
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Handles PUT requests to update an existing product.
     * PUT /api/v1/products/{id}
     * @param id The ID of the product to update.
     * @param productRequestDTO The ProductRequestDTO containing updated data.
     *                          @Valid: Triggers validation on the incoming DTO.
     * @return ResponseEntity with the updated ProductResponseDTO and HTTP status 200 (OK).
     *         Returns 404 (Not Found) if the product does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(updatedProduct); // Returns 200 OK
    }

    /**
     * Handles DELETE requests to delete a product by ID.
     * DELETE /api/v1/products/{id}
     * @param id The ID of the product to delete.
     * @return ResponseEntity with no content and HTTP status 204 (No Content).
     *         Returns 404 (Not Found) if the product does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
@@@@
**Explanation:**
This is your **Controller** layer, the entry point for your REST API.
*   `@RestController`: Marks this class as a REST controller. It's a convenience annotation that combines `@Controller` and `@ResponseBody`.
*   `@RequestMapping("/api/v1/products")`: Sets the base path for all endpoints in this controller.
*   `@RequiredArgsConstructor` (Lombok): Injects the `ProductService`.
*   **Endpoint Methods:** Each method corresponds to an HTTP request type and path.
    *   `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Map HTTP methods to handler methods.
    *   `@PathVariable`: Extracts values from the URL path (e.g., `{id}`).
    *   `@RequestBody`: Binds the HTTP request body to a Java object (`ProductRequestDTO`).
    *   `@Valid`: Triggers the validation on the `ProductRequestDTO` using the annotations defined in the DTO.
    *   `ResponseEntity`: A flexible class for returning HTTP responses, allowing you to control the status code, headers, and body.
    *   `HttpStatus.CREATED` (201) for POST, `HttpStatus.NO_CONTENT` (204) for DELETE. Default is `HttpStatus.OK` (200).
*   **Error Handling:** The `ResourceNotFoundException` is automatically handled by Spring due to its `@ResponseStatus` annotation, leading to a `404 Not Found` response without explicit `try-catch` blocks here.
*   **Layered Architecture:** Notice how the controller only handles HTTP concerns (request mapping, input validation, response formatting) and delegates all business logic to the `ProductService`.

---

### Unit Test Files

Unit tests are crucial for ensuring the correctness of your code. Here are tests for the Service and Controller layers.

$$$$ src/test/java/com/example/restapi
&&&& RestapiApplicationTests.java
@@@@
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
@@@@
**Explanation:**
This is a standard Spring Boot test that simply verifies that your application context can load without errors. It's a quick way to catch basic configuration problems.

---

$$$$ src/test/java/com/example/restapi/service
&&&& ProductServiceTest.java
@@@@
package com.example.restapi.service;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
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
 * Unit tests for the ProductService class.
 * Uses Mockito to mock the ProductRepository dependency.
 *
 * @ExtendWith(MockitoExtension.class): Integrates Mockito with JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService instance
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequestDTO productRequestDTO;
    private ProductRequestDTO updatedProductRequestDTO;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop for work", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless gaming mouse", 50.00);

        productRequestDTO = new ProductRequestDTO("Keyboard", "Mechanical keyboard", 150.00);
        updatedProductRequestDTO = new ProductRequestDTO("Updated Laptop", "Ultra-light laptop", 1300.00);
    }

    @Test
    @DisplayName("Should retrieve all products")
    void shouldGetAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(1).getName()).isEqualTo("Mouse");
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @Test
    @DisplayName("Should retrieve product by ID")
    void shouldGetProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        ProductResponseDTO result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by ID not found")
    void shouldThrowExceptionWhenProductByIdNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create a new product")
    void shouldCreateProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 150.00);
        Product savedProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 150.00);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Keyboard");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateProduct() {
        // Given
        Product updatedEntity = new Product(1L, "Updated Laptop", "Ultra-light laptop", 1300.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedEntity);

        // When
        ProductResponseDTO result = productService.updateProduct(1L, updatedProductRequestDTO);

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
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updatedProductRequestDTO));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteProduct() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure deleteById was never called
    }
}
@@@@
**Explanation:**
These are unit tests for `ProductService` using **Mockito**.
*   `@ExtendWith(MockitoExtension.class)`: Integrates Mockito with JUnit 5.
*   `@Mock`: Creates a mock instance of `ProductRepository`. This means you're not actually talking to a real database; you're simulating its behavior.
*   `@InjectMocks`: Injects the mocked `ProductRepository` into the `ProductService` instance that you're testing.
*   `@BeforeEach`: Sets up common test data before each test method runs.
*   `when().thenReturn()`: This is Mockito's way of defining the behavior of your mock objects. You tell the mock what to return when a specific method is called with specific arguments.
*   `verify()`: Asserts that a certain method on a mock object was called (or not called) a specific number of times.
*   `assertThat()` (from AssertJ): Provides a fluent API for making assertions, making tests more readable.
*   `assertThrows()`: JUnit 5 assertion for testing if an expected exception is thrown.
*   **Goal:** These tests verify the business logic within `ProductService` in isolation, ensuring it correctly handles data, calls the repository, and throws exceptions when expected.

---

$$$$ src/test/java/com/example/restapi/controller
&&&& ProductControllerTest.java
@@@@
package com.example.restapi.controller;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.exception.ResourceNotFoundException;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for the ProductController.
 * Uses @WebMvcTest to test the controller layer in isolation, without starting a full server.
 *
 * @WebMvcTest(ProductController.class): Focuses on Spring MVC components,
 *                                       only scanning beans relevant to web layers.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired // Automatically injects MockMvc
    private MockMvc mockMvc;

    @Autowired // Automatically injects ObjectMapper for JSON conversion
    private ObjectMapper objectMapper;

    @MockBean // Mocks ProductService, preventing it from interacting with other layers
    private ProductService productService;

    private ProductResponseDTO productResponseDTO1;
    private ProductResponseDTO productResponseDTO2;
    private ProductRequestDTO productRequestDTO;
    private ProductRequestDTO invalidProductRequestDTO;


    @BeforeEach
    void setUp() {
        productResponseDTO1 = new ProductResponseDTO(1L, "Laptop", "Powerful laptop", 1200.00);
        productResponseDTO2 = new ProductResponseDTO(2L, "Mouse", "Wireless mouse", 50.00);
        productRequestDTO = new ProductRequestDTO("Keyboard", "Mechanical keyboard", 150.00);
        invalidProductRequestDTO = new ProductRequestDTO("", "Description", -10.00); // Invalid data for testing
    }

    @Test
    @DisplayName("Should return list of products for GET /api/v1/products")
    void shouldReturnListOfProducts() throws Exception {
        // Given
        List<ProductResponseDTO> allProducts = Arrays.asList(productResponseDTO1, productResponseDTO2);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()", is(allProducts.size()))) // Check list size
                .andExpect(jsonPath("$[0].name", is(productResponseDTO1.getName())))
                .andExpect(jsonPath("$[1].name", is(productResponseDTO2.getName())));

        verify(productService, times(1)).getAllProducts(); // Verify service method was called
    }

    @Test
    @DisplayName("Should return product by ID for GET /api/v1/products/{id}")
    void shouldReturnProductById() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponseDTO1);

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(productResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productResponseDTO1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Should return 404 for GET /api/v1/products/{id} when product not found")
    void shouldReturnNotFoundWhenProductByIdNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("Should create a new product for POST /api/v1/products")
    void shouldCreateNewProduct() throws Exception {
        // Given
        ProductResponseDTO createdProduct = new ProductResponseDTO(3L, productRequestDTO.getName(), productRequestDTO.getDescription(), productRequestDTO.getPrice());
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(createdProduct);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO))) // Convert DTO to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(createdProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdProduct.getName())));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for POST /api/v1/products with invalid data")
    void shouldReturnBadRequestWhenCreatingProductWithInvalidData() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequestDTO)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name", is("Product name is required"))) // Check validation error message
                .andExpect(jsonPath("$.price", is("Product price must be non-negative")));

        verify(productService, never()).createProduct(any(ProductRequestDTO.class)); // Service should not be called
    }

    @Test
    @DisplayName("Should update an existing product for PUT /api/v1/products/{id}")
    void shouldUpdateExistingProduct() throws Exception {
        // Given
        ProductRequestDTO updateRequest = new ProductRequestDTO("Updated Laptop", "New description", 1300.00);
        ProductResponseDTO updatedProduct = new ProductResponseDTO(1L, "Updated Laptop", "New description", 1300.00);

        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(updatedProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())));

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 404 for PUT /api/v1/products/{id} when updating non-existent product")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
        // Given
        ProductRequestDTO updateRequest = new ProductRequestDTO("Updated Laptop", "New description", 1300.00);
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).updateProduct(anyLong(), any(ProductRequestDTO.class));

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 204 No Content for DELETE /api/v1/products/{id}")
    void shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should return 404 for DELETE /api/v1/products/{id} when deleting non-existent product")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation:**
These are tests for `ProductController` using **`@WebMvcTest`**.
*   `@WebMvcTest(ProductController.class)`: This annotation is specialized for testing Spring MVC controllers. It auto-configures Spring MVC infrastructure but only scans the specified controller and its direct dependencies, *not* the full application context. This makes tests faster and more isolated.
*   `@Autowired MockMvc`: `MockMvc` allows you to perform HTTP requests to your controller in a test environment without actually starting a server.
*   `@Autowired ObjectMapper`: Used to convert Java objects to JSON strings (for request bodies) and vice-versa.
*   `@MockBean ProductService`: Instead of the real `ProductService`, a mock version is injected. This isolates the controller from the service layer, allowing you to focus solely on testing the controller's behavior (e.g., correct HTTP status, request/response mapping, validation handling).
*   `mockMvc.perform()`: Simulates an HTTP request.
*   `get()`, `post()`, `put()`, `delete()`: HTTP methods.
*   `.contentType()`, `.content()`: Specify request headers and body.
*   `.andExpect(status().isOk())`, `.andExpect(jsonPath("$.name", is("Laptop")))`: Assertions on the HTTP response status and JSON body content.
*   `verify()`: Verifies that the mocked service methods were called as expected.
*   **Goal:** These tests ensure that your controller correctly handles HTTP requests, maps URLs to methods, processes request bodies, applies validation, and returns appropriate HTTP status codes and response bodies.

---