package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.RefreshToken;
import pt.clutchmetrics.entity.User;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser(User user);
}