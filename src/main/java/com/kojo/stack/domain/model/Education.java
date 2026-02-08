package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Education Document
 * Represents educational background and certifications in MongoDB
 */
@Document(collection = "education")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String institution;

    private List<String> subjects;

    private String type;

    private String duration;
}
