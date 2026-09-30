package com.backend.weatheralertapp.api;

import com.backend.weatheralertapp.models.dtos.responses.GeocodingApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GeocodingClient {

    private final WebClient geocodingWebClient;

    public GeocodingClient(WebClient geocodingWebClient) {
        this.geocodingWebClient = geocodingWebClient;
    }

    public GeocodingApiResponse search(String locationName){
        return geocodingWebClient
                .get()
                .uri( uriBuilder -> uriBuilder
                        .path("/v1/search")
                        .queryParam("name",locationName)
                        .build())
                .retrieve()
                .bodyToMono(GeocodingApiResponse.class)
                .block();
    }
}
