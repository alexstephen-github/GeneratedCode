Hello there! I'd be happy to provide a complete code template for a Spring Boot REST API, including a full project structure, all necessary files, and unit tests, along with explanations.

This template will demonstrate a basic CRUD (Create, Read, Update, Delete) API for a "Product" resource.

---

### Project Overview

We'll create a Spring Boot application named `product-api` with the following structure and components:

1.  **`pom.xml`**: Maven project configuration.
2.  **`application.properties`**: Spring Boot configuration (e.g., database, server port).
3.  **`ProductApiApplication.java`**: The main Spring Boot application class.
4.  **`model/Product.java`**: JPA Entity representing a product in the database.
5.  **`dto/ProductDTO.java`**: Data Transfer Object for sending/receiving product data, including validation.
6.  **`repository/ProductRepository.java`**: Spring Data JPA repository for database interactions.
7.  **`service/ProductService.java`**: Business logic layer, orchestrating data access and DTO mapping.
8.  **`controller/ProductController.java`**: REST endpoint handling HTTP requests and responses.

We will also include unit tests for each of these layers:

1.  **`repository/ProductRepositoryTest.java`**: Tests the persistence layer using `@DataJpaTest`.
2.  **`service/ProductServiceTest.java`**: Tests the business logic using Mockito for mocking the repository.
3.  **`controller/ProductControllerTest.java`**: Tests the web layer using `@WebMvcTest` and MockMvc.

Let's get started with the code!

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
        <version>3.2.5</version> <!-- Use a recent stable version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>product-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>product-api</name>
    <description>Code template for Spring Boot REST API</description>

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
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Database Driver (H2 for in-memory, convenient for development and testing) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok for boilerplate code reduction (getters, setters, constructors, etc.) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test Starter (includes JUnit, Mockito, Spring Test) -->
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
This `pom.xml` defines your project and its essential dependencies:
*   **`parent`**: Inherits from `spring-boot-starter-parent` to manage Spring Boot versions and provide default configurations.
*   **`spring-boot-starter-web`**: Provides everything needed for building web applications, including RESTful services (e.g., embedded Tomcat server, Spring MVC).
*   **`spring-boot-starter-data-jpa`**: Includes Spring Data JPA, Hibernate, and embedded database support, enabling easy interaction with relational databases.
*   **`spring-boot-starter-validation`**: Integrates Bean Validation (JSR 380) for declarative validation of request bodies.
*   **`spring-boot-devtools`**: Enhances the development experience with features like automatic restarts and LiveReload.
*   **`h2`**: An in-memory database, excellent for development and testing environments. For production, you'd typically switch to a persistent database like MySQL or PostgreSQL.
*   **`lombok`**: A library that reduces boilerplate code (e.g., getters, setters, constructors) through annotations, making your code cleaner.
*   **`spring-boot-starter-test`**: Provides comprehensive testing utilities, including JUnit 5, Mockito for mocking, and Spring Test for integration tests.
*   **`spring-boot-maven-plugin`**: Creates an executable JAR and helps manage the application lifecycle.

---

$$$$ src/main/resources
&&&& application.properties
@@@@
# Application server port
server.port=8080

# H2 Database Configuration (for in-memory database)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1;DB_LOCK_TIMEOUT=10000
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update # 'update' creates/updates schema based on entities. Use 'create' to drop and recreate. 'none' for production.
spring.jpa.show-sql=true           # Log SQL queries to console
spring.jpa.properties.hibernate.format_sql=true # Format SQL queries nicely
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect # Specify dialect for H2
@@@@

**Explanation:**
This file configures your Spring Boot application's runtime behavior:
*   **`server.port`**: Sets the port on which your application's embedded server will listen (default is 8080).
*   **`spring.h2.console.*`**: Enables the H2 database web console, accessible at `http://localhost:8080/h2-console`, providing a GUI to view and manage your in-memory database.
*   **`spring.datasource.*`**: Defines the connection properties for the H2 in-memory database.
*   **`spring.jpa.hibernate.ddl-auto`**: Controls how Hibernate (the JPA implementation) interacts with the database schema.
    *   `update`: Hibernate attempts to update the schema to match your entities. Suitable for development.
    *   `create`: Drops existing tables and recreates them on startup. Useful for testing.
    *   `none`: No schema generation. Recommended for production environments where database migrations are managed separately (e.g., with Flyway or Liquibase).
*   **`spring.jpa.show-sql`**: Logs all SQL statements executed by Hibernate to the console.
*   **`spring.jpa.properties.hibernate.format_sql`**: Formats the logged SQL for better readability.
*   **`spring.jpa.properties.hibernate.dialect`**: Specifies the SQL dialect for the database, allowing Hibernate to generate optimized SQL queries.

---

$$$$ src/main/java/com/example/productapi
&&&& ProductApiApplication.java
@@@@
package com.example.productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot Product API application.
 * This class uses the @SpringBootApplication annotation, which is a convenience annotation
 * that combines:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the 'com.example.productapi' package (and its sub-packages), allowing it to find controllers, services, etc.
 */
@SpringBootApplication
public class ProductApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiApplication.class, args);
    }

}
@@@@

**Explanation:**
*   **`@SpringBootApplication`**: This is the primary annotation for Spring Boot applications. It's a meta-annotation that combines three important annotations:
    *   `@Configuration`: Declares the class as a source of bean definitions.
    *   `@EnableAutoConfiguration`: Automatically configures the Spring application based on the JARs present in the classpath and other settings.
    *   `@ComponentScan`: Scans the current package and its sub-packages for components (e.g., `@Component`, `@Service`, `@Repository`, `@Controller`, `@RestController`) to register them as Spring beans.
*   **`main` method**: The standard entry point for any Java application. `SpringApplication.run()` bootstraps the application, creates the Spring application context, and launches the embedded web server (Tomcat by default).

---

$$$$ src/main/java/com/example/productapi/model
&&&& Product.java
@@@@
package com.example.productapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 * Annotated with JPA annotations for Object-Relational Mapping (ORM).
 * Uses Lombok for reducing boilerplate code like getters/setters.
 */
@Entity // Marks this class as a JPA entity, meaning it will be mapped to a database table.
@Table(name = "products") // Specifies the actual table name in the database. If omitted, the class name 'Product' would be used.
@Data // Lombok annotation: Automatically generates getters, setters, equals(), hashCode(), and toString() methods.
@NoArgsConstructor // Lombok annotation: Generates a no-argument constructor, which is required by JPA.
@AllArgsConstructor // Lombok annotation: Generates a constructor with all fields as arguments.
public class Product {

    @Id // Marks this field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key generation strategy.
                                                       // IDENTITY means the database will auto-increment the ID (e.g., H2, MySQL).
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;

    // A custom constructor can be added if specific initialization logic is required,
    // but for simple cases, Lombok's @AllArgsConstructor is sufficient.
    // Example:
    // public Product(String name, String description, double price, int quantity) {
    //     this.name = name;
    //     this.description = description;
    //     this.price = price;
    //     this.quantity = quantity;
    // }
}
@@@@

**Explanation:**
*   **`@Entity`**: Marks `Product` as a JPA entity, indicating that it represents a table in the database.
*   **`@Table(name = "products")`**: Explicitly names the database table to which this entity maps.
*   **`@Data` (Lombok)**: A powerful annotation that generates common boilerplate code like getters, setters, `equals()`, `hashCode()`, and `toString()`.
*   **`@NoArgsConstructor` (Lombok)**: Generates a constructor with no arguments. This is a requirement for JPA entities.
*   **`@AllArgsConstructor` (Lombok)**: Generates a constructor with all fields as arguments.
*   **`@Id`**: Designates the `id` field as the primary key.
*   **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Configures the primary key to be automatically generated by the database, typically an auto-incrementing column.

---

$$$$ src/main/java/com/example/productapi/dto
&&&& ProductDTO.java
@@@@
package com.example.productapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Product.
 * Used to transfer product data between different layers (e.g., Controller to Service, Service to Controller).
 * Helps decouple the API from the internal entity structure, allows for request/response specific fields,
 * and integrates JSR-380 validation.
 * Uses Lombok for boilerplate code reduction.
 */
@Data // Lombok annotation: Generates getters, setters, equals, hashCode, and toString methods
@NoArgsConstructor // Lombok annotation: Generates a no-argument constructor
@AllArgsConstructor // Lombok annotation: Generates a constructor with all fields as arguments
public class ProductDTO {

    private Long id; // ID is optional for creation requests, but present for responses

    @NotBlank(message = "Product name is required") // Ensures the name is not null and not empty after trimming
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0") // Price must be a positive decimal
    private Double price;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative") // Quantity cannot be negative
    private Integer quantity;
}
@@@@

**Explanation:**
*   **DTO (Data Transfer Object)**: `ProductDTO` is a plain Java object used to send data from the client to the server and vice-versa. It decouples the API contract from the internal `Product` entity, allowing you to expose only necessary fields and apply validation specific to the API.
*   **`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)**: Provide standard boilerplate methods and constructors.
*   **Validation Annotations (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@Min`)**: These are JSR-380 Bean Validation annotations. When a controller method parameter is annotated with `@Valid` and this DTO, Spring automatically validates the incoming data against these rules.
    *   `@NotBlank`: For `String` fields, ensures the field is not `null` and its trimmed length is greater than zero.
    *   `@NotNull`: For any object type, ensures the field is not `null`.
    *   `@DecimalMin(value = "0.01")`: For numeric types, ensures the value is greater than or equal to 0.01.
    *   `@Min(value = 0)`: For integer types, ensures the value is greater than or equal to 0.

---

$$$$ src/main/java/com/example/productapi/repository
&&&& ProductRepository.java
@@@@
package com.example.productapi.repository;

import com.example.productapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Product entities.
 * Extends JpaRepository to provide standard CRUD (Create, Read, Update, Delete) operations
 * and pagination/sorting capabilities out-of-the-box.
 * Spring automatically generates an implementation for this interface at runtime.
 */
@Repository // Marks this interface as a Spring Data JPA repository component. Spring's component scanning will find and register it.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA automatically provides methods like:
    // - save(Product product)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - count()
    // ... and many more.

    // Custom query methods can be added by following Spring Data JPA naming conventions, e.g.:
    // List<Product> findByNameContainingIgnoreCase(String name); // Finds products by name (case-insensitive substring match)

    // Or by using the @Query annotation for more complex JPQL or native SQL:
    // @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    // List<Product> findProductsByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
@@@@

**Explanation:**
*   **`@Repository`**: A specialized `@Component` annotation that marks the interface as a Spring Data JPA repository. Spring will find and register an implementation for it.
*   **`JpaRepository<Product, Long>`**: This is the core interface from Spring Data JPA. By extending it, `ProductRepository` automatically inherits a rich set of CRUD operations (e.g., `save()`, `findById()`, `findAll()`, `deleteById()`) for the `Product` entity. The `Long` specifies the type of the primary key (`id` field in `Product`). Spring Data JPA provides the implementation at runtime, significantly reducing boilerplate code for data access.
*   **Custom Queries (Commented examples)**: You can define your own methods in this interface, and Spring Data JPA will automatically generate the query based on the method name (e.g., `findByNameContainingIgnoreCase`). For more complex queries, you can use the `@Query` annotation with JPQL (Java Persistence Query Language) or native SQL.

---

$$$$ src/main/java/com/example/productapi/service
&&&& ProductService.java
@@@@
package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing Product-related business logic.
 * Interacts with the ProductRepository for data access and handles mapping
 * between Product entities and ProductDTOs.
 */
@Service // Marks this class as a Spring service component. Service classes encapsulate business logic.
public class ProductService {

    private final ProductRepository productRepository;

    // Dependency Injection: Spring automatically injects an instance of ProductRepository
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products and maps them to DTOs.
     * @return A list of ProductDTOs.
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto) // Map each Product entity to a ProductDTO
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its ID and maps it to a DTO.
     * @param id The ID of the product.
     * @return An Optional containing the ProductDTO if found, otherwise empty.
     */
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto); // Map the found Product entity to a ProductDTO
    }

    /**
     * Creates a new product from a DTO.
     * @param productDTO The DTO containing product data.
     * @return The created ProductDTO with the generated ID.
     */
    @Transactional // Ensures the entire method executes within a single database transaction.
                   // If an exception occurs, the transaction will be rolled back.
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO); // Convert DTO to entity
        Product savedProduct = productRepository.save(product); // Persist entity
        return convertToDto(savedProduct); // Convert saved entity back to DTO for response
    }

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productDTO The DTO with updated product data.
     * @return An Optional containing the updated ProductDTO if found, otherwise empty.
     */
    @Transactional
    public Optional<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Update fields of the existing product entity
                    existingProduct.setName(productDTO.getName());
                    existingProduct.setDescription(productDTO.getDescription());
                    existingProduct.setPrice(productDTO.getPrice());
                    existingProduct.setQuantity(productDTO.getQuantity());
                    Product updatedProduct = productRepository.save(existingProduct); // save() also handles updates for existing entities
                    return convertToDto(updatedProduct);
                });
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @return true if the product was found and deleted, false otherwise.
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product); // Delete the found product
                    return true; // Return true as deletion was successful
                }).orElse(false); // If product not found, return false
    }

    /**
     * Converts a Product entity to a ProductDTO.
     * This helper method encapsulates the mapping logic.
     * @param product The Product entity.
     * @return The corresponding ProductDTO.
     */
    private ProductDTO convertToDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    /**
     * Converts a ProductDTO to a Product entity.
     * Note: ID is not set from DTO as it's typically auto-generated by the database for new entities.
     * For updates, the ID would be retrieved from the path variable and set on the existing entity
     * (as seen in updateProduct method).
     * @param productDTO The ProductDTO.
     * @return The corresponding Product entity.
     */
    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        return product;
    }
}
@@@@

**Explanation:**
*   **`@Service`**: Marks this class as a Spring service component. Service classes encapsulate the business logic of your application, acting as an intermediary between the controllers and the data access layer (repositories).
*   **Dependency Injection**: The `ProductRepository` is injected into the `ProductService` constructor. Spring automatically provides an instance of `ProductRepository` because it's a Spring Data JPA repository (and thus a Spring bean).
*   **`@Transactional`**: This annotation ensures that a method executes within a single database transaction. If the method completes successfully, the transaction is committed; if an unchecked exception occurs, the transaction is rolled back. This is crucial for maintaining data consistency, especially for write operations (`create`, `update`, `delete`).
*   **CRUD Operations**: The service provides methods for common operations: `getAllProducts`, `getProductById`, `createProduct`, `updateProduct`, and `deleteProduct`.
*   **Entity-to-DTO / DTO-to-Entity Mapping**: The `convertToDto` and `convertToEntity` private helper methods are responsible for translating between the internal `Product` entity (used by the repository and service) and the `ProductDTO` (exposed to the controller and client). This separation is a good practice for maintaining flexibility, decoupling, and ensuring the API contract is independent of the database schema.
*   **`Optional`**: Methods like `getProductById` and `updateProduct` return `Optional<ProductDTO>` to explicitly indicate that a product might not be found, which helps prevent `NullPointerException`s and makes the API more robust.

---

$$$$ src/main/java/com/example/productapi/controller
&&&& ProductController.java
@@@@
package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product resources.
 * Handles incoming HTTP requests, delegates business logic to the ProductService,
 * and constructs appropriate HTTP responses.
 * Uses ProductDTO for request and response bodies, ensuring a clean API contract.
 */
@RestController // Marks this class as a REST controller. Combines @Controller and @ResponseBody.
@RequestMapping("/api/products") // Base path for all endpoints defined in this controller.
public class ProductController {

    private final ProductService productService;

    // Dependency Injection: Spring automatically injects an instance of ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     * HTTP GET request to /api/products
     * @return A list of ProductDTOs and HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK with the list of products in the response body.
    }

    /**
     * Retrieves a single product by its ID.
     * HTTP GET request to /api/products/{id}
     * @param id The ID of the product to retrieve.
     * @return The ProductDTO if found with HTTP 200 OK, or HTTP 404 Not Found if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // If product is found, wrap it in 200 OK response.
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found.
    }

    /**
     * Creates a new product.
     * HTTP POST request to /api/products
     * @param productDTO The ProductDTO containing the data for the new product.
     *                   The @Valid annotation triggers validation rules defined in ProductDTO.
     *                   The @RequestBody annotation maps the incoming JSON request body to the ProductDTO object.
     * @return The created ProductDTO with HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created status.
    }

    /**
     * Updates an existing product.
     * HTTP PUT request to /api/products/{id}
     * @param id The ID of the product to update.
     * @param productDTO The ProductDTO containing the updated data.
     *                   The @Valid annotation triggers validation rules defined in ProductDTO.
     * @return The updated ProductDTO with HTTP 200 OK, or HTTP 404 Not Found if the product does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO)
                .map(ResponseEntity::ok) // If updated successfully, wrap it in 200 OK response.
                .orElse(ResponseEntity.notFound().build()); // If product not found, return 404 Not Found.
    }

    /**
     * Deletes a product.
     * HTTP DELETE request to /api/products/{id}
     * @param id The ID of the product to delete.
     * @return HTTP 204 No Content if deleted successfully, or HTTP 404 Not Found if the product does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content upon successful deletion.
        } else {
            return ResponseEntity.notFound().build(); // Returns 404 Not Found if the product could not be deleted (e.g., not found).
        }
    }
}
@@@@

**Explanation:**
*   **`@RestController`**: A convenience annotation that combines `@Controller` and `@ResponseBody`. It tells Spring that this class is a controller that handles REST requests and that the return value of its methods should be directly bound to the web response body (e.g., as JSON).
*   **`@RequestMapping("/api/products")`**: Defines the base URI path for all endpoints within this controller. All routes will start with `/api/products`.
*   **Dependency Injection**: `ProductService` is injected into the controller's constructor.
*   **`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`**: These are shortcut annotations for `@RequestMapping` with specific HTTP methods.
    *   `@GetMapping`: Maps HTTP GET requests (for retrieving resources).
    *   `@PostMapping`: Maps HTTP POST requests (for creating new resources).
    *   `@PutMapping`: Maps HTTP PUT requests (for updating existing resources).
    *   `@DeleteMapping`: Maps HTTP DELETE requests (for deleting resources).
*   **`@PathVariable`**: Binds a method parameter to a URI template variable (e.g., `{id}` in the URL).
*   **`@RequestBody`**: Binds the HTTP request body (typically JSON) to a method parameter, automatically deserializing it into a Java object (`ProductDTO` in this case).
*   **`@Valid`**: Triggers the validation process for the `ProductDTO` object based on the JSR-380 annotations defined within `ProductDTO`. If validation fails, Spring will typically return a `400 Bad Request` response with error details.
*   **`ResponseEntity`**: A powerful class that represents the entire HTTP response, allowing you to control the status code, headers, and body.
    *   `ResponseEntity.ok()`: Returns an HTTP 200 OK status.
    *   `new ResponseEntity<>(object, HttpStatus.CREATED)`: Returns an HTTP 201 Created status.
    *   `ResponseEntity.noContent().build()`: Returns an HTTP 204 No Content status.
    *   `ResponseEntity.notFound().build()`: Returns an HTTP 404 Not Found status.

---

### Unit Test Files

Now, let's add the corresponding unit test files for each layer.

---

$$$$ src/test/java/com/example/productapi/repository
&&&& ProductRepositoryTest.java
@@@@
package com.example.productapi.repository;

import com.example.productapi.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductRepository using @DataJpaTest.
 * This annotation configures an in-memory database and loads only JPA-related components,
 * making tests fast and focused on the persistence layer.
 */
@DataJpaTest // Auto-configures an in-memory database (H2 by default) and scans for JPA entities and Spring Data repositories.
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager; // Used to persist entities directly for setting up test data, bypassing the repository itself.

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Clear existing data before each test to ensure a clean state
        productRepository.deleteAll();

        // Setup test data
        product1 = new Product(null, "Laptop", "High-performance laptop", 1200.00, 10);
        product2 = new Product(null, "Mouse", "Wireless optical mouse", 25.00, 50);

        // Persist using TestEntityManager to ensure entities are in the database context
        // This is preferred over using productRepository.save() for setup in repository tests,
        // as it isolates the repository's own save method from the test setup.
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush(); // Flush ensures data is written to the database before repository operations are tested
    }

    @Test
    void whenFindAll_thenReturnAllProducts() {
        // When
        Iterable<Product> products = productRepository.findAll();

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).contains(product1, product2);
    }

    @Test
    void whenFindById_thenReturnProduct() {
        // When
        Optional<Product> foundProduct = productRepository.findById(product1.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void whenFindByIdNotFound_thenReturnEmptyOptional() {
        // When
        Optional<Product> foundProduct = productRepository.findById(99L); // ID that doesn't exist

        // Then
        assertThat(foundProduct).isNotPresent();
    }

    @Test
    void whenSaveProduct_thenProductIsPersisted() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 75.00, 30);

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Keyboard");

        // Verify it can be found in the database
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get()).isEqualTo(savedProduct);
    }

    @Test
    void whenUpdateProduct_thenProductIsUpdated() {
        // Given
        Product productToUpdate = entityManager.find(Product.class, product1.getId()); // Get managed entity
        productToUpdate.setPrice(1250.00);
        productToUpdate.setQuantity(8);

        // When
        Product updatedProduct = productRepository.save(productToUpdate);

        // Then
        assertThat(updatedProduct.getPrice()).isEqualTo(1250.00);
        assertThat(updatedProduct.getQuantity()).isEqualTo(8);

        // Verify the update in the database
        Optional<Product> foundProduct = productRepository.findById(product1.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getPrice()).isEqualTo(1250.00);
    }

    @Test
    void whenDeleteProduct_thenProductIsRemoved() {
        // Given
        Long idToDelete = product1.getId();

        // When
        productRepository.deleteById(idToDelete);
        entityManager.flush(); // Ensure deletion is committed to the database

        // Then
        Optional<Product> deletedProduct = productRepository.findById(idToDelete);
        assertThat(deletedProduct).isNotPresent();

        // Verify other products remain
        assertThat(productRepository.findAll()).hasSize(1);
        assertThat(productRepository.findAll()).containsOnly(product2);
    }
}
@@@@

**Explanation:**
*   **`@DataJpaTest`**: This annotation is specifically designed for testing JPA repositories. It sets up an in-memory database (H2 by default), scans for `@Entity` classes and Spring Data JPA repositories, and provides a `TestEntityManager`. It doesn't load the entire Spring application context, making these tests fast and focused on the persistence layer.
*   **`TestEntityManager`**: A wrapper around the standard JPA `EntityManager` tailored for tests. It's useful for persisting entities *directly* to set up test data without going through the repository itself, ensuring your repository is tested in isolation.
*   **`@Autowired`**: Used to inject `ProductRepository` and `TestEntityManager`.
*   **`@BeforeEach`**: This method runs before each test method. It's used to set up a clean state for each test, ensuring test isolation. Here, we delete all existing products and then persist new test data.
*   **Assertions (`assertThat`)**: We use AssertJ fluent assertions for readability and expressiveness in our test validations.
*   **Test Cases**: Cover common repository operations: `findAll`, `findById` (found and not found), `save` (create), `save` (update), and `deleteById`.

---

$$$$ src/test/java/com/example/productapi/service
&&&& ProductServiceTest.java
@@@@
package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService using Mockito.
 * We mock the ProductRepository dependency to isolate and test the service layer's business logic.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5 tests.
public class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository. Its methods won't execute real database calls.
    private ProductRepository productRepository;

    @InjectMocks // Injects the mock objects (like productRepository) into the ProductService instance.
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    void setUp() {
        // Initialize test entities and DTOs before each test method
        product1 = new Product(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless optical mouse", 25.00, 50);

        productDTO1 = new ProductDTO(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        productDTO2 = new ProductDTO(2L, "Mouse", "Wireless optical mouse", 25.00, 50);
    }

    @Test
    void whenGetAllProducts_thenReturnListOfProductDTOs() {
        // Given: Configure the mock repository to return a list of products when findAll() is called.
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When: Call the service method under test.
        List<ProductDTO> result = productService.getAllProducts();

        // Then: Assert the expected outcome.
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(productDTO1, productDTO2);
        // Verify that the productRepository.findAll() method was called exactly once.
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void whenGetProductByIdFound_thenReturnProductDTO() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        Optional<ProductDTO> result = productService.getProductById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(productDTO1);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetProductByIdNotFound_thenReturnEmptyOptional() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<ProductDTO> result = productService.getProductById(99L);

        // Then
        assertThat(result).isNotPresent();
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void whenCreateProduct_thenReturnCreatedProductDTO() {
        // Given
        ProductDTO newProductDTO = new ProductDTO(null, "Keyboard", "Mechanical keyboard", 75.00, 30);
        Product savedProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 75.00, 30);
        ProductDTO expectedDTO = new ProductDTO(3L, "Keyboard", "Mechanical keyboard", 75.00, 30);

        // Configure mock to return `savedProduct` when `save()` is called with *any* Product entity.
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductDTO result = productService.createProduct(newProductDTO);

        // Then
        assertThat(result).isEqualTo(expectedDTO);
        assertThat(result.getId()).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void whenUpdateProductFound_thenReturnUpdatedProductDTO() {
        // Given
        ProductDTO updatedProductDTO = new ProductDTO(1L, "Laptop Pro", "Updated description", 1300.00, 8);
        Product existingProduct = new Product(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        Product savedProduct = new Product(1L, "Laptop Pro", "Updated description", 1300.00, 8); // Simulate save returning updated entity

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Optional<ProductDTO> result = productService.updateProduct(1L, updatedProductDTO);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(updatedProductDTO);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void whenUpdateProductNotFound_thenReturnEmptyOptional() {
        // Given
        ProductDTO updatedProductDTO = new ProductDTO(1L, "Laptop Pro", "Updated description", 1300.00, 8);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<ProductDTO> result = productService.updateProduct(99L, updatedProductDTO);

        // Then
        assertThat(result).isNotPresent();
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class)); // Verify save was NOT called
    }

    @Test
    void whenDeleteProductFound_thenReturnTrue() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        // For void methods, use doNothing().when() or just don't configure if default (do nothing) is desired.
        doNothing().when(productRepository).delete(product1);

        // When
        boolean result = productService.deleteProduct(1L);

        // Then
        assertThat(result).isTrue();
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void whenDeleteProductNotFound_thenReturnFalse() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        boolean result = productService.deleteProduct(99L);

        // Then
        assertThat(result).isFalse();
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).delete(any(Product.class)); // Verify delete was NOT called
    }
}
@@@@

**Explanation:**
*   **`@ExtendWith(MockitoExtension.class)`**: Integrates Mockito with JUnit 5, enabling Mockito annotations.
*   **`@Mock`**: Creates a mock object for `ProductRepository`. This mock replaces the actual repository, allowing you to control its behavior without hitting a real database.
*   **`@InjectMocks`**: Injects the mock objects (like `productRepository`) into the annotated field (`productService`). This creates an instance of `ProductService` with its `ProductRepository` dependency satisfied by the mock.
*   **`when().thenReturn()`**: Mockito's way of defining behavior for mock methods. For example, `when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2))` tells the mock to return a specific list of products whenever `findAll()` is called.
*   **`verify(mock, times(n)).method()`**: Used to verify that a specific method on a mock object was called a certain number of times. `verify(mock, never()).method()` checks that a method was never called.
*   **`any(Product.class)`**: A Mockito argument matcher. It matches any `Product` object passed to the mocked method. Useful when you don't care about the exact object instance.
*   **`doNothing().when(mock).voidMethod()`**: How you configure behavior for void methods when mocking.
*   **Test Cases**: Focus on testing the business logic within the service layer:
    *   Retrieving all products.
    *   Retrieving a product by ID (found and not found).
    *   Creating a product.
    *   Updating a product (found and not found).
    *   Deleting a product (found and not found).

---

$$$$ src/test/java/com/example/productapi/controller
&&&& ProductControllerTest.java
@@@@
package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.service.ProductService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProductController using @WebMvcTest.
 * This annotation focuses on testing Spring MVC components. It loads only web-related components
 * (like the controller itself) and allows for testing controllers in isolation by mocking service layers.
 */
@WebMvcTest(ProductController.class) // Configures Spring MVC for testing only ProductController.
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests against the controller without starting a full HTTP server.

    @MockBean // Creates a Mockito mock of ProductService and adds it to the Spring application context.
              // This mock replaces the actual service, isolating the controller test.
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Utility for converting Java objects to/from JSON.

    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    void setUp() {
        // Initialize test DTOs before each test method.
        productDTO1 = new ProductDTO(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        productDTO2 = new ProductDTO(2L, "Mouse", "Wireless optical mouse", 25.00, 50);
    }

    @Test
    void whenGetAllProducts_thenReturnProductList() throws Exception {
        // Given: Configure the mock service to return a list of DTOs.
        when(productService.getAllProducts()).thenReturn(Arrays.asList(productDTO1, productDTO2));

        // When & Then: Perform a GET request and assert the response.
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)) // Set request content type
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$", hasSize(2))) // Expect a JSON array of size 2
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Mouse")));
    }

    @Test
    void whenGetProductByIdFound_thenReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(productDTO1));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L) // Request for ID 1
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void whenGetProductByIdNotFound_thenReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenReturn(Optional.empty()); // Simulate product not found

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L) // Request for non-existent ID
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void whenCreateProduct_thenReturnCreatedProduct() throws Exception {
        // Given
        ProductDTO newProductRequest = new ProductDTO(null, "Keyboard", "Mechanical keyboard", 75.00, 30);
        ProductDTO createdProductResponse = new ProductDTO(3L, "Keyboard", "Mechanical keyboard", 75.00, 30);

        // Configure mock service to return a DTO with a generated ID.
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(createdProductResponse);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductRequest))) // Convert DTO to JSON string for request body
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));
    }

    @Test
    void whenCreateProductWithInvalidData_thenReturnBadRequest() throws Exception {
        // Given: Invalid product data (empty name, negative price/quantity)
        ProductDTO invalidProductDTO = new ProductDTO(null, "", "Desc", -10.00, -5);

        // When & Then: Perform POST request and expect bad request due to validation errors.
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductDTO)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name", is("Product name is required"))) // Check for specific validation errors
                .andExpect(jsonPath("$.price", is("Price must be greater than 0")))
                .andExpect(jsonPath("$.quantity", is("Quantity cannot be negative")));
    }


    @Test
    void whenUpdateProductFound_thenReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO updatedProductRequest = new ProductDTO(1L, "Laptop Pro", "Updated description", 1300.00, 8);

        // Configure mock service to return the updated DTO within an Optional.
        when(productService.updateProduct(any(Long.class), any(ProductDTO.class)))
                .thenReturn(Optional.of(updatedProductRequest));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProductRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop Pro")));
    }

    @Test
    void whenUpdateProductNotFound_thenReturnNotFound() throws Exception {
        // Given
        ProductDTO nonExistentProductDTO = new ProductDTO(99L, "Non Existent", "N/A", 100.00, 1);
        when(productService.updateProduct(any(Long.class), any(ProductDTO.class)))
                .thenReturn(Optional.empty()); // Simulate product not found for update

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentProductDTO)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void whenDeleteProductFound_thenReturnNoContent() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true); // Simulate successful deletion

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    void whenDeleteProductNotFound_thenReturnNotFound() throws Exception {
        // Given
        when(productService.deleteProduct(99L)).thenReturn(false); // Simulate deletion of non-existent product

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }
}
@@@@

**Explanation:**
*   **`@WebMvcTest(ProductController.class)`**: This annotation is specific for testing Spring MVC controllers. It auto-configures Spring MVC components (like `DispatcherServlet`, `Controller`, etc.) but does *not* load the entire application context or database. It focuses on the web layer, keeping tests fast.
*   **`MockMvc`**: Provided by `@WebMvcTest`, this object allows you to perform HTTP requests against your controller without starting a full HTTP server. You can then assert on the response status, headers, and body.
*   **`@MockBean`**: Used to add Mockito mocks to the Spring application context. When `@WebMvcTest` scans for components, it finds `ProductController`, which depends on `ProductService`. `@MockBean` tells Spring to provide a *mock* instance of `ProductService` instead of its real implementation. This isolates the controller test from the service layer.
*   **`ObjectMapper`**: A Jackson utility class used to convert Java objects to JSON strings and vice-versa. Essential for creating request bodies and asserting on response bodies.
*   **`mockMvc.perform()`**: Initiates an HTTP request.
    *   `get("/api/products")`: Specifies the HTTP method and URI.
    *   `contentType(MediaType.APPLICATION_JSON)`: Sets the `Content-Type` header.
    *   `content(objectMapper.writeValueAsString(dto))`: Sets the request body.
*   **`andExpect()`**: Chains assertions on the `MockHttpServletResponse`.
    *   `status().isOk()`: Asserts the HTTP status code (e.g., 200, 201, 204, 400, 404).
    *   `jsonPath("$[0].name", is("Laptop"))`: Uses JsonPath expressions to navigate the JSON response and assert specific values. `$` refers to the root of the JSON.
*   **Test Cases**: Cover all controller endpoints for various scenarios, including valid requests, not found scenarios, and invalid input (triggering validation).

---

This comprehensive template provides a solid foundation for building a Spring Boot REST API, following good practices like layered architecture, DTOs, validation, and thorough unit testing. You can expand upon this by adding more complex business logic, authentication/authorization, error handling, pagination, and more advanced querying.