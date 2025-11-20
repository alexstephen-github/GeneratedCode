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
