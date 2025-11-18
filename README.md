Hello there! I'd be happy to provide you with a comprehensive Spring Boot REST API code template. This template will cover the core components of a typical RESTful service, including a data model, repository, service layer, and controller, along with unit tests and explanations.

---

### **Getting Started with Spring Boot REST API Template**

This template demonstrates a basic CRUD (Create, Read, Update, Delete) REST API for managing `Product` resources.

**How to Start a Spring Boot Project (Recommended):**

1.  **Spring Initializr**: The easiest way to start is by using the [Spring Initializr](https://start.spring.io/).
    *   **Project**: Maven Project
    *   **Language**: Java
    *   **Spring Boot**: (Choose a stable version, e.g., 3.x.x)
    *   **Group**: `com.example`
    *   **Artifact**: `productmanagement` (This will be our project name)
    *   **Package Name**: `com.example.productmanagement`
    *   **Dependencies**: Add the following:
        *   `Spring Web` (for building RESTful applications)
        *   `Spring Data JPA` (for database interaction)
        *   `H2 Database` (an in-memory database for development/testing; you can swap this for PostgreSQL, MySQL, etc., later)
        *   `Lombok` (optional, but highly recommended for reducing boilerplate code)
        *   `Spring Boot DevTools` (for faster development cycles)
    *   Click "Generate" and download the project. Unzip it into your desired directory.

2.  **IDE Import**: Import the project into your favorite IDE (IntelliJ IDEA, VS Code with Java extensions, Eclipse STS).

Now, let's look at the code structure.

---

### **Project Structure and Code**

Here's the detailed structure and code for our `ProductManagementApp`:

#### **1. Project Object Model (`pom.xml`)**

This file defines project dependencies and build configurations.

`$$$$ `
`&&&& pom.xml`
`@@@@`
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.5</version> <!-- Use your desired Spring Boot version -->
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>productmanagement</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>productmanagement</name>
	<description>Product Management REST API</description>

	<properties>
		<java.version>17</java.version> <!-- Use your desired Java version -->
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

		<!-- Database (H2 for development/testing) -->
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>

		<!-- Lombok (Optional, but highly recommended for boilerplate reduction) -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

		<!-- Development Tools -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>

		<!-- Testing Dependencies -->
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
`@@@@`
**Explanation:**
*   **`spring-boot-starter-parent`**: Provides default configurations and dependency management for Spring Boot projects.
*   **`spring-boot-starter-web`**: Includes Tomcat and Spring MVC, essential for building web applications and REST APIs.
*   **`spring-boot-starter-data-jpa`**: Integrates Spring Data JPA and Hibernate for easy database interaction.
*   **`h2`**: An in-memory database, perfect for rapid development and testing without needing a separate database server.
*   **`lombok`**: Generates boilerplate code like getters, setters, constructors, etc., at compile time, making your code cleaner.
*   **`spring-boot-starter-test`**: Provides a comprehensive set of testing libraries, including JUnit, Mockito, and Spring Test.

#### **2. Main Application Class**

This is the entry point of your Spring Boot application.

`$$$$ src/main/java/com/example/productmanagement`
`&&&& ProductManagementApplication.java`
`@@@@`
package com.example.productmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductManagementApplication.class, args);
	}

}
`@@@@`
**Explanation:**
*   **`@SpringBootApplication`**: This is a convenience annotation that combines three important annotations:
    *   `@Configuration`: Tags the class as a source of bean definitions for the application context.
    *   `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
    *   `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com.example.productmanagement` package, allowing it to find our controllers, services, and repositories.

#### **3. Model (Entity)**

Represents the data structure for a `Product` and maps to a database table.

`$$$$ src/main/java/com/example/productmanagement/model`
`&&&& Product.java`
`@@@@`
package com.example.productmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Marks this class as a JPA entity, meaning it maps to a database table.
@Data // From Lombok: Automatically generates getters, setters, toString(), equals(), and hashCode().
@NoArgsConstructor // From Lombok: Generates a no-argument constructor.
@AllArgsConstructor // From Lombok: Generates a constructor with all fields as arguments.
public class Product {

    @Id // Marks this field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key to be auto-generated by the database.
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;

}
`@@@@`
**Explanation:**
*   **`@Entity`**: A JPA annotation indicating that this class is a database entity.
*   **`@Data` (Lombok)**: Reduces boilerplate by generating standard methods like `getters`, `setters`, `equals`, `hashCode`, and `toString`.
*   **`@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)**: Generate constructors, which are useful for JPA and object creation.
*   **`@Id`**: Marks `id` as the primary key.
*   **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Specifies that the primary key value will be generated automatically by the database (e.g., auto-increment).

#### **4. Custom Exception**

A simple exception for handling cases where a resource is not found.

`$$$$ src/main/java/com/example/productmanagement/exception`
`&&&& ResourceNotFoundException.java`
`@@@@`
package com.example.productmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Sets the HTTP status code to 404 Not Found when this exception is thrown.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
`@@@@`
**Explanation:**
*   **`@ResponseStatus(HttpStatus.NOT_FOUND)`**: This annotation tells Spring to respond with an HTTP 404 (Not Found) status code when this exception is thrown from a controller method. This provides a clear error signal to API clients.

#### **5. Repository Interface**

Handles data access operations for the `Product` entity.

`$$$$ src/main/java/com/example/productmanagement/repository`
`&&&& ProductRepository.java`
`@@@@`
package com.example.productmanagement.repository;

import com.example.productmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository.
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides out-of-the-box CRUD operations:
    // save(), findById(), findAll(), deleteById(), etc.

    // You can also define custom query methods if needed, e.g.:
    Optional<Product> findByName(String name);
}
`@@@@`
**Explanation:**
*   **`@Repository`**: A stereotype annotation indicating that the class provides the mechanism for storage, retrieval, search, update, and delete operation on objects. It's primarily used for component scanning.
*   **`JpaRepository<Product, Long>`**: This interface from Spring Data JPA provides generic CRUD operations.
    *   `Product`: The entity type it manages.
    *   `Long`: The type of the entity's primary key (`id`).
*   By extending `JpaRepository`, we automatically get methods like `save()`, `findById()`, `findAll()`, `deleteById()`, etc., without writing any implementation.
*   **`Optional<Product> findByName(String name);`**: An example of a custom query method. Spring Data JPA can automatically generate the implementation for this method based on its name.

#### **6. Service Layer**

Contains the business logic. It orchestrates operations between the controller and repository.

##### **Service Interface**

`$$$$ src/main/java/com/example/productmanagement/service`
`&&&& ProductService.java`
`@@@@`
package com.example.productmanagement.service;

import com.example.productmanagement.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
}
`@@@@`
**Explanation:**
*   Defines the contract for product-related business operations. Separating the interface from the implementation allows for easier testing and changing implementations without affecting other parts of the application.

##### **Service Implementation**

`$$$$ src/main/java/com/example/productmanagement/service/impl`
`&&&& ProductServiceImpl.java`
`@@@@`
package com.example.productmanagement.service.impl;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a service component, allowing Spring to manage it.
@Transactional // Ensures that all methods in this service are executed within a transaction.
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // Constructor injection is preferred over @Autowired on fields for better testability.
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
`@@@@`
**Explanation:**
*   **`@Service`**: A stereotype annotation indicating that this class holds the business logic.
*   **`@Transactional`**: Ensures that all methods within this service class are executed within a database transaction. If any operation within a method fails, the entire transaction is rolled back.
*   **Constructor Injection**: The `ProductRepository` is injected via the constructor, which is considered a best practice for dependency injection as it makes the dependencies explicit and improves testability.
*   **`ResourceNotFoundException`**: Used to signal when a product is not found, providing a consistent error response through the `@ResponseStatus` annotation on the exception class.

#### **7. Controller Layer**

Exposes RESTful endpoints for clients to interact with the API.

`$$$$ src/main/java/com/example/productmanagement/controller`
`&&&& ProductController.java`
`@@@@`
package com.example.productmanagement.controller;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller, combining @Controller and @ResponseBody.
@RequestMapping("/api/products") // Base URI for all endpoints in this controller.
public class ProductController {

    private final ProductService productService;

    // Constructor injection
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products: GET /api/products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // GET product by ID: GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // CREATE a new product: POST /api/products
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // UPDATE an existing product: PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // DELETE a product: DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
`@@@@`
**Explanation:**
*   **`@RestController`**: Combines `@Controller` and `@ResponseBody`. `@Controller` makes the class a Spring MVC controller, and `@ResponseBody` automatically serializes the return value of methods into JSON/XML, making it suitable for REST APIs.
*   **`@RequestMapping("/api/products")`**: Defines the base URI for all endpoints in this controller. All methods will be mapped under `/api/products`.
*   **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`**: These are convenience annotations for mapping HTTP GET, POST, PUT, and DELETE requests, respectively.
*   **`@PathVariable`**: Binds a method parameter to a URI template variable (e.g., `{id}`).
*   **`@RequestBody`**: Binds the HTTP request body to a method parameter. Spring automatically deserializes the JSON/XML request body into a `Product` object.
*   **`ResponseEntity`**: A flexible class that allows you to control the entire HTTP response, including status code, headers, and body.
*   **`HttpStatus`**: Provides HTTP status codes (e.g., `OK`, `CREATED`, `NO_CONTENT`, `NOT_FOUND`).

#### **8. Configuration (`application.properties`)**

Contains application-specific settings.

`$$$$ src/main/resources`
`&&&& application.properties`
`@@@@`
# Server configuration
server.port=8080

# H2 Database configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update # 'update' will create/update schema; 'create-drop' for testing, 'validate' for production.
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.springframework.web=INFO
logging.level.com.example.productmanagement=DEBUG
`@@@@`
**Explanation:**
*   **`server.port`**: Specifies the port on which the application will run.
*   **`spring.h2.console.enabled=true`**: Enables the H2 database web console, accessible at `http://localhost:8080/h2-console`.
*   **`spring.datasource.url`**: Configures an in-memory H2 database named `productdb`. `DB_CLOSE_DELAY=-1` keeps the database open as long as the JVM is running.
*   **`spring.jpa.hibernate.ddl-auto=update`**: Controls schema generation. `update` attempts to update the schema to match the entities. For production, `validate` or `none` is often preferred, with schema changes managed manually or via migration tools (e.g., Flyway, Liquibase). `create-drop` is useful for testing, creating a fresh schema on startup and dropping it on shutdown.
*   **`spring.jpa.show-sql=true`**: Logs SQL queries to the console.
*   **`logging.level.*`**: Configures logging levels for different packages.

---

### **Unit Test Cases**

Writing unit tests is crucial for ensuring the correctness and maintainability of your code. We'll use JUnit 5 and Mockito.

#### **9. Service Unit Test**

Tests the business logic of `ProductService` in isolation.

`$$$$ src/test/java/com/example/productmanagement/service`
`&&&& ProductServiceTest.java`
`@@@@`
package com.example.productmanagement.service;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.impl.ProductServiceImpl;
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

@ExtendWith(MockitoExtension.class) // Initializes mocks for JUnit 5
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductServiceImpl
    private ProductServiceImpl productService; // Use the implementation class here

    private Product product1;
    private Product product2;

    @BeforeEach // Set up common test data before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @DisplayName("Test - Get All Products")
    @Test
    void givenProductsExist_whenGetAllProducts_thenReturnProductList() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<Product> products = productService.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @DisplayName("Test - Get Product by ID (Found)")
    @Test
    void givenProductId_whenGetProductById_thenReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        Optional<Product> foundProduct = productService.getProductById(1L);

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @DisplayName("Test - Get Product by ID (Not Found)")
    @Test
    void givenInvalidProductId_whenGetProductById_thenReturnEmptyOptional() {
        // Given
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        // When
        Optional<Product> foundProduct = productService.getProductById(3L);

        // Then
        assertThat(foundProduct).isEmpty();
        verify(productRepository, times(1)).findById(3L);
    }

    @DisplayName("Test - Create Product")
    @Test
    void givenProductObject_whenCreateProduct_thenReturnSavedProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 75.00, 20);
        when(productRepository.save(any(Product.class))).thenReturn(product1); // Mock save to return a product with ID

        // When
        Product createdProduct = productService.createProduct(newProduct);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isEqualTo(1L); // Expect ID from mocked return
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("Test - Update Product (Found)")
    @Test
    void givenProductIdAndProductDetails_whenUpdateProduct_thenReturnUpdatedProduct() {
        // Given
        Product updatedDetails = new Product(null, "Laptop Pro", "Gaming Laptop", 1500.00, 8);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails); // Mock save to return updated product

        // When
        Product updatedProduct = productService.updateProduct(1L, updatedDetails);

        // Then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getName()).isEqualTo("Laptop Pro");
        assertThat(updatedProduct.getPrice()).isEqualTo(1500.00);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("Test - Update Product (Not Found) - Throws Exception")
    @Test
    void givenInvalidProductId_whenUpdateProduct_thenThrowResourceNotFoundException() {
        // Given
        Product updatedDetails = new Product(null, "NonExistent", "Details", 100.00, 1);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updatedDetails));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class)); // Ensure save is not called
    }

    @DisplayName("Test - Delete Product (Found)")
    @Test
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

    @DisplayName("Test - Delete Product (Not Found) - Throws Exception")
    @Test
    void givenInvalidProductId_whenDeleteProduct_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure delete is not called
    }
}
`@@@@`
**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5.
*   **`@Mock`**: Used to create mock objects for dependencies (here, `ProductRepository`). Mock objects simulate the behavior of real objects.
*   **`@InjectMocks`**: Injects the mock objects into the instance of the class under test (here, `ProductServiceImpl`).
*   **`@BeforeEach`**: A JUnit annotation to execute a setup method before each test method, ensuring a fresh state.
*   **`when().thenReturn()`**: Mockito's way of defining the behavior of mock objects. For example, `when(productRepository.findAll()).thenReturn(...)` tells the mock repository to return a specific list when its `findAll()` method is called.
*   **`verify()`**: Mockito's way of verifying that specific methods on a mock object were called (and how many times).
*   **`assertThat` (AssertJ)**: A fluent assertion library that provides more readable and expressive assertions.
*   **`assertThrows` (JUnit)**: Used to test that a specific exception is thrown.
*   **`doNothing().when()`**: Used to mock void methods.

#### **10. Controller Unit Test**

Tests the endpoints of `ProductController` using `@WebMvcTest`.

`$$$$ src/test/java/com/example/productmanagement/controller`
`&&&& ProductControllerTest.java`
`@@@@`
package com.example.productmanagement.controller;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.service.ProductService;
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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Configures Spring Boot to test only the ProductController.
class ProductControllerTest {

    @Autowired // Auto-injects MockMvc, which allows simulating HTTP requests.
    private MockMvc mockMvc;

    @MockBean // Creates a mock of ProductService and adds it to the Spring context.
    private ProductService productService;

    @Autowired // Auto-injects ObjectMapper to convert Java objects to JSON and vice versa.
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @DisplayName("Test - Get All Products API")
    @Test
    void givenProducts_whenGetAllProducts_thenReturnJsonArray() throws Exception {
        // Given
        given(productService.getAllProducts()).willReturn(Arrays.asList(product1, product2));

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Prints request and response details to console
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()", is(2))) // Expect JSON array of size 2
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].name", is("Mouse")));
    }

    @DisplayName("Test - Get Product by ID API (Found)")
    @Test
    void givenProductId_whenGetProductById_thenReturnJsonProduct() throws Exception {
        // Given
        given(productService.getProductById(1L)).willReturn(Optional.of(product1));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1200.00)));
    }

    @DisplayName("Test - Get Product by ID API (Not Found)")
    @Test
    void givenInvalidProductId_whenGetProductById_thenReturnNotFound() throws Exception {
        // Given
        given(productService.getProductById(99L)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @DisplayName("Test - Create Product API")
    @Test
    void givenProductObject_whenCreateProduct_thenReturnSavedProduct() throws Exception {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 75.00, 20);
        given(productService.createProduct(any(Product.class))).willReturn(new Product(3L, "Keyboard", "Mechanical keyboard", 75.00, 20));

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))) // Convert Product object to JSON string
                .andDo(print())
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name", is("Keyboard")))
                .andExpect(jsonPath("$.id", is(3)));
    }

    @DisplayName("Test - Update Product API")
    @Test
    void givenUpdatedProduct_whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = new Product(1L, "Laptop Pro", "Gaming Laptop", 1500.00, 8);
        given(productService.updateProduct(anyLong(), any(Product.class))).willReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is("Laptop Pro")))
                .andExpect(jsonPath("$.price", is(1500.00)));
    }

    @DisplayName("Test - Update Product API (Not Found)")
    @Test
    void givenInvalidProductId_whenUpdateProduct_thenReturnNotFound() throws Exception {
        // Given
        Product nonExistentProduct = new Product(99L, "NonExistent", "Details", 100.00, 1);
        given(productService.updateProduct(anyLong(), any(Product.class)))
                .willThrow(new ResourceNotFoundException("Product not found with id: " + 99L));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentProduct)))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @DisplayName("Test - Delete Product API")
    @Test
    void givenProductId_whenDeleteProduct_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @DisplayName("Test - Delete Product API (Not Found)")
    @Test
    void givenInvalidProductId_whenDeleteProduct_thenReturnNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id: " + 99L))
                .when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }
}
`@@@@`
**Explanation:**
*   **`@WebMvcTest(ProductController.class)`**: This annotation focuses on testing the Spring MVC layer. It loads only the necessary components for the `ProductController`, excluding other parts like services and repositories.
*   **`@Autowired MockMvc`**: `MockMvc` allows you to send HTTP requests to the controller and assert the responses without starting a full HTTP server.
*   **`@MockBean ProductService`**: Instead of `@Mock`, `@MockBean` is used in Spring Boot tests. It creates a Mockito mock for `ProductService` and adds it to the Spring application context, replacing any actual `ProductService` bean. This ensures that when `ProductController` tries to inject `ProductService`, it gets our mock.
*   **`@Autowired ObjectMapper`**: Used to convert Java objects to JSON strings (for request bodies) and vice versa.
*   **`given().willReturn()` (BDDMockito)**: A more readable syntax for `when().thenReturn()` in behavior-driven development (BDD) style tests.
*   **`mockMvc.perform(...)`**: Simulates an HTTP request (GET, POST, PUT, DELETE).
*   **`.andDo(print())`**: Prints the request and response details, useful for debugging.
*   **`.andExpect(status().isOk())`, `.andExpect(status().isCreated())`, etc.**: Assertions on the HTTP status code of the response.
*   **`.andExpect(jsonPath("$.name", is("Laptop")))`**: Assertions on the JSON response body using JsonPath expressions.

---

### **How to Run and Test the Application**

1.  **Build**: Open a terminal in the project root directory and run:
    ```bash
    ./mvnw clean install
    ```
2.  **Run**: Execute the main application class:
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

3.  **H2 Console**: Access the in-memory H2 database console at `http://localhost:8080/h2-console`.
    *   **JDBC URL**: `jdbc:h2:mem:productdb`
    *   **User Name**: `sa`
    *   **Password**: `password`
    You can see the `PRODUCT` table created by JPA.

4.  **Test with cURL or Postman/Insomnia**:

    *   **Create Product (POST)**
        ```bash
        curl -X POST http://localhost:8080/api/products \
        -H "Content-Type: application/json" \
        -d '{"name": "Smartphone", "description": "Latest model smartphone", "price": 899.99, "quantity": 15}'
        ```
        (Expected: HTTP 201 Created, returns the created product with ID)

    *   **Get All Products (GET)**
        ```bash
        curl http://localhost:8080/api/products
        ```
        (Expected: HTTP 200 OK, returns a list of products)

    *   **Get Product by ID (GET)** (Use an ID from a created product, e.g., 1)
        ```bash
        curl http://localhost:8080/api/products/1
        ```
        (Expected: HTTP 200 OK, returns the product with ID 1)

    *   **Update Product (PUT)** (Use an ID from a created product)
        ```bash
        curl -X PUT http://localhost:8080/api/products/1 \
        -H "Content-Type: application/json" \
        -d '{"id": 1, "name": "Smartphone X", "description": "Updated model", "price": 949.99, "quantity": 12}'
        ```
        (Expected: HTTP 200 OK, returns the updated product)

    *   **Delete Product (DELETE)** (Use an ID from a created product)
        ```bash
        curl -X DELETE http://localhost:8080/api/products/1
        ```
        (Expected: HTTP 204 No Content)

5.  **Run Unit Tests**:
    ```bash
    ./mvnw test
    ```
    This will execute all test files in the `src/test/java` directory.

---

This template provides a solid foundation for building Spring Boot REST APIs. Feel free to expand upon it with more complex features, security, DTOs, validation, and advanced error handling as your application grows!