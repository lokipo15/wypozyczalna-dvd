package com.studia.wypozyczalnia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

public record CreateTitleRequest(
    @NotBlank @Size(max = 255) String name,
    Integer year,
    @Size(max = 100) String genre,
    String description,
    @Size(max = 100) String tvdbId,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) java.math.BigDecimal pricePerDay) {
}
