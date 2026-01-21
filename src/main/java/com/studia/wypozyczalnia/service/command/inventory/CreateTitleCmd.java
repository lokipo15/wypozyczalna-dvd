package com.studia.wypozyczalnia.service.command.inventory;

import java.math.BigDecimal;

public record CreateTitleCmd(String name, Integer year, String genre, String description, String tvdbId, BigDecimal pricePerDay) {
}
