package com.dev.engineerrant.models;

import com.dev.engineerrant.classes.Owner;

// works for register & login
public class ModelGithub {
    String login;
    String avatar_url;
    String html_url;
    String followers_url;
    String following_url;
    String starred_url;
    String repos_url;
    int public_repos;
    int followers;
    int following;
    String created_at;
    String blog;
    String bio;
    String location;
    String twitter_username;
    String name;

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public String getFollowing_url() {
        return following_url;
    }

    public String getStarred_url() {
        return starred_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public int getPublic_repos() {
        return public_repos;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getBlog() {
        return blog;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getTwitter_username() {
        return twitter_username;
    }
}

