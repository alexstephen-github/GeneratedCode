Hello there! I'd be glad to help you with a comprehensive code template for a Spring Boot REST API. This template includes a basic CRUD (Create, Read, Update, Delete) functionality for a `Product` entity, along with best practices like a layered architecture, exception handling, and unit tests.

Here's the code template:

#### Project Setup (`pom.xml`)
This `pom.xml` includes essential dependencies for Spring Boot Web, Spring Data JPA, H2 Database (for in-memory testing), Lombok (for boilerplate reduction), and testing libraries.

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
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter for building RESTful applications -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA Starter for database interaction -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database for in-memory database testing and development -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok to reduce boilerplate code (getters, setters, constructors) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test Starter for unit and integration testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Mockito for mocking dependencies in unit tests -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin for packaging and running the application -->
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
*   `spring-boot-starter-web`: Provides all necessary dependencies to build web applications, including RESTful services.
*   `spring-boot-starter-data-jpa`: Simplifies database access using JPA (Java Persistence API) and Hibernate.
*   `h2`: An in-memory database, perfect for development and testing without needing a full database setup.
*   `lombok`: A library that automatically generates boilerplate code like getters, setters, constructors, and `equals`/`hashCode` methods, making your POJOs much cleaner.
*   `spring-boot-starter-test`: Includes JUnit, Mockito, and Spring Test for writing comprehensive tests.

---

### **1. Main Application Class**

This is the entry point of your Spring Boot application.

$$$$ src/main/java/com/example/demo
&&&& DemoApplication.java
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
**Explanation:**
*   `@SpringBootApplication`: This annotation combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. It marks a configuration class that starts the auto-configuration process, allowing Spring Boot to guess and configure beans based on your classpath and other settings, and also scans for other components in the package.

---

### **2. Model (Entity)**

This defines the data structure for our `Product`. It's a JPA entity that will be mapped to a database table.

$$$$ src/main/java/com/example/demo/model
&&&& Product.java
@@@@
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Marks this class as a JPA entity, mapped to a database table
@Data // Lombok annotation: Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // Lombok annotation: Generates a no-argument constructor
@AllArgsConstructor // Lombok annotation: Generates a constructor with all fields as arguments
public class Product {

    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key to be auto-generated by the database
    private Long id;
    private String name;
    private double price;
    private String description;

    // We can add a custom constructor if needed, or rely on Lombok's @NoArgsConstructor/@AllArgsConstructor
    // public Product(String name, double price, String description) {
    //     this.name = name;
    //     this.price = price;
    //     this.description = description;
    // }
}
@@@@
**Explanation:**
*   `@Entity`: Standard JPA annotation, indicating that this class is an entity and will be mapped to a database table.
*   `@Id`: Marks the `id` field as the primary key.
*   `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Configures the primary key to be auto-incremented by the database.
*   `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`: Lombok annotations that drastically reduce boilerplate code for getters, setters, constructors, `equals`, `hashCode`, and `toString` methods.

---

### **3. Repository Interface**

This interface extends Spring Data JPA's `JpaRepository`, providing ready-to-use CRUD operations without writing any implementation code.

$$$$ src/main/java/com/example/demo/repository
&&&& ProductRepository.java
@@@@
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Optional: Indicates that this interface is a "Repository", useful for clarity and component scanning
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.
    // You can add custom query methods here, e.g.:
    Optional<Product> findByName(String name);
}
@@@@
**Explanation:**
*   `JpaRepository<Product, Long>`: Inherits a rich set of methods for CRUD operations on `Product` entities, where `Long` is the type of the primary key.
*   `findByName(String name)`: An example of a custom query method. Spring Data JPA can automatically generate the implementation for such methods based on their name.

---

### **4. Service Layer**

The service layer contains the business logic. It orchestrates interactions between the controller and the repository, providing a clean API for performing operations on `Product` entities.

$$$$ src/main/java/com/example/demo/service
&&&& ProductService.java
@@@@
package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired // Injects ProductRepository instance
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true) // Improves performance for read operations
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Transactional // Ensures atomicity for write operations
    public Product createProduct(Product product) {
        // You can add business logic here, e.g., validate product data
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setDescription(productDetails.getDescription());

        // You can add more validation or business logic here
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
@@@@
**Explanation:**
*   `@Service`: Annotates this class as a Spring service, making it a candidate for component scanning.
*   `@Autowired`: Used for dependency injection. Spring automatically provides an instance of `ProductRepository`. Using constructor injection (as shown) is generally recommended.
*   `@Transactional`: Ensures that methods are executed within a database transaction. If an exception occurs, the transaction can be rolled back, maintaining data integrity. `readOnly = true` is an optimization for methods that only read data.
*   Error Handling: Uses `orElseThrow` to throw a custom `ProductNotFoundException` if a product is not found, providing clearer error messages.

---

#### **5. Custom Exception**

A dedicated exception for when a product is not found.

$$$$ src/main/java/com/example/demo/exception
&&&& ProductNotFoundException.java
@@@@
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Sets the HTTP status code to 404 Not Found when this exception is thrown
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
@@@@
**Explanation:**
*   `@ResponseStatus(HttpStatus.NOT_FOUND)`: When this exception is thrown from a controller, Spring will automatically return an HTTP 404 (Not Found) status code.

---

#### **6. Global Exception Handler**

This class centralizes exception handling across all controllers.

$$$$ src/main/java/com/example/demo/exception
&&&& GlobalExceptionHandler.java
@@@@
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice // Enables this class to handle exceptions across the entire application
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class) // Specifies that this method handles ProductNotFoundException
    public ResponseEntity<Object> handleProductNotFoundException(
            ProductNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", "")); // Extract actual path

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class) // Generic handler for any other unhandled exceptions
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred: " + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
@@@@
**Explanation:**
*   `@ControllerAdvice`: A specialization of `@Component` that allows handling exceptions across the whole application, not just an individual controller.
*   `@ExceptionHandler(ProductNotFoundException.class)`: This method will be invoked whenever a `ProductNotFoundException` is thrown.
*   `ResponseEntity<Object>`: Provides full control over the HTTP response, including status code, headers, and body.
*   Custom Error Body: Creates a structured JSON response for errors, including timestamp, status, error message, and path.

---

### **7. Controller (REST API Endpoints)**

This class exposes the REST endpoints for `Product` operations.

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

@RestController // Marks this class as a REST Controller, handling incoming HTTP requests
@RequestMapping("/api/products") // Base URL path for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired // Injects ProductService instance
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping // Handles GET requests to /api/products
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK); // Returns 200 OK with the list of products
    }

    @GetMapping("/{id}") // Handles GET requests to /api/products/{id}
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK); // Returns 200 OK with the found product
    }

    @PostMapping // Handles POST requests to /api/products
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created with the new product
    }

    @PutMapping("/{id}") // Handles PUT requests to /api/products/{id}
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK); // Returns 200 OK with the updated product
    }

    @DeleteMapping("/{id}") // Handles DELETE requests to /api/products/{id}
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content
    }
}
@@@@
**Explanation:**
*   `@RestController`: Combines `@Controller` and `@ResponseBody`. This means that the return value of controller methods should be bound directly to the web response body.
*   `@RequestMapping("/api/products")`: Sets the base path for all request mappings in this controller.
*   `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Specific annotations for handling different HTTP methods.
*   `@PathVariable`: Extracts a value from the URI path.
*   `@RequestBody`: Maps the HTTP request body to a Java object.
*   `ResponseEntity`: A generic type that allows full control over the HTTP response, including status code, headers, and body. This is crucial for returning appropriate HTTP statuses (e.g., `200 OK`, `201 Created`, `204 No Content`, `404 Not Found`).

---

### **8. Configuration (`application.properties`)**

This file contains application-specific settings, including database configuration.

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
spring.jpa.hibernate.ddl-auto=update # Creates/updates schema automatically. Use 'create' for fresh start, 'none' for production.
spring.jpa.show-sql=true # Logs SQL statements to the console
spring.jpa.properties.hibernate.format_sql=true # Formats SQL statements for readability

# Logging configuration (optional, for more detailed logs)
# logging.level.org.springframework.web=DEBUG
# logging.level.org.hibernate.SQL=DEBUG
# logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
@@@@
**Explanation:**
*   `server.port`: Specifies the port on which the application will run.
*   `spring.h2.console.enabled=true`: Enables the H2 database console, accessible at `/h2-console`.
*   `spring.datasource.url`: Defines the connection URL for the H2 in-memory database. `jdbc:h2:mem:testdb` means an in-memory database named `testdb`.
*   `spring.jpa.hibernate.ddl-auto=update`: Hibernate (the JPA provider) will automatically update the database schema based on your entities. For production, you'd typically set this to `none` and use migration tools like Flyway or Liquibase.
*   `spring.jpa.show-sql=true`: Prints SQL queries to the console.

---

### **9. Unit Tests**

#### **ProductService Test**

Tests the business logic in the `ProductService`. Uses Mockito to mock the `ProductRepository`.

$$$$ src/test/java/com/example/demo/service
&&&& ProductServiceTest.java
@@@@
package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class) // Initializes Mockito mocks
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach // Runs before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop for work and gaming");
        product2 = new Product(2L, "Mouse", 25.00, "Ergonomic wireless mouse");
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        verify(productRepository, times(1)).findAll(); // Verifies that findAll was called once
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(3L);
        });

        String expectedMessage = "Product not found with id: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1);

        assertNotNull(createdProduct);
        assertEquals("Laptop", createdProduct.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testUpdateProductFound() {
        Product updatedDetails = new Product(1L, "Gaming Laptop", 1500.00, "High-performance gaming laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails); // Mock save returns the updated product

        Product result = productService.updateProduct(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getName());
        assertEquals(1500.00, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedDetails = new Product(3L, "Non-existent Product", 100.00, "Description");
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(3L, updatedDetails);
        });

        assertTrue(exception.getMessage().contains("Product not found with id: 3"));
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).save(any(Product.class)); // Ensure save is not called
    }

    @Test
    void testDeleteProductFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(3L)).thenReturn(false);

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(3L);
        });

        assertTrue(exception.getMessage().contains("Product not found with id: 3"));
        verify(productRepository, times(1)).existsById(3L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure deleteById is not called
    }
}
@@@@
**Explanation:**
*   `@ExtendWith(MockitoExtension.class)`: Integrates JUnit 5 with Mockito.
*   `@Mock`: Creates a mock object for `ProductRepository`. This mock will simulate the behavior of the real repository, allowing us to test the `ProductService` in isolation.
*   `@InjectMocks`: Injects the mock `ProductRepository` into `ProductService`.
*   `@BeforeEach`: Sets up common test data before each test method runs.
*   `when().thenReturn()`: Configures the behavior of the mock repository.
*   `verify()`: Checks if specific methods on the mock object were called.
*   `assertThrows()`: JUnit 5 assertion to verify that a specific exception is thrown.

---

#### **ProductController Test**

Tests the REST endpoints of the `ProductController`. Uses Spring's `MockMvc` for simulating HTTP requests without starting a full server.

$$$$ src/test/java/com/example/demo/controller
&&&& ProductControllerTest.java
@@@@
package com.example.demo.controller;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(ProductController.class) // Focuses on testing the ProductController by disabling full auto-configuration
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Provides methods for sending HTTP requests to the controller

    @MockBean // Creates a mock bean for ProductService in the Spring application context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert Java objects to JSON strings and vice-versa

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop");
        product2 = new Product(2L, "Mouse", 25.00, "Wireless mouse");
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products") // Perform a GET request
                        .contentType(MediaType.APPLICATION_JSON)) // Set content type
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Expect two items in the JSON array
                .andExpect(jsonPath("$[0].name").value("Laptop")) // Verify data of the first item
                .andExpect(jsonPath("$[1].name").value("Mouse"));
        
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductByIdFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product1);

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(anyLong())).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    void testCreateProduct() throws Exception {
        Product newProduct = new Product(null, "Keyboard", 75.00, "Mechanical keyboard");
        Product savedProduct = new Product(3L, "Keyboard", 75.00, "Mechanical keyboard");
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))) // Convert object to JSON string
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Keyboard"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProductDetails = new Product(1L, "Gaming Laptop", 1500.00, "High-end gaming laptop");
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProductDetails);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gaming Laptop"))
                .andExpect(jsonPath("$.price").value(1500.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        Product updatedProductDetails = new Product(99L, "Non-existent", 100.00, "Description");
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new ProductNotFoundException("Product not found with id: 99"));

        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong()); // Mock void method

        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        doThrow(new ProductNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation:**
*   `@WebMvcTest(ProductController.class)`: This annotation configures a Spring application context for testing a specific controller. It disables full auto-configuration and only scans for components relevant to web layers (controllers, filters, etc.).
*   `MockMvc`: A powerful tool for testing Spring MVC controllers without needing to deploy the application to a server. It simulates HTTP requests and allows assertions on the response.
*   `@MockBean`: Creates a mock instance of `ProductService` and adds it to the Spring application context as a bean. This replaces the actual `ProductService` with our mock for testing purposes.
*   `ObjectMapper`: A Jackson library class used for serializing and deserializing JSON. Essential for converting Java objects to JSON for request bodies and back again for assertions.
*   `mockMvc.perform()`: Initiates an HTTP request.
*   `andExpect()`: Used to assert expectations on the response (e.g., status code, content type, JSON payload).
*   `jsonPath("$[0].name").value("Laptop")`: Uses JSONPath expressions to navigate and assert values within the JSON response body.
*   `doNothing().when()`: Used to mock void methods.
*   `doThrow().when()`: Used to mock methods that throw exceptions.

---

### **How to Run This Project:**

1.  **Save the files:** Create the folder structure as specified and save each code block into its respective file.
2.  **Open in IDE:** Import the project into an IDE like IntelliJ IDEA or Eclipse as a Maven project.
3.  **Run `DemoApplication.java`:** Execute the `main` method in `DemoApplication.java`.
4.  **Access H2 Console:** Once the application starts, you can access the H2 database console at `http://localhost:8080/h2-console`. Use JDBC URL `jdbc:h2:mem:testdb`, username `sa`, and password `password`.
5.  **Test Endpoints (e.g., with Postman or curl):**
    *   **Create Product (POST):**
        ```
        POST http://localhost:8080/api/products
        Content-Type: application/json
        
        {
            "name": "Smartphone",
            "price": 699.99,
            "description": "Latest model smartphone"
        }
        ```
    *   **Get All Products (GET):**
        ```
        GET http://localhost:8080/api/products
        ```
    *   **Get Product by ID (GET):**
        ```
        GET http://localhost:8080/api/products/1
        ```
    *   **Update Product (PUT):**
        ```
        PUT http://localhost:8080/api/products/1
        Content-Type: application/json
        
        {
            "id": 1,
            "name": "Premium Smartphone",
            "price": 749.99,
            "description": "Updated latest model smartphone with extra features"
        }
        ```
    *   **Delete Product (DELETE):**
        ```
        DELETE http://localhost:8080/api/products/1
        ```
6.  **Run Tests:** In your IDE, you can run all tests or individual test classes/methods from `ProductServiceTest.java` and `ProductControllerTest.java`.

This template provides a solid foundation for building a robust Spring Boot REST API! Let me know if you need any further customization or have more questions.