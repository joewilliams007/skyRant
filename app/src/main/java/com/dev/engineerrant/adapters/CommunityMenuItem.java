package com.dev.engineerrant.adapters;

public class CommunityMenuItem {
    private final String item;
    private final String type;

    public CommunityMenuItem(String item, String type) {
        this.item = item;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getItem() {
        return item;
    }
}
