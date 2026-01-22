package com.studia.wypozyczalnia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.studia.wypozyczalnia.domain.UserAccount;
import com.studia.wypozyczalnia.domain.Customer;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.repository.UserAccountRepository;
import com.studia.wypozyczalnia.repository.CustomerRepository;
import com.studia.wypozyczalnia.service.UserAccountService;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;
import com.studia.wypozyczalnia.service.command.user.UpdateUserCmd;

/**
 * Implementacja serwisu zarządzającego kontami użytkowników.
 */
@Service
@Transactional(readOnly = true)
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository,
                                  CustomerRepository customerRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Tworzy nowe konto użytkownika z walidacją unikalności nazwy.
     */
    @Override
    @Transactional
    public UserAccount createUser(CreateUserCmd cmd) {
        userAccountRepository.findByUsernameIgnoreCase(cmd.username()).ifPresent(existing -> {
            throw new ConflictException("Username already used");
        });
        var user = new UserAccount();
        user.setUsername(cmd.username());
        user.setDisplayName(cmd.displayName());
        user.setPasswordHash(passwordEncoder.encode(cmd.password()));
        user.setRole(cmd.role());
        user.setActive(cmd.active() != null ? cmd.active() : Boolean.TRUE);
        if (cmd.customerId() != null) {
            Customer customer = customerRepository.findById(cmd.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));
            user.setCustomer(customer);
        }
        return userAccountRepository.save(user);
    }

    /**
     * Aktualizuje dane istniejącego użytkownika.
     */
    @Override
    @Transactional
    public UserAccount updateUser(Long id, UpdateUserCmd cmd) {
        var user = getUser(id);
        user.setDisplayName(cmd.displayName());
        if (cmd.password() != null && !cmd.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(cmd.password()));
        }
        user.setRole(cmd.role());
        user.setActive(cmd.active());
        return userAccountRepository.save(user);
    }

    /**
     * Usuwa konto użytkownika.
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        var user = getUser(id);
        userAccountRepository.delete(user);
    }

    /**
     * Pobiera użytkownika po identyfikatorze.
     */
    @Override
    public UserAccount getUser(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    /**
     * Zwraca listę wszystkich kont użytkowników.
     */
    @Override
    public List<UserAccount> listUsers() {
        return userAccountRepository.findAll();
    }
}
