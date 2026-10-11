package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.ChallengeMetric;
import pt.clutchmetrics.entity.GameCode;

import java.time.LocalDateTime;

public class ChallengeParticipationResponse {

    private Long participationId;
    private Long challengeId;

    private String challengeTitle;

    private GameCode gameCode;

    private ChallengeMetric targetMetric;
    private Integer targetValue;

    private Integer progress;
    private boolean completed;

    private LocalDateTime joinedAt;
    private LocalDateTime completedAt;

    public ChallengeParticipationResponse(
            Long participationId,
            Long challengeId,
            String challengeTitle,
            GameCode gameCode,
            ChallengeMetric targetMetric,
            Integer targetValue,
            Integer progress,
            boolean completed,
            LocalDateTime joinedAt,
            LocalDateTime completedAt) {

        this.participationId = participationId;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.gameCode = gameCode;
        this.targetMetric = targetMetric;
        this.targetValue = targetValue;
        this.progress = progress;
        this.completed = completed;
        this.joinedAt = joinedAt;
        this.completedAt = completedAt;
    }

    public Long getParticipationId() {
        return participationId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public ChallengeMetric getTargetMetric() {
        return targetMetric;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public Integer getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}