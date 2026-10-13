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
                    "Já existe uma conta com este email."
            );
        }

        Role userRole = roleRepository
                .findByName(RoleName.USER)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "A role USER não existe."
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
                                        "O token de confirmação é inválido."
                                ));

        if (verificationToken.isExpired()) {
            throw new ExpiredVerificationTokenException(
                    "O token de confirmação expirou."
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
                                "Email ou password incorretos."
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Email ou password incorretos."
            );
        }

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException(
                    "Confirma o teu email antes de iniciares sessão."
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
                                        "O token de recuperação é inválido."
                                ));

        if (passwordResetToken.isExpired()) {

            passwordResetTokenRepository.delete(passwordResetToken);

            throw new ExpiredPasswordResetTokenException(
                    "O token de recuperação expirou."
            );
        }

        User user = passwordResetToken.getUser();

        String newPasswordHash =
                passwordEncoder.encode(newPassword);

        user.setPasswordHash(newPasswordHash);

        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);

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
                                "Utilizador não encontrado."
                        ));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash())) {

            throw new InvalidCurrentPasswordException(
                    "A password atual está incorreta."
            );
        }

        String newPasswordHash =
                passwordEncoder.encode(newPassword);

        user.setPasswordHash(newPasswordHash);

        userRepository.save(user);

        refreshTokenService.revokeAllUserTokens(user);

        auditService.log(
                user,
                AuditAction.PASSWORD_CHANGED,
                "User",
                user.getId(),
                "Utilizador alterou a password."
        );
    }
}