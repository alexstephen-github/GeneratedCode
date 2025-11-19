package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for Product representation (request/response)
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class ProductDTO {
    private Long id; // ID might be null for creation requests
    private String name;
    private String description;
    private double price;
    private int quantity;
}
