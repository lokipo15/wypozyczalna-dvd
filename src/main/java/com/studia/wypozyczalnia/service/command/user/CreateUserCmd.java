package com.studia.wypozyczalnia.service.command.user;

import com.studia.wypozyczalnia.domain.enums.Role;

public record CreateUserCmd(String username, String displayName, Role role, Boolean active) {
}
