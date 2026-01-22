package com.studia.wypozyczalnia.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.dto.DvdCopyDto;

/**
 * Mapper konwertujący encje kopii DVD na DTO.
 */
public final class DvdCopyMapper {

    private DvdCopyMapper() {
    }

    /**
     * Konwertuje pojedynczą kopię na DTO.
     */
    public static DvdCopyDto toDto(DvdCopy copy) {
        if (copy == null) {
            return null;
        }
        return new DvdCopyDto(
            copy.getId(),
            copy.getTitle() != null ? copy.getTitle().getId() : null,
            copy.getInventoryCode(),
            copy.getStatus(),
            copy.getCreatedAt(),
            copy.getUpdatedAt());
    }

    /**
     * Konwertuje listę kopii na listę DTO.
     */
    public static List<DvdCopyDto> toDtoList(List<DvdCopy> copies) {
        return copies == null ? List.of() : copies.stream().filter(Objects::nonNull).map(DvdCopyMapper::toDto).collect(Collectors.toList());
    }
}
