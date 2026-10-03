package com.backend.weatheralertapp.controllers;

import com.backend.weatheralertapp.models.dtos.requests.CreateWeatherAlertRequest;
import com.backend.weatheralertapp.models.dtos.requests.UpdateWeatherRequest;
import com.backend.weatheralertapp.models.dtos.responses.WeatherAlertResponse;
import com.backend.weatheralertapp.services.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<WeatherAlertResponse> findAll() {
        return alertService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public WeatherAlertResponse findById(@PathVariable Long id) {
        return alertService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WeatherAlertResponse> create(
            @Valid @RequestBody CreateWeatherAlertRequest request,
            Authentication authentication
    ) {
        WeatherAlertResponse response =
                alertService.create(request, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WeatherAlertResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWeatherRequest request
    ) {
        return alertService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
