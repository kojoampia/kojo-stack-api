package com.kojo.stack.service;

import com.kojo.stack.api.dto.ProjectDTO;
import com.kojo.stack.api.mapper.ProjectMapper;
import com.kojo.stack.domain.model.Project;
import com.kojo.stack.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectService - Business logic for project management
 * Handles CRUD operations and event publishing
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Cacheable(value = "projects")
    public List<ProjectDTO> getAll() {
        log.info("Fetching all projects");
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "activeProjects")
    public List<ProjectDTO> getActive() {
        log.info("Fetching active projects");
        return repository.findByStatus(Project.ProjectStatus.ACTIVE).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getByType(String type) {
        log.info("Fetching projects by type: {}", type);
        try {
            Project.ProjectType projectType = Project.ProjectType.valueOf(type);
            return repository.findByType(projectType).stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid project type: {}", type);
            return List.of();
        }
    }

    public List<ProjectDTO> searchByClient(String client) {
        log.info("Searching projects by client: {}", client);
        return repository.findByClientContainingIgnoreCase(client).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"projects", "activeProjects"}, allEntries = true)
    public ProjectDTO create(ProjectDTO dto) {
        log.info("Creating new project: {}", dto.getName());
        Project entity = mapper.toEntity(dto);
        Project saved = repository.save(entity);
        
        // Publish event for event-driven consumers
        publishProjectCreatedEvent(saved);
        
        return mapper.toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"projects", "activeProjects"}, allEntries = true)
    public ProjectDTO update(String id, ProjectDTO dto) {
        log.info("Updating project with id: {}", id);
        Project entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
        
        entity.setName(dto.getName());
        entity.setClient(dto.getClient());
        entity.setDescription(dto.getDescription());
        entity.setStack(dto.getStack());
        entity.setArchitecture(dto.getArchitecture());
        
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = {"projects", "activeProjects"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting project with id: {}", id);
        repository.deleteById(id);
    }

    private void publishProjectCreatedEvent(Project project) {
        try {
            kafkaTemplate.send("project-events", "project.created", project);
            log.info("Published project.created event for project: {}", project.getId());
        } catch (Exception e) {
            log.warn("Failed to publish event for project: {}", project.getId(), e);
        }
    }
}
