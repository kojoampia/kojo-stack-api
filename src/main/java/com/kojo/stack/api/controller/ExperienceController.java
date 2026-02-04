package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.ExperienceDTO;
import com.kojo.stack.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ExperienceController - REST endpoints for experience management
 */
@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
@Tag(name = "Experiences", description = "Professional experience endpoints")
public class ExperienceController {

    private final ExperienceService service;

    @GetMapping
    @Operation(summary = "Get all experiences", description = "Retrieve all professional experiences")
    public ResponseEntity<List<ExperienceDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active experiences")
    public ResponseEntity<List<ExperienceDTO>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @GetMapping("/search")
    @Operation(summary = "Search experiences by company")
    public ResponseEntity<List<ExperienceDTO>> searchByCompany(@RequestParam String company) {
        return ResponseEntity.ok(service.searchByCompany(company));
    }

    @PostMapping
    @Operation(summary = "Create new experience")
    public ResponseEntity<ExperienceDTO> create(@RequestBody ExperienceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update experience")
    public ResponseEntity<ExperienceDTO> update(@PathVariable String id, @RequestBody ExperienceDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete experience")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
