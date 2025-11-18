package com.example.productapi.controller;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Configures Spring Boot for testing controllers
class ProductControllerTest {

    @Autowired // Injects MockMvc, which allows simulating HTTP requests
    private MockMvc mockMvc;

    @MockBean // Creates a mock instance of ProductService and adds it to Spring context
    private ProductService productService;

    @Autowired // Injects ObjectMapper to convert Java objects to JSON
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;
    private ProductResponse productResponse1;
    private ProductResponse productResponse2;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("A product for testing");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setQuantity(5);

        productResponse1 = new ProductResponse(1L, "Laptop", "Powerful laptop", new BigDecimal("1200.00"), 10);
        productResponse2 = new ProductResponse(2L, "Mouse", "Gaming mouse", new BigDecimal("50.00"), 50);
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        // Given
        ProductResponse createdProductResponse = new ProductResponse(3L, productRequest.getName(), productRequest.getDescription(), productRequest.getPrice(), productRequest.getQuantity());
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(createdProductResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        // Given
        ProductRequest invalidProductRequest = new ProductRequest(); // Name, price, quantity are null
        invalidProductRequest.setDescription("Invalid product");

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequest)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }


    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(productResponse1, productResponse2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponse1);

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Test Product");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("109.99"));
        updateRequest.setQuantity(7);

        ProductResponse updatedProductResponse = new ProductResponse(1L, "Updated Test Product", "Updated description", new BigDecimal("109.99"), 7);

        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(updatedProductResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Test Product"));
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        when(productService.updateProduct(anyLong(), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException("Product not found for update"));

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found for update"));
    }

    @Test
    void deleteProduct_shouldReturnNoContent_whenFound() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    void deleteProduct_shouldReturnNotFound_whenNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found for delete")).when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message").value("Product not found for delete"));
    }
}
