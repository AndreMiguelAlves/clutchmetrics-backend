
package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.UserFollow;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository
        extends JpaRepository<UserFollow, Long> {

    // Verificar se um utilizador já segue outro
    boolean existsByFollower_IdAndFollowing_Id(
            Long followerId,
            Long followingId
    );

    // Encontrar uma relação de seguimento
    Optional<UserFollow> findByFollower_IdAndFollowing_Id(
            Long followerId,
            Long followingId
    );

    // Contar seguidores de um jogador
    long countByFollowing_Id(Long userId);

    // Contar quantos jogadores um utilizador segue
    long countByFollower_Id(Long userId);

    // Consultar os seguidores de um jogador
    List<UserFollow> findByFollowing_IdOrderByCreatedAtDesc(
            Long userId
    );

    // Consultar os jogadores que um utilizador segue
    List<UserFollow> findByFollower_IdOrderByCreatedAtDesc(
            Long userId
    );
}
