package com.example.productapi.controller;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/v1/products") // Base path for all endpoints in this controller
@RequiredArgsConstructor // Lombok: Generates a constructor with all final fields
public class ProductController {

    private final ProductService productService; // Injected by Lombok's @RequiredArgsConstructor

    // POST /api/v1/products - Create a new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        // @Valid triggers validation defined in ProductRequest DTO
        // @RequestBody maps the incoming JSON to ProductRequest object
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created
    }

    // GET /api/v1/products - Get all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK
    }

    // GET /api/v1/products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        // @PathVariable extracts the ID from the URL path
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product); // Returns 200 OK
    }

    // PUT /api/v1/products/{id} - Update product by ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct); // Returns 200 OK
    }

    // DELETE /api/v1/products/{id} - Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
