package com.studia.wypozyczalnia.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TitleDto(Long id,
                       String name,
                       Integer year,
                       List<String> genres,
                       String description,
                       String tvdbId,
                       BigDecimal rating,
                       BigDecimal pricePerDay,
                       String thumbnailUrl,
                       String imageUrl,
                       Instant createdAt,
                       Instant updatedAt) {
}
