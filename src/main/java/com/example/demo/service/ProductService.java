package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Product entities.
 * This layer encapsulates business logic and interacts with the repository.
 *
 * @Service: Marks this class as a Spring Service, indicating it holds business logic.
 * @RequiredArgsConstructor: A Lombok annotation that generates a constructor with
 *                          arguments for all fields marked with 'final'. This is
 *                          a convenient way to achieve constructor-based dependency injection.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Converts a Product entity to a ProductResponse DTO.
     * @param product The Product entity to convert.
     * @return The corresponding ProductResponse DTO.
     */
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }

    /**
     * Retrieves all products.
     *
     * @return A list of ProductResponse DTOs.
     * @Transactional(readOnly = true): Indicates that the method only reads from the database.
     *                                 Spring optimizes read-only transactions for performance.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product.
     * @return The ProductResponse DTO if found.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     * @Transactional(readOnly = true): Indicates that the method only reads from the database.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    /**
     * Creates a new product.
     *
     * @param productRequest The ProductRequest DTO containing product details.
     * @return The ProductResponse DTO of the newly created product.
     * @Transactional: Indicates that the method should be executed within a transaction.
     *                 If the method completes successfully, the transaction is committed;
     *                 otherwise, it is rolled back.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product(
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getPrice()
        );
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param productRequest The ProductRequest DTO containing updated product details.
     * @return The ProductResponse DTO of the updated product.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     * @Transactional: Ensures atomicity of the update operation.
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException If no product is found with the given ID.
     * @Transactional: Ensures atomicity of the delete operation.
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
