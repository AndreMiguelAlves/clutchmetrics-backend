package pt.clutchmetrics.dto;

public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public LoginResponse(
            Long id,
            String username,
            String email,
            String accessToken,
            String refreshToken) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}