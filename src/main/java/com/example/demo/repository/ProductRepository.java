package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Optional: indicates that this is a DAO component, though JpaRepository usually implies it.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query methods (Spring Data JPA generates implementation automatically)
    Optional<Product> findByName(String name);
    List<Product> findByPriceGreaterThan(double price);
}
