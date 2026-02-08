package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Project Document
 * Represents a consulting project or engagement in MongoDB
 */
@Document(collection = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    private String client;

    private ProjectType type;

    private String description;

    private List<String> stack;

    private ProjectStatus status;

    private String architecture;

    private LocalDate startDate;

    private LocalDate endDate;

    public enum ProjectType {
        MICROSERVICES, DEVOPS, BACKEND_SERVICE, FRONTEND, FULL_STACK, DATA_ENGINEERING, CONSULTING, MIGRATION, ETL, MONITORING
    }

    public enum ProjectStatus {
        LIVE, PENDING, MAINTENANCE, CONSULTING, PLANNING, ACTIVE, COMPLETED, ON_HOLD, ARCHIVED
    }
}
