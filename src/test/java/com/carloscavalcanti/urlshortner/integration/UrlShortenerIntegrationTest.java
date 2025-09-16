package com.carloscavalcanti.urlshortner.integration;

import com.carloscavalcanti.urlshortner.dto.ShortenUrlRequest;
import com.carloscavalcanti.urlshortner.dto.ShortenUrlResponse;
import com.carloscavalcanti.urlshortner.dto.AnalyticsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class UrlShortenerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", () -> mongoDBContainer.getMappedPort(27017));
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    void shouldShortenUrlAndRedirect() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.example.com");

        // When - Shorten URL
        MvcResult result = mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.shortCode").exists())
                .andReturn();

        // Then
        String responseContent = result.getResponse().getContentAsString();
        ShortenUrlResponse response = objectMapper.readValue(responseContent, ShortenUrlResponse.class);

        assertNotNull(response.getShortCode());
        assertEquals("https://www.example.com", response.getOriginalUrl());
        assertTrue(response.getShortUrl().contains(response.getShortCode()));

        // When - Redirect using short code
        mockMvc.perform(get("/" + response.getShortCode()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.example.com"));
    }

    @Test
    void shouldReturnAnalyticsAfterClicks() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");

        // When - Shorten URL
        MvcResult result = mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ShortenUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShortenUrlResponse.class
        );

        // Simulate clicks
        mockMvc.perform(get("/" + response.getShortCode()))
                .andExpect(status().isFound());

        mockMvc.perform(get("/" + response.getShortCode()))
                .andExpect(status().isFound());

        // When - Get Analytics
        MvcResult analyticsResult = mockMvc.perform(get("/api/analytics/" + response.getShortCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value(response.getShortCode()))
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"))
                .andExpect(jsonPath("$.totalClicks").value(2))
                .andReturn();

        // Then
        AnalyticsResponse analytics = objectMapper.readValue(
                analyticsResult.getResponse().getContentAsString(),
                AnalyticsResponse.class
        );

        assertEquals(response.getShortCode(), analytics.getShortCode());
        assertEquals("https://www.google.com", analytics.getOriginalUrl());
        assertEquals(2L, analytics.getTotalClicks());
        assertTrue(analytics.isActive());
    }

    @Test
    void shouldReturn404ForNonExistentShortCode() throws Exception {
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForNonExistentAnalytics() throws Exception {
        mockMvc.perform(get("/api/analytics/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateUrlFormat() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("invalid-url");

        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
