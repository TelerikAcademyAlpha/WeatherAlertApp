package com.backend.weatheralertapp.services;

import com.backend.weatheralertapp.api.GeocodingClient;
import com.backend.weatheralertapp.events.GeocodingRequestedEvent;
import com.backend.weatheralertapp.models.dtos.GeocodingResult;
import com.backend.weatheralertapp.models.dtos.responses.GeocodingApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Service
public class GeocodingService {
    private static final Logger log =  LoggerFactory.getLogger(GeocodingService.class);

    private final GeocodingClient geocodingClient;
    private final AlertCoordinateUpdater coordinateUpdater;

    public GeocodingService(GeocodingClient geocodingClient, AlertCoordinateUpdater coordinateUpdater) {
        this.geocodingClient = geocodingClient;
        this.coordinateUpdater = coordinateUpdater;
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGeocodingRequest(GeocodingRequestedEvent event) {
        Long alertId = event.alertId();
        String locationName = event.locationName();

        try {
            GeocodingApiResponse response =
                    geocodingClient.search(locationName);

            if (response == null
                    || response.results() == null
                    || response.results().isEmpty()) {
                log.warn("No coordinates found for '{}'", locationName);
                return;
            }

            GeocodingResult result = response.results().get(0);

            if (result.latitude() == null || result.longitude() == null) {
                log.warn("Coordinates are missing for '{}'", locationName);
                return;
            }

            boolean updated = coordinateUpdater.updateCoordinates(
                    alertId,
                    locationName,
                    result.latitude(),
                    result.longitude()
            );

            if (!updated) {
                log.warn(
                        "Alert {} could not be updated with coordinates for '{}'",
                        alertId,
                        locationName
                );
            }

        } catch (Exception exception) {
            log.error(
                    "Failed to geocode location '{}' for alert {}",
                    locationName,
                    alertId,
                    exception
            );
        }
    }
}
