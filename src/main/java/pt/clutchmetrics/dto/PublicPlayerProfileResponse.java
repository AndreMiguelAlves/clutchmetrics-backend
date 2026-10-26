
package pt.clutchmetrics.dto;

public class PublicPlayerProfileResponse {

    private final Long userId;
    private final String username;
    private final String displayName;
    private final String bio;
    private final String avatarUrl;
    private final String youtubeUrl;
    private final String twitchUrl;
    private final String tiktokUrl;
    private final String instagramUrl;

    public PublicPlayerProfileResponse(
            Long userId,
            String username,
            String displayName,
            String bio,
            String avatarUrl,
            String youtubeUrl,
            String twitchUrl,
            String tiktokUrl,
            String instagramUrl
    ) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.youtubeUrl = youtubeUrl;
        this.twitchUrl = twitchUrl;
        this.tiktokUrl = tiktokUrl;
        this.instagramUrl = instagramUrl;
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


    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getTwitchUrl() {
        return twitchUrl;
    }

    public String getTiktokUrl() {
        return tiktokUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

}
