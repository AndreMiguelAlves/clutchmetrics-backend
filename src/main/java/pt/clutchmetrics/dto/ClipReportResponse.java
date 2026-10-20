
package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.ClipReportReason;
import pt.clutchmetrics.entity.ClipReportStatus;

import java.time.LocalDateTime;

public class ClipReportResponse {

    private final Long id;
    private final Long reportedClipId;
    private final String reportedClipTitle;

    private final Long reporterId;
    private final String reporterUsername;

    private final ClipReportReason reason;
    private final ClipReportStatus status;
    private final String details;

    private final String reviewedByUsername;
    private final LocalDateTime createdAt;
    private final LocalDateTime reviewedAt;

    public ClipReportResponse(
            Long id,
            Long reportedClipId,
            String reportedClipTitle,
            Long reporterId,
            String reporterUsername,
            ClipReportReason reason,
            ClipReportStatus status,
            String details,
            String reviewedByUsername,
            LocalDateTime createdAt,
            LocalDateTime reviewedAt
    ) {
        this.id = id;
        this.reportedClipId = reportedClipId;
        this.reportedClipTitle = reportedClipTitle;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
        this.reason = reason;
        this.status = status;
        this.details = details;
        this.reviewedByUsername = reviewedByUsername;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getReportedClipId() {
        return reportedClipId;
    }

    public String getReportedClipTitle() {
        return reportedClipTitle;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public ClipReportReason getReason() {
        return reason;
    }

    public ClipReportStatus getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public String getReviewedByUsername() {
        return reviewedByUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}
