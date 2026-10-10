package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.CreateMatchRequest;
import pt.clutchmetrics.dto.MatchResponse;
import pt.clutchmetrics.service.GameMatchService;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.entity.MatchResult;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class GameMatchController {

    private final GameMatchService gameMatchService;

    public GameMatchController(
            GameMatchService gameMatchService) {

        this.gameMatchService = gameMatchService;
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(
            Authentication authentication,
            @Valid @RequestBody CreateMatchRequest request) {

        MatchResponse response =
                gameMatchService.createMatch(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getMyMatches(
            Authentication authentication,

            @RequestParam(required = false)
            GameCode gameCode,

            @RequestParam(required = false)
            MatchResult result) {

        List<MatchResponse> response =
                gameMatchService.getMyMatches(
                        authentication.getName(),
                        gameCode,
                        result
                );

        return ResponseEntity.ok(response);
    }
}