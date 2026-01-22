package com.studia.wypozyczalnia.service.command.rental;

import java.time.Instant;

public record CreateRentalCmd(Long userId, Long copyId, Instant dueAt) {
}
