package com.studia.wypozyczalnia.exception;

/**
 * Wyjątek informujący o braku wymaganego zasobu.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
