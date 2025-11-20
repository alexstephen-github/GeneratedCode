package com.example.restapi.service;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.model.Product;

import java.util.List;

/**
 * Interface for the Product Service.
 * Defines the business logic operations available for Product entities.
 *
 * Using an interface promotes loose coupling and allows for multiple implementations
 * (e.g., a mock implementation for testing, a database implementation).
 */
public interface ProductService {

    /**
     * Retrieves all products.
     * @return A list of all products.
     */
    List<Product> getAllProducts();

    /**
     * Retrieves a product by its ID.
     * @param id The ID of the product to retrieve.
     * @return The found product.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    Product getProductById(Long id);

    /**
     * Creates a new product.
     * @param productDTO The DTO containing the data for the new product.
     * @return The created product entity.
     */
    Product createProduct(ProductDTO productDTO);

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productDTO The DTO containing the updated data.
     * @return The updated product entity.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    Product updateProduct(Long id, ProductDTO productDTO);

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @throws com.example.restapi.exception.ResourceNotFoundException if the product is not found.
     */
    void deleteProduct(Long id);
}
