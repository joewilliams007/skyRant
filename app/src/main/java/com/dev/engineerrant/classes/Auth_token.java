package com.dev.engineerrant.classes;

public class Auth_token {
    int id;
    String key;
    int expire_time;
    int user_id;

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getExpire_time() {
        return expire_time;
    }

    public int getUser_id() {
        return user_id;
    }
}
