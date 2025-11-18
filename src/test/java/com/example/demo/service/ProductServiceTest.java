package com.example.demo.service;

import com.example.demo.exception.ProductNotFoundException;
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

@ExtendWith(MockitoExtension.class) // Initializes Mockito mocks
class ProductServiceTest {

    @Mock // Creates a mock instance of ProductRepository
    private ProductRepository productRepository;

    @InjectMocks // Injects the mocks into ProductService
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach // Runs before each test method
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop for work and gaming");
        product2 = new Product(2L, "Mouse", 25.00, "Ergonomic wireless mouse");
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        verify(productRepository, times(1)).findAll(); // Verifies that findAll was called once
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(3L);
        });

        String expectedMessage = "Product not found with id: 3";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(productRepository, times(1)).findById(3L);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1);

        assertNotNull(createdProduct);
        assertEquals("Laptop", createdProduct.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testUpdateProductFound() {
        Product updatedDetails = new Product(1L, "Gaming Laptop", 1500.00, "High-performance gaming laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails); // Mock save returns the updated product

        Product result = productService.updateProduct(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getName());
        assertEquals(1500.00, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedDetails = new Product(3L, "Non-existent Product", 100.00, "Description");
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(3L, updatedDetails);
        });

        assertTrue(exception.getMessage().contains("Product not found with id: 3"));
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).save(any(Product.class)); // Ensure save is not called
    }

    @Test
    void testDeleteProductFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(3L)).thenReturn(false);

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(3L);
        });

        assertTrue(exception.getMessage().contains("Product not found with id: 3"));
        verify(productRepository, times(1)).existsById(3L);
        verify(productRepository, never()).deleteById(anyLong()); // Ensure deleteById is not called
    }
}
