package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.GameMatch;
import pt.clutchmetrics.entity.MatchMetrics;

import java.util.Optional;

public interface MatchMetricsRepository
        extends JpaRepository<MatchMetrics, Long> {

    Optional<MatchMetrics> findByMatch(GameMatch match);
}