package pt.clutchmetrics.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.clutchmetrics.audit.AuditService;

import pt.clutchmetrics.dto.AdminUserResponse;
import pt.clutchmetrics.dto.AuditLogResponse;
import pt.clutchmetrics.dto.UpdateUserRolesRequest;
import pt.clutchmetrics.dto.UpdateUserStatusRequest;

import pt.clutchmetrics.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuditService auditService;
    private final AdminService adminService;

    public AdminController(
            AuditService auditService,
            AdminService adminService) {

        this.auditService = auditService;
        this.adminService = adminService;
    }

    // =========================================================
    // AUDIT LOGS
    // =========================================================

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>>
    getAuditLogs() {

        return ResponseEntity.ok(
                auditService.getAllLogs()
        );
    }

    // =========================================================
    // UTILIZADORES
    // =========================================================

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers()
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse>
    getUserById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminService.getUserById(userId)
        );
    }

    // =========================================================
    // ATIVAR / DESATIVAR
    // =========================================================

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse>
    updateUserStatus(
            Authentication authentication,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(
                adminService.updateStatus(
                        authentication.getName(),
                        userId,
                        request
                )
        );
    }

    // =========================================================
    // ROLES
    // =========================================================

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<AdminUserResponse>
    updateUserRoles(
            Authentication authentication,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {

        return ResponseEntity.ok(
                adminService.updateRoles(
                        authentication.getName(),
                        userId,
                        request
                )
        );
    }
}