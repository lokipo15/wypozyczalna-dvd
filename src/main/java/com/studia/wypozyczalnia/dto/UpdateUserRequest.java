package com.studia.wypozyczalnia.dto;

import com.studia.wypozyczalnia.domain.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank @Size(max = 255) String displayName,
    @Size(min = 8, max = 255) String password,
    @NotNull Role role,
    @NotNull Boolean active) {
}
