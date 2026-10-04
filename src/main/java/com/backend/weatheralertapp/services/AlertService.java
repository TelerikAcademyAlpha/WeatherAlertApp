package com.backend.weatheralertapp.services;

import com.backend.weatheralertapp.events.GeocodingRequestedEvent;
import com.backend.weatheralertapp.mappers.Mapper;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.models.WeatherAlert;
import com.backend.weatheralertapp.models.dtos.requests.CreateWeatherAlertRequest;
import com.backend.weatheralertapp.models.dtos.requests.UpdateWeatherRequest;
import com.backend.weatheralertapp.models.dtos.responses.WeatherAlertResponse;
import com.backend.weatheralertapp.repositories.AlertRepository;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final Mapper map;
    private final ApplicationEventPublisher eventPublisher;

    public AlertService(
            AlertRepository alertRepository,
            UserRepository userRepository,
            Mapper map,
            ApplicationEventPublisher eventPublisher
    ) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.map = map;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public WeatherAlertResponse create(
            CreateWeatherAlertRequest request,
            Authentication authentication
    ) {
        User creator = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user was not found"
                ));

        WeatherAlert alert = map.toEntity(request);
        alert.setCreatedBy(creator);

        WeatherAlert savedAlert = alertRepository.save(alert);

        eventPublisher.publishEvent(
                new GeocodingRequestedEvent(
                        savedAlert.getId(),
                        savedAlert.getLocationName()
                )
        );

        return map.toResponse(savedAlert);
    }

    @Transactional(readOnly = true)
    public List<WeatherAlertResponse> findAll() {
        return alertRepository.findAll()
                .stream()
                .map(map::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WeatherAlertResponse findById(Long id) {
        WeatherAlert alert = getAlertOrThrow(id);
        return map.toResponse(alert);
    }

    @Transactional
    public WeatherAlertResponse update(
            Long id,
            UpdateWeatherRequest request
    ) {
        WeatherAlert alert = getAlertOrThrow(id);

        String oldLocation = alert.getLocationName();
        String newLocation = request.locationName().trim();

        boolean locationChanged =
                !oldLocation.trim().equalsIgnoreCase(newLocation);

        map.updateEntity(request, alert);

        alert.setLocationName(newLocation);

        if (locationChanged) {
            // coordinates from provious location are not valid anymore
            alert.setLatitude(null);
            alert.setLongitude(null);
        }

        WeatherAlert savedAlert = alertRepository.save(alert);

        if (locationChanged) {
            eventPublisher.publishEvent(
                    new GeocodingRequestedEvent(
                            savedAlert.getId(),
                            savedAlert.getLocationName()
                    )
            );
        }

        return map.toResponse(savedAlert);
    }

    @Transactional
    public void delete(Long id) {
        WeatherAlert alert = getAlertOrThrow(id);
        alertRepository.delete(alert);
    }

    private WeatherAlert getAlertOrThrow(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Weather alert with id " + id + " was not found"
                ));
    }
}
