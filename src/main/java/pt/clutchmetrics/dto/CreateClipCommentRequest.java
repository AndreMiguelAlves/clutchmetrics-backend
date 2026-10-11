package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateClipCommentRequest {

    @NotBlank(message = "O comentário é obrigatório")
    @Size(
            max = 500,
            message = "O comentário não pode ultrapassar 500 caracteres"
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