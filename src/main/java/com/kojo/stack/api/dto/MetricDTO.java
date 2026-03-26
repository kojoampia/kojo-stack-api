package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * MetricDTO - Data Transfer Object for Metric
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Operational metric entry")
public class MetricDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "metric-001")
    private String id;

    @Schema(description = "Metric label", example = "API Availability")
    private String label;

    @Schema(description = "Metric value", example = "99.95%")
    private String value;

    @Schema(description = "Trend direction", example = "up")
    private String trend;

    @Schema(description = "Metric category", example = "Availability")
    private String category;

    @Schema(description = "Metric description", example = "Overall API uptime across all services")
    private String description;
}
