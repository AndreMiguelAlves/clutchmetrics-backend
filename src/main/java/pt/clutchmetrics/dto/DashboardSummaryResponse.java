package pt.clutchmetrics.dto;

public class DashboardSummaryResponse {

    private long totalMatches;
    private long wins;
    private long losses;
    private long draws;

    private double winRate;
    private double averageKda;
    private double averageKills;
    private double averageDeaths;
    private double averageAssists;

    public DashboardSummaryResponse(
            long totalMatches,
            long wins,
            long losses,
            long draws,
            double winRate,
            double averageKda,
            double averageKills,
            double averageDeaths,
            double averageAssists) {

        this.totalMatches = totalMatches;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.winRate = winRate;
        this.averageKda = averageKda;
        this.averageKills = averageKills;
        this.averageDeaths = averageDeaths;
        this.averageAssists = averageAssists;
    }

    public long getTotalMatches() {
        return totalMatches;
    }

    public long getWins() {
        return wins;
    }

    public long getLosses() {
        return losses;
    }

    public long getDraws() {
        return draws;
    }

    public double getWinRate() {
        return winRate;
    }

    public double getAverageKda() {
        return averageKda;
    }

    public double getAverageKills() {
        return averageKills;
    }

    public double getAverageDeaths() {
        return averageDeaths;
    }

    public double getAverageAssists() {
        return averageAssists;
    }
}