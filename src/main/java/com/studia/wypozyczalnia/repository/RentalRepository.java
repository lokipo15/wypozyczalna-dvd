package com.studia.wypozyczalnia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studia.wypozyczalnia.domain.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByReturnedAtIsNull();

    List<Rental> findByCustomerIdOrderByRentedAtDesc(Long customerId);

    Optional<Rental> findByCopyIdAndReturnedAtIsNull(Long copyId);

    Optional<Rental> findByIdAndReturnedAtIsNull(Long rentalId);

    boolean existsByCustomerIdAndReturnedAtIsNull(Long customerId);

    void deleteByCopyIdIn(List<Long> copyIds);
}
