package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateClipCommentRequest {

    @NotBlank(message = "Comment is required.")
    @Size(
            max = 500,
            message = "Comment must not exceed 500 characters."
    )
    private String content;

    public CreateClipCommentRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}