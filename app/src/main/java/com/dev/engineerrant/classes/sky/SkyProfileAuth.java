package com.dev.engineerrant.classes.sky;

import java.util.List;

public class SkyProfileAuth {
    int user_id;
    String username;
    String color;
    String avatar;
    int used_score;
    String blocked_users;
    String blocked_words;
    String following;
    String background;
    int timestamp;
    int reactions;

    String avatar_frame_url;
    String avatar_bg_hex;
    String avatar_bg_url;
    String profile_bg_url;

    public String getAvatar_frame_url() {
        return avatar_frame_url;
    }

    public String getAvatar_bg_hex() {
        return avatar_bg_hex;
    }

    public String getAvatar_bg_url() {
        return avatar_bg_url;
    }

    public String getProfile_bg_url() {
        return profile_bg_url;
    }

    public int getReactions() {
        return reactions;
    }

    public String getFollowing() {
        return following;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getColor() {
        return color;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getUsed_score() {
        return used_score;
    }

    public String getBlocked_users() {
        return blocked_users;
    }

    public String getBlocked_words() {
        return blocked_words;
    }

    public String getBackground() {
        return background;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
