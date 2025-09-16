package com.carloscavalcanti.urlshortner.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import com.carloscavalcanti.urlshortner.model.ClickInfo;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalyticsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private List<ClickInfo> recentClicks;
    private boolean active;
}
