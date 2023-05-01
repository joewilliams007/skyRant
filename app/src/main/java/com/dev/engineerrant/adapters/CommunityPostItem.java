package com.dev.engineerrant.adapters;

public class CommunityPostItem {
    private final String txt;
    private final String item;

    public CommunityPostItem(String txt, String item) {
        this.txt = txt;
        this.item = item;
    }

    public String getTxt() {
        return txt;
    }

    public String getItem() {
        return item;
    }
}
