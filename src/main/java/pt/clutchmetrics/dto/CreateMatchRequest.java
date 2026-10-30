package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.MatchResult;
import pt.clutchmetrics.entity.MatchCategory;

import java.time.LocalDateTime;

public class CreateMatchRequest {

    @NotNull(message = "Game account is required.")
    private Long playerGameAccountId;

    @Size(max = 150, message = "External match ID must not exceed 150 characters.")
    private String externalMatchId;

    @NotNull(message = "Match result is required.")
    private MatchResult result;

    private MatchCategory category;

    @NotNull(message = "Match date is required.")
    private LocalDateTime playedAt;

    @Min(value = 0, message = "Match duration cannot be negative.")
    private Integer durationSeconds;

    @NotNull(message = "Kills are required.")
    @Min(value = 0, message = "Kills cannot be negative.")
    private Integer kills;

    @NotNull(message = "Deaths are required.")
    @Min(value = 0, message = "Deaths cannot be negative.")
    private Integer deaths;

    @NotNull(message = "Assists are required.")
    @Min(value = 0, message = "Assists cannot be negative.")
    private Integer assists;

    @Min(value = 0, message = "Score cannot be negative.")
    private Integer score;

    public CreateMatchRequest() {
    }

    public Long getPlayerGameAccountId() {
        return playerGameAccountId;
    }

    public void setPlayerGameAccountId(Long playerGameAccountId) {
        this.playerGameAccountId = playerGameAccountId;
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
        this.category = category;
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
}