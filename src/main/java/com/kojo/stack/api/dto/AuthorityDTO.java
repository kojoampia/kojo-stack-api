package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * AuthorityDTO - Data Transfer Object for Authority
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User authority/role information")
public class AuthorityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "auth-001")
    private String id;

    @Schema(description = "Authority name", example = "ROLE_ADMIN")
    private String name;
}
