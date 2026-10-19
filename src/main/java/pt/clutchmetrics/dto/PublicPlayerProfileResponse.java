
package pt.clutchmetrics.dto;

public class PublicPlayerProfileResponse {

    private final Long userId;
    private final String username;
    private final String displayName;
    private final String bio;
    private final String avatarUrl;

    public PublicPlayerProfileResponse(
            Long userId,
            String username,
            String displayName,
            String bio,
            String avatarUrl
    ) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
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

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
