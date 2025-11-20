package com.example.productapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Product.
 * Used to transfer product data between different layers (e.g., Controller to Service, Service to Controller).
 * Helps decouple the API from the internal entity structure, allows for request/response specific fields,
 * and integrates JSR-380 validation.
 * Uses Lombok for boilerplate code reduction.
 */
@Data // Lombok annotation: Generates getters, setters, equals, hashCode, and toString methods
@NoArgsConstructor // Lombok annotation: Generates a no-argument constructor
@AllArgsConstructor // Lombok annotation: Generates a constructor with all fields as arguments
public class ProductDTO {

    private Long id; // ID is optional for creation requests, but present for responses

    @NotBlank(message = "Product name is required") // Ensures the name is not null and not empty after trimming
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0") // Price must be a positive decimal
    private Double price;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative") // Quantity cannot be negative
    private Integer quantity;
}
