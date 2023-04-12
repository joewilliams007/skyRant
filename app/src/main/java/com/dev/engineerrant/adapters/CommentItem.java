package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.Links;

import java.util.List;

public class CommentItem {
    private final String text;
    private final String image;
    private final String type;
    private final int score;
    private int numComments;
    private final long created_time;

    private final int id;
    private final String username;
    private final int vote_state;

    public int getVote_state() {
        return vote_state;
    }
    private final String i;
    private final String b;
    private final int user_score;
    private final int user_id;
    private final List<Links> links;

    public List<Links> getLinks() {
        return links;
    }

    public CommentItem(String image_url, String text, int id, String type, int score, long created_time, String username, int vote_state, String b, String i, int user_score, int user_id, List<Links> links) {
        this.image = image_url;
        this.text = text;
        this.id = id;
        this.type = type;
        this.score = score;
        this.created_time = created_time;
        this.username = username;
        this.vote_state = vote_state;
        this.b = b;
        this.i = i;
        this.user_id = user_id;
        this.user_score = user_score;
        this.links = links;
    }



    public int getUser_score() {
        return user_score;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getI() { // profile color
        return i;
    }

    public String getB() { // profile image
        return b;
    }
    public String getUsername() {
        return username;
    }
    public long getCreated_time() {
        return created_time;
    }
    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getNumComments() {
        return numComments;
    }

}
