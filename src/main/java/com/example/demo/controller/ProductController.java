package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Product resources.
 * Handles HTTP requests and delegates business logic to ProductService.
 */
@RestController // Marks this class as a Spring REST controller
@RequestMapping("/api/products") // Base path for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for ProductController, injecting ProductService.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Retrieves all products.
     * @return A ResponseEntity containing a list of products and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET /api/products/{id}
     * Retrieves a product by its ID.
     * @param id The ID of the product to retrieve from the URL path.
     * @return A ResponseEntity containing the product and HTTP status OK if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * POST /api/products
     * Creates a new product.
     * @param product The product object sent in the request body.
     * @return A ResponseEntity containing the newly created product and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product.
     * @param id The ID of the product to update from the URL path.
     * @param productDetails The product object with updated details sent in the request body.
     * @return A ResponseEntity containing the updated product and HTTP status OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product by its ID.
     * @param id The ID of the product to delete from the URL path.
     * @return A ResponseEntity with HTTP status NO_CONTENT (204) upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
