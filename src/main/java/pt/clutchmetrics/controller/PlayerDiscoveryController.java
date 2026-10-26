
package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.clutchmetrics.dto.UserFollowPlayerResponse;
import pt.clutchmetrics.service.PlayerDiscoveryService;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerDiscoveryController {

    private final PlayerDiscoveryService playerDiscoveryService;

    public PlayerDiscoveryController(
            PlayerDiscoveryService playerDiscoveryService) {

        this.playerDiscoveryService = playerDiscoveryService;
    }

    @GetMapping("/discover")
    public ResponseEntity<List<UserFollowPlayerResponse>> discoverPlayers(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search) {

        List<UserFollowPlayerResponse> players =
                playerDiscoveryService.searchPlayers(
                        authentication.getName(),
                        search
                );

        return ResponseEntity.ok(players);
    }
}
