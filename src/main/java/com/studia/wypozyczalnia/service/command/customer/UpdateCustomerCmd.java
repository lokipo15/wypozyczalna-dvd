package com.studia.wypozyczalnia.service.command.customer;

public record UpdateCustomerCmd(String firstName, String lastName, String email, String phone, Boolean active) {
}
