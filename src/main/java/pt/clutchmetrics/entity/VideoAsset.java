
package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_assets")
public class VideoAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(
            name = "original_file_name",
            nullable = false,
            unique = true,
            length = 60
    )
    private String originalFileName;


    @Column(name = "uploaded_file_name", length = 255)
    private String uploadedFileName;


    @Column(name = "processed_file_name", length = 60)
    private String processedFileName;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected VideoAsset() {
    }

    public VideoAsset(User user, String originalFileName) {
        this.user = user;
        this.originalFileName = originalFileName;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public String getProcessedFileName() {
        return processedFileName;
    }

    public void setProcessedFileName(String processedFileName) {
        this.processedFileName = processedFileName;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }


}
