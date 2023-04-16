package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.Avatars;

public class LinkItem {
    private final String link;
    private final Boolean image;

    public Boolean getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }

    public LinkItem(String link, Boolean image) {
        this.link = link;
        this.image = image;
    }
}
