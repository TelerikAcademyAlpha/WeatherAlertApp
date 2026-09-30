package com.backend.weatheralertapp.models.dtos.responses;


import com.backend.weatheralertapp.models.dtos.GeocodingResult;

import java.util.List;

public record GeocodingApiResponse(
        List<GeocodingResult> results
) {
}
