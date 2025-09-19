package com.carloscavalcanti.urlshortner.service;

import com.carloscavalcanti.urlshortner.dto.*;
import com.carloscavalcanti.urlshortner.model.ClickInfo;
import com.carloscavalcanti.urlshortner.model.UrlMapping;
import com.carloscavalcanti.urlshortner.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;
    private final AnalyticsResponseDTOMapper analyticsMapper;
    private final ShortenUrlResponseDTOMapper shortenMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-code-length:6}")
    private int shortCodeLength;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ShortenUrlResponseDTO shortenUrl(final ShortenUrlRequestDTO request) {
        var originalUrl = request.getLongUrl();

        // Check if URL already exists
        var existingMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent() && existingMapping.get().isActive()) {
            var mapping = existingMapping.get();
            log.info("URL already exists, returning existing short code: {}", mapping.getShortCode());

            return shortenMapper.toDTO(mapping, baseUrl);
        }

        // Generate unique short code
        var shortCode = generateUniqueShortCode();

        // Create and save mapping
        var urlMapping = UrlMapping.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .build();

        urlMappingRepository.save(urlMapping);

        log.info("Created new URL mapping: {} -> {}", shortCode, originalUrl);

        return shortenMapper.toDTO(urlMapping, baseUrl);
    }

    public String redirectAndTrack(final String shortCode,
                                   final String userAgent,
                                   final String ipAddress) {

        Optional<UrlMapping> mappingOpt = urlMappingRepository.findByShortCode(shortCode);

        if (mappingOpt.isEmpty() || !mappingOpt.get().isActive()) {
            log.warn("Short code not found or inactive: {}", shortCode);
            return null;
        }

        var mapping = mappingOpt.get();

        // Track the click
        var clickInfo = ClickInfo.builder()
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .timestamp(LocalDateTime.now())
                .build();

        mapping.getClicks().add(clickInfo);
        mapping.incrementClickCount();

        urlMappingRepository.save(mapping);

        log.info("Redirecting {} to {}, total clicks: {}",
                shortCode, mapping.getOriginalUrl(), mapping.getClickCount()
        );

        return mapping.getOriginalUrl();
    }

    public AnalyticsResponseDTO getAnalytics(final String shortCode) {
        var mappingOpt = urlMappingRepository.findByShortCode(shortCode);

        if (mappingOpt.isEmpty()) {
            log.warn("Analytics requested for non-existent short code: {}", shortCode);
            return null;
        }

        var mapping = mappingOpt.get();

        return analyticsMapper.toDTO(mapping);

//        return new AnalyticsResponseDTO(
//                mapping.getShortCode(),
//                mapping.getOriginalUrl(),
//                mapping.getClickCount(),
//                mapping.getCreatedAt(),
//                mapping.getClicks(),
//                mapping.isActive()
//        );
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomString(shortCodeLength);
        } while (urlMappingRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    private String generateRandomString(final int length) {
        var sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
