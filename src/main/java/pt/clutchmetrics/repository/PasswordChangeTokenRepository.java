
package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.PasswordChangeToken;
import pt.clutchmetrics.entity.User;

import java.util.Optional;

public interface PasswordChangeTokenRepository
        extends JpaRepository<PasswordChangeToken, Long> {

    Optional<PasswordChangeToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);
}
