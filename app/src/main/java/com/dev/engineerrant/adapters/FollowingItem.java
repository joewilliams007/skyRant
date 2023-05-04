package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.SupporterItems;

public class FollowingItem {
    private final String id, username, avatar, color;

    public FollowingItem(String id, String username, String color, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getColor() {
        return color;
    }
}
