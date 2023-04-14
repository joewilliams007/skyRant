package com.dev.engineerrant.classes;

public class Profile {
    String username;
    int score;

    String about;
    String location;
    int created_time;
    String skills;
    String github;
    String website;
    User_avatar avatar;
    Content content;
    int user_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public String getAbout() {
        return about;
    }

    public String getLocation() {
        return location;
    }

    public int getCreated_time() {
        return created_time;
    }

    public String getSkills() {
        return skills;
    }

    public String getGithub() {
        return github;
    }

    public String getWebsite() {
        return website;
    }

    public User_avatar getAvatar() {
        return avatar;
    }

    public Content getContent() {
        return content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreated_time(int created_time) {
        this.created_time = created_time;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setAvatar(User_avatar avatar) {
        this.avatar = avatar;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
