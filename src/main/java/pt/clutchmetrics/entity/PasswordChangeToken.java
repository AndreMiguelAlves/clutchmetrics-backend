
package pt.clutchmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_change_tokens")
public class PasswordChangeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false, length = 255)
    private String newPasswordHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected PasswordChangeToken() {
    }

    public PasswordChangeToken(
            User user,
            String tokenHash,
            String newPasswordHash,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.newPasswordHash = newPasswordHash;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getNewPasswordHash() {
        return newPasswordHash;
    }

    public boolean isExpired() {
        return !LocalDateTime.now().isBefore(expiresAt);
    }
}
