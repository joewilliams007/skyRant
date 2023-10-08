package com.dev.engineerrant.network.models.kbin;


import com.dev.engineerrant.classes.kbin.Avatar;
import com.dev.engineerrant.classes.kbin.Cover;
import com.dev.engineerrant.classes.kbin.Items;

import java.util.List;

public class ModelUser {
    Integer userId;
    String username;
    String about;
    Avatar avatar;
    Cover cover;
    String createdAt;
    Integer followersCount;
    Boolean isBot;
    Boolean isFollowedByUser;
    Boolean isFollowerOfUser;
    Boolean isBlockedByUser;

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Cover getCover() {
        return cover;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getFollowersCount() {
        return followersCount;
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
}

