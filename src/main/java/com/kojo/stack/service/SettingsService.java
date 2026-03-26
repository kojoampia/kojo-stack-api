package com.kojo.stack.service;

import com.kojo.stack.api.dto.SettingsDTO;
import com.kojo.stack.domain.model.Settings;
import com.kojo.stack.repository.SettingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SettingsService - Business logic for application settings management
 * Handles CRUD operations for user settings
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SettingsService {

    private final SettingsRepository repository;

    @Cacheable(value = "settings")
    public List<SettingsDTO> getAll() {
        log.info("Fetching all settings");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "settings", key = "#id")
    public SettingsDTO getById(String id) {
        log.info("Fetching settings with id: {}", id);
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Settings not found: " + id));
    }

    @Transactional
    @CacheEvict(value = "settings", allEntries = true)
    public SettingsDTO create(SettingsDTO dto) {
        log.info("Creating new settings");
        Settings entity = toEntity(dto);
        Settings saved = repository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = "settings", allEntries = true)
    public SettingsDTO update(String id, SettingsDTO dto) {
        log.info("Updating settings with id: {}", id);
        Settings entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Settings not found: " + id));
        
        entity.setVerboseLogging(dto.getVerboseLogging());
        entity.setBetaFeatures(dto.getBetaFeatures());
        entity.setTheme(dto.getTheme());
        entity.setLanguage(dto.getLanguage());
        
        return toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "settings", allEntries = true)
    public void delete(String id) {
        log.info("Deleting settings with id: {}", id);
        repository.deleteById(id);
    }

    // Mapper methods
    private SettingsDTO toDTO(Settings entity) {
        return SettingsDTO.builder()
                .id(entity.getId())
                .verboseLogging(entity.getVerboseLogging())
                .betaFeatures(entity.getBetaFeatures())
                .theme(entity.getTheme())
                .language(entity.getLanguage())
                .build();
    }

    private Settings toEntity(SettingsDTO dto) {
        return Settings.builder()
                .id(dto.getId())
                .verboseLogging(dto.getVerboseLogging())
                .betaFeatures(dto.getBetaFeatures())
                .theme(dto.getTheme())
                .language(dto.getLanguage())
                .build();
    }
}
