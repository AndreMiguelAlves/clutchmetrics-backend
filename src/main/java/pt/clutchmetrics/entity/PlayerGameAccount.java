package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "player_game_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_game",
                        columnNames = {"player_profile_id", "game_id"}
                )
        }
)
public class PlayerGameAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_profile_id", nullable = false)
    private PlayerProfile playerProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(name = "external_id", length = 150)
    private String externalId;

    @Column(length = 50)
    private String region;

    @Column(length = 50)
    private String platform;

    @Column(name = "connected")
    private Boolean connected = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlayerGameAccount() {
    }

    public PlayerGameAccount(
            PlayerProfile playerProfile,
            Game game,
            String nickname) {

        this.playerProfile = playerProfile;
        this.game = game;
        this.nickname = nickname;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public Game getGame() {
        return game;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isConnected() {
        return connected == null || connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}