package com.dev.engineerrant.network.models.matrix;


import com.dev.engineerrant.classes.matrix.Well_known;

public class ModelMatrixLogin {
    String user_id;
    String access_token;
    String home_server;
    String device_id;
    Well_known well_known;

    public String getUser_id() {
        return user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getHome_server() {
        return home_server;
    }

    public String getDevice_id() {
        return device_id;
    }

    public Well_known getWell_known() {
        return well_known;
    }
}

