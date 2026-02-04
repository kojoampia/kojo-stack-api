package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Settings - MongoDB document for application settings
 */
@Document(collection = "settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private Boolean verboseLogging;

    private Boolean betaFeatures;

    private String theme;
}
