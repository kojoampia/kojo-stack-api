package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.ProjectDTO;
import com.kojo.stack.domain.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ProjectMapper - Converts between Project and ProjectDTO
 * Uses MapStruct for compile-time safe mapping
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    ProjectDTO toDTO(Project entity);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    Project toEntity(ProjectDTO dto);
}
