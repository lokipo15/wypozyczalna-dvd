package com.studia.wypozyczalnia.exception;

/**
 * Wyjątek używany w przypadku błędnej walidacji danych wejściowych.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
