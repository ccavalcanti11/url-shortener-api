package com.carloscavalcanti.urlshortner.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ShortenUrlResponse {

    private String shortUrl;
    private String originalUrl;
    private String shortCode;
}
