package com.dev.engineerrant.classes.dev;

import java.util.List;
import java.util.Map;

public class NotifData {
    List<NotifItems> items;
    NotifUnread unread;
    int check_time;

    Map<Integer, String> username_map;

    public Map<Integer, String> getUsername_map() {
        return username_map;
    }

    public int getCheck_time() {
        return check_time;
    }

    public List<NotifItems> getItems() {
        return items;
    }

    public NotifUnread getUnread() {
        return unread;
    }
}
