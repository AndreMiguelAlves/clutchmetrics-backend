package pt.clutchmetrics.dto;

import java.time.LocalDateTime;

public class PlayerProfileResponse {

    private Long id;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlayerProfileResponse(
            Long id,
            String displayName,
            String bio,
            String avatarUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}