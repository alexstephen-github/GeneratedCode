package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST Controller, handling incoming HTTP requests
@RequestMapping("/api/products") // Base URL path for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired // Injects ProductService instance
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping // Handles GET requests to /api/products
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK); // Returns 200 OK with the list of products
    }

    @GetMapping("/{id}") // Handles GET requests to /api/products/{id}
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK); // Returns 200 OK with the found product
    }

    @PostMapping // Handles POST requests to /api/products
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created with the new product
    }

    @PutMapping("/{id}") // Handles PUT requests to /api/products/{id}
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK); // Returns 200 OK with the updated product
    }

    @DeleteMapping("/{id}") // Handles DELETE requests to /api/products/{id}
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content
    }
}
