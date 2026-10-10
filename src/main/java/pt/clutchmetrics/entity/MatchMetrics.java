package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_metrics")
public class MatchMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private GameMatch match;

    private Integer kills;

    private Integer deaths;

    private Integer assists;

    private Integer score;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MatchMetrics() {
    }

    public MatchMetrics(GameMatch match) {
        this.match = match;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public GameMatch getMatch() {
        return match;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}