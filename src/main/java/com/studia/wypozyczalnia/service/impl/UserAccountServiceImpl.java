package com.studia.wypozyczalnia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studia.wypozyczalnia.domain.UserAccount;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.repository.UserAccountRepository;
import com.studia.wypozyczalnia.service.UserAccountService;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;
import com.studia.wypozyczalnia.service.command.user.UpdateUserCmd;

@Service
@Transactional(readOnly = true)
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public UserAccount createUser(CreateUserCmd cmd) {
        userAccountRepository.findByUsernameIgnoreCase(cmd.username()).ifPresent(existing -> {
            throw new ConflictException("Username already used");
        });
        var user = new UserAccount();
        user.setUsername(cmd.username());
        user.setDisplayName(cmd.displayName());
        user.setRole(cmd.role());
        user.setActive(cmd.active() != null ? cmd.active() : Boolean.TRUE);
        return userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public UserAccount updateUser(Long id, UpdateUserCmd cmd) {
        var user = getUser(id);
        user.setDisplayName(cmd.displayName());
        user.setRole(cmd.role());
        user.setActive(cmd.active());
        return userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        var user = getUser(id);
        userAccountRepository.delete(user);
    }

    @Override
    public UserAccount getUser(Long id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<UserAccount> listUsers() {
        return userAccountRepository.findAll();
    }
}
