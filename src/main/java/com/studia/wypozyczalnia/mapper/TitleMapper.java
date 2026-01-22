package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.Title;
import com.studia.wypozyczalnia.dto.TitleDto;

/**
 * Mapper konwertujący encje tytułów na DTO.
 */
public final class TitleMapper {

    private TitleMapper() {
    }

    /**
     * Konwertuje encję tytułu na DTO.
     */
    public static TitleDto toDto(Title entity) {
        if (entity == null) {
            return null;
        }
        return new TitleDto(
            entity.getId(),
            entity.getName(),
            entity.getYear(),
            entity.getGenres() != null ? entity.getGenres() : List.of(),
            entity.getDescription(),
            entity.getTvdbId(),
            entity.getRating(),
            entity.getPricePerDay(),
            entity.getThumbnailUrl(),
            entity.getImageUrl(),
            entity.getCreatedAt(),
            entity.getUpdatedAt());
    }

    /**
     * Konwertuje listę encji tytułów na listę DTO.
     */
    public static List<TitleDto> toDtoList(List<Title> titles) {
        return titles == null ? List.of() : titles.stream().filter(Objects::nonNull).map(TitleMapper::toDto).collect(Collectors.toList());
    }
}
