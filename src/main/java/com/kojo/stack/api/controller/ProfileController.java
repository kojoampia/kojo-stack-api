package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.ProfileDTO;
import com.kojo.stack.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProfileController - REST endpoints for user profile management
 */
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "User profile management endpoints")
public class ProfileController {

    private final ProfileService service;

    @GetMapping
    @Operation(summary = "Get all user profiles")
    public ResponseEntity<List<ProfileDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID")
    public ResponseEntity<ProfileDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get profile by email")
    public ResponseEntity<ProfileDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @PostMapping
    @Operation(summary = "Create new user profile")
    public ResponseEntity<ProfileDTO> create(@RequestBody ProfileDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ProfileDTO> update(@PathVariable String id, @RequestBody ProfileDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
