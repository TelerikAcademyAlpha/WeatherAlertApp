package com.backend.weatheralertapp.events;

public record GeocodingRequestedEvent(
        Long alertId,
        String locationName
) {
}
