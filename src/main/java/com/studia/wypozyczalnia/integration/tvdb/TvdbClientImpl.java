package com.studia.wypozyczalnia.integration.tvdb;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.studia.wypozyczalnia.exception.ExternalServiceException;
import com.studia.wypozyczalnia.integration.tvdb.dto.TvdbSeriesData;
import com.studia.wypozyczalnia.integration.tvdb.dto.TvdbSearchResponse;
import com.studia.wypozyczalnia.integration.tvdb.dto.TvdbTitleDetailsResponse;
import com.studia.wypozyczalnia.service.dto.TvdbSearchItem;
import com.studia.wypozyczalnia.service.dto.TvdbTitleDetails;

@Component
public class TvdbClientImpl implements TvdbClient {

    private final WebClient webClient;
    private final TvdbProperties properties;

    public TvdbClientImpl(@Qualifier("tvdbWebClient") WebClient webClient, TvdbProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    @Override
    public List<TvdbSearchItem> search(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }
        ensureConfigured();
        try {
            var response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/series").queryParam("name", query).build())
                .retrieve()
                .bodyToMono(TvdbSearchResponse.class)
                .block(Duration.ofMillis(properties.getTimeoutMs()));
            var data = response != null && response.data() != null ? response.data() : Collections.<TvdbSeriesData>emptyList();
            return data.stream().map(this::toSearchItem).collect(Collectors.toList());
        } catch (WebClientResponseException ex) {
            throw new ExternalServiceException("TVDB request failed: " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("TVDB request failed", ex);
        }
    }

    @Override
    public Optional<TvdbTitleDetails> getDetails(String tvdbId) {
        if (!StringUtils.hasText(tvdbId)) {
            return Optional.empty();
        }
        ensureConfigured();
        try {
            var response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/series/{id}").build(tvdbId))
                .retrieve()
                .bodyToMono(TvdbTitleDetailsResponse.class)
                .block(Duration.ofMillis(properties.getTimeoutMs()));
            if (response == null || response.data() == null) {
                return Optional.empty();
            }
            return Optional.of(toDetails(response.data()));
        } catch (WebClientResponseException.NotFound ex) {
            return Optional.empty();
        } catch (WebClientResponseException ex) {
            throw new ExternalServiceException("TVDB details request failed: " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("TVDB details request failed", ex);
        }
    }

    private TvdbSearchItem toSearchItem(TvdbSeriesData data) {
        return new TvdbSearchItem(
            data.id(),
            data.seriesName(),
            parseYear(data.firstAired()),
            data.overview());
    }

    private TvdbTitleDetails toDetails(TvdbSeriesData data) {
        return new TvdbTitleDetails(
            data.id(),
            data.seriesName(),
            parseYear(data.firstAired()),
            data.genre(),
            data.overview());
    }

    private Integer parseYear(String firstAired) {
        if (!StringUtils.hasText(firstAired) || firstAired.length() < 4) {
            return null;
        }
        try {
            return Integer.parseInt(firstAired.substring(0, 4));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new ExternalServiceException("TVDB API key not configured");
        }
    }
}
