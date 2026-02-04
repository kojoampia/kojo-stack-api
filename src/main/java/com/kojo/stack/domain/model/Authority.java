package com.kojo.stack.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Authority - MongoDB document for user authority/role
 */
@Document(collection = "authorities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;
}
