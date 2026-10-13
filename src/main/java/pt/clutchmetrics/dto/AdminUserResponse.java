package pt.clutchmetrics.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUserResponse {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminUserResponse(
            Long id,
            String username,
            String email,
            boolean enabled,
            List<String> roles,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getRoles() {
        return roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}