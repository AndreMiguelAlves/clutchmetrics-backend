
package pt.clutchmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clip_reports")
public class ClipReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Clip denunciado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id")
    private Clip clip;

    // Informação preservada caso o clip seja eliminado
    @Column(name = "reported_clip_id", nullable = false)
    private Long reportedClipId;

    @Column(name = "reported_clip_title", nullable = false, length = 150)
    private String reportedClipTitle;

    // Utilizador que fez a denúncia
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // Motivo da denúncia
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ClipReportReason reason;

    // Estado da denúncia
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClipReportStatus status;

    // Explicação adicional (opcional)
    @Column(length = 500)
    private String details;

    // Administrador que analisou a denúncia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public ClipReport() {
    }

    public ClipReport(
            Clip clip,
            User reporter,
            ClipReportReason reason,
            String details) {

        this.clip = clip;
        this.reportedClipId = clip.getId();
        this.reportedClipTitle = clip.getTitle();
        this.reporter = reporter;
        this.reason = reason;
        this.details = details;
        this.status = ClipReportStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = ClipReportStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public Long getReportedClipId() {
        return reportedClipId;
    }

    public String getReportedClipTitle() {
        return reportedClipTitle;
    }

    public User getReporter() {
        return reporter;
    }

    public ClipReportReason getReason() {
        return reason;
    }

    public ClipReportStatus getStatus() {
        return status;
    }

    public void setStatus(ClipReportStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
