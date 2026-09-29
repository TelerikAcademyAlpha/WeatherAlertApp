package com.backend.weatheralertapp.repositories;

import com.backend.weatheralertapp.models.WeatherAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<WeatherAlert, Long> {
}
