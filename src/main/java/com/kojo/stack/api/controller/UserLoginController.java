package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.UserLoginDTO;
import com.kojo.stack.service.UserLoginService;
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
 * UserLoginController - REST endpoints for user login management
 * All endpoints require ROLE_ADMIN authentication
 */
@RestController
@RequestMapping("/api/v1/user-logins")
@RequiredArgsConstructor
@Tag(name = "User Logins", description = "User login credentials and authorities management endpoints")
public class UserLoginController {

    private final UserLoginService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get all user logins")
    public ResponseEntity<List<UserLoginDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by ID")
    public ResponseEntity<UserLoginDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by username")
    public ResponseEntity<UserLoginDTO> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getByUsername(username));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by email")
    public ResponseEntity<UserLoginDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Create new user login")
    public ResponseEntity<UserLoginDTO> create(@RequestBody UserLoginDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Update user login")
    public ResponseEntity<UserLoginDTO> update(@PathVariable String id, @RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Delete user login")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
