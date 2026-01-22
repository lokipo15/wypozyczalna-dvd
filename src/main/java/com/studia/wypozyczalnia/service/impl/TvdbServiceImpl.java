package com.studia.wypozyczalnia.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.studia.wypozyczalnia.config.TvdbProperties;
import com.studia.wypozyczalnia.dto.TvdbSearchResultDto;
import com.studia.wypozyczalnia.exception.ExternalServiceException;
import com.studia.wypozyczalnia.exception.ValidationException;
import com.studia.wypozyczalnia.service.TvdbService;
import reactor.core.publisher.Mono;

/**
 * Implementacja serwisu komunikującego się z TVDB i mapującego wyniki wyszukiwania.
 */
@Service
@Transactional(readOnly = true)
public class TvdbServiceImpl implements TvdbService {

    private static final Logger log = LoggerFactory.getLogger(TvdbServiceImpl.class);
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final WebClient webClient;
    private final TvdbProperties properties;

    public TvdbServiceImpl(WebClient.Builder webClientBuilder, TvdbProperties properties) {
        this.properties = properties;
        this.webClient = webClientBuilder
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
            .build();
    }

    /**
     * Wyszukuje tytuły w TVDB i zwraca zmapowane wyniki.
     */
    @Override
    public List<TvdbSearchResultDto> search(String query) {
        if (!StringUtils.hasText(query)) {
            throw new ValidationException("Search query must not be empty");
        }
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new ValidationException("TVDB API key is not configured");
        }
        try {
            log.debug("TVDB search started query='{}'", query.trim());
            var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/search")
                    .queryParam("q", query.trim())
                    .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> {
                        log.warn("TVDB search failed status={} body={}", clientResponse.statusCode().value(), truncate(body));
                        return Mono.error(new ExternalServiceException("TVDB request failed with status " + clientResponse.statusCode().value()));
                    }))
                .bodyToMono(JsonNode.class)
                .block(Duration.ofMillis(properties.getTimeoutMs()));
            if (response == null) {
                throw new ExternalServiceException("TVDB request returned empty response");
            }
            log.debug("TVDB search success query='{}'", query.trim());
            return mapResponse(response);
        } catch (ExternalServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("TVDB search error query='{}'", query.trim(), ex);
            throw new ExternalServiceException("TVDB request failed", ex);
        }
    }

    private List<TvdbSearchResultDto> mapResponse(JsonNode response) {
        var dataNode = response.path("data");
        if (!dataNode.isArray()) {
            return List.of();
        }
        List<TvdbSearchResultDto> results = new ArrayList<>();
        for (JsonNode item : dataNode) {
            if (!isMovie(item)) {
                continue;
            }
            Long tvdbId = extractId(item);
            if (tvdbId == null) {
                continue;
            }
            String title = resolveTitle(item);
            String description = resolveDescription(item);
            Integer year = resolveYear(item);
            List<String> genres = resolveGenres(item);
            String thumbnail = resolveThumbnail(item);
            String image = resolveImage(item, thumbnail);
            var rating = resolveRating(item);
            results.add(new TvdbSearchResultDto(tvdbId, title, description, year, genres, thumbnail, image, rating));
            if (results.size() >= 10) {
                break;
            }
        }
        return List.copyOf(results);
    }

    private boolean isMovie(JsonNode item) {
        String type = textValue(item.path("type"));
        return "movie".equalsIgnoreCase(type);
    }

    private Long extractId(JsonNode item) {
        long id = item.path("tvdb_id").asLong(0L);
        if (id == 0L) {
            id = item.path("id").asLong(0L);
        }
        return id == 0L ? null : id;
    }

    private String resolveTitle(JsonNode item) {
        String fromTranslations = firstNonBlank(
            extractTranslation(item.path("translations"), "pol"),
            extractTranslation(item.path("translations"), "eng"));
        return firstNonBlank(fromTranslations,
            textValue(item.path("name")),
            textValue(item.path("title")));
    }

    private String resolveDescription(JsonNode item) {
        String fromOverviews = firstNonBlank(
            extractOverview(item.path("overviews"), "pol"),
            extractOverview(item.path("overviews"), "eng"));
        return firstNonBlank(fromOverviews,
            textValue(item.path("overview")));
    }

    private Integer resolveYear(JsonNode item) {
        Integer parsed = parseYearFromText(item.path("year"));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseYearFromText(item.path("releaseYear"));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseYearFromDate(textValue(item.path("first_air_time")));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseYearFromDate(textValue(item.path("releaseDate")));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseYearFromDate(textValue(item.path("firstAired")));
        if (parsed != null) {
            return parsed;
        }
        return parseYearFromDate(textValue(item.path("aired")));
    }

    private Integer parseYearFromText(JsonNode node) {
        String value = textValue(node);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return parseYearFromDate(value);
        }
    }

    private Integer parseYearFromDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value, DATE_FORMATTER).getYear();
        } catch (DateTimeParseException ex) {
            Matcher matcher = YEAR_PATTERN.matcher(value);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
            return null;
        }
    }

    private List<String> resolveGenres(JsonNode item) {
        var genresNode = item.path("genres");
        if (!genresNode.isArray()) {
            return List.of();
        }
        List<String> genres = new ArrayList<>();
        for (JsonNode genreNode : genresNode) {
            String g = textValue(genreNode);
            if (StringUtils.hasText(g)) {
                genres.add(g);
            }
        }
        return List.copyOf(genres);
    }

    private String resolveThumbnail(JsonNode item) {
        return firstNonBlank(
            textValue(item.path("thumbnail")),
            extractFromArtworks(item, "thumbnail"),
            extractFromArtworks(item, "image"),
            textValue(item.path("image")),
            textValue(item.path("image_url")));
    }

    private String resolveImage(JsonNode item, String thumbnail) {
        return firstNonBlank(
            textValue(item.path("image")),
            textValue(item.path("image_url")),
            extractFromArtworks(item, "image"),
            thumbnail);
    }

    private BigDecimal resolveRating(JsonNode item) {
        String[] candidates = {
            textValue(item.path("score")),
            textValue(item.path("rating")),
            textValue(item.path("site_rating"))
        };
        for (String candidate : candidates) {
            if (!StringUtils.hasText(candidate)) {
                continue;
            }
            try {
                return new BigDecimal(candidate);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String extractTranslation(JsonNode translationsNode, String lang) {
        if (!translationsNode.isObject()) {
            return null;
        }
        var langNode = translationsNode.path(lang);
        if (langNode.isMissingNode() || langNode.isNull()) {
            return null;
        }
        if (langNode.isTextual()) {
            return langNode.asText();
        }
        return firstNonBlank(textValue(langNode.path("title")), textValue(langNode.path("name")), textValue(langNode.path("translation")), textValue(langNode));
    }

    private String extractOverview(JsonNode overviewsNode, String lang) {
        if (!overviewsNode.isObject()) {
            return null;
        }
        var langNode = overviewsNode.path(lang);
        if (langNode.isMissingNode() || langNode.isNull()) {
            return null;
        }
        if (langNode.isTextual()) {
            return langNode.asText();
        }
        return textValue(langNode.path("overview"));
    }

    private String extractFromArtworks(JsonNode item, String field) {
        var artworks = item.path("artworks");
        if (!artworks.isArray()) {
            return null;
        }
        for (JsonNode artwork : artworks) {
            String val = textValue(artwork.path(field));
            if (StringUtils.hasText(val)) {
                return val;
            }
        }
        return null;
    }

    private String textValue(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            var text = node.asText();
            return StringUtils.hasText(text) ? text : null;
        }
        if (node.isNumber()) {
            return node.asText();
        }
        return null;
    }

    private String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String c : candidates) {
            if (StringUtils.hasText(c)) {
                return c;
            }
        }
        return null;
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > 500 ? value.substring(0, 500) + "..." : value;
    }
}
