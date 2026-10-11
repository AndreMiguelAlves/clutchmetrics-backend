package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "sponsor_name", nullable = false, length = 150)
    private String sponsorName;

    @Column(name = "reward_description", nullable = false, length = 500)
    private String rewardDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_metric", nullable = false, length = 50)
    private ChallengeMetric targetMetric;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChallengeStatus status;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Challenge() {
    }

    public Challenge(
            Game game,
            String title,
            String description,
            String sponsorName,
            String rewardDescription,
            ChallengeMetric targetMetric,
            Integer targetValue,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {

        this.game = game;
        this.title = title;
        this.description = description;
        this.sponsorName = sponsorName;
        this.rewardDescription = rewardDescription;
        this.targetMetric = targetMetric;
        this.targetValue = targetValue;
        this.startsAt = startsAt;
        this.endsAt = endsAt;

        this.status = ChallengeStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public ChallengeMetric getTargetMetric() {
        return targetMetric;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}