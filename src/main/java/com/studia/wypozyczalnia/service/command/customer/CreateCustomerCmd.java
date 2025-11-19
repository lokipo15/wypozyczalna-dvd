package com.studia.wypozyczalnia.service.command.customer;

public record CreateCustomerCmd(String firstName, String lastName, String email, String phone) {
}
