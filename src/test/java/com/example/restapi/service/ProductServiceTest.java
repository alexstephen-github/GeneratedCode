package com.example.restapi.service;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import com.example.restapi.service.impl.ProductServiceImpl;
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
 * Unit tests for {@link ProductServiceImpl}.
 *
 * {@code @ExtendWith(MockitoExtension.class)}: Enables Mockito annotations like {@code @Mock} and {@code @InjectMocks}.
 *   This automatically initializes mocks before each test method.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductServiceImpl
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "High performance laptop", 1200.00);
        product2 = new Product(2L, "Mouse", "Wireless gaming mouse", 50.00);
        productDTO = new ProductDTO("Keyboard", "Mechanical keyboard", 100.00);
    }

    @Test
    @DisplayName("Should return all products")
    void givenProductsExist_whenGetAllProducts_thenReturnAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<Product> products = productService.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).containsExactlyInAnyOrder(product1, product2);
        verify(productRepository, times(1)).findAll(); // Verify that findAll was called once
    }

    @Test
    @DisplayName("Should return product by ID when found")
    void givenProductId_whenGetProductById_thenReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // When
        Product foundProduct = productService.getProductById(1L);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by ID not found")
    void givenInvalidProductId_whenGetProductById_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create a new product")
    void givenProductDTO_whenCreateProduct_thenReturnCreatedProduct() {
        // Given
        Product productToSave = new Product(null, productDTO.getName(), productDTO.getDescription(), productDTO.getPrice());
        when(productRepository.save(any(Product.class))).thenReturn(product1); // Simulate saving and returning product1 (with ID)

        // When
        Product createdProduct = productService.createProduct(productDTO);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(productDTO.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update an existing product")
    void givenProductIdAndProductDTO_whenUpdateProduct_thenReturnUpdatedProduct() {
        // Given
        Product updatedProduct = new Product(1L, "Updated Laptop", "Updated description", 1300.00);
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated description", 1300.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        Product result = productService.updateProduct(1L, updateDTO);

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
    void givenInvalidProductIdAndProductDTO_whenUpdateProduct_thenThrowResourceNotFoundException() {
        // Given
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated description", 1300.00);
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(999L, updateDTO));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class)); // save should not be called
    }

    @Test
    @DisplayName("Should delete a product by ID")
    void givenProductId_whenDeleteProduct_thenDoNothing() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L); // Mock void method

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void givenInvalidProductId_whenDeleteProduct_thenThrowResourceNotFoundException() {
        // Given
        when(productRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong()); // deleteById should not be called
    }
}
