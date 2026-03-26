package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.MetricDTO;
import com.kojo.stack.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MetricController - REST endpoints for metric management
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Operational metrics endpoints")
public class MetricController {

    private final MetricService service;

    @GetMapping
    @Operation(summary = "Get all metrics")
    public ResponseEntity<List<MetricDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get metric by ID")
    public ResponseEntity<MetricDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get metrics by category")
    public ResponseEntity<List<MetricDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @PostMapping
    @Operation(summary = "Create new metric")
    public ResponseEntity<MetricDTO> create(@RequestBody MetricDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update metric")
    public ResponseEntity<MetricDTO> update(@PathVariable String id, @RequestBody MetricDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete metric")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
