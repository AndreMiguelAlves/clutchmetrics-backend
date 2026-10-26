
package pt.clutchmetrics.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.entity.UserFollow;
import pt.clutchmetrics.repository.UserFollowRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.dto.UserFollowStatsResponse;
import pt.clutchmetrics.dto.UserFollowPlayerResponse;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.repository.PlayerProfileRepository;

import java.util.List;

@Service
public class UserFollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final PlayerProfileRepository playerProfileRepository;


    public UserFollowService(
            UserFollowRepository userFollowRepository,
            UserRepository userRepository,
            PlayerProfileRepository playerProfileRepository) {

        this.userFollowRepository = userFollowRepository;
        this.userRepository = userRepository;
        this.playerProfileRepository = playerProfileRepository;
    }


    // SEGUIR UM JOGADOR

    @Transactional
    public void followUser(String email, Long targetUserId) {

        User follower = findCurrentUser(email);

        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Player not found."
                ));

        if (follower.getId().equals(following.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot follow yourself."
            );
        }

        boolean alreadyFollowing =
                userFollowRepository.existsByFollower_IdAndFollowing_Id(
                        follower.getId(),
                        following.getId()
                );

        if (alreadyFollowing) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already following this player."
            );
        }

        UserFollow userFollow = new UserFollow(
                follower,
                following
        );

        userFollowRepository.save(userFollow);
    }

    // DEIXAR DE SEGUIR UM JOGADOR

    @Transactional
    public void unfollowUser(String email, Long targetUserId) {

        User follower = findCurrentUser(email);

        UserFollow userFollow = userFollowRepository
                .findByFollower_IdAndFollowing_Id(
                        follower.getId(),
                        targetUserId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "You are not following this player."
                ));

        userFollowRepository.delete(userFollow);
    }

    // CONTAR FOLLOWERS

    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {

        return userFollowRepository.countByFollowing_Id(userId);
    }

    // CONTAR FOLLOWING

    @Transactional(readOnly = true)
    public long countFollowing(Long userId) {

        return userFollowRepository.countByFollower_Id(userId);
    }

    // VERIFICAR SE JÁ SEGUE UM JOGADOR

    @Transactional(readOnly = true)
    public boolean isFollowing(String email, Long targetUserId) {

        User user = findCurrentUser(email);

        return userFollowRepository
                .existsByFollower_IdAndFollowing_Id(
                        user.getId(),
                        targetUserId
                );
    }


    @Transactional(readOnly = true)
    public UserFollowStatsResponse getFollowStats(
            String email,
            Long targetUserId) {

        User currentUser = findCurrentUser(email);

        if (!userRepository.existsById(targetUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Player not found."
            );
        }

        boolean ownProfile =
                currentUser.getId().equals(targetUserId);

        boolean following = !ownProfile &&
                userFollowRepository
                        .existsByFollower_IdAndFollowing_Id(
                                currentUser.getId(),
                                targetUserId
                        );

        long followersCount =
                userFollowRepository.countByFollowing_Id(targetUserId);

        long followingCount =
                userFollowRepository.countByFollower_Id(targetUserId);

        return new UserFollowStatsResponse(
                followersCount,
                followingCount,
                following,
                ownProfile
        );
    }


// LISTA DE SEGUIDORES

    @Transactional(readOnly = true)
    public List<UserFollowPlayerResponse> getFollowers(Long userId) {

        ensurePlayerExists(userId);

        return userFollowRepository
                .findByFollowing_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(follow -> toPlayerResponse(follow.getFollower()))
                .toList();
    }

// LISTA DE JOGADORES SEGUIDOS

    @Transactional(readOnly = true)
    public List<UserFollowPlayerResponse> getFollowing(Long userId) {

        ensurePlayerExists(userId);

        return userFollowRepository
                .findByFollower_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(follow -> toPlayerResponse(follow.getFollowing()))
                .toList();
    }

// CONVERTER UTILIZADOR PARA DTO

    private UserFollowPlayerResponse toPlayerResponse(User user) {

        PlayerProfile profile = playerProfileRepository
                .findByUserId(user.getId())
                .orElse(null);

        return new UserFollowPlayerResponse(
                user.getId(),
                user.getUsername(),
                profile != null
                        ? profile.getDisplayName()
                        : user.getUsername(),
                profile != null
                        ? profile.getAvatarUrl()
                        : null
        );
    }

// CONFIRMAR QUE O JOGADOR EXISTE

    private void ensurePlayerExists(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Player not found."
            );
        }
    }


    // IDENTIFICAR O UTILIZADOR AUTENTICADO

    private User findCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found."
                ));
    }
}
