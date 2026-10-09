package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "O token é obrigatório")
    private String token;

    @NotBlank(message = "A nova password é obrigatória")
    @Size(min = 8, max = 72, message = "A password deve ter entre 8 e 72 caracteres")
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}