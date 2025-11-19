package com.studia.wypozyczalnia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dvdRentalOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("DVD Rental API")
                .version("1.0")
                .description("REST API for DVD rental management")
                .contact(new Contact().name("DVD Rental Team")));
    }
}
