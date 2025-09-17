package com.carloscavalcanti.urlshortner.dto;

import com.carloscavalcanti.urlshortner.model.UrlMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnalyticsResponseDTOMapper {

    @Mapping(target = "totalClicks", expression = "java(urlMapping.getClickCount())")
    @Mapping(target = "recentClicks", expression = "java(urlMapping.getClicks())")
    @Mapping(target = "shortCode", expression = "java(urlMapping.getShortCode())")
    @Mapping(target = "originalUrl", expression = "java(urlMapping.getOriginalUrl())")
    @Mapping(target = "createdAt", expression = "java(urlMapping.getCreatedAt())")
    @Mapping(target = "active", expression = "java(urlMapping.isActive())")
    AnalyticsResponseDTO toDTO(UrlMapping urlMapping);
}
