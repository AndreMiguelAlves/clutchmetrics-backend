package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.PlayerProfileResponse;
import pt.clutchmetrics.dto.UpdatePlayerProfileRequest;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.service.PlayerProfileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.clutchmetrics.service.AvatarStorageService;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/profile")
public class PlayerProfileController {

    private final PlayerProfileService playerProfileService;
    private final AvatarStorageService avatarStorageService;

    public PlayerProfileController(
            PlayerProfileService playerProfileService,
            AvatarStorageService avatarStorageService) {

        this.playerProfileService = playerProfileService;
        this.avatarStorageService = avatarStorageService;
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


    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<PlayerProfileResponse> uploadAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        // Validar e guardar a fotografia
        String fileName = avatarStorageService.saveAvatar(file);

        // Construir o endereço da imagem
        String avatarUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/profile/avatars/")
                .path(fileName)
                .toUriString();

        // Atualizar o avatar do utilizador autenticado
        PlayerProfile profile = playerProfileService.updateMyAvatar(
                authentication.getName(),
                avatarUrl
        );

        return ResponseEntity.ok(toResponse(profile));
    }


    @GetMapping("/avatars/{fileName}")
    public ResponseEntity<Resource> getAvatar(
            @PathVariable String fileName) {

        Path avatarPath = avatarStorageService
                .getAvatarPath(fileName);

        MediaType mediaType;

        if (fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".webp")) {
            mediaType = MediaType.parseMediaType("image/webp");
        } else {
            mediaType = MediaType.IMAGE_JPEG;
        }

        Resource resource = new FileSystemResource(avatarPath);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }


    @DeleteMapping("/me/avatar")
    public ResponseEntity<PlayerProfileResponse> removeAvatar(
            Authentication authentication) {

        PlayerProfile profile = playerProfileService.updateMyAvatar(
                authentication.getName(),
                null
        );

        return ResponseEntity.ok(toResponse(profile));
    }


    private PlayerProfileResponse toResponse(
            PlayerProfile profile) {


        return new PlayerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getYoutubeUrl(),
                profile.getTwitchUrl(),
                profile.getTiktokUrl(),
                profile.getInstagramUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );

    }
}