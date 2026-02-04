package com.kojo.stack.service;

import com.kojo.stack.api.dto.ProfileDTO;
import com.kojo.stack.domain.model.Profile;
import com.kojo.stack.domain.repository.ProfileRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileService - Business logic for user profile management
 * Handles CRUD operations for user profiles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository repository;

    @Timed
    @Cacheable(value = "profiles")
    public List<ProfileDTO> getAll() {
        log.info("Fetching all profiles");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Timed
    @Cacheable(value = "profile", key = "#id")
    public ProfileDTO getById(String id) {
        log.info("Fetching profile with id: {}", id);
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + id));
    }

    @Timed
    public ProfileDTO getByEmail(String email) {
        log.info("Fetching profile with email: {}", email);
        return repository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Profile not found for email: " + email));
    }

    @Timed
    @Transactional
    @CacheEvict(value = "profiles", allEntries = true)
    public ProfileDTO create(ProfileDTO dto) {
        log.info("Creating new profile for: {}", dto.getEmail());
        Profile entity = toEntity(dto);
        Profile saved = repository.save(entity);
        return toDTO(saved);
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"profiles", "profile"}, allEntries = true)
    public ProfileDTO update(String id, ProfileDTO dto) {
        log.info("Updating profile with id: {}", id);
        Profile entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + id));
        
        entity.setName(dto.getName());
        entity.setTitle(dto.getTitle());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setLocation(dto.getLocation());
        entity.setAvatar(dto.getAvatar());
        
        return toDTO(repository.save(entity));
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"profiles", "profile"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting profile with id: {}", id);
        repository.deleteById(id);
    }

    // Mapper methods
    private ProfileDTO toDTO(Profile entity) {
        return ProfileDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .title(entity.getTitle())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .location(entity.getLocation())
                .avatar(entity.getAvatar())
                .build();
    }

    private Profile toEntity(ProfileDTO dto) {
        return Profile.builder()
                .id(dto.getId())
                .name(dto.getName())
                .title(dto.getTitle())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .location(dto.getLocation())
                .avatar(dto.getAvatar())
                .build();
    }
}
