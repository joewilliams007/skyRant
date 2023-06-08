package com.dev.engineerrant.classes.sky;

import java.util.List;

public class SkyProfile {
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
    int reactions_count;
    List<Reactions> reactions;

    public List<Reactions> getReactions() {
        return reactions;
    }

    public int getReactions_count() {
        return reactions_count;
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
