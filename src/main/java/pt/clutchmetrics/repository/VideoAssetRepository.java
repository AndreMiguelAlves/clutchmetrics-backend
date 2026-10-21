
package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.VideoAsset;
import java.util.List;

import java.util.Optional;

public interface VideoAssetRepository
        extends JpaRepository<VideoAsset, Long> {

    Optional<VideoAsset> findByIdAndUser_Id(
            Long id,
            Long userId
    );

    List<VideoAsset> findByUser_IdOrderByCreatedAtDesc(Long userId);

}
