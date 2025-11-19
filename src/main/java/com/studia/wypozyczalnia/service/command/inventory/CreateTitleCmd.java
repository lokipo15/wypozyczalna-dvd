package com.studia.wypozyczalnia.service.command.inventory;

public record CreateTitleCmd(String name, Integer year, String genre, String description, String tvdbId) {
}
