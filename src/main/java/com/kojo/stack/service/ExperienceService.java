package com.kojo.stack.service;

import com.kojo.stack.api.dto.ExperienceDTO;
import com.kojo.stack.api.mapper.ExperienceMapper;
import com.kojo.stack.domain.model.Experience;
import com.kojo.stack.repository.ExperienceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ExperienceService - Business logic for experience management
 * Handles CRUD operations and domain-specific queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExperienceService {

    private final ExperienceRepository repository;
    private final ExperienceMapper mapper;

    @Cacheable(value = "experiences")
    public List<ExperienceDTO> getAll() {
        log.info("Fetching all experiences");
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "activeExperiences")
    public List<ExperienceDTO> getActive() {
        log.info("Fetching active experiences");
        return repository.findByStatus(Experience.StatusType.ACTIVE).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ExperienceDTO> searchByCompany(String company) {
        log.info("Searching experiences by company: {}", company);
        return repository.findByCompanyContainingIgnoreCase(company).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"experiences", "activeExperiences"}, allEntries = true)
    public ExperienceDTO create(ExperienceDTO dto) {
        log.info("Creating new experience for company: {}", dto.getCompany());
        Experience entity = mapper.toEntity(dto);
        Experience saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"experiences", "activeExperiences"}, allEntries = true)
    public ExperienceDTO update(String id, ExperienceDTO dto) {
        log.info("Updating experience with id: {}", id);
        Experience entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found: " + id));
        
        entity.setCompany(dto.getCompany());
        entity.setRole(dto.getRole());
        entity.setPeriod(dto.getPeriod());
        entity.setDescription(dto.getDescription());
        entity.setStatus(Experience.StatusType.valueOf(dto.getStatus()));
        entity.setStack(dto.getStack());
        
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = {"experiences", "activeExperiences"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting experience with id: {}", id);
        repository.deleteById(id);
    }
}
