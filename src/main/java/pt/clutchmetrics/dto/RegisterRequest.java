package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "O username é obrigatório")
    @Size(min = 3, max = 100, message = "O username deve ter entre 3 e 100 caracteres")
    private String username;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email não é válido")
    private String email;

    @NotBlank(message = "A password é obrigatória")
    @Size(min = 8, max = 72, message = "A password deve ter entre 8 e 72 caracteres")
    private String password;

    public RegisterRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
