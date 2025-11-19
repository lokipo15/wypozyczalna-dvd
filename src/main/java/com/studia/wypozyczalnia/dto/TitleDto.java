package com.studia.wypozyczalnia.dto;

import java.time.Instant;

public record TitleDto(Long id,
                       String name,
                       Integer year,
                       String genre,
                       String description,
                       String tvdbId,
                       Instant createdAt,
                       Instant updatedAt) {
}
