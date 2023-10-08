package com.dev.engineerrant.classes.matrix;

public class LoginData {
    Identifier identifier;
    String initial_device_display_name;
    String password;
    String type;

    public LoginData(Identifier identifier, String initial_device_display_name, String password, String type) {
        this.identifier = identifier;
        this.initial_device_display_name = initial_device_display_name;
        this.password = password;
        this.type = type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getInitial_device_display_name() {
        return initial_device_display_name;
    }

    public void setInitial_device_display_name(String initial_device_display_name) {
        this.initial_device_display_name = initial_device_display_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
