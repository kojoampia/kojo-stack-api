package com.kojo.stack.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController - Health check and status endpoints
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Application health and status")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @Operation(summary = "Get application health status")
    public ResponseEntity<? extends Health> getHealth() {
        return ResponseEntity.ok((Health) healthEndpoint.health());
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe for Kubernetes")
    public ResponseEntity<String> getLiveness() {
        return ResponseEntity.ok("UP");
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe for Kubernetes")
    public ResponseEntity<String> getReadiness() {
        return ResponseEntity.ok("READY");
    }
}
