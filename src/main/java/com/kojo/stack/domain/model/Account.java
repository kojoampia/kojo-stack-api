package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

/**
 * Account - MongoDB document for user login credentials and authorities
 */
@Document(collection = "user_logins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String login;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private boolean activated;

    private String langKey;

    private String imageUrl;

    private String activationKey;

    private String resetKey;



    @DBRef
    private Set<Authority> authorities;

    /**
     * Get authorities as a string array of authority names
     */
    public String[] getAuthoritiesAsArray() {
        if (authorities == null || authorities.isEmpty()) {
            return new String[0];
        }
        return authorities.stream()
                .map(Authority::getName)
                .toArray(String[]::new);
    }
}
