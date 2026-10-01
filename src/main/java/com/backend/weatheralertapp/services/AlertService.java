package com.backend.weatheralertapp.services;

import com.backend.weatheralertapp.mappers.Mapper;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.models.WeatherAlert;
import com.backend.weatheralertapp.models.dtos.requests.CreateWeatherAlertRequest;
import com.backend.weatheralertapp.models.dtos.responses.GeocodingApiResponse;
import com.backend.weatheralertapp.models.dtos.responses.WeatherAlertResponse;
import com.backend.weatheralertapp.repositories.AlertRepository;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final Mapper map;
    private final GeocodingService geocodingService;

    public AlertService(AlertRepository alertRepository, UserRepository userRepository, Mapper map, GeocodingService geocodingService) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.map = map;
        this.geocodingService = geocodingService;
    }


    public WeatherAlertResponse create(
            CreateWeatherAlertRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );

        WeatherAlert alert = map.toEntity(request);
        alert.setCreatedBy(creator);

        WeatherAlert savedAlert = alertRepository.save(alert);

        geocodingService.addCoordinates(
                savedAlert.getId(),
                savedAlert.getLocationName()
        );

        return map.toResponse(savedAlert);
    }
}
