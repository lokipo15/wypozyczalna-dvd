package com.studia.wypozyczalnia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.studia.wypozyczalnia.domain.Customer;
import com.studia.wypozyczalnia.exception.BusinessRuleException;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.repository.CustomerRepository;
import com.studia.wypozyczalnia.repository.RentalRepository;
import com.studia.wypozyczalnia.service.CustomerService;
import com.studia.wypozyczalnia.service.command.customer.CreateCustomerCmd;
import com.studia.wypozyczalnia.service.command.customer.UpdateCustomerCmd;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RentalRepository rentalRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, RentalRepository rentalRepository) {
        this.customerRepository = customerRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public Customer createCustomer(CreateCustomerCmd cmd) {
        customerRepository.findByEmailIgnoreCase(cmd.email()).ifPresent(existing -> {
            throw new ConflictException("Email already used");
        });
        var customer = new Customer();
        customer.setFirstName(cmd.firstName());
        customer.setLastName(cmd.lastName());
        customer.setEmail(cmd.email());
        customer.setPhone(cmd.phone());
        customer.setActive(Boolean.TRUE);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerCmd cmd) {
        var customer = getCustomer(id);
        customerRepository.findByEmailIgnoreCase(cmd.email()).ifPresent(existing -> {
            if (!existing.getId().equals(customer.getId())) {
                throw new ConflictException("Email already used");
            }
        });
        customer.setFirstName(cmd.firstName());
        customer.setLastName(cmd.lastName());
        customer.setEmail(cmd.email());
        customer.setPhone(cmd.phone());
        customer.setActive(cmd.active());
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        var customer = getCustomer(id);
        if (rentalRepository.existsByCustomerIdAndReturnedAtIsNull(customer.getId())) {
            throw new BusinessRuleException("Customer has active rentals");
        }
        customerRepository.delete(customer);
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Override
    public List<Customer> findCustomers(String query) {
        if (!StringUtils.hasText(query)) {
            return customerRepository.findAll();
        }
        var q = query.trim();
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q);
    }
}
