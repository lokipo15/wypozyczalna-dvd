package com.studia.wypozyczalnia.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studia.wypozyczalnia.domain.enums.Role;
import com.studia.wypozyczalnia.dto.RegisterRequest;
import com.studia.wypozyczalnia.security.JwtService;
import com.studia.wypozyczalnia.security.UserPrincipal;
import com.studia.wypozyczalnia.service.CustomerService;
import com.studia.wypozyczalnia.service.command.customer.CreateCustomerCmd;
import com.studia.wypozyczalnia.service.command.user.CreateUserCmd;

/**
 * Serwis odpowiedzialny za uwierzytelnianie i rejestrację użytkowników.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;
    private final CustomerService customerService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserAccountService userAccountService, CustomerService customerService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
        this.customerService = customerService;
    }

    /**
     * Loguje użytkownika i generuje token JWT.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return token JWT
     */
    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);
    }

    @Transactional
    /**
     * Rejestruje nowego użytkownika, a dla roli klienta tworzy powiązany rekord klienta.
     *
     * @param request dane rejestracyjne
     * @return token JWT nowo utworzonego użytkownika
     */
    public String register(RegisterRequest request) {
        Long customerId = null;
        if (request.role() == Role.CUSTOMER) {
            var customer = customerService.createCustomer(new CreateCustomerCmd(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone()));
            customerId = customer.getId();
        }
        var user = userAccountService.createUser(new CreateUserCmd(
            request.username(),
            request.displayName(),
            request.password(),
            request.role(),
            Boolean.TRUE,
            customerId));
        return jwtService.generateToken(UserPrincipal.from(user));
    }
}
