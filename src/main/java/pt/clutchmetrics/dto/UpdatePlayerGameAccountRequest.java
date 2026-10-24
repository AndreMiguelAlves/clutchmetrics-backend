package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePlayerGameAccountRequest {

    @NotBlank(message = "Nickname is required.")
    @Size(max = 100)
    private String nickname;

    @Size(max = 150)
    private String externalId;

    @Size(max = 50)
    private String region;

    @Size(max = 50)
    private String platform;

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