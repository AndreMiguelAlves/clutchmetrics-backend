package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.MatchResult;

import java.time.LocalDateTime;

public class MatchResponse {

    private Long matchId;
    private Long playerGameAccountId;
    private GameCode gameCode;
    private String gameName;
    private String nickname;
    private String externalMatchId;
    private MatchResult result;
    private LocalDateTime playedAt;
    private Integer durationSeconds;

    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer score;

    private double kda;

    public MatchResponse(
            Long matchId,
            Long playerGameAccountId,
            GameCode gameCode,
            String gameName,
            String nickname,
            String externalMatchId,
            MatchResult result,
            LocalDateTime playedAt,
            Integer durationSeconds,
            Integer kills,
            Integer deaths,
            Integer assists,
            Integer score,
            double kda) {

        this.matchId = matchId;
        this.playerGameAccountId = playerGameAccountId;
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.nickname = nickname;
        this.externalMatchId = externalMatchId;
        this.result = result;
        this.playedAt = playedAt;
        this.durationSeconds = durationSeconds;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.score = score;
        this.kda = kda;
    }

    public Long getMatchId() {
        return matchId;
    }

    public Long getPlayerGameAccountId() {
        return playerGameAccountId;
    }

    public GameCode getGameCode() {
        return gameCode;
    }

    public String getGameName() {
        return gameName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getExternalMatchId() {
        return externalMatchId;
    }

    public MatchResult getResult() {
        return result;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
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

    public Integer getScore() {
        return score;
    }

    public double getKda() {
        return kda;
    }
}