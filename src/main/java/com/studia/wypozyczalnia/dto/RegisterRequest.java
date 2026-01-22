package com.studia.wypozyczalnia.dto;

import com.studia.wypozyczalnia.domain.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(max = 100) String username,
    @NotBlank @Size(max = 255) String displayName,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotBlank @Email @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @NotNull Role role) {
}
