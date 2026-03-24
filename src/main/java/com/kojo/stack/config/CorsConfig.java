package com.kojo.stack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS Configuration
 * Allows Angular frontend to communicate with the Spring Boot backend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials in requests
        config.setAllowCredentials(true);

        // Allow specific origins (development)
        config.addAllowedOrigin("http://localhost:4200");    // Angular dev server
        config.addAllowedOrigin("http://localhost:4800");    // Angular alt port
        config.addAllowedOrigin("http://localhost:8080");    // Docker frontend
        config.addAllowedOrigin("http://127.0.0.1:4200");
        config.addAllowedOrigin("http://127.0.0.1:4800");
        config.addAllowedOrigin("http://localhost:8085");    // Spring Boot backend

        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // Expose custom headers
        config.addExposedHeader("X-Total-Count");
        config.addExposedHeader("X-Page-Number");
        config.addExposedHeader("X-Page-Size");

        // Max age for preflight requests
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
