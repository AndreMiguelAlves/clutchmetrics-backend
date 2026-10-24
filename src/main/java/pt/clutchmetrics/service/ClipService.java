package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.clutchmetrics.dto.ClipCommentResponse;
import pt.clutchmetrics.dto.CreateClipCommentRequest;
import pt.clutchmetrics.dto.CreateClipRequest;

import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipComment;
import pt.clutchmetrics.entity.ClipLike;
import pt.clutchmetrics.entity.ClipStatus;
import pt.clutchmetrics.entity.ClipVisibility;
import pt.clutchmetrics.entity.Game;
import pt.clutchmetrics.entity.User;

import pt.clutchmetrics.exception.ClipCommentNotFoundException;
import pt.clutchmetrics.exception.ClipNotFoundException;
import pt.clutchmetrics.exception.DuplicateClipLikeException;
import pt.clutchmetrics.exception.GameNotFoundException;
import pt.clutchmetrics.exception.InvalidClipStateException;

import pt.clutchmetrics.repository.ClipCommentRepository;
import pt.clutchmetrics.repository.ClipLikeRepository;
import pt.clutchmetrics.repository.ClipRepository;
import pt.clutchmetrics.repository.GameRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;
import pt.clutchmetrics.repository.ClipReportRepository;
import pt.clutchmetrics.entity.ClipReportStatus;
import pt.clutchmetrics.entity.ClipReport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.dto.CreateClipFromVideoRequest;
import pt.clutchmetrics.entity.VideoAsset;
import pt.clutchmetrics.repository.VideoAssetRepository;
import java.nio.file.Path;
import pt.clutchmetrics.dto.UpdateClipRequest;


import java.util.List;

@Service
public class ClipService {

    private final ClipRepository clipRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ClipLikeRepository clipLikeRepository;
    private final ClipCommentRepository clipCommentRepository;
    private final AuditService auditService;
    private final ClipReportRepository clipReportRepository;
    private final VideoAssetRepository videoAssetRepository;
    private final VideoStorageService videoStorageService;
    private final String appBaseUrl;
    private final VideoProcessingService videoProcessingService;


    public ClipService(
            ClipRepository clipRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            ClipLikeRepository clipLikeRepository,
            AuditService auditService,
            ClipCommentRepository clipCommentRepository,
            ClipReportRepository clipReportRepository,
            VideoAssetRepository videoAssetRepository,
            VideoStorageService videoStorageService,
            VideoProcessingService videoProcessingService,
            @Value("${app.base-url}") String appBaseUrl
    ) {


        this.clipRepository = clipRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.clipLikeRepository = clipLikeRepository;
        this.clipCommentRepository = clipCommentRepository;
        this.auditService = auditService;
        this.clipReportRepository = clipReportRepository;
        this.videoAssetRepository = videoAssetRepository;
        this.appBaseUrl = appBaseUrl;
        this.videoStorageService = videoStorageService;
        this.videoProcessingService = videoProcessingService;
    }


    // =========================================================
    // CRIAR CLIP
    // =========================================================

    @Transactional
    public Clip createClip(
            String email,
            CreateClipRequest request) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        Game game = null;

        if (request.getGameCode() != null) {

            game = gameRepository
                    .findByCode(request.getGameCode())
                    .orElseThrow(() ->
                            new GameNotFoundException(
                                    "O jogo selecionado não existe."
                            ));
        }

        Clip clip = new Clip(
                user,
                game,
                request.getTitle().trim(),
                request.getVideoUrl().trim()
        );

        clip.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null
        );

        clip.setThumbnailUrl(
                request.getThumbnailUrl() != null
                        ? request.getThumbnailUrl().trim()
                        : null
        );

        clip.setDurationSeconds(
                request.getDurationSeconds()
        );

        return clipRepository.save(clip);
    }


    @Transactional
    public Clip createClipFromVideo(
            String email,
            CreateClipFromVideoRequest request) {

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
                        request.getVideoAssetId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Video not found."
                        )
                );

        // 3. Confirmar que o vídeo já foi processado
        if (videoAsset.getProcessedFileName() == null ||
                videoAsset.getProcessedFileName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Video must be processed before creating a clip."
            );
        }

        // 4. Identificar o jogo
        Game game = gameRepository
                .findByCode(request.getGameCode())
                .orElseThrow(() ->
                        new GameNotFoundException(
                                "The selected game does not exist."
                        )
                );

        // 5. Criar o Clip inicialmente privado
        Clip clip = new Clip(
                user,
                game,
                request.getTitle().trim(),
                "pending"
        );

        clip.setVideoAsset(videoAsset);

        clip.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null
        );


// Obter automaticamente a duração real do MP4 processado
        clip.setDurationSeconds(
                videoProcessingService.getProcessedVideoDuration(
                        videoAsset.getProcessedFileName()
                )
        );


        // O MP4 já foi realmente processado pelo FFmpeg
        clip.setStatus(ClipStatus.READY);

        // 6. Guardar o Clip para obter o seu ID
        Clip savedClip = clipRepository.saveAndFlush(clip);

        // 7. Preparar o endereço de reprodução
        String videoUrl = appBaseUrl.replaceAll("/+$", "")
                + "/api/clips/"
                + savedClip.getId()
                + "/video";

        savedClip.setVideoUrl(videoUrl);

        // 8. Guardar o endereço definitivo
        return clipRepository.saveAndFlush(savedClip);
    }

    // =========================================================
// EDITAR TÍTULO E DESCRIÇÃO DO CLIP
// =========================================================

    @Transactional
    public Clip updateClip(
            String email,
            Long clipId,
            UpdateClipRequest request) {

        // Procurar o clip e confirmar que pertence ao utilizador
        Clip clip = getOwnedClip(email, clipId);

        // Atualizar o título
        clip.setTitle(request.getTitle().trim());

        // Atualizar a descrição
        clip.setDescription(
                request.getDescription() != null
                        && !request.getDescription().isBlank()
                        ? request.getDescription().trim()
                        : null
        );

        // Guardar as alterações
        return clipRepository.save(clip);
    }

    // =========================================================
    // LISTAR OS MEUS CLIPS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Clip> getMyClips(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        return clipRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // =========================================================
    // PROCESSAR CLIP
    // =========================================================

    @Transactional
    public Clip processClip(
            String email,
            Long clipId) {

        Clip clip = getOwnedClip(
                email,
                clipId
        );

        if (clip.getStatus() != ClipStatus.UPLOADED) {

            throw new InvalidClipStateException(
                    "Apenas clips com estado UPLOADED podem ser processados."
            );
        }

        clip.setStatus(
                ClipStatus.PROCESSING
        );

        /*
         * Por enquanto o processamento é simulado.
         * Mais tarde podemos substituir isto pelo
         * processamento real do vídeo.
         */

        clip.setStatus(
                ClipStatus.READY
        );

        return clipRepository.save(clip);
    }

    // =========================================================
    // PUBLICAR CLIP
    // =========================================================

    @Transactional
    public Clip publishClip(
            String email,
            Long clipId) {

        Clip clip = getOwnedClip(
                email,
                clipId
        );

        if (clip.getStatus() != ClipStatus.READY) {

            throw new InvalidClipStateException(
                    "O clip tem de estar READY antes de ser publicado."
            );
        }


        // Verificar se o clip foi removido pela moderação
        boolean removedByModeration = clipReportRepository
                .findByClip(clip)
                .stream()
                .anyMatch(report ->
                        report.getStatus() == ClipReportStatus.RESOLVED
                );

        if (removedByModeration) {
            throw new InvalidClipStateException(
                    "This clip was removed by moderation and cannot be published again."
            );
        }


        clip.setVisibility(
                ClipVisibility.PUBLIC
        );

        Clip savedClip = clipRepository.save(clip);

        auditService.log(
                savedClip.getUser(),
                AuditAction.CLIP_PUBLISHED,
                "Clip",
                savedClip.getId(),
                "Utilizador publicou o clip: " + savedClip.getTitle()
        );

        return savedClip;
    }

    // =========================================================
    // RETIRAR CLIP DO FEED
    // =========================================================

    @Transactional
    public Clip unpublishClip(
            String email,
            Long clipId) {

        Clip clip = getOwnedClip(
                email,
                clipId
        );

        clip.setVisibility(
                ClipVisibility.PRIVATE
        );

        return clipRepository.save(clip);
    }


    @Transactional(readOnly = true)
    public Path getClipVideoPath(
            Long clipId,
            String viewerEmail) {

        // 1. Procurar o clip
        Clip clip = clipRepository
                .findById(clipId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Clip not found."
                        )
                );

        // 2. Verificar se o clip está publicado
        boolean isPublic =
                clip.getStatus() == ClipStatus.READY &&
                        clip.getVisibility() == ClipVisibility.PUBLIC;

        // 3. Verificar se pertence ao utilizador autenticado
        boolean isOwner =
                viewerEmail != null &&
                        clip.getUser().getEmail()
                                .equalsIgnoreCase(viewerEmail);

        // 4. Impedir acesso a clips privados de outros utilizadores
        if (!isPublic && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Clip not found."
            );
        }

        // 5. Confirmar que está associado a um VideoAsset
        VideoAsset videoAsset = clip.getVideoAsset();

        if (videoAsset == null ||
                !videoAsset.getUser().getId()
                        .equals(clip.getUser().getId()) ||
                videoAsset.getProcessedFileName() == null ||
                videoAsset.getProcessedFileName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Processed video not found."
            );
        }

        // 6. Localizar o MP4 no armazenamento
        try {
            return videoStorageService.getProcessedVideoPath(
                    videoAsset.getProcessedFileName()
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Processed video not found."
            );
        }
    }


    // =========================================================
    // FEED
    // =========================================================

    @Transactional(readOnly = true)
    public List<Clip> getFeed() {

        return clipRepository
                .findByVisibilityAndStatusOrderByCreatedAtDesc(
                        ClipVisibility.PUBLIC,
                        ClipStatus.READY
                );
    }

    @Transactional(readOnly = true)
    public List<Clip> getPublicClipsByUserId(Long userId) {

        return getFeed()
                .stream()
                .filter(clip ->
                        clip.getUser().getId().equals(userId)
                )
                .toList();
    }

    // =========================================================
    // LIKE
    // =========================================================

    @Transactional
    public void likeClip(
            String email,
            Long clipId) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        Clip clip = clipRepository
                .findById(clipId)
                .orElseThrow(() ->
                        new ClipNotFoundException(
                                "O clip não existe."
                        ));

        if (clip.getStatus() != ClipStatus.READY ||
                clip.getVisibility() != ClipVisibility.PUBLIC) {

            throw new InvalidClipStateException(
                    "Só é possível dar like em clips públicos e READY."
            );
        }

        if (clipLikeRepository
                .existsByClipAndUser(clip, user)) {

            throw new DuplicateClipLikeException(
                    "Já deste like neste clip."
            );
        }

        ClipLike like =
                new ClipLike(
                        clip,
                        user
                );

        clipLikeRepository.save(like);
    }

    // =========================================================
    // REMOVER LIKE
    // =========================================================

    @Transactional
    public void unlikeClip(
            String email,
            Long clipId) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        Clip clip = clipRepository
                .findById(clipId)
                .orElseThrow(() ->
                        new ClipNotFoundException(
                                "O clip não existe."
                        ));

        if (!clipLikeRepository
                .existsByClipAndUser(clip, user)) {

            throw new ClipNotFoundException(
                    "Não existe like deste utilizador neste clip."
            );
        }

        clipLikeRepository
                .deleteByClipAndUser(
                        clip,
                        user
                );
    }

    // =========================================================
    // CONTAR LIKES
    // =========================================================

    @Transactional(readOnly = true)
    public long countLikes(Clip clip) {

        return clipLikeRepository
                .countByClip(clip);
    }

    // =========================================================
    // ADICIONAR COMENTÁRIO
    // =========================================================

    @Transactional
    public ClipCommentResponse addComment(
            String email,
            Long clipId,
            CreateClipCommentRequest request) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        Clip clip = clipRepository
                .findById(clipId)
                .orElseThrow(() ->
                        new ClipNotFoundException(
                                "O clip não existe."
                        ));

        if (clip.getStatus() != ClipStatus.READY ||
                clip.getVisibility() != ClipVisibility.PUBLIC) {

            throw new InvalidClipStateException(
                    "Só é possível comentar clips públicos e READY."
            );
        }

        ClipComment comment =
                new ClipComment(
                        clip,
                        user,
                        request.getContent().trim()
                );

        ClipComment savedComment =
                clipCommentRepository.save(comment);

        return toCommentResponse(
                savedComment
        );
    }

    // =========================================================
    // LISTAR COMENTÁRIOS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ClipCommentResponse> getComments(
            Long clipId) {

        Clip clip = clipRepository
                .findById(clipId)
                .orElseThrow(() ->
                        new ClipNotFoundException(
                                "O clip não existe."
                        ));

        return clipCommentRepository
                .findByClipOrderByCreatedAtAsc(clip)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    // =========================================================
    // APAGAR COMENTÁRIO
    // =========================================================

    @Transactional
    public void deleteComment(
            String email,
            Long commentId) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        ClipComment comment =
                clipCommentRepository
                        .findByIdAndUser(
                                commentId,
                                user
                        )
                        .orElseThrow(() ->
                                new ClipCommentNotFoundException(
                                        "O comentário não existe ou não pertence ao utilizador."
                                ));

        clipCommentRepository.delete(
                comment
        );
    }

    // =========================================================
    // CONTAR COMENTÁRIOS
    // =========================================================

    @Transactional(readOnly = true)
    public long countComments(Clip clip) {

        return clipCommentRepository
                .countByClip(clip);
    }

    @Transactional(readOnly = true)
    public boolean hasLiked(String email, Clip clip) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilizador não encontrado."
                        )
                );

        return clipLikeRepository.existsByClipAndUser(
                clip,
                user
        );
    }



    @Transactional
    public void deleteClip(String email, Long clipId) {

        // Confirmar que o clip pertence ao utilizador
        Clip clip = getOwnedClip(email, clipId);

        // Guardar os dados necessários para a auditoria
        User user = clip.getUser();
        Long deletedClipId = clip.getId();
        String deletedClipTitle = clip.getTitle();

        // Eliminar likes e comentários associados
        clipLikeRepository.deleteAllByClip(clip);
        clipCommentRepository.deleteAllByClip(clip);

        // Desassociar as denúncias antes de eliminar o clip
        for (ClipReport report : clipReportRepository.findByClip(clip)) {
            report.setClip(null);
        }

// Guardar a alteração das relações na base de dados
        clipReportRepository.flush();


        // Eliminar o clip
        clipRepository.delete(clip);

        // Registar a eliminação nos Audit Logs
        auditService.log(
                user,
                AuditAction.CLIP_DELETED,
                "CLIP",
                deletedClipId,
                "Deleted clip \"" + deletedClipTitle + "\""
        );
    }



    // =========================================================
    // PROCURAR CLIP DO PRÓPRIO UTILIZADOR
    // =========================================================

    private Clip getOwnedClip(
            String email,
            Long clipId) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Utilizador não encontrado."
                        ));

        return clipRepository
                .findByIdAndUser(
                        clipId,
                        user
                )
                .orElseThrow(() ->
                        new ClipNotFoundException(
                                "O clip não existe ou não pertence ao utilizador."
                        ));
    }

    // =========================================================
    // CONVERTER COMENTÁRIO PARA DTO
    // =========================================================

    private ClipCommentResponse toCommentResponse(
            ClipComment comment) {

        return new ClipCommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}