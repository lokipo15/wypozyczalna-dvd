package com.studia.wypozyczalnia.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTitleRequest(
    @NotBlank @Size(max = 255) String name,
    Integer year,
    List<@Size(max = 100) String> genres,
    @Size(max = 5000) String description,
    @Size(max = 100) String tvdbId,
    @DecimalMin(value = "0.0", inclusive = true) @DecimalMax(value = "10.0", inclusive = true) @Digits(integer = 2, fraction = 1) BigDecimal rating,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal pricePerDay,
    @Size(max = 1024) String thumbnailUrl,
    @Size(max = 1024) String imageUrl) {
}
