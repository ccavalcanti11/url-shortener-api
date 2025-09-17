package com.carloscavalcanti.urlshortner.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import com.carloscavalcanti.urlshortner.model.ClickInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class AnalyticsResponseDTO {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    @Singular
    private List<ClickInfo> recentClicks;
    private boolean active;

    // Constructor with defensive copying
    public AnalyticsResponseDTO(final String shortCode,
                                final String originalUrl,
                                final Long totalClicks,
                                final LocalDateTime createdAt,
                                final List<ClickInfo> recentClicks,
                                final boolean active) {

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
    public void setRecentClicks(final List<ClickInfo> recentClicks) {
        this.recentClicks = recentClicks != null ? new ArrayList<>(recentClicks) : new ArrayList<>();
    }
}
