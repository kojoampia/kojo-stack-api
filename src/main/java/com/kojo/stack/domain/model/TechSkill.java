package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * TechSkill Document
 * Represents technical skills and proficiency levels in MongoDB
 */
@Document(collection = "tech_skills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    private String category;

    private Integer level;  // 0-100 proficiency

    private String icon;
}
