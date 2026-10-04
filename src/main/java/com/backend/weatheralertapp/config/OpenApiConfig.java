package com.backend.weatheralertapp.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Weather Alert API",
                version = "1.0",
                description = """
                        REST API for managing weather alerts.
                        Alerts are asynchronously enriched with geographic
                        coordinates using an external geocoding service.
                        """
        )
)
public class OpenApiConfig {
}