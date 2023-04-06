package com.dev.engineerrant.classes;

public class Projects {
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
    public String getLanguage() {
        return language;
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

    public Boolean getActive() {
        return active;
    }
}
