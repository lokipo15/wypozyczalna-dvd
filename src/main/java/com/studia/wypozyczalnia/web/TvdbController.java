package com.studia.wypozyczalnia.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.service.TvdbService;
import com.studia.wypozyczalnia.dto.TvdbSearchResultDto;

/**
 * Kontroler udostępniający wyszukiwanie tytułów w TVDB.
 */
@RestController
@RequestMapping("/api/tvdb")
@Validated
public class TvdbController {

    private final TvdbService tvdbService;

    public TvdbController(TvdbService tvdbService) {
        this.tvdbService = tvdbService;
    }

    /**
     * Wyszukuje filmy w TVDB na podstawie zapytania tekstowego.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','CLERK')")
    public java.util.List<TvdbSearchResultDto> search(@RequestParam("q") String query) {
        return tvdbService.search(query);
    }
}
