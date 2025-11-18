package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entities.
 * Extends JpaRepository to inherit standard CRUD operations and more.
 * Spring Data JPA automatically provides implementations for these methods at runtime.
 *
 * JpaRepository takes two generic parameters:
 * 1. The entity type (Product)
 * 2. The type of the entity's primary key (Long)
 *
 * @Repository: Stereotype annotation indicating that this is a repository component
 *              and provides a hint for component-scanning.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // You can define custom query methods here if needed.
    // Spring Data JPA can automatically generate queries based on method names.
    // Example: Find a product by its name
    Optional<Product> findByName(String name);
}
