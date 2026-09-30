package com.backend.weatheralertapp.models.dtos.responses;

import com.backend.weatheralertapp.enums.Severity;

public record WeatherAlertResponse(
        Long id,
        String description,
        String locationName,
        Double latitude,
        Double longitude,
        Severity severity
) {
}
