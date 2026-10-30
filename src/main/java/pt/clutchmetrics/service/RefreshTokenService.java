package pt.clutchmetrics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.entity.RefreshToken;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.exception.ExpiredRefreshTokenException;
import pt.clutchmetrics.exception.InvalidRefreshTokenException;
import pt.clutchmetrics.repository.RefreshTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plus(Duration.ofMillis(refreshTokenExpiration));

        RefreshToken refreshToken =
                new RefreshToken(
                        token,
                        user,
                        expiresAt
                );

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public User validateAndConsume(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Invalid refresh token."
                        ));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has already been revoked."
            );
        }

        if (refreshToken.isExpired()) {

            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new ExpiredRefreshTokenException(
                    "Refresh token has expired."
            );
        }

        User user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new InvalidRefreshTokenException(
                    "Account is not active."
            );
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return user;
    }

    @Transactional
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Invalid refresh token."
                        ));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has already been revoked."
            );
        }

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {

        refreshTokenRepository
                .findByUser(user)
                .forEach(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }
}