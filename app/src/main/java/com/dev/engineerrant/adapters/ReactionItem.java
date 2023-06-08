package com.dev.engineerrant.adapters;

public class ReactionItem {
    private final String id, username, avatar, color, reaction, post_id;

    public ReactionItem(String id, String username, String color, String avatar, String reaction, String post_id) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.color = color;
        this.reaction = reaction;
        this.post_id = post_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getReaction() {
        return reaction;
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
