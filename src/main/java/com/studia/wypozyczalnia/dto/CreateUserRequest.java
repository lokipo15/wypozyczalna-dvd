package com.studia.wypozyczalnia.dto;

import com.studia.wypozyczalnia.domain.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Size(max = 100) String username,
    @NotBlank @Size(max = 255) String displayName,
    @NotNull Role role,
    @NotNull Boolean active) {
}
