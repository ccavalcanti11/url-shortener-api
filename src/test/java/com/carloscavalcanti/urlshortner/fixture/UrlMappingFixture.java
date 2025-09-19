package com.carloscavalcanti.urlshortner.fixture;

import com.carloscavalcanti.urlshortner.model.UrlMapping;

import java.util.ArrayList;
import java.util.Set;

public class UrlMappingFixture {

    private UrlMappingFixture() {}

    public static UrlMapping.UrlMappingBuilder createUrlMapping() {
        return UrlMapping.builder()
                .shortCode("abc123")
                .originalUrl("https://www.example.com")
                .createdAt(java.time.LocalDateTime.now())
                .expiresAt(java.time.LocalDateTime.now().plusDays(30))
                .clickCount(0L)
                .active(true)
                .clicks(new ArrayList<>());
    }
}
