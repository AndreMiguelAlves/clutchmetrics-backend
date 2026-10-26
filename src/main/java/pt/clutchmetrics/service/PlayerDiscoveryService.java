
package pt.clutchmetrics.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.dto.UserFollowPlayerResponse;
import pt.clutchmetrics.entity.PlayerProfile;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.repository.PlayerProfileRepository;
import pt.clutchmetrics.repository.UserRepository;

import java.util.List;

@Service
public class PlayerDiscoveryService {

    private final UserRepository userRepository;
    private final PlayerProfileRepository playerProfileRepository;

    public PlayerDiscoveryService(
            UserRepository userRepository,
            PlayerProfileRepository playerProfileRepository) {

        this.userRepository = userRepository;
        this.playerProfileRepository = playerProfileRepository;
    }

    @Transactional(readOnly = true)
    public List<UserFollowPlayerResponse> searchPlayers(
            String email,
            String search) {

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found."
                ));

        String searchTerm = search == null
                ? ""
                : search.trim();

        if (searchTerm.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search term is too long."
            );
        }

        List<User> users = userRepository.searchPlayers(
                searchTerm,
                PageRequest.of(0, 20)
        );

        return users.stream()
                .filter(user ->
                        !user.getId().equals(currentUser.getId())
                )
                .map(this::toPlayerResponse)
                .toList();
    }

    private UserFollowPlayerResponse toPlayerResponse(User user) {

        PlayerProfile profile = playerProfileRepository
                .findByUserId(user.getId())
                .orElse(null);

        return new UserFollowPlayerResponse(
                user.getId(),
                user.getUsername(),
                profile != null
                        ? profile.getDisplayName()
                        : user.getUsername(),
                profile != null
                        ? profile.getAvatarUrl()
                        : null
        );
    }
}
