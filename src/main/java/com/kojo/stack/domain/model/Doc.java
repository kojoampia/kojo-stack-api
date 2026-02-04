package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Doc Document
 * Represents technical documentation, ADRs, and guides in MongoDB
 */
@Document(collection = "documentation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String title;

    private String type;

    private List<String> tags;

    private String content;

    private LocalDate lastUpdated;
}
