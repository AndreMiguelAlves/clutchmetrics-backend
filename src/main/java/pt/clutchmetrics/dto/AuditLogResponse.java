package pt.clutchmetrics.dto;

import pt.clutchmetrics.audit.AuditAction;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;

    private Long userId;
    private String username;
    private String email;

    private AuditAction action;

    private String entityType;
    private Long entityId;

    private String description;

    private LocalDateTime createdAt;

    public AuditLogResponse(
            Long id,
            Long userId,
            String username,
            String email,
            AuditAction action,
            String entityType,
            Long entityId,
            String description,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}