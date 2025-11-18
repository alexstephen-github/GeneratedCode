package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes Mockito mocks
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductRequest productRequest;

    @BeforeEach // Runs before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", new BigDecimal("1200.00"), 10);
        product2 = new Product(2L, "Mouse", "Gaming mouse", new BigDecimal("50.00"), 50);

        productRequest = new ProductRequest();
        productRequest.setName("Keyboard");
        productRequest.setDescription("Mechanical keyboard");
        productRequest.setPrice(new BigDecimal("100.00"));
        productRequest.setQuantity(20);
    }

    @Test
    void createProduct_shouldReturnProductResponse() {
        // Given
        Product newProduct = new Product(null, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());
        Product savedProduct = new Product(3L, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());

        // When
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse result = productService.createProduct(productRequest);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Keyboard", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_shouldReturnListOfProductResponses() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProductResponse_whenFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProductResponse_whenFound() {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Laptop");
        updateRequest.setDescription("Better performance");
        updateRequest.setPrice(new BigDecimal("1300.00"));
        updateRequest.setQuantity(8);

        Product updatedProductEntity = new Product(1L, "Updated Laptop", "Better performance", new BigDecimal("1300.00"), 8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductEntity);

        // When
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Laptop", result.getName());
        assertEquals(new BigDecimal("1300.00"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, productRequest));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldSucceed_whenFound() {
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
    void deleteProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        // Given
        when(productRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
