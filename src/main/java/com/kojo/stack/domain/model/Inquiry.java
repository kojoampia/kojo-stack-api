package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Inquiry Document
 * Represents a consulting inquiry from the "Hire Consultant" form in MongoDB
 */
@Document(collection = "inquiries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    private String email;

    private InquiryType type;

    private String message;

    @Builder.Default
    private InquiryStatus status = InquiryStatus.NEW;

    public enum InquiryType {
        BACKEND, FRONTEND, DEVOPS, DATA_ENGINEERING, FULL_STACK, CONSULTING, OTHER
    }

    public enum InquiryStatus {
        NEW, VIEWED, IN_PROGRESS, COMPLETED, REJECTED
    }
}
