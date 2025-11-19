Here's a comprehensive code template for a Spring Boot REST API, including a basic CRUD example for a `Product` entity, along with unit tests and explanations.

This example uses:
*   **Spring Web**: For building RESTful endpoints.
*   **Spring Data JPA**: For data access with an ORM.
*   **H2 Database**: An in-memory database for easy setup and testing.
*   **Lombok**: To reduce boilerplate code (getters, setters, constructors).

Let's start with the `pom.xml` which defines all necessary dependencies.

$$$$
&&&&pom.xml
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
        <!-- Spring Boot Starter Web: Includes Tomcat and Spring MVC -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Starter Data JPA: Includes Hibernate and Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database: An in-memory database for development and testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok: Reduces boilerplate code like getters, setters, constructors -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Starter Test: Includes JUnit, Mockito, Spring Test -->
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
This `pom.xml` defines the project's dependencies and build configuration.
*   `spring-boot-starter-parent`: Provides Spring Boot defaults for Maven, including dependency management.
*   `spring-boot-starter-web`: Essential for building web applications, includes embedded Tomcat and Spring MVC.
*   `spring-boot-starter-data-jpa`: Provides everything needed to use JPA with Hibernate, including Spring Data JPA for easy repository creation.
*   `h2`: An in-memory database used for development and testing. It's easy to set up and doesn't require an external database server. `scope` is `runtime` because it's typically only needed at runtime, not compile time.
*   `lombok`: A library that automatically plugs into your build process to generate boilerplate code (like getters, setters, constructors, `equals`, `hashCode`, `toString`) at compile time, keeping your source code clean.
*   `spring-boot-starter-test`: Contains all necessary libraries for testing Spring Boot applications, including JUnit 5, Mockito, and Spring Test.

---

Next, the main application class.

$$$$src/main/java/com/example/demo
&&&&DemoApplication.java
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
`@SpringBootApplication` is a convenience annotation that combines three common Spring Boot annotations:
1.  `@Configuration`: Tags the class as a source of bean definitions for the application context.
2.  `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
3.  `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com.example.demo` package, allowing it to find and register controllers, services, repositories, etc.
The `main` method uses `SpringApplication.run()` to launch the Spring Boot application.

---

Configure the application properties for the H2 database.

$$$$src/main/resources
&&&&application.properties
@@@@
# H2 Database Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update # options: none, update, create, create-drop
spring.jpa.show-sql=true # Log SQL queries
spring.jpa.properties.hibernate.format_sql=true # Format SQL queries in logs
@@@@
**Explanation:**
*   `spring.h2.console.enabled=true`: Enables the H2 database console, accessible via your browser.
*   `spring.h2.console.path=/h2-console`: Sets the URL path for the H2 console.
*   `spring.datasource.url=jdbc:h2:mem:testdb`: Configures an in-memory H2 database named `testdb`. This means data will be lost when the application restarts.
*   `spring.datasource.driverClassName`, `username`, `password`: Standard database connection properties.
*   `spring.jpa.database-platform`: Specifies the Hibernate dialect for H2.
*   `spring.jpa.hibernate.ddl-auto=update`: Hibernate's DDL (Data Definition Language) auto-generation strategy. `update` means it will update the schema if entities change. Other common options are `create-drop` (creates and drops on startup/shutdown, good for tests), `create` (creates on startup), and `none` (no DDL generation, useful for production with managed schemas).
*   `spring.jpa.show-sql=true`, `spring.jpa.properties.hibernate.format_sql=true`: Configures Hibernate to log SQL statements to the console and format them for readability.

---

Define the `Product` entity (the data model).

$$$$src/main/java/com/example/demo/model
&&&&Product.java
@@@@
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products") // Optional: specifies table name if different from class name
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;

    @Column(nullable = false) // Not null constraint
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;
}
@@@@
**Explanation:**
*   `@Entity`: Marks this class as a JPA entity, meaning it maps to a table in the database.
*   `@Table(name = "products")`: Specifies the database table name. If omitted, the table name defaults to the class name (`Product`).
*   `@Data`: (Lombok) Generates all the boilerplate: getters for all fields, setters for all non-final fields, `equals()` and `hashCode()` implementations, and a `toString()` method.
*   `@NoArgsConstructor`: (Lombok) Generates a constructor with no arguments. Required by JPA.
*   `@AllArgsConstructor`: (Lombok) Generates a constructor with arguments for all fields. Useful for creating instances.
*   `@Id`: Marks the `id` field as the primary key of the entity.
*   `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Configures the primary key to be automatically generated by the database (e.g., auto-increment in MySQL/H2).
*   `@Column(nullable = false)`: Specifies that the `name`, `price`, and `quantity` columns cannot be null in the database.

---

Define the `ProductDTO` (Data Transfer Object) for request/response bodies. This helps in separating the internal entity structure from external API representation and validation.

$$$$src/main/java/com/example/demo/dto
&&&&ProductDTO.java
@@@@
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for Product representation (request/response)
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class ProductDTO {
    private Long id; // ID might be null for creation requests
    private String name;
    private String description;
    private double price;
    private int quantity;
}
@@@@
**Explanation:**
*   `ProductDTO` is a simple POJO used to transfer data between the client and the server. It's good practice to use DTOs to:
    *   **Decouple API from Entity**: Changes to the internal `Product` entity (e.g., adding internal fields not meant for the client) don't directly affect the API contract.
    *   **Control Data Exposure**: You can expose only specific fields or combine fields from multiple entities.
    *   **Validation**: Validation annotations can be applied here without cluttering the entity.
*   Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) are used again for brevity.

---

Define the `ProductRepository` for data access.

$$$$src/main/java/com/example/demo/repository
&&&&ProductRepository.java
@@@@
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Optional: indicates that this is a DAO component, though JpaRepository usually implies it.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query methods (Spring Data JPA generates implementation automatically)
    Optional<Product> findByName(String name);
    List<Product> findByPriceGreaterThan(double price);
}
@@@@
**Explanation:**
*   `@Repository`: A specialization of `@Component` that indicates that an annotated class is a "Repository". This is a marker for Spring's component scanning.
*   `extends JpaRepository<Product, Long>`: This is the core of Spring Data JPA. By extending `JpaRepository`, you automatically get CRUD (Create, Read, Update, Delete) operations and pagination/sorting capabilities for the `Product` entity with `Long` as its primary key type.
*   **Custom Query Methods**: Spring Data JPA allows you to define custom query methods by simply naming them according to specific conventions (e.g., `findByName`, `findByPriceGreaterThan`). Spring Data JPA will automatically generate the implementation for these methods based on the property names of your `Product` entity.

---

Define the `ProductService` for business logic.

$$$$src/main/java/com/example/demo/service
&&&&ProductService.java
@@@@
package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired // Inject ProductRepository
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create a new product
    public Product createProduct(Product product) {
        // Here you could add more business logic, validation, etc.
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Update an existing product
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        product.setName(productDetails.getName());
        product.setDescription(productDetails.setDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());

        return productRepository.save(product);
    }

    // Delete a product
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    // You can add more specific business logic here, e.g., check stock, apply discounts, etc.
}
@@@@
**Explanation:**
*   `@Service`: Marks this class as a Spring service component. It contains business logic and acts as an intermediary between the controller and the repository.
*   `@Autowired`: Used for dependency injection. Spring automatically provides an instance of `ProductRepository` when `ProductService` is created. Constructor injection is generally preferred for mandatory dependencies.
*   **CRUD Operations**: Methods for creating, retrieving, updating, and deleting products are defined.
*   `ResourceNotFoundException`: A custom exception is used to handle cases where a product with a given ID is not found. This will be caught by a global exception handler later if implemented (not in this template for brevity, but a good practice).

---

Create a custom exception for better error handling.

$$$$src/main/java/com/example/demo/exception
&&&&ResourceNotFoundException.java
@@@@
package com.example.demo.exception;

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
**Explanation:**
*   `@ResponseStatus(HttpStatus.NOT_FOUND)`: This annotation tells Spring to automatically return an HTTP 404 Not Found status code when this exception is thrown from a controller method. This simplifies error handling and provides a clearer API response.
*   `extends RuntimeException`: Makes it an unchecked exception, meaning it doesn't need to be declared in method signatures.

---

Define the `ProductController` (REST API endpoints).

$$$$src/main/java/com/example/demo/controller
&&&&ProductController.java
@@@@
package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired // Inject ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Helper method to convert Entity to DTO
    private ProductDTO convertToDto(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity());
    }

    // Helper method to convert DTO to Entity (for creation/update)
    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId()); // ID might be null for new products
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        return product;
    }

    // GET all products
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products); // Returns 200 OK with list of products
    }

    // GET product by ID
    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok) // Returns 200 OK with the product
                .orElse(ResponseEntity.notFound().build()); // Returns 404 Not Found if product doesn't exist
    }

    // Create a new product
    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(convertToDto(createdProduct), HttpStatus.CREATED); // Returns 201 CREATED
    }

    // Update an existing product
    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product productDetails = convertToEntity(productDTO);
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(convertToDto(updatedProduct)); // Returns 200 OK
    }

    // Delete a product
    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
@@@@
**Explanation:**
*   `@RestController`: Combines `@Controller` and `@ResponseBody`. `@Controller` makes it a Spring MVC controller, and `@ResponseBody` means the return value of the methods should be bound directly to the web response body.
*   `@RequestMapping("/api/products")`: Sets the base path for all endpoints in this controller to `/api/products`.
*   `@Autowired`: Injects `ProductService`.
*   **`convertToDto` and `convertToEntity` methods**: These are helper methods to map between the `Product` entity and `ProductDTO`. This is crucial when separating your domain model from your API model. For larger applications, you might use a library like ModelMapper or MapStruct for this.
*   `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: Annotations to map HTTP GET, POST, PUT, and DELETE requests to specific handler methods.
*   `@PathVariable`: Binds a URI template variable (e.g., `{id}`) to a method parameter.
*   `@RequestBody`: Binds the HTTP request body to a method parameter (automatically deserializes JSON/XML to `ProductDTO`).
*   `ResponseEntity`: A class that represents the entire HTTP response (status code, headers, and body). It allows for fine-grained control over the response.
    *   `ResponseEntity.ok()`: Returns an HTTP 200 OK status.
    *   `ResponseEntity.notFound().build()`: Returns an HTTP 404 Not Found status.
    *   `new ResponseEntity<>(body, HttpStatus.CREATED)`: Returns an HTTP 201 Created status.
    *   `ResponseEntity.noContent().build()`: Returns an HTTP 204 No Content status.

---

### Unit Test Cases

Now, let's create unit tests for the Service and Controller layers.

#### ProductService Test

$$$$src/test/java/com/example/demo/service
&&&&ProductServiceTest.java
@@@@
package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product created = productService.createProduct(product1);

        assertNotNull(created);
        assertEquals("Laptop", created.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> found = productService.getProductById(1L);

        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> found = productService.getProductById(99L);

        assertFalse(found.isPresent());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateProductSuccess() {
        Product updatedDetails = new Product(1L, "Laptop Pro", "New powerful laptop", 1500.00, 8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails);

        Product updated = productService.updateProduct(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Laptop Pro", updated.getName());
        assertEquals(1500.00, updated.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1); // product1 is updated and saved
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedDetails = new Product(99L, "NonExistent", "Desc", 100.0, 1);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updatedDetails));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(product1);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
@@@@
**Explanation:**
*   `@ExtendWith(MockitoExtension.class)`: Integrates Mockito with JUnit 5, allowing annotations like `@Mock` and `@InjectMocks`.
*   `@Mock private ProductRepository productRepository;`: Creates a mock instance of `ProductRepository`. We don't want to hit a real database during unit tests, so we mock its behavior.
*   `@InjectMocks private ProductService productService;`: Creates an instance of `ProductService` and injects the `@Mock` instances (like `productRepository`) into it.
*   `@BeforeEach`: Method run before each test method to set up common objects (e.g., `product1`, `product2`).
*   `when(...).thenReturn(...)`: Defines the behavior of the mocked `productRepository`. For example, `when(productRepository.findById(1L)).thenReturn(Optional.of(product1))` means when `findById(1L)` is called on the mock, it should return `Optional.of(product1)`.
*   `any(Product.class)`: A Mockito argument matcher that matches any instance of `Product`.
*   `verify(productRepository, times(1)).save(product1)`: Asserts that the `save` method on `productRepository` was called exactly once with `product1` as an argument.
*   `assertThrows(ResourceNotFoundException.class, ...)`: Asserts that a specific exception is thrown.
*   `never()`: Verifies that a method was never called.

---

#### ProductController Test

$$$$src/test/java/com/example/demo/controller
&&&&ProductControllerTest.java
@@@@
package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Configures Spring to test only ProductController
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to send HTTP requests to the controller

    @MockBean // Creates a mock of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Utility to convert objects to JSON

    private Product product1;
    private Product product2;
    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);

        productDTO1 = new ProductDTO(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        productDTO2 = new ProductDTO(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Expect 2 items in the JSON array
                .andExpect(jsonPath("$[0].name", is(productDTO1.getName())))
                .andExpect(jsonPath("$[1].name", is(productDTO2.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductByIdFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(productDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productDTO1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductDTO newProductDTO = new ProductDTO(null, "Keyboard", "Mechanical keyboard", 150.00, 20);
        Product createdProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 150.00, 20);

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductDTO))) // Convert DTO to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductDTO updatedProductDTO = new ProductDTO(1L, "Laptop Updated", "Updated desc", 1300.00, 9);
        Product updatedEntity = new Product(1L, "Laptop Updated", "Updated desc", 1300.00, 9);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is("Laptop Updated")))
                .andExpect(jsonPath("$.price", is(1300.00)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        ProductDTO updatedProductDTO = new ProductDTO(99L, "NonExistent", "Desc", 100.0, 1);

        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
@@@@
**Explanation:**
*   `@WebMvcTest(ProductController.class)`: This annotation is specific for testing Spring MVC controllers. It auto-configures Spring MVC infrastructure and limits the beans loaded to only those relevant for the controller, including `@Controller`, `@RestController`, `@ControllerAdvice`, and `@JsonComponent` beans, but not `@Service` or `@Repository` beans.
*   `@Autowired private MockMvc mockMvc;`: `MockMvc` allows you to perform requests against the controller without starting a full HTTP server. It simulates HTTP requests and responses.
*   `@MockBean private ProductService productService;`: Because `WebMvcTest` doesn't load `@Service` beans, we need to provide a mock for `ProductService` so the controller has its dependency satisfied.
*   `@Autowired private ObjectMapper objectMapper;`: Used to convert Java objects (like `ProductDTO`) to JSON strings for request bodies.
*   `mockMvc.perform(get("/api/products"))`: Initiates an HTTP GET request to `/api/products`.
*   `.andExpect(status().isOk())`: Asserts the HTTP status code of the response.
*   `.andExpect(jsonPath("$.name", is("Laptop")))`: Uses JsonPath expressions to assert values in the JSON response body. `$` refers to the root of the JSON.
*   `contentType(MediaType.APPLICATION_JSON)`: Sets the `Content-Type` header of the request.
*   `content(objectMapper.writeValueAsString(newProductDTO))`: Sets the request body to the JSON representation of `newProductDTO`.
*   `verify(productService, times(1)).getAllProducts();`: Asserts that `productService.getAllProducts()` was called exactly once by the controller.
*   `eq(1L)`: A Mockito argument matcher used when you have other matchers (like `any(Product.class)`) in the same method call, ensuring specific arguments are matched correctly.

This template provides a solid foundation for building a Spring Boot REST API with good practices and comprehensive testing.