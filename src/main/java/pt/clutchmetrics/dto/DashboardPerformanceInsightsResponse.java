
package pt.clutchmetrics.dto;

public record DashboardPerformanceInsightsResponse(

        PeriodStats currentPeriod,
        PeriodStats previousPeriod,

        Double kdaChangePercent,
        Double winRateChangePoints,
        Double killsChangePercent

) {

    public record PeriodStats(
            long totalMatches,
            double averageKda,
            double winRate,
            double averageKills
    ) {
    }
}
