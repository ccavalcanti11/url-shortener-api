package com.carloscavalcanti.urlshortner.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickInfo {

    private LocalDateTime timestamp;
    private String userAgent;
    private String ipAddress;

    public ClickInfo(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
