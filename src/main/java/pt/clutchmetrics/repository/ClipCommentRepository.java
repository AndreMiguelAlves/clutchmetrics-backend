package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipComment;
import pt.clutchmetrics.entity.User;

import java.util.List;
import java.util.Optional;

public interface ClipCommentRepository
        extends JpaRepository<ClipComment, Long> {

    @EntityGraph(attributePaths = "user")
    List<ClipComment> findByClipOrderByCreatedAtAsc(
            Clip clip
    );

    Optional<ClipComment> findByIdAndUser(
            Long id,
            User user
    );

    long countByClip(
            Clip clip
    );
}