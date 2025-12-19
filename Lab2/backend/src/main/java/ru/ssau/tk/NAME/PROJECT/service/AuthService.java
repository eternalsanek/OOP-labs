package ru.ssau.tk.NAME.PROJECT.service;

import ru.ssau.tk.NAME.PROJECT.dto.AuthRequestDTO;
import ru.ssau.tk.NAME.PROJECT.dto.AuthResponseDTO;
import ru.ssau.tk.NAME.PROJECT.dto.UserDTO;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(AuthRequestDTO request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if user exists
        if (userService.getUserByName(request.getUsername()).isPresent()) {
            log.warn("User already exists: {}", request.getUsername());
            throw new AuthException("User already exists");
        }

        // Create user DTO
        UserDTO userDTO = UserDTO.builder()
                .name(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER") // Default role
                .build();

        // Create user
        UserDTO createdUser = userService.createUser(userDTO);

        log.info("User registered successfully: {}", createdUser.getName());

        // Generate auth response
        return AuthResponseDTO.builder()
                .id(createdUser.getId())  // Добавляем id
                .username(createdUser.getName())
                .role(createdUser.getRole())
                .message("User registered successfully")
                .build();
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());

        try {
            log.debug("Attempting authentication for user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            log.debug("Authentication successful for user: {}", request.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDTO user = userService.getUserByName(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("User not found after successful authentication: {}", request.getUsername());
                        return new AuthException("User not found");
                    });

            log.info("User logged in successfully: {}", user.getName());

            return AuthResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getName())
                    .role(user.getRole())
                    .message("Login successful")
                    .build();

        } catch (Exception e) {
            log.error("Authentication failed for user: {}. Error: {}", request.getUsername(), e.getMessage(), e);
            throw new AuthException("Invalid username or password");
        }
    }

    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("User logged out: {}", auth.getName());
            SecurityContextHolder.clearContext();
        }
    }

    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        return userService.getUserByName(auth.getName())
                .orElse(null);
    }
}
