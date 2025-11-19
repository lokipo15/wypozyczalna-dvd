package com.studia.wypozyczalnia.dto;

import java.time.Instant;

import com.studia.wypozyczalnia.domain.enums.Role;

public record UserAccountDto(Long id,
                             String username,
                             String displayName,
                             Role role,
                             Boolean active,
                             Instant createdAt,
                             Instant updatedAt) {
}
