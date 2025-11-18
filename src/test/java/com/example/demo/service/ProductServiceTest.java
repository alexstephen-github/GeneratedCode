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
