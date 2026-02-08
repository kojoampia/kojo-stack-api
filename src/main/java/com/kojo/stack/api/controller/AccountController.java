package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.AccountDTO;
import com.kojo.stack.service.AccountService;
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
 * AccountController - REST endpoints for user login management
 * All endpoints require ROLE_ADMIN authentication
 */
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "User Accounts", description = "User account credentials and authorities management endpoints")
public class AccountController {

    private final AccountService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get all user logins")
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by ID")
    public ResponseEntity<AccountDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/login/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by login")
    public ResponseEntity<AccountDTO> getByLogin(@PathVariable String login) {
        return ResponseEntity.ok(service.getByLogin(login));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Get user login by email")
    public ResponseEntity<AccountDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Create new user login")
    public ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed
    @Operation(summary = "Update user login")
    public ResponseEntity<AccountDTO> update(@PathVariable String id, @RequestBody AccountDTO dto) {
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
