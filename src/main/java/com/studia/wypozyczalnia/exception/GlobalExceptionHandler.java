package com.studia.wypozyczalnia.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Globalny handler wyjątków mapujący błędy na odpowiedzi HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    /**
     * Obsługuje brak zasobu.
     */
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return ErrorResponse.of("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * Obsługuje błędy walidacji biznesowej.
     */
    public ErrorResponse handleValidation(ValidationException ex) {
        return ErrorResponse.of("VALIDATION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * Obsługuje błędy walidacji adnotacji.
     */
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
        return ErrorResponse.of("VALIDATION_ERROR", "Constraint violation", details);
    }

    @ExceptionHandler({BusinessRuleException.class, ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    /**
     * Obsługuje konflikty danych.
     */
    public ErrorResponse handleConflict(RuntimeException ex) {
        return ErrorResponse.of("CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    /**
     * Obsługuje błędy usług zewnętrznych.
     */
    public ErrorResponse handleExternal(ExternalServiceException ex) {
        return ErrorResponse.of("EXTERNAL_SERVICE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * Obsługuje błędy walidacji ciała żądania.
     */
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacement) -> existing, LinkedHashMap::new));
        return ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details);
    }

    @ExceptionHandler(Exception.class)
    /**
     * Obsługuje pozostałe nieprzechwycone błędy.
     */
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        var response = ErrorResponse.of("INTERNAL_ERROR", "Unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
