package com.dev.engineerrant.network.models.matrix;

import com.dev.engineerrant.classes.dev.Auth_token;


public class ModelToken {
    String access_token;
    String home_server;
    String user_id;
    String device_id;

    public String getAccess_token() {
        return access_token;
    }

    public String getHome_server() {
        return home_server;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDevice_id() {
        return device_id;
    }
}

