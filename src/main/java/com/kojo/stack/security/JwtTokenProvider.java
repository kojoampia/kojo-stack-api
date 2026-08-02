package com.kojo.stack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtTokenProvider - Manages JWT token generation, validation, and claims extraction
 * Uses HMAC with SHA-512 for secure token signing
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * Claim carrying the comma-separated granted authorities of the subject.
     */
    public static final String AUTHORITIES_CLAIM = "auth";

    @Value("${app.jwtSecret:}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}")
    private long jwtExpirationMs;

    /**
     * HS256 requires a key of at least 256 bits. Refuse to start with a missing or
     * undersized secret rather than silently falling back to a well-known default.
     */
    @PostConstruct
    void validateSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "app.jwtSecret is not configured. Set the JWT_SECRET environment variable "
                            + "to a random value of at least 32 characters.");
        }
        if (jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "app.jwtSecret is too short for HS256; it must be at least 32 bytes.");
        }
    }

    /**
     * Generate JWT token from authentication
     */
    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return buildToken(authentication.getName(), authorities);
    }

    /**
     * Generate JWT token from username (useful for testing)
     */
    public String generateToken(String username) {
        return buildToken(username, "");
    }

    /**
     * Generate JWT token from a username and an explicit set of authorities.
     */
    public String generateToken(String username, Collection<String> authorities) {
        return buildToken(username, String.join(",", authorities));
    }

    private String buildToken(String username, String authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_CLAIM, authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Read the granted authorities carried by the token. Tokens issued before the
     * authorities claim existed yield an empty list rather than failing.
     */
    public List<String> getAuthoritiesFromToken(String token) {
        Object claim = getAllClaimsFromToken(token).get(AUTHORITIES_CLAIM);
        if (claim == null || claim.toString().isBlank()) {
            return List.of();
        }
        return List.of(claim.toString().split(",")).stream()
                .map(String::trim)
                .filter(authority -> !authority.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Get expiration date from JWT token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Get all claims from JWT token
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}
