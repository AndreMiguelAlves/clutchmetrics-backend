package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "A password atual é obrigatória")
    private String currentPassword;

    @NotBlank(message = "A nova password é obrigatória")
    @Size(
            min = 8,
            max = 72,
            message = "A nova password deve ter entre 8 e 72 caracteres"
    )
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}