package com.dev.engineerrant.network.models.dev;


import com.dev.engineerrant.classes.dev.Avatars;
import com.dev.engineerrant.classes.dev.Me;
import com.dev.engineerrant.classes.dev.Options;

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

