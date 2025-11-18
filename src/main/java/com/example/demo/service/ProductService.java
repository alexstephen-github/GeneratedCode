package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service layer for managing Product entities.
 * Contains business logic and orchestrates data access via ProductRepository.
 */
@Service // Marks this class as a Spring service component
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductService, injecting ProductRepository.
     * Spring's @Autowired handles the dependency injection.
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products.
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     * Throws ResponseStatusException if the product is not found.
     * @param id The ID of the product to retrieve.
     * @return The found Product.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
    }

    /**
     * Creates a new product.
     * The ID of the product in the input object is ignored; a new ID is always generated.
     * @param product The product object to create.
     * @return The newly created product with its assigned ID.
     */
    public Product createProduct(Product product) {
        // Ensure that we create a new product, ignoring any client-provided ID for new creation
        product.setId(null);
        return productRepository.save(product);
    }

    /**
     * Updates an existing product.
     * Throws ResponseStatusException if the product to update is not found.
     * @param id The ID of the product to update.
     * @param productDetails The product object containing updated details.
     * @return The updated Product.
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        // Do not update ID. The ID from productDetails is ignored, existingProduct's ID is retained.
        return productRepository.save(existingProduct);
    }

    /**
     * Deletes a product by its ID.
     * Throws ResponseStatusException if the product to delete is not found.
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
    }
}
