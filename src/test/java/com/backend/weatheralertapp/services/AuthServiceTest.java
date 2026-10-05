package com.backend.weatheralertapp.services;


import com.backend.weatheralertapp.enums.Role;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.models.dtos.requests.RegisterRequest;
import com.backend.weatheralertapp.models.dtos.responses.UserResponse;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_createsEnabledUserWithEncodedPassword() {

        RegisterRequest request =
                new RegisterRequest("  weatherAdmin  ", "Password123");

        when(userRepository.existsByUsername("weatherAdmin"))
                .thenReturn(false);
        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = authService.register(request);

        assertEquals(1L, response.id());
        assertEquals("weatherAdmin", response.username());
        assertEquals(Role.USER, response.role());
        assertTrue(response.enabled());

        verify(passwordEncoder).encode("Password123");
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encoded-password")
                        && user.getRole() == Role.USER
                        && user.isEnabled()
        ));
    }

    @Test
    void register_whenUsernameAlreadyExists_throwsConflict() {
        RegisterRequest request =
                new RegisterRequest("weatherAdmin", "Password123");

        when(userRepository.existsByUsername("weatherAdmin"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(
                HttpStatus.CONFLICT.value(),
                exception.getStatusCode().value()
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void blockUser_disablesUser() {
        User user = createUser(10L, "weatherUser", true);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = authService.blockUser(10L,"test");

        assertFalse(response.enabled());
        verify(userRepository).save(user);
    }

    @Test
    void unblockUser_enablesUser() {
        User user = createUser(10L, "weatherUser", false);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = authService.unblockUser(10L);

        assertTrue(response.enabled());
        verify(userRepository).save(user);
    }

    @Test
    void blockUser_whenUserDoesNotExist_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.blockUser(99L,"test")
        );

        assertEquals(
                HttpStatus.NOT_FOUND.value(),
                exception.getStatusCode().value()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    private User createUser(Long id, String username, boolean enabled) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(Role.USER);
        user.setEnabled(enabled);
        return user;
    }
}