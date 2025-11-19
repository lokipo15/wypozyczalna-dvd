package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.UserAccount;
import com.studia.wypozyczalnia.dto.UserAccountDto;

public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccountDto toDto(UserAccount entity) {
        if (entity == null) {
            return null;
        }
        return new UserAccountDto(
            entity.getId(),
            entity.getUsername(),
            entity.getDisplayName(),
            entity.getRole(),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt());
    }

    public static List<UserAccountDto> toDtoList(List<UserAccount> users) {
        return users == null ? List.of() : users.stream().filter(Objects::nonNull).map(UserAccountMapper::toDto).collect(Collectors.toList());
    }
}
