package com.example.restapi.service.impl;

import com.example.restapi.dto.ProductDTO;
import com.example.restapi.exception.ResourceNotFoundException;
import com.example.restapi.model.Product;
import com.example.restapi.repository.ProductRepository;
import com.example.restapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the ProductService interface.
 * Contains the business logic for managing products.
 *
 * {@code @Service}: Marks this class as a Spring service component.
 * {@code @Transactional}: Ensures that all operations within a method run within a single transaction.
 *   If any part of the method fails, the entire transaction is rolled back.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection of ProductRepository.
     * Spring automatically injects an instance of ProductRepository.
     */
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     * {@code @Transactional(readOnly = true)}: Optimization for read-only operations,
     * signaling to the underlying JPA provider that no modifications will occur.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     * Uses {@code orElseThrow} to throw a custom exception if the product is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Creates a new product by converting the DTO to an entity and saving it.
     */
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     * Retrieves the product, updates its fields from the DTO, and saves the changes.
     */
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());

        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product by its ID.
     * Checks if the product exists before attempting to delete.
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
