package com.backend.weatheralertapp.models;

import com.backend.weatheralertapp.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "weather_alerts")
@NoArgsConstructor
@Getter
@Setter
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

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}
