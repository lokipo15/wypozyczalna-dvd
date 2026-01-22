package com.studia.wypozyczalnia.domain;

import com.studia.wypozyczalnia.domain.base.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Encja klienta przechowująca dane kontaktowe i status aktywności.
 */
@jakarta.persistence.Entity
@Table(name = "customer")
public class Customer extends Entity {

    /**
     * Identyfikator klienta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Imię klienta.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Nazwisko klienta.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Adres e-mail klienta.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Numer telefonu klienta.
     */
    private String phone;

    /**
     * Flaga określająca aktywność klienta.
     */
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
