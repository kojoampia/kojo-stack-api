package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.TechSkillDTO;
import com.kojo.stack.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SkillController - REST endpoints for skill browsing
 */
@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Technical skills endpoints")
public class SkillController {

    private final SkillService service;

    @GetMapping
    @Operation(summary = "Get all skills")
    public ResponseEntity<List<TechSkillDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get skills by category")
    public ResponseEntity<List<TechSkillDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @GetMapping("/expert")
    @Operation(summary = "Get expert-level skills", description = "Get all skills with proficiency >= 80")
    public ResponseEntity<List<TechSkillDTO>> getExpertSkills() {
        return ResponseEntity.ok(service.getExpertSkills());
    }
}
