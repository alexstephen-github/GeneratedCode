package com.example.restapi.controller;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link ProductController}.
 *
 * {@code @WebMvcTest(ProductController.class)}: Focuses on Spring MVC components.
 *   - It auto-configures Spring MVC infrastructure and limits the beans to only those needed
 *     for testing controllers.
 *   - It does NOT load the full application context (e.g., service, repository layers are not loaded).
 *   - Used with {@code MockMvc} to send HTTP requests to the controller.
 * {@code @MockBean}: Adds mock objects to the Spring application context for dependencies
 *   that the controller needs (e.g., ProductService).
 */
@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests

    @MockBean // Creates a mock instance of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

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
    @DisplayName("Should create product and return 201 CREATED")
    void givenProductDTO_whenCreateProduct_thenReturnSavedProduct() throws Exception {
        // Given
        given(productService.createProduct(any(ProductDTO.class)))
                .willReturn(new Product(3L, productDTO.getName(), productDTO.getDescription(), productDTO.getPrice()));

        // When
        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)));

        // Then
        response.andDo(print()) // Print the request and response details
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name", is(productDTO.getName())))
                .andExpect(jsonPath("$.description", is(productDTO.getDescription())))
                .andExpect(jsonPath("$.price", is(productDTO.getPrice())));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST for invalid product creation")
    void givenInvalidProductDTO_whenCreateProduct_thenReturnBadRequest() throws Exception {
        // Given
        ProductDTO invalidProductDTO = new ProductDTO("", null, 0.0); // Invalid data

        // When
        ResultActions response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.validationErrors.name", is("Product name is required")))
                .andExpect(jsonPath("$.validationErrors.price", is("Product price must be greater than 0")));
    }


    @Test
    @DisplayName("Should get all products and return 200 OK")
    void givenListOfProducts_whenGetAllProducts_thenReturnProductsList() throws Exception {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        given(productService.getAllProducts()).willReturn(products);

        // When
        ResultActions response = mockMvc.perform(get("/api/products"));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.size()", is(products.size())))
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));
    }

    @Test
    @DisplayName("Should get product by ID and return 200 OK")
    void givenProductId_whenGetProductById_thenReturnProduct() throws Exception {
        // Given
        given(productService.getProductById(1L)).willReturn(product1);

        // When
        ResultActions response = mockMvc.perform(get("/api/products/{id}", 1L));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is(product1.getName())))
                .andExpect(jsonPath("$.price", is(product1.getPrice())));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when product by ID not found")
    void givenInvalidProductId_whenGetProductById_thenReturnNotFound() throws Exception {
        // Given
        given(productService.getProductById(anyLong())).willThrow(new ResourceNotFoundException("Product not found"));

        // When
        ResultActions response = mockMvc.perform(get("/api/products/{id}", 999L));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    @DisplayName("Should update product and return 200 OK")
    void givenUpdateProductDTO_whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO updatedDTO = new ProductDTO("Updated Laptop", "Updated desc", 1300.00);
        Product updatedProduct = new Product(1L, "Updated Laptop", "Updated desc", 1300.00);

        given(productService.updateProduct(anyLong(), any(ProductDTO.class))).willReturn(updatedProduct);

        // When
        ResultActions response = mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is(updatedProduct.getName())))
                .andExpect(jsonPath("$.price", is(updatedProduct.getPrice())));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when updating non-existent product")
    void givenInvalidProductId_whenUpdateProduct_thenReturnNotFound() throws Exception {
        // Given
        ProductDTO updateDTO = new ProductDTO("Updated Laptop", "Updated desc", 1300.00);
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).updateProduct(anyLong(), any(ProductDTO.class));

        // When
        ResultActions response = mockMvc.perform(put("/api/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }

    @Test
    @DisplayName("Should delete product and return 204 NO CONTENT")
    void givenProductId_whenDeleteProduct_thenReturnsNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(anyLong()); // Mock void method

        // When
        ResultActions response = mockMvc.perform(delete("/api/products/{id}", 1L));

        // Then
        response.andDo(print())
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deleting non-existent product")
    void givenInvalidProductId_whenDeleteProduct_thenReturnNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).deleteProduct(anyLong());

        // When
        ResultActions response = mockMvc.perform(delete("/api/products/{id}", 999L));

        // Then
        response.andDo(print())
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.message", is("Product not found")));
    }
}
