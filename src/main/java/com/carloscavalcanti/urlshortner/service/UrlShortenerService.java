package com.carloscavalcanti.urlshortner.service;

import com.carloscavalcanti.urlshortner.dto.AnalyticsResponse;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlRequest;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlResponse;
import com.carloscavalcanti.urlshortner.model.ClickInfo;
import com.carloscavalcanti.urlshortner.model.UrlMapping;
import com.carloscavalcanti.urlshortner.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-code-length:6}")
    private int shortCodeLength;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        String originalUrl = request.getLongUrl();

        // Check if URL already exists
        Optional<UrlMapping> existingMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent() && existingMapping.get().isActive()) {
            UrlMapping mapping = existingMapping.get();
            log.info("URL already exists, returning existing short code: {}", mapping.getShortCode());
            return new ShortenUrlResponse(
                baseUrl + "/" + mapping.getShortCode(),
                mapping.getOriginalUrl(),
                mapping.getShortCode()
            );
        }

        // Generate unique short code
        String shortCode = generateUniqueShortCode();

        // Create and save mapping
        UrlMapping urlMapping = new UrlMapping(shortCode, originalUrl);
        urlMappingRepository.save(urlMapping);

        log.info("Created new URL mapping: {} -> {}", shortCode, originalUrl);

        return new ShortenUrlResponse(
            baseUrl + "/" + shortCode,
            originalUrl,
            shortCode
        );
    }

    @Cacheable(value = "urlMappings", key = "#shortCode")
    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode);
    }

    public String redirectAndTrack(String shortCode, String userAgent, String ipAddress) {
        Optional<UrlMapping> mappingOpt = getOriginalUrl(shortCode);

        if (mappingOpt.isEmpty() || !mappingOpt.get().isActive()) {
            log.warn("Short code not found or inactive: {}", shortCode);
            return null;
        }

        UrlMapping mapping = mappingOpt.get();

        // Track the click
        ClickInfo clickInfo = new ClickInfo(LocalDateTime.now(), userAgent, ipAddress);
        mapping.getClicks().add(clickInfo);
        mapping.incrementClickCount();

        urlMappingRepository.save(mapping);

        log.info("Redirecting {} to {}, total clicks: {}", shortCode, mapping.getOriginalUrl(), mapping.getClickCount());

        return mapping.getOriginalUrl();
    }

    public AnalyticsResponse getAnalytics(String shortCode) {
        Optional<UrlMapping> mappingOpt = urlMappingRepository.findByShortCode(shortCode);

        if (mappingOpt.isEmpty()) {
            log.warn("Analytics requested for non-existent short code: {}", shortCode);
            return null;
        }

        UrlMapping mapping = mappingOpt.get();

        return new AnalyticsResponse(
            mapping.getShortCode(),
            mapping.getOriginalUrl(),
            mapping.getClickCount(),
            mapping.getCreatedAt(),
            mapping.getClicks(),
            mapping.isActive()
        );
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomString(shortCodeLength);
        } while (urlMappingRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
