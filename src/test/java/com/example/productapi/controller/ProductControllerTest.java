package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProductController using @WebMvcTest.
 * This annotation focuses on testing Spring MVC components. It loads only web-related components
 * (like the controller itself) and allows for testing controllers in isolation by mocking service layers.
 */
@WebMvcTest(ProductController.class) // Configures Spring MVC for testing only ProductController.
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests against the controller without starting a full HTTP server.

    @MockBean // Creates a Mockito mock of ProductService and adds it to the Spring application context.
              // This mock replaces the actual service, isolating the controller test.
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Utility for converting Java objects to/from JSON.

    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    void setUp() {
        // Initialize test DTOs before each test method.
        productDTO1 = new ProductDTO(1L, "Laptop", "High-performance laptop", 1200.00, 10);
        productDTO2 = new ProductDTO(2L, "Mouse", "Wireless optical mouse", 25.00, 50);
    }

    @Test
    void whenGetAllProducts_thenReturnProductList() throws Exception {
        // Given: Configure the mock service to return a list of DTOs.
        when(productService.getAllProducts()).thenReturn(Arrays.asList(productDTO1, productDTO2));

        // When & Then: Perform a GET request and assert the response.
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON)) // Set request content type
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$", hasSize(2))) // Expect a JSON array of size 2
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Mouse")));
    }

    @Test
    void whenGetProductByIdFound_thenReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(productDTO1));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L) // Request for ID 1
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void whenGetProductByIdNotFound_thenReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenReturn(Optional.empty()); // Simulate product not found

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L) // Request for non-existent ID
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void whenCreateProduct_thenReturnCreatedProduct() throws Exception {
        // Given
        ProductDTO newProductRequest = new ProductDTO(null, "Keyboard", "Mechanical keyboard", 75.00, 30);
        ProductDTO createdProductResponse = new ProductDTO(3L, "Keyboard", "Mechanical keyboard", 75.00, 30);

        // Configure mock service to return a DTO with a generated ID.
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(createdProductResponse);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductRequest))) // Convert DTO to JSON string for request body
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));
    }

    @Test
    void whenCreateProductWithInvalidData_thenReturnBadRequest() throws Exception {
        // Given: Invalid product data (empty name, negative price/quantity)
        ProductDTO invalidProductDTO = new ProductDTO(null, "", "Desc", -10.00, -5);

        // When & Then: Perform POST request and expect bad request due to validation errors.
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductDTO)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name", is("Product name is required"))) // Check for specific validation errors
                .andExpect(jsonPath("$.price", is("Price must be greater than 0")))
                .andExpect(jsonPath("$.quantity", is("Quantity cannot be negative")));
    }


    @Test
    void whenUpdateProductFound_thenReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO updatedProductRequest = new ProductDTO(1L, "Laptop Pro", "Updated description", 1300.00, 8);

        // Configure mock service to return the updated DTO within an Optional.
        when(productService.updateProduct(any(Long.class), any(ProductDTO.class)))
                .thenReturn(Optional.of(updatedProductRequest));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProductRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop Pro")));
    }

    @Test
    void whenUpdateProductNotFound_thenReturnNotFound() throws Exception {
        // Given
        ProductDTO nonExistentProductDTO = new ProductDTO(99L, "Non Existent", "N/A", 100.00, 1);
        when(productService.updateProduct(any(Long.class), any(ProductDTO.class)))
                .thenReturn(Optional.empty()); // Simulate product not found for update

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentProductDTO)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void whenDeleteProductFound_thenReturnNoContent() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true); // Simulate successful deletion

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    void whenDeleteProductNotFound_thenReturnNotFound() throws Exception {
        // Given
        when(productService.deleteProduct(99L)).thenReturn(false); // Simulate deletion of non-existent product

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }
}
