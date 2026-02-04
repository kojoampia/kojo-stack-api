package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.DocDTO;
import com.kojo.stack.domain.model.Doc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * DocMapper - Converts between Doc and DocDTO
 * Uses MapStruct for compile-time safe mapping
 */
@Mapper(componentModel = "spring")
public interface DocMapper {

    DocDTO toDTO(Doc entity);

    Doc toEntity(DocDTO dto);
}
