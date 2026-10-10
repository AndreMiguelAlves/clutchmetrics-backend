package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePlayerProfileRequest {

    @NotBlank(message = "O nome público é obrigatório")
    @Size(min = 3, max = 100, message = "O nome público deve ter entre 3 e 100 caracteres")
    private String displayName;

    @Size(max = 500, message = "A bio não pode ultrapassar 500 caracteres")
    private String bio;

    @Size(max = 500, message = "O URL do avatar não pode ultrapassar 500 caracteres")
    private String avatarUrl;

    public UpdatePlayerProfileRequest() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}