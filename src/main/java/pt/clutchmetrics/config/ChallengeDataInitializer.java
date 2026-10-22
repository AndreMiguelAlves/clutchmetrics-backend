
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

            Game lol = gameRepository
                    .findByCode(GameCode.LEAGUE_OF_LEGENDS)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "League of Legends was not found."
                            )
                    );

            Game cs2 = gameRepository
                    .findByCode(GameCode.CS2)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Counter-Strike 2 was not found."
                            )
                    );

            // November League of Legends challenge
            String novemberTitle =
                    "Road to the Finals - November 2026";

            if (!challengeRepository.existsByTitle(novemberTitle)) {

                Challenge novemberChallenge = new Challenge(
                        lol,
                        novemberTitle,
                        "Win 12 League of Legends matches by November 20, 2026.",
                        "ClutchMetrics",
                        "Premium Gaming Headset",
                        ChallengeMetric.WINS,
                        12,
                        LocalDateTime.of(2026, 10, 1, 0, 0),
                        LocalDateTime.of(2026, 11, 20, 23, 59)
                );

                novemberChallenge.setStatus(ChallengeStatus.ACTIVE);
                challengeRepository.save(novemberChallenge);
            }

            // December Counter-Strike 2 challenge
            String cs2Title = "CS2 Clutch Master Challenge";

            if (!challengeRepository.existsByTitle(cs2Title)) {

                Challenge cs2Challenge = new Challenge(
                        cs2,
                        cs2Title,
                        "Win 5 Counter-Strike 2 matches before December 20, 2026.",
                        "SteelSeries",
                        "Gaming Mouse",
                        ChallengeMetric.WINS,
                        5,
                        LocalDateTime.of(2026, 10, 22, 0, 0),
                        LocalDateTime.of(2026, 12, 20, 23, 59)
                );

                cs2Challenge.setStatus(ChallengeStatus.ACTIVE);
                challengeRepository.save(cs2Challenge);
            }

            // December League of Legends challenge
            String winterTitle = "Winter Ranked Sprint";

            if (!challengeRepository.existsByTitle(winterTitle)) {

                Challenge winterChallenge = new Challenge(
                        lol,
                        winterTitle,
                        "Win 8 League of Legends matches before December 15, 2026.",
                        "Logitech G",
                        "Mechanical Gaming Keyboard",
                        ChallengeMetric.WINS,
                        8,
                        LocalDateTime.of(2026, 10, 22, 0, 0),
                        LocalDateTime.of(2026, 12, 15, 23, 59)
                );

                winterChallenge.setStatus(ChallengeStatus.ACTIVE);
                challengeRepository.save(winterChallenge);
            }
        };
    }
}
