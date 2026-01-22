package com.studia.wypozyczalnia.service;

import java.util.List;

import com.studia.wypozyczalnia.dto.TvdbSearchResultDto;

public interface TvdbService {

    List<TvdbSearchResultDto> search(String query);
}
