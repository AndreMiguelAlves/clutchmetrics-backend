package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.entity.User;

import java.util.Optional;

public interface PlayerProfileRepository
        extends JpaRepository<PlayerProfile, Long> {

    Optional<PlayerProfile> findByUser(User user);

    Optional<PlayerProfile> findByUserId(Long userId);
}
