package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.ClipStatus;
import pt.clutchmetrics.entity.ClipVisibility;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.ClipVideoVersion;

import java.time.LocalDateTime;

public class ClipResponse {

    private Long id;

    private Long authorId;
    private String authorUsername;

    private GameCode gameCode;
    private String gameName;

    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;

    private ClipStatus status;
    private ClipVisibility visibility;
    private ClipVideoVersion videoVersion;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;

    public ClipResponse(
            Long id,
            Long authorId,
            String authorUsername,
            GameCode gameCode,
            String gameName,
            String title,
            String description,
            String videoUrl,
            String thumbnailUrl,
            Integer durationSeconds,
            ClipStatus status,
            ClipVisibility visibility,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long likeCount,
            long commentCount,
            boolean likedByCurrentUser) {

        this.id = id;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.durationSeconds = durationSeconds;
        this.status = status;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public String getGameName() {
        return gameName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public ClipStatus getStatus() {
        return status;
    }

    public ClipVisibility getVisibility() {
        return visibility;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public ClipVideoVersion getVideoVersion() {
        return videoVersion;
    }

    public void setVideoVersion(ClipVideoVersion videoVersion) {
        this.videoVersion = videoVersion;
    }
}