package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.CreatePlayerGameAccountRequest;
import pt.clutchmetrics.dto.PlayerGameAccountResponse;
import pt.clutchmetrics.entity.PlayerGameAccount;
import pt.clutchmetrics.service.PlayerGameAccountService;
import pt.clutchmetrics.dto.UpdatePlayerGameAccountRequest;

import java.util.List;

@RestController
@RequestMapping("/api/game-accounts")
public class PlayerGameAccountController {

    private final PlayerGameAccountService playerGameAccountService;

    public PlayerGameAccountController(
            PlayerGameAccountService playerGameAccountService) {

        this.playerGameAccountService = playerGameAccountService;
    }

    @PostMapping
    public ResponseEntity<PlayerGameAccountResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreatePlayerGameAccountRequest request) {

        PlayerGameAccount account =
                playerGameAccountService.create(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(account));
    }

    @GetMapping
    public ResponseEntity<List<PlayerGameAccountResponse>> getMyAccounts(
            Authentication authentication) {

        List<PlayerGameAccountResponse> response =
                playerGameAccountService
                        .getMyAccounts(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PlayerGameAccountResponse> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlayerGameAccountRequest request) {

        PlayerGameAccount account =
                playerGameAccountService.update(
                        authentication.getName(),
                        id,
                        request
                );

        return ResponseEntity.ok(
                toResponse(account)
        );
    }


    @PutMapping("/{id}/disconnect")
    public ResponseEntity<PlayerGameAccountResponse> disconnect(
            Authentication authentication,
            @PathVariable Long id) {

        PlayerGameAccount account =
                playerGameAccountService.disconnect(
                        authentication.getName(),
                        id
                );

        return ResponseEntity.ok(
                toResponse(account)
        );
    }


    @PutMapping("/{id}/reconnect")
    public ResponseEntity<PlayerGameAccountResponse> reconnect(
            Authentication authentication,
            @PathVariable Long id) {

        PlayerGameAccount account =
                playerGameAccountService.reconnect(
                        authentication.getName(),
                        id
                );

        return ResponseEntity.ok(
                toResponse(account)
        );
    }

    private PlayerGameAccountResponse toResponse(
            PlayerGameAccount account) {

        return new PlayerGameAccountResponse(
                account.getId(),
                account.getGame().getCode(),
                account.getGame().getName(),
                account.getNickname(),
                account.getExternalId(),
                account.getRegion(),
                account.getPlatform(),
                account.isConnected(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}