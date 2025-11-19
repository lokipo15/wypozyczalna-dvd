package com.studia.wypozyczalnia.service.command.rental;

import java.time.Instant;

public record CreateRentalCmd(Long customerId, Long copyId, Instant dueAt) {
}
