package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) for sending product details as a response.
 * This can be useful to hide certain entity fields (e.g., sensitive data) or to combine fields
 * from multiple entities, decoupling the API response from the internal entity structure.
 *
 * @Data: Lombok annotation for getters, setters, equals, hashCode, and toString.
 * @NoArgsConstructor: Lombok annotation for a no-argument constructor.
 * @AllArgsConstructor: Lombok annotation for an all-argument constructor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
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
