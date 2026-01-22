package com.studia.wypozyczalnia.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.dto.CreateTitleRequest;
import com.studia.wypozyczalnia.dto.DvdCopyDto;
import com.studia.wypozyczalnia.dto.TitleDto;
import com.studia.wypozyczalnia.mapper.DvdCopyMapper;
import com.studia.wypozyczalnia.mapper.TitleMapper;
import com.studia.wypozyczalnia.service.InventoryService;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleCmd;

import jakarta.validation.Valid;

/**
 * Kontroler do zarządzania tytułami i ich kopiami.
 */
@RestController
@RequestMapping("/api/titles")
@Validated
public class TitleController {

    private final InventoryService inventoryService;

    public TitleController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Tworzy nowy tytuł.
     */
    @PostMapping
    public ResponseEntity<TitleDto> createTitle(@Valid @RequestBody CreateTitleRequest request) {
        var cmd = new CreateTitleCmd(request.name(), request.year(), request.genres(), request.description(), request.tvdbId(), request.rating(), request.pricePerDay(), request.thumbnailUrl(), request.imageUrl());
        var title = inventoryService.createTitle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(TitleMapper.toDto(title));
    }

    /**
     * Aktualizuje istniejący tytuł.
     */
    @PutMapping("/{id}")
    public TitleDto updateTitle(@PathVariable Long id, @Valid @RequestBody CreateTitleRequest request) {
        var cmd = new CreateTitleCmd(request.name(), request.year(), request.genres(), request.description(), request.tvdbId(), request.rating(), request.pricePerDay(), request.thumbnailUrl(), request.imageUrl());
        return TitleMapper.toDto(inventoryService.updateTitle(id, cmd));
    }

    /**
     * Zwraca listę tytułów, opcjonalnie filtrowaną po frazie.
     */
    @GetMapping
    public List<TitleDto> listTitles(@RequestParam(name = "q", required = false) String query) {
        return TitleMapper.toDtoList(inventoryService.searchLocalTitles(query));
    }

    /**
     * Pobiera szczegóły tytułu.
     */
    @GetMapping("/{id}")
    public TitleDto getTitle(@PathVariable Long id) {
        return TitleMapper.toDto(inventoryService.getTitle(id));
    }

    /**
     * Usuwa tytuł z systemu.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable Long id) {
        inventoryService.deleteTitle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Zwraca kopie tytułu z opcjonalnym filtrem statusu.
     */
    @GetMapping("/{id}/copies")
    public List<DvdCopyDto> listCopies(@PathVariable Long id, @RequestParam(name = "status", required = false) CopyStatus status) {
        return DvdCopyMapper.toDtoList(inventoryService.findCopies(id, status));
    }
}
