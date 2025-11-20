package com.example.restapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 *
 * {@code @Entity}: Marks this class as a JPA entity, meaning it maps to a database table.
 * {@code @Table}: Specifies the table name in the database.
 * {@code @Data} (Lombok): Generates getters, setters, toString, equals, and hashCode methods.
 * {@code @NoArgsConstructor} (Lombok): Generates a constructor with no arguments.
 * {@code @AllArgsConstructor} (Lombok): Generates a constructor with all arguments.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * The unique identifier for the product.
     * {@code @Id}: Marks this field as the primary key.
     * {@code @GeneratedValue}: Configures the way the primary key is generated.
     *   - GenerationType.IDENTITY: The database assigns the primary key (e.g., auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     * {@code @Column}: Specifies column details like name, length, and nullability.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * A description of the product.
     */
    @Column(length = 500)
    private String description;

    /**
     * The price of the product.
     */
    @Column(nullable = false)
    private Double price;
}
