package com.kojo.stack.api.controller;

import com.kojo.stack.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - JWT Authentication endpoints
 * Provides login and token generation
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "JWT authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            
            log.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid token format"));
        }

        String jwt = token.substring(7);
        if (tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromToken(jwt);
            return ResponseEntity.ok(new TokenValidationResponse(true, username));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new TokenValidationResponse(false, null));
    }

    /**
     * LoginRequest DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * AuthResponse DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String type;
    }

    /**
     * TokenValidationResponse DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
    }

    /**
     * ErrorResponse DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
