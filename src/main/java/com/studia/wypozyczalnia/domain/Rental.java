package com.studia.wypozyczalnia.domain;

import java.time.Instant;

import com.studia.wypozyczalnia.domain.base.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Wypożyczenie kopii DVD przypisane do klienta.
 */
@jakarta.persistence.Entity
@Table(name = "rental")
public class Rental extends Entity {

    /**
     * Identyfikator wypożyczenia.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Klient, który dokonał wypożyczenia.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * Wypożyczona kopia DVD.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "copy_id", nullable = false)
    private DvdCopy copy;

    /**
     * Data rozpoczęcia wypożyczenia.
     */
    @Column(name = "rented_at", nullable = false)
    private Instant rentedAt;

    /**
     * Termin zwrotu kopii.
     */
    @Column(name = "due_at")
    private Instant dueAt;

    /**
     * Faktyczna data zwrotu kopii.
     */
    @Column(name = "returned_at")
    private Instant returnedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public DvdCopy getCopy() {
        return copy;
    }

    public void setCopy(DvdCopy copy) {
        this.copy = copy;
    }

    public Instant getRentedAt() {
        return rentedAt;
    }

    public void setRentedAt(Instant rentedAt) {
        this.rentedAt = rentedAt;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
    }

    public boolean isActive() {
        return returnedAt == null;
    }
}
