package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.Rental;
import com.studia.wypozyczalnia.dto.RentalDto;

public final class RentalMapper {

    private RentalMapper() {
    }

    public static RentalDto toDto(Rental rental) {
        if (rental == null) {
            return null;
        }
        return new RentalDto(
            rental.getId(),
            rental.getCustomer() != null ? rental.getCustomer().getId() : null,
            rental.getCopy() != null ? rental.getCopy().getId() : null,
            rental.getRentedAt(),
            rental.getDueAt(),
            rental.getReturnedAt(),
            rental.getCreatedAt(),
            rental.getUpdatedAt());
    }

    public static List<RentalDto> toDtoList(List<Rental> rentals) {
        return rentals == null ? List.of() : rentals.stream().filter(Objects::nonNull).map(RentalMapper::toDto).collect(Collectors.toList());
    }
}
