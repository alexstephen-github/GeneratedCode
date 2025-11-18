package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product management.
 * Handles incoming HTTP requests and sends back HTTP responses.
 *
 * @RestController: A convenience annotation that combines @Controller and @ResponseBody.
 *                  It marks the class as a Spring MVC controller where every method
 *                  returns a domain object instead of a view, and the domain object
 *                  is converted to JSON/XML (depending on the client's Accept header).
 * @RequestMapping: Maps HTTP requests to handler methods of MVC and REST controllers.
 *                  Here, it sets the base URL for all endpoints in this controller to /api/products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for ProductController, injecting ProductService.
     *
     * @param productService The service for Product entities.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products : Get all products.
     *
     * @return A ResponseEntity containing a list of products and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK with the list of products
    }

    /**
     * GET /api/products/{id} : Get a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return A ResponseEntity containing the product if found (200 OK), or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // If product found, return 200 OK with product
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    /**
     * POST /api/products : Create a new product.
     *
     * @param product The product object to be created, sent in the request body.
     * @return A ResponseEntity containing the created product (201 Created), or 400 Bad Request
     *         if a product with the same name already exists.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created
        } catch (IllegalArgumentException e) {
            // Handle business logic errors, e.g., product with same name exists
            return ResponseEntity.badRequest().build(); // Or return a custom error body
        }
    }

    /**
     * PUT /api/products/{id} : Update an existing product.
     *
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details, sent in the request body.
     * @return A ResponseEntity containing the updated product (200 OK), or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(ResponseEntity::ok) // If product updated, return 200 OK with updated product
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    /**
     * DELETE /api/products/{id} : Delete a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return A ResponseEntity with 204 No Content if deleted, or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if product doesn't exist
        }
    }
}
