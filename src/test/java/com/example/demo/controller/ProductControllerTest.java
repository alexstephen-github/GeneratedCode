package com.example.demo.controller;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the ProductController.
 *
 * @WebMvcTest: Focuses on Spring MVC components. It auto-configures Spring MVC
 *              infrastructure and scans for @Controller, @ControllerAdvice, @JsonComponent,
 *              Filter, and WebMvcConfigurer beans.
 *              It doesn't load the full application context.
 * @MockBean: Used to add mock objects to the Spring application context.
 *            If a bean of the same type already exists, it will be replaced.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean
    private ProductService productService; // Mock the service layer dependency

    @DisplayName("Test GET /api/products - success")
    @Test
    void whenGetAllProducts_thenReturnListOfProductResponses() throws Exception {
        // Given
        List<ProductResponse> productResponses = Arrays.asList(
                new ProductResponse(1L, "Laptop", "High performance laptop", 1200.00),
                new ProductResponse(2L, "Mouse", "Wireless mouse", 25.00)
        );
        when(productService.getAllProducts()).thenReturn(productResponses);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @DisplayName("Test GET /api/products/{id} - success")
    @Test
    void whenGetProductById_thenReturnProductResponse() throws Exception {
        // Given
        ProductResponse productResponse = new ProductResponse(1L, "Laptop", "High performance laptop", 1200.00);
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).getProductById(1L);
    }

    @DisplayName("Test GET /api/products/{id} - not found")
    @Test
    void whenGetProductById_thenReturnsNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));

        verify(productService, times(1)).getProductById(99L);
    }

    @DisplayName("Test POST /api/products - success")
    @Test
    void whenCreateProduct_thenReturnCreatedProductResponse() throws Exception {
        // Given
        ProductRequest request = new ProductRequest("Keyboard", "Mechanical keyboard", 75.00);
        ProductResponse response = new ProductResponse(3L, "Keyboard", "Mechanical keyboard", 75.00);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Keyboard"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @DisplayName("Test POST /api/products - validation error")
    @Test
    void whenCreateProductWithInvalidData_thenReturnBadRequest() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest("", "No name", -10.00); // Invalid name and price

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.name").value("Product name is required"))
                .andExpect(jsonPath("$.validationErrors.price").value("Product price must be positive"));

        verify(productService, never()).createProduct(any(ProductRequest.class)); // Service should not be called
    }

    @DisplayName("Test PUT /api/products/{id} - success")
    @Test
    void whenUpdateProduct_thenReturnUpdatedProductResponse() throws Exception {
        // Given
        ProductRequest request = new ProductRequest("Updated Laptop", "Improved model", 1300.00);
        ProductResponse response = new ProductResponse(1L, "Updated Laptop", "Improved model", 1300.00);
        when(productService.updateProduct(anyLong(), any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1300.00));

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequest.class));
    }

    @DisplayName("Test PUT /api/products/{id} - not found")
    @Test
    void whenUpdateProduct_thenReturnsNotFound() throws Exception {
        // Given
        ProductRequest request = new ProductRequest("NonExistent", "Desc", 100.00);
        when(productService.updateProduct(99L, request)).thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).updateProduct(99L, request);
    }

    @DisplayName("Test PUT /api/products/{id} - validation error")
    @Test
    void whenUpdateProductWithInvalidData_thenReturnBadRequest() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest("Valid Name", "Desc", -100.00); // Invalid price

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.price").value("Product price must be positive"));

        verify(productService, never()).updateProduct(anyLong(), any(ProductRequest.class));
    }

    @DisplayName("Test DELETE /api/products/{id} - success")
    @Test
    void whenDeleteProduct_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @DisplayName("Test DELETE /api/products/{id} - not found")
    @Test
    void whenDeleteProduct_thenReturnsNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id: 99")).when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));

        verify(productService, times(1)).deleteProduct(99L);
    }
}
