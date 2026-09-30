package com.backend.weatheralertapp.models.dtos.requests;

import com.backend.weatheralertapp.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateWeatherRequest(
        @NotBlank String description,
        @NotBlank String locationName,
        @NotNull Severity severity
) {
}
