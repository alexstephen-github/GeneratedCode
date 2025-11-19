package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
public class ProductController {

    private final ProductService productService;

    @Autowired // Inject ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Helper method to convert Entity to DTO
    private ProductDTO convertToDto(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity());
    }

    // Helper method to convert DTO to Entity (for creation/update)
    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId()); // ID might be null for new products
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        return product;
    }

    // GET all products
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products); // Returns 200 OK with list of products
    }

    // GET product by ID
    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok) // Returns 200 OK with the product
                .orElse(ResponseEntity.notFound().build()); // Returns 404 Not Found if product doesn't exist
    }

    // Create a new product
    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(convertToDto(createdProduct), HttpStatus.CREATED); // Returns 201 CREATED
    }

    // Update an existing product
    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product productDetails = convertToEntity(productDTO);
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(convertToDto(updatedProduct)); // Returns 200 OK
    }

    // Delete a product
    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
