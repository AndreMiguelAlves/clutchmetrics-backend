package pt.clutchmetrics.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.RegisterRequest;
import pt.clutchmetrics.dto.RegisterResponse;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.service.AuthService;
import pt.clutchmetrics.dto.LoginRequest;
import pt.clutchmetrics.dto.LoginResponse;
import pt.clutchmetrics.security.JwtService;
import org.springframework.security.core.Authentication;
import pt.clutchmetrics.entity.RefreshToken;
import pt.clutchmetrics.service.RefreshTokenService;
import pt.clutchmetrics.dto.RefreshTokenRequest;
import pt.clutchmetrics.dto.RefreshTokenResponse;
import pt.clutchmetrics.dto.LogoutRequest;
import pt.clutchmetrics.dto.ForgotPasswordRequest;
import pt.clutchmetrics.dto.ResetPasswordRequest;
import pt.clutchmetrics.dto.ChangePasswordRequest;

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

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        authService.verifyEmail(token);

        return ResponseEntity.ok(
                "Conta confirmada com sucesso."
        );
    }

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

    @GetMapping("/me")
    public ResponseEntity<String> me(
            Authentication authentication) {

        return ResponseEntity.ok(
                "Autenticado como: " +
                        authentication.getName()
        );
    }

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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Valid @RequestBody LogoutRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Sessão terminada com sucesso."
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "Se existir uma conta com esse email, receberás instruções para recuperar a password."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password alterada com sucesso."
        );
    }

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
                "Password alterada com sucesso."
        );
    }
}