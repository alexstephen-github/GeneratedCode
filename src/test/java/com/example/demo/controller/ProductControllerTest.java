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

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController.
 * Uses @WebMvcTest to focus on Spring MVC components and mocks other layers.
 */
@WebMvcTest(ProductController.class) // Only loads ProductController and its dependencies
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to/from JSON

    // Mock the ProductService dependency for isolation
    @MockBean
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1200.00, "Powerful laptop");
        product2 = new Product(2L, "Mouse", 25.00, "Wireless mouse");
    }

    @Test
    @DisplayName("GET /api/products should return list of products")
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Expect a JSON array of size 2
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product when found")
    void getProductById_shouldReturnProduct_whenFound() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(product1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 404 Not Found when product not found")
    void getProductById_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("POST /api/products should create new product")
    void createProduct_shouldCreateProduct() throws Exception {
        // Arrange
        Product newProduct = new Product(null, "Keyboard", 75.00, "Mechanical keyboard");
        Product savedProduct = new Product(3L, "Keyboard", 75.00, "Mechanical keyboard");
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))) // Convert Product object to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("POST /api/products should return 400 Bad Request for invalid product creation")
    void createProduct_shouldReturnBadRequest_forInvalidProduct() throws Exception {
        // Arrange - Simulate a business rule violation (e.g., product name already exists)
        Product newProduct = new Product(null, "Existing Product", 100.00, "Description");
        when(productService.createProduct(any(Product.class))).thenThrow(new IllegalArgumentException("Product with name 'Existing Product' already exists."));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request

        verify(productService, times(1)).createProduct(any(Product.class));
    }


    @Test
    @DisplayName("PUT /api/products/{id} should update existing product")
    void updateProduct_shouldUpdateProduct_whenFound() throws Exception {
        // Arrange
        Product updatedDetails = new Product(1L, "Laptop Pro", 1300.00, "Updated powerful laptop");
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(Optional.of(updatedDetails));

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop Pro")));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 404 Not Found when updating non-existent product")
    void updateProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        Product updatedDetails = new Product(99L, "NonExistent", 100.00, "Non-existent product");
        when(productService.updateProduct(eq(99L), any(Product.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should delete product successfully")
    void deleteProduct_shouldDeleteProduct_whenFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should return 404 Not Found when deleting non-existent product")
    void deleteProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
