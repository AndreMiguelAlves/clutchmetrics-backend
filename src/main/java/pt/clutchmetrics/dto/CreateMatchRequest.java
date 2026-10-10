package pt.clutchmetrics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.clutchmetrics.entity.MatchResult;

import java.time.LocalDateTime;

public class CreateMatchRequest {

    @NotNull(message = "A conta de jogo é obrigatória")
    private Long playerGameAccountId;

    @Size(max = 150, message = "O ID externo da partida não pode ultrapassar 150 caracteres")
    private String externalMatchId;

    @NotNull(message = "O resultado da partida é obrigatório")
    private MatchResult result;

    @NotNull(message = "A data da partida é obrigatória")
    private LocalDateTime playedAt;

    @Min(value = 0, message = "A duração não pode ser negativa")
    private Integer durationSeconds;

    @NotNull(message = "Kills é obrigatório")
    @Min(value = 0, message = "Kills não pode ser negativo")
    private Integer kills;

    @NotNull(message = "Deaths é obrigatório")
    @Min(value = 0, message = "Deaths não pode ser negativo")
    private Integer deaths;

    @NotNull(message = "Assists é obrigatório")
    @Min(value = 0, message = "Assists não pode ser negativo")
    private Integer assists;

    @Min(value = 0, message = "O score não pode ser negativo")
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