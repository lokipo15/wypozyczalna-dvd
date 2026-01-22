package com.studia.wypozyczalnia.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.dto.CreateCopyRequest;
import com.studia.wypozyczalnia.dto.DvdCopyDto;
import com.studia.wypozyczalnia.dto.UpdateCopyStatusRequest;
import com.studia.wypozyczalnia.mapper.DvdCopyMapper;
import com.studia.wypozyczalnia.service.InventoryService;
import com.studia.wypozyczalnia.service.command.inventory.AddCopyCmd;

import jakarta.validation.Valid;

/**
 * Kontroler zarządzający kopiami DVD.
 */
@RestController
@RequestMapping("/api/copies")
@Validated
public class CopyController {

    private final InventoryService inventoryService;

    public CopyController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Tworzy nową kopię DVD dla wskazanego tytułu.
     */
    @PostMapping
    public ResponseEntity<DvdCopyDto> createCopy(@Valid @RequestBody CreateCopyRequest request) {
        var copy = inventoryService.addCopy(new AddCopyCmd(request.titleId(), request.inventoryCode()));
        return ResponseEntity.status(HttpStatus.CREATED).body(DvdCopyMapper.toDto(copy));
    }

    /**
     * Aktualizuje status kopii DVD.
     */
    @PatchMapping("/{id}/status")
    public DvdCopyDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateCopyStatusRequest request) {
        inventoryService.updateCopyStatus(id, request.status());
        return DvdCopyMapper.toDto(inventoryService.getCopy(id));
    }

    /**
     * Pobiera szczegóły kopii DVD.
     */
    @GetMapping("/{id}")
    public DvdCopyDto getCopy(@PathVariable Long id) {
        return DvdCopyMapper.toDto(inventoryService.getCopy(id));
    }
}
