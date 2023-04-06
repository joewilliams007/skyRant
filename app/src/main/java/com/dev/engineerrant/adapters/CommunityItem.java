package com.dev.engineerrant.adapters;

public class CommunityItem {
    String title;
    String os;
    String type;
    long timestamp_added;
    String desc;
    String relevant_dr_url;
    String website;
    String github;
    String language;
    Boolean active;

    public CommunityItem(String title, String os, String type, long timestamp_added, String desc, String relevant_dr_url, String website,String github, String language, Boolean active) {
        this.title = title;
        this.os = os;
        this.type = type;
        this.timestamp_added = timestamp_added;
        this.desc = desc;
        this.relevant_dr_url = relevant_dr_url;
        this.website = website;
        this.github = github;
        this.language = language;
        this.active = active;
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

    public String getDesc() {
        return desc;
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

    public Boolean getActive() {
        return active;
    }


}
