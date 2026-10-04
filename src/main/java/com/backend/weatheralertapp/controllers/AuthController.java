package com.backend.weatheralertapp.controllers;

import com.backend.weatheralertapp.models.dtos.requests.RegisterRequest;
import com.backend.weatheralertapp.models.dtos.responses.UserResponse;
import com.backend.weatheralertapp.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/auth/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                authService.getAllUsers()
        );
    }

    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                authService.getUserById(userId)
        );
    }

    @PostMapping("/admin/users/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> blockUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.blockUser(
                        userId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/admin/users/{userId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse unblockUser(@PathVariable Long userId) {
        return authService.unblockUser(userId);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserResponse> me(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.getCurrentUser(
                        authentication.getName()
                )
        );
    }
}
