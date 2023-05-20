package com.dev.engineerrant.classes.sky;

public class Reactions {
    int reaction_id;
    int user_id;
    int post_id;
    String reaction;
    int timestamp;
    String avatar;
    String username;
    String color;

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getColor() {
        return color;
    }

    public String getReaction() {
        return reaction;
    }

    public int getReaction_id() {
        return reaction_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPost_id() {
        return post_id;
    }


    public int getTimestamp() {
        return timestamp;
    }
}
