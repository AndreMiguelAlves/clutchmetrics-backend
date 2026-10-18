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