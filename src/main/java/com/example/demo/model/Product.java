package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products") // Optional: specifies table name if different from class name
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;

    @Column(nullable = false) // Not null constraint
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;
}
