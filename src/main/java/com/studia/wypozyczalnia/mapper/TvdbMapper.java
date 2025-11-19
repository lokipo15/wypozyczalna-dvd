package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.dto.TvdbSearchItemDto;
import com.studia.wypozyczalnia.service.dto.TvdbSearchItem;

public final class TvdbMapper {

    private TvdbMapper() {
    }

    public static TvdbSearchItemDto toDto(TvdbSearchItem item) {
        if (item == null) {
            return null;
        }
        return new TvdbSearchItemDto(item.tvdbId(), item.name(), item.year(), item.overview());
    }

    public static List<TvdbSearchItemDto> toDtoList(List<TvdbSearchItem> items) {
        return items == null ? List.of() : items.stream().filter(Objects::nonNull).map(TvdbMapper::toDto).collect(Collectors.toList());
    }
}
