package com.dev.engineerrant.network.models.dev;

import com.dev.engineerrant.classes.dev.Auth_token;


public class ModelLogin {
    Boolean success;
    Auth_token auth_token;

    public Boolean getSuccess() {
        return success;
    }

    public Auth_token getAuth_token() {
        return auth_token;
    }
}

