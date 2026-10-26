
package pt.clutchmetrics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.clutchmetrics.dto.UserFollowStatsResponse;
import pt.clutchmetrics.service.UserFollowService;
import pt.clutchmetrics.dto.UserFollowPlayerResponse;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(
            UserFollowService userFollowService) {

        this.userFollowService = userFollowService;
    }

    // SEGUIR JOGADOR

    @PostMapping("/{userId}")
    public ResponseEntity<Void> followPlayer(
            Authentication authentication,
            @PathVariable Long userId) {

        userFollowService.followUser(
                authentication.getName(),
                userId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // DEIXAR DE SEGUIR JOGADOR

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollowPlayer(
            Authentication authentication,
            @PathVariable Long userId) {

        userFollowService.unfollowUser(
                authentication.getName(),
                userId
        );

        return ResponseEntity.noContent().build();
    }

    // ESTATÍSTICAS DOS SEGUIDORES

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserFollowStatsResponse> getStats(
            Authentication authentication,
            @PathVariable Long userId) {

        UserFollowStatsResponse response =
                userFollowService.getFollowStats(
                        authentication.getName(),
                        userId
                );

        return ResponseEntity.ok(response);
    }

    // LISTAR SEGUIDORES

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserFollowPlayerResponse>> getFollowers(
            @PathVariable Long userId) {

        List<UserFollowPlayerResponse> followers =
                userFollowService.getFollowers(userId);

        return ResponseEntity.ok(followers);
    }


    // LISTAR JOGADORES SEGUIDOS

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserFollowPlayerResponse>> getFollowing(
            @PathVariable Long userId) {

        List<UserFollowPlayerResponse> following =
                userFollowService.getFollowing(userId);

        return ResponseEntity.ok(following);
    }

}
