package com.kojo.stack.service;

import com.kojo.stack.api.dto.DocDTO;
import com.kojo.stack.api.mapper.DocMapper;
import com.kojo.stack.domain.model.Doc;
import com.kojo.stack.repository.DocRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DocumentationService - Business logic for documentation management
 * Handles tech docs, ADRs, and guides
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DocumentationService {

    private final DocRepository repository;
    private final DocMapper mapper;

    @Cacheable(value = "docs")
    public List<DocDTO> getAll() {
        log.info("Fetching all documentation");
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "docsByType")
    public List<DocDTO> getByType(String type) {
        log.info("Fetching docs by type: {}", type);
        return repository.findByType(type).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocDTO> searchByTitle(String title) {
        log.info("Searching docs by title: {}", title);
        return repository.findByTitleContainingIgnoreCase(title).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocDTO> getByTag(String tag) {
        log.info("Fetching docs by tag: {}", tag);
        return repository.findByTag(tag).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"docs", "docsByType"}, allEntries = true)
    public DocDTO create(DocDTO dto) {
        log.info("Creating new documentation: {}", dto.getTitle());
        Doc entity = mapper.toEntity(dto);
        Doc saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"docs", "docsByType"}, allEntries = true)
    public DocDTO update(String id, DocDTO dto) {
        log.info("Updating documentation with id: {}", id);
        Doc entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documentation not found: " + id));
        
        entity.setTitle(dto.getTitle());
        entity.setType(dto.getType());
        entity.setContent(dto.getContent());
        entity.setTags(dto.getTags());
        
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = {"docs", "docsByType"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting documentation with id: {}", id);
        repository.deleteById(id);
    }
}
