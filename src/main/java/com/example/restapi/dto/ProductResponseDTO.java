package com.example.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for sending product data in API responses.
 * This object is used to format data before sending it back to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    /**
     * Unique identifier for the product.
     */
    private Long id;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Description of the product.
     */
    private String description;

    /**
     * Price of the product.
     */
    private Double price;
}
