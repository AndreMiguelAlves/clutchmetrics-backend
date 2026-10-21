

package pt.clutchmetrics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pt.clutchmetrics.entity.VideoAsset;
import pt.clutchmetrics.service.VideoAssetService;
import jakarta.validation.Valid;
import pt.clutchmetrics.dto.ProcessVideoRequest;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import pt.clutchmetrics.dto.VideoAssetResponse;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/clips/videos")
public class VideoUploadController {

    private final VideoAssetService videoAssetService;

    public VideoUploadController(
            VideoAssetService videoAssetService) {

        this.videoAssetService = videoAssetService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadVideo(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        VideoAsset videoAsset = videoAssetService.uploadVideo(
                authentication.getName(),
                file
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", videoAsset.getId(),
                        "fileName", videoAsset.getOriginalFileName()
                ));
    }

    @PostMapping("/{videoAssetId}/process")
    public ResponseEntity<Map<String, Object>> processVideo(
            Authentication authentication,
            @PathVariable Long videoAssetId,
            @Valid @RequestBody ProcessVideoRequest request) {

        // Confirmar que existe um utilizador autenticado
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Executar o processamento através do serviço
        VideoAsset videoAsset = videoAssetService.processVideo(
                authentication.getName(),
                videoAssetId,
                request.getStartSeconds(),
                request.getEndSeconds()
        );

        // Devolver a identificação do vídeo processado
        return ResponseEntity.ok(
                Map.of(
                        "id", videoAsset.getId(),
                        "processedFileName",
                        videoAsset.getProcessedFileName(),
                        "message", "Video processed successfully."
                )
        );
    }

    @GetMapping("/{videoAssetId}/download")
    public ResponseEntity<Resource> downloadVideo(
            Authentication authentication,
            @PathVariable Long videoAssetId) throws IOException {

        // 1. Verificar se o utilizador está autenticado
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // 2. Verificar o proprietário e localizar o MP4
        Path videoPath = videoAssetService.getProcessedVideoForUser(
                authentication.getName(),
                videoAssetId
        );

        // 3. Preparar o ficheiro para download
        Resource resource = new FileSystemResource(videoPath);

        String downloadName = "clutchmetrics-video-"
                + videoAssetId + ".mp4";

        String contentDisposition = ContentDisposition
                .attachment()
                .filename(downloadName)
                .build()
                .toString();

        // 4. Devolver o ficheiro MP4
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition
                )
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentLength(Files.size(videoPath))
                .body(resource);
    }

    @GetMapping("/me")
    public ResponseEntity<List<VideoAssetResponse>> getMyVideos(
            Authentication authentication) {

        // Confirmar que o utilizador está autenticado
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Consultar os vídeos do utilizador autenticado
        List<VideoAssetResponse> videos =
                videoAssetService.getMyVideos(
                        authentication.getName()
                );

        return ResponseEntity.ok(videos);
    }

}
