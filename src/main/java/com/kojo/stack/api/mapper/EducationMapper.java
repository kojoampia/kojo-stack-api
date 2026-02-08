package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.EducationDTO;
import com.kojo.stack.domain.model.Education;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * EducationMapper - MapStruct mapper for Education entity/DTO conversion
 */
@Mapper(componentModel = "spring")
public interface EducationMapper {

    EducationDTO toDTO(Education entity);

    Education toEntity(EducationDTO dto);

    void updateEntityFromDTO(EducationDTO dto, @MappingTarget Education entity);
}
