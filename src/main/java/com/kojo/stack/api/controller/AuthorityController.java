package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.AuthorityDTO;
import com.kojo.stack.service.AuthorityService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AuthorityController - REST endpoints for authority/role management
 * All endpoints require ROLE_ADMIN authentication
 */
@RestController
@RequestMapping("/api/v1/authorities")
@RequiredArgsConstructor
@Tag(name = "Authorities", description = "User authority/role management endpoints")
public class AuthorityController {

    private final AuthorityService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get all authorities")
    public ResponseEntity<List<AuthorityDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get authority by ID")
    public ResponseEntity<AuthorityDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get authority by name")
    public ResponseEntity<AuthorityDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(service.getByName(name));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Create new authority")
    public ResponseEntity<AuthorityDTO> create(@RequestBody AuthorityDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Update authority")
    public ResponseEntity<AuthorityDTO> update(@PathVariable String id, @RequestBody AuthorityDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Delete authority")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
