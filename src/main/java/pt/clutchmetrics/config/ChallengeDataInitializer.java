package pt.clutchmetrics.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.clutchmetrics.entity.*;
import pt.clutchmetrics.repository.ChallengeRepository;
import pt.clutchmetrics.repository.GameRepository;

import java.time.LocalDateTime;

@Configuration
public class ChallengeDataInitializer {

    @Bean
    CommandLineRunner initializeChallenges(
            ChallengeRepository challengeRepository,
            GameRepository gameRepository) {

        return args -> {

            String title =
                    "Consegue 5 vitórias em League of Legends";

            if (challengeRepository.existsByTitle(title)) {
                return;
            }

            Game game = gameRepository
                    .findByCode(GameCode.LEAGUE_OF_LEGENDS)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "League of Legends não existe."
                            ));

            Challenge challenge =
                    new Challenge(
                            game,
                            title,
                            "Consegue 5 vitórias e completa o desafio patrocinado.",
                            "Logitech G",
                            "Headset gaming Logitech G",
                            ChallengeMetric.WINS,
                            5,
                            LocalDateTime.now(),
                            LocalDateTime.now().plusDays(7)
                    );

            challenge.setStatus(
                    ChallengeStatus.ACTIVE
            );

            challengeRepository.save(challenge);
        };
    }
}