package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.Avatars;
import com.dev.engineerrant.classes.Profile;

public class UsersItem {
    private final Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public UsersItem(Profile profile) {
        this.profile = profile;
    }
}
