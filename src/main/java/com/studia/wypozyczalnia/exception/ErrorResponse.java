package com.studia.wypozyczalnia.exception;

import java.util.Map;

public record ErrorResponse(String code, String message, Map<String, Object> details) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, Map.of());
    }

    public static ErrorResponse of(String code, String message, Map<String, Object> details) {
        return new ErrorResponse(code, message, details);
    }
}
