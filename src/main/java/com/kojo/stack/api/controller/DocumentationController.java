package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.DocDTO;
import com.kojo.stack.service.DocumentationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DocumentationController - REST endpoints for documentation management
 */
@RestController
@RequestMapping("/api/v1/docs")
@RequiredArgsConstructor
@Tag(name = "Documentation", description = "Technical documentation endpoints")
public class DocumentationController {

    private final DocumentationService service;

    @GetMapping
    @Operation(summary = "Get all documentation")
    public ResponseEntity<List<DocDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get documentation by type")
    public ResponseEntity<List<DocDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @GetMapping("/search")
    @Operation(summary = "Search documentation by title")
    public ResponseEntity<List<DocDTO>> search(@RequestParam String title) {
        return ResponseEntity.ok(service.searchByTitle(title));
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get documentation by tag")
    public ResponseEntity<List<DocDTO>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(service.getByTag(tag));
    }

    @PostMapping
    @Operation(summary = "Create new documentation")
    public ResponseEntity<DocDTO> create(@RequestBody DocDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update documentation")
    public ResponseEntity<DocDTO> update(@PathVariable String id, @RequestBody DocDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete documentation")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
