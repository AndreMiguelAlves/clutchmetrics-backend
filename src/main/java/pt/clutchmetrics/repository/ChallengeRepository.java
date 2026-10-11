package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Challenge;
import pt.clutchmetrics.entity.ChallengeStatus;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository
        extends JpaRepository<Challenge, Long> {

    @EntityGraph(attributePaths = "game")
    List<Challenge> findByStatusOrderByStartsAtDesc(
            ChallengeStatus status
    );

    @Override
    @EntityGraph(attributePaths = "game")
    Optional<Challenge> findById(Long id);

    boolean existsByTitle(String title);
}