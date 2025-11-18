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
