package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * KpiDTO - Data Transfer Object for Kpi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard KPI card entry")
public class KpiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "kpi-001")
    private String id;

    @Schema(description = "KPI label", example = "Total Experience")
    private String label;

    @Schema(description = "Primary display value", example = "16+")
    private String value;

    @Schema(description = "Value unit or suffix", example = "Years")
    private String unit;

    @Schema(description = "Icon class name", example = "fa-clock")
    private String icon;

    @Schema(description = "Accent color name", example = "cyan")
    private String color;

    @Schema(description = "Progress bar percentage (0-100)", example = "95")
    private Integer progress;

    @Schema(description = "Secondary text below the value", example = "Java • Angular • K8s")
    private String subtitle;

    @Schema(description = "Display order", example = "1")
    private Integer sortOrder;
}
