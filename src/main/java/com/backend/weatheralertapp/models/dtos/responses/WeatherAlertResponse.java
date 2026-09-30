package com.backend.weatheralertapp.models.dtos.responses;

import com.backend.weatheralertapp.enums.Severity;

import java.math.BigDecimal;

public record WeatherAlertResponse(
        Long id,
        String description,
        String locationName,
        BigDecimal latitude,
        BigDecimal longitude,
        Severity severity
) {
}
