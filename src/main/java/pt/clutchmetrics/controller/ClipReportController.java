
package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.dto.CreateClipReportRequest;
import pt.clutchmetrics.service.ClipReportService;

@RestController
@RequestMapping("/api/clips")
public class ClipReportController {

    private final ClipReportService clipReportService;

    public ClipReportController(
            ClipReportService clipReportService) {

        this.clipReportService = clipReportService;
    }

    @PostMapping("/{clipId}/reports")
    public ResponseEntity<Void> reportClip(
            Authentication authentication,
            @PathVariable Long clipId,
            @Valid @RequestBody CreateClipReportRequest request) {

        // Apenas utilizadores autenticados
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required."
            );
        }

        // Criar a denúncia
        clipReportService.reportClip(
                authentication.getName(),
                clipId,
                request.getReason(),
                request.getDetails()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}

