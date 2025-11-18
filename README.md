Here's a comprehensive code template for a Spring Boot REST API, including typical layers, unit tests, and explanations. We'll create a simple API for managing `Product` resources.

### 1. Project Setup (pom.xml)

This `pom.xml` includes essential dependencies for a Spring Boot REST API: `spring-boot-starter-web` for building web applications, `spring-boot-starter-data-jpa` for database interaction, `h2` as an in-memory database for quick setup and testing, and `spring-boot-starter-test` for testing.

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
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot REST API</description>

    <properties>
        <java.version>17</java.version> <!-- Use Java 17 or higher -->
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter for building RESTful APIs -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Data JPA Starter for database interaction -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database - In-memory database, great for development and testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Spring Boot Test Starter for unit and integration testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Lombok for reducing boilerplate code (getters, setters, constructors) -->
        <!-- Add this if you want to use Lombok. Remember to install Lombok plugin in your IDE. -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
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
*   **`parent`**: Inherits from Spring Boot's parent POM for dependency management and plugin configuration.
*   **`dependencies`**:
    *   `spring-boot-starter-web`: Provides all necessary components for building REST APIs (Spring MVC, Tomcat embedded server, Jackson for JSON parsing).
    *   `spring-boot-starter-data-jpa`: Simplifies database access with Spring Data JPA and Hibernate.
    *   `h2`: An in-memory database, perfect for development and testing as it doesn't require a separate database server.
    *   `spring-boot-starter-test`: Contains utilities for testing Spring Boot applications (JUnit, Mockito, Spring Test).
    *   `lombok`: (Optional but highly recommended) Reduces boilerplate code like getters, setters, constructors, etc., using annotations. Make sure to install the Lombok plugin in your IDE.

---

### 2. Application Configuration

`application.properties` for basic settings, especially useful for configuring the in-memory H2 database.

$$$$ src/main/resources
&&&& application.properties
@@@@
# Server Port
server.port=8080

# H2 Database Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update # Creates/updates schema automatically. Use 'none' or 'validate' in production.
spring.jpa.show-sql=true # Log SQL queries to console
spring.jpa.properties.hibernate.format_sql=true # Format logged SQL queries
@@@@
**Explanation:**
*   **`server.port`**: Sets the application's port.
*   **`spring.h2.console.*`**: Enables the H2 database web console, accessible at `http://localhost:8080/h2-console`.
*   **`spring.datasource.*`**: Configures the H2 in-memory database. `jdbc:h2:mem:testdb` means a database named `testdb` will be created in memory.
*   **`spring.jpa.hibernate.ddl-auto=update`**: This tells Hibernate to update the database schema based on your JPA entities. For production, you usually set this to `none` or `validate` and manage schema changes with migration tools (e.g., Flyway, Liquibase).
*   **`spring.jpa.show-sql=true`**: Logs all SQL statements executed by Hibernate, which is very useful for debugging.

---

### 3. Main Application Class

The entry point of your Spring Boot application.

$$$$ src/main/java/com/example/demo
&&&& DemoApplication.java
@@@@
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
@@@@
**Explanation:**
*   **`@SpringBootApplication`**: This is a convenience annotation that combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It essentially sets up everything you need for a Spring Boot application.
*   **`main` method**: Uses `SpringApplication.run()` to bootstrap and launch the Spring application.

---

### 4. Model (Product Entity)

Represents the data structure for a product, mapped to a database table using JPA annotations.

$$$$ src/main/java/com/example/demo/model
&&&& Product.java
@@@@
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the application.
 * This class is mapped to a database table named 'products'.
 *
 * @Entity: Marks this class as a JPA entity, meaning it corresponds to a table in the database.
 * @Table: Specifies the name of the database table for this entity.
 * @Data: A Lombok annotation to automatically generate getters, setters, toString(), equals(), and hashCode().
 * @NoArgsConstructor: A Lombok annotation to generate a no-argument constructor.
 * @AllArgsConstructor: A Lombok annotation to generate a constructor with all fields as arguments.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * The unique identifier for the product.
     * @Id: Marks this field as the primary key of the entity.
     * @GeneratedValue: Specifies how the primary key value is generated.
     *                  GenerationType.IDENTITY indicates that the database assigns an identity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product. Cannot be null.
     * @Column: Specifies the mapping of the field to a database column.
     *          'nullable = false' ensures the column cannot contain null values.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The price of the product. Cannot be null.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * The description of the product.
     */
    private String description;
}
@@@@
**Explanation:**
*   **`@Entity`**: Marks this class as a JPA entity, mapping it to a database table.
*   **`@Table(name = "products")`**: Specifies the table name in the database.
*   **`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`**: Lombok annotations to automatically generate boilerplate code (getters, setters, constructors, etc.).
*   **`@Id`**: Marks `id` as the primary key.
*   **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Configures the primary key to be auto-generated by the database.
*   **`@Column(nullable = false)`**: Ensures the `name` and `price` columns in the database cannot be null.

---

### 5. Repository Layer

An interface for data access operations, leveraging Spring Data JPA.

$$$$ src/main/java/com/example/demo/repository
&&&& ProductRepository.java
@@@@
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit standard CRUD operations and more.
 * Spring Data JPA automatically provides implementations for these methods at runtime.
 *
 * JpaRepository takes two generic parameters:
 * 1. The entity type (Product)
 * 2. The type of the entity's primary key (Long)
 *
 * @Repository: Stereotype annotation indicating that this is a repository component
 *              and provides a hint for component-scanning.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // You can define custom query methods here if needed.
    // Spring Data JPA can automatically generate queries based on method names.
    // Example: Find a product by its name
    Optional<Product> findByName(String name);
}
@@@@
**Explanation:**
*   **`@Repository`**: A stereotype annotation that marks this interface as a Spring Data repository, enabling component scanning.
*   **`extends JpaRepository<Product, Long>`**: This is the core of Spring Data JPA. By extending `JpaRepository`, our `ProductRepository` automatically inherits a rich set of CRUD (Create, Read, Update, Delete) and pagination/sorting methods for the `Product` entity with `Long` as its primary key type.
*   **`Optional<Product> findByName(String name);`**: An example of a custom query method. Spring Data JPA can derive queries from method names (e.g., `findBy<FieldName>`). `Optional` is used to handle cases where no product is found, preventing `NullPointerException`s.

---

### 6. Service Layer

Contains the business logic for product-related operations.

$$$$ src/main/java/com/example/demo/service
&&&& ProductService.java
@@@@
package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Product entities.
 * This class encapsulates the business logic and acts as an intermediary
 * between the Controller and the Repository layers.
 *
 * @Service: Stereotype annotation indicating that this class is a service component.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductService, injecting ProductRepository.
     * Spring will automatically inject an instance of ProductRepository because of @Autowired
     * (or implicitly if there's only one constructor).
     *
     * @param productRepository The repository for Product entities.
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products.
     *
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return An Optional containing the product if found, or empty if not found.
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Creates a new product.
     *
     * @param product The product object to be created.
     * @return The created product with its generated ID.
     */
    public Product createProduct(Product product) {
        // Here you might add business logic, validation, etc.
        // For example, checking if a product with the same name already exists.
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new IllegalArgumentException("Product with name '" + product.getName() + "' already exists.");
        }
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details.
     * @return An Optional containing the updated product if found, or empty if not found.
     */
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setDescription(productDetails.getDescription());
                    return productRepository.save(existingProduct);
                });
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return true if the product was found and deleted, false otherwise.
     */
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}
@@@@
**Explanation:**
*   **`@Service`**: Marks this class as a Spring service component, indicating it holds business logic.
*   **Dependency Injection**: `ProductRepository` is injected into the `ProductService` constructor. Spring manages the lifecycle of these beans.
*   **Business Logic**: This layer contains methods like `getAllProducts`, `getProductById`, `createProduct`, `updateProduct`, and `deleteProduct`.
    *   Notice the `createProduct` method includes a simple validation: it checks if a product with the same name already exists before saving. This is an example of business logic.
    *   Methods like `getProductById` and `updateProduct` return `Optional` to clearly indicate that a resource might not be found, promoting safer code.
    *   The `updateProduct` method retrieves the existing product, updates its fields, and then saves it.

---

### 7. Controller Layer

Exposes RESTful endpoints for the Product API.

$$$$ src/main/java/com/example/demo/controller
&&&& ProductController.java
@@@@
package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product management.
 * Handles incoming HTTP requests and sends back HTTP responses.
 *
 * @RestController: A convenience annotation that combines @Controller and @ResponseBody.
 *                  It marks the class as a Spring MVC controller where every method
 *                  returns a domain object instead of a view, and the domain object
 *                  is converted to JSON/XML (depending on the client's Accept header).
 * @RequestMapping: Maps HTTP requests to handler methods of MVC and REST controllers.
 *                  Here, it sets the base URL for all endpoints in this controller to /api/products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for ProductController, injecting ProductService.
     *
     * @param productService The service for Product entities.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products : Get all products.
     *
     * @return A ResponseEntity containing a list of products and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK with the list of products
    }

    /**
     * GET /api/products/{id} : Get a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return A ResponseEntity containing the product if found (200 OK), or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // If product found, return 200 OK with product
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    /**
     * POST /api/products : Create a new product.
     *
     * @param product The product object to be created, sent in the request body.
     * @return A ResponseEntity containing the created product (201 Created), or 400 Bad Request
     *         if a product with the same name already exists.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created
        } catch (IllegalArgumentException e) {
            // Handle business logic errors, e.g., product with same name exists
            return ResponseEntity.badRequest().build(); // Or return a custom error body
        }
    }

    /**
     * PUT /api/products/{id} : Update an existing product.
     *
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details, sent in the request body.
     * @return A ResponseEntity containing the updated product (200 OK), or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(ResponseEntity::ok) // If product updated, return 200 OK with updated product
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    /**
     * DELETE /api/products/{id} : Delete a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return A ResponseEntity with 204 No Content if deleted, or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if product doesn't exist
        }
    }
}
@@@@
**Explanation:**
*   **`@RestController`**: Combines `@Controller` and `@ResponseBody`. It means this class handles incoming web requests and directly returns the response body (typically JSON/XML).
*   **`@RequestMapping("/api/products")`**: Sets the base path for all endpoints in this controller to `/api/products`.
*   **`@Autowired`**: Injects `ProductService`.
*   **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`**: Map HTTP GET, POST, PUT, DELETE requests to specific handler methods.
*   **`@PathVariable Long id`**: Extracts the `id` from the URL path (e.g., `/api/products/1`).
*   **`@RequestBody Product product`**: Binds the HTTP request body (JSON) to a `Product` object.
*   **`ResponseEntity`**: A flexible way to return HTTP responses, allowing you to control the status code, headers, and body.
    *   `ResponseEntity.ok(body)`: Returns HTTP 200 OK.
    *   `new ResponseEntity<>(body, HttpStatus.CREATED)`: Returns HTTP 201 Created.
    *   `ResponseEntity.notFound().build()`: Returns HTTP 404 Not Found.
    *   `ResponseEntity.noContent().build()`: Returns HTTP 204 No Content.
    *   `ResponseEntity.badRequest().build()`: Returns HTTP 400 Bad Request.

---

### 8. Unit Test for Service Layer

Testing the `ProductService` in isolation, mocking the `ProductRepository`.

$$$$ src/test/java/com/example/demo/service
&&&& ProductServiceTest.java
@@@@
package com.example.demo.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProductService.
 * Uses Mockito to mock the ProductRepository dependency.
 *
 * @ExtendWith(MockitoExtension.class): Integrates Mockito with JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // Mock the ProductRepository dependency
    @Mock
    private ProductRepository productRepository;

    // Inject mocks into ProductService instance
    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    /**
     * Set up common test data before each test method runs.
     */
    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop");
        product2 = new Product(2L, "Mouse", 25.00, "Wireless mouse");
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_shouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product1.getName(), result.get(0).getName());
        assertEquals(product2.getName(), result.get(1).getName());
        verify(productRepository, times(1)).findAll(); // Verify findAll was called once
    }

    @Test
    @DisplayName("Should return product by ID when found")
    void getProductById_shouldReturnProduct_whenFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(product1.getName(), result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when product by ID not found")
    void getProductById_shouldReturnEmptyOptional_whenNotFound() {
        // Arrange
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(3L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    @DisplayName("Should create a new product successfully")
    void createProduct_shouldCreateProduct_whenNameDoesNotExist() {
        // Arrange
        Product newProduct = new Product(null, "Keyboard", 75.00, "Mechanical keyboard");
        when(productRepository.findByName(newProduct.getName())).thenReturn(Optional.empty());
        when(productRepository.save(newProduct)).thenReturn(new Product(3L, "Keyboard", 75.00, "Mechanical keyboard"));

        // Act
        Product createdProduct = productService.createProduct(newProduct);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(3L, createdProduct.getId());
        assertEquals("Keyboard", createdProduct.getName());
        verify(productRepository, times(1)).findByName("Keyboard");
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating product with existing name")
    void createProduct_shouldThrowException_whenNameExists() {
        // Arrange
        Product newProduct = new Product(null, "Laptop", 1500.00, "Gaming laptop"); // Name 'Laptop' already exists (product1)
        when(productRepository.findByName(newProduct.getName())).thenReturn(Optional.of(product1)); // Simulate existing product

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(newProduct);
        });
        assertEquals("Product with name 'Laptop' already exists.", thrown.getMessage());
        verify(productRepository, times(1)).findByName("Laptop");
        verify(productRepository, never()).save(any(Product.class)); // Verify save was NOT called
    }

    @Test
    @DisplayName("Should update an existing product when found")
    void updateProduct_shouldUpdateProduct_whenFound() {
        // Arrange
        Product updatedDetails = new Product(1L, "Laptop Pro", 1300.00, "Updated powerful laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails); // Mock save returns the updated product

        // Act
        Optional<Product> result = productService.updateProduct(1L, updatedDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Laptop Pro", result.get().getName());
        assertEquals(1300.00, result.get().getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return empty optional when updating non-existent product")
    void updateProduct_shouldReturnEmptyOptional_whenNotFound() {
        // Arrange
        Product updatedDetails = new Product(3L, "NonExistent", 100.00, "Non-existent product");
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.updateProduct(3L, updatedDetails);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).save(any(Product.class)); // Verify save was NOT called
    }

    @Test
    @DisplayName("Should delete a product successfully when found")
    void deleteProduct_shouldDeleteProduct_whenFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(product1); // Mock void method

        // Act
        boolean result = productService.deleteProduct(1L);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent product")
    void deleteProduct_shouldReturnFalse_whenNotFound() {
        // Arrange
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.deleteProduct(3L);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).delete(any(Product.class)); // Verify delete was NOT called
    }
}
@@@@
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@Mock private ProductRepository productRepository;`**: Creates a mock instance of `ProductRepository`. This mock will simulate the behavior of the real repository.
*   **`@InjectMocks private ProductService productService;`**: Injects the mocked `ProductRepository` into an instance of `ProductService`. This is the class we are testing.
*   **`@BeforeEach`**: Method runs before each test to set up common test data.
*   **`when(...).thenReturn(...)`**: Mockito's way of defining the behavior of mock objects. For example, `when(productRepository.findAll()).thenReturn(products);` tells the mock to return `products` whenever `findAll()` is called.
*   **`doNothing().when(...).delete(...)`**: Used for mocking void methods.
*   **`verify(mock, times(n)).method()`**: Verifies that a specific method on a mock was called a certain number of times. `never()` verifies it was never called.
*   **`Assertions`**: JUnit 5 assertions (`assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `assertThrows`) are used to check expected outcomes.

---

### 9. Unit Test for Controller Layer

Testing the `ProductController` using MockMvc to simulate HTTP requests without starting a full server.

$$$$ src/test/java/com/example/demo/controller
&&&& ProductControllerTest.java
@@@@
package com.example.demo.controller;

import com.example.demo.model.Product;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController.
 * Uses @WebMvcTest to focus on Spring MVC components and mocks other layers.
 */
@WebMvcTest(ProductController.class) // Only loads ProductController and its dependencies
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to/from JSON

    // Mock the ProductService dependency for isolation
    @MockBean
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop");
        product2 = new Product(2L, "Mouse", 25.00, "Wireless mouse");
    }

    @Test
    @DisplayName("GET /api/products should return list of products")
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Expect a JSON array of size 2
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product when found")
    void getProductById_shouldReturnProduct_whenFound() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(product1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 404 Not Found when product not found")
    void getProductById_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("POST /api/products should create new product")
    void createProduct_shouldCreateProduct() throws Exception {
        // Arrange
        Product newProduct = new Product(null, "Keyboard", 75.00, "Mechanical keyboard");
        Product savedProduct = new Product(3L, "Keyboard", 75.00, "Mechanical keyboard");
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))) // Convert Product object to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("POST /api/products should return 400 Bad Request for invalid product creation")
    void createProduct_shouldReturnBadRequest_forInvalidProduct() throws Exception {
        // Arrange - Simulate a business rule violation (e.g., product name already exists)
        Product newProduct = new Product(null, "Existing Product", 100.00, "Description");
        when(productService.createProduct(any(Product.class))).thenThrow(new IllegalArgumentException("Product with name 'Existing Product' already exists."));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request

        verify(productService, times(1)).createProduct(any(Product.class));
    }


    @Test
    @DisplayName("PUT /api/products/{id} should update existing product")
    void updateProduct_shouldUpdateProduct_whenFound() throws Exception {
        // Arrange
        Product updatedDetails = new Product(1L, "Laptop Pro", 1300.00, "Updated powerful laptop");
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(Optional.of(updatedDetails));

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop Pro")));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 404 Not Found when updating non-existent product")
    void updateProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        Product updatedDetails = new Product(99L, "NonExistent", 100.00, "Non-existent product");
        when(productService.updateProduct(eq(99L), any(Product.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should delete product successfully")
    void deleteProduct_shouldDeleteProduct_whenFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should return 404 Not Found when deleting non-existent product")
    void deleteProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation:**
*   **`@WebMvcTest(ProductController.class)`**: This annotation tells Spring to configure only the web layer components (controllers, filters, etc.) and not the full application context. It also auto-configures `MockMvc` for testing. Only `ProductController` and its directly needed dependencies will be loaded.
*   **`@Autowired private MockMvc mockMvc;`**: Injects `MockMvc`, which allows you to send HTTP requests to the controller without starting a real server.
*   **`@Autowired private ObjectMapper objectMapper;`**: Injects `ObjectMapper` from Jackson, useful for converting Java objects to JSON strings and vice-versa for request/response bodies.
*   **`@MockBean private ProductService productService;`**: Creates a Mockito mock of `ProductService` and adds it to the Spring application context as a bean. Any calls to `ProductService` from the `ProductController` will go to this mock.
*   **`mockMvc.perform(get("/api/products"))`**: Simulates an HTTP GET request to `/api/products`.
*   **`.andExpect(status().isOk())`**: Asserts that the HTTP status code is 200 OK.
*   **`.andExpect(jsonPath("$", hasSize(2)))`**: Uses JSONPath to assert properties of the JSON response. `$` refers to the root element. `hasSize(2)` checks if the array has 2 elements.
*   **`.andExpect(jsonPath("$[0].name", is(product1.getName())))`**: Asserts the name of the first product in the response.
*   **`.content(objectMapper.writeValueAsString(newProduct))`**: Converts a Java object to its JSON string representation to be used as the request body.
*   **`verify(productService, times(1)).createProduct(any(Product.class))`**: Verifies that the `createProduct` method on the `productService` mock was called exactly once with any `Product` object.
*   **`eq(1L)` and `any(Product.class)`**: Mockito matchers used inside `when` and `verify` to match arguments flexibly. `eq` is for exact matching, `any` for matching any instance of a class.

---

This complete template provides a robust starting point for building Spring Boot REST APIs, adhering to common architectural patterns and including essential unit tests for maintainability.