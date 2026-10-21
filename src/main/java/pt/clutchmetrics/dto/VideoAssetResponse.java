
package pt.clutchmetrics.dto;

import java.time.LocalDateTime;

public class VideoAssetResponse {

    private final Long id;
    private final boolean processed;
    private final LocalDateTime createdAt;

    public VideoAssetResponse(
            Long id,
            boolean processed,
            LocalDateTime createdAt) {

        this.id = id;
        this.processed = processed;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public boolean isProcessed() {
        return processed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

