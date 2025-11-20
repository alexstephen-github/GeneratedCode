package com.example.restapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the REST API.
 * This class centralizes the handling of exceptions across all controllers.
 *
 * {@code @ControllerAdvice}: A specialization of {@code @Component} that makes a class
 * able to handle exceptions across the whole application.
 *
 * Extends {@code ResponseEntityExceptionHandler}: Provides a convenient base class
 * for {@code @ControllerAdvice} classes to handle standard Spring MVC exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@code ResourceNotFoundException}.
     *
     * @param ex The ResourceNotFoundException instance.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a custom error message and HTTP 404 status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "NOT_FOUND"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles generic {@code Exception} (catch-all for unhandled exceptions).
     *
     * @param ex The Exception instance.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a custom error message and HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@code MethodArgumentNotValidException} for validation errors.
     * This method overrides the default handling provided by {@code ResponseEntityExceptionHandler}.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @param headers The HTTP headers.
     * @param status The HTTP status.
     * @param request The current web request.
     * @return A {@code ResponseEntity} with a map of validation errors and HTTP 400 status.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                "BAD_REQUEST",
                errors
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * A simple structure to hold error details for API responses.
     */
    private static class ErrorDetails {
        private LocalDateTime timestamp;
        private String message;
        private String details;
        private String errorCode;
        private Map<String, String> validationErrors; // For MethodArgumentNotValidException

        public ErrorDetails(LocalDateTime timestamp, String message, String details, String errorCode) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.errorCode = errorCode;
        }

        public ErrorDetails(LocalDateTime timestamp, String message, String details, String errorCode, Map<String, String> validationErrors) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.errorCode = errorCode;
            this.validationErrors = validationErrors;
        }

        // Getters for all fields
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
        public String getErrorCode() { return errorCode; }
        public Map<String, String> getValidationErrors() { return validationErrors; }
    }
}
