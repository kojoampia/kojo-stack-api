package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * ProfileDTO - Data Transfer Object for User Profile
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class ProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "profile-001")
    private String id;

    @Schema(description = "Full name", example = "John Kojo Ampia-Addison")
    private String name;

    @Schema(description = "Professional title", example = "Senior Software Architect & DevOps Engineer")
    private String title;

    @Schema(description = "Email address", example = "kojo.ampia@jojoaddison.net")
    private String email;

    @Schema(description = "Phone number", example = "+43 676 922 1796")
    private String phone;

    @Schema(description = "Location", example = "Vienna, Austria")
    private String location;

    @Schema(description = "Avatar URL", example = "assets/kojo-ampia-addison.jpeg")
    private String avatar;
}
