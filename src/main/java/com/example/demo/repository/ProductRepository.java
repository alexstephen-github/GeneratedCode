package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Optional: Indicates that this interface is a "Repository", useful for clarity and component scanning
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.
    // You can add custom query methods here, e.g.:
    Optional<Product> findByName(String name);
}
