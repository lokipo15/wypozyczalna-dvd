package com.studia.wypozyczalnia.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.studia.wypozyczalnia.repository.UserAccountRepository;

/**
 * Serwis ładujący dane użytkownika na potrzeby Spring Security.
 */
@Service
public class UserAccountDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Pobiera użytkownika na podstawie nazwy.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByUsernameIgnoreCase(username)
            .map(UserPrincipal::from)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
