package com.dev.engineerrant.classes.kbin;

public class User {
    int userId;
    String username;
    Boolean isBot;
    Boolean isFollowedByUser;
    Boolean isFollowerOfUser;
    Boolean isBlockedByUser;
    Avatar avatar;
    String apId;
    String apProfileId;
    String createdAt;

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getBot() {
        return isBot;
    }

    public Boolean getFollowedByUser() {
        return isFollowedByUser;
    }

    public Boolean getFollowerOfUser() {
        return isFollowerOfUser;
    }

    public Boolean getBlockedByUser() {
        return isBlockedByUser;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getApId() {
        return apId;
    }

    public String getApProfileId() {
        return apProfileId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
