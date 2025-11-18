package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the application.
 * This class is mapped to a database table named 'products'.
 *
 * @Entity: Marks this class as a JPA entity, meaning it corresponds to a table in the database.
 * @Table: Specifies the name of the database table for this entity.
 * @Data: A Lombok annotation to automatically generate getters, setters, toString(), equals(), and hashCode().
 * @NoArgsConstructor: A Lombok annotation to generate a no-argument constructor.
 * @AllArgsConstructor: A Lombok annotation to generate a constructor with all fields as arguments.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * The unique identifier for the product.
     * @Id: Marks this field as the primary key of the entity.
     * @GeneratedValue: Specifies how the primary key value is generated.
     *                  GenerationType.IDENTITY indicates that the database assigns an identity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product. Cannot be null.
     * @Column: Specifies the mapping of the field to a database column.
     *          'nullable = false' ensures the column cannot contain null values.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The price of the product. Cannot be null.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * The description of the product.
     */
    private String description;
}
