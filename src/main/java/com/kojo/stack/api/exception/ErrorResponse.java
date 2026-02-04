package com.kojo.stack.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse - Standardized error response structure
 * Used by GlobalExceptionHandler for all API errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "Timestamp of error occurrence")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "500")
    private Integer status;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Validation errors by field (if applicable)")
    private Map<String, String> validationErrors;
}
