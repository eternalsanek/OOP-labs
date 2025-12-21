package ru.ssau.tk.NAME.PROJECT.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer; // Добавлен импорт для Customizer

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");

        http
                // Отключаем встроенную поддержку CORS в Spring Security, полагаясь на WebConfig.java
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // Настройка сессий для совместимости с Basic Auth (если используется сессия)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Используем сессии, если нужно
                )
                // Настройка HTTP Basic
                .httpBasic(Customizer.withDefaults())
                // Настройка авторизации запросов
                .authorizeHttpRequests(auth -> auth
                        // Разрешить все OPTIONS запросы (CORS Preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public endpoints
                        .requestMatchers(HttpMethod.GET, "/", "/api/v1/", "/api/test", "/api/debug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/api/test", "/api/debug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()  // For H2 console if used

                        // User registration and login (public) - ВАЖНО: эти строки теперь идут ПОСЛЕ OPTIONS
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole("ADMIN", "MODERATOR")

                        // User management (self or admin)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").authenticated()

                        // Functions (authenticated users)
                        .requestMatchers(HttpMethod.GET, "/api/v1/functions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/functions").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/functions/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/functions/**").hasAnyRole("USER", "ADMIN", "MODERATOR")

                        // Points (authenticated users)
                        .requestMatchers(HttpMethod.GET, "/api/v1/points/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/points").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/points/**").hasAnyRole("USER", "ADMIN", "MODERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/points/**").hasAnyRole("USER", "ADMIN", "MODERATOR")

                        // Search API (authenticated)
                        .requestMatchers(HttpMethod.GET, "/api/search/**").authenticated()

                        // Deny all other requests
                        .anyRequest().denyAll()
                );

        log.info("Security configuration applied successfully");
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("Creating DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("Creating AuthenticationManager");
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }
}
