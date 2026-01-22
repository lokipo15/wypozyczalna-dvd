package com.studia.wypozyczalnia.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.studia.wypozyczalnia.domain.UserAccount;

/**
 * Reprezentacja użytkownika na potrzeby autoryzacji Spring Security.
 */
public class UserPrincipal implements UserDetails {

    private final UserAccount user;

    public UserPrincipal(UserAccount user) {
        this.user = user;
    }

    /**
     * Tworzy principal na podstawie encji użytkownika.
     */
    public static UserPrincipal from(UserAccount user) {
        return new UserPrincipal(user);
    }

    /**
     * Zwraca encję użytkownika powiązaną z principalem.
     */
    public UserAccount getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(user.getActive());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getActive());
    }
}
