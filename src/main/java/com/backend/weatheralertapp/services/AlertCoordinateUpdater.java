package com.backend.weatheralertapp.services;

import com.backend.weatheralertapp.models.WeatherAlert;
import com.backend.weatheralertapp.repositories.AlertRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AlertCoordinateUpdater {
    private final AlertRepository repository;

    public AlertCoordinateUpdater(AlertRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean updateCoordinates(
            Long alertId,
            String searchedLocation,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        Optional<WeatherAlert> optionalAlert =
                repository.findById(alertId);

        if (optionalAlert.isEmpty()) {
            return false;
        }

        WeatherAlert alert = optionalAlert.get();

        // Проверяваме, че alert-ът още има местоположението,
        // за което търсихме координати.
        if (!alert.getLocationName().equalsIgnoreCase(searchedLocation)) {
            return false;
        }

        alert.setLatitude(latitude);
        alert.setLongitude(longitude);

        repository.save(alert);

        return true;
    }
}
