
package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.clutchmetrics.dto.PublicPlayerProfileResponse;
import pt.clutchmetrics.service.PlayerProfileService;

@RestController
@RequestMapping("/api/players")
public class PublicPlayerController {

    private final PlayerProfileService playerProfileService;

    public PublicPlayerController(
            PlayerProfileService playerProfileService
    ) {
        this.playerProfileService = playerProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PublicPlayerProfileResponse> getPublicProfile(
            @PathVariable Long userId
    ) {

        PublicPlayerProfileResponse profile =
                playerProfileService.getPublicProfileByUserId(userId);

        return ResponseEntity.ok(profile);
    }
}
