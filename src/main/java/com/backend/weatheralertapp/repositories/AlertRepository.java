package com.backend.weatheralertapp.repositories;

import com.backend.weatheralertapp.models.WeatherAlert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<WeatherAlert, Long> {
    List<WeatherAlert> findByCreatedBy_Username(String username);

    @EntityGraph(attributePaths = "createdBy")
    Optional<WeatherAlert> findAlertWithOwnerById(Long id);
}
