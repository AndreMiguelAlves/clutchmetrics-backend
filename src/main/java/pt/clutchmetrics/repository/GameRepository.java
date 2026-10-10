package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Game;
import pt.clutchmetrics.entity.GameCode;

import java.util.Optional;

public interface GameRepository
        extends JpaRepository<Game, Long> {

    Optional<Game> findByCode(GameCode code);
}