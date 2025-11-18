package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple in-memory repository for Product entities.
 * This simulates a database interaction without requiring a real database setup.
 * Uses ConcurrentHashMap for thread-safe storage and AtomicLong for ID generation.
 */
@Repository // Marks this class as a Spring repository component
public class ProductRepository {

    // Simulates a database table where keys are IDs and values are Product objects
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1); // For generating unique IDs

    /**
     * Finds all products currently stored in the repository.
     * @return A list of all products.
     */
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Finds a product by its ID.
     * @param id The ID of the product to find.
     * @return An Optional containing the product if found, or an empty Optional otherwise.
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    /**
     * Saves a new product or updates an existing one.
     * If the product ID is null, it's treated as a new product and a new ID is assigned.
     * If the product ID exists, the existing product is updated.
     * @param product The product to save or update.
     * @return The saved/updated product with its ID.
     */
    public Product save(Product product) {
        if (product.getId() == null) {
            // New product: assign a unique ID
            product.setId(nextId.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @return true if the product was found and deleted, false otherwise.
     */
    public boolean deleteById(Long id) {
        return products.remove(id) != null;
    }

    /**
     * Clears all products from the repository (useful for testing or full reset).
     */
    public void clear() {
        products.clear();
        nextId.set(1);
    }
}
