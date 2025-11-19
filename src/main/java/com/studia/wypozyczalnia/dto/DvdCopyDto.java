package com.studia.wypozyczalnia.dto;

import java.time.Instant;

import com.studia.wypozyczalnia.domain.enums.CopyStatus;

public record DvdCopyDto(Long id,
                         Long titleId,
                         String inventoryCode,
                         CopyStatus status,
                         Instant createdAt,
                         Instant updatedAt) {
}
