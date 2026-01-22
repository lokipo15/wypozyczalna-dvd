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

import com.studia.wypozyczalnia.dto.CreateCustomerRequest;
import com.studia.wypozyczalnia.dto.CustomerDto;
import com.studia.wypozyczalnia.dto.UpdateCustomerRequest;
import com.studia.wypozyczalnia.mapper.CustomerMapper;
import com.studia.wypozyczalnia.service.CustomerService;
import com.studia.wypozyczalnia.service.command.customer.CreateCustomerCmd;
import com.studia.wypozyczalnia.service.command.customer.UpdateCustomerCmd;

import jakarta.validation.Valid;

/**
 * Kontroler do zarządzania klientami.
 */
@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Tworzy nowego klienta.
     */
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        var customer = customerService.createCustomer(new CreateCustomerCmd(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.toDto(customer));
    }

    /**
     * Aktualizuje dane klienta.
     */
    @PutMapping("/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        var customer = customerService.updateCustomer(id, new UpdateCustomerCmd(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.active()));
        return CustomerMapper.toDto(customer);
    }

    /**
     * Pobiera dane klienta.
     */
    @GetMapping("/{id}")
    public CustomerDto getCustomer(@PathVariable Long id) {
        return CustomerMapper.toDto(customerService.getCustomer(id));
    }

    /**
     * Zwraca listę klientów, opcjonalnie filtrowaną po frazie.
     */
    @GetMapping
    public List<CustomerDto> listCustomers(@RequestParam(name = "q", required = false) String query) {
        return CustomerMapper.toDtoList(customerService.findCustomers(query));
    }

    /**
     * Usuwa klienta.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
