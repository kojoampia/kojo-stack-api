package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.KpiDTO;
import com.kojo.stack.service.KpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * KpiController - REST endpoints for dashboard KPI card management
 */
@RestController
@RequestMapping("/api/v1/kpis")
@RequiredArgsConstructor
@Tag(name = "KPIs", description = "Dashboard KPI card endpoints")
public class KpiController {

    private final KpiService service;

    @GetMapping
    @Operation(summary = "Get all KPI cards ordered by sort order")
    public ResponseEntity<List<KpiDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get KPI card by ID")
    public ResponseEntity<KpiDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new KPI card")
    public ResponseEntity<KpiDTO> create(@RequestBody KpiDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update KPI card")
    public ResponseEntity<KpiDTO> update(@PathVariable String id, @RequestBody KpiDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete KPI card")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
