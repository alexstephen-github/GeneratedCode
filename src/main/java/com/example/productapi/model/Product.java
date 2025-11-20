package com.example.productapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity in the database.
 * Annotated with JPA annotations for Object-Relational Mapping (ORM).
 * Uses Lombok for reducing boilerplate code like getters/setters.
 */
@Entity // Marks this class as a JPA entity, meaning it will be mapped to a database table.
@Table(name = "products") // Specifies the actual table name in the database. If omitted, the class name 'Product' would be used.
@Data // Lombok annotation: Automatically generates getters, setters, equals(), hashCode(), and toString() methods.
@NoArgsConstructor // Lombok annotation: Generates a no-argument constructor, which is required by JPA.
@AllArgsConstructor // Lombok annotation: Generates a constructor with all fields as arguments.
public class Product {

    @Id // Marks this field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key generation strategy.
                                                       // IDENTITY means the database will auto-increment the ID (e.g., H2, MySQL).
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;

    // A custom constructor can be added if specific initialization logic is required,
    // but for simple cases, Lombok's @AllArgsConstructor is sufficient.
    // Example:
    // public Product(String name, String description, double price, int quantity) {
    //     this.name = name;
    //     this.description = description;
    //     this.price = price;
    //     this.quantity = quantity;
    // }
}
