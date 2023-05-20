package com.dev.engineerrant.network.models.dev;


import com.dev.engineerrant.classes.dev.News;
import com.dev.engineerrant.classes.dev.Rants;
import com.dev.engineerrant.classes.dev.Unread;

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

