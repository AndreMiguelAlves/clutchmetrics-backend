package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.dto.CreateMatchRequest;
import pt.clutchmetrics.dto.MatchResponse;
import pt.clutchmetrics.entity.GameMatch;
import pt.clutchmetrics.entity.MatchMetrics;
import pt.clutchmetrics.entity.PlayerGameAccount;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.exception.PlayerGameAccountNotFoundException;
import pt.clutchmetrics.repository.GameMatchRepository;
import pt.clutchmetrics.repository.MatchMetricsRepository;
import pt.clutchmetrics.repository.PlayerGameAccountRepository;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.MatchResult;
import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GameMatchService {

    private final GameMatchRepository gameMatchRepository;
    private final MatchMetricsRepository matchMetricsRepository;
    private final PlayerGameAccountRepository playerGameAccountRepository;
    private final PlayerProfileService playerProfileService;
    private final ChallengeService challengeService;
    private final AuditService auditService;

    public GameMatchService(
            GameMatchRepository gameMatchRepository,
            MatchMetricsRepository matchMetricsRepository,
            PlayerGameAccountRepository playerGameAccountRepository,
            PlayerProfileService playerProfileService,
            ChallengeService challengeService,
            AuditService auditService) {

        this.gameMatchRepository = gameMatchRepository;
        this.matchMetricsRepository = matchMetricsRepository;
        this.playerGameAccountRepository = playerGameAccountRepository;
        this.playerProfileService = playerProfileService;
        this.challengeService = challengeService;
        this.auditService = auditService;
    }

    @Transactional
    public MatchResponse createMatch(
            String email,
            CreateMatchRequest request) {

        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        PlayerGameAccount gameAccount =
                playerGameAccountRepository
                        .findByIdAndPlayerProfile(
                                request.getPlayerGameAccountId(),
                                profile
                        )
                        .orElseThrow(() ->
                                new PlayerGameAccountNotFoundException(
                                        "The game account does not exist or does not belong to you."
                                ));

        // Verificar se a partida já foi registada nesta conta de jogo
        String externalMatchId = request.getExternalMatchId();

        if (externalMatchId != null && !externalMatchId.isBlank()) {

            externalMatchId = externalMatchId.trim();

            boolean alreadyExists = gameMatchRepository
                    .existsByPlayerGameAccountAndExternalMatchId(
                            gameAccount,
                            externalMatchId
                    );

            if (alreadyExists) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This match is already registered for this game account."
                );
            }
        }

        GameMatch match =
                new GameMatch(
                        gameAccount,
                        request.getResult(),
                        request.getPlayedAt()
                );

        match.setCategory(request.getCategory());

        match.setExternalMatchId(externalMatchId);

        match.setDurationSeconds(
                request.getDurationSeconds()
        );

        GameMatch savedMatch =
                gameMatchRepository.save(match);

        MatchMetrics metrics =
                new MatchMetrics(savedMatch);

        metrics.setKills(request.getKills());
        metrics.setDeaths(request.getDeaths());
        metrics.setAssists(request.getAssists());
        metrics.setScore(request.getScore());

        matchMetricsRepository.save(metrics);

        challengeService.updateProgressForMatch(
                email,
                savedMatch,
                metrics
        );

        auditService.log(
                gameAccount.getPlayerProfile().getUser(),
                AuditAction.MATCH_CREATED,
                "GameMatch",
                savedMatch.getId(),
                "Match created with result: " + savedMatch.getResult() + "."
        );

        return toResponse(
                savedMatch,
                metrics
        );
    }

    private double calculateKda(
            int kills,
            int deaths,
            int assists) {

        int divisor = Math.max(deaths, 1);

        double kda =
                (kills + assists) / (double) divisor;

        return Math.round(kda * 100.0) / 100.0;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMyMatches(
            String email,
            GameCode gameCode,
            MatchResult result) {

        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        return gameMatchRepository
                .findByPlayerGameAccount_PlayerProfileOrderByPlayedAtDesc(profile)
                .stream()

                .filter(match ->
                        gameCode == null ||
                                match.getPlayerGameAccount()
                                        .getGame()
                                        .getCode() == gameCode
                )

                .filter(match ->
                        result == null ||
                                match.getResult() == result
                )

                .map(match -> {

                    MatchMetrics metrics =
                            matchMetricsRepository
                                    .findByMatch(match)
                                    .orElse(null);

                    return toResponse(match, metrics);
                })

                .toList();
    }

    private MatchResponse toResponse(
            GameMatch match,
            MatchMetrics metrics) {

        int kills = metrics != null && metrics.getKills() != null
                ? metrics.getKills()
                : 0;

        int deaths = metrics != null && metrics.getDeaths() != null
                ? metrics.getDeaths()
                : 0;

        int assists = metrics != null && metrics.getAssists() != null
                ? metrics.getAssists()
                : 0;

        Integer score =
                metrics != null
                        ? metrics.getScore()
                        : null;

        double kda =
                calculateKda(
                        kills,
                        deaths,
                        assists
                );

        PlayerGameAccount gameAccount =
                match.getPlayerGameAccount();


        return new MatchResponse(
                match.getId(),
                gameAccount.getId(),
                gameAccount.getGame().getCode(),
                gameAccount.getGame().getName(),
                gameAccount.getNickname(),
                match.getExternalMatchId(),
                match.getResult(),
                match.getCategory(),
                match.getPlayedAt(),
                match.getDurationSeconds(),
                kills,
                deaths,
                assists,
                score,
                kda
        );

    }
}