package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.clutchmetrics.dto.ChatMessageRequest;
import pt.clutchmetrics.dto.ChatMessageResponse;
import pt.clutchmetrics.dto.ChatResponse;

import pt.clutchmetrics.entity.ChatMessage;
import pt.clutchmetrics.entity.ChatSender;
import pt.clutchmetrics.entity.User;

import pt.clutchmetrics.repository.ChatMessageRepository;
import pt.clutchmetrics.repository.UserRepository;

import java.util.List;

@Service
public class ChatbotService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatbotService(
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository) {

        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // ENVIAR MENSAGEM
    // =========================================================

    @Transactional
    public ChatResponse sendMessage(
            String email,
            ChatMessageRequest request) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        String content =
                request.getMessage().trim();

        ChatMessage userMessage =
                new ChatMessage(
                        user,
                        ChatSender.USER,
                        content
                );

        ChatMessage savedUserMessage =
                chatMessageRepository.save(
                        userMessage
                );

        String botReply =
                generateReply(content);

        ChatMessage botMessage =
                new ChatMessage(
                        user,
                        ChatSender.BOT,
                        botReply
                );

        ChatMessage savedBotMessage =
                chatMessageRepository.save(
                        botMessage
                );

        return new ChatResponse(
                toResponse(savedUserMessage),
                toResponse(savedBotMessage)
        );
    }

    // =========================================================
    // HISTÓRICO
    // =========================================================

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        return chatMessageRepository
                .findByUserOrderByCreatedAtAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // RESPOSTAS DO BOT
    // =========================================================

    private String generateReply(String message) {

        String normalized =
                message.toLowerCase();

        if (normalized.contains("estatística")
                || normalized.contains("estatisticas")
                || normalized.contains("dashboard")
                || normalized.contains("kda")) {

            return "Podes consultar as tuas estatísticas, KDA e evolução na área Dashboard.";
        }

        if (normalized.contains("clip")
                || normalized.contains("vídeo")
                || normalized.contains("video")) {

            return "Na área de Clips podes criar, processar, publicar e consultar os teus clips.";
        }

        if (normalized.contains("challenge")
                || normalized.contains("desafio")) {

            return "Na área de Challenges podes aderir a desafios patrocinados e acompanhar o teu progresso.";
        }

        if (normalized.contains("premium")
                || normalized.contains("subscrição")
                || normalized.contains("subscricao")
                || normalized.contains("plano")) {

            return "Podes consultar ou alterar o teu plano na área de Subscrição.";
        }

        if (normalized.contains("password")
                || normalized.contains("palavra-passe")
                || normalized.contains("senha")) {

            return "Podes alterar a tua password nas definições da conta ou utilizar a recuperação de password.";
        }

        if (normalized.contains("conta de jogo")
                || normalized.contains("league")
                || normalized.contains("valorant")
                || normalized.contains("cs2")
                || normalized.contains("rocket league")) {

            return "Podes gerir as tuas contas de jogo na área Game Accounts.";
        }

        if (normalized.contains("olá")
                || normalized.contains("ola")
                || normalized.contains("hello")
                || normalized.contains("boas")) {

            return "Olá! Sou o assistente do ClutchMetrics. Posso ajudar-te com estatísticas, clips, challenges, subscrições e conta.";
        }

        return "Não encontrei uma resposta específica. Posso ajudar-te com estatísticas, clips, challenges, subscrições ou gestão da conta.";
    }

    // =========================================================
    // CONVERSÃO PARA DTO
    // =========================================================

    private ChatMessageResponse toResponse(
            ChatMessage message) {

        return new ChatMessageResponse(
                message.getId(),
                message.getSender(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}