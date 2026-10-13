package pt.clutchmetrics.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.dto.AuditLogResponse;
import pt.clutchmetrics.entity.User;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(
            User user,
            AuditAction action,
            String entityType,
            Long entityId,
            String description) {

        AuditLog auditLog =
                new AuditLog(
                        user,
                        action,
                        entityType,
                        entityId,
                        description
                );

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(
            AuditLog auditLog) {

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getUser().getId(),
                auditLog.getUser().getUsername(),
                auditLog.getUser().getEmail(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDescription(),
                auditLog.getCreatedAt()
        );
    }
}