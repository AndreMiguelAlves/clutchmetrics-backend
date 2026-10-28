
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

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

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

    // ENVIAR MENSAGEM

    @Transactional
    public ChatResponse sendMessage(
            String email,
            ChatMessageRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "User not found."
                ));

        String content = request.getMessage().trim();

        ChatMessage savedUserMessage = chatMessageRepository.save(
                new ChatMessage(user, ChatSender.USER, content)
        );

        String botReply = generateReply(content);

        ChatMessage savedBotMessage = chatMessageRepository.save(
                new ChatMessage(user, ChatSender.BOT, botReply)
        );

        return new ChatResponse(
                toResponse(savedUserMessage),
                toResponse(savedBotMessage)
        );
    }

    // HISTORICO

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "User not found."
                ));

        return chatMessageRepository
                .findByUserOrderByCreatedAtAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // RESPOSTAS BASEADAS EM PALAVRAS-CHAVE

    private String generateReply(String message) {

        String text = normalize(message);

        // ACCOUNT DELETION - NOT IMPLEMENTED

        if (hasAny(text,
                "delete account", "delete my account",
                "remove my account", "close my account",
                "account deletion")) {

            return "Account deletion is not available as a self-service "
                    + "feature yet. You can visit the Help Center or "
                    + "Contact Us section for support. "
                    + "Self-service account deletion is planned "
                    + "as a future improvement.";
        }

        // TWO-FACTOR AUTHENTICATION - NOT IMPLEMENTED

        if (hasAny(text,
                "2fa", "two factor", "two step",
                "authenticator", "multi factor", "mfa")) {

            return "ClutchMetrics currently uses password-based "
                    + "authentication with email verification "
                    + "and secure sessions. Two-factor authentication "
                    + "(2FA) is planned as a future security improvement.";
        }

        // PASSWORD RECOVERY

        if (hasAny(text,
                "forgot password", "reset password",
                "recover password", "forgot my password",
                "lost password", "recuperar password")) {

            return "On the Sign In page, click Forgot Password "
                    + "and enter your registered email address. "
                    + "You will receive a password reset link. "
                    + "Follow the instructions to choose a new password.";
        }

        // CHANGE PASSWORD

        if (hasAny(text,
                "change password", "change my password",
                "update password", "password change",
                "alterar password")) {

            return "Open Change Password in the Account section. "
                    + "Enter your current password and your new password. "
                    + "ClutchMetrics will send a confirmation link "
                    + "to your registered email address. "
                    + "Your password changes only after confirmation. "
                    + "You will then need to sign in again.";
        }

        // LOGIN AND REGISTRATION

        if (hasAny(text,
                "register", "registration", "sign up",
                "create account", "create an account",
                "verify email", "email verification")) {

            return "To create a ClutchMetrics account, "
                    + "select Create an Account on the Sign In page. "
                    + "Complete the registration form and confirm "
                    + "your email address using the verification link. "
                    + "You can sign in after verification.";
        }

        if (hasAny(text,
                "login", "log in", "sign in",
                "cant log in", "cannot log in")) {

            return "You can sign in using your registered email "
                    + "and password. If you cannot remember your "
                    + "password, use Forgot Password on the Sign In page. "
                    + "New accounts must verify their email first.";
        }

        if (hasAny(text, "password", "palavra passe", "senha")) {

            return "You can change your password in Account > "
                    + "Change Password, with email confirmation. "
                    + "If you cannot sign in, use Forgot Password "
                    + "on the Sign In page.";
        }

        // CONTENT REPORTING

        if (hasAny(text,
                "report", "report clip", "report video",
                "spam", "inappropriate content",
                "moderation", "denunciar", "denuncia")) {

            return "To report a clip, open the Feed, "
                    + "find the video and use its options menu. "
                    + "Choose Report, select a reason and provide "
                    + "details if needed. Administrators can review "
                    + "reports in the Content Reports section.";
        }

        // DELETE VIDEOS AND CLIPS

        if (hasAny(text, "delete", "remove", "eliminar", "apagar")
                && hasAny(text,
                "clip", "clips", "video", "videos",
                "highlight", "highlights")) {

            return "Open Creator Studio to manage your content. "
                    + "In My Clips, use Delete to remove a clip. "
                    + "In My Videos, you can delete uploaded videos "
                    + "when they are no longer needed. "
                    + "A video cannot be deleted while clips "
                    + "still depend on it. Remove those clips first.";
        }

        // UNPUBLISH

        if (hasAny(text,
                "unpublish", "remove from feed",
                "make clip private", "make video private")) {

            return "Open Creator Studio > My Clips and select Unpublish "
                    + "on the public clip. It will no longer appear "
                    + "in the Feed, but it remains available "
                    + "in your own clip library.";
        }

        // PUBLISH

        if (hasAny(text,
                "publish", "publish clip", "post clip",
                "post video", "share to feed")) {

            return "Open Creator Studio and create a clip from "
                    + "an uploaded or processed video, or add one "
                    + "from a supported direct video URL. "
                    + "Once the clip is ready, use Publish "
                    + "in My Clips to make it visible in the Feed.";
        }

        // EXTERNAL VIDEO URLS

        if (hasAny(text,
                "add from url", "external url",
                "video url", "external video",
                "video link", "direct video link")) {

            return "In Creator Studio > My Clips, select Add from URL. "
                    + "Enter a direct video URL, choose the game "
                    + "and provide the clip details. "
                    + "ClutchMetrics stores the URL and clip information, "
                    + "but does not automatically download the external "
                    + "video file. The original hosting service must "
                    + "remain accessible for playback.";
        }

        // KEEP ORIGINAL

        if (hasAny(text,
                "keep original", "original video",
                "original format")) {

            return "Keep Original creates a clip using your uploaded "
                    + "MP4 without trimming or vertical conversion. "
                    + "The original duration, aspect ratio "
                    + "and resolution are preserved.";
        }

        // TRIM ORIGINAL

        if (hasAny(text,
                "trim original", "trim video",
                "trim a video", "cut video",
                "cut a video")) {

            return "Trim Original lets you select a start and end time "
                    + "to create a shorter clip. "
                    + "It preserves the source video's aspect ratio "
                    + "and resolution instead of converting it "
                    + "to the vertical 9:16 format.";
        }

        // THUMBNAILS

        if (hasAny(text,
                "thumbnail", "thumbnails",
                "cover image", "preview image")) {

            return "ClutchMetrics prioritizes a custom thumbnail "
                    + "when available. Otherwise, it attempts "
                    + "to capture a frame from the video, usually "
                    + "around the two-second mark. "
                    + "If no frame is available, it uses "
                    + "the game's image as a fallback.";
        }

        // FUTURE VIDEO FEATURES

        if (hasAny(text,
                "watermark", "4k", "subtitles",
                "captions", "transitions",
                "video effects", "add music",
                "background music", "automatic highlights",
                "auto highlights")) {

            return "Advanced editing features such as watermarks, "
                    + "music, subtitles, effects, transitions, "
                    + "4K export and automatic highlight detection "
                    + "are not fully implemented yet. "
                    + "They are possible future improvements "
                    + "to Creator Studio.";
        }

        // VIDEO PROCESSING AND EXPORT

        if (hasAny(text,
                "process video", "video processing",
                "vertical video", "vertical export",
                "9 16", "720p", "1080p",
                "shorts", "reels")) {

            return "In Creator Studio > Video Editor, upload an MP4 "
                    + "and select the desired start and end time. "
                    + "Process Video creates a vertical 9:16 highlight "
                    + "using FFmpeg. Free users can process videos "
                    + "at 720 x 1280, while Premium users can process "
                    + "at 1080 x 1920. The uploaded original "
                    + "is preserved.";
        }

        // VIDEO LIBRARY AND CREATOR STUDIO

        if (hasAny(text,
                "creator studio", "my videos", "my clips",
                "upload", "uploaded videos",
                "clip", "clips", "highlight",
                "highlights", "video", "videos",
                "export", "download mp4")) {

            return "Creator Studio lets you upload MP4 gameplay videos, "
                    + "preview them and create highlights. "
                    + "Process Video converts selected moments "
                    + "to vertical format. Keep Original preserves "
                    + "the full source video, while Trim Original "
                    + "cuts a selected interval without changing "
                    + "its aspect ratio. My Videos manages uploaded "
                    + "assets, and My Clips manages highlights, "
                    + "exports and Feed publishing.";
        }

        // FEED AND COMMUNITY INTERACTIONS

        if (hasAny(text,
                "feed", "like", "likes",
                "comment", "comments",
                "share clip", "share a clip",
                "share link", "up next")) {

            return "The Feed lets you discover and watch public "
                    + "gaming clips. You can search for content, "
                    + "filter by game, like and comment on videos, "
                    + "share clip links and explore other videos "
                    + "in Up Next. Public clips are managed "
                    + "by their creators in Creator Studio.";
        }

        // DISCOVER PLAYERS AND FOLLOWERS

        if (hasAny(text,
                "discover players", "find players",
                "find a player", "follow",
                "unfollow", "followers", "following",
                "public profile", "player profile")) {

            return "Open Discover Players to search by username "
                    + "or display name. Select View Profile "
                    + "to explore a player's public information "
                    + "and clips. You can also follow or unfollow "
                    + "other players to connect with the community.";
        }

        // GAME ACCOUNTS

        if (hasAny(text,
                "game account", "game accounts",
                "connect game", "disconnect game",
                "reconnect game", "steam id",
                "league of legends", "valorant",
                "counter strike", "cs2")) {

            return "In Game Accounts, you can connect, edit, "
                    + "disconnect and reconnect your gaming accounts. "
                    + "ClutchMetrics supports League of Legends, "
                    + "Valorant and Counter-Strike 2. "
                    + "You can link one account per game. "
                    + "League of Legends and Valorant include "
                    + "region options, while CS2 uses a Steam ID "
                    + "without a fixed account-region field.";
        }

        // PREMIUM PERFORMANCE INSIGHTS

        if (hasAny(text,
                "performance insights",
                "advanced insights",
                "advanced performance",
                "last 7 days vs previous",
                "compare weeks")) {

            return "Performance Insights is a Premium feature "
                    + "in Analytics. It compares the last seven "
                    + "days with the previous seven days, showing "
                    + "changes in average KDA, win rate "
                    + "and average kills.";
        }

        // UNLIMITED ANALYTICS

        if (hasAny(text,
                "all time", "unlimited analytics",
                "full history", "complete history")) {

            return "Unlimited Analytics is a Premium feature. "
                    + "It unlocks the All Time period in Analytics, "
                    + "allowing you to analyze your complete "
                    + "available match history instead of only "
                    + "the shorter supported time periods.";
        }

        // SCORE

        if (hasAny(text,
                "score", "match score", "game score")) {

            return "The Score shown in match history currently "
                    + "comes from the stored match data. "
                    + "It is not an official score calculated "
                    + "through each game's API. "
                    + "Game-specific scoring models are "
                    + "a possible future improvement.";
        }

        // KDA

        if (hasAny(text, "kda", "kills deaths assists")) {

            return "KDA is a performance indicator based on kills, "
                    + "deaths and assists. A commonly used calculation "
                    + "is (kills + assists) / deaths, with special "
                    + "handling when deaths are zero. "
                    + "You can explore KDA trends and filter "
                    + "matches in Analytics.";
        }

        // ANALYTICS

        if (hasAny(text,
                "analytics", "statistics", "stats",
                "win rate", "performance",
                "dashboard", "overview")) {

            return "Overview gives you a summary of performance, "
                    + "recent matches and connected games. "
                    + "Analytics provides more detailed statistics, "
                    + "KDA trends and filters by game, period, "
                    + "result, match category and KDA. "
                    + "Premium also unlocks All Time analytics "
                    + "and Performance Insights.";
        }

        // MATCHES

        if (hasAny(text,
                "matches", "match history",
                "match results", "recent matches")) {

            return "The Matches section shows your recorded match "
                    + "history across supported games. "
                    + "You can filter matches by game, "
                    + "result and Competitive or Casual category. "
                    + "Each match includes performance details "
                    + "such as KDA, kills, deaths and assists.";
        }

        // CHALLENGES

        if (hasAny(text,
                "challenge", "challenges",
                "sponsored challenge",
                "rewards", "sponsor")) {

            return "The Challenges section includes active, "
                    + "completed and ended gaming challenges. "
                    + "You can join an active challenge and "
                    + "track your progress toward its objective. "
                    + "The sponsors and physical rewards currently "
                    + "shown are demonstration examples; "
                    + "real commercial partnerships and prize "
                    + "distribution are not integrated yet.";
        }

        // BILLING - NOT IMPLEMENTED

        if (hasAny(text,
                "payment", "payments", "billing",
                "credit card", "stripe",
                "pay for premium")) {

            return "ClutchMetrics currently provides Free and "
                    + "Premium plans for demonstration. "
                    + "Premium is presented at 9.99 euros per month, "
                    + "but real payment processing and recurring "
                    + "billing are not integrated yet.";
        }

        // SUBSCRIPTION

        if (hasAny(text,
                "premium", "subscription",
                "upgrade", "downgrade",
                "free plan", "current plan",
                "switch plan", "membership")) {

            return "Open Subscription to view or change your plan. "
                    + "Free includes core statistics and vertical "
                    + "video processing at 720p. Premium adds "
                    + "All Time analytics, Performance Insights "
                    + "and vertical video processing at 1080p. "
                    + "Plan switching currently works in the "
                    + "demonstration environment without "
                    + "real payments.";
        }

        // ADMIN FEATURES

        if (hasAny(text,
                "user management", "audit logs",
                "content reports", "admin panel",
                "administrator", "admin features",
                "permissions", "user roles")) {

            return "Administrators have access to User Management, "
                    + "Audit Logs and Content Reports. "
                    + "These sections allow authorized admins "
                    + "to manage user roles and account status, "
                    + "review recorded actions and moderate "
                    + "reported clips. Regular users cannot "
                    + "access these administrative features.";
        }

        // PROFILE AND SOCIAL LINKS

        if (hasAny(text,
                "profile", "display name", "avatar",
                "bio", "social links",
                "twitch", "instagram", "tiktok",
                "youtube", "profile picture")) {

            return "In Profile, you can update your display name, "
                    + "bio, social links and avatar. "
                    + "You can also open View Public Profile "
                    + "to see how other players view "
                    + "your ClutchMetrics identity. "
                    + "Use Change Password separately "
                    + "to update your account password.";
        }

        // ABOUT THE PLATFORM

        if (hasAny(text,
                "what is clutchmetrics", "about clutchmetrics",
                "supported games", "features")) {

            return "ClutchMetrics combines gaming performance "
                    + "analytics, match history, video creation "
                    + "and a player community in one platform. "
                    + "It currently supports League of Legends, "
                    + "Valorant and Counter-Strike 2. "
                    + "Visit Why ClutchMetrics or Key Features "
                    + "in the footer to learn more.";
        }

        // GREETINGS

        if (hasAny(text,
                "hi", "hello", "hey",
                "ola", "boas", "good morning",
                "good afternoon")) {

            return "Hi! I'm the ClutchMetrics Assistant. "
                    + "I can help with Analytics, Matches, "
                    + "Game Accounts, Creator Studio, Feed, "
                    + "Challenges, Premium and account settings. "
                    + "What would you like to know?";
        }

        // HELP AND SUPPORT

        if (hasAny(text,
                "help", "support", "contact",
                "faq", "email support")) {

            return "You can ask me about ClutchMetrics features "
                    + "or visit Help Center / FAQ in the footer. "
                    + "For questions I cannot resolve, "
                    + "use Contact Us to reach support.";
        }

        // DEFAULT RESPONSE

        return "I'm not sure how to answer that question yet. "
                + "I can help with Creator Studio, Analytics, "
                + "Game Accounts, Feed, Challenges, Premium "
                + "and account settings. Try asking: "
                + "'How do I process a video?', "
                + "'What does Premium include?', "
                + "'How do I report a clip?' "
                + "or 'How do I change my password?'.";
    }

    // NORMALIZAR TEXTO PARA COMPARAR PALAVRAS-CHAVE

    private String normalize(String value) {

        String withoutAccents = Normalizer
                .normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return withoutAccents
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private boolean hasAny(String text, String... phrases) {

        String paddedText = " " + text + " ";

        for (String phrase : phrases) {
            String normalizedPhrase = normalize(phrase);

            if (paddedText.contains(" " + normalizedPhrase + " ")) {
                return true;
            }
        }

        return false;
    }

    // CONVERTER ENTIDADE PARA DTO

    private ChatMessageResponse toResponse(ChatMessage message) {

        return new ChatMessageResponse(
                message.getId(),
                message.getSender(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
