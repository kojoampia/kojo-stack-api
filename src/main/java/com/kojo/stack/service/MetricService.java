package com.kojo.stack.service;

import com.kojo.stack.api.dto.MetricDTO;
import com.kojo.stack.domain.model.Metric;
import com.kojo.stack.repository.MetricRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MetricService - Business logic for metric management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MetricService {

    private final MetricRepository repository;

    @Cacheable(value = "metrics")
    public List<MetricDTO> getAll() {
        log.info("Fetching all metrics");
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "metricsByCategory")
    public List<MetricDTO> getByCategory(String category) {
        log.info("Fetching metrics by category: {}", category);
        return repository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MetricDTO getById(String id) {
        log.info("Fetching metric with id: {}", id);
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Metric not found: " + id));
    }

    @Transactional
    @CacheEvict(value = {"metrics", "metricsByCategory"}, allEntries = true)
    public MetricDTO create(MetricDTO dto) {
        log.info("Creating new metric: {}", dto.getLabel());
        Metric entity = toEntity(dto);
        Metric saved = repository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = {"metrics", "metricsByCategory"}, allEntries = true)
    public MetricDTO update(String id, MetricDTO dto) {
        log.info("Updating metric with id: {}", id);
        Metric entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metric not found: " + id));

        entity.setLabel(dto.getLabel());
        entity.setValue(dto.getValue());
        entity.setTrend(dto.getTrend());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());

        return toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = {"metrics", "metricsByCategory"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting metric with id: {}", id);
        repository.deleteById(id);
    }

    private MetricDTO toDTO(Metric entity) {
        return MetricDTO.builder()
                .id(entity.getId())
                .label(entity.getLabel())
                .value(entity.getValue())
                .trend(entity.getTrend())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .build();
    }

    private Metric toEntity(MetricDTO dto) {
        return Metric.builder()
                .id(dto.getId())
                .label(dto.getLabel())
                .value(dto.getValue())
                .trend(dto.getTrend())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
    }
}
