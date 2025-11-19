package com.studia.wypozyczalnia.service;

import java.util.List;

import com.studia.wypozyczalnia.domain.Customer;
import com.studia.wypozyczalnia.service.command.customer.CreateCustomerCmd;
import com.studia.wypozyczalnia.service.command.customer.UpdateCustomerCmd;

public interface CustomerService {

    Customer createCustomer(CreateCustomerCmd cmd);

    Customer updateCustomer(Long id, UpdateCustomerCmd cmd);

    void deleteCustomer(Long id);

    Customer getCustomer(Long id);

    List<Customer> findCustomers(String query);
}
