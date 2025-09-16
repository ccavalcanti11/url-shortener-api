package com.carloscavalcanti.urlshortner.service;

import com.carloscavalcanti.urlshortner.dto.AnalyticsResponseDTO;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlRequestDTO;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlResponseDTO;
import com.carloscavalcanti.urlshortner.model.UrlMapping;
import com.carloscavalcanti.urlshortner.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlShortenerService, "baseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(urlShortenerService, "shortCodeLength", 6);
    }

    @Test
    void shouldShortenNewUrl() {
        // Given
        String originalUrl = "https://www.example.com";
        ShortenUrlRequestDTO request = new ShortenUrlRequestDTO();
        request.setLongUrl(originalUrl);

        when(urlMappingRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ShortenUrlResponseDTO response = urlShortenerService.shortenUrl(request);

        // Then
        assertNotNull(response);
        assertEquals(originalUrl, response.getOriginalUrl());
        assertTrue(response.getShortUrl().startsWith("http://localhost:8080/"));
        assertNotNull(response.getShortCode());
        assertEquals(6, response.getShortCode().length());

        verify(urlMappingRepository).save(any(UrlMapping.class));
    }

    @Test
    void shouldReturnExistingShortCodeForDuplicateUrl() {
        // Given
        String originalUrl = "https://www.example.com";
        String existingShortCode = "abc123";
        ShortenUrlRequestDTO request = new ShortenUrlRequestDTO();
        request.setLongUrl(originalUrl);

        UrlMapping existingMapping = new UrlMapping(existingShortCode, originalUrl);
        when(urlMappingRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingMapping));

        // When
        ShortenUrlResponseDTO response = urlShortenerService.shortenUrl(request);

        // Then
        assertNotNull(response);
        assertEquals(originalUrl, response.getOriginalUrl());
        assertEquals(existingShortCode, response.getShortCode());
        assertEquals("http://localhost:8080/" + existingShortCode, response.getShortUrl());

        verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    }

    @Test
    void shouldRedirectAndTrackClick() {
        // Given
        String shortCode = "abc123";
        String originalUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";
        String ipAddress = "192.168.1.1";

        UrlMapping mapping = new UrlMapping(shortCode, originalUrl);
        when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = urlShortenerService.redirectAndTrack(shortCode, userAgent, ipAddress);

        // Then
        assertEquals(originalUrl, result);
        verify(urlMappingRepository).save(any(UrlMapping.class));
    }

    @Test
    void shouldReturnNullForNonExistentShortCode() {
        // Given
        String shortCode = "nonexistent";
        when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        String result = urlShortenerService.redirectAndTrack(shortCode, "userAgent", "ip");

        // Then
        assertNull(result);
        verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    }

    @Test
    void shouldReturnAnalytics() {
        // Given
        String shortCode = "abc123";
        String originalUrl = "https://www.example.com";
        UrlMapping mapping = new UrlMapping(shortCode, originalUrl);
        mapping.setClickCount(5L);

        when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));

        // When
        AnalyticsResponseDTO response = urlShortenerService.getAnalytics(shortCode);

        // Then
        assertNotNull(response);
        assertEquals(shortCode, response.getShortCode());
        assertEquals(originalUrl, response.getOriginalUrl());
        assertEquals(5L, response.getTotalClicks());
        assertTrue(response.isActive());
    }

    @Test
    void shouldReturnNullAnalyticsForNonExistentShortCode() {
        // Given
        String shortCode = "nonexistent";
        when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        AnalyticsResponseDTO response = urlShortenerService.getAnalytics(shortCode);

        // Then
        assertNull(response);
    }
}
