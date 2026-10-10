package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.MatchResult;

import java.time.LocalDateTime;

public class DashboardEvolutionPointResponse {

    private Long matchId;
    private LocalDateTime playedAt;
    private GameCode gameCode;
    private MatchResult result;

    private double kda;
    private Integer kills;
    private Integer deaths;
    private Integer assists;

    public DashboardEvolutionPointResponse(
            Long matchId,
            LocalDateTime playedAt,
            GameCode gameCode,
            MatchResult result,
            double kda,
            Integer kills,
            Integer deaths,
            Integer assists) {

        this.matchId = matchId;
        this.playedAt = playedAt;
        this.gameCode = gameCode;
        this.result = result;
        this.kda = kda;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
    }

    public Long getMatchId() {
        return matchId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public MatchResult getResult() {
        return result;
    }

    public double getKda() {
        return kda;
    }

    public Integer getKills() {
        return kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public Integer getAssists() {
        return assists;
    }
}