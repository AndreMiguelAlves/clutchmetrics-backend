package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.GameMatch;
import pt.clutchmetrics.entity.PlayerGameAccount;
import pt.clutchmetrics.entity.PlayerProfile;

import java.util.List;

public interface GameMatchRepository
        extends JpaRepository<GameMatch, Long> {

    @EntityGraph(attributePaths = {
            "playerGameAccount",
            "playerGameAccount.game"
    })
    List<GameMatch> findByPlayerGameAccountOrderByPlayedAtDesc(
            PlayerGameAccount playerGameAccount
    );

    @EntityGraph(attributePaths = {
            "playerGameAccount",
            "playerGameAccount.game"
    })
    List<GameMatch> findByPlayerGameAccount_PlayerProfileOrderByPlayedAtDesc(
            PlayerProfile playerProfile
    );
}