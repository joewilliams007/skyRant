package com.dev.engineerrant.network.models;


import com.dev.engineerrant.classes.News;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.classes.Unread;

import java.util.List;

// works for register & login
public class ModelFeed {
    List<Rants> rants;
    Boolean success;
    int num_notifs;
    Unread unread;
    News news;

    public News getNews() {
        return news;
    }

    public Unread getUnread() {
        return unread;
    }

    public int getNum_notifs() {
        return num_notifs;
    }

    public Boolean getSuccess() {
        return success;
    }

    public List<Rants> getRants() {
        return rants;
    }


}

