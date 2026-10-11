package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.ChallengeMetric;
import pt.clutchmetrics.entity.ChallengeStatus;
import pt.clutchmetrics.entity.GameCode;

import java.time.LocalDateTime;

public class ChallengeResponse {

    private Long id;

    private GameCode gameCode;
    private String gameName;

    private String title;
    private String description;

    private String sponsorName;
    private String rewardDescription;

    private ChallengeMetric targetMetric;
    private Integer targetValue;

    private ChallengeStatus status;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public ChallengeResponse(
            Long id,
            GameCode gameCode,
            String gameName,
            String title,
            String description,
            String sponsorName,
            String rewardDescription,
            ChallengeMetric targetMetric,
            Integer targetValue,
            ChallengeStatus status,
            LocalDateTime startsAt,
            LocalDateTime endsAt) {

        this.id = id;
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.title = title;
        this.description = description;
        this.sponsorName = sponsorName;
        this.rewardDescription = rewardDescription;
        this.targetMetric = targetMetric;
        this.targetValue = targetValue;
        this.status = status;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Long getId() {
        return id;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public String getGameName() {
        return gameName;
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

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }
}