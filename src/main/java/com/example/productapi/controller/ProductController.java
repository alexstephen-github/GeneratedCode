package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product resources.
 * Handles incoming HTTP requests, delegates business logic to the ProductService,
 * and constructs appropriate HTTP responses.
 * Uses ProductDTO for request and response bodies, ensuring a clean API contract.
 */
@RestController // Marks this class as a REST controller. Combines @Controller and @ResponseBody.
@RequestMapping("/api/products") // Base path for all endpoints defined in this controller.
public class ProductController {

    private final ProductService productService;

    // Dependency Injection: Spring automatically injects an instance of ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     * HTTP GET request to /api/products
     * @return A list of ProductDTOs and HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // Returns 200 OK with the list of products in the response body.
    }

    /**
     * Retrieves a single product by its ID.
     * HTTP GET request to /api/products/{id}
     * @param id The ID of the product to retrieve.
     * @return The ProductDTO if found with HTTP 200 OK, or HTTP 404 Not Found if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // If product is found, wrap it in 200 OK response.
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found.
    }

    /**
     * Creates a new product.
     * HTTP POST request to /api/products
     * @param productDTO The ProductDTO containing the data for the new product.
     *                   The @Valid annotation triggers validation rules defined in ProductDTO.
     *                   The @RequestBody annotation maps the incoming JSON request body to the ProductDTO object.
     * @return The created ProductDTO with HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Returns 201 Created status.
    }

    /**
     * Updates an existing product.
     * HTTP PUT request to /api/products/{id}
     * @param id The ID of the product to update.
     * @param productDTO The ProductDTO containing the updated data.
     *                   The @Valid annotation triggers validation rules defined in ProductDTO.
     * @return The updated ProductDTO with HTTP 200 OK, or HTTP 404 Not Found if the product does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO)
                .map(ResponseEntity::ok) // If updated successfully, wrap it in 200 OK response.
                .orElse(ResponseEntity.notFound().build()); // If product not found, return 404 Not Found.
    }

    /**
     * Deletes a product.
     * HTTP DELETE request to /api/products/{id}
     * @param id The ID of the product to delete.
     * @return HTTP 204 No Content if deleted successfully, or HTTP 404 Not Found if the product does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content upon successful deletion.
        } else {
            return ResponseEntity.notFound().build(); // Returns 404 Not Found if the product could not be deleted (e.g., not found).
        }
    }
}
