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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

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



        profile.setYoutubeUrl(
                validateSocialUrl(
                        request.getYoutubeUrl(),
                        "YouTube",
                        Set.of("youtube.com", "www.youtube.com",
                                "m.youtube.com")
                )
        );

        profile.setTwitchUrl(
                validateSocialUrl(
                        request.getTwitchUrl(),
                        "Twitch",
                        Set.of("twitch.tv", "www.twitch.tv")
                )
        );

        profile.setTiktokUrl(
                validateSocialUrl(
                        request.getTiktokUrl(),
                        "TikTok",
                        Set.of("tiktok.com", "www.tiktok.com")
                )
        );

        profile.setInstagramUrl(
                validateSocialUrl(
                        request.getInstagramUrl(),
                        "Instagram",
                        Set.of("instagram.com", "www.instagram.com")
                )
        );



        return playerProfileRepository.save(profile);
    }


    @Transactional
    public PlayerProfile updateMyAvatar(
            String email,
            String avatarUrl) {

        PlayerProfile profile = getMyProfile(email);

        profile.setAvatarUrl(avatarUrl);

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
                profile.getAvatarUrl(),
                profile.getYoutubeUrl(),
                profile.getTwitchUrl(),
                profile.getTiktokUrl(),
                profile.getInstagramUrl()
        );

    }

    private String validateSocialUrl(
            String value,
            String platform,
            Set<String> allowedHosts) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String url = value.trim();

        if (url.length() > 2048) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    platform + " URL is too long."
            );
        }

        URI uri;

        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid " + platform + " URL."
            );
        }

        String host = uri.getHost();

        if (!"https".equalsIgnoreCase(uri.getScheme()) ||
                host == null ||
                !allowedHosts.contains(
                        host.toLowerCase(Locale.ROOT)
                ) ||
                uri.getUserInfo() != null ||
                uri.getPort() != -1 ||
                uri.getRawPath() == null ||
                uri.getRawPath().isBlank() ||
                uri.getRawPath().equals("/")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please enter a valid " + platform +
                            " profile URL."
            );
        }

        return url;
    }

}