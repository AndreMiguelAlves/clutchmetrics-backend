package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.PasswordResetToken;
import pt.clutchmetrics.entity.User;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    void deleteByUser(User user);
}