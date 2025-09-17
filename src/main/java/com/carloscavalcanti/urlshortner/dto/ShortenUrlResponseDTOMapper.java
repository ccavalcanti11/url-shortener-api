package com.carloscavalcanti.urlshortner.dto;

import com.carloscavalcanti.urlshortner.model.UrlMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShortenUrlResponseDTOMapper {

    default ShortenUrlResponseDTO toDTO(UrlMapping urlMapping, String baseUrl) {
        if (urlMapping == null) {
            return null;
        }

        return ShortenUrlResponseDTO.builder()
                .shortUrl(baseUrl + "/" + urlMapping.getShortCode())
                .originalUrl(urlMapping.getOriginalUrl())
                .shortCode(urlMapping.getShortCode())
                .build();
    }
}
