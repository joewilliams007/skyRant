package com.dev.engineerrant.network.models.sky;

import com.dev.engineerrant.classes.sky.Reactions;

import java.util.List;

// works for register & login
public class ModelSkyPost {
    Boolean success;
    Boolean error;
    String message;
    List<Reactions> reactions;

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<Reactions> getReactions() {
        return reactions;
    }
}

