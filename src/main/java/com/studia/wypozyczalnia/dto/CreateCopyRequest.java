package com.studia.wypozyczalnia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCopyRequest(@NotNull Long titleId,
                                @NotBlank @Size(max = 100) String inventoryCode) {
}
