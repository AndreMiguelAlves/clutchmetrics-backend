
package pt.clutchmetrics.dto;

public class UserFollowPlayerResponse {

    private final Long userId;
    private final String username;
    private final String displayName;
    private final String avatarUrl;

    public UserFollowPlayerResponse(
            Long userId,
            String username,
            String displayName,
            String avatarUrl) {

        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
