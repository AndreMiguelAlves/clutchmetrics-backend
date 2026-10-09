package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {

    @NotBlank(message = "O refresh token é obrigatório")
    private String refreshToken;

    public LogoutRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}