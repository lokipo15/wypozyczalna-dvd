package com.studia.wypozyczalnia.integration.tvdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TvdbSeriesData(
    @JsonProperty("id") String id,
    @JsonProperty("seriesName") String seriesName,
    @JsonProperty("overview") String overview,
    @JsonProperty("firstAired") String firstAired,
    @JsonProperty("genre") String genre) {
}
