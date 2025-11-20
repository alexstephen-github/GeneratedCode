package com.example.restapi.controller;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.model.Product;
import com.example.restapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing products.
 * Exposes API endpoints for CRUD operations on Product resources.
 *
 * {@code @RestController}: Combines {@code @Controller} and {@code @ResponseBody}.
 *   {@code @Controller}: Marks the class as a Spring MVC controller.
 *   {@code @ResponseBody}: Indicates that the return value of the methods should be bound
 *     directly to the web response body.
 * {@code @RequestMapping("/api/products")}: Maps all requests starting with /api/products to this controller.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor for dependency injection of ProductService.
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     * GET /api/products
     * @return A {@code ResponseEntity} containing a list of products and HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Retrieves a single product by its ID.
     * GET /api/products/{id}
     * {@code @PathVariable}: Binds the path variable from the URL to the method parameter.
     * @param id The ID of the product to retrieve.
     * @return A {@code ResponseEntity} containing the product and HTTP 200 OK status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Creates a new product.
     * POST /api/products
     * {@code @RequestBody}: Binds the HTTP request body to the ProductDTO object.
     * {@code @Valid}: Triggers validation of the ProductDTO based on its annotations.
     * {@code @ResponseStatus(HttpStatus.CREATED)}: Sets the default response status to 201 Created.
     * @param productDTO The DTO containing the data for the new product.
     * @return A {@code ResponseEntity} containing the created product and HTTP 201 CREATED status.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Updates an existing product.
     * PUT /api/products/{id}
     * @param id The ID of the product to update.
     * @param productDTO The DTO containing the updated data.
     * @return A {@code ResponseEntity} containing the updated product and HTTP 200 OK status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/products/{id}
     * {@code @ResponseStatus(HttpStatus.NO_CONTENT)}: Sets the default response status to 204 No Content.
     * @param id The ID of the product to delete.
     * @return A {@code ResponseEntity} with no content and HTTP 204 NO_CONTENT status.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
