package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.ChatSender;

import java.time.LocalDateTime;

public class ChatMessageResponse {

    private Long id;
    private ChatSender sender;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessageResponse(
            Long id,
            ChatSender sender,
            String content,
            LocalDateTime createdAt) {

        this.id = id;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public ChatSender getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}