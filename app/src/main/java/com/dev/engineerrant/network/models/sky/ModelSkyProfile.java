package com.dev.engineerrant.network.models.sky;

import com.dev.engineerrant.classes.sky.SkyProfile;

// works for register & login
public class ModelSkyProfile {
    Boolean success;
    Boolean error;
    String message;
    SkyProfile profile;

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public SkyProfile getProfile() {
        return profile;
    }
}