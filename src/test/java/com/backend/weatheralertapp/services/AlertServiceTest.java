package com.backend.weatheralertapp.services;


import com.backend.weatheralertapp.enums.Severity;
import com.backend.weatheralertapp.events.GeocodingRequestedEvent;
import com.backend.weatheralertapp.mappers.Mapper;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.models.WeatherAlert;
import com.backend.weatheralertapp.models.dtos.requests.CreateWeatherAlertRequest;
import com.backend.weatheralertapp.models.dtos.requests.UpdateWeatherRequest;
import com.backend.weatheralertapp.models.dtos.responses.WeatherAlertResponse;
import com.backend.weatheralertapp.repositories.AlertRepository;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AlertService alertService;

    private User user;
    private WeatherAlert alert;
    private WeatherAlertResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("admin");

        alert = new WeatherAlert();
        alert.setId(1L);
        alert.setDescription("Heavy rain expected");
        alert.setLocationName("Sofia");
        alert.setSeverity(Severity.HIGH);
        alert.setCreatedBy(user);

        response = new WeatherAlertResponse(
                1L,
                "Heavy rain expected",
                "Sofia",
                null,
                null,
                Severity.HIGH
        );
    }

    @Test
    void create_savesAlertAndPublishesGeocodingEvent() {
        CreateWeatherAlertRequest request = new CreateWeatherAlertRequest(
                "Heavy rain expected",
                "Sofia",
                Severity.HIGH
        );

        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));
        when(mapper.toEntity(request)).thenReturn(alert);
        when(alertRepository.save(alert)).thenReturn(alert);
        when(mapper.toResponse(alert)).thenReturn(response);

        WeatherAlertResponse result =
                alertService.create(request, authentication);

        assertEquals(response, result);
        verify(alertRepository).save(alert);
        verify(eventPublisher).publishEvent(
                new GeocodingRequestedEvent(1L, "Sofia")
        );
    }

    @Test
    void findAll_returnsMappedAlerts() {
        when(alertRepository.findAll()).thenReturn(List.of(alert));
        when(mapper.toResponse(alert)).thenReturn(response);

        List<WeatherAlertResponse> result = alertService.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void findById_whenAlertExists_returnsResponse() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(mapper.toResponse(alert)).thenReturn(response);

        WeatherAlertResponse result = alertService.findById(1L);

        assertEquals(response, result);
    }

    @Test
    void findById_whenAlertDoesNotExist_throwsNotFound() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> alertService.findById(99L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND.value(),
                exception.getStatusCode().value()
        );
    }

    @Test
    void update_whenLocationChanges_clearsCoordinatesAndPublishesEvent() {
        alert.setLatitude(new BigDecimal("42.6977"));
        alert.setLongitude(new BigDecimal("23.3219"));

        UpdateWeatherRequest request = new UpdateWeatherRequest(
                "Updated description",
                "Varna",
                Severity.MEDIUM
        );

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        doAnswer(invocation -> {
            UpdateWeatherRequest updateRequest = invocation.getArgument(0);
            WeatherAlert targetAlert = invocation.getArgument(1);

            targetAlert.setDescription(updateRequest.description());
            targetAlert.setLocationName(updateRequest.locationName());
            targetAlert.setSeverity(updateRequest.severity());

            return null;
        }).when(mapper).updateEntity(request, alert);

        when(alertRepository.save(alert)).thenReturn(alert);

        WeatherAlertResponse updatedResponse = new WeatherAlertResponse(
                1L,
                "Updated description",
                "Varna",
                null,
                null,
                Severity.MEDIUM
        );

        when(mapper.toResponse(alert)).thenReturn(updatedResponse);

        WeatherAlertResponse result = alertService.update(1L, request);

        assertEquals(updatedResponse, result);
        assertEquals("Varna", alert.getLocationName());
        assertNull(alert.getLatitude());
        assertNull(alert.getLongitude());

        verify(alertRepository).save(alert);
        verify(eventPublisher).publishEvent(
                new GeocodingRequestedEvent(1L, "Varna")
        );
    }

    @Test
    void update_whenLocationDoesNotChange_doesNotPublishGeocodingEvent() {
        alert.setLatitude(new BigDecimal("42.6977"));
        alert.setLongitude(new BigDecimal("23.3219"));

        UpdateWeatherRequest request = new UpdateWeatherRequest(
                "Updated description",
                "Sofia",
                Severity.MEDIUM
        );

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        doAnswer(invocation -> {
            UpdateWeatherRequest updateRequest = invocation.getArgument(0);
            WeatherAlert targetAlert = invocation.getArgument(1);

            targetAlert.setDescription(updateRequest.description());
            targetAlert.setLocationName(updateRequest.locationName());
            targetAlert.setSeverity(updateRequest.severity());

            return null;
        }).when(mapper).updateEntity(request, alert);

        when(alertRepository.save(alert)).thenReturn(alert);

        WeatherAlertResponse updatedResponse = new WeatherAlertResponse(
                1L,
                "Updated description",
                "Sofia",
                alert.getLatitude(),
                alert.getLongitude(),
                Severity.MEDIUM
        );

        when(mapper.toResponse(alert)).thenReturn(updatedResponse);

        WeatherAlertResponse result = alertService.update(1L, request);

        assertEquals(updatedResponse, result);
        assertEquals(new BigDecimal("42.6977"), alert.getLatitude());
        assertEquals(new BigDecimal("23.3219"), alert.getLongitude());

        verify(alertRepository).save(alert);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void delete_whenAlertExists_deletesIt() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        alertService.delete(1L);

        verify(alertRepository).delete(alert);
    }
}