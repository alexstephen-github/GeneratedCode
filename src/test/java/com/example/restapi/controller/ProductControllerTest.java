package com.example.restapi.controller;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.service.ProductService;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for the ProductController.
 * Uses @WebMvcTest to test the controller layer in isolation, without starting a full server.
 *
 * @WebMvcTest(ProductController.class): Focuses on Spring MVC components,
 *                                       only scanning beans relevant to web layers.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired // Automatically injects MockMvc
    private MockMvc mockMvc;

    @Autowired // Automatically injects ObjectMapper for JSON conversion
    private ObjectMapper objectMapper;

    @MockBean // Mocks ProductService, preventing it from interacting with other layers
    private ProductService productService;

    private ProductResponseDTO productResponseDTO1;
    private ProductResponseDTO productResponseDTO2;
    private ProductRequestDTO productRequestDTO;
    private ProductRequestDTO invalidProductRequestDTO;


    @BeforeEach
    void setUp() {
        productResponseDTO1 = new ProductResponseDTO(1L, "Laptop", "Powerful laptop", 1200.00);
        productResponseDTO2 = new ProductResponseDTO(2L, "Mouse", "Wireless mouse", 50.00);
        productRequestDTO = new ProductRequestDTO("Keyboard", "Mechanical keyboard", 150.00);
        invalidProductRequestDTO = new ProductRequestDTO("", "Description", -10.00); // Invalid data for testing
    }

    @Test
    @DisplayName("Should return list of products for GET /api/v1/products")
    void shouldReturnListOfProducts() throws Exception {
        // Given
        List<ProductResponseDTO> allProducts = Arrays.asList(productResponseDTO1, productResponseDTO2);
        when(productService.getAllProducts()).thenReturn(allProducts);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()", is(allProducts.size()))) // Check list size
                .andExpect(jsonPath("$[0].name", is(productResponseDTO1.getName())))
                .andExpect(jsonPath("$[1].name", is(productResponseDTO2.getName())));

        verify(productService, times(1)).getAllProducts(); // Verify service method was called
    }

    @Test
    @DisplayName("Should return product by ID for GET /api/v1/products/{id}")
    void shouldReturnProductById() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(productResponseDTO1);

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(productResponseDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productResponseDTO1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Should return 404 for GET /api/v1/products/{id} when product not found")
    void shouldReturnNotFoundWhenProductByIdNotFound() throws Exception {
        // Given
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    @DisplayName("Should create a new product for POST /api/v1/products")
    void shouldCreateNewProduct() throws Exception {
        // Given
        ProductResponseDTO createdProduct = new ProductResponseDTO(3L, productRequestDTO.getName(), productRequestDTO.getDescription(), productRequestDTO.getPrice());
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(createdProduct);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO))) // Convert DTO to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(createdProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdProduct.getName())));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for POST /api/v1/products with invalid data")
    void shouldReturnBadRequestWhenCreatingProductWithInvalidData() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductRequestDTO)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.name", is("Product name is required"))) // Check validation error message
                .andExpect(jsonPath("$.price", is("Product price must be non-negative")));

        verify(productService, never()).createProduct(any(ProductRequestDTO.class)); // Service should not be called
    }

    @Test
    @DisplayName("Should update an existing product for PUT /api/v1/products/{id}")
    void shouldUpdateExistingProduct() throws Exception {
        // Given
        ProductRequestDTO updateRequest = new ProductRequestDTO("Updated Laptop", "New description", 1300.00);
        ProductResponseDTO updatedProduct = new ProductResponseDTO(1L, "Updated Laptop", "New description", 1300.00);

        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(updatedProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())));

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 404 for PUT /api/v1/products/{id} when updating non-existent product")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
        // Given
        ProductRequestDTO updateRequest = new ProductRequestDTO("Updated Laptop", "New description", 1300.00);
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).updateProduct(anyLong(), any(ProductRequestDTO.class));

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 204 No Content for DELETE /api/v1/products/{id}")
    void shouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should return 404 for DELETE /api/v1/products/{id} when deleting non-existent product")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
