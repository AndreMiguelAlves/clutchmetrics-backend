package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipLike;
import pt.clutchmetrics.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


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
    @Modifying
    @Query("DELETE FROM ClipLike l WHERE l.clip = :clip")
    void deleteAllByClip(@Param("clip") Clip clip);
}