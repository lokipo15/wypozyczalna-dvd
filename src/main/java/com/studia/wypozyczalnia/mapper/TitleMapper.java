package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.Title;
import com.studia.wypozyczalnia.dto.TitleDto;

public final class TitleMapper {

    private TitleMapper() {
    }

    public static TitleDto toDto(Title entity) {
        if (entity == null) {
            return null;
        }
        return new TitleDto(
            entity.getId(),
            entity.getName(),
            entity.getYear(),
            entity.getGenre(),
            entity.getDescription(),
            entity.getTvdbId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt());
    }

    public static List<TitleDto> toDtoList(List<Title> titles) {
        return titles == null ? List.of() : titles.stream().filter(Objects::nonNull).map(TitleMapper::toDto).collect(Collectors.toList());
    }
}
