package pt.clutchmetrics.dto;

public class ChatResponse {

    private ChatMessageResponse userMessage;
    private ChatMessageResponse botMessage;

    public ChatResponse(
            ChatMessageResponse userMessage,
            ChatMessageResponse botMessage) {

        this.userMessage = userMessage;
        this.botMessage = botMessage;
    }

    public ChatMessageResponse getUserMessage() {
        return userMessage;
    }

    public ChatMessageResponse getBotMessage() {
        return botMessage;
    }
}