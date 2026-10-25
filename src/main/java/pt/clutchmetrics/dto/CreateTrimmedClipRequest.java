
package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreateTrimmedClipRequest {

    @NotNull
    private Long videoAssetId;

    @NotNull
    private GameCode gameCode;

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    private Integer startSeconds;

    @NotNull
    @Min(1)
    private Integer endSeconds;

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

    public Integer getStartSeconds() {
        return startSeconds;
    }

    public void setStartSeconds(Integer startSeconds) {
        this.startSeconds = startSeconds;
    }

    public Integer getEndSeconds() {
        return endSeconds;
    }

    public void setEndSeconds(Integer endSeconds) {
        this.endSeconds = endSeconds;
    }
}
