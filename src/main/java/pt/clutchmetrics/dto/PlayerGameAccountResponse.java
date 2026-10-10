package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.GameCode;

import java.time.LocalDateTime;

public class PlayerGameAccountResponse {

    private Long id;
    private GameCode gameCode;
    private String gameName;
    private String nickname;
    private String externalId;
    private String region;
    private String platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlayerGameAccountResponse(
            Long id,
            GameCode gameCode,
            String gameName,
            String nickname,
            String externalId,
            String region,
            String platform,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.nickname = nickname;
        this.externalId = externalId;
        this.region = region;
        this.platform = platform;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public String getGameName() {
        return gameName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getRegion() {
        return region;
    }

    public String getPlatform() {
        return platform;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}