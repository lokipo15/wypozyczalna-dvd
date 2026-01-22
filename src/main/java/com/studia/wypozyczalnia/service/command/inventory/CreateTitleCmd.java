package com.studia.wypozyczalnia.service.command.inventory;

import java.math.BigDecimal;
import java.util.List;

public record CreateTitleCmd(String name,
                             Integer year,
                             List<String> genres,
                             String description,
                             String tvdbId,
                             BigDecimal rating,
                             BigDecimal pricePerDay,
                             String thumbnailUrl,
                             String imageUrl) {
}
