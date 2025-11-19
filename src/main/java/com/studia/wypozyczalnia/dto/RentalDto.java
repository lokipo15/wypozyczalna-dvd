package com.studia.wypozyczalnia.dto;

import java.time.Instant;

public record RentalDto(Long id,
                        Long customerId,
                        Long copyId,
                        Instant rentedAt,
                        Instant dueAt,
                        Instant returnedAt,
                        Instant createdAt,
                        Instant updatedAt) {
}
