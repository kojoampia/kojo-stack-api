package com.kojo.stack.api.mapper;

import com.kojo.stack.api.dto.InquiryDTO;
import com.kojo.stack.domain.model.Inquiry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * InquiryMapper - Converts between Inquiry and InquiryDTO
 * Uses MapStruct for compile-time safe mapping
 */
@Mapper(componentModel = "spring")
public interface InquiryMapper {

    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    InquiryDTO toDTO(Inquiry entity);

    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    Inquiry toEntity(InquiryDTO dto);
}
