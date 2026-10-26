
package pt.clutchmetrics.dto;

public class UserFollowStatsResponse {

    private final long followersCount;
    private final long followingCount;
    private final boolean following;
    private final boolean ownProfile;

    public UserFollowStatsResponse(
            long followersCount,
            long followingCount,
            boolean following,
            boolean ownProfile) {

        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.ownProfile = ownProfile;
        this.following = following;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isOwnProfile() {
        return ownProfile;
    }
}

