package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 *
 * @Repository: Indicates that an annotated class is a "Repository",
 * which is a mechanism for encapsulating storage, retrieval, and search behavior
 * which emulates a collection of objects. Spring Data JPA automatically provides
 * an implementation at runtime.
 *
 * JpaRepository<Product, Long>: Extends JpaRepository, providing standard CRUD operations
 * and pagination/sorting capabilities for the Product entity with Long as its primary key type.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be added here if needed,
    // e.g., Optional<Product> findByName(String name);
}
