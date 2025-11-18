Hello! I'd be happy to provide a complete code template for a Spring Boot REST API, including a typical project structure, essential components, and unit tests.

This template will demonstrate a simple REST API for managing "Products" with standard CRUD (Create, Read, Update, Delete) operations. It uses an in-memory H2 database for easy setup and testing.

---

### **Project Overview**

The template follows common Spring Boot practices:

*   **Layered Architecture**: Controller, Service, Repository.
*   **Data Transfer Objects (DTOs)**: Separate request and response models from domain entities.
*   **Exception Handling**: Custom exception and global exception handler.
*   **JPA/Hibernate**: For database interaction.
*   **H2 Database**: An in-memory database for convenience in development and testing.
*   **Unit Tests**: For both the service and controller layers.

---

### **Project Structure and Code**

`$$$$ `
This represents the root directory of your Maven project.

`$$$$ pom.xml`
`&&&& pom.xml`
@@@@
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Use the latest stable version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot REST API</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter (includes Tomcat, Spring MVC) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Data JPA Starter (includes Hibernate) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database (in-memory database for development/testing) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok (for reducing boilerplate code like getters/setters/constructors) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Starter Test (for unit and integration testing) -->
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
**Explanation**:
This `pom.xml` defines your project's dependencies:
*   `spring-boot-starter-web`: Essential for building RESTful APIs.
*   `spring-boot-starter-data-jpa`: Provides Spring Data JPA and Hibernate for database interaction.
*   `h2`: An in-memory database, perfect for local development and unit testing.
*   `lombok`: A utility to reduce boilerplate code (getters, setters, constructors).
*   `spring-boot-starter-test`: Comprehensive testing utilities, including JUnit, Mockito, and Spring Test.

---

`$$$$ src/main/java/com/example/demo`
`&&&& DemoApplication.java`
@@@@
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
@@@@
**Explanation**:
This is the main entry point for your Spring Boot application. The `@SpringBootApplication` annotation combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`, making it a convenient way to bootstrap a Spring application.

---

`$$$$ src/main/java/com/example/demo/model`
`&&&& Product.java`
@@@@
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 */
@Entity
@Table(name = "products") // Specifies the table name for this entity
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
@Builder // Lombok: Provides a builder pattern for creating instances
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates ID for new entities
    private Long id;
    private String name;
    private String description;
    private double price;

    // Note: Lombok handles boilerplate code, so explicit getters/setters/constructors are not needed.
}
@@@@
**Explanation**:
This is your JPA entity.
*   `@Entity`: Marks this class as a JPA entity, meaning it maps to a database table.
*   `@Table(name = "products")`: Specifies the name of the database table.
*   `@Id`: Marks the `id` field as the primary key.
*   `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Configures the ID to be auto-generated by the database.
*   `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`: Lombok annotations that automatically generate boilerplate code like getters, setters, constructors, `toString()`, `equals()`, and `hashCode()`, keeping your code clean.

---

`$$$$ src/main/java/com/example/demo/dto`
`&&&& ProductRequestDTO.java`
@@@@
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a Product.
 * Used as input for REST API endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Product name cannot be blank") // Validation: field must not be null and must contain at least one non-whitespace character.
    private String name;

    private String description; // Description can be null or empty

    @NotNull(message = "Product price cannot be null") // Validation: field must not be null
    @Positive(message = "Product price must be positive") // Validation: field value must be greater than zero
    private Double price;
}
@@@@
**Explanation**:
*   **Data Transfer Object (DTO)**: This class is used for incoming data (e.g., when creating or updating a product). It decouples the API input structure from the internal `Product` entity.
*   **Validation Annotations**: `@NotBlank`, `@NotNull`, `@Positive` are Jakarta Bean Validation annotations. Spring Boot automatically integrates with these to validate incoming request bodies.

---

`$$$$ src/main/java/com/example/demo/dto`
`&&&& ProductResponseDTO.java`
@@@@
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning Product details.
 * Used as output for REST API endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
}
@@@@
**Explanation**:
*   **Data Transfer Object (DTO)**: This class is used for outgoing data (e.g., when fetching product details). It ensures that only necessary information is exposed via the API and can hide internal entity details.

---

`$$$$ src/main/java/com/example/demo/exception`
`&&&& ResourceNotFoundException.java`
@@@@
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to be thrown when a requested resource is not found.
 * This exception will result in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Maps this exception to an HTTP 404 status
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
@@@@
**Explanation**:
*   **Custom Exception**: This `ResourceNotFoundException` is a specific exception for when a requested resource doesn't exist.
*   `@ResponseStatus(HttpStatus.NOT_FOUND)`: This annotation tells Spring to automatically return an HTTP 404 (Not Found) status code when this exception is thrown from a controller method.

---

`$$$$ src/main/java/com/example/demo/exception`
`&&&& GlobalExceptionHandler.java`
@@@@
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Catches specific exceptions and returns appropriate HTTP responses.
 */
@ControllerAdvice // Enables this class to handle exceptions across the entire application
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException, returning a 404 Not Found status.
     *
     * @param ex The ResourceNotFoundException instance.
     * @param request The WebRequest.
     * @return A ResponseEntity containing error details.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND.toString()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentNotValidException (thrown during DTO validation),
     * returning a 400 Bad Request status with validation errors.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return A ResponseEntity containing validation error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unhandled exceptions, returning a 500 Internal Server Error status.
     *
     * @param ex The generic Exception instance.
     * @param request The WebRequest.
     * @return A ResponseEntity containing error details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.toString()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Helper class to structure error responses consistently.
 */
record ErrorDetails(LocalDateTime timestamp, String message, String details, String errorCode) {}
@@@@
**Explanation**:
*   `@ControllerAdvice`: This annotation allows this class to provide centralized exception handling across all `@RequestMapping` methods.
*   `@ExceptionHandler`: Methods annotated with this will handle specific exceptions.
    *   `handleResourceNotFoundException`: Catches `ResourceNotFoundException` and returns a structured 404 response.
    *   `handleValidationExceptions`: Catches `MethodArgumentNotValidException` (thrown when DTO validation fails) and returns a 400 response with details about which fields failed validation.
    *   `handleGlobalException`: A catch-all for any other unhandled exceptions, returning a 500 Internal Server Error.
*   `ErrorDetails` record: A simple immutable class (using Java 17 records) to provide a consistent structure for error responses.

---

`$$$$ src/main/java/com/example/demo/repository`
`&&&& ProductRepository.java`
@@@@
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Product entities.
 * Provides standard CRUD operations and custom query capabilities.
 */
@Repository // Indicates that this is a DAO component
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.
    // You can add custom query methods here if needed, e.g.,
    // List<Product> findByNameContainingIgnoreCase(String name);
}
@@@@
**Explanation**:
*   `@Repository`: Indicates that this interface is a repository, a mechanism for encapsulating storage, retrieval, and search behavior.
*   `JpaRepository<Product, Long>`: This interface from Spring Data JPA provides out-of-the-box CRUD operations for the `Product` entity, where `Long` is the type of the primary key. You don't need to write any implementation for basic CRUD.

---

`$$$$ src/main/java/com/example/demo/service`
`&&&& ProductService.java`
@@@@
package com.example.demo.service;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;

import java.util.List;

/**
 * Interface for the Product service layer.
 * Defines the business operations related to Product management.
 */
public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
}
@@@@
**Explanation**:
*   **Service Interface**: Defines the contract for the business logic related to products. This promotes loose coupling and makes it easier to swap out implementations if needed. It also helps in unit testing by allowing mocking of this interface.

---

`$$$$ src/main/java/com/example/demo/service`
`&&&& ProductServiceImpl.java`
@@@@
package com.example.demo.service;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService interface.
 * Contains the business logic for managing products.
 */
@Service // Indicates that this is a service component
@RequiredArgsConstructor // Lombok: Generates a constructor with required arguments (final fields)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository; // Injects the ProductRepository

    /**
     * Creates a new product based on the provided DTO.
     *
     * @param productRequestDTO The DTO containing product creation details.
     * @return The DTO of the newly created product.
     */
    @Override
    @Transactional // Ensures the method runs within a database transaction
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .build();
        Product savedProduct = productRepository.save(product);
        return mapToProductResponseDTO(savedProduct);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The DTO of the found product.
     * @throws ResourceNotFoundException if the product with the given ID does not exist.
     */
    @Override
    @Transactional(readOnly = true) // Optimizes transaction for read-only operations
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponseDTO(product);
    }

    /**
     * Retrieves all products.
     *
     * @return A list of DTOs for all products.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id The ID of the product to update.
     * @param productRequestDTO The DTO containing updated product details.
     * @return The DTO of the updated product.
     * @throws ResourceNotFoundException if the product with the given ID does not exist.
     */
    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(productRequestDTO.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponseDTO(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException if the product with the given ID does not exist.
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Helper method to convert a Product entity to a ProductResponseDTO.
     *
     * @param product The Product entity.
     * @return The corresponding ProductResponseDTO.
     */
    private ProductResponseDTO mapToProductResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
@@@@
**Explanation**:
*   `@Service`: Marks this class as a service component in the Spring application context.
*   `@RequiredArgsConstructor`: Lombok annotation that generates a constructor for any `final` fields, handling dependency injection.
*   `private final ProductRepository productRepository`: The `ProductRepository` is injected here to interact with the database.
*   `@Transactional`: Ensures that methods are executed within a database transaction. If an exception occurs, the transaction will be rolled back. `readOnly = true` is an optimization for read operations.
*   **Business Logic**: This class contains the actual logic for CRUD operations, including mapping between DTOs and entities, and handling `ResourceNotFoundException`.
*   `mapToProductResponseDTO`: A private helper method to convert a `Product` entity into a `ProductResponseDTO`. This is a good practice to encapsulate mapping logic.

---

`$$$$ src/main/java/com/example/demo/controller`
`&&&& ProductController.java`
@@@@
package com.example.demo.controller;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing Product resources.
 * Handles HTTP requests and delegates to the ProductService for business logic.
 */
@RestController // Marks this class as a REST Controller
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
@RequiredArgsConstructor // Lombok: Generates a constructor with required arguments (final fields)
public class ProductController {

    private final ProductService productService; // Injects the ProductService

    /**
     * Creates a new product.
     * POST /api/products
     *
     * @param productRequestDTO The product details from the request body.
     * @return ResponseEntity containing the created product and HTTP status 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Specifies the HTTP status for successful creation
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO);
    }

    /**
     * Retrieves a product by its ID.
     * GET /api/products/{id}
     *
     * @param id The ID of the product.
     * @return ResponseEntity containing the product and HTTP status 200 OK.
     */
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * Retrieves all products.
     * GET /api/products
     *
     * @return ResponseEntity containing a list of all products and HTTP status 200 OK.
     */
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Updates an existing product.
     * PUT /api/products/{id}
     *
     * @param id The ID of the product to update.
     * @param productRequestDTO The updated product details from the request body.
     * @return ResponseEntity containing the updated product and HTTP status 200 OK.
     */
    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.updateProduct(id, productRequestDTO);
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/products/{id}
     *
     * @param id The ID of the product to delete.
     * @return ResponseEntity with no content and HTTP status 204 No Content.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Specifies the HTTP status for successful deletion
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
@@@@
**Explanation**:
*   `@RestController`: Combines `@Controller` and `@ResponseBody`, meaning every method's return value is directly bound to the web response body.
*   `@RequestMapping("/api/products")`: Sets the base path for all endpoints in this controller.
*   `@RequiredArgsConstructor`: Injects `ProductService`.
*   `@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`: Map HTTP methods to specific controller methods.
*   `@PathVariable`: Binds a URI template variable (e.g., `{id}`) to a method parameter.
*   `@RequestBody`: Binds the HTTP request body to a method parameter.
*   `@Valid`: Triggers validation on the `ProductRequestDTO` using the annotations defined in the DTO (e.g., `@NotBlank`, `@NotNull`).
*   `@ResponseStatus`: Sets the HTTP status code for successful responses (e.g., 201 Created for POST, 204 No Content for DELETE). For GET/PUT, Spring typically defaults to 200 OK.

---

`$$$$ src/main/resources`
`&&&& application.properties`
@@@@
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# JPA and Hibernate configuration
# ddl-auto: create-drop means Hibernate will create the schema on startup and drop it on shutdown.
#            This is good for development and testing with an in-memory database.
#            For production, use 'none' or 'update' with caution, or a Flyway/Liquibase tool.
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server port
server.port=8080
@@@@
**Explanation**:
*   **H2 Database**: Configures an in-memory H2 database. `DB_CLOSE_DELAY=-1` keeps the database alive as long as the JVM is running, and `DB_CLOSE_ON_EXIT=FALSE` prevents it from closing when the last connection is closed.
*   **JPA/Hibernate**:
    *   `spring.jpa.hibernate.ddl-auto=update`: Hibernate will update the database schema based on your entities when the application starts. For production, consider `none` and use migration tools like Flyway or Liquibase. `create-drop` is also common for testing to start with a fresh schema every time.
    *   `spring.jpa.show-sql=true` and `spring.jpa.properties.hibernate.format_sql=true`: Log SQL queries generated by Hibernate, useful for debugging.
*   **Server Port**: Sets the port on which the Spring Boot application will run.

---

### **Unit Test Cases**

`$$$$ src/test/java/com/example/demo/service`
`&&&& ProductServiceTest.java`
@@@@
package com.example.demo.service;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
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
 * Unit tests for the ProductServiceImpl class.
 * Uses Mockito to mock the ProductRepository dependency.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
public class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductServiceImpl
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach // Runs before each test method
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Powerful computing device")
                .price(1200.00)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Mouse")
                .description("Wireless mouse")
                .price(25.00)
                .build();

        productRequestDTO = ProductRequestDTO.builder()
                .name("New Product")
                .description("A brand new item")
                .price(99.99)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Laptop")
                .description("Powerful computing device")
                .price(1200.00)
                .build();
    }

    @DisplayName("Test createProduct - success")
    @Test
    void testCreateProduct_Success() {
        // Given
        Product newProduct = Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .build();
        Product savedProduct = Product.builder()
                .id(3L) // Assuming ID is generated upon saving
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Product");
        verify(productRepository, times(1)).save(any(Product.class)); // Verify save was called once
    }

    @DisplayName("Test getProductById - success")
    @Test
    void testGetProductById_Success() {
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

    @DisplayName("Test getProductById - not found")
    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @DisplayName("Test getAllProducts - success")
    @Test
    void testGetAllProducts_Success() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(1).getName()).isEqualTo("Mouse");
        verify(productRepository, times(1)).findAll();
    }

    @DisplayName("Test updateProduct - success")
    @Test
    void testUpdateProduct_Success() {
        // Given
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Laptop")
                .description("Updated description")
                .price(1300.00)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(1300.00)
                .build();
        ProductResponseDTO result = productService.updateProduct(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getPrice()).isEqualTo(1300.00);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("Test updateProduct - not found")
    @Test
    void testUpdateProduct_NotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Non Existent")
                .price(10.0)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updateRequest));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @DisplayName("Test deleteProduct - success")
    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L); // Mock void method

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @DisplayName("Test deleteProduct - not found")
    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure delete was not called
    }
}
@@@@
**Explanation**:
*   `@ExtendWith(MockitoExtension.class)`: Integrates Mockito with JUnit 5.
*   `@Mock`: Creates a mock instance of `ProductRepository`. When `productService` calls methods on `productRepository`, it will interact with this mock instead of a real database.
*   `@InjectMocks`: Injects the mock `productRepository` into the `productService` instance under test.
*   `@BeforeEach`: Sets up common objects before each test.
*   **`when().thenReturn()`**: Mockito syntax to define the behavior of mocked methods.
*   **`verify()`**: Mockito syntax to assert that certain methods on the mock were called (or not called) a specific number of times.
*   `assertThrows()`: JUnit 5 assertion to check if a specific exception is thrown.
*   This test class thoroughly covers the `ProductService`'s CRUD operations, including success and "not found" scenarios.

---

`$$$$ src/test/java/com/example/demo/controller`
`&&&& ProductControllerTest.java`
@@@@
package com.example.demo.controller;

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProductService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController class.
 * Uses @WebMvcTest to focus on Spring MVC components and MockMvc for simulating HTTP requests.
 */
@WebMvcTest(ProductController.class) // Configures Spring context for testing only ProductController
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON and vice versa

    @MockBean // Creates a mock instance of ProductService and adds it to the Spring context
    private ProductService productService;

    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO1;
    private ProductResponseDTO productResponseDTO2;

    @BeforeEach // Runs before each test method
    void setUp() {
        productRequestDTO = ProductRequestDTO.builder()
                .name("Test Product")
                .description("A product for testing")
                .price(100.00)
                .build();

        productResponseDTO1 = ProductResponseDTO.builder()
                .id(1L)
                .name("Test Product 1")
                .description("Description 1")
                .price(10.00)
                .build();

        productResponseDTO2 = ProductResponseDTO.builder()
                .id(2L)
                .name("Test Product 2")
                .description("Description 2")
                .price(20.00)
                .build();
    }

    @DisplayName("Test createProduct - success")
    @Test
    void testCreateProduct_Success() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponseDTO1);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO))) // Convert DTO to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(productResponseDTO1.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDTO1.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDTO1.getPrice()));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @DisplayName("Test createProduct - validation failure (blank name)")
    @Test
    void testCreateProduct_ValidationFailure_BlankName() throws Exception {
        // Given
        ProductRequestDTO invalidDto = ProductRequestDTO.builder()
                .name(" ") // Blank name
                .description("Invalid product")
                .price(50.00)
                .build();

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name").value("Product name cannot be blank")); // Check for validation error message

        verify(productService, never()).createProduct(any(ProductRequestDTO.class));
    }

    @DisplayName("Test getProductById - success")
    @Test
    void testGetProductById_Success() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponseDTO1);

        // When & Then
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(productResponseDTO1.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDTO1.getName()));

        verify(productService, times(1)).getProductById(1L);
    }

    @DisplayName("Test getProductById - not found")
    @Test
    void testGetProductById_NotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService, times(1)).getProductById(99L);
    }

    @DisplayName("Test getAllProducts - success")
    @Test
    void testGetAllProducts_Success() throws Exception {
        // Given
        List<ProductResponseDTO> products = Arrays.asList(productResponseDTO1, productResponseDTO2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(productResponseDTO1.getName()));

        verify(productService, times(1)).getAllProducts();
    }

    @DisplayName("Test updateProduct - success")
    @Test
    void testUpdateProduct_Success() throws Exception {
        // Given
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(150.00)
                .build();
        ProductResponseDTO updatedResponse = ProductResponseDTO.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated Description")
                .price(150.00)
                .build();

        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(updatedResponse.getId()))
                .andExpect(jsonPath("$.name").value(updatedResponse.getName()))
                .andExpect(jsonPath("$.price").value(updatedResponse.getPrice()));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
    }

    @DisplayName("Test updateProduct - not found")
    @Test
    void testUpdateProduct_NotFound() throws Exception {
        // Given
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Non Existent")
                .price(10.0)
                .build();
        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // When & Then
        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).updateProduct(eq(99L), any(ProductRequestDTO.class));
    }

    @DisplayName("Test deleteProduct - success")
    @Test
    void testDeleteProduct_Success() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L); // Mock void method

        // When & Then
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @DisplayName("Test deleteProduct - not found")
    @Test
    void testDeleteProduct_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation**:
*   `@WebMvcTest(ProductController.class)`: This annotation configures a Spring application context containing only the beans relevant for MVC tests (e.g., `ProductController`, `@ControllerAdvice`, and some Spring infrastructure beans). It's faster than `@SpringBootTest`.
*   `@Autowired MockMvc mockMvc`: `MockMvc` allows you to send simulated HTTP requests to your controller and assert on the results (status, headers, content).
*   `@Autowired ObjectMapper objectMapper`: Used to convert Java objects to JSON strings and vice-versa, which is crucial for sending and receiving JSON in REST APIs.
*   `@MockBean ProductService productService`: A mock of the `ProductService` is created and injected into `ProductController`. This ensures that the controller's dependencies are mocked, allowing you to test the controller in isolation.
*   **`mockMvc.perform()`**: Starts the request building process.
    *   `post("/api/products")`, `get("/api/products/1")`, etc.: Specifies the HTTP method and URL.
    *   `contentType(MediaType.APPLICATION_JSON)`: Sets the `Content-Type` header.
    *   `content(objectMapper.writeValueAsString(dto))`: Sets the request body.
*   **`.andExpect()`**: Used to assert on the response.
    *   `status().isCreated()`, `status().isOk()`, `status().isNotFound()`: Assert HTTP status codes.
    *   `jsonPath("$.id").value(value)`: Asserts values in the JSON response body using JsonPath expressions.
*   **`verify()`**: Verifies that `productService` methods were called as expected.
*   These tests cover various scenarios for the controller, including successful calls, validation failures, and handling of `ResourceNotFoundException`.

---

This template provides a solid foundation for building Spring Boot REST APIs, demonstrating best practices for structure, data handling, error management, and testing. You can expand upon this by adding more complex business logic, security, logging, and database migration tools as your project grows.