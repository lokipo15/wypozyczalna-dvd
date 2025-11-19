package com.studia.wypozyczalnia.dto;

import com.studia.wypozyczalnia.domain.enums.CopyStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateCopyStatusRequest(@NotNull CopyStatus status) {
}
