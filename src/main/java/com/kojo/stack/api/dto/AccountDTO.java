package com.kojo.stack.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * AccountDTO - Data Transfer Object for Account
 * Used for REST API requests/responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login credentials and authorities")
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier", example = "user-001")
    private String id;

    @Schema(description = "Login", example = "admin")
    private String login;

    @Schema(description = "Email address", example = "admin@example.com")
    private String email;

    /**
     * Accepted on create/update, never serialized back to a client.
     * {@link com.kojo.stack.service.AccountService#toDTO} additionally refuses to populate it.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Password (write-only)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Set of authorities/roles")
    private Set<AuthorityDTO> authorities;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Account activation status", example = "true")
    private boolean activated;

    @Schema(description = "Language key", example = "en")
    private String langKey;

    @Schema(description = "Image URL", example = "http://example.com/image.png")
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Activation key (write-only)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String activationKey;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Reset key (write-only)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String resetKey;


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
