package pt.clutchmetrics.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.clutchmetrics.entity.Game;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.repository.GameRepository;

@Configuration
public class GameDataInitializer {

    @Bean
    CommandLineRunner initializeGames(
            GameRepository gameRepository) {

        return args -> {

            createGameIfMissing(
                    gameRepository,
                    GameCode.LEAGUE_OF_LEGENDS,
                    "League of Legends"
            );

            createGameIfMissing(
                    gameRepository,
                    GameCode.VALORANT,
                    "Valorant"
            );

            createGameIfMissing(
                    gameRepository,
                    GameCode.CS2,
                    "Counter-Strike 2"
            );
        };
    }

    private void createGameIfMissing(
            GameRepository gameRepository,
            GameCode code,
            String name) {

        if (gameRepository.findByCode(code).isEmpty()) {
            gameRepository.save(
                    new Game(code, name)
            );
        }
    }
}