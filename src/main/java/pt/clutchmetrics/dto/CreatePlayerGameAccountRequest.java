package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.GameCode;

public class CreatePlayerGameAccountRequest {

    @NotNull(message = "Game is required.")
    private GameCode gameCode;

    @NotBlank(message = "Nickname is required.")
    @Size(max = 100, message = "Nickname must not exceed 100 characters.")
    private String nickname;

    @Size(max = 150, message = "External ID must not exceed 150 characters.")
    private String externalId;

    @Size(max = 50, message = "Region must not exceed 50 characters.")
    private String region;

    @Size(max = 50, message = "Platform must not exceed 50 characters.")
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