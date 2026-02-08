package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.EducationDTO;
import com.kojo.stack.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EducationController - REST endpoints for education management
 */
@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
@Tag(name = "Education", description = "Education and certification endpoints")
public class EducationController {

    private final EducationService service;

    @GetMapping
    @Operation(summary = "Get all education records")
    public ResponseEntity<List<EducationDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get education by ID")
    public ResponseEntity<EducationDTO> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get education by type")
    public ResponseEntity<List<EducationDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @PostMapping
    @Operation(summary = "Create new education record")
    public ResponseEntity<EducationDTO> create(@RequestBody EducationDTO dto) {
        EducationDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update education record")
    public ResponseEntity<EducationDTO> update(@PathVariable String id, @RequestBody EducationDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete education record")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
