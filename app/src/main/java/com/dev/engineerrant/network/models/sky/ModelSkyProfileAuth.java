package com.dev.engineerrant.network.models.sky;

import com.dev.engineerrant.classes.sky.SkyProfile;
import com.dev.engineerrant.classes.sky.SkyProfileAuth;

// works for register & login
public class ModelSkyProfileAuth {
    Boolean success;
    Boolean error;
    String message;
    SkyProfileAuth profile;

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public SkyProfileAuth getProfile() {
        return profile;
    }
}