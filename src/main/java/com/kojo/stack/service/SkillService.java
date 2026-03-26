package com.kojo.stack.service;

import com.kojo.stack.api.dto.TechSkillDTO;
import com.kojo.stack.api.mapper.ExperienceMapper;
import com.kojo.stack.domain.model.TechSkill;
import com.kojo.stack.repository.SkillRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SkillService - Business logic for skill management
 * Provides skill browsing and filtering capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository repository;

    @Cacheable(value = "skills")
    public List<TechSkillDTO> getAll() {
        log.info("Fetching all skills");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "skillsByCategory")
    public List<TechSkillDTO> getByCategory(String category) {
        log.info("Fetching skills by category: {}", category);
        return repository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TechSkillDTO> getExpertSkills() {
        log.info("Fetching expert-level skills (>= 80)");
        return repository.findByLevelGreaterThanEqualOrderByLevelDesc(80).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"skills", "skillsByCategory"}, allEntries = true)
    public TechSkillDTO create(TechSkillDTO dto) {
        log.info("Creating new skill: {}", dto.getName());
        TechSkill entity = mapToEntity(dto);
        TechSkill saved = repository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"skills", "skillsByCategory"}, allEntries = true)
    public TechSkillDTO update(String id, TechSkillDTO dto) {
        log.info("Updating skill with id: {}", id);
        TechSkill entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + id));

        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setLevel(dto.getLevel());
        entity.setIcon(dto.getIcon());

        return mapToDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = {"skills", "skillsByCategory"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting skill with id: {}", id);
        repository.deleteById(id);
    }

    private TechSkillDTO mapToDTO(TechSkill entity) {
        return TechSkillDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .level(entity.getLevel())
                .icon(entity.getIcon())
                .build();
    }

    private TechSkill mapToEntity(TechSkillDTO dto) {
        return TechSkill.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .level(dto.getLevel())
                .icon(dto.getIcon())
                .build();
    }
}
