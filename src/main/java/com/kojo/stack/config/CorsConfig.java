package com.kojo.stack.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * CORS Configuration
 * Allows the Angular frontend to communicate with the Spring Boot backend.
 *
 * Origins come from {@code app.security.cors-origins} (env: {@code CORS_ORIGINS}) as a
 * comma-separated list. When unset, the local development origins below are used, which
 * keeps `npm start` working without extra configuration while granting nothing in
 * production. Previously the origin list was hardcoded and the configuration property
 * was read by nothing.
 */
@Configuration
@Slf4j
public class CorsConfig {

    /**
     * Origins used when {@code app.security.cors-origins} is not configured.
     */
    private static final List<String> DEFAULT_DEV_ORIGINS = List.of(
            "http://localhost:4200",   // Angular dev server
            "http://localhost:4800",   // Angular alt port
            "http://127.0.0.1:4200",
            "http://127.0.0.1:4800");

    @Value("${app.security.cors-origins:}")
    private String corsOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = resolveOrigins();
        origins.forEach(config::addAllowedOrigin);
        log.info("CORS allowed origins: {}", origins);

        // Allow credentials in requests. This is incompatible with a wildcard origin,
        // which is why origins are always an explicit list.
        config.setAllowCredentials(true);

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

    private List<String> resolveOrigins() {
        if (corsOrigins == null || corsOrigins.isBlank()) {
            log.info("app.security.cors-origins is not set; falling back to development origins");
            return DEFAULT_DEV_ORIGINS;
        }
        return List.of(corsOrigins.split(",")).stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }
}
