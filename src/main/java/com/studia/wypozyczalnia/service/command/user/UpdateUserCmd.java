package com.studia.wypozyczalnia.service.command.user;

import com.studia.wypozyczalnia.domain.enums.Role;

public record UpdateUserCmd(String displayName, Role role, Boolean active) {
}
