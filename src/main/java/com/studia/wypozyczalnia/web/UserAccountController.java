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
import org.springframework.web.bind.annotation.RestController;

import com.studia.wypozyczalnia.dto.CreateUserRequest;
import com.studia.wypozyczalnia.dto.UpdateUserRequest;
import com.studia.wypozyczalnia.dto.UserAccountDto;
import com.studia.wypozyczalnia.mapper.UserAccountMapper;
import com.studia.wypozyczalnia.service.UserAccountService;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;
import com.studia.wypozyczalnia.service.command.user.UpdateUserCmd;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    public ResponseEntity<UserAccountDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        var user = userAccountService.createUser(new CreateUserCmd(
            request.username(),
            request.displayName(),
            request.role(),
            request.active()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserAccountMapper.toDto(user));
    }

    @PutMapping("/{id}")
    public UserAccountDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        var user = userAccountService.updateUser(id, new UpdateUserCmd(
            request.displayName(),
            request.role(),
            request.active()));
        return UserAccountMapper.toDto(user);
    }

    @GetMapping
    public List<UserAccountDto> listUsers() {
        return UserAccountMapper.toDtoList(userAccountService.listUsers());
    }

    @GetMapping("/{id}")
    public UserAccountDto getUser(@PathVariable Long id) {
        return UserAccountMapper.toDto(userAccountService.getUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
