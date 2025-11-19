package com.studia.wypozyczalnia.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.studia.wypozyczalnia.integration.tvdb.TvdbProperties;

import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient tvdbWebClient(TvdbProperties properties) {
        var httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMillis(properties.getTimeoutMs()));

        var builder = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1_000_000))
                .build());

        if (StringUtils.hasText(properties.getBaseUrl())) {
            builder.baseUrl(properties.getBaseUrl());
        }
        if (StringUtils.hasText(properties.getApiKey())) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey());
        }
        return builder.build();
    }
}
