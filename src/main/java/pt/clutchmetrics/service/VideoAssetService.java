
package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.entity.VideoAsset;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.repository.VideoAssetRepository;
import java.nio.file.Path;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import pt.clutchmetrics.dto.VideoAssetResponse;
import java.util.List;

@Service
public class VideoAssetService {

    private final UserRepository userRepository;
    private final VideoAssetRepository videoAssetRepository;
    private final VideoStorageService videoStorageService;
    private final VideoProcessingService videoProcessingService;


    public VideoAssetService(
            UserRepository userRepository,
            VideoAssetRepository videoAssetRepository,
            VideoStorageService videoStorageService,
            VideoProcessingService videoProcessingService) {

        this.userRepository = userRepository;
        this.videoAssetRepository = videoAssetRepository;
        this.videoStorageService = videoStorageService;
        this.videoProcessingService = videoProcessingService;
    }


    public VideoAsset uploadVideo(
            String email,
            MultipartFile file) {

        // 1. Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found."
                        )
                );

        // 2. Guardar o ficheiro MP4 no disco
        String fileName = videoStorageService
                .saveOriginalVideo(file);

        try {

            // 3. Associar o vídeo ao utilizador
            VideoAsset videoAsset = new VideoAsset(
                    user,
                    fileName
            );


            // Obter o nome original do ficheiro enviado
            String uploadedFileName = file.getOriginalFilename();

// Evitar nomes vazios
            if (uploadedFileName == null || uploadedFileName.isBlank()) {
                uploadedFileName = "Uploaded video.mp4";
            }

// Remover eventuais caminhos incluídos no nome
            uploadedFileName = uploadedFileName.replace('\\', '/');
            uploadedFileName = uploadedFileName.substring(
                    uploadedFileName.lastIndexOf('/') + 1
            );

// Respeitar o limite de 255 caracteres da base de dados
            if (uploadedFileName.length() > 255) {
                uploadedFileName = uploadedFileName.substring(0, 255);
            }

// Guardar o nome visível para o utilizador
            videoAsset.setUploadedFileName(uploadedFileName);


            // 4. Guardar a associação na base de dados
            return videoAssetRepository
                    .saveAndFlush(videoAsset);

        } catch (RuntimeException exception) {

            // Remover o ficheiro se falhar o registo na BD
            try {
                Files.deleteIfExists(
                        videoStorageService
                                .getOriginalVideoPath(fileName)
                );
            } catch (IOException | IllegalArgumentException cleanupError) {
                exception.addSuppressed(cleanupError);
            }

            throw exception;
        }
    }


    public VideoAsset processVideo(
            String email,
            Long videoAssetId,
            int startSeconds,
            int endSeconds) {

        // 1. Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found."
                        )
                );


        VideoAsset videoAsset = videoAssetRepository
                .findByIdAndUser_Id(
                        videoAssetId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Video not found."
                        )
                );


        // 3. Impedir novo processamento do mesmo vídeo
        if (videoAsset.getProcessedFileName() != null) {
            throw new IllegalStateException(
                    "This video has already been processed."
            );
        }

        // 4. Executar o FFmpeg
        String processedFileName =
                videoProcessingService.processVerticalVideo(
                        videoAsset.getOriginalFileName(),
                        startSeconds,
                        endSeconds
                );


        // 5. Guardar o resultado na base de dados
        try {
            videoAsset.setProcessedFileName(processedFileName);

            return videoAssetRepository.saveAndFlush(videoAsset);

        } catch (RuntimeException exception) {

            // 6. Se a BD falhar, remover o MP4 processado
            try {
                videoStorageService.deleteProcessedVideo(
                        processedFileName
                );

            } catch (IOException | IllegalArgumentException cleanupError) {

                // Preservar o erro original
                exception.addSuppressed(cleanupError);
            }

            throw exception;
        }
    }

    public Path getProcessedVideoForUser(
            String email,
            Long videoAssetId) {

        // 1. Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        // 2. Verificar se o vídeo pertence ao utilizador
        VideoAsset videoAsset = videoAssetRepository
                .findByIdAndUser_Id(
                        videoAssetId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Video not found."
                        )
                );

        // 3. Confirmar que o vídeo já foi processado
        String processedFileName =
                videoAsset.getProcessedFileName();

        if (processedFileName == null ||
                processedFileName.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Video has not been processed yet."
            );
        }

        // 4. Localizar o ficheiro processado no disco
        return videoStorageService
                .getProcessedVideoPath(processedFileName);
    }

    public List<VideoAssetResponse> getMyVideos(String email) {

        // Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        // Consultar os vídeos pertencentes ao utilizador
        List<VideoAsset> videos = videoAssetRepository
                .findByUser_IdOrderByCreatedAtDesc(user.getId());

        // Converter as entidades em DTOs

        return videos.stream()
                .map(video -> new VideoAssetResponse(
                        video.getId(),
                        video.getProcessedFileName() != null
                                && !video.getProcessedFileName().isBlank(),
                        video.getCreatedAt(),
                        video.getUploadedFileName()
                ))
                .toList();

    }

}
