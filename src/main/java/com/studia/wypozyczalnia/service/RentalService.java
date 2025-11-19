package com.studia.wypozyczalnia.service;

import java.util.List;

import com.studia.wypozyczalnia.domain.Rental;
import com.studia.wypozyczalnia.service.command.rental.CreateRentalCmd;
import com.studia.wypozyczalnia.service.command.rental.ReturnRentalCmd;

public interface RentalService {

    Rental createRental(CreateRentalCmd cmd);

    Rental returnRental(ReturnRentalCmd cmd);

    List<Rental> listActiveRentals();

    List<Rental> customerHistory(Long customerId);

    Rental getRental(Long rentalId);
}
