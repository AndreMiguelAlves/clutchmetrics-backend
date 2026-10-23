
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
import pt.clutchmetrics.repository.ClipRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VideoAssetService {

    private final UserRepository userRepository;
    private final VideoAssetRepository videoAssetRepository;
    private final VideoStorageService videoStorageService;
    private final VideoProcessingService videoProcessingService;
    private final SubscriptionService subscriptionService;
    private final ClipRepository clipRepository;
    private static final Logger log =
            LoggerFactory.getLogger(VideoAssetService.class);


    public VideoAssetService(
            UserRepository userRepository,
            VideoAssetRepository videoAssetRepository,
            ClipRepository clipRepository,
            VideoStorageService videoStorageService,
            VideoProcessingService videoProcessingService,
            SubscriptionService subscriptionService) {

        this.userRepository = userRepository;
        this.videoAssetRepository = videoAssetRepository;
        this.clipRepository = clipRepository;
        this.videoStorageService = videoStorageService;
        this.videoProcessingService = videoProcessingService;
        this.subscriptionService = subscriptionService;
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


        // 4. Verificar a subscrição do utilizador
        var subscription =
                subscriptionService.getMySubscription(email);

        boolean fullHd =
                subscription.isActive() &&
                        subscription.getPlan().getCode()
                                == pt.clutchmetrics.entity.PlanCode.PREMIUM;

// 5. Processar o vídeo na resolução permitida
        String processedFileName =
                videoProcessingService.processVerticalVideo(
                        videoAsset.getOriginalFileName(),
                        startSeconds,
                        endSeconds,
                        fullHd
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


    public Path getOriginalVideoForUser(
            String email,
            Long videoAssetId) {

        // Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        // Garantir que o vídeo pertence ao utilizador
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

        // Localizar o MP4 original no armazenamento
        return videoStorageService.getOriginalVideoPath(
                videoAsset.getOriginalFileName()
        );
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

    @Transactional
    public void deleteVideo(String email, Long videoAssetId) {

        // 1. Identificar o utilizador autenticado
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        // 2. Confirmar que o vídeo pertence ao utilizador
        VideoAsset video = videoAssetRepository
                .findByIdAndUser_Id(videoAssetId, user.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Video not found."
                        )
                );

        // 3. Impedir a eliminação de vídeos usados em Clips
        if (clipRepository.existsByVideoAsset_Id(videoAssetId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This video is associated with one or more clips."
            );
        }

        // 4. Guardar os nomes dos ficheiros antes de apagar o registo
        String originalFileName = video.getOriginalFileName();
        String processedFileName = video.getProcessedFileName();

        // 5. Eliminar o registo da base de dados
        videoAssetRepository.delete(video);
        videoAssetRepository.flush();

        // 6. Remover os ficheiros apenas depois do commit da BD
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        try {
                            Files.deleteIfExists(
                                    videoStorageService.getOriginalVideoPath(
                                            originalFileName
                                    )
                            );
                        } catch (IOException | RuntimeException exception) {
                            log.error(
                                    "Could not delete original video: {}",
                                    originalFileName,
                                    exception
                            );
                        }

                        if (processedFileName != null &&
                                !processedFileName.isBlank()) {

                            try {
                                videoStorageService.deleteProcessedVideo(
                                        processedFileName
                                );
                            } catch (IOException | RuntimeException exception) {
                                log.error(
                                        "Could not delete processed video: {}",
                                        processedFileName,
                                        exception
                                );
                            }
                        }
                    }
                }
        );
    }

}
