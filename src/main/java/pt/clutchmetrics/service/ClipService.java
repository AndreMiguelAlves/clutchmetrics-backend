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

import java.util.List;

@Service
public class ClipService {

    private final ClipRepository clipRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ClipLikeRepository clipLikeRepository;
    private final ClipCommentRepository clipCommentRepository;

    public ClipService(
            ClipRepository clipRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            ClipLikeRepository clipLikeRepository,
            ClipCommentRepository clipCommentRepository) {

        this.clipRepository = clipRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.clipLikeRepository = clipLikeRepository;
        this.clipCommentRepository = clipCommentRepository;
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

        clip.setVisibility(
                ClipVisibility.PUBLIC
        );

        return clipRepository.save(clip);
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