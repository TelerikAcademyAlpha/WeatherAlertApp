package com.backend.weatheralertapp.models;

import com.backend.weatheralertapp.enums.Severity;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "weather_alerts")
public class WeatherAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 400)
    private String locationName;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    public WeatherAlert(){}


}
