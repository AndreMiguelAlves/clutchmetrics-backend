
package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.ClipReportReason;

public class CreateClipReportRequest {

    @NotNull(message = "Report reason is required.")
    private ClipReportReason reason;

    @Size(
            max = 500,
            message = "Report details cannot exceed 500 characters."
    )
    private String details;

    public CreateClipReportRequest() {
    }

    public ClipReportReason getReason() {
        return reason;
    }

    public void setReason(ClipReportReason reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
