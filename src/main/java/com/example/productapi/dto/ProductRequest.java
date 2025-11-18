package com.example.productapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
public class ProductRequest {

    @NotBlank(message = "Product name cannot be empty") // Validation: field must not be null or empty string
    private String name;

    private String description;

    @NotNull(message = "Product price cannot be null") // Validation: field must not be null
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0") // Validation: min value for decimal
    private BigDecimal price;

    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Product quantity cannot be negative") // Validation: min value for integer
    private Integer quantity;
}
