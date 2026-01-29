package com.auction.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration - Central security setup.
 * 
 * Key Configurations:
 * 1. Stateless session management (no server-side sessions)
 * 2. CORS configuration for frontend
 * 3. JWT authentication filter
 * 4. Password encryption
 * 5. Method-level security (@PreAuthorize)
 * 
 * @EnableWebSecurity - Enables Spring Security
 * @EnableMethodSecurity - Enables @PreAuthorize on methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password encoder bean - BCrypt hashing algorithm.
     * 
     * BCrypt Features:
     * - Automatic salt generation (prevents rainbow table attacks)
     * - Adaptive (can increase rounds as hardware improves)
     * - One-way hash (cannot decrypt)
     * - Slow by design (prevents brute force)
     * 
     * Default strength: 10 rounds
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager - entry point for authentication.
     * 
     * Used in AuthService.login() to authenticate credentials.
     * Spring Security 7 requires UserDetailsService in constructor.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        
        // In Spring Security 7, constructor requires UserDetailsService
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        
        return new ProviderManager(List.of(authProvider));
    }

    /**
     * Security filter chain - main security configuration.
     * 
     * Order matters! Filters execute in the order they're added.
     * Our JWT filter runs BEFORE UsernamePasswordAuthenticationFilter
     * to set SecurityContext before authorization checks.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT authentication)
                .csrf(csrf -> csrf.disable())
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Stateless session management (no server-side sessions)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers("/graphql").permitAll()
                        .requestMatchers("/graphiql/**").permitAll()
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS configuration - allow frontend origin.
     * 
     * CORS (Cross-Origin Resource Sharing):
     * - Browser security feature
     * - Blocks requests from different origins (domain:port)
     * - Our frontend (localhost:5173) ≠ backend (localhost:8080)
     * - Must explicitly allow frontend origin
     * 
     * Production: Replace localhost with actual frontend URL
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow frontend origin
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174"  // Backup port
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Allow all headers (Authorization header needed for JWT)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight responses for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

