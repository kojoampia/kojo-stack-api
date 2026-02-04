package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * ExperienceDTO - Data Transfer Object for Experience
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Professional experience information")
public class ExperienceDTO implements Serializable {

    @Schema(description = "Unique identifier", example = "exp-001")
    private String id;

    @Schema(description = "Company name", example = "Bundesrechenzentrum (BRZ)")
    private String company;

    @Schema(description = "Job title", example = "Software Developer & DevOps Engineer")
    private String role;

    @Schema(description = "Employment period", example = "07/2025 - Present")
    private String period;

    @Schema(description = "Current status", example = "ACTIVE")
    private String status;

    @Schema(description = "Job description")
    private String description;

    @Schema(description = "Technology stack used")
    private List<String> stack;

    @Schema(description = "Performance metrics")
    private List<MetricDTO> metrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metric data")
    public static class MetricDTO implements Serializable {
        @Schema(description = "Metric label", example = "Performance")
        private String label;
        
        @Schema(description = "Metric value", example = "95%")
        private String value;
    }
}
