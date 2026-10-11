package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipStatus;
import pt.clutchmetrics.entity.ClipVisibility;
import pt.clutchmetrics.entity.User;

import java.util.List;
import java.util.Optional;

public interface ClipRepository
        extends JpaRepository<Clip, Long> {

    @EntityGraph(attributePaths = {
            "user",
            "game"
    })
    List<Clip> findByUserOrderByCreatedAtDesc(
            User user
    );

    @EntityGraph(attributePaths = {
            "user",
            "game"
    })
    List<Clip> findByVisibilityOrderByCreatedAtDesc(
            ClipVisibility visibility
    );

    @EntityGraph(attributePaths = {
            "user",
            "game"
    })
    Optional<Clip> findByIdAndUser(
            Long id,
            User user
    );

    @EntityGraph(attributePaths = {
            "user",
            "game"
    })
    List<Clip> findByVisibilityAndStatusOrderByCreatedAtDesc(
            ClipVisibility visibility,
            ClipStatus status
    );
}