package com.carloscavalcanti.urlshortner.model;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "url_mappings")
public class UrlMapping {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String shortCode;
    
    private String originalUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expiresAt;
    
    private Long clickCount = 0L;
    
    private List<ClickInfo> clicks = new ArrayList<>();
    
    private boolean active = true;
    
    public UrlMapping(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0L;
        this.clicks = new ArrayList<>();
        this.active = true;
    }

    // Defensive getter - overrides Lombok's generated getter
    public List<ClickInfo> getClicks() {
        return clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }

    // Defensive setter - overrides Lombok's generated setter
    public void setClicks(List<ClickInfo> clicks) {
        this.clicks = clicks != null ? new ArrayList<>(clicks) : new ArrayList<>();
    }

    public void incrementClickCount() {
        this.clickCount++;
        this.clicks.add(new ClickInfo(LocalDateTime.now()));
    }
}
