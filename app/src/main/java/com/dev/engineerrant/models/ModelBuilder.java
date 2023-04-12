package com.dev.engineerrant.models;


import com.dev.engineerrant.classes.Avatars;
import com.dev.engineerrant.classes.Me;
import com.dev.engineerrant.classes.Options;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.classes.Unread;

import java.util.List;

// works for register & login
public class ModelBuilder {
    List<Avatars> avatars;
    Boolean success;
    Me me;
    List<Options> options;

    public List<Avatars> getAvatars() {
        return avatars;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Me getMe() {
        return me;
    }

    public List<Options> getOptions() {
        return options;
    }
}

