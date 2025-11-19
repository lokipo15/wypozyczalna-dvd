package com.studia.wypozyczalnia.integration.tvdb;

import java.util.List;
import java.util.Optional;

import com.studia.wypozyczalnia.service.dto.TvdbSearchItem;
import com.studia.wypozyczalnia.service.dto.TvdbTitleDetails;

public interface TvdbClient {

    List<TvdbSearchItem> search(String query);

    Optional<TvdbTitleDetails> getDetails(String tvdbId);
}
