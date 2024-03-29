package com.dev.engineerrant.classes.dev;

import java.util.List;

public class Rants {
    Integer id;
    int score;
    String text;
    int created_time;
    int num_comments;
    Boolean edited;
    String user_username;
    String link;
    Weekly weekly;

    String body; // NOTE ONLY FOR PROFILE COMMENTS
    Integer rant_id; // NOTE ONLY FOR PROFILE COMMENTS
    String url; // NOTE ONLY FOR SKYAPI Rants;

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public Integer getRant_id() {
        return rant_id;
    }

    public String getLink() {
        return link;
    }

    int user_score;

    public Weekly getWeekly() {
        return weekly;
    }

    int user_id;
    User_avatar user_avatar;
    Object attached_image;
    String[] tags;
    List<Links> links;

    public List<Links> getLinks() {
        return links;
    }

    public String[] getTags() {
        return tags;
    }

    public User_avatar getUser_avatar() {
        return user_avatar;
    }
    int vote_state;

    public int getVote_state() {
        return vote_state;
    }

    public Integer getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getText() {
        return text;
    }

    public int getCreated_time() {
        return created_time;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public Boolean getEdited() {
        return edited;
    }

    public String getUser_username() {
        return user_username;
    }

    public int getUser_score() {
        return user_score;
    }

    public int getUser_id() {
        return user_id;
    }


    public Object getAttached_image() {
        return attached_image;
    }

}
