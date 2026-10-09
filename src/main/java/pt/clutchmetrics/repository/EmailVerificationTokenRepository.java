package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.EmailVerificationToken;
import pt.clutchmetrics.entity.User;

import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUser(User user);

    void deleteByUser(User user);
}