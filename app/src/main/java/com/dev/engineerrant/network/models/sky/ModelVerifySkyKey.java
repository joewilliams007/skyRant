package com.dev.engineerrant.network.models.sky;

// works for register & login
public class ModelVerifySkyKey {
    Boolean success;
    Boolean error;
    String verify_key;
    String message;
    String verify_post_id;

    public String getVerify_post_id() {
        return verify_post_id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getVerify_key() {
        return verify_key;
    }

    public String getMessage() {
        return message;
    }
}

