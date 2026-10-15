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

    @PostMapping("/{clipId}/like")
    public ResponseEntity<String> likeClip(
            Authentication authentication,
            @PathVariable Long clipId) {

        clipService.likeClip(
                authentication.getName(),
                clipId
        );

        return ResponseEntity.ok(
                "Like adicionado com sucesso."
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
                "Like removido com sucesso."
        );
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
                "Comentário eliminado com sucesso."
        );
    }

    private ClipResponse toResponse(Clip clip) {

        return new ClipResponse(
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
    }

    private ClipResponse toFeedResponse(
            Clip clip,
            String email) {

        return new ClipResponse(
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
    }

}