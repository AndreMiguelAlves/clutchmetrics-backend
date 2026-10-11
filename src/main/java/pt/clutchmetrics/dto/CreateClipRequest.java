package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreateClipRequest {

    private GameCode gameCode;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título não pode ultrapassar 150 caracteres")
    private String title;

    @Size(max = 1000, message = "A descrição não pode ultrapassar 1000 caracteres")
    private String description;

    @NotBlank(message = "O URL do vídeo é obrigatório")
    @Size(max = 500, message = "O URL do vídeo não pode ultrapassar 500 caracteres")
    private String videoUrl;

    @Size(max = 500, message = "O URL da thumbnail não pode ultrapassar 500 caracteres")
    private String thumbnailUrl;

    @Min(value = 0, message = "A duração não pode ser negativa")
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