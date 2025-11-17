package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProductService.
 * Uses Mockito to mock the ProductRepository.
 *
 * @ExtendWith(MockitoExtension.class): Integrates Mockito with JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mock ProductRepository into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00);
        productRequest = new ProductRequest("Keyboard", "Mechanical keyboard", 75.00);
    }

    @DisplayName("Test getAllProducts - success")
    @Test
    void whenGetAllProducts_thenReturnProductList() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> products = productService.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
        assertThat(products.get(1).getName()).isEqualTo("Mouse");
        verify(productRepository, times(1)).findAll(); // Verify findAll was called once
    }

    @DisplayName("Test getProductById - success")
    @Test
    void whenGetProductById_thenReturnProductResponse() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        ProductResponse foundProduct = productService.getProductById(1L);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(1L);
        assertThat(foundProduct.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @DisplayName("Test getProductById - not found")
    @Test
    void whenGetProductById_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.getProductById(99L));
        assertThat(exception.getMessage()).isEqualTo("Product not found with id: 99");
        verify(productRepository, times(1)).findById(99L);
    }

    @DisplayName("Test createProduct - success")
    @Test
    void whenCreateProduct_thenReturnNewProductResponse() {
        // Given
        Product newProduct = new Product(null, "Keyboard", "Mechanical keyboard", 75.00);
        Product savedProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 75.00);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse createdProduct = productService.createProduct(productRequest);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isEqualTo(3L);
        assertThat(createdProduct.getName()).isEqualTo("Keyboard");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("Test updateProduct - success")
    @Test
    void whenUpdateProduct_thenReturnUpdatedProductResponse() {
        // Given
        ProductRequest updateRequest = new ProductRequest("Updated Laptop", "Improved model", 1300.00);
        Product updatedProductEntity = new Product(1L, "Updated Laptop", "Improved model", 1300.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductEntity);

        // When
        ProductResponse updatedResponse = productService.updateProduct(1L, updateRequest);

        // Then
        assertThat(updatedResponse).isNotNull();
        assertThat(updatedResponse.getId()).isEqualTo(1L);
        assertThat(updatedResponse.getName()).isEqualTo("Updated Laptop");
        assertThat(updatedResponse.getPrice()).isEqualTo(1300.00);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("Test updateProduct - not found")
    @Test
    void whenUpdateProduct_thenThrowResourceNotFoundException() {
        // Given
        ProductRequest updateRequest = new ProductRequest("NonExistent", "Desc", 100.00);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(99L, updateRequest));
        assertThat(exception.getMessage()).isEqualTo("Product not found with id: 99");
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class)); // save should not be called
    }

    @DisplayName("Test deleteProduct - success")
    @Test
    void whenDeleteProduct_thenDoNothing() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @DisplayName("Test deleteProduct - not found")
    @Test
    void whenDeleteProduct_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.deleteProduct(99L));
        assertThat(exception.getMessage()).isEqualTo("Product not found with id: 99");
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong()); // deleteById should not be called
    }
}
