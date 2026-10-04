package com.backend.weatheralertapp.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    @Bean
    public OpenApiCustomizer authenticationEndpoints() {

        return openApi -> {

            Schema<Object> loginSchema = new Schema<>()
                    .type("object");

            loginSchema.addProperty(
                    "username",
                    new Schema<String>()
                            .type("string")
                            .example("admin")
            );

            loginSchema.addProperty(
                    "password",
                    new Schema<String>()
                            .type("string")
                            .format("password")
            );

            RequestBody loginRequest = new RequestBody()
                    .required(true)
                    .content(
                            new Content().addMediaType(
                                    "application/x-www-form-urlencoded",
                                    new MediaType().schema(loginSchema)
                            )
                    );

            Operation loginOperation = new Operation()
                    .tags(List.of("Authentication"))
                    .summary("Login")
                    .description(
                            "Authenticates a user and creates an HTTP session."
                    )
                    .addParametersItem(
                            new Parameter()
                                    .name("X-XSRF-TOKEN")
                                    .in("header")
                                    .required(true)
                                    .description("CSRF token from GET /api/auth/csrf")
                                    .schema(new Schema<String>().type("string"))
                    )
                    .requestBody(loginRequest)
                    .responses(
                            new ApiResponses()
                                    .addApiResponse(
                                            "204",
                                            new ApiResponse()
                                                    .description("Login successful")
                                    )
                                    .addApiResponse(
                                            "401",
                                            new ApiResponse()
                                                    .description("Invalid username or password")
                                    )
                    );

            openApi.getPaths().addPathItem(
                    "/api/auth/login",
                    new PathItem().post(loginOperation)
            );

            Operation logoutOperation = new Operation()
                    .tags(List.of("Authentication"))
                    .summary("Logout")
                    .description("Logs out the current authenticated user and invalidates the HTTP session.")
                    .addParametersItem(
                            new Parameter()
                                    .name("X-XSRF-TOKEN")
                                    .in("header")
                                    .required(true)
                                    .description("CSRF token from GET /api/auth/csrf")
                                    .schema(new Schema<String>().type("string"))
                    )
                    .responses(
                            new ApiResponses()
                                    .addApiResponse(
                                            "204",
                                            new ApiResponse()
                                                    .description("Logout successful")
                                    )
                                    .addApiResponse(
                                            "403",
                                            new ApiResponse()
                                                    .description("Invalid or missing CSRF token")
                                    )
                    );

            openApi.getPaths().addPathItem(
                    "/api/auth/logout",
                    new PathItem().post(logoutOperation)
            );
        };
    }
}