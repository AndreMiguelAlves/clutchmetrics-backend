
package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import pt.clutchmetrics.dto.AuthMeResponse;
import pt.clutchmetrics.dto.ChangePasswordRequest;
import pt.clutchmetrics.dto.ForgotPasswordRequest;
import pt.clutchmetrics.dto.LoginRequest;
import pt.clutchmetrics.dto.LoginResponse;
import pt.clutchmetrics.dto.LogoutRequest;
import pt.clutchmetrics.dto.RefreshTokenRequest;
import pt.clutchmetrics.dto.RefreshTokenResponse;
import pt.clutchmetrics.dto.RegisterRequest;
import pt.clutchmetrics.dto.RegisterResponse;
import pt.clutchmetrics.dto.ResetPasswordRequest;

import pt.clutchmetrics.entity.RefreshToken;
import pt.clutchmetrics.entity.User;

import pt.clutchmetrics.security.JwtService;

import pt.clutchmetrics.service.AuthService;
import pt.clutchmetrics.service.RefreshTokenService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.authService = authService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Register a new user account.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Verify the user's email address.
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String token) {

        authService.verifyEmail(token);

        // Redirect to the ClutchMetrics login page.
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(
                        "http://localhost:3000/login?verified=success"
                ))
                .build();
    }

    // Authenticate the user and generate tokens.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(request);

        String accessToken =
                jwtService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                accessToken,
                refreshToken.getToken()
        );

        return ResponseEntity.ok(response);
    }

    // Retrieve information about the authenticated user.
    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me(
            Authentication authentication) {

        List<String> roles =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(authority ->
                                authority
                                        .getAuthority()
                                        .replace("ROLE_", "")
                        )
                        .toList();

        return ResponseEntity.ok(
                new AuthMeResponse(
                        authentication.getName(),
                        roles
                )
        );
    }

    // Generate a new access token and refresh token.
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        User user = refreshTokenService
                .validateAndConsume(request.getRefreshToken());

        String newAccessToken =
                jwtService.generateAccessToken(user);

        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        RefreshTokenResponse response =
                new RefreshTokenResponse(
                        newAccessToken,
                        newRefreshToken.getToken()
                );

        return ResponseEntity.ok(response);
    }

    // Revoke the user's refresh token.
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Valid @RequestBody LogoutRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Logged out successfully."
        );
    }

    // Send password recovery instructions by email.
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "If an account exists with this email address, " +
                        "you will receive password reset instructions."
        );
    }

    // Reset a forgotten password using a valid token.
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password updated successfully."
        );
    }

    // Request a password change for the authenticated user.
    // The password is not changed until email confirmation.
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "A confirmation link has been sent to your email. " +
                        "Your password will only change after confirmation."
        );
    }

    // Confirm a pending password change using an email token.
    @PostMapping("/confirm-password-change")
    public ResponseEntity<String> confirmPasswordChange(
            @RequestBody Map<String, String> request) {

        String token = request.get("token");

        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid password change token."
            );
        }

        authService.confirmPasswordChange(token);

        return ResponseEntity.ok(
                "Password changed successfully. Please sign in again."
        );
    }
}
