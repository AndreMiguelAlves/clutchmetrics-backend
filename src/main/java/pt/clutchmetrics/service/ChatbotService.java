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

        String normalized = message.trim().toLowerCase();


        // Delete a clip
        if (normalized.contains("delete")
                && (normalized.contains("clip")
                || normalized.contains("video"))) {

            return "To delete a clip, open the Clips section, "
                    + "find your highlight, and click Delete. "
                    + "Confirm the action to permanently remove "
                    + "the clip from your account. "
                    + "This action cannot be undone.";
        }

        // Unpublish a clip
        if (normalized.contains("unpublish")
                || normalized.contains("remove from feed")) {

            return "To remove your clip from the public Feed, "
                    + "go to the Clips section and click Unpublish. "
                    + "Your clip will become private but will "
                    + "remain available in your Clips section.";
        }

        // Publish a clip
        if (normalized.contains("publish")
                && (normalized.contains("clip")
                || normalized.contains("video"))) {

            return "To publish a clip, open the Clips section. "
                    + "Make sure your clip has been processed "
                    + "and has READY status. Then click Publish "
                    + "to share it with the ClutchMetrics community. "
                    + "Clips removed by moderation cannot be republished.";
        }

        // Process a clip
        if (normalized.contains("process")
                && normalized.contains("clip")) {

            return "After creating a clip, open the Clips section "
                    + "and click Process. This changes the clip "
                    + "from UPLOADED to READY, allowing you to publish it. "
                    + "Automatic video editing is not available yet.";
        }

        // Upgrade to Premium
        if (normalized.contains("upgrade")
                || normalized.contains("get premium")) {

            return "To upgrade your account, open the Subscription "
                    + "section and select the Premium plan. "
                    + "You can also switch back to the Free plan "
                    + "from the same page.";
        }

        // Report inappropriate content
        if (normalized.contains("report")
                && (normalized.contains("inappropriate")
                || normalized.contains("spam"))) {

            return "To report inappropriate content or spam, "
                    + "open the Feed and find the relevant clip. "
                    + "Click Report, select the appropriate reason, "
                    + "and optionally provide more details. "
                    + "Submit your report for administrator review.";
        }


        // Reporting and moderation
        if (normalized.contains("report")
                || normalized.contains("denúncia")
                || normalized.contains("denunciar")
                || normalized.contains("moderation")) {

            return "You can report inappropriate or unrelated content "
                    + "directly from the Feed. Open a clip, click Report, "
                    + "select a reason, and submit your report. "
                    + "Our moderation team will review it.";
        }

        // Player statistics
        if (normalized.contains("stat")
                || normalized.contains("dashboard")
                || normalized.contains("kda")
                || normalized.contains("analytics")
                || normalized.contains("estatística")) {

            return "You can explore your gaming statistics and "
                    + "performance metrics in the Overview and Analytics "
                    + "sections. ClutchMetrics helps you track your "
                    + "progress and review your matches.";
        }

        // Clips and highlights
        if (normalized.contains("clip")
                || normalized.contains("highlight")
                || normalized.contains("video")
                || normalized.contains("vídeo")
                || normalized.contains("upload")
                || normalized.contains("export")) {

            return "You can manage your highlights in the Clips section. "
                    + "Create a clip by selecting a game and providing "
                    + "a title and video URL. You can then process, "
                    + "publish, or unpublish it. You can also download "
                    + "the original MP4 when a local video file is available. "
                    + "Vertical video export is coming soon!";
        }

        // Challenges
        if (normalized.contains("challenge")
                || normalized.contains("desafio")) {

            return "Visit the Challenges section to explore "
                    + "gaming challenges and available activities "
                    + "within ClutchMetrics.";
        }

        // Subscriptions
        if (normalized.contains("premium")
                || normalized.contains("subscription")
                || normalized.contains("plan")
                || normalized.contains("subscrição")) {

            return "ClutchMetrics offers Free and Premium subscription "
                    + "plans. You can check your current plan and "
                    + "manage your subscription in the Subscription section.";
        }

        // Password management
        if (normalized.contains("password")
                || normalized.contains("senha")
                || normalized.contains("palavra-passe")) {

            return "You can update your password in the Change Password "
                    + "section. If you cannot access your account, "
                    + "use the Forgot Password option on the login page.";
        }

        // Game accounts
        if (normalized.contains("game account")
                || normalized.contains("valorant")
                || normalized.contains("league of legends")
                || normalized.contains("rocket league")
                || normalized.contains("cs2")) {

            return "You can manage your linked gaming accounts in "
                    + "the Game Accounts section. ClutchMetrics supports "
                    + "League of Legends, Valorant, Counter-Strike 2, "
                    + "and Rocket League.";
        }

        // User account
        if (normalized.contains("account")
                || normalized.contains("profile")
                || normalized.contains("conta")
                || normalized.contains("perfil")) {

            return "You can view your profile in the Profile section "
                    + "and manage your account using the available "
                    + "account settings.";
        }

        // Greetings
        if (normalized.equals("hi")
                || normalized.startsWith("hi ")
                || normalized.contains("hello")
                || normalized.contains("hey")
                || normalized.contains("olá")
                || normalized.contains("ola")
                || normalized.contains("boas")) {

            return "Hi! I'm the ClutchMetrics Assistant. "
                    + "I can help you with player statistics, clips, "
                    + "challenges, subscriptions, and account management. "
                    + "How can I help you today?";
        }

        // General help
        if (normalized.contains("help")
                || normalized.contains("support")
                || normalized.contains("ajuda")) {

            return "I'm here to help! You can ask me about statistics, "
                    + "clips, game accounts, subscriptions, "
                    + "password management, or reporting content.";
        }

        // Default response

        return "I'm not sure how to answer that question yet, "
                + "but I can help you navigate ClutchMetrics! "
                + "Try asking me: "
                + "'How do I publish a clip?', "
                + "'How do I report a video?', "
                + "'How can I change my password?', "
                + "or 'How do I upgrade to Premium?'.";

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