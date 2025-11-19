package com.studia.wypozyczalnia.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTitleFromTvdbRequest(@NotBlank String tvdbId) {
}
