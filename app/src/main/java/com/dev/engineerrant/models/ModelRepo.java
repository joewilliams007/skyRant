package com.dev.engineerrant.models;

import com.dev.engineerrant.classes.Changelog;
import com.dev.engineerrant.classes.Owner;

import java.util.List;

// works for register & login
public class ModelRepo {
    String pushed_at;
    String created_at;
    String language;
    Boolean archived;
    String name;
    String html_url;
    String full_name;
    int stargazers_count;
    int forks_count;
    int open_issues_count;
    Owner owner;
    String description;

    public String getDescription() {
        return description;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getPushed_at() {
        return pushed_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLanguage() {
        return language;
    }

    public Boolean getArchived() {
        return archived;
    }

    public String getName() {
        return name;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getStargazers_count() {
        return stargazers_count;
    }

    public int getForks_count() {
        return forks_count;
    }

    public int getOpen_issues_count() {
        return open_issues_count;
    }
}

