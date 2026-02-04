package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.SettingsDTO;
import com.kojo.stack.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SettingsController - REST endpoints for application settings management
 */
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Application settings management endpoints")
public class SettingsController {

    private final SettingsService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get settings by ID")
    public ResponseEntity<SettingsDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new application settings")
    public ResponseEntity<SettingsDTO> create(@RequestBody SettingsDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update application settings")
    public ResponseEntity<SettingsDTO> update(@PathVariable String id, @RequestBody SettingsDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete application settings")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
