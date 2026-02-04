package com.kojo.stack.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

/**
 * InquiryDTO - Data Transfer Object for Inquiry
 * Used for the "Hire Consultant" form submission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Consulting inquiry from potential clients")
public class InquiryDTO implements Serializable {

    @Schema(description = "Unique identifier")
    private String id;

    @NotBlank(message = "Name is required")
    @Schema(description = "Client name", example = "John Doe")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    @Schema(description = "Client email", example = "john@example.com")
    private String email;

    @NotBlank(message = "Type is required")
    @Schema(description = "Inquiry type", example = "BACKEND")
    private String type;

    @Schema(description = "Inquiry message/details")
    private String message;

    @Schema(description = "Inquiry status", example = "NEW")
    private String status;
}
