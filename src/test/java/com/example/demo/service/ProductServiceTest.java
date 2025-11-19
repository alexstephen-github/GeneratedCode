package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product created = productService.createProduct(product1);

        assertNotNull(created);
        assertEquals("Laptop", created.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> found = productService.getProductById(1L);

        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> found = productService.getProductById(99L);

        assertFalse(found.isPresent());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateProductSuccess() {
        Product updatedDetails = new Product(1L, "Laptop Pro", "New powerful laptop", 1500.00, 8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails);

        Product updated = productService.updateProduct(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Laptop Pro", updated.getName());
        assertEquals(1500.00, updated.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1); // product1 is updated and saved
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedDetails = new Product(99L, "NonExistent", "Desc", 100.0, 1);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, updatedDetails));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(product1);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
