package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreatePlayerGameAccountRequest {

    @NotNull(message = "O jogo é obrigatório")
    private GameCode gameCode;

    @NotBlank(message = "O nickname é obrigatório")
    @Size(max = 100, message = "O nickname não pode ultrapassar 100 caracteres")
    private String nickname;

    @Size(max = 150, message = "O external ID não pode ultrapassar 150 caracteres")
    private String externalId;

    @Size(max = 50, message = "A região não pode ultrapassar 50 caracteres")
    private String region;

    @Size(max = 50, message = "A plataforma não pode ultrapassar 50 caracteres")
    private String platform;

    public CreatePlayerGameAccountRequest() {
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public void setGameCode(GameCode gameCode) {
        this.gameCode = gameCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}