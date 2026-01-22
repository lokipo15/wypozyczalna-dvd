package com.studia.wypozyczalnia.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.dto.CreateRentalRequest;
import com.studia.wypozyczalnia.dto.RentalDto;
import com.studia.wypozyczalnia.dto.ReturnRentalRequest;
import com.studia.wypozyczalnia.mapper.RentalMapper;
import com.studia.wypozyczalnia.service.RentalService;
import com.studia.wypozyczalnia.service.command.rental.CreateRentalCmd;
import com.studia.wypozyczalnia.service.command.rental.ReturnRentalCmd;

import jakarta.validation.Valid;

/**
 * Kontroler obsługujący operacje na wypożyczeniach.
 */
@RestController
@RequestMapping("/api/rentals")
@Validated
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Tworzy nowe wypożyczenie dla wskazanego użytkownika i kopii.
     */
    @PostMapping
    public ResponseEntity<RentalDto> createRental(@Valid @RequestBody CreateRentalRequest request) {
        var rental = rentalService.createRental(new CreateRentalCmd(
            request.userId(),
            request.copyId(),
            request.dueAt()));
        return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDto(rental));
    }

    /**
     * Zwraca aktywne wypożyczenie.
     */
    @PostMapping("/return")
    public RentalDto returnRental(@Valid @RequestBody ReturnRentalRequest request) {
        var rental = rentalService.returnRental(new ReturnRentalCmd(request.rentalId(), request.copyId()));
        return RentalMapper.toDto(rental);
    }

    /**
     * Zwraca wszystkie aktywne wypożyczenia.
     */
    @GetMapping("/active")
    public List<RentalDto> listActive() {
        return RentalMapper.toDtoList(rentalService.listActiveRentals());
    }

    /**
     * Zwraca historię wypożyczeń dla podanego klienta.
     */
    @GetMapping("/customer/{customerId}/history")
    public List<RentalDto> customerHistory(@PathVariable Long customerId) {
        log.debug("Historia wypożyczeń dla klienta id={}", customerId);
        return RentalMapper.toDtoList(rentalService.customerHistory(customerId));
    }

    /**
     * Zwraca historię wypożyczeń dla podanego użytkownika (powiązanego klienta).
     */
    @GetMapping("/user/{userId}/history")
    public List<RentalDto> userHistory(@PathVariable Long userId) {
        log.debug("Historia wypożyczeń dla użytkownika id={}", userId);
        return RentalMapper.toDtoList(rentalService.customerHistory(userId));
    }

    /**
     * Pobiera szczegóły wypożyczenia.
     */
    @GetMapping("/{id}")
    public RentalDto getRental(@PathVariable Long id) {
        return RentalMapper.toDto(rentalService.getRental(id));
    }
}
