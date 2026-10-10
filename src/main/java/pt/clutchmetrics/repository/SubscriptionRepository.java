package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Subscription;
import pt.clutchmetrics.entity.User;

import java.util.Optional;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    @EntityGraph(attributePaths = "plan")
    Optional<Subscription> findByUser(User user);
}