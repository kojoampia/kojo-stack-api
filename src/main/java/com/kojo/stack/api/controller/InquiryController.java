package com.kojo.stack.api.controller;

import com.kojo.stack.api.dto.InquiryDTO;
import com.kojo.stack.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * InquiryController - REST endpoints for inquiry management
 * Handles the "Hire Consultant" form submissions
 */
@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
@Tag(name = "Inquiries", description = "Consulting inquiry endpoints")
public class InquiryController {

    private final InquiryService service;

    @GetMapping
    @Operation(summary = "Get all inquiries", description = "Retrieve all consulting inquiries (admin only)")
    public ResponseEntity<List<InquiryDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/new")
    @Operation(summary = "Get new inquiries")
    public ResponseEntity<List<InquiryDTO>> getNewInquiries() {
        return ResponseEntity.ok(service.getNewInquiries());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get inquiries by type")
    public ResponseEntity<List<InquiryDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit new inquiry", description = "Submit a consulting inquiry through the 'Hire Consultant' form")
    public ResponseEntity<InquiryDTO> submitInquiry(@Valid @RequestBody InquiryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submitInquiry(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inquiry")
    public ResponseEntity<InquiryDTO> update(@PathVariable String id, @Valid @RequestBody InquiryDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update inquiry status")
    public ResponseEntity<InquiryDTO> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inquiry")
    public ResponseEntity<Void> deleteInquiry(@PathVariable String id) {
        service.deleteInquiry(id);
        return ResponseEntity.noContent().build();
    }
}
