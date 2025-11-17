package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) for creating or updating a product.
 * Used to encapsulate request data, providing validation and decoupling the API from the internal entity structure.
 *
 * @Data: Lombok annotation for getters, setters, equals, hashCode, and toString.
 * @NoArgsConstructor: Lombok annotation for a no-argument constructor.
 * @AllArgsConstructor: Lombok annotation for an all-argument constructor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    /**
     * Name of the product.
     * @NotBlank: Validates that the string is not null and the trimmed length is greater than zero.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * Description of the product.
     * No specific validation here, as it can be optional or empty.
     */
    private String description;

    /**
     * Price of the product.
     * @NotNull: Validates that the value is not null.
     * @Positive: Validates that the numeric value is strictly positive.
     */
    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be positive")
    private Double price;
}
