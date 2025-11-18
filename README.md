Hello! I'd be happy to help you generate a code template for a Spring Boot REST API. This template will provide a foundational structure for a typical RESTful service, including entities, repositories, services, controllers, DTOs, and unit tests, along with basic error handling and configuration.

We'll create a simple API for managing "Products."

Here's the complete code template:

#### **Project Configuration and Root Files**

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
        <version>3.2.5</version> <!-- Use a recent stable Spring Boot version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>product-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>product-api</name>
    <description>Demo project for Spring Boot REST API</description>

    <properties>
        <java.version>17</java.version> <!-- Use Java 17 or higher -->
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
            <artifactId>spring-boot-starter-actuator</artifactId> <!-- For monitoring, health checks -->
        </dependency>

        <!-- Database Driver (H2 for in-memory, simple development) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- You can replace H2 with other databases like PostgreSQL:
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        -->

        <!-- Lombok (Optional, for boilerplate reduction) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test Starter -->
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
*   **`pom.xml`**: This is the Maven project configuration file.
    *   It defines the project's parent (Spring Boot starter parent for dependency management).
    *   Includes essential starters: `web` (for REST APIs), `data-jpa` (for database interaction), `validation` (for request body validation), `actuator` (for production-ready features like health checks).
    *   Adds `h2` as an in-memory database for easy development and testing. You can swap this with any other JDBC-compliant database driver.
    *   Includes `lombok` to reduce boilerplate code (e.g., getters, setters, constructors).
    *   Includes `spring-boot-starter-test` for comprehensive unit and integration testing.

$$$$
&&&& application.properties
@@@@
# Server configuration
server.port=8080

# H2 Database configuration (in-memory)
spring.datasource.url=jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA configuration
spring.jpa.hibernate.ddl-auto=update # 'update' creates/updates schema based on entities. Use 'create' to drop and recreate on startup. 'none' for production.
spring.jpa.show-sql=true           # Log SQL statements
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true     # Enable H2 console for browser access
spring.h2.console.path=/h2-console # Path to H2 console

# Enable Actuator endpoints
management.endpoints.web.exposure.include=* # Expose all actuator endpoints
@@@@
**Explanation:**
*   **`application.properties`**: This file contains application-specific configurations.
    *   **`server.port`**: Sets the port the application will run on.
    *   **H2 Database**: Configures the in-memory H2 database. `DB_CLOSE_DELAY=-1` keeps the database alive as long as the JVM is running. `spring.h2.console.enabled` allows accessing the H2 web console at `/h2-console`.
    *   **JPA/Hibernate**:
        *   `ddl-auto=update`: Hibernate will update the database schema based on your JPA entities. For production, consider `none` and manage schema migrations with tools like Flyway or Liquibase.
        *   `show-sql=true`: Logs the SQL queries executed by Hibernate, useful for debugging.
    *   **Actuator**: Exposes all Actuator endpoints, providing useful operational insights.

#### **Main Application Class**

$$$$ src/main/java/com/example/productapi
&&&& ProductApiApplication.java
@@@@
package com.example.productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class ProductApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiApplication.class, args);
    }

}
@@@@
**Explanation:**
*   **`ProductApiApplication.java`**: The entry point of your Spring Boot application.
    *   `@SpringBootApplication`: A convenient annotation that bundles `@Configuration` (defines beans), `@EnableAutoConfiguration` (Spring Boot's magic for auto-configuring based on classpath), and `@ComponentScan` (scans the current package and sub-packages for Spring components).
    *   `SpringApplication.run()`: Boots up the Spring application context.

#### **Entity Layer**

$$$$ src/main/java/com/example/productapi/entity
&&&& Product.java
@@@@
package com.example.productapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity // Marks this class as a JPA entity
@Table(name = "products") // Specifies the table name in the database
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates an all-argument constructor
public class Product {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates ID for new entities (IDENTITY for H2, MySQL, PostgreSQL)
    private Long id;

    @Column(nullable = false) // Specifies column properties: cannot be null
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;
}
@@@@
**Explanation:**
*   **`Product.java`**: Represents the `Product` entity that maps to a database table.
    *   `@Entity`: Marks the class as a JPA entity.
    *   `@Table`: Defines the database table name.
    *   `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`: Lombok annotations to automatically generate boilerplate code (getters, setters, constructors) reducing verbosity.
    *   `@Id`: Designates the primary key.
    *   `@GeneratedValue`: Configures how the primary key is generated (e.g., `IDENTITY` for auto-incrementing in many databases).
    *   `@Column`: Customizes column properties like `nullable` or `length`.

#### **Repository Layer**

$$$$ src/main/java/com/example/productapi/repository
&&&& ProductRepository.java
@@@@
package com.example.productapi.repository;

import com.example.productapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Not strictly necessary, but good practice to explicitly mark
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides CRUD operations for Product entity with Long as ID type

    // You can define custom query methods here, Spring Data JPA will implement them
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}
@@@@
**Explanation:**
*   **`ProductRepository.java`**: This is the Data Access Layer (DAL) interface.
    *   `JpaRepository<Product, Long>`: Extends Spring Data JPA's `JpaRepository` interface. This provides out-of-the-box CRUD (Create, Read, Update, Delete) methods for the `Product` entity with `Long` as its primary key type.
    *   Spring Data JPA automatically implements this interface at runtime.
    *   You can define custom query methods (e.g., `findByNameContainingIgnoreCase`) following specific naming conventions, and Spring Data JPA will generate the appropriate SQL query.

#### **DTO (Data Transfer Object) Layer**

$$$$ src/main/java/com/example/productapi/dto
&&&& ProductRequest.java
@@@@
package com.example.productapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
public class ProductRequest {

    @NotBlank(message = "Product name cannot be empty") // Validation: field must not be null or empty string
    private String name;

    private String description;

    @NotNull(message = "Product price cannot be null") // Validation: field must not be null
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0") // Validation: min value for decimal
    private BigDecimal price;

    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Product quantity cannot be negative") // Validation: min value for integer
    private Integer quantity;
}
@@@@

$$$$ src/main/java/com/example/productapi/dto
&&&& ProductResponse.java
@@@@
package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates an all-argument constructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
@@@@
**Explanation:**
*   **`ProductRequest.java`**: Used for incoming requests (e.g., `POST`, `PUT`).
    *   Includes validation annotations (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@Min`) from `jakarta.validation` to ensure data integrity before processing.
*   **`ProductResponse.java`**: Used for outgoing responses (e.g., `GET`, `POST` return).
    *   DTOs help decouple the API contract from the internal entity structure, allowing you to change your entity without immediately breaking clients, and to only expose necessary data.

#### **Service Layer**

$$$$ src/main/java/com/example/productapi/service
&&&& ProductService.java
@@@@
package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring Service component
@RequiredArgsConstructor // Lombok: Generates a constructor with all final fields
public class ProductService {

    private final ProductRepository productRepository; // Injected by Lombok's @RequiredArgsConstructor

    @Transactional // Ensures the entire method runs within a transaction
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Transactional(readOnly = true) // Read-only transaction for better performance
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Helper method to convert Entity to DTO
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
@@@@
**Explanation:**
*   **`ProductService.java`**: Contains the business logic for `Product` operations.
    *   `@Service`: Marks this class as a Spring service component, making it eligible for component scanning.
    *   `@RequiredArgsConstructor`: Lombok annotation to automatically inject `productRepository` via constructor.
    *   `@Transactional`: Ensures that methods are executed within a database transaction. If an exception occurs, the transaction will be rolled back. `readOnly = true` is an optimization for read operations.
    *   Handles `ResourceNotFoundException` when a product is not found, ensuring consistent error responses.
    *   Uses a helper `mapToProductResponse` method to convert `Product` entities to `ProductResponse` DTOs, promoting separation of concerns.

#### **Exception Handling Layer**

$$$$ src/main/java/com/example/productapi/exception
&&&& ResourceNotFoundException.java
@@@@
package com.example.productapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Sets the HTTP status code for this exception
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
@@@@

$$$$ src/main/java/com/example/productapi/exception
&&&& GlobalExceptionHandler.java
@@@@
package com.example.productapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Combines @ControllerAdvice and @ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false), // Get URL
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorDetailsWithErrors errorDetails = new ErrorDetailsWithErrors(
                LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper classes for consistent error response structure
    private record ErrorDetails(LocalDateTime timestamp, String message, String details, int status) {}
    private record ErrorDetailsWithErrors(LocalDateTime timestamp, String message, String details, int status, Map<String, String> errors) {}
}
@@@@
**Explanation:**
*   **`ResourceNotFoundException.java`**: A custom exception for when a requested resource is not found.
    *   `@ResponseStatus(HttpStatus.NOT_FOUND)`: Automatically sets the HTTP response status code to 404 when this exception is thrown.
*   **`GlobalExceptionHandler.java`**: A centralized place to handle exceptions across all controllers.
    *   `@RestControllerAdvice`: Enables this class to provide global exception handling for `RestController`s.
    *   `@ExceptionHandler`: Marks methods to handle specific exception types.
        *   `handleResourceNotFoundException`: Catches `ResourceNotFoundException` and returns a `404 Not Found` response with a custom error structure.
        *   `handleMethodArgumentNotValid`: Catches `MethodArgumentNotValidException` (thrown when `@Valid` fails) and returns a `400 Bad Request` with details about validation errors.
        *   `handleGlobalException`: A catch-all for any other `Exception`, returning a `500 Internal Server Error`.
    *   Uses record classes (`ErrorDetails`, `ErrorDetailsWithErrors`) for a clean and consistent JSON error response format.

#### **Controller Layer**

$$$$ src/main/java/com/example/productapi/controller
&&&& ProductController.java
@@@@
package com.example.productapi.controller;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/v1/products") // Base path for all endpoints in this controller
@RequiredArgsConstructor // Lombok: Generates a constructor with all final fields
public class ProductController {

    private final ProductService productService; // Injected by Lombok's @RequiredArgsConstructor

    // POST /api/v1/products - Create a new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        // @Valid triggers validation defined in ProductRequest DTO
        // @RequestBody maps the incoming JSON to ProductRequest object
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created
    }

    // GET /api/v1/products - Get all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK
    }

    // GET /api/v1/products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        // @PathVariable extracts the ID from the URL path
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product); // Returns 200 OK
    }

    // PUT /api/v1/products/{id} - Update product by ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct); // Returns 200 OK
    }

    // DELETE /api/v1/products/{id} - Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
@@@@
**Explanation:**
*   **`ProductController.java`**: The entry point for client requests.
    *   `@RestController`: Combines `@Controller` and `@ResponseBody`, meaning methods return data directly as JSON/XML.
    *   `@RequestMapping("/api/v1/products")`: Sets the base path for all endpoints in this controller. Using `/api/v1` is a common practice for API versioning.
    *   `@RequiredArgsConstructor`: Injects `productService`.
    *   **HTTP Methods (`@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`):** Map incoming HTTP requests to specific methods.
    *   `@Valid`: Triggers the validation defined in the `ProductRequest` DTO for incoming request bodies.
    *   `@RequestBody`: Binds the HTTP request body to a Java object.
    *   `@PathVariable`: Extracts a variable from the URL path.
    *   `ResponseEntity`: Provides full control over the HTTP response, including status code, headers, and body.

#### **Unit Test Files**

$$$$ src/test/java/com/example/productapi/service
&&&& ProductServiceTest.java
@@@@
package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes Mockito mocks
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach // Runs before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", new BigDecimal("1200.00"), 10);
        product2 = new Product(2L, "Mouse", "Gaming mouse", new BigDecimal("50.00"), 50);

        productRequest = new ProductRequest();
        productRequest.setName("Keyboard");
        productRequest.setDescription("Mechanical keyboard");
        productRequest.setPrice(new BigDecimal("100.00"));
        productRequest.setQuantity(20);
    }

    @Test
    void createProduct_shouldReturnProductResponse() {
        // Given
        Product newProduct = new Product(null, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());
        Product savedProduct = new Product(3L, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());

        // When
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse result = productService.createProduct(productRequest);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Keyboard", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_shouldReturnListOfProductResponses() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProductResponse_whenFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProductResponse_whenFound() {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Laptop");
        updateRequest.setDescription("Better performance");
        updateRequest.setPrice(new BigDecimal("1300.00"));
        updateRequest.setQuantity(8);

        Product updatedProductEntity = new Product(1L, "Updated Laptop", "Better performance", new BigDecimal("1300.00"), 8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductEntity);

        // When
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Laptop", result.getName());
        assertEquals(new BigDecimal("1300.00"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, productRequest));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldSucceed_whenFound() {
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
    void deleteProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
@@@@
**Explanation:**
*   **`ProductServiceTest.java`**: Unit tests for the `ProductService`.
    *   `@ExtendWith(MockitoExtension.class)`: Integrates JUnit 5 with Mockito, allowing the use of `@Mock` and `@InjectMocks`.
    *   `@Mock`: Creates mock objects (e.g., `productRepository`) that simulate the behavior of real dependencies.
    *   `@InjectMocks`: Injects the created mocks into the `productService` instance.
    *   `@BeforeEach`: Sets up common test data before each test method runs.
    *   `when().thenReturn()`: Configures the behavior of mock methods when they are called.
    *   `verify()`: Checks if certain methods on the mock objects were called with specific arguments and how many times.
    *   `assert*`: JUnit assertions to verify the expected outcomes.
    *   `assertThrows`: Verifies that a specific exception is thrown.

$$$$ src/test/java/com/example/productapi/controller
&&&& ProductControllerTest.java
@@@@
package com.example.productapi.controller;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Configures Spring Boot for testing controllers
class ProductControllerTest {

    @Autowired // Injects MockMvc, which allows simulating HTTP requests
    private MockMvc mockMvc;

    @MockBean // Creates a mock instance of ProductService and adds it to Spring context
    private ProductService productService;

    @Autowired // Injects ObjectMapper to convert Java objects to JSON
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;
    private ProductResponse productResponse1;
    private ProductResponse productResponse2;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("A product for testing");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setQuantity(5);

        productResponse1 = new ProductResponse(1L, "Laptop", "Powerful laptop", new BigDecimal("1200.00"), 10);
        productResponse2 = new ProductResponse(2L, "Mouse", "Gaming mouse", new BigDecimal("50.00"), 50);
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        // Given
        ProductResponse createdProductResponse = new ProductResponse(3L, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(createdProductResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        // Given
        ProductRequest invalidProductRequest = new ProductRequest(); // Name, price, quantity are null
        invalidProductRequest.setDescription("Invalid product");

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequest)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }


    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(productResponse1, productResponse2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponse1);

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Test Product");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("109.99"));
        updateRequest.setQuantity(7);

        ProductResponse updatedProductResponse = new ProductResponse(1L, "Updated Test Product", "Updated description", new BigDecimal("109.99"), 7);

        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(updatedProductResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Test Product"));
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        when(productService.updateProduct(anyLong(), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found for update"));

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found for update"));
    }

    @Test
    void deleteProduct_shouldReturnNoContent_whenFound() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    void deleteProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found for delete")).when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found for delete"));
    }
}
@@@@
**Explanation:**
*   **`ProductControllerTest.java`**: Unit tests for the `ProductController`.
    *   `@WebMvcTest(ProductController.class)`: Auto-configures Spring MVC components specifically for testing `ProductController`, mocking out the service layer and other components.
    *   `@Autowired MockMvc`: Injects `MockMvc`, a powerful tool for testing Spring MVC controllers without starting a full HTTP server.
    *   `@MockBean ProductService`: Replaces the actual `ProductService` bean with a Mockito mock in the Spring application context used for testing. This ensures that the controller is tested in isolation, verifying its interaction with the service.
    *   `@Autowired ObjectMapper`: Used to convert Java objects (like DTOs) to JSON strings for request bodies.
    *   `mockMvc.perform()`: Simulates an HTTP request (e.g., `post`, `get`, `put`, `delete`).
    *   `.andExpect()`: Chains assertions to verify the HTTP response status, headers, and body content using `jsonPath()`.
    *   `when().thenReturn()` and `doNothing().when()` / `doThrow().when()`: Configure the behavior of the mocked `productService` methods.

This comprehensive template covers essential layers of a Spring Boot REST API, including proper project setup, data modeling, business logic, API exposure, error handling, and unit testing.