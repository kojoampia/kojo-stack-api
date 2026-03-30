package com.kojo.stack.service.dto;

import java.io.Serializable;

/**
 * A DTO representing a password reset completion payload.
 */
public class PasswordResetFinishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String newPassword;

    public PasswordResetFinishDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordResetFinishDTO(String key, String newPassword) {
        this.key = key;
        this.newPassword = newPassword;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
