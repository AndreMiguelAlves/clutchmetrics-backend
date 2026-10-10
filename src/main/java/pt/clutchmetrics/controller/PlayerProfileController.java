package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.PlayerProfileResponse;
import pt.clutchmetrics.dto.UpdatePlayerProfileRequest;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.service.PlayerProfileService;

@RestController
@RequestMapping("/api/profile")
public class PlayerProfileController {

    private final PlayerProfileService playerProfileService;

    public PlayerProfileController(
            PlayerProfileService playerProfileService) {

        this.playerProfileService = playerProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<PlayerProfileResponse> getMyProfile(
            Authentication authentication) {

        PlayerProfile profile =
                playerProfileService.getMyProfile(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                toResponse(profile)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<PlayerProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdatePlayerProfileRequest request) {

        PlayerProfile profile =
                playerProfileService.updateMyProfile(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(
                toResponse(profile)
        );
    }

    private PlayerProfileResponse toResponse(
            PlayerProfile profile) {

        return new PlayerProfileResponse(
                profile.getId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}