package com.studia.wypozyczalnia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTitleRequest(
    @NotBlank @Size(max = 255) String name,
    Integer year,
    @Size(max = 100) String genre,
    String description,
    @Size(max = 100) String tvdbId) {
}
