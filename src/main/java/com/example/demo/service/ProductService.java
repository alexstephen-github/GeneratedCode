package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Product entities.
 * This class encapsulates the business logic and acts as an intermediary
 * between the Controller and the Repository layers.
 *
 * @Service: Stereotype annotation indicating that this class is a service component.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductService, injecting ProductRepository.
     * Spring will automatically inject an instance of ProductRepository because of @Autowired
     * (or implicitly if there's only one constructor).
     *
     * @param productRepository The repository for Product entities.
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products.
     *
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return An Optional containing the product if found, or empty if not found.
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Creates a new product.
     *
     * @param product The product object to be created.
     * @return The created product with its generated ID.
     */
    public Product createProduct(Product product) {
        // Here you might add business logic, validation, etc.
        // For example, checking if a product with the same name already exists.
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new IllegalArgumentException("Product with name '" + product.getName() + "' already exists.");
        }
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details.
     * @return An Optional containing the updated product if found, or empty if not found.
     */
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setDescription(productDetails.getDescription());
                    return productRepository.save(existingProduct);
                });
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return true if the product was found and deleted, false otherwise.
     */
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}
