package com.studia.wypozyczalnia.exception;

/**
 * Wyjątek oznaczający naruszenie reguł biznesowych.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
