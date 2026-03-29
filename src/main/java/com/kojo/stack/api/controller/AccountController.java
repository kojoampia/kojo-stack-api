package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.AccountDTO;
import com.kojo.stack.api.vm.ManagedUserVM;
import com.kojo.stack.service.AccountService;
import com.kojo.stack.service.InvalidPasswordException;
import com.kojo.stack.service.UserService;
import com.kojo.stack.service.dto.PasswordChangeDTO;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    /**
     * Get the currently authenticated user's account info.
     * This endpoint is used by the Angular frontend after login.
     */
    @GetMapping
    @Timed
    @Operation(summary = "Get current authenticated user account")
    public ResponseEntity<AccountDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.getByLogin(authentication.getName()));
    }

    @GetMapping("/all")
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

    /**
     * {@code POST  /change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping("/change-password")
    @Timed
    @Operation(summary = "Change current user's password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        service.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
