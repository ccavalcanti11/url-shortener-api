package com.carloscavalcanti.urlshortner.dto;

import lombok.Getter;
import lombok.Setter;
import com.carloscavalcanti.urlshortner.model.ClickInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnalyticsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private List<ClickInfo> recentClicks;
    private boolean active;

    // Constructor with defensive copying
    public AnalyticsResponse(String shortCode, String originalUrl, Long totalClicks,
                           LocalDateTime createdAt, List<ClickInfo> recentClicks, boolean active) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.totalClicks = totalClicks;
        this.createdAt = createdAt;
        this.recentClicks = recentClicks != null ? new ArrayList<>(recentClicks) : new ArrayList<>();
        this.active = active;
    }

    // Defensive getter
    public List<ClickInfo> getRecentClicks() {
        return recentClicks != null ? new ArrayList<>(recentClicks) : new ArrayList<>();
    }

    // Defensive setter
    public void setRecentClicks(List<ClickInfo> recentClicks) {
        this.recentClicks = recentClicks != null ? new ArrayList<>(recentClicks) : new ArrayList<>();
    }
}
