package com.backend.weatheralertapp.config;

import com.backend.weatheralertapp.enums.Role;
import com.backend.weatheralertapp.models.User;
import com.backend.weatheralertapp.repositories.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    ApplicationRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Environment environment
    ) {
        return args -> {
            boolean enabled = environment.getProperty(
                    "app.bootstrap-admin.enabled",
                    Boolean.class,
                    false
            );

            if (!enabled){
                return;
            }

            String username = environment.getProperty(
                    "app.bootstrap-admin.username",
                    ""
            );

            String password = environment.getProperty(
                    "app.bootstrap-admin.password",
                    ""
            );

            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Bootstrap admin is enabled, but username or password is missing"
                );
            }

            if (userRepository.existsByUsername(username)) {
                return;
            }

            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);

            userRepository.save(admin);

        };
    }
}
