package com.studia.wypozyczalnia.domain;

import com.studia.wypozyczalnia.domain.base.Entity;
import com.studia.wypozyczalnia.domain.enums.Role;
import com.studia.wypozyczalnia.domain.Customer;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Konto użytkownika aplikacji wraz z powiązaniem do klienta.
 */
@jakarta.persistence.Entity
@Table(name = "user_account")
public class UserAccount extends Entity {

    /**
     * Identyfikator użytkownika.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unikalna nazwa użytkownika do logowania.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Wyświetlana nazwa użytkownika.
     */
    @Column(name = "display_name", nullable = false)
    private String displayName;

    /**
     * Zahaszowane hasło użytkownika.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Rola użytkownika w systemie.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private Role role;

    /**
     * Flaga określająca aktywność konta.
     */
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    /**
     * Powiązany klient, jeśli użytkownik ma rolę klienta.
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
