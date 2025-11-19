package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class) // Configures Spring to test only ProductController
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to send HTTP requests to the controller

    @MockBean // Creates a mock of ProductService and adds it to the Spring context
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Utility to convert objects to JSON

    private Product product1;
    private Product product2;
    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        product2 = new Product(2L, "Mouse", "Wireless mouse", 25.00, 50);

        productDTO1 = new ProductDTO(1L, "Laptop", "Powerful laptop", 1200.00, 10);
        productDTO2 = new ProductDTO(2L, "Mouse", "Wireless mouse", 25.00, 50);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Expect 2 items in the JSON array
                .andExpect(jsonPath("$[0].name", is(productDTO1.getName())))
                .andExpect(jsonPath("$[1].name", is(productDTO2.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductByIdFound() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(productDTO1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(productDTO1.getName())));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).getProductById(99L);
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductDTO newProductDTO = new ProductDTO(null, "Keyboard", "Mechanical keyboard", 150.00, 20);
        Product createdProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 150.00, 20);

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductDTO))) // Convert DTO to JSON
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductDTO updatedProductDTO = new ProductDTO(1L, "Laptop Updated", "Updated desc", 1300.00, 9);
        Product updatedEntity = new Product(1L, "Laptop Updated", "Updated desc", 1300.00, 9);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.name", is("Laptop Updated")))
                .andExpect(jsonPath("$.price", is(1300.00)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        ProductDTO updatedProductDTO = new ProductDTO(99L, "NonExistent", "Desc", 100.0, 1);

        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(put("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found

        verify(productService, times(1)).deleteProduct(99L);
    }
}
