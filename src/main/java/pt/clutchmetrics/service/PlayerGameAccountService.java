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
                                "O jogo selecionado não existe."
                        ));

        if (playerGameAccountRepository
                .existsByPlayerProfileAndGame(profile, game)) {

            throw new DuplicatePlayerGameAccountException(
                    "Já tens uma conta associada a este jogo."
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
}
