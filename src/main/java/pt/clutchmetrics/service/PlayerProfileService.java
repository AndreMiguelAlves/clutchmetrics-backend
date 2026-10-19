package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.dto.UpdatePlayerProfileRequest;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.exception.PlayerProfileNotFoundException;
import pt.clutchmetrics.repository.PlayerProfileRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.dto.PublicPlayerProfileResponse;

@Service
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;
    private final UserRepository userRepository;

    public PlayerProfileService(
            PlayerProfileRepository playerProfileRepository,
            UserRepository userRepository) {

        this.playerProfileRepository = playerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PlayerProfile createProfileForUser(User user) {

        PlayerProfile profile =
                new PlayerProfile(
                        user,
                        user.getUsername()
                );

        return playerProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public PlayerProfile getMyProfile(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new PlayerProfileNotFoundException(
                                "Utilizador não encontrado."
                        ));

        return playerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new PlayerProfileNotFoundException(
                                "Perfil de jogador não encontrado."
                        ));
    }

    @Transactional
    public PlayerProfile updateMyProfile(
            String email,
            UpdatePlayerProfileRequest request) {

        PlayerProfile profile = getMyProfile(email);

        profile.setDisplayName(
                request.getDisplayName().trim()
        );

        profile.setBio(
                request.getBio() != null
                        ? request.getBio().trim()
                        : null
        );

        profile.setAvatarUrl(
                request.getAvatarUrl() != null
                        ? request.getAvatarUrl().trim()
                        : null
        );

        return playerProfileRepository.save(profile);
    }


    @Transactional(readOnly = true)
    public PublicPlayerProfileResponse getPublicProfileByUserId(Long userId) {

        PlayerProfile profile = playerProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new PlayerProfileNotFoundException(
                                "Perfil de jogador não encontrado."
                        )
                );

        return new PublicPlayerProfileResponse(
                profile.getUser().getId(),
                profile.getUser().getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl()
        );
    }

}