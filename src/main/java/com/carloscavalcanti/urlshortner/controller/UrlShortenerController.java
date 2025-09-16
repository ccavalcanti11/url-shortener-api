package com.carloscavalcanti.urlshortner.controller;

import com.carloscavalcanti.urlshortner.dto.AnalyticsResponse;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlRequest;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlResponse;
import com.carloscavalcanti.urlshortner.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "URL Shortener", description = "API for URL shortening and analytics")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/api/shorten")
    @Operation(summary = "Shorten a URL", description = "Create a short URL from a long URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "URL shortened successfully",
                content = @Content(schema = @Schema(implementation = ShortenUrlResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid URL format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request) {

        log.info("Received request to shorten URL: {}", request.getLongUrl());

        try {
            ShortenUrlResponse response = urlShortenerService.shortenUrl(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error shortening URL: {}", request.getLongUrl(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to original URL", description = "Redirect from short code to original URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
        @ApiResponse(responseCode = "404", description = "Short code not found")
    })
    public ResponseEntity<Void> redirectToOriginalUrl(
            @Parameter(description = "Short code identifier") @PathVariable String shortCode,
            HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);

        log.info("Redirect request for short code: {} from IP: {}", shortCode, ipAddress);

        String originalUrl = urlShortenerService.redirectAndTrack(shortCode, userAgent, ipAddress);

        if (originalUrl == null) {
            log.warn("Short code not found: {}", shortCode);
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @GetMapping("/api/analytics/{shortCode}")
    @Operation(summary = "Get URL analytics", description = "Retrieve analytics data for a short URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully",
                content = @Content(schema = @Schema(implementation = AnalyticsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short code not found")
    })
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @Parameter(description = "Short code identifier") @PathVariable String shortCode) {

        log.info("Analytics request for short code: {}", shortCode);

        AnalyticsResponse analytics = urlShortenerService.getAnalytics(shortCode);

        if (analytics == null) {
            log.warn("Analytics not found for short code: {}", shortCode);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(analytics);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
