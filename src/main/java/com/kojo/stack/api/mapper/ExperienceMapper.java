package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.ExperienceDTO;
import com.kojo.stack.domain.model.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ExperienceMapper - Converts between Experience and ExperienceDTO
 * Uses MapStruct for compile-time safe mapping
 */
@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    @Mapping(source = "status", target = "status")
    @Mapping(source = "metrics", target = "metrics")
    ExperienceDTO toDTO(Experience entity);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "metrics", target = "metrics")
    Experience toEntity(ExperienceDTO dto);

    @Mapping(source = "label", target = "label")
    @Mapping(source = "value", target = "value")
    ExperienceDTO.MetricDTO toMetricDTO(Experience.Metric metric);

    @Mapping(source = "label", target = "label")
    @Mapping(source = "value", target = "value")
    Experience.Metric toMetric(ExperienceDTO.MetricDTO metricDTO);
}
