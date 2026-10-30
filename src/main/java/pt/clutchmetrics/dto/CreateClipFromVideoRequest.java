
package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreateClipFromVideoRequest {

    @NotNull(message = "Video is required.")
    @Min(value = 1, message = "Invalid video ID.")
    private Long videoAssetId;

    @NotNull(message = "Game is required.")
    private GameCode gameCode;

    @NotBlank(message = "Title is required.")
    @Size(max = 150, message = "Title must not exceed 150 characters.")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters.")
    private String description;

    public CreateClipFromVideoRequest() {
    }

    public Long getVideoAssetId() {
        return videoAssetId;
    }

    public void setVideoAssetId(Long videoAssetId) {
        this.videoAssetId = videoAssetId;
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
}
