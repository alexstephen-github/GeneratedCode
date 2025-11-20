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
