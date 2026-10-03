package com.backend.weatheralertapp.models.dtos.responses;

import com.backend.weatheralertapp.enums.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        boolean enabled) {
}