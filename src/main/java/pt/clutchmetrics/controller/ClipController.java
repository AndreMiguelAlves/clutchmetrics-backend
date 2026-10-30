package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.ClipCommentResponse;
import pt.clutchmetrics.dto.ClipResponse;
import pt.clutchmetrics.dto.CreateClipCommentRequest;
import pt.clutchmetrics.dto.CreateClipRequest;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.service.ClipService;
import org.springframework.web.bind.annotation.PathVariable;
import pt.clutchmetrics.dto.CreateClipFromVideoRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import pt.clutchmetrics.dto.UpdateClipRequest;
import java.nio.file.Path;
import pt.clutchmetrics.dto.CreateTrimmedClipRequest;

import java.util.List;

@RestController
@RequestMapping("/api/clips")
public class ClipController {

    private final ClipService clipService;

    public ClipController(ClipService clipService) {
        this.clipService = clipService;
    }

    @PostMapping
    public ResponseEntity<ClipResponse> createClip(
            Authentication authentication,
            @Valid @RequestBody CreateClipRequest request) {

        Clip clip = clipService.createClip(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(clip));
    }


    @PostMapping("/from-video")
    public ResponseEntity<ClipResponse> createClipFromVideo(
            Authentication authentication,
            @Valid @RequestBody CreateClipFromVideoRequest request) {

        Clip clip = clipService.createClipFromVideo(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(clip));
    }


    @PostMapping("/from-trimmed")
    public ResponseEntity<ClipResponse> createTrimmedClipFromVideo(
            Authentication authentication,
            @Valid @RequestBody CreateTrimmedClipRequest request) {

        Clip clip = clipService.createTrimmedClipFromVideo(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(clip));
    }


    @PostMapping("/from-original")
    public ResponseEntity<ClipResponse> createClipFromOriginal(
            Authentication authentication,
            @Valid @RequestBody CreateClipFromVideoRequest request) {

        // Confirmar que existe um utilizador autenticado
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Criar o clip utilizando o MP4 original
        Clip clip = clipService.createOriginalClipFromVideo(
                authentication.getName(),
                request
        );

        // Devolver o clip criado
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(clip));
    }

    // =========================================================
// EDITAR TÍTULO E DESCRIÇÃO DO CLIP
// =========================================================

    @PutMapping("/{clipId}")
    public ResponseEntity<ClipResponse> updateClip(
            Authentication authentication,
            @PathVariable Long clipId,
            @Valid @RequestBody UpdateClipRequest request) {

        Clip clip = clipService.updateClip(
                authentication.getName(),
                clipId,
                request
        );

        return ResponseEntity.ok(toResponse(clip));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ClipResponse>> getMyClips(
            Authentication authentication) {

        List<ClipResponse> response = clipService
                .getMyClips(authentication.getName())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{clipId}/process")
    public ResponseEntity<ClipResponse> processClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        Clip clip = clipService.processClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                toResponse(clip)
        );
    }

    @PostMapping("/{clipId}/publish")
    public ResponseEntity<ClipResponse> publishClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        Clip clip = clipService.publishClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                toResponse(clip)
        );
    }

    @PostMapping("/{clipId}/unpublish")
    public ResponseEntity<ClipResponse> unpublishClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        Clip clip = clipService.unpublishClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                toResponse(clip)
        );
    }

    @GetMapping("/feed")
    public ResponseEntity<List<ClipResponse>> getFeed(
            Authentication authentication) {

        List<ClipResponse> response = clipService
                .getFeed()
                .stream()
                .map(clip ->
                        toFeedResponse(
                                clip,
                                authentication.getName()
                        )
                )
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/players/{userId}")
    public ResponseEntity<List<ClipResponse>> getPublicClipsByPlayer(
            @PathVariable Long userId
    ) {

        List<ClipResponse> response = clipService
                .getPublicClipsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{clipId}/like")
    public ResponseEntity<String> likeClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        clipService.likeClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                "Clip liked successfully."
        );
    }

    @DeleteMapping("/{clipId}/like")
    public ResponseEntity<String> unlikeClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        clipService.unlikeClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                "Clip unliked successfully."
        );
    }


    @DeleteMapping("/{clipId}")
    public ResponseEntity<Void> deleteClip(
            Authentication authentication,
            @PathVariable Long clipId
    ) {

        clipService.deleteClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{clipId}/comments")
    public ResponseEntity<ClipCommentResponse> addComment(
            Authentication authentication,
            @PathVariable Long clipId,
            @Valid @RequestBody CreateClipCommentRequest request) {

        ClipCommentResponse response =
                clipService.addComment(
                        authentication.getName(),
                        clipId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{clipId}/comments")
    public ResponseEntity<List<ClipCommentResponse>> getComments(
            @PathVariable Long clipId) {

        return ResponseEntity.ok(
                clipService.getComments(clipId)
        );
    }

    @DeleteMapping("/{clipId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            Authentication authentication,
            @PathVariable Long clipId,
            @PathVariable Long commentId) {

        clipService.deleteComment(
                authentication.getName(),
                commentId
        );

        return ResponseEntity.ok(
                "Comment deleted successfully."
        );
    }


    @GetMapping("/{clipId}/video")
    public ResponseEntity<Resource> streamClipVideo(
            Authentication authentication,
            @PathVariable Long clipId) {

        // 1. Identificar o utilizador, caso esteja autenticado
        String viewerEmail = null;

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            viewerEmail = authentication.getName();
        }

        // 2. Verificar as permissões e localizar o vídeo
        Path videoPath = clipService.getClipVideoPath(
                clipId,
                viewerEmail
        );

        // 3. Preparar o ficheiro para reprodução
        Resource resource = new FileSystemResource(videoPath);

        // 4. Devolver o MP4 para o leitor de vídeo
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(resource);
    }


    private ClipResponse toResponse(Clip clip) {

        ClipResponse response = new ClipResponse(
                clip.getId(),

                clip.getUser().getId(),
                clip.getUser().getUsername(),

                clip.getGame() != null
                        ? clip.getGame().getCode()
                        : null,

                clip.getGame() != null
                        ? clip.getGame().getName()
                        : null,

                clip.getTitle(),
                clip.getDescription(),
                clip.getVideoUrl(),
                clip.getThumbnailUrl(),
                clip.getDurationSeconds(),
                clip.getStatus(),
                clip.getVisibility(),
                clip.getCreatedAt(),
                clip.getUpdatedAt(),

                clipService.countLikes(clip),
                clipService.countComments(clip),
                false
        );

        response.setVideoVersion(clip.getVideoVersion());

        return response;
    }

    private ClipResponse toFeedResponse(
            Clip clip,
            String email) {

        ClipResponse response = new ClipResponse(
                clip.getId(),
                clip.getUser().getId(),
                clip.getUser().getUsername(),

                clip.getGame() != null
                        ? clip.getGame().getCode()
                        : null,

                clip.getGame() != null
                        ? clip.getGame().getName()
                        : null,

                clip.getTitle(),
                clip.getDescription(),
                clip.getVideoUrl(),
                clip.getThumbnailUrl(),
                clip.getDurationSeconds(),
                clip.getStatus(),
                clip.getVisibility(),
                clip.getCreatedAt(),
                clip.getUpdatedAt(),

                clipService.countLikes(clip),
                clipService.countComments(clip),
                clipService.hasLiked(email, clip)
        );

        response.setVideoVersion(clip.getVideoVersion());

        return response;
    }

}