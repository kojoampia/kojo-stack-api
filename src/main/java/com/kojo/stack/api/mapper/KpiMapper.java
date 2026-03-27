package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.KpiDTO;
import com.kojo.stack.domain.model.Kpi;
import org.mapstruct.Mapper;

/**
 * KpiMapper - Converts between Kpi and KpiDTO
 * Uses MapStruct for compile-time safe mapping
 */
@Mapper(componentModel = "spring")
public interface KpiMapper {

    KpiDTO toDTO(Kpi entity);

    Kpi toEntity(KpiDTO dto);
}
