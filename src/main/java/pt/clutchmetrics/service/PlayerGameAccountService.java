package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.dto.CreatePlayerGameAccountRequest;
import pt.clutchmetrics.entity.Game;
import pt.clutchmetrics.entity.PlayerGameAccount;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.exception.DuplicatePlayerGameAccountException;
import pt.clutchmetrics.exception.GameNotFoundException;
import pt.clutchmetrics.repository.GameRepository;
import pt.clutchmetrics.repository.PlayerGameAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.clutchmetrics.dto.UpdatePlayerGameAccountRequest;

import java.util.List;

@Service
public class PlayerGameAccountService {

    private final PlayerGameAccountRepository playerGameAccountRepository;
    private final GameRepository gameRepository;
    private final PlayerProfileService playerProfileService;

    public PlayerGameAccountService(
            PlayerGameAccountRepository playerGameAccountRepository,
            GameRepository gameRepository,
            PlayerProfileService playerProfileService) {

        this.playerGameAccountRepository = playerGameAccountRepository;
        this.gameRepository = gameRepository;
        this.playerProfileService = playerProfileService;
    }

    @Transactional
    public PlayerGameAccount create(
            String email,
            CreatePlayerGameAccountRequest request) {

        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        Game game = gameRepository
                .findByCode(request.getGameCode())
                .orElseThrow(() ->
                        new GameNotFoundException(
                                "The selected game does not exist."
                        ));

        if (playerGameAccountRepository
                .existsByPlayerProfileAndGame(profile, game)) {

            throw new DuplicatePlayerGameAccountException(
                    "You already have an account linked to this game."
            );
        }

        PlayerGameAccount account =
                new PlayerGameAccount(
                        profile,
                        game,
                        request.getNickname().trim()
                );

        account.setExternalId(
                request.getExternalId() != null
                        ? request.getExternalId().trim()
                        : null
        );

        account.setRegion(
                request.getRegion() != null
                        ? request.getRegion().trim()
                        : null
        );

        account.setPlatform(
                request.getPlatform() != null
                        ? request.getPlatform().trim()
                        : null
        );

        return playerGameAccountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<PlayerGameAccount> getMyAccounts(String email) {

        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        return playerGameAccountRepository
                .findByPlayerProfile(profile);
    }
    @Transactional
    public PlayerGameAccount update(
            String email,
            Long accountId,
            UpdatePlayerGameAccountRequest request) {

        // Obter o perfil do utilizador autenticado
        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        // Procurar a conta e verificar se pertence ao utilizador
        PlayerGameAccount account =
                playerGameAccountRepository
                        .findByIdAndPlayerProfile(accountId, profile)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Game account not found."
                                )
                        );

        // Atualizar os dados da conta
        account.setNickname(request.getNickname().trim());

        account.setExternalId(
                request.getExternalId() != null
                        ? request.getExternalId().trim()
                        : null
        );

        account.setRegion(
                request.getRegion() != null
                        ? request.getRegion().trim()
                        : null
        );

        account.setPlatform(
                request.getPlatform() != null
                        ? request.getPlatform().trim()
                        : null
        );

        // Guardar as alterações
        return playerGameAccountRepository.save(account);
    }
    @Transactional
    public PlayerGameAccount disconnect(
            String email,
            Long accountId) {

        // Identificar o utilizador autenticado
        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        // Procurar apenas uma conta pertencente ao utilizador
        PlayerGameAccount account =
                playerGameAccountRepository
                        .findByIdAndPlayerProfile(accountId, profile)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Game account not found."
                                )
                        );

        // Marcar a conta como desconectada
        account.setConnected(false);

        return playerGameAccountRepository.save(account);
    }

    @Transactional
    public PlayerGameAccount reconnect(
            String email,
            Long accountId) {

        // Identificar o utilizador autenticado
        PlayerProfile profile =
                playerProfileService.getMyProfile(email);

        // Procurar a conta existente
        PlayerGameAccount account =
                playerGameAccountRepository
                        .findByIdAndPlayerProfile(accountId, profile)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Game account not found."
                                )
                        );

        // Reativar a mesma conta
        account.setConnected(true);

        return playerGameAccountRepository.save(account);
    }
}
