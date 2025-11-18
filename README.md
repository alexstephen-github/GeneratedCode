Hello there! I'd be happy to provide a comprehensive code template for a Spring Boot REST API. This template will include a basic CRUD (Create, Read, Update, Delete) functionality for a `Product` entity, along with service and controller layers, and essential unit tests.

### Project Overview

This template demonstrates a common layered architecture for Spring Boot applications:

1.  **Model**: Defines the data structure (e.g., `Product`).
2.  **Repository**: Handles data persistence (here, an in-memory repository for simplicity).
3.  **Service**: Contains business logic and orchestrates data operations.
4.  **Controller**: Exposes REST endpoints for client interaction.
5.  **Tests**: Unit tests for both service and controller layers.

### Project Structure (Conceptual)

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── demo
│   │   │               ├── DemoApplication.java  (Main Spring Boot app)
│   │   │               ├── model
│   │   │               │   └── Product.java      (Data model)
│   │   │               ├── repository
│   │   │               │   └── ProductRepository.java (In-memory data store)
│   │   │               ├── service
│   │   │               │   └── ProductService.java (Business logic)
│   │   │               └── controller
│   │   │                   └── ProductController.java (REST endpoints)
│   │   └── resources
│   │       └── application.properties (Configuration)
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── demo
│                       ├── service
│                       │   └── ProductServiceTest.java (Service unit tests)
│                       └── controller
│                           └── ProductControllerTest.java (Controller unit tests)
└── pom.xml (Maven build file)
```

---

Let's generate the code!

#### 1. `pom.xml` (Maven Build File)

This file defines your project's dependencies and build configuration.

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
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot REST API</description>

    <properties>
        <java.version>17</java.version> <!-- Ensure this matches your JDK version -->
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter: Includes Tomcat and Spring MVC -->
        <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Test Starter: Includes JUnit, Mockito, Spring Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Lombok (Optional but highly recommended for boilerplate reduction) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin: For building executable JARs -->
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
*   **`spring-boot-starter-parent`**: Provides default configurations and dependency management for Spring Boot projects.
*   **`spring-boot-starter-web`**: Essential for building web applications, including RESTful services. It brings in Spring MVC, Tomcat, and Jackson (for JSON processing).
*   **`spring-boot-starter-test`**: Provides testing capabilities, including JUnit 5, Mockito, and Spring Test.
*   **`lombok`**: A library that automatically plugs into your build process and auto-generates code like getters, setters, constructors, etc., reducing boilerplate code. It's optional but widely used.
*   **`spring-boot-maven-plugin`**: Creates an executable JAR file that includes all necessary dependencies.

---

#### 2. Main Spring Boot Application Class

This is the entry point of your Spring Boot application.

$$$$ src/main/java/com/example/demo
&&&& DemoApplication.java
@@@@
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Spring Boot application.
 * This class uses @SpringBootApplication which is a convenience annotation that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings. For example, if spring-webmvc is on the classpath,
 *   this annotation flags the application as a web application and sets up a DispatcherServlet.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the `com.example.demo` package, allowing it to find controllers, services, etc.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
@@@@
**Explanation:**
*   **`@SpringBootApplication`**: A convenience annotation that combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It's the primary annotation for a Spring Boot application.
*   **`main` method**: Uses `SpringApplication.run()` to launch the application.

---

#### 3. Model

The `Product` class represents the data structure for products in our application.

$$$$ src/main/java/com/example/demo/model
&&&& Product.java
@@@@
package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the system.
 * Uses Lombok annotations to reduce boilerplate code.
 */
@Data // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields as arguments
public class Product {
    private Long id;
    private String name;
    private double price;
}
@@@@
**Explanation:**
*   **`@Data` (Lombok)**: Automatically generates getters, setters, `toString()`, `equals()`, and `hashCode()` methods.
*   **`@NoArgsConstructor` (Lombok)**: Generates a constructor with no arguments.
*   **`@AllArgsConstructor` (Lombok)**: Generates a constructor with arguments for all fields.
*   **`id`, `name`, `price`**: Basic attributes for a product.

---

#### 4. Repository

This class handles data access for `Product` entities. For this template, we're using an in-memory `ConcurrentHashMap` to simulate a database.

$$$$ src/main/java/com/example/demo/repository
&&&& ProductRepository.java
@@@@
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple in-memory repository for Product entities.
 * This simulates a database interaction without requiring a real database setup.
 * Uses ConcurrentHashMap for thread-safe storage and AtomicLong for ID generation.
 */
@Repository // Marks this class as a Spring repository component
public class ProductRepository {

    // Simulates a database table where keys are IDs and values are Product objects
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1); // For generating unique IDs

    /**
     * Finds all products currently stored in the repository.
     * @return A list of all products.
     */
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Finds a product by its ID.
     * @param id The ID of the product to find.
     * @return An Optional containing the product if found, or an empty Optional otherwise.
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    /**
     * Saves a new product or updates an existing one.
     * If the product ID is null, it's treated as a new product and a new ID is assigned.
     * If the product ID exists, the existing product is updated.
     * @param product The product to save or update.
     * @return The saved/updated product with its ID.
     */
    public Product save(Product product) {
        if (product.getId() == null) {
            // New product: assign a unique ID
            product.setId(nextId.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @return true if the product was found and deleted, false otherwise.
     */
    public boolean deleteById(Long id) {
        return products.remove(id) != null;
    }

    /**
     * Clears all products from the repository (useful for testing or full reset).
     */
    public void clear() {
        products.clear();
        nextId.set(1);
    }
}
@@@@
**Explanation:**
*   **`@Repository`**: Indicates that this class is a repository component in the Spring application context, enabling Spring's exception translation and other features.
*   **`ConcurrentHashMap`**: A thread-safe map used to store products, simulating a database table.
*   **`AtomicLong`**: Used to generate unique IDs for new products in a thread-safe manner.
*   **`findAll()`, `findById()`, `save()`, `deleteById()`**: Standard CRUD operations.
*   **`Optional<Product>`**: Used by `findById()` to clearly indicate that a product might not be found, promoting safer code.

---

#### 5. Service Layer

The `ProductService` class contains the business logic for managing products.

$$$$ src/main/java/com/example/demo/service
&&&& ProductService.java
@@@@
package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service layer for managing Product entities.
 * Contains business logic and orchestrates data access via ProductRepository.
 */
@Service // Marks this class as a Spring service component
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductService, injecting ProductRepository.
     * Spring's @Autowired handles the dependency injection.
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products.
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     * Throws ResponseStatusException if the product is not found.
     * @param id The ID of the product to retrieve.
     * @return The found Product.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
    }

    /**
     * Creates a new product.
     * The ID of the product in the input object is ignored; a new ID is always generated.
     * @param product The product object to create.
     * @return The newly created product with its assigned ID.
     */
    public Product createProduct(Product product) {
        // Ensure that we create a new product, ignoring any client-provided ID for new creation
        product.setId(null);
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     * Throws ResponseStatusException if the product to update is not found.
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details.
     * @return The updated Product.
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        // Do not update ID. The ID from productDetails is ignored, existingProduct's ID is retained.
        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product by its ID.
     * Throws ResponseStatusException if the product to delete is not found.
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
    }
}
@@@@
**Explanation:**
*   **`@Service`**: Indicates that this class is a service component in the Spring application context.
*   **`@Autowired`**: Used for dependency injection. Spring will automatically provide an instance of `ProductRepository`.
*   **`getAllProducts()`, `getProductById()`, `createProduct()`, `updateProduct()`, `deleteProduct()`**: These methods encapsulate the business logic.
*   **`ResponseStatusException`**: Used to signal HTTP error statuses (like `404 Not Found`) directly from the service layer when an entity isn't found, simplifying error handling for the controller.

---

#### 6. Controller Layer

The `ProductController` class exposes the REST endpoints for interacting with products.

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
 * REST Controller for managing Product resources.
 * Handles HTTP requests and delegates business logic to ProductService.
 */
@RestController // Marks this class as a Spring REST controller
@RequestMapping("/api/products") // Base path for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for ProductController, injecting ProductService.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Retrieves all products.
     * @return A ResponseEntity containing a list of products and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET /api/products/{id}
     * Retrieves a product by its ID.
     * @param id The ID of the product to retrieve from the URL path.
     * @return A ResponseEntity containing the product and HTTP status OK if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * POST /api/products
     * Creates a new product.
     * @param product The product object sent in the request body.
     * @return A ResponseEntity containing the newly created product and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product.
     * @param id The ID of the product to update from the URL path.
     * @param productDetails The product object with updated details sent in the request body.
     * @return A ResponseEntity containing the updated product and HTTP status OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product by its ID.
     * @param id The ID of the product to delete from the URL path.
     * @return A ResponseEntity with HTTP status NO_CONTENT (204) upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
@@@@
**Explanation:**
*   **`@RestController`**: Combines `@Controller` and `@ResponseBody`. It indicates that this class handles incoming web requests and that the return values of its methods should be directly bound to the web response body.
*   **`@RequestMapping("/api/products")`**: Defines the base URL path for all endpoints in this controller.
*   **`@Autowired`**: Injects `ProductService`.
*   **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`**: Map HTTP GET, POST, PUT, and DELETE requests to specific handler methods.
*   **`@PathVariable`**: Binds a URI template variable (e.g., `{id}`) to a method parameter.
*   **`@RequestBody`**: Binds the HTTP request body to a method parameter (e.g., converts JSON to a `Product` object).
*   **`ResponseEntity<?>`**: A convenient way to customize the HTTP response, including status code, headers, and body.

---

#### 7. Configuration (`application.properties`)

Basic configuration for your Spring Boot application.

$$$$ src/main/resources
&&&& application.properties
@@@@
# Server port
server.port=8080

# Logging level (optional)
logging.level.com.example.demo=INFO
@@@@
**Explanation:**
*   **`server.port`**: Specifies the port on which the embedded Tomcat server will run (default is 8080).
*   **`logging.level`**: Configures the logging level for your application package.

---

### Unit Test Cases

Now, let's add unit tests for the `ProductService` and `ProductController`.

#### 8. Service Unit Test

Tests the business logic of the `ProductService` in isolation, mocking its dependencies.

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
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProductService.
 * Uses Mockito to mock the ProductRepository dependency.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mock(s) into an instance of ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach // Sets up test data before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00);
        product2 = new Product(2L, "Mouse", 25.00);
    }

    @Test
    @DisplayName("Should return all products")
    void getAllProducts_shouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products); // Mock repository behavior

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product1, result.get(0));
        assertEquals(product2, result.get(1));
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @Test
    @DisplayName("Should return product by ID")
    void getProductById_shouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        Product result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(product1.getId(), result.getId());
        assertEquals(product1.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception if product not found by ID")
    void getProductById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.getProductById(99L));

        assertEquals("404 NOT_FOUND \"Product not found with id: 99\"", exception.getMessage());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create a new product")
    void createProduct_shouldCreateProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", 75.00);
        Product savedProduct = new Product(3L, "Keyboard", 75.00); // Repository assigns ID
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productService.createProduct(newProduct);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId()); // Verify ID was assigned
        assertEquals("Keyboard", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update an existing product")
    void updateProduct_shouldUpdateProduct() {
        // Given
        Product updatedDetails = new Product(null, "Updated Laptop", 1300.00); // ID ignored by service logic
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(
                new Product(1L, "Updated Laptop", 1300.00)
        );

        // When
        Product result = productService.updateProduct(1L, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId()); // ID remains the same
        assertEquals("Updated Laptop", result.getName());
        assertEquals(1300.00, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception if product not found for update")
    void updateProduct_shouldThrowExceptionWhenNotFound() {
        // Given
        Product updatedDetails = new Product(null, "NonExistent Product", 100.00);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.updateProduct(99L, updatedDetails));

        assertEquals("404 NOT_FOUND \"Product not found with id: 99\"", exception.getMessage());
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class)); // Save should not be called
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void deleteProduct_shouldDeleteProduct() {
        // Given
        when(productRepository.deleteById(1L)).thenReturn(true);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).deleteById(1L);
        // No exception means success
    }

    @Test
    @DisplayName("Should throw exception if product not found for delete")
    void deleteProduct_shouldThrowExceptionWhenNotFound() {
        // Given
        when(productRepository.deleteById(99L)).thenReturn(false);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.deleteProduct(99L));

        assertEquals("404 NOT_FOUND \"Product not found with id: 99\"", exception.getMessage());
        verify(productRepository, times(1)).deleteById(99L);
    }
}
@@@@
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@Mock`**: Creates a mock object of `ProductRepository`. This allows you to define how the repository behaves without actually interacting with its real implementation.
*   **`@InjectMocks`**: Creates an instance of `ProductService` and injects the `@Mock` dependencies into it.
*   **`@BeforeEach`**: Ensures that `product1` and `product2` are re-initialized before each test.
*   **`when().thenReturn()`**: Mockito syntax to define the behavior of mocked methods.
*   **`verify().times(n)`**: Verifies that a specific method on the mock was called a certain number of times.
*   **`assertThrows()`**: JUnit 5 assertion to check if a specific exception is thrown.
*   These tests cover successful operations and error scenarios (e.g., product not found).

---

#### 9. Controller Unit Test

Tests the `ProductController` endpoints, mocking the `ProductService` layer. Uses `MockMvc` to simulate HTTP requests.

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
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController.
 * Uses @WebMvcTest to test the controller layer in isolation,
 * mocking the ProductService dependency.
 */
@WebMvcTest(ProductController.class) // Focuses Spring Boot tests on ProductController
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @MockBean // Creates a mock instance of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to/from JSON

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00);
        product2 = new Product(2L, "Mouse", 25.00);
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void getProductById_shouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(product1);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200.00));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 if product not found")
    void getProductById_shouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("POST /api/products - Should create a new product")
    void createProduct_shouldCreateProduct() throws Exception {
        // Given
        Product newProduct = new Product(null, "Keyboard", 75.00);
        Product savedProduct = new Product(3L, "Keyboard", 75.00);
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(75.00));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update an existing product")
    void updateProduct_shouldUpdateProduct() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "Updated Laptop", 1300.00); // ID will be ignored by service
        Product updatedProduct = new Product(1L, "Updated Laptop", 1300.00);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1300.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should return 404 if product not found for update")
    void updateProduct_shouldReturnNotFound() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "NonExistent Product", 100.00);
        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete a product")
    void deleteProduct_shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L); // Mock void method

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should return 404 if product not found for delete")
    void deleteProduct_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"))
                .when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation:**
*   **`@WebMvcTest(ProductController.class)`**: Configures a Spring application context with only the beans relevant for testing an MVC controller. It will auto-configure `MockMvc`, Jackson `ObjectMapper`, etc., but won't load the full application context.
*   **`@Autowired MockMvc mockMvc`**: Injects `MockMvc`, which is used to send HTTP requests to the controller.
*   **`@MockBean ProductService productService`**: Creates a mock of `ProductService` and adds it to the Spring context, replacing the real `ProductService` instance that the `ProductController` would normally depend on.
*   **`@Autowired ObjectMapper objectMapper`**: Used to serialize Java objects to JSON strings for request bodies and deserialize JSON responses.
*   **`mockMvc.perform(...)`**: Initiates a request to a controller.
*   **`andExpect(status().isOk())`, `andExpect(jsonPath("$.id").value(1L))`**: Assertions to check the HTTP status code and the JSON response body. `jsonPath` allows you to navigate and assert values within the JSON response.
*   **`doNothing().when()` / `doThrow().when()`**: Mockito syntax for defining behavior for `void` methods or throwing exceptions.

---

### How to Run This Template:

1.  **Save the files**: Place each code block into the corresponding folder structure.
2.  **Open in IDE**: Import the project into an IDE like IntelliJ IDEA or Eclipse as a Maven project.
3.  **Run `DemoApplication.java`**: Right-click on `DemoApplication.java` and choose "Run".
4.  **Access the API**:
    *   Open your browser or an API client (like Postman or Insomnia).
    *   **GET all products**: `http://localhost:8080/api/products` (Initially, it will be empty)
    *   **POST a new product**: `http://localhost:8080/api/products`
        *   Method: `POST`
        *   Body (raw, JSON):
            ```json
            {
                "name": "Smartphone",
                "price": 699.99
            }
            ```
    *   **GET product by ID**: `http://localhost:8080/api/products/1` (Replace `1` with the actual ID returned from the POST)
    *   **PUT to update product**: `http://localhost:8080/api/products/1`
        *   Method: `PUT`
        *   Body (raw, JSON):
            ```json
            {
                "name": "iPhone 15",
                "price": 799.00
            }
            ```
    *   **DELETE product**: `http://localhost:8080/api/products/1`
        *   Method: `DELETE`

5.  **Run Tests**: In your IDE, right-click on `src/test/java` and select "Run Tests" to execute all unit tests.

This complete template provides a solid foundation for building Spring Boot REST APIs with good practices and testing coverage!