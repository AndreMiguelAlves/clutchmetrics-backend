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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public ChallengeService(
            ChallengeRepository challengeRepository,
            ChallengeParticipationRepository participationRepository,
            UserRepository userRepository) {

        this.challengeRepository = challengeRepository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
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
                                "Utilizador não encontrado."
                        ));

        Challenge challenge = challengeRepository
                .findById(challengeId)
                .orElseThrow(() ->
                        new ChallengeNotFoundException(
                                "O challenge não existe."
                        ));

        if (challenge.getStatus() != ChallengeStatus.ACTIVE) {
            throw new InvalidChallengeStateException(
                    "Este challenge não está ativo."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(challenge.getStartsAt())
                || now.isAfter(challenge.getEndsAt())) {

            throw new InvalidChallengeStateException(
                    "Este challenge está fora do período de participação."
            );
        }

        if (participationRepository
                .existsByChallengeAndUser(
                        challenge,
                        user
                )) {

            throw new DuplicateChallengeParticipationException(
                    "Já estás a participar neste challenge."
            );
        }

        ChallengeParticipation participation =
                new ChallengeParticipation(
                        challenge,
                        user
                );

        return participationRepository.save(
                participation
        );
    }

    @Transactional(readOnly = true)
    public List<ChallengeParticipation> getMyChallenges(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
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
                                "Utilizador não encontrado."
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
            }

            participationRepository.save(
                    participation
            );
        }
    }
}