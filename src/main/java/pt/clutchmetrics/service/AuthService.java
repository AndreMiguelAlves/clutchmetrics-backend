package pt.clutchmetrics.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.dto.RegisterRequest;
import pt.clutchmetrics.entity.EmailVerificationToken;
import pt.clutchmetrics.entity.Role;
import pt.clutchmetrics.entity.RoleName;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.exception.DuplicateEmailException;
import pt.clutchmetrics.repository.EmailVerificationTokenRepository;
import pt.clutchmetrics.repository.RoleRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.exception.ExpiredVerificationTokenException;
import pt.clutchmetrics.exception.InvalidVerificationTokenException;
import pt.clutchmetrics.dto.LoginRequest;
import pt.clutchmetrics.exception.AccountNotVerifiedException;
import pt.clutchmetrics.exception.InvalidCredentialsException;
import pt.clutchmetrics.entity.PasswordResetToken;
import pt.clutchmetrics.repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import pt.clutchmetrics.exception.ExpiredPasswordResetTokenException;
import pt.clutchmetrics.exception.InvalidPasswordResetTokenException;
import pt.clutchmetrics.exception.InvalidCurrentPasswordException;
import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;
import pt.clutchmetrics.repository.PasswordChangeTokenRepository;
import pt.clutchmetrics.entity.PasswordChangeToken;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordChangeTokenRepository passwordChangeTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PlayerProfileService playerProfileService;
    private final SubscriptionService subscriptionService;
    private final AuditService auditService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordChangeTokenRepository passwordChangeTokenRepository,
            RefreshTokenService refreshTokenService,
            EmailService emailService,
            PlayerProfileService playerProfileService,
            AuditService auditService,
            SubscriptionService subscriptionService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordChangeTokenRepository = passwordChangeTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.playerProfileService = playerProfileService;
        this.subscriptionService = subscriptionService;
        this.auditService = auditService;
    }

    @Transactional
    public User register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "An account with this email already exists."
            );
        }

        Role userRole = roleRepository
                .findByName(RoleName.USER)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The USER role does not exist."
                        ));

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                username,
                email,
                passwordHash
        );

        user.addRole(userRole);

        User savedUser = userRepository.save(user);

        playerProfileService.createProfileForUser(savedUser);

        subscriptionService.createFreeSubscription(savedUser);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                new EmailVerificationToken(
                        token,
                        savedUser,
                        LocalDateTime.now().plusHours(24)
                );

        emailVerificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                token
        );

        return savedUser;
    }

    @Transactional
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new InvalidVerificationTokenException(
                                        "Invalid email verification token."
                                ));

        if (verificationToken.isExpired()) {
            throw new ExpiredVerificationTokenException(
                    "Email verification token has expired."
            );
        }

        User user = verificationToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    public User login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException(
                    "Please verify your email before signing in."
            );
        }

        return user;
    }

    @Transactional
    public void forgotPassword(String requestedEmail) {

        String email = requestedEmail.trim().toLowerCase();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken =
                new PasswordResetToken(
                        token,
                        user,
                        LocalDateTime.now().plusMinutes(30)
                );

        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                token
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken passwordResetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new InvalidPasswordResetTokenException(
                                        "Invalid password reset token."
                                ));

        if (passwordResetToken.isExpired()) {

            passwordResetTokenRepository.delete(passwordResetToken);

            throw new ExpiredPasswordResetTokenException(
                    "Password reset token has expired."
            );
        }

        User user = passwordResetToken.getUser();

        String newPasswordHash =
                passwordEncoder.encode(newPassword);


        user.setPasswordHash(newPasswordHash);

// Invalidate previously issued access tokens.
        user.incrementTokenVersion();

        userRepository.save(user);



        passwordResetTokenRepository.delete(passwordResetToken);

// Cancel any pending password change requests.
        passwordChangeTokenRepository.deleteByUser(user);

        refreshTokenService.revokeAllUserTokens(user);

    }


    @Transactional
    public void changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "User not found."
                        ));

        // Verificar a password atual.
        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash())) {

            throw new InvalidCurrentPasswordException(
                    "The current password is incorrect."
            );
        }

        // Eliminar pedidos anteriores deste utilizador.
        passwordChangeTokenRepository.deleteByUser(user);
        passwordChangeTokenRepository.flush();

        // Criar um token aleatório de confirmação.
        String token = UUID.randomUUID().toString();

        // Guardar apenas o hash do token.
        String tokenHash = hashPasswordChangeToken(token);

        // Proteger a nova password antes de a guardar.
        String newPasswordHash =
                passwordEncoder.encode(newPassword);

        // Criar pedido com validade de 15 minutos.
        PasswordChangeToken passwordChangeToken =
                new PasswordChangeToken(
                        user,
                        tokenHash,
                        newPasswordHash,
                        LocalDateTime.now().plusMinutes(15)
                );

        passwordChangeTokenRepository.save(passwordChangeToken);

        // Enviar o link de confirmação por email.
        emailService.sendPasswordChangeConfirmationEmail(
                user.getEmail(),
                token
        );
    }


    @Transactional
    public void confirmPasswordChange(String token) {

        // Verificar se foi fornecido um token.
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid password change token."
            );
        }

        // Proteger o token recebido antes de o pesquisar.
        String tokenHash = hashPasswordChangeToken(token);

        // Procurar o pedido de alteração de password.
        PasswordChangeToken passwordChangeToken =
                passwordChangeTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid password change token."
                                )
                        );

        // Verificar se o pedido expirou.
        if (passwordChangeToken.isExpired()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password change confirmation link has expired."
            );
        }

        // Identificar o utilizador.
        User user = passwordChangeToken.getUser();

        // Atualizar a password com o hash guardado.

        user.setPasswordHash(
                passwordChangeToken.getNewPasswordHash()
        );

// Invalidate previously issued access tokens.
        user.incrementTokenVersion();

        userRepository.save(user);



        passwordChangeTokenRepository.deleteByUser(user);

// Invalidate any pending password recovery links.
        passwordResetTokenRepository.deleteByUser(user);

        refreshTokenService.revokeAllUserTokens(user);


        // Registar a alteração nos Audit Logs.
        auditService.log(
                user,
                AuditAction.PASSWORD_CHANGED,
                "User",
                user.getId(),
                "User confirmed the password change by email."
        );
    }


    private String hashPasswordChangeToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    exception
            );
        }
    }

}