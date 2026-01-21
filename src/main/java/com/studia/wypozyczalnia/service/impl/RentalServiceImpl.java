package com.studia.wypozyczalnia.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.domain.Rental;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.exception.ValidationException;
import com.studia.wypozyczalnia.repository.CustomerRepository;
import com.studia.wypozyczalnia.repository.DvdCopyRepository;
import com.studia.wypozyczalnia.repository.RentalRepository;
import com.studia.wypozyczalnia.service.RentalService;
import com.studia.wypozyczalnia.service.command.rental.CreateRentalCmd;
import com.studia.wypozyczalnia.service.command.rental.ReturnRentalCmd;

@Service
@Transactional(readOnly = true)
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final DvdCopyRepository dvdCopyRepository;

    public RentalServiceImpl(RentalRepository rentalRepository, CustomerRepository customerRepository, DvdCopyRepository dvdCopyRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.dvdCopyRepository = dvdCopyRepository;
    }

    @Override
    @Transactional
    public Rental createRental(CreateRentalCmd cmd) {
        var now = Instant.now();
        if (cmd.dueAt() != null && !cmd.dueAt().isAfter(now)) {
            throw new ValidationException("Due date must be in the future");
        }
        var customer = customerRepository.findById(cmd.customerId())
            .orElseThrow(() -> new NotFoundException("Customer not found"));
        var copy = lockCopy(cmd.copyId());
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new ConflictException("Copy not available");
        }
        rentalRepository.findByCopyIdAndReturnedAtIsNull(copy.getId()).ifPresent(rental -> {
            throw new ConflictException("Copy already rented");
        });

        copy.setStatus(CopyStatus.RENTED);
        dvdCopyRepository.save(copy);

        var rental = new Rental();
        rental.setCustomer(customer);
        rental.setCopy(copy);
        rental.setRentedAt(now);
        rental.setDueAt(cmd.dueAt());
        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental returnRental(ReturnRentalCmd cmd) {
        var rental = resolveActiveRental(cmd);
        var copy = lockCopy(rental.getCopy().getId());
        if (rental.getReturnedAt() != null) {
            throw new ConflictException("Rental already returned");
        }
        var returnedAt = Instant.now();
        rental.setReturnedAt(returnedAt);
        copy.setStatus(CopyStatus.AVAILABLE);
        dvdCopyRepository.save(copy);
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> listActiveRentals() {
        return rentalRepository.findByReturnedAtIsNull();
    }

    @Override
    public List<Rental> customerHistory(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found"));
        return rentalRepository.findByCustomerIdOrderByRentedAtDesc(customerId);
    }

    @Override
    public Rental getRental(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new NotFoundException("Rental not found"));
    }

    private DvdCopy lockCopy(Long copyId) {
        return dvdCopyRepository.findWithLockingById(copyId)
            .orElseThrow(() -> new NotFoundException("Copy not found"));
    }

    private Rental resolveActiveRental(ReturnRentalCmd cmd) {
        if (cmd.rentalId() != null) {
            return rentalRepository.findByIdAndReturnedAtIsNull(cmd.rentalId())
                .orElseThrow(() -> new ConflictException("Rental already closed or not found"));
        }
        if (cmd.copyId() != null) {
            return rentalRepository.findByCopyIdAndReturnedAtIsNull(cmd.copyId())
                .orElseThrow(() -> new ConflictException("Copy does not have an active rental"));
        }
        throw new ValidationException("RentalId or copyId must be provided");
    }
}
