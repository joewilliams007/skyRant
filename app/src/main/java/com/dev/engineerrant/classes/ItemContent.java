package com.dev.engineerrant.classes;

import java.util.List;

public class ItemContent {
    List<Rants> rants;
    List<Rants>  upvoted;
    List<Rants> comments;
    List<Rants> favorites;

    public List<Rants> getRants() {
        return rants;
    }

    public List<Rants> getUpvoted() {
        return upvoted;
    }

    public List<Rants> getComments() {
        return comments;
    }

    public List<Rants> getFavorites() {
        return favorites;
    }
}
