package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * ProjectDTO - Data Transfer Object for Project
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Consulting project information")
public class ProjectDTO implements Serializable {

    @Schema(description = "Unique identifier", example = "proj-001")
    private String id;

    @Schema(description = "Project name", example = "Health Brokerage Platform")
    private String name;

    @Schema(description = "Client company", example = "Abofonsa Mobile Health")
    private String client;

    @Schema(description = "Project type", example = "MICROSERVICES")
    private String type;

    @Schema(description = "Project description")
    private String description;

    @Schema(description = "Technology stack")
    private List<String> stack;

    @Schema(description = "Project status", example = "ACTIVE")
    private String status;

    @Schema(description = "Architecture pattern", example = "Event-Driven")
    private String architecture;

    @Schema(description = "Project start date")
    private LocalDate startDate;

    @Schema(description = "Project end date")
    private LocalDate endDate;
}
