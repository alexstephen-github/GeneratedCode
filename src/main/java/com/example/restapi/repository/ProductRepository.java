package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Spring Data JPA automatically provides the implementation for this interface at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom query methods here if needed,
    // e.g., List<Product> findByNameContainingIgnoreCase(String name);
}
