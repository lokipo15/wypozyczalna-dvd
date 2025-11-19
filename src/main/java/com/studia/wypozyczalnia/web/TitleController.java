package com.studia.wypozyczalnia.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.dto.CreateTitleFromTvdbRequest;
import com.studia.wypozyczalnia.dto.CreateTitleRequest;
import com.studia.wypozyczalnia.dto.DvdCopyDto;
import com.studia.wypozyczalnia.dto.TitleDto;
import com.studia.wypozyczalnia.dto.TvdbSearchItemDto;
import com.studia.wypozyczalnia.mapper.DvdCopyMapper;
import com.studia.wypozyczalnia.mapper.TitleMapper;
import com.studia.wypozyczalnia.mapper.TvdbMapper;
import com.studia.wypozyczalnia.service.InventoryService;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleFromTvdbCmd;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/titles")
@Validated
public class TitleController {

    private final InventoryService inventoryService;

    public TitleController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<TitleDto> createTitle(@Valid @RequestBody CreateTitleRequest request) {
        var cmd = new CreateTitleCmd(request.name(), request.year(), request.genre(), request.description(), request.tvdbId());
        var title = inventoryService.createOrUpdateTitle(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(TitleMapper.toDto(title));
    }

    @GetMapping
    public List<TitleDto> listTitles(@RequestParam(name = "q", required = false) String query) {
        return TitleMapper.toDtoList(inventoryService.searchLocalTitles(query));
    }

    @GetMapping("/{id}")
    public TitleDto getTitle(@PathVariable Long id) {
        return TitleMapper.toDto(inventoryService.getTitle(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable Long id) {
        inventoryService.deleteTitle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/copies")
    public List<DvdCopyDto> listCopies(@PathVariable Long id, @RequestParam(name = "status", required = false) CopyStatus status) {
        return DvdCopyMapper.toDtoList(inventoryService.findCopies(id, status));
    }

    @GetMapping("/search/tvdb")
    public List<TvdbSearchItemDto> searchTvdb(@RequestParam("q") String query) {
        return TvdbMapper.toDtoList(inventoryService.searchTvdb(query));
    }

    @PostMapping("/from-tvdb")
    public ResponseEntity<TitleDto> createFromTvdb(@Valid @RequestBody CreateTitleFromTvdbRequest request) {
        var title = inventoryService.createTitleFromTvdb(new CreateTitleFromTvdbCmd(request.tvdbId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(TitleMapper.toDto(title));
    }
}
