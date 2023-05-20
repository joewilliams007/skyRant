package com.dev.engineerrant.classes.dev;

public class AvatarsMenus {
    Boolean selected;
    String text;
    Object id;
    Object sub_type;

    public AvatarsMenus(Boolean selected, String text, Object id, Object sub_type) {
        this.selected = selected;
        this.text = text;
        this.id = id;
        this.sub_type = sub_type;
    }

    public Boolean getSelected() {
        return selected;
    }

    public String getText() {
        return text;
    }

    public Object getId() {
        return id;
    }

    public Object getSub_type() {
        return sub_type;
    }
}
