package com.example.productapi.repository;

import com.example.productapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Product entities.
 * Extends JpaRepository to provide standard CRUD (Create, Read, Update, Delete) operations
 * and pagination/sorting capabilities out-of-the-box.
 * Spring automatically generates an implementation for this interface at runtime.
 */
@Repository // Marks this interface as a Spring Data JPA repository component. Spring's component scanning will find and register it.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA automatically provides methods like:
    // - save(Product product)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - count()
    // ... and many more.

    // Custom query methods can be added by following Spring Data JPA naming conventions, e.g.:
    // List<Product> findByNameContainingIgnoreCase(String name); // Finds products by name (case-insensitive substring match)

    // Or by using the @Query annotation for more complex JPQL or native SQL:
    // @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    // List<Product> findProductsByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
