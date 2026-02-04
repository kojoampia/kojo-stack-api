package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * TechSkillDTO - Data Transfer Object for TechSkill
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Technical skill and proficiency level")
public class TechSkillDTO implements Serializable {

    @Schema(description = "Unique identifier")
    private String id;

    @Schema(description = "Skill name", example = "Java 21")
    private String name;

    @Schema(description = "Skill category", example = "Backend")
    private String category;

    @Schema(description = "Proficiency level (0-100)", example = "95")
    private Integer level;

    @Schema(description = "Icon name for UI", example = "java")
    private String icon;
}
