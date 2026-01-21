package com.studia.wypozyczalnia.dto;

import java.time.Instant;
import java.math.BigDecimal;

public record TitleDto(Long id,
                       String name,
                       Integer year,
                       String genre,
                       String description,
                       String tvdbId,
                       BigDecimal pricePerDay,
                       Instant createdAt,
                       Instant updatedAt) {
}
