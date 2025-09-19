package com.carloscavalcanti.urlshortner.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "url_mappings")
@Builder
@NoArgsConstructor
public class UrlMapping {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String shortCode;
    
    private String originalUrl;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
    
    @Builder.Default
    private Long clickCount = 0L;
    
    @Builder.Default
    private List<ClickInfo> clicks = new ArrayList<>();
    
    @Builder.Default
    private boolean active = true;

    // Custom all-args constructor with defensive copying
    public UrlMapping(String id, String shortCode, String originalUrl,
                     LocalDateTime createdAt, LocalDateTime expiresAt,
                     Long clickCount, List<ClickInfo> clicks, boolean active) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.clickCount = clickCount;
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
        this.active = active;
    }

    // Custom builder class to handle defensive copying
    public static class UrlMappingBuilder {
        public UrlMappingBuilder clicks(List<ClickInfo> clicks) {
            this.clicks$value = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
            this.clicks$set = true;
            return this;
        }
    }

    // Defensive getter - overrides Lombok's generated getter
    public List<ClickInfo> getClicks() {
        return clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }

    // Defensive setter - overrides Lombok's generated setter
    public void setClicks(final List<ClickInfo> clicks) {
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }

    public void incrementClickCount() {
        this.clickCount++;
        this.clicks.add(new ClickInfo(LocalDateTime.now()));
    }
}
