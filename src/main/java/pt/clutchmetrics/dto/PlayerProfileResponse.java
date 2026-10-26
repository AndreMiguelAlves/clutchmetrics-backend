package pt.clutchmetrics.dto;

import java.time.LocalDateTime;

public class PlayerProfileResponse {

    private Long id;
    private Long userId;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String youtubeUrl;
    private String twitchUrl;
    private String tiktokUrl;
    private String instagramUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlayerProfileResponse(
            Long id,
            Long userId,
            String displayName,
            String bio,
            String avatarUrl,
            String youtubeUrl,
            String twitchUrl,
            String tiktokUrl,
            String instagramUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.youtubeUrl = youtubeUrl;
        this.twitchUrl = twitchUrl;
        this.tiktokUrl = tiktokUrl;
        this.instagramUrl = instagramUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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


    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getTwitchUrl() {
        return twitchUrl;
    }

    public String getTiktokUrl() {
        return tiktokUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

}