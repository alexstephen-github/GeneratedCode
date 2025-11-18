package com.example.restapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating a product.
 * This object is used to receive data from incoming API requests.
 * It includes validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    /**
     * Name of the product. Must not be null or empty.
     * @NotBlank: Validates that the annotated string is not null and not empty or whitespace.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * Description of the product. Can be null or empty.
     */
    private String description;

    /**
     * Price of the product. Must not be null and must be greater than or equal to 0.0.
     * @NotNull: Validates that the annotated value is not null.
     * @DecimalMin: Validates that the annotated number has a value greater than or equal to the specified minimum.
     */
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product price must be non-negative")
    private Double price;
}
