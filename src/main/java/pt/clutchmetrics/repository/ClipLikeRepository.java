package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipLike;
import pt.clutchmetrics.entity.User;

public interface ClipLikeRepository
        extends JpaRepository<ClipLike, Long> {

    boolean existsByClipAndUser(
            Clip clip,
            User user
    );

    void deleteByClipAndUser(
            Clip clip,
            User user
    );

    long countByClip(
            Clip clip
    );
}