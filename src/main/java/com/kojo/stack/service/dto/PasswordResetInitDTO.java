package com.kojo.stack.service.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * A DTO representing a password reset initiation payload.
 */
public class PasswordResetInitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    public PasswordResetInitDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordResetInitDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
