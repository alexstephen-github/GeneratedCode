package com.example.restapi.repository;

import com.example.restapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entities.
 *
 * {@code @Repository}: Indicates that this is a DAO (Data Access Object) component.
 * Extends {@code JpaRepository<Product, Long>}:
 * - Provides standard CRUD (Create, Read, Update, Delete) operations for the Product entity.
 * - The first type parameter is the entity type (Product).
 * - The second type parameter is the type of the entity's primary key (Long).
 * Spring Data JPA automatically provides an implementation at runtime.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods can be added here if needed,
    // Spring Data JPA can infer queries from method names (e.g., findByNameContaining)
}
