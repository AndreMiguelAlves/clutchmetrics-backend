package pt.clutchmetrics.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.clutchmetrics.dto.ChatMessageRequest;
import pt.clutchmetrics.dto.ChatMessageResponse;
import pt.clutchmetrics.dto.ChatResponse;

import pt.clutchmetrics.service.ChatbotService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(
            ChatbotService chatbotService) {

        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(
            Authentication authentication,
            @Valid @RequestBody ChatMessageRequest request) {

        return ResponseEntity.ok(
                chatbotService.sendMessage(
                        authentication.getName(),
                        request
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageResponse>>
    getHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                chatbotService.getHistory(
                        authentication.getName()
                )
        );
    }
}