package com.studia.wypozyczalnia.exception;

/**
 * Wyjątek sygnalizujący konflikt danych lub operacji.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
