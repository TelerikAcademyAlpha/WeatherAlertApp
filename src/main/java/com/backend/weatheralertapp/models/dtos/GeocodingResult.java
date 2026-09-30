package com.backend.weatheralertapp.models.dtos;

import java.math.BigDecimal;

public record GeocodingResult(
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        String country
) {
}
