package com.carloscavalcanti.urlshortner.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickInfo {

    private LocalDateTime timestamp;
    private String userAgent;
    private String ipAddress;

    public ClickInfo(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
