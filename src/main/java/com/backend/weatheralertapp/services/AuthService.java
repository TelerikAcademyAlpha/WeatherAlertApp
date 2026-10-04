package com.backend.weatheralertapp.services;

import com.backend.weatheralertapp.enums.Role;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.models.dtos.requests.RegisterRequest;
import com.backend.weatheralertapp.models.dtos.responses.UserResponse;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        String username = request.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse unblockUser(Long userId) {
        User user = findUser(userId);
        user.setEnabled(true);

        return toResponse(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(Long userId) {
        return toResponse(findUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse blockUser(
            Long userId,
            String currentUsername
    ) {
        User user = findUser(userId);

        if (user.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot block your own account"
            );
        }

        user.setEnabled(false);

        return toResponse(userRepository.save(user));
    }

    public UserResponse getCurrentUser(
            String username
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
