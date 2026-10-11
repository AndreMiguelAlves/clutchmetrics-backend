package pt.clutchmetrics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.clutchmetrics.dto.ChallengeParticipationResponse;
import pt.clutchmetrics.dto.ChallengeResponse;
import pt.clutchmetrics.entity.Challenge;
import pt.clutchmetrics.entity.ChallengeParticipation;
import pt.clutchmetrics.service.ChallengeService;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(
            ChallengeService challengeService) {

        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeResponse>>
    getActiveChallenges() {

        List<ChallengeResponse> response =
                challengeService
                        .getActiveChallenges()
                        .stream()
                        .map(this::toChallengeResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{challengeId}/join")
    public ResponseEntity<ChallengeParticipationResponse>
    joinChallenge(
            Authentication authentication,
            @PathVariable Long challengeId) {

        ChallengeParticipation participation =
                challengeService.joinChallenge(
                        authentication.getName(),
                        challengeId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        toParticipationResponse(
                                participation
                        )
                );
    }

    @GetMapping("/me")
    public ResponseEntity<List<ChallengeParticipationResponse>>
    getMyChallenges(
            Authentication authentication) {

        List<ChallengeParticipationResponse> response =
                challengeService
                        .getMyChallenges(
                                authentication.getName()
                        )
                        .stream()
                        .map(this::toParticipationResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private ChallengeResponse toChallengeResponse(
            Challenge challenge) {

        return new ChallengeResponse(
                challenge.getId(),
                challenge.getGame().getCode(),
                challenge.getGame().getName(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getSponsorName(),
                challenge.getRewardDescription(),
                challenge.getTargetMetric(),
                challenge.getTargetValue(),
                challenge.getStatus(),
                challenge.getStartsAt(),
                challenge.getEndsAt()
        );
    }

    private ChallengeParticipationResponse
    toParticipationResponse(
            ChallengeParticipation participation) {

        Challenge challenge =
                participation.getChallenge();

        return new ChallengeParticipationResponse(
                participation.getId(),
                challenge.getId(),
                challenge.getTitle(),
                challenge.getGame().getCode(),
                challenge.getTargetMetric(),
                challenge.getTargetValue(),
                participation.getProgress(),
                participation.isCompleted(),
                participation.getJoinedAt(),
                participation.getCompletedAt()
        );
    }
}