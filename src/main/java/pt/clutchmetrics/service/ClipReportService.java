
package pt.clutchmetrics.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.entity.*;
import pt.clutchmetrics.repository.ClipRepository;
import pt.clutchmetrics.repository.ClipReportRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.dto.ClipReportResponse;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class ClipReportService {

    private final ClipReportRepository clipReportRepository;
    private final ClipRepository clipRepository;
    private final UserRepository userRepository;

    public ClipReportService(
            ClipReportRepository clipReportRepository,
            ClipRepository clipRepository,
            UserRepository userRepository) {

        this.clipReportRepository = clipReportRepository;
        this.clipRepository = clipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClipReport reportClip(
            String email,
            Long clipId,
            ClipReportReason reason,
            String details) {

        // Identificar quem está a denunciar
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found."
                        )
                );

        // Procurar o clip denunciado
        Clip clip = clipRepository.findById(clipId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Clip not found."
                        )
                );

        // Apenas clips públicos e prontos podem ser denunciados
        if (clip.getStatus() != ClipStatus.READY ||
                clip.getVisibility() != ClipVisibility.PUBLIC) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only public clips can be reported."
            );
        }

        // Não permitir denúncias do próprio vídeo
        if (clip.getUser().getId().equals(reporter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot report your own clip."
            );
        }

        // Impedir denúncias duplicadas
        if (clipReportRepository.existsByClipAndReporter(clip, reporter)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already reported this clip."
            );
        }

        // Confirmar que foi escolhido um motivo
        if (reason == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Report reason is required."
            );
        }

        // Limpar e validar a descrição adicional
        String cleanDetails =
                details == null ? null : details.trim();

        if (cleanDetails != null && cleanDetails.isEmpty()) {
            cleanDetails = null;
        }

        if (cleanDetails != null && cleanDetails.length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Report details cannot exceed 500 characters."
            );
        }

        // Criar e guardar a denúncia
        ClipReport report = new ClipReport(
                clip,
                reporter,
                reason,
                cleanDetails
        );

        return clipReportRepository.save(report);
    }


        @Transactional(readOnly = true)
        public List<ClipReportResponse> getAllReports() {

            return clipReportRepository
                    .findAllByOrderByCreatedAtDesc()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }


    private ClipReportResponse toResponse(ClipReport report) {

        String reviewedByUsername = null;

        if (report.getReviewedBy() != null) {
            reviewedByUsername =
                    report.getReviewedBy().getUsername();
        }

        return new ClipReportResponse(
                report.getId(),
                report.getReportedClipId(),
                report.getReportedClipTitle(),
                report.getReporter().getId(),
                report.getReporter().getUsername(),
                report.getReason(),
                report.getStatus(),
                report.getDetails(),
                reviewedByUsername,
                report.getCreatedAt(),
                report.getReviewedAt()
        );
    }


    @Transactional
    public ClipReportResponse dismissReport(
            String adminEmail,
            Long reportId) {

        // Procurar a denúncia
        ClipReport report = clipReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Report not found."
                        )
                );

        // Verificar se já foi analisada
        if (report.getStatus() != ClipReportStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This report has already been reviewed."
            );
        }

        // Identificar o administrador
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Admin user not found."
                        )
                );

        // Atualizar a denúncia
        report.setStatus(ClipReportStatus.DISMISSED);
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());

        // Guardar e devolver a resposta
        return toResponse(clipReportRepository.save(report));
    }


    @Transactional
    public ClipReportResponse resolveReport(
            String adminEmail,
            Long reportId) {

        // Procurar a denúncia
        ClipReport report = clipReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Report not found."
                        )
                );

        // Confirmar que ainda não foi analisada
        if (report.getStatus() != ClipReportStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This report has already been reviewed."
            );
        }

        // Identificar o administrador
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Admin user not found."
                        )
                );

        // Obter o clip denunciado
        Clip clip = report.getClip();

        if (clip == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The reported clip is no longer available."
            );
        }

        // Retirar o clip do Feed
        clip.setVisibility(ClipVisibility.PRIVATE);
        clipRepository.save(clip);

        // Marcar a denúncia como resolvida
        report.setStatus(ClipReportStatus.RESOLVED);
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());

        return toResponse(clipReportRepository.save(report));
    }


}

