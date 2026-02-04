package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * SettingsDTO - Data Transfer Object for Application Settings
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Application settings and preferences")
public class SettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "settings-001")
    private String id;

    @Schema(description = "Verbose logging enabled", example = "true")
    private Boolean verboseLogging;

    @Schema(description = "Beta features enabled", example = "false")
    private Boolean betaFeatures;

    @Schema(description = "UI theme", example = "default")
    private String theme;
}
