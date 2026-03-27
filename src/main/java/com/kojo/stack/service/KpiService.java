package com.kojo.stack.service;

import com.kojo.stack.api.dto.KpiDTO;
import com.kojo.stack.api.mapper.KpiMapper;
import com.kojo.stack.domain.model.Kpi;
import com.kojo.stack.repository.KpiRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * KpiService - Business logic for dashboard KPI card management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KpiService {

    private final KpiRepository repository;
    private final KpiMapper mapper;

    @Cacheable(value = "kpis")
    public List<KpiDTO> getAll() {
        log.info("Fetching all KPIs");
        return repository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public KpiDTO getById(String id) {
        log.info("Fetching KPI with id: {}", id);
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("KPI not found: " + id));
    }

    @Transactional
    @CacheEvict(value = "kpis", allEntries = true)
    public KpiDTO create(KpiDTO dto) {
        log.info("Creating new KPI: {}", dto.getLabel());
        Kpi entity = mapper.toEntity(dto);
        Kpi saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    @CacheEvict(value = "kpis", allEntries = true)
    public KpiDTO update(String id, KpiDTO dto) {
        log.info("Updating KPI with id: {}", id);
        Kpi entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("KPI not found: " + id));

        entity.setLabel(dto.getLabel());
        entity.setValue(dto.getValue());
        entity.setUnit(dto.getUnit());
        entity.setIcon(dto.getIcon());
        entity.setColor(dto.getColor());
        entity.setProgress(dto.getProgress());
        entity.setSubtitle(dto.getSubtitle());
        entity.setSortOrder(dto.getSortOrder());

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "kpis", allEntries = true)
    public void delete(String id) {
        log.info("Deleting KPI with id: {}", id);
        repository.deleteById(id);
    }
}
