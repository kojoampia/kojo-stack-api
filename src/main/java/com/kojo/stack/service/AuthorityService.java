package com.kojo.stack.service;

import com.kojo.stack.api.dto.AuthorityDTO;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.domain.repository.AuthorityRepository;
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
 * AuthorityService - Business logic for authority/role management
 * Handles CRUD operations for user authorities
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthorityService {

    private final AuthorityRepository repository;

    @Timed
    @Cacheable(value = "authorities")
    public List<AuthorityDTO> getAll() {
        log.info("Fetching all authorities");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Timed
    @Cacheable(value = "authority", key = "#id")
    public AuthorityDTO getById(String id) {
        log.info("Fetching authority with id: {}", id);
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Authority not found: " + id));
    }

    @Timed
    public AuthorityDTO getByName(String name) {
        log.info("Fetching authority with name: {}", name);
        return repository.findByName(name)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Authority not found for name: " + name));
    }

    @Timed
    @Transactional
    @CacheEvict(value = "authorities", allEntries = true)
    public AuthorityDTO create(AuthorityDTO dto) {
        log.info("Creating new authority: {}", dto.getName());
        Authority entity = toEntity(dto);
        Authority saved = repository.save(entity);
        return toDTO(saved);
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"authorities", "authority"}, allEntries = true)
    public AuthorityDTO update(String id, AuthorityDTO dto) {
        log.info("Updating authority with id: {}", id);
        Authority entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Authority not found: " + id));
        
        entity.setName(dto.getName());
        
        return toDTO(repository.save(entity));
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"authorities", "authority"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting authority with id: {}", id);
        repository.deleteById(id);
    }

    // Mapper methods
    private AuthorityDTO toDTO(Authority entity) {
        return AuthorityDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private Authority toEntity(AuthorityDTO dto) {
        return Authority.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
