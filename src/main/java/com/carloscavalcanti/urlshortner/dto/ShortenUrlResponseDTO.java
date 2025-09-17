package com.carloscavalcanti.urlshortner.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortenUrlResponseDTO {

    private String shortUrl;
    private String originalUrl;
    private String shortCode;
}
