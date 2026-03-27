package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Kpi - MongoDB document for dashboard KPI cards
 */
@Document(collection = "kpis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kpi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String label;

    private String value;

    private String unit;

    private String icon;

    private String color;

    private Integer progress;

    private String subtitle;

    private Integer sortOrder;
}
