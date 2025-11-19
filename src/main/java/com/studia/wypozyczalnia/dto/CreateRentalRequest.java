package com.studia.wypozyczalnia.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record CreateRentalRequest(@NotNull Long customerId,
                                   @NotNull Long copyId,
                                   Instant dueAt) {
}
