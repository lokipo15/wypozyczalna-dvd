package com.studia.wypozyczalnia.service;

import java.util.List;

import com.studia.wypozyczalnia.domain.UserAccount;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;
import com.studia.wypozyczalnia.service.command.user.UpdateUserCmd;

public interface UserAccountService {

    UserAccount createUser(CreateUserCmd cmd);

    UserAccount updateUser(Long id, UpdateUserCmd cmd);

    void deleteUser(Long id);

    UserAccount getUser(Long id);

    List<UserAccount> listUsers();
}
