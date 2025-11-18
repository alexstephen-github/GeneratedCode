package com.example.productapi.repository;

import com.example.productapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Not strictly necessary, but good practice to explicitly mark
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides CRUD operations for Product entity with Long as ID type

    // You can define custom query methods here, Spring Data JPA will implement them
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}
