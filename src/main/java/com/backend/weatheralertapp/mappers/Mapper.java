package com.backend.weatheralertapp.mappers;

import com.backend.weatheralertapp.models.WeatherAlert;
import com.backend.weatheralertapp.models.dtos.requests.CreateWeatherAlertRequest;
import com.backend.weatheralertapp.models.dtos.requests.UpdateWeatherRequest;
import com.backend.weatheralertapp.models.dtos.responses.WeatherAlertResponse;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public WeatherAlert toEntity(CreateWeatherAlertRequest request) {
        WeatherAlert alert = new WeatherAlert();

        alert.setDescription(request.description());
        alert.setLocationName(request.locationName());
        alert.setSeverity(request.severity());

        alert.setLatitude(null);
        alert.setLongitude(null);

        return alert;
    }

    public void updateEntity(UpdateWeatherRequest request, WeatherAlert alert) {
        alert.setDescription(request.description());
        alert.setLocationName(request.locationName());
        alert.setSeverity(request.severity());
    }

    public WeatherAlertResponse toResponse(WeatherAlert alert) {
        return new WeatherAlertResponse(
                alert.getId(),
                alert.getDescription(),
                alert.getLocationName(),
                alert.getLatitude(),
                alert.getLongitude(),
                alert.getSeverity()
        );
    }
}
