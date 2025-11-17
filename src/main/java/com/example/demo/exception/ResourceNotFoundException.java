package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a resource (e.g., Product) was not found.
 *
 * @ResponseStatus: Causes Spring to return the specified HTTP status code (404 NOT FOUND)
 * whenever this exception is thrown from a controller method.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message (e.g., "Product not found with id: 123").
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
