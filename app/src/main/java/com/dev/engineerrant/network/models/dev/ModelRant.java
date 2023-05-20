package com.dev.engineerrant.network.models.dev;

import com.dev.engineerrant.classes.dev.Comment;
import com.dev.engineerrant.classes.dev.Rants;

import java.util.List;

// works for register & login
public class ModelRant {
    List<Comment> comments;
    Rants rant;

    public List<Comment> getComments() {
        return comments;
    }

    public Rants getRant() {
        return rant;
    }
}

