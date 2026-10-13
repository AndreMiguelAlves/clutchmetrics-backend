package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;

import pt.clutchmetrics.dto.AdminUserResponse;
import pt.clutchmetrics.dto.UpdateUserRolesRequest;
import pt.clutchmetrics.dto.UpdateUserStatusRequest;

import pt.clutchmetrics.entity.Role;
import pt.clutchmetrics.entity.RoleName;
import pt.clutchmetrics.entity.User;

import pt.clutchmetrics.exception.AdminUserNotFoundException;
import pt.clutchmetrics.exception.InvalidAdminOperationException;

import pt.clutchmetrics.repository.RoleRepository;
import pt.clutchmetrics.repository.UserRepository;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    public AdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuditService auditService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditService = auditService;
    }

    // =========================================================
    // LISTAR UTILIZADORES
    // =========================================================

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // CONSULTAR UTILIZADOR
    // =========================================================

    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(Long userId) {

        User user = findUser(userId);

        return toResponse(user);
    }

    // =========================================================
    // ATIVAR / DESATIVAR UTILIZADOR
    // =========================================================

    @Transactional
    public AdminUserResponse updateStatus(
            String adminEmail,
            Long userId,
            UpdateUserStatusRequest request) {

        User admin = findUserByEmail(adminEmail);
        User targetUser = findUser(userId);

        if (admin.getId().equals(targetUser.getId())
                && Boolean.FALSE.equals(request.getEnabled())) {

            throw new InvalidAdminOperationException(
                    "Não podes desativar a tua própria conta de administrador."
            );
        }

        targetUser.setEnabled(
                request.getEnabled()
        );

        User savedUser =
                userRepository.save(targetUser);

        auditService.log(
                admin,
                AuditAction.USER_STATUS_CHANGED,
                "User",
                savedUser.getId(),
                "Estado do utilizador "
                        + savedUser.getEmail()
                        + " alterado para enabled="
                        + savedUser.isEnabled()
        );

        return toResponse(savedUser);
    }

    // =========================================================
    // ALTERAR ROLES
    // =========================================================

    @Transactional
    public AdminUserResponse updateRoles(
            String adminEmail,
            Long userId,
            UpdateUserRolesRequest request) {

        User admin = findUserByEmail(adminEmail);
        User targetUser = findUser(userId);

        if (admin.getId().equals(targetUser.getId())
                && !request.getRoles().contains(RoleName.ADMIN)) {

            throw new InvalidAdminOperationException(
                    "Não podes remover o teu próprio role ADMIN."
            );
        }

        targetUser.getRoles().clear();

        for (RoleName roleName : request.getRoles()) {

            Role role = roleRepository
                    .findByName(roleName)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Role não encontrado: " + roleName
                            ));

            targetUser.addRole(role);
        }

        User savedUser =
                userRepository.save(targetUser);

        String rolesDescription =
                savedUser.getRoles()
                        .stream()
                        .map(role ->
                                role.getName().name()
                        )
                        .sorted()
                        .toList()
                        .toString();

        auditService.log(
                admin,
                AuditAction.USER_ROLES_CHANGED,
                "User",
                savedUser.getId(),
                "Roles do utilizador "
                        + savedUser.getEmail()
                        + " alterados para "
                        + rolesDescription
        );

        return toResponse(savedUser);
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private User findUser(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new AdminUserNotFoundException(
                                "Utilizador não encontrado."
                        ));
    }

    private User findUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new AdminUserNotFoundException(
                                "Utilizador não encontrado."
                        ));
    }

    private AdminUserResponse toResponse(User user) {

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                role.getName().name()
                        )
                        .sorted()
                        .toList();

        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}