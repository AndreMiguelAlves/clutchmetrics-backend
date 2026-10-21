
package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProcessVideoRequest {

    @NotNull(message = "Start time is required.")
    @Min(value = 0, message = "Start time cannot be negative.")
    private Integer startSeconds;

    @NotNull(message = "End time is required.")
    @Min(value = 1, message = "End time must be greater than zero.")
    private Integer endSeconds;

    public ProcessVideoRequest() {
    }

    public Integer getStartSeconds() {
        return startSeconds;
    }

    public void setStartSeconds(Integer startSeconds) {
        this.startSeconds = startSeconds;
    }

    public Integer getEndSeconds() {
        return endSeconds;
    }

    public void setEndSeconds(Integer endSeconds) {
        this.endSeconds = endSeconds;
    }
}

