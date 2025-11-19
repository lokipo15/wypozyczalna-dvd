package com.studia.wypozyczalnia.dto;

import jakarta.validation.constraints.AssertTrue;

public record ReturnRentalRequest(Long rentalId, Long copyId) {

    @AssertTrue(message = "Either rentalId or copyId must be provided")
    public boolean hasIdentifier() {
        return rentalId != null || copyId != null;
    }
}
