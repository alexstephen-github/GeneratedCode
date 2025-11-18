package com.example.restapi.controller;

import com.example.restapi.dto.ProductRequestDTO;
import com.example.restapi.dto.ProductResponseDTO;
import com.example.restapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product-related API endpoints.
 * Handles HTTP requests and delegates business logic to the ProductService.
 *
 * @RestController: Combines @Controller and @ResponseBody, meaning every method
 *                  returns a domain object instead of a view, and the domain
 *                  object is automatically marshaled into JSON/XML.
 * @RequestMapping("/api/v1/products"): Base URL for all endpoints in this controller.
 * @RequiredArgsConstructor: Lombok annotation for constructor injection of ProductService.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService; // Injected via constructor

    /**
     * Handles GET requests to retrieve all products.
     * GET /api/v1/products
     * @return ResponseEntity with a list of ProductResponseDTOs and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK
    }

    /**
     * Handles GET requests to retrieve a product by ID.
     * GET /api/v1/products/{id}
     * @param id The ID of the product to retrieve.
     * @return ResponseEntity with the ProductResponseDTO and HTTP status 200 (OK).
     *         Returns 404 (Not Found) if the product does not exist (handled by ResourceNotFoundException).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product); // Returns 200 OK
    }

    /**
     * Handles POST requests to create a new product.
     * POST /api/v1/products
     * @param productRequestDTO The ProductRequestDTO containing data for the new product.
     *                          @Valid: Triggers validation on the incoming DTO.
     * @return ResponseEntity with the created ProductResponseDTO and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        // Using .created() to return 201 Created status and include Location header
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Handles PUT requests to update an existing product.
     * PUT /api/v1/products/{id}
     * @param id The ID of the product to update.
     * @param productRequestDTO The ProductRequestDTO containing updated data.
     *                          @Valid: Triggers validation on the incoming DTO.
     * @return ResponseEntity with the updated ProductResponseDTO and HTTP status 200 (OK).
     *         Returns 404 (Not Found) if the product does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(updatedProduct); // Returns 200 OK
    }

    /**
     * Handles DELETE requests to delete a product by ID.
     * DELETE /api/v1/products/{id}
     * @param id The ID of the product to delete.
     * @return ResponseEntity with no content and HTTP status 204 (No Content).
     *         Returns 404 (Not Found) if the product does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
