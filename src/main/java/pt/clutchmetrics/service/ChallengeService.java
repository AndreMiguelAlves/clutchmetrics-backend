package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.entity.*;
import pt.clutchmetrics.exception.ChallengeNotFoundException;
import pt.clutchmetrics.exception.DuplicateChallengeParticipationException;
import pt.clutchmetrics.exception.InvalidChallengeStateException;
import pt.clutchmetrics.repository.ChallengeParticipationRepository;
import pt.clutchmetrics.repository.ChallengeRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            ChallengeParticipationRepository participationRepository,
            AuditService auditService,
            UserRepository userRepository) {

        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Challenge> getActiveChallenges() {

        return challengeRepository
                .findByStatusOrderByStartsAtDesc(
                        ChallengeStatus.ACTIVE
                );
    }

    @Transactional
    public ChallengeParticipation joinChallenge(
            String email,
            Long challengeId) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found."
                        ));

        Challenge challenge = challengeRepository
                .findById(challengeId)
                .orElseThrow(() ->
                        new ChallengeNotFoundException(
                                "Challenge not found."
                        ));

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new InvalidChallengeStateException(
                    "This challenge is not active."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(challenge.getStartsAt())
                || now.isAfter(challenge.getEndsAt())) {

            throw new InvalidChallengeStateException(
                    "This challenge is outside the participation period."
            );
        }

        if (participationRepository
                .existsByChallengeAndUser(
                        challenge,
                        user
                )) {

            throw new DuplicateChallengeParticipationException(
                    "You have already joined this challenge."
            );
        }

        ChallengeParticipation participation =
                new ChallengeParticipation(
                        challenge,
                        user
                );

        ChallengeParticipation savedParticipation =
                participationRepository.save(
                        participation
                );

        auditService.log(
                user,
                AuditAction.CHALLENGE_JOINED,
                "Challenge",
                challenge.getId(),
                "User joined challenge: "
                        + challenge.getTitle()
        );

        return savedParticipation;
    }

    @Transactional(readOnly = true)
    public List<ChallengeParticipation> getMyChallenges(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found."
                        ));

        return participationRepository
                .findByUserOrderByJoinedAtDesc(user);
    }

    @Transactional
    public void updateProgressForMatch(
            String email,
            GameMatch match,
            MatchMetrics metrics) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found."
                        ));

        List<ChallengeParticipation> participations =
                participationRepository
                        .findByUserAndCompletedFalse(user);

        LocalDateTime now = LocalDateTime.now();

        for (ChallengeParticipation participation : participations) {

            Challenge challenge =
                    participation.getChallenge();

            // Challenge tem de estar ativo
            if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
                continue;
            }

            // Challenge ainda tem de estar dentro da data válida
            if (now.isBefore(challenge.getStartsAt())
                    || now.isAfter(challenge.getEndsAt())) {
                continue;
            }

            // A partida deve ter sido disputada durante a participação
            LocalDateTime playedAt = match.getPlayedAt();
            LocalDateTime joinedAt = participation.getJoinedAt();

            if (playedAt == null ||
                    joinedAt == null ||
                    playedAt.isBefore(joinedAt) ||
                    playedAt.isBefore(challenge.getStartsAt()) ||
                    playedAt.isAfter(challenge.getEndsAt()) ||
                    playedAt.isAfter(now)) {

                continue;
            }

            // A partida tem de ser do mesmo jogo do challenge
            if (!challenge.getGame().getId().equals(
                    match.getPlayerGameAccount()
                            .getGame()
                            .getId())) {

                continue;
            }

            int progressToAdd = 0;

            switch (challenge.getTargetMetric()) {

                case WINS -> {

                    if (match.getResult() == MatchResult.WIN) {
                        progressToAdd = 1;
                    }
                }

                case KILLS -> {
                    progressToAdd = metrics.getKills();
                }

                case MATCHES_PLAYED -> {
                    progressToAdd = 1;
                }
            }

            if (progressToAdd <= 0) {
                continue;
            }

            int newProgress =
                    participation.getProgress()
                            + progressToAdd;

            // Não deixamos ultrapassar o objetivo
            newProgress = Math.min(
                    newProgress,
                    challenge.getTargetValue()
            );

            participation.setProgress(
                    newProgress
            );

            if (newProgress >= challenge.getTargetValue()) {

                participation.setCompleted(true);
                participation.setCompletedAt(
                        LocalDateTime.now()
                );

                auditService.log(
                        user,
                        AuditAction.CHALLENGE_COMPLETED,
                        "Challenge",
                        challenge.getId(),
                        "User completed challenge: "
                                + challenge.getTitle()
                );
            }

            participationRepository.save(
                    participation
            );
        }
    }
}