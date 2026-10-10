package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Game;
import pt.clutchmetrics.entity.PlayerGameAccount;
import pt.clutchmetrics.entity.PlayerProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface PlayerGameAccountRepository
        extends JpaRepository<PlayerGameAccount, Long> {

    @EntityGraph(attributePaths = "game")
    List<PlayerGameAccount> findByPlayerProfile(
            PlayerProfile playerProfile
    );

    Optional<PlayerGameAccount> findByPlayerProfileAndGame(
            PlayerProfile playerProfile,
            Game game
    );

    boolean existsByPlayerProfileAndGame(
            PlayerProfile playerProfile,
            Game game
    );
}