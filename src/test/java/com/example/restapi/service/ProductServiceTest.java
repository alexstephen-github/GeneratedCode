package com.example.restapi.service;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
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

/**
 * Unit tests for the ProductService class.
 * Uses Mockito to mock the ProductRepository dependency.
 *
 * @ExtendWith(MockitoExtension.class): Integrates Mockito with JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService instance
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequestDTO productRequestDTO;
    private ProductRequestDTO updatedProductRequestDTO;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop for work", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless gaming mouse", 50.00);

        productRequestDTO = new ProductRequestDTO("Keyboard", "Mechanical keyboard", 150.00);
        updatedProductRequestDTO = new ProductRequestDTO("Updated Laptop", "Ultra-light laptop", 1300.00);
    }

    @Test
    @DisplayName("Should retrieve all products")
    void shouldGetAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(1).getName()).isEqualTo("Mouse");
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @Test
    @DisplayName("Should retrieve product by ID")
    void shouldGetProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        ProductResponseDTO result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by ID not found")
    void shouldThrowExceptionWhenProductByIdNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create a new product")
    void shouldCreateProduct() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 150.00);
        Product savedProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 150.00);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Keyboard");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateProduct() {
        // Given
        Product updatedEntity = new Product(1L, "Updated Laptop", "Ultra-light laptop", 1300.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedEntity);

        // When
        ProductResponseDTO result = productService.updateProduct(1L, updatedProductRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getPrice()).isEqualTo(1300.00);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updatedProductRequestDTO));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteProduct() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure deleteById was never called
    }
}
