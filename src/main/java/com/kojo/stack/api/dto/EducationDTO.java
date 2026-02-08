package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * EducationDTO - Data Transfer Object for Education
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Education and certification record")
public class EducationDTO implements Serializable {

    @Schema(description = "Unique identifier")
    private String id;

    @Schema(description = "Educational institution name", example = "University of Technology (TU Wien)")
    private String institution;

    @Schema(description = "List of subjects studied", example = "[\"Computer Science\", \"Economics\"]")
    private List<String> subjects;

    @Schema(description = "Type of education", example = "University Bachelor Education")
    private String type;

    @Schema(description = "Duration of study", example = "October, 2004 - June 2010")
    private String duration;
}
