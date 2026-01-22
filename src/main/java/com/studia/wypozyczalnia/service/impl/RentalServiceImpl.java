package com.studia.wypozyczalnia.service.impl;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.studia.wypozyczalnia.repository.UserAccountRepository;
import com.studia.wypozyczalnia.service.RentalService;
import com.studia.wypozyczalnia.service.command.rental.CreateRentalCmd;
import com.studia.wypozyczalnia.service.command.rental.ReturnRentalCmd;

/**
 * Implementacja serwisu obsługującego wypożyczenia kopii DVD.
 */
@Service
@Transactional(readOnly = true)
public class RentalServiceImpl implements RentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final UserAccountRepository userAccountRepository;

    public RentalServiceImpl(RentalRepository rentalRepository,
                             CustomerRepository customerRepository,
                             DvdCopyRepository dvdCopyRepository,
                             UserAccountRepository userAccountRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Tworzy nowe wypożyczenie dla użytkownika powiązanego z klientem.
     */
    @Override
    @Transactional
    public Rental createRental(CreateRentalCmd cmd) {
        var now = Instant.now();
        if (cmd.dueAt() != null && !cmd.dueAt().isAfter(now)) {
            throw new ValidationException("Due date must be in the future");
        }
        var customer = resolveCustomer(cmd.userId());
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

    /**
     * Oznacza wypożyczenie jako zwrócone i przywraca dostępność kopii.
     */
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

    /**
     * Zwraca listę aktywnych wypożyczeń.
     */
    @Override
    public List<Rental> listActiveRentals() {
        return rentalRepository.findByReturnedAtIsNull();
    }

    /**
     * Zwraca historię wypożyczeń klienta.
     */
    @Override
    public List<Rental> customerHistory(Long customerId) {
        Long resolvedCustomerId = resolveCustomerIdFromCustomerOrUser(customerId);
        if (resolvedCustomerId == null) {
            log.debug("Historia wypożyczeń: brak klienta dla id={} (user lub customer)", customerId);
            return List.of();
        }
        var rentals = rentalRepository.findByCustomerIdOrderByRentedAtDesc(resolvedCustomerId);
        log.debug("Historia wypożyczeń: idWejscia={} zmapowaneNaCustomerId={} liczbaPoz={}"
            , customerId, resolvedCustomerId, rentals.size());
        return rentals;
    }

    /**
     * Pobiera wypożyczenie po identyfikatorze.
     */
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

    private com.studia.wypozyczalnia.domain.Customer resolveCustomer(Long userId) {
        if (userId == null) {
            throw new ValidationException("UserId is required");
        }
        var user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        var customer = user.getCustomer();
        if (customer == null) {
            throw new NotFoundException("Customer not found for user");
        }
        return customer;
    }

    private Long resolveCustomerIdFromCustomerOrUser(Long id) {
        if (id == null) {
            return null;
        }
        return customerRepository.findById(id)
            .map(com.studia.wypozyczalnia.domain.Customer::getId)
            .orElseGet(() -> userAccountRepository.findById(id)
                .map(user -> user.getCustomer() != null ? user.getCustomer().getId() : null)
                .orElse(null));
    }
}
