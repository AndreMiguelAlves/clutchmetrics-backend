package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import pt.clutchmetrics.dto.DashboardSummaryResponse;
import pt.clutchmetrics.dto.MatchResponse;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.MatchResult;
import pt.clutchmetrics.dto.DashboardEvolutionPointResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import pt.clutchmetrics.dto.DashboardHistoryResponse;
import pt.clutchmetrics.entity.MatchCategory;
import pt.clutchmetrics.dto.DashboardPerformanceInsightsResponse;

@Service
public class DashboardService {

    private final GameMatchService gameMatchService;

    public DashboardService(
            GameMatchService gameMatchService) {

        this.gameMatchService = gameMatchService;
    }

    public DashboardSummaryResponse getSummary(
            String email,
            GameCode gameCode,
            Integer days) {

        List<MatchResponse> matches =
                gameMatchService.getMyMatches(
                        email,
                        gameCode,
                        null
                );

        matches = filterByDays(matches, days);

        long totalMatches = matches.size();

        long wins = matches.stream()
                .filter(match ->
                        match.getResult() == MatchResult.WIN)
                .count();

        long losses = matches.stream()
                .filter(match ->
                        match.getResult() == MatchResult.LOSS)
                .count();

        long draws = matches.stream()
                .filter(match ->
                        match.getResult() == MatchResult.DRAW)
                .count();

        double winRate =
                totalMatches == 0
                        ? 0
                        : (wins * 100.0) / totalMatches;

        double averageKda = matches.stream()
                .mapToDouble(MatchResponse::getKda)
                .average()
                .orElse(0);

        double averageKills = matches.stream()
                .mapToInt(MatchResponse::getKills)
                .average()
                .orElse(0);

        double averageDeaths = matches.stream()
                .mapToInt(MatchResponse::getDeaths)
                .average()
                .orElse(0);

        double averageAssists = matches.stream()
                .mapToInt(MatchResponse::getAssists)
                .average()
                .orElse(0);

        return new DashboardSummaryResponse(
                totalMatches,
                wins,
                losses,
                draws,
                round(winRate),
                round(averageKda),
                round(averageKills),
                round(averageDeaths),
                round(averageAssists)
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public List<DashboardEvolutionPointResponse> getEvolution(
            String email,
            GameCode gameCode,
            int limit,
            Integer days) {

        List<MatchResponse> matches =
                gameMatchService.getMyMatches(
                        email,
                        gameCode,
                        null
                );

        matches = filterByDays(matches, days);

        List<MatchResponse> limitedMatches =
                matches.stream()
                        .limit(limit)
                        .toList();

        List<MatchResponse> chronological =
                new ArrayList<>(limitedMatches);

        Collections.reverse(chronological);

        return chronological.stream()
                .map(match ->
                        new DashboardEvolutionPointResponse(
                                match.getMatchId(),
                                match.getPlayedAt(),
                                match.getGameCode(),
                                match.getResult(),
                                match.getKda(),
                                match.getKills(),
                                match.getDeaths(),
                                match.getAssists()
                        )
                )
                .toList();
    }


    public DashboardHistoryResponse getHistory(
            String email,
            GameCode gameCode,
            Integer days,
            MatchResult result,
            MatchCategory category,
            Double minKda,
            int page,
            int pageSize) {

        // Obter as partidas do utilizador
        List<MatchResponse> matches =
                gameMatchService.getMyMatches(
                        email,
                        gameCode,
                        result
                );

        // Aplicar filtro por período
        matches = filterByDays(matches, days);
        if (category != null) {
            matches = matches.stream()
                    .filter(match -> match.getCategory() == category)
                    .toList();
        }

        // Aplicar filtro por KDA mínimo
        if (minKda != null) {
            matches = matches.stream()
                    .filter(match -> match.getKda() >= minKda)
                    .toList();
        }

        // Garantir valores seguros para a paginação
        int safePage = Math.max(page, 0);
        int safePageSize = Math.min(
                Math.max(pageSize, 1),
                50
        );

        // Número total de partidas após os filtros
        long totalMatches = matches.size();

        // Calcular os limites da página
        long offset = (long) safePage * safePageSize;

        int start = (int) Math.min(
                offset,
                totalMatches
        );

        int end = (int) Math.min(
                offset + safePageSize,
                totalMatches
        );

        // Obter apenas as partidas da página solicitada
        List<MatchResponse> pageMatches =
                matches.subList(start, end);

        // Verificar se existem mais partidas
        boolean hasMore = end < totalMatches;

        return new DashboardHistoryResponse(
                pageMatches,
                totalMatches,
                safePage,
                safePageSize,
                hasMore
        );
    }


    public DashboardPerformanceInsightsResponse getPerformanceInsights(
            String email,
            GameCode gameCode) {

        List<MatchResponse> matches =
                gameMatchService.getMyMatches(
                        email,
                        gameCode,
                        null
                );

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime currentStart = now.minusDays(7);
        LocalDateTime previousStart = now.minusDays(14);

        // Partidas dos últimos 7 dias
        List<MatchResponse> currentMatches = matches.stream()
                .filter(match ->
                        match.getPlayedAt() != null &&
                                !match.getPlayedAt().isBefore(currentStart) &&
                                !match.getPlayedAt().isAfter(now)
                )
                .toList();

        // Partidas dos 7 dias anteriores
        List<MatchResponse> previousMatches = matches.stream()
                .filter(match ->
                        match.getPlayedAt() != null &&
                                !match.getPlayedAt().isBefore(previousStart) &&
                                match.getPlayedAt().isBefore(currentStart)
                )
                .toList();

        DashboardPerformanceInsightsResponse.PeriodStats current =
                calculatePeriodStats(currentMatches);

        DashboardPerformanceInsightsResponse.PeriodStats previous =
                calculatePeriodStats(previousMatches);

        boolean comparable =
                current.totalMatches() > 0 &&
                        previous.totalMatches() > 0;

        Double kdaChange = comparable && previous.averageKda() != 0
                ? round(
                (current.averageKda() - previous.averageKda())
                / previous.averageKda() * 100
        )
                : null;

        Double winRateChange = comparable
                ? round(current.winRate() - previous.winRate())
                : null;

        Double killsChange = comparable && previous.averageKills() != 0
                ? round(
                (current.averageKills() - previous.averageKills())
                / previous.averageKills() * 100
        )
                : null;

        return new DashboardPerformanceInsightsResponse(
                current,
                previous,
                kdaChange,
                winRateChange,
                killsChange
        );
    }


    private DashboardPerformanceInsightsResponse.PeriodStats
    calculatePeriodStats(List<MatchResponse> matches) {

        long totalMatches = matches.size();

        long wins = matches.stream()
                .filter(match -> match.getResult() == MatchResult.WIN)
                .count();

        double winRate = totalMatches == 0
                ? 0
                : (wins * 100.0) / totalMatches;

        double averageKda = matches.stream()
                .mapToDouble(MatchResponse::getKda)
                .average()
                .orElse(0);

        double averageKills = matches.stream()
                .mapToInt(MatchResponse::getKills)
                .average()
                .orElse(0);

        return new DashboardPerformanceInsightsResponse.PeriodStats(
                totalMatches,
                round(averageKda),
                round(winRate),
                round(averageKills)
        );
    }


    private List<MatchResponse> filterByDays(
            List<MatchResponse> matches,
            Integer days) {

        if (days == null || days <= 0) {
            return matches;
        }

        LocalDateTime cutoff =
                LocalDateTime.now().minusDays(days);

        return matches.stream()
                .filter(match ->
                        match.getPlayedAt() != null
                                && !match.getPlayedAt()
                                .isBefore(cutoff)
                )
                .toList();
    }
}