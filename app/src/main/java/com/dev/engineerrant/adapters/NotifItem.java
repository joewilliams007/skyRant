package com.dev.engineerrant.adapters;

public class NotifItem {
    private long created_time;
    private String type; // comment_vote, comment_content, comment_mention, comment_discuss, content_vote, rant_sub
    private int read; // 1 read 0 unread
    private int rant_id;
    private int uid; // ID of the user that "fired" the notif
    private String username;

    public NotifItem(long created_time, String type, int read, int rant_id, int uid, String username) {
        this.created_time = created_time;
        this.type = type;
        this.read = read;
        this.rant_id = rant_id;
        this.uid = uid;
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
    public long getCreated_time() {
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
