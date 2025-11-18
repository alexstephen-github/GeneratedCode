package com.example.restapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 * This is the domain model class.
 *
 * Lombok annotations:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @NoArgsConstructor: Generates a constructor with no arguments.
 * - @AllArgsConstructor: Generates a constructor with all arguments.
 *
 * JPA annotations:
 * - @Entity: Marks this class as a JPA entity, meaning it maps to a database table.
 * - @Table: Specifies the name of the database table (optional, defaults to class name).
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     * @Id: Marks this field as the primary key.
     * @GeneratedValue: Specifies the primary key generation strategy.
     *                  IDENTITY uses an auto-incrementing column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product. Cannot be null.
     * @Column(nullable = false): Ensures this column cannot be null in the database.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Price of the product. Cannot be null.
     */
    @Column(nullable = false)
    private Double price;
}
