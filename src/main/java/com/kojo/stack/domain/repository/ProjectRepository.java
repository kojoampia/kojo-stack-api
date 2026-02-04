package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Project Repository
 * Data access layer for Project documents in MongoDB
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByStatus(Project.ProjectStatus status);

    List<Project> findByType(Project.ProjectType type);

    List<Project> findByClientContainingIgnoreCase(String client);
}
