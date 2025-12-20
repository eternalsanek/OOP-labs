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
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/", "/api/v1/", "/api/test", "/api/debug/**").permitAll()
                        .requestMatchers("/", "/api/test", "/api/debug/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()  // For H2 console if used

                        // User registration and login (public)
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
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
                        .requestMatchers("/api/search/**").authenticated()

                        // Deny all other requests
                        .anyRequest().denyAll()
                )
                .httpBasic(httpBasic -> {});

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
