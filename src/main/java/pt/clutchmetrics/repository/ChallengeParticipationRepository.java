package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Challenge;
import pt.clutchmetrics.entity.ChallengeParticipation;
import pt.clutchmetrics.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRepository
        extends JpaRepository<ChallengeParticipation, Long> {

    boolean existsByChallengeAndUser(
            Challenge challenge,
            User user
    );

    @EntityGraph(attributePaths = {
            "challenge",
            "challenge.game"
    })
    List<ChallengeParticipation> findByUserOrderByJoinedAtDesc(
            User user
    );

    @EntityGraph(attributePaths = {
            "challenge",
            "challenge.game"
    })
    Optional<ChallengeParticipation> findByChallengeAndUser(
            Challenge challenge,
            User user
    );

    @EntityGraph(attributePaths = {
            "challenge",
            "challenge.game"
    })
    List<ChallengeParticipation> findByUserAndCompletedFalse(
            User user
    );
}