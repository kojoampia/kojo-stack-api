package com.kojo.stack.service;

import com.kojo.stack.api.dto.TechSkillDTO;
import com.kojo.stack.api.mapper.ExperienceMapper;
import com.kojo.stack.domain.model.TechSkill;
import com.kojo.stack.repository.SkillRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
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

    private TechSkillDTO mapToDTO(TechSkill entity) {
        return TechSkillDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .level(entity.getLevel())
                .icon(entity.getIcon())
                .build();
    }
}
