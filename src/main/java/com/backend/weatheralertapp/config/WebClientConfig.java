package com.backend.weatheralertapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient geocodingWebClient() {
        return WebClient.builder()
                .baseUrl("https://geocoding-api.open-meteo.com")
                .build();
    }
}
