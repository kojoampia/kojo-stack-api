package com.kojo.stack.service;

import com.kojo.stack.api.dto.EducationDTO;
import com.kojo.stack.domain.model.Education;
import com.kojo.stack.repository.EducationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EducationService - Business logic for education management
 * Provides education CRUD and filtering capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository repository;

    @Cacheable(value = "education")
    public List<EducationDTO> getAll() {
        log.info("Fetching all education records");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "educationByType")
    public List<EducationDTO> getByType(String type) {
        log.info("Fetching education by type: {}", type);
        return repository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EducationDTO> getById(String id) {
        log.info("Fetching education by id: {}", id);
        return repository.findById(id)
                .map(this::mapToDTO);
    }

    @Transactional
    @CacheEvict(value = {"education", "educationByType"}, allEntries = true)
    public EducationDTO create(EducationDTO dto) {
        log.info("Creating new education record: {}", dto.getInstitution());
        Education entity = mapToEntity(dto);
        Education saved = repository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"education", "educationByType"}, allEntries = true)
    public Optional<EducationDTO> update(String id, EducationDTO dto) {
        log.info("Updating education record: {}", id);
        return repository.findById(id)
                .map(entity -> {
                    entity.setInstitution(dto.getInstitution());
                    entity.setSubjects(dto.getSubjects());
                    entity.setType(dto.getType());
                    entity.setDuration(dto.getDuration());
                    return mapToDTO(repository.save(entity));
                });
    }

    @Transactional
    @CacheEvict(value = {"education", "educationByType"}, allEntries = true)
    public boolean delete(String id) {
        log.info("Deleting education record: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private EducationDTO mapToDTO(Education entity) {
        return EducationDTO.builder()
                .id(entity.getId())
                .institution(entity.getInstitution())
                .subjects(entity.getSubjects())
                .type(entity.getType())
                .duration(entity.getDuration())
                .build();
    }

    private Education mapToEntity(EducationDTO dto) {
        return Education.builder()
                .id(dto.getId())
                .institution(dto.getInstitution())
                .subjects(dto.getSubjects())
                .type(dto.getType())
                .duration(dto.getDuration())
                .build();
    }
}
