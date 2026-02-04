package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DocDTO - Data Transfer Object for Documentation
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Technical documentation, ADRs, and guides")
public class DocDTO implements Serializable {

    @Schema(description = "Document identifier", example = "ADR-2024-001")
    private String id;

    @Schema(description = "Document title", example = "Event-Driven Architecture Strategy")
    private String title;

    @Schema(description = "Document type", example = "ADR")
    private String type;

    @Schema(description = "Associated tags")
    private List<String> tags;

    @Schema(description = "Document content")
    private String content;

    @Schema(description = "Last updated date")
    private LocalDate lastUpdated;
}
