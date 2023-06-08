package com.dev.engineerrant.classes.sky;

public class Projects {
    Integer project_id;
    Integer user_id;
    Integer stars;
    Integer comments;
    Integer reactions;
    String title;
    String os;
    String type;
    long timestamp_added;
    long timestamp_created;
    String description;
    String relevant_dr_url;
    String website;
    String github;
    String language;
    Integer active;
    Integer archived;
    Integer owner_user_id;
    String owner;

    public Integer getProject_id() {
        return project_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getStars() {
        return stars;
    }

    public Integer getComments() {
        return comments;
    }

    public Integer getReactions() {
        return reactions;
    }

    public String getTitle() {
        return title;
    }

    public String getOs() {
        return os;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp_added() {
        return timestamp_added;
    }

    public long getTimestamp_created() {
        return timestamp_created;
    }

    public String getDescription() {
        return description;
    }

    public String getRelevant_dr_url() {
        return relevant_dr_url;
    }

    public String getWebsite() {
        return website;
    }

    public String getGithub() {
        return github;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getActive() {
        return active;
    }

    public Integer getArchived() {
        return archived;
    }

    public Integer getOwner_user_id() {
        return owner_user_id;
    }

    public String getOwner() {
        return owner;
    }
}