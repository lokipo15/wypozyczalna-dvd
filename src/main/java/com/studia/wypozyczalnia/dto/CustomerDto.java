package com.studia.wypozyczalnia.dto;

import java.time.Instant;

public record CustomerDto(Long id,
                          String firstName,
                          String lastName,
                          String email,
                          String phone,
                          Boolean active,
                          Instant createdAt,
                          Instant updatedAt) {
}
