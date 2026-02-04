package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * UserLoginDTO - Data Transfer Object for UserLogin
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login credentials and authorities")
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "user-001")
    private String id;

    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "Email address", example = "admin@example.com")
    private String email;

    @Schema(description = "Password", example = "admin123")
    private String password;

    @Schema(description = "Set of authorities/roles")
    private Set<AuthorityDTO> authorities;

    /**
     * Get authorities as a string array of authority names
     */
    public String[] getAuthoritiesAsArray() {
        if (authorities == null || authorities.isEmpty()) {
            return new String[0];
        }
        return authorities.stream()
                .map(AuthorityDTO::getName)
                .toArray(String[]::new);
    }
}
