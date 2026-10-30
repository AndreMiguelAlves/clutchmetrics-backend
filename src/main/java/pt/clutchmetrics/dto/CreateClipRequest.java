package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;
import jakarta.validation.constraints.NotNull;

public class CreateClipRequest {

    @NotNull(message = "Game is required.")
    private GameCode gameCode;

    @NotBlank(message = "Title is required.")
    @Size(max = 150, message = "Title must not exceed 150 characters.")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters.")
    private String description;

    @NotBlank(message = "Video URL is required.")
    @Size(max = 500, message = "Video URL must not exceed 500 characters.")
    private String videoUrl;

    @Size(max = 500, message = "Video URL must not exceed 500 characters.")
    private String thumbnailUrl;

    @Min(value = 0, message = "Duration cannot be negative.")
    private Integer durationSeconds;

    public CreateClipRequest() {
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public void setGameCode(GameCode gameCode) {
        this.gameCode = gameCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}