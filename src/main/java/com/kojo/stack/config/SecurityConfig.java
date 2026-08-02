package com.kojo.stack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kojo.stack.security.CustomUserDetailsService;
import com.kojo.stack.security.JwtAuthenticationFilter;
import com.kojo.stack.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * SecurityConfig - Spring Security configuration for stateless JWT authentication
 * - Public content (projects, skills, docs, experience, education, profile) is readable anonymously
 * - Account, authority and inquiry data require authentication and are further guarded by @PreAuthorize
 * - POST, PUT, PATCH, DELETE requests require JWT authentication
 * - Session management is stateless
 * - Uses CustomUserDetailsService to load users from MongoDB
 *
 * {@code @EnableMethodSecurity} is required for the {@code @PreAuthorize} annotations on
 * AccountController and AuthorityController to be enforced at all.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        
        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        // Disable CSRF for stateless API
        .csrf(csrf -> csrf.disable())

        // Set session management to stateless
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Configure authorization rules
        .authorizeHttpRequests(authz -> authz
                // Authentication endpoints - public (must be before catch-all rules)
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/account/reset-password/init").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/account/reset-password/finish").permitAll()

                // Public "Hire Consultant" form submission
                .requestMatchers(HttpMethod.POST, "/api/v1/inquiries/submit").permitAll()

                // Public portfolio content - anonymous read only.
                // Deliberately enumerated: a blanket GET /api/v1/** rule previously exposed
                // account records (including password hashes) and customer inquiries.
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/projects/**",
                        "/api/v1/skills/**",
                        "/api/v1/docs/**",
                        "/api/v1/experiences/**",
                        "/api/v1/education/**",
                        "/api/v1/profiles/**",
                        "/api/v1/settings/**",
                        "/api/v1/kpis/**",
                        "/api/v1/health/**").permitAll()

                // Infrastructure endpoints
                .requestMatchers(HttpMethod.GET, "/health", "/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Sensitive resources - authentication required for every method.
                // Fine-grained role checks live on the controllers via @PreAuthorize.
                .requestMatchers("/api/v1/account/**").authenticated()
                .requestMatchers("/api/v1/authorities/**").authenticated()
                .requestMatchers("/api/v1/inquiries/**").authenticated()
                .requestMatchers("/api/v1/metrics/**").authenticated()

                // Any other request requires authentication
                .anyRequest().authenticated()
        )

        // Add JWT filter
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

        // Exception handling
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendError(403, "Forbidden");
                })
        );

        return http.build();
    }
}
