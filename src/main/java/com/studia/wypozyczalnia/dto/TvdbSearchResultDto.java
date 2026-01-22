package com.studia.wypozyczalnia.dto;

import java.math.BigDecimal;
import java.util.List;

public record TvdbSearchResultDto(Long tvdbId,
                                  String title,
                                  String description,
                                  Integer year,
                                  List<String> genres,
                                  String thumbnailUrl,
                                  String imageUrl,
                                  BigDecimal rating) {
}
