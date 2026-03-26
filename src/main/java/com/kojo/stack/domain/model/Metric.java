package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Metric - MongoDB document for operational metrics
 */
@Document(collection = "metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String label;

    private String value;

    private String trend;

    private String category;

    private String description;
}
