package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class GameMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_game_account_id", nullable = false)
    private PlayerGameAccount playerGameAccount;

    @Column(name = "external_match_id", length = 150)
    private String externalMatchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchResult result;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private MatchCategory category = MatchCategory.UNKNOWN;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public GameMatch() {
    }

    public GameMatch(
            PlayerGameAccount playerGameAccount,
            MatchResult result,
            LocalDateTime playedAt) {

        this.playerGameAccount = playerGameAccount;
        this.result = result;
        this.playedAt = playedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PlayerGameAccount getPlayerGameAccount() {
        return playerGameAccount;
    }

    public String getExternalMatchId() {
        return externalMatchId;
    }

    public void setExternalMatchId(String externalMatchId) {
        this.externalMatchId = externalMatchId;
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        this.result = result;
    }

    public MatchCategory getCategory() {
        return category;
    }

    public void setCategory(MatchCategory category) {
        this.category = category != null
                ? category
                : MatchCategory.UNKNOWN;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}