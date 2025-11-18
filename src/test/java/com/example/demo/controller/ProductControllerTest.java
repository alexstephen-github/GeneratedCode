package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController.
 * Uses @WebMvcTest to test the controller layer in isolation,
 * mocking the ProductService dependency.
 */
@WebMvcTest(ProductController.class) // Focuses Spring Boot tests on ProductController
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests in tests

    @MockBean // Creates a mock instance of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to/from JSON

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00);
        product2 = new Product(2L, "Mouse", 25.00);
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void getProductById_shouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(product1);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200.00));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 if product not found")
    void getProductById_shouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("POST /api/products - Should create a new product")
    void createProduct_shouldCreateProduct() throws Exception {
        // Given
        Product newProduct = new Product(null, "Keyboard", 75.00);
        Product savedProduct = new Product(3L, "Keyboard", 75.00);
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(75.00));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update an existing product")
    void updateProduct_shouldUpdateProduct() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "Updated Laptop", 1300.00); // ID will be ignored by service
        Product updatedProduct = new Product(1L, "Updated Laptop", 1300.00);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1300.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should return 404 if product not found for update")
    void updateProduct_shouldReturnNotFound() throws Exception {
        // Given
        Product updatedDetails = new Product(null, "NonExistent Product", 100.00);
        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete a product")
    void deleteProduct_shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L); // Mock void method

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should return 404 if product not found for delete")
    void deleteProduct_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"))
                .when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
