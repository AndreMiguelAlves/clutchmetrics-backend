package pt.clutchmetrics.dto;

import java.time.LocalDateTime;

public class ClipCommentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;

    public ClipCommentResponse(
            Long id,
            Long userId,
            String username,
            String content,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.username = username;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}