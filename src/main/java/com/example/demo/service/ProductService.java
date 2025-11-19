package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired // Inject ProductRepository
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create a new product
    public Product createProduct(Product product) {
        // Here you could add more business logic, validation, etc.
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Update an existing product
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update fields
        product.setName(productDetails.getName());
        product.setDescription(productDetails.setDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());

        return productRepository.save(product);
    }

    // Delete a product
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    // You can add more specific business logic here, e.g., check stock, apply discounts, etc.
}
