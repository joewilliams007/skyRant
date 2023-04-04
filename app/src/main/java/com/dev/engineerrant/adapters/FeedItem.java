package com.dev.engineerrant.adapters;

public class FeedItem {
    private String text;
    private String image;
    private String type;
    private int score;
    private int numComments;
    private long created_time;

    private int id;
    private String username;
    private int vote_state;

    public int getVote_state() {
        return vote_state;
    }
    private String i;
    private String b;
    private String[] tags;
    private int user_score;
    private int user_id;



    public FeedItem(String image_url, String text, int id, String type, int score, int numComments, long created_time, String username, int vote_state, String b, String i, String[] tags, int user_score, int user_id) {
        this.image = image_url;
        this.text = text;
        this.id = id;
        this.type = type;
        this.score = score;
        this.numComments = numComments;
        this.username = username;
        this.vote_state = vote_state;
        this.created_time = created_time;
        this.b = b;
        this.i = i;
        this.tags = tags;
        this.user_id = user_id;
        this.user_score = user_score;
    }

    public String[] getTags() {
        return tags;
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
