package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.ProjectDTO;
import com.kojo.stack.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController - REST endpoints for project management
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project portfolio endpoints")
public class ProjectController {

    private final ProjectService service;

    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieve all consulting projects")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active projects")
    public ResponseEntity<List<ProjectDTO>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get projects by type", description = "Filter projects by type (MICROSERVICES, DEVOPS, etc.)")
    public ResponseEntity<List<ProjectDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects by client")
    public ResponseEntity<List<ProjectDTO>> searchByClient(@RequestParam String client) {
        return ResponseEntity.ok(service.searchByClient(client));
    }

    @PostMapping
    @Operation(summary = "Create new project")
    public ResponseEntity<ProjectDTO> create(@RequestBody ProjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public ResponseEntity<ProjectDTO> update(@PathVariable String id, @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
