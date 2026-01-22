package com.studia.wypozyczalnia.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.domain.Title;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.repository.RentalRepository;
import com.studia.wypozyczalnia.repository.DvdCopyRepository;
import com.studia.wypozyczalnia.repository.TitleRepository;
import com.studia.wypozyczalnia.service.InventoryService;
import com.studia.wypozyczalnia.service.command.inventory.AddCopyCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleCmd;
import com.studia.wypozyczalnia.exception.ValidationException;

/**
 * Implementacja serwisu zarządzającego tytułami i kopiami w magazynie.
 */
@Service
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final TitleRepository titleRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final RentalRepository rentalRepository;

    public InventoryServiceImpl(TitleRepository titleRepository, DvdCopyRepository dvdCopyRepository, RentalRepository rentalRepository) {
        this.titleRepository = titleRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * Tworzy nowy tytuł na podstawie komendy wejściowej.
     */
    @Override
    @Transactional
    public Title createTitle(CreateTitleCmd cmd) {
        var name = cmd.name();
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Title name is required");
        }
        if (cmd.pricePerDay() == null) {
            throw new ValidationException("Price per day is required");
        }
        validateRating(cmd.rating());
        titleRepository.findByNameIgnoreCase(name.trim()).ifPresent(existing -> {
            throw new ConflictException("Title name already exists");
        });
        var title = new Title();
        applyTitleData(title, cmd);
        return titleRepository.save(title);
    }

    /**
     * Aktualizuje dane tytułu.
     */
    @Override
    @Transactional
    public Title updateTitle(Long id, CreateTitleCmd cmd) {
        var title = getTitle(id);
        var name = cmd.name();
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Title name is required");
        }
        if (cmd.pricePerDay() == null) {
            throw new ValidationException("Price per day is required");
        }
        validateRating(cmd.rating());
        titleRepository.findByNameIgnoreCase(name.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("Title name already exists");
            }
        });
        applyTitleData(title, cmd);
        return titleRepository.save(title);
    }

    /**
     * Usuwa tytuł wraz z możliwymi do usunięcia kopiami.
     */
    @Override
    @Transactional
    public void deleteTitle(Long titleId) {
        var title = getTitle(titleId);
        var copies = dvdCopyRepository.findByTitleId(title.getId());
        if (!copies.isEmpty()) {
            var rentedCopies = copies.stream()
                .filter(copy -> copy.getStatus() == CopyStatus.RENTED)
                .toList();
            var deletableCopies = copies.stream()
                .filter(copy -> copy.getStatus() != CopyStatus.RENTED)
                .toList();
            if (!deletableCopies.isEmpty()) {
                var deletableIds = deletableCopies.stream().map(DvdCopy::getId).toList();
                rentalRepository.deleteByCopyIdIn(deletableIds);
                dvdCopyRepository.deleteAll(deletableCopies);
            }
            if (!rentedCopies.isEmpty()) {
                throw new ConflictException("Cannot delete title with rented copies");
            }
        }
        titleRepository.delete(title);
    }

    /**
     * Pobiera tytuł po identyfikatorze.
     */
    @Override
    public Title getTitle(Long id) {
        return titleRepository.findById(id).orElseThrow(() -> new NotFoundException("Title not found"));
    }

    /**
     * Wyszukuje tytuły lokalnie na podstawie frazy.
     */
    @Override
    public List<Title> searchLocalTitles(String query) {
        if (!StringUtils.hasText(query)) {
            return titleRepository.findAll();
        }
        return titleRepository.findByNameContainingIgnoreCase(query.trim());
    }

    /**
     * Dodaje nową kopię DVD do tytułu.
     */
    @Override
    @Transactional
    public DvdCopy addCopy(AddCopyCmd cmd) {
        var title = titleRepository.findById(cmd.titleId())
            .orElseThrow(() -> new NotFoundException("Title not found"));
        dvdCopyRepository.findByInventoryCode(cmd.inventoryCode())
            .ifPresent(existing -> {
                throw new ConflictException("Inventory code already exists");
            });
        var copy = new DvdCopy();
        copy.setTitle(title);
        copy.setInventoryCode(cmd.inventoryCode());
        copy.setStatus(CopyStatus.AVAILABLE);
        return dvdCopyRepository.save(copy);
    }

    /**
     * Aktualizuje status kopii.
     */
    @Override
    @Transactional
    public void updateCopyStatus(Long copyId, CopyStatus status) {
        var copy = getCopy(copyId);
        copy.setStatus(status);
        dvdCopyRepository.save(copy);
    }

    /**
     * Pobiera kopię po identyfikatorze.
     */
    @Override
    public DvdCopy getCopy(Long id) {
        return dvdCopyRepository.findById(id).orElseThrow(() -> new NotFoundException("Copy not found"));
    }

    /**
     * Zwraca kopie powiązane z tytułem, opcjonalnie filtrowane po statusie.
     */
    @Override
    public List<DvdCopy> findCopies(Long titleId, CopyStatus status) {
        getTitle(titleId);
        if (status != null) {
            return dvdCopyRepository.findByTitleIdAndStatus(titleId, status);
        }
        return dvdCopyRepository.findByTitleId(titleId);
    }

    /**
     * Zwraca dostępne kopie danego tytułu.
     */
    @Override
    public List<DvdCopy> findAvailableCopiesByTitle(Long titleId) {
        return findCopies(titleId, CopyStatus.AVAILABLE);
    }

    private void applyTitleData(Title title, CreateTitleCmd cmd) {
        title.setName(cmd.name().trim());
        title.setYear(cmd.year());
        title.setGenres(cmd.genres() != null ? cmd.genres() : List.of());
        title.setDescription(cmd.description());
        title.setTvdbId(cmd.tvdbId());
        title.setRating(cmd.rating());
        title.setPricePerDay(cmd.pricePerDay());
        title.setThumbnailUrl(cmd.thumbnailUrl());
        title.setImageUrl(cmd.imageUrl());
    }

    private void validateRating(BigDecimal rating) {
        if (rating == null) {
            return;
        }
        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.TEN) > 0) {
            throw new ValidationException("Rating must be between 0 and 10");
        }
    }

}
