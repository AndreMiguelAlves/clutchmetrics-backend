package pt.clutchmetrics.dto;

import java.util.List;

public class AuthMeResponse {

    private String email;
    private List<String> roles;

    public AuthMeResponse(
            String email,
            List<String> roles) {

        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}