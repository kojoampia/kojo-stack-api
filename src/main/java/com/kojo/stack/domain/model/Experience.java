package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Experience Document
 * Represents professional work experience in MongoDB
 */
@Document(collection = "experiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experience implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String company;

    private String role;

    private String period;

    private StatusType status;

    private String description;

    private List<String> stack;

    private List<Metric> metrics;

    public enum StatusType {
        ACTIVE, 
        COMPLETED, 
        ON_HOLD, 
        CANCELLED, 
        PLANNED, 
        ARCHIVED, 
        ONGOING, 
        EXPIRED, 
        TERMINATED, 
        SUSPENDED,
        PENDING,
        REJECTED,
        APPROVED,
        IN_PROGRESS,
        FINISHED,
        CANCELED,
        WITHDRAWN,
        DEPRECATED,
        RETIRED,
        RESOLVED,
        OPEN,
        STABLE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metric {
        private String label;
        private String value;
    }
}
