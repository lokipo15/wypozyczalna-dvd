package com.studia.wypozyczalnia.exception;

/**
 * Wyjątek reprezentujący błąd w komunikacji z usługą zewnętrzną.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
