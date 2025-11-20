package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProductRepository}.
 *
 * {@code @DataJpaTest}: A specialized annotation for JPA tests.
 *   - It disables full auto-configuration and instead applies only configuration relevant to JPA tests.
 *   - By default, tests annotated with {@code @DataJpaTest} are transactional and roll back at the end
 *     of each test method. This means your test data won't pollute the database.
 *   - It uses an in-memory database (like H2) if available on the classpath.
 */
@DataJpaTest
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager; // Used to manually persist and retrieve entities for tests

    private Product product1;
    private Product product2;

    /**
     * Set up common test data before each test method.
     */
    @BeforeEach
    void setUp() {
        // Clear the database before each test to ensure isolation
        productRepository.deleteAll();

        product1 = new Product(null, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(null, "Mouse", "Wireless gaming mouse", 50.00);

        // Persist entities using TestEntityManager to ensure they are properly managed
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
    }

    @Test
    @DisplayName("Should save a product")
    void givenProductObject_whenSave_thenReturnSavedProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 100.00);

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Keyboard");
    }

    @Test
    @DisplayName("Should find all products")
    void givenProductsInDb_whenFindAll_thenReturnAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).containsExactlyInAnyOrder(product1, product2);
    }

    @Test
    @DisplayName("Should find product by ID")
    void givenProductId_whenFindById_thenReturnProduct() {
        // When
        Optional<Product> foundProduct = productRepository.findById(product1.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void givenInvalidProductId_whenFindById_thenReturnEmptyOptional() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L); // Non-existent ID

        // Then
        assertThat(foundProduct).isNotPresent();
    }

    @Test
    @DisplayName("Should update a product")
    void givenProductObject_whenUpdateProduct_thenReturnUpdatedProduct() {
        // Given
        product1.setName("Updated Laptop");
        product1.setPrice(1300.00);

        // When
        Product updatedProduct = productRepository.save(product1);

        // Then
        assertThat(updatedProduct.getName()).isEqualTo("Updated Laptop");
        assertThat(updatedProduct.getPrice()).isEqualTo(1300.00);
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void givenProductId_whenDeleteById_thenRemoveProduct() {
        // When
        productRepository.deleteById(product1.getId());
        Optional<Product> foundProduct = productRepository.findById(product1.getId());

        // Then
        assertThat(foundProduct).isNotPresent();
        assertThat(productRepository.findAll()).hasSize(1); // Only product2 should remain
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void givenProductId_whenExistsById_thenReturnTrue() {
        // When
        boolean exists = productRepository.existsById(product1.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist by ID")
    void givenInvalidProductId_whenExistsById_thenReturnFalse() {
        // When
        boolean exists = productRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
