package com.dev.engineerrant.classes;

public class NotifItems {
    int created_time;
    String type; // comment_vote, comment_content, comment_mention, comment_discuss, content_vote, rant_sub
    int read; // 1 read 0 unread
    int rant_id;
    int uid; // ID of the user that "fired" the notif

    public int getCreated_time() {
        return created_time;
    }

    public String getType() {
        return type;
    }

    public int getRead() {
        return read;
    }

    public int getRant_id() {
        return rant_id;
    }

    public int getUid() {
        return uid;
    }
}
