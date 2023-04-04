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
}
