
package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreateClipFromVideoRequest {

    @NotNull(message = "O vídeo é obrigatório.")
    @Min(value = 1, message = "O ID do vídeo é inválido.")
    private Long videoAssetId;

    @NotNull(message = "O jogo é obrigatório.")
    private GameCode gameCode;

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 150, message = "O título não pode ultrapassar 150 caracteres.")
    private String title;

    @Size(max = 1000, message = "A descrição não pode ultrapassar 1000 caracteres.")
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
