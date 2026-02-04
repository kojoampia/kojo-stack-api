package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Profile - MongoDB document for user profile
 */
@Document(collection = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    private String title;

    private String email;

    private String phone;

    private String location;

    private String avatar;
}
