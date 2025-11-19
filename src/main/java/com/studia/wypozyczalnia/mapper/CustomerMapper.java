package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.Customer;
import com.studia.wypozyczalnia.dto.CustomerDto;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerDto toDto(Customer entity) {
        if (entity == null) {
            return null;
        }
        return new CustomerDto(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt());
    }

    public static List<CustomerDto> toDtoList(List<Customer> customers) {
        return customers == null ? List.of() : customers.stream().filter(Objects::nonNull).map(CustomerMapper::toDto).collect(Collectors.toList());
    }
}
