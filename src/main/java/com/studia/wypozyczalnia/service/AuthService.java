package com.studia.wypozyczalnia.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studia.wypozyczalnia.domain.UserAccount;
import com.studia.wypozyczalnia.dto.RegisterRequest;
import com.studia.wypozyczalnia.security.JwtService;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserAccountService userAccountService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);
    }

    @Transactional
    public UserAccount register(RegisterRequest request) {
        return userAccountService.createUser(new CreateUserCmd(
            request.username(),
            request.displayName(),
            request.password(),
            request.role(),
            Boolean.TRUE));
    }
}
