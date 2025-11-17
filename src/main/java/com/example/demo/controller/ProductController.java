package com.example.demo.controller;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product management.
 * Handles incoming HTTP requests and delegates to the ProductService for business logic.
 *
 * @RestController: A convenience annotation that combines @Controller and @ResponseBody.
 *                 It's specialized for building RESTful web services.
 * @RequestMapping: Maps HTTP requests to handler methods. Here, all methods in this
 *                  controller will be prefixed with "/api/products".
 * @RequiredArgsConstructor: Lombok annotation for constructor injection of final fields.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Handles GET requests to retrieve all products.
     *
     * @return A ResponseEntity containing a list of ProductResponse DTOs and HTTP status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Or return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Handles GET requests to retrieve a single product by its ID.
     *
     * @param id The ID of the product to retrieve. Extracted from the URL path.
     * @return A ResponseEntity containing the ProductResponse DTO and HTTP status 200 OK.
     *         Throws ResourceNotFoundException if product is not found, handled by GlobalExceptionHandler.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Handles POST requests to create a new product.
     *
     * @param productRequest The ProductRequest DTO containing product details, sent in the request body.
     *                       @Valid: Triggers validation on the DTO based on JSR-303 annotations (e.g., @NotBlank, @NotNull).
     * @return A ResponseEntity containing the newly created ProductResponse DTO and HTTP status 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Handles PUT requests to update an existing product.
     *
     * @param id The ID of the product to update.
     * @param productRequest The ProductRequest DTO containing updated product details.
     * @return A ResponseEntity containing the updated ProductResponse DTO and HTTP status 200 OK.
     *         Throws ResourceNotFoundException if product is not found, handled by GlobalExceptionHandler.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Handles DELETE requests to delete a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return A ResponseEntity with no content and HTTP status 204 NO_CONTENT.
     *         Throws ResourceNotFoundException if product is not found, handled by GlobalExceptionHandler.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
