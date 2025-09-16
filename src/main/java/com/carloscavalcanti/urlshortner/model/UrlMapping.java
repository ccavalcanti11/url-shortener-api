package com.carloscavalcanti.urlshortner.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    public void incrementClickCount() {
        this.clickCount++;
        this.clicks.add(new ClickInfo(LocalDateTime.now()));
    }
}
